package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * author hua �˿��ļ����������
 */
public class DwbkTipsFileOperForFj extends AbstractTipsFileOper{
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid,  String biztype,
			 String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap)
			throws ITFEBizException {
		// ���ֲ�ͬ�ط��ӿ�
		MulitTableDto multi = new MulitTableDto();
		multi.setBiztype(biztype);// ҵ������
		multi.setSbookorgcode(bookorgCode);// �����������
		int fbcount = 0; // �ְ�������
		int recordNum = 0; // ��¼��¼��
		int recordhead = 0; 
		String fname = new File(file).getName();
			
		List<IDto> listdto = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		
		File fi = new File(file);
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal famtPack = new BigDecimal("0.00");
		String trecode = "";
		String tmpPackNo = "";
		BigDecimal famthead = new BigDecimal("0.00");
		List<String> voulist = new ArrayList<String>();
		try {
			HashMap<String,TsPaybankDto> paymap = this.makeBankMap();
			HashMap<String, TsConverttaxorgDto> taxmap =this.makeTaxMap(bookorgCode);
			HashMap<String, TsConvertfinorgDto> fincmap =this.makeFincMap(bookorgCode);
			List<String[]> fileContent = super.readFile(file, ",");
			String[] firstStr = fileContent.get(0);
			//�����ļ�ͷ
			recordhead = Integer.parseInt(firstStr[2]);
			famthead = new BigDecimal(firstStr[3]);
			fileContent.remove(0); //ȥ���ļ�ͷ
			fileContent.remove(fileContent.size() - 1); //ȥ��У����
			int checkFile = 0;
			int li_Detail = (fileContent.size()-1) / 1000;
			for (int k = 0; k <= li_Detail; k++) {
				int li_TempCount = 0;
				if (li_Detail == k) {
					li_TempCount = fileContent.size();
				} else {
					li_TempCount = (k + 1) * 1000;
				}
				tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
						.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
								SequenceName.TRAID_SEQ_CACHE,
								SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
				for (int j = k * 1000; j < li_TempCount; j++) {
					String[] singDto = fileContent.get(j);
					TbsTvDwbkDto dwbkDto = new TbsTvDwbkDto();

					dwbkDto.setSelecvouno(singDto[0]); //���
					if(singDto[1] == null || "".equals(singDto[1].trim())) {
						multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]�д���'�˿�������'Ϊ��!");
					}
					if(!this.checkTreasury(singDto[1]+"00", bookorgCode)) {
						multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]�й���������룺"+singDto[1]+"00"+" û����'����������Ϣ����'�в��ҵ�!");
					}
					dwbkDto.setSpayertrecode(singDto[1]+"00"); //�˿�������
					trecode = singDto[1]+"00";
					checkFile++;
					if(checkFile == 1) {
						//У���ļ��ظ�,�������+�ļ���
						String errorInfo = this.checkFileExsit(bookorgCode, trecode, fi.getName(), MsgConstant.MSG_NO_5101);
						if(null != errorInfo && errorInfo.length() > 0) {
							multi.getErrorList().add(errorInfo);
							return multi;
						}
					}
					
					if(ITFECommonConstant.ISCONVERT.equals("0")){
						dwbkDto.setStaxorgcode(singDto[2]); // ���ջ���
					}else{
						// TBS���ջ��ش���
						String taxkey= trecode+singDto[2];
						String strTipsTaxOrg ="";
						if (!taxmap.containsKey(taxkey)) {
							multi.getErrorList().add("�˿��ļ�[" + fi.getName()
									+ "]�й���������룺"+trecode+",���ջ��ش��룺" + singDto[2]
									+ " û��ά��'���ջ��ض���'!");
						}else {
							TsConverttaxorgDto taxogdto = taxmap.get(taxkey);
							// Tips���ջ��ش���
							strTipsTaxOrg = taxogdto.getStcbstaxorgcode();
						}
						//�˿����
						dwbkDto.setStaxorgcode(strTipsTaxOrg);
					}
										
					dwbkDto.setSagenttaxorgcode(singDto[3]);//�����
					dwbkDto.setSpayeecode(singDto[4]);//�տλ
					dwbkDto.setSecocode(singDto[5]);//�˿��˾������ʹ���
					dwbkDto.setSetpcode(singDto[6]);//�˿�����ҵ���ʹ���
					dwbkDto.setDbill(CommonUtil.strToDate(singDto[7])); //�����
					if(singDto[8] == null || "".equals(singDto[8].trim())) {
						multi.getErrorList().add(fi.getName() +" �˿��ļ�["+fi.getName()+"]�д���'�˿�ƾ֤��'Ϊ��!");
					}
					if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_FALSE)) {
						//�������½ӿڣ�����Ҫ�����ַ���ȡ
						if(singDto[8].length() > 8) {
							dwbkDto.setSdwbkvoucode(singDto[8].substring(singDto[8].length()-8));//�˿�ƾ֤��
						}else {
							dwbkDto.setSdwbkvoucode(singDto[8]);//�˿�ƾ֤��
						}
						if(!VerifyParamTrans.isNumber(singDto[8])){
							multi.getErrorList().add("�˿��ļ�[" + fi.getName()
									+ "]�д���ƾ֤���"+singDto[8]+"�а�������֮����ַ�!");
						}
					}else if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_TRUE)){
						dwbkDto.setSdwbkvoucode(singDto[8]);//�˿�ƾ֤��
					}
					
					voulist.add(singDto[2].trim()+","+dwbkDto.getSdwbkvoucode().trim());
					if(singDto[9] == null || "".equals(singDto[9].trim())) {
						multi.getErrorList().add(fi.getName() +" �˿��ļ�["+fi.getName()+"]�д���'�տλ������'Ϊ��!");
					}
					TsPaybankDto bnkpaydto = paymap.get(singDto[9].trim());
					if(null == bnkpaydto) {
						multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]�����տλ�����У�'"+singDto[9].trim()+"'û����'֧���кŲ�ѯ����' ���ҵ���Ӧ�������к�!");
					}else {
						if(null != bnkpaydto.getSstate() && "0".equals(bnkpaydto.getSstate())) {
							multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]���տλ�����У�'"+singDto[9].trim()+"' ����'��Чǰ'״̬!");
						}else if (null != bnkpaydto.getSstate() && "2".equals(bnkpaydto.getSstate())) {
							multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]���տλ�����У�'"+singDto[9].trim()+"' ����'ע��'״̬!");
						}	
					}							
					dwbkDto.setSpayeeopnbnkno(singDto[9]);//�տλ������
					if(singDto[10] == null || "".equals(singDto[10].trim())) {
						multi.getErrorList().add(fi.getName() +" �˿��ļ�["+fi.getName()+"]�д���'�տλ����'Ϊ��!");
					}
					String errChi_10 = VerifyParamTrans.verifyNotUsableChinese(singDto[10]);
					if(null != errChi_10 && !"".equals(errChi_10)) {
						multi.getErrorList().add(fi.getName() +" �˿��ļ�["+fi.getName()+"]���տ������ƴ��ڷǷ��ַ���"+errChi_10);
					}
					dwbkDto.setSpayeename(singDto[10]);//�տλ����
					if(singDto[11] == null || "".equals(singDto[11].trim())) {
						multi.getErrorList().add(fi.getName() +" �˿��ļ�["+fi.getName()+"]�д���'�տλ�˺�'Ϊ��!");
					}
					dwbkDto.setSpayeeacct(singDto[11]);//�տλ�˺�
					if(singDto[12] == null || "".equals(singDto[12].trim())) {
						multi.getErrorList().add(fi.getName() +" �˿��ļ�["+fi.getName()+"]�д���'�˿⼶��'Ϊ��!");
					}
					dwbkDto.setCbdglevel(singDto[12]);//�˿⼶��
					if(singDto[13] == null || "".equals(singDto[13].trim())) {
						multi.getErrorList().add(fi.getName() +" �˿��ļ�["+fi.getName()+"]�д���'�˿��Ŀ����'Ϊ��!");
					}
					String err = this.verifySubject(dmap, singDto[13], MsgConstant.MSG_NO_1104, "", fi.getName(),singDto[8]);
					if(!"".equals(err)) {
						multi.getErrorList().add(err);
					}
					dwbkDto.setSbdgsbtcode(singDto[13]);//�˿��Ŀ����
					dwbkDto.setSdwbkreasoncode(singDto[14]);//�˿�ԭ��
					dwbkDto.setSexamorg(singDto[15]);//�˿���������
					dwbkDto.setSdwbkby(singDto[16]);//�˿�����
					dwbkDto.setFdwbkratio(new BigDecimal(singDto[17]));//�˿����
					dwbkDto.setFdwbkamt(new BigDecimal(singDto[18])); //�˿��ܶ�
					dwbkDto.setFamt(new BigDecimal(singDto[19]));//�˿���
					famt = famt.add(new BigDecimal(singDto[19]));
					famtPack = famtPack.add(new BigDecimal(singDto[19]));
					if(singDto.length == 20) {
						dwbkDto.setSastflag("");//������־
					}else {
						dwbkDto.setSastflag(singDto[20]);//������־
					}
					dwbkDto.setSbookorgcode(bookorgCode);// �����������
					dwbkDto.setCtrimflag("0"); // �����ڱ�־��Ϊ��
					dwbkDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
					dwbkDto.setSbiztype(biztype);// ҵ������
					dwbkDto.setSfilename(fi.getName()); //�����ļ���
					dwbkDto.setDvoucher(CommonUtil.strToDate(fi.getName().substring(0,8))); // ��Ʊ���ڲ�Ϊ��
					dwbkDto.setCbdgkind(MsgConstant.BDG_KIND_IN); //Ԥ�����಻��Ϊ�� Ĭ����1��Ԥ����
					dwbkDto.setCbckflag("0"); //�˻ر�־����Ϊ��
					dwbkDto.setDaccept(TimeFacade.getCurrentDateTime()); //�������ڲ���Ϊ��
					dwbkDto.setSaimtrecode(trecode);
					dwbkDto.setSpackageno(tmpPackNo);
					dwbkDto.setSfilename(fi.getName());
					listdto.add(dwbkDto);
					recordNum++;
					fbcount++;
				}
				TvFilepackagerefDto packdto = new TvFilepackagerefDto();
				
				packdto.setSorgcode(bookorgCode);
				packdto.setStrecode(trecode);
				packdto.setSfilename(fi.getName());
				
				
				if (!fincmap.containsKey(trecode)) {
					multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]�и��ݺ���������룺"+bookorgCode+"�� ����������룺"+trecode+" û���� '����������Ϣά������'���ҵ���Ӧ������������!");
				}else {
					TsConvertfinorgDto gd = fincmap.get(trecode);
					packdto.setStaxorgcode(gd.getSfinorgcode());
				}				
				packdto.setScommitdate(TimeFacade.getCurrentStringTime());
				packdto.setSaccdate(TimeFacade.getCurrentStringTime());
				packdto.setSpackageno(tmpPackNo);
				packdto.setSoperationtypecode(biztype);					
				packdto.setIcount(fbcount);
				packdto.setNmoney(famtPack);
				packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
				packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
				packdto.setSusercode(userid);
				packdto.setImodicount(0);
				fbcount = 0;
				famtPack = new BigDecimal("0.00");
				packdtos.add(packdto);
			}
			
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("�˿⵼���ļ������쳣 \n"+e.getMessage(), e);
		} catch (SequenceException e) {
			log.error(e);
			throw new ITFEBizException("�˿⵼���ļ������쳣 \n"+e.getMessage(),e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣 \n"+e.getMessage(),e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ�����쳣 \n"+e.getMessage(),e);
		}
		if(famthead.compareTo(famt) != 0) {
			multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]���ļ�ͷ���[" + famthead + "]����ϸ�ܽ��[" + famt
						+ "]����");			
		}
		
		//�ļ���ƾ֤���У��
		int oldSize = 0;
		int newSize = 0;
		Set<String> sets = new HashSet<String>();
		for (String item : voulist) {
			oldSize = sets.size();
			sets.add(item.trim());
			newSize = sets.size();
			if (newSize == oldSize) {
				multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]�д���ƾ֤����ظ�");
			}
		}
		
		multi.setFatherDtos(listdto);
		multi.setPackDtos(packdtos);
		multi.setVoulist(voulist);
		multi.setFamt(famt); //�ܽ��
		multi.setTotalCount(recordhead); //�ܱ���
		return multi;

	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}
}

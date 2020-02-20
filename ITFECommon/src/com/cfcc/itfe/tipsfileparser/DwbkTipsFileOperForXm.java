package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * author hua �˿��ļ����������
 */
public class DwbkTipsFileOperForXm extends AbstractTipsFileOper {
	
	
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
		String fname = new File(file).getName();
		String ctrimflag = fname.substring(fname.length()-5, fname.length()-4);
		List<IDto> listdto = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		List<IDto> famtdtos = new ArrayList<IDto>();
		File fi = new File(file);
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal famtPack = new BigDecimal("0.00");
		String trecode = "";
		String tmpPackNo = "";
		List<String> voulist = new ArrayList<String>(); //ƾ֤���
		try {
			HashMap<String, TsConvertfinorgDto> fincmap =this.makeFincMap(bookorgCode);
			HashMap<String,TsPaybankDto> paymap = this.makeBankMap();
			List<String[]> fileContent = super.readFile(file, ",");
			if(file.endsWith("txt")) {  //��ϸ�սӿ�
				int li_Detail = (fileContent.size()-1) / 1000;
				if(fileContent.size() == (li_Detail * 1000)) {
					li_Detail = li_Detail - 1;
				}
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
						
						dwbkDto.setSpayertrecode(singDto[2]);// �տ�������
						trecode = singDto[2];
						if(!this.checkTreasury(trecode, bookorgCode)) {
							multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]�й���������룺"+trecode+" û����'����������Ϣ����'�в��ҵ�!");
						}
						if (singDto[3] != null || !singDto[3].trim().equals("")) {
							dwbkDto.setSaimtrecode(singDto[3]); // Ŀ�Ĺ������
						} else {
							dwbkDto.setSaimtrecode(singDto[2]); // Ŀ�Ĺ������
						}
						TsConverttaxorgDto convertTaxOrgDto = new TsConverttaxorgDto();
						// �����������
						convertTaxOrgDto.setSorgcode(bookorgCode);
						// �����������
						convertTaxOrgDto.setStrecode(trecode);
						// TBS���ջ��ش���
						convertTaxOrgDto.setStbstaxorgcode(singDto[1]);
						List dtoL = CommonFacade.getODB().findRsByDtoWithUR(
								convertTaxOrgDto);
						String strTipsTaxOrg = "";
						if (dtoL == null || dtoL.size() == 0) {
							multi.getErrorList().add("�˿��ļ�[" + fi.getName()
									+ "]�й���������룺"+trecode+",���ջ��ش��룺" + singDto[1]
									+ " û��ά��'���ջ��ض���'!");
						}else {
							TsConverttaxorgDto taxogdto = (TsConverttaxorgDto) dtoL.get(0);
							// Tips���ջ��ش���
							strTipsTaxOrg = taxogdto.getStcbstaxorgcode();
							// ���ջ��ش���
							dwbkDto.setStaxorgcode(strTipsTaxOrg);
						}						
						
						dwbkDto.setSdwbkvoucode(singDto[0]); // ƾ֤���
						//�ɽӿ�У��ƾ֤��Ų�����8λ����Ϊ����
						if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_FALSE)) {
							//�������½ӿڣ�����Ҫ�����ַ���ȡ
							if(singDto[0].length() > 8) {
								dwbkDto.setSdwbkvoucode(singDto[0].substring(singDto[0].length()-8));//�˿�ƾ֤��
							}else {
								if(singDto[0].length() < 8){
									multi.getErrorList().add("�˿��ļ�[" + fi.getName()+ "]��ƾ֤���"+singDto[0]+"����Ϊ8λ!");
								}
								dwbkDto.setSdwbkvoucode(singDto[0]);//�˿�ƾ֤��
							}
							if(!VerifyParamTrans.isNumber(singDto[0])){
								multi.getErrorList().add("�˿��ļ�[" + fi.getName()+ "]�д���ƾ֤���"+singDto[0]+"�а�������֮����ַ�!");
							}
						}else if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_TRUE)){
							dwbkDto.setSdwbkvoucode(singDto[0]);//�˿�ƾ֤��
						}
						voulist.add(strTipsTaxOrg.trim()+","+singDto[0].trim());
						
						dwbkDto.setCbdglevel(singDto[4]); // Ԥ�㼶��
						dwbkDto.setCbdgkind(singDto[5]); // Ԥ������
						
						String err = this.verifySubject(dmap, singDto[6], MsgConstant.MSG_NO_1104, "", fi.getName(),singDto[0]);
						if(!"".equals(err)) {
							multi.getErrorList().add(err);
						}
						dwbkDto.setSbdgsbtcode(singDto[6]); // ��Ŀ����
						dwbkDto.setSastflag(singDto[7]); // ������־
						dwbkDto.setSdwbkreasoncode(singDto[8]); // �˿�ԭ�����
						dwbkDto.setSdwbkby(singDto[9]); // �˿�����
						dwbkDto.setSexamorg(singDto[10]); // ��������
						
						dwbkDto.setDaccept(TimeFacade.getCurrentDateTime());// D_ACCEPT�������ڲ���Ϊ��

						if (singDto[11] != null && !singDto[11].trim().equals("")) {
							dwbkDto.setFdwbkratio(new BigDecimal(singDto[11])); // �˿����
						} else {
							dwbkDto.setFdwbkratio(new BigDecimal("0.00"));
						}

						if (singDto[12] != null && !singDto[12].trim().equals("")) {
							dwbkDto.setFdwbkamt(new BigDecimal(singDto[12])); // �˿��ܶ�
						} else {
							dwbkDto.setFdwbkamt(new BigDecimal("0.00"));
						}

						if (singDto[13] != null && !singDto[13].trim().equals("")) {
							dwbkDto.setFamt(new BigDecimal(singDto[13])); // ������
							famt = famt.add(new BigDecimal(singDto[13]));
							famtPack = famtPack.add(new BigDecimal(singDto[13]));
						} else {
							dwbkDto.setFamt(new BigDecimal("0.00"));
						}

						dwbkDto.setCbckflag(singDto[14]); // �˻ر�־
						dwbkDto.setSpayeeacct(singDto[15]); // �տ��ʺ�
						dwbkDto.setSpayeecode(singDto[16]); // �տλ����
						
						dwbkDto.setSbookorgcode(bookorgCode);// �����������
						dwbkDto.setCtrimflag(ctrimflag); // �����ڱ�־��Ϊ��
						dwbkDto.setSbiztype(biztype);// ҵ������
						dwbkDto.setDvoucher(CommonUtil.strToDate(fi.getName().substring(0,8))); // ��Ʊ���ڲ�Ϊ��
						dwbkDto.setSelecvouno(singDto[0]); //����ƾ֤��Ų���Ϊ��
						dwbkDto.setSpackageno(tmpPackNo);
						dwbkDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
						dwbkDto.setSfilename(fi.getName()); //�����ļ���
						listdto.add(dwbkDto);
						fbcount++;
						recordNum++;
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
			}else if (file.endsWith("tmp")) { //�ʽ�ڽӿ�
				String srcvreno = fi.getName().substring(0, 12);
				for (int i = 0; i < fileContent.size(); i++) {
					TvPayoutfinanceDto dto = new TvPayoutfinanceDto();
					dto.setSrcvreckbnkno(srcvreno);
					String[] strs = fileContent.get(i);
					if("<CA>".equals(strs[0].toUpperCase())) {
						break ;
					}
					dto.setSsndbnkno(strs[0]);   //�������к�
					dto.setSrcvbnkno(strs[1]);   //�������к�
					dto.setFamt(new BigDecimal(strs[2]));  //���
					famt = famt.add(new BigDecimal(strs[2]));
					famtPack = famtPack.add(new BigDecimal(strs[2]));
					dto.setSpayeropnbnkno(strs[3]);    //�����˿������к�
					dto.setSpayeracct(strs[4]);   //�������˺�
					dto.setSpayername(strs[5]);   //����������
					dto.setSpayeraddr(strs[6]);   //�����˵�ַ
					if(strs[7] == null || "".equals(strs[7].trim())) {
						multi.getErrorList().add(fi.getName() +" �˿��ļ�["+fi.getName()+"]�д���'�տλ������'Ϊ��!");
					}
					TsPaybankDto bnkpaydto = paymap.get(strs[7].trim());
					if(null == bnkpaydto) {
						multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]�����տ��˿����У�'"+strs[7].trim()+"'û����'֧���кŲ�ѯ����' ���ҵ���Ӧ�������к�!");
					}else {
						if(null != bnkpaydto.getSstate() && "0".equals(bnkpaydto.getSstate())) {
							multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]���տ��˿����У�'"+strs[7].trim()+"' ����'��Чǰ'״̬!");
						}else if (null != bnkpaydto.getSstate() && "2".equals(bnkpaydto.getSstate())) {
							multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]���տ��˿����У�'"+strs[7].trim()+"' ����'ע��'״̬!");
						}
					}					
					dto.setSpayeeopnbnkno(strs[7]);   //�տ��˿������к�
					dto.setSpayeeacct(strs[8]);     //�տ����˺�	
					String errChi_9 = VerifyParamTrans.verifyNotUsableChinese(strs[9]);
					if(null != errChi_9 && !"".equals(errChi_9)) {
						multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]���տ������ƴ��ڷǷ��ַ���"+errChi_9);
					}
					dto.setSpayeename(strs[9]);		//�տ�������
					dto.setSpayeeaddr(strs[10]);	//�տ��˵�ַ
					dto.setSpaybizkind(strs[11]);   //֧��ҵ������
					dto.setSbiztype(strs[12]);   //ҵ�����ͺ�
					String errChi_13 = VerifyParamTrans.verifyNotUsableChinese(strs[13]);
					if(null != errChi_13 && !"".equals(errChi_13)) {
						multi.getErrorList().add("�˿��ļ�["+fi.getName()+"]�и��Դ��ڷǷ��ַ���"+errChi_13);
					}
					dto.setSaddword(strs[13]);      //����
					
					famtdtos.add(dto);
				}
			}		
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("�˿⵼���ļ��������� \n"+e.getMessage(),e);
		} catch (SequenceException e) {
			log.error(e);
			throw new ITFEBizException("�˿⵼���ļ������쳣 \n"+e.getMessage(),e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("���ݲ�ѯ�쳣 \n"+e.getMessage(),e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("���ݲ�ѯ�쳣 \n"+e.getMessage(),e);
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
		multi.setSonDtos(famtdtos);
		multi.setVoulist(voulist);
		multi.setFamt(famt); //�ܽ��
		multi.setTotalCount(recordNum); //�ܱ���
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

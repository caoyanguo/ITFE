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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertassitsignDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * author hua ����
 */
public class InCorrhandbookTipsFileOper extends AbstractTipsFileOper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> dmap) throws ITFEBizException {
		try {
			/**���н�����ʼ�� */
			File fi = new File(file);
			List<String[]> fileContent = super.readFile(file, ","); // �������ȡ�ļ�
			
			int fbcount = 0; // �ְ�������
			int recordNum = 1; // ��¼��¼��
			int checkFile = 0; // У���ļ����ظ�

			String fname = new File(file).getName(); // �ļ���
			String ctrimflag = ""; // �����ڱ�־
			//�麣�ĸ����ļ�Ϊ14λ�������ڱ�־Ϊ��.txt��ǰ�����ڶ�λ
//			String ctrimflag = fname.substring(fname.length() - 5, fname.length() - 4); // �����ڱ�־
			if(fname.toLowerCase().replaceAll(".txt", "").length() == 14 ){
				ctrimflag = fname.substring(12, 13); // �����ڱ�־
			}else{
				ctrimflag = fname.substring(fname.length() - 5, fname.length() - 4); // �����ڱ�־
			}

			MulitTableDto multi = new MulitTableDto();
			multi.setBiztype(biztype);// ҵ������
			multi.setSbookorgcode(bookorgCode);// �����������
			
			String trecode = ""; // �������
			String tmpPackNo = ""; // ����ˮ��
			
			HashMap<String, TsConverttaxorgDto> mapTaxInfo = SrvCacheFacade.cacheTaxInfo(bookorgCode); // �������ջ��ض��ռ���
			TsTreasuryDto finddto = new TsTreasuryDto();//���Һ��������ܲ����Ĺ���
			finddto.setSorgcode(bookorgCode);
			Map<String,TsTreasuryDto> treMap = new HashMap<String,TsTreasuryDto>();
			List<TsTreasuryDto> treList = CommonFacade.getODB().findRsByDto(finddto);
			if(treList!=null&&treList.size()>0)
			{
				for(TsTreasuryDto fordto:treList)
					treMap.put(fordto.getStrecode(), fordto);
			}
			BigDecimal famt = new BigDecimal("0.00"); // �����ļ����ܽ��
			BigDecimal famtPack = new BigDecimal("0.00"); // �ְ����õĽ�����

			List<IDto> listdto = new ArrayList<IDto>(); // ������¼����
			List<IDto> packdtos = new ArrayList<IDto>(); // �ļ������Ӧ��ϵ����
			HashSet<String> taxorgset = new HashSet<String>(); // ������ջ��أ����ڷְ�
			List<String> voulist = new ArrayList<String>(); // ƾ֤��ż���
			HashMap<String, List<String[]>> splitMap = new HashMap<String, List<String[]>>();// ���ջ���-��Ӧ��¼
			
			/**�������Ӱ���ԭ���ջ��طְ�   by hua 2013-04-02*/
			/**
			 * 1�������ҳ����е����ջ���
			 */
			for(String[] tmpS : fileContent) {
				if(null != tmpS[3] && !"".equals(tmpS[3])) {
					taxorgset.add(tmpS[3].trim());
				}else { //���ԭ�����ջ��ز�����ֱ�ӷ���
					multi.getErrorList().add("�����ļ�["+fi.getName()+"]�д��ڴ���'ԭ���ջ��ش���'Ϊ��!");
					return multi;
				}
			}
			
			/**
			 * 2�������е����ջ��غͶ�Ӧ�ļ�¼�߼��Ϸֿ�
			 */
			for(String taxcode : taxorgset) {
				List<String[]> tmpList = new ArrayList<String[]>();
				for(String[] tmpS2 : fileContent) {
					if(tmpS2[3].trim().equals(taxcode)) {
						tmpList.add(tmpS2);
					}
				}
				splitMap.put(taxcode, tmpList);
			}
			
			/**
			 * 3����ʽ�ְ����������ˮ�Ų����������ļ���Ӧ��ϵ
			 */
			for(String taxcode1 : taxorgset) {
				List<String[]> tmpInCorrList = splitMap.get(taxcode1);
				int li_Detail = (tmpInCorrList.size()-1) / 1000;
				// ȡ�ø�����־���ձ�
				HashMap viceMap = getViceSignMap(bookorgCode);
				for (int k = 0; k <= li_Detail; k++) {
					int li_TempCount = 0;
					if (li_Detail == k) {
						li_TempCount = tmpInCorrList.size();
					} else {
						li_TempCount = (k + 1) * 1000;
					}
					//��������ˮ��
					tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
							.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
									SequenceName.TRAID_SEQ_CACHE,
									SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
					for (int j = k * 1000; j < li_TempCount; j++) {
						String[] singDto = tmpInCorrList.get(j);
						TbsTvInCorrhandbookDto incorrDto = new TbsTvInCorrhandbookDto();
						incorrDto.setCcurbdgkind(singDto[0]); // Ԥ�����ࣨ1-Ԥ���ڣ�2��Ԥ���⣩					
						if(singDto[1] == null || "".equals(singDto[1].trim())) {
							multi.getErrorList().add("�����ļ�["+fi.getName()+"]�д���'ԭ�տ�������'Ϊ��!");
						}
						trecode = singDto[1];
						if(!treMap.containsKey(trecode)) {
							multi.getErrorList().add("�����ļ�["+fi.getName()+"]��ԭ�տ����������룺"+trecode+" û����'����������Ϣ����'�в��ҵ�!");
						}
						incorrDto.setSoripayeetrecode(singDto[1]); // ԭ�տ�������
						
						checkFile++;
						if(checkFile == 1) {
							//У���ļ��ظ�,�������+�ļ���
							String errorInfo = this.checkFileExsit(bookorgCode, trecode, fi.getName(), MsgConstant.MSG_NO_1105);
							if(null != errorInfo && errorInfo.length() > 0) {
								multi.getErrorList().add(errorInfo);
								return multi;
							}
						}
						
						
						if(singDto[2] == null || "".equals(singDto[2])) {
							multi.getErrorList().add("�����ļ�["+fi.getName()+"]�д���'ԭĿ�Ĺ������'Ϊ��!");
						}else {
							incorrDto.setSoriaimtrecode(singDto[2]);// ԭĿ�Ĺ������
						}
						if(!ITFECommonConstant.ISCONVERT.equals("0")){
							if (!mapTaxInfo.containsKey(singDto[2]+singDto[3])) {
								multi.getErrorList().add("�����ļ�["+fi.getName()+"]��ԭ���ջ��ش�����ձ���û��ά��" + singDto[3]
													+ "��Ӧ��TIPS���ջ��أ�");
							}
						}
						incorrDto.setSoritaxorgcode(singDto[3]); // ԭ���ջ��ش���
						incorrDto.setCoribdglevel(singDto[4]); // ԭԤ�㼶��
						incorrDto.setSoriastflag(singDto[5]); // ԭ������־
						
						if (StateConstant.shareBudgetLevel.equals(singDto[4]) && null==singDto[5]) {
							multi.getErrorList().add("ԭԤ�㼶��Ϊ����ԭ������־����Ϊ�գ�");
						}
						// �жϸ�����־��Ӧ��ϵ�Ĵ�����
						if(!ITFECommonConstant.ISCONVERT.equals("0")){
							if (null != singDto[5] && !"".equals(singDto[5])) { // ������־
								if (!viceMap.containsKey(singDto[2] + singDto[6]
										+ singDto[5])) {
									if (!viceMap.containsKey(singDto[2] + singDto[5])) {
										if (!viceMap.containsKey(singDto[6] + singDto[5])) {
											if (!viceMap.containsKey(singDto[5])) {
												multi.getErrorList().add(
														"�����ļ�[" + fi.getName() + "]�и�����־��"
																+ singDto[7]
																+ " û����'������־����ά��'������ά��!");
											}
										}
									}
								}
							}
						}
						
						if(singDto[6] == null || "".equals(singDto[6].trim())) {
							multi.getErrorList().add("�����ļ�["+fi.getName()+"]�д���'ԭ��Ŀ����'Ϊ��!");
						}
						String err = this.verifySubject(dmap, singDto[6].trim(), MsgConstant.MSG_NO_1105, "1", fi.getName(),singDto[16]);
						if(!"".equals(err)) {
							multi.getErrorList().add(err);
						}
						incorrDto.setSoribdgsbtcode(singDto[6]); // ԭ��Ŀ����
						incorrDto.setForicorramt(new BigDecimal(singDto[7])); // ԭ������
						incorrDto.setSreasoncode(singDto[8]); // ����ԭ�����
						if(singDto[9] == null || "".equals(singDto[9].trim())) {
							multi.getErrorList().add("�����ļ�["+fi.getName()+"]�д���'���տ�������'Ϊ��!");
						}

						if(!this.checkTreasury(singDto[9], bookorgCode)) {
							multi.getErrorList().add("�����ļ�["+fi.getName()+"]�����տ����������룺"+singDto[9]+" û����'����������Ϣ����'�в��ҵ�!");
						}
						incorrDto.setScurpayeetrecode(singDto[9]); // ���տ�������
						
						if(singDto[10] == null || "".equals(singDto[10])) {
							multi.getErrorList().add("�����ļ�["+fi.getName()+"]�д���'��Ŀ�Ĺ������'Ϊ��!");
						}else {
							incorrDto.setScuraimtrecode(singDto[10]); // ��Ŀ�Ĺ������
						}
						
						if(singDto[11] == null || "".equals(singDto[11].trim())) {
							multi.getErrorList().add("�����ļ�["+fi.getName()+"]�д���'�����ջ��ش���'Ϊ��!");
						}
						if(!ITFECommonConstant.ISCONVERT.equals("0")){
							if (!mapTaxInfo.containsKey(singDto[10]+singDto[11])) {
								multi.getErrorList().add("�����ļ�["+fi.getName()+"]�������ջ��ش�����ձ���û��ά��" + singDto[11]
													+ "��Ӧ��TIPS���ջ��أ�");
							}
						}
						incorrDto.setScurtaxorgcode(singDto[11]); // �����ջ��ش���
						incorrDto.setCcurbdglevel(singDto[12]); // ��Ԥ�㼶�Σ�0������1--���룬2--ʡ��3--�У�4--���أ�5--����
						incorrDto.setScurastflag(singDto[13]); // �ָ�����־
						if (StateConstant.shareBudgetLevel.equals(singDto[12]) && null==singDto[13]) {
							multi.getErrorList().add("��Ԥ�㼶��Ϊ�����ָ�����־����Ϊ��");
						}
						// �жϸ�����־��Ӧ��ϵ�Ĵ�����
						if(!ITFECommonConstant.ISCONVERT.equals("0")){
							if (null != singDto[13] && !"".equals(singDto[13])) { // ������־
								if (!viceMap.containsKey(singDto[10] + singDto[14]
										+ singDto[13])) {
									if (!viceMap.containsKey(singDto[10] + singDto[13])) {
										if (!viceMap.containsKey(singDto[14] + singDto[13])) {
											if (!viceMap.containsKey(singDto[13])) {
												multi.getErrorList().add(
														"�����ļ�[" + fi.getName() + "]�и�����־��"
																+ singDto[7]
																+ " û����'������־����ά��'������ά��!");
											}
										}
									}
								}
							}
						}
						if(singDto[14] == null || "".equals(singDto[14].trim())) {
							multi.getErrorList().add("�����ļ�["+fi.getName()+"]�д���'�ֿ�Ŀ����'Ϊ��!");
						}
						err = this.verifySubject(dmap, singDto[14].trim(), MsgConstant.MSG_NO_1105, "1", fi.getName(),singDto[16]);
						if(!"".equals(err)) {
							multi.getErrorList().add(err);
						}
						incorrDto.setScurbdgsbtcode(singDto[14]); // �ֿ�Ŀ����
						incorrDto.setFcurcorramt(new BigDecimal(singDto[15])); // �ַ�����(#.00)
						
						if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_FALSE)) {
							//�������½ӿڣ�����Ҫ�����ַ���ȡ������Ϊ8λ������
							if(!VerifyParamTrans.isNumber(singDto[16])){
								multi.getErrorList().add("�����ļ�[" + fi.getName()+ "]��ƾ֤���"+singDto[16]+"�а�������֮����ַ�!");
							}
							if(singDto[16].length() > 8) {
								incorrDto.setScorrvouno(singDto[16].substring(singDto[16].length()-8));//ƾ֤���
							}else {
								if(singDto[16].length() < 8){
									multi.getErrorList().add("�����ļ�[" + fi.getName()+ "]��ƾ֤���"+singDto[16]+"����Ϊ8λ!");
								}
								incorrDto.setScorrvouno(singDto[16]); // ƾ֤���
							}
						}else if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_TRUE)){
							incorrDto.setScorrvouno(singDto[16]); // ƾ֤���
						}					
						//TODO �Ƿ���Ҫ���չ���������ջ�����У��ƾ֤��ţ������ǣ�����
						voulist.add(incorrDto.getScorrvouno());
						
						incorrDto.setSelecvouno(singDto[16]); //����ƾ֤��Ų�Ϊ��
						incorrDto.setCtrimflag(ctrimflag); //�ֵ����ڲ�Ϊ��
						incorrDto.setCoribdgkind(StateConstant.BudgetType_IN); //ԪԤ�����಻Ϊ��
						incorrDto.setDaccept(TimeFacade.getCurrentDateTime()); //��������
						incorrDto.setDvoucher(CommonUtil.strToDate(fi.getName().substring(0, 8)));
						famt = famt.add(new BigDecimal(singDto[7]));
						famtPack = famtPack.add(new BigDecimal(singDto[7]));
						incorrDto.setSbookorgcode(bookorgCode);// �����������
						incorrDto.setSpackageno(tmpPackNo);
						incorrDto.setSfilename(fi.getName());
						incorrDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
						fbcount ++;
						listdto.add(incorrDto);
					}
					TvFilepackagerefDto packdto = new TvFilepackagerefDto();
					packdto.setSorgcode(bookorgCode);
					packdto.setStrecode(trecode);
					packdto.setSfilename(fi.getName());
					packdto.setStaxorgcode(taxcode1);
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
			}
			
			//У���ļ���ƾ֤����ظ���
			int oldSize = 0;
			int newSize = 0;
			Set<String> sets = new HashSet<String>();
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					multi.getErrorList().add("�����ļ�["+fi.getName()+"]�д���ƾ֤����ظ�");
				}
			}
			
			multi.setFatherDtos(listdto);
			multi.setPackDtos(packdtos);
			multi.setVoulist(voulist);
			multi.setFamt(famt);  //�ܽ��
			multi.setTotalCount(recordNum); //�ܱ���
			return multi; 

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			throw new ITFEBizException("���������ļ��������� \n"+e.getMessage(), e);
		}
	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * ������־���չ�ϵ��
	 * 
	 * @throws ITFEBizException
	 */
	private HashMap<String, String> getViceSignMap(String sbookorgcode)
			throws ITFEBizException {
		HashMap<String, String> vicemap = new HashMap<String, String>();
		TsConvertassitsignDto viceDto = new TsConvertassitsignDto();
		viceDto.setSorgcode(sbookorgcode);
		try {
			List list = CommonFacade.getODB().findRsByDto(viceDto);
			for (int i = 0; i < list.size(); i++) {
				TsConvertassitsignDto dto = (TsConvertassitsignDto) list.get(i);
				vicemap.put(dto.getStrecode() + dto.getSbudgetsubcode()
						+ dto.getStbsassitsign(), dto.getStipsassistsign());
				vicemap.put(dto.getSbudgetsubcode() + dto.getStbsassitsign(),
						dto.getStipsassistsign());
				vicemap.put(dto.getStrecode() + dto.getStbsassitsign(), dto
						.getStipsassistsign());
				vicemap.put(dto.getStbsassitsign(), dto.getStipsassistsign());
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("ȡ�ø�����־���չ�ϵ���쳣 \n" + e.getMessage(), e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("ȡ�ø�����־���չ�ϵ���쳣 \n" + e.getMessage(), e);
		}
		return vicemap;
	}

}

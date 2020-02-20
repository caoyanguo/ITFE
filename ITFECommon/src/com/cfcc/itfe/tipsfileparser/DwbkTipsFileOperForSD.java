package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.CommonParamDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertassitsignDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * author hua �˿��ļ����������
 */
public class DwbkTipsFileOperForSD extends AbstractTipsFileOper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> dmap) throws ITFEBizException {
		// ���ֲ�ͬ�ط��ӿ�
		MulitTableDto multi = new MulitTableDto();
		multi.setBiztype(biztype);// ҵ������
		multi.setSbookorgcode(bookorgCode);// �����������

		String fname = new File(file).getName();
		String ctrimflag = fname.substring(fname.length() - 5,
				fname.length() - 4);
		File fi = new File(file);
		BigDecimal famt = new BigDecimal("0.00");

		String trecode = "";

		// ȡ�ļ��е���Ϣת��ΪdwbkDtoList
		List<TbsTvDwbkDto> dwbkDtoList = new ArrayList();
		// �������ջ��ش���ְ���Map
		Map<String, String> taxOrgCodeMap = new HashMap<String, String>();
		// ���ÿһ�����ջ��ش����Ӧ��dwbkDtoList��Ȼ���ٸ���dwbkDtoList�ĸ�������1000�ʽ��зְ�
		Map<String, List> taxOrgCodeDtoListMap = new HashMap();
		// ��װ��Ҫ���ص�dwbkDtoList
		List<IDto> dwbkDtoListReturn = new ArrayList();
		// ��װ��Ҫ���صİ���ϢpackageFileList
		List<IDto> packageFileList = new ArrayList();
		// ƾ֤���Set��������֤ƾ֤����Ƿ��ظ�
		Set<String> voucodeSet = new HashSet<String>();
		// ƾ֤����б�,���ڷ��ر�����У�鵱��ƾ֤����Ƿ��ظ�ʹ��
		List<String> voulist = new ArrayList<String>();
		// ȡ�ù�������뼶�ζ�Ӧ��ϵ
		HashMap treMap = getTreLevelMap(bookorgCode);
		// ȡ�ø�����־���ձ�
		HashMap viceMap = getViceSignMap(bookorgCode);
		try {
			String key = "";
			String encyptMode = "";
			List<String[]> fileContent = super.readFile(file, ",");
			CommonParamDto _dto = (CommonParamDto) paramdto;
			encyptMode = _dto.getEncryptMode();
			int record = fileContent.size();
			if (StateConstant.SM3_ENCRYPT.equals(encyptMode) && record > 1) {
				record = record - 1;
			}
			TsTreasuryDto finddto = new TsTreasuryDto();//���Һ��������ܲ����Ĺ���
			finddto.setSorgcode(bookorgCode);
			Map<String,TsTreasuryDto> treOrg = new HashMap<String,TsTreasuryDto>();
			List<TsTreasuryDto> treList = CommonFacade.getODB().findRsByDto(finddto);
			if(treList!=null&&treList.size()>0)
			{
				for(TsTreasuryDto fordto:treList)
					treOrg.put(fordto.getStrecode(), fordto);
			}
			for (int i = 0; i < record; i++) {
				String[] singDto = fileContent.get(i);
				// �ڵ�0�л�ȡһ����Կ�����ݲ�ͬ�����ȡ��Կ
				if (i == 0) {
					if ("SHANDONG".equals(_dto.getArea())) {
						key = this.findKeyForValidate("", ""); // ����ļ�У��Key
					} else {// ���մ������Կ����ģʽȡ��Ӧ����Կ//�޸�Ϊ����TIPS�����ջ���������Կ
						TsMankeyDto keydto = null;
						if(ITFECommonConstant.ISCONVERT.equals("0")){
								keydto = TipsFileDecrypt.findKeyByKeyMode(_dto
										.getKeyMode(), bookorgCode, singDto[2]);
						}else{
							HashMap<String, TsConverttaxorgDto> mapTaxInfo = SrvCacheFacade.cacheTaxInfo(bookorgCode);
							TsConverttaxorgDto tmpdto = mapTaxInfo.get(singDto[2]+ singDto[1]);
							if (null != tmpdto) {
								if (StateConstant.SPECIAL_AREA_GUANGDONG.equals(_dto.getArea())) {
									// �˴��޸�Ϊ����TCBS���ջ���������Կ
									keydto = TipsFileDecrypt.findKeyByKeyMode(_dto
											.getKeyMode(), bookorgCode, tmpdto
											.getStcbstaxorgcode());
								}else{
									// �˴��޸�Ϊ����TCBS���ջ���������Կ
									keydto = TipsFileDecrypt.findKeyByKeyMode(_dto
											.getKeyMode(), bookorgCode, singDto[2]);
								}
								
							} else {
								multi.getErrorList().add(
										"���ջ��ض��ձ���û��ά��" + singDto[1]
												+ "��Ӧ��TIPS���ջ��أ�");
								return multi;
							}
						}
						
						
						

						if (null != keydto) {
							key = keydto.getSkey();
						} else {
							multi.getErrorList().add(
									"�˿��ļ�[" + fi.getName() + "û�в��ҵ���Ӧ�Ľ�����Կ��");
							return multi;
						}
					}
				}
				// �����ܵķ�ʽ��ҪУ����Կ��ɽ�����Ĵ��ķ�ʽ��Ϊ������ģʽ����
				// �����ܵķ�ʽ�У��ļ�����20���ֶΣ����ܷ�ʽ��ֻ��19��
				if (StateConstant.NO_ENCRYPT.equals(encyptMode)) {
					if (!this.importValidator(singDto[0], singDto[6],
							singDto[15], new BigDecimal(singDto[13]),
							singDto[19], key)) {
						multi.getErrorList().add(
								"�˿��ļ�[" + fi.getName() + "]�е� " + (i + 1)
										+ " �д���У������֤����!");
					}
					if (singDto.length != StateConstant.DWBK_CONTENTNUM_SD
							.intValue()) {
						multi.getErrorList().add(
								"���ڽ����˿⵼����ļ�[" + fi.getName() + "]���ֶ���Ŀ����!");
					}
				}
				if (StateConstant.SD_ENCRYPT.equals(encyptMode)) {
					if (!SM3Process.verifySM3Sign(StringUtils
							.join(singDto, ","), key)) {
						multi.getErrorList().add(
								"ʵ���ʽ��ļ�[" + fi.getName() + "]�е� " + (i + 1)
										+ " ������ǩ��У��ʧ��!");
					}
				}
				// �˴����ļ��е���Ϣ��ն�Ӧ�Ĺ�ϵ����dwbkDto��
				TbsTvDwbkDto dwbkDto = new TbsTvDwbkDto();
				dwbkDto.setSpayertrecode(singDto[2]);// �տ�������
				trecode = singDto[2];
				if (i == 0) {
					// У���ļ��ظ�,�������+�ļ���
					String errorInfo = this.checkFileExsit(bookorgCode,
							trecode, fi.getName(), MsgConstant.MSG_NO_1104);
					if (null != errorInfo && errorInfo.length() > 0) {
						multi.getErrorList().add(errorInfo);
						return multi;
					}
					// �����ļ���֤SM3�㷨����ȷ��
					if (StateConstant.SM3_ENCRYPT.equals(encyptMode)) {
						if (!SM3Process.verifySM3SignFile(file, key)) {
							multi.getErrorList().add(
									"ʵ���ʽ��ļ�[" + fi.getName() + "]SM3ǩ����֤ʧ��!");
							return multi;
						}
					}
				}
				if (!treOrg.containsKey(trecode)) {
					multi.getErrorList().add(
							"�˿��ļ�[" + fi.getName() + "]�й���������룺" + trecode
									+ " û����'����������Ϣ����'�в��ҵ�!");
				}
				if (singDto[3] != null && !singDto[3].trim().equals("")) {
					dwbkDto.setSaimtrecode(singDto[3]); // Ŀ�Ĺ������
				} else {
					dwbkDto.setSaimtrecode(singDto[2]); // Ŀ�Ĺ������
				}
				String strTipsTaxOrg = "";
				if(ITFECommonConstant.ISCONVERT.equals("0")){
					strTipsTaxOrg = singDto[1];
					dwbkDto.setStaxorgcode(strTipsTaxOrg);
					taxOrgCodeMap.put(strTipsTaxOrg, "");
				}else{
					TsConverttaxorgDto convertTaxOrgDto = new TsConverttaxorgDto();
					// �����������
					convertTaxOrgDto.setSorgcode(bookorgCode);
					// �����������
					convertTaxOrgDto.setStrecode(trecode);
					// TBS���ջ��ش���
					convertTaxOrgDto.setStbstaxorgcode(singDto[1]);
					List dtoL = CommonFacade.getODB().findRsByDtoWithUR(
							convertTaxOrgDto);
					
					if (dtoL == null || dtoL.size() == 0) {
						multi.getErrorList().add(
								"�˿��ļ�[" + fi.getName() + "]�й���������룺" + trecode
										+ ",���ջ��ش��룺" + singDto[1]
										+ " û��ά��'���ջ��ض���'!");
					} else {
						TsConverttaxorgDto taxogdto = (TsConverttaxorgDto) dtoL
								.get(0);
						// Tips���ջ��ش���
						strTipsTaxOrg = taxogdto.getStcbstaxorgcode();
						// ���ջ��ش���
						dwbkDto.setStaxorgcode(strTipsTaxOrg);
						taxOrgCodeMap.put(strTipsTaxOrg, "");
					}
				}
				

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
				voulist.add(strTipsTaxOrg.trim() + "," + singDto[0].trim());
				int voucodeSetOldSize = voucodeSet.size();
				voucodeSet.add(strTipsTaxOrg.trim() + singDto[0].trim());
				int voucodeSetNewSize = voucodeSet.size();
				if (voucodeSetOldSize == voucodeSetNewSize)
					multi.getErrorList().add(
							"�˿��ļ�[" + fi.getName() + "]�д���ƾ֤���" + singDto[0]
									+ "�ظ�!");

				dwbkDto.setCbdglevel(singDto[4]); // Ԥ�㼶��
				// ���⼶����Ԥ�㼶��У��
				if (!"0".equals(ITFECommonConstant.IFVERIFYTRELEVEL)) {
					String errorinfo = VerifyParamTrans.verifyTreasuryLevelDW(
							trecode, (String) treMap.get(trecode), singDto[4]);
					if (!"".equals(errorinfo)) {
						multi.getErrorList().add(
								"�˿��ļ�[" + fi.getName() + "]ƾ֤���Ϊ[" + singDto[0]
										+ "]�е�Ԥ�㼶�Σ�" + singDto[4]
										+ " ��Ӧ�Ĺ��⼶��У�����!" + "[" + errorinfo
										+ "]");
					}
				}
				dwbkDto.setCbdgkind(singDto[5]); // Ԥ������

				TsBudgetsubjectDto bsdto = new TsBudgetsubjectDto();
				bsdto.setSorgcode(bookorgCode);
				bsdto.setSsubjectcode(singDto[6].trim());
				List<IDto> list = CommonFacade.getODB()
						.findRsByDtoWithUR(bsdto);
				if (list == null || list.size() == 0) {
					multi.getErrorList().add(
							"�˿��ļ�[" + fi.getName() + "]�еĿ�Ŀ���룺" + singDto[6]
									+ " û���ҵ���Ӧ��Ϣ!");
				}
				dwbkDto.setSbdgsbtcode(singDto[6]); // ��Ŀ����
				if (StateConstant.shareBudgetLevel.equals(singDto[4]) && null==singDto[7]) {
					multi.getErrorList().add("Ԥ�㼶��Ϊ����������־����Ϊ�գ�");
				}
				// �жϸ�����־��Ӧ��ϵ�Ĵ�����
				if(!ITFECommonConstant.ISCONVERT.equals("0")){
					if (null != singDto[7] && !"".equals(singDto[7])) { // ������־
						if (!viceMap.containsKey(singDto[2] + singDto[6]
								+ singDto[7])) {
							if (!viceMap.containsKey(singDto[2] + singDto[7])) {
								if (!viceMap.containsKey(singDto[6] + singDto[7])) {
									if (!viceMap.containsKey(singDto[7])) {
										multi.getErrorList().add(
												"�˿��ļ�[" + fi.getName() + "]�и�����־��"
														+ singDto[7]
														+ " û����'������־����ά��'������ά��!");
									}
								}
							}
						}
					}
				}
				dwbkDto.setSastflag(singDto[7]);
				dwbkDto.setSdwbkreasoncode(singDto[8]); // �˿�ԭ�����
				dwbkDto.setSdwbkby(singDto[9]); // �˿�����
				dwbkDto.setSexamorg(singDto[10]); // ��������
				dwbkDto.setDaccept(TimeFacade.getCurrentDateTime());// D_ACCEPT�������ڲ���Ϊ��;

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
				} else {
					dwbkDto.setFamt(new BigDecimal("0.00"));
				}

				dwbkDto.setCbckflag(singDto[14]); // �˻ر�־
				dwbkDto.setSpayeeacct(singDto[15]); // �տ��ʺ�
				dwbkDto.setSpayeecode(singDto[16]); // �տλ����
				String errChi_17 = VerifyParamTrans.verifyNotUsableChinese(singDto[17]);
				if(null != errChi_17 && !"".equals(errChi_17)) {
					multi.getErrorList().add("�˿��ļ�[" + fi.getName()+ "]ƾ֤���Ϊ[" + singDto[0]+ "]���տ������ƴ��ڷǷ��ַ���"+errChi_17);
				}
				if (singDto[17] != null && singDto[17].length() > 60) {
					dwbkDto.setSpayeename(singDto[17].substring(0, 60)); // �տ����˺�����
				} else {
					dwbkDto.setSpayeename(singDto[17]); // �տ����˺�����
				}
				if (singDto[18].trim().length() == 12) {// �к����Ϊ12λ����֧��ϵͳ�кŴ���
					dwbkDto.setSpayeeopnbnkno(singDto[18].trim());
				} else {
					TsGenbankandreckbankDto bankdto = new TsGenbankandreckbankDto();
					bankdto.setSbookorgcode(bookorgCode);
					bankdto.setSgenbankcode(singDto[18].trim());
					List dot = CommonFacade.getODB().findRsByDtoWithUR(bankdto);
					if (dot == null || dot.size() == 0) {
						multi.getErrorList().add(
								"�˿��ļ�[" + fi.getName() + "]���ݿ����д��ࣺ'"
										+ singDto[18].trim()
										+ "' û����'���д�����֧���кŶ�Ӧ��ϵ����' ���ҵ���Ӧ"
										+ "���չ�ϵ������ά��!");
					} else {
						TsGenbankandreckbankDto bdto = (TsGenbankandreckbankDto) dot
								.get(0);
						String spayeebnkno = bdto.getSreckbankcode();
						dwbkDto.setSpayeeopnbnkno(spayeebnkno);// �����д���(����ת�����տ��˿������к�)
					}
				}
				/**
				 * �㶫���Ӹ����ֶΣ��ж�����ֶ���ĿΪ20�������һλ���븽���ֶΣ�������ԣ����˷���û���ã�TIPS���
				 */
				if (singDto.length == 20) {
					String errChi_19 = VerifyParamTrans.verifyNotUsableChinese(singDto[19]);
					if(null != errChi_19 && !"".equals(errChi_19)) {
						multi.getErrorList().add("�˿��ļ�[" + fi.getName()+ "]ƾ֤���Ϊ[" + singDto[0]+ "]�и��Դ��ڷǷ��ַ���"+errChi_19);
					}
					if (null != singDto[19] && !"".equals(singDto[19])) {
						dwbkDto.setSaddword(singDto[19]);
					}
				}

				dwbkDto.setSbookorgcode(bookorgCode);// �����������
				dwbkDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
				dwbkDto.setCtrimflag(ctrimflag); // �����ڱ�־��Ϊ��
				dwbkDto.setSbiztype(biztype);// ҵ������
				dwbkDto.setDvoucher(CommonUtil.strToDate(fi.getName()
						.substring(0, 8))); // ��Ʊ���ڲ�Ϊ��
				dwbkDto.setSelecvouno(singDto[0]); // ����ƾ֤��Ų���Ϊ��

				dwbkDto.setSfilename(fi.getName()); // �����ļ���
				dwbkDto.setSpackageno(""); // ����ˮ��
				dwbkDtoList.add(dwbkDto);
				taxOrgCodeMap.put(dwbkDto.getStaxorgcode(), "");
			}
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("�˿⵼���ļ��������� \n" + e.getMessage(), e);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("���ݿ��ѯ�쳣 \n" + e.getMessage(), e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("���ݿ��ѯ�쳣 \n" + e.getMessage(), e);
		} catch (IOException e) {
			log.error(e);
			throw new ITFEBizException("�ļ���֤ǩ���쳣 \n" + e.getMessage(), e);
		}
		for (String taxOrgCode : taxOrgCodeMap.keySet()) {
			List<TbsTvDwbkDto> taxOrgCodeDwbkDtoList = new ArrayList();
			if (null != taxOrgCode && !"".equals(taxOrgCode)) {
				for (int k = 0; k < dwbkDtoList.size(); k++) {
					if (taxOrgCode.equals(dwbkDtoList.get(k).getStaxorgcode())) {
						taxOrgCodeDwbkDtoList.add(dwbkDtoList.get(k));
					}
				}
				taxOrgCodeDtoListMap.put(taxOrgCode, taxOrgCodeDwbkDtoList);
			}
		}

		for (String taxOrgCode : taxOrgCodeDtoListMap.keySet()) {
			List<TbsTvDwbkDto> taxOrgCodeDwbkDtoList = taxOrgCodeDtoListMap
					.get(taxOrgCode);
			int li_Detail = (taxOrgCodeDwbkDtoList.size() - 1) / 1000;
			for (int k = 0; k <= li_Detail; k++) {
				// ÿ�������ܽ��
				BigDecimal famtAll = new BigDecimal("0.00");
				// �ܱ���
				int li_Count = 0;
				int li_TempCount = 0;
				if (li_Detail == k) {
					li_TempCount = taxOrgCodeDwbkDtoList.size();
				} else {
					li_TempCount = (k + 1) * 1000;
				}
				String tmpPackNo = "";
				try {
					tmpPackNo = SequenceGenerator
							.changePackNoForLocal(SequenceGenerator
									.getNextByDb2(
											SequenceName.FILENAME_PACKNO_REF_SEQ,
											SequenceName.TRAID_SEQ_CACHE,
											SequenceName.TRAID_SEQ_STARTWITH,
											MsgConstant.SEQUENCE_MAX_DEF_VALUE));
				} catch (SequenceException e) {
					log.error(e);
					throw new ITFEBizException("��ȡ����ˮ���쳣 \n" + e.getMessage(),
							e);
				}
				String ls_TreCode = "";
				for (int j = k * 1000; j < li_TempCount; j++) {
					li_Count++;
					TbsTvDwbkDto tvDwbkDto = taxOrgCodeDwbkDtoList.get(j);
					famtAll = famtAll.add(tvDwbkDto.getFamt());
					tvDwbkDto.setSpackageno(tmpPackNo);
					dwbkDtoListReturn.add(tvDwbkDto);
					if (ls_TreCode.equals(""))
						ls_TreCode = tvDwbkDto.getSaimtrecode();
				}
				TvFilepackagerefDto packdto = new TvFilepackagerefDto();
				packdto.setSorgcode(bookorgCode);
				// �����������
				packdto.setStrecode(ls_TreCode);
				packdto.setSfilename(fi.getName());
				// ���ջ��ش���
				packdto.setStaxorgcode(taxOrgCode);
				packdto.setScommitdate(TimeFacade.getCurrentStringTime());
				packdto.setSaccdate(TimeFacade.getCurrentStringTime());
				packdto.setSpackageno(tmpPackNo);
				packdto.setSoperationtypecode(biztype);
				packdto.setIcount(li_Count);
				packdto.setNmoney(famtAll);

				packdto
						.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
				packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
				packdto.setSusercode(userid);
				packdto.setImodicount(0);
				packdto.setSpackageno(tmpPackNo);
				packageFileList.add(packdto);
			}
		}
		multi.setFatherDtos(dwbkDtoListReturn);
		multi.setPackDtos(packageFileList);
		multi.setVoulist(voulist);
		return multi;
	}

	/**
	 * ȡ�ù�������뼶�εĶ�Ӧ��ϵ
	 * 
	 * @throws ITFEBizException
	 */
	private HashMap<String, String> getTreLevelMap(String sbookorgcode)
			throws ITFEBizException {
		HashMap<String, String> tremap = new HashMap<String, String>();
		TsTreasuryDto trepk = new TsTreasuryDto();
		trepk.setSorgcode(sbookorgcode);
		try {
			List list = CommonFacade.getODB().findRsByDto(trepk);
			for (int i = 0; i < list.size(); i++) {
				TsTreasuryDto dto = (TsTreasuryDto) list.get(i);
				tremap.put(dto.getStrecode(), dto.getStrelevel());
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("ȡ�ù�������뼶�εĶ�Ӧ��ϵ�쳣 \n" + e.getMessage(),
					e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("ȡ�ù�������뼶�εĶ�Ӧ��ϵ�쳣 \n" + e.getMessage(),
					e);
		}
		return tremap;
	}

	/**
	 * ������־���չ�ϵ��
	 * 
	 * @throws ITFEBizException
	 */
	public HashMap<String, String> getViceSignMap(String sbookorgcode)
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

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}
}

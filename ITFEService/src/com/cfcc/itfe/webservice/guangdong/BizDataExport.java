package com.cfcc.itfe.webservice.guangdong;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.ocsp.RespData;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.ParamConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.TipsParamDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsMankeymodeDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.pk.TsOrganPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.dataquery.findatastatdown.FinDataStatDownService;
import com.cfcc.itfe.service.dataquery.tipsdataexport.TipsDataExportService;
import com.cfcc.itfe.service.para.paramtransform.ParamTransformService;
import com.cfcc.itfe.util.AreaSpecUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/*
 * ҵ�����ݵ���
 */
public class BizDataExport {
	private static Log log = LogFactory.getLog(BizDataExport.class);
	/*
	 * /<billType,ҵ������billType������ParamConstant.TABS�е�λ��>
	 */
	private Map<String, Integer> billTypeMap = new HashMap<String, Integer>();
	/**
	 * �ļ�·���ָ���
	 */
	private static String separator = System.getProperty("file.separator");

	/*
	 * ��ʼ��billTypeMap
	 */
	public BizDataExport() {
		billTypeMap.put("30", 0);
		billTypeMap.put("31", 1);
		billTypeMap.put("32", 2);
		billTypeMap.put("33", 3);
		billTypeMap.put("34", 4);
		billTypeMap.put("35", 5);
		billTypeMap.put("36", 6);
		billTypeMap.put("37", 7);
		billTypeMap.put("38", 8);
		billTypeMap.put("39", 9);
		billTypeMap.put("40", 10);
		billTypeMap.put("41", 11);
		billTypeMap.put("42", 12);
		billTypeMap.put("43", 13);
		billTypeMap.put("44", 14);
		billTypeMap.put("45", 15);
		billTypeMap.put("46", 16);
	}

	/*
	 * ҵ�����ݵ��� billTypeҵ������ paramStr�ԡ�,���ָ�Ĳ���---�������,ί������
	 */
	public String bizFileExport(String billType, String paramStr) {
		// ��������
		String ls_Return = "";
		try {
			/**
			 * 1��У����ֲ����Ƿ����Ҫ��
			 */
			String[] params = paramStr.split(",");
			String[] tempParams = new String[2];
			Arrays.fill(tempParams, "");
			for (int i = 0; i < params.length; i++) {
				tempParams[i] = params[i];
			}
			Map<String, TsTreasuryDto> treasuryInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			String trecode = tempParams[0];
			if (StringUtils.isBlank(trecode)) { // �������
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ������[�������]����Ϊ��!");
			} else {
				if (!treasuryInfo.containsKey(trecode)) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�������" + trecode + "������!");
				}
			}
			String ls_CommitDate = tempParams[1];
			if (StringUtils.isBlank(ls_CommitDate)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ������[ί������]����Ϊ��!");
			} else {
				if (!ls_CommitDate.matches("^20[0-9]{6}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ������[ί������]��ʽ��Ϊ[YYYYMMDD]!");
				}
			}
			String orgcode = treasuryInfo.get(trecode).getSorgcode();
			// ��ѯ�ļ������Ӧ��ϵ��
			TvFilepackagerefDto filepackagerefDto = new TvFilepackagerefDto();
			filepackagerefDto.setSorgcode(orgcode);
			filepackagerefDto.setStrecode(trecode);
			/** ����֧����һ��Ԥ��֧�����ļ������Ӧ��ϵ�����涼�����17 */
			filepackagerefDto.setSoperationtypecode(StringUtils.equals(billType, BizTypeConstant.BIZ_TYPE_PAY_OUT2) ? BizTypeConstant.BIZ_TYPE_PAY_OUT : billType);
			filepackagerefDto.setScommitdate(ls_CommitDate);
			filepackagerefDto.setSretcode(DealCodeConstants.DEALCODE_ITFE_RECEIPT);
			filepackagerefDto.setSchkstate(StateConstant.CONFIRMSTATE_YES);
			List<TvFilepackagerefDto> fileDtoList = CommonFacade.getODB().findRsByDtoWithUR(filepackagerefDto);
			if (fileDtoList != null && fileDtoList.size() > 0) {
				List<String> reports = new ArrayList<String>();
				for (TvFilepackagerefDto temDto : fileDtoList) {
					String fileName = temDto.getSfilename();
					if (StringUtils.isNotBlank(fileName) && //
							StringUtils.equals(billType, BizTypeConstant.BIZ_TYPE_PAY_OUT2) && //
							(endsWithIgnoreCase(fileName, "170.txt") || endsWithIgnoreCase(fileName, "171.txt"))) {
						continue; 
					}
					String fullFileName = ITFECommonConstant.FILE_ROOT_PATH + ITFECommonConstant.FILE_UPLOAD + orgcode + separator + ls_CommitDate + separator + fileName;
					File tempFile = new File(fullFileName);
					if (tempFile.isFile() && tempFile.exists()) {
						String reportString = FileUtil.getInstance().readFile(fullFileName);
						if (StringUtils.isNotBlank(reportString)) {
							reports.add(reportString);
						}
					}
				}
				if (reports != null && reports.size() > 0) {
					ls_Return = WsUtils.generateResultXml(StateConstant.COMMON_YES, "", "ҵ����������ɹ�!", reports);
				} else {
					ls_Return = WsUtils.generateResultXml(StateConstant.SUBMITSTATE_DONE.toString(), "�����ҵ�����ݲ�����(ֻ������ҵ��״̬Ϊ'�ѻ�ִ'������)!");
				}
			} else {
				return WsUtils.generateResultXml(StateConstant.SUBMITSTATE_DONE.toString(), "�����ҵ�����ݲ�����(ֻ������ҵ��״̬Ϊ'�ѻ�ִ'������)!");
			}
		} catch (Exception e) {
			log.error("�㶫ҵ��������������쳣(gd_webservice): " + e.getMessage(), e);
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�㶫ҵ��������������쳣!");
		}
		return ls_Return;
	}

	/*
	 * �������ݵ��� billTypeҵ������ paramStrΪ�������
	 */
	public String ParamDataExport(String billType, String paramStr,String splitStr) {
		String ls_Return = "";
		try {
			// ��ѯDto
			TrStockdayrptDto fdto = new TrStockdayrptDto();
			/**
			 * 1��У����ֲ����Ƿ����Ҫ��
			 */
			Map<String, TsTreasuryDto> treasuryInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			String trecode = paramStr;
			if (StringUtils.isBlank(trecode)) { // �������
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ������[�������]����Ϊ��!");
			} else {
				if (!treasuryInfo.containsKey(trecode)) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�������" + trecode + "�����ڣ�");
				}
			}
			String orgcode = treasuryInfo.get(trecode).getSorgcode();
			ParamTransformService paramTransformService = new ParamTransformService();
			paramTransformService.setLoginInfo(packLoginInfo(orgcode));
			// String lineSplit = ",";// �ֶηָ��
			List checkList = new ArrayList();
			int nuber = billTypeMap.get(billType);
			TdEnumvalueDto dto = new TdEnumvalueDto();
			dto.setSvalue(ParamConstant.TABS[nuber][0]);
			dto.setSvaluecmt(ParamConstant.TABS[nuber][1]);
			checkList.add(dto);
			List<String> filelist = paramTransformService.export(checkList, splitStr, orgcode); 
			if (filelist != null && filelist.size() > 0) {
				if (filelist.get(0).startsWith("NODATA")) {
					return WsUtils.generateResultXml(StateConstant.SUBMITSTATE_DONE.toString(), "����Ĳ������ݲ�����!");
				} else {
					String reportFileName = filelist.get(0); // Ŀǰ����һ��ֻ������һ���ļ�
					String path = ITFECommonConstant.FILE_ROOT_PATH;// + separator;
					reportFileName = path + reportFileName;
					ls_Return = WsUtils.generateResultXml(StateConstant.COMMON_YES, "��������ɹ�!", FileUtil.getInstance().readFile(reportFileName));
				}
			} else {
				return WsUtils.generateResultXml(StateConstant.SUBMITSTATE_DONE.toString(), "����Ĳ������ݲ�����!");
			}
		} catch (Exception e) {
			log.error("�㶫������������쳣(gd_webservice): " + e.getMessage(), e);
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�㶫������������쳣(gd_webservice): " + e.getMessage());
		}
		return ls_Return;
	}

	/*
	 * �����������ձ���(3128) Data Export(����ʽ) paramStr�ԡ�,���ָ�Ĳ��� paramStr���� ��������-Ԥ�������ձ���(3128) :���ηֱ�Ϊ������� ������ʼ�������ڣ���ֹ����,���ջ��ش��룬Ͻ����־������˰Ʊ������Χ
	 */
	public String IncomeDayrptDataExportTable(String billType, String paramStr) {
		String ls_Return = "";
		try {
			// ��ѯDto
			// TrStockdayrptDto fdto = new TrStockdayrptDto();
			/**
			 * 1��У����ֲ����Ƿ����Ҫ��
			 */
			String[] params = paramStr.split(",");
			String[] tempParams = new String[6];
			Arrays.fill(tempParams, "");
			for (int i = 0; i < params.length; i++) {
				tempParams[i] = params[i];
			}
			Map<String, TsTreasuryDto> treasuryInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			Map<String, TsConvertfinorgDto> cacheFincInfo = SrvCacheFacade.cacheFincInfo(null);
			String trecode = tempParams[0];
			if (StringUtils.isBlank(trecode)) { // �������
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ������[�������]����Ϊ��!");
			} else {
				if (!treasuryInfo.containsKey(trecode)) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�������" + trecode + "�����ڣ�");
				}
				if (!cacheFincInfo.containsKey(trecode)) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�������[" + trecode + "]��Ӧ�Ĳ���������������ڻ�δά��!");
				}
				// fdto.setStrecode(trecode);
			}
			String startDate = tempParams[1]; // ��ʼ����
			if (StringUtils.isBlank(startDate)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ������[��ʼ����]����Ϊ��!");
			} else {
				if (!startDate.matches("^20[0-9]{6}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ������[��ʼ����]��ʽ��Ϊ[YYYYMMDD]!");
				}
			}
			String endDate = tempParams[2]; // ��ֹ����
			if (StringUtils.isBlank(endDate)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ������[��ֹ����]����Ϊ��!");
			} else {
				if (!endDate.matches("^20[0-9]{6}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ������[��ֹ����]��ʽ��Ϊ[YYYYMMDD]!");
				}
			}
			if (endDate.compareToIgnoreCase(startDate) < 0) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "��ʼ���ڱ���С�ڽ�������!");
			}

			String orgcode = treasuryInfo.get(trecode).getSorgcode(); // �����������
			String finOrgcode = cacheFincInfo.get(trecode).getSfinorgcode(); // ������������
			TipsDataExportService tipsDataExportService = new TipsDataExportService();
			tipsDataExportService.setLoginInfo(packLoginInfo(orgcode));

			// �����ļ��������
			Map<String, List<String>> map = new HashMap<String, List<String>>();

			/*
			 * ��ʽ��ʼ����
			 */

			List checklist = new ArrayList();
			TipsParamDto paramdto = new TipsParamDto();
			// paramdto.setSorgcode(orgcode);
			paramdto.setSorgcode(finOrgcode);
			paramdto.setStrecode(trecode);
			String staxorgcode = tempParams[3]; // ���ջ��ش���
			if (null != staxorgcode && !"".equals(staxorgcode)) {
				paramdto.setStaxorgcode(staxorgcode);
			}
			String sbeflag = tempParams[4]; // Ͻ����־
			if (null != sbeflag && !"".equals(sbeflag)) {
				paramdto.setSbeflag(sbeflag);
			}

			paramdto.setStartdate(new java.sql.Date(TimeFacade.parseDate(startDate).getTime()));
			paramdto.setEnddate(new java.sql.Date(TimeFacade.parseDate(endDate).getTime()));

			String ifSub = tempParams[5]; // ����˰Ʊ������Χ
			if (!StringUtils.isBlank(ifSub)) {
				if (!ifSub.matches("^[01]{1}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "����˰Ʊ������Χ����Ϊ[0-�������¼�����1-�����¼�����]!");
				}
				paramdto.setIfsub(ifSub);
			}
			paramdto.setExptype("0"); // //����0����ʽ����ø�����ʽ1
			if ("13".equals(billType)) {
				checklist.add(initValue("3128�������������ձ���", StateConstant.RecvTips_3128_SR));
			} else if ("14".equals(billType)) {
				checklist.add(initValue("3128�����������ձ���", StateConstant.RecvTips_3128_KC));
			} else if ("15".equals(billType)) {
				checklist.add(initValue("3139�������������ˮ��ϸ", StateConstant.RecvTips_3139));
			} else if ("16".equals(billType)) {
				checklist.add(initValue("3129�����������˰Ʊ��Ϣ", StateConstant.RecvTips_3129));
			}
			map = tipsDataExportService.generateTipsToFile(checklist, paramdto, "");
			// ���ص��ļ�·��
			List filelist = map.get("files");
			if (filelist != null && filelist.size() > 0) {
				// String reportFileName = (String) filelist.get(0); // Ŀǰ����һ��ֻ������һ���ļ�
				ls_Return = WsUtils.generateResultXml(StateConstant.COMMON_YES, "��������ɹ�!", packFileList2Result(filelist));
			} else {
				return WsUtils.generateResultXml(StateConstant.SUBMITSTATE_DONE.toString(), "����ı������ڻ�δ����!");
			}
			tipsDataExportService.deleteTheFiles(filelist); // ���ݴ���֮��ɾ��������������
		} catch (Exception e) {
			log.error("����ӿ�������������쳣(gd_webservice): " + e.getMessage(), e);
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "����ӿ�������������쳣(gd_webservice)!");
		}
		return ls_Return;
	}

	/**
	 * �������Ķ���ļ��ϲ���һ���ַ�����
	 * 
	 * @param fileList
	 * @return
	 * @throws Exception
	 */
	private String packFileList2Result(List fileList) throws Exception {
		StringBuffer sb = new StringBuffer();
		for (Object obj : fileList) {
			String reportFileName = (String) obj;
			sb.append(FileUtil.getInstance().readFile(reportFileName)).append("\r\n");
		}
		return sb.toString();
	}

	public TdEnumvalueDto initValue(String type, String mark) {
		TdEnumvalueDto dto = new TdEnumvalueDto();
		dto.setSvalue("�Ƿ񵼳�");
		dto.setSremark(mark);
		dto.setSvaluecmt(type);
		return dto;
	}

	/*
	 * ��������-Ԥ�������ձ���(3128) Data Export billType�������ͣ�paramStr�ԡ�,���ָ�Ĳ��� paramStr���� ��������-Ԥ�������ձ���(3128) :���ηֱ�Ϊ������� ���������ڣ�Ԥ�����࣬Ͻ����־������ϼƱ�־�������ڱ�־�����ջ��ش���
	 */
	public String IncomeDayrptDataExport(String billType, String paramStr) {
		try {
			/**
			 * 1��У����ֲ����Ƿ����Ҫ��
			 */
			String[] params = paramStr.split(",");
			String[] tempParams = new String[7];
			Arrays.fill(tempParams, "");
			for (int i = 0; i < params.length; i++) {
				tempParams[i] = params[i];
			}
			Map<String, TsTreasuryDto> treasuryInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			String trecode = tempParams[0];
			if (StringUtils.isBlank(trecode)) { // �������
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ������[�������]����Ϊ��!");
			} else {
				if (!treasuryInfo.containsKey(trecode)) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�������" + trecode + "�����ڣ�");
				}
			}
			String orgcode = treasuryInfo.get(trecode).getSorgcode();
			String reportDate = tempParams[1]; // ��������
			if (StringUtils.isBlank(reportDate)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ������[��������]����Ϊ��!");
			} else {
				if (!reportDate.matches("^20[0-9]{6}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ������[��������]��ʽ��Ϊ[YYYYMMDD]!");
				}
			}
			String budgettype = tempParams[2]; // Ԥ������
			if (StringUtils.isBlank(budgettype)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "Ԥ�����಻��Ϊ��![1-Ԥ���� 2-Ԥ����]");
			} else {
				if (!budgettype.matches("^[12]{1}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "Ԥ���������Ϊ[1-Ԥ���� 2-Ԥ����]!");
				}
			}
			String belongflag = tempParams[3]; // Ͻ����־
			if (StringUtils.isBlank(belongflag)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "Ͻ����־����Ϊ��![0-���� 1-ȫϽ 3-ȫϽ�Ǳ���]");
			} else {
				if (!belongflag.matches("^[013]{1}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "Ͻ����־����Ϊ[0-���� 1-ȫϽ 3-ȫϽ�Ǳ���]!");
				}
			}
			String dividegroup = tempParams[4]; // �Ƿ񺬿�ϼ�
			if (StringUtils.isBlank(dividegroup)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "����ϼƱ�־����Ϊ��![0-�� 1-��]");
			} else {
				if (!dividegroup.matches("^[01]{1}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "����ϼƱ�־����Ϊ[0-�� 1-��]!");
				}
			}
			String strimflag = tempParams[5]; // �����ڱ�־
			if (StringUtils.isBlank(strimflag)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ڱ�־����Ϊ��![0-������ 1-������]");
			} else {
				if (!strimflag.matches("^[01]{1}$")) {
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ڱ�־����Ϊ[0-������ 1-������]!");
				}
			}
			String taxorgcode = tempParams[6]; // ���ջ��ش���
			if (StringUtils.isBlank(taxorgcode)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "���ջ��ش��벻��Ϊ��!");
			}

			/**
			 * 2����ʼ��װ��̨������Ҫ�Ĳ���dto
			 */
			TrIncomedayrptDto incomedto = new TrIncomedayrptDto();
			incomedto.setStrecode(trecode);

			HashMap<String, TsConvertfinorgDto> finMap = SrvCacheFacade.cacheFincInfo(orgcode);
			if (!finMap.containsKey(trecode)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�������[" + trecode + "]��Ӧ�Ĳ���������������ڻ�δά��!");
			}
			// �������ش���
			incomedto.setSfinorgcode(finMap.get(trecode).getSfinorgcode());
			// ����
			incomedto.setSrptdate(reportDate);
			// Ԥ������
			incomedto.setSbudgettype(budgettype);
			// Ͻ����־
			incomedto.setSbelongflag(belongflag);
			// �Ƿ񺬿�ϼ�
			incomedto.setSdividegroup(dividegroup);
			// �����ڱ�־
			incomedto.setStrimflag(strimflag);
			// ���ջ��ش���
			incomedto.setStaxorgcode(taxorgcode);

			/**
			 * 3�����������ͷ�װ��list, Ŀǰֻ����һ������һ�����͵ı���
			 */
			List<TdEnumvalueDto> reportTypeList = new ArrayList<TdEnumvalueDto>();
			TdEnumvalueDto reportTypeDto = new TdEnumvalueDto();
			reportTypeDto.setSvalue(billType);
			reportTypeList.add(reportTypeDto);

			/**
			 * 4��ʵ����������񲢵���
			 */
			FinDataStatDownService finDataStatDownService = new FinDataStatDownService();
			finDataStatDownService.setLoginInfo(packLoginInfo(orgcode));

			List<String> reportFileList = finDataStatDownService.makeRptFile(incomedto, reportTypeList);

			/**
			 * 5�������ؽ��, ���������ظ����ö�
			 */
			if (reportFileList == null || reportFileList.size() == 0) {
				return WsUtils.generateResultXml(StateConstant.SUBMITSTATE_DONE.toString(), "����ı������ڻ�δ����!");
			}
			String reportFileName = ITFECommonConstant.FILE_ROOT_PATH + reportFileList.get(0); // Ŀǰ����һ��ֻ������һ���ļ�
			return WsUtils.generateResultXml(StateConstant.COMMON_YES, "��������ɹ�!", FileUtil.getInstance().readFile(reportFileName));

		} catch (Exception e) {
			log.error("�㶫������������쳣(gd_webservice): " + e.getMessage(), e);
			if(e != null && e.getCause() != null && e.getCause().getMessage().contains("�ط��������ջ��ش���")) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�㶫������������쳣(gd_webservice): " + e.getCause().getMessage());
			}
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�㶫������������쳣(gd_webservice): " + e.getMessage());
		}
	}

	/**
	 * Ϊ��ģ���¼, �Լ���װ��¼��Ϣ @TODO �������ɻ���
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	public ITFELoginInfo packLoginInfo(String orgcode) throws Exception {
		ITFELoginInfo itfeinfo = new ITFELoginInfo();
		TsUsersDto queryDto = new TsUsersDto();
		queryDto.setSorgcode(orgcode);
		queryDto.setSusertype(StateConstant.User_Type_Normal);
		List userList = CommonFacade.getODB().findRsByDto(queryDto);
		if (userList == null || userList.size() == 0) {
			throw new RuntimeException("�û���Ϣ������!");
		}
		TsUsersDto userDto = (TsUsersDto) userList.get(0);
		itfeinfo.setSuserName(userDto.getSusername());
		itfeinfo.setSuserType(userDto.getSusertype());
		itfeinfo.setSuserCode(userDto.getSusercode());
		itfeinfo.setOrgKind(userDto.getSorgcode());
		itfeinfo.setSorgcode(userDto.getSorgcode());
		itfeinfo.setCurrentDate(TimeFacade.getCurrent2StringTime());
		itfeinfo.setScertId(userDto.getScertid());

		/**
		 * ȡ�����Ƿ���ܵ�ֵ, ���ڻ��沢û�к������������(�Ժ����������Ϊkey��),����ֻ�ܴ����ݿ���ȡֵ
		 */
		TsOrganPK topk = new TsOrganPK();
		topk.setSorgcode(userDto.getSorgcode());
		IDto toDto = DatabaseFacade.getODB().find(topk);
		if (toDto == null) {
			throw new RuntimeException("�����������[" + userDto.getSorgcode() + "]�ں������������Ϣ�в�����!");
		}
		itfeinfo.setIscollect(((TsOrganDto) toDto).getSiscollect());

		// ȡ�ط���ɫ�ͼ��ܷ�ʽ,���ڶ�Ӧloginfo��
		HashMap<String, String> encryptMode = (HashMap<String, String>) ContextFactory.getApplicationContext().getBean("encryptMode");
		String area = AreaSpecUtil.getInstance().getArea();
		String sysflag = AreaSpecUtil.getInstance().getSysflag();
		itfeinfo.setEncryptMode(encryptMode);
		itfeinfo.setArea(area);
		itfeinfo.setSysflag(sysflag);
		// ȡ���ܷ�ʽ�ǰ�����������ܻ���ȫʡͳһ
		List<TsMankeymodeDto> _dtoList = DatabaseFacade.getODB().find(TsMankeymodeDto.class);
		if (_dtoList.size() > 0) {
			TsMankeymodeDto _dto = _dtoList.get(0);
			if (StateConstant.KEY_ALL.equals(_dto.getSkeymode())) {
				itfeinfo.setMankeyMode(StateConstant.KEY_ALL);// ȫʡͳһά��
			} else if (StateConstant.KEY_BOOK.equals(_dto.getSkeymode())) {
				itfeinfo.setMankeyMode(StateConstant.KEY_BOOK); // ����������ά��
			} else {// 
				itfeinfo.setMankeyMode(StateConstant.KEY_BOOK); // Ŀǰ��֧�ְ����������ȫʡͳһ
			}

		} else {
			itfeinfo.setMankeyMode(StateConstant.KEY_BOOK); // Ĭ�ϰ��պ�������ά��
		}
		// ������������
		itfeinfo.setPublicparam(ITFECommonConstant.PUBLICPARAM);
		return itfeinfo;
	}

	public static boolean endsWithIgnoreCase(String str, String suffix) {
		return endsWith(str, suffix, true);
	}

	private static boolean endsWith(String str, String suffix, boolean ignoreCase) {
		if ((str == null) || (suffix == null)) {
			return (str == null) && (suffix == null);
		}
		if (suffix.length() > str.length()) {
			return false;
		}
		int strOffset = str.length() - suffix.length();
		return str.regionMatches(ignoreCase, strOffset, suffix, 0, suffix.length());
	}
}

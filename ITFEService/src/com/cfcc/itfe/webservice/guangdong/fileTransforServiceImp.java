package com.cfcc.itfe.webservice.guangdong;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.FileResolveCommonService;
import com.cfcc.itfe.service.recbiz.uploadmodule.UploadUIService;
import com.cfcc.itfe.util.FileRootPathUtil;
import com.cfcc.itfe.util.FileUtil;

/**
 * �ṩ���㶫���õ�webservice�ӿ�<br>
 * 1��ҵ���ϴ��ӿ�<br>
 * 2������ش��ӿ�<br>
 * 
 * @author hua
 * 
 */
@WebService(endpointInterface = "com.cfcc.itfe.webservice.guangdong.IfileTransfor", serviceName = "IfileTransfor")
public class fileTransforServiceImp implements IfileTransfor {
	private static Log log = LogFactory.getLog(fileTransforServiceImp.class);
	private BizDataExport bizDataExport = new BizDataExport();;

	/**
	 * ҵ�����ݷ���
	 * 
	 * @param fileHandler
	 *            �ļ�����
	 * @param biztype
	 *            ҵ������
	 * @param paramStr
	 *            ԭǰ�ý�����Ҫ¼���Ҫ�أ��Զ��ŷָ�
	 * @param fileName
	 *            �ļ�����
	 * @param treCode
	 *            �������
	 * @return
	 */
	public String sendCommBizData(String fileContent, String biztype, String paramStr, String fileName, String treCode) {
		long moment = System.currentTimeMillis();
		log.debug("====�㶫WebServiceҵ��ӿڵ��ÿ�ʼ(" + moment + "):[biztype: " + biztype + ", paramStr: " + paramStr + ", fileName: " + fileName + ", treCode: " + treCode + "]====");
		String ls_filekind = WsUtils.verifyBizType(biztype);
		if ("".equals(ls_filekind)) {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "ҵ������ֵ" + biztype + "���Ϲ淶!");
		}
		if (StringUtils.isBlank(treCode)) {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "������벻��Ϊ��!");
		}
		if (!WsUtils.checkFileName(biztype, fileName)) {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�ļ����Ʋ��Ϲ淶!");
		}

		List<String> fileList = new ArrayList<String>();
		Boolean isErrorFileNeedDel = Boolean.FALSE;// �����Ƿ���Ҫɾ�������ļ�
		// �����ļ������
		OutputStream os = null;
		try {
			// ���ݹ�����뻺��ȡ�����ڵĻ�������
			Map<String, TsTreasuryDto> treasuryInfo = SrvCacheFacade.cacheTreasuryInfo(null);
			if (!treasuryInfo.containsKey(treCode)) {
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�������" + treCode + "�����ڣ�");
			}
			String ls_OrgCode = treasuryInfo.get(treCode).getSorgcode();

			// ����ļ��Ƿ��Ѿ����ڣ������ļ����Ʋ����ظ�����Ϊ��ѯʱҪ���ļ���������ѯ
			String rootPath = FileRootPathUtil.getInstance().getRoot();
			String uploadFilePath = ITFECommonConstant.FILE_UPLOAD + ls_OrgCode + "/" + DateUtil.date2String2(TimeFacade.getCurrentDate());
			String filePath = rootPath + uploadFilePath;
			fileList.add(uploadFilePath + "/" + fileName);
			File file = new File(filePath, fileName);
			// 1.��鵱���ļ�Ŀ¼���Ƿ�����ļ�����ΪfileName���ļ���2.����TV_FILEPACKAGEREF���Ƿ���ڸ��ļ����Ƶļ�¼
			if (WsUtils.verifyImportRepeat(ls_OrgCode, treCode, fileName)) {// "���¼�ļ�����")
				return WsUtils.generateResultXml(StateConstant.COMMON_NO, "���ļ��Ѿ��Ѿ�����, �벻Ҫ�ظ��ϴ�!");
			} else {
				createDir(filePath, file); // ����Ŀ¼
				FileUtil.getInstance().writeFile(file.getAbsolutePath(), fileContent);
				// ��¼������־
				saveTvrecvlog(file.getAbsolutePath(), biztype, ls_OrgCode);
			}

			FileResolveCommonService frs = new FileResolveCommonService();
			ITFELoginInfo packLoginInfo = bizDataExport.packLoginInfo(ls_OrgCode);
			frs.setLoginInfo(packLoginInfo);
			MulitTableDto bizDto = null;
			if (BizTypeConstant.BIZ_TYPE_INCOME.equals(biztype)) { // �����ļ�
				try {
					FileResultDto fileResultDto = WsUtils.processIncome(fileName, file.getAbsolutePath(), ls_OrgCode, paramStr); // TODO �ʽ�������ˮ����ô����?
					// fileResultDto.setStrasrlno(paramStr);//�ʽ�������ˮ��
					UploadUIService uploadUIService = new UploadUIService();
					uploadUIService.setLoginInfo(packLoginInfo);
					uploadUIService.UploadDate(fileResultDto);
				} catch (Exception e) { // �������뵼��ӿ�û����صĽ������ֵ, ����ͨ���쳣���ж��Ƿ���ɹ�
					log.error("���������ļ������쳣(gd_webservice)", e);
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ļ��ϴ�ʧ��");
				}
				return WsUtils.generateResultXml(StateConstant.COMMON_YES, "�ļ��ϴ��ɹ�!");

			} else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY.equals(biztype)//
					|| BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY.equals(biztype)) { // ����֧�����
				bizDto = frs.loadFile(fileList, "", ls_filekind, null);

			} else if (BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK.equals(biztype)//
					|| BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK.equals(biztype)) { // ����֧������
				// �˿���Ҫ��¼�����ʽ���Ϣ
				if (StringUtils.isBlank(paramStr)) {
					isErrorFileNeedDel = Boolean.TRUE;
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "����֧���˿�����ҵ�������Ϣ����Ϊ��!(�����ʽ���Ϣ)");
				}
				String[] tempParams = paramStr.split(",");
				if (tempParams == null || tempParams.length != 5) {
					isErrorFileNeedDel = Boolean.TRUE;
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "����֧���˿�����ҵ�������Ϣ���Ϲ淶�������������, ����������Զ���(,)�ָ�!");
				}
				String errorInfo = WsUtils.checkPayreckBackParam(tempParams);
				if (StringUtils.isNotBlank(errorInfo)) {
					isErrorFileNeedDel = Boolean.TRUE;
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, errorInfo);
				}
				String famt = tempParams[0]; // �����ʽ��ܶ�
				String paydictno = tempParams[1]; // ֧���������
				String payentrustdate = tempParams[2]; // ֧��ί������
				String paymsgno = tempParams[3]; // ֧�����ı��
				String paysndbnkno = tempParams[4]; // ֧���������к�
				TvPayreckBankBackDto payreckbackdto = new TvPayreckBankBackDto();
				try {
					payreckbackdto.setFamt(new BigDecimal(famt));
				} catch (Exception e) {
					isErrorFileNeedDel = Boolean.TRUE;
					return WsUtils.generateResultXml(StateConstant.COMMON_NO, "����֧���˿�����ҵ�������Ϣ��[�����ʽ��ܶ�]��ʽ�Ƿ�!");
				}
				payreckbackdto.setSpaydictateno(paydictno);
				payreckbackdto.setDpayentrustdate(new java.sql.Date(TimeFacade.parseDate(payentrustdate, "yyyyMMdd").getTime()));
				payreckbackdto.setSpaymsgno(paymsgno);
				payreckbackdto.setSpaysndbnkno(paysndbnkno);
				bizDto = frs.loadFile(fileList, "", ls_filekind, payreckbackdto);

			} else {
				bizDto = frs.loadFile(fileList, biztype, ls_filekind, null); // ʵ�����˿�

			}
			if (bizDto != null) {
				if (bizDto.getErrorCount() == 0 && (bizDto.getErrorList() == null || bizDto.getErrorList().size() == 0)) {// "MulitTableDto".equals("У����سɹ�")
					return WsUtils.generateResultXml(StateConstant.COMMON_YES, "�ļ��ϴ��ɹ���");

				} else {
					// ���������ɾ�������ļ�
					WsUtils.delServerWrongFile(fileList);
					// FileU
					isErrorFileNeedDel = Boolean.TRUE;
					String errorInfo = WsUtils.packErrorInfo(bizDto.getErrorList());
					if (StringUtils.isNotBlank(errorInfo)) { // ֻ���������Ĵ�����Ϣʱ��ƴ�Ӵ�����Ϣ, ������ʾ���Ѻ�һЩ.
						return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�ļ��ϴ�ʧ��, ������Ϣ���£�\r\n" + errorInfo);
					} else {
						return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�ļ��ϴ�ʧ��!");
					}
				}
			}

		} catch (Exception e) {
			log.error("�㶫�����ļ������쳣(gd_webservice)��", e);
			isErrorFileNeedDel = Boolean.TRUE;
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {
					log.error("�ر�������쳣��", e);
				}
			}
			if (isErrorFileNeedDel) {
				try {
					WsUtils.delServerWrongFile(fileList);
				} catch (Exception e) {
					log.error("ɾ�������ļ������쳣!(gd_webservice)!");
				}
			}
			log.debug("====================�㶫WebServiceҵ��ӿڵ��ý���(" + moment + ")!====================");
		}

		return WsUtils.generateResultXml(StateConstant.COMMON_NO, "ϵͳ����δ֪�쳣, ���ٴγ��Ի���ϵ����Ա��");
	}

	/**
	 * ҵ����ˮ��ϸ���ݵ���
	 * 
	 * @param billType
	 *            ҵ������
	 * @param params
	 *            ԭ�������ݵ��������ѯ���������ַ������룬�Զ��ŷָ���<br>
	 * @param separator ָ�����ڷָ�ķ���(1-���ţ�2-tab����) ��ʱֻ�����ڲ�������
	 * @return
	 */
	public String readBizSeriData(String billType, String paramStr, String separator) {
		long moment = System.currentTimeMillis();
		log.debug("====�㶫WebService����ӿڵ��ÿ�ʼ(" + moment + "):[billType: " + billType + ", paramStr: " + paramStr + ", separator: " + separator + "]====");
		/**
		 * 1��У����ֲ����Ƿ����Ҫ��
		 */
		if (StringUtils.isBlank(billType)) {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�������Ͳ���Ϊ��!");
		}
		if (StringUtils.isBlank(paramStr) || StringUtils.isBlank(paramStr.replace(",", ""))) {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ��������Ϊ��!");
		}

		String[] params = paramStr.split(",");
		if (null == params || params.length == 0) {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "�����ѯ�������ж�����Զ���(,)�ָ�!");
		}
		String ls_Return = "";
		String useSplitStr = ","; 
		if(StringUtils.isNotBlank(separator)) {
			if(StringUtils.equals("1", separator.trim())) {
				useSplitStr = ",";
			} else if(StringUtils.equals("2", separator.trim())) {
				useSplitStr = "	";
			}
		}
		
		// �Բ��������жϵ�������
		if (params.length == 1) {
			// ��������
			ls_Return = bizDataExport.ParamDataExport(billType, paramStr,useSplitStr);
		} else if (params.length == 2) {
			// �Բ��������ж��Ƿ�Ϊҵ�����ݵ���
			ls_Return = bizDataExport.bizFileExport(billType, paramStr);
		} else if (params.length == 6) {
			// TIPS�·�����Ԥ�����뱨��--����ʽ
			ls_Return = bizDataExport.IncomeDayrptDataExportTable(billType, paramStr);
		} else if (params.length == 7) {
			// ��������-Ԥ�������ձ���(3128)
			ls_Return = bizDataExport.IncomeDayrptDataExport(billType, paramStr);
		} else {
			return WsUtils.generateResultXml(StateConstant.COMMON_NO, "��������Ϊ" + params.length + "�����ѯ������������ȷ!");
		}
		
		// ��Ҫ��ӡ����־���д�����ֹ�������ص��������⡣
		try {
			if(log.isDebugEnabled()) {
				if (ls_Return.length() > 1024) { // ������ȴ���1024���ʹ�ӡ���屨������
					if (StringUtils.contains(ls_Return, "<result>1</result>")) { //�ɹ��Ŵ�ӡ
						String replaceXml = ls_Return.replaceAll("(<report>)(\\S*?)(</report>)", "$1��������̫��,��չʾ!$3");
						log.debug("��������:" + billType + ";�������:" + paramStr + ";���ص����ݣ�" + replaceXml);
					} else {
						log.debug("��������:" + billType + ";�������:" + paramStr + ";���ص����ݣ����Ϊʧ��(������ݳ��ȴ���1024,�Ҳ�����<result>1</result>)!" );
					}
				} else {
					log.debug("��������:" + billType + ";�������:" + paramStr + ";���ص����ݣ�" + ls_Return);
				}
			} else {
				log.debug("��������:" + billType + ";�������:" + paramStr + ";���ص����ݣ�" + (ls_Return.contains("<result>1</result>")?"�ɹ�!":"�����쳣��������!"));
			}
		} catch (Exception e) {
			log.error(e);
		}
		log.debug("=======================�㶫WebService����ӿڵ��ý���(" + moment + ")!=======================");
		return ls_Return;
	}

	/**
	 * ���ݼ���ʱ�ǽ�����־
	 */
	private void saveTvrecvlog(String filePath, String biztype, String orgCode) throws ITFEBizException {
		// �ļ��ϴ���·��
		try {
			TvRecvlogDto dto = new TvRecvlogDto();
			// ȡ������־��ˮ
			dto.setSrecvno(StampFacade.getStampSendSeq("JS"));
			// ���ͻ�������
			dto.setSrecvorgcode(orgCode);
			// �ϴ�����
			dto.setSdate(DateUtil.date2String2(TSystemFacade.findDBSystemDate()));
			// ҵ������
			dto.setSoperationtypecode(biztype);
			// �ļ��ڷ������ĵ�ַ
			dto.setStitle(filePath);
			// ����ʱ��
			dto.setSrecvtime(TSystemFacade.getDBSystemTime());
			// ��������
			dto.setSsenddate(TimeFacade.getCurrentStringTime());
			// ������˵��
			dto.setSretcodedesc("�ϴ��ɹ�");
			dto.setSifsend(StateConstant.MSG_SENDER_FLAG_3);// �ļ���ʽ
			dto.setSturnsendflag(StateConstant.SendFinNo);// ת����־
			dto.setSdemo("�ļ��ϴ��ɹ�");
			DatabaseFacade.getODB().create(dto);
		} catch (Exception e) {
			log.error("���������־ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * ����Ŀ¼
	 * 
	 * @param filePath
	 * @param file
	 */
	private void createDir(String filePath, File file) {
		if (file.exists()) {// "Ŀ¼�ļ�����")
			file.delete();
		} else {
			// ���Ŀ¼������, �ȴ���
			File dir = new File(filePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}
	}
}

package com.cfcc.itfe.webservice.guangdong;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.encrypt.Base64;
import com.cfcc.itfe.webservice.guangdong.income.FileOperFacade;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.core.service.filetransfer.support.FileSystemConfig;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ��������webService����ֵ<br>
 * <?xml version="1.0" encoding="GBK"?><br>
 * <root><br>
 * <head><br>
 * <result>0</result><br>
 * <status></status><br>
 * <message>�ļ�������</message><br>
 * </head><br>
 * <body><br>
 * <report>��һ������</report><br>
 * </body><br>
 * </root><br>
 * 
 * @author hua
 * 
 */
public class WsUtils {
	private static Log log = LogFactory.getLog(WsUtils.class);

	/**
	 * ���ɷ�������(���ڽṹ��,��ֱ�Ӳ�ȡ�ַ���ƴ��,��û��ʹ��dom����)
	 * 
	 * @param result
	 *            0-ʧ�� 1-�ɹ�
	 * @param message
	 *            ��Ҫ���ص���ϸ��Ϣ
	 * @return
	 */
	public static String generateResultXml(String result, String message) {
		return generateResultXml(result, "", message, "");
	}

	/**
	 * ���ɷ�������(���ڽṹ��,��ֱ�Ӳ�ȡ�ַ���ƴ��,��û��ʹ��dom����)
	 * 
	 * @param result
	 *            0-ʧ�� 1-�ɹ�
	 * @param message
	 *            ��Ҫ���ص���ϸ��Ϣ
	 * @param report
	 *            ��������
	 * @return
	 */
	public static String generateResultXml(String result, String message, String report) {
		return generateResultXml(result, "", message, report);
	}

	/**
	 * ���ɷ�������(���ڽṹ��,��ֱ�Ӳ�ȡ�ַ���ƴ��,��û��ʹ��dom����)
	 * 
	 * @param result
	 *            0-ʧ�� 1-�ɹ�
	 * @param status
	 *            �ļ�����״̬ 0-δ���� 1-������ 2-���ύ 3-������ 4-����ɹ� 5-����ʧ��
	 * @param message
	 *            ��Ҫ���ص���ϸ��Ϣ
	 * @param report
	 *            ��������
	 * @return
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 */
	public static String generateResultXml(String result, String status, String message, String report) {
		StringBuffer retXml = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		retXml.append("<root>");
		retXml.append("<head>");
		retXml.append("<result>" + handleNull(result) + "</result>");
		retXml.append("<status>" + handleNull(status) + "</status>");
		retXml.append("<message>" + handleNull(message) + "</message>");
		retXml.append("</head>");
		retXml.append("<body>");
		retXml.append("<report>");
		try {
			retXml.append(StringUtils.isBlank(handleNull(report)) ? handleNull(report) : base64Encode(handleNull(report)));
		} catch (Exception e) {
			log.error("����WebService���ݳ����쳣(GD)", e);
		}
		retXml.append("</report>");
		retXml.append("</body>");
		retXml.append("</root>");
		String ls_Return = null;
		try {
			//�����ص�Stringת������ΪGBK
			ls_Return = new String(retXml.toString().getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ls_Return;
	}
	
	/**
	 * ���ɷ�������(���ڽṹ��,��ֱ�Ӳ�ȡ�ַ���ƴ��,��û��ʹ��dom����)
	 * 
	 * @param result
	 *            0-ʧ�� 1-�ɹ�
	 * @param status
	 *            �ļ�����״̬ 0-δ���� 1-������ 2-���ύ 3-������ 4-����ɹ� 5-����ʧ��
	 * @param message
	 *            ��Ҫ���ص���ϸ��Ϣ
	 * @param reports
	 *            ��������
	 *            ����
	 * @return
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 */
	public static String generateResultXml(String result, String status, String message, List<String> reports) {
		StringBuffer retXml = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\"?>");
		retXml.append("<root>");
		retXml.append("<head>");
		retXml.append("<result>" + handleNull(result) + "</result>");
		retXml.append("<status>" + handleNull(status) + "</status>");
		retXml.append("<message>" + handleNull(message) + "</message>");
		retXml.append("</head>");
		retXml.append("<body>");
		for(String report:reports){
			retXml.append("<report>");
			try {
				retXml.append(StringUtils.isBlank(handleNull(report)) ? handleNull(report) : base64Encode(handleNull(report)));
			} catch (Exception e) {
				log.error("����WebService���ݳ����쳣(GD)", e);
			}
			retXml.append("</report>");
		}
		retXml.append("</body>");
		retXml.append("</root>");
		String ls_Return = null;
		try {
			//�����ص�Stringת������ΪGBK
			ls_Return = new String(retXml.toString().getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ls_Return;
	}

	/**
	 * ����ΪNull�����
	 * 
	 * @param value
	 * @return
	 */
	public static String handleNull(String value) {
		if (StringUtils.isBlank(value)) {
			return "";
		}
		return value;
	}

	/**
	 * ���ַ�������base64����
	 * 
	 * @param src�ַ���
	 * @return base64�ַ���
	 * @throws UnsupportedEncodingException
	 */
	public static String base64Encode(String src) throws UnsupportedEncodingException, Exception {
		byte[] outByte = Base64.encode(src.getBytes());
		String ret = null;
		ret = new String(outByte, "GBK");
		return ret;
	}

	/**
	 * ��base64������ַ���������Դ�ַ��� gbk
	 * 
	 * @param src
	 *            base64������ַ���
	 * @return Դ�ַ���
	 * @throws UnsupportedEncodingException
	 */
	public static String base64Decode(String src) throws UnsupportedEncodingException, Exception {
		byte[] out = Base64.decode(src);

		String ret = null;
		ret = new String(out, "GBK");
		return ret;
	}

	/**
	 * У�鴫����ļ����Ƿ���Ϲ淶
	 * 
	 * @param biztype
	 *            ҵ������
	 * @param fileName
	 *            �ļ���
	 * @return true ���� false ������
	 */
	public static boolean checkFileName(String biztype, String srcFileName) {
		if (StringUtils.isBlank(biztype)) {
			return Boolean.FALSE;
		}
		if (StringUtils.isBlank(srcFileName)) {
			return Boolean.FALSE;
		}
		String newFileName = srcFileName.toLowerCase();
		if (StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN)// ֱ�Ӷ��
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN)// ��Ȩ���
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_INCOME)// ����˰Ʊ
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_RET_TREASURY)// �˿�
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_PAY_OUT)// һ��Ԥ��֧��
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_PAY_OUT2)// ����֧��
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)// ֱ�ӻ���
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)// ֱ���˿�
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)// ��Ȩ����
				|| StringUtils.equals(biztype, BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) { // ��Ȩ�˿�
			// ����8λ����+2λ��4λ���κ�+��λҵ������+�����ڱ�־
			String regular = "^20[0-9]{6}(\\w{2}||\\w{4})[0-9]{2}[01]{1}.txt$";
			boolean match = newFileName.matches(regular);
			if (match) {
				String fileNameBizType = newFileName.substring(newFileName.length() - 7, newFileName.lastIndexOf(".") - 1);
				if (StringUtils.isBlank(verifyBizType(fileNameBizType)) || !StringUtils.equals(fileNameBizType, biztype)) {
					return Boolean.FALSE;
				}
			} else {
				return Boolean.FALSE;
			}

		} else {
			// ����ҵ���ݲ�У��
		}

		return Boolean.TRUE;
	}

	/**
	 * ����ҵ������biztype�����ر��ı��
	 */
	public static String verifyBizType(String biztype) {
		String ls_Return = "";
		Map<String, String> bizTypeMap = new HashMap<String, String>();
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_INCOME, "����˰Ʊ");
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_PAY_OUT, MsgConstant.SHIBO_SHANDONG_DAORU);// ʵ���ʽ�
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_PAY_OUT2, MsgConstant.SHIBO_SHANDONG_DAORU);// ʵ���ʽ�(����֧��)
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN, MsgConstant.ZHIJIE_DAORU);// ֱ��֧�����
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN, MsgConstant.SHOUQUAN_DAORU);// ��Ȩ֧�����
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_RET_TREASURY, MsgConstant.TUIKU_SHANDONG_DAORU);// �˿⣨�㶫��ȡɽ���ӿڣ�
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_CORRECT, MsgConstant.GENGZHENG_DAORU);// ����
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY, MsgConstant.APPLYPAY_DAORU);// ����֧�����㣬ֱ��֧����������
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY, MsgConstant.APPLYPAY_DAORU);// ����֧������
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK, MsgConstant.APPLYPAY_BACK_DAORU);// ����֧���˿�����
		bizTypeMap.put(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK, MsgConstant.APPLYPAY_BACK_DAORU);// ����֧���˿�����
		if (bizTypeMap.containsKey(biztype))
			ls_Return = bizTypeMap.get(biztype);
		return ls_Return;
	}

	/**
	 * ���ļ��������صĴ�����Ϣ�б�ƴ�ӳ��ַ���
	 * 
	 * @param errorList
	 * @return
	 */
	public static String packErrorInfo(List<String> errorList) {
		StringBuffer sb = new StringBuffer("");
		if (errorList != null && errorList.size() > 0) {
			for (String error : errorList) {
				sb.append(error + "\r\n");
			}
		}
		return sb.toString();
	}

	/**
	 * У���Ƿ��ظ�����(��VerfyFileName.java��ֲ����, ��Ҫ����ӹ����������, �����Ϳ��԰��չ��������������)
	 * 
	 * @param sorgcode
	 * @param sfilename
	 * @throws ITFEBizException
	 */
	public static boolean verifyImportRepeat(String sorgcode, String trecode, String sfilename) throws ITFEBizException {
		TvFilepackagerefDto finddto = new TvFilepackagerefDto();
		finddto.setSorgcode(sorgcode);
		finddto.setStrecode(trecode);
		finddto.setSfilename(sfilename);
		try {
			List list = CommonFacade.getODB().findRsByDto(finddto);
			if (null == list || list.size() == 0) {
				return false;
			}
			return true;
		} catch (JAFDatabaseException e) {
			log.error("У�鵼���ļ����ظ���ʱ�������ݿ��쳣!", e);
			throw new ITFEBizException("У�鵼���ļ����ظ���ʱ�������ݿ��쳣!", e);
		} catch (ValidateException e) {
			log.error("У�鵼���ļ����ظ���ʱ�������ݿ��쳣!", e);
			throw new ITFEBizException("У�鵼���ļ����ظ���ʱ�������ݿ��쳣!", e);
		}
	}

	/**
	 * ɾ�������ļ�
	 * 
	 * @param filelist
	 * @throws ITFEBizException
	 */
	public static void delServerWrongFile(List<String> filelist) throws ITFEBizException {
		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
		// �ļ��ϴ���·��
		String root = sysconfig.getRoot();
		try {
			for (int i = 0; i < filelist.size(); i++) {
				File tmpfile = new File(root + filelist.get(i));
				if (tmpfile != null && tmpfile.isFile() && tmpfile.exists()) {
					tmpfile.getAbsoluteFile().delete();
					if (((String) filelist.get(i)).endsWith(".pas")) {
						File pasfile = new File(root + ((String) filelist.get(i)).replaceAll(".pas", ".tmp"));
						if (pasfile != null && pasfile.isFile() && pasfile.exists())
							pasfile.getAbsoluteFile().delete();

					} else {
						File pasfile = new File(root + ((String) filelist.get(i)).replaceAll(".txt", ".tmp"));
						if (pasfile != null && pasfile.isFile() && pasfile.exists())
							pasfile.getAbsoluteFile().delete();
					}
				}
			}
		} catch (Exception e) {
			log.error("ɾ���������ļ�ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * У���˿¼��Ϣ
	 * 
	 * @param paramStr
	 * @return
	 */
	public static String checkPayreckBackParam(String[] tempParams) {
		String famt = tempParams[0]; // �����ʽ��ܶ�
		String paydictno = tempParams[1]; // ֧���������
		String payentrustdate = tempParams[2]; // ֧��ί������
		String paymsgno = tempParams[3]; // ֧�����ı��
		String paysndbnkno = tempParams[4]; // ֧���������к�
		String errorInfoHead = "����֧���˿�����ҵ�������Ϣ��";
		if (StringUtils.isBlank(famt)) {
			return errorInfoHead + "[�����ʽ��ܶ�]����Ϊ��!";
		}
		if (StringUtils.isBlank(paydictno)) {
			return errorInfoHead + "[֧���������]����Ϊ��!";
		} else {
			if (!paydictno.matches("\\d{8}")) {
				return errorInfoHead + "[֧���������]��ʽ�Ƿ�, ӦΪ8λ����!";
			}
		}
		if (StringUtils.isBlank(payentrustdate)) {
			return errorInfoHead + "[֧��ί������]����Ϊ��!";
		} else {
			if (!payentrustdate.matches("20\\d{6}")) {
				return errorInfoHead + "[֧��ί������]��ʽ�Ƿ�, ��ʽӦΪ[YYYYMMDD]!";
			}
		}
		if (StringUtils.isBlank(paymsgno)) {
			return errorInfoHead + "[֧�����ı��]����Ϊ��!";
		}
		if (StringUtils.isBlank(paysndbnkno)) {
			return errorInfoHead + "[֧���������к�]����Ϊ��!";
		} else {
			if (!paysndbnkno.matches("\\d{12}")) {
				return errorInfoHead + "[֧���������к�]��ʽ�Ƿ�, ӦΪ12λ����!";
			}
		}
		return null;
	}

	/**
	 * ��ֲ�ͻ������뵼�����
	 * 
	 * @param filename
	 *            �ļ���
	 * @param filepath
	 *            �ļ�·��
	 * @param orgcode
	 *            ��������
	 * @param trasrlno
	 *            �ʽ�������ˮ��
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileResultDto processIncome(String filename, String filepath, String orgcode, String trasrlno) throws ITFEBizException {
		FileResultDto fileResultDto = FileOperFacade.dealIncomeFile(filepath, filename, orgcode);

		if (fileResultDto.getFileColumnLen() != 13) {
			// ����ҵ��ҪУ���ʽ�������ˮ���Ƿ���д
			if (null == trasrlno || "".equals(trasrlno.trim()) || trasrlno.trim().length() != 18) {
				// MessageDialog.openMessageDialog(null, "����ҵ���ʽ�������ˮ�ű�����д���ұ���Ϊ18λ��");
				trasrlno = "111111111111111111"; // Ĭ���ʽ�������ˮ��Ϊ18��1
			}
			fileResultDto.setIsError(false);
			fileResultDto.setStrasrlno(trasrlno.trim());
		} else {
			fileResultDto.setIsError(false);
		}
		return fileResultDto;
	}
}

package com.cfcc.itfe.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.constants.DeptoneGlobalBeanId;
import com.cfcc.deptone.common.core.exception.DecryptSecurityException;
import com.cfcc.deptone.common.core.exception.EncryptSecurityException;
import com.cfcc.deptone.common.core.security.ca.support.CASecurityHandler;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.jaf.core.loader.ContextFactory;

public class CASecurityUtil {

	private static Log logger = LogFactory.getLog(CASecurityUtil.class);

	/**
	 * ���ļ����ݽ�����ǩ
	 * 
	 * @param String
	 *            fileContent �������ݣ��ַ������ͣ�
	 * @throws ITFEBizException
	 */
	public static void verifySignDN(String fileContent) throws ITFEBizException {
		CASecurityHandler ca = (CASecurityHandler) ContextFactory.getApplicationContext().getBean(DeptoneGlobalBeanId.CaSecurityHandlerLocalTips);
		try {
			String signdn = ca.getSignDN(fileContent);
			if (null == signdn || "".equals(signdn.trim())) {
				logger.error("������ǩʧ�ܣ�");
				throw new ITFEBizException("������ǩʧ�ܣ�");
			}
		} catch (DecryptSecurityException e) {
			logger.error("������ǩʧ�ܣ�", e);
			throw new ITFEBizException("������ǩʧ�ܣ�", e);
		}
	}

	/**
	 * ���ļ����м�ǩ�������ؼ�ǩ����ļ�
	 * 
	 * @param String
	 *            xmlcontent ����ǩ���ļ�
	 * @return
	 * @throws ITFEBizException
	 */
	public static String getXmlSign(String xmlcontent) throws ITFEBizException {
		CASecurityHandler ca = (CASecurityHandler) ContextFactory.getApplicationContext().getBean("CaSecurityHandlerLocalTips");

		try {
			String signXml = ca.getSign(xmlcontent);
//			logger.debug("�ļ���ǩ�� " + (xmlcontent + signXml));
			return xmlcontent + signXml;
		} catch (EncryptSecurityException e) {
			logger.error("�ļ���ǩʧ�ܣ�", e);
			throw new ITFEBizException("�ļ���ǩʧ�ܣ�", e);
		}
	}
}

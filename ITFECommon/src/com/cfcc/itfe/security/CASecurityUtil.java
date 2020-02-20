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
	 * 对文件内容进行验签
	 * 
	 * @param String
	 *            fileContent 报文内容（字符串类型）
	 * @throws ITFEBizException
	 */
	public static void verifySignDN(String fileContent) throws ITFEBizException {
		CASecurityHandler ca = (CASecurityHandler) ContextFactory.getApplicationContext().getBean(DeptoneGlobalBeanId.CaSecurityHandlerLocalTips);
		try {
			String signdn = ca.getSignDN(fileContent);
			if (null == signdn || "".equals(signdn.trim())) {
				logger.error("报文验签失败！");
				throw new ITFEBizException("报文验签失败！");
			}
		} catch (DecryptSecurityException e) {
			logger.error("报文验签失败！", e);
			throw new ITFEBizException("报文验签失败！", e);
		}
	}

	/**
	 * 对文件进行加签，并返回加签后的文件
	 * 
	 * @param String
	 *            xmlcontent 待加签的文件
	 * @return
	 * @throws ITFEBizException
	 */
	public static String getXmlSign(String xmlcontent) throws ITFEBizException {
		CASecurityHandler ca = (CASecurityHandler) ContextFactory.getApplicationContext().getBean("CaSecurityHandlerLocalTips");

		try {
			String signXml = ca.getSign(xmlcontent);
//			logger.debug("文件加签后： " + (xmlcontent + signXml));
			return xmlcontent + signXml;
		} catch (EncryptSecurityException e) {
			logger.error("文件加签失败！", e);
			throw new ITFEBizException("文件加签失败！", e);
		}
	}
}

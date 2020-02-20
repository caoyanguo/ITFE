package com.cfcc.itfe.transformer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

import com.cfcc.itfe.security.SM3Process;
import com.cfcc.yak.exception.YakTransformerException;
import com.cfcc.yak.i18n.YakMessages;

public class SM3Transformer extends AbstractMessageAwareTransformer {

	private static Log logger = LogFactory.getLog(SM3Transformer.class);

	private String tradDirection;

	public Object transform(MuleMessage message, String outputEncoding) throws TransformerException {
		try {
			if ("sign".equals(tradDirection)) {
				// 对报文进行加签
				String xmlcontent = (String) message.getPayload();
				logger.info("SM3报文内容：" + xmlcontent);

				// return CASecurityUtil.getXmlSign(xmlcontent);
				// return SM3Process.calculateSign(xmlcontent, "icfcc");
				String sm3Sign = SM3Process.calculateSign(xmlcontent, "icfcc");
				return xmlcontent + "<!--" + sm3Sign + "-->";
			} else {
				String xmlcontent = (String) message.getPayload();
				// CASecurityUtil.verifySignDN(xmlcontent);
				SM3Process.checkSM3Sign(xmlcontent, "icfcc"); // 对接收的报文进行验签
				return message.getPayload();
			}
		} catch (Exception e) {
			logger.error(e, e);
			throw new YakTransformerException(YakMessages.msgTransformError(e.getMessage()), e);
		}
	}

	public String getTradDirection() {
		return tradDirection;
	}

	public void setTradDirection(String tradDirection) {
		this.tradDirection = tradDirection;
	}

}

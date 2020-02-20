package com.cfcc.itfe.transformer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

import com.cfcc.itfe.security.CASecurityUtil;
import com.cfcc.yak.exception.YakTransformerException;
import com.cfcc.yak.i18n.YakMessages;

public class CATransformer extends AbstractMessageAwareTransformer {

	private static Log logger = LogFactory.getLog(CATransformer.class);

	/**
	 * ��������(��ǩ[sign]����ǩ[verifysign])
	 */
	private String tradDirection;

	public Object transform(MuleMessage message, String outputEncoding) throws TransformerException {

		try {
			if ("sign".equals(tradDirection)) {
//				return message.getPayload();
				// �Ա��Ľ��м�ǩ
				String xmlcontent = (String) message.getPayload();
				logger.error("�������ݣ�" + xmlcontent);
				return CASecurityUtil.getXmlSign(xmlcontent);
			} else {
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

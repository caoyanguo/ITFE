package com.cfcc.itfe.transformer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;

public class ChangeMsgNoTransformer extends AbstractMessageAwareTransformer {
	
	private static Log logger = LogFactory.getLog(ChangeMsgNoTransformer.class);
	
	public Object transform(MuleMessage message, String outputEncoding) throws TransformerException {
		String msgno = (String)message.getProperty(MessagePropertyKeys.MSG_NO_KEY);
		//���ձ���Ϊ2201��2202ʱ��ת�����ı��Ϊ2201_IN��2202_IN
		if(MsgConstant.MSG_NO_2201.equals(msgno) 
				|| MsgConstant.MSG_NO_2202.equals(msgno)){
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY, msgno+"_IN");
			logger.debug("���ñ��ı��" + msgno + "Ϊ��" + msgno + "_IN");
		}
		return message.getPayload();
	}
}

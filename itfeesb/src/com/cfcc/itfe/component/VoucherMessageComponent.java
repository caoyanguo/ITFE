package com.cfcc.itfe.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.config.MuleProperties;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.msgmanager.core.IMsgManagerServer;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.yak.exception.YakTransformerException;
import com.cfcc.yak.i18n.YakMessages;

/**
 * 
 * ƾ֤���Ľ������
 * 
 */
public class VoucherMessageComponent implements Callable {
	
	private static Log logger = LogFactory.getLog(VoucherMessageComponent.class);

	public Object onCall(MuleEventContext eventContext) throws Exception {
		//ƾ֤���ı��
		String msgno = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_NO_KEY);
		long start=System.currentTimeMillis();
		eventContext.getMessage().setStringProperty(MuleProperties.MULE_REPLY_TO_STOP_PROPERTY, "true");
		eventContext.getMessage().setReplyTo(null);		
		// ���ձ���ʱ�������õĲ������ת��
		eventContext.transformMessage();						
       logger.debug("==============����" + msgno + "���Ĵ�����=============="+eventContext.getEncoding());
       try{		    	  
			IMsgManagerServer msgServer = (IMsgManagerServer) ContextFactory.getApplicationContext().getBean(
					MsgConstant.VOUCHER_MSG_SERVER + msgno);
			logger.info("XXXXXXXXXXXXXXXXXXXX="+(System.currentTimeMillis()-start)/1000);
			msgServer.dealMsg(eventContext);   	   		
       } catch(Exception e1){
    	   logger.debug("û���ҵ�ƥ��ı��ı��[" + msgno + "]������");
			Exception e=new Exception("û���ҵ�ƥ��ı��ı��[" + msgno + "]������");
			eventContext.getMessage().setStringProperty(
					MessagePropertyKeys.MSG_NO_KEY, MsgConstant.VOUCHER_MSG_SERVER +msgno);
			throw new YakTransformerException(YakMessages.msgTransformError(e
					.getMessage()), e);
		}
        return null;
	}
		

}

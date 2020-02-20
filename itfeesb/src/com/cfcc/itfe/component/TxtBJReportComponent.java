package com.cfcc.itfe.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.msgmanager.core.IMsgManagerServer;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.yak.exception.YakTransformerException;
import com.cfcc.yak.i18n.YakMessages;

/**
 * 
 * 报文发送接收
 * 
 */
public class TxtBJReportComponent implements Callable {
	
	private static Log logger = LogFactory.getLog(TxtBJReportComponent.class);

	public Object onCall(MuleEventContext eventContext) throws Exception {
		try{
			eventContext.transformMessage();
			MuleMessage muleMsg = eventContext.getMessage();
			String fileName = (String) eventContext.getMessage().getProperty(
					MessagePropertyKeys.MSG_FILE_NAME);
			String msg =FileUtil.getInstance().readFile(fileName);
			muleMsg.setPayload(msg);
			eventContext.sendEvent(muleMsg, "SendReportToBJFinc");
		}catch(Exception e){
			logger.error(e);
			throw new YakTransformerException(YakMessages.msgTransformError(e
					.getMessage()), e);	
		}
        return eventContext;
	}
		

}

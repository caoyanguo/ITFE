package com.cfcc.itfe.component;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.lifecycle.Callable;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.msgmanager.core.IMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 
 * 报文发送接收
 * 
 */
public class TimerProcSaveMessageComponent implements Callable {
	
	private static Log logger = LogFactory.getLog(TimerProcSaveMessageComponent.class);

	public Object onCall(MuleEventContext eventContext) throws Exception {
		long start=System.currentTimeMillis();	
		// 接收报文时调用配置的插件进行转化
		eventContext.transformMessage();
		String msgno = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_NO_KEY);
			
        if (ITFECommonConstant.msgNoConfig.containsKey(msgno)) {
			/**
			 * 说明：要调用此方法，必须在conf/config/bizconfig/MsgManagerServer.xml中配置好。
			 */
			IMsgManagerServer msgServer = (IMsgManagerServer) ContextFactory
					.getApplicationContext().getBean(
							MsgConstant.SPRING_MSG_PROC_SERVER + msgno);
			msgServer.dealMsg(eventContext);
		
			logger.info("XXXXXXXXXXXXXXXXXXXX="+(System.currentTimeMillis()-start)/1000);
		}
        return eventContext.getMessage().getPayload();
	}
		

}

package com.cfcc.itfe.msgmanager.msg;


import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;

public class Recv9003MsgServer extends AbstractMsgManagerServer {
	
	private static Log logger = LogFactory.getLog(Recv9003MsgServer.class);

	/**
	 * 报文信息处理
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap msgMap = (HashMap) cfxMap.get("MSG");
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			String desorgcode = (String) headMap.get("DES");// 接收机构代码
			String sendorgcode = (String) headMap.get("SRC");// 发送机构代码
			String msgNo = (String) headMap.get("MsgNo");// 报文编号
//			String msgRef  = (String)headMap.get("MsgRef");
			HashMap TraStatusCheck9003 = (HashMap) msgMap.get("TraStatusCheck9003");
			String SendOrgCode = (String)TraStatusCheck9003.get("SendOrgCode");
//			String SearchType = (String)TraStatusCheck9003.get("SearchType");
//			String OriMsgNo = (String)TraStatusCheck9003.get("OriMsgNo");
			String OriEntrustDate = (String)TraStatusCheck9003.get("OriEntrustDate");
//			String OriPackNo = (String)TraStatusCheck9003.get("OriPackNo");
			String OriTraNo = (String)TraStatusCheck9003.get("OriTraNo");

			
			String msgid = (String) headMap.get("MsgID");
//			String sdate = (String) headMap.get("WorkDate");
			String _srecvno  = StampFacade.getStampSendSeq("JS");
			String _ssendno= StampFacade.getStampSendSeq("FS");
			

			// 记接收日志
			MsgLogFacade.writeRcvLog(_srecvno, _ssendno, (String) headMap
					.get("SRC"), OriEntrustDate, (String) headMap.get("MsgNo"),
					(String) headMap.get("SRC"), (String) eventContext.getMessage()
							.getProperty("XML_MSG_FILE_PATH"), 0, null, OriTraNo, null,
							SendOrgCode, SendOrgCode, null, (String) headMap.get("MsgID"),
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
					(String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_RECV+(String) headMap.get("MsgNo"));

			// 写发送日志
			MsgLogFacade.writeSendLog(_ssendno, _srecvno, (String) headMap
					.get("SRC"), (String) headMap.get("DES"), OriEntrustDate,
					(String) headMap.get("MsgNo"), (String) eventContext
							.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
					null, OriTraNo, null, null, SendOrgCode, null, (String) headMap
							.get("MsgID"), DealCodeConstants.DEALCODE_ITFE_SEND,
					null, null, (String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND+(String) headMap.get("MsgNo"));

			// 记录接收财政的消息记录--广西MQ消息ID匹配机制修改
			String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
			String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
			MsgLogFacade.writeMQMessageLog(sendorgcode, desorgcode, msgNo, msgid, TimeFacade.getCurrentStringTime(), "", jmsMessageID, jmsCorrelationID, SendOrgCode);
			
			// 获得原报文，重新发送
			Object msg = eventContext.getMessage().getProperty("MSG_INFO");
			eventContext.getMessage().setPayload(msg);
		} catch (SequenceException e1) {
			logger.error("取接收或者发送流水号错误!", e1);
			throw new ITFEBizException("取接收或者发送流水号错误!", e1);
		}
	}

}

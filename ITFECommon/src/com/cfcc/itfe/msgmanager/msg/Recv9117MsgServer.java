package com.cfcc.itfe.msgmanager.msg;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;

public class Recv9117MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9117MsgServer.class);

	/**
	 * 支出核对包重发请求（9117） 用财政系统未收到支出核对包3200时，申请TIPS重新发送
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();

		HashMap cfxMap = (HashMap) muleMessage.getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap getMsg9117Map = (HashMap) msgMap.get("GetMsg9117");

		// 解析报文中的请求信息CFX->MSG->GetMsg9117
		String sendOrgCode = (String) getMsg9117Map.get("SendOrgCode"); // 发起机构代码
		String ntrustDate = (String) getMsg9117Map.get("EntrustDate"); // 委托日期
		String oriPackMsgNo = (String) getMsg9117Map.get("OriPackMsgNo");// 原包报文编号
		String oriChkDate = (String) getMsg9117Map.get("OriChkDate");// 原核对日期
		String oriPackNo = (String) getMsg9117Map.get("OriPackNo");// 原包流水号
		String orgType = (String) getMsg9117Map.get("OrgType");// 机构类型

		String _srecvno = null;
		String _ssendno = null;
		try {
			_srecvno = StampFacade.getStampSendSeq("JS");
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e1) {
			logger.error("取接收或者发送流水号错误!", e1);
			throw new ITFEBizException("取接收或者发送流水号错误!", e1);
		}

		// 记接收日志
		MsgLogFacade.writeRcvLog(_srecvno, _ssendno, (String) headMap
				.get("DES"), ntrustDate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0, null, null, null,
				null, sendOrgCode, null, (String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, null);

		// 写发送日志
		MsgLogFacade.writeSendLog(_ssendno, _srecvno, (String) headMap
				.get("SRC"), (String) headMap.get("DES"), ntrustDate,
				(String) headMap.get("MsgNo"), (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				null, null, null, null, sendOrgCode, null, (String) headMap
						.get("MsgID"), DealCodeConstants.DEALCODE_ITFE_SEND,
				null, null, (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, null);

		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

/**
 * 三方机构验证与撤销
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * @author wangtuo
 * 
 */
public class Recv9114MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9114MsgServer.class);

	/**
	 * 报文信息处理
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// 解析报文头 headMap
		String desorgcode = (String) headMap.get("DES");// 接收机构代码
		String sendorgcode = (String) headMap.get("SRC");// 发送机构代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号
		String sbookorgcode=sendorgcode;
		
		HashMap proveinfo9114 = (HashMap) msgMap.get("ProveInfo9114");
		
		String billOrgCode = (String) proveinfo9114.get("SendOrgCode"); //发起方代码
		String EntrustDate = (String) proveinfo9114.get("EntrustDate");//委托日期
		String VCNo = (String) proveinfo9114.get("VCNo");//验证撤销序号
		/*
		 * 接收/发送日志
		 */
		String recvseqno;// 接收日志流水号
		String sendseqno;// 发送日志流水号
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
			sendseqno = StampFacade.getStampSendSeq("FS"); // 取发送日志流水
			TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null).get(billOrgCode);
			if (null!=_dto) {
				sbookorgcode =_dto.getSorgcode();
			}
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("取接收/发送日志SEQ出错");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("根据征收机关代码取核算主体代码出错！");
		}
		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode, EntrustDate, msgNo,
				billOrgCode, (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), VCNo, null, null, billOrgCode, null,msgid,
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_RECV+msgNo);

		// 写发送日志
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, sbookorgcode, desorgcode, EntrustDate,
				msgNo, (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), VCNo, null, null, billOrgCode, null,
				msgid,
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND+msgNo);
		
		// 记录接收财政的消息记录--广西MQ消息ID匹配机制修改
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendorgcode, desorgcode, msgNo, msgid, TimeFacade.getCurrentStringTime(), "", jmsMessageID, jmsCorrelationID, billOrgCode);
		
		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

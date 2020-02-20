/**
 * 实时扣税冲正请求
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvTaxCancelDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * @author wangtuo
 * 
 */
public class Recv1021MsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Recv1021MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap rushapply1021 = (HashMap) msgMap.get("RushApply1021");
	

		// 解析报文头 headMap
		String orgcode = (String) headMap.get("DES");// 接收机构代码
		String sendorgcode = (String) headMap.get("SRC");// 发送机构代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号
		String sbookorgcode=sendorgcode;

		// 解析冲正申请信息 msgMap-->rushapply1021
		String taxorgcode = (String) rushapply1021.get("TaxOrgCode");// 征收机关代码
		String entrustdate = (String) rushapply1021.get("EntrustDate");// 委托日期
		String cancelno = (String) rushapply1021.get("CancelNo");// 冲正申请号
		String orientrustdate = (String) rushapply1021.get("OriEntrustDate");// 原委托日期
		String oritrano = (String) rushapply1021.get("OriTraNo");// 原交易流水号
		String cancelreason = (String) rushapply1021.get("CancelReason");// 冲正原因

		String dealCode = StateConstant.DEAL_CODE_0000;// 默认成功
		String detailDealCode = StateConstant.DATA_FLAG_CHECK;// 默认接收成功
		String cancelanswer = StateConstant.CANCELANSWER_FLAG_NOCHECK;// 默认未应答

		// 组织DTo准备保存数据******************************************************
		// 实时冲正信息(1021) TvTaxCancelDto
		TvTaxCancelDto taxcanceldto = new TvTaxCancelDto();

		String bizseq;// 业务流水号
		try {
			bizseq = StampFacade.getBizSeq("SSCZ");
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("取业务流水号SEQ出错");
		}
		taxcanceldto.setSseq(bizseq);// 业务流水号
		taxcanceldto.setStaxorgcode(taxorgcode);// 征收机关代码
		taxcanceldto.setSentrustdate(entrustdate);// 委托日期
		taxcanceldto.setScancelappno(cancelno);// 冲正申请号
		taxcanceldto.setSmsgid(msgid);// 报文标识号
		taxcanceldto.setSorientrustdate(orientrustdate);// 原委托日期
		taxcanceldto.setSoritrano(oritrano);// 原交易流水号
		taxcanceldto.setScancelreason(cancelreason);// 冲正原因
		taxcanceldto.setCcancelanswer(cancelanswer);// 冲正应答
		taxcanceldto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// 更新时间不能为空

		// 保存实时冲正信息数据 ,可能有dto是空的情况
		if (null != taxcanceldto) {
			try {
				// 保存数据
				DatabaseFacade.getDb().create(taxcanceldto);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("数据库出错", e);
			}
		}

		/*
		 * 接收/发送日志
		 */
		String recvseqno;// 接收日志流水号
		String sendseqno;// 发送日志流水号
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
			sendseqno = StampFacade.getStampSendSeq("FS"); // 取发送日志流水
			TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null).get(taxorgcode);
			if (null!=_dto) {
				sbookorgcode =_dto.getSorgcode();
			}
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("取接收日志SEQ出错");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("根据财政代码取核算主体代码失败！");
		}

		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode, entrustdate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), cancelno, null, null, taxorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_RECV);

		// 写发送日志
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, sbookorgcode, (String) headMap.get("DES"), entrustdate,
				(String) headMap.get("MsgNo"), (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
				new BigDecimal(0), null, null, null, taxorgcode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND);
		
		// 记录接收财政的消息记录--广西MQ消息ID匹配机制修改
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendorgcode, orgcode, msgNo, msgid, TimeFacade.getCurrentStringTime(), "", jmsMessageID, jmsCorrelationID, taxorgcode);

		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

/**
 * 
 */
package com.cfcc.itfe.msgmanager.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor9120;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author wangtuo
 * 
 */
public class Send9120MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Send9120MsgServer.class);

	/**
	 * 报文信息处理
	 * 
	 * @param eventContext
	 * @throws ITFEBizException
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		MuleMessage muleMessage = eventContext.getMessage();
		String msgid = null;
		try {
			msgid = MsgSeqFacade.getMsgSendSeq();
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}
		// 报文处理码
		String result = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_STATE);
		// 报文处理附言
		String addword = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_ADDWORD);
		// 报文编号
		String msgno = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_TAG_KEY);
		// 报文描述
		String desc = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_DESC);
		// 报文参考号
		String msgref = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_REF);
		// 连接性测试报文
		Map xmlMap = Dto2MapFor9120.tranfor(msgid, msgref, desc, result,
				addword, msgno,"");
		// 设置消息体
		muleMessage.setProperty(MessagePropertyKeys.MSG_NO_KEY,
				MsgConstant.MSG_NO_9120);

		String _ssendno = null;
		try {
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e1) {
			logger.error("取接收或者发送流水号错误!", e1);
			throw new ITFEBizException("取接收或者发送流水号错误!", e1);
		}
//		// 根据原报文编号和原报文参考号查询原报文所对应的出票单位
//		List tvsendloglist = new ArrayList();
//		TvSendlogDto billorgsendlog = null;
//		String selectSql = "select * from TV_SENDLOG where S_OPERATIONTYPECODE=? and S_SEQ=?";
//		SQLExecutor executor;
//		String sbillorg="";
//		try {
//			executor = DatabaseFacade.getDb().getSqlExecutorFactory()
//					.getSQLExecutor();
//			executor.addParam(msgno);
//			executor.addParam(desc);
//			SQLResults res = executor.runQueryCloseCon(selectSql,
//					TvSendlogDto.class);
//			tvsendloglist.addAll(res.getDtoCollection());
//			
//			billorgsendlog = (TvSendlogDto) tvsendloglist.get(0);
//			sbillorg = billorgsendlog.getSbillorg();
//			
//		} catch (JAFDatabaseException e) {
//			logger.error("查询出票单位出现数据库异常!", e);
////			throw new ITFEBizException("查询出票单位出现数据库异常!", e);
//		}

		// 写发送日志
		muleMessage.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
				MsgLogFacade.writeSendLogWithResult(_ssendno, null,
						ITFECommonConstant.SRC_NODE,
						ITFECommonConstant.DES_NODE, TimeFacade
								.getCurrentStringTime(),
						MsgConstant.MSG_NO_9120, null, 0, null, null, null,
						null, "", null, msgid,
						DealCodeConstants.DEALCODE_ITFE_SEND, msgno, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), null, MsgConstant.ITFE_SEND_9120));
		muleMessage.setPayload(xmlMap);
	}
}

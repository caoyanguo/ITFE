package com.cfcc.itfe.msgmanager.msg;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor9121;

public class Send9121MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Send9121MsgServer.class);

	/**
	 * 报文信息处理
	 * 
	 * @param eventContext
	 * @throws ITFEBizException
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		MuleMessage muleMessage = eventContext.getMessage();
		HeadDto headdto = (HeadDto) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_HEAD_DTO);

		TvRecvlogDto recvlogdto = (TvRecvlogDto) eventContext.getMessage()
				.getProperty(MessagePropertyKeys.MSG_DTO);

		String msg = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_ORGCODE);// 处理结果说明

		// 连接性测试报文
		Map xmlMap = Dto2MapFor9121.tranfor(recvlogdto, headdto, msg);
		// 设置消息体
		muleMessage.setProperty(MessagePropertyKeys.MSG_NO_KEY,
				MsgConstant.MSG_NO_9121);

		String _ssendno = null;
		try {
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e1) {
			logger.error("取接收或者发送流水号错误!", e1);
			throw new ITFEBizException("取接收或者发送流水号错误!", e1);
		}

		// 写发送日志
		muleMessage.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
				MsgLogFacade.writeSendLogWithResult(_ssendno, null, headdto
						.get_SRC(), headdto.get_DES(), TimeFacade
						.getCurrentStringTime(), MsgConstant.MSG_NO_9121, null,
						0, null, null, null, null, recvlogdto.getSbillorg(),
						null, headdto.get_msgID(),
						DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), null, null));
		muleMessage.setPayload(xmlMap);
	}
}

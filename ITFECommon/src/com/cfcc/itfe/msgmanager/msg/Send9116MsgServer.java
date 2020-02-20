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
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor9116;

public class Send9116MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Send9116MsgServer.class);
	
	/**
	 * 9116 财政收入核对包重发请求
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		
		MuleMessage muleMessage = eventContext.getMessage();
		TvSendlogDto dto = (TvSendlogDto) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_DTO);
		HeadDto headdto = (HeadDto) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_HEAD_DTO);

		Map xmlMap = Dto2MapFor9116.tranfor(headdto,dto);
		
		muleMessage.setProperty(MessagePropertyKeys.MSG_NO_KEY,
				MsgConstant.MSG_NO_9116);
		
		
		String _ssendno = null;
		try {
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e1) {
			logger.error("取发送流水号错误!", e1);
			throw new ITFEBizException("取发送流水号错误!", e1);
		}
		
		// 写发送日志
		muleMessage.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,MsgLogFacade.writeSendLogWithResult(_ssendno, null, headdto.get_SRC(), headdto.get_DES(), TimeFacade
				.getCurrentStringTime(),headdto.get_msgNo(),
				null, 0, null, null, null, null, dto.getSbillorg(), null,
				headdto.get_msgID(),DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER),
				null,null));
		muleMessage.setPayload(xmlMap);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

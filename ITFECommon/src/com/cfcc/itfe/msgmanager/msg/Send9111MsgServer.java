package com.cfcc.itfe.msgmanager.msg;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.ContentDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor9111;

public class Send9111MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Send9111MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		MuleMessage muleMessage = eventContext.getMessage();
		// 取得报文信息直接发送报文
		ContentDto dto = (ContentDto) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_DTO);
		String orgcode = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_ORGCODE);

		Map xmlmap = Dto2MapFor9111.tranfor(dto, orgcode);
        String  msgid = (String) ((Map)(((Map)xmlmap.get("cfx")).get("HEAD"))).get("MsgID");
		// 设置消息体
		muleMessage.setProperty(MessagePropertyKeys.MSG_NO_KEY,
				MsgConstant.MSG_NO_9111);

		String _ssendno = null;
		try {
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (com.cfcc.itfe.exception.SequenceException e1) {
			logger.error("取接收或者发送流水号错误!", e1);
			throw new ITFEBizException("取接收或者发送流水号错误!", e1);
		}
        
		// 写发送日志
		muleMessage.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
				MsgLogFacade.writeSendLogWithResult(_ssendno, null,
						orgcode,
						ITFECommonConstant.DES_NODE,dto.get_OriEntrustDate(),
						MsgConstant.MSG_NO_9111, null, 0, null, null, null,
						null, dto.get_OriSendOrgCode(), null, msgid,
						DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), null, null));
		muleMessage.setPayload(xmlmap);
	}
}

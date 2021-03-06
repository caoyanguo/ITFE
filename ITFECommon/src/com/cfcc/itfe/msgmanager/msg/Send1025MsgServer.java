/**
 * 征收机关申请报表
 */
package com.cfcc.itfe.msgmanager.msg;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.util.transformer.Dto2MapFor1025;

/**
 * comment:征收机关申请报表
 * 
 * @author wangtuo
 * 
 */
public class Send1025MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Send1025MsgServer.class);

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

		String sendorgcode = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_ORGCODE);

		String rptdate = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_DATE);

		String rptrange = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_CONTENT);

		// 连接性测试报文
		Map xmlMap = Dto2MapFor1025.tranfor(headdto, sendorgcode, rptdate,
				rptrange);
		// 设置消息体
		muleMessage.setProperty(MessagePropertyKeys.MSG_NO_KEY, headdto
				.get_msgNo());

		String _ssendno = null;
		try {
			_ssendno = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e1) {
			logger.error("取接收或者发送流水号错误!", e1);
			throw new ITFEBizException("取接收或者发送流水号错误!", e1);
		}

		// 写发送日志
		muleMessage.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
				MsgLogFacade.writeSendLogWithResult(_ssendno, null,
						ITFECommonConstant.SRC_NODE,
						ITFECommonConstant.DES_NODE, rptdate, headdto
								.get_msgNo(), null, 0, null, null, null, null,
						sendorgcode, null, headdto.get_msgID(),
						DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), null, null));
		muleMessage.setPayload(xmlMap);
	}

}

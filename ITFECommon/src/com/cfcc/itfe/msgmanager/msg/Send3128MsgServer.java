package com.cfcc.itfe.msgmanager.msg;

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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.util.transformer.Dto2MapFor3128;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class Send3128MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Send3128MsgServer.class);

	@Override
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		MuleMessage muleMessage = eventContext.getMessage();
		String msgKey = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_NO_KEY);
		String sendorgcode = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_ORGCODE);
		String rtpdate = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_DATE);
		String finOrgCode = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_BILL_CODE);
		String rptDate = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DATE);
		Map<String, Object> map = Dto2MapFor3128.tranfor((List<IDto>)eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_CONTENT),finOrgCode,rptDate);
		try {
			String _ssendno = StampFacade.getStampSendSeq("FS");

			muleMessage
					.setProperty(
							MessagePropertyKeys.MSG_SEND_LOG_DTO,
							MsgLogFacade
									.writeSendLogWithResult(
											_ssendno,
											null,
											ITFECommonConstant.SRC_NODE,
											ITFECommonConstant.DES_NODE,
											rtpdate,
											MsgConstant.MSG_NO_3128,
											null,
											0,
											null,
											null,
											null,
											null,
											sendorgcode,
											null,
											(String)map.get("MsgID"),
											DealCodeConstants.DEALCODE_ITFE_SEND,
											null,
											null,
											(String) eventContext
													.getMessage()
													.getProperty(
															MessagePropertyKeys.MSG_SENDER),
											null, null));
			muleMessage.setPayload(map);
		} catch (SequenceException e1) {
			logger.error("取接收或者发送流水号错误!", e1);
			throw new ITFEBizException("取接收或者发送流水号错误!", e1);
		}
	}

}

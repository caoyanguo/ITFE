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
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.transformer.Dto2MapForTbsVoucher;

public class Reissue1000MsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(Reissue1000MsgServer.class);

	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {
			MuleMessage muleMessage = eventContext.getMessage();
			// 取得报文信息直接发送报文
			TvVoucherinfoDto tvVoucherinfoDto = null;
			String Spaybankcode = null;
			Map xmlmap = null;
			tvVoucherinfoDto = (TvVoucherinfoDto) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DTO);
			Spaybankcode = tvVoucherinfoDto.getSpaybankcode();
			xmlmap = Dto2MapForTbsVoucher.tranfor(tvVoucherinfoDto);
			String msgid = (String) ((Map) (((Map) xmlmap.get("cfx")).get("HEAD"))).get("MsgID");
			// 设置消息体
			muleMessage.setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_TBS_NO_1000 + "_VOUCHER_TBS");
			String _ssendno = StampFacade.getStampSendSeq("FS");
			// 记录资金报文日志
			Map<String, Object> headMap = (Map) (((Map) xmlmap.get("cfx")).get("HEAD"));

			// 写发送日志
			muleMessage.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO, MsgLogFacade.writeSendLogWithResult(_ssendno, null, tvVoucherinfoDto.getSorgcode(),
					(String) headMap.get("DES"), TimeFacade.getCurrentStringTime(), MsgConstant.MSG_TBS_NO_1000, (String) eventContext.getMessage()
							.getProperty("XML_MSG_FILE_PATH"), 1, tvVoucherinfoDto.getNmoney(), tvVoucherinfoDto.getSpackno(), tvVoucherinfoDto.getStrecode(),
					null, Spaybankcode, null, msgid, DealCodeConstants.DEALCODE_ITFE_SEND, null, null, (String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null, null));

			muleMessage.setPayload(xmlmap);
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		}
	}

}

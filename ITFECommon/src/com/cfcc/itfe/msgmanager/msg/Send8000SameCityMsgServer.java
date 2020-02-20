package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.voucher.service.VoucherUtil;

public class Send8000SameCityMsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Send8000SameCityMsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		
		String payoutVouType = (String) eventContext.getMessage().getProperty("payoutVouType"); // 
		String treCode = (String) eventContext.getMessage().getProperty("treCode"); // 
		String entrustDate = (String) eventContext.getMessage().getProperty("entrustDate"); // 
		String packNo = (String) eventContext.getMessage().getProperty("packNo"); // 
		String bankCode =  (String) eventContext.getMessage().getProperty("bankCode"); //
		String orgcode = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_ORGCODE); // 机构代码
		
		Map xmlMap = VoucherUtil.verificationOfAccount(payoutVouType, treCode, entrustDate, packNo);
		if(xmlMap==null){
			logger.debug("无对应的对账数据");
			return;
		}
		int num = Integer.parseInt((String)((Map)((Map)((Map)xmlMap.get("cfx")).get("MSG")).get("BatchHead8000")).get("SendPackNum"));
		BigDecimal amt =  new BigDecimal((String)((Map)((Map)((Map)xmlMap.get("cfx")).get("MSG")).get("BatchHead8000")).get("SendPackAmt"));
		String ls_MsgId  = (String)((Map)((Map)xmlMap.get("cfx")).get("HEAD")).get("MsgID");
		MuleMessage message = new DefaultMuleMessage(new HashMap());
		message.setProperty(MessagePropertyKeys.MSG_NO_KEY,bankCode+"-8000");
		
		// 写发送日志
		try {
			message.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
						MsgLogFacade.writeSendLogWithResult(StampFacade.getStampSendSeq("FS"), null,orgcode,
								ITFECommonConstant.DES_NODE, TimeFacade.getCurrentStringTime(),MsgConstant.MSG_NO_8000, 
								(String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), num, amt, "", treCode,
								null, "", null, ls_MsgId,
								DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
								StateConstant.MSG_SENDER_FLAG_0, null, MsgConstant.ITFE_AUTO_SEND+bankCode+"-8000"));
		} catch (SequenceException e) {
			logger.debug(e);
			throw new ITFEBizException("取发送流水号失败",e);
		}
		// 设置消息体
		eventContext.getMessage().setPayload(xmlMap);
	}
}

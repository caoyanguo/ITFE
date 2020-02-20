package com.cfcc.itfe.msgmanager.msg;

import java.util.Map;

import org.mule.api.MuleEventContext;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.util.transformer.Dto2MapFor5001;

public class DayReportMsgServer extends AbstractMsgManagerServer {

	// private static Log logger = LogFactory.getLog(DayReportMsgServer.class);

	/**
	 * 报文信息处理
	 * 
	 * @param eventContext
	 * @throws ITFEBizException
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		String orgcode = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_BILL_CODE);
		String srptdate = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DATE);
		String bookorgcode = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_ORGCODE);// 机构代码

		// String -> MAP
		Map xmlMap = Dto2MapFor5001.tranfor(bookorgcode+"-"+orgcode, srptdate);
		String ls_MsgId  = (String)((Map)((Map)xmlMap.get("cfx")).get("HEAD")).get("MsgID");
		
		// 写发送日志
		String ls_SendSeq = "";
		try {
			ls_SendSeq = StampFacade.getStampSendSeq("FS");
		} catch (SequenceException e) {
			throw new ITFEBizException("取发送流水号失败",e);
		}
		// 设置消息体
		eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_5001);
		eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
				MsgLogFacade.writeSendLogWithResult(ls_SendSeq, null,
						bookorgcode,
						ITFECommonConstant.DES_NODE, TimeFacade
								.getCurrentStringTime(),
						MsgConstant.MSG_NO_5001, null, 0, null, null, null,
						null, null, null, ls_MsgId,
						DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER),  MsgConstant.ITFE_SEND, MsgConstant.ITFE_REQ+MsgConstant.MSG_NO_5001));
		
		
		// 设置消息体
		eventContext.getMessage().setPayload(xmlMap);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

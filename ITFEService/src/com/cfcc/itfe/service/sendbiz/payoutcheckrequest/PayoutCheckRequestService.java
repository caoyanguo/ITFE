package com.cfcc.itfe.service.sendbiz.payoutcheckrequest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;

/**
 * @author t60
 * @time   12-02-24 10:44:42
 * codecomment: 
 */

public class PayoutCheckRequestService extends AbstractPayoutCheckRequestService {
	private static Log log = LogFactory.getLog(PayoutCheckRequestService.class);

	public void payoutCheckRequest(String sendorgcode, String entrustdate,
			String oripackmsgno, String orichkdate, String oripackno,
			String orgtype) throws ITFEBizException {
		HeadDto dto = new HeadDto();
		dto.set_msgNo(oripackmsgno);
		dto.set_workDate(orichkdate);
		dto.set_APP(oripackno);
		dto.set_VER(orgtype);
		
		HeadDto headdto = new HeadDto();
		headdto.set_VER(MsgConstant.MSG_HEAD_VER);
		headdto.set_SRC(sendorgcode);
		headdto.set_APP(MsgConstant.MSG_HEAD_APP);
		headdto.set_DES(entrustdate);
		headdto.set_msgNo(MsgConstant.MSG_NO_9117);
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headdto.set_msgID(msgid);
			headdto.set_msgRef(msgid);
		} catch (SequenceException e) {
			log.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}
		headdto.set_workDate(TimeFacade.getCurrentStringTime());

		try {
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
					MsgConstant.MSG_NO_9117 + "_OUT");
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
			message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, sendorgcode);// 发送机关代码
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, getLoginInfo().getSorgcode());// 发送机关代码
			message.setProperty(MessagePropertyKeys.MSG_DATE, entrustdate);// 委托日期
			message.setProperty(MessagePropertyKeys.MSG_DTO, dto);
			message.setPayload(map);
			if(getLoginInfo().getSorgcode()!=null&&getLoginInfo().getSorgcode().startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
		} catch (MuleException e) {
			log.error("调用后台报文处理的时候出现异常!", e);
			throw new ITFEBizException("调用后台报文处理的时候出现异常!", e);
		}
	}

}
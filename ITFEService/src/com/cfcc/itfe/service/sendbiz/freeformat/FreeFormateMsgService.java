package com.cfcc.itfe.service.sendbiz.freeformat;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
/**
 * @author db2admin
 * @time   12-03-15 14:59:16
 * codecomment: 
 */

public class FreeFormateMsgService extends AbstractFreeFormateMsgService {
	private static Log log = LogFactory.getLog(FreeFormateMsgService.class);	


	/**
	 * 发送
	 	 
	 * @generated
	 * @param sendOrgCode
	 * @param rcvOrgCode
	 * @param content
	 * @throws ITFEBizException	 
	 */
    public void sendFreeFormateMsg(String sendOrgCode, String rcvOrgCode, String content) throws ITFEBizException {
    	HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();

//		sendOrgCode = this.getLoginInfo().getSorgcode();
		HeadDto headdto = new HeadDto();
		headdto.set_VER(MsgConstant.MSG_HEAD_VER);
		headdto.set_SRC(ITFECommonConstant.SRC_NODE);
		headdto.set_APP(MsgConstant.MSG_HEAD_APP);
		headdto.set_DES(ITFECommonConstant.DES_NODE);
		headdto.set_msgNo(MsgConstant.MSG_NO_9105);
		
		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);

		// 报文头 HEAD
		headMap.put("VER", MsgConstant.MSG_HEAD_VER);
		if(getLoginInfo().getSorgcode()!=null&&getLoginInfo().getSorgcode().startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.MSG_NO_9105);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			log.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}
		HashMap<String, Object> FreeFormat9105 = new HashMap<String, Object>();
		msgMap.put("FreeFormat9105", FreeFormat9105);
		FreeFormat9105.put("SrcNodeCode", ITFECommonConstant.SRC_NODE);
		FreeFormat9105.put("DesNodeCode", ITFECommonConstant.DES_NODE);
		FreeFormat9105.put("SendOrgCode", sendOrgCode);
		FreeFormat9105.put("RcvOrgCode", rcvOrgCode);
		FreeFormat9105.put("Content", content);
		
		try {
			MuleClient client = new MuleClient();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY, MsgConstant.MSG_NO_9105+"_OUT");
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, sendOrgCode);// 发送机关代码
			message.setProperty(MessagePropertyKeys.MSG_DATE, TimeFacade.getCurrentStringTime());// 入库日期
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
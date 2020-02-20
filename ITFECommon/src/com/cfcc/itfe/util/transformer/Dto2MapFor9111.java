package com.cfcc.itfe.util.transformer;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.ContentDto;

public class Dto2MapFor9111 {
	
	private static Log logger = LogFactory.getLog(Dto2MapFor9111.class);
	
	/**
	 * 报文信息转化
	 * @param dto
	 * @param orgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(ContentDto dto,String orgcode) throws ITFEBizException{
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();
		
		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);
		
		// 报文头 HEAD
		headMap.put("VER", MsgConstant.MSG_HEAD_VER);
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo",MsgConstant.MSG_NO_9111);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}
	
		HashMap<String, Object> getMsgMap = new HashMap<String, Object>();
		// 报文体GETMSG
		getMsgMap.put("SendOrgCode", orgcode); // 发起机构代码
		getMsgMap.put("EntrustDate", dto.get_OriEntrustDate()); // 委托日期
		getMsgMap.put("OriPackMsgNo", dto.get_OriPackMsgNo()); // 原包报文编号
		getMsgMap.put("OriSendOrgCode", dto.get_OriSendOrgCode()); // 原发起机构代码
		getMsgMap.put("OriEntrustDate", dto.get_OriEntrustDate().replaceAll("-", "")); // 原委托日期
		getMsgMap.put("OriPackNo", dto.get_OriPackNo()); // 原包流水号
		getMsgMap.put("OrgType", MsgConstant.MSG_ORG_TYPE_FINA); // 机构类型[财政]
		
		msgMap.put("GetMsg9111", getMsgMap);

		return map;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

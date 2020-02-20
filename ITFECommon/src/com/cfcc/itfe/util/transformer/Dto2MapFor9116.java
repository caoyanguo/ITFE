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
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;

public class Dto2MapFor9116 {

	private static Log logger = LogFactory.getLog(Dto2MapFor9116.class);

	/**
	 * 报文信息转化
	 * 
	 * @param dto
	 * @param orgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(HeadDto headto, TvSendlogDto dto)
			throws ITFEBizException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();

		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);

		// 报文头 HEAD
		headMap.put("VER", headto.get_VER());
		headMap.put("SRC", headto.get_SRC());
		headMap.put("DES", headto.get_DES());
		headMap.put("APP", headto.get_APP());
		headMap.put("MsgNo", headto.get_msgNo());
		headMap.put("WorkDate", headto.get_workDate());
		headMap.put("MsgID", headto.get_msgID());
		headMap.put("MsgRef", headto.get_msgRef());

		HashMap<String, Object> getMsgMap = new HashMap<String, Object>();
		// 报文体GETMSG
		getMsgMap.put("SendOrgCode", dto.getSsendorgcode()); // 发起机构代码
		getMsgMap.put("EntrustDate", dto.getSdate().replaceAll("-", "")); // 委托日期
		getMsgMap.put("OriPackMsgNo", dto.getSoperationtypecode()); // 原包报文编号
		getMsgMap.put("OriChkDate", dto.getSsenddate().replaceAll("-", "")); // 原对账日期
		getMsgMap.put("OriChkAcctType", dto.getSifsend()); // 原文件对账类型
		getMsgMap.put("OriReportMonth", dto.getSdemo()); // 原报表所属年月
		getMsgMap.put("OirDocNameMonth", dto.getSretcodedesc()); // 原文件名所在年月

		msgMap.put("GetMsg9116", getMsgMap);

		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

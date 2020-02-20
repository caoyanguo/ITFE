package com.cfcc.itfe.util.transformer;

import java.util.HashMap;
import java.util.Map;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.HeadDto;

public class Dto2MapFor1024 {

	public static Map tranfor(HeadDto headdto,String orgcode) throws ITFEBizException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();

		HashMap<String, Object> msgMap = new HashMap<String, Object>();
		HashMap<String, Object> requestBill1024Map = new HashMap<String, Object>();
		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);
		msgMap.put("RequestBill1024", requestBill1024Map);

		// 报文头 HEAD
		headMap.put("VER", headdto.get_VER());
		if(orgcode!=null&&orgcode.startsWith("1702"))
		{
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
			headdto.set_SRC(ITFECommonConstant.SRCCITY_NODE);
		}
		else
		{
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
			headdto.set_SRC(ITFECommonConstant.SRC_NODE);
		}
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", headdto.get_APP());
		headMap.put("MsgNo", headdto.get_msgNo());
		headMap.put("MsgID", headdto.get_msgID());
		headMap.put("MsgRef", headdto.get_msgRef());
		headMap.put("WorkDate", headdto.get_workDate());

		// MSG->RequestBill1024
		requestBill1024Map.put("SendOrgCode", headdto.get_SRC());
		requestBill1024Map.put("InTreDate", headdto.get_DES());

		// 为了便于传参，以前的src和des分别设为 发起机构代码和入库日期，现在对其更正
		headdto.set_DES(ITFECommonConstant.DES_NODE);
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

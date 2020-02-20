package com.cfcc.itfe.util.transformer;

import java.util.HashMap;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;

public class Dto2MapFor9121 {

	public static HashMap tranfor(TvRecvlogDto recvlogdto, HeadDto headdto,
			String msg) throws ITFEBizException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap9121 = new HashMap<String, Object>();
		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);
		// 报文头 HEAD
		headMap.put("VER", headdto.get_VER());
		headMap.put("SRC", headdto.get_SRC());
		headMap.put("DES", headdto.get_DES());
		headMap.put("APP", headdto.get_APP());
		headMap.put("MsgNo", headdto.get_msgNo());
		headMap.put("MsgID", headdto.get_msgID());
		headMap.put("MsgRef", headdto.get_msgRef());
		headMap.put("WorkDate", headdto.get_workDate());
		// 报文内容
		msgMap.put("MsgReturn9121", msgMap9121);
		msgMap9121.put("OriMsgNo", recvlogdto.getSoperationtypecode());
		msgMap9121.put("OriSendOrgCode", recvlogdto.getSbillorg());
		msgMap9121.put("OriEntrustDate", recvlogdto.getSdate());
		msgMap9121.put("OriRequestNo", recvlogdto.getSpackno());
		msgMap9121.put("Result", recvlogdto.getSretcode());
		msgMap9121.put("AddWord", msg);
		return map;
	}
}

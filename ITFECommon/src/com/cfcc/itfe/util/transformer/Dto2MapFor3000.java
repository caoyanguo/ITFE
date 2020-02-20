package com.cfcc.itfe.util.transformer;

import java.util.HashMap;
import java.util.Map;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.HeadDto;

public class Dto2MapFor3000 {
	public static Map tranfor(HeadDto headdto) throws ITFEBizException{
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		
		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);

		// 报文头 HEAD
		headMap.put("VER", headdto.get_VER());
		headMap.put("SRC", headdto.get_SRC());
		headMap.put("DES", headdto.get_DES());
		headMap.put("APP", headdto.get_APP());
		headMap.put("MsgNo",headdto.get_msgNo());
		headMap.put("MsgID", headdto.get_msgID());
		headMap.put("MsgRef", headdto.get_msgRef());
		headMap.put("WorkDate", headdto.get_workDate());
		headMap.put("Reserve", headdto.get_reserve());
		
		return map;
	}
}

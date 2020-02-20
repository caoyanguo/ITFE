package com.cfcc.itfe.util.transformer;

import java.util.HashMap;
import java.util.Map;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.ContentDto;
import com.cfcc.itfe.persistence.dto.HeadDto;

public class Dto2MapFor9006 {
	
	public static Map tranfor(HeadDto headdto,ContentDto contentdto) throws ITFEBizException{
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap9006 = new HashMap<String, Object>();
		
		
		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);
		
		// 报文头 HEAD
		headMap.put("VER", headdto.get_VER());
		headMap.put("SRC", headdto.get_SRC());
		headMap.put("DES", headdto.get_DES());
		headMap.put("APP", headdto.get_APP());
		headMap.put("MsgNo",headdto.get_msgNo());
		headMap.put("MsgID", headdto.get_msgID());
		headMap.put("MsgRef", headdto.get_msgRef());
		headMap.put("WorkDate", headdto.get_workDate());
		
		//报文内容
		msgMap.put("LoginInfo9006",msgMap9006 );
		msgMap9006.put("Password",contentdto.get_logPass() );
		msgMap9006.put("NewPassword",contentdto.get_logNewPass());
		
		return map;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

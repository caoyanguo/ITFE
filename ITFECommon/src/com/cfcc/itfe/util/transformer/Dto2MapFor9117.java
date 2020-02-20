/**
 * 
 */
package com.cfcc.itfe.util.transformer;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;

/**
 * @author wangtuo
 * 
 */
public class Dto2MapFor9117 {
	private static Log logger = LogFactory.getLog(Dto2MapFor9117.class);

	/**
	 * DTO转化XML报文(支出核对包重发请求)
	 * 
	 * @param String
	 *            sendorgcode 发送机关代码
	 * @param String
	 *            entrustdate 委托日期
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(HeadDto headdto, HeadDto dto,String orgcode)
			throws ITFEBizException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();

		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);

		headMap.put("VER", MsgConstant.MSG_HEAD_VER);
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.MSG_NO_9117);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		headMap.put("MsgID", headdto.get_msgID());
		headMap.put("MsgRef", headdto.get_msgID());
		
		HashMap<String, Object> GetMsg9117 = new HashMap<String, Object>();
		GetMsg9117.put("SendOrgCode", headdto.get_SRC());
		GetMsg9117.put("EntrustDate", headdto.get_DES());
		GetMsg9117.put("OriPackMsgNo", dto.get_msgNo());
		GetMsg9117.put("OriChkDate", dto.get_workDate().replaceAll("-", ""));
		GetMsg9117.put("OriPackNo", dto.get_APP());
		GetMsg9117.put("OrgType", dto.get_VER());

		msgMap.put("GetMsg9117", GetMsg9117);

		return map;
	}
}

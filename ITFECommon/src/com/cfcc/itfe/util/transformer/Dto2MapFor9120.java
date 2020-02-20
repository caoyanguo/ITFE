/**
 * 
 */
package com.cfcc.itfe.util.transformer;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;

/**
 * @author VAIO
 * 
 */
public class Dto2MapFor9120 {
	private static Log logger = LogFactory.getLog(Dto2MapFor9120.class);

	/**
	 * DTO转化XML报文
	 * 
	 * @param String
	 *            sendorgcode 发送机关代码
	 * @param String
	 *            entrustdate 委托日期
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(String msgid,String msgref,String desc,String result,String addword,String msgno,String orgcode) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_9120);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		headMap.put("MsgID", msgid);
		if (null ==msgref||"".equals(msgref)) {
			msgref = msgid;
		}
		headMap.put("MsgRef", msgref);
		
		HashMap<String, Object> MsgReturn9120 = new HashMap<String, Object>();
		MsgReturn9120.put("OriMsgNo", msgno);
		MsgReturn9120.put("Result", result);
		MsgReturn9120.put("AddWord", addword);
		MsgReturn9120.put("Information",desc);

		msgMap.put("MsgReturn9120", MsgReturn9120);

		return map;
	}
}

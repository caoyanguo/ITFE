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
public class Dto2MapFor3001Tbs {
	private static Log logger = LogFactory.getLog(Dto2MapFor3001Tbs.class);

	/**
	 * DTOת��XML����
	 * 
	 * @param String
	 *            sendorgcode ���ͻ��ش���
	 * @param String
	 *            entrustdate ί������
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(String msgid,String msgref,String desc,String result,String addword,String msgno) throws ITFEBizException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();

		// ���ñ��Ľڵ� CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);

		headMap.put("VER", MsgConstant.MSG_HEAD_VER);
		headMap.put("SRC", "111111111111");
		headMap.put("DES", desc);
		headMap.put("APP", "TCQS");
		headMap.put("MsgNo", MsgConstant.MSG_TBS_NO_3001);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		headMap.put("MsgID", msgid);
		if (null ==msgref||"".equals(msgref)) {
			msgref = msgid;
		}
		headMap.put("MsgRef", msgref);
		
		HashMap<String, Object> MsgReturn3001 = new HashMap<String, Object>();
		MsgReturn3001.put("OriMsgNo", msgno);
		MsgReturn3001.put("Result", result);
		MsgReturn3001.put("AddWord", addword);
		MsgReturn3001.put("Information","");

		msgMap.put("MsgReturn3001", MsgReturn3001);

		return map;
	}
}

/**
 * 财政电子税票申请
 */
package com.cfcc.itfe.util.transformer;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * @author wangtuo
 * 
 */
public class Dto2MapFor5003 {
	private static Log logger = LogFactory.getLog(Dto2MapFor5003.class);

	/**
	 * DTO转化XML报文(财政电子税票申请)
	 * 
	 * @param String
	 *            sendorgcode 发送机构代码 - 财政代码
	 * @param String
	 *            applydate 报表日期
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(HeadDto headdto, String sendorgcode,
			String applydate,String orgcode) throws ITFEBizException {
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
			headMap.put("MsgNo", MsgConstant.MSG_NO_5003);
			headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
			headMap.put("MsgID", headdto.get_msgID());
			headMap.put("MsgRef", headdto.get_msgID());

			HashMap<String, Object> GetMsg5003 = new HashMap<String, Object>();
			GetMsg5003.put("SendOrgCode", sendorgcode);
			GetMsg5003.put("ApplyDate", applydate);
			msgMap.put("GetMsg5003", GetMsg5003);

	
		return map;
	}
}
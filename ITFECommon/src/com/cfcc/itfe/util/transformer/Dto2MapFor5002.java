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

public class Dto2MapFor5002 {
	private static Log logger = LogFactory.getLog(Dto2MapFor5002.class);

	public static Map tranfor(HeadDto headdto,String orgcode) throws ITFEBizException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		
		HashMap<String, Object> msgMap = new HashMap<String, Object>();
		HashMap<String, Object> requestBill5002Map = new HashMap<String, Object>();
		try {
			// 设置报文节点 CFX
			map.put("cfx", cfxMap);
			cfxMap.put("HEAD", headMap);
			cfxMap.put("MSG", msgMap);
			msgMap.put("RequestBill5002", requestBill5002Map);

			// 报文头 HEAD
			headMap.put("VER", headdto.get_VER());
			if(orgcode!=null&&orgcode.startsWith("1702"))
				headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
			else
				headMap.put("SRC", ITFECommonConstant.SRC_NODE);
			headMap.put("DES", ITFECommonConstant.DES_NODE);
			headMap.put("APP", headdto.get_APP());
			headMap.put("MsgNo", headdto.get_msgNo());
			headMap.put("MsgID", headdto.get_msgID());
			headMap.put("MsgRef", headdto.get_msgRef());
			headMap.put("WorkDate", headdto.get_workDate());

			// MSG->RequestBill5002
			requestBill5002Map.put("SendOrgCode", headdto.get_SRC());
			requestBill5002Map.put("InTreDate", headdto.get_DES());

			// 为了便于传参，以前的src和des分别设为 发起机构代码和入库日期，现在对其更正
			if(orgcode!=null&&orgcode.startsWith("1702"))
				headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
			else
				headMap.put("SRC", ITFECommonConstant.SRC_NODE);
			headdto.set_DES(ITFECommonConstant.DES_NODE);
		} catch (Exception e) {
			logger.error("保存发送日志出现异常！", e);
			throw new ITFEBizException("保存发送日志的时候出现异常！", e);
		}

		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

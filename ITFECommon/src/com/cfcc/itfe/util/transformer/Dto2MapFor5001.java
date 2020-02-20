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

public class Dto2MapFor5001 {
	
	private static Log logger = LogFactory.getLog(Dto2MapFor5001.class);

	/**
	 * DTO转化XML报文(财政申请收入日报表)
	 * 
	 * @param String
	 *            orgcode 机构代码 - 财政代码
	 * @param String
	 *            srptdate 报表日期
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(String orgcode, String srptdate) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_5001);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}
		
		HashMap<String, Object> requestHeadMap = new HashMap<String, Object>();
		requestHeadMap.put("SendOrgCode", orgcode.indexOf("-")>0?orgcode.split("-")[1]:orgcode);
		requestHeadMap.put("ReportDate", srptdate.replaceAll("-", ""));
		
		HashMap<String, Object> requestRptMap = new HashMap<String, Object>();
		requestRptMap.put("NrBudget", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("NrDrawBack", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("NrRemove", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("Amount", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("NrShare", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("TrBudget", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("TrDrawBack", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("TrRemove", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("TrShare", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("Stock", MsgConstant.REPORT_DAY_DOWN_YES);
		
		msgMap.put("RequestHead5001", requestHeadMap);
		msgMap.put("RequestReport5001", requestRptMap);
				
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// TODO Auto-generated method stub

	}

}

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

public class Dto2MapFor1025 {
	private static Log logger = LogFactory.getLog(Dto2MapFor1025.class);

	/**
	 * DTOת��XML����(���ջ������������ձ���)
	 * 
	 * @param String
	 *            orgcode �������� - ���ջ��ش���
	 * @param String
	 *            srptdate ��������
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(HeadDto headdto, String orgcode, String srptdate,
			String rptrange) throws ITFEBizException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();

		// ���ñ��Ľڵ� CFX
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_1025);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		headMap.put("MsgID", headdto.get_msgID());
		headMap.put("MsgRef", headdto.get_msgID());

		HashMap<String, Object> requestHeadMap = new HashMap<String, Object>();
		requestHeadMap.put("SendOrgCode", orgcode);
		requestHeadMap.put("ReportDate", srptdate);
		requestHeadMap.put("ReportArea", rptrange);// ����Χ 0:������1:ȫϽ

		HashMap<String, Object> requestRptMap = new HashMap<String, Object>();
		requestRptMap.put("NrBudget", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("NrDrawBack", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("NrRemove", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("Amount", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("NrShare", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("NrPay", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("Debt", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("TrBudget", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("TrDrawBack", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("TrRemove", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("TrShare", MsgConstant.REPORT_DAY_DOWN_YES);
		requestRptMap.put("Stock", MsgConstant.REPORT_DAY_DOWN_YES);

		msgMap.put("RequestHead1025", requestHeadMap);
		msgMap.put("RequestReport1025", requestRptMap);

		return map;
	}
}

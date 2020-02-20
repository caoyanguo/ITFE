package com.cfcc.itfe.util.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class Dto2MapFor3128 {

	private static Log logger = LogFactory.getLog(Dto2MapFor3128.class);

	public static Map tranfor(List<IDto> rtpList,String finOrgCode,String rptDate) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> cfxMap = new HashMap<String, Object>();
			HashMap<String, Object> headMap = new HashMap<String, Object>();
			HashMap<String, Object> msgMap = new HashMap<String, Object>();
			HashMap<String, Object> billHead3128Map = new HashMap<String, Object>();
			headMap.put("VER", MsgConstant.MSG_HEAD_VER);
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
			headMap.put("DES", ITFECommonConstant.DES_NODE);
			headMap.put("APP", MsgConstant.MSG_HEAD_APP);
			headMap.put("MsgNo", MsgConstant.MSG_NO_3128);
			headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
			headMap.put("Reserve", MsgConstant.CFCCHL);

			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
			map.put("MsgID", msgid);
			
			
			map.put("cfx", cfxMap);
			cfxMap.put("HEAD", headMap);
			cfxMap.put("MSG", msgMap);
			msgMap.put("BillHead3128", billHead3128Map);
			billHead3128Map.put("FinOrgCode", finOrgCode);
			billHead3128Map.put("ReportDate", rptDate);
			HashMap<String, Object> nrBudget3128 = new HashMap<String, Object>();
			HashMap<String, Object> stock3128 = new HashMap<String, Object>();
			List<Object> stockBillList = new ArrayList<Object>();
			List<Object> nrBudgetBill3128 = new ArrayList<Object>();
			for (IDto idto : rtpList) {
				if(idto instanceof TrStockdayrptDto){
					stockBillList.add(getStockBillMap((TrStockdayrptDto)idto));
				}else if(idto instanceof TrIncomedayrptDto){
					nrBudgetBill3128.add(getInComeRptMap((TrIncomedayrptDto)idto));
				}
			}
			if(null != stockBillList && stockBillList.size() > 0){
				msgMap.put("Stock3128", stock3128);
				stock3128.put("StockBill3128", stockBillList);
			}
			if(null != nrBudgetBill3128 && nrBudgetBill3128.size() > 0){
				msgMap.put("NrBudget3128", nrBudget3128);
				nrBudget3128.put("NrBudgetBill3128", nrBudgetBill3128);
			}

			return map;
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}

	}
	/**
	 * 库存
	 * @param trStockdayrptDto
	 * @return
	 */
	
	private static Map<String, Object> getStockBillMap(TrStockdayrptDto trStockdayrptDto){
		HashMap<String, Object> stockBill3128Map = new HashMap<String, Object>();
		stockBill3128Map.put("TreCode", trStockdayrptDto.getStrecode());
		stockBill3128Map.put("AcctCode", trStockdayrptDto.getSaccno());
		stockBill3128Map.put("AcctName", trStockdayrptDto.getSaccname());
		stockBill3128Map.put("AcctDate", trStockdayrptDto.getSaccdate());
		stockBill3128Map.put("YesterdayBalance", MtoCodeTrans.transformString(trStockdayrptDto.getNmoneyyesterday()));
		stockBill3128Map.put("TodayReceipt", MtoCodeTrans.transformString(trStockdayrptDto.getNmoneyin()));
		stockBill3128Map.put("TodayPay", MtoCodeTrans.transformString(trStockdayrptDto.getNmoneyout()));
		stockBill3128Map.put("TodayBalance", MtoCodeTrans.transformString(trStockdayrptDto.getNmoneytoday()));
		return stockBill3128Map;
		
	}
	/**
	 * 收入日报
	 * @param trIncomedayrptDto
	 * @return
	 */
	private static Map<String, Object> getInComeRptMap(TrIncomedayrptDto trIncomedayrptDto){
		HashMap<String, Object> nrBudgetBill3128Map = new HashMap<String, Object>();
		nrBudgetBill3128Map.put("TaxOrgCode", trIncomedayrptDto.getStaxorgcode());
		nrBudgetBill3128Map.put("TreCode", trIncomedayrptDto.getStrecode());
		nrBudgetBill3128Map.put("BudgetType", trIncomedayrptDto.getSbudgettype());
		nrBudgetBill3128Map.put("BudgetLevelCode", trIncomedayrptDto.getSbudgetlevelcode());
		nrBudgetBill3128Map.put("BudgetSubjectCode", trIncomedayrptDto.getSbudgetsubcode());
		nrBudgetBill3128Map.put("BudgetSubjectName", trIncomedayrptDto.getSbudgetsubname());
		nrBudgetBill3128Map.put("DayAmt", MtoCodeTrans.transformString(trIncomedayrptDto.getNmoneyday()));
		nrBudgetBill3128Map.put("TenDayAmt", MtoCodeTrans.transformString(trIncomedayrptDto.getNmoneytenday()));
		nrBudgetBill3128Map.put("MonthAmt", MtoCodeTrans.transformString(trIncomedayrptDto.getNmoneymonth()));
		nrBudgetBill3128Map.put("QuarterAmt", MtoCodeTrans.transformString(trIncomedayrptDto.getNmoneyquarter()));
		nrBudgetBill3128Map.put("YearAmt", MtoCodeTrans.transformString(trIncomedayrptDto.getNmoneyyear()));
		return nrBudgetBill3128Map;
	}
}

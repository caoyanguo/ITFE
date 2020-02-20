package com.cfcc.itfe.component;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.ExportBusinessDataCsv;

/**
 * 
 * 广西需求：导出实拨资金、集中支付额度、集中支付划款定时任务
 * 导出全部数据一次，导出tcbs回执数据一次
 * 
 */
public class TimerBussDataToDirComponent implements Callable {
	//80000-成功,80001-失败,	80002-处理中,80008-未发送,80009-待处理,80013-销号失败
	private static Log logger = LogFactory.getLog(TimerBussDataToDirComponent.class);
	public Object onCall(MuleEventContext eventContext) throws Exception {
		String datadate = TimeFacade.getCurrentStringTime();
		logger.info("实拨资金、集中支付额度、集中支付划款导出文件start=================================="+datadate);
		TvPayoutmsgmainDto payout = new TvPayoutmsgmainDto();//实拨资金
		TvDirectpaymsgmainDto directpay = new TvDirectpaymsgmainDto();//直接支付额度
		TvGrantpaymsgmainDto grantpay = new TvGrantpaymsgmainDto();//授权支付额度
		TvPayreckBankDto payreckbank = new TvPayreckBankDto();//商行划款
		TvPayreckBankBackDto payreckbankbank = new TvPayreckBankBackDto();//商行退款
		payout.setScommitdate(datadate);		
		directpay.setScommitdate(datadate);		
		grantpay.setScommitdate(datadate);		
		payreckbank.setDentrustdate(CommonUtil.strToDate(datadate));		
		payreckbankbank.setDentrustdate(CommonUtil.strToDate(datadate));
		ExportBusinessDataCsv exportDataUtil = new ExportBusinessDataCsv();
		exportDataUtil.exportPayoutData(payout, "",null);//导出实拨资金当天全部状态数据
		exportDataUtil.exportPayoutBack(payout,null);//导出实拨资金退回当天全部状态数据
		exportDataUtil.exportDirectPayData(directpay, "",null);//导出直接支付额度当天全部状态数据
		exportDataUtil.exportGrantPayData(grantpay, "",null);//导出授权支付额度当天全部状态数据
		exportDataUtil.exportCommApplyPayData(payreckbank, "",null);//导出商行划款当天全部状态数据
		exportDataUtil.exportCommApplyPayBackData(payreckbankbank, "",null);//导出商行退款当天全部状态数据
		exportDataUtil.exportPayoutData(payout, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//导出实拨资金当天TCBS回执的数据
		exportDataUtil.exportDirectPayData(directpay, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//导出直接支付额度当天TCBS回执的数据
		exportDataUtil.exportGrantPayData(grantpay, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//导出授权支付额度当天TCBS回执的数据
		exportDataUtil.exportCommApplyPayData(payreckbank, " AND (S_RESULT='80000' or S_RESULT='80001')",null);//导出商行划款当天TCBS回执的数据
		exportDataUtil.exportCommApplyPayBackData(payreckbankbank, " AND (S_STATUS='80000' or S_STATUS='80001')",null);//导出商行退款当天TCBS回执的数据
		exportDataUtil.recvLogCopyFile(datadate,"3143");//拷贝接收日志表3143文件到ftp目录
		exportDataUtil.recvLogCopyFile(datadate,"3144");//拷贝接收日志表3144文件到ftp目录
		logger.info("实拨资金、集中支付额度、集中支付划款导出文件end==================================");
		return eventContext;
	}
}

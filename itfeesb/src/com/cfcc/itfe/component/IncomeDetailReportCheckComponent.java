package com.cfcc.itfe.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.time.service.IncomeDetailReportCheck;

/**
 * 功能：定时核对入库流水与报表金额
 * @author 何健荣
 *@time 2014-02-25
 */
public class IncomeDetailReportCheckComponent implements Callable {
	private static Log logger = LogFactory.getLog(IncomeDetailReportCheckComponent.class);
	
	public Object onCall(MuleEventContext eventContext) throws Exception {
		logger.debug("======================== 定时核对入库流水与报表金额是否相等任务开启 ========================");
		new IncomeDetailReportCheck().incomeDetailReportCheck();
		logger.debug("======================== 定时核对入库流水与报表金额是否相等任务关闭 ========================");
		return eventContext.getMessage();
	}
}

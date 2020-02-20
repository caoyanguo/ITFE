package com.cfcc.itfe.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.time.service.IncomeDetailReportCheck;

/**
 * ���ܣ���ʱ�˶������ˮ�뱨����
 * @author �ν���
 *@time 2014-02-25
 */
public class IncomeDetailReportCheckComponent implements Callable {
	private static Log logger = LogFactory.getLog(IncomeDetailReportCheckComponent.class);
	
	public Object onCall(MuleEventContext eventContext) throws Exception {
		logger.debug("======================== ��ʱ�˶������ˮ�뱨�����Ƿ���������� ========================");
		new IncomeDetailReportCheck().incomeDetailReportCheck();
		logger.debug("======================== ��ʱ�˶������ˮ�뱨�����Ƿ��������ر� ========================");
		return eventContext.getMessage();
	}
}

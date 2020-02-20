package com.cfcc.itfe.component;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.voucher.service.VoucherUtil;


/**
 * 
 * 报文发送接收
 * 
 */
public class InterestDetailComponent implements Callable {

	private static Log logger = LogFactory.getLog(InterestDetailComponent.class);

	public Object onCall(MuleEventContext eventContext) throws Exception {
		logger.debug("定时将计息的凭证 转移到计息明细表中");
		VoucherUtil.putVoucherDataToInteresDetail("");
		return null;
	}
}

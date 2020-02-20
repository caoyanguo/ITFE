package com.cfcc.itfe.component;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.voucher.service.VoucherUtil;


/**
 * 
 * ���ķ��ͽ���
 * 
 */
public class InterestDetailComponent implements Callable {

	private static Log logger = LogFactory.getLog(InterestDetailComponent.class);

	public Object onCall(MuleEventContext eventContext) throws Exception {
		logger.debug("��ʱ����Ϣ��ƾ֤ ת�Ƶ���Ϣ��ϸ����");
		VoucherUtil.putVoucherDataToInteresDetail("");
		return null;
	}
}

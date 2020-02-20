package com.cfcc.itfe.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;

public class VoucherTimerSendFailes implements Callable {

	private static Log logger = LogFactory.getLog(VoucherTimerSendFailes.class);

	public Object onCall(MuleEventContext arg0) throws Exception {
		logger
				.debug("======================== ƾ֤�ⶨʱ18:00���͵���ƾ֤������ ========================");
		Voucher voucher = (Voucher) ContextFactory.getApplicationContext()
				.getBean(MsgConstant.VOUCHER);
		try {
			//��ȡ�Զ��ص���������
			Map<String, List> map = SrvCacheFacade
					.cacheVoucherAuto(new TsVouchercommitautoDto()
							.columnSreturbacknauto());
			if (map != null && map.size() > 0) {
				List lists = new ArrayList();
				//��ѯTC����ʧ�ܵ�ƾ֤
				List tmpList = new VoucherTimerSendTips().findVoucherDto(map
						.get("VOUCHER"), DealCodeConstants.VOUCHER_FAIL_TCBS,
						false);
				if (null != tmpList && tmpList.size() > 0)
					lists.add(tmpList);
				//��ѯTIPS����ʧ�ܵ�ƾ֤
				tmpList = new VoucherTimerSendTips().findVoucherDto(map
						.get("VOUCHER"), DealCodeConstants.VOUCHER_FAIL_TIPS,
						false);
				if (null != tmpList && tmpList.size() > 0)
					lists.add(tmpList);
				
				
				if (lists != null && lists.size() > 0) {
					for (List list : (List<List>) lists) {
						List<List> newLists = (List<List>) list.get(0);
//						newLists.add(list.size() >= 100 ? list.subList(0, 100)
//								: list);
						voucher.voucherReturnBack(newLists.size() >= 100 ? newLists.subList(0, 100): newLists);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			VoucherException.saveErrInfo(null, e);
		}
		logger
				.debug("======================== ƾ֤�ⶨʱ18:00���͵���ƾ֤����ر� ========================");
		return null;
	}

}

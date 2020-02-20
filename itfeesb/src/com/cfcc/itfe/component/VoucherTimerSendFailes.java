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
				.debug("======================== 凭证库定时18:00发送电子凭证任务开启 ========================");
		Voucher voucher = (Voucher) ContextFactory.getApplicationContext()
				.getBean(MsgConstant.VOUCHER);
		try {
			//获取自动回单参数配置
			Map<String, List> map = SrvCacheFacade
					.cacheVoucherAuto(new TsVouchercommitautoDto()
							.columnSreturbacknauto());
			if (map != null && map.size() > 0) {
				List lists = new ArrayList();
				//查询TC处理失败的凭证
				List tmpList = new VoucherTimerSendTips().findVoucherDto(map
						.get("VOUCHER"), DealCodeConstants.VOUCHER_FAIL_TCBS,
						false);
				if (null != tmpList && tmpList.size() > 0)
					lists.add(tmpList);
				//查询TIPS处理失败的凭证
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
				.debug("======================== 凭证库定时18:00发送电子凭证任务关闭 ========================");
		return null;
	}

}

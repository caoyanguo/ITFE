package com.cfcc.itfe.component;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;


@SuppressWarnings({ "static-access", "unchecked" })
public class VoucherTimerReadService implements Callable{
	private static Log logger = LogFactory.getLog(VoucherTimerReadService.class);
	
	// 定时读取凭证
	
	
	public Object onCall(MuleEventContext eventContext)  {		
		logger.debug("======================== 凭证库定时读取凭证任务开启 ========================");		
		try {
			Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
					MsgConstant.VOUCHER);
			Map<String, List> map=SrvCacheFacade.cacheVoucherAuto(new TsVouchercommitautoDto().columnSreadauto());
			if(map!=null&&map.size()>0){
				voucher.voucherRead(map.get("VOUCHER"));
			}
			if(ITFECommonConstant.PUBLICPARAM.contains(",timer=all"))
			{
				VoucherTimerSendTips sendtips = new VoucherTimerSendTips();
				sendtips.onCall(eventContext);//发送tips
				VoucherTimerStamp stamp = new VoucherTimerStamp();
				stamp.onCall(eventContext);//签章
				VoucherTimerReturnSuccess returnsuccess = new VoucherTimerReturnSuccess();
				returnsuccess.onCall(eventContext);//发送回单
			}
			if(ITFECommonConstant.PUBLICPARAM.contains(",timer=allth,"))
			{
				VoucherTimerSendFailes sendfailes = new VoucherTimerSendFailes();
				sendfailes.onCall(eventContext);//退回
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			VoucherException.saveErrInfo(null, e);
		} catch (ITFEBizException e) {
			logger.error(e);
			VoucherException.saveErrInfo(null, e);
		}catch(Exception e){
			logger.error(e);
			VoucherException.saveErrInfo(null, e);
		}
		logger.debug("======================== 凭证库定时读取凭证任务关闭 ========================");
		return null;
		
		
	}
}

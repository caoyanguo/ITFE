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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class VoucherTimerReturnSuccess implements Callable{
	private static Log logger = LogFactory.getLog(VoucherTimerReturnSuccess.class);

	public Object onCall(MuleEventContext eventContext)  {
		logger.debug("======================== 凭证库定时发送电子凭证任务开启 ========================");
		Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
				MsgConstant.VOUCHER);		
		try {			
			Map<String, List> map = SrvCacheFacade.cacheVoucherAuto(new TsVouchercommitautoDto().columnSreturnvoucherauto());
			if(map!=null&&map.size()>0){
				List lists=new VoucherTimerSendTips().findVoucherDto(map.get("VOUCHER"),DealCodeConstants.VOUCHER_STAMP,false);
				if(lists!=null&&lists.size()>0){
					for(List list:(List<List>)lists){					
						List<List> newLists=new ArrayList<List>();
						newLists.add(list.size()>=100?list.subList(0, 100):list);
						try{
						voucher.voucherSendReturnSuccess(newLists);	
						}catch(Exception e)
						{
							logger.error(e);
							continue;
						}
					}				
				}
			}					
		}catch(Exception e){
			logger.error(e);
			VoucherException.saveErrInfo(null, e);
		}		
		logger.debug("======================== 凭证库定时发送电子凭证任务关闭 ========================");
		return null;	
	}


}

package com.cfcc.itfe.component;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.jaf.core.loader.ContextFactory;


/**
 * ���ܣ���ʱ����2301��2302���ģ�
 * @author Administrator
 */
public class TimerGen2301And2302 implements Callable {
	private static Log logger = LogFactory.getLog(TimerGen2301And2302.class);

	public Object onCall(MuleEventContext eventContext) throws Exception {
		logger.debug("========================ǰ�ö�ʱ��ʱ����2301��2302��������ʼ========================");
		Map<String, TsVouchercommitautoDto> treMap=SrvCacheFacade.cacheVoucherAutoCommit();
		if(treMap!=null&&treMap.size()>0)
		{
			List<TvVoucherinfoDto> voucherList = new ArrayList<TvVoucherinfoDto>();
			TvVoucherinfoDto vDto = null;
			for(String mapkey:treMap.keySet())
			{
				vDto = new TvVoucherinfoDto();
				vDto.setStrecode(mapkey);
				vDto.setShold1("1");
				vDto.setScheckdate(TimeFacade.getCurrentStringTime());
				vDto.setScreatdate(TimeFacade.getCurrentStringTime());
				vDto.setSvtcode(MsgConstant.VOUCHER_NO_2301);
				vDto.setSorgcode(SrvCacheFacade.cacheTreasuryInfo("").get(mapkey)==null?"":SrvCacheFacade.cacheTreasuryInfo("").get(mapkey).getSorgcode());
				voucherList.add(vDto);
				vDto = new TvVoucherinfoDto();
				vDto.setStrecode(mapkey);
				vDto.setSorgcode(SrvCacheFacade.cacheTreasuryInfo("").get(mapkey)==null?"":SrvCacheFacade.cacheTreasuryInfo("").get(mapkey).getSorgcode());
				vDto.setShold1("4");
				vDto.setScheckdate(TimeFacade.getCurrentStringTime());
				vDto.setScreatdate(TimeFacade.getCurrentStringTime());
				vDto.setSvtcode(MsgConstant.VOUCHER_NO_2301);
				voucherList.add(vDto);
				vDto = new TvVoucherinfoDto();
				vDto.setStrecode(mapkey);
				vDto.setSorgcode(SrvCacheFacade.cacheTreasuryInfo("").get(mapkey)==null?"":SrvCacheFacade.cacheTreasuryInfo("").get(mapkey).getSorgcode());
				vDto.setScheckdate(TimeFacade.getCurrentStringTime());
				vDto.setScreatdate(TimeFacade.getCurrentStringTime());
				vDto.setSvtcode(MsgConstant.VOUCHER_NO_2302);
				voucherList.add(vDto);
			}
			if(voucherList!=null&&voucherList.size()>0)
			{
				Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER);
				voucher.voucherGenerate(voucherList);
			}
		}
		logger.debug("========================ǰ�ö�ʱ��ʱ����2301��2302�����������========================");
		return eventContext;
	}
}

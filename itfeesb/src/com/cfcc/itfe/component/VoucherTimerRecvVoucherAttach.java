package com.cfcc.itfe.component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
/**
 * 功能：无纸化凭证自动接收凭证附件任务
 * @author 何健荣
 * @time  2014-07-21
 */


public class VoucherTimerRecvVoucherAttach implements Callable{
	private static Log logger = LogFactory.getLog(VoucherTimerRecvVoucherAttach.class);
		
	public Object onCall(MuleEventContext eventContext)  {		
		logger.debug("======================== 凭证库定时接收凭证附件任务开启 ========================");		
		try {
			Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
					MsgConstant.VOUCHER);
			Map<String, List> map=SrvCacheFacade.cacheVoucherAuto(new TsVouchercommitautoDto().columnSreadauto());
			if(map!=null&&map.size()>0){
				recvVoucherAttach(findStrecode(map.get("VOUCHER")));
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
		logger.debug("======================== 凭证库定时接收凭证附件任务关闭 ========================");
		return null;				
	}
			
	private void recvVoucherAttach(HashSet set) {
		for(Iterator it=set.iterator();it.hasNext();){
			try{
				TsConvertfinorgDto dto=(TsConvertfinorgDto) it.next();
				TvVoucherinfoDto voucherDto=new TvVoucherinfoDto();
				voucherDto.setSorgcode(dto.getSorgcode());
				voucherDto.setStrecode(dto.getStrecode());
				voucherDto.setSadmdivcode(dto.getSadmdivcode());
				voucherDto.setScreatdate(TimeFacade.getCurrentStringTime());
				voucherDto.setSstyear(TimeFacade.getCurrentStringTime().substring(0,4));
				VoucherUtil.recvVoucherAttach(voucherDto);			
			} catch(ITFEBizException e){
				logger.error(e);
				VoucherException.saveErrInfo(null, e);			
			}
		}
	}
	
	private HashSet findStrecode(List list) throws JAFDatabaseException, ITFEBizException{
		HashSet set=new HashSet();
		HashMap<String, TsConvertfinorgDto> bankmap = SrvCacheFacade.cacheFincInfoByFinc(null);
		for(List voucherList:(List<List>)list){
			TsConvertfinorgDto dto=bankmap.get((String) voucherList.get(1));
			if(dto!=null)
				set.add(dto);
		}
		return set;
	}
	
}

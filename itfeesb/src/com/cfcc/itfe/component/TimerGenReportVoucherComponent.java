package com.cfcc.itfe.component;


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 
 * 定时生成报表对账凭证
 * 
 */
@SuppressWarnings("unchecked")
public class TimerGenReportVoucherComponent implements Callable {
	private static Log logger = LogFactory.getLog(TimerGenReportVoucherComponent.class);
	public Object onCall(MuleEventContext eventContext) throws Exception {
		logger.info("定时生成报表对账凭证任务start=================================="+TimeFacade.getCurrentStringTime());
		String dateparam = ITFECommonConstant.PUBLICPARAM;
		String startDate = TimeFacade.getCurrentStringTime();
		String endDate = TimeFacade.getCurrentStringTime();
		if(dateparam!=null&&dateparam.indexOf(",genreport=")>=0&&dateparam.indexOf("-")>=0)//,genreport=01-10,
		{
			dateparam = StringUtils.substring(dateparam,dateparam.indexOf(",genreport="));
			dateparam = StringUtils.replace(dateparam, ",genreport=", "");
			if(dateparam.indexOf(",")>0)
				dateparam = dateparam.substring(0,dateparam.indexOf(","));
			String startDay = (StringUtils.split(dateparam, "-")[0]);
			String endDay = (StringUtils.split(dateparam, "-")[1]);
			startDate = StringUtils.substring(startDate, 0,6)+startDay;
			endDate = StringUtils.substring(endDate, 0,6)+endDay;
			if(startDate.equals(endDate))
			{
				startDate = TimeFacade.getCurrentStringTime();
				endDate = TimeFacade.getCurrentStringTime();
			}else
			{
				if(Integer.parseInt(endDate)>=Integer.parseInt(TimeFacade.getCurrentStringTime()))
				{
					return eventContext;
				}
			}
			List<TvVoucherinfoDto> list = new ArrayList<TvVoucherinfoDto>();
//			List<TsTreasuryDto> tList = DatabaseFacade.getDb().find(TsTreasuryDto.class);
			// 修改为取缓存里面的国库代码 --20150316 hua
			List<TsTreasuryDto> tList = (List<TsTreasuryDto>) SrvCacheFacade.cacheTreasuryInfo(null).values();
			if(tList!=null&&tList.size()>0)
			{
				TvVoucherinfoDto voudto = null;
				SQLExecutor execDetail = null;
				try{
						execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
						List isexislist = null;
						String sql = "select * from TV_VOUCHERINFO where S_ORGCODE=? and S_TRECODE=? and S_HOLD3=? and S_HOLD4=? and S_VTCODE=?";
						for(TsTreasuryDto tempdto:tList)
						{
							voudto = new TvVoucherinfoDto();
							voudto.setSorgcode(tempdto.getSorgcode());
							voudto.setStrecode(tempdto.getStrecode());
							voudto.setShold3(startDate);
							voudto.setShold4(endDate);
							voudto.setSvtcode(MsgConstant.VOUCHER_NO_3511);
							execDetail.addParam(voudto.getSorgcode());
							execDetail.addParam(voudto.getStrecode());
							execDetail.addParam(voudto.getShold3());
							execDetail.addParam(voudto.getShold4());
							execDetail.addParam(voudto.getSvtcode());
							isexislist = (List)execDetail.runQuery(sql, voudto.getClass()).getDtoCollection();
							if(isexislist==null||isexislist.size()<=0)
								list.add(voudto);
							voudto = new TvVoucherinfoDto();
							voudto.setSorgcode(tempdto.getSorgcode());
							voudto.setStrecode(tempdto.getStrecode());
							voudto.setShold3(startDate);
							voudto.setShold4(endDate);
							voudto.setSvtcode(MsgConstant.VOUCHER_NO_3512);
							execDetail.addParam(voudto.getSorgcode());
							execDetail.addParam(voudto.getStrecode());
							execDetail.addParam(voudto.getShold3());
							execDetail.addParam(voudto.getShold4());
							execDetail.addParam(voudto.getSvtcode());
							isexislist = (List)execDetail.runQuery(sql, voudto.getClass()).getDtoCollection();
							if(isexislist==null||isexislist.size()<=0)
								list.add(voudto);
							voudto = new TvVoucherinfoDto();
							voudto.setSorgcode(tempdto.getSorgcode());
							voudto.setStrecode(tempdto.getStrecode());
							voudto.setShold3(startDate);
							voudto.setShold4(endDate);
							voudto.setSvtcode(MsgConstant.VOUCHER_NO_3513);
							execDetail.addParam(voudto.getSorgcode());
							execDetail.addParam(voudto.getStrecode());
							execDetail.addParam(voudto.getShold3());
							execDetail.addParam(voudto.getShold4());
							execDetail.addParam(voudto.getSvtcode());
							isexislist = (List)execDetail.runQuery(sql, voudto.getClass()).getDtoCollection();
							if(isexislist==null||isexislist.size()<=0)
								list.add(voudto);
						}
				}catch (Exception e) {
					if(execDetail!=null)
						execDetail.closeConnection();
							throw e;
				}finally
				{
					if(execDetail!=null)
						execDetail.closeConnection();
				}
				Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER);
				List resultList = voucher.voucherGenerate(list);
				if(resultList!=null&&resultList.size()>0)
					logger.info("定时生成报表对账凭证任务生成条数=================================="+resultList.get(0));
			}
		}else
		{
			return eventContext;
		}
		logger.info("定时生成报表对账凭证任务end==================================");
		return eventContext;
	}
}

package com.cfcc.itfe.component;


import java.util.ArrayList;
import java.util.HashMap;
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
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 
 * 定时生成业务对账凭证
 * 
 */
@SuppressWarnings("unchecked")
public class TimerGenBussVoucherComponent implements Callable {
	private static Log logger = LogFactory.getLog(TimerGenBussVoucherComponent.class);
	public Object onCall(MuleEventContext eventContext) throws Exception {
		logger.info("定时生成业务对账凭证任务start=================================="+TimeFacade.getCurrentStringTime());
		String startDate = TimeFacade.getCurrentStringTime();
		String endDate = TimeFacade.getCurrentStringTime();
			List<TvVoucherinfoDto> list = new ArrayList<TvVoucherinfoDto>();
			// 修改为取缓存里面的国库代码 --20150316 hua
			SQLExecutor execTre = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			String sqltre = "SELECT * FROM TS_TREASURY t WHERE t.S_TRECODE IN (SELECT p.S_TRECODE FROM TS_STAMPPOSITION p WHERE p.S_VTCODE IN ('3507','3508','3510'))";
			List<TsTreasuryDto> listtre = (List<TsTreasuryDto>) execTre.runQuery(sqltre, TsTreasuryDto.class).getDtoCollection();
			
//			HashMap<String, TsTreasuryDto> tremap =  SrvCacheFacade.cacheTreasuryInfo(null);
			if(listtre!=null&&listtre.size()>0)
			{
//				TvVoucherinfoDto voudto = null;
				SQLExecutor execDetail = null;
				try{
						execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
//						List isexislist = null;
//						String sql = "";
						creatBill(startDate, endDate, list, listtre, execDetail);//每日对账
						if(TimeFacade.getEndDateOfMonth(endDate).equals(endDate)){//月度对账
							String firstdate = endDate.substring(0, 6)+"01";
							creatBill(firstdate, endDate, list, listtre, execDetail);
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
//		}else
//		{
//			return eventContext;
//		}
		logger.info("定时生成业务对账凭证任务end==================================");
		return eventContext;
	}
	private void creatBill(String startDate, String endDate,
			List<TvVoucherinfoDto> list, List<TsTreasuryDto> listtre,
			SQLExecutor execDetail) throws JAFDatabaseException {
		TvVoucherinfoDto voudto;
		List isexislist;
		String sql = "select * from TV_VOUCHERINFO where S_ORGCODE=? and S_TRECODE=? and S_HOLD3=? and S_HOLD4=? and S_VTCODE=?";
		for(TsTreasuryDto tempdto:listtre)
		{
			voudto = new TvVoucherinfoDto();
			voudto.setSorgcode(tempdto.getSorgcode());
			voudto.setStrecode(tempdto.getStrecode());
			voudto.setShold3(startDate);
			voudto.setShold4(endDate);
			voudto.setSvtcode(MsgConstant.VOUCHER_NO_3507);
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
			voudto.setSvtcode(MsgConstant.VOUCHER_NO_3508);
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
			voudto.setSvtcode(MsgConstant.VOUCHER_NO_3509);
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
			voudto.setSvtcode(MsgConstant.VOUCHER_NO_3510);
			execDetail.addParam(voudto.getSorgcode());
			execDetail.addParam(voudto.getStrecode());
			execDetail.addParam(voudto.getShold3());
			execDetail.addParam(voudto.getShold4());
			execDetail.addParam(voudto.getSvtcode());
			isexislist = (List)execDetail.runQuery(sql, voudto.getClass()).getDtoCollection();
			if(isexislist==null||isexislist.size()<=0)
				list.add(voudto);
		}
	}
	
}

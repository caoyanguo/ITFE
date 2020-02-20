package com.cfcc.itfe.component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherFactory;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 功能：无纸化凭证自动提交TIPS任务
 * @author 何健荣
 * @time  2014-03-07
 */
public class VoucherTimerSendTips implements Callable{
	private static Log logger = LogFactory.getLog(VoucherTimerSendTips.class);
	
	public Object onCall(MuleEventContext eventContext)  {
		logger.debug("======================== 凭证库定时发送Tips任务开启 ========================");		
		Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
						MsgConstant.VOUCHER);
		try {
			Map<String, List> map = SrvCacheFacade.cacheVoucherAuto(new TsVouchercommitautoDto().columnScommitauto());
			if(map.size()>0){
				List lists=findVoucherDto(map.get("VOUCHER"));				
				if(lists!=null&&lists.size()>0){
					for(List list:(List<List>)lists){
						voucherCommit(packageGenerateByVtcode(list, 1000), voucher);
					}
				}
			}				
		}catch(Exception e){
			logger.error(e);
			VoucherException.saveErrInfo(null, e);
		}		
		logger.debug("======================== 凭证库定时发送Tips任务关闭 ========================");
		return null;	
	}
	
	/**
	 * 根据组织机构代码、区划代码、凭证类型、状态查找凭证
	 * 且根据凭证类型分组
	 * @param list
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	private List findVoucherDto(List list) throws JAFDatabaseException, ValidateException{		
		return findVoucherDto(list, null,true);
	}
	
	/**
	 * 根据组织机构代码、区划代码、凭证类型、状态查找凭证
	 * 且根据凭证类型分组
	 * @param list
	 * @param status
	 * @param flag
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List findVoucherDto(List list, String status,boolean flag) throws JAFDatabaseException, ValidateException{
		List lists=new ArrayList();
		TvVoucherinfoDto voucherDto=new TvVoucherinfoDto();	
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(TimeFacade.getCurrentDate());
		calendar.add(Calendar.DATE, -10);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
		String getdate = dateFormat.format(calendar.getTime());
		SQLExecutor sqlExe = null;
		try
		{
			sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			String sql = null;
			for(List auto:(List<List>)list){
				sql = "SELECT * FROM TV_VOUCHERINFO where S_ORGCODE=? and S_ADMDIVCODE=? and S_VTCODE = ? and S_STATUS=? ";
				sqlExe.addParam(auto.get(0));
				sqlExe.addParam(auto.get(2));
				sqlExe.addParam(auto.get(3));
				if(flag)
				{
					status = getStatus(auto.get(3)+"")+"";
					sqlExe.addParam(status);
				}
				else
				{
					if(MsgConstant.VOUCHER_NO_5408.equals(auto.get(3)))
						sqlExe.addParam(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					else
						sqlExe.addParam(status);
				}
				voucherDto.setSorgcode(auto.get(0)+"");
				voucherDto.setSadmdivcode(auto.get(2)+"");					
				voucherDto.setSvtcode(auto.get(3)+"");
				voucherDto.setSstatus(status);
				List voucherList= null;
				//防止自动提交 ，厦门需求 
				if ((!(DealCodeConstants.VOUCHER_STAMP+DealCodeConstants.VOUCHER_SUCCESS_NO_BACK+DealCodeConstants.VOUCHER_FAIL_TCBS).contains(status))&&ITFECommonConstant.PUBLICPARAM.indexOf(",xm5207,")>=0 &&(MsgConstant.VOUCHER_NO_5207.equals(voucherDto.getSvtcode())||MsgConstant.VOUCHER_NO_5267.equals(voucherDto.getSvtcode()))) {
					sql = sql + " and S_EXT2<>? ";
					sqlExe.addParam("1");
				}else
				{
					sql = sql + " and S_CREATDATE>=?";
					sqlExe.addParam(getdate);
				}
				voucherList = (List)sqlExe.runQuery(sql,TvVoucherinfoDto.class).getDtoCollection();	
				if(voucherList!=null&&voucherList.size()>0) 
				{
					lists.add(voucherList);	
				}
			}
		}catch(Exception e)
		{
			logger.error("定时任务查询sql错误VoucherTimerSendTips.findVoucherDto：",e);
		}finally
		{
			if(sqlExe!=null)
				sqlExe.closeConnection();
		}
		return lists;
	}
	
	/**
	 * 凭证提交
	 * @param lists
	 * @param voucher
	 */
	private void voucherCommit(List lists,Voucher voucher) {
		for(List list:(List<List>)lists){
			try {
				voucher.voucherCommit(list);
			} catch (ITFEBizException e) {
				logger.error(e);
				VoucherException.saveErrInfo((((List<TvVoucherinfoDto>)list).get(0)).getSvtcode(), e);
			}
		}
	}
	
	/**
	 * 凭证分包
	 * @param list
	 * @param size 最大分包数量
	 * @return
	 */
	private List packageGenerateByVtcode(List list,int size){
		List<TvVoucherinfoDto> voucherList=new ArrayList<TvVoucherinfoDto>();
		List<List> lists=new ArrayList<List>();
		lists.add(voucherList);
		for(int i=0;i<list.size();i++){			
			if(i!=0&&i%size==0){
				voucherList=new ArrayList();
				lists.add(voucherList);
			}voucherList.add(((List<TvVoucherinfoDto>)list).get(i));
		}
		return lists;
	}
	
	/**
	 * 根据凭证类型查找凭证状态
	 * @param svtcode
	 * @return
	 */
	private String getStatus(String svtcode){
		if(svtcode.equals(MsgConstant.VOUCHER_NO_5207)||
				svtcode.equals(MsgConstant.VOUCHER_NO_5209)||svtcode.equals(MsgConstant.VOUCHER_NO_5267))
			return DealCodeConstants.VOUCHER_CHECK_SUCCESS;				
		else if((svtcode.equals(MsgConstant.VOUCHER_NO_2301)||
				 svtcode.equals(MsgConstant.VOUCHER_NO_2302))&&
				 	ITFECommonConstant.ISITFECOMMIT.equals(StateConstant.COMMON_YES)){			
			return DealCodeConstants.VOUCHER_VALIDAT_SUCCESS;				
		}else if(svtcode.equals(MsgConstant.VOUCHER_NO_5106)||
				svtcode.equals(MsgConstant.VOUCHER_NO_5108))
			return DealCodeConstants.VOUCHER_VALIDAT_SUCCESS;
		return null;
	}
}

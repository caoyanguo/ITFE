package com.cfcc.itfe.client.dataquery.selectrecord2;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.dataquery.selectrecord1.ISelectRecordGDService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;

/**
 * codecomment: 
 * @author db2admin
 * @time   16-01-18 10:15:19
 * 子系统: DataQuery
 * 模块:selectRecord2
 * 组件:SelectRecordTwoGD
 */
public class SelectRecordTwoGDBean extends AbstractSelectRecordTwoGDBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(SelectRecordTwoGDBean.class);
    private ITFELoginInfo loginInfo;
    private String reportPath = "com/cfcc/itfe/client/ireport/guangdongTwoReport.jasper";
	private List reportlist = null;
	private Map reportmap = null;
    public SelectRecordTwoGDBean() {
      super();
      searchDto = new TvPayreckBankDto();
      loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();    
      searchDto.setSbookorgcode(loginInfo.getSorgcode());
      pagingcontext = new PagingContext(this);
      startDate = TimeFacade.getCurrentDateTime();
      endDate = TimeFacade.getCurrentDateTime();
      startMoney = new BigDecimal(0.00);
      endMoney = new BigDecimal(0.00);
      reportlist = new ArrayList();
      reportmap = new HashMap();
    }
    
	/**
	 * Direction: 查询
	 * ename: queryInfo
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryInfo(Object o){
    	if (startDate == null || startDate.equals("")) {
			MessageDialog.openMessageDialog(null, "请输入起始日期！");
			return "";
		}
    	if (endDate == null || endDate.equals("")) {
			MessageDialog.openMessageDialog(null, "请输入终止日期！");
			return "";
		}
    	
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = new PageResponse();
		pageResponse = retrieve(pageRequest);
		if (pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录！");
		}
		pagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
          return super.queryInfo(o);
    }

	/**
	 * Direction: 打印
	 * ename: queryPrint
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryPrint(Object o){
    	 reportlist.clear();
         reportmap.clear();
    	if (startDate == null || startDate.equals("")) {
			MessageDialog.openMessageDialog(null, "请输入起始日期！");
			return "";
		}
    	if (endDate == null || endDate.equals("")) {
			MessageDialog.openMessageDialog(null, "请输入终止日期！");
			return "";
		}
    	String ls_AndSql = "";
		if(startMoney!=null&&startMoney.compareTo(new BigDecimal(0))!=0){
			ls_AndSql = " and F_AMT >= "+startMoney;
		}
		if(endMoney!=null&&endMoney.compareTo(new BigDecimal(0))!=0){
			ls_AndSql += " and F_AMT <= "+endMoney;
		}
		try {
			reportlist = commonDataAccessService.findRsByDto(searchDto," and S_RESULT = '"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' and D_ENTRUSTDATE >= '"+startDate+"' and D_ENTRUSTDATE <= '"+endDate+"' "+ls_AndSql, "TV_PAYRECK_BANK");
			BigDecimal allMoney = new BigDecimal(0.00);
			for(int i=0;i<reportlist.size();i++){
				((TvPayreckBankDto)reportlist.get(i)).setSfinorgcode((i+1)+"");
				allMoney = allMoney.add(((TvPayreckBankDto)reportlist.get(i)).getFamt());
			}
			reportmap.put("SUM_AMT", allMoney);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Throwable e) {
			log.error(e);
			Exception e1 = new Exception("查询数据异常！", e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(), e1);
		}
    	if(reportlist == null || reportlist.size() == 0 ){
    		MessageDialog.openMessageDialog(null, "没有需要打印的数据！");
    		return null;
    	}
          return super.queryPrint(o);
    }

	/**
	 * Direction: 返回
	 * ename: backQuery
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String backQuery(Object o){
          return super.backQuery(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
		PageResponse page = null;
		String ls_AndSql = "";
		if(startMoney!=null&&startMoney.compareTo(new BigDecimal(0))!=0){
			ls_AndSql = " and F_AMT >= "+startMoney;
		}
		if(endMoney!=null&&endMoney.compareTo(new BigDecimal(0))!=0){
			ls_AndSql += " and F_AMT <= "+endMoney;
		}
		try {
				page = commonDataAccessService.findRsByDtoPaging(searchDto, pageRequest,
						" S_BOOKORGCODE = '"+loginInfo.getSorgcode()+"' and S_RESULT = '"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' and D_ENTRUSTDATE >= '"+startDate+"' and D_ENTRUSTDATE <= '"+endDate+"' "+ls_AndSql, "D_ENTRUSTDATE DESC");
			return page;

		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Throwable e) {
			log.error(e);
			Exception e1 = new Exception("查询数据异常！", e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(), e1);
		}
		return super.retrieve(pageRequest);
	}

	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public String getReportPath() {
		return reportPath;
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public List getReportlist() {
		return reportlist;
	}

	public void setReportlist(List reportlist) {
		this.reportlist = reportlist;
	}

	public Map getReportmap() {
		return reportmap;
	}

	public void setReportmap(Map reportmap) {
		this.reportmap = reportmap;
	}

}
package com.cfcc.itfe.client.dataquery.tvincomedetailreportcheck;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.persistence.dto.TvIncomeDetailReportCheckDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author hjr
 * @time   14-02-26 15:26:35
 * 子系统: DataQuery
 * 模块:tvIncomeDetailReportCheck
 * 组件:TvIncomeDetailReportCheck
 */
public class TvIncomeDetailReportCheckBean extends AbstractTvIncomeDetailReportCheckBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TvIncomeDetailReportCheckBean.class);
    private ITFELoginInfo loginInfo;
    private TsSyslogDto syslogdto;
    PagingContext logpagingcontext = null;
    public TvIncomeDetailReportCheckBean() {
      super();
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto = new TvIncomeDetailReportCheckDto(); 
      syslogdto = new TsSyslogDto();
      dto.setSorgcode(loginInfo.getSorgcode());
      dto.setScreatdate(TimeFacade.getCurrentStringTimebefor());
      logpagingcontext = new PagingContext(this);
      pagingcontext = new PagingContext(this);
      init();
    }
    
	// 初始化显示明细记录
	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
    
	/**
	 * Direction: 查询
	 * ename: search
	 * 引用方法: 
	 * viewers: 报表与入库流水核对界面
	 * messages: 
	 */
    public String search(Object o){
		refreshTable();
		return super.search(o);
    }

	/**
	 * Direction: 核对
	 * ename: incomeDetailReportCheck
	 * 引用方法: 
	 * viewers: 报表与入库流水核对界面
	 * messages: 
	 */
    public String incomeDetailReportCheck(Object o){
    		if(dto.getScreatdate()==null){
    			MessageDialog.openMessageDialog(null, "请输入报表日期！");
    			return null;
    		}
    		try {
				String result=tvIncomeDetailReportCheckService.incomeDetailReportCheck(dto);
				if(result.length()==0){
					List list=commonDataAccessService.findRsByDto(dto);
					if(list==null||list.size()==0){
						MessageDialog.openMessageDialog(null, "该日期下无要核对的数据！");						
						init();
						editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
						return "";
					}else
						MessageDialog.openMessageDialog(null, "报表与入库流水核对完成！");				
				}else					
					MessageDialog.openMessageDialog(null, result);					
				refreshTable();			
			} catch (ITFEBizException e) {
				log.error(e);		
				MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e);
			}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
			}catch(Throwable e){
				log.error(e);	
				MessageDialog.openErrorDialog(
						Display.getDefault().getActiveShell(),new Exception("操作出现异常！",e));
			}		
          return super.incomeDetailReportCheck(o);
    }
    
	/**
	 * Direction: 查询日志
	 * ename: querysyslog
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String querysyslog(Object o){
    	if(StringUtils.isBlank(syslogdto.getSdate())){
    		MessageDialog.openMessageDialog(null, "请输入报表日期！");
			return null;
    	}
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrievelog(pageRequest);
		if(pageResponse.getTotalCount()==0)
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录！");
		logpagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.querysyslog(o);
    }
    
    public void refreshTable(){
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(new PageRequest());
		if(pageResponse.getTotalCount()==0)
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录，请先核对数据！");
		pagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);    
    	try {
    		return commonDataAccessService.findRsByDtoPaging(dto,
					pageRequest, "1=1", "TS_SYSUPDATE DESC");
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}	catch (Throwable e) {	
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),new Exception("查询数据异常！",e));
		}
		return super.retrieve(pageRequest);
	}
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrievelog(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);    
    	String sqlwhere = "1=1";
    	if(StringUtils.isBlank(syslogdto.getSoperationtypecode())){
    		sqlwhere += " and S_OPERATIONTYPECODE in ('3128','3129','3139')";
    	}
    	try {
    		return commonDataAccessService.findRsByDtoPaging(syslogdto,
					pageRequest, sqlwhere, "S_TIME DESC");
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}	catch (Throwable e) {	
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),new Exception("查询数据异常！",e));
		}
		return super.retrieve(pageRequest);
	}

	public TsSyslogDto getSyslogdto() {
		return syslogdto;
	}

	public void setSyslogdto(TsSyslogDto syslogdto) {
		this.syslogdto = syslogdto;
	}

	public PagingContext getLogpagingcontext() {
		return logpagingcontext;
	}

	public void setLogpagingcontext(PagingContext logpagingcontext) {
		this.logpagingcontext = logpagingcontext;
	}

}
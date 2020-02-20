package com.cfcc.itfe.client.dataquery.tvexceptionmanquery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvExceptionmanDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author Administrator
 * @time   13-03-28 14:49:35
 * 子系统: DataQuery
 * 模块:tvexceptionmanquery
 * 组件:Tvexceptionmanquery
 */
public class TvexceptionmanqueryBean extends AbstractTvexceptionmanqueryBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TvexceptionmanqueryBean.class);
  	private PagingContext pagingContext; // 分页控件
	protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
	private String dworkdatestart;//开始日期
	private String dworkdateend;//结束日期     
    public TvexceptionmanqueryBean() {
     	super();
      	dto = new TvExceptionmanDto();
      	dworkdatestart = TimeFacade.getCurrentStringTime();
		dworkdateend = TimeFacade.getCurrentStringTime();
		pagingContext = new PagingContext(this);
    }
    
	/**
	 * Direction: 查询
	 * ename: query
	 * 引用方法: 
	 * viewers: 报文异常信息查询结果
	 * messages: 
	 */
    public String query(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingContext.setPage(pageResponse);

		if (pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			editor.fireModelChanged();
			return null;
		}
		editor.fireModelChanged();
		return "";
    }

	/**
	 * Direction: 返回
	 * ename: back
	 * 引用方法: 
	 * viewers: 报文异常信息查询
	 * messages: 
	 */
    public String back(Object o){
          return super.back(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	TvExceptionmanDto eDto=new TvExceptionmanDto();
    	eDto.setSofbizkind(dto.getSofbizkind());
   		StringBuffer wheresql = new StringBuffer();
		StringBuffer datesql=new StringBuffer();
		wheresql.append("1=1");
		if(isNotNull(eDto.getSofbizkind()))
			wheresql.append(" AND (S_OFBIZKIND='"+MsgConstant.VOUCHER_MSG_SERVER+eDto.getSofbizkind()+"' OR S_OFBIZKIND='"+eDto.getSofbizkind()+"')");
			eDto.setSofbizkind(null);
		if(isNull(dworkdatestart)&&isNull(dworkdateend)){
			
		}else{
			if(isNotNull(dworkdatestart))
			{
				datesql.append(" D_WORKDATE >= '"+dworkdatestart+"' ");
				if(isNotNull(dworkdateend))
					datesql.append(" AND D_WORKDATE <= '"+dworkdateend+"' ");
			}else
				datesql.append(" D_WORKDATE <= '"+dworkdateend+"' ");
		}
		if(datesql.toString()!=null&&datesql.toString().length()>0){
			wheresql.append(" AND ( ").append(datesql.toString()).append(")");
		}
		try{
		return commonDataAccessService.findRsByDtoPaging(eDto, pageRequest, wheresql.toString(), " TS_UPDATE desc");			
		} catch (ITFEBizException e) {
			log.error("查看异常日志信息出现异常！", e);
			MessageDialog.openErrorDialog(null, e);
			return super.retrieve(pageRequest);
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失败\r\n请重新登录！");
		}catch(Exception e){
			Exception e1=new Exception("查看异常日志信息出现异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			
		}
		return null;
	}
    
    private boolean isNull(Object object)
	{
		if(object==null||"NULL".equals(object.toString().toUpperCase())||"".equals(object.toString()))
			return true;
		else
			return false;
	}
	private boolean isNotNull(Object object)
	{
		if(object==null||"NULL".equals(object.toString().toUpperCase())||"".equals(object.toString()))
			return false;
		else
			return true;
	}

	public PagingContext getPagingContext() {
		return pagingContext;
	}

	public void setPagingContext(PagingContext pagingContext) {
		this.pagingContext = pagingContext;
	}

	public String getDworkdatestart() {
		return dworkdatestart;
	}

	public void setDworkdatestart(String dworkdatestart) {
		this.dworkdatestart = dworkdatestart;
	}

	public String getDworkdateend() {
		return dworkdateend;
	}

	public void setDworkdateend(String dworkdateend) {
		this.dworkdateend = dworkdateend;
	}

}
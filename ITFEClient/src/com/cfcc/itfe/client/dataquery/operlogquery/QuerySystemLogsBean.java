package com.cfcc.itfe.client.dataquery.operlogquery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 09-10-23 09:48:31 子系统: DataQuery 模块:OperLogQuery 组件:QuerySystemLogs
 */
public class QuerySystemLogsBean extends AbstractQuerySystemLogsBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(QuerySystemLogsBean.class);
	private TsSyslogDto dto = new TsSyslogDto();
	private PagingContext pagingcontext = new PagingContext(this);
	private String datestart=null;
	private String dateend=null;
	private List<Mapper> userlist=null;
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();

	public TsSyslogDto getDto() {
		return dto;
	}

	public void setDto(TsSyslogDto dto) {
		this.dto = dto;
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public QuerySystemLogsBean() {
		super();		
		datestart=TimeFacade.getCurrentStringTime();
		dateend=TimeFacade.getCurrentStringTime();
		dto.setSorgcode(loginfo.getSorgcode());
	}

	/**
	 * Direction: 系统日志查询 ename: querySystemLogs 引用方法: viewers: 查询结果 messages:
	 */
	public String querySystemLogs(Object o) {
		if(StringUtils.isNotBlank(datestart)&&Integer.parseInt(datestart)>Integer.parseInt(TimeFacade.getCurrentStringTime())){
			MessageDialog.openMessageDialog(null, "所属日期起不能大于当前系统日期！");
    		return "";
		}
		if(StringUtils.isNotBlank(dateend)&&Integer.parseInt(dateend)>Integer.parseInt(TimeFacade.getCurrentStringTime())){
			MessageDialog.openMessageDialog(null, "所属日期止不能大于当前系统日期！");
    		return "";
		}
		if(StringUtils.isNotBlank(datestart)&&StringUtils.isNotBlank(dateend)&&Integer.parseInt(datestart)>Integer.parseInt(dateend)){
			MessageDialog.openMessageDialog(null, "所属日期起不能大于所属日期止！");
    		return "";
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " 查询无记录！");
			return null;
		}
		return super.querySystemLogs(o);
	}

	/**
	 * Direction: 返回日志查询 ename: backQuerySysLogs 引用方法: viewers: 查询结果 messages:
	 */
	public String backQuerySysLogs(Object o) {
		return super.backQuerySysLogs(o);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		pageRequest.setPageSize(50);
		StringBuffer wheresql = new StringBuffer();
		wheresql.append("1=1");
		if(StringUtils.isNotBlank(datestart))
			wheresql.append(" AND s_date >= ").append(datestart);
		if(StringUtils.isNotBlank(dateend))
			wheresql.append(" AND s_date <= ").append(dateend);
		try {
			return commonDataAccessService.findRsByDtoPaging(dto, pageRequest, wheresql.toString(), " S_TIME desc");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}return null;
	}

	public String getDatestart() {
		return datestart;
	}

	public void setDatestart(String datestart) {
		this.datestart = datestart;
	}

	public String getDateend() {
		return dateend;
	}

	public void setDateend(String dateend) {
		this.dateend = dateend;
	}

	public List<Mapper> getUserlist() {
		
		if(userlist==null||userlist.size()<=0)
		{
			TsUsersDto querydto = new TsUsersDto();
			querydto.setSorgcode(loginfo.getSorgcode());
			List<TsUsersDto> templist = null;
			userlist = new ArrayList<Mapper>();
			try {
				templist = commonDataAccessService.findRsByDto(querydto);
				if(templist!=null&&templist.size()>0)
				{
					Mapper mapper = null;
					for(TsUsersDto tempdto:templist)
					{
						mapper = new Mapper(tempdto.getSusercode(),tempdto.getSusername());
						userlist.add(mapper);
					}
				}
			} catch (ITFEBizException e) {
				log.error(e);
			}
		}
		return userlist;
	}

	public void setUserlist(List<Mapper> userlist) {
		this.userlist = userlist;
	}
}
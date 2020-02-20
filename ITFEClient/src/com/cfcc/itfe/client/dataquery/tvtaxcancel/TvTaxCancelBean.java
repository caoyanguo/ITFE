/**
 * 实时冲正信息查询
 */
package com.cfcc.itfe.client.dataquery.tvtaxcancel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvTaxCancelDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:实时冲正信息查询
 * 
 * @author wangtuo
 * @time 10-06-02 09:48:01 子系统: DataQuery 模块:TvTaxCancel 组件:TvTaxCancel
 */
public class TvTaxCancelBean extends AbstractTvTaxCancelBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TvTaxCancelBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();

	public TvTaxCancelBean() {
		super();
		dto = new TvTaxCancelDto();
		dto.setSentrustdate(TimeFacade.getCurrentStringTime());
		pagingcontext = new PagingContext(this);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		return super.singleSelect(o);
	}

	/**
	 * Direction: 查询 ename: taxCancelQuery 引用方法: viewers: 实时冲正查询结果 messages:
	 */
	public String taxCancelQuery(Object o) {

		// 返回页面
		String returnpage = null;

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " 查询无记录！");
			returnpage = "实时冲正信息查询";
		} else {
			returnpage = super.taxCancelQuery(o);
		}
		return returnpage;
	}

	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 实时冲正信息查询 messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, "1=1", "", dto.tableName());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TvTaxCancelBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}
}
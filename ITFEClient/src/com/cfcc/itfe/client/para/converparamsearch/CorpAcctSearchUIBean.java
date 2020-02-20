package com.cfcc.itfe.client.para.converparamsearch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.persistence.dto.TdCorpacctDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author db2admin
 * @time 13-03-05 18:50:38 子系统: Para 模块:ConverParamSearch 组件:CorpAcctSearchUI
 */
public class CorpAcctSearchUIBean extends AbstractCorpAcctSearchUIBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(CorpAcctSearchUIBean.class);

	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	private PagingContext pagingcontext = new PagingContext(this);
	private TdCorpacctDto querydto = null;

	public CorpAcctSearchUIBean() {
		super();
		querydto= new TdCorpacctDto();
	}

	/**
	 * Direction: 查询 ename: queryBudget 引用方法: viewers: 查询结果 messages:
	 */
	public String queryBudget(Object o) {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		return super.queryBudget(o);
	}

	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 信息查询 messages:
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
			querydto.setSbookorgcode(loginfo.getSorgcode());
			return commonDataAccessService.findRsByDtoWithWherePaging(querydto,
					pageRequest, "1=1");

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public TdCorpacctDto getQuerydto() {
		return querydto;
	}

	public void setQuerydto(TdCorpacctDto querydto) {
		this.querydto = querydto;
	}

}
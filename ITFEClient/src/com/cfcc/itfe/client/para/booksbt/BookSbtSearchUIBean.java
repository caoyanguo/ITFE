package com.cfcc.itfe.client.para.booksbt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.persistence.dto.TdBooksbtDto;
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
 * @time 13-03-05 20:05:36 子系统: Para 模块:Booksbt 组件:BookSbtSearchUI
 */
public class BookSbtSearchUIBean extends AbstractBookSbtSearchUIBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(BookSbtSearchUIBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	private PagingContext pagingcontext = new PagingContext(this);
	private TdBooksbtDto querydto = null;

	public BookSbtSearchUIBean() {
		super();
		querydto = new TdBooksbtDto();
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

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public TdBooksbtDto getQuerydto() {
		return querydto;
	}

	public void setQuerydto(TdBooksbtDto querydto) {
		this.querydto = querydto;
	}

}
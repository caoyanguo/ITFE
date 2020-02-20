/**
 * 实时扣税查询
 */
package com.cfcc.itfe.client.dataquery.onlinetaxquery;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvTaxDto;
import com.cfcc.itfe.persistence.dto.TvTaxItemDto;
import com.cfcc.itfe.persistence.dto.TvTaxKindDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:实时扣税信息查询 实时扣税历史信息查询 实时扣税税目历史信息 实时扣税税种历史信息 根据输入日期判别查询当天或历史表
 * 
 * @author wangtuo
 * @time 10-06-01 16:26:06 子系统: DataQuery 模块:onlineTaxQuery 组件:OnlineTaxQuery
 */
public class OnlineTaxQueryBean extends AbstractOnlineTaxQueryBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(OnlineTaxQueryBean.class);

	private PagingContext pagingcontext = new PagingContext(this);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();

	private List<TvTaxKindDto> tvTaxKindList; // 实时扣税税种列表 TVTAXKIND
	private List<TvTaxItemDto> tvTaxItemList; // 实时扣税税目列表 TVTAXITEM

	/**
	 * 实时扣税税种dto
	 */
	TvTaxKindDto taxkinddto = null;

	/**
	 * 实时扣税税目dto
	 */
	TvTaxItemDto taxitemdto = null;

	// 实时扣税单选用dto
	private TvTaxDto taxsingledto = null;

	/**
	 * 实时扣税信息(1001)
	 */
	TvTaxDto taxdto = null;

	public OnlineTaxQueryBean() {
		super();
		// 实时扣税信息
		taxdto = new TvTaxDto();

		// 默认查询日期为当天
		taxdto.setSentrustdate(TimeFacade.getCurrentStringTime());
	}

	/**
	 * Direction: 查询 ename: query 引用方法: viewers: 实时扣税查询结果 messages:
	 */
	public String query(Object o) {

		// 返回页面
		String returnpage = null;

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " 查询无记录！");
			returnpage = "实时扣税查询条件";
		} else {
			returnpage = super.query(o);
		}
		return returnpage;
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		taxsingledto = (TvTaxDto) o;
		// 刷新实时扣税信息列表
		fireClickTax();

		editor.fireModelChanged();
		return super.singleSelect(o);
	}

	/**
	 * 刷新实时扣税信息列表单击事件
	 */
	private void fireClickTax() {
		try {

			// 实时扣税税种查询dto
			taxkinddto = new TvTaxKindDto();
			taxkinddto.setSseq(taxsingledto.getSseq());
			// 实时扣税税种列表 TVTAXKIND
			tvTaxKindList = commonDataAccessService.findRsByDto(taxkinddto, "",
					taxkinddto.tableName());

			// 实时扣税税目查询dto
			taxitemdto = new TvTaxItemDto();

			taxitemdto.setSseq(taxsingledto.getSseq());
			// 实时扣税税目列表 TVTAXITEM
			tvTaxItemList = commonDataAccessService.findRsByDto(taxitemdto, "",
					taxitemdto.tableName());

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		editor.fireModelChanged();
	}

	/**
	 * Direction: 返回查询界面 ename: goback 引用方法: viewers: 实时扣税查询结果 messages:
	 */
	public String goback(Object o) {
		tvTaxKindList.clear();
		tvTaxItemList.clear();
		return super.goback(o);
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
			// 实时扣税信息查询
			return commonDataAccessService.findRsByDtoWithWherePaging(taxdto,
					pageRequest, "1=1", "", taxdto.tableName());
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
		OnlineTaxQueryBean.log = log;
	}

	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public List<TvTaxKindDto> getTvTaxKindList() {
		return tvTaxKindList;
	}

	public void setTvTaxKindList(List<TvTaxKindDto> tvTaxKindList) {
		this.tvTaxKindList = tvTaxKindList;
	}

	public List<TvTaxItemDto> getTvTaxItemList() {
		return tvTaxItemList;
	}

	public void setTvTaxItemList(List<TvTaxItemDto> tvTaxItemList) {
		this.tvTaxItemList = tvTaxItemList;
	}

	public TvTaxKindDto getTaxkinddto() {
		return taxkinddto;
	}

	public void setTaxkinddto(TvTaxKindDto taxkinddto) {
		this.taxkinddto = taxkinddto;
	}

	public TvTaxItemDto getTaxitemdto() {
		return taxitemdto;
	}

	public void setTaxitemdto(TvTaxItemDto taxitemdto) {
		this.taxitemdto = taxitemdto;
	}

	public TvTaxDto getTaxsingledto() {
		return taxsingledto;
	}

	public void setTaxsingledto(TvTaxDto taxsingledto) {
		this.taxsingledto = taxsingledto;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public TvTaxDto getTaxdto() {
		return taxdto;
	}

	public void setTaxdto(TvTaxDto taxdto) {
		this.taxdto = taxdto;
	}
}
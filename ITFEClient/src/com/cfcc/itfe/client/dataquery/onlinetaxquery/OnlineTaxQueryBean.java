/**
 * ʵʱ��˰��ѯ
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
 * codecomment:ʵʱ��˰��Ϣ��ѯ ʵʱ��˰��ʷ��Ϣ��ѯ ʵʱ��˰˰Ŀ��ʷ��Ϣ ʵʱ��˰˰����ʷ��Ϣ �������������б��ѯ�������ʷ��
 * 
 * @author wangtuo
 * @time 10-06-01 16:26:06 ��ϵͳ: DataQuery ģ��:onlineTaxQuery ���:OnlineTaxQuery
 */
public class OnlineTaxQueryBean extends AbstractOnlineTaxQueryBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(OnlineTaxQueryBean.class);

	private PagingContext pagingcontext = new PagingContext(this);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();

	private List<TvTaxKindDto> tvTaxKindList; // ʵʱ��˰˰���б� TVTAXKIND
	private List<TvTaxItemDto> tvTaxItemList; // ʵʱ��˰˰Ŀ�б� TVTAXITEM

	/**
	 * ʵʱ��˰˰��dto
	 */
	TvTaxKindDto taxkinddto = null;

	/**
	 * ʵʱ��˰˰Ŀdto
	 */
	TvTaxItemDto taxitemdto = null;

	// ʵʱ��˰��ѡ��dto
	private TvTaxDto taxsingledto = null;

	/**
	 * ʵʱ��˰��Ϣ(1001)
	 */
	TvTaxDto taxdto = null;

	public OnlineTaxQueryBean() {
		super();
		// ʵʱ��˰��Ϣ
		taxdto = new TvTaxDto();

		// Ĭ�ϲ�ѯ����Ϊ����
		taxdto.setSentrustdate(TimeFacade.getCurrentStringTime());
	}

	/**
	 * Direction: ��ѯ ename: query ���÷���: viewers: ʵʱ��˰��ѯ��� messages:
	 */
	public String query(Object o) {

		// ����ҳ��
		String returnpage = null;

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѯ�޼�¼��");
			returnpage = "ʵʱ��˰��ѯ����";
		} else {
			returnpage = super.query(o);
		}
		return returnpage;
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		taxsingledto = (TvTaxDto) o;
		// ˢ��ʵʱ��˰��Ϣ�б�
		fireClickTax();

		editor.fireModelChanged();
		return super.singleSelect(o);
	}

	/**
	 * ˢ��ʵʱ��˰��Ϣ�б����¼�
	 */
	private void fireClickTax() {
		try {

			// ʵʱ��˰˰�ֲ�ѯdto
			taxkinddto = new TvTaxKindDto();
			taxkinddto.setSseq(taxsingledto.getSseq());
			// ʵʱ��˰˰���б� TVTAXKIND
			tvTaxKindList = commonDataAccessService.findRsByDto(taxkinddto, "",
					taxkinddto.tableName());

			// ʵʱ��˰˰Ŀ��ѯdto
			taxitemdto = new TvTaxItemDto();

			taxitemdto.setSseq(taxsingledto.getSseq());
			// ʵʱ��˰˰Ŀ�б� TVTAXITEM
			tvTaxItemList = commonDataAccessService.findRsByDto(taxitemdto, "",
					taxitemdto.tableName());

		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}

		editor.fireModelChanged();
	}

	/**
	 * Direction: ���ز�ѯ���� ename: goback ���÷���: viewers: ʵʱ��˰��ѯ��� messages:
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
			// ʵʱ��˰��Ϣ��ѯ
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
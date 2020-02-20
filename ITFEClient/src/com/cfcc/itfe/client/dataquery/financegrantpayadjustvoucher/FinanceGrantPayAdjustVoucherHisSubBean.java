package com.cfcc.itfe.client.dataquery.financegrantpayadjustvoucher;

import java.util.Properties;

import com.cfcc.itfe.persistence.dto.HtfGrantpayAdjustsubDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class FinanceGrantPayAdjustVoucherHisSubBean extends BasicModel
		implements IPageDataProvider {
	
	private HtfGrantpayAdjustsubDto searchDto;
	public PagingContext pagingContext;
	private ICommonDataAccessService service = (ICommonDataAccessService) getService(ICommonDataAccessService.class);

	public FinanceGrantPayAdjustVoucherHisSubBean() {
		super();
		pagingContext = new PagingContext(this);
	}
	
	/**
	 * ������ʷ����
	 * @param o
	 * @return
	 */
	public String searchDtoList(Object o){
		if(o instanceof HtfGrantpayAdjustsubDto){
			searchDto = (HtfGrantpayAdjustsubDto)o;
		}
		PageRequest request = new PageRequest();
		PageResponse response = retrieve(request);
		if (response == null || response.getTotalCount() == 0) {
			return null;
		} else {
//			pagingContext.setPageNum(response.getTotalCount());
			pagingContext.setPage(response);
		}
		return "";
	}

	public PageResponse retrieve(PageRequest request) {
		PageResponse response = new PageResponse(request);
		try {
			String wheresql = " 1=1 ";
			response = service.findRsByDtoWithWherePaging(searchDto, request,
					wheresql);
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			this.editor.fireModelChanged();
			return null;
		}
		return response;
	}

	public Properties getMESSAGE_PROPERTIES() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMessage(String _direction, String _msgkey) {
		// TODO Auto-generated method stub
		return null;
	}

	public HtfGrantpayAdjustsubDto getSearchDto() {
		return searchDto;
	}

	public void setSearchDto(HtfGrantpayAdjustsubDto searchDto) {
		this.searchDto = searchDto;
	}

	public PagingContext getPagingContext() {
		return pagingContext;
	}

	public void setPagingContext(PagingContext pagingContext) {
		this.pagingContext = pagingContext;
	}

}

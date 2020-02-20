package com.cfcc.itfe.client.recbiz.tfunitrecordmain;

import java.util.Properties;

import com.cfcc.itfe.persistence.dto.TfUnitrecordsubDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class TfUnitrecordsubBean extends BasicModel implements IPageDataProvider {
	
	private TfUnitrecordsubDto searchDto;
	public PagingContext subPagingContext;
	private ICommonDataAccessService service = (ICommonDataAccessService) getService(ICommonDataAccessService.class);

	public TfUnitrecordsubBean() {
		super();
		subPagingContext = new PagingContext(this);
	}
	
	/**
	 * ²éÕÒ×Ó±í
	 * @param o
	 * @return
	 */
	public String searchDtoList(Object o){
		if(o instanceof TfUnitrecordsubDto){
			searchDto = (TfUnitrecordsubDto)o;
		}
		PageRequest request = new PageRequest();
		PageResponse response = retrieve(request);
		if (response == null || response.getTotalCount() == 0) {
			return null;
		} else {
//			subPagingContext.setPageNum(response.getTotalCount());
			subPagingContext.setPage(response);
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
		return null;
	}

	public String getMessage(String _direction, String _msgkey) {
		return null;
	}

	public TfUnitrecordsubDto getSearchDto() {
		return searchDto;
	}

	public void setSearchDto(TfUnitrecordsubDto searchDto) {
		this.searchDto = searchDto;
	}

	public PagingContext getSubPagingContext() {
		return subPagingContext;
	}

	public void setSubPagingContext(PagingContext subPagingContext) {
		this.subPagingContext = subPagingContext;
	}

}

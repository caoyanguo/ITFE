package com.cfcc.itfe.client.recbiz.overdrawpayinto;

import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanSubDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class GrantPaySubBean implements IPageDataProvider {
	private PagingContext subpagecontext;
	private ICommonDataAccessService service;
	private TbsTvGrantpayplanMainDto mainDto ;	
	
	public GrantPaySubBean(ICommonDataAccessService service, TbsTvGrantpayplanMainDto mainDto) {
		this.subpagecontext = new PagingContext(this);
		this.service = service;
		this.mainDto = mainDto;
	}

	public PageResponse retrieve(PageRequest request) {
		try { 
			if(null != mainDto) {
				TbsTvGrantpayplanSubDto subDto = new TbsTvGrantpayplanSubDto();
				subDto.setIvousrlno(mainDto.getIvousrlno());
				PageResponse response =  service.findRsByDtoWithWherePaging(subDto, request, " 1=1 ");
				subpagecontext.setPage(response);
				return response;
			}
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return null;
	}

	public PagingContext getSubpagecontext() {
		return subpagecontext;
	}

	public void setSubpagecontext(PagingContext subpagecontext) {
		this.subpagecontext = subpagecontext;
	}

	public ICommonDataAccessService getService() {
		return service;
	}

	public void setService(ICommonDataAccessService service) {
		this.service = service;
	}

	public TbsTvGrantpayplanMainDto getMainDto() {
		return mainDto;
	}

	public void setMainDto(TbsTvGrantpayplanMainDto mainDto) {
		this.mainDto = mainDto;
	}	
}

package com.cfcc.itfe.client.recbiz.commercialbankpaybackment;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpaySubDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanSubDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class PayreckBankBackSubBean implements IPageDataProvider {
	private PagingContext maintablepage ;
	private ICommonDataAccessService service ;
	private TbsTvBnkpayMainDto maindto;
	public PayreckBankBackSubBean(ICommonDataAccessService service,TbsTvBnkpayMainDto mainDto) {
		this.service = service;
		this.maindto = mainDto;
		this.maintablepage = new PagingContext(this);
	}
	public PageResponse retrieve(PageRequest request) {		
		try {
			if(null != maindto) {
				TbsTvBnkpaySubDto subDto = new TbsTvBnkpaySubDto();
				subDto.setIvousrlno(maindto.getIvousrlno());
				subDto.setSbookorgcode(maindto.getSbookorgcode());
				PageResponse response =  service.findRsByDtoWithWherePaging(subDto, request, " 1=1 ");
				maintablepage.setPage(response);
				return response;
			}
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return null;
	}
	
	public PagingContext getMaintablepage() {
		return maintablepage;
	}
	public void setMaintablepage(PagingContext maintablepage) {
		this.maintablepage = maintablepage;
	}
	public ICommonDataAccessService getService() {
		return service;
	}
	public void setService(ICommonDataAccessService service) {
		this.service = service;
	}
	public TbsTvBnkpayMainDto getMaindto() {
		return maindto;
	}
	public void setMaindto(TbsTvBnkpayMainDto maindto) {
		this.maindto = maindto;
	}
	
}

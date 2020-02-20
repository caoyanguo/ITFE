package com.cfcc.itfe.client.common.page;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.service.dataquery.directpayquery.IDirectPayService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class SubDirectPayBean implements IPageDataProvider {

	private IDirectPayService directPayService;
	private TvDirectpaymsgmainDto maindto;
	private PagingContext subtablepage;
	private String expfunccode;
	private String payamt;
	public SubDirectPayBean(IDirectPayService directPayService,
			TvDirectpaymsgmainDto maindto) {
		this.directPayService = directPayService;
		this.maindto = maindto;
		this.subtablepage = new PagingContext(this);
	}

	public PageResponse retrieve(PageRequest pageRequest) {
		try {
			PageResponse response = directPayService.findSubByPage(maindto,
					pageRequest,expfunccode,payamt);
			subtablepage.setPage(response);
			return response;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

	public TvDirectpaymsgmainDto getMaindto() {
		return maindto;
	}

	public void setMaindto(TvDirectpaymsgmainDto maindto) {
		this.maindto = maindto;
	}

	public PagingContext getSubtablepage() {
		return subtablepage;
	}

	public void setSubtablepage(PagingContext subtablepage) {
		this.subtablepage = subtablepage;
	}

	public IDirectPayService getDirectPayService() {
		return directPayService;
	}

	public void setDirectPayService(IDirectPayService directPayService) {
		this.directPayService = directPayService;
	}

	public String getExpfunccode() {
		return expfunccode;
	}

	public void setExpfunccode(String expfunccode) {
		this.expfunccode = expfunccode;
	}

	public String getPayamt() {
		return payamt;
	}

	public void setPayamt(String payamt) {
		this.payamt = payamt;
	}
	

}

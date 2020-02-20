package com.cfcc.itfe.client.common.page;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.service.dataquery.directpayquery.IDirectPayService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class MainDirectPayBean implements IPageDataProvider {
	
	private IDirectPayService directPayService;
	private TvDirectpaymsgmainDto maindto;
	private PagingContext maintablepage;
	private String expfunccode;
	private String payamt;
	public MainDirectPayBean(IDirectPayService directPayService,
			TvDirectpaymsgmainDto maindto) {
		this.directPayService = directPayService;
		this.maindto = maindto;
		this.maintablepage = new PagingContext(this);
	}

	public PageResponse retrieve(PageRequest pageRequest) {
		try {
			PageResponse response = directPayService.findMainByPage(
					maindto, pageRequest,expfunccode,payamt);
			maintablepage.setPage(response);
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

	public PagingContext getMaintablepage() {
		return maintablepage;
	}

	public void setMaintablepage(PagingContext maintablepage) {
		this.maintablepage = maintablepage;
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

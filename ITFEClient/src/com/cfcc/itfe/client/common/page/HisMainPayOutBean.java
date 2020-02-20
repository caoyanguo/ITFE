package com.cfcc.itfe.client.common.page;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.service.dataquery.payoutquery.IPayoutService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class HisMainPayOutBean implements IPageDataProvider {

	private IPayoutService payOutService;
	private HtvPayoutmsgmainDto maindto;
	private PagingContext maintablepage;
	private String expfunccode;
	private String payamt;
	public HisMainPayOutBean(IPayoutService payOutService, HtvPayoutmsgmainDto maindto) {
		this.payOutService = payOutService;
		this.maindto = maindto;
		this.maintablepage = new PagingContext(this);
	}

	public PageResponse retrieve(PageRequest pageRequest) {
		try {
			PageResponse response = payOutService.findMainByPageForHis(maindto, pageRequest, expfunccode, payamt);
			maintablepage.setPage(response);
			return response;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

	public HtvPayoutmsgmainDto getMaindto() {
		return maindto;
	}

	public void setMaindto(HtvPayoutmsgmainDto maindto) {
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
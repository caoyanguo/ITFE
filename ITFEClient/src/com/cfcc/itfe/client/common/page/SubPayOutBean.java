package com.cfcc.itfe.client.common.page;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.service.dataquery.payoutquery.IPayoutService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class SubPayOutBean implements IPageDataProvider {

	private IPayoutService payOutService;
	private TvPayoutmsgmainDto maindto;
	private PagingContext subtablepage;
	private String expfunccode;
	private String payamt;
	public SubPayOutBean(IPayoutService payOutService, TvPayoutmsgmainDto maindto) {
		this.payOutService = payOutService;
		this.maindto = maindto;
		this.subtablepage = new PagingContext(this);
	}

	public PageResponse retrieve(PageRequest pageRequest) {
		
		if(null == maindto || null == maindto.getSbizno()){
			return null;
		}
		
		try {
			PageResponse response = payOutService.findSubByPage(maindto, pageRequest, expfunccode, payamt);
			subtablepage.setPage(response);
			return response;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

	public TvPayoutmsgmainDto getMaindto() {
		return maindto;
	}

	public void setMaindto(TvPayoutmsgmainDto maindto) {
		this.maindto = maindto;
	}

	public PagingContext getSubtablepage() {
		return subtablepage;
	}

	public void setSubtablepage(PagingContext subtablepage) {
		this.subtablepage = subtablepage;
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

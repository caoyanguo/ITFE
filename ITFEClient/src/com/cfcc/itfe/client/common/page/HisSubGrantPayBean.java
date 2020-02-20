package com.cfcc.itfe.client.common.page;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.HtvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.service.dataquery.grantpayquery.IGrantPayService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class HisSubGrantPayBean implements IPageDataProvider {

	private IGrantPayService grantPayService;
	private HtvGrantpaymsgmainDto maindto;
	private String sstatus;
	private PagingContext subtablepage;
	private String expfunccode;
	private String payamt;
	public HisSubGrantPayBean(IGrantPayService grantPayService,
			HtvGrantpaymsgmainDto maindto,String sstatus) {
		this.grantPayService = grantPayService;
		this.maindto = maindto;
		this.sstatus = sstatus;
		this.subtablepage = new PagingContext(this);
	}

	public PageResponse retrieve(PageRequest pageRequest) {
		try {
			PageResponse response = grantPayService.findSubByPageForHis(maindto,
					sstatus,pageRequest, expfunccode, payamt);
			subtablepage.setPage(response);
			return response;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

	public HtvGrantpaymsgmainDto getMaindto() {
		return maindto;
	}

	public void setMaindto(HtvGrantpaymsgmainDto maindto) {
		this.maindto = maindto;
	}

	public PagingContext getSubtablepage() {
		return subtablepage;
	}

	public void setSubtablepage(PagingContext subtablepage) {
		this.subtablepage = subtablepage;
	}

	public String getSstatus() {
		return sstatus;
	}

	public void setSstatus(String sstatus) {
		this.sstatus = sstatus;
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

package com.cfcc.itfe.client.common.page;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.service.dataquery.commapplypayquery.ICommApplyPayQueryService;
import com.cfcc.itfe.service.dataquery.pbcpayquery.IPbcpayService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class SubCommApplyPayBean implements IPageDataProvider {
	
	
	public PagingContext getSubtablepage() {
		return subtablepage;
	}

	public void setSubtablepage(PagingContext subtablepage) {
		this.subtablepage = subtablepage;
	}

	public ICommApplyPayQueryService getCommApplyPayService() {
		return commApplyPayService;
	}

	public void setCommApplyPayService(ICommApplyPayQueryService commApplyPayService) {
		this.commApplyPayService = commApplyPayService;
	}

	public TvPayreckBankDto getMaindto() {
		return maindto;
	}

	public void setMaindto(TvPayreckBankDto maindto) {
		this.maindto = maindto;
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

	private ICommApplyPayQueryService commApplyPayService;
	private TvPayreckBankDto maindto;
	private PagingContext subtablepage;
	private String expfunccode;
	private String payamt;
	public SubCommApplyPayBean(ICommApplyPayQueryService commApplyPayService, TvPayreckBankDto maindto){
		this.commApplyPayService = commApplyPayService;
		this.maindto = maindto;
		this.subtablepage = new PagingContext(this);
	}

	public PageResponse retrieve(PageRequest request) {
		if(null == maindto || null == maindto.getSbookorgcode() || null == maindto.getSpackno()){
			return null;
		}
		try {
			PageResponse response = commApplyPayService.findSubByPage(maindto, request, expfunccode, payamt);
			subtablepage.setPage(response);
			return response;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

}

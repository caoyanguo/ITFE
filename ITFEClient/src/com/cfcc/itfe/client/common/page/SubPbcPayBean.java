package com.cfcc.itfe.client.common.page;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.service.dataquery.pbcpayquery.IPbcpayService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class SubPbcPayBean implements IPageDataProvider {
	
	
	public PagingContext getSubtablepage() {
		return subtablepage;
	}

	public void setSubtablepage(PagingContext subtablepage) {
		this.subtablepage = subtablepage;
	}

	public IPbcpayService getPbcPayService() {
		return pbcPayService;
	}

	public void setPbcPayService(IPbcpayService pbcPayService) {
		this.pbcPayService = pbcPayService;
	}

	public TvPbcpayMainDto getMaindto() {
		return maindto;
	}

	public void setMaindto(TvPbcpayMainDto maindto) {
		this.maindto = maindto;
	}

	private IPbcpayService pbcPayService;
	private TvPbcpayMainDto maindto;
	private PagingContext subtablepage;
	
	public SubPbcPayBean(IPbcpayService pbcPayService, TvPbcpayMainDto maindto){
		this.pbcPayService = pbcPayService;
		this.maindto = maindto;
		this.subtablepage = new PagingContext(this);
	}

	public PageResponse retrieve(PageRequest request) {
		if(null == maindto || null == maindto.getIvousrlno()){
			return null;
		}
		try {
			PageResponse response = pbcPayService.findSubByPage(maindto, request);
			subtablepage.setPage(response);
			return response;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

}

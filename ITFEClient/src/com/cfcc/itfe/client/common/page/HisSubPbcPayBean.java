package com.cfcc.itfe.client.common.page;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.HtvPbcpayMainDto;
import com.cfcc.itfe.service.dataquery.pbcpayquery.IPbcpayService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class HisSubPbcPayBean implements IPageDataProvider {
	
	private IPbcpayService pbcPayService;
	private HtvPbcpayMainDto maindto;
	private PagingContext subtablepage;
	
	public HisSubPbcPayBean(IPbcpayService pbcPayService, HtvPbcpayMainDto maindto){
		this.pbcPayService = pbcPayService;
		this.maindto = maindto;
		this.subtablepage = new PagingContext(this);
	}

	public PageResponse retrieve(PageRequest request) {
		if(null == maindto || null == maindto.getIvousrlno()){
			return null;
		}
		try {
			PageResponse response = pbcPayService.findSubByPageForHis(maindto, request);
			subtablepage.setPage(response);
			return response;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

	public IPbcpayService getPbcPayService() {
		return pbcPayService;
	}

	public void setPbcPayService(IPbcpayService pbcPayService) {
		this.pbcPayService = pbcPayService;
	}

	public HtvPbcpayMainDto getMaindto() {
		return maindto;
	}

	public void setMaindto(HtvPbcpayMainDto maindto) {
		this.maindto = maindto;
	}

	public PagingContext getSubtablepage() {
		return subtablepage;
	}

	public void setSubtablepage(PagingContext subtablepage) {
		this.subtablepage = subtablepage;
	}

}

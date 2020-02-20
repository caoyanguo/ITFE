package com.cfcc.itfe.client.common.page;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.service.dataquery.pbcpayquery.IPbcpayService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;

public class MainPbcPayBean implements IPageDataProvider {
	
	private IPbcpayService pbcPayService;
	private TvPbcpayMainDto maindto;
	private PagingContext maintablepage;
	
	public MainPbcPayBean(IPbcpayService pbcPayService, TvPbcpayMainDto maindto){
		this.pbcPayService = pbcPayService;
		this.maindto = maindto;
		this.maintablepage = new PagingContext(this);
	}

	public TvPbcpayMainDto getMaindto() {
		return maindto;
	}

	public void setMaindto(TvPbcpayMainDto maindto) {
		this.maindto = maindto;
	}

	public PagingContext getMaintablepage() {
		return maintablepage;
	}

	public void setMaintablepage(PagingContext maintablepage) {
		this.maintablepage = maintablepage;
	}

	public PageResponse retrieve(PageRequest request) {
		try {
			PageResponse response = pbcPayService.findMainByPage(maindto, request);
			maintablepage.setPage(response);
			return response;
		} catch (ITFEBizException e) {
			e.printStackTrace();
			return null;
		}
		
	}

}

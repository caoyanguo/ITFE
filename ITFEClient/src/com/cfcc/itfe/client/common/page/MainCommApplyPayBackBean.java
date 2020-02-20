package com.cfcc.itfe.client.common.page;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.service.dataquery.commapplypaybackquery.ICommApplyPayBackQueryService;
import com.cfcc.itfe.service.dataquery.commapplypayquery.ICommApplyPayQueryService;
import com.cfcc.itfe.service.dataquery.pbcpayquery.IPbcpayService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;

public class MainCommApplyPayBackBean implements IPageDataProvider {
	
	private ICommApplyPayBackQueryService commApplyPayService;
	private TvPayreckBankBackDto maindto;
	private PagingContext maintablepage;
	private String expfunccode;
	private String payamt;
	public MainCommApplyPayBackBean(ICommApplyPayBackQueryService commApplyPayService, TvPayreckBankBackDto maindto){
		this.commApplyPayService = commApplyPayService;
		this.maindto = maindto;
		this.maintablepage = new PagingContext(this);
	}

	public TvPayreckBankBackDto getMaindto() {
		return maindto;
	}

	public void setMaindto(TvPayreckBankBackDto maindto) {
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
			PageResponse response = commApplyPayService.findMainByPage(maindto, request, expfunccode, payamt);
			maintablepage.setPage(response);
			return response;
		} catch (ITFEBizException e) {
			e.printStackTrace();
			return null;
		}
		
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

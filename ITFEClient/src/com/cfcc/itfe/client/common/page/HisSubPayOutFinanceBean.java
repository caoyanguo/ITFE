package com.cfcc.itfe.client.common.page;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.HtvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.service.dataquery.payoutfinancequery.IPayOutFinanceQueryService;
import com.cfcc.itfe.service.dataquery.payoutquery.IPayoutService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
public class HisSubPayOutFinanceBean implements IPageDataProvider {
	private IPayOutFinanceQueryService payOutFinanceService;
	private HtvPayoutfinanceMainDto maindto;
	private PagingContext subtablepage;

	public HisSubPayOutFinanceBean(IPayOutFinanceQueryService payOutFinanceService, HtvPayoutfinanceMainDto maindto) {
		this.payOutFinanceService = payOutFinanceService;
		this.maindto = maindto;
		this.subtablepage = new PagingContext(this);
	}

	public PageResponse retrieve(PageRequest pageRequest) {
		
		if(null == maindto || null == maindto.getIvousrlno()){
			return null;
		}
		
		try {
			PageResponse response = payOutFinanceService.findSubByPageForHis(maindto, pageRequest);
			subtablepage.setPage(response);
			return response;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

	public HtvPayoutfinanceMainDto getMaindto() {
		return maindto;
	}

	public void setMaindto(HtvPayoutfinanceMainDto maindto) {
		this.maindto = maindto;
	}

	public PagingContext getSubtablepage() {
		return subtablepage;
	}

	public void setSubtablepage(PagingContext subtablepage) {
		this.subtablepage = subtablepage;
	}

}

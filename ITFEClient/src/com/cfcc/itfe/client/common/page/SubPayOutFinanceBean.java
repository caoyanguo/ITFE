package com.cfcc.itfe.client.common.page;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.service.dataquery.payoutfinancequery.IPayOutFinanceQueryService;
import com.cfcc.itfe.service.dataquery.payoutquery.IPayoutService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
public class SubPayOutFinanceBean  implements IPageDataProvider {
	private IPayOutFinanceQueryService payOutFinanceService;
	private TvPayoutfinanceMainDto maindto;
	private PagingContext subtablepage;

	public SubPayOutFinanceBean(IPayOutFinanceQueryService payOutFinanceService, TvPayoutfinanceMainDto maindto) {
		this.payOutFinanceService = payOutFinanceService;
		this.maindto = maindto;
		this.subtablepage = new PagingContext(this);
	}

	public PageResponse retrieve(PageRequest pageRequest) {
		
		if(null == maindto || null == maindto.getIvousrlno()){
			return null;
		}
		
		try {
			PageResponse response = payOutFinanceService.findSubByPage(maindto, pageRequest);
			subtablepage.setPage(response);
			return response;
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

	public TvPayoutfinanceMainDto getMaindto() {
		return maindto;
	}

	public void setMaindto(TvPayoutfinanceMainDto maindto) {
		this.maindto = maindto;
	}

	public PagingContext getSubtablepage() {
		return subtablepage;
	}

	public void setSubtablepage(PagingContext subtablepage) {
		this.subtablepage = subtablepage;
	}
}

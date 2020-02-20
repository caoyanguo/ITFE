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
public class MainPayOutFinanceBean implements IPageDataProvider {
	private IPayOutFinanceQueryService payOutFinanceService;
	private TvPayoutfinanceMainDto maindto;
	private PagingContext maintablepage;

	public MainPayOutFinanceBean(IPayOutFinanceQueryService payOutFinanceService, TvPayoutfinanceMainDto maindto) {
		this.payOutFinanceService = payOutFinanceService;
		this.maindto = maindto;
		this.maintablepage = new PagingContext(this);
	}

	public PageResponse retrieve(PageRequest pageRequest) {
		try {
			PageResponse response = payOutFinanceService.findMainByPage(maindto, pageRequest);
			maintablepage.setPage(response);
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

	public PagingContext getMaintablepage() {
		return maintablepage;
	}

	public void setMaintablepage(PagingContext maintablepage) {
		this.maintablepage = maintablepage;
	}
}

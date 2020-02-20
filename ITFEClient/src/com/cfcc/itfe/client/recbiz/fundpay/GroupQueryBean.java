package com.cfcc.itfe.client.recbiz.fundpay;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GroupQueryBean  implements IPageDataProvider {
	private static Log log = LogFactory.getLog(GroupQueryBean.class);
	private PagingContext groupDetailPage ;
	private ICommonDataAccessService service ;
	private TbsTvPayoutDto payout ;	

	public GroupQueryBean(ICommonDataAccessService service, TbsTvPayoutDto payout) {
		super();
		this.groupDetailPage = new PagingContext(this);
		this.service = service;
		this.payout = payout;
	}

	public PageResponse retrieve(PageRequest request) {
		try {
			if(null != payout) {
				PageResponse response = service.findRsByDtoWithWherePaging(payout, request, " 1=1 ");
				if(null == response || response.getTotalCount() == 0) {
					groupDetailPage.setPage(response);
					return null;
				}
				groupDetailPage.setPage(response);
				return response;
			}			
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return null;
	}
	
	public PagingContext getGroupDetailPage() {
		return groupDetailPage;
	}
	public void setGroupDetailPage(PagingContext groupDetailPage) {
		this.groupDetailPage = groupDetailPage;
	}
	public ICommonDataAccessService getService() {
		return service;
	}
	public void setService(ICommonDataAccessService service) {
		this.service = service;
	}
	public TbsTvPayoutDto getPayout() {
		return payout;
	}
	public void setPayout(TbsTvPayoutDto payout) {
		this.payout = payout;
	}
}

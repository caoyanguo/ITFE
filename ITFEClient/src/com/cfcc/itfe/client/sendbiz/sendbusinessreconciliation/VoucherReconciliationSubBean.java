package com.cfcc.itfe.client.sendbiz.sendbusinessreconciliation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.recbiz.voucherload.IVoucherLoadService;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.BasicModel;
import com.cfcc.jaf.rcp.util.MessageDialog;

public class VoucherReconciliationSubBean extends BasicModel implements
		IPageDataProvider {

	private TvVoucherinfoDto searchDto;
	public PagingContext pagingContext;
	private ICommonDataAccessService service = (ICommonDataAccessService) getService(ICommonDataAccessService.class);
	protected IVoucherLoadService voucherLoadService = (IVoucherLoadService)getService(IVoucherLoadService.class);
	public VoucherReconciliationSubBean() {
		super();
		pagingContext = new PagingContext(this);
	}
	
	/**
	 * ²éÕÒ×Ó±í
	 * @param o
	 * @return
	 */
	public String searchDtoList(Object o){
		if(o instanceof TvVoucherinfoDto){
			searchDto = (TvVoucherinfoDto)o;
		}
		PageRequest request = new PageRequest();
		PageResponse response = retrieve(request);
		if (response == null || response.getTotalCount() == 0) {
			return null;
		} else {
			pagingContext.setPage(response);
		}
		return "";
	}

	public PageResponse retrieve(PageRequest request) {
		PageResponse response = new PageResponse(request);
		try {
			String wheresql = " 1=1 ";
			Map map = new HashMap();
			map.put("searchdto", searchDto);
			map = voucherLoadService.queryVoucherReturnStatus(map);
			List voucherlist = null;
			if(map!=null)
			{
				voucherlist = new ArrayList();
				for(Object key:map.keySet())
				{
					voucherlist.add(map.get(key));
				}
				response.setTotalCount(voucherlist.size());
				response.getData().clear();
				if(voucherlist.size()>=((request.getStartPosition()-1)+request.getPageSize()))
					response.setData(voucherlist.subList(request.getStartPosition()-1, (request.getStartPosition()-1)+request.getPageSize()));
				else
					response.setData(voucherlist.subList(request.getStartPosition()-1, voucherlist.size()));
			}
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			this.editor.fireModelChanged();
			return null;
		}
		return response;
	}

	public Properties getMESSAGE_PROPERTIES() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMessage(String _direction, String _msgkey) {
		// TODO Auto-generated method stub
		return null;
	}

	public TvVoucherinfoDto getSearchDto() {
		return searchDto;
	}

	public void setSearchDto(TvVoucherinfoDto searchDto) {
		this.searchDto = searchDto;
	}

	public PagingContext getPagingContext() {
		return pagingContext;
	}

	public void setPagingContext(PagingContext pagingContext) {
		this.pagingContext = pagingContext;
	}

}

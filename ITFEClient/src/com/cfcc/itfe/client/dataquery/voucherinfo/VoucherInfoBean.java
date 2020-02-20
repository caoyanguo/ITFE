package com.cfcc.itfe.client.dataquery.voucherinfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author whj
 * @time   13-09-04 11:20:28
 * 子系统: DataQuery
 * 模块:VoucherInfo
 * 组件:VoucherInfo
 */
public class VoucherInfoBean extends AbstractVoucherInfoBean implements IPageDataProvider {
	private PagingContext pagingcontext = new PagingContext(this);
	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
    private static Log log = LogFactory.getLog(VoucherInfoBean.class);
    public VoucherInfoBean() {
      super();
      searchDto = new TvVoucherinfoDto();
      detailDto = new TvVoucherinfoDto();
      selectedDto=new TvVoucherinfoDto();
                  
    }
    
	/**
	 * Direction: 返回
	 * ename: goback
	 * 引用方法: 
	 * viewers: 对账结果查询
	 * messages: 
	 */
    public String goback(Object o){
          return super.goback(o);
    }
    /**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		selectedDto = (TvVoucherinfoDto) o;
		return super.singleSelect(o);
	}
	  /**
	 * Direction: shu ename: singleSelect 引用方法: viewers: * messages:
	 */
	 public String doubleTodetail(Object o){
		 selectedDto = (TvVoucherinfoDto) o;
		 return super.doubleTodetail(o);
	    }
	 /**
	  * 跳转至明细界面
	  */
	 public String goDetail(Object o){
	        
	        return super.goDetail(o);
	    }
	/**
	 * Direction: 查询
	 * ename: queryInfo
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryInfo(Object o){
    	PageRequest request = new PageRequest();
    	PageResponse pageResponse = retrieve(request);
    	if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " 查询无记录！");
			pagingcontext.setPage(new PageResponse());
			this.editor.fireModelChanged(ModelChangedEvent.SAFE_REFRESH_TABLE_EVENT);
			return null;
		}
    	pagingcontext.setPage(pageResponse);
    	this.editor.fireModelChanged();
          return super.queryInfo(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest request) {
    		try {
				return	voucherInfoService.searchResult(searchDto, dzType, request);
			} catch (ITFEBizException e) {
				log.error("查询对账信息异常:"+e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}
	}

}
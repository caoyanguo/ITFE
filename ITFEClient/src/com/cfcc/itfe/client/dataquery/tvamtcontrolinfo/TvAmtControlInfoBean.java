package com.cfcc.itfe.client.dataquery.tvamtcontrolinfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvAmtControlInfoDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author win7
 * @time   13-09-17 02:55:26
 * 子系统: DataQuery
 * 模块:TvAmtControlInfo
 * 组件:TvAmtControlInfo
 */
public class TvAmtControlInfoBean extends AbstractTvAmtControlInfoBean implements IPageDataProvider {
	private PagingContext pagingcontext = new PagingContext(this);
	public PagingContext getPagingcontext() {
		return pagingcontext;
	}

	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
	}
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
    private static Log log = LogFactory.getLog(TvAmtControlInfoBean.class);
    public TvAmtControlInfoBean() {
      super();
      selectedDto = new TvAmtControlInfoDto();
      searchDto = new TvAmtControlInfoDto();
                  
    }
    
	/**
	 * Direction: 单击
	 * ename: singleclick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleclick(Object o){
    	selectedDto = (TvAmtControlInfoDto) o;
          return super.singleclick(o);
    }

	/**
	 * Direction: 双击
	 * ename: doubleclick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleclick(Object o){
    	selectedDto = (TvAmtControlInfoDto) o;
          return super.doubleclick(o);
    }

	/**
	 * Direction: 返回
	 * ename: backmain
	 * 引用方法: 
	 * viewers: 额度控制信息查询
	 * messages: 
	 */
    public String backmain(Object o){
          return super.backmain(o);
    }
    
    /**
	 * Direction: 跳转至详细界面
	 * ename: godetailview
	 * 引用方法: 
	 * viewers: 明细信息
	 * messages: 
	 */
    public String godetailview(Object o){
        
    	return super.godetailview(o);
    }
    
    public String searchinfo(Object o){
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
          return super.searchinfo(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest request) {
    	try {
			return	commonDataAccessService.findRsByDtoWithWherePaging(searchDto, request, " 1=1 and N_AMT > 0 ");
		} catch (ITFEBizException e) {
			log.error("查询对账信息异常:"+e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
	}

}
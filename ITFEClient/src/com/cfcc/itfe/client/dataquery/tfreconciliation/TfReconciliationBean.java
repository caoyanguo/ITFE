package com.cfcc.itfe.client.dataquery.tfreconciliation;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.dataquery.tfreconciliation.ITfReconciliationService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfReconciliationDto;

/**
 * codecomment: 
 * @author Administrator
 * @time   17-04-04 10:47:03
 * 子系统: DataQuery
 * 模块:TfReconciliation
 * 组件:TfReconciliation
 */
public class TfReconciliationBean extends AbstractTfReconciliationBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TfReconciliationBean.class);
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    private ITFELoginInfo logInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    
    public TfReconciliationBean() {
      super();
      searchDto = new TfReconciliationDto();
      searchDto.setSchkdate(TimeFacade.getCurrentStringTime());
      searchDto.setStrecode(logInfo.getSorgcode().substring(0, 10));
      pagingcontext = new PagingContext(this);
      reportPath = "/com/cfcc/itfe/client/ireport/itfe_TF_RECONCILIATION.jasper";
      rsList = new ArrayList();
      paramMap = new HashMap();
      printDto = new TfReconciliationDto();
      reportPathDetail = "/com/cfcc/itfe/client/ireport/itfe_TF_RECONCILIATION_detail.jasper";
      rsListDetail = new ArrayList();
      paramMapDetail = new HashMap();
                  
    }
    
	/**
	 * Direction: 查询到结果界面
	 * ename: queryResult
	 * 引用方法: 
	 * viewers: 查询结果界面
	 * messages: 
	 */
    public String queryResult(Object o){
    	printDto = new TfReconciliationDto();
    	PageRequest pageRequest = new PageRequest();
		try {
			PageResponse p = this.retrieve(pageRequest);
			if (p == null || p.getPageRowCount() == 0) {
				MessageDialog.openMessageDialog(null,"查询数据不存在!");
				return null;
			}else{
				pagingcontext.setPage(p);
			}
		} catch (Exception e) {
			log.error("查询数据错误！", e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
        return super.queryResult(o);
    }

	/**
	 * Direction: 返回到查询界面
	 * ename: backQuery
	 * 引用方法: 
	 * viewers: 查询界面
	 * messages: 
	 */
    public String backQuery(Object o){
          return super.backQuery(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	printDto = (TfReconciliationDto)o;
    	return super.singleSelect(o);
    }

	/**
	 * Direction: 报表
	 * ename: toReport
	 * 引用方法: 
	 * viewers: 报表
	 * messages: 
	 */
    public String toReport(Object o){
    	TfReconciliationDto reconciliationDto = null;
    	try {
			rsList = commonDataAccessService.findRsByDtoWithWhere(searchDto, " and 1=1 ");
			for(int i = 0;i<rsList.size();i++){
				reconciliationDto = (TfReconciliationDto)rsList.get(i);
				String svoutype = reconciliationDto.getSpayoutvoutype();
				if(svoutype != null && !svoutype.trim().equals("")){
					if(svoutype.equals("1")){
						reconciliationDto.setSpayoutvoutype("实拨");
					}else if(svoutype.equals("2")){
						reconciliationDto.setSpayoutvoutype("退库");
					}else if(svoutype.equals("3")){
						reconciliationDto.setSpayoutvoutype("商行划款");
					}else if(svoutype.equals("4")){
						reconciliationDto.setSpayoutvoutype("资金清算回执");
					}else if(svoutype.equals("5")){
						reconciliationDto.setSpayoutvoutype("退款通知报文");
					}else if(svoutype.equals("6")){
						reconciliationDto.setSpayoutvoutype("其他");
					}
				}
			}
		} catch (ITFEBizException e) {
			log.error("打开报表 异常",e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}
		return super.toReport(o);
    }

	/**
	 * Direction: 报文明细
	 * ename: detail
	 * 引用方法: 
	 * viewers: 报文明细
	 * messages: 
	 */
    public String detail(Object o){
    	if (printDto == null || printDto.getSid() == null) {
    		MessageDialog.openMessageDialog(null, "请单击选中一条记录查询详细信息！");
			return null;
		}
    	return super.detail(o);
    }

	/**
	 * Direction: 打印明细
	 * ename: printDetail
	 * 引用方法: 
	 * viewers: 明细报表
	 * messages: 
	 */
    public String printDetail(Object o){
    	rsListDetail.clear();
    	String svoutype = printDto.getSpayoutvoutype();
    	if(svoutype != null && !svoutype.trim().equals("")){
	    	if(svoutype.equals("1")){
	    		printDto.setSpayoutvoutype("实拨");
			}else if(svoutype.equals("2")){
				printDto.setSpayoutvoutype("退库");
			}else if(svoutype.equals("3")){
				printDto.setSpayoutvoutype("商行划款");
			}else if(svoutype.equals("4")){
				printDto.setSpayoutvoutype("资金清算回执");
			}else if(svoutype.equals("5")){
				printDto.setSpayoutvoutype("退款通知报文");
			}else if(svoutype.equals("6")){
				printDto.setSpayoutvoutype("其他");
			}
    	}
    	rsListDetail.add(printDto);
    	return super.printDetail(o);
    }

	/**
	 * Direction: 返回明细
	 * ename: backToDetail
	 * 引用方法: 
	 * viewers: 报文明细
	 * messages: 
	 */
    public String backToDetail(Object o){
    	return super.backToDetail(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest request) {
    	PageResponse response = new PageResponse(request);
		try {
			String wheresql = " 1=1 ";
			response = commonDataAccessService.findRsByDtoWithWherePaging(searchDto, request, wheresql);
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			this.editor.fireModelChanged();
		}
		return response;
	}

}
package com.cfcc.itfe.client.dataquery.tfrefundnotice;

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
import com.cfcc.itfe.service.dataquery.tfrefundnotice.ITfRefundNoticeService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TfRefundNoticeDto;

/**
 * codecomment: 
 * @author Administrator
 * @time   17-03-27 20:07:23
 * 子系统: DataQuery
 * 模块:TfRefundNotice
 * 组件:TfRefundNotice
 */
public class TfRefundNoticeBean extends AbstractTfRefundNoticeBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TfRefundNoticeBean.class);
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    private ITFELoginInfo logInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    List enumList = null;
    String realValue = null;
    
    public TfRefundNoticeBean() {
		super();
		searchDto = new TfRefundNoticeDto();
		searchDto.setSentrustdate(TimeFacade.getCurrentStringTime());
		searchDto.setStrecode(logInfo.getSorgcode().substring(0, 10));
		pagingcontext = new PagingContext(this);
		reportPath = "/com/cfcc/itfe/client/ireport/itfe_TF_REFUND_NOTICE.jasper";
		rsList = new ArrayList();
		paramMap = new HashMap();
		printDto = new TfRefundNoticeDto();
		reportPathDetail = "/com/cfcc/itfe/client/ireport/itfe_TF_REFUND_NOTICE_detail.jasper";
		rsListDetail = new ArrayList();
		paramMapDetail = new HashMap();
      
		enumList = new ArrayList<TdEnumvalueDto>();
		TdEnumvalueDto valuedtoa = new TdEnumvalueDto();
		valuedtoa.setStypecode("实拨");
		valuedtoa.setSvalue("1");
		enumList.add(valuedtoa);
		
		TdEnumvalueDto valuedtob = new TdEnumvalueDto();
		valuedtob.setStypecode("退库");
		valuedtob.setSvalue("2");
		enumList.add(valuedtob);
		
		TdEnumvalueDto valuedtoc = new TdEnumvalueDto();
		valuedtoc.setStypecode("商行划款");
		valuedtoc.setSvalue("3");
		enumList.add(valuedtoc);
        
		TdEnumvalueDto valuedtod = new TdEnumvalueDto();
		valuedtod.setStypecode("其他");
		valuedtod.setSvalue("4");
		enumList.add(valuedtod);
		
		realValue = "1";
		searchDto.setSpayoutvoutype(realValue);
    }
    
	/**
	 * Direction: 查询到结果界面
	 * ename: queryResult
	 * 引用方法: 
	 * viewers: 查询结果界面
	 * messages: 
	 */
    public String queryResult(Object o){
    	printDto = new TfRefundNoticeDto();
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
    	printDto = (TfRefundNoticeDto)o;
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
    	try {
			rsList = commonDataAccessService.findRsByDtoWithWhere(searchDto, " and 1=1 ");
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
			searchDto.setSpayoutvoutype(realValue);
			response = commonDataAccessService.findRsByDtoWithWherePaging(searchDto, request, wheresql);
		} catch (Exception e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			this.editor.fireModelChanged();
		}
		return response;
	}

	public List getEnumList() {
		return enumList;
	}

	public void setEnumList(List enumList) {
		this.enumList = enumList;
	}

	public String getRealValue() {
		return realValue;
	}

	public void setRealValue(String realValue) {
		this.realValue = realValue;
	}

}
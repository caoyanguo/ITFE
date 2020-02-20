package com.cfcc.itfe.client.dataquery.voucherhissearch;

import itferesourcepackage.AllVoucherTypeEnumFactory;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeVoucherHisOcx;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.pk.HtvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;

/**
 * codecomment: 
 * @author db2itfe
 * @time   13-11-19 16:25:46
 * 子系统: DataQuery
 * 模块:voucherHisSearch
 * 组件:VoucherHisSearch
 */
public class VoucherHisSearchBean extends AbstractVoucherHisSearchBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(VoucherHisSearchBean.class);
    List<HtvVoucherinfoDto> checkList=null;   
	
	//用户登录信息
	private ITFELoginInfo loginInfo;
	private String voucherType=null;
	private List<Mapper> voucherTypeList=null;
    public VoucherHisSearchBean() {
      super();
      dto = new HtvVoucherinfoDto();
      pagingcontext = new PagingContext(this);
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      checkList=new ArrayList();
      String dateStr = TimeFacade.getCurrentStringTime();
      dto.setScreatdate(dateStr);
      dto.setSstyear(dateStr.substring(0, 4));
      dto.setSorgcode(loginInfo.getSorgcode());
      voucherTypeList = new AllVoucherTypeEnumFactory().getEnums(null);            
    }
    

	/**
	 * Direction: 查询
	 * ename: searchVoucherHis
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String searchVoucherHis(Object o){
    	if (dto.getSvtcode() == null || dto.getSvtcode().equals("")) {
			MessageDialog.openMessageDialog(null, "请先选择凭证类型！");
			return "";
		}
		refreshTable();
		return super.searchVoucherHis(o);
    }
    public void refreshTable(){
    	init();
		checkList.clear();
    }
    private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录！");
		}
		pagingcontext.setPage(pageResponse);
	}
	/**
	 * Direction: 电子凭证还原展示
	 * ename: voucherHisView
	 * 引用方法: 
	 * viewers: 电子凭证查看界面
	 * messages: 
	 */
    public String voucherHisView(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要查看的记录！");
    		return "";
    	}
    	try{
        	ActiveXCompositeVoucherHisOcx.init(0);

    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
    		log.error(e);
    		Exception e1=new Exception("凭证查看操作出现异常！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
    	}catch(Error e){
    		log.error(e);
    		Exception e1=new Exception("凭证查看操作OCX调用出现系统错误，请联系开发人员！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
    	}
          return super.voucherHisView(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	PageResponse page = null;
    	try {
    		page =  commonDataAccessService.findRsByDtoPaging(dto,
					pageRequest, "1=1", "S_RECVTIME DESC");
    	
    		return page;
    		
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}	catch (Throwable e) {	
			log.error(e);
			Exception e1=new Exception("查询数据异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}
		return super.retrieve(pageRequest);
	}

	public List<HtvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<HtvVoucherinfoDto> checkList) {
		this.checkList = checkList;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
		this.voucherType = voucherType;
		this.dto.setSvtcode(voucherType);
		//动态显示
		if (MsgConstant.VOUCHER_NO_5207.equals(voucherType) || MsgConstant.VOUCHER_NO_5209.equals(voucherType)) {
			List contreaNames1 = new ArrayList();
			contreaNames1.add("凭证查询一览表代理行");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
			List contreaNames = new ArrayList();
			contreaNames.add("凭证查询一览表出票单位");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
		}else{
			List contreaNames = new ArrayList();
			contreaNames.add("凭证查询一览表出票单位");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("凭证查询一览表代理行");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
		}
		pagingcontext.setPage(new PageResponse());
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged();
	}

	public List<Mapper> getVoucherTypeList() {
		return voucherTypeList;
	}

	public void setVoucherTypeList(List<Mapper> voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}

	public int queryVoucherJOintCount(HtvVoucherinfoDto vDto) {
		int count=0;
    	TsVouchercommitautoDto tDto=new TsVouchercommitautoDto();
    	tDto.setSorgcode(vDto.getSorgcode());
    	tDto.setStrecode(vDto.getStrecode());
    	tDto.setSvtcode(vDto.getSvtcode());
    	try {
			List<TsVouchercommitautoDto> list= (List) commonDataAccessService.findRsByDto(tDto);
			if(list==null||list.size()==0)
				return -1;
			tDto=list.get(0);
			if(tDto.getSjointcount()==null){
				return -1;
			}			
		} catch (ITFEBizException e) {
			log.error(e);
			return -2;
		}catch(Exception e){
			log.error(e);
			return -1;
		}
		return tDto.getSjointcount();
	}


	public String getOcxVoucherServerURL() {
		String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("获取OCX控件URL地址操作出现异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
	}
	
    /**
     * 获取签章服务地址
     * @return
     */
    public String getOCXStampServerURL(){
    	String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXStampServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("获取OCX控件签章服务URL地址操作出现异常！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
    }
	
	public HtvVoucherinfoDto getDto(HtvVoucherinfoDto dto) throws ITFEBizException{
		HtvVoucherinfoPK pk=new HtvVoucherinfoPK();
    	pk.setSdealno(dto.getSdealno());    	   	
    	return (HtvVoucherinfoDto) commonDataAccessService.find(pk);
    }
}
package com.cfcc.itfe.client.dataquery.paybankrefundquery;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.HtfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * 功能：收款银行退款通知查询
 * @author hejianrong
 * @time   14-11-16 14:03:08
 * 子系统: DataQuery
 * 模块:directPaymentQuery
 * 组件:DirectPaymentQuery
 */
public class PayBankRefundQueryBean extends AbstractPayBankRefundQueryBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(PayBankRefundQueryBean.class);
   
	/** 属性列表 */
    private ITFELoginInfo loginfo;
    private TfPaybankRefundmainDto finddto;
	private String selectedtable;
	private List tableMapperList;
	private IDto idto;

    public PayBankRefundQueryBean() {
      super();         
      initTableMapperList();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
      finddto=new TfPaybankRefundmainDto();
      finddto.setSorgcode(loginfo.getSorgcode());
      finddto.setScommitdate(TimeFacade.getCurrentStringTime());
      selectedtable = "0";
      pagingcontextMain = new PagingContext(this);
      pagingcontextSub = new PagingContext(this);
      pagingcontextMainHis = new PagingContext(this);
      pagingcontextSubHis = new PagingContext(this);      
    }
    
	/**
	 * Direction: 查询列表事件
	 * ename: searchList
	 * 引用方法: 
	 * viewers: 直接支付信息列表
	 * messages: 
	 */
    public String searchList(Object o){
    	if(StringUtils.isBlank(selectedtable)){
    		MessageDialog.openMessageDialog(null, "请选择要查询的表!");
    		return "";
    	}
		if(selectedtable.equals("0")){
			idto=finddto;
		}else{
			idto=new HtfPaybankRefundmainDto();
			try {
				CommonUtil.copyProperties(idto, finddto);
			} catch (IllegalAccessException e) {
				log.error(e);
				MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),
						new Exception("历史表和当前表表结构不一致!",e));
				return "";
			} catch (InvocationTargetException e) {
				log.error(e);
				MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),
						new Exception("历史表和当前表表结构不一致!",e));
				return "";
			}
		}
		if(!init())
			return "";
		if(selectedtable.equals("1"))
			return "收款银行退款通知信息列表(历史表)";
		return super.searchList(o);
	}
    
    public boolean init(){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录！");
			return false;
		}
		if(idto instanceof TfPaybankRefundmainDto){
			pagingcontextMain.setPage(pageResponse);
		}else if(idto instanceof HtfPaybankRefundmainDto){
			pagingcontextMainHis.setPage(pageResponse);
		}else if(idto instanceof TfPaybankRefundsubDto){
			pagingcontextSub.setPage(pageResponse);
		}else if(idto instanceof HtfPaybankRefundsubDto){
			pagingcontextSubHis.setPage(pageResponse);
		}		
		editor.fireModelChanged();
		return true;
    }
    
	/**
	 * Direction: 返回查询界面
	 * ename: rebackSearch
	 * 引用方法: 
	 * viewers: 直接支付查询界面
	 * messages: 
	 */
    public String rebackSearch(Object o){
          return super.rebackSearch(o);
    }

	/**
	 * Direction: 主信息双击事件
	 * ename: doubleclickMain
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleclickMain(Object o){
    	if(o instanceof TfPaybankRefundmainDto){
    		idto=new TfPaybankRefundsubDto();   		
    		((TfPaybankRefundsubDto)idto).setIvousrlno(((TfPaybankRefundmainDto) o).getIvousrlno());
    	}else if(o instanceof HtfPaybankRefundmainDto){
    		idto=new HtfPaybankRefundsubDto();   		
    		((HtfPaybankRefundsubDto)idto).setIvousrlno(((HtfPaybankRefundmainDto) o).getIvousrlno());
    	}
    	init(); 
        return super.doubleclickMain(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	try {
    		return  commonDataAccessService.findRsByDtoPaging(idto,
					pageRequest, "1=1", " I_VOUSRLNO DESC");   	
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}	catch (Throwable e) {	
			log.error(e);
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),
					new Exception("查询数据异常！",e));
		}
		return super.retrieve(pageRequest);
	}
    
    public void initTableMapperList(){
    	Mapper m1=new Mapper("0","当前表");
    	Mapper m2=new Mapper("1","历史表");
    	tableMapperList=new ArrayList<Mapper>();
    	tableMapperList.add(m1);
    	tableMapperList.add(m2);    	
    }

	public TfPaybankRefundmainDto getFinddto() {
		return finddto;
	}

	public void setFinddto(TfPaybankRefundmainDto finddto) {
		this.finddto = finddto;
	}

	public String getSelectedtable() {
		return selectedtable;
	}

	public void setSelectedtable(String selectedtable) {
		this.selectedtable = selectedtable;
	}

	public List getTableMapperList() {
		return tableMapperList;
	}

	public void setTableMapperList(List tableMapperList) {
		this.tableMapperList = tableMapperList;
	}

}
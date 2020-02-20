package com.cfcc.itfe.client.sendbiz.stockaccountvoucher;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeVoucherStockAccountOcx;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author db2admin
 * @time   14-03-31 14:24:20
 * 子系统: SendBiz
 * 模块:stockAccountVoucher
 * 组件:StockAccountVoucherForMonth
 */
public class StockAccountVoucherForMonthBean extends AbstractStockAccountVoucherForMonthBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(StockAccountVoucherForMonthBean.class);
    private ITFELoginInfo loginfo;
    List<TvVoucherinfoDto> checkList=null;
    public StockAccountVoucherForMonthBean() {
      super();
      dto = new TvVoucherinfoDto();
      dto.setScreatdate(TimeFacade.getCurrentStringTime());
      dto.setScheckdate(TimeFacade.getCurrentStringTime().substring(0, 6));
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      pagingcontext = new PagingContext(this);
      checkList=new ArrayList<TvVoucherinfoDto>();
    }
    
    
    /**
     * 获取OCX控件url
     * @return
     */
    public String getOcxVoucherServerURL(){
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
    
	/**
	 * Direction: 生成凭证并发送
	 * ename: createVoucherAndSend
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    @SuppressWarnings("unchecked")
	public String createVoucherAndSend(Object o){
    	if(dto.getScheckdate()==null||dto.getScheckdate().equals("")){
    		MessageDialog.openMessageDialog(null, "请先选择对账月份！");
    		return "";
    	}
    	
    	List<TvVoucherinfoDto> list=new ArrayList<TvVoucherinfoDto>();
    	int count=0;
		List<TsTreasuryDto> tList=new ArrayList<TsTreasuryDto>();
		TvVoucherinfoDto vDto=new TvVoucherinfoDto();
		try {
			TsTreasuryDto tDto=new TsTreasuryDto();
			tDto.setSorgcode(loginfo.getSorgcode());
			if(dto.getStrecode()==null||dto.getStrecode().equals("")){				
				tList=commonDataAccessService.findRsByDto(tDto);
			}else{
				tDto.setStrecode(dto.getStrecode());				
				tList.add(tDto);
			}			
			for(TsTreasuryDto tsDto:tList){
				vDto=new TvVoucherinfoDto();
		    	vDto.setSorgcode(loginfo.getSorgcode());
				vDto.setScreatdate(dto.getScreatdate());
				vDto.setScheckdate(dto.getScheckdate());
				vDto.setSvtcode(MsgConstant.VOUCHER_NO_3552);
				vDto.setStrecode(tsDto.getStrecode());
				list.add(vDto);
			}
		}catch(java.lang.NullPointerException e){
			MessageDialog.openMessageDialog(null, "国库主体参数未维护！");
			return "";
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
			return "";
		} catch(Exception e){
			log.error(e);
			Exception e1=new Exception("生成凭证操作出现异常！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
			return "";
		}
    	List resultList=null;
    	try {
    		if(list.size()>0){
    			resultList=voucherLoadService.voucherGenerate(list);
    		}
    		count=Integer.parseInt(resultList.get(0)+"");
    		if(count==0){
    			MessageDialog.openMessageDialog(null, "当前日期下无报表数据！");
				return "";
    		}    		
    		if(count==-1){
    			MessageDialog.openMessageDialog(null, "国库："+resultList.get(1)+"对账月份："+dto.getScheckdate()+"下库款账户月度对账已生成凭证，不能重复生成凭证！");
        		refreshTable();
        		return "";
        	}								
		} catch (ITFEBizException e) {
			log.error(e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);			
			refreshTable();
			return "";
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
			return "";
		}catch(Exception e){
			log.error(e);		
			Exception e1=new Exception("生成凭证操作出现异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);			
			return "";
		}	
		MessageDialog.openMessageDialog(null, "报表凭证生成操作成功，成功条数为："+count+" ！");
		refreshTable();		
        return super.createVoucherAndSend(o);
    }

	/**
	 * Direction: 全选
	 * ename: selectAll
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    @SuppressWarnings("unchecked")
	public String selectAll(Object o){
    	if(checkList==null||checkList.size()==0){
         	checkList = new ArrayList<TvVoucherinfoDto>();
         	checkList.addAll(pagingcontext.getPage().getData());
         }else{
        	checkList.clear(); 
         }
         this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
         return super.selectAll(o);
    }

	/**
	 * Direction: 发送凭证电子凭证库
	 * ename: voucherSend
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherSend(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要发送电子凭证的记录！");
    		return "";
    	}
    	int count=0;
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行发送电子凭证操作吗？")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
			if(!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus().trim()))){
				MessageDialog.openMessageDialog(null, "请选择凭证状态为 \"处理成功\" 的记录！");
        		return "";
				}
	    	}
		
    	try {
    		count=voucherLoadService.voucherReturnSuccess(checkList);
    		MessageDialog.openMessageDialog(null, "发送电子凭证   "+checkList.size()+" 条，成功条数为："+count+" ！");
    		refreshTable();
		} catch (ITFEBizException e) {			
			log.error(e);	
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
			return "";
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("发送电子凭证库操作出现异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			
			return "";
		}
	
    	return super.voucherSend(o);
    }

	/**
	 * Direction: 查询
	 * ename: search
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String search(Object o){
    	refreshTable();
        return super.search(o);
    }
    
    public void refreshTable(){
    	init();
		checkList.clear();		
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
    }
    
    private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录，请先生成凭证！");
		}
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * Direction: 凭证查看
	 * ename: voucherView
	 * 引用方法: 
	 * viewers: 凭证查看界面
	 * messages: 
	 */
    public String voucherView(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要查看的记录！");
    		return "";
    	}
    	try{
    		ActiveXCompositeVoucherStockAccountOcx.init(0);
        	
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
			return "";
		}catch(Exception e){
    		log.error(e);
    		Exception e1=new Exception("凭证查看异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
    	}
        return super.voucherView(o);
    }
    
    
    /**
	 * Direction: 查询凭证打印次数
	 * ename: queryVoucherPrintCount
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String queryVoucherPrintCount(TvVoucherinfoDto vDto){
    	String err=null;
    	try {
			err=voucherLoadService.queryVoucherPrintCount(vDto);
			
		} catch (ITFEBizException e) {			
			log.error(e);
			return "查询异常";
		}
    	return err;
    }
    
    /**
	 * Direction: 查询凭证联数
	 * ename: queryVoucherPrintCount
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public int queryVoucherJOintCount(TvVoucherinfoDto vDto){
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
    
    public String getVoucherXMl(TvVoucherinfoDto vDto) throws ITFEBizException{
   	    return voucherLoadService.voucherStampXml(vDto);
    }
    
    public TvVoucherinfoDto getDto(TvVoucherinfoDto dto) throws ITFEBizException{
    	TvVoucherinfoPK pk=new TvVoucherinfoPK();
    	pk.setSdealno(dto.getSdealno());    	   	
    	return (TvVoucherinfoDto) commonDataAccessService.find(pk);
    }
    
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	PageResponse page = null;
    	StringBuffer wheresql=new StringBuffer();
    	wheresql.append(" 1=1 AND S_VTCODE = '"+MsgConstant.VOUCHER_NO_3552+"' ");
    	try {
    		page =  commonDataAccessService.findRsByDtoPaging(dto, pageRequest, wheresql.toString(), " TS_SYSUPDATE desc");
    	
    		return page;
    				
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
			
		}catch (Throwable e) {
			log.error(e);
			Exception e1=new Exception("凭证查询出现异常！",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			
		}
		return super.retrieve(pageRequest);
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		StockAccountVoucherForMonthBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public List<TvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TvVoucherinfoDto> checkList) {
		this.checkList = checkList;
	}

    
}
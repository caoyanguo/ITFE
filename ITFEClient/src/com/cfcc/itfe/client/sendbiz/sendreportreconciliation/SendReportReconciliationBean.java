package com.cfcc.itfe.client.sendbiz.sendreportreconciliation;

import itferesourcepackage.ReportBussReadReturnEnumFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeReconciliationOcx;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeReconciliationReturnOcx;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfReportDefrayMainDto;
import com.cfcc.itfe.persistence.dto.TfReportDefraySubDto;
import com.cfcc.itfe.persistence.dto.TfReportDepositMainDto;
import com.cfcc.itfe.persistence.dto.TfReportDepositSubDto;
import com.cfcc.itfe.persistence.dto.TfReportIncomeMainDto;
import com.cfcc.itfe.persistence.dto.TfReportIncomeSubDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment: 
 * @author zhangliang
 * @time   14-09-17 15:26:35
 * 子系统: SendBiz
 * 模块:sendReportReconciliation
 * 组件:SendReportReconciliation
 */
@SuppressWarnings("unchecked")
public class SendReportReconciliationBean extends AbstractSendReportReconciliationBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(SendReportReconciliationBean.class);
    private ReportIncomeSubBean incomeSubBean;
    private ReportDefraySubBean defraySubBean;
    private ReportDepositSubBean depositSubBean;
    private String strecode = null;
    private List taxtrelist = null;
    List<TvVoucherinfoDto> checkList=null;   
    List<TvVoucherinfoDto> returnList=null;
    private List acctList = null;
	//用户登录信息
	private ITFELoginInfo loginInfo;
	private String stamp;
	private List stampList=null;
	private String voucherType=null;
	private List voucherTypeList=null;
	private Map paramMap = null;
	private String ysjc;
	private String vouchertype=MsgConstant.VOUCHER_NO_3513;//凭证类型3511-收入报表对账，3512-支出报表对账，3513-库存账户对账
	private List recvDeptList = null;
    private String recvDept = null;
    private String month = null;
    private List monthlist = null;
    public SendReportReconciliationBean() {
    	super();
    	incomeSubBean = new ReportIncomeSubBean();
    	defraySubBean = new ReportDefraySubBean();
    	depositSubBean = new ReportDepositSubBean();
    	incomeMainDto = new TfReportIncomeMainDto();
        defrayMainDto = new TfReportDefrayMainDto();
        depositMainDto = new TfReportDepositMainDto();
        dto = new TvVoucherinfoDto();
        loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
        checkList=new ArrayList();
        paramMap = new HashMap();
        dto.setSorgcode(loginInfo.getSorgcode());
        pagingcontext = new PagingContext(this); 
        dto.setSvtcode(vouchertype);//凭证类型3511-收入报表对账，3512-支出报表对账，3513-库存账户对账
        dto.setScheckdate(TimeFacade.getCurrentStringTime());//报表起始日期
        dto.setSpaybankcode(TimeFacade.getCurrentStringTime());//报表截止日期
		dto.setSattach("1");//征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444)
		dto.setShold3("0");//辖属标志0-本级，1-全辖
//		dto.setShold2();//预算级次1-中央，2-省，3-市，4-县，5-乡
		dto.setScheckvouchertype("1");//报表种类1
        TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
  	  	tsTreasuryDto.setSorgcode(loginInfo.getSorgcode());
  	  	List<TsTreasuryDto> list = null;
	  	  try {
	  		list = (List<TsTreasuryDto>)commonDataAccessService.findRsByDto(tsTreasuryDto);
	  		acctList = new ArrayList();
	  		TsInfoconnorgaccDto qd = new TsInfoconnorgaccDto();
	  		qd.setSorgcode(loginInfo.getSorgcode());
	  		List relist =commonDataAccessService.findRsByDto(qd);
	  		for(int i=0;i<relist.size();i++)
	  		{
	  			qd = (TsInfoconnorgaccDto)relist.get(i);
	  			Mapper map = new Mapper(qd.getSpayeraccount(), qd.getSpayername());
	  			acctList.add(map);
	  		}
	  	  } catch (ITFEBizException e){
	  		  log.error(e);
	  	  }
	  	  if(list!=null && list.size() > 0){
	  		  dto.setStrecode(list.get(0).getStrecode());//国库代码
	  		  strecode = dto.getStrecode();
	  	  }
        dto.setSvtcode(vouchertype);
  	  	refreshStampType("");           
    }
    /**
	 * Direction: 生成凭证
	 * ename: voucherGenerator
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherGenerator(Object o){
    	if(Integer.parseInt(dto.getScheckdate())>Integer.parseInt(TimeFacade.getCurrentStringTime())){
    		MessageDialog.openMessageDialog(null, "对账起始日期不能大于当前系统日期！");
    		return "";
    	}
    	if(Integer.parseInt(dto.getSpaybankcode())>Integer.parseInt(TimeFacade.getCurrentStringTime())){
    		MessageDialog.openMessageDialog(null, "对账终止日期不能大于当前系统日期！");
    		return "";
    	}
    	if(Integer.parseInt(dto.getScheckdate())>Integer.parseInt(dto.getSpaybankcode())){
    		MessageDialog.openMessageDialog(null, "对账起始日期不能大于终止日期！");
    		return "";
    	}
    	if(loginInfo.getPublicparam().contains(",datelength=2,"))
		{
	    	if(Integer.parseInt(dto.getSpaybankcode().substring(4,6))-Integer.parseInt(dto.getScheckdate().substring(4,6))>1)
	    	{
	    		MessageDialog.openMessageDialog(null, "时间段不能大于两个月！");
	    		return "";
	    	}
		}
    	if(vouchertype==null||vouchertype.equals("")){
    		MessageDialog.openMessageDialog(null, "凭证类型不可为空！");
    		return "";
    	}
    	TvVoucherinfoDto vDto=null;
    	List<TvVoucherinfoDto> list=new ArrayList<TvVoucherinfoDto>();
    	int count=0;
		List<TsTreasuryDto> tList=new ArrayList<TsTreasuryDto>();
		try {
			TsTreasuryDto tDto=new TsTreasuryDto();
			tDto.setSorgcode(loginInfo.getSorgcode());
			if(dto.getStrecode()==null||dto.getStrecode().equals("")){				
					tList=commonDataAccessService.findRsByDto(tDto);
				}else{
					tDto.setStrecode(dto.getStrecode());				
					tList.add(tDto);
			}
			for(TsTreasuryDto tsDto:tList){
				vDto=new TvVoucherinfoDto();
				vDto.setSorgcode(dto.getSorgcode());
				vDto.setSpaybankcode(dto.getSpaybankcode());//对账截止日期
	    		vDto.setScheckdate(dto.getScheckdate());//对账起始日期
	    		vDto.setScreatdate(dto.getScreatdate());//凭证日期
	    		vDto.setStrecode(tsDto.getStrecode());//国库代码
				if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3511))
		    	{
		    		vDto.setShold1(dto.getShold1());//预算种类
		    		vDto.setShold2(dto.getShold2());//预算级次
		    		vDto.setShold3(dto.getShold3());//辖属标志
		    		vDto.setSattach(dto.getSattach());//征收机关
		    		vDto.setScheckvouchertype(dto.getScheckvouchertype());//报表种类
		    	}
		    	else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3512))
		    	{
		    		vDto.setShold1(dto.getShold1());//预算种类
		    		vDto.setScheckvouchertype(dto.getScheckvouchertype());//报表种类
		    	}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3513)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3514))
		    	{
		    		vDto.setShold1(dto.getShold1());//库款账户
		    	}
		    	vDto.setSvtcode(vouchertype);
				list.add(vDto);
				if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3513)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3514)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3512)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3511)){
					try{
						List dtoList = commonDataAccessService.findRsByDto(vDto);
						vDto.setScreatdate(TimeFacade.getCurrentStringTime());//凭证日期
						if(dtoList!=null&&dtoList.size()>0)
						{
							count=-1;
							break;
						}
					}catch(java.lang.IndexOutOfBoundsException e){
						continue;
					}catch(Exception e){
						log.error(e);
						Exception e1=new Exception("生成凭证操作出现异常！",e);			
						MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
						return "";
					}
				}else
					vDto.setScreatdate(TimeFacade.getCurrentStringTime());//凭证日期
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
    	if(count==-1){
    		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
    				.getCurrentComposite().getShell(), "提示", "国库："+vDto.getStrecode()+" 当前时间段："+dto.getScheckdate()+"到"+vDto.getSpaybankcode()+"已生成凭证，确定继续生成吗？")) {
    			return "";
    		}
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
        return super.voucherGenerator(o);
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
	 * Direction: 删除凭证
	 * ename: deletegenvoucher
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delgenvoucher(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要删除电子凭证数据记录！");
    		return "";
    	}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行删除操作吗？")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
			if(!DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus().trim())){
				MessageDialog.openMessageDialog(null, "只有状态为处理成功的数据可以删除！");
        		return "";
			}
    	}
    	try {
	    	for(TvVoucherinfoDto infoDto:checkList)
	    	{
	    		commonDataAccessService.delete(infoDto);
	    	}
	    	refreshTable();
    	} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("删除数据失败！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}
        return "";
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
	 * Direction: 查询 
	 * ename: search
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherSearch(Object o){
    	dto.setSorgcode(loginInfo.getSorgcode());
    	dto.setSvtcode(vouchertype);//凭证类型
    	if(dto.getStrecode()==null||dto.getStrecode().equals("")){
    		MessageDialog.openMessageDialog(null, "国库代码不可为空！");
    		return "";
    	}
    	if(dto.getSvtcode()==null||dto.getSvtcode().equals("")){
    		MessageDialog.openMessageDialog(null, "请先选择凭证类型！");
    		return "";
    	}
    	
    	refreshTable();
        return super.voucherSearch(o);
    }
    
    private void init() {
    	dto.setSvtcode(vouchertype);//凭证类型
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录，请先生成凭证！");
		}//else if(MsgConstant.VOUCHER_NO_3511.equals(dto.getSvtcode()))
//		{
//			for(TvVoucherinfoDto temp:(List<TvVoucherinfoDto>)pageResponse.getData())
//			{
//				if("0".equals(temp.getSattach()))
//					temp.setSattach(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
//				else if("1".equals(temp.getSattach()))
//					temp.setSattach(MsgConstant.MSG_TAXORG_SHARE_CLASS);
//				else if("2".equals(temp.getSattach()))
//					temp.setSattach("");
//				else if("3".equals(temp.getSattach()))
//					temp.setSattach(MsgConstant.MSG_TAXORG_NATION_CLASS);
//				else if("4".equals(temp.getSattach()))
//					temp.setSattach(MsgConstant.MSG_TAXORG_PLACE_CLASS);
//				else if("5".equals(temp.getSattach()))
//					temp.setSattach(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
//				else if("6".equals(temp.getSattach()))
//					temp.setSattach(MsgConstant.MSG_TAXORG_OTHER_CLASS);
//			}
//		}
		pagingcontext.setPage(pageResponse);
	}
    
    /**
	 * Direction: 全选
	 * ename: selectAll
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
    	 if(checkList==null||checkList.size()==0){
         	checkList = new ArrayList();
         	checkList.addAll(pagingcontext.getPage().getData());
         }
         else
         	checkList.clear();
         this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
         return super.selectAll(o);
    }
    
    /**
	 * Direction: 发送电子凭证
	 * ename: sendReturnVoucher
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
    	List newcheckList = new ArrayList();
    	for(TvVoucherinfoDto vDto:this.checkList){
    		try {
    			newcheckList.add(getDto(vDto));
			} catch (ITFEBizException e) {
				Exception e1=new Exception("重新查询数据出现错误！",e);			
				MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
			}
			
    	}
    	checkList.clear();
    	checkList.addAll(newcheckList);
    	for(TvVoucherinfoDto infoDto:checkList)
    	{
    		if (loginInfo.getPublicparam().indexOf(",stampmode=sign") >= 0) {
				if (!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto
						.getSstatus().trim()))) {
					MessageDialog.openMessageDialog(null,
							"请选择凭证状态为 \"处理成功\" 的记录！");
					return "";
				}
			} else {
				if (!infoDto.getSstatus().equals(DealCodeConstants.VOUCHER_STAMP)&&!infoDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS))
				{
					MessageDialog.openMessageDialog(null,"请选择凭证状态为\"签章成功\" ，或凭证状态为\"已回单\""
							+ "详细信息为  \"回单状态:对方未接收\" ，\"回单状态:对方接收失败\"，\"回单状态:对方签收失败\"，"
							+ "\"回单状态:本方未发送 \"，\"回单状态:对方已退回\"的记录！");
					return "";
				}else if (infoDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS)) {
					String repeatVoucherFlag = getRepeatVoucherFlag(infoDto);// 获取重新发送凭证辅助标志
					if (!repeatVoucherFlag.equals("0")) {
						infoDto.setSreturnerrmsg(infoDto.getSreturnerrmsg() + "$"
								+ repeatVoucherFlag);
					} else
					{
						MessageDialog.openMessageDialog(null,"请选择凭证状态为\"签章成功\" ，或凭证状态为\"已回单\""
								+ "详细信息为  \"回单状态:对方未接收\" ，\"回单状态:对方接收失败\"，\"回单状态:对方签收失败\"，"
								+ "\"回单状态:本方未发送 \"，\"回单状态:对方已退回\"的记录！");
						return "";
					}
				}
			}
	    }		
    	try {   		
    		 count=voucherLoadService.voucherReturnSuccess(checkList);
    		 MessageDialog.openMessageDialog(null, "发送电子凭证   "+checkList.size()+" 条，成功条数为："+count+" ！");
    		 refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("发送电子凭证库操作出现异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
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
	 * Direction: 凭证查看
	 * ename: voucherView
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherView(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要查看的记录！");
    		return "";
    	}
    	try{
    		ActiveXCompositeReconciliationOcx.init(0);
        	
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
    		log.error(e);
    		Exception e1=new Exception("凭证查看异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
    	}
        return super.voucherView(o);
    }
    /**
	 * Direction: 回单还原展示
	 * ename: returnVoucherView
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String returnVoucherView(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要查看的记录！");
    		return "";
    	}
    	try{
    		if(returnList==null)
    			returnList = new ArrayList<TvVoucherinfoDto>();
	    	TvVoucherinfoDto fdto = new TvVoucherinfoDto();
	    	List tempList = null;
	    	for(TvVoucherinfoDto vdto:checkList)
	    	{
	    		fdto.setSext2(vdto.getSvoucherno());
	    		if(MsgConstant.VOUCHER_NO_3511.equals(vdto.getSvtcode()))
	    		{
	    			fdto.setSvtcode(MsgConstant.VOUCHER_NO_5511);
	    			tempList = commonDataAccessService.findRsByDto(fdto);
	    			if(tempList!=null&&tempList.size()>0)
	    				returnList.addAll(tempList);
	    		}else if(MsgConstant.VOUCHER_NO_3512.equals(vdto.getSvtcode()))
	    		{
	    			fdto.setSvtcode(MsgConstant.VOUCHER_NO_5512);
	    			tempList = commonDataAccessService.findRsByDto(fdto);
	    			if(tempList!=null&&tempList.size()>0)
	    				returnList.addAll(tempList);
	    		}else if(MsgConstant.VOUCHER_NO_3513.equals(vdto.getSvtcode()))
	    		{
	    			fdto.setSvtcode(MsgConstant.VOUCHER_NO_5513);
	    			tempList = commonDataAccessService.findRsByDto(fdto);
	    			if(tempList!=null&&tempList.size()>0)
	    				returnList.addAll(tempList);
	    		}
	    	}
	    	if(returnList==null||returnList.size()==0){
	    		MessageDialog.openMessageDialog(null, "选择的记录没有收到对方的回单！");
	    		return "";
	    	}
    		ActiveXCompositeReconciliationReturnOcx.init(0);
        	
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
    		log.error(e);
    		Exception e1=new Exception("凭证查看异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
    	}
        return super.returnVoucherView(o);
    }
    /**
	 * Direction: 签章
	 * ename: transferCompletedStamp
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherStamp(Object o){    	
    	boolean ocxflag=false;
    	List<TvVoucherinfoDto> checkList=new ArrayList<TvVoucherinfoDto>();
    	String stamp=null;
    	TvVoucherinfoDto dto=new TvVoucherinfoDto();    
    	if(o instanceof List){
			List  ocxStampList=(List) o;
    		String stampname=(String) ocxStampList.get(0);       		
    		dto=(TvVoucherinfoDto) ocxStampList.get(1);    		
    		TsStamppositionDto stampPostionDto=new TsStamppositionDto();
    		stampPostionDto.setSorgcode(dto.getSorgcode());
    		stampPostionDto.setStrecode(dto.getStrecode());
    		stampPostionDto.setSvtcode(dto.getSvtcode());
    		stampPostionDto.setSstampname(stampname);
				try {
					stampPostionDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(stampPostionDto).get(0);
				} catch (ITFEBizException e) {
					log.error(e); 
					Exception e1=new Exception("签章出现异常！",e);			
					MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
					return "";
				}			
			stamp=stampPostionDto.getSstamptype();
			this.stamp = stampPostionDto.getSstamptype();
			checkList.add(dto);
			ocxflag=true;			
    	}
    	if(!ocxflag){
    		stamp=this.stamp;
    		checkList=this.checkList;
    		dto=this.dto;
    	}
    	int count=0;
    	if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择签章类型！");
			return null;
		}
    	
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要签章的记录！");
    		return "";
    	}
    	
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行签章操作吗？")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
	    		if(!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus().trim()))){
	    			MessageDialog.openMessageDialog(null, "请选择凭证状态为处理成功的记录！");	    			
	        		return "";
	    		}
	    	}		
    	try {
    		if(!((TvVoucherinfoDto)checkList.get(0)).getSvtcode().equals(dto.getSvtcode())){
    			MessageDialog.openMessageDialog(null, "选择的参数[凭证类型]与校验数据不符！");    			
    			return "";       		
    		}    		
    		TsUsersDto uDto=new TsUsersDto();
    		uDto.setSorgcode(loginInfo.getSorgcode());
    		uDto.setSusercode(loginInfo.getSuserCode());
    		uDto=(TsUsersDto) commonDataAccessService.findRsByDto(uDto).get(0);
    		TsStamppositionDto stampDto=new TsStamppositionDto();
    		stampDto.setSvtcode(dto.getSvtcode());
    		stampDto.setSorgcode(loginInfo.getSorgcode());
    		stampDto.setSstamptype(stamp);
    		stampDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(stampDto).get(0);
    		String permission= uDto.getSstamppermission();
    		boolean flag=true;
    		if(permission==null||permission.equals("")){
    			flag=false;
    		}else{
    			if(permission.indexOf(",")<0){
        			if(!permission.equals(stamp)){
        				flag=false;
        			}       			
        		}else{
        			flag=false;
        			String[] permissions=permission.split(",");
        			for(int i=0;i<permissions.length;i++){
        				if(permissions[i].equals(stamp)){
        					flag=true;
        					break;
        				}
        			}        			
        		}
    		}    		
    		if(flag==false){
				MessageDialog.openMessageDialog(null, "当前用户无  \""+stampDto.getSstampname()+"\"  签章权限！");    			
    			return "";
			}
    		TsTreasuryDto tDto=new TsTreasuryDto();
    		TsStamppositionDto sDto=new TsStamppositionDto();
    		Map map=new HashMap();
    		String usercode=uDto.getSusercode();
    		String stampuser="";
    		String stampid="";
    		for(TvVoucherinfoDto vDto:checkList){
    			map.put(vDto.getStrecode(), "");
    			stampid=vDto.getSstampid();
    			if(stampid!=null&&!stampid.equals("")){
					String[] stampids=stampid.split(",");
					for(int i=0;i<stampids.length;i++){
						if(stamp.equals(stampids[i])){
							MessageDialog.openMessageDialog(null, "凭证编号："+vDto.getSvoucherno()+" 已签  \""+stampDto.getSstampname()+"\" ，同一凭证不能重复签章！");			    			
			    			return "";
						}
					}
    			}
    			if(!stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)&&!stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)&&!stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)){
    				stampuser=vDto.getSstampuser();
    				if(stampuser!=null&&!stampuser.equals("")){
						String[] stampusers=stampuser.split(",");
						for(int i=0;i<stampusers.length;i++){							
							if(usercode.equals(stampusers[i])){
								TsStamppositionDto tstampDto=new TsStamppositionDto();
								tstampDto.setSorgcode(loginInfo.getSorgcode());
								tstampDto.setSvtcode(dto.getSvtcode());
								String[]  stampids=vDto.getSstampid().split(",");
								for(int j=0;j<stampids.length;j++){
									if(!stampids[i].equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
										tstampDto.setSstamptype(stampids[i]);
										break;
									}
								}
								tstampDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(tstampDto).get(0);
								MessageDialog.openMessageDialog(null, "凭证编号："+vDto.getSvoucherno()+" 当前用户已签  \""+tstampDto.getSstampname()+"\" ，同一用户只能签一次私章，请选择其他用户！");    			    			
    			    			return "";
							}
						}
					
    				}
        		}
    		}    		
    		Iterator it=map.keySet().iterator();    		
    		List lists=new ArrayList();
    		List list=null;
    		List sinList=null;
    		List<TsStamppositionDto> sList=null;
    		List vList=null;
    		String strecode="";
//    		String xml="";
    		while(it.hasNext()){
    			 strecode=it.next()+"";
    			 vList=new ArrayList();
    			 tDto=new TsTreasuryDto();
    			 sDto=new TsStamppositionDto();
    			 sList=new ArrayList<TsStamppositionDto>();
    			 list=new ArrayList();
    			try{
    				tDto.setSorgcode(loginInfo.getSorgcode());
    				tDto.setStrecode(strecode);
        			tDto=(TsTreasuryDto) commonDataAccessService.findRsByDto(tDto).get(0);
    			}catch(Exception e){
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中不存在！");
    				
    				return "";
    			}    			
    			try{
    				sDto.setSorgcode(loginInfo.getSorgcode());
        			sDto.setStrecode(strecode);
        			sDto.setSvtcode(dto.getSvtcode());
        			sList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(sDto);
        			sDto.setSstamptype(stamp);
        			sDto= (TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
        			
    			}catch(Exception e){
    			
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "国库主体代码："+strecode+" "+stampDto.getSstampname()+" 参数未维护！ ");
    				
    				return "";
    			}    			
    			if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
    				if(tDto.getSrotarycertid()==null||tDto.getSrotarycertid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"证书ID 参数未维护！ ");
        				
        				return "";
        			}else if(tDto.getSrotaryid()==null||tDto.getSrotaryid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"印章ID 参数未维护！ ");
        				
        				return "";
        			}
    			}else if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){

    				if(tDto.getScertid()==null||tDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"证书ID 参数未维护！ ");
        				
        				return "";
        			}else if(tDto.getSstampid()==null||tDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"印章ID 参数未维护！ ");
        				
        				return "";
        			}
    			
    			}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH))
				{
    				if(tDto.getSattachcertid()==null||tDto.getSattachcertid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"证书ID 参数未维护！ ");
        				
        				return "";
        			}else if(tDto.getSattachid()==null||tDto.getSattachid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"印章ID 参数未维护！ ");
        				
        				return "";
        			}
				}else{
    				if(uDto.getScertid()==null||uDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "当前用户  "+stampDto.getSstampname()+"  证书ID参数未维护！ ");        				
        				return "";
        			}else if(uDto.getSstampid()==null||uDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "当前用户   "+stampDto.getSstampname()+"  印章ID参数未维护！ ");        				
        				return "";
        			}    		    			
    			}
    			for(TvVoucherinfoDto vDto:checkList){
    				if(vDto.getStrecode().equals(strecode)){    					
    					sinList=new ArrayList();
    					sinList.add(vDto);
						stampid=vDto.getSstampid();	
						String seq=sDto.getSstampsequence();   						
						if(seq!=null&&!seq.equals("")){
							List<String> seqList=new ArrayList<String>();
    						for(int i=0;i<sList.size();i++){
    							TsStamppositionDto tsDto=(TsStamppositionDto) sList.get(i);
    							if(tsDto.getSstampsequence()!=null&&!tsDto.getSstampsequence().equals("")){
    								seqList.add(tsDto.getSstampsequence());
    							}
    						}
    						if(seqList!=null&&seqList.size()>0){
    							String[] seqs=seqList.toArray(new String[seqList.size()]);
    							ActiveXCompositeReconciliationOcx.sortStringArray(seqs);    							
    							String temp="";
    							for(int i=seqs.length-1;i>-1;i--){
    								if(Integer.parseInt(seqs[i])<Integer.parseInt(seq)){
    									temp=seqs[i];
    									break;
    								}
    							}
    							if(!temp.equals("")){
    								List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
    								TsStamppositionDto tsDto=new TsStamppositionDto();
    								tsDto.setSorgcode(loginInfo.getSorgcode());
    								tsDto.setStrecode(strecode);
    								tsDto.setSvtcode(vDto.getSvtcode());
    								tsDto.setSstampsequence(temp);
    			        			tsList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(tsDto);
    			        			String err="";
    			        			for(TsStamppositionDto tstampDto:tsList){
    			        				err=tstampDto.getSstampname()+" , "+err;
    			        			}
    			        			err=err.substring(0,err.lastIndexOf(","));
    			        			if(stampid==null||stampid.equals("")){
        								MessageDialog.openMessageDialog(null, "国库代码："+vDto.getStrecode()+" 凭证类型: "+vDto.getSvtcode()+vDto.getSvoucherno()+" \""+stampDto.getSstampname()+"\"签章前请先 \""+err+"\"签章！");
            			    			
            			    			return "";
        							
    			        			}else{
    			        				err="";
    			        				String[] stampids=stampid.split(",");
			        					List<TsStamppositionDto> tsList1=new ArrayList<TsStamppositionDto>();
			        					for(int j=0;j<tsList.size();j++){
			        						for(int i=0;i<stampids.length;i++){
    			        						if(stampids[i].equals(tsList.get(j).getSstamptype())){
    			        							tsList1.add(tsList.get(j));
    			        						}
    			        					}
		        						}
			        					tsList.removeAll(tsList1);
			        					if(tsList.size()>0){
			        						for(TsStamppositionDto tstampDto:tsList){
		    			        				err=tstampDto.getSstampname()+" , "+err;
		    			        			}
			        						err=err.substring(0,err.lastIndexOf(","));
			        						MessageDialog.openMessageDialog(null, "国库代码："+vDto.getStrecode()+" 凭证类型: "+vDto.getSvtcode()+" \""+stampDto.getSstampname()+"\"签章前请先 \""+err+"\"签章！");
	            			    			
	            			    			return "";
			        					}
    			        			}
    			        		
    							}
    						}
						}
						if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_OFFICIALSTAMP)){								
								sinList.add(ActiveXCompositeReconciliationOcx.getVoucherStamp(vDto, tDto.getScertid(), sDto.getSstampposition(), tDto.getSstampid()));   	    												
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP)){								
								sinList.add(ActiveXCompositeReconciliationOcx.getVoucherStamp(vDto, tDto.getSrotarycertid(), sDto.getSstampposition(), tDto.getSrotaryid()));   	    												
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH))
						{
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP))
								sinList.add(ActiveXCompositeReconciliationOcx.getVoucherStamp(vDto, tDto.getSattachcertid(), sDto.getSstampposition(), tDto.getSattachid()));
						}else{
							if(!loginInfo.getPublicparam().contains(",jbrstamp=server,"))
							{
								sinList.add(ActiveXCompositeReconciliationOcx.getVoucherStamp(vDto, uDto.getScertid(), sDto.getSstampposition(),uDto.getSstampid()));
							}
						}
    					vList.add(sinList);
    				}
    			}
    			list.add(uDto);
    			list.add(tDto);
    			list.add(sDto);
    			list.add(sList.size());
    			list.add(vList);    			
    			lists.add(list);
    		}
    		count=voucherLoadService.voucherStamp(lists);
    		if(ocxflag){
    			
    			return count+"";
    		}
    		MessageDialog.openMessageDialog(null, "凭证签章   "+checkList.size()+" 条，成功条数为："+count+" ！");
    		refreshTable();
    	}catch (ITFEBizException e) {
    		log.error(e);    		
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
			return "";
			
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("签章操作出现异常！",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
			
		}		
        return super.voucherStamp(o);
    }
    
    /**
	 * Direction:签章撤销
	 * ename: transferCompletedStampCancle
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String voucherStampCancle(Object o){
    	int count=0;        	
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要签章撤销的记录！");
    		return "";
    	}
    	if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "请选择签章类型！");
			return null;
		}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行签章撤销操作吗？")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
	    		if(!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus()))&&!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus()))){
	    			MessageDialog.openMessageDialog(null, "请选择凭证状态为处理成功、签章成功的记录！");
	    			
	        		return "";
	    		}
	    	}
		
    	try {
    		if(!((TvVoucherinfoDto)checkList.get(0)).getSvtcode().equals(dto.getSvtcode())){
    			MessageDialog.openMessageDialog(null, "选择的参数[凭证类型]与校验数据不符！");
    			
    			return "";
        		
    		}
   
    		TsUsersDto uDto=new TsUsersDto();
    		uDto.setSorgcode(loginInfo.getSorgcode());
    		uDto.setSusercode(loginInfo.getSuserCode());
    		uDto=(TsUsersDto) commonDataAccessService.findRsByDto(uDto).get(0);
    		TsStamppositionDto stampDto=new TsStamppositionDto();
    		stampDto.setSvtcode(dto.getSvtcode());
    		stampDto.setSorgcode(loginInfo.getSorgcode());
    		stampDto.setSstamptype(stamp);
    		stampDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(stampDto).get(0);
    		String permission= uDto.getSstamppermission();
    		boolean flag=true;
    		if(permission==null||permission.equals("")){
    			flag=false;
    		}else{
    			if(permission.indexOf(",")<0){
        			if(!permission.equals(stamp)){
        				flag=false;
        			}
        			
        		}else{
        			flag=false;
        			String[] permissions=permission.split(",");
        			for(int i=0;i<permissions.length;i++){
        				if(permissions[i].equals(stamp)){
        					flag=true;
        					break;
        				}
        			}
        			
        		}
    		}
    		boolean managerPermission=false;
    		if(flag==false){
				MessageDialog.openMessageDialog(null, "当前用户无  \""+stampDto.getSstampname()+"\"  签章权限！，可通过主管授权撤销签章");
				String msg = "需要主管授权才能才能撤销签章！";
				if(!AdminConfirmDialogFacade.open("B_247", "报表对账凭证", "授权用户"+loginInfo.getSuserName()+"撤销签章", msg)){
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					return null;
				}else{
					managerPermission=true;
				}
				
			}
    		TsTreasuryDto tDto=new TsTreasuryDto();
    		TsStamppositionDto sDto=new TsStamppositionDto();
    		Map map=new HashMap();
    		String usercode=uDto.getSusercode();
//    		String stampuser="";
    		String stampid="";    		
    		for(TvVoucherinfoDto vDto:checkList){
    			usercode=uDto.getSusercode();
    			map.put(vDto.getStrecode(), "");
    			stampid=vDto.getSstampid();
    			int j=-1;
    			if(stampid==null||stampid.equals("")){
    				flag=false;
    			}else{
    				flag=false;
					String[] stampids=stampid.split(",");
					for(int i=0;i<stampids.length;i++){
						if(stamp.equals(stampids[i])){
							flag=true;
							j=i;
							break;
							
						}
					}
    			
    			}if(flag==false){
					MessageDialog.openMessageDialog(null, "凭证编号："+vDto.getSvoucherno()+" 未签  \""+stampDto.getSstampname()+"\" ！");	    			
	    			return "";
				}
    			if(managerPermission==false){
    				if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
						usercode=usercode+"!";
					}else if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
						usercode=usercode+"#";
					}
    				String stampuserboolean=vDto.getSstampuser().split(",")[j];
    				if(!stampuserboolean.equals(usercode)){
    					MessageDialog.openMessageDialog(null, "凭证编号："+vDto.getSvoucherno()+"   \""+stampDto.getSstampname()+"\" 不是当前用户所签！可通过主管授权撤销签章");
    					String msg = "需要主管授权才能才能撤销签章！";
    					if(!AdminConfirmDialogFacade.open("B_247", "收入报表凭证", "授权用户"+loginInfo.getSuserName()+"撤销签章", msg)){
    						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    						return null;
    					}else{
    						managerPermission=true;
    					}    				
    				}   				
    			}
    		}
    		
    		Iterator it=map.keySet().iterator();
    		
    		List lists=new ArrayList();
    		List list=null;
    		List sinList=null;
    		List<TsStamppositionDto> sList=null;
    		List vList=null;
    		
    		String strecode="";
//    		String xml="";
    		while(it.hasNext()){
    			 strecode=it.next()+"";
    			 vList=new ArrayList<TvVoucherinfoDto>();
    			 tDto=new TsTreasuryDto();
    			 sDto=new TsStamppositionDto();
    			 sList=new ArrayList<TsStamppositionDto>();
    			 list=new ArrayList();
    			try{
    				tDto.setSorgcode(loginInfo.getSorgcode());
    				tDto.setStrecode(strecode);
        			tDto=(TsTreasuryDto) commonDataAccessService.findRsByDto(tDto).get(0);
    			}catch(Exception e){
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中不存在！");
    				
    				return "";
    			}
    			
    			try{
    				sDto.setSorgcode(loginInfo.getSorgcode());
        			sDto.setStrecode(strecode);
        			sDto.setSvtcode(dto.getSvtcode());
        			sList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(sDto);
        			sDto.setSstamptype(stamp);
        			sDto= (TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
        			
    			}catch(Exception e){
    			
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "国库主体代码："+strecode+" "+stampDto.getSstampname()+" 参数未维护！ ");
    				
    				return "";
    			}
    			
    			if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
    				if(tDto.getSrotarycertid()==null||tDto.getSrotarycertid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"证书ID 参数未维护！ ");
        				
        				return "";
        			}else if(tDto.getSrotaryid()==null||tDto.getSrotaryid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"印章ID 参数未维护！ ");
        				
        				return "";
        			}
    			}else if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){

    				if(tDto.getScertid()==null||tDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"证书ID 参数未维护！ ");
        				
        				return "";
        			}else if(tDto.getSstampid()==null||tDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "国库主体代码"+strecode+"在国库主体信息参数中 "+stampDto.getSstampname()+"印章ID 参数未维护！ ");
        				
        				return "";
        			}
    			
    			}else{
    				if(uDto.getScertid()==null||uDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "当前用户  "+stampDto.getSstampname()+"  证书ID参数未维护！ ");
        				
        				return "";
        			}else if(uDto.getSstampid()==null||uDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "当前用户   "+stampDto.getSstampname()+"  印章ID参数未维护！ ");
        				
        				return "";
        			}
    		    			
    			}
 
    			for(TvVoucherinfoDto vDto:checkList){
    				if(vDto.getStrecode().equals(strecode)){
    					
    					sinList=new ArrayList();
    					sinList.add(vDto);
						stampid=vDto.getSstampid();	
						String seq=sDto.getSstampsequence();   						
						if(seq!=null&&!seq.equals("")){
							List<String> seqList=new ArrayList<String>();
    						for(int i=0;i<sList.size();i++){
    							TsStamppositionDto tsDto=(TsStamppositionDto) sList.get(i);
    							if(tsDto.getSstampsequence()!=null&&!tsDto.getSstampsequence().equals("")){
    								seqList.add(tsDto.getSstampsequence());
    							}
    						}
    						if(seqList!=null&&seqList.size()>0){
    							String[] seqs=seqList.toArray(new String[seqList.size()]);
    							ActiveXCompositeReconciliationOcx.sortStringArray(seqs);
    							
    							String temp="";
    							for(int i=0;i<seqs.length;i++){
    								if(Integer.parseInt(seqs[i])>Integer.parseInt(seq)){
    									temp=seqs[i];
    									break;
    								}
    							}
    							if(!temp.equals("")){
    								List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
    								TsStamppositionDto tsDto=new TsStamppositionDto();
    								tsDto.setSorgcode(loginInfo.getSorgcode());
    								tsDto.setStrecode(strecode);
    								tsDto.setSvtcode(vDto.getSvtcode());
    								tsDto.setSstampsequence(temp);
    			        			tsList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(tsDto);
    			        			String err="";
    			      
			        				String[] stampids=stampid.split(",");
//		        					List<TsStamppositionDto> tsList1=new ArrayList<TsStamppositionDto>();
		        					for(int j=0;j<tsList.size();j++){
		        						for(int i=0;i<stampids.length;i++){
			        						if(stampids[i].equals(tsList.get(j).getSstamptype())){
			        							err=tsList.get(j).getSstampname()+" "+err;
			        						}
			        					}
	        						}
		        					if(!err.trim().equals("")){
		        						for(TsStamppositionDto tstampDto:tsList){
	    			        				err=tstampDto.getSstampname()+" , "+err;
	    			        			}
		        						err=err.substring(0,err.lastIndexOf(","));
		        						MessageDialog.openMessageDialog(null, "国库代码："+vDto.getStrecode()+" 凭证类型: "+vDto.getSvtcode()+" \""+stampDto.getSstampname()+"\"撤销签章前请先撤销 \""+err+"\"签章！");
            			    			
            			    			return "";
		        					}    			        		
    							}
    						}
						}
						int j=-1;
						String[] stampids=stampid.split(",");
						for(int i=0;i<stampids.length;i++){
							if(stamp.equals(stampids[i])){								
								j=i;
								break;
								
							}
						}
						TsUsersDto userDto=new TsUsersDto();
						userDto.setSorgcode(loginInfo.getSorgcode());
						String user=vDto.getSstampuser().split(",")[j];
						
						userDto.setSusercode(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)?user.substring(0, (user.length()-1)):(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)?user.substring(0, (user.length()-1)):user));
						userDto=(TsUsersDto) commonDataAccessService.findRsByDto(userDto).get(0);
						sinList.add(userDto);
						vList.add(sinList);
    				}
    			}
    			
    			list.add(tDto);
    			list.add(sDto);
    			
    			list.add(vList);
    			
    			lists.add(list);
    		}
    		count=voucherLoadService.voucherStampCancle(lists);
    		MessageDialog.openMessageDialog(null, "凭证撤销签章   "+checkList.size()+" 条，成功条数为："+count+" ！");
    		refreshTable();
    	}catch (ITFEBizException e) {
    		log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
			return "";
			
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("签章操作撤销出现异常！",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
			
		}
        return super.voucherStampCancle(o);
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
//    	int count=0;
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
    

    public List<Mapper> getStampEnums(TvVoucherinfoDto vDto){
		List<Mapper> maplist = new ArrayList<Mapper>();
		
		List<TsStamppositionDto> enumList = new ArrayList<TsStamppositionDto>();
		TsStamppositionDto tDto=new TsStamppositionDto();
		tDto.setSorgcode(vDto.getSorgcode());
		tDto.setStrecode(vDto.getStrecode());
		tDto.setSvtcode(vDto.getSvtcode());
		try {
			enumList =commonDataAccessService.findRsByDto(tDto);
			if(enumList!=null&&enumList.size()>0){
				for (TsStamppositionDto emuDto : enumList) {
					Mapper map = new Mapper(emuDto.getSstamptype(), emuDto.getSstampname());
					maplist.add(map);
				}
			}
			
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("获取凭证签章列表出现异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}		
		return maplist;
	}
    public String getVoucherXMl(TvVoucherinfoDto vDto) throws ITFEBizException{
    	
    	return voucherLoadService.voucherStampXml(vDto);
    }
    
    public void refreshTable(){
    	init();
		checkList.clear();
		refreshStampType();
    }
    
    /**
     * 刷新签章类型
     * 
     */
    public void refreshStampType(){
    	TsStamppositionDto tsDto=new TsStamppositionDto();
        tsDto.setSorgcode(dto.getSorgcode());
      	tsDto.setSvtcode(dto.getSvtcode());
      	tsDto.setStrecode(dto.getStrecode());
      	Set set=new HashSet();
      	TsStamppositionDto sDto=new TsStamppositionDto();
      	List<TsStamppositionDto> tList=null;
      	stampList=new ArrayList();
      	List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
      	try{
          	tList= commonDataAccessService.findRsByDto(tsDto);
          	if(tList.size()>0){
          		for(TsStamppositionDto sdto:tList){
          			set.add(sdto.getSstamptype());
          		}
          		for(String stamptype:(Set<String>)set){
          			sDto=new TsStamppositionDto();
          			sDto.setSorgcode(dto.getSorgcode());
          			sDto.setSvtcode(dto.getSvtcode());
          			sDto.setSstamptype(stamptype);
          			sDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
          			tsList.add(sDto);
          		}
          		stampList.addAll(tsList);
          		if(stampList.size()==1){
          			stamp = ((TsStamppositionDto)stampList.get(0)).getSstamptype();     
          		}
          	}      		
      	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
      		log.error(e);
      		Exception e1=new Exception("凭证刷新操作出现异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);			   			
      	}
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    }
    /**
     * 刷新签章类型
     * 
     */
    public void refreshStampType(String type){
    	TsStamppositionDto tsDto=new TsStamppositionDto();
        tsDto.setSorgcode(dto.getSorgcode());
      	tsDto.setSvtcode(dto.getSvtcode());
      	tsDto.setStrecode(dto.getStrecode());
      	Set set=new HashSet();
      	TsStamppositionDto sDto=new TsStamppositionDto();
      	List<TsStamppositionDto> tList=null;
      	stampList=new ArrayList();
      	List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
      	try{
          	tList= commonDataAccessService.findRsByDto(tsDto);
          	if(tList.size()>0){
          		for(TsStamppositionDto sdto:tList){
          			set.add(sdto.getSstamptype());
          		}
          		for(String stamptype:(Set<String>)set){
          			sDto=new TsStamppositionDto();
          			sDto.setSorgcode(dto.getSorgcode());
          			sDto.setSvtcode(dto.getSvtcode());
          			sDto.setSstamptype(stamptype);
          			sDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
          			tsList.add(sDto);
          		}
          		stampList.addAll(tsList);
          		if(stampList.size()==1){
          			stamp = ((TsStamppositionDto)stampList.get(0)).getSstamptype();     
          		}
          	}      		
      	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		}catch(Exception e){
      		log.error(e);
      		Exception e1=new Exception("凭证刷新操作出现异常！",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);			   			
      	}
//		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    }
    
    /**
	 * Direction: 双击查看业务明细
	 * ename: doubleClick
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String doubleClick(Object o){
        TvVoucherinfoDto voucherinfoDto = (TvVoucherinfoDto)o;
        if(MsgConstant.VOUCHER_NO_3511.equals(voucherinfoDto.getSvtcode())){
        	incomeMainDto = new TfReportIncomeMainDto();
        	incomeMainDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReportIncomeMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(incomeMainDto);
				if(list==null||list.size()<=0)
					return "";
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "查询收入报表对账信息业务数据出错！");
			}
			incomeMainDto = list.get(0);
			TfReportIncomeSubDto incomeSubDto = new TfReportIncomeSubDto();
			incomeSubDto.setIvousrlno(incomeMainDto.getIvousrlno());
			String tmp = incomeSubBean.searchDtoList(incomeSubDto);
			if(tmp==null){
    			PagingContext p = incomeSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "收入报表对账";
        }else if(MsgConstant.VOUCHER_NO_3512.equals(voucherinfoDto.getSvtcode())){
        	defrayMainDto = new TfReportDefrayMainDto();
        	defrayMainDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReportDefrayMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(defrayMainDto);
				if(list==null||list.size()<=0)
					return "";
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "查询支出报表对账业务数据出错！");
			}
			defrayMainDto = list.get(0);
			TfReportDefraySubDto defraySubDto = new TfReportDefraySubDto();
			defraySubDto.setIvousrlno(defrayMainDto.getIvousrlno());
			String tmp = defraySubBean.searchDtoList(defraySubDto);
			if(tmp==null){
    			PagingContext p = defraySubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "支出报表对账";
        }else if(MsgConstant.VOUCHER_NO_3513.equals(voucherinfoDto.getSvtcode())){
        	depositMainDto = new TfReportDepositMainDto();
        	depositMainDto.setIvousrlno(Long.valueOf(voucherinfoDto.getSdealno()));
        	List<TfReportDepositMainDto> list = null;
        	try {
				list = commonDataAccessService.findRsByDto(depositMainDto);
				if(list==null||list.size()<=0)
					return "";
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "查询库款账户对账业务数据出错！");
			}
			depositMainDto = list.get(0);
			TfReportDepositSubDto depositSubDto = new TfReportDepositSubDto();
			depositSubDto.setIvousrlno(depositMainDto.getIvousrlno());
			String tmp = depositSubBean.searchDtoList(depositSubDto);
			if(tmp==null){
    			PagingContext p = depositSubBean.getPagingContext();
    			p.setPage(new PageResponse());
    		}
			return "库款账户对账";
        }
        return "";
    }
    
        /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	dto.setSvtcode(vouchertype);
    	PageResponse page = null;
    	TvVoucherinfoDto vDto=new TvVoucherinfoDto();
    	vDto.setSvtcode(dto.getSvtcode());
    	vDto.setSstatus(dto.getSstatus());
    	vDto.setScreatdate(dto.getScreatdate());//凭证日期
    	vDto.setStrecode(dto.getStrecode());//国库代码
    	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3511))
    	{
    		vDto.setShold1(dto.getShold1());//预算种类
    		vDto.setShold2(dto.getShold2());//预算级次
    		vDto.setShold3(dto.getShold3());//辖属标志
    		vDto.setSattach(dto.getSattach());//征收机关
    		vDto.setScheckvouchertype(dto.getScheckvouchertype());//报表种类
    		vDto.setSpaybankcode(dto.getSpaybankcode());//对账截止日期
    		vDto.setScheckdate(dto.getScheckdate());//对账起始日期
    	}
    	else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3512))
    	{
    		vDto.setShold1(dto.getShold1());//预算种类
    		vDto.setShold2(null);//预算级次
    		vDto.setShold3(null);//辖属标志
    		vDto.setScheckvouchertype(dto.getScheckvouchertype());//报表种类
    		vDto.setSpaybankcode(dto.getSpaybankcode());//对账截止日期
    		vDto.setScheckdate(dto.getScheckdate());//对账起始日期
    	}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3513)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3514))
    	{
    		vDto.setShold1(dto.getShold1());//库款账户
    		vDto.setShold2(null);//预算级次
    		vDto.setShold3(null);//辖属标志
    		vDto.setScheckvouchertype(null);//报表种类
    		vDto.setSpaybankcode(dto.getSpaybankcode());//对账截止日期
    		vDto.setScheckdate(dto.getScheckdate());//对账起始日期
    	}
//    	}3512-dto.shold1预算种类dto.scheckvouchertype报表种类dto.spaybankcode对账截止日期dto.scheckdate对账起始日期dto.screatdate凭证日期dto.strecode国库代码
//    	3513-dto.shold1库款账户dto.spaybankcode对账截止日期dto.scheckdate对账起始日期dto.screatdate凭证日期dto.strecode国库代码
//    	3511-dto.shold3辖属标志dto.shold1预算种类dto.scheckvouchertype报表种类dto.shold2预算级次dto.sattach征收机关
    	try {
    		if(loginInfo.getPublicparam().contains((",sendreportearchhold=5,")))
			{
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(TimeFacade.getCurrentDate());
				calendar.add(Calendar.DATE, -5);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
				String getdate = dateFormat.format(calendar.getTime());
				page =  commonDataAccessService.findRsByDtoPaging(vDto,pageRequest,"1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+vDto.getSorgcode()+(vDto.getStrecode()==null?"":"' and S_TRECODE='"+vDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+vDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')"," TS_SYSUPDATE DESC");
			}else if(loginInfo.getPublicparam().contains((",sendreportearchhold=15,")))
			{
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(TimeFacade.getCurrentDate());
				calendar.add(Calendar.DATE, -15);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
				String getdate = dateFormat.format(calendar.getTime());
				commonDataAccessService.findRsByDtoPaging(vDto,pageRequest,"1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+vDto.getSorgcode()+(vDto.getStrecode()==null?"":"' and S_TRECODE='"+vDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+vDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')"," TS_SYSUPDATE DESC");
			}else
			{
				page =  commonDataAccessService.findRsByDtoPaging(vDto,pageRequest, "1=1","TS_SYSUPDATE DESC");
			}
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
    public String getRepeatVoucherFlag(TvVoucherinfoDto dto) {
		String repeatVoucherFlag = "0";
		String[] errArr = { "回单状态:本方未发送","回单状态:对方未接收", "回单状态:对方接收失败", "回单状态:对方签收失败",
				 };// 回单状态：接收失败等状态
		for (int i = 0; i < errArr.length; i++) {
			if (dto.getSdemo().indexOf(errArr[i]) > -1)
				repeatVoucherFlag = "1";
			if (dto.getSdemo().indexOf("[财政机构]" + errArr[i]) > -1)
				repeatVoucherFlag = "3";
			if (dto.getSdemo().indexOf("[代理银行]" + errArr[i]) > -1)
				repeatVoucherFlag = "4";
			if (dto.getSdemo().indexOf("[财政机构]" + errArr[i]) > -1
					&& dto.getSdemo().indexOf("[代理银行]" + errArr[i]) > -1)
				repeatVoucherFlag = "5";
			if (!repeatVoucherFlag.equals("0"))
				break;
		}
		if (dto.getSdemo().indexOf("回单状态:对方已退回") > -1)
			repeatVoucherFlag = "2";
		return repeatVoucherFlag;
	}
    public TvVoucherinfoDto getDto(TvVoucherinfoDto dto) throws ITFEBizException{
    	TvVoucherinfoPK pk=new TvVoucherinfoPK();
    	pk.setSdealno(dto.getSdealno());    	   	
    	return (TvVoucherinfoDto) commonDataAccessService.find(pk);
    }
	public List<TvVoucherinfoDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TvVoucherinfoDto> checkList) {
		this.checkList = checkList;
	}



	public ITFELoginInfo getLoginInfo() {
		return loginInfo;
	}

	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public List getStampList() {
		return stampList;
	}

	public void setStampList(List stampList) {
		this.stampList = stampList;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
		this.dto.setSvtcode(voucherType);
		refreshStampType();
	}

	public List getVoucherTypeList() {
		voucherTypeList = new ReportBussReadReturnEnumFactory().getEnums("0450");
		if(voucherTypeList==null||voucherTypeList.size()<=0)
		{
			voucherTypeList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.VOUCHER_NO_3511, "收入报表对账3511");
			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3512, "支出报表对账3512");
			Mapper map2 = new Mapper(MsgConstant.VOUCHER_NO_3513, "库款账户对账3513");
			;//凭证类型3511-收入报表对账，3512-支出报表对账，3513-库存账户对账
			voucherTypeList.add(map);
			voucherTypeList.add(map1);
			voucherTypeList.add(map2);
		}
		return voucherTypeList;
	}

	public void setVoucherTypeList(List voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}

	public Map getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map paramMap) {
		this.paramMap = paramMap;
	}

	public String getVouchertype() {
		return vouchertype;
	}

	public void setVouchertype(String vouchertype) {
		this.vouchertype = vouchertype;
		this.dto.setSvtcode(vouchertype);
		PageResponse page = new PageResponse();
		checkList.clear();
		pagingcontext.setPage(page);
		if(MsgConstant.VOUCHER_NO_3511.equals(vouchertype))
		{
			dto.setScheckvouchertype("1");//报表种类1
			dto.setShold1("1");//预算种类1-预算内2-预算外
			List contreaNames = new ArrayList();
			contreaNames.add("收入报表对账生成条件");
			contreaNames.add("收入报表对账查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("支出报表对账生成条件");
			contreaNames1.add("库款账户对账生成条件");
			contreaNames1.add("支出报表对账查询结果一览表");
			contreaNames1.add("库款账户对账查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
		} else if(MsgConstant.VOUCHER_NO_3512.equals(vouchertype))
		{
			dto.setScheckvouchertype("1");//报表种类1
			dto.setShold1("1");//预算种类1-预算内2-预算外
			List contreaNames = new ArrayList();
			contreaNames.add("支出报表对账生成条件");
			contreaNames.add("支出报表对账查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("库款账户对账生成条件");
			contreaNames1.add("收入报表对账生成条件");
			contreaNames1.add("收入报表对账查询结果一览表");
			contreaNames1.add("库款账户对账查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
		}else if(MsgConstant.VOUCHER_NO_3513.equals(vouchertype)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3514))
		{
			dto.setShold1("");//库款账户
			List contreaNames = new ArrayList();
			contreaNames.add("库款账户对账生成条件");
			contreaNames.add("库款账户对账查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("收入报表对账生成条件");
			contreaNames1.add("支出报表对账生成条件");
			contreaNames1.add("收入报表对账查询结果一览表");
			contreaNames1.add("支出报表对账查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
		}
		refreshStampType();
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}
	public String getYsjc() {
		return ysjc;
	}
	public void setYsjc(String ysjc) {
		this.ysjc = ysjc;
		dto.setShold2(ysjc);
	}
	public List getAcctList() {
		return acctList;
	}
	public void setAcctList(List acctList) {
		this.acctList = acctList;
	}
	public List<TvVoucherinfoDto> getReturnList() {
		return returnList;
	}
	public void setReturnList(List<TvVoucherinfoDto> returnList) {
		this.returnList = returnList;
	}
	public ReportIncomeSubBean getIncomeSubBean() {
		return incomeSubBean;
	}
	public void setIncomeSubBean(ReportIncomeSubBean incomeSubBean) {
		this.incomeSubBean = incomeSubBean;
	}
	public ReportDefraySubBean getDefraySubBean() {
		return defraySubBean;
	}
	public void setDefraySubBean(ReportDefraySubBean defraySubBean) {
		this.defraySubBean = defraySubBean;
	}
	public ReportDepositSubBean getDepositSubBean() {
		return depositSubBean;
	}
	public void setDepositSubBean(ReportDepositSubBean depositSubBean) {
		this.depositSubBean = depositSubBean;
	}
	/**
	 * Direction: 回单状态查询 ename: queryStatusReturnVoucher 引用方法: viewers: *
	 * messages:
	 */
	public String queryStatusReturnVoucher(Object o) {
		int count = 0;
		if (checkList == null || checkList.size() == 0) {
			MessageDialog.openMessageDialog(null, "请选择要查看回单状态的记录！");
			return "";
		}
		if (checkdbstatus() == 1) {
			MessageDialog.openMessageDialog(null,
					"选择的记录中凭证状态与数据库不一致,请重新查询后再进行操作！");
			return "";
		}
		for (TvVoucherinfoDto infoDto : checkList) {
			if (!(DealCodeConstants.VOUCHER_SUCCESS.equals(infoDto.getSstatus().trim())||DealCodeConstants.VOUCHER_SUCCESS_BACK.equals(infoDto.getSstatus()))) {
				MessageDialog.openMessageDialog(null, "请选择凭证状态为回单成功或者退票成功的记录！");
				return "";
			}
		}
		try {
			count = voucherLoadService.queryStatusReturnVoucher(checkList);
			MessageDialog.openMessageDialog(null, "凭证查看回单状态" + checkList.size()
					+ " 条，成功条数为：" + count + " , 请查看！");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e);
		} catch (com.cfcc.jaf.core.invoker.http.HttpInvokerException e) {
			log.error(e);
			MessageDialog
					.openMessageDialog(null, "超过规定时间未操作业务,会话已失效\r\n请重新登录！");
		} catch (Exception e) {
			log.error(e);
			Exception e1 = new Exception("凭证查看回单状态操作出现异常！", e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(), e1);
		}
		return "";
	}
	// 判断用户选择的记录是否与数据库中状态一致，如果不一致，则提示用户重新查询后，再进行操作
	// 按照用户选择的条件重新查询数据库，逐条比对用户状态
	public int checkdbstatus() {
		if (dto.getSvtcode() == null || dto.getSvtcode().equals("")) {
			dto.setSvtcode(checkList.get(0).getSvtcode());
		}
		int i = 0;
		try {
			TvVoucherinfoDto vDto=new TvVoucherinfoDto();
	    	vDto.setSvtcode(dto.getSvtcode());
	    	vDto.setSstatus(dto.getSstatus());
	    	vDto.setScreatdate(dto.getScreatdate());//凭证日期
	    	vDto.setStrecode(dto.getStrecode());//国库代码
	    	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3511))
	    	{
	    		vDto.setShold1(dto.getShold1());//预算种类
	    		vDto.setShold2(dto.getShold2());//预算级次
	    		vDto.setShold3(dto.getShold3());//辖属标志
	    		vDto.setSattach(dto.getSattach());//征收机关
	    		vDto.setScheckvouchertype(dto.getScheckvouchertype());//报表种类
	    		vDto.setSpaybankcode(dto.getSpaybankcode());//对账截止日期
	    		vDto.setScheckdate(dto.getScheckdate());//对账起始日期
	    	}
	    	else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3512))
	    	{
	    		vDto.setShold1(dto.getShold1());//预算种类
	    		vDto.setShold2(null);//预算级次
	    		vDto.setShold3(null);//辖属标志
	    		vDto.setScheckvouchertype(dto.getScheckvouchertype());//报表种类
	    		vDto.setSpaybankcode(dto.getSpaybankcode());//对账截止日期
	    		vDto.setScheckdate(dto.getScheckdate());//对账起始日期
	    	}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3513)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3514))
	    	{
	    		vDto.setShold1(dto.getShold1());//库款账户
	    		vDto.setShold2(null);//预算级次
	    		vDto.setShold3(null);//辖属标志
	    		vDto.setScheckvouchertype(null);//报表种类
	    		vDto.setSpaybankcode(dto.getSpaybankcode());//对账截止日期
	    		vDto.setScheckdate(dto.getScheckdate());//对账起始日期
	    	}
			List<TvVoucherinfoDto> queryList = commonDataAccessService
					.findRsByDto(vDto);
			if (queryList.size() > 0) {
				HashMap<String, String> map = new HashMap<String, String>();
				for (TvVoucherinfoDto tmp : queryList) {
					if (!map.containsKey(tmp.getSdealno())) {
						map.put(tmp.getSdealno(), tmp.getSstatus());
					}
				}
				for (TvVoucherinfoDto tmp : checkList) {
					if (map.containsKey(tmp.getSdealno())
							&& tmp.getSstatus().equals(
									map.get(tmp.getSdealno()))) {
						continue;
					} else {
						i++;
						break;
					}
				}
			} else {
				return 1;
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return 1;
		}
		return i;
	}
	public String getStrecode() {
		return strecode;
	}
	public void setStrecode(String strecode) {
		this.strecode = strecode;
		dto.setStrecode(strecode);
		getTaxtrelist();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}
	public List getTaxtrelist() {
		Mapper taxmap = null;
		taxtrelist=new ArrayList();
		taxmap = new Mapper("0","财政大类");
		taxtrelist.add(taxmap);
		taxmap = new Mapper("1","不分");
		taxtrelist.add(taxmap);
		taxmap = new Mapper("2","本级征收机关");
		taxtrelist.add(taxmap);
		taxmap = new Mapper("3","国税大类");//111111111111
		taxtrelist.add(taxmap);
		taxmap = new Mapper("4","地税大类");//222222222222
		taxtrelist.add(taxmap);
		taxmap = new Mapper("5","海关大类");//333333333333
		taxtrelist.add(taxmap);
		taxmap = new Mapper("6","其它大类");//555555555555
		taxtrelist.add(taxmap);
		if(dto.getStrecode()!=null&&!"".equals(dto.getStrecode()))
		{
			TsTaxorgDto taxdto = new TsTaxorgDto();
			taxdto.setSorgcode(dto.getSorgcode());
			taxdto.setStrecode(dto.getStrecode());
			try {
				List<TsTaxorgDto> queryList = commonDataAccessService.findRsByDto(taxdto, " order by s_taxorgcode ");
				if(queryList!=null&&queryList.size()>0)
				{
					Map<String,TsTaxorgDto> tempMap = new HashMap<String,TsTaxorgDto>();
					for(TsTaxorgDto temp:queryList)
					{
						if(!tempMap.containsKey(temp.getStaxorgcode()))
						{
							if(temp.getStaxorgname()!=null&&!"".equals(temp.getStaxorgname()))
								taxmap = new Mapper(temp.getStaxorgcode(),temp.getStaxorgname());
							else
								taxmap = new Mapper(temp.getStaxorgcode(),temp.getStaxorgcode());
							tempMap.put(temp.getStaxorgcode(), temp);
							taxtrelist.add(taxmap);
						}
					}
				}
			} catch (ITFEBizException e) {
				e.printStackTrace();
			}
		}
		return taxtrelist;
	}
	public void setTaxtrelist(List taxtrelist) {
		this.taxtrelist = taxtrelist;
	}
	public List getRecvDeptList() {
		if(recvDeptList==null)
			recvDeptList = new ArrayList();
		return recvDeptList;
	}
	public void setRecvDeptList(List recvDeptList) {
		this.recvDeptList = recvDeptList;
	}
	public String getRecvDept() {
		return recvDept;
	}
	public void setRecvDept(String recvDept) {
		this.recvDept = recvDept;
	}
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		if(month!=null&&!"".equals(month)&&!"0".equals(month))
		{
			dto.setScheckdate(TimeFacade.getCurrentStringTime().substring(0,4)+month+"01");
			dto.setSpaybankcode(TimeFacade.getEndDateOfMonth(dto.getScheckdate()));
		}else
		{
			dto.setScheckdate(TimeFacade.getCurrentStringTime());
			dto.setSpaybankcode(TimeFacade.getCurrentStringTime());
		}
		this.month = month;
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	public List getMonthlist() {
		if(monthlist==null)
		{
			monthlist = new ArrayList();
			Mapper map = new Mapper("0", "");
			monthlist.add(map);
			for(int i=1;i<=12;i++)
			{
				if(i<10)
					map = new Mapper("0"+i,i+"月");
				else
					map = new Mapper(String.valueOf(i),String.valueOf(i)+"月");
				monthlist.add(map);
			}
		}
		return monthlist;
	}

	public void setMonthlist(List monthlist) {
		this.monthlist = monthlist;
	}
}
package com.cfcc.itfe.client.recbiz.voucherreport;

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
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeVoucherOcx;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeVoucherReportOcx;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
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
 * @author hjr
 * @time   13-08-11 17:53:03
 * 子系统: RecBiz
 * 模块:voucherReport
 * 组件:VoucherReport
 */
@SuppressWarnings("unchecked")
public class VoucherReportBean extends AbstractVoucherReportBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(VoucherReportBean.class);
    List<TvVoucherinfoDto> checkList=null;   
	private String strecode = null;
	//用户登录信息
	private ITFELoginInfo loginInfo;
	private boolean isJiLinArea = false ;
	private String stamp;
	private List stampList=null;
	private String voucherType=null;
	private List voucherTypeList=null;
	private Map paramMap = null;
	private List taxtrelist = null;
	private String ysjc;
	private String vouchertype=MsgConstant.VOUCHER_NO_3401;//凭证类型3404-全省库存日报表(吉林采用3453)，3401-收入日报表，3402-库存日报表 3567 收入月报
//	private String trecode;//国库代码
//	private String voudate=TimeFacade.getCurrentStringTime();//凭证日期
//	private String taxorgcode="2";//征收机关代码征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444)
//	private String reportkind="1";//报表类型1-日报 2-月报 3-年报
//	private String budgettype="1";//预算种类1-预算内2-预算外
//	private String belongflag="0";//辖属标志0-本级，1-全辖，3-全辖非本级
//	private String budgetlevelcode;//预算级次0-不分级次 1-中央2省级3地市级4区县 5乡级 0-共享，1-中央，2-省，3-市，4-县，5-乡，6-地方
//	private String billkind;//报表种类0-不分报表种类1-正常期预算收入报表2-正常期退库报表体3-正常期调拨收入报表4-总额分成报表体5-调整期预算收入报表体6-调整期退库报表体7-调整期调拨收入报表体
//	//1-正常期预算收入报表2-正常期退库报表体3-正常期调拨收入报表4-总额分成报表体									5-调整期预算收入报表体6-调整期退库报表体7-调整期调拨收入报表体
//	//1-正常期预算收入报表体，2-正常期退库报表体，3-正常期调拨收入报表体，4-总额分成报表体，5-正常期共享分成报表体，6-调整期预算收入报表体，7-调整期退库报表体，8-调整期调拨收入报表体，9-调整期共享分成报表体
    public VoucherReportBean() {
      super();
      dto = new TvVoucherinfoDto();
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      if (loginInfo.getPublicparam().indexOf(",jilin,")!= -1) {
		setJiLinArea(true);
	}
      checkList=new ArrayList();
      paramMap = new HashMap();
//      dto.setScreatdate(TimeFacade.getCurrentStringTime());
      dto.setSorgcode(loginInfo.getSorgcode());
      pagingcontext = new PagingContext(this); 
//      voucherTypeList= new itferesourcepackage.VoucherReportEnumFactory().getEnums(null);
      dto.setSvtcode(vouchertype);//凭证类型3404-全省库存日报表，3401-收入日报表，3402-库存日报表,3567-预算收入月报表
//      dto.setSattach("1");//报表类型1-日报 2-月报 3-年报
      dto.setScheckdate(TimeFacade.getCurrentStringTime());//报表日期
      dto.setShold1(MsgConstant.BDG_KIND_IN);//预算种类
      dto.setShold3(MsgConstant.RULE_SIGN_ALL);//辖属标志0-本级，1-全辖，3-全辖非本级
      dto.setSpaybankcode("1");//征收机关代码征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444)
      dto.setScheckvouchertype("1");//报表种类1-正常期预算收入报表
      TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
	  tsTreasuryDto.setSorgcode(loginInfo.getSorgcode());
	  List<TsTreasuryDto> list = null;
	  try {
		  list = (List<TsTreasuryDto>)commonDataAccessService.findRsByDto(tsTreasuryDto);
	  } catch (ITFEBizException e){
		  log.error(e);
	  }
	  if(list!=null && list.size() > 0){
		  dto.setStrecode(list.get(0).getStrecode());//国库代码
		  strecode = dto.getStrecode();
	  }
      dto.setShold2("");//预算级次0-不分级次 1-中央2省级3地市级4区县 5乡级 0-共享，1-中央，2-省，3-市，4-县，5-乡，6-地方
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
    	if(vouchertype==null||vouchertype.equals("")){
    		MessageDialog.openMessageDialog(null, "凭证类型不可为空！");
    		return "";
    	}
    	if(dto.getStrecode()==null||dto.getStrecode().equals("")){
    		MessageDialog.openMessageDialog(null, "国库代码不可为空！");
    		return "";
    	}
    	if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
    	{
    		if(dto.getScheckdate()==null||dto.getScheckdate().equals("")){
	    		MessageDialog.openMessageDialog(null, "报表年、月不可为空！");
	    		return "";
	    	}else if(dto.getScheckdate().length()!=6)
	    	{
	    		MessageDialog.openMessageDialog(null, "报表年、月格式为6位(年份4位,月份两位)！");
	    		return "";
	    	}
    	}else
    	{
	    	if(dto.getScheckdate()==null||dto.getScheckdate().equals("")){
	    		MessageDialog.openMessageDialog(null, "报表日期不可为空！");
	    		return "";
	    	}
    	}
    	if(MsgConstant.VOUCHER_NO_3404.equals(vouchertype)|| MsgConstant.VOUCHER_NO_3453.equals(vouchertype))
    	{
//	    	if(dto.getSpaybankcode()==null||dto.getSpaybankcode().equals("")){
//	    		MessageDialog.openMessageDialog(null, "征收机关不可为空！");
//	    		return "";
//	    	}
//	    	if(dto.getStrecode()==null||dto.getStrecode().equals("")){
//	    		MessageDialog.openMessageDialog(null, "国库代码不可为空！");
//	    		return "";
//	    	}
	    	TsTreasuryDto tDto=new TsTreasuryDto();
			tDto.setSorgcode(loginInfo.getSorgcode());
			tDto.setStrecode(loginInfo.getSorgcode().substring(0,10));				
			try {
				List treList = commonDataAccessService.findRsByDto(tDto);
				if(treList!=null&&treList.size()>0)
				{
					tDto = (TsTreasuryDto)treList.get(0);
					if(!"2".equals(tDto.getStrelevel()) && !("000002100003".equals(commonDataAccessService.getSrcNode())))
					{
						MessageDialog.openMessageDialog(null, "此功能只有省本级核算主体使用！");
				    	return "";
					}
				}else
				{
					MessageDialog.openMessageDialog(null, "此功能只有省本级核算主体使用！");
			    	return "";
				}
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "判断是否省级国库异常！");
		    	return "";
			}
    	}
    	if(MsgConstant.VOUCHER_NO_3401.equals(vouchertype)||MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
    	{
//    		if(dto.getSattach()==null||dto.getSattach().equals("")){
//        		MessageDialog.openMessageDialog(null, "报表类型不可为空！");
//        		return "";
//        	}
    		if(dto.getShold2()!=null&&"6".equals(dto.getShold2()))
    		{
    			dto.setShold1(null);//预算种类
    			dto.setScheckvouchertype(null);//报表种类
    			dto.setShold3(MsgConstant.RULE_SIGN_ALL);//辖属标志
    			dto.setSpaybankcode("000000000000");//征收机关
    		}else
    		{
    			if(dto.getShold1()==null||dto.getShold1().equals("")){
            		MessageDialog.openMessageDialog(null, "预算种类不可为空！");
            		return "";
            	}
            	if(dto.getShold3()==null||dto.getShold3().equals("")){
            		MessageDialog.openMessageDialog(null, "辖属标志不可为空！");
            		return "";
            	}else if("3".equals(dto.getShold3()))
            	{
            		MessageDialog.openMessageDialog(null, "辖属标志不支持全辖非本级！");
            		return "";
            	}
            	if(dto.getScheckvouchertype()==null||"".equals(dto.getScheckvouchertype()))
            	{
            		MessageDialog.openMessageDialog(null, "报表种类不可为空！");
            		return "";
            	}else if(dto.getScheckvouchertype()!=null&&("5".equals(dto.getScheckvouchertype())||"9".equals(dto.getScheckvouchertype())))
            	{
            		MessageDialog.openMessageDialog(null, "报表种类不支持共享分成报表！");
            		return "";
            	}
            	if(dto.getShold2()!=null&&("0".equals(dto.getShold2())))//||"6".equals(dto.getShold2())
            	{
            		MessageDialog.openMessageDialog(null, "预算级次不支持共享！");
            		return "";
            	}
    		}
        	
    	}
    	dto.setSvtcode(vouchertype);
    	List<TvVoucherinfoDto> list=new ArrayList<TvVoucherinfoDto>();
    	int count=0;
		List<TsTreasuryDto> tList=new ArrayList<TsTreasuryDto>();
		TvVoucherinfoDto vDto=new TvVoucherinfoDto();
		try {
			TsTreasuryDto tDto=new TsTreasuryDto();
			tDto.setSorgcode(loginInfo.getSorgcode());
			if(MsgConstant.VOUCHER_NO_3404.equals(dto.getSvtcode()) || MsgConstant.VOUCHER_NO_3453.equals(dto.getSvtcode()))
			{
				tDto.setStrecode(loginInfo.getSorgcode().substring(0,10));				
				tList.add(tDto);
			}else
			{
				if(dto.getStrecode()==null||dto.getStrecode().equals("")){				
					tList=commonDataAccessService.findRsByDto(tDto);
				}else{
					tDto.setStrecode(dto.getStrecode());				
					tList.add(tDto);
				}
			}
			for(TsTreasuryDto tsDto:tList){
				vDto=new TvVoucherinfoDto();
		    	vDto.setSorgcode(loginInfo.getSorgcode());
				vDto.setScheckdate(dto.getScheckdate());//报表日期
				vDto.setSvtcode(vouchertype);
				vDto.setStrecode(tsDto.getStrecode());
				if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3401)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567))
				{
					if(dto.getShold2()!=null&&"6".equals(dto.getShold2()))
		    		{
						vDto.setShold1(null);//预算种类
		    			vDto.setShold2(dto.getShold2());//预算级次
		    			vDto.setScheckvouchertype(null);//报表种类
		    			vDto.setShold3(MsgConstant.RULE_SIGN_ALL);//辖属标志
		    			vDto.setSpaybankcode("000000000000");//征收机关
		    			vDto.setSattach(dto.getSattach());//报表类型
		    		}else
		    		{
						vDto.setSattach(dto.getSattach());//报表类型
						vDto.setShold1(dto.getShold1());//预算种类
						vDto.setShold3(dto.getShold3());//辖属标志
						vDto.setSpaybankcode(dto.getSpaybankcode());//征收机关
						vDto.setShold2(dto.getShold2());//预算级次
						vDto.setScheckvouchertype(dto.getScheckvouchertype());//报表种类
		    		}
				}
				list.add(vDto);
				if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3401)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3404) || vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3453)){
					try{
						if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567))
						{
							if(vDto.getScheckdate()!=null&&vDto.getScheckdate().length()==6)
								vDto.setScheckdate(vDto.getScheckdate()+"01");
							Map<String,String> dateMap = commonDataAccessService.getmapforkey("getEndDateOfMonth-"+loginInfo.getSorgcode()+"-"+vDto.getStrecode()+"-"+vDto.getScheckdate());
				    		if(dateMap!=null&&dateMap.get("getEndDateOfMonth")!=null)
				    			vDto.setScheckdate(dateMap.get("getEndDateOfMonth"));
				    		else
				    			vDto.setScheckdate(TimeFacade.getEndDateOfMonth(dto.getScheckdate()));
						}
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
//    		if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3404)|| vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3453))
//    		{
//    			if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
//        				.getCurrentComposite().getShell(), "提示"," 当前日期："+dto.getScheckdate()+"已生成全省库存日报凭证，确定继续生成吗？")) {
//        			return "";
//        		}
//    		}
//    		else if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402))
//    		{
//        		MessageDialog.openMessageDialog(null, "国库："+vDto.getStrecode()+" 当前日期："+dto.getScheckdate()+"库存日报已生成凭证，不能重复生成凭证！");
//    			refreshTable();
//        		return "";
//    		}
//    		else 
    		if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
    				.getCurrentComposite().getShell(), "提示", "国库："+vDto.getStrecode()+" 当前日期："+dto.getScheckdate()+"已生成凭证，确定继续生成吗？")) {
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
    		if(count==-1){
//    			if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402))
//        		{
//    				MessageDialog.openMessageDialog(null, "国库："+resultList.get(1)+"当前日期："+dto.getScheckdate()+"下库存日报已生成凭证，不能重复生成凭证！");
//        		}
        		MessageDialog.openMessageDialog(null, "报表凭证生成操作成功，成功条数为："+resultList.get(2)+" ！");
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
			MessageDialog.openMessageDialog(null,"超过规定时间未操作业务,会话已失效\r\n请重新登录！");
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
    
    public String getResult3208(List list){
    	if(list==null||list.size()==0){
    		return "当前日期下无凭证数据！";
    	}
    	String repeat=list.get(0)+"";
    	String succCount=list.get(1)+"";
    	if(repeat.equals("0")){
    		return  "凭证生成操作成功，成功条数为："+succCount+" ！";
    	}else if(succCount.equals("0")){
    		return "凭证日期："+dto.getScreatdate()+" 的实拨资金退款已生成凭证，不能重复生成凭证！";
    	}else{
    		return "凭证生成操作成功，成功条数："+succCount+" , 已存在凭证条数："+repeat+"！";
    	}		
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
//    	if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
//    	{
//    		if(dto.getScheckdate()==null||dto.getScheckdate().equals("")){
//	    		MessageDialog.openMessageDialog(null, "报表年、月不可为空！");
//	    		return "";
//	    	}else if(dto.getScheckdate().length()!=6)
//	    	{
//	    		MessageDialog.openMessageDialog(null, "报表年、月格式为6位(年份4位,月份两位)！");
//	    		return "";
//	    	}
//    	}else
//    	{
//	    	if(dto.getScheckdate()==null||dto.getScheckdate().equals("")){
//	    		MessageDialog.openMessageDialog(null, "报表日期不可为空！");
//	    		return "";
//	    	}
//    	}
    	if(MsgConstant.VOUCHER_NO_3404.equals(vouchertype) || MsgConstant.VOUCHER_NO_3453.equals(vouchertype))
    	{
    		String checkdate = new String();
    		checkdate = dto.getScheckdate();
	    	dto = new TvVoucherinfoDto();
	    	dto.setSorgcode(loginInfo.getSorgcode());
	    	dto.setSvtcode(vouchertype);//凭证类型
	    	dto.setStrecode(strecode);
	    	dto.setScheckdate(checkdate);
    		TsTreasuryDto tDto=new TsTreasuryDto();
			tDto.setSorgcode(loginInfo.getSorgcode());
			tDto.setStrecode(loginInfo.getSorgcode().substring(0,10));				
			try {
				List treList = commonDataAccessService.findRsByDto(tDto);
				if(treList!=null&&treList.size()>0)
				{
					tDto = (TsTreasuryDto)treList.get(0);
					if(!"2".equals(tDto.getStrelevel()))
					{
						MessageDialog.openMessageDialog(null, "此功能只有省本级核算主体使用！");
				    	return "";
					}
				}else
				{
					MessageDialog.openMessageDialog(null, "此功能只有省本级核算主体使用！");
			    	return "";
				}
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "判断是否省级国库异常！");
		    	return "";
			}
    	}
    	if(MsgConstant.VOUCHER_NO_3401.equals(vouchertype)||MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
    	{
    		if(dto.getShold2()!=null&&("0".equals(dto.getShold2())))
        	{
        		MessageDialog.openMessageDialog(null, "预算级次不支持共享！");
        		return "";
        	}
//    		if(dto.getSattach()==null||dto.getSattach().equals("")){
//        		MessageDialog.openMessageDialog(null, "报表类型不可为空！");
//        		return "";
//        	}
    		if(dto.getShold2()!=null&&"6".equals(dto.getShold2()))
    		{
    			dto.setShold1(null);//预算种类
    			dto.setScheckvouchertype(null);//报表种类
    			dto.setShold3(MsgConstant.RULE_SIGN_ALL);//辖属标志
    			dto.setSpaybankcode("000000000000");//征收机关
    		}else
    		{
	        	if(dto.getShold1()==null||dto.getShold1().equals("")){
	        		MessageDialog.openMessageDialog(null, "预算种类不可为空！");
	        		return "";
	        	}
	        	if(dto.getShold3()==null||dto.getShold3().equals("")){
	        		MessageDialog.openMessageDialog(null, "辖属标志不可为空！");
	        		return "";
	        	}else if("3".equals(dto.getShold3()))
	        	{
	        		MessageDialog.openMessageDialog(null, "辖属标志不支持全辖非本级！");
	        		return "";
	        	}
	        	if(dto.getScheckvouchertype()!=null&&("5".equals(dto.getScheckvouchertype())||"9".equals(dto.getScheckvouchertype())))
	        	{
	        		MessageDialog.openMessageDialog(null, "报表种类不支持共享分成报表！");
	        		return "";
	        	}
    		}
    	}
    	if(dto.getSvtcode()==null||dto.getSvtcode().equals("")){
    		MessageDialog.openMessageDialog(null, "请先选择凭证类型！");
    		return "";
    	}
    	
    	refreshTable();
        return super.voucherSearch(o);
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
    	for(TvVoucherinfoDto infoDto:checkList){
			if(!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus().trim()))){
				MessageDialog.openMessageDialog(null, "请选择凭证状态为 \"签章成功\" 的记录！");
        		return "";
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
        	ActiveXCompositeVoucherReportOcx.init(0);
        	
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
    							ActiveXCompositeVoucherOcx.sortStringArray(seqs);    							
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
								sinList.add(ActiveXCompositeVoucherReportOcx.getVoucherStamp(vDto, tDto.getScertid(), sDto.getSstampposition(), tDto.getSstampid()));   	    												
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP)){								
								sinList.add(ActiveXCompositeVoucherReportOcx.getVoucherStamp(vDto, tDto.getSrotarycertid(), sDto.getSstampposition(), tDto.getSrotaryid()));   	    												
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH))
						{
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP))
								sinList.add(ActiveXCompositeVoucherReportOcx.getVoucherStamp(vDto, tDto.getSattachcertid(), sDto.getSstampposition(), tDto.getSattachid()));
						}else{
							if(!loginInfo.getPublicparam().contains(",jbrstamp=server,"))
							{
								sinList.add(ActiveXCompositeVoucherReportOcx.getVoucherStamp(vDto, uDto.getScertid(), sDto.getSstampposition(),uDto.getSstampid()));
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
				if(!AdminConfirmDialogFacade.open("B_247", "收入报表凭证", "授权用户"+loginInfo.getSuserName()+"撤销签章", msg)){
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
    							ActiveXCompositeVoucherOcx.sortStringArray(seqs);
    							
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
        /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	dto.setSvtcode(vouchertype);
    	PageResponse page = null;
    	TvVoucherinfoDto vDto=null;
    	TvVoucherinfoDto sDto=null;
    	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402))
    	{
	    	vDto = new TvVoucherinfoDto();
	    	vDto.setSorgcode(loginInfo.getSorgcode());
			vDto.setScheckdate(dto.getScheckdate());//报表日期
			vDto.setSvtcode(vouchertype);
			vDto.setStrecode(dto.getStrecode());
			vDto.setSstatus(dto.getSstatus());
    	}
    	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3404)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3453))
    	{
	    	sDto = new TvVoucherinfoDto();
	    	sDto.setSorgcode(loginInfo.getSorgcode());
	    	sDto.setScheckdate(dto.getScheckdate());//报表日期
	    	sDto.setSvtcode(vouchertype);
	    	sDto.setSstatus(dto.getSstatus());
    	}
//    	try {
//	    	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567))
//	    	{
//	    		if(dto.getScheckdate()!=null&&dto.getScheckdate().length()==6)
//	    			dto.setScheckdate(dto.getScheckdate()+"01");
//	    		Map<String,String> dateMap = commonDataAccessService.getmapforkey("getEndDateOfMonth-"+loginInfo.getSorgcode()+"-"+dto.getStrecode()+"-"+dto.getScheckdate());
//	    		if(dateMap!=null&&dateMap.get("getEndDateOfMonth")!=null)
//	    			dto.setScheckdate(dateMap.get("getEndDateOfMonth"));
//	    		else
//	    			dto.setScheckdate(TimeFacade.getEndDateOfMonth(dto.getScheckdate()));
//	    	}
//    	}catch(Exception e)
//    	{
//    		log.error(e);
//			MessageDialog.openMessageDialog(null, "日期非法！");
//			return null;
//    	}
    	try {
    		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402))
    		{
    			if(loginInfo.getPublicparam().contains((",vouchersearchold=5,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -5);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(vDto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+vDto.getSorgcode()+(vDto.getStrecode()==null?"":"' and S_TRECODE='"+vDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+vDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else if(loginInfo.getPublicparam().contains((",vouchersearchold=15,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -15);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(vDto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+vDto.getSorgcode()+(vDto.getStrecode()==null?"":"' and S_TRECODE='"+vDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+vDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else
    			{
    				page =  commonDataAccessService.findRsByDtoPaging(vDto,pageRequest, "1=1"," TS_SYSUPDATE desc");
    			}
    		}
    		else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3401)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567))
    		{
    			if(dto.getShold2()!=null&&"6".equals(dto.getShold2()))
	    		{
					dto.setShold1(null);//预算种类
//	    			vDto.setShold2(dto.getShold2());//预算级次
					dto.setScheckvouchertype(null);//报表种类
					dto.setShold3(MsgConstant.RULE_SIGN_ALL);//辖属标志
					dto.setSpaybankcode("000000000000");//征收机关
	    		}
    			if(loginInfo.getPublicparam().contains((",vouchersearchold=5,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -5);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(dto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+dto.getSorgcode()+(dto.getStrecode()==null?"":"' and S_TRECODE='"+dto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+dto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else if(loginInfo.getPublicparam().contains((",vouchersearchold=15,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -15);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(dto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+dto.getSorgcode()+(dto.getStrecode()==null?"":"' and S_TRECODE='"+dto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+dto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else
    			{
    				page =  commonDataAccessService.findRsByDtoPaging(dto,pageRequest, "1=1", " TS_SYSUPDATE desc");
    			}
    			if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567))
    			{
    				if(dto!=null&&dto.getScheckdate()!=null&&dto.getScheckdate().length()>6)
    					dto.setScheckdate(dto.getScheckdate().substring(0,6));
    			}
    		}
    		else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3410))
    		{
    			TvVoucherinfoDto zdto = new TvVoucherinfoDto();
    			zdto.setSvtcode(dto.getSvtcode());
    			zdto.setStrecode(dto.getStrecode());
    			zdto.setScreatdate(dto.getScreatdate());
    			zdto.setScheckvouchertype(dto.getScheckvouchertype());
    			zdto.setShold1(dto.getShold1());
    			zdto.setSstatus(dto.getSstatus());
    			if(loginInfo.getPublicparam().contains((",vouchersearchhold=5,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -5);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(zdto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+sDto.getSorgcode()+(sDto.getStrecode()==null?"":"' and S_TRECODE='"+sDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+sDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else if(loginInfo.getPublicparam().contains((",vouchersearchhold=15,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -15);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(zdto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+sDto.getSorgcode()+(sDto.getStrecode()==null?"":"' and S_TRECODE='"+sDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+sDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else
    			{
    				page =  commonDataAccessService.findRsByDtoPaging(zdto,pageRequest, "1=1", " TS_SYSUPDATE desc");
    			}
    		}else
    		{
    			if(loginInfo.getPublicparam().contains((",vouchersearchhold=5,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -5);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(dto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+sDto.getSorgcode()+(sDto.getStrecode()==null?"":"' and S_TRECODE='"+sDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+sDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else if(loginInfo.getPublicparam().contains((",vouchersearchhold=15,")))
    			{
    				Calendar calendar = Calendar.getInstance();
    				calendar.setTime(TimeFacade.getCurrentDate());
    				calendar.add(Calendar.DATE, -15);
    				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
    				String getdate = dateFormat.format(calendar.getTime());
    				page =  commonDataAccessService.findRsByDtoPaging(dto,pageRequest, "1=1 AND S_PAYBANKCODE IS NOT NULL OR (S_ORGCODE='"+sDto.getSorgcode()+(sDto.getStrecode()==null?"":"' and S_TRECODE='"+sDto.getStrecode())+"' and  S_CREATDATE>='"+getdate+"' and S_VTCODE='"+sDto.getSvtcode()+"' and S_STATUS!='"+DealCodeConstants.VOUCHER_READRETURN+"')", " TS_SYSUPDATE desc");
    			}else
    			{
    				page =  commonDataAccessService.findRsByDtoPaging(dto,pageRequest, "1=1", " TS_SYSUPDATE desc");
    			}
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
		if(voucherTypeList==null||voucherTypeList.size()<=0)
		{
			voucherTypeList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.VOUCHER_NO_3401, "收入日报表");
			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3567, "收入月报表");
			Mapper map2 = new Mapper(MsgConstant.VOUCHER_NO_3402, "库存日报表");
			Mapper map3; 
			if (isJiLinArea) {
				 map3 = new Mapper(MsgConstant.VOUCHER_NO_3453, "全省库存日报表");
			}else {
				 map3 = new Mapper(MsgConstant.VOUCHER_NO_3404, "全省库存日报表");
			}
			
			voucherTypeList.add(map);
			voucherTypeList.add(map1);
			voucherTypeList.add(map2);
			voucherTypeList.add(map3);
			if(loginInfo.getPublicparam().contains(",zc=true,"))
			{
				Mapper map4 = new Mapper(MsgConstant.VOUCHER_NO_3410, "支出日报表");
				voucherTypeList.add(map4);
			}
		}
		return voucherTypeList;
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
		PageResponse page = new PageResponse();
		if(dto.getScheckdate()==null)
			 dto.setScheckdate(TimeFacade.getCurrentStringTime());//报表日期
		if(!MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
		{
			if(dto.getScheckdate().length()<8)
				dto.setScheckdate(TimeFacade.getCurrentStringTime());//报表日期
		}
		if(MsgConstant.VOUCHER_NO_3410.equals(vouchertype))
			dto.setScreatdate(TimeFacade.getCurrentStringTime());
		else
			dto.setScreatdate(null);
		if(MsgConstant.VOUCHER_NO_3402.equals(vouchertype))
		{
			List contreaNames = new ArrayList();
			contreaNames.add("收入日报生成条件");
			contreaNames.add("全省库存日报生成条件");
			contreaNames.add("收入月报生成条件");
			contreaNames.add("收入日报查询结果一览表");
			contreaNames.add("地方级收入月报生成条件");
			contreaNames.add("地方级收入日报生成条件");
			contreaNames.add("地方月报查询结果一览表");
			contreaNames.add("支出报表生成条件");
			contreaNames.add("支出报表查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			dto.setShold1(null);//预算种类
		    dto.setShold3(null);//辖属标志0-本级，1-全辖，3-全辖非本级
		    dto.setSpaybankcode(null);//征收机关代码征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444)
		    dto.setScheckvouchertype(null);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("库存日报生成条件");
			contreaNames1.add("库存日报查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
			checkList.clear();
			pagingcontext.setPage(page);
		}else if(MsgConstant.VOUCHER_NO_3401.equals(vouchertype))
		{
//			dto.setSattach("1");//设置为类型为日报
			List contreaNames = new ArrayList();
			if("6".equals(dto.getShold2()))
			{
				contreaNames.add("地方级收入日报生成条件");
				contreaNames.add("地方月报查询结果一览表");
			}
			else
			{
				contreaNames.add("收入日报生成条件");
				contreaNames.add("收入日报查询结果一览表");
			}
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("库存日报生成条件");
			contreaNames1.add("收入月报生成条件");
			contreaNames1.add("全省库存日报生成条件");
			contreaNames1.add("库存日报查询结果一览表");
			contreaNames1.add("地方级收入月报生成条件");
			if("6".equals(dto.getShold2()))
			{
				contreaNames1.add("收入日报生成条件");
				contreaNames1.add("收入日报查询结果一览表");
			}else
			{
				contreaNames1.add("地方级收入日报生成条件");
				contreaNames1.add("地方月报查询结果一览表");
			}
			contreaNames1.add("支出报表生成条件");
			contreaNames1.add("支出报表查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
			checkList.clear();
			pagingcontext.setPage(page);
		}else if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
		{
//			dto.setSattach("2");//设置为类型为月报
			if(dto!=null&&dto.getScheckdate()!=null&&dto.getScheckdate().length()>6)
				dto.setScheckdate(dto.getScheckdate().substring(0,6));
			List contreaNames = new ArrayList();
			if("6".equals(dto.getShold2()))
			{
				contreaNames.add("地方级收入月报生成条件");
				contreaNames.add("地方月报查询结果一览表");
			}else
			{
				contreaNames.add("收入月报生成条件");
				contreaNames.add("收入日报查询结果一览表");
			}
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("库存日报生成条件");
			contreaNames1.add("收入日报生成条件");
			contreaNames1.add("全省库存日报生成条件");
			contreaNames1.add("库存日报查询结果一览表");
			contreaNames1.add("地方级收入日报生成条件");
			if("6".equals(dto.getShold2()))
			{
				contreaNames1.add("收入月报生成条件");
				contreaNames1.add("收入日报查询结果一览表");
			}else
			{
				contreaNames1.add("地方级收入月报生成条件");
				contreaNames1.add("地方月报查询结果一览表");
			}
			contreaNames1.add("支出报表生成条件");
			contreaNames1.add("支出报表查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
			checkList.clear();
			pagingcontext.setPage(page);
		}else if(MsgConstant.VOUCHER_NO_3404.equals(vouchertype)||MsgConstant.VOUCHER_NO_3453.equals(vouchertype))
		{
			TsTreasuryDto tDto=new TsTreasuryDto();
			tDto.setSorgcode(loginInfo.getSorgcode());
			tDto.setStrecode(loginInfo.getSorgcode().substring(0,10));				
			try {
				List treList = commonDataAccessService.findRsByDto(tDto);
				if(treList!=null&&treList.size()>0)
				{
					tDto = (TsTreasuryDto)treList.get(0);
					if(!"2".equals(tDto.getStrelevel()) && !("000002100003".equals(commonDataAccessService.getSrcNode())))
					{
						MessageDialog.openMessageDialog(null, "此功能只有省本级核算主体使用！");
			    		return;
					}
				}else
				{
					MessageDialog.openMessageDialog(null, "此功能只有省本级核算主体使用！");
		    		return;
				}
			} catch (ITFEBizException e) {
				MessageDialog.openMessageDialog(null, "判断是否省级国库异常！");
	    		return;
			}
			List contreaNames = new ArrayList();
			contreaNames.add("收入日报生成条件");
			contreaNames.add("库存日报生成条件");
			contreaNames.add("收入月报生成条件");
			contreaNames.add("收入日报查询结果一览表");
			contreaNames.add("地方级收入月报生成条件");
			contreaNames.add("地方月报查询结果一览表");
			contreaNames.add("地方级收入日报生成条件");
			contreaNames.add("支出报表生成条件");
			contreaNames.add("支出报表查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("全省库存日报生成条件");
			contreaNames1.add("库存日报查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
			checkList.clear();
			pagingcontext.setPage(page);
		}else if(MsgConstant.VOUCHER_NO_3410.equals(vouchertype))
		{
			List contreaNames = new ArrayList();
			contreaNames.add("收入日报生成条件");
			contreaNames.add("库存日报生成条件");
			contreaNames.add("收入月报生成条件");
			contreaNames.add("收入日报查询结果一览表");
			contreaNames.add("地方级收入月报生成条件");
			contreaNames.add("地方月报查询结果一览表");
			contreaNames.add("地方级收入日报生成条件");
			contreaNames.add("全省库存日报生成条件");
			contreaNames.add("库存日报查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("支出报表生成条件");
			contreaNames1.add("支出报表查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
			checkList.clear();
			pagingcontext.setPage(page);
		}
		this.dto.setSvtcode(vouchertype);
		setYsjc(null);
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
		PageResponse page = new PageResponse();
		checkList.clear();
		pagingcontext.setPage(page);
		if(ysjc==null||"".equals(ysjc))
			return;
		if("6".equals(ysjc))//判断预算级次为地方
		{
			if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
			{
				if(dto!=null&&dto.getScheckdate()!=null&&dto.getScheckdate().length()>6)
					dto.setScheckdate(dto.getScheckdate().substring(0,6));
			}else
			{
				dto.setScheckdate(TimeFacade.getCurrentStringTime());
			}
			List contreaNames = new ArrayList();
			if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
				contreaNames.add("地方级收入月报生成条件");
			else
				contreaNames.add("地方级收入日报生成条件");
			contreaNames.add("地方月报查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("收入月报生成条件");
			contreaNames1.add("库存日报生成条件");
			contreaNames1.add("收入日报生成条件");
			contreaNames1.add("全省库存日报生成条件");
			contreaNames1.add("库存日报查询结果一览表");
			contreaNames1.add("收入日报查询结果一览表");
			if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
				contreaNames1.add("地方级收入日报生成条件");
			else
				contreaNames1.add("地方级收入月报生成条件");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
			
		}else
		{
			List contreaNames = new ArrayList();
			if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
				contreaNames.add("收入月报生成条件");
			else
				contreaNames.add("收入日报生成条件");
			contreaNames.add("收入日报查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("地方级收入月报生成条件");
			contreaNames1.add("库存日报生成条件");
			if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
				contreaNames1.add("收入日报生成条件");
			else
				contreaNames1.add("收入月报生成条件");
			contreaNames1.add("地方级收入日报生成条件");
			contreaNames1.add("地方级收入月报生成条件");
			contreaNames1.add("全省库存日报生成条件");
			contreaNames1.add("库存日报查询结果一览表");
			contreaNames1.add("地方月报查询结果一览表");
			if(MsgConstant.VOUCHER_NO_3567.equals(vouchertype))
				contreaNames1.add("收入日报生成条件");
			else
				contreaNames1.add("收入月报生成条件");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
		}
		refreshStampType();
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}
	public void setJiLinArea(boolean isJiLinArea) {
		this.isJiLinArea = isJiLinArea;
	}
	public boolean isJiLinArea() {
		return isJiLinArea;
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
			List<TvVoucherinfoDto> queryList = commonDataAccessService
					.findRsByDto(dto);
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
		taxmap = new Mapper("1","不分");
		taxtrelist.add(taxmap);
		taxmap = new Mapper("0","财政大类");
		taxtrelist.add(taxmap);
		taxmap = new Mapper("2","本级不分征收机关");
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
				List<TsTaxorgDto> queryList = commonDataAccessService.findRsByDto(taxdto," order by s_taxorgcode ");
				if(queryList!=null&&queryList.size()>0)
				{
					for(TsTaxorgDto temp:queryList)
					{
						if(temp.getStaxorgname()!=null&&!"".equals(temp.getStaxorgname()))
							taxmap = new Mapper(temp.getStaxorgcode(),temp.getStaxorgname());
						else
							taxmap = new Mapper(temp.getStaxorgcode(),temp.getStaxorgcode());
						taxtrelist.add(taxmap);
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
}
package com.cfcc.itfe.client.para.reportautogen;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsAutogenervoucherDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;

/**
 * codecomment: 
 * @author zhangliang
 * @time   15-11-11 10:12:15
 * 子系统: Para
 * 模块:reportautogen
 * 组件:Reportautogen
 */
public class ReportautogenBean extends AbstractReportautogenBean implements IPageDataProvider {
	private String ysjc;
	private String vouchertype=MsgConstant.VOUCHER_NO_3401;//凭证类型3404-全省库存日报表(吉林采用3453)，3401-收入日报表，3402-库存日报表 3567 收入月报
    private static Log log = LogFactory.getLog(ReportautogenBean.class);
    boolean issearch = true;
    private TsAutogenervoucherDto dto = null;
    private ITFELoginInfo loginInfo;
    private List voucherTypeList=null;
    List<TsAutogenervoucherDto> checkList=null;
    private List taxtrelist = null;
    protected ICommonDataAccessService commonDataAccessService = (ICommonDataAccessService)getService(ICommonDataAccessService.class);
    public ReportautogenBean() {
      super();
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      dto = new TsAutogenervoucherDto();
      dto.setSorgcode(loginInfo.getSorgcode());
      checkList=new ArrayList<TsAutogenervoucherDto>();
      pagingcontext = new PagingContext(this); 
      dto.setSvtcode(vouchertype);//凭证类型3404-全省库存日报表，3401-收入日报表，3402-库存日报表,3567-预算收入月报表
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
	  }
      dto.setShold2("");//预算级次0-不分级次 1-中央2省级3地市级4区县 5乡级 0-共享，1-中央，2-省，3-市，4-县，5-乡，6-地方
      dto.setSvtcode(vouchertype);
      PageRequest pageRequest = new PageRequest();
      PageResponse pageResponse=new PageResponse();
      pageResponse = retrieve(pageRequest);
      pagingcontext.setPage(pageResponse);
    }
    /**
	 * Direction: 查询
	 * ename: searchdata
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String searchdata(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		issearch = false;
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录，请先生成凭证！");
		}
		pagingcontext.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.searchdata(o);
    }
    
	/**
	 * Direction: 保存
	 * ename: savedata
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String savedata(Object o){
    	if(vouchertype==null||vouchertype.equals("")){
    		MessageDialog.openMessageDialog(null, "凭证类型不可为空！");
    		return "";
    	}
    	if(dto.getStrecode()==null||dto.getStrecode().equals("")){
    		MessageDialog.openMessageDialog(null, "国库代码不可为空！");
    		return "";
    	}
    	if(MsgConstant.VOUCHER_NO_3404.equals(vouchertype)|| MsgConstant.VOUCHER_NO_3453.equals(vouchertype))
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
            	if(dto.getShold2()==null||"".equals(dto.getShold2()))
            	{
            		MessageDialog.openMessageDialog(null, "预算级次不可为空！");
            		return "";
            	}
    		}
        	
    	}
    	dto.setSvtcode(vouchertype);
		try {
			
			List dtoList = commonDataAccessService.findRsByDto(dto);
			if(dtoList!=null&&dtoList.size()>0)
			{
				MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "国库当前条件自动生成数据已经存在,同样的条件不可重复保存！");
				return "";
			}
			String ieq = "00000000"+commonDataAccessService.getSequenceNo("TRAID_SEQ");
			dto.setSdealno(TimeFacade.getCurrentStringTime()+ieq.substring(ieq.length()-8));
			TsConvertfinorgDto cDto=new TsConvertfinorgDto();
			cDto.setSorgcode(dto.getSorgcode());
			cDto.setStrecode(dto.getStrecode());
			List cList = commonDataAccessService.findRsByDto(cDto);
			if(cList!=null&&cList.size()>0)
				dto.setSadmdivcode(((TsConvertfinorgDto)cList.get(0)).getSadmdivcode());
			else
				dto.setSadmdivcode("0000000000");
			dto.setSvoucherno(ieq);
			dto.setSfilename("autoreport");
			dto.setSvoucherflag("1");
			dto.setNmoney(new BigDecimal(66666));
			dto.setScreatdate(TimeFacade.getCurrentStringTime());
			dto.setScheckdate(TimeFacade.getCurrentStringTime());
			dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
			dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0,4));
			commonDataAccessService.create(dto);
			MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "保存成功！");
			dto = new TsAutogenervoucherDto();
		      dto.setSorgcode(loginInfo.getSorgcode());
		      checkList=new ArrayList<TsAutogenervoucherDto>();
		      pagingcontext = new PagingContext(this); 
		      dto.setSvtcode(vouchertype);//凭证类型3404-全省库存日报表，3401-收入日报表，3402-库存日报表,3567-预算收入月报表
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
			  }
		      dto.setShold2("");//预算级次0-不分级次 1-中央2省级3地市级4区县 5乡级 0-共享，1-中央，2-省，3-市，4-县，5-乡，6-地方
		      dto.setSvtcode(vouchertype);
			PageRequest pageRequest = new PageRequest();
			PageResponse pageResponse=new PageResponse();
			issearch = true;
			pageResponse = retrieve(pageRequest);
			pagingcontext.setPage(pageResponse);
			editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
		}catch(java.lang.IndexOutOfBoundsException e){
			Exception e1=new Exception("生成凭证操作出现异常！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("生成凭证操作出现异常！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
			return "";
		}
        return super.savedata(o);
    }
	/**
	 * Direction: 删除
	 * ename: deletedata
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String deletedata(Object o){
    	try {
	        if(checkList!=null&&checkList.size()>0)
	        {
	        	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
	    				.getCurrentComposite().getShell(), "提示", "你确定要删除选中的数据吗？")) {
	    			return "";
	    		}
	        	for(TsAutogenervoucherDto tempdto : checkList)
						commonDataAccessService.delete(tempdto);
	        	PageRequest pageRequest = new PageRequest();
				PageResponse pageResponse=new PageResponse();
				issearch = true;
				pageResponse = retrieve(pageRequest);
				pagingcontext.setPage(pageResponse);
				editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	        }else
	        {
	        	MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "请选中需要删除的数据！");
	        }
    	} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}
        return super.deletedata(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	checkList=new ArrayList<TsAutogenervoucherDto>();
    	dto.setSvtcode(vouchertype);
    	PageResponse page = null;
    	TsAutogenervoucherDto vDto = new TsAutogenervoucherDto();
    	TsAutogenervoucherDto sDto = new TsAutogenervoucherDto();
    	try {
    		if(issearch)
        	{
    	    	vDto.setSorgcode(loginInfo.getSorgcode());
    	    	vDto.setSvtcode(dto.getSvtcode());
    	    	page =  commonDataAccessService.findRsByDtoWithWherePaging(vDto,pageRequest, "1=1");
    	    	return page;
        	}
        	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402))
        	{
    	    	vDto.setSorgcode(loginInfo.getSorgcode());
    			vDto.setSvtcode(vouchertype);
    			vDto.setStrecode(dto.getStrecode());
        	}
        	if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3404)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3453))
        	{
    	    	sDto = new TsAutogenervoucherDto();
    	    	sDto.setSorgcode(loginInfo.getSorgcode());
    	    	sDto.setSvtcode(vouchertype);
        	}
    		if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3401)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3567))
    		{
    			if(dto.getShold2()!=null&&"6".equals(dto.getShold2()))
	    		{
					dto.setShold1(null);//预算种类
//	    			vDto.setShold2(dto.getShold2());//预算级次
					dto.setScheckvouchertype(null);//报表种类
					dto.setShold3(MsgConstant.RULE_SIGN_ALL);//辖属标志
					dto.setSpaybankcode("000000000000");//征收机关
	    		}else
	    		{
	    			vDto.setSvtcode(dto.getSvtcode());
	    			vDto.setStrecode(dto.getStrecode());
	    			vDto.setShold1(dto.getShold1());
	    			vDto.setShold2(dto.getShold2());
	    			vDto.setScheckvouchertype(dto.getScheckvouchertype());
	    			vDto.setShold3(dto.getShold3());
	    			vDto.setSpaybankcode(dto.getSpaybankcode());
	    			vDto.setSisauto(dto.getSisauto());
	    		}
    		}
    		page =  commonDataAccessService.findRsByDtoWithWherePaging(vDto,pageRequest, "1=1");
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
	public String getYsjc() {
		return ysjc;
	}
	public void setYsjc(String ysjc) {
		this.ysjc = ysjc;
		dto.setShold2(ysjc);
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
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
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
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			dto.setShold1(null);//预算种类
		    dto.setShold3(null);//辖属标志0-本级，1-全辖，3-全辖非本级
		    dto.setSpaybankcode(null);//征收机关代码征收机关代码1-不分，2-本级财政征收机关，0-财政大类(444444444444)
		    dto.setScheckvouchertype(null);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("库存日报生成条件");
			contreaNames1.add("库存日报查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
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
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
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
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
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
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("全省库存日报生成条件");
			contreaNames1.add("库存日报查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
		}
		this.dto.setSvtcode(vouchertype);
		setYsjc(null);
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}
	public List getVoucherTypeList() {
		if(voucherTypeList==null||voucherTypeList.size()<=0)
		{
			voucherTypeList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.VOUCHER_NO_3401, "收入日报表");
//			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3567, "收入月报表");
			Mapper map2 = new Mapper(MsgConstant.VOUCHER_NO_3402, "库存日报表");
//			Mapper map3 = new Mapper(MsgConstant.VOUCHER_NO_3404, "全省库存日报表");; 
			voucherTypeList.add(map);
//			voucherTypeList.add(map1);
			voucherTypeList.add(map2);
//			voucherTypeList.add(map3);
		}
		return voucherTypeList;
	}

	public void setVoucherTypeList(List voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}
	public TsAutogenervoucherDto getDto() {
		return dto;
	}
	public void setDto(TsAutogenervoucherDto dto) {
		this.dto = dto;
	}
	public PagingContext getPagingcontext() {
		return pagingcontext;
	}
	public void setPagingcontext(PagingContext pagingcontext) {
		this.pagingcontext = pagingcontext;
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
	public List<TsAutogenervoucherDto> getCheckList() {
		return checkList;
	}
	public void setCheckList(List<TsAutogenervoucherDto> checkList) {
		this.checkList = checkList;
	}
}
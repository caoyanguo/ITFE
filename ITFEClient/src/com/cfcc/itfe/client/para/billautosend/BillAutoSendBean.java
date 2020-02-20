package com.cfcc.itfe.client.para.billautosend;

import java.util.*;
import java.io.*;
import java.lang.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;
import org.eclipse.swt.widgets.Display;

import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.para.billautosend.IBillAutoSendService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsAutogenervoucherDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsBillautosendDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvUsersconditionDto;

/**
 * codecomment: 
 * @author Administrator
 * @time   14-08-06 14:44:29
 * 子系统: Para
 * 模块:billAutoSend
 * 组件:BillAutoSend
 */
public class BillAutoSendBean extends AbstractBillAutoSendBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(BillAutoSendBean.class);
    ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
    private TsBillautosendDto searchDto;
    private List taxtrelist = null;
    List<TsAutogenervoucherDto> checkList=null;   
    private Map<String,String> hashmap = null;
    private Map<String,String> maphash = null;
    boolean issearch = true;
    private String strecode = null;
    private String vouvtcode=MsgConstant.VOUCHER_NO_3405;//报表类型3405-总额分成,3406-两税返还
	private String shold4="8888888888";//科目代码
	private List voucherTypeList = null;
	private PagingContext vpaging = null;
    public BillAutoSendBean() {
      super();
      voudto = new TsAutogenervoucherDto();
      dto = new TsBillautosendDto();
      dto.setSorgcode(loginfo.getSorgcode());
      searchDto = new TsBillautosendDto();
      searchDto.setSorgcode(loginfo.getSorgcode());
      pagingcontext = new PagingContext(this);
      checkList=new ArrayList();
      vpaging = new PagingContext(this);
      TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
	  tsTreasuryDto.setSorgcode(loginfo.getSorgcode());
	  voudto.setSorgcode(loginfo.getSorgcode());
	  voudto.setShold1(MsgConstant.BUDGET_TYPE_IN_BDG);
	  voudto.setSvtcode(vouvtcode);
	  voudto.setShold4(shold4);
	  voudto.setShold2(MsgConstant.TIME_FLAG_NORMAL);
	  List<TsTreasuryDto> list = null;
	  try {
		  list = (List<TsTreasuryDto>)commonDataAccessService.findRsByDto(tsTreasuryDto);
	  } catch (ITFEBizException e){
		  log.error(e);
	  }
	  if(list!=null && list.size() > 0){
		  voudto.setStrecode(list.get(0).getStrecode());//国库代码
	  }
      init();           
    }
    
	/**
	 * Direction: 跳转录入界面
	 * ename: goInput
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String goInput(Object o){
    	dto = new TsBillautosendDto();
        dto.setSorgcode(loginfo.getSorgcode());
        strecode = null;
          return super.goInput(o);
    }
    private void init() {
    	issearch = true;
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
	/**
	 * Direction: 录入保存
	 * ename: inputSave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	
		try {
			billAutoSendService.addInfo(dto);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsBillautosendDto();
		init();
          return backMaintenance(o);
    }

	/**
	 * Direction: 返回到维护界面
	 * ename: backMaintenance
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String backMaintenance(Object o){
//    	dto.setStaxorgcode(hashmap.get(dto.getStaxorgcode()));
    	issearch = false;
          return super.backMaintenance(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
//    	if(dto.getStaxorgcode()!=null&&!"".equals(dto.getStaxorgcode())&&hashmap.get(dto.getStaxorgcode())!=null)
//    		dto.setStaxorgcode(hashmap.get(dto.getStaxorgcode()));
    	dto = (TsBillautosendDto) o;
        return super.singleSelect(o);
    }

	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if(!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor.getCurrentComposite().getShell(), "提示", "是否确认删除?"))
    	{
    		return "";
    	}
    	if (dto.getSorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		try {
//			dto.setStaxorgcode(maphash.get(dto.getStaxorgcode()));
			billAutoSendService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsBillautosendDto();
		init();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.delete(o);
    }

	/**
	 * Direction: 到修改界面
	 * ename: goModify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String goModify(Object o){
//    	dto.setStaxorgcode(maphash.get(dto.getStaxorgcode()));
    	dto.setSbillkind(dto.getSbillkind().trim());
    	strecode = dto.getStrecode();
          return super.goModify(o);
    }

	/**
	 * Direction: 修改保存
	 * ename: modifySave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
    	try {
//    		if(dto.getStaxorgcode()!=null&&!"".equals(dto.getStaxorgcode())&&hashmap.get(dto.getStaxorgcode())!=null)
//        		dto.setStaxorgcode(hashmap.get(dto.getStaxorgcode()));
    		billAutoSendService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsBillautosendDto();
		
        return backMaintenance(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest arg0) {
    	try {
    		PageResponse pg = null;
    		if(issearch)
    			pg = commonDataAccessService.findRsByDtoWithWherePaging(searchDto,arg0,"1=1");
    		else
    		{
    			TsAutogenervoucherDto sdto = new TsAutogenervoucherDto();
    			sdto.setSorgcode(loginfo.getSorgcode());
    			sdto.setSvtcode(voudto.getSvtcode());
    			sdto.setShold3(voudto.getShold3());
    			sdto.setSext1(voudto.getSext1());
    			if(sdto.getSvtcode()==null||"".equals(sdto.getSvtcode()))
    				sdto.setSvtcode(MsgConstant.VOUCHER_NO_3405);
    			pg = commonDataAccessService.findRsByDtoWithWherePaging(sdto,arg0,"1=1");
    		}
//    		List list = pg.getData();
//    		if(list!=null&&list.size()>0)
//    		{
//    			TsBillautosendDto sdto = null;
//    			if(hashmap==null)
//    				hashmap = new HashMap<String,String>();
//    			if(maphash==null)
//    				maphash = new HashMap<String,String>();
//    			Mapper taxmap = null;
//    			for(int j=0;j<getTaxtrelist().size();j++)
//    			{
//    				taxmap = (Mapper)getTaxtrelist().get(j);
//    				hashmap.put(String.valueOf(taxmap.getUnderlyValue()), String.valueOf(taxmap.getDisplayValue()));
//    				maphash.put(String.valueOf(taxmap.getDisplayValue()),String.valueOf(taxmap.getUnderlyValue()));
//    			}
//    			TsTaxorgDto taxdto = new TsTaxorgDto();
//    			taxdto.setSorgcode(loginfo.getSorgcode());
//    			try {
//    				List<TsTaxorgDto> queryList = commonDataAccessService.findRsByDto(taxdto," order by s_taxorgcode ");
//    				if(queryList!=null&&queryList.size()>0)
//    				{
//    					for(TsTaxorgDto temp:queryList)
//    					{
//    						if(temp.getStaxorgname()!=null&&!"".equals(temp.getStaxorgname()))
//    							taxmap = new Mapper(temp.getStaxorgcode(),temp.getStaxorgname());
//    						else
//    							taxmap = new Mapper(temp.getStaxorgcode(),temp.getStaxorgcode());
//    						hashmap.put(String.valueOf(taxmap.getUnderlyValue()), String.valueOf(taxmap.getDisplayValue()));
//    						maphash.put(String.valueOf(taxmap.getDisplayValue()),String.valueOf(taxmap.getUnderlyValue()));
//    					}
//    				}
//    			} catch (ITFEBizException e) {
//    				e.printStackTrace();
//    			}
//    			for(int i=0;i<list.size();i++)
//    			{
//    				sdto = (TsBillautosendDto)list.get(i);
//    				sdto.setStaxorgcode(hashmap.get(sdto.getStaxorgcode()));
//    			}
//    		}
    		return pg;
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(arg0);
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
							taxmap = new Mapper(temp.getStaxorgcode(),temp.getStaxorgcode()+temp.getStaxorgname());
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

	public String getStrecode() {
		return strecode;
	}
	public List getVoucherTypeList() {
		if(voucherTypeList==null)
		{
			voucherTypeList = new ArrayList();
			Mapper map = new Mapper(MsgConstant.VOUCHER_NO_3405, "总额分成报表");
			Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3406, "两税返还报表");
			voucherTypeList.add(map);
			voucherTypeList.add(map1);
			voudto.setSvtcode(MsgConstant.VOUCHER_NO_3405);
			voudto.setShold3("0");
		}
		return voucherTypeList;
	}
	public List<TsAutogenervoucherDto> getCheckList() {
		return checkList;
	}

	public void setCheckList(List<TsAutogenervoucherDto> checkList) {
		this.checkList = checkList;
	}
	public void setVoucherTypeList(List voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}
	public void setStrecode(String strecode) {
		this.strecode = strecode;
		dto.setStrecode(strecode);
		getTaxtrelist();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}
	public String getVouvtcode() {
		return vouvtcode;
	}

	public void setVouvtcode(String vouvtcode) {
		this.vouvtcode = vouvtcode;
		voudto.setSvtcode(vouvtcode);
		if(MsgConstant.VOUCHER_NO_3405.equals(vouvtcode))
		{
			List contreaNames = new ArrayList();
			contreaNames.add("两税返还报表生成");
			contreaNames.add("两税返还查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("总额分成报表生成");
			contreaNames1.add("总额分成查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
			setShold4("8888888888");
		}else
		{
			List contreaNames = new ArrayList();
			contreaNames.add("两税返还报表生成");
			contreaNames.add("两税返还查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames, true);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("总额分成报表生成");
			contreaNames1.add("总额分成查询结果一览表");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, false);
			setShold4(null);
		}
		checkList.clear();
		PageResponse page = new PageResponse();
		vpaging.setPage(page);
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
	}

	public String getShold4() {
		if(StringUtils.isBlank(shold4)||StringUtils.isEmpty(shold4))
		{
			try {
				TvUsersconditionDto userScondto = new TvUsersconditionDto();
				userScondto.setSorgcode(loginfo.getSorgcode());
				userScondto.setSusercode(loginfo.getSuserCode());
				List<TvUsersconditionDto> usersconList = commonDataAccessService.findRsByDto(userScondto);
				if(usersconList!=null&&usersconList.size()>0)
					shold4 = usersconList.get(0).getSconditions();
				voudto.setShold4(shold4);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "得到记忆用户科目代码错误！");
			}
		}
		return shold4;
	}

	public void setShold4(String shold4) {
		if(shold4!=null&&!"".equals(shold4)&&MsgConstant.VOUCHER_NO_3406.equals(vouvtcode))
		{
			try {
				TvUsersconditionDto userScondto = new TvUsersconditionDto();
				userScondto.setSorgcode(loginfo.getSorgcode());
				userScondto.setSusercode(loginfo.getSuserCode());
				List searList = commonDataAccessService.findRsByDto(userScondto);
				if(searList!=null&&searList.size()>0)
				{
					userScondto = (TvUsersconditionDto)searList.get(0);
					if(!shold4.equals(userScondto.getSconditions()))
					{
						userScondto.setSconditions(shold4);
						commonDataAccessService.updateData(userScondto);
					}
				}else
				{
					userScondto.setSusername(loginfo.getSuserName());
					userScondto.setSconditions(shold4);
					commonDataAccessService.create(userScondto);
				}
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openMessageDialog(null, "记忆用户科目代码错误！");
			}
		}else
		{
			if(MsgConstant.VOUCHER_NO_3406.equals(vouvtcode))
			{
				Map<String,String> getMap = null;
				try {
					getMap = commonDataAccessService.getmapforkey(loginfo.getSorgcode());
				} catch (ITFEBizException e) {
					log.error(e);
					MessageDialog.openMessageDialog(null, "得到记忆用户科目代码错误！");
				}
				if(getMap!=null&&getMap.get(loginfo.getSuserCode())!=null)
					shold4 = getMap.get(loginfo.getSuserCode());
				else
					shold4 = "";
			}else
			{
				if(!"7777777777".equals(shold4)&&!"8888888888".equals(shold4)&&!"9999999999".equals(shold4))
				{
					MessageDialog.openMessageDialog(null, "总额分成报表科目代码只能是7777777777或8888888888或9999999999!");
					shold4="8888888888";
				}
			}
		}
		this.shold4 = shold4;
		voudto.setShold4(shold4);
	}
	/**
	 * Direction: 总额分成两税返还查询
	 * ename: divisearch
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String divisearch(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		issearch = false;
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录，请先新增数据！");
		}
		vpaging.setPage(pageResponse);
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
        return super.divisearch(o);
    }
    
	/**
	 * Direction: 总额分成两税返还保存
	 * ename: diviadd
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String diviadd(Object o){
    	if(vouvtcode==null||vouvtcode.equals("")){
    		MessageDialog.openMessageDialog(null, "凭证类型不可为空！");
    		return "";
    	}
    	if(voudto.getStrecode()==null||voudto.getStrecode().equals("")){
    		MessageDialog.openMessageDialog(null, "国库代码不可为空！");
    		return "";
    	}
    	try {
			if(voudto.getSext1()==null)
				voudto.setSext1("");
			List dtoList = commonDataAccessService.findRsByDto(voudto);
			if(dtoList!=null&&dtoList.size()>0)
			{
				MessageDialog.openMessageDialog(this.editor.getCurrentComposite().getShell(), "国库当前条件数据已经存在,同样的条件不可重复保存！");
				return "";
			}
			TsAutogenervoucherDto savedto = (TsAutogenervoucherDto)voudto.clone();
			TsConvertfinorgDto cDto=new TsConvertfinorgDto();
			cDto.setSorgcode(savedto.getSorgcode());		
			cDto.setStrecode(savedto.getStrecode());
			List TsConvertfinorgDtoList=commonDataAccessService.findRsByDto(cDto);
			cDto=(TsConvertfinorgDto) TsConvertfinorgDtoList.get(0);
			savedto.setSadmdivcode(cDto.getSadmdivcode());
			if(TsConvertfinorgDtoList==null||TsConvertfinorgDtoList.size()==0){
				MessageDialog.openMessageDialog(Display.getDefault().getActiveShell(),"国库："+cDto.getStrecode()+" 对应的财政代码未维护！");
				return "";
			}
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){			
				
				MessageDialog.openMessageDialog(Display.getDefault().getActiveShell(),"国库："+cDto.getStrecode()+" 对应的区划代码未维护！");
				return "";
			}
			String ieq = "00000000"+commonDataAccessService.getSequenceNo("TRAID_SEQ");
			savedto.setSdealno(TimeFacade.getCurrentStringTime()+ieq.substring(ieq.length()-8));
			savedto.setSfilename("auto-zefcandlsfh");
			savedto.setSvoucherflag("1");
			savedto.setSvoucherno(savedto.getSdealno());
			savedto.setNmoney(new BigDecimal(66666));
			savedto.setScreatdate(TimeFacade.getCurrentStringTime());
			savedto.setScheckdate(TimeFacade.getCurrentStringTime());
			savedto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
			savedto.setSstyear(TimeFacade.getCurrentStringTime().substring(0,4));
			if(savedto.getSext1()==null)
				savedto.setSext1("");
			commonDataAccessService.create(savedto);
			divisearch(o);
    	}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("操作出现异常！",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
			return "";
		}
        return super.diviadd(o);
    }
    
	/**
	 * Direction: 总额分成两税返还删除
	 * ename: dividel
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String dividel(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "请选择要删除电子凭证数据记录！");
    		return "";
    	}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "提示", "你确定要对选中的记录执行删除操作吗?")) {
			return "";
		}
    	try {
	    	for(IDto infoDto:checkList)
	    	{
	    		commonDataAccessService.delete(infoDto);
	    	}
	    	divisearch(o);
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
        return super.dividel(o);
    }

	public TsBillautosendDto getSearchDto() {
		return searchDto;
	}

	public void setSearchDto(TsBillautosendDto searchDto) {
		this.searchDto = searchDto;
	}

	public PagingContext getVpaging() {
		return vpaging;
	}

	public void setVpaging(PagingContext vpaging) {
		this.vpaging = vpaging;
	}
}
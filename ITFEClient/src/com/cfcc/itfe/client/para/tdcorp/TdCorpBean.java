package com.cfcc.itfe.client.para.tdcorp;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.pk.TsUsersPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment: 
 * @author zhang
 * @time   10-12-28 12:42:35
 * 子系统: Para
 * 模块:TdCorp
 * 组件:TdCorp
 */
public class TdCorpBean extends AbstractTdCorpBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TdCorpBean.class);
    
    ITFELoginInfo loginfo;
    private String sbookorgcode;
    private TsUsersPK tsuserspk=new TsUsersPK();
    private TsUsersDto tsuser=new TsUsersDto();
    private TdCorpDto searchDto;
    public TdCorpBean() {
      super();
      dto=new TdCorpDto();
      searchDto = new TdCorpDto();
      pagingcontext = new PagingContext(this);
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
		.getDefault().getLoginInfo();
      tsuserspk.setSorgcode(loginfo.getSorgcode());
      tsuserspk.setSusercode(loginfo.getSuserCode());
      try {
    	  tsuser=(TsUsersDto) commonDataAccessService.find(tsuserspk);
	} catch (Throwable e) {
		log.error(e);
		MessageDialog.openErrorDialog(null, e);
	}
      searchDto.setSbookorgcode(loginfo.getSorgcode());
      //init();
    }
    
    private void init() {
    	searchDto = new TdCorpDto();
    	searchDto.setSbookorgcode(loginfo.getSorgcode());

		/*PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);*/
	}
    public String searchRs(Object o){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		if(pageResponse.getData().size()<=0){
			MessageDialog.openMessageDialog(null,"没有查询到符合条件的数据");
		}
		pagingcontext.setPage(pageResponse);
    	return super.searchRs(o);
    }
    public String rebackSearch(Object o){
    	searchDto.setSbookorgcode(loginfo.getSorgcode());
    	return super.rebackSearch(o);
    }
	/**
	 * Direction: 跳转录入界面
	 * ename: goInput
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String goInput(Object o){
    	dto = new TdCorpDto();
    	dto.setSbookorgcode(loginfo.getSorgcode());
          return super.goInput(o);
    }

	/**
	 * Direction: 录入保存
	 * ename: inputSave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	if(datacheck(dto,"add")){
    		return null;
    	}
    	try {
    		tdCorpService.addInfo(dto);
    		
   		} catch (Throwable e) {
   			log.error(e);
   			MessageDialog.openErrorDialog(null, e);
   			
   			return super.inputSave(o);
   		}
   		MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
   		
//   		init();
   		editor.fireModelChanged();
//        return super.inputSave(o);
        return searchRs(o);
    }

	/**
	 * Direction: 返回到维护界面
	 * ename: backMaintenance
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new  TdCorpDto();
		init();
		setSbookorgcode("");
          return super.backMaintenance(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto = (TdCorpDto) o;
          return super.singleSelect(o);
    }

	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if(!tsuser.getSusertype().equals("2")){
    		MessageDialog.openMessageDialog(null,"该功能由业务主管维护,当前用户不能执行此操作");
    		return null;
    	}
    	if (dto.getSbookorgcode()== null) {
			MessageDialog.openMessageDialog(null,StateConstant.DELETESELECT);
			return "";
		}
    	try {
    		tdCorpService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		init();
		editor.fireModelChanged();
		return this.searchRs(o);
    }

	/**
	 * Direction: 到修改界面
	 * ename: goModify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String goModify(Object o){
    	if(!tsuser.getSusertype().equals("2")){
    		MessageDialog.openMessageDialog(null,"该功能由业务主管维护,当前用户不能执行此操作");
    		return null;
    	}
    	if (dto.getSbookorgcode() == null||dto.getScorpcode()==null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
    	
          return super.goModify(o);
    }

	/**
	 * Direction: 修改保存
	 * ename: modifySave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
    	
    	if(datacheck(dto,"modify")){
    		return null;
    	}
    	try {
    		tdCorpService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TdCorpDto();
		editor.fireModelChanged();
		return super.backMaintenance(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {

    	//dto.setSbookorgcode(loginfo.getSorgcode());
	
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(searchDto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}
    
    private boolean datacheck(TdCorpDto dto,String flag){
//    	if(null==dto.getSbookorgcode()||dto.getSbookorgcode().trim().length()==0){
//    		MessageDialog.openMessageDialog(null, "核算主体代码不能为空不能为空！");
//			return true;
//    	}else if(null==dto.getScorpcode()||dto.getScorpcode().trim().length()==0){
//    		MessageDialog.openMessageDialog(null, "预算单位代码不能为空！");
//    		return true;
//    	}else if(null==dto.getCtrimflag()||dto.getCtrimflag().trim().length()==0){
//    		MessageDialog.openMessageDialog(null, "调整期标志不能为空！");
//    		return true;
//    	}else if(null==dto.getStrecode()||dto.getStrecode().trim().length()==0){
//    		MessageDialog.openMessageDialog(null, "国库代码不能为空!");
//    		return true;
//    	}
		
    	if (flag.equals("add")) {
    		TdCorpDto tempdto=new TdCorpDto();
	    	tempdto.setScorpcode(dto.getScorpcode());
	    	tempdto.setCtrimflag(dto.getCtrimflag());
	    	tempdto.setStrecode(dto.getStrecode());
	    	List list=null;
    	try{
    		list = commonDataAccessService.findRsByDto(tempdto);
    	}catch(ITFEBizException e){
    		MessageDialog.openErrorDialog(null, e);
			return true;
    	}
    	if(null !=list&&list.size()>0){
    		MessageDialog.openMessageDialog(null, "国库代码+法人代码重复!");
    		return true;
    	}else{
    		return false;
    	}
    	}
		return false;
    }

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public String getSbookorgcode() {
		return sbookorgcode;
	}

	public void setSbookorgcode(String sbookorgcode) {
		this.sbookorgcode = sbookorgcode;
	}

	public TdCorpDto getSearchDto() {
		return searchDto;
	}

	public void setSearchDto(TdCorpDto searchDto) {
		this.searchDto = searchDto;
	}
	
	

}
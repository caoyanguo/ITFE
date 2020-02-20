package com.cfcc.itfe.client.para.tsoperationform;

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
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsOperationformDto;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.service.ITFELoginInfo;

/**
 * codecomment: 
 * @author caoyg
 * @time   09-10-20 08:42:01
 * 子系统: Para
 * 模块:TsOperationform
 * 组件:TsOperationform
 */
public class TsOperationformBean extends AbstractTsOperationformBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsOperationformBean.class);
    private List list;
    private String smodelid;
    private String sformtype;
    
    public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public String getSmodelid() {
		return smodelid;
	}

	public void setSmodelid(String smodelid) {
		this.smodelid = smodelid;
	}

	public String getSformtype() {
		return sformtype;
	}

	public void setSformtype(String sformtype) {
		this.sformtype = sformtype;
	}

	public TsOperationformBean() {
      super();
      dto = new TsOperationformDto();
      pagingcontext = new PagingContext(this);
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
    	dto = new TsOperationformDto();
		return super.goInput(o);
		
    }
   
    
	/**
	 * Direction: 录入保存
	 * ename: inputSave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	dto.setSmodelid( getSmodelid());
    	dto.setSformtype(getSformtype());
    	try {
    		tsOperationformService.addInfo(dto);
   		} catch (Throwable e) {
   			log.error(e);
   			MessageDialog.openErrorDialog(null, e);
   			return super.inputSave(o);
   		}
   		MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
   		dto = new TsOperationformDto();
   		init();
   		setSformtype("");
		setSmodelid("");
   		return super.backMaintenance(o);
       }
    

	/**
	 * Direction: 返回到维护界面
	 * ename: backMaintenance
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String backMaintenance(Object o){
    	dto = new TsOperationformDto();
		init();
		setSformtype("");
		setSmodelid("");
		return super.backMaintenance(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto = (TsOperationformDto) o;
		return super.singleSelect(o);
    }

	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if ((dto == null) || (dto.getSformid() == null)) {
			MessageDialog.openMessageDialog(null,StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(editor.getCurrentComposite().getShell(), "提示", "是否删除盖章联" + dto.getSformid())){
			return "";
		}
		try {
			tsOperationformService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsOperationformDto();
		init();
		return super.backMaintenance(o);
    }

	/**
	 * Direction: 到修改界面
	 * ename: goModify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String goModify(Object o){
    	setSformtype(dto.getSformtype());
		setSmodelid(dto.getSformid());
    	if (dto == null ||dto.getSformid() == null) {
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
    	dto.setSmodelid( getSmodelid());
    	dto.setSformtype(getSformtype());
    	try {
    		tsOperationformService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsOperationformDto();
		setSformtype("");
		setSmodelid("");
		return super.backMaintenance(o);
		}
     
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {

    	try {
			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}
    private void init() {
    	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
		.getDefault().getLoginInfo();
    	TsOperationmodelDto operationdto = new TsOperationmodelDto ();
    	try {
			list  = commonDataAccessService.findRsByDto(operationdto);
			
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return ;
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
  
}
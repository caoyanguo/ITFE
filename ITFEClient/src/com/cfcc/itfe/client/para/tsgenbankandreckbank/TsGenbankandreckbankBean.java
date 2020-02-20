package com.cfcc.itfe.client.para.tsgenbankandreckbank;

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
import com.cfcc.itfe.service.para.tsgenbankandreckbank.ITsGenbankandreckbankService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ICommonDataAccessService;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.IItfeCacheService;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;

/**
 * codecomment: 
 * @author t60
 * @time   12-02-21 11:24:39
 * 子系统: Para
 * 模块:TsGenbankandreckbank
 * 组件:TsGenbankandreckbank
 */
public class TsGenbankandreckbankBean extends AbstractTsGenbankandreckbankBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsGenbankandreckbankBean.class);
    private List list;
	private ITFELoginInfo loginfo = null;
	private List organlist;
    private List list2;
    public TsGenbankandreckbankBean() {
      super();
      loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
		.getLoginInfo();
    dto = new TsGenbankandreckbankDto();
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
    	dto=new TsGenbankandreckbankDto();
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
    	if(!check()){
    		return null;
    	}
    	try {
			tsGenbankandreckbankService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);

			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsGenbankandreckbankDto();
		init();
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
    	dto = new TsGenbankandreckbankDto();
    	dto.setSbookorgcode(loginfo.getSorgcode());
		init();
        return super.backMaintenance(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
    	dto=(TsGenbankandreckbankDto)o;
        return super.singleSelect(o);
    }

	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (dto.getSbookorgcode()== null||dto.getSgenbankcode()==null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		try {
			tsGenbankandreckbankService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsGenbankandreckbankDto();
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
    	if (dto == null || dto.getSbookorgcode() == null||dto.getSgenbankcode()==null) {
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
    	try {
			tsGenbankandreckbankService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsGenbankandreckbankDto();
		return super.backMaintenance(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
    	TsGenbankandreckbankDto tmpdto = new TsGenbankandreckbankDto();
		try {
			tmpdto.setSbookorgcode(loginfo.getSorgcode());
			return commonDataAccessService.findRsByDtoWithWherePaging(tmpdto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}
    
    private void init() {
    	TsOrganDto orgdto = new TsOrganDto();
		orgdto.setSorgcode(loginfo.getSorgcode());
		try {
			organlist = commonDataAccessService.findRsByDto(orgdto);

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
    	TsGenbankandreckbankDto dbrDto=new TsGenbankandreckbankDto();
    	dbrDto.setSbookorgcode(loginfo.getSorgcode());
    	TsTreasuryDto tredto = new TsTreasuryDto();
		tredto.setSorgcode(loginfo.getSorgcode());
		try {
			list = commonDataAccessService.findRsByDto(dbrDto);
			list2 = commonDataAccessService.findRsByDto(tredto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
    private boolean check(){
    	TsGenbankandreckbankDto tmpdto=new TsGenbankandreckbankDto();
    	tmpdto.setSbookorgcode(dto.getSbookorgcode());
    	tmpdto.setSgenbankcode(dto.getSgenbankcode());
    	tmpdto.setStrecode(dto.getStrecode());
    	try {
			List list = commonDataAccessService.findRsByDto(tmpdto);
			if (null != list && list.size() > 0) {
				MessageDialog.openMessageDialog(null,
						"【代理银行代码】不能重复!");
				return false;
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return false;
		}
		return true;
    }

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public List getOrganlist() {
		return organlist;
	}

	public void setOrganlist(List organlist) {
		this.organlist = organlist;
	}
	public List getList2() {
		return list2;
	}

	public void setList2(List list2) {
		this.list2 = list2;
	}
}
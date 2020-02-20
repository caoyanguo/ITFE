package com.cfcc.itfe.client.para.tsoperationtype;

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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsOperationtypeDto;

/**
 * codecomment: 
 * @author caoyg
 * @time   09-10-20 08:42:02
 * 子系统: Para
 * 模块:TsOperationtype
 * 组件:TsOperationtype
 */
public class TsOperationtypeBean extends AbstractTsOperationtypeBean implements IPageDataProvider {
    private static Log log = LogFactory.getLog(TsOperationtypeBean.class);
    private String isSend;
    private String isRec;
   
	public String getIsSend() {
		return isSend;
	}

	public void setIsSend(String isSend) {
		this.isSend = isSend;
	}

	public String getIsRec() {
		return isRec;
	}

	public void setIsRec(String isRec) {
		this.isRec = isRec;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	private List list;
    public TsOperationtypeBean() {
      super();
      dto = new TsOperationtypeDto();
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
    	dto = new TsOperationtypeDto();
    	setIsRec("");
    	setIsSend("");
		return super.goInput(o);
    }

	/**
	 * Direction: 录入保存
	 * ename: inputSave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
    	dto.setSissend(getIsSend());
    	dto.setSisrecv(getIsRec());
    	try {
    		tsOperationtypeService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null,StateConstant.INPUTSAVE);
		dto = new TsOperationtypeDto();
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
    	dto = new  TsOperationtypeDto();
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
    	dto = (TsOperationtypeDto) o;
		return super.singleSelect(o);
    }

	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
    	if (dto == null || dto.getSoperationtypecode() == null) {
			MessageDialog.openMessageDialog(null,StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(editor.getCurrentComposite().getShell(), "提示", "是否删除业务种类" + dto.getSoperationtypecode())){
			return "";
		}
		try {
			tsOperationtypeService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsOperationtypeDto();
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
    	if (dto == null || dto.getSoperationtypecode()== null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
    	setIsRec(dto.getSisrecv());
    	setIsSend(dto.getSissend());
        return super.goModify(o);
    }

	/**
	 * Direction: 修改保存
	 * ename: modifySave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String modifySave(Object o){
    	dto.setSissend(getIsSend());
    	dto.setSisrecv(getIsRec());
    	try {
    		tsOperationtypeService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsOperationtypeDto();
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
//		ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
//				.getDefault().getLoginInfo();
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}
}
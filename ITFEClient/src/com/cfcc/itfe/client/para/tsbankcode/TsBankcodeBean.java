package com.cfcc.itfe.client.para.tsbankcode;

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
import com.cfcc.itfe.persistence.dto.TsBankcodeDto;

/**
 * codecomment: 
 * @author lushaoqing
 * @time   10-09-26 10:35:24
 * 子系统: Para
 * 模块:TsBankcode
 * 组件:TsBankcode
 */
public class TsBankcodeBean extends AbstractTsBankcodeBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(TsBankcodeBean.class);
    TsBankcodeDto findDto = new TsBankcodeDto();
    public TsBankcodeBean() {
      super();
      dto = new TsBankcodeDto();
      pagingcontext = new PagingContext(this);
    }
    
	/**
	 * Direction: 跳转录入界面
	 * ename: goInput
	 * 引用方法: 
	 * viewers: 录入界面
	 * messages: 
	 */
    public String goInput(Object o){
    	dto = new TsBankcodeDto();
    	return super.goInput(o);
    }

	/**
	 * Direction: 录入保存
	 * ename: inputSave
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String inputSave(Object o){
		String verifyRs = verifyPorp(dto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}

		try {
			tsBankcodeService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsBankcodeDto();
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
    	dto = new TsBankcodeDto();
        return super.backMaintenance(o);
    }

	/**
	 * Direction: 单选
	 * ename: singleSelect
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String singleSelect(Object o){
		dto = (TsBankcodeDto) o;
        return super.singleSelect(o);
    }

	/**
	 * Direction: 删除
	 * ename: delete
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String delete(Object o){
		if (null == dto || null == dto.getSbnkcode() || null == dto.getSacctstatus()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return null;
		}

		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
				null, "删除数据确认!", "是否确认要删除这条记录！");
		if (flag) {
			try {
				tsBankcodeService.delInfo(dto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return super.delete(o);
			}
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			dto = (TsBankcodeDto)findDto.clone();
		}
         return this.goSearchResult(o);
    }

	/**
	 * Direction: 到修改界面
	 * ename: goModify
	 * 引用方法: 
	 * viewers: 修改界面
	 * messages: 
	 */
    public String goModify(Object o){
		if (null == dto || null == dto.getSbnkcode() || null == dto.getSacctstatus()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return null;
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
		String verifyRs = verifyPorp(dto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}
		try {
			tsBankcodeService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = (TsBankcodeDto)findDto.clone();
		return this.goSearchResult(o);
    }

	/**
	 * Direction: 到查询结果界面
	 * ename: goSearchResult
	 * 引用方法: 
	 * viewers: 维护界面
	 * messages: 
	 */
    public String goSearchResult(Object o){
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);

		if (pageResponse.getTotalCount() <= 0) {
			MessageDialog.openMessageDialog(null, "没有查询到符合条件的记录!");
			return super.goSearchResult(o);
		}

		editor.fireModelChanged();
        return super.goSearchResult(o);
    }
    /*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf.common.page.PageRequest)
	 */
    public PageResponse retrieve(PageRequest pageRequest) {
		if (null == dto) {
			dto = new TsBankcodeDto();
		}
		try {
			if(null != dto && ((dto.getSbnkname() != null && dto.getSbnkname().trim() != "")
					||(dto.getSbnkcode() != null && dto.getSbnkcode().trim() != "")
					||(dto.getSacctstatus() != null && dto.getSacctstatus().trim() != "")))
			{
				findDto = (TsBankcodeDto)dto.clone();
			}
			return tsBankcodeService.findBanknoByPage(dto, pageRequest);
		} catch (Exception e) {
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	/**
	 * 校验字段属性
	 * 
	 * @param idto
	 * @return
	 */
	public String verifyPorp(TsBankcodeDto idto) {
		if (null == idto) {
			return "要操作的纪录为空，请确认！";
		}

		if (null == idto.getSbnkcode() || "".equals(idto.getSbnkcode().trim())
				|| idto.getSbnkcode().trim().length() != 12) {
			return "银行账号代码必须填写，且必须为12位！";
		}

		if (null == idto.getSacctstatus() || "".equals(idto.getSacctstatus())) {
			return "银行账号状态为空！";
		}
		
		if (null == idto.getSbnkname() || "".equals(idto.getSbnkname())) {
			return "银行账号名称为空！";
		}

		return null;
	}
}
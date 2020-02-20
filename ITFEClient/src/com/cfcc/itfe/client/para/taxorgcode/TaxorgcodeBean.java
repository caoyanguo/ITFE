package com.cfcc.itfe.client.para.taxorgcode;

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
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.service.ITFELoginInfo;

/**
 * codecomment:
 * 
 * @author Administrator
 * @time 17-06-20 10:47:10 子系统: Para 模块:taxorgcode 组件:Taxorgcode
 */
public class TaxorgcodeBean extends AbstractTaxorgcodeBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TaxorgcodeBean.class);
	private ITFELoginInfo loginfo;

	public TaxorgcodeBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		dto = new TsTaxorgDto();
		pagingcontext = new PagingContext(this);
		searchdto = new TsTaxorgDto();
		searchdto.setSorgcode(StateConstant.ORG_CENTER_CODE);
	}

	/**
	 * Direction: 查询 ename: search 引用方法: viewers: 查询结果 messages:
	 */
	public String search(Object o) {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		return super.search(o);
	}

	/**
	 * Direction: 设置 ename: adddata 引用方法: viewers: 录入界面 messages:
	 */
	public String adddata(Object o) {
		dto = new TsTaxorgDto();
		dto.setSorgcode(StateConstant.ORG_CENTER_CODE);
		return super.adddata(o);
	}

	/**
	 * Direction: 修改 ename: updatedata 引用方法: viewers: 修改界面 messages:
	 */
	public String updatedata(Object o) {
		if (null == dto.getStaxorgcode() || "".equals(dto.getStaxorgcode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		dto.setStaxprop(dto.getStaxprop().trim());
		return super.updatedata(o);
	}

	/**
	 * Direction: 删除 ename: deletedata 引用方法: viewers: * messages:
	 */
	public String deletedata(Object o) {
		if (null == dto.getStaxorgcode() || "".equals(dto.getStaxorgcode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		// 提示用户确定删除
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否要删除选择的征收机关代码"
				+ dto.getStaxorgcode())) {
			return "";
		}
		try {
			tsTaxorgService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.deletedata(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		search(null);
		editor.fireModelChanged();
		return super.deletedata(o);
	}

	/**
	 * Direction: 返回 ename: reback 引用方法: viewers: 查询结果 messages:
	 */
	public String reback(Object o) {
		search(o) ;
		return super.reback(o);
	}

	/**
	 * Direction: 保存 ename: savedata 引用方法: viewers: * messages:
	 */
	public String savedata(Object o) {
		try {
			tsTaxorgService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.savedata(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		search(o);
		return super.savedata(o);
	}

	/**
	 * Direction: 修改保存 ename: saveupdae 引用方法: viewers: 查询结果 messages:
	 */
	public String saveupdae(Object o) {
		try {
			tsTaxorgService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.saveupdae(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsTaxorgDto();
		dto.setSorgcode(StateConstant.ORG_CENTER_CODE);
		return super.saveupdae(o);
	}

	/**
	 * Direction: 单击选择 ename: singleselect 引用方法: viewers: * messages:
	 */
	public String singleselect(Object o) {
		dto = (TsTaxorgDto) o;
		return super.singleselect(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		try {
			searchdto.setSorgcode(StateConstant.ORG_CENTER_CODE);
			/*return commonDataAccessService.findRsByDtoWithWherePaging(querydto,
					pageRequest, "1=1");*/
			PageResponse pr=commonDataAccessService.findRsByDtoWithWherePaging(searchdto,
					pageRequest, "1=1");
			List<TsTaxorgDto> list=pr.getData();
			for(TsTaxorgDto ttd:list){
				if(ttd.getStaxprop()==null)
					ttd.setStaxprop("");
			}
			pr.setData(list);
			return pr;
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TaxorgcodeBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

}
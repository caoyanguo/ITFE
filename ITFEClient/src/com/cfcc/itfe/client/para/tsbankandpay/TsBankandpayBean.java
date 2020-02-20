package com.cfcc.itfe.client.para.tsbankandpay;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsBankandpayDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author wangtuo
 * @time 10-04-08 12:58:20 子系统: Para 模块:TsBankandpay 组件:TsBankandpay
 */
public class TsBankandpayBean extends AbstractTsBankandpayBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsBankandpayBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();

	public TsBankandpayBean() {
		super();
		dto = new TsBankandpayDto();
		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: 查询 ename: queryBudget 引用方法: viewers: 查询结果 messages:
	 */
	public void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsBankandpayDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 */
	public String inputSave(Object o) {
		// if (!datacheck()) {
		// return null;
		// }
		if (null == dto.getSnationtaxbank()
				|| "".equals(dto.getSnationtaxbank().trim())) {
			MessageDialog.openMessageDialog(null, "国税银行代码不能为空！");
			return null;
		}
		if (null == dto.getSareataxbank()
				|| "".equals(dto.getSareataxbank().trim())) {
			MessageDialog.openMessageDialog(null, "地税银行代码不能为空！");
			return null;
		}
		try {
			dto.getSorgcode();
			tsBankandpayService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsBankandpayDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsBankandpayDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsBankandpayDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto.getSnationtaxbank()
				|| "".equals(dto.getSnationtaxbank())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		// 提示用户确定删除
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否要删除选择的国税银行"
				+ dto.getSnationtaxbank())) {
			return "";
		}
		try {
			tsBankandpayService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsBankandpayDto();
		init();
		editor.fireModelChanged();
		return super.delete(o);
	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (null == dto.getSnationtaxbank()
				|| "".equals(dto.getSnationtaxbank())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}

	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 */
	public String modifySave(Object o) {
		// if (!datacheck()) {
		// return null;
		// }
		try {
			tsBankandpayService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsBankandpayDto();
		init();
		return super.backMaintenance(o);
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
			dto.setSorgcode(loginfo.getSorgcode());
			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, "1=1");

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
		TsBankandpayBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

}
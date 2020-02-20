package com.cfcc.itfe.client.para.tsbudgetsubjectforquery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectforqueryDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author zgz
 * @time 12-05-18 16:32:15 子系统: Para 模块:TsBudgetsubjectForQuery
 *       组件:TsBudgetsubjectForQuery
 */
public class TsBudgetsubjectForQueryBean extends
		AbstractTsBudgetsubjectForQueryBean implements IPageDataProvider {

	private static Log log = LogFactory
			.getLog(TsBudgetsubjectForQueryBean.class);
	private ITFELoginInfo loginfo = null;

	public TsBudgetsubjectForQueryBean() {
		super();
		dto = new TsBudgetsubjectforqueryDto();

		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		dto.setSorgcode(loginfo.getSorgcode());
		pagingcontext = new PagingContext(this);
		init();
	}

	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsBudgetsubjectforqueryDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 */
	public String inputSave(Object o) {
		String verifyRs = verifyPorp(dto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}
		try {
			tsBudgetsubjectForQueryService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsBudgetsubjectforqueryDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsBudgetsubjectforqueryDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsBudgetsubjectforqueryDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto || null == dto.getSsubjectcode()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return null;
		}

		boolean flag = org.eclipse.jface.dialogs.MessageDialog.openConfirm(
				null, "删除数据确认!", "是否确认要删除这条记录！");
		if (flag) {
			try {
				tsBudgetsubjectForQueryService.delInfo(dto);
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return super.delete(o);
			}
			MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
			dto = new TsBudgetsubjectforqueryDto();
		}

		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (null == dto || null == dto.getSsubjectcode()) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}

	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 */
	public String modifySave(Object o) {
		String verifyRs = verifyPorp(dto);
		if (null != verifyRs) {
			MessageDialog.openMessageDialog(null, verifyRs);
			return null;
		}
		try {
			tsBudgetsubjectForQueryService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsBudgetsubjectforqueryDto();
		return super.backMaintenance(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {
		if (null == dto) {
			dto = new TsBudgetsubjectforqueryDto();
		}

		dto.setSorgcode(this.getLoginfo().getSorgcode());
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					arg0, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(arg0);
	}
	/**
	 * 校验字段属性
	 * 
	 * @param idto
	 * @return
	 */
	public String verifyPorp(TsBudgetsubjectforqueryDto idto) {
		if (null == idto) {
			return "要操作的纪录为空，请确认！";
		}

		if (null == idto.getSsubjectcode()|| "".equals(idto.getSsubjectcode().trim())) {
			return "科目代码为空！";
		}

		if (null == idto.getSsubjectname() || "".equals(idto.getSsubjectname())) {
			return "科目名称为空！";
		}

		return null;
	}
	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

}
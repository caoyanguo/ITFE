package com.cfcc.itfe.client.para.tsconnection;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsConnectionDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:01 子系统: Para 模块:TsConnection 组件:TsConnection
 */
public class TsConnectionBean extends AbstractTsConnectionBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsConnectionBean.class);
	private List list1;
	private List list2;
	private String sconnorgcodea;
	private String sconnorgcodeb;

	public List getList1() {
		return list1;
	}

	public void setList1(List list1) {
		this.list1 = list1;
	}

	public List getList2() {
		return list2;
	}

	public void setList2(List list2) {
		this.list2 = list2;
	}

	public String getSconnorgcodea() {
		return sconnorgcodea;
	}

	public void setSconnorgcodea(String sconnorgcodea) {
		this.sconnorgcodea = sconnorgcodea;
	}

	public String getSconnorgcodeb() {
		return sconnorgcodeb;
	}

	public void setSconnorgcodeb(String sconnorgcodeb) {
		this.sconnorgcodeb = sconnorgcodeb;
	}

	public TsConnectionBean() {
		super();
		dto = new TsConnectionDto();
		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 * 
	 * @param
	 */
	public String goInput(Object o) {
		dto = new TsConnectionDto();
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 */
	public String inputSave(Object o) {
		 if (!datacheck()) {
				return null;
			}
		dto.setSconnorgcodea(getSconnorgcodea());
		dto.setSconnorgcodeb(getSconnorgcodeb());

		if (dto.getSconnorgcodea().equals(dto.getSconnorgcodeb())) {
			MessageDialog.openMessageDialog(null, "连通机构甲方与连通机构乙方不能相同");
			return null;
		}
		try {
			tsConnectionService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsConnectionDto();
		setSconnorgcodea("");
		setSconnorgcodeb("");
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsConnectionDto();
		setSconnorgcodea("");
		setSconnorgcodeb("");
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsConnectionDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto == null || dto.getSconnorgcodea() == null
				|| dto.getSconnorgcodeb() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(editor.getCurrentComposite().getShell(), "提示", "是否删除选中的连通性？ ")){
			return "";
		}
		try {
			tsConnectionService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsConnectionDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (dto == null || dto.getSconnorgcodea() == null
				|| dto.getSconnorgcodeb() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		setSconnorgcodea(dto.getSconnorgcodea().trim());
		setSconnorgcodeb(dto.getSconnorgcodeb().trim());

		return super.goModify(o);
	}

	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 */
	public String modifySave(Object o) {
		 if (!datacheck()) {
				return null;
			}
		 dto.setSconnorgcodea(getSconnorgcodea());
		 dto.setSconnorgcodeb(getSconnorgcodeb());
		if (dto.getSconnorgcodea().equals(dto.getSconnorgcodeb())) {
			MessageDialog.openMessageDialog(null, "连通机构甲方与连通机构乙方不能相同");
			return null;
		}
		try {
			tsConnectionService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsConnectionDto();
		setSconnorgcodea("");
		setSconnorgcodeb("");
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
		TsOrganDto orgdto1 = new TsOrganDto();
		try {
			list1 = commonDataAccessService.findRsByDto(orgdto1);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		TsOrganDto orgdto2 = new TsOrganDto();
		try {
			list2 = commonDataAccessService.findRsByDto(orgdto2);

		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	private boolean datacheck() {
		if (null == getSconnorgcodea() || "".equals(getSconnorgcodea().trim())) {
			MessageDialog.openMessageDialog(null, "机构连通甲方不能为空！");
			return false;
		}
		if (null == getSconnorgcodeb() || "".equals(getSconnorgcodeb().trim())) {
			MessageDialog.openMessageDialog(null, "机构连通乙方不能为空！");
			return false;
		}
		return true;

	}

}
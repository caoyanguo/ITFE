package com.cfcc.itfe.client.para.tstaxorg;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
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
 * @time 10-04-08 12:58:21 子系统: Para 模块:TsTaxorg 组件:TsTaxorg
 */
public class TsTaxorgBean extends AbstractTsTaxorgBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsTaxorgBean.class);
	private ITFELoginInfo loginfo;
	// 查询dto
	private TsTaxorgDto querydto;

	public TsTaxorgBean() {
		super();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
		.getDefault().getLoginInfo();
		dto = new TsTaxorgDto();
		querydto = new TsTaxorgDto();
		pagingcontext = new PagingContext(this);

	}

	/**
	 * Direction: 查询 ename: query 引用方法: viewers: 维护界面 messages:
	 */
	public String query(Object o) {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		return super.query(o);
	}

	/**
	 * Direction: 返回 ename: goBack 引用方法: viewers: 信息查询 messages:
	 */
	public String goBack(Object o) {

		return super.goBack(o);
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsTaxorgDto();
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
		try {
			dto.getSorgcode();
			tsTaxorgService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsTaxorgDto();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsTaxorgDto();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsTaxorgDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
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
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		query(null);
		editor.fireModelChanged();
		return super.delete(o);
	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (null == dto.getStaxorgcode() || "".equals(dto.getStaxorgcode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		dto.setStaxprop(dto.getStaxprop().trim());
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
			tsTaxorgService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsTaxorgDto();
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
			querydto.setSorgcode(loginfo.getSorgcode());
			/*return commonDataAccessService.findRsByDtoWithWherePaging(querydto,
					pageRequest, "1=1");*/
			PageResponse pr=commonDataAccessService.findRsByDtoWithWherePaging(querydto,
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
		TsTaxorgBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public TsTaxorgDto getQuerydto() {
		return querydto;
	}

	public void setQuerydto(TsTaxorgDto querydto) {
		this.querydto = querydto;
	}

}
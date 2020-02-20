package com.cfcc.itfe.client.para.tsinfoconnorg;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgDto;
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
 * @time 09-10-20 08:42:02 子系统: Para 模块:TsInfoconnorg 组件:TsInfoconnorg
 */
public class TsInfoconnorgBean extends AbstractTsInfoconnorgBean implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsInfoconnorgBean.class);
	private ITFELoginInfo loginfo;
	private List list2;
	private String strecode = "0000000000";
	private String repeatskey;

	public String getRepeatskey() {
		return repeatskey;
	}

	public void setRepeatskey(String repeatskey) {
		this.repeatskey = repeatskey;
	}

	public TsInfoconnorgBean() {
		super();
		dto = new TsInfoconnorgDto();
		pagingcontext = new PagingContext(this);
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		init();
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		dto = new TsInfoconnorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		this.repeatskey="";
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 */
	public String inputSave(Object o) {
		if (!datacheck("add")) {
			return null;
		}
		dto.setStrecode(strecode);
		dto.setSorgcode(loginfo.getSorgcode());
		try {
			tsInfoconnorgService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsInfoconnorgDto();
		init();
		return backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsInfoconnorgDto();
		init();
		setRepeatskey("");
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsInfoconnorgDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto || dto.getSconnorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(
				this.editor.getCurrentComposite().getShell(), StateConstant.TIPS,
				StateConstant.DELETECONFIRM)) {
			dto = new TsInfoconnorgDto();
			return null;
		}
		try {
			tsInfoconnorgService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsInfoconnorgDto();
		init();
		return super.backMaintenance(o);

	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (dto == null || dto.getStrecode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		
		dto.setSkey(null);
		setRepeatskey(dto.getSkey());
		editor.fireModelChanged();
		return super.goModify(o);

	}

	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 */
	public String modifySave(Object o) {
		if (!datacheck("modify")) {
			return null;
		}
		dto.setStrecode(strecode);
		try {
			tsInfoconnorgService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsInfoconnorgDto();
		return backMaintenance(o);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		dto = new TsInfoconnorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		try {
			return commonDataAccessService.findRsByDtoWithWherePaging(dto, pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);

	}

	// 初始化
	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	// 数据校验
	private boolean datacheck(String flag) {
		if (null == dto.getSconnorgcode() || "".equals(dto.getSconnorgcode().trim())) {
			MessageDialog.openMessageDialog(null, "联网机构代码不能为空！");
			return false;
		}
		if (null == dto.getSkey() || "".equals(dto.getSkey().trim()) || dto.getSkey().trim().length() != 16) {
			MessageDialog.openMessageDialog(null, "密钥应该为16位！");
			return false;
		}
		if (null == getRepeatskey() || "".equals(getRepeatskey().trim())) {
			MessageDialog.openMessageDialog(null, "确认密钥不能为空！");
			return false;
		}

		if (!dto.getSkey().equals(getRepeatskey())) {
			MessageDialog.openMessageDialog(null, "密钥与确认密钥不相同！");
			return false;
		}
		// 录入判重
		if (flag.equals("add")) {
			TsInfoconnorgDto tempdto = new TsInfoconnorgDto();
			tempdto.setSorgcode(loginfo.getSorgcode());
			tempdto.setSconnorgcode(dto.getSconnorgcode());
			try {
				List list = commonDataAccessService.findRsByDto(tempdto);
				if (null != list && list.size() > 0) {
					MessageDialog.openMessageDialog(null, "【核算主体代码 +联网机构代码】不能重复!");
					return false;
				}
			} catch (Throwable e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
			}
		}
		return true;

	}

}
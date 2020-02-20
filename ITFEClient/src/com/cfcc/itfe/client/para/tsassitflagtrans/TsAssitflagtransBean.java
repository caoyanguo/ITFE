package com.cfcc.itfe.client.para.tsassitflagtrans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsAssitflagtransDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author VAIO
 * @time 10-04-08 12:58:21 ��ϵͳ: Para ģ��:TsAssitflagtrans ���:TsAssitflagtrans
 */
public class TsAssitflagtransBean extends AbstractTsAssitflagtransBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsAssitflagtransBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();

	public TsAssitflagtransBean() {
		super();
		dto = new TsAssitflagtransDto();
		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: ��ѯ ename: query ���÷���: viewers: ά������ messages:
	 */
	public void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TsAssitflagtransDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 */
	public String inputSave(Object o) {
		// if (!datacheck()) {
		// return null;
		// }
		if (null == dto.getSbudgetsubject()
				|| "".equals(dto.getSbudgetsubject().trim())) {
			dto.setSbudgetsubject("N");
		}
		if (null == dto.getSbudgetlevel()
				|| "".equals(dto.getSbudgetlevel().trim())) {
			MessageDialog.openMessageDialog(null, "Ԥ�㼶�β���Ϊ�գ�");
			return null;
		}
		if (null == dto.getStrecode()
				|| "".equals(dto.getStrecode().trim())) {
			dto.setStrecode("N");
		}
		try {
			dto.getSorgcode();
			tsAssitflagtransService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsAssitflagtransDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsAssitflagtransDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsAssitflagtransDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto.getSbudgetsubject()
				|| "".equals(dto.getSbudgetsubject())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		// ��ʾ�û�ȷ��ɾ��
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�Ҫɾ��ѡ���Ԥ���Ŀ"
				+ dto.getSbudgetsubject())) {
			return "";
		}
		try {
			tsAssitflagtransService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsAssitflagtransDto();
		init();
		editor.fireModelChanged();
		return super.delete(o);
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if (null == dto.getSbudgetsubject()
				|| "".equals(dto.getSbudgetsubject())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 */
	public String modifySave(Object o) {
		// if (!datacheck()) {
		// return null;
		// }
		try {
			tsAssitflagtransService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsAssitflagtransDto();
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
			TsAssitflagtransDto tmpdto = new TsAssitflagtransDto();
			tmpdto.setSorgcode(loginfo.getSorgcode());
			return commonDataAccessService.findRsByDtoWithWherePaging(tmpdto,
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
		TsAssitflagtransBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

}
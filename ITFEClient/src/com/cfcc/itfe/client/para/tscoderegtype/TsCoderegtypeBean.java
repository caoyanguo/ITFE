package com.cfcc.itfe.client.para.tscoderegtype;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.persistence.dto.TsCoderegtypeDto;
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
 * @time 10-04-08 12:58:21 ��ϵͳ: Para ģ��:TsCoderegtype ���:TsCoderegtype
 */
public class TsCoderegtypeBean extends AbstractTsCoderegtypeBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsCoderegtypeBean.class);
	ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();

	public TsCoderegtypeBean() {
		super();
		dto = new TsCoderegtypeDto();
		pagingcontext = new PagingContext(this);
		init();
	}

	public void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TsCoderegtypeDto();
		dto.setSorgcode(loginfo.getSorgcode());
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 */
	public String inputSave(Object o) {
		if (null == dto.getSregcode() || "".equals(dto.getSregcode())) {
			MessageDialog.openMessageDialog(null, "�ǼǴ��벻��Ϊ�գ�");
			return null;
		}
		if (null == dto.getSregname() || "".equals(dto.getSregname())) {
			MessageDialog.openMessageDialog(null, "�ǼǴ��뺬�岻��Ϊ�գ�");
			return null;
		}
		try {
			dto.setSorgcode(loginfo.getSorgcode());
			tsCoderegtypeService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsCoderegtypeDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsCoderegtypeDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsCoderegtypeDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto.getSregcode() || "".equals(dto.getSregcode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		// ��ʾ�û�ȷ��ɾ��
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�Ҫɾ��ѡ��ĵǼǴ���:"
				+ dto.getSregcode())) {
			return "";
		}
		try {
			tsCoderegtypeService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsCoderegtypeDto();
		init();
		editor.fireModelChanged();
		return super.delete(o);
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if (null == dto.getSregcode() || "".equals(dto.getSregcode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		return super.goModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 */
	public String modifySave(Object o) {
		if (null == dto.getSregname() || "".equals(dto.getSregname())) {
			MessageDialog.openMessageDialog(null, "�ǼǴ��뺬�岻��Ϊ�գ�");
			return null;
		}
		try {
			tsCoderegtypeService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsCoderegtypeDto();
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
		TsCoderegtypeBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

}
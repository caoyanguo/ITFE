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
 * @time 10-04-08 12:58:20 ��ϵͳ: Para ģ��:TsBankandpay ���:TsBankandpay
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
	 * Direction: ��ѯ ename: queryBudget ���÷���: viewers: ��ѯ��� messages:
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
		dto = new TsBankandpayDto();
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
		if (null == dto.getSnationtaxbank()
				|| "".equals(dto.getSnationtaxbank().trim())) {
			MessageDialog.openMessageDialog(null, "��˰���д��벻��Ϊ�գ�");
			return null;
		}
		if (null == dto.getSareataxbank()
				|| "".equals(dto.getSareataxbank().trim())) {
			MessageDialog.openMessageDialog(null, "��˰���д��벻��Ϊ�գ�");
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
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsBankandpayDto();
		init();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsBankandpayDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto.getSnationtaxbank()
				|| "".equals(dto.getSnationtaxbank())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		// ��ʾ�û�ȷ��ɾ��
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�Ҫɾ��ѡ��Ĺ�˰����"
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
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
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
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
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
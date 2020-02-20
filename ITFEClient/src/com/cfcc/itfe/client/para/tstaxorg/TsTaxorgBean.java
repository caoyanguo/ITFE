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
 * @time 10-04-08 12:58:21 ��ϵͳ: Para ģ��:TsTaxorg ���:TsTaxorg
 */
public class TsTaxorgBean extends AbstractTsTaxorgBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsTaxorgBean.class);
	private ITFELoginInfo loginfo;
	// ��ѯdto
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
	 * Direction: ��ѯ ename: query ���÷���: viewers: ά������ messages:
	 */
	public String query(Object o) {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		return super.query(o);
	}

	/**
	 * Direction: ���� ename: goBack ���÷���: viewers: ��Ϣ��ѯ messages:
	 */
	public String goBack(Object o) {

		return super.goBack(o);
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TsTaxorgDto();
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
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsTaxorgDto();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsTaxorgDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == dto.getStaxorgcode() || "".equals(dto.getStaxorgcode())) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		// ��ʾ�û�ȷ��ɾ��
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�Ҫɾ��ѡ������ջ��ش���"
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
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
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
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
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
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
 * @time 09-10-20 08:42:02 ��ϵͳ: Para ģ��:TsInfoconnorg ���:TsInfoconnorg
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
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		dto = new TsInfoconnorgDto();
		dto.setSorgcode(loginfo.getSorgcode());
		this.repeatskey="";
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
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
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsInfoconnorgDto();
		init();
		setRepeatskey("");
		return super.backMaintenance(o);
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsInfoconnorgDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
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
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
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
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
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

	// ��ʼ��
	private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
	}

	// ����У��
	private boolean datacheck(String flag) {
		if (null == dto.getSconnorgcode() || "".equals(dto.getSconnorgcode().trim())) {
			MessageDialog.openMessageDialog(null, "�����������벻��Ϊ�գ�");
			return false;
		}
		if (null == dto.getSkey() || "".equals(dto.getSkey().trim()) || dto.getSkey().trim().length() != 16) {
			MessageDialog.openMessageDialog(null, "��ԿӦ��Ϊ16λ��");
			return false;
		}
		if (null == getRepeatskey() || "".equals(getRepeatskey().trim())) {
			MessageDialog.openMessageDialog(null, "ȷ����Կ����Ϊ�գ�");
			return false;
		}

		if (!dto.getSkey().equals(getRepeatskey())) {
			MessageDialog.openMessageDialog(null, "��Կ��ȷ����Կ����ͬ��");
			return false;
		}
		// ¼������
		if (flag.equals("add")) {
			TsInfoconnorgDto tempdto = new TsInfoconnorgDto();
			tempdto.setSorgcode(loginfo.getSorgcode());
			tempdto.setSconnorgcode(dto.getSconnorgcode());
			try {
				List list = commonDataAccessService.findRsByDto(tempdto);
				if (null != list && list.size() > 0) {
					MessageDialog.openMessageDialog(null, "������������� +�����������롿�����ظ�!");
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
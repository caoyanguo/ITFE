package com.cfcc.itfe.client.para.tsorgan;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:02 ��ϵͳ: Para ģ��:TsOrgan ���:TsOrgan
 */
public class TsOrganBean extends AbstractTsOrganBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsOrganBean.class);
	private List<TsOrganDto> orglist = new ArrayList<TsOrganDto>();
	private String orgLevel = "";
	private String sorgname;
	private String orgstate;
	ITFELoginInfo loginfo;
	
	

	public TsOrganBean() {
		super();
		dto = new TsOrganDto();
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		if (loginfo.getSorgcode().equals(StateConstant.ORG_CENTER_CODE)) {
			dto.setSgovernorgcode(loginfo.getSorgcode());
		} else {
			dto.setSorgcode(loginfo.getSorgcode());
		}
		
//		pagingcontext = new PagingContext(this);
		init();
	}

	/**
	 * Direction: ��ת¼����� ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		if (!loginfo.getSorgcode().equals(StateConstant.ORG_CENTER_CODE)) {
			MessageDialog.openMessageDialog(null, "����������������Ĺ���Աͳһά��,���ܵ���������");
			return null;
		}
		dto = new TsOrganDto();
		dto.setSgovernorgcode(loginfo.getSorgcode());
		setOrgstate("");
		setSorgname("");
		setOrgLevel("");

		return super.goInput(o);

	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 */
	public String inputSave(Object o) {

		if (!datacheck()) {
			return null;
		}
		dto.setSorgstatus(getOrgstate());
		dto.setSorglevel(getOrgLevel());
		dto.setSorgcode(dto.getSorgcode().trim());
		
		try {
			tsOrganService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);

			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsOrganDto();
		setOrgstate("");
		setSorgname("");
		setOrgLevel("");

		return backMaintenance(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: s viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsOrganDto();
		dto.setSgovernorgcode(loginfo.getSorgcode());
		setOrgstate("");
		setSorgname("");
		setOrgLevel("");
		init();
		editor.fireModelChanged();
		return super.backMaintenance(o);

	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		dto = (TsOrganDto) o;
		return super.singleSelect(o);
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (!loginfo.getSorgcode().equals(StateConstant.ORG_CENTER_CODE)) {
			MessageDialog.openMessageDialog(null, "����������������Ĺ���Աͳһά��,���ܵ���������");
			return null;
		}
		if (dto == null || dto.getSorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(editor
				.getCurrentComposite().getShell(), "��ʾ", "�Ƿ�ɾ������"
				+ dto.getSorgcode())) {
			dto = new TsOrganDto();
			return "";
		}
		try {
			tsOrganService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);

		return backMaintenance(o);
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		if (!loginfo.getSorgcode().equals(StateConstant.ORG_CENTER_CODE)) {
			MessageDialog.openMessageDialog(null, "����������������Ĺ���Աͳһά��,���ܵ���������");
			return null;
		}
		if (dto == null || dto.getSorgcode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		setOrgLevel(dto.getSorglevel());
		setSorgname(dto.getSorgname());
		setOrgstate(dto.getSorgstatus());
		return super.goModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 */
	public String modifySave(Object o) {
		if (!datacheck()) {
			return null;
		}
		dto.setSorgstatus(getOrgstate());
		dto.setSorglevel(getOrgLevel());
		try {
			tsOrganService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);

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
		try {
			commonDataAccessService.findRsByDtoUR(dto);
			return commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, "1=1");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	private void init() {
		try {
			orglist = commonDataAccessService.findRsByDtoUR(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
//		PageRequest pageRequest = new PageRequest();
//		PageResponse pageResponse = retrieve(pageRequest);
//		pagingcontext.setPage(pageResponse);
	}

	public String getOrgstate() {
		return orgstate;
	}

	public void setOrgstate(String orgstate) {
		this.orgstate = orgstate;
	}

	public String getOrgLevel() {
		return orgLevel;
	}

	public void setOrgLevel(String orgLevel) {
		this.orgLevel = orgLevel;
	}

	public String getSorgname() {
		return sorgname;
	}

	public void setSorgname(String sorgname) {
		this.sorgname = sorgname;
	}

	private boolean datacheck() {
		if (null == dto.getSorgcode() || "".equals(dto.getSorgcode().trim())) {
			MessageDialog.openMessageDialog(null, "����������벻��Ϊ�գ�");
			return false;
		}
		if(!isNumber(dto.getSorgcode()) || 12 != dto.getSorgcode().trim().length()){
			MessageDialog.openMessageDialog(null, "��������������Ϊ12λ���֣�");
			return false;
		}
		
		if (null == dto.getSorgname() || "".equals(dto.getSorgname().trim())) {
			MessageDialog.openMessageDialog(null, "�����������Ʋ���Ϊ�գ�");
			return false;
		}
//		if (null == orgLevel || "".equals(orgLevel.trim())) {
//			MessageDialog.openMessageDialog(null, "�������β���Ϊ�գ�");
//			return false;
//		}
		if (null == orgstate || "".equals(orgstate.trim())) {
			MessageDialog.openMessageDialog(null, "����״̬����Ϊ�գ�");
			return false;
		}
		return true;

	}
	
	/**
	 * �ж��ַ����Ƿ����������
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str)
    {
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]*");
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false)
        {
           return false;
        }
        else
        {
           return true;
        }
    }

	public List<TsOrganDto> getOrglist() {
		return orglist;
	}

	public void setOrglist(List<TsOrganDto> orglist) {
		this.orglist = orglist;
	}

}
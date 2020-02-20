package com.cfcc.itfe.client.para.tsusersfunc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsSysfuncDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsUsersfuncDto;
import com.cfcc.itfe.security.Md5App;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.mvc.editors.AbstractMetaDataEditorPart;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:01 ��ϵͳ: Para ģ��:TsUsersfunc ���:TsUsersfunc
 */
public class TsUsersfuncBean extends AbstractTsUsersfuncBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsUsersfuncBean.class);
	private String selectorgcode = "";
	private ITFELoginInfo loginInfo;
	private List<TsOrganDto> orginfoList = new ArrayList<TsOrganDto>();
	private List<TsUsersDto> userList = new ArrayList<TsUsersDto>();
	private List<TsSysfuncDto> sysFuncList = new ArrayList<TsSysfuncDto>();
	private List<TsSysfuncDto> checkSysFuncList = new ArrayList<TsSysfuncDto>();
	private TsUsersDto userdto = new TsUsersDto();
	private String orgCenter;
	// ���ڿ���ҵ�������Ȩ��
	private List<TdEnumvalueDto> buttonFuncList = new ArrayList<TdEnumvalueDto>();
	private List<TdEnumvalueDto> checkButtonFuncList = new ArrayList<TdEnumvalueDto>();
	private List<TsUsersDto> oprUserList = new ArrayList<TsUsersDto>();
	private TsUsersDto oprUserdto = new TsUsersDto();
	private String oprorgcode = "";

	public void setSelectorgcode(String selectorgcode) {
		this.selectorgcode = selectorgcode;
		List list = queryUserbyOrg(selectorgcode);
		if(StateConstant.ORG_CENTER_CODE.equals(selectorgcode))
		{
			sysFuncList = new ArrayList<TsSysfuncDto>();
			checkSysFuncList = new ArrayList<TsSysfuncDto>();
		}else
		{
			if (list != null && list.size() > 0) {
				querySysFunc();
	
			} else {
				sysFuncList.clear();
			}
		}
		editor.fireModelChanged();
	}

	public void setOprorgcode(String oprorgcode) {
		this.oprorgcode = oprorgcode;
		List list = queryOprUserbyOrg(oprorgcode);
		if (list != null && list.size() > 0) {
			queryOprFunc();
		} else {
			buttonFuncList.clear();
		}
		editor.fireModelChanged();
	}

	public TsUsersfuncBean() {
		super();
		dto = new TsUsersfuncDto();
		init();
	}

	/**
	 * Direction: ��ת¼����� ename: goInput ���÷���: viewers: ¼����� messages:
	 */
	public String goInput(Object o) {
		return super.goInput(o);
	}

	/**
	 * Direction: ¼�뱣�� ename: inputSave ���÷���: viewers: * messages:
	 */
	public String inputSave(Object o) {

		if (null == userdto || userdto.getSusercode() == null) {
			MessageDialog.openMessageDialog(null, "��ѡ���û���¼");
			return null;
		} else {
			List<TsUsersfuncDto> funcList = new ArrayList<TsUsersfuncDto>();
			if (this.checkSysFuncList != null
					&& this.checkSysFuncList.size() > 0) {
				for (int i = 0; i < checkSysFuncList.size(); i++) {
					TsUsersfuncDto userfuncdto = new TsUsersfuncDto();
					userfuncdto.setSorgcode(userdto.getSorgcode());
					userfuncdto.setSusercode(userdto.getSusercode());
					userfuncdto.setSfunccode(checkSysFuncList.get(i)
							.getSfunccode());
					funcList.add(userfuncdto);
				}
			} else {
				TsUsersfuncDto userfuncdto = new TsUsersfuncDto();
				userfuncdto.setSorgcode(userdto.getSorgcode());
				userfuncdto.setSusercode(userdto.getSusercode());
				funcList.add(userfuncdto);
			}
			try {
				tsUsersfuncService.addInfo(funcList);
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return null;
			}

		}
		MessageDialog.openMessageDialog(null, "���ݱ���ɹ�");
		// checkSysFuncList.clear();

		return super.inputSave(o);
	}

	/**
	 * Direction: ���ص�ά������ ename: backMaintenance ���÷���: viewers: ά������ messages:
	 */
	public String backMaintenance(Object o) {
		MVCUtils.closeEditor(this.editor, false);
		return super.backMaintenance(o);
	}

	public String selectAll(Object o) {
		if (checkSysFuncList != null
				&& checkSysFuncList.size() == sysFuncList.size()) {
			checkSysFuncList = new ArrayList<TsSysfuncDto>();
			return "�û�Ȩ��ά��";
		}
		checkSysFuncList = new ArrayList<TsSysfuncDto>();
		checkSysFuncList.addAll(sysFuncList);
		return "�û�Ȩ��ά��";
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		userdto = (TsUsersDto) o;
		try {
			if(StateConstant.ORG_CENTER_CODE.equals(userdto.getSorgcode()))
			{
				sysFuncList = new ArrayList<TsSysfuncDto>();
				checkSysFuncList = new ArrayList<TsSysfuncDto>();
			}else
			{
				sysFuncList = tsUsersfuncService.sysFuncList(userdto);
				checkSysFuncList = tsUsersfuncService.queryUserFunc(userdto);
			}
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		AbstractMetaDataEditorPart holder = (AbstractMetaDataEditorPart) this
				.getModelHolder();
		holder.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);

		return null;
	}

	/**
	 * Direction: ɾ�� ename: delete ���÷���: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == userdto || userdto.getSusercode() == null) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���Ȩ�޵��û���");
			return "";
		}
		try {
			if (!checkAdmin()) {
				MessageDialog.openMessageDialog(null, "û��Ȩ�޽��д������������ϵ����Ա!");
				return "";
			}
			TsUsersDto deldto = new TsUsersDto();
			deldto.setSorgcode(userdto.getSorgcode());
			deldto.setSusercode(userdto.getSusercode());
			tsUsersfuncService.delInfo(deldto);
			checkSysFuncList.clear();
			editor.fireModelChanged();
			MessageDialog.openMessageDialog(null, "�û�" + userdto.getSusercode()
					+ "��Ȩ���Ѿ������������Ϊ���û���Ȩ��");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.delete(o);
	}

	/**
	 * Direction: �������� ename: repeatPwd ���÷���: viewers: * messages:
	 */
	public String repeatPwd(Object o) {
		if (null == userdto || userdto.getSusercode() == null) {
			MessageDialog.openMessageDialog(null, "��ѡ��Ҫ����������û�");
			return "";
		}
		org.eclipse.jface.dialogs.MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
				null, "����������ʾ", null, "�Ƿ�ȷ��Ҫ���û�[" + userdto.getSusercode()
						+ "]����������",
				org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
						"ȷ��", "ȡ��" }, 0);
		if (msg.open() == org.eclipse.jface.dialogs.MessageDialog.OK) {
			String pwd = new Md5App().makeMd5(StateConstant.User_Default_PWD);
			userdto.setSpassword(pwd);
			userdto.setSpassmoddate(TimeFacade.getCurrentStringTime());
			try {
				commonDataAccessService.updateData(userdto);
				MessageDialog.openMessageDialog(null, "�û�["
						+ userdto.getSusercode() + "]�������ѱ�����Ϊ" + "'"
						+ StateConstant.User_Default_PWD + "'");
				userdto = null;
			} catch (ITFEBizException e) {
				log.error(e);
				MessageDialog.openErrorDialog(null, e);
				return "";
			}
		} else {
			return "";
		}
		return super.repeatPwd(o);
	}

	public boolean checkAdmin() {
		TsUsersDto user = new TsUsersDto();
		user.setSusercode(loginInfo.getSuserCode());
		user.setSorgcode(loginInfo.getSorgcode());
		try {
			List l = commonDataAccessService.findRsByDtoUR(user);
			if (null != l && l.size() > 0) {
				TsUsersDto dto = (TsUsersDto) l.get(0);
				if (StateConstant.User_Type_Admin.equals(dto.getSusertype())
						|| loginInfo.getSorgcode().equals(
								StateConstant.ORG_CENTER_CODE)) { // �ж��Ƿ��ǹ���Ա�û�
					return true;
				} else {
					return false;
				}
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return false;
	}

	/**
	 * Direction: ���޸Ľ��� ename: goModify ���÷���: viewers: �޸Ľ��� messages:
	 */
	public String goModify(Object o) {
		return super.goModify(o);
	}

	/**
	 * Direction: �޸ı��� ename: modifySave ���÷���: viewers: * messages:
	 */
	public String modifySave(Object o) {
		return super.modifySave(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest arg0) {
		return super.retrieve(arg0);
	}

	private void init() {
		loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
		// ȡ���ĵĺ����������
		TsOrganDto orgdto = new TsOrganDto();
		orgdto.setSorglevel(StateConstant.ORG_CENTER_LEVEL);
		orgdto.setSorgstatus(StateConstant.ORG_STATE);
		try {
			List<TsOrganDto> list = commonDataAccessService.findRsByDto(orgdto);
			if (null != list && list.size() > 0) {
				orgCenter = list.get(0).getSorgcode();
			} else {
				MessageDialog.openMessageDialog(null, "ȡ���ĺ�������������");
				return;
			}
		} catch (Throwable e1) {
			MessageDialog.openErrorDialog(null, e1);
			return;
		}

		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		// ��ʼ������������
		try {
			orginfoList = tsUsersfuncService.queryOrg(dto);

			// if (orginfoList.size()>0) {
			// TsOrganDto _dto= orginfoList.get(0);
			// setSelectorgcode(_dto.getSorgcode());
			// }
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
	}

	// ����������ѡ���ѯ�û���Ϣ
	private List queryUserbyOrg(String _sorgcode) {
		TsUsersDto dto = new TsUsersDto();
		// if (!_sorgcode.equals(orgCenter)) {
		dto.setSorgcode(_sorgcode);
		// }
		try {
			userList = commonDataAccessService.findRsByDtoUR(dto);
			// editor.fireModelChanged();
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return userList;
	}

	// ����������ѡ���ѯ�û���Ϣ
	private List queryOprUserbyOrg(String _sorgcode) {
		TsUsersDto dto = new TsUsersDto();
		// if (!_sorgcode.equals(orgCenter)) {
		dto.setSorgcode(_sorgcode);
		dto.setSusertype(StateConstant.User_Type_Normal);
		// }
		try {
			oprUserList = commonDataAccessService.findRsByDtoUR(dto);
			// editor.fireModelChanged();
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return oprUserList;
	}

	// ��ʼ��ϵͳ���ܲ˵�
	private void querySysFunc() {
		TsSysfuncDto dto = new TsSysfuncDto();
		if (!loginInfo.getSorgcode().equals(orgCenter)) {
			// ��������Ĺ���Ա����ô���Կ�������Ȩ�ޣ�����ֻ�ܿ����ض���Ȩ��
			// dto.setSflag("1");
		}
		try {
			sysFuncList = tsUsersfuncService.initSysFunc(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if (sysFuncList.size() > 0) {
			checkSysFuncList = sysFuncList;
		}

	}

	// ��ʼ��ҵ��ƾ֤�����ܰ�ť
	private void queryOprFunc() {
		try {
			TdEnumvalueDto idto = new TdEnumvalueDto();
			idto.setStypecode("0900");
			buttonFuncList = commonDataAccessService.findRsByDto(idto,
					"order by s_value");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}

	}

	/**
	 * Direction: ��ѡ ename: oprSingleSelect ���÷���: viewers: * messages:
	 */
	public String oprSingleSelect(Object o) {
		oprUserdto = (TsUsersDto) o;
		try {
			checkButtonFuncList.clear();
			String s = oprUserdto.getShold2();
			if (null != s && !"".equals(s.trim())) {
				String[] arr = s.split(",");
				for (int i = 0; i < arr.length; i++) {
					if (StateConstant.COMMON_YES.equals(arr[i])) {
						checkButtonFuncList.add(buttonFuncList.get(i));
					}
				}
			}

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		AbstractMetaDataEditorPart holder = (AbstractMetaDataEditorPart) this
				.getModelHolder();
		holder.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);

		return null;
	}

	/**
	 * Direction: ȫѡ1 ename: selectFundAll ���÷���: viewers: * messages:
	 */
	public String selectFundAll(Object o) {
		if (checkButtonFuncList != null
				&& checkButtonFuncList.size() == buttonFuncList.size()) {
			checkButtonFuncList = new ArrayList<TdEnumvalueDto>();
			return "ҵ����Ȩ��ά��";
		}
		checkButtonFuncList = new ArrayList<TdEnumvalueDto>();
		checkButtonFuncList.addAll(buttonFuncList);
		return "ҵ����Ȩ��ά��";
	}

	/**
	 * Direction: ����1 ename: inputOprSave ���÷���: viewers: * messages:
	 */
	public String inputSaveOpr(Object o) {
		String s = "";
		for (int i = 0; i < buttonFuncList.size(); i++) {
			TdEnumvalueDto _dto = buttonFuncList.get(i);
			if (checkButtonFuncList.contains(_dto)) {
				s += "1,";
			} else {
				s += "0,";
			}
		}
		try {
			oprUserdto.setShold2(s);
			commonDataAccessService.updateData(oprUserdto);
			MessageDialog.openMessageDialog(null, "����ɹ���");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return "";
	}

	public List<TsOrganDto> getOrginfoList() {
		return orginfoList;
	}

	public void setOrginfoList(List<TsOrganDto> orginfoList) {
		this.orginfoList = orginfoList;
	}

	public TsUsersDto getUserdto() {
		return userdto;
	}

	public void setUserdto(TsUsersDto userdto) {
		this.userdto = userdto;
	}

	public String getOrgCenter() {
		return orgCenter;
	}

	public void setOrgCenter(String orgCenter) {
		this.orgCenter = orgCenter;
	}

	public List<TsUsersDto> getUserList() {
		return userList;
	}

	public void setUserList(List<TsUsersDto> userList) {
		this.userList = userList;
	}

	public List<TsSysfuncDto> getSysFuncList() {
		return sysFuncList;
	}

	public void setSysFuncList(List<TsSysfuncDto> sysFuncList) {
		this.sysFuncList = sysFuncList;
	}

	public List<TsSysfuncDto> getCheckSysFuncList() {
		return checkSysFuncList;
	}

	public void setCheckSysFuncList(List<TsSysfuncDto> checkSysFuncList) {
		this.checkSysFuncList = checkSysFuncList;
	}

	public String getSelectorgcode() {
		return selectorgcode;
	}

	public List<TdEnumvalueDto> getButtonFuncList() {
		return buttonFuncList;
	}

	public void setButtonFuncList(List<TdEnumvalueDto> buttonFuncList) {
		this.buttonFuncList = buttonFuncList;
	}

	public List<TdEnumvalueDto> getCheckButtonFuncList() {
		return checkButtonFuncList;
	}

	public void setCheckButtonFuncList(List<TdEnumvalueDto> checkButtonFuncList) {
		this.checkButtonFuncList = checkButtonFuncList;
	}

	public String getOprorgcode() {
		return oprorgcode;
	}

	public List<TsUsersDto> getOprUserList() {
		return oprUserList;
	}

	public void setOprUserList(List<TsUsersDto> oprUserList) {
		this.oprUserList = oprUserList;
	}

	public TsUsersDto getOprUserdto() {
		return oprUserdto;
	}

	public void setOprUserdto(TsUsersDto oprUserdto) {
		this.oprUserdto = oprUserdto;
	}
	
}
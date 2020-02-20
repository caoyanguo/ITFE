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
 * @time 09-10-20 08:42:01 子系统: Para 模块:TsUsersfunc 组件:TsUsersfunc
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
	// 用于控制业务处理界面权限
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
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 */
	public String inputSave(Object o) {

		if (null == userdto || userdto.getSusercode() == null) {
			MessageDialog.openMessageDialog(null, "请选择用户记录");
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
		MessageDialog.openMessageDialog(null, "数据保存成功");
		// checkSysFuncList.clear();

		return super.inputSave(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		MVCUtils.closeEditor(this.editor, false);
		return super.backMaintenance(o);
	}

	public String selectAll(Object o) {
		if (checkSysFuncList != null
				&& checkSysFuncList.size() == sysFuncList.size()) {
			checkSysFuncList = new ArrayList<TsSysfuncDto>();
			return "用户权限维护";
		}
		checkSysFuncList = new ArrayList<TsSysfuncDto>();
		checkSysFuncList.addAll(sysFuncList);
		return "用户权限维护";
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
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
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (null == userdto || userdto.getSusercode() == null) {
			MessageDialog.openMessageDialog(null, "请选择要清除权限的用户。");
			return "";
		}
		try {
			if (!checkAdmin()) {
				MessageDialog.openMessageDialog(null, "没有权限进行此项操作，请联系管理员!");
				return "";
			}
			TsUsersDto deldto = new TsUsersDto();
			deldto.setSorgcode(userdto.getSorgcode());
			deldto.setSusercode(userdto.getSusercode());
			tsUsersfuncService.delInfo(deldto);
			checkSysFuncList.clear();
			editor.fireModelChanged();
			MessageDialog.openMessageDialog(null, "用户" + userdto.getSusercode()
					+ "的权限已经清除，请重新为该用户授权。");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.delete(o);
	}

	/**
	 * Direction: 重置密码 ename: repeatPwd 引用方法: viewers: * messages:
	 */
	public String repeatPwd(Object o) {
		if (null == userdto || userdto.getSusercode() == null) {
			MessageDialog.openMessageDialog(null, "请选择要重置密码的用户");
			return "";
		}
		org.eclipse.jface.dialogs.MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
				null, "密码重置提示", null, "是否确认要将用户[" + userdto.getSusercode()
						+ "]的密码重置",
				org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
						"确认", "取消" }, 0);
		if (msg.open() == org.eclipse.jface.dialogs.MessageDialog.OK) {
			String pwd = new Md5App().makeMd5(StateConstant.User_Default_PWD);
			userdto.setSpassword(pwd);
			userdto.setSpassmoddate(TimeFacade.getCurrentStringTime());
			try {
				commonDataAccessService.updateData(userdto);
				MessageDialog.openMessageDialog(null, "用户["
						+ userdto.getSusercode() + "]的密码已被重置为" + "'"
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
								StateConstant.ORG_CENTER_CODE)) { // 判断是否是管理员用户
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
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		return super.goModify(o);
	}

	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
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
		// 取中心的核算主体代码
		TsOrganDto orgdto = new TsOrganDto();
		orgdto.setSorglevel(StateConstant.ORG_CENTER_LEVEL);
		orgdto.setSorgstatus(StateConstant.ORG_STATE);
		try {
			List<TsOrganDto> list = commonDataAccessService.findRsByDto(orgdto);
			if (null != list && list.size() > 0) {
				orgCenter = list.get(0).getSorgcode();
			} else {
				MessageDialog.openMessageDialog(null, "取中心核算主体代码出错");
				return;
			}
		} catch (Throwable e1) {
			MessageDialog.openErrorDialog(null, e1);
			return;
		}

		loginInfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		// 初始化机构下拉框
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

	// 根据下拉框选择查询用户信息
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

	// 根据下拉框选择查询用户信息
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

	// 初始化系统功能菜单
	private void querySysFunc() {
		TsSysfuncDto dto = new TsSysfuncDto();
		if (!loginInfo.getSorgcode().equals(orgCenter)) {
			// 如果是中心管理员，那么可以看到所有权限，否则只能看到特定的权限
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

	// 初始化业务凭证处理功能按钮
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
	 * Direction: 单选 ename: oprSingleSelect 引用方法: viewers: * messages:
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
	 * Direction: 全选1 ename: selectFundAll 引用方法: viewers: * messages:
	 */
	public String selectFundAll(Object o) {
		if (checkButtonFuncList != null
				&& checkButtonFuncList.size() == buttonFuncList.size()) {
			checkButtonFuncList = new ArrayList<TdEnumvalueDto>();
			return "业务处理权限维护";
		}
		checkButtonFuncList = new ArrayList<TdEnumvalueDto>();
		checkButtonFuncList.addAll(buttonFuncList);
		return "业务处理权限维护";
	}

	/**
	 * Direction: 保存1 ename: inputOprSave 引用方法: viewers: * messages:
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
			MessageDialog.openMessageDialog(null, "保存成功！");
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
package com.cfcc.itfe.client.para.tsuserstampfunction;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsUserstampDto;
import com.cfcc.itfe.persistence.dto.TsUserstampfunctionDto;
import com.cfcc.itfe.persistence.dto.UserStampInfoDto;
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
 * @time 09-10-20 08:42:02 子系统: Para 模块:TsUserstampfunction
 *       组件:TsUserstampfunction
 */
public class TsUserstampfunctionBean extends AbstractTsUserstampfunctionBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TsUserstampfunctionBean.class);

	private String selectorgcode = "";
	private List<TdEnumvalueDto> orginfoList = new ArrayList<TdEnumvalueDto>();
	private List<UserStampInfoDto> userStampList = new ArrayList<UserStampInfoDto>();
	private List<UserStampInfoDto> userStampInfoList = new ArrayList<UserStampInfoDto>();
	private List<UserStampInfoDto> userStampInfoCheckList = new ArrayList<UserStampInfoDto>();
	private UserStampInfoDto userstampdto;
	private String orgCenter;
	private ITFELoginInfo loginfo;

	public List<UserStampInfoDto> getUserStampInfoList() {
		return userStampInfoList;
	}

	public void setUserStampInfoList(List<UserStampInfoDto> userStampInfoList) {
		this.userStampInfoList = userStampInfoList;
	}

	public List<UserStampInfoDto> getUserStampInfoCheckList() {
		return userStampInfoCheckList;
	}

	public void setUserStampInfoCheckList(
			List<UserStampInfoDto> userStampInfoCheckList) {
		this.userStampInfoCheckList = userStampInfoCheckList;
	}

	public UserStampInfoDto getUserstampdto() {
		return userstampdto;
	}

	public void setUserstampdto(UserStampInfoDto userstampdto) {
		this.userstampdto = userstampdto;
	}

	public TsUserstampfunctionBean() {
		super();
		dto = new TsUserstampfunctionDto();
		init();

	}

	public String getSelectorgcode() {
		return selectorgcode;
	}

	public void setSelectorgcode(String selectorgcode) {
		this.selectorgcode = selectorgcode;
		List list = queryUserbyOrg(selectorgcode);
		if (list != null && list.size() > 0) {
			querystampplace();
		} else {
			userStampList.clear();
			userStampInfoList.clear();
		}
		editor.fireModelChanged();

	}

	public List<TdEnumvalueDto> getOrginfoList() {
		return orginfoList;
	}

	public void setOrginfoList(List<TdEnumvalueDto> orginfoList) {
		this.orginfoList = orginfoList;
	}

	public List<UserStampInfoDto> getUserStampList() {
		return userStampList;
	}

	public void setUserStampList(List<UserStampInfoDto> userStampList) {
		this.userStampList = userStampList;
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

		if (null == userstampdto || userstampdto.getSusercode() == null) {
			MessageDialog.openMessageDialog(null, "请选择用户签章记录");
			return null;
		} else {
			for (int i = 0; i < userStampInfoCheckList.size(); i++) {
				if (!userstampdto.getSstamptypecode().equals(
						userStampInfoCheckList.get(i).getSstamptypecode())) {
					MessageDialog
							.openMessageDialog(null, "签章类型不一致,用户列表对应"
									+ userstampdto.getSstamptypename()
									+ "在盖章位置中选择了"
									+ userStampInfoCheckList.get(i)
											.getSstamptypename());
					return null;
				}
			}
		}
		List<TsUserstampfunctionDto> funcList = new ArrayList<TsUserstampfunctionDto>();
		if (null != this.userStampInfoCheckList
				&& this.userStampInfoCheckList.size() > 0) {
			for (int i = 0; i < userStampInfoCheckList.size(); i++) {
				TsUserstampfunctionDto userfuncdto = new TsUserstampfunctionDto();
				userfuncdto.setSorgcode(selectorgcode);
				userfuncdto.setSusercode(userstampdto.getSusercode());
				userfuncdto.setSstamptypecode(userstampdto.getSstamptypecode());
				userfuncdto.setSmodelid(userStampInfoCheckList.get(i)
						.getSmodelid());
				userfuncdto.setSplaceid(userStampInfoCheckList.get(i)
						.getSplaceid());
				funcList.add(userfuncdto);
			}
		} else {
			TsUserstampfunctionDto userfuncdto = new TsUserstampfunctionDto();
			userfuncdto.setSorgcode(selectorgcode);
			userfuncdto.setSusercode(userstampdto.getSusercode());
			userfuncdto.setSstamptypecode(userstampdto.getSstamptypecode());
			funcList.add(userfuncdto);
		}
		try {
			tsUserstampfunctionService.addInfo(funcList);
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return null;
		}

		MessageDialog.openMessageDialog(null, "数据保存成功");
		// userStampInfoCheckList.clear();

		return super.inputSave(o);

	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		MVCUtils.closeEditor(this.editor, false);
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		userstampdto = (UserStampInfoDto) o;
		try {
			TsUserstampfunctionDto userStamp = new TsUserstampfunctionDto();
			userStamp.setSstamptypecode(userstampdto.getSstamptypecode());
			userStampInfoList = tsUserstampfunctionService.findstampPosInfo(userStamp);
			userStampInfoCheckList = tsUserstampfunctionService.findstampPosCheckInfo(userstampdto);
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
		return super.delete(o);
	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		return super.goModify(o);
	}

	/*
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
		} catch (ITFEBizException e1) {
			MessageDialog.openErrorDialog(null, e1);
			return;
		}

		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();

		// 初始化机构下拉框
		try {
			orginfoList = commonDataAccessService.findOrgList(loginfo
					.getSorgcode());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
	}

	// 根据下拉框选择查询用户信息
	private List queryUserbyOrg(String _sorgcode) {
		TsUserstampDto dto = new TsUserstampDto();
		if (!_sorgcode.equals(orgCenter)) {
			dto.setSorgcode(_sorgcode);
		}
		try {
			userStampList = tsUserstampfunctionService.findUserInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return userStampList;
	}

	// 初始化盖章位置列表
	private void querystampplace() {
		TsUserstampfunctionDto dto = new TsUserstampfunctionDto();
		try {
			userStampInfoList = tsUserstampfunctionService.findstampPosInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if (null != userStampInfoList && userStampInfoList.size() > 0) {
			userStampInfoCheckList = userStampInfoList;
		}

	}

}
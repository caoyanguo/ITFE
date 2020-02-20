package com.cfcc.itfe.client.para.tsusers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.common.encrypt.KoalClientPkcs7Encrypt;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.AsspOcx;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.security.Md5App;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author caoyg
 * @time 09-10-20 08:42:02 子系统: Para 模块:TsUsers 组件:TsUsers
 */
public class TsUsersBean extends AbstractTsUsersBean implements
		IPageDataProvider {

	private static Log log = LogFactory.getLog(TsUsersBean.class);
	private List list2;
	private String susertype;
	private String suserstatus;
	private String sorgcode;
	private String orgCenter;
	private List<TsOrganDto> list = new ArrayList<TsOrganDto>();
	private List<TsUsersDto> userList = new ArrayList<TsUsersDto>();
	// 选择签章list
	private List<TdEnumvalueDto> checklist = null;
	// 签章名称list
	private List<TdEnumvalueDto> reportlist = null;

	ITFELoginInfo loginfo;


	public String getOrgCenter() {
		return orgCenter;
	}

	public void setOrgCenter(String orgCenter) {
		this.orgCenter = orgCenter;
	}

	public List getList2() {
		return list2;
	}

	public void setList2(List list2) {
		this.list2 = list2;
	}

	public String getSusertype() {
		return susertype;
	}

	public void setSusertype(String susertype) {
		this.susertype = susertype;
	}

	public String getSuserstatus() {
		return suserstatus;
	}

	public void setSuserstatus(String suserstatus) {
		this.suserstatus = suserstatus;
	}

	public String getSorgcode() {
		return sorgcode;
	}

	public void setSorgcode(String sorgcode) {
		this.sorgcode = sorgcode;
	}

	public TsUsersBean() {
		super();
		dto = new TsUsersDto();
		// pagingcontext = new PagingContext(this);
		loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor.getDefault()
				.getLoginInfo();
		init();
	}

	/**
	 * Direction: 跳转录入界面 ename: goInput 引用方法: viewers: 录入界面 messages:
	 */
	public String goInput(Object o) {
		if(!checkAdmin()) {
			MessageDialog.openMessageDialog(null, "没有权限进行此项操作,请联系管理员!");
			return "";
		}
		dto = new TsUsersDto();
		setSorgcode("");
		setSuserstatus("1");
		setSusertype("");
		dto.setSpassmodcycle(30);
		String date="";
		try {
		    date=commonDataAccessService.getSysDate();
		    date=date.replace("-", "");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "获取系统时间出错："+e.getMessage());
		}
		dto.setSpassmoddate(date);
		return super.goInput(o);
	}

	/**
	 * Direction: 录入保存 ename: inputSave 引用方法: viewers: * messages:
	 */
	public String inputSave(Object o) {
		if (!datacheck()) {
			return null;
		}
		if (!loginfo.getSorgcode().equals(StateConstant.ORG_CENTER_CODE))
		{
			if(loginfo.getPublicparam().contains(",payoutstampmode=true,")||loginfo.getPublicparam().contains(",adduser=verify,"))
			{
				if(!AdminConfirmDialogFacade.open("B_018", "用户信息维护", "新增用户", "新增用户会计主管授权!")){
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					return null;
				}
			}
		}
		//获取签章权限
		StringBuffer stamppermission = new StringBuffer();
		for(int i = 0; i < checklist.size(); i++){
			TdEnumvalueDto valuedto = checklist.get(i);
			if(i<checklist.size()-1){
				stamppermission.append(valuedto.getSvalue()+",");
			}else{
				stamppermission.append(valuedto.getSvalue());
			}
		}
		dto.setSstamppermission(stamppermission.toString());
		dto.setSusertype(getSusertype());
		dto.setSuserstatus(getSuserstatus());
		dto.setSorgcode(getSorgcode());
		dto.setImodicount(0);
		//默认给用户分配全部的业务处理权限
		dto.setShold2("1,1,1,1,1,1,1,1,1,1,");
		String date="";
		try {
		    date=commonDataAccessService.getSysDate();
		    date=date.replace("-", "");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "获取系统时间出错："+e.getMessage());
		}
		dto.setSpassmoddate(date);
		try {
			tsUsersService.addInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.inputSave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.INPUTSAVE);
		dto = new TsUsersDto();
		setSorgcode("");
		setSuserstatus("");
		setSusertype("");
		return backMaintenance(o);
	}

	/**
	 * Direction: 返回到维护界面 ename: backMaintenance 引用方法: viewers: 维护界面 messages:
	 */
	public String backMaintenance(Object o) {
		dto = new TsUsersDto();
		setSorgcode("");
		setSuserstatus("");
		setSusertype("");
		init();
		editor.fireModelChanged();
		return super.backMaintenance(o);
	}

	/**
	 * Direction: 单选 ename: singleSelect 引用方法: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		TsUsersDto tmpdto = new TsUsersDto();
		tmpdto = (TsUsersDto) o;
		try {
			dto=tsUsersService.getSingleInfo(tmpdto);
		} catch (ITFEBizException e) {
			e.printStackTrace();
			log.info("查询用户信息异常:"+e.getMessage());
		}
		return super.singleSelect(o);
	}

	/**
	 * 从Usb-Key中获取DN
	 */
	public String getDNFromKey(Object o) {
		AsspOcx asspOcx = new AsspOcx(new Shell());
		dto.setScertid(asspOcx.DlgSelectCertId());
		dto.setSstampid(asspOcx.DlgSelectStampId());
		editor.fireModelChanged();
		return super.getDNFromKey(o);
	}

	/**
	 * Direction: 删除 ename: delete 引用方法: viewers: * messages:
	 */
	public String delete(Object o) {
		if (dto.getSusercode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if(!checkAdmin()) {
			MessageDialog.openMessageDialog(null, "没有权限进行此项操作,请联系管理员!");
			return "";
		}
		// 提示用户确定删除
		if (!org.eclipse.jface.dialogs.MessageDialog.openQuestion(this.editor
				.getCurrentComposite().getShell(), "提示", "是否要删除选择的用户"
				+ dto.getSusercode())) {
			return "";
		}
		if ((dto.getSorgcode().equals(loginfo.getSorgcode()))
				&& (dto.getSusercode().equals(loginfo.getSuserCode()))) {
			// 不能删除目前登陆的用户
			MessageDialog.openMessageDialog(null, "不能删除当前登录用户"
					+ dto.getSusercode());
			return "";
		}
		try {
			if(loginfo.getPublicparam().contains(",payoutstampmode=true,")||loginfo.getPublicparam().contains(",adduser=verify,"))
			{
				if(!AdminConfirmDialogFacade.open("B_018", "用户信息维护", "删除用户", "删除用户会计主管授权!")){
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					return null;
				}
			}
			tsUsersService.delInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.delete(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.DELETESAVE);
		dto = new TsUsersDto();
		return backMaintenance(o);
	}

	/**
	 * Direction: 到修改界面 ename: goModify 引用方法: viewers: 修改界面 messages:
	 */
	public String goModify(Object o) {
		if (dto == null || dto.getSusercode() == null) {
			MessageDialog.openMessageDialog(null, StateConstant.DELETESELECT);
			return "";
		}
		if(!checkAdmin()) {
			if(!dto.getSusercode().equals(loginfo.getSuserCode())) { //除了管理员只能修改自己的信息
				MessageDialog.openMessageDialog(null, "非管理员用户,只能修改本用户信息!");
				return "";
			}
		}
		setSorgcode(dto.getSorgcode());
		setSuserstatus(dto.getSuserstatus());
		setSusertype(dto.getSusertype());
		if (null!=dto.getSstamppermission()) {
		//获取当前用户权限
		String[] stamppermission = dto.getSstamppermission().split(",");
		List permissionlist = Arrays.asList(stamppermission);
		for(int i = 0; i < reportlist.size(); i++){
			TdEnumvalueDto valuedto = reportlist.get(i);
			if(permissionlist.contains(valuedto.getSvalue())){
				checklist.add(valuedto);
			}
		}
		}
		String date="";
		try {
		    date=commonDataAccessService.getSysDate();
		    date=date.replace("-", "");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openMessageDialog(null, "获取系统时间出错："+e.getMessage());
		}
		dto.setSpassmoddate(date);
		return super.goModify(o);
	}

	/**
	 * Direction: 修改保存 ename: modifySave 引用方法: viewers: * messages:
	 */
	public String modifySave(Object o) {
		if (!datacheck()) {
			return null;
		}
		//获取签章权限
		StringBuffer stamppermission = new StringBuffer();
		for(int i = 0; i < checklist.size(); i++){
			TdEnumvalueDto valuedto = checklist.get(i);
			if(i<checklist.size()-1){
				stamppermission.append(valuedto.getSvalue()+",");
			}else{
				stamppermission.append(valuedto.getSvalue());
			}
		}
		dto.setSstamppermission(stamppermission.toString());
		dto.setSusertype(getSusertype());
		dto.setSuserstatus(getSuserstatus());
		dto.setSorgcode(getSorgcode());
		try {
			tsUsersService.modInfo(dto);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return super.modifySave(o);
		}
		MessageDialog.openMessageDialog(null, StateConstant.MODIFYSAVE);
		dto = new TsUsersDto();

		return backMaintenance(o);
	}

	public boolean checkAdmin() {
		TsUsersDto user = new TsUsersDto();
		user.setSusercode(loginfo.getSuserCode());
		user.setSorgcode(loginfo.getSorgcode());
		try {
			List l = commonDataAccessService.findRsByDtoUR(user);
			if(null != l && l.size() > 0) {
				TsUsersDto dto = (TsUsersDto)l.get(0);
				if(loginfo.getPublicparam().indexOf(",payoutstampmode=true,")>=0)//厦门角色管理管理员和业务主管互换
				{
						return true;
				}else
				{
					if (StateConstant.User_Type_Admin.equals(dto.getSusertype())|| loginfo.getSorgcode().equals(StateConstant.ORG_CENTER_CODE)) { // 判断是否是管理员用户
						return true;
					} else {
						return false;
					}
				}
			}
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {
		if (!loginfo.getSorgcode().equals(orgCenter)) {
			dto.setSorgcode(loginfo.getSorgcode());
		} else {
			dto = new TsUsersDto();
		}
		try {
			PageResponse response=new PageResponse();
			
			response=commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, "1=1");
			List<TsUsersDto> tmplist=new ArrayList<TsUsersDto>();
			for (Iterator iterator = response.getData().iterator(); iterator.hasNext();) {
				TsUsersDto tmpdto = (TsUsersDto) iterator.next();
				String mission="";
				if (null!=dto.getSstamppermission()) {
					//获取当前用户权限
					String[] stamppermission = dto.getSstamppermission().split(",");
					List permissionlist = Arrays.asList(stamppermission);
					for(int i = 0; i < reportlist.size(); i++){
						TdEnumvalueDto valuedto = reportlist.get(i);
						if(permissionlist.contains(valuedto.getSvalue())){
//							checklist.add(valuedto);
							mission+=valuedto.getSvaluecmt()+",";
						}
					}
					}
				dto.setSstamppermission(mission);
				tmplist.add(tmpdto);
			}
			response.setData(tmplist);
			return response;
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		
		return super.retrieve(pageRequest);
	}

	private void init() {
		checklist = new ArrayList();
		reportlist = new ArrayList();
		// 取中心的核算主体代码
		TsOrganDto orgdto = new TsOrganDto();
		orgdto.setSorglevel(StateConstant.ORG_CENTER_LEVEL);
		orgdto.setSorgstatus(StateConstant.ORG_STATE);
		try {
			list = commonDataAccessService.findRsByDto(orgdto);
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
		TsOrganDto orgdto2 = new TsOrganDto();

		orgdto2.setSorgcode(loginfo.getSorgcode());
		orgdto2.setSorgstatus(StateConstant.ORG_STATE);

		try {
			list2 = commonDataAccessService.findRsByDto(orgdto2,
					" OR S_GOVERNORGCODE = '" + loginfo.getSorgcode() + "'");
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
			return;
		}

		dto.setSorgcode(loginfo.getSorgcode());

		try {
			// 签章名称
			TsStamppositionDto stampdto = new TsStamppositionDto();
			Map map = new HashMap();
			stampdto.setSorgcode(loginfo.getSorgcode());
			List stampPositionList = commonDataAccessService.findRsByDtoUR(stampdto);
			for(int i=0; i < stampPositionList.size(); i++){
				TsStamppositionDto tempdto = (TsStamppositionDto) stampPositionList.get(i);
				TdEnumvalueDto enumvaluedto = new TdEnumvalueDto();
				if(!map.containsKey(tempdto.getSstamptype())){
					map.put(tempdto.getSstamptype(), tempdto.getSstampname());
					enumvaluedto.setSvalue(tempdto.getSstamptype());
					enumvaluedto.setSvaluecmt(tempdto.getSstampname());
					reportlist.add(enumvaluedto);
				}
				
			}
			List<TsUsersDto> tmplist=new ArrayList<TsUsersDto>();
			userList=new ArrayList<TsUsersDto>();
			tmplist = commonDataAccessService.findRsByDto(dto,
					" OR S_orgcode IN( select s_orgcode from ts_organ  where  S_GOVERNORGCODE  = '"
							+ loginfo.getSorgcode() + "')");
			
			for (Iterator iterator = tmplist.iterator(); iterator
					.hasNext();) {
				TsUsersDto tmpdto = (TsUsersDto) iterator.next();
				String mission="";
				if (null!=tmpdto.getSstamppermission()) {
					//获取当前用户权限
					String[] stamppermission = tmpdto.getSstamppermission().split(",");
					List permissionlist = Arrays.asList(stamppermission);
					for(int i = 0; i < reportlist.size(); i++){
						TdEnumvalueDto valuedto = reportlist.get(i);
						if(permissionlist.contains(valuedto.getSvalue())){
//							checklist.add(valuedto);
							mission+=valuedto.getSvaluecmt()+",";
						}
					}
					}

				tmpdto.setSstamppermission(mission);
				userList.add(tmpdto);
			}
			
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		
		// PageRequest pageRequest = new PageRequest();
		// PageResponse pageResponse = retrieve(pageRequest);
		// pagingcontext.setPage(pageResponse);
	}

	private boolean datacheck() {
		if (null == sorgcode || "".equals(sorgcode)) {
			MessageDialog.openMessageDialog(null, "用户所属机构不能为空！");
			return false;
		}
		if (null == dto.getSusercode() || "".equals(dto.getSusercode())) {
			MessageDialog.openMessageDialog(null, "用户代码不能为空！");
			return false;
		}
		if (null == dto.getSusername() || "".equals(dto.getSusername())) {
			MessageDialog.openMessageDialog(null, "用户姓名不能为空！");
			return false;
		}
		if (null == susertype || "".equals(susertype)) {
			MessageDialog.openMessageDialog(null, "用户类型不能为空！");
			return false;
		}
		if (null == suserstatus || "".equals(suserstatus.trim())) {
			MessageDialog.openMessageDialog(null, "用户状态不能空！");
			return false;
		}
		if (null!=dto.getSpassmodcycle()&&dto.getSpassmodcycle().toString().length()>2){
			MessageDialog.openMessageDialog(null, "口令有效期应小于100");
			dto.setSpassmodcycle(30);
			this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TEXT_EVENT);
			return false;
		}
		if(dto.getSloginstatus()==null)
			dto.setSloginstatus("");
		return true;

	}
	
	/**
	 * Direction: 密码重置
	 * ename: repeatPwd
	 * 引用方法: 
	 * viewers: * messages: 
	 */
    public String repeatPwd(Object o){
    	if (null == dto || dto.getSusercode() == null) {		
			MessageDialog.openMessageDialog(null, "请选择要重置密码的用户");
			return "";
		}
    	org.eclipse.jface.dialogs.MessageDialog msg = new org.eclipse.jface.dialogs.MessageDialog(
				null, "密码重置提示", null, "是否确认要将用户["+dto.getSusercode()+"]的密码重置",
				org.eclipse.jface.dialogs.MessageDialog.QUESTION, new String[] {
						"确认", "取消" }, 0);
    	if (msg.open() == org.eclipse.jface.dialogs.MessageDialog.OK) {
    		try {
    			String pwd = new Md5App().makeMd5(StateConstant.User_Default_PWD);
    			dto.setSpassword(pwd);
        		dto.setSpassmoddate(TimeFacade.getCurrentStringTime());
        		dto.setImodicount(0);//设置为0，系统重新登录时会提示修改密码
    			commonDataAccessService.updateData(dto);
    			MessageDialog.openMessageDialog(null, "用户["+dto.getSusercode()+"]的密码已被重置为"+"'"+StateConstant.User_Default_PWD+"'");
    			dto = null;
    		} catch (ITFEBizException e) {
    			log.error(e);
    			MessageDialog.openErrorDialog(null, e);
    			return "";
    		}
    	}else {
    		return "";
    	}    	
        return super.repeatPwd(o);
    }
	
	public List<TsOrganDto> getList() {
		return list;
	}

	public void setList(List<TsOrganDto> list) {
		this.list = list;
	}

	public List<TsUsersDto> getUserList() {
		return userList;
	}

	public void setUserList(List<TsUsersDto> userList) {
		this.userList = userList;
	}

	public List<TdEnumvalueDto> getChecklist() {
		return checklist;
	}

	public void setChecklist(List<TdEnumvalueDto> checklist) {
		this.checklist = checklist;
	}

	public List<TdEnumvalueDto> getReportlist() {
		return reportlist;
	}

	public void setReportlist(List<TdEnumvalueDto> reportlist) {
		this.reportlist = reportlist;
	}

}
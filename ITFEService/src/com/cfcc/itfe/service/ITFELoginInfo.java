package com.cfcc.itfe.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.jaf.core.interfaces.ILoginInfo;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

/**
 * @author Caoyg
 * @time 09-10-12 16:40:34
 */
public class ITFELoginInfo implements ILoginInfo {

	private static final long serialVersionUID = -8783016655436816333L;

	/**
	 * 系统用户
	 */
	boolean systemUser = false;
	
	/**
	 * 用户使用默认口令登录时,需要修改口令
	 */
	boolean chgPwd = false;
	boolean modifyPwd = false;

	/**
	 * 是否第一次登录 取值为真时第一次登录,为假时不是第一次登录
	 */
	boolean firstLogin = false;

	/**
	 * 是否有效 用户是否到达规定修改密码时间, 为真时已经到达规定的密码修改时间，为假时没有到达！
	 */
	boolean invalidation = false;

	/** 核算主体代码	 */
	private String sorgcode;
	
	/** 机构类型 */
	private String orgKind;

	/** 机构名称 */
	private String sorgName;
	
	private String suserType;
	
	/** 用户代码 */
	private String suserCode;
	
	/** 用户名称 */
	private String suserName;
	
	/** 用户密码 */
	private String spassword;

	/** 当前功能码 */
	private String scurFunc;

	/** 菜单名称 */
	private String smenuName;
	
	/** 签名信息 */
	private String ssign = null;
	
	/** 口令重复次数 */
	private int pwdRepeateCount = 1;

	/** 该用户有权限操作的国库代码列表 */
	private List treasuryCodes;
	
	/** 功能列表，控制前台菜单显示 */
	private List functionList = new ArrayList();
	
	private String currentDate;
	
	private String version;
	
	private String iscollect;
	
	private String tipslogState;
	
	private String sysflag;
	//证书ID
	private String scertId;
	//登录方式
	private char logintype;
	
	public String getScertId() {
		return scertId;
	}

	public void setScertId(String scertId) {
		this.scertId = scertId;
	}
	/**
	 * 密码有效期
	 */
	private int validpassdays =0;
	
	public int getValidpassdays() {
		return validpassdays;
	}
	/**
	 * 业务加密方式
	 */
	private HashMap <String, String> encryptMode;
	/**
	 * 区分地方特色
	 */
	private String area;
	
	/**
	 *密钥设置方式
	 */
	private String MankeyMode;

	public void setValidpassdays(int validpassdays) {
		this.validpassdays = validpassdays;
	}
	
	/**
	 * 公共配置参数
	 */
	private String publicparam;
	
	
	public String getPublicparam() {
		return publicparam;
	}

	public void setPublicparam(String publicparam) {
		this.publicparam = publicparam;
	}
	/**
	 * 登陆方式，0-用户名口令；1-用户名，UK；2-用户名口令，UK
	 */
	public static final char LOGIN_TYPE = '0'; 
	
	public String toString() {
		StringBuffer sb = new StringBuffer("用户信息:");
		if(getPublicparam().contains(",payoutstampmode=true,"))
		{
			if(StateConstant.User_Type_MainBiz.equals(suserType))
				sb.append("角色[管理员] ");
			else if(StateConstant.User_Type_Normal.equals(suserType))
				sb.append("角色[操作员] ");
			else if(StateConstant.User_Type_Admin.equals(suserType))
				sb.append("角色[业务主管] ");
			else if(StateConstant.User_Type_Stat.equals(suserType))
				sb.append("角色[监控员] ");
		}else
		{
			if(StateConstant.User_Type_Admin.equals(suserType))
				sb.append("角色[管理员] ");
			else if(StateConstant.User_Type_Normal.equals(suserType))
				sb.append("角色[操作员] ");
			else if(StateConstant.User_Type_MainBiz.equals(suserType))
				sb.append("角色[业务主管] ");
			else if(StateConstant.User_Type_Stat.equals(suserType))
				sb.append("角色[监控员] ");
		}
		sb.append("用户[").append(suserCode).append("] ");
		sb.append("机构代码[").append(sorgcode).append("] ");
		sb.append("工作日期[").append(currentDate).append("] ");
		sb.append(" TIPS状态[").append(tipslogState).append("] ");
		if (publicparam.contains(",localdbbackup=true,")) {
			sb.append("前置备份状态:[").append("1".equals(scurFunc) ? "成功":"不成功").append("] ");
		}
		if (publicparam.contains(",monvoudbbackup=true,")) {
			sb.append("凭证库备份状态:[").append("1".equals(smenuName) ? "成功":"不成功").append("] ");
		}
		return sb.toString();
	}

	// 获取 签数据
	public String getSignData() {
		StringBuffer sb = new StringBuffer();
		sb.append(sorgcode);
		sb.append(suserCode);
		return sb.toString();
	}

	/**
	 * @return the isChgPwd
	 */
	public Boolean getChgPwd() {
		return chgPwd;
	}

	/**
	 * @return the isChgPwd
	 */
	public Boolean isChgPwd() {
		return chgPwd;
	}

	/**
	 * @param isChgPwd
	 *            the isChgPwd to set
	 */
	public void setChgPwd(Boolean isChgPwd) {
		this.chgPwd = isChgPwd;
	}

	public Boolean getModifyPwd() {
		return modifyPwd;
	}

	public Boolean isModifyPwd() {
		return modifyPwd;
	}

	public void setModifyPwd(Boolean modifyPwd) {
		this.modifyPwd = modifyPwd;
	}

	/**
	 * @return firstLogin
	 */
	public boolean isFirstLogin() {
		return firstLogin;
	}

	/**
	 * @param firstLogin
	 *            要设置的 firstLogin
	 */
	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	/**
	 * @return invalidation
	 */
	public boolean isInvalidation() {
		return invalidation;
	}

	/**
	 * @param invalidation
	 *            要设置的 invalidation
	 */
	public void setInvalidation(boolean invalidation) {
		this.invalidation = invalidation;
	}

	/**
	 * @return systemUser
	 */
	public boolean isSystemUser() {
		return systemUser;
	}

	/**
	 * @param systemUser
	 *            要设置的 systemUser
	 */
	public void setSystemUser(boolean systemUser) {
		this.systemUser = systemUser;
	}

	public String getSorgcode() {
		return sorgcode;
	}

	public void setSorgcode(String sorgcode) {
		this.sorgcode = sorgcode;
	}

	public String getOrgKind() {
		return orgKind;
	}

	public void setOrgKind(String orgKind) {
		this.orgKind = orgKind;
	}

	public int getPwdRepeateCount() {
		return pwdRepeateCount;
	}

	public void setPwdRepeateCount(int pwdRepeateCount) {
		this.pwdRepeateCount = pwdRepeateCount;
	}

	public List getTreasuryCodes() {
		return treasuryCodes;
	}

	public void setTreasuryCodes(List treasuryCodes) {
		this.treasuryCodes = treasuryCodes;
	}

	public List getFunctionList() {
		return functionList;
	}

	public void setFunctionList(List functionList) {
		this.functionList = functionList;
	}

	public void setChgPwd(boolean chgPwd) {
		this.chgPwd = chgPwd;
	}

	public void setModifyPwd(boolean modifyPwd) {
		this.modifyPwd = modifyPwd;
	}

	public String getSorgName() {
		return sorgName;
	}

	public void setSorgName(String sorgName) {
		this.sorgName = sorgName;
	}

	public String getSuserCode() {
		return suserCode;
	}

	public void setSuserCode(String suserCode) {
		this.suserCode = suserCode;
	}

	public String getSuserName() {
		return suserName;
	}

	public void setSuserName(String suserName) {
		this.suserName = suserName;
	}

	public String getSpassword() {
		return spassword;
	}

	public void setSpassword(String spassword) {
		this.spassword = spassword;
	}

	public String getScurFunc() {
		return scurFunc;
	}

	public void setScurFunc(String scurFunc) {
		this.scurFunc = scurFunc;
	}

	public String getSmenuName() {
		return smenuName;
	}

	public void setSmenuName(String smenuName) {
		this.smenuName = smenuName;
	}

	public String getSsign() {
		return ssign;
	}

	public void setSsign(String ssign) {
		this.ssign = ssign;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public HashMap<String, String> getEncryptMode() {
		return encryptMode;
	}

	public void setEncryptMode(HashMap<String, String> encryptMode) {
		this.encryptMode = encryptMode;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getMankeyMode() {
		return MankeyMode;
	}

	public void setMankeyMode(String mankeyMode) {
		MankeyMode = mankeyMode;
	}

	public String getIscollect() {
		return iscollect;
	}

	public void setIscollect(String iscollect) {
		this.iscollect = iscollect;
	}

	public String getTipslogState() {
		return tipslogState;
	}

	public void setTipslogState(String tipslogState) {
		this.tipslogState = tipslogState;
	}

	public String getSysflag() {
		return sysflag;
	}

	public void setSysflag(String sysflag) {
		this.sysflag = sysflag;
	}

	public char getLogintype() {
		return logintype;
	}

	public void setLogintype(char logintype2) {
		this.logintype = logintype2;
	}

	public String getSuserType() {
		return suserType;
	}

	public void setSuserType(String suserType) {
		this.suserType = suserType;
	}
	
	
}
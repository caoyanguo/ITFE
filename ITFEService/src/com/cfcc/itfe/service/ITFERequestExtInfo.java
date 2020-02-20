package com.cfcc.itfe.service;

import com.cfcc.jaf.core.interfaces.IRequestExtInfo;
import com.cfcc.jaf.core.interfaces.ILoginInfo;

/**
 * @author Caoyg
 * @time 09-10-12 16:40:34
 */
public class ITFERequestExtInfo implements IRequestExtInfo {
	public void setLoginInfo(ITFELoginInfo loginInfo) {
		this.loginInfo = loginInfo;
	}

	private static final long serialVersionUID = -5284793971987040125L;
	/**
	 * 判断是否记录日志
	 */
	private boolean isLog;
	private String funcCode;// 当前功能代码
	private String menuName;// 当前菜单名称
	private ITFELoginInfo loginInfo;

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getFuncCode() {
		return funcCode;
	}

	public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}

	public boolean isLog() {
		return isLog;
	}

	public void setLog(boolean isLog) {
		this.isLog = isLog;
	}

	/**
	 * 主管代码
	 */
	private String directorCode;
	/**
	 * 主管密码
	 */
	private String directorPassword;

	private String directorSign;

	/**
	 * 主管授权成功
	 */
	boolean directorAuthorizationSucc = false;

	// 获取 签属内容数据
	public String getDirectorSignData() {
		StringBuffer sb = new StringBuffer();
		sb.append(directorCode);
		return sb.toString();
	}

	/**
	 * 获取 签署后的数据
	 * 
	 * @return
	 */
	public String getDirectorSign() {
		return directorSign;
	}

	public void setDirectorSign(String sign) {
		this.directorSign = sign;
	}

	/**
	 * @return 返回 directorCode。
	 */
	public String getDirectorCode() {
		return directorCode;
	}

	/**
	 * @param directorCode
	 *            要设置的 directorCode。
	 */
	public void setDirectorCode(String p_directorCode) {
		directorCode = p_directorCode;
	}

	/**
	 * @return 返回 directorPassword。
	 */
	public String getDirectorPassword() {
		return directorPassword;
	}

	/**
	 * @param directorPassword
	 *            要设置的 directorPassword。
	 */
	public void setDirectorPassword(String p_directorPassword) {
		directorPassword = p_directorPassword;
	}

	/**
	 * @return the directorAuthorizationSucc
	 */
	public boolean isDirectorAuthorizationSucc() {
		return directorAuthorizationSucc;
	}

	/**
	 * @param directorAuthorizationSucc
	 *            the directorAuthorizationSucc to set
	 */
	public void setDirectorAuthorizationSucc(boolean directorAuthorizationSucc) {
		this.directorAuthorizationSucc = directorAuthorizationSucc;
	}

	public ILoginInfo getLoginInfo() {
		return loginInfo;

		// TODO Auto-generated method stub
		// return null;
	}

	public void setLoginInfo(ILoginInfo arg0) {
		this.loginInfo = (ITFELoginInfo) arg0;
	}
}

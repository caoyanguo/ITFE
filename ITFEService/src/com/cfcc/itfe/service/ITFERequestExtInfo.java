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
	 * �ж��Ƿ��¼��־
	 */
	private boolean isLog;
	private String funcCode;// ��ǰ���ܴ���
	private String menuName;// ��ǰ�˵�����
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
	 * ���ܴ���
	 */
	private String directorCode;
	/**
	 * ��������
	 */
	private String directorPassword;

	private String directorSign;

	/**
	 * ������Ȩ�ɹ�
	 */
	boolean directorAuthorizationSucc = false;

	// ��ȡ ǩ����������
	public String getDirectorSignData() {
		StringBuffer sb = new StringBuffer();
		sb.append(directorCode);
		return sb.toString();
	}

	/**
	 * ��ȡ ǩ��������
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
	 * @return ���� directorCode��
	 */
	public String getDirectorCode() {
		return directorCode;
	}

	/**
	 * @param directorCode
	 *            Ҫ���õ� directorCode��
	 */
	public void setDirectorCode(String p_directorCode) {
		directorCode = p_directorCode;
	}

	/**
	 * @return ���� directorPassword��
	 */
	public String getDirectorPassword() {
		return directorPassword;
	}

	/**
	 * @param directorPassword
	 *            Ҫ���õ� directorPassword��
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

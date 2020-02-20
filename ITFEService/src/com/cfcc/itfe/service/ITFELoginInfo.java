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
	 * ϵͳ�û�
	 */
	boolean systemUser = false;
	
	/**
	 * �û�ʹ��Ĭ�Ͽ����¼ʱ,��Ҫ�޸Ŀ���
	 */
	boolean chgPwd = false;
	boolean modifyPwd = false;

	/**
	 * �Ƿ��һ�ε�¼ ȡֵΪ��ʱ��һ�ε�¼,Ϊ��ʱ���ǵ�һ�ε�¼
	 */
	boolean firstLogin = false;

	/**
	 * �Ƿ���Ч �û��Ƿ񵽴�涨�޸�����ʱ��, Ϊ��ʱ�Ѿ�����涨�������޸�ʱ�䣬Ϊ��ʱû�е��
	 */
	boolean invalidation = false;

	/** �����������	 */
	private String sorgcode;
	
	/** �������� */
	private String orgKind;

	/** �������� */
	private String sorgName;
	
	private String suserType;
	
	/** �û����� */
	private String suserCode;
	
	/** �û����� */
	private String suserName;
	
	/** �û����� */
	private String spassword;

	/** ��ǰ������ */
	private String scurFunc;

	/** �˵����� */
	private String smenuName;
	
	/** ǩ����Ϣ */
	private String ssign = null;
	
	/** �����ظ����� */
	private int pwdRepeateCount = 1;

	/** ���û���Ȩ�޲����Ĺ�������б� */
	private List treasuryCodes;
	
	/** �����б�����ǰ̨�˵���ʾ */
	private List functionList = new ArrayList();
	
	private String currentDate;
	
	private String version;
	
	private String iscollect;
	
	private String tipslogState;
	
	private String sysflag;
	//֤��ID
	private String scertId;
	//��¼��ʽ
	private char logintype;
	
	public String getScertId() {
		return scertId;
	}

	public void setScertId(String scertId) {
		this.scertId = scertId;
	}
	/**
	 * ������Ч��
	 */
	private int validpassdays =0;
	
	public int getValidpassdays() {
		return validpassdays;
	}
	/**
	 * ҵ����ܷ�ʽ
	 */
	private HashMap <String, String> encryptMode;
	/**
	 * ���ֵط���ɫ
	 */
	private String area;
	
	/**
	 *��Կ���÷�ʽ
	 */
	private String MankeyMode;

	public void setValidpassdays(int validpassdays) {
		this.validpassdays = validpassdays;
	}
	
	/**
	 * �������ò���
	 */
	private String publicparam;
	
	
	public String getPublicparam() {
		return publicparam;
	}

	public void setPublicparam(String publicparam) {
		this.publicparam = publicparam;
	}
	/**
	 * ��½��ʽ��0-�û������1-�û�����UK��2-�û������UK
	 */
	public static final char LOGIN_TYPE = '0'; 
	
	public String toString() {
		StringBuffer sb = new StringBuffer("�û���Ϣ:");
		if(getPublicparam().contains(",payoutstampmode=true,"))
		{
			if(StateConstant.User_Type_MainBiz.equals(suserType))
				sb.append("��ɫ[����Ա] ");
			else if(StateConstant.User_Type_Normal.equals(suserType))
				sb.append("��ɫ[����Ա] ");
			else if(StateConstant.User_Type_Admin.equals(suserType))
				sb.append("��ɫ[ҵ������] ");
			else if(StateConstant.User_Type_Stat.equals(suserType))
				sb.append("��ɫ[���Ա] ");
		}else
		{
			if(StateConstant.User_Type_Admin.equals(suserType))
				sb.append("��ɫ[����Ա] ");
			else if(StateConstant.User_Type_Normal.equals(suserType))
				sb.append("��ɫ[����Ա] ");
			else if(StateConstant.User_Type_MainBiz.equals(suserType))
				sb.append("��ɫ[ҵ������] ");
			else if(StateConstant.User_Type_Stat.equals(suserType))
				sb.append("��ɫ[���Ա] ");
		}
		sb.append("�û�[").append(suserCode).append("] ");
		sb.append("��������[").append(sorgcode).append("] ");
		sb.append("��������[").append(currentDate).append("] ");
		sb.append(" TIPS״̬[").append(tipslogState).append("] ");
		if (publicparam.contains(",localdbbackup=true,")) {
			sb.append("ǰ�ñ���״̬:[").append("1".equals(scurFunc) ? "�ɹ�":"���ɹ�").append("] ");
		}
		if (publicparam.contains(",monvoudbbackup=true,")) {
			sb.append("ƾ֤�ⱸ��״̬:[").append("1".equals(smenuName) ? "�ɹ�":"���ɹ�").append("] ");
		}
		return sb.toString();
	}

	// ��ȡ ǩ����
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
	 *            Ҫ���õ� firstLogin
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
	 *            Ҫ���õ� invalidation
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
	 *            Ҫ���õ� systemUser
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
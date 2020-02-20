package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class ContentDto implements IDto {
	
	/**
	 * 报文内容格式
	
	/**
     * 登陆密码 
     */
    private String _logPass;

    /**
     * 登陆新密码
     */
    private String _logNewPass;
    
    /**
     * 报文编号
     */
    private String _Msgno;
    /**
     * 原包报文编号
     */
    private String _OriPackMsgNo;

    /**
     * 原机构代码
     */
    private String _OriSendOrgCode;
    
    /**
     * 原报流水号
     */
    private String _OriPackNo;
    /**
     * 原委托日期
     */
    private String _OriEntrustDate;

	public String get_OriPackMsgNo() {
		return _OriPackMsgNo;
	}

	public void set_OriPackMsgNo(String oriPackMsgNo) {
		_OriPackMsgNo = oriPackMsgNo;
	}

	public String get_OriSendOrgCode() {
		return _OriSendOrgCode;
	}

	public void set_OriSendOrgCode(String oriSendOrgCode) {
		_OriSendOrgCode = oriSendOrgCode;
	}

	public String get_OriPackNo() {
		return _OriPackNo;
	}

	public void set_OriPackNo(String oriPackNo) {
		_OriPackNo = oriPackNo;
	}

	public String get_OriEntrustDate() {
		return _OriEntrustDate;
	}

	public void set_OriEntrustDate(String oriEntrustDate) {
		_OriEntrustDate = oriEntrustDate;
	}

	public String get_Msgno() {
		return _Msgno;
	}

	public void set_Msgno(String msgno) {
		_Msgno = msgno;
	}

	public String get_logPass() {
		return _logPass;
	}

	public void set_logPass(String pass) {
		_logPass = pass;
	}

	public String get_logNewPass() {
		return _logNewPass;
	}

	public void set_logNewPass(String newPass) {
		_logNewPass = newPass;
	}

	public String checkValid() {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkValid(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String checkValidExcept(String[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDto[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPK getPK() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isParent() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setChildren(IDto[] arg0) {
		// TODO Auto-generated method stub
		
	}

  

}

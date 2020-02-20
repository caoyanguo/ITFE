package com.cfcc.itfe.persistence.dto;

import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jaform.parent.IPK;

public class FileProcInfoDto implements IDto {
	
	/**
     * 处理信息
     */
    private String  msginfo;

    /**
     * 返回值
     */
    private String StrRet;
    

	public String getMsginfo() {
		return msginfo;
	}

	public void setMsginfo(String msginfo) {
		this.msginfo = msginfo;
	}

	

	public String getStrRet() {
		return StrRet;
	}

	public void setStrRet(String strRet) {
		StrRet = strRet;
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

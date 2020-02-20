package com.cfcc.itfe.service;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import com.cfcc.jaf.core.interfaces.support.AbstractService;
import com.cfcc.jaf.core.interfaces.ILoginInfo;
import com.cfcc.jaf.core.interfaces.IRequestExtInfo;
import com.cfcc.jaf.core.interfaces.IResponseExtInfo;
import com.cfcc.jaf.core.interfaces.support.AbstractService;
import com.cfcc.jaf.core.invoker.aop.advice.AuthorizationException;

/**
 * @author Caoyg
 * @time 09-10-12 16:40:34
 * @codecomponent 
 */

public abstract class AbstractITFEService extends AbstractService {

	/**
	 * ����ع�
	 */
	public void setRollbackOnly() {

		super.setRollBack(true);
		if (this.getResponseExtInfo() instanceof ITFEResponseExtInfo) {
		    // TODO Auto-generated method stub
		}
	}

	/**
	 * ȡ���û���Ϣ
	 * 
	 * @return LoginInfo
	 */
	public ITFELoginInfo getLoginInfo() {
		ILoginInfo loginInfo = super.getLoginInfo();
		if (loginInfo == null) {
			throw new AuthorizationException("�û���¼��ϢΪ�գ�");
		}
		if (loginInfo instanceof ITFELoginInfo) {
			ITFELoginInfo info = (ITFELoginInfo) loginInfo;
			return info;
		} else {
			throw new AuthorizationException("�û���¼��Ϣ���� LoginInfo,���ǣ�"
					+ loginInfo.getClass().getName());
		}
	}

	/**
	 * 
	 * @returnResponseExtInfo
	 */
	public ITFEResponseExtInfo getResponseExtInfo() {
		IResponseExtInfo responseExtInfo = super.getResponseExtInfo();
		if (responseExtInfo instanceof ITFEResponseExtInfo) {
			ITFEResponseExtInfo info = (ITFEResponseExtInfo) responseExtInfo;
			return info;
		} else {
			throw new AuthorizationException("�û���Ӧ��Ϣ���� ResponseExtInfo,���ǣ�"
					+ responseExtInfo.getClass().getName());
		}
	}

	public ITFERequestExtInfo getRequestExtInfo() {
		IRequestExtInfo requestExtInfo = super.getRequestExtInfo();
		if (requestExtInfo instanceof ITFERequestExtInfo) {
			ITFERequestExtInfo info = (ITFERequestExtInfo) requestExtInfo;
			return info;
		} else {
			throw new AuthorizationException("�û�������Ϣ���� RequestExtInfo,���ǣ�"
					+ requestExtInfo.getClass().getName());
		}
	}

	/**
	 * ����Service��LoginInfo
	 * 
	 * @param info
	 */
	public void setLoginInfo(ITFELoginInfo info) {
		super.setLoginInfo(info);
	}
}
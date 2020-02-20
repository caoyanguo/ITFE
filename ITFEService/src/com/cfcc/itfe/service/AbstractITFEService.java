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
	 * 事物回滚
	 */
	public void setRollbackOnly() {

		super.setRollBack(true);
		if (this.getResponseExtInfo() instanceof ITFEResponseExtInfo) {
		    // TODO Auto-generated method stub
		}
	}

	/**
	 * 取得用户信息
	 * 
	 * @return LoginInfo
	 */
	public ITFELoginInfo getLoginInfo() {
		ILoginInfo loginInfo = super.getLoginInfo();
		if (loginInfo == null) {
			throw new AuthorizationException("用户登录信息为空！");
		}
		if (loginInfo instanceof ITFELoginInfo) {
			ITFELoginInfo info = (ITFELoginInfo) loginInfo;
			return info;
		} else {
			throw new AuthorizationException("用户登录信息不是 LoginInfo,而是："
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
			throw new AuthorizationException("用户响应信息不是 ResponseExtInfo,而是："
					+ responseExtInfo.getClass().getName());
		}
	}

	public ITFERequestExtInfo getRequestExtInfo() {
		IRequestExtInfo requestExtInfo = super.getRequestExtInfo();
		if (requestExtInfo instanceof ITFERequestExtInfo) {
			ITFERequestExtInfo info = (ITFERequestExtInfo) requestExtInfo;
			return info;
		} else {
			throw new AuthorizationException("用户请求信息不是 RequestExtInfo,而是："
					+ requestExtInfo.getClass().getName());
		}
	}

	/**
	 * 设置Service的LoginInfo
	 * 
	 * @param info
	 */
	public void setLoginInfo(ITFELoginInfo info) {
		super.setLoginInfo(info);
	}
}
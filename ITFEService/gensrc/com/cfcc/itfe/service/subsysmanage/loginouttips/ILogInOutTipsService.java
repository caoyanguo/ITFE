package com.cfcc.itfe.service.subsysmanage.loginouttips;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.ContentDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:42
 * @generated
 * codecomment: 
 */
public interface ILogInOutTipsService extends IService {



	/**
	 * 登陆报文发送	 
	 * @generated
	 * @param dto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String sendLoginMsg(ContentDto dto) throws ITFEBizException;

	/**
	 * 签退报文发送	 
	 * @generated
	 * @param dto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String sendLogOutMsg(ContentDto dto) throws ITFEBizException;

	/**
	 * 查询测试结果	 
	 * @generated
	 * @param msgno
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String queryLoginResult(String msgno) throws ITFEBizException;

}
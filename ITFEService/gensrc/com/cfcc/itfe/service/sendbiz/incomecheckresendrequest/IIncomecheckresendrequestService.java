package com.cfcc.itfe.service.sendbiz.incomecheckresendrequest;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;
import com.cfcc.jaf.core.interfaces.IService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;

/**
 * @author Administrator
 * @time   19-12-08 13:00:37
 * @generated
 * codecomment: 
 */
public interface IIncomecheckresendrequestService extends IService {



	/**
	 * ���ͱ���
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
   public abstract void sendMsg(TvSendlogDto dto) throws ITFEBizException;

}
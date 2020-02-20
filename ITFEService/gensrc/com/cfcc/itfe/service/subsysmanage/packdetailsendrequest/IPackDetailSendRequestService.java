package com.cfcc.itfe.service.subsysmanage.packdetailsendrequest;

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
public interface IPackDetailSendRequestService extends IService {



	/**
	 * µÇÂ½±¨ÎÄ·¢ËÍ	 
	 * @generated
	 * @param dto
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
   public abstract String sendRequestMsg(ContentDto dto) throws ITFEBizException;

}
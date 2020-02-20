package com.cfcc.itfe.service.commonsubsys.commondbaccess;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.AbstractReFreshCacheInfoService;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
/**
 * @author db2admin
 * @time   12-04-06 20:22:00
 * codecomment: 
 */

public class ReFreshCacheInfoService extends AbstractReFreshCacheInfoService {
	private static Log log = LogFactory.getLog(ReFreshCacheInfoService.class);	


	/**
	 * reloadBuffer
	 	 
	 * @generated
	 * @param sbookorgcode
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String reloadBuffer(String sbookorgcode) throws ITFEBizException {
    	try {
			SrvCacheFacade.reloadBuffer(sbookorgcode);
			return "同步参数成功！";
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("同步参数失败！");
		}
    	
   
    }

}
package com.cfcc.itfe.service.para.tsconnection;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import com.cfcc.itfe.service.para.tsconnection.AbstractTsConnectionService;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author caoyg
 * @time   09-10-20 08:42:01
 * codecomment: 
 */

public class TsConnectionService extends AbstractTsConnectionService {
	private static Log log = LogFactory.getLog(TsConnectionService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
			DatabaseFacade.getDb().create(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段连通机构甲方，连通机构乙方已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
		return null;
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
			CommonFacade.getODB().deleteRsByDto(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		DatabaseFacade.getDb().update(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段连通机构甲方，连通机构乙方已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
	}
    
}
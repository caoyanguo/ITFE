package com.cfcc.itfe.service.para.tsdrawbackreason;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import org.springframework.orm.jpa.vendor.Database;

import com.cfcc.itfe.persistence.dto.TsDrawbackreasonDto;
import com.cfcc.itfe.service.para.tsdrawbackreason.AbstractTsDrawbackreasonService;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author yuanjiong
 * @time   12-02-20 15:43:51
 * codecomment: 
 */

public class TsDrawbackreasonService extends AbstractTsDrawbackreasonService {
	private static Log log = LogFactory.getLog(TsDrawbackreasonService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		TsDrawbackreasonDto dto = (TsDrawbackreasonDto)dtoInfo;
    		DatabaseFacade.getDb().create(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段核算主体代码，横联退库原因代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException("保存记录出错:"+e.getMessage(),e);
		}
		return null;
    }

	/**
	 * 删除	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		DatabaseFacade.getDb().delete(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		} 
    }

	/**
	 * 修改	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		TsDrawbackreasonDto dto = (TsDrawbackreasonDto)dtoInfo;
    		DatabaseFacade.getDb().update(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段核算主体代码，横联退库原因代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException("修改记录出错："+e.getMessage(), e);
		} 
    }

}
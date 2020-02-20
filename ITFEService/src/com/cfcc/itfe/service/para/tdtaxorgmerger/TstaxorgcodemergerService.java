package com.cfcc.itfe.service.para.tdtaxorgmerger;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TdTaxorgMergerDto;
import com.cfcc.itfe.service.para.tdtaxorgmerger.AbstractTstaxorgcodemergerService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 * @author Yuan
 * @time   13-03-25 15:33:03
 * codecomment: 
 */

public class TstaxorgcodemergerService extends AbstractTstaxorgcodemergerService {
	private static Log log = LogFactory.getLog(TstaxorgcodemergerService.class);

	public void add(TdTaxorgMergerDto dto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().create(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段合并前征收机关代码，合并后征收机关代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(e.getMessage());
		}
	}

	public void delete(TdTaxorgMergerDto dto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().delete(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e.getMessage());
		}
	}

	public void modify(TdTaxorgMergerDto dto, String precode, String aftercode) throws ITFEBizException {
		try {
			SQLExecutor sqlExecutor=DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			String sql="UPDATE TD_TAXORG_MERGER SET S_PRETAXORGCODE = ?	,S_AFTERTAXORGCODE = ?,S_TAXORGNAME = ?,S_BIZTYPE= ?  WHERE S_BOOKORGCODE = ? AND S_PRETAXORGCODE = ? AND S_AFTERTAXORGCODE = ?";
			sqlExecutor.addParam(dto.getSpretaxorgcode());
			sqlExecutor.addParam(dto.getSaftertaxorgcode());
			sqlExecutor.addParam(dto.getStaxorgname());
			sqlExecutor.addParam(dto.getSbiztype());
			sqlExecutor.addParam(dto.getSbookorgcode());
			sqlExecutor.addParam(precode);
			sqlExecutor.addParam(aftercode);
			sqlExecutor.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段合并前征收机关代码，合并后征收机关代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(e.getMessage());
		}
	}


}
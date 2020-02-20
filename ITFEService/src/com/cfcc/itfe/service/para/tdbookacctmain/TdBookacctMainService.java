package com.cfcc.itfe.service.para.tdbookacctmain;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;
import com.cfcc.itfe.service.para.tdbookacctmain.AbstractTdBookacctMainService;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TdBookacctMainDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author ZZD
 * @time   13-03-04 16:53:28
 * codecomment: 
 */

public class TdBookacctMainService extends AbstractTdBookacctMainService {
	private static Log log = LogFactory.getLog(TdBookacctMainService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void addInfo(TdBookacctMainDto dtoInfo) throws ITFEBizException {
    	try {
    		TdBookacctMainDto dto = (TdBookacctMainDto)dtoInfo;
    		dto.setSbookorgcode(getLoginInfo().getSorgcode());
			DatabaseFacade.getDb().create(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段核算主体代码，会计账号已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
    }

	/**
	 * 删除	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(TdBookacctMainDto dtoInfo) throws ITFEBizException {
    	try {
    		dtoInfo.setSbookorgcode(getLoginInfo().getSorgcode());
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
	 * 修改	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(TdBookacctMainDto dtoInfo) throws ITFEBizException {
    	try {
    		TdBookacctMainDto dto = (TdBookacctMainDto)dtoInfo;
    		dto.setSbookorgcode(getLoginInfo().getSorgcode());
			DatabaseFacade.getDb().update(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("字段核算主体代码，会计账号已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
    }

}
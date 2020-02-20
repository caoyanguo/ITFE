package com.cfcc.itfe.service.para.tsbankcodeconverttype;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.service.para.tsbankcodeconverttype.AbstractTsConvertbanktypeService;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author tyler
 * @time   13-10-16 17:35:08
 * codecomment: 
 */

public class TsConvertbanktypeService extends AbstractTsConvertbanktypeService {
	private static Log log = LogFactory.getLog(TsConvertbanktypeService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		TsConvertbanktypeDto cbt = (TsConvertbanktypeDto) dtoInfo;
    		String sbankcodeStr = cbt.getSbankcode();
    		HashMap<String, TsPaybankDto> payBankInfos = SrvCacheFacade.cachePayBankInfo();
    		if(!payBankInfos.containsKey(sbankcodeStr))
    		{
    			throw new RuntimeException("代理银行代码必须在支付行号范围内");
    		}else
    		{
    			DatabaseFacade.getDb().create(dtoInfo);
    		}
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段机构代码，国库代码，代理银行代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(e.getMessage(), e);
		}
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
    		TsConvertbanktypeDto cbt = (TsConvertbanktypeDto) dtoInfo;
    		String sbankcodeStr = cbt.getSbankcode();
    		HashMap<String, TsPaybankDto> payBankInfos = SrvCacheFacade.cachePayBankInfo();
    		if(!payBankInfos.containsKey(sbankcodeStr))
    		{
    			throw new RuntimeException("代理银行代码必须在支付行号范围内");
    		}else
    		{
    			DatabaseFacade.getDb().update(dtoInfo);
    		}
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段机构代码，国库代码，代理银行代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}
    }

}
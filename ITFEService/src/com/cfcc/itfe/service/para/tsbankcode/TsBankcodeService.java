package com.cfcc.itfe.service.para.tsbankcode;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsBankcodeDto;
import com.cfcc.itfe.service.para.tsbankcode.AbstractTsBankcodeService;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author lushaoqing
 * @time   10-09-26 10:35:23
 * codecomment: 
 */

public class TsBankcodeService extends AbstractTsBankcodeService {
	private static Log log = LogFactory.getLog(TsBankcodeService.class);	


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
				throw new ITFEBizException("字段银行账号代码，银行账号状态已存在，不能重复录入！", e);
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
    		DatabaseFacade.getDb().delete(dtoInfo);
		} catch (JAFDatabaseException e) {
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
				throw new ITFEBizException("字段银行账号代码，银行账号状态已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
    }
    
	/**
	 * 分页查询银行账号信息	 
	 * @generated
	 * @param findDto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
   public PageResponse findBanknoByPage(TsBankcodeDto findDto, PageRequest pageRequest) throws ITFEBizException
   {
		// 取得银行名称 - 支持模糊查询
   		try
   		{
   			String banknameStr = null;
   			String bankname = findDto.getSbnkname();
   			String wherestr = null;
   			if(bankname != null && (bankname.trim() != ""))
   			{
   				banknameStr = " (S_BNKNAME like '%" + bankname + "%' or S_BNKNAME like '%" + bankname.toLowerCase() + "%') ";
   				wherestr = banknameStr;
   				findDto.setSbnkname(null);
   			}
		
			if (null == wherestr) {
				return CommonFacade.getODB().findRsByDtoPaging(findDto, pageRequest, null, " S_BNKCODE ");
			} else {
				return CommonFacade.getODB().findRsByDtoPaging(findDto, pageRequest, wherestr, " S_BNKCODE ");
			}
		} catch (JAFDatabaseException e) {
			log.error("分页查询税票收入信息时错误!", e);
			throw new ITFEBizException("分页查询税票收入信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询税票收入信息时错误!", e);
			throw new ITFEBizException("分页查询税票收入信息时错误!", e);
		}
	}

}
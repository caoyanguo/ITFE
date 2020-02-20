package com.cfcc.itfe.service.para.tsgenbankandreckbank;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.pk.TsGenbankandreckbankPK;
import com.cfcc.itfe.service.para.tsgenbankandreckbank.AbstractTsGenbankandreckbankService;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author t60
 * @time   12-02-21 11:24:39
 * codecomment: 
 */

public class TsGenbankandreckbankService extends AbstractTsGenbankandreckbankService {
	private static Log log = LogFactory.getLog(TsGenbankandreckbankService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		TsGenbankandreckbankDto dto = (TsGenbankandreckbankDto)dtoInfo;
    		DatabaseFacade df=DatabaseFacade.getDb();
    		DatabaseFacade.getDb().find(TsGenbankandreckbankDto.class);
    		DatabaseFacade.getDb().create(dto);
    		TsConvertbanktypeDto banktype = new TsConvertbanktypeDto();
    		banktype.setSorgcode(dto.getSbookorgcode());
    		banktype.setStrecode(dto.getStrecode());
    		banktype.setSbankcode(dto.getSreckbankcode());
    		banktype.setSbankname(dto.getSreckbankname());
    		banktype.setSbanktype(dto.getSgenbankcode().substring(0, 3));
    		DatabaseFacade.getDb().create(banktype);
		} catch (JAFDatabaseException e) {
			log.error(e);
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
    		TsGenbankandreckbankDto dto = (TsGenbankandreckbankDto)dtoInfo;
    		TsConvertbanktypeDto banktype = new TsConvertbanktypeDto();
    		banktype.setSorgcode(dto.getSbookorgcode());
    		banktype.setStrecode(dto.getStrecode());
    		banktype.setSbankcode(dto.getSreckbankcode());
    		DatabaseFacade.getDb().delete(banktype);
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
    		TsGenbankandreckbankDto dto = (TsGenbankandreckbankDto)dtoInfo;
    		TsGenbankandreckbankPK dpk = new TsGenbankandreckbankPK();
    		dpk.setIseqno(dto.getIseqno());
    		TsGenbankandreckbankDto oridto = (TsGenbankandreckbankDto) DatabaseFacade.getDb().find(dpk);
    		TsConvertbanktypeDto banktype = new TsConvertbanktypeDto();
    		banktype.setSorgcode(oridto.getSbookorgcode());
    		banktype.setStrecode(oridto.getStrecode());
    		banktype.setSbankcode(dto.getSreckbankcode());
    		banktype.setSbankname(dto.getSreckbankname());
    		banktype.setSbanktype(dto.getSgenbankcode().substring(0, 3));
    		DatabaseFacade.getDb().update(dto);
    		DatabaseFacade.getDb().update(banktype);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("修改记录出错："+e.getMessage(), e);
		} 
    }

}
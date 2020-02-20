package com.cfcc.itfe.service.para.billautosend;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsBillautosendDto;
import com.cfcc.itfe.service.para.billautosend.AbstractBillAutoSendService;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author Administrator
 * @time   14-08-06 14:44:29
 * codecomment: 
 */

public class BillAutoSendService extends AbstractBillAutoSendService {
	private static Log log = LogFactory.getLog(BillAutoSendService.class);	


	/**
	 * ����	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
    		TsBillautosendDto dto = (TsBillautosendDto)dtoInfo;
			DatabaseFacade.getDb().create(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
		if (e.getSqlState().equals("23505")) { 
			throw new ITFEBizException("�ֶκ���������룬������룬�����ظ�¼�룡", e);
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
    		TsBillautosendDto dto = (TsBillautosendDto)dtoInfo;
   		DatabaseFacade.getDb().update(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("�ֶκ���������룬������룬�ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
    }

}
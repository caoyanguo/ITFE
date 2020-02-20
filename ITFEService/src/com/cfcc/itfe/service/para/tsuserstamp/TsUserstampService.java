package com.cfcc.itfe.service.para.tsuserstamp;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsUserstampDto;
import com.cfcc.itfe.service.para.tsuserstamp.AbstractTsUserstampService;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author caoyg
 * @time   09-10-20 08:42:02
 * codecomment: 
 */

public class TsUserstampService extends AbstractTsUserstampService {
	private static Log log = LogFactory.getLog(TsUserstampService.class);	


	/**
	 * ����	 
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
			throw new ITFEBizException("�ֶ���������������Ա���ƣ�ǩ�������Ѵ��ڣ������ظ�¼�룡", e);
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
    		//���ݿ��������
			TsUserstampDto user = (TsUserstampDto)dtoInfo;
			String strWhere = "where s_orgcode='" + user.getSorgcode() + "' and s_usercode='" + user.getSusercode()
				+ "' and s_stamptypecode='" + user.getSstamptypecode() + "'";
    		//����ɾ���û��ĸ���Ȩ��
			String sql = "delete from ts_userstampfunction " + strWhere;
			DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql);
			//ɾ���û������ǩ�µĶ�Ӧ��ϵ
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
				throw new ITFEBizException("�ֶ���������������Ա���ƣ�ǩ�������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
    }

}
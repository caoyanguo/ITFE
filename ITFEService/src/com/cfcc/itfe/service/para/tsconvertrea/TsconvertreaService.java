package com.cfcc.itfe.service.para.tsconvertrea;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsConvertreaDto;
import com.cfcc.itfe.service.para.tsconvertrea.AbstractTsconvertreaService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author Administrator
 * @time 13-04-16 10:36:55 codecomment:
 */

public class TsconvertreaService extends AbstractTsconvertreaService {
	private static Log log = LogFactory.getLog(TsconvertreaService.class);

	/**
	 * �����Ϣ
	 * 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException
	 */
	public void addInfo(IDto dto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().create(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("�ֶκ�����������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException("¼������Ӧ��ϵʧ�ܣ�", e);
		}
	}

	/**
	 * ɾ����Ϣ
	 * 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException
	 */
	public void delInfo(IDto dto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().delete(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("ɾ�������Ӧ��ϵʧ�ܣ�", e);
		}
	}

	/**
	 * �޸���Ϣ
	 * 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException
	 */
	public void modInfo(TsConvertreaDto updateDto,TsConvertreaDto oriDto) throws ITFEBizException {
		try {
			String sql = "UPDATE TS_CONVERTREA SET S_ORGCODE = ?,S_TRECODE = ? ,S_TCBSTREA = ?  WHERE S_ORGCODE = ? AND S_TRECODE =  ? ";
			SQLExecutor sqlexec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlexec.addParam(getLoginInfo().getSorgcode());
			sqlexec.addParam(updateDto.getStrecode());
			sqlexec.addParam(updateDto.getStcbstrea());
			sqlexec.addParam(getLoginInfo().getSorgcode());
			sqlexec.addParam(oriDto.getStrecode());
			sqlexec.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("�ֶκ�����������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException("�޸Ĺ����Ӧ��ϵʧ�ܣ�", e);
		}
	}

}
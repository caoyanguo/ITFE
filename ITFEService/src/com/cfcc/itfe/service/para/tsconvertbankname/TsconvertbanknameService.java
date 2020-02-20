package com.cfcc.itfe.service.para.tsconvertbankname;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.service.para.tsconvertbankname.AbstractTsconvertbanknameService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author King
 * @time 13-05-24 10:10:53 codecomment:
 */

public class TsconvertbanknameService extends AbstractTsconvertbanknameService {
	private static Log log = LogFactory.getLog(TsconvertbanknameService.class);

	/**
	 * ����
	 * 
	 * @generated
	 * @param idto
	 * @throws ITFEBizException
	 */
	public void addInfo(IDto idto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().create(idto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("�ֶκ���������룬�������������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException("¼�������������ղ���ʧ�ܣ�", e);
		}
	}

	/**
	 * ɾ����Ϣ
	 * 
	 * @generated
	 * @param idto
	 * @throws ITFEBizException
	 */
	public void delInfo(IDto idto) throws ITFEBizException {
		try {
			DatabaseFacade.getODB().delete(idto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("ɾ�������������ղ���ʧ�ܣ�", e);
		}
	}

	/**
	 * ��Ϣ�޸�
	 * 
	 * @generated
	 * @param idto
	 * @throws ITFEBizException
	 */
	public void modInfo(TsConvertbanknameDto olddto, TsConvertbanknameDto newdto)
			throws ITFEBizException {
		String sql = "UPDATE TS_CONVERTBANKNAME SET S_ORGCODE = ? ,S_BANKNAME = ? ,S_TCBANKNAME = ? ,S_BANKCODE = ?  WHERE S_ORGCODE = ? AND  S_BANKNAME = ?";
		try {
			SQLExecutor sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.addParam(newdto.getSorgcode());
			sqlExecutor.addParam(newdto.getSbankname());
			sqlExecutor.addParam(newdto.getStcbankname());
			sqlExecutor.addParam(newdto.getSbankcode());
			sqlExecutor.addParam(olddto.getSorgcode());
			sqlExecutor.addParam(olddto.getSbankname());
			sqlExecutor.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("�ֶκ���������룬�������������Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException("�޸������������ղ���ʧ�ܣ�", e);
		}
	}

}
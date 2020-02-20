package com.cfcc.itfe.service.para.tsconvertax;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsConvertaxDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author Administrator
 * @time   13-04-16 14:55:14
 * codecomment: 
 */

public class TsconvertaxService extends AbstractTsconvertaxService {
	private static Log log = LogFactory.getLog(TsconvertaxService.class);	


	/**
	 * �����Ϣ
	 	 
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
				throw new ITFEBizException("�ֶκ������ջ��ش����Ѵ��ڣ������ظ�¼�룡", e);
			}
			throw new ITFEBizException("¼���Ӧ��ϵʧ�ܣ�", e);
		}
    }

	/**
	 * �޸���Ϣ
	 	 
	 * @generated
	 * @param nowdto
	 * @param oridto
	 * @throws ITFEBizException	 
	 */
    public void modInfo(TsConvertaxDto nowdto, TsConvertaxDto oridto) throws ITFEBizException {
      String sql = "UPDATE TS_CONVERTAX SET S_ORGCODE = ? ,S_TAXCODE = ? ,S_TCBSTAX = ?  WHERE S_ORGCODE = ? AND S_TAXCODE = ?";
      try {
		SQLExecutor sqlexec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
		sqlexec.addParam(getLoginInfo().getSorgcode());
		sqlexec.addParam(nowdto.getStaxcode());
		sqlexec.addParam(nowdto.getStcbstax());
		sqlexec.addParam(getLoginInfo().getSorgcode());
		sqlexec.addParam(oridto.getStaxcode());
		sqlexec.runQueryCloseCon(sql);
	} catch (JAFDatabaseException e) {
		log.error(e);
		if (e.getSqlState().equals("23505")) { 
			throw new ITFEBizException("�ֶκ������ջ��ش����Ѵ��ڣ������ظ�¼�룡", e);
		}
		throw new ITFEBizException("�޸Ķ�Ӧ��ϵʧ�ܣ�", e);
	}
    }

	/**
	 * ɾ��dto��Ϣ
	 	 
	 * @generated
	 * @param dto
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dto) throws ITFEBizException {
    	try {
			DatabaseFacade.getODB().delete(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("ɾ����Ӧ��ϵʧ�ܣ�", e);
		}
    }

}
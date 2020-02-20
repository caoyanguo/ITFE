package com.cfcc.itfe.service.commonsubsys.commondbaccess;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.persistence.dto.TdEnumvalueDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * @author caoyg
 * @time   09-11-09 20:12:52
 * codecomment: 
 */

public class ItfeCacheService extends AbstractItfeCacheService {
	private static Log log = LogFactory.getLog(ItfeCacheService.class);	


	/**
	 * ����ö��ֵ��Ϣ	 
	 * @generated
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List cacheEnumValue() throws ITFEBizException {
      return null;
    }

	/**
	 * ���ݴ����ŷ���ö��ֵ��Ϣ	 
	 * @generated
	 * @param typecode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List cacheEnumValueByCode(String typecode) throws ITFEBizException {
    	List<TdEnumvalueDto> list = null;
		try {
			list = TSystemFacade.findEnumValuebyTypeCode(typecode);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯö��ֵ����", e);
		}
		return list;
    	
    }

	public String cacheGetCenterOrg() throws ITFEBizException {
		try {
			return  TSystemFacade.findCenterOrg();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ���ĺ�������������", e);
		}
		
	}

	public List cacheGetValueByDto(IDto idto) throws ITFEBizException {
		 try {
			return CommonFacade.getODB().findRsByDto(idto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}
	}

	public List cacheEnumValueBySql(String sql) throws ITFEBizException {
		// TODO Auto-generated method stub
		SQLExecutor sqlExecutor = null;
		try {
			sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			SQLResults sqlResults = sqlExecutor.runQuery(sql, TdEnumvalueDto.class);
			if(null == sqlResults || null == sqlResults.getDtoCollection() || sqlResults.getDtoCollection().size() == 0){
				return null;
			}
			return (List) sqlResults.getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����", e);
		}finally{
			if(null != sqlExecutor){
				sqlExecutor.closeConnection();
			}
		}
		
	}

}
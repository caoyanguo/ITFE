package com.cfcc.itfe.service.para.tstaxpayacct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author db2admin
 * @time   14-06-18 15:19:53
 * codecomment: 
 */

public class TsTaxPayacctService extends AbstractTsTaxPayacctService {
	private static Log log = LogFactory.getLog(TsTaxPayacctService.class);	


	/**
	 * 录入
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
    public Boolean addInfo(IDto dto) throws ITFEBizException {
    	boolean flag=false;
    	try {
			DatabaseFacade.getDb().create(dto);
			flag=true;
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("付款人账户已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
      return flag;
    }

	/**
	 * 修改
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
    public Boolean modifyInfo(IDto dto) throws ITFEBizException {
    	boolean flag=false;
    	try {
			DatabaseFacade.getDb().update(dto);
			flag=true;
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("付款人账户已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}
      return flag;
    }

	/**
	 * 删除
	 	 
	 * @generated
	 * @param dto
	 * @return java.lang.Boolean
	 * @throws ITFEBizException	 
	 */
    public Boolean delete(IDto dto) throws ITFEBizException {
    	boolean flag=false;
    	try {
			DatabaseFacade.getODB().delete(dto);
			flag=true;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
      return flag;
    }

}
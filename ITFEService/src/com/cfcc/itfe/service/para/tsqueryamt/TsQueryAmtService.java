package com.cfcc.itfe.service.para.tsqueryamt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author caoyg
 * @time   15-03-26 16:46:52
 * codecomment: 
 */

public class TsQueryAmtService extends AbstractTsQueryAmtService {
	private static Log log = LogFactory.getLog(TsQueryAmtService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param addinfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto addinfo) throws ITFEBizException {
    	try {
			DatabaseFacade.getDb().create(addinfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException("【国库主体代码+业务类型+金额区间标识】对应记录 已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
		return null;
    }

	/**
	 * 删除	 
	 * @generated
	 * @param delInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto delInfo) throws ITFEBizException {
    	try {
    		DatabaseFacade.getDb().delete(delInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}  
    }

	/**
	 * 修改	 
	 * @generated
	 * @param modifyInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto modifyInfo(IDto modifyInfo) throws ITFEBizException {
    	try {
			DatabaseFacade.getDb().update(modifyInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("【国库主体代码+业务类型+金额区间标识】对应记录 已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
      return null;
    }

}
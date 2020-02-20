package com.cfcc.itfe.service.para.tpsharedivide;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TpShareDivideDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author ZZD
 * @time   13-03-04 11:19:36
 * codecomment: 
 */

public class TpShareDivideService extends AbstractTpShareDivideService {
	private static Log log = LogFactory.getLog(TpShareDivideService.class);	


	/**
	 * Ôö¼Ó	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void addInfo(TpShareDivideDto dtoInfo) throws ITFEBizException {
    	try {
    		TpShareDivideDto dto = (TpShareDivideDto)dtoInfo;
			DatabaseFacade.getDb().create(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException(StateConstant.PRIMAYKEY, e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
    }

	/**
	 * É¾³ý	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(TpShareDivideDto dtoInfo) throws ITFEBizException {
    	try {
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
	 * ÐÞ¸Ä	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(TpShareDivideDto dtoInfo) throws ITFEBizException {
    	try {
    		TpShareDivideDto dto = (TpShareDivideDto)dtoInfo;
			DatabaseFacade.getDb().update(dto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException(StateConstant.PRIMAYKEY, e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} 
    }

}
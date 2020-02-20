package com.cfcc.itfe.service.para.tsbudgetsubjectforquery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author zgz
 * @time 12-05-18 16:32:15 codecomment:
 */

public class TsBudgetsubjectForQueryService extends
		AbstractTsBudgetsubjectForQueryService {
	private static Log log = LogFactory
			.getLog(TsBudgetsubjectForQueryService.class);

	public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			DatabaseFacade.getDb().create(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段核算主体代码，科目代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		}
		return null;
	}

	public void delInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			DatabaseFacade.getDb().delete(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}

	}

	public void modInfo(IDto dtoInfo) throws ITFEBizException {
		try {
			DatabaseFacade.getDb().update(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException("字段核算主体代码，科目代码已存在，不能重复录入！", e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}

	}

}
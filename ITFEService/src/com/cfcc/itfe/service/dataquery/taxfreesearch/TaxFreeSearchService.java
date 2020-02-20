package com.cfcc.itfe.service.dataquery.taxfreesearch;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
/**
 * @author db2admin
 * @time   13-02-21 11:27:13
 * codecomment: 
 */

public class TaxFreeSearchService extends AbstractTaxFreeSearchService {
	private static Log log = LogFactory.getLog(TaxFreeSearchService.class);

	public void updateFail(List dtoInfos) throws ITFEBizException {
		if(ITFECommonConstant.IFNEWINTERFACE.equals("1")){
    		throw new ITFEBizException("新版TIPS接口自动更新此状态，不可以手动更新！");
    		
    	}
		try {
			DatabaseFacade.getDb().update(CommonUtil.listTArray(dtoInfos));
		} catch (JAFDatabaseException e1) {
			log.error(e1);
			throw new ITFEBizException("批量更新数据失败", e1);
		}
		
	}

	public void updateSuccess(List dtoInfos) throws ITFEBizException {
		if(ITFECommonConstant.IFNEWINTERFACE.equals("1")){
    		throw new ITFEBizException("新版TIPS接口自动更新此状态，不可以手动更新！");
    	}
		try {
			DatabaseFacade.getDb().update(CommonUtil.listTArray(dtoInfos));
		} catch (JAFDatabaseException e1) {
			log.error(e1);
			throw new ITFEBizException("批量更新数据失败", e1);
		}
		
	}	


}
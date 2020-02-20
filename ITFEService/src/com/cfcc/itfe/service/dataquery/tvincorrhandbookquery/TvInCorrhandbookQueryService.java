package com.cfcc.itfe.service.dataquery.tvincorrhandbookquery;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.service.dataquery.tvincorrhandbookquery.AbstractTvInCorrhandbookQueryService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
/**
 * @author t60
 * @time   12-02-22 16:26:14
 * codecomment: 
 */

public class TvInCorrhandbookQueryService extends AbstractTvInCorrhandbookQueryService {
	private static Log log = LogFactory.getLog(TvInCorrhandbookQueryService.class);

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
package com.cfcc.itfe.service.para.tssyspara;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsSysparaDto;
import com.cfcc.itfe.service.para.tssyspara.AbstractTsSysParasService;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * @author t60
 * @time   12-03-02 09:11:15
 * codecomment: 
 */

public class TsSysParasService extends AbstractTsSysParasService {
	private static Log log = LogFactory.getLog(TsSysParasService.class);	


	/**
	 * ÐÞ¸Ä	 
	 * @generated
	 * @param dtoList
	 * @throws ITFEBizException	 
	 */
    public void modify(List dtoList) throws ITFEBizException {
    	try {
    		 SQLExecutor sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
			.getSQLExecutor();
    		String sql = "delete from ts_syspara where S_ORGCODE = ?";
    		sqlExec.addParam(getLoginInfo().getSorgcode());
    		sqlExec.runQueryCloseCon(sql);
    	    DatabaseFacade.getODB().create(CommonUtil.listTArray(dtoList));
    		
			
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException(StateConstant.PRIMAYKEY, e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		}
    }

}
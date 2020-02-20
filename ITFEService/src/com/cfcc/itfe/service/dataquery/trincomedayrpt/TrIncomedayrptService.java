package com.cfcc.itfe.service.dataquery.trincomedayrpt;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.service.dataquery.trincomedayrpt.AbstractTrIncomedayrptService;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;


/**
 * @author t60
 * @time   12-02-24 10:44:46
 * codecomment: 
 */

public class TrIncomedayrptService extends AbstractTrIncomedayrptService {
	private static Log log = LogFactory.getLog(TrIncomedayrptService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
      return null;
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dtoInfo) throws ITFEBizException {
      
    }

	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto dtoInfo) throws ITFEBizException {
      
    }

	public List exportQuery(IDto dtoInfo, String wheresql)
			throws ITFEBizException {
		TrIncomedayrptDto tempdto = (TrIncomedayrptDto) dtoInfo;
		try {
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String sql = "SELECT "
		        + "S_FINORGCODE as sfinorgcode, S_TAXORGCODE as staxorgcode, S_TRECODE as strecode, S_RPTDATE as srptdate, S_BUDGETTYPE as sbudgettype, "
		        + "S_BUDGETLEVELCODE as sbudgetlevelcode, S_BUDGETSUBCODE as sbudgetsubcode, S_BUDGETSUBNAME as sbudgetsubname, N_MONEYDAY as nmoneyday, "
		        + "N_MONEYMONTH as nmoneymonth, N_MONEYYEAR as nmoneyyear, S_BELONGFLAG as sbelongflag, S_TRIMFLAG as strimflag, "
		        + "S_DIVIDEGROUP as sdividegroup, S_BILLKIND as sbillkind "
		        + "FROM TR_INCOMEDAYRPT "
		        +" WHERE " 
		        ;
			if (null != tempdto.getStrecode()
					&& tempdto.getStrecode().trim().length() > 0) {
				sql = sql + " S_TRECODE=? ";
				exec.addParam(tempdto.getStrecode());
			}else{
				sql = sql + wheresql;
			}
			
			if (null != tempdto.getSfinorgcode()
					&& tempdto.getSfinorgcode().trim().length() > 0) {
				sql = sql + " AND S_FINORGCODE =?";
				exec.addParam(tempdto.getSfinorgcode());
			}
			if (null != tempdto.getStaxorgcode()
					&& tempdto.getStaxorgcode().trim().length() > 0) {
				sql = sql + " AND S_TAXORGCODE=? ";
				exec.addParam(tempdto.getStaxorgcode());
			}

			if (null != tempdto.getSrptdate()
					&& tempdto.getSrptdate().trim().length() > 0) {
				sql = sql + " AND S_RPTDATE=? ";
				exec.addParam(tempdto.getSrptdate());
			}
			if (null != tempdto.getSbudgettype()
					&& tempdto.getSbudgettype().trim().length() > 0) {
				sql = sql + " AND S_BUDGETTYPE=? ";
				exec.addParam(tempdto.getSbudgettype());
			}
			if (null != tempdto.getSbudgetlevelcode()
					&& tempdto.getSbudgetlevelcode().trim().length() > 0) {
				sql = sql + " AND S_BUDGETLEVELCODE=? ";
				exec.addParam(tempdto.getSbudgetlevelcode());
			}
			if (null != tempdto.getSbelongflag()
					&& tempdto.getSbelongflag().trim().length() > 0) {
				sql = sql + " AND S_BELONGFLAG=? ";
				exec.addParam(tempdto.getSbelongflag());
			}
			if (null != tempdto.getStrimflag()
					&& tempdto.getStrimflag().trim().length() > 0) {
				sql = sql + " AND S_TRIMFLAG=? ";
				exec.addParam(tempdto.getStrimflag());
			}
			if (null != tempdto.getSbillkind()
					&& tempdto.getSbillkind().trim().length() > 0) {
				sql = sql + " AND S_BILLKIND=? ";
				exec.addParam(tempdto.getSbillkind());
			}
			sql = sql + " order by strecode,staxorgcode";
			SQLResults res = exec.runQueryCloseCon(sql, TrIncomedayrptDto.class);
			List list = new ArrayList();
			list.addAll(res.getDtoCollection());
			return list;
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询核算主体代码出错", e);
		}
	}


}
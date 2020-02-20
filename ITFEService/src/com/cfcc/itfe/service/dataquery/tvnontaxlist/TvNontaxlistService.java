package com.cfcc.itfe.service.dataquery.tvnontaxlist;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.NontaxDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author Administrator
 * @time   18-04-27 15:05:00
 * codecomment: 
 */

public class TvNontaxlistService extends AbstractTvNontaxlistService {
	private static Log log = LogFactory.getLog(TvNontaxlistService.class);	

	public List findInfo(NontaxDto idto) throws ITFEBizException {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT a.S_TRECODE AS strecode, a.S_TAXORGCODE AS sfinorgcode,a.S_PAYBANKNO AS spaybankno," +
				"b.S_HOLD1 AS shold1,b.S_HOLD4 AS shold4,b.S_HOLD2 AS shold2,b.S_ITEMCODE AS sitemcode," +
				"b.S_XADDWORD AS sxaddword,b.S_VICESIGN AS svicesign,b.N_DETAILAMT AS ntraamt ");
		sb.append("FROM TV_NONTAXMAIN a,TV_NONTAXSUB b,tv_voucherinfo c  ");
		sb.append("WHERE c.S_TRECODE = ?");
		sb.append(" AND c.S_CONFIRUSERCODE >= ? AND c.S_CONFIRUSERCODE <= ? and c.S_STATUS=?");
		sb.append(" AND c.S_DEALNO = a.S_DEALNO AND a.S_DEALNO = b.S_DEALNO ");
		if(isNotNull(idto.getSfinorgcode())){
			sb.append(" AND a.S_TAXORGCODE =?");
		}
		if(isNotNull(idto.getSpaybankno())){
			sb.append(" AND a.S_PAYBANKNO = ?");
		}
		if(isNotNull(idto.getShold1())){
			sb.append(" AND b.S_HOLD1 = ?");
		}
		if(isNotNull(idto.getSitemcode())){
			sb.append(" AND b.S_ITEMCODE = ?");
		}
		if(idto.getNtraamt() != null){
			sb.append(" AND b.N_TRAAMT = ?");
		}
		sb.append(" ORDER BY a.S_TRECODE,a.S_FINORGCODE,a.S_PAYBANKNO");
		
		SQLExecutor sqlExec = null;
		SQLResults rs = null;
		try {
			sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
//			rs = sqlExec.runQueryCloseCon(sb.toString());
			sqlExec.addParam(idto.getStrecode());
			sqlExec.addParam(idto.getSstartdate());
			sqlExec.addParam(idto.getSenddate());
			sqlExec.addParam("80");
			if(isNotNull(idto.getSfinorgcode())){
				sqlExec.addParam(idto.getSfinorgcode());
			}
			if(isNotNull(idto.getSpaybankno())){
				sqlExec.addParam(idto.getSpaybankno());
			}
			if(isNotNull(idto.getShold1())){
				sqlExec.addParam(idto.getShold1());
			}
			if(isNotNull(idto.getSitemcode())){
				sqlExec.addParam(idto.getSitemcode());
			}
			if(idto.getNtraamt() != null){
				sqlExec.addParam(idto.getNtraamt());
			}
			sqlExec.setMaxRows(1000000);
			rs = sqlExec.runQueryCloseCon(sb.toString(), idto.getClass(), true);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(e);
		}
		return (List)rs.getDtoCollection();
	}
	
	public boolean isNotNull(String svar){
		if(svar != null && !svar.trim().equals("")){
			return true;
		}
		return false;
	}

}
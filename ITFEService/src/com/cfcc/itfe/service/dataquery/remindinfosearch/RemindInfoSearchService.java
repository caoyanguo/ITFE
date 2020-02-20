package com.cfcc.itfe.service.dataquery.remindinfosearch;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.service.dataquery.remindinfosearch.AbstractRemindInfoSearchService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author Administrator
 * @time 15-03-26 15:14:56 codecomment:
 */

public class RemindInfoSearchService extends AbstractRemindInfoSearchService {
	private static Log log = LogFactory.getLog(RemindInfoSearchService.class);

	/**
	 * 提醒信息查询
	 * 
	 * @generated
	 * @param params
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List searchInfo(List params) throws ITFEBizException {
		try {
			SQLExecutor sqlExecutore = DatabaseFacade.getODB()
					.getSqlExecutorFactory().getSQLExecutor();
			SQLResults sqlResults = sqlExecutore.runQueryCloseCon(getSQL(
					(String) params.get(0), (String) params.get(1),
					(String) params.get(2), (String) params.get(3),
					(String) params.get(4)));
			return parseSqlResult(sqlResults);
		} catch (JAFDatabaseException e) {
			log.error("查询提醒信息失败！", e);
			throw new ITFEBizException("查询提醒信息失败！", e);
		}
	}

	/**
	 * 组装sql语句
	 * @param treCode
	 * @param createDate
	 * @param remindIs
	 * @param voucherSource
	 * @param vtcode
	 * @return
	 */
	private String getSQL(String treCode, String createDate, String remindIs,
			String voucherSource, String vtcode) {

		StringBuffer sql = new StringBuffer();
		sql
				.append("SELECT  S_ORGCODE,S_TRECODE,S_ADMDIVCODE,S_STYEAR,S_VTCODE,S_VOUCHERNO,S_ATTACH,S_RETURNERRMSG,S_CREATDATE,N_MONEY,S_STATUS,S_DEMO,S_PAYBANKCODE,S_CONFIRUSERCODE,");
		sql.append(" CASE ");
		sql
				.append(" WHEN (S_VTCODE = '5951' AND S_STATUS != '15') THEN '1' ");	//单位清册不为15签收成功，进入预警
		sql
				.append(" WHEN (S_VTCODE = '8207' AND (S_STATUS != '100') ) THEN '1' "); //8207状态不为100，没有读取回单成功的业务都没有做完，所以预警
		sql.append(" WHEN ((S_VTCODE = '2301' OR S_VTCODE = '5106' OR S_VTCODE = '5108' OR S_VTCODE = '5201' OR S_VTCODE = '5207' OR S_VTCODE = '5209' OR S_VTCODE = '5253' OR S_VTCODE = '5351' OR S_VTCODE = '2252') AND  (S_STATUS IN ('10','15','16','17','30','45','62','72','90')) ) THEN '1'  ");
		sql
				.append(" WHEN (S_VTCODE = '2203' AND S_STATUS != '80' ) THEN '1'  ");
		sql
				.append("   WHEN (S_VTCODE = '2302'  AND ((S_STATUS IN ('10','15','16','17','30','45','62','72','90')) OR ((days(to_DATE(S_CREATDATE,'YYYYMMDD')) - days(to_DATE(S_HOLD4,'YYYYMMDD')) >= 2))) ) THEN '1'  ");
		sql.append(" ELSE '0' END AS REMIND FROM TV_VOUCHERINFO WHERE S_PAYBANKCODE IS NOT NULL  ");
		StringBuffer whereSql = new StringBuffer();
		whereSql.append(" AND S_ORGCODE = '" + getLoginInfo().getSorgcode() + "' ");
		whereSql.append(" AND S_TRECODE = '" + treCode + "' ");
		whereSql.append(" AND S_CREATDATE = '" + createDate + "' ");
		
		//凭证来源
		if (StringUtils.isNotBlank(voucherSource)) {
			if ("001".equals(voucherSource)) {
				whereSql
				.append(" AND S_VTCODE IN ('2252','5106','5108','5201','5207','5209','5253','5351','8207') ");
			}else{
				whereSql.append(" AND S_PAYBANKCODE ='" + voucherSource + "' ");
			}
		}else{
			whereSql
			.append(" AND S_VTCODE IN ('2252','2301','2302','5108','5106','5201','5207','5209','5253','5351','8207','5951','2203') ");
		}
		//凭证编号
		if(StringUtils.isNotBlank(vtcode)){
			whereSql.append(" AND S_VTCODE ='" + vtcode + "' ");
		}

		if (StringUtils.isNotBlank(remindIs)) {
			return "SELECT * FROM (" + sql.toString() + whereSql.toString()
					+ ") WHERE REMIND = '" + remindIs + "'";
		} else {
			return sql.toString() + whereSql.toString();
		}

	}
	
	private List parseSqlResult(SQLResults sqlResults){
		if(null == sqlResults || sqlResults.getRowCount() == 0 || "NULL".equals(sqlResults)){
			return null;
		}
		List result = new ArrayList();
		TvVoucherinfoDto tmpVoucherInfo ;
		for(int i = 0 ; i < sqlResults.getRowCount() ; i ++){
			tmpVoucherInfo = new TvVoucherinfoDto();
			tmpVoucherInfo.setSorgcode(sqlResults.getString(i, "S_ORGCODE"));
			tmpVoucherInfo.setStrecode(sqlResults.getString(i, "S_TRECODE"));
			tmpVoucherInfo.setSadmdivcode(sqlResults.getString(i, "S_ADMDIVCODE"));
			tmpVoucherInfo.setSstyear(sqlResults.getString(i, "S_STYEAR"));
			tmpVoucherInfo.setSvtcode(sqlResults.getString(i, "S_VTCODE"));
			tmpVoucherInfo.setSvoucherno(sqlResults.getString(i, "S_VOUCHERNO"));
			tmpVoucherInfo.setSattach(sqlResults.getString(i, "S_ATTACH"));
			tmpVoucherInfo.setSreturnerrmsg(sqlResults.getString(i, "S_RETURNERRMSG"));
			tmpVoucherInfo.setScreatdate(sqlResults.getString(i, "S_CREATDATE"));
			tmpVoucherInfo.setNmoney(sqlResults.getBigDecimal(i, "N_MONEY"));
			tmpVoucherInfo.setSstatus(sqlResults.getString(i, "S_STATUS"));
			tmpVoucherInfo.setSdemo(sqlResults.getString(i, "S_DEMO"));
			tmpVoucherInfo.setSpaybankcode(sqlResults.getString(i, "S_PAYBANKCODE"));
			tmpVoucherInfo.setSconfirusercode(sqlResults.getString(i, "S_CONFIRUSERCODE"));
			tmpVoucherInfo.setShold1(sqlResults.getString(i, "REMIND"));
			result.add(tmpVoucherInfo);
		}
		return result;
	}

}
package com.cfcc.itfe.service.dataquery.budgetsubcodemonthlyreport;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.*;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.service.dataquery.budgetsubcodemonthlyreport.AbstractBudgetsubcodemonthlyreportService;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.BudgetsubcodemonthDto;
import com.cfcc.itfe.persistence.dto.ShanghaiReport;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author Administrator
 * @time 13-09-04 10:35:27 codecomment:
 */

public class BudgetsubcodemonthlyreportService extends
		AbstractBudgetsubcodemonthlyreportService {
	private static Log log = LogFactory
			.getLog(BudgetsubcodemonthlyreportService.class);

	/**
	 * 获取报表内容
	 * 
	 * @generated
	 * @param searchdto
	 * @param reporttype
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List getReportData(ShanghaiReport searchdto, String reporttype) throws ITFEBizException {
    	//reporttype
    	String moneyunit = "";
    	if(StateConstant.MONEY_UNIT_3.equals(searchdto.getMoneyunit())){
    		moneyunit = "100000000";
    	}else if(StateConstant.MONEY_UNIT_2.equals(searchdto.getMoneyunit())){
    		moneyunit = "10000";
    	}else if(StateConstant.MONEY_UNIT_1.equals(searchdto.getMoneyunit())){
    		moneyunit = "1";
    	}
    	StringBuffer sql  = new StringBuffer();
    	sql.append("SELECT yearcode,S_BUDGETSUBCODE , S_BUDGETSUBNAME,max(CASE WHEN monthcode = '01' THEN N_MONEYMONTH ELSE 0 END)/" + moneyunit + "  yiamt,max(CASE WHEN monthcode = '02' THEN N_MONEYMONTH ELSE 0 END)/" + moneyunit + "  eramt,max(CASE WHEN monthcode = '03' THEN N_MONEYMONTH ELSE 0 END)/" + moneyunit + "   sanamt,max(CASE WHEN monthcode = '04' THEN N_MONEYMONTH ELSE 0 END)/" + moneyunit + "    siamt,max(CASE WHEN monthcode = '05' THEN N_MONEYMONTH ELSE 0 END)/" + moneyunit + "    wuamt,max(CASE WHEN monthcode = '06' THEN N_MONEYMONTH ELSE 0 END)/" + moneyunit + "    liuamt,max(CASE WHEN monthcode = '07' THEN N_MONEYMONTH ELSE 0 END)/" + moneyunit + "    qiamt,max(CASE WHEN monthcode = '08' THEN N_MONEYMONTH ELSE 0 END)/" + moneyunit + "    baamt,max(CASE WHEN monthcode = '09' THEN N_MONEYMONTH ELSE 0 END)/" + moneyunit + "    jiuamt,max(CASE WHEN monthcode = '10' THEN N_MONEYMONTH ELSE 0 END)/" + moneyunit + "    shiamt,max(CASE WHEN monthcode = '11' THEN N_MONEYMONTH ELSE 0 END)/" + moneyunit + "    shiyiamt,max(CASE WHEN monthcode = '12' THEN N_MONEYMONTH ELSE 0 END)/" + moneyunit + "    shieramt  FROM( ");
//    	sql.append("SELECT yearcode,S_BUDGETSUBCODE , S_BUDGETSUBNAME,max(CASE WHEN monthcode = '01' THEN N_MONEYMONTH ELSE 0 END)  yiamt,max(CASE WHEN monthcode = '02' THEN N_MONEYMONTH ELSE 0 END)  eramt,max(CASE WHEN monthcode = '03' THEN N_MONEYMONTH ELSE 0 END)   sanamt,max(CASE WHEN monthcode = '04' THEN N_MONEYMONTH ELSE 0 END)    siamt,max(CASE WHEN monthcode = '05' THEN N_MONEYMONTH ELSE 0 END)    wuamt,max(CASE WHEN monthcode = '06' THEN N_MONEYMONTH ELSE 0 END)    liuamt,max(CASE WHEN monthcode = '07' THEN N_MONEYMONTH ELSE 0 END)    qiamt,max(CASE WHEN monthcode = '08' THEN N_MONEYMONTH ELSE 0 END)    baamt,max(CASE WHEN monthcode = '09' THEN N_MONEYMONTH ELSE 0 END)    jiuamt,max(CASE WHEN monthcode = '10' THEN N_MONEYMONTH ELSE 0 END)    shiamt,max(CASE WHEN monthcode = '11' THEN N_MONEYMONTH ELSE 0 END)    shiyiamt,max(CASE WHEN monthcode = '12' THEN N_MONEYMONTH ELSE 0 END)    shieramt  FROM( ");
    	sql.append("SELECT LEFT(S_RPTDATE,4) yearcode,S_BUDGETSUBCODE,S_BUDGETSUBNAME, substr(S_RPTDATE,5,2) monthcode,N_MONEYMONTH FROM (");
    	
    	StringBuffer where = new StringBuffer();
    	where.append(" where 1=1 AND length(S_BUDGETSUBNAME) > 1 ");
    	List param = new ArrayList();
    	//国库
    	if(StringUtils.isNotBlank(searchdto.getTrecode())){
    		where.append(" AND S_TRECODE = ? ");
    		param.add(searchdto.getTrecode());
    	}
    	//预算科目
    	if(StringUtils.isNotBlank(searchdto.getSubcode())){
    		where.append(" AND (S_BUDGETSUBCODE in (" + searchdto.getSubcode() + ")) ");
    	}
    	//预算级次
    	if(StringUtils.isNotBlank(searchdto.getSlevelcode())){
    		if(StateConstant.SUBJECTLEVEL_CENTER.equals(searchdto.getSlevelcode()) || StateConstant.SUBJECTLEVEL_AREA.equals(searchdto.getSlevelcode()) || StateConstant.SUBJECTLEVEL_CITY.equals(searchdto.getSlevelcode())){
    			where.append(" AND S_BUDGETLEVELCODE = ? ");
    			param.add(searchdto.getSbelongflag());
    			//地方级 = 区级+市级
    		}else if(StateConstant.SUBJECTLEVEL_LOCAL.equals(searchdto.getSlevelcode())){
    			where.append(" AND (S_BUDGETLEVELCODE = ? OR S_BUDGETLEVELCODE = ?) ");
    			param.add(StateConstant.SUBJECTLEVEL_AREA);
    			param.add(StateConstant.SUBJECTLEVEL_CITY);
    		}
    	}
    	
    	if("TR_INCOMEDAYRPT".equals(reporttype)){
    		//调整期
				where.append(" AND S_TRIMFLAG = ? ");
				param.add(searchdto.getStrimflag());
			// 辖属标志
				where.append(" AND S_BELONGFLAG = ? ");
				param.add(searchdto.getSbelongflag());
    	}
    	where.append(" AND (S_RPTDATE BETWEEN ? AND ?) ");
    	param.add(DateUtil.date2String2(searchdto.getStartdate()));
    	param.add(DateUtil.date2String2(searchdto.getEnddate()));
    	if("TR_INCOMEDAYRPT".equals(reporttype)){
    		where.append(" AND (S_BILLKIND = ? OR S_BILLKIND = ? ) ");
    		param.add(StateConstant.REPORTTYPE_FLAG_NRBUDGETBILL);
    		param.add(StateConstant.REPORTTYPE_FLAG_TRBUDGETBILL);
    	}
    	param.addAll(param);
    	sql.append("SELECT * FROM " + reporttype + " " + where.toString() + " UNION ALL " + "SELECT * FROM H" + reporttype + " " + where.toString());
    	sql.append("))emp  GROUP BY yearcode,S_BUDGETSUBCODE , S_BUDGETSUBNAME");
    	try {
			SQLExecutor sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.setMaxRows(150000);
			sqlExecutor.addParam(param);
			return (List) sqlExecutor.runQueryCloseCon(sql.toString(), BudgetsubcodemonthDto.class).getDtoCollection();
    	} catch (JAFDatabaseException e) {
			log.error("获取报表数据失败！",e);
			throw new ITFEBizException("获取报表数据失败！",e);
		}
    }
}
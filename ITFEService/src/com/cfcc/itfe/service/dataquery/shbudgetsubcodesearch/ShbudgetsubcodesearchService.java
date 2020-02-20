package com.cfcc.itfe.service.dataquery.shbudgetsubcodesearch;

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
import com.cfcc.itfe.service.dataquery.shbudgetsubcodesearch.AbstractShbudgetsubcodesearchService;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.ShanghaiReport;
import com.cfcc.itfe.persistence.dto.ShanghaiReport2;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author Administrator
 * @time   13-09-06 10:00:14
 * codecomment: 
 */

public class ShbudgetsubcodesearchService extends AbstractShbudgetsubcodesearchService {
	private static Log log = LogFactory.getLog(ShbudgetsubcodesearchService.class);	


	/**
	 * 获取报表数据
	 	 
	 * @generated
	 * @param searchdto
	 * @param bizkind
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List getreportdata(ShanghaiReport searchdto, String bizkind) throws ITFEBizException {
    	String moneyunit = "";
    	if(StateConstant.MONEY_UNIT_3.equals(searchdto.getMoneyunit())){
    		moneyunit = "100000000";
    	}else if(StateConstant.MONEY_UNIT_2.equals(searchdto.getMoneyunit())){
    		moneyunit = "10000";
    	}else if(StateConstant.MONEY_UNIT_1.equals(searchdto.getMoneyunit())){
    		moneyunit = "1";
    	}
    	StringBuffer sql = new StringBuffer();
    	sql.append("SELECT C.S_BUDGETSUBCODE,C.S_BUDGETSUBNAME,D.S_RPTDATE,d.N_MONEYDAY/" + moneyunit +" N_MONEYDAY FROM (");
    	sql.append("SELECT S_SUBJECTCODE   S_BUDGETSUBCODE,S_SUBJECTNAME S_BUDGETSUBNAME FROM TS_BUDGETSUBJECT WHERE S_ORGCODE =  ? AND S_SUBJECTCODE IN (" + searchdto.getSubcode() + ")) C LEFT JOIN ( ");
    	StringBuffer where = new StringBuffer();
    	where.append(" where 1=1 ");
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
    	if("TR_INCOMEDAYRPT".equals(bizkind)){
    		//调整期
				where.append(" AND S_TRIMFLAG = ? ");
				param.add(searchdto.getStrimflag());
			// 辖属标志
				where.append(" AND S_BELONGFLAG = ? ");
				param.add(searchdto.getSbelongflag());
    	}
    	//处理报表日期
    	where.append(" AND S_RPTDATE " + genrptdatewhere(searchdto));
    	param.addAll(param);
    	sql.append(" SELECT S_BUDGETSUBCODE,S_BUDGETSUBNAME,S_RPTDATE,sum(N_MONEYDAY) N_MONEYDAY FROM  " + bizkind + where.toString() + " GROUP BY S_BUDGETSUBCODE,S_BUDGETSUBNAME,S_RPTDATE UNION ALL  ");
    	sql.append(" SELECT S_BUDGETSUBCODE,S_BUDGETSUBNAME,S_RPTDATE,sum(N_MONEYDAY) N_MONEYDAY FROM  H" + bizkind + where.toString() + " GROUP BY S_BUDGETSUBCODE,S_BUDGETSUBNAME,S_RPTDATE " );
    	sql.append(") D ON C.S_BUDGETSUBCODE = D.S_BUDGETSUBCODE WHERE D.S_RPTDATE IS NOT NULL ORDER BY d.S_RPTDATE ASC");
    	try {
			SQLExecutor sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.setMaxRows(150000);
			sqlExecutor.addParam(getLoginInfo().getSorgcode());
			sqlExecutor.addParam(param);
			return (List) sqlExecutor.runQueryCloseCon(sql.toString(), ShanghaiReport2.class).getDtoCollection();
		} catch (JAFDatabaseException e) {
			log.error("查询报表日报！",e);
			throw new ITFEBizException("查询报表日报！",e);
		}
    }
    
    private String genrptdatewhere(ShanghaiReport searchdto){
    	if(StateConstant.REPORT_DAY.equals(searchdto.getReporttype())){
    		return "='" + DateUtil.date2String2(searchdto.getStartdate()) + "'";
    	}else if(StateConstant.REPORT_TEN.equals(searchdto.getReporttype())){
    		if(StateConstant.TENRPT_TOP.equals(searchdto.getReportkind())){	//上旬
    			return "BETWEEN '" + DateUtil.date2String2(searchdto.getStartdate()).substring(0, 6) + "01' AND '" +  DateUtil.date2String2(searchdto.getStartdate()).substring(0, 6) + "10' ";
    		}else if(StateConstant.TENRPT_MIDDLE.equals(searchdto.getReportkind())){//中旬
    			return "BETWEEN '" + DateUtil.date2String2(searchdto.getStartdate()).substring(0, 6) + "11' AND '" +  DateUtil.date2String2(searchdto.getStartdate()).substring(0, 6) + "20' ";
    		}else{	//下旬
    			return "BETWEEN '" + DateUtil.date2String2(searchdto.getStartdate()).substring(0, 6) + "21' AND '" +  DateUtil.date2String2(searchdto.getStartdate()).substring(0, 6) + DateUtil.date2String2(com.cfcc.jaf.common.util.DateUtil.getMonthLastDay(searchdto.getStartdate())).substring(6, 8) + "' ";
    		}
    	}else if(StateConstant.REPORT_MONTH.equals(searchdto.getReporttype())){
    		return "BETWEEN '" + DateUtil.date2String2(searchdto.getStartdate()).substring(0, 6) + "01' AND '" +  DateUtil.date2String2(searchdto.getEnddate()).substring(0, 6) + DateUtil.date2String2(com.cfcc.jaf.common.util.DateUtil.getMonthLastDay(searchdto.getEnddate())).substring(6, 8) + "' ";
    	}else if(StateConstant.REPORT_QUAR.equals(searchdto.getReporttype())){
    		if(StateConstant.QUARTERRPT_FIRST.equals(searchdto.getReportkind())){	//第一季
    			return "BETWEEN '" + DateUtil.date2String2(searchdto.getStartdate()).substring(0, 4) + "0101' AND '" +  DateUtil.date2String2(searchdto.getEnddate()).substring(0, 4) + "0331' ";
    		}else if(StateConstant.QUARTERRPT_SECOND.equals(searchdto.getReportkind())){	//第二季
    			return "BETWEEN '" + DateUtil.date2String2(searchdto.getStartdate()).substring(0, 4) + "0401' AND '" +  DateUtil.date2String2(searchdto.getEnddate()).substring(0, 4) + "0630' ";
    		}else if(StateConstant.QUARTERRPT_THREE.equals(searchdto.getReportkind())){	//第三季
    			return "BETWEEN '" + DateUtil.date2String2(searchdto.getStartdate()).substring(0, 4) + "0701' AND '" +  DateUtil.date2String2(searchdto.getEnddate()).substring(0, 4) + "0930' ";
    		}else{
    			return "BETWEEN '" + DateUtil.date2String2(searchdto.getStartdate()).substring(0, 4) + "1001' AND '" +  DateUtil.date2String2(searchdto.getEnddate()).substring(0, 4) + "1231' ";
    		}
    	}else if(StateConstant.REPORT_HALFYEAR.equals(searchdto.getReporttype())){
    		if(StateConstant.HALFYEARRPT_TOP.equals(searchdto.getReportkind())){	//上半年
    			return "BETWEEN '" + DateUtil.date2String2(searchdto.getStartdate()).substring(0, 4) + "0101' AND '" +  DateUtil.date2String2(searchdto.getEnddate()).substring(0, 4) + "0630' ";
    		}else{	//下半年
    			return "BETWEEN '" + DateUtil.date2String2(searchdto.getStartdate()).substring(0, 4) + "0701' AND '" +  DateUtil.date2String2(searchdto.getEnddate()).substring(0, 4) + "1231' ";
    		}
    	}else{
    		return "BETWEEN '" + DateUtil.date2String2(searchdto.getStartdate()).substring(0, 4) + "0101' AND '" +  DateUtil.date2String2(searchdto.getEnddate()).substring(0, 4) + "1231' ";
    	}
    }

}
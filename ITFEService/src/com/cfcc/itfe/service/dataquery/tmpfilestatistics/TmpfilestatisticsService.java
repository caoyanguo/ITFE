package com.cfcc.itfe.service.dataquery.tmpfilestatistics;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvInFileTmpDto;
import com.cfcc.itfe.persistence.dto.TvInfileTmpDto;
import com.cfcc.itfe.service.dataquery.tmpfilestatistics.AbstractTmpfilestatisticsService;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
/**
 * @author Administrator
 * @time   12-05-16 16:26:36
 * codecomment: 
 */

public class TmpfilestatisticsService extends AbstractTmpfilestatisticsService {
	private static Log log = LogFactory.getLog(TmpfilestatisticsService.class);	


	/**
	 * 统计电子税票
	 	 
	 * @generated
	 * @param starttime
	 * @param endtime
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List getlist(String starttime, String endtime, String trecode) throws ITFEBizException {
    	StringBuffer sql = new StringBuffer();

    	sql.append("SELECT S_TRECODE AS strecode,S_PAYBNKNO AS bankno,");
    	sql.append("CASE WHEN SUBSTR (S_TAXORGCODE,1,1) = '1' THEN '国税' WHEN SUBSTR (S_TAXORGCODE,1,1)='2' THEN '地税' WHEN SUBSTR (S_TAXORGCODE,1,1)='3' THEN '海关' WHEN SUBSTR (S_TAXORGCODE,1,1)='4' THEN '财政'  ELSE '其他'END AS placename,");
    	sql.append("count(1) AS sunnum,sum(F_TRAAMT) AS sumamt,");
    	sql.append("CASE WHEN s_orimsgno = '1102' THEN '批扣' WHEN s_orimsgno = '1001' THEN '实扣' WHEN s_orimsgno = '1130' THEN '自缴核销' WHEN s_orimsgno = '2090' then '银行端交款' ELSE s_orimsgno END AS biztype");
    	sql.append(" FROM HTV_FIN_INCOMEONLINE  WHERE (S_ACCT  BETWEEN ?  AND ? ) AND ( S_TRASTATE IN ('20','30','40','50','60')) ");
    	sql.append(trecode);
    	sql.append(" GROUP BY S_TRECODE,S_TRENAME,S_PAYBNKNO,SUBSTR (S_TAXORGCODE,1,1),s_orimsgno");
    	try {
			SQLExecutor sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.addParam(starttime);
			sqlExecutor.addParam(endtime);
			SQLResults result =  sqlExecutor.runQueryCloseCon(sql.toString(), TvInFileTmpDto.class);
			if(null != result && result.getDtoCollection().size() > 0){
				Map<String, TsPaybankDto> mapTsPayBank = SrvCacheFacade.cachePayBankInfo();
				Map<String, TsTreasuryDto> mapTreInfo = SrvCacheFacade.cacheTreasuryInfo(this.getLoginInfo().getSorgcode());
				List<TvInFileTmpDto> resultList = (List<TvInFileTmpDto>) result.getDtoCollection();
				List dtoList = new ArrayList();
				for(TvInFileTmpDto dto : resultList){
					dto.setBankname(mapTsPayBank.get(dto.getBankno()) == null ? null : mapTsPayBank.get(dto.getBankno()).getSbankname());
					dto.setStrecodename(mapTreInfo.get(dto.getStrecode()) == null ? null : mapTreInfo.get(dto.getStrecode()).getStrename());
					dtoList.add(dto);
				}
				return dtoList;
			}else{
				return null;
			}
		} catch (JAFDatabaseException e) {
			log.error("查询数据库失败！");
			throw new ITFEBizException("统计电子税票信息失败!", e);
		}
    }



}
package com.cfcc.itfe.service.dataquery.querylogconsole;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TvRecvLogShowDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
/**
 * @author sjz
 * @time   10-01-01 21:35:35
 * codecomment: 
 */

public class QueryLogConsoleService extends AbstractQueryLogConsoleService {
	private static Log log = LogFactory.getLog(QueryLogConsoleService.class);	


	/**
	 * 查询指定日期的接收（发送）日志	 
	 * @generated
	 * @param strType
	 * @param queryDate
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List<TvRecvLogShowDto> getLogByDate(String strType,String queryDate) throws ITFEBizException {
    	try{
    		//用户信息
    		ITFELoginInfo user = getLoginInfo();
    		String sql = "";
    		if (StateConstant.LOG_SEND.equals(strType)) {// 发送
    			sql = "select a.s_sendno,a.s_recvno,a.s_date,a.s_operationtypecode,b.s_operationtypename,"
            		+ "a.s_recvorgcode,a.s_title,a.s_sendtime,a.s_retcode,a.s_retcodedesc "
            		+ "from tv_sendlog a,ts_operationtype b "
            		+ "where a.s_sendorgcode='" + user.getSorgcode() + "' and a.s_operationtypecode=b.s_operationtypecode ";
            	if ((queryDate != null) && (queryDate.length() > 0)){
            		//如果日期不为空，那么增加日期参数
            		sql += "and a.s_date='" + queryDate + "' ";
            	}
            	sql += "order by a.s_sendtime desc";
    		} else {// 接收
    			sql = "select a.s_recvno,a.s_sendno,a.s_date,a.s_operationtypecode,b.s_operationtypename,"
            		+ "a.s_sendorgcode,c.s_orgname,a.s_title,a.s_recvtime,a.s_retcode,a.s_retcodedesc "
            		+ "from tv_recvlog a,ts_operationtype b,ts_organ c "
            		+ "where a.s_recvorgcode='" + user.getSorgcode() + "' and "
            		+ "a.s_operationtypecode=b.s_operationtypecode and a.s_sendorgcode=c.s_orgcode ";
            	if ((queryDate != null) && (queryDate.length() > 0)){
            		//如果日期不为空，那么增加日期参数
            		sql += "and a.s_date='" + queryDate + "' ";
            	}
            	sql += "order by a.s_recvtime desc";
    		}
    		SQLResults res = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor().runQueryCloseCon(sql, TvRecvLogShowDto.class);
        	List<TvRecvLogShowDto> result = (List<TvRecvLogShowDto>)res.getDtoCollection();
    		return result;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("查询收发日志时出错", e);
		}
    }

}
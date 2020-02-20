package com.cfcc.itfe.service.dataquery.incomeprintproc;

import java.util.*;
import java.math.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.io.*;
import java.lang.*;

import org.apache.commons.logging.*;

import com.cfcc.deptone.mto.paymentmto.msgparser.conf.MsgConstant;
import com.cfcc.itfe.service.dataquery.incomeprintproc.AbstractDealIncomeService;
import com.cfcc.itfe.exception.ITFEBizException;import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.itfe.persistence.dto.IncomeReportDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan
 * @time   10-05-24 13:45:48
 * codecomment: 
 */

public class DealIncomeService extends AbstractDealIncomeService {
	private static Log log = LogFactory.getLog(DealIncomeService.class);	


	/**
	 * 分页查询国库收入信息	 
	 * @generated
	 * @param finddto
	 * @param pageRequest
	 * @return com.cfcc.jaf.common.page.PageResponse
	 * @throws ITFEBizException	 
	 */
    public PageResponse findIncomeByPage(TvInfileDto finddto, PageRequest pageRequest) throws ITFEBizException {
		try {
			// 取得操作员的机构代码
			String orgcode = this.getLoginInfo().getSorgcode();
			finddto.setSorgcode(orgcode);
			
			String wherestr = "";
			return CommonFacade.getODB().findRsByDtoPagingGroupby(finddto, pageRequest, null, " S_ACCDATE, S_RECVTRECODE, S_FILENAME ");

		} catch (JAFDatabaseException e) {
			log.error("分页查询国库收入信息时错误!", e);
			throw new ITFEBizException("分页查询国库收入信息时错误!", e);
		} catch (ValidateException e) {
			log.error("分页查询国库收入信息时错误!", e);
			throw new ITFEBizException("分页查询国库收入信息时错误!", e);
		}
    }

	/**
	 * 查询打印国库收入信息	 
	 * @generated
	 * @param printdto
	 * @return com.cfcc.itfe.persistence.dto.TvInfileDto
	 * @throws ITFEBizException	 
	 */
	public List findIncomeByPrint(TvInfileDto printdto) throws ITFEBizException {
		// TODO Auto-generated method stub
		SQLExecutor selectsqlExec = null;
		SQLResults rs = null;
		List list = new ArrayList();
		
		try
		{
		selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
		
		String reportsql;
		reportsql = "select a.S_ACCDATE,c.S_TRENAME, e.S_ORGNAME as S_RECVTRENAME, d.S_TAXORGNAME, a.S_BUDGETTYPE, f.S_VALUECMT as S_BUDGETLEVELCODE, a.S_ASSITSIGN, a.S_BUDGETSUBCODE,b.S_SUBJECTNAME as S_BUDGETSUBNAME, a.N_MONEY"
			+ " from (select S_ORGCODE, S_ACCDATE, S_RECVTRECODE, S_TAXORGCODE, S_BUDGETLEVELCODE, S_BUDGETTYPE, S_ASSITSIGN, S_BUDGETSUBCODE,  sum(N_MONEY) as N_MONEY"
			+ " from TV_INFILE where S_ORGCODE = ? and S_FILENAME = ?"
			+ " group by S_ORGCODE, S_ACCDATE, S_RECVTRECODE, S_TAXORGCODE, S_BUDGETLEVELCODE, S_BUDGETTYPE, S_ASSITSIGN, S_BUDGETSUBCODE) a"
			+ " left join TS_BUDGETSUBJECT b on a.S_ORGCODE = b.S_ORGCODE and a.S_BUDGETSUBCODE = b.S_SUBJECTCODE and a.S_BUDGETTYPE = b.S_BUDGETTYPE"
		+ " left join TS_TREASURY c on a.S_ORGCODE = c.S_ORGCODE and a.S_RECVTRECODE = c.S_TRECODE and c.S_TRIMFLAG ='"
		+ MsgConstant.TBS_TRIMFLAG_NORMAL  + "'"
		+ " left join TS_TAXORG d on a.S_ORGCODE = d.S_ORGCODE and a.S_RECVTRECODE = d.S_TRECODE and a.S_TAXORGCODE =d.S_TAXORGCODE"
		+ " left join TS_ORGAN e on a.S_ORGCODE = e.S_ORGCODE"
		+ " left join (select n.S_VALUE, n.S_VALUECMT from TD_ENUMTYPE m, TD_ENUMVALUE n where m.S_TYPECODE = '0121' and m.S_TYPECODE = n.S_TYPECODE) f"
		+ " on a.S_BUDGETLEVELCODE = f.S_VALUE";
		
		// 取得操作员的机构代码
		String orgcode = this.getLoginInfo().getSorgcode();
		selectsqlExec.addParam(orgcode);
		selectsqlExec.addParam(printdto.getSfilename());
		
		rs = selectsqlExec.runQueryCloseCon(reportsql,IncomeReportDto.class);
		}catch (JAFDatabaseException e) {
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
			
			log.error("查询打印国库收入信息的时候出现异常!", e);
			throw new ITFEBizException("查询打印国库收入信息的时候出现异常!", e);
		}
		list.addAll(rs.getDtoCollection());
		
		return list;
	}

}
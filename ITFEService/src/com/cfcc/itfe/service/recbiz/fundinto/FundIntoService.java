package com.cfcc.itfe.service.recbiz.fundinto;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.BizDataCountDto;
import com.cfcc.itfe.persistence.dto.ShiboDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsCheckbalanceDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * @author Administrator
 * @time   12-02-25 10:48:59
 * codecomment: 
 */

public class FundIntoService extends AbstractFundIntoService {
	private static Log log = LogFactory.getLog(FundIntoService.class);	


	/**
	 * 获取收款人信息
	 	 
	 * @generated
	 * @param taxcode
	 * @param orgcode
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List getPayerInfo(String taxcode, String orgcode) throws ITFEBizException {
    	String sql = "SELECT acc.S_PAYERACCOUNT AS payeracct,acc.S_PAYERNAME AS payername,acc.S_PAYERADDR AS payeraddress FROM TS_INFOCONNORGACC acc,TS_CONVERTTAXORG org " +
    				" WHERE acc.S_ORGCODE = org.S_ORGCODE AND acc.S_ORGCODE = ? AND acc.S_TRECODE = org.S_TRECODE " +
                    " AND org.S_TBSTAXORGCODE = ? ";
    	try {
			SQLExecutor sqlexec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlexec.addParam(orgcode);
			sqlexec.addParam(taxcode);
			return (List) sqlexec.runQueryCloseCon(sql, ShiboDto.class).getDtoCollection();
    	} catch (JAFDatabaseException e) {
			log.error("查询数据库有误！", e);
			throw new ITFEBizException("查询数据库有误！" ,e);
		}
    }

    /**
	 * 得到预算科目代码缓存
	 	 
	 * @generated
	 * @return java.util.Map
	 * @throws ITFEBizException	 
	 */
	public Map makeBudgCode() throws ITFEBizException {
		Map<String, TsBudgetsubjectDto> map;
		try {
			map = SrvCacheFacade.cacheTsBdgsbtInfo(getLoginInfo().getSorgcode());
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("获取预算科目信息异常",e);
		}
		return map;
	}
	
	/**
	 * 保存或更新数据
	 */
	public void createOrUpdate(IDto idto) throws ITFEBizException {
		try {
			TsCheckbalanceDto balance = (TsCheckbalanceDto)idto;
			
			TsCheckbalanceDto bal = new TsCheckbalanceDto();
			bal.setSorgcode(getLoginInfo().getSorgcode());
			bal.setSusercode(getLoginInfo().getSuserCode());
			bal.setSgroupid(balance.getSgroupid());
			
			List l = CommonFacade.getODB().findRsByDtoWithUR(bal);
			if(null == l || l.size() == 0) {
				balance.setSisbalance("0");
				DatabaseFacade.getODB().create(balance);
			}else {				
				String updateSQL = "update TS_CHECKBALANCE set I_TOTALNUM=? ,N_TOTALMONEY=? WHERE S_ORGCODE=? AND S_GROUPID=? AND S_USERCODE=?";
				SQLExecutor sqlexec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				sqlexec.addParam(balance.getItotalnum());
				sqlexec.addParam(balance.getNtotalmoney());
				sqlexec.addParam(getLoginInfo().getSorgcode());
				sqlexec.addParam(balance.getSgroupid());
				sqlexec.addParam(getLoginInfo().getSuserCode());
				sqlexec.runQueryCloseCon(updateSQL);
			}			
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("保存或更新数据异常",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("保存或更新数据异常",e);
		}
	}

	/**
	 * 更新组号数据
	 */
	public String updateGroup(BizDataCountDto paramdto, String allsql,
			String sqlwhere, IDto idto,String operType) throws ITFEBizException {
		try {
			String updateSQL = "";
			if("add".equals(operType)) {
				updateSQL = "update TS_CHECKBALANCE set I_CHECKEDNUM=nvl(I_CHECKEDNUM,0)+1 ,N_CHECKMONEY=nvl(N_CHECKMONEY,0)+? " +
					"WHERE S_ORGCODE=? AND S_GROUPID=? AND S_USERCODE=?";
			}else if("wipe".equals(operType)) {
				updateSQL = "update TS_CHECKBALANCE set I_CHECKEDNUM=nvl(I_CHECKEDNUM,0)-1 ,N_CHECKMONEY=nvl(N_CHECKMONEY,0)-? " +
					"WHERE S_ORGCODE=? AND S_GROUPID=? AND S_USERCODE=? AND S_ISBALANCE=?";
				DatabaseFacade.getODB().update(idto);
			}
					
			SQLExecutor sqlexec = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlexec.addParam(paramdto.getTotalfamt());
			sqlexec.addParam(getLoginInfo().getSorgcode());
			sqlexec.addParam(paramdto.getBizname());
			sqlexec.addParam(getLoginInfo().getSuserCode());
			if("wipe".equals(operType)) {
				sqlexec.addParam("0");
			}
			sqlexec.runQueryCloseCon(updateSQL);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("更新数据异常",e);
		}
		
		return sqlwhere;		
	}

	/**
	 * 删除数据	 	 
	 * @generated
	 * @param idto
	 * @throws ITFEBizException	 
	 */
   public void deleteData(IDto idto) throws ITFEBizException {
	   	try {
			DatabaseFacade.getODB().delete(idto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("删除组号数据异常", e);
		}
   }

}
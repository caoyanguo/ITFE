package com.cfcc.itfe.service.para.tsconverttaxorgforzj;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 功能：浙江征收机关对照表服务组件
 * @author hejianrong
 * @time   14-07-29 10:44:14
 * codecomment: 
 */

public class TsConverttaxorgForZjService extends AbstractTsConverttaxorgForZjService {
	private static Log log = LogFactory.getLog(TsConverttaxorgForZjService.class);	


	/**
	 * 增加	 
	 * @generated
	 * @param dtoInfo
	 * @return com.cfcc.jaf.persistence.jaform.parent.IDto
	 * @throws ITFEBizException	 
	 */
    public IDto addInfo(IDto dtoInfo) throws ITFEBizException {
    	SQLExecutor selectExce = null;
    	
    	try {
			DatabaseFacade.getDb().create(dtoInfo);

			// 校验国地TBS机关代码的唯一性
			String orgcode = this.getLoginInfo().getSorgcode();
			selectExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			// 1.1 校验国税机关代码
			String nationsql = " select S_NATIONTAXORGCODE ,S_TRECODE ,count(*) as num from TS_CONVERTTAXORG "
					+ " where S_ORGCODE = ? and S_NATIONTAXORGCODE <> 'N' group by S_NATIONTAXORGCODE , S_TRECODE having count(*) >= 2";
			selectExce.addParam(orgcode);
			SQLResults nationrs = selectExce.runQuery(nationsql);
			if(nationrs.getRowCount() > 0){
				String errinfo = nationrs.getString(0, 0) + "_" + nationrs.getString(0, 1);
				selectExce.closeConnection();
				throw new ITFEBizException("国税征收机关_国库代码[" + errinfo + "]重复");
			}
			
			// 1.2 校验地税机关代码
			String placesql = " select S_AREATAXORGCODE ,S_TRECODE ,count(*) as num from TS_CONVERTTAXORG "
				+ " where S_ORGCODE = ? and S_AREATAXORGCODE <> 'N' group by S_AREATAXORGCODE , S_TRECODE having count(*) >= 2";
			selectExce.clearParams();
			selectExce.addParam(orgcode);
			SQLResults palcers = selectExce.runQuery(placesql);
			if(palcers.getRowCount() > 0){
				String errinfo = palcers.getString(0, 0) + "_" + palcers.getString(0, 1);
				selectExce.closeConnection();
				throw new ITFEBizException("地税征收机关_国库代码[" + errinfo + "]重复");
			}
			
			// 1.3 校验TBS机关代码
			String tbssql = " select S_TBSTAXORGCODE ,S_TRECODE ,count(*) as num from TS_CONVERTTAXORG "
				+ " where S_ORGCODE = ? and S_TBSTAXORGCODE <> 'N' group by S_TBSTAXORGCODE , S_TRECODE having count(*) >= 2";
			selectExce.clearParams();
			selectExce.addParam(orgcode);
			SQLResults tbsrs = selectExce.runQuery(tbssql);
			if(tbsrs.getRowCount() > 0){
				String errinfo = tbsrs.getString(0, 0) + "_" + tbsrs.getString(0, 1);
				selectExce.closeConnection();
				throw new ITFEBizException("TBS征收机关_国库代码[" + errinfo + "]重复");
			}
			
			if(null != selectExce){
				selectExce.closeConnection();
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) { 
				throw new ITFEBizException(StateConstant.PRIMAYKEY, e);
			}
			throw new ITFEBizException(StateConstant.INPUTFAIL, e);
		} finally{
			if(null != selectExce){
				selectExce.closeConnection();
			}
		}
		return null;
    }


	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void delInfo(IDto dtoInfo) throws ITFEBizException {
    	try {
			CommonFacade.getODB().deleteRsByDto(dtoInfo);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException(StateConstant.DELETEFAIL, e);
		}
    }


	/**
	 * ${JMethod.getCodecomment()}	 
	 * @generated
	 * @param initInfo
	 * @param dtoInfo
	 * @throws ITFEBizException	 
	 */
    public void modInfo(IDto initInfo,IDto dtoInfo) throws ITFEBizException {
    	SQLExecutor selectExce = null;
    	
    	try {
    		TsConverttaxorgDto wheredto = (TsConverttaxorgDto)initInfo;
    		TsConverttaxorgDto updatedto = (TsConverttaxorgDto)dtoInfo;
    		SQLExecutor updatesqlExec = null;

    		String updateSql = "update TS_CONVERTTAXORG set S_NATIONTAXORGCODE = ? , S_AREATAXORGCODE = ? ,S_TCBSTAXORGCODE = ?, I_MODICOUNT = ? where ";
    		updateSql = updateSql + "S_ORGCODE = ? and S_TRECODE = ? and S_TBSTAXORGCODE = ? and S_NATIONTAXORGCODE = ? and S_AREATAXORGCODE = ? ";

			updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updatesqlExec.addParam(updatedto.getSnationtaxorgcode());
			updatesqlExec.addParam(updatedto.getSareataxorgcode());
			updatesqlExec.addParam(updatedto.getStcbstaxorgcode());
			updatesqlExec.addParam(updatedto.getImodicount());
			updatesqlExec.addParam(wheredto.getSorgcode());
			updatesqlExec.addParam(wheredto.getStrecode());
			updatesqlExec.addParam(wheredto.getStbstaxorgcode());
			updatesqlExec.addParam(wheredto.getSnationtaxorgcode());
			updatesqlExec.addParam(wheredto.getSareataxorgcode());
			updatesqlExec.runQuery(updateSql);
			updatesqlExec.closeConnection();
//    		DatabaseFacade.getDb().update(dtoInfo);
			
			// 校验国地TBS机关代码的唯一性
			String orgcode = this.getLoginInfo().getSorgcode();
			selectExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			// 1.1 校验国税机关代码
			String nationsql = " select S_NATIONTAXORGCODE ,S_TRECODE ,count(*) as num from TS_CONVERTTAXORG "
					+ " where S_ORGCODE = ? and S_NATIONTAXORGCODE <> 'N' group by S_NATIONTAXORGCODE , S_TRECODE having count(*) >= 2";
			selectExce.addParam(orgcode);
			SQLResults nationrs = selectExce.runQuery(nationsql);
			if(nationrs.getRowCount() > 0){
				String errinfo = nationrs.getString(0, 0) + "_" + nationrs.getString(0, 1);
				selectExce.closeConnection();
				throw new ITFEBizException("国税征收机关_国库代码[" + errinfo + "]重复");
			}
			
			// 1.2 校验地税机关代码
			String placesql = " select S_AREATAXORGCODE ,S_TRECODE ,count(*) as num from TS_CONVERTTAXORG "
				+ " where S_ORGCODE = ? and S_AREATAXORGCODE <> 'N' group by S_AREATAXORGCODE , S_TRECODE having count(*) >= 2";
			selectExce.clearParams();
			selectExce.addParam(orgcode);
			SQLResults palcers = selectExce.runQuery(placesql);
			if(palcers.getRowCount() > 0){
				String errinfo = palcers.getString(0, 0) + "_" + palcers.getString(0, 1);
				selectExce.closeConnection();
				throw new ITFEBizException("地税征收机关_国库代码[" + errinfo + "]重复");
			}
			
			// 1.3 校验TBS机关代码
			String tbssql = " select S_TBSTAXORGCODE ,S_TRECODE ,count(*) as num from TS_CONVERTTAXORG "
				+ " where S_ORGCODE = ? and S_TBSTAXORGCODE <> 'N' group by S_TBSTAXORGCODE , S_TRECODE having count(*) >= 2";
			selectExce.clearParams();
			selectExce.addParam(orgcode);
			SQLResults tbsrs = selectExce.runQuery(tbssql);
			if(tbsrs.getRowCount() > 0){
				String errinfo = tbsrs.getString(0, 0) + "_" + tbsrs.getString(0, 1);
				selectExce.closeConnection();
				throw new ITFEBizException("TCBS征收机关_国库代码[" + errinfo + "]重复");
			}
			
			if(null != selectExce){
				selectExce.closeConnection();
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			if (e.getSqlState().equals("23505")) {
				throw new ITFEBizException(StateConstant.PRIMAYKEY, e);
			}
			throw new ITFEBizException(StateConstant.MODIFYFAIL, e);
		} finally{
			if(null != selectExce){
				selectExce.closeConnection();
			}
		}
    }

}
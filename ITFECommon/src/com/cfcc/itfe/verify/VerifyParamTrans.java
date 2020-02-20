package com.cfcc.itfe.verify;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 主要功能:校验参数转化是否成功 
 * @author zhouchuan
 *
 */
public class VerifyParamTrans {

	private static Log logger = LogFactory.getLog(VerifyParamTrans.class);	
	
	/**
	 * 校验税票收入参数转化是否成功
	 * 校验字段：征收机关(S_TBS_TAXORGCODE)，辅助标志(S_TBS_ASSITSIGN)
	 * 
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @throws ITFEBizException
	 */
	public static void verifyTBSIncomeTrans(String orgcode, String filename)
			throws ITFEBizException {
		// 先按照 [机构代码+文件名称] 做为原始条件(需要在参数表建立唯一索引或直接设置PK)
		String updateTaxSql = " update TV_INFILE_TMP a "
				+ " set a.S_TAXORGCODE = "
				+ "(select b.S_TCBSTAXORGCODE from TS_CONVERTTAXORG b where a.S_RECVTRECODE = b.S_TRECODE and a.S_ORGCODE = b.S_ORGCODE and a.S_TBS_TAXORGCODE = b.S_TBSTAXORGCODE ) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";

//		String updateAssistSql1 = " update TV_INFILE_TMP a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and a.S_BUDGETSUBCODE = b.S_BUDGETSUBCODE) "
//				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
//		
//		String updateAssistSql2 = " update TV_INFILE_TMP a "
//			+ " set a.S_ASSITSIGN = "
//			+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and b.S_BUDGETSUBCODE ='N' and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)) "
//			+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)";
		
		//按国库代码和科目代码精确匹配
		String updateAssistSql1 = " update TV_INFILE_TMP a "
			+ " set a.S_ASSITSIGN = "
			+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and a.S_RECVTRECODE = b.S_TRECODE and a.S_BUDGETSUBCODE = b.S_BUDGETSUBCODE and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN ) "
			+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
	
		//国库代码精确匹配、科目代码为N
		String updateAssistSql2 = " update TV_INFILE_TMP a "
		+ " set a.S_ASSITSIGN = "
		+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and a.S_RECVTRECODE = b.S_TRECODE and b.S_BUDGETSUBCODE ='N' and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)) "
		+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)";
		
		//科目代码精确匹配、国库代码为N
		String updateAssistSql3 = " update TV_INFILE_TMP a "
			+ " set a.S_ASSITSIGN = "
			+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and b.S_TRECODE = 'N' and a.S_BUDGETSUBCODE = b.S_BUDGETSUBCODE and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)) "
			+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)";
			
		//科目代码为N、国库代码为N
		String updateAssistSql4 = " update TV_INFILE_TMP a "
			+ " set a.S_ASSITSIGN = "
			+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and b.S_TRECODE = 'N' and b.S_BUDGETSUBCODE = 'N' and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)) "
			+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)";
			
		String selectTaxSql = " select distinct S_RECVTRECODE,S_TBS_TAXORGCODE from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and (S_TAXORGCODE is NULL)";
		String selectAssistsign = "SELECT DISTINCT S_BUDGETSUBCODE FROM TV_INFILE_TMP WHERE S_ORGCODE = ? AND S_FILENAME = ? AND S_BUDGETLEVELCODE= ? AND (S_ASSITSIGN ='' OR S_ASSITSIGN IS NULL)";
		SQLExecutor updatesqlExec = null;
		SQLExecutor selectsqlExec = null;
		SQLExecutor selectAsssqlExec = null;
		try {
			// 第一步 先进行征收机关参数转化
			updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateTaxSql);
			
			// 第二步 进行第一次辅助标志参数转化
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateAssistSql1);
			
			// 第三步 进行第一次辅助标志参数转化
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateAssistSql2);
			
			// 第四步 进行第一次辅助标志参数转化
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateAssistSql3);
			
			// 第五步 进行第二次辅助标志参数转化
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQueryCloseCon(updateAssistSql4);
			
			// 第六步 判断征收机关参数转化是否成功,辅助标志不用做判断
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults rs = selectsqlExec.runQueryCloseCon(selectTaxSql);
			int count = rs.getRowCount();
			
			//第七步 判断共享级次的辅助标志是否为空
			selectAsssqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectAsssqlExec.addParam(orgcode);
			selectAsssqlExec.addParam(filename);
			selectAsssqlExec.addParam(MsgConstant.BUDGET_LEVEL_SHARE);
			SQLResults rsAss = selectAsssqlExec.runQueryCloseCon(selectAssistsign);
			int countAss = rsAss.getRowCount();
			if(count == 0 && countAss == 0){
				return ;
			}else{
				StringBuffer sbuf= new StringBuffer("");
				if(count > 0){
					for(int i = 0 ; i < count; i++){
						sbuf.append("国库 [")
						.append(rs.getString(i, 0))
						.append("] 对应的征收机关代码 [")
						.append(rs.getString(i, 1))
						.append("] 没有维护参数对照表!")
						.append("\r\n");
					}
				}
				if(countAss > 0 && !"000001900000".equals(ITFECommonConstant.SRC_NODE)){
					sbuf.append("共享级次科目");
					for(int i = 0 ; i < countAss; i++){
						sbuf.append(" [")
						.append(rsAss.getString(i, 0))
						.append("]");
						if(i!=countAss-1){
							sbuf.append(",");
						}
					}
					sbuf.append("的辅助标志为空或者没有维护辅助标志对照表!");
				}
				if("".equals(sbuf.toString()))
					return ;
				throw new ITFEBizException(sbuf.toString());
			}
		} catch (JAFDatabaseException e) {
			if(null != updatesqlExec){
				updatesqlExec.closeConnection();
			}
			
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
			
			logger.error("进行收入税票参数转化的时候出现异常!", e);
			throw new ITFEBizException("进行收入税票参数转化的时候出现异常!", e);
		}finally{
			if(null != updatesqlExec){
				updatesqlExec.closeConnection();
			}
			
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
		}
	}
	/**
	 * 把TCBS征收机关设为TBS征收机关，TCBS辅助标志设为TBS辅助标志(广西)
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @throws ITFEBizException
	 */
	public static void SetTCBSIncomeTrans(String orgcode, String filename)
			throws ITFEBizException {
		String updateTaxSql = " update TV_INFILE_TMP a "
				+ " set a.S_TAXORGCODE = a.S_TBS_TAXORGCODE"
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and a.s_dealno=a.S_DEALNO";

		String updateAssistSql1 = " update TV_INFILE_TMP a "
			+ " set a.S_ASSITSIGN =a.S_TBS_ASSITSIGN "
			+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and a.s_dealno=a.S_DEALNO ";
	
		SQLExecutor updatesqlExec = null;
		try {
			// 第一步 先进行征收机关参数转化
			updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateTaxSql);
			
			// 第二步 进行第一次辅助标志参数转化
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateAssistSql1);
			
		} catch (JAFDatabaseException e) {
			if(null != updatesqlExec){
				updatesqlExec.closeConnection();
			}
			logger.error("进行收入税票参数转化的时候出现异常!", e);
			throw new ITFEBizException("进行收入税票参数转化的时候出现异常!", e);
		}finally{
			if(null != updatesqlExec){
				updatesqlExec.closeConnection();
			}
		}
	}
	
	/**
	 * 校验地税税票收入参数转化是否成功
	 * 
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            s_operbatch 文件批次
	 * @throws ITFEBizException
	 */
	public static List<FileObjDto> verifyPlaceIncomeTrans(String orgcode,String s_operbatch) throws ITFEBizException{
		
		SQLExecutor updateTreExec = null;
		SQLExecutor selectTreExec = null;
		
		SQLExecutor updateTaxExec = null;
		SQLExecutor selectTaxExec = null;
		
		SQLExecutor selectTaxTreRefExce = null;
		SQLExecutor tmpSelectExce = null;
		
		SQLExecutor updateBnkExec = null;
		
		SQLExecutor updateassistflagExce = null;
		
		SQLExecutor updatelevelExce = null;
		
		try {
			// 第一步 先进行国库参数转化并校验
			String updateTreSql = " update TV_INFILE_TMP_PLACE a "
				+ " set a.S_TCBSTRECODE = "
				+ "(select b.S_TRECODE from TS_TREASURY b where a.S_TRECODE = b.S_TRECODEAREA and b.S_ORGCODE = ? and b.S_TRIMFLAG = '0' ) "
				+ " where a.S_OPERBATCH = ? ";
			String selectTreSql = " select distinct S_TRECODE from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? and (S_TCBSTRECODE is NULL or S_TCBSTRECODE = '')";
			// 1.1 转化
			updateTreExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateTreExec.addParam(orgcode);
			updateTreExec.addParam(s_operbatch);
			updateTreExec.runQueryCloseCon(updateTreSql);
			// 1.2校验
			selectTreExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTreExec.addParam(s_operbatch);
			SQLResults treRs = selectTreExec.runQueryCloseCon(selectTreSql);
			int treCount = treRs.getRowCount();
			if(treCount != 0){
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < treCount; i++){
					sbuf.append("地税国库代码 [")
					.append(treRs.getString(i, 0))
					.append("] ")
					.append("没有在国库主体代码参数中维护！")
					.append("\r\n");
				}
				throw new ITFEBizException(sbuf.toString());
			}
		
			// 第二步 先进行征收机关参数转化并校验
			String updateTaxSql = " update TV_INFILE_TMP_PLACE a "
				+ " set a.S_TCBSTAXORGCODE = "
				+ "(select b.S_TCBSTAXORGCODE from TS_CONVERTTAXORG b where a.S_TCBSTRECODE = b.S_TRECODE and b.S_ORGCODE = ? and a.S_TAXORGCODE = b.S_AREATAXORGCODE ) "
				+ " where a.S_OPERBATCH = ? ";
			String selectTaxSql = " select distinct S_TAXORGCODE from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? and (S_TCBSTAXORGCODE is NULL or S_TCBSTAXORGCODE = '')";
			// 1.1 转化
			updateTaxExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateTaxExec.addParam(orgcode);
			updateTaxExec.addParam(s_operbatch);
			updateTaxExec.runQueryCloseCon(updateTaxSql);
			// 1.2校验
			selectTaxExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTaxExec.addParam(s_operbatch);
			SQLResults taxRs = selectTaxExec.runQueryCloseCon(selectTaxSql);
			int taxCount = taxRs.getRowCount();
			if(taxCount != 0){
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < taxCount; i++){
					sbuf.append("地税征收机关代码 [")
					.append(taxRs.getString(i, 0))
					.append("] ")
					.append("没有在征收机关对照参数表中维护！")
					.append("\r\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
			
			// 第三步  校验征收机关和国库代码对应关系
			List<FileObjDto> rtndtos = new ArrayList<FileObjDto>();
			String wheresql = " where S_ORGCODE = ? and ( " ; 
			String selectTaxTreRefSql = " select distinct S_TCBSTAXORGCODE , S_TCBSTRECODE from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? ";
			selectTaxTreRefExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTaxTreRefExce.addParam(s_operbatch);
			SQLResults taxTreRs = selectTaxTreRefExce.runQueryCloseCon(selectTaxTreRefSql);
			int taxTreCount = taxTreRs.getRowCount();
			if(taxTreCount == 0){
				logger.error("校验征收机关和国库代码对应关系时,没有找到符合条件的记录!");
				throw new ITFEBizException("校验征收机关和国库代码对应关系时,没有找到符合条件的记录!");
			}else{
				for(int i = 0 ; i < taxTreCount ; i++){
					FileObjDto tmpdto = new FileObjDto();
					String tmptaxorgcode = taxTreRs.getString(i, 0);
					String tmptrecode = taxTreRs.getString(i, 1);
					
					tmpdto.setStaxorgcode(tmptaxorgcode);
					tmpdto.setStrecode(tmptrecode);
					
					rtndtos.add(tmpdto);
					
					if(i == taxTreCount -1){
						wheresql = wheresql + " ( S_TAXORGCODE = '" + tmptaxorgcode + "' and S_TRECODE = '" + tmptrecode + "') ) " ;
					}else{
						wheresql = wheresql + " ( S_TAXORGCODE = '" + tmptaxorgcode + "' and S_TRECODE = '" + tmptrecode + "') or ";
					}
				}
			}
			
			HashMap<String, String> tmpmap = new HashMap<String, String>();
			String tmptaxSql = " select distinct S_TAXORGCODE,S_TRECODE from TS_TAXORG " + wheresql;
			tmpSelectExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			tmpSelectExce.addParam(orgcode);
			SQLResults tmpRs = tmpSelectExce.runQueryCloseCon(tmptaxSql);
			int tmpCount = tmpRs.getRowCount();
			for(int i = 0 ; i < tmpCount ; i++){
				String tmptaxorgcode = tmpRs.getString(i, 0);
				String tmptrecode = tmpRs.getString(i, 1);
				
				tmpmap.put(tmptaxorgcode + "_" + tmptrecode, tmptaxorgcode + "_" + tmptrecode);
			}
			
			for(int j = 0 ; j < rtndtos.size(); j++){
				String tmpkey = rtndtos.get(j).getStaxorgcode() + "_" + rtndtos.get(j).getStrecode();
				if(null == tmpmap.get(tmpkey)){
					throw new ITFEBizException("征收机关 [" + rtndtos.get(j).getStaxorgcode() + "] 与国库 [" + rtndtos.get(j).getStrecode() + "] 的对应关系没有维护!");
				}
			}
			
			// 第四步 进行开户行的转化
			String updateBnkSql = " update TV_INFILE_TMP_PLACE a "
				+ " set a.S_PAYOPBNKNO = "
				+ "(select b.S_PAYERACCCODE from TS_BANKANDPAY b where a.S_PAYBNKNO = b.S_AREATAXBANK and b.S_ORGCODE = ? ) "
				+ " where a.S_OPERBATCH = ? ";
			updateBnkExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateBnkExec.addParam(orgcode);
			updateBnkExec.addParam(s_operbatch);
			updateBnkExec.runQueryCloseCon(updateBnkSql);
			
			// 第五步 进行辅助标志参数转化
			updateassistflagExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			//1.1 匹配预算级次去校验
//			String updateAssistSql4 = " update TV_INFILE_TMP_PLACE a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and b.S_BUDGETSUBJECT = 'N' and b.S_TRECODE = 'N') "
//				+ " where a.S_OPERBATCH = ? and (a.S_ASSITSIGN is NULL or a.S_ASSITSIGN = '') ";
//
//			String updateAssistSql3 = " update TV_INFILE_TMP_PLACE a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and a.S_BUDGETSUBJECTCODE = b.S_BUDGETSUBJECT and b.S_TRECODE = 'N') "
//				+ " where a.S_OPERBATCH = ? and (a.S_ASSITSIGN is NULL or a.S_ASSITSIGN = '') ";
//
//			String updateAssistSql2 = " update TV_INFILE_TMP_PLACE a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and b.S_BUDGETSUBJECT = 'N' and a.S_TCBSTRECODE = b.S_TRECODE) "
//				+ " where a.S_OPERBATCH = ? and (a.S_ASSITSIGN is NULL or a.S_ASSITSIGN = '') ";

			String updateAssistSql1 = " update TV_INFILE_TMP_PLACE a "
				+ " set a.S_ASSITSIGN = "
				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and a.S_BUDGETSUBJECTCODE = b.S_BUDGETSUBJECT and a.S_TCBSTRECODE = b.S_TRECODE) "
				+ " where a.S_OPERBATCH = ? ";
//			String selectAssignSql = " select distinct S_TCBSTRECODE,S_BUDGETSUBJECTCODE from TV_INFILE_TMP_PLACE "
//				+ "where S_OPERBATCH = ? and S_BUDGETLEVELCODE = '6' and (S_ASSITSIGN is NULL or S_ASSITSIGN = '')";
			String selectAssignSql = "select a.S_BUDGETLEVELCODE,a.S_TCBSTRECODE,a.S_BUDGETSUBJECTCODE from "
				+"(select distinct S_BUDGETLEVELCODE,S_TCBSTRECODE,S_BUDGETSUBJECTCODE from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? and S_BUDGETLEVELCODE = '6') a"
				+ " where not exists (select S_BUDGETLEVEL,S_TRECODE,S_BUDGETSUBJECT from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and a.S_BUDGETSUBJECTCODE = b.S_BUDGETSUBJECT and a.S_TCBSTRECODE = b.S_TRECODE)";

//			//1.1 转化(匹配级次、国库和预算科目）
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(s_operbatch);
//			updateassistflagExce.runQuery(updateAssistSql1);

			
//			//1.2  转化(匹配预算级次和国库）
//			updateassistflagExce.clearParams();
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(s_operbatch);
//			updateassistflagExce.runQuery(updateAssistSql2);

			//1.1 校验
			selectTreExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTreExec.addParam(s_operbatch);
			selectTreExec.addParam(orgcode);
			SQLResults assignRs = selectTreExec.runQueryCloseCon(selectAssignSql);
			int assignCount = assignRs.getRowCount();
			if(assignCount != 0){
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < assignCount; i++){
					sbuf.append("国库代码 [")
					.append(assignRs.getString(i, 1))
					.append("] 与预算科目 [")
					.append(assignRs.getString(i, 2))
					.append("] 没有维护辅助标志参数转化!")
					.append("\r\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
			
			//1.2 转化(匹配级次、国库和预算科目）
			updateassistflagExce.addParam(orgcode);
			updateassistflagExce.addParam(s_operbatch);
			updateassistflagExce.runQuery(updateAssistSql1);
//			//1.3 匹配预算级次和国库代码去校验
//			updateassistflagExce.clearParams();
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(s_operbatch);
//			updateassistflagExce.runQuery(updateAssistSql3);
//			
//			//1.4 匹配预算级次、预算科目和国库代码去校验
//			updateassistflagExce.clearParams();
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(s_operbatch);
//			updateassistflagExce.runQueryCloseCon(updateAssistSql4);
			
			// 第六步 进行预算级次的转化
			updatelevelExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updatelevelSql = " update TV_INFILE_TMP_PLACE set S_BUDGETLEVELCODE = '0' where S_BUDGETLEVELCODE = '6' and S_OPERBATCH = ?";
			updatelevelExce.addParam(s_operbatch);
			updatelevelExce.runQueryCloseCon(updatelevelSql);
			
			// 第七步 税率赋值为1.00
			updatelevelExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updatetaxrateSql = " update TV_INFILE_TMP_PLACE set N_TAXRATE = 1.00 where S_OPERBATCH = ?";
			updatelevelExce.addParam(s_operbatch);
			updatelevelExce.runQueryCloseCon(updatetaxrateSql);
			
			return rtndtos;
		} catch (JAFDatabaseException e) {
			if (null != updateTreExec) {
				updateTreExec.closeConnection();
			}
			if (null != selectTreExec) {
				selectTreExec.closeConnection();
			}

			if (null != updateTaxExec) {
				updateTaxExec.closeConnection();
			}
			if (null != selectTaxExec) {
				selectTaxExec.closeConnection();
			}

			if (null != selectTaxTreRefExce) {
				selectTaxTreRefExce.closeConnection();
			}
			if (null != tmpSelectExce) {
				tmpSelectExce.closeConnection();
			}

			if (null != updateBnkExec) {
				updateBnkExec.closeConnection();
			}
			if (null != updateassistflagExce) {
				updateassistflagExce.closeConnection();
			}
			
			if(null != updatelevelExce){
				updatelevelExce.closeConnection();
			}

			logger.error("进行收入税票参数转化的时候出现异常!", e);
			throw new ITFEBizException("进行收入税票参数转化的时候出现异常!", e);
		}
	}
	
	/**
	 * 校验地税税票收入参数转化是否成功
	 * 
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @throws ITFEBizException
	 */
	public static List<FileObjDto> verifyNationIncomeTrans(String orgcode,String filename) throws ITFEBizException{
		
		SQLExecutor updateTreExec = null;
		SQLExecutor selectTreExec = null;
		
		SQLExecutor updateTaxExec = null;
		SQLExecutor selectTaxExec = null;
		
		SQLExecutor selectTaxTreRefExce = null;
		SQLExecutor tmpSelectExce = null;
		
		SQLExecutor updateBnkExec = null;
		
		SQLExecutor updateassistflagExce = null;
		
		SQLExecutor updatelevelExce = null;
		
		try {
			// 第一步 先进行国库参数转化并校验
			String updateTreSql = " update TV_INFILE_TMP_COUNTRY a "
				+ " set a.S_TCBSTRECODE = "
				+ "(select b.S_TRECODE from TS_TREASURY b where a.S_PAYEETRECODE = b.S_TRECODENATION and b.S_ORGCODE = ? and b.S_TRIMFLAG = '0' ) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
			String selectTreSql = " select distinct S_PAYEETRECODE from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? and (S_TCBSTRECODE is NULL or S_TCBSTRECODE = '')";
			// 1.1 转化
			updateTreExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateTreExec.addParam(orgcode);
			updateTreExec.addParam(orgcode);
			updateTreExec.addParam(filename);
			updateTreExec.runQueryCloseCon(updateTreSql);
			// 1.2校验
			selectTreExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTreExec.addParam(orgcode);
			selectTreExec.addParam(filename);
			SQLResults treRs = selectTreExec.runQueryCloseCon(selectTreSql);
			int treCount = treRs.getRowCount();
			if(treCount != 0){
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < treCount; i++){
					sbuf.append("国税国库代码 [")
					.append(treRs.getString(i, 0))
					.append("] ")
					.append("没有在国库主体代码参数中维护！")
					.append("\r\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
		
			// 第二步 先进行征收机关参数转化并校验
			String updateTaxSql = " update TV_INFILE_TMP_COUNTRY a "
				+ " set a.S_TCBSTAXORGCODE = "
				+ "(select b.S_TCBSTAXORGCODE from TS_CONVERTTAXORG b where a.S_TCBSTRECODE = b.S_TRECODE and b.S_ORGCODE = ? and a.S_TAXORGCODE = b.S_NATIONTAXORGCODE ) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
			String selectTaxSql = " select distinct S_TAXORGCODE from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? and (S_TCBSTAXORGCODE is NULL or S_TCBSTAXORGCODE = '')";
			// 1.1 转化
			updateTaxExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateTaxExec.addParam(orgcode);
			updateTaxExec.addParam(orgcode);
			updateTaxExec.addParam(filename);
			updateTaxExec.runQueryCloseCon(updateTaxSql);
			// 1.2校验
			selectTaxExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTaxExec.addParam(orgcode);
			selectTaxExec.addParam(filename);
			SQLResults taxRs = selectTaxExec.runQueryCloseCon(selectTaxSql);
			int taxCount = taxRs.getRowCount();
			if(taxCount != 0){
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < taxCount; i++){
					sbuf.append("国税征收机关代码 [")
					.append(taxRs.getString(i, 0))
					.append("] ")
					.append("没有在征收机关对照参数表中维护！")
					.append("\r\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
			
			// 第三步  校验征收机关和国库代码对应关系
			List<FileObjDto> rtndtos = new ArrayList<FileObjDto>();
			String wheresql = " where S_ORGCODE = ? and ( " ; 
			String selectTaxTreRefSql = " select distinct S_TCBSTAXORGCODE , S_TCBSTRECODE from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? ";
			selectTaxTreRefExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTaxTreRefExce.addParam(orgcode);
			selectTaxTreRefExce.addParam(filename);
			SQLResults taxTreRs = selectTaxTreRefExce.runQueryCloseCon(selectTaxTreRefSql);
			int taxTreCount = taxTreRs.getRowCount();
			if(taxTreCount == 0){
				logger.error("校验征收机关和国库代码对应关系时,没有找到符合条件的记录!");
				throw new ITFEBizException("校验征收机关和国库代码对应关系时,没有找到符合条件的记录!");
			}else{
				for(int i = 0 ; i < taxTreCount ; i++){
					FileObjDto tmpdto = new FileObjDto();
					String tmptaxorgcode = taxTreRs.getString(i, 0);
					String tmptrecode = taxTreRs.getString(i, 1);
					
					tmpdto.setStaxorgcode(tmptaxorgcode);
					tmpdto.setStrecode(tmptrecode);
					
					rtndtos.add(tmpdto);
					
					if(i == taxTreCount -1){
						wheresql = wheresql + " ( S_TAXORGCODE = '" + tmptaxorgcode + "' and S_TRECODE = '" + tmptrecode + "') ) " ;
					}else{
						wheresql = wheresql + " ( S_TAXORGCODE = '" + tmptaxorgcode + "' and S_TRECODE = '" + tmptrecode + "') or ";
					}
				}
			}
			
			HashMap<String, String> tmpmap = new HashMap<String, String>();
			String tmptaxSql = " select distinct S_TAXORGCODE,S_TRECODE from TS_TAXORG " + wheresql;
			tmpSelectExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			tmpSelectExce.addParam(orgcode);
			SQLResults tmpRs = tmpSelectExce.runQueryCloseCon(tmptaxSql);
			int tmpCount = tmpRs.getRowCount();
			for(int i = 0 ; i < tmpCount ; i++){
				String tmptaxorgcode = tmpRs.getString(i, 0);
				String tmptrecode = tmpRs.getString(i, 1);
				
				tmpmap.put(tmptaxorgcode + "_" + tmptrecode, tmptaxorgcode + "_" + tmptrecode);
			}
			
			for(int j = 0 ; j < rtndtos.size(); j++){
				String tmpkey = rtndtos.get(j).getStaxorgcode() + "_" + rtndtos.get(j).getStrecode();
				if(null == tmpmap.get(tmpkey)){
					throw new ITFEBizException("征收机关 [" + rtndtos.get(j).getStaxorgcode() + "] 与国库 [" + rtndtos.get(j).getStrecode() + "] 的对应关系没有维护!");
				}
			}
			
			// 第四步 进行开户行的转化
			String updateBnkSql = " update TV_INFILE_TMP_COUNTRY a "
				+ " set a.S_OPENACCBANKCODE = "
				+ "(select b.S_PAYERACCCODE from TS_BANKANDPAY b where a.S_PAYBNKNO = b.S_NATIONTAXBANK and b.S_ORGCODE = ? ) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
			updateBnkExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateBnkExec.addParam(orgcode);
			updateBnkExec.addParam(orgcode);
			updateBnkExec.addParam(filename);
			updateBnkExec.runQueryCloseCon(updateBnkSql);
			
			// 第五步 进行辅助标志参数转化
			updateassistflagExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
//			String updateAssistSql4 = " update TV_INFILE_TMP_COUNTRY a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and b.S_BUDGETSUBJECT = 'N' and b.S_TRECODE = 'N') "
//				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and ( a.S_ASSITSIGN is NULL or a.S_ASSITSIGN = '' )";
//			String updateAssistSql3 = " update TV_INFILE_TMP_COUNTRY a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and a.S_BUDGETSUBCODE = b.S_BUDGETSUBJECT and b.S_TRECODE = 'N') "
//				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and ( a.S_ASSITSIGN is NULL or a.S_ASSITSIGN = '' )";
//			String updateAssistSql2 = " update TV_INFILE_TMP_COUNTRY a "
//				+ " set a.S_ASSITSIGN = "
//				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and b.S_BUDGETSUBJECT = 'N' and a.S_TCBSTRECODE = b.S_TRECODE ) "
//				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and ( a.S_ASSITSIGN is NULL or a.S_ASSITSIGN = '' )";
			String updateAssistSql1 = " update TV_INFILE_TMP_COUNTRY a "
				+ " set a.S_ASSITSIGN = "
				+ "(select b.S_TIPSASSISTSIGN from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and a.S_BUDGETSUBCODE = b.S_BUDGETSUBJECT and a.S_TCBSTRECODE = b.S_TRECODE) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
//			String selectAssignSql = " select distinct a.S_TCBSTRECODE,a.S_BUDGETSUBCODE from TV_INFILE_TMP_COUNTRY a, TS_ASSITFLAGTRANS b "
//				+ "where a.S_ORGCODE = ? and a.S_FILENAME = ? and a.S_BUDGETLEVELCODE = '6' and (a.S_BUDGETLEVELCODE <> b.S_BUDGETLEVEL and a.S_BUDGETSUBCODE <> b.S_BUDGETSUBJECT and a.S_TCBSTRECODE <> b.S_TRECODE)";
			String selectAssignSql = "select a.S_BUDGETLEVELCODE,a.S_TCBSTRECODE,a.S_BUDGETSUBCODE from "
				+"(select distinct S_BUDGETLEVELCODE,S_TCBSTRECODE,S_BUDGETSUBCODE from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? and S_BUDGETLEVELCODE = '6') a"
				+ " where not exists (select S_BUDGETLEVEL,S_TRECODE,S_BUDGETSUBJECT from TS_ASSITFLAGTRANS b where b.S_ORGCODE = ? and a.S_BUDGETLEVELCODE = b.S_BUDGETLEVEL and a.S_BUDGETSUBCODE = b.S_BUDGETSUBJECT and a.S_TCBSTRECODE = b.S_TRECODE)";

//			//1.1 转化（匹配级次、国库和预算科目）
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(filename);
//			updateassistflagExce.runQuery(updateAssistSql1);
		
//			//1.2转化（匹配预算级次和国库）
//			updateassistflagExce.clearParams();
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(filename);
//			updateassistflagExce.runQuery(updateAssistSql2);
			
			//1.1 校验
			selectTreExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectTreExec.addParam(orgcode);
			selectTreExec.addParam(filename);
			selectTreExec.addParam(orgcode);
			SQLResults assignRs = selectTreExec.runQueryCloseCon(selectAssignSql);
			int assignCount = assignRs.getRowCount();
			if(assignCount != 0){
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < assignCount; i++){
					sbuf.append("国库代码 [")
					.append(assignRs.getString(i, 1))
					.append("] 与预算科目 [")
					.append(assignRs.getString(i, 2))
					.append("] 没有维护辅助标志参数转化!")
					.append("\r\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
			
			//1.2 转化（匹配级次、国库和预算科目）
			updateassistflagExce.addParam(orgcode);
			updateassistflagExce.addParam(orgcode);
			updateassistflagExce.addParam(filename);
			updateassistflagExce.runQuery(updateAssistSql1);
			
//			//1.3匹配预算级次和国库代码去校验
//			updateassistflagExce.clearParams();
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(filename);
//			updateassistflagExce.runQuery(updateAssistSql3);
//			
//			//1.4 匹配预算级次、预算科目和国库代码去校验
//			updateassistflagExce.clearParams();
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(orgcode);
//			updateassistflagExce.addParam(filename);;
//			updateassistflagExce.runQueryCloseCon(updateAssistSql4);
			
			// 第六步 进行预算级次的转化
			updatelevelExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updatelevelSql = " update TV_INFILE_TMP_COUNTRY set S_BUDGETLEVELCODE = '0' where S_BUDGETLEVELCODE = '6' and S_ORGCODE = ? and S_FILENAME = ?";
			updatelevelExce.addParam(orgcode);
			updatelevelExce.addParam(filename);
			updatelevelExce.runQueryCloseCon(updatelevelSql);
			
			return rtndtos;
		} catch (JAFDatabaseException e) {
			if (null != updateTreExec) {
				updateTreExec.closeConnection();
			}
			if (null != selectTreExec) {
				selectTreExec.closeConnection();
			}

			if (null != updateTaxExec) {
				updateTaxExec.closeConnection();
			}
			if (null != selectTaxExec) {
				selectTaxExec.closeConnection();
			}

			if (null != selectTaxTreRefExce) {
				selectTaxTreRefExce.closeConnection();
			}
			if (null != tmpSelectExce) {
				tmpSelectExce.closeConnection();
			}

			if (null != updateBnkExec) {
				updateBnkExec.closeConnection();
			}
			if (null != updateassistflagExce) {
				updateassistflagExce.closeConnection();
			}
			
			if(null != updatelevelExce){
				updatelevelExce.closeConnection();
			}

			logger.error("进行收入税票参数转化的时候出现异常!", e);
			throw new ITFEBizException("进行收入税票参数转化的时候出现异常!", e);
		}
	}
	
	/**
	 * 校验地税税票收入参数转化是否成功
	 * 
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            s_operbatch 文件批次
	 * @throws ITFEBizException
	 */
	public static List<FileObjDto> verifyTIPSIncomeTrans(String orgcode,String filename) throws ITFEBizException{
		return null;
	}
	
	/**
	 * 校验直接支付(或授权支付)额度的预算种类
	 * 
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            tablename 表名
	 * @throws ITFEBizException
	 */
	public static String verifyPayOutBdgKind(String orgcode, String filename,String tablename) throws ITFEBizException {
		String selectTaxSql = " select distinct S_TZBM from " + tablename
				+ " where S_ORGCODE = ? and S_FILENAME = ? ";
		
		SQLExecutor selectsqlExec = null;
		try {
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults rs = selectsqlExec.runQueryCloseCon(selectTaxSql);
			int count = rs.getRowCount();
			if(count == 0 ){
				logger.error("导入数据失败,请确认!");
				throw new ITFEBizException("导入数据失败,请确认!");
			}
			
			boolean bdgkind_in = false;
			boolean bdgkind_out = false;
			
			for(int i = 0 ; i < count ; i++){
				String sbdgkind = rs.getString(0, 0).replace(".","").replace("0", "").trim();
				if (MsgConstant.BDG_KIND_IN.equals(sbdgkind)
						|| MsgConstant.BDG_KIND_IN_CASH.equals(sbdgkind)) {
					bdgkind_in = true;
				}else if(MsgConstant.BDG_KIND_OUT.equals(sbdgkind)){
					bdgkind_out = false;
				}
			}
			
			if(bdgkind_in && bdgkind_out){
				logger.error("导入数据对应的预算种类不一致,请确认!");
				throw new ITFEBizException("导入数据对应的预算种类不一致,请确认!");
			}
			
			if(bdgkind_in){
				return MsgConstant.BDG_KIND_IN;
			}else if(bdgkind_out){
				return MsgConstant.BDG_KIND_OUT;
			}else{
				logger.error("导入数据对应的预算种类错误,请确认!");
				throw new ITFEBizException("导入数据对应的预算种类错误,请确认!");
			}
		} catch (JAFDatabaseException e) {
			logger.error("进行收入税票参数转化的时候出现异常!", e);
			throw new ITFEBizException("进行收入税票参数转化的时候出现异常!", e);
		}
	}
	
	/**
	 * 校验文件总金额是否一致
	 * 
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            tablename 表名
	 * @param BigDecimal
	 *            fileamt 文件总金额
	 * @throws ITFEBizException
	 */
	public static void verifySumAmt(String orgcode, String filename, String tablename, BigDecimal fileamt) throws ITFEBizException {
		String selectTaxSql = " select sum(N_MONEY) from " + tablename + " where S_ORGCODE = ? and S_FILENAME = ? ";

		SQLExecutor selectsqlExec = null;
		try {
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults rs = selectsqlExec.runQueryCloseCon(selectTaxSql);
			BigDecimal dbamt = rs.getBigDecimal(0, 0);
			if(null == dbamt){
				logger.error("录入文件金额与文件实际金额不相等,请确认!");
				throw new ITFEBizException("录入文件金额与文件实际金额不相等,请确认!");
			}
			
			if(fileamt.compareTo(dbamt) != 0){
				logger.error("录入文件金额[" + fileamt +"]与文件实际金额[" + dbamt +"]不相等,请确认!");
				throw new ITFEBizException("录入文件金额[" + fileamt +"]与文件实际金额[" + dbamt +"]不相等,请确认!");
			}
		} catch (JAFDatabaseException e) {
			logger.error("计算导入文件的金额的时候出现异常!", e);
			throw new ITFEBizException("计算导入文件的金额的时候出现异常!", e);
		}
	}

	/**
	 * 校验预算科目
	 * 
	 * @param String
	 *            csourceflag 数据来源
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            s_operbatch 文件批次
	 * @throws ITFEBizException
	 */
	public static void verifyBdgSbt(String csourceflag,String orgcode, String filename,String s_operbatch) throws ITFEBizException{
		SQLExecutor selectsqlExec = null;
		try {
			// 默认预算科目的调拨标志为非调拨,录入标志为可录入,科目种类为收入,预算种类为预算内、预算外或共用,
			// 科目属性:级次为0 共享时,科目属性必须为3 共享. 级次为1 中央时,科目属性不能为地方固定.
			// 级次为其他[2,3,4,5]的时候,科目属性不能为中央固定
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			SQLResults rs = null;
			if (MsgConstant.MSG_SOURCE_TBS.equals(csourceflag)) {
				String tbssql = "select tmp.S_BUDGETSUBCODE ,tmp.S_BUDGETLEVELCODE , param.S_SUBJECTCODE"
						+ " from ( select * from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? ) tmp left join TS_BUDGETSUBJECT param "
						+ " on tmp.S_ORGCODE = param.S_ORGCODE and tmp.S_BUDGETSUBCODE = param.S_SUBJECTCODE "
						+ " and param.S_MOVEFLAG = '0' and param.S_WRITEFLAG = '1' and param.S_SUBJECTCLASS = '1' "
//						+ " and (param.S_BUDGETTYPE = '1' or param.S_BUDGETTYPE = '3') "
						+ " and ((tmp.S_BUDGETLEVELCODE = '0' and param.S_SUBJECTATTR = '3') or (tmp.S_BUDGETLEVELCODE = '1' and param.S_SUBJECTATTR <> '2') "
						+ " or ((tmp.S_BUDGETLEVELCODE = '2' or tmp.S_BUDGETLEVELCODE = '3' or tmp.S_BUDGETLEVELCODE = '4' or tmp.S_BUDGETLEVELCODE = '5') "
						+ " and param.S_SUBJECTATTR <> '1'))";
				selectsqlExec.addParam(orgcode);
				selectsqlExec.addParam(filename);
				rs = selectsqlExec.runQueryCloseCon(tbssql);
			} else if (MsgConstant.MSG_SOURCE_NATION.equals(csourceflag)) {
				String nationSql = "select tmp.S_BUDGETSUBCODE ,tmp.S_BUDGETLEVELCODE , param.S_SUBJECTCODE"
					+ " from ( select * from TV_INFILE_TMP_COUNTRY where S_ORGCODE = ? and S_FILENAME = ? ) tmp left join TS_BUDGETSUBJECT param "
					+ " on tmp.S_ORGCODE = param.S_ORGCODE and tmp.S_BUDGETSUBCODE = param.S_SUBJECTCODE "
					+ " and param.S_MOVEFLAG = '0' and param.S_WRITEFLAG = '1' and param.S_SUBJECTCLASS = '1' "
//					+ " and (param.S_BUDGETTYPE = '1' or param.S_BUDGETTYPE = '3') "
					+ " and ((tmp.S_BUDGETLEVELCODE = '0' and param.S_SUBJECTATTR = '3') or (tmp.S_BUDGETLEVELCODE = '1' and param.S_SUBJECTATTR <> '2') "
					+ " or ((tmp.S_BUDGETLEVELCODE = '2' or tmp.S_BUDGETLEVELCODE = '3' or tmp.S_BUDGETLEVELCODE = '4' or tmp.S_BUDGETLEVELCODE = '5') "
					+ " and param.S_SUBJECTATTR <> '1'))";
				selectsqlExec.addParam(orgcode);
				selectsqlExec.addParam(filename);
				rs = selectsqlExec.runQueryCloseCon(nationSql);
			} else if (MsgConstant.MSG_SOURCE_PLACE.equals(csourceflag)) {
				String placeSql = "select tmp.S_BUDGETSUBJECTCODE ,tmp.S_BUDGETLEVELCODE , param.S_SUBJECTCODE"
					+ " from (select * from TV_INFILE_TMP_PLACE where S_OPERBATCH = ? ) tmp left join TS_BUDGETSUBJECT param "
					+ " on tmp.S_BUDGETSUBJECTCODE = param.S_SUBJECTCODE "
					+ " and param.S_ORGCODE = ? "
					+ " and param.S_MOVEFLAG = '0' and param.S_WRITEFLAG = '1' and param.S_SUBJECTCLASS = '1' "
//					+ " and (param.S_BUDGETTYPE = '1' or param.S_BUDGETTYPE = '3') " 
					+ " and ((tmp.S_BUDGETLEVELCODE = '0' and param.S_SUBJECTATTR = '3') or (tmp.S_BUDGETLEVELCODE = '1' and param.S_SUBJECTATTR <> '2') "
					+ " or ((tmp.S_BUDGETLEVELCODE = '2' or tmp.S_BUDGETLEVELCODE = '3' or tmp.S_BUDGETLEVELCODE = '4' or tmp.S_BUDGETLEVELCODE = '5') "
					+ " and param.S_SUBJECTATTR <> '1'))";
				selectsqlExec.addParam(s_operbatch);
				selectsqlExec.addParam(orgcode);
				rs = selectsqlExec.runQueryCloseCon(placeSql);
			} else if (MsgConstant.MSG_SOURCE_TIPS.equals(csourceflag)) {
				String tipsSql = "select tmp.S_BUDGETSUBCODE ,tmp.S_BUDGETLEVELCODE , param.S_SUBJECTCODE"
					+ " from (select * from TV_INFILE_TMP_TIPS where S_ORGCODE = ? and S_FILENAME = ?) tmp left join TS_BUDGETSUBJECT param "
					+ " on tmp.S_ORGCODE = param.S_ORGCODE and tmp.S_BUDGETSUBCODE = param.S_SUBJECTCODE "
					+ " and param.S_MOVEFLAG = '0' and param.S_WRITEFLAG = '1' and param.S_SUBJECTCLASS = '1' "
//					+ " and (param.S_BUDGETTYPE = '1' or param.S_BUDGETTYPE = '3') "
					+ " and ((tmp.S_BUDGETLEVELCODE = '0' and param.S_SUBJECTATTR = '3') or (tmp.S_BUDGETLEVELCODE = '1' and param.S_SUBJECTATTR <> '2') "
					+ " or ((tmp.S_BUDGETLEVELCODE = '2' or tmp.S_BUDGETLEVELCODE = '3' or tmp.S_BUDGETLEVELCODE = '4' or tmp.S_BUDGETLEVELCODE = '5') "
					+ " and param.S_SUBJECTATTR <> '1'))";
				selectsqlExec.addParam(orgcode);
				selectsqlExec.addParam(filename);
				rs = selectsqlExec.runQueryCloseCon(tipsSql);
			} else{
				logger.error("数据来源标志[" + csourceflag +"]错误!");
				throw new ITFEBizException("数据来源标志[" + csourceflag +"]错误!");
			}
			
			StringBuffer errinfo = new StringBuffer();
			boolean errflag = false;
			int count = rs.getRowCount();
			int sumcout = 0 ;
			for(int i = 0 ; i < count ; i++){
				String bdgsbtcode = rs.getString(i, 0);
				String bdglevel = rs.getString(i, 1);
				String param = rs.getString(i, 2);
				if(null == param){
					errflag = true;
					sumcout++;
					errinfo.append("预算科目 [" + bdgsbtcode + "] 与预算级次 [" + bdglevel +"] 校验错误!\r\n");
					if(sumcout > 20){
						throw new ITFEBizException(errinfo.toString());
					}
				}
			}
			
			if(errflag){
				throw new ITFEBizException(errinfo.toString());
			}
		}catch (JAFDatabaseException e) {
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
			
			logger.error("进行预算科目校验的时候出现异常!", e);
			throw new ITFEBizException("进行预算科目校验的时候出现异常!", e);
		}
	}
	
	/**
	 * 校验收款行行号是否存在
	 * 
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @throws ITFEBizException
	 */
	public static void verifyBankno(String orgcode, String filename) throws ITFEBizException {
		String selectBanknoSql = "select a.S_PAYEEBANKNO from "
			+"(select distinct S_PAYEEBANKNO from TV_PAYOUTMSGMAIN where S_ORGCODE = ? and S_FILENAME = ?) a"
			+ " where not exists (select b.S_BNKCODE from TS_BANKCODE b where b.S_BNKCODE = a.S_PAYEEBANKNO and b.S_ACCTSTATUS = '1')";

		SQLExecutor selectsqlExec = null;
		try {
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults banknoRs = selectsqlExec.runQueryCloseCon(selectBanknoSql);
			int assignCount = banknoRs.getRowCount();
			if(assignCount != 0){
				StringBuffer sbuf = new StringBuffer("以下收款行行号没有维护银行账号信息维护!");
				for(int i = 0 ; i < assignCount; i++){
					sbuf.append(banknoRs.getString(i, 0));
					sbuf.append("\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
		} catch (JAFDatabaseException e) {
			logger.error("收款行行号校验的时候出现异常!", e);
			throw new ITFEBizException("收款行行号校验的时候出现异常!", e);
		}
	}
	
	/**
	 * 校验国库级次与预算级次的关系（收入）
	 * 1：TCBS代码修改前：
	 * 国库级次为市级，预算级次只能为市、中央、共享；
	 * 国库级次为县级，预算级次只能为县、中央、共享。
	 * 2：TCBS代码修改后：
	 * 国库级次为市级，预算级次只能为市、广西加省、中央、共享；
	 * 国库级次为县级，预算级次只能为县、市、广西加省、中央、共享。
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            verifyFlag 校验标记
	 * @throws ITFEBizException
	 */
	public static void verifyTreasuryLevel(String orgcode, String filename, String verifyFlag) throws ITFEBizException {
		String selectTreLevel="";
		if("1".equals(verifyFlag)){//TCBS代码修改前
			selectTreLevel = "SELECT a.S_RECVTRECODE,a.S_TBS_TAXORGCODE,a.S_BUDGETLEVELCODE,b.S_TRELEVEL FROM TV_INFILE_TMP a,TS_TREASURY b"
								+ " WHERE a.S_ORGCODE = ? AND a.S_FILENAME = ? AND a.S_RECVTRECODE = b.S_TRECODE"
									+ " AND ((b.S_TRELEVEL='" + MsgConstant.BUDGET_LEVEL_PREFECTURE + "' AND a.S_BUDGETLEVELCODE NOT IN ('"+MsgConstant.BUDGET_LEVEL_SHARE+"','"+MsgConstant.BUDGET_LEVEL_CENTER+"','"+MsgConstant.BUDGET_LEVEL_DISTRICT+"','"+MsgConstant.BUDGET_LEVEL_PREFECTURE+"'))"
									+ " OR (b.S_TRELEVEL='" + MsgConstant.BUDGET_LEVEL_DISTRICT + "' AND a.S_BUDGETLEVELCODE NOT IN ('"+MsgConstant.BUDGET_LEVEL_SHARE+"','"+MsgConstant.BUDGET_LEVEL_CENTER+"','"+MsgConstant.BUDGET_LEVEL_DISTRICT+"')))";
		}else if("2".equals(verifyFlag)){//TCBS代码修改后
			selectTreLevel = "SELECT a.S_RECVTRECODE,a.S_TBS_TAXORGCODE,a.S_BUDGETLEVELCODE,b.S_TRELEVEL FROM TV_INFILE_TMP a,TS_TREASURY b"
				+ " WHERE a.S_ORGCODE = ? AND a.S_FILENAME = ? AND a.S_RECVTRECODE = b.S_TRECODE"
					+ " AND ((b.S_TRELEVEL='" + MsgConstant.BUDGET_LEVEL_DISTRICT + "' AND a.S_BUDGETLEVELCODE NOT IN ('"+MsgConstant.BUDGET_LEVEL_SHARE+"','"+(orgcode.startsWith("20")?MsgConstant.BUDGET_LEVEL_PROVINCE+"','":"")+MsgConstant.BUDGET_LEVEL_CENTER+"','"+MsgConstant.BUDGET_LEVEL_DISTRICT+"'))"
					+ " OR (b.S_TRELEVEL='" + MsgConstant.BUDGET_LEVEL_PREFECTURE + "' AND a.S_BUDGETLEVELCODE NOT IN ('"+MsgConstant.BUDGET_LEVEL_SHARE+"','"+(orgcode.startsWith("20")?MsgConstant.BUDGET_LEVEL_PROVINCE+"','":"")+MsgConstant.BUDGET_LEVEL_CENTER+"','"+MsgConstant.BUDGET_LEVEL_DISTRICT+"','"+MsgConstant.BUDGET_LEVEL_PREFECTURE+"')))";
		}else{
			return;
		}
		SQLExecutor selectsqlExec = null;
		try {
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults trelevelRs = selectsqlExec.runQueryCloseCon(selectTreLevel);
			int count = trelevelRs.getRowCount();
			if(count != 0){
				StringBuffer errinfo = new StringBuffer("以下国库级次与预算级次的对应关系错!\n");
				for(int i = 0 ; i < count; i++){
					String trecode = trelevelRs.getString(i, 0);
					String taxorgcode = trelevelRs.getString(i, 1);
					String budgetLevel = trelevelRs.getString(i, 2);
					errinfo.append("国库代码[" + trecode +"],征收机关 ["+ taxorgcode + "]与预算级次[" + budgetLevel +"]校验错误!\r\n");
				}
				
				throw new ITFEBizException(errinfo.toString());
			}
		} catch (JAFDatabaseException e) {
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
			logger.error(" 校验国库级次与预算级次关系的时候出现异常!", e);
			throw new ITFEBizException(" 校验国库级次与预算级次关系的时候出现异常!", e);
		}finally{
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
		}
	}
	/**
	 * 校验国库是否是机构可操作的国库。
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            verifyFlag 校验标记
	 * @throws ITFEBizException
	 */
	public static void verifyTreasury(String orgcode, String filename) throws ITFEBizException {
		if(!orgcode.startsWith("20"))
			return;
		String selectTreLevel="SELECT S_RECVTRECODE,S_DESCTRECODE FROM TV_INFILE_TMP where S_RECVTRECODE!=S_DESCTRECODE and s_orgcode=? and S_FILENAME=?";
		SQLExecutor selectsqlExec = null;
		try {
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults trelevelRs = selectsqlExec.runQueryCloseCon(selectTreLevel);
			int count = trelevelRs.getRowCount();
			if(count != 0){
				StringBuffer errinfo = new StringBuffer("以下收款国库和目的国库不一致!\n");
				for(int i = 0 ; i < count; i++){
					String trecode = trelevelRs.getString(i, 0);
					String desorgcode = trelevelRs.getString(i, 1);
					errinfo.append("收款国库代码目的国库代码不一致[" + trecode+"]["+desorgcode+"]校验错误!\n");
				}
				throw new ITFEBizException(errinfo.toString());
			}
		} catch (JAFDatabaseException e) {
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
			logger.error(" 校验收款国库代码目的国库代码不一致时出现异常!", e);
			throw new ITFEBizException(" 校验收款国库代码目的国库代码不一致时出现异常!", e);
		}finally{
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
		}
	}
	/**
	 * 校验国库级次与预算级次的关系（退库）
	 * 1：TCBS代码修改前：
	 * 国库级次为市级，预算级次只能为市、中央、共享；
	 * 国库级次为县级，预算级次只能为县、中央、共享。
	 * 2：TCBS代码修改后：
	 * 国库级次为市级，预算级次只能为市、中央、共享；
	 * 国库级次为县级，预算级次只能为县、市、中央、共享。
	 * @param String
	 *            trecode 国库代码
	 * @param String
	 *            trelevel 国库级次
	 * @param String
	 *            bdglevel 预算级次
	 * @throws ITFEBizException
	 */
	public static String verifyTreasuryLevelDW(String trecode, String trelevel, String bdglevel) throws ITFEBizException {
		String errorInfo="";
		//县库对应的预算级次列表
		List<String> provinceList = new ArrayList<String>();
		provinceList.add(MsgConstant.BUDGET_LEVEL_PREFECTURE);
		provinceList.add(MsgConstant.BUDGET_LEVEL_CENTER);
		provinceList.add(MsgConstant.BUDGET_LEVEL_SHARE);
		//市库对应的预算级次列表
		List<String> cityList = new ArrayList<String>();
		cityList.add(MsgConstant.BUDGET_LEVEL_DISTRICT);
		if("000077100005".equals(ITFECommonConstant.SRC_NODE))//广西校验省
			cityList.add(MsgConstant.BUDGET_LEVEL_PROVINCE);
		cityList.add(MsgConstant.BUDGET_LEVEL_CENTER);
		cityList.add(MsgConstant.BUDGET_LEVEL_SHARE);
		//县库对应的预算级次列表（TCBS代码修改后）
		List<String> provinceListA = new ArrayList<String>();
		provinceListA.add(MsgConstant.BUDGET_LEVEL_PREFECTURE);
		provinceListA.add(MsgConstant.BUDGET_LEVEL_DISTRICT);
		if("000077100005".equals(ITFECommonConstant.SRC_NODE))//广西校验省
			cityList.add(MsgConstant.BUDGET_LEVEL_PROVINCE);
		provinceListA.add(MsgConstant.BUDGET_LEVEL_CENTER);
		provinceListA.add(MsgConstant.BUDGET_LEVEL_SHARE);
		List<String> sbdglist = null;
		if("000077100005".equals(ITFECommonConstant.SRC_NODE))//广西校验省
		{
			sbdglist = new ArrayList<String>();
			sbdglist.add(MsgConstant.BUDGET_LEVEL_PROVINCE);
			sbdglist.add(MsgConstant.BUDGET_LEVEL_CENTER);
			sbdglist.add(MsgConstant.BUDGET_LEVEL_SHARE);
		}
		if("1".equals(ITFECommonConstant.IFVERIFYTRELEVEL)){
			if(MsgConstant.BUDGET_LEVEL_DISTRICT.equals(trelevel)){//市级
				if(!cityList.contains(bdglevel)){
					errorInfo = trecode+"_"+trelevel+"_"+bdglevel;
				}
			}else if(MsgConstant.BUDGET_LEVEL_PREFECTURE.equals(trelevel)){//县级
				if(!provinceList.contains(bdglevel)){
					errorInfo = trecode+"_"+trelevel+"_"+bdglevel;
				}
			}else if(MsgConstant.BUDGET_LEVEL_PROVINCE.equals(trelevel)&&sbdglist!=null&&sbdglist.size()>0)//省级
			{
				if(!sbdglist.contains(bdglevel)){
					errorInfo = trecode+"_"+trelevel+"_"+bdglevel;
				}
			}

		}else if("2".equals(ITFECommonConstant.IFVERIFYTRELEVEL)){
			if(MsgConstant.BUDGET_LEVEL_DISTRICT.equals(trelevel)){
				if(!cityList.contains(bdglevel)){
					errorInfo = trecode+"_"+trelevel+"_"+bdglevel;
				}
			}else if(MsgConstant.BUDGET_LEVEL_PREFECTURE.equals(trelevel)){
				if(!provinceListA.contains(bdglevel)){
					errorInfo = trecode+"_"+trelevel+"_"+bdglevel;
				}
			}else if(MsgConstant.BUDGET_LEVEL_PROVINCE.equals(trelevel)&&sbdglist!=null&&sbdglist.size()>0)//省级
			{
				if(!sbdglist.contains(bdglevel)){
					errorInfo = trecode+"_"+trelevel+"_"+bdglevel;
				}
			}
		}
		return errorInfo;
	}
	
	/**
	 * 判断字符串是否是数字组成
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str)
    {
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]*");
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false)
        {
           return false;
        }
        else
        {
           return true;
        }
    }
	
	/**
	 * 判断字段是否为空：如果为空返回true，否则返回false
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str)
    {
        if("".equals(str.trim())|| str==null)
           return true;
        else
           return false;
    }

	/**
	 * 校验传入的字符中是否含有生僻字等非法字符(' 可能需要单独校验，因为存不到数据库中)
	 * chiStr--要校验的字符串
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException 
	 */
	public static String verifyNotUsableChinese(String chiStr) throws ITFEBizException, JAFDatabaseException {
		HashMap<String,String> validateChineseMap = SrvCacheFacade.cacheNotUsableChinese();
		String errChi = "";
		for(int i  = 0 ; i < chiStr.length();i++) {
			Character chi = chiStr.charAt(i);
			if(validateChineseMap.containsKey(chi+"") || (chi+"").equals("'")) {
				if(i<(chiStr.length()-1)) {
					errChi += chi+"、";
				}else {
					errChi += chi;
				}
			}
		}
		if(errChi.endsWith("、")) {
			return errChi.substring(0, errChi.length()-1);
		}
		return errChi;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	/**
	 * 校验税票收入参数转化是否成功
	 * 校验字段：征收机关(S_TBS_TAXORGCODE)，辅助标志(S_TBS_ASSITSIGN)
	 * 
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @throws ITFEBizException
	 */
	public static void verifyTBSIncomeTransForZJ(String orgcode, String filename)
			throws ITFEBizException {
		// 先按照 [机构代码+文件名称] 做为原始条件(需要在参数表建立唯一索引或直接设置PK)
		String updateTaxSql = " update TV_INFILE_TMP a "
				+ " set a.S_TAXORGCODE = "
				+ "(select b.S_TCBSTAXORGCODE from TS_CONVERTTAXORG b where a.S_RECVTRECODE = b.S_TRECODE and a.S_ORGCODE = b.S_ORGCODE and a.S_TBS_TAXORGCODE = b.S_TBSTAXORGCODE ) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";

		String updateAssistSql1 = " update TV_INFILE_TMP a "
				+ " set a.S_ASSITSIGN = "
				+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and a.S_BUDGETSUBCODE = b.S_BUDGETSUBCODE) "
				+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? ";
		
		String updateAssistSql2 = " update TV_INFILE_TMP a "
			+ " set a.S_ASSITSIGN = "
			+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_ORGCODE = b.S_ORGCODE and a.S_TBS_ASSITSIGN = b.S_TBSASSITSIGN and b.S_BUDGETSUBCODE ='N' and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)) "
			+ " where a.S_ORGCODE = ? and a.S_FILENAME = ? and (a.S_ASSITSIGN ='' or a.S_ASSITSIGN is null)";
		
		String selectTaxSql = " select distinct S_RECVTRECODE , S_TBS_TAXORGCODE from TV_INFILE_TMP where S_ORGCODE = ? and S_FILENAME = ? and (S_TAXORGCODE is NULL)";
		
		SQLExecutor updatesqlExec = null;
		SQLExecutor selectsqlExec = null;
		try {
			// 第一步 先进行征收机关参数转化
			updatesqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateTaxSql);
			
			// 第二步 进行第一次辅助标志参数转化
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQuery(updateAssistSql1);
			
			// 第二步 进行第二次辅助标志参数转化
			updatesqlExec.clearParams();
			updatesqlExec.addParam(orgcode);
			updatesqlExec.addParam(filename);
			updatesqlExec.runQueryCloseCon(updateAssistSql2);
			
			// 第三步 判断征收机关参数转化是否成功,辅助标志不用做判断
			selectsqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			selectsqlExec.addParam(orgcode);
			selectsqlExec.addParam(filename);
			SQLResults rs = selectsqlExec.runQueryCloseCon(selectTaxSql);
			int count = rs.getRowCount();
			if(count == 0){
				return ;
			}else{
				StringBuffer sbuf = new StringBuffer("");
				for(int i = 0 ; i < count; i++){
					sbuf.append("国库 [")
					.append(rs.getString(i, 0))
					.append("] 对应的征收机关代码 [")
					.append(rs.getString(i, 1))
					.append("] 没有维护参数对照表!")
					.append("\r\n");
				}
				
				throw new ITFEBizException(sbuf.toString());
			}
		} catch (JAFDatabaseException e) {
			if(null != updatesqlExec){
				updatesqlExec.closeConnection();
			}
			
			if(null != selectsqlExec){
				selectsqlExec.closeConnection();
			}
			
			logger.error("进行收入税票参数转化的时候出现异常!", e);
			throw new ITFEBizException("进行收入税票参数转化的时候出现异常!", e);
		}
	}

}

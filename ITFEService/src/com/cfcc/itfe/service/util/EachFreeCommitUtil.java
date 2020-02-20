package com.cfcc.itfe.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TbsTvFreeDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 *免抵调：逐笔销号-确认提交
 */

public class EachFreeCommitUtil {
	private static Log logger = LogFactory.getLog(EachDwbkCommitUtil.class);

	/**
	 * 免抵调：逐笔销号-确认提交
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmFree(String bizType, IDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {

		String strVousrlno = String.valueOf(((TbsTvFreeDto) idto)
				.getIvousrlno());
		SQLExecutor sqlExec = null;
		try {
			/**
			 * 临时表写入正式表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String movedataSql = "INSERT INTO TV_FREE("
					+ "I_VOUSRLNO,S_TAXORGCODE,S_PACKNO,S_TRANO,D_BILLDATE,"
					+ "S_ELECTROTAXVOUNO,S_FREEVOUNO,C_FREEPLUTYPE,S_FREEPLUSUBJECTCODE,"
					+ "C_FREEPLULEVEL,S_TBSFREEPLUSIGN,S_FREEPLUSIGN,S_FREEPLUPTRECODE,F_FREEPLUAMT,"
					+ "C_FREEMIKIND,S_FREEMISUBJECT,C_FREEMILEVEL,S_TBSFREEMISIGN,S_FREEMISIGN,S_FREEMIPTRE,"
					+ "F_FREEMIAMT,S_CORPCODE,C_TRIMSIGN,S_STATUS,S_FILENAME,"
					+ "S_BOOKORGCODE,D_ACCEPTDATE,D_AUDITDATE,S_CHANNELCODE,"
					+ "S_ADDWORD,TS_SYSUPDATE)"
					+ "SELECT I_VOUSRLNO,S_TAXORGCODE,S_PACKNO,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),D_BILLDATE,"
					+ "S_ELECTROTAXVOUNO,S_FREEVOUNO,C_FREEPLUTYPE,S_FREEPLUSUBJECTCODE,"
					+ "C_FREEPLULEVEL,S_FREEPLUSIGN,'',S_FREEPLUPTRECODE,F_FREEPLUAMT,"
					+ "C_FREEMIKIND,S_FREEMISUBJECT,C_FREEMILEVEL,S_FREEMISIGN,'',S_FREEMIPTRE,"
					+ "F_FREEMIAMT,S_CORPCODE,C_TRIMFLAG,'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING + "',S_FILENAME,"
					+ "S_BOOKORGCODE,D_ACCEPTDATE,D_ACCTDATE,S_CHANNELCODE,"
					+ "S_ADDWORD,CURRENT TIMESTAMP "
					+ "FROM TBS_TV_FREE A WHERE I_VOUSRLNO= ? ";

			// 关键字：凭证流水号
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 更新辅助标志
			 */
			// 按国库代码和科目代码精确匹配
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql1 = " update TV_FREE a "
					+ " set a.S_FREEPLUSIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_FREEPLUPTRECODE = b.S_TRECODE and a.S_FREEPLUSUBJECTCODE = b.S_BUDGETSUBCODE and a.S_TBSFREEPLUSIGN = b.S_TBSASSITSIGN ), "
					+ "a.S_FREEMISIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_FREEMIPTRE = b.S_TRECODE and a.S_FREEMISUBJECT = b.S_BUDGETSUBCODE and a.S_TBSFREEMISIGN = b.S_TBSASSITSIGN ) "
					+ " where a.I_VOUSRLNO= ? ";
			// 关键字：凭证流水号
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(updateAssistSql1);

			/**
			 * 国库代码精确匹配、科目代码为N
			 */
			/**
			 * 免抵调增辅助标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql2 = " update TV_FREE a "
					+ " set a.S_FREEPLUSIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_FREEPLUPTRECODE = b.S_TRECODE and b.S_BUDGETSUBCODE =? and a.S_TBSFREEPLUSIGN = b.S_TBSASSITSIGN and (a.S_FREEPLUSIGN =? or a.S_FREEPLUSIGN is null)) "
					+ " where a.I_VOUSRLNO= ? and (a.S_FREEPLUSIGN =? or a.S_FREEPLUSIGN is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql2);

			/**
			 * 免抵调减辅助标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql2 = " update TV_FREE a "
					+ " set a.S_FREEMISIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_FREEMIPTRE = b.S_TRECODE and b.S_BUDGETSUBCODE =? and a.S_TBSFREEMISIGN = b.S_TBSASSITSIGN and (a.S_FREEMISIGN =? or a.S_FREEMISIGN is null)) "
					+ " where a.I_VOUSRLNO= ? and (a.S_FREEMISIGN =? or a.S_FREEMISIGN is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql2);

			/**
			 * 科目代码精确匹配、国库代码为N
			 */
			/**
			 * 免抵调增辅助标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql3 = " update TV_FREE a "
					+ " set a.S_FREEPLUSIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and a.S_FREEPLUSUBJECTCODE = b.S_BUDGETSUBCODE and a.S_TBSFREEPLUSIGN = b.S_TBSASSITSIGN and (a.S_FREEPLUSIGN =? or a.S_FREEPLUSIGN is null)) "
					+ " where a.I_VOUSRLNO= ? and (a.S_FREEPLUSIGN =? or a.S_FREEPLUSIGN is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql3);
			/**
			 * 免抵调减辅助标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql3 = " update TV_FREE a "
					+ " set a.S_FREEMISIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and a.S_FREEMISUBJECT = b.S_BUDGETSUBCODE and a.S_TBSFREEMISIGN = b.S_TBSASSITSIGN and (a.S_FREEMISIGN =? or a.S_FREEMISIGN is null)) "
					+ " where a.I_VOUSRLNO= ? and (a.S_FREEMISIGN =? or a.S_FREEMISIGN is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql3);

			/**
			 * 科目代码为N、国库代码为N
			 */
			/**
			 * 免抵调增辅助标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql4 = " update TV_FREE a "
					+ " set a.S_FREEPLUSIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and b.S_BUDGETSUBCODE = ? and a.S_TBSFREEPLUSIGN = b.S_TBSASSITSIGN and (a.S_FREEPLUSIGN =? or a.S_FREEPLUSIGN is null)) "
					+ " where a.I_VOUSRLNO= ? and (a.S_FREEPLUSIGN =? or a.S_FREEPLUSIGN is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql4);
			/**
			 * 免抵调减辅助标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql4 = " update TV_FREE a "
					+ " set a.S_FREEMISIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and b.S_BUDGETSUBCODE = ? and a.S_TBSFREEMISIGN = b.S_TBSASSITSIGN and (a.S_FREEMISIGN =? or a.S_FREEMISIGN is null)) "
					+ " where a.I_VOUSRLNO= ? and (a.S_FREEMISIGN =? or a.S_FREEMISIGN is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql4);
			/**
			 * 修改销号标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_FREE SET S_STATUS=? WHERE I_VOUSRLNO= ? ";
			// 关键字：凭证流水号
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			// 判断是否已经销号，并且是否可以发送
			sendMsgUtil.checkAndSendMsg((TbsTvFreeDto) idto,
					MsgConstant.MSG_NO_1106, TbsTvFreeDto.tableName(), null,
					loginfo);

		} catch (JAFDatabaseException e) {
			logger.error("数据库操作异常!", e);
			throw new ITFEBizException("数据库操作异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}
}
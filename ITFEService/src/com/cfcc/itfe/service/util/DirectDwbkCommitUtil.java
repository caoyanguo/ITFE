package com.cfcc.itfe.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author 曹艳国 退库直接提交
 * @time 12-02-21 08:45:49 
 */

public class  DirectDwbkCommitUtil{
	private static Log logger = LogFactory.getLog(DirectDwbkCommitUtil.class);
			
	/**
	 * 退库：直接提交
	 * 
	 * @throws ITFEBizException
	 */
	static boolean confirmRetTreasury(String bizType,ITFELoginInfo loginfo) throws ITFEBizException {
		boolean bResult;

		
		/**
		 * 判断是否维护退库原因代码对照
		 */
		bResult = CheckBizParam.checkDrawbackReason(null,loginfo);
		if (!bResult) {
			return false;
		}
		SQLExecutor sqlExec = null;
		try {
			/**
			 * 临时表写入正式表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String movedataSql="";
			if(!ITFECommonConstant.ISCONVERT.equals("0")){//征收机关代码、辅助标志做转换
				movedataSql = "INSERT INTO TV_DWBK("
					+ "I_VOUSRLNO,S_ELECVOUNO,S_DWBKVOUCODE,S_TAXORGCODE,S_AGENTTAXORGCODE,"
					+ "S_PAYERTRECODE,S_AIMTRECODE,C_BDGKIND,C_BDGLEVEL,S_BDGSBTCODE,"
					+ "S_DWBKREASONCODE,S_ASTFLAG,S_DWBKBY,F_DWBKRATIO,F_DWBKAMT,"
					+ "S_EXAMORG,F_AMT,C_BCKFLAG,S_PAYEECODE,S_PAYEEACCT,"
					+ "S_PAYEENAME,S_PAYEEOPNBNKNO,D_ACCEPT,D_ACCT,D_VOUCHER,"
					+ "D_BILL,S_ETPCODE,S_ECOCODE,C_TRIMFLAG,S_BIZTYPE,"
					+ "S_PACKAGENO,S_STATUS,S_DEALNO,S_FILENAME,S_BOOKORGCODE,TS_SYSUPDATE,S_TBSASTFLAG)"
					+ " SELECT "
					+ " I_VOUSRLNO,S_ELECVOUNO,S_DWBKVOUCODE,"
					+ "S_TAXORGCODE,S_AGENTTAXORGCODE,"
					+ " S_PAYERTRECODE,S_AIMTRECODE,C_BDGKIND,C_BDGLEVEL,S_BDGSBTCODE,"
					+ " (SELECT MAX(S_TCBSDRAWCODE) FROM TS_DRAWBACKREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE"
					+ "  AND S_TBSDRAWCODE=A.S_DWBKREASONCODE),"
					+ "'',"
					+ " S_DWBKBY,F_DWBKRATIO,F_DWBKAMT,"
					+ " S_EXAMORG,F_AMT,C_BCKFLAG,S_PAYEECODE,S_PAYEEACCT,"
					+ " S_PAYEENAME,S_PAYEEOPNBNKNO,D_ACCEPT,D_ACCT,D_VOUCHER,"
					+ " D_BILL,S_ETPCODE,S_ECOCODE,C_TRIMFLAG,S_BIZTYPE,"
					+ " S_PACKAGENO,'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "',substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),"
					+ "S_FILENAME,S_BOOKORGCODE,CURRENT TIMESTAMP,S_ASTFLAG "
					+ " FROM TBS_TV_DWBK A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
			}else{//征收机关代码、辅助标志不做转换
				movedataSql = "INSERT INTO TV_DWBK("
					+ "I_VOUSRLNO,S_ELECVOUNO,S_DWBKVOUCODE,S_TAXORGCODE,S_AGENTTAXORGCODE,"
					+ "S_PAYERTRECODE,S_AIMTRECODE,C_BDGKIND,C_BDGLEVEL,S_BDGSBTCODE,"
					+ "S_DWBKREASONCODE,S_ASTFLAG,S_DWBKBY,F_DWBKRATIO,F_DWBKAMT,"
					+ "S_EXAMORG,F_AMT,C_BCKFLAG,S_PAYEECODE,S_PAYEEACCT,"
					+ "S_PAYEENAME,S_PAYEEOPNBNKNO,D_ACCEPT,D_ACCT,D_VOUCHER,"
					+ "D_BILL,S_ETPCODE,S_ECOCODE,C_TRIMFLAG,S_BIZTYPE,"
					+ "S_PACKAGENO,S_STATUS,S_DEALNO,S_FILENAME,S_BOOKORGCODE,TS_SYSUPDATE,S_TBSASTFLAG)"
					+ " SELECT "
					+ " I_VOUSRLNO,S_ELECVOUNO,S_DWBKVOUCODE,"
					+ "S_TAXORGCODE,S_AGENTTAXORGCODE,"
					+ " S_PAYERTRECODE,S_AIMTRECODE,C_BDGKIND,C_BDGLEVEL,S_BDGSBTCODE,"
					+ " (SELECT MAX(S_TCBSDRAWCODE) FROM TS_DRAWBACKREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE"
					+ "  AND S_TBSDRAWCODE=A.S_DWBKREASONCODE),"
					+ " S_ASTFLAG,"
					+ " S_DWBKBY,F_DWBKRATIO,F_DWBKAMT,"
					+ " S_EXAMORG,F_AMT,C_BCKFLAG,S_PAYEECODE,S_PAYEEACCT,"
					+ " S_PAYEENAME,S_PAYEEOPNBNKNO,D_ACCEPT,D_ACCT,D_VOUCHER,"
					+ " D_BILL,S_ETPCODE,S_ECOCODE,C_TRIMFLAG,S_BIZTYPE,"
					+ " S_PACKAGENO,'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "',substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),"
					+ "S_FILENAME,S_BOOKORGCODE,CURRENT TIMESTAMP,S_ASTFLAG "
					+ " FROM TBS_TV_DWBK A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
			}
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号标志
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 更新辅助标志
			 */
			if(!ITFECommonConstant.ISCONVERT.equals("0")){//辅助标志做转换
			// 按国库代码和科目代码精确匹配
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql1 = " update TV_DWBK a "
					+ " set a.S_ASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_PAYERTRECODE = b.S_TRECODE and a.S_BDGSBTCODE = b.S_BUDGETSUBCODE and a.S_TBSASTFLAG = b.S_TBSASSITSIGN ) "
					+ " where a.S_BOOKORGCODE = ? and a.S_ASTFLAG =? ";
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql1);

			// 国库代码精确匹配、科目代码为N
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql2 = " update TV_DWBK a "
					+ " set a.S_ASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_PAYERTRECODE = b.S_TRECODE and b.S_BUDGETSUBCODE =? and a.S_TBSASTFLAG = b.S_TBSASSITSIGN and (a.S_ASTFLAG =? or a.S_ASTFLAG is null)) "
					+ " where a.S_BOOKORGCODE = ? and (a.S_ASTFLAG =? or a.S_ASTFLAG is null)";
			// 核算主体代码
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql2);

			// 科目代码精确匹配、国库代码为N
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql3 = " update TV_DWBK a "
					+ " set a.S_ASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and a.S_BDGSBTCODE = b.S_BUDGETSUBCODE and a.S_TBSASTFLAG = b.S_TBSASSITSIGN and (a.S_ASTFLAG =? or a.S_ASTFLAG is null)) "
					+ " where a.S_BOOKORGCODE = ? and (a.S_ASTFLAG =? or a.S_ASTFLAG is null)";
			// 核算主体代码
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql3);

			// 科目代码为N、国库代码为N
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql4 = " update TV_DWBK a "
					+ " set a.S_ASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and b.S_BUDGETSUBCODE = ? and a.S_TBSASTFLAG = b.S_TBSASSITSIGN and (a.S_ASTFLAG =? or a.S_ASTFLAG is null)) "
					+ " where a.S_BOOKORGCODE = ? and (a.S_ASTFLAG =? or a.S_ASTFLAG is null)";
			// 核算主体代码
			sqlExec.addParam("N");
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql4);
			}

			/**
			 * 修改销号标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_DWBK SET S_STATUS=? WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
			// 核算主体代码
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号标志
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 向TIPS发送报文
			 */

			// 按包流水号发送TIPS
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? "
					+ " AND S_CHKSTATE= ?  AND S_OPERATIONTYPECODE= ?";
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号标志
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			// 业务类型
			sqlExec.addParam(bizType);
			SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
			int row = rsfilepac.getRowCount();
			for (int i = 0; i < row; i++) {
				// // 按包流水号发送TIPS
				sendMsgUtil.sendMsg("", loginfo.getSorgcode(), rsfilepac
						.getString(i, 0), MsgConstant.MSG_NO_1104, rsfilepac
						.getString(i, 1), false);
			}
			/**
			 * 修改文件与包流水对应关系表(已发送，已销号)
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=?"
					+ " WHERE S_ORGCODE= ? AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? )  AND S_OPERATIONTYPECODE= ?";
			// 核算主体代码
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号标志
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			// 业务类型
			sqlExec.addParam(bizType);
			sqlExec.runQueryCloseCon(movedataSql);
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
package com.cfcc.itfe.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

public class CheckBizParam {
	private static Log logger = LogFactory.getLog(CheckBizParam.class);

	/**
	 * 判断是否维护财政机构信息
	 * 
	 * @param bizType
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean checkConvertFinOrg(String bizType, TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		boolean bresult = true;
		String selectSQL = "";
		String fileName = "";
		String treCode = "";
		if (null != idto) {
			fileName = idto.getSfilename();
			treCode = idto.getStrecode();
		}

		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// 直接支付
			if (fileName.equals("")) {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			}

		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// 授权支付
			if (fileName.equals("")) {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_GRANTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_GRANTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ?  AND S_TRECODE= ?"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			}
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// 实拨资金
			if (fileName.equals("")) {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_PAYOUT A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			} else {
//				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_PAYOUT A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ?  AND S_TRECODE= ?"
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_PAYOUT A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			}

		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// 批量拨付
			if (fileName.equals("")) {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TV_PAYOUTFINANCE A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TV_PAYOUTFINANCE A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			}

		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// 人行办理支付
			if (fileName.equals("")) {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_PBCPAY A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_PBCPAY A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			}

		} else if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {// 商行办理支付划款申请
			if (fileName.equals("")) {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_BNKPAY_MAIN A WHERE S_BOOKORGCODE= ? AND (S_STATE IS NULL OR S_STATE= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_BNKPAY_MAIN A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			}

		}else {
			return false;
		}

		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// 机构代码
			sqlExec.addParam(loginfo.getSorgcode());

			if (null == idto) {
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));// 未核销
			} else {
				if ((BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType))
						|| (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) || BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
						|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {
					sqlExec.addParam(fileName);// 导入文件名
				} else {
					sqlExec.addParam(fileName);// 导入文件名
					sqlExec.addParam(treCode);// 国库主体
				}

			}

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("文件[" + trasrlnoRs.getString(0, 1) + "]中的国库主体代码["
						+ trasrlnoRs.getString(0, 0) + "]未维护财政机构信息，不能确认提交!");
				throw new ITFEBizException("文件[" + trasrlnoRs.getString(0, 1)
						+ "]中的国库主体代码[" + trasrlnoRs.getString(0, 0)
						+ "]未维护财政机构信息，不能确认提交!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 判断是否维护银行代码与支付行号对应关系
	 * 
	 * @param bizType
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean checkGenbankandreckbank(String bizType, TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		boolean bresult = true;
		String selectSQL = "";
		String bankName = "";
		String fileName = null;
		String treCode = null;
		if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// 授权支付
			if (null==idto) {
				selectSQL = "SELECT DISTINCT S_BNKNO,S_FILENAME FROM TBS_TV_GRANTPAYPLAN_MAIN A "
						+ " WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_BNKNO)";
			} else {
				fileName=idto.getSfilename();
				treCode=idto.getStrecode();
				selectSQL = "SELECT DISTINCT S_BNKNO,S_FILENAME FROM TBS_TV_GRANTPAYPLAN_MAIN A "
						+ " WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?"
						+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_BNKNO)";

			}
			bankName = "经办银行代码";
		} else if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// 直接支付
			if (null==idto) {
				selectSQL = "SELECT DISTINCT S_PAYEEOPNBNKNO,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A "
						+ " WHERE S_TRECODE<>S_PAYEEOPNBNKNO AND S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO)";
			} else {
				fileName=idto.getSfilename();
				treCode=idto.getStrecode();
				selectSQL = "SELECT DISTINCT S_PAYEEOPNBNKNO,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A "
						+ " WHERE  S_TRECODE<>S_PAYEEOPNBNKNO AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?"
						+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO)";

			}
			bankName = "收款人开户行";
		} else
			return false;

		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// 机构代码
			sqlExec.addParam(loginfo.getSorgcode());

			if (null==idto) {
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));// 未核销
			} else {
				sqlExec.addParam(fileName);// 导入文件名
				sqlExec.addParam(treCode);//国库主体
			}

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("文件[" + trasrlnoRs.getString(0, 1) + "]中的"
						+ bankName + "[" + trasrlnoRs.getString(0, 0)
						+ "]未维护银行代码与支付行号对应关系，不能确认提交!");
				throw new ITFEBizException("文件[" + trasrlnoRs.getString(0, 1)
						+ "]中的" + bankName + "[" + trasrlnoRs.getString(0, 0)
						+ "]未维护银行代码与支付行号对应关系，不能确认提交!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 判断是否维护银行代码与支付行号对应关系
	 * 
	 * @param bizType
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean checkGenbankandreckbank(String bizType, IDto idto)
			throws ITFEBizException {
		boolean bresult = true;
		String selectSQL = "";
		String strVousrlno = "";
		String bankName = "";
		if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// 授权支付
			selectSQL = "SELECT DISTINCT S_BNKNO,S_FILENAME FROM TBS_TV_GRANTPAYPLAN_MAIN A "
					+ " WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_BNKNO)";
			strVousrlno = String.valueOf(((TbsTvGrantpayplanMainDto) idto)
					.getIvousrlno());
			bankName = "经办银行代码";
		} else if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// 直接支付
			selectSQL = "SELECT DISTINCT S_PAYEEOPNBNKNO,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A "
					+ " WHERE  S_TRECODE<>S_PAYEEOPNBNKNO AND I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO)";
			strVousrlno = String.valueOf(((TbsTvDirectpayplanMainDto) idto)
					.getIvousrlno());
			bankName = "收款人开户行";
		} else
			return false;

		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// 凭证流水号
			sqlExec.addParam(strVousrlno);
			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("凭证流水号为[" + strVousrlno + "]的记录中的" + bankName
						+ "[" + trasrlnoRs.getString(0, 0)
						+ "]未维护银行代码与支付行号对应关系，不能确认提交!");
				throw new ITFEBizException("凭证流水号为[" + strVousrlno + "]的记录中的"
						+ bankName + "[" + trasrlnoRs.getString(0, 0)
						+ "]未维护银行代码与支付行号对应关系，不能确认提交!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 判断是否维护财政机构信息
	 * 
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean checkConvertFinOrg(String bizType, IDto idto)
			throws ITFEBizException {
		boolean bresult = true;
		String selectSQL = "";
		String strVousrlno = "";
		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// 直接支付
			selectSQL = "SELECT DISTINCT S_TRECODE FROM TBS_TV_DIRECTPAYPLAN_MAIN A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			strVousrlno = String.valueOf(((TbsTvDirectpayplanMainDto) idto)
					.getIvousrlno());
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// 授权支付
			selectSQL = "SELECT DISTINCT S_TRECODE FROM TBS_TV_GRANTPAYPLAN_MAIN A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			strVousrlno = String.valueOf(((TbsTvGrantpayplanMainDto) idto)
					.getIvousrlno());
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)) {// 实拨资金
			selectSQL = "SELECT DISTINCT S_TRECODE FROM TBS_TV_PAYOUT A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			strVousrlno = String
					.valueOf(((TbsTvPayoutDto) idto).getIvousrlno());
		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// 批量拨付
			selectSQL = "SELECT DISTINCT S_TRECODE FROM TV_PAYOUTFINANCE A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			strVousrlno = String.valueOf(((TvPayoutfinanceDto) idto)
					.getIvousrlno());
		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// 人行办理直接支付
			selectSQL = "SELECT DISTINCT S_TRECODE FROM TBS_TV_PBCPAY A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			strVousrlno = String
					.valueOf(((TbsTvPbcpayDto) idto).getIvousrlno());
		} else if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {// 商行办理支付划款申请
			selectSQL = "SELECT DISTINCT S_TRECODE FROM TBS_TV_BNKPAY_MAIN A WHERE I_VOUSRLNO= ? "
				    + " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
		    strVousrlno = String
				.valueOf(((TbsTvBnkpayMainDto) idto).getIvousrlno());
	    } else
			return false;

		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			// 凭证流水号
			sqlExec.addParam(strVousrlno);

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("凭证流水号为[" + strVousrlno + "]的记录中的国库主体代码["
						+ trasrlnoRs.getString(0, 0) + "]未维护财政机构信息，不能确认提交!");
				throw new ITFEBizException("凭证流水号为[" + strVousrlno
						+ "]的记录中的国库主体代码[" + trasrlnoRs.getString(0, 0)
						+ "]未维护财政机构信息，不能确认提交!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 判断是否维护征收机关对照表
	 * 
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean checkConvertTaxOrg(String bizType, TvFilepackagerefDto idto,
			String sBeforeOrAfter, ITFELoginInfo loginfo)
			throws ITFEBizException {
		boolean bresult = true;
		String selectSQL = "";
		String sExpAdd = "";
		if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// 退库
			if (null==idto) {
				selectSQL = "SELECT DISTINCT S_TAXORGCODE,S_FILENAME FROM TBS_TV_DWBK A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
						+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_PAYERTRECODE AND S_TBSTAXORGCODE=A.S_TAXORGCODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_TAXORGCODE,S_FILENAME FROM TBS_TV_DWBK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
						+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_PAYERTRECODE AND S_TBSTAXORGCODE=A.S_TAXORGCODE)";
			}

		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)
				&& sBeforeOrAfter.equals("1")) {// 更正业务原征收机关代码
			if (null==idto) {
				selectSQL = "SELECT DISTINCT S_ORITAXORGCODE,S_FILENAME FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
						+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_ORIPAYEETRECODE AND S_TBSTAXORGCODE=A.S_ORITAXORGCODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_ORITAXORGCODE,S_FILENAME FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
						+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_ORIPAYEETRECODE AND S_TBSTAXORGCODE=A.S_ORITAXORGCODE)";
			}
			sExpAdd = "原";
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)
				&& sBeforeOrAfter.equals("2")) {// 更正业务现征收机关代码
			if (null==idto) {
				selectSQL = "SELECT DISTINCT S_CURTAXORGCODE,S_FILENAME FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
						+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_CURPAYEETRECODE AND S_TBSTAXORGCODE=A.S_CURTAXORGCODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_CURTAXORGCODE,S_FILENAME FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
						+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_CURPAYEETRECODE AND S_TBSTAXORGCODE=A.S_CURTAXORGCODE)";
			}
			sExpAdd = "现";
		} else
			return false;

		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// 机构代码
			sqlExec.addParam(loginfo.getSorgcode());
			if (null==idto) {
				// 销号标志
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));
			} else {
				// 导入文件名
				sqlExec.addParam(idto.getSfilename());
				//国库代码
//				sqlExec.addParam(idto.getStrecode());
			}

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("文件[" + trasrlnoRs.getString(0, 1) + "]中的"
						+ sExpAdd + "征收机关代码[" + trasrlnoRs.getString(0, 0)
						+ "]未维护征收机关对照，不能确认提交!");
				throw new ITFEBizException("文件[" + trasrlnoRs.getString(0, 1)
						+ "]中的征收机关代码[" + trasrlnoRs.getString(0, 0)
						+ "]未维护征收机关对照，不能确认提交!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 判断是否维护征收机关对照表
	 * 
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean checkConvertTaxOrg(String bizType, IDto idto,
			String sBeforeOrAfter) throws ITFEBizException {
		boolean bresult = true;
		String selectSQL = "";
		String strVousrlno = "";
		String sExpAdd = "";
		if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// 退库
			selectSQL = "SELECT DISTINCT S_TAXORGCODE FROM TBS_TV_DWBK A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_PAYERTRECODE AND S_TBSTAXORGCODE=A.S_TAXORGCODE)";
			strVousrlno = String.valueOf(((TbsTvDwbkDto) idto).getIvousrlno());
		} else if (BizTypeConstant.BIZ_TYPE_TAX_FREE.equals(bizType)) {// 免抵调
			selectSQL = "SELECT DISTINCT S_TAXORGCODE FROM TBS_TV_DWBK A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_PAYERTRECODE AND S_TBSTAXORGCODE=A.S_TAXORGCODE)";
			strVousrlno = String.valueOf(((TbsTvDwbkDto) idto).getIvousrlno());
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)
				&& sBeforeOrAfter.equals("1")) {// 更正业务判断原征收机关代码

			selectSQL = "SELECT DISTINCT S_ORITAXORGCODE FROM TBS_TV_IN_CORRHANDBOOK A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_ORIPAYEETRECODE AND S_TBSTAXORGCODE=A.S_ORITAXORGCODE)";
			strVousrlno = String.valueOf(((TbsTvInCorrhandbookDto) idto)
					.getIvousrlno());
			sExpAdd = "原";
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)
				&& sBeforeOrAfter.equals("2")) {// 更正业务判断现征收机关代码

			selectSQL = "SELECT DISTINCT S_CURTAXORGCODE FROM TBS_TV_IN_CORRHANDBOOK A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_CURPAYEETRECODE AND S_TBSTAXORGCODE=A.S_CURTAXORGCODE)";
			strVousrlno = String.valueOf(((TbsTvInCorrhandbookDto) idto)
					.getIvousrlno());
			sExpAdd = "现";
		} else
			return false;

		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			// 凭证流水号
			sqlExec.addParam(strVousrlno);

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("凭证流水号为[" + strVousrlno + "]的记录中的" + sExpAdd
						+ "征收机关代码[" + trasrlnoRs.getString(0, 0)
						+ "]未维护征收机关对照，不能确认提交!");
				throw new ITFEBizException("凭证流水号为[" + strVousrlno + "]的记录中的"
						+ sExpAdd + "征收机关代码[" + trasrlnoRs.getString(0, 0)
						+ "]未维护征收机关对照，不能确认提交!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 判断是否维护更正原因代码对照
	 * 
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean checkCorrReason(TvFilepackagerefDto idto, ITFELoginInfo loginfo)
			throws ITFEBizException {
		boolean bresult = true;
		String selectSQL;
		if (null==idto) {
			selectSQL = "SELECT DISTINCT S_REASONCODE,S_FILENAME FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? ) "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CORRREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE "
					+ " AND S_TBSCORRCODE=A.S_REASONCODE)";
		} else {
			selectSQL = "SELECT DISTINCT S_REASONCODE,S_FILENAME FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CORRREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE "
					+ " AND S_TBSCORRCODE=A.S_REASONCODE)";
		}

		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// 机构代码
			sqlExec.addParam(loginfo.getSorgcode());
			if (null==idto) {
				// 销号标志
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));
			} else {
				// 导入文件名
				sqlExec.addParam(idto.getSfilename());
				//国库代码
//				sqlExec.addParam(idto.getStrecode());
			}

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("文件[" + trasrlnoRs.getString(0, 1) + "]中的更正原因代码["
						+ trasrlnoRs.getString(0, 0) + "]未维护更正原因代码对照，不能确认提交!");
				throw new ITFEBizException("文件[" + trasrlnoRs.getString(0, 1)
						+ "]中的更正原因代码[" + trasrlnoRs.getString(0, 0)
						+ "]未维护更正原因代码对照，不能确认提交!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 判断是否维护更正原因代码对照
	 * 
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean checkCorrReason(IDto idto) throws ITFEBizException {
		boolean bresult = true;
		String strVousrlno = "";
		String selectSQL = "SELECT DISTINCT S_REASONCODE FROM TBS_TV_IN_CORRHANDBOOK A WHERE I_VOUSRLNO= ? "
				+ " AND NOT EXISTS (SELECT 1 FROM TS_CORRREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE "
				+ " AND S_TBSCORRCODE=A.S_REASONCODE)";
		strVousrlno = String.valueOf(((TbsTvInCorrhandbookDto) idto)
				.getIvousrlno());
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// 凭证流水号
			sqlExec.addParam(strVousrlno);

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("凭证流水号为[" + strVousrlno + "]的记录中的更正原因代码["
						+ trasrlnoRs.getString(0, 0) + "]未维护更正原因代码对照，不能确认提交!");
				throw new ITFEBizException("凭证流水号为[" + strVousrlno
						+ "]的记录中的更正原因代码[" + trasrlnoRs.getString(0, 0)
						+ "]未维护更正原因代码对照，不能确认提交!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 判断是否维护退库原因代码对照
	 * 
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean checkDrawbackReason(TvFilepackagerefDto idto, ITFELoginInfo loginfo)
			throws ITFEBizException {
		boolean bresult = true;
		String selectSQL;
		if (null==idto) {
			selectSQL = "SELECT DISTINCT S_DWBKREASONCODE,S_FILENAME FROM TBS_TV_DWBK A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
					+ " AND NOT EXISTS (SELECT 1 FROM TS_DRAWBACKREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE "
					+ " AND S_TBSDRAWCODE=A.S_DWBKREASONCODE)";
		} else {
			selectSQL = "SELECT DISTINCT S_DWBKREASONCODE,S_FILENAME FROM TBS_TV_DWBK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_DRAWBACKREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE "
					+ " AND S_TBSDRAWCODE=A.S_DWBKREASONCODE)";
		}

		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// 机构代码
			sqlExec.addParam(loginfo.getSorgcode());
			if (null==idto) {
				// 销号标志
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));
			} else {
				// 导入文件名
				sqlExec.addParam(idto.getSfilename());
				//国库主体
//				sqlExec.addParam(idto.getStrecode());
			}

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("文件[" + trasrlnoRs.getString(0, 1) + "]中的退库原因代码["
						+ trasrlnoRs.getString(0, 0) + "]未维护退库原因代码对照，不能确认提交!");
				throw new ITFEBizException("文件[" + trasrlnoRs.getString(0, 1)
						+ "]中的退库原因代码[" + trasrlnoRs.getString(0, 0)
						+ "]未维护退库原因代码对照，不能确认提交!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 判断是否维护退库原因代码对照
	 * 
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean checkDrawbackReason(IDto idto) throws ITFEBizException {
		boolean bresult = true;
		String strVousrlno = "";
		String selectSQL = "SELECT DISTINCT S_DWBKREASONCODE FROM TBS_TV_DWBK A WHERE I_VOUSRLNO= ? "
				+ " AND NOT EXISTS (SELECT 1 FROM TS_DRAWBACKREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE "
				+ " AND S_TBSDRAWCODE=A.S_DWBKREASONCODE)";
		strVousrlno = String.valueOf(((TbsTvDwbkDto) idto).getIvousrlno());
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// 凭证流水号
			sqlExec.addParam(strVousrlno);

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("凭证流水号为[" + strVousrlno + "]的记录中的退库原因代码["
						+ trasrlnoRs.getString(0, 0) + "]未维护退库原因代码对照，不能确认提交!");
				throw new ITFEBizException("凭证流水号为[" + strVousrlno
						+ "]的记录中的退库原因代码[" + trasrlnoRs.getString(0, 0)
						+ "]未维护退库原因代码对照，不能确认提交!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 返回业务名称
	 * 
	 * @param bizType
	 * @return
	 */
	public static String getBizname(String bizType) {
		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// 直接支付
			return "直接支付额度导入";
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// 授权支付
			return "直接支付额度导入";
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// 实拨资金
			return "实拨资金导入";
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)) {// 更正
			return "预算收入更正导入";
		} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// 退库
			return "退库业务导入";
		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// 批量拨付
			return "批量拨付";
		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// 人行办理支付
			return "人行办理直接支付";
		}else if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)) {// 商行办理支付划款申请
			return "商行办理支付划款申请";
		}else if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {// 商行办理支付划款申请退回
			return "商行办理支付划款申请退回";
		}
		return bizType;

	}

	/**
	 * 判断收款行行号是否为空
	 * @param bizType
	 * @param idto
	 * @param loginfo
	 * @return
	 * @throws ITFEBizException 
	 */
	static boolean checkPayeeBankno(String bizType, TvFilepackagerefDto idto, ITFELoginInfo loginfo) throws ITFEBizException{
		boolean bresult = true;
		String selectSQL = "SELECT * FROM TBS_TV_PAYOUT WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND (S_PAYEEBANKNO=? OR S_PAYEEOPNBNKNO IS NULL)";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// 机构代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(idto.getSfilename());
			sqlExec.addParam("");
			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("提交信息中存在银行行号没有补录的记录，不能确认提交!");
				throw new ITFEBizException("提交信息中存在银行行号没有补录的记录，不能确认提交!");
			}
			return bresult;
		}catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}
	
	/**
	 * 判断收款行行号是否为空
	 * @param bizType
	 * @param idto
	 * @param loginfo
	 * @return
	 * @throws ITFEBizException 
	 */
	static boolean checkPayeeBanknoForEach(String bizType, IDto idto, ITFELoginInfo loginfo) throws ITFEBizException{
		boolean bresult = true;
		String strVousrlno="";
		String selectSQL = "SELECT * FROM TBS_TV_PAYOUT WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND I_VOUSRLNO=? AND (S_PAYEEBANKNO=? OR S_PAYEEOPNBNKNO IS NULL)";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			strVousrlno = String.valueOf(((TbsTvPayoutDto) idto).getIvousrlno());
			// 机构代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(((TbsTvPayoutDto) idto).getSfilename());
			// 凭证流水号
			sqlExec.addParam(((TbsTvPayoutDto) idto).getIvousrlno());
			sqlExec.addParam("");
			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("凭证流水号为[" + strVousrlno + "]的收款行行号为空，不能确认提交!");
				throw new ITFEBizException("凭证流水号为[" + strVousrlno + "]的收款行行号为空，不能确认提交!");
			}
			return bresult;
		}catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}
}

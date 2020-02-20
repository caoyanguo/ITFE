package com.cfcc.itfe.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TbsTvFreeDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author朱国珍
 * @time 12-02-21 08:45:49 逐笔删除
 */

public class EachDeleteUtil {
	private static Log logger = LogFactory.getLog(EachDeleteUtil.class);

	/**
	 * 直接支付额度：逐笔销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deleteDircetPay(IDto idto, ITFELoginInfo loginfo)
			throws ITFEBizException {
		SQLExecutor sqlExec = null;
		try {
			TbsTvDirectpayplanMainDto dto = (TbsTvDirectpayplanMainDto) idto;
			/**
			 * 删除临时从表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_DIRECTPAYPLAN_SUB A WHERE EXISTS(SELECT 1 FROM TBS_TV_DIRECTPAYPLAN_MAIN "
					+ " WHERE I_VOUSRLNO=A.I_VOUSRLNO AND S_BOOKORGCODE= ? AND S_FILENAME= ? )";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 删除临时主表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TBS_TV_DIRECTPAYPLAN_MAIN WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? ";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? ";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
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

	/**
	 * 授权支付额度：逐笔销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deleteGrantPay(IDto idto, ITFELoginInfo loginfo)
			throws ITFEBizException {
		SQLExecutor sqlExec = null;
		try {
			TbsTvGrantpayplanMainDto dto = (TbsTvGrantpayplanMainDto) idto;
			/**
			 * 删除临时从表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_GRANTPAYPLAN_SUB A WHERE EXISTS(SELECT 1 FROM TBS_TV_GRANTPAYPLAN_MAIN "
					+ " WHERE I_VOUSRLNO=A.I_VOUSRLNO AND S_BOOKORGCODE= ? AND S_FILENAME= ? )";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 删除临时主表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TBS_TV_GRANTPAYPLAN_MAIN WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? ";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? ";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
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

	/**
	 * 实拨资金：逐笔销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deletePayout(IDto idto, ITFELoginInfo loginfo)
			throws ITFEBizException {
		SQLExecutor sqlExec = null;
		try {
			TbsTvPayoutDto dto = (TbsTvPayoutDto) idto;
			/**
			 * 删除临时表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_PAYOUT A WHERE I_VOUSRLNO= ? ";

			// 凭证流水号
			sqlExec.addParam(dto.getIvousrlno());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 更新文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
			// 凭证金额
			sqlExec.addParam(dto.getFamt());
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			// 包流水号
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.runQueryCloseCon(movedataSql);
			
			/**
			 * 删除文件与包对应关系表中总金额为0的记录
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM　TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? AND N_MONEY=? AND I_COUNT=?";
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			// 包流水号
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.addParam(0);
			sqlExec.addParam(0);
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

	/**
	 * 退库：逐笔销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deleteRetTreasury(IDto idto, ITFELoginInfo loginfo)
			throws ITFEBizException {
		SQLExecutor sqlExec = null;
		try {
			TbsTvDwbkDto dto = (TbsTvDwbkDto)idto;
			/**
			 * 删除临时表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_DWBK A WHERE I_VOUSRLNO= ? ";
			// 凭证流水号
			sqlExec.addParam(dto.getIvousrlno());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 更新文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
			// 凭证金额
			sqlExec.addParam(dto.getFamt());
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			// 包流水号
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.runQueryCloseCon(movedataSql);
			
			/**
			 * 删除文件与包对应关系表中总金额为0的记录
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM　TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? AND N_MONEY=? AND I_COUNT=?";
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			// 包流水号
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.addParam(0);
			sqlExec.addParam(0);
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
	/**
	 * 免抵调：逐笔销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deleteFree(IDto idto, ITFELoginInfo loginfo)
			throws ITFEBizException {
		SQLExecutor sqlExec = null;
		try {
			TbsTvFreeDto dto = (TbsTvFreeDto)idto;
			/**
			 * 删除临时表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_Free A WHERE I_VOUSRLNO= ? ";
			// 凭证流水号
			sqlExec.addParam(dto.getIvousrlno());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 更新文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
			// 凭证金额
			sqlExec.addParam(dto.getFfreepluamt().add(dto.getFfreemiamt()));
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			// 包流水号
			sqlExec.addParam(dto.getSpackno());
			sqlExec.runQueryCloseCon(movedataSql);
			
			/**
			 * 删除文件与包对应关系表中总金额为0的记录
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM　TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? AND N_MONEY=? AND I_COUNT=?";
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			// 包流水号
			sqlExec.addParam(dto.getSpackno());
			sqlExec.addParam(0);
			sqlExec.addParam(0);
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
	/**
	 * 更正：逐笔销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deleteCorrect(IDto idto, ITFELoginInfo loginfo)
			throws ITFEBizException {
		SQLExecutor sqlExec = null;
		try {
			TbsTvInCorrhandbookDto dto = (TbsTvInCorrhandbookDto)idto;
			/**
			 * 删除临时表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_IN_CORRHANDBOOK A WHERE I_VOUSRLNO= ? ";

			// 凭证流水号
			sqlExec.addParam(dto.getIvousrlno());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 更新文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
			// 凭证原发生金额
			sqlExec.addParam(dto.getForicorramt());
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			// 包流水号
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表中总金额为0的记录
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM　TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? AND N_MONEY=? AND I_COUNT=?";
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			// 包流水号
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.addParam(0);
			sqlExec.addParam(0);
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


	/**
	 * 人行办理直接支付：逐笔销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deletePbcPayout(IDto idto, ITFELoginInfo loginfo)
			throws ITFEBizException {
		SQLExecutor sqlExec = null;
		try {
			TbsTvPbcpayDto dto = (TbsTvPbcpayDto) idto;
			/**
			 * 删除临时表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_PBCPAY A WHERE I_VOUSRLNO= ? ";

			// 凭证流水号
			sqlExec.addParam(dto.getIvousrlno());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 更新文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
			// 凭证金额
			sqlExec.addParam(dto.getFamt());
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			// 包流水号
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.runQueryCloseCon(movedataSql);
			
			/**
			 * 删除文件与包对应关系表中总金额为0的记录
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM　TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? AND N_MONEY=? AND I_COUNT=?";
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(dto.getSfilename());
			// 包流水号
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.addParam(0);
			sqlExec.addParam(0);
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
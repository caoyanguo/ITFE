package com.cfcc.itfe.service.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import sun.security.action.GetLongAction;

import com.cfcc.itfe.config.BizConfigInfo;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author 曹艳国 实拨资金
 * @time 12-02-21 08:45:49 批量销号，逐笔销号，直接提交，一些判断等
 */

public class BatchDirectDeleteUtil {
	private static Log logger = LogFactory.getLog(BatchDirectDeleteUtil.class);

	/**
	 * 直接支付额度：批量销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deleteDircetPay(TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		SQLExecutor sqlExec = null;
		String fileName = idto.getSfilename();
		String streCode = idto.getStrecode();
		try {

			/**
			 * 删除临时从表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_DIRECTPAYPLAN_SUB A WHERE EXISTS(SELECT 1 FROM TBS_TV_DIRECTPAYPLAN_MAIN "
					+ " WHERE I_VOUSRLNO=A.I_VOUSRLNO AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? )";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 删除临时主表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TBS_TV_DIRECTPAYPLAN_MAIN WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? ";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 判断是否还有未销号数据 没有则直接发送报文
			 */
			sendMsgUtil.checkAndSendMsgForPayplan(MsgConstant.MSG_NO_5102,
					TbsTvDirectpayplanMainDto.tableName(),
					idto.getSpackageno(), loginfo,
					BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN);

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
	 * 授权支付额度：批量销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deleteGrantPay(TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		SQLExecutor sqlExec = null;
		String fileName = idto.getSfilename();
		String streCode = idto.getStrecode();
		try {

			/**
			 * 删除临时从表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_GRANTPAYPLAN_SUB A WHERE EXISTS(SELECT 1 FROM TBS_TV_GRANTPAYPLAN_MAIN "
					+ " WHERE I_VOUSRLNO=A.I_VOUSRLNO AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?)";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 删除临时主表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TBS_TV_GRANTPAYPLAN_MAIN WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 判断是否还有未销号数据 没有则直接发送报文
			 */
			sendMsgUtil.checkAndSendMsgForPayplan(MsgConstant.MSG_NO_5103,
					TbsTvGrantpayplanMainDto.tableName(), idto.getSpackageno(),
					loginfo, BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
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
	 * 实拨资金：批量销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deletePayout(TvFilepackagerefDto idto, ITFELoginInfo loginfo)
			throws ITFEBizException {
		String fileName = idto.getSfilename();
		String treCode = idto.getStrecode();
		SQLExecutor sqlExec = null;
		try {
			/**
			 * 删除临时表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

//			String movedataSql = "DELETE FROM TBS_TV_PAYOUT A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
			String movedataSql = "DELETE FROM TBS_TV_PAYOUT A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? ";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
//			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除正式子表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

//			movedataSql = "DELETE FROM TV_PAYOUTMSGSUB A WHERE EXISTS (SELECT 1 from TV_PAYOUTMSGMAIN WHERE S_BIZNO=A.S_BIZNO AND S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?)";
			movedataSql = "DELETE FROM TV_PAYOUTMSGSUB A WHERE EXISTS (SELECT 1 from TV_PAYOUTMSGMAIN WHERE S_BIZNO=A.S_BIZNO AND S_ORGCODE= ? AND S_FILENAME= ? )";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
//			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除正式主表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

//			movedataSql = "DELETE FROM TV_PAYOUTMSGMAIN Where S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
			movedataSql = "DELETE FROM TV_PAYOUTMSGMAIN Where S_ORGCODE= ? AND S_FILENAME= ? ";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
//			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

//			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ?  AND S_TRECODE= ?";
			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ? ";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
//			sqlExec.addParam(treCode);
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
	 * 退库：批量销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deleteRetTreasury(TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		String fileName = idto.getSfilename();
		String treCode = idto.getStrecode();
		SQLExecutor sqlExec = null;
		try {
			/**
			 * 删除临时表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			//String movedataSql = "DELETE FROM TBS_TV_DWBK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_PAYERTRECODE= ?";
			String movedataSql = "DELETE FROM TBS_TV_DWBK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			//sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除业务表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();

			//movedataSql = "DELETE FROM TV_DWBK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_PAYERTRECODE= ?";
			movedataSql = "DELETE FROM TV_DWBK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			//sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();

			//movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			//sqlExec.addParam(treCode);
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
	 * 免抵调：批量销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deleteFree(TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		String fileName = idto.getSfilename();
		String treCode = idto.getStrecode();
		SQLExecutor sqlExec = null;
		try {
			/**
			 * 删除临时表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_Free A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_FREEPLUPTRECODE= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除业务表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FREE A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_FREEPLUPTRECODE= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(treCode);
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
	 * 更正：批量销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deleteCorrect(TvFilepackagerefDto idto, ITFELoginInfo loginfo)
			throws ITFEBizException {
		String fileName = idto.getSfilename();
		String treCode = idto.getStrecode();
		SQLExecutor sqlExec = null;
		try {
			/**
			 * 删除临时表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_CURPAYEETRECODE= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除业务表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_CURPAYEETRECODE= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ?  AND S_TRECODE= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(treCode);
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
	 * 批量拨付：批量销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deleteBatch(TvFilepackagerefDto idto, ITFELoginInfo loginfo)
			throws ITFEBizException {
		SQLExecutor sqlExec = null;
		String fileName = idto.getSfilename();
		try {
			/**
			 * 删除临时表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TV_PAYOUTFINANCE A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? ";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ? ";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
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
	 * 人行办理直接支付：批量销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deletePbcPayout(TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		SQLExecutor sqlExec = null;
		String fileName = idto.getSfilename();
		String treCode = idto.getStrecode();
		try {
			/**
			 * 删除业务从表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TV_PBCPAY_SUB A WHERE EXISTS (SELECT 1 FROM TBS_TV_PBCPAY WHERE I_VOUSRLNO=A.I_VOUSRLNO "
					+ " AND S_BOOKORGCODE=? AND S_FILENAME= ? AND S_TRECODE= ?)";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除业务主表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_PBCPAY_MAIN A WHERE EXISTS (SELECT 1 FROM TBS_TV_PBCPAY WHERE I_VOUSRLNO=A.I_VOUSRLNO "
					+ " AND S_BOOKORGCODE=? AND S_FILENAME= ? AND S_TRECODE= ?)";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除临时表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TBS_TV_PBCPAY A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(treCode);
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
	 * 商行办理支付划款申请：批量销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deletePayreckBank(TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		SQLExecutor sqlExec = null;
		String fileName = idto.getSfilename();
		String streCode = idto.getStrecode();
		String pckano  = idto.getSpackageno();
		try {

			/**
			 * 删除临时从表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM tbs_tv_bnkpay_sub A WHERE EXISTS(SELECT 1 FROM tbs_tv_bnkpay_main "
					+ " WHERE I_VOUSRLNO=A.I_VOUSRLNO AND S_PACKNO = ? AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? )";

			//包流水号
			sqlExec.addParam(pckano);
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 删除临时主表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM tbs_tv_bnkpay_main WHERE  S_PACKNO = ? AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// 包流水号
			sqlExec.addParam(pckano);
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF WHERE S_PACKAGENO = ? AND S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? ";

			// 包流水号
			sqlExec.addParam(pckano);
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

//			/**
//			 * 判断是否还有未销号数据 没有则直接发送报文
//			 */
//			sendMsgUtil.checkAndSendMsgForPayplan(MsgConstant.APPLYPAY_DAORU,
//					TbsTvBnkpayMainDto.tableName(),
//					idto.getSpackageno(), loginfo,
//					BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);

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
	 * 商行办理支付划款申请退回：批量销号-删除
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean deletePayreckBackBank(TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		SQLExecutor sqlExec = null;
		String fileName = idto.getSfilename();
		String streCode = idto.getStrecode();
		String pckano  = idto.getSpackageno();
		try {

			/**
			 * 删除临时从表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM tbs_tv_bnkpay_sub A WHERE EXISTS(SELECT 1 FROM tbs_tv_bnkpay_main "
					+ " WHERE I_VOUSRLNO=A.I_VOUSRLNO AND S_PACKNO = ? AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? )";

			// 包流水号
			sqlExec.addParam(pckano);
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 删除临时主表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM tbs_tv_bnkpay_main WHERE S_PACKNO = ? AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// 包流水号
			sqlExec.addParam(pckano);
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 删除文件与包对应关系表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF WHERE S_PACKAGENO = ? AND S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? ";

			// 包流水号
			sqlExec.addParam(pckano);
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

//			/**
//			 * 判断是否还有未销号数据 没有则直接发送报文
//			 */
//			sendMsgUtil.checkAndSendMsgForPayplan(MsgConstant.APPLYPAY_DAORU,
//					TbsTvBnkpayMainDto.tableName(),
//					idto.getSpackageno(), loginfo,
//					BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);

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
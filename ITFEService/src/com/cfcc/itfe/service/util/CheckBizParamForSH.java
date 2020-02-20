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

public class CheckBizParamForSH {
	/**
	 * 为上海的补录行号校验单独做的一个类
	 */
	private static Log logger = LogFactory.getLog(CheckBizParamForSH.class);

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
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			boolean bresult = true;
			String selectSQL = "";
			String bankName = "";
			String fileName = null;
			String treCode = null;
			if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// 直接支付
				/**
				 * 首先判断是否还存在为补录行号的记录，有则直接抛出异常
				 */
				selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_DIRECTPAYPLAN_MAIN WHERE (S_PAYEEOPNBNKNO IS NULL OR S_PAYEEOPNBNKNO ='')"
				+ " and S_TRECODE<>S_PAYEEOPNBNKNO AND S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? ) and S_IFMATCH=? ";
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",""));// 未核销
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				if(null != idto) {
					selectSQL += " AND S_FILENAME= ? ";
					sqlExec.addParam(idto.getSfilename());
				}
				SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
				if (trasrlnoRs.getRowCount() > 0) {
					bresult = false;
					logger.error("文件[" + trasrlnoRs.getString(0, 0) + "]中存在未补录收款人开户行行号信息，不能确认提交!");
					throw new ITFEBizException("文件[" + trasrlnoRs.getString(0, 0) + "]中存在未补录收款人开户行行号信息，不能确认提交!");
				}
				sqlExec.clearParams();
				
				if (null==idto) {
					selectSQL = "SELECT DISTINCT S_PAYEEOPNBNKNO,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A "
							+ " WHERE S_TRECODE<>S_PAYEEOPNBNKNO AND S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? ) AND (S_IFMATCH IS NULL OR S_IFMATCH=?) "
							+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO) ";
				} else {
					fileName=idto.getSfilename();
					treCode=idto.getStrecode();
					selectSQL = "SELECT DISTINCT S_PAYEEOPNBNKNO,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A "
							+ " WHERE  S_TRECODE<>S_PAYEEOPNBNKNO AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? AND (S_IFMATCH IS NULL OR S_IFMATCH=?) "
							+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO) ";
				}
				bankName = "收款人开户行";
			} else {
				return false;
			}
			
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			// 机构代码
			sqlExec.addParam(loginfo.getSorgcode());

			if (null==idto) {
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));// 未核销
			} else {
				sqlExec.addParam(fileName);// 导入文件名
				sqlExec.addParam(treCode);//国库主体
			}
			sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_NO);
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
		SQLExecutor sqlExec = null;
		String selectSQL = "";
		String strVousrlno = "";
		String bankName = "";
		try {
			if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// 授权支付
				selectSQL = "SELECT DISTINCT S_BNKNO,S_FILENAME FROM TBS_TV_GRANTPAYPLAN_MAIN A "
						+ " WHERE I_VOUSRLNO= ? AND (S_IFMATCH IS NULL OR S_IFMATCH=?) "
						+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_BNKNO)";
				strVousrlno = String.valueOf(((TbsTvGrantpayplanMainDto) idto)
						.getIvousrlno());
				bankName = "经办银行代码";
			} else if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// 直接支付
				strVousrlno = String.valueOf(((TbsTvDirectpayplanMainDto) idto)
						.getIvousrlno());
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				/**
				 * 首先判断是否还存在为补录行号的记录，有则直接抛出异常
				 */
				selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_DIRECTPAYPLAN_MAIN WHERE (S_PAYEEOPNBNKNO IS NULL OR S_PAYEEOPNBNKNO ='')"
				+ " and S_TRECODE<>S_PAYEEOPNBNKNO AND I_VOUSRLNO= ? AND (S_STATUS IS NULL OR S_STATUS= ? ) and S_IFMATCH=? ";
				sqlExec.addParam(strVousrlno);
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",""));// 未核销
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
				if (trasrlnoRs.getRowCount() > 0) {
					bresult = false;
					logger.error("文件[" + trasrlnoRs.getString(0, 0) + "]中存在未补录收款人开户行行号信息，不能确认提交!");
					throw new ITFEBizException("文件[" + trasrlnoRs.getString(0, 0) + "]中存在未补录收款人开户行行号信息，不能确认提交!");
				}
				sqlExec.clearParams();
				
				selectSQL = "SELECT DISTINCT S_PAYEEOPNBNKNO,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A "
						+ " WHERE  S_TRECODE<>S_PAYEEOPNBNKNO AND I_VOUSRLNO= ? AND (S_IFMATCH IS NULL OR S_IFMATCH=?) "
						+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO)";
				
				bankName = "收款人开户行";
			} else
				return false;
		
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// 凭证流水号
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_NO);
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
	 * 增加单独的方法来校验是否有未补录行号记录存在
	 * @return
	 */
	static boolean checkIsMatchNameOver(String bizType, TvFilepackagerefDto idto,ITFELoginInfo loginfo) throws ITFEBizException{
		SQLExecutor sqlExec = null;
		String selectSQL = "";
		boolean bresult = true;
		try {
			if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
					||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)) {
				// 商行办理支付划款申请
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				/**
				 * 首先判断是否还存在为补录行号的记录，有则直接抛出异常
				 */
				if(null != idto) {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEEOPNBNKNO IS NULL OR S_PAYEEOPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? and s_filename=? ";
				}else {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEEOPNBNKNO IS NULL OR S_PAYEEOPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? ";
				}
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",""));// 未核销
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				if(null != idto) {
					sqlExec.addParam(idto.getSfilename());
				}
	 			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
				if (trasrlnoRs.getRowCount() > 0) {
					bresult = false;
					logger.error("文件[" + trasrlnoRs.getString(0, 0) + "]中存在未补录收款人开户行行号信息，不能确认提交!");
					throw new ITFEBizException("文件[" + trasrlnoRs.getString(0, 0) + "]中存在未补录收款人开户行行号信息，不能确认提交!");
				}
			}else if(bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
					||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {
				//商行划款申请退回
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				/**
				 * 首先判断是否还存在为补录行号的记录，有则直接抛出异常
				 */
				if(null != idto) {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEROPNBNKNO IS NULL OR S_PAYEROPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? and s_filename=? ";
				}else {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEROPNBNKNO IS NULL OR S_PAYEROPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? ";
				}
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",""));// 未核销
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				if(null != idto) {
					sqlExec.addParam(idto.getSfilename());
				}
	 			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
				if (trasrlnoRs.getRowCount() > 0) {
					bresult = false;
					logger.error("文件[" + trasrlnoRs.getString(0, 0) + "]中存在未补录付款人开户行行号信息，不能确认提交!");
					throw new ITFEBizException("文件[" + trasrlnoRs.getString(0, 0) + "]中存在未补录付款人开户行行号信息，不能确认提交!");
				}
			}
		} 
		catch (JAFDatabaseException e) {
			logger.error("数据提交出现异常!", e);
			throw new ITFEBizException("数据提交出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		
		return bresult;
	}

	/**
	 * 为逐笔销号提供判断是否有未补录行号信息
	 * @param bizType
	 * @param idto
	 * @return
	 * @throws ITFEBizException
	 */
	public static boolean checkIsMatchNameOverEach(String bizType, IDto idto,ITFELoginInfo loginfo) throws ITFEBizException {
		SQLExecutor sqlExec = null;
		String selectSQL = "";
		boolean bresult = true;
		try {
			if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
					||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)
					) {
				// 商行办理支付划款申请
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				/**
				 * 首先判断是否还存在为补录行号的记录，有则直接抛出异常
				 */
				if(null != idto) {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEEOPNBNKNO IS NULL OR S_PAYEEOPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? and I_VOUSRLNO=? ";
				}else {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEEOPNBNKNO IS NULL OR S_PAYEEOPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? ";
				}
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",""));// 未核销
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				if(null != idto) {
					sqlExec.addParam(((TbsTvBnkpayMainDto)idto).getIvousrlno());
				}
	 			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
				if (trasrlnoRs.getRowCount() > 0) {
					bresult = false;
					logger.error("文件[" + trasrlnoRs.getString(0, 0) + "]中存在未补录收款人开户行行号信息，不能确认提交!");
					throw new ITFEBizException("文件[" + trasrlnoRs.getString(0, 0) + "]中存在未补录收款人开户行行号信息，不能确认提交!");
				}
			}else if(bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
					||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {
				// 商行办理支付划款申请退回
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				/**
				 * 首先判断是否还存在为补录行号的记录，有则直接抛出异常
				 */
				if(null != idto) {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEROPNBNKNO IS NULL OR S_PAYEROPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? and I_VOUSRLNO=? ";
				}else {
					selectSQL = "SELECT S_FILENAME,S_PAYEEOPNBNKNO FROM TBS_TV_BNKPAY_MAIN WHERE (S_PAYEROPNBNKNO IS NULL OR S_PAYEROPNBNKNO ='') 	"
						+ "  AND (S_STATE IS NULL OR S_STATE= ? ) and S_BOOKORGCODE=? and S_IFMATCH=? ";
				}
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",""));// 未核销
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				if(null != idto) {
					sqlExec.addParam(((TbsTvBnkpayMainDto)idto).getIvousrlno());
				}
	 			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
				if (trasrlnoRs.getRowCount() > 0) {
					bresult = false;
					logger.error("文件[" + trasrlnoRs.getString(0, 0) + "]中存在未补录付款人开户行行号信息，不能确认提交!");
					throw new ITFEBizException("文件[" + trasrlnoRs.getString(0, 0) + "]中存在未补录付款人开户行行号信息，不能确认提交!");
				}
			}
		} 
		catch (JAFDatabaseException e) {
			logger.error("数据提交出现异常!", e);
			throw new ITFEBizException("数据提交出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return bresult;
	}
}

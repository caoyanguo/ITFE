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

public class CheckIfConfirm {
	private static Log logger = LogFactory.getLog(CheckIfConfirm.class);

	/**
	 * 判断文件名与包对应关系中是否已销号
	 * 
	 * @param bizType
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	public static boolean ifConfirm(String bizType, TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		boolean bResult = true;
		String selectSQL = "";
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// 实拨资金有两个业务类型
			if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
					||(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY))
					||(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK))) {
				if (null==idto) {
					selectSQL = "SELECT 1 FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_CHKSTATE= ? "
							+ " AND (S_OPERATIONTYPECODE= ? OR S_OPERATIONTYPECODE= ? )";
				} else {
					selectSQL = "SELECT 1 FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_CHKSTATE= ? "
	//						+ " AND (S_OPERATIONTYPECODE= ? OR S_OPERATIONTYPECODE= ? ) AND S_FILENAME= ? AND S_TRECODE= ?";
							+ " AND (S_OPERATIONTYPECODE= ? OR S_OPERATIONTYPECODE= ? ) AND S_FILENAME= ? "+("".equals(idto.getStrecode())?"":" and S_TRECODE='"+idto.getStrecode()+"'");
				}
				
			} else {
				if (null==idto) {
					selectSQL = "SELECT 1 FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_CHKSTATE= ? "
							+ " AND S_OPERATIONTYPECODE= ? ";
				} else {
					selectSQL = "SELECT 1 FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_CHKSTATE= ? "
							+ " AND S_OPERATIONTYPECODE= ? AND S_FILENAME= ?  AND S_TRECODE= ?";
				}
				
			}
			// 机构代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号状态
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES
					.replaceAll("\"", ""));
			
			if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)) {
				// 业务类型（实拨资金有两个业务类型）
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAY_OUT);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAY_OUT2);
			} else if (bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)) {
				// 业务类型（划款申请有两个业务类型）
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY);
			}else if (bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {
				// 业务类型（划款申请退回有两个业务类型）
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK);
			}else {
				// 业务类型
				sqlExec.addParam(bizType);
			}
			if (null!=idto) {
				// 导入文件名
				sqlExec.addParam(idto.getSfilename());
				if (!BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)&&!(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY))
						&&!(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK))) {
					//国库主体
					sqlExec.addParam(idto.getStrecode());
				}				
			}
			if (null!=idto.getSpackageno()) {
				selectSQL = selectSQL +" AND S_PACKAGENO = ? ";
				//包流水号
				sqlExec.addParam(idto.getSpackageno());
			}
			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			bResult = (trasrlnoRs.getRowCount() > 0);

			return bResult;
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
	 * 判断临时表是否已销号
	 * 
	 * @param bizType
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean ifEachConfirm(String bizType, IDto idto)
			throws ITFEBizException {
		boolean bResult = false;
		try {
			if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// 直接支付
				TbsTvDirectpayplanMainDto dirpayDto = (TbsTvDirectpayplanMainDto) DatabaseFacade
						.getDb().find(idto);
				if (dirpayDto != null
						&& StateConstant.CONFIRMSTATE_YES.equals(dirpayDto
								.getSstatus()))
					bResult = true;
			} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// 授权支付
				TbsTvGrantpayplanMainDto grantpayDto = (TbsTvGrantpayplanMainDto) DatabaseFacade
						.getDb().find(idto);
				if (grantpayDto != null
						&& StateConstant.CONFIRMSTATE_YES.equals(grantpayDto
								.getSstatus()))
					bResult = true;

			} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
					|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// 实拨资金
				TbsTvPayoutDto payoutDto = (TbsTvPayoutDto) DatabaseFacade
						.getDb().find(idto);
				if (payoutDto != null
						&& StateConstant.CONFIRMSTATE_YES.equals(payoutDto
								.getSstatus()))
					bResult = true;

			} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)) {// 更正
				TbsTvInCorrhandbookDto corrhandbookDto = (TbsTvInCorrhandbookDto) DatabaseFacade
						.getDb().find(idto);
				if (corrhandbookDto != null
						&& StateConstant.CONFIRMSTATE_YES
								.equals(corrhandbookDto.getSstatus()))
					bResult = true;

			} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// 退库
				TbsTvDwbkDto dwdbDto = (TbsTvDwbkDto) DatabaseFacade.getDb()
						.find(idto);
				if (dwdbDto != null
						&& StateConstant.CONFIRMSTATE_YES.equals(dwdbDto
								.getSstatus()))
					bResult = true;

			} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// 批量拨付
				TvPayoutfinanceDto payoutFinDto = (TvPayoutfinanceDto) DatabaseFacade
						.getDb().find(idto);
				if (payoutFinDto != null
						&& StateConstant.CONFIRMSTATE_YES.equals(payoutFinDto
								.getSstatus()))
					bResult = true;
			} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// 人行直接支付
				TbsTvPbcpayDto pbcpayoutDto = (TbsTvPbcpayDto) DatabaseFacade
						.getDb().find(idto);
				if (pbcpayoutDto != null
						&& StateConstant.CONFIRMSTATE_YES.equals(pbcpayoutDto
								.getSstatus()))
					bResult = true;
			}else if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
					||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)
					||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
					||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {//商行办理支付划款申请
				TbsTvBnkpayMainDto bnkpayDto = (TbsTvBnkpayMainDto) DatabaseFacade
				        .getDb().find(idto);
		        if (bnkpayDto != null
				        && StateConstant.CONFIRMSTATE_YES.equals(bnkpayDto
						        .getSstate()))
			        bResult = true;
	        }
			return bResult;
		} catch (JAFDatabaseException e) {
			logger.error("判断临时表是否已销号时出现异常!", e);
			throw new ITFEBizException("判断临时表是否已销号时出现异常!", e);
		}
	}
	/**
	 * 判断文件名与包对应关系中是否已销号
	 * 
	 * @param bizType90
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	static boolean ifDirectConfirm(String bizType,ITFELoginInfo loginfo) throws ITFEBizException {
		boolean bResult = true;
		String selectSQL = "";
		// 实拨资金有两个业务类型、商行划款申请（退回）有两个业务类型
		if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				||bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)||bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)
				||bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)||bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)
				||bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)
				||bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)
				) {
			selectSQL = "SELECT 1 FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? )"
					+ "AND (S_OPERATIONTYPECODE= ? OR S_OPERATIONTYPECODE= ? )";

		} else {
			selectSQL = "SELECT 1 FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? )"
					+ "AND S_OPERATIONTYPECODE= ? ";
		}
		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// 机构代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号状态
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)) {
				// 业务类型（实拨资金有两个业务类型）
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAY_OUT);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAY_OUT2);
			}else if (bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)|| bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)
					|| bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)) {
				// 业务类型（划款申请有两个业务类型）
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY);
			}else if (bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)||bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)
		            ||bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {
				// 业务类型（划款申请退回有两个业务类型）
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK);
			}else {
				// 业务类型
				sqlExec.addParam(bizType);
			}

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			bResult = !(trasrlnoRs.getRowCount() > 0);

			return bResult;
		} catch (JAFDatabaseException e) {
			logger.error("查询数据的时候出现异常!", e);
			throw new ITFEBizException("查询数据的时候出现异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

}

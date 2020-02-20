package com.cfcc.itfe.twcs;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class updateInfoFacade {
	private static Log logger = LogFactory.getLog(updateInfoFacade.class);
	/**
	 * 根据TCBS返回报文更新凭证状态
	 * 
	 */
	public static void VoucherReceiveTWCS(String DealNo, String sstatus, String ls_Description)
			throws Exception {
		SQLExecutor updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
				.getSQLExecutor();
		String updateVoucherSql = "update " + TvVoucherinfoDto.tableName()
					+ " set S_STATUS = ? , S_DEMO = ? "
					+ " where S_DEALNO = ? and S_STATUS IN (?,?,?,?)";
		
		try {
			if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(sstatus)) {
				updateExce.addParam(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				updateExce.addParam("处理成功");
			} else {
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TCBS);
				updateExce.addParam(ls_Description);
			}
			    updateExce.addParam(DealNo);
				updateExce.addParam(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS); //校验成功
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED); //已发送
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE); //已收妥
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TIPS);//交易重复或者tips处理失败
				updateExce.addParam(DealCodeConstants.VOUCHER_REGULATORY_AUDITING);//监管中
			
			updateExce.runQueryCloseCon(updateVoucherSql);
			logger
					.debug("===============================凭证接收TWCSs回执报文更新凭证状态成功===============================");

		} catch (Exception e) {
			try {
				logger.error(e);
				VoucherException.saveErrInfo(DealNo, e);
			} catch (Exception e1) {
				logger.error(e1);
				VoucherException.saveErrInfo(null, e1);
			}
		} finally {
			if (updateExce != null) {
				updateExce.closeConnection();
			}
		}

	}

}

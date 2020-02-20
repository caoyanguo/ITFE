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
	 * ����TCBS���ر��ĸ���ƾ֤״̬
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
				updateExce.addParam("����ɹ�");
			} else {
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TCBS);
				updateExce.addParam(ls_Description);
			}
			    updateExce.addParam(DealNo);
				updateExce.addParam(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS); //У��ɹ�
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED); //�ѷ���
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE); //������
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TIPS);//�����ظ�����tips����ʧ��
				updateExce.addParam(DealCodeConstants.VOUCHER_REGULATORY_AUDITING);//�����
			
			updateExce.runQueryCloseCon(updateVoucherSql);
			logger
					.debug("===============================ƾ֤����TWCSs��ִ���ĸ���ƾ֤״̬�ɹ�===============================");

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

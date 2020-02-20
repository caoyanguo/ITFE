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
	 * �ж��Ƿ�ά������������Ϣ
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

		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// ֱ��֧��
			if (fileName.equals("")) {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			}

		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// ��Ȩ֧��
			if (fileName.equals("")) {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_GRANTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_GRANTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ?  AND S_TRECODE= ?"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			}
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// ʵ���ʽ�
			if (fileName.equals("")) {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_PAYOUT A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			} else {
//				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_PAYOUT A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ?  AND S_TRECODE= ?"
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TBS_TV_PAYOUT A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			}

		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// ��������
			if (fileName.equals("")) {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TV_PAYOUTFINANCE A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_TRECODE,S_FILENAME FROM TV_PAYOUTFINANCE A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			}

		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// ���а���֧��
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
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {// ���а���֧����������
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
			// ��������
			sqlExec.addParam(loginfo.getSorgcode());

			if (null == idto) {
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));// δ����
			} else {
				if ((BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType))
						|| (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) || BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
						|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {
					sqlExec.addParam(fileName);// �����ļ���
				} else {
					sqlExec.addParam(fileName);// �����ļ���
					sqlExec.addParam(treCode);// ��������
				}

			}

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("�ļ�[" + trasrlnoRs.getString(0, 1) + "]�еĹ����������["
						+ trasrlnoRs.getString(0, 0) + "]δά������������Ϣ������ȷ���ύ!");
				throw new ITFEBizException("�ļ�[" + trasrlnoRs.getString(0, 1)
						+ "]�еĹ����������[" + trasrlnoRs.getString(0, 0)
						+ "]δά������������Ϣ������ȷ���ύ!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * �ж��Ƿ�ά�����д�����֧���кŶ�Ӧ��ϵ
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
		if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// ��Ȩ֧��
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
			bankName = "�������д���";
		} else if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// ֱ��֧��
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
			bankName = "�տ��˿�����";
		} else
			return false;

		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// ��������
			sqlExec.addParam(loginfo.getSorgcode());

			if (null==idto) {
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));// δ����
			} else {
				sqlExec.addParam(fileName);// �����ļ���
				sqlExec.addParam(treCode);//��������
			}

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("�ļ�[" + trasrlnoRs.getString(0, 1) + "]�е�"
						+ bankName + "[" + trasrlnoRs.getString(0, 0)
						+ "]δά�����д�����֧���кŶ�Ӧ��ϵ������ȷ���ύ!");
				throw new ITFEBizException("�ļ�[" + trasrlnoRs.getString(0, 1)
						+ "]�е�" + bankName + "[" + trasrlnoRs.getString(0, 0)
						+ "]δά�����д�����֧���кŶ�Ӧ��ϵ������ȷ���ύ!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * �ж��Ƿ�ά�����д�����֧���кŶ�Ӧ��ϵ
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
		if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// ��Ȩ֧��
			selectSQL = "SELECT DISTINCT S_BNKNO,S_FILENAME FROM TBS_TV_GRANTPAYPLAN_MAIN A "
					+ " WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_BNKNO)";
			strVousrlno = String.valueOf(((TbsTvGrantpayplanMainDto) idto)
					.getIvousrlno());
			bankName = "�������д���";
		} else if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// ֱ��֧��
			selectSQL = "SELECT DISTINCT S_PAYEEOPNBNKNO,S_FILENAME FROM TBS_TV_DIRECTPAYPLAN_MAIN A "
					+ " WHERE  S_TRECODE<>S_PAYEEOPNBNKNO AND I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO)";
			strVousrlno = String.valueOf(((TbsTvDirectpayplanMainDto) idto)
					.getIvousrlno());
			bankName = "�տ��˿�����";
		} else
			return false;

		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// ƾ֤��ˮ��
			sqlExec.addParam(strVousrlno);
			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("ƾ֤��ˮ��Ϊ[" + strVousrlno + "]�ļ�¼�е�" + bankName
						+ "[" + trasrlnoRs.getString(0, 0)
						+ "]δά�����д�����֧���кŶ�Ӧ��ϵ������ȷ���ύ!");
				throw new ITFEBizException("ƾ֤��ˮ��Ϊ[" + strVousrlno + "]�ļ�¼�е�"
						+ bankName + "[" + trasrlnoRs.getString(0, 0)
						+ "]δά�����д�����֧���кŶ�Ӧ��ϵ������ȷ���ύ!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * �ж��Ƿ�ά������������Ϣ
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
		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// ֱ��֧��
			selectSQL = "SELECT DISTINCT S_TRECODE FROM TBS_TV_DIRECTPAYPLAN_MAIN A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			strVousrlno = String.valueOf(((TbsTvDirectpayplanMainDto) idto)
					.getIvousrlno());
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// ��Ȩ֧��
			selectSQL = "SELECT DISTINCT S_TRECODE FROM TBS_TV_GRANTPAYPLAN_MAIN A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			strVousrlno = String.valueOf(((TbsTvGrantpayplanMainDto) idto)
					.getIvousrlno());
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)) {// ʵ���ʽ�
			selectSQL = "SELECT DISTINCT S_TRECODE FROM TBS_TV_PAYOUT A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			strVousrlno = String
					.valueOf(((TbsTvPayoutDto) idto).getIvousrlno());
		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// ��������
			selectSQL = "SELECT DISTINCT S_TRECODE FROM TV_PAYOUTFINANCE A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			strVousrlno = String.valueOf(((TvPayoutfinanceDto) idto)
					.getIvousrlno());
		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// ���а���ֱ��֧��
			selectSQL = "SELECT DISTINCT S_TRECODE FROM TBS_TV_PBCPAY A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)";
			strVousrlno = String
					.valueOf(((TbsTvPbcpayDto) idto).getIvousrlno());
		} else if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {// ���а���֧����������
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

			// ƾ֤��ˮ��
			sqlExec.addParam(strVousrlno);

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("ƾ֤��ˮ��Ϊ[" + strVousrlno + "]�ļ�¼�еĹ����������["
						+ trasrlnoRs.getString(0, 0) + "]δά������������Ϣ������ȷ���ύ!");
				throw new ITFEBizException("ƾ֤��ˮ��Ϊ[" + strVousrlno
						+ "]�ļ�¼�еĹ����������[" + trasrlnoRs.getString(0, 0)
						+ "]δά������������Ϣ������ȷ���ύ!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * �ж��Ƿ�ά�����ջ��ض��ձ�
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
		if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// �˿�
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
				&& sBeforeOrAfter.equals("1")) {// ����ҵ��ԭ���ջ��ش���
			if (null==idto) {
				selectSQL = "SELECT DISTINCT S_ORITAXORGCODE,S_FILENAME FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
						+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_ORIPAYEETRECODE AND S_TBSTAXORGCODE=A.S_ORITAXORGCODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_ORITAXORGCODE,S_FILENAME FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
						+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_ORIPAYEETRECODE AND S_TBSTAXORGCODE=A.S_ORITAXORGCODE)";
			}
			sExpAdd = "ԭ";
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)
				&& sBeforeOrAfter.equals("2")) {// ����ҵ�������ջ��ش���
			if (null==idto) {
				selectSQL = "SELECT DISTINCT S_CURTAXORGCODE,S_FILENAME FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
						+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_CURPAYEETRECODE AND S_TBSTAXORGCODE=A.S_CURTAXORGCODE)";
			} else {
				selectSQL = "SELECT DISTINCT S_CURTAXORGCODE,S_FILENAME FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? "
						+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
						+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_CURPAYEETRECODE AND S_TBSTAXORGCODE=A.S_CURTAXORGCODE)";
			}
			sExpAdd = "��";
		} else
			return false;

		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// ��������
			sqlExec.addParam(loginfo.getSorgcode());
			if (null==idto) {
				// ���ű�־
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));
			} else {
				// �����ļ���
				sqlExec.addParam(idto.getSfilename());
				//�������
//				sqlExec.addParam(idto.getStrecode());
			}

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("�ļ�[" + trasrlnoRs.getString(0, 1) + "]�е�"
						+ sExpAdd + "���ջ��ش���[" + trasrlnoRs.getString(0, 0)
						+ "]δά�����ջ��ض��գ�����ȷ���ύ!");
				throw new ITFEBizException("�ļ�[" + trasrlnoRs.getString(0, 1)
						+ "]�е����ջ��ش���[" + trasrlnoRs.getString(0, 0)
						+ "]δά�����ջ��ض��գ�����ȷ���ύ!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * �ж��Ƿ�ά�����ջ��ض��ձ�
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
		if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// �˿�
			selectSQL = "SELECT DISTINCT S_TAXORGCODE FROM TBS_TV_DWBK A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_PAYERTRECODE AND S_TBSTAXORGCODE=A.S_TAXORGCODE)";
			strVousrlno = String.valueOf(((TbsTvDwbkDto) idto).getIvousrlno());
		} else if (BizTypeConstant.BIZ_TYPE_TAX_FREE.equals(bizType)) {// ��ֵ�
			selectSQL = "SELECT DISTINCT S_TAXORGCODE FROM TBS_TV_DWBK A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_PAYERTRECODE AND S_TBSTAXORGCODE=A.S_TAXORGCODE)";
			strVousrlno = String.valueOf(((TbsTvDwbkDto) idto).getIvousrlno());
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)
				&& sBeforeOrAfter.equals("1")) {// ����ҵ���ж�ԭ���ջ��ش���

			selectSQL = "SELECT DISTINCT S_ORITAXORGCODE FROM TBS_TV_IN_CORRHANDBOOK A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_ORIPAYEETRECODE AND S_TBSTAXORGCODE=A.S_ORITAXORGCODE)";
			strVousrlno = String.valueOf(((TbsTvInCorrhandbookDto) idto)
					.getIvousrlno());
			sExpAdd = "ԭ";
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)
				&& sBeforeOrAfter.equals("2")) {// ����ҵ���ж������ջ��ش���

			selectSQL = "SELECT DISTINCT S_CURTAXORGCODE FROM TBS_TV_IN_CORRHANDBOOK A WHERE I_VOUSRLNO= ? "
					+ " AND NOT EXISTS (SELECT 1 FROM TS_CONVERTTAXORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_CURPAYEETRECODE AND S_TBSTAXORGCODE=A.S_CURTAXORGCODE)";
			strVousrlno = String.valueOf(((TbsTvInCorrhandbookDto) idto)
					.getIvousrlno());
			sExpAdd = "��";
		} else
			return false;

		SQLExecutor sqlExec = null;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			// ƾ֤��ˮ��
			sqlExec.addParam(strVousrlno);

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("ƾ֤��ˮ��Ϊ[" + strVousrlno + "]�ļ�¼�е�" + sExpAdd
						+ "���ջ��ش���[" + trasrlnoRs.getString(0, 0)
						+ "]δά�����ջ��ض��գ�����ȷ���ύ!");
				throw new ITFEBizException("ƾ֤��ˮ��Ϊ[" + strVousrlno + "]�ļ�¼�е�"
						+ sExpAdd + "���ջ��ش���[" + trasrlnoRs.getString(0, 0)
						+ "]δά�����ջ��ض��գ�����ȷ���ύ!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * �ж��Ƿ�ά������ԭ��������
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
			// ��������
			sqlExec.addParam(loginfo.getSorgcode());
			if (null==idto) {
				// ���ű�־
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));
			} else {
				// �����ļ���
				sqlExec.addParam(idto.getSfilename());
				//�������
//				sqlExec.addParam(idto.getStrecode());
			}

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("�ļ�[" + trasrlnoRs.getString(0, 1) + "]�еĸ���ԭ�����["
						+ trasrlnoRs.getString(0, 0) + "]δά������ԭ�������գ�����ȷ���ύ!");
				throw new ITFEBizException("�ļ�[" + trasrlnoRs.getString(0, 1)
						+ "]�еĸ���ԭ�����[" + trasrlnoRs.getString(0, 0)
						+ "]δά������ԭ�������գ�����ȷ���ύ!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * �ж��Ƿ�ά������ԭ��������
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
			// ƾ֤��ˮ��
			sqlExec.addParam(strVousrlno);

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("ƾ֤��ˮ��Ϊ[" + strVousrlno + "]�ļ�¼�еĸ���ԭ�����["
						+ trasrlnoRs.getString(0, 0) + "]δά������ԭ�������գ�����ȷ���ύ!");
				throw new ITFEBizException("ƾ֤��ˮ��Ϊ[" + strVousrlno
						+ "]�ļ�¼�еĸ���ԭ�����[" + trasrlnoRs.getString(0, 0)
						+ "]δά������ԭ�������գ�����ȷ���ύ!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * �ж��Ƿ�ά���˿�ԭ��������
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
			// ��������
			sqlExec.addParam(loginfo.getSorgcode());
			if (null==idto) {
				// ���ű�־
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));
			} else {
				// �����ļ���
				sqlExec.addParam(idto.getSfilename());
				//��������
//				sqlExec.addParam(idto.getStrecode());
			}

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("�ļ�[" + trasrlnoRs.getString(0, 1) + "]�е��˿�ԭ�����["
						+ trasrlnoRs.getString(0, 0) + "]δά���˿�ԭ�������գ�����ȷ���ύ!");
				throw new ITFEBizException("�ļ�[" + trasrlnoRs.getString(0, 1)
						+ "]�е��˿�ԭ�����[" + trasrlnoRs.getString(0, 0)
						+ "]δά���˿�ԭ�������գ�����ȷ���ύ!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * �ж��Ƿ�ά���˿�ԭ��������
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
			// ƾ֤��ˮ��
			sqlExec.addParam(strVousrlno);

			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("ƾ֤��ˮ��Ϊ[" + strVousrlno + "]�ļ�¼�е��˿�ԭ�����["
						+ trasrlnoRs.getString(0, 0) + "]δά���˿�ԭ�������գ�����ȷ���ύ!");
				throw new ITFEBizException("ƾ֤��ˮ��Ϊ[" + strVousrlno
						+ "]�ļ�¼�е��˿�ԭ�����[" + trasrlnoRs.getString(0, 0)
						+ "]δά���˿�ԭ�������գ�����ȷ���ύ!");
			}
			return bresult;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * ����ҵ������
	 * 
	 * @param bizType
	 * @return
	 */
	public static String getBizname(String bizType) {
		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// ֱ��֧��
			return "ֱ��֧����ȵ���";
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// ��Ȩ֧��
			return "ֱ��֧����ȵ���";
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// ʵ���ʽ�
			return "ʵ���ʽ���";
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)) {// ����
			return "Ԥ�������������";
		} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// �˿�
			return "�˿�ҵ����";
		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// ��������
			return "��������";
		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// ���а���֧��
			return "���а���ֱ��֧��";
		}else if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)) {// ���а���֧����������
			return "���а���֧����������";
		}else if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {// ���а���֧�����������˻�
			return "���а���֧�����������˻�";
		}
		return bizType;

	}

	/**
	 * �ж��տ����к��Ƿ�Ϊ��
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
			// ��������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(idto.getSfilename());
			sqlExec.addParam("");
			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("�ύ��Ϣ�д��������к�û�в�¼�ļ�¼������ȷ���ύ!");
				throw new ITFEBizException("�ύ��Ϣ�д��������к�û�в�¼�ļ�¼������ȷ���ύ!");
			}
			return bresult;
		}catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}
	
	/**
	 * �ж��տ����к��Ƿ�Ϊ��
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
			// ��������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(((TbsTvPayoutDto) idto).getSfilename());
			// ƾ֤��ˮ��
			sqlExec.addParam(((TbsTvPayoutDto) idto).getIvousrlno());
			sqlExec.addParam("");
			SQLResults trasrlnoRs = sqlExec.runQueryCloseCon(selectSQL);
			if (trasrlnoRs.getRowCount() > 0) {
				bresult = false;
				logger.error("ƾ֤��ˮ��Ϊ[" + strVousrlno + "]���տ����к�Ϊ�գ�����ȷ���ύ!");
				throw new ITFEBizException("ƾ֤��ˮ��Ϊ[" + strVousrlno + "]���տ����к�Ϊ�գ�����ȷ���ύ!");
			}
			return bresult;
		}catch (JAFDatabaseException e) {
			logger.error("��ѯ���ݵ�ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ���ݵ�ʱ������쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
	}
}

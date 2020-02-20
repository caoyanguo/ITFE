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
 * @author ���޹� ʵ���ʽ�
 * @time 12-02-21 08:45:49 �������ţ�������ţ�ֱ���ύ��һЩ�жϵ�
 */

public class BatchDirectDeleteUtil {
	private static Log logger = LogFactory.getLog(BatchDirectDeleteUtil.class);

	/**
	 * ֱ��֧����ȣ���������-ɾ��
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
			 * ɾ����ʱ�ӱ�
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_DIRECTPAYPLAN_SUB A WHERE EXISTS(SELECT 1 FROM TBS_TV_DIRECTPAYPLAN_MAIN "
					+ " WHERE I_VOUSRLNO=A.I_VOUSRLNO AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? )";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ɾ����ʱ����
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TBS_TV_DIRECTPAYPLAN_MAIN WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? ";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * �ж��Ƿ���δ�������� û����ֱ�ӷ��ͱ���
			 */
			sendMsgUtil.checkAndSendMsgForPayplan(MsgConstant.MSG_NO_5102,
					TbsTvDirectpayplanMainDto.tableName(),
					idto.getSpackageno(), loginfo,
					BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN);

		} catch (JAFDatabaseException e) {
			logger.error("���ݿ�����쳣!", e);
			throw new ITFEBizException("���ݿ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}

	/**
	 * ��Ȩ֧����ȣ���������-ɾ��
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
			 * ɾ����ʱ�ӱ�
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_GRANTPAYPLAN_SUB A WHERE EXISTS(SELECT 1 FROM TBS_TV_GRANTPAYPLAN_MAIN "
					+ " WHERE I_VOUSRLNO=A.I_VOUSRLNO AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?)";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ɾ����ʱ����
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TBS_TV_GRANTPAYPLAN_MAIN WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * �ж��Ƿ���δ�������� û����ֱ�ӷ��ͱ���
			 */
			sendMsgUtil.checkAndSendMsgForPayplan(MsgConstant.MSG_NO_5103,
					TbsTvGrantpayplanMainDto.tableName(), idto.getSpackageno(),
					loginfo, BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN);
		} catch (JAFDatabaseException e) {
			logger.error("���ݿ�����쳣!", e);
			throw new ITFEBizException("���ݿ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}

	/**
	 * ʵ���ʽ���������-ɾ��
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
			 * ɾ����ʱ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

//			String movedataSql = "DELETE FROM TBS_TV_PAYOUT A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
			String movedataSql = "DELETE FROM TBS_TV_PAYOUT A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? ";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
//			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ����ʽ�ӱ�
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

//			movedataSql = "DELETE FROM TV_PAYOUTMSGSUB A WHERE EXISTS (SELECT 1 from TV_PAYOUTMSGMAIN WHERE S_BIZNO=A.S_BIZNO AND S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?)";
			movedataSql = "DELETE FROM TV_PAYOUTMSGSUB A WHERE EXISTS (SELECT 1 from TV_PAYOUTMSGMAIN WHERE S_BIZNO=A.S_BIZNO AND S_ORGCODE= ? AND S_FILENAME= ? )";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
//			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ����ʽ����
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

//			movedataSql = "DELETE FROM TV_PAYOUTMSGMAIN Where S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
			movedataSql = "DELETE FROM TV_PAYOUTMSGMAIN Where S_ORGCODE= ? AND S_FILENAME= ? ";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
//			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

//			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ?  AND S_TRECODE= ?";
			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ? ";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
//			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
		} catch (JAFDatabaseException e) {
			logger.error("���ݿ�����쳣!", e);
			throw new ITFEBizException("���ݿ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}

	/**
	 * �˿⣺��������-ɾ��
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
			 * ɾ����ʱ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			//String movedataSql = "DELETE FROM TBS_TV_DWBK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_PAYERTRECODE= ?";
			String movedataSql = "DELETE FROM TBS_TV_DWBK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			//sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ��ҵ���
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();

			//movedataSql = "DELETE FROM TV_DWBK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_PAYERTRECODE= ?";
			movedataSql = "DELETE FROM TV_DWBK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			//sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();

			//movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			//sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
		} catch (JAFDatabaseException e) {
			logger.error("���ݿ�����쳣!", e);
			throw new ITFEBizException("���ݿ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}
	/**
	 * ��ֵ�����������-ɾ��
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
			 * ɾ����ʱ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_Free A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_FREEPLUPTRECODE= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ��ҵ���
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FREE A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_FREEPLUPTRECODE= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
		} catch (JAFDatabaseException e) {
			logger.error("���ݿ�����쳣!", e);
			throw new ITFEBizException("���ݿ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}
	/**
	 * ��������������-ɾ��
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
			 * ɾ����ʱ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_CURPAYEETRECODE= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ��ҵ���
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_CURPAYEETRECODE= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ?  AND S_TRECODE= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
		} catch (JAFDatabaseException e) {
			logger.error("���ݿ�����쳣!", e);
			throw new ITFEBizException("���ݿ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}

	/**
	 * ������������������-ɾ��
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
			 * ɾ����ʱ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TV_PAYOUTFINANCE A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? ";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ? ";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			sqlExec.runQueryCloseCon(movedataSql);

		} catch (JAFDatabaseException e) {
			logger.error("���ݿ�����쳣!", e);
			throw new ITFEBizException("���ݿ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}

	/**
	 * ���а���ֱ��֧������������-ɾ��
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
			 * ɾ��ҵ��ӱ�
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TV_PBCPAY_SUB A WHERE EXISTS (SELECT 1 FROM TBS_TV_PBCPAY WHERE I_VOUSRLNO=A.I_VOUSRLNO "
					+ " AND S_BOOKORGCODE=? AND S_FILENAME= ? AND S_TRECODE= ?)";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ��ҵ������
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_PBCPAY_MAIN A WHERE EXISTS (SELECT 1 FROM TBS_TV_PBCPAY WHERE I_VOUSRLNO=A.I_VOUSRLNO "
					+ " AND S_BOOKORGCODE=? AND S_FILENAME= ? AND S_TRECODE= ?)";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ����ʱ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TBS_TV_PBCPAY A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF A WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(treCode);
			sqlExec.runQueryCloseCon(movedataSql);
		} catch (JAFDatabaseException e) {
			logger.error("���ݿ�����쳣!", e);
			throw new ITFEBizException("���ݿ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}

	/**
	 * ���а���֧���������룺��������-ɾ��
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
			 * ɾ����ʱ�ӱ�
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM tbs_tv_bnkpay_sub A WHERE EXISTS(SELECT 1 FROM tbs_tv_bnkpay_main "
					+ " WHERE I_VOUSRLNO=A.I_VOUSRLNO AND S_PACKNO = ? AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? )";

			//����ˮ��
			sqlExec.addParam(pckano);
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ɾ����ʱ����
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM tbs_tv_bnkpay_main WHERE  S_PACKNO = ? AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// ����ˮ��
			sqlExec.addParam(pckano);
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF WHERE S_PACKAGENO = ? AND S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? ";

			// ����ˮ��
			sqlExec.addParam(pckano);
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

//			/**
//			 * �ж��Ƿ���δ�������� û����ֱ�ӷ��ͱ���
//			 */
//			sendMsgUtil.checkAndSendMsgForPayplan(MsgConstant.APPLYPAY_DAORU,
//					TbsTvBnkpayMainDto.tableName(),
//					idto.getSpackageno(), loginfo,
//					BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);

		} catch (JAFDatabaseException e) {
			logger.error("���ݿ�����쳣!", e);
			throw new ITFEBizException("���ݿ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}
	
	/**
	 * ���а���֧�����������˻أ���������-ɾ��
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
			 * ɾ����ʱ�ӱ�
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM tbs_tv_bnkpay_sub A WHERE EXISTS(SELECT 1 FROM tbs_tv_bnkpay_main "
					+ " WHERE I_VOUSRLNO=A.I_VOUSRLNO AND S_PACKNO = ? AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? )";

			// ����ˮ��
			sqlExec.addParam(pckano);
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ɾ����ʱ����
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM tbs_tv_bnkpay_main WHERE S_PACKNO = ? AND S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";

			// ����ˮ��
			sqlExec.addParam(pckano);
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF WHERE S_PACKAGENO = ? AND S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? ";

			// ����ˮ��
			sqlExec.addParam(pckano);
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(streCode);
			sqlExec.runQueryCloseCon(movedataSql);

//			/**
//			 * �ж��Ƿ���δ�������� û����ֱ�ӷ��ͱ���
//			 */
//			sendMsgUtil.checkAndSendMsgForPayplan(MsgConstant.APPLYPAY_DAORU,
//					TbsTvBnkpayMainDto.tableName(),
//					idto.getSpackageno(), loginfo,
//					BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);

		} catch (JAFDatabaseException e) {
			logger.error("���ݿ�����쳣!", e);
			throw new ITFEBizException("���ݿ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}
}
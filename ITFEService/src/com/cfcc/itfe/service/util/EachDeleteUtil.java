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
 * @author�����
 * @time 12-02-21 08:45:49 ���ɾ��
 */

public class EachDeleteUtil {
	private static Log logger = LogFactory.getLog(EachDeleteUtil.class);

	/**
	 * ֱ��֧����ȣ��������-ɾ��
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
			 * ɾ����ʱ�ӱ�
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_DIRECTPAYPLAN_SUB A WHERE EXISTS(SELECT 1 FROM TBS_TV_DIRECTPAYPLAN_MAIN "
					+ " WHERE I_VOUSRLNO=A.I_VOUSRLNO AND S_BOOKORGCODE= ? AND S_FILENAME= ? )";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ɾ����ʱ����
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TBS_TV_DIRECTPAYPLAN_MAIN WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? ";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? ";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
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
	 * ��Ȩ֧����ȣ��������-ɾ��
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
			 * ɾ����ʱ�ӱ�
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_GRANTPAYPLAN_SUB A WHERE EXISTS(SELECT 1 FROM TBS_TV_GRANTPAYPLAN_MAIN "
					+ " WHERE I_VOUSRLNO=A.I_VOUSRLNO AND S_BOOKORGCODE= ? AND S_FILENAME= ? )";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ɾ����ʱ����
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TBS_TV_GRANTPAYPLAN_MAIN WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? ";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? ";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
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
	 * ʵ���ʽ��������-ɾ��
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
			 * ɾ����ʱ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_PAYOUT A WHERE I_VOUSRLNO= ? ";

			// ƾ֤��ˮ��
			sqlExec.addParam(dto.getIvousrlno());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * �����ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
			// ƾ֤���
			sqlExec.addParam(dto.getFamt());
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			// ����ˮ��
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.runQueryCloseCon(movedataSql);
			
			/**
			 * ɾ���ļ������Ӧ��ϵ�����ܽ��Ϊ0�ļ�¼
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM��TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? AND N_MONEY=? AND I_COUNT=?";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			// ����ˮ��
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.addParam(0);
			sqlExec.addParam(0);
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
	 * �˿⣺�������-ɾ��
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
			 * ɾ����ʱ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_DWBK A WHERE I_VOUSRLNO= ? ";
			// ƾ֤��ˮ��
			sqlExec.addParam(dto.getIvousrlno());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * �����ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
			// ƾ֤���
			sqlExec.addParam(dto.getFamt());
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			// ����ˮ��
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.runQueryCloseCon(movedataSql);
			
			/**
			 * ɾ���ļ������Ӧ��ϵ�����ܽ��Ϊ0�ļ�¼
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM��TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? AND N_MONEY=? AND I_COUNT=?";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			// ����ˮ��
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.addParam(0);
			sqlExec.addParam(0);
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
	 * ��ֵ����������-ɾ��
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
			 * ɾ����ʱ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_Free A WHERE I_VOUSRLNO= ? ";
			// ƾ֤��ˮ��
			sqlExec.addParam(dto.getIvousrlno());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * �����ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
			// ƾ֤���
			sqlExec.addParam(dto.getFfreepluamt().add(dto.getFfreemiamt()));
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			// ����ˮ��
			sqlExec.addParam(dto.getSpackno());
			sqlExec.runQueryCloseCon(movedataSql);
			
			/**
			 * ɾ���ļ������Ӧ��ϵ�����ܽ��Ϊ0�ļ�¼
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM��TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? AND N_MONEY=? AND I_COUNT=?";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			// ����ˮ��
			sqlExec.addParam(dto.getSpackno());
			sqlExec.addParam(0);
			sqlExec.addParam(0);
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
	 * �������������-ɾ��
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
			 * ɾ����ʱ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_IN_CORRHANDBOOK A WHERE I_VOUSRLNO= ? ";

			// ƾ֤��ˮ��
			sqlExec.addParam(dto.getIvousrlno());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * �����ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
			// ƾ֤ԭ�������
			sqlExec.addParam(dto.getForicorramt());
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			// ����ˮ��
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ɾ���ļ������Ӧ��ϵ�����ܽ��Ϊ0�ļ�¼
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM��TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? AND N_MONEY=? AND I_COUNT=?";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			// ����ˮ��
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.addParam(0);
			sqlExec.addParam(0);
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
	 * ���а���ֱ��֧�����������-ɾ��
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
			 * ɾ����ʱ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = "DELETE FROM TBS_TV_PBCPAY A WHERE I_VOUSRLNO= ? ";

			// ƾ֤��ˮ��
			sqlExec.addParam(dto.getIvousrlno());
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * �����ļ������Ӧ��ϵ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
			// ƾ֤���
			sqlExec.addParam(dto.getFamt());
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			// ����ˮ��
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.runQueryCloseCon(movedataSql);
			
			/**
			 * ɾ���ļ������Ӧ��ϵ�����ܽ��Ϊ0�ļ�¼
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "DELETE FROM��TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? AND N_MONEY=? AND I_COUNT=?";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(dto.getSfilename());
			// ����ˮ��
			sqlExec.addParam(dto.getSpackageno());
			sqlExec.addParam(0);
			sqlExec.addParam(0);
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

}
package com.cfcc.itfe.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author ����� ��ֵ�
 * @time 13-02-21 08:45:49 �������ţ�������ţ�ֱ���ύ��һЩ�жϵ�
 */

public class BatchFreeCommitUtil {
	private static Log logger = LogFactory.getLog(BatchFreeCommitUtil.class);

	/**
	 * ��ֵ�����������-ȷ���ύ
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmFree(String bizType, TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		String fileName = idto.getSfilename();

		SQLExecutor sqlExec = null;
		try {
			/**
			 * ��ʱ��д����ʽ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String movedataSql = "INSERT INTO TV_FREE("
					+ "I_VOUSRLNO,S_TAXORGCODE,S_PACKNO,S_TRANO,D_BILLDATE,"
					+ "S_ELECTROTAXVOUNO,S_FREEVOUNO,C_FREEPLUTYPE,S_FREEPLUSUBJECTCODE,"
					+ "C_FREEPLULEVEL,S_TBSFREEPLUSIGN,S_FREEPLUSIGN,S_FREEPLUPTRECODE,F_FREEPLUAMT,"
					+ "C_FREEMIKIND,S_FREEMISUBJECT,C_FREEMILEVEL,S_TBSFREEMISIGN,S_FREEMISIGN,S_FREEMIPTRE,"
					+ "F_FREEMIAMT,S_CORPCODE,C_TRIMSIGN,S_STATUS,S_FILENAME,"
					+ "S_BOOKORGCODE,D_ACCEPTDATE,D_AUDITDATE,S_CHANNELCODE,"
					+ "S_ADDWORD,TS_SYSUPDATE)"
					+ "SELECT I_VOUSRLNO,S_TAXORGCODE,S_PACKNO,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),D_BILLDATE,"
					+ "S_ELECTROTAXVOUNO,S_FREEVOUNO,C_FREEPLUTYPE,S_FREEPLUSUBJECTCODE,"
					+ "C_FREEPLULEVEL,S_FREEPLUSIGN,'',S_FREEPLUPTRECODE,F_FREEPLUAMT,"
					+ "C_FREEMIKIND,S_FREEMISUBJECT,C_FREEMILEVEL,S_FREEMISIGN,'',S_FREEMIPTRE,"
					+ "F_FREEMIAMT,S_CORPCODE,C_TRIMFLAG,'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "',S_FILENAME,"
					+ "S_BOOKORGCODE,D_ACCEPTDATE,D_ACCTDATE,S_CHANNELCODE,"
					+ "S_ADDWORD,CURRENT TIMESTAMP "
					+ "FROM TBS_TV_FREE A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_FREEPLUPTRECODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(idto.getStrecode());
			// ����״̬
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ���¸�����־
			 */
			/**
			 * ���������Ϳ�Ŀ���뾫ȷƥ��
			 */
			/**
			 * ��ֵ���������־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql1 = " update TV_FREE a "
					+ " set a.S_FREEPLUSIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_FREEPLUPTRECODE = b.S_TRECODE and a.S_FREEPLUSUBJECTCODE = b.S_BUDGETSUBCODE and a.S_TBSFREEPLUSIGN = b.S_TBSASSITSIGN )"
					+ " where a.S_BOOKORGCODE = ? and a.S_FILENAME = ? and a.S_FREEPLUPTRECODE= ?";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(idto.getStrecode());
			sqlExec.runQueryCloseCon(updateAssistSql1);
			/**
			 * ��ֵ���������־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql1 = " update TV_FREE a "
					+ " set a.S_FREEMISIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_FREEMIPTRE = b.S_TRECODE and a.S_FREEMISUBJECT = b.S_BUDGETSUBCODE and a.S_TBSFREEMISIGN = b.S_TBSASSITSIGN ) "
					+ " where a.S_BOOKORGCODE = ? and a.S_FILENAME = ? and a.S_FREEMIPTRE= ?";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(idto.getStrecode());
			sqlExec.runQueryCloseCon(updateAssistSql1);

			/**
			 * ������뾫ȷƥ�䡢��Ŀ����ΪN
			 */
			/**
			 * ��ֵ���������־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql2 = " update TV_FREE a "
					+ " set a.S_FREEPLUSIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_FREEPLUPTRECODE = b.S_TRECODE and b.S_BUDGETSUBCODE =? and a.S_TBSFREEPLUSIGN = b.S_TBSASSITSIGN and (a.S_FREEPLUSIGN =? or a.S_FREEPLUSIGN is null)) "
					+ " where a.S_BOOKORGCODE = ? and a.S_FILENAME = ? and a.S_FREEPLUPTRECODE= ? and (a.S_FREEPLUSIGN =? or a.S_FREEPLUSIGN is null)";
			// �����������
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(idto.getStrecode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql2);
			/**
			 * ��ֵ���������־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql2 = " update TV_FREE a "
					+ " set a.S_FREEMISIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_FREEMIPTRE = b.S_TRECODE and b.S_BUDGETSUBCODE =? and a.S_TBSFREEMISIGN = b.S_TBSASSITSIGN and (a.S_FREEMISIGN =? or a.S_FREEMISIGN is null)) "
					+ " where a.S_BOOKORGCODE = ? and a.S_FILENAME = ? and a.S_FREEMIPTRE= ? and (a.S_FREEMISIGN =? or a.S_FREEMISIGN is null)";
			// �����������
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(idto.getStrecode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql2);

			/**
			 * ��Ŀ���뾫ȷƥ�䡢�������ΪN
			 */
			/**
			 * ��ֵ���������־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql3 = " update TV_FREE a "
					+ " set a.S_FREEPLUSIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and a.S_FREEPLUSUBJECTCODE = b.S_BUDGETSUBCODE and a.S_TBSFREEPLUSIGN = b.S_TBSASSITSIGN and (a.S_FREEPLUSIGN =? or a.S_FREEPLUSIGN is null)) "
					+ " where a.S_BOOKORGCODE = ? and a.S_FILENAME = ? and a.S_FREEPLUPTRECODE= ? and (a.S_FREEPLUSIGN =? or a.S_FREEPLUSIGN is null)";

			// �����������
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(idto.getStrecode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql3);
			/**
			 * ��ֵ���������־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql3 = " update TV_FREE a "
					+ " set a.S_FREEMISIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and a.S_FREEMISUBJECT = b.S_BUDGETSUBCODE and a.S_TBSFREEMISIGN = b.S_TBSASSITSIGN and (a.S_FREEMISIGN =? or a.S_FREEMISIGN is null)) "
					+ " where a.S_BOOKORGCODE = ? and a.S_FILENAME = ? and a.S_FREEMIPTRE= ? and (a.S_FREEMISIGN =? or a.S_FREEMISIGN is null)";
			// �����������
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(idto.getStrecode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql3);

			/**
			 * ��Ŀ����ΪN���������ΪN
			 */
			/**
			 * ��ֵ���������־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql4 = " update TV_FREE a "
					+ " set a.S_FREEPLUSIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and b.S_BUDGETSUBCODE = ? and a.S_TBSFREEPLUSIGN = b.S_TBSASSITSIGN and (a.S_FREEPLUSIGN =? or a.S_FREEPLUSIGN is null)) "
					+ " where a.S_BOOKORGCODE = ? and a.S_FILENAME = ? and a.S_FREEPLUPTRECODE= ? and (a.S_FREEPLUSIGN =? or a.S_FREEPLUSIGN is null)";

			// �����������
			sqlExec.addParam("N");
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(idto.getStrecode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql4);
			/**
			 * ��ֵ���������־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql4 = " update TV_FREE a "
					+ " set a.S_FREEMISIGN = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and b.S_BUDGETSUBCODE = ? and a.S_TBSFREEMISIGN = b.S_TBSASSITSIGN and (a.S_FREEMISIGN =? or a.S_FREEMISIGN is null)) "
					+ " where a.S_BOOKORGCODE = ? and a.S_FILENAME = ? and a.S_FREEMIPTRE= ? and (a.S_FREEMISIGN =? or a.S_FREEMISIGN is null)";
			// �����������
			sqlExec.addParam("N");
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(idto.getStrecode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql4);
			/**
			 * �޸����ű�־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_Free SET S_STATUS=? WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_FREEPLUPTRECODE= ?";
			// �����������
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(idto.getStrecode());
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ��TIPS���ͱ���
			 */
			// �鿴�ļ�����Ӧ�İ���ˮ����������ʱ�����Ƿ����δ���ŵ�����
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql = "SELECT count(1) FROM TBS_TV_Free A "
					+ " WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
					+ " AND EXISTS (SELECT 1 FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= A.S_BOOKORGCODE "
					+ " AND S_PACKAGENO=A.S_PACKNO AND S_FILENAME= ? and S_TRECODE= ?)";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// ���ű�־
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			// �����ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(idto.getStrecode());
			SQLResults rs = sqlExec.runQueryCloseCon(strsql);
			// ������δ���ŵ�����
			if (rs.getInt(0, 0) == 0) {
				// ������ˮ�ŷ���TIPS
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(fileName);
				// ��������
				sqlExec.addParam(idto.getStrecode());
				SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
				int row = rsfilepac.getRowCount();
				for (int i = 0; i < row; i++) {
					// // ������ˮ�ŷ���TIPS
					sendMsgUtil.sendMsg(fileName, loginfo.getSorgcode(),
							rsfilepac.getString(i, 0), MsgConstant.MSG_NO_1106,
							rsfilepac.getString(i, 1), false);
				}
			}
			/**
			 * �޸��ļ������ˮ��Ӧ��ϵ��(�ѷ��ͣ�������)
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=?"
					+ " WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
			// �����������
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
			sqlExec.addParam(idto.getStrecode());
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
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

import com.cfcc.itfe.config.ITFECommonConstant;
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
 * @time 12-02-21 08:45:49  �������ţ�������ţ�ֱ���ύ��һЩ�жϵ�
 */

public class  DirectCorrectCommitUtil{
	private static Log logger = LogFactory.getLog(DirectCorrectCommitUtil.class);
			
	/**
	 * ������ֱ���ύ
	 * 
	 * @param bizType
	 * @throws ITFEBizException
	 */
	static boolean confirmCorrect(String bizType,ITFELoginInfo loginfo) throws ITFEBizException {
		boolean bResult;
		if(!ITFECommonConstant.ISCONVERT.equals("0")){//���ջ��ش��롢������־��ת��
		/**
		 * �ж��Ƿ�ά��ԭ���ջ��ش�����ձ�
		 */
		bResult = CheckBizParam.checkConvertTaxOrg(bizType, null, "1",loginfo);
		if (!bResult) {
			return false;
		}
		/**
		 * �ж��Ƿ�ά�������ջ��ش�����ձ�
		 */
		bResult = CheckBizParam.checkConvertTaxOrg(bizType, null, "2",loginfo);
		if (!bResult) {
			return false;
		}
		}
		/**
		 * �ж��Ƿ�ά������ԭ��������
		 */
		bResult = CheckBizParam.checkCorrReason(null,loginfo);
		if (!bResult) {
			return false;
		}
		SQLExecutor sqlExec = null;
		try {
			/**
			 * ��ʱ��д����ʽ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String movedataSql = "";
			if(!ITFECommonConstant.ISCONVERT.equals("0")){//���ջ��ش��롢������־��ת��
				movedataSql = "INSERT INTO TV_IN_CORRHANDBOOK("
					+ "I_VOUSRLNO,S_ELECVOUNO,S_CORRVOUNO,D_ACCEPT,D_ACCT,"
					+ "D_VOUCHER,S_ORIPAYEETRECODE,S_ORIAIMTRECODE,S_ORITAXORGCODE,S_ORIBDGSBTCODE,"
					+ "C_ORIBDGLEVEL,C_ORIBDGKIND,S_ORIASTFLAG,"
					+ " F_ORICORRAMT,S_CURPAYEETRECODE,S_CURAIMTRECODE,S_CURTAXORGCODE,"
					+ " S_CURBDGSBTCODE,C_CURBDGLEVEL,C_CURBDGKIND,"
					+ " F_CURCORRAMT,S_CURASTFLAG,S_REASONCODE,C_TRIMFLAG,S_PACKAGENO,"
					+ "S_DEALNO,S_STATUS,S_FILENAME,S_BOOKORGCODE,TS_SYSUPDATE,S_TBSORIASTFLAG,S_TBSCURASTFLAG)"
					+ " SELECT "
					+ "I_VOUSRLNO,S_ELECVOUNO,S_CORRVOUNO,D_ACCEPT,D_ACCT,"
					+ "D_VOUCHER,S_ORIPAYEETRECODE,S_ORIAIMTRECODE,"
					+ "(SELECT MAX(S_TCBSTAXORGCODE) FROM TS_CONVERTTAXORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_ORIPAYEETRECODE AND S_TBSTAXORGCODE=A.S_ORITAXORGCODE),"
					+ "S_ORIBDGSBTCODE,C_ORIBDGLEVEL,C_ORIBDGKIND,"
					+ "'',"
					+ "F_ORICORRAMT,S_CURPAYEETRECODE,S_CURAIMTRECODE,"
					+ " (SELECT MAX(S_TCBSTAXORGCODE) FROM TS_CONVERTTAXORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_CURPAYEETRECODE AND S_TBSTAXORGCODE=A.S_CURTAXORGCODE),"
					+ "S_CURBDGSBTCODE,C_CURBDGLEVEL,C_CURBDGKIND,F_CURCORRAMT,"
					+ "'',"
					+ "(SELECT MAX(S_TCBSCORRCODE) FROM TS_CORRREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE"
					+ " AND S_TBSCORRCODE=A.S_REASONCODE),C_TRIMFLAG,S_PACKAGENO,"
					+ "substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "',S_FILENAME,S_BOOKORGCODE,CURRENT TIMESTAMP,S_ORIASTFLAG,S_CURASTFLAG  "
					+ " FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
			}else{//���ջ��ش��롢������־����ת��
				movedataSql = "INSERT INTO TV_IN_CORRHANDBOOK("
					+ "I_VOUSRLNO,S_ELECVOUNO,S_CORRVOUNO,D_ACCEPT,D_ACCT,"
					+ "D_VOUCHER,S_ORIPAYEETRECODE,S_ORIAIMTRECODE,S_ORITAXORGCODE,S_ORIBDGSBTCODE,"
					+ "C_ORIBDGLEVEL,C_ORIBDGKIND,S_ORIASTFLAG,"
					+ " F_ORICORRAMT,S_CURPAYEETRECODE,S_CURAIMTRECODE,S_CURTAXORGCODE,"
					+ " S_CURBDGSBTCODE,C_CURBDGLEVEL,C_CURBDGKIND,"
					+ " F_CURCORRAMT,S_CURASTFLAG,S_REASONCODE,C_TRIMFLAG,S_PACKAGENO,"
					+ "S_DEALNO,S_STATUS,S_FILENAME,S_BOOKORGCODE,TS_SYSUPDATE,S_TBSORIASTFLAG,S_TBSCURASTFLAG)"
					+ " SELECT "
					+ "I_VOUSRLNO,S_ELECVOUNO,S_CORRVOUNO,D_ACCEPT,D_ACCT,"
					+ "D_VOUCHER,S_ORIPAYEETRECODE,S_ORIAIMTRECODE,"
					+ "S_ORITAXORGCODE,"
					+ "S_ORIBDGSBTCODE,C_ORIBDGLEVEL,C_ORIBDGKIND,"
					+ "S_ORIASTFLAG,"
					+ "F_ORICORRAMT,S_CURPAYEETRECODE,S_CURAIMTRECODE,"
					+ "S_CURTAXORGCODE,"
					+ "S_CURBDGSBTCODE,C_CURBDGLEVEL,C_CURBDGKIND,F_CURCORRAMT,"
					+ "S_CURASTFLAG,"
					+ "(SELECT MAX(S_TCBSCORRCODE) FROM TS_CORRREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE"
					+ " AND S_TBSCORRCODE=A.S_REASONCODE),C_TRIMFLAG,S_PACKAGENO,"
					+ "substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "',S_FILENAME,S_BOOKORGCODE,CURRENT TIMESTAMP,S_ORIASTFLAG,S_CURASTFLAG  "
					+ " FROM TBS_TV_IN_CORRHANDBOOK A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
			}

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// ���ű�־
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ���¡�ԭ������־��
			 */
			if(!ITFECommonConstant.ISCONVERT.equals("0")){//������־��ת��
			// ���������Ϳ�Ŀ���뾫ȷƥ��
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql1 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_ORIASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_ORIAIMTRECODE = b.S_TRECODE and a.S_ORIBDGSBTCODE = b.S_BUDGETSUBCODE and a.S_TBSORIASTFLAG = b.S_TBSASSITSIGN ) "
					+ " where a.S_BOOKORGCODE= ? AND a.S_ORIASTFLAG =? ";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql1);

			// ������뾫ȷƥ�䡢��Ŀ����ΪN
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql2 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_ORIASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_ORIAIMTRECODE = b.S_TRECODE and b.S_BUDGETSUBCODE =? and a.S_TBSORIASTFLAG = b.S_TBSASSITSIGN and (a.S_ORIASTFLAG =? or a.S_ORIASTFLAG is null)) "
					+ " where a.S_BOOKORGCODE= ? and (a.S_ORIASTFLAG =? or a.S_ORIASTFLAG is null)";
			// �����������
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql2);

			// ��Ŀ���뾫ȷƥ�䡢�������ΪN
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql3 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_ORIASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and a.S_ORIBDGSBTCODE = b.S_BUDGETSUBCODE and a.S_TBSORIASTFLAG = b.S_TBSASSITSIGN and (a.S_ORIASTFLAG =? or a.S_ORIASTFLAG is null)) "
					+ " where a.S_BOOKORGCODE= ? and (a.S_ORIASTFLAG =? or a.S_ORIASTFLAG is null)";
			// �����������
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql3);

			// ��Ŀ����ΪN���������ΪN
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql4 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_ORIASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and b.S_BUDGETSUBCODE = ? and a.S_TBSORIASTFLAG = b.S_TBSASSITSIGN and (a.S_ORIASTFLAG =? or a.S_ORIASTFLAG is null)) "
					+ " where a.S_BOOKORGCODE= ? and (a.S_ORIASTFLAG =? or a.S_ORIASTFLAG is null)";
			// �����������
			sqlExec.addParam("N");
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql4);

			/**
			 * ���¡��ָ�����־��
			 */
			// ���������Ϳ�Ŀ���뾫ȷƥ��
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql1 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_CURASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_ORIAIMTRECODE = b.S_TRECODE and a.S_CURBDGSBTCODE = b.S_BUDGETSUBCODE and a.S_TBSCURASTFLAG = b.S_TBSASSITSIGN ) "
					+ " where a.S_BOOKORGCODE= ? ";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.runQueryCloseCon(updateAssistSql1);

			// ������뾫ȷƥ�䡢��Ŀ����ΪN
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql2 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_CURASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_CURAIMTRECODE = b.S_TRECODE and b.S_BUDGETSUBCODE =? and a.S_TBSCURASTFLAG = b.S_TBSASSITSIGN and (a.S_CURASTFLAG =? or a.S_CURASTFLAG is null)) "
					+ " where a.S_BOOKORGCODE= ? and (a.S_CURASTFLAG =? or a.S_CURASTFLAG is null)";
			// �����������
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql2);

			// ��Ŀ���뾫ȷƥ�䡢�������ΪN
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql3 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_CURASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and a.S_CURBDGSBTCODE = b.S_BUDGETSUBCODE and a.S_TBSCURASTFLAG = b.S_TBSASSITSIGN and (a.S_CURASTFLAG =? or a.S_CURASTFLAG is null)) "
					+ " where a.S_BOOKORGCODE= ? and (a.S_CURASTFLAG =? or a.S_CURASTFLAG is null)";
			// �����������
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql3);

			// ��Ŀ����ΪN���������ΪN
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql4 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_CURASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and b.S_BUDGETSUBCODE = ? and a.S_TBSCURASTFLAG = b.S_TBSASSITSIGN and (a.S_CURASTFLAG =? or a.S_CURASTFLAG is null)) "
					+ " where a.S_BOOKORGCODE= ? and (a.S_CURASTFLAG =? or a.S_CURASTFLAG is null)";
			// �����������
			sqlExec.addParam("N");
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql4);
			}
			/**
			 * �޸����ű�־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_IN_CORRHANDBOOK SET S_STATUS=? WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
			// �����������
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// ���ű�־
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ��TIPS���ͱ���
			 */

			// ������ˮ�ŷ���TIPS
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? "
					+ " AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? )  AND S_OPERATIONTYPECODE= ?";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// ���ű�־
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			// ҵ������
			sqlExec.addParam(bizType);
			SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
			int row = rsfilepac.getRowCount();
			for (int i = 0; i < row; i++) {
				// // ������ˮ�ŷ���TIPS
				sendMsgUtil.sendMsg("", loginfo.getSorgcode(), rsfilepac
						.getString(i, 0), MsgConstant.MSG_NO_1105, rsfilepac
						.getString(i, 1), false);
			}
			/**
			 * �޸��ļ������ˮ��Ӧ��ϵ��(�ѷ��ͣ�������)
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=?"
					+ " WHERE S_ORGCODE= ? AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? )  AND S_OPERATIONTYPECODE= ? ";
			// �����������
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
			sqlExec.addParam(loginfo.getSorgcode());
			// ���ű�־
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			// ҵ������
			sqlExec.addParam(bizType);
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
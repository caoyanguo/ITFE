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
 * @author ���а���֧��ֱ���ύ
 */

public class  DirectPBCPayOutCommitUtil{
	private static Log logger = LogFactory.getLog(DirectPBCPayOutCommitUtil.class);
			
	/**
	 * ���а���֧��
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmPbcPayout(String bizType, ITFELoginInfo loginfo)
			throws ITFEBizException {
		boolean bResult;
		/**
		 * �ж��Ƿ�ά������������Ϣ
		 */
		bResult = CheckBizParam.checkConvertFinOrg(bizType, null,loginfo);
		if (!bResult) {
			return false;
		}
		
		SQLExecutor sqlExec = null;
		try {
			/**
			 * ��ʱ��д��������ʽ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql =" INSERT INTO TV_PBCPAY_MAIN" 
				+"	(I_VOUSRLNO,S_TRANO,S_ORGCODE,S_TRECODE,S_BILLORG,"
				+"	S_ENTRUSTDATE,S_PACKNO,S_PAYOUTVOUTYPENO,S_PAYMODE,S_VOUNO,D_VOUCHER,"
				+"	S_PAYERACCT,S_PAYERNAME,S_PAYEEACCT,S_PAYEENAME,S_RCVBNKNO,S_PAYEEOPNBNKNO,  "
				+"	S_ADDWORD,C_BDGKIND,I_OFYEAR,S_BDGADMTYPE,F_AMT,C_TRIMFLAG, "
				+"	S_STATUS,S_BIZTYPE,S_BACKFLAG,S_INPUTERID,TS_SYSUPDATE) "
				+"	select  "
				+"	I_VOUSRLNO,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),S_BOOKORGCODE,S_TRECODE," 
				+"	(SELECT MAX(S_FINORGCODE) FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE), "
				+"	substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2)," 
				+"	S_PACKAGENO,'1','0',S_VOUNO,"
				+"	substr(CHAR(D_VOUCHER),1,4)||substr(CHAR(D_VOUCHER),6,2)||substr(CHAR(D_VOUCHER),9,2), "
				+"	S_PAYERACCT,S_PAYERNAME,S_PAYEEACCT,S_PAYEENAME,S_PAYEEOPNBNKNO,S_PAYEEOPNBNKNO, "
				+"	S_ADDWORD,C_BDGKIND,INTEGER(substr(CHAR(D_VOUCHER),1,4)),'',F_AMT,C_TRIMFLAG, "
				+"	'"+DealCodeConstants.DEALCODE_ITFE_DEALING+"'," 
				+"	S_BIZTYPE,S_BACKFLAG,'"+loginfo.getSuserCode()+"',CURRENT TIMESTAMP  "
				+"	FROM  TBS_TV_PBCPAY A WHERE S_BOOKORGCODE=? AND (S_STATUS IS NULL OR S_STATUS= ?)";
			
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// ����״̬
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ��ʱ��д���ӱ���ʽ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "INSERT INTO TV_PBCPAY_SUB (I_VOUSRLNO,I_SEQNO,S_BDGORGCODE,S_FUNCSBTCODE,S_ECOSBTCODE,C_ACCTPROP,F_AMT,TS_SYSUPDATE)"
				+"SELECT TRIM(CHAR(I_VOUSRLNO))   "
				+",1,S_BDGORGCODE,S_FUNCSBTCODE,S_ECOSBTCODE,'',F_AMT,CURRENT TIMESTAMP "
				+"FROM TBS_TV_PBCPAY A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";

			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// ����״̬
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * �޸����ű�־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_PBCPAY SET S_STATUS=? WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
			// �����������
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// ���ű�־
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ��TIPS���ͱ���
			 */
			// ������ˮ�ŷ���TIPS
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? "
					+ " AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND S_OPERATIONTYPECODE= ? ";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// ���ű�־
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
			// ҵ�����ͣ�ʵ���ʽ�������ҵ�����ͣ�
			sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY);
			SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
			int row = rsfilepac.getRowCount();
			for (int i = 0; i < row; i++) {
				// // ������ˮ�ŷ���TIPS
				sendMsgUtil.sendMsg("", loginfo.getSorgcode(), rsfilepac
						.getString(i, 0), MsgConstant.MSG_NO_5104, rsfilepac
						.getString(i, 1), false);
			}
			/**
			 * �޸��ļ������ˮ��Ӧ��ϵ��(�ѷ��ͣ�������)
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=?"
					+ " WHERE S_ORGCODE= ? AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND S_OPERATIONTYPECODE= ? )";
			// �����������
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
			sqlExec.addParam(loginfo.getSorgcode());
			// ���ű�־
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
			sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY);
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
	}}
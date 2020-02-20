package com.cfcc.itfe.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author ���а���֧��ֱ���ύ
 */

public class  DirectPayreckBankCommitUtil{
	private static Log logger = LogFactory.getLog(DirectPayreckBankCommitUtil.class);
			
	/**
	 * ���а���֧��
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmPayreckBank(String bizType, ITFELoginInfo loginfo)
			throws ITFEBizException {
		boolean bResult;
		/**
		 * �ж��Ƿ���δ��¼�к���Ϣ
		 */
		if(StateConstant.IF_MATCHBNKNAME_YES.equals(ITFECommonConstant.ISMATCHBANKNAME)) {
			bResult = CheckBizParamForSH.checkIsMatchNameOver(bizType, null,loginfo);
			if(!bResult) {
				return false;
			}
		}
		
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

			String movedataSql =" INSERT INTO TV_PAYRECK_BANK" 
				+"	(I_VOUSRLNO,D_ENTRUSTDATE,S_PACKNO,S_AGENTBNKCODE,S_TRANO,S_FINORGCODE,"
				+"	S_TRECODE,S_BOOKORGCODE,S_VOUNO,D_VOUDATE,S_PAYERACCT,S_PAYERNAME,S_PAYERADDR,"
				+"	S_PAYEEACCT,S_PAYEENAME,S_PAYEEADDR,S_PAYEEOPBKNO,S_ADDWORD,S_BUDGETTYPE,"
				+"	S_TRIMSIGN,S_OFYEAR,F_AMT,I_STATINFNUM,S_RESULT,S_PROCSTAT,D_ACCEPTDATE,TS_UPDATE,S_PAYMODE,S_FILENAME) "
				+"	select I_VOUSRLNO, "
				+"	substr(CHAR(D_ACCEPT),1,4)||'-'||substr(CHAR(D_ACCEPT),6,2)||'-'||substr(CHAR(D_ACCEPT),9,2),S_PACKNO," 
				+"	S_AGENTBNKCODE,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8)," 
				+"  (SELECT MAX(S_FINORGCODE) FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)," 
				+"  S_TRECODE,S_BOOKORGCODE,S_VOUNO,substr(CHAR(D_VOUCHER),1,4)||'-'||substr(CHAR(D_VOUCHER),6,2)||'-'||substr(CHAR(D_VOUCHER),9,2)," 
				+"  S_PAYERACCT,S_PAYERNAME,coalesce(S_PAYERADDR,''),S_PAYEEACCT,S_PAYEENAME,coalesce(S_PAYEEADDR,''),S_PAYEEOPNBNKNO,coalesce(S_ADDWORD,'')," 
				+"  C_BDGKIND,coalesce(C_TRIMFLAG,''),substr(CHAR(D_VOUCHER),1,4),F_ZEROSUMAMT+F_SMALLSUMAMT,0,'"
				+ 	DealCodeConstants.DEALCODE_ITFE_DEALING
				+"',S_STATE," 
				+"	substr(CHAR(D_ACCEPT),1,4)||'-'||substr(CHAR(D_ACCEPT),6,2)||'-'||substr(CHAR(D_ACCEPT),9,2),CURRENT TIMESTAMP,TRANSBIZTYPE(S_BIZTYPE),S_FILENAME  "
				+"	FROM  TBS_TV_BNKPAY_MAIN A WHERE S_BOOKORGCODE=? AND (S_STATE IS NULL OR S_STATE= ?)";
			
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
				movedataSql+=" AND ( S_BIZTYPE = ? OR S_BIZTYPE = ? ) ";
			}else{
				movedataSql+=" AND S_BIZTYPE = ? ";
			}
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// ����״̬
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY);
			}else{
				sqlExec.addParam(bizType);
			}
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ��ʱ��д���ӱ���ʽ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "INSERT INTO TV_PAYRECK_BANK_LIST("
				+ " I_VOUSRLNO,I_SEQNO,"
				+ " S_BDGORGCODE,S_FUNCBDGSBTCODE,S_ECNOMICSUBJECTCODE,F_AMT,S_ACCTPROP,TS_UPDATE)"
				+ " SELECT A.I_VOUSRLNO,"
				+ " I_GRPINNERSEQNO,"
				+ " A.S_BDGORGCODE,A.S_FUNCSBTCODE,coalesce(A.S_ECOSBTCODE,''),A.F_AMT,A.C_ACCTPROP,"
				+ " CURRENT TIMESTAMP "
				+ " FROM TBS_TV_BNKPAY_SUB A,TBS_TV_BNKPAY_MAIN B"
				+ " WHERE A.S_BOOKORGCODE=B.S_BOOKORGCODE"
				+ " AND A.I_VOUSRLNO=B.I_VOUSRLNO"
				+ " AND B.S_BOOKORGCODE= ? AND (B.S_STATE IS NULL OR B.S_STATE= ? )";
			
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
				movedataSql+=" AND ( B.S_BIZTYPE = ? OR B.S_BIZTYPE = ? ) ";
			}else{
				movedataSql+=" AND B.S_BIZTYPE = ? ";
			}
			
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// ����״̬
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
			
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY);
			}else{
				sqlExec.addParam(bizType);
			}
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * �޸����ű�־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_BNKPAY_MAIN SET S_STATE=? WHERE S_BOOKORGCODE= ? AND (S_STATE IS NULL OR S_STATE= ? )";
			
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
				movedataSql+=" AND ( S_BIZTYPE = ? OR S_BIZTYPE = ? ) ";
			}else{
				movedataSql+=" AND S_BIZTYPE = ? ";
			}
			
			// �����������
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// ���ű�־
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
			
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY);
			}else{
				sqlExec.addParam(bizType);
			}
			
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ��TIPS���ͱ���
			 */
			// ������ˮ�ŷ���TIPS
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql = "";
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
			    strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE,S_OPERATIONTYPECODE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? "
				       + " AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND ( S_OPERATIONTYPECODE= ? or S_OPERATIONTYPECODE= ? ) ";
			}else{
				strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE,S_OPERATIONTYPECODE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? "
				       + " AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND S_OPERATIONTYPECODE= ? ";
			}
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// ���ű�־
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
			// ҵ�����ͣ����а���֧����������������ҵ�����ͣ�
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY);
			}else{
				sqlExec.addParam(bizType);
			}
			SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
			int row = rsfilepac.getRowCount();
			for (int i = 0; i < row; i++) {
				// // ������ˮ�ŷ���TIPS
				sendMsgUtil.sendMsg(rsfilepac.getString(i, 2), loginfo.getSorgcode(), rsfilepac
						.getString(i, 0), MsgConstant.APPLYPAY_DAORU, rsfilepac
						.getString(i, 1), false);
			}
			/**
			 * �޸��ļ������ˮ��Ӧ��ϵ��(�ѷ��ͣ�������)
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
				movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=?"
					+ " WHERE S_ORGCODE= ? AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND ( S_OPERATIONTYPECODE= ? or S_OPERATIONTYPECODE= ? )";
			}else{
				movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=?"
					+ " WHERE S_ORGCODE= ? AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND S_OPERATIONTYPECODE= ? ";
			}
			// �����������
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
			sqlExec.addParam(loginfo.getSorgcode());
			// ���ű�־
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)){
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY);
			}else{
				sqlExec.addParam(bizType);
			}
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
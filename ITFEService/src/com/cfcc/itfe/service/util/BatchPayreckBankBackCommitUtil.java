package com.cfcc.itfe.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author  ���а���֧�����������˻�
 * @time 12-02-21 08:45:49 �������ţ�������ţ�ֱ���ύ��һЩ�жϵ�
 */

public class BatchPayreckBankBackCommitUtil {
	private static Log logger = LogFactory
			.getLog(BatchPayreckBankBackCommitUtil.class);

	/**
	 * ���а���֧���������룺��������-ȷ���ύ
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmPayreckBackBank(String bizType, TvFilepackagerefDto idto,
			ITFELoginInfo loginfo,IDto _dto) throws ITFEBizException {
		String fileName = idto.getSfilename();
		
		boolean bResult;
		
		/**
		 * �ж��Ƿ���δ��¼�к���Ϣ
		 */
		if(StateConstant.IF_MATCHBNKNAME_YES.equals(ITFECommonConstant.ISMATCHBANKNAME)) {
			bResult = CheckBizParamForSH.checkIsMatchNameOver(bizType, idto,loginfo);
			if(!bResult) {
				return false;
			}
		}
		
		/**
		 * �ж��Ƿ�ά������������Ϣ
		 */
		bResult = CheckBizParam.checkConvertFinOrg(bizType, idto, loginfo);
		if (!bResult) {
			return false;
		}

		TvPayreckBankBackDto payreckdto=(TvPayreckBankBackDto) _dto;
		SQLExecutor sqlExec = null;
		try {
			/**
			 * ��ʱ��д��������ʽ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			String movedataSql = " INSERT INTO tv_payreck_bank_back" 
				+"	(I_VOUSRLNO,D_ENTRUSTDATE,S_PACKNO,S_AGENTBNKCODE,S_TRANO,S_VOUNO,D_VOUDATE,S_FINORGCODE,S_TRECODE,S_BOOKORGCODE,S_ORITRANO," 
				+"  D_ORIENTRUSTDATE,S_ORIVOUNO,D_ORIVOUDATE,S_PAYERACCT,S_PAYERNAME,S_PAYEEACCT,S_PAYEENAME,S_PAYDICTATENO,S_PAYMSGNO," 
				+"  D_PAYENTRUSTDATE,S_PAYSNDBNKNO,S_BUDGETTYPE,S_TRIMSIGN,S_OFYEAR,F_AMT,I_STATINFNUM,S_STATUS,D_ACCEPTDATE,TS_UPDATE,S_PAYMODE,S_FILENAME) "
				+"	select A.I_VOUSRLNO, "
				+"	substr(CHAR(D_ACCEPT),1,4)||'-'||substr(CHAR(D_ACCEPT),6,2)||'-'||substr(CHAR(D_ACCEPT),9,2),S_PACKNO," 
				+"	S_AGENTBNKCODE,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),S_VOUNO," 
				+"  substr(CHAR(D_VOUCHER),1,4)||'-'||substr(CHAR(D_VOUCHER),6,2)||'-'||substr(CHAR(D_VOUCHER),9,2), " 
				+"  (SELECT MAX(S_FINORGCODE) FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)," 
				+"  A.S_TRECODE,A.S_BOOKORGCODE,'',substr(CHAR(D_ACCEPT),1,4)||'-'||substr(CHAR(D_ACCEPT),6,2)||'-'||substr(CHAR(D_ACCEPT),9,2),"
				+"  A.S_ORIVOUNO,A.D_ORIVOUDATE,S_PAYEEACCT,S_PAYEENAME,S_PAYERACCT,S_PAYERNAME,S_PAYDICTATENO,S_PAYMSGNO,D_PAYENTRUSTDATE,S_PAYSNDBNKNO,"
				+"  C_BDGKIND,C_TRIMFLAG,substr(CHAR(D_VOUCHER),1,4),F_ZEROSUMAMT+F_SMALLSUMAMT,"
				+"  I_CHGNUM,'"
				+	DealCodeConstants.DEALCODE_ITFE_DEALING
				+"',substr(CHAR(D_ACCEPT),1,4)||'-'||substr(CHAR(D_ACCEPT),6,2)||'-'||substr(CHAR(D_ACCEPT),9,2),CURRENT TIMESTAMP,TRANSBIZTYPE(S_BIZTYPE),S_FILENAME"
			    +"	FROM  TBS_TV_BNKPAY_MAIN A  WHERE   A.S_BOOKORGCODE=? AND A.S_FILENAME=? AND (A.S_STATE IS NULL OR A.S_STATE= ?)";
			
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
//			sqlExec.addParam(idto.getStrecode());
			// ����״̬
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ��ʱ��д���ӱ���ʽ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql ="INSERT INTO TV_PAYRECK_BANK_BACK_LIST("
				+ " I_VOUSRLNO,I_SEQNO,S_ORIVOUNO,D_ORIVOUDATE,"
				+ " S_BDGORGCODE,S_FUNCBDGSBTCODE,S_ECNOMICSUBJECTCODE,F_AMT,S_ACCTPROP,TS_UPDATE)"
				+ " SELECT A.I_VOUSRLNO "
				+ " ,i_GrpInnerSeqNo,A.S_ORIVOUNO,A.D_ORIVOUDATE,"
				+ " A.S_BDGORGCODE,A.S_FUNCSBTCODE,A.S_ECOSBTCODE,A.F_AMT,A.C_ACCTPROP,"
				+ " CURRENT TIMESTAMP "
				+ " FROM TBS_TV_BNKPAY_SUB A,TBS_TV_BNKPAY_MAIN B"
				+ " WHERE A.I_VOUSRLNO=B.I_VOUSRLNO"
				+ " AND B.S_BOOKORGCODE=? AND B.S_FILENAME=?  AND (B.S_STATE IS NULL OR B.S_STATE= ?)";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
//			sqlExec.addParam(idto.getStrecode());
			// ����״̬
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * �޸����ű�־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_BNKPAY_MAIN SET S_STATE=? WHERE S_BOOKORGCODE= ? AND S_FILENAME= ?  ";
			// �����������
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// �ļ���
			sqlExec.addParam(fileName);
			// ��������
//			sqlExec.addParam(idto.getStrecode());
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ��TIPS���ͱ���
			 */
			// �鿴�ļ�����Ӧ�İ���ˮ����������ʱ�����Ƿ����δ���ŵ�����
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql = "SELECT count(1) FROM TBS_TV_BNKPAY_MAIN A "
					+ " WHERE A.S_BOOKORGCODE= ?  AND (S_STATE IS NULL OR S_STATE= ?) "
					+ " AND EXISTS (SELECT 1 FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= A.S_BOOKORGCODE "
					+ " AND S_PACKAGENO=A.S_PACKNO AND S_FILENAME= ?  )";
			// �����������
			sqlExec.addParam(loginfo.getSorgcode());
			// ���ű�־
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			// �����ļ���
			sqlExec.addParam(fileName);
			// ��������
//			sqlExec.addParam(idto.getStrecode());
			SQLResults rs = sqlExec.runQueryCloseCon(strsql);
			// ������δ���ŵ�����
			if (rs.getInt(0, 0) == 0) {
				// ������ˮ�ŷ���TIPS
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? ";
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(fileName);
				// ��������
//				sqlExec.addParam(idto.getStrecode());
				SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
				int row = rsfilepac.getRowCount();
				for (int i = 0; i < row; i++) {
					// // ������ˮ�ŷ���TIPS
					sendMsgUtil.sendMsg(fileName, loginfo.getSorgcode(),
							rsfilepac.getString(i, 0), MsgConstant.APPLYPAY_BACK_DAORU,
							rsfilepac.getString(i, 1), false);

					/**
					 * �޸��ļ������ˮ��Ӧ��ϵ��(�ѷ���)
					 */
					sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_RETCODE=? WHERE S_ORGCODE= ? AND S_PACKAGENO= ? ";
					// �����������
					sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
					sqlExec.addParam(loginfo.getSorgcode());
					// ����ˮ��
					sqlExec.addParam(rsfilepac.getString(i, 0));
					sqlExec.runQueryCloseCon(movedataSql);
				}
			}

			/**
			 * �޸��ļ������ˮ��Ӧ��ϵ��(������)
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=? WHERE S_ORGCODE= ? AND S_FILENAME= ?  ";
			// �����������
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// �����ļ���
			sqlExec.addParam(fileName);
			// ��������
//			sqlExec.addParam(idto.getStrecode());
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
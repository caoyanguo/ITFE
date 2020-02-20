package com.cfcc.itfe.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author ���޹� ���������˻�
 * @time 12-02-21 08:45:49  �������ţ�������ţ�ֱ���ύ��һЩ�жϵ�
 */

public class  EachPayreckBackBankCommitUtil{
	private static Log logger = LogFactory.getLog(EachPayreckBackBankCommitUtil.class);
			
	/**
	 * ���������˻أ���������-ȷ���ύ
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmPayreckBackBank(String bizType, IDto idto,ITFELoginInfo loginfo,IDto _dto)
			throws ITFEBizException {
		boolean bResult;

		/**
		 * �ж��Ƿ���δ��¼�к���Ϣ
		 */
		if(StateConstant.IF_MATCHBNKNAME_YES.equals(ITFECommonConstant.ISMATCHBANKNAME)) {
			bResult = CheckBizParamForSH.checkIsMatchNameOverEach(bizType, idto,loginfo);
			if(!bResult) {
				return false;
			}
		}
		
		/**
		 * �ж��Ƿ�ά������������Ϣ
		 */
		bResult = CheckBizParam.checkConvertFinOrg(bizType, idto);
		if (!bResult) {
			return false;
		}
		String strVousrlno = String.valueOf(((TbsTvBnkpayMainDto) idto)
				.getIvousrlno());
		TvPayreckBankBackDto payreckdto=(TvPayreckBankBackDto) _dto;
		SQLExecutor sqlExec = null;
		try {
			/**
			 * ������ʱ��д��������ʽ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String movedataSql =" INSERT INTO tv_payreck_bank_back" 
				+"	(I_VOUSRLNO,D_ENTRUSTDATE,S_PACKNO,S_AGENTBNKCODE,S_TRANO,S_VOUNO,D_VOUDATE,S_FINORGCODE,S_TRECODE,S_BOOKORGCODE,S_ORITRANO," 
				+"  D_ORIENTRUSTDATE,S_ORIVOUNO,D_ORIVOUDATE,S_PAYERACCT,S_PAYERNAME,S_PAYEEACCT,S_PAYEENAME,S_PAYDICTATENO,S_PAYMSGNO," 
				+"  D_PAYENTRUSTDATE,S_PAYSNDBNKNO,S_BUDGETTYPE,S_TRIMSIGN,S_OFYEAR,F_AMT,I_STATINFNUM,S_STATUS,D_ACCEPTDATE,TS_UPDATE,S_PAYMODE,S_FILENAME) "
				+"	select  A.I_VOUSRLNO,"
				+"	substr(CHAR(D_ACCEPT),1,4)||'-'||substr(CHAR(D_ACCEPT),6,2)||'-'||substr(CHAR(D_ACCEPT),9,2),S_PACKNO," 
				+"	S_AGENTBNKCODE,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),S_VOUNO," 
				+"  substr(CHAR(D_VOUCHER),1,4)||'-'||substr(CHAR(D_VOUCHER),6,2)||'-'||substr(CHAR(D_VOUCHER),9,2), " 
				+"  (SELECT MAX(S_FINORGCODE) FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)," 
				+"  S_TRECODE,A.S_BOOKORGCODE,'',substr(CHAR(D_ACCEPT),1,4)||'-'||substr(CHAR(D_ACCEPT),6,2)||'-'||substr(CHAR(D_ACCEPT),9,2),"
				+"  A.S_ORIVOUNO,A.D_ORIVOUDATE,S_PAYEEACCT,S_PAYEENAME,S_PAYERACCT,S_PAYERNAME,S_PAYDICTATENO,S_PAYMSGNO,D_PAYENTRUSTDATE,S_PAYSNDBNKNO,"
				+"  C_BDGKIND,C_TRIMFLAG,substr(CHAR(D_VOUCHER),1,4),F_ZEROSUMAMT+F_SMALLSUMAMT,"
				+"  I_CHGNUM,'"
				+	DealCodeConstants.DEALCODE_ITFE_DEALING
				+"',substr(CHAR(D_ACCEPT),1,4)||'-'||substr(CHAR(D_ACCEPT),6,2)||'-'||substr(CHAR(D_ACCEPT),9,2),CURRENT TIMESTAMP,TRANSBIZTYPE(S_BIZTYPE),S_FILENAME "
			    +"	FROM  TBS_TV_BNKPAY_MAIN A  WHERE A.I_VOUSRLNO=? ";
		
			// �ؼ��֣�ƾ֤��ˮ��
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * �ӱ���ʱ��д���ӱ���ʽ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "INSERT INTO TV_PAYRECK_BANK_BACK_LIST("
				+ " I_VOUSRLNO,I_SEQNO,S_ORIVOUNO,D_ORIVOUDATE,"
				+ " S_BDGORGCODE,S_FUNCBDGSBTCODE,S_ECNOMICSUBJECTCODE,F_AMT,S_ACCTPROP,TS_UPDATE)"
				+ " SELECT "
				+ " A.I_VOUSRLNO,A.i_GrpInnerSeqNo,A.S_ORIVOUNO,A.D_ORIVOUDATE,"
				+ " A.S_BDGORGCODE,A.S_FUNCSBTCODE,A.S_ECOSBTCODE,A.F_AMT,A.C_ACCTPROP,"
				+ " CURRENT TIMESTAMP "
				+ " FROM TBS_TV_BNKPAY_SUB A,TBS_TV_BNKPAY_MAIN B"
				+ " where A.I_VOUSRLNO=B.I_VOUSRLNO"
				+ " AND B.I_VOUSRLNO= ? ";

			// �ؼ��֣�ƾ֤��ˮ��
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * �޸���ʱ��ü�¼���ű�־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_BNKPAY_MAIN SET S_STATE=? WHERE I_VOUSRLNO= ? ";
			// �ؼ��֣�ƾ֤��ˮ��
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			// ���÷��ͱ���
			sendMsgUtil.checkAndSendMsg((TbsTvBnkpayMainDto) idto,
					MsgConstant.APPLYPAY_BACK_DAORU, TbsTvBnkpayMainDto.tableName(), null,loginfo);
			
		} catch (JAFDatabaseException e) {
			logger.error("���ݿ�����쳣!", e);
			throw new ITFEBizException("���ݿ�����쳣!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}	}
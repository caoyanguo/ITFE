package com.cfcc.itfe.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author ���޹� ʵ���ʽ�
 * @time 12-02-21 08:45:49  �������ţ�������ţ�ֱ���ύ��һЩ�жϵ�
 */

public class  EachPBCPayOutCommitUtil{
	private static Log logger = LogFactory.getLog(EachPBCPayOutCommitUtil.class);
			
	/**
	 * ʵ���ʽ���������-ȷ���ύ
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmPbcPayout(String bizType, IDto idto,ITFELoginInfo loginfo)
			throws ITFEBizException {
		boolean bResult;

		/**
		 * �ж��Ƿ�ά������������Ϣ
		 */
		bResult = CheckBizParam.checkConvertFinOrg(bizType, idto);
		if (!bResult) {
			return false;
		}
		String strVousrlno = String.valueOf(((TbsTvPbcpayDto) idto)
				.getIvousrlno());
		SQLExecutor sqlExec = null;
		try {
			/**
			 * ������ʱ��д��������ʽ��
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
				+"	FROM  TBS_TV_PBCPAY A WHERE I_VOUSRLNO=? ";

			// �ؼ��֣�ƾ֤��ˮ��
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * �ӱ���ʱ��д���ӱ���ʽ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "INSERT INTO TV_PBCPAY_SUB (I_VOUSRLNO,I_SEQNO,S_BDGORGCODE,S_FUNCSBTCODE,S_ECOSBTCODE,C_ACCTPROP,F_AMT,TS_SYSUPDATE)"
				+"SELECT I_VOUSRLNO   "
				+",1,S_BDGORGCODE,S_FUNCSBTCODE,S_ECOSBTCODE,'',F_AMT,CURRENT TIMESTAMP "
				+"FROM TBS_TV_PBCPAY WHERE I_VOUSRLNO= ? ";

			// �ؼ��֣�ƾ֤��ˮ��
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * �޸���ʱ��ü�¼���ű�־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_PBCPAY SET S_STATUS=? WHERE I_VOUSRLNO= ? ";
			// �ؼ��֣�ƾ֤��ˮ��
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			// ���÷��ͱ���
			sendMsgUtil.checkAndSendMsg((TbsTvPbcpayDto) idto,
					MsgConstant.MSG_NO_5104, TbsTvPbcpayDto.tableName(), null,loginfo);
			
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
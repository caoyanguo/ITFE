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
 * @author ���޹� ʵ���ʽ�
 * @time 12-02-21 08:45:49  �������ţ�������ţ�ֱ���ύ��һЩ�жϵ�
 */

public class  EachPayOutCommitUtil{
	private static Log logger = LogFactory.getLog(EachPayOutCommitUtil.class);
			
	/**
	 * ʵ���ʽ��������-ȷ���ύ
	 * 
	 * @param bizType
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmPayout(String bizType, IDto idto,ITFELoginInfo loginfo)
			throws ITFEBizException {
		boolean bResult;

		/**
		 * �ж��Ƿ�ά������������Ϣ
		 */
		bResult = CheckBizParam.checkConvertFinOrg(bizType, idto);
		if (!bResult) {
			return false;
		}
		/**
		 * �ж��տ����к��Ƿ�Ϊ��
		 */
		bResult = CheckBizParam.checkPayeeBanknoForEach(bizType, idto, loginfo);
		if (!bResult) {
			return false;
		}
		String strVousrlno = String.valueOf(((TbsTvPayoutDto) idto)
				.getIvousrlno());
		SQLExecutor sqlExec = null;
		try {
			/**
			 * ������ʱ��д��������ʽ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String movedataSql = "INSERT INTO TV_PAYOUTMSGMAIN"
					+ "(S_BIZNO,S_ORGCODE,S_COMMITDATE,S_ACCDATE,S_FILENAME,"
					+ "S_TRECODE,S_PACKAGENO,S_PAYUNIT,S_PAYEEBANKNO,S_DEALNO,"
					+ "S_TAXTICKETNO,S_GENTICKETDATE,S_PAYERACCT,S_PAYERNAME,S_PAYERADDR,"
					+ "N_MONEY,S_RECBANKNO,S_RECACCT,S_RECNAME,S_TRIMFLAG,"
					+ "S_BUDGETUNITCODE,S_UNITCODENAME,S_OFYEAR,S_BUDGETTYPE,S_STATUS,"
					+ "S_USERCODE,S_ADDWORD,S_DEMO,S_BACKFLAG,TS_SYSUPDATE)"
					+ " SELECT "
					+ " TRIM(CHAR(I_VOUSRLNO)),S_BOOKORGCODE,"
					+ "substr(CHAR(D_Accept),1,4)||substr(CHAR(D_Accept),6,2)||substr(CHAR(D_Accept),9,2),"
					+ "substr(CHAR(D_Accept),1,4)||substr(CHAR(D_Accept),6,2)||substr(CHAR(D_Accept),9,2),"
					+ "S_FILENAME,S_TRECODE,S_PACKAGENO,"
					+ "(SELECT Max(S_FINORGCODE) FROM TS_CONVERTFINORG"
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE),"
					+ "S_PAYEEBANKNO,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),S_VOUNO,"
					+ "substr(CHAR(D_VOUCHER),1,4)||substr(CHAR(D_VOUCHER),6,2)||substr(CHAR(D_VOUCHER),9,2),"
					+ "S_PAYERACCT,S_PAYERNAME,'',F_AMT,S_PAYEEOPNBNKNO,"
					+ "S_PAYEEACCT,S_PAYEENAME,C_TRIMFLAG,S_BDGORGCODE,S_BDGORGNAME,"
					+ "TRIM(CHAR(I_OFYEAR)),C_BDGKIND,'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING + "','"
					+ loginfo.getSuserCode()
					+ "',S_ADDWORD,S_MOVEFUNDREASON,'0',CURRENT TIMESTAMP "
					+ " FROM TBS_TV_PAYOUT A WHERE I_VOUSRLNO= ? ";

			// �ؼ��֣�ƾ֤��ˮ��
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * �ӱ���ʱ��д���ӱ���ʽ��
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "INSERT INTO TV_PAYOUTMSGSUB "
					+ "(S_BIZNO,S_SEQNO,S_ACCDATE,S_ECNOMICSUBJECTCODE,"
					+ "S_BUDGETPRJCODE,N_MONEY,S_FUNSUBJECTCODE,TS_SYSUPDATE)"
					+ " SELECT "
					+ "TRIM(CHAR(I_VOUSRLNO)),1,"
					+ "substr(CHAR(D_Accept),1,4)||substr(CHAR(D_Accept),6,2)||substr(CHAR(D_Accept),9,2)"
					+ ",S_ECOSBTCODE,'',F_AMT,S_FUNCSBTCODE,CURRENT TIMESTAMP"
					+ " FROM TBS_TV_PAYOUT WHERE I_VOUSRLNO= ? ";

			// �ؼ��֣�ƾ֤��ˮ��
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * �޸���ʱ��ü�¼���ű�־
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_PAYOUT SET S_STATUS=? WHERE I_VOUSRLNO= ? ";
			// �ؼ��֣�ƾ֤��ˮ��
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);

			// ���÷��ͱ���
			sendMsgUtil.checkAndSendMsg((TbsTvPayoutDto) idto,
					MsgConstant.MSG_NO_5101, TbsTvPayoutDto.tableName(), null,loginfo);
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
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
 * @author ~{2\Q^9z~} ~{J52&WJ=p~}
 * @time 12-02-21 08:45:49 ~{EzA?Oz:E#,Vp1JOz:E#,V1=SLa=;#,R;P)EP6O5H~}
 */

public class DirectDirectPayCommitUtil {
	private static Log logger = LogFactory
			.getLog(DirectDirectPayCommitUtil.class);

	/**
	 * ~{V1=SV'866n6H#:V1=SLa=;~}
	 * 
	 * @param bizType
	 * @throws ITFEBizException
	 */
	static boolean confirmDircetPay(String bizType, ITFELoginInfo loginfo)
			throws ITFEBizException {
		boolean bResult;
		/**
		 * ~{EP6OJG7qN,;$2FU~;z99PEO"~}
		 */
		bResult = CheckBizParam.checkConvertFinOrg(bizType, null, loginfo);
		if (!bResult) {
			return false;
		}
		/**
		 * ~{EP6OJG7qN,;$4z@m4zBkSkV'86PP:E6TS&9XO5~}
		 */
		if(ITFECommonConstant.ISMATCHBANKNAME.equals(StateConstant.IF_MATCHBNKNAME_YES)) { //~{IO:#~}
			bResult = CheckBizParamForSH.checkGenbankandreckbank(bizType, null, loginfo);
		}else {
			bResult = CheckBizParam.checkGenbankandreckbank(bizType, null, loginfo);
		}
		
		if (!bResult) {
			return false;
		}
		SQLExecutor sqlExec = null;
		try {
			/**
			 * ~{Vw1mAYJ11mP4HkVw1mU}J=1m~}
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			//~{EP6OJG7qPhR*=xPP29B<PP:E~}(~{IO:#~})
			String movedataSql = "";
			
		
			
			if(ITFECommonConstant.ISMATCHBANKNAME.equals(StateConstant.IF_MATCHBNKNAME_NO)) {
				movedataSql = "INSERT INTO TV_DIRECTPAYMSGMAIN("
						+ "I_VOUSRLNO,S_ORGCODE,S_COMMITDATE,S_ACCDATE,S_FILENAME,"
						+ "S_PACKAGENO,S_TRECODE,S_PAYUNIT,S_TRANSBANKCODE,"
						+ "S_DEALNO,N_MONEY,S_TAXTICKETNO,S_PACKAGETICKETNO,S_GENTICKETDATE,"
						+ "S_BUDGETTYPE,S_OFYEAR,S_TRANSACTUNIT,S_AMTTYPE,S_STATUS,"
						+ "S_USERCODE,S_DEMO,TS_SYSUPDATE,S_PAYACCTNO,S_PAYACCTNAME,S_PAYBANKNO,S_PAYEEACCTNO,S_PAYEEACCTNAME)"
						+ " SELECT "
						+ "I_VOUSRLNO,S_BOOKORGCODE,"
						+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
						+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
						+ "S_FILENAME,S_PACKAGENO,S_TRECODE,(SELECT Max(S_FINORGCODE) FROM TS_CONVERTFINORG "
						+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE),'',"
						+ "substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),F_AMT,S_VOUNO,S_VOUNO,"
						+ "substr(CHAR(D_VOUCHER),1,4)||substr(CHAR(D_VOUCHER),6,2)||substr(CHAR(D_VOUCHER),9,2),"
						+ "C_BDGKIND,TRIM(CHAR(I_OFYEAR)),"
						+" CASE (SELECT count(*) FROM TS_GENBANKANDRECKBANK WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO AND S_TRECODE=A.S_TRECODE)"
						+" WHEN 0 THEN (SELECT max(S_RECKBANKCODE) FROM TS_GENBANKANDRECKBANK WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO)"
						+" ELSE (SELECT max(S_RECKBANKCODE) FROM TS_GENBANKANDRECKBANK WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO AND S_TRECODE=A.S_TRECODE)"
						+" end,"
//						+ " Case S_PAYEEOPNBNKNO WHEN s_trecode THEN S_PAYEEOPNBNKNO ELSE "
//						+ "	(SELECT Max(S_RECKBANKCODE) FROM Ts_Genbankandreckbank "
//						+ "   WHERE S_BOOKORGCODE=A.S_BOOKORGCODE "
//						+ "   AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO) "
//						+ " END,"
						+ "  Case S_PAYEEOPNBNKNO WHEN s_trecode THEN '0' ELSE '1' END,'"
						+ DealCodeConstants.DEALCODE_ITFE_DEALING
						+ "','"
						+ loginfo.getSuserCode()
						+ "','',CURRENT TIMESTAMP,S_PAYERACCT,'',S_PAYEEOPNBNKNO,S_PAYEEACCT,S_PAYEENAME"
						+ " FROM TBS_TV_DIRECTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
				// ~{:KKcVwLe4zBk~}
				sqlExec.addParam(loginfo.getSorgcode());
				// ~{Oz:E1jV>~}
				sqlExec
						.addParam(StateConstant.CONFIRMSTATE_NO
								.replaceAll("\"", ""));
				sqlExec.runQueryCloseCon(movedataSql);
			}else {
				//1~{!"JWOH2YWw2;PhR*29B<PPC{5DPEO"#,UbP)PEO"PhR*=xPPV'86O5M3PP:EW*;;~}
				movedataSql = "INSERT INTO TV_DIRECTPAYMSGMAIN("
					+ "I_VOUSRLNO,S_ORGCODE,S_COMMITDATE,S_ACCDATE,S_FILENAME,"
					+ "S_PACKAGENO,S_TRECODE,S_PAYUNIT,S_TRANSBANKCODE,"
					+ "S_DEALNO,N_MONEY,S_TAXTICKETNO,S_PACKAGETICKETNO,S_GENTICKETDATE,"
					+ "S_BUDGETTYPE,S_OFYEAR,S_TRANSACTUNIT,S_AMTTYPE,S_STATUS,"
					+ "S_USERCODE,S_DEMO,TS_SYSUPDATE,S_PAYACCTNO,S_PAYACCTNAME,S_PAYBANKNO,S_PAYEEACCTNO,S_PAYEEACCTNAME)"
					+ " SELECT "
					+ "I_VOUSRLNO,S_BOOKORGCODE,"
					+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
					+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
					+ "S_FILENAME,S_PACKAGENO,S_TRECODE,(SELECT Max(S_FINORGCODE) FROM TS_CONVERTFINORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE),'',"
					+ "substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),F_AMT,S_VOUNO,S_VOUNO,"
					+ "substr(CHAR(D_VOUCHER),1,4)||substr(CHAR(D_VOUCHER),6,2)||substr(CHAR(D_VOUCHER),9,2),"
					+ "C_BDGKIND,TRIM(CHAR(I_OFYEAR)),"
					+" CASE (SELECT count(*) FROM TS_GENBANKANDRECKBANK WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO AND S_TRECODE=A.S_TRECODE)"
					+" WHEN 0 THEN (SELECT max(S_RECKBANKCODE) FROM TS_GENBANKANDRECKBANK WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO)"
					+" ELSE (SELECT max(S_RECKBANKCODE) FROM TS_GENBANKANDRECKBANK WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO AND S_TRECODE=A.S_TRECODE)"
					+" end,"
//					+ " Case S_PAYEEOPNBNKNO WHEN s_trecode THEN S_PAYEEOPNBNKNO ELSE "
//					+ "	(SELECT Max(S_RECKBANKCODE) FROM Ts_Genbankandreckbank "
//					+ "   WHERE S_BOOKORGCODE=A.S_BOOKORGCODE "
//					+ "   AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO) "
//					+ " END,"
					+ "  Case S_PAYEEOPNBNKNO WHEN s_trecode THEN '0' ELSE '1' END,'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "','"
					+ loginfo.getSuserCode()
					+ "','',CURRENT TIMESTAMP,S_PAYERACCT,'',S_PAYEEOPNBNKNO,S_PAYEEACCT,S_PAYEENAME"
					+ " FROM TBS_TV_DIRECTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
					+ " AND (S_IFMATCH IS NULL OR S_IFMATCH =?)"; 
				// ~{:KKcVwLe4zBk~}
				sqlExec.addParam(loginfo.getSorgcode());
				// ~{Oz:E1jV>~}
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_NO);
				sqlExec.runQuery(movedataSql);
				
				sqlExec.clearParams();
				//2~{!"H;:s6TDGP)PhR*29B<PPC{5D=xPP2YWw#,UbP)PEO"SC29B<5DPP:E<4?I#!~}
				movedataSql = "INSERT INTO TV_DIRECTPAYMSGMAIN("
					+ "I_VOUSRLNO,S_ORGCODE,S_COMMITDATE,S_ACCDATE,S_FILENAME,"
					+ "S_PACKAGENO,S_TRECODE,S_PAYUNIT,S_TRANSBANKCODE,"
					+ "S_DEALNO,N_MONEY,S_TAXTICKETNO,S_PACKAGETICKETNO,S_GENTICKETDATE,"
					+ "S_BUDGETTYPE,S_OFYEAR,S_TRANSACTUNIT,S_AMTTYPE,S_STATUS,"
					+ "S_USERCODE,S_DEMO,TS_SYSUPDATE)"
					+ " SELECT "
					+ "I_VOUSRLNO,S_BOOKORGCODE,"
					+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
					+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
					+ "S_FILENAME,S_PACKAGENO,S_TRECODE,(SELECT Max(S_FINORGCODE) FROM TS_CONVERTFINORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE),'',"
					+ "substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),F_AMT,S_VOUNO,S_VOUNO,"
					+ "substr(CHAR(D_VOUCHER),1,4)||substr(CHAR(D_VOUCHER),6,2)||substr(CHAR(D_VOUCHER),9,2),"
					+ "C_BDGKIND,TRIM(CHAR(I_OFYEAR)),"
					+ " S_PAYEEOPNBNKNO ,"
					+ "  Case S_PAYEEOPNBNKNO WHEN s_trecode THEN '0' ELSE '1' END,'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "','"
					+ loginfo.getSuserCode()
					+ "','',CURRENT TIMESTAMP"
					+ " FROM TBS_TV_DIRECTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
					+ " AND  S_IFMATCH =?"; 
				// ~{:KKcVwLe4zBk~}
				sqlExec.addParam(loginfo.getSorgcode());
				// ~{Oz:E1jV>~}
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				sqlExec.runQuery(movedataSql);
			}
			/**
			 * ~{WS1mAYJ11mP4HkWS1mU}J=1m~}
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "INSERT INTO TV_DIRECTPAYMSGSUB("
					+ "I_VOUSRLNO,I_DETAILSEQNO,S_ORGCODE,S_PACKAGENO,S_BUDGETUNITCODE,"
					+ "S_TAXTICKETNO,S_LINE,S_OFYEAR,I_NOAFTERPACKAGE,S_FUNSUBJECTCODE,"
					+ "S_ECOSUBJECTCODE,N_MONEY,S_ACCDATE,S_USERCODE,S_DEMO,TS_SYSUPDATE)"
					+ " SELECT "
					+ "A.I_VOUSRLNO,A.I_DETAILSEQNO,A.S_BOOKORGCODE,B.S_PACKAGENO,A.S_BDGORGCODE,"
					+ "B.S_VOUNO,TRIM(CHAR(A.I_DETAILSEQNO)),TRIM(CHAR(B.I_OFYEAR)),0,A.S_FUNCSBTCODE,"
					+ "A.S_ECOSBTCODE,A.F_AMT,"
					+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),'"
					+ loginfo.getSuserCode()
					+ "','',CURRENT TIMESTAMP"
					+ " FROM TBS_TV_DIRECTPAYPLAN_SUB A,TBS_TV_DIRECTPAYPLAN_MAIN B"
					+ " WHERE A.S_BOOKORGCODE=B.S_BOOKORGCODE"
					+ " AND A.I_VOUSRLNO=B.I_VOUSRLNO"
					+ " AND B.S_BOOKORGCODE= ? AND (B.S_STATUS IS NULL OR B.S_STATUS= ? )";
			// ~{:KKcVwLe4zBk~}
			sqlExec.addParam(loginfo.getSorgcode());
			// ~{Oz:E1jV>~}
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ~{P^8DVw1mOz:E1jV>~}
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_DIRECTPAYPLAN_MAIN SET S_STATUS=? WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? ) ";
			// ~{:KKcVwLe4zBk~}
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// ~{Oz:E1jV>~}
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ~{040|AwK.:E7"KM~}TIPS
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? "
					+ " AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ?) AND S_OPERATIONTYPECODE= ?";
			// ~{:KKcVwLe4zBk~}
			sqlExec.addParam(loginfo.getSorgcode());
			// ~{Oz:E1jV>~}
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			// ~{R5Nq@`PM~}
			sqlExec.addParam(bizType);
			SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
			int row = rsfilepac.getRowCount();
			for (int i = 0; i < row; i++) {
				// // ~{040|AwK.:E7"KM~}TIPS
				sendMsgUtil.sendMsg("", loginfo.getSorgcode(), rsfilepac
						.getString(i, 0), MsgConstant.MSG_NO_5102, rsfilepac
						.getString(i, 1), false);
			}

			/**
			 * ~{P^8DND<~Sk0|AwK.6TS&9XO51m~}(~{RQ7"KM#,RQOz:E~})
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=?"
					+ " WHERE S_ORGCODE= ? AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND S_OPERATIONTYPECODE= ? ";
			// ~{:KKcVwLe4zBk~}
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
			sqlExec.addParam(loginfo.getSorgcode());
			// ~{Oz:E1jV>~}
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			// ~{R5Nq@`PM~}
			sqlExec.addParam(bizType);
			sqlExec.runQueryCloseCon(movedataSql);

		} catch (JAFDatabaseException e) {
			logger.error("数据库操作异常!", e);
			throw new ITFEBizException("数据库操作异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}
}
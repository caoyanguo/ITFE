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
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author ~{2\Q^9z~} ~{J52&WJ=p~}
 * @time 12-02-21 08:45:49 ~{EzA?Oz:E#,Vp1JOz:E#,V1=SLa=;#,R;P)EP6O5H~}
 */

public class BatchDirectPayCommitUtil {
	private static Log logger = LogFactory
			.getLog(BatchDirectPayCommitUtil.class);

	/**
	 * ~{V1=SV'866n6H#:EzA?Oz:E~}-~{H7HOLa=;~}
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	public static boolean confirmDircetPay(String bizType, TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		boolean bResult;
		/**
		 * ~{EP6OJG7qN,;$2FU~;z99PEO"~}
		 */
		bResult = CheckBizParam.checkConvertFinOrg(bizType, idto, loginfo);
		if (!bResult) {
			return false;
		}

		/**
		 * ~{EP6OJG7qN,;$RxPP4zBkSkV'86PP:E6TS&9XO5~}
		 */
		if(ITFECommonConstant.ISMATCHBANKNAME.equals(StateConstant.IF_MATCHBNKNAME_YES)) { //~{IO:#~}
			bResult = CheckBizParamForSH.checkGenbankandreckbank(bizType, idto,
					loginfo);
		}else {
			bResult = CheckBizParam.checkGenbankandreckbank(bizType, idto,
					loginfo);
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
					+ " FROM TBS_TV_DIRECTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
				// ~{:KKcVwLe4zBk~}
				sqlExec.addParam(loginfo.getSorgcode());
				// ~{ND<~C{~}
				sqlExec.addParam(idto.getSfilename());
				//~{9z?bVwLe~}
				sqlExec.addParam(idto.getStrecode());
				// ~{Oz:EW4L,~}
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
					+ " Case S_PAYEEOPNBNKNO WHEN s_trecode THEN S_PAYEEOPNBNKNO ELSE "
					+ "	(SELECT Max(S_RECKBANKCODE) FROM Ts_Genbankandreckbank "
					+ "   WHERE S_BOOKORGCODE=A.S_BOOKORGCODE "
					+ "   AND S_GENBANKCODE=A.S_PAYEEOPNBNKNO) "
					+ " END,"
					+ "  Case S_PAYEEOPNBNKNO WHEN s_trecode THEN '0' ELSE '1' END,'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "','"
					+ loginfo.getSuserCode()
					+ "','',CURRENT TIMESTAMP,S_PAYERACCT,'',S_PAYEEOPNBNKNO,S_PAYEEACCT,S_PAYEENAME"
					+ " FROM TBS_TV_DIRECTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
					+ " AND (S_IFMATCH IS NULL OR S_IFMATCH =?) ";
				// ~{:KKcVwLe4zBk~}
				sqlExec.addParam(loginfo.getSorgcode());
				// ~{ND<~C{~}
				sqlExec.addParam(idto.getSfilename());
				//~{9z?bVwLe~}
				sqlExec.addParam(idto.getStrecode());
				// ~{Oz:EW4L,~}
				sqlExec
						.addParam(StateConstant.CONFIRMSTATE_NO
								.replaceAll("\"", ""));
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
					+ " FROM TBS_TV_DIRECTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
					+ " AND  S_IFMATCH =? ";
				// ~{:KKcVwLe4zBk~}
				sqlExec.addParam(loginfo.getSorgcode());
				// ~{ND<~C{~}
				sqlExec.addParam(idto.getSfilename());
				//~{9z?bVwLe~}
				sqlExec.addParam(idto.getStrecode());
				// ~{Oz:EW4L,~}
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
				sqlExec.addParam(StateConstant.IF_MATCHBNKNAME_YES);
				sqlExec.runQueryCloseCon(movedataSql);
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
					+ " AND B.S_BOOKORGCODE= ? AND B.S_FILENAME= ? AND B.S_TRECODE= ? AND (B.S_STATUS IS NULL OR B.S_STATUS= ? )";
			// ~{:KKcVwLe4zBk~}
			sqlExec.addParam(loginfo.getSorgcode());
			// ~{ND<~C{~}
			sqlExec.addParam(idto.getSfilename());
			//~{9z?bVwLe~}
			sqlExec.addParam(idto.getStrecode());
			// ~{Oz:EW4L,~}
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ~{P^8DVw1mOz:E1jV>~}
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_DIRECTPAYPLAN_MAIN SET S_STATUS=? WHERE S_BOOKORGCODE= ? AND S_FILENAME= ?  AND S_TRECODE= ?  ";
			// ~{:KKcVwLe4zBk~}
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// ~{ND<~C{~}
			sqlExec.addParam(idto.getSfilename());
			//~{9z?bVwLe~}
			sqlExec.addParam(idto.getStrecode());
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ~{Or~}TIPS~{7"KM1(ND~}
			 */
			// ~{2i?4ND<~C{6TS&5D0|AwK.:ETZVw1mAYJ11mVPJG7q4fTZN4Oz:E5DJ}>]~}
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql = "SELECT count(1) FROM TBS_TV_DIRECTPAYPLAN_MAIN A "
					+ " WHERE A.S_BOOKORGCODE= ?  AND (S_STATUS IS NULL OR S_STATUS= ?) "
					+ " AND EXISTS (SELECT 1 FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= A.S_BOOKORGCODE "
					+ " AND S_PACKAGENO=A.S_PACKAGENO AND S_FILENAME= ? AND S_TRECODE= ?)";
			// ~{:KKcVwLe4zBk~}
			sqlExec.addParam(loginfo.getSorgcode());
			// ~{Oz:E1jV>~}
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			// ~{5<HkND<~C{~}
			sqlExec.addParam(idto.getSfilename());
			//~{9z?bVwLe~}
			sqlExec.addParam(idto.getStrecode());
			
			SQLResults rs = sqlExec.runQueryCloseCon(strsql);
			// ~{2;4fTZN4Oz:E5DJ}>]~}
			if (rs.getInt(0, 0) == 0) {
				// ~{040|AwK.:E7"KM~}TIPS
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(idto.getSfilename());
				sqlExec.addParam(idto.getStrecode());
				SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
				int row = rsfilepac.getRowCount();
				for (int i = 0; i < row; i++) {
					// // ~{040|AwK.:E7"KM~}TIPS
					sendMsgUtil.sendMsg(idto.getSfilename(), loginfo.getSorgcode(),
							rsfilepac.getString(i, 0), MsgConstant.MSG_NO_5102,
							rsfilepac.getString(i, 1), false);

					/**
					 * ~{P^8DND<~Sk0|AwK.6TS&9XO51m~}(~{RQ7"KM~})
					 */
					sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_RETCODE=? WHERE S_ORGCODE= ? AND S_PACKAGENO= ? ";
					// ~{:KKcVwLe4zBk~}
					sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
					sqlExec.addParam(loginfo.getSorgcode());
					// ~{0|AwK.:E~}
					sqlExec.addParam(rsfilepac.getString(i, 0));
					sqlExec.runQueryCloseCon(movedataSql);
				}
			}

			/**
			 * ~{P^8DND<~Sk0|AwK.6TS&9XO51m~}(~{RQOz:E~})
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=? WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
			// ~{:KKcVwLe4zBk~}
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// ~{5<HkND<~C{~}
			sqlExec.addParam(idto.getSfilename());
			//~{9z?bVwLe~}
			sqlExec.addParam(idto.getStrecode());
			sqlExec.runQueryCloseCon(movedataSql);
		} catch (JAFDatabaseException e) {
			logger.error("~{J}>]?b2YWwRl3#~}!", e);
			throw new ITFEBizException("~{J}>]?b2YWwRl3#~}!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}

}
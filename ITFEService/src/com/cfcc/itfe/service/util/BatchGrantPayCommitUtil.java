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
 * @author ~{2\Q^9z~} ~{J52&WJ=p~}
 * @time 12-02-21 08:45:49 ~{EzA?Oz:E#,Vp1JOz:E#,V1=SLa=;#,R;P)EP6O5H~}
 */

public class BatchGrantPayCommitUtil {
	private static Log logger = LogFactory
			.getLog(BatchGrantPayCommitUtil.class);

	/**
	 * ~{JZH(V'866n6H#:EzA?Oz:E~}-~{H7HOLa=;~}
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmGrantPay(String bizType, TvFilepackagerefDto idto,
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
		bResult = CheckBizParam.checkGenbankandreckbank(bizType, idto, loginfo);
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
			String movedataSql = "INSERT INTO TV_GRANTPAYMSGMAIN("
					+ "I_VOUSRLNO,S_ORGCODE,S_LIMITID,S_COMMITDATE,S_ACCDATE,"
					+ "S_FILENAME,S_TRECODE,S_PACKAGENO,S_PAYUNIT,S_TRANSBANKCODE,"
					+ "N_MONEY,S_PACKAGETICKETNO,S_GENTICKETDATE,S_OFYEAR,S_OFMONTH,"
					+ "S_TRANSACTUNIT,S_AMTTYPE,S_USERCODE,S_STATUS,S_DEMO,TS_SYSUPDATE,S_DEALNO,S_BUDGETUNITCODE,S_BUDGETTYPE)"
					+ " SELECT "
					+ "I_VOUSRLNO,S_BOOKORGCODE,substr(S_FILENAME,9,2),"
					+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
					+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
					+ "S_FILENAME,S_TRECODE,S_PACKAGENO,"
					+ "(SELECT Max(S_FINORGCODE) FROM TS_CONVERTFINORG "
					+ "	WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE),'',"
					+ "F_ZEROSUMAMT+F_SMALLSUMAMT,S_VOUNO,"
					+ "'"
					+ idto.getSfilename().substring(0, 8)
					+ "',"
					+ "substr(CHAR(D_ACCEPT),1,4),TRIM(CHAR(I_OFMONTH)),"
					+" CASE (SELECT count(*) FROM TS_GENBANKANDRECKBANK WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_BNKNO AND S_TRECODE=A.S_TRECODE)"
					+" WHEN 0 THEN (SELECT max(S_RECKBANKCODE) FROM TS_GENBANKANDRECKBANK WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_BNKNO)"
					+" ELSE (SELECT max(S_RECKBANKCODE) FROM TS_GENBANKANDRECKBANK WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_BNKNO AND S_TRECODE=A.S_TRECODE)"
					+" end,"
					//+ " (SELECT Max(S_RECKBANKCODE) FROM Ts_Genbankandreckbank WHERE S_BOOKORGCODE=A.S_BOOKORGCODE AND S_GENBANKCODE=A.S_BNKNO),"
					+"'1','"+ loginfo.getSuserCode()+ "','"+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "','',CURRENT TIMESTAMP,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),S_BDGORGCODE,C_BDGKIND "
					+ " FROM TBS_TV_GRANTPAYPLAN_MAIN A WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";

			// ~{:KKcVwLe4zBk~}
			sqlExec.addParam(loginfo.getSorgcode());
			// ~{ND<~C{~}
			sqlExec.addParam(idto.getSfilename());
			// ~{9z?bVwLe~}
			sqlExec.addParam(idto.getStrecode());
			// ~{Oz:EW4L,~}
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * ~{WS1mAYJ11mP4HkWS1mU}J=1m~}
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "INSERT INTO TV_GRANTPAYMSGSUB("
					+ "I_VOUSRLNO,I_DETAILSEQNO,S_ORGCODE,S_FILENAME,S_LIMITID,"
					+ "S_OFYEAR,S_LINE,S_PACKAGENO,S_DEALNO,S_PACKAGETICKETNO,"
					+ "S_BUDGETUNITCODE,S_BUDGETTYPE,S_FUNSUBJECTCODE,S_ECOSUBJECTCODE,N_MONEY,"
					+ "S_ACCATTRIB,S_ACCDATE,S_STATUS,S_USERCODE,TS_SYSUPDATE,S_DEMO)"
					+ " SELECT "
					+ "A.I_VOUSRLNO,ROW_NUMBER() OVER (ORDER BY A.I_VOUSRLNO) AS rownumber,B.S_BOOKORGCODE,B.S_FILENAME,substr(B.S_FILENAME,9,2),"
					+ "substr(CHAR(B.D_ACCEPT),1,4),TRIM(CHAR(ROW_NUMBER() OVER (ORDER BY A.I_VOUSRLNO))) AS rownumber,B.S_PACKAGENO,"
					+ "substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),B.S_VOUNO,B.S_BDGORGCODE,B.C_BDGKIND,"
					+ "A.S_FUNCSBTCODE,A.S_ECOSBTCODE,A.F_ZEROSUMAMT+A.F_SMALLSUMAMT,A.S_ACCATTRIB,"
					+ "substr(CHAR(B.D_ACCEPT),1,4)||substr(CHAR(B.D_ACCEPT),6,2)||substr(CHAR(B.D_ACCEPT),9,2),'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "','"
					+ loginfo.getSuserCode()
					+ "',CURRENT TIMESTAMP,''"
					+ " FROM ("
//					+ "SELECT I_VOUSRLNO,S_FUNCSBTCODE,S_ECOSBTCODE,sum(F_ZEROSUMAMT) AS F_ZEROSUMAMT,"
//					+ "sum(F_SMALLSUMAMT) AS F_SMALLSUMAMT,S_ACCATTRIB "
					+ "SELECT I_VOUSRLNO,S_FUNCSBTCODE,S_ECOSBTCODE,F_ZEROSUMAMT,"
					+ "F_SMALLSUMAMT,S_ACCATTRIB "
					+ " FROM ("
					+ " SELECT I_VOUSRLNO,I_DETAILSEQNO,S_FUNCSBTCODE,"
					+ "S_ECOSBTCODE,F_ZEROSUMAMT,0 AS F_SMALLSUMAMT,'1' AS S_ACCATTRIB FROM TBS_TV_GRANTPAYPLAN_SUB "
					+ " WHERE F_ZEROSUMAMT<>0 AND F_SMALLSUMAMT=0"
					+ " UNION ALL "
					+ " SELECT I_VOUSRLNO,I_DETAILSEQNO,S_FUNCSBTCODE,"
					+ "S_ECOSBTCODE,0 AS F_ZEROSUMAMT,F_SMALLSUMAMT,'2' AS S_ACCATTRIB  FROM TBS_TV_GRANTPAYPLAN_SUB "
					+ " WHERE F_ZEROSUMAMT=0 AND F_SMALLSUMAMT<>0 "
					+ " UNION ALL "
					+ " SELECT I_VOUSRLNO,I_DETAILSEQNO,S_FUNCSBTCODE,"
					+ "S_ECOSBTCODE,F_ZEROSUMAMT,0 AS F_SMALLSUMAMT,'1' AS S_ACCATTRIB FROM TBS_TV_GRANTPAYPLAN_SUB "
					+ "WHERE F_ZEROSUMAMT<>0 AND F_SMALLSUMAMT<>0 "
					+ " UNION ALL "
					+ " SELECT I_VOUSRLNO,I_DETAILSEQNO,S_FUNCSBTCODE, "
					+ "S_ECOSBTCODE,0 AS  F_ZEROSUMAMT,F_SMALLSUMAMT,'2' AS S_ACCATTRIB FROM TBS_TV_GRANTPAYPLAN_SUB "
					+ "WHERE F_ZEROSUMAMT<>0 AND F_SMALLSUMAMT<>0) "
					//+ " GROUP BY I_VOUSRLNO,S_FUNCSBTCODE,S_ECOSBTCODE,S_ACCATTRIB "
					+ ") AS A,TBS_TV_GRANTPAYPLAN_MAIN B "
					+ " WHERE A.I_VOUSRLNO=B.I_VOUSRLNO AND B.S_BOOKORGCODE= ? AND B.S_FILENAME= ? AND B.S_TRECODE= ? AND (B.S_STATUS IS NULL OR B.S_STATUS= ? )";

			// ~{:KKcVwLe4zBk~}
			sqlExec.addParam(loginfo.getSorgcode());
			// ~{ND<~C{~}
			sqlExec.addParam(idto.getSfilename());
			// ~{9z?bVwLe~}
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
			movedataSql = "UPDATE TBS_TV_GRANTPAYPLAN_MAIN SET S_STATUS=? WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
			// ~{:KKcVwLe4zBk~}
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// ~{ND<~C{~}
			sqlExec.addParam(idto.getSfilename());
			// ~{9z?bVwLe~}
			sqlExec.addParam(idto.getStrecode());
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * ~{Or~}TIPS~{7"KM1(ND~}
			 */
			// ~{2i?4ND<~C{6TS&5D0|AwK.:ETZVw1mAYJ11mVPJG7q4fTZN4Oz:E5DJ}>]~}
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql = "SELECT count(1) FROM TBS_TV_GRANTPAYPLAN_MAIN A "
					+ " WHERE A.S_BOOKORGCODE= ?  AND (S_STATUS IS NULL OR S_STATUS= ? )"
					+ " AND EXISTS (SELECT 1 FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= A.S_BOOKORGCODE "
					+ " AND S_PACKAGENO=A.S_PACKAGENO AND S_FILENAME= ? AND S_TRECODE= ? )";
			// ~{:KKcVwLe4zBk~}
			sqlExec.addParam(loginfo.getSorgcode());
			// ~{Oz:E1jV>~}
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			// ~{5<HkND<~C{~}
			sqlExec.addParam(idto.getSfilename());
			// ~{9z?bVwLe~}
			sqlExec.addParam(idto.getStrecode());
			SQLResults rs = sqlExec.runQueryCloseCon(strsql);
			// ~{2;4fTZN4Oz:E5DJ}>]~}
			if (rs.getInt(0, 0) == 0) {
				// ~{040|AwK.:E7"KM~}TIPS
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
				sqlExec.addParam(loginfo.getSorgcode());
				// ~{5<HkND<~C{~}
				sqlExec.addParam(idto.getSfilename());
				// ~{9z?bVwLe~}
				sqlExec.addParam(idto.getStrecode());
				SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
				int row = rsfilepac.getRowCount();
				for (int i = 0; i < row; i++) {
					// // ~{040|AwK.:E7"KM~}TIPS
					sendMsgUtil.sendMsg(idto.getSfilename(), loginfo
							.getSorgcode(), rsfilepac.getString(i, 0),
							MsgConstant.MSG_NO_5103, rsfilepac.getString(i, 1),
							false);
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
			// ~{9z?bVwLe~}
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
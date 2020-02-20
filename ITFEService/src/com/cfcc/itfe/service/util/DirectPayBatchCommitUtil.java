package com.cfcc.itfe.service.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import org.mule.DefaultMuleMessage;
//import org.mule.api.MuleException;
//import org.mule.api.MuleMessage;
//import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
//import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
//import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
//import com.cfcc.itfe.exception.SequenceException;
//import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
//import com.cfcc.itfe.facade.MsgSeqFacade;
//import com.cfcc.itfe.facade.TSystemFacade;
//import com.cfcc.itfe.facade.time.TimeFacade;
//import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
//import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
//import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
//import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
//import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
//import com.cfcc.itfe.persistence.dto.TsSyslogDto;
//import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
//import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author 批量拨付直接提交
 * @time 12-02-21 08:45:49  直接提交
 */

public class  DirectPayBatchCommitUtil{
	private static Log logger = LogFactory.getLog(DirectPayBatchCommitUtil.class);
			
	/**
	 * 批量拨付：直接提交
	 * 
	 * @throws ITFEBizException
	 */
	static boolean confirmBatch(String bizType,ITFELoginInfo loginfo) throws ITFEBizException {
		boolean bResult;
	
		/**
		 * 判断是否维护财政机构信息
		 */
		bResult = CheckBizParam.checkConvertFinOrg(bizType, null,loginfo);
		if (!bResult) {
			return false;
		}
		SQLExecutor sqlExec = null;
		try {
			/**
			 * 临时表写入主表正式表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String movedataSql = "";
			if("000077100005".equals(ITFECommonConstant.SRC_NODE))
				movedataSql = "INSERT INTO TV_PAYOUTFINANCE_MAIN"
					+ "(I_VOUSRLNO,S_BOOKORGCODE,S_TRECODE,S_BILLORG,S_PAYEEBANKNO,"
					+ "S_PAYERACCT,S_PAYERNAME,S_PAYERADDR,S_ENTRUSTDATE,S_PACKAGENO,"
					+ "S_PAYOUTVOUTYPE,S_VOUNO,S_VOUDATE,S_ADDWORD,S_BUDGETTYPE,"
					+ "S_BDGORGCODE,S_FUNCSBTCODE,S_ECNOMICSUBJECTCODE,N_AMT,S_STATUS,"
					+ "S_FILENAME,S_RESULT,TS_SYSUPDATE)"
					+ " SELECT "
					+ "I_VOUSRLNO,S_BOOKORGCODE,s_TreCode,"
					+ "(SELECT MAX(S_FINORGCODE) FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE),"
					+ " S_RCVRECKBNKNO,S_PAYERACCT,S_PAYERNAME,S_PAYERADDR,"
					+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
					+ "S_PACKAGENO,'1',S_PACKAGENO,"
					+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
					+ "S_ADDWORD,S_BUDGETTYPE,"
					+ "  S_BDGORGCODE,S_FUNCSBTCODE,'',F_AMT,'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "',S_FILENAME,'',CURRENT TIMESTAMP"
					+ " FROM ("
					+ "SELECT MAX(I_VOUSRLNO) AS I_VOUSRLNO,S_BOOKORGCODE,S_TRECODE, S_RCVRECKBNKNO,S_PAYERACCT,MAX(S_PAYERNAME) S_PAYERNAME,MAX(S_PAYERADDR) S_PAYERADDR,"
					+ "D_ACCEPT,S_PACKAGENO,'',S_BUDGETTYPE,S_BDGORGCODE,S_FUNCSBTCODE,sum(F_AMT) F_AMT,S_FILENAME,S_ADDWORD"
					+ " FROM TV_PAYOUTFINANCE A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? ) "
					+ " GROUP BY  S_BOOKORGCODE,S_TRECODE, S_RCVRECKBNKNO,S_PAYERACCT,"
					+ "D_ACCEPT,S_PACKAGENO,S_BUDGETTYPE,S_BDGORGCODE,S_FUNCSBTCODE,S_FILENAME,S_ADDWORD"
					+ ") A";
			else {
				movedataSql = "INSERT INTO TV_PAYOUTFINANCE_MAIN"
					+ "(I_VOUSRLNO,S_BOOKORGCODE,S_TRECODE,S_BILLORG,S_PAYEEBANKNO,"
					+ "S_PAYERACCT,S_PAYERNAME,S_PAYERADDR,S_ENTRUSTDATE,S_PACKAGENO,"
					+ "S_PAYOUTVOUTYPE,S_VOUNO,S_VOUDATE,S_ADDWORD,S_BUDGETTYPE,"
					+ "S_BDGORGCODE,S_FUNCSBTCODE,S_ECNOMICSUBJECTCODE,N_AMT,S_STATUS,"
					+ "S_FILENAME,S_RESULT,TS_SYSUPDATE)"
					+ " SELECT "
					+ "I_VOUSRLNO,S_BOOKORGCODE,s_TreCode,"
					+ "(SELECT MAX(S_FINORGCODE) FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE),"
					+ " S_RCVRECKBNKNO,S_PAYERACCT,S_PAYERNAME,S_PAYERADDR,"
					+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
					+ "S_PACKAGENO,'1',S_PACKAGENO,"
					+ "substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
					+ "'',S_BUDGETTYPE,"
					+ "  S_BDGORGCODE,S_FUNCSBTCODE,'',F_AMT,'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "',S_FILENAME,'',CURRENT TIMESTAMP"
					+ " FROM ("
					+ "SELECT MAX(I_VOUSRLNO) AS I_VOUSRLNO,S_BOOKORGCODE,S_TRECODE, S_RCVRECKBNKNO,S_PAYERACCT,MAX(S_PAYERNAME) S_PAYERNAME,MAX(S_PAYERADDR) S_PAYERADDR,"
					+ "D_ACCEPT,S_PACKAGENO,'',S_BUDGETTYPE,S_BDGORGCODE,S_FUNCSBTCODE,sum(F_AMT) F_AMT,S_FILENAME"
					+ " FROM TV_PAYOUTFINANCE A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? ) "
					+ " GROUP BY  S_BOOKORGCODE,S_TRECODE, S_RCVRECKBNKNO,S_PAYERACCT,"
					+ "D_ACCEPT,S_PACKAGENO,S_BUDGETTYPE,S_BDGORGCODE,S_FUNCSBTCODE,S_FILENAME"
					+ ") A";
			}
	
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号标志
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 临时表写入子表正式表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			if("000077100005".equals(ITFECommonConstant.SRC_NODE))
			{
				movedataSql = "INSERT INTO TV_PAYOUTFINANCE_SUB"
					+ "(I_VOUSRLNO,S_SEQNO,S_TRANO,S_PAYEEACCT,S_PAYEENAME,S_PAYEEADDR,S_PAYEEOPNBNKNO,N_SUBAMT,TS_SYSUPDATE)"
					+ " SELECT "
					+ "(SELECT MAX(I_VOUSRLNO) FROM TV_PAYOUTFINANCE_MAIN WHERE S_BOOKORGCODE= A.S_BOOKORGCODE"
					+ " AND S_FILENAME=A.S_FILENAME AND S_ADDWORD=A.S_ADDWORD AND S_PACKAGENO=A.S_PACKAGENO),I_VOUSRLNO,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),"
					+ "S_PAYEEACCT,S_PAYEENAME,S_PAYEEADDR,S_PAYEEOPNBNKNO,"
					+ " F_AMT,CURRENT TIMESTAMP FROM TV_PAYOUTFINANCE A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
			}else {
				movedataSql = "INSERT INTO TV_PAYOUTFINANCE_SUB"
					+ "(I_VOUSRLNO,S_SEQNO,S_TRANO,S_PAYEEACCT,S_PAYEENAME,S_PAYEEADDR,S_PAYEEOPNBNKNO,N_SUBAMT,TS_SYSUPDATE)"
					+ " SELECT "
					+ "(SELECT MAX(I_VOUSRLNO) FROM TV_PAYOUTFINANCE WHERE S_BOOKORGCODE= A.S_BOOKORGCODE"
					+ " AND S_FILENAME=A.S_FILENAME AND S_PACKAGENO=A.S_PACKAGENO),I_VOUSRLNO,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),"
					+ "S_PAYEEACCT,S_PAYEENAME,S_PAYEEADDR,S_PAYEEOPNBNKNO,"
					+ " F_AMT,CURRENT TIMESTAMP FROM TV_PAYOUTFINANCE A WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
			}
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号标志
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);
	
			/**
			 * 修改销号标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TV_PAYOUTFINANCE SET S_STATUS=? WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )";
			// 核算主体代码
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号标志
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);
	
			/**
			 * 修改文件与包流水对应关系表(待处理，已销号)
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
//			if("000077100005".equals(ITFECommonConstant.SRC_NODE))
//			{
//				sqlExec.setAutoCommit(false);
//				movedataSql = "delete from TV_FILEPACKAGEREF where S_ORGCODE= '"+loginfo.getSorgcode()+"' AND (S_CHKSTATE IS NULL OR S_CHKSTATE='"+StateConstant.CONFIRMSTATE_NO.replaceAll("\"", "")+"') and S_OPERATIONTYPECODE='"+bizType+"'";
//				sqlExec.runQuery(movedataSql);
//				movedataSql = "insert into TV_FILEPACKAGEREF(S_ORGCODE,S_TRECODE,S_FILENAME,S_TAXORGCODE,S_COMMITDATE,S_ACCDATE,S_PACKAGENO,"
//					+"S_OPERATIONTYPECODE,I_COUNT,N_MONEY,S_RETCODE,S_USERCODE,I_MODICOUNT,S_DEMO,S_CHKSTATE,S_MSGID,T_SENDTIME)"
//					+"select S_BOOKORGCODE,S_TRECODE,S_FILENAME,S_BILLORG,S_ENTRUSTDATE,S_VOUDATE,S_PACKAGENO,'"+bizType+"',"
//					+"(select count(*) from TV_PAYOUTFINANCE_SUB where S_SEQNO=main.I_VOUSRLNO),(select sum(N_SUBAMT)from TV_PAYOUTFINANCE_SUB where S_SEQNO=main.I_VOUSRLNO),"
//					+"'"+DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING+"','"+loginfo.getSuserCode()+"',0,'附言处理初始化','"+StateConstant.CONFIRMSTATE_YES+"',null,null "
//					+"from TV_PAYOUTFINANCE_MAIN main WHERE S_BOOKORGCODE= '"+loginfo.getSorgcode()+"' and S_STATUS='"+DealCodeConstants.DEALCODE_ITFE_DEALING+"'";
//				sqlExec.runQuery(movedataSql);
//				sqlExec.commitTrans();
//				sqlExec.closeConnection();
//			}else {			}
				movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=?"
					+ " WHERE S_ORGCODE= ? AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? )  AND S_OPERATIONTYPECODE= ? ";
				// 核算主体代码
				sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
				sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
				sqlExec.addParam(loginfo.getSorgcode());
				// 销号标志
				sqlExec
				.addParam(StateConstant.CONFIRMSTATE_NO
						.replaceAll("\"", ""));
				// 业务类型
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
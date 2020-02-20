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
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * @author 商行办理支付直接提交
 */

public class  DirectPayreckBankBackCommitUtil{
	private static Log logger = LogFactory.getLog(DirectPayreckBankBackCommitUtil.class);
			
	/**
	 * 商行办理支付
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmPayreckBackBank(String bizType, ITFELoginInfo loginfo, IDto _dto)
			throws ITFEBizException {
		boolean bResult;
		
		/**
		 * 判断是否有未补录行号信息
		 */
		if(StateConstant.IF_MATCHBNKNAME_YES.equals(ITFECommonConstant.ISMATCHBANKNAME)) {
			bResult = CheckBizParamForSH.checkIsMatchNameOver(bizType, null,loginfo);
			if(!bResult) {
				return false;
			}
		}
		
		/**
		 * 判断是否维护财政机构信息
		 */
		bResult = CheckBizParam.checkConvertFinOrg(bizType, null,loginfo);
		if (!bResult) {
			return false;
		}
		TvPayreckBankBackDto payreckdto=(TvPayreckBankBackDto) _dto;
		SQLExecutor sqlExec = null;
		try {
			/**
			 * 临时表写入主表正式表
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
			    +"	FROM  TBS_TV_BNKPAY_MAIN A  WHERE   A.S_BOOKORGCODE=? AND (A.S_STATE IS NULL OR A.S_STATE= ?)";
			
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				movedataSql+=" AND ( S_BIZTYPE = ? OR S_BIZTYPE = ? ) ";
			}else{
				movedataSql+=" AND S_BIZTYPE = ? ";
			}
			
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号状态
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK);
			}else{
				sqlExec.addParam(bizType);
			}
			
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 临时表写入子表正式表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "INSERT INTO TV_PAYRECK_BANK_BACK_LIST("
				+ " I_VOUSRLNO,I_SEQNO,S_ORIVOUNO,D_ORIVOUDATE,"
				+ " S_BDGORGCODE,S_FUNCBDGSBTCODE,S_ECNOMICSUBJECTCODE,F_AMT,S_ACCTPROP,TS_UPDATE)"
				+ " SELECT "
				+ " A.I_VOUSRLNO,I_GRPINNERSEQNO,A.S_ORIVOUNO,A.D_ORIVOUDATE,"
				+ " A.S_BDGORGCODE,A.S_FUNCSBTCODE,A.S_ECOSBTCODE,A.F_AMT,A.C_ACCTPROP,"
				+ " CURRENT TIMESTAMP "
				+ " FROM TBS_TV_BNKPAY_SUB A,TBS_TV_BNKPAY_MAIN B"
				+ " WHERE A.S_BOOKORGCODE=B.S_BOOKORGCODE"
				+ " AND A.I_VOUSRLNO=B.I_VOUSRLNO"
				+ " AND B.S_BOOKORGCODE= ? AND (B.S_STATE IS NULL OR B.S_STATE= ? )";
			
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				movedataSql+=" AND ( B.S_BIZTYPE = ? OR B.S_BIZTYPE = ? ) ";
			}else{
				movedataSql+=" AND B.S_BIZTYPE = ? ";
			}
			
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号状态
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
			
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK);
			}else{
				sqlExec.addParam(bizType);
			}
			
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 修改销号标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_BNKPAY_MAIN SET S_STATE=? WHERE S_BOOKORGCODE= ? AND (S_STATE IS NULL OR S_STATE= ? )";
			
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				movedataSql+=" AND ( S_BIZTYPE = ? OR S_BIZTYPE = ? ) ";
			}else{
				movedataSql+=" AND S_BIZTYPE = ? ";
			}
			
			// 核算主体代码
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号标志
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
			
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK);
			}else{
				sqlExec.addParam(bizType);
			}
			
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 向TIPS发送报文
			 */
			// 按包流水号发送TIPS
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql = "";
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
			    strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE,S_OPERATIONTYPECODE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? "
				       + " AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND ( S_OPERATIONTYPECODE= ? or S_OPERATIONTYPECODE= ? ) ";
			}else{
				strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE,S_OPERATIONTYPECODE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? "
				       + " AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND S_OPERATIONTYPECODE= ? ";
			}
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号标志
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
			// 业务类型（商行办理支付划款申请有两个业务类型）
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK);
			}else{
				sqlExec.addParam(bizType);
			}
			SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
			int row = rsfilepac.getRowCount();
			for (int i = 0; i < row; i++) {
				// // 按包流水号发送TIPS
				sendMsgUtil.sendMsg( rsfilepac
						.getString(i, 2), loginfo.getSorgcode(), rsfilepac
						.getString(i, 0), MsgConstant.APPLYPAY_BACK_DAORU, rsfilepac
						.getString(i, 1), false);
			}
			/**
			 * 修改文件与包流水对应关系表(已发送，已销号)
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=?" 
					+ " WHERE S_ORGCODE= ? AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND ( S_OPERATIONTYPECODE= ? or S_OPERATIONTYPECODE= ? )";
			}else{
				movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=?"
					+ " WHERE S_ORGCODE= ? AND (S_CHKSTATE IS NULL OR S_CHKSTATE= ? ) AND S_OPERATIONTYPECODE= ? ";
			}
			// 核算主体代码
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号标志
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"", ""));
			if(bizType.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK+BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)){
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK);
				sqlExec.addParam(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK);
			}else{
				sqlExec.addParam(bizType);
			}
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
	}}
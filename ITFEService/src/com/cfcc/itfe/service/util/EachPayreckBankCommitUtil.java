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
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author 曹艳国 实拨资金
 * @time 12-02-21 08:45:49  批量销号，逐笔销号，直接提交，一些判断等
 */

public class  EachPayreckBankCommitUtil{
	private static Log logger = LogFactory.getLog(EachPayreckBankCommitUtil.class);
			
	/**
	 * 实拨资金：批量销号-确认提交
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmPayreckBank(String bizType, IDto idto,ITFELoginInfo loginfo)
			throws ITFEBizException {
		boolean bResult;

		/**
		 * 判断是否有未补录行号信息
		 */
		if(StateConstant.IF_MATCHBNKNAME_YES.equals(ITFECommonConstant.ISMATCHBANKNAME)) {
			bResult = CheckBizParamForSH.checkIsMatchNameOverEach(bizType, idto,loginfo);
			if(!bResult) {
				return false;
			}
		}
		
		/**
		 * 判断是否维护财政机构信息
		 */
		bResult = CheckBizParam.checkConvertFinOrg(bizType, idto);
		if (!bResult) {
			return false;
		}
		String strVousrlno = String.valueOf(((TbsTvBnkpayMainDto) idto)
				.getIvousrlno());
		SQLExecutor sqlExec = null;
		try {
			/**
			 * 主表临时表写入主表正式表
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
				+"  S_TRECODE,S_BOOKORGCODE,S_VOUNO,substr(CHAR(D_VOUCHER),1,4)||'-'||substr(CHAR(D_VOUCHER),6,2)||'-'||substr(CHAR(D_VOUCHER),9,2), "
				+"	S_PAYERACCT,S_PAYERNAME,coalesce(S_PAYERADDR,''),S_PAYEEACCT,S_PAYEENAME,coalesce(S_PAYEEADDR,''),S_PAYEEOPNBNKNO,coalesce(S_ADDWORD,''),C_BDGKIND,"
				+"	C_TRIMFLAG,substr(CHAR(D_VOUCHER),1,4),F_ZEROSUMAMT+F_SMALLSUMAMT,0,'" 
				+  	DealCodeConstants.DEALCODE_ITFE_DEALING
				+"',S_STATE," 
				+"	substr(CHAR(D_ACCEPT),1,4)||'-'||substr(CHAR(D_ACCEPT),6,2)||'-'||substr(CHAR(D_ACCEPT),9,2),CURRENT TIMESTAMP,TRANSBIZTYPE(S_BIZTYPE),S_FILENAME  "
				+"	FROM  TBS_TV_BNKPAY_MAIN A WHERE I_VOUSRLNO=? ";
			
			// 关键字：凭证流水号
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 子表临时表写入子表正式表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "INSERT INTO TV_PAYRECK_BANK_LIST("
				+ " I_VOUSRLNO,I_SEQNO,"
				+ " S_BDGORGCODE,S_FUNCBDGSBTCODE,S_ECNOMICSUBJECTCODE,F_AMT,S_ACCTPROP,TS_UPDATE)"
				+ " SELECT  "
				+ " a.I_VOUSRLNO,a.i_GrpInnerSeqNo,"
				+ " A.S_BDGORGCODE,A.S_FUNCSBTCODE,coalesce(A.S_ECOSBTCODE,''),A.F_AMT,A.C_ACCTPROP,"
				+ " CURRENT TIMESTAMP "
				+ " FROM TBS_TV_BNKPAY_SUB A,TBS_TV_BNKPAY_MAIN B"
				+ " WHERE A.S_BOOKORGCODE=B.S_BOOKORGCODE"
				+ " AND A.I_VOUSRLNO=B.I_VOUSRLNO"
				+ " AND B.I_VOUSRLNO=? ";

			// 关键字：凭证流水号
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 修改临时表该记录销号标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_BNKPAY_MAIN SET S_STATE=? WHERE I_VOUSRLNO= ? ";
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			// 关键字：凭证流水号
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			// 调用发送报文
			sendMsgUtil.checkAndSendMsg((TbsTvBnkpayMainDto) idto,
					MsgConstant.APPLYPAY_DAORU, TbsTvBnkpayMainDto.tableName(), null,loginfo);
			
		} catch (JAFDatabaseException e) {
			logger.error("数据库操作异常!", e);
			throw new ITFEBizException("数据库操作异常!", e);
		} finally {
			if (null != sqlExec) {
				sqlExec.closeConnection();
			}
		}
		return true;
	}	}
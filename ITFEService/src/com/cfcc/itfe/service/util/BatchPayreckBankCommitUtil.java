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
 * @author  商行办理支付划款申请
 * @time 12-02-21 08:45:49 批量销号，逐笔销号，直接提交，一些判断等
 */

public class BatchPayreckBankCommitUtil {
	private static Log logger = LogFactory
			.getLog(BatchPayreckBankCommitUtil.class);

	/**
	 * 商行办理支付划款申请：批量销号-确认提交
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmPayreckBank(String bizType, TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		String fileName = idto.getSfilename();
		
		boolean bResult;
		/**
		 * 判断是否有未补录行号信息
		 */
		if(StateConstant.IF_MATCHBNKNAME_YES.equals(ITFECommonConstant.ISMATCHBANKNAME)) {
			bResult = CheckBizParamForSH.checkIsMatchNameOver(bizType, idto,loginfo);
			if(!bResult) {
				return false;
			}
		}
		
		/**
		 * 判断是否维护财政机构信息
		 */
		bResult = CheckBizParam.checkConvertFinOrg(bizType, idto, loginfo);
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

			String movedataSql = " INSERT INTO TV_PAYRECK_BANK" 
				+"	(i_VouSrlNo,D_ENTRUSTDATE,S_PACKNO,S_AGENTBNKCODE,S_TRANO,S_FINORGCODE,"
				+"	S_TRECODE,S_BOOKORGCODE,S_VOUNO,D_VOUDATE,S_PAYERACCT,S_PAYERNAME,S_PAYERADDR,"
				+"	S_PAYEEACCT,S_PAYEENAME,S_PAYEEADDR,S_PAYEEOPBKNO,S_ADDWORD,S_BUDGETTYPE,"
				+"	S_TRIMSIGN,S_OFYEAR,F_AMT,I_STATINFNUM,S_RESULT,S_PROCSTAT,D_ACCEPTDATE,TS_UPDATE,S_PAYMODE,S_FILENAME) "
				+"	select  "
				+"	i_VouSrlNo,substr(CHAR(D_ACCEPT),1,4)||'-'||substr(CHAR(D_ACCEPT),6,2)||'-'||substr(CHAR(D_ACCEPT),9,2),S_PACKNO," 
				+"	S_AGENTBNKCODE,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8)," 
				+"  (SELECT MAX(S_FINORGCODE) FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE)," 
				+"  S_TRECODE,S_BOOKORGCODE,S_VOUNO,substr(CHAR(D_VOUCHER),1,4)||'-'||substr(CHAR(D_VOUCHER),6,2)||'-'||substr(CHAR(D_VOUCHER),9,2), "
				+"	S_PAYERACCT,S_PAYERNAME,coalesce(S_PAYERADDR,''),S_PAYEEACCT,S_PAYEENAME,coalesce(S_PAYEEADDR,''),S_PAYEEOPNBNKNO,S_ADDWORD,C_BDGKIND,"
				+"	coalesce(C_TRIMFLAG,''),substr(CHAR(D_VOUCHER),1,4),F_ZEROSUMAMT+F_SMALLSUMAMT,0,'"
				+  	DealCodeConstants.DEALCODE_ITFE_DEALING
				+"',S_STATE," 
				+"	substr(CHAR(D_ACCEPT),1,4)||'-'||substr(CHAR(D_ACCEPT),6,2)||'-'||substr(CHAR(D_ACCEPT),9,2),CURRENT TIMESTAMP,TRANSBIZTYPE(S_BIZTYPE),S_FILENAME "
				+"	FROM  TBS_TV_BNKPAY_MAIN A WHERE S_BOOKORGCODE=? AND S_FILENAME=?  AND (S_STATE IS NULL OR S_STATE= ?)";
		
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
//			sqlExec.addParam(idto.getStrecode());
			// 销号状态
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 临时表写入子表正式表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();

			movedataSql = "INSERT INTO TV_PAYRECK_BANK_LIST("
				+ " i_VouSrlNo,I_SEQNO,"
				+ " S_BDGORGCODE,S_FUNCBDGSBTCODE,S_ECNOMICSUBJECTCODE,F_AMT,S_ACCTPROP,TS_UPDATE)"
				+ " SELECT A.i_VouSrlNo,I_GRPINNERSEQNO,"
				+ " A.S_BDGORGCODE,A.S_FUNCSBTCODE,coalesce(A.S_ECOSBTCODE,''),A.F_AMT,A.C_ACCTPROP,"
				+ " CURRENT TIMESTAMP "
				+ " FROM TBS_TV_BNKPAY_SUB A,TBS_TV_BNKPAY_MAIN B"
				+ " WHERE A.S_BOOKORGCODE=B.S_BOOKORGCODE"
				+ " AND A.I_VOUSRLNO=B.I_VOUSRLNO"
				+ " AND B.S_BOOKORGCODE=? AND B.S_FILENAME=? AND (B.S_STATE IS NULL OR B.S_STATE= ?)";
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
//			sqlExec.addParam(idto.getStrecode());
			// 销号状态
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 修改销号标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_BNKPAY_MAIN SET S_STATE=? WHERE S_BOOKORGCODE= ? AND S_FILENAME= ? ";
			// 核算主体代码
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// 文件名
			sqlExec.addParam(fileName);
			// 国库主体
//			sqlExec.addParam(idto.getStrecode());
			sqlExec.runQueryCloseCon(movedataSql);

			/**
			 * 向TIPS发送报文
			 */
			// 查看文件名对应的包流水号在主表临时表中是否存在未销号的数据
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql = "SELECT count(1) FROM TBS_TV_BNKPAY_MAIN A "
					+ " WHERE A.S_BOOKORGCODE= ?  AND (S_STATE IS NULL OR S_STATE= ?) "
					+ " AND EXISTS (SELECT 1 FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= A.S_BOOKORGCODE "
					+ " AND S_PACKAGENO=A.S_PACKNO AND S_FILENAME= ?  )";
			// 核算主体代码
			sqlExec.addParam(loginfo.getSorgcode());
			// 销号标志
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			// 导入文件名
			sqlExec.addParam(fileName);
			// 国库主体
//			sqlExec.addParam(idto.getStrecode());
			SQLResults rs = sqlExec.runQueryCloseCon(strsql);
			// 不存在未销号的数据
			if (rs.getInt(0, 0) == 0) {
				// 按包流水号发送TIPS
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? ";
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(fileName);
				// 国库主体
//				sqlExec.addParam(idto.getStrecode());
				SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
				int row = rsfilepac.getRowCount();
				for (int i = 0; i < row; i++) {
					// // 按包流水号发送TIPS
					sendMsgUtil.sendMsg(fileName, loginfo.getSorgcode(),
							rsfilepac.getString(i, 0), MsgConstant.APPLYPAY_DAORU,
							rsfilepac.getString(i, 1), false);

					/**
					 * 修改文件与包流水对应关系表(已发送)
					 */
					sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_RETCODE=? WHERE S_ORGCODE= ? AND S_PACKAGENO= ? ";
					// 核算主体代码
					sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
					sqlExec.addParam(loginfo.getSorgcode());
					// 包流水号
					sqlExec.addParam(rsfilepac.getString(i, 0));
					sqlExec.runQueryCloseCon(movedataSql);
				}
			}

			/**
			 * 修改文件与包流水对应关系表(已销号)
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=? WHERE S_ORGCODE= ? AND S_FILENAME= ? ";
			// 核算主体代码
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(loginfo.getSorgcode());
			// 导入文件名
			sqlExec.addParam(fileName);
			// 国库主体
//			sqlExec.addParam(idto.getStrecode());
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
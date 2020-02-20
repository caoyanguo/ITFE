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
 *退库：逐笔销号-确认提交
 */

public class  EachDwbkCommitUtil{
	private static Log logger = LogFactory.getLog(EachDwbkCommitUtil.class);
			
	/**
	 * 退库：逐笔销号-确认提交
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmRetTreasury(String bizType, IDto idto,ITFELoginInfo loginfo)
			throws ITFEBizException {
		boolean bResult;
		/**
		 * 判断是否维护退库原因代码对照
		 */
		bResult = CheckBizParam.checkDrawbackReason(idto);
		if (!bResult) {
			return false;
		}
		String strVousrlno = String.valueOf(((TbsTvDwbkDto) idto)
				.getIvousrlno());
		SQLExecutor sqlExec = null;
		try {
			/**
			 * 临时表写入正式表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String movedataSql="";
			if(!ITFECommonConstant.ISCONVERT.equals("0")){//征收机关代码、辅助标志做转换
				movedataSql = "INSERT INTO TV_DWBK("
					+ "I_VOUSRLNO,S_ELECVOUNO,S_DWBKVOUCODE,S_TAXORGCODE,S_AGENTTAXORGCODE,"
					+ "S_PAYERTRECODE,S_AIMTRECODE,C_BDGKIND,C_BDGLEVEL,S_BDGSBTCODE,"
					+ "S_DWBKREASONCODE,S_ASTFLAG,S_DWBKBY,F_DWBKRATIO,F_DWBKAMT,"
					+ "S_EXAMORG,F_AMT,C_BCKFLAG,S_PAYEECODE,S_PAYEEACCT,"
					+ "S_PAYEENAME,S_PAYEEOPNBNKNO,D_ACCEPT,D_ACCT,D_VOUCHER,"
					+ "D_BILL,S_ETPCODE,S_ECOCODE,C_TRIMFLAG,S_BIZTYPE,"
					+ "S_PACKAGENO,S_STATUS,S_DEALNO,S_FILENAME,S_BOOKORGCODE,TS_SYSUPDATE,S_TBSASTFLAG)"
					+ " SELECT "
					+ " I_VOUSRLNO,S_ELECVOUNO,S_DWBKVOUCODE,"
					+ "S_TAXORGCODE,S_AGENTTAXORGCODE,"
					+ " S_PAYERTRECODE,S_AIMTRECODE,C_BDGKIND,C_BDGLEVEL,S_BDGSBTCODE,"
					+ " (SELECT MAX(S_TCBSDRAWCODE) FROM TS_DRAWBACKREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE"
					+ "  AND S_TBSDRAWCODE=A.S_DWBKREASONCODE),"
					+ "'',"
					+ " S_DWBKBY,F_DWBKRATIO,F_DWBKAMT,"
					+ " S_EXAMORG,F_AMT,C_BCKFLAG,S_PAYEECODE,S_PAYEEACCT,"
					+ " S_PAYEENAME,S_PAYEEOPNBNKNO,D_ACCEPT,D_ACCT,D_VOUCHER,"
					+ " D_BILL,S_ETPCODE,S_ECOCODE,C_TRIMFLAG,S_BIZTYPE,"
					+ " S_PACKAGENO,'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "',substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),"
					+ "S_FILENAME,S_BOOKORGCODE,CURRENT TIMESTAMP,S_ASTFLAG "
					+ " FROM TBS_TV_DWBK A WHERE I_VOUSRLNO= ? ";
			}else{//征收机关代码、辅助标志不做转换
				movedataSql = "INSERT INTO TV_DWBK("
					+ "I_VOUSRLNO,S_ELECVOUNO,S_DWBKVOUCODE,S_TAXORGCODE,S_AGENTTAXORGCODE,"
					+ "S_PAYERTRECODE,S_AIMTRECODE,C_BDGKIND,C_BDGLEVEL,S_BDGSBTCODE,"
					+ "S_DWBKREASONCODE,S_ASTFLAG,S_DWBKBY,F_DWBKRATIO,F_DWBKAMT,"
					+ "S_EXAMORG,F_AMT,C_BCKFLAG,S_PAYEECODE,S_PAYEEACCT,"
					+ "S_PAYEENAME,S_PAYEEOPNBNKNO,D_ACCEPT,D_ACCT,D_VOUCHER,"
					+ "D_BILL,S_ETPCODE,S_ECOCODE,C_TRIMFLAG,S_BIZTYPE,"
					+ "S_PACKAGENO,S_STATUS,S_DEALNO,S_FILENAME,S_BOOKORGCODE,TS_SYSUPDATE,S_TBSASTFLAG)"
					+ " SELECT "
					+ " I_VOUSRLNO,S_ELECVOUNO,S_DWBKVOUCODE,"
					+ "S_TAXORGCODE,S_AGENTTAXORGCODE,"
					+ " S_PAYERTRECODE,S_AIMTRECODE,C_BDGKIND,C_BDGLEVEL,S_BDGSBTCODE,"
					+ " (SELECT MAX(S_TCBSDRAWCODE) FROM TS_DRAWBACKREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE"
					+ "  AND S_TBSDRAWCODE=A.S_DWBKREASONCODE),"
					+ " S_ASTFLAG,"
					+ " S_DWBKBY,F_DWBKRATIO,F_DWBKAMT,"
					+ " S_EXAMORG,F_AMT,C_BCKFLAG,S_PAYEECODE,S_PAYEEACCT,"
					+ " S_PAYEENAME,S_PAYEEOPNBNKNO,D_ACCEPT,D_ACCT,D_VOUCHER,"
					+ " D_BILL,S_ETPCODE,S_ECOCODE,C_TRIMFLAG,S_BIZTYPE,"
					+ " S_PACKAGENO,'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "',substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),"
					+ "S_FILENAME,S_BOOKORGCODE,CURRENT TIMESTAMP,S_ASTFLAG "
					+ " FROM TBS_TV_DWBK A WHERE I_VOUSRLNO= ? ";
			}

			// 关键字：凭证流水号
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 更新辅助标志
			 */
			if(!ITFECommonConstant.ISCONVERT.equals("0")){//辅助标志做转换
			// 按国库代码和科目代码精确匹配
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql1 = " update TV_DWBK a "
					+ " set a.S_ASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_PAYERTRECODE = b.S_TRECODE and a.S_BDGSBTCODE = b.S_BUDGETSUBCODE and a.S_TBSASTFLAG = b.S_TBSASSITSIGN ) "
					+ " where a.I_VOUSRLNO= ? ";
			// 关键字：凭证流水号
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(updateAssistSql1);

			// 国库代码精确匹配、科目代码为N
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql2 = " update TV_DWBK a "
					+ " set a.S_ASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_PAYERTRECODE = b.S_TRECODE and b.S_BUDGETSUBCODE =? and a.S_TBSASTFLAG = b.S_TBSASSITSIGN and (a.S_ASTFLAG =? or a.S_ASTFLAG is null)) "
					+ " where a.I_VOUSRLNO= ? and (a.S_ASTFLAG =? or a.S_ASTFLAG is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql2);

			// 科目代码精确匹配、国库代码为N
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql3 = " update TV_DWBK a "
					+ " set a.S_ASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and a.S_BDGSBTCODE = b.S_BUDGETSUBCODE and a.S_TBSASTFLAG = b.S_TBSASSITSIGN and (a.S_ASTFLAG =? or a.S_ASTFLAG is null)) "
					+ " where a.I_VOUSRLNO= ? and (a.S_ASTFLAG =? or a.S_ASTFLAG is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql3);

			// 科目代码为N、国库代码为N
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql4 = " update TV_DWBK a "
					+ " set a.S_ASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and b.S_BUDGETSUBCODE = ? and a.S_TBSASTFLAG = b.S_TBSASSITSIGN and (a.S_ASTFLAG =? or a.S_ASTFLAG is null)) "
					+ " where a.I_VOUSRLNO= ? and (a.S_ASTFLAG =? or a.S_ASTFLAG is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql4);
			}

			/**
			 * 修改销号标志
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			movedataSql = "UPDATE TBS_TV_DWBK SET S_STATUS=? WHERE I_VOUSRLNO= ? ";
			// 关键字：凭证流水号
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			// 判断是否已经销号，并且是否可以发送
			sendMsgUtil.checkAndSendMsg((TbsTvDwbkDto) idto,
					MsgConstant.MSG_NO_1104, TbsTvDwbkDto.tableName(), null,loginfo);
		
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
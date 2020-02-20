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
 * @author 更正逐笔提交
 * @time 12-02-21 08:45:49
 */

public class EachCorrectCommitUtil {
	private static Log logger = LogFactory.getLog(EachCorrectCommitUtil.class);

	/**
	 * 更正：逐笔销号-确认提交
	 * 
	 * @param fileName
	 * @throws ITFEBizException
	 */
	static boolean confirmCorrect(String bizType, IDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		boolean bResult;
		if(!ITFECommonConstant.ISCONVERT.equals("0")){//征收机关代码、辅助标志做转换
		/**
		 * 判断是否维护原征收机关对照表
		 */
		bResult = CheckBizParam.checkConvertTaxOrg(bizType, idto, "1");
		if (!bResult) {
			return false;
		}
		/**
		 * 判断是否维护现征收机关对照表
		 */
		bResult = CheckBizParam.checkConvertTaxOrg(bizType, idto, "2");
		if (!bResult) {
			return false;
		}
		}
		/**
		 * 判断是否维护更正原因代码对照
		 */
		bResult = CheckBizParam.checkCorrReason(idto);
		if (!bResult) {
			return false;
		}
		String strVousrlno = String.valueOf(((TbsTvInCorrhandbookDto) idto)
				.getIvousrlno());
		SQLExecutor sqlExec = null;
		try {
			/**
			 * 临时表写入正式表
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String movedataSql = "";
			if(!ITFECommonConstant.ISCONVERT.equals("0")){//征收机关代码、辅助标志做转换
				movedataSql = "INSERT INTO TV_IN_CORRHANDBOOK("
					+ "I_VOUSRLNO,S_ELECVOUNO,S_CORRVOUNO,D_ACCEPT,D_ACCT,"
					+ "D_VOUCHER,S_ORIPAYEETRECODE,S_ORIAIMTRECODE,S_ORITAXORGCODE,S_ORIBDGSBTCODE,"
					+ "C_ORIBDGLEVEL,C_ORIBDGKIND,S_ORIASTFLAG,"
					+ " F_ORICORRAMT,S_CURPAYEETRECODE,S_CURAIMTRECODE,S_CURTAXORGCODE,"
					+ " S_CURBDGSBTCODE,C_CURBDGLEVEL,C_CURBDGKIND,"
					+ " F_CURCORRAMT,S_CURASTFLAG,S_REASONCODE,C_TRIMFLAG,S_PACKAGENO,"
					+ "S_DEALNO,S_STATUS,S_FILENAME,S_BOOKORGCODE,TS_SYSUPDATE,S_TBSORIASTFLAG,S_TBSCURASTFLAG)"
					+ " SELECT "
					+ "I_VOUSRLNO,S_ELECVOUNO,S_CORRVOUNO,D_ACCEPT,D_ACCT,"
					+ "D_VOUCHER,S_ORIPAYEETRECODE,S_ORIAIMTRECODE,"
					+ "(SELECT MAX(S_TCBSTAXORGCODE) FROM TS_CONVERTTAXORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_ORIPAYEETRECODE AND S_TBSTAXORGCODE=A.S_ORITAXORGCODE),"
					+ "S_ORIBDGSBTCODE,C_ORIBDGLEVEL,C_ORIBDGKIND,"
					+ "'',"
					+ "F_ORICORRAMT,S_CURPAYEETRECODE,S_CURAIMTRECODE,"
					+ " (SELECT MAX(S_TCBSTAXORGCODE) FROM TS_CONVERTTAXORG "
					+ " WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_CURPAYEETRECODE AND S_TBSTAXORGCODE=A.S_CURTAXORGCODE),"
					+ "S_CURBDGSBTCODE,C_CURBDGLEVEL,C_CURBDGKIND,F_CURCORRAMT,"
					+ "'',"
					+ "(SELECT MAX(S_TCBSCORRCODE) FROM TS_CORRREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE"
					+ " AND S_TBSCORRCODE=A.S_REASONCODE),C_TRIMFLAG,S_PACKAGENO,"
					+ "substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "',S_FILENAME,S_BOOKORGCODE,CURRENT TIMESTAMP,S_ORIASTFLAG,S_CURASTFLAG  "
					+ " FROM TBS_TV_IN_CORRHANDBOOK A WHERE I_VOUSRLNO= ? ";
			}else{//征收机关代码、辅助标志不做转换
				movedataSql = "INSERT INTO TV_IN_CORRHANDBOOK("
					+ "I_VOUSRLNO,S_ELECVOUNO,S_CORRVOUNO,D_ACCEPT,D_ACCT,"
					+ "D_VOUCHER,S_ORIPAYEETRECODE,S_ORIAIMTRECODE,S_ORITAXORGCODE,S_ORIBDGSBTCODE,"
					+ "C_ORIBDGLEVEL,C_ORIBDGKIND,S_ORIASTFLAG,"
					+ " F_ORICORRAMT,S_CURPAYEETRECODE,S_CURAIMTRECODE,S_CURTAXORGCODE,"
					+ " S_CURBDGSBTCODE,C_CURBDGLEVEL,C_CURBDGKIND,"
					+ " F_CURCORRAMT,S_CURASTFLAG,S_REASONCODE,C_TRIMFLAG,S_PACKAGENO,"
					+ "S_DEALNO,S_STATUS,S_FILENAME,S_BOOKORGCODE,TS_SYSUPDATE,S_TBSORIASTFLAG,S_TBSCURASTFLAG)"
					+ " SELECT "
					+ "I_VOUSRLNO,S_ELECVOUNO,S_CORRVOUNO,D_ACCEPT,D_ACCT,"
					+ "D_VOUCHER,S_ORIPAYEETRECODE,S_ORIAIMTRECODE,"
					+ "S_ORITAXORGCODE,"
					+ "S_ORIBDGSBTCODE,C_ORIBDGLEVEL,C_ORIBDGKIND,"
					+ "S_ORIASTFLAG,"
					+ "F_ORICORRAMT,S_CURPAYEETRECODE,S_CURAIMTRECODE,"
					+ "S_CURTAXORGCODE,"
					+ "S_CURBDGSBTCODE,C_CURBDGLEVEL,C_CURBDGKIND,F_CURCORRAMT,"
					+ "S_CURASTFLAG,"
					+ "(SELECT MAX(S_TCBSCORRCODE) FROM TS_CORRREASON WHERE S_BOOKORGCODE=A.S_BOOKORGCODE"
					+ " AND S_TBSCORRCODE=A.S_REASONCODE),C_TRIMFLAG,S_PACKAGENO,"
					+ "substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),'"
					+ DealCodeConstants.DEALCODE_ITFE_DEALING
					+ "',S_FILENAME,S_BOOKORGCODE,CURRENT TIMESTAMP,S_ORIASTFLAG,S_CURASTFLAG  "
					+ " FROM TBS_TV_IN_CORRHANDBOOK A WHERE I_VOUSRLNO= ? ";
			}
			// 关键字：凭证流水号
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 更新“原辅助标志”
			 */
			if(!ITFECommonConstant.ISCONVERT.equals("0")){//辅助标志做转换
			// 按国库代码和科目代码精确匹配
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql1 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_ORIASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_ORIAIMTRECODE = b.S_TRECODE and a.S_ORIBDGSBTCODE = b.S_BUDGETSUBCODE and a.S_TBSORIASTFLAG = b.S_TBSASSITSIGN ) "
					+ " where a.I_VOUSRLNO = ? ";
			// 关键字：凭证流水号
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(updateAssistSql1);

			// 国库代码精确匹配、科目代码为N
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql2 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_ORIASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_ORIAIMTRECODE = b.S_TRECODE and b.S_BUDGETSUBCODE =? and a.S_TBSORIASTFLAG = b.S_TBSASSITSIGN and (a.S_ORIASTFLAG =? or a.S_ORIASTFLAG is null)) "
					+ " where a.I_VOUSRLNO= ? and (a.S_ORIASTFLAG =? or a.S_ORIASTFLAG is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql2);

			// 科目代码精确匹配、国库代码为N
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql3 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_ORIASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and a.S_ORIBDGSBTCODE = b.S_BUDGETSUBCODE and a.S_TBSORIASTFLAG = b.S_TBSASSITSIGN and (a.S_ORIASTFLAG =? or a.S_ORIASTFLAG is null)) "
					+ " where a.I_VOUSRLNO = ? and (a.S_ORIASTFLAG =? or a.S_ORIASTFLAG is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql3);

			// 科目代码为N、国库代码为N
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String updateAssistSql4 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_ORIASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and b.S_BUDGETSUBCODE = ? and a.S_TBSORIASTFLAG = b.S_TBSASSITSIGN and (a.S_ORIASTFLAG =? or a.S_ORIASTFLAG is null)) "
					+ " where a.I_VOUSRLNO = ? and (a.S_ORIASTFLAG =? or a.S_ORIASTFLAG is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql4);

			/**
			 * 更新“现辅助标志”
			 */
			// 按国库代码和科目代码精确匹配
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql1 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_CURASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_ORIAIMTRECODE = b.S_TRECODE and a.S_CURBDGSBTCODE = b.S_BUDGETSUBCODE and a.S_TBSCURASTFLAG = b.S_TBSASSITSIGN ) "
					+ " where a.I_VOUSRLNO = ? ";
			// 关键字：凭证流水号
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(updateAssistSql1);

			// 国库代码精确匹配、科目代码为N
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql2 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_CURASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and a.S_CURAIMTRECODE = b.S_TRECODE and b.S_BUDGETSUBCODE =? and a.S_TBSCURASTFLAG = b.S_TBSASSITSIGN and (a.S_CURASTFLAG =? or a.S_CURASTFLAG is null)) "
					+ " where a.I_VOUSRLNO = ? and (a.S_CURASTFLAG =? or a.S_CURASTFLAG is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql2);

			// 科目代码精确匹配、国库代码为N
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql3 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_CURASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and a.S_CURBDGSBTCODE = b.S_BUDGETSUBCODE and a.S_TBSCURASTFLAG = b.S_TBSASSITSIGN and (a.S_CURASTFLAG =? or a.S_CURASTFLAG is null)) "
					+ " where a.I_VOUSRLNO = ? and (a.S_CURASTFLAG =? or a.S_CURASTFLAG is null)";
			// 关键字：凭证流水号
			sqlExec.addParam("N");
			sqlExec.addParam("");
			sqlExec.addParam(strVousrlno);
			sqlExec.addParam("");
			sqlExec.runQueryCloseCon(updateAssistSql3);

			// 科目代码为N、国库代码为N
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateAssistSql4 = " update TV_IN_CORRHANDBOOK a "
					+ " set a.S_CURASTFLAG = "
					+ "(select b.S_TIPSASSISTSIGN from TS_CONVERTASSITSIGN b where a.S_BOOKORGCODE = b.S_ORGCODE "
					+ " and b.S_TRECODE = ? and b.S_BUDGETSUBCODE = ? and a.S_TBSCURASTFLAG = b.S_TBSASSITSIGN and (a.S_CURASTFLAG =? or a.S_CURASTFLAG is null)) "
					+ " where a.I_VOUSRLNO = ? and (a.S_CURASTFLAG =? or a.S_CURASTFLAG is null)";
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
			movedataSql = "UPDATE TBS_TV_IN_CORRHANDBOOK SET S_STATUS=? WHERE I_VOUSRLNO= ? ";
			// 关键字：凭证流水号
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam(strVousrlno);
			sqlExec.runQueryCloseCon(movedataSql);
			/**
			 * 判断所销号记录对应的导入文件是否销号
			 */
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql = "SELECT count(1) FROM TBS_TV_IN_CORRHANDBOOK A "
					+ " WHERE A.S_BOOKORGCODE= ? AND A.S_FILENAME= ? AND A.S_CURPAYEETRECODE= ? AND (S_STATUS IS NULL OR S_STATUS = ? )";

			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam(((TbsTvInCorrhandbookDto) idto).getSfilename());
			sqlExec.addParam(((TbsTvInCorrhandbookDto) idto)
					.getScurpayeetrecode());
			sqlExec
					.addParam(StateConstant.CONFIRMSTATE_NO
							.replaceAll("\"", ""));
			SQLResults rs = sqlExec.runQueryCloseCon(strsql);
			// 所销号记录对应的导入文件不存在未销号的数据
			if (rs.getInt(0, 0) == 0) {
				/**
				 * 向TIPS发送报文
				 */
				// 查看文件名对应的包流水号在主表临时表中是否存在未销号的数据
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				strsql = "SELECT count(1) FROM TBS_TV_IN_CORRHANDBOOK A "
						+ " WHERE S_BOOKORGCODE= ? AND (S_STATUS IS NULL OR S_STATUS= ? )"
						+ " AND EXISTS (SELECT 1 FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= A.S_BOOKORGCODE "
						+ " AND S_PACKAGENO=A.S_PACKAGENO AND S_FILENAME= ? AND S_TRECODE= ?)";
				// 核算主体代码
				sqlExec.addParam(loginfo.getSorgcode());
				// 销号标志
				sqlExec.addParam(StateConstant.CONFIRMSTATE_NO.replaceAll("\"",
						""));
				// 导入文件名
				sqlExec
						.addParam(((TbsTvInCorrhandbookDto) idto)
								.getSfilename());
				// 国库主体
				sqlExec.addParam(((TbsTvInCorrhandbookDto) idto)
						.getScurpayeetrecode());
				rs = sqlExec.runQueryCloseCon(strsql);
				// 不存在未销号的数据
				if (rs.getInt(0, 0) == 0) {
					// 按包流水号发送TIPS
					sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_TRECODE= ?";
					sqlExec.addParam(loginfo.getSorgcode());
					sqlExec.addParam(((TbsTvInCorrhandbookDto) idto)
							.getSfilename());
					// 国库主体
					sqlExec.addParam(((TbsTvInCorrhandbookDto) idto)
							.getScurpayeetrecode());
					SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
					int row = rsfilepac.getRowCount();
					for (int i = 0; i < row; i++) {
						// // 按包流水号发送TIPS
						sendMsgUtil.sendMsg(((TbsTvInCorrhandbookDto) idto)
								.getSfilename(), loginfo.getSorgcode(),
								rsfilepac.getString(i, 0),
								MsgConstant.MSG_NO_1105, rsfilepac.getString(i,
										1), false);
					}
				}
				/**
				 * 修改文件与包流水对应关系表(已发送，已销号)
				 */
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=?"
						+ " WHERE S_ORGCODE= ? AND S_FILENAME= ?  AND S_TRECODE= ?";
				// 核算主体代码
				sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
				sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
				sqlExec.addParam(loginfo.getSorgcode());
				// 文件名
				sqlExec
						.addParam(((TbsTvInCorrhandbookDto) idto)
								.getSfilename());
				// 国库主体
				sqlExec.addParam(((TbsTvInCorrhandbookDto) idto)
						.getScurpayeetrecode());
				sqlExec.runQueryCloseCon(movedataSql);
			}

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
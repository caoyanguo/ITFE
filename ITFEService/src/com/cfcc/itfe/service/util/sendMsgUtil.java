package com.cfcc.itfe.service.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TbsTvFreeDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

public class sendMsgUtil {
	private static Log logger = LogFactory.getLog(sendMsgUtil.class);

	/**
	 * 发送报文处理
	 * 
	 * @param String
	 *            sfilename 文件名称
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            spackno 包流水号
	 * @param String
	 *            msgno 报文编号
	 * @param String
	 *            commitdate 委托日期
	 * 
	 * @param boolean isRepeat 是否重发
	 * @throws ITFEBizException
	 */
	public static void sendMsg(String sfilename, String sorgcode,
			String spackno, String msgno, String commitdate, boolean isRepeat)
			throws ITFEBizException {
		try {
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_FILE_NAME, sfilename);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, sorgcode);
			if(MsgConstant.MSG_NO_1105.equals(msgno)||MsgConstant.MSG_NO_1104.equals(msgno)
				||MsgConstant.MSG_NO_5101.equals(msgno)||MsgConstant.MSG_NO_5102.equals(msgno)
				||MsgConstant.MSG_NO_5103.equals(msgno))
				msgno = msgno+"_OUT";
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY, msgno);
			message.setProperty(MessagePropertyKeys.MSG_REPEAT, isRepeat);
			message.setPayload(map);
			message.setProperty(MessagePropertyKeys.MSG_PACK_NO, spackno);
			message.setProperty(MessagePropertyKeys.MSG_DATE, commitdate);
			if(sorgcode!=null&&sorgcode.startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
			ServiceUtil.checkResult(message);
		} catch (MuleException e) {
			logger.error("调用后台报文处理的时候出现异常!", e);
			throw new ITFEBizException("调用后台报文处理的时候出现异常!", e);
		}
	}

	/**
	 * @param filename
	 * @param msgno
	 * @param tablename
	 * @param vousrlno
	 * @throws ITFEBizException
	 */
	public static void checkAndSendMsg(IDto idto, String msgno,
			String tablename, Long vousrlno, ITFELoginInfo loginfo)
			throws ITFEBizException {
		String filename ="";
		String trecode ="";
		Long svousrlno = new Long(0);
		String pakageno="";
		if(idto instanceof TbsTvPayoutDto) {
			TbsTvPayoutDto paydto = (TbsTvPayoutDto)idto;
			filename = paydto.getSfilename();
			trecode = paydto.getStrecode();
		}else if(idto instanceof TbsTvDwbkDto) {
			TbsTvDwbkDto dwbkdto = (TbsTvDwbkDto)idto;
			filename = dwbkdto.getSfilename();
			trecode = dwbkdto.getSpayertrecode();
			pakageno = dwbkdto.getSpackageno();
		} else if (idto instanceof TbsTvFreeDto) {
			TbsTvFreeDto freedto = (TbsTvFreeDto) idto;
			filename = freedto.getSfilename();
			trecode = freedto.getSfreepluptrecode();
		} else if (idto instanceof TbsTvPbcpayDto) {
			TbsTvPbcpayDto pbcdto = (TbsTvPbcpayDto) idto;
			filename = pbcdto.getSfilename();
			trecode = pbcdto.getStrecode();
		}else if(idto instanceof TbsTvBnkpayMainDto) {
			TbsTvBnkpayMainDto bankdto = (TbsTvBnkpayMainDto)idto;
			filename = bankdto.getSfilename();
			trecode = bankdto.getStrecode();
			svousrlno = bankdto.getIvousrlno();
			pakageno = bankdto.getSpackno();
		}
		/**
		 * 判断所销号记录对应的导入文件是否销号
		 */
		SQLExecutor sqlExec = null;
		try {

			/**
			 * 销号失败的记录写入正式表
			 */
			if (null != vousrlno && msgno.equals(MsgConstant.MSG_NO_5101)) {
				TbsTvPayoutDto payDto = new TbsTvPayoutDto();
				TbsTvPayoutDto payDto2 = new TbsTvPayoutDto();
				payDto.setSbookorgcode(loginfo.getSorgcode());
				payDto.setIvousrlno(vousrlno);
				List l = CommonFacade.getODB().findRsByDtoWithUR(payDto);
				if (null != l && l.size() != 0) {
					payDto2 = (TbsTvPayoutDto) l.get(0);
					recordWaitForPayout(payDto2, loginfo);
				}
				
			}

			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			String strsql="";
			if (msgno.equals(MsgConstant.MSG_NO_1104) || msgno.equals(MsgConstant.MSG_NO_5101)) {
				 strsql = "SELECT count(1) FROM "
					+ tablename
					+ " WHERE S_BOOKORGCODE= ? AND  S_FILENAME= ? AND (S_STATUS IS NULL OR S_STATUS = ? )";
			} else if (msgno.equals(MsgConstant.MSG_NO_1106)) {
				strsql = "SELECT count(1) FROM "
					+ tablename
					+ " WHERE S_BOOKORGCODE= ? AND  S_FILENAME= ? AND S_FREEPLUPTRECODE= ? AND (S_STATUS IS NULL OR S_STATUS = ? )";
			}else if(msgno.equals(MsgConstant.APPLYPAY_DAORU)
					||msgno.equals(MsgConstant.APPLYPAY_BACK_DAORU)) {
				 strsql = "SELECT count(1) FROM "
					+ tablename
					+ " WHERE  S_BOOKORGCODE= ? AND  S_FILENAME= ? AND S_TRECODE= ? AND (S_STATE IS NULL OR S_STATE = ? )";
			}else {
				strsql = "SELECT count(1) FROM "
					+ tablename
					+ " WHERE S_BOOKORGCODE= ? AND  S_FILENAME= ? AND S_TRECODE= ? AND (S_STATUS IS NULL OR S_STATUS = ? )";
			}

			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam(filename);
			//国库主体
			if (!msgno.equals(MsgConstant.MSG_NO_1104) && !msgno.equals(MsgConstant.MSG_NO_5101)){
				sqlExec.addParam(trecode);
			}
			sqlExec.addParam(StateConstant.CONFIRMSTATE_NO);
			SQLResults rs = sqlExec.runQuery(strsql);
			
			if (msgno.equals(MsgConstant.MSG_NO_1104) || msgno.equals(MsgConstant.MSG_NO_5101)) {
				strsql = "SELECT count(1) FROM "
					+ tablename
					+ " WHERE S_BOOKORGCODE= ? AND  S_FILENAME= ? AND ( S_STATUS = ? AND S_PACKAGENO <>? )";
			} else if (msgno.equals(MsgConstant.MSG_NO_1106)) {
				strsql = "SELECT count(1) FROM "
					+ tablename
					+ " WHERE S_BOOKORGCODE= ? AND  S_FILENAME= ? AND S_FREEPLUPTRECODE= ? AND ( S_STATUS = ? AND S_PACKNO <>? )";
			}else if (msgno.equals(MsgConstant.APPLYPAY_DAORU)
					||msgno.equals(MsgConstant.APPLYPAY_BACK_DAORU)){
				strsql = "SELECT count(1) FROM "
					+ tablename
					+ " WHERE  S_BOOKORGCODE= ? AND  S_FILENAME= ? AND S_TRECODE= ? AND ( S_STATE = ? AND S_PACKNO <>? )";
			}else {
				strsql = "SELECT count(1) FROM "
					+ tablename
					+ " WHERE S_BOOKORGCODE= ? AND  S_FILENAME= ? AND S_TRECODE= ? AND ( S_STATUS = ? AND S_PACKAGENO <>? )";
			}

			
			sqlExec.addParam(loginfo.getSorgcode());
			sqlExec.addParam(filename);
			//国库主体
			if (!msgno.equals(MsgConstant.MSG_NO_1104) && !msgno.equals(MsgConstant.MSG_NO_5101)){
				sqlExec.addParam(trecode);
			}
			sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
			sqlExec.addParam("00000000");
			SQLResults rs2 = sqlExec.runQueryCloseCon(strsql);
			// 所销号记录对应的导入文件不存在未销号的数据,并且文件下有销号成功的记录才发送
			if (rs.getInt(0, 0) == 0 && rs2.getInt(0, 0) > 0) {
				// 按包流水号发送TIPS
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				if (msgno.equals(MsgConstant.MSG_NO_5101)) {
					strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO "
							+ " in (select distinct S_PACKAGENO from TV_PAYOUTMSGMAIN WHERE S_FILENAME = ? AND S_ORGCODE= ?)";
				} else if (msgno.equals(MsgConstant.MSG_NO_1104)) {
					strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO "
							+ " in (select distinct S_PACKAGENO from TV_DWBK WHERE S_FILENAME = ? AND S_ORGCODE= ?)";
				} else if (msgno.equals(MsgConstant.MSG_NO_1106)) {
					strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO "
							+ " in (select distinct S_PACKNO from TV_FREE WHERE S_FILENAME = ? AND S_FREEPLUPTRECODE= ?)";
				} else if (msgno.equals(MsgConstant.MSG_NO_5104)) {
					strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO "
							+ " in (select distinct S_PACKAGENO from TV_PBCPAY_MAIN WHERE S_FILENAME = ? AND S_TRECODE= ?)";
				}else if (msgno.equals(MsgConstant.APPLYPAY_DAORU)
						||msgno.equals(MsgConstant.APPLYPAY_BACK_DAORU)) {
					strsql = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO "
						    + " in (select distinct S_PACKNO from TBS_TV_BNKPAY_MAIN WHERE  S_FILENAME = ? AND S_TRECODE= ?)";
			}
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(filename);
				sqlExec.addParam(filename);
				//国库主体
				if (msgno.equals(MsgConstant.MSG_NO_1104) || msgno.equals(MsgConstant.MSG_NO_5101)){
					sqlExec.addParam(loginfo.getSorgcode());
				}else{
					sqlExec.addParam(trecode);
				}
				SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql);
				int row = rsfilepac.getRowCount();
				for (int i = 0; i < row; i++) {
					// // 按包流水号发送TIPS
					sendMsg(filename, loginfo.getSorgcode(), rsfilepac
							.getString(i, 0), msgno, rsfilepac.getString(i, 1),
							false);
				}
				/**
				 * 修改文件与包流水对应关系表(已发送，已销号)
				 */
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				String movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=?"
						+ " WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ?";
				sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
				sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(filename);
				sqlExec.addParam(pakageno);
				sqlExec.runQueryCloseCon(movedataSql);
			}

		} catch (JAFDatabaseException e) {
			logger.error("判断临时表是否已全部销号处理失败!", e);
			throw new ITFEBizException("判断临时表是否已全部销号处理失败!", e);
		} catch (ValidateException e) {
			logger.error("数据库操作异常!", e);
			throw new ITFEBizException("数据库操作异常!", e);
		}finally{
			if(sqlExec!=null){
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 实拨销号失败的记录.不在校验直接，写入正式表，包序号填写00000000
	 * 
	 * @throws ITFEBizException
	 */
	public static void recordFailVouForPay(Long ivousrlno, ITFELoginInfo loginfo)
			throws ITFEBizException {

		String sqlmain = "INSERT INTO TV_PAYOUTMSGMAIN(S_BIZNO,S_ORGCODE,S_COMMITDATE,S_ACCDATE,S_FILENAME,S_TRECODE,S_PACKAGENO,S_PAYUNIT,S_PAYEEBANKNO,S_DEALNO,S_TAXTICKETNO,S_GENTICKETDATE,S_PAYERACCT,S_PAYERNAME,S_PAYERADDR,N_MONEY,S_RECBANKNO,S_RECACCT,S_RECNAME,S_TRIMFLAG,S_BUDGETUNITCODE,S_UNITCODENAME,S_OFYEAR,S_BUDGETTYPE,S_STATUS,S_USERCODE,S_ADDWORD,S_DEMO,S_BACKFLAG,TS_SYSUPDATE) "
				+ " SELECT TRIM(CHAR(I_VOUSRLNO)),S_BOOKORGCODE,substr(CHAR(D_Accept),1,4)||substr(CHAR(D_Accept),6,2)||substr(CHAR(D_Accept),9,2),substr(CHAR(D_Accept),1,4)||substr(CHAR(D_Accept),6,2)||substr(CHAR(D_Accept),9,2),S_FILENAME,S_TRECODE,?,"
				+ " S_TRECODE,S_PAYEEBANKNO,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),S_VOUNO,substr(CHAR(D_VOUCHER),1,4)||substr(CHAR(D_VOUCHER),6,2)||substr(CHAR(D_VOUCHER),9,2),S_PAYERACCT,S_PAYERNAME,'',F_AMT,S_PAYEEOPNBNKNO,S_PAYEEACCT,S_PAYEENAME,"
				+ " C_TRIMFLAG,S_BDGORGCODE,S_BDGORGNAME,TRIM(CHAR(I_OFYEAR)),C_BDGKIND,?,?" 
				+ ",S_ADDWORD,S_MOVEFUNDREASON,'0',CURRENT TIMESTAMP FROM TBS_TV_PAYOUT A WHERE I_VOUSRLNO= ?";
		String sqlsub = "INSERT INTO TV_PAYOUTMSGSUB (S_BIZNO,S_SEQNO,S_ACCDATE,S_ECNOMICSUBJECTCODE,S_BUDGETPRJCODE,N_MONEY,S_FUNSUBJECTCODE,TS_SYSUPDATE) "
				+ "SELECT TRIM(CHAR(I_VOUSRLNO)),1,substr(CHAR(D_Accept),1,4)||substr(CHAR(D_Accept),6,2)||substr(CHAR(D_Accept),9,2),S_ECOSBTCODE,'',F_AMT,S_FUNCSBTCODE,CURRENT TIMESTAMP FROM TBS_TV_PAYOUT WHERE I_VOUSRLNO= ? ";
		SQLExecutor sqlExec = null;

		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.addParam("00000000");
			sqlExec.addParam(DealCodeConstants.DEALCODE_CHECK_FAIL);
			sqlExec.addParam(loginfo.getSuserCode());
			sqlExec.addParam(ivousrlno);
			sqlExec.runQuery(sqlmain);
			sqlExec.clearParams();
			sqlExec.addParam(ivousrlno);
			sqlExec.runQueryCloseCon(sqlsub);

		} catch (JAFDatabaseException e) {
			logger.error("销号失败的记录写正式表失败！!", e);
			throw new ITFEBizException("销号失败的记录写正式表失败!", e);
		}finally{
			if(sqlExec!=null){
				sqlExec.closeConnection();
			}
		}
	}

	/**
	 * 退库销号失败的记录.不在校验直接，写入正式表，包序号填写00000000
	 * 
	 * @throws ITFEBizException
	 */
	public static void recordFailVouForDwbk(Long ivousrlno)
			throws ITFEBizException {

		String sqlmain = " INSERT INTO TV_DWBK(I_VOUSRLNO,S_ELECVOUNO,S_DWBKVOUCODE,S_TAXORGCODE,S_AGENTTAXORGCODE, S_PAYERTRECODE,S_AIMTRECODE,C_BDGKIND,C_BDGLEVEL,S_BDGSBTCODE, S_DWBKREASONCODE,S_ASTFLAG,S_DWBKBY,F_DWBKRATIO,F_DWBKAMT, S_EXAMORG,F_AMT,C_BCKFLAG,S_PAYEECODE,S_PAYEEACCT, S_PAYEENAME,S_PAYEEOPNBNKNO,D_ACCEPT,D_ACCT,D_VOUCHER, D_BILL,S_ETPCODE,S_ECOCODE,C_TRIMFLAG,S_BIZTYPE, S_PACKAGENO,S_STATUS,S_DEALNO,S_FILENAME,S_BOOKORGCODE,TS_SYSUPDATE)"
				+ "SELECT  I_VOUSRLNO,S_ELECVOUNO,S_DWBKVOUCODE,S_TAXORGCODE,S_AGENTTAXORGCODE,S_PAYERTRECODE,S_AIMTRECODE,C_BDGKIND,C_BDGLEVEL,S_BDGSBTCODE, "
				+ " S_DWBKREASONCODE,S_ASTFLAG,S_DWBKBY,F_DWBKRATIO,F_DWBKAMT,S_EXAMORG,F_AMT,C_BCKFLAG,S_PAYEECODE,S_PAYEEACCT,  S_PAYEENAME,S_PAYEEOPNBNKNO,"
				+ "D_ACCEPT,D_ACCT,D_VOUCHER,  D_BILL,S_ETPCODE,S_ECOCODE,C_TRIMFLAG,S_BIZTYPE,  ?,?,"
				+ " substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8), S_FILENAME,S_BOOKORGCODE,CURRENT TIMESTAMP   FROM TBS_TV_DWBK A WHERE I_VOUSRLNO= ? ";
		try {
			SQLExecutor sqlExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam("00000000");
			sqlExec.addParam(DealCodeConstants.DEALCODE_CHECK_FAIL);
			sqlExec.addParam(ivousrlno);
			sqlExec.runQueryCloseCon(sqlmain);
		} catch (JAFDatabaseException e) {
			logger.error("销号失败的记录写正式表失败！!", e);
			throw new ITFEBizException("销号失败的记录写正式表失败!", e);
		}
	}

	/**
	 * 人行办理支付销号失败的记录.不在校验直接，写入正式表，包序号填写00000000
	 * 
	 * @throws ITFEBizException
	 */
	public static void recordFailVouForPbcPay(Long ivousrlno,
			ITFELoginInfo loginfo) throws ITFEBizException {
		String sqlmain = " INSERT INTO TV_PBCPAY_MAIN"
				+ "	(I_VOUSRLNO,S_TRANO,S_ORGCODE,S_TRECODE,S_BILLORG,"
				+ "	S_ENTRUSTDATE,S_PACKNO,S_PAYOUTVOUTYPENO,S_PAYMODE,S_VOUNO,D_VOUCHER,"
				+ "	S_PAYERACCT,S_PAYERNAME,S_PAYEEACCT,S_PAYEENAME,S_RCVBNKNO,S_PAYEEOPNBNKNO,  "
				+ "	S_ADDWORD,C_BDGKIND,I_OFYEAR,S_BDGADMTYPE,F_AMT,C_TRIMFLAG, "
				+ "	S_STATUS,S_BIZTYPE,S_BACKFLAG,S_INPUTERID,TS_SYSUPDATE) "
				+ "	select  "
				+ "	I_VOUSRLNO,substr(CHAR(100000000+nextval FOR ITFE_TRAID_SEQ),2,8),S_BOOKORGCODE,S_TRECODE,"
				+ "	(SELECT MAX(S_FINORGCODE) FROM TS_CONVERTFINORG WHERE S_ORGCODE=A.S_BOOKORGCODE AND S_TRECODE=A.S_TRECODE), "
				+ "	substr(CHAR(D_ACCEPT),1,4)||substr(CHAR(D_ACCEPT),6,2)||substr(CHAR(D_ACCEPT),9,2),"
				+ "	?,?,?,S_VOUNO,"
				+ "	substr(CHAR(D_VOUCHER),1,4)||substr(CHAR(D_VOUCHER),6,2)||substr(CHAR(D_VOUCHER),9,2), "
				+ "	S_PAYERACCT,S_PAYERNAME,S_PAYEEACCT,S_PAYEENAME,S_PAYEEOPNBNKNO,S_PAYEEOPNBNKNO, "
				+ "	'',C_BDGKIND,INTEGER(substr(CHAR(D_VOUCHER),1,4)),'',F_AMT,C_TRIMFLAG, "
				+ "	?,"
				+ "	S_BIZTYPE,S_BACKFLAG,"+ "?,CURRENT TIMESTAMP  "
				+ "	FROM  TBS_TV_PBCPAY A WHERE I_VOUSRLNO=? ";

		String sqlsub = "INSERT INTO TV_PBCPAY_SUB (I_VOUSRLNO,I_SEQNO,S_BDGORGCODE,S_FUNCSBTCODE,S_ECOSBTCODE,C_ACCTPROP,F_AMT,TS_SYSUPDATE)"
				+ "SELECT I_VOUSRLNO,1,S_BDGORGCODE,S_FUNCSBTCODE,S_ECOSBTCODE,'',F_AMT,CURRENT TIMESTAMP FROM TBS_TV_PBCPAY WHERE I_VOUSRLNO= ? ";

		SQLExecutor sqlExec = null;

		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExec.addParam("00000000");
			sqlExec.addParam("1");
			sqlExec.addParam("0");
			sqlExec.addParam(DealCodeConstants.DEALCODE_CHECK_FAIL);
			sqlExec.addParam(loginfo.getSuserCode());
			sqlExec.addParam(ivousrlno);
			sqlExec.runQuery(sqlmain);
			sqlExec.clearParams();
			sqlExec.addParam(ivousrlno);
			sqlExec.runQueryCloseCon(sqlsub);
		} catch (JAFDatabaseException e) {
			logger.error("销号失败的记录写正式表失败！!", e);
			throw new ITFEBizException("销号失败的记录写正式表失败!", e);
		}
	}

	/**
	 * 额度销号删除文件时 判断本包流水号中是否还有未销号的数据 没有则直接发送报文
	 */
	public static void checkAndSendMsgForPayplan(String msgno,
			String tablename, String packNo, ITFELoginInfo loginfo,
			String bizType) throws ITFEBizException {
		// 1、本操作要求在删除文件之后操作
		// a.判断包流水对应关系 是否还有未销号数据
		TvFilepackagerefDto refDto = new TvFilepackagerefDto();
		refDto.setSpackageno(packNo); // 参数为包流水号
		refDto.setSorgcode(loginfo.getSorgcode()); // 核算主体
		refDto.setSchkstate(StateConstant.CONFIRMSTATE_NO); // 交易状态
		try {
			SQLExecutor sqlExec = null;
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			// b.得到该包流水号下所有未销号数据集
			List refList = CommonFacade.getODB().findRsByDtoWithUR(refDto);
			// c.判读 1---数据集为空或NULL，直接发送报文
			// 2---存在数据则直接返回
			if (null == refList || refList.size() == 0) { // 发送报文
				String strsql = "SELECT count(1) FROM "
						+ tablename
						+ " WHERE S_BOOKORGCODE= ? AND S_PACKAGENO=? AND S_STATUS = ? ";
				sqlExec.addParam(loginfo.getSorgcode());
				sqlExec.addParam(packNo);
				sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
				SQLResults rs2 = sqlExec.runQueryCloseCon(strsql);
				// 所销号记录对应的导入文件不存在未销号的数据,并且文件下有销号成功的记录才发送
				if (rs2.getInt(0, 0) > 0) {
					// 按包流水号发送TIPS
					sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					String strsql1 = "SELECT DISTINCT S_PACKAGENO,S_COMMITDATE FROM TV_FILEPACKAGEREF WHERE S_ORGCODE = ? AND S_PACKAGENO=?";
					// 核算主体代码
					sqlExec.addParam(loginfo.getSorgcode());
					// 包流水号
					sqlExec.addParam(packNo);
					SQLResults rsfilepac = sqlExec.runQueryCloseCon(strsql1);
					int row = rsfilepac.getRowCount();
					for (int i = 0; i < row; i++) {
						// // 按包流水号发送TIPS
						sendMsgUtil.sendMsg("", loginfo.getSorgcode(),
								rsfilepac.getString(i, 0), msgno, rsfilepac
										.getString(i, 1), false);
					}
					/**
					 * 修改文件与包流水对应关系表(已发送，已销号)
					 */
					sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					String movedataSql = "UPDATE TV_FILEPACKAGEREF SET S_CHKSTATE=?,S_RETCODE=? WHERE S_ORGCODE= ? AND S_PACKAGENO= ? ";
					sqlExec.addParam(StateConstant.CONFIRMSTATE_YES);
					sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_SEND);
					sqlExec.addParam(loginfo.getSorgcode());
					sqlExec.addParam(packNo);
					sqlExec.runQueryCloseCon(movedataSql);
				}
			}
		} catch (Exception e) {
			logger.error("数据库操作异常!", e);
			throw new ITFEBizException("数据库操作异常!", e);
		}

	}

	public static void recordWaitForPayout(TbsTvPayoutDto paydto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		SQLExecutor sqlExec = null;
		try {
			TvFilepackagerefDto ref = new TvFilepackagerefDto();
			ref.setSorgcode(loginfo.getSorgcode());
			ref.setSpackageno(paydto.getSpackageno());
			List refl = CommonFacade.getODB().findRsByDtoWithUR(ref);
			if (refl != null) { // 只有一条记录 则只更新记录状态为待定
				TvFilepackagerefDto newRef = new TvFilepackagerefDto();
				newRef = (TvFilepackagerefDto) refl.get(0);
				if (newRef.getIcount() == 1) {
					paydto.setSbillorg(StateConstant.Check_AddWord_Wait);//设置s_billorg为销号待定字段
					String newName = paydto.getSfilename() + "_" + paydto.getSpackageno();
					paydto.setSfilename(newName);
					DatabaseFacade.getODB().update(paydto);
					DatabaseFacade.getODB().delete(newRef);//删除是因为文件名是主键无法修改(update)，只好先删除后创建
					newRef.setSfilename(newName); //将只有一笔记录的包流水对应关系的文件名同时修改，这样就销号单独组报文了而不受原文件名限制
					DatabaseFacade.getODB().create(newRef);
					return;
				}
				// 1.更改包流水对应关系
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();

				String movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
				// 凭证金额
				sqlExec.addParam(paydto.getFamt());
				// 核算主体代码
				sqlExec.addParam(loginfo.getSorgcode());
				// 文件名
				sqlExec.addParam(paydto.getSfilename());
				// 包流水号
				sqlExec.addParam(paydto.getSpackageno());
				sqlExec.runQueryCloseCon(movedataSql);
				String tmpPackNo = SequenceGenerator
						.changePackNoForLocal(SequenceGenerator.getNextByDb2(
								SequenceName.FILENAME_PACKNO_REF_SEQ,
								SequenceName.TRAID_SEQ_CACHE,
								SequenceName.TRAID_SEQ_STARTWITH,
								MsgConstant.SEQUENCE_MAX_DEF_VALUE));
				String newFileName = paydto.getSfilename() + "_" + tmpPackNo;
				paydto.setSpackageno(tmpPackNo); // 更新新流水号
//				paydto.setSaddword(StateConstant.Check_AddWord_Wait);
				paydto.setSbillorg(StateConstant.Check_AddWord_Wait);//设置s_billorg为销号待定字段
				paydto.setSfilename(newFileName);
				newRef.setSorgcode(loginfo.getSorgcode());
				newRef.setSpackageno(tmpPackNo);
				newRef.setIcount(1);
				newRef.setSfilename(newFileName);
				newRef.setNmoney(paydto.getFamt());
				DatabaseFacade.getODB().create(newRef); // 保存新的对应关系
				DatabaseFacade.getODB().update(paydto); // 更新信息
			}
		} catch (SequenceException e) {
			logger.error("获取包流水号异常!", e);
			throw new ITFEBizException("获取包流水号异常!", e);
		} catch (JAFDatabaseException e) {
			logger.error("数据库操作异常!", e);
			throw new ITFEBizException("数据库操作异常!", e);
		} catch (ValidateException e) {
			logger.error("数据库操作异常!", e);
			throw new ITFEBizException("数据库操作异常!", e);
		}
	}

}

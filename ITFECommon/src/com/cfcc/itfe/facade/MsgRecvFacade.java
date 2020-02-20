package com.cfcc.itfe.facade;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsOperationmodelDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvFreeDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 收到回执报文处理
 * 
 * @author zhouchuan
 * 
 */
public class MsgRecvFacade {

	private static Log logger = LogFactory.getLog(MsgRecvFacade.class);

	/**
	 * 根据报文编号作为检索条件更新报文头信息
	 * 
	 * @param String
	 *            smsgno 报文编号
	 * @param String
	 *            staxorgcode 机关代码(征收机关或出票单位)
	 * @param String
	 *            spackno 包流水号
	 * @param String
	 *            scommitdate 委托日期
	 * @param String
	 *            state 交易状态
	 * @throws ITFEBizException
	 */
	public static void updateMsgHeadByMsgNo(String smsgno, String staxorgcode,
			String spackno, String scommitdate, String state)
			throws ITFEBizException {
		/**
		 * 第一步 根据报文编号找到对应的业务类型
		 */
		String biztype = PublicSearchFacade.getBizTypeByMsgNo(smsgno);

		/**
		 * 第二步 更新报表头信息
		 */
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();

			String updateSql = " update "
					+ TvFilepackagerefDto.tableName()
					+ " set S_RETCODE = ? "
					+ " where S_TAXORGCODE = ? and S_COMMITDATE =? and S_PACKAGENO =? and S_OPERATIONTYPECODE = ? ";
			updateExec.addParam(state);
			updateExec.addParam(staxorgcode);
			updateExec.addParam(scommitdate);
			updateExec.addParam(spackno);
			updateExec.addParam(biztype);
			updateExec.runQueryCloseCon(updateSql);
		} catch (JAFDatabaseException e) {
			logger.error("更新报表头信息时出现数据库异常!", e);
			throw new ITFEBizException("更新报表头信息时出现数据库异常!", e);
		}
	}

	/**
	 * 根据报文编号作为检索条件更新报文头信息
	 * 
	 * @param String
	 *            smsgno 报文编号
	 * @param String
	 *            staxorgcode 机关代码(征收机关或出票单位)
	 * @param String
	 *            spackno 包流水号
	 * @param String
	 *            scommitdate 委托日期
	 * @param String
	 *            state 交易状态
	 * @throws ITFEBizException
	 */
	public static void updateMsgHeadByMsgNofor3201(String staxorgcode,
			String spackno, String scommitdate, String state)
			throws ITFEBizException {

		/**
		 * 第二步 更新报表头信息
		 */
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();

			String updateSql = " update "
					+ TvFilepackagerefDto.tableName()
					+ " set S_RETCODE = ? "
					+ " where S_TAXORGCODE = ? and S_COMMITDATE =? and S_PACKAGENO =?  ";
			updateExec.addParam(state);
			updateExec.addParam(staxorgcode);
			updateExec.addParam(scommitdate);
			updateExec.addParam(spackno);
			updateExec.runQueryCloseCon(updateSql);
		} catch (JAFDatabaseException e) {
			logger.error("更新报表头信息时出现数据库异常!", e);
			throw new ITFEBizException("更新报表头信息时出现数据库异常!", e);
		}
	}

	/*
	 * 根据原报文编号更新业务状态
	 */
	public static void updateMsgBizDetailByOriMsgNo(String msgref,
			String orimsgno, String state, String spackno,
			HashMap<String, String> map) throws ITFEBizException {

		/**
		 * 第一步 根据报文编号找到对应的业务类型
		 */
		String ls_UpdateSql = "";
		String tablename = map.get(orimsgno);
		// 主要针对9121,9120报文处理
		if(orimsgno.equals(MsgConstant.MSG_NO_5104) ||orimsgno.equals(MsgConstant.MSG_NO_2202)|| orimsgno.equals(MsgConstant.MSG_NO_1106)) {
			ls_UpdateSql = "update " + tablename
				+ " set S_STATUS = ? where S_PACKNO = ? AND S_STATUS = ? ";
		} else if(orimsgno.equals(MsgConstant.MSG_NO_2201)){
			ls_UpdateSql = "update " + tablename
			+ " set S_Result = ? where S_PACKNO = ? AND S_Result = ? ";
		}else{
			ls_UpdateSql = "update " + tablename
				+ " set S_STATUS = ? where S_PACKAGENO = ? AND S_STATUS = ? ";
		}

		/**
		 * 第二步 更新业务数据表
		 */
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();

			updateExec.addParam(state);
			updateExec.addParam(spackno);
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);

			updateExec.runQueryCloseCon(ls_UpdateSql);
			
		} catch (JAFDatabaseException e) {
			logger.error("更新业务明细表出现异常!" + tablename, e);
			throw new ITFEBizException("更新业务明细表出现异常!" + tablename, e);
		}
		//上海无纸化5201、5106、5108根据原报文编号更新业务主表或拆分凭证索引表状态
		updateMsgBizDetailByOriMsgNo(orimsgno, state, spackno);
	}

	
	/**
	 * 上海无纸化5201、5106、5108根据原报文编号更新业务主表或拆分凭证索引表状态
	 * @param orimsgno
	 * @param state
	 * @param spackno
	 * @throws ITFEBizException
	 */
	public static void updateMsgBizDetailByOriMsgNo(String orimsgno, String state, String spackno) 
		throws ITFEBizException {		
		//非上海无纸化不需要此流程
		if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")<0)
			return;
		//直接支付和授权支付额度接收TIPS回执更新拆分凭证索引表
		if(orimsgno.equals(MsgConstant.MSG_NO_5103)||orimsgno.equals(MsgConstant.MSG_NO_5102)){
			VoucherUtil.updateVoucherSplitReceiveTIPS(orimsgno, state, spackno);
			return;
		}		
		if(!orimsgno.equals(MsgConstant.MSG_NO_2201)&&!orimsgno.equals(MsgConstant.MSG_NO_5104))
			return;
		String tablename="TF_DIRECTPAYMSGMAIN";	
		String ls_UpdateSql =  "update " + tablename+ " set S_STATUS = ? where S_PACKAGENO = ? AND S_STATUS = ? ";
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			updateExec.addParam(state);
			updateExec.addParam(spackno);
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);
			updateExec.runQueryCloseCon(ls_UpdateSql);			
		} catch (JAFDatabaseException e) {
			logger.error("更新业务明细表出现异常!" + tablename, e);
			throw new ITFEBizException("更新业务明细表出现异常!" + tablename, e);
		}
	}

	
	/*
	 * 根据报文的MSG_ID更新关系表包流水号状态
	 */
	public static void updateMsgHeadByMsgId(String smsgid, String state,
			String sdemo) throws ITFEBizException {
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			String updateSql = " update " + TvFilepackagerefDto.tableName()
					+ " set S_RETCODE = ? ,s_demo = ?" + " where S_MSGID = ? ";
			updateExec.addParam(state);
			updateExec.addParam(sdemo);
			updateExec.addParam(smsgid);
			updateExec.runQueryCloseCon(updateSql);
		} catch (JAFDatabaseException e) {
			logger.error("更新报表头信息时出现数据库异常!", e);
			throw new ITFEBizException("更新报表头信息时出现数据库异常!", e);
		}
	}

	/**
	 * 更新发送日志的状态回填接收日志流水号
	 * 
	 * @param _dto
	 * @param state
	 * @param recvno
	 * @param sdemo
	 * @throws ITFEBizException
	 */
	public static void updateMsgSendLogByMsgId(TvSendlogDto _dto, String state,
			String recvno, String sdemo) throws ITFEBizException {
		try {
			_dto.setSretcode(state);
			if (null != recvno) {
				_dto.setSrecvno(recvno);
			}
			_dto.setSdemo(sdemo);
			DatabaseFacade.getODB().update(_dto);
		} catch (JAFDatabaseException e) {
			logger.error("更新报表头信息时出现数据库异常!", e);
			throw new ITFEBizException("更新报表头信息时出现数据库异常!", e);
		}
	}

	/*
	 * 根据TC回执报文更新业务表状态
	 */
	public static void updateMsgByMsgPackageNo(String orimsgno,
			String oripackno, String orientrustDate, String sstate)
			throws ITFEBizException {
		String ls_UpdateSql = "";
		// 主要针对9121报文处理
		if (orimsgno.equals("1104")) {
			// 退库业务
			ls_UpdateSql = "update "
					+ TvDwbkDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ? and S_STATUS != ? and D_ACCEPT = ? ";
			orientrustDate = CommonUtil.strToDate(orientrustDate).toString();
		} else if (orimsgno.equals("1105")) {
			// 更正
			ls_UpdateSql = "update "
					+ TvInCorrhandbookDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ?  and S_STATUS != ? and D_ACCEPT = ? ";
			orientrustDate = CommonUtil.strToDate(orientrustDate).toString();
		} else if (orimsgno.equals("1106")) {
			// 免抵调
			ls_UpdateSql = "update "
					+ TvFreeDto.tableName()
					+ " set S_STATUS = ? where S_PACKNO = ?  and S_STATUS != ? and D_ACCEPTDATE = ? ";
			orientrustDate = CommonUtil.strToDate(orientrustDate).toString();
		} else if (orimsgno.equals("5101")) {
			// 实拨资金
			ls_UpdateSql = "update "
					+ TvPayoutmsgmainDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ?  and S_STATUS != ? and S_COMMITDATE = ? ";
		} else if (orimsgno.equals("5102")) {
			// 直接支付额度
			ls_UpdateSql = "update "
					+ TvDirectpaymsgmainDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ?  and S_STATUS != ? and S_COMMITDATE = ? ";
		} else if (orimsgno.equals("5103")) {
			// 授权支付额度
			ls_UpdateSql = "update "
					+ TvGrantpaymsgmainDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ?  and S_STATUS != ? and S_COMMITDATE = ? ";
		} else if (orimsgno.equals("5112")) {
			// 代发财政性款项请求
			ls_UpdateSql = "update "
					+ TvPayoutfinanceMainDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ?  and S_STATUS != ? and S_ENTRUSTDATE = ? ";
		} else if (orimsgno.equals("7211")) {
			// 收入税票
			ls_UpdateSql = "update "
					+ TvInfileDto.tableName()
					+ " set S_STATUS = ? where S_PACKAGENO = ?  and S_STATUS != ? and S_COMMITDATE = ? ";
		} else if (orimsgno.equals("5104")) {
			//人行办理直接支付
			ls_UpdateSql = "update "
					+ TvPbcpayMainDto.tableName()
					+ " set S_STATUS = ? where S_PACKNO = ?  and S_STATUS != ? and S_ENTRUSTDATE = ? ";
		}else if (orimsgno.equals("2201")) {
			//2201划款申请
			ls_UpdateSql = "update "
					+ TvPayreckBankDto.tableName()
					+ " set S_Result = ? where s_PackNo = ?  and S_Result != ? and d_EntrustDate = ? "; 
			orientrustDate = CommonUtil.strToDate(orientrustDate).toString();
		}else if (orimsgno.equals("2202")) {
			//2202划款申请退回
				ls_UpdateSql = "update "
						+ TvPayreckBankBackDto.tableName()
						+ " set s_STATUS = ? where s_PackNo = ?  and s_STATUS != ? and d_EntrustDate = ? "; 
			orientrustDate = CommonUtil.strToDate(orientrustDate).toString();
		}else {
			logger.info("此报文编号" + orimsgno + "没有对应的业务表!");
		}
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.clearParams();
			updateExec.addParam(sstate);
			updateExec.addParam(oripackno);
			updateExec.addParam(sstate);
			updateExec.addParam(orientrustDate);
			updateExec.runQueryCloseCon(ls_UpdateSql);
		} catch (JAFDatabaseException e) {
			logger.error("更新报表头信息时出现数据库异常!", e);
			throw new ITFEBizException("更新报表头信息时出现数据库异常!", e);
		}
	}

	/*
	 * 根据回执报文更新日志表状态
	 */
	public static void updateSendLogByPackageNo(String orimsgno,
			String orisendorgcode, String oripackno, String orientrustDate,
			String sstate) throws ITFEBizException {
		String ls_UpdateSql = " update "
				+ TvSendlogDto.tableName()
				+ " set S_RETCODE = ? "
				+ " where S_PACKNO = ? and S_DATE = ? and S_SENDORGCODE = ? and S_OPERATIONTYPECODE = ? ";
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.clearParams();
			updateExec.addParam(sstate);
			updateExec.addParam(oripackno);
			updateExec.addParam(orientrustDate);
			updateExec.addParam(orisendorgcode);
			updateExec.addParam(orimsgno);
			updateExec.runQueryCloseCon(ls_UpdateSql);
		} catch (JAFDatabaseException e) {
			logger.error("更新报表头信息时出现数据库异常!", e);
			throw new ITFEBizException("更新报表头信息时出现数据库异常!", e);
		}
	}

	/*
	 * 根据回执报文更新日志表状态
	 */
	public static void updateRecvLogByPackageNo(String orimsgno,
			String orisendorgcode, String oripackno, String orientrustDate,
			String sstate) throws ITFEBizException {
		String ls_UpdateSql = " update "
				+ TvRecvlogDto.tableName()
				+ " set S_RETCODE = ? "
				+ " where S_PACKNO = ? and S_DATE = ? and S_SENDORGCODE = ?  and S_OPERATIONTYPECODE = ? ";
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.clearParams();
			updateExec.addParam(sstate);
			updateExec.addParam(oripackno);
			updateExec.addParam(orientrustDate);
			updateExec.addParam(orisendorgcode);
			updateExec.addParam(orimsgno);
			updateExec.runQueryCloseCon(ls_UpdateSql);
		} catch (JAFDatabaseException e) {
			logger.error("更新报表头信息时出现数据库异常!", e);
			throw new ITFEBizException("更新报表头信息时出现数据库异常!", e);
		}
	}

	/**
	 * 
	 * 根据MSGID查找发送日志表
	 * 
	 * @param smsgid
	 * @return
	 * @throws ITFEBizException
	 */
	public static TvSendlogDto findSendLogByMsgId(String smsgid, String msgno)
			throws ITFEBizException {
		try {
			TvSendlogDto _dto = new TvSendlogDto();
			_dto.setSseq(smsgid);
			_dto.setSoperationtypecode(msgno);
			List<TvSendlogDto> list = CommonFacade.getODB()
					.findRsByDtoForWhere(_dto, "And 1=1");
			if (list.size() > 0) {
				TvSendlogDto orisenddto = list.get(0);
				return orisenddto;
			}
		} catch (JAFDatabaseException e) {
			logger.error("根据MSGID查找发送日志表出现异常!", e);
			throw new ITFEBizException("根据MSGID查找发送日志表出现异常!", e);
		} catch (ValidateException e) {
			logger.error("根据MSGID查找发送日志表出现异常!", e);
			throw new ITFEBizException("根据MSGID查找发送日志表出现异常!", e);
		}
		return null;
	}

	/**
	 * 
	 * 根据MSGID查找发送日志表
	 * 
	 * @param smsgid
	 * @return
	 * @throws ITFEBizException
	 */
	public static TvSendlogDto findSendLogByMsgId(String msgno, String billorg,
			String sdate, String spackno, String result)
			throws ITFEBizException {
		try {
			TvSendlogDto _dto = new TvSendlogDto();
			_dto.setSbillorg(billorg);
			_dto.setSoperationtypecode(msgno);
			_dto.setSdate(sdate);
			_dto.setSpackno(spackno);
			List<TvSendlogDto> list = CommonFacade.getODB()
					.findRsByDtoForWhere(_dto, "And 1=1");
			if (list.size() > 0) {
				TvSendlogDto orisenddto = list.get(0);
				return orisenddto;
			}
		} catch (JAFDatabaseException e) {
			logger.error("根据MSGID查找发送日志表出现异常!", e);
			throw new ITFEBizException("根据MSGID查找发送日志表出现异常!", e);
		} catch (ValidateException e) {
			logger.error("根据MSGID查找发送日志表出现异常!", e);
			throw new ITFEBizException("根据MSGID查找发送日志表出现异常!", e);
		}
		return null;
	}

	/**
	 * 根据三要素条件作为检索条件更新报文头信息
	 * 
	 * @param String
	 *            smsgno 报文编号
	 * @param String
	 *            staxorgcode 机关代码(征收机关或出票单位)
	 * @param String
	 *            spackno 包流水号
	 * @param String
	 *            scommitdate 委托日期
	 * @param String
	 *            state 交易状态
	 * @throws ITFEBizException
	 */
	public static void updateMsgHeadByCon(String staxorgcode, String spackno,
			String scommitdate, String state) throws ITFEBizException {
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();

			String updateSql = " update "
					+ TvFilepackagerefDto.tableName()
					+ " set S_RETCODE = ? "
					+ " where S_TAXORGCODE = ? and S_COMMITDATE =? and S_PACKAGENO =? ";
			updateExec.addParam(state);
			updateExec.addParam(staxorgcode);
			updateExec.addParam(scommitdate);
			updateExec.addParam(spackno);
			updateExec.runQueryCloseCon(updateSql);
		} catch (JAFDatabaseException e) {
			logger.error("更新文件与包对应关系出现异常!", e);
			throw new ITFEBizException("更新文件与包对应关系出现异常!", e);
		}
	}

	/**
	 * 国库代码取得核算主体代码
	 * 
	 * @param strecode
	 * @return
	 * @throws ITFEBizException
	 */
	public static TsTreasuryDto getBookOrgByTre(String strecode)
			throws ITFEBizException {
		try {
			TsTreasuryDto _dto = new TsTreasuryDto();
			_dto.setStrecode(strecode);
			List<TsTreasuryDto> list = CommonFacade.getODB().findRsByDtoWithUR(
					_dto);
			if (null != list && list.size() == 1) {
				return list.get(0);
			}
		} catch (JAFDatabaseException e) {
			logger.error("根据国库代码查询对应的核算主体出错!", e);
			throw new ITFEBizException("根据国库代码查询对应的核算主体出错!", e);
		} catch (ValidateException e) {
			logger.error("根据国库代码查询对应的核算主体出错!", e);
			throw new ITFEBizException("根据国库代码查询对应的核算主体出错!", e);
		}
		return null;

	}

	/**
	 * 根据财政代码得到核算主体代码
	 * 
	 * @param strecode
	 * @return
	 * @throws ITFEBizException
	 */
	public static TsConvertfinorgDto getBookOrgByFin(String sFinCode)
			throws ITFEBizException {
		try {
			TsConvertfinorgDto _dto = new TsConvertfinorgDto();
			_dto.setSfinorgcode(sFinCode);
			List<TsConvertfinorgDto> list = CommonFacade.getODB()
					.findRsByDtoWithUR(_dto);
			if (null != list && list.size() == 1) {
				return list.get(0);
			}
		} catch (JAFDatabaseException e) {
			logger.error("根据财政代码查询对应的核算主体出错!", e);
			throw new ITFEBizException("根据财政代码查询对应的核算主体出错!", e);
		} catch (ValidateException e) {
			logger.error("根据财政代码查询对应的核算主体出错!", e);
			throw new ITFEBizException("根据财政代码查询对应的核算主体出错!", e);
		}
		return null;

	}

	/**
	 * 根据原包号更新成新包号
	 * 
	 * @param msgno
	 * @param oriPackNo
	 * @param newPackNo
	 * @param map
	 * @throws ITFEBizException
	 */
	public static void updateNewPackNoByOldPackNo(String msgno,
			String oriPackNo, String newPackNo, HashMap<String, String> map)
			throws ITFEBizException {
		String tablename = map.get(msgno);
		String sql = "";
		if(msgno.equals(MsgConstant.PBC_DIRECT_IMPORT) ||msgno.equals(MsgConstant.MSG_NO_2201)||msgno.equals(MsgConstant.MSG_NO_2202)||msgno.equals(MsgConstant.MSG_NO_1106)) {
			sql ="update " + tablename
				+ " set S_PACKNO = ? where S_PACKNO = ? ";
		}else {
			sql = "update " + tablename
			+ " set S_PACKAGENO = ? where S_PACKAGENO = ? ";
		}		
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.addParam(newPackNo);
			updateExec.addParam(oriPackNo);
			updateExec.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			logger.error("更新业务明细表出现异常!" + tablename, e);
			throw new ITFEBizException("更新业务明细表出现异常!" + tablename, e);
		}
	}

	/**
	 * 将文件与包对应关系中旧包号改为新包号
	 * 
	 * @param msgno
	 * @param oriPackNo
	 * @param newPackNo
	 * @param map
	 * @throws ITFEBizException
	 */
	public static void updateFileRefPackNo(String oriPackNo, String newPackNo)
			throws ITFEBizException {
		String sql = "update " + TvFilepackagerefDto.tableName()
				+ " set S_PACKAGENO = ? where S_PACKAGENO = ? ";
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.addParam(newPackNo);
			updateExec.addParam(oriPackNo);
			updateExec.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			logger
					.error("更新文件与包对应关系出现异常!" + TvFilepackagerefDto.tableName(),
							e);
			throw new ITFEBizException("更新文件与包对应关系出现异常!"
					+ TvFilepackagerefDto.tableName(), e);
		}
	}
	
	/**
	 * 将凭证索引表中的旧包号改为新包号
	 * 
	 * @param msgno
	 * @param oriPackNo
	 * @param newPackNo
	 * @param map
	 * @throws ITFEBizException
	 */
	public static void updateVoucherInfoByOldPackNo(String oriPackno, 
			String tmpPackNo)
			throws ITFEBizException {
		String sql = "update " + TvVoucherinfoDto.tableName()
				+ " set S_PACKNO = ? where S_PACKNO = ? ";
		try {
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.addParam(tmpPackNo);
			updateExec.addParam(oriPackno);
			updateExec.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			logger.error("更新凭证索引表出现异常!" + TvVoucherinfoDto.tableName(),e);
			throw new ITFEBizException("更新凭证索引表出现异常!" + TvVoucherinfoDto.tableName(), e);
		}
	}

	/**
	 * 接收TIPS下发的入库流水等下发报文
	 * @param eventContext
	 * @param msgno
	 * @throws ITFEBizException
	 */
	public static void recvMsglog(MuleEventContext eventContext, String msgno) throws ITFEBizException {
		try {
			String recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
			TsOperationmodelDto _dto = new TsOperationmodelDto();
			String _smodelid = recvseqno.substring(0, 10);
			int _ino = Integer.valueOf(recvseqno.substring(10, 20));
			String path = (String) eventContext.getMessage().getProperty(
					"XML_MSG_FILE_PATH");
			_dto.setSmodelid(_smodelid);
			_dto.setIno(_ino);
			_dto.setSoperationtypecode(msgno);
			_dto.setSmodelsavepath(path);
			DatabaseFacade.getDb().create(_dto);
			if (StateConstant.SendFinYes
					.equals(ITFECommonConstant.IFSENDMSGTOFIN)) {
				//设置TIPS主动下发的MQCORRELID
				eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
				Object msg = eventContext.getMessage().getProperty("MSG_INFO");
				eventContext.getMessage().setPayload(msg);
			} else {
				eventContext.setStopFurtherProcessing(true);
				return;
			}
		} catch (SequenceException e) {
			logger.error("取接收流水号失败!", e);
			throw new ITFEBizException("取接收流水号失败!", e);
		} catch (JAFDatabaseException e) {
			logger.error("记接收报文日志表失败!", e);
			throw new ITFEBizException("记录接收报文日志表失败!", e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

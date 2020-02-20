package com.cfcc.itfe.service.recbiz.reportdownload;

import java.io.File;
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
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLBatchRetriever;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan 说明：这里的报表下载因为业务需求，暂时不提供调整期报表数据
 * @time 09-11-09 10:51:29 codecomment:
 */

public class RptDownloadService extends AbstractRptDownloadService {

	public static final int MAX_NUM = 5000; // 每次取出最大记录数

	private static Log logger = LogFactory.getLog(RptDownloadService.class);

	/**
	 * 报表数据下载
	 * 
	 * @generated
	 * @param String
	 *            strecode 国库代码
	 * @param String
	 *            rptDate 报表代码
	 * @param String
	 *            taxprop 征收机关性质
	 * @param String
	 *            rpttype 报表范围
	 * @return java.util.List
	 * @throws ITFEBizException
	 */
	public List downloadRpt(String strecode, String rptDate, String taxprop,
			String rpttype) throws ITFEBizException {
		String sorgcode = this.getLoginInfo().getSorgcode(); // 取得机构代码
		String dirsep = File.separator; // 取得系统分割符
		String srcpath = rptDate + dirsep + sorgcode + dirsep + "report"
				+ dirsep; // 原路径
		List<String> filepathList = new ArrayList<String>();

		if (MsgConstant.RULE_SIGN_ALL.equals(rpttype)
				&& MsgConstant.MSG_TAXORG_FINANCE_PROP.equals(taxprop)) {
			// 财政类报表(需要全辖收入报表和库存报表)
			String stockfilepath = getStockRpt(strecode, rptDate, srcpath);
			if (null != stockfilepath) {
				filepathList.add(stockfilepath);
			}
		}

		String incomefilepath = getIncomeRpt(strecode, rptDate, srcpath,
				taxprop, rpttype);
		if (null != incomefilepath) {
			filepathList.add(incomefilepath);
		}

		return filepathList;
	}

	/**
	 * 报表申请
	 * 
	 * @generated
	 * @param String
	 *            strecode 国库代码
	 * @param String
	 *            srptdate 报表日期
	 * @throws ITFEBizException
	 */
	public void requestRpt(String strecode, String srptdate)
			throws ITFEBizException {
		try {
			/**
			 * 第一步 根据国库代码找到对应的财政代码
			 */
			TsConvertfinorgDto finadto = new TsConvertfinorgDto();
			finadto.setStrecode(strecode);
//			if (!StateConstant.ORG_CENTER_CODE.equals(getLoginInfo().getSorgcode())) {
//				finadto.setSorgcode(this.getLoginInfo().getSorgcode());
//			}
			
			List<TsConvertfinorgDto> list = CommonFacade.getODB().findRsByDto(
					finadto);
			if (null == list || list.size() == 0) {
				logger.error("请在财政机构信息中维护国库和财政代码的对应关系!国库代码:"+strecode);
				throw new ITFEBizException("请在财政机构信息中维护国库和财政代码的对应关系!国库代码:"+strecode);
			}

			String finorgcode = list.get(0).getSfinorgcode();

			/**
			 * 第二步 根据财政代码和申请报表日期调用后台处理
			 */
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
					MsgConstant.MSG_NO_5001 + "_OUT");
			message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, finorgcode);
			message.setProperty(MessagePropertyKeys.MSG_DATE, srptdate);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, getLoginInfo()
					.getSorgcode());
			message.setPayload(map);
			if(strecode!=null&&strecode.startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
			ServiceUtil.checkResult(message);
		} catch (MuleException e) {
			logger.error("调用YAK后台报文处理出现异常!", e);
			throw new ITFEBizException("调用YAK后台报文处理出现异常!", e);
		} catch (JAFDatabaseException e) {
			logger.error("调用后台处理出现数据库异常!", e);
			throw new ITFEBizException("调用后台处理出现数据库异常!", e);
		} catch (ValidateException e) {
			logger.error("调用后台处理出现数据库异常!", e);
			throw new ITFEBizException("调用后台处理出现数据库异常!", e);
		}
	}

	/**
	 * 收入报表
	 * 
	 * @param String
	 *            strecode 国库代码
	 * @param String
	 *            rptDate 报表日期
	 * @param String
	 *            srcpath 跟路径
	 * @param String
	 *            taxprop 征收机关性质
	 * @param String
	 *            rpttype 报表类型
	 * @return
	 * @throws ITFEBizException
	 */
	private String getIncomeRpt(String strecode, String rptDate,
			String srcpath, String taxprop, String rpttype)
			throws ITFEBizException {
		SQLBatchRetriever batchRetriever = null;
		boolean isAllIncomeRecordflag = false;
		String selectSQL = null;

		try {
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			String taxsql = ""; // 文件名称中的征收机关代码

			if (MsgConstant.MSG_TAXORG_FINANCE_PROP.equals(taxprop)) {
				taxsql = MsgConstant.MSG_TAXORG_SHARE_CLASS;

				if (MsgConstant.RULE_SIGN_ALL.equals(rpttype)) {
					// 如果是财政全辖-征收机关代码为不分
					selectSQL = "select S_TRECODE,S_BUDGETLEVELCODE,S_TAXORGCODE,S_BUDGETSUBCODE,S_RPTDATE,S_BUDGETTYPE,N_MONEYDAY,N_MONEYMONTH,N_MONEYYEAR"
							+ " from "
							+ TrIncomedayrptDto.tableName()
							+ " where S_TRECODE = ? and S_RPTDATE = ? and S_BELONGFLAG = ? and S_TRIMFLAG = ? and S_TAXORGCODE = ? with ur";

					batchRetriever.clearParams();
					batchRetriever.addParam(strecode); // 国库代码
					batchRetriever.addParam(rptDate); // 报表日期
					batchRetriever.addParam(rpttype); // 辖属标志
					batchRetriever.addParam(MsgConstant.TIME_FLAG_NORMAL); // 调整期标志为正常期
					batchRetriever.addParam(MsgConstant.MSG_TAXORG_SHARE_CLASS); // 不分征收机关
					batchRetriever.setMaxRows(MAX_NUM); // 设置每次取记录的最大数
				} else {
					// 如果是财政本级-不需要征收机关做为条件
					selectSQL = "select S_TRECODE,S_BUDGETLEVELCODE,S_TAXORGCODE,S_BUDGETSUBCODE,S_RPTDATE,S_BUDGETTYPE,N_MONEYDAY,N_MONEYMONTH,N_MONEYYEAR"
							+ " from "
							+ TrIncomedayrptDto.tableName()
							+ " where S_TRECODE = ? and S_RPTDATE = ? and S_BELONGFLAG = ? and S_TRIMFLAG = ? with ur";

					batchRetriever.clearParams();
					batchRetriever.addParam(strecode); // 国库代码
					batchRetriever.addParam(rptDate); // 报表日期
					batchRetriever.addParam(rpttype); // 辖属标志
					batchRetriever.addParam(MsgConstant.TIME_FLAG_NORMAL); // 调整期标志为正常期
					batchRetriever.setMaxRows(MAX_NUM); // 设置每次取记录的最大数
				}
			} else {
				taxsql = getTaxBigClassByProp(taxprop);

				if (MsgConstant.RULE_SIGN_ALL.equals(rpttype)) {
					// 如果是国地税全辖-征收机关代码为各自的部门大类代码
					selectSQL = "select S_TRECODE,S_BUDGETLEVELCODE,S_TAXORGCODE,S_BUDGETSUBCODE,S_RPTDATE,S_BUDGETTYPE,N_MONEYDAY,N_MONEYMONTH,N_MONEYYEAR"
							+ " from "
							+ TrIncomedayrptDto.tableName()
							+ " where S_TRECODE = ? and S_RPTDATE = ? and S_BELONGFLAG = ? and S_TRIMFLAG = ? and S_TAXORGCODE = ? with ur";

					batchRetriever.clearParams();
					batchRetriever.addParam(strecode); // 国库代码
					batchRetriever.addParam(rptDate); // 报表日期
					batchRetriever.addParam(rpttype); // 辖属标志
					batchRetriever.addParam(MsgConstant.TIME_FLAG_NORMAL); // 调整期标志为正常期
					batchRetriever.addParam(taxsql); // 各个部分的征收机关大类
					batchRetriever.setMaxRows(MAX_NUM); // 设置每次取记录的最大数
				} else {
					// 如果是国地税本级-按照征收机关性质做为条件
					selectSQL = "select tn.S_TRECODE,tn.S_BUDGETLEVELCODE,ts.S_TBSTAXORGCODE,tn.S_BUDGETSUBCODE,tn.S_RPTDATE,tn.S_BUDGETTYPE,tn.N_MONEYDAY,tn.N_MONEYMONTH,tn.N_MONEYYEAR"
							+ " from "
							+ TrIncomedayrptDto.tableName()
							+ " tn , "
							+ TsConverttaxorgDto.tableName()
							+ " ts "
							+ " where tn.S_TAXORGCODE = ts.S_TCBSTAXORGCODE and tn.S_TRECODE = ts.S_TRECODE and ts.I_MODICOUNT = ? and tn.S_TRECODE = ? and tn.S_RPTDATE = ?"
							+ " and tn.S_BELONGFLAG = ? and tn.S_TRIMFLAG = ? and ts.S_TBSTAXORGCODE <> 'N' with ur";

					batchRetriever.clearParams();
					batchRetriever.addParam(taxprop); // 机关代码性质
					batchRetriever.addParam(strecode); // 国库代码
					batchRetriever.addParam(rptDate); // 报表日期
					batchRetriever.addParam(rpttype); // 辖属标志
					batchRetriever.addParam(MsgConstant.TIME_FLAG_NORMAL); // 调整期标志为正常期
					batchRetriever.setMaxRows(MAX_NUM); // 设置每次取记录的最大数
				}

			}

			String incomeAllPath = srcpath + "s" + strecode + "_1" + rpttype
					+ rptDate + "_" + taxsql + ".txt";

			batchRetriever.runQuery(selectSQL);
			// 预算收入报表格式头
			StringBuffer allbuf = new StringBuffer();
			allbuf
					.append("skgkdm,mdgkdm,ssgkdm,ysjc,jgdm,kmdm,zwrq,yszl,rlj,ylj,nlj");
			allbuf.append(StateConstant.SPACE_SPLIT);

			// 第二步 删除服务器文件(可能已经生成过)
			FileUtil.getInstance().deleteFiles(
					ITFECommonConstant.FILE_ROOT_PATH + incomeAllPath);

			while (batchRetriever.hasMore()) {
				SQLResults result = batchRetriever.RetrieveNextBatch();

				int count = result.getRowCount();
				for (int i = 0; i < count; i++) {
					isAllIncomeRecordflag = true;
					int j = 0;
					String trecode = result.getString(i, j++);
					allbuf.append(trecode); // 国库代码
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(trecode); // 国库代码
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(trecode); // 国库代码
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getString(i, j++)); // 预算级次
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getString(i, j++)); // 征收机关代码
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getString(i, j++)); // 预算科目代码
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getString(i, j++)); // 报表日期
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getString(i, j++)); // 预算种类
					allbuf.append(StateConstant.INCOME_SPLIT);
					// allbuf.append(result.getString(i, j++)); // 预算科目代码名称
					// allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getBigDecimal(i, j++)); // 日累计
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getBigDecimal(i, j++)); // 月累计
					allbuf.append(StateConstant.INCOME_SPLIT);
					allbuf.append(result.getBigDecimal(i, j++)); // 年累计
					allbuf.append(StateConstant.SPACE_SPLIT);
				}
				// 追加方式写文件
				FileUtil.getInstance().writeFile(
						ITFECommonConstant.FILE_ROOT_PATH + incomeAllPath,
						allbuf.toString(), true);

				allbuf = new StringBuffer();
			}

			batchRetriever.closeConnection();

			if (isAllIncomeRecordflag) {
				return incomeAllPath;
			} else {
				return null;
			}
		} catch (JAFDatabaseException e) {
			String error = "查询预算收入报表时出现数据库异常！";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} catch (FileOperateException e) {
			String error = "生成预算收入报表文件时出现异常！";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}
		}
	}

	/**
	 * 库存日报
	 * 
	 * @param String
	 *            strecode 国库代码
	 * @param String
	 *            srptdate 报表日期
	 * @param String
	 *            srcpath 报表根路径
	 * 
	 * @throws ITFEBizException
	 */
	private String getStockRpt(String strecode, String rptDate, String srcpath)
			throws ITFEBizException {

		SQLBatchRetriever batchRetriever = null;
		try {
			boolean isStockRecordflag = false;
			String stockSQL = "select S_ACCNO,S_ACCNAME,S_ACCDATE,N_MONEYYESTERDAY,N_MONEYIN,N_MONEYOUT,N_MONEYTODAY "
					+ " from "
					+ TrStockdayrptDto.tableName()
					+ " where S_TRECODE = ? and S_RPTDATE = ? order by S_ACCNO with ur";

			// 第一步 查询符合条件的记录
			batchRetriever = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLBatchRetriever();
			batchRetriever.addParam(strecode); // 国库代码
			batchRetriever.addParam(rptDate); // 报表日期
			batchRetriever.setMaxRows(MAX_NUM); // 设置每次取记录的最大数

			batchRetriever.runQuery(stockSQL);

			// 库存报表格式头
			StringBuffer sbuf = new StringBuffer();
			sbuf.append("kjzh,kjzhm,zwrq,srye,brdfsr,brjfzc,brye");
			sbuf.append(StateConstant.SPACE_SPLIT);

			// String stockSelfPath = srcpath + strecode + "_" +
			// MsgConstant.RULE_SIGN_SELF + "_" + MsgConstant.REPORT_TYPE_STOCK
			// + ".txt";
			String stockSelfPath = srcpath + "s" + strecode + "_c"
					+ MsgConstant.RULE_SIGN_SELF + rptDate + ".txt";

			// 第二步 删除服务器文件(可能已经生成过)
			FileUtil.getInstance().deleteFiles(
					ITFECommonConstant.FILE_ROOT_PATH + stockSelfPath);

			while (batchRetriever.hasMore()) {
				SQLResults result = batchRetriever.RetrieveNextBatch();

				int count = result.getRowCount();
				for (int i = 0; i < count; i++) {
					isStockRecordflag = true;
					int j = 0;
					// sbuf.append(result.getString(i, j++)); // 国库代码
					// sbuf.append(StateConstant.INCOME_SPLIT);
					// sbuf.append(result.getString(i, j++)); // 报表日期
					// sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getString(i, j++)); // 帐户代码
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getString(i, j++)); // 帐户名称
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getString(i, j++)); // 帐户日期
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getBigDecimal(i, j++)); // 上日余额
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getBigDecimal(i, j++)); // 本日收入
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getBigDecimal(i, j++)); // 本日支出
					sbuf.append(StateConstant.INCOME_SPLIT);
					sbuf.append(result.getBigDecimal(i, j++)); // 本日余额
					sbuf.append(StateConstant.SPACE_SPLIT);
				}
				// 追加方式写文件
				FileUtil.getInstance().writeFile(
						ITFECommonConstant.FILE_ROOT_PATH + stockSelfPath,
						sbuf.toString(), true);

				sbuf = new StringBuffer();
			}

			batchRetriever.closeConnection();

			if (isStockRecordflag) {
				return stockSelfPath;
			} else {
				return null;
			}
		} catch (JAFDatabaseException e) {
			String error = "查询库存日报表时出现数据库异常！";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} catch (FileOperateException e) {
			String error = "生成库存日报表文件时出现异常！";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (null != batchRetriever) {
				batchRetriever.closeConnection();
			}
		}
	}

	/**
	 * 根据征收机关性质取得征收机关大类
	 * 
	 * @param String
	 *            taxprop 征收机关性质
	 * @return
	 */
	private String getTaxBigClassByProp(String taxprop) {
		if (MsgConstant.MSG_TAXORG_NATION_PROP.equals(taxprop)) {
			return MsgConstant.MSG_TAXORG_NATION_CLASS;
		}
		if (MsgConstant.MSG_TAXORG_PLACE_PROP.equals(taxprop)) {
			return MsgConstant.MSG_TAXORG_PLACE_CLASS;
		}
		if (MsgConstant.MSG_TAXORG_CUSTOM_PROP.equals(taxprop)) {
			return MsgConstant.MSG_TAXORG_CUSTOM_CLASS;
		}
		if (MsgConstant.MSG_TAXORG_OTHER_PROP.equals(taxprop)) {
			return MsgConstant.MSG_TAXORG_OTHER_CLASS;
		}

		return MsgConstant.MSG_TAXORG_SHARE_CLASS;
	}

	public void sendApplyInfo(String acctdate, String msgid, String finorg,
			String sorgcode) throws ITFEBizException {
		try {
			if (MsgConstant.MSG_NO_5001.equals(msgid)) {
				MuleClient client = new MuleClient();
				Map map = new HashMap();
				MuleMessage message = new DefaultMuleMessage(map);
				message.setProperty(MessagePropertyKeys.MSG_NO_KEY, msgid
						+ "_OUT");
				message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, finorg);
				message.setProperty(MessagePropertyKeys.MSG_DATE, acctdate);
				message.setProperty(MessagePropertyKeys.MSG_ORGCODE, sorgcode);
				message.setPayload(map);
				if(sorgcode!=null&&sorgcode.startsWith("1702"))
					message = client.send("vm://ManagerMsgToPbcCity", message);
				else
					message = client.send("vm://ManagerMsgToPbc", message);
			} else {
				HeadDto headdto = new HeadDto();
				headdto.set_VER(MsgConstant.MSG_HEAD_VER);
				headdto.set_SRC(finorg);
				headdto.set_APP(MsgConstant.MSG_HEAD_APP);
				headdto.set_DES(acctdate);
				headdto.set_msgNo(msgid);
				try {
					String msgid0 = MsgSeqFacade.getMsgSendSeq();
					headdto.set_msgID(msgid0);
					headdto.set_msgRef(msgid0);
				} catch (SequenceException e) {
					log.error("取交易流水号时出现异常！", e);
					throw new ITFEBizException("取交易流水号时出现异常！", e);
				}
				headdto.set_workDate(TimeFacade.getCurrentStringTime());

				MuleClient client = new MuleClient();
				Map map = new HashMap();
				MuleMessage message = new DefaultMuleMessage(map);
				message.setProperty(MessagePropertyKeys.MSG_NO_KEY, msgid
						+ "_OUT");
				message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
				message.setProperty(MessagePropertyKeys.MSG_ORGCODE,
						getLoginInfo().getSorgcode());// 核算主体代码
				message.setProperty(MessagePropertyKeys.MSG_DATE, acctdate);// 入库日期
				message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, finorg);// 发送机关代码

				message.setPayload(map);
				if(sorgcode!=null&&sorgcode.startsWith("1702"))
					message = client.send("vm://ManagerMsgToPbcCity", message);
				else
					message = client.send("vm://ManagerMsgToPbc", message);
			}

		} catch (MuleException e) {
			log.error(e);
			throw new ITFEBizException("发送申请报文失败!", e);
		}

	}

	/**
	 * * 收入报表(正常期和调整期都有)
	 * 
	 * { SQLBatchRetriever batchRetriever = null; try { String selectSQL ="select S_TRECODE,S_BUDGETLEVELCODE,S_TAXORGCODE,S_BUDGETSUBCODE,S_RPTDATE,S_BUDGETTYPE,N_MONEYDAY,N_MONEYMONTH,N_MONEYYEAR"
	 * + " from " + TrIncomedayrptDto.tableName() +" where S_TRECODE = ? and S_RPTDATE = ? and S_BELONGFLAG = ? and S_TRIMFLAG = ? with ur"
	 * ;
	 * 
	 * 
	 * 本级收入(正常期)
	 * 
	 * { boolean isSelfIncomeRecordflag = false; // 第一步 查询符合条件的记录 batchRetriever
	 * = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLBatchRetriever();
	 * batchRetriever.addParam(strecode); // 国库代码
	 * batchRetriever.addParam(rptDate); // 报表日期
	 * batchRetriever.addParam(MsgConstant.RULE_SIGN_SELF); // 辖属标志为本级
	 * batchRetriever.addParam(MsgConstant.TIME_FLAG_NORMAL); // 调整期标志为正常期
	 * batchRetriever.setMaxRows(MAX_NUM); // 设置每次取记录的最大数
	 * 
	 * batchRetriever.runQuery(selectSQL); StringBuffer selfbuf = new
	 * StringBuffer(); // 预算收入报表格式头 selfbuf.append(
	 * "skgkdm,mdgkdm,ssgkdm,ysjc,jgdm,kmdm,zwrq,yszl,rlj,ylj,nlj");
	 * selfbuf.append(StateConstant.SPACE_SPLIT);
	 * 
	 * // 说明： 由于TBS和本级和全辖枚举值相反，因此这里特殊处理 String incomeSelfPath = srcpath + "s" +
	 * strecode + "_1" + MsgConstant.RULE_SIGN_ALL + rptDate + "_" +
	 * MsgConstant.TIME_FLAG_NORMAL + ".txt";
	 * 
	 * // 第二步 删除服务器文件(可能已经生成过)
	 * FileUtil.getInstance().deleteFiles(ITFECommonConstant.FILE_ROOT_PATH +
	 * incomeSelfPath);
	 * 
	 * while (batchRetriever.hasMore()) { SQLResults result =
	 * batchRetriever.RetrieveNextBatch();
	 * 
	 * int count = result.getRowCount(); for (int i = 0; i < count; i++) {
	 * isSelfIncomeRecordflag = true; int j = 0; String trecode =
	 * result.getString(i, j++); selfbuf.append(trecode); // 国库代码
	 * selfbuf.append(StateConstant.INCOME_SPLIT); selfbuf.append(trecode); //
	 * 国库代码 selfbuf.append(StateConstant.INCOME_SPLIT); selfbuf.append(trecode);
	 * // 国库代码 selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // 预算级次
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // 征收机关代码
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // 预算科目代码
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // 报表日期
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // 预算种类
	 * selfbuf.append(StateConstant.INCOME_SPLIT); //
	 * selfbuf.append(result.getString(i, j++)); // 预算科目代码名称 //
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getBigDecimal(i, j++)); // 日累计
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getBigDecimal(i, j++)); // 月累计
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getBigDecimal(i, j++)); // 年累计
	 * selfbuf.append(StateConstant.SPACE_SPLIT); } // 追加方式写文件
	 * FileUtil.getInstance().writeFile(ITFECommonConstant.FILE_ROOT_PATH +
	 * incomeSelfPath, selfbuf.toString(), true);
	 * 
	 * selfbuf = new StringBuffer(); }
	 * 
	 * if(isSelfIncomeRecordflag){ filepathList.add(incomeSelfPath); } }
	 * 
	 * 
	 * 本级收入(调整期)
	 * 
	 * { boolean isSelfIncomeRecordflag = false; // 第一步 查询符合条件的记录 batchRetriever
	 * = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLBatchRetriever();
	 * batchRetriever.addParam(strecode); // 国库代码
	 * batchRetriever.addParam(rptDate); // 报表日期
	 * batchRetriever.addParam(MsgConstant.RULE_SIGN_SELF); // 辖属标志为本级
	 * batchRetriever.addParam(MsgConstant.TIME_FLAG_TRIM); // 调整期标志为调整期
	 * batchRetriever.setMaxRows(MAX_NUM); // 设置每次取记录的最大数
	 * 
	 * batchRetriever.runQuery(selectSQL); StringBuffer selfbuf = new
	 * StringBuffer(); // 预算收入报表格式头 selfbuf.append(
	 * "skgkdm,mdgkdm,ssgkdm,ysjc,jgdm,kmdm,zwrq,yszl,rlj,ylj,nlj");
	 * selfbuf.append(StateConstant.SPACE_SPLIT);
	 * 
	 * // 说明： 由于TBS和本级和全辖枚举值相反，因此这里特殊处理 String incomeSelfPath = srcpath + "s" +
	 * strecode + "_1" + MsgConstant.RULE_SIGN_ALL + rptDate + "_" +
	 * MsgConstant.TIME_FLAG_TRIM + ".txt";
	 * 
	 * // 第二步 删除服务器文件(可能已经生成过)
	 * FileUtil.getInstance().deleteFiles(ITFECommonConstant.FILE_ROOT_PATH +
	 * incomeSelfPath);
	 * 
	 * while (batchRetriever.hasMore()) { SQLResults result =
	 * batchRetriever.RetrieveNextBatch();
	 * 
	 * int count = result.getRowCount(); for (int i = 0; i < count; i++) {
	 * isSelfIncomeRecordflag = true; int j = 0; String trecode =
	 * result.getString(i, j++); selfbuf.append(trecode); // 国库代码
	 * selfbuf.append(StateConstant.INCOME_SPLIT); selfbuf.append(trecode); //
	 * 国库代码 selfbuf.append(StateConstant.INCOME_SPLIT); selfbuf.append(trecode);
	 * // 国库代码 selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // 预算级次
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // 征收机关代码
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // 预算科目代码
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // 报表日期
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getString(i, j++)); // 预算种类
	 * selfbuf.append(StateConstant.INCOME_SPLIT); //
	 * selfbuf.append(result.getString(i, j++)); // 预算科目代码名称 //
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getBigDecimal(i, j++)); // 日累计
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getBigDecimal(i, j++)); // 月累计
	 * selfbuf.append(StateConstant.INCOME_SPLIT);
	 * selfbuf.append(result.getBigDecimal(i, j++)); // 年累计
	 * selfbuf.append(StateConstant.SPACE_SPLIT); } // 追加方式写文件
	 * FileUtil.getInstance().writeFile(ITFECommonConstant.FILE_ROOT_PATH +
	 * incomeSelfPath, selfbuf.toString(), true);
	 * 
	 * selfbuf = new StringBuffer(); }
	 * 
	 * if(isSelfIncomeRecordflag){ filepathList.add(incomeSelfPath); }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * 全辖收入（调整期）
	 * 
	 * { boolean isAllIncomeRecordflag = false; // 第一步 查询符合条件的记录
	 * batchRetriever.clearParams(); batchRetriever.addParam(strecode); // 国库代码
	 * batchRetriever.addParam(rptDate); // 报表日期
	 * batchRetriever.addParam(MsgConstant.RULE_SIGN_ALL); // 辖属标志为全辖
	 * batchRetriever.addParam(MsgConstant.TIME_FLAG_TRIM); // 调整期标志为调整期
	 * batchRetriever.setMaxRows(MAX_NUM); // 设置每次取记录的最大数
	 * 
	 * batchRetriever.runQuery(selectSQL); // 预算收入报表格式头 StringBuffer allbuf =
	 * new StringBuffer(); allbuf.append(
	 * "skgkdm,mdgkdm,ssgkdm,ysjc,jgdm,kmdm,zwrq,yszl,rlj,ylj,nlj");
	 * allbuf.append(StateConstant.SPACE_SPLIT);
	 * 
	 * // 说明： 由于TBS和本级和全辖枚举值相反，因此这里特殊处理 // String incomeAllPath = srcpath +
	 * strecode + "_" + MsgConstant.RULE_SIGN_ALL + "_" +
	 * MsgConstant.REPORT_TYPE_INCOME + ".txt"; String incomeAllPath = srcpath +
	 * "s" + strecode + "_1" + MsgConstant.RULE_SIGN_SELF + rptDate + "_" +
	 * MsgConstant.TIME_FLAG_TRIM + ".txt";
	 * 
	 * // 第二步 删除服务器文件(可能已经生成过)
	 * FileUtil.getInstance().deleteFiles(ITFECommonConstant.FILE_ROOT_PATH +
	 * incomeAllPath);
	 * 
	 * while (batchRetriever.hasMore()) { SQLResults result =
	 * batchRetriever.RetrieveNextBatch();
	 * 
	 * int count = result.getRowCount(); for (int i = 0; i < count; i++) {
	 * isAllIncomeRecordflag = true; int j = 0; String trecode =
	 * result.getString(i, j++); allbuf.append(trecode); // 国库代码
	 * allbuf.append(StateConstant.INCOME_SPLIT); allbuf.append(trecode); //
	 * 国库代码 allbuf.append(StateConstant.INCOME_SPLIT); allbuf.append(trecode);
	 * // 国库代码 allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getString(i, j++)); // 预算级次
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getString(i, j++)); // 征收机关代码
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getString(i, j++)); // 预算科目代码
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getString(i, j++)); // 报表日期
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getString(i, j++)); // 预算种类
	 * allbuf.append(StateConstant.INCOME_SPLIT); //
	 * allbuf.append(result.getString(i, j++)); // 预算科目代码名称 //
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getBigDecimal(i, j++)); // 日累计
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getBigDecimal(i, j++)); // 月累计
	 * allbuf.append(StateConstant.INCOME_SPLIT);
	 * allbuf.append(result.getBigDecimal(i, j++)); // 年累计
	 * allbuf.append(StateConstant.SPACE_SPLIT); } // 追加方式写文件
	 * FileUtil.getInstance().writeFile(ITFECommonConstant.FILE_ROOT_PATH +
	 * incomeAllPath, allbuf.toString(), true);
	 * 
	 * allbuf = new StringBuffer(); }
	 * 
	 * if(isAllIncomeRecordflag){ filepathList.add(incomeAllPath); } }
	 * 
	 * batchRetriever.closeConnection(); } catch (JAFDatabaseException e) {
	 * String error = "查询预算收入报表时出现数据库异常！"; logger.error(error, e); throw new
	 * ITFEBizException(error, e); } catch (FileOperateException e) { String
	 * error = "生成预算收入报表文件时出现异常！"; logger.error(error, e); throw new
	 * ITFEBizException(error, e); } finally { if (null != batchRetriever) {
	 * batchRetriever.closeConnection(); } } }
	 * 
	 * 
	 * 库存日报
	 * 
	 * { SQLBatchRetriever batchRetriever = null; try { boolean
	 * isStockRecordflag = false; String stockSQL ="select S_ACCNO,S_ACCNAME,S_ACCDATE,N_MONEYYESTERDAY,N_MONEYIN,N_MONEYOUT,N_MONEYTODAY "
	 * + " from " + TrStockdayrptDto.tableName() +
	 * " where S_TRECODE = ? and S_RPTDATE = ? order by S_ACCNO with ur";
	 * 
	 * // 第一步 查询符合条件的记录 batchRetriever =
	 * DatabaseFacade.getDb().getSqlExecutorFactory().getSQLBatchRetriever();
	 * batchRetriever.addParam(strecode); // 国库代码
	 * batchRetriever.addParam(rptDate); // 报表日期
	 * batchRetriever.setMaxRows(MAX_NUM); // 设置每次取记录的最大数
	 * 
	 * batchRetriever.runQuery(stockSQL);
	 * 
	 * // 库存报表格式头 StringBuffer sbuf = new StringBuffer();
	 * sbuf.append("kjzh,kjzhm,zwrq,srye,brdfsr,brjfzc,brye");
	 * sbuf.append(StateConstant.SPACE_SPLIT);
	 * 
	 * // String stockSelfPath = srcpath + strecode + "_" +
	 * MsgConstant.RULE_SIGN_SELF + "_" + MsgConstant.REPORT_TYPE_STOCK +
	 * ".txt"; String stockSelfPath = srcpath + "s" + strecode + "_c" +
	 * MsgConstant.RULE_SIGN_ALL + rptDate + ".txt";
	 * 
	 * // 第二步 删除服务器文件(可能已经生成过)
	 * FileUtil.getInstance().deleteFiles(ITFECommonConstant.FILE_ROOT_PATH +
	 * stockSelfPath);
	 * 
	 * while (batchRetriever.hasMore()) { SQLResults result =
	 * batchRetriever.RetrieveNextBatch();
	 * 
	 * int count = result.getRowCount(); for (int i = 0; i < count; i++) {
	 * isStockRecordflag = true; int j = 0; // sbuf.append(result.getString(i,
	 * j++)); // 国库代码 // sbuf.append(StateConstant.INCOME_SPLIT); //
	 * sbuf.append(result.getString(i, j++)); // 报表日期 //
	 * sbuf.append(StateConstant.INCOME_SPLIT); sbuf.append(result.getString(i,
	 * j++)); // 帐户代码 sbuf.append(StateConstant.INCOME_SPLIT);
	 * sbuf.append(result.getString(i, j++)); // 帐户名称
	 * sbuf.append(StateConstant.INCOME_SPLIT); sbuf.append(result.getString(i,
	 * j++)); // 帐户日期 sbuf.append(StateConstant.INCOME_SPLIT);
	 * sbuf.append(result.getBigDecimal(i, j++)); // 上日余额
	 * sbuf.append(StateConstant.INCOME_SPLIT);
	 * sbuf.append(result.getBigDecimal(i, j++)); // 本日收入
	 * sbuf.append(StateConstant.INCOME_SPLIT);
	 * sbuf.append(result.getBigDecimal(i, j++)); // 本日支出
	 * sbuf.append(StateConstant.INCOME_SPLIT);
	 * sbuf.append(result.getBigDecimal(i, j++)); // 本日余额
	 * sbuf.append(StateConstant.SPACE_SPLIT); } // 追加方式写文件
	 * FileUtil.getInstance().writeFile(ITFECommonConstant.FILE_ROOT_PATH +
	 * stockSelfPath, sbuf.toString(), true);
	 * 
	 * sbuf = new StringBuffer(); }
	 * 
	 * if(isStockRecordflag){ filepathList.add(stockSelfPath); }
	 * 
	 * batchRetriever.closeConnection(); } catch (JAFDatabaseException e) {
	 * String error = "查询库存日报表时出现数据库异常！"; logger.error(error, e); throw new
	 * ITFEBizException(error, e); } catch (FileOperateException e) { String
	 * error = "生成库存日报表文件时出现异常！"; logger.error(error, e); throw new
	 * ITFEBizException(error, e); } finally { if (null != batchRetriever) {
	 * batchRetriever.closeConnection(); } }
	 * 
	 * }
	 */
}
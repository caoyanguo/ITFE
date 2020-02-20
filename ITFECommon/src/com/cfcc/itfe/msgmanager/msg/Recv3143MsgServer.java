/**
 * 功能:收到划款申请回执处理
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author zhouchuan
 * 
 */
public class Recv3143MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3143MsgServer.class);

	@SuppressWarnings( { "unchecked" })
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");

		// 报文头信息CFX->HEAD
		String sorgcode = (String) headMap.get("SRC"); // 发起机构代码
		String sdescode = (String) headMap.get("DES");// 接收节点代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgID = (String) headMap.get("MsgID");// 报文标识号
		// String msgRef = (String) headMap.get("MsgRef");// 报文参考号
		// String sdate = (String) headMap.get("WorkDate");// 工作日期

		/**
		 * 取得回执头信息
		 */
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead3143");

		String strecode = (String) batchHeadMap.get("TreCode"); // 国库代码
		String sfinorgcode = (String) batchHeadMap.get("FinOrgCode");// 财政机关代码
		String sagentbnkcode = (String) batchHeadMap.get("AgentBnkCode");// 代理银行行号
		String sentrustDate = (String) batchHeadMap.get("EntrustDate");// 委托日期
		String spackno = (String) batchHeadMap.get("PackNo"); // 包流水号
		String soripackno = (String) batchHeadMap.get("OriPackNo");// 原包流水号
		String sorientrustdate = (String) batchHeadMap.get("OriEntrustDate");// 原委托日期
		// String sallnum = (String) batchHeadMap.get("AllNum");//总笔数
		// String sallamt = (String) batchHeadMap.get("AllAmt");//总金额
		// String spayoutvoutype = (String)
		// batchHeadMap.get("PayoutVouType");//支出凭证类型
		String spaymode = (String) batchHeadMap.get("PayMode");// 支付方式

		int count = 0;
		BigDecimal allamt = new BigDecimal("0.00");
		String sdemo = "原报文编号:" + MsgConstant.APPLYPAY_DAORU + ",原包流水:"
				+ soripackno.trim() + ",财政机构代码" + sfinorgcode.trim()
				+ ",原委托日期:" + sorientrustdate.trim();
		String sendno = null;
		String recvorg = sdescode;

		/**
		 * 取得回执批量信息
		 */
		List<Object> batchReturnList = (List<Object>) msgMap.get("Bill3143");
		if (null == batchReturnList || batchReturnList.size() == 0) {
			return;
		} else {
			count = batchReturnList.size();
			SQLExecutor updateExce = null;
			try {
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				String updateSql;
				if (ITFECommonConstant.ISITFECOMMIT
						.equals(StateConstant.COMMON_YES)) {
					updateSql = "update "
							+ TvPayreckBankDto.tableName()
							+ " set S_RESULT = ? , S_ADDWORD = ? ,D_ACCTDATE= ? ,S_XPAYAMT = ? , S_XCLEARDATE = ? ,S_XPAYSNDBNKNO = ? "
							+ " where S_AGENTBNKCODE = ? and S_VOUNO = ? and F_AMT = ? and S_RESULT = ? and S_PACKNO = ? and S_TRANO = ?";
				} else {
					updateSql = "update "
							+ TvPayreckBankDto.tableName()
							+ " set S_RESULT = ? , S_ADDWORD = ? ,D_ACCTDATE= ? ,S_XPAYAMT = ? , S_XCLEARDATE = ? ,S_XPAYSNDBNKNO = ? "
							+ " where S_AGENTBNKCODE = ? and S_VOUNO = ? and F_AMT = ? and S_RESULT = ? ";
				}

				for (int i = 0; i < count; i++) {
					updateExce.clearParams();
					HashMap tmpmap = (HashMap) batchReturnList.get(i);
					String sorivouno = (String) tmpmap.get("VouNo"); // 凭证编号
					String sorivoudate = (String) tmpmap.get("VouDate"); // 凭证日期
					String soritrano = (String) tmpmap.get("OriTraNo"); // 原交易流水号
					String sPayDictateNo = (String) tmpmap.get("PayDictateNo");// 支付交易序号
					String sPayMsgNo = (String) tmpmap.get("PayMsgNo");// 支付报文编号
					String sPayEntrustDate = (String) tmpmap
							.get("PayEntrustDate");// 支付委托日期
					String sPaySndBnkNo = (String) tmpmap.get("PaySndBnkNo");// 支付发起行行号
					String sPayResult = (String) tmpmap.get("PayResult");// 拨付结果
					String sAddWord = (String) tmpmap.get("AddWord");// 附言
					String sacctdate = (String) tmpmap.get("AcctDate"); // TCBS系统处理该笔业务的账务日期
					String sAmt = (String) tmpmap.get("Amt");// 金额
					allamt = allamt.add(new BigDecimal(sAmt)); // 计算合计金额
					if (DealCodeConstants.DEALCODE_TIPS_SUCCESS
							.equals(sPayResult)
							|| StateConstant.COMMON_NO.equals(sPayResult))
						sAddWord = DealCodeConstants.PROCESS_SUCCESS;

					// 根据处理结果转化交易状态
					String sstatus = PublicSearchFacade
							.getDetailStateByDealCode(sPayResult);

					// 上海无纸化并且是直接支付（支付方式：0-直接 1-授权），更新5201业务主表和凭证索引表状态
					if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
							&& spaymode.equals(MsgConstant.directPay)) {
						VoucherUtil.updateVoucherStatusRecvTCBSDirectMsg(
								strecode, sacctdate, soripackno, sorivoudate,
								sorivouno, allamt, sstatus, sAddWord,
								soritrano, sorientrustdate,
								StateConstant.BIZTYPE_CODE_BATCH);
					}
					updateExce.addParam(sstatus);
					// 上海无纸化并且是授权支付（支付方式：0-直接 1-授权）
					if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
							&& spaymode.equals(MsgConstant.grantPay)) {
						// 回单附言 填写3143中支付交易序号+报文编号+支付委托日期
						updateExce.addParam(sPayDictateNo + sPayMsgNo
								+ sPayEntrustDate);
					} else {
						updateExce.addParam(sAddWord);
					}
					updateExce.addParam(CommonUtil.strToDate(sacctdate));
					updateExce.addParam(BigDecimal
							.valueOf(Double.valueOf(sAmt)));
					updateExce.addParam(CommonUtil.strToDate(sacctdate));
					updateExce.addParam(sPaySndBnkNo);// 支付发起行行号(上海无纸化回单中使用)
					updateExce.addParam(sagentbnkcode);// 代理银行行号
					updateExce.addParam(sorivouno);// 原凭证编号
					updateExce.addParam(sAmt);// 发生额
					updateExce
							.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// 处理中状态
					if (ITFECommonConstant.ISITFECOMMIT
							.equals(StateConstant.COMMON_YES)) {
						updateExce.addParam(soripackno);// 原包流水号
						updateExce.addParam(soritrano);// 原交易流水号
					}
					updateExce.runQuery(updateSql);
					try {
						Voucher voucher = (Voucher) ContextFactory
								.getApplicationContext().getBean(
										MsgConstant.VOUCHER);
						voucher.VoucherReceiveTCBS(strecode,
								MsgConstant.VOUCHER_NO_2301, soripackno,
								sorivoudate, sorivouno, new BigDecimal(sAmt),
								sstatus, sAddWord);
					} catch (Exception e) {
						logger.error(e);
						VoucherException.saveErrInfo(null, e);
					}

				}
				updateExce.closeConnection();
				// 取原发送包
				TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(
						MsgConstant.APPLYPAY_DAORU, sagentbnkcode,
						sorientrustdate, soripackno,
						DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				// 接收日志流水d
				String _srecvno = StampFacade.getStampSendSeq("JS");
				// 更新原包状态
				if (null != senddto) {
					// 更新原发送日志流水号
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno,
							"");
					sendno = senddto.getSsendno();
					recvorg = senddto.getSsendorgcode();
					// 根据条件更新文件与包的对应关系表
					MsgRecvFacade.updateMsgHeadByCon(sfinorgcode, soripackno,
							sorientrustdate,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH");
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);

				// 记接收日志
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg,
						sentrustDate, msgNo, sorgcode, filepath,
						batchReturnList.size(), allamt, spackno, strecode,
						null, sfinorgcode, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo);

			} catch (JAFDatabaseException e) {
				String error = "接收3143报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (SequenceException e) {
				String error = "接收3143报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
			}
		}
		// if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
		// {
		eventContext.getMessage().setCorrelationId(
				"ID:524551000000000000000000000000000000000000000000");
		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
		// }else
		// eventContext.setStopFurtherProcessing(true);
		return;

	}

}

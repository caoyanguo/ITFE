/**
 * 功能:收到划款申请退回回执处理
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
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
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
public class Recv3144MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3144MsgServer.class);

	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		/**
		 * 取得报文头信息
		 */
		// 报文头信息CFX->HEAD
		String desnode = (String) headMap.get("DES");// 接收节点代码
		String srcnode = (String) headMap.get("SRC");// 发起节点代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文标识号

		/**
		 * 取得回执头信息
		 */
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead3144");
		String agentBnkCode = (String) batchHeadMap.get("AgentBnkCode"); // 代理银行行号
		String finorgcode = (String) batchHeadMap.get("FinOrgCode");// 财政机关代码
		String ls_TreCode = (String) batchHeadMap.get("TreCode"); // 国库主体代码
		String entrustdate = (String) batchHeadMap.get("EntrustDate"); // 委托日期
		String packno = (String) batchHeadMap.get("PackNo"); // 包流水号
		String oriEntrustDate = (String) batchHeadMap.get("OriEntrustDate"); // 原委托日期
		String oriPackNo = (String) batchHeadMap.get("OriPackNo"); // 原包流水号
		String PayMode = (String) batchHeadMap.get("PayMode"); // 支付方式
		int count = 0;
		BigDecimal allamt = new BigDecimal("0.00");

		String sdemo = "原报文编号:" + MsgConstant.APPLYPAY_BACK_DAORU + ",原包流水:"
				+ oriPackNo.trim() + ",原发起机构代码" + finorgcode.trim() + ",原委托日期:"
				+ oriEntrustDate.trim();
		String sendno = null;
		String recvorg = desnode;

		/**
		 * 取得回执批量信息
		 */
		List<Object> batchReturnList = (List<Object>) msgMap
				.get("BatchReturn3144");
		String result = null;
		if (null == batchReturnList || batchReturnList.size() == 0) {
			return;
		} else {
			SQLExecutor updateExce = null;
			try {
				/**
				 * 取得主凭证要素作为条件去更新主凭证交易状态
				 */
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				count = batchReturnList.size();
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();
					HashMap tmpmap = (HashMap) batchReturnList.get(i);
					String vouno = (String) tmpmap.get("VouNo"); // 凭证编号
					String voudate = (String) tmpmap.get("VouDate"); // 凭证日期
					// String orivouno = (String) tmpmap.get("OriVouNo"); //
					// 原凭证编号
					String orivoudate = (String) tmpmap.get("OriVouDate"); // 原凭证日期
					String soritrano = (String) tmpmap.get("OriTraNo"); // 原交易流水号
					String acctdate = (String) tmpmap.get("AcctDate");// 账务日期
					String sAmt = (String) tmpmap.get("Amt");// 金额
					BigDecimal Amt = MtoCodeTrans.transformBigDecimal(sAmt); // 金额
					result = (String) tmpmap.get("Result");// 处理结果
					String description = (String) tmpmap.get("Description");// 说明

					String demo;

					// 根据处理结果转化交易状态
					String sstatus = PublicSearchFacade
							.getDetailStateByDealCode(result);
					if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(sstatus)) {
						demo = DealCodeConstants.PROCESS_SUCCESS;
					} else {
						demo = DealCodeConstants.PROCESS_FAIL;
					}

					// 如果是上海无纸化并且是直接支付，更新收款银行退款通知2252业务主表和凭证索引表状态
					if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
							&& PayMode.equals(MsgConstant.directPay))
						VoucherUtil.updateVoucherStatusRecvTCBSBankRefund(
								ls_TreCode, acctdate, oriPackNo, orivoudate,
								vouno, Amt, sstatus, demo, soritrano,
								oriEntrustDate);

					updateExce.addParam(sstatus);
					// 如果是上海无纸化并且是授权支付
					if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
							&& PayMode.equals(MsgConstant.grantPay)) {
						// 回单附言 填写 3144中的原交易流水号
						updateExce.addParam(soritrano);
					} else {
						updateExce.addParam(demo);
					}
					updateExce.addParam(Amt);
					updateExce.addParam(CommonUtil.strToDate(acctdate));
					updateExce.addParam(agentBnkCode);// “XPaySndBnkNo支付发起行行号”
					// 填写3144中填写代理银行行号
					updateExce.addParam(agentBnkCode);// 代理银行代码
					updateExce.addParam(vouno);// 原凭证编号
					updateExce.addParam(Amt);// 发生额
					updateExce
							.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// 处理中状态
					if (ITFECommonConstant.ISITFECOMMIT
							.equals(StateConstant.COMMON_YES)) {
						updateExce.addParam(oriPackNo);// 原包流水号
						updateExce.addParam(soritrano);// 原交易流水号
						updateExce
								.runQuery("update "
										+ TvPayreckBankBackDto.tableName()
										+ " set S_STATUS = ? , S_ADDWORD = ? ,S_XPAYAMT = ? ,S_XCLEARDATE = ? ,S_XPAYSNDBNKNO = ? "
										+ " where S_AGENTBNKCODE = ? and S_VOUNO = ? and F_AMT = ? and S_STATUS = ? and S_PACKNO = ? and S_TRANO = ?");
					} else {
						updateExce
								.runQuery("update "
										+ TvPayreckBankBackDto.tableName()
										+ " set S_STATUS = ? , S_ADDWORD = ? ,S_XPAYAMT = ? ,S_XCLEARDATE = ? ,S_XPAYSNDBNKNO = ? "
										+ " where S_AGENTBNKCODE = ? and S_VOUNO = ? and F_AMT = ? and S_STATUS = ? ");
					}

					try {
						Voucher voucher = (Voucher) ContextFactory
								.getApplicationContext().getBean(
										MsgConstant.VOUCHER);

						voucher.VoucherReceiveTCBS(ls_TreCode,
								MsgConstant.VOUCHER_NO_2302, oriPackNo,
								voudate, vouno, MtoCodeTrans
										.transformBigDecimal("-" + sAmt),
								sstatus, description);
					} catch (Exception e) {
						logger.error(e);
						VoucherException.saveErrInfo(null, e);
					}
				}

				// 取原发送包
				TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(
						MsgConstant.APPLYPAY_BACK_DAORU, agentBnkCode,
						oriEntrustDate, oriPackNo,
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
					MsgRecvFacade.updateMsgHeadByCon(finorgcode, oriPackNo,
							oriEntrustDate,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH");
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// 记接收日志
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg,
						entrustdate, msgNo, srcnode, filepath, batchReturnList
								.size(), allamt, packno, ls_TreCode,
						agentBnkCode, finorgcode, null, msgid,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo);
			} catch (JAFDatabaseException e) {
				String error = "更新划款申请退款业务回执状态时出现数据库异常！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (SequenceException e) {
				String error = "取交易流水号的时候出现数据库异常！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} finally {
				if (updateExce != null)
					updateExce.closeConnection();
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

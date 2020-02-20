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
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 接收、解析、发送人民银行办理支付无（有）纸凭证回执(3135) 主要功能：接受、解析、发送3135报文
 * 
 * @author zhangxh
 * 
 */
public class Recv3135MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3135MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		/*
		 * 第1步 解析报文头信息，记录接收日志
		 */
		// 报文的处理方式 1 手工 2 MQ
		String bankInput = (String) eventContext.getMessage().getProperty(
				"BANK_INPUT");
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		int count = 0;

		// 报文头信息CFX->HEAD
		String sorgcode = (String) headMap.get("SRC"); // 发起机构代码
		String sdescode = (String) headMap.get("DES");// 接收节点代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgID = (String) headMap.get("MsgID");// 报文标识号
		String MsgRef = (String) headMap.get("MsgRef");// 报文参考号
		String WorkDate = (String) headMap.get("WorkDate");// 工作日期

		// 解析实时业务头 CFX->MSG->BatchHead3135
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3135");
		String treCode = (String) batchheadMap.get("TreCode"); // 国库主体代码
		String billOrg = (String) batchheadMap.get("BillOrg"); // 出票单位
		String entrustDate = (String) batchheadMap.get("EntrustDate"); // 委托日期
		String packNo = (String) batchheadMap.get("PackNo"); // 包流水号
		String oriEntrustDate = (String) batchheadMap.get("OriEntrustDate"); // 原委托日期
		String oriPackNo = (String) batchheadMap.get("OriPackNo"); // 原包流水号
		int AllNum = Integer.parseInt(batchheadMap.get("AllNum").toString()
				.trim()); // 总笔数
		BigDecimal AllAmt = MtoCodeTrans.transformBigDecimal(batchheadMap
				.get("AllAmt")); // 总金额
		String payoutVouType = (String) batchheadMap.get("PayoutVouType"); // 支出凭证类型
		String PayMode = (String) batchheadMap.get("PayMode"); // 支付方式
		String sendno = null;
		String recvorg = sdescode;

		/*
		 * 第2步 解析报文体信息，更新数据库信息
		 */
		// 额度信息 CFX->MSG->BatchReturn3135
		SQLExecutor updateExce = null;
		try {
			List BatchReturn3135s = (List) msgMap.get("BatchReturn3135");
			if (null == BatchReturn3135s || BatchReturn3135s.size() == 0) {
				return;
			} else {
				count = BatchReturn3135s.size();

				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();

				String updateSql = "update "
						+ TvPbcpayMainDto.tableName()
						+ " set S_STATUS = ?,S_ADDWORD = ?,D_ACCT =? ,S_DESCRIPTION=?"
						+ " where S_TRECODE = ?  and S_BillORG = ? and S_ENTRUSTDATE = ? and S_PACKNO = ? "
						+ " and S_VOUNO = ?  and S_traNO = ? ";

				for (int i = 0; i < BatchReturn3135s.size(); i++) {
					HashMap BatchReturn3135 = (HashMap) BatchReturn3135s.get(i);
					String VouNo = (String) BatchReturn3135.get("VouNo"); // 凭证编号
					String VouDate = (String) BatchReturn3135.get("VouDate"); // 凭证日期
					String OriTraNo = (String) BatchReturn3135.get("OriTraNo"); // 原交易流水号
					BigDecimal Amt = MtoCodeTrans
							.transformBigDecimal(BatchReturn3135.get("Amt")); // 合计金额
					String AcctDate = (String) BatchReturn3135.get("AcctDate"); // 账务日期
					String Result = (String) BatchReturn3135.get("Result"); // 处理结果
					String Description = (String) BatchReturn3135
							.get("Description"); // 说明

					// 根据处理结果转化交易状态
					String sstatus = PublicSearchFacade
							.getDetailStateByDealCode(Result);
					//上海无纸化，更新5201业务主表和凭证索引表状态
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0)
						VoucherUtil.updateVoucherStatusRecvTCBSDirectMsg(treCode, AcctDate, oriPackNo, VouDate, 
								VouNo, Amt, sstatus, Result, OriTraNo, oriEntrustDate, StateConstant.BIZTYPE_CODE_SINGLE);					
					updateExce.addParam(sstatus);
					updateExce.addParam(Result);
					updateExce.addParam(AcctDate);
					updateExce.addParam(Description);
					updateExce.addParam(treCode);
					updateExce.addParam(billOrg);
					updateExce.addParam(entrustDate);
					updateExce.addParam(oriPackNo);
					updateExce.addParam(VouNo);
					updateExce.addParam(OriTraNo);
					updateExce.runQuery(updateSql);
				}
				updateExce.closeConnection();

				// 取原发送包
				TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(
						MsgConstant.MSG_NO_5104, billOrg, entrustDate, packNo,
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
					MsgRecvFacade.updateMsgHeadByCon(billOrg, packNo,
							entrustDate,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// 记接收日志
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg,
						entrustDate, msgNo, sorgcode, filepath,
						BatchReturn3135s.size(), AllAmt, packNo, treCode, null,
						billOrg, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, null);

			}

		} catch (JAFDatabaseException e) {
			String error = "接收3135报文处理失败！";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} catch (SequenceException e) {
			String error = "接收3135报文处理失败！";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		}finally{
			if(null != updateExce){
				updateExce.closeConnection();
			}
		}
	}
}

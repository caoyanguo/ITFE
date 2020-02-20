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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 
 * 主要功能：6.2.15　TCBS批量处理结果通知(3190)
 * 
 * @author wangyunbin
 * 
 */
public class Recv3190MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3190MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		// 报文头信息CFX->HEAD
		String sorgcode = (String) headMap.get("SRC"); // 发起机构代码
		String sdescode = (String) headMap.get("DES");// 接收节点代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgID = (String) headMap.get("MsgID");// 报文标识号
		String msgRef = (String) headMap.get("MsgRef");// 报文参考号
		String sdate = (String) headMap.get("WorkDate");// 工作日期
		String recvorg = sdescode;

		/**
		 * 解析入库批量头 MSG->BillHead3190
		 */
		HashMap billhead3139 = (HashMap) msgMap.get("BatchHead3190");

		String OriMsgNo = (String) billhead3139.get("OriMsgNo");// 原报文编号
		String OriTaxOrgCode = (String) billhead3139.get("OriTaxOrgCode"); // 原征收机构代码
		String OriEntrustDate = (String) billhead3139.get("OriEntrustDate"); // 原委托日期
		String OriPackNo = (String) billhead3139.get("OriPackNo"); // 原包流水号
		int allNum = Integer.valueOf((String) billhead3139.get("AllNum")); // 总笔数
		BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(billhead3139
				.get("AllAmt"));// 总金额
		String sdemo = "原报文编号:" + OriMsgNo + ",原包流水:" + OriPackNo + ",原发起机构代码"
				+ OriTaxOrgCode + ",原委托日期:" + OriEntrustDate;
		String updateSql = "";
		String sendno = null;
		List returnList = (List) msgMap.get("BatchReturn3190");
		HashMap<String, String> tabMap = ITFECommonConstant.bizMsgNoList;
		String tabname = tabMap.get(OriMsgNo);
		if (null == returnList || returnList.size() == 0) {
			return;
		} else {
			updateSql = "update "+ tabname
			+ " set S_STATUS = ? ";
			//三种业务条件不一样
			if (MsgConstant.MSG_NO_1104.equals(OriMsgNo)) {//退库
				updateSql+=", D_ACCT = ? , XPAYAMT = ? where S_TAXORGCODE = ? and D_ACCEPT  = ?  and S_DEALNO = ?";
			}else if(MsgConstant.MSG_NO_1105.equals(OriMsgNo)){//更正
				updateSql+=" where S_ORITAXORGCODE = ? and D_ACCEPT  = ?  and S_DEALNO = ?";
			}else if(MsgConstant.MSG_NO_1106.equals(OriMsgNo)){//免抵调
				updateSql+=" where S_TAXORGCODE = ? and D_ACCEPTDATE  = ?  and S_TRANO = ?";
			}
			int count = returnList.size();
			for (int i = 0; i < count; i++) {
				HashMap BatchReturn3190Map = (HashMap) returnList.get(i);
				String OriTraNo = (String) BatchReturn3190Map.get("OriTraNo"); // 原交易流水号
				String Vouno = (String) BatchReturn3190Map.get("VouNo"); // 凭证编号
				BigDecimal TraAmt = MtoCodeTrans
						.transformBigDecimal(BatchReturn3190Map.get("TraAmt"));// 交易金额
				String OpStat = (String) BatchReturn3190Map.get("OpStat"); // 处理状态
				String AddWord = (String) BatchReturn3190Map.get("AddWord"); // 附言
				String ChannelCode = (String) BatchReturn3190Map
						.get("ChannelCode"); // 渠道代码
				SQLExecutor updateExce = null;
				try {
					updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					updateExce.clearParams();
					String sstatus = PublicSearchFacade
							.getDetailStateByDealCode(OpStat);
					updateExce.addParam(sstatus);
					if(MsgConstant.MSG_NO_1104.equals(OriMsgNo)){
						updateExce.addParam(CommonUtil.strToDate(sdate));
						updateExce.addParam(TraAmt.toString());
					}
					updateExce.addParam(OriTaxOrgCode);
					updateExce.addParam(CommonUtil.strToDate(OriEntrustDate)
							.toString());
					updateExce.addParam(OriTraNo);
					updateExce.runQuery(updateSql);
					updateExce.closeConnection();
					/**
					 * 退库业务更新凭证索引表（无纸化业务）
					 */
					if(MsgConstant.MSG_NO_1104.equals(OriMsgNo)){
						try{
							Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER);
							voucher.VoucherReceiveTCBS(OriTaxOrgCode, MsgConstant.VOUCHER_NO_5209, OriPackNo,
									OriEntrustDate, Vouno, TraAmt, sstatus, "");
						}catch(Exception e){
							logger.error(e);
							VoucherException.saveErrInfo(null, e);
						}
					}
				} catch (JAFDatabaseException e) {
					String error = "更新接收TCBS批量处理结果通知(3190)回执状态时出现数据库异常！";
					logger.error(error, e);
					throw new ITFEBizException(error, e);
				} finally {
					if (null != updateExce) {
						updateExce.closeConnection();
					}
				}
			}
			// 取原发送包
			TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(OriMsgNo,
					OriTaxOrgCode, OriEntrustDate, OriPackNo,
					DealCodeConstants.DEALCODE_ITFE_RECEIPT);
			// 接收日志流水
			String _srecvno;
			try {
				_srecvno = StampFacade.getStampSendSeq("JS");

				// 更新原包状态
				if (null != senddto) {
					// 更新原发送日志流水号
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno,
							"");
					sendno = senddto.getSsendno();
					recvorg = senddto.getSsendorgcode();
					// 根据条件更新文件与包的对应关系表
					MsgRecvFacade.updateMsgHeadByCon(OriTaxOrgCode, OriPackNo,
							OriEntrustDate,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// 记接收日志
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sdate,
						msgNo, sorgcode, filepath, allNum, allAmt, OriPackNo,
						null, null, OriTaxOrgCode, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo);

			} catch (SequenceException e) {
				String error = "接收(3190)回执状态时出现数据库异常！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			}

		}
		
		eventContext.getMessage().setCorrelationId(
		"ID:524551000000000000000000000000000000000000000000");
		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
		
//		eventContext.setStopFurtherProcessing(true);
		return;
	}
}

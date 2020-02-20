/**
 * 功能:收到授权支付额度回执处理
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * @author zhouchuan
 *
 */
public class RecvGrantMsgServer extends AbstractMsgManagerServer {
	
	private static Log logger = LogFactory.getLog(RecvGrantMsgServer.class);

	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		
		// 报文头信息CFX->HEAD
		String sorgcode = (String) headMap.get("SRC"); // 发起机构代码
		String sdescode = (String) headMap.get("DES");// 接收节点代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgID = (String) headMap.get("MsgID");// 报文标识号
//		String msgRef = (String) headMap.get("MsgRef");// 报文参考号
//		String sdate = (String) headMap.get("WorkDate");// 工作日期

		/**
		 * 取得回执头信息
		 */
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead3134");
		String strecode = (String) batchHeadMap.get("TreCode"); // 国库代码
		String sentrustDate = (String) batchHeadMap.get("EntrustDate");//委托日期
		String spackno = (String) batchHeadMap.get("PackNo"); // 包流水号
		String sbillorg = (String) batchHeadMap.get("BillOrg"); // 出票单位
		String sorientrustDate = (String) batchHeadMap.get("OriEntrustDate"); // 原委托日期
		String soripackno = (String) batchHeadMap.get("OriPackNo"); // 原包流水号
//		String spayoutvoutype = (String) batchHeadMap.get("PayoutVouType");//支出凭证类型
		int count = 0 ;
		BigDecimal allamt = new BigDecimal("0.00");
		String sdemo ="原报文编号:"+MsgConstant.MSG_NO_5103+",原包流水:"+soripackno.trim()+",原发起机构代码"+sbillorg.trim()+",原委托日期:"+sorientrustDate.trim();
		String sendno = null;
		String recvorg = sdescode;

		/**
		 * 取得回执批量信息
		 */
		List<Object> batchReturnList = (List<Object>) msgMap.get("BatchReturn3134");
		if (null == batchReturnList || batchReturnList.size() == 0) {
			return;
		} else {
			count = batchReturnList.size();
			SQLExecutor updateExce = null;
			try {
				//取原发送包
				TvSendlogDto  senddto = MsgRecvFacade.findSendLogByMsgId(MsgConstant.MSG_NO_5103, sbillorg, sorientrustDate, soripackno, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				if (null!=senddto) {
					String ifsendfinc = senddto.getSifsend();
					//接收日志流水d
					String _srecvno = StampFacade.getStampSendSeq("JS");
					if (null!=ifsendfinc && ifsendfinc.equals(StateConstant.MSG_SENDER_FLAG_2)) {//如果是财政发起，直接转发给财政
						// 获取原报文直接发送
						eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
						Object msg = eventContext.getMessage().getProperty("MSG_INFO");
						eventContext.getMessage().setPayload(msg);
						// 写发送日志
						MsgLogFacade.writeSendLog(_srecvno, senddto.getSrecvno(), senddto.getSsendorgcode(), (String) headMap.get("DES"), sentrustDate,
								(String) headMap.get("MsgNo"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), 0, new BigDecimal("0.0"), spackno, null,
								null, sbillorg, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
								(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND);
						return;
					}
				}
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				//更新业务表状态
				String updateSql = "update " + TvGrantpaymsgmainDto.tableName() + " set S_STATUS = ? , S_DEMO = ? ,S_ACCDATE = ? ,N_XALLAMT = ? "
				+ " where S_TRECODE = ? and S_PAYUNIT = ? and S_COMMITDATE = ? and S_PACKAGENO = ? "
				+ " and S_PACKAGETICKETNO = ? and S_GENTICKETDATE = ? and S_DEALNO = ? and (S_STATUS NOT IN (? , ? )) ";
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();
					HashMap tmpmap = (HashMap) batchReturnList.get(i);
					String sorivouno = (String) tmpmap.get("VouNo"); // 原凭证编号
					String sorivoudate = (String) tmpmap.get("VouDate"); // 原凭证日期
					String soritrano = (String) tmpmap.get("OriTraNo"); // 原交易流水号
					String dsumamt = (String) tmpmap.get("SumAmt"); // 合计金额
					allamt = allamt.add(new BigDecimal(dsumamt)); // 计算合计金额
					String sacctdate = (String) tmpmap.get("AcctDate"); // TCBS系统处理该笔业务的账务日期
					String sresult = (String) tmpmap.get("Result"); // 处理结果
//					String ls_Description = (String) tmpmap.get("Description"); // 说明
					String demo;
					
					//根据处理结果转化交易状态   
					String sstatus = PublicSearchFacade.getDetailStateByDealCode(sresult);
					if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(sstatus)) {
						demo = DealCodeConstants.PROCESS_SUCCESS;
					} else {
						demo = DealCodeConstants.PROCESS_FAIL;
					}
					//接收TCBS回执更新拆分凭证索引表
					if(VoucherUtil.updateVoucherSplitReceiveTCBS(soritrano, sstatus, new BigDecimal(dsumamt), sacctdate, sorientrustDate, demo))
						continue;
					
					updateExce.addParam(sstatus);
					updateExce.addParam(demo);
					updateExce.addParam(sacctdate);
					updateExce.addParam(new BigDecimal(dsumamt));
					updateExce.addParam(strecode);//国库代码
					updateExce.addParam(sbillorg);//出票单位
					updateExce.addParam(sorientrustDate);//原委托日期
					updateExce.addParam(soripackno);//原包号
					updateExce.addParam(sorivouno);//原凭证编号
					updateExce.addParam(sorivoudate);//原凭证日期
					updateExce.addParam(soritrano);//原交易流水
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);//不更新状态 [80000] 处理成功  的凭证
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);//不更新状态  [80001] 处理失败 的凭证
					updateExce.runQuery(updateSql);
				}
				
				//更新凭证索引表状态：当所有拆分的凭证全部接收成功
				VoucherUtil.updateVoucherStatusRecvTCBSGrantMsg(soripackno, sorientrustDate);
				
				//接收日志流水d
				String _srecvno = StampFacade.getStampSendSeq("JS");
				//更新原包状态
				if (null!=senddto) {
					//更新原发送日志流水号
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto, DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno, "");
					sendno = senddto.getSsendno();
					recvorg =senddto.getSsendorgcode(); 
					// 根据条件更新文件与包的对应关系表
					MsgRecvFacade.updateMsgHeadByCon( sbillorg, soripackno, sorientrustDate, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage().getProperty(
						"XML_MSG_FILE_PATH");
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// 记接收日志
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sentrustDate, msgNo,
						sorgcode, filepath, batchReturnList.size(), allamt, spackno, strecode, null,
						sbillorg, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo );


			} catch (JAFDatabaseException e) {
				String error = "接收3134报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (SequenceException e) {
				String error = "接收3134报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			}  finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
			}
		}
		if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
		{
			eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
			// 获得原报文，重新发送
			Object msg = eventContext.getMessage().getProperty("MSG_INFO");
			eventContext.getMessage().setPayload(msg);
		}else
			eventContext.setStopFurtherProcessing(true);
		return;
	}
}

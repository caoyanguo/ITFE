package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.deptone.common.core.exception.MessageException;
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
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFreeDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.JmsSendUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class Recv9110MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9110MsgServer.class);

	/**
	 * 报文信息处理
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		String msgRef = (String) headMap.get("MsgRef");
		String msgid = (String) headMap.get("MsgID");
		String recvorg = (String) headMap.get("DES");
		String sdate = (String) headMap.get("WorkDate");
		/**
		 * 解析信息头
		 */
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead9110");
		String orimsgno = (String) batchheadMap.get("OriMsgNo"); // 原报文编号
		String oriorgcode = (String) batchheadMap.get("OriSendOrgCode"); // 原发起机构代码[发起原交易的机构的代]
		String orientrustdate = (String) batchheadMap.get("OriEntrustDate");// 原委托日期[扣款请求发起日期]
		String oripackno = (String) batchheadMap.get("OriPackNo");// 原包流水号
//		int allnum = Integer.valueOf((String) (batchheadMap.get("AllNum")));// 总笔数[包中包含的交易总数，要求总笔数不大于1000]
//		BigDecimal allamt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("AllAmt"));// 总金额[包中所有交易的总金额]
		String result = (String) batchheadMap.get("Result");// 处理结果
		String addword = (String) batchheadMap.get("AddWord"); // 附言
		String sendno = null ;//发送流水号
		String sbillorg = null;//出票单位
		String packno = null ;//原包号
		String strecode = null ;//国库代码
		String sstatus = "";//处理状态
		
		// 接收日志流水
		String _srecvno = null;
		try {
			_srecvno = StampFacade.getStampSendSeq("JS");
		} catch (SequenceException e1) {
			logger.error("取接收流水号出错!", e1);
			throw new ITFEBizException("取接收流水号出错!", e1);
		}
		// 查找原发送日志
		TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,
				orimsgno);
		if (null != senddto) {
			// 更新原发送日志流水号
			//包处理吗
			String pkgstate = PublicSearchFacade.getPackageStateByDealCode(result);
			MsgRecvFacade.updateMsgSendLogByMsgId(senddto, pkgstate, _srecvno,
					addword);
			//更新包对应关系表
			MsgRecvFacade.updateMsgHeadByMsgId(msgRef,pkgstate,addword);
			recvorg =senddto.getSsendorgcode();
			sendno = senddto.getSsendno();
			sbillorg = senddto.getSbillorg();
		    packno = senddto.getSpackno();
			strecode = senddto.getStrecode();
			if (StateConstant.MSG_SENDER_FLAG_2.equals(senddto.getSifsend())&&("1001".equals(orimsgno)||"1102".equals(orimsgno))) {
				// 写发送日志
				MsgLogFacade.writeSendLog(_srecvno, sendno,recvorg, oriorgcode, orientrustdate,
						(String) headMap.get("MsgNo"), (String) eventContext
								.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
						new BigDecimal(0), null, null, null, oriorgcode, null,
						(String) headMap.get("MsgID"),
						DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND);
				Object msg = eventContext.getMessage().getProperty("MSG_INFO");// 获得原报文，重新发送
				if(MsgConstant.TIPSNODE_GUANGXI.equals(ITFECommonConstant.SRC_NODE))
				{
					//取得原发起报文的MQMSGID
					TvMqmessageDto dto = MsgLogFacade.queryMQMSGID(orimsgno, msgRef);
					String correlationId = "ID:524551000000000000000000000000000000000000000000";
					if(dto==null){
						eventContext.getMessage().setCorrelationId(correlationId);
					}else{
						correlationId = dto.getSmqmsgid();
						eventContext.getMessage().setCorrelationId(correlationId);
					}
					try {
						if(!"1102".equals(orimsgno)&&!"1104".equals(orimsgno)&&!"1105".equals(orimsgno))
						{	
							if(recvorg!=null&&recvorg.contains("000002700009"))
								JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, correlationId, false,recvorg);
							else
								JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, correlationId, false,recvorg);
							eventContext.setStopFurtherProcessing(true);// 不做进一步处理
						}
						else
						{
							eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");//批量队列消息头写死的id
							eventContext.getMessage().setPayload(msg);
						}
					} catch (MessageException e) {
						logger.error(e);
						throw new ITFEBizException("不用yak发送，自发送报文设置时效性失败：",e);
					}
				}else
					eventContext.getMessage().setPayload(msg);
			}else
				eventContext.setStopFurtherProcessing(true);
		}
		String sendorg = (String) headMap.get("SRC");
		String biztype = (String) headMap.get("MsgNo");
		String filepath = (String) eventContext.getMessage().getProperty(
		"XML_MSG_FILE_PATH");
//        String stamp = TimeFacade.getCurrentStringTime();
        String ifsend = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER);
       // 记接收日志
       MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sdate, biztype,
		sendorg, filepath, 0, null, packno, strecode, null,
		sbillorg, null, msgid,
		DealCodeConstants.DEALCODE_ITFE_RECEIVER, addword, null,
		ifsend, "0", addword + "原包流水号:" + packno);

		List returnList = (List) msgMap.get("BatchReturn9110");
		if (null == returnList || returnList.size() == 0) {
			return;
		}
		String updateSqlCommon = "";
		/*
		 * 退库请求
		 */
		if(MsgConstant.MSG_NO_1104.equals(orimsgno)){
			updateSqlCommon = "update " + TvDwbkDto.tableName() + " set S_STATUS = ?,S_DEMO=? "
			+ " where S_TAXORGCODE = ? and D_ACCEPT = ? and S_DEALNO = ? and S_PACKAGENO = ? and F_AMT = ? "
			+ " and (S_DWBKVOUCODE = ? or S_ELECVOUNO = ? )";
		}else if(MsgConstant.MSG_NO_1105.equals(orimsgno)){
			//更正请求
			updateSqlCommon = "update " + TvInCorrhandbookDto.tableName() + " set S_STATUS = ?,S_DEMO=? "
			+ " where S_CURTAXORGCODE = ? and D_ACCEPT = ? and S_DEALNO = ? and S_PACKAGENO = ? and F_CURCORRAMT = ? "
			+ " and (S_CORRVOUNO = ? or S_ELECVOUNO = ? )";
		}else if(MsgConstant.MSG_NO_1106.equals(orimsgno)) {
			//免抵调
			updateSqlCommon = "update " + TvFreeDto.tableName() + " set S_STATUS = ?,S_ADDWORD=? "
			+ " where S_TAXORGCODE = ? and D_ACCEPTDATE = ? and S_TRANO = ? and S_PACKNO = ? and F_FREEPLUAMT = ? "
			+ " and (S_FREEVOUNO = ? or S_ELECTROTAXVOUNO = ? )";
		}
		
		if(!updateSqlCommon.equals("")){
			SQLExecutor updateExce = null;
			try {
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				int count = returnList.size();
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();

					HashMap returnmap = (HashMap) returnList.get(i);
					String oritrano = (String) returnmap.get("OriTraNo"); // 原交易流水号[商业银行填写，带回原批量扣税（费）请求明细中的税（费）票交易流水号]
					BigDecimal traamt = MtoCodeTrans.transformBigDecimal(returnmap.get("TraAmt"));// 交易金额[发生的金额]
					String papertaxvouno = (String) returnmap.get("PaperTaxVouNo");// 印刷凭证号码[费票号码]
					String electortaxvouno = (String) returnmap.get("ElectroTaxVouNo"); // 电子凭证号码
					String returnresult = (String) returnmap.get("Result");// 处理结果
					String addWord = MtoCodeTrans.transformString(returnmap.get("AddWord"));// 附言
					
					sstatus = PublicSearchFacade.getDetailStateByDealCode(returnresult);

					updateExce.addParam(sstatus);
					updateExce.addParam(addWord);
					updateExce.addParam(oriorgcode);
					updateExce.addParam(CommonUtil.strToDate(orientrustdate));
					updateExce.addParam(oritrano);
					updateExce.addParam(oripackno);
					updateExce.addParam(traamt);
					updateExce.addParam(papertaxvouno);
					updateExce.addParam(electortaxvouno);

					updateExce.runQuery(updateSqlCommon);
				}

				updateExce.closeConnection();
				
			} catch (JAFDatabaseException e) {
				String error = "更新退库请求或更正请求回执状态时出现数据库异常！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
			}
		}
		
		//更新凭证索引表状态
		try{
			Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
					MsgConstant.VOUCHER);
			voucher.VoucherReceiveTIPSResult(oripackno, sstatus ,addword, MsgConstant.MSG_NO_9110);
		} catch(Exception e){
			logger.error(e);
			VoucherException.saveErrInfo(null, e);
		}

		/**
		 * 现在该报文只是针对自缴核销定时组包出错后的处理
		 */
		if (MsgConstant.MSG_NO_1103.equals(orimsgno)) {
			SQLExecutor updateExce = null;
			try {
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				String updateSql = "update " + TvInfileDto.tableName() + " set S_STATUS = ? , S_DEMO = ? "
						+ " where S_TAXORGCODE = ? and S_COMMITDATE = ? and S_DEALNO = ? and S_PACKAGENO = ? and N_MONEY = ? "
						+ " and (S_TAXTICKETNO = ? or S_TAXTICKETNO = ? )";

				int count = returnList.size();
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();

					HashMap returnmap = (HashMap) returnList.get(i);
					String oritrano = (String) returnmap.get("OriTraNo"); // 原交易流水号[商业银行填写，带回原批量扣税（费）请求明细中的税（费）票交易流水号]
					BigDecimal traamt = MtoCodeTrans.transformBigDecimal(returnmap.get("TraAmt"));// 交易金额[发生的金额]
					String papertaxvouno = (String) returnmap.get("PaperTaxVouNo");// 印刷凭证号码[费票号码]
					String electortaxvouno = (String) returnmap.get("ElectroTaxVouNo"); // 电子凭证号码
					String returnresult = (String) returnmap.get("Result");// 处理结果

					sstatus = PublicSearchFacade.getDetailStateByDealCode(returnresult);

					updateExce.addParam(sstatus);
					updateExce.addParam(returnresult);
					updateExce.addParam(oriorgcode);
					updateExce.addParam(orientrustdate);
					updateExce.addParam(oritrano);
					updateExce.addParam(oripackno);
					updateExce.addParam(traamt);
					updateExce.addParam(papertaxvouno);
					updateExce.addParam(electortaxvouno);

					updateExce.runQuery(updateSql);
				}

				updateExce.closeConnection();
				
			} catch (JAFDatabaseException e) {
				String error = "更新税票收入回执状态时出现数据库异常！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
			}
		}
		eventContext.setStopFurtherProcessing(true);
		return;
	}

}

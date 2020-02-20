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

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author wangwenbo
 * 
 */
public class Recv2000_3143MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv2000_3143MsgServer.class);

	@SuppressWarnings({ "unchecked" })
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

		/**
		 * 取得回执头信息
		 */
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead2000");
		
		String strecode = (String) batchHeadMap.get("TreCode"); // 国库代码
		String sentrustDate = (String) batchHeadMap.get("EntrustDate");// 委托日期
		String spackno = (String) batchHeadMap.get("PackNo"); // 包流水号
		String sbillorg = (String) batchHeadMap.get("BillOrg"); // 出票单位
		String sorientrustDate = (String) batchHeadMap.get("OriEntrustDate"); // 原委托日期
		String soripackno = (String) batchHeadMap.get("OriPackNo"); // 原包流水号
		int sumnum = Integer.parseInt((String) batchHeadMap.get("AllNum")); // 总笔数
		BigDecimal sumamt = new BigDecimal((String) batchHeadMap.get("AllAmt")); // 总金额
		
		int count = 0 ;
		String sdemo ="原报文编号:"+MsgConstant.MSG_NO_1000+MsgConstant.APPLYPAY_DAORU+",原包流水:"+soripackno.trim()+",原发起机构代码"+sbillorg.trim()+",原委托日期:"+sorientrustDate.trim();
		String sendno = null;
		String recvorg = sdescode;
		
		/**
		 * 取得回执批量信息
		 */
		List<Object> billReturnList = (List<Object>) msgMap.get("BillReturn2000");
		if (null == billReturnList || billReturnList.size() == 0) {
			return;
		} else {
			count = billReturnList.size();
			SQLExecutor updateExce = null;
			try {
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				String updateSql = "update " + TvPayreckBankDto.tableName() + " set S_RESULT = ? , S_ADDWORD = ? ,D_ACCTDATE= ? ,S_XPAYAMT = ? , S_XCLEARDATE = ?  "
				+ " where  S_VOUNO = ? and F_AMT = ? and S_RESULT = ? and S_PACKNO = ? and S_TRANO = ?";
				TvVoucherinfoDto tmpInfoDto = null;
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();
					HashMap tmpmap = (HashMap) billReturnList.get(i);
					String sorivouno = (String) tmpmap.get("VouNo"); // 原凭证编号
					String sorivoudate = (String) tmpmap.get("VouDate"); // 原凭证日期
					String soritrano = (String) tmpmap.get("OriTraNo"); // 原交易流水号
					String sAmt = (String) tmpmap.get("Amt"); // 金额
					String sacctdate = (String) tmpmap.get("AcctDate"); // 该笔业务的清算日期
					String sresult = (String) tmpmap.get("Result"); // 处理结果
					String sAddWord = (String) tmpmap.get("Description"); // 处理结果的说明
					
					if (DealCodeConstants.DEALCODE_TIPS_SUCCESS.equals(sresult) || StateConstant.COMMON_NO.equals(sresult)) 
						sAddWord = DealCodeConstants.PROCESS_SUCCESS;
					 
					//根据处理结果转化交易状态   
					String sstatus = PublicSearchFacade.getDetailStateByDealCode(sresult);
					updateExce.addParam(sstatus);
					updateExce.addParam(sAddWord);
					updateExce.addParam(CommonUtil.strToDate(sacctdate));
					updateExce.addParam(BigDecimal.valueOf(Double.valueOf(sAmt)));
					updateExce.addParam(CommonUtil.strToDate(sacctdate));
					updateExce.addParam(sorivouno);//原凭证编号
					updateExce.addParam(sAmt);//发生额
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);//处理中状态
					updateExce.addParam(soripackno);//原包流水号
					updateExce.addParam(soritrano);//原交易流水号
					updateExce.runQuery(updateSql);
					
					
					tmpInfoDto = new TvVoucherinfoDto();
					tmpInfoDto.setStrecode(strecode);
					tmpInfoDto.setSvoucherno(sorivouno);
					tmpInfoDto.setScreatdate(sorivoudate);
					tmpInfoDto.setSext1("40");
					tmpInfoDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(tmpInfoDto).get(0);
					if(null == tmpInfoDto ){
						logger.error("接收资金回执报文2000，未找到原凭证！");
						throw new ITFEBizException("接收资金回执报文2000，未找到原凭证！");
					}
					if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(sstatus)){
						tmpInfoDto.setSext1("41");
						tmpInfoDto.setSdemo("清算成功");
					}else{
						tmpInfoDto.setSext1("42");
						tmpInfoDto.setSdemo("清算失败");
					}
					DatabaseFacade.getODB().update(tmpInfoDto);
//					try{
//						Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
//								MsgConstant.VOUCHER);
//						voucher.VoucherReceiveTCBS(strecode, MsgConstant.VOUCHER_NO_2301, soripackno,
//								sorivoudate, sorivouno,new BigDecimal(sAmt), sstatus, sAddWord);
//					}catch(Exception e){
//						logger.error(e);
//						VoucherException.saveErrInfo(null, e);
//					}
				
				}
				updateExce.closeConnection();
				//取原发送包
				//TvSendlogDto  senddto = MsgRecvFacade.findSendLogByMsgId(MsgConstant.APPLYPAY_DAORU, sagentbnkcode, sentrustDate, soripackno, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				TvSendlogDto  senddto = MsgRecvFacade.findSendLogByMsgId(msgRef, msgNo);
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
						sorgcode, filepath, billReturnList.size(), sumamt, spackno, strecode, null,
						sbillorg, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo );


			} catch (JAFDatabaseException e) {
				String error = "接收2000-3143报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (SequenceException e) {
				String error = "接收2000-3143报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (ValidateException e) {
				String error = "接收2000-3143报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			}   finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
			}
		}
		eventContext.setStopFurtherProcessing(true);
		return;
		
	}

}

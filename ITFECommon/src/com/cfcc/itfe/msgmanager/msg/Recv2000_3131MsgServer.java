/**
 * 功能:收到实拨资金额度回执处理
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
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author wangyunbin
 * 
 */
public class Recv2000_3131MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(RecvPayOutMsgServer.class);

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
		//BigDecimal allamt = new BigDecimal("0.00");
		String sendno = null;
		String recvorg = sdescode;
		String sdemo ="原报文编号:"+MsgConstant.MSG_NO_1000+"-"+MsgConstant.MSG_NO_5101+",原包流水:"+soripackno.trim()+",原发起机构代码"+sbillorg.trim()+",原委托日期:"+sorientrustDate.trim();
		/**
		 * 取得回执批量信息
		 */
		List<Object> batchReturnList = (List<Object>) msgMap.get("BillReturn2000");
		if (null == batchReturnList || batchReturnList.size() == 0) {
			return;
		} else {
			count = batchReturnList.size();
			SQLExecutor updateExce = null;
			
			try {
				//取原发送包
				TvSendlogDto  senddto = MsgRecvFacade.findSendLogByMsgId(MsgConstant.MSG_NO_1000, sbillorg, sorientrustDate, soripackno, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				//接收日志流水
				String _srecvno = StampFacade.getStampSendSeq("JS");
				if (null!=senddto) {
					String ifsendfinc = senddto.getSifsend();
					if (null!=ifsendfinc && ifsendfinc.equals(StateConstant.MSG_SENDER_FLAG_2)) {//如果是财政发起，直接转发给财政
						// 获取原报文直接发送
						Object msg = eventContext.getMessage().getProperty("MSG_INFO");
						eventContext.getMessage().setPayload(msg);
						// 写发送日志
						MsgLogFacade.writeSendLog(_srecvno, senddto.getSrecvno(), senddto.getSsendorgcode(), (String) headMap.get("DES"), sentrustDate,
								(String) headMap.get("MsgNo"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), sumnum, sumamt, spackno, null,
								null, sbillorg, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
								(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND);
						return;
					}
				}
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				
				String updateSql = "update " + TvPayoutmsgmainDto.tableName() + " set S_STATUS = ? , S_DEMO = ? , S_XPAYAMT = ? , S_XPAYDATE = ? , S_XAGENTBUSINESSNO = ?"
						+ " where S_TRECODE = ?  and S_PAYUNIT = ? and S_COMMITDATE = ? and S_PACKAGENO = ? "
						+ " and S_TAXTICKETNO = ? and S_GENTICKETDATE = ? and S_DEALNO = ? and S_STATUS NOT IN (? , ? ) ";
				
				
				TvVoucherinfoDto tmpInfoDto = null;
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();
					HashMap tmpmap = (HashMap) batchReturnList.get(i);
					String sorivouno = (String) tmpmap.get("VouNo"); // 原凭证编号
					String sorivoudate = (String) tmpmap.get("VouDate"); // 原凭证日期
					String soritrano = (String) tmpmap.get("OriTraNo"); // 原交易流水号
					String sAmt = (String) tmpmap.get("Amt"); // 金额
					//allamt = allamt.add(new BigDecimal(dsumamt)); // 计算合计金额
					String sacctdate = (String) tmpmap.get("AcctDate"); // 该笔业务的清算日期
					String sresult = (String) tmpmap.get("Result"); // 处理结果
					String ls_Description = (String) tmpmap.get("Description"); // 处理结果的说明
					
					//根据处理结果转化交易状态   
					String sstatus = PublicSearchFacade.getDetailStateByDealCode(sresult);
					updateExce.addParam(sstatus);//状态
					updateExce.addParam(sresult);
					updateExce.addParam(sAmt);//清算金额
					updateExce.addParam(sacctdate);//清算日期
					updateExce.addParam(soritrano);//原交易流水号
					updateExce.addParam(strecode);
					updateExce.addParam(sbillorg);
					updateExce.addParam(sorientrustDate);
					updateExce.addParam(soripackno);
					
					updateExce.addParam(sorivouno);
					updateExce.addParam(sorivoudate);
					updateExce.addParam(soritrano);
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);//不更新状态 [80000] 处理成功  的凭证
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);//不更新状态  [80001] 处理失败 的凭证
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
					
					
					
//					/**
//					 * 根据条件 更新凭证状态
//					 */
//					try{
//						Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
//								MsgConstant.VOUCHER);
//						//更新实拨资金5207索引表状态
//						voucher.VoucherReceiveTCBS(strecode, MsgConstant.VOUCHER_NO_5207, soripackno,
//								sorivoudate, sorivouno,new BigDecimal(sAmt), sstatus,ls_Description);
//					}catch(Exception e){
//						logger.error(e);
//						VoucherException.saveErrInfo(null, e);
//					}
				}
				
				//更新原包状态
				if (null!=senddto) {
					//更新原发送日志流水号
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto, DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno, "");
					sendno = senddto.getSsendno();
				    recvorg = senddto.getSsendorgcode();
					// 根据条件更新文件与包的对应关系表
					MsgRecvFacade.updateMsgHeadByCon( sbillorg, soripackno, sorientrustDate, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage().getProperty(
						"XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// 记接收日志
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sentrustDate, msgNo,
						sorgcode, filepath, batchReturnList.size(), sumamt, spackno, strecode, null,
						sbillorg, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo );
				
			} catch (JAFDatabaseException e) {
				String error = "接收2000-3131报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (SequenceException e) {
				String error = "接收2000-3131报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (ValidateException e) {
				String error = "接收2000-3131报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			}  finally {
				if (null != updateExce) {
					updateExce.closeConnection();
				}
			}
		}
		eventContext.setStopFurtherProcessing(true);
		return;
	}

}


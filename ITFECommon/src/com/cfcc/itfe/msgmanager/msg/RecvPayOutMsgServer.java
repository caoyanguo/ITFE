/**
 * 功能:收到实拨资金额度回执处理
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author zhouchuan
 * 
 */
public class RecvPayOutMsgServer extends AbstractMsgManagerServer {

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
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead3131");
		String strecode = (String) batchHeadMap.get("TreCode"); // 国库代码
		String spayeebankno = (String) batchHeadMap.get("PayeeBankNo");// 收款行行号
		String sentrustDate = (String) batchHeadMap.get("EntrustDate");// 委托日期
		String spackno = (String) batchHeadMap.get("PackNo"); // 包流水号
		String sbillorg = (String) batchHeadMap.get("BillOrg"); // 出票单位
		String sorientrustDate = (String) batchHeadMap.get("OriEntrustDate"); // 原委托日期
		String soripackno = (String) batchHeadMap.get("OriPackNo"); // 原包流水号
		String spayoutvoutype = (String) batchHeadMap.get("PayoutVouType");// 支出凭证类型
		int sumnum = Integer.parseInt((String) batchHeadMap.get("AllNum")); // 总笔数
		BigDecimal sumamt = new BigDecimal((String) batchHeadMap.get("AllAmt")); // 总金额
		int count = 0;
		BigDecimal allamt = new BigDecimal("0.00");
		String sendno = null;
		String recvorg = sdescode;
		String sdemo = "原报文编号:" + MsgConstant.MSG_NO_5101 + ",原包流水:"
				+ soripackno.trim() + ",原发起机构代码" + sbillorg.trim() + ",原委托日期:"
				+ sorientrustDate.trim();
		/**
		 * 取得回执批量信息
		 */
		List<Object> batchReturnList = (List<Object>) msgMap
				.get("BatchReturn3131");
		if (null == batchReturnList || batchReturnList.size() == 0) {
			return;
		} else {
			count = batchReturnList.size();
			SQLExecutor updateExce = null;

			try {
				// 取原发送包
				TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(
						MsgConstant.MSG_NO_5101, sbillorg, sorientrustDate,
						soripackno, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				// 接收日志流水
				String _srecvno = StampFacade.getStampSendSeq("JS");
				if (null != senddto) {
					String ifsendfinc = senddto.getSifsend();
					if (null != ifsendfinc
							&& ifsendfinc
									.equals(StateConstant.MSG_SENDER_FLAG_2)) {// 如果是财政发起，直接转发给财政
						// 获取原报文直接发送
						Object msg = eventContext.getMessage().getProperty(
								"MSG_INFO");
						eventContext.getMessage().setPayload(msg);
						// 写发送日志
						MsgLogFacade.writeSendLog(_srecvno, senddto
								.getSrecvno(), senddto.getSsendorgcode(),
								(String) headMap.get("DES"), sentrustDate,
								(String) headMap.get("MsgNo"),
								(String) eventContext.getMessage().getProperty(
										"XML_MSG_FILE_PATH"), sumnum, sumamt,
								spackno, null, null, sbillorg, null,
								(String) headMap.get("MsgID"),
								DealCodeConstants.DEALCODE_ITFE_SEND, null,
								null,
								(String) eventContext.getMessage().getProperty(
										MessagePropertyKeys.MSG_SENDER), null,
								MsgConstant.LOG_ADDWORD_SEND);
						return;
					}
				}
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				
				TsTreasuryDto tredto = SrvCacheFacade.cacheTreasuryInfo(null).get(strecode);
				if (StateConstant.COMMON_YES.equals(String.valueOf(tredto==null?"":tredto.getSpayunitname()))) {
					updatePayoutDetailState(updateExce, batchReturnList, soripackno);
				}else{
					updatePayoutMainStatus(updateExce, batchReturnList, soripackno, strecode, spayeebankno, sbillorg, sorientrustDate);
				}

				
				// 更新原包状态
				if (null != senddto) {
					// 更新原发送日志流水号
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno,
							"");
					sendno = senddto.getSsendno();
					recvorg = senddto.getSsendorgcode();
					// 根据条件更新文件与包的对应关系表
					MsgRecvFacade.updateMsgHeadByCon(sbillorg, soripackno,
							sorientrustDate,
							DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// 记接收日志
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg,
						sentrustDate, msgNo, sorgcode, filepath,
						batchReturnList.size(), allamt, spackno, strecode,
						null, sbillorg, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo);

			} catch (JAFDatabaseException e) {
				String error = "接收3131报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (SequenceException e) {
				String error = "接收3131报文处理失败！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			} catch (ValidateException e) {
				String error = "接收3131报文处理失败！";
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

	/***
	 * 拨款清单模式的状态更新，等明细全部更新成功后，更新主单信息
	 * @throws JAFDatabaseException
	 * @throws ValidateException 
	 */
	private void updatePayoutDetailState(SQLExecutor updateExce,
			List<Object> batchReturnList, String soripackno)
			throws JAFDatabaseException, ValidateException {
		String updatesqldetail = "update "
				+ TvPayoutmsgsubDto.tableName()
				+ " set S_STATUS = ?,S_XADDWORD=?,S_XPAYAMT = ?,S_XPAYDATE=?,S_XAGENTBUSINESSNO = ?,S_ACCDATE=? "
				+ " where S_PACKAGENO = ?  AND S_TRASNO = ?  AND S_VOUCHERNO =? AND (S_STATUS <> '80000' OR S_STATUS IS NULL)";
		int count = batchReturnList.size();
		String sorivouno = null;
		String soritrano = null;
		String sacctdate = null;
	    
		for (int i = 0; i < count; i++) {
			updateExce.clearParams();
			HashMap tmpmap = (HashMap) batchReturnList.get(i);
			sorivouno = (String) tmpmap.get("VouNo"); // 原凭证编号
			String sorivoudate = (String) tmpmap.get("VouDate"); // 原凭证日期
			soritrano = (String) tmpmap.get("OriTraNo"); // 原交易流水号
			String dsumamt = (String) tmpmap.get("Amt"); // 金额
			sacctdate = (String) tmpmap.get("AcctDate"); // TCBS系统处理该笔业务的账务日期
			String sresult = (String) tmpmap.get("Result"); // 处理结果
			String ls_Description = (String) tmpmap.get("Description"); // 处理结果的说明

			// 根据处理结果转化交易状态
			String sstatus = PublicSearchFacade
					.getDetailStateByDealCode(sresult);
			updateExce.addParam(sstatus);// 状态
			if (ls_Description.length() > 100) {
				ls_Description.substring(0, 100);
			}
			if (DealCodeConstants.DEALCODE_ITFE_FAIL.equals(sstatus)) {
				dsumamt = "0.00";
			}
			updateExce.addParam(ls_Description);// 错误原因
			updateExce.addParam(dsumamt);// 清算金额
			updateExce.addParam(sacctdate);// 清算日期
			updateExce.addParam(soritrano);// 原交易流水号
			updateExce.addParam(sacctdate);// 账务日期
			updateExce.addParam(soripackno);// 包序号
			updateExce.addParam(soritrano);// 交易流水号
			updateExce.addParam(sorivouno);// 凭证编号
			updateExce.runQuery(updatesqldetail);
		}
	
		TvPayoutmsgsubDto _dto = new TvPayoutmsgsubDto();
		_dto.setSpackageno(soripackno);
		_dto.setStrasno(soritrano);
		_dto.setSvoucherno(sorivouno);
		List <TvPayoutmsgsubDto> list = CommonFacade.getODB().findRsByDto(_dto);
		String bizno ;
		Boolean b =Boolean.TRUE;
		BigDecimal allamt=BigDecimal.ZERO;
		String errinfo = "";
		String status = DealCodeConstants.VOUCHER_FAIL_TCBS;
		Boolean isFail = Boolean.FALSE;
		if (list.size()> 0) {
			bizno =list.get(0).getSbizno();
			//判断bizno的明细是否都收到回执，如果都收到的话，则更新主单和索引表，如果还有没收到的，则不进行更新
			TvPayoutmsgsubDto qdto = new TvPayoutmsgsubDto();
			qdto.setSbizno(bizno);
			List <TvPayoutmsgsubDto> qlist = CommonFacade.getODB().findRsByDto(qdto);
			for (TvPayoutmsgsubDto  comp : qlist) {
				if (null==comp.getSstatus()) {
					b =Boolean.FALSE;
					continue;
				}
				if (DealCodeConstants.DEALCODE_ITFE_FAIL.equals(comp.getSstatus())) {
					isFail =Boolean.TRUE; 
					errinfo +=comp.getSvoucherno()+":"+comp.getSxaddword()+"||";
				}
				if (comp.getSstatus().equals(DealCodeConstants.DEALCODE_ITFE_SUCCESS)) {
					allamt = allamt.add(comp.getNmoney()); // 计算合计金额
				}
			}
			String sdemo="";
			String voustatus="";
            if (b) {//说明已经全部回单，需要更新主单信息和凭证索引表
            	//根据明细的回执情况，判断一个主单对应的明细是否已经全部接收到回执，更新主单信息和索引表的状态
              //计算处理成功的总金额
            	//更新凭证索引表
               if (errinfo.length() > 1000) {
   				 sdemo = errinfo.substring(0, 1000);
   			   }else{
   				 sdemo = errinfo;
   			   }
               if (StringUtils.isBlank(sdemo)) {
   				  sdemo= "TCBS处理成功";
 			   }
               if (sdemo.endsWith("||")) {
            	   sdemo =sdemo.substring(0, sdemo.length() -2);
			   }
    		   /*if (!isFail) {
    			   status = DealCodeConstants.VOUCHER_SUCCESS_NO_BACK;  
  			   }else{
  				   status = DealCodeConstants.VOUCHER_FAIL_TCBS;
			   }*/
              if (allamt.compareTo(BigDecimal.ZERO)>0) {
            	   status = DealCodeConstants.VOUCHER_SUCCESS_NO_BACK; 
            	   voustatus= DealCodeConstants.DEALCODE_ITFE_SUCCESS;
		       }else{
		    	   status = DealCodeConstants.VOUCHER_FAIL_TCBS; 
		    	   voustatus= DealCodeConstants.DEALCODE_ITFE_FAIL;
		       }
  			  updatesqldetail = "update TV_PAYOUTMSGMAIN SET  S_STATUS= ? , S_XPAYAMT = ?,S_XPAYDATE = ?,S_XAGENTBUSINESSNO = ? WHERE S_BIZNO = ?";
  			  updateExce.clearParams();
  			  updateExce.addParam(voustatus);// 
  			  updateExce.addParam(allamt);// 金额
  			  updateExce.addParam(sacctdate);// 日期
  			  updateExce.addParam(soritrano);// 交易流水
  			  updateExce.addParam(bizno);// 凭证流水号
  			  updateExce.runQuery(updatesqldetail);
  			  
  			  updatesqldetail = "update TV_VOUCHERINFO SET  S_STATUS = ?,S_DEMO = ?  WHERE S_DEALNO = ?";
  			  updateExce.clearParams();
  			  updateExce.addParam(status);// 包号
  			  updateExce.addParam(sdemo);//附言
  			  updateExce.addParam(bizno);// 日期
  			  updateExce.runQuery(updatesqldetail);
			}
			
		}
		updateExce.closeConnection();
		
  
		
	}
	
	private void updatePayoutMainStatus(SQLExecutor updateExce,List<Object> batchReturnList, String soripackno,String strecode,String spayeebankno,String sbillorg,String sorientrustDate) throws JAFDatabaseException{

		String updateSql = "update "
				+ TvPayoutmsgmainDto.tableName()
				+ " set S_STATUS = ? , S_DEMO = ? , S_XPAYAMT = ? , S_XPAYDATE = ? , S_XAGENTBUSINESSNO = ?"
				+ " where S_TRECODE = ? and S_PAYEEBANKNO = ? and S_PAYUNIT = ? and S_COMMITDATE = ? and S_PACKAGENO = ? "
				+ " and S_TAXTICKETNO = ? and S_GENTICKETDATE = ? and S_DEALNO = ? and S_STATUS NOT IN (? , ? ) ";
		
		int count = batchReturnList.size();

		for (int i = 0; i < count; i++) {
			updateExce.clearParams();
			HashMap tmpmap = (HashMap) batchReturnList.get(i);
			String sorivouno = (String) tmpmap.get("VouNo"); // 原凭证编号
			String sorivoudate = (String) tmpmap.get("VouDate"); // 原凭证日期
			String soritrano = (String) tmpmap.get("OriTraNo"); // 原交易流水号
			String dsumamt = (String) tmpmap.get("Amt"); // 金额
			//allamt = allamt.add(new BigDecimal(dsumamt)); // 计算合计金额
			String sacctdate = (String) tmpmap.get("AcctDate"); // TCBS系统处理该笔业务的账务日期
			String sresult = (String) tmpmap.get("Result"); // 处理结果
			String ls_Description = (String) tmpmap.get("Description"); // 处理结果的说明

			// 根据处理结果转化交易状态
			String sstatus = PublicSearchFacade
					.getDetailStateByDealCode(sresult);
			updateExce.addParam(sstatus);// 状态
			updateExce.addParam(sresult);
			updateExce.addParam(dsumamt);// 清算金额
			updateExce.addParam(sacctdate);// 清算日期
			updateExce.addParam(soritrano);// 原交易流水号
			updateExce.addParam(strecode);
			updateExce.addParam(spayeebankno);
			updateExce.addParam(sbillorg);
			updateExce.addParam(sorientrustDate);
			updateExce.addParam(soripackno);
			updateExce.addParam(sorivouno);
			updateExce.addParam(sorivoudate);
			updateExce.addParam(soritrano);
			updateExce
					.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// 不更新状态
																		// [80000]
																		// 处理成功
																		// 的凭证
			updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);// 不更新状态
																		// [80001]
																		// 处理失败
																		// 的凭证
			updateExce.runQuery(updateSql);
			// 接收TCBS回执更新批量业务支付明细8207主表状态（上海）
			VoucherUtil
					.updateMatinDtoReceiveTCBS(
							MsgConstant.VOUCHER_NO_8207, sstatus,
							sresult, sacctdate,
							new BigDecimal(dsumamt), strecode,
							sorivouno, sorivoudate,
							MsgConstant.VOUCHER_NO_5207);

			/**
			 * 根据条件 更新凭证状态
			 */
			try {
				Voucher voucher = (Voucher) ContextFactory
						.getApplicationContext().getBean(
								MsgConstant.VOUCHER);
				// 更新实拨资金5207索引表状态
				voucher.VoucherReceiveTCBS(strecode,
						MsgConstant.VOUCHER_NO_5207, soripackno,
						sorivoudate, sorivouno,
						new BigDecimal(dsumamt), sstatus,
						ls_Description);
				/**
				 * 更新批量业务索引表8207的凭证状态（上海）
				 * 
				 * @author 张会斌
				 */
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0)
					voucher.VoucherReceiveTCBS(strecode,
							MsgConstant.VOUCHER_NO_8207,
							MsgConstant.VOUCHER_NO_5207, sorivoudate,
							sorivouno, new BigDecimal(dsumamt),
							sstatus, ls_Description);
			} catch (Exception e) {
				logger.error(e);
				VoucherException.saveErrInfo(null, e);
			}
		}
		updateExce.closeConnection();
	}

}

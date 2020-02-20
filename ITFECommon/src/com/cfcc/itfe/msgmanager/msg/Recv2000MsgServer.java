/**
 * 功能:收到实拨资金额度回执处理
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

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
import com.cfcc.itfe.persistence.dto.TfFundAppropriationDto;
import com.cfcc.itfe.persistence.dto.TfFundClearReceiptDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author wangyunbin
 * 
 */
public class Recv2000MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv2000MsgServer.class);

	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		String updateSql = null,_srecvno = null ,_ssendno = null;
		int count = 0;
		SQLExecutor updateExce = null;
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

		TfFundClearReceiptDto tfFundClearReceiptDto = new TfFundClearReceiptDto();
		tfFundClearReceiptDto.setSmsgid(msgID);
		tfFundClearReceiptDto.setSmsgref(msgRef);
		tfFundClearReceiptDto.setSmsgno(msgNo);
		/**
		 * 取得回执头信息
		 */
		HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead2000");
		String strecode = (String) batchHeadMap.get("TreCode"); // 国库代码
		String sentrustDate = (String) batchHeadMap.get("EntrustDate");// 委托日期
		String spackno = (String) batchHeadMap.get("PackNo"); // 包流水号
		String soripackno = (String) batchHeadMap.get("OriPackNo"); // 原包流水号
		int sumnum = Integer.parseInt((String) batchHeadMap.get("AllNum")); // 总笔数
		BigDecimal sumamt = new BigDecimal((String) batchHeadMap.get("AllAmt")); // 总金额
		
		tfFundClearReceiptDto.setStrecode(strecode);
		tfFundClearReceiptDto.setSentrustdate(sentrustDate);
		tfFundClearReceiptDto.setSpackno(spackno);
		tfFundClearReceiptDto.setSoripackno(soripackno);
		tfFundClearReceiptDto.setNallamt(sumamt);
		
		//记录收发日志，包流水号不能大于8位
		if(spackno.length()>8){
			spackno = spackno.substring(spackno.length()-8);
		}
		
		String sendno = null;
		String recvorg = sdescode;
		String sdemo = "原报文编号:" + MsgConstant.MSG_NO_1000 + ",原包流水:" + soripackno.trim() + "已回执";
		
		String returnWorld = "接收成功";
		String returnStatus = "90000";
		/**
		 * 取得回执批量信息
		 */
		try {
			List<Object> batchReturnList = (List<Object>) msgMap.get("BillReturn2000");
			if (null == batchReturnList || batchReturnList.size() == 0) {
				return;
			} else {
				count = batchReturnList.size();
				// 取原发送包
				TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,MsgConstant.MSG_NO_1000);
				if (null == senddto) {
					returnWorld = "获取原发送包数据失败";
					returnStatus = "90001";
					logger.error("凭证编号：1000,msgRef：" + msgRef);
					throw new ITFEBizException("获取发送包数据失败msgRef：" + msgRef);
				}
				if(senddto.getSretcode().equals(DealCodeConstants.DEALCODE_ITFE_RECEIPT)){
					returnWorld = "收到的清算回执报文重复";
					returnStatus = "90002";
					logger.error("报文标识号：" + msgID + "的清算回执重复！");
					throw new ITFEBizException("接收的清算回执重复，msgid：" + msgID);
				}
				// 接收发送日志流水
				_srecvno = StampFacade.getStampSendSeq("JS");
				_ssendno = StampFacade.getStampSendSeq("FS");
				if ("1".equals((String) batchHeadMap.get("PayoutVouType"))) { //实拨
					updateSql = "update "
							+ TvPayoutmsgmainDto.tableName()
							+ " set S_STATUS = ? , S_DEMO = ? , S_XPAYAMT = ? , S_XPAYDATE = ? , S_XAGENTBUSINESSNO = ?"
							+ " where S_TRECODE = ?   and S_PACKAGENO = ? "
							+ " and S_TAXTICKETNO = ? and S_GENTICKETDATE = ? and S_STATUS NOT IN (? , ? ) ";
				} else if ("2".equals((String) batchHeadMap.get("PayoutVouType"))) { //退库
//					HashMap<String, String> tabMap = ITFECommonConstant.bizMsgNoList;
//					tabname = tabMap.get(MsgConstant.MSG_NO_1104);
//					updateSql = "update "+ tabname+ " set S_STATUS = ?  where S_TAXORGCODE = ? and D_ACCEPT  = ?  and S_DEALNO = ?";
				} else if ("3".equals((String) batchHeadMap.get("PayoutVouType"))) { //商行划款
					updateSql = "update "
							+ TvPayreckBankDto.tableName()
							+ " set S_RESULT = ? , S_ADDWORD = ? ,D_ACCTDATE= ? ,S_XPAYAMT = ? , S_XCLEARDATE = ?  "
							+ " where  S_VOUNO = ? and F_AMT = ? and S_RESULT = ? and S_PACKNO = ? and S_TRANO = ?";
				} else { //其他
					returnWorld = "凭证类型填写有误";
					returnStatus = "90001";
					logger.error("凭证类型填写有误");
					throw new ITFEBizException("凭证类型填写有误");
				}
				
				
				tfFundClearReceiptDto.setIallnum(count);
				tfFundClearReceiptDto.setSpayoutvoutype((String) batchHeadMap.get("PayoutVouType"));
				
				List tmpList = null;
				TvVoucherinfoDto tmpInfoDto = null;
				String sstatus = null;
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				TfFundAppropriationDto tfFundAppropriationDto = null;
				for (int i = 0; i < count; i++) {
					updateExce.clearParams();
					HashMap tmpmap = (HashMap) batchReturnList.get(i);
					String sorivouno = (String) tmpmap.get("VouNo"); // 原凭证编号
					String sorivoudate = (String) tmpmap.get("VouDate"); // 原凭证日期
					String soritrano = (String) tmpmap.get("OriTraNo"); // 原交易流水号
					String sAmt = (String) tmpmap.get("Amt"); // 金额
					// allamt = allamt.add(new BigDecimal(dsumamt)); // 计算合计金额
					String sacctdate = (String) tmpmap.get("AcctDate"); // 该笔业务的清算日期
					String sresult = (String) tmpmap.get("Result"); // 处理结果
					String sAddWord = (String) tmpmap.get("Description"); // 处理结果的说明
					
					tfFundClearReceiptDto.setSvouno(sorivouno);
					tfFundClearReceiptDto.setSvoudate(sorivoudate);
					tfFundClearReceiptDto.setSoritrano(soritrano);
					tfFundClearReceiptDto.setNamt(new BigDecimal(sAmt));
					tfFundClearReceiptDto.setSacctdate(sacctdate);
					tfFundClearReceiptDto.setSresult(PublicSearchFacade.getDetailStateByDealCode(sresult));
					tfFundClearReceiptDto.setSdescription(sAddWord);
					DatabaseFacade.getODB().create(tfFundClearReceiptDto);
					
					if ("3".equals((String) batchHeadMap.get("PayoutVouType"))) { //商行划款
						if (DealCodeConstants.DEALCODE_TIPS_SUCCESS.equals(sresult) || StateConstant.COMMON_NO.equals(sresult))
							sAddWord = DealCodeConstants.PROCESS_SUCCESS;
					}
					// 根据处理结果转化交易状态
					sstatus = PublicSearchFacade.getDetailStateByDealCode(sresult);
					if ("1".equals((String) batchHeadMap.get("PayoutVouType"))) { //实拨
						updateExce.addParam(sstatus);// 状态
						updateExce.addParam(sresult);
						updateExce.addParam(sAmt);// 清算金额
						updateExce.addParam(sacctdate);// 清算日期
						updateExce.addParam(soritrano);// 原交易流水号
						updateExce.addParam(strecode);
						updateExce.addParam(soripackno);
						updateExce.addParam(sorivouno);
						updateExce.addParam(sorivoudate);
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// 不更新状态[80000]处理成功的凭证
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);// 不更新状态[80001]处理失败的凭证
						updateExce.runQuery(updateSql);
					}else if("2".equals((String) batchHeadMap.get("PayoutVouType"))){ //退库
						/*tfFundAppropriationDto = new TfFundAppropriationDto();
						tfFundAppropriationDto.setSmsgref(msgRef);
						tfFundAppropriationDto.setSpackno(soritrano);
						tmpList = CommonFacade.getODB().findRsByDto(
								tfFundAppropriationDto);
						if (null == tmpList) {
							returnWorld = "未查询到资金报文";
							returnStatus = "90001";
							throw new ITFEBizException("未查询到资金报文" + msgRef);
						}
						tfFundAppropriationDto = (TfFundAppropriationDto) tmpList
								.get(0);
						if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(sstatus)) {
							tfFundAppropriationDto.setSstatus("0"); // 清算成功
						} else {
							tfFundAppropriationDto.setSaddword(sAddWord);
						}
						DatabaseFacade.getODB().update(tfFundAppropriationDto);
						continue;*/
					}else if("3".equals((String) batchHeadMap.get("PayoutVouType"))){ //商行划款
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
					}
					tmpInfoDto = new TvVoucherinfoDto();
					tmpInfoDto.setStrecode(strecode);
					tmpInfoDto.setSvoucherno(sorivouno);
					tmpInfoDto.setSext4(sorivoudate); //凭证日期
					tmpInfoDto.setSext1("40");
					tmpList = CommonFacade.getODB().findRsByDto(tmpInfoDto);
					if (null == tmpList || tmpList.size() == 0) {
						returnWorld = "接收资金回执报文2000，未找到原凭证";
						returnStatus = "90001";
						logger.error("接收资金回执报文2000，未找到原凭证！");
						throw new ITFEBizException("接收资金回执报文2000，未找到原凭证！");
					}
					tmpInfoDto = (TvVoucherinfoDto) tmpList.get(0);
					// 更新资金报文状态1000
					tfFundAppropriationDto = new TfFundAppropriationDto();
					tfFundAppropriationDto.setSmsgref(msgRef);
					tfFundAppropriationDto.setSpackno(soritrano); //原包流水号与原交易流水号相同(在发送资金报文时包流水号与交易流水号相同--单包单笔)
					tmpList = CommonFacade.getODB().findRsByDto(tfFundAppropriationDto);
					if (null == tmpList || tmpList.size() == 0) {
						returnWorld = "未查询到资金报文";
						returnStatus = "90001";
						logger.error("未查询到资金报文" + msgRef);
						throw new ITFEBizException("未查询到资金报文" + msgRef);
					}
					tfFundAppropriationDto = (TfFundAppropriationDto) tmpList.get(0);
					if(!"71727380".contains(tmpInfoDto.getSstatus()))
					{
						if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(sstatus)) {
							tmpInfoDto.setSext1("41");
							tmpInfoDto.setSdemo("清算成功");
							tmpInfoDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);//处理成功
							tfFundAppropriationDto.setSstatus("0"); // 清算成功
						} else {
							tmpInfoDto.setSext1("42");
							tmpInfoDto.setSdemo("清算失败");
							tmpInfoDto.setSstatus(DealCodeConstants.VOUCHER_FAIL_TCBS);//处理失败
							tfFundAppropriationDto.setSaddword(sAddWord);
						}
					}
					DatabaseFacade.getODB().update(tfFundAppropriationDto);
					DatabaseFacade.getODB().update(tmpInfoDto);
				}

				// 更新原包状态
				MsgRecvFacade.updateMsgSendLogByMsgId(senddto,DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno, "");
				sendno = senddto.getSsendno();
				recvorg = senddto.getSsendorgcode();
				String filepath = (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER);
				
				// 记接收日志
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg,
						sentrustDate, msgNo, sorgcode, filepath,
						batchReturnList.size(), sumamt, spackno, strecode,
						null, null, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo);
			}
		} catch (JAFDatabaseException e) {
			logger.error(e.getMessage(), e);
			returnStatus = "90006";
			returnWorld = e.getMessage();
			throw new ITFEBizException(e.getMessage(), e);
		} catch (SequenceException e) {
			logger.error(e.getMessage(), e);
			returnStatus = "90006";
			returnWorld = e.getMessage();
			throw new ITFEBizException(e.getMessage(), e);
		} catch (ValidateException e) {
			logger.error(e.getMessage(), e);
			returnStatus = "90006";
			returnWorld = e.getMessage();
			throw new ITFEBizException(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			returnStatus = "90006";
			returnWorld = e.getMessage();
			throw new ITFEBizException(e.getMessage(), e);
		} finally {
			if (null != updateExce) {
				updateExce.closeConnection();
			}
			try {
				TvVoucherinfoDto vDto = new TvVoucherinfoDto();
				vDto.setSpaybankcode(sorgcode);
				vDto.setStrecode(strecode);
				
				MuleClient client = new MuleClient();

				Map map = new HashMap();
				MuleMessage message = new DefaultMuleMessage(map);
				message.setProperty(MessagePropertyKeys.MSG_NO_KEY,"TBS_3001_OUT");
				message.setProperty(MessagePropertyKeys.MSG_STATE, returnStatus);
				message.setProperty(MessagePropertyKeys.MSG_ADDWORD, returnWorld);
				message.setProperty(MessagePropertyKeys.MSG_TAG_KEY, msgNo);
				message.setProperty(MessagePropertyKeys.MSG_DESC, sorgcode);
				message.setProperty(MessagePropertyKeys.MSG_REF, msgID);
				message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
				message.setProperty(MessagePropertyKeys.MSG_PACK_NO, spackno);
				//接收发送主键值
				message.setProperty(MessagePropertyKeys.MSG_ORGCODE, _srecvno);	//接收
				message.setProperty(MessagePropertyKeys.MSG_BILL_CODE, _ssendno);	//发送
				
				message.setPayload(map);

				message = client.send("vm://ManagerMsgWithCommBank", message);

			} catch (MuleException e) {
				logger.error(e);
				throw new ITFEBizException(e.getMessage(),e);
			} 
		}
	}

}

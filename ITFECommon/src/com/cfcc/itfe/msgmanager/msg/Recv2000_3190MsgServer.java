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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 
 * 主要功能：6.2.15　TCBS批量处理结果通知(3190)
 * 
 * @author wangwenbo
 * 
 */
public class Recv2000_3190MsgServer extends AbstractMsgManagerServer {

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
		
		String sdemo = "原报文编号:" + MsgConstant.MSG_NO_1000 + ",原包流水:" + soripackno + ","+ ",原委托日期:" + sorientrustDate;
		String updateSql = "";
		String sendno = null;
		List billReturnList = (List) msgMap.get("BillReturn2000");
		HashMap<String, String> tabMap = ITFECommonConstant.bizMsgNoList;
		String tabname = tabMap.get(MsgConstant.MSG_NO_1104);
		if (null == billReturnList || billReturnList.size() == 0) {
			return;
		} else {
			updateSql = "update "+ tabname+ " set S_STATUS = ? ";
			updateSql+=" where S_TAXORGCODE = ? and D_ACCEPT  = ?  and S_DEALNO = ?";
			int count = billReturnList.size();
			TvVoucherinfoDto tmpInfoDto = null;
			for (int i = 0; i < count; i++) {
				HashMap tmpmap = (HashMap) billReturnList.get(i);
				String sorivouno = (String) tmpmap.get("VouNo"); // 原凭证编号
				String sorivoudate = (String) tmpmap.get("VouDate"); // 原凭证日期
				String soritrano = (String) tmpmap.get("OriTraNo"); // 原交易流水号
				String dsumamt = (String) tmpmap.get("Amt"); // 金额
				//allamt = allamt.add(new BigDecimal(dsumamt)); // 计算合计金额
				String sacctdate = (String) tmpmap.get("AcctDate"); // 该笔业务的清算日期
				String sresult = (String) tmpmap.get("Result"); // 处理结果
				String ls_Description = (String) tmpmap.get("Description"); // 处理结果的说明
				SQLExecutor updateExce = null;
				try {
					updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
							.getSQLExecutor();
					updateExce.clearParams();
					String sstatus = PublicSearchFacade.getDetailStateByDealCode(sresult);
					updateExce.addParam(sstatus);
					updateExce.addParam(sbillorg);
					updateExce.addParam(CommonUtil.strToDate(sorientrustDate)
							.toString());
					updateExce.addParam(soritrano);
					updateExce.runQuery(updateSql);
					updateExce.closeConnection();
					
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
//					 * 退库业务更新凭证索引表（无纸化业务）
//					 */
//					try{
//						Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER);
//						voucher.VoucherReceiveTCBS(sbillorg, MsgConstant.VOUCHER_NO_5209, soripackno,
//								sorientrustDate, sorivouno, new BigDecimal(dsumamt), sstatus, "");
//					}catch(Exception e){
//						logger.error(e);
//						VoucherException.saveErrInfo(null, e);
//					}
				} catch (JAFDatabaseException e) {
					String error = "更新接收TCBS批量处理结果通知(3190)回执状态时出现数据库异常！";
					logger.error(error, e);
					throw new ITFEBizException(error, e);
				} catch (ValidateException e) {
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
			//TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(OriMsgNo,OriTaxOrgCode, OriEntrustDate, OriPackNo,DealCodeConstants.DEALCODE_ITFE_RECEIPT);
			TvSendlogDto  senddto = MsgRecvFacade.findSendLogByMsgId(msgRef, msgNo);
			// 接收日志流水
			String _srecvno;
			try {
				_srecvno = StampFacade.getStampSendSeq("JS");
				// 更新原包状态
				if (null != senddto) {
					// 更新原发送日志流水号
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto,DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno,"");
					sendno = senddto.getSsendno();
					recvorg = senddto.getSsendorgcode();
					// 根据条件更新文件与包的对应关系表
					MsgRecvFacade.updateMsgHeadByCon( sbillorg, soripackno, sorientrustDate, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
				}
				String filepath = (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH");
				String stamp = TimeFacade.getCurrentStringTime();
				String ifsend = (String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER);
				// 记接收日志
				MsgLogFacade.writeRcvLog(_srecvno, sendno, recvorg, sentrustDate, msgNo,
						sorgcode, filepath, billReturnList.size(), sumamt, spackno, strecode, null,
						sbillorg, null, msgID,
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						ifsend, MsgConstant.ITFE_SEND, sdemo );

			} catch (SequenceException e) {
				String error = "接收(2000-3190)回执状态时出现数据库异常！";
				logger.error(error, e);
				throw new ITFEBizException(error, e);
			}

		}
		eventContext.setStopFurtherProcessing(true);
		return;
	}
}

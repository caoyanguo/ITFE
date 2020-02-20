package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;

import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;

import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 本工作日TIPS向某外联机构转发的所有相关报文信息
 * 主要功能：接收代发财政款项回执
 * @author wangyunbin
 * 
 */
public class Recv3136MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3136MsgServer.class);

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
		
		/**
		 * 解析入库批量头 MSG->BillHead3136
		 */
		// 解析实时业务头 CFX->MSG->BatchReturn3136
		HashMap batchheadMap = (HashMap) msgMap.get("BatchReturn3136");
		String treCode = (String) batchheadMap.get("TreCode"); // 国库主体代码
		String oriBillOrg = (String) batchheadMap.get("OriBillOrg"); // 原出票单位
		String entrustDate = (String) batchheadMap.get("EntrustDate"); // 委托日期
		String packNo = (String) batchheadMap.get("PackNo"); // 包流水号
		String oriPackNo = (String) batchheadMap.get("OriPackNo"); // 原包流水号
		String oriEntrustDate = (String) batchheadMap.get("OriEntrustDate"); // 原委托日期
		
		int allNum = Integer.parseInt(batchheadMap.get("AllNum").toString().trim()); // 总笔数
		BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("AllAmt")); // 总金额
		
		String payoutVouType = (String) batchheadMap.get("PayoutVouType"); // 支出凭证类型

		String vouNo = (String) batchheadMap.get("VouNo"); // 凭证编号
		String vouDate = (String) batchheadMap.get("VouDate"); // 凭证日期
		String result = (String) batchheadMap.get("Result"); // 处理结果
		String addWord = (String) batchheadMap.get("AddWord"); // 附言
		String sendno = null;
		String sdemo ="原报文编号:"+MsgConstant.MSG_NO_5112+",原包流水:"+oriPackNo.trim()+",原发起机构代码"+oriBillOrg.trim()+",原委托日期:"+oriEntrustDate.trim();
		String sbookorgcode = sdescode;
		

		
		SQLExecutor updateExce = null;
		try {
			
			TsTreasuryDto findto = SrvCacheFacade.cacheTreasuryInfo(null).get(treCode);
			if (null!=findto) {
				sbookorgcode =findto.getSorgcode();
			}
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updateSql = "update " + TvPayoutfinanceMainDto.tableName() + " set S_STATUS = ? , S_RESULT = ? "
					+ " where S_BILLORG = ? and S_ENTRUSTDATE  = ? and S_PACKAGENO = ? and N_AMT = ? "
					+ " and S_VOUNO = ? ";
			updateExce.clearParams();
			String sstatus = PublicSearchFacade.getDetailStateByDealCode(result);
			updateExce.addParam(sstatus);
			updateExce.addParam(MtoCodeTrans.transformString(addWord));
			updateExce.addParam(oriBillOrg);
			updateExce.addParam(oriEntrustDate);
			updateExce.addParam(oriPackNo);
			updateExce.addParam(allAmt);
			updateExce.addParam(vouNo);
			updateExce.runQuery(updateSql);
			updateExce.closeConnection();
			//取原发送包
			TvSendlogDto  senddto = MsgRecvFacade.findSendLogByMsgId(MsgConstant.MSG_NO_5112, oriBillOrg, oriEntrustDate, oriPackNo, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
			//接收日志流水d
			String _srecvno = StampFacade.getStampSendSeq("JS");
			//更新原包状态
			if (null!=senddto) {
				//更新原发送日志流水号
				MsgRecvFacade.updateMsgSendLogByMsgId(senddto, DealCodeConstants.DEALCODE_ITFE_RECEIPT, _srecvno, "");
				sendno = senddto.getSsendno();
				// 根据条件更新文件与包的对应关系表
				MsgRecvFacade.updateMsgHeadByCon( oriBillOrg, oriPackNo, oriEntrustDate, DealCodeConstants.DEALCODE_ITFE_RECEIPT);
			}
			String filepath = (String) eventContext.getMessage().getProperty(
					"XML_MSG_FILE_PATH");
			String stamp = TimeFacade.getCurrentStringTime();
			String ifsend = (String) eventContext.getMessage().getProperty(
					MessagePropertyKeys.MSG_SENDER);
			// 记接收日志
			MsgLogFacade.writeRcvLog(_srecvno, sendno, sbookorgcode, entrustDate, msgNo,
					sorgcode, filepath, allNum, allAmt, packNo, treCode, null,
					oriBillOrg, null, msgID,
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, addWord, null,
					ifsend, MsgConstant.ITFE_SEND, sdemo );
		} catch (JAFDatabaseException e) {
			String error = "更新接收代发财政款项回执状态时出现数据库异常！";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} catch (SequenceException e) {
			String error = "更新接收代发财政款项回执状态时出现数据库异常！";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (null != updateExce) {
				updateExce.closeConnection();
			}
		}
		eventContext.setStopFurtherProcessing(true);
		return;
	}
}

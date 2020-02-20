package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 
 * 主要功能：接收与征收机关更正核对通知
 * @author wangyunbin
 * 
 */
public class Recv3124MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3124MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		/**
		 * 解析入库批量头 MSG->BillHead3139
		 */
		// 解析实时业务头 CFX->MSG->BatchReturn3136
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3124");
		String ChkDate = (String) batchheadMap.get("ChkDate"); // 对账日期
		String PackNo = (String) batchheadMap.get("PackNo"); // 包流水号
		String TaxOrgCode = (String) batchheadMap.get("TaxOrgCode"); // 征收机关代码
		int AllNum = Integer.parseInt(batchheadMap.get("AllNum").toString().trim()); // 总笔数
		BigDecimal AllAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("AllAmt")); // 总金额
		int ChildPackNum = Integer.parseInt(batchheadMap.get("ChildPackNum").toString().trim()); // 子包总数
		String CurPackNo = (String) batchheadMap.get("CurPackNo"); // 本包序号
		int CurPackNum = Integer.parseInt(batchheadMap.get("CurPackNum").toString().trim()); // 本包笔数
		BigDecimal CurPackAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("CurPackAmt")); // 本包金额

		/**
		 * 保存接收日志
		 */
		try {
			TvRecvlogDto recvlogdto = new TvRecvlogDto();
			recvlogdto.setSrecvno(StampFacade.getStampSendSeq("JS")); // 流水号
			recvlogdto.setSpackno(PackNo); // 原包流水号
			recvlogdto.setSdate(ChkDate);// 所属日期-委托日期
			recvlogdto.setSoperationtypecode(MsgConstant.MSG_NO_3124);// 业务凭证类型
			recvlogdto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));// 发送时间
			recvlogdto.setSretcode(""); // 处理结果
			recvlogdto.setSretcodedesc(AllNum+"-"+AllAmt); // 处理说明- 附言
			recvlogdto.setIcount(AllNum);
			recvlogdto.setNmoney(AllAmt);
			recvlogdto.setStitle((String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"));
			recvlogdto.setSsenddate(TimeFacade.getCurrentStringTime());
			recvlogdto.setSrecvorgcode((String) headMap.get("DES"));
			recvlogdto.setSsendorgcode((String) headMap.get("SRC"));
			recvlogdto.setSseq((String) headMap.get("MsgID"));
			DatabaseFacade.getDb().create(recvlogdto);
		} catch (SequenceException e) {
			logger.error("取接收流水号失败!", e);
			throw new ITFEBizException("取接收流水号失败", e);
		} catch (JAFDatabaseException e) {
			logger.error("创建接收日志出现数据库异常!", e);
			throw new ITFEBizException("创建接收日志出现数据库异常!", e);
		}
		/**
		 * 
		
		List returnList = (List) msgMap.get("CompCorrTrea3124");
		if (null == returnList || returnList.size() == 0) {
			return;
		}
		
		SQLExecutor updateExce = null;
		try {
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updateSql = "update " + TvInCorrhandbookDto.tableName() + " set S_STATUS = ? "
			+ " where S_CURTAXORGCODE = ? and D_ACCEPT  = ? and S_PACKAGENO = ? and S_DEALNO = ? and F_CURCORRAMT = ? "
			+ " and S_CORRVOUNO = ? ";
			int count = returnList.size();
			for (int i = 0; i < count; i++) {
				updateExce.clearParams();
				HashMap CompRetTrea3124Map = (HashMap) returnList.get(i);
				String OriSendOrgCode = (String) CompRetTrea3124Map.get("OriSendOrgCode"); // 原发起机构代码
				String OriEntrustDate = (String) CompRetTrea3124Map.get("OriEntrustDate"); // 原委托日期
				String OriPackNo = (String) CompRetTrea3124Map.get("OriPackNo"); // 原包流水号
				String OriTraNo = (String) CompRetTrea3124Map.get("OriTraNo"); // 原交易流水号
				String CorrVouNo = (String) CompRetTrea3124Map.get("CorrVouNo"); // 更正凭证编号
				BigDecimal OriTraAmt = MtoCodeTrans.transformBigDecimal(CompRetTrea3124Map.get("OriTraAmt"));// 原交易金额
				BigDecimal CurTraAmt = MtoCodeTrans.transformBigDecimal(CompRetTrea3124Map.get("CurTraAmt"));// 现交易金额
				String OpStat = (String) CompRetTrea3124Map.get("OpStat"); // 处理状态
				String AddWord = (String) CompRetTrea3124Map.get("AddWord"); // 附言
				String sstatus = PublicSearchFacade.getDetailStateByDealCode(OpStat);
				updateExce.addParam(sstatus);
				updateExce.addParam(OriSendOrgCode);
				updateExce.addParam(CommonUtil.strToDate(OriEntrustDate).toString());
				updateExce.addParam(OriPackNo);
				updateExce.addParam(OriTraNo);
				updateExce.addParam(CurTraAmt);
				updateExce.addParam(CorrVouNo);

				updateExce.runQuery(updateSql);
			}

			updateExce.closeConnection();
			
		} catch (JAFDatabaseException e) {
			String error = "更新更正处理回执状态时出现数据库异常！";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (null != updateExce) {
				updateExce.closeConnection();
			}
		}
		 * 
		 */
		eventContext.setStopFurtherProcessing(true);
		return;
	}
}

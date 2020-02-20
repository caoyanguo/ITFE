/**
 * 入库流水明细
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.HtvTaxorgIncomeDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvTaxorgIncomeDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author wangtuo
 * 
 */
public class Proc3126MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Proc3126MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		HashMap BillHead3126 = (HashMap) msgMap.get("BillHead3126");

		// 解析报文头 headMap
		String orgcode = (String) headMap.get("DES");// 接收机构代码
		String sendorgcode = (String) headMap.get("SRC");// 发送机构代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号
		String msgref = (String) headMap.get("MsgRef"); // 报文id号
		/**
		 * 解析入库批量头信息 msgMap-->BillHead3126
		 */
		String TaxOrgCode = (String) BillHead3126.get("TaxOrgCode"); // 征收机关代码
		String InTreDate = (String) BillHead3126.get("InTreDate"); // 入库凭证日期
		String PackNo = (String) BillHead3126.get("PackNo"); // 包流水号
		String ChildPackNum = (String) BillHead3126.get("ChildPackNum"); // 子包总数
		String CurPackNo = (String) BillHead3126.get("CurPackNo"); // 本包序号
		String TaxAllNum = (String) BillHead3126.get("TaxAllNum"); // 本包税票总笔数
		String DrawBackAllNum = (String) BillHead3126.get("DrawBackAllNum"); // 本包退库总笔数
		String CorrAllNum = (String) BillHead3126.get("CorrAllNum"); // 本包更正总笔数
		String FreeAllNum = (String) BillHead3126.get("FreeAllNum"); // 本包免抵调总笔数

		List Bill3126list = (List) msgMap.get("Bill3126");// 入库流水信息
        //先不保存数据，直接转发外机构
		/*// 先不保存数据
		TvTaxorgIncomeDto deldto = new TvTaxorgIncomeDto();
		deldto.setStaxorgcode(TaxOrgCode);
		deldto.setSintredate(InTreDate);
		// 先删除对应的数据
		HtvTaxorgIncomeDto hdeldto = new HtvTaxorgIncomeDto();
		hdeldto.setStaxorgcode(TaxOrgCode);
		hdeldto.setSintredate(InTreDate);
		try {
			CommonFacade.getODB().deleteRsByDto(deldto);
			CommonFacade.getODB().deleteRsByDto(hdeldto);
		} catch (ValidateException e1) {
			logger.error("删除入库流水明细时发生数据库异常!", e1);
			throw new ITFEBizException("查询入库流水明细时发生数据库异常!", e1);
		} catch (JAFDatabaseException e1) {
			logger.error("删除预入库流水明细时发生数据库异常!", e1);
			throw new ITFEBizException("删除入库流水明细时发生数据库异常!", e1);
		}
*/
		/**
		 * 解析入库流水信息 msgMap-> Bill3126
		 
		if (null == Bill3126list || Bill3126list.size() == 0) {
			// 没有入库流水明细数据
		} else {

			int taxtypecount = Bill3126list.size();
			// 存库用LIST
			List<IDto> incomelist = new ArrayList<IDto>();
			for (int i = 0; i < taxtypecount; i++) {
				HashMap Bill3126 = (HashMap) Bill3126list.get(i);

				String TreCode = (String) Bill3126.get("TreCode"); // 国库代码
				String ExportVouType = (String) Bill3126.get("ExportVouType"); // 导出凭证类型
				String ExpTaxVouNo = (String) Bill3126.get("ExpTaxVouNo"); // 凭证号码
				String BudgetType = (String) Bill3126.get("BudgetType"); // 预算种类
				String BudgetLevelCode = (String) Bill3126
						.get("BudgetLevelCode"); // 预算级次
				String BudgetSubjectCode = (String) Bill3126
						.get("BudgetSubjectCode"); // 预算科目
				BigDecimal TraAmt = MtoCodeTrans.transformBigDecimal(Bill3126
						.get("TraAmt")); // 交易金额
				String Origin = (String) Bill3126.get("Origin"); // 凭证来源

				// 组织DTO准备保存数据******************************************************
				// 入库流水明细回执信息 TvTaxorgIncomeDto
				TvTaxorgIncomeDto tvtaxorgincomedto = new TvTaxorgIncomeDto();
				tvtaxorgincomedto.setStaxorgcode(TaxOrgCode); // 财政机关代码
				tvtaxorgincomedto.setStrecode(TreCode); // 国库代码
				tvtaxorgincomedto.setSintredate(InTreDate); // 入库凭证日期
				tvtaxorgincomedto.setIpkgseqno(PackNo); // 包流水号
				tvtaxorgincomedto.setSexpvouno(ExpTaxVouNo); // 凭证编号
				tvtaxorgincomedto.setSexpvoutype(ExportVouType); // 导出凭证类型
				tvtaxorgincomedto.setCbdglevel(BudgetLevelCode); // 预算级次
				tvtaxorgincomedto.setSbdgsbtcode(BudgetSubjectCode); // 预算科目代码
				tvtaxorgincomedto.setCbdgkind(BudgetType); // 预算种类
				tvtaxorgincomedto.setFamt(TraAmt); // 金额
				tvtaxorgincomedto.setCvouchannel(Origin); // 凭证来源
				incomelist.add(tvtaxorgincomedto);
				if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
						|| (i + 1) == taxtypecount) {
					try {
						DatabaseFacade.getDb().create(
								CommonUtil.listTArray(incomelist));
					} catch (JAFDatabaseException e) {
						logger.error("保存入库流水明细回执数据时出现数据库异常!", e);
						throw new ITFEBizException("保存入库流水明细回执数据时出现数据库异常!", e);
					}
					incomelist.clear();
				}
			}
		}
		*/
		/*
		 * 接收/发送日志
		 */
		String recvseqno;// 接收日志流水号
		String sendseqno;// 发送日志流水号
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("取接收/发送日志SEQ出错");
		}
		String path = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_FILE_NAME);
		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, recvseqno, orgcode, InTreDate, msgNo,
				sendorgcode, path, Integer
						.valueOf(TaxAllNum), new BigDecimal(0), PackNo, null,
				null, TaxOrgCode, null, msgid,
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_RECV_TIPS+msgNo);

		/*// 判断是否需要转发给财政，如果为2则转发给财政并记发送日志
		TvSendlogDto tvsendlog = null;
		List tvsendloglist = null;
		try {
			tvsendlog = new TvSendlogDto();
			tvsendlog.setSdate(InTreDate);
			tvsendlog.setSbillorg(TaxOrgCode);
			tvsendlog.setSoperationtypecode(MsgConstant.MSG_NO_1024);
//			tvsendlog.setSseq(msgref);
			tvsendloglist = CommonFacade.getODB().findRsByDto(tvsendlog);
		} catch (Exception e1) {
			logger.error("查询对应原始报文时出现数据库异常!", e1);
			throw new ITFEBizException("查询对应原始报文时出现数据库异常!", e1);
		}

		if (null == tvsendloglist || tvsendloglist.size() == 0) {
			eventContext.setStopFurtherProcessing(true);
			logger.error("找不到对应的1024原始发送报文!");
		} else if (tvsendloglist.size() >= 2) {
			try {
				tvsendlog = (TvSendlogDto) tvsendloglist.get(0);
				tvsendlog.setSretcode(DealCodeConstants.DEALCODE_TIPS_SUCCESS);
				tvsendlog
						.setSproctime(new Timestamp(new java.util.Date().getTime()));
				DatabaseFacade.getDb().update(tvsendlog);
			} catch (JAFDatabaseException e1) {
				logger.error("更新发送日志处理状态出现数据库异常!", e1);
				throw new ITFEBizException("更新发送日志处理状态出现数据库异常!", e1);
			}
			*/
//			if (StateConstant.MSG_SENDER_FLAG_2.equals(tvsendlog.getSifsend())) {
				// 写发送日志
			

//				// 不做进一步处理
				eventContext.setStopFurtherProcessing(true);
				return;
//			}
			
//		}
	}
}

/**
 * 实时扣税请求
 */
package com.cfcc.itfe.msgmanager.msg;

import java.sql.Timestamp;
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
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvTaxDto;
import com.cfcc.itfe.persistence.dto.TvTaxItemDto;
import com.cfcc.itfe.persistence.dto.TvTaxKindDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * 
 * @author wangtuo
 * 
 */
public class Recv1001MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv1001MsgServer.class);

	/**
	 * 报文信息处理
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// 解析报文头 headMap
		String orgcode = (String) headMap.get("DES");// 接收机构代码
		String sendorgcode = (String) headMap.get("SRC");// 发送机构代码
		String msgNo = (String) headMap.get("MsgNo");// 报文编号
		String msgid = (String) headMap.get("MsgID"); // 报文id号
		String sbookorgcode = sendorgcode;

		/**
		 * 解析实时业务头 msgMap-->RealHead1001
		 */
		HashMap realhead1001 = (HashMap) msgMap.get("RealHead1001");

		String taxorgcode = (String) realhead1001.get("TaxOrgCode");// 征收机关代码
		String entrustdate = (String) realhead1001.get("EntrustDate");// 委托日期
		String trano = (String) realhead1001.get("TraNo");// 交易流水号

		/**
		 * 解析转账信息 msgMap-->TurnAccount1001
		 */
		HashMap turnaccount1001 = (HashMap) msgMap.get("TurnAccount1001");

		String handletype = (String) turnaccount1001.get("HandleType");// 经收类别
		String payeebankno = (String) turnaccount1001.get("PayeeBankNo");// 收款行行号
		String payeeorgcode = (String) turnaccount1001.get("PayeeOrgCode");// 收款单位代码
		String payeeacct = (String) turnaccount1001.get("PayeeAcct");// 收款人账号
		String payeename = (String) turnaccount1001.get("PayeeName");// 收款人名称
		String paybkcode = (String) turnaccount1001.get("PayBkCode");// 付款行行号
		String payopbkcode = (String) turnaccount1001.get("PayOpBkCode");// 付款开户行行

		/**
		 * 解析基本信息－付款信息 msgMap-->Payment1001
		 */
		HashMap payment1001 = (HashMap) msgMap.get("Payment1001");

		String payacct = (String) payment1001.get("PayAcct");// 付款账户
		String handorgname = (String) payment1001.get("HandOrgName");// 缴款单位名称
		String traamt = (String) payment1001.get("TraAmt");// 交易金额
		String Yaxvouno = (String) payment1001.get("TaxVouNo");// 税票号码
		String billdate = (String) payment1001.get("BillDate");// 开票日期
		String taxpaycode = (String) payment1001.get("TaxPayCode");// 纳税人编码
		String taxpayname = (String) payment1001.get("TaxPayName");// 纳税人名称
		String corpcode = (String) payment1001.get("CorpCode");// 企业代码
		String protocolno = (String) payment1001.get("ProtocolNo");// 协议书号
		String budgettype = (String) payment1001.get("BudgetType");// 预算种类
		String trimsign = (String) payment1001.get("TrimSign");// 整理期标志
		String corptype = (String) payment1001.get("CorpType");// 企业注册类型
		String printvousign = (String) payment1001.get("PrintVouSign");// 打印付款凭证标志
		String remark = (String) payment1001.get("Remark");// 备注
		String remark1 = (String) payment1001.get("Remark1");// 备注1
		String remark2 = (String) payment1001.get("Remark2");// 备注2
		String taxtypenum = (String) payment1001.get("TaxTypeNum");// 税种条数

		// 组织DTO准备保存数据******************************************************
		// 实时扣税信息 TvTaxDto
		TvTaxDto tvtaxdto = new TvTaxDto();

		/*
		 * 实时业务头信息 msgMap-->RealHead1001
		 */
		tvtaxdto.setStaxorgcode(taxorgcode);// 征收机关代码
		tvtaxdto.setStrano(trano);// 交易流水号
		tvtaxdto.setSentrustdate(entrustdate);// 委托日期
		/*
		 * 转账信息 msgMap-->TurnAccount1001
		 */
		tvtaxdto.setChandletype(handletype);// 经收类别
		tvtaxdto.setSpayeebankno(payeebankno);// 收款行行号
		tvtaxdto.setSpayeeorgcode(payeeorgcode);// 收款单位代码
		tvtaxdto.setSpayeeacct(payeeacct);// 收款人账号
		tvtaxdto.setSpayeename(payeename);// 收款人名称
		tvtaxdto.setSpaybkcode(paybkcode);// 付款行行号
		tvtaxdto.setSpayopbkcode(payopbkcode);// 付款开户行行号
		/*
		 * 基本信息－付款信息 msgMap-->Payment1001
		 */
		tvtaxdto.setSpayacct(payacct);// 付款账户
		tvtaxdto.setShandorgname(handorgname);// 缴款单位名称
		tvtaxdto.setFtraamt(MtoCodeTrans.transformBigDecimal(traamt));// 交易金额
		tvtaxdto.setStaxvouno(Yaxvouno);// 税票号码
		tvtaxdto.setSbilldate(billdate);// 开票日期
		tvtaxdto.setStaxpaycode(taxpaycode);// 纳税人编码
		tvtaxdto.setStaxpayname(taxpayname);// 纳税人名称
		tvtaxdto.setScorpcode(corpcode);// 企业代码
		tvtaxdto.setSprotocolno(protocolno);// 协议书号
		tvtaxdto.setCbudgettype(budgettype);// 预算种类
		tvtaxdto.setCtrimsign(trimsign);// 整理期标志
		tvtaxdto.setScorptype(corptype);// 企业注册类型
		tvtaxdto.setCprintflag(printvousign);// 打印付款凭证标志
		tvtaxdto.setSremark(remark);// 备注
		tvtaxdto.setSremark1(remark1);// 备注1
		tvtaxdto.setSremark2(remark2);// 备注2
		tvtaxdto.setItaxkindcount(Short.valueOf(taxtypenum));// 税种条数

		String detailDealCode = StateConstant.DATA_FLAG_CHECK;// 处理状态 默认接收成功
		String cancelDealCode = StateConstant.CANCEL_FLAG_NOCHECK;// 默认未冲正
		String reckontype = StateConstant.RECKONTYPE_FLAG_ONE;// 默认1清算渠道

		tvtaxdto.setSmsgid(msgid);// 报文标识号
		// tvtaxdto.setSmqmsgid(msgid);//消息标识号
		tvtaxdto.setSpayeeopbkcode(payeebankno);// 收款开户行行号
		tvtaxdto.setCreckontype(reckontype);// 清算渠道
		tvtaxdto.setSprocstat(detailDealCode);// 处理状态
		tvtaxdto.setSresult("00000");// 处理结果
		tvtaxdto.setCcancelstat(cancelDealCode);// 冲正状态
		tvtaxdto.setSacceptdate(TimeFacade.getCurrentStringTime());// 受理日期
		tvtaxdto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// 更新时间
		tvtaxdto.setSstatus("00000");// 交易状态
		String bizseq;// 业务流水号
		try {
			bizseq = StampFacade.getBizSeq("SSKS");
			tvtaxdto.setSseq(bizseq);// 业务流水号
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("取业务流水号SEQ出错");
		}

		// 保存实时扣税信息数据 ,可能有dto是空的情况
		if (null != tvtaxdto) {
			try {
				// 保存实时扣税信息数据
				DatabaseFacade.getDb().create(tvtaxdto);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("数据库出错", e);
			}
		}

		List taxtypelist1001 = (List) payment1001.get("TaxTypeList1001");// 税种明细
		/**
		 * 解析税（费）票信息－税（费）种明细 msgMap-->payment1001-->taxtypelist1001
		 */
		int taxtypecount = taxtypelist1001.size();
		for (int i = 0; i < taxtypecount; i++) {
			HashMap taxtypelist1001map = (HashMap) taxtypelist1001.get(i);

			String projectid = (String) taxtypelist1001map.get("ProjectId");// 项目序号
			String budgetsubjectcode = (String) taxtypelist1001map
					.get("BudgetSubjectCode");// 预算科目代码
			String limitdate = (String) taxtypelist1001map.get("LimitDate");// 限缴日期
			String taxtypename = (String) taxtypelist1001map.get("TaxTypeName");// 税种名称
//			String budgetlevelcode = (String) taxtypelist1001map
//					.get("BudgetLevelCode");// 预算级次代码
//			String budgetlevelname = (String) taxtypelist1001map
//					.get("BudgetLevelName");// 预算级次名称
			String taxstartdate = (String) taxtypelist1001map
					.get("TaxStartDate");// 税款所属日期起
			String taxenddate = (String) taxtypelist1001map.get("TaxEndDate");// 税款所属日期止
			String vicesign = (String) taxtypelist1001map.get("ViceSign");// 辅助标志
			String taxtype = (String) taxtypelist1001map.get("TaxType");// 税款类型
			String taxtypeamt = (String) taxtypelist1001map.get("TaxTypeAmt");// 税种金额
			String detailnum = (String) taxtypelist1001map.get("DetailNum");// 明细条数
			List taxsubjectlist1001 = (List) taxtypelist1001map
					.get("TaxSubjectList1001");// 税目明细

			// 组织DTO准备保存数据******************************************************
			// 实时扣税税种信息 dto
			TvTaxKindDto tvtaxkinddto = new TvTaxKindDto();

			String budgetlevel = MsgConstant.BUDGET_LEVEL_CENTER;// 预算级次 1 中央

			tvtaxkinddto.setSseq(bizseq);// 业务流水号
			tvtaxkinddto.setCbudgetlevel(budgetlevel);// 预算级次 1 中央
			tvtaxkinddto.setFsubjectamt(MtoCodeTrans
					.transformBigDecimal(taxtypeamt));// 科目金额
			tvtaxkinddto.setIprojectid(Integer.valueOf(projectid));// 项目序号
			// 税种序号，大小小于100
			tvtaxkinddto.setIdetailno(Integer.valueOf(detailnum));// 明细条数
			tvtaxkinddto.setSbudgetsubjectcode(budgetsubjectcode);// 预算科目代码
			// tvtaxkinddto.setSentrustdate(entrustdate);// 委托日期
			tvtaxkinddto.setSlimitdate(limitdate);// 限缴日期
			tvtaxkinddto.setStaxenddate(taxenddate);// 税款所属日期止
			// tvtaxkinddto.setStaxorgcode(taxorgcode);// 征收机关代码
			tvtaxkinddto.setStaxstartdate(taxstartdate);// 税款所属日期起
			tvtaxkinddto.setStaxtypename(taxtypename);// 税种名称
			// tvtaxkinddto.setStrano(trano);// 交易流水号
			tvtaxkinddto.setSvicesign(vicesign);// 辅助标志
			tvtaxkinddto.setCtaxtype(taxtype);// 税款类型
			tvtaxkinddto.setTsupdate(new Timestamp(new java.util.Date()
					.getTime()));// 更新时间

			// 保存实时扣税税种信息数据 ,可能有List 是空的情况
			if (null != tvtaxkinddto) {
				try {
					// 保存数据
					DatabaseFacade.getDb().create(tvtaxkinddto);
				} catch (JAFDatabaseException e) {
					logger.error(e);
					throw new ITFEBizException("数据库出错", e);
				}
			}

			/**
			 * 解析税种信息－税目明细
			 * msgMap-->payment1001-->taxtypelist1001-->taxsubjectlist1001
			 */
			int taxsubjectcount = taxsubjectlist1001.size();
			for (int j = 0; j < taxsubjectcount; j++) {
				HashMap taxsubjectlist1001map = (HashMap) taxsubjectlist1001
						.get(j);

				String detailno = (String) taxsubjectlist1001map
						.get("DetailNo");// 明细序号
				String taxsubjectcode = (String) taxsubjectlist1001map
						.get("TaxSubjectCode");// 税目代码
				String taxsubjectname = (String) taxsubjectlist1001map
						.get("TaxSubjectName");// 税目名称
				String taxnumber = (String) taxsubjectlist1001map
						.get("TaxNumber");// 课税数量
				String taxamt = (String) taxsubjectlist1001map.get("TaxAmt");// 计税金额
				String taxrate = (String) taxsubjectlist1001map.get("TaxRate");// 税率
				String exptaxamt = (String) taxsubjectlist1001map
						.get("ExpTaxAmt");// 应缴税额
				String discounttaxamt = (String) taxsubjectlist1001map
						.get("DiscountTaxAmt");// 扣除额
				String facttaxamt = (String) taxsubjectlist1001map
						.get("FactTaxAmt");// 实缴税额

				// 组织DTo准备保存数据******************************************************
				// 实时扣税税目信息 dto
				TvTaxItemDto tvtaxitemdto = new TvTaxItemDto();

				tvtaxitemdto.setSseq(bizseq);// 业务流水号
				// tvtaxitemdto.setStrano(trano);// 交易流水号
				tvtaxitemdto.setIprojectid(Integer.valueOf(projectid));// 项目序号
				// tvtaxitemdto.setSentrustdate(entrustdate);// 委托日期
				// tvtaxitemdto.setStaxorgcode(taxorgcode);// 征收机关代码
				tvtaxitemdto.setIdetailno(Integer.valueOf(detailno));// 明细序号
				tvtaxitemdto.setStaxsubjectcode(taxsubjectcode);// 税目代码
				tvtaxitemdto.setStaxsubjectname(taxsubjectname);// 税目名称
				tvtaxitemdto.setItaxnumber(Integer.valueOf(taxnumber));// 课税数量
				tvtaxitemdto.setFtaxamt(MtoCodeTrans
						.transformBigDecimal(taxamt));// 计税金额
				tvtaxitemdto.setFtaxrate(MtoCodeTrans
						.transformBigDecimal(taxrate));// 税率
				tvtaxitemdto.setFexptaxamt(MtoCodeTrans
						.transformBigDecimal(exptaxamt));// 应缴税额
				tvtaxitemdto.setFdiscounttaxamt(MtoCodeTrans
						.transformBigDecimal(discounttaxamt));// 扣除额
				tvtaxitemdto.setFrealamt(MtoCodeTrans
						.transformBigDecimal(facttaxamt));// 实缴税额
				tvtaxitemdto.setTsupdate(new Timestamp(new java.util.Date()
						.getTime()));// 更新时间

				// 保存实时扣税税目信息数据 ,可能有是空的情况
				if (null != tvtaxitemdto) {
					try {
						// 保存数据
						DatabaseFacade.getDb().create(tvtaxitemdto);
					} catch (JAFDatabaseException e) {
						logger.error(e);
						throw new ITFEBizException("数据库出错", e);
					}
				}
			}
		}
		/*
		 * 接收/发送日志
		 */
		String recvseqno;// 接收日志流水号
		String sendseqno;// 发送日志流水号
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
			sendseqno = StampFacade.getStampSendSeq("FS"); // 取发送日志流水
			// 根据国库查询核算主体
			TsTreasuryDto _dto = SrvCacheFacade.cacheTreasuryInfo(null).get(payeeorgcode);
			if (null != _dto) {
				sbookorgcode = _dto.getSorgcode();
			}
			
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("取接收日志SEQ出错");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("根据国库代码取核算主体代码出错!");
		}

		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode, entrustdate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), Integer
						.valueOf(taxtypenum), MtoCodeTrans
						.transformBigDecimal(traamt), trano, null, payeebankno,
				taxorgcode, null, (String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_RECV);

		// 写发送日志
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, sbookorgcode, (String) headMap.get("DES"), entrustdate,
				(String) headMap.get("MsgNo"), (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), Integer
						.valueOf(taxtypenum), MtoCodeTrans
						.transformBigDecimal(traamt), trano, payeeorgcode,
				payeebankno, taxorgcode, null, (String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND);
		
		// 记录接收财政的消息记录--广西MQ消息ID匹配机制修改
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendorgcode, orgcode, msgNo, msgid, TimeFacade.getCurrentStringTime(), trano, jmsMessageID, jmsCorrelationID, taxorgcode);
		// 获得原报文，重新发送
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

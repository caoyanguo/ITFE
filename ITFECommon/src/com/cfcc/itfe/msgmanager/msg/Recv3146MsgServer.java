package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
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
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.TvPbcpaySubDto;
//import com.cfcc.itfe.persistence.dto.TvGrantpayplanMainDto;
//import com.cfcc.itfe.persistence.dto.TvPbcgrantpayMainDto;
//import com.cfcc.itfe.persistence.dto.TvPbcgrantpaySubDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 接收、解析、发送人民银行办理支付退款通知(3146) 主要功能：接受、解析、发送3146报文
 * 
 * @author zhangxh
 * 
 */
public class Recv3146MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3146MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {

			/*
			 * 第1步 解析报文头信息，记录接收日志
			 */
			// 报文的处理方式 1 手工 2 MQ
			String bankInput = (String) eventContext.getMessage().getProperty(
					"BANK_INPUT");
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			HashMap msgMap = (HashMap) cfxMap.get("MSG");

			// 报文头信息CFX->HEAD
			String sorgcode = (String) headMap.get("SRC"); // 发起机构代码
			String sdescode = (String) headMap.get("DES");// 接收节点代码
			String MsgNo = (String) headMap.get("MsgNo");// 报文编号
			String MsgID = (String) headMap.get("MsgID");// 报文标识号
			String MsgRef = (String) headMap.get("MsgRef");// 报文参考号
			String WorkDate = (String) headMap.get("WorkDate");// 工作日期

			// 解析实时业务头 CFX->MSG->BatchHead3146
			HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3146");

			String trreCode = (String) batchheadMap.get("TreCode"); // 国库主体代码
			String billOrg = (String) batchheadMap.get("BillOrg"); // 出票单位
			String entrustDate = (String) batchheadMap.get("EntrustDate"); // 委托日期
			String packNo = (String) batchheadMap.get("PackNo"); // 包流水号
			int allNum = Integer.valueOf((String) batchheadMap.get("AllNum")); // 总笔数
			BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(batchheadMap
					.get("AllAmt")); // 总金额
			String PayMode = (String) batchheadMap.get("PayMode"); // 支付方式
			String sbookorgcode = sdescode;
			/*
			 * 第2步 解析报文体信息，记录额度、明细数据
			 */
			// 额度信息CFX->MSG ->Bill3146
			List Bill3146s = (List) msgMap.get("Bill3146");
			for (int i = 0; i < Bill3146s.size(); i++) {
				HashMap Bill3146 = (HashMap) Bill3146s.get(i);
				String VouNo = (String) Bill3146.get("VouNo"); // 凭证编号
				String VouDate = (String) Bill3146.get("VouDate"); // 凭证日期
				String OriTraNo = (String) Bill3146.get("OriTraNo"); // 原交易流水号
				String OriEntrustDate = (String) Bill3146.get("OriEntrustDate"); // 原委托日期
				String OriVouNo = (String) Bill3146.get("OriVouNo"); // 原支出凭证编号
				String OriVouDate = (String) Bill3146.get("OriVouDate"); // 原支出凭证日期
				String PayerAcct = (String) Bill3146.get("PayerAcct"); // 付款人账号
				String PayerName = (String) Bill3146.get("PayerName"); // 付款人名称
				String PayerAddr = (String) Bill3146.get("PayerAddr"); // 付款人地址
				BigDecimal Amt = MtoCodeTrans.transformBigDecimal(Bill3146.get("Amt")); // 金额
				String PayeeBankNo = (String) Bill3146.get("PayeeBankNo"); // 收款行行号
				String PayeeOpBkNo = (String) Bill3146.get("PayeeOpBkNo"); // 收款人开户行行号
				String PayeeAcct = (String) Bill3146.get("PayeeAcct"); // 收款人账号
				String PayeeName = (String) Bill3146.get("PayeeName"); // 收款人名称
				String AddWord = (String) Bill3146.get("AddWord"); // 附言
				String OfYear = (String) Bill3146.get("OfYear"); // 所属年度
				String BudgetType = (String) Bill3146.get("BudgetType"); // 预算种类
				String TrimSign = (String) Bill3146.get("TrimSign"); // 整理期标志
				String BckReason = (String) Bill3146.get("BckReason"); // 退回原因
				int StatInfNum = Integer.valueOf((String) Bill3146
						.get("StatInfNum")); // 统计信息条数

				String seq = StampFacade.getBizSeq("RBTH"); // 业务流水号
				TsTreasuryDto findto = SrvCacheFacade.cacheTreasuryInfo(null).get(trreCode);
				if (null!=findto) {
					sbookorgcode =findto.getSorgcode();
				}

				// 组织DTO准备保存额度信息--------------------------
				// 人行通过支付方式办理的授权支付主体信息与退回信息 dto
				TvPbcpayMainDto tvpbcgrantpaymaindto = new TvPbcpayMainDto();
				tvpbcgrantpaymaindto.setIvousrlno((new Long(OriTraNo))*(-10)); // 业务流水号
				tvpbcgrantpaymaindto.setStrano(OriTraNo);// 交易流水号
				tvpbcgrantpaymaindto.setSorgcode(sbookorgcode); // 发起机构代码 
				tvpbcgrantpaymaindto.setStrecode(trreCode);// 国库主体代码
				tvpbcgrantpaymaindto.setSbillorg(billOrg);// 出票单位
				tvpbcgrantpaymaindto.setSentrustdate(entrustDate);// 委托日期
				tvpbcgrantpaymaindto.setSpackno(packNo);// 包流水号
				tvpbcgrantpaymaindto.setSpayoutvoutypeno("0");// 支出凭证类型
				tvpbcgrantpaymaindto.setSpaymode(PayMode);// 支付方式
				tvpbcgrantpaymaindto.setSvouno(VouNo);// 凭证编号
				tvpbcgrantpaymaindto.setDvoucher(VouDate);// 凭证日期
				tvpbcgrantpaymaindto.setSpayeracct(PayerAcct);// 付款人账号
				tvpbcgrantpaymaindto.setSpayername(PayerName);// 付款人名称
				tvpbcgrantpaymaindto.setSpayeraddr(PayerAddr);// 付款人地址
				tvpbcgrantpaymaindto.setSpayeeacct(PayeeAcct);// 收款人账号
				tvpbcgrantpaymaindto.setSpayeename(PayeeName);// 收款人名称
				tvpbcgrantpaymaindto.setSpayeeaddr("");// 收款人地址
				tvpbcgrantpaymaindto.setSrcvbnkno(PayeeBankNo);// 接收行行号
				tvpbcgrantpaymaindto.setSpayeeopnbnkno(PayeeOpBkNo);// 收款开户行行号
				tvpbcgrantpaymaindto.setSaddword(AddWord);// 附言
				tvpbcgrantpaymaindto.setCbdgkind(BudgetType);// 预算种类
				tvpbcgrantpaymaindto.setIofyear(Integer.parseInt(OfYear));// 所属年度
				tvpbcgrantpaymaindto.setSbdgadmtype(" ");// 预算管理类型
				tvpbcgrantpaymaindto.setFamt(Amt);// 金额
				tvpbcgrantpaymaindto.setStrastate("");// 处理结果
				tvpbcgrantpaymaindto.setDacct("");// TCBS账务日期
				tvpbcgrantpaymaindto.setCtrimflag(TrimSign);// 调整期标志
				tvpbcgrantpaymaindto.setIdetailnio(StatInfNum);// 明细记录数
				tvpbcgrantpaymaindto.setIchgnum(1);// 修改次数
				tvpbcgrantpaymaindto.setSinputerid("sysadmin");// 录入员代码
				tvpbcgrantpaymaindto.setTssysupdate(new Timestamp(
						new java.util.Date().getTime()));// 系统更新时间

				tvpbcgrantpaymaindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_BACK); // 交易状态
				tvpbcgrantpaymaindto.setSbackflag(StateConstant.COMMON_YES);// 退回标志
				tvpbcgrantpaymaindto.setSdescription(BckReason);// 处理结果说明--退回原因
				tvpbcgrantpaymaindto.setDorientrustdate(OriEntrustDate);// 原委托日期
				tvpbcgrantpaymaindto.setSoritrano(OriTraNo);// 原交易流水号
				tvpbcgrantpaymaindto.setSorivouno(OriVouNo);// 原凭证编号
				tvpbcgrantpaymaindto.setDorivoudate(OriVouDate);// 原凭证日期

				//--20101025 新增更新退库标志---------------
				upBackFlag(tvpbcgrantpaymaindto);
				
				// 保存数据
				DatabaseFacade.getDb().create(tvpbcgrantpaymaindto);

				// 明显信息 CFX->MSG->Bill3146->Detail3146
				List Detail3146s = (List) Bill3146.get("Detail3146");
				for (int j = 0; j < Detail3146s.size(); j++) {
					HashMap Detail3146 = (HashMap) Detail3146s.get(j);
					String SeqNo = (String) Detail3146.get("SeqNo"); // 序号
					String BdgOrgCode = (String) Detail3146.get("BdgOrgCode"); // 预算单位代码
					String FuncBdgSbtCode = (String) Detail3146
							.get("FuncBdgSbtCode"); // 功能类科目代码
					String EcnomicSubjectCode = (String) Detail3146
							.get("EcnomicSubjectCode"); // 经济类科目代码
					BigDecimal Amt1 = MtoCodeTrans
							.transformBigDecimal(Detail3146.get("Amt")); // 发生额
					String AcctProp = (String) Detail3146.get("AcctProp"); // 账户性质

					// 组织DTO准备保存明细信息--------------------------
					// 人行通过支付方式办理的授权支付子信息dto
					TvPbcpaySubDto tvpbcgrantpaysubdto = new TvPbcpaySubDto();
					tvpbcgrantpaysubdto.setIvousrlno((new Long(OriTraNo))*(-10)); // 业务流水号
					tvpbcgrantpaysubdto.setIseqno(Integer.parseInt(SeqNo)); // 组内序号
					tvpbcgrantpaysubdto.setSbdgorgcode(BdgOrgCode);// 预算单位代码
					tvpbcgrantpaysubdto.setSfuncsbtcode(FuncBdgSbtCode); // 功能类科目代码
					tvpbcgrantpaysubdto.setSecosbtcode(EcnomicSubjectCode); // 经济类科目代码
					tvpbcgrantpaysubdto.setFamt(Amt1); // 金额
					tvpbcgrantpaysubdto.setCacctprop(AcctProp); // 账户性质
					// 保存数据
					DatabaseFacade.getDb().create(tvpbcgrantpaysubdto);
				}
			}

			/*
			 * 第3步 保存发送日志，发送报文
			 */
			// 记发送日志
			String _srecvno = StampFacade.getStampSendSeq("JS");
			String filepath = (String) eventContext.getMessage().getProperty(
			"XML_MSG_FILE_PATH");
	        String stamp = TimeFacade.getCurrentStringTime();
	       String ifsend = (String) eventContext.getMessage().getProperty(
			MessagePropertyKeys.MSG_SENDER);
			MsgLogFacade.writeRcvLog(_srecvno, _srecvno, sbookorgcode, entrustDate, MsgNo,
					sorgcode, filepath, allNum, allAmt, packNo, trreCode, null,
					billOrg, null, MsgID,
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, "人行办理支付退回", null,
					null, MsgConstant.ITFE_SEND, "人行办理支付退回" );

		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("数据库出错", e);
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("确定sequence信息出错", e);
		}
	}
	
	/**
	 * 更新退库标志
	 * @param dto
	 * @throws ITFEBizException
	 */
	private void upBackFlag(TvPbcpayMainDto dto) throws ITFEBizException{
		
		SQLExecutor updateExce = null;
		try {
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updateSql = "update "
				+ dto.tableName()
				+ " set s_BackFlag = ? ,s_Description=? "
				+ " where  s_VouNo = ? and d_Voucher=? ";
			updateExce.addParam(dto.getSbackflag());
			updateExce.addParam(dto.getSdescription());
			updateExce.addParam(dto.getSorivouno());
			updateExce.addParam(dto.getDorivoudate());
			
			if(null !=dto.getDorientrustdate() && !"".endsWith(dto.getDorientrustdate())){
				updateSql+=" and s_EntrustDate = ? ";
				updateExce.addParam(dto.getDorientrustdate());
			}
			if(null !=dto.getSoritrano() && !"".endsWith(dto.getSoritrano())){
				updateSql+=" and S_TRANO = ? ";
				updateExce.addParam(dto.getSoritrano());
			}
			updateExce.runQuery(updateSql);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("更新3146退库数据出错", e);
		} finally {
			if (null != updateExce) {
				updateExce.closeConnection();
			}
		}
	}
}

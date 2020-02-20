package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgSubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.BizTableUtil;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 接收、解析、发送财政的实拨资金退款通知(3145) 主要功能：接受、解析、发送3145报文
 * 
 * @author zhangxh
 * 
 */
public class Recv3145MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3145MsgServer.class);

	/**
	 * 报文信息处理
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		SQLExecutor updateExce = null;
		try {
			
			 // 第1步 解析报文头信息，记录接收日志
			String filename = (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"); // 文件名称 
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			HashMap msgMap = (HashMap) cfxMap.get("MSG");

			// 报文头信息CFX->HEAD
			String sorgcode = (String) headMap.get("SRC"); // 发起机构代码
			String sdescode = (String) headMap.get("DES");// 接收节点代码
			String MsgNo = (String) headMap.get("MsgNo");// 报文编号
			String MsgID = (String) headMap.get("MsgID");// 报文标识号
//			String MsgRef = (String) headMap.get("MsgRef");// 报文参考号
//			String WorkDate = (String) headMap.get("WorkDate");// 工作日期

			// 解析实时业务头 CFX->MSG->BatchHead3145
			HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3145");
			String sbookorgcode = "000000000000";
			String billOrg = (String) batchheadMap.get("BillOrg"); // 出票单位
			String trreCode = (String) batchheadMap.get("TreCode"); // 国库主体代码
			String entrustDate = (String) batchheadMap.get("EntrustDate"); // 委托日期
			String packNo = (String) batchheadMap.get("PackNo"); // 包流水号
			int allNum = Integer.valueOf((String) batchheadMap.get("AllNum")); // 总笔数
			BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("AllAmt")); // 总金额
			String payoutVouType = (String) batchheadMap.get("PayoutVouType"); // 支出凭证类型
			
			String orgcode = BizTableUtil.getOrgcodeByTrecode(trreCode);
			Map<String,TdCorpDto> rpmap = SrvCacheFacade.cacheTdCorpInfo(orgcode); //法人代码缓存
			HashMap<String, TsBudgetsubjectDto> budgetsubjectmap = SrvCacheFacade.cacheTsBdgsbtInfo(orgcode); //科目代码缓存

			
			// 第2步 解析报文体信息，记录明细数据
			 
			// 额度信息 CFX->MSG->Bill3145
			List Bill3145s = (List) msgMap.get("Bill3145");
			for (int i = 0; i < Bill3145s.size(); i++) {

				HashMap Bill3145 = (HashMap) Bill3145s.get(i);
				String VouNo = (String) Bill3145.get("VouNo"); // 凭证编号
				String VouDate = (String) Bill3145.get("VouDate"); // 凭证日期
				String oriTraNo = (String) Bill3145.get("OriTraNo");//原交易流水号
				String oriEntrustDate = (String) Bill3145.get("OriEntrustDate");//原委托日期
				String OriVouNo = (String) Bill3145.get("OriVouNo"); // 原支出凭证编号
				String OriVouDate = (String) Bill3145.get("OriVouDate"); // 原支出凭证日期
				String PayerAcct = (String) Bill3145.get("PayerAcct"); // 付款人账号
				String PayerName = (String) Bill3145.get("PayerName"); // 付款人名称
				String PayerAddr = (String) Bill3145.get("PayerAddr"); // 付款人地址
				BigDecimal Amt = MtoCodeTrans.transformBigDecimal(Bill3145.get("Amt")); // 合计金额
				String PayeeOpBkNo = (String) Bill3145.get("PayeeOpBkNo"); // 收款人开户行行号
				String PayeeBankNo = (String) Bill3145.get("PayeeBankNo"); // 收款行行号
				String PayeeAcct = (String) Bill3145.get("PayeeAcct"); // 收款人账号
				String PayeeName = (String) Bill3145.get("PayeeName"); // 收款人名称
				String AddWord = (String) Bill3145.get("AddWord"); // 附言
				String BdgOrgCode = (String) Bill3145.get("BdgOrgCode"); // 预算单位代码
//				String BudgetOrgName = (String) Bill3145.get("BudgetOrgName"); // 预算单位名称
				String OfYear = (String) Bill3145.get("OfYear"); // 所属年度
				String BudgetType = (String) Bill3145.get("BudgetType"); // 预算种类
				String TrimSign = (String) Bill3145.get("TrimSign"); // 整理期标志
				String BckReason = (String) Bill3145.get("BckReason"); // 退回原因
				//int StatInfNum = Integer.valueOf((String) Bill3145.get("StatInfNum")); // 统计信息条数
				
				String seq = StampFacade.getBizSeq("SBTH"); // 业务流水号

				// 组织DTO准备保存额度信息--------------------------
				// 实拨资金无（有）纸凭证请求主体信息 dto
				TvPayoutbackmsgMainDto tvpayoutmsgmaindto = new TvPayoutbackmsgMainDto();
				tvpayoutmsgmaindto.setSbizno(seq); // 业务流水号
				sbookorgcode = BizTableUtil.getOrgcodeByTrecode(trreCode);
				tvpayoutmsgmaindto.setSorgcode(sbookorgcode); // 发起机构代码 
				tvpayoutmsgmaindto.setScommitdate(entrustDate);// 委托日期
				tvpayoutmsgmaindto.setSaccdate(VouDate);// TCBS账务日期
				tvpayoutmsgmaindto.setSfilename(filename); // 导入文件名
				tvpayoutmsgmaindto.setStrecode(trreCode);// 国库主体代码
				tvpayoutmsgmaindto.setSpackageno(packNo);// 包流水号
				tvpayoutmsgmaindto.setSpayunit(billOrg);// 出票单位
				tvpayoutmsgmaindto.setSvouno(VouNo);// 凭证编号
				tvpayoutmsgmaindto.setSvoudate(VouDate);// 凭证日期
				tvpayoutmsgmaindto.setSoritrano(oriTraNo); // 原交易流水号
				tvpayoutmsgmaindto.setSorientrustdate(oriEntrustDate);
				tvpayoutmsgmaindto.setSorivouno(OriVouNo);// 原凭证编号
				tvpayoutmsgmaindto.setSorivoudate(OriVouDate);// 原凭证日期

				tvpayoutmsgmaindto.setSpayeracct(PayerAcct);// 付款人账号
				tvpayoutmsgmaindto.setSpayername(PayerName);// 付款人名称
				tvpayoutmsgmaindto.setSpayeraddr(PayerAddr);// 付款人地址
				tvpayoutmsgmaindto.setNmoney(Amt);// 金额
				tvpayoutmsgmaindto.setSpayeeopbkno(PayeeOpBkNo);// 收款开户行行号
				tvpayoutmsgmaindto.setSpayeebankno(PayeeBankNo);//收款行行号
				tvpayoutmsgmaindto.setSpayeeacct(PayeeAcct);// 收款人账号
				tvpayoutmsgmaindto.setSpayeename(PayeeName);// 收款人名称
				tvpayoutmsgmaindto.setStrimflag(TrimSign);// 调整期标志
				tvpayoutmsgmaindto.setSbudgetunitcode(BdgOrgCode==null?"":BdgOrgCode); // 预算单位代码
				if(rpmap!=null && rpmap.get(BdgOrgCode)!=null){
					tvpayoutmsgmaindto.setSunitcodename(rpmap.get(BdgOrgCode).getScorpname()); // 预算单位名称
				}else{
					tvpayoutmsgmaindto.setSunitcodename(""); // 预算单位名称
				}
				tvpayoutmsgmaindto.setSofyear(OfYear);// 所属年度
				tvpayoutmsgmaindto.setSbudgettype(BudgetType);// 预算种类
				tvpayoutmsgmaindto.setSusercode("sysadmin");// 录入员代码
				tvpayoutmsgmaindto.setSaddword(AddWord); // 附言
				tvpayoutmsgmaindto.setSdemo(BckReason); // 备注
				tvpayoutmsgmaindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS); // 交易状态
				tvpayoutmsgmaindto.setSbackflag(StateConstant.MSG_BACK_FLAG_YES);// 退回标志
				tvpayoutmsgmaindto.setSisreturn(StateConstant.COMMON_NO);//是否已经生成凭证
				tvpayoutmsgmaindto.setSbckreason(BckReason);// 退回原因
				
				
				//用于存储字表信息
				List<IDto> subList = new ArrayList<IDto>();
				// 明显信息 CFX->MSG->Bill3145->Detail3145
				List Detail3145s = (List) Bill3145.get("Detail3145");
				for (int j = 0; j < Detail3145s.size(); j++) {
					HashMap Detail3145 = (HashMap) Detail3145s.get(j);
					String SeqNo = (String) Detail3145.get("SeqNo"); // 序号
					String FuncBdgSbtCode = (String) Detail3145.get("FuncBdgSbtCode"); // 功能类科目代码
					String EcnomicSubjectCode = (String) Detail3145.get("EcnomicSubjectCode"); // 经济类科目代码
					String BudgetPrjCode = (String) Detail3145.get("BudgetPrjCode"); // 预算项目代码
					BigDecimal Amt1 = MtoCodeTrans.transformBigDecimal(Detail3145.get("Amt")); // 发生额

					// 组织DTO准备保存明细信息--------------------------
					// 实拨资金无（有）纸凭证请求子信息dto
					TvPayoutbackmsgSubDto tvpayoutmsgsubdto = new TvPayoutbackmsgSubDto();
					tvpayoutmsgsubdto.setSbizno(seq); // 业务流水号
					tvpayoutmsgsubdto.setSseqno(Integer.parseInt(SeqNo)); // 明细序号
					tvpayoutmsgsubdto.setSecnomicsubjectcode(EcnomicSubjectCode); // 经济类科目代码
					tvpayoutmsgsubdto.setSbudgetprjcode(BudgetPrjCode); // 预算项目代码
					tvpayoutmsgsubdto.setNmoney(Amt1); // 金额
					tvpayoutmsgsubdto.setSfunsubjectcode(FuncBdgSbtCode); // 功能科目代码
					if(budgetsubjectmap != null && budgetsubjectmap.get(FuncBdgSbtCode)!=null){
						if(budgetsubjectmap.get(FuncBdgSbtCode).getSsubjectname().length() >= 15)
						{
							tvpayoutmsgsubdto.setSfunsubjectname(budgetsubjectmap.get(FuncBdgSbtCode).getSsubjectname().substring(0, 15));
						}else{
							tvpayoutmsgsubdto.setSfunsubjectname(budgetsubjectmap.get(FuncBdgSbtCode).getSsubjectname());							
						}
					}
					subList.add(tvpayoutmsgsubdto);
				}
				
				/**
				 * 根据原凭证的存在与否来更新2252原凭证主表和索引表状态
				 * @author 张会斌
				 */
				TfPaybankRefundmainDto tfPaybankRefundmainDto = new TfPaybankRefundmainDto();
				tfPaybankRefundmainDto.setSorgcode(orgcode);
				tfPaybankRefundmainDto.setStrecode(trreCode);
				tfPaybankRefundmainDto.setSoriginalvoucherno(OriVouNo);
				tfPaybankRefundmainDto.setSpaytypecode(StateConstant.PAYOUT_PAY_CODE);
				List<TfPaybankRefundmainDto> paymentDetailDtoList = CommonFacade.getODB().findRsByDto(tfPaybankRefundmainDto);
				if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&null!=paymentDetailDtoList&&paymentDetailDtoList.size()>0){
					//用预留字段S_HOLD4设置批量标志，用于区分单笔、批量退款
					tvpayoutmsgmaindto.setShold4(StateConstant.BIZTYPE_CODE_BATCH);
					
					updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
					
					//更新主表
					String updateSql = "update " + TfPaybankRefundmainDto.tableName() + " set S_STATUS = ? , S_DEMO = ? ,N_PAYAMT = ? "
							+ " where S_ORGCODE = ? and S_TRECODE = ? and S_ORIGINALVOUCHERNO = ? and S_PAYTYPECODE = ? ";
					
					updateExce.clearParams();
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);//状态
					updateExce.addParam(BckReason);//退回原因
					updateExce.addParam(Amt);//退回金额
					updateExce.addParam(orgcode);//核算主体代码
					updateExce.addParam(trreCode);//国库代码
					updateExce.addParam(OriVouNo);//凭证编号
					updateExce.addParam(StateConstant.PAYOUT_PAY_CODE);//支付方式编码
					updateExce.runQuery(updateSql);
					
					//更新子表(用于实拨资金退款的数据源)
					String updateSubSql = "update " + TfPaybankRefundsubDto.tableName() + " set S_HOLD1 = ? , S_HOLD2 = ? ,S_HOLD3 = ? ,S_HOLD4 = ?,S_EXT1 = ? ,S_EXT2 = ? ,S_EXT3 = ? "
					+ " where I_VOUSRLNO = ? ";
			
					updateExce.clearParams();
					updateExce.addParam(orgcode);//核算主体代码
					updateExce.addParam(trreCode);//国库代码
					updateExce.addParam(OriVouNo);//原凭证编号
					updateExce.addParam(OriVouDate);//原凭证日期
					updateExce.addParam(StateConstant.COMMON_NO);//是否已经生成凭证
					updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);//交易状态
					updateExce.addParam(StateConstant.MSG_BACK_FLAG_YES);// 退款标志
					updateExce.addParam(paymentDetailDtoList.get(0).getIvousrlno());//凭证流水号
					updateExce.runQuery(updateSubSql);
					
					
					//更新索引表
					String updateIndexSql = "update " + TvVoucherinfoDto.tableName() + " set S_STATUS = ? , S_DEMO = ? "
					+ " where S_ORGCODE = ? and S_TRECODE = ? and S_VTCODE = ?  and S_VOUCHERNO = ? and S_STATUS = ? ";
					
					updateExce.addParam(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					updateExce.addParam("处理成功");
					updateExce.addParam(orgcode);
					updateExce.addParam(trreCode);
					updateExce.addParam(MsgConstant.VOUCHER_NO_2252);
					updateExce.addParam(paymentDetailDtoList.get(0).getSvoucherno());
					updateExce.addParam(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);//校验成功，因为2252不发送TIPS
					updateExce.runQueryCloseCon(updateIndexSql);
				}
				TvPayoutbackmsgMainDto findto = new TvPayoutbackmsgMainDto();
				findto.setSvouno(tvpayoutmsgmaindto.getSvouno());
				findto.setStrecode(tvpayoutmsgmaindto.getStrecode());
				List findlist = CommonFacade.getODB().findRsByDto(findto);
				if(findlist==null||findlist.size()<=0||ITFECommonConstant.PUBLICPARAM.contains(",3145insert=more,"))
				{
					// 保存主表数据
					DatabaseFacade.getDb().create(tvpayoutmsgmaindto);
					// 保存字表数据
					DatabaseFacade.getDb().create(CommonUtil.listTArray(subList));
				}
			}
			
			// 记录接收日志
			// 组织DTO准备保存日志信息--------------------------
			String recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
			String sendseqno = StampFacade.getStampSendSeq("FS"); // 取发送日志流水
			MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode,
					entrustDate, MsgNo, sorgcode, (String) eventContext
							.getMessage().getProperty("XML_MSG_FILE_PATH"),
					allNum, allAmt, packNo, trreCode, "", billOrg,
					payoutVouType, MsgID,
					DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
					(String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null, null);

			
			// 第3步 保存发送日志，发送报文
			 
			// 记发送日志
			// 组织DTo准备保存数据******************************************************
			MsgLogFacade.writeSendLog(sendseqno, recvseqno, sbookorgcode, sdescode,
					entrustDate, MsgNo, (String) eventContext.getMessage()
							.getProperty("XML_MSG_FILE_PATH"), allNum, allAmt,
					packNo, trreCode, "", billOrg, payoutVouType, MsgID,
					DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
					(String) eventContext.getMessage().getProperty(
							MessagePropertyKeys.MSG_SENDER), null, null);

			// 取得报文信息直接转发报文
			String xmlstr = (String) eventContext.getMessage().getProperty("MSG_INFO");
			eventContext.getMessage().setPayload(xmlstr);

		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("数据库出错", e);
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("确定sequence信息出错", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询原凭证时出错", e);
		}finally{
			if(updateExce != null){
				updateExce.closeConnection();
			}
		}
	}
}

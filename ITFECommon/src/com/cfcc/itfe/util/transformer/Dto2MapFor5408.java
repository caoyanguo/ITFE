package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvNontaxsubDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
public class Dto2MapFor5408 {
	
	private static Log logger = LogFactory.getLog(Dto2MapFor5408.class);
	
	/**
	 * DTO转化XML报文(非税收入)Tips旧版本
	 * 
	 * @param List
	 *            <TvInfileDto> list 发送报文对象集合
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            packno 包流水号
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public static Map tranfor(List<TvNontaxmainDto> list,String orgcode,String filename,String packno ,boolean isRepeat) throws ITFEBizException{
		if(null == list || list.size() == 0){
			throw new ITFEBizException("要转化的对象为空,请确认!");
		}
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();
		
		// 设置报文节点 CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);

		// 报文头 HEAD
		headMap.put("VER", MsgConstant.MSG_HEAD_VER);
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo",MsgConstant.MSG_NO_7211);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("取交易流水号时出现异常！", e);
			throw new ITFEBizException("取交易流水号时出现异常！", e);
		}

		/** 
		 * 设置报文消息体 MSG
		 * 由一个报文头、一个批量头、一个转账信息段、多个基本信息段(包含一个付款信息段、一个税票信息段、一个税种信息段(包含零到多个税目信息段))构成
		 */
		//1.1　批量头
		HashMap<String, String> batchHeadMap = new HashMap<String, String>();
		batchHeadMap.put("TaxOrgCode", list.get(0).getStaxorgcode());// 征收机关代码
		batchHeadMap.put("EntrustDate", list.get(0).getScommitdate().replaceAll("-", "")); // 委托日期
		batchHeadMap.put("PackNo", list.get(0).getSpackageno()); // 包流水号
		batchHeadMap.put("AllNum", String.valueOf(list.size())); // 总笔数
		BigDecimal allamt = new BigDecimal("0.00"); // 总金额
//		batchHeadMap.put("AllAmt", "0.00");

		// 1.2　转账信息
		HashMap<String, String> turnAccountMap = new HashMap<String, String>();
		turnAccountMap.put("BizType", MsgConstant.MSG_BIZ_MODEL_NET); // 业务模式[地方横向联网业务]
		turnAccountMap.put("FundSrlNo", MtoCodeTrans.transformString(list.get(0).getShold1())); // 资金收纳流水号
		turnAccountMap.put("PayBnkNo", MtoCodeTrans.transformString(list.get(0).getSpaybankno())); // 付款行行号
		turnAccountMap.put("PayeeTreCode", list.get(0).getStrecode()); // 收款国库代码
//		turnAccountMap.put("PayeeTreName", MtoCodeTrans.transformString(null)); // 收款国库名称
		
		// 1.3　报文体(多个基本信息段)
		HashMap<String, Object> taxBody7211Map = new HashMap<String, Object>();
		
		List<Object> taxInfo7211List = new ArrayList<Object>();
		TvNontaxsubDto subdto = null;
		TvNontaxmainDto maindto = null;
		for (int i = 0; i < list.size(); i++) {
			maindto = list.get(i);
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(list.get(i));
			allamt = allamt.add(list.get(i).getNmoney()); // 计算汇总金额
			for(int j=0;j<sublist.size();j++)
			{
				HashMap<String, Object> taxInfo7211Map = new HashMap<String, Object>();// 1.3.1.1　报文体(基本信息段)- 一个付款信息段
				subdto = (TvNontaxsubDto)sublist.get(j);
				HashMap<String, String> payment7211Map = new HashMap<String, String>();
				if (list.get(i).getSid().length()<8) {
					String transno = "10000000"+list.get(i).getSid();
					payment7211Map.put("TraNo", transno); // 交易流水号
				}else{
				    payment7211Map.put("TraNo", list.get(i).getSid()); // 交易流水号
				}
				payment7211Map.put("TraAmt", MtoCodeTrans.transformString(subdto.getNtraamt())); //交易金额
				payment7211Map.put("PayOpBnkNo", MtoCodeTrans.transformString(maindto.getSpaybankno())); //付款开户行行号
				payment7211Map.put("PayOpBnkName", MtoCodeTrans.transformString(maindto.getSpayacctbankname())); // 付款开户行名称
				if(null == subdto.getShold2() || "".equals(subdto.getShold2().trim())){
					payment7211Map.put("HandOrgName", "dfhl"); // 缴款单位名称
				}else{
					payment7211Map.put("HandOrgName", subdto.getShold2()); // 缴款单位名称
				}
				
				payment7211Map.put("PayAcct", MtoCodeTrans.transformString(maindto.getSpayacctno())); // 付款账户

				// 1.3.1.2　报文体(基本信息段)-一个税票信息段
				HashMap<String, String> taxVou7211Map = new HashMap<String, String>();
				taxVou7211Map.put("TaxVouNo", MtoCodeTrans.transformString(subdto.getShold1())); // 税票号码
				taxVou7211Map.put("BillDate", MtoCodeTrans.transformString(maindto.getSvoudate())); // 开票日期
				if(null == subdto.getShold1() || "".equals(subdto.getShold1().trim())){
					taxVou7211Map.put("TaxPayCode", "10101"); // 纳税人编码
				}else{
					taxVou7211Map.put("TaxPayCode", MtoCodeTrans.transformString(subdto.getShold1())); // 纳税人编码
				}
				if(null == subdto.getShold2() || "".equals(subdto.getShold2().trim())){
					taxVou7211Map.put("TaxPayName", "dfhl"); // 纳税人名称
				}else{
					taxVou7211Map.put("TaxPayName", MtoCodeTrans.transformString(subdto.getShold2())); // 纳税人名称
				}

				taxVou7211Map.put("BudgetType", MtoCodeTrans.transformString(maindto.getSbudgettype())); // 预算种类
				taxVou7211Map.put("TrimSign", MtoCodeTrans.transformString(maindto.getStrimflag())); // 整理期标志
				taxVou7211Map.put("CorpCode",  MtoCodeTrans.transformString(maindto.getStaxpaycode())); // 企业代码
				taxVou7211Map.put("CorpName", MtoCodeTrans.transformString(maindto.getStaxpayname())); // 企业名称

				// 1.3.1.3　报文体(基本信息段)-一个税种信息段
				HashMap<String, Object> taxType7211Map = new HashMap<String, Object>();
				taxType7211Map.put("BudgetSubjectCode", MtoCodeTrans.transformString(subdto.getSitemcode())); // 预算科目代码
				taxType7211Map.put("BudgetSubjectName", MtoCodeTrans.transformString(subdto.getSitemname())); // 预算科目名称
				
				if(null == maindto.getSexpdate() || "".equals(maindto.getSexpdate().trim())){
					taxType7211Map.put("LimitDate", maindto.getSvoudate().replaceAll("-", "")); // 限缴日期
				}else{
					taxType7211Map.put("LimitDate", maindto.getSexpdate().replaceAll("-", "")); // 限缴日期
				}
				
				taxType7211Map.put("TaxTypeCode", MtoCodeTrans.transformString(maindto.getStaxtypecode())); // 税种代码
				taxType7211Map.put("TaxTypeName", MtoCodeTrans.transformString(maindto.getStaxtypename())); // 税种名称
				taxType7211Map.put("BudgetLevelCode", MtoCodeTrans.transformString(subdto.getSxaddword())); // 预算级次代码
				if(subdto.getSxaddword()!=null&&"0".equals(subdto.getSxaddword()))
					taxType7211Map.put("BudgetLevelName", "共享"); // 预算级次名称
				else if(subdto.getSxaddword()!=null&&"1".equals(subdto.getSxaddword()))
					taxType7211Map.put("BudgetLevelName", "中央"); // 预算级次名称
				else if(subdto.getSxaddword()!=null&&"2".equals(subdto.getSxaddword()))
					taxType7211Map.put("BudgetLevelName", "省"); // 预算级次名称
				else if(subdto.getSxaddword()!=null&&"3".equals(subdto.getSxaddword()))
					taxType7211Map.put("BudgetLevelName", "市"); // 预算级次名称
				else if(subdto.getSxaddword()!=null&&"4".equals(subdto.getSxaddword()))
					taxType7211Map.put("BudgetLevelName", "县"); // 预算级次名称
				else if(subdto.getSxaddword()!=null&&"5".equals(subdto.getSxaddword()))
					taxType7211Map.put("BudgetLevelName", "乡"); // 预算级次名称
				else
					taxType7211Map.put("BudgetLevelName", MtoCodeTrans.transformString(maindto.getSbudgetlevelname())); // 预算级次名称
				
				if(null == list.get(i).getStaxstartdate() || "".equals(list.get(i).getStaxstartdate().trim())){
					taxType7211Map.put("TaxStartDate", maindto.getSvoudate()); // 税款所属日期起
				}else{
					taxType7211Map.put("TaxStartDate", maindto.getStaxstartdate()); // 税款所属日期起
				}
				
				if(null == list.get(i).getSexpdate() || "".equals(list.get(i).getSexpdate().trim())){
					taxType7211Map.put("TaxEndDate", maindto.getSvoudate().replaceAll("-", "")); // 限缴日期
				}else{
					taxType7211Map.put("TaxEndDate", maindto.getSexpdate().replaceAll("-", "")); // 税款所属日期止
				}
				taxType7211Map.put("ViceSign", MtoCodeTrans.transformString(subdto.getSvicesign())); // 辅助标志
				taxType7211Map.put("TaxType", "1"); // 税款类型
				taxType7211Map.put("HandBookKind", "2"); // 缴款书种类
				taxType7211Map.put("DetailNum", 0); //明细条数
				taxType7211Map.put("SubjectList7211", new ArrayList<Object>());
				
				// 1.3.1.3.1　报文体(基本信息段)-一个税种信息_包含零个段税目信息段
//				taxType7211Map.put("SubjectList7211", new ArrayList<Object>());
				
				taxInfo7211Map.put("Payment7211", payment7211Map);
				taxInfo7211Map.put("TaxVou7211", taxVou7211Map);
				taxInfo7211Map.put("TaxType7211", taxType7211Map);
				taxInfo7211List.add(taxInfo7211Map);
			}
		}
		
		taxBody7211Map.put("TaxInfo7211", taxInfo7211List);
		batchHeadMap.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead7211", batchHeadMap);
		msgMap.put("TurnAccount7211", turnAccountMap);
		msgMap.put("TaxBody7211", taxBody7211Map);
		
//		// 更新凭证状态为已经发送
//		
//		String updateSQL = "update TV_VOUCHERINFO set S_STATUS = ? where S_ORGCODE = ? and S_FILENAME = ? and S_PACKAGENO = ? and S_COMMITDATE = ? ";
//		SQLExecutor sqlExec;
//		try {
//			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
//			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING); // 处理中
//			sqlExec.addParam(orgcode);
//			sqlExec.addParam(filename);
//			sqlExec.addParam(packno);
//			sqlExec.addParam(list.get(0).getScommitdate());
//			sqlExec.runQueryCloseCon(updateSQL);
//		} catch (JAFDatabaseException e) {
//			logger.error("更新税票明细状态的时候出现异常！", e);
//			throw new ITFEBizException("更新税票明细状态的时候出现异常！", e);
//		}
		
//		// 更新报文头的状态为处理中
//		String updateFileSQL = "update TV_FILEPACKAGEREF set S_RETCODE = ? where S_ORGCODE = ? and S_TRECODE = ? and S_FILENAME = ? and S_PACKAGENO = ? ";
//		SQLExecutor sqlUpdateExec;
//		try {
//			sqlUpdateExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
//			sqlUpdateExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING); // 处理中
//			sqlUpdateExec.addParam(orgcode);
//			sqlUpdateExec.addParam(list.get(0).getStrecode());
//			sqlUpdateExec.addParam(filename);
//			sqlUpdateExec.addParam(packno);
//			sqlUpdateExec.runQueryCloseCon(updateFileSQL);
//		} catch (JAFDatabaseException e) {
//			logger.error("更新报文头的时候出现异常！", e);
//			throw new ITFEBizException("更新报文头的时候出现异常！", e);
//		}
		return map;
	}

}

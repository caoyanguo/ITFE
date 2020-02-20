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
public class Dto2MapFor5408_JL {
	
	private static Log logger = LogFactory.getLog(Dto2MapFor5408_JL.class);
	
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
		for (int i = 0; i < list.size(); i++) {
			TvNontaxsubDto subdto = new TvNontaxsubDto();
			subdto.setSdealno(list.get(i).getSdealno());
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(list.get(i));
			
			allamt = allamt.add(list.get(i).getNmoney()); // 计算汇总金额
			
			// 1.3.1　报文体(基本信息段)
			HashMap<String, Object> taxInfo7211Map = new HashMap<String, Object>();
			
			// 1.3.1.1　报文体(基本信息段)- 一个付款信息段
			HashMap<String, String> payment7211Map = new HashMap<String, String>();
			if (list.get(i).getSid().length()<8) {
				String transno = "00000000"+list.get(i).getSid();
				payment7211Map.put("TraNo", transno); // 交易流水号
			}else{
			    payment7211Map.put("TraNo", list.get(i).getSid()); // 交易流水号
			}
			
			payment7211Map.put("TraAmt", MtoCodeTrans.transformString(list.get(i).getNmoney())); //交易金额
			payment7211Map.put("PayOpBnkNo", MtoCodeTrans.transformString(list.get(i).getSpaybankno())); //付款开户行行号
//			payment7211Map.put("PayOpBnkName", MtoCodeTrans.transformString(null)); // 付款开户行名称
			if(null == list.get(i).getStaxpayname() || "".equals(list.get(i).getStaxpayname().trim())){
				payment7211Map.put("HandOrgName", "dfhl"); // 缴款单位名称
			}else{
				payment7211Map.put("HandOrgName", MtoCodeTrans.transformString(list.get(i).getSpayacctname())); // 缴款单位名称
			}
			
			payment7211Map.put("PayAcct", MtoCodeTrans.transformString(list.get(i).getSpayacctno())); // 付款账户

			// 1.3.1.2　报文体(基本信息段)-一个税票信息段
			HashMap<String, String> taxVou7211Map = new HashMap<String, String>();
			taxVou7211Map.put("TaxVouNo", list.get(i).getSvoucherno()); // 税票号码
			taxVou7211Map.put("BillDate", list.get(i).getSvoudate()); // 开票日期
			if(null == list.get(i).getStaxpaycode() || "".equals(list.get(i).getStaxpaycode().trim())){
				taxVou7211Map.put("TaxPayCode", "10101"); // 纳税人编码
			}else{
				taxVou7211Map.put("TaxPayCode", MtoCodeTrans.transformString(list.get(i).getStaxpaycode())); // 纳税人编码
			}
			if(null == list.get(i).getStaxpayname() || "".equals(list.get(i).getStaxpayname().trim())){
				taxVou7211Map.put("TaxPayName", "dfhl"); // 纳税人名称
			}else{
				taxVou7211Map.put("TaxPayName", MtoCodeTrans.transformString(list.get(i).getStaxpayname())); // 纳税人名称
			}

			taxVou7211Map.put("BudgetType", list.get(i).getSbudgettype()); // 预算种类
			taxVou7211Map.put("TrimSign", list.get(i).getStrimflag()); // 整理期标志
			taxVou7211Map.put("CorpCode",  MtoCodeTrans.transformString(list.get(i).getStaxpaycode())); // 企业代码
			taxVou7211Map.put("CorpName", MtoCodeTrans.transformString(list.get(i).getStaxpayname())); // 企业名称
//			taxVou7211Map.put("CorpType", MtoCodeTrans.transformString(null)); // 企业注册类型
//			taxVou7211Map.put("Remark", MtoCodeTrans.transformString(null)); // 备注
//			taxVou7211Map.put("Remark1", MtoCodeTrans.transformString(null)); // 备注1
//			taxVou7211Map.put("Remark2", MtoCodeTrans.transformString(null)); // 备注2

			// 1.3.1.3　报文体(基本信息段)-一个税种信息段
			HashMap<String, Object> taxType7211Map = new HashMap<String, Object>();
			taxType7211Map.put("BudgetSubjectCode", ((TvNontaxsubDto)sublist.get(0)).getSbudgetsubject()); // 预算科目代码
//			taxType7211Map.put("BudgetSubjectName", MtoCodeTrans.transformString(null)); // 预算科目名称
			
			if(null == list.get(i).getSexpdate() || "".equals(list.get(i).getSexpdate().trim())){
				taxType7211Map.put("LimitDate", list.get(i).getSvoudate().replaceAll("-", "")); // 限缴日期
			}else{
				taxType7211Map.put("LimitDate", list.get(i).getSexpdate().replaceAll("-", "")); // 限缴日期
			}
			
			taxType7211Map.put("TaxTypeCode", MtoCodeTrans.transformString(list.get(i).getStaxtypecode())); // 税种代码
			taxType7211Map.put("TaxTypeName", MtoCodeTrans.transformString(list.get(i).getStaxtypename())); // 税种名称
			taxType7211Map.put("BudgetLevelCode", list.get(i).getSbudgetlevelcode()); // 预算级次代码
			taxType7211Map.put("BudgetLevelName", list.get(i).getSbudgetlevelname()); // 预算级次名称
			
			if(null == list.get(i).getStaxstartdate() || "".equals(list.get(i).getStaxstartdate().trim())){
				taxType7211Map.put("TaxStartDate", list.get(i).getSvoudate()); // 税款所属日期起
			}else{
				taxType7211Map.put("TaxStartDate", list.get(i).getStaxstartdate()); // 税款所属日期起
			}
			
			if(null == list.get(i).getSexpdate() || "".equals(list.get(i).getSexpdate().trim())){
				taxType7211Map.put("TaxEndDate", list.get(i).getSvoudate().replaceAll("-", "")); // 限缴日期
			}else{
				taxType7211Map.put("TaxEndDate", list.get(i).getSexpdate().replaceAll("-", "")); // 税款所属日期止
			}
			
			
			taxType7211Map.put("ViceSign", MtoCodeTrans.transformString(((TvNontaxsubDto)sublist.get(i)).getSvicesign())); // 辅助标志
			taxType7211Map.put("TaxType", "1"); // 税款类型
			taxType7211Map.put("HandBookKind", "2"); // 缴款书种类
			
			
			if(MsgConstant.MSG_SOURCE_PLACE.equals(list.get(i).getSsourceflag())){
				if(null == list.get(i).getSext1() || null == list.get(i).getSext1()){
					taxType7211Map.put("DetailNum", 0); //明细条数
					taxType7211Map.put("SubjectList7211", new ArrayList<Object>());
				}else{
					taxType7211Map.put("DetailNum", 1); //明细条数
					HashMap<String, Object> subjectList7211Map = new HashMap<String, Object>();
					subjectList7211Map.put("DetailNo", "1");
					subjectList7211Map.put("TaxSubjectCode", MtoCodeTrans.transformString(list.get(i).getStaxtypecode()));
					subjectList7211Map.put("TaxSubjectName", MtoCodeTrans.transformString(list.get(i).getStaxtypename()));
					subjectList7211Map.put("TaxNumber", MtoCodeTrans.transformString(list.get(i).getScount()));
					subjectList7211Map.put("TaxAmt", MtoCodeTrans.transformString(list.get(i).getNmoney()));
					subjectList7211Map.put("TaxRate", MtoCodeTrans.transformString(list.get(i).getNmoney()));
					subjectList7211Map.put("ExpTaxAmt", MtoCodeTrans.transformString(list.get(i).getNmoney()));
					subjectList7211Map.put("DiscountTaxAmt", MtoCodeTrans.transformString(list.get(i).getNmoney()));
					subjectList7211Map.put("FactTaxAmt", MtoCodeTrans.transformString(list.get(i).getNmoney()));
					
					List<Object> subjectList7211List = new ArrayList<Object>();
					subjectList7211List.add(subjectList7211Map);
					taxType7211Map.put("SubjectList7211", subjectList7211List);
				}
			}else{
				taxType7211Map.put("DetailNum", 0); //明细条数
				taxType7211Map.put("SubjectList7211", new ArrayList<Object>());
			}
			
			// 1.3.1.3.1　报文体(基本信息段)-一个税种信息_包含零个段税目信息段
//			taxType7211Map.put("SubjectList7211", new ArrayList<Object>());
			
			taxInfo7211Map.put("Payment7211", payment7211Map);
			taxInfo7211Map.put("TaxVou7211", taxVou7211Map);
			taxInfo7211Map.put("TaxType7211", taxType7211Map);
			taxInfo7211List.add(taxInfo7211Map);
		}
		
		taxBody7211Map.put("TaxInfo7211", taxInfo7211List);
		batchHeadMap.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead7211", batchHeadMap);
		msgMap.put("TurnAccount7211", turnAccountMap);
		msgMap.put("TaxBody7211", taxBody7211Map);
		return map;
	}

}

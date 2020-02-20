package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class Dto2MapFor7211 {

	private static Log logger = LogFactory.getLog(Dto2MapFor7211.class);

	/**
	 * DTO转化XML报文(收入税票业务)
	 * 
	 * @param List
	 *            <TvInfileDto> list 发送报文对象集合
	 * @param String
	 *            orgcode 机构代码
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            packno 包流水号
	 * @param boolean isRepeat 是否重发报文
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(List<TvInfileDto> list,String orgcode,String filename,String packno,boolean isRepeat) throws ITFEBizException{
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
		turnAccountMap.put("FundSrlNo", MtoCodeTrans.transformString(list.get(0).getStrasrlno())); // 资金收纳流水号
		turnAccountMap.put("PayBnkNo", MtoCodeTrans.transformString(list.get(0).getSpaybnkno())); // 付款行行号
		turnAccountMap.put("PayeeTreCode", list.get(0).getSrecvtrecode()); // 收款国库代码
//		turnAccountMap.put("PayeeTreName", MtoCodeTrans.transformString(null)); // 收款国库名称
		
		// 1.3　报文体(多个基本信息段)
		HashMap<String, Object> taxBody7211Map = new HashMap<String, Object>();
		
		List<Object> taxInfo7211List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			allamt = allamt.add(list.get(i).getNmoney()); // 计算汇总金额
			
			// 1.3.1　报文体(基本信息段)
			HashMap<String, Object> taxInfo7211Map = new HashMap<String, Object>();
			
			// 1.3.1.1　报文体(基本信息段)- 一个付款信息段
			HashMap<String, String> payment7211Map = new HashMap<String, String>();
			payment7211Map.put("TraNo", list.get(i).getSdealno()); // 交易流水号
			payment7211Map.put("TraAmt", MtoCodeTrans.transformString(list.get(i).getNmoney())); //交易金额
			payment7211Map.put("PayOpBnkNo", MtoCodeTrans.transformString(list.get(i).getSopenaccbankcode())); //付款开户行行号
//			payment7211Map.put("PayOpBnkName", MtoCodeTrans.transformString(null)); // 付款开户行名称
			if(null == list.get(i).getStaxpayname() || "".equals(list.get(i).getStaxpayname().trim())){
				payment7211Map.put("HandOrgName", "dfhl"); // 缴款单位名称
			}else{
				payment7211Map.put("HandOrgName", MtoCodeTrans.transformString(list.get(i).getStaxpayname())); // 缴款单位名称
			}
			
			payment7211Map.put("PayAcct", MtoCodeTrans.transformString(list.get(i).getSpayacct())); // 付款账户

			// 1.3.1.2　报文体(基本信息段)-一个税票信息段
			HashMap<String, String> taxVou7211Map = new HashMap<String, String>();
			taxVou7211Map.put("TaxVouNo", list.get(i).getStaxticketno()); // 税票号码
			taxVou7211Map.put("BillDate", list.get(i).getSgenticketdate()); // 开票日期
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
			taxType7211Map.put("BudgetSubjectCode", list.get(i).getSbudgetsubcode()); // 预算科目代码
//			taxType7211Map.put("BudgetSubjectName", MtoCodeTrans.transformString(null)); // 预算科目名称
			
			if(null == list.get(i).getSlimitdate() || "".equals(list.get(i).getSlimitdate().trim())){
				taxType7211Map.put("LimitDate", list.get(i).getSaccdate().replaceAll("-", "")); // 限缴日期
			}else{
				taxType7211Map.put("LimitDate", list.get(i).getSlimitdate().replaceAll("-", "")); // 限缴日期
			}
			
			taxType7211Map.put("TaxTypeCode", MtoCodeTrans.transformString(list.get(i).getStaxtypecode())); // 税种代码
			taxType7211Map.put("TaxTypeName", MtoCodeTrans.transformString(list.get(i).getStaxtypename())); // 税种名称
			taxType7211Map.put("BudgetLevelCode", list.get(i).getSbudgetlevelcode()); // 预算级次代码
			taxType7211Map.put("BudgetLevelName", PublicSearchFacade.getBdgLevelNameByCode(list.get(i).getSbudgetlevelcode())); // 预算级次名称
			
			if(null == list.get(i).getStaxstartdate() || "".equals(list.get(i).getStaxstartdate().trim())){
				taxType7211Map.put("TaxStartDate", list.get(i).getSaccdate()); // 税款所属日期起
			}else{
				taxType7211Map.put("TaxStartDate", list.get(i).getStaxstartdate()); // 税款所属日期起
			}
			
			if(null == list.get(i).getStaxenddate() || "".equals(list.get(i).getStaxenddate().trim())){
				taxType7211Map.put("TaxEndDate", list.get(i).getSaccdate().replaceAll("-", "")); // 限缴日期
			}else{
				taxType7211Map.put("TaxEndDate", list.get(i).getStaxenddate().replaceAll("-", "")); // 税款所属日期止
			}
			
			
			taxType7211Map.put("ViceSign", MtoCodeTrans.transformString(list.get(i).getSassitsign())); // 辅助标志
			taxType7211Map.put("TaxType", "1"); // 税款类型
			taxType7211Map.put("HandBookKind", "2"); // 缴款书种类
			
			
			if(MsgConstant.MSG_SOURCE_PLACE.equals(list.get(i).getSsourceflag())){
				if(null == list.get(i).getNtaxamt() || null == list.get(i).getNfacttaxamt()){
					taxType7211Map.put("DetailNum", 0); //明细条数
					taxType7211Map.put("SubjectList7211", new ArrayList<Object>());
				}else{
					taxType7211Map.put("DetailNum", 1); //明细条数
					HashMap<String, Object> subjectList7211Map = new HashMap<String, Object>();
					subjectList7211Map.put("DetailNo", "1");
					subjectList7211Map.put("TaxSubjectCode", MtoCodeTrans.transformString(list.get(i).getStaxtypecode()));
					subjectList7211Map.put("TaxSubjectName", MtoCodeTrans.transformString(list.get(i).getStaxsubjectname()));
					subjectList7211Map.put("TaxNumber", MtoCodeTrans.transformString(list.get(i).getStaxnumber()));
					subjectList7211Map.put("TaxAmt", MtoCodeTrans.transformString(list.get(i).getNtaxamt()));
					subjectList7211Map.put("TaxRate", MtoCodeTrans.transformString(list.get(i).getNtaxrate()));
					subjectList7211Map.put("ExpTaxAmt", MtoCodeTrans.transformString(list.get(i).getNfacttaxamt()));
					subjectList7211Map.put("DiscountTaxAmt", MtoCodeTrans.transformString(list.get(i).getNdiscounttaxamt()));
					subjectList7211Map.put("FactTaxAmt", MtoCodeTrans.transformString(list.get(i).getNfacttaxamt()));
					
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
		
		// 更新凭证状态为已经发送
		String updateSQL = "update TV_INFILE set S_STATUS = ? where S_ORGCODE = ? and S_FILENAME = ? and S_PACKAGENO = ? and S_COMMITDATE = ? ";
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING); // 处理中
			sqlExec.addParam(orgcode);
			sqlExec.addParam(filename);
			sqlExec.addParam(packno);
			sqlExec.addParam(list.get(0).getScommitdate());
			sqlExec.runQueryCloseCon(updateSQL);
		} catch (JAFDatabaseException e) {
			logger.error("更新税票明细状态的时候出现异常！", e);
			throw new ITFEBizException("更新税票明细状态的时候出现异常！", e);
		}
		
		// 更新报文头的状态为处理中
		String updateFileSQL = "update TV_FILEPACKAGEREF set S_RETCODE = ? where S_ORGCODE = ? and S_TRECODE = ? and S_FILENAME = ? and S_PACKAGENO = ? ";
		SQLExecutor sqlUpdateExec;
		try {
			sqlUpdateExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlUpdateExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING); // 处理中
			sqlUpdateExec.addParam(orgcode);
			sqlUpdateExec.addParam(list.get(0).getSrecvtrecode());
			sqlUpdateExec.addParam(filename);
			sqlUpdateExec.addParam(packno);
			sqlUpdateExec.runQueryCloseCon(updateFileSQL);
		} catch (JAFDatabaseException e) {
			logger.error("更新报文头的时候出现异常！", e);
			throw new ITFEBizException("更新报文头的时候出现异常！", e);
		}
	
//		// 更新包头状态为已经发送 
//		TvFilepackagerefDto packdto = new TvFilepackagerefDto();
//		packdto.setSorgcode(orgcode); // 机构代码
//		packdto.setStrecode(list.get(0).getSrecvtrecode()); // 国库代码
//		packdto.setSfilename(filename); // 导入文件名
//		packdto.setStaxorgcode(list.get(0).getStaxorgcode()); // 机关代码
//		packdto.setScommitdate(list.get(0).getScommitdate()); // 委托日期
//		packdto.setSaccdate(TimeFacade.getCurrentStringTime()); // 账务日期
//		packdto.setSpackageno(packno); // 包流水号
//		packdto.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_INCOME); // 业务类型
//		packdto.setIcount(list.size()); // 总笔数
//		packdto.setNmoney(allamt); // 总金额
//		packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_DEALING); // 处理中
//		packdto.setSusercode(list.get(0).getSusercode()); // 操作员代码
//		packdto.setSdemo(list.get(0).getStrasrlno()); // 资金收纳流水号
//		packdto.setImodicount(2);
//
//		try {
//			DatabaseFacade.getDb().update(packdto);
//		} catch (JAFDatabaseException e) {
//			logger.error("更新报文头的时候出现异常！", e);
//			throw new ITFEBizException("更新报文头的时候出现异常！", e);
//		}
		
		return map;
	}
}

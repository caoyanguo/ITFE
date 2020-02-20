package com.cfcc.itfe.msgmanager.msg;

import java.io.UnsupportedEncodingException;
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
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinCustomonlineDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.webservice.FinReportService;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 海关财政电子税票3178
 * 
 * @author zhangliang
 * 
 */
@SuppressWarnings("unchecked")
public class Proc3178MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Proc3178MsgServer.class);

	/**
	 * 报文信息处理
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		/**
		 * 解析信息头
		 */
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3178");
		String finOrgCode = (String) batchheadMap.get("FinOrgCode"); // 财政机关代码
		String applyDate = (String) batchheadMap.get("ApplyDate"); // 申请日期
		String packNo = (String) batchheadMap.get("PackNo"); // 包流水号
		String treCode = (String) batchheadMap.get("TreCode"); // 国库代码
		String treName = (String) batchheadMap.get("TreName"); // 国库名称
		String allNum = (String) batchheadMap.get("AllNum"); // 总笔数
		BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("AllAmt"));// 总金额
		int childPackNum = Integer.valueOf((String) batchheadMap.get("ChildPackNum")); // 子包总数
		String curPackNo = (String) batchheadMap.get("CurPackNo"); // 本包序号
		int curPackNum = Integer.valueOf((String) batchheadMap.get("CurPackNum")); // 本包笔数
		BigDecimal curPackAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("CurPackAmt"));// 本包金额
		String sbookorgcode = (String) headMap.get("DES");

		
		List <IDto> list = new ArrayList<IDto>();
		// 查询报表是否接收过
		TvRecvlogDto logDto = new TvRecvlogDto();
		logDto.setSseq((String) headMap.get("MsgID"));
		try {
			HashMap<String, TsConvertfinorgDto> mapFincInfoByFinc = SrvCacheFacade.cacheFincInfoByFinc(null);
			if (!mapFincInfoByFinc.containsKey(finOrgCode)) {
				eventContext.setStopFurtherProcessing(true);
				return;
			}
			// 根据国库查询核算主体
			TsTreasuryDto _dto = SrvCacheFacade.cacheTreasuryInfo(null).get(treCode);
			if (null != _dto) {
				sbookorgcode = _dto.getSorgcode();
			}
			List<TvRecvlogDto> finddtolist = CommonFacade.getODB().findRsByDto(
					logDto);
			// 已经接收日志记录
			if (null == finddtolist || finddtolist.size() == 0) { // 说明已经没有接收过，程序重新解析
				HashMap taxbody3178 = (HashMap) msgMap.get("TaxBody3178");
				if (null != taxbody3178) {
					List taxinfo3178 = (List) taxbody3178.get("TaxInfo3178");
					if (null == taxinfo3178 || taxinfo3178.size() == 0) {
						return;
					} else {
						try {
							int count = taxinfo3178.size();
							for (int i = 0; i < count; i++) {
								HashMap taxBody3178map = (HashMap) taxinfo3178.get(i);
								HashMap taxInfo3178map = (HashMap) taxBody3178map.get("TaxInfo3178");
								HashMap taxVou3178 = (HashMap) taxBody3178map.get("TaxVou3178");
								String TaxOrgCode = (String) taxVou3178.get("TaxOrgCode"); // 征收机关代码
								String PayBnkNo = (String) taxVou3178.get("PayBnkNo"); // 付款行行号
								String TraNo = (String) taxVou3178.get("TraNo"); // 交易流水号
								String OriMsgNo = (String) taxVou3178.get("OriMsgNo"); // 原报文编号
								BigDecimal TraAmt = MtoCodeTrans.transformBigDecimal(taxVou3178.get("TraAmt")); // 交易金额
								String PayerOpBkNo = (String) taxVou3178.get("PayerOpBkNo"); // 付款开户行行号
								String PayerOpBkName = (String) taxVou3178.get("PayerOpBkName"); // 付款开户行名称
								String HandOrgName = (String) taxVou3178.get("HandOrgName"); // 缴款单位名称
								String PayAcct = (String) taxVou3178.get("PayAcct"); // 付款账户
								String TaxVouNo = (String) taxVou3178.get("TaxVouNo"); // 税票号码
								String BillDate = (String) taxVou3178.get("BillDate"); // 开票日期
								String TaxPayCode = (String) taxVou3178.get("TaxPayCode"); // 纳税人编码
								String TaxPayName = (String) taxVou3178.get("TaxPayName"); // 纳税人名称
								String TaxPayCode1 = (String) taxVou3178.get("TaxPayCode1"); // 纳税人编码
								String TaxPayName1 = (String) taxVou3178.get("TaxPayName1"); // 纳税人名称
								String BudgetType = (String) taxVou3178.get("BudgetType"); // 预算种类
								String TrimSign = (String) taxVou3178.get("TrimSign"); // 整理期标志
								String CorpCode = (String) taxVou3178.get("CorpCode"); // 企业代码
								String CorpName = (String) taxVou3178.get("CorpName"); // 企业名称
								String CorpType = (String) taxVou3178.get("CorpType"); // 企业注册类型
								String BudgetSubjectCode = (String) taxVou3178.get("BudgetSubjectCode"); // 预算科目代码
								String BudgetSubjectName = (String) taxVou3178.get("BudgetSubjectName"); // 预算科目名称
								String LimitDate = (String) taxVou3178.get("LimitDate"); // 限缴日期
								String TaxTypeCode = (String) taxVou3178.get("TaxTypeCode"); // 税种代码
								String TaxTypeName = (String) taxVou3178.get("TaxTypeName"); // 税种名称
								String BudgetLevelCode = (String) taxVou3178.get("BudgetLevelCode"); // 预算级次代码
								String BudgetLevelName = (String) taxVou3178.get("BudgetLevelName"); // 预算级次名称
								String TaxStartDate = (String) taxVou3178.get("TaxStartDate"); // 税款所属日期起
								String TaxEndDate = (String) taxVou3178.get("TaxEndDate"); // 税款所属日期止
								String ViceSign = (String) taxVou3178.get("ViceSign"); // 辅助标志
								String TaxType = (String) taxVou3178.get("TaxType"); // 税款类型
								String Remark = (String) taxVou3178.get("Remark"); // 备注
								String Remark1 = (String) taxVou3178.get("Remark1"); // 备注1
								String Remark2 = (String) taxVou3178.get("Remark2"); // 备注2
								String OpStat = (String) taxVou3178.get("OpStat"); // 处理状态
//								TvFinIncomeonlineDto dto = new TvFinIncomeonlineDto();
								TvFinCustomonlineDto dto = new TvFinCustomonlineDto();
								/**
								 * 组织DTO准备保存数据*****************************
								 */
								String _sseq = "";
								try {
									_sseq = StampFacade.getStampSendSeq("JS"); // 取财政申请入库流水信息业务流水
								} catch (SequenceException e) {
									logger.error(e);
									throw new ITFEBizException(
											"取财政申请电子税票信息业务流水SEQ出错");
								}
								dto.setSseq(_sseq);// 业务流水号
								dto.setSfinorgcode(finOrgCode); // 财政机关代码
								dto.setSapplydate(applyDate); // 申请日期
								dto.setSpackno(packNo); // 包流水号
								dto.setStrecode(treCode); // 国库代码
								dto.setStrename(treName); // 国库名称					
								if(treName==null || treName.trim().equals("")){
									dto.setStrename("");// 国库名称
								}else{
									dto.setStrename(treName);// 国库名称
								}
								dto.setStaxorgcode(TaxOrgCode); // 征收机关代码
								if(PayBnkNo==null || PayBnkNo.trim().equals("")){
									dto.setSpaybnkno("");// 付款行行号
								}else{
								  dto.setSpaybnkno(PayBnkNo.trim());// 付款行行号
								}
								dto.setStrano(TraNo); // 交易流水号
								dto.setSorimsgno(OriMsgNo); // 原报文编号
								dto.setNtraamt(TraAmt); // 交易金额
								if(PayerOpBkNo==null || PayerOpBkNo.trim().equals("")){
									dto.setSpayeropbkno("");// 付款人开户行行号
								}else{
								   dto.setSpayeropbkno(PayerOpBkNo.trim());// 付款人开户行行号
								}
								
								if(PayerOpBkName==null || PayerOpBkName.trim().equals("")){
									dto.setSpayeropbkname("");// 付款开户行名称
								}else{
								   dto.setSpayeropbkname(PayerOpBkName.trim());// 付款开户行名称
								}
								dto.setShandorgname(HandOrgName); // 缴款单位名称
								
								if(PayAcct==null || PayAcct.trim().equals("")){
								   dto.setSpayacct("");// 付款人账号
								}else{
								   dto.setSpayacct(PayAcct.trim());// 付款人账号
								}
								dto.setStaxvouno(TaxVouNo); // 税票号码
								dto.setSbilldate(BillDate); // 开票日期
								dto.setStaxpaycode(TaxPayCode); // 纳税人编码
								dto.setStaxpayname(TaxPayName); // 纳税人名称
								dto.setSbudgettype(BudgetType); // 预算种类
								dto.setStrimsign(TrimSign); // 整理期标志
								
								if(CorpCode==null || CorpCode.trim().equals("")){
									dto.setScorpcode("");// 企业代码
								}else{
									dto.setScorpcode(CorpCode.trim());// 企业代码
								}
								
								if(CorpName==null || CorpName.trim().equals("")){
									dto.setScorpname("");// 企业名称
								}else{
									dto.setScorpname(CorpName.trim());// 企业名称
								}
								
								if(CorpType==null || CorpType.trim().equals("")){
									dto.setScorptype("");// 企业类型
								}else{
									dto.setScorptype(CorpType.trim());// 企业类型
								}
								dto.setSbudgetsubjectcode(BudgetSubjectCode); // 预算科目代码
								
								if(BudgetSubjectName==null || BudgetSubjectName.trim().equals("")){
									dto.setSbudgetsubjectname("");// 预算科目名称
								}else{
									dto.setSbudgetsubjectname(BudgetSubjectName.trim());// 预算科目名称
								}
								dto.setSlimitdate(LimitDate.trim());// 限缴日期
								
								if(TaxTypeCode==null || TaxTypeCode.trim().equals("")){
									dto.setStaxtypecode("");// 税种代码
								}else{
									dto.setStaxtypecode(TaxTypeCode.trim());// 税种代码
								}
								
								if(TaxTypeName==null || TaxTypeName.trim().equals("")){
									dto.setStaxtypename("");// 税种名称
								}else{
									dto.setStaxtypename(TaxTypeName.trim());// 税种名称
								}
								dto.setSbudgetlevelcode(BudgetLevelCode); // 预算级次代码
								if(BudgetLevelName==null || BudgetLevelName.trim().equals("")){
									dto.setSbudgetlevelname("");// 预算级次名称
								}else{
									dto.setSbudgetlevelname(BudgetLevelName.trim());// 预算级次名称
								}
								dto.setStaxstartdate(TaxStartDate); // 税款所属日期起
								dto.setStaxenddate(TaxEndDate); // 税款所属日期止
								
								if(ViceSign==null || ViceSign.trim().equals("")){
									dto.setSvicesign("");// 辅助标志
								}else{
									dto.setSvicesign(ViceSign);// 辅助标志
								}
								if(TaxType==null || TaxType.trim().equals("")){
									dto.setStaxtype("");//税款类型
								}else{
									dto.setStaxtype(TaxType.trim());// 税款类型
								}
								if(Remark==null || Remark.trim().equals("")){
									dto.setSremark("");// 备注
								}else{
									dto.setSremark(StringUtil.replace(Remark.trim(), ",", "."));// 备注
								}
								
								if(Remark1==null || Remark1.trim().equals("")){
									dto.setSremark1("");// 备注1
								}else{
									dto.setSremark1(StringUtil.replace(Remark1.trim(), ",", "."));// 备注1
								}
								
								if(Remark2==null || Remark2.trim().equals("")){
									dto.setSremark2("");// 备注2
								}else{
									dto.setSremark2(StringUtil.replace(Remark2.trim(), ",", "."));// 备注2
								}
								dto.setSopstat(OpStat.trim());// 处理状态
								dto.setSext1(String.valueOf(new Timestamp(new java.util.Date().getTime())));// 系统更新时间
								dto.setSext2(TimeFacade.getCurrentStringTime());
								list.add(dto);
								if ((list.size()>0 && list.size() % 1000 == 0) ||( i+1) == count) {
								 DatabaseFacade.getDb().create(
										 list.toArray(new IDto[list.size()]));
								 list = new ArrayList<IDto>();
								}
							}
							saveDownloadReportCheck(applyDate,treCode);
						} catch (JAFDatabaseException e) {
							String error = "保存3178报文（TIPS发送数据报文包核对）时出现数据库异常！";
							logger.error(error, e);
							throw new ITFEBizException(error, e);
						}
					}
				}
			}
		} catch (Exception e) {
			String error = "查询3178财政申请电子税票信息数据时出现数据库异常！";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		}
		/*
		 * 接收/发送日志
		 */
		String recvseqno;// 接收日志流水号
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
		} catch (SequenceException e) {
			logger.error("取接收流水号失败!", e);
			throw new ITFEBizException("取接收流水号失败", e);
		}
		String path = (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH");
		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, recvseqno, sbookorgcode, applyDate,
				(String) headMap.get("MsgNo"), (String) headMap.get("SRC"),
				path, Integer.parseInt(allNum),allAmt, packNo, treCode, String.valueOf(childPackNum), finOrgCode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,
				MsgConstant.LOG_ADDWORD_RECV_TIPS);
		
		eventContext.setStopFurtherProcessing(true);
		return;
	

	}
	private void saveDownloadReportCheck(String date,String trecode)
	{
		if(date==null||trecode==null||"".equals(date)||"".equals(trecode))
			return;
		TdDownloadReportCheckDto finddto = new TdDownloadReportCheckDto();
		finddto.setSdates(date);
		finddto.setStrecode(trecode);
		try {
			TdDownloadReportCheckDto dto = (TdDownloadReportCheckDto)DatabaseFacade.getODB().find(finddto);
			if(dto==null)
			{
				finddto.setSishaiguan("1");
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if("0".equals(dto.getSshuipiao())||null==dto.getSshuipiao())
				{
					dto.setSishaiguan("1");
					DatabaseFacade.getODB().update(dto);
				}
			}
		} catch (JAFDatabaseException e) {
			log.error("保存下载报表情况检查表失败:"+e.toString());
		}catch(Exception e)
		{
			log.error("保存下载报表情况检查表失败:"+e.toString());
		}
	}
}

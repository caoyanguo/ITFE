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

import com.cfcc.deptone.mto.paymentmto.msgparser.util.MsgFormat;
import com.cfcc.itfe.config.ITFECommonConstant;
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
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsSysbatchDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.webservice.FinReportService;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 本工作日TIPS向某外联机构转发的所有相关报文信息（报文编号涉及：3129电子税票） 主要功能：核对从TIPS系统发送给ITFE系统包的信息
 * 
 * @author zhouchuan
 * 
 */
public class Proc3129MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Proc3129MsgServer.class);

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
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3129");
		String ApplyDate = (String) batchheadMap.get("ApplyDate"); // 申请日期
		String finOrgCode = (String) batchheadMap.get("FinOrgCode"); // 财政机关代码
		String applyDate = (String) batchheadMap.get("ApplyDate"); // 申请日期
		String packNo = (String) batchheadMap.get("PackNo"); // 包流水号
		String treCode = (String) batchheadMap.get("TreCode"); // 国库代码
		String treName = (String) batchheadMap.get("TreName"); // 国库名称
		String allNum = (String) batchheadMap.get("AllNum"); // 总笔数
		BigDecimal allAmt = MtoCodeTrans.transformBigDecimal(batchheadMap
				.get("AllAmt"));// 总金额
		int childPackNum = Integer.valueOf((String) batchheadMap
				.get("ChildPackNum")); // 子包总数
		String curPackNo = (String) batchheadMap.get("CurPackNo"); // 本包序号
		int curPackNum = Integer.valueOf((String) batchheadMap
				.get("CurPackNum")); // 本包笔数
		BigDecimal curPackAmt = MtoCodeTrans.transformBigDecimal(batchheadMap
				.get("CurPackAmt"));// 本包金额
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
				HashMap taxbody3129 = (HashMap) msgMap.get("TaxBody3129");
				if (null != taxbody3129) {
					List taxinfo3129 = (List) taxbody3129.get("TaxInfo3129");
					if (null == taxinfo3129 || taxinfo3129.size() == 0) {
						return;
					} else {
						try {
							int count = taxinfo3129.size();
							for (int i = 0; i < count; i++) {
								HashMap taxBody3129map = (HashMap) taxinfo3129
										.get(i);
								HashMap taxInfo3129map = (HashMap) taxBody3129map
										.get("TaxInfo3129");
								HashMap taxVou3129 = (HashMap) taxBody3129map
										.get("TaxVou3129");
								String TaxOrgCode = (String) taxVou3129
										.get("TaxOrgCode"); // 征收机关代码
								String PayBnkNo = (String) taxVou3129
										.get("PayBnkNo"); // 付款行行号
								String TraNo = (String) taxVou3129.get("TraNo"); // 交易流水号
								String OriMsgNo = (String) taxVou3129
										.get("OriMsgNo"); // 原报文编号
								BigDecimal TraAmt = MtoCodeTrans
										.transformBigDecimal(taxVou3129
												.get("TraAmt")); // 交易金额
								String PayerOpBkNo = (String) taxVou3129
										.get("PayerOpBkNo"); // 付款开户行行号
								String PayerOpBkName = (String) taxVou3129
										.get("PayerOpBkName"); // 付款开户行名称
								String HandOrgName = (String) taxVou3129
										.get("HandOrgName"); // 缴款单位名称
								String PayAcct = (String) taxVou3129
										.get("PayAcct"); // 付款账户
								String TaxVouNo = (String) taxVou3129
										.get("TaxVouNo"); // 税票号码
								String BillDate = (String) taxVou3129
										.get("BillDate"); // 开票日期
								String TaxPayCode = (String) taxVou3129
										.get("TaxPayCode"); // 纳税人编码
								String TaxPayName = (String) taxVou3129
										.get("TaxPayName"); // 纳税人名称
								String BudgetType = (String) taxVou3129
										.get("BudgetType"); // 预算种类
								String TrimSign = (String) taxVou3129
										.get("TrimSign"); // 整理期标志
								String CorpCode = (String) taxVou3129
										.get("CorpCode"); // 企业代码
								String CorpName = (String) taxVou3129
										.get("CorpName"); // 企业名称
								String CorpType = (String) taxVou3129
										.get("CorpType"); // 企业注册类型
								String BudgetSubjectCode = (String) taxVou3129
										.get("BudgetSubjectCode"); // 预算科目代码
								String BudgetSubjectName = (String) taxVou3129
										.get("BudgetSubjectName"); // 预算科目名称
								String LimitDate = (String) taxVou3129
										.get("LimitDate"); // 限缴日期
								String TaxTypeCode = (String) taxVou3129
										.get("TaxTypeCode"); // 税种代码
								String TaxTypeName = (String) taxVou3129
										.get("TaxTypeName"); // 税种名称
								String BudgetLevelCode = (String) taxVou3129
										.get("BudgetLevelCode"); // 预算级次代码
								String BudgetLevelName = (String) taxVou3129
										.get("BudgetLevelName"); // 预算级次名称
								String TaxStartDate = (String) taxVou3129
										.get("TaxStartDate"); // 税款所属日期起
								String TaxEndDate = (String) taxVou3129
										.get("TaxEndDate"); // 税款所属日期止
								String ViceSign = (String) taxVou3129
										.get("ViceSign"); // 辅助标志
								String TaxType = (String) taxVou3129
										.get("TaxType"); // 税款类型
								String Remark = (String) taxVou3129
										.get("Remark"); // 备注
								String Remark1 = (String) taxVou3129
										.get("Remark1"); // 备注1
								String Remark2 = (String) taxVou3129
										.get("Remark2"); // 备注2
								String OpStat = (String) taxVou3129
										.get("OpStat"); // 处理状态
								TvFinIncomeonlineDto dto = new TvFinIncomeonlineDto();
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
								dto.setSorgcode(finOrgCode); // 财政机关代码
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
								dto.setFtraamt(TraAmt); // 交易金额
								if(PayerOpBkNo==null || PayerOpBkNo.trim().equals("")){
									dto.setSpayeropnbnkno("");// 付款人开户行行号
								}else{
								   dto.setSpayeropnbnkno(PayerOpBkNo.trim());// 付款人开户行行号
								}
								
								if(PayerOpBkName==null || PayerOpBkName.trim().equals("")){
									dto.setPayeropbkname("");// 付款开户行名称
								}else{
								   dto.setPayeropbkname(PayerOpBkName.trim());// 付款开户行名称
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
								dto.setCbudgettype(BudgetType); // 预算种类
								dto.setCtrimflag(TrimSign); // 整理期标志
								
								if(CorpCode==null || CorpCode.trim().equals("")){
									dto.setSetpcode("");// 企业代码
								}else{
									dto.setSetpcode(CorpCode.trim());// 企业代码
								}
								
								if(CorpName==null || CorpName.trim().equals("")){
									dto.setSetpname("");// 企业名称
								}else{
									dto.setSetpname(CorpName.trim());// 企业名称
								}
								
								if(CorpType==null || CorpType.trim().equals("")){
									dto.setSetptype("");// 企业类型
								}else{
									dto.setSetptype(CorpType.trim());// 企业类型
								}
								dto.setSbdgsbtcode(BudgetSubjectCode); // 预算科目代码
								
								if(BudgetSubjectName==null || BudgetSubjectName.trim().equals("")){
									dto.setSbdgsbtname("");// 预算科目名称
								}else{
									dto.setSbdgsbtname(BudgetSubjectName.trim());// 预算科目名称
								}
								dto.setSlimit(LimitDate.trim());// 限缴日期
								
								if(TaxTypeCode==null || TaxTypeCode.trim().equals("")){
									dto.setStaxtypecode("");// 税种代码
								}else{
									dto.setStaxtypecode(TaxTypeCode.trim());// 税种代码
								}
								
								if(TaxTypeName==null || TaxTypeName.trim().equals("")){
									dto.setStaxkindname("");// 税种名称
								}else{
									dto.setStaxkindname(TaxTypeName.trim());// 税种名称
								}
								dto.setCbdglevel(BudgetLevelCode); // 预算级次代码
								if(BudgetLevelName==null || BudgetLevelName.trim().equals("")){
									dto.setCbdglevelname("");// 预算级次名称
								}else{
									dto.setCbdglevelname(BudgetLevelName.trim());// 预算级次名称
								}
								dto.setStaxstartdate(TaxStartDate); // 税款所属日期起
								dto.setStaxenddate(TaxEndDate); // 税款所属日期止
								
								if(ViceSign==null || ViceSign.trim().equals("")){
									dto.setSastflag("");// 辅助标志
								}else{
									dto.setSastflag(ViceSign);// 辅助标志
								}
								if(TaxType==null || TaxType.trim().equals("")){
									dto.setCtaxtype("");//税款类型
								}else{
									dto.setCtaxtype(TaxType.trim());// 税款类型
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
								dto.setStrastate(OpStat.trim());// 处理状态
								dto.setSacct(ApplyDate);//账务日期
							    dto.setSinputerid("admin");
								dto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));// 系统更新时间
								list.add(dto);
								if ((list.size()>0 && list.size() % 1000 == 0) ||( i+1) == count) {
								 DatabaseFacade.getDb().create(
										 list.toArray(new IDto[list.size()]));
								 saveDownloadReportCheck(ApplyDate,treCode);
								 list = new ArrayList<IDto>();
								}
							}
						} catch (JAFDatabaseException e) {
							String error = "保存3129报文（TIPS发送数据报文包核对）时出现数据库异常！";
							logger.error(error, e);
							throw new ITFEBizException(error, e);
						}
					}
				}
			}
		} catch (Exception e) {
			String error = "查询3129财政申请电子税票信息数据时出现数据库异常！";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		}
		/*
		 * 接收/发送日志
		 */
		String recvseqno;// 接收日志流水号
		String sendseqno;// 发送日志流水号
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // 取接收日志流水
		} catch (SequenceException e) {
			logger.error("取接收流水号失败!", e);
			throw new ITFEBizException("取接收流水号失败", e);
		}
		String path = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_FILE_NAME);
		// 记接收日志
		MsgLogFacade.writeRcvLog(recvseqno, recvseqno, sbookorgcode, applyDate,
				(String) headMap.get("MsgNo"), (String) headMap.get("SRC"),
				path, 0, new BigDecimal(0), packNo, treCode, String.valueOf(childPackNum), finOrgCode, null,
				(String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null,
				MsgConstant.LOG_ADDWORD_RECV_TIPS);
		
		//用于判断所接财政数据是否已经完整，如果完整则调用WebService通知财政取数    --20131230 by hua
		if("030010011118".equals((String) headMap.get("DES"))) {//目前只支持北京
			//1、首先看是否已经给财政发过通知，如果没有发过则继续
			boolean boo = BusinessFacade.checkAndSaveRecvlog(finOrgCode, applyDate, MsgConstant.MSG_NO_3129, StateConstant.COMMON_NO);
			if(!boo) {
				//2、校验数据完整性并得到msgid的集合
				String[] msgidArray = BusinessFacade.checkIfComplete(finOrgCode, applyDate, MsgConstant.MSG_NO_3129);
				//3、调用通知接口
				if(null != msgidArray && msgidArray.length > 0) {
					FinReportService finReportService = new FinReportService();
					try {
						finReportService.readReportNotice(finOrgCode, applyDate, MsgConstant.MSG_NO_3129,String.valueOf(childPackNum),msgidArray);
						BusinessFacade.checkAndSaveRecvlog(finOrgCode, applyDate, MsgConstant.MSG_NO_3129, StateConstant.COMMON_YES);
					} catch (UnsupportedEncodingException e) {
						String error = "保存3129报文（TIPS发送数据报文包核对）时出现数据库异常！";
						logger.error(error, e);
						throw new ITFEBizException(error, e);
					}
					
				}
			}
		}
		
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
				finddto.setSshuipiao("1");
				DatabaseFacade.getODB().create(finddto);
			}else
			{
				if("0".equals(dto.getSshuipiao())||null==dto.getSshuipiao())
				{
					dto.setSshuipiao("1");
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

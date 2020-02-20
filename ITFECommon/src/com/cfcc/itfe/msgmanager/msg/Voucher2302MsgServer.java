package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.persistence.pk.TsInfoconnorgaccPK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
@SuppressWarnings({ "unchecked", "static-access" })
public class Voucher2302MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher2302MsgServer.class);
	private Voucher voucher;

	/**
	 * 财政发往人行的申请退款凭证回单
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String, String> dealnos = (HashMap<String, String>) muleMessage
				.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("解析申请退款凭证回单2302报文出现错误！", e);
			throw new ITFEBizException("解析申请退款凭证回单2302报文出现错误！", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String AdmDivCode = "";// 行政区划代码
		String StYear = "";// 年度
		String VtCode = "";// 凭证类型
//		String currentDate = TimeFacade.getCurrentStringTime();// 当前系统日期
//		List<String> voucherList = new ArrayList<String>();

		// 获取行政区划代码、年度和凭证类型
		if (VoucherBodyList.size() > 0) {
			Element element = (Element) VoucherBodyList.get(0);
			AdmDivCode = element.attribute("AdmDivCode").getText();
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}

		// 预算单位代码list
		ArrayList<String> agencyCodeList = null;
		// 预算科目代码list
		ArrayList<String> expFuncCodeList = null;

		String SupDepCode = "";
		TvPayreckBankBackDto maindto = null;
		List<TvPayreckBankBackListDto> subDtoList = null;
		List lists = new ArrayList();
		List list = null;
		// 201310报文规范增加字段
		String payDictateNo = "";// 支付交易序号
		String payMsgNo = "";// 支付报文编号
		String payEntrustDate = "";// 支付委托日期
		// String paySndBnkNo = "";// 支付发起行行号(报文规范修改--删除paySndBnkNo字段)
		// 凭证序列号
		String mainvou = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		// 解析报文
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {
				// 明细合计金额
				BigDecimal sumAmt = new BigDecimal("0.00");
				// VoucherBody
				Element element = (Element) VoucherBodyList.get(i);
				// 报文头与报文体凭证编号不一致导致签收异常（宁夏）
				String VoucherNo = element.attribute("VoucherNo").getText();// 凭证编号
				// Voucher
				Element elementVoucher = (Element) element
						.selectSingleNode("Voucher");
				// 明细信息List
				List listDetail = elementVoucher.selectSingleNode("DetailList")
						.selectNodes("Detail");
				// 报文校验信息dto
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerify voucherVerify = new VoucherVerify();
				/**
				 * 凭证信息
				 */
//				String Attach = ((Element) VoucherBodyList.get(i))
//						.elementText("Attach");// 附加信息
				String Id = elementVoucher.elementText("Id");// 申请划款凭证Id
				String VouDate = elementVoucher.elementText("VouDate"); // 凭证日期
				// String VoucherNo = elementVoucher.elementText("VoucherNo");//
				// 凭证号
				String TreCode = elementVoucher.elementText("TreCode"); // 国库主体代码
				String FinOrgCode = elementVoucher.elementText("FinOrgCode");// 财政机关代码
				String BgtTypeCode = elementVoucher.elementText("BgtTypeCode");// 预算类型编码
				String BgtTypeName = elementVoucher.elementText("BgtTypeName");// 预算类型名称
				String FundTypeCode = elementVoucher
						.elementText("FundTypeCode");// 资金性质编码
				String FundTypeName = elementVoucher
						.elementText("FundTypeName");// 资金性质名称
				String PayTypeCode = elementVoucher.elementText("PayTypeCode");// 支付方式编码
				String PayTypeName = elementVoucher.elementText("PayTypeName");// 支付方式名称
				String AgentAcctNo = elementVoucher.elementText("AgentAcctNo");// 原收款银行账号
				String AgentAcctName = elementVoucher
						.elementText("AgentAcctName");// 原收款银行账户名称
				String AgentAcctBankName = elementVoucher
						.elementText("AgentAcctBankName");// 原收款银行
				String ClearAcctNo = elementVoucher.elementText("ClearAcctNo");// 原付款账号
				String ClearAcctName = elementVoucher
						.elementText("ClearAcctName");// 原付款账户名称
				String ClearAcctBankName = elementVoucher
						.elementText("ClearAcctBankName");// 原付款银行
				String PayAmt = elementVoucher.elementText("PayAmt");// 汇总清算金额
//				String ReturnDate = elementVoucher.elementText("ReturnDate");	//商行实际退款日期
				String verifyPayAmt = PayAmt;
				String PayBankName = elementVoucher.elementText("PayBankName");// 代理银行名称
				String PayBankNo = elementVoucher.elementText("PayBankNo");// 代理银行行号
				// 通过前置提交集中支付清算
				if (StateConstant.COMMON_YES
						.equals(ITFECommonConstant.ISITFECOMMIT)) {
					payDictateNo = elementVoucher.elementText("PayDictateNo");// 大额支付退款交易序号
					payMsgNo = elementVoucher.elementText("PayMsgNo");// 支付报文编号
					payEntrustDate = elementVoucher
							.elementText("PayEntrustDate");// 支付委托日期
					// paySndBnkNo =
					// elementVoucher.elementText("PaySndBnkNo");// 支付发起行行号
				}
				String Remark = elementVoucher.elementText("Remark");// 摘要
				if (Remark != null
						&& new String(Remark.getBytes("GBK"), "iso-8859-1")
								.length() > 200)
					Remark = Remark.substring(0, 100);
				String MoneyCorpCode = elementVoucher
						.elementText("MoneyCorpCode");// 金融机构编码
				String XPaySndBnkNo = elementVoucher
						.elementText("XPaySndBnkNo");// 支付发起行行号
				String XAddWord = elementVoucher.elementText("XAddWord");// 附言
				String XClearDate = elementVoucher.elementText("XClearDate");// 清算日期
//				String XPayAmt = elementVoucher.elementText("XPayAmt");// 汇总清算金额
				String Hold1 = elementVoucher.elementText("Hold1");// 预留字段1
//				String Hold2 = elementVoucher.elementText("Hold2");// 预留字段2
				String tklx = "";
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					tklx = elementVoucher.elementText("TKLX"); // 上海增加 TKLX：退款类型
				}

				/**
				 * 组装TvPayreckBankBackDto对象
				 **/
				maindto = new TvPayreckBankBackDto();
				mainvou = dealnos.get(VoucherNo);// 获取序列号
				
				//查找凭证索引表
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(
						vDto).get(0);
				
				
				maindto.setStrano(mainvou.substring(8, 16));// 申请划款凭证Id
				maindto.setIvousrlno(Long.valueOf(mainvou));// 凭证流水号
				maindto.setSid(Id);// 申请划款凭证Id
				maindto.setSadmdivcode(AdmDivCode);// 行政区划代码
				maindto.setSofyear(StYear);// 业务年度
				maindto.setSvtcode(VtCode);// 凭证类型编号
//				SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
				maindto.setDvoudate(CommonUtil.strToDate(VouDate)); // 凭证日期
				maindto.setSvouno(VoucherNo);// 凭证号
				maindto.setSbookorgcode(ls_OrgCode);// 核算主体代码
				maindto.setStrecode(TreCode); // 国库主体代码
				maindto.setSfinorgcode(FinOrgCode);// 财政机关代码
				maindto.setSbgttypecode(BgtTypeCode);// 预算类型编码
				maindto.setSbgttypename(BgtTypeName);// 预算类型名称
				maindto.setSfundtypecode(FundTypeCode);// 资金性质编码
				maindto.setSfundtypename(FundTypeName);// 资金性质名称
				maindto.setSpaytypecode(PayTypeCode);// 支付方式编码
				if("12".equals(PayTypeCode)|| PayTypeCode.startsWith("001002")){//财政普遍采用的是6位
					maindto.setSpaymode("1");// 授权支付
				}else if("11".equals(PayTypeCode) || PayTypeCode.startsWith("001001")){//财政普遍采用的是6位
					maindto.setSpaymode("0");// 支付方式编码    直接支付
				}
				maindto.setSpaytypename(PayTypeName);// 支付方式名称
				maindto.setSpayeeacct(AgentAcctNo);// 收款银行账号
				maindto.setSpayeename(AgentAcctName);// 收款银行账户名称
				maindto.setSagentacctbankname(AgentAcctBankName);// 收款银行
				maindto.setSpayeracct(ClearAcctNo);// 付款账号
				maindto.setSpayername(ClearAcctName);// 付款账户名称
				maindto.setSclearacctbankname(ClearAcctBankName);// 付款银行
				// 金额取绝对值
				Double dpayamt = Math.abs(Double.valueOf(PayAmt));
				PayAmt = new DecimalFormat("#.00").format(dpayamt);

				maindto.setFamt(new BigDecimal(PayAmt));// 汇总清算金额
				maindto.setSpaybankname(PayBankName);// 代理银行名称
				maindto.setSagentbnkcode(PayBankNo);// 代理银行行号
				maindto.setSremark(Remark);// 摘要
				maindto.setSmoneycorpcode(MoneyCorpCode);// 金融机构编码
				maindto.setSxpaysndbnkno(XPaySndBnkNo);// 支付发起行行号
				maindto.setSaddword(XAddWord);// 附言
				maindto.setSxcleardate(CommonUtil.strToDate(XClearDate));// 清算日期
				maindto.setSxpayamt(BigDecimal.valueOf(Double
						.valueOf(verifyPayAmt)));
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					maindto.setShold1(tklx);// 上海增加 TKLX：退款类型 增加至hold1字段
				} else {
					maindto.setShold1(Hold1);// 预留字段1
				}
				maindto.setDentrustdate(DateUtil.currentDate());// 委托日期
				maindto.setDorientrustdate(DateUtil.currentDate());// 原委托日期
				maindto.setSoritrano("");// 原交易流水号
				maindto.setDacceptdate(DateUtil.currentDate());// 接收日期
				maindto.setSpackno("0000");// 包流水号
				maindto.setStrimsign(MsgConstant.TIME_FLAG_NORMAL);// 调整期标志
				if ("000073100012".equals(ITFECommonConstant.SRC_NODE)) {// 湖南资金性质作为预算种类
					maindto.setSbudgettype(FundTypeCode);
				}else if(TreCode.startsWith("06")){//沈阳按照收款账户分为预算内、外
					if(ClearAcctNo.endsWith("271001") || MsgConstant.BDG_KIND_IN.equals(FundTypeCode)){
						maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);//预算种类(默认预算内)
					}else{
						maindto.setSbudgettype(MsgConstant.BDG_KIND_OUT);//预算种类(默认预算内)
					}
				} else {
					maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);// 预算种类(默认预算内)
					TsInfoconnorgaccPK tmppk = new TsInfoconnorgaccPK();
					tmppk.setSorgcode(ls_OrgCode); // 核算主体
					tmppk.setStrecode(TreCode); // TreCode国库主体
					tmppk.setSpayeraccount(ClearAcctNo);// 付款账号
					TsInfoconnorgaccDto resultdto = null;
					try {
						resultdto = (TsInfoconnorgaccDto) DatabaseFacade.getDb().find(tmppk);
						if(resultdto!=null&&resultdto.getSbiztype()!=null&&!"".equals(resultdto.getSbiztype()))
						{
							maindto.setSbudgettype(resultdto.getSbiztype());
						}
					} catch (JAFDatabaseException e) {
						logger.error("查询库款账户异常:" + e.getMessage());
					}
				}
				maindto.setDorivoudate(CommonUtil.strToDate(TimeFacade
						.getCurrentStringTimebefor()));// 原凭证日期
				maindto.setSorivouno(VoucherNo);// 原凭证编号
				maindto.setSpaysndbnkno(PayBankNo);// 收款人开户行行号
				maindto.setDentrustdate(TimeFacade.getCurrentDateTime());
				// 通过前置提交集中支付清算
				if (StateConstant.COMMON_YES
						.equals(ITFECommonConstant.ISITFECOMMIT)) {
					maindto.setSpaydictateno(payDictateNo);// 大额支付退款交易序号
					maindto.setSpaymsgno(payMsgNo);// 支付报文编号
					maindto.setDpayentrustdate(CommonUtil
							.strToDate(payEntrustDate));// 支付委托日期
					// maindto.setSpaysndbnkno(paySndBnkNo);// 支付发起行行号
					String errPayMsg = verify(payDictateNo, payMsgNo,
							payEntrustDate);
					if (errPayMsg != null) {
						voucher.voucherComfail(vDto.getSdealno(), errPayMsg);
						continue;
					}
				}
//				if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0 && StringUtils.isBlank(ReturnDate)){
//					voucher.voucherComfail(vDto.getSdealno(), "ReturnDate不能为空!");
//					continue;
//				}
//				maindto.setShold2(ReturnDate);// 预留字段2	保存上海实际退款日期
//				vDto.setShold4(ReturnDate);	//存储实际退款日期
				
				maindto.setSfilename(ls_FileName);
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);// 状态
				// 处理中
				maindto.setTsupdate(new Timestamp(new java.util.Date()
						.getTime()));// 系统时间
			

				// 预算单位代码list
				agencyCodeList = new ArrayList<String>();
				// 预算科目代码list
				expFuncCodeList = new ArrayList<String>();
				/**
				 * 组装TvPayreckBankListDto对象
				 */
				subDtoList = new ArrayList<TvPayreckBankBackListDto>();
				// 子表明细Id集合
				List<String> subDtoIdList = new ArrayList<String>();
				String sdetailId = null;// 明细Id
				boolean breakfor = false;
				StringBuffer verifyVoucherNoSql = new StringBuffer("");//校验sql
				List<String> paramList = new ArrayList<String>();
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);
					/**
					 * 校验报文明细Id节点是否存在 1、若节点不存在，是老版本，不更新明细Id 2、若节点存在，是新版本，更新明细Id
					 */
					Element sdetailIdElement = elementDetail.element("Id");
					// 节点不存在
					if (sdetailIdElement == null)
						subDtoIdList.add("节点不存在");
					else
						sdetailId = sdetailIdElement.getText();// 明细Id
					String VoucherNol = elementDetail.elementText("VoucherNo");// 支付凭证单号
					String OriVoucherNo = "";// 原支付凭证单号
					String OriVouDetailNo = "";// 原支付凭证明细单号
					String ReturnDate = "";
					if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
						ReturnDate = elementDetail.elementText("ReturnDate");	//商行实际退款日期
						OriVoucherNo = elementDetail
								.elementText("OriVoucherNo");// 原支付凭证单号
						OriVouDetailNo = elementDetail
								.elementText("OriVouDetailNo");// 原支付凭证明细单号
						//如果正常退款  OriVoucherNo原支付凭证单号和OriVouDetailNo原支付凭证明细单号  不能为空
						if(StringUtils.isNotBlank(tklx)){
							if(StateConstant.TKLX_1.equals(tklx) && (StringUtils.isBlank(OriVoucherNo) || StringUtils.isBlank(OriVouDetailNo))){
								voucher.voucherComfail(mainvou, "凭证编号：" + VoucherNo + "，明细Id为：" + sdetailId + "中的原支付凭证单号或原支付凭证明细单号不能为空！");
								breakfor = true;
								break;
							}
						}else{
							voucher.voucherComfail(mainvou, "凭证编号：" + VoucherNo + "退款类型(TKLX)不能为空！");
							breakfor = true;
							break;
						}
						if(StringUtils.isBlank(ReturnDate)){
							voucher.voucherComfail(vDto.getSdealno(), "ReturnDate不能为空!");
							breakfor = true;
							break;
						}
						
						
					}
					SupDepCode = elementDetail.elementText("SupDepCode");// 一级预算单位编码
					String SupDepName = elementDetail.elementText("SupDepName");// 一级预算单位名称
					if(new String(SupDepName.getBytes("GBK"),"iso-8859-1").length()>60)
						SupDepName = CommonUtil.subString(SupDepName,60);
					String ExpFuncCode = elementDetail
							.elementText("ExpFuncCode");// 支出功能分类科目编码
					String ExpFuncName = elementDetail
							.elementText("ExpFuncName");// 支出功能分类科目名称
					String sPayAmt = elementDetail.elementText("PayAmt");// 支付金额
					String PaySummaryName = elementDetail
							.elementText("PaySummaryName");// 摘要名称
					
					String sdetailHold1 = elementDetail.elementText("Hold1");// 预留字段1
					String sdetailHold2 = elementDetail.elementText("Hold2");// 预留字段2
					String sdetailHold3 = elementDetail.elementText("Hold3");// 预留字段3
					String sdetailHold4 = elementDetail.elementText("Hold4");// 预留字段4
					sdetailHold1 = sdetailHold1.getBytes().length>=42 ? CommonUtil.subString(sdetailHold1, 42) : sdetailHold1;
					sdetailHold2 = sdetailHold2.getBytes().length>=42 ? CommonUtil.subString(sdetailHold2, 42) : sdetailHold1;
					sdetailHold3 = sdetailHold3.getBytes().length>=900 ? CommonUtil.subString(sdetailHold3, 900) : sdetailHold1;
					sdetailHold4 = sdetailHold4.getBytes().length>=42 ? CommonUtil.subString(sdetailHold4, 42) : sdetailHold1;
					
					/****************广州新增字段修改20161020****************/
					String DAVoucherNo      = elementDetail.elementText("DAVoucherNo"); //原支付申请序号      
					String FundTypeCodeSub     = elementDetail.elementText("FundTypeCode"); //资金性质编码        
					String FundTypeNameSub     = elementDetail.elementText("FundTypeName"); //资金性质名称        
					String AgencyCode       = elementDetail.elementText("AgencyCode"); //基层预算单位编码    
					String AgencyName       = elementDetail.elementText("AgencyName"); //基层预算单位名称    
					String ExpEcoCode       = elementDetail.elementText("ExpEcoCode"); //支出经济分类科目编码
					String ExpEcoName       = elementDetail.elementText("ExpEcoName"); //支出经济分类科目名称
					String PayeeAcctNo      = elementDetail.elementText("PayeeAcctNo"); //原收款人账号        
					String PayeeAcctName    = elementDetail.elementText("PayeeAcctName"); //原收款人名称        
					String PayeeAcctBankName= elementDetail.elementText("PayeeAcctBankName"); //原收款人银行        
					String PayeeAcctBankNo  = elementDetail.elementText("PayeeAcctBankNo"); //原收款人银行行号    
					String RemarkSub           = elementDetail.elementText("Remark"); //备注  
					/****************广州新增字段修改20161020****************/
					
					TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
					// 此处的设值待确认
					subdto.setIseqno(j + 1);
					subdto.setSid(sdetailId);
					subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);// 账户性质
					subdto.setIvousrlno(Long.valueOf(mainvou));// 子表序列号
					subdto.setSvoucherno(VoucherNol);// 子表明细序号
					verifyVoucherNoSql.append(" (c.S_VOUCHERNO=?) or");
					paramList.add(VoucherNol);
					// 支付凭证单号
					subdto.setSbdgorgcode(SupDepCode);// 一级预算单位编码
					subdto.setSsupdepname(SupDepName);// 一级预算单位名称
					subdto.setSfuncbdgsbtcode(ExpFuncCode);// 支出功能分类科目编码
					subdto.setSexpfuncname(ExpFuncName);// 支出功能分类科目名称
					subdto.setSecnomicsubjectcode("");// 经济科目代码
					// 金额取绝对值
					Double dpayamtsub = Math.abs(Double.valueOf(sPayAmt));
					sPayAmt = new DecimalFormat("#.00").format(dpayamtsub);
					subdto.setFamt(new BigDecimal(sPayAmt));// 支付金额
					subdto.setSpaysummaryname(PaySummaryName);// 摘要名称
					subdto.setShold1(sdetailHold1);// 预留字段1
					subdto.setShold2(sdetailHold2);// 预留字段2
					subdto.setShold3(sdetailHold3);// 预留字段3
					subdto.setShold4(ReturnDate);// 预留字段4
					subdto.setTsupdate(new Timestamp(new java.util.Date()
							.getTime()));// 更新时间
					subdto.setSorivouno(OriVoucherNo);// 原支付凭证单号
					subdto.setSorivoudetailno(OriVouDetailNo);// 原支付凭证明细单号
					// 设置预算单位、科目LIST,明细合计

					if (!agencyCodeList.contains(TreCode + SupDepCode)) {
						agencyCodeList.add(TreCode + SupDepCode);
					}
					expFuncCodeList.add(ExpFuncCode);
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double
							.valueOf(sPayAmt)));
					
					/****************广州新增字段修改20161020****************/
					//暂时通过判断新增必填字段是否有值来确定填充字段的来源，后期可以通过Area或者节点号等方式来判断
					if(StringUtils.isNotBlank(FundTypeCodeSub) && StringUtils.isNotBlank(FundTypeNameSub)) {
						subdto.setSdavoucherno(DAVoucherNo);//原支付申请序号
						subdto.setSfundtypecode(FundTypeCodeSub);//资金性质编码
						subdto.setSfundtypename(FundTypeNameSub);//资金性质名称
						subdto.setSagencycode(AgencyCode);//基层预算单位编码
						subdto.setSagencyname(AgencyName);//基层预算单位名称
						subdto.setSexpecocode(ExpEcoCode);//支出经济分类科目编码
						subdto.setSexpeconame(ExpEcoName);//支出经济分类科目名称
						subdto.setSpayeeacctno(PayeeAcctNo);//原收款人账号
						subdto.setSpayeeacctname(PayeeAcctName);//原收款人名称
						subdto.setSpayeeacctbankname(PayeeAcctBankName);//原收款人银行
						subdto.setSpayeeacctbankno(PayeeAcctBankNo);//原收款人银行行号
						subdto.setSremark(RemarkSub);//备注	
					}
					/****************广州新增字段修改20161020****************/
					
					subDtoList.add(subdto);
					subDtoIdList.add(subdto.getSid());
				}
				//如果 明细中有问题 跳出主循环
				if(breakfor){
					continue;
				}
				/**
				 * 校验明细Id是否为空或重复
				 */
				String checkIdMsg = voucherVerify
						.checkValidSudDtoId(subDtoIdList);
				if (checkIdMsg != null) {
					// 返回错误信息签收失败
					voucher.voucherComfail(mainvou, checkIdMsg);
					continue;
				}
				if(ITFECommonConstant.PUBLICPARAM.contains(",verifyVoucherNo=true,")||ITFECommonConstant.PUBLICPARAM.contains(",verifyid=true,")){
					String sql = "select c.S_VOUCHERNO as S_VOUCHERNO from TV_VOUCHERINFO a,TV_PAYRECK_BANK_BACK_LIST c where a.S_STYEAR=? and (a.S_STATUS <>? and a.S_STATUS <>? and a.S_STATUS <>?) and a.S_trecode=? and a.S_DEALNO = c.I_VOUSRLNO and ("+verifyVoucherNoSql.toString().substring(0,verifyVoucherNoSql.lastIndexOf("or"))+")";
					String mainsql = "select count(*) from (select S_ID from  TV_PAYRECK_BANK_BACK where S_ID=? union select S_ID from HTV_PAYRECK_BANK_BACK where S_ID=?)";
					SQLExecutor execDetail = null;
					try
					{
						execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
						SQLResults result = null;
						if(ITFECommonConstant.PUBLICPARAM.contains(",verifyid=true,"))
						{
							execDetail.addParam(maindto.getSid());
							execDetail.addParam(maindto.getSid());
							result=  execDetail.runQuery(mainsql);//查询数据
							if(result!=null&&result.getInt(0, 0)>0)
							{
								voucher.voucherComfail(mainvou, "主单中id编号有重复！");
								continue;
							}
						}
						if(ITFECommonConstant.PUBLICPARAM.contains(",verifyVoucherNo=true,"))
						{
							execDetail.addParam(maindto.getSofyear());
							execDetail.addParam(DealCodeConstants.VOUCHER_FAIL);
							execDetail.addParam(DealCodeConstants.VOUCHER_RECEIVE_FAIL);
							execDetail.addParam(DealCodeConstants.VOUCHER_ACCEPT);
							execDetail.addParam(maindto.getStrecode());
							if(paramList!=null&&paramList.size()>0)
							{
								for(String temp:paramList)
									execDetail.addParam(temp);
							}
							result=  execDetail.runQuery(sql.toString());//查询数据
							if(result!=null&&result.getRowCount()>0)
							{
								voucher.voucherComfail(mainvou, "明细中支付凭证编号"+result.getString(0, 0)+"已经清算！");
								continue;
							}
						}
						if(execDetail!=null)
							execDetail.closeConnection();
					}catch (Exception e2) {
						logger.error(e2.getMessage(),e2);
						continue;
					}finally
					{
						if(execDetail!=null)
							execDetail.closeConnection();
					}
				}
				/**
				 * 组装verifydto,进行报文校验
				 */
				verifydto.setTrecode(TreCode);
				verifydto.setFinorgcode(FinOrgCode);
				verifydto.setVoucherno(VoucherNo);
				verifydto.setVoudate(VouDate);
				verifydto.setPaytypecode(PayTypeCode);
				verifydto.setPaybankno(PayBankNo);
				verifydto.setAgentAcctNo(AgentAcctNo);
				verifydto.setAgentAcctName(AgentAcctName);
				verifydto.setClearAcctNo(ClearAcctNo);
				verifydto.setClearAcctName(ClearAcctName);
				// 增加年度，总金额的校验 by renqingbin
				verifydto.setOfyear(StYear);
				verifydto.setFamt(verifyPayAmt);
				//校验预算种类
				verifydto.setBudgettype(maindto.getSbudgettype());
				//核算主体
				verifydto.setOrgcode(maindto.getSbookorgcode());
				String returnmsg = voucherVerify.checkValid(verifydto,
						MsgConstant.VOUCHER_NO_2302);
				if (returnmsg != null) {// 返回错误信息签收失败
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}
				/**
				 *校验主表金额是否与子表金额相等
				 * 
				 */
				if (maindto.getFamt().compareTo(sumAmt) != 0) {
					String errMsg = "主单金额与明细累计金额不相等，主单金额：" + maindto.getFamt()
							+ " 明细累计金额： " + sumAmt;
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				/**
				 *明细条数不能超过500
				 * 
				 */
				if (subDtoList.size() > 499) {
					String errMsg = "明细条数必须小于500!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}
				maindto.setIstatinfnum(subDtoList.size());// 明细信息条数
				/**
				 * 业务表入库，校验报文合法性
				 * 
				 * 业务主表，子表入库失败即签收失败
				 */
				TvPayreckBankBackListDto[] subDtos = new TvPayreckBankBackListDto[subDtoList
						.size()];
				subDtos = (TvPayreckBankBackListDto[]) subDtoList
						.toArray(subDtos);
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(subDtos);
				vDto.setIcount(subDtoList.size());
				vDto.setSext1(maindto.getSpaymode());//支付方式
				if(ITFECommonConstant.PUBLICPARAM.contains(",quotabudgettype,"))
				{
					vDto.setSext4(maindto.getSbudgettype());
				}
				DatabaseFacade.getODB().update(vDto);
			} catch (Exception e) {
				logger.error(e);
				voucher.voucherComfail(mainvou, "报文不规范：" + e.getMessage());
				continue;
			}
			// 签收成功
			try {
				voucher.voucherConfirmSuccess(vDto.getSdealno());
			} catch (ITFEBizException e) {
				logger.error(e);
				continue;
			}

			list = new ArrayList();
			list.add(maindto);
			list.add(vDto);
			list.add(expFuncCodeList);
			list.add(agencyCodeList);// 预算单位list
			lists.add(list);
		}

		/**
		 * 校验凭证信息模块
		 */
		try {
			if (lists.size() > 0) {
				voucher.voucherVerify(lists, VtCode);
			}

		} catch (ITFEBizException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
		}
		return;
	}

	public String verify(String PayDictateNo, String PayMsgNo,
			String PayEntrustDate) {
		if (StringUtils.isBlank(PayDictateNo)) {
			return "支付交易序号不能为空。";
		}
		if (StringUtils.isBlank(PayMsgNo)) {
			return "支付报文编号不能为空。";
		}
		if (StringUtils.isBlank(PayEntrustDate)) {
			return "支付委托日期不能为空。";
		}
		return null;
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}

}

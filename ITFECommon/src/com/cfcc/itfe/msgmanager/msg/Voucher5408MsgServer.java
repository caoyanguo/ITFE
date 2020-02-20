package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvNontaxsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;

public class Voucher5408MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5671MsgServer.class);
	private Voucher voucher;

	/**
	 * 代理行发往人行的非税凭证5408，此类只保存业务数据
	 */
	@SuppressWarnings( { "static-access", "unchecked" })
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String, String> dealnos = (HashMap<String, String>) muleMessage
				.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("解析非税收入缴库单5408报文出现错误！", e);
			throw new ITFEBizException("解析非税收入缴库单5408报文出现错误！", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String StYear = "";// 年度
		String VtCode = "";// 凭证类型

		// 获取行政区划代码、年度和凭证类型
		if (VoucherBodyList.size() > 0) {
			// VoucherBody
			Element element = (Element) VoucherBodyList.get(0);
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}

		// 预算科目代码list
		ArrayList<String> expFuncCodeList = null;

		// String sAgencyCode ="";
		TvNontaxmainDto maindto = null;
		ArrayList<TvNontaxsubDto> subDtoList = null;
		List lists = new ArrayList();
		List<Object> list = null;
		// 凭证序列号
		String mainvou = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		// 解析报文
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {
				String errmessage = null;
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
				String Id = elementVoucher.elementText("Id");// 财政授权支付汇总清算额度通知单Id
				String VouDate = elementVoucher.elementText("VouDate"); // 凭证日期
				String FinOrgCode = elementVoucher.elementText("FinOrgCode");// 财政机关代码
				String TreCode = elementVoucher.elementText("TreCode"); // 国库主体代码
				String PayeeAcctNo = elementVoucher.elementText("ClearAcctNo"); // 收款人账号
				if(isNull(PayeeAcctNo))
					errmessage = "收款人账号不可为空!";
				String PayeeAcctName = elementVoucher.elementText("ClearAcctName"); // 收款人名称
				if(isNull(PayeeAcctName))
					errmessage = "收款人名称不可为空!";
				String PayeeAcctBankName = elementVoucher.elementText("ClearAcctBankName"); // 收款人开户行
				if(isNull(PayeeAcctBankName))
					errmessage = "收款人开户行不可为空!";
				String BudgetLevelCode = elementVoucher.elementText("BudgetLevelCode"); // 预算级次编码
				String BudgetLevelName = elementVoucher.elementText("BudgetLevelName"); // 预算级次名称
				String FundTypeCode = elementVoucher.elementText("FundTypeCode"); // 资金性质编码
				String FundTypeName = elementVoucher.elementText("FundTypeName"); // 资金性质名称
				String PayBankCode = elementVoucher.elementText("PayBankCode"); // 代理银行代码
				if(isNull(PayBankCode))
					errmessage = "代理银行代码不可为空!";
				String PayBankName = elementVoucher.elementText("PayBankName"); // 代理银行名称
				if(isNull(PayBankName))
					errmessage = "代理银行名称不可为空!";
				String PayBankNo = elementVoucher.elementText("PayBankNo"); // 付款行行号
				if(isNull(PayBankNo))
					errmessage = "付款行行号不可为空!";
				String AgentAcctName = elementVoucher.elementText("AgentAcctName"); // 付款人名称
				if(isNull(AgentAcctName))
					errmessage = "付款人名称不可为空!";
				String AgentAcctNo = elementVoucher.elementText("AgentAcctNo"); // 付款人账号
				if(isNull(AgentAcctNo))
					errmessage = "付款人账号不可为空!";
				String AgentAcctBankName = elementVoucher.elementText("AgentAcctBankName"); // 付款人开户行
				if(isNull(AgentAcctBankName))
					errmessage = "付款人开户行不可为空!";
				String PayDictateNo = null;
				String PayMsgNo = null;
				if(TreCode.startsWith("06")){
					PayDictateNo = elementVoucher.elementText("XPBPayDictateNo"); // 支付交易序号
					PayMsgNo = elementVoucher.elementText("XPBPayMsgNo"); // 支付报文编号
				}else{
					PayDictateNo = elementVoucher.elementText("PayDictateNo"); // 支付交易序号
					PayMsgNo = elementVoucher.elementText("PayMsgNo"); // 支付报文编号
				}
				if(isNull(PayDictateNo))
					errmessage = "支付交易序号不可为空!";
				
				if(isNull(PayMsgNo))
					errmessage = "支付报文编号不可为空!";
//				String PayEntrustDate = elementVoucher.elementText("PayEntrustDate"); // 支付委托日期
				String XAddWord = elementVoucher.elementText("XAddWord"); // 附言
				String SumAmt = elementVoucher.elementText("SumAmt"); // 缴库金额
				String XAcctDate = elementVoucher.elementText("XAcctDate"); // 实际支付日期
				String XSumAmt = elementVoucher.elementText("XSumAmt"); // 入库金额
				String Hold1 = elementVoucher.elementText("Hold1"); // 预留字段1
				String Hold2 = elementVoucher.elementText("Hold2"); // 预留字段2
				String Remark = elementVoucher.elementText("Remark"); // 备注
				String TaxOrgCode = elementVoucher.elementText("TaxOrgCode"); // 征收机关代码
				String TaxOrgName = elementVoucher.elementText("TaxOrgName"); // 征收机关名称
				
				/**
				 * 组装TvGrantpaymsgmainDto对象
				 **/
				maindto = new TvNontaxmainDto();
				maindto.setScommitdate(TimeFacade.getCurrentStringTime());
				mainvou = dealnos.get(VoucherNo).toString();
				maindto.setSorgcode(ls_OrgCode);//核算主体代码
				maindto.setSdealno(mainvou);// 流水号
				maindto.setSid(Id);// 缴库单Id
				maindto.setSvoudate(VouDate); // 凭证日期
				maindto.setSvoucherno(VoucherNo);
				maindto.setSfinorgcode(FinOrgCode);// 财政机关代码
				maindto.setStrecode(TreCode); // 国库主体代码
				maindto.setSpayeeacctno(PayeeAcctNo); // 收款人账号
				maindto.setSpayeeacctnane(PayeeAcctName); // 收款人名称
				maindto.setSpayeeacctbankname(PayeeAcctBankName); // 收款人开户行
				maindto.setSbudgetlevelcode(BudgetLevelCode); // 预算级次编码
				maindto.setSbudgetlevelname(BudgetLevelName); // 预算级次名称
				maindto.setShold3(FundTypeCode);//资金性质编码
				maindto.setShold4(FundTypeName);//资金性质名称
				maindto.setSpayacctname(AgentAcctName); // 付款人名称
				maindto.setSpayacctno(AgentAcctNo); // 付款人账号
				maindto.setSpayacctbankname(AgentAcctBankName); // 付款人开户行
				maindto.setSpaybankno(PayBankNo); // 付款行行号
				maindto.setSpaybankcode(PayBankCode);
				maindto.setSpaybankname(PayBankName);
				maindto.setSpaymsgno(PayMsgNo);
				maindto.setSpaydictateno(PayDictateNo);
				maindto.setSxaddword(XAddWord); // 附言
				maindto.setSxtradate(XAcctDate); // 实际支付日期
				maindto.setStaxorgcode(TaxOrgCode);// 征收机关代码
				if(maindto.getStaxorgcode()==null||"".equals(maindto.getStaxorgcode())||"null".equals(maindto.getStaxorgcode())||"NULL".equals(maindto.getStaxorgcode()))
					maindto.setStaxorgcode(Hold1);
				
				maindto.setStaxorgname(TaxOrgName);// 征收机关名称
				if(XSumAmt==null || XSumAmt.equals(""))
					XSumAmt = "0.00";
				maindto.setNxmoney(BigDecimal.valueOf(Double.valueOf(XSumAmt))); // 入库金额
//				maindto.setShold1(Hold1); // 预留字段1-资金收纳流水号用
				maindto.setShold2(Hold2); // 预留字段2
				maindto.setSdemo(Remark);// 备注
				maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(SumAmt))); // 缴库金额
				maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
				maindto.setStrimflag(StateConstant.TRIMSIGN_FLAG_NORMAL);
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
				
				if(TreCode.startsWith("06")){
					String PayEntrustDate = elementVoucher.elementText("PayEntrustDate"); // 支付委托日期
//					String FinCode = elementVoucher.elementText("FinCode"); // 金融机构编码
//					String PeriodType = elementVoucher.elementText("PeriodType"); // 业务期间类型名称
					String PeriodTypeCode = elementVoucher.elementText("PeriodTypeCode"); // 业务期间类型代码
					String BusinessMode = elementVoucher.elementText("BusinessMode"); // 业务模式类型
//					String XPBAddWord = elementVoucher.elementText("XPBAddWord"); // 代理银行附言
//					String XPBAcctDate = elementVoucher.elementText("XPBAcctDate"); // 代理银行处理日期
//					String XPBSumAmt = elementVoucher.elementText("XPBSumAmt"); // 代理银行缴库金额
//					String XPBPayDictateNo = elementVoucher.elementText("XPBPayDictateNo"); // 支付交易序号
//					String XPBPayMsgNo = elementVoucher.elementText("XPBPayMsgNo"); // 支付报文编号
					if(maindto.getStaxorgcode()==null||"".equals(maindto.getStaxorgcode())||"null".equals(maindto.getStaxorgcode())||"NULL".equals(maindto.getStaxorgcode()))
						maindto.setStaxorgcode(FinOrgCode);
					
					if(PeriodTypeCode.equals("01")){//正常业务
						maindto.setStrimflag(StateConstant.TRIMSIGN_FLAG_NORMAL);	
					}else{
						maindto.setStrimflag(StateConstant.TRIMSIGN_FLAG_TRIM);	//调整期业务
					}
//					maindto.setScommitdate(PayEntrustDate);
				}
				
				TsTreasuryDto streDto = new TsTreasuryDto();
				streDto.setStrecode(TreCode);
//				List<TsTreasuryDto>	streDtoList = null;
//				try {
//					streDtoList = CommonFacade.getODB().findRsByDto(streDto);
//				} catch (ValidateException e) {
//					logger.error(e + "查询国库信息失败！");
//				}
				// 预算科目代码list
				expFuncCodeList = new ArrayList<String>();
				/**
				 * 组装TvPayoutmsgsubDto对象
				 */
				subDtoList = new ArrayList<TvNontaxsubDto>();
				boolean isfail = false;
				for (int j = 0; j < listDetail.size(); j++) {

					Element elementDetail = (Element) listDetail.get(j);
					Element sdetailIdElement = elementDetail.element("Id");
					String SubId = "";
					// 节点不存在
					if (sdetailIdElement != null)
						SubId = elementDetail.elementText("Id");// 明细ID

					String IncomeSortCode = elementDetail.elementText("IncomeSortCode");// 收入分类科目编码
					String IncomeSortName = elementDetail.elementText("IncomeSortName");// 收入分类科目名称
					String subRemark = elementDetail.elementText("Remark");// 备注
					String Amt = elementDetail.elementText("Amt");// 缴库金额
					String sdetailHold1 = elementDetail.elementText("Hold1");// 预留字段1
					if(!TreCode.startsWith("06") && isNull(sdetailHold1))
						errmessage = "缴款识别码不可为空!";
					String sdetailHold2 = elementDetail.elementText("Hold2");// 预留字段2
					if(!TreCode.startsWith("06") && isNull(sdetailHold2))
						errmessage = "缴款人全称不可为空!";
					String sdetailHold3 = elementDetail.elementText("Hold3");// 预留字段3
					if(!TreCode.startsWith("06") && isNull(sdetailHold3))
						errmessage = "收款人账号不可为空!";
					String sdetailHold4 = elementDetail.elementText("Hold4");// 预留字段4
					if(!TreCode.startsWith("06") && isNull(sdetailHold4))
						errmessage = "执收单位名称不可为空!";
					String ViceSign = elementDetail.elementText("ViceSign");// 辅助标志
					TvNontaxsubDto subdto = new TvNontaxsubDto();
					subdto.setSid(SubId);
					subdto.setSdealno(mainvou);
					subdto.setSseqno(String.valueOf(j));
					subdto.setNtraamt(BigDecimal.valueOf(Double.valueOf(Amt)));// 缴库金额
					subdto.setSfundtypecode(FundTypeCode);// 资金性质编码
					subdto.setSbudgetsubject(IncomeSortCode);
					subdto.setSbudgetname(IncomeSortName);
					subdto.setSitemcode(IncomeSortCode);//收入分类科目编码
					subdto.setSitemname(IncomeSortName);//收入分类科目名称
					subdto.setNdetailamt(BigDecimal.valueOf(Double.valueOf(Amt)));// 明细金额
					subdto.setShold1(sdetailHold1);// 缴款识别码
					if(TreCode.startsWith("06")){
						subdto.setShold1(VoucherNo);// 缴款识别码
					}
					subdto.setShold2(sdetailHold2);// 缴款人全称
					subdto.setShold3(sdetailHold3);// 收款人账号
					subdto.setShold4(sdetailHold4);// 执收单位名称
					subdto.setSvicesign(ViceSign);// 辅助标志
					subdto.setSxaddword(subRemark);//预算级次
					if(subRemark!=null&&"0".equals(subRemark))//级次为共享
					{
						if(ViceSign==null||"".equals(ViceSign))
							isfail = true;
					}else
					{
						if(ViceSign!=null&&!"".equals(ViceSign))
							isfail = true;
					}
					expFuncCodeList.add(IncomeSortCode);
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(Amt)));
					subDtoList.add(subdto);
				}
				maindto.setScount(String.valueOf(subDtoList==null?0:subDtoList.size()));
				// 根据交易流水号取得凭证索引表dto
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(
						vDto).get(0);
				if(isfail)
				{
					voucher.voucherComfail(vDto.getSdealno(), "预算级次和辅助标志不对应!");
					continue;
				}
				if(errmessage!=null&&errmessage.length()>5)
				{
					voucher.voucherComfail(vDto.getSdealno(), errmessage);
					continue;
				}
				/**
				 * 组装verifydto,进行报文校验
				 */
				verifydto.setTrecode(TreCode);
				verifydto.setFinorgcode(FinOrgCode);
				verifydto.setVoucherno(VoucherNo);
				verifydto.setVoudate(VouDate);
				verifydto.setPaybankno(PayBankNo);
				// 增加年度、月份，总金额的校验 by renqingbin
				verifydto.setOfyear(StYear);
				verifydto.setFamt(String.valueOf(sumAmt));
				String returnmsg = voucherVerify.checkValid(verifydto,
						MsgConstant.VOUCHER_NO_5408);
				if (returnmsg != null) {// 返回错误信息签收失败
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}
				
				/**
				 *校验主表金额是否与子表金额相等
				 * 
				 */
				if (maindto.getNmoney().compareTo(sumAmt) != 0) {
					String errMsg = "主单金额与明细累计金额不相等，主单金额："
							+ maindto.getNmoney() + " 明细累计金额： " + sumAmt;
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}

				/**
				 *校验凭证编号不能大于17位
				 * 
				 */
				if (maindto.getSvoucherno().getBytes().length > 20) {
					String errMsg = "凭证编号不能大于20位！";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}

				/**
				 *校验主表金额=0.00且明细条数=1
				 * 
				 */
				if (maindto.getNmoney().compareTo(BigDecimal.ZERO) == 0
						&& subDtoList.size() == 1) {
					String errMsg = "主单金额：" + maindto.getNmoney() + " ,明细条数：1";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}

				/**
				 * 业务表入库，校验报文合法性
				 * 
				 * 业务主表，子表入库失败即签收失败
				 */
				TvNontaxsubDto [] subDtos = new TvNontaxsubDto[subDtoList.size()];
				subDtos = (TvNontaxsubDto[]) subDtoList.toArray(subDtos);

				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(subDtos);
				vDto.setIcount(subDtoList.size());
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
				VoucherException voucherE = new VoucherException();
				voucherE.saveErrInfo(VtCode, e);
				throw new ITFEBizException("签收凭证报文" + VtCode + "出现异常", e);
			}

			list = new ArrayList<Object>();
			list.add(maindto);
			list.add(vDto);
			list.add(expFuncCodeList);
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				list.add(subDtoList);
			}
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
			throw new ITFEBizException(e);
		} catch (Exception e) {
			logger.error(e);
			VoucherException voucherE = new VoucherException();
			voucherE.saveErrInfo(VtCode, e);
			throw new ITFEBizException("校验凭证报文" + VtCode + "出现异常", e);
		}
		return;
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}
	private boolean isNull(String code)
	{
		if(code==null||"".equals(code)||"null".equals(code.toLowerCase()))
			return true;
		else
			return false;
	}
}

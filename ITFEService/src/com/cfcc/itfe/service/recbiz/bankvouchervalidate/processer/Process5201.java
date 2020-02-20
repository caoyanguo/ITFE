package com.cfcc.itfe.service.recbiz.bankvouchervalidate.processer;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.service.recbiz.bankvouchervalidate.AbstractBankVoucherValidateProcesser;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 5201解析类
 * 
 * @author hua
 * 
 */
public class Process5201 extends AbstractBankVoucherValidateProcesser {
	public MulitTableDto process(String fullFileName) {
		MulitTableDto result = null;
		result = new MulitTableDto();
		result.setFatherDtos(new ArrayList<IDto>());
		result.setSonDtos(new ArrayList<IDto>());
		List<IDto> mainList = result.getFatherDtos();
		List<IDto> subList = result.getSonDtos();
		List<String> delMainList = result.getVoulist();
		try {
			Element doc = parseFile2Doc(fullFileName).getRootElement();
			if (doc == null) {
				result.getErrorList().add("无法解析的文件, 请查证!.");
				return result;
			}
			List voucherList = doc.selectNodes("Voucher");
			if (voucherList == null || voucherList.size() == 0) {
				result.getErrorList().add("无法识别的报文内容, 找不到<Voucher/>节点!");
				return result;
			}

			out: for (Object voucherTemp : voucherList) {
				Element elementVoucher = (Element) voucherTemp;
				/**
				 * 凭证信息
				 */
				// String VoucherNo = elementVoucher.elementText("VoucherNo");//凭证号
				String id = elementVoucher.elementText("Id");// 财政授权支付凭证Id
				String admDivCode = elementVoucher.elementText("AdmDivCode");// 行政区划代码
				if (StringUtils.isBlank(admDivCode)) {
					result.getErrorList().add(new File(fullFileName).getName() + ": 文件中存在'行政区划代码'为空情况, 请查证!");
					break out;
				}
				String stYear = elementVoucher.elementText("StYear");// 业务年度
				String vtCode = elementVoucher.elementText("VtCode");// 凭证类型编号
				if (!MsgConstant.VOUCHER_NO_5201.equals(vtCode)) {
					result.getErrorList().add("不支持该类型报文导入!");
					break;
				}
				String VoucherNo = elementVoucher.elementText("VoucherNo");// 凭证编号
				if (StringUtils.isBlank(VoucherNo)) {
					result.getErrorList().add(new File(fullFileName).getName() + ": 文件中存在'主单凭证编号'为空情况, 请查证!");
					break out;
				}
				String VouDate = elementVoucher.elementText("VouDate"); // 凭证日期
				String FundTypeCode = elementVoucher.elementText("FundTypeCode"); // 资金性质编码
				String FundTypeName = elementVoucher.elementText("FundTypeName"); // 资金性质名称
				String BgtTypeCode = elementVoucher.elementText("BgtTypeCode");// 预算类型编码
				String BgtTypeName = elementVoucher.elementText("BgtTypeName");// 预算类型名称
				String PayTypeCode = elementVoucher.elementText("PayTypeCode"); // 支付方式编码
				String PayTypeName = elementVoucher.elementText("PayTypeName"); // 支付方式名称
				String ProCatCode = elementVoucher.elementText("ProCatCode");// 收支管理编码
				String ProCatName = elementVoucher.elementText("ProCatName");// 收支管理名称
				String MOFDepCode = elementVoucher.elementText("MOFDepCode");// 业务处室编码
				String MOFDepName = elementVoucher.elementText("MOFDepName");// 业务处室名称
				String FileNoCode = elementVoucher.elementText("FileNoCode");// 指标文号编码
				String FileNoName = elementVoucher.elementText("FileNoName");// 指标文号名称
				String SupDepCode = elementVoucher.elementText("SupDepCode");// 一级预算单位编码
				String SupDepName = elementVoucher.elementText("SupDepName");// 一级预算单位名称
				String AgencyCode = elementVoucher.elementText("AgencyCode"); // 基层预算单位编码
				String AgencyName = elementVoucher.elementText("AgencyName"); // 基层预算单位名称
				String ExpFuncCode = elementVoucher.elementText("ExpFuncCode");// 支出功能分类科目编码
				String ExpFuncName = elementVoucher.elementText("ExpFuncName");// 支出功能分类科目名称
				String ExpFuncCode1 = elementVoucher.elementText("ExpFuncCode1");// 支出功能分类科目编码
				String ExpFuncName1 = elementVoucher.elementText("ExpFuncName1");// 支出功能分类科目名称
				String ExpFuncCode2 = elementVoucher.elementText("ExpFuncCode2");// 支出功能分类科目编码
				String ExpFuncName2 = elementVoucher.elementText("ExpFuncName2");// 支出功能分类科目名称
				String ExpFuncCode3 = elementVoucher.elementText("ExpFuncCode3");// 支出功能分类科目编码
				String ExpFuncName3 = elementVoucher.elementText("ExpFuncName3");// 支出功能分类科目名称
				String ExpEcoCode = elementVoucher.elementText("ExpEcoCode");// 支出经济分类科目编码
				String ExpEcoName = elementVoucher.elementText("ExpEcoName");// 支出经济分类科目名称
				String ExpEcoCode1 = elementVoucher.elementText("ExpEcoCode1");// 支出经济分类科目编码
				String ExpEcoName1 = elementVoucher.elementText("ExpEcoName1");// 支出经济分类科目名称
				String ExpEcoCode2 = elementVoucher.elementText("ExpEcoCode2");// 支出经济分类科目编码
				String ExpEcoName2 = elementVoucher.elementText("ExpEcoName2");// 支出经济分类科目名称
				String DepProCode = elementVoucher.elementText("DepProCode");// 预算项目编码
				String DepProName = elementVoucher.elementText("DepProName");// 预算项目名称
				String SetModeCode = elementVoucher.elementText("SetModeCode");// 结算方式编码
				String SetModeName = elementVoucher.elementText("SetModeName");// 结算方式名称
				String PayBankCode = elementVoucher.elementText("PayBankCode"); // 代理银行编码
				String PayBankName = elementVoucher.elementText("PayBankName"); // 代理银行名称
				String ClearBankCode = elementVoucher.elementText("ClearBankCode");// 清算银行编码
				String ClearBankName = elementVoucher.elementText("ClearBankName");// 清算银行名称
				String PayeeAcctNo = elementVoucher.elementText("PayeeAcctNo");// 收款人账号
				String PayeeAcctName = elementVoucher.elementText("PayeeAcctName");// 收款人名称
				String PayeeAcctBankName = elementVoucher.elementText("PayeeAcctBankName");// 收款人银行
				String PayeeAcctBankNo = elementVoucher.elementText("PayeeAcctBankNo");// 收款人银行行号
				String PayAcctNo = elementVoucher.elementText("PayAcctNo"); // 付款人账号
				String PayAcctName = elementVoucher.elementText("PayAcctName"); // 付款人名称
				String PayAcctBankName = elementVoucher.elementText("PayAcctBankName"); // 付款人银行
				String PaySummaryCode = elementVoucher.elementText("PaySummaryCode");// 用途编码
				String PaySummaryName = elementVoucher.elementText("PaySummaryName");// 用途名称
				String PayAmt = elementVoucher.elementText("PayAmt");// 支付金额
				if (StringUtils.isBlank(PayAmt)) {
					result.getErrorList().add(new File(fullFileName).getName() + ": 文件中存在'支付金额'为空情况, 请查证!");
					break out;
				}
				String BusinessTypeCode = elementVoucher.elementText("BusinessTypeCode"); // 业务类型编码
				String BusinessTypeName = elementVoucher.elementText("BusinessTypeName"); // 业务类型名称
				String CheckNo = elementVoucher.elementText("CheckNo");// 支票号（结算号）
				String XPayDate = elementVoucher.elementText("XPayDate"); // 实际支付日期
				String XAgentBusinessNo = elementVoucher.elementText("XAgentBusinessNo");// 银行交易流水号
				String XCheckNo = elementVoucher.elementText("XCheckNo");// 支票号(结算号)
				String XPayAmt = elementVoucher.elementText("XPayAmt");// 实际支付金额
				String XPayeeAcctBankName = elementVoucher.elementText("XPayeeAcctBankName");// 收款人银行
				String XPayeeAcctNo = elementVoucher.elementText("XPayeeAcctNo");// 收款人账号
				String XPayeeAcctName = elementVoucher.elementText("XPayeeAcctName"); // 收款人全称
				String Hold1 = elementVoucher.elementText("Hold1");// 预留字段1
				String Hold2 = elementVoucher.elementText("Hold2");// 预留字段2

				/**
				 * 组装TfPaymentDetailsmainDto对象
				 **/
				TfDirectpaymsgmainDto maindto = new TfDirectpaymsgmainDto();
				String mainvou = generateVousrlno();
				maindto.setIvousrlno(Long.parseLong(mainvou));
				maindto.setSvoudate(VouDate);
				maindto.setSvtcode(vtCode);
				maindto.setSfundtypecode(FundTypeCode);
				maindto.setSfundtypename(FundTypeName);
				maindto.setSbgttypecode(BgtTypeCode);
				maindto.setSbgttypename(BgtTypeName);
				maindto.setSpaytypename(PayTypeName);
				maindto.setSprocatcode(ProCatCode);
				maindto.setSprocatname(ProCatName);
				maindto.setSmofdepcode(MOFDepCode);
				maindto.setSmofdepname(MOFDepName);
				maindto.setSfilenocode(FileNoCode);
				maindto.setSfilenoname(FileNoName);
				maindto.setSsupdepcode(SupDepCode);
				maindto.setSsupdepname(SupDepName);
				maindto.setSagencycode(AgencyCode);
				maindto.setSagencyname(AgencyName);
				maindto.setSexpfunccode(ExpFuncCode);
				maindto.setSexpfuncname(ExpFuncName);
				maindto.setSexpfunccode1(ExpFuncCode1);
				maindto.setSexpfuncname1(ExpFuncName1);
				maindto.setSexpfunccode2(ExpFuncCode2);
				maindto.setSexpfuncname2(ExpFuncName2);
				maindto.setSexpfunccode3(ExpFuncCode3);
				maindto.setSexpfuncname3(ExpFuncName3);
				maindto.setSexpecocode(ExpEcoCode);
				maindto.setSexpeconame(ExpEcoName);
				maindto.setSexpecocode1(ExpEcoCode1);
				maindto.setSexpeconame1(ExpEcoName1);
				maindto.setSexpecocode2(ExpEcoCode2);
				maindto.setSexpeconame2(ExpEcoName2);
				maindto.setSdepprocode(DepProCode);
				maindto.setSdepproname(DepProName);
				maindto.setSsetmodecode(SetModeCode);
				maindto.setSsetmodename(SetModeName);
				maindto.setSpaybankcode(PayBankCode);
				maindto.setSpaybankname(PayBankName);
				maindto.setSclearbankcode(ClearBankCode);
				maindto.setSclearbankname(ClearBankName);
				maindto.setSpayeeacctno(PayeeAcctNo);
				maindto.setSpayeeacctname(PayeeAcctName);
				maindto.setSpayeeacctbankname(PayeeAcctBankName);
				maindto.setSpayeeacctbankno(PayeeAcctBankNo);
				maindto.setSpayacctno(PayAcctNo);
				maindto.setSpayacctname(PayAcctName);
				maindto.setSpayacctbankname(PayAcctBankName);
				maindto.setSpaysummarycode(PaySummaryCode);
				maindto.setSpaysummaryname(PaySummaryName);
				maindto.setNpayamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));
				maindto.setSbusinesstypecode(BusinessTypeCode);
				maindto.setSbusinesstypename(BusinessTypeName);
				maindto.setScheckno(CheckNo);
				maindto.setSxpaydate(XPayDate);
				maindto.setSxagentbusinessno(XAgentBusinessNo);
				maindto.setSxcheckno(XCheckNo);
				maindto.setNxpayamt(null);
				maindto.setSxpayeeacctbankname(XPayeeAcctBankName);
				maindto.setSxpayeeacctno(XPayeeAcctNo);
				maindto.setSxpayeeacctname(XPayeeAcctName);
				maindto.setShold1(Hold1);
				maindto.setShold2(Hold2);
				if (PayTypeCode != null && "11".equals(PayTypeCode)) {
					maindto.setSpaytypecode("0");// 直接支付
				} else {
					maindto.setSpaytypecode(PayTypeCode);
				}
				maindto.setSid(id);
				maindto.setSvoucherno(VoucherNo);
				maindto.setSstyear(stYear);// 业务年度
				maindto.setSfinorgcode("");// 财政机构代码
				maindto.setSdealno(mainvou.substring(8, 16));// 交易流水号
				maindto.setScommitdate(TimeFacade.getCurrentStringTime());// 委托日期
				maindto.setSfilename(fullFileName);// 文件名
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);// 状态
				maindto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));// 系统时间
				maindto.setSorgcode(getLoginfo().getSorgcode());
				maindto.setStrecode("");
				maindto.setSadmdivcode(admDivCode);
				maindto.setSdemo("");
				maindto.setSbackflag("0");// 退回标志 0-未退回 1-已退回

				mainList.add(maindto);
				// 删除主表规则：行政区划+报文类型+凭证编号+支付金额
				delMainList.add(new StringBuilder()//
						.append(admDivCode)//
						.append("#")//
						.append(vtCode)//
						.append("#")//
						.append(VoucherNo)//
						.append("#")//
						.append(new BigDecimal(PayAmt)).toString());

				List detailList = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");

				/**
				 * 组装TfDirectpaymsgsubDto对象
				 */
				for (int x = 0; x < detailList.size(); x++) {
					Element elementDetail = (Element) detailList.get(x);
					String sdetailId = elementDetail.elementText("Id");// 明细编号
					String VoucherBillId = elementDetail.elementText("VoucherBillId");// 财政直接支付凭证Id
					String VoucherBillNo = elementDetail.elementText("VoucherBillNo");// 财政直接支付凭证单号
					if (StringUtils.isBlank(VoucherBillNo)) {
						result.getErrorList().add(new File(fullFileName).getName() + ": 文件中存在'财政支付授权凭证单号(明细)'为空情况, 请查证!");
						break out;
					}
					String sdetailVoucherNo = elementDetail.elementText("VoucherNo");// 支付申请序号
					String sdetailFundTypeCode = elementDetail.elementText("FundTypeCode");// 资金性质编码
					String sdetailFundTypeName = elementDetail.elementText("FundTypeName");// 资金性质名称
					String sdetailBgtTypeCode = elementDetail.elementText("BgtTypeCode");// 预算类型编码
					String sdetailBgtTypeName = elementDetail.elementText("BgtTypeName");// 预算类型名称
					String sdetailProCatCode = elementDetail.elementText("ProCatCode");// 收支管理编码
					String sdetailProCatName = elementDetail.elementText("ProCatName");// 收支管理名称
					String sdetailPayKindCode = elementDetail.elementText("PayKindCode");// 支出类型编码
					String sdetailPayKindName = elementDetail.elementText("PayKindName");// 支出类型名称
					String sdetailMOFDepCode = elementDetail.elementText("MOFDepCode");// 业务处室编码
					String sdetailMOFDepName = elementDetail.elementText("MOFDepName");// 业务处室名称
					String sdetailFileNoCode = elementDetail.elementText("FileNoCode");// 指标文号编码
					String sdetailFileNoName = elementDetail.elementText("FileNoName");// 指标文号名称
					String sdetailSupDepCode = elementDetail.elementText("SupDepCode");// 一级预算单位编码
					String sdetailSupDepName = elementDetail.elementText("SupDepName");// 一级预算单位名称
					String sdetailAgencyCode = elementDetail.elementText("AgencyCode");// 基层预算单位编码
					String sdetailAgencyName = elementDetail.elementText("AgencyName");// 基层预算单位名称
					String sdetailExpFuncCode = elementDetail.elementText("ExpFuncCode");// 支出功能分类科目编码
					String sdetailExpFuncName = elementDetail.elementText("ExpFuncName");// 支出功能分类科目名称
					String sdetailExpEcoCode = elementDetail.elementText("ExpEcoCode");// 支出经济分类科目编码
					String sdetailExpEcoName = elementDetail.elementText("ExpEcoName");// 支出经济分类科目名称
					String sdetailDepProCode = elementDetail.elementText("DepProCode");// 预算项目编码
					String sdetailDepProName = elementDetail.elementText("DepProName");// 预算项目名称
					String sdetailPayeeAcctNo = elementDetail.elementText("PayeeAcctNo");// 收款人账号
					String sdetailPayeeAcctName = elementDetail.elementText("PayeeAcctName");// 收款人名称
					String sdetailPayeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");// 收款人银行
					String sdetailPayeeAcctBankNo = elementDetail.elementText("PayeeAcctBankNo");// 收款人银行行号
					String sdetailPayAmt = elementDetail.elementText("PayAmt");// 支付金额
					String sdetailRemark = elementDetail.elementText("Remark");// 备注
					String sdetailDetailXPayDate = elementDetail.elementText("XPayDate");// 实际支付日期
					String sdetailXAgentBusinessNo = elementDetail.elementText("XAgentBusinessNo");// 银行交易流水号
					String sdetailXCheckNo = elementDetail.elementText("XCheckNo");// 支票号(结算号)
					String sdetailXPayAmt = elementDetail.elementText("XPayAmt");// 实际支付金额
					String sdetailXPayeeAcctBankName = elementDetail.elementText("XPayeeAcctBankName");// 收款人银行
					String sdetailXPayeeAcctNo = elementDetail.elementText("XPayeeAcctNo");// 收款人账号
					String sdetailXPayeeAcctName = elementDetail.elementText("XPayeeAcctName");// 收款人全称
					String XAddWord = elementDetail.elementText("XAddWord");// 失败原因
					String sdetailHold1 = elementDetail.elementText("Hold1");// 预留字段1
					String sdetailHold2 = elementDetail.elementText("Hold2");// 预留字段2
					String sdetailHold3 = elementDetail.elementText("Hold3");// 预留字段3
					String sdetailHold4 = elementDetail.elementText("Hold4");// 预留字段4

					TfDirectpaymsgsubDto subdto = new TfDirectpaymsgsubDto();
					subdto.setIvousrlno(Long.parseLong(mainvou));
					subdto.setIseqno((long) (x + 1));
					subdto.setSid(sdetailId);
					subdto.setSvoucherbillid(VoucherBillId);
					subdto.setSvoucherbillno(VoucherBillNo);
					// subdto.setSvoucherno(sdetailVoucherNo);
					subdto.setSvoucherno(VoucherNo);
					subdto.setSfundtypecode(FundTypeCode == null ? "" : FundTypeCode);
					subdto.setSfundtypename(FundTypeName == null ? "" : FundTypeName);
					subdto.setSbgttypecode(sdetailBgtTypeCode);
					subdto.setSbgttypename(sdetailBgtTypeName);
					subdto.setSprocatcode(sdetailProCatCode);
					subdto.setSprocatname(sdetailProCatName);
					subdto.setSpaykindcode(sdetailPayKindCode);
					subdto.setSpaykindname(sdetailPayKindName);
					subdto.setSmofdepcode(sdetailMOFDepCode);
					subdto.setSmofdepname(sdetailMOFDepName);
					subdto.setSfilenocode(sdetailFileNoCode);
					subdto.setSsupdepcode(sdetailFileNoName);
					subdto.setSsupdepname(sdetailSupDepCode);
					subdto.setSagencycode(sdetailAgencyCode);
					subdto.setSagencyname(sdetailAgencyName);
					subdto.setSexpfunccode(sdetailExpFuncCode);
					subdto.setSexpfuncname(sdetailExpFuncName);
					subdto.setSexpecocode((StringUtils.isNotBlank(sdetailExpEcoCode) && sdetailExpEcoCode.getBytes().length <= 30) ? sdetailExpEcoCode : "");// 经济科目编码只取小于30位的值
					subdto.setSexpeconame(sdetailExpEcoName);
					subdto.setSdepprocode(sdetailDepProCode);
					subdto.setSdepproname(sdetailDepProName);
					subdto.setSpayeeacctno(sdetailPayeeAcctNo);
					subdto.setSpayeeacctname(sdetailPayeeAcctName);
					subdto.setSpayeeacctbankname(sdetailPayeeAcctBankName);
					subdto.setSpayeeacctbankno(sdetailPayeeAcctBankNo);
					subdto.setNpayamt(BigDecimal.valueOf(Double.valueOf(sdetailPayAmt)));
					subdto.setSremark(sdetailRemark);
					subdto.setSxpaydate(sdetailDetailXPayDate);
					subdto.setSxagentbusinessno(sdetailXAgentBusinessNo);
					subdto.setSxcheckno(sdetailXCheckNo);
					subdto.setNxpayamt(null);
					subdto.setSxpayeeacctbankname(sdetailXPayeeAcctBankName);
					subdto.setSxpayeeacctno(sdetailXPayeeAcctNo);
					subdto.setSxpayeeacctname(sdetailXPayeeAcctName);
					subdto.setSxaddword(XAddWord);
					subdto.setShold1(sdetailHold1);
					subdto.setShold2(sdetailHold2);
					subdto.setShold3(sdetailHold3);
					subdto.setShold4(sdetailHold4);

					subList.add(subdto);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("解析5201报文出现异常", e);
		}
		return result;
	}
}

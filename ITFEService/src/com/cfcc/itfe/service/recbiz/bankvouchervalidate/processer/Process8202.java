package com.cfcc.itfe.service.recbiz.bankvouchervalidate.processer;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpaymsgsubDto;
import com.cfcc.itfe.service.recbiz.bankvouchervalidate.AbstractBankVoucherValidateProcesser;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 8202报文解析类
 * 
 * @author hua
 * 
 */
public class Process8202 extends AbstractBankVoucherValidateProcesser {
	public MulitTableDto process(String fullFileName) {
		MulitTableDto result = null;

		result = new MulitTableDto();
		result.setFatherDtos(new ArrayList<IDto>());
		result.setSonDtos(new ArrayList<IDto>());
		List<IDto> mainList = result.getFatherDtos();
		List<IDto> subList = result.getSonDtos();
		List<String> delMainList = result.getVoulist(); // 用于保存删除重复记录使用的条件
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
				String id = elementVoucher.elementText("Id");// 财政授权支付凭证Id
				String admDivCode = elementVoucher.elementText("AdmDivCode");// 行政区划代码
				if (StringUtils.isBlank(admDivCode)) {
					result.getErrorList().add(new File(fullFileName).getName() + ": 文件中存在'行政区划代码'为空情况, 请查证!");
					break out;
				}
				String stYear = elementVoucher.elementText("StYear");// 业务年度
				String vtCode = elementVoucher.elementText("VtCode");// 凭证类型编号
				if (!MsgConstant.VOUCHER_NO_8202.equals(vtCode)) {
					result.getErrorList().add("不支持该类型报文导入!");
					break;
				}
				String vouDate = elementVoucher.elementText("VouDate");// 凭证日期
				String voucherNo = elementVoucher.elementText("VoucherNo");// 凭证号
				if (StringUtils.isBlank(voucherNo)) {
					result.getErrorList().add(new File(fullFileName).getName() + ": 文件中存在'主单凭证编号'为空情况, 请查证!");
					break out;
				}
				String fundTypeCode = elementVoucher.elementText("FundTypeCode");// 资金性质编码
				String fundTypeName = elementVoucher.elementText("FundTypeName");// 资金性质名称
				String bgtTypeCode = elementVoucher.elementText("BgtTypeCode");// 预算类型编码
				String bgtTypeName = elementVoucher.elementText("BgtTypeName");// 预算类型名称
				String payTypeCode = elementVoucher.elementText("PayTypeCode");// 支付方式编码
				String payTypeName = elementVoucher.elementText("PayTypeName");// 支付方式名称
				String proCatCode = elementVoucher.elementText("ProCatCode");// 收支管理编码
				String proCatName = elementVoucher.elementText("ProCatName");// 收支管理名称
				String mOFDepCode = elementVoucher.elementText("MOFDepCode");// 业务处室编码
				String mOFDepName = elementVoucher.elementText("MOFDepName");// 业务处室名称
				String supDepCode = elementVoucher.elementText("SupDepCode");// 一级预算单位编码
				String supDepName = elementVoucher.elementText("SupDepName");// 一级预算单位名称
				String agencyCode = elementVoucher.elementText("AgencyCode");// 基层预算单位编码
				String agencyName = elementVoucher.elementText("AgencyName");// 基层预算单位名称
				String expFuncCode = elementVoucher.elementText("ExpFuncCode");// 功能分类科目编码
				String expFuncName = elementVoucher.elementText("ExpFuncName");// 功能分类科目名称
				String expFuncCode1 = elementVoucher.elementText("ExpFuncCode1");// 支出功能分类类编码
				String expFuncName1 = elementVoucher.elementText("ExpFuncName1");// 支出功能分类类名称
				String expFuncCode2 = elementVoucher.elementText("ExpFuncCode2");// 支出功能分类款编码
				String expFuncName2 = elementVoucher.elementText("ExpFuncName2");// 支出功能分类款名称
				String expFuncCode3 = elementVoucher.elementText("ExpFuncCode3");// 支出功能分类项编码
				String expFuncName3 = elementVoucher.elementText("ExpFuncName3");// 支出功能分类项名称
				String expEcoCode = elementVoucher.elementText("ExpEcoCode");// 经济分类科目编码
				String expEcoName = elementVoucher.elementText("ExpEcoName");// 经济分类科目名称
				String expEcoCode1 = elementVoucher.elementText("ExpEcoCode1");// 支出经济分类类编码
				String expEcoName1 = elementVoucher.elementText("ExpEcoName1");// 支出经济分类类名称
				String expEcoCode2 = elementVoucher.elementText("ExpEcoCode2");// 支出经济分类款编码
				String expEcoName2 = elementVoucher.elementText("ExpEcoName2");// 支出经济分类款名称
				String depProCode = elementVoucher.elementText("DepProCode");// 预算项目编码
				String depProName = elementVoucher.elementText("DepProName");// 预算项目名称
				String setModeCode = elementVoucher.elementText("SetModeCode");// 结算方式编码
				String setModeName = elementVoucher.elementText("SetModeName");// 结算方式名称
				String payBankCode = elementVoucher.elementText("PayBankCode");// 代理银行编码
				String payBankName = elementVoucher.elementText("PayBankName");// 代理银行名称
				String clearBankCode = elementVoucher.elementText("ClearBankCode");// 清算银行编码
				String clearBankName = elementVoucher.elementText("ClearBankName");// 清算银行名称
				String payeeAcctNo = elementVoucher.elementText("PayeeAcctNo");// 收款人账号
				String payeeAcctName = elementVoucher.elementText("PayeeAcctName");// 收款人名称
				String payeeAcctBankName = elementVoucher.elementText("PayeeAcctBankName");// 收款人银行
				String payeeAcctBankNo = elementVoucher.elementText("PayeeAcctBankNo");// 收款人银行行号
				String payAcctNo = elementVoucher.elementText("PayAcctNo");// 付款人账号
				String payAcctName = elementVoucher.elementText("PayAcctName");// 付款人名称
				String payAcctBankName = elementVoucher.elementText("PayAcctBankName");// 付款人银行
				String paySummaryCode = elementVoucher.elementText("PaySummaryCode");// 用途编码
				String paySummaryName = elementVoucher.elementText("PaySummaryName");// 用途名称
				String payAmt = elementVoucher.elementText("PayAmt");// 支付金额
				if (StringUtils.isBlank(payAmt)) {
					result.getErrorList().add(new File(fullFileName).getName() + ": 文件中存在'支付金额'为空情况, 请查证!");
					break out;
				}
				String payMgrCode = elementVoucher.elementText("PayMgrCode");// 支付类型编码
				String payMgrName = elementVoucher.elementText("PayMgrName");// 支付类型名称
				String fundDealModeCode = elementVoucher.elementText("FundDealModeCode");// 办理方式编码
				String fundDealModeName = elementVoucher.elementText("FundDealModeName");// 办理方式名称
				String acessAuthGroupCode = elementVoucher.elementText("AcessAuthGroupCode");// 自助柜面业务权限分组标识
				String businessTypeCode = elementVoucher.elementText("BusinessTypeCode");// 业务类型编码
				String businessTypeName = elementVoucher.elementText("BusinessTypeName");// 业务类型名称
				String taxBillNo = elementVoucher.elementText("TaxBillNo");// 申报完税凭证号
				String taxayerID = elementVoucher.elementText("TaxayerID");// 纳税人识别号
				String taxOrgCode = elementVoucher.elementText("TaxOrgCode");// 税务征收机关代码
				String checkNo = elementVoucher.elementText("CheckNo");// 支票号（结算号）
				String xPayDate = elementVoucher.elementText("XPayDate");// 实际支付日期
				String xAgentBusinessNo = elementVoucher.elementText("XAgentBusinessNo");// 银行交易流水号
				String xCheckNo = elementVoucher.elementText("XCheckNo");// 支票号(结算号)
				String xPayAmt = elementVoucher.elementText("XPayAmt");// 实际支付金额
				String xPayeeAcctBankName = elementVoucher.elementText("XPayeeAcctBankName");// 收款人银行
				String xPayeeAcctNo = elementVoucher.elementText("XPayeeAcctNo");// 收款人账号
				String xPayeeAcctName = elementVoucher.elementText("XPayeeAcctName");// 收款人全称
				String hold1 = elementVoucher.elementText("Hold1");// 预留字段1
				String hold2 = elementVoucher.elementText("Hold2");// 预留字段2

				/**
				 * 组装主单对象
				 */
				TfGrantpaymsgmainDto mainDto = new TfGrantpaymsgmainDto();
				String mainvou = generateVousrlno();
				mainDto.setIvousrlno(Long.valueOf(mainvou));
				mainDto.setSorgcode(getLoginfo().getSorgcode());
				mainDto.setStrecode(""); // TODO 国库代码
				mainDto.setSfinorgcode(""); // TODO 财政机关代码 代理银行代码？？？
				mainDto.setSfilename(fullFileName);
				mainDto.setScommitdate(TimeFacade.getCurrentStringTime()); // 当做导入时间
				mainDto.setTssysupdate(DateUtil.currentTimestamp());
				mainDto.setSdealno(mainvou.substring(8, 16));
				mainDto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);

				mainDto.setSid(id);
				mainDto.setSadmdivcode(admDivCode);
				mainDto.setSstyear(stYear);
				mainDto.setSvtcode(vtCode);
				mainDto.setSvoudate(vouDate);
				mainDto.setSvoucherno(voucherNo);
				mainDto.setSfundtypecode(fundTypeCode);
				mainDto.setSfundtypename(fundTypeName);
				mainDto.setSbgttypecode(bgtTypeCode);
				mainDto.setSbgttypename(bgtTypeName);
				mainDto.setSpaytypecode(payTypeCode);
				mainDto.setSpaytypename(payTypeName);
				mainDto.setSprocatcode(proCatCode);
				mainDto.setSprocatname(proCatName);
				mainDto.setSmofdepcode(mOFDepCode);
				mainDto.setSmofdepname(mOFDepName);
				mainDto.setSsupdepcode(supDepCode);
				mainDto.setSsupdepname(supDepName);
				mainDto.setSagencycode(agencyCode);
				mainDto.setSagencyname(agencyName);
				mainDto.setSexpfunccode(expFuncCode);
				mainDto.setSexpfuncname(expFuncName);
				mainDto.setSexpfunccode1(expFuncCode1);
				mainDto.setSexpfuncname1(expFuncName1);
				mainDto.setSexpfunccode2(expFuncCode2);
				mainDto.setSexpfuncname2(expFuncName2);
				mainDto.setSexpfunccode3(expFuncCode3);
				mainDto.setSexpfuncname3(expFuncName3);
				mainDto.setSexpecocode(expEcoCode);
				mainDto.setSexpeconame(expEcoName);
				mainDto.setSexpecocode1(expEcoCode1);
				mainDto.setSexpeconame1(expEcoName1);
				mainDto.setSexpecocode2(expEcoCode2);
				mainDto.setSexpeconame2(expEcoName2);
				mainDto.setSdepprocode(depProCode);
				mainDto.setSdepproname(depProName);
				mainDto.setSsetmodecode(setModeCode);
				mainDto.setSsetmodename(setModeName);
				mainDto.setSpaybankcode(payBankCode);
				mainDto.setSpaybankname(payBankName);
				mainDto.setSclearbankcode(clearBankCode);
				mainDto.setSclearbankname(clearBankName);
				mainDto.setSpayeeacctno(payeeAcctNo);
				mainDto.setSpayeeacctname(payeeAcctName);
				mainDto.setSpayeeacctbankname(payeeAcctBankName);
				mainDto.setSpayeeacctbankno(payeeAcctBankNo);
				mainDto.setSpayacctno(payAcctNo);
				mainDto.setSpayacctname(payAcctName);
				mainDto.setSpayacctbankname(payAcctBankName);
				mainDto.setSpaysummarycode(paySummaryCode);
				mainDto.setSpaysummaryname(paySummaryName);
				mainDto.setNpayamt(new BigDecimal(payAmt));
				mainDto.setSpaymgrcode(payMgrCode);
				mainDto.setSpaymgrname(payMgrName);
				mainDto.setSfunddealmodecode(fundDealModeCode);
				mainDto.setSfunddealmodename(fundDealModeName);
				mainDto.setSbusinesstypecode(businessTypeCode);
				mainDto.setSbusinesstypename(businessTypeName);
				mainDto.setStaxbillno(taxBillNo);
				mainDto.setStaxayerid(taxayerID);
				mainDto.setStaxorgcode(taxOrgCode);
				mainDto.setScheckno(checkNo);
				mainDto.setSxpaydate(xPayDate);
				mainDto.setSxagentbusinessno(xAgentBusinessNo);
				mainDto.setSxcheckno(xCheckNo);
				mainDto.setNxpayamt(xPayAmt == null ? null : new BigDecimal(xPayAmt));
				mainDto.setSxpayeeacctbankname(xPayeeAcctBankName);
				mainDto.setSxpayeeacctno(xPayeeAcctNo);
				mainDto.setSxpayeeacctname(xPayeeAcctName);
				mainDto.setShold1(hold1);
				mainDto.setShold2(hold2);

				mainList.add(mainDto);
				// 删除主表规则：行政区划+报文类型+凭证编号+支付金额
				delMainList.add(new StringBuilder()//
						.append(admDivCode)//
						.append("#")//
						.append(vtCode)//
						.append("#")//
						.append(voucherNo)//
						.append("#")//
						.append(new BigDecimal(payAmt)).toString());

				List detailList = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
				for (int x = 0; x < detailList.size(); x++) {
					Element elementDetail = (Element) detailList.get(x);
					String sub_id = elementDetail.elementText("Id");// 支付明细Id
					String sub_voucherBillId = elementDetail.elementText("VoucherBillId");// 财政授权支付凭证Id
					String sub_voucherBillNo = elementDetail.elementText("VoucherBillNo");// 财政授权支付凭证单号
					if (StringUtils.isBlank(sub_voucherBillNo)) {
						result.getErrorList().add(new File(fullFileName).getName() + ": 文件中存在'财政支付授权凭证单号(明细)'为空情况, 请查证!");
						break out;
					}
					String sub_voucherNo = elementDetail.elementText("VoucherNo");// 支付申请序号
					String sub_fundTypeCode = elementDetail.elementText("FundTypeCode");// 资金性质编码
					String sub_fundTypeName = elementDetail.elementText("FundTypeName");// 资金性质名称
					String sub_bgtTypeCode = elementDetail.elementText("BgtTypeCode");// 预算类型编码
					String sub_bgtTypeName = elementDetail.elementText("BgtTypeName");// 预算类型名称
					String sub_proCatCode = elementDetail.elementText("ProCatCode");// 收支管理编码
					String sub_proCatName = elementDetail.elementText("ProCatName");// 收支管理名称
					String sub_payKindCode = elementDetail.elementText("PayKindCode");// 支出类型编码
					String sub_payKindName = elementDetail.elementText("PayKindName");// 支出类型名称
					String sub_supDepCode = elementDetail.elementText("SupDepCode");// 一级预算单位编码
					String sub_supDepName = elementDetail.elementText("SupDepName");// 一级预算单位名称
					String sub_agencyCode = elementDetail.elementText("AgencyCode");// 基层预算单位编码
					String sub_agencyName = elementDetail.elementText("AgencyName");// 基层预算单位名称
					String sub_expFuncCode = elementDetail.elementText("ExpFuncCode");// 功能分类科目编码
					String sub_expFuncName = elementDetail.elementText("ExpFuncName");// 功能分类科目名称
					String sub_expEcoCode = elementDetail.elementText("ExpEcoCode");// 经济分类科目编码
					String sub_expEcoName = elementDetail.elementText("ExpEcoName");// 经济分类科目名称
					String sub_depProCode = elementDetail.elementText("DepProCode");// 预算项目编码
					String sub_depProName = elementDetail.elementText("DepProName");// 预算项目名称
					String sub_payeeAcctNo = elementDetail.elementText("PayeeAcctNo");// 收款人账号
					String sub_payeeAcctName = elementDetail.elementText("PayeeAcctName");// 收款人名称
					String sub_payeeAcctBankName = elementDetail.elementText("PayeeAcctBankName");// 收款人银行
					String sub_payeeAcctBankNo = elementDetail.elementText("PayeeAcctBankNo");// 收款人银行行号
					String sub_payAmt = elementDetail.elementText("PayAmt");// 支付金额
					String sub_xPayDate = elementDetail.elementText("XPayDate");// 实际支付日期
					String sub_xAgentBusinessNo = elementDetail.elementText("XAgentBusinessNo");// 银行交易流水号
					String sub_xCheckNo = elementDetail.elementText("XCheckNo");// 支票号(结算号)
					String sub_xPayAmt = elementDetail.elementText("XPayAmt");// 实际支付金额
					String sub_xAddWord = elementDetail.elementText("XAddWord");// 附言
					String sub_xPayeeAcctBankName = elementDetail.elementText("XPayeeAcctBankName");// 收款人银行
					String sub_xPayeeAcctNo = elementDetail.elementText("XPayeeAcctNo");// 收款人账号
					String sub_xPayeeAcctName = elementDetail.elementText("XPayeeAcctName");// 收款人全称
					String sub_remark = elementDetail.elementText("Remark");// 备注
					String sub_hold1 = elementDetail.elementText("Hold1");// 预留字段1
					String sub_hold2 = elementDetail.elementText("Hold2");// 预留字段2
					String sub_hold3 = elementDetail.elementText("Hold3");// 预留字段3
					String sub_hold4 = elementDetail.elementText("Hold4");// 预留字段4

					/**
					 * 组装明细对象
					 */
					TfGrantpaymsgsubDto subDto = new TfGrantpaymsgsubDto();
					/*
					 * S_VOUCHERNO支付申请序号不能为空; S_FUNDTYPECODE资金性质编码不能为空; S_FUNDTYPENAME资金性质名称不能为空; S_PAYEEACCTNO收款人账号不能为空; S_PAYEEACCTNAME收款人名称不能为空; S_PAYEEACCTBANKNAME收款人银行不能为空;
					 */
					subDto.setIvousrlno(Long.valueOf(mainvou));
					subDto.setIseqno((long) (x + 1));
					subDto.setSid(sub_id);
					subDto.setSvoucherbillid(sub_voucherBillId);
					subDto.setSvoucherbillno(sub_voucherBillNo);
					subDto.setSvoucherno(sub_voucherNo == null ? "" : sub_voucherNo);
					subDto.setSfundtypecode(sub_fundTypeCode == null ? "" : sub_fundTypeCode);
					subDto.setSfundtypename(sub_fundTypeName == null ? "" : sub_fundTypeName);
					subDto.setSbgttypecode(sub_bgtTypeCode);
					subDto.setSbgttypename(sub_bgtTypeName);
					subDto.setSprocatcode(sub_proCatCode);
					subDto.setSprocatname(sub_proCatName);
					subDto.setSpaykindcode(sub_payKindCode);
					subDto.setSpaykindname(sub_payKindName);
					subDto.setSsupdepcode(sub_supDepCode);
					subDto.setSsupdepname(sub_supDepName);
					subDto.setSagencycode(sub_agencyCode);
					subDto.setSagencyname(sub_agencyName);
					subDto.setSexpfunccode(sub_expFuncCode);
					subDto.setSexpfuncname(sub_expFuncName);
					subDto.setSexpecocode(sub_expEcoCode);
					subDto.setSexpeconame(sub_expEcoName);
					subDto.setSdepprocode(sub_depProCode);
					subDto.setSdepproname(sub_depProName);
					subDto.setSpayeeacctno(sub_payeeAcctNo == null ? "" : sub_payeeAcctNo);
					subDto.setSpayeeacctname(sub_payeeAcctName == null ? "" : sub_payeeAcctName);
					subDto.setSpayeeacctbankname(sub_payeeAcctBankName == null ? "" : sub_payeeAcctBankName);
					subDto.setSpayeeacctbankno(sub_payeeAcctBankNo);
					subDto.setNpayamt(sub_payAmt == null ? null : new BigDecimal(sub_payAmt));
					subDto.setSxpaydate(sub_xPayDate);
					subDto.setSxagentbusinessno(sub_xAgentBusinessNo);
					subDto.setSxcheckno(sub_xCheckNo);
					subDto.setNxpayamt(sub_xPayAmt == null ? null : new BigDecimal(sub_xPayAmt));
					subDto.setSxpayeeacctbankname(sub_xPayeeAcctBankName);
					subDto.setSxpayeeacctno(sub_xPayeeAcctNo);
					subDto.setSxpayeeacctname(sub_xPayeeAcctName);
					subDto.setSremark(sub_remark);
					subDto.setShold1(sub_hold1);
					subDto.setShold2(sub_hold2);
					subDto.setShold3(sub_hold3);
					subDto.setShold4(sub_hold4);

					subList.add(subDto);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("解析8202报文出现异常", e);
		}

		return result;
	}
}

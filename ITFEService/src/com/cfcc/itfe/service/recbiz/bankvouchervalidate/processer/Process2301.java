package com.cfcc.itfe.service.recbiz.bankvouchervalidate.processer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.service.recbiz.bankvouchervalidate.AbstractBankVoucherValidateProcesser;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * 2301报文解析类(为了测试方便写的, 实际导入不包含此报文)
 * 
 * @author hua
 * 
 */
public class Process2301 extends AbstractBankVoucherValidateProcesser {

	public MulitTableDto process(String fullFileName) {
		MulitTableDto result = null;

		result = new MulitTableDto();
		result.setFatherDtos(new ArrayList<IDto>());
		result.setSonDtos(new ArrayList<IDto>());
		List<IDto> mainList = result.getFatherDtos();
		List<IDto> subList = result.getSonDtos();
		try {
			Document doc = parseFile2Doc(fullFileName);
			if (doc == null) {
				result.getErrorList().add("无法解析的文件, 请查证!.");
				return result;
			}
			List voucherList = doc.selectNodes("Voucher");
			if (voucherList == null || voucherList.size() == 0) {
				result.getErrorList().add("无法识别的报文内容, 找不到<Voucher/>节点!");
				return result;
			}

			for (Object voucherTemp : voucherList) {
				Element elementVoucher = (Element) voucherTemp;

				// Element elementVoucher = (Element) element.selectSingleNode("Voucher");
				/**
				 * 凭证信息
				 */
				// String Attach = ((Element) VoucherBodyList.get(i)).elementText("Attach");// 附加信息
				String admDivCode = elementVoucher.elementText("AdmDivCode");// 行政区划代码
				String stYear = elementVoucher.elementText("StYear");// 业务年度
				String vtCode = elementVoucher.elementText("VtCode");// 凭证类型编号
				String Id = elementVoucher.elementText("Id");// 申请划款凭证Id
				String VouDate = elementVoucher.elementText("VouDate"); // 凭证日期
				String VoucherNo = elementVoucher.elementText("VoucherNo");// 凭证号
				String TreCode = elementVoucher.elementText("TreCode"); // 国库主体代码
				String FinOrgCode = elementVoucher.elementText("FinOrgCode");// 财政机关代码
				String BgtTypeCode = elementVoucher.elementText("BgtTypeCode");// 预算类型编码
				String BgtTypeName = elementVoucher.elementText("BgtTypeName");// 预算类型名称
				String FundTypeCode = elementVoucher.elementText("FundTypeCode");// 资金性质编码
				String FundTypeName = elementVoucher.elementText("FundTypeName");// 资金性质名称
				String PayTypeCode = elementVoucher.elementText("PayTypeCode");// 支付方式编码
				String PayTypeName = elementVoucher.elementText("PayTypeName");// 支付方式名称
				String AgentAcctNo = elementVoucher.elementText("AgentAcctNo");// 收款银行账号
				String AgentAcctName = elementVoucher.elementText("AgentAcctName");// 收款银行账户名称
				String AgentAcctBankName = elementVoucher.elementText("AgentAcctBankName");// 收款银行
				String ClearAcctNo = elementVoucher.elementText("ClearAcctNo");// 付款账号
				String ClearAcctName = elementVoucher.elementText("ClearAcctName");// 付款账户名称
				String ClearAcctBankName = elementVoucher.elementText("ClearAcctBankName");// 付款银行
				String PayAmt = elementVoucher.elementText("PayAmt");// 汇总清算金额
				String PayBankName = elementVoucher.elementText("PayBankName");// 代理银行名称
				String PayBankNo = elementVoucher.elementText("PayBankNo");// 代理银行行号
				String Remark = elementVoucher.elementText("Remark");// 摘要
				String MoneyCorpCode = elementVoucher.elementText("MoneyCorpCode");// 金融机构编码
				String XPaySndBnkNo = elementVoucher.elementText("XPaySndBnkNo");// 支付发起行行号
				String XAddWord = elementVoucher.elementText("XAddWord");// 附言
				String XClearDate = elementVoucher.elementText("XClearDate");// 清算日期
				String XPayAmt = elementVoucher.elementText("XPayAmt");// 汇总清算金额
				String Hold1 = elementVoucher.elementText("Hold1");// 预留字段1
				String Hold2 = elementVoucher.elementText("Hold2");// 预留字段2

				/**
				 * 组装TvPayreckBankDto对象
				 **/
				TvPayreckBankDto maindto = new TvPayreckBankDto();
				String mainvou = generateVousrlno();
				maindto.setStrano(mainvou.substring(8, 16));// 申请划款凭证Id
				maindto.setIvousrlno(Long.valueOf(mainvou));// 凭证流水号
				maindto.setSid(Id);// 申请划款凭证Id
				maindto.setSadmdivcode(admDivCode);// 行政区划代码
				maindto.setSofyear(stYear);// 业务年度
				maindto.setSvtcode(vtCode);// 凭证类型编号\
				SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
				maindto.setSbookorgcode(getLoginfo().getSorgcode());// 核算主体代码
				maindto.setDvoudate(CommonUtil.strToDate(VouDate)); // 凭证日期
				maindto.setSvouno(VoucherNo);// 凭证号
				maindto.setStrecode(TreCode); // 国库主体代码
				maindto.setSfinorgcode(FinOrgCode);// 财政机关代码
				maindto.setSbgttypecode(BgtTypeCode);// 预算类型编码
				maindto.setSbgttypename(BgtTypeName);// 预算类型名称
				maindto.setSfundtypecode(FundTypeCode);// 资金性质编码
				maindto.setSfundtypename(FundTypeName);// 资金性质名称
				maindto.setSpaytypecode(PayTypeCode);// 支付方式编码
				if ("12".equals(PayTypeCode) || PayTypeCode.startsWith("001002")) {// 财政普遍采用的是6位
					maindto.setSpaymode("1");// 授权支付
				} else if ("11".equals(PayTypeCode) || PayTypeCode.startsWith("001001")) {// 财政普遍采用的是6位
					maindto.setSpaymode("0");// 支付方式编码 直接支付
				}
				maindto.setSpaytypename(PayTypeName);// 支付方式名称
				maindto.setSpayeeacct(AgentAcctNo);// 收款银行账号
				maindto.setSpayeename(AgentAcctName);// 收款银行账户名称
				maindto.setSagentacctbankname(AgentAcctBankName);// 收款银行
				maindto.setSpayeeaddr("");// 收款人地址
				maindto.setSpayeraddr("");// 付款人地址
				maindto.setSpayeracct(ClearAcctNo);// 付款账号
				maindto.setSpayername(ClearAcctName);// 付款账户名称
				maindto.setSclearacctbankname(ClearAcctBankName);// 付款银行
				maindto.setFamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));// 汇总清算金额
				maindto.setSpaybankname(PayBankName);// 代理银行名称
				maindto.setSagentbnkcode(PayBankNo);// 代理银行行号
				maindto.setSdescription(Remark);// 摘要
				maindto.setSmoneycorpcode(MoneyCorpCode);// 金融机构编码
				maindto.setSxpaysndbnkno(XPaySndBnkNo);// 支付发起行行号
				maindto.setSaddword("this is addword");// 摘要当做附言
				maindto.setSxcleardate(CommonUtil.strToDate(XClearDate));// 清算日期
				if (XPayAmt != null && !XPayAmt.equals("")) {
					maindto.setSxpayamt(BigDecimal.valueOf(Double.valueOf(XPayAmt)));// 汇总清算金额
				} else {
					maindto.setSxpayamt(BigDecimal.valueOf(Double.valueOf("0.00")));// 汇总清算金额
				}
				if ("1".equals(ITFECommonConstant.ISCHECKPAYPLAN)) {
					maindto.setSxpayamt(BigDecimal.valueOf(Double.valueOf(PayAmt)));
				}
				maindto.setShold1(Hold1);// 预留字段1
				maindto.setShold2(Hold2);// 预留字段2
				maindto.setDentrustdate(DateUtil.currentDate());// 委托日期
				maindto.setSpackno("0000");// 包流水号
				maindto.setStrimsign(MsgConstant.TIME_FLAG_NORMAL);// 调整期标志
				if ("000073100012".equals(ITFECommonConstant.SRC_NODE)) {// 湖南资金性质作为预算种类
					maindto.setSbudgettype(FundTypeCode);// 预算种类(默认预算内)
				} else {
					maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);// 预算种类(默认预算内)
				}
				maindto.setSpayeeopbkno(PayBankNo);// 收款人开户行行号
				maindto.setSfilename(fullFileName);
				maindto.setSresult(DealCodeConstants.DEALCODE_ITFE_DEALING);// 状态 处理中
				maindto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// 系统时间

				mainList.add(maindto);

				List detailList = elementVoucher.selectSingleNode("DetailList").selectNodes("Detail");
				for (int x = 0; x < detailList.size(); x++) {
					Element elementDetail = (Element) detailList.get(x);

					/**
					 * 校验报文明细Id节点是否存在 1、若节点不存在，是老版本，不更新明细Id 2、若节点存在，是新版本，更新明细Id
					 */
					Element sdetailIdElement = elementDetail.element("Id");
					// 节点不存在
					/*
					 * if(sdetailIdElement==null) subDtoIdList.add("节点不存在"); else{ sdetailId = sdetailIdElement.getText();//明细Id if(StringUtils.isBlank(sdetailId)){ errDetailMsg="明细ID字段不能为空！"; break;
					 * } }
					 */
					String VoucherNol = elementDetail.elementText("VoucherNo");// 支付凭证单号
					String SupDepCode = elementDetail.elementText("SupDepCode");// 一级预算单位编码
					String SupDepName = elementDetail.elementText("SupDepName");// 一级预算单位名称
					String ExpFuncCode = elementDetail.elementText("ExpFuncCode");// 支出功能分类科目编码
					String ExpFuncName = elementDetail.elementText("ExpFuncName");// 支出功能分类科目名称
					String sPayAmt = elementDetail.elementText("PayAmt");// 支付金额
					String PaySummaryName = elementDetail.elementText("PaySummaryName");// 摘要名称
					String sdetailHold1 = elementDetail.elementText("Hold1");// 预留字段1
					String sdetailHold2 = elementDetail.elementText("Hold2");// 预留字段2
					String sdetailHold3 = elementDetail.elementText("Hold3");// 预留字段3
					String sdetailHold4 = elementDetail.elementText("Hold4");// 预留字段4

					String PayDate = elementDetail.elementText("PayDate");// 预留字段4

					TvPayreckBankListDto subdto = new TvPayreckBankListDto();
					// 此处的设值待确认
					subdto.setIseqno(x + 1);// 序号
					subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);// 账户性质
					subdto.setIvousrlno(Long.valueOf(mainvou));// 子表序列号
					subdto.setSvouchern0(VoucherNol);// 子表明细序号
					// 支付凭证单号
					subdto.setSbdgorgcode(SupDepCode);// 一级预算单位编码
					subdto.setSsupdepname(SupDepName);// 一级预算单位名称
					subdto.setSfuncbdgsbtcode(ExpFuncCode);// 支出功能分类科目编码
					subdto.setSexpfuncname(ExpFuncName);// 支出功能分类科目名称
					subdto.setSecnomicsubjectcode("");// 经济科目代码
					subdto.setFamt(BigDecimal.valueOf(Double.valueOf(sPayAmt)));// 支付金额
					subdto.setSpaysummaryname(PaySummaryName);// 摘要名称
					subdto.setShold1(sdetailHold1);// 预留字段1
					subdto.setShold2(sdetailHold2);// 预留字段2
					subdto.setShold3(sdetailHold3);// 预留字段3
					subdto.setShold4(sdetailHold4);// 预留字段4
					subdto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// 更新时间
					subdto.setSid("");

					subList.add(subdto);
				}
				
				maindto.setIstatinfnum(subList.size());
			}
		} catch (Exception e) {
			throw new RuntimeException("解析2301报文出现异常", e);
		}
		return result;
	}

}

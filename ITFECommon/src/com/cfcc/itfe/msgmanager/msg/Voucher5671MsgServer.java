package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvNontaxsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.service.VoucherVerify;

public class Voucher5671MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5671MsgServer.class);
	private Voucher voucher;

	/**
	 * 代理行发往人行的非税凭证5671，此类只保存业务数据
	 */
	@SuppressWarnings( { "static-access", "unchecked" })
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
			logger.error("解析非税收入缴库单5671报文出现错误！", e);
			throw new ITFEBizException("解析非税收入缴库单5671报文出现错误！", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String AdmDivCode = "";// 行政区划代码
		String StYear = "";// 年度
		String VtCode = "";// 凭证类型

		String currentDate = TimeFacade.getCurrentStringTime();// 当前系统日期
		List<String> voucherList = new ArrayList<String>();

		// 获取行政区划代码、年度和凭证类型
		if (VoucherBodyList.size() > 0) {
			// VoucherBody
			Element element = (Element) VoucherBodyList.get(0);
			AdmDivCode = element.attribute("AdmDivCode").getText();
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}

		// 预算科目代码list
		ArrayList<String> expFuncCodeList = null;

		// String sAgencyCode ="";
		TvNontaxmainDto maindto = null;
		List mainDtoList = null;
		ArrayList<TvNontaxsubDto> subDtoList = null;
		List lists = new ArrayList();
		List<Object> list = null;
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
				String Id = elementVoucher.elementText("Id");// 财政授权支付汇总清算额度通知单Id
				// String VoucherNo =
				// elementVoucher.elementText("VoucherNo");//凭证号
				String VouDate = elementVoucher.elementText("VouDate"); // 凭证日期
				String ExpDate = elementVoucher.elementText("ExpDate"); // 缴库期限

				String FinOrgCode = elementVoucher.elementText("FinOrgCode");// 财政机关代码
				String FinOrgName = elementVoucher.elementText("FinOrgName");// 财政机关名称
				String TreCode = elementVoucher.elementText("TreCode"); // 国库主体代码

				String PayeeAcctNo = elementVoucher.elementText("PayeeAcctNo"); // 收款人账号
				String PayeeAcctName = elementVoucher
						.elementText("PayeeAcctName"); // 收款人名称
				String PayeeAcctBankName = elementVoucher
						.elementText("PayeeAcctBankName"); // 收款人开户行
				String BudgetLevelCode = elementVoucher
						.elementText("BudgetLevelCode"); // 预算级次编码
				String BudgetLevelName = elementVoucher
						.elementText("BudgetLevelName"); // 预算级次名称
				String PayAcctName = elementVoucher.elementText("PayAcctName"); // 付款人名称
				String PayAcctNo = elementVoucher.elementText("PayAcctNo"); // 付款人账号
				String PayAcctBankName = elementVoucher
						.elementText("PayAcctBankName"); // 付款人开户行
				String PayBankNo = elementVoucher.elementText("XPayBankNo"); // 付款行行号
				String TraNoM = elementVoucher.elementText("XTraNo"); // 资金交易流水号
				String OrginVouNo = elementVoucher.elementText("OrginVouNo"); // 缴库通知凭证号
				String XAddWord = elementVoucher.elementText("XAddWord"); // 附言
				String XTraDate = elementVoucher.elementText("XTraDate"); // 实际支付日期
				String XTraAmt = elementVoucher.elementText("XTraAmt"); // 入库金额
				String Hold1 = elementVoucher.elementText("Hold1"); // 预留字段1
				String Hold2 = elementVoucher.elementText("Hold2"); // 预留字段2
				String Hold3 = elementVoucher.elementText("Hold3"); // 预留字段3
				String Hold4 = elementVoucher.elementText("Hold4"); // 预留字段4
				String Remark = elementVoucher.elementText("Remark"); // 备注
				String TotalCount = elementVoucher.elementText("TotalCount"); // 总笔数
				String TraAmt = elementVoucher.elementText("TraAmt"); // 缴库金额
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
				maindto.setSexpdate(ExpDate); // 缴库期限
				maindto.setSfinorgcode(FinOrgCode);// 财政机关代码
				maindto.setSfingorgname(FinOrgName);// 财政机关名称
				maindto.setStrecode(TreCode); // 国库主体代码
				maindto.setSpayeeacctno(PayeeAcctNo); // 收款人账号
				maindto.setSpayeeacctnane(PayeeAcctName); // 收款人名称
				maindto.setSpayeeacctbankname(PayeeAcctBankName); // 收款人开户行
				maindto.setSbudgetlevelcode(BudgetLevelCode); // 预算级次编码
				maindto.setSbudgetlevelname(BudgetLevelName); // 预算级次名称
				maindto.setSpayacctname(PayAcctName); // 付款人名称
				maindto.setSpayacctno(PayAcctNo); // 付款人账号
				maindto.setSpayacctbankname(PayAcctBankName); // 付款人开户行
				maindto.setSpaybankno(PayBankNo); // 付款行行号
				maindto.setStranom(TraNoM); // 资金交易流水号
				maindto.setSorginvouno(OrginVouNo); // 缴库通知凭证号
				maindto.setSxaddword(XAddWord); // 附言
				maindto.setSxtradate(XTraDate); // 实际支付日期
				maindto.setStaxorgcode(TaxOrgCode);// 征收机关代码
				maindto.setStaxorgname(TaxOrgName);// 征收机关名称
				if(XTraAmt==null || XTraAmt.equals(""))
					XTraAmt = "0.00";
				maindto.setNxmoney(BigDecimal.valueOf(Double.valueOf(XTraAmt))); // 入库金额
				maindto.setShold1(Hold1); // 预留字段1
				maindto.setShold2(Hold2); // 预留字段2
				maindto.setShold3(Hold3); // 预留字段3
				maindto.setShold4(Hold4); // 预留字段4
				maindto.setScount(TotalCount); // 总笔数
				maindto.setSdemo(Remark);// 备注
				maindto.setNmoney(BigDecimal.valueOf(Double.valueOf(TraAmt))); // 缴库金额
				maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);
				maindto.setStrimflag(StateConstant.TRIMSIGN_FLAG_NORMAL);
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
				// 预算科目代码list
				expFuncCodeList = new ArrayList<String>();
				/**
				 * 组装TvPayoutmsgsubDto对象
				 */
				subDtoList = new ArrayList<TvNontaxsubDto>();
				for (int j = 0; j < listDetail.size(); j++) {

					Element elementDetail = (Element) listDetail.get(j);
					Element sdetailIdElement = elementDetail.element("Id");
					String SubId = "";
					// 节点不存在
					if (sdetailIdElement != null)
						SubId = elementDetail.elementText("Id");// 明细ID

					String TroNo = elementDetail.elementText("TroNo");// 交易流水号
					String VoucherNosub = elementDetail.elementText("VoucherNo");// 凭证号
					
					String TraAmtsub = elementDetail
							.elementText("TraAmt");// 缴库金额
					String FundTypecode = elementDetail
							.elementText("FundTypecode");// 资金性质编码
					String PayCode = elementDetail
							.elementText("PayCode");// 缴款码
					String BudgetSubject = elementDetail
							.elementText("BudgetSubject");// 预算科目代码
					String BudgetName = elementDetail
							.elementText("BudgetName");// 预算科目名称
					String ItemCode = elementDetail
							.elementText("ItemCode");// 收入项目编码
					String ItemName = elementDetail
							.elementText("ItemName");// 收入项目名称
					String DetailAmt = elementDetail
							.elementText("DetailAmt");// 明细金额
					
					String sdetailHold1 = elementDetail.elementText("Hold1");// 预留字段1
					String sdetailHold2 = elementDetail.elementText("Hold2");// 预留字段2
					String sdetailHold3 = elementDetail.elementText("Hold3");// 预留字段3
					String sdetailHold4 = elementDetail.elementText("Hold4");// 预留字段4
					String ViceSign = elementDetail.elementText("ViceSign");// 辅助标志
					TvNontaxsubDto subdto = new TvNontaxsubDto();
					
					subdto.setSdealno(mainvou);
					subdto.setSseqno(String.valueOf(++j));
					subdto.setStrono(TroNo);// 交易流水号
					subdto.setSvoucherno(VoucherNosub);// 凭证号
					
					subdto.setNtraamt(BigDecimal.valueOf(Double.valueOf(TraAmtsub)));// 缴库金额
					subdto.setSfundtypecode(FundTypecode);// 资金性质编码
					subdto.setSpaycode(PayCode);// 缴款码
					subdto.setSbudgetsubject(BudgetSubject);// 预算科目代码
					subdto.setSbudgetname(BudgetName);// 预算科目名称
					subdto.setSitemcode(ItemCode);// 收入项目编码
					subdto.setSitemname(ItemName);// 收入项目名称
					subdto.setNdetailamt(BigDecimal.valueOf(Double.valueOf(DetailAmt)));// 明细金额
					subdto.setShold1(sdetailHold1);// 预留字段1
					subdto.setShold2(sdetailHold2);// 预留字段2
					subdto.setShold3(sdetailHold3);// 预留字段3
					subdto.setShold4(sdetailHold4);// 预留字段4
					subdto.setSvicesign(ViceSign);// 辅助标志
					expFuncCodeList.add(BudgetSubject);
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double.valueOf(DetailAmt)));
					subDtoList.add(subdto);
				}
				String ls_VoucherEnd = "";

				// 根据交易流水号取得凭证索引表dto
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(
						vDto).get(0);

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
				verifydto.setFamt(TraAmt);
				String returnmsg = voucherVerify.checkValid(verifydto,
						MsgConstant.VOUCHER_NO_5671);
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
}

package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailssubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherCompare;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
@SuppressWarnings({ "unchecked", "static-access" })
public class Voucher8207MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher8207MsgServer.class);
	private Voucher voucher;

	/**
	 * 财政发往人行的批量业务支付明细凭证8207，此类只保存业务数据
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		String voucherXml = (String) muleMessage.getPayload();
		String ls_FileName = (String) muleMessage.getProperty("fileName");
//		String ls_OrgCode = (String) muleMessage.getProperty("orgCode");
		HashMap<String, String> dealnos = (HashMap<String, String>) muleMessage
				.getProperty("dealnos");
		Document fxrDoc = null;
		try {
			fxrDoc = DocumentHelper.parseText(voucherXml);
		} catch (DocumentException e) {
			logger.error("解析财政批量业务支付明细凭证8207报文出现错误！", e);
			throw new ITFEBizException("解析财政批量业务支付明细凭证8207报文出现错误！", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String StYear = "";// 年度
		String VtCode = "";// 凭证类型

//		String currentDate = TimeFacade.getCurrentStringTime();// 当前系统日期
//		List<String> voucherList = new ArrayList<String>();

		// 获取行政区划代码、年度和凭证类型
		if (VoucherBodyList.size() > 0) {
			Element element = (Element) VoucherBodyList.get(0);
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}
		TfPaymentDetailsmainDto maindto = null;
		List subDtoList = null;
		// 凭证序列号
		String mainvou = "";
		TvVoucherinfoDto vDto;
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
				// String VoucherNo =
				// elementVoucher.elementText("VoucherNo");//凭证号
				String VouDate = elementVoucher.elementText("VouDate"); // 凭证日期
				String OriginalVtCode = elementVoucher
						.elementText("OriginalVtCode"); // 主凭证类型编号
				String OriginalVoucherNo = elementVoucher
						.elementText("OriginalVoucherNo"); // 主支付凭证编号
				String FundTypeCode = elementVoucher
						.elementText("FundTypeCode"); // 资金性质编码
				String FundTypeName = elementVoucher
						.elementText("FundTypeName"); // 资金性质名称
				String PayDictateNo = elementVoucher
						.elementText("PayDictateNo"); // 支付交易序号
				String PayMsgNo = elementVoucher.elementText("PayMsgNo"); // 支付报文编号
				String PayEntrustDate = elementVoucher
						.elementText("PayEntrustDate"); // 支付委托日期
				String PaySndBnkNo = elementVoucher.elementText("PaySndBnkNo"); // 支付发起行行号
				String SumAmt = elementVoucher.elementText("SumAmt"); // 汇总支付金额
				String AgencyCode = elementVoucher.elementText("AgencyCode"); // 基层预算单位编码
				String AgencyName = elementVoucher.elementText("AgencyName"); // 基层预算单位名称
				String PayAcctNo = elementVoucher.elementText("PayAcctNo"); // 付款人账号
				String PayAcctName = elementVoucher.elementText("PayAcctName"); // 付款人名称
				String PayAcctBankName = elementVoucher
						.elementText("PayAcctBankName"); // 付款人银行
				String PayBankCode = elementVoucher.elementText("PayBankCode"); // 代理银行编码
				String PayBankName = elementVoucher.elementText("PayBankName"); // 代理银行名称
				String BusinessTypeCode = elementVoucher
						.elementText("BusinessTypeCode"); // 业务类型编码
				String BusinessTypeName = elementVoucher
						.elementText("BusinessTypeName"); // 业务类型名称
				String PayTypeCode = elementVoucher.elementText("PayTypeCode"); // 支付方式编码
				String PayTypeName = elementVoucher.elementText("PayTypeName"); // 支付方式名称
				String XPayDate = elementVoucher.elementText("XPayDate"); // 实际支付日期
				String XSumAmt = elementVoucher.elementText("XSumAmt"); // 实际支付汇总金额
				String ExpFuncCode = elementVoucher.elementText("ExpFuncCode");// 支出功能科目代码
				String ExpFuncName = elementVoucher.elementText("ExpFuncName");// 支出功能科目名称
				String Remark = elementVoucher.elementText("Remark");// 备注
				String Hold1 = elementVoucher.elementText("Hold1");// 预留字段1
				String Hold2 = elementVoucher.elementText("Hold2");// 预留字段2

				/**
				 * 组装TfPaymentDetailsmainDto对象
				 **/
				vDto = new TvVoucherinfoDto();
				mainvou = dealnos.get(VoucherNo);// 获取序列号
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(
						vDto).get(0);

				maindto = new TfPaymentDetailsmainDto();
				maindto.setIvousrlno(Long.parseLong(mainvou));
				maindto.setSvoudate(VouDate);
				maindto.setSvtcode(VtCode);
				maindto.setSoriginalvtcode(OriginalVtCode);
				maindto.setSoriginalvoucherno(OriginalVoucherNo);
				maindto.setSfundtypecode(FundTypeCode);
				maindto.setSfundtypename(FundTypeName);
				maindto.setSpaydictateno(PayDictateNo);
				maindto.setSpaymsgno(PayMsgNo);
				maindto.setSpayentrustdate(PayEntrustDate);
				maindto.setSpaysndbnkno(PaySndBnkNo);
				maindto.setNsumamt(BigDecimal.valueOf(Double.valueOf(SumAmt)));
				maindto.setSagencycode(AgencyCode);
				maindto.setSagencyname(AgencyName);
				maindto.setSpayacctno(PayAcctNo);
				maindto.setSpayacctname(PayAcctName);
				maindto.setSpayacctbankname(PayAcctBankName);
				maindto.setSpaybankcode(PayBankCode);
				maindto.setSpaybankname(PayBankName);
				maindto.setSbusinesstypecode(BusinessTypeCode);
				maindto.setSbusinesstypename(BusinessTypeName);
				if (PayTypeCode != null && "11".equals(PayTypeCode)) {
					maindto.setSpaytypecode("0");// 直接支付
				} else if (PayTypeCode != null && "12".equals(PayTypeCode))
					maindto.setSpaytypecode("1");// 授权支付
				else if (PayTypeCode != null)
					maindto.setSpaytypecode(PayTypeCode);
				maindto.setSpaytypename(PayTypeName);
				maindto.setSxpaydate(XPayDate);
				if (DealCodeConstants.VOUCHER_FLAT_1.equals(vDto
						.getSvoucherflag())) {
					if (StringUtils.isBlank(XSumAmt)) {
						voucher.voucherComfail(vDto.getSdealno(),
								"回单中，实际支付汇总金额不能为空！");
						continue;
					}
					maindto.setNxsumamt(MtoCodeTrans
							.transformBigDecimal(XSumAmt));
				} else {
					maindto.setNxsumamt(null);
				}
				maindto.setShold1(Hold1);
				maindto.setShold2(Hold2);
				maindto.setSfilename(ls_FileName);
				maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);
				maindto.setTssysupdate(new Timestamp(new java.util.Date()
						.getTime()));// 系统时间
				maindto.setSext1(ExpFuncCode);
				maindto.setSext2(ExpFuncName);
				maindto.setSext3(Remark);

				// 业务子表集合
				subDtoList = new ArrayList<TfPaymentDetailssubDto>();
				// 子表明细Id集合
				List<String> subDtoIdList = new ArrayList<String>();
				/**
				 * 组装TfPaymentDetailssubDto对象
				 */
				for (int j = 0; j < listDetail.size(); j++) {

					Element elementDetail = (Element) listDetail.get(j);
					String Id = elementDetail.elementText("Id");// 明细编号
					String OriVoucherNo = elementDetail
							.elementText("OriVoucherNo");// 支付凭证编号
					String PayeeAcctNo = elementDetail
							.elementText("PayeeAcctNo");// 收款人账号
					String PayeeAcctName = elementDetail
							.elementText("PayeeAcctName");// 收款人名称
					String PayeeAcctBankName = elementDetail
							.elementText("PayeeAcctBankName");// 收款人银行
					String PayAmt = elementDetail.elementText("PayAmt");// 支付金额
					String sdetailRemark = elementDetail.elementText("Remark");// 备注
					String DetailXPayDate = elementDetail
							.elementText("XPayDate");// 实际支付日期
					String XAgentBusinessNo = elementDetail
							.elementText("XAgentBusinessNo");// 银行交易流水号
					String XPayAmt = elementDetail.elementText("XPayAmt");// 实际支付金额
					String XPayeeAcctBankName = elementDetail
							.elementText("XPayeeAcctBankName");// 收款人银行
					String XPayeeAcctNo = elementDetail
							.elementText("XPayeeAcctNo");// 收款人账号
					String XAddWordCode = elementDetail
							.elementText("XAddWordCode");// 失败原因代码
					String XAddWord = elementDetail.elementText("XAddWord");// 失败原因
					String sdetailHold1 = elementDetail.elementText("Hold1");// 预留字段1
					String sdetailHold2 = elementDetail.elementText("Hold2");// 预留字段2
					String sdetailHold3 = elementDetail.elementText("Hold3");// 预留字段3
					String sdetailHold4 = elementDetail.elementText("Hold4");// 预留字段4
					//上海新增两个字段
					String expFuncCode = elementDetail.elementText("ExpFuncCode");//功能分类科目编码
					String expFuncName = elementDetail.elementText("ExpFuncName");//功能分类科目名称
					TfPaymentDetailssubDto subdto = new TfPaymentDetailssubDto();
					subdto.setSext1(expFuncCode);
					subdto.setSext2(expFuncName);
					subdto.setIvousrlno(Long.parseLong(mainvou));
					subdto.setIseqno((long) (j + 1));
					subdto.setSid(Id);
					subdto.setSorivoucherno(OriVoucherNo);
					subdto.setSpayeeacctno(PayeeAcctNo);
					subdto.setSpayeeacctname(PayeeAcctName);
					subdto.setSpayeeacctbankname(PayeeAcctBankName);
					subdto.setNpayamt(BigDecimal
							.valueOf(Double.valueOf(PayAmt)));
					subdto.setSremark(sdetailRemark);
					subdto.setSxpaydate(DetailXPayDate);
					subdto.setSxagentbusinessno(XAgentBusinessNo);
					if (DealCodeConstants.VOUCHER_FLAT_1.equals(vDto
							.getSvoucherflag())) {
						if (StringUtils.isBlank(XPayAmt)) {
							voucher.voucherComfail(vDto.getSdealno(),
									"回单中，实际支付金额不能为空！");
							continue;
						}
						subdto.setNxpayamt(MtoCodeTrans
								.transformBigDecimal(XPayAmt));
					} else {
						subdto.setNxpayamt(null);
					}
					subdto.setSxpayeeacctbankname(XPayeeAcctBankName);
					subdto.setSxpayeeacctno(XPayeeAcctNo);
					subdto.setSxaddwordcode(XAddWordCode);
					subdto.setSxaddword(XAddWord);
					subdto.setShold1(sdetailHold1);
					subdto.setShold2(sdetailHold2);
					subdto.setShold3(sdetailHold3);
					subdto.setShold4(sdetailHold4);

					// 明细合计金额
					sumAmt = sumAmt.add(BigDecimal.valueOf(Double
							.valueOf(PayAmt)));
					subDtoList.add(subdto);
					subDtoIdList.add(subdto.getSid());

				}

				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					vDto.setShold3(OriginalVoucherNo);// 8207比对凭证编号
				}
				vDto.setShold4(BusinessTypeCode);// 业务类型编码
				maindto.setSstyear(vDto.getSstyear());
				maindto.setSvoucherno(vDto.getSvoucherno());
				maindto.setSorgcode(vDto.getSorgcode());
				maindto.setStrecode(vDto.getStrecode());
				maindto.setSadmdivcode(vDto.getSadmdivcode());
				maindto.setSdemo(vDto.getSattach());

				/**
				 * 校验明细Id是否为空或重复
				 */
				String checkIdMsg = voucherVerify
						.checkValidSudDtoId(subDtoIdList);
				if (checkIdMsg != null) {
					// 返回错误信息签收失败
					voucher.voucherComfail(vDto.getSdealno(), checkIdMsg);
					continue;
				}
				/**
				 * 组装verifydto,进行报文校验
				 */
				verifydto.setTrecode(maindto.getStrecode());
				verifydto.setFinorgcode(maindto.getSorgcode());
				verifydto.setVoucherno(VoucherNo);
				verifydto.setVoudate(VouDate);
				verifydto.setPaybankno(PayBankCode);
				verifydto.setPaytypecode(PayTypeCode);
				verifydto.setClearAcctNo(PayAcctNo);
				verifydto.setClearAcctName(PayAcctName);
				// 增加年度，总金额的校验 by renqingbin
				verifydto.setOfyear(StYear);
				verifydto.setFamt(SumAmt);
				// 上海地方特色报文校验
				// 增加原凭证类型、资金性质编码、 业务类型编码的校验
				verifydto.setOriginalVtCode(OriginalVtCode);
				verifydto.setFundTypeCode(FundTypeCode);
				verifydto.setBusinessTypeCode(BusinessTypeCode);
				verifydto.setPayVoucherNo(OriginalVoucherNo);
				String returnmsg = voucherVerify.checkValid(verifydto,
						MsgConstant.VOUCHER_NO_8207);
				if (returnmsg != null) {// 返回错误信息签收失败
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}

				/**
				 *校验主表金额是否与子表金额相等
				 * 
				 */
				if (maindto.getNsumamt().compareTo(sumAmt) != 0) {
					String errMsg = "主单金额与明细累计金额不相等，主单金额："
							+ maindto.getNsumamt() + " 明细累计金额： " + sumAmt;
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

				// 接收8207商行和财政回单处理
				// <VoucherFlag>0发送单/1回单/ </ VoucherFlag >
				if (null != vDto
						&& DealCodeConstants.VOUCHER_FLAT_1.equals(vDto
								.getSvoucherflag())) {
					TvVoucherinfoDto tmpDto = new TvVoucherinfoDto();
					tmpDto.setSvoucherno(vDto.getSvoucherno());
					tmpDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS); // 状态已回单
					tmpDto.setSvoucherflag(DealCodeConstants.VOUCHER_FLAT_1);
					List<TvVoucherinfoDto> list = CommonFacade.getODB()
							.findRsByDto(tmpDto);
					if (null == list || list.size() == 0) {
						voucher
								.voucherComfail(vDto.getSdealno(),
										"找不到对应的回单信息！");
						continue;
					}
					TvVoucherinfoDto searchDto = list.get(0);
					if (null != searchDto) {
						VoucherCompare voucherCompare = new VoucherCompare();
						// 对本回单凭证和接收财政发送的8207进行对比并且更新
						List returnList = new ArrayList();
						List acceptList = new ArrayList();
						// 回单信息
						returnList.add(vDto);
						returnList.add(maindto);
						returnList.add(subDtoList);
						// 原始信息
						acceptList.add(searchDto);
						TfPaymentDetailsmainDto tfPaymentDetailsmainDto = new TfPaymentDetailsmainDto();
						tfPaymentDetailsmainDto.setIvousrlno(Long
								.valueOf(searchDto.getSdealno()));
						acceptList.add(CommonFacade.getODB().findRsByDto(
								tfPaymentDetailsmainDto).get(0));
						TfPaymentDetailssubDto tfPaymentDetailssubDto = new TfPaymentDetailssubDto();
						tfPaymentDetailssubDto.setIvousrlno(Long
								.valueOf(searchDto.getSdealno()));
						acceptList.add(CommonFacade.getODB().findRsByDto(
								tfPaymentDetailssubDto));
						String msg = voucherCompare.compare8207AcceptAndReturn(
								returnList, acceptList);
						if (StringUtils.isBlank(msg)) {
							// 签收成功
							try {
								voucher
										.voucherConfirmSuccess(vDto
												.getSdealno());
							} catch (ITFEBizException e) {
								logger.error(e);
								VoucherException voucherE = new VoucherException();
								voucherE.saveErrInfo(VtCode, e);
								throw new ITFEBizException("签收凭证报文" + VtCode
										+ "出现异常", e);
							}
							vDto.setSdemo("读取回单成功");	//详细信息设为空
							vDto
									.setSstatus(DealCodeConstants.VOUCHER_READRETURN);
							DatabaseFacade.getODB().update(vDto);
							DatabaseFacade.getODB().update(
									(IDto) acceptList.get(1));
							DatabaseFacade.getODB().update(
									CommonUtil
											.listTArray((List<IDto>) acceptList
													.get(2)));
						} else {
							voucher.voucherComfail(vDto.getSdealno(), msg);
						}
					} else {
						voucher
								.voucherComfail(vDto.getSdealno(),
										"找不到对应的凭证信息！");
					}
					continue;
				}

				/**
				 * 业务表入库，校验报文合法性
				 * 
				 * 业务主表，子表入库失败即签收失败
				 */
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(
						CommonUtil.listTArray(subDtoList));
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

			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0) {
				try {
					VoucherUtil.voucherVerifyUpdateStatus(vDto, null, "校验成功",
							true);
				} catch (JAFDatabaseException e) {
					logger.error(e);
					VoucherException.saveErrInfo(vDto.getSvtcode(), e
							.getMessage());
				}
				continue;
			}

			// 更新凭证状态为"校验中"
			try {
				VoucherUtil.voucherVerifyUpdateStatus(vDto, maindto
						.getSoriginalvtcode());
				// 凭证比对
				VoucherCompare voucherCompare = new VoucherCompare();
				voucherCompare.VoucherCompare(vDto, maindto, subDtoList);
			} catch (JAFDatabaseException e) {
				String errMsg = "凭证编号：" + vDto.getSvoucherno()
						+ "更新凭证状态为\"校验中\"操作失败";
				logger.error(e);
				VoucherException.saveErrInfo(vDto.getSvtcode(), errMsg
						+ e.getMessage());
			} catch (ITFEBizException e) {
				logger.error(e);
				VoucherException.saveErrInfo(vDto.getSvtcode(), e.getMessage());
			}
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

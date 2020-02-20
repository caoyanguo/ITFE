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

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvPayoutDetailListDto;
import com.cfcc.itfe.persistence.dto.TvPayoutDetailMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherVerify;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Recv5257MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv5257MsgServer.class);
	private Voucher voucher;

	/**
	 * 人行发往财政的实拨拨款凭证明细清单（报文编号5257）
	 */
	@SuppressWarnings( { "unchecked", "null" })
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
			logger.error("解析实拨拨款明细清单5257报文出现错误！", e);
			throw new ITFEBizException("解析实拨拨款明细清单5257报文出现错误！", e);

		}
		// 获取VoucherBody
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String AdmDivCode = "";// 行政区划代码
		String StYear = "";// 年度
		String VtCode = "";// 凭证类型

		String currentDate = TimeFacade.getCurrentStringTime();// 当前系统日期
		List<String> voucherList = new ArrayList<String>();

		// 公用参数 —— 行政区划代码、年度和凭证类型
		if (VoucherBodyList.size() > 0) {
			Element element = (Element) VoucherBodyList.get(0);
			AdmDivCode = element.attribute("AdmDivCode").getText();
			StYear = element.attribute("StYear").getText();
			VtCode = element.attribute("VtCode").getText();
		}

		ArrayList<BigDecimal> amtList = new ArrayList<BigDecimal>(); // 负金额list
		TvPayoutDetailMainDto maindto = null; // 主表
		TvPayoutDetailListDto listdto = null;// 子表

		List subDtoList = null;
		List lists = new ArrayList();
		List list = null;
		// 凭证序列号
		String mainvou = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		// 解析报文
		for (int i = 0; i < VoucherBodyList.size(); i++) {
			try {

				String ErrorMsg="";
				// 获取VoucherBody
				Element element = (Element) VoucherBodyList.get(i);
				// 报文头与报文体凭证编号
				String VoucherNo = element.attribute("VoucherNo").getText();// 凭证编号
				// 获取报文头信息
				Element elementVoucher = (Element) element
						.selectSingleNode("Voucher");
				// 获取明细信息List
				List listDetail = elementVoucher.selectSingleNode("DetailList")
						.selectNodes("Detail");
				// 统计明细金额
				BigDecimal sumAmt = new BigDecimal("0.00");
				// 报文校验信息dto
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				VoucherVerify voucherVerify = new VoucherVerify();
				/**
				 * 凭证信息
				 */
				String Attach = ((Element) VoucherBodyList.get(i))
						.elementText("Attach");// 附加信息
				/**
				 * 正式解析报文 <主单信息>
				 */
				String Id = elementVoucher.elementText("Id");// 拨款凭证清单Id
				/*
				 * 行政区划代码、业务年度、凭证类型编号已解析
				 */
				String VouDate = elementVoucher.elementText("VouDate"); // 凭证日期
				String VoucherNo_ID = elementVoucher.elementText("VoucherNo");// 拨款凭证清单ID
				String TreCode = elementVoucher.elementText("TreCode");// 
				String FinOrgCode = elementVoucher.elementText("FinOrgCode");// 
				String PayAmt = elementVoucher.elementText("PayAmt"); // 汇总拨款总金额

				String PrintUser = elementVoucher.elementText("PrintUser");// 打印人
				String Remark = elementVoucher.elementText("Remark");// 备注
				String Hold1 = elementVoucher.elementText("Hold1");// 预留字段1
				String Hold2 = elementVoucher.elementText("Hold2");// 预留字段2

				/**
				 * 保存解析数据 主表
				 **/
				maindto = new TvPayoutDetailMainDto();
				mainvou = dealnos.get(VoucherNo);// 获取序列号
				maindto.setIvousrlno(Long.valueOf(mainvou));// 凭证流水号
				maindto.setSid(Id); // S_ID IS '拨款清单明细编号';
				maindto.setSadmdivcode(AdmDivCode); // S_ADMDIVCODE IS '行政区划代码';
				maindto.setSstyear(StYear); // S_STYEAR IS '业务年度';
				maindto.setSvtcode(VtCode); // S_VTCODE IS '凭证类型编号5257';
				maindto.setSvoudate(VouDate); // S_VOUDATE IS '凭证日期';
				maindto.setSvoucherno(VoucherNo_ID); // S_VOUCHERNO IS '凭证号';
				maindto.setStrecode(TreCode);// 
				maindto.setSfinorgcode(FinOrgCode);
				maindto.setNpayamt(new BigDecimal(PayAmt)); // N_PAYAMT//
				// '汇总拨款金额';
				if (maindto.getNpayamt().compareTo(new BigDecimal("0.00")) == -1) {
					amtList.add(maindto.getNpayamt());
				}
				maindto.setSprintuser(PrintUser); // S_PRINTUSER IS '打印人';
				maindto.setSremark(Remark); // S_REMARK IS '备注';
				maindto.setShold1(Hold1); // S_HOLD2 IS '预留字段1';
				maindto.setShold2(Hold2); // S_HOLD3 IS '预留字段2';

				/**
				 * 组装子表信息 组装对象TvPayoutDetailListDto
				 * 
				 */
				subDtoList = new ArrayList<TvPayoutDetailListDto>();
				for (int j = 0; j < listDetail.size(); j++) {
					// 明细合计金额
					Element elementDetail = (Element) listDetail.get(j);
					String _sId = elementDetail.elementText("Id");// 拨款明细编号
					String _svoucherbillid = elementDetail
							.elementText("VoucherBillId");// 拨款凭证清单Id
					String _spayvoucherno = elementDetail
							.elementText("PayVoucherNo");// 实拨拨款凭证单号
					String _sfundtypecode = elementDetail
							.elementText("FundTypeCode");// 资金性质编码
					String _sfundtypename = elementDetail
							.elementText("FundTypeName");// 
					String _spaytypecode = elementDetail
							.elementText("PayTypeCode");// 支付方式编码
					String _spaytypename = elementDetail
							.elementText("PayTypeName");//
					String _spayeeacctno = elementDetail
							.elementText("PayeeAcctNo");// 收款人账号
					String _spayeeacctname = elementDetail
							.elementText("PayeeAcctName");// 收款人名称
					String _spayeeacctbankname = elementDetail
							.elementText("PayeeAcctBankName");// 收款人银行
					String _spayeeacctbankno = elementDetail
							.elementText("PayeeAcctBankNo");// 收款银行行号
					String _spayacctno = elementDetail.elementText("PayAcctNo");// 付款人账号
					String _spayacctname = elementDetail
							.elementText("PayAcctName");// 付款人名称
					String _spayacctbankname = elementDetail
							.elementText("PayAcctBankName");// 付款人银行
					String _sagencycode = elementDetail
							.elementText("AgencyCode");// 预算单位编码
					String _sagencyname = elementDetail
							.elementText("AgencyName");// 预算单位名称
					String _sexpfunccode = elementDetail
							.elementText("ExpFuncCode");// 支出功能分类科目编码
					String _sexpfuncname = elementDetail
							.elementText("ExpFuncName");// 支出功能分类科目名称
					String _sexpecocode = elementDetail
							.elementText("ExpEcoCode");// 支出经济分类科目类编码
					String _sexpeconame = elementDetail
							.elementText("ExpEcoName");// 支出经济分类科目类名称
					String _spaysummarycode = elementDetail
							.elementText("PaySummaryCode");// 用途编码
					String _spaysummaryname = elementDetail
							.elementText("PaySummaryName");// 用途名称
					String _npayamt = elementDetail.elementText("PayAmt");// 支付金额
					String _dpaydate = elementDetail.elementText("PayDate");// 拨款日期
					String _sdetailHold1 = elementDetail.elementText("Hold1");// 预留字段1
					String _sdetailHold2 = elementDetail.elementText("Hold2");// 预留字段2
					String _sdetailHold3 = elementDetail.elementText("Hold3");// 预留字段3
					String _sdetailHold4 = elementDetail.elementText("Hold4");// 预留字段4

					/*
					 * 保存子表信息
					 */
					TvPayoutDetailListDto subdto = new TvPayoutDetailListDto();
					subdto.setIvousrlno(Long.valueOf(mainvou));// 子表序列号
					subdto.setSid(_sId);// S_ID IS '拨款清单明细编号';
					subdto.setSvoucherbillid(_svoucherbillid);// S_VOUCHERBILLID IS '拨款凭证清单ID';
					subdto.setSpayvoucherno(_spayvoucherno);// S_PAYVOUCHERNO IS// '拨款凭证凭证号';
					subdto.setSfundtypecode(_sfundtypecode);// S_FUNDTYPECODE IS// '资金性质编码';
					subdto.setSfundtypename(_sfundtypename);// S_FUNDTYPENAME IS/ '资金性质名称';
					subdto.setSpaytypecode(_spaytypecode);// S_PAYTYPECODE IS// '支付方式编码';
					subdto.setSpaytypename(_spaytypename);// S_PAYTYPENAME IS// '支付方式名称';
					subdto.setSpayeeacctno(_spayeeacctno);// S_PAYEEACCTNO IS/ '收款人账号';
					subdto.setSpayeeacctname(_spayeeacctname);// S_PAYEEACCTNAME/ IS '收款人名称';
					
					subdto.setSpayacctno(_spayacctno);// S_PAYACCTNO IS '付款人账号';
					subdto.setSpayacctname(_spayacctname);// S_PAYACCTNAME IS
					// '付款人名称';
					subdto.setSpayacctbankname(_spayacctbankname);// S_PAYACCTBANKNAME
					// IS
					// '付款人银行';
					subdto.setSagencycode(_sagencycode);// S_AGENCYCODE IS
					// '预算单位编码';
					subdto.setSagencyname(_sagencyname);// S_AGENCYNAME IS
					// '预算单位名称';
					subdto.setSexpfunccode(_sexpfunccode);// S_EXPFUNCCODE IS
					// '支出功能分类科目编码';
					subdto.setSexpfuncname(_sexpfuncname);// S_EXPFUNCNAME IS
					// '支持功能分类科目名称';
					subdto.setSexpecocode(_sexpecocode);// S_EXPECOCODE IS
					// '支出经济分类科目编码';
					subdto.setSexpeconame(_sexpeconame);// S_EXPECONAME IS
					// '支出经济分类科目名称';
					subdto.setSpaysummarycode(_spaysummarycode);// S_PAYSUMMARYCODE
					// IS '用途编码';
					subdto.setSpaysummaryname(_spaysummaryname);// S_PAYSUMMARYNAME
					// IS '用途名称';
					subdto.setNpayamt(new BigDecimal(_npayamt));// N_PAYAMT IS
					// '支付金额';
					// 负金额列表
					if (subdto.getNpayamt().compareTo(new BigDecimal("0.00")) == -1) {
						amtList.add(subdto.getNpayamt());
					}
					// 累加明细金额
					sumAmt = sumAmt.add(subdto.getNpayamt());

					subdto.setDpaydate(_dpaydate);// D_PAYDATE IS '拨款日期';
					subdto.setShold1(_sdetailHold1);// S_HOLD1 IS '预留字段1';
					subdto.setShold2(_sdetailHold2);// S_HOLD2 IS '预留字段2';
					subdto.setShold3(_sdetailHold3);// S_HOLD3 IS '预留字段3';
					subdto.setShold4(_sdetailHold4);// S_HOLD4 IS '预留字段4';
					
					subdto.setSpayeeacctbankname(_spayeeacctbankname);// S_PAYEEACCTBANKNAME	// '收款人银行';
					// S_PAYEEACCTBANKNO// IS '收款银行行号(人行补填)';\
					if (null!=_spayeeacctbankno&&_spayeeacctbankno.length()==12) {
						subdto.setSpayeeacctbankno(_spayeeacctbankno);
					}else {
						subdto.setSpayeeacctbankno(null);
//						String spayeeacctbankno=CollectionInfo(subdto,maindto);
//						logger.debug("-----------收款银行行号(人行补填)S_PAYEEACCTBANKNO：["+spayeeacctbankno+"]");
//						if (null!=spayeeacctbankno&&spayeeacctbankno.length()==12) {
//							subdto.setSpayeeacctbankno(spayeeacctbankno);//补录收款银行行号
//						}else {
//							ErrorMsg+=_spayvoucherno+";";
//						}
						
					}
					subDtoList.add(subdto);
				}
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
				// 增加年度，总金额的校验
				verifydto.setOfyear(StYear);
				verifydto.setFamt(PayAmt);
				// 增加校验预算单位名称
				String returnmsg = voucherVerify.checkValid(verifydto,
						MsgConstant.VOUCHER_NO_5257);

				if (returnmsg != null) {// 返回错误信息签收失败
					voucher.voucherComfail(vDto.getSdealno(), returnmsg);
					continue;
				}

				if (ErrorMsg != null&& ErrorMsg.trim().length()>0) {// 返回错误信息签收失败
					voucher.voucherComfail(vDto.getSdealno(), "未找到凭证号为["+ErrorMsg+"]的原始凭证");
					continue;
				}
				/**
				 *校验拨款清单中是否有负金额
				 */

				if (amtList.size() > 0) {
					String errMsg = "拨款清单信息中包含有负金额的数据!";
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}

				/**
				 *校验主表金额是否与子表金额相等
				 */
				if (maindto.getNpayamt().compareTo(sumAmt) != 0) {
					String errMsg = "主单金额与明细累计金额不相等，主单金额："
							+ maindto.getNpayamt() + " 明细累计金额： " + sumAmt;
					voucher.voucherComfail(vDto.getSdealno(), errMsg);
					continue;
				}

				/**
				 * 业务表入库，校验报文合法性
				 * 
				 * 业务主表，子表入库失败即签收失败
				 */
				TvPayoutDetailListDto[] subDtos = new TvPayoutDetailListDto[subDtoList
						.size()];
				subDtos = (TvPayoutDetailListDto[]) subDtoList.toArray(subDtos);
				DatabaseFacade.getODB().create(maindto);
				DatabaseFacade.getODB().create(subDtos);
				vDto.setIcount(subDtoList.size());
			} catch (JAFDatabaseException e) {
				logger.error(e);
				voucher.voucherComfail(mainvou, "保存数据库出现错误：" + e.getMessage());
				continue;
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
				VoucherException.saveErrInfo(vDto.getSvtcode(), e);
				continue;
			}

			list = new ArrayList();
			list.add(maindto);
			list.add(vDto);
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
			throw new ITFEBizException("校验凭证报文" + VtCode + "出现异常", e);
		}
		return;
	}
/**
 * 收款银行行号
 * 查找范围： 实拨资金表、历史表、索引表查找处理成功
 * 
 * 凭证编号+财政机关代码+国库代码+区划代码+年度
 * @param _spayvoucherno
 * @return
 * @throws ValidateException 
 * @throws JAFDatabaseException 
 */
	private String CollectionInfo(TvPayoutDetailListDto subdto,TvPayoutDetailMainDto maindto) throws JAFDatabaseException, ValidateException {
//		vDto=(TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto).get(0);
		TvPayoutmsgmainDto dto=new TvPayoutmsgmainDto();
		String	sql=" AND S_TAXTICKETNO='"+subdto.getSpayvoucherno()+"' AND S_TRECODE='"+maindto.getStrecode()+"' AND S_PAYUNIT='"+maindto.getSfinorgcode()+
		"' and  S_XPAYAMT='"+subdto.getNpayamt()+"' AND S_OFYEAR='"+maindto.getSstyear()+"'";
		List<TvPayoutmsgmainDto> list= CommonFacade.getODB().findRsByDtoForWhere(dto, sql);

		if (list.size()>0) {
			TvPayoutmsgmainDto tvPayoutmsgmainDto=list.get(0);
			String bankno=tvPayoutmsgmainDto.getSrecbankno();
			if (null!=bankno&&bankno.length()==12 ) {
				return bankno;
			}
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

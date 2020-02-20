package com.cfcc.itfe.voucher.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import sun.misc.BASE64Decoder;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailssubDto;
import com.cfcc.itfe.persistence.dto.TfVoucherSplitDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.encrypt.Base64;
import com.cfcc.itfe.util.transformer.Dto2MapFor8000;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.webservice.VoucherService;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

@SuppressWarnings("unchecked")
public class VoucherUtil {
	private static Log logger = LogFactory.getLog(VoucherUtil.class);
	private static HashMap map;// 接收凭证附件(绿色通道)缓存

	/**
	 * 组装凭证签章报文
	 * 
	 */
	public static String getVoucherStampXML(TvVoucherinfoDto dto)
			throws ITFEBizException {
		try {
			String stampXML = "";
			Document fxrDoc;// 临时的凭证报文
			Document successDoc;// 返回给凭证库的成功报文
			successDoc = DocumentHelper.createDocument();
			successDoc.setXMLEncoding("GBK");
			Element root = successDoc.addElement("MOF");
			Element VoucherCount = root.addElement("VoucherCount");
			VoucherCount.setText("1");
			Element newBody = root.addElement("VoucherBody");
			newBody.addAttribute("AdmDivCode", dto.getSadmdivcode().replace(
					MsgConstant.VOUCHERSAMP_ATTACH, ""));
			newBody.addAttribute("StYear", dto.getSstyear());
			newBody.addAttribute("VtCode", dto.getSvtcode());
			newBody.addAttribute("VoucherNo", dto.getSvoucherno());
			Element VoucherFlag = newBody.addElement("VoucherFlag");
			VoucherFlag.setText("1");
			// Element Return_Reason = newBody.addElement("Return_Reason");
			Element Attach = newBody.addElement("Attach");
			Attach.setText("SUCCESS");
			Element Voucher = newBody.addElement("Voucher");
			// 循环读取凭证报文，获取成功的凭证VoucherBody
			String s = FileUtil.getInstance().readFile(dto.getSfilename());
			/*
			 * 获取系统自主生成凭证报文，除5207 5209 2301 2302 5106 5108外报文 VoucherFlag回单标志：0
			 * 凭证自主生成报文 1 回单报文
			 */
			if (!(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)
					||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5287)
					||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5407)
					||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5551)
					||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5552)
					||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5553)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5257)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2252)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5671)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408)
					||((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)) && dto.getSfilename().contains("rev"+dto.getSvtcode()))
					)) {

				VoucherFlag.setText("0");// 回单标志：0 凭证自主生成报文 1 回单报文
				stampXML = com.cfcc.itfe.util.FileUtil.getInstance().readFile(
						dto.getSfilename());
				Voucher.setText(base64Encode(stampXML));
				return successDoc.asXML().replaceAll("&lt;", "<").replaceAll(
						"&gt;", ">");
			}
			/*
			 * 获取回单报文
			 */
			fxrDoc = DocumentHelper.parseText(s);
			List listNodes = fxrDoc.selectSingleNode("MOF").selectNodes(
					"VoucherBody");
			for (int i = 0; i < listNodes.size(); i++) {
				String VoucherNo = ((Element) listNodes.get(i)).attribute(
						"VoucherNo").getText();
				if (VoucherNo.equals(dto.getSvoucherno())) {
					Element voucherbody = (Element) listNodes.get(i);
					Node voucherNode = voucherbody.selectSingleNode("Voucher");

					/**
					 * 此处添加回填字段如 金额、清算日期（实拨资金、划款申请、划款申请退款）
					 * 
					 */
					if(MsgConstant.VOUCHER_NO_5407.equals(dto.getSvtcode()))
					{
						if (null != voucherNode.selectSingleNode("XAcctDate")) {
							TvInCorrhandbookDto maindto = new TvInCorrhandbookDto();
							maindto.setIvousrlno(Long.valueOf(dto.getSdealno()));
							String xacctdate = maindto.getDxacctdate();
							if(xacctdate==null||"".equals(xacctdate))
								xacctdate = TimeFacade.getCurrentStringTime();
							voucherNode.selectSingleNode("XAcctDate").setText(xacctdate + "");
						}
					}else if (MsgConstant.VOUCHER_NO_5207.equals(dto.getSvtcode())
							|| MsgConstant.VOUCHER_NO_5267.equals(dto
									.getSvtcode())) {
						TvPayoutmsgmainDto mainDto = new TvPayoutmsgmainDto();
						mainDto.setStaxticketno(dto.getSvoucherno());
						mainDto.setSbizno(dto.getSdealno());
						mainDto.setStrecode(dto.getStrecode());
						List<TvPayoutmsgmainDto> list = CommonFacade.getODB()
								.findRsByDto(mainDto);
						if (null != list && list.size() > 0) {
							mainDto = list.get(0);
						} else {
							throw new ITFEBizException("根据索引表记录查询业务表信息出错！");
						}
						String XPayDate = mainDto.getSxpaydate();
						// 浙江地方需求，应对清算日期签章提示报文被篡改情况，暂时提取系统日期
						if (StringUtils.isBlank(XPayDate)
								&& ITFECommonConstant.PUBLICPARAM
										.indexOf("singStampflag") >= 0)
							XPayDate = TimeFacade.getCurrentStringTime();
						String sxagentbusinessno = mainDto.getSdealno();// 原交易流水号
						BigDecimal XPayAmt = mainDto.getNmoney();
						if (ITFECommonConstant.ISCHECKPAYPLAN.equals("1")) {
							XPayDate = TimeFacade.getCurrentStringTime();
							XPayAmt = dto.getNmoney();
						}
						if (ITFECommonConstant.PUBLICPARAM
								.indexOf(",payoutstampmode=true,") >= 0
								&& dto.getSadmdivcode().indexOf(
										MsgConstant.VOUCHERSAMP_ATTACH) >= 0) {
							XPayAmt = new BigDecimal("0");
							XPayDate = "";
							sxagentbusinessno = "";
							dto.setSadmdivcode(dto.getSadmdivcode().replace(
									MsgConstant.VOUCHERSAMP_ATTACH, ""));
						}
						// 防止规范中大小写区别
						if (null != voucherNode.selectSingleNode("XPayAmt")) {
							voucherNode.selectSingleNode("XPayAmt").setText(
									XPayAmt + "");
						} else if (null != voucherNode
								.selectSingleNode("XpayAmt")) {
							voucherNode.selectSingleNode("XpayAmt").setText(
									XPayAmt + "");
						}
						voucherNode.selectSingleNode("XPayDate").setText(
								XPayDate + "");
						voucherNode.selectSingleNode("XAgentBusinessNo")
								.setText(sxagentbusinessno + "");
						
						TsTreasuryDto tredto = SrvCacheFacade.cacheTreasuryInfo(null).get(dto.getStrecode());
						//拨款明细补充明细数据中的X字段
						if (StateConstant.COMMON_YES.equals(String.valueOf(tredto==null?"":tredto.getSpayunitname()))) {
							voucherNode.selectSingleNode("XPayAmt").setText(
									mainDto.getSxpayamt() + "");
							fillPayoutDetailXinfo(mainDto, voucherNode);	
						} else{
						/**
						 * 根据业务主表查找对应的明信子信息(上海特色) 用于回单补录明细信息中的
						 * XpayAmt-金额、XPayDate
						 * -支付日期、XAgentBusinessNo-银行交易流水号、XAddWord-失败原因
						 * 
						 * @author 张会斌
						 */
						if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
								|| MsgConstant.VOUCHER_NO_5267.equals(dto
										.getSvtcode())
								|| MsgConstant.VOUCHER_NO_5207.equals(dto
										.getSvtcode())) {
							List subList = PublicSearchFacade
									.findSubDtoByMain(mainDto);
							if (subList == null || subList.size() == 0)
								throw new ITFEBizException(
										"根据业务主表记录查询子表明细信息出错！");
							HashMap subdtoMap = VoucherUtil
									.convertListToMap(subList);
							List<Node> detailList = voucherNode
									.selectSingleNode("DetailList")
									.selectNodes("Detail");
							for (Node detailNode : detailList) {
								String sDetailId = ((Element) detailNode)
										.elementText("Id");
								TvPayoutmsgsubDto subdto = (TvPayoutmsgsubDto) subdtoMap
										.get(sDetailId);
								if (subdto == null)
									throw new ITFEBizException("未找到和报文明细Id"
											+ sDetailId + "匹配的明细子表记录！");
								if (detailNode.selectSingleNode("XPayDate") != null)
									detailNode.selectSingleNode("XPayDate")
											.setText(XPayDate + "");
								if (detailNode
										.selectSingleNode("XAgentBusinessNo") != null)
									detailNode.selectSingleNode(
											"XAgentBusinessNo").setText(
											sxagentbusinessno + "");
								if (detailNode.selectSingleNode("XpayAmt") != null)
									detailNode.selectSingleNode("XpayAmt")
											.setText(subdto.getNmoney() + "");
								if (ITFECommonConstant.PUBLICPARAM
										.indexOf(",payoutstampmode=true,") >= 0
										&& dto.getSadmdivcode().indexOf(
												MsgConstant.VOUCHERSAMP_ATTACH) >= 0) {
									if (detailNode.selectSingleNode("XPayAmt") != null)
										detailNode.selectSingleNode("XPayAmt")
												.setText("0");

								} else {
									if (detailNode.selectSingleNode("XPayAmt") != null)
										detailNode
												.selectSingleNode("XPayAmt")
												.setText(
														subdto.getNmoney() + "");
								}
								if (MsgConstant.VOUCHER_NO_5267.equals(dto
										.getSvtcode())
										&& detailNode
												.selectSingleNode("XAddWord") != null)
									detailNode.selectSingleNode("XAddWord")
											.setText(dto.getSattach() + "");
							}
						 }
						}

					} else if (MsgConstant.VOUCHER_NO_5671.equals(dto
							.getSvtcode())||MsgConstant.VOUCHER_NO_5408.equals(dto
									.getSvtcode())) {
						TvNontaxmainDto mainDto = new TvNontaxmainDto();
						mainDto.setSvoucherno(dto.getSvoucherno());
						mainDto.setSdealno(dto.getSdealno());
						mainDto.setStrecode(dto.getStrecode());
						List<TvNontaxmainDto> list = CommonFacade.getODB().findRsByDto(mainDto);
						if (null != list && list.size() > 0) {
							mainDto = list.get(0);
						} else {
							throw new ITFEBizException("根据索引表记录查询业务表信息出错！");
						}
						String XPayDate = mainDto.getSxtradate();
						if (null == XPayDate||"".equals(XPayDate.trim())) {
							XPayDate = mainDto.getScommitdate();
						}
						BigDecimal XPayAmt = mainDto.getNxmoney();
						if(XPayAmt==null||XPayAmt.compareTo(new BigDecimal(0))==0)
							XPayAmt = mainDto.getNmoney();
						if (ITFECommonConstant.PUBLICPARAM
								.indexOf(",payoutstampmode=true,") >= 0
								&& dto.getSadmdivcode().indexOf(
										MsgConstant.VOUCHERSAMP_ATTACH) >= 0) {
							XPayAmt = new BigDecimal("0");
							XPayDate = "";
							dto.setSadmdivcode(dto.getSadmdivcode().replace(
									MsgConstant.VOUCHERSAMP_ATTACH, ""));
						}
						if(MsgConstant.VOUCHER_NO_5408.equals(dto.getSvtcode())){
							voucherNode.selectSingleNode("XAddWord").setText(mainDto.getSxaddword()+ "");
							voucherNode.selectSingleNode("XSumAmt").setText(XPayAmt + "");
							voucherNode.selectSingleNode("XAcctDate").setText(XPayDate + "");
						}else{
							voucherNode.selectSingleNode("XPbcAddWord").setText(mainDto.getSxaddword()+ "");
							voucherNode.selectSingleNode("XPbcTraAmt").setText(XPayAmt + "");
							voucherNode.selectSingleNode("XPbcTraDate").setText(XPayDate + "");
						}
					}else if (MsgConstant.VOUCHER_NO_2301.equals(dto
							.getSvtcode())) {
						TvPayreckBankDto mainDto = new TvPayreckBankDto();
						mainDto.setSvouno(dto.getSvoucherno());
						mainDto.setIvousrlno(Long.valueOf(dto.getSdealno()));
						mainDto.setStrecode(dto.getStrecode());
						List<TvPayreckBankDto> list = CommonFacade.getODB()
								.findRsByDto(mainDto);
						if (null != list && list.size() > 0) {
							mainDto = list.get(0);
						} else {
							throw new ITFEBizException("根据索引表记录查询业务表信息出错！");
						}
						Date xpaydate = mainDto.getSxcleardate();
						if (null == xpaydate) {
							xpaydate = mainDto.getDentrustdate();
						}
						String XPayDate = TimeFacade.formatDate(xpaydate,
								"yyyyMMdd");
						BigDecimal XPayAmt = mainDto.getSxpayamt();
						if(XPayAmt==null||XPayAmt.compareTo(new BigDecimal(0))==0)
							XPayAmt = dto.getNmoney();
						if (ITFECommonConstant.ISCHECKPAYPLAN.equals("1")) {
							XPayDate = TimeFacade.getCurrentStringTime();
							XPayAmt = dto.getNmoney();
						}
						if (ITFECommonConstant.PUBLICPARAM
								.indexOf(",payoutstampmode=true,") >= 0
								&& dto.getSadmdivcode().indexOf(
										MsgConstant.VOUCHERSAMP_ATTACH) >= 0) {
							XPayAmt = new BigDecimal("0");
							XPayDate = "";
							dto.setSadmdivcode(dto.getSadmdivcode().replace(
									MsgConstant.VOUCHERSAMP_ATTACH, ""));
						}
						voucherNode.selectSingleNode("XPayAmt").setText(
								XPayAmt + "");
						voucherNode.selectSingleNode("XClearDate").setText(
								XPayDate + "");
						voucherNode.selectSingleNode("VouDate").setText(
								TimeFacade.formatDate(mainDto.getDvoudate(),
										"yyyyMMdd")
										+ "");
						/**
						 * 上海无纸化授权支付回单中填写支付发起行行号和附言(支付方式：0-直接 1-授权)
						 * 
						 * @author 张会斌 “XPaySndBnkNo支付发起行行号” 填写3143中支付发起行行号
						 *         “XAddWord附言” 填写3143中支付交易序号+报文编号+支付委托日期
						 */
						if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
								&& mainDto.getSpaymode().equals("1")) {
							String XPaySndBnkNo = mainDto.getSxpaysndbnkno();
							String XAddWord = mainDto.getSaddword();
							voucherNode.selectSingleNode("XPaySndBnkNo")
									.setText(XPaySndBnkNo + "");
							voucherNode.selectSingleNode("XAddWord").setText(
									XAddWord + "");
						}
					} else if (MsgConstant.VOUCHER_NO_2302.equals(dto
							.getSvtcode())) {
						TvPayreckBankBackDto mainDto = new TvPayreckBankBackDto();
						mainDto.setSvouno(dto.getSvoucherno());
						mainDto.setIvousrlno(Long.valueOf(dto.getSdealno()));
						mainDto.setStrecode(dto.getStrecode());
						List<TvPayreckBankBackDto> list = CommonFacade.getODB()
								.findRsByDto(mainDto);
						if (null != list && list.size() > 0) {
							mainDto = list.get(0);
						} else {
							throw new ITFEBizException("根据索引表记录查询业务表信息出错！");
						}
						Date xpaydate = mainDto.getSxcleardate();
						if (null == xpaydate) {
							xpaydate = mainDto.getDentrustdate();
						}
						String XPayDate = TimeFacade.formatDate(xpaydate,
								"yyyyMMdd");
						BigDecimal XPayAmt = mainDto.getSxpayamt();
						if(XPayAmt==null||XPayAmt.compareTo(new BigDecimal(0))==0)
							XPayAmt = dto.getNmoney();
						if (ITFECommonConstant.ISCHECKPAYPLAN.equals("1")) {
							XPayDate = TimeFacade.getCurrentStringTime();
							XPayAmt = dto.getNmoney();
						}
						if (ITFECommonConstant.PUBLICPARAM
								.indexOf(",payoutstampmode=true,") >= 0
								&& dto.getSadmdivcode().indexOf(
										MsgConstant.VOUCHERSAMP_ATTACH) >= 0) {
							XPayAmt = new BigDecimal("0");
							XPayDate = "";
							dto.setSadmdivcode(dto.getSadmdivcode().replace(
									MsgConstant.VOUCHERSAMP_ATTACH, ""));
						}
						voucherNode.selectSingleNode("XPayAmt").setText(
								MtoCodeTrans.transformString(new BigDecimal(
										"0.00").subtract(XPayAmt.abs())));
						voucherNode.selectSingleNode("XClearDate").setText(
								XPayDate + "");
						voucherNode.selectSingleNode("VouDate").setText(
								TimeFacade.formatDate(mainDto.getDvoudate(),
										"yyyyMMdd")
										+ "");
						/**
						 * 上海无纸化授权支付退款回单中填写支付发起行行号和附言(支付方式：0-直接 1-授权)
						 * 
						 * @author 张会斌 “XPaySndBnkNo支付发起行行号” 填写3144中填写代理银行行号 　 *
						 *         “XAddWord附言” 填写 原交易流水号。
						 */
						if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
								&& mainDto.getSpaymode().equals("1")) {
							String XPaySndBnkNo = mainDto.getSxpaysndbnkno();
							String XAddWord = mainDto.getSaddword();
							voucherNode.selectSingleNode("XPaySndBnkNo")
									.setText(XPaySndBnkNo + "");
							voucherNode.selectSingleNode("XAddWord").setText(
									XAddWord + "");
						}
					} else if (MsgConstant.VOUCHER_NO_5209.equals(dto
							.getSvtcode())) {
						TvDwbkDto mainDto = new TvDwbkDto();
						mainDto.setSbizno(dto.getSdealno());
						mainDto.setSaimtrecode(dto.getStrecode());
						List<TvDwbkDto> list = CommonFacade.getODB()
								.findRsByDto(mainDto);
						if (null != list && list.size() > 0) {
							mainDto = list.get(0);
						} else {
							throw new ITFEBizException("根据索引表记录查询业务表信息出错！");
						}
						String XPayDate = TimeFacade.getCurrentStringTime();
						BigDecimal XPayAmt = mainDto.getXpayamt();
						if(XPayAmt==null||XPayAmt.compareTo(new BigDecimal(0))==0)
							XPayAmt = mainDto.getXpayamt();
						if (ITFECommonConstant.ISCHECKPAYPLAN.equals("1")) {
							XPayDate = TimeFacade.getCurrentStringTime();
							XPayAmt = dto.getNmoney();
						}
						if (ITFECommonConstant.PUBLICPARAM
								.indexOf(",payoutstampmode=true,") >= 0
								&& dto.getSadmdivcode().indexOf(
										MsgConstant.VOUCHERSAMP_ATTACH) >= 0) {
							XPayAmt = new BigDecimal("0");
							XPayDate = "";
							dto.setSadmdivcode(dto.getSadmdivcode().replace(
									MsgConstant.VOUCHERSAMP_ATTACH, ""));
						}
						// 防止规范中大小写区别
						if (null != voucherNode.selectSingleNode("XPayAmt")) {
							voucherNode.selectSingleNode("XPayAmt").setText(
									XPayAmt + "");
						} else if (null != voucherNode
								.selectSingleNode("XpayAmt")) {
							voucherNode.selectSingleNode("XpayAmt").setText(
									XPayAmt + "");
						}
						voucherNode.selectSingleNode("XPayDate").setText(
								XPayDate + "");
					} else if (MsgConstant.VOUCHER_NO_5106.equals(dto
							.getSvtcode())) {
						TvGrantpaymsgmainDto mainDto = new TvGrantpaymsgmainDto();
						mainDto.setIvousrlno(Long.parseLong(dto.getSdealno()));
						if (!(ITFECommonConstant.PUBLICPARAM
								.indexOf(",payoutstampmode=true,") >= 0 && dto
								.getSadmdivcode().indexOf(
										MsgConstant.VOUCHERSAMP_ATTACH) >= 0))
							mainDto
									.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
						List<TvGrantpaymsgmainDto> listDto = CommonFacade
								.getODB().findRsByDto(mainDto);
						if (null != listDto && listDto.size() > 0) {
							mainDto = listDto.get(0);
						} else {
							throw new ITFEBizException("根据索引表记录查询业务表信息出错！");
						}
						String XPayDate = listDto.get(0).getSxacctdate();
						if (XPayDate == null || XPayDate.equals("")
								|| XPayDate.equals("NULL")) {
							XPayDate = listDto.get(0).getSaccdate();
						}
						if (ITFECommonConstant.PUBLICPARAM
								.indexOf(",payoutstampmode=true,") >= 0
								&& dto.getSadmdivcode().indexOf(
										MsgConstant.VOUCHERSAMP_ATTACH) >= 0) {
							XPayDate = "";
							dto.setSadmdivcode(dto.getSadmdivcode().replace(
									MsgConstant.VOUCHERSAMP_ATTACH, ""));
						}
						voucherNode.selectSingleNode("XAcctDate").setText(
								XPayDate + "");
					} else if (MsgConstant.VOUCHER_NO_5108.equals(dto
							.getSvtcode())) {
						TvDirectpaymsgmainDto mainDto = new TvDirectpaymsgmainDto();
						mainDto.setIvousrlno(Long.parseLong(dto.getSdealno()));
						List<TvDirectpaymsgmainDto> list = CommonFacade
								.getODB().findRsByDto(mainDto);
						if (list != null && list.size() > 0) {
							mainDto = list.get(0);
							String XPayDate = mainDto.getSxacctdate();
							if (XPayDate == null || XPayDate.equals("")
									|| XPayDate.equals("NULL")) {
								XPayDate =mainDto.getSaccdate();
							}
							if (ITFECommonConstant.PUBLICPARAM
									.indexOf(",payoutstampmode=true,") >= 0
									&& dto.getSadmdivcode().indexOf(
											MsgConstant.VOUCHERSAMP_ATTACH) >= 0) {
								XPayDate = "";
								dto.setSadmdivcode(dto.getSadmdivcode()
										.replace(
												MsgConstant.VOUCHERSAMP_ATTACH,
												""));
							}
							voucherNode.selectSingleNode("XAcctDate").setText(
									XPayDate + "");
						} else {
							throw new ITFEBizException("根据索引表记录查询业务表信息出错！");
						}
					} else if (MsgConstant.VOUCHER_NO_5351.equals(dto
							.getSvtcode())) {
						TfGrantpayAdjustmainDto mainDto = new TfGrantpayAdjustmainDto();
						mainDto.setIvousrlno(Long.parseLong(dto.getSdealno()));
						mainDto
								.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
						List<TfGrantpayAdjustmainDto> list = CommonFacade
								.getODB().findRsByDto(mainDto);
						if (list != null && list.size() > 0) {
							mainDto = list.get(0);
							String XPayDate = mainDto.getSxaccdate();
							if (XPayDate == null || XPayDate.equals("")
									|| XPayDate.equals("NULL")) {
								XPayDate = TimeFacade.getCurrentStringTime();
							}
							voucherNode.selectSingleNode("XAccDate").setText(
									XPayDate + "");
						} else {
							throw new ITFEBizException("根据索引表记录查询业务表信息出错！");
						}
						/**
						 * 根据业务主表查找对应的明信子信息(上海特色) 用于回单补录明细信息中的 XDealResult-处理结果
						 * XAddWord-附言
						 * 
						 * @author 张会斌
						 */
						List subList = PublicSearchFacade
								.findSubDtoByMain(mainDto);
						if (subList == null || subList.size() == 0)
							throw new ITFEBizException("根据业务主表记录查询子表明细信息出错！");
						HashMap subdtoMap = VoucherUtil
								.convertListToMap(subList);
						List<Node> detailList = voucherNode.selectSingleNode(
								"DetailList").selectNodes("Detail");
						for (Node detailNode : detailList) {
							String sDetailId = ((Element) detailNode)
									.elementText("Id");
							TfGrantpayAdjustsubDto subdto = (TfGrantpayAdjustsubDto) subdtoMap
									.get(sDetailId);
							if (subdto == null)
								throw new ITFEBizException("未找到和报文明细Id"
										+ sDetailId + "匹配的明细子表记录！");
							detailNode
									.selectSingleNode("XDealResult")
									.setText(
											dto
													.getSstatus()
													.equals(
															DealCodeConstants.VOUCHER_SUCCESS_NO_BACK) ? "1"
													: "0");
							detailNode.selectSingleNode("XAddWord").setText(
									dto.getSdemo() + "");
						}
					} else if (MsgConstant.VOUCHER_NO_5201.equals(dto
							.getSvtcode())) {
						TfDirectpaymsgmainDto mainDto = new TfDirectpaymsgmainDto();
						mainDto.setIvousrlno(Long.parseLong(dto.getSdealno()));
						mainDto
								.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
						List<TfDirectpaymsgmainDto> list = CommonFacade
								.getODB().findRsByDto(mainDto);
						if (list != null && list.size() > 0) {
							mainDto = list.get(0);
							String XPayDate = mainDto.getSxpaydate();
							voucherNode.selectSingleNode("XPayDate").setText(
									XPayDate + "");
							voucherNode.selectSingleNode("XAgentBusinessNo")
									.setText(mainDto.getSdealno() + "");
							voucherNode
									.selectSingleNode("XPayAmt")
									.setText(
											dto
													.getSstatus()
													.equals(
															DealCodeConstants.VOUCHER_SUCCESS_NO_BACK) ? MtoCodeTrans
													.transformString(mainDto
															.getNxpayamt())
													: "0.00");// 实际支付金额，支付失败填0
							voucherNode
									.selectSingleNode("XPayeeAcctBankName")
									.setText(mainDto.getSpayacctbankname() + "");
							voucherNode.selectSingleNode("XPayeeAcctNo")
									.setText(mainDto.getSpayeeacctno() + "");
							voucherNode.selectSingleNode("XPayeeAcctName")
									.setText(mainDto.getSpayeeacctname() + "");

						} else {
							throw new ITFEBizException("根据索引表记录查询业务表信息出错！");
						}
						List subList = PublicSearchFacade
								.findSubDtoByMain(mainDto);
						if (subList == null || subList.size() == 0)
							throw new ITFEBizException("根据业务主表记录查询子表明细信息出错！");
						HashMap subdtoMap = VoucherUtil
								.convertListToMap(subList);
						List<Node> detailList = voucherNode.selectSingleNode(
								"DetailList").selectNodes("Detail");
						for (Node detailNode : detailList) {
							String sDetailId = ((Element) detailNode)
									.elementText("Id");
							TfDirectpaymsgsubDto subdto = (TfDirectpaymsgsubDto) subdtoMap
									.get(sDetailId);
							if (subdto == null)
								throw new ITFEBizException("未找到和报文明细Id"
										+ sDetailId + "匹配的明细子表记录！");
							detailNode.selectSingleNode("XPayDate").setText(
									mainDto.getSxpaydate() + "");
							detailNode.selectSingleNode("XAgentBusinessNo")
									.setText(mainDto.getSdealno() + "");
							detailNode.selectSingleNode("XPayAmt").setText(
									subdto.getNpayamt() + "");
							detailNode
									.selectSingleNode("XPayeeAcctBankName")
									.setText(
											subdto.getSpayeeacctbankname() + "");
							detailNode.selectSingleNode("XPayeeAcctNo")
									.setText(subdto.getSpayeeacctno() + "");
							detailNode.selectSingleNode("XPayeeAcctName")
									.setText(subdto.getSpayeeacctname() + "");
						}
					} else if (MsgConstant.VOUCHER_NO_8207.equals(dto
							.getSvtcode())) {
						VoucherFlag.setText("0");// 回单标志：0 凭证自主生成报文 1 回单报文
					}else if(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5551)
					||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5552)
					||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5553))
					{
						voucherNode.selectSingleNode("XAcctDate").setText(voucherNode.selectSingleNode("ClearDate").getText());
					}
					Element oldVoucher = (Element) voucherbody
							.selectSingleNode("Voucher");
					Document vouDoc = DocumentHelper.createDocument();
					vouDoc.setXMLEncoding("GBK");
					Element newVou = vouDoc.addElement("Voucher");
					newVou.setContent(oldVoucher.content());
					String newStr = vouDoc.asXML();
					logger.debug("凭证签章写入的未编码报文：" + newStr);
					Voucher.setText(VoucherUtil.base64Encode(newStr));
				}
			}
			stampXML = successDoc.asXML();
			logger.debug("凭证签章写入的报文：" + stampXML);
			dto.setSadmdivcode(dto.getSadmdivcode().replace(
					MsgConstant.VOUCHERSAMP_ATTACH, ""));
			return stampXML;
		} catch (ITFEBizException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证签章报文出现异常！", e);
		}

	}

	/**
	 * 组装签章位置报文
	 * 
	 * @throws ITFEBizException
	 * 
	 */
	public static String getStampPotisionXML(String stampPosition,
			String stampID) throws ITFEBizException {
		try {
			String stampPotisionXML = "";
			Document stampDoc = null;
			stampDoc = DocumentHelper.createDocument();
			stampDoc.setXMLEncoding("GBK");
			Element Root = stampDoc.addElement("MOF");
			Element Stamp = Root.addElement("Stamp");
			Stamp.addAttribute("No", stampPosition);
			Stamp.setText(stampID);
			stampPotisionXML = stampDoc.asXML().replaceAll("&lt;", "<")
					.replaceAll("&gt;", ">");
			logger.debug("凭证签章写入的签章位置报文：" + stampPotisionXML);
			return stampPotisionXML;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装签章位置报文出现异常！", e);
		}

	}

	/**
	 * 组装签名位置报文
	 * 
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 * 
	 */
	public static HashMap getStampPotisionXML(TvVoucherinfoDto dto)
			throws ITFEBizException, JAFDatabaseException {
		TsStamppositionDto sDto = new TsStamppositionDto();
		sDto.setSorgcode(dto.getSorgcode());
		sDto.setStrecode(dto.getStrecode());
		sDto.setSvtcode(dto.getSvtcode());

		List<TsStamppositionDto> list = null;
		try {
			list = CommonFacade.getODB().findRsByDto(sDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查找凭证类型：" + dto.getSvtcode()
					+ " 签名位置异常！", e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("查找凭证类型：" + dto.getSvtcode()
					+ " 签名位置异常！", e);
		}

		if (list == null || list.size() == 0) {
			throw new ITFEBizException("国库：" + dto.getStrecode() + " 凭证类型："
					+ dto.getSvtcode() + " 签名位置在 [签章位置参数维护] 中未维护！");
		}
		try {
			// 获取证书国库代码中的证书ID
			HashMap<String, TsTreasuryDto> tremap = SrvCacheFacade
					.cacheTreasuryInfo(null);
			TsTreasuryDto _dto = tremap.get(dto.getStrecode());
			String stampPotisionXML = "";
			Document stampDoc = null;
			stampDoc = DocumentHelper.createDocument();
			stampDoc.setXMLEncoding("GBK");
			Element Root = stampDoc.addElement("MOF");
			for (TsStamppositionDto stampDto : list) {
				Element Stamp = Root.addElement("Stamp");
				if(ITFECommonConstant.PUBLICPARAM.contains(",qmversion=old,"))
    			{
					Stamp.addAttribute("No", "rh_qm");
					Stamp.setText(_dto.getScertid());
    			}else
    			{
    				Stamp.addAttribute("No", stampDto.getSstampposition());
    				Stamp.setText(_dto.getScertid());
    			}
			}
			stampPotisionXML = stampDoc.asXML().replaceAll("&lt;", "<")
					.replaceAll("&gt;", ">");
			logger.debug("凭证签名写入的签名位置报文：" + stampPotisionXML);
			HashMap<String, String> retmap = new HashMap<String, String>();
			retmap.put("signposxml", stampPotisionXML);
			retmap.put("signcertid", _dto.getScertid());
			return retmap;

		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装签章位置报文出现异常！", e);
		}
	}

	/**
	 * 由base64编码的字符串，生成源字符串 gbk
	 * 
	 * @param src
	 *            base64编码的字符串
	 * @return 源字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String base64Decode(String src)
			throws UnsupportedEncodingException, Exception {
		byte[] out = Base64.decode(src);

		String ret = null;
		ret = new String(out, "GBK");
		return ret;
	}

	/**
	 * 解析电子凭证库读取报文获取单个凭证总金额
	 * 
	 * @throws Exception
	 */
	public static String getTotalAmt(String svtcode, Node voucherNode)
			throws Exception {
		String Total = null;

		if (svtcode.equals(MsgConstant.VOUCHER_NO_5502)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_2502)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_3503)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_3504)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_3505)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_3506))
			Total = voucherNode.selectSingleNode("AllAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5106))
			Total = voucherNode.selectSingleNode("PlanAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_8207))
			Total = voucherNode.selectSingleNode("SumAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5951)||svtcode.equals(MsgConstant.VOUCHER_NO_5901))
			Total = "0.00";
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_2252))
			Total = voucherNode.selectSingleNode("PayAmt").getText().replace(
					"-", "");
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_2507))
			Total = voucherNode.selectSingleNode("AllAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5507))
			Total = voucherNode.selectSingleNode("AllAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5508))
			Total = voucherNode.selectSingleNode("AllAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5509))
			Total = voucherNode.selectSingleNode("AllAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5510))
			Total = voucherNode.selectSingleNode("AllAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5511))
			Total = voucherNode.selectSingleNode("AllAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5512))
			Total = voucherNode.selectSingleNode("AllAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5513))
			Total = voucherNode.selectSingleNode("AcctAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5407))
			Total = voucherNode.selectSingleNode("Amt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5408))
			Total = voucherNode.selectSingleNode("SumAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5671))
			Total = voucherNode.selectSingleNode("TraAmt").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_3507)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_3508)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_3509)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_5551)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_5552)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_5553))
			Total = voucherNode.selectSingleNode("AllAmt").getText();
		else
			Total = voucherNode.selectSingleNode("PayAmt")==null?"0.0":voucherNode.selectSingleNode("PayAmt").getText();
		return Total;
	}

	/**
	 * 解析电子凭证库读取报文获取出票单位
	 * 
	 * @throws Exception
	 */
	public static String getSpaybankcode(String svtcode, Node voucherNode)
			throws Exception {
		String Spaybankcode = "";
		if (((svtcode.equals(MsgConstant.VOUCHER_NO_5207)||svtcode.equals("5287") || svtcode
				.equals(MsgConstant.VOUCHER_NO_5267)) && ITFECommonConstant.PUBLICPARAM
				.indexOf(",sh,") < 0)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_5209)) {
			Spaybankcode = voucherNode.selectSingleNode("FinOrgCode").getText();
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5106)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_5108)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_2302)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_2301)) {
			Spaybankcode = voucherNode.selectSingleNode("PayBankNo").getText();
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5671)){
			Spaybankcode = voucherNode.selectSingleNode("XPayBankNo").getText();
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5408)){
			Spaybankcode = voucherNode.selectSingleNode("PayBankNo")==null?"":voucherNode.selectSingleNode("PayBankNo").getText();
		}else if (svtcode.equals(MsgConstant.VOUCHER_NO_2502)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_8207)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_5351)) {
			Spaybankcode = voucherNode.selectSingleNode("PayBankCode")
					.getText();
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5502)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_5257)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_5551)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_5552)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_5553)) {
			Spaybankcode = voucherNode.selectSingleNode("FinOrgCode").getText();
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5201)) {
			String BusinessTypeCode = voucherNode.selectSingleNode(
					"BusinessTypeCode").getText();
			if (StringUtils.isBlank(BusinessTypeCode))
				throw new ITFEBizException("业务类型编码不能为空。");
			Spaybankcode = voucherNode.selectSingleNode("PayeeAcctBankNo")
					.getText();
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5207)
				&& ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
			String BusinessTypeCode = voucherNode.selectSingleNode(
					"BusinessTypeCode").getText();
			if (StringUtils.isBlank(BusinessTypeCode))
				throw new ITFEBizException("业务类型编码不能为空。");
			Spaybankcode = voucherNode.selectSingleNode("PayeeAcctBankNo")
					.getText();
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5253)) {
			Spaybankcode = voucherNode.selectSingleNode("PayeeAcctBankNo")
					.getText();
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_2252)) {
			Spaybankcode = voucherNode.selectSingleNode("PaySndBnkNo")
					.getText();
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5507))
			Spaybankcode = voucherNode.selectSingleNode("ClearBankCode")
					.getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_2507))
			Spaybankcode = voucherNode.selectSingleNode("ClearAccNo").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5508))
			Spaybankcode = voucherNode.selectSingleNode("ClearAccNo").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5509))
			Spaybankcode = voucherNode.selectSingleNode("ClearAccNo").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5510))
			Spaybankcode = voucherNode.selectSingleNode("ClearAccNo").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5511))
			Spaybankcode = voucherNode.selectSingleNode("FinOrgCode").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5512))
			Spaybankcode = voucherNode.selectSingleNode("FinOrgCode").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5513))
			Spaybankcode = voucherNode.selectSingleNode("AcctNo").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_5407))
			Spaybankcode = voucherNode.selectSingleNode("FinOrgCode").getText();
		else if (svtcode.equals(MsgConstant.VOUCHER_NO_3507)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_3508)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_3509)) {
			List<Node> nodes = voucherNode.selectSingleNode("DetailList")
					.selectNodes("Detail");
			if (null == nodes || nodes.size() == 0) {
				throw new ITFEBizException("获取代理银行代码失败！");
			}
			return nodes.get(0).selectSingleNode("PayBankNo").getText();
		}else
		{
			if(voucherNode.selectSingleNode("FinOrgCode")!=null)
				Spaybankcode = voucherNode.selectSingleNode("FinOrgCode").getText();
			else if(voucherNode.selectSingleNode("PayBankNo")!=null)
				Spaybankcode = voucherNode.selectSingleNode("PayBankNo").getText();
			else if(voucherNode.selectSingleNode("PayBankNo")!=null)
				Spaybankcode = voucherNode.selectSingleNode("PayBankNo").getText();
			else if(voucherNode.selectSingleNode("treCode")!=null)
				Spaybankcode = voucherNode.selectSingleNode("treCode").getText();
		}
		return Spaybankcode;
	}

	/**
	 * 根据银行代码取得行别代码
	 * 
	 * @throws Exception
	 */
	public static String getSpaybankType(String trecode, String billorgcode)
			throws ITFEBizException {
		String bankType = "";
		HashMap<String, TsConvertbanktypeDto> mapBankType;
		try {
			mapBankType = SrvCacheFacade.cacheTsconvertBankType(null);
			if (mapBankType != null && mapBankType.size() != 0) {
				TsConvertbanktypeDto tsConvertbanktypeDto = mapBankType
						.get(trecode + billorgcode);
				if (tsConvertbanktypeDto == null) {
					throw new ITFEBizException("国库：" + trecode + " 银行行号："
							+ billorgcode + " 在 [ 代理银行信息维护参数 ] 中未维护！");
				}
				bankType = tsConvertbanktypeDto.getSbanktype();
				if (bankType == null || "".equals(bankType)) {
					throw new ITFEBizException("[代理银行信息维护参数]中没有维护银行行号["
							+ billorgcode + "]的行别代码！");
				}
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("根据代理银行取得行别代码出现异常！");
		}
		return bankType;
	}

	/**
	 * 更新凭证勾兑信息(上海特色) 添加勾兑凭证类型、勾兑凭证编号、业务类型编号
	 * 
	 * @param dto
	 * @param voucherNode
	 */
	public static void updateVoucherCompareInfo(TvVoucherinfoDto dto,
			Node voucherNode) {
		if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0)
			return;
		String checkvouchertype = "";
		String checkVoucherno = "";
		String BusinessTypeCode = "";
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {
			checkVoucherno = voucherNode.selectSingleNode("ClearVoucherNo")
					.getText();
			if (dto.getNmoney().compareTo(BigDecimal.ZERO) == 0)
				checkvouchertype = MsgConstant.VOUCHER_NO_5351;
			else
				checkvouchertype = MsgConstant.VOUCHER_NO_2301;
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {
			checkVoucherno = voucherNode.selectSingleNode("PayVoucherNo")
					.getText();
			if (dto.getNmoney().compareTo(BigDecimal.ZERO) == 0)
				checkvouchertype = MsgConstant.VOUCHER_NO_5253;
			else
				checkvouchertype = MsgConstant.VOUCHER_NO_5201;
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)) {
			checkvouchertype = MsgConstant.VOUCHER_NO_5106;
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253)) {
			checkvouchertype = MsgConstant.VOUCHER_NO_5108;
		} else if ((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207) || dto
				.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267))) {
			BusinessTypeCode = voucherNode.selectSingleNode("BusinessTypeCode")
					.getText();
			if (StringUtils.isNotBlank(BusinessTypeCode)
					&& BusinessTypeCode
							.equals(StateConstant.BIZTYPE_CODE_BATCH))
				checkvouchertype = MsgConstant.VOUCHER_NO_8207;
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)) {
			BusinessTypeCode = voucherNode.selectSingleNode("BusinessTypeCode")
					.getText();
			if (StringUtils.isNotBlank(BusinessTypeCode)
					&& BusinessTypeCode
							.equals(StateConstant.BIZTYPE_CODE_BATCH))
				checkvouchertype = MsgConstant.VOUCHER_NO_5108;
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)) {
			checkVoucherno = voucherNode.selectSingleNode("OriginalVoucherNo")
					.getText();
			BusinessTypeCode = voucherNode.selectSingleNode("BusinessTypeCode")
					.getText();
			checkvouchertype = voucherNode.selectSingleNode("OriginalVtCode")
					.getText();
		}
		dto.setScheckvouchertype(checkvouchertype);
		dto.setShold3(checkVoucherno);
		dto.setShold4(BusinessTypeCode);
	}

	/**
	 * 签收失败，不更新凭证状态
	 * 
	 * @param dto
	 * @param errMsg
	 */
	public static void confirmVoucherfail(TvVoucherinfoDto dto, String errMsg) {
		try {
			errMsg = errMsg == null ? "报文不规范" : errMsg;
			errMsg = errMsg.length() > 100 ? errMsg.substring(0, 100) : errMsg;
			VoucherService voucherService = new VoucherService();
			voucherService.confirmVoucherfail(null, dto.getSadmdivcode(),
					Integer.parseInt(dto.getSstyear()), dto.getSvtcode(),
					new String[] { dto.getSvoucherno() },
					new String[] { errMsg });
			if (VoucherFactory.getVouMap() != null
					&& VoucherFactory.getVouMap().get(dto.getSvoucherno()+dto.getStrecode()) != null)
				VoucherFactory.getVouMap().remove(dto.getSvoucherno()+dto.getStrecode());
		} catch (ITFEBizException e) {
			logger.error(e);
		}
	}

	/**
	 * 签收成功，不更新凭证状态
	 * 
	 * @param vDto
	 * @throws ITFEBizException
	 */
	public static void voucherConfirmSuccess(TvVoucherinfoDto vDto)
			throws ITFEBizException {
		String errMsg = "";
		try {
			VoucherService voucherService = new VoucherService();
			errMsg = voucherService.confirmVoucher(null, vDto.getSadmdivcode(),
					Integer.parseInt(vDto.getSstyear()), vDto.getSvtcode(),
					new String[] { vDto.getSvoucherno() });
			if (VoucherFactory.getVouMap() != null
					&& VoucherFactory.getVouMap().get(vDto.getSvoucherno()+vDto.getStrecode()) != null)
				VoucherFactory.getVouMap().remove(vDto.getSvoucherno()+vDto.getStrecode());
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("凭证：" + vDto.getSvoucherno()
					+ " 签收成功异常！");
		}
		if (!StringUtils.isBlank(errMsg))
			throw new ITFEBizException("凭证：" + vDto.getSvoucherno()
					+ " 签收成功异常！");
	}

	/**
	 * 解析业务对账3503 3504 3505 3506回单报文
	 * 
	 * @param voucherNode
	 * @param dto
	 * @return
	 */
	public static boolean voucherDealMsgXML(Node voucherNode,
			TvVoucherinfoDto dto) {
		if (!dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3503)
				&& !dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3504)
				&& !dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3505)
				&& !dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3506))
			return false;
		boolean flag = true;
		TvVoucherinfoDto voucherDto = new TvVoucherinfoDto();
		voucherDto.setSvtcode(dto.getSvtcode());
		voucherDto.setSadmdivcode(dto.getSadmdivcode());
		voucherDto.setSvoucherno(dto.getSvoucherno());
		List list;
		// 校验人行凭证是否存在
		try {
			list = CommonFacade.getODB().findRsByDto(voucherDto);
			if (list == null || list.size() == 0) {
				confirmVoucherfail(dto, "人行凭证" + dto.getSvoucherno() + "不存在");
				return flag;
			} else if (list.size() > 1) {
				confirmVoucherfail(dto, "人行凭证" + dto.getSvoucherno() + "存在多条");
				return flag;
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			confirmVoucherfail(dto, e.getMessage());
			return flag;
		} catch (ValidateException e) {
			logger.error(e);
			confirmVoucherfail(dto, e.getMessage());
			return flag;
		}
		// 校验凭证是否已发送对账凭证库
		voucherDto = (TvVoucherinfoDto) list.get(0);
		if (!voucherDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS)) {
			confirmVoucherfail(dto, "人行凭证" + dto.getSvoucherno()
					+ "未发送电子凭证库，不能接收回单报文");
			return flag;
		}
		try {
			// 校验凭证报文是否规范
			String XCheckResult = voucherNode.selectSingleNode("XCheckResult")
					.getText();
			String XCheckReason = voucherNode.selectSingleNode("XCheckReason")
					.getText();
			if (StringUtils.isBlank(XCheckResult)
					|| (!XCheckResult.equals("0") && !XCheckResult.equals("1"))) {
				confirmVoucherfail(dto, "对账结果节点XCheckResult的值必须是0或1");
				return flag;
			}
			// 签收成功
			voucherConfirmSuccess(dto);
			// 更新凭证回单对账结果
			voucherDto
					.setSstatus(DealCodeConstants.VOUCHER_RETURN_SUCCESS_ALREADY);
			if (XCheckResult.equals("0"))
				voucherDto.setSdemo("对账成功");
			else
				voucherDto.setSdemo(StringUtils.isBlank(XCheckReason) ? "对账失败"
						: ("对账失败：" + XCheckReason));
			updateVoucherState(voucherDto);
		} catch (Exception e) {
			logger.error(e);
			confirmVoucherfail(dto, e.getMessage());
		}
		return flag;
	}

	/**
	 * 解析凭证对账5502 2502报文
	 * 
	 * @param svtcode
	 * @param voucherNode
	 * @param dto
	 */
	public static void voucherDealMsgXML(String svtcode, Node voucherNode,
			TvVoucherinfoDto dto) {
		if (!svtcode.equals(MsgConstant.VOUCHER_NO_2502)
				&& !svtcode.equals(MsgConstant.VOUCHER_NO_5502))
			return;
		try {
			// String
			// AdmDivCode=voucherNode.selectSingleNode("AdmDivCode").getText();
			String StYear = voucherNode.selectSingleNode("StYear").getText();
			// String VtCode=voucherNode.selectSingleNode("VtCode").getText();
			String VouDate = voucherNode.selectSingleNode("VouDate").getText();
			String VouNo = voucherNode.selectSingleNode("VoucherNo").getText();
			String TreCode = voucherNode.selectSingleNode("TreCode").getText();
			String CheckDate = voucherNode.selectSingleNode("CheckDate")
					.getText();
			String EVoucherType = voucherNode.selectSingleNode("EVoucherType")
					.getText();
			String AllAmt = voucherNode.selectSingleNode("AllAmt").getText();
			String AllNum = voucherNode.selectSingleNode("AllNum").getText();
			String Hold1 = voucherNode.selectSingleNode("Hold1").getText();
			String Hold2 = voucherNode.selectSingleNode("Hold2").getText();
			String Hold3 = voucherNode.selectSingleNode("Hold3").getText();
			String Hold4 = voucherNode.selectSingleNode("Hold4").getText();
			if (svtcode.equals(MsgConstant.VOUCHER_NO_5502)) {
				String FinOrgCode = voucherNode.selectSingleNode("FinOrgCode")
						.getText();
				String ClearBankName = voucherNode.selectSingleNode(
						"ClearBankName").getText();
				dto.setSverifyusercode(FinOrgCode);
				dto.setSstampid(ClearBankName);
			} else {
				String PayBankCode = voucherNode
						.selectSingleNode("PayBankCode").getText();
				dto.setSpaybankcode(PayBankCode);
			}
			List listDetail = voucherNode.selectSingleNode("DetailList")
					.selectNodes("Detail");
			dto.setSvoucherno(VouNo);
			dto.setSstampuser(VouDate);
			dto.setScheckdate(CheckDate);
			dto.setScheckvouchertype(EVoucherType);
			dto.setIcount(Integer.parseInt(AllNum));
			dto.setShold1(Hold1);
			dto.setShold2(Hold2);
			dto.setShold3(Hold3);
			dto.setShold4(Hold4);
			// 对账单主单信息写入凭证
			updateVoucherState(dto);
			// 校验报文总笔数与明细总笔数是否一致
			if (voucherVerify(AllNum, listDetail.size() + "", dto))
				return;
			// 校验凭证报文对账笔数与人行笔数是否一致
			if (voucherVerify(dto, AllNum))
				return;
			BigDecimal Total = new BigDecimal("0.00");// 明细累计总金额
			List list = new ArrayList();
			VoucherVerify voucherVerify = new VoucherVerify();
			for (Element elementDetail : (List<Element>) listDetail) {
				// String Id=elementDetail.elementText("Id");
				String VoucherNo = elementDetail.elementText("VoucherNo");// 凭证号
				String Amt = elementDetail.elementText("Amt");// 金额
				String ProState = elementDetail.elementText("ProState");// 凭证状态
				// 0-正常
				// 1-退回
				Total = Total.add(MtoCodeTrans.transformBigDecimal(Amt));
				VoucherVerifyDto verifydto = new VoucherVerifyDto();
				verifydto.setTrecode(TreCode);
				verifydto.setVoudate(VouDate);
				verifydto.setOfyear(StYear);
				verifydto.setVoucherno(VoucherNo);
				verifydto.setFamt(Amt);
				if (svtcode.equals(MsgConstant.VOUCHER_NO_5502))
					verifydto.setFinorgcode(dto.getSverifyusercode());
				else
					verifydto.setFinorgcode("0000000000");// 默认补填校验dto财政代码
				if (svtcode.equals(MsgConstant.VOUCHER_NO_2502))
					verifydto.setPaybankno(dto.getSpaybankcode());
				// 校验报文关键字段格式
				String returnmsg = voucherVerify.checkValid(verifydto, svtcode);
				String ProStateMsg = (StringUtils.isBlank(ProState) ? "凭证状态字段ProState值不能为空。"
						: !ProState.equals("0") && !ProState.equals("1") ? "凭证状态字段ProState值必须是0或1。"
								: "");
				returnmsg = returnmsg == null ? ProStateMsg : returnmsg
						+ ProStateMsg;
				if (StringUtils.isNotBlank(returnmsg)) {
					VoucherFactory.voucherComfail(dto.getSdealno(), returnmsg);
					return;
				}
				verifydto.setOfyear(ProState);// 凭证状态 0-正常 1-退回
				list.add(verifydto);
			}
			// 校验报文总金额与明细累计总金额是否一致
			if (voucherVerify(MtoCodeTrans.transformBigDecimal(AllAmt), Total,
					dto))
				return;
			// 校验凭证报文格式： 校验凭证日期和对账日期是否大于当前接收日期时 校验国库代码，财政机构
			if (voucherVerify(dto))
				return;
			// 签收成功
			VoucherFactory.voucherConfirmSuccess(dto.getSdealno());
			// 解析凭证对账5502 2502报文明细信息
			voucherDealMsgXML(list, dto);
		} catch (Exception e) {
			logger.error(e);
			VoucherFactory.voucherComfail(dto.getSdealno(), e.getMessage());
		}
	}

	/**
	 * 解析凭证对账5502 2502报文明细信息
	 * 
	 * @param lists
	 * @param dto
	 * @throws ITFEBizException
	 */
	public static void voucherDealMsgXML(List lists, TvVoucherinfoDto dto)
			throws ITFEBizException {
		dto.setSdemo(null);
		for (VoucherVerifyDto verifydto : (List<VoucherVerifyDto>) lists) {
			String VoucherNo = verifydto.getVoucherno();// 凭证号
			String Amt = verifydto.getFamt();// 金额
			String ProState = verifydto.getOfyear();// 凭证状态 0-正常 1-退回
			List<TvVoucherinfoDto> list = findVoucherDto(VoucherNo, dto);
			dto.setSstatus(DealCodeConstants.VOUCHER_CHECKACCOUNT_FAIL);
			if (list == null || list.size() == 0)
				dto.setSdemo(dto.getSdemo() == null ? "凭证" + VoucherNo + "不存在"
						: dto.getSdemo() + "、" + "凭证" + VoucherNo + "不存在");
			else if (list.size() > 1)
				dto.setSdemo(dto.getSdemo() == null ? "凭证" + VoucherNo
						+ "数据库存在" + list.size() + "个" : dto.getSdemo() + "、"
						+ "凭证" + VoucherNo + "数据库存在" + list.size() + "个");
			else {
				TvVoucherinfoDto vDto = list.get(0);
				if (!((vDto.getSstatus().equals(
						DealCodeConstants.VOUCHER_SUCCESS) && ProState
						.equals(StateConstant.VOUCHE_PROSTATE_NORMAL)) || (vDto
						.getSstatus().equals(DealCodeConstants.VOUCHER_FAIL) && ProState
						.equals(StateConstant.VOUCHE_PROSTATE_RETURN)))
						&& vDto.getNmoney().compareTo(
								MtoCodeTrans.transformBigDecimal(Amt)) == 0) {
					dto.setSdemo(dto.getSdemo() == null ? "凭证" + VoucherNo
							+ "状态不符" : dto.getSdemo() + "、" + "凭证" + VoucherNo
							+ "状态不符");
					vDto.setSreturnerrmsg("对账失败(状态不符)");
				} else if (!((vDto.getSstatus().equals(
						DealCodeConstants.VOUCHER_SUCCESS) && ProState
						.equals(StateConstant.VOUCHE_PROSTATE_NORMAL)) || (vDto
						.getSstatus().equals(DealCodeConstants.VOUCHER_FAIL) && ProState
						.equals(StateConstant.VOUCHE_PROSTATE_RETURN)))
						&& vDto.getNmoney().compareTo(
								MtoCodeTrans.transformBigDecimal(Amt)) != 0) {
					dto.setSdemo(dto.getSdemo() == null ? "凭证" + VoucherNo
							+ "状态金额不符" : dto.getSdemo() + "、" + "凭证"
							+ VoucherNo + "状态金额不符");
					vDto.setSreturnerrmsg("对账失败(状态金额不符)");
				} else if (((vDto.getSstatus().equals(
						DealCodeConstants.VOUCHER_SUCCESS) && ProState
						.equals(StateConstant.VOUCHE_PROSTATE_NORMAL)) || (vDto
						.getSstatus().equals(DealCodeConstants.VOUCHER_FAIL) && ProState
						.equals(StateConstant.VOUCHE_PROSTATE_RETURN)))
						&& vDto.getNmoney().compareTo(
								MtoCodeTrans.transformBigDecimal(Amt)) != 0) {
					dto.setSdemo(dto.getSdemo() == null ? "凭证" + VoucherNo
							+ "金额不符" : dto.getSdemo() + "、" + "凭证" + VoucherNo
							+ "金额不符");
					vDto.setSreturnerrmsg("对账失败(金额不符)");
				} else
					vDto.setSreturnerrmsg("对账成功");
				updateVoucherState(vDto);
			}
		}
		if (dto.getSdemo() == null) {
			dto.setSstatus(DealCodeConstants.VOUCHER_CHECKACCOUNT_SUCCESS);
			dto.setSdemo("对账成功");
		}
		dto.setSdemo(dto.getSdemo().length() > 1024 ? dto.getSdemo().substring(
				0, 1024) : dto.getSdemo());
		updateVoucherState(dto);
	}

	/**
	 * 根据凭证号码、对账日期查找凭证
	 * 
	 * @param VoucherNo
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public static List findVoucherDto(String VoucherNo, TvVoucherinfoDto dto)
			throws ITFEBizException {
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		vDto.setSvoucherno(VoucherNo);
		vDto.setScreatdate(dto.getScheckdate());
		try {
			return CommonFacade.getODB().findRsByDto(vDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		}
	}

	/**
	 * 根据国库代码、凭证类型、凭证编号、凭证状态查找比对凭证
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public static TvVoucherinfoDto findVoucherDto(TvVoucherinfoDto dto,
			String status) throws ITFEBizException {
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		vDto.setStrecode(dto.getStrecode());
		vDto.setSvtcode(dto.getScheckvouchertype());
		vDto.setScheckvouchertype(dto.getSvtcode());// 比对凭证类型
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {
			vDto.setSvoucherno(dto.getShold3());
		} else {
			vDto.setShold3(dto.getSvoucherno());
		}
		vDto.setSstatus(status);
		try {
			List<TvVoucherinfoDto> list = CommonFacade.getODB().findRsByDto(
					vDto);
			if (list == null || list.size() == 0)
				return null;
			else if (list.size() > 1)
				throw new ITFEBizException("国库代码[" + dto.getStrecode()
						+ "]凭证类型[" + vDto.getSvtcode() + "]凭证编号["
						+ vDto.getSvoucherno() + "]状态为 [校验中]的凭证数据库存在"
						+ list.size() + "个");
			return (TvVoucherinfoDto) list.get(0);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查找凭证信息异常！", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查找凭证信息异常！", e);
		}
	}

	/**
	 * 根据国库代码、比对凭证类型、凭证编号、凭证状态查找原凭证
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public static List findVoucherDto(TvVoucherinfoDto dto)
			throws ITFEBizException {
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		vDto.setStrecode(dto.getStrecode());
		vDto.setSvtcode(dto.getSvtcode());
		vDto.setScheckvouchertype(dto.getScheckvouchertype());// 比对凭证类型
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106))
			vDto.setShold3(dto.getShold3());
		else
			vDto.setSvoucherno(dto.getSvoucherno());
		vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT);
		try {
			return CommonFacade.getODB().findRsByDto(vDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查找凭证信息异常！", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查找凭证信息异常！", e);
		}
	}

	/**
	 * 根据国库代码、凭证类型、凭证编号、凭证状态查找凭证
	 * 
	 * @param strecode
	 * @param svtcode
	 * @param svoucherno
	 * @param status
	 * @return
	 * @throws ITFEBizException
	 */
	public static List findVoucherDto(String strecode, String svtcode,
			String svoucherno, String status) throws ITFEBizException {
		TvVoucherinfoDto dto = new TvVoucherinfoDto();
		dto.setStrecode(strecode);
		dto.setSvtcode(svtcode);
		dto.setSvoucherno(svoucherno);
		dto.setSstatus(status);
		try {
			return CommonFacade.getODB().findRsByDto(dto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查找凭证信息异常！", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查找凭证信息异常！", e);
		}
	}

	/**
	 * 根据拆分流水号、委托日期查找拆分凭证
	 * 
	 * @param ssplitvousrlno
	 * @param scommitdate
	 * @return
	 * @throws ITFEBizException
	 */
	public static TfVoucherSplitDto findVoucherSplitDto(String ssplitvousrlno,
			String scommitdate) {
		TfVoucherSplitDto dto = new TfVoucherSplitDto();
		dto.setSsplitvousrlno(ssplitvousrlno);
		dto.setScommitdate(scommitdate);
		try {
			List<TfVoucherSplitDto> list = CommonFacade.getODB().findRsByDto(
					dto);
			if (list != null && list.size() > 0)
				dto = list.get(0);
		} catch (ValidateException e) {
			logger.error("查找拆分凭证信息异常！", e);
			VoucherException.saveErrInfo(null, "查找拆分凭证信息异常！");
		} catch (JAFDatabaseException e) {
			logger.error("查找拆分凭证信息异常！", e);
			VoucherException.saveErrInfo(null, "查找拆分凭证信息异常！");
		}
		return dto;
	}

	/**
	 * 查找相同凭证类型和凭证编号下的所有拆分凭证
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public static List findVoucherSplitDto(TfVoucherSplitDto dto) {
		TfVoucherSplitDto tempdto = new TfVoucherSplitDto();
		tempdto.setIvousrlno(dto.getIvousrlno());
		tempdto.setSmainno(dto.getSmainno());
		tempdto.setSvtcode(dto.getSvtcode());
		tempdto.setScommitdate(dto.getScommitdate());
		try {
			return CommonFacade.getODB().findRsByDto(tempdto);
		} catch (JAFDatabaseException e) {
			logger.error("查找拆分凭证信息异常！", e);
			VoucherException.saveErrInfo(dto.getSvtcode(), e);
		} catch (ValidateException e) {
			logger.error("查找拆分凭证信息异常！", e);
			VoucherException.saveErrInfo(dto.getSvtcode(), e);
		}
		return null;
	}

	/**
	 * 根据凭证索引表查找业务主表信息
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public static List findMainDtoByVoucher(TvVoucherinfoDto dto)
			throws ITFEBizException {
		IDto maindto = null;
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {// 直接支付额度5108
			maindto = new TvDirectpaymsgmainDto();
			((TvDirectpaymsgmainDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)) {// 直接支付5201
			maindto = new TfDirectpaymsgmainDto();
			((TfDirectpaymsgmainDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {// 授权支付额度5106
			maindto = new TvGrantpaymsgmainDto();
			((TvGrantpaymsgmainDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)) {// 实拨拨款凭证5207
			maindto = new TvPayoutmsgmainDto();
			((TvPayoutmsgmainDto) maindto).setSbizno(dto.getSdealno());
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)) {// 财政批量业务支付明细8207
			maindto = new TfPaymentDetailsmainDto();
			((TfPaymentDetailsmainDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)) {// 申请划款凭证回单2301
			maindto = new TvPayreckBankDto();
			((TvPayreckBankDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)) {// 申请退款凭证回单2302
			maindto = new TvPayreckBankBackDto();
			((TvPayreckBankBackDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253)) {// 财政直接支付调整凭证5253
			maindto = new TfDirectpayAdjustmainDto();
			((TfDirectpayAdjustmainDto) maindto).setIvousrlno(Long
					.parseLong(dto.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)) {// 财政授权支付调整凭证5351主表
			maindto = new TfGrantpayAdjustmainDto();
			((TfGrantpayAdjustmainDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3208)) { // 实拨资金退款
			maindto = new TvPayoutbackmsgMainDto();
			((TvPayoutbackmsgMainDto) maindto).setSbizno(dto.getSdealno());
		} else
			throw new ITFEBizException("没有定义此业务类型!");
		try {
			return CommonFacade.getODB().findRsByDto(maindto);
		} catch (JAFDatabaseException e) {
			logger.error("根据凭证索引表查找业务主表信息异常!", e);
			throw new ITFEBizException("根据凭证索引表查找业务主表信息异常!", e);
		} catch (ValidateException e) {
			logger.error("根据凭证索引表查找业务主表信息异常!", e);
			throw new ITFEBizException("根据凭证索引表查找业务主表信息异常!", e);
		}
	}

	/**
	 * 校验凭证报文对账笔数与人行笔数是否一致
	 * 
	 * @param dto
	 * @param AllNum
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public static boolean voucherVerify(TvVoucherinfoDto dto, String AllNum)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		String str;
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		vDto.setScreatdate(dto.getScheckdate());
		vDto.setSvtcode(dto.getScheckvouchertype());
		vDto.setStrecode(dto.getStrecode());
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2502)) {
			vDto.setSpaybankcode(dto.getSpaybankcode());
			str = "代理银行";
		} else
			str = "财政";
		boolean flag = true;
		List<TvVoucherinfoDto> list = CommonFacade.getODB()
				.findRsByWhereForDto(
						vDto,
						" S_STATUS IN ('" + DealCodeConstants.VOUCHER_SUCCESS
								+ "' ,'" + DealCodeConstants.VOUCHER_FAIL
								+ "')");
		if (list == null || list.size() == 0) {
			dto.setSstatus(DealCodeConstants.VOUCHER_CHECKACCOUNT_FAIL);
			dto.setSdemo("人行笔数：0" + " " + str + "笔数：" + dto.getIcount());
		} else {
			if (Integer.parseInt(AllNum) != list.size()) {
				dto.setSstatus(DealCodeConstants.VOUCHER_CHECKACCOUNT_FAIL);
				dto.setSdemo((dto.getSdemo() == null ? "" : dto.getSdemo())
						+ "； 人行笔数：" + list.size() + " " + str + "笔数："
						+ dto.getIcount());
			} else
				return false;
		}
		if (flag)
			VoucherFactory.voucherComfail(dto.getSdealno(), dto.getSdemo());
		return flag;
	}

	/**
	 * 校验报文总笔数与明细累计总笔数是否一致
	 * 
	 * @param AllNum
	 * @param DetailNum
	 * @param dto
	 * @return
	 */
	public static boolean voucherVerify(String AllNum, String DetailNum,
			TvVoucherinfoDto dto) {
		if (!AllNum.equals(DetailNum)) {
			dto.setSstatus(DealCodeConstants.VOUCHER_CHECKACCOUNT_FAIL);
			dto.setSdemo("报文总笔数与明细累计总笔数不一致");
			VoucherFactory.voucherComfail(dto.getSdealno(), dto.getSdemo());
			return true;
		}
		return false;
	}

	/**
	 * 校验报文总金额与明细累计总金额是否一致
	 * 
	 * @param AllAmt
	 * @param DetailAmt
	 * @param dto
	 * @return
	 */
	public static boolean voucherVerify(BigDecimal AllAmt,
			BigDecimal DetailAmt, TvVoucherinfoDto dto) {
		if (AllAmt.compareTo(DetailAmt) != 0) {
			dto.setSstatus(DealCodeConstants.VOUCHER_CHECKACCOUNT_FAIL);
			dto.setSdemo("报文总金额与明细累计总金额不一致");
			VoucherFactory.voucherComfail(dto.getSdealno(), dto.getSdemo());
			return true;
		}
		return false;
	}

	/**
	 * 校验凭证报文格式： 校验凭证日期和对账日期是否大于当前接收日期时 校验国库代码，财政机构
	 * 
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public static boolean voucherVerify(TvVoucherinfoDto dto)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		boolean flag = true;
		if (Integer.parseInt(dto.getSstampuser()) > Integer.parseInt(TimeFacade
				.getCurrentStringTime()))
			dto.setSdemo("凭证日期:" + dto.getSstampuser() + " 大于接收日期："
					+ TimeFacade.getCurrentStringTime());
		else if (Integer.parseInt(dto.getScheckdate()) > Integer
				.parseInt(TimeFacade.getCurrentStringTime()))
			dto.setSdemo("对账日期:" + dto.getScheckdate() + " 大于接收日期："
					+ TimeFacade.getCurrentStringTime());
		else if ((dto.getIcount() + "").length() > 8)
			dto.setSdemo("凭证笔数：" + dto.getIcount() + "大于8位");
		else
			flag = false;
		if (flag) {
			VoucherFactory.voucherComfail(dto.getSdealno(), dto.getSdemo());
			return flag;
		}
		VoucherVerify voucherVerify = new VoucherVerify();
		if (!voucherVerify.verifyTreasury(dto.getStrecode(), dto.getSorgcode())) {
			dto.setSdemo(voucherVerify.getTmpFailReason());
			VoucherFactory.voucherComfail(dto.getSdealno(), dto.getSdemo());
			return true;
		}
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5502)) {
			if (!voucherVerify.verifySfinOrgCode(dto.getSverifyusercode(), dto
					.getSorgcode())) {
				dto.setSdemo(voucherVerify.getTmpFailReason());
				VoucherFactory.voucherComfail(dto.getSdealno(), dto.getSdemo());
				return true;
			}
		}
		return flag;
	}

	/**
	 * 对字符串进行base64编码
	 * 
	 * @param src字符串
	 * @return base64字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String base64Encode(String src)
			throws UnsupportedEncodingException, Exception {
		byte[] outByte = Base64.encode(src.getBytes());
		String ret = null;
		ret = new String(outByte, "GBK");
		return ret;
	}

	/**
	 * 根据凭证类型获取业务类型
	 */
	public static String getOperationType(String ls_VtCode) {
		String ls_OperationType = "";
		if (ls_VtCode.equals(MsgConstant.VOUCHER_NO_5106))
			ls_OperationType = BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN;
		else if (ls_VtCode.equals(MsgConstant.VOUCHER_NO_5108))
			ls_OperationType = BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN;
		else if (ls_VtCode.equals(MsgConstant.VOUCHER_NO_5207)
				|| ls_VtCode.equals(MsgConstant.VOUCHER_NO_5267))
			ls_OperationType = BizTypeConstant.BIZ_TYPE_PAY_OUT;
		else if (ls_VtCode.equals(MsgConstant.VOUCHER_NO_2301))
			ls_OperationType = BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY;
		else if (ls_VtCode.equals(MsgConstant.VOUCHER_NO_2302))
			ls_OperationType = BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK;
		else if (ls_VtCode.equals(MsgConstant.VOUCHER_NO_5209))
			ls_OperationType = BizTypeConstant.BIZ_TYPE_RET_TREASURY;
		return ls_OperationType;
	}

	/**
	 * 同城清算接口，根据业务类型获取清算凭证类型
	 */
	public static String getPayOutVouType(String ls_OprationTypeCode) {
		String ls_PayOutVouType = "";
		if (ls_OprationTypeCode.equals(BizTypeConstant.BIZ_TYPE_PAY_OUT))
			ls_PayOutVouType = "1";
		else if (ls_OprationTypeCode
				.equals(BizTypeConstant.BIZ_TYPE_RET_TREASURY))
			ls_PayOutVouType = "2";
		else if (ls_OprationTypeCode
				.equals(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY))
			ls_PayOutVouType = "3";
		return ls_PayOutVouType;
	}

	public static void getElementList(Element element, Element voucherbody) {
		List elements = element.elements();
		if (elements.size() == 0) {

			String xpath = element.getName();

			String value = element.getText();
			Element newElement = voucherbody.addElement(xpath);
			newElement.setText(value);

		} else {

			for (Iterator it = elements.iterator(); it.hasNext();) {
				Element elem = (Element) it.next();

				if (elem.getName().equals("Detail")) {
					if (voucherbody.getName().equals("Detail")) {
						voucherbody = voucherbody.getParent();
					}
					voucherbody = voucherbody.addElement(elem.getName());

				}
				if (elem.getName().equals("DetailList")) {
					voucherbody = voucherbody.addElement(elem.getName());

				}

				getElementList(elem, voucherbody);
			}
		}
	}

	/**
	 * 凭证列表根据区划代码分包
	 * 
	 * @param list
	 * @return
	 * @throws ITFEBizException
	 */
	public static List getListByAdmdivcode(List<TvVoucherinfoDto> list)
			throws ITFEBizException {
		Map map = new HashMap();
		for (TvVoucherinfoDto dto : list) {
			map.put(dto.getSadmdivcode(), "");
		}
		if (map.size() == 0) {
			return null;
		}
		List<List> lists = new ArrayList<List>();
		try {

			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				List<TvVoucherinfoDto> newList = new ArrayList<TvVoucherinfoDto>();
				for (TvVoucherinfoDto dto : list) {
					String admdivcode = dto.getSadmdivcode();

					if (admdivcode.equals(key)) {

						newList.add(dto);
					}
				}
				lists.add(newList);
			}

		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("凭证列表根据区划代码分包出现异常！", e);
		}

		return lists;
	}

	/**
	 * 凭证列表根据开户行行号分包
	 * 
	 * @param list
	 * @return
	 * @throws ITFEBizException
	 */
	public static List getListBySpayeebankno(List<TvPayoutmsgmainDto> list)
			throws ITFEBizException {
		Map map = new HashMap();
		for (TvPayoutmsgmainDto dto : list) {
			map.put(dto.getSpayeebankno(), "");
		}
		if (map.size() == 0) {
			return null;
		}
		List<List> lists = new ArrayList<List>();
		try {

			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				List<TvPayoutmsgmainDto> newList = new ArrayList<TvPayoutmsgmainDto>();
				for (TvPayoutmsgmainDto dto : list) {
					if (dto.getSpayeebankno().equals(key)) {

						newList.add(dto);
					}
				}
				lists.add(newList);
			}

		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("实拨资金根据开户行行号分包出现异常！", e);
		}

		return lists;
	}

	public static List getListBySagentbnkcode(List<TvPayreckBankDto> list)
			throws ITFEBizException {
		Map map = new HashMap();
		List<List> lists = new ArrayList<List>();
		try {
			// 按照代理银行号+支付方式进行分包
			for (TvPayreckBankDto dto : list) {
				map.put(dto.getSagentbnkcode() + "_" + dto.getSpaymode(), "");
			}
			if (map.size() == 0) {
				return null;
			}
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				List<TvPayreckBankDto> newList = new ArrayList<TvPayreckBankDto>();
				for (TvPayreckBankDto dto : list) {
					if ((dto.getSagentbnkcode() + "_" + dto.getSpaymode())
							.equals(key)) {
						newList.add(dto);
					}
				}
				lists.add(newList);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("商业银行集中支付划款申请主表根据代理银行行号分包出现异常！", e);
		}
		return lists;
	}

	public static List getListBySagentbnkcodes(List<TvPayreckBankBackDto> list)
			throws ITFEBizException {
		Map map = new HashMap();
		List<List> lists = new ArrayList<List>();
		try {
			// 按照代理银行号+支付方式进行分包
			for (TvPayreckBankBackDto dto : list) {
				map.put(dto.getSagentbnkcode() + "_" + dto.getSpaymode(), "");
			}
			if (map.size() == 0) {
				return null;
			}
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				List<TvPayreckBankBackDto> newList = new ArrayList<TvPayreckBankBackDto>();
				for (TvPayreckBankBackDto dto : list) {
					if ((dto.getSagentbnkcode() + "_" + dto.getSpaymode())
							.equals(key)) {
						newList.add(dto);
					}
				}
				lists.add(newList);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("商业银行办理支付退款汇总信息表根据代理银行行号分包出现异常！", e);
		}
		return lists;
	}

	/**
	 * 凭证列表根据征收机构代码分包(收入退付凭证)
	 * 
	 * @param list
	 * @return
	 * @throws ITFEBizException
	 */
	public static List getListByStaxorgcode(List<TvDwbkDto> list)
			throws ITFEBizException {
		Map map = new HashMap();
		for (TvDwbkDto dto : list) {
			map.put(dto.getStaxorgcode(), "");
		}
		if (map.size() == 0) {
			return null;
		}
		List<List> lists = new ArrayList<List>();
		try {

			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				List<TvDwbkDto> newList = new ArrayList<TvDwbkDto>();
				for (TvDwbkDto dto : list) {
					if (dto.getStaxorgcode().equals(key)) {

						newList.add(dto);
					}
				}
				lists.add(newList);
			}

		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("收入退付根据征收机构代码分包出现异常！", e);
		}

		return lists;
	}

	/**
	 * 直接支付5201根据代理银行号分包
	 * 
	 * @param list
	 * @return
	 * @throws ITFEBizException
	 */
	public static List getListBySpaybankcode(List<TfDirectpaymsgmainDto> list)
			throws ITFEBizException {
		Map map = new HashMap();
		for (TfDirectpaymsgmainDto dto : list)
			map.put(dto.getSpayeeacctbankno(), "");
		if (map.size() == 0)
			new ITFEBizException("查询的主表信息为空！");
		List<List> lists = new ArrayList<List>();
		try {
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				List<TfDirectpaymsgmainDto> newList = new ArrayList<TfDirectpaymsgmainDto>();
				for (TfDirectpaymsgmainDto dto : list) {
					if (dto.getSpayeeacctbankno().equals(key))
						newList.add(dto);
				}
				lists.add(newList);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("直接支付根据开户行行号分包出现异常！", e);
		}
		return lists;
	}

	/**
	 * 收款银行退款通知2252根据支付发起行行号分包
	 * 
	 * @param list
	 * @return
	 * @throws ITFEBizException
	 */
	public static List getListBySpaysndbnkno(List<TfPaybankRefundmainDto> list)
			throws ITFEBizException {
		Map map = new HashMap();
		for (TfPaybankRefundmainDto dto : list)
			map.put(dto.getSpaysndbnkno(), "");
		if (map.size() == 0)
			new ITFEBizException("查询的主表信息为空！");
		List<List> lists = new ArrayList<List>();
		try {
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				List<TfPaybankRefundmainDto> newList = new ArrayList<TfPaybankRefundmainDto>();
				for (TfPaybankRefundmainDto dto : list) {
					if (dto.getSpaysndbnkno().equals(key))
						newList.add(dto);
				}
				lists.add(newList);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("直接支付根据开户行行号分包出现异常！", e);
		}
		return lists;
	}
	/**
	 * 更正分包
	 * 
	 * @param list
	 * @return
	 * @throws ITFEBizException
	 */
	public static List getListByCorrhandbook(List<TvInCorrhandbookDto> list)
			throws ITFEBizException {
		Map map = new HashMap();
		for (TvInCorrhandbookDto dto : list)
			map.put(String.valueOf(dto.getSfinorgcode()), "");
		if (map.size() == 0)
			throw new ITFEBizException("查询的主表信息为空！");
		List<List> lists = new ArrayList<List>();
		try {
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				List<TvInCorrhandbookDto> newList = new ArrayList<TvInCorrhandbookDto>();
				for (TvInCorrhandbookDto dto : list) {
					if (String.valueOf(dto.getSfinorgcode()).equals(key))
						newList.add(dto);
				}
				lists.add(newList);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("更正模拟分包出现异常！", e);
		}
		return lists;
	}
	/**
	 * 非税收入分包
	 * 
	 * @param list
	 * @return
	 * @throws ITFEBizException
	 */
	public static List getListByNontaxIncome(List<TvNontaxmainDto> list)
			throws ITFEBizException {
		Map map = new HashMap();
		for (TvNontaxmainDto dto : list)
			map.put(dto.getShold1(), "");
		if (map.size() == 0)
			throw new ITFEBizException("查询的主表信息为空！");
		List<List> lists = new ArrayList<List>();
		try {
			for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				List<TvNontaxmainDto> newList = new ArrayList<TvNontaxmainDto>();
				for (TvNontaxmainDto dto : list) {
					if (String.valueOf(dto.getShold1()).equals(key))
						newList.add(dto);
				}
				lists.add(newList);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("更正模拟分包出现异常！", e);
		}
		return lists;
	}
	/**
	 * 查询凭证状态
	 * 
	 * @param objs
	 * @return
	 */
	public static String getVoucherReturnStatus(Object[] objs) {
		String sdemo = "";
		String description = objs[1] + "";
		if (description == null
				|| description.toUpperCase().trim().equals("NULL")) {
			description = "";
		}
		if ((objs[0] + "").equals("-1")) {
			sdemo = "查看凭证状态异常：未找到对应凭证         "
					+ (description.indexOf("未找到对应凭证") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("0")) {
			sdemo = "发送单状态:对方未接收     "
					+ (description.indexOf("对方未接收") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("1")) {
			sdemo = "发送单状态:对方接收成功     "
					+ (description.indexOf("对方接收成功") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("2")) {
			sdemo = "发送单状态:对方接收失败     "
					+ (description.indexOf("对方接收失败") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("3")) {
			sdemo = "发送单状态:对方签收成功     "
					+ (description.indexOf("对方签收成功") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("4")) {
			sdemo = "发送单状态:对方签收失败     "
					+ (description.indexOf("对方签收失败") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("5")) {
			sdemo = "发送单状态:对方已退回     "
					+ (description.indexOf("对方已退回") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("6")) {
			sdemo = "回单状态:对方未接收     "
					+ (description.indexOf("对方未接收") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("7")) {
			sdemo = "回单状态:对方接收成功     "
					+ (description.indexOf("对方接收成功") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("8")) {
			sdemo = "回单状态:对方接收失败     "
					+ (description.indexOf("对方接收失败") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("9")) {
			sdemo = "回单状态:对方签收成功     "
					+ (description.indexOf("对方签收成功") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("10")) {
			sdemo = "回单状态:对方签收失败     "
					+ (description.indexOf("对方签收失败") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("11")) {
			sdemo = "回单状态:对方已退回     "
					+ (description.indexOf("对方已退回") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("12")) {
			sdemo = "发送单状态:已收到对方回单     "
					+ (description.indexOf("已收到对方回单") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("13")) {
			sdemo = "发送单状态:本方未发送     "
					+ (description.indexOf("本方未发送") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("14")) {
			sdemo = "回单状态:本方未发送     "
					+ (description.indexOf("本方未发送") > -1 ? "" : description);
		} else {
			sdemo = objs[0] + "";
		}
		return sdemo;
	}

	/**
	 * 根据行别代码查找接收机构类型
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	public static String getDecOrgType(TvVoucherinfoDto dto)
			throws ITFEBizException {
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3551)
				|| (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3504)
						&& dto.getSpaybankcode() != null && !dto
						.getSpaybankcode().equals(""))
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3507)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3582)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3587)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5507)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2507)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2252)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5671)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408)
				||(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3512)&&"35122".equals(dto.getSext5()))) {
			return VoucherUtil.getSpaybankType(dto.getStrecode(), dto
					.getSpaybankcode());// 行别代码
		}
		// 云南地税特色
		if (ITFECommonConstant.SRC_NODE.equals("000087100006")) {
			if ((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207) || dto
					.getSvtcode().equals(MsgConstant.VOUCHER_NO_5209))
					&& dto.getSattach() != null
					&& dto.getSattach().trim().length() == 3)
				return dto.getSattach().trim();
		}
		if ((ITFECommonConstant.PUBLICPARAM.contains(",send3208=more,")||ITFECommonConstant.PUBLICPARAM.contains(",send3508=more,")||ITFECommonConstant.PUBLICPARAM.contains(",send3510=more,"))
				&& dto.getSext5() != null
				&& dto.getSext5().trim().length() == 3)
			return dto.getSext5();
		return StateConstant.ORGCODE_FIN;
	}

	/**
	 * 根据主表信息查找凭证列表
	 * 
	 * @param list
	 * @param svtcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static List getVoucherByMainDtoList(List list, String svtcode)
			throws ITFEBizException {
		StringBuffer ls_SQLBf = new StringBuffer(" where ");
		List<String> params = new ArrayList<String>();
		String sql = " (S_DEALNO = ? ) or";
		if (svtcode.equals(MsgConstant.VOUCHER_NO_5207)
				|| svtcode.equals(MsgConstant.VOUCHER_NO_5267)) {
			List<TvPayoutmsgmainDto> mainDtoList = list;
			for (int i = 0; i < mainDtoList.size(); i++) {
				ls_SQLBf.append(sql);
				params.add(mainDtoList.get(i).getSbizno());
			}
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_2301)) {
			List<TvPayreckBankDto> mainDtoList = list;
			for (int i = 0; i < mainDtoList.size(); i++) {
				ls_SQLBf.append(sql);
				params.add(String.valueOf(mainDtoList.get(i).getIvousrlno()));
			}
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_2302)) {
			List<TvPayreckBankBackDto> mainDtoList = list;
			for (int i = 0; i < mainDtoList.size(); i++) {
				ls_SQLBf.append(sql);
				params.add(String.valueOf(mainDtoList.get(i).getIvousrlno()));
			}
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5209)) {
			List<TvDwbkDto> mainDtoList = list;
			for (int i = 0; i < mainDtoList.size(); i++) {
				ls_SQLBf.append(sql);
				params.add(String.valueOf(mainDtoList.get(i).getIvousrlno()));
			}
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5106)) {
			List<TvGrantpaymsgmainDto> mainDtoList = list;
			for (int i = 0; i < mainDtoList.size(); i++) {
				ls_SQLBf.append(sql);
				params.add(String.valueOf(mainDtoList.get(i).getIvousrlno()));
			}
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5108)) {
			List<TvDirectpaymsgmainDto> mainDtoList = list;
			for (int i = 0; i < mainDtoList.size(); i++) {
				ls_SQLBf.append(sql);
				params.add(String.valueOf(mainDtoList.get(i).getIvousrlno()));
			}
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5201)) {
			List<TfDirectpaymsgmainDto> mainDtoList = list;
			for (int i = 0; i < mainDtoList.size(); i++) {
				ls_SQLBf.append(sql);
				params.add(String.valueOf(mainDtoList.get(i).getIvousrlno()));
			}
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_2252)) {
			List<TfPaybankRefundmainDto> mainDtoList = list;
			for (int i = 0; i < mainDtoList.size(); i++) {
				ls_SQLBf.append(sql);
				params.add(String.valueOf(mainDtoList.get(i).getIvousrlno()));
			}
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5407)) {
			List<TvInCorrhandbookDto> mainDtoList = list;
			for (int i = 0; i < mainDtoList.size(); i++) {
				ls_SQLBf.append(sql);
				params.add(String.valueOf(mainDtoList.get(i).getIvousrlno()));
			}
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5671)||svtcode.equals(MsgConstant.VOUCHER_NO_5408)) {
			List<TvNontaxmainDto> mainDtoList = list;
			for (int i = 0; i < mainDtoList.size(); i++) {
				ls_SQLBf.append(sql);
				params.add(String.valueOf(mainDtoList.get(i).getSdealno()));
			}
		} else
			throw new ITFEBizException("没有定义此业务类型!");

		String ls_SQL = ls_SQLBf.toString();
		ls_SQL = ls_SQL.substring(0, ls_SQL.length() - 2);
		List<TvVoucherinfoDto> voucherList = null;
		try {
			voucherList = DatabaseFacade.getDb().find(TvVoucherinfoDto.class,
					ls_SQL, params);
		} catch (JAFDatabaseException e) {
			logger.error("根据主表信息查找凭证列表错误： " + e);
			throw new ITFEBizException("根据主表信息查找凭证列表异常！", e);
		}
		return voucherList;
	}

	// 批量更新凭证状态
	public static int updateVoucherState(List<TvVoucherinfoDto> list)
			throws ITFEBizException {
		TvVoucherinfoDto[] dtos = new TvVoucherinfoDto[list.size()];
		dtos = list.toArray(dtos);
		try {
			DatabaseFacade.getDb().update(dtos);
		} catch (JAFDatabaseException e) {
			logger.error("更新凭证信息时出现异常：" + e);
			throw new ITFEBizException("更新凭证信息时出现异常！", e);
		}
		return list.size();
	}

	/**
	 * 更新凭证状态
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public static int updateVoucherState(TvVoucherinfoDto dto)
			throws ITFEBizException {
		List<TvVoucherinfoDto> list = new ArrayList<TvVoucherinfoDto>();
		list.add(dto);
		TvVoucherinfoDto[] dtos = new TvVoucherinfoDto[list.size()];
		dtos = list.toArray(dtos);
		try {
			DatabaseFacade.getDb().update(dtos);
		} catch (JAFDatabaseException e) {
			logger.error("更新凭证信息时出现异常：" + e);
			throw new ITFEBizException("更新凭证信息时出现异常！", e);
		}
		return list.size();
	}

	/**
	 * 发送凭证报文TIPS
	 * 
	 * @param vDto
	 * @throws ITFEBizException
	 */
	public static void sendTips(TvVoucherinfoDto vDto) throws ITFEBizException {
		try {
			MuleMessage message = new DefaultMuleMessage("");
			MuleClient client = new MuleClient();

			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
					MsgConstant.VOUCHER + "_" + vDto.getSvtcode());
			message.setProperty("packno", vDto.getSpackno());
			message = client.send("vm://VoucherSendTips", message);
		} catch (MuleException e1) {
			logger.error(e1);
			throw new ITFEBizException("发送凭证报文TIPS出现异常！", e1);
		}
	}

	/**
	 * 凭证java转化xml报文组件
	 * 
	 * @param vDto
	 * @param map
	 * @throws ITFEBizException
	 */
	public static void sendTips(TvVoucherinfoDto vDto, Map map)
			throws ITFEBizException {
		try {
			String ls = "";
			MuleMessage message = new DefaultMuleMessage(ls);
			MuleClient client = new MuleClient();
			// 上海实拨资金退款模板与其他地方不一样
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
					&& vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3208)) {
				message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
						MsgConstant.VOUCHER + "_" + vDto.getSvtcode() + "SH");
			} else {
				message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
						MsgConstant.VOUCHER + "_" + vDto.getSvtcode());
			}
			message.setProperty("fileName", vDto.getSfilename());
			message.setProperty("xml", map);
			message = client.send("vm://VoucherXmlTransformer", message);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("调用MQ后台操作出现异常！", e);
		}
	}

	/**
	 * 凭证报文解析
	 * 
	 * @param vDto
	 * @param ls_FinOrgCode
	 * @param dealnos
	 * @param Xml
	 * @throws ITFEBizException
	 * @throws MuleException
	 * @throws Exception
	 */
	public static void sendTips(TvVoucherinfoDto vDto, String ls_FinOrgCode,
			Map<String, String> dealnos, String Xml) throws ITFEBizException,
			MuleException, Exception {

		MuleMessage message = new DefaultMuleMessage("");
		MuleClient client = new MuleClient();
		message.setStringProperty(MessagePropertyKeys.MSG_NO_KEY, vDto
				.getSvtcode());

		message.setProperty("fileName", vDto.getSfilename());
		message.setProperty("orgCode", vDto.getSorgcode());
		message.setProperty("finOrgCode", ls_FinOrgCode);// 财政代码
		message.setProperty("dealnos", dealnos);
		message.setPayload(Xml);
		message = client.send("vm://VoucherLoadMsg", message);

	}

	/**
	 * 校验成功后更新状态
	 * 
	 * @return
	 * @throws ITFEBizException
	 * @throws ITFEBizException
	 */
	public static void voucherVerifyUpdateStatus(TvVoucherinfoDto vDto,
			String tablename, String errMsg, boolean flag)
			throws JAFDatabaseException, ITFEBizException {
		if (flag) {
			TsTreasuryDto streDto = new TsTreasuryDto();
			streDto.setStrecode(vDto.getStrecode());
			List<TsTreasuryDto>	streDtoList = null;
			try {
				streDtoList = CommonFacade.getODB().findRsByDto(streDto);
			} catch (ValidateException e) {
				logger.error(e + "查询国库信息失败！");
			}
			
			// 更新凭证索引表
			if (vDto.getSvtcode().equalsIgnoreCase(MsgConstant.VOUCHER_NO_5257)) {
				// 实拨清单校验通过即可更改为处理成功
				vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				vDto.setSdemo("处理成功");
			} else if (vDto.getSvtcode().equalsIgnoreCase(MsgConstant.VOUCHER_NO_5671)
					||(vDto.getSvtcode().equalsIgnoreCase(MsgConstant.VOUCHER_NO_5408)&&!streDtoList.get(0).getStreattrib().equals("2"))) {//5408非代理库补录资金收纳流水号
				// 非税清单校验通过后为校验中，补录资金收纳流水号后才为校验成功
				vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT);
				vDto.setSdemo("校验中,等待补录资金收纳流水号");
			}else {
				vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				vDto.setSdemo("校验成功");
			}
			DatabaseFacade.getDb().update(vDto);
			logger.debug("===================== 校验成功========================");
		} else {
			vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_FAIL);
			vDto.setSdemo("凭证编号:[" + vDto.getSvoucherno() + "]" + errMsg);
			DatabaseFacade.getDb().update(vDto);
			if(vDto.getSvtcode().equalsIgnoreCase(MsgConstant.VOUCHER_NO_5408))
			{
				TvNontaxmainDto searchdto = new TvNontaxmainDto();
				searchdto.setSdealno(vDto.getSdealno());
				searchdto = (TvNontaxmainDto)DatabaseFacade.getDb().find(searchdto);
				searchdto.setSstatus("");
				DatabaseFacade.getDb().update(searchdto);
			}
			logger.debug("===================== 校验失败========================");
		}
	}

	/**
	 * 业务要素全部校验成功后更新状态为"校验中"
	 * 
	 * @param vDto
	 * @param OriginalVtCode凭证类型
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static void voucherVerifyUpdateStatus(TvVoucherinfoDto vDto,
			String OriginalVtCode) throws JAFDatabaseException,
			ITFEBizException {
		// 更新凭证索引表
		vDto.setScheckvouchertype(OriginalVtCode);// 比对凭证类型
		vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT);
		vDto.setSdemo("等待[" + OriginalVtCode + "]凭证进行业务要素勾兑");
		DatabaseFacade.getDb().update(vDto);
	}

	/**
	 * 校验成功后更新状态(用于陕西特色业务---读取财政)
	 * 
	 * @author sunyan
	 * @throws ITFEBizException
	 * @throws ITFEBizException
	 */
	public static void voucherVerifyUpdateStatusSX(TvVoucherinfoSxDto vDto,
			String tablename, String errMsg, boolean flag)
			throws JAFDatabaseException, ITFEBizException {
		if (flag) {
			// 更新凭证索引表
			vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
			vDto.setSdemo("校验成功");
			DatabaseFacade.getDb().update(vDto);
			logger.debug("===================== 校验成功========================");
		} else {
			vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_FAIL);
			vDto.setSdemo(errMsg);
			DatabaseFacade.getDb().update(vDto);
			logger.debug("===================== 校验失败========================");
		}
	}

	/**
	 * 批量更新额度处理成功后更新业务表状态 flag，true代表处理成功，false代表处理失败
	 * 
	 * @return
	 * @throws ITFEBizException
	 */
	public static void voucherVerifyBatchUpdateStatus(TvVoucherinfoDto vDto,
			boolean flag) throws JAFDatabaseException {
		SQLExecutor updateExec = null;
		String tablename = returnAllTableNameByVtcode(vDto.getSvtcode());
		String sql = "";
		if (tablename.equals("TV_PAYRECK_BANK")) {
			sql = "UPDATE  " + tablename
					+ " SET S_RESULT = ? WHERE I_VOUSRLNO = ?";
		} else if (tablename.equals("TV_PAYOUTMSGMAIN")) {
			sql = "UPDATE  " + tablename
					+ " SET S_STATUS = ? WHERE S_BIZNO = ?";
		} else {
			sql = "UPDATE  " + tablename
					+ " SET S_STATUS = ? WHERE I_VOUSRLNO = ?";
		}
		// 如果是授权支付额度，要更新明细表状态
		String ls_UpdateSub = "";
		if (MsgConstant.VOUCHER_NO_5106.equals(vDto.getSvtcode())) {
			ls_UpdateSub = "UPDATE TV_GRANTPAYMSGSUB SET S_STATUS = ? where I_VOUSRLNO = ? ";
		}
		updateExec = DatabaseFacade.getDb().getSqlExecutorFactory()
				.getSQLExecutor();
		if (flag) {
			// 更新凭证索引表
			vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
			vDto.setSdemo("处理成功");
			// 更新业务表
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
			updateExec.addParam(Long.valueOf(vDto.getSdealno()));
			logger.debug("===================== 处理成功========================");
		} else {
			vDto.setSstatus(DealCodeConstants.VOUCHER_FAIL_TCBS);
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);
			updateExec.addParam(vDto.getSdealno());
			logger.debug("===================== 处理失败========================");
		}
		DatabaseFacade.getDb().update(vDto);
		updateExec.runQuery(sql);
		if (!ls_UpdateSub.equals("")) {
			if (flag) {
				updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
			} else {
				updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);
			}
			updateExec.addParam(Long.valueOf(vDto.getSdealno()));
			updateExec.runQueryCloseCon(ls_UpdateSub);
		}
		if (updateExec != null) {
			updateExec.closeConnection();
		}

	}

	public static boolean isVoucherRepeat(TvVoucherinfoDto dto)
			throws ITFEBizException {
		boolean flag = false;
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3552)) {
			TvVoucherinfoDto vDto = new TvVoucherinfoDto();
			vDto.setSorgcode(dto.getSorgcode());
			vDto.setStrecode(dto.getStrecode());
			vDto.setScheckdate(dto.getScheckdate());
			vDto.setSvtcode(dto.getSvtcode());
			try {
				List list = (List) CommonFacade.getODB().findRsByDto(vDto);
				return !(list == null || list.size() == 0);

			} catch (Exception e) {
				logger.error(e);
				throw new ITFEBizException(e.getMessage(), e);
			}

		}
		return flag;
	}

	/*
	 * 获取授权支付额度业务的流水号
	 */
	public static String getGrantSequence() throws ITFEBizException {
		String currentDate = TimeFacade.getCurrentStringTime();
		long mainvou = 100000000;
		String seq = "";
		try {
			seq = SequenceGenerator.getNextByDb2(
					SequenceName.TRA_ID_SEQUENCE_KEY,
					SequenceName.TRAID_SEQ_CACHE,
					SequenceName.TRAID_SEQ_STARTWITH);
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
		seq = String.valueOf(mainvou + Long.parseLong(seq));
		return currentDate + seq.substring(seq.length() - 8, seq.length());
	}
	
	/**
	 * 获取明细流水号
	 * 
	 * @param mainvou
	 * @param i
	 * @return
	 * @throws ITFEBizException
	 */
	public static String getDetailSequence(String mainvou, int i)
			throws ITFEBizException {
		return mainvou + String.valueOf(10000 + i).substring(1, 5);
	}

	/*
	 * list,value = DealNos 根据授权支付额度根据交易流水号更新索引表状态
	 */
	public static Boolean updateVoucherStatus(List<String> list, String status,
			String optionType) {
		if (null != list && list.size() > 0) {
			String tableName = "TV_GRANTPAYMSGMAIN";
			String ls_Demo = "";
			if (DealCodeConstants.DEALCODE_ITFE_FAIL.equals(status)) {
				ls_Demo = "处理失败";
			} else if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(status)) {
				ls_Demo = "处理成功";
			}
			StringBuffer lsb_VoucherDealNos = new StringBuffer("");
			String ls_VoucherDealNos = "";
			for (int i = 0; i < list.size(); i++) {
				lsb_VoucherDealNos.append("'" + list.get(i) + "'" + ",");
			}
			ls_VoucherDealNos = lsb_VoucherDealNos.toString();
			ls_VoucherDealNos = ls_VoucherDealNos.substring(0,
					ls_VoucherDealNos.length() - 1);
			String ls_UpdateVoucher = "update TV_VOUCHERINFO set S_STATUS = '"
					+ status + "',S_DEMO = '" + ls_Demo
					+ "' where S_DEALNO in (select I_VOUSRLNO from "
					+ tableName + " where S_DEALNO in (" + ls_VoucherDealNos
					+ "))" + " and (S_STATUS = ? or S_STATUS = ?)";
			SQLExecutor updateExce = null;
			try {
				updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();
				// 只更新凭证索引表状态为【已发送】【已收妥】的记录
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE);
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED);
				updateExce.runQueryCloseCon(ls_UpdateVoucher);
			} catch (Exception e) {
				logger.debug("授权支付额度凭证更新失败");
			} finally {
				if (null != updateExce)
					updateExce.closeConnection();
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * 电子凭证库与TIPS规范字段校验比对
	 * 
	 * @param 电子凭证库报文节点
	 *            node
	 * @param 电子凭证库报文节点的值
	 *            value
	 * @param TIPS规范字段长度
	 *            tipsLength
	 * @return
	 * @throws ITFEBizException
	 */
	public static String voucherCheckVaild(List list, String errMsg) {
		String err = "";
		for (String[] arr : (List<String[]>) list) {
			String node = arr[0];
			String value = arr[1];
			String tipsLength = arr[2];
			if (value == null || value.getBytes().length == 0) {
				err = err + "节点：" + node + "值为空,";
			} else if (value.length() > Integer.parseInt(tipsLength)) {
				err = err + "节点：" + node + "宽度" + value.length()
						+ "超过tips规范最大宽度" + tipsLength + ",";
			}
		}
		return err.equals("") == true ? "报文不规范：" + errMsg : err.substring(0,
				err.lastIndexOf(","));
	}

	/*
	 * 根据凭证类型返回业务表名称（额度控制判断使用）
	 */
	public static String returnTableNameByVtcode(String vtCode) {
		String ls_TableName = "";
		if (MsgConstant.VOUCHER_NO_5106.equals(vtCode))
			ls_TableName = "TV_GRANTPAYMSGMAIN";
		else if (MsgConstant.VOUCHER_NO_5108.equals(vtCode))
			ls_TableName = "TV_DIRECTPAYMSGMAIN";
		else if (MsgConstant.VOUCHER_NO_2301.equals(vtCode))
			ls_TableName = "TV_PAYRECK_BANK";
		else if (MsgConstant.VOUCHER_NO_2302.equals(vtCode))
			ls_TableName = "TV_PAYRECK_BANK_BACK";
		else if (MsgConstant.VOUCHER_NO_8207.equals(vtCode))
			ls_TableName = "TF_PAYMENT_DETAILSMAIN";
		return ls_TableName;
	}

	/*
	 * 根据凭证类型返回业务表名称（全业务）
	 */
	public static String returnAllTableNameByVtcode(String vtCode) {
		String ls_TableName = "";
		if (MsgConstant.VOUCHER_NO_5106.equals(vtCode))
			ls_TableName = "TV_GRANTPAYMSGMAIN";
		else if (MsgConstant.VOUCHER_NO_5108.equals(vtCode))
			ls_TableName = "TV_DIRECTPAYMSGMAIN";
		else if (MsgConstant.VOUCHER_NO_2301.equals(vtCode))
			ls_TableName = "TV_PAYRECK_BANK";
		else if (MsgConstant.VOUCHER_NO_2302.equals(vtCode))
			ls_TableName = "TV_PAYRECK_BANK_BACK";
		else if (MsgConstant.VOUCHER_NO_5207.equals(vtCode)
				|| MsgConstant.VOUCHER_NO_5267.equals(vtCode))
			ls_TableName = "TV_PAYOUTMSGMAIN";
		else if (MsgConstant.VOUCHER_NO_5209.equals(vtCode))
			ls_TableName = "TV_DWBK";
		else if (MsgConstant.VOUCHER_NO_5201.equals(vtCode))
			ls_TableName = "TF_DIRECTPAYMSGMAIN";
		else if (MsgConstant.VOUCHER_NO_8207.equals(vtCode))
			ls_TableName = "TF_PAYMENT_DETAILSMAIN";
		else if (MsgConstant.VOUCHER_NO_5253.equals(vtCode))
			ls_TableName = "TF_DIRECTPAY_ADJUSTMAIN";
		else if (MsgConstant.VOUCHER_NO_5351.equals(vtCode))
			ls_TableName = "TF_PAYBANK_REFUNDMAIN";
		else if (MsgConstant.VOUCHER_NO_2252.equals(vtCode))
			ls_TableName = "TF_REPORT_INCOME_MAIN";
		else if (MsgConstant.VOUCHER_NO_5951.equals(vtCode))
			ls_TableName = "TF_UNITRECORDMAIN";
		else if (MsgConstant.VOUCHER_NO_5671.equals(vtCode)||MsgConstant.VOUCHER_NO_5408.equals(vtCode))
			ls_TableName = "TV_NONTAXMAIN";
		else if (MsgConstant.VOUCHER_NO_5407.equals(vtCode))
			ls_TableName = "TV_IN_CORRHANDBOOK";
		return ls_TableName;
	}

	/*
	 * 根据凭证类型返回业务表dto
	 */
	public static IDto returnDtoByVtcode(String vtCode) {
		IDto dto = null;
		if (MsgConstant.VOUCHER_NO_5106.equals(vtCode))
			dto = new TvGrantpaymsgmainDto();
		else if (MsgConstant.VOUCHER_NO_5108.equals(vtCode))
			dto = new TvDirectpaymsgmainDto();
		else if (MsgConstant.VOUCHER_NO_2301.equals(vtCode))
			dto = new TvPayreckBankDto();
		else if (MsgConstant.VOUCHER_NO_2302.equals(vtCode))
			dto = new TvPayreckBankBackDto();
		return dto;
	}

	/**
	 * 凭证退回批量业务主表状态
	 * 
	 * @param list
	 * @throws ITFEBizException
	 */
	public static void voucherReturnBackUpdateStatus(List list)
			throws ITFEBizException {
		SQLExecutor updateExec = null;
		String tablename = returnAllTableNameByVtcode(((TvVoucherinfoDto) list
				.get(0)).getSvtcode());
		String sql = "";
		if (tablename.equals("TV_PAYRECK_BANK")) {
			sql = "UPDATE  " + tablename
					+ " SET S_RESULT = ? WHERE I_VOUSRLNO IN( '";
		} else if (tablename.equals("TV_PAYOUTMSGMAIN")) {
			sql = "UPDATE  " + tablename
					+ " SET S_STATUS = ? WHERE S_BIZNO IN( '";
		}else if (tablename.equals("TV_NONTAXMAIN")) {
			sql = "UPDATE  " + tablename
			+ " SET S_STATUS = ? WHERE S_DEALNO IN( '";
		} else {
			sql = "UPDATE  " + tablename
					+ " SET S_STATUS = ? WHERE I_VOUSRLNO IN( '";
		}
		for (TvVoucherinfoDto dto : (List<TvVoucherinfoDto>) list) {
			sql = sql + dto.getSdealno() + "' , '";
		}
		sql = sql.substring(0, sql.lastIndexOf(",")) + ")";

		try {
			updateExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			updateExec.clearParams();
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);
			updateExec.runQueryCloseCon(sql);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		} finally {
			if (updateExec != null) {
				updateExec.closeConnection();
			}
		}
	}

	/**
	 * 根据征收机关代码规则获取征收机关代码名称
	 * 
	 * @param staxorgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static String getStaxorgname(String staxorgcode)
			throws ITFEBizException {
		String staxorgname = "";
		if (staxorgcode.equals("000000000000")) {
			staxorgname = "全辖";
		} else if (staxorgcode.equals("111111111111")) {
			staxorgname = "国税部门";
		} else if (staxorgcode.equals("222222222222")) {
			staxorgname = "地税部门";
		} else if (staxorgcode.equals("333333333333")) {
			staxorgname = "海关部门";
		} else if (staxorgcode.equals("444444444444")) {
			staxorgname = "财政部门";
		} else if (staxorgcode.equals("555555555555")) {
			staxorgname = "其它部门";
		} else {
			staxorgname = "征收机关未维护";
//			throw new ITFEBizException("征收机关代码：" + staxorgcode
//					+ " 对应的征收机关名称在\" 征收机关代码维护 \"中未维护！");
		}
		return staxorgname;
	}

	/**
	 * 签收成功异常、签收失败异常删除之前相同凭证的已读取状态的数据
	 * 
	 * @param voucherdto
	 * @param flag
	 *            签收成功异常true、签收失败异常标志false
	 * @throws ITFEBizException
	 */
	public static void delRepeateData(TvVoucherinfoDto voucherdto, boolean flag)
			throws ITFEBizException {
		String selectsql = " where S_ADMDIVCODE = ? AND S_VTCODE = ? AND S_CREATDATE = ? AND S_VOUCHERNO = ? AND S_STATUS=? AND S_DEALNO <> ? ";
		List paramlist = new ArrayList();
		paramlist.add(voucherdto.getSadmdivcode());
		paramlist.add(voucherdto.getSvtcode());
		paramlist.add(voucherdto.getScreatdate());
		paramlist.add(voucherdto.getSvoucherno());
		paramlist.add(DealCodeConstants.VOUCHER_ACCEPT);// 已读取
		paramlist.add(voucherdto.getSdealno());
		try {
			List<TvVoucherinfoDto> deleteList = DatabaseFacade.getDb().find(
					TvVoucherinfoDto.class, selectsql, paramlist);
			TvVoucherinfoDto[] voucherdtos = new TvVoucherinfoDto[deleteList
					.size()];
			voucherdtos = deleteList.toArray(voucherdtos);
			// 删除索引表
			DatabaseFacade.getDb().delete(voucherdtos);
			if (flag) {
				// 签收成功删除业务表
				for (TvVoucherinfoDto dto : deleteList) {
					String deletesql = "";
					if (MsgConstant.VOUCHER_NO_5207.equals(dto.getSvtcode())
							|| MsgConstant.VOUCHER_NO_5267.equals(dto
									.getSvtcode())) {// 实拨资金
						deletesql = " S_BIZNO = " + dto.getSdealno();
						deleteVouTable(TvPayoutmsgmainDto.tableName(),
								TvPayoutmsgsubDto.tableName(), deletesql);
					} else if (MsgConstant.VOUCHER_NO_5106.equals(dto
							.getSvtcode())) {// 授权支付
						deletesql = " I_VOUSRLNO = "
								+ Long.valueOf(dto.getSdealno());
						deleteVouTable(TvGrantpaymsgmainDto.tableName(),
								TvGrantpaymsgsubDto.tableName(), deletesql);
					} else if (MsgConstant.VOUCHER_NO_5108.equals(dto
							.getSvtcode())) {// 直接支付
						deletesql = " I_VOUSRLNO = "
								+ Long.valueOf(dto.getSdealno());
						deleteVouTable(TvDirectpaymsgmainDto.tableName(),
								TvDirectpaymsgmainDto.tableName(), deletesql);
					} else if (MsgConstant.VOUCHER_NO_2301.equals(dto
							.getSvtcode())) {// 划款申请
						deletesql = " I_VOUSRLNO = "
								+ Long.valueOf(dto.getSdealno());
						deleteVouTable(TvPayreckBankDto.tableName(),
								TvPayreckBankListDto.tableName(), deletesql);
					} else if (MsgConstant.VOUCHER_NO_2302.equals(dto
							.getSvtcode())) {// 退款申请
						deletesql = " I_VOUSRLNO = "
								+ Long.valueOf(dto.getSdealno());
						deleteVouTable(TvPayreckBankBackDto.tableName(),
								TvPayreckBankBackListDto.tableName(), deletesql);
					} else if (MsgConstant.VOUCHER_NO_5209.equals(dto
							.getSvtcode())) {// 收入退付
						deletesql = " I_VOUSRLNO = "
								+ Long.valueOf(dto.getSdealno());
						deleteVouTable(TvDwbkDto.tableName(), null, deletesql);
					}
				}
			}
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("删除凭证编号" + voucherdto.getSvoucherno()
					+ " 重复的数据出现异常！");
		}

	}

	/**
	 * 删除数据 - 按主子表条件清理
	 * 
	 * @param maintable
	 * @param subtable
	 * @param moveSql
	 * @throws ITFEBizException
	 */
	public static void deleteVouTable(String maintable, String subtable,
			String delSql) throws ITFEBizException {
		SQLExecutor delSubExec = null;
		SQLExecutor delMainExec = null;
		try {
			if (null != subtable && !"".equals(subtable.trim())) {
				// 第一步 删除交易子表
				String delsubSql = "delete from " + subtable + " where "
						+ delSql;
				delSubExec = DatabaseFacade.getODB().getSqlExecutorFactory()
						.getSQLExecutor();
				delSubExec.runQueryCloseCon(delsubSql);
			}

			if (delSql != null && !"".equals(delSql)) {
				// 第二步 删除交易主表
				String delmainSql = "delete from " + maintable + " where "
						+ delSql;
				delMainExec = DatabaseFacade.getODB().getSqlExecutorFactory()
						.getSQLExecutor();
				delMainExec.runQueryCloseCon(delmainSql);
			}
		} catch (JAFDatabaseException e) {
			logger.error("数据删除失败！", e);
			throw new ITFEBizException("数据删除失败！", e);
		} finally {
			if (delSubExec != null) {
				delSubExec.closeConnection();
			}
			if (delMainExec != null) {
				delMainExec.closeConnection();
			}
		}
	}

	/**
	 * 生成凭证文件名称
	 * 
	 * @param svtcode
	 * @param screatdate
	 * @param sdealno
	 * @return
	 */
	public static String getVoucherFileName(String svtcode, String screatdate,
			String sdealno) {
		return ITFECommonConstant.FILE_ROOT_PATH + File.separator + "Voucher"
				+ File.separator + screatdate + File.separator + "send"
				+ svtcode + "_" + sdealno + ".msg";
	}

	/**
	 * 接收凭证附件(绿色通道)
	 * 
	 * @param stYear
	 * @param admDivCode
	 * @throws ITFEBizException
	 */
	public static int recvVoucherAttach(TvVoucherinfoDto dto)
			throws ITFEBizException {
		map = new HashMap();
		getData(Integer.parseInt(dto.getSstyear()), dto.getSadmdivcode());
		return mergeFile(dto);
	}

	/**
	 * 读取接收凭证附件
	 * 
	 * @param stYear
	 * @param admDivCode
	 * @throws ITFEBizException
	 */
	public static void getData(int stYear, String admDivCode)
			throws ITFEBizException {
		VoucherService voucherService = new VoucherService();
		byte[] bytes = voucherService.getData("001", stYear, admDivCode);
		if (bytes != null && bytes.length > 0) {
			parseXml(bytes);
			getData(stYear, admDivCode);
		}
	}
	public static void main(String[] mains)
	{
		FileInputStream input = null;
		try {
			input = new FileInputStream("D:/sd.xml");
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];

			do {
				int size = input.read(buffer);
				if (size == -1)
					break;
				byteArray.write(buffer, 0, size);
			} while (true);
			byte[] data = byteArray.toByteArray();
			map = new HashMap();
			parseXml(data);
			
			TvVoucherinfoDto dto = new TvVoucherinfoDto();
			dto.setSorgcode("000000000000");
			dto.setStrecode("0000000000");
			mergeFile(dto);
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		} catch (Exception e) {
			
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception ex) {

				}
			}
		}
	}
	/**
	 * 解析接收凭证附件报文
	 * 
	 * @param bytes
	 */
	public static void parseXml(byte[] bytes) {
		try {
			String basexml = new String(bytes, "GBK");
			String xml = null;
			// 对发过来的原始加密报文进行第一次解密 @author 张会斌
			logger.info("绿色通道读取的未解码报文:"+basexml);
			try
			{
				xml = base64Decode(basexml);
			}catch(Exception ee)
			{
				logger.error("绿色通道二次解码:",ee);
				BASE64Decoder base64Decoder = new BASE64Decoder();
				byte[] out = base64Decoder.decodeBuffer(basexml);
				xml = new String(out, "GBK");
			}
			logger.info("绿色通道读取的解码报文:"+xml);
			Document fxrDoc = null;
			try{
				fxrDoc = DocumentHelper.parseText(xml);
			}catch(Exception xe)
			{
				logger.error("绿色通道三次解码:",xe);
				BASE64Decoder base64Decoder = new BASE64Decoder();
				byte[] out = base64Decoder.decodeBuffer(basexml);
				xml = new String(out, "GBK");
				fxrDoc = DocumentHelper.parseText(xml);
			}
			
			Node File = fxrDoc.selectSingleNode("File");
			String FileName = File.selectSingleNode("FileName").getText();
			String FileBlockCount = File.selectSingleNode("FileBlockCount")
					.getText();
			String AfewBlock = File.selectSingleNode("AfewBlock").getText();

//			String AdmDivCode = File.selectSingleNode("AdmDivCode")==null?"":File.selectSingleNode("AdmDivCode").getText();
//			String SendDate = File.selectSingleNode("SendDate")==null?"":File.selectSingleNode("SendDate").getText();
			String VtCode = File.selectSingleNode("VtCode")==null?"":File.selectSingleNode("VtCode").getText();
			if(VtCode==null||"".equals(VtCode))
			{
				VtCode = File.selectSingleNode("vtCode")==null?"":File.selectSingleNode("vtCode").getText();
			}
			String VoucherNos = File.selectSingleNode("VoucherNos")==null?"":File.selectSingleNode("VoucherNos").getText();
			if(VoucherNos==null||"".equals(VoucherNos))
			{
				VoucherNos = File.selectSingleNode("voucherNo")==null?"":File.selectSingleNode("voucherNo").getText();
			}
			VoucherNos = VoucherNos.replace(",", "-");
			// String Attach = File.selectSingleNode("Attach").getText();
			String FileData = File.selectSingleNode("FileData").getText();

			if (StringUtils.isBlank(FileName)
					|| StringUtils.isBlank(FileBlockCount)
					|| StringUtils.isBlank(AfewBlock)
					|| StringUtils.isBlank(FileData)) {
				logger.error("接收文件失败，报文字段FileName、FileBlockCount、AfewBlock、FileData存在空值！");
				return;
			}
			// 将凭证附件写入缓存
			List list = null;
			if(ITFECommonConstant.PUBLICPARAM.contains(",greendata=voucherno,"))
			{
				list =(List) map.get( VoucherNos+"-"+FileName + "#"+ FileBlockCount);
				if (list == null || list.size() == 0) {
					list = new ArrayList();
					map.put(VoucherNos+"-"+FileName + "#" + FileBlockCount, list);
				}
				list.add(FileData + "$" + AfewBlock);
			}else
			{
				list =(List) map.get(FileName + "#"+ FileBlockCount);
				if (list == null || list.size() == 0) {
					list = new ArrayList();
					map.put(FileName + "#" + FileBlockCount, list);
				}
				list.add(FileData + "$" + AfewBlock);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * 将接收的凭证附件报文写入文件
	 * 
	 * @param dto
	 * @throws ITFEBizException
	 */
	public static int mergeFile(TvVoucherinfoDto dto) throws ITFEBizException {
		int result = 0;
		logger.info("处理文件开始，文件包数：============" + map.size());
		if (map.size() == 0)
			return result;
		String separator = File.separator;
		String root = ITFECommonConstant.FILE_ROOT_PATH + "VoucherAttach"
				+ separator + dto.getSorgcode() + separator + dto.getStrecode()
				+ separator + TimeFacade.getCurrentStringTime();
		File file = new File(root);
		if (!file.exists())
			file.mkdirs();
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			try {
				String relateFileName = it.next() + "";
				String FileBlockCount = relateFileName.substring(relateFileName
						.lastIndexOf("#") + 1);// 文件分包总数
				String FileName = relateFileName.substring(0, relateFileName
						.lastIndexOf("#"));// 文件名
				List<String> list = (List) map.get(relateFileName);
				if (list == null || list.size() == 0
						|| list.size() != Integer.parseInt(FileBlockCount)) {
					logger.error("接收文件失败，文件：" + FileName + "接收子文件总数不全！"+(list==null?0:list.size())+"=================="+FileBlockCount);
					continue;
				}else
				{
					logger.info("接收文件成功，文件：" + FileName + "接收子文件总数！"+(list==null?0:list.size())+"=================="+FileBlockCount);
				}
				// 将缓存数据写入文件（追加写入）
				java.io.FileOutputStream out = new java.io.FileOutputStream(
						root + separator + FileName, true);
				// 存储接收到的所有附件信息（加密的）
				StringBuilder Buffer = new StringBuilder();
				for (int i = 1; i <= list.size(); i++) {
					boolean flag = false;
					for (String data : list) {
						String FileData = data.substring(0, data.indexOf("$"));// 文件数据
						String AfewBlock = data
								.substring(data.indexOf("$") + 1);// 文件顺序号
						if (AfewBlock.equals(i + "")) {
							// 将收到的附件信息（加密的）追加写入到Buffer
							Buffer.append(FileData);
							flag = true;
						}
					}
					if (!flag) {
						// 接收文件失败,删除缓存文件
						FileUtil.getInstance().deleteFile(
								root + separator + FileName);
						logger.error("接收文件失败，文件：" + FileName + "未接收文件顺序号为" + i
								+ "的子文件！");
						result -= 1;
						break;
					}
				}
				// 用系统自带的解密类解密附件信息，并将其写入文件
				BASE64Decoder base64Decoder = new BASE64Decoder();
				byte[] decoderBytes = base64Decoder.decodeBuffer(Buffer
						.toString());
				out.write(decoderBytes);
				logger.info("保存文件成功,文件路径："+(root + separator + FileName));
				map.put(relateFileName, "");
				if (out != null)
					out.close();
				result += 1;
			} catch (Exception e) {
				logger.error(e);
				throw new ITFEBizException(e.getMessage());
			}
		}
		map.clear();
		return result;
	}

	/**
	 * 将list集合转成HashMap key：明细Id; vaule：子表dto
	 * 
	 * @param subdtoList
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap convertListToMap(List subdtoList)
			throws ITFEBizException {
		if (subdtoList == null || subdtoList.size() == 0)
			throw new ITFEBizException("子表集合为空！");
		HashMap subdtoMap = new HashMap();
		if ((subdtoList.get(0)) instanceof TfDirectpaymsgsubDto) {// 直接支付凭证5201子表
			for (TfDirectpaymsgsubDto tempsudto : (List<TfDirectpaymsgsubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else if ((subdtoList.get(0)) instanceof TvPayoutmsgsubDto) {// 实拨资金业务信息5207子表
			for (TvPayoutmsgsubDto tempsudto : (List<TvPayoutmsgsubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else if ((subdtoList.get(0)) instanceof TfPaymentDetailssubDto) {// 批量业务支付明细8207子表
			for (TfPaymentDetailssubDto tempsudto : (List<TfPaymentDetailssubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else if ((subdtoList.get(0)) instanceof TfDirectpayAdjustsubDto) {// 直接支付调整凭证5253子表
			for (TfDirectpayAdjustsubDto tempsudto : (List<TfDirectpayAdjustsubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else if ((subdtoList.get(0)) instanceof TvDirectpaymsgsubDto) {// 直接支付额度凭证5253子表
			for (TvDirectpaymsgsubDto tempsudto : (List<TvDirectpaymsgsubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else if ((subdtoList.get(0)) instanceof TfPaybankRefundsubDto) {// 收款银行退款通知2252子表
			for (TfPaybankRefundsubDto tempsudto : (List<TfPaybankRefundsubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else if ((subdtoList.get(0)) instanceof TfGrantpayAdjustsubDto) {// 授权支付调整凭证5351子表
			for (TfGrantpayAdjustsubDto tempsudto : (List<TfGrantpayAdjustsubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else
			throw new ITFEBizException("没有定义此业务类型!");
		return subdtoMap;
	}

	/**
	 * 根据主表的代理银行代码+金额将主表转换成Map
	 * 
	 * @param Object
	 *            idto
	 * @return Map
	 * @throws ITFEBizException
	 */
	public static HashMap convertMainDtoToMap(Object idto)
			throws ITFEBizException {
		HashMap map = new HashMap();
		String paybankcode = null;
		BigDecimal payAmt = BigDecimal.ZERO;
		// 财政授权支付额度5106主表
		if (idto instanceof TvGrantpaymsgmainDto) {
			TvGrantpaymsgmainDto maindto = (TvGrantpaymsgmainDto) idto;
			paybankcode = maindto.getStransactunit();// 代理行号
			payAmt = payAmt.add(maindto.getNmoney());// 总金额
		}
		// 财政授权支付划款申请2301主表
		else if (idto instanceof TvPayreckBankDto) {
			TvPayreckBankDto maindto = (TvPayreckBankDto) idto;
			paybankcode = maindto.getSagentbnkcode();// 代理行号
			payAmt = payAmt.add(maindto.getFamt());// 总金额
		}
		// 财政授权支付调整5351主表
		else if (idto instanceof TfGrantpayAdjustmainDto) {
			TfGrantpayAdjustmainDto maindto = (TfGrantpayAdjustmainDto) idto;
			paybankcode = maindto.getSpaybankcode();// 代理行号
			payAmt = payAmt.add(maindto.getNpayamt());// 总金额
		}
		// 财政直接支付调整5253主表
		else if (idto instanceof TfDirectpayAdjustmainDto) {
			TfDirectpayAdjustmainDto maindto = (TfDirectpayAdjustmainDto) idto;
			paybankcode = maindto.getSpayeeacctbankno();// 代理行号
			payAmt = payAmt.add(maindto.getNpayamt());// 总金额
		}
		// 财政直接支付额度5108主表
		else if (idto instanceof TvDirectpaymsgmainDto) {
			TvDirectpaymsgmainDto maindto = (TvDirectpaymsgmainDto) idto;
			paybankcode = maindto.getSpaybankno();// 代理行号
			payAmt = payAmt.add(maindto.getNmoney());// 总金额
		}
		// 财政直接支付5201主表
		else if (idto instanceof TfDirectpaymsgmainDto) {
			TfDirectpaymsgmainDto maindto = (TfDirectpaymsgmainDto) idto;
			// 如果不是批量业务把代理银行设为国库代码与额度匹配
			if (StateConstant.BIZTYPE_CODE_BATCH.equals(maindto
					.getSbusinesstypecode().trim())) {
				paybankcode = maindto.getSpayeeacctbankno();// 代理行号
			} else {
				paybankcode = maindto.getStrecode(); // 代理行号
			}
			payAmt = payAmt.add(maindto.getNpayamt());// 总金额
		} else
			throw new ITFEBizException("没有定义此业务类型!");
		map.put("paybankcode", paybankcode);
		map.put("payAmt", MtoCodeTrans.transformString(payAmt));
		return map;
	}

	/**
	 * 根据 预算单位代码+功能科目代码+金额 将subdtoList集合转成Map key：预算单位代码|功能科目代码 value：金额
	 * 
	 * @param subdtoList
	 * @return Map
	 * @throws ITFEBizException
	 */
	public static HashMap convertSubListToMap(List subdtoList)
			throws ITFEBizException {
		if (subdtoList == null || subdtoList.size() == 0)
			throw new ITFEBizException("子表集合为空！");
		HashMap<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		String code = null;// 预算单位代码+功能科目代码
		BigDecimal payAmt = BigDecimal.ZERO;// 金额
		// 财政直接支付额度5108子表
		if ((subdtoList.get(0)) instanceof TvDirectpaymsgsubDto) {
			for (TvDirectpaymsgsubDto subdto : (List<TvDirectpaymsgsubDto>) subdtoList) {
				code = subdto.getSbudgetunitcode() + "|"
						+ subdto.getSfunsubjectcode();// 预算单位代码+功能科目代码
				payAmt = subdto.getNmoney();// 金额
				BigDecimal tempAmt = map.get(code);
				if (tempAmt != null)
					payAmt = payAmt.add(tempAmt);
				map.put(code, payAmt);
			}
		}
		// 财政直接支付5201子表
		else if ((subdtoList.get(0)) instanceof TfDirectpaymsgsubDto) {
			for (TfDirectpaymsgsubDto subdto : (List<TfDirectpaymsgsubDto>) subdtoList) {
				code = subdto.getSagencycode() + "|" + subdto.getSexpfunccode();// 预算单位代码+功能科目代码
				payAmt = subdto.getNpayamt();// 金额
				BigDecimal tempAmt = map.get(code);
				if (tempAmt != null)
					payAmt = payAmt.add(tempAmt);
				map.put(code, payAmt);
			}
		}
		// 授权支付额度凭证5106子表
		else if ((subdtoList.get(0)) instanceof TvGrantpaymsgsubDto) {
			for (TvGrantpaymsgsubDto subdto : (List<TvGrantpaymsgsubDto>) subdtoList) {
				code = subdto.getSbudgetunitcode() + "|"
						+ subdto.getSfunsubjectcode();// 预算单位代码+功能科目代码
				payAmt = subdto.getNmoney();// 金额
				BigDecimal tempAmt = map.get(code);
				if (tempAmt != null)
					payAmt = payAmt.add(tempAmt);
				map.put(code, payAmt);
			}
		}
		// 财政授权支付划款申请凭证2301子表
		else if ((subdtoList.get(0)) instanceof TvPayreckBankListDto) {
			for (TvPayreckBankListDto subdto : (List<TvPayreckBankListDto>) subdtoList) {
				code = subdto.getSbdgorgcode() + "|"
						+ subdto.getSfuncbdgsbtcode();// 预算单位代码+功能科目代码
				payAmt = subdto.getFamt();// 金额
				BigDecimal tempAmt = map.get(code);
				if (tempAmt != null)
					payAmt = payAmt.add(tempAmt);
				map.put(code, payAmt);
			}
		}
		// 财政直接授权支付调整零额度凭证5253子表
		else if ((subdtoList.get(0)) instanceof TfDirectpayAdjustsubDto) {
			for (TfDirectpayAdjustsubDto subdto : (List<TfDirectpayAdjustsubDto>) subdtoList) {
				code = subdto.getSagencycode() + "|" + subdto.getSexpfunccode();// 预算单位代码+功能科目代码
				payAmt = subdto.getNpayamt();// 金额
				BigDecimal tempAmt = map.get(code);
				if (tempAmt != null)
					payAmt = payAmt.add(tempAmt);
				map.put(code, payAmt);
			}
		}
		// 财政授权支付调整零额度凭证5351子表
		else if ((subdtoList.get(0)) instanceof TfGrantpayAdjustsubDto) {
			for (TfGrantpayAdjustsubDto subdto : (List<TfGrantpayAdjustsubDto>) subdtoList) {
				code = subdto.getSsupdepcode() + "|" + subdto.getSexpfunccode();// 预算单位代码+功能科目代码
				payAmt = subdto.getNpayamt();// 金额
				BigDecimal tempAmt = map.get(code);
				if (tempAmt != null)
					payAmt = payAmt.add(tempAmt);
				map.put(code, payAmt);
			}
		} else
			throw new ITFEBizException("没有定义此业务类型!");
		return map;
	}

	/**
	 * 接收TCBS回执更新拆分凭证索引表和业务主表及当业务主表全部接收后更新凭证索引表 true 该凭证是拆分凭证 false 该凭证不是拆分凭证
	 * 
	 * @param soritrano
	 *            原交易流水号
	 * @param sstatus
	 *            TCBS处理状态
	 * @param allamt
	 *            实际支付金额
	 * @param sacctdate
	 *            实际支付日期
	 * @param sorientrustDate
	 *            原委托日期
	 * @param ls_Description
	 *            备注
	 * @return
	 */
	public static boolean updateVoucherSplitReceiveTCBS(String soritrano,
			String sstatus, BigDecimal allamt, String sacctdate,
			String sorientrustDate, String ls_Description) {
		// 非上海无纸化不需要拆分凭证
		if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0)
			return false;
		// 根据拆分流水号、委托日期查找原拆分凭证
		TfVoucherSplitDto dto = findVoucherSplitDto(soritrano, sorientrustDate);
		// 拆分凭证索引表记录为空，说明不是拆分凭证
		if (dto == null || StringUtils.isBlank(dto.getSstatus()))
			return false;
		boolean flag = true;
		// 校验拆分凭证状态，若状态为ITFE处理结果 [80000] 处理成功 、ITFE处理结果 [80001] 处理失败 则不操作
		if (dto.getSstatus().equals(DealCodeConstants.DEALCODE_ITFE_SUCCESS)
				|| dto.getSstatus()
						.equals(DealCodeConstants.DEALCODE_ITFE_FAIL))
			return flag;
		// 更新拆分凭证信息
		updateVoucherSpilt(dto, sstatus, ls_Description, allamt, sacctdate);
		// 查找相同凭证类型和凭证编号下的所有拆分凭证
		List list = findVoucherSplitDto(dto);
		if (list == null || list.size() == 0)
			return flag;
		int i = 0;
		BigDecimal nxallamt = BigDecimal.ZERO;
		// 校验拆分凭证是否已全部接收
		for (TfVoucherSplitDto tempdto : (List<TfVoucherSplitDto>) list) {
			if (!(tempdto.getSstatus().equals(
					DealCodeConstants.DEALCODE_ITFE_SUCCESS) || tempdto
					.getSstatus().equals(DealCodeConstants.DEALCODE_ITFE_FAIL)))
				break;
			nxallamt = nxallamt.add(tempdto.getNxallamt());
			i++;
			if (tempdto.getSstatus().equals(
					DealCodeConstants.DEALCODE_ITFE_SUCCESS)) {
				dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
				dto.setSdemo(DealCodeConstants.PROCESS_SUCCESS);
			}
		}
		// 拆分凭证未全部接收，不更新业务主表状态
		if (i != list.size())
			return flag;
		try {
			// 拆分凭证已全部接收，更新业务主表信息
			updateMainDtoByVoucherSplit(dto, nxallamt, dto.getSstatus(), dto
					.getSdemo());
			// 更新5106凭证索引表状态，5106额度不在此流程更新凭证索引表
			if (!dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {
				Voucher voucher = (Voucher) ContextFactory
						.getApplicationContext().getBean(MsgConstant.VOUCHER);
				voucher.VoucherReceiveTCBS(dto.getStrecode(), dto.getSvtcode(),
						dto.getSpackageno(), dto.getScommitdate(), dto
								.getSmainno(), nxallamt, dto.getSstatus(), dto
								.getSdemo());
			}
		} catch (ITFEBizException e) {
			logger.error(e);
			VoucherException.saveErrInfo(dto.getSvtcode(), e);
		} catch (Exception e) {
			logger.error(e);
			VoucherException.saveErrInfo(dto.getSvtcode(), e);
		}
		return flag;
	}

	/**
	 * 更新拆分凭证信息
	 * 
	 * @param dto
	 * @param sstatus
	 * @param ls_Description
	 * @param allamt
	 * @param sacctdate
	 */
	public static void updateVoucherSpilt(TfVoucherSplitDto dto,
			String sstatus, String ls_Description, BigDecimal allamt,
			String sacctdate) {
		dto.setSstatus(sstatus);
		dto.setSdemo(ls_Description);
		dto.setNxallamt(allamt);
		dto.setSxaccdate(sacctdate);
		try {
			DatabaseFacade.getODB().update(dto);
		} catch (JAFDatabaseException e) {
			logger.error("更新拆分凭证信息异常！", e);
			VoucherException.saveErrInfo(dto.getSvtcode(), "更新拆分凭证信息异常！"
					+ e.getMessage());
		}
	}

	/**
	 * 根据拆分凭证更新业务主表信息
	 * 
	 * @param dto
	 * @param nxallamt
	 * @param sstatus
	 * @param ls_Description
	 * @throws ITFEBizException
	 */
	public static void updateMainDtoByVoucherSplit(TfVoucherSplitDto dto,
			BigDecimal nxallamt, String sstatus, String ls_Description)
			throws ITFEBizException {
		String errMsg = "凭证类型：" + dto.getSvtcode() + "，凭证编号："
				+ dto.getSsplitno() + "，委托日期：" + dto.getScommitdate() + "，"
				+ "交易流水号：" + dto.getSsplitvousrlno() + "接收TCBS回执时业务主表不存在。";
		List mainDtoList = null;
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {
			TvGrantpaymsgmainDto maindto = new TvGrantpaymsgmainDto();
			maindto.setIvousrlno(dto.getIvousrlno());
			maindto.setSpackageticketno(dto.getSmainno());
			maindto.setScommitdate(dto.getScommitdate());
			try {
				mainDtoList = CommonFacade.getODB().findRsByDto(maindto);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("根据拆分凭证查找业务信息异常！", e);
			} catch (ValidateException e) {
				logger.error(e);
				throw new ITFEBizException("根据拆分凭证查找业务信息异常！", e);
			}
			// 业务主表不存在，记入异常日志
			if (mainDtoList == null || mainDtoList.size() == 0) {
				logger.error(errMsg);
				VoucherException.saveErrInfo(dto.getSvtcode(), errMsg);
				return;
			}
			maindto = (TvGrantpaymsgmainDto) mainDtoList.get(0);
			// 校验业务主表状态，若状态为ITFE处理结果 [80000] 处理成功 、ITFE处理结果 [80001] 处理失败 则不操作
			if (maindto.getSstatus().equals(
					DealCodeConstants.DEALCODE_ITFE_SUCCESS)
					|| maindto.getSstatus().equals(
							DealCodeConstants.DEALCODE_ITFE_FAIL))
				return;
			maindto.setSstatus(sstatus);
			maindto.setSdemo(ls_Description);
			maindto.setNxallamt(nxallamt);
			maindto.setSaccdate(dto.getSxaccdate());
			// 更新业务主表信息
			try {
				DatabaseFacade.getODB().update(maindto);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("更新业务主表信息异常！", e);
			}
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {
			TvDirectpaymsgmainDto maindto = new TvDirectpaymsgmainDto();
			maindto.setIvousrlno(dto.getIvousrlno());
			maindto.setSpackageticketno(dto.getSmainno());
			maindto.setScommitdate(dto.getScommitdate());

			try {
				mainDtoList = CommonFacade.getODB().findRsByDto(maindto);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("根据拆分凭证查找业务信息异常！", e);
			} catch (ValidateException e) {
				logger.error(e);
				throw new ITFEBizException("根据拆分凭证查找业务信息异常！", e);
			}
			// 业务主表不存在，记入异常日志
			if (mainDtoList == null || mainDtoList.size() == 0) {
				logger.error(errMsg);
				VoucherException.saveErrInfo(dto.getSvtcode(), errMsg);
				return;
			}
			maindto = (TvDirectpaymsgmainDto) mainDtoList.get(0);
			// 校验业务主表状态，若状态为ITFE处理结果 [80000] 处理成功 、ITFE处理结果 [80001] 处理失败 则不操作
			if (maindto.getSstatus().equals(
					DealCodeConstants.DEALCODE_ITFE_SUCCESS)
					|| maindto.getSstatus().equals(
							DealCodeConstants.DEALCODE_ITFE_FAIL))
				return;
			maindto.setSstatus(sstatus);
			maindto.setSdemo(ls_Description);
			maindto.setNxallamt(nxallamt);
			maindto.setSaccdate(dto.getSxaccdate());
			// 更新业务主表信息
			try {
				DatabaseFacade.getODB().update(maindto);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("更新业务主表信息异常！", e);
			}
		}
	}

	/**
	 * 授权支付额度业务接收TCBS回执更新凭证索引表
	 * 
	 * @param orimsgno
	 * @param state
	 * @param spackno
	 */
	public static void updateVoucherStatusRecvTCBSGrantMsg(String soripackno,
			String sorientrustDate) throws JAFDatabaseException {
		String sql = " SELECT AB.I_VOUSRLNO AS I_VOUSRLNO,(CASE WHEN C.S_ID IS NULL THEN '"
				+ DealCodeConstants.DEALCODE_ITFE_FAIL
				+ "' "
				+ " ELSE '"
				+ DealCodeConstants.DEALCODE_ITFE_SUCCESS
				+ "' END ) AS S_STATUS , (CASE WHEN C.S_ID IS NULL "
				+ " THEN 'TCBS处理失败' ELSE '处理成功' END ) AS S_DEMO FROM (SELECT A.S_ID, "
				+ " A.I_VOUSRLNO FROM ( SELECT COUNT(0) "
				+ " AS S_ID,I_VOUSRLNO  FROM TV_GRANTPAYMSGMAIN WHERE S_PACKAGENO= '"
				+ soripackno
				+ "'"
				+ " AND "
				+ " S_COMMITDATE= '"
				+ sorientrustDate
				+ "' GROUP BY I_VOUSRLNO) A ,(SELECT COUNT(0) AS S_ID,I_VOUSRLNO FROM TV_GRANTPAYMSGMAIN "
				+ " WHERE S_PACKAGENO= '"
				+ soripackno
				+ "'  AND S_COMMITDATE='"
				+ sorientrustDate
				+ "' AND ("
				+ " S_STATUS='"
				+ DealCodeConstants.DEALCODE_ITFE_SUCCESS
				+ "' OR S_STATUS='"
				+ DealCodeConstants.DEALCODE_ITFE_FAIL
				+ "')"
				+ " GROUP BY I_VOUSRLNO)B WHERE A.I_VOUSRLNO=B.I_VOUSRLNO AND (A.S_ID= B.S_ID)) AB LEFT JOIN (SELECT COUNT(0) AS S_ID,"
				+ " I_VOUSRLNO FROM TV_GRANTPAYMSGMAIN WHERE S_PACKAGENO='"
				+ soripackno
				+ "' AND S_COMMITDATE='"
				+ sorientrustDate
				+ "' "
				+ " AND (S_STATUS='"
				+ DealCodeConstants.DEALCODE_ITFE_SUCCESS
				+ "' )GROUP BY I_VOUSRLNO)C ON AB.I_VOUSRLNO=C.I_VOUSRLNO";
		// 查询已全部接收TCBS回执的授权支付额度业务主表信息
		SQLExecutor sqlExec = DatabaseFacade.getODB().getSqlExecutorFactory()
				.getSQLExecutor();
		SQLResults rs = sqlExec.runQueryCloseCon(sql,
				TvGrantpaymsgmainDto.class);
		if (rs == null)
			return;
		List<TvGrantpaymsgmainDto> list = (List<TvGrantpaymsgmainDto>) rs
				.getDtoCollection();
		if (list == null || list.size() == 0)
			return;
		Voucher voucher = (Voucher) ContextFactory.getApplicationContext()
				.getBean(MsgConstant.VOUCHER);
		for (TvGrantpaymsgmainDto dto : list) {
			try {
				// 根据TCBS返回报文更新凭证状态
				voucher.VoucherReceiveTCBS(dto.getStrecode(),
						MsgConstant.VOUCHER_NO_5106, dto.getSpackageno(), dto
								.getScommitdate(), dto.getIvousrlno() + "",
						null, dto.getSstatus(), dto.getSdemo());
			} catch (Exception e) {
				logger.error(e);
				VoucherException.saveErrInfo(MsgConstant.VOUCHER_NO_5106, e);
			}
		}
	}

	/**
	 * 直接业务5201接收TCBS回执更新业务主表和凭证索引表（上海）
	 * 
	 * @param strecode
	 * @param sacctdate
	 * @param packno
	 * @param voucherdate
	 * @param voucherno
	 * @param amt
	 * @param sstatus
	 * @param ls_Description
	 * @param soritrano
	 * @param sorientrustdate
	 * @param BusinessTypeCode
	 */
	public static void updateVoucherStatusRecvTCBSDirectMsg(String strecode,
			String sacctdate, String packno, String voucherdate,
			String voucherno, BigDecimal amt, String sstatus,
			String ls_Description, String soritrano, String sorientrustdate,
			String BusinessTypeCode) {

		String updateSql = "update "
				+ TfDirectpaymsgmainDto.tableName()
				+ " set S_STATUS = ? , S_DEMO = ? ,S_XPAYDATE= ? ,N_XPAYAMT = ?  "
				+ " where S_DEALNO = ? and S_COMMITDATE = ? and S_VOUCHERNO = ? and S_STATUS NOT IN (? , ? ) and S_BUSINESSTYPECODE = ?";
		SQLExecutor updateExce = null;
		try {
			TvVoucherinfoDto searchDto = new TvVoucherinfoDto();
			searchDto.setSvoucherno(voucherno);
			searchDto.setSpackno(packno);
			searchDto.setScreatdate(sorientrustdate);
			List list = CommonFacade.getODB().findRsByDto(searchDto);
			if (null != list && list.size() > 0) {
				BusinessTypeCode = ((TvVoucherinfoDto) list.get(0)).getShold4();
			}

			updateExce = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			updateExce.addParam(sstatus);
			updateExce.addParam(ls_Description);
			updateExce.addParam(sacctdate);
			updateExce.addParam(amt);
			updateExce.addParam(soritrano);
			updateExce.addParam(sorientrustdate);
			updateExce.addParam(voucherno);// 原凭证编号
			updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// 不更新状态
			// [80000]
			// 处理成功
			// 的凭证
			updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);// 不更新状态
			// [80001]
			// 处理失败
			// 的凭证
			updateExce.addParam(BusinessTypeCode);// 批量业务（上海）
			updateExce.runQuery(updateSql);
			Voucher voucher = (Voucher) ContextFactory.getApplicationContext()
					.getBean(MsgConstant.VOUCHER);
			// 更新直接支付业务索引表5201的凭证状态（上海）
			voucher.VoucherReceiveTCBS(strecode, MsgConstant.VOUCHER_NO_5201,
					packno, BusinessTypeCode, voucherno, amt, sstatus,
					ls_Description);
			if (BusinessTypeCode.equals(StateConstant.BIZTYPE_CODE_BATCH))
				updateMatinDtoReceiveTCBS(MsgConstant.VOUCHER_NO_8207, sstatus,
						ls_Description, sacctdate, amt, strecode, voucherno,
						voucherdate, MsgConstant.VOUCHER_NO_5201);
			// 批量业务更新批量业务明细索引表8207的凭证状态（上海）
			voucher.VoucherReceiveTCBS(strecode, MsgConstant.VOUCHER_NO_8207,
					MsgConstant.VOUCHER_NO_5201, voucherdate, voucherno, amt,
					sstatus, ls_Description);
		} catch (Exception e) {
			logger.error(e);
			VoucherException.saveErrInfo(null, e);
		} finally {
			updateExce.closeConnection();
		}

	}

	/**
	 *收款银行退款通知2252接收TCBS回执更新业务主表和凭证索引表（上海）
	 * 
	 * @param strecode
	 * @param sacctdate
	 * @param packno
	 * @param voucherdate
	 * @param voucherno
	 * @param amt
	 * @param sstatus
	 * @param ls_Description
	 * @param soritrano
	 * @param sorientrustdate
	 */
	public static void updateVoucherStatusRecvTCBSBankRefund(String strecode,
			String sacctdate, String packno, String voucherdate,
			String voucherno, BigDecimal amt, String sstatus,
			String ls_Description, String soritrano, String sorientrustdate) {
		String updateSql = "update "
				+ TfPaybankRefundmainDto.tableName()
				+ " set S_STATUS = ? , S_DEMO = ? ,S_HOLD1= ? ,S_HOLD2 = ?  "
				+ " where S_DEALNO = ? and S_COMMITDATE = ? and S_VOUCHERNO = ? and S_STATUS NOT IN (? , ? ) ";
		SQLExecutor updateExce = null;
		try {
			updateExce = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			updateExce.addParam(sstatus);
			updateExce.addParam(ls_Description);
			updateExce.addParam(sacctdate);
			updateExce.addParam(amt);
			updateExce.addParam(soritrano);
			updateExce.addParam(sorientrustdate);
			updateExce.addParam(voucherno);// 原凭证编号
			updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// 不更新状态
			// [80000]
			// 处理成功
			// 的凭证
			updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);// 不更新状态
			// [80001]
			// 处理失败
			// 的凭证
			updateExce.runQuery(updateSql);
			Voucher voucher = (Voucher) ContextFactory.getApplicationContext()
					.getBean(MsgConstant.VOUCHER);
			// 更新2252索引表凭证状态（上海）
			voucher.VoucherReceiveTCBS(strecode, MsgConstant.VOUCHER_NO_2252,
					packno, voucherdate, voucherno, amt, sstatus,
					ls_Description);
		} catch (Exception e) {
			logger.error(e);
			VoucherException.saveErrInfo(null, e);
		} finally {
			if (updateExce != null) {
				updateExce.closeConnection();
			}
		}

	}

	/**
	 * 接收TIPS回执更新拆分凭证索引表
	 * 
	 * @param orimsgno
	 * @param state
	 * @param spackno
	 */
	public static void updateVoucherSplitReceiveTIPS(String orimsgno,
			String state, String spackno) {
		// 非上海无纸化不需要拆分凭证
		if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0)
			return;
		// 只接收5106、5108额度拆分凭证
		String svtcode = null;
		if (orimsgno.equals(MsgConstant.MSG_NO_5103))
			svtcode = MsgConstant.VOUCHER_NO_5106;
		else if (orimsgno.equals(MsgConstant.MSG_NO_5102))
			svtcode = MsgConstant.VOUCHER_NO_5108;
		else
			return;
		try {
			String ls_UpdateSql = "update TF_VOUCHER_SPLIT set S_STATUS = ? where S_PACKAGENO = ? AND S_STATUS = ? AND S_VTCODE = ?";
			SQLExecutor updateExec = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExec.addParam(state);
			updateExec.addParam(spackno);
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);
			updateExec.addParam(svtcode);
			updateExec.runQueryCloseCon(ls_UpdateSql);
		} catch (JAFDatabaseException e) {
			logger.error("更新拆分凭证索引表出现异常!", e);
			VoucherException.saveErrInfo(svtcode, "更新拆分凭证索引表出现异常!");
		}
	}

	/**
	 * 接收TCBS回执更新主表状态（上海）
	 * 
	 * @author 张会斌
	 * @param svtcode
	 * @param sstatus
	 * @param sresult
	 * @param dsumamt
	 * @param strecode
	 * @param sorivouno
	 * @param sorivoudate
	 * @param sorivtcode
	 */
	public static void updateMatinDtoReceiveTCBS(String svtcode,
			String sstatus, String sresult, String sacctdate,
			BigDecimal dsumamt, String strecode, String sorivouno,
			String sorivoudate, String sorivtcode) {
		// 非上海无纸化不需要执行此操作
		if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0)
			return;
		// 只更新批量业务支付明细8207业务主表状态
		if (!svtcode.equals(MsgConstant.VOUCHER_NO_8207))
			return;
		String ls_UpdateSql = null;
		String tableName = returnTableNameByVtcode(svtcode);
		if (svtcode.equals(MsgConstant.VOUCHER_NO_8207))
			ls_UpdateSql = " update "
					+ tableName
					+ " set S_STATUS = ? , S_DEMO = ? , N_XSUMAMT = ? , S_XPAYDATE = ? "
					+ " where"
					+ " S_TRECODE = ? and S_VOUDATE = ? and S_ORIGINALVTCODE = ? and S_ORIGINALVOUCHERNO = ? "
					+ " and S_BUSINESSTYPECODE = ? and S_STATUS NOT IN (? , ? ) ";
		try {
			SQLExecutor updateExce = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			updateExce.addParam(sstatus);// 状态
			updateExce.addParam(sresult);// 备注
			updateExce.addParam(dsumamt);// 清算金额
			updateExce.addParam(sacctdate);// 清算日期
			if (svtcode.equals(MsgConstant.VOUCHER_NO_8207)) {
				updateExce.addParam(strecode);
				updateExce.addParam(sorivoudate);// 凭证日期
				updateExce.addParam(sorivtcode);
				updateExce.addParam(sorivouno);
				updateExce.addParam(StateConstant.BIZTYPE_CODE_BATCH);
				updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// 不更新状态
				// [80000]
				// 处理成功
				// 的凭证
				updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);// 不更新状态
				// [80001]
				// 处理失败
				// 的凭证
			}
			updateExce.runQueryCloseCon(ls_UpdateSql);
		} catch (JAFDatabaseException e) {
			logger.error("更新业务主表信息异常！", e);
		}
	}

	/**
	 * 北京 同城清算对账接口转换 8000
	 */
	public static Map verificationOfAccount(String payoutVouType,
			String treCode, String entrustDate, String packNo) {
		String ls_SQL = "";
		if (payoutVouType == null || payoutVouType.equals(""))
			ls_SQL = " AND (S_OPERATIONTYPECODE = '"
					+ BizTypeConstant.BIZ_TYPE_PAY_OUT
					+ "' or S_OPERATIONTYPECODE = '"
					+ BizTypeConstant.BIZ_TYPE_RET_TREASURY
					+ "' or S_OPERATIONTYPECODE = '"
					+ BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY + "') ";
		TvFilepackagerefDto filePackDto = new TvFilepackagerefDto();
		filePackDto.setScommitdate(entrustDate);
		filePackDto.setStrecode(treCode);
		if (payoutVouType != null && !packNo.equals(""))
			filePackDto.setSpackageno(packNo);
		List fileDtoList = null;
		try {
			fileDtoList = CommonFacade.getODB().findRsByDtoForWhere(
					filePackDto, ls_SQL);
		} catch (JAFDatabaseException e) {
			logger.error(e);
		} catch (ValidateException e) {
			logger.error(e);
		} catch (Exception e) {
			logger.error(e);
			VoucherException.saveErrInfo(MsgConstant.MSG_NO_9000, e);
		}
		Map map = null;
		if (fileDtoList != null && fileDtoList.size() > 0) {
			try {
				map = Dto2MapFor8000.tranfor(fileDtoList,treCode);
			} catch (ITFEBizException e) {
				logger.debug(e);
			}
		} else {
			logger.debug("无对应的对账数据");
		}
		return map;
	}

	/**
	 * 获取比对凭证编号
	 * 
	 * @param vtcode
	 * @param voucherCompareVoucherno
	 * @param voucherno
	 * @return
	 */
	public static String getVoucherCompareVoucherno(String vtcode,
			String voucherCompareVoucherno, String voucherno) {
		if (vtcode.equals(MsgConstant.VOUCHER_NO_2252))
			return "支付凭证号（调整凭证号）" + voucherCompareVoucherno;
		else if ((MsgConstant.VOUCHER_NO_5106.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
				.indexOf(",sh,") >= 0)
				|| (MsgConstant.VOUCHER_NO_5108.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
						.indexOf(",sh,") >= 0))
			return "原业务单据号" + voucherCompareVoucherno;
		else if (vtcode.equals(MsgConstant.VOUCHER_NO_8207))
			return "主支付凭证编号" + voucherCompareVoucherno;
		return "凭证编号" + voucherno;
	}

	/**
	 * 上海计息功能-计息的凭证 转移到计息明细表中
	 */
	public static void putVoucherDataToInteresDetail(String orgcode) {
		logger.debug("将计息的凭证 转移到计息明细表中");
		String ls_sqlOrgCodeA = "";
		String ls_sqlOrgCodeB = "";
		if (orgcode != null && !orgcode.equals("")) {
			ls_sqlOrgCodeA = " and a.S_ORGCODE = '" + orgcode + "' ";
			ls_sqlOrgCodeB = " and main.S_BOOKORGCODE = '" + orgcode + "' ";
		}
		SQLExecutor sqlExecutorDetail = null;
		try {
			String ls_sql = "MERGE INTO TF_INTEREST_DETAIL A USING ("
					+ "select TRIM(TRIM(CHAR(main.I_VOUSRLNO))||CHAR(sub.I_SEQNO)) AS S_TRANO,main.S_BOOKORGCODE,main.S_AGENTBNKCODE,main.S_VOUNO,replace(sub.S_PAYDATE,'-','')  AS S_PAYDATE,replace(char(S_XCLEARDATE),'-','') AS S_XCLEARDATE,sub.F_AMT,"
					+ "main.S_TRECODE,banktype.S_BANKTYPE,sub.S_VOUCHERN0,'2301' AS vouchertype	"
					+ "from TV_PAYRECK_BANK main, TS_CONVERTBANKTYPE banktype ,TV_PAYRECK_BANK_LIST sub "
					+ "WHERE S_PAYMODE = '1' "
					+ "and S_RESULT = '80000' "
					+ "and to_DATE(TRIM(S_PAYDATE),'yyyy-mm-dd hh24:mi:ss') < DATE(S_XCLEARDATE) 	"
					+ "AND  DATE(S_XCLEARDATE) <= CURRENT DATE "
					// + "AND main.S_BOOKORGCODE = banktype.S_ORGCODE "
					+ "AND main.S_TRECODE = banktype.S_TRECODE "
					+ "AND main.S_AGENTBNKCODE = banktype.S_BANKCODE "
					+ "AND main.I_VOUSRLNO = sub.I_VOUSRLNO"
					+ ls_sqlOrgCodeB
					+ ") B  "
					+ "ON (BIGINT(b.S_TRANO) = a.I_VOUSRLNO "
					+ "AND a.S_ORGCODE = b.S_BOOKORGCODE "
					+ "and A.S_VOUCHERNO = B.S_VOUNO "
					+ "AND A.S_INTERESTDATE = substr(replace(replace(b.S_PAYDATE,'/',''),'-',''),1,8)	"
					+ "AND A.S_LIQUIDATIONDATE = replace(char(b.S_XCLEARDATE),'-','')  "
					+ "AND A.S_BANKNO = B.S_AGENTBNKCODE AND A.s_vouchertype = B.vouchertype "
					+ ls_sqlOrgCodeA
					+ ")  "
					+ "WHEN NOT MATCHED THen INSERT(I_VOUSRLNO,S_ORGCODE,S_BANKTYPE,S_VOUCHERNO,S_VOUCHERTYPE,S_BANKNO,S_INTERESTDATE,S_LIQUIDATIONDATE,N_MONEY, S_STATUS,S_DEMO,TS_SYSUPDATE,S_USERCODE,S_EXT1,S_EXT2,S_EXT3)  "
					+ "VALUES(S_TRANO,S_BOOKORGCODE,S_BANKTYPE,S_VOUNO,vouchertype,S_AGENTBNKCODE,substr(replace(S_PAYDATE,'/',''),1,8),substr(S_XCLEARDATE,1,8),F_AMT,'','',CURRENT DATE,'','',S_TRECODE,S_VOUCHERN0)";
			sqlExecutorDetail = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExecutorDetail.runQuery(ls_sql);
			sqlExecutorDetail.clearParams();

			// 计算退款
			ls_sql = "MERGE INTO TF_INTEREST_DETAIL A USING (select TRIM(TRIM(CHAR(main.I_VOUSRLNO))||CHAR(sub.I_SEQNO)) AS S_TRANO,main.S_BOOKORGCODE,main.S_AGENTBNKCODE,main.S_VOUNO,replace(main.s_hold2,'-','')  AS S_PAYDATE,replace(char(main.S_XCLEARDATE),'-','') AS S_XCLEARDATE,sub.F_AMT,main.S_TRECODE,banktype.S_BANKTYPE,sub.S_VOUCHERNO,'2302' AS vouchertype from TV_PAYRECK_BANK_BACK main, TS_CONVERTBANKTYPE banktype ,TV_PAYRECK_BANK_BACK_LIST sub WHERE main.S_PAYMODE = '1' and main.S_STATUS = '80000' and main.S_BOOKORGCODE = ? and to_DATE(TRIM(main.S_HOLD2),'yyyymmdd') < DATE(S_XCLEARDATE)   AND main.S_TRECODE = banktype.S_TRECODE AND main.S_AGENTBNKCODE = banktype.S_BANKCODE AND main.I_VOUSRLNO = sub.I_VOUSRLNO AND  DATE(main.S_XCLEARDATE) <= CURRENT DATE) B  ON (BIGINT(b.S_TRANO) = a.I_VOUSRLNO AND a.S_ORGCODE = b.S_BOOKORGCODE and A.S_VOUCHERNO = B.S_VOUNO AND A.S_INTERESTDATE = substr(replace(replace(b.S_PAYDATE,'/',''),'-',''),1,8)	AND A.S_LIQUIDATIONDATE = replace(char(b.S_XCLEARDATE),'-','')  AND A.S_BANKNO = B.S_AGENTBNKCODE  AND A.s_vouchertype = B.vouchertype and a.S_ORGCODE = ? )  WHEN NOT MATCHED THen INSERT(I_VOUSRLNO,S_ORGCODE,S_BANKTYPE,S_VOUCHERNO,S_VOUCHERTYPE,S_BANKNO,S_INTERESTDATE,S_LIQUIDATIONDATE,N_MONEY, S_STATUS,S_DEMO,TS_SYSUPDATE,S_USERCODE,S_EXT1,S_EXT2,S_EXT3)  VALUES(S_TRANO,S_BOOKORGCODE,S_BANKTYPE,S_VOUNO,vouchertype,S_AGENTBNKCODE,substr(replace(S_PAYDATE,'/',''),1,8),substr(S_XCLEARDATE,1,8),F_AMT,'','',CURRENT DATE,'','',S_TRECODE,S_VOUCHERNO)";
			sqlExecutorDetail.addParam(orgcode);
			sqlExecutorDetail.addParam(orgcode);
			sqlExecutorDetail.runQuery(ls_sql);
		} catch (Exception e) {
			logger.debug("定时将计息的凭证 转移到计息明细表中出现异常！");
		} finally {
			if (sqlExecutorDetail != null)
				sqlExecutorDetail.closeConnection();
		}
	}

	/**
	 * 青岛对账凭证编号生成规则
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException 
	 */
	public static String getCheckNo(TvVoucherinfoDto dto, List<IDto> tempList,
			int i) throws ITFEBizException {
		String paybankcode = "";
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3508)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3510)) {
			paybankcode = "011";
		} else {
			MainAndSubDto mdto = (MainAndSubDto) tempList.get(0);
			if (mdto.getMainDto() instanceof TvPayreckBankDto) {
				TvPayreckBankDto ddto = (TvPayreckBankDto) mdto.getMainDto();
				paybankcode = ddto.getSagentbnkcode().substring(0, 3);
			} else if (mdto.getMainDto() instanceof TvPayreckBankBackDto) {
				TvPayreckBankBackDto ddto = (TvPayreckBankBackDto) mdto
						.getMainDto();
				paybankcode = ddto.getSagentbnkcode().substring(0, 3);
			}
		}
		String num = "";
		/*StringBuffer sql = new StringBuffer(
				"SELECT * FROM TV_VOUCHERINFO WHERE S_VTCODE='"
						+ dto.getSvtcode() + "' ");
		sql.append("AND S_CREATDATE='" + TimeFacade.getCurrentStringTime()
				+ "'  ");
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3507)
				&& dto.getSpaybankcode() != null
				&& !"".equals(dto.getSpaybankcode())) {
			sql.append("AND S_PAYBANKCODE='" + dto.getSpaybankcode() + "'  ");
		}
		sql.append(" order by S_VOUCHERNO DESC");
		SQLExecutor sqlExec = null;*/
		/*try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			List list = (List) sqlExec.runQuery(sql.toString(),
					TvVoucherinfoDto.class).getDtoCollection();
			if ((list != null) && (list.size() > 0)) {
				String voucherno = ((TvVoucherinfoDto) list.get(0))
						.getSvoucherno();
				voucherno = voucherno.substring(voucherno.length() - 3,
						voucherno.length());
				 String seqno = VoucherUtil.getGrantSequence();
				 seqno  = seqno.substring(seqno.length() - 3);
				 num = String.valueOf(Integer.parseInt(voucherno) + 1 + seqno);
			} else {
				if(i!=0){
					num = String.valueOf(i+1);
				}else{
					num = "001";
				}
			}
			if (num.length() == 1)
				num = "00" + num;
			else if (num.length() == 2)
				num = "0" + num;
		} catch (JAFDatabaseException e) {
			logger.error("青岛获取凭证编号失败：" + num);
			throw new ITFEBizException("青岛获取凭证编号失败", e);
		} catch (ITFEBizException e) {
			logger.error("青岛获取凭证编号失败：" + num);
			throw new ITFEBizException("青岛获取凭证编号失败", e);
		} finally {
			if (sqlExec != null)
				sqlExec.closeConnection();
		}
		**/
		 String seqno = VoucherUtil.getGrantSequence();
		 seqno  = seqno.substring(seqno.length() - 3);
		 num = String.valueOf(seqno);
		 String voucherno = dto.getSvtcode() + paybankcode
				+ TimeFacade.getCurrentStringTime().substring(2) + num;
		 return voucherno;
	}

	/**
	 * 代理库解析xml
	 * 
	 * @param xml
	 * @throws ITFEBizException
	 */
	public static void acceptVoucherXml(String xml, TvVoucherinfoDto voucherInfo) throws ITFEBizException{
		 Exception error = null;
		 List list = null;
		 try {
			 	String result = "42";	//清算失败
				Document fxrDocVoucher = DocumentHelper.parseText(xml);
				Node voucherNode = fxrDocVoucher.selectSingleNode("Voucher");
				TvVoucherinfoDto tvVoucherinfoDto = new TvVoucherinfoDto();
				tvVoucherinfoDto.setStrecode(voucherInfo.getStrecode());
				tvVoucherinfoDto.setSvoucherno(voucherInfo.getSvoucherno());
				tvVoucherinfoDto.setNmoney(voucherInfo.getNmoney());
				tvVoucherinfoDto.setSext4(voucherNode.selectSingleNode("VouDate").getText());
				list = CommonFacade.getODB().findRsByDto(tvVoucherinfoDto);
				if(null == list || list.size() == 0){
					return ;
				}
				tvVoucherinfoDto = (TvVoucherinfoDto) list.get(0);
				if(MsgConstant.VOUCHER_NO_2301.equals(voucherInfo.getSvtcode())){
//					String XPaySndBnkNo = voucherNode.selectSingleNode("XPaySndBnkNo").getText();
					String XAddWord = voucherNode.selectSingleNode("XAddWord").getText();
					String XClearDate = voucherNode.selectSingleNode("XClearDate").getText();
					String XPayAmt = voucherNode.selectSingleNode("XPayAmt").getText();
					TvPayreckBankDto tvPayreckBankDto = new TvPayreckBankDto();
					tvPayreckBankDto.setIvousrlno(Long.valueOf(tvVoucherinfoDto.getSdealno()));
					list = CommonFacade.getODB().findRsByDto(tvPayreckBankDto);
					if(null == list || list.size() == 0){
						return ;
					}
					tvPayreckBankDto = (TvPayreckBankDto) list.get(0);
					tvPayreckBankDto.setSxcleardate(DateUtil.stringToDate(XClearDate));
					tvPayreckBankDto.setSxpayamt(StringUtils.isBlank(XPayAmt)? BigDecimal.ZERO :new BigDecimal(XPayAmt));
					tvPayreckBankDto.setSresult(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
					tvPayreckBankDto.setSaddword(XAddWord);
					if(StringUtils.isNotBlank(XPayAmt) && tvPayreckBankDto.getSxpayamt().compareTo(BigDecimal.ZERO) != 0){
						result = "41";	//清算成功
					}
					DatabaseFacade.getODB().update(tvPayreckBankDto);
				}else if(MsgConstant.VOUCHER_NO_2302.equals(voucherInfo.getSvtcode())){
					String XPaySndBnkNo = voucherNode.selectSingleNode("XPaySndBnkNo").getText();
//					String XAddWord = voucherNode.selectSingleNode("XAddWord").getText();
					String XClearDate = voucherNode.selectSingleNode("XClearDate").getText();
					String XPayAmt = voucherNode.selectSingleNode("XPayAmt").getText();
					TvPayreckBankBackDto tvPayreckBankBackDto = new TvPayreckBankBackDto();
					tvPayreckBankBackDto.setIvousrlno(Long.valueOf(tvVoucherinfoDto.getSdealno()));
					list = CommonFacade.getODB().findRsByDto(tvPayreckBankBackDto);
					if(null == list || list.size() == 0){
						return ;
					}
					tvPayreckBankBackDto = (TvPayreckBankBackDto) list.get(0);
					tvPayreckBankBackDto.setSxcleardate(DateUtil.stringToDate(XClearDate));
					tvPayreckBankBackDto.setSxpayamt(StringUtils.isBlank(XPayAmt)? BigDecimal.ZERO :new BigDecimal(XPayAmt));
					tvPayreckBankBackDto.setSxpaysndbnkno(XPaySndBnkNo);
					tvPayreckBankBackDto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
					if(StringUtils.isNotBlank(XPayAmt) && tvPayreckBankBackDto.getSxpayamt().compareTo(BigDecimal.ZERO) != 0){
						result = "41";	//清算成功
					}
					DatabaseFacade.getODB().update(tvPayreckBankBackDto);
				}else if(MsgConstant.VOUCHER_NO_5207.equals(voucherInfo.getSvtcode())){
					String XAgentBusinessNo = voucherNode.selectSingleNode("XAgentBusinessNo").getText();
					String XPayDate = voucherNode.selectSingleNode("XPayDate").getText();
					String XpayAmt = voucherNode.selectSingleNode("XpayAmt").getText();	
					TvPayoutmsgmainDto tvPayoutmsgmainDto = new TvPayoutmsgmainDto();
					tvPayoutmsgmainDto.setSbizno(tvVoucherinfoDto.getSdealno());
					list = CommonFacade.getODB().findRsByDto(tvPayoutmsgmainDto);
					if(null == list || list.size() == 0){
						return ;
					}
					tvPayoutmsgmainDto = (TvPayoutmsgmainDto) list.get(0);
					tvPayoutmsgmainDto.setSxagentbusinessno(XAgentBusinessNo);
					tvPayoutmsgmainDto.setSxpayamt(StringUtils.isBlank(XpayAmt)? BigDecimal.ZERO :new BigDecimal(XpayAmt));
					tvPayoutmsgmainDto.setSxpaydate(XPayDate);
					if(StringUtils.isNotBlank(XpayAmt) && tvPayoutmsgmainDto.getSxpayamt().compareTo(BigDecimal.ZERO) != 0){
						result = "41";	//清算成功
					}
					DatabaseFacade.getODB().update(tvPayoutmsgmainDto);
				}else if(MsgConstant.VOUCHER_NO_5209.equals(voucherInfo.getSvtcode()) || MsgConstant.VOUCHER_NO_3209.equals(voucherInfo.getSvtcode())){
					String XAgentBusinessNo = voucherNode.selectSingleNode("XAgentBusinessNo").getText();
					String XPayDate = voucherNode.selectSingleNode("XPayDate").getText();
					String XpayAmt = voucherNode.selectSingleNode("XPayAmt").getText();
					TvDwbkDto tvDwbkDto = new TvDwbkDto();
					tvDwbkDto.setIvousrlno(Long.valueOf(tvVoucherinfoDto.getSdealno()));
					list = CommonFacade.getODB().findRsByDto(tvDwbkDto);
					if(null == list || list.size() == 0){
						return ;
					}
					tvDwbkDto = (TvDwbkDto) list.get(0);
					tvDwbkDto.setXagentbusinessno(XAgentBusinessNo);
					tvDwbkDto.setXpayamt(StringUtils.isBlank(XpayAmt)? BigDecimal.ZERO :new BigDecimal(XpayAmt));
					tvDwbkDto.setShold1(XPayDate);	//清算日期	暂时使用hold1
					if(StringUtils.isNotBlank(XpayAmt) && tvDwbkDto.getXpayamt().compareTo(BigDecimal.ZERO) != 0){
						result = "41";	//清算成功
					}
					DatabaseFacade.getODB().update(tvDwbkDto);
//					TvPayoutmsgmainDto tvPayoutmsgmainDto = new TvPayoutmsgmainDto();
//					tvPayoutmsgmainDto.setSbizno(tvVoucherinfoDto.getSdealno());
//					tvPayoutmsgmainDto = (TvPayoutmsgmainDto) CommonFacade.getODB().findRsByDto(tvPayoutmsgmainDto).get(0);
//					tvPayoutmsgmainDto.setSxagentbusinessno(XAgentBusinessNo);
//					tvPayoutmsgmainDto.setSxpayamt(StringUtils.isBlank(XpayAmt)? BigDecimal.ZERO :new BigDecimal(XpayAmt));
//					tvPayoutmsgmainDto.setSxpaydate(XPayDate);
//					DatabaseFacade.getODB().update(tvPayoutmsgmainDto);
				}else{
					return;
				}
				tvVoucherinfoDto.setSstatus(result);
				tvVoucherinfoDto.setSext1(result);
				if("41".equals(result)){
					tvVoucherinfoDto.setSdemo("清算成功");
				}else{
					tvVoucherinfoDto.setSdemo("清算失败");
				}
				DatabaseFacade.getODB().update(tvVoucherinfoDto);
			}catch (IndexOutOfBoundsException e) {
				error = e;
				logger.error("查询主表信息异常：与凭证：" + voucherInfo.getSvoucherno()
						+ "对应的主表记录不存在！   ");
				throw new ITFEBizException("查询主表信息异常：与凭证：" + voucherInfo.getSvoucherno()
						+ "对应的主表记录不存在！    errorCodeID:"
						+ System.currentTimeMillis(), e);
			}catch (DocumentException e) {
				error = e;
				logger.error("读取凭证回单失败！凭证编号：" + voucherInfo.getSvoucherno());
				throw new ITFEBizException("读取凭证回单失败！凭证编号：" + voucherInfo.getSvoucherno() , e);
			} catch (JAFDatabaseException e) {
				error = e;
				logger.error("读取凭证回单失败！凭证编号：" + voucherInfo.getSvoucherno());
				throw new ITFEBizException("读取凭证回单失败！凭证编号：" + voucherInfo.getSvoucherno(), e);
			} catch (Exception e) {
				error = e;
				logger.error("读取凭证回单失败！凭证编号：" + voucherInfo.getSvoucherno());
				throw new ITFEBizException("读取凭证回单失败！凭证编号：" + voucherInfo.getSvoucherno(), e);
			}finally{
				if(null != error){	//处理失败退回凭证
					VoucherService voucherService = new VoucherService();
					voucherService.confirmVoucherfail(null,voucherInfo.getSadmdivcode(), Integer.valueOf(voucherInfo.getSstyear()), voucherInfo.getSvtcode(),new String[] { voucherInfo.getSvoucherno() },new String[] {error.getMessage() });
				}
			}
			
		}
	
	/**
	 * 回填拨款清单中的明细信息
	 * @throws ITFEBizException 
	 */
	
	public static void fillPayoutDetailXinfo(IDto  mainDto,Node voucherNode) throws ITFEBizException{

			List subList = PublicSearchFacade
					.findSubDtoByMain(mainDto);
			if (subList == null || subList.size() == 0)
				throw new ITFEBizException(
						"根据业务主表记录查询子表明细信息出错！");
			HashMap subdtoMap = VoucherUtil
					.convertListToMap(subList);
			List<Node> detailList = voucherNode
					.selectSingleNode("DetailList")
					.selectNodes("Detail");
			for (Node detailNode : detailList) {
				String sDetailId = ((Element) detailNode)
						.elementText("Id");
				TvPayoutmsgsubDto subdto = (TvPayoutmsgsubDto) subdtoMap
						.get(sDetailId);
				if (subdto == null)
					throw new ITFEBizException("未找到和报文明细Id"
							+ sDetailId + "匹配的明细子表记录！");
				String XPayDate = subdto.getSxpaydate();
				TvPayoutmsgmainDto maindto = (TvPayoutmsgmainDto) mainDto;
				maindto = (TvPayoutmsgmainDto) mainDto;
				if (null==XPayDate) {
					XPayDate = maindto.getSxpaydate();
					if (null==XPayDate) {
						XPayDate = TimeFacade.getCurrentStringTime(); 
					}
				}
				String sxagentbusinessno = subdto.getSxagentbusinessno();
				if (null==sxagentbusinessno) {
					sxagentbusinessno="000000000";
				}
				String XPayAmt= subdto.getSxpayamt().toString();
				if (subdto.getSxpayamt().compareTo(BigDecimal.ZERO)==0 && null==subdto.getSstatus() && maindto.getSstatus().equals(DealCodeConstants.DEALCODE_ITFE_SUCCESS)) {
					//暂时默认成功
					XPayAmt = subdto.getNmoney().toString();
				}
				String XAddword = subdto.getSxaddword();
				if (detailNode.selectSingleNode("XPayDate") != null){
					detailNode.selectSingleNode("XPayDate")
							.setText(XPayDate + "");
				}
				if (detailNode
						.selectSingleNode("XAgentBusinessNo") != null){
					detailNode.selectSingleNode(
							"XAgentBusinessNo").setText(
							sxagentbusinessno + "");
				}
				if (detailNode.selectSingleNode("XAddWord") != null){
					detailNode.selectSingleNode("XAddWord")
							.setText(XAddword + "");
				}			
				if (detailNode.selectSingleNode("XPayAmt") != null){
						detailNode.selectSingleNode("XPayAmt")
								.setText(XPayAmt +"");
				}
				
			} 
	
			}
		
	
	
	}

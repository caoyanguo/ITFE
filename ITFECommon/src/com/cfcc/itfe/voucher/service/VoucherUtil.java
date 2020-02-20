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
	private static HashMap map;// ����ƾ֤����(��ɫͨ��)����

	/**
	 * ��װƾ֤ǩ�±���
	 * 
	 */
	public static String getVoucherStampXML(TvVoucherinfoDto dto)
			throws ITFEBizException {
		try {
			String stampXML = "";
			Document fxrDoc;// ��ʱ��ƾ֤����
			Document successDoc;// ���ظ�ƾ֤��ĳɹ�����
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
			// ѭ����ȡƾ֤���ģ���ȡ�ɹ���ƾ֤VoucherBody
			String s = FileUtil.getInstance().readFile(dto.getSfilename());
			/*
			 * ��ȡϵͳ��������ƾ֤���ģ���5207 5209 2301 2302 5106 5108�ⱨ�� VoucherFlag�ص���־��0
			 * ƾ֤�������ɱ��� 1 �ص�����
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

				VoucherFlag.setText("0");// �ص���־��0 ƾ֤�������ɱ��� 1 �ص�����
				stampXML = com.cfcc.itfe.util.FileUtil.getInstance().readFile(
						dto.getSfilename());
				Voucher.setText(base64Encode(stampXML));
				return successDoc.asXML().replaceAll("&lt;", "<").replaceAll(
						"&gt;", ">");
			}
			/*
			 * ��ȡ�ص�����
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
					 * �˴���ӻ����ֶ��� ���������ڣ�ʵ���ʽ𡢻������롢���������˿
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
							throw new ITFEBizException("�����������¼��ѯҵ�����Ϣ����");
						}
						String XPayDate = mainDto.getSxpaydate();
						// �㽭�ط�����Ӧ����������ǩ����ʾ���ı��۸��������ʱ��ȡϵͳ����
						if (StringUtils.isBlank(XPayDate)
								&& ITFECommonConstant.PUBLICPARAM
										.indexOf("singStampflag") >= 0)
							XPayDate = TimeFacade.getCurrentStringTime();
						String sxagentbusinessno = mainDto.getSdealno();// ԭ������ˮ��
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
						// ��ֹ�淶�д�Сд����
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
						//������ϸ������ϸ�����е�X�ֶ�
						if (StateConstant.COMMON_YES.equals(String.valueOf(tredto==null?"":tredto.getSpayunitname()))) {
							voucherNode.selectSingleNode("XPayAmt").setText(
									mainDto.getSxpayamt() + "");
							fillPayoutDetailXinfo(mainDto, voucherNode);	
						} else{
						/**
						 * ����ҵ��������Ҷ�Ӧ����������Ϣ(�Ϻ���ɫ) ���ڻص���¼��ϸ��Ϣ�е�
						 * XpayAmt-��XPayDate
						 * -֧�����ڡ�XAgentBusinessNo-���н�����ˮ�š�XAddWord-ʧ��ԭ��
						 * 
						 * @author �Ż��
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
										"����ҵ�������¼��ѯ�ӱ���ϸ��Ϣ����");
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
									throw new ITFEBizException("δ�ҵ��ͱ�����ϸId"
											+ sDetailId + "ƥ�����ϸ�ӱ��¼��");
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
							throw new ITFEBizException("�����������¼��ѯҵ�����Ϣ����");
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
							throw new ITFEBizException("�����������¼��ѯҵ�����Ϣ����");
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
						 * �Ϻ���ֽ����Ȩ֧���ص�����д֧���������кź͸���(֧����ʽ��0-ֱ�� 1-��Ȩ)
						 * 
						 * @author �Ż�� ��XPaySndBnkNo֧���������кš� ��д3143��֧���������к�
						 *         ��XAddWord���ԡ� ��д3143��֧���������+���ı��+֧��ί������
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
							throw new ITFEBizException("�����������¼��ѯҵ�����Ϣ����");
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
						 * �Ϻ���ֽ����Ȩ֧���˿�ص�����д֧���������кź͸���(֧����ʽ��0-ֱ�� 1-��Ȩ)
						 * 
						 * @author �Ż�� ��XPaySndBnkNo֧���������кš� ��д3144����д���������к� �� *
						 *         ��XAddWord���ԡ� ��д ԭ������ˮ�š�
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
							throw new ITFEBizException("�����������¼��ѯҵ�����Ϣ����");
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
						// ��ֹ�淶�д�Сд����
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
							throw new ITFEBizException("�����������¼��ѯҵ�����Ϣ����");
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
							throw new ITFEBizException("�����������¼��ѯҵ�����Ϣ����");
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
							throw new ITFEBizException("�����������¼��ѯҵ�����Ϣ����");
						}
						/**
						 * ����ҵ��������Ҷ�Ӧ����������Ϣ(�Ϻ���ɫ) ���ڻص���¼��ϸ��Ϣ�е� XDealResult-������
						 * XAddWord-����
						 * 
						 * @author �Ż��
						 */
						List subList = PublicSearchFacade
								.findSubDtoByMain(mainDto);
						if (subList == null || subList.size() == 0)
							throw new ITFEBizException("����ҵ�������¼��ѯ�ӱ���ϸ��Ϣ����");
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
								throw new ITFEBizException("δ�ҵ��ͱ�����ϸId"
										+ sDetailId + "ƥ�����ϸ�ӱ��¼��");
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
													: "0.00");// ʵ��֧����֧��ʧ����0
							voucherNode
									.selectSingleNode("XPayeeAcctBankName")
									.setText(mainDto.getSpayacctbankname() + "");
							voucherNode.selectSingleNode("XPayeeAcctNo")
									.setText(mainDto.getSpayeeacctno() + "");
							voucherNode.selectSingleNode("XPayeeAcctName")
									.setText(mainDto.getSpayeeacctname() + "");

						} else {
							throw new ITFEBizException("�����������¼��ѯҵ�����Ϣ����");
						}
						List subList = PublicSearchFacade
								.findSubDtoByMain(mainDto);
						if (subList == null || subList.size() == 0)
							throw new ITFEBizException("����ҵ�������¼��ѯ�ӱ���ϸ��Ϣ����");
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
								throw new ITFEBizException("δ�ҵ��ͱ�����ϸId"
										+ sDetailId + "ƥ�����ϸ�ӱ��¼��");
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
						VoucherFlag.setText("0");// �ص���־��0 ƾ֤�������ɱ��� 1 �ص�����
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
					logger.debug("ƾ֤ǩ��д���δ���뱨�ģ�" + newStr);
					Voucher.setText(VoucherUtil.base64Encode(newStr));
				}
			}
			stampXML = successDoc.asXML();
			logger.debug("ƾ֤ǩ��д��ı��ģ�" + stampXML);
			dto.setSadmdivcode(dto.getSadmdivcode().replace(
					MsgConstant.VOUCHERSAMP_ATTACH, ""));
			return stampXML;
		} catch (ITFEBizException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("��װƾ֤ǩ�±��ĳ����쳣��", e);
		}

	}

	/**
	 * ��װǩ��λ�ñ���
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
			logger.debug("ƾ֤ǩ��д���ǩ��λ�ñ��ģ�" + stampPotisionXML);
			return stampPotisionXML;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("��װǩ��λ�ñ��ĳ����쳣��", e);
		}

	}

	/**
	 * ��װǩ��λ�ñ���
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
			throw new ITFEBizException("����ƾ֤���ͣ�" + dto.getSvtcode()
					+ " ǩ��λ���쳣��", e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("����ƾ֤���ͣ�" + dto.getSvtcode()
					+ " ǩ��λ���쳣��", e);
		}

		if (list == null || list.size() == 0) {
			throw new ITFEBizException("���⣺" + dto.getStrecode() + " ƾ֤���ͣ�"
					+ dto.getSvtcode() + " ǩ��λ���� [ǩ��λ�ò���ά��] ��δά����");
		}
		try {
			// ��ȡ֤���������е�֤��ID
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
			logger.debug("ƾ֤ǩ��д���ǩ��λ�ñ��ģ�" + stampPotisionXML);
			HashMap<String, String> retmap = new HashMap<String, String>();
			retmap.put("signposxml", stampPotisionXML);
			retmap.put("signcertid", _dto.getScertid());
			return retmap;

		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("��װǩ��λ�ñ��ĳ����쳣��", e);
		}
	}

	/**
	 * ��base64������ַ���������Դ�ַ��� gbk
	 * 
	 * @param src
	 *            base64������ַ���
	 * @return Դ�ַ���
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
	 * ��������ƾ֤���ȡ���Ļ�ȡ����ƾ֤�ܽ��
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
	 * ��������ƾ֤���ȡ���Ļ�ȡ��Ʊ��λ
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
				throw new ITFEBizException("ҵ�����ͱ��벻��Ϊ�ա�");
			Spaybankcode = voucherNode.selectSingleNode("PayeeAcctBankNo")
					.getText();
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5207)
				&& ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
			String BusinessTypeCode = voucherNode.selectSingleNode(
					"BusinessTypeCode").getText();
			if (StringUtils.isBlank(BusinessTypeCode))
				throw new ITFEBizException("ҵ�����ͱ��벻��Ϊ�ա�");
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
				throw new ITFEBizException("��ȡ�������д���ʧ�ܣ�");
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
	 * �������д���ȡ���б����
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
					throw new ITFEBizException("���⣺" + trecode + " �����кţ�"
							+ billorgcode + " �� [ ����������Ϣά������ ] ��δά����");
				}
				bankType = tsConvertbanktypeDto.getSbanktype();
				if (bankType == null || "".equals(bankType)) {
					throw new ITFEBizException("[����������Ϣά������]��û��ά�������к�["
							+ billorgcode + "]���б���룡");
				}
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("���ݴ�������ȡ���б��������쳣��");
		}
		return bankType;
	}

	/**
	 * ����ƾ֤������Ϣ(�Ϻ���ɫ) ��ӹ���ƾ֤���͡�����ƾ֤��š�ҵ�����ͱ��
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
	 * ǩ��ʧ�ܣ�������ƾ֤״̬
	 * 
	 * @param dto
	 * @param errMsg
	 */
	public static void confirmVoucherfail(TvVoucherinfoDto dto, String errMsg) {
		try {
			errMsg = errMsg == null ? "���Ĳ��淶" : errMsg;
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
	 * ǩ�ճɹ���������ƾ֤״̬
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
			throw new ITFEBizException("ƾ֤��" + vDto.getSvoucherno()
					+ " ǩ�ճɹ��쳣��");
		}
		if (!StringUtils.isBlank(errMsg))
			throw new ITFEBizException("ƾ֤��" + vDto.getSvoucherno()
					+ " ǩ�ճɹ��쳣��");
	}

	/**
	 * ����ҵ�����3503 3504 3505 3506�ص�����
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
		// У������ƾ֤�Ƿ����
		try {
			list = CommonFacade.getODB().findRsByDto(voucherDto);
			if (list == null || list.size() == 0) {
				confirmVoucherfail(dto, "����ƾ֤" + dto.getSvoucherno() + "������");
				return flag;
			} else if (list.size() > 1) {
				confirmVoucherfail(dto, "����ƾ֤" + dto.getSvoucherno() + "���ڶ���");
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
		// У��ƾ֤�Ƿ��ѷ��Ͷ���ƾ֤��
		voucherDto = (TvVoucherinfoDto) list.get(0);
		if (!voucherDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS)) {
			confirmVoucherfail(dto, "����ƾ֤" + dto.getSvoucherno()
					+ "δ���͵���ƾ֤�⣬���ܽ��ջص�����");
			return flag;
		}
		try {
			// У��ƾ֤�����Ƿ�淶
			String XCheckResult = voucherNode.selectSingleNode("XCheckResult")
					.getText();
			String XCheckReason = voucherNode.selectSingleNode("XCheckReason")
					.getText();
			if (StringUtils.isBlank(XCheckResult)
					|| (!XCheckResult.equals("0") && !XCheckResult.equals("1"))) {
				confirmVoucherfail(dto, "���˽���ڵ�XCheckResult��ֵ������0��1");
				return flag;
			}
			// ǩ�ճɹ�
			voucherConfirmSuccess(dto);
			// ����ƾ֤�ص����˽��
			voucherDto
					.setSstatus(DealCodeConstants.VOUCHER_RETURN_SUCCESS_ALREADY);
			if (XCheckResult.equals("0"))
				voucherDto.setSdemo("���˳ɹ�");
			else
				voucherDto.setSdemo(StringUtils.isBlank(XCheckReason) ? "����ʧ��"
						: ("����ʧ�ܣ�" + XCheckReason));
			updateVoucherState(voucherDto);
		} catch (Exception e) {
			logger.error(e);
			confirmVoucherfail(dto, e.getMessage());
		}
		return flag;
	}

	/**
	 * ����ƾ֤����5502 2502����
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
			// ���˵�������Ϣд��ƾ֤
			updateVoucherState(dto);
			// У�鱨���ܱ�������ϸ�ܱ����Ƿ�һ��
			if (voucherVerify(AllNum, listDetail.size() + "", dto))
				return;
			// У��ƾ֤���Ķ��˱��������б����Ƿ�һ��
			if (voucherVerify(dto, AllNum))
				return;
			BigDecimal Total = new BigDecimal("0.00");// ��ϸ�ۼ��ܽ��
			List list = new ArrayList();
			VoucherVerify voucherVerify = new VoucherVerify();
			for (Element elementDetail : (List<Element>) listDetail) {
				// String Id=elementDetail.elementText("Id");
				String VoucherNo = elementDetail.elementText("VoucherNo");// ƾ֤��
				String Amt = elementDetail.elementText("Amt");// ���
				String ProState = elementDetail.elementText("ProState");// ƾ֤״̬
				// 0-����
				// 1-�˻�
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
					verifydto.setFinorgcode("0000000000");// Ĭ�ϲ���У��dto��������
				if (svtcode.equals(MsgConstant.VOUCHER_NO_2502))
					verifydto.setPaybankno(dto.getSpaybankcode());
				// У�鱨�Ĺؼ��ֶθ�ʽ
				String returnmsg = voucherVerify.checkValid(verifydto, svtcode);
				String ProStateMsg = (StringUtils.isBlank(ProState) ? "ƾ֤״̬�ֶ�ProStateֵ����Ϊ�ա�"
						: !ProState.equals("0") && !ProState.equals("1") ? "ƾ֤״̬�ֶ�ProStateֵ������0��1��"
								: "");
				returnmsg = returnmsg == null ? ProStateMsg : returnmsg
						+ ProStateMsg;
				if (StringUtils.isNotBlank(returnmsg)) {
					VoucherFactory.voucherComfail(dto.getSdealno(), returnmsg);
					return;
				}
				verifydto.setOfyear(ProState);// ƾ֤״̬ 0-���� 1-�˻�
				list.add(verifydto);
			}
			// У�鱨���ܽ������ϸ�ۼ��ܽ���Ƿ�һ��
			if (voucherVerify(MtoCodeTrans.transformBigDecimal(AllAmt), Total,
					dto))
				return;
			// У��ƾ֤���ĸ�ʽ�� У��ƾ֤���ںͶ��������Ƿ���ڵ�ǰ��������ʱ У�������룬��������
			if (voucherVerify(dto))
				return;
			// ǩ�ճɹ�
			VoucherFactory.voucherConfirmSuccess(dto.getSdealno());
			// ����ƾ֤����5502 2502������ϸ��Ϣ
			voucherDealMsgXML(list, dto);
		} catch (Exception e) {
			logger.error(e);
			VoucherFactory.voucherComfail(dto.getSdealno(), e.getMessage());
		}
	}

	/**
	 * ����ƾ֤����5502 2502������ϸ��Ϣ
	 * 
	 * @param lists
	 * @param dto
	 * @throws ITFEBizException
	 */
	public static void voucherDealMsgXML(List lists, TvVoucherinfoDto dto)
			throws ITFEBizException {
		dto.setSdemo(null);
		for (VoucherVerifyDto verifydto : (List<VoucherVerifyDto>) lists) {
			String VoucherNo = verifydto.getVoucherno();// ƾ֤��
			String Amt = verifydto.getFamt();// ���
			String ProState = verifydto.getOfyear();// ƾ֤״̬ 0-���� 1-�˻�
			List<TvVoucherinfoDto> list = findVoucherDto(VoucherNo, dto);
			dto.setSstatus(DealCodeConstants.VOUCHER_CHECKACCOUNT_FAIL);
			if (list == null || list.size() == 0)
				dto.setSdemo(dto.getSdemo() == null ? "ƾ֤" + VoucherNo + "������"
						: dto.getSdemo() + "��" + "ƾ֤" + VoucherNo + "������");
			else if (list.size() > 1)
				dto.setSdemo(dto.getSdemo() == null ? "ƾ֤" + VoucherNo
						+ "���ݿ����" + list.size() + "��" : dto.getSdemo() + "��"
						+ "ƾ֤" + VoucherNo + "���ݿ����" + list.size() + "��");
			else {
				TvVoucherinfoDto vDto = list.get(0);
				if (!((vDto.getSstatus().equals(
						DealCodeConstants.VOUCHER_SUCCESS) && ProState
						.equals(StateConstant.VOUCHE_PROSTATE_NORMAL)) || (vDto
						.getSstatus().equals(DealCodeConstants.VOUCHER_FAIL) && ProState
						.equals(StateConstant.VOUCHE_PROSTATE_RETURN)))
						&& vDto.getNmoney().compareTo(
								MtoCodeTrans.transformBigDecimal(Amt)) == 0) {
					dto.setSdemo(dto.getSdemo() == null ? "ƾ֤" + VoucherNo
							+ "״̬����" : dto.getSdemo() + "��" + "ƾ֤" + VoucherNo
							+ "״̬����");
					vDto.setSreturnerrmsg("����ʧ��(״̬����)");
				} else if (!((vDto.getSstatus().equals(
						DealCodeConstants.VOUCHER_SUCCESS) && ProState
						.equals(StateConstant.VOUCHE_PROSTATE_NORMAL)) || (vDto
						.getSstatus().equals(DealCodeConstants.VOUCHER_FAIL) && ProState
						.equals(StateConstant.VOUCHE_PROSTATE_RETURN)))
						&& vDto.getNmoney().compareTo(
								MtoCodeTrans.transformBigDecimal(Amt)) != 0) {
					dto.setSdemo(dto.getSdemo() == null ? "ƾ֤" + VoucherNo
							+ "״̬����" : dto.getSdemo() + "��" + "ƾ֤"
							+ VoucherNo + "״̬����");
					vDto.setSreturnerrmsg("����ʧ��(״̬����)");
				} else if (((vDto.getSstatus().equals(
						DealCodeConstants.VOUCHER_SUCCESS) && ProState
						.equals(StateConstant.VOUCHE_PROSTATE_NORMAL)) || (vDto
						.getSstatus().equals(DealCodeConstants.VOUCHER_FAIL) && ProState
						.equals(StateConstant.VOUCHE_PROSTATE_RETURN)))
						&& vDto.getNmoney().compareTo(
								MtoCodeTrans.transformBigDecimal(Amt)) != 0) {
					dto.setSdemo(dto.getSdemo() == null ? "ƾ֤" + VoucherNo
							+ "����" : dto.getSdemo() + "��" + "ƾ֤" + VoucherNo
							+ "����");
					vDto.setSreturnerrmsg("����ʧ��(����)");
				} else
					vDto.setSreturnerrmsg("���˳ɹ�");
				updateVoucherState(vDto);
			}
		}
		if (dto.getSdemo() == null) {
			dto.setSstatus(DealCodeConstants.VOUCHER_CHECKACCOUNT_SUCCESS);
			dto.setSdemo("���˳ɹ�");
		}
		dto.setSdemo(dto.getSdemo().length() > 1024 ? dto.getSdemo().substring(
				0, 1024) : dto.getSdemo());
		updateVoucherState(dto);
	}

	/**
	 * ����ƾ֤���롢�������ڲ���ƾ֤
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
	 * ���ݹ�����롢ƾ֤���͡�ƾ֤��š�ƾ֤״̬���ұȶ�ƾ֤
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
		vDto.setScheckvouchertype(dto.getSvtcode());// �ȶ�ƾ֤����
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
				throw new ITFEBizException("�������[" + dto.getStrecode()
						+ "]ƾ֤����[" + vDto.getSvtcode() + "]ƾ֤���["
						+ vDto.getSvoucherno() + "]״̬Ϊ [У����]��ƾ֤���ݿ����"
						+ list.size() + "��");
			return (TvVoucherinfoDto) list.get(0);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("����ƾ֤��Ϣ�쳣��", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("����ƾ֤��Ϣ�쳣��", e);
		}
	}

	/**
	 * ���ݹ�����롢�ȶ�ƾ֤���͡�ƾ֤��š�ƾ֤״̬����ԭƾ֤
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
		vDto.setScheckvouchertype(dto.getScheckvouchertype());// �ȶ�ƾ֤����
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
			throw new ITFEBizException("����ƾ֤��Ϣ�쳣��", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("����ƾ֤��Ϣ�쳣��", e);
		}
	}

	/**
	 * ���ݹ�����롢ƾ֤���͡�ƾ֤��š�ƾ֤״̬����ƾ֤
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
			throw new ITFEBizException("����ƾ֤��Ϣ�쳣��", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("����ƾ֤��Ϣ�쳣��", e);
		}
	}

	/**
	 * ���ݲ����ˮ�š�ί�����ڲ��Ҳ��ƾ֤
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
			logger.error("���Ҳ��ƾ֤��Ϣ�쳣��", e);
			VoucherException.saveErrInfo(null, "���Ҳ��ƾ֤��Ϣ�쳣��");
		} catch (JAFDatabaseException e) {
			logger.error("���Ҳ��ƾ֤��Ϣ�쳣��", e);
			VoucherException.saveErrInfo(null, "���Ҳ��ƾ֤��Ϣ�쳣��");
		}
		return dto;
	}

	/**
	 * ������ͬƾ֤���ͺ�ƾ֤����µ����в��ƾ֤
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
			logger.error("���Ҳ��ƾ֤��Ϣ�쳣��", e);
			VoucherException.saveErrInfo(dto.getSvtcode(), e);
		} catch (ValidateException e) {
			logger.error("���Ҳ��ƾ֤��Ϣ�쳣��", e);
			VoucherException.saveErrInfo(dto.getSvtcode(), e);
		}
		return null;
	}

	/**
	 * ����ƾ֤���������ҵ��������Ϣ
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public static List findMainDtoByVoucher(TvVoucherinfoDto dto)
			throws ITFEBizException {
		IDto maindto = null;
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {// ֱ��֧�����5108
			maindto = new TvDirectpaymsgmainDto();
			((TvDirectpaymsgmainDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)) {// ֱ��֧��5201
			maindto = new TfDirectpaymsgmainDto();
			((TfDirectpaymsgmainDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {// ��Ȩ֧�����5106
			maindto = new TvGrantpaymsgmainDto();
			((TvGrantpaymsgmainDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267)) {// ʵ������ƾ֤5207
			maindto = new TvPayoutmsgmainDto();
			((TvPayoutmsgmainDto) maindto).setSbizno(dto.getSdealno());
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)) {// ��������ҵ��֧����ϸ8207
			maindto = new TfPaymentDetailsmainDto();
			((TfPaymentDetailsmainDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)) {// ���뻮��ƾ֤�ص�2301
			maindto = new TvPayreckBankDto();
			((TvPayreckBankDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)) {// �����˿�ƾ֤�ص�2302
			maindto = new TvPayreckBankBackDto();
			((TvPayreckBankBackDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253)) {// ����ֱ��֧������ƾ֤5253
			maindto = new TfDirectpayAdjustmainDto();
			((TfDirectpayAdjustmainDto) maindto).setIvousrlno(Long
					.parseLong(dto.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)) {// ������Ȩ֧������ƾ֤5351����
			maindto = new TfGrantpayAdjustmainDto();
			((TfGrantpayAdjustmainDto) maindto).setIvousrlno(Long.parseLong(dto
					.getSdealno()));
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3208)) { // ʵ���ʽ��˿�
			maindto = new TvPayoutbackmsgMainDto();
			((TvPayoutbackmsgMainDto) maindto).setSbizno(dto.getSdealno());
		} else
			throw new ITFEBizException("û�ж����ҵ������!");
		try {
			return CommonFacade.getODB().findRsByDto(maindto);
		} catch (JAFDatabaseException e) {
			logger.error("����ƾ֤���������ҵ��������Ϣ�쳣!", e);
			throw new ITFEBizException("����ƾ֤���������ҵ��������Ϣ�쳣!", e);
		} catch (ValidateException e) {
			logger.error("����ƾ֤���������ҵ��������Ϣ�쳣!", e);
			throw new ITFEBizException("����ƾ֤���������ҵ��������Ϣ�쳣!", e);
		}
	}

	/**
	 * У��ƾ֤���Ķ��˱��������б����Ƿ�һ��
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
			str = "��������";
		} else
			str = "����";
		boolean flag = true;
		List<TvVoucherinfoDto> list = CommonFacade.getODB()
				.findRsByWhereForDto(
						vDto,
						" S_STATUS IN ('" + DealCodeConstants.VOUCHER_SUCCESS
								+ "' ,'" + DealCodeConstants.VOUCHER_FAIL
								+ "')");
		if (list == null || list.size() == 0) {
			dto.setSstatus(DealCodeConstants.VOUCHER_CHECKACCOUNT_FAIL);
			dto.setSdemo("���б�����0" + " " + str + "������" + dto.getIcount());
		} else {
			if (Integer.parseInt(AllNum) != list.size()) {
				dto.setSstatus(DealCodeConstants.VOUCHER_CHECKACCOUNT_FAIL);
				dto.setSdemo((dto.getSdemo() == null ? "" : dto.getSdemo())
						+ "�� ���б�����" + list.size() + " " + str + "������"
						+ dto.getIcount());
			} else
				return false;
		}
		if (flag)
			VoucherFactory.voucherComfail(dto.getSdealno(), dto.getSdemo());
		return flag;
	}

	/**
	 * У�鱨���ܱ�������ϸ�ۼ��ܱ����Ƿ�һ��
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
			dto.setSdemo("�����ܱ�������ϸ�ۼ��ܱ�����һ��");
			VoucherFactory.voucherComfail(dto.getSdealno(), dto.getSdemo());
			return true;
		}
		return false;
	}

	/**
	 * У�鱨���ܽ������ϸ�ۼ��ܽ���Ƿ�һ��
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
			dto.setSdemo("�����ܽ������ϸ�ۼ��ܽ�һ��");
			VoucherFactory.voucherComfail(dto.getSdealno(), dto.getSdemo());
			return true;
		}
		return false;
	}

	/**
	 * У��ƾ֤���ĸ�ʽ�� У��ƾ֤���ںͶ��������Ƿ���ڵ�ǰ��������ʱ У�������룬��������
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
			dto.setSdemo("ƾ֤����:" + dto.getSstampuser() + " ���ڽ������ڣ�"
					+ TimeFacade.getCurrentStringTime());
		else if (Integer.parseInt(dto.getScheckdate()) > Integer
				.parseInt(TimeFacade.getCurrentStringTime()))
			dto.setSdemo("��������:" + dto.getScheckdate() + " ���ڽ������ڣ�"
					+ TimeFacade.getCurrentStringTime());
		else if ((dto.getIcount() + "").length() > 8)
			dto.setSdemo("ƾ֤������" + dto.getIcount() + "����8λ");
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
	 * ���ַ�������base64����
	 * 
	 * @param src�ַ���
	 * @return base64�ַ���
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
	 * ����ƾ֤���ͻ�ȡҵ������
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
	 * ͬ������ӿڣ�����ҵ�����ͻ�ȡ����ƾ֤����
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
	 * ƾ֤�б������������ְ�
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
			throw new ITFEBizException("ƾ֤�б������������ְ������쳣��", e);
		}

		return lists;
	}

	/**
	 * ƾ֤�б���ݿ������кŷְ�
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
			throw new ITFEBizException("ʵ���ʽ���ݿ������кŷְ������쳣��", e);
		}

		return lists;
	}

	public static List getListBySagentbnkcode(List<TvPayreckBankDto> list)
			throws ITFEBizException {
		Map map = new HashMap();
		List<List> lists = new ArrayList<List>();
		try {
			// ���մ������к�+֧����ʽ���зְ�
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
			throw new ITFEBizException("��ҵ���м���֧����������������ݴ��������кŷְ������쳣��", e);
		}
		return lists;
	}

	public static List getListBySagentbnkcodes(List<TvPayreckBankBackDto> list)
			throws ITFEBizException {
		Map map = new HashMap();
		List<List> lists = new ArrayList<List>();
		try {
			// ���մ������к�+֧����ʽ���зְ�
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
			throw new ITFEBizException("��ҵ���а���֧���˿������Ϣ����ݴ��������кŷְ������쳣��", e);
		}
		return lists;
	}

	/**
	 * ƾ֤�б�������ջ�������ְ�(�����˸�ƾ֤)
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
			throw new ITFEBizException("�����˸��������ջ�������ְ������쳣��", e);
		}

		return lists;
	}

	/**
	 * ֱ��֧��5201���ݴ������кŷְ�
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
			new ITFEBizException("��ѯ��������ϢΪ�գ�");
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
			throw new ITFEBizException("ֱ��֧�����ݿ������кŷְ������쳣��", e);
		}
		return lists;
	}

	/**
	 * �տ������˿�֪ͨ2252����֧���������кŷְ�
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
			new ITFEBizException("��ѯ��������ϢΪ�գ�");
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
			throw new ITFEBizException("ֱ��֧�����ݿ������кŷְ������쳣��", e);
		}
		return lists;
	}
	/**
	 * �����ְ�
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
			throw new ITFEBizException("��ѯ��������ϢΪ�գ�");
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
			throw new ITFEBizException("����ģ��ְ������쳣��", e);
		}
		return lists;
	}
	/**
	 * ��˰����ְ�
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
			throw new ITFEBizException("��ѯ��������ϢΪ�գ�");
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
			throw new ITFEBizException("����ģ��ְ������쳣��", e);
		}
		return lists;
	}
	/**
	 * ��ѯƾ֤״̬
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
			sdemo = "�鿴ƾ֤״̬�쳣��δ�ҵ���Ӧƾ֤         "
					+ (description.indexOf("δ�ҵ���Ӧƾ֤") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("0")) {
			sdemo = "���͵�״̬:�Է�δ����     "
					+ (description.indexOf("�Է�δ����") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("1")) {
			sdemo = "���͵�״̬:�Է����ճɹ�     "
					+ (description.indexOf("�Է����ճɹ�") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("2")) {
			sdemo = "���͵�״̬:�Է�����ʧ��     "
					+ (description.indexOf("�Է�����ʧ��") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("3")) {
			sdemo = "���͵�״̬:�Է�ǩ�ճɹ�     "
					+ (description.indexOf("�Է�ǩ�ճɹ�") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("4")) {
			sdemo = "���͵�״̬:�Է�ǩ��ʧ��     "
					+ (description.indexOf("�Է�ǩ��ʧ��") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("5")) {
			sdemo = "���͵�״̬:�Է����˻�     "
					+ (description.indexOf("�Է����˻�") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("6")) {
			sdemo = "�ص�״̬:�Է�δ����     "
					+ (description.indexOf("�Է�δ����") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("7")) {
			sdemo = "�ص�״̬:�Է����ճɹ�     "
					+ (description.indexOf("�Է����ճɹ�") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("8")) {
			sdemo = "�ص�״̬:�Է�����ʧ��     "
					+ (description.indexOf("�Է�����ʧ��") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("9")) {
			sdemo = "�ص�״̬:�Է�ǩ�ճɹ�     "
					+ (description.indexOf("�Է�ǩ�ճɹ�") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("10")) {
			sdemo = "�ص�״̬:�Է�ǩ��ʧ��     "
					+ (description.indexOf("�Է�ǩ��ʧ��") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("11")) {
			sdemo = "�ص�״̬:�Է����˻�     "
					+ (description.indexOf("�Է����˻�") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("12")) {
			sdemo = "���͵�״̬:���յ��Է��ص�     "
					+ (description.indexOf("���յ��Է��ص�") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("13")) {
			sdemo = "���͵�״̬:����δ����     "
					+ (description.indexOf("����δ����") > -1 ? "" : description);
		} else if ((objs[0] + "").equals("14")) {
			sdemo = "�ص�״̬:����δ����     "
					+ (description.indexOf("����δ����") > -1 ? "" : description);
		} else {
			sdemo = objs[0] + "";
		}
		return sdemo;
	}

	/**
	 * �����б������ҽ��ջ�������
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
					.getSpaybankcode());// �б����
		}
		// ���ϵ�˰��ɫ
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
	 * ����������Ϣ����ƾ֤�б�
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
			throw new ITFEBizException("û�ж����ҵ������!");

		String ls_SQL = ls_SQLBf.toString();
		ls_SQL = ls_SQL.substring(0, ls_SQL.length() - 2);
		List<TvVoucherinfoDto> voucherList = null;
		try {
			voucherList = DatabaseFacade.getDb().find(TvVoucherinfoDto.class,
					ls_SQL, params);
		} catch (JAFDatabaseException e) {
			logger.error("����������Ϣ����ƾ֤�б���� " + e);
			throw new ITFEBizException("����������Ϣ����ƾ֤�б��쳣��", e);
		}
		return voucherList;
	}

	// ��������ƾ֤״̬
	public static int updateVoucherState(List<TvVoucherinfoDto> list)
			throws ITFEBizException {
		TvVoucherinfoDto[] dtos = new TvVoucherinfoDto[list.size()];
		dtos = list.toArray(dtos);
		try {
			DatabaseFacade.getDb().update(dtos);
		} catch (JAFDatabaseException e) {
			logger.error("����ƾ֤��Ϣʱ�����쳣��" + e);
			throw new ITFEBizException("����ƾ֤��Ϣʱ�����쳣��", e);
		}
		return list.size();
	}

	/**
	 * ����ƾ֤״̬
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
			logger.error("����ƾ֤��Ϣʱ�����쳣��" + e);
			throw new ITFEBizException("����ƾ֤��Ϣʱ�����쳣��", e);
		}
		return list.size();
	}

	/**
	 * ����ƾ֤����TIPS
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
			throw new ITFEBizException("����ƾ֤����TIPS�����쳣��", e1);
		}
	}

	/**
	 * ƾ֤javaת��xml�������
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
			// �Ϻ�ʵ���ʽ��˿�ģ���������ط���һ��
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
			throw new ITFEBizException("����MQ��̨���������쳣��", e);
		}
	}

	/**
	 * ƾ֤���Ľ���
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
		message.setProperty("finOrgCode", ls_FinOrgCode);// ��������
		message.setProperty("dealnos", dealnos);
		message.setPayload(Xml);
		message = client.send("vm://VoucherLoadMsg", message);

	}

	/**
	 * У��ɹ������״̬
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
				logger.error(e + "��ѯ������Ϣʧ�ܣ�");
			}
			
			// ����ƾ֤������
			if (vDto.getSvtcode().equalsIgnoreCase(MsgConstant.VOUCHER_NO_5257)) {
				// ʵ���嵥У��ͨ�����ɸ���Ϊ����ɹ�
				vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				vDto.setSdemo("����ɹ�");
			} else if (vDto.getSvtcode().equalsIgnoreCase(MsgConstant.VOUCHER_NO_5671)
					||(vDto.getSvtcode().equalsIgnoreCase(MsgConstant.VOUCHER_NO_5408)&&!streDtoList.get(0).getStreattrib().equals("2"))) {//5408�Ǵ���ⲹ¼�ʽ�������ˮ��
				// ��˰�嵥У��ͨ����ΪУ���У���¼�ʽ�������ˮ�ź��ΪУ��ɹ�
				vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT);
				vDto.setSdemo("У����,�ȴ���¼�ʽ�������ˮ��");
			}else {
				vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				vDto.setSdemo("У��ɹ�");
			}
			DatabaseFacade.getDb().update(vDto);
			logger.debug("===================== У��ɹ�========================");
		} else {
			vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_FAIL);
			vDto.setSdemo("ƾ֤���:[" + vDto.getSvoucherno() + "]" + errMsg);
			DatabaseFacade.getDb().update(vDto);
			if(vDto.getSvtcode().equalsIgnoreCase(MsgConstant.VOUCHER_NO_5408))
			{
				TvNontaxmainDto searchdto = new TvNontaxmainDto();
				searchdto.setSdealno(vDto.getSdealno());
				searchdto = (TvNontaxmainDto)DatabaseFacade.getDb().find(searchdto);
				searchdto.setSstatus("");
				DatabaseFacade.getDb().update(searchdto);
			}
			logger.debug("===================== У��ʧ��========================");
		}
	}

	/**
	 * ҵ��Ҫ��ȫ��У��ɹ������״̬Ϊ"У����"
	 * 
	 * @param vDto
	 * @param OriginalVtCodeƾ֤����
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static void voucherVerifyUpdateStatus(TvVoucherinfoDto vDto,
			String OriginalVtCode) throws JAFDatabaseException,
			ITFEBizException {
		// ����ƾ֤������
		vDto.setScheckvouchertype(OriginalVtCode);// �ȶ�ƾ֤����
		vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT);
		vDto.setSdemo("�ȴ�[" + OriginalVtCode + "]ƾ֤����ҵ��Ҫ�ع���");
		DatabaseFacade.getDb().update(vDto);
	}

	/**
	 * У��ɹ������״̬(����������ɫҵ��---��ȡ����)
	 * 
	 * @author sunyan
	 * @throws ITFEBizException
	 * @throws ITFEBizException
	 */
	public static void voucherVerifyUpdateStatusSX(TvVoucherinfoSxDto vDto,
			String tablename, String errMsg, boolean flag)
			throws JAFDatabaseException, ITFEBizException {
		if (flag) {
			// ����ƾ֤������
			vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
			vDto.setSdemo("У��ɹ�");
			DatabaseFacade.getDb().update(vDto);
			logger.debug("===================== У��ɹ�========================");
		} else {
			vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_FAIL);
			vDto.setSdemo(errMsg);
			DatabaseFacade.getDb().update(vDto);
			logger.debug("===================== У��ʧ��========================");
		}
	}

	/**
	 * �������¶�ȴ���ɹ������ҵ���״̬ flag��true������ɹ���false������ʧ��
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
		// �������Ȩ֧����ȣ�Ҫ������ϸ��״̬
		String ls_UpdateSub = "";
		if (MsgConstant.VOUCHER_NO_5106.equals(vDto.getSvtcode())) {
			ls_UpdateSub = "UPDATE TV_GRANTPAYMSGSUB SET S_STATUS = ? where I_VOUSRLNO = ? ";
		}
		updateExec = DatabaseFacade.getDb().getSqlExecutorFactory()
				.getSQLExecutor();
		if (flag) {
			// ����ƾ֤������
			vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
			vDto.setSdemo("����ɹ�");
			// ����ҵ���
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
			updateExec.addParam(Long.valueOf(vDto.getSdealno()));
			logger.debug("===================== ����ɹ�========================");
		} else {
			vDto.setSstatus(DealCodeConstants.VOUCHER_FAIL_TCBS);
			updateExec.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);
			updateExec.addParam(vDto.getSdealno());
			logger.debug("===================== ����ʧ��========================");
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
	 * ��ȡ��Ȩ֧�����ҵ�����ˮ��
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
	 * ��ȡ��ϸ��ˮ��
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
	 * list,value = DealNos ������Ȩ֧����ȸ��ݽ�����ˮ�Ÿ���������״̬
	 */
	public static Boolean updateVoucherStatus(List<String> list, String status,
			String optionType) {
		if (null != list && list.size() > 0) {
			String tableName = "TV_GRANTPAYMSGMAIN";
			String ls_Demo = "";
			if (DealCodeConstants.DEALCODE_ITFE_FAIL.equals(status)) {
				ls_Demo = "����ʧ��";
			} else if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(status)) {
				ls_Demo = "����ɹ�";
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
				// ֻ����ƾ֤������״̬Ϊ���ѷ��͡��������ס��ļ�¼
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE);
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED);
				updateExce.runQueryCloseCon(ls_UpdateVoucher);
			} catch (Exception e) {
				logger.debug("��Ȩ֧�����ƾ֤����ʧ��");
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
	 * ����ƾ֤����TIPS�淶�ֶ�У��ȶ�
	 * 
	 * @param ����ƾ֤�ⱨ�Ľڵ�
	 *            node
	 * @param ����ƾ֤�ⱨ�Ľڵ��ֵ
	 *            value
	 * @param TIPS�淶�ֶγ���
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
				err = err + "�ڵ㣺" + node + "ֵΪ��,";
			} else if (value.length() > Integer.parseInt(tipsLength)) {
				err = err + "�ڵ㣺" + node + "���" + value.length()
						+ "����tips�淶�����" + tipsLength + ",";
			}
		}
		return err.equals("") == true ? "���Ĳ��淶��" + errMsg : err.substring(0,
				err.lastIndexOf(","));
	}

	/*
	 * ����ƾ֤���ͷ���ҵ������ƣ���ȿ����ж�ʹ�ã�
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
	 * ����ƾ֤���ͷ���ҵ������ƣ�ȫҵ��
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
	 * ����ƾ֤���ͷ���ҵ���dto
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
	 * ƾ֤�˻�����ҵ������״̬
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
	 * �������ջ��ش�������ȡ���ջ��ش�������
	 * 
	 * @param staxorgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static String getStaxorgname(String staxorgcode)
			throws ITFEBizException {
		String staxorgname = "";
		if (staxorgcode.equals("000000000000")) {
			staxorgname = "ȫϽ";
		} else if (staxorgcode.equals("111111111111")) {
			staxorgname = "��˰����";
		} else if (staxorgcode.equals("222222222222")) {
			staxorgname = "��˰����";
		} else if (staxorgcode.equals("333333333333")) {
			staxorgname = "���ز���";
		} else if (staxorgcode.equals("444444444444")) {
			staxorgname = "��������";
		} else if (staxorgcode.equals("555555555555")) {
			staxorgname = "��������";
		} else {
			staxorgname = "���ջ���δά��";
//			throw new ITFEBizException("���ջ��ش��룺" + staxorgcode
//					+ " ��Ӧ�����ջ���������\" ���ջ��ش���ά�� \"��δά����");
		}
		return staxorgname;
	}

	/**
	 * ǩ�ճɹ��쳣��ǩ��ʧ���쳣ɾ��֮ǰ��ͬƾ֤���Ѷ�ȡ״̬������
	 * 
	 * @param voucherdto
	 * @param flag
	 *            ǩ�ճɹ��쳣true��ǩ��ʧ���쳣��־false
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
		paramlist.add(DealCodeConstants.VOUCHER_ACCEPT);// �Ѷ�ȡ
		paramlist.add(voucherdto.getSdealno());
		try {
			List<TvVoucherinfoDto> deleteList = DatabaseFacade.getDb().find(
					TvVoucherinfoDto.class, selectsql, paramlist);
			TvVoucherinfoDto[] voucherdtos = new TvVoucherinfoDto[deleteList
					.size()];
			voucherdtos = deleteList.toArray(voucherdtos);
			// ɾ��������
			DatabaseFacade.getDb().delete(voucherdtos);
			if (flag) {
				// ǩ�ճɹ�ɾ��ҵ���
				for (TvVoucherinfoDto dto : deleteList) {
					String deletesql = "";
					if (MsgConstant.VOUCHER_NO_5207.equals(dto.getSvtcode())
							|| MsgConstant.VOUCHER_NO_5267.equals(dto
									.getSvtcode())) {// ʵ���ʽ�
						deletesql = " S_BIZNO = " + dto.getSdealno();
						deleteVouTable(TvPayoutmsgmainDto.tableName(),
								TvPayoutmsgsubDto.tableName(), deletesql);
					} else if (MsgConstant.VOUCHER_NO_5106.equals(dto
							.getSvtcode())) {// ��Ȩ֧��
						deletesql = " I_VOUSRLNO = "
								+ Long.valueOf(dto.getSdealno());
						deleteVouTable(TvGrantpaymsgmainDto.tableName(),
								TvGrantpaymsgsubDto.tableName(), deletesql);
					} else if (MsgConstant.VOUCHER_NO_5108.equals(dto
							.getSvtcode())) {// ֱ��֧��
						deletesql = " I_VOUSRLNO = "
								+ Long.valueOf(dto.getSdealno());
						deleteVouTable(TvDirectpaymsgmainDto.tableName(),
								TvDirectpaymsgmainDto.tableName(), deletesql);
					} else if (MsgConstant.VOUCHER_NO_2301.equals(dto
							.getSvtcode())) {// ��������
						deletesql = " I_VOUSRLNO = "
								+ Long.valueOf(dto.getSdealno());
						deleteVouTable(TvPayreckBankDto.tableName(),
								TvPayreckBankListDto.tableName(), deletesql);
					} else if (MsgConstant.VOUCHER_NO_2302.equals(dto
							.getSvtcode())) {// �˿�����
						deletesql = " I_VOUSRLNO = "
								+ Long.valueOf(dto.getSdealno());
						deleteVouTable(TvPayreckBankBackDto.tableName(),
								TvPayreckBankBackListDto.tableName(), deletesql);
					} else if (MsgConstant.VOUCHER_NO_5209.equals(dto
							.getSvtcode())) {// �����˸�
						deletesql = " I_VOUSRLNO = "
								+ Long.valueOf(dto.getSdealno());
						deleteVouTable(TvDwbkDto.tableName(), null, deletesql);
					}
				}
			}
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException("ɾ��ƾ֤���" + voucherdto.getSvoucherno()
					+ " �ظ������ݳ����쳣��");
		}

	}

	/**
	 * ɾ������ - �����ӱ���������
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
				// ��һ�� ɾ�������ӱ�
				String delsubSql = "delete from " + subtable + " where "
						+ delSql;
				delSubExec = DatabaseFacade.getODB().getSqlExecutorFactory()
						.getSQLExecutor();
				delSubExec.runQueryCloseCon(delsubSql);
			}

			if (delSql != null && !"".equals(delSql)) {
				// �ڶ��� ɾ����������
				String delmainSql = "delete from " + maintable + " where "
						+ delSql;
				delMainExec = DatabaseFacade.getODB().getSqlExecutorFactory()
						.getSQLExecutor();
				delMainExec.runQueryCloseCon(delmainSql);
			}
		} catch (JAFDatabaseException e) {
			logger.error("����ɾ��ʧ�ܣ�", e);
			throw new ITFEBizException("����ɾ��ʧ�ܣ�", e);
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
	 * ����ƾ֤�ļ�����
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
	 * ����ƾ֤����(��ɫͨ��)
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
	 * ��ȡ����ƾ֤����
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
	 * ��������ƾ֤��������
	 * 
	 * @param bytes
	 */
	public static void parseXml(byte[] bytes) {
		try {
			String basexml = new String(bytes, "GBK");
			String xml = null;
			// �Է�������ԭʼ���ܱ��Ľ��е�һ�ν��� @author �Ż��
			logger.info("��ɫͨ����ȡ��δ���뱨��:"+basexml);
			try
			{
				xml = base64Decode(basexml);
			}catch(Exception ee)
			{
				logger.error("��ɫͨ�����ν���:",ee);
				BASE64Decoder base64Decoder = new BASE64Decoder();
				byte[] out = base64Decoder.decodeBuffer(basexml);
				xml = new String(out, "GBK");
			}
			logger.info("��ɫͨ����ȡ�Ľ��뱨��:"+xml);
			Document fxrDoc = null;
			try{
				fxrDoc = DocumentHelper.parseText(xml);
			}catch(Exception xe)
			{
				logger.error("��ɫͨ�����ν���:",xe);
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
				logger.error("�����ļ�ʧ�ܣ������ֶ�FileName��FileBlockCount��AfewBlock��FileData���ڿ�ֵ��");
				return;
			}
			// ��ƾ֤����д�뻺��
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
	 * �����յ�ƾ֤��������д���ļ�
	 * 
	 * @param dto
	 * @throws ITFEBizException
	 */
	public static int mergeFile(TvVoucherinfoDto dto) throws ITFEBizException {
		int result = 0;
		logger.info("�����ļ���ʼ���ļ�������============" + map.size());
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
						.lastIndexOf("#") + 1);// �ļ��ְ�����
				String FileName = relateFileName.substring(0, relateFileName
						.lastIndexOf("#"));// �ļ���
				List<String> list = (List) map.get(relateFileName);
				if (list == null || list.size() == 0
						|| list.size() != Integer.parseInt(FileBlockCount)) {
					logger.error("�����ļ�ʧ�ܣ��ļ���" + FileName + "�������ļ�������ȫ��"+(list==null?0:list.size())+"=================="+FileBlockCount);
					continue;
				}else
				{
					logger.info("�����ļ��ɹ����ļ���" + FileName + "�������ļ�������"+(list==null?0:list.size())+"=================="+FileBlockCount);
				}
				// ����������д���ļ���׷��д�룩
				java.io.FileOutputStream out = new java.io.FileOutputStream(
						root + separator + FileName, true);
				// �洢���յ������и�����Ϣ�����ܵģ�
				StringBuilder Buffer = new StringBuilder();
				for (int i = 1; i <= list.size(); i++) {
					boolean flag = false;
					for (String data : list) {
						String FileData = data.substring(0, data.indexOf("$"));// �ļ�����
						String AfewBlock = data
								.substring(data.indexOf("$") + 1);// �ļ�˳���
						if (AfewBlock.equals(i + "")) {
							// ���յ��ĸ�����Ϣ�����ܵģ�׷��д�뵽Buffer
							Buffer.append(FileData);
							flag = true;
						}
					}
					if (!flag) {
						// �����ļ�ʧ��,ɾ�������ļ�
						FileUtil.getInstance().deleteFile(
								root + separator + FileName);
						logger.error("�����ļ�ʧ�ܣ��ļ���" + FileName + "δ�����ļ�˳���Ϊ" + i
								+ "�����ļ���");
						result -= 1;
						break;
					}
				}
				// ��ϵͳ�Դ��Ľ�������ܸ�����Ϣ��������д���ļ�
				BASE64Decoder base64Decoder = new BASE64Decoder();
				byte[] decoderBytes = base64Decoder.decodeBuffer(Buffer
						.toString());
				out.write(decoderBytes);
				logger.info("�����ļ��ɹ�,�ļ�·����"+(root + separator + FileName));
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
	 * ��list����ת��HashMap key����ϸId; vaule���ӱ�dto
	 * 
	 * @param subdtoList
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap convertListToMap(List subdtoList)
			throws ITFEBizException {
		if (subdtoList == null || subdtoList.size() == 0)
			throw new ITFEBizException("�ӱ���Ϊ�գ�");
		HashMap subdtoMap = new HashMap();
		if ((subdtoList.get(0)) instanceof TfDirectpaymsgsubDto) {// ֱ��֧��ƾ֤5201�ӱ�
			for (TfDirectpaymsgsubDto tempsudto : (List<TfDirectpaymsgsubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else if ((subdtoList.get(0)) instanceof TvPayoutmsgsubDto) {// ʵ���ʽ�ҵ����Ϣ5207�ӱ�
			for (TvPayoutmsgsubDto tempsudto : (List<TvPayoutmsgsubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else if ((subdtoList.get(0)) instanceof TfPaymentDetailssubDto) {// ����ҵ��֧����ϸ8207�ӱ�
			for (TfPaymentDetailssubDto tempsudto : (List<TfPaymentDetailssubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else if ((subdtoList.get(0)) instanceof TfDirectpayAdjustsubDto) {// ֱ��֧������ƾ֤5253�ӱ�
			for (TfDirectpayAdjustsubDto tempsudto : (List<TfDirectpayAdjustsubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else if ((subdtoList.get(0)) instanceof TvDirectpaymsgsubDto) {// ֱ��֧�����ƾ֤5253�ӱ�
			for (TvDirectpaymsgsubDto tempsudto : (List<TvDirectpaymsgsubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else if ((subdtoList.get(0)) instanceof TfPaybankRefundsubDto) {// �տ������˿�֪ͨ2252�ӱ�
			for (TfPaybankRefundsubDto tempsudto : (List<TfPaybankRefundsubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else if ((subdtoList.get(0)) instanceof TfGrantpayAdjustsubDto) {// ��Ȩ֧������ƾ֤5351�ӱ�
			for (TfGrantpayAdjustsubDto tempsudto : (List<TfGrantpayAdjustsubDto>) subdtoList) {
				subdtoMap.put(tempsudto.getSid(), tempsudto);
			}
		} else
			throw new ITFEBizException("û�ж����ҵ������!");
		return subdtoMap;
	}

	/**
	 * ��������Ĵ������д���+������ת����Map
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
		// ������Ȩ֧�����5106����
		if (idto instanceof TvGrantpaymsgmainDto) {
			TvGrantpaymsgmainDto maindto = (TvGrantpaymsgmainDto) idto;
			paybankcode = maindto.getStransactunit();// �����к�
			payAmt = payAmt.add(maindto.getNmoney());// �ܽ��
		}
		// ������Ȩ֧����������2301����
		else if (idto instanceof TvPayreckBankDto) {
			TvPayreckBankDto maindto = (TvPayreckBankDto) idto;
			paybankcode = maindto.getSagentbnkcode();// �����к�
			payAmt = payAmt.add(maindto.getFamt());// �ܽ��
		}
		// ������Ȩ֧������5351����
		else if (idto instanceof TfGrantpayAdjustmainDto) {
			TfGrantpayAdjustmainDto maindto = (TfGrantpayAdjustmainDto) idto;
			paybankcode = maindto.getSpaybankcode();// �����к�
			payAmt = payAmt.add(maindto.getNpayamt());// �ܽ��
		}
		// ����ֱ��֧������5253����
		else if (idto instanceof TfDirectpayAdjustmainDto) {
			TfDirectpayAdjustmainDto maindto = (TfDirectpayAdjustmainDto) idto;
			paybankcode = maindto.getSpayeeacctbankno();// �����к�
			payAmt = payAmt.add(maindto.getNpayamt());// �ܽ��
		}
		// ����ֱ��֧�����5108����
		else if (idto instanceof TvDirectpaymsgmainDto) {
			TvDirectpaymsgmainDto maindto = (TvDirectpaymsgmainDto) idto;
			paybankcode = maindto.getSpaybankno();// �����к�
			payAmt = payAmt.add(maindto.getNmoney());// �ܽ��
		}
		// ����ֱ��֧��5201����
		else if (idto instanceof TfDirectpaymsgmainDto) {
			TfDirectpaymsgmainDto maindto = (TfDirectpaymsgmainDto) idto;
			// �����������ҵ��Ѵ���������Ϊ�����������ƥ��
			if (StateConstant.BIZTYPE_CODE_BATCH.equals(maindto
					.getSbusinesstypecode().trim())) {
				paybankcode = maindto.getSpayeeacctbankno();// �����к�
			} else {
				paybankcode = maindto.getStrecode(); // �����к�
			}
			payAmt = payAmt.add(maindto.getNpayamt());// �ܽ��
		} else
			throw new ITFEBizException("û�ж����ҵ������!");
		map.put("paybankcode", paybankcode);
		map.put("payAmt", MtoCodeTrans.transformString(payAmt));
		return map;
	}

	/**
	 * ���� Ԥ�㵥λ����+���ܿ�Ŀ����+��� ��subdtoList����ת��Map key��Ԥ�㵥λ����|���ܿ�Ŀ���� value�����
	 * 
	 * @param subdtoList
	 * @return Map
	 * @throws ITFEBizException
	 */
	public static HashMap convertSubListToMap(List subdtoList)
			throws ITFEBizException {
		if (subdtoList == null || subdtoList.size() == 0)
			throw new ITFEBizException("�ӱ���Ϊ�գ�");
		HashMap<String, BigDecimal> map = new HashMap<String, BigDecimal>();
		String code = null;// Ԥ�㵥λ����+���ܿ�Ŀ����
		BigDecimal payAmt = BigDecimal.ZERO;// ���
		// ����ֱ��֧�����5108�ӱ�
		if ((subdtoList.get(0)) instanceof TvDirectpaymsgsubDto) {
			for (TvDirectpaymsgsubDto subdto : (List<TvDirectpaymsgsubDto>) subdtoList) {
				code = subdto.getSbudgetunitcode() + "|"
						+ subdto.getSfunsubjectcode();// Ԥ�㵥λ����+���ܿ�Ŀ����
				payAmt = subdto.getNmoney();// ���
				BigDecimal tempAmt = map.get(code);
				if (tempAmt != null)
					payAmt = payAmt.add(tempAmt);
				map.put(code, payAmt);
			}
		}
		// ����ֱ��֧��5201�ӱ�
		else if ((subdtoList.get(0)) instanceof TfDirectpaymsgsubDto) {
			for (TfDirectpaymsgsubDto subdto : (List<TfDirectpaymsgsubDto>) subdtoList) {
				code = subdto.getSagencycode() + "|" + subdto.getSexpfunccode();// Ԥ�㵥λ����+���ܿ�Ŀ����
				payAmt = subdto.getNpayamt();// ���
				BigDecimal tempAmt = map.get(code);
				if (tempAmt != null)
					payAmt = payAmt.add(tempAmt);
				map.put(code, payAmt);
			}
		}
		// ��Ȩ֧�����ƾ֤5106�ӱ�
		else if ((subdtoList.get(0)) instanceof TvGrantpaymsgsubDto) {
			for (TvGrantpaymsgsubDto subdto : (List<TvGrantpaymsgsubDto>) subdtoList) {
				code = subdto.getSbudgetunitcode() + "|"
						+ subdto.getSfunsubjectcode();// Ԥ�㵥λ����+���ܿ�Ŀ����
				payAmt = subdto.getNmoney();// ���
				BigDecimal tempAmt = map.get(code);
				if (tempAmt != null)
					payAmt = payAmt.add(tempAmt);
				map.put(code, payAmt);
			}
		}
		// ������Ȩ֧����������ƾ֤2301�ӱ�
		else if ((subdtoList.get(0)) instanceof TvPayreckBankListDto) {
			for (TvPayreckBankListDto subdto : (List<TvPayreckBankListDto>) subdtoList) {
				code = subdto.getSbdgorgcode() + "|"
						+ subdto.getSfuncbdgsbtcode();// Ԥ�㵥λ����+���ܿ�Ŀ����
				payAmt = subdto.getFamt();// ���
				BigDecimal tempAmt = map.get(code);
				if (tempAmt != null)
					payAmt = payAmt.add(tempAmt);
				map.put(code, payAmt);
			}
		}
		// ����ֱ����Ȩ֧����������ƾ֤5253�ӱ�
		else if ((subdtoList.get(0)) instanceof TfDirectpayAdjustsubDto) {
			for (TfDirectpayAdjustsubDto subdto : (List<TfDirectpayAdjustsubDto>) subdtoList) {
				code = subdto.getSagencycode() + "|" + subdto.getSexpfunccode();// Ԥ�㵥λ����+���ܿ�Ŀ����
				payAmt = subdto.getNpayamt();// ���
				BigDecimal tempAmt = map.get(code);
				if (tempAmt != null)
					payAmt = payAmt.add(tempAmt);
				map.put(code, payAmt);
			}
		}
		// ������Ȩ֧����������ƾ֤5351�ӱ�
		else if ((subdtoList.get(0)) instanceof TfGrantpayAdjustsubDto) {
			for (TfGrantpayAdjustsubDto subdto : (List<TfGrantpayAdjustsubDto>) subdtoList) {
				code = subdto.getSsupdepcode() + "|" + subdto.getSexpfunccode();// Ԥ�㵥λ����+���ܿ�Ŀ����
				payAmt = subdto.getNpayamt();// ���
				BigDecimal tempAmt = map.get(code);
				if (tempAmt != null)
					payAmt = payAmt.add(tempAmt);
				map.put(code, payAmt);
			}
		} else
			throw new ITFEBizException("û�ж����ҵ������!");
		return map;
	}

	/**
	 * ����TCBS��ִ���²��ƾ֤�������ҵ��������ҵ������ȫ�����պ����ƾ֤������ true ��ƾ֤�ǲ��ƾ֤ false ��ƾ֤���ǲ��ƾ֤
	 * 
	 * @param soritrano
	 *            ԭ������ˮ��
	 * @param sstatus
	 *            TCBS����״̬
	 * @param allamt
	 *            ʵ��֧�����
	 * @param sacctdate
	 *            ʵ��֧������
	 * @param sorientrustDate
	 *            ԭί������
	 * @param ls_Description
	 *            ��ע
	 * @return
	 */
	public static boolean updateVoucherSplitReceiveTCBS(String soritrano,
			String sstatus, BigDecimal allamt, String sacctdate,
			String sorientrustDate, String ls_Description) {
		// ���Ϻ���ֽ������Ҫ���ƾ֤
		if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0)
			return false;
		// ���ݲ����ˮ�š�ί�����ڲ���ԭ���ƾ֤
		TfVoucherSplitDto dto = findVoucherSplitDto(soritrano, sorientrustDate);
		// ���ƾ֤�������¼Ϊ�գ�˵�����ǲ��ƾ֤
		if (dto == null || StringUtils.isBlank(dto.getSstatus()))
			return false;
		boolean flag = true;
		// У����ƾ֤״̬����״̬ΪITFE������ [80000] ����ɹ� ��ITFE������ [80001] ����ʧ�� �򲻲���
		if (dto.getSstatus().equals(DealCodeConstants.DEALCODE_ITFE_SUCCESS)
				|| dto.getSstatus()
						.equals(DealCodeConstants.DEALCODE_ITFE_FAIL))
			return flag;
		// ���²��ƾ֤��Ϣ
		updateVoucherSpilt(dto, sstatus, ls_Description, allamt, sacctdate);
		// ������ͬƾ֤���ͺ�ƾ֤����µ����в��ƾ֤
		List list = findVoucherSplitDto(dto);
		if (list == null || list.size() == 0)
			return flag;
		int i = 0;
		BigDecimal nxallamt = BigDecimal.ZERO;
		// У����ƾ֤�Ƿ���ȫ������
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
		// ���ƾ֤δȫ�����գ�������ҵ������״̬
		if (i != list.size())
			return flag;
		try {
			// ���ƾ֤��ȫ�����գ�����ҵ��������Ϣ
			updateMainDtoByVoucherSplit(dto, nxallamt, dto.getSstatus(), dto
					.getSdemo());
			// ����5106ƾ֤������״̬��5106��Ȳ��ڴ����̸���ƾ֤������
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
	 * ���²��ƾ֤��Ϣ
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
			logger.error("���²��ƾ֤��Ϣ�쳣��", e);
			VoucherException.saveErrInfo(dto.getSvtcode(), "���²��ƾ֤��Ϣ�쳣��"
					+ e.getMessage());
		}
	}

	/**
	 * ���ݲ��ƾ֤����ҵ��������Ϣ
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
		String errMsg = "ƾ֤���ͣ�" + dto.getSvtcode() + "��ƾ֤��ţ�"
				+ dto.getSsplitno() + "��ί�����ڣ�" + dto.getScommitdate() + "��"
				+ "������ˮ�ţ�" + dto.getSsplitvousrlno() + "����TCBS��ִʱҵ���������ڡ�";
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
				throw new ITFEBizException("���ݲ��ƾ֤����ҵ����Ϣ�쳣��", e);
			} catch (ValidateException e) {
				logger.error(e);
				throw new ITFEBizException("���ݲ��ƾ֤����ҵ����Ϣ�쳣��", e);
			}
			// ҵ���������ڣ������쳣��־
			if (mainDtoList == null || mainDtoList.size() == 0) {
				logger.error(errMsg);
				VoucherException.saveErrInfo(dto.getSvtcode(), errMsg);
				return;
			}
			maindto = (TvGrantpaymsgmainDto) mainDtoList.get(0);
			// У��ҵ������״̬����״̬ΪITFE������ [80000] ����ɹ� ��ITFE������ [80001] ����ʧ�� �򲻲���
			if (maindto.getSstatus().equals(
					DealCodeConstants.DEALCODE_ITFE_SUCCESS)
					|| maindto.getSstatus().equals(
							DealCodeConstants.DEALCODE_ITFE_FAIL))
				return;
			maindto.setSstatus(sstatus);
			maindto.setSdemo(ls_Description);
			maindto.setNxallamt(nxallamt);
			maindto.setSaccdate(dto.getSxaccdate());
			// ����ҵ��������Ϣ
			try {
				DatabaseFacade.getODB().update(maindto);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("����ҵ��������Ϣ�쳣��", e);
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
				throw new ITFEBizException("���ݲ��ƾ֤����ҵ����Ϣ�쳣��", e);
			} catch (ValidateException e) {
				logger.error(e);
				throw new ITFEBizException("���ݲ��ƾ֤����ҵ����Ϣ�쳣��", e);
			}
			// ҵ���������ڣ������쳣��־
			if (mainDtoList == null || mainDtoList.size() == 0) {
				logger.error(errMsg);
				VoucherException.saveErrInfo(dto.getSvtcode(), errMsg);
				return;
			}
			maindto = (TvDirectpaymsgmainDto) mainDtoList.get(0);
			// У��ҵ������״̬����״̬ΪITFE������ [80000] ����ɹ� ��ITFE������ [80001] ����ʧ�� �򲻲���
			if (maindto.getSstatus().equals(
					DealCodeConstants.DEALCODE_ITFE_SUCCESS)
					|| maindto.getSstatus().equals(
							DealCodeConstants.DEALCODE_ITFE_FAIL))
				return;
			maindto.setSstatus(sstatus);
			maindto.setSdemo(ls_Description);
			maindto.setNxallamt(nxallamt);
			maindto.setSaccdate(dto.getSxaccdate());
			// ����ҵ��������Ϣ
			try {
				DatabaseFacade.getODB().update(maindto);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("����ҵ��������Ϣ�쳣��", e);
			}
		}
	}

	/**
	 * ��Ȩ֧�����ҵ�����TCBS��ִ����ƾ֤������
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
				+ " THEN 'TCBS����ʧ��' ELSE '����ɹ�' END ) AS S_DEMO FROM (SELECT A.S_ID, "
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
		// ��ѯ��ȫ������TCBS��ִ����Ȩ֧�����ҵ��������Ϣ
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
				// ����TCBS���ر��ĸ���ƾ֤״̬
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
	 * ֱ��ҵ��5201����TCBS��ִ����ҵ�������ƾ֤�������Ϻ���
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
			updateExce.addParam(voucherno);// ԭƾ֤���
			updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// ������״̬
			// [80000]
			// ����ɹ�
			// ��ƾ֤
			updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);// ������״̬
			// [80001]
			// ����ʧ��
			// ��ƾ֤
			updateExce.addParam(BusinessTypeCode);// ����ҵ���Ϻ���
			updateExce.runQuery(updateSql);
			Voucher voucher = (Voucher) ContextFactory.getApplicationContext()
					.getBean(MsgConstant.VOUCHER);
			// ����ֱ��֧��ҵ��������5201��ƾ֤״̬���Ϻ���
			voucher.VoucherReceiveTCBS(strecode, MsgConstant.VOUCHER_NO_5201,
					packno, BusinessTypeCode, voucherno, amt, sstatus,
					ls_Description);
			if (BusinessTypeCode.equals(StateConstant.BIZTYPE_CODE_BATCH))
				updateMatinDtoReceiveTCBS(MsgConstant.VOUCHER_NO_8207, sstatus,
						ls_Description, sacctdate, amt, strecode, voucherno,
						voucherdate, MsgConstant.VOUCHER_NO_5201);
			// ����ҵ���������ҵ����ϸ������8207��ƾ֤״̬���Ϻ���
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
	 *�տ������˿�֪ͨ2252����TCBS��ִ����ҵ�������ƾ֤�������Ϻ���
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
			updateExce.addParam(voucherno);// ԭƾ֤���
			updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// ������״̬
			// [80000]
			// ����ɹ�
			// ��ƾ֤
			updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);// ������״̬
			// [80001]
			// ����ʧ��
			// ��ƾ֤
			updateExce.runQuery(updateSql);
			Voucher voucher = (Voucher) ContextFactory.getApplicationContext()
					.getBean(MsgConstant.VOUCHER);
			// ����2252������ƾ֤״̬���Ϻ���
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
	 * ����TIPS��ִ���²��ƾ֤������
	 * 
	 * @param orimsgno
	 * @param state
	 * @param spackno
	 */
	public static void updateVoucherSplitReceiveTIPS(String orimsgno,
			String state, String spackno) {
		// ���Ϻ���ֽ������Ҫ���ƾ֤
		if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0)
			return;
		// ֻ����5106��5108��Ȳ��ƾ֤
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
			logger.error("���²��ƾ֤����������쳣!", e);
			VoucherException.saveErrInfo(svtcode, "���²��ƾ֤����������쳣!");
		}
	}

	/**
	 * ����TCBS��ִ��������״̬���Ϻ���
	 * 
	 * @author �Ż��
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
		// ���Ϻ���ֽ������Ҫִ�д˲���
		if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0)
			return;
		// ֻ��������ҵ��֧����ϸ8207ҵ������״̬
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
			updateExce.addParam(sstatus);// ״̬
			updateExce.addParam(sresult);// ��ע
			updateExce.addParam(dsumamt);// ������
			updateExce.addParam(sacctdate);// ��������
			if (svtcode.equals(MsgConstant.VOUCHER_NO_8207)) {
				updateExce.addParam(strecode);
				updateExce.addParam(sorivoudate);// ƾ֤����
				updateExce.addParam(sorivtcode);
				updateExce.addParam(sorivouno);
				updateExce.addParam(StateConstant.BIZTYPE_CODE_BATCH);
				updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// ������״̬
				// [80000]
				// ����ɹ�
				// ��ƾ֤
				updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_FAIL);// ������״̬
				// [80001]
				// ����ʧ��
				// ��ƾ֤
			}
			updateExce.runQueryCloseCon(ls_UpdateSql);
		} catch (JAFDatabaseException e) {
			logger.error("����ҵ��������Ϣ�쳣��", e);
		}
	}

	/**
	 * ���� ͬ��������˽ӿ�ת�� 8000
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
			logger.debug("�޶�Ӧ�Ķ�������");
		}
		return map;
	}

	/**
	 * ��ȡ�ȶ�ƾ֤���
	 * 
	 * @param vtcode
	 * @param voucherCompareVoucherno
	 * @param voucherno
	 * @return
	 */
	public static String getVoucherCompareVoucherno(String vtcode,
			String voucherCompareVoucherno, String voucherno) {
		if (vtcode.equals(MsgConstant.VOUCHER_NO_2252))
			return "֧��ƾ֤�ţ�����ƾ֤�ţ�" + voucherCompareVoucherno;
		else if ((MsgConstant.VOUCHER_NO_5106.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
				.indexOf(",sh,") >= 0)
				|| (MsgConstant.VOUCHER_NO_5108.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
						.indexOf(",sh,") >= 0))
			return "ԭҵ�񵥾ݺ�" + voucherCompareVoucherno;
		else if (vtcode.equals(MsgConstant.VOUCHER_NO_8207))
			return "��֧��ƾ֤���" + voucherCompareVoucherno;
		return "ƾ֤���" + voucherno;
	}

	/**
	 * �Ϻ���Ϣ����-��Ϣ��ƾ֤ ת�Ƶ���Ϣ��ϸ����
	 */
	public static void putVoucherDataToInteresDetail(String orgcode) {
		logger.debug("����Ϣ��ƾ֤ ת�Ƶ���Ϣ��ϸ����");
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

			// �����˿�
			ls_sql = "MERGE INTO TF_INTEREST_DETAIL A USING (select TRIM(TRIM(CHAR(main.I_VOUSRLNO))||CHAR(sub.I_SEQNO)) AS S_TRANO,main.S_BOOKORGCODE,main.S_AGENTBNKCODE,main.S_VOUNO,replace(main.s_hold2,'-','')  AS S_PAYDATE,replace(char(main.S_XCLEARDATE),'-','') AS S_XCLEARDATE,sub.F_AMT,main.S_TRECODE,banktype.S_BANKTYPE,sub.S_VOUCHERNO,'2302' AS vouchertype from TV_PAYRECK_BANK_BACK main, TS_CONVERTBANKTYPE banktype ,TV_PAYRECK_BANK_BACK_LIST sub WHERE main.S_PAYMODE = '1' and main.S_STATUS = '80000' and main.S_BOOKORGCODE = ? and to_DATE(TRIM(main.S_HOLD2),'yyyymmdd') < DATE(S_XCLEARDATE)   AND main.S_TRECODE = banktype.S_TRECODE AND main.S_AGENTBNKCODE = banktype.S_BANKCODE AND main.I_VOUSRLNO = sub.I_VOUSRLNO AND  DATE(main.S_XCLEARDATE) <= CURRENT DATE) B  ON (BIGINT(b.S_TRANO) = a.I_VOUSRLNO AND a.S_ORGCODE = b.S_BOOKORGCODE and A.S_VOUCHERNO = B.S_VOUNO AND A.S_INTERESTDATE = substr(replace(replace(b.S_PAYDATE,'/',''),'-',''),1,8)	AND A.S_LIQUIDATIONDATE = replace(char(b.S_XCLEARDATE),'-','')  AND A.S_BANKNO = B.S_AGENTBNKCODE  AND A.s_vouchertype = B.vouchertype and a.S_ORGCODE = ? )  WHEN NOT MATCHED THen INSERT(I_VOUSRLNO,S_ORGCODE,S_BANKTYPE,S_VOUCHERNO,S_VOUCHERTYPE,S_BANKNO,S_INTERESTDATE,S_LIQUIDATIONDATE,N_MONEY, S_STATUS,S_DEMO,TS_SYSUPDATE,S_USERCODE,S_EXT1,S_EXT2,S_EXT3)  VALUES(S_TRANO,S_BOOKORGCODE,S_BANKTYPE,S_VOUNO,vouchertype,S_AGENTBNKCODE,substr(replace(S_PAYDATE,'/',''),1,8),substr(S_XCLEARDATE,1,8),F_AMT,'','',CURRENT DATE,'','',S_TRECODE,S_VOUCHERNO)";
			sqlExecutorDetail.addParam(orgcode);
			sqlExecutorDetail.addParam(orgcode);
			sqlExecutorDetail.runQuery(ls_sql);
		} catch (Exception e) {
			logger.debug("��ʱ����Ϣ��ƾ֤ ת�Ƶ���Ϣ��ϸ���г����쳣��");
		} finally {
			if (sqlExecutorDetail != null)
				sqlExecutorDetail.closeConnection();
		}
	}

	/**
	 * �ൺ����ƾ֤������ɹ���
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
			logger.error("�ൺ��ȡƾ֤���ʧ�ܣ�" + num);
			throw new ITFEBizException("�ൺ��ȡƾ֤���ʧ��", e);
		} catch (ITFEBizException e) {
			logger.error("�ൺ��ȡƾ֤���ʧ�ܣ�" + num);
			throw new ITFEBizException("�ൺ��ȡƾ֤���ʧ��", e);
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
	 * ��������xml
	 * 
	 * @param xml
	 * @throws ITFEBizException
	 */
	public static void acceptVoucherXml(String xml, TvVoucherinfoDto voucherInfo) throws ITFEBizException{
		 Exception error = null;
		 List list = null;
		 try {
			 	String result = "42";	//����ʧ��
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
						result = "41";	//����ɹ�
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
						result = "41";	//����ɹ�
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
						result = "41";	//����ɹ�
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
					tvDwbkDto.setShold1(XPayDate);	//��������	��ʱʹ��hold1
					if(StringUtils.isNotBlank(XpayAmt) && tvDwbkDto.getXpayamt().compareTo(BigDecimal.ZERO) != 0){
						result = "41";	//����ɹ�
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
					tvVoucherinfoDto.setSdemo("����ɹ�");
				}else{
					tvVoucherinfoDto.setSdemo("����ʧ��");
				}
				DatabaseFacade.getODB().update(tvVoucherinfoDto);
			}catch (IndexOutOfBoundsException e) {
				error = e;
				logger.error("��ѯ������Ϣ�쳣����ƾ֤��" + voucherInfo.getSvoucherno()
						+ "��Ӧ�������¼�����ڣ�   ");
				throw new ITFEBizException("��ѯ������Ϣ�쳣����ƾ֤��" + voucherInfo.getSvoucherno()
						+ "��Ӧ�������¼�����ڣ�    errorCodeID:"
						+ System.currentTimeMillis(), e);
			}catch (DocumentException e) {
				error = e;
				logger.error("��ȡƾ֤�ص�ʧ�ܣ�ƾ֤��ţ�" + voucherInfo.getSvoucherno());
				throw new ITFEBizException("��ȡƾ֤�ص�ʧ�ܣ�ƾ֤��ţ�" + voucherInfo.getSvoucherno() , e);
			} catch (JAFDatabaseException e) {
				error = e;
				logger.error("��ȡƾ֤�ص�ʧ�ܣ�ƾ֤��ţ�" + voucherInfo.getSvoucherno());
				throw new ITFEBizException("��ȡƾ֤�ص�ʧ�ܣ�ƾ֤��ţ�" + voucherInfo.getSvoucherno(), e);
			} catch (Exception e) {
				error = e;
				logger.error("��ȡƾ֤�ص�ʧ�ܣ�ƾ֤��ţ�" + voucherInfo.getSvoucherno());
				throw new ITFEBizException("��ȡƾ֤�ص�ʧ�ܣ�ƾ֤��ţ�" + voucherInfo.getSvoucherno(), e);
			}finally{
				if(null != error){	//����ʧ���˻�ƾ֤
					VoucherService voucherService = new VoucherService();
					voucherService.confirmVoucherfail(null,voucherInfo.getSadmdivcode(), Integer.valueOf(voucherInfo.getSstyear()), voucherInfo.getSvtcode(),new String[] { voucherInfo.getSvoucherno() },new String[] {error.getMessage() });
				}
			}
			
		}
	
	/**
	 * ������嵥�е���ϸ��Ϣ
	 * @throws ITFEBizException 
	 */
	
	public static void fillPayoutDetailXinfo(IDto  mainDto,Node voucherNode) throws ITFEBizException{

			List subList = PublicSearchFacade
					.findSubDtoByMain(mainDto);
			if (subList == null || subList.size() == 0)
				throw new ITFEBizException(
						"����ҵ�������¼��ѯ�ӱ���ϸ��Ϣ����");
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
					throw new ITFEBizException("δ�ҵ��ͱ�����ϸId"
							+ sDetailId + "ƥ�����ϸ�ӱ��¼��");
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
					//��ʱĬ�ϳɹ�
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

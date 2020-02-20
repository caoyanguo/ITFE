package com.cfcc.itfe.twcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.filters.TokenFilter.Trim;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfRefundNoticeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTbsorgstatusDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.itfe.voucher.service.VoucherFactory;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.webservice.VoucherService;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

public class TWCSVerifyUtils {
	private static Log logger = LogFactory.getLog(TWCSVerifyUtils.class);

	public static String verifyInfoLocked(TvVoucherinfoDto infoDto) {
		if (StringUtils.isBlank(infoDto.getSdealno())
				|| StringUtils.isBlank(infoDto.getSvoucherno())) {
			return "查询凭证索引信息失败";
		}
		if (StringUtils.isNotBlank(infoDto.getSext3())
				&& TwcsDealCodeConstants.VOUCHER_LOCKING.equals(infoDto
						.getSext3())) {
			return "凭证状态未同步";
		}
		return null;
	}

	/**
	 * 查询更新凭证信息，用于重庆实拨资金更新状态
	 * 
	 * @param treCode
	 * @param vtCode
	 * @param vouDate
	 * @param vouNo
	 * @param admDivCode
	 * @return
	 * @throws ITFEBizException
	 */
	public static String updateVoucherStatus(String vtCode,String dealnos)
			throws ITFEBizException {
		try {
			String[] deals =  dealnos.split("##");
			List list = new ArrayList();
			for (int i = 0; i < deals.length; i++) {
				String str[] =  deals[i].split("\\|");
				String dealno = str[0];
				String status = str[1];
				String info =   str[2];
				if ("80000".equals(status)) {//前置处理成功状态
					status = DealCodeConstants.VOUCHER_REGULATORY_SUCCESS;
				}else{
					status = DealCodeConstants.VOUCHER_REGULATORY_FAULT;
				}
				TvVoucherinfoDto tvVoucherinfoDto = new TvVoucherinfoDto();
				tvVoucherinfoDto.setSdealno(dealno);
				tvVoucherinfoDto.setSvtcode(vtCode);
				List<TvVoucherinfoDto> lists = CommonFacade.getODB().findRsByDto(tvVoucherinfoDto);
				if (lists.size()>0) {
					TvVoucherinfoDto tmp = lists.get(0);
					tmp.setSstatus(status);
					tmp.setSdemo(info);
					list.add(tmp);
				}	
			}
			DatabaseFacade.getODB().update(CommonUtil.listTArray(list));
			return "";
		} catch (JAFDatabaseException e) {
			logger.error("TBSVerifyUtils.getTvVoucherinfoDto[" + e.getMessage()
					+ "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (ValidateException e) {
			logger.error("TBSVerifyUtils.getTvVoucherinfoDto[" + e.getMessage()
					+ "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}
	
	
	
	/**
	 * 同步更新凭证的清算状态，四川使用
	 * 
	 * @param treCode
	 * @param vtCode
	 * @param vouDate
	 * @param vouNo
	 * @param admDivCode
	 * @return
	 * @throws ITFEBizException
	 */
	public static String updateVoucherReckStatus(String vtCode,String dealnos)
			throws ITFEBizException {
		try {
			String[] deals =  dealnos.split("##");
			List list = new ArrayList();
			for (int i = 0; i < deals.length; i++) {
				String str[] =  deals[i].split("\\|");
				String dealno = str[0];
				String status = str[1];
				String info =   str[2];
				if ("80000".equals(status)) {//前置处理成功状态
					status =DealCodeConstants.VOUCHER_REGULATORY_SUCCESS;
				}else{
					status = DealCodeConstants.VOUCHER_REGULATORY_FAULT;
				}
				//更新凭证表状态
				if (vtCode.equals("5207")) {
					
				}else if (vtCode.equals("5209")) {
					
				}else if (vtCode.equals("2301")) {
					
				}else if (vtCode.equals("2302")) {
					
				}else if (vtCode.equals("5207")) {
					
				}if (vtCode.equals("5207")) {
					
				}
				//更新索引表状态
				if ("80000".equals(status)) {//前置处理成功状态
					status =DealCodeConstants.VOUCHER_REGULATORY_SUCCESS;
				}else{
					status = DealCodeConstants.VOUCHER_REGULATORY_FAULT;
				}
				TvVoucherinfoDto tvVoucherinfoDto = new TvVoucherinfoDto();
				tvVoucherinfoDto.setSdealno(dealno);
				tvVoucherinfoDto.setSvtcode(vtCode);
				List<TvVoucherinfoDto> lists = CommonFacade.getODB().findRsByDto(tvVoucherinfoDto);
				if (lists.size()>0) {
					TvVoucherinfoDto tmp = lists.get(0);
					tmp.setSstatus(status);
					tmp.setSdemo(info);
					list.add(tmp);
				}	
			}
			DatabaseFacade.getODB().update(CommonUtil.listTArray(list));
			return null;
		} catch (JAFDatabaseException e) {
			logger.error("TBSVerifyUtils.getTvVoucherinfoDto[" + e.getMessage()
					+ "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (ValidateException e) {
			logger.error("TBSVerifyUtils.getTvVoucherinfoDto[" + e.getMessage()
					+ "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}


	
	
	/**
	 * 
	 * @param tbs_011
	 *            获取发送财政配置凭证信息
	 * @param tvVoucherinfoDto
	 *            凭证索引信息
	 * @param decOrgType
	 *            接收机构
	 * @param isGenVoucher
	 *            是否本系统生成凭证信息
	 * @return 成功返回null 失败返回错误原因
	 * @throws ITFEBizException
	 */
	public static void sendVoucherUtil(Map<String, String> tbs_011,
			TvVoucherinfoDto tvVoucherinfoDto, String decOrgType,
			boolean isGenVoucher) throws ITFEBizException {
		try {
			String voucherOperation = tbs_011
					.get(tvVoucherinfoDto.getSvtcode());
			String[] voucherOperations = voucherOperation.split(",");
			VoucherService voucherService = new VoucherService();
			HashMap<String, String> map = null;
			String errReason = null;
			for (int i = 0; i < voucherOperations.length; i++) {
				if (StringUtils.isNotBlank(tvVoucherinfoDto.getSext2())
						&& tvVoucherinfoDto.getSext2().trim().indexOf(
								voucherOperations[i]) >= 0) {
					continue;
				}
				if (voucherOperations[i].startsWith("qm")) {
					// 签名操作
					map = VoucherUtil.getStampPotisionXML(tvVoucherinfoDto);
					// 写入签名凭证
					errReason = voucherService.signByNos(map.get("signcertid"),
							tvVoucherinfoDto.getSadmdivcode(), Integer
									.parseInt(tvVoucherinfoDto.getSstyear()),
							tvVoucherinfoDto.getSvtcode(), getVoucherXML(
									tvVoucherinfoDto,
									(MsgConstant.VOUCHER_NO_5209.equals(tvVoucherinfoDto.getSvtcode()) || MsgConstant.VOUCHER_NO_3209.equals(tvVoucherinfoDto.getSvtcode()))?true:false,
									(MsgConstant.VOUCHER_NO_5106
											.equals(tvVoucherinfoDto.getSvtcode()) || MsgConstant.VOUCHER_NO_5108
											.equals(tvVoucherinfoDto.getSvtcode()) ) ? true
											: false)
									.getBytes(), map.get("signposxml"));
					if (StringUtils.isNotBlank(errReason)) {
						logger.error("发送凭证[国库代码:"
								+ tvVoucherinfoDto.getStrecode() + "凭证编号:"
								+ tvVoucherinfoDto.getSvoucherno() + "凭证日期:"
								+ tvVoucherinfoDto.getScreatdate()
								+ "]失败！======" + errReason);
						updateVoucherInfoStatus(tvVoucherinfoDto,
								TwcsDealCodeConstants.VOUCHER_APP_FAIL,
								errReason);
						throw new ITFEBizException("发送凭证[国库代码:"
								+ tvVoucherinfoDto.getStrecode() + "凭证编号:"
								+ tvVoucherinfoDto.getSvoucherno() + "凭证日期:"
								+ tvVoucherinfoDto.getScreatdate()
								+ "]失败！======" + errReason);
					} else {
						updateVoucherOperation(tvVoucherinfoDto,
								voucherOperations[i]);
					}
				} else if (voucherOperations[i].startsWith("qz")) {
					// 签章 获取默认签章用户
					String[] stampOperations = voucherOperations[i].split("_");
					if (stampOperations.length <= 1) {
						logger.error("发送凭证[国库代码:"
								+ tvVoucherinfoDto.getStrecode() + "凭证编号:"
								+ tvVoucherinfoDto.getSvoucherno() + "凭证日期:"
								+ tvVoucherinfoDto.getScreatdate()
								+ "]失败！======签章配置有误");
						throw new ITFEBizException("发送凭证[国库代码:"
								+ tvVoucherinfoDto.getStrecode() + "凭证编号:"
								+ tvVoucherinfoDto.getSvoucherno() + "凭证日期:"
								+ tvVoucherinfoDto.getScreatdate()
								+ "]失败！======签章配置有误    errorCodeID:"
								+ System.currentTimeMillis());
					}
					errReason = voucherStampOperation(tvVoucherinfoDto,
							stampOperations[1]);
					if (StringUtils.isNotBlank(errReason)) {
						updateVoucherInfoStatus(tvVoucherinfoDto,
								TwcsDealCodeConstants.VOUCHER_APP_FAIL,
								errReason);
						logger.error("发送凭证[国库代码:"
								+ tvVoucherinfoDto.getStrecode() + "凭证编号:"
								+ tvVoucherinfoDto.getSvoucherno() + "凭证日期:"
								+ tvVoucherinfoDto.getScreatdate()
								+ "]失败！======" + errReason);
						throw new ITFEBizException("发送凭证[国库代码:"
								+ tvVoucherinfoDto.getStrecode() + "凭证编号:"
								+ tvVoucherinfoDto.getSvoucherno() + "凭证日期:"
								+ tvVoucherinfoDto.getScreatdate()
								+ "]失败！======" + errReason + "    errorCodeID:"
								+ System.currentTimeMillis());
					} else {
						updateVoucherOperation(tvVoucherinfoDto,
								voucherOperations[i]);
					}
				}

			}
			if(tvVoucherinfoDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301) || tvVoucherinfoDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)){
				errReason = voucherService.sendVoucherFullSigns(null,
						tvVoucherinfoDto.getSadmdivcode(), decOrgType, Integer
								.valueOf(tvVoucherinfoDto.getSstyear()),
						tvVoucherinfoDto.getSvtcode(),
						new String[] { tvVoucherinfoDto.getSvoucherno() });
			}else{
				errReason = voucherService.sendVoucher(null,
						tvVoucherinfoDto.getSadmdivcode(), decOrgType, Integer
								.valueOf(tvVoucherinfoDto.getSstyear()),
						tvVoucherinfoDto.getSvtcode(),
						new String[] { tvVoucherinfoDto.getSvoucherno() });
			}
			if (StringUtils.isNotBlank(errReason)) {
				updateVoucherInfoStatus(tvVoucherinfoDto,
						TwcsDealCodeConstants.VOUCHER_APP_FAIL, errReason);
				logger.error("发送凭证[国库代码:" + tvVoucherinfoDto.getStrecode()
						+ "凭证编号:" + tvVoucherinfoDto.getSvoucherno() + "凭证日期:"
						+ tvVoucherinfoDto.getScreatdate() + "]失败！======"
						+ errReason);
				throw new ITFEBizException("sendVoucherUtil发送凭证[国库代码:"
						+ tvVoucherinfoDto.getStrecode() + "凭证编号:"
						+ tvVoucherinfoDto.getSvoucherno() + "凭证日期:"
						+ tvVoucherinfoDto.getScreatdate() + "]失败！======"
						+ errReason + "    errorCodeID:"
						+ System.currentTimeMillis());
			}
			if ((ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode02,") >= 0)
					|| ((ITFECommonConstant.PUBLICPARAM
							.indexOf(",tbsmode=mode01,") >= 0) && (MsgConstant.VOUCHER_NO_5106
							.equals(tvVoucherinfoDto.getSvtcode())
							|| MsgConstant.VOUCHER_NO_5108
									.equals(tvVoucherinfoDto.getSvtcode()) || MsgConstant.VOUCHER_NO_3208
							.equals(tvVoucherinfoDto.getSvtcode())))) {
				updateVoucherInfoStatus(tvVoucherinfoDto,
						TwcsDealCodeConstants.VOUCHER_SUCCESS, "回单成功");
			} else {
				updateVoucherInfoStatus(tvVoucherinfoDto,
						TwcsDealCodeConstants.VOUCHER_LIQUIDATION_ING, "清算中");
			}
		} catch (ITFEBizException e) {
			logger.error("TBSVerifyUtils.sendVoucherUtil[" + e.getMessage()
					+ "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (Exception e) {
			logger.error("TBSVerifyUtils.sendVoucherUtil[" + e.getMessage()
					+ "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		}

	}

	private static String voucherStampOperation(
			TvVoucherinfoDto tvVoucherinfoDto, String userCode)
			throws ITFEBizException {
		// 查询默认签章用户
		try {
			TsUsersDto tsUsersDto = new TsUsersDto();
			tsUsersDto.setSorgcode(tvVoucherinfoDto.getSorgcode());
			tsUsersDto.setSusercode(userCode);
			tsUsersDto = (TsUsersDto) CommonFacade.getODB().findRsByDto(
					tsUsersDto).get(0);
			// 查询凭证签章位置
			List stampPositionList = findStamppositionDto(tvVoucherinfoDto);
			if (stampPositionList == null || stampPositionList.size() == 0)
				throw new ITFEBizException("签章位置未维护");
			// 查询国库信息和签章ID信息
			List strecodeList = findTsTreasuryDto(tvVoucherinfoDto);
			// 签章操作
			Voucher voucher = (Voucher) ContextFactory.getApplicationContext()
					.getBean(MsgConstant.VOUCHER);
			String str = voucherStamp(voucher, stampPositionList.size(),
					findStampTypeDtoList(sort(stampPositionList),
							tvVoucherinfoDto), strecodeList, tvVoucherinfoDto,
					tsUsersDto);
			if (StringUtils.isBlank(str)) {
				return null;
			} else {
				return str;
			}
		} catch (JAFDatabaseException e) {
			logger.error("TBSVerifyUtils.voucherStampOperation["
					+ e.getMessage() + "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (ValidateException e) {
			logger.error("TBSVerifyUtils.voucherStampOperation["
					+ e.getMessage() + "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (NumberFormatException e) {
			logger.error("TBSVerifyUtils.voucherStampOperation["
					+ e.getMessage() + "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (ITFEBizException e) {
			logger.error("TBSVerifyUtils.voucherStampOperation["
					+ e.getMessage() + "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param tvVoucherinfoDto
	 * @param operation
	 *            操作记录
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	private static void updateVoucherOperation(
			TvVoucherinfoDto tvVoucherinfoDto, String operation)
			throws ITFEBizException {
		try {
			if (StringUtils.isBlank(tvVoucherinfoDto.getSdealno())
					|| StringUtils.isBlank(tvVoucherinfoDto.getSvoucherno())) {
				logger.error("凭证索引信息有误！======记录操作记录:" + operation);
				throw new ITFEBizException("更新凭证操作日志异常！ " + "    errorCodeID:"
						+ System.currentTimeMillis());
			}
			if (StringUtils.isBlank(tvVoucherinfoDto.getSext2())) {
				tvVoucherinfoDto.setSext2(operation);
			} else {
				tvVoucherinfoDto.setSext2(tvVoucherinfoDto.getSext2() + ","
						+ operation);
			}
			tvVoucherinfoDto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
			tvVoucherinfoDto.setSdemo("签章(名)成功");
			DatabaseFacade.getODB().update(tvVoucherinfoDto);
		} catch (JAFDatabaseException e) {
			logger.error("TBSVerifyUtils.updateVoucherOperation["
					+ e.getMessage() + "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param tvVoucherinfoDto
	 * @param errReason
	 *            失败原因
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	public static void updateVoucherInfoStatus(
			TvVoucherinfoDto tvVoucherinfoDto, String status, String msg)
			throws ITFEBizException {
		try {
			if (StringUtils.isBlank(tvVoucherinfoDto.getSdealno())
					|| StringUtils.isBlank(tvVoucherinfoDto.getSvoucherno())) {
				logger.error("凭证索引信息有误！======记录状态和说明:" + status + msg);
				throw new ITFEBizException("更新凭证失败原因异常！ " + "    errorCodeID:"
						+ System.currentTimeMillis());
			}
			tvVoucherinfoDto.setSdemo(msg);
			tvVoucherinfoDto.setSext1(status);
			tvVoucherinfoDto.setSstatus(status);
			DatabaseFacade.getODB().update(tvVoucherinfoDto);
		} catch (JAFDatabaseException e) {
			logger.error("TBSVerifyUtils.updateVoucherInfoStatus["
					+ e.getMessage() + "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param dto
	 * @param isGenVoucher
	 *            是否系统自动生成凭证
	 * @param isWriteX
	 *            是否需要补录X开头字段
	 * @return
	 * @throws ITFEBizException
	 */
	private static String getVoucherXML(TvVoucherinfoDto dto,
			boolean isGenVoucher, boolean isWriteX) throws ITFEBizException {
		try {
			// 如果需要补录X开头
			if (isWriteX) {
				return VoucherUtil.getVoucherStampXML(dto);
			}
			String stampXML = "";
			Document fxrDoc;// 临时的凭证报文
			Document successDoc;// 返回给凭证库的成功报文
			successDoc = DocumentHelper.createDocument();
			successDoc.setXMLEncoding("GBK");
			Element root = successDoc.addElement("MOF");
			Element VoucherCount = root.addElement("VoucherCount");
			VoucherCount.setText("1");
			Element newBody = root.addElement("VoucherBody");
			newBody.addAttribute("AdmDivCode", dto.getSadmdivcode());
			newBody.addAttribute("StYear", dto.getSstyear());
			newBody.addAttribute("VtCode", dto.getSvtcode());
			newBody.addAttribute("VoucherNo", dto.getSvoucherno());
			Element VoucherFlag = newBody.addElement("VoucherFlag");
			VoucherFlag.setText("0");
			// Element Return_Reason = newBody.addElement("Return_Reason");
			Element Attach = newBody.addElement("Attach");
			Attach.setText("SUCCESS");
			Element Voucher = newBody.addElement("Voucher");

			if (isGenVoucher) {
				VoucherFlag.setText("0");// 回单标志：0 凭证自主生成报文 1 回单报文
				stampXML = com.cfcc.itfe.util.FileUtil.getInstance().readFile(
						dto.getSfilename());
				Voucher.setText(VoucherUtil.base64Encode(stampXML));
				return successDoc.asXML().replaceAll("&lt;", "<").replaceAll(
						"&gt;", ">");
			}
			/*
			 * 获取回单报文
			 */
			String s = FileUtil.getInstance().readFile(dto.getSfilename());
			fxrDoc = DocumentHelper.parseText(s);
			List listNodes = fxrDoc.selectSingleNode("MOF").selectNodes(
					"VoucherBody");
			for (int i = 0; i < listNodes.size(); i++) {
				String VoucherNo = ((Element) listNodes.get(i)).attribute(
						"VoucherNo").getText();
				if (VoucherNo.equals(dto.getSvoucherno())) {
					Element voucherbody = (Element) listNodes.get(i);
					Node voucherNode = voucherbody.selectSingleNode("Voucher");

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
			// dto.setSadmdivcode(dto.getSadmdivcode());
			return stampXML;
		} catch (ITFEBizException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	/**
	 * 根据凭证索引表查询对应的业务数据，将业务数据按照地方横连格式封装 返回txt
	 * 
	 * @param tvVoucherinfoDto
	 * @return 返回List数组 index0:更新过索引表的dto list数组 index1：业务数据按照地方横连格式封装
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 * @throws UnsupportedEncodingException
	 */
	public static List getBizDatas(List<TvVoucherinfoDto> tvInfoList)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		int count = 0;
		if (tvInfoList.size() > 100) {
			count = 100;
		} else {
			count = tvInfoList.size();
		}
		TvVoucherinfoDto tmpdto = null;
		StringBuffer resultStr = new StringBuffer();
		List resultDto = new ArrayList();
		List mainList = null;
		List subList = new ArrayList();
		for (int i = 0; i < count; i++) {
			subList.clear();
			tmpdto = tvInfoList.get(i);
			if (MsgConstant.VOUCHER_NO_5106.equals(tmpdto.getSvtcode())) {
				TvGrantpaymsgmainDto tvGrantpaymsgmainDto = new TvGrantpaymsgmainDto();
				tvGrantpaymsgmainDto.setIvousrlno(Long.valueOf(tmpdto
						.getSdealno()));
				mainList = CommonFacade.getODB().findRsByDto(
						tvGrantpaymsgmainDto);
				if (null == mainList || mainList.size() == 0) {
					continue;
				}
				List packList = null;
				for(int j=0;j<mainList.size();j++){
					tvGrantpaymsgmainDto = (TvGrantpaymsgmainDto) mainList.get(j);
					TvGrantpaymsgsubDto tvGrantpaymsgsubDto = new TvGrantpaymsgsubDto();
					tvGrantpaymsgsubDto.setIvousrlno(tvGrantpaymsgmainDto.getIvousrlno());
					tvGrantpaymsgsubDto.setSpackageticketno(tvGrantpaymsgmainDto.getSpackageticketno());
					packList = CommonFacade.getODB().findRsByDto(tvGrantpaymsgsubDto);
					subList.add(packList);
				}
				tmpdto.setSext4(tvGrantpaymsgmainDto.getSgenticketdate()
						.replaceAll("-", ""));
				
				List<IDto> dtoList = new ArrayList<IDto>();
				for (int j=0;j<mainList.size();j++) {
					TvGrantpaymsgmainDto gMainDto = (TvGrantpaymsgmainDto)mainList.get(j);
					gMainDto.setSstatus("80000");
					dtoList.add(gMainDto);
				}
				DatabaseFacade.getODB().update(CommonUtil.listTArray(dtoList));
			} else if (MsgConstant.VOUCHER_NO_5108.equals(tmpdto.getSvtcode())) {
				TvDirectpaymsgmainDto tvDirectpaymsgmainDto = new TvDirectpaymsgmainDto();
				tvDirectpaymsgmainDto.setIvousrlno(Long.valueOf(tmpdto
						.getSdealno()));
				mainList = CommonFacade.getODB().findRsByDto(
						tvDirectpaymsgmainDto);
				if (null == mainList || mainList.size() == 0) {
					continue;
				}
				tvDirectpaymsgmainDto = (TvDirectpaymsgmainDto) mainList.get(0);
				TvDirectpaymsgsubDto tvDirectpaymsgsubDto = new TvDirectpaymsgsubDto();
				tvDirectpaymsgsubDto.setIvousrlno(tvDirectpaymsgmainDto
						.getIvousrlno());
				subList = CommonFacade.getODB().findRsByDto(
						tvDirectpaymsgsubDto);
				if (null == subList || subList.size() == 0) {
					continue;
				}
				tmpdto.setSext4(tvDirectpaymsgmainDto.getSgenticketdate());
			} else if (MsgConstant.VOUCHER_NO_2301.equals(tmpdto.getSvtcode())) {
				TvPayreckBankDto tvPayreckBankDto = new TvPayreckBankDto();
				tvPayreckBankDto
						.setIvousrlno(Long.valueOf(tmpdto.getSdealno()));
				mainList = CommonFacade.getODB().findRsByDto(tvPayreckBankDto);
				if (null == mainList || mainList.size() == 0) {
					continue;
				}
				tvPayreckBankDto = (TvPayreckBankDto) mainList.get(0);
				TvPayreckBankListDto tvPayreckBankListDto = new TvPayreckBankListDto();
				tvPayreckBankListDto.setIvousrlno(tvPayreckBankDto
						.getIvousrlno());
				subList = CommonFacade.getODB().findRsByDto(
						tvPayreckBankListDto);
				if (null == subList || subList.size() == 0) {
					continue;
				}
				tmpdto.setSext4(DateUtil.date2String2(tvPayreckBankDto
						.getDvoudate()));
			} else if (MsgConstant.VOUCHER_NO_2302.equals(tmpdto.getSvtcode())) {
				TvPayreckBankBackDto tvPayreckBankBackDto = new TvPayreckBankBackDto();
				tvPayreckBankBackDto.setIvousrlno(Long.valueOf(tmpdto
						.getSdealno()));
				mainList = CommonFacade.getODB().findRsByDto(
						tvPayreckBankBackDto);
				if (null == mainList || mainList.size() == 0) {
					continue;
				}
				tvPayreckBankBackDto = (TvPayreckBankBackDto) mainList.get(0);
				TvPayreckBankBackListDto tvPayreckBankBackListDto = new TvPayreckBankBackListDto();
				tvPayreckBankBackListDto.setIvousrlno(tvPayreckBankBackDto
						.getIvousrlno());
				subList = CommonFacade.getODB().findRsByDto(
						tvPayreckBankBackListDto);
				if (null == subList || subList.size() == 0) {
					continue;
				}
				tmpdto.setSext4(DateUtil.date2String2(tvPayreckBankBackDto
						.getDvoudate()));
			} else if (MsgConstant.VOUCHER_NO_5207.equals(tmpdto.getSvtcode())) {
				TvPayoutmsgmainDto tvPayoutmsgmainDto = new TvPayoutmsgmainDto();
				tvPayoutmsgmainDto.setSbizno(tmpdto.getSdealno());
				mainList = CommonFacade.getODB()
						.findRsByDto(tvPayoutmsgmainDto);
				if (mainList == null || mainList.size() == 0) {
					continue;
				}
				tvPayoutmsgmainDto = (TvPayoutmsgmainDto) mainList.get(0);
				TvPayoutmsgsubDto tvPayoutmsgsubDto = new TvPayoutmsgsubDto();
				tvPayoutmsgsubDto.setSbizno(tvPayoutmsgmainDto.getSbizno());
				subList = CommonFacade.getODB().findRsByDto(tvPayoutmsgsubDto);
				if (mainList == null || mainList.size() == 0) {
					continue;
				}
				tmpdto.setSext4(tvPayoutmsgmainDto.getSgenticketdate()
						.replaceAll("-", ""));
			} else if (MsgConstant.VOUCHER_NO_5209.equals(tmpdto.getSvtcode())) {
				TvDwbkDto tvDwbkDto = new TvDwbkDto();
				tvDwbkDto.setIvousrlno(Long.valueOf(tmpdto.getSdealno()));
				mainList = CommonFacade.getODB().findRsByDto(tvDwbkDto);
				if (mainList == null || mainList.size() == 0) {
					continue;
				}
				tvDwbkDto = (TvDwbkDto) mainList.get(0);
				tmpdto.setSext4(DateUtil.date2String2(tvDwbkDto.getDvoucher()));
			} else if (MsgConstant.VOUCHER_NO_2003.equals(tmpdto.getSvtcode())) {
				TfRefundNoticeDto refundNoticeDto = new TfRefundNoticeDto();
				refundNoticeDto.setSid(tmpdto.getSdealno());
				mainList = CommonFacade.getODB().findRsByDto(refundNoticeDto);
				if (mainList == null || mainList.size() == 0) {
					continue;
				}
				refundNoticeDto = (TfRefundNoticeDto) mainList.get(0);
				tmpdto.setSext4(refundNoticeDto.getSvoudate());
			} else if(MsgConstant.VOUCHER_NO_3210.equals(tmpdto.getSvtcode())){
				TvDwbkDto tvDwbkDto = new TvDwbkDto();
				tvDwbkDto.setIvousrlno(Long.valueOf(tmpdto.getSdealno()));
				mainList = CommonFacade.getODB().findRsByDto(tvDwbkDto);
				if (mainList == null || mainList.size() == 0) {
					continue;
				}
				tvDwbkDto = (TvDwbkDto) mainList.get(0);
				tmpdto.setSext4(DateUtil.date2String2(tvDwbkDto.getDvoucher()));
			} else {
				continue;
			}
			getBizStr(resultStr, mainList, subList, tmpdto.getSvtcode());
			resultDto.add(tmpdto);
		}

		List result = new ArrayList();
		if (resultDto.size() != 0) {
			result.add(resultDto);
			String os = System.getProperties().getProperty("os.name");
			String retFlag = "";
			if (os.indexOf("Win") >= 0)  {
				retFlag = "\r\n"; //回车换行符
			}else{
				retFlag ="\n"; //换行符
			}
			result.add("##" + resultDto.size() +retFlag + resultStr.toString());
			return result;
		}
		return null;
	}

	private static void getBizStr(StringBuffer str, List mainList,
			List subList, String vtCode) throws ITFEBizException {
		String os = System.getProperties().getProperty("os.name");
		if (MsgConstant.VOUCHER_NO_5106.equals(vtCode)) {
			TvGrantpaymsgmainDto maindto = null;
			for(int n=0;n<mainList.size();n++){
				maindto = (TvGrantpaymsgmainDto) mainList.get(n);
				// 主单**开头
				str.append("**");
				str.append(maindto.getIvousrlno()+","); //凭证流水号1
				str.append(maindto.getSorgcode()+","); //机构代码2
				str.append(maindto.getScommitdate()+",");//委托日期3
				str.append(maindto.getStrecode() + ","); // 国库主体代码4
				str.append(maindto.getSpayunit() + ","); // 出票单位5
				str.append(maindto.getNmoney() + ","); // 零余额发生额6
				str.append(maindto.getSpackageticketno() + ","); // 凭证编号7
				str.append(maindto.getSgenticketdate() + ","); // 开票日期8
				str.append(maindto.getSofyear() + ","); // 所属年度9
				str.append(maindto.getSofmonth() + ","); // 所属月份10
				str.append(maindto.getStransactunit() + ","); // 经办单位11
				str.append(maindto.getSamttype() + ","); // 额度种类12
				str.append(maindto.getSbudgetunitcode() + ","); // 预算单位代码13
				str.append(maindto.getSbudgettype() + ","); // 预算种类14
				str.append(maindto.getSpaybankno() + ","); // 代理银行行号15
				str.append(maindto.getSpaybankcode() + ","); // 代理银行编码16
				str.append(maindto.getSgenticketdate() + ","); //凭证日期17
				String tStr = maindto.getSdemo();
				if(tStr != null && !tStr.trim().equals("")){
					if(tStr.contains(",")){
						tStr = tStr.replace(",", "，");
					}
				}
				str.append(tStr + ","); // 备注18
				str.append(maindto.getSdealno() + ""); // 交易流水号19
				if (os.indexOf("Win") >= 0)  {
					str.append("\r\n"); //回车换行符
				}else{
					str.append("\n"); //换行符
				}
				TvGrantpaymsgsubDto subdto = null;
				List tmpList = (List)subList.get(n);
				for (int m = 0; m < tmpList.size(); m++) {
					subdto = (TvGrantpaymsgsubDto) tmpList.get(m);
					// 明细以#*开头
					str.append("#*");
					str.append(subdto.getIvousrlno() + ","); // 凭证流水号
					str.append(subdto.getIdetailseqno()+","); //明细序号
					str.append(subdto.getSpackageticketno() + ","); //凭证编号
					str.append(subdto.getSfunsubjectcode() + ",");// 预算支出功能科目
					str.append(subdto.getSecosubjectcode() + ",");// 预算支出经济科目
					str.append(subdto.getNmoney() + ","); // 零余额发生额
					str.append(subdto.getSaccattrib()); //账户性质
					if (os.indexOf("Win") >= 0)  {
						str.append("\r\n"); //回车换行符
					}else{
						str.append("\n"); //换行符
					}
				}
			}
		} else if (MsgConstant.VOUCHER_NO_5108.equals(vtCode)) {
			TvDirectpaymsgmainDto maindto = null;
			maindto = (TvDirectpaymsgmainDto) mainList.get(0);
			// 主单**开头
			str.append("**");
			str.append(maindto.getIvousrlno()+","); //凭证流水号1
			str.append(maindto.getSorgcode()+","); //机构代码2
			str.append(maindto.getScommitdate() + ","); //委托日期3
			str.append(maindto.getStrecode() + ","); // 国库主体代码4
			str.append(maindto.getSpayunit() + ","); // 出票单位5
			str.append(maindto.getNmoney() + ","); // 零余额发生额6
			str.append(maindto.getSpackageticketno() + ","); // 凭证编号7
			str.append(maindto.getSgenticketdate() + ","); // 开票日期8
			str.append(maindto.getSbudgettype() + ","); // 预算种类14
			str.append(maindto.getSofyear() + ","); // 所属年度9
			str.append(maindto.getStransactunit() + ","); // 经办单位11
			str.append(maindto.getSamttype() + ","); // 额度种类12
			String tStr = maindto.getSdemo();
			if(tStr != null && !tStr.trim().equals("")){
				if(tStr.contains(",")){
					tStr = tStr.replace(",", "，");
				}
			}
			str.append(tStr + ","); // 备注18
			str.append(maindto.getSdealno() + ""); // 交易流水号19
			if (os.indexOf("Win") >= 0)  {
				str.append("\r\n"); //回车换行符
			}else{
				str.append("\n"); //换行符
			}
			TvDirectpaymsgsubDto subdto = null;
			for (int i = 0; i < subList.size(); i++) {
				subdto = (TvDirectpaymsgsubDto) subList.get(i);
				// 明细开头#*
				str.append("#*");
				str.append(subdto.getIvousrlno() + ","); // 凭证流水号
				str.append(subdto.getIdetailseqno()+","); //明细序号
				str.append(subdto.getSfunsubjectcode() + ",");// 功能科目代码
				str.append(subdto.getSecosubjectcode() + ",");// 预算支出经济科目
				str.append(subdto.getSagencycode() + ","); // 预算单位代码
				str.append(subdto.getNmoney()); // 发生额
				if (os.indexOf("Win") >= 0)  {
					str.append("\r\n"); //回车换行符
				}else{
					str.append("\n"); //换行符
				}
			}
	
		} else if (MsgConstant.VOUCHER_NO_2301.equals(vtCode)) {
			TvPayreckBankDto maindto = null;
			maindto = (TvPayreckBankDto) mainList.get(0);
			// 主单**开头
			str.append("**");
			str.append(maindto.getIvousrlno()+ ","); // 凭证流水号
			str.append(maindto.getSbookorgcode()+ ",");//核算主体代码
			str.append(maindto.getDentrustdate()+ ",");//委托日期
			str.append(maindto.getSagentbnkcode()+ ",");//代理银行行号
			str.append(maindto.getStrano()+ ",");//交易流水号
			str.append(maindto.getSfinorgcode()+ ",");//财政机关代码
			str.append(maindto.getStrecode() + ","); // 国库代码
			str.append(maindto.getSvouno() + ","); // 凭证编号
			str.append(maindto.getDvoudate() + ","); // 凭证日期
			str.append(maindto.getSpayeracct() + ","); // 付款人账号
			str.append(maindto.getSpayername() + ","); // 付款人名称
			str.append(maindto.getSpayeraddr()+ ","); // 付款人地址
			str.append(maindto.getSpayeeacct() + ","); // 收款人账号
			str.append(maindto.getSpayeename() + ","); // 收款人名称
			str.append(maindto.getSpayeeaddr() + ","); // 收款人地址
			str.append(maindto.getSpayeeopbkno() + ","); // 收款人开户行行号
			str.append(maindto.getSbudgettype() + ","); // 预算种类代码
			str.append(maindto.getStrimsign() + ","); // 整理期标志
			str.append(maindto.getSofyear() + ","); // 所属年度
			str.append(maindto.getSpayeeopbkno() + ","); // 收款人开户行行号
			str.append(maindto.getFamt() + ","); // 零余额发生额,小额现金发生额,
			str.append(maindto.getSpaymode() + ","); //支付方式
			str.append(maindto.getDacceptdate() + ","); //受理日期
			str.append(maindto.getIstatinfnum() + ","); //明细条数
			str.append(maindto.getSpaytypecode() + ","); //支付方式编码
			str.append(maindto.getSpaytypename() + ","); //支付方式名称
			str.append(maindto.getSagentbnkcode() + ","); // 接收行行号 (代理银行行号)
			
			String tStr = maindto.getSaddword();
			if(tStr != null && !tStr.trim().equals("")){
				if(tStr.contains(",")){
					tStr = tStr.replace(",", "，");
				}
			}
			str.append(tStr + ","); // 附言
			String tStr1 = maindto.getSdescription();
			if(tStr1 != null && !tStr1.trim().equals("")){
				if(tStr1.contains(",")){
					tStr1 = tStr1.replace(",", "，");
				}
			}
			str.append(tStr1 + ","); //  摘要代码(说明)
			//清算日期
			str.append(maindto.getSxcleardate() + ","); //清算日期
			if (os.indexOf("Win") >= 0)  {
				str.append("\r\n"); //回车换行符
			}else{
				str.append("\n"); //换行符
			}
			
			TvPayreckBankListDto subdto = null;
			for (int i = 0; i < subList.size(); i++) {
				subdto = (TvPayreckBankListDto) subList.get(i);
				// 明细开头#*
				str.append("#*");
				str.append(subdto.getIvousrlno()+ ",");//凭证流水号
				str.append(subdto.getIseqno()+",");//明细序号
				str.append(subdto.getSbdgorgcode() + ","); // 预算单位代码
				str.append(subdto.getSfuncbdgsbtcode() + ",");// 功能科目代码
				str.append(subdto.getSecnomicsubjectcode() + ",");//经济科目代码
				str.append(subdto.getSacctprop() + ","); // 账户性质
				str.append(subdto.getFamt()); // 支付金额
				if (os.indexOf("Win") >= 0)  {
					str.append("\r\n"); //回车换行符
				}else{
					str.append("\n"); //换行符
				}
			}
		} else if (MsgConstant.VOUCHER_NO_2302.equals(vtCode)) {
			TvPayreckBankBackDto maindto = null;
			maindto = (TvPayreckBankBackDto) mainList.get(0);
			// 主单**开头
			str.append("**");
			str.append(maindto.getIvousrlno()+ ","); // 凭证流水号
			str.append(maindto.getSbookorgcode()+ ",");//核算主体代码
			str.append(maindto.getDentrustdate()+ ",");//委托日期
			str.append(maindto.getSagentbnkcode()+ ",");//代理银行行号
			str.append(maindto.getStrano()+ ",");//交易流水号
			str.append(maindto.getSvouno() + ","); // 凭证编号
			str.append(maindto.getDvoudate() + ","); // 凭证日期
			str.append(maindto.getSfinorgcode()+ ",");//财政机关代码
			str.append(maindto.getStrecode() + ","); // 国库代码
			str.append(maindto.getSoritrano() + ","); // 原交易流水号
			str.append(maindto.getDorientrustdate()+ ",");//原委托日期
			str.append(maindto.getSorivouno() + ","); // 原交易流水号
			str.append(maindto.getDorivoudate() + ","); //原凭证日期
			str.append(maindto.getSpayeracct() + ","); // 付款人账号
			str.append(maindto.getSpayername() + ","); // 付款人全称
			str.append(maindto.getSpayeeacct() + ","); // 收款人账户
			str.append(maindto.getSpayeename() + ","); // 收款人名称
			str.append(maindto.getSpaydictateno() + ","); // 支付交易序号
			str.append(maindto.getSpaymsgno() + ","); // 支付报文编号
			str.append(maindto.getDpayentrustdate() + ","); // 支付委托日期
			str.append(maindto.getSpaysndbnkno() + ","); // 支付发起行行号
			str.append(maindto.getSbudgettype() + ","); // 预算种类代码
			str.append(maindto.getStrimsign() + ","); // 整理期标志
			str.append(maindto.getSofyear() + ","); // 所属年度
			str.append(maindto.getFamt() + ","); // 零余额发生额、
			str.append(maindto.getIstatinfnum() + ","); //明细条数
			str.append(maindto.getDacceptdate() + ","); //受理日期
			str.append(maindto.getSpaymode() + ","); //支付方式
			str.append(maindto.getSpaytypecode() + ","); //支付方式编码
			str.append(maindto.getSpaytypename() + ","); //支付方式名称
			
			
			String tStr = maindto.getSaddword();
			if(tStr != null && !tStr.trim().equals("")){
				if(tStr.contains(",")){
					tStr = tStr.replace(",", "，");
				}
			}
			str.append(tStr + ","); // 附言
			String tStr1 = maindto.getSremark();
			if(tStr1 != null && !tStr1.trim().equals("")){
				if(tStr1.contains(",")){
					tStr1 = tStr1.replace(",", "，");
				}
			}
			str.append(tStr1 + ","); //  摘要代码(说明)
			//清算日期
			str.append(maindto.getSxcleardate() + ","); //清算日期
			if (os.indexOf("Win") >= 0)  {
				str.append("\r\n"); //回车换行符
			}else{
				str.append("\n"); //换行符
			}
			TvPayreckBankBackListDto subdto = null;
			for (int i = 0; i < subList.size(); i++) {
				subdto = (TvPayreckBankBackListDto) subList.get(i);
				// 明细开头#*
				str.append("#*");
				str.append(subdto.getIvousrlno()+ ",");//凭证流水号
				str.append(subdto.getIseqno()+ ",");// 明细序号
				str.append(subdto.getSorivouno()+ ","); // 原凭证编号
				str.append(subdto.getDorivoudate()+ ","); // 原凭证日期
				str.append(subdto.getSbdgorgcode() + ","); // 预算单位代码
				str.append(subdto.getSfuncbdgsbtcode() + ","); // 功能科目代码
				str.append(subdto.getSecnomicsubjectcode() + ",");//经济科目代码
				str.append(subdto.getSacctprop() + ","); // 账户性质
				str.append(subdto.getFamt()); // 支付金额
				if (os.indexOf("Win") >= 0)  {
					str.append("\r\n"); //回车换行符
				}else{
					str.append("\n"); //换行符
				}
			}
		} else if (MsgConstant.VOUCHER_NO_5207.equals(vtCode)) {
			TvPayoutmsgmainDto maindto = null;
			TvPayoutmsgsubDto subdto = null;
			maindto = (TvPayoutmsgmainDto) mainList.get(0);
			subdto = (TvPayoutmsgsubDto) subList.get(0);
			// 主单**开头
			str.append("**");
			str.append(maindto.getSbizno() + ","); // 发送流水号
			str.append(maindto.getSorgcode() + ","); // 机构代码
			str.append(maindto.getScommitdate() + ","); // 委托日期
			str.append(maindto.getSgenticketdate()+",");//凭证日期
			str.append(maindto.getStrecode() + ","); // 国库代码
			str.append(maindto.getSpayunit() + ","); // 出票单位
			str.append(maindto.getSpayeebankno() + ","); //转发银行行号
			str.append(maindto.getSdealno()+ ",");//交易流水号
			str.append(maindto.getStaxticketno() + ","); // 凭证号码
			str.append(maindto.getSpayeracct() + ","); // 付款账号
			str.append(maindto.getSpayername() + ","); // 付款人名称
			str.append(maindto.getSpayeraddr() + ","); // 付款人地址
			str.append(maindto.getNmoney() + ","); // 发生额(#.00)
			str.append(maindto.getSrecacct() + ","); // 收款人账号
			str.append(maindto.getSrecname() + ","); // 收款人名称
			str.append(maindto.getSrecbankno() + ","); // 收款人开户行行号
			str.append(maindto.getSbudgetunitcode() + ","); // 预算单位代码
			str.append(maindto.getSunitcodename() + ","); // 预算单位名称
			str.append(maindto.getStrimflag() + ","); // 整理期标志
			str.append(maindto.getSofyear() + ","); // 所属年度
			str.append(maindto.getSbudgettype() + ","); // 预算种类代码
			str.append(maindto.getSorivouno() + ","); // 原支出凭证编号
			str.append(maindto.getSorivoudate() + ","); // 原支出凭证日期
			str.append(maindto.getSbackflag() + ","); // 退回标志
			str.append(maindto.getSinputrecbankno() + ","); // 补录的行号
			str.append(maindto.getSinputrecbankname() + ","); // 补录的行名
			str.append(maindto.getScheckstatus()+ ","); // 补录复核状态
			str.append(maindto.getSinputusercode()+ ","); // 补录人
			str.append(maindto.getSchecksercode()+ ","); // 复核人

			String sStr = maindto.getSpaysummaryname();
			if(sStr != null && !sStr.trim().equals("")){
				if(sStr.contains(",")){
					sStr = sStr.replace(",", "，");
				}
			}
			str.append(returnValue(sStr) + ","); // 用途
			String tStr1 = maindto.getSaddword();
			if(tStr1 != null && !tStr1.trim().equals("")){
				if(tStr1.contains(",")){
					tStr1 = tStr1.replace(",", "，");
				}
			}
			str.append(tStr1 + ","); //附言
			
			String tStr2 = maindto.getSdemo();
			if(tStr2 != null && !tStr2.trim().equals("")){
				if(tStr2.contains(",")){
					tStr2 = tStr2.replace(",", "，");
				}
			}
			str.append(tStr2 + ","); //备注
			if (os.indexOf("Win") >= 0)  {
				str.append("\r\n"); //回车换行符
			}else{
				str.append("\n"); //换行符
			}
			for (int i = 0; i < subList.size(); i++) {
				subdto = (TvPayoutmsgsubDto) subList.get(i);
				// 明细开头#*
				str.append("#*");
				str.append(subdto.getSbizno()+ ",");//凭证流水号
				str.append(subdto.getSseqno()+ ",");// 明细序号
				str.append(subdto.getSfunsubjectcode() + ","); // 功能科目代码
				str.append(subdto.getSecnomicsubjectcode() + ",");//经济科目代码
				str.append(subdto.getNmoney()); // 支付金额
				if (os.indexOf("Win") >= 0)  {
					str.append("\r\n"); //回车换行符
				}else{
					str.append("\n"); //换行符
				}
			}
		} else if (MsgConstant.VOUCHER_NO_5209.equals(vtCode)) {
			TvDwbkDto tvDwbkDto = (TvDwbkDto) mainList.get(0);
			str.append("**");
			str.append(tvDwbkDto.getIvousrlno()+ ","); //凭证流水号
			str.append(tvDwbkDto.getSelecvouno() + ","); // 电子凭证编号
			str.append(tvDwbkDto.getSdwbkvoucode() + ","); // 退库凭证编号 
			str.append(tvDwbkDto.getStaxorgcode() + ","); // 征收机关代码,
			str.append(tvDwbkDto.getSagenttaxorgcode() + ","); // 代征征收机关代码,
			str.append(tvDwbkDto.getSpayertrecode() + ","); // 收款国库代码 
			str.append(tvDwbkDto.getSaimtrecode() + ","); //目的国库代码
			str.append(tvDwbkDto.getCbdgkind() + ","); // 预算种类,
			str.append(tvDwbkDto.getCbdglevel() + ","); // 预算级次,
			str.append(tvDwbkDto.getSbdgsbtcode() + ","); // 科目代码
			str.append(tvDwbkDto.getSdwbkreasoncode()+",");//	退库原因代码
			str.append(tvDwbkDto.getSastflag()+",");//辅助标志
			str.append(tvDwbkDto.getFamt()+",");//金额
			str.append(tvDwbkDto.getCbckflag()+",");//退回标志
			str.append(tvDwbkDto.getSpayeecode().trim() + ","); // 收款单位代码
			str.append(tvDwbkDto.getSpayeeacct() + ","); // 收款账号,
			str.append(tvDwbkDto.getSpayeename() + ","); // 收款人名称,
			str.append(tvDwbkDto.getSpayeeopnbnkno() + ","); // 收款行行号
			str.append(tvDwbkDto.getDaccept() + ","); // 受理日期
			str.append(tvDwbkDto.getDvoucher() + ","); // 开票日期
			str.append(tvDwbkDto.getDbill() + ","); // 凭证日期
			str.append(tvDwbkDto.getCtrimflag() + ","); // 调整期标志
			str.append(tvDwbkDto.getSbiztype() + ","); // 业务类型
			str.append(tvDwbkDto.getSdealno() + ","); // 交易流水号
			str.append(tvDwbkDto.getSinputrecbankname() + ","); // 支付系统行名匹配
			str.append(tvDwbkDto.getSinputrecbankno() + ",");//匹配支付系统行号1
			String sStr = tvDwbkDto.getSdemo();
			if(sStr != null && !sStr.trim().equals("")){
				if(sStr.contains(",")){
					sStr = sStr.replace(",", "，");
				}
			}
			str.append(returnValue(sStr) + ","); // 备注
			if (os.indexOf("Win") >= 0)  {
				str.append("\r\n"); //回车换行符
			}else{
				str.append("\n"); //换行符
			}
		} else if(MsgConstant.VOUCHER_NO_3210.equals(vtCode)){
			TvDwbkDto tvDwbkDto = (TvDwbkDto) mainList.get(0);
			str.append("**");
			str.append(tvDwbkDto.getIvousrlno() + ","); // 交易流水号 ,
			str.append(tvDwbkDto.getSdwbkvoucode().trim() + ","); // 原凭证号码,
			str.append(DateUtil.date2String2(tvDwbkDto.getDaccept()).trim() + ","); // 委托日期,
			str.append(tvDwbkDto.getSpayertrecode().trim() + ","); // 国库代码,
			str.append(DateUtil.date2String2(tvDwbkDto.getDvoucher()).trim()  + ","); // 凭证日期,
			str.append(tvDwbkDto.getSpayacctno().trim() + ","); // 付款账户,
			str.append(tvDwbkDto.getSpayacctname().trim() + ","); //付款人名称,
			str.append(","); // 付款人地址,
			str.append(tvDwbkDto.getFamt() + ","); // 金额,
			str.append(tvDwbkDto.getXagentbusinessno().trim() + ","); // 付款行行号,
			str.append(tvDwbkDto.getSpayeeopnbnkno().trim() + ","); // 收款行行号,
			str.append(tvDwbkDto.getSpayeeacct().trim() + ","); // 收款账户,
			str.append(tvDwbkDto.getSpayeename().trim() + ","); // 收款人名称,
			str.append(tvDwbkDto.getSreturnreasonname().trim() + ","); // 具体退库退回的原因
			str.append(MsgConstant.VOUCHER_NO_3210 + ","); // 附言
			str.append(tvDwbkDto.getStaxorgcode() + ","); // 征收机关
			str.append(tvDwbkDto.getSbdgsbtcode() + ","); // 科目代码
			str.append(tvDwbkDto.getCbdgkind() + ","); //预算种类
			str.append(tvDwbkDto.getCbdglevel() + ","); //预算级次
			str.append(tvDwbkDto.getCtrimflag() + ","); //调整期标志
			str.append(tvDwbkDto.getShold1() + ","); //辅助标志
			str.append(tvDwbkDto.getSdwbkreasoncode().trim()); // 退款原因代码 cyg20161019日增加
		} else {
			throw new ITFEBizException("读取凭证参数错误！");
		}
	}

	/**
	 * 校验接口参数synchronousVoucherStatus
	 * 
	 * @param admDivCode
	 * @param vtCode
	 * @param treCode
	 * @param vouDate
	 * @param vouNo
	 */
	public static String verifySynchronousVoucherStatusParam(String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo) {
		String str = verifyConfirmVoucherParam(admDivCode, vtCode, treCode,
				vouDate, vouNo);
		if (StringUtils.isNotBlank(str)) {
			return str;
		}
		return null;
	}

	/**
	 * 校验接口参数sendVoucher
	 * 
	 * @param admDivCode
	 * @param vtCode
	 * @param treCode
	 * @param vouDate
	 * @param vouNo
	 */
	public static String verifySendVoucherParam(String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo) {
		String str = verifyConfirmVoucherParam(admDivCode, vtCode, treCode,
				vouDate, vouNo);
		if (StringUtils.isNotBlank(str)) {
			return str;
		}
		return null;
	}

	/**
	 * 校验接口参数returnVoucherBack
	 * 
	 * @param admDivCode
	 * @param vtCode
	 * @param treCode
	 * @param vouDate
	 * @param vouNo
	 * @param errMsg
	 */
	public static String verifyReturnVoucherBackParam(String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo,
			String errMsg) {
		String str = verifyConfirmVoucherParam(admDivCode, vtCode, treCode,
				vouDate, vouNo);
		if (StringUtils.isNotBlank(str)) {
			return str;
		} else if (StringUtils.isBlank(errMsg)) {
			return "退回失败信息不能为空";
		}
		return null;
	}

	/**
	 * 校验接口参数confirmVoucher
	 * 
	 * @param admDivCode
	 * @param vtCode
	 * @param treCode
	 * @param vouDate
	 * @param vouNo
	 */
	public static String verifyConfirmVoucherParam(String admDivCode,
			String vtCode, String treCode, String vouDate, String vouNo) {
		String str = verifyReadVoucherParam(admDivCode, vtCode, treCode,
				vouDate);
		if (StringUtils.isNotBlank(str)) {
			return str;
		} else if (StringUtils.isBlank(vouNo)) {
			return "凭证编号不能为空";
		}
		return null;
	}

	/**
	 * 校验接口参数信息readVoucher
	 * 
	 * @param admDivCode
	 * @param vtCode
	 * @param treCode
	 * @param vouDate
	 */
	public static String verifyReadVoucherParam(String admDivCode,
			String vtCode, String treCode, String vouDate) {
		// if (StringUtils.isBlank(admDivCode)) {
		// return "区划代码不能为空";
		// } else
		if (StringUtils.isBlank(vtCode)) {
			return "凭证编号不能为空";
		} else if (StringUtils.isBlank(treCode)) {
			return "国库代码不能为空";
		} else if (StringUtils.isBlank(vouDate)) {
			return "凭证日期不能为空";
		}
		return null;
	}

	/**
	 * 查询凭证信息
	 * 
	 * @param treCode
	 * @param vtCode
	 * @param vouDate
	 * @param vouNo
	 * @param admDivCode
	 * @return
	 * @throws ITFEBizException
	 */
	public static TvVoucherinfoDto getTvVoucherinfoDto(String treCode,
			String vtCode, String vouDate, String vouNo, String admDivCode)
			throws ITFEBizException {
		try {
			TvVoucherinfoDto tvVoucherinfoDto = new TvVoucherinfoDto();
			tvVoucherinfoDto.setStrecode(treCode);
			tvVoucherinfoDto.setSvtcode(vtCode);
			tvVoucherinfoDto.setScreatdate(vouDate);
			tvVoucherinfoDto.setSstatus(TwcsDealCodeConstants.VOUCHER_ACCEPT_RETURNVOUCHER);
			List<TvVoucherinfoDto> lists = CommonFacade.getODB().findRsByDto(
					tvVoucherinfoDto);
			if (null == lists || lists.size() == 0) {
				logger.error("未找到[区划代码:" + admDivCode + "凭证编号:" + vouNo
						+ "凭证日期:" + vouDate + "]凭证信息！");
			}
			return lists.get(0);
		} catch (JAFDatabaseException e) {
			logger.error("TBSVerifyUtils.getTvVoucherinfoDto[" + e.getMessage()
					+ "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (ValidateException e) {
			logger.error("TBSVerifyUtils.getTvVoucherinfoDto[" + e.getMessage()
					+ "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * 查询凭证信息列表
	 * 
	 * @param treCode
	 * @param vtCode
	 * @param vouDate
	 * @param vouNo
	 * @param admDivCode
	 * @return
	 * @throws ITFEBizException
	 */
	public static List getTvVoucherinfoList(String treCode,
			String vtCode, String vouDate, String admDivCode)
			throws ITFEBizException {
		try {
			TvVoucherinfoDto tvVoucherinfoDto = new TvVoucherinfoDto();
			tvVoucherinfoDto.setStrecode(treCode);
			tvVoucherinfoDto.setSvtcode(vtCode);
			tvVoucherinfoDto.setScreatdate(vouDate);
			tvVoucherinfoDto.setSext3(TwcsDealCodeConstants.VOUCHER_ACCEPT_RETURNVOUCHER);
			List<TvVoucherinfoDto> lists = CommonFacade.getODB().findRsByDto(
					tvVoucherinfoDto);
			if (null == lists || lists.size() == 0) {
				logger.error("未找到[区划代码:" + admDivCode + "凭证类型:" + vtCode
						+ "凭证日期:" + vouDate + "]凭证信息！");
			}
			return lists;
		} catch (JAFDatabaseException e) {
			logger.error("TBSVerifyUtils.getTvVoucherinfoDto[" + e.getMessage()
					+ "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (ValidateException e) {
			logger.error("TBSVerifyUtils.getTvVoucherinfoDto[" + e.getMessage()
					+ "]", e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}
	
	

	/**
	 * 根据组织机构代码、区划代码、凭证类型、状态查找签章位置参数
	 * 
	 * @param voucherDto
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	private static List findStamppositionDto(TvVoucherinfoDto voucherDto)
			throws JAFDatabaseException, ValidateException {
		List params = new ArrayList();
		params.add(voucherDto.getSorgcode());
		params.add(voucherDto.getSadmdivcode());
		params.add(voucherDto.getSvtcode());
		params.add("rh_" + voucherDto.getSvtcode() + "_qm");
		return DatabaseFacade
				.getODB()
				.findWithUR(
						TsStamppositionDto.class,
						" WHERE S_ORGCODE = ? AND S_ADMDIVCODE = ? AND S_VTCODE = ? AND S_STAMPPOSITION != ? ",
						params);
	}

	/**
	 * 根据组织机构代码、国库代码查找国库
	 * 
	 * @param voucherDto
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public static List findTsTreasuryDto(TvVoucherinfoDto voucherDto)
			throws JAFDatabaseException, ValidateException {
		TsTreasuryDto dto = new TsTreasuryDto();
		dto.setSorgcode(voucherDto.getSorgcode());
		dto.setStrecode(voucherDto.getStrecode());
		return CommonFacade.getODB().findRsByDto(dto);
	}

	/**
	 * 查找未签章的签章类型
	 * 
	 * @param list
	 * @param dto
	 * @return
	 */
	private static List findStampTypeDtoList(List list, TvVoucherinfoDto dto) {
		List stampList = new ArrayList();
		stampList.addAll(list);
		if (!StringUtils.isBlank(dto.getSstampid())) {
			String[] stampids = dto.getSstampid().split(",");
			HashSet<TsStamppositionDto> set = new HashSet<TsStamppositionDto>();
			for (int i = 0; i < stampids.length; i++) {
				for (TsStamppositionDto stampDto : (List<TsStamppositionDto>) stampList) {
					if (stampDto.getSstamptype().equals(stampids[i]))
						set.add(stampDto);
				}
			}
			stampList.removeAll(set);
		}
		return stampList;
	}

	/**
	 * 根据签章顺序大小对List集合排序
	 * 
	 * @param list
	 * @return
	 */
	public static List sort(List list) {
		Collections.sort(list, new Comparator<TsStamppositionDto>() {
			public int compare(TsStamppositionDto dto1, TsStamppositionDto dto2) {
				return Integer.parseInt(dto1.getSstampsequence())
						- Integer.parseInt(dto2.getSstampsequence());
			}
		});
		return list;
	}

	/**
	 * 凭证签章
	 * 
	 * @param voucher
	 * @param size
	 * @param stampPositionList
	 * @param strecodeList
	 * @param dto
	 * @throws ITFEBizException
	 * @throws NumberFormatException
	 */
	public static String voucherStamp(Voucher voucher, int size,
			List stampPositionList, List strecodeList, TvVoucherinfoDto dto,
			TsUsersDto tsUsersDto) throws NumberFormatException,
			ITFEBizException {
		if (stampPositionList == null || stampPositionList.size() == 0)
			throw new ITFEBizException("签章位置未维护");
		int sequence = 0;
		boolean flag = false;
		String str = null;
		for (int i = 0; i < stampPositionList.size(); i++) {
			TsStamppositionDto stampDto = (TsStamppositionDto) stampPositionList
					.get(i);
			if (flag
					&& sequence < Integer
							.parseInt(stampDto.getSstampsequence()))
				continue;
			if (verifyStamp(stampDto, strecodeList)) {
				sequence = Integer.parseInt(stampDto.getSstampsequence());
				flag = true;
				continue;
			}
			str = voucherStamp(voucher, size, stampDto,
					(TsTreasuryDto) strecodeList.get(0), dto, tsUsersDto);
			if (StringUtils.isBlank(str)) {
				continue;
			} else {
				return str;
			}
		}
		return null;
	}

	/**
	 * 校验国库参数、签章ID、证书ID、默认签章用户
	 * 
	 * @param stampDto
	 * @param strecodeList
	 * @return
	 * @throws ITFEBizException
	 */
	private static boolean verifyStamp(TsStamppositionDto stampDto,
			List strecodeList) throws ITFEBizException {
		boolean flag = true;
		if (stampDto.getSstamptype().equals(MsgConstant.VOUCHERSAMP_ROTARY)
				|| stampDto.getSstamptype().equals(
						MsgConstant.VOUCHERSAMP_OFFICIAL)) {
			if (strecodeList == null || strecodeList.size() == 0)
				throw new ITFEBizException("组织机构代码" + stampDto.getSorgcode()
						+ " 国库主体代码" + stampDto.getStrecode()
						+ "在[国库主体信息参数维护]中不存在！" + "    errorCodeID:"
						+ System.currentTimeMillis());
			else if ((stampDto.getSstamptype().equals(
					MsgConstant.VOUCHERSAMP_ROTARY) && StringUtils
					.isBlank(((TsTreasuryDto) strecodeList.get(0))
							.getSrotarycertid()))
					|| (stampDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_OFFICIAL) && StringUtils
							.isBlank(((TsTreasuryDto) strecodeList.get(0))
									.getScertid())))
				throw new ITFEBizException("国库主体代码" + stampDto.getStrecode()
						+ "在[国库主体信息参数维护]中 " + stampDto.getSstampname()
						+ " 证书ID参数未维护！ " + "    errorCodeID:"
						+ System.currentTimeMillis());
			else if ((stampDto.getSstamptype().equals(
					MsgConstant.VOUCHERSAMP_ROTARY) && StringUtils
					.isBlank(((TsTreasuryDto) strecodeList.get(0))
							.getSrotaryid()))
					|| (stampDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_OFFICIAL) && StringUtils
							.isBlank(((TsTreasuryDto) strecodeList.get(0))
									.getSstampid())))
				throw new ITFEBizException("国库主体代码" + stampDto.getStrecode()
						+ "在[国库主体信息参数维护]中 " + stampDto.getSstampname()
						+ " 签章ID参数未维护！ " + "    errorCodeID:"
						+ System.currentTimeMillis());
			else {
				return false;
			}

		}
		throw new ITFEBizException("暂时不支持私章操作" + "    errorCodeID:"
				+ System.currentTimeMillis());

		// else{
		// if(StringUtils.isBlank(stampDto.getSstampuser()))
		// throw new ITFEBizException("私章："+stampDto.getSstampname()+
		// " 对应的默认签章用户在[签章位置参数维护]中未维护！ ");
		// else{
		// userDto.setSusercode(stampDto.getSstampuser());
		// userDto.setSorgcode(stampDto.getSorgcode());
		// try {
		// List<TsUsersDto>
		// usersDtoList=CommonFacade.getODB().findRsByDto(userDto);
		// if(usersDtoList==null||usersDtoList.size()==0)
		// throw new
		// ITFEBizException("私章："+stampDto.getSstampname()+" 对应的默认签章用户"+
		// " 用户代码："+userDto.getSusercode()+" 在[用户信息维护]中不存在！");
		// else{
		// userDto=usersDtoList.get(0);
		// if(StringUtils.isBlank(userDto.getScertid()))
		// throw new
		// ITFEBizException("私章："+stampDto.getSstampname()+" 对应的默认签章用户"+
		// " 用户姓名："+userDto.getSusername()+" 用户代码："+userDto.getSusercode()+"在[用户信息维护]中的证书ID参数未维护！");
		// else if(StringUtils.isBlank(userDto.getSstampid()))
		// throw new
		// ITFEBizException("私章："+stampDto.getSstampname()+" 对应的默认签章用户"+
		// " 用户姓名："+userDto.getSusername()+" 用户代码："+userDto.getSusercode()+"在[用户信息维护]中的签章ID参数未维护！");
		// else
		// return false;
		// }
		// } catch (Exception e) {
		// logger.error(e);
		// VoucherException.saveErrInfo(stampDto.getSvtcode(),e.getMessage());
		// }
		// }
		// }
	}

	/**
	 * 凭证签章
	 * 
	 * @param voucher
	 * @param size
	 * @param stampDto
	 * @param strecodeDto
	 * @param dto
	 * @throws ITFEBizException
	 */
	private static String voucherStamp(Voucher voucher, int size,
			TsStamppositionDto stampDto, TsTreasuryDto strecodeDto,
			TvVoucherinfoDto dto, TsUsersDto tsUsersDto)
			throws ITFEBizException {
		List<List> lists = new ArrayList<List>();
		List list = new ArrayList();
		List vList = new ArrayList();
		List sinList = new ArrayList();
		vList.add(sinList);
		sinList.add(dto);
		list.add(tsUsersDto);
		list.add(strecodeDto);
		list.add(stampDto);
		list.add(size);
		list.add(vList);
		lists.add(list);
		try {
			return voucherStamp(lists);
		} catch (Exception e) {
			logger.error(e);
			VoucherException.saveErrInfo(stampDto.getSvtcode(), e.getMessage());
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	/**
	 * 凭证签章
	 * 
	 * @throws ITFEBizException
	 * 
	 */
	private static String voucherStamp(List lists) throws ITFEBizException {
		VoucherService voucherService = new VoucherService();
		String stampXML = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		for (List list : (List<List>) lists) {
			// 签章用户信息
			TsUsersDto uDto = (TsUsersDto) list.get(0);
			// 国库信息
			TsTreasuryDto tDto = (TsTreasuryDto) list.get(1);
			// 签章位置信息
			TsStamppositionDto sDto = (TsStamppositionDto) list.get(2);
			int size = Integer.parseInt(list.get(3) + "");
			List vList = (List) list.get(4);
			for (List sinList : (List<List>) vList) {
				String err = null;
				vDto = (TvVoucherinfoDto) sinList.get(0);
				String certID = "";
				String stampID = "";
				if (sDto.getSstamptype().equals(
						MsgConstant.VOUCHERSAMP_OFFICIAL)) {
					certID = tDto.getScertid();
					stampID = tDto.getSstampid();
				} else if (sDto.getSstamptype().equals(
						MsgConstant.VOUCHERSAMP_ROTARY)) {
					if (vDto.getSstatus().equals(
							DealCodeConstants.VOUCHER_SUCCESS_BACK)) {
						certID = tDto.getSattachcertid();
						stampID = tDto.getSattachid();
						size = 1;
					} else {
						certID = tDto.getSrotarycertid();
						stampID = tDto.getSrotaryid();
					}
				} else if (sDto.getSstamptype().equals(
						MsgConstant.VOUCHERSAMP_ATTACH)) {
					certID = tDto.getSattachcertid();
					stampID = tDto.getSattachid();
				} else {
					certID = uDto.getScertid();
					stampID = uDto.getSstampid();
				}
				if (sinList.size() == 1) {
					// 服务器签章
					err = voucherService.signStampByNos(certID, vDto.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),vDto.getSvtcode(), 
							getVoucherXML(vDto,
									(MsgConstant.VOUCHER_NO_5209.equals(vDto.getSvtcode()) || MsgConstant.VOUCHER_NO_3209.equals(vDto.getSvtcode()))?true:false,
									(MsgConstant.VOUCHER_NO_5106.equals(vDto.getSvtcode()) || MsgConstant.VOUCHER_NO_5108.equals(vDto.getSvtcode()) 
											||MsgConstant.VOUCHER_NO_5207.equals(vDto.getSvtcode()) ) ? true : false).getBytes(),
							VoucherUtil.getStampPotisionXML(sDto.getSstampposition(), stampID), vDto.getSvoucherno(), sDto.getSstampname());
				}
				// 签章成功
				if (StringUtils.isBlank(err)) {
					return null;
				} else {
					logger.error(err);
					return err;
				}
			}
		}
		return null;
	}

	/**
	 * 接收回单 更新X字段信息
	 * 
	 * @param list
	 * @return
	 * @throws ITFEBizException
	 */
	public static boolean acceptReturnVoucer(String xml)
			throws ITFEBizException {
		try {
			String StYear, VtCode = null;
			if (StringUtils.isBlank(xml)) {
				throw new ITFEBizException("接收参数有误！");
			}
			Document fxrDoc = DocumentHelper.parseText(xml);
			List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
					"VoucherBody");
			// 获取行政区划代码、年度和凭证类型
			if (VoucherBodyList.size() > 0) {
				Element element = (Element) VoucherBodyList.get(0);
				StYear = element.attribute("StYear").getText();
				VtCode = element.attribute("VtCode").getText();
			}

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// catch (Exception e) {
		// logger.error("TBSVerifyUtils.acceptReturnVoucer[" + e.getMessage()
		// + "]", e);
		// throw new ITFEBizException(e.getMessage(), e);
		// }

		return Boolean.TRUE;
	}

	public static void acceptVoucherXml(String xml) {
	}

	public static String verifyNodeStatus(String orgCode)
			throws ITFEBizException {
		try {
			if (StringUtils.isBlank(orgCode)) {
				return "节点代码不能为空";
			}
			TsTbsorgstatusDto tsTbsorgstatusDto = new TsTbsorgstatusDto();
			tsTbsorgstatusDto
					.setSorgcode(orgCode.substring(0, 3) + "000000000");
			List resultList = CommonFacade.getODB().findRsByDto(
					tsTbsorgstatusDto);
			if (null == resultList || resultList.size() == 0) {
				return "节点信息未找到";
			}
			tsTbsorgstatusDto = (TsTbsorgstatusDto) resultList.get(0);
			if (!("0".equals(tsTbsorgstatusDto.getSstatus()))) {
				return "节点不是登陆状态";
			}
		} catch (JAFDatabaseException e) {
			logger.error("TBSVerifyUtils.verifyStatus[" + e.getMessage() + "]",
					e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (ValidateException e) {
			logger.error("TBSVerifyUtils.verifyStatus[" + e.getMessage() + "]",
					e);
			throw new ITFEBizException(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 解析报文内容
	 * 
	 * @param voucherType
	 * @param fileContents
	 * @return
	 * @throws ITFEBizException
	 */
	public static String jxVoucherByType(String voucherType,
			String[] fileContents) throws ITFEBizException {
		if (MsgConstant.VOUCHER_NO_3208.equals(voucherType)) {
			return genVoucherInfoBy3208(voucherType, fileContents);
		} else if (MsgConstant.VOUCHER_NO_5209.equals(voucherType)) {
			return genVoucherInfoBy5209(voucherType, fileContents);
		}
		return null;
	}

	/**
	 * 生成3208报文
	 * 
	 * @param voucherType
	 * @param fileContents
	 * @return
	 * @throws ITFEBizException
	 */
	private static String genVoucherInfoBy3208(String voucherType,
			String[] fileContents) throws ITFEBizException {
		try {
			TvVoucherinfoDto tvVoucherinfoDto = null;
			String[] fileLine = null;
			String[] mainInfo = null;
			String[] subInfo = null;
			List resultDto = new ArrayList();
			for (String str : fileContents) {
				if (StringUtils.isBlank(str))
					continue;
				fileLine = str.split("\\#\\*");
				mainInfo = fileLine[0].split(",");
				// 主单字段个数
				if (mainInfo.length != 24) {
					return "主单信息有误";
				}
				// 根据国库代码查询机构代码和区划代码
				TsConvertfinorgDto searchFinInfo = new TsConvertfinorgDto();
				searchFinInfo.setStrecode(mainInfo[0]);
				searchFinInfo = (TsConvertfinorgDto) CommonFacade.getODB()
						.findRsByDto(searchFinInfo).get(0);
				// 生成索引表信息
				tvVoucherinfoDto = new TvVoucherinfoDto();
				tvVoucherinfoDto.setSdealno(VoucherUtil.getGrantSequence());
				tvVoucherinfoDto.setStrecode(searchFinInfo.getStrecode());
				tvVoucherinfoDto.setSadmdivcode(searchFinInfo.getSadmdivcode());
				tvVoucherinfoDto.setSorgcode(searchFinInfo.getSorgcode());
				tvVoucherinfoDto.setSstyear(mainInfo[1]);
				tvVoucherinfoDto.setSvtcode(MsgConstant.VOUCHER_NO_3208);
				tvVoucherinfoDto.setSvoucherno(mainInfo[3]);
				tvVoucherinfoDto.setSvoucherflag("1");
				tvVoucherinfoDto.setScreatdate(TimeFacade
						.getCurrentStringTime()); // 接收日期
				tvVoucherinfoDto.setSext4(mainInfo[2]); // 凭证日期
				tvVoucherinfoDto.setNmoney(new BigDecimal(mainInfo[23]));
				tvVoucherinfoDto.setSstatus("80");
				tvVoucherinfoDto.setSext1("20");
				tvVoucherinfoDto.setSfilename(ITFECommonConstant.FILE_ROOT_PATH
						+ "Voucher" + File.separator
						+ tvVoucherinfoDto.getScreatdate() + File.separator
						+ "send" + tvVoucherinfoDto.getSvtcode() + "_"
						+ tvVoucherinfoDto.getSdealno() + ".msg");

				HashMap<String, Object> map = new HashMap<String, Object>();
				HashMap<String, Object> vouchermap = new HashMap<String, Object>();
				// 设置报文节点 Voucher
				map.put("Voucher", vouchermap);
				// 设置报文消息体
				vouchermap.put("Id", tvVoucherinfoDto.getSdealno());// 实拨退款通知书Id
				vouchermap.put("AdmDivCode", tvVoucherinfoDto.getSadmdivcode());// 行政区划代码
				vouchermap.put("StYear", tvVoucherinfoDto.getSstyear());// 业务年度
				vouchermap.put("VtCode", tvVoucherinfoDto.getSvtcode());// 凭证类型编号
				vouchermap.put("VouDate", tvVoucherinfoDto.getScreatdate());// 凭证日期
				vouchermap.put("VoucherNo", tvVoucherinfoDto.getSvoucherno());// 凭证号
				vouchermap.put("AgentBusinessNo", returnValue(mainInfo[4]));// 原银行交易流水号
				vouchermap.put("OriBillNo", returnValue(mainInfo[5]));// 原拨款单单号
				vouchermap.put("OriVouDate", returnValue(mainInfo[6]));// 原拨款单凭证日期
				vouchermap.put("OriPayDate", returnValue(mainInfo[7]));// 原支付日期
				vouchermap.put("FundTypeCode", returnValue(mainInfo[8]));// 资金性质编码
				vouchermap.put("FundTypeName", returnValue(mainInfo[9]));// 资金性质名称
				vouchermap.put("PayTypeCode", returnValue(mainInfo[10]));// 支付方式编码
				vouchermap.put("PayTypeName", returnValue(mainInfo[11]));// 支付方式名称
				vouchermap.put("ProCatCode", returnValue(""));// 收支管理编码
				vouchermap.put("ProCatName", returnValue(""));// 收支管理名称
				vouchermap.put("PayAcctNo", returnValue(mainInfo[14]));// 付款人账号
				vouchermap.put("PayAcctName", returnValue(mainInfo[15]));// 付款人名称
				vouchermap.put("PayAcctBankName", returnValue(mainInfo[16]));// 付款人银行
				vouchermap.put("PayeeAcctNo", returnValue(mainInfo[17]));// 收款人账号
				vouchermap.put("PayeeAcctName", returnValue(mainInfo[18]));// 收款人名称
				vouchermap.put("PayeeAcctBankName", returnValue(mainInfo[19]));// 收款人银行
				vouchermap.put("PayeeAcctBankNo", returnValue(mainInfo[20]));// 收款银行行号
				vouchermap.put("PaySummaryCode", returnValue(mainInfo[21]));// 用途编码
				vouchermap.put("PaySummaryName", returnValue(mainInfo[22]));// 用途名称
				vouchermap.put("PayAmt", new BigDecimal(
						returnValue(mainInfo[23])));// 汇总退款金额
				vouchermap.put("Remark", ""); // 备注信息
				vouchermap.put("Hold1", "");// 预留字段1附言占用"000043100019".equals(ITFECommonConstant.SRC_NODE)?dto.getSdemo():""
				vouchermap.put("Hold2", "");// 预留字段2

				List<Object> DetailList = new ArrayList<Object>();
				List<Object> Detail = new ArrayList<Object>();
				HashMap<String, Object> Detailmap = null;
				for (int i = 1; i < fileLine.length; i++) {
					subInfo = fileLine[i].split(",");
					if (subInfo.length != 11) {
						return "凭证编号:" + tvVoucherinfoDto.getSvoucherno()
								+ "明细单有误";
					}
					Detailmap = new HashMap<String, Object>();
					Detailmap.put("Id", tvVoucherinfoDto.getSdealno()); // 退款通知书明细编号
					Detailmap.put("VoucherBillId", tvVoucherinfoDto
							.getSdealno()); // 退款通知书Id
					Detailmap.put("BgtTypeCode", returnValue(subInfo[0])); // 预算类型编码
					Detailmap.put("BgtTypeName", returnValue(subInfo[1])); // 预算类型名称
					Detailmap.put("ProCatCode", returnValue(subInfo[2])); // 收支管理编码
					Detailmap.put("ProCatName", returnValue(subInfo[3])); // 收支管理名称
					Detailmap.put("AgencyCode", returnValue(subInfo[4])); // 预算单位编码
					Detailmap.put("AgencyName", returnValue(subInfo[5])); // 预算单位名称
					Detailmap.put("ExpFuncCode", returnValue(subInfo[6])); // 支出功能分类科目编码
					Detailmap.put("ExpFuncName", returnValue(subInfo[7])); // 支出功能分类科目名称
					Detailmap.put("ExpEcoCode", returnValue(subInfo[8])); // 支出经济分类科目编码
					Detailmap.put("ExpEcoName", returnValue(subInfo[9])); // 支出经济分类科目名称
					Detailmap.put("Amt", MtoCodeTrans
							.transformString(returnValue(subInfo[10]))); // 退款金额
					Detailmap.put("Hold1", returnValue("")); // 预留字段1
					Detailmap.put("Hold2", returnValue("")); // 预留字段2
					Detailmap.put("Hold3", returnValue("")); // 预留字段3
					Detailmap.put("Hold4", returnValue("")); // 预留字段4
					Detail.add(Detailmap);
				}
				subInfo = null;
				HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
				DetailListmap.put("Detail", Detail);
				DetailList.add(DetailListmap);
				vouchermap.put("DetailList", DetailList);
				// 生成凭证xml信息
				VoucherUtil.sendTips(tvVoucherinfoDto, map);
				resultDto.add(tvVoucherinfoDto);
			}
			DatabaseFacade.getODB().create(CommonUtil.listTArray(resultDto));
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("生成实拨退款凭证3208失败", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("生成实拨退款凭证3208失败", e);
		}
		return null;
	}

	private static String genVoucherInfoBy5209(String voucherType,
			String[] fileContents) throws ITFEBizException {
		TvVoucherinfoDto tvVoucherinfoDto = null;
		String[] mainInfo = null;
		SQLExecutor sqlExecutor = null;
		try {
			String str = fileContents[0];
			mainInfo = str.split("\\|");
			// 根据国库代码查询机构代码和区划代码
			TsConvertfinorgDto searchFinInfo = new TsConvertfinorgDto();
			searchFinInfo.setStrecode(mainInfo[3]);
			searchFinInfo.setSorgcode(mainInfo[3]+"04");//

			searchFinInfo = (TsConvertfinorgDto) CommonFacade.getODB()
					.findRsByDto(searchFinInfo).get(0);
			// 生成索引表信息
			tvVoucherinfoDto = new TvVoucherinfoDto();
			tvVoucherinfoDto.setSdealno(VoucherUtil.getGrantSequence());
			tvVoucherinfoDto.setStrecode(searchFinInfo.getStrecode());
			tvVoucherinfoDto.setSadmdivcode(searchFinInfo.getSadmdivcode());
			tvVoucherinfoDto.setSorgcode(searchFinInfo.getSorgcode());
			tvVoucherinfoDto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));
			tvVoucherinfoDto.setSvtcode(ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode01,") >= 0?MsgConstant.VOUCHER_NO_3209:MsgConstant.VOUCHER_NO_5209);
			tvVoucherinfoDto.setSvoucherno(mainInfo[0]);
			tvVoucherinfoDto.setSvoucherflag("1");
			tvVoucherinfoDto.setScreatdate(mainInfo[15]); // 接收日期
			tvVoucherinfoDto.setSext4(mainInfo[1]); // 凭证日期
			tvVoucherinfoDto.setNmoney(new BigDecimal(mainInfo[6]));
			tvVoucherinfoDto.setSstatus("80");
			tvVoucherinfoDto.setSext1("20");
			Calendar cal = Calendar.getInstance();
			tvVoucherinfoDto.setSrecvtime(new Timestamp(cal.getTimeInMillis()));
			tvVoucherinfoDto.setSpaybankcode(mainInfo[8]);//收款人开户行行号
			tvVoucherinfoDto.setSfilename(ITFECommonConstant.FILE_ROOT_PATH
					+ "Voucher" + File.separator
					+ tvVoucherinfoDto.getScreatdate() + File.separator
					+ "rev" + tvVoucherinfoDto.getSvtcode() + "_"
					+ tvVoucherinfoDto.getSdealno() + ".msg");
			FileUtil.getInstance().writeFile(tvVoucherinfoDto.getSfilename(), str);
			TvDwbkDto maindto = new TvDwbkDto();
			maindto.setIvousrlno(Long.parseLong(tvVoucherinfoDto.getSdealno())); // 凭证流水号
			maindto.setSbizno(tvVoucherinfoDto.getSdealno()); //
			maindto.setSexamorg(searchFinInfo.getSfinorgcode());	//财政机构代码
			maindto.setSdealno(tvVoucherinfoDto.getSdealno().substring(8,16)); // 交易流水号
			maindto.setSelecvouno(tvVoucherinfoDto.getSvoucherno()); // 电子凭证编号
			maindto.setSdwbkvoucode(tvVoucherinfoDto.getSvoucherno()); // 退库凭证号
			maindto.setSpayertrecode(tvVoucherinfoDto.getStrecode()); // 付款国库代码
			maindto.setSaimtrecode(tvVoucherinfoDto.getStrecode()); // 目的国库代码
			maindto.setCbckflag(StateConstant.MSG_BACK_FLAG_NO); // 退回标志
			maindto.setCbdgkind(MsgConstant.BDG_KIND_IN); // 预算种类
			maindto.setCtrimflag(MsgConstant.TIME_FLAG_NORMAL); // 调整期标志
			maindto.setSpackageno(""); // 包流水号
			maindto.setStaxorgcode(mainInfo[2]);	//征收机关代码
			maindto.setSfundtypecode(MsgConstant.BDG_KIND_IN);	//默认预算内
			maindto.setSfundtypename("预算内");	//默认预算内
			maindto.setSpayeeacct(returnValue(mainInfo[9])); // 收款人账号
			maindto.setSpayeename(returnValue(mainInfo[10]));	//收款人名称
			maindto.setSpayeecode("");	//收款人代码  预算单位代码
			maindto.setSrecbankname(returnValue(mainInfo[7]));// 收款人银行号
			maindto.setSpayeeopnbnkno(returnValue(mainInfo[8]));// 收款人开户行行号
			maindto.setFamt(tvVoucherinfoDto.getNmoney());
			maindto.setSbdgsbtcode(returnValue(mainInfo[12]));// 科目代码
			maindto.setSincomesortname(returnValue(mainInfo[12]));//收入分类科目名称
			maindto.setSpayeename(mainInfo[10]); // 收款人名称
			maindto.setDaccept(TimeFacade.getCurrentDateTime());
			maindto.setDvoucher(CommonUtil.strToDate(mainInfo[1]));
			maindto.setDacct(TimeFacade.getCurrentDateTime());
			maindto.setDbill(TimeFacade.getCurrentDateTime());
			maindto.setSbookorgcode(tvVoucherinfoDto.getSorgcode());
			maindto.setSfilename(tvVoucherinfoDto.getSfilename());
			maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);// 状态
			maindto.setSreturnreasonname(returnValue(mainInfo[11]));// 退回原因
			maindto.setSpayacctno(returnValue(mainInfo[4]));// 付款人账号
			maindto.setSpayacctname(returnValue(mainInfo[5]));// 付款人名称
			//厦门需要预算级次和共享分成辅助标识代码
			if(mainInfo.length >= 14){
				maindto.setSastflag(mainInfo[13]);
			}else{
				maindto.setSastflag("");
			}
			//厦门需要预算级次和共享分成辅助标识代码
			if(mainInfo.length >= 15){
				maindto.setCbdglevel(mainInfo[14]);
			}else{
				maindto.setCbdglevel("");
			}
			maindto.setSdwbkreasoncode(returnValue(mainInfo[11]));
			DatabaseFacade.getODB().create(maindto);
			DatabaseFacade.getODB().create(tvVoucherinfoDto);
			
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode01,") >= 0) { // 厦门发凭证库报文
				return genXiaMenVoucherInfoBy5209(tvVoucherinfoDto, maindto);
			}else{ //北京发MQ报文(发送退库报文到商业银行)
				MuleMessage message = new DefaultMuleMessage("");
				MuleClient client = new MuleClient();
				message.setStringProperty(MessagePropertyKeys.MSG_NO_KEY,
						"TBS_1000");
				message.setProperty(MessagePropertyKeys.MSG_DTO, tvVoucherinfoDto);
				// message.setProperty("orgCode", vDto.getSorgcode());
				message = client.send("vm://ManagerMsgWithCommBank", message);
				//查询报文是否成功发送出去，如果不成功的话，不会记TF_FUND_APPROPRIATION表
				String sql = "SELECT count(*) FROM TF_FUND_APPROPRIATION WHERE S_PACKNO = ? AND S_TRECODE = ? AND S_VOUNO = ? AND S_VOUDATE = ?";
				sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory()
				.getSQLExecutor();
				sqlExecutor.addParam(tvVoucherinfoDto.getSpackno());
				sqlExecutor.addParam(tvVoucherinfoDto.getStrecode());
				sqlExecutor.addParam(tvVoucherinfoDto.getSvoucherno());
				sqlExecutor.addParam(tvVoucherinfoDto.getSext4());
				SQLResults sqlResults = sqlExecutor.runQuery(sql);
				if (sqlResults.getInt(0, 0) == 0) {
					return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + "发送清算报文失败");
				} else {
					return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
				}
			}
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("生成退库退回5209凭证失败", e);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("生成退库退回5209凭证失败", e);
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("生成退库退回5209凭证失败", e);
		} catch (FileOperateException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("生成退库退回5209凭证失败", e);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("生成退库退回5209凭证失败", e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("生成退库退回5209凭证失败", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("生成退库退回5209凭证失败", e);
		}finally
		{
			if(null!=sqlExecutor)
				sqlExecutor.closeConnection();
		}
	}
	
	private static String genXiaMenVoucherInfoBy5209(TvVoucherinfoDto tvVoucherinfoDto,
			TvDwbkDto maindto) throws ITFEBizException {
		try {
			//生成xml文件
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", tvVoucherinfoDto.getSdealno());// 收入退付凭证Id
			vouchermap.put("AdmDivCode", tvVoucherinfoDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", tvVoucherinfoDto.getSstyear());// 业务年度
			vouchermap.put("VtCode", tvVoucherinfoDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", tvVoucherinfoDto.getSext4());// 凭证日期
			vouchermap.put("VoucherNo", tvVoucherinfoDto.getSvoucherno());// 凭证号
			vouchermap.put("TreCode", tvVoucherinfoDto.getStrecode());// 国库主体代码
			vouchermap.put("FinOrgCode", maindto.getSexamorg());// 财政机关代码
			vouchermap.put("TaxOrgCode", returnValue(maindto.getStaxorgcode()));
			vouchermap.put("TaxOrgName", "");// 财政机关代码
			//预算级次
			vouchermap.put("BudgetLevelCode", returnValue(maindto.getCbdglevel()));
			vouchermap.put("BudgetType", StateConstant.BudgetType_IN);	//预算内
			
			vouchermap.put("FundTypeCode", returnValue(maindto.getSfundtypecode()));// 资金性质编码
			vouchermap.put("FundTypeName", returnValue(maindto.getSfundtypename()));// 资金性质名称
			vouchermap.put("ClearBankCode", returnValue(maindto.getSclearbankcode()));// 人民银行编码
			vouchermap.put("ClearBankName", returnValue(maindto.getSclearbankname()));// 人民银行名称
			vouchermap.put("PayeeAcctNo", returnValue(maindto.getSpayeeacct()));// 收款人账号
			vouchermap.put("PayeeAcctName", returnValue(maindto.getSpayeename()));// 款人名称
			vouchermap.put("PayeeAcctBankName", "");// 收款人银行
			vouchermap.put("PayeeAcctBankNo", returnValue(maindto.getSpayeeopnbnkno()));// 收款银行行号
			
			vouchermap.put("PayAcctNo", returnValue(maindto.getSpayacctno()));// 付款人账号
			vouchermap.put("PayAcctName", returnValue(maindto.getSpayacctname()));// 付款人名称
			vouchermap.put("PayAcctBankName", returnValue(maindto.getSpayacctbankname()));// 付款人银行
			vouchermap.put("AgencyCode", "");// 预算单位编码
			vouchermap.put("AgencyName", "");// 预算单位名称
			vouchermap.put("nReturnReasonCode", returnValue(maindto.getSreturnreasonname()));// 退付原因
			vouchermap.put("nReturnReasonName", returnValue(maindto.getSreturnreasonname()));// 退付原因
			vouchermap.put("PayAmt", MtoCodeTrans.transformString(maindto.getFamt()));// 退付金额
			vouchermap.put("TrimSign", StateConstant.TRIMSIGN_FLAG_NORMAL);// 调整期
			vouchermap.put("XPayAmt", "");// 实际退付金额
			vouchermap.put("XPayDate", "");// 退付日期
			vouchermap.put("XAgentBusinessNo", "");// 银行交易流水号
			vouchermap.put("Hold1", "退税");// 厦门默认 退税
			vouchermap.put("Hold2", returnValue(maindto.getSastflag()));// 厦门 共享分成辅助标识代码
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			Detailmap.put("Id", tvVoucherinfoDto.getSdealno()); // 退付明细编号
			Detailmap.put("VoucherBillId", tvVoucherinfoDto.getSdealno()); // 退付凭证Id
			Detailmap.put("BgtTypeCode", ""); // 预算类型编码
			Detailmap.put("BgtTypeName", ""); // 预算类型名称
			Detailmap.put("ProCatCode", ""); // 收支管理编码
			Detailmap.put("ProCatName", ""); // 收支管理名称
			
			Detailmap.put("PayeeAcctNo", returnValue(maindto.getSpayeeacct()));// 收款人账号
			Detailmap.put("PayeeAcctName", returnValue(maindto.getSpayeename()));// 款人名称
			Detailmap.put("PayeeAcctBankName", returnValue(maindto.getSrecbankname()));// 收款人银行
			Detailmap.put("PayeeAcctBankNo", returnValue(maindto.getSpayeeopnbnkno()));// 收款银行行号
			
//			Detailmap.put("PayeeAcctNo", returnValue(maindto.getSpayeeacct())); // 收款人账号
//			Detailmap.put("PayeeAcctName", returnValue(maindto.getSpayeename())); // 收款人名称
//			Detailmap.put("PayeeAcctBankName", returnValue(maindto.getSrecbankname())); // 收款人银行
//			Detailmap.put("PayeeAcctBankNo", ""); // 收款银行行号
			Detailmap.put("AgencyCode", ""); // 预算单位编码
			Detailmap.put("AgencyName", ""); // 预算单位名称
			Detailmap.put("IncomeSortCode", returnValue(maindto.getSbdgsbtcode())); // 收入分类科目编码
			Detailmap.put("IncomeSortName", returnValue(maindto.getSincomesortname())); // 收入分类科目名称
			Detailmap.put("IncomeSortCode1", ""); // 收入分类科目名称
			Detailmap.put("IncomeSortName1", ""); // 收入分类科目名称
			Detailmap.put("IncomeSortCode2", ""); // 收入分类科目名称
			Detailmap.put("IncomeSortName2", ""); // 收入分类科目名称
			Detailmap.put("IncomeSortCode3", ""); // 收入分类科目名称
			Detailmap.put("IncomeSortName3", ""); // 收入分类科目名称
			Detailmap.put("IncomeSortCode4", ""); // 收入分类科目名称
			Detailmap.put("IncomeSortName4", ""); // 收入分类科目名称
			Detailmap.put("PayAmt", MtoCodeTrans.transformString(maindto.getFamt())); // 退付金额
			Detailmap.put("XPayAmt", "");// 实际退付金额
			Detailmap.put("XPayDate", "");// 退付日期
			Detailmap.put("XAgentBusinessNo", "");// 银行交易流水号
			Detailmap.put("XAddWord", "");// 银行交易流水号
			Detailmap.put("Hold1", ""); 
			Detailmap.put("Hold2", ""); 
			Detailmap.put("Hold3", ""); 
			Detailmap.put("Hold4", ""); 
			Detail.add(Detailmap);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			// 生成凭证xml信息
			VoucherUtil.sendTips(tvVoucherinfoDto, map);
			
//				String stampXML = "";
			/*Document fxrDoc;// 临时的凭证报文
			Document successDoc;// 返回给凭证库的成功报文
			successDoc = DocumentHelper.createDocument();
			successDoc.setXMLEncoding("GBK");
			Element root = successDoc.addElement("MOF");
			Element VoucherCount = root.addElement("VoucherCount");
			VoucherCount.setText("1");
			Element newBody = root.addElement("VoucherBody");
			newBody.addAttribute("AdmDivCode", tvVoucherinfoDto.getSadmdivcode());
			newBody.addAttribute("StYear", tvVoucherinfoDto.getSstyear());
			newBody.addAttribute("VtCode", tvVoucherinfoDto.getSvtcode());
			newBody.addAttribute("VoucherNo", tvVoucherinfoDto.getSvoucherno());
			Element VoucherFlag = newBody.addElement("VoucherFlag");
			VoucherFlag.setText("0");
			// Element Return_Reason = newBody.addElement("Return_Reason");
			Element Attach = newBody.addElement("Attach");
			Attach.setText("SUCCESS");
			Element Voucher = newBody.addElement("Voucher");
			String stampXML = com.cfcc.itfe.util.FileUtil.getInstance().readFile(
					tvVoucherinfoDto.getSfilename());
			Voucher.setText(stampXML.replaceAll("<Voucher>", "").replaceAll("</Voucher>", ""));
			com.cfcc.itfe.util.FileUtil.getInstance().deleteFile(tvVoucherinfoDto.getSfilename());
//				return successDoc.asXML().replaceAll("&lt;", "<").replaceAll(
//						"&gt;", ">");
//				System.out.println(successDoc.asXML().replaceAll("&lt;", "<").replaceAll(
//						"&gt;", ">"));
			com.cfcc.itfe.util.FileUtil.getInstance().writeFile(tvVoucherinfoDto.getSfilename(), successDoc.asXML().replaceAll("&lt;", "<").replaceAll(
					"&gt;", ">"));*/
			//签章发凭证库
			TwcsImpl tbsImpl = new TwcsImpl();
			return tbsImpl.sendVoucher(tvVoucherinfoDto.getSadmdivcode(), tvVoucherinfoDto.getSvtcode(), 
					tvVoucherinfoDto.getStrecode(), tvVoucherinfoDto.getScreatdate(),tvVoucherinfoDto.getSvoucherno());
			
		} catch (ITFEBizException e) {
			logger.error(e);
			throw new ITFEBizException("生成退库退回5209凭证失败", e);
		} 
	}

	private static String returnValue(String value) {
		if (value == null || "".equals(value)) {
			return "";
		} else {
			return value;
		}
	}
	
	/**
	 * 对字符串进行base64编码
	 * 
	 * @param src字符串
	 * @return base64字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String base64Encode(String src){
		try {
			return VoucherUtil.base64Encode(src);
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
			return TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage();
		}
	}

}

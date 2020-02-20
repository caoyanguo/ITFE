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
			return "��ѯƾ֤������Ϣʧ��";
		}
		if (StringUtils.isNotBlank(infoDto.getSext3())
				&& TwcsDealCodeConstants.VOUCHER_LOCKING.equals(infoDto
						.getSext3())) {
			return "ƾ֤״̬δͬ��";
		}
		return null;
	}

	/**
	 * ��ѯ����ƾ֤��Ϣ����������ʵ���ʽ����״̬
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
				if ("80000".equals(status)) {//ǰ�ô���ɹ�״̬
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
	 * ͬ������ƾ֤������״̬���Ĵ�ʹ��
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
				if ("80000".equals(status)) {//ǰ�ô���ɹ�״̬
					status =DealCodeConstants.VOUCHER_REGULATORY_SUCCESS;
				}else{
					status = DealCodeConstants.VOUCHER_REGULATORY_FAULT;
				}
				//����ƾ֤��״̬
				if (vtCode.equals("5207")) {
					
				}else if (vtCode.equals("5209")) {
					
				}else if (vtCode.equals("2301")) {
					
				}else if (vtCode.equals("2302")) {
					
				}else if (vtCode.equals("5207")) {
					
				}if (vtCode.equals("5207")) {
					
				}
				//����������״̬
				if ("80000".equals(status)) {//ǰ�ô���ɹ�״̬
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
	 *            ��ȡ���Ͳ�������ƾ֤��Ϣ
	 * @param tvVoucherinfoDto
	 *            ƾ֤������Ϣ
	 * @param decOrgType
	 *            ���ջ���
	 * @param isGenVoucher
	 *            �Ƿ�ϵͳ����ƾ֤��Ϣ
	 * @return �ɹ�����null ʧ�ܷ��ش���ԭ��
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
					// ǩ������
					map = VoucherUtil.getStampPotisionXML(tvVoucherinfoDto);
					// д��ǩ��ƾ֤
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
						logger.error("����ƾ֤[�������:"
								+ tvVoucherinfoDto.getStrecode() + "ƾ֤���:"
								+ tvVoucherinfoDto.getSvoucherno() + "ƾ֤����:"
								+ tvVoucherinfoDto.getScreatdate()
								+ "]ʧ�ܣ�======" + errReason);
						updateVoucherInfoStatus(tvVoucherinfoDto,
								TwcsDealCodeConstants.VOUCHER_APP_FAIL,
								errReason);
						throw new ITFEBizException("����ƾ֤[�������:"
								+ tvVoucherinfoDto.getStrecode() + "ƾ֤���:"
								+ tvVoucherinfoDto.getSvoucherno() + "ƾ֤����:"
								+ tvVoucherinfoDto.getScreatdate()
								+ "]ʧ�ܣ�======" + errReason);
					} else {
						updateVoucherOperation(tvVoucherinfoDto,
								voucherOperations[i]);
					}
				} else if (voucherOperations[i].startsWith("qz")) {
					// ǩ�� ��ȡĬ��ǩ���û�
					String[] stampOperations = voucherOperations[i].split("_");
					if (stampOperations.length <= 1) {
						logger.error("����ƾ֤[�������:"
								+ tvVoucherinfoDto.getStrecode() + "ƾ֤���:"
								+ tvVoucherinfoDto.getSvoucherno() + "ƾ֤����:"
								+ tvVoucherinfoDto.getScreatdate()
								+ "]ʧ�ܣ�======ǩ����������");
						throw new ITFEBizException("����ƾ֤[�������:"
								+ tvVoucherinfoDto.getStrecode() + "ƾ֤���:"
								+ tvVoucherinfoDto.getSvoucherno() + "ƾ֤����:"
								+ tvVoucherinfoDto.getScreatdate()
								+ "]ʧ�ܣ�======ǩ����������    errorCodeID:"
								+ System.currentTimeMillis());
					}
					errReason = voucherStampOperation(tvVoucherinfoDto,
							stampOperations[1]);
					if (StringUtils.isNotBlank(errReason)) {
						updateVoucherInfoStatus(tvVoucherinfoDto,
								TwcsDealCodeConstants.VOUCHER_APP_FAIL,
								errReason);
						logger.error("����ƾ֤[�������:"
								+ tvVoucherinfoDto.getStrecode() + "ƾ֤���:"
								+ tvVoucherinfoDto.getSvoucherno() + "ƾ֤����:"
								+ tvVoucherinfoDto.getScreatdate()
								+ "]ʧ�ܣ�======" + errReason);
						throw new ITFEBizException("����ƾ֤[�������:"
								+ tvVoucherinfoDto.getStrecode() + "ƾ֤���:"
								+ tvVoucherinfoDto.getSvoucherno() + "ƾ֤����:"
								+ tvVoucherinfoDto.getScreatdate()
								+ "]ʧ�ܣ�======" + errReason + "    errorCodeID:"
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
				logger.error("����ƾ֤[�������:" + tvVoucherinfoDto.getStrecode()
						+ "ƾ֤���:" + tvVoucherinfoDto.getSvoucherno() + "ƾ֤����:"
						+ tvVoucherinfoDto.getScreatdate() + "]ʧ�ܣ�======"
						+ errReason);
				throw new ITFEBizException("sendVoucherUtil����ƾ֤[�������:"
						+ tvVoucherinfoDto.getStrecode() + "ƾ֤���:"
						+ tvVoucherinfoDto.getSvoucherno() + "ƾ֤����:"
						+ tvVoucherinfoDto.getScreatdate() + "]ʧ�ܣ�======"
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
						TwcsDealCodeConstants.VOUCHER_SUCCESS, "�ص��ɹ�");
			} else {
				updateVoucherInfoStatus(tvVoucherinfoDto,
						TwcsDealCodeConstants.VOUCHER_LIQUIDATION_ING, "������");
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
		// ��ѯĬ��ǩ���û�
		try {
			TsUsersDto tsUsersDto = new TsUsersDto();
			tsUsersDto.setSorgcode(tvVoucherinfoDto.getSorgcode());
			tsUsersDto.setSusercode(userCode);
			tsUsersDto = (TsUsersDto) CommonFacade.getODB().findRsByDto(
					tsUsersDto).get(0);
			// ��ѯƾ֤ǩ��λ��
			List stampPositionList = findStamppositionDto(tvVoucherinfoDto);
			if (stampPositionList == null || stampPositionList.size() == 0)
				throw new ITFEBizException("ǩ��λ��δά��");
			// ��ѯ������Ϣ��ǩ��ID��Ϣ
			List strecodeList = findTsTreasuryDto(tvVoucherinfoDto);
			// ǩ�²���
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
	 *            ������¼
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	private static void updateVoucherOperation(
			TvVoucherinfoDto tvVoucherinfoDto, String operation)
			throws ITFEBizException {
		try {
			if (StringUtils.isBlank(tvVoucherinfoDto.getSdealno())
					|| StringUtils.isBlank(tvVoucherinfoDto.getSvoucherno())) {
				logger.error("ƾ֤������Ϣ����======��¼������¼:" + operation);
				throw new ITFEBizException("����ƾ֤������־�쳣�� " + "    errorCodeID:"
						+ System.currentTimeMillis());
			}
			if (StringUtils.isBlank(tvVoucherinfoDto.getSext2())) {
				tvVoucherinfoDto.setSext2(operation);
			} else {
				tvVoucherinfoDto.setSext2(tvVoucherinfoDto.getSext2() + ","
						+ operation);
			}
			tvVoucherinfoDto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
			tvVoucherinfoDto.setSdemo("ǩ��(��)�ɹ�");
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
	 *            ʧ��ԭ��
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
				logger.error("ƾ֤������Ϣ����======��¼״̬��˵��:" + status + msg);
				throw new ITFEBizException("����ƾ֤ʧ��ԭ���쳣�� " + "    errorCodeID:"
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
	 *            �Ƿ�ϵͳ�Զ�����ƾ֤
	 * @param isWriteX
	 *            �Ƿ���Ҫ��¼X��ͷ�ֶ�
	 * @return
	 * @throws ITFEBizException
	 */
	private static String getVoucherXML(TvVoucherinfoDto dto,
			boolean isGenVoucher, boolean isWriteX) throws ITFEBizException {
		try {
			// �����Ҫ��¼X��ͷ
			if (isWriteX) {
				return VoucherUtil.getVoucherStampXML(dto);
			}
			String stampXML = "";
			Document fxrDoc;// ��ʱ��ƾ֤����
			Document successDoc;// ���ظ�ƾ֤��ĳɹ�����
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
				VoucherFlag.setText("0");// �ص���־��0 ƾ֤�������ɱ��� 1 �ص�����
				stampXML = com.cfcc.itfe.util.FileUtil.getInstance().readFile(
						dto.getSfilename());
				Voucher.setText(VoucherUtil.base64Encode(stampXML));
				return successDoc.asXML().replaceAll("&lt;", "<").replaceAll(
						"&gt;", ">");
			}
			/*
			 * ��ȡ�ص�����
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
					logger.debug("ƾ֤ǩ��д���δ���뱨�ģ�" + newStr);
					Voucher.setText(VoucherUtil.base64Encode(newStr));
				}
			}
			stampXML = successDoc.asXML();
			logger.debug("ƾ֤ǩ��д��ı��ģ�" + stampXML);
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
	 * ����ƾ֤�������ѯ��Ӧ��ҵ�����ݣ���ҵ�����ݰ��յط�������ʽ��װ ����txt
	 * 
	 * @param tvVoucherinfoDto
	 * @return ����List���� index0:���¹��������dto list���� index1��ҵ�����ݰ��յط�������ʽ��װ
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
				retFlag = "\r\n"; //�س����з�
			}else{
				retFlag ="\n"; //���з�
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
				// ����**��ͷ
				str.append("**");
				str.append(maindto.getIvousrlno()+","); //ƾ֤��ˮ��1
				str.append(maindto.getSorgcode()+","); //��������2
				str.append(maindto.getScommitdate()+",");//ί������3
				str.append(maindto.getStrecode() + ","); // �����������4
				str.append(maindto.getSpayunit() + ","); // ��Ʊ��λ5
				str.append(maindto.getNmoney() + ","); // ��������6
				str.append(maindto.getSpackageticketno() + ","); // ƾ֤���7
				str.append(maindto.getSgenticketdate() + ","); // ��Ʊ����8
				str.append(maindto.getSofyear() + ","); // �������9
				str.append(maindto.getSofmonth() + ","); // �����·�10
				str.append(maindto.getStransactunit() + ","); // ���쵥λ11
				str.append(maindto.getSamttype() + ","); // �������12
				str.append(maindto.getSbudgetunitcode() + ","); // Ԥ�㵥λ����13
				str.append(maindto.getSbudgettype() + ","); // Ԥ������14
				str.append(maindto.getSpaybankno() + ","); // ���������к�15
				str.append(maindto.getSpaybankcode() + ","); // �������б���16
				str.append(maindto.getSgenticketdate() + ","); //ƾ֤����17
				String tStr = maindto.getSdemo();
				if(tStr != null && !tStr.trim().equals("")){
					if(tStr.contains(",")){
						tStr = tStr.replace(",", "��");
					}
				}
				str.append(tStr + ","); // ��ע18
				str.append(maindto.getSdealno() + ""); // ������ˮ��19
				if (os.indexOf("Win") >= 0)  {
					str.append("\r\n"); //�س����з�
				}else{
					str.append("\n"); //���з�
				}
				TvGrantpaymsgsubDto subdto = null;
				List tmpList = (List)subList.get(n);
				for (int m = 0; m < tmpList.size(); m++) {
					subdto = (TvGrantpaymsgsubDto) tmpList.get(m);
					// ��ϸ��#*��ͷ
					str.append("#*");
					str.append(subdto.getIvousrlno() + ","); // ƾ֤��ˮ��
					str.append(subdto.getIdetailseqno()+","); //��ϸ���
					str.append(subdto.getSpackageticketno() + ","); //ƾ֤���
					str.append(subdto.getSfunsubjectcode() + ",");// Ԥ��֧�����ܿ�Ŀ
					str.append(subdto.getSecosubjectcode() + ",");// Ԥ��֧�����ÿ�Ŀ
					str.append(subdto.getNmoney() + ","); // ��������
					str.append(subdto.getSaccattrib()); //�˻�����
					if (os.indexOf("Win") >= 0)  {
						str.append("\r\n"); //�س����з�
					}else{
						str.append("\n"); //���з�
					}
				}
			}
		} else if (MsgConstant.VOUCHER_NO_5108.equals(vtCode)) {
			TvDirectpaymsgmainDto maindto = null;
			maindto = (TvDirectpaymsgmainDto) mainList.get(0);
			// ����**��ͷ
			str.append("**");
			str.append(maindto.getIvousrlno()+","); //ƾ֤��ˮ��1
			str.append(maindto.getSorgcode()+","); //��������2
			str.append(maindto.getScommitdate() + ","); //ί������3
			str.append(maindto.getStrecode() + ","); // �����������4
			str.append(maindto.getSpayunit() + ","); // ��Ʊ��λ5
			str.append(maindto.getNmoney() + ","); // ��������6
			str.append(maindto.getSpackageticketno() + ","); // ƾ֤���7
			str.append(maindto.getSgenticketdate() + ","); // ��Ʊ����8
			str.append(maindto.getSbudgettype() + ","); // Ԥ������14
			str.append(maindto.getSofyear() + ","); // �������9
			str.append(maindto.getStransactunit() + ","); // ���쵥λ11
			str.append(maindto.getSamttype() + ","); // �������12
			String tStr = maindto.getSdemo();
			if(tStr != null && !tStr.trim().equals("")){
				if(tStr.contains(",")){
					tStr = tStr.replace(",", "��");
				}
			}
			str.append(tStr + ","); // ��ע18
			str.append(maindto.getSdealno() + ""); // ������ˮ��19
			if (os.indexOf("Win") >= 0)  {
				str.append("\r\n"); //�س����з�
			}else{
				str.append("\n"); //���з�
			}
			TvDirectpaymsgsubDto subdto = null;
			for (int i = 0; i < subList.size(); i++) {
				subdto = (TvDirectpaymsgsubDto) subList.get(i);
				// ��ϸ��ͷ#*
				str.append("#*");
				str.append(subdto.getIvousrlno() + ","); // ƾ֤��ˮ��
				str.append(subdto.getIdetailseqno()+","); //��ϸ���
				str.append(subdto.getSfunsubjectcode() + ",");// ���ܿ�Ŀ����
				str.append(subdto.getSecosubjectcode() + ",");// Ԥ��֧�����ÿ�Ŀ
				str.append(subdto.getSagencycode() + ","); // Ԥ�㵥λ����
				str.append(subdto.getNmoney()); // ������
				if (os.indexOf("Win") >= 0)  {
					str.append("\r\n"); //�س����з�
				}else{
					str.append("\n"); //���з�
				}
			}
	
		} else if (MsgConstant.VOUCHER_NO_2301.equals(vtCode)) {
			TvPayreckBankDto maindto = null;
			maindto = (TvPayreckBankDto) mainList.get(0);
			// ����**��ͷ
			str.append("**");
			str.append(maindto.getIvousrlno()+ ","); // ƾ֤��ˮ��
			str.append(maindto.getSbookorgcode()+ ",");//�����������
			str.append(maindto.getDentrustdate()+ ",");//ί������
			str.append(maindto.getSagentbnkcode()+ ",");//���������к�
			str.append(maindto.getStrano()+ ",");//������ˮ��
			str.append(maindto.getSfinorgcode()+ ",");//�������ش���
			str.append(maindto.getStrecode() + ","); // �������
			str.append(maindto.getSvouno() + ","); // ƾ֤���
			str.append(maindto.getDvoudate() + ","); // ƾ֤����
			str.append(maindto.getSpayeracct() + ","); // �������˺�
			str.append(maindto.getSpayername() + ","); // ����������
			str.append(maindto.getSpayeraddr()+ ","); // �����˵�ַ
			str.append(maindto.getSpayeeacct() + ","); // �տ����˺�
			str.append(maindto.getSpayeename() + ","); // �տ�������
			str.append(maindto.getSpayeeaddr() + ","); // �տ��˵�ַ
			str.append(maindto.getSpayeeopbkno() + ","); // �տ��˿������к�
			str.append(maindto.getSbudgettype() + ","); // Ԥ���������
			str.append(maindto.getStrimsign() + ","); // �����ڱ�־
			str.append(maindto.getSofyear() + ","); // �������
			str.append(maindto.getSpayeeopbkno() + ","); // �տ��˿������к�
			str.append(maindto.getFamt() + ","); // ��������,С���ֽ�����,
			str.append(maindto.getSpaymode() + ","); //֧����ʽ
			str.append(maindto.getDacceptdate() + ","); //��������
			str.append(maindto.getIstatinfnum() + ","); //��ϸ����
			str.append(maindto.getSpaytypecode() + ","); //֧����ʽ����
			str.append(maindto.getSpaytypename() + ","); //֧����ʽ����
			str.append(maindto.getSagentbnkcode() + ","); // �������к� (���������к�)
			
			String tStr = maindto.getSaddword();
			if(tStr != null && !tStr.trim().equals("")){
				if(tStr.contains(",")){
					tStr = tStr.replace(",", "��");
				}
			}
			str.append(tStr + ","); // ����
			String tStr1 = maindto.getSdescription();
			if(tStr1 != null && !tStr1.trim().equals("")){
				if(tStr1.contains(",")){
					tStr1 = tStr1.replace(",", "��");
				}
			}
			str.append(tStr1 + ","); //  ժҪ����(˵��)
			//��������
			str.append(maindto.getSxcleardate() + ","); //��������
			if (os.indexOf("Win") >= 0)  {
				str.append("\r\n"); //�س����з�
			}else{
				str.append("\n"); //���з�
			}
			
			TvPayreckBankListDto subdto = null;
			for (int i = 0; i < subList.size(); i++) {
				subdto = (TvPayreckBankListDto) subList.get(i);
				// ��ϸ��ͷ#*
				str.append("#*");
				str.append(subdto.getIvousrlno()+ ",");//ƾ֤��ˮ��
				str.append(subdto.getIseqno()+",");//��ϸ���
				str.append(subdto.getSbdgorgcode() + ","); // Ԥ�㵥λ����
				str.append(subdto.getSfuncbdgsbtcode() + ",");// ���ܿ�Ŀ����
				str.append(subdto.getSecnomicsubjectcode() + ",");//���ÿ�Ŀ����
				str.append(subdto.getSacctprop() + ","); // �˻�����
				str.append(subdto.getFamt()); // ֧�����
				if (os.indexOf("Win") >= 0)  {
					str.append("\r\n"); //�س����з�
				}else{
					str.append("\n"); //���з�
				}
			}
		} else if (MsgConstant.VOUCHER_NO_2302.equals(vtCode)) {
			TvPayreckBankBackDto maindto = null;
			maindto = (TvPayreckBankBackDto) mainList.get(0);
			// ����**��ͷ
			str.append("**");
			str.append(maindto.getIvousrlno()+ ","); // ƾ֤��ˮ��
			str.append(maindto.getSbookorgcode()+ ",");//�����������
			str.append(maindto.getDentrustdate()+ ",");//ί������
			str.append(maindto.getSagentbnkcode()+ ",");//���������к�
			str.append(maindto.getStrano()+ ",");//������ˮ��
			str.append(maindto.getSvouno() + ","); // ƾ֤���
			str.append(maindto.getDvoudate() + ","); // ƾ֤����
			str.append(maindto.getSfinorgcode()+ ",");//�������ش���
			str.append(maindto.getStrecode() + ","); // �������
			str.append(maindto.getSoritrano() + ","); // ԭ������ˮ��
			str.append(maindto.getDorientrustdate()+ ",");//ԭί������
			str.append(maindto.getSorivouno() + ","); // ԭ������ˮ��
			str.append(maindto.getDorivoudate() + ","); //ԭƾ֤����
			str.append(maindto.getSpayeracct() + ","); // �������˺�
			str.append(maindto.getSpayername() + ","); // ������ȫ��
			str.append(maindto.getSpayeeacct() + ","); // �տ����˻�
			str.append(maindto.getSpayeename() + ","); // �տ�������
			str.append(maindto.getSpaydictateno() + ","); // ֧���������
			str.append(maindto.getSpaymsgno() + ","); // ֧�����ı��
			str.append(maindto.getDpayentrustdate() + ","); // ֧��ί������
			str.append(maindto.getSpaysndbnkno() + ","); // ֧���������к�
			str.append(maindto.getSbudgettype() + ","); // Ԥ���������
			str.append(maindto.getStrimsign() + ","); // �����ڱ�־
			str.append(maindto.getSofyear() + ","); // �������
			str.append(maindto.getFamt() + ","); // �������
			str.append(maindto.getIstatinfnum() + ","); //��ϸ����
			str.append(maindto.getDacceptdate() + ","); //��������
			str.append(maindto.getSpaymode() + ","); //֧����ʽ
			str.append(maindto.getSpaytypecode() + ","); //֧����ʽ����
			str.append(maindto.getSpaytypename() + ","); //֧����ʽ����
			
			
			String tStr = maindto.getSaddword();
			if(tStr != null && !tStr.trim().equals("")){
				if(tStr.contains(",")){
					tStr = tStr.replace(",", "��");
				}
			}
			str.append(tStr + ","); // ����
			String tStr1 = maindto.getSremark();
			if(tStr1 != null && !tStr1.trim().equals("")){
				if(tStr1.contains(",")){
					tStr1 = tStr1.replace(",", "��");
				}
			}
			str.append(tStr1 + ","); //  ժҪ����(˵��)
			//��������
			str.append(maindto.getSxcleardate() + ","); //��������
			if (os.indexOf("Win") >= 0)  {
				str.append("\r\n"); //�س����з�
			}else{
				str.append("\n"); //���з�
			}
			TvPayreckBankBackListDto subdto = null;
			for (int i = 0; i < subList.size(); i++) {
				subdto = (TvPayreckBankBackListDto) subList.get(i);
				// ��ϸ��ͷ#*
				str.append("#*");
				str.append(subdto.getIvousrlno()+ ",");//ƾ֤��ˮ��
				str.append(subdto.getIseqno()+ ",");// ��ϸ���
				str.append(subdto.getSorivouno()+ ","); // ԭƾ֤���
				str.append(subdto.getDorivoudate()+ ","); // ԭƾ֤����
				str.append(subdto.getSbdgorgcode() + ","); // Ԥ�㵥λ����
				str.append(subdto.getSfuncbdgsbtcode() + ","); // ���ܿ�Ŀ����
				str.append(subdto.getSecnomicsubjectcode() + ",");//���ÿ�Ŀ����
				str.append(subdto.getSacctprop() + ","); // �˻�����
				str.append(subdto.getFamt()); // ֧�����
				if (os.indexOf("Win") >= 0)  {
					str.append("\r\n"); //�س����з�
				}else{
					str.append("\n"); //���з�
				}
			}
		} else if (MsgConstant.VOUCHER_NO_5207.equals(vtCode)) {
			TvPayoutmsgmainDto maindto = null;
			TvPayoutmsgsubDto subdto = null;
			maindto = (TvPayoutmsgmainDto) mainList.get(0);
			subdto = (TvPayoutmsgsubDto) subList.get(0);
			// ����**��ͷ
			str.append("**");
			str.append(maindto.getSbizno() + ","); // ������ˮ��
			str.append(maindto.getSorgcode() + ","); // ��������
			str.append(maindto.getScommitdate() + ","); // ί������
			str.append(maindto.getSgenticketdate()+",");//ƾ֤����
			str.append(maindto.getStrecode() + ","); // �������
			str.append(maindto.getSpayunit() + ","); // ��Ʊ��λ
			str.append(maindto.getSpayeebankno() + ","); //ת�������к�
			str.append(maindto.getSdealno()+ ",");//������ˮ��
			str.append(maindto.getStaxticketno() + ","); // ƾ֤����
			str.append(maindto.getSpayeracct() + ","); // �����˺�
			str.append(maindto.getSpayername() + ","); // ����������
			str.append(maindto.getSpayeraddr() + ","); // �����˵�ַ
			str.append(maindto.getNmoney() + ","); // ������(#.00)
			str.append(maindto.getSrecacct() + ","); // �տ����˺�
			str.append(maindto.getSrecname() + ","); // �տ�������
			str.append(maindto.getSrecbankno() + ","); // �տ��˿������к�
			str.append(maindto.getSbudgetunitcode() + ","); // Ԥ�㵥λ����
			str.append(maindto.getSunitcodename() + ","); // Ԥ�㵥λ����
			str.append(maindto.getStrimflag() + ","); // �����ڱ�־
			str.append(maindto.getSofyear() + ","); // �������
			str.append(maindto.getSbudgettype() + ","); // Ԥ���������
			str.append(maindto.getSorivouno() + ","); // ԭ֧��ƾ֤���
			str.append(maindto.getSorivoudate() + ","); // ԭ֧��ƾ֤����
			str.append(maindto.getSbackflag() + ","); // �˻ر�־
			str.append(maindto.getSinputrecbankno() + ","); // ��¼���к�
			str.append(maindto.getSinputrecbankname() + ","); // ��¼������
			str.append(maindto.getScheckstatus()+ ","); // ��¼����״̬
			str.append(maindto.getSinputusercode()+ ","); // ��¼��
			str.append(maindto.getSchecksercode()+ ","); // ������

			String sStr = maindto.getSpaysummaryname();
			if(sStr != null && !sStr.trim().equals("")){
				if(sStr.contains(",")){
					sStr = sStr.replace(",", "��");
				}
			}
			str.append(returnValue(sStr) + ","); // ��;
			String tStr1 = maindto.getSaddword();
			if(tStr1 != null && !tStr1.trim().equals("")){
				if(tStr1.contains(",")){
					tStr1 = tStr1.replace(",", "��");
				}
			}
			str.append(tStr1 + ","); //����
			
			String tStr2 = maindto.getSdemo();
			if(tStr2 != null && !tStr2.trim().equals("")){
				if(tStr2.contains(",")){
					tStr2 = tStr2.replace(",", "��");
				}
			}
			str.append(tStr2 + ","); //��ע
			if (os.indexOf("Win") >= 0)  {
				str.append("\r\n"); //�س����з�
			}else{
				str.append("\n"); //���з�
			}
			for (int i = 0; i < subList.size(); i++) {
				subdto = (TvPayoutmsgsubDto) subList.get(i);
				// ��ϸ��ͷ#*
				str.append("#*");
				str.append(subdto.getSbizno()+ ",");//ƾ֤��ˮ��
				str.append(subdto.getSseqno()+ ",");// ��ϸ���
				str.append(subdto.getSfunsubjectcode() + ","); // ���ܿ�Ŀ����
				str.append(subdto.getSecnomicsubjectcode() + ",");//���ÿ�Ŀ����
				str.append(subdto.getNmoney()); // ֧�����
				if (os.indexOf("Win") >= 0)  {
					str.append("\r\n"); //�س����з�
				}else{
					str.append("\n"); //���з�
				}
			}
		} else if (MsgConstant.VOUCHER_NO_5209.equals(vtCode)) {
			TvDwbkDto tvDwbkDto = (TvDwbkDto) mainList.get(0);
			str.append("**");
			str.append(tvDwbkDto.getIvousrlno()+ ","); //ƾ֤��ˮ��
			str.append(tvDwbkDto.getSelecvouno() + ","); // ����ƾ֤���
			str.append(tvDwbkDto.getSdwbkvoucode() + ","); // �˿�ƾ֤��� 
			str.append(tvDwbkDto.getStaxorgcode() + ","); // ���ջ��ش���,
			str.append(tvDwbkDto.getSagenttaxorgcode() + ","); // �������ջ��ش���,
			str.append(tvDwbkDto.getSpayertrecode() + ","); // �տ������� 
			str.append(tvDwbkDto.getSaimtrecode() + ","); //Ŀ�Ĺ������
			str.append(tvDwbkDto.getCbdgkind() + ","); // Ԥ������,
			str.append(tvDwbkDto.getCbdglevel() + ","); // Ԥ�㼶��,
			str.append(tvDwbkDto.getSbdgsbtcode() + ","); // ��Ŀ����
			str.append(tvDwbkDto.getSdwbkreasoncode()+",");//	�˿�ԭ�����
			str.append(tvDwbkDto.getSastflag()+",");//������־
			str.append(tvDwbkDto.getFamt()+",");//���
			str.append(tvDwbkDto.getCbckflag()+",");//�˻ر�־
			str.append(tvDwbkDto.getSpayeecode().trim() + ","); // �տλ����
			str.append(tvDwbkDto.getSpayeeacct() + ","); // �տ��˺�,
			str.append(tvDwbkDto.getSpayeename() + ","); // �տ�������,
			str.append(tvDwbkDto.getSpayeeopnbnkno() + ","); // �տ����к�
			str.append(tvDwbkDto.getDaccept() + ","); // ��������
			str.append(tvDwbkDto.getDvoucher() + ","); // ��Ʊ����
			str.append(tvDwbkDto.getDbill() + ","); // ƾ֤����
			str.append(tvDwbkDto.getCtrimflag() + ","); // �����ڱ�־
			str.append(tvDwbkDto.getSbiztype() + ","); // ҵ������
			str.append(tvDwbkDto.getSdealno() + ","); // ������ˮ��
			str.append(tvDwbkDto.getSinputrecbankname() + ","); // ֧��ϵͳ����ƥ��
			str.append(tvDwbkDto.getSinputrecbankno() + ",");//ƥ��֧��ϵͳ�к�1
			String sStr = tvDwbkDto.getSdemo();
			if(sStr != null && !sStr.trim().equals("")){
				if(sStr.contains(",")){
					sStr = sStr.replace(",", "��");
				}
			}
			str.append(returnValue(sStr) + ","); // ��ע
			if (os.indexOf("Win") >= 0)  {
				str.append("\r\n"); //�س����з�
			}else{
				str.append("\n"); //���з�
			}
		} else if(MsgConstant.VOUCHER_NO_3210.equals(vtCode)){
			TvDwbkDto tvDwbkDto = (TvDwbkDto) mainList.get(0);
			str.append("**");
			str.append(tvDwbkDto.getIvousrlno() + ","); // ������ˮ�� ,
			str.append(tvDwbkDto.getSdwbkvoucode().trim() + ","); // ԭƾ֤����,
			str.append(DateUtil.date2String2(tvDwbkDto.getDaccept()).trim() + ","); // ί������,
			str.append(tvDwbkDto.getSpayertrecode().trim() + ","); // �������,
			str.append(DateUtil.date2String2(tvDwbkDto.getDvoucher()).trim()  + ","); // ƾ֤����,
			str.append(tvDwbkDto.getSpayacctno().trim() + ","); // �����˻�,
			str.append(tvDwbkDto.getSpayacctname().trim() + ","); //����������,
			str.append(","); // �����˵�ַ,
			str.append(tvDwbkDto.getFamt() + ","); // ���,
			str.append(tvDwbkDto.getXagentbusinessno().trim() + ","); // �������к�,
			str.append(tvDwbkDto.getSpayeeopnbnkno().trim() + ","); // �տ����к�,
			str.append(tvDwbkDto.getSpayeeacct().trim() + ","); // �տ��˻�,
			str.append(tvDwbkDto.getSpayeename().trim() + ","); // �տ�������,
			str.append(tvDwbkDto.getSreturnreasonname().trim() + ","); // �����˿��˻ص�ԭ��
			str.append(MsgConstant.VOUCHER_NO_3210 + ","); // ����
			str.append(tvDwbkDto.getStaxorgcode() + ","); // ���ջ���
			str.append(tvDwbkDto.getSbdgsbtcode() + ","); // ��Ŀ����
			str.append(tvDwbkDto.getCbdgkind() + ","); //Ԥ������
			str.append(tvDwbkDto.getCbdglevel() + ","); //Ԥ�㼶��
			str.append(tvDwbkDto.getCtrimflag() + ","); //�����ڱ�־
			str.append(tvDwbkDto.getShold1() + ","); //������־
			str.append(tvDwbkDto.getSdwbkreasoncode().trim()); // �˿�ԭ����� cyg20161019������
		} else {
			throw new ITFEBizException("��ȡƾ֤��������");
		}
	}

	/**
	 * У��ӿڲ���synchronousVoucherStatus
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
	 * У��ӿڲ���sendVoucher
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
	 * У��ӿڲ���returnVoucherBack
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
			return "�˻�ʧ����Ϣ����Ϊ��";
		}
		return null;
	}

	/**
	 * У��ӿڲ���confirmVoucher
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
			return "ƾ֤��Ų���Ϊ��";
		}
		return null;
	}

	/**
	 * У��ӿڲ�����ϢreadVoucher
	 * 
	 * @param admDivCode
	 * @param vtCode
	 * @param treCode
	 * @param vouDate
	 */
	public static String verifyReadVoucherParam(String admDivCode,
			String vtCode, String treCode, String vouDate) {
		// if (StringUtils.isBlank(admDivCode)) {
		// return "�������벻��Ϊ��";
		// } else
		if (StringUtils.isBlank(vtCode)) {
			return "ƾ֤��Ų���Ϊ��";
		} else if (StringUtils.isBlank(treCode)) {
			return "������벻��Ϊ��";
		} else if (StringUtils.isBlank(vouDate)) {
			return "ƾ֤���ڲ���Ϊ��";
		}
		return null;
	}

	/**
	 * ��ѯƾ֤��Ϣ
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
				logger.error("δ�ҵ�[��������:" + admDivCode + "ƾ֤���:" + vouNo
						+ "ƾ֤����:" + vouDate + "]ƾ֤��Ϣ��");
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
	 * ��ѯƾ֤��Ϣ�б�
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
				logger.error("δ�ҵ�[��������:" + admDivCode + "ƾ֤����:" + vtCode
						+ "ƾ֤����:" + vouDate + "]ƾ֤��Ϣ��");
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
	 * ������֯�������롢�������롢ƾ֤���͡�״̬����ǩ��λ�ò���
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
	 * ������֯�������롢���������ҹ���
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
	 * ����δǩ�µ�ǩ������
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
	 * ����ǩ��˳���С��List��������
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
	 * ƾ֤ǩ��
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
			throw new ITFEBizException("ǩ��λ��δά��");
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
	 * У����������ǩ��ID��֤��ID��Ĭ��ǩ���û�
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
				throw new ITFEBizException("��֯��������" + stampDto.getSorgcode()
						+ " �����������" + stampDto.getStrecode()
						+ "��[����������Ϣ����ά��]�в����ڣ�" + "    errorCodeID:"
						+ System.currentTimeMillis());
			else if ((stampDto.getSstamptype().equals(
					MsgConstant.VOUCHERSAMP_ROTARY) && StringUtils
					.isBlank(((TsTreasuryDto) strecodeList.get(0))
							.getSrotarycertid()))
					|| (stampDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_OFFICIAL) && StringUtils
							.isBlank(((TsTreasuryDto) strecodeList.get(0))
									.getScertid())))
				throw new ITFEBizException("�����������" + stampDto.getStrecode()
						+ "��[����������Ϣ����ά��]�� " + stampDto.getSstampname()
						+ " ֤��ID����δά���� " + "    errorCodeID:"
						+ System.currentTimeMillis());
			else if ((stampDto.getSstamptype().equals(
					MsgConstant.VOUCHERSAMP_ROTARY) && StringUtils
					.isBlank(((TsTreasuryDto) strecodeList.get(0))
							.getSrotaryid()))
					|| (stampDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_OFFICIAL) && StringUtils
							.isBlank(((TsTreasuryDto) strecodeList.get(0))
									.getSstampid())))
				throw new ITFEBizException("�����������" + stampDto.getStrecode()
						+ "��[����������Ϣ����ά��]�� " + stampDto.getSstampname()
						+ " ǩ��ID����δά���� " + "    errorCodeID:"
						+ System.currentTimeMillis());
			else {
				return false;
			}

		}
		throw new ITFEBizException("��ʱ��֧��˽�²���" + "    errorCodeID:"
				+ System.currentTimeMillis());

		// else{
		// if(StringUtils.isBlank(stampDto.getSstampuser()))
		// throw new ITFEBizException("˽�£�"+stampDto.getSstampname()+
		// " ��Ӧ��Ĭ��ǩ���û���[ǩ��λ�ò���ά��]��δά���� ");
		// else{
		// userDto.setSusercode(stampDto.getSstampuser());
		// userDto.setSorgcode(stampDto.getSorgcode());
		// try {
		// List<TsUsersDto>
		// usersDtoList=CommonFacade.getODB().findRsByDto(userDto);
		// if(usersDtoList==null||usersDtoList.size()==0)
		// throw new
		// ITFEBizException("˽�£�"+stampDto.getSstampname()+" ��Ӧ��Ĭ��ǩ���û�"+
		// " �û����룺"+userDto.getSusercode()+" ��[�û���Ϣά��]�в����ڣ�");
		// else{
		// userDto=usersDtoList.get(0);
		// if(StringUtils.isBlank(userDto.getScertid()))
		// throw new
		// ITFEBizException("˽�£�"+stampDto.getSstampname()+" ��Ӧ��Ĭ��ǩ���û�"+
		// " �û�������"+userDto.getSusername()+" �û����룺"+userDto.getSusercode()+"��[�û���Ϣά��]�е�֤��ID����δά����");
		// else if(StringUtils.isBlank(userDto.getSstampid()))
		// throw new
		// ITFEBizException("˽�£�"+stampDto.getSstampname()+" ��Ӧ��Ĭ��ǩ���û�"+
		// " �û�������"+userDto.getSusername()+" �û����룺"+userDto.getSusercode()+"��[�û���Ϣά��]�е�ǩ��ID����δά����");
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
	 * ƾ֤ǩ��
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
	 * ƾ֤ǩ��
	 * 
	 * @throws ITFEBizException
	 * 
	 */
	private static String voucherStamp(List lists) throws ITFEBizException {
		VoucherService voucherService = new VoucherService();
		String stampXML = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		for (List list : (List<List>) lists) {
			// ǩ���û���Ϣ
			TsUsersDto uDto = (TsUsersDto) list.get(0);
			// ������Ϣ
			TsTreasuryDto tDto = (TsTreasuryDto) list.get(1);
			// ǩ��λ����Ϣ
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
					// ������ǩ��
					err = voucherService.signStampByNos(certID, vDto.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),vDto.getSvtcode(), 
							getVoucherXML(vDto,
									(MsgConstant.VOUCHER_NO_5209.equals(vDto.getSvtcode()) || MsgConstant.VOUCHER_NO_3209.equals(vDto.getSvtcode()))?true:false,
									(MsgConstant.VOUCHER_NO_5106.equals(vDto.getSvtcode()) || MsgConstant.VOUCHER_NO_5108.equals(vDto.getSvtcode()) 
											||MsgConstant.VOUCHER_NO_5207.equals(vDto.getSvtcode()) ) ? true : false).getBytes(),
							VoucherUtil.getStampPotisionXML(sDto.getSstampposition(), stampID), vDto.getSvoucherno(), sDto.getSstampname());
				}
				// ǩ�³ɹ�
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
	 * ���ջص� ����X�ֶ���Ϣ
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
				throw new ITFEBizException("���ղ�������");
			}
			Document fxrDoc = DocumentHelper.parseText(xml);
			List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
					"VoucherBody");
			// ��ȡ�����������롢��Ⱥ�ƾ֤����
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
				return "�ڵ���벻��Ϊ��";
			}
			TsTbsorgstatusDto tsTbsorgstatusDto = new TsTbsorgstatusDto();
			tsTbsorgstatusDto
					.setSorgcode(orgCode.substring(0, 3) + "000000000");
			List resultList = CommonFacade.getODB().findRsByDto(
					tsTbsorgstatusDto);
			if (null == resultList || resultList.size() == 0) {
				return "�ڵ���Ϣδ�ҵ�";
			}
			tsTbsorgstatusDto = (TsTbsorgstatusDto) resultList.get(0);
			if (!("0".equals(tsTbsorgstatusDto.getSstatus()))) {
				return "�ڵ㲻�ǵ�½״̬";
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
	 * ������������
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
	 * ����3208����
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
				// �����ֶθ���
				if (mainInfo.length != 24) {
					return "������Ϣ����";
				}
				// ���ݹ�������ѯ�����������������
				TsConvertfinorgDto searchFinInfo = new TsConvertfinorgDto();
				searchFinInfo.setStrecode(mainInfo[0]);
				searchFinInfo = (TsConvertfinorgDto) CommonFacade.getODB()
						.findRsByDto(searchFinInfo).get(0);
				// ������������Ϣ
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
						.getCurrentStringTime()); // ��������
				tvVoucherinfoDto.setSext4(mainInfo[2]); // ƾ֤����
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
				// ���ñ��Ľڵ� Voucher
				map.put("Voucher", vouchermap);
				// ���ñ�����Ϣ��
				vouchermap.put("Id", tvVoucherinfoDto.getSdealno());// ʵ���˿�֪ͨ��Id
				vouchermap.put("AdmDivCode", tvVoucherinfoDto.getSadmdivcode());// ������������
				vouchermap.put("StYear", tvVoucherinfoDto.getSstyear());// ҵ�����
				vouchermap.put("VtCode", tvVoucherinfoDto.getSvtcode());// ƾ֤���ͱ��
				vouchermap.put("VouDate", tvVoucherinfoDto.getScreatdate());// ƾ֤����
				vouchermap.put("VoucherNo", tvVoucherinfoDto.getSvoucherno());// ƾ֤��
				vouchermap.put("AgentBusinessNo", returnValue(mainInfo[4]));// ԭ���н�����ˮ��
				vouchermap.put("OriBillNo", returnValue(mainInfo[5]));// ԭ�������
				vouchermap.put("OriVouDate", returnValue(mainInfo[6]));// ԭ���ƾ֤����
				vouchermap.put("OriPayDate", returnValue(mainInfo[7]));// ԭ֧������
				vouchermap.put("FundTypeCode", returnValue(mainInfo[8]));// �ʽ����ʱ���
				vouchermap.put("FundTypeName", returnValue(mainInfo[9]));// �ʽ���������
				vouchermap.put("PayTypeCode", returnValue(mainInfo[10]));// ֧����ʽ����
				vouchermap.put("PayTypeName", returnValue(mainInfo[11]));// ֧����ʽ����
				vouchermap.put("ProCatCode", returnValue(""));// ��֧�������
				vouchermap.put("ProCatName", returnValue(""));// ��֧��������
				vouchermap.put("PayAcctNo", returnValue(mainInfo[14]));// �������˺�
				vouchermap.put("PayAcctName", returnValue(mainInfo[15]));// ����������
				vouchermap.put("PayAcctBankName", returnValue(mainInfo[16]));// ����������
				vouchermap.put("PayeeAcctNo", returnValue(mainInfo[17]));// �տ����˺�
				vouchermap.put("PayeeAcctName", returnValue(mainInfo[18]));// �տ�������
				vouchermap.put("PayeeAcctBankName", returnValue(mainInfo[19]));// �տ�������
				vouchermap.put("PayeeAcctBankNo", returnValue(mainInfo[20]));// �տ������к�
				vouchermap.put("PaySummaryCode", returnValue(mainInfo[21]));// ��;����
				vouchermap.put("PaySummaryName", returnValue(mainInfo[22]));// ��;����
				vouchermap.put("PayAmt", new BigDecimal(
						returnValue(mainInfo[23])));// �����˿���
				vouchermap.put("Remark", ""); // ��ע��Ϣ
				vouchermap.put("Hold1", "");// Ԥ���ֶ�1����ռ��"000043100019".equals(ITFECommonConstant.SRC_NODE)?dto.getSdemo():""
				vouchermap.put("Hold2", "");// Ԥ���ֶ�2

				List<Object> DetailList = new ArrayList<Object>();
				List<Object> Detail = new ArrayList<Object>();
				HashMap<String, Object> Detailmap = null;
				for (int i = 1; i < fileLine.length; i++) {
					subInfo = fileLine[i].split(",");
					if (subInfo.length != 11) {
						return "ƾ֤���:" + tvVoucherinfoDto.getSvoucherno()
								+ "��ϸ������";
					}
					Detailmap = new HashMap<String, Object>();
					Detailmap.put("Id", tvVoucherinfoDto.getSdealno()); // �˿�֪ͨ����ϸ���
					Detailmap.put("VoucherBillId", tvVoucherinfoDto
							.getSdealno()); // �˿�֪ͨ��Id
					Detailmap.put("BgtTypeCode", returnValue(subInfo[0])); // Ԥ�����ͱ���
					Detailmap.put("BgtTypeName", returnValue(subInfo[1])); // Ԥ����������
					Detailmap.put("ProCatCode", returnValue(subInfo[2])); // ��֧�������
					Detailmap.put("ProCatName", returnValue(subInfo[3])); // ��֧��������
					Detailmap.put("AgencyCode", returnValue(subInfo[4])); // Ԥ�㵥λ����
					Detailmap.put("AgencyName", returnValue(subInfo[5])); // Ԥ�㵥λ����
					Detailmap.put("ExpFuncCode", returnValue(subInfo[6])); // ֧�����ܷ����Ŀ����
					Detailmap.put("ExpFuncName", returnValue(subInfo[7])); // ֧�����ܷ����Ŀ����
					Detailmap.put("ExpEcoCode", returnValue(subInfo[8])); // ֧�����÷����Ŀ����
					Detailmap.put("ExpEcoName", returnValue(subInfo[9])); // ֧�����÷����Ŀ����
					Detailmap.put("Amt", MtoCodeTrans
							.transformString(returnValue(subInfo[10]))); // �˿���
					Detailmap.put("Hold1", returnValue("")); // Ԥ���ֶ�1
					Detailmap.put("Hold2", returnValue("")); // Ԥ���ֶ�2
					Detailmap.put("Hold3", returnValue("")); // Ԥ���ֶ�3
					Detailmap.put("Hold4", returnValue("")); // Ԥ���ֶ�4
					Detail.add(Detailmap);
				}
				subInfo = null;
				HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
				DetailListmap.put("Detail", Detail);
				DetailList.add(DetailListmap);
				vouchermap.put("DetailList", DetailList);
				// ����ƾ֤xml��Ϣ
				VoucherUtil.sendTips(tvVoucherinfoDto, map);
				resultDto.add(tvVoucherinfoDto);
			}
			DatabaseFacade.getODB().create(CommonUtil.listTArray(resultDto));
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("����ʵ���˿�ƾ֤3208ʧ��", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("����ʵ���˿�ƾ֤3208ʧ��", e);
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
			// ���ݹ�������ѯ�����������������
			TsConvertfinorgDto searchFinInfo = new TsConvertfinorgDto();
			searchFinInfo.setStrecode(mainInfo[3]);
			searchFinInfo.setSorgcode(mainInfo[3]+"04");//

			searchFinInfo = (TsConvertfinorgDto) CommonFacade.getODB()
					.findRsByDto(searchFinInfo).get(0);
			// ������������Ϣ
			tvVoucherinfoDto = new TvVoucherinfoDto();
			tvVoucherinfoDto.setSdealno(VoucherUtil.getGrantSequence());
			tvVoucherinfoDto.setStrecode(searchFinInfo.getStrecode());
			tvVoucherinfoDto.setSadmdivcode(searchFinInfo.getSadmdivcode());
			tvVoucherinfoDto.setSorgcode(searchFinInfo.getSorgcode());
			tvVoucherinfoDto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));
			tvVoucherinfoDto.setSvtcode(ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode01,") >= 0?MsgConstant.VOUCHER_NO_3209:MsgConstant.VOUCHER_NO_5209);
			tvVoucherinfoDto.setSvoucherno(mainInfo[0]);
			tvVoucherinfoDto.setSvoucherflag("1");
			tvVoucherinfoDto.setScreatdate(mainInfo[15]); // ��������
			tvVoucherinfoDto.setSext4(mainInfo[1]); // ƾ֤����
			tvVoucherinfoDto.setNmoney(new BigDecimal(mainInfo[6]));
			tvVoucherinfoDto.setSstatus("80");
			tvVoucherinfoDto.setSext1("20");
			Calendar cal = Calendar.getInstance();
			tvVoucherinfoDto.setSrecvtime(new Timestamp(cal.getTimeInMillis()));
			tvVoucherinfoDto.setSpaybankcode(mainInfo[8]);//�տ��˿������к�
			tvVoucherinfoDto.setSfilename(ITFECommonConstant.FILE_ROOT_PATH
					+ "Voucher" + File.separator
					+ tvVoucherinfoDto.getScreatdate() + File.separator
					+ "rev" + tvVoucherinfoDto.getSvtcode() + "_"
					+ tvVoucherinfoDto.getSdealno() + ".msg");
			FileUtil.getInstance().writeFile(tvVoucherinfoDto.getSfilename(), str);
			TvDwbkDto maindto = new TvDwbkDto();
			maindto.setIvousrlno(Long.parseLong(tvVoucherinfoDto.getSdealno())); // ƾ֤��ˮ��
			maindto.setSbizno(tvVoucherinfoDto.getSdealno()); //
			maindto.setSexamorg(searchFinInfo.getSfinorgcode());	//������������
			maindto.setSdealno(tvVoucherinfoDto.getSdealno().substring(8,16)); // ������ˮ��
			maindto.setSelecvouno(tvVoucherinfoDto.getSvoucherno()); // ����ƾ֤���
			maindto.setSdwbkvoucode(tvVoucherinfoDto.getSvoucherno()); // �˿�ƾ֤��
			maindto.setSpayertrecode(tvVoucherinfoDto.getStrecode()); // ����������
			maindto.setSaimtrecode(tvVoucherinfoDto.getStrecode()); // Ŀ�Ĺ������
			maindto.setCbckflag(StateConstant.MSG_BACK_FLAG_NO); // �˻ر�־
			maindto.setCbdgkind(MsgConstant.BDG_KIND_IN); // Ԥ������
			maindto.setCtrimflag(MsgConstant.TIME_FLAG_NORMAL); // �����ڱ�־
			maindto.setSpackageno(""); // ����ˮ��
			maindto.setStaxorgcode(mainInfo[2]);	//���ջ��ش���
			maindto.setSfundtypecode(MsgConstant.BDG_KIND_IN);	//Ĭ��Ԥ����
			maindto.setSfundtypename("Ԥ����");	//Ĭ��Ԥ����
			maindto.setSpayeeacct(returnValue(mainInfo[9])); // �տ����˺�
			maindto.setSpayeename(returnValue(mainInfo[10]));	//�տ�������
			maindto.setSpayeecode("");	//�տ��˴���  Ԥ�㵥λ����
			maindto.setSrecbankname(returnValue(mainInfo[7]));// �տ������к�
			maindto.setSpayeeopnbnkno(returnValue(mainInfo[8]));// �տ��˿������к�
			maindto.setFamt(tvVoucherinfoDto.getNmoney());
			maindto.setSbdgsbtcode(returnValue(mainInfo[12]));// ��Ŀ����
			maindto.setSincomesortname(returnValue(mainInfo[12]));//��������Ŀ����
			maindto.setSpayeename(mainInfo[10]); // �տ�������
			maindto.setDaccept(TimeFacade.getCurrentDateTime());
			maindto.setDvoucher(CommonUtil.strToDate(mainInfo[1]));
			maindto.setDacct(TimeFacade.getCurrentDateTime());
			maindto.setDbill(TimeFacade.getCurrentDateTime());
			maindto.setSbookorgcode(tvVoucherinfoDto.getSorgcode());
			maindto.setSfilename(tvVoucherinfoDto.getSfilename());
			maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_DEALING);// ״̬
			maindto.setSreturnreasonname(returnValue(mainInfo[11]));// �˻�ԭ��
			maindto.setSpayacctno(returnValue(mainInfo[4]));// �������˺�
			maindto.setSpayacctname(returnValue(mainInfo[5]));// ����������
			//������ҪԤ�㼶�κ͹���ֳɸ�����ʶ����
			if(mainInfo.length >= 14){
				maindto.setSastflag(mainInfo[13]);
			}else{
				maindto.setSastflag("");
			}
			//������ҪԤ�㼶�κ͹���ֳɸ�����ʶ����
			if(mainInfo.length >= 15){
				maindto.setCbdglevel(mainInfo[14]);
			}else{
				maindto.setCbdglevel("");
			}
			maindto.setSdwbkreasoncode(returnValue(mainInfo[11]));
			DatabaseFacade.getODB().create(maindto);
			DatabaseFacade.getODB().create(tvVoucherinfoDto);
			
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode01,") >= 0) { // ���ŷ�ƾ֤�ⱨ��
				return genXiaMenVoucherInfoBy5209(tvVoucherinfoDto, maindto);
			}else{ //������MQ����(�����˿ⱨ�ĵ���ҵ����)
				MuleMessage message = new DefaultMuleMessage("");
				MuleClient client = new MuleClient();
				message.setStringProperty(MessagePropertyKeys.MSG_NO_KEY,
						"TBS_1000");
				message.setProperty(MessagePropertyKeys.MSG_DTO, tvVoucherinfoDto);
				// message.setProperty("orgCode", vDto.getSorgcode());
				message = client.send("vm://ManagerMsgWithCommBank", message);
				//��ѯ�����Ƿ�ɹ����ͳ�ȥ��������ɹ��Ļ��������TF_FUND_APPROPRIATION��
				String sql = "SELECT count(*) FROM TF_FUND_APPROPRIATION WHERE S_PACKNO = ? AND S_TRECODE = ? AND S_VOUNO = ? AND S_VOUDATE = ?";
				sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory()
				.getSQLExecutor();
				sqlExecutor.addParam(tvVoucherinfoDto.getSpackno());
				sqlExecutor.addParam(tvVoucherinfoDto.getStrecode());
				sqlExecutor.addParam(tvVoucherinfoDto.getSvoucherno());
				sqlExecutor.addParam(tvVoucherinfoDto.getSext4());
				SQLResults sqlResults = sqlExecutor.runQuery(sql);
				if (sqlResults.getInt(0, 0) == 0) {
					return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + "�������㱨��ʧ��");
				} else {
					return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
				}
			}
		} catch (JAFDatabaseException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("�����˿��˻�5209ƾ֤ʧ��", e);
		} catch (ValidateException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("�����˿��˻�5209ƾ֤ʧ��", e);
		} catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("�����˿��˻�5209ƾ֤ʧ��", e);
		} catch (FileOperateException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("�����˿��˻�5209ƾ֤ʧ��", e);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("�����˿��˻�5209ƾ֤ʧ��", e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("�����˿��˻�5209ƾ֤ʧ��", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e);
			throw new ITFEBizException("�����˿��˻�5209ƾ֤ʧ��", e);
		}finally
		{
			if(null!=sqlExecutor)
				sqlExecutor.closeConnection();
		}
	}
	
	private static String genXiaMenVoucherInfoBy5209(TvVoucherinfoDto tvVoucherinfoDto,
			TvDwbkDto maindto) throws ITFEBizException {
		try {
			//����xml�ļ�
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", tvVoucherinfoDto.getSdealno());// �����˸�ƾ֤Id
			vouchermap.put("AdmDivCode", tvVoucherinfoDto.getSadmdivcode());// ������������
			vouchermap.put("StYear", tvVoucherinfoDto.getSstyear());// ҵ�����
			vouchermap.put("VtCode", tvVoucherinfoDto.getSvtcode());// ƾ֤���ͱ��
			vouchermap.put("VouDate", tvVoucherinfoDto.getSext4());// ƾ֤����
			vouchermap.put("VoucherNo", tvVoucherinfoDto.getSvoucherno());// ƾ֤��
			vouchermap.put("TreCode", tvVoucherinfoDto.getStrecode());// �����������
			vouchermap.put("FinOrgCode", maindto.getSexamorg());// �������ش���
			vouchermap.put("TaxOrgCode", returnValue(maindto.getStaxorgcode()));
			vouchermap.put("TaxOrgName", "");// �������ش���
			//Ԥ�㼶��
			vouchermap.put("BudgetLevelCode", returnValue(maindto.getCbdglevel()));
			vouchermap.put("BudgetType", StateConstant.BudgetType_IN);	//Ԥ����
			
			vouchermap.put("FundTypeCode", returnValue(maindto.getSfundtypecode()));// �ʽ����ʱ���
			vouchermap.put("FundTypeName", returnValue(maindto.getSfundtypename()));// �ʽ���������
			vouchermap.put("ClearBankCode", returnValue(maindto.getSclearbankcode()));// �������б���
			vouchermap.put("ClearBankName", returnValue(maindto.getSclearbankname()));// ������������
			vouchermap.put("PayeeAcctNo", returnValue(maindto.getSpayeeacct()));// �տ����˺�
			vouchermap.put("PayeeAcctName", returnValue(maindto.getSpayeename()));// ��������
			vouchermap.put("PayeeAcctBankName", "");// �տ�������
			vouchermap.put("PayeeAcctBankNo", returnValue(maindto.getSpayeeopnbnkno()));// �տ������к�
			
			vouchermap.put("PayAcctNo", returnValue(maindto.getSpayacctno()));// �������˺�
			vouchermap.put("PayAcctName", returnValue(maindto.getSpayacctname()));// ����������
			vouchermap.put("PayAcctBankName", returnValue(maindto.getSpayacctbankname()));// ����������
			vouchermap.put("AgencyCode", "");// Ԥ�㵥λ����
			vouchermap.put("AgencyName", "");// Ԥ�㵥λ����
			vouchermap.put("nReturnReasonCode", returnValue(maindto.getSreturnreasonname()));// �˸�ԭ��
			vouchermap.put("nReturnReasonName", returnValue(maindto.getSreturnreasonname()));// �˸�ԭ��
			vouchermap.put("PayAmt", MtoCodeTrans.transformString(maindto.getFamt()));// �˸����
			vouchermap.put("TrimSign", StateConstant.TRIMSIGN_FLAG_NORMAL);// ������
			vouchermap.put("XPayAmt", "");// ʵ���˸����
			vouchermap.put("XPayDate", "");// �˸�����
			vouchermap.put("XAgentBusinessNo", "");// ���н�����ˮ��
			vouchermap.put("Hold1", "��˰");// ����Ĭ�� ��˰
			vouchermap.put("Hold2", returnValue(maindto.getSastflag()));// ���� ����ֳɸ�����ʶ����
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			Detailmap.put("Id", tvVoucherinfoDto.getSdealno()); // �˸���ϸ���
			Detailmap.put("VoucherBillId", tvVoucherinfoDto.getSdealno()); // �˸�ƾ֤Id
			Detailmap.put("BgtTypeCode", ""); // Ԥ�����ͱ���
			Detailmap.put("BgtTypeName", ""); // Ԥ����������
			Detailmap.put("ProCatCode", ""); // ��֧�������
			Detailmap.put("ProCatName", ""); // ��֧��������
			
			Detailmap.put("PayeeAcctNo", returnValue(maindto.getSpayeeacct()));// �տ����˺�
			Detailmap.put("PayeeAcctName", returnValue(maindto.getSpayeename()));// ��������
			Detailmap.put("PayeeAcctBankName", returnValue(maindto.getSrecbankname()));// �տ�������
			Detailmap.put("PayeeAcctBankNo", returnValue(maindto.getSpayeeopnbnkno()));// �տ������к�
			
//			Detailmap.put("PayeeAcctNo", returnValue(maindto.getSpayeeacct())); // �տ����˺�
//			Detailmap.put("PayeeAcctName", returnValue(maindto.getSpayeename())); // �տ�������
//			Detailmap.put("PayeeAcctBankName", returnValue(maindto.getSrecbankname())); // �տ�������
//			Detailmap.put("PayeeAcctBankNo", ""); // �տ������к�
			Detailmap.put("AgencyCode", ""); // Ԥ�㵥λ����
			Detailmap.put("AgencyName", ""); // Ԥ�㵥λ����
			Detailmap.put("IncomeSortCode", returnValue(maindto.getSbdgsbtcode())); // ��������Ŀ����
			Detailmap.put("IncomeSortName", returnValue(maindto.getSincomesortname())); // ��������Ŀ����
			Detailmap.put("IncomeSortCode1", ""); // ��������Ŀ����
			Detailmap.put("IncomeSortName1", ""); // ��������Ŀ����
			Detailmap.put("IncomeSortCode2", ""); // ��������Ŀ����
			Detailmap.put("IncomeSortName2", ""); // ��������Ŀ����
			Detailmap.put("IncomeSortCode3", ""); // ��������Ŀ����
			Detailmap.put("IncomeSortName3", ""); // ��������Ŀ����
			Detailmap.put("IncomeSortCode4", ""); // ��������Ŀ����
			Detailmap.put("IncomeSortName4", ""); // ��������Ŀ����
			Detailmap.put("PayAmt", MtoCodeTrans.transformString(maindto.getFamt())); // �˸����
			Detailmap.put("XPayAmt", "");// ʵ���˸����
			Detailmap.put("XPayDate", "");// �˸�����
			Detailmap.put("XAgentBusinessNo", "");// ���н�����ˮ��
			Detailmap.put("XAddWord", "");// ���н�����ˮ��
			Detailmap.put("Hold1", ""); 
			Detailmap.put("Hold2", ""); 
			Detailmap.put("Hold3", ""); 
			Detailmap.put("Hold4", ""); 
			Detail.add(Detailmap);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			// ����ƾ֤xml��Ϣ
			VoucherUtil.sendTips(tvVoucherinfoDto, map);
			
//				String stampXML = "";
			/*Document fxrDoc;// ��ʱ��ƾ֤����
			Document successDoc;// ���ظ�ƾ֤��ĳɹ�����
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
			//ǩ�·�ƾ֤��
			TwcsImpl tbsImpl = new TwcsImpl();
			return tbsImpl.sendVoucher(tvVoucherinfoDto.getSadmdivcode(), tvVoucherinfoDto.getSvtcode(), 
					tvVoucherinfoDto.getStrecode(), tvVoucherinfoDto.getScreatdate(),tvVoucherinfoDto.getSvoucherno());
			
		} catch (ITFEBizException e) {
			logger.error(e);
			throw new ITFEBizException("�����˿��˻�5209ƾ֤ʧ��", e);
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
	 * ���ַ�������base64����
	 * 
	 * @param src�ַ���
	 * @return base64�ַ���
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

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

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class Voucher5904MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5904MsgServer.class);
	private Voucher voucher;

	/**
	 * 基础元数据5904，此类只保存参数数据
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
			logger.error("解析基础元数据5904报文出现错误！", e);
			throw new ITFEBizException("解析基础元数据5904报文出现错误！", e);

		}
		List VoucherBodyList = fxrDoc.selectSingleNode("MOF").selectNodes(
				"VoucherBody");
		String AdmDivCode = "";// 行政区划代码
		String StYear = "";// 年度
		String VtCode = "";// 凭证类型
		List subDtoList = null;
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
		List mainDtoList = null;

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

				// String VoucherNo =
				// element.attribute("VoucherNo").getText();// 凭证编号
				// Voucher
				Element elementVoucher = (Element) element
						.selectSingleNode("Voucher");
				String VoucherNo = elementVoucher.elementText("VoucherNo");
				mainvou = dealnos.get(VoucherNo);// 获取序列号
				/**
				 * 凭证信息
				 */
				String VouDate = elementVoucher.elementText("VouDate"); // 凭证日期
				AdmDivCode = elementVoucher.elementText("AdmDivCode"); // 凭证日期
				String VoucherCheckNo = elementVoucher
						.elementText("VoucherCheckNo");// 基础数据单号
				String ChildPackNum = elementVoucher
						.elementText("ChildPackNum");// 子包总数
				String CurPackNo = elementVoucher.elementText("CurPackNo");// 本包序号
				String DataEle = elementVoucher.elementText("DataEle");// 基础数据代码
				String DataEleName = elementVoucher.elementText("DataEleName");// 基础数据中文名称
				String Hold1 = elementVoucher.elementText("Hold1");// 预留字段1
				String Hold2 = elementVoucher.elementText("Hold2");// 预留字段2
				TsConvertfinorgDto conorg = new TsConvertfinorgDto();
				conorg.setSadmdivcode(AdmDivCode);
				conorg = (TsConvertfinorgDto) CommonFacade.getODB()
						.findRsByDto(conorg).get(0);
				// 明细信息List
				List listDetail = elementVoucher.selectSingleNode("DetailList")
						.selectNodes("Detail");
				for (int j = 0; j < listDetail.size(); j++) {
					Element elementDetail = (Element) listDetail.get(j);

					String Id = elementDetail.elementText("Id");// 序号

					String ChrId = elementDetail.elementText("ChrId");// 本要素ID
					String ChrCode = elementDetail.elementText("ChrCode");// 显示编码
					String ChrName = elementDetail.elementText("ChrName");// 显示名称
					String LevelNum = elementDetail.elementText("LevelNum");// 级次
					String IsLeaf = elementDetail.elementText("IsLeaf");// 是否底级
					String Enabled = elementDetail.elementText("Enabled");// 是否启用
					String CreateDate = elementDetail.elementText("CreateDate");// 创建时间
					String LatestOpDate = elementDetail
							.elementText("LatestOpDate");// 最后修改时间
					String IsDeleted = elementDetail.elementText("IsDeleted");// 是否删除
					String LastVer = elementDetail.elementText("LastVer");// 最后版本
					String ParentId = elementDetail.elementText("ParentId");// 父级ID
					String CurVer = elementDetail.elementText("CurVer");// 当前版本
					String AcctNo = elementDetail.elementText("AcctNo");// 账号
					String AcctBankName = elementDetail
							.elementText("AcctBankName");// 开户银行名称
					String AcctBankCode = elementDetail
							.elementText("AcctBankCode");// 开户银行编码
					String Hold1sub = elementDetail.elementText("Hold1");// 预留字段1
					String Hold2sub = elementDetail.elementText("Hold2");// 预留字段2
					String Hold3sub = elementDetail.elementText("Hold3");// 预留字段3
					String Hold4sub = elementDetail.elementText("Hold4");// 预留字段4
					if(DataEleName.equals("")){
						
					}
				}

				/**
				 * 业务表入库，校验报文合法性
				 * 
				 * 业务主表，子表入库失败即签收失败
				 */
				if (DataEle.equals("1")) {// 预算科目
					TsBudgetsubjectDto[] subDtos = new TsBudgetsubjectDto[subDtoList
							.size()];
					subDtos = (TsBudgetsubjectDto[]) subDtoList
							.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				} else if (DataEle.equals("2")) {// 支付行号
					TsPaybankDto[] subDtos = new TsPaybankDto[subDtoList.size()];
					subDtos = (TsPaybankDto[]) subDtoList.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				} else if (DataEle.equals("3")) {// 零余额账户
					TsPayacctinfoDto[] subDtos = new TsPayacctinfoDto[subDtoList
							.size()];
					subDtos = (TsPayacctinfoDto[]) subDtoList.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				} else if (DataEle.equals("4")) {// 预算单位变动情况
					TdCorpDto[] subDtos = new TdCorpDto[subDtoList.size()];
					subDtos = (TdCorpDto[]) subDtoList.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				}

				// 签收成功
				vDto = new TvVoucherinfoDto();
				vDto.setSdealno(mainvou);
				vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(
						vDto).get(0);
				vDto.setIcount(subDtoList.size());
				vDto.setSext5(DataEleName);
				vDto.setShold1(Hold1);
				vDto.setShold2(Hold2);
				vDto.setSext4(DataEle);

				voucher.voucherConfirmSuccess(vDto.getSdealno());

				vDto.setSstatus(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				vDto.setSdemo("校验成功");
				DatabaseFacade.getODB().update(vDto);
				logger.debug("==============校验成功==============");
			} catch (ITFEBizException e) {
				logger.error(e);
				VoucherException voucherE = new VoucherException();
				voucherE.saveErrInfo(VtCode, e);
				throw new ITFEBizException("签收凭证报文" + VtCode + "出现异常", e);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				VoucherException voucherE = new VoucherException();
				voucherE.saveErrInfo(VtCode, e);
				throw new ITFEBizException("签收凭证报文" + VtCode + "出现异常", e);
			} catch (ValidateException e) {
				logger.error(e);
				VoucherException voucherE = new VoucherException();
				voucherE.saveErrInfo(VtCode, e);
				throw new ITFEBizException("签收凭证报文" + VtCode + "出现异常", e);
			} catch (Exception e) {
				logger.error(e);
				VoucherException voucherE = new VoucherException();
				voucherE.saveErrInfo(VtCode, e);
				throw new ITFEBizException("签收凭证报文" + VtCode + "出现异常", e);
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

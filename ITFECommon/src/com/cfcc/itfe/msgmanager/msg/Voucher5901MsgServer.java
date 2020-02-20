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

public class Voucher5901MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Voucher5901MsgServer.class);
	private Voucher voucher;

	/**
	 * 基础元数据5901，此类只保存参数数据
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
			logger.error("解析基础元数据5901报文出现错误！", e);
			throw new ITFEBizException("解析基础元数据5901报文出现错误！", e);

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

				String VoucherNo = element.attribute("VoucherNo").getText();// 凭证编号
				// Voucher
				Element elementVoucher = (Element) element
						.selectSingleNode("Voucher");
				mainvou = dealnos.get(VoucherNo);// 获取序列号
				/**
				 * 凭证信息
				 */
				String VouDate = elementVoucher.elementText("VouDate"); // 凭证日期
				AdmDivCode = elementVoucher.elementText("AdmDivCode"); // 凭证日期
				String DataEle = elementVoucher.elementText("DataEle");// 基础数据代码
				String DataEleName = elementVoucher.elementText("DataEleName");// 基础数据中文名称
				String Hold1 = elementVoucher.elementText("Hold1");// 预留字段1
				String Hold2 = elementVoucher.elementText("Hold2");// 预留字段2
				String Hold3 = elementVoucher.elementText("Hold3");// 预留字段3
				String Hold4 = elementVoucher.elementText("Hold4");// 预留字段4
				TsConvertfinorgDto conorg = new TsConvertfinorgDto();
				conorg.setSadmdivcode(AdmDivCode);
				conorg = (TsConvertfinorgDto) CommonFacade.getODB()
						.findRsByDto(conorg).get(0);
				// 明细信息List
				List listDetail = elementVoucher.selectSingleNode("DetailList")
						.selectNodes("Detail");

				if (DataEle.equals("1")) {// 预算科目
					subDtoList = new ArrayList<TsBudgetsubjectDto>();
					for (int j = 0; j < listDetail.size(); j++) {
						Element elementDetail = (Element) listDetail.get(j);
						String Subjectcode = elementDetail
								.elementText("Subjectcode");// 科目代码
						String Subjectname = elementDetail
								.elementText("Subjectname");// 科目名称
						String Subjecttype = elementDetail
								.elementText("Subjecttype");// 科目类型
						String Subjectclassification = elementDetail
								.elementText("Subjectclassification");// 科目分类
						String IEsign = elementDetail.elementText("IEsign");// 收支标志
						String Entrymark = elementDetail
								.elementText("Entrymark");// 录入标志
						String Subjectattribute = elementDetail
								.elementText("Subjectattribute");// 科目属性
						String Allocationmark = elementDetail
								.elementText("Allocationmark");// 调拨标志
						String SubjectcodeSimplify = elementDetail
								.elementText("SubjectcodeSimplify");// 科目简码
						String FiscalCode = elementDetail
								.elementText("FiscalCode");// 财政代码
						String TaxCode = elementDetail.elementText("TaxCode");// 税务代码
						String SuperiorsCode = elementDetail
								.elementText("SuperiorsCode");// 上级代码
						String UniformCode = elementDetail
								.elementText("UniformCode");// 统一代码
						String Statisticalcode = elementDetail
								.elementText("Statisticalcode");// 统计代码
						TsBudgetsubjectDto idto = new TsBudgetsubjectDto();
						idto.setSsubjectcode(Subjectcode);// 科目代码
						idto.setSsubjectname(Subjectname);// 科目名称
						idto.setSsubjecttype(Subjecttype);// 科目类型
						idto.setSsubjectclass(Subjectclassification);// 科目分类
						idto.setSinoutflag(IEsign);// 收支标志
						idto.setSwriteflag(Entrymark);// 录入标志
						idto.setSsubjectattr(Subjectattribute);// 科目属性
						idto.setSmoveflag(Allocationmark);// 调拨标志
						idto.setSorgcode(conorg.getSorgcode());
						subDtoList.add(idto);
					}
				} else if (DataEle.equals("2")) {// 支付行号
					subDtoList = new ArrayList<TsPaybankDto>();
					for (int j = 0; j < listDetail.size(); j++) {
						Element elementDetail = (Element) listDetail.get(j);
						String Paylinenumber = elementDetail
								.elementText("Paylinenumber");// 支付行号
						String Status = elementDetail.elementText("Status");// 状态
						String Liquidationlinenumber = elementDetail
								.elementText("Liquidationlinenumber");// 清算行行号
						String City = elementDetail.elementText("City");// 所在城市
						String Participantsfullname = elementDetail
								.elementText("Participantsfullname");// 参与者全称
						String effectivedate = elementDetail
								.elementText("effectivedate");// 生效日期
						String Expirationdate = elementDetail
								.elementText("Expirationdate");// 失效日期
						String Remark = elementDetail.elementText("Remark");// 备注

						TsPaybankDto idto = new TsPaybankDto();
						idto.setSbankno(Paylinenumber);// 支付行号
						idto.setSstate(Status);// 状态
						idto.setSpaybankno(Liquidationlinenumber);// 清算行行号
						idto.setSofcity(City);// 所在城市
						idto.setSbankname(Participantsfullname);// 参与者全称
						idto.setDaffdate(DateUtil.stringToDate(effectivedate));// 生效日期
						idto.setSorgcode("000000000000");
						subDtoList.add(idto);
					}
				} else if (DataEle.equals("3")) {// 零余额账户
					subDtoList = new ArrayList<TsPayacctinfoDto>();
					for (int j = 0; j < listDetail.size(); j++) {
						Element elementDetail = (Element) listDetail.get(j);
						TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
						accdto.setStrecode(conorg.getStrecode());
						accdto.setSbiztype("1");
						accdto = (TsInfoconnorgaccDto) CommonFacade.getODB()
								.findRsByDto(accdto).get(0);

						String ZeroAccount = elementDetail
								.elementText("ZeroAccount");// 零余额账号
						String ZBAsstate = elementDetail
								.elementText("ZBAsstate");// 零余额账户状态
						String Zerotime = elementDetail
								.elementText("Zerotime");// 零余额操作时间
						String ZeroBanknumber = elementDetail
								.elementText("ZeroBanknumber");// 零余额开户行号
						String ZeroBankname = elementDetail
								.elementText("ZeroBankname");// 零余额开户行名称
						String Zerocodingunit = elementDetail
								.elementText("Zerocodingunit");// 零余额开户单位编码
						String ZeroAccountName = elementDetail
								.elementText("ZeroAccountName");// 零余额开户单位名称
						TsPayacctinfoDto idto = new TsPayacctinfoDto();
						idto.setSorgcode(conorg.getSorgcode());
						idto.setStrecode(conorg.getStrecode());
						idto.setSpayeeacct(ZeroAccount);// 收款人账户
						idto.setSpayeename(ZeroAccountName);// 收款人名称
						idto.setSpayeracct(accdto.getSpayeraccount());// 付款人帐户
						idto.setSpayername(accdto.getSpayername());// 付款人名称
						idto.setSgenbankcode(ZeroBanknumber);// 代理银行行号
						idto.setSbiztype("1");// 业务类型
						subDtoList.add(idto);
					}
				} else if (DataEle.equals("4")) {// 预算单位变动情况
					subDtoList = new ArrayList<TdCorpDto>();
					for (int j = 0; j < listDetail.size(); j++) {
						Element elementDetail = (Element) listDetail.get(j);
						String SupDepName = elementDetail
								.elementText("SupDepName");// 单位名称
						String SupDepCode = elementDetail
								.elementText("SupDepCode");// 单位编码
						String UnitChangeStatus = elementDetail
								.elementText("UnitChangeStatus");// 单位变更状态
						// 0：变更、1：新增、
						// 2：删除
						String IssuedDate = elementDetail
								.elementText("IssuedDate");// 发文日期
						String EffectiveDate1 = elementDetail
								.elementText("EffectiveDate1");// 生效日期

						TdCorpDto idto = new TdCorpDto();
						idto.setSbookorgcode(conorg.getSorgcode());
						idto.setStrecode(conorg.getStrecode());
						idto.setScorpcode(SupDepCode);// 收款人账户
						idto.setScorpcode(SupDepName);// 收款人名称
						subDtoList.add(idto);
					}
				}

				/**
				 * 业务表入库，校验报文合法性
				 * 
				 * 业务主表，子表入库失败即签收失败
				 */
				if (DataEle.equals("1")) {// 预算科目
					TsBudgetsubjectDto[] subDtos = new TsBudgetsubjectDto[subDtoList.size()];
					subDtos = (TsBudgetsubjectDto[]) subDtoList.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				} else if (DataEle.equals("2")) {// 支付行号
					TsPaybankDto[] subDtos = new TsPaybankDto[subDtoList.size()];
					subDtos = (TsPaybankDto[]) subDtoList.toArray(subDtos);
					DatabaseFacade.getODB().create(subDtos);
				} else if (DataEle.equals("3")) {// 零余额账户
					TsPayacctinfoDto[] subDtos = new TsPayacctinfoDto[subDtoList.size()];
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
				vDto.setShold3(Hold3);
				vDto.setShold4(Hold4);
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

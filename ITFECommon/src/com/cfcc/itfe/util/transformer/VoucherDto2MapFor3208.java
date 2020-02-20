package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

public class VoucherDto2MapFor3208 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3208.class);

	@SuppressWarnings("unchecked")
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException {
		List list = findMainDto(vDto);
		if (list.size() == 0)
			return list;
		List lists = new ArrayList();
		for (TvVoucherinfoAllocateIncomeDto mainDto : (List<TvVoucherinfoAllocateIncomeDto>) list) {
//			List<TvVoucherinfoDto> voutherList = voucherIsRepeat(vDto, mainDto);
//			if (voutherList != null && voutherList.size() > 0) {
//				continue;
//			}
			lists.add(voucherTranfor(vDto, mainDto));
		}
		return lists;
	}

	/**
	 * 生成凭证 生成凭证报文
	 * 
	 * @param vDto
	 * @param mainDto
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	private List voucherTranfor(TvVoucherinfoDto vDto,
			TvVoucherinfoAllocateIncomeDto mainDto) throws ITFEBizException {
		TvVoucherinfoDto dto = (TvVoucherinfoDto) vDto.clone();
		dto.setSdealno(VoucherUtil.getGrantSequence());
		dto.setSvoucherno(dto.getSdealno());
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));
		dto.setSvoucherflag("1");
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto
				.getScreatdate(), dto.getSdealno()));
		dto.setSadmdivcode(mainDto.getSadmdivcode());
		dto.setSvoucherno(mainDto.getSdealno());
		dto.setNmoney(mainDto.getNmoney());
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("处理成功");
		dto.setShold4("1");	//标识该笔为TC资金导入
		List lists = new ArrayList();
		lists.add(dto);
		lists.add(mainDto);
		Map map = tranforPayoutBack(lists);
		lists.clear();
		lists.add(map);
		lists.add(dto);
		return lists;
	}

	/**
	 * 查询实拨资金退款业务表信息
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List findMainDto(TvVoucherinfoDto dto) throws ITFEBizException {
		TvVoucherinfoAllocateIncomeDto mainDto = new TvVoucherinfoAllocateIncomeDto();
		mainDto.setSorgcode(dto.getSorgcode());
		mainDto.setStrecode(dto.getStrecode());
		mainDto.setScommitdate(dto.getScreatdate());
		mainDto.setSvtcode(dto.getSvtcode());
		try {
			return CommonFacade.getODB().findRsByDto(mainDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！", e);
		}
	}

	/**
	 * 凭证判重
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List voucherIsRepeat(TvVoucherinfoDto dto,
			TvVoucherinfoAllocateIncomeDto mainDto) throws ITFEBizException {
		TvVoucherinfoDto vDto = (TvVoucherinfoDto) dto.clone();
		vDto.setSadmdivcode(mainDto.getSadmdivcode());
		vDto.setSvoucherno(mainDto.getSdealno());
		try {
			return CommonFacade.getODB().findRsByDto(vDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！", e);
		}
	}

	/**
	 * DTO转化XML报文
	 * 
	 * @param List
	 *            生成报文要素集合
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map tranforPayoutBack(List lists) throws ITFEBizException {
		try {
			// 杭州实拨资金退款(最新接口20141015_V2.0版本)，杭州节点代码201057100006，宁波节点代码000057400006
			if (ITFECommonConstant.SRC_NODE.equals("201057100006")
					|| ITFECommonConstant.SRC_NODE.equals("000057400006")) {
				return tranforPayoutBackForHZ(lists);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) lists
					.get(1);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", vDto.getSdealno());// 实拨退款通知书Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());// 业务年度
			vouchermap.put("VtCode", vDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());// 凭证日期
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// 凭证号
			vouchermap.put("AgentBusinessNo", returnValue(dto.getSdealno()));// 原银行交易流水号
			vouchermap.put("OriBillNo", returnValue(""));// 原拨款单单号
			vouchermap.put("OriVouDate", returnValue(dto.getShold2()));// 原拨款单凭证日期
			vouchermap.put("OriPayDate", returnValue(dto.getShold2()));// 原支付日期
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				vouchermap.put("FundTypeCode", returnValue("1"));// 资金性质编码
				vouchermap.put("FundTypeName", returnValue("预算管理资金"));// 资金性质名称
				vouchermap.put("PayTypeCode", returnValue("91"));// 支付方式编码
				vouchermap.put("PayTypeName", returnValue("实拨"));// 支付方式名称
				// vouchermap.put("AgencyCode", returnValue("")); // 预算单位编码
				// vouchermap.put("AgencyName", returnValue("")); // 预算单位名称
			} else {
				vouchermap.put("FundTypeCode", returnValue(""));// 资金性质编码
				vouchermap.put("FundTypeName", returnValue(""));// 资金性质名称
				vouchermap.put("PayTypeCode", returnValue(dto.getSvtcodedes()));// 支付方式编码
				vouchermap.put("PayTypeName", returnValue(dto.getStradekind()));// 支付方式名称
			}
			vouchermap.put("AgencyCode", returnValue("")); // 预算单位编码
			vouchermap.put("AgencyName", returnValue("")); // 预算单位名称
			vouchermap.put("ProCatCode", returnValue(""));// 收支管理编码
			vouchermap.put("ProCatName", returnValue(""));// 收支管理名称
			vouchermap.put("PayAcctNo", returnValue(dto.getSpayacctno()));// 付款人账号
			vouchermap.put("PayAcctName", returnValue(dto.getSpayacctname()));// 付款人名称
			vouchermap.put("PayAcctBankName", returnValue(dto
					.getSpayacctbankname()));// 付款人银行
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayeeacctno()));// 收款人账号
			vouchermap.put("PayeeAcctName",
					returnValue(dto.getSpayeeacctname()));// 收款人名称
			vouchermap.put("PayeeAcctBankName", returnValue(dto
					.getSpayeeacctbankname()));// 收款人银行
			vouchermap.put("PayeeAcctBankNo", returnValue(dto
					.getSpayeeacctbankname()));// 收款银行行号
			vouchermap.put("PaySummaryCode", returnValue(""));// 用途编码
			vouchermap.put("PaySummaryName", returnValue(""));// 用途名称
			vouchermap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(dto.getNmoney()));// 汇总退款金额
			vouchermap.put("Remark", returnValue(dto.getSdemo())); // 备注信息
			vouchermap.put("Hold1", returnValue(dto.getSdemo()));// 预留字段1附言占用"000043100019".equals(ITFECommonConstant.SRC_NODE)?dto.getSdemo():""
			vouchermap.put("Hold2", returnValue(dto.getStradekind()));// 预留字段2

			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			Detailmap.put("Id", dto.getSdealno()); // 退款通知书明细编号
			Detailmap.put("VoucherBillId", dto.getSdealno()); // 退款通知书Id
			Detailmap.put("BgtTypeCode", returnValue("")); // 预算类型编码
			Detailmap.put("BgtTypeName", returnValue("")); // 预算类型名称
			Detailmap.put("ProCatCode", returnValue("")); // 收支管理编码
			Detailmap.put("ProCatName", returnValue("")); // 收支管理名称
			Detailmap.put("PayeeAcctNo", returnValue(dto.getSpayeeacctno()));// 收款人账号
			Detailmap.put("PayeeAcctName",returnValue(dto.getSpayeeacctname()));// 收款人名称
			Detailmap.put("PayeeAcctBankName", returnValue(dto.getSpayeeacctbankname()));// 收款人银行
			Detailmap.put("PayeeAcctBankNo", returnValue(dto.getSpayeeacctbankname()));// 收款银行行号
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				Detailmap.put("ExpFuncCode", returnValue("99999")); // 支出功能分类科目编码
				Detailmap.put("ExpFuncName", returnValue("99999")); // 支出功能分类科目名称
				Detailmap.put("AgencyCode", returnValue("99999")); // 预算单位编码
				Detailmap.put("AgencyName", returnValue("99999")); // 预算单位名称
			} else {
				Detailmap.put("AgencyCode", returnValue("")); // 预算单位编码
				Detailmap.put("AgencyName", returnValue("")); // 预算单位名称
				Detailmap.put("ExpFuncCode", returnValue("")); // 支出功能分类科目编码
				Detailmap.put("ExpFuncName", returnValue("")); // 支出功能分类科目名称
			}
			Detailmap.put("ExpFuncCode1", returnValue("-"));// 支出功能分类科目编码
			Detailmap.put("ExpFuncName1", returnValue("-"));// 支出功能分类科目名称
			Detailmap.put("ExpFuncCode2", returnValue("-"));// 支出功能分类科目编码
			Detailmap.put("ExpFuncName2", returnValue("-"));// 支出功能分类科目名称
			Detailmap.put("ExpFuncCode3", returnValue("-"));// 支出功能分类科目编码
			Detailmap.put("ExpFuncName3", returnValue("-"));// 支出功能分类科目名称
			Detailmap.put("ExpEcoCode", returnValue("-")); // 支出经济分类科目编码
			Detailmap.put("ExpEcoName", returnValue("-")); // 支出经济分类科目名称
			Detailmap.put("Amt", "-"
					+ MtoCodeTrans.transformString(dto.getNmoney())); // 退款金额
			Detailmap.put("Hold1", returnValue("")); // 预留字段1
			Detailmap.put("Hold2", returnValue("")); // 预留字段2
			Detailmap.put("Hold3", returnValue("")); // 预留字段3
			Detailmap.put("Hold4", returnValue("")); // 预留字段4
			Detail.add(Detailmap);

			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！", e);
		}
	}

	/**
	 * 杭州实拨资金退款_TCBS资金报文导入(20141015_V2.0最新接口)
	 * 
	 * @param lists
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> tranforPayoutBackForHZ(List lists)
			throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) lists
					.get(1);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", vDto.getSdealno());// 实拨退款通知书Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());// 业务年度
			vouchermap.put("VtCode", vDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());// 凭证日期
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// 凭证号
			vouchermap.put("AgentBusinessNo", returnValue(dto.getSdealno()));// 原银行交易流水号
			vouchermap.put("OriBillNo", returnValue(""));// 原拨款单单号
			vouchermap.put("OriVouDate", returnValue(dto.getShold2()));// 原拨款单凭证日期
			vouchermap.put("OriPayDate", returnValue(dto.getShold2()));// 原支付日期
			vouchermap.put("FundTypeCode", returnValue(""));// 资金性质编码
			vouchermap.put("FundTypeName", returnValue(""));// 资金性质名称
			vouchermap.put("PayTypeCode", returnValue(dto.getSvtcodedes()));// 支付方式编码
			vouchermap.put("PayTypeName", returnValue(dto.getStradekind()));// 支付方式名称
			vouchermap.put("ProCatCode", returnValue(""));// 收支管理编码
			vouchermap.put("ProCatName", returnValue(""));// 收支管理名称
			vouchermap.put("AgencyCode", returnValue("")); // 预算单位编码
			vouchermap.put("AgencyName", returnValue("")); // 预算单位名称

			vouchermap.put("PayAcctNo", returnValue(dto.getSpayeeacctno()));// 原付款人账号
			vouchermap.put("PayAcctName", returnValue(dto.getSpayeeacctname()));// 原付款人名称
			HashMap<String, TsPaybankDto> bankmap = null;
			if (StringUtils.isBlank(dto.getShold1())
					&& !StringUtils.isBlank(dto.getSreceivebankno())) {
				bankmap = SrvCacheFacade.cachePayBankInfo();
				TsPaybankDto paybankDto = bankmap.get(dto.getSreceivebankno());
				vouchermap.put("PayAcctBankName", returnValue(paybankDto
						.getSbankname()));// 原收款人银行
			} else {
				vouchermap.put("PayAcctBankName", returnValue(dto.getShold1()));// 原收款人银行
			}

			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayacctno()));// 原收款人账号
			vouchermap.put("PayeeAcctName", returnValue(dto.getSpayacctname()));// 原收款人名称
			vouchermap.put("PayeeAcctBankName", returnValue(dto
					.getSforwardbankname()));// 原收款人银行
			vouchermap.put("PayeeAcctBankNo", returnValue(dto
					.getSpayeeacctbankname()));// 原收款银行行号

			vouchermap.put("PaySummaryCode", returnValue(""));// 用途编码
			vouchermap.put("PaySummaryName", returnValue(""));// 用途名称
			vouchermap.put("Remark", returnValue(dto.getSdemo()));// 退库原因
			vouchermap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(dto.getNmoney()));// 汇总退款金额
			vouchermap.put("Hold1", returnValue(dto.getSdemo()));// 预留字段1附言占用"000043100019".equals(ITFECommonConstant.SRC_NODE)?dto.getSdemo():""
			vouchermap.put("Hold2", returnValue(""));// 预留字段2

			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			Detailmap.put("Id", dto.getSdealno()); // 退款通知书明细编号
			Detailmap.put("VoucherBillId", vDto.getSdealno()); // 退款通知书Id
			Detailmap.put("BgtTypeCode", returnValue("")); // 预算类型编码
			Detailmap.put("BgtTypeName", returnValue("")); // 预算类型名称
			Detailmap.put("ProCatCode", returnValue("")); // 收支管理编码
			Detailmap.put("ProCatName", returnValue("")); // 收支管理名称
			Detailmap.put("AgencyCode", returnValue("")); // 预算单位编码
			Detailmap.put("AgencyName", returnValue("")); // 预算单位名称
			Detailmap.put("PayeeAcctNo", returnValue(dto.getSpayacctno()));// 原收款人账号
			Detailmap.put("PayeeAcctName", returnValue(dto.getSpayacctname()));// 原收款人名称
			Detailmap.put("PayeeAcctBankName", returnValue(dto.getSforwardbankname()));// 原收款人银行
			Detailmap.put("PayeeAcctBankNo", returnValue(dto.getSpayeeacctbankname()));// 原收款银行行号
			Detailmap.put("ExpFuncCode", returnValue("-")); // 支出功能分类科目编码
			Detailmap.put("ExpFuncName", returnValue("-")); // 支出功能分类科目名称
			Detailmap.put("ExpFuncCode1", returnValue("-"));// 支出功能分类科目编码
			Detailmap.put("ExpFuncName1", returnValue("-"));// 支出功能分类科目名称
			Detailmap.put("ExpFuncCode2", returnValue("-"));// 支出功能分类科目编码
			Detailmap.put("ExpFuncName2", returnValue("-"));// 支出功能分类科目名称
			Detailmap.put("ExpFuncCode3", returnValue("-"));// 支出功能分类科目编码
			Detailmap.put("ExpFuncName3", returnValue("-"));// 支出功能分类科目名称
			Detailmap.put("ExpEcoCode", returnValue("-")); // 支出经济分类科目编码
			Detailmap.put("ExpEcoName", returnValue("-")); // 支出经济分类科目名称
			Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(dto.getNmoney())); // 退款金额
			Detailmap.put("Hold1", returnValue("")); // 预留字段1
			Detailmap.put("Hold2", returnValue("")); // 预留字段2
			Detailmap.put("Hold3", returnValue("")); // 预留字段3
			Detailmap.put("Hold4", returnValue("")); // 预留字段4
			Detail.add(Detailmap);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！", e);
		}
	}

	/**
	 * DTO转化XML报文
	 * 
	 * @param List
	 *            生成报文要素集合
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map tranfor(List lists) throws ITFEBizException {
		try {
			// 杭州实拨资金退款(最新接口20141015_V2.0版本),杭州节点代码201057100006,宁波节点代码000057400006
			if (ITFECommonConstant.SRC_NODE.equals("201057100006")
					|| ITFECommonConstant.SRC_NODE.equals("000057400006")) {
				return tranforForHZ(lists);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvPayoutmsgmainDto dto = (TvPayoutmsgmainDto) lists.get(1);// 实拨资金拨款凭证主表
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(2);// 实拨资金拨款索引表
			List<TvPayoutmsgsubDto> sublist = (List<TvPayoutmsgsubDto>) lists
					.get(3);// 实拨资金拨款明细
			// 实拨资金退回原因
			String payoutBackReason = (String) lists.get(4);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", vDto.getSdealno());// 实拨退款通知书Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());// 业务年度
			vouchermap.put("VtCode", vDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());// 凭证日期
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// 凭证号
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();

			vouchermap.put("AgentBusinessNo", returnValue(dto
					.getSxagentbusinessno()));// 原银行交易流水号
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				vouchermap.put("OriBillNo",returnValue(sublist.get(0).getSid()));// 原拨款凭证的拨款明细编号
			} else {
				vouchermap.put("OriBillNo",returnValue(dto.getStaxticketno()));// 原拨款单单号
			}
			vouchermap.put("OriVouDate", returnValue(dto.getSgenticketdate()));// 原拨款单凭证日期
			vouchermap.put("OriPayDate", returnValue(dto.getSxpaydate()));// 原支付日期
			vouchermap.put("FundTypeCode", dto.getSfundtypecode());// 资金性质编码
			vouchermap.put("FundTypeName", dto.getSfundtypename());// 资金性质名称
			vouchermap.put("PayTypeCode", dto.getSpaytypecode());// 支付方式编码
			vouchermap.put("PayTypeName", dto.getSpaytypename());// 支付方式名称
			vouchermap.put("AgencyCode", returnValue(dto.getSbudgetunitcode())); // 预算单位编码
			vouchermap.put("AgencyName", returnValue(dto.getSunitcodename())); // 预算单位名称
			vouchermap.put("ProCatCode", returnValue(""));// 收支管理编码
			vouchermap.put("ProCatName", returnValue(""));// 收支管理名称
			
			vouchermap.put("PayAcctNo", dto.getSpayeracct());// 付款人账号vouchermap.put("PayAcctNo",// dto.getSpayeracct());
			vouchermap.put("PayAcctName",dto.getSpayername() );// 付款人名称vouchermap.put("PayAcctName",// dto.getSpayername());
			vouchermap.put("PayAcctBankName", dto.getSpayerbankname());// 付款人银行vouchermap.put("PayAcctBankName",// dto.getSpayerbankname());
			vouchermap.put("PayeeAcctNo", dto.getSrecacct());// 收款人账号vouchermap.put("PayeeAcctNo",// dto.getSrecacct());
			vouchermap.put("PayeeAcctName", dto.getSrecname());// 收款人名称vouchermap.put("PayeeAcctName",// dto.getSrecname());
			vouchermap.put("PayeeAcctBankName",dto.getSrecbankname() );// 收款人银行vouchermap.put("PayeeAcctBankName",// dto.getSrecbankname());
			TsPaybankDto paydto = new TsPaybankDto();
			paydto.setSpaybankname(dto.getSrecbankname());
			List<TsPaybankDto> payList = CommonFacade.getODB().findRsByDto(paydto);
			vouchermap.put("PayeeAcctBankNo", payList != null&& payList.size() > 0 ? returnValue(payList.get(0).getSpaybankno()): returnValue(dto.getSrecbankno()));// 收款银行行号vouchermap.put("PayeeAcctBankNo",// returnValue(dto.getSrecbankno()));	
			
			vouchermap.put("PaySummaryCode", returnValue(dto.getSpaysummarycode()));// 用途编码
			vouchermap.put("PaySummaryName", returnValue(dto.getSpaysummaryname()));// 用途名称
			vouchermap.put("PayAmt", "-"+ MtoCodeTrans.transformString(dto.getShold2()));// 汇总退款金额
			vouchermap.put("Remark", returnValue(payoutBackReason)); // 备注信息
			if(ITFECommonConstant.PUBLICPARAM.contains(",jilin,")){
				vouchermap.put("Hold1", returnValue(payoutBackReason));///吉林 以前使用shold1字段作为附言使用
			}else{
				vouchermap.put("Hold1", returnValue(dto.getShold1()));// 预留字段1 
			}
			vouchermap.put("Hold2", returnValue(""));// 预留字段2
			TsTreasuryDto tredto = SrvCacheFacade.cacheTreasuryInfo(null).get(dto.getStrecode());
			for (TvPayoutmsgsubDto subdto : sublist) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", dto.getSdealno() + subdto.getSbizno()); // 退款通知书明细编号
				Detailmap.put("VoucherBillId", vDto.getSdealno()); // 退款通知书Id
				Detailmap.put("BgtTypeCode", returnValue(subdto.getSbgttypecode())); // 预算类型编码
				Detailmap.put("BgtTypeName", returnValue(subdto.getSbgttypename())); // 预算类型名称
				Detailmap.put("ProCatCode",returnValue(subdto.getSprocatcode())); // 收支管理编码
				Detailmap.put("ProCatName",returnValue(subdto.getSprocatname())); // 收支管理名称
				Detailmap.put("AgencyCode", subdto.getSagencycode()); // 预算单位编码
				Detailmap.put("AgencyName", subdto.getSagencyname()); // 预算单位名称
				/*Detailmap.put("PayeeAcctNo", dto.getSpayeracct());// 收款人账号vouchermap.put("PayeeAcctNo",// dto.getSrecacct());
				Detailmap.put("PayeeAcctName", dto.getSpayername());// 收款人名称vouchermap.put("PayeeAcctName",// dto.getSrecname());
				Detailmap.put("PayeeAcctBankName", dto.getSpayerbankname());// 收款人银行
				Detailmap.put("PayeeAcctBankNo", payList != null&& payList.size() > 0 ? returnValue(payList.get(0).getSpaybankno()): returnValue(dto.getSrecbankno()));// 收款银行行号
			*/	
				//曹艳国修改一下四行
				Detailmap.put("PayeeAcctNo", dto.getSrecacct());// 收款人账号vouchermap.put("PayeeAcctNo",// dto.getSrecacct());
				Detailmap.put("PayeeAcctName", dto.getSrecname());// 收款人名称vouchermap.put("PayeeAcctName",// dto.getSrecname());
				Detailmap.put("PayeeAcctBankName", dto.getSrecbankname());// 收款人银行
				Detailmap.put("PayeeAcctBankNo", payList != null&& payList.size() > 0 ? returnValue(payList.get(0).getSpaybankno()): returnValue(dto.getSrecbankno()));// 收款银行行号
				
				Detailmap.put("VoucherDetailNo", (dto.getStaxticketno()==null||"".equals(dto.getStaxticketno()))?"12345678":dto.getStaxticketno());//原凭证编号
				Detailmap.put("ExpFuncCode", subdto.getSfunsubjectcode()); // 支出功能分类科目编码
				Detailmap.put("ExpFuncName", subdto.getSexpfuncname()); // 支出功能分类科目名称
				Detailmap.put("ExpFuncCode1", "-");// 支出功能分类科目编码
				Detailmap.put("ExpFuncName1", "-");// 支出功能分类科目名称
				Detailmap.put("ExpFuncCode2", "-");// 支出功能分类科目编码
				Detailmap.put("ExpFuncName2", "-");// 支出功能分类科目名称
				Detailmap.put("ExpFuncCode3", "-");// 支出功能分类科目编码
				Detailmap.put("ExpFuncName3", "-");// 支出功能分类科目名称
				Detailmap.put("ExpEcoCode",returnValue(subdto.getSexpecocode())); // 支出经济分类科目编码
				Detailmap.put("ExpEcoName",returnValue(subdto.getSexpeconame())); // 支出经济分类科目名称
				if (dto.getNmoney().compareTo(new BigDecimal(dto.getShold2())) == 0) {
					Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(subdto.getNmoney())); // 全额退款金额
				} else {
					Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(dto.getShold2())); // 部分退款金额
				}
				Detailmap.put("Hold1", returnValue(subdto.getShold1())); // 预留字段1
				Detailmap.put("Hold2", returnValue(subdto.getShold2())); // 预留字段2
				Detailmap.put("Hold3", returnValue(subdto.getShold3())); // 预留字段3
				Detailmap.put("Hold4", returnValue(subdto.getShold4())); // 预留字段4
				//拨款清单模式
				if (StateConstant.COMMON_YES.equals(String.valueOf(tredto==null?"":tredto.getSpayunitname()))) {
					vouchermap.put("PayeeAcctNo", subdto.getSpayeeacctno());// 收款人账号vouchermap.put("PayeeAcctNo",// dto.getSrecacct());
					vouchermap.put("PayeeAcctName", subdto.getSpayeeacctname());// 收款人名称vouchermap.put("PayeeAcctName",// dto.getSrecname());
					vouchermap.put("PayeeAcctBankName",subdto.getSpayeeacctbankname() );// 收款人银行vouchermap.put("PayeeAcctBankName",// dto.getSrecbankname());
				}
				Detail.add(Detailmap);
			}
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！", e);
		}
	}

	/**
	 * 杭州实拨资金退款_前置发起（最新接口20141015_V2.0）
	 * 
	 * @param lists
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> tranforForHZ(List lists) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvPayoutmsgmainDto dto = (TvPayoutmsgmainDto) lists.get(1);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(2);
			List<TvPayoutmsgsubDto> sublist = (List<TvPayoutmsgsubDto>) lists
					.get(3);
			// 实拨资金退回原因
			String payoutBackReason = (String) lists.get(4);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", vDto.getSdealno());// 实拨退款通知书Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());// 业务年度
			vouchermap.put("VtCode", vDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());// 凭证日期
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// 凭证号
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();

			vouchermap.put("AgentBusinessNo", returnValue(dto
					.getSxagentbusinessno()));// 原银行交易流水号
			vouchermap.put("OriBillNo", returnValue(dto.getStaxticketno()));// 原拨款单单号
			vouchermap.put("OriVouDate", returnValue(dto.getSgenticketdate()));// 原拨款单凭证日期
			vouchermap.put("OriPayDate", returnValue(dto.getSxpaydate()));// 原支付日期
			vouchermap.put("FundTypeCode", dto.getSfundtypecode());// 资金性质编码
			vouchermap.put("FundTypeName", dto.getSfundtypename());// 资金性质名称
			vouchermap.put("PayTypeCode", dto.getSpaytypecode());// 支付方式编码
			vouchermap.put("PayTypeName", dto.getSpaytypename());// 支付方式名称
			vouchermap.put("ProCatCode", returnValue(""));// 收支管理编码
			vouchermap.put("ProCatName", returnValue(""));// 收支管理名称

			vouchermap.put("PayAcctNo", returnValue(dto.getSpayeracct()));// 原付款人账号
			vouchermap.put("PayAcctName", returnValue(dto.getSpayername()));// 原付款人名称
			vouchermap.put("PayAcctBankName", returnValue(dto
					.getSpayerbankname()));// 原付款人银行

			vouchermap.put("PayeeAcctNo", returnValue(dto.getSrecacct()));// 原收款人账号
			vouchermap.put("PayeeAcctName", returnValue(dto.getSrecname()));// 原收款人名称
			vouchermap.put("PayeeAcctBankName", returnValue(dto
					.getSrecbankname()));// 原收款人银行
			vouchermap.put("PayeeAcctBankNo", returnValue(dto.getSrecbankno()));// 原收款银行行号

			vouchermap.put("PaySummaryCode", returnValue(dto
					.getSpaysummarycode()));// 用途编码
			vouchermap.put("PaySummaryName", returnValue(dto.getSaddword()));// 用途名称
			// 杭州节点代码20105710000
			if (ITFECommonConstant.SRC_NODE.equals("201057100006")) {
				vouchermap.put("Remark", returnValue(payoutBackReason));// 退库原因
				vouchermap.put("Hold1", returnValue(dto.getShold1()));// 预留字段1
			} else {
				vouchermap.put("Hold1", returnValue(payoutBackReason));// 预留字段1(退款原因,宁波用)
			}
			vouchermap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(dto.getShold2()));// 汇总退款金额
			vouchermap.put("Hold2", returnValue(""));// 预留字段2
			String subjectCode = "";
			String subjectName = "";
			for (TvPayoutmsgsubDto subdto : sublist) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", subdto.getSbizno()); // 退款通知书明细编号
				Detailmap.put("VoucherBillId", vDto.getSdealno()); // 退款通知书Id
				Detailmap.put("BgtTypeCode", returnValue(subdto
						.getSbgttypecode())); // 预算类型编码
				Detailmap.put("BgtTypeName", returnValue(subdto
						.getSbgttypename())); // 预算类型名称
				Detailmap.put("ProCatCode",
						returnValue(subdto.getSprocatcode())); // 收支管理编码
				Detailmap.put("ProCatName",
						returnValue(subdto.getSprocatname())); // 收支管理名称
				subjectCode = subdto.getSagencycode();
				subjectName = subdto.getSagencyname();
				// 杭州节点代码20105710000
				if (ITFECommonConstant.SRC_NODE.equals("201057100006")) {
					Detailmap.put("AgencyCode", subjectCode); // 预算单位编码
					Detailmap.put("AgencyName", subjectName); // 预算单位名称
					Detailmap
							.put("PayeeAcctNo", returnValue(dto.getSrecacct()));// 原收款人账号
					Detailmap.put("PayeeAcctName", returnValue(dto
							.getSrecname()));// 原收款人名称
					Detailmap.put("PayeeAcctBankName", returnValue(dto
							.getSrecbankname()));// 原收款人银行
					Detailmap.put("PayeeAcctBankNo", returnValue(dto
							.getSrecbankno()));// 原收款银行行号
				}
				Detailmap.put("ExpFuncCode", subdto.getSfunsubjectcode()); // 支出功能分类科目编码
				Detailmap.put("ExpFuncName", subdto.getSexpfuncname()); // 支出功能分类科目名称
				Detailmap.put("ExpEcoCode",
						returnValue(subdto.getSexpecocode())); // 支出经济分类科目编码
				Detailmap.put("ExpEcoName",
						returnValue(subdto.getSexpeconame())); // 支出经济分类科目名称
				if (dto.getNmoney().compareTo(new BigDecimal(dto.getShold2())) == 0) {
					Detailmap.put("Amt", "-"
							+ MtoCodeTrans.transformString(subdto.getNmoney())); // 全额退款金额
				} else {
					Detailmap.put("Amt", "-"
							+ MtoCodeTrans.transformString(dto.getShold2())); // 部分退款金额
				}
				Detailmap.put("ExpFuncCode1", returnValue(""));// 支出功能分类科目编码
				Detailmap.put("ExpFuncName1", returnValue(""));// 支出功能分类科目名称
				Detailmap.put("ExpFuncCode2", returnValue(""));// 支出功能分类科目编码
				Detailmap.put("ExpFuncName2", returnValue(""));// 支出功能分类科目名称
				Detailmap.put("ExpFuncCode3", returnValue(""));// 支出功能分类科目编码
				Detailmap.put("ExpFuncName3", returnValue(""));// 支出功能分类科目名称
				Detailmap.put("Hold1", returnValue(subdto.getShold1())); // 预留字段1
				Detailmap.put("Hold2", returnValue(subdto.getShold2())); // 预留字段2
				Detailmap.put("Hold3", returnValue(subdto.getShold3())); // 预留字段3
				Detailmap.put("Hold4", returnValue(subdto.getShold4())); // 预留字段4
				Detail.add(Detailmap);
			}
			vouchermap.put("AgencyCode", subjectCode); // 预算单位编码
			vouchermap.put("AgencyName", subjectName); // 预算单位名称
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！", e);
		}
	}

	/**
	 * DTO转化XML报文(根据TCbs回执生成方式)
	 * 
	 * @param List
	 *            生成报文要素集合
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map tranforForTCBS(TvPayoutbackmsgMainDto maindto,
			TvVoucherinfoDto voucherdto) throws ITFEBizException {
		try {
			// 杭州实拨资金退款_TCBS回执(最新接口20141015_V2.0版本),杭州节点代码201057100006,宁波节点代码000057400006
			if (ITFECommonConstant.SRC_NODE.equals("201057100006")
					|| ITFECommonConstant.SRC_NODE.equals("000057400006")) {
				return tranforForTCBSForHZ(maindto, voucherdto);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", voucherdto.getSdealno());// 实拨退款通知书Id
			vouchermap.put("AdmDivCode", voucherdto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", voucherdto.getSstyear());// 业务年度
			vouchermap.put("VtCode", voucherdto.getSvtcode());// 凭证类型编号

			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			vouchermap.put("VouDate", maindto.getSvoudate());// 凭证日期
			vouchermap.put("VoucherNo", maindto.getSvouno());// 凭证号
			vouchermap.put("AgentBusinessNo", returnValue(maindto.getSoritrano()));// 原银行交易流水号
			HashMap<String, TsPaybankDto>  bankmap = null;
			try {
				bankmap = SrvCacheFacade.cachePayBankInfo();
			} catch (JAFDatabaseException e1) {
				logger.error("获取行名对照信息出错："+e1);
			}
			//查找实拨资金原信息，部分需要字段需要返填
			TvPayoutmsgmainDto payinfo = null;
			TvPayoutmsgmainDto payoutdto = new TvPayoutmsgmainDto();
			payoutdto.setStaxticketno(maindto.getSorivouno());
			List payoutlist = CommonFacade.getODB().findRsByDto(payoutdto);
			if (payoutlist.size()>0) {
				payinfo = (TvPayoutmsgmainDto) payoutlist.get(0);
			}else{
				HtvPayoutmsgmainDto hpayoutdto = new HtvPayoutmsgmainDto();
				hpayoutdto.setStaxticketno(maindto.getSorivouno());
				List hpayoutlist = CommonFacade.getODB().findRsByDto(hpayoutdto);
				if (hpayoutlist.size()>0) {
					payinfo = (TvPayoutmsgmainDto) hpayoutlist.get(0);
				}
			}
			if (null==payinfo) {
				payinfo = new TvPayoutmsgmainDto();
			}
			
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				List<TvPayoutmsgsubDto> payoutSublist = this
						.findPayoutSubDtoByMain(maindto);
				if (payoutSublist == null || payoutSublist.size() == 0) {
					vouchermap.put("OriBillNo", "");
				} else {
					vouchermap.put("OriBillNo", returnValue(payoutSublist
							.get(0).getSid()));// 原拨款凭证的拨款明细编号
				}
				vouchermap.put("FundTypeCode", payinfo.getSfundtypecode()==null ? "1" : payinfo.getSfundtypecode());// 资金性质编码
				vouchermap.put("FundTypeName", payinfo.getSfundtypename()==null ? "预算管理资金" :payinfo.getSfundtypename());// 资金性质名称
				vouchermap.put("Remark", returnValue(maindto.getSdemo())); // 备注信息
			} else {
				vouchermap.put("OriBillNo", returnValue(maindto.getSorivouno()));// 原拨款单单号
				vouchermap.put("FundTypeCode", payinfo.getSfundtypecode()==null ? "1" : payinfo.getSfundtypecode());// 资金性质编码
				vouchermap.put("FundTypeName", payinfo.getSfundtypename()==null ? "预算管理资金" :payinfo.getSfundtypename());// 资金性质名称
				vouchermap.put("AgencyCode", maindto.getSbudgetunitcode()==null ? payinfo.getSbudgetunitcode() : maindto.getSbudgetunitcode());// 预算单位编码
				vouchermap.put("AgencyName",returnValue(maindto.getSunitcodename()==null ? payinfo.getSunitcodename() : maindto.getSunitcodename())); // 预算单位名称
				vouchermap.put("Remark", returnValue(maindto.getSdemo())); // 备注信息
			}
			vouchermap.put("OriVouDate", returnValue(maindto.getSvoudate()));// 原拨款单凭证日期
			vouchermap.put("OriPayDate", returnValue(maindto.getSorivoudate()));// 原支付日期
			vouchermap.put("PayTypeCode", "91");// 支付方式编码
			vouchermap.put("PayTypeName", "实拨退回");// 支付方式名称
			vouchermap.put("ProCatCode", returnValue(""));// 收支管理编码
			vouchermap.put("ProCatName", returnValue(""));// 收支管理名称
			vouchermap.put("PayeeAcctNo", returnValue(maindto.getSpayeracct()));// 收款人账号
			vouchermap.put("PayeeAcctName", returnValue(maindto.getSpayername()));// 收款人名称
			TsPaybankDto paydto = bankmap.get(maindto.getSpayername());
			vouchermap.put("PayeeAcctBankName", payinfo.getSrecbankname());// 收款人银行
			
			if (!StringUtils.isBlank(maindto.getSpayeeopbkno())) {// 收款人开户行行号
				bankmap = SrvCacheFacade.cachePayBankInfo();
				TsPaybankDto paybankDto = bankmap
						.get(maindto.getSpayeeopbkno());
				vouchermap.put("PayAcctBankName", returnValue(paybankDto
						.getSbankname()));// 原付款人银行
			} else {
				vouchermap.put("PayAcctBankName", returnValue(maindto
						.getSpayeebankno()) );// 原付款人银行
			}
			if(vouchermap.get("PayeeAcctBankName")==null)
				vouchermap.put("PayeeAcctBankName", returnValue("--"));// 收款人银行
			if(vouchermap.get("PayAcctBankName")==null)
				vouchermap.put("PayAcctBankName", returnValue("--"));
			vouchermap.put("PayAcctNo", returnValue(maindto.getSpayeeacct()));// 付款人账号
			vouchermap.put("PayAcctName", returnValue(maindto.getSpayeename()));// 付款人名称
			vouchermap.put("PayeeAcctBankNo", paydto!=null ? returnValue(paydto.getSbankno()): returnValue(payinfo.getSrecbankno()));// 原收款银行行号
			vouchermap.put("PayeeAcctBankName", paydto!=null ? returnValue(paydto.getSbankname()) : returnValue(payinfo.getSrecbankname()));// 原收款银行名称
			vouchermap.put("PaySummaryCode", payinfo.getSpaysummarycode()==null ? "" : payinfo.getSpaysummarycode() );// 用途编码
			vouchermap.put("PaySummaryName", payinfo.getSpaysummaryname()==null ? maindto.getSbckreason() : payinfo.getSpaysummaryname());// 用途名称
			vouchermap.put("PayAmt", "-"+ MtoCodeTrans.transformString(maindto.getNmoney()));// 汇总退款金额
			vouchermap.put("Hold1", (maindto.getSaddword()!=null&&maindto.getSaddword().getBytes().length>42)? CommonUtil.subString(maindto.getSaddword(),42) : maindto.getSaddword());// 预留字段1
			if(ITFECommonConstant.PUBLICPARAM.contains(",xm5207,"))
			{
				if(payoutdto!=null&&payoutdto.getShold1()!=null&&!"".equals(payoutdto.getShold1()))
					vouchermap.put("Hold1",payoutdto.getShold1());
				/*else if(hpayoutdto!=null&&hpayoutdto.getShold1()!=null&&!"".equals(hpayoutdto.getShold1()))
					vouchermap.put("Hold1",hpayoutdto.getShold1());*/
			}
			vouchermap.put("Hold2", "");// 预留字段2
			
			for (int j = 0; j < sublist.size(); j++) {
				TvPayoutbackmsgSubDto subdto = (TvPayoutbackmsgSubDto) sublist
						.get(j);
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", maindto.getSvouno() + subdto.getSseqno()); // 退款通知书明细编号
				Detailmap.put("VoucherBillId", voucherdto.getSdealno()); // 退款通知书Id
				Detailmap.put("BgtTypeCode", "-"); // 预算类型编码
				Detailmap.put("BgtTypeName", "-"); // 预算类型名称
				Detailmap.put("ProCatCode", ""); // 收支管理编码
				Detailmap.put("ProCatName", ""); // 收支管理名称
				Detailmap.put("AgencyCode", maindto.getSbudgetunitcode()==null ? payinfo.getSbudgetunitcode() : maindto.getSbudgetunitcode()); // 预算单位编码
				Detailmap.put("AgencyName", returnValue(returnValue(maindto.getSunitcodename()==null ? payinfo.getSunitcodename() : maindto.getSunitcodename()))); // 预算单位名称
				Detailmap.put("PayeeAcctNo", returnValue(maindto.getSpayeracct()));// 原收款人账号
				Detailmap.put("PayeeAcctName", returnValue(maindto.getSpayername()));// 原收款人名称
				Detailmap.put("PayeeAcctBankName", paydto!=null ? returnValue(paydto.getSbankname()) : returnValue(payinfo.getSrecbankname()));// 原收款银行名称
				Detailmap.put("PayeeAcctBankNo", paydto!=null ? returnValue(paydto.getSbankno()): returnValue(payinfo.getSrecbankno()));// 原收款银行行号
				Detailmap.put("VoucherDetailNo", maindto.getSvouno());//原凭证编号
				Detailmap.put("ExpFuncCode", subdto.getSfunsubjectcode()); // 支出功能分类科目编码
				Detailmap.put("ExpFuncName", returnValue(subdto.getSfunsubjectname())); // 支出功能分类科目名称
				Detailmap.put("ExpFuncCode1", "-");// 支出功能分类科目编码
				Detailmap.put("ExpFuncName1", "-");// 支出功能分类科目名称
				Detailmap.put("ExpFuncCode2", "-");// 支出功能分类科目编码
				Detailmap.put("ExpFuncName2", "-");// 支出功能分类科目名称
				Detailmap.put("ExpFuncCode3", "-");// 支出功能分类科目编码
				Detailmap.put("ExpFuncName3", "-");// 支出功能分类科目名称
				Detailmap.put("ExpEcoCode", "-"); // 支出经济分类科目编码
				Detailmap.put("ExpEcoName", "-"); // 支出经济分类科目名称
				Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(subdto.getNmoney())); // 退款金额
				Detailmap.put("Hold1", returnValue(subdto.getShold1())); // 预留字段1
				Detailmap.put("Hold2", returnValue(subdto.getShold2())); // 预留字段2
				Detailmap.put("Hold3", returnValue(subdto.getShold3())); // 预留字段3
				Detailmap.put("Hold4", returnValue(subdto.getShold4())); // 预留字段4
				Detail.add(Detailmap);
			}
			/***
			 * 部分字段返填保存，用于生成3508使用
			 */
			maindto.setSbudgetunitcode(maindto.getSbudgetunitcode()==null ? payinfo.getSbudgetunitcode() : maindto.getSbudgetunitcode());
			maindto.setSunitcodename(returnValue(returnValue(maindto.getSunitcodename()==null ? payinfo.getSunitcodename() : maindto.getSunitcodename())));
			maindto.setShold1(payinfo.getSrecbankname());
			maindto.setShold2(payinfo.getSrecbankno());
			maindto.setShold3(payinfo.getSfundtypecode()==null ? "1" : payinfo.getSfundtypecode());
			maindto.setShold4(payinfo.getSfundtypename()==null ? "预算管理资金" :payinfo.getSfundtypename());
			DatabaseFacade.getODB().update(maindto);
			
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！", e);
		}
	}

	/**
	 * 杭州实拨资金退款_TCBS回执(最新接口20141015_V2.0版本)
	 * 
	 * @param maindto
	 * @param voucherdto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> tranforForTCBSForHZ(
			TvPayoutbackmsgMainDto maindto, TvVoucherinfoDto voucherdto)
			throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", voucherdto.getSdealno());// 实拨退款通知书Id
			vouchermap.put("AdmDivCode", voucherdto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", voucherdto.getSstyear());// 业务年度
			vouchermap.put("VtCode", voucherdto.getSvtcode());// 凭证类型编号

			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			vouchermap.put("VouDate", maindto.getSvoudate());// 凭证日期
			vouchermap.put("VoucherNo", maindto.getSvouno());// 凭证号
			vouchermap.put("AgentBusinessNo", returnValue(maindto
					.getSoritrano()));// 原银行交易流水号
			vouchermap.put("OriBillNo", returnValue(maindto.getSorivouno()));// 原拨款单单号
			vouchermap.put("OriVouDate", returnValue(maindto.getSorivoudate()));// 原拨款单凭证日期
			vouchermap.put("OriPayDate", returnValue(""));// 原支付日期
			vouchermap.put("FundTypeCode", "");// 资金性质编码
			vouchermap.put("FundTypeName", "");// 资金性质名称
			vouchermap.put("PayTypeCode", "91");// 支付方式编码
			vouchermap.put("PayTypeName", "实拨退回");// 支付方式名称
			vouchermap.put("ProCatCode", returnValue(""));// 收支管理编码
			vouchermap.put("ProCatName", returnValue(""));// 收支管理名称
			vouchermap.put("AgencyCode", maindto.getSbudgetunitcode()); // 预算单位编码
			vouchermap.put("AgencyName",
					returnValue(maindto.getSunitcodename())); // 预算单位名称

			vouchermap.put("PayAcctNo", returnValue(maindto.getSpayeeacct()));// 原付款人账号
			vouchermap.put("PayAcctName", returnValue(maindto.getSpayeename()));// 原付款人名称
			HashMap<String, TsPaybankDto> bankmap = null;
			if (!StringUtils.isBlank(maindto.getSpayeeopbkno())) {// 收款人开户行行号
				bankmap = SrvCacheFacade.cachePayBankInfo();
				TsPaybankDto paybankDto = bankmap
						.get(maindto.getSpayeeopbkno());
				vouchermap.put("PayAcctBankName", returnValue(paybankDto
						.getSbankname()));// 原付款人银行
			} else {
				vouchermap.put("PayAcctBankName", returnValue(maindto
						.getSpayeename()));// 原付款人银行
			}
			vouchermap.put("PayeeAcctNo", returnValue(maindto.getSpayeracct()));// 原收款人账号
			vouchermap.put("PayeeAcctName",
					returnValue(maindto.getSpayername()));// 原收款人名称
			TsPaybankDto paydto = new TsPaybankDto();
			paydto.setSbankname(maindto.getSpayername());
			List<TsPaybankDto> payList = CommonFacade.getODB().findRsByDto(paydto);
			vouchermap.put("PayeeAcctBankName", payList != null
					&& payList.size() > 0 ? payList.get(0).getSbankname()
					: returnValue(maindto.getSpayername()));// 原收款银行名称
			vouchermap.put("PayeeAcctBankNo", payList != null
					&& payList.size() > 0 ? payList.get(0).getSbankno()
					: returnValue(""));// 原收款银行行号

			vouchermap.put("PaySummaryCode", "");// 用途编码
			vouchermap
					.put("PaySummaryName", returnValue(maindto.getSaddword()));// 用途名称
			// 杭州节点代码20105710000
			if (ITFECommonConstant.SRC_NODE.equals("201057100006")) {
				vouchermap.put("Remark", returnValue(maindto.getSbckreason()));// 退款原因
				vouchermap.put("Hold1", "");// 预留字段1
			} else {
				vouchermap.put("Hold1", returnValue(maindto.getSbckreason()));// 预留字段1(退款原因,宁波用)
			}
			vouchermap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(maindto.getNmoney()));// 汇总退款金额
			vouchermap.put("Hold2", "");// 预留字段2
			for (int j = 0; j < sublist.size(); j++) {
				TvPayoutbackmsgSubDto subdto = (TvPayoutbackmsgSubDto) sublist
						.get(j);
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", subdto.getSseqno()); // 退款通知书明细编号
				Detailmap.put("VoucherBillId", voucherdto.getSdealno()); // 退款通知书Id
				Detailmap.put("BgtTypeCode", ""); // 预算类型编码
				Detailmap.put("BgtTypeName", ""); // 预算类型名称
				Detailmap.put("ProCatCode", ""); // 收支管理编码
				Detailmap.put("ProCatName", ""); // 收支管理名称
				// 杭州节点代码20105710000
				if (ITFECommonConstant.SRC_NODE.equals("201057100006")) {
					Detailmap.put("AgencyCode", maindto.getSbudgetunitcode()); // 预算单位编码
					Detailmap.put("AgencyName", returnValue(maindto.getSunitcodename())); // 预算单位名称
					Detailmap.put("PayeeAcctNo", returnValue(maindto.getSpayeracct()));// 原收款人账号
					Detailmap.put("PayeeAcctName", returnValue(maindto.getSpayername()));// 原收款人名称
					Detailmap.put("PayeeAcctBankName", payList != null&& payList.size() > 0 ? payList.get(0).getSbankname() : returnValue(maindto.getSpayername()));// 原收款银行名称
					Detailmap.put("PayeeAcctBankNo", payList != null&& payList.size() > 0 ? payList.get(0).getSbankno(): returnValue(""));// 原收款银行行号
				}
				Detailmap.put("ExpFuncCode", subdto.getSfunsubjectcode()); // 支出功能分类科目编码
				Detailmap.put("ExpFuncName", returnValue(subdto.getSfunsubjectname())); // 支出功能分类科目名称
				Detailmap.put("ExpEcoCode", ""); // 支出经济分类科目编码
				Detailmap.put("ExpEcoName", ""); // 支出经济分类科目名称
				Detailmap.put("Amt", "-"
						+ MtoCodeTrans.transformString(subdto.getNmoney())); // 退款金额
				Detailmap.put("Hold1", returnValue(subdto.getShold1())); // 预留字段1
				Detailmap.put("Hold2", returnValue(subdto.getShold2())); // 预留字段2
				Detailmap.put("Hold3", returnValue(subdto.getShold3())); // 预留字段3
				Detailmap.put("Hold4", returnValue(subdto.getShold4())); // 预留字段4
				Detail.add(Detailmap);
			}
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！", e);
		}
	}

	/**
	 * 根据原凭证号查找原拨款凭证的拨款明细编号
	 * 
	 * @param maindto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List<TvPayoutmsgsubDto> findPayoutSubDtoByMain(
			TvPayoutbackmsgMainDto maindto) throws ITFEBizException {
		TvPayoutmsgmainDto payoutMainDto = new TvPayoutmsgmainDto();
		payoutMainDto.setSorgcode(maindto.getSorgcode());
		payoutMainDto.setStrecode(maindto.getStrecode());
		payoutMainDto.setStaxticketno(maindto.getSorivouno());
		payoutMainDto.setSgenticketdate(maindto.getSorivoudate());
		// payoutMainDto.setSbusinesstypecode(StateConstant.BIZTYPE_CODE_BATCH);
		try {
			List<TvPayoutmsgmainDto> payoutMainDtoList = CommonFacade.getODB()
					.findRsByDto(payoutMainDto);
			if (null != payoutMainDtoList && payoutMainDtoList.size() > 0) {
				TvPayoutmsgsubDto paySubDto = new TvPayoutmsgsubDto();
				paySubDto.setSbizno(payoutMainDtoList.get(0).getSbizno());
				List<TvPayoutmsgsubDto> payDetailDtoList = CommonFacade
						.getODB().findRsByDto(paySubDto);
				if (null != payDetailDtoList && payDetailDtoList.size() > 0) {
					return payDetailDtoList;
				}
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("根据原凭证号查找原拨款凭证的拨款明细编号出错！", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("根据原凭证号查找原拨款凭证的拨款明细编号出错！", e);
		}
		return null;
	}

	private String returnValue(String value) {
		if (value == null || "".equals(value)) {
			return "";
		} else {
			return value;
		}
	}

	@SuppressWarnings("unchecked")
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

}

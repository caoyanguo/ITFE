package com.cfcc.itfe.util.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

public class VoucherDto2MapFor3268 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3268.class);

	@SuppressWarnings("unchecked")
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException {
		List list = findMainDto(vDto);
		if (list.size() == 0)
			return list;
		List lists = new ArrayList();
		for (TvVoucherinfoAllocateIncomeDto mainDto : (List<TvVoucherinfoAllocateIncomeDto>) list) {
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
			vouchermap.put("OriBillNo", returnValue(dto.getSoripaydealno()));// 原拨款单单号
			vouchermap.put("OriVouDate", returnValue(dto.getShold2()));// 原拨款单凭证日期
			vouchermap.put("OriPayDate", returnValue(dto.getShold2()));// 原支付日期
			vouchermap.put("FundTypeCode", returnValue("1"));// 资金性质编码
			vouchermap.put("FundTypeName", returnValue("预算管理资金"));// 资金性质名称
			vouchermap.put("PayTypeCode", returnValue(dto.getSvtcodedes()));// 支付方式编码
			vouchermap.put("PayTypeName", returnValue(dto.getStradekind()));// 支付方式名称
			vouchermap.put("ProCatCode", returnValue("99999"));// 收支管理编码
			vouchermap.put("ProCatName", returnValue("99999"));// 收支管理名称
			vouchermap.put("AgencyCode", returnValue("99999")); // 预算单位编码
			vouchermap.put("AgencyName", returnValue("99999")); // 预算单位名称
			vouchermap.put("PayAcctNo", returnValue(dto.getSpayacctno()));// 付款人账号
			vouchermap.put("PayAcctName", returnValue(dto.getSpayacctname()));// 付款人名称
			vouchermap.put("PayAcctBankName", returnValue(dto.getSpayacctbankname()));// 付款人银行
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayeeacctno()));// 收款人账号
			vouchermap.put("PayeeAcctName",returnValue(dto.getSpayeeacctname()));// 收款人名称
			vouchermap.put("PayeeAcctBankName", returnValue(dto.getSpayeeacctbankname()));// 收款人银行
			vouchermap.put("PayeeAcctBankNo", returnValue(dto.getSpayeeacctbankname()));// 收款银行行号
			vouchermap.put("PaySummaryCode", returnValue(""));// 用途编码
			vouchermap.put("PaySummaryName", returnValue(""));// 用途名称
			vouchermap.put("PayAmt", "-"+ MtoCodeTrans.transformString(dto.getNmoney()));// 汇总退款金额
			vouchermap.put("Remark", returnValue(dto.getSdemo())); // 备注信息
			vouchermap.put("Hold1", returnValue(""));// 预留字段1
			vouchermap.put("Hold2", returnValue(""));// 预留字段2
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			Detailmap.put("Id", dto.getSdealno()); // 退款通知书明细编号
			Detailmap.put("VoucherBillId", dto.getSdealno()); // 退款通知书Id
			Detailmap.put("BgtTypeCode", returnValue("99999")); // 预算类型编码
			Detailmap.put("BgtTypeName", returnValue("99999")); // 预算类型名称
			Detailmap.put("DepProCode", returnValue("99999")); // 预算项目编码
			Detailmap.put("DepProName", returnValue("99999")); // 预算项目名称
			Detailmap.put("ProCatCode", returnValue("99999")); // 收支管理编码
			Detailmap.put("ProCatName", returnValue("99999")); // 收支管理名称
			Detailmap.put("FundTypeCode", returnValue("1"));// 资金性质编码
			Detailmap.put("FundTypeName", returnValue("预算管理资金"));// 资金性质名称
			Detailmap.put("AgencyCode", returnValue("99999")); // 预算单位编码
			Detailmap.put("AgencyName", returnValue("99999")); // 预算单位名称
			Detailmap.put("PayTypeCode", returnValue(dto.getSvtcodedes()));// 支付方式编码
			Detailmap.put("PayTypeName", returnValue(dto.getStradekind()));// 支付方式名称
			Detailmap.put("PayAcctNo", returnValue(dto.getSpayacctno()));// 付款人账号
			Detailmap.put("PayAcctName", returnValue(dto.getSpayacctname()));// 付款人名称
			Detailmap.put("PayAcctBankName", returnValue(dto.getSpayacctbankname()));// 付款人银行
			Detailmap.put("PayeeAcctNo", returnValue(dto.getSpayeeacctno()));// 收款人账号
			Detailmap.put("PayeeAcctName",returnValue(dto.getSpayeeacctname()));// 收款人名称
			Detailmap.put("PayeeAcctBankName", returnValue(dto.getSpayeeacctbankname()));// 收款人银行
			Detailmap.put("PayeeAcctBankNo", returnValue(dto.getSpayeeacctbankname()));// 收款银行行号
			Detailmap.put("ProjectCategoryCode", returnValue("99999")); // 专户项目归类编码
			Detailmap.put("ProjectCategoryName", returnValue("99999")); // 专户项目归类名称
			Detailmap.put("ExpFuncCode", returnValue("99999")); // 支出功能分类科目编码
			Detailmap.put("ExpFuncName", returnValue("99999")); // 支出功能分类科目名称
			Detailmap.put("ExpEcoCode", returnValue("99999")); // 支出经济分类科目编码
			Detailmap.put("ExpEcoName", returnValue("99999")); // 支出经济分类科目名称
			Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(dto.getNmoney())); // 退款金额
			Detailmap.put("Hold1", returnValue("")); // 预留字段1
			Detailmap.put("Hold2", returnValue("")); // 预留字段2
			Detailmap.put("Hold3", returnValue(""));// 预留字段3
			Detailmap.put("Hold4", returnValue(""));// 预留字段4
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
			vouchermap.put("AgentBusinessNo", returnValue(dto.getSxagentbusinessno()));// 原银行交易流水号
			vouchermap.put("OriBillNo", returnValue(dto.getStaxticketno()));// 原拨款单单号
			vouchermap.put("OriVouDate", returnValue(dto.getSgenticketdate()));// 原拨款单凭证日期
			vouchermap.put("OriPayDate", returnValue(dto.getSxpaydate()));// 原支付日期
			vouchermap.put("FundTypeCode", dto.getSfundtypecode());// 资金性质编码
			vouchermap.put("FundTypeName", dto.getSfundtypename());// 资金性质名称
			vouchermap.put("PayTypeCode", dto.getSpaytypecode());// 支付方式编码
			vouchermap.put("PayTypeName", dto.getSpaytypename());// 支付方式名称
			if(vouchermap.get("FundTypeCode")==null||StringUtils.isBlank(String.valueOf(vouchermap.get("FundTypeCode")))||StringUtils.isEmpty(String.valueOf(vouchermap.get("FundTypeCode"))))
				vouchermap.put("FundTypeCode", "1");// 资金性质编码
			if(vouchermap.get("FundTypeName")==null||StringUtils.isNotBlank(String.valueOf(vouchermap.get("FundTypeName")))||StringUtils.isNotEmpty(String.valueOf(vouchermap.get("FundTypeName"))))
				vouchermap.put("FundTypeName", "预算管理资金");// 资金性质名称
			if(vouchermap.get("PayTypeCode")==null||StringUtils.isNotBlank(String.valueOf(vouchermap.get("PayTypeCode")))||StringUtils.isNotEmpty(String.valueOf(vouchermap.get("PayTypeCode"))))
				vouchermap.put("PayTypeCode", "91");// 支付方式编码
			if(vouchermap.get("PayTypeName")==null||StringUtils.isNotBlank(String.valueOf(vouchermap.get("PayTypeName")))||StringUtils.isNotEmpty(String.valueOf(vouchermap.get("PayTypeName"))))
				vouchermap.put("PayTypeName", "专户实拨退回");// 支付方式名称
			vouchermap.put("ProCatCode", returnValue("99999"));// 收支管理编码
			vouchermap.put("ProCatName", returnValue("99999"));// 收支管理名称
			vouchermap.put("AgencyCode", returnValue(dto.getSbudgetunitcode())); // 预算单位编码
			vouchermap.put("AgencyName", returnValue(dto.getSunitcodename())); // 预算单位名称
			TsPaybankDto paydto = new TsPaybankDto();
			vouchermap.put("PayAcctNo", returnValue(dto.getSrecacct()));// 付款人账号vouchermap.put("PayAcctNo",// dto.getSpayeracct());
			vouchermap.put("PayAcctName", returnValue(dto.getSrecname()));// 付款人名称vouchermap.put("PayAcctName",// dto.getSpayername());
			vouchermap.put("PayAcctBankName", returnValue(dto.getSrecbankname()));// 付款人银行vouchermap.put("PayAcctBankName",// dto.getSpayerbankname());
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayeracct()));// 收款人账号vouchermap.put("PayeeAcctNo",// dto.getSrecacct());
			vouchermap.put("PayeeAcctName", returnValue(dto.getSpayername()));// 收款人名称vouchermap.put("PayeeAcctName",// dto.getSrecname());
			vouchermap.put("PayeeAcctBankName", returnValue(dto.getSpayerbankname()));// 收款人银行vouchermap.put("PayeeAcctBankName",// dto.getSrecbankname());
			List<TsPaybankDto> payList = null;
			if(dto.getSpayerbankname()!=null&&!"".equals(dto.getSpayerbankname()))
			{
				paydto.setSpaybankname(dto.getSpayerbankname());
				payList = CommonFacade.getODB().findRsByDto(paydto);
			}
			vouchermap.put("PayeeAcctBankNo", payList != null&& payList.size() > 0 ? payList.get(0).getSpaybankno():returnValue(dto.getSrecbankno()));// 收款银行行号vouchermap.put("PayeeAcctBankNo",// returnValue(dto.getSrecbankno()));
			vouchermap.put("PaySummaryCode", returnValue(dto.getSpaysummarycode()));// 用途编码
			vouchermap.put("PaySummaryName", returnValue(dto.getSpaysummaryname()));// 用途名称
			vouchermap.put("PayAmt", "-"+ MtoCodeTrans.transformString(dto.getShold2()));// 汇总退款金额
			vouchermap.put("Remark", returnValue(payoutBackReason)); // 备注信息
			vouchermap.put("Hold1", returnValue(dto.getShold1()));// 预留字段1
			vouchermap.put("Hold2", returnValue(""));// 预留字段2
			for (TvPayoutmsgsubDto subdto : sublist) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", dto.getSdealno() + subdto.getSbizno()); // 退款通知书明细编号
				Detailmap.put("VoucherBillId", vDto.getSdealno()); // 退款通知书Id
				Detailmap.put("BgtTypeCode", returnValue(subdto.getSbgttypecode())); // 预算类型编码
				Detailmap.put("BgtTypeName", returnValue(subdto.getSbgttypename())); // 预算类型名称
				Detailmap.put("DepProCode", returnValue(subdto.getSbudgetprjcode())); // 预算项目编码
				Detailmap.put("DepProName", returnValue(subdto.getShold4())); // 预算项目名称
				Detailmap.put("ProCatCode",returnValue(subdto.getSprocatcode())); // 收支管理编码
				Detailmap.put("ProCatName",returnValue(subdto.getSprocatname())); // 收支管理名称
				Detailmap.put("AgencyCode", subdto.getSagencycode()); // 预算单位编码
				Detailmap.put("AgencyName", subdto.getSagencyname()); // 预算单位名称
				Detailmap.put("FundTypeCode", dto.getSfundtypecode());// 资金性质编码
				Detailmap.put("FundTypeName", dto.getSfundtypename());// 资金性质名称
				Detailmap.put("PayTypeCode", dto.getSpaytypecode());// 支付方式编码
				Detailmap.put("PayTypeName", dto.getSpaytypename());// 支付方式名称
				Detailmap.put("PayAcctNo", returnValue(dto.getSrecacct()));// 付款人账号
				Detailmap.put("PayAcctName", returnValue(dto.getSrecname()));// 付款人名称
				Detailmap.put("PayAcctBankName", returnValue(dto.getSrecbankname()));// 付款人银行
				Detailmap.put("PayeeAcctBankName", returnValue(dto.getSpayerbankname()));// 收款人银行
				Detailmap.put("PayeeAcctBankNo", vouchermap.get("PayeeAcctBankNo"));// 收款银行行号
				Detailmap.put("PayeeAcctNo", returnValue(dto.getSpayeracct()));// 收款人账号
				Detailmap.put("PayeeAcctName", returnValue(dto.getSpayername()));// 收款人名称
				Detailmap.put("ProjectCategoryCode", returnValue(subdto.getShold2())); // 专户项目归类编码
				Detailmap.put("ProjectCategoryName", returnValue(subdto.getShold3())); // 专户项目归类名称
				Detailmap.put("ExpFuncCode", returnValue(subdto.getSfunsubjectcode())); // 支出功能分类科目编码
				Detailmap.put("ExpFuncName", returnValue(subdto.getSexpfuncname())); // 支出功能分类科目名称
				Detailmap.put("ExpEcoCode",returnValue(subdto.getSexpecocode())); // 支出经济分类科目编码
				Detailmap.put("ExpEcoName",returnValue(subdto.getSexpeconame())); // 支出经济分类科目名称
				Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(subdto.getNmoney())); // 全额退款金额
				Detailmap.put("Hold1", returnValue(subdto.getShold1())); // 预留字段1
				Detailmap.put("Hold2", returnValue("")); // 预留字段2
				Detailmap.put("Hold3", returnValue(""));// 预留字段3
				Detailmap.put("Hold4", returnValue(""));// 预留字段4
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
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			vouchermap.put("Id", voucherdto.getSdealno());// 实拨退款通知书Id
			vouchermap.put("AdmDivCode", voucherdto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", voucherdto.getSstyear());// 业务年度
			vouchermap.put("VtCode", voucherdto.getSvtcode());// 凭证类型编号
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, TsPaybankDto>  bankmap = null;
			try {
				bankmap = SrvCacheFacade.cachePayBankInfo();
			} catch (JAFDatabaseException e1) {
				logger.error("获取行名对照信息出错："+e1);
			}
			vouchermap.put("VouDate", maindto.getSvoudate());// 凭证日期
			vouchermap.put("VoucherNo", maindto.getSvouno());// 凭证号
			vouchermap.put("AgentBusinessNo", returnValue(maindto.getSoritrano()));// 原银行交易流水号
			vouchermap.put("OriBillNo", returnValue(maindto.getSorivouno()));// 原拨款单单号
			vouchermap.put("OriVouDate", returnValue(maindto.getSorivoudate()));// 原拨款单凭证日期
			vouchermap.put("OriPayDate", returnValue(maindto.getSorivoudate()));// 原支付日期
			vouchermap.put("FundTypeCode", "1");// 资金性质编码
			vouchermap.put("FundTypeName", "预算管理资金");// 资金性质名称
			vouchermap.put("PayTypeCode", "91");// 支付方式编码
			vouchermap.put("PayTypeName", "专户实拨退回");// 支付方式名称
			vouchermap.put("ProCatCode", returnValue("-"));// 收支管理编码
			vouchermap.put("ProCatName", returnValue("-"));// 收支管理名称
			vouchermap.put("AgencyCode", maindto.getSbudgetunitcode()); // 预算单位编码
			vouchermap.put("AgencyName", returnValue(maindto.getSunitcodename())); // 预算单位名称
			vouchermap.put("PayAcctNo", maindto.getSpayeracct());// 付款人账号
			vouchermap.put("PayAcctName", maindto.getSpayername());// 付款人名称
			TvPayoutmsgmainDto payoutdto = new TvPayoutmsgmainDto();
			payoutdto.setStaxticketno(maindto.getSorivouno());
			List payoutlist = CommonFacade.getODB().findRsByDto(payoutdto);
			if(payoutlist!=null&&payoutlist.size()==1)
			{
				payoutdto = (TvPayoutmsgmainDto)payoutlist.get(0);
				vouchermap.put("PayAcctBankName", payoutdto.getSrecbankname());// 付款人银行
			}else
			{
				HtvPayoutmsgmainDto hpayoutdto = new HtvPayoutmsgmainDto();
				hpayoutdto.setStaxticketno(maindto.getSorivouno());
				List hpayoutlist = CommonFacade.getODB().findRsByDto(hpayoutdto);
				if(hpayoutlist!=null&&hpayoutlist.size()==1)
				{
					hpayoutdto = (HtvPayoutmsgmainDto)hpayoutlist.get(0);
					vouchermap.put("PayAcctBankName", hpayoutdto.getSrecbankname());// 付款人银行
				}
			}
			if(vouchermap.get("PayAcctBankName")==null)
				vouchermap.put("PayAcctBankName", returnValue("--"));// 付款人银行
			vouchermap.put("PayeeAcctNo", maindto.getSpayeeacct());// 收款人账号
			vouchermap.put("PayeeAcctName", maindto.getSpayeename());// 收款人名称
			vouchermap.put("PayeeAcctBankName", returnValue(bankmap==null?maindto.getSpayeename():bankmap.get(maindto.getSpayeebankno())==null?maindto.getSpayeename():bankmap.get(maindto.getSpayeebankno()).getSbankname()));// 收款人银行
			vouchermap.put("PayeeAcctBankNo", maindto.getSpayeebankno());// 收款银行行号
			vouchermap.put("PaySummaryCode", "");// 用途编码
			vouchermap.put("PaySummaryName", maindto.getSaddword());// 用途名称
			vouchermap.put("PayAmt", "-"+ MtoCodeTrans.transformString(maindto.getNmoney()));// 汇总退款金额
			vouchermap.put("Remark", returnValue(maindto.getSdemo())); // 备注信息
			vouchermap.put("Hold1", maindto.getShold1());// 预留字段1
			vouchermap.put("Hold2", maindto.getShold2());// 预留字段2
			TsPaybankDto paydto = bankmap.get(maindto.getSpayername());
			for (int j = 0; j < sublist.size(); j++) {
				TvPayoutbackmsgSubDto subdto = (TvPayoutbackmsgSubDto) sublist
						.get(j);
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", maindto.getSvouno() + subdto.getSseqno()); // 退款通知书明细编号
				Detailmap.put("VoucherBillId", voucherdto.getSdealno()); // 退款通知书Id
				Detailmap.put("DepProCode", returnValue(subdto.getSbudgetprjcode())); // 预算项目编码
				Detailmap.put("DepProName", returnValue("")); // 预算项目名称
				Detailmap.put("BgtTypeCode", ""); // 预算类型编码
				Detailmap.put("BgtTypeName", ""); // 预算类型名称
				Detailmap.put("ProCatCode", ""); // 收支管理编码
				Detailmap.put("ProCatName", ""); // 收支管理名称
				Detailmap.put("AgencyCode", maindto.getSbudgetunitcode()); // 预算单位编码
				Detailmap.put("AgencyName", returnValue(maindto.getSunitcodename())); // 预算单位名称
				Detailmap.put("PayAcctNo", maindto.getSpayeracct());// 付款人账号
				Detailmap.put("PayAcctName", maindto.getSpayername());// 付款人名称
				Detailmap.put("PayAcctBankName", maindto.getSpayername());// 付款人银行
				Detailmap.put("PayeeAcctNo", returnValue(maindto.getSpayeracct()));// 原收款人账号
				Detailmap.put("PayeeAcctName", returnValue(maindto.getSpayername()));// 原收款人名称
				Detailmap.put("PayeeAcctBankName", paydto!=null? paydto.getSbankname() : returnValue(maindto.getSpayername()));// 原收款银行名称
				Detailmap.put("PayeeAcctBankNo",paydto!=null ? paydto.getSbankno(): returnValue(""));// 原收款银行行号
				Detailmap.put("ProjectCategoryCode", returnValue("")); // 专户项目归类编码
				Detailmap.put("ProjectCategoryName", returnValue("")); // 专户项目归类名称
				Detailmap.put("ExpFuncCode", subdto.getSfunsubjectcode()); // 支出功能分类科目编码
				Detailmap.put("ExpFuncName", returnValue(subdto.getSfunsubjectname())); // 支出功能分类科目名称
				Detailmap.put("ExpEcoCode", ""); // 支出经济分类科目编码
				Detailmap.put("ExpEcoName", ""); // 支出经济分类科目名称
				Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(subdto.getNmoney())); // 退款金额
				Detailmap.put("Hold1", returnValue(subdto.getShold1())); // 预留字段1
				Detailmap.put("Hold2", returnValue(subdto.getShold2())); // 预留字段2
				Detailmap.put("Hold3", returnValue(""));// 预留字段3
				Detailmap.put("Hold4", returnValue(""));// 预留字段4
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

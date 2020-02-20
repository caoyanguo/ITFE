package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 直接支付退款通知(上海扩展)
 * 
 * @author hejianrong time 2014-10-29
 * 
 */
public class VoucherDto2MapFor2203 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor2203.class);

	/**
	 * 生成凭证 生成凭证报文
	 * 
	 * @param list
	 * @throws ITFEBizException
	 */
	public Map tranfor(List list) throws ITFEBizException {
		TvVoucherinfoDto dto = null;
		List subdtoList = new ArrayList();
		Map map = null;
		String svtcode = (String) list.get(0);
		IDto idto = (IDto) list.get(1);
		List lists = new ArrayList();
		if (idto instanceof TfDirectpaymsgmainDto) {
			TfDirectpaymsgmainDto maindto = (TfDirectpaymsgmainDto) idto;
			maindto.setSbackflag("1");
			subdtoList = PublicSearchFacade.findSubDtoByMain(maindto);
			if (maindto.getSrefundtype().equals("1")) {
				for (TfDirectpaymsgsubDto suddto : (List<TfDirectpaymsgsubDto>) subdtoList) {
					if (suddto.getIseqno() == 1) {
						subdtoList.clear();
						subdtoList.add(suddto);
						break;
					}
				}
			}
			dto = tranfor(maindto.getSorgcode(), maindto.getStrecode(), maindto
					.getSadmdivcode(), maindto.getNbackmoney(), maindto
					.getSrefundtype(), String.valueOf(maindto.getIvousrlno()));
		} else if (idto instanceof TvPayreckBankBackDto) {
			TvPayreckBankBackDto maindto = (TvPayreckBankBackDto) idto;
			maindto.setSbackflag("1");
			subdtoList = PublicSearchFacade.findSubDtoByMain(maindto);
			dto = tranfor(maindto.getSbookorgcode(), maindto.getStrecode(),
					maindto.getSadmdivcode(), maindto.getFamt(), maindto
							.getSrefundtype(), String.valueOf(maindto
							.getIvousrlno()));

		}
		lists.add(dto);
		lists.add(idto);
		lists.add(subdtoList);
		map = voucherTranfor(lists);
		VoucherUtil.sendTips(dto, map);
		try {
			// 生成凭证索引表记录
			DatabaseFacade.getODB().create(dto);
			// 更新主表信息
			DatabaseFacade.getODB().update(idto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("生成凭证信息写入数据库异常!");
		}
		map.put("voucherDto", dto);
		return map;
	}

	@SuppressWarnings("unchecked")
	private TvVoucherinfoDto tranfor(String orgcode, String strecode,
			String admdivcode, BigDecimal amt, String refundtype,
			String voucherno) throws ITFEBizException {
		TvVoucherinfoDto dto = new TvVoucherinfoDto();
		dto.setSorgcode(orgcode);
		dto.setStrecode(strecode);
		dto.setSdealno(VoucherUtil.getGrantSequence());
		dto.setSvoucherno(dto.getSdealno());
		dto.setSvtcode(MsgConstant.VOUCHER_NO_2203);
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));
		dto.setSvoucherflag("1");
		dto.setScreatdate(TimeFacade.getCurrentStringTime());
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto
				.getScreatdate(), dto.getSdealno()));
		dto.setSadmdivcode(admdivcode);
		dto.setNmoney(amt);
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("处理成功");
		dto.setScreatdate(TimeFacade.getCurrentStringTime());
		dto.setShold1(refundtype);
		dto.setShold2(voucherno + "01"); // 获取原发起报文号
		return dto;
	}

	/**
	 * DTO转化XML报文 由2202生成
	 * 
	 * @param List
	 *            生成报文要素集合
	 * @return
	 * @throws ITFEBizException
	 */
	public Map tranfor2202(List lists) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvPayreckBankBackDto maindto = (TvPayreckBankBackDto) lists.get(1);
			List<TvPayreckBankBackListDto> subdtoList2202 = (List<TvPayreckBankBackListDto>) lists
					.get(2);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", String.valueOf(vDto.getSdealno()));// 实拨退款通知书Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());// 业务年度
			vouchermap.put("VtCode", vDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());// 凭证日期
			vouchermap.put("VoucherNo", String.valueOf(vDto.getSdealno()));// 凭证号
			vouchermap.put("PayBankCode", "");// 代理银行编码
			vouchermap.put("PayBankName", "");// 代理银行名称
			vouchermap.put("RefundType", maindto.getSrefundtype());// 退款类型
			if (StateConstant.REFUNDTYPE_5.equals(maindto.getSrefundtype()
					.trim())) { // RefundType=1：需要退款的原支付凭证单号支付明细单号；RefundType=2：需要退款的原支付凭证单号
				vouchermap.put("OriBillNo", "");// 原业务单据号
				vouchermap.put("AgentBusinessNo", "");// 银行交易流水号
			} else {
				vouchermap.put("OriBillNo", maindto.getSorivouno());// 原业务单据号
				vouchermap.put("AgentBusinessNo", maindto.getStrano());// 银行交易流水号
			}

			vouchermap.put("PayAcctNo", maindto.getSpayeracct());// 原付款人账号
			vouchermap.put("PayAcctName", maindto.getSpayername());// 原付款人名称
			vouchermap.put("PayAcctBankName", maindto.getSclearacctbankname());// 原付款人银行
			vouchermap.put("PayeeAcctNo", maindto.getSpayeeacct());// 原收款人账号
			vouchermap.put("PayeeAcctName", maindto.getSpayeename());// 原收款人名称
			vouchermap
					.put("PayeeAcctBankName", maindto.getSagentacctbankname());// 原收款人银行
			vouchermap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(maindto.getFamt()));// 退款金额
			vouchermap.put("Remark", maindto.getSremark());// 备注信息
			vouchermap.put("PayDate", maindto.getSxcleardate().toString()
					.replaceAll("-", ""));// 实际退款日期
			vouchermap.put("Hold1", "");// 预留字段1
			vouchermap.put("Hold2", "");// 预留字段2
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			int count = 1;
			TfDirectpaymsgsubDto tfDirectpaymsgmainDto;
			for (TvPayreckBankBackListDto subdto : subdtoList2202) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", subdto.getSid()); // 退款通知书明细编号
				Detailmap.put("VoucherBillId", subdto.getSorivouno()
						+ subdto.getSid()); // 退款通知书Id 由凭证编号+序号
				Detailmap.put("BgtTypeCode", maindto.getSbgttypecode()); // 预算类型编码
				Detailmap.put("BgtTypeName", maindto.getSbgttypename()); // 预算类型名称
				Detailmap.put("ProCatCode", ""); // 收支管理编码
				Detailmap.put("ProCatName", ""); // 收支管理名称
				Detailmap.put("PayKindCode", ""); // 支出类型编码
				Detailmap.put("PayKindName", ""); // 支出类型名称
				Detailmap.put("MOFDepCode", ""); // 业务处室编码
				Detailmap.put("MOFDepName", ""); // 业务处室名称
				if (StateConstant.REFUNDTYPE_5.equals(maindto.getSrefundtype()
						.trim())) {
					Detailmap.put("AgencyCode", ""); // 基层预算单位编码
					Detailmap.put("AgencyName", ""); // 基层预算单位名称
					Detailmap.put("ExpFuncCode", "99999"); // 功能分类科目编码
					Detailmap.put("ExpFuncName", ""); // 功能分类科目名称
					// 原支付申请序号 获取hold1 存储5201的voucherno字段
					Detailmap.put("VoucherNo", subdto.getShold1()); // 原支付申请序号
				} else {
					Detailmap.put("AgencyCode", subdto.getSbdgorgcode()); // 基层预算单位编码
					Detailmap.put("AgencyName", subdto.getSsupdepname()); // 基层预算单位名称
					Detailmap.put("ExpFuncCode", subdto.getSfuncbdgsbtcode()); // 功能分类科目编码
					Detailmap.put("ExpFuncName", subdto.getSexpfuncname()); // 功能分类科目名称
					// 原支付申请序号 获取hold1 存储5201的voucherno字段
					Detailmap.put("VoucherNo", subdto.getShold1()); // 原支付申请序号
				}

				Detailmap.put("ExpEcoCode", subdto.getSecnomicsubjectcode()); // 经济分类科目编码
				Detailmap.put("ExpEcoName", ""); // 经济分类科目名称
				Detailmap.put("DepProCode", ""); // 预算项目编码
				Detailmap.put("DepProName", ""); // 预算项目名称
				Detailmap.put("PayAcctNo", maindto.getSpayeracct()); // 原付款人账号
				Detailmap.put("PayAcctName", maindto.getSpayername()); // 原付款人名称
				Detailmap.put("PayAcctBankName", maindto
						.getSclearacctbankname()); // 原付款人银行
				Detailmap.put("PayeeAcctNo", maindto.getSpayeeacct()); // 原收款人账号
				Detailmap.put("PayeeAcctName", maindto.getSpayeename()); // 原收款人名称
				Detailmap.put("PayeeAcctBankName", maindto
						.getSagentacctbankname()); // 原收款人银行
//				Detailmap.put("PayeeAcctBankNo", maindto.getSagentbnkcode()); // 原收款人银行行号
				Detailmap.put("PayeeAcctBankNo", ""); // 原收款人银行行号
				Detailmap.put("PayAmt", "-"
						+ MtoCodeTrans.transformString(subdto.getFamt())); // 退款金额
				Detailmap.put("Remark", subdto.getSpaysummaryname()); // 退款原因
				Detailmap.put("Hold1", ""); // 预留字段1
				Detailmap.put("Hold2", ""); // 预留字段2
				Detailmap.put("Hold3", ""); // 预留字段3
				Detailmap.put("Hold4", ""); // 预留字段4
				count++;
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
	 * DTO转化XML报文（单笔业务） 由5201生成
	 * 
	 * @param List
	 *            生成报文要素集合
	 * @return
	 * @throws ITFEBizException
	 */
	public Map tranfor5201(List lists) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TfDirectpaymsgmainDto maindto5201 = (TfDirectpaymsgmainDto) lists
					.get(1);
			List<TfDirectpaymsgsubDto> subdtoList5201 = (List<TfDirectpaymsgsubDto>) lists
					.get(2);
			TfPaybankRefundsubDto refundSubDto = new TfPaybankRefundsubDto();
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", vDto.getSdealno());// 实拨退款通知书Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());// 业务年度
			vouchermap.put("VtCode", vDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());// 凭证日期
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// 凭证号
			vouchermap.put("PayBankCode", "");// 代理银行编码
			vouchermap.put("PayBankName", "");// 代理银行名称
			vouchermap.put("AgentBusinessNo", maindto5201.getSdealno());// 银行交易流水号
			vouchermap.put("OriBillNo", maindto5201.getSvoucherno());// 原业务单据号
			vouchermap.put("RefundType", maindto5201.getSrefundtype());// 退款类型
			vouchermap.put("PayAcctNo", maindto5201.getSpayacctno());// 原付款人账号
			vouchermap.put("PayAcctName", maindto5201.getSpayacctname());// 原付款人名称
			vouchermap
					.put("PayAcctBankName", maindto5201.getSpayacctbankname());// 原付款人银行
			vouchermap.put("PayeeAcctNo", maindto5201.getSpayeeacctno());// 原收款人账号
			vouchermap.put("PayeeAcctName", maindto5201.getSpayeeacctname());// 原收款人名称
			vouchermap.put("PayeeAcctBankName", maindto5201
					.getSpayeeacctbankname());// 原收款人银行
			vouchermap
					.put("PayAmt", "-"
							+ MtoCodeTrans.transformString(maindto5201
									.getNbackmoney()));// 退款金额
			vouchermap.put("Remark", maindto5201.getSbckreason());// 备注信息
			vouchermap.put("PayDate", TimeFacade.getCurrentStringTime());// 实际退款日期
			vouchermap.put("Hold1", "");// 预留字段1
			vouchermap.put("Hold2", "");// 预留字段2
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			for (TfDirectpaymsgsubDto subdto5201 : subdtoList5201) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", subdto5201.getSid()); // 退款通知书明细编号
				Detailmap.put("VoucherBillId", vDto.getSdealno()); // 退款通知书Id
				Detailmap.put("VoucherNo", subdto5201.getSvoucherno()); // 原支付申请序号
				Detailmap.put("BgtTypeCode", subdto5201.getSbgttypecode()); // 预算类型编码
				Detailmap.put("BgtTypeName", subdto5201.getSbgttypename()); // 预算类型名称
				Detailmap.put("ProCatCode", subdto5201.getSprocatcode()); // 收支管理编码
				Detailmap.put("ProCatName", subdto5201.getSprocatname()); // 收支管理名称
				Detailmap.put("PayKindCode", subdto5201.getSpaykindcode()); // 支出类型编码
				Detailmap.put("PayKindName", subdto5201.getSpaykindname()); // 支出类型名称
				Detailmap.put("MOFDepCode", subdto5201.getSmofdepcode()); // 业务处室编码
				Detailmap.put("MOFDepName", subdto5201.getSmofdepname()); // 业务处室名称
				Detailmap.put("AgencyCode", subdto5201.getSagencycode()); // 基层预算单位编码
				Detailmap.put("AgencyName", subdto5201.getSagencyname()); // 基层预算单位名称
				Detailmap.put("ExpFuncCode", subdto5201.getSexpecocode()); // 功能分类科目编码
				Detailmap.put("ExpFuncName", subdto5201.getSexpeconame()); // 功能分类科目名称
				Detailmap.put("ExpEcoCode", subdto5201.getSexpecocode()); // 经济分类科目编码
				Detailmap.put("ExpEcoName", subdto5201.getSexpeconame()); // 经济分类科目名称
				Detailmap.put("DepProCode", null); // 预算项目编码
				Detailmap.put("DepProName", null); // 预算项目名称
				Detailmap.put("PayAcctNo", maindto5201.getSpayacctno()); // 原付款人账号
				Detailmap.put("PayAcctName", maindto5201.getSpayacctname()); // 原付款人名称
				Detailmap.put("PayAcctBankName", maindto5201
						.getSpayacctbankname()); // 原付款人银行
				Detailmap.put("PayeeAcctNo", subdto5201.getSpayeeacctno()); // 原收款人账号
				Detailmap.put("PayeeAcctName", subdto5201.getSpayeeacctname()); // 原收款人名称
				Detailmap.put("PayeeAcctBankName", subdto5201
						.getSpayeeacctbankname()); // 原收款人银行
//				Detailmap.put("PayeeAcctBankNo", subdto5201
//						.getSpayeeacctbankno()); // 原收款人银行行号
				Detailmap.put("PayeeAcctBankNo", ""); // 原收款人银行行号
				Detailmap
						.put("PayAmt", "-"
								+ MtoCodeTrans.transformString(subdto5201
										.getNpayamt())); // 退款金额
				Detailmap.put("Remark", maindto5201.getSbckreason()); // 退款原因
				Detailmap.put("Hold1", ""); // 预留字段1
				Detailmap.put("Hold2", ""); // 预留字段2
				Detailmap.put("Hold3", ""); // 预留字段3
				Detailmap.put("Hold4", ""); // 预留字段4
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

	public Map voucherTranfor(List lists) throws ITFEBizException {
		Map map = null;
		IDto maindto = (IDto) lists.get(1);
		if (maindto instanceof TfDirectpaymsgmainDto) {
			map = tranfor5201(lists);
		} else if (maindto instanceof TvPayreckBankBackDto) {
			map = tranfor2202(lists);
		} else if (maindto instanceof TvVoucherinfoAllocateIncomeDto) {
			map = tranfor2203(lists);
		}
		return map;

	}

	@SuppressWarnings("unchecked")
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException {
		List list = findMainDto(vDto);
		if (list.size() == 0)
			return list;
		List lists = new ArrayList();
		for (TvVoucherinfoAllocateIncomeDto mainDto : (List<TvVoucherinfoAllocateIncomeDto>) list) {
			List<TvVoucherinfoDto> voutherList = voucherIsRepeat(vDto, mainDto);
			if (voutherList != null && voutherList.size() > 0) {
				continue;
			}
			List tmpList = (List) voucherTranfor(vDto, mainDto);
			lists.add(tmpList);
			try {
				copyMain2203to2202((TvVoucherinfoDto) tmpList.get(1), mainDto);
				makeSub2203to2202((TvVoucherinfoDto) tmpList.get(1), mainDto);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("复制2203凭证信息出错！", e);
			} catch (ValidateException e) {
				logger.error(e);
				throw new ITFEBizException("复制2203凭证信息出错！", e);
			}
		}
		return lists;
	}

	/**
	 * 查询直接支付导入文件业务表信息
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
			throw new ITFEBizException("查询直接支付导入业务信息出错！", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询直接支付导入业务信息出错！", e);
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
			throw new ITFEBizException("查询索引表直接支付信息出错！", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询索引表直接支付信息出错！", e);
		}
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
		// dto.setSvoucherno(mainDto.getSdealno());
		dto.setNmoney(mainDto.getNmoney());
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("处理成功");
		dto.setShold2(dto.getSdealno() + "01");
		dto.setShold4("1");	//标识该笔为TC资金导入
		List lists = new ArrayList();
		lists.add(dto);
		lists.add(mainDto);
		Map map = tranfor2203(lists);
		lists.clear();
		lists.add(map);
		lists.add(dto);
		return lists;
	}

	/**
	 * DTO转化XML报文 由2203生成
	 * 
	 * @param List
	 *            生成报文要素集合
	 * @return
	 * @throws ITFEBizException
	 */
	public Map tranfor2203(List lists) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto fundDto = (TvVoucherinfoAllocateIncomeDto) lists
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
			vouchermap.put("PayBankCode", "");// 代理银行编码
			vouchermap.put("PayBankName", "");// 代理银行名称
			vouchermap.put("AgentBusinessNo", "");// 银行交易流水号
			vouchermap.put("OriBillNo", "");// 原业务单据号
			vouchermap.put("RefundType", StateConstant.REFUNDTYPE_5);// 不确定退款
			vouchermap.put("PayAcctNo", fundDto.getSpayacctno());// 原付款人账号
			vouchermap.put("PayAcctName", fundDto.getSpayacctname());// 原付款人名称
			vouchermap.put("PayAcctBankName", fundDto.getSpayacctbankname());// 原付款人银行
			vouchermap.put("PayeeAcctNo", fundDto.getSpayeeacctno());// 原收款人账号
			vouchermap.put("PayeeAcctName", fundDto.getSpayeeacctname());// 原收款人名称
			vouchermap
					.put("PayeeAcctBankName", fundDto.getSpayeeacctbankname());// 原收款人银行
			vouchermap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(fundDto.getNmoney()));// 退款金额
			vouchermap.put("Remark", "不确定性退款");// 备注信息
			vouchermap.put("PayDate", TimeFacade.getCurrentStringTime());// 实际退款日期
			vouchermap.put("Hold1", "");// 预留字段1
			vouchermap.put("Hold2", "");// 预留字段2
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			// 从TCBS资金导入的业务没有明细，仅按照金额生成一笔
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			Detailmap.put("Id", "1"); // 退款通知书明细编号
			Detailmap.put("VoucherBillId", "1"); // 退款通知书Id
			Detailmap.put("VoucherNo", ""); // // 原支付申请序号
			Detailmap.put("BgtTypeCode", ""); // 预算类型编码
			Detailmap.put("BgtTypeName", ""); // 预算类型名称
			Detailmap.put("ProCatCode", ""); // 收支管理编码
			Detailmap.put("ProCatName", ""); // 收支管理名称
			Detailmap.put("PayKindCode", ""); // 支出类型编码
			Detailmap.put("PayKindName", ""); // 支出类型名称
			Detailmap.put("MOFDepCode", ""); // 业务处室编码
			Detailmap.put("MOFDepName", ""); // 业务处室名称
			Detailmap.put("AgencyCode", ""); // 基层预算单位编码
			Detailmap.put("AgencyName", ""); // 基层预算单位名称
			Detailmap.put("ExpFuncCode", "99999"); // 功能分类科目编码
			Detailmap.put("ExpFuncName", ""); // 功能分类科目名称
			Detailmap.put("ExpEcoCode", ""); // 经济分类科目编码
			Detailmap.put("ExpEcoName", ""); // 经济分类科目名称
			Detailmap.put("DepProCode", ""); // 预算项目编码
			Detailmap.put("DepProName", ""); // 预算项目名称
			Detailmap.put("PayAcctNo", fundDto.getSpayacctno()); // 原付款人账号
			Detailmap.put("PayAcctName", fundDto.getSpayacctname()); // 原付款人名称
			Detailmap.put("PayAcctBankName", fundDto.getSpayacctbankname()); // 原付款人银行
			Detailmap.put("PayeeAcctNo", fundDto.getSpayeeacctno()); // 原收款人账号
			Detailmap.put("PayeeAcctName", fundDto.getSpayeeacctname());// 原收款人名称
			Detailmap.put("PayeeAcctBankName", fundDto.getSpayeeacctbankname()); // 原收款人银行
			Detailmap.put("PayeeAcctBankNo", ""); // 原收款人银行行号
			Detailmap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(fundDto.getNmoney())); // 退款金额
			Detailmap.put("Remark", "不确定性退款"); // 退款原因
			Detailmap.put("Hold1", ""); // 预留字段1
			Detailmap.put("Hold2", ""); // 预留字段2
			Detailmap.put("Hold3", ""); // 预留字段3
			Detailmap.put("Hold4", ""); // 预留字段4
			Detail.add(Detailmap);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("2203组装凭证报文异常！", e);
		}
	}

	/**
	 * 复制TCBS来账资金凭证表2203数据,用于前置生成2302，不确定退款只有主表，没有明细
	 * 
	 * @param dto
	 * @param maindto5201
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException 
	 * @throws ValidateException 
	 */
	private static TvPayreckBankBackDto copyMain2203to2202(
			TvVoucherinfoDto dto, TvVoucherinfoAllocateIncomeDto maindto2203)
			throws JAFDatabaseException, ITFEBizException, ValidateException {
		Map<String, TsInfoconnorgaccDto> accMap = null;
		List<TsInfoconnorgaccDto> accList = null;
		TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
		accdto.setStrecode(dto.getStrecode());
		accdto.setSorgcode(dto.getSorgcode());
		accList = (List<TsInfoconnorgaccDto>) CommonFacade.getODB()
				.findRsByDto(accdto);
		if (accList != null && accList.size() > 0) {
			accMap = new HashMap<String, TsInfoconnorgaccDto>();
			for (TsInfoconnorgaccDto tempdto : accList) {
				if (tempdto.getSpayeraccount().indexOf(
						dto.getSorgcode() + "271") >= 0)
					accMap.put("271", tempdto); // 上海市财政局国库存款户
				else if (tempdto.getSpayeraccount().indexOf(dto.getSorgcode() + "371") >= 0 && !(tempdto.getSpayername().indexOf("授权") >= 0))
					accMap.put("371", tempdto);
			}
		}
		TvPayreckBankBackDto maindto = new TvPayreckBankBackDto();
		maindto.setStrano(maindto2203.getSpaydealno());// 申请划款凭证Id
		maindto.setIvousrlno(Long.valueOf(dto.getSdealno()));// 凭证流水号
		maindto.setSid("1");// 申请划款凭证Id
		maindto.setSadmdivcode(maindto2203.getSadmdivcode());// 行政区划代码
		maindto.setSofyear(maindto2203.getScommitdate().substring(0, 4));// 业务年度
		maindto.setSvtcode(dto.getSvtcode());// 凭证类型编号
		maindto.setDvoudate(CommonUtil.strToDate(dto.getScreatdate())); // 凭证日期
		maindto.setSvouno(dto.getSvoucherno());// 凭证号
		maindto.setSbookorgcode(maindto2203.getSorgcode());// 核算主体代码
		maindto.setStrecode(maindto2203.getStrecode()); // 国库主体代码
		TsConvertfinorgDto tmpFinOrg = getConverFinortInfo(maindto2203.getSorgcode(), maindto2203.getStrecode());
		if(null == tmpFinOrg){
		maindto.setSfinorgcode("000000000000");// 财政机关代码
		}else{
			maindto.setSfinorgcode(tmpFinOrg.getSfinorgcode());// 财政机关代码
		}
		maindto.setSbgttypecode("");// 预算类型编码
		maindto.setSbgttypename("");// 预算类型名称
		maindto.setSfundtypecode(StateConstant.COMMON_YES);// 资金性质编码
		maindto.setSfundtypename("预算内");// 资金性质名称
		maindto.setSpaymode("0");
		maindto.setSpaytypecode("0");// 支付方式编码
		maindto.setSpaytypename("直接支付");// 支付方式名称
		maindto.setSpayeeacct(accMap.get("271").getSpayeraccount());// 收款人账号
		maindto.setSpayeename(accMap.get("271").getSpayername());// 收款人账户名称
//		maindto.setSagentacctbankname(maindto2203.getSpayeeacctbankname());// 收款银行
		maindto.setSagentacctbankname("中国人民银行上海分行国库处");// 收款银行
		maindto.setSpayeracct(accMap.get("371").getSpayeraccount());// 付款账号
		maindto.setSpayername(accMap.get("371").getSpayername());// 付款账户名称
//		maindto.setSclearacctbankname(maindto2203.getSpayacctbankname());// 付款银行
		maindto.setSclearacctbankname("中国人民银行上海分行国库处");// 付款银行
		maindto.setFamt(maindto2203.getNmoney());// 汇总清算金额
//		maindto.setSpaybankname(maindto2203.getSpayacctbankname());// 代理银行名称
		maindto.setSpaybankname("中国人民银行上海分行国库处");// 代理银行名称
		maindto.setSagentbnkcode(maindto2203.getSforwardbankno());// 代理银行行号
		maindto.setSremark(maindto2203.getSdemo());// 摘要
		maindto.setSmoneycorpcode("");// 金融机构编码
		maindto.setShold1(maindto2203.getShold1());// 预留字段1
		maindto.setShold2(maindto2203.getShold2());// 预留字段2
		maindto.setDentrustdate(DateUtil.currentDate());// 委托日期
		maindto.setDorientrustdate(CommonUtil.strToDate(maindto2203
				.getScommitdate()));// 原委托日期
		maindto.setSpackno(maindto2203.getSpaydealno());// 包流水号
		maindto.setSoritrano(maindto2203.getSpaydealno());// 原交易流水号
		maindto.setDacceptdate(CommonUtil.strToDate(dto.getScreatdate()));// 接收日期
		maindto.setStrimsign(MsgConstant.TIME_FLAG_NORMAL);// 调整期标志
		maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);// 预算种类(默认预算内)
		maindto.setDorivoudate(CommonUtil.strToDate(maindto2203
				.getScommitdate()));// 原凭证日期
		maindto.setSorivouno("0");// 原凭证编号
		maindto.setSpaydictateno(maindto2203.getSpaydealno());// 大额支付退款交易序号
		maindto.setSpaymsgno(maindto2203.getSreportkind());// 支付报文编号
		maindto.setDpayentrustdate(CommonUtil.strToDate(maindto2203
				.getScommitdate()));// 支付委托日期
		maindto.setSpaysndbnkno(maindto2203.getSforwardbankno());// 支付发起行行号
		maindto.setSfilename(dto.getSfilename());
		maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// 状态 处理中
		maindto.setIstatinfnum(0);
		maindto.setSxpaysndbnkno(maindto2203.getSforwardbankno());// 支付发起行行号
		maindto.setSaddword(maindto2203.getSdemo());// 附言
		maindto.setSbackflag(StateConstant.COMMON_YES);
		maindto.setSrefundtype(StateConstant.NOREFUNDTYPE);
		maindto.setNbackmoney(maindto2203.getNmoney());
		maindto.setSbckreason(maindto2203.getSdemo());
		maindto.setSxcleardate(CommonUtil.strToDate(maindto2203
				.getScommitdate()));// 清算日期
		maindto.setSxpayamt(maindto2203.getNmoney());
		maindto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// 系统时间
		DatabaseFacade.getODB().create(maindto);
		return maindto;
	}

	/**
	 * 根据核算主体和国库代码获取财政机构代码
	 * @throws ITFEBizException 
	 */
	private static TsConvertfinorgDto getConverFinortInfo(String orgcode,
			String trecode) throws ITFEBizException {
		try {
			TsConvertfinorgDto searchDto = new TsConvertfinorgDto();
			searchDto.setSorgcode(orgcode);
			searchDto.setStrecode(trecode);
			List<TsConvertfinorgDto> lists = CommonFacade.getODB().findRsByDto(
					searchDto);
			if(null == lists || lists.size() == 0){
				return null;
			}else{
				return lists.get(0);
			}
		} catch (JAFDatabaseException e) {
			logger.error("根据核算主体：" + orgcode + "国库代码：" + trecode +"查找财政机构代码异常！",e);
			throw new ITFEBizException("根据核算主体：" + orgcode + "国库代码：" + trecode +"查找财政机构代码异常！", e);
		} catch (ValidateException e) {
			logger.error("根据核算主体：" + orgcode + "国库代码：" + trecode +"查找财政机构代码异常！",e);
			throw new ITFEBizException("根据核算主体：" + orgcode + "国库代码：" + trecode +"查找财政机构代码异常！", e);
		}
	}

	/**
	 * * 生成TCBS来账资金凭证表2203数据,用于前置生成2302，不确定退款只有主表，明细自己生成一个特殊的科目和预算单位
	 * 
	 * @param maindto5201
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	private static TvPayreckBankBackListDto makeSub2203to2202(
			TvVoucherinfoDto dto, TvVoucherinfoAllocateIncomeDto maindto2203)
			throws ITFEBizException, JAFDatabaseException {
		TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
		subdto.setIseqno(1);
		subdto.setSid("1");
		subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);// 账户性质
		subdto.setIvousrlno(Long.valueOf(dto.getSdealno()));// 凭证流水号
		subdto.setSvoucherno(dto.getSvoucherno());// 子表明细序号
		subdto.setSbdgorgcode("0000");// 一级预算单位编码
		subdto.setSsupdepname("0000");// 一级预算单位名称
		subdto.setSfuncbdgsbtcode("999999");// 支出功能分类科目编码
		subdto.setSexpfuncname("999999");// 支出功能分类科目名称
		subdto.setSecnomicsubjectcode("");// 经济科目代码
		subdto.setFamt(maindto2203.getNmoney());// 支付金额
		subdto.setSpaysummaryname(maindto2203.getSdemo());// 摘要名称
		subdto.setShold1("");// 预留字段1
		subdto.setShold2("");// 预留字段2
		subdto.setShold3("");// 预留字段3
		subdto.setShold4("");// 预留字段4
		subdto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// 更新时间
		subdto.setSorivouno("");// 原支付凭证单号
		subdto.setSorivoudetailno("");// 原支付凭证明细单号
		subdto.setDorivoudate(CommonUtil
				.strToDate(maindto2203.getScommitdate()));
		subdto.setShold1(dto.getSvoucherno());
		DatabaseFacade.getODB().create(subdto);

		return null;

	}

}

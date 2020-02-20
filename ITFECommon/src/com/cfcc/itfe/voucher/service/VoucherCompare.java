package com.cfcc.itfe.voucher.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailssubDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 凭证报文比对类
 * 
 * @author hejianrong
 * @time 2014-09-15
 */
public class VoucherCompare {
	private static Log logger = LogFactory.getLog(VoucherCompare.class);
	private String tmpFailReason = "";// 勾兑失败原因
	private TvVoucherinfoDto checkdto;// 与原凭证比对的凭证信息
	private IDto checkMaindDto;// 与原凭证比对的主表信息
	private List checkSubdtoList;// 与原凭证比对的子表信息
	private TvVoucherinfoDto tempdto;// 初始凭证信息(5108凭证)
	private int count = 0;// 比对成功凭证笔数

	/**
	 * 凭证关键业务要素勾兑(批量)
	 * 
	 * @param dto
	 *            索引表信息
	 * @param maindto
	 *            主表信息
	 * @param subdtoList
	 *            子表集合
	 * @return
	 * @throws ITFEBizException
	 */
	public int VoucherCompare(List<List> succList) throws ITFEBizException {
		// 非上海无纸化或校验业务要素成功的凭证集合为0，凭证不比对
		if (succList == null || succList.size() == 0)
			return 0;
		else if(((Object)succList.get(0)) instanceof Integer)
			return ((Integer)(Object)succList.get(0)).intValue();
			
		// 上海无纸化特色凭证报文比对
		for (List list : succList) {
			TvVoucherinfoDto dto = (TvVoucherinfoDto) list.get(1);
			IDto maindto = (IDto) list.get(0);
			List subdtoList = null;
			// 如果校验成功，则不比对（主要是针对单笔业务）
			if (dto.getSstatus().equals(
					DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)) {
				count += 1;
				continue;
			}
			// 子表集合
			if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)||dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5267))
				subdtoList = (List) list.get(3);
			else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5351)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)
					|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5253))
				subdtoList = (List) list.get(4);
			else
				throw new ITFEBizException("没有定义此业务类型!");
			VoucherCompare(dto, maindto, subdtoList);
		}
		return count;
	}

	/**
	 * 凭证关键业务要素勾兑
	 * 
	 * @param dto
	 *            索引表信息
	 * @param maindto
	 *            主表信息
	 * @param subdtoList
	 *            子表集合
	 * @return
	 * @throws ITFEBizException
	 */
	public void VoucherCompare(TvVoucherinfoDto dto, IDto maindto,
			List subdtoList) throws ITFEBizException {
		try {
			// 判断凭证是否重复
			if (voucherCompareIsRepeat(dto))
				return;
			// 5201和8207、5207和8207是全业务要素勾兑(一比一)
			if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)
					|| dto.getScheckvouchertype().equals(
							MsgConstant.VOUCHER_NO_8207)) {
				voucherCompareAll(dto, maindto, subdtoList);
			}
			// 5108和5201、5106和2301、5108和5253、5106和5351是汇总勾兑
			else
				voucherCompareSummary(dto, maindto, subdtoList);
			// 未找到对应的凭证报文，比对操作结束
			if (checkdto == null) {
				// 将报文数据放入缓存
				SrvCacheFacade.cacheVoucherCompare(dto, maindto, subdtoList);
				return;
			}
			// 报文比对成功
			if (StringUtils.isBlank(tmpFailReason))
				voucherCompareUpdateStatusSuccess(dto, maindto, subdtoList);
			// 报文比对失败
			else
				voucherCompareUpdateStatusFail(dto);
			// 比对操作完成删除对应的缓存数据
			removeCacheVoucherCompare(dto);
		} catch (ITFEBizException e) {
			logger.error(e);
			// 比对操作异常删除对应的缓存数据
			removeCacheVoucherCompare(dto);
			throw new ITFEBizException(e.getMessage());
		} catch (JAFDatabaseException e) {
			logger.error(e);
			removeCacheVoucherCompare(dto);
			throw new ITFEBizException("查看数据库信息或更新凭证状态异常！", e);
		} catch (Exception e) {
			logger.error(e);
			removeCacheVoucherCompare(dto);
			throw new ITFEBizException("凭证比对操作异常！", e);
		}
		tmpFailReason = "";
	}

	/**
	 * 汇总清算信息凭证勾兑 主单：代理银行行号+总金额 明细：预算单位代码+功能科目代码+金额
	 * 
	 * @param dto
	 * @param maindto
	 * @param subdtoList
	 * @throws ITFEBizException
	 */
	private void voucherCompareSummary(TvVoucherinfoDto dto, IDto maindto,
			List subdtoList) throws ITFEBizException {
		HashMap checkMaindtoMap = null;
		HashMap checkSubdtoMap = null;
		// 从缓存查找对应的凭证比对信息
		List voucherCheckList = SrvCacheFacade.cacheVoucherCompare(dto);
		if (voucherCheckList != null && voucherCheckList.size() > 0) {
			checkdto = (TvVoucherinfoDto) voucherCheckList.get(0);
			checkMaindDto = (IDto) voucherCheckList.get(1);
			checkSubdtoList = (List) voucherCheckList.get(2);
		} else {
			// 查找索引表信息
			checkdto = VoucherUtil.findVoucherDto(dto,
					DealCodeConstants.VOUCHER_VALIDAT);
			// 未找到符合条件的索引表信息，比对操作终止
			if (checkdto == null)
				return;
			checkMaindDto = (IDto) VoucherUtil.findMainDtoByVoucher(checkdto)
					.get(0);
			// 授权支付额度5106涉及到凭证拆分，所以要查询出所有业务子表和汇总业务主表金额
			if (dto.getScheckvouchertype().equals(MsgConstant.VOUCHER_NO_5106)) {
				((TvGrantpaymsgmainDto) checkMaindDto).setNmoney(checkdto
						.getNmoney());
				((TvGrantpaymsgmainDto) checkMaindDto)
						.setSpackageticketno(null);
			}
			checkSubdtoList = PublicSearchFacade
					.findSubDtoByMain(checkMaindDto);
		}
		// 汇总清算信息凭证勾兑
			voucherCompareSummaryMain(dto, VoucherUtil
					.convertMainDtoToMap(maindto), VoucherUtil
					.convertMainDtoToMap(checkMaindDto));
		// 主单比对失败，不再比对明细，流程结束
		if (StringUtils.isNotBlank(tmpFailReason))
			return;
		// 比对明细预算单位代码+功能科目代码+金额
		voucherCompareSummarySub(dto, VoucherUtil
				.convertSubListToMap(subdtoList), VoucherUtil
				.convertSubListToMap(checkSubdtoList));
	}

	/**
	 * 汇总清算信息凭证勾兑 主单：代理银行行号+总金额
	 * 
	 * @param dto
	 * @param maindtoMap
	 * @param checkMaindtoMap
	 */
	private void voucherCompareSummaryMain(TvVoucherinfoDto dto,
			HashMap maindtoMap, HashMap checkMaindtoMap) {
		String paybankcode = (String) maindtoMap.get("paybankcode");// 代理行号
		String checkPaybankcode = (String) checkMaindtoMap.get("paybankcode");// 比对代理行号
		BigDecimal payAmt = MtoCodeTrans.transformBigDecimal(maindtoMap
				.get("payAmt"));// 总金额
		BigDecimal checkPayAmt = MtoCodeTrans
				.transformBigDecimal(checkMaindtoMap.get("payAmt"));// 比对总金额
		if (!paybankcode.equals(checkPaybankcode)) {
			tmpFailReason += "主单[代理银行行号]不相等，" + dto.getSvtcode() + "代理银行行号："
					+ paybankcode + "、" + dto.getScheckvouchertype()
					+ "代理银行行号：" + checkPaybankcode + "。";
		}
		if (payAmt.compareTo(checkPayAmt) != 0) {
			tmpFailReason += "主单[金额]不相等，" + dto.getSvtcode() + "金额：" + payAmt
					+ "、" + dto.getScheckvouchertype() + "金额：" + checkPayAmt
					+ "。";
		}
	}

	/**
	 * 汇总清算信息凭证勾兑 明细：预算单位代码+功能科目代码+金额
	 * 
	 * @param dto
	 * @param subdtoMap
	 * @param checkSubdtoMap
	 */
	private void voucherCompareSummarySub(TvVoucherinfoDto dto,
			HashMap subdtoMap, HashMap checkSubdtoMap) {
		// 比对明细汇总笔数
		voucherCompareSubDtoCount(subdtoMap, checkSubdtoMap, "明细汇总笔数", dto);
		// 明细汇总笔数不相等，不再比对明细详细信息，流程结束
		if (StringUtils.isNotBlank(tmpFailReason))
			return;
		String code = null;// 预算单位代码|功能科目代码
		BigDecimal detailPayAmt = BigDecimal.ZERO;// 明细汇总金额
		BigDecimal checkDetailPayAmt = BigDecimal.ZERO;// 比对明细汇总金额
		// 比对明细预算单位代码+功能科目代码+金额
		for (Iterator<String> it = subdtoMap.keySet().iterator(); it.hasNext();) {
			code = it.next();
			if (!checkSubdtoMap.containsKey(code)) {
				this.tmpFailReason += dto.getSvtcode() + "明细[预算单位代码]"
						+ code.substring(0, code.indexOf("|")) + "、[支出功能科目代码]"
						+ code.substring(code.indexOf("|") + 1) + "在"
						+ dto.getScheckvouchertype() + "不存在。";
				continue;
			}
			detailPayAmt = (BigDecimal) subdtoMap.get(code);
			checkDetailPayAmt = (BigDecimal) checkSubdtoMap.get(code);
			if (detailPayAmt.compareTo(checkDetailPayAmt) != 0)
				this.tmpFailReason += "明细相同预算单位代码"
						+ code.substring(0, code.indexOf("|")) + "、支出功能科目代码"
						+ code.substring(code.indexOf("|") + 1) + "[金额]不相等，"
						+ dto.getSvtcode() + "金额：" + detailPayAmt + "、"
						+ dto.getScheckvouchertype() + "金额："
						+ checkDetailPayAmt + "。";
		}
	}

	/**
	 * 全业务要素勾兑(一比一)
	 * 
	 * @param dto
	 * @param maindto
	 * @param subdtoList
	 * @throws ITFEBizException
	 */
	private void voucherCompareAll(TvVoucherinfoDto dto, IDto maindto,
			List subdtoList) throws ITFEBizException {
		HashMap checkSubdtoMap = new HashMap();
		// 从缓存查找对应的凭证比对信息
		List voucherCheckList = SrvCacheFacade.cacheVoucherCompare(dto);
		if (voucherCheckList != null && voucherCheckList.size() > 0) {
			checkdto = (TvVoucherinfoDto) voucherCheckList.get(0);
			checkMaindDto = (IDto) voucherCheckList.get(1);
			checkSubdtoList = (List) voucherCheckList.get(2);
		} else {
			// 查找索引表信息
			checkdto = VoucherUtil.findVoucherDto(dto,
					DealCodeConstants.VOUCHER_VALIDAT);
			// 未找到符合条件的索引表信息，比对操作终止
			if (checkdto == null)
				return;
			checkMaindDto = (IDto) VoucherUtil.findMainDtoByVoucher(checkdto)
					.get(0);
			checkSubdtoList = PublicSearchFacade
					.findSubDtoByMain(checkMaindDto);
		}
		// 比对主单信息
		voucherCompareAllMain(dto, maindto);
		// 主单比对失败，不再比对明细，流程结束
		if (StringUtils.isNotBlank(tmpFailReason))
			return;
		// 比对明细信息
		voucherCompareAllSub(dto, VoucherUtil.convertListToMap(subdtoList),
				VoucherUtil.convertListToMap(checkSubdtoList));
	}

	/**
	 * 全业务要素勾兑(一比一) 比对主单信息
	 * 
	 * @param dto
	 * @param maindto
	 * @param subdtoMap
	 * @param checkSubdtoMap
	 */
	private void voucherCompareAllMain(TvVoucherinfoDto dto, IDto maindto) {
		// 比对主单信息
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)
				|| dto.getScheckvouchertype().equals(
						MsgConstant.VOUCHER_NO_5207)) {
			TvPayoutmsgmainDto maindto5207 = null;
			TfPaymentDetailsmainDto maindto8207 = null;
			if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)) {
				maindto5207 = (TvPayoutmsgmainDto) maindto;
				maindto8207 = (TfPaymentDetailsmainDto) checkMaindDto;
			} else {
				maindto5207 = (TvPayoutmsgmainDto) checkMaindDto;
				maindto8207 = (TfPaymentDetailsmainDto) maindto;
			}
			// 5207和8207主单信息比对
			voucherCompareMainDto5207(maindto5207, maindto8207);
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)
				|| dto.getScheckvouchertype().equals(
						MsgConstant.VOUCHER_NO_5201)) {
			TfDirectpaymsgmainDto maindto5201 = null;
			TfPaymentDetailsmainDto maindto8207 = null;
			if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)) {
				maindto5201 = (TfDirectpaymsgmainDto) maindto;
				maindto8207 = (TfPaymentDetailsmainDto) checkMaindDto;
			} else {
				maindto5201 = (TfDirectpaymsgmainDto) checkMaindDto;
				maindto8207 = (TfPaymentDetailsmainDto) maindto;
			}
			// 5201和8207主单信息比对
			voucherCompareMainDto5201(maindto5201, maindto8207);
		}
	}

	/**
	 * 全业务要素勾兑(一比一) 比对明细信息
	 * 
	 * @param dto
	 * @param subdtoMap
	 * @param checkSubdtoMap
	 */
	private void voucherCompareAllSub(TvVoucherinfoDto dto, HashMap subdtoMap,
			HashMap checkSubdtoMap) {
		// 比对报文明细笔数
		voucherCompareSubDtoCount(subdtoMap, checkSubdtoMap, "明细笔数", dto);
		// 明细笔数不相等，不再比对明细详细信息，流程结束
		if (StringUtils.isNotBlank(tmpFailReason))
			return;
		// 比对报文明细Id是否存在
		for (Iterator<String> it = subdtoMap.keySet().iterator(); it.hasNext();) {
			String sid = it.next();
			if (!checkSubdtoMap.containsKey(sid))
				tmpFailReason += dto.getSvtcode() + "明细Id：" + sid + "在"
						+ dto.getScheckvouchertype() + "不存在。";
		}
		// 明细Id比对失败，不再比对明细详细信息，流程结束
		if (StringUtils.isNotBlank(tmpFailReason))
			return;
		// 比对明细详细信息
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)
				|| dto.getScheckvouchertype().equals(
						MsgConstant.VOUCHER_NO_5207)) {
			TvPayoutmsgsubDto suddto5207 = null;
			TfPaymentDetailssubDto suddto8207 = null;
			for (Iterator<String> it = subdtoMap.keySet().iterator(); it
					.hasNext();) {
				String sid = it.next();
				if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)) {
					suddto5207 = (TvPayoutmsgsubDto) subdtoMap.get(sid);
					suddto8207 = (TfPaymentDetailssubDto) checkSubdtoMap
							.get(sid);
				} else {
					suddto5207 = (TvPayoutmsgsubDto) checkSubdtoMap.get(sid);
					suddto8207 = (TfPaymentDetailssubDto) subdtoMap.get(sid);
				}
				// 5207和8207明细详细信息比对
				voucherCompareSubDto5207(suddto5207, suddto8207);
			}
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)
				|| dto.getScheckvouchertype().equals(
						MsgConstant.VOUCHER_NO_5201)) {
			TfDirectpaymsgsubDto suddto5201 = null;
			TfPaymentDetailssubDto suddto8207 = null;
			for (Iterator<String> it = subdtoMap.keySet().iterator(); it
					.hasNext();) {
				String sid = it.next();
				if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)) {
					suddto5201 = (TfDirectpaymsgsubDto) subdtoMap.get(sid);
					suddto8207 = (TfPaymentDetailssubDto) checkSubdtoMap
							.get(sid);
				} else {
					suddto5201 = (TfDirectpaymsgsubDto) checkSubdtoMap.get(sid);
					suddto8207 = (TfPaymentDetailssubDto) subdtoMap.get(sid);
				}
				// 5207和8207明细详细信息比对
				voucherCompareSubDto5201(suddto5201, suddto8207);
			}
		}
	}

	/**
	 * 实拨资金5207报文和批量明细8207报文比对主单信息
	 * 
	 * @param maindto5207
	 * @param maindto8207
	 */
	private void voucherCompareMainDto5207(TvPayoutmsgmainDto maindto5207,
			TfPaymentDetailsmainDto maindto8207) {
		if (maindto5207.getNmoney().compareTo(maindto8207.getNsumamt()) != 0) {
			tmpFailReason += "主单[金额]不相等，" + MsgConstant.VOUCHER_NO_5207 + "金额："
					+ maindto5207.getNmoney() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "金额："
					+ maindto8207.getNsumamt() + "。";
		}
		if (!maindto5207.getSpayeracct().equals(maindto8207.getSpayacctno())) {
			tmpFailReason += "主单[付款人账号]不相等，" + MsgConstant.VOUCHER_NO_5207
					+ "付款人账号：" + maindto5207.getSpayeracct() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "付款人账号："
					+ maindto8207.getSpayacctno() + "。";
		}
		if (!maindto5207.getSpayername().equals(maindto8207.getSpayacctname())) {
			tmpFailReason += "主单[付款人名称]不相等，" + MsgConstant.VOUCHER_NO_5207
					+ "付款人名称：" + maindto5207.getSpayername() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "付款人名称："
					+ maindto8207.getSpayacctname() + "。";
		}
		if (!maindto5207.getSpayerbankname().equals(
				maindto8207.getSpayacctbankname())) {
			tmpFailReason += "主单[付款人银行行名]不相等，" + MsgConstant.VOUCHER_NO_5207
					+ "付款人银行行名：" + maindto5207.getSpayerbankname() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "付款人银行行名："
					+ maindto8207.getSpayacctbankname() + "。";
		}
		if (!maindto5207.getSrecbankno().equals(maindto8207.getSpaybankcode())) {
			tmpFailReason += "主单[代理银行编码]不相等，" + MsgConstant.VOUCHER_NO_5207
					+ "代理银行编码：" + maindto5207.getSrecbankno() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "代理银行编码："
					+ maindto8207.getSpaybankcode() + "。";
		}
		// if(!maindto5207.getSclearbankname().equals(maindto8207.getSpaybankname())){
		// tmpFailReason+="主单[代理银行名称]不相等，"+MsgConstant.VOUCHER_NO_5207+"代理银行名称："+
		// maindto5207.getSclearbankname()+"、"+MsgConstant.VOUCHER_NO_8207+"代理银行名称："+maindto8207.getSpaybankname()+"。";
		// }
	}

	/**
	 * 实拨资金5207报文和批量明细8207报文比对明细信息
	 * 
	 * @param suddto5207
	 * @param suddto8207
	 */
	private void voucherCompareSubDto5207(TvPayoutmsgsubDto suddto5207,
			TfPaymentDetailssubDto suddto8207) {
		if (suddto5207.getNmoney().compareTo(suddto8207.getNpayamt()) != 0) {
			tmpFailReason += "明细[金额]不相等，" + MsgConstant.VOUCHER_NO_5207 + "金额："
					+ suddto5207.getNmoney() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "金额："
					+ suddto8207.getNpayamt() + "。";
		}
		if (!suddto5207.getSpayeeacctno().equals(suddto8207.getSpayeeacctno())) {
			tmpFailReason += "明细[收款人账号]不相等，" + MsgConstant.VOUCHER_NO_5207
					+ "收款人账号：" + suddto5207.getSpayeeacctno() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "收款人账号："
					+ suddto8207.getSpayeeacctno() + "。";
		}
		if (!suddto5207.getSpayeeacctname().equals(
				suddto8207.getSpayeeacctname())) {
			tmpFailReason += "明细[收款人名称]不相等，" + MsgConstant.VOUCHER_NO_5207
					+ "收款人名称：" + suddto5207.getSpayeeacctname() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "收款人名称："
					+ suddto8207.getSpayeeacctname() + "。";
		}
		if (!suddto5207.getSpayeeacctbankname().equals(
				suddto8207.getSpayeeacctbankname())) {
			tmpFailReason += "明细[收款人银行]不相等，" + MsgConstant.VOUCHER_NO_5207
					+ "收款人银行：" + suddto5207.getNmoney() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "收款人银行："
					+ suddto8207.getSpayeeacctbankname() + "。";
		}
	}

	/**
	 * 直接支付5201报文和批量明细8207报文比对主单信息
	 * 
	 * @param dto5201
	 * @param dto8207
	 */
	private void voucherCompareMainDto5201(TfDirectpaymsgmainDto maindto5201,
			TfPaymentDetailsmainDto maindto8207) {
		if (maindto5201.getNpayamt().compareTo(maindto8207.getNsumamt()) != 0) {
			tmpFailReason += "主单[金额]不相等，" + MsgConstant.VOUCHER_NO_5201 + "金额："
					+ maindto5201.getNpayamt() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "金额："
					+ maindto8207.getNsumamt() + "。";
		}
		if (!maindto5201.getSpayacctno().equals(maindto8207.getSpayacctno())) {
			tmpFailReason += "主单[付款人账号]不相等，" + MsgConstant.VOUCHER_NO_5201
					+ "付款人账号：" + maindto5201.getSpayacctno() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "付款人账号："
					+ maindto8207.getSpayacctno() + "。";
		}
		if (!maindto5201.getSpayacctname()
				.equals(maindto8207.getSpayacctname())) {
			tmpFailReason += "主单[付款人名称]不相等，" + MsgConstant.VOUCHER_NO_5201
					+ "付款人名称：" + maindto5201.getSpayacctname() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "付款人名称："
					+ maindto8207.getSpayacctname() + "。";
		}
		if (!maindto5201.getSpayacctbankname().equals(
				maindto8207.getSpayacctbankname())) {
			tmpFailReason += "主单[付款人银行行名]不相等，" + MsgConstant.VOUCHER_NO_5201
					+ "付款人银行行名：" + maindto5201.getSpayacctbankname() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "付款人银行行名："
					+ maindto8207.getSpayacctbankname() + "。";
		}
		if (!maindto5201.getSpayeeacctbankno().equals(
				maindto8207.getSpaybankcode())) {
			tmpFailReason += "主单[代理银行编码]不相等，" + MsgConstant.VOUCHER_NO_5201
					+ "代理银行编码：" + maindto5201.getSpayeeacctbankno() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "代理银行编码："
					+ maindto8207.getSpaybankcode() + "。";
		}
		if (!maindto5201.getSpayeeacctbankname().equals(
				maindto8207.getSpaybankname())) {
			tmpFailReason += "主单[代理银行名称]不相等，" + MsgConstant.VOUCHER_NO_5201
					+ "代理银行名称：" + maindto5201.getSpayeeacctbankname() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "代理银行名称："
					+ maindto8207.getSpaybankname() + "。";
		}
	}

	/**
	 * 直接支付5201报文和批量明细8207报文比对明细信息
	 * 
	 * @param suddto5201
	 * @param suddto8207
	 */
	private void voucherCompareSubDto5201(TfDirectpaymsgsubDto suddto5201,
			TfPaymentDetailssubDto suddto8207) {
		if (suddto5201.getNpayamt().compareTo(suddto8207.getNpayamt()) != 0) {
			tmpFailReason += "明细[金额]不相等，" + MsgConstant.VOUCHER_NO_5201 + "金额："
					+ suddto5201.getNpayamt() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "金额："
					+ suddto8207.getNpayamt() + "。";
		}
		if (!suddto5201.getSpayeeacctno().equals(suddto8207.getSpayeeacctno())) {
			tmpFailReason += "明细[收款人账号]不相等，" + MsgConstant.VOUCHER_NO_5201
					+ "收款人账号：" + suddto5201.getSpayeeacctno() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "收款人账号："
					+ suddto8207.getSpayeeacctno() + "。";
		}
		if (!suddto5201.getSpayeeacctname().equals(
				suddto8207.getSpayeeacctname())) {
			tmpFailReason += "明细[收款人名称]不相等，" + MsgConstant.VOUCHER_NO_5201
					+ "收款人名称：" + suddto5201.getSpayeeacctname() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "收款人名称："
					+ suddto8207.getSpayeeacctname() + "。";
		}
		if (!suddto5201.getSpayeeacctbankname().equals(
				suddto8207.getSpayeeacctbankname())) {
			tmpFailReason += "明细[收款人银行]不相等，" + MsgConstant.VOUCHER_NO_5201
					+ "收款人银行：" + suddto5201.getSpayeeacctbankname() + "、"
					+ MsgConstant.VOUCHER_NO_8207 + "收款人银行："
					+ suddto8207.getSpayeeacctbankname() + "。";
		}
	}

	/**
	 * 比对报文明细笔数
	 * 
	 * @param subdtoMap
	 * @param checkSubdtoMap
	 * @param sdemo
	 * @param dto
	 */
	private void voucherCompareSubDtoCount(HashMap subdtoMap,
			HashMap checkSubdtoMap, String sdemo, TvVoucherinfoDto dto) {
		if (subdtoMap.size() != checkSubdtoMap.size())
			tmpFailReason += sdemo + "不相等，" + dto.getSvtcode() + sdemo + "："
					+ subdtoMap.size() + "、" + dto.getScheckvouchertype()
					+ sdemo + "：" + checkSubdtoMap.size() + "。";
	}

	/**
	 * 报文比对成功更新凭证状态
	 * 
	 * @param dto
	 * @param maindto
	 * @param subdtoList
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public void voucherCompareUpdateStatusSuccess(TvVoucherinfoDto dto,
			IDto maindto, List subdtoList) throws JAFDatabaseException,
			ITFEBizException {
		// 报文比对成功后，判断报文是否还与其他报文进行比对
		// true 继续比对 false 不再比对，更新报文状态为成功
		if (VoucherCompreIsContinue(dto, maindto, subdtoList))
			return;
		// 报文比对成功，更新凭证状态
		VoucherUtil.voucherVerifyUpdateStatus(dto, null, "报文比对成功", true);
		VoucherUtil.voucherVerifyUpdateStatus(checkdto, null, "报文比对成功", true);
		tempdto = findTempVoucher(dto);
		if (tempdto != null)
			VoucherUtil.voucherVerifyUpdateStatus(tempdto, null, "业务要素勾兑成功",
					true);
		logger.debug("=====================报文比对成功========================");
		count++;

	}

	/**
	 * 报文比对失败更新凭证状态
	 * 
	 * @param dto
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public void voucherCompareUpdateStatusFail(TvVoucherinfoDto dto)
			throws JAFDatabaseException, ITFEBizException {
		String failReason = tmpFailReason.getBytes().length > 1000 ? tmpFailReason
				.substring(0, 500)
				: tmpFailReason;
		VoucherUtil.voucherVerifyUpdateStatus(dto, null, "业务要素勾兑失败："
				+ failReason, false);
		if (checkdto != null)
			VoucherUtil.voucherVerifyUpdateStatus(checkdto, null, "业务要素勾兑失败："
					+ failReason, false);
		tempdto = findTempVoucher(dto);
		if (tempdto != null)
			VoucherUtil.voucherVerifyUpdateStatus(tempdto, null, "业务要素勾兑失败："
					+ failReason, false);
		logger.debug("=====================报文比对失败========================");
		logger.debug(dto.getSvtcode() + "报文与" + dto.getScheckvouchertype()
				+ "报文比对失败，失败原因：" + tmpFailReason);
	}

	/**
	 * 报文比对成功后，判断报文是否还与其他报文进行比对 true 继续比对 false 不再比对，更新报文状态为成功
	 * 
	 * @param dto
	 * @param maindto
	 * @param subdtoList
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	private Boolean VoucherCompreIsContinue(TvVoucherinfoDto dto, IDto maindto,
			List subdtoList) throws ITFEBizException, JAFDatabaseException {
		Boolean flag = false;
		// 单笔的5201不需要与8207比对
		// 5201和5108报文比对成功后，5201和8207报文继续比对
		if ((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)
				&& dto.getScheckvouchertype().equals(
						MsgConstant.VOUCHER_NO_5201) && "1".equals(dto
				.getShold1().trim()))
				|| (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)
						&& dto.getScheckvouchertype().equals(
								MsgConstant.VOUCHER_NO_5108) && StateConstant.BIZTYPE_CODE_BATCH
						.equals(dto.getShold4().trim()))) {
			flag = true;
			String sdemo5108 = "等待[5201]凭证与[8207]的报文进行业务要素勾兑";
			String sdemo5201 = "等待[8207]的报文进行业务要素勾兑";
			if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {
				dto.setSdemo(sdemo5108);
				checkdto.setSdemo(sdemo5201);
				checkdto.setScheckvouchertype(MsgConstant.VOUCHER_NO_8207);
				tempdto = dto;// 缓存dto为5108凭证dto
			} else {
				dto.setSdemo(sdemo5201);
				dto.setScheckvouchertype(MsgConstant.VOUCHER_NO_8207);
				checkdto.setSdemo(sdemo5108);
				tempdto = checkdto;
			}
			DatabaseFacade.getODB().update(dto);
			DatabaseFacade.getODB().update(checkdto);
			logger.debug("=====================报文比对成功========================");
			if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {
				dto = checkdto;
				maindto = checkMaindDto;
				subdtoList = checkSubdtoList;
			}
			// 凭证继续比对
			VoucherCompare(dto, maindto, subdtoList);
		}
		return flag;
	}

	/**
	 * 5201、5207凭证比对成功后业务主表附言填写对应的8207凭证编号
	 * 
	 * @param dto
	 * @param maindto
	 * @throws JAFDatabaseException
	 */
	private void VoucherCompareUpdateMainDto(TvVoucherinfoDto dto, IDto maindto)
			throws JAFDatabaseException {
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)
				|| dto.getScheckvouchertype().equals(
						MsgConstant.VOUCHER_NO_5201)) {
			TfDirectpaymsgmainDto maindto5201 = null;
			String sdemo = null;
			if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)) {
				maindto5201 = (TfDirectpaymsgmainDto) maindto;
				sdemo = checkdto.getSvoucherno() + "。" + maindto5201.getSdemo();
			} else {
				maindto5201 = (TfDirectpaymsgmainDto) checkMaindDto;
				sdemo = dto.getSvoucherno() + "。" + maindto5201.getSdemo();
			}
			// 5201凭证附言填写对应的8207凭证编号
			maindto5201.setSdemo(sdemo.getBytes().length > 60 ? sdemo
					.substring(0, 30) : sdemo);
			DatabaseFacade.getODB().update(maindto5201);
		} else if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5207)
				|| dto.getScheckvouchertype().equals(
						MsgConstant.VOUCHER_NO_5207)) {
			TvPayoutmsgmainDto maindto5207 = null;
			String sdemo = null;
			if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)) {
				maindto5207 = (TvPayoutmsgmainDto) maindto;
				sdemo = checkdto.getSvoucherno() + "。" + maindto5207.getSdemo();
			} else {
				maindto5207 = (TvPayoutmsgmainDto) checkMaindDto;
				sdemo = dto.getSvoucherno() + "。" + maindto5207.getSdemo();
			}
			// 5207凭证附言填写对应的8207凭证编号
			maindto5207.setSdemo(sdemo.getBytes().length > 60 ? sdemo
					.substring(0, 30) : sdemo);
			DatabaseFacade.getODB().update(maindto5207);
		}
	}

	/**
	 * 比对操作完成删除对应的缓存数据
	 * 
	 * @param dto
	 */
	private void removeCacheVoucherCompare(TvVoucherinfoDto dto) {
		// 比对操作完成删除对应的缓存数据
		SrvCacheFacade.removeCacheVoucherCompare(dto);
		SrvCacheFacade.removeCacheVoucherCompare(checkdto);
		SrvCacheFacade.removeCacheVoucherCompare(tempdto);
	}

	/**
	 * 查找原5108凭证
	 * 
	 * @param idto
	 * @return
	 * @throws ITFEBizException
	 */
	private TvVoucherinfoDto findTempVoucher(TvVoucherinfoDto dto)
			throws ITFEBizException {
		if ((!dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201) && !dto
				.getScheckvouchertype().equals(MsgConstant.VOUCHER_NO_5201))
				|| checkdto == null)
			return null;
		if (tempdto != null)
			return tempdto;
		TvVoucherinfoDto tempdto = dto.getSvtcode().equals(
				MsgConstant.VOUCHER_NO_5201) ? dto : checkdto;
		tempdto.setScheckvouchertype(MsgConstant.VOUCHER_NO_5108);
		List voucherCheckList = SrvCacheFacade.cacheVoucherCompare(tempdto);
		if (voucherCheckList != null && voucherCheckList.size() > 0)
			return (TvVoucherinfoDto) voucherCheckList.get(0);
		else
			return VoucherUtil.findVoucherDto(tempdto,
					DealCodeConstants.VOUCHER_VALIDAT);
	}

	/**
	 * 判断凭证是否重复 true 重复，凭证不再比对，更新凭证状态为校验失败 false 不重复，继续比对凭证
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	private Boolean voucherCompareIsRepeat(TvVoucherinfoDto dto)
			throws ITFEBizException, JAFDatabaseException {
		List list = VoucherUtil.findVoucherDto(dto);
		if (list == null || list.size() <= 1)
			return false;
		if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207))
			tmpFailReason = "勾兑凭证编号" + dto.getShold3() + "重复。";
		else
			tmpFailReason = "凭证编号" + dto.getSvoucherno() + "重复。";
		for (TvVoucherinfoDto tempdto : (List<TvVoucherinfoDto>) list) {
			voucherCompareUpdateStatusFail(tempdto);
			removeCacheVoucherCompare(dto);
		}
		return true;
	}

	public int getCount() {
		return count;
	}

	/**
	 * 对比 回单8207和接收财政发送8207单子内容对比 returnList 回单内容 acceptList 接收的8207
	 */
	public String compare8207AcceptAndReturn(List returnList, List acceptList) {
		// <VoucherFlag>0发送单/1回单/ </ VoucherFlag >
		DecimalFormat df = new DecimalFormat("0.00");
		if (null == returnList || returnList.size() == 0 || null == acceptList
				|| acceptList.size() == 0) {
			return "对比8207信息时，组织基础信息有误！";
		}
		TvVoucherinfoDto returnDto = (TvVoucherinfoDto) returnList.get(0);
		TvVoucherinfoDto acceptDto = (TvVoucherinfoDto) acceptList.get(0);
		// 比对凭证索引信息
		if (!returnDto.getSvoucherno().equals(acceptDto.getSvoucherno())) {
			return "现凭证编号：[" + returnDto.getSvoucherno() + "]与原凭证编码：["
					+ acceptDto.getSvoucherno() + "]不一致！";
		} else if (!returnDto.getStrecode().equals(acceptDto.getStrecode())) {
			return "现凭证国库代码：[" + returnDto.getStrecode() + "]与原凭证国库代码：["
					+ acceptDto.getStrecode() + "]不一致！";
		} else if (!returnDto.getSadmdivcode().equals(
				acceptDto.getSadmdivcode())) {
			return "现凭证行政区划代码：[" + returnDto.getStrecode() + "]与原凭证行政区划代码：["
					+ acceptDto.getStrecode() + "]不一致！";
		} else if (!MtoCodeTrans.transformString(returnDto.getNmoney()).equals(
				MtoCodeTrans.transformString(acceptDto.getNmoney()))) {
			return "现凭金额：["
					+ MtoCodeTrans.transformString(returnDto.getNmoney())
					+ "]与原金额：["
					+ MtoCodeTrans.transformString(acceptDto.getNmoney())
					+ "]不一致！";
		} else if (!returnDto.getSstyear().equals(acceptDto.getSstyear())) {
			return "现凭证年度：[" + returnDto.getSstyear() + "]与原凭证年度：["
					+ acceptDto.getSstyear() + "]不一致！";
		}
		// 比对主单信息
		TfPaymentDetailsmainDto returnTfPaymentDetailsmainDto = (TfPaymentDetailsmainDto) returnList
				.get(1);
		TfPaymentDetailsmainDto acceptTfPaymentDetailsmainDto = (TfPaymentDetailsmainDto) acceptList
				.get(1);
		if (!returnTfPaymentDetailsmainDto.getSvoudate().equals(
				acceptTfPaymentDetailsmainDto.getSvoudate())) {
			return "现凭证日期：[" + returnTfPaymentDetailsmainDto.getSvoudate()
					+ "]与原凭证日期：[" + acceptTfPaymentDetailsmainDto.getSvoudate()
					+ "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSoriginalvtcode().equals(
				acceptTfPaymentDetailsmainDto.getSoriginalvtcode())) {
			return "现主凭证类型编号：["
					+ returnTfPaymentDetailsmainDto.getSoriginalvtcode()
					+ "]与原主凭证类型编号：["
					+ acceptTfPaymentDetailsmainDto.getSoriginalvtcode()
					+ "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSoriginalvoucherno()
				.equals(acceptTfPaymentDetailsmainDto.getSoriginalvoucherno())) {
			return "现主支付凭证编号：["
					+ returnTfPaymentDetailsmainDto.getSoriginalvoucherno()
					+ "]与原主支付凭证编号：["
					+ acceptTfPaymentDetailsmainDto.getSoriginalvoucherno()
					+ "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSfundtypecode().equals(
				acceptTfPaymentDetailsmainDto.getSfundtypecode())) {
			return "现资金性质编码：["
					+ returnTfPaymentDetailsmainDto.getSfundtypecode()
					+ "]与原资金性质编码：["
					+ acceptTfPaymentDetailsmainDto.getSfundtypecode()
					+ "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSfundtypename().equals(
				acceptTfPaymentDetailsmainDto.getSfundtypename())) {
			return "现资金性质名称：["
					+ returnTfPaymentDetailsmainDto.getSfundtypename()
					+ "]与原资金性质名称：["
					+ acceptTfPaymentDetailsmainDto.getSfundtypename()
					+ "]不一致！";
		} else if (!MtoCodeTrans.transformString(
				df.format(returnTfPaymentDetailsmainDto.getNsumamt())).equals(
				MtoCodeTrans.transformString(acceptTfPaymentDetailsmainDto
						.getNsumamt()))) {
			return "现汇总支付金额：["
					+ MtoCodeTrans
							.transformString(df
									.format(returnTfPaymentDetailsmainDto
											.getNsumamt()))
					+ "]与原汇总支付金额：["
					+ MtoCodeTrans
							.transformString(acceptTfPaymentDetailsmainDto
									.getNsumamt()) + "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSpayacctno().equals(
				acceptTfPaymentDetailsmainDto.getSpayacctno())) {
			return "现付款人账号：[" + returnTfPaymentDetailsmainDto.getSpayacctno()
					+ "]与原付款人账号：["
					+ acceptTfPaymentDetailsmainDto.getSpayacctno() + "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSpayacctname().equals(
				acceptTfPaymentDetailsmainDto.getSpayacctname())) {
			return "现付款人名称：[" + returnTfPaymentDetailsmainDto.getSpayacctname()
					+ "]与原付款人名称：["
					+ acceptTfPaymentDetailsmainDto.getSpayacctname() + "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSpayacctbankname().equals(
				acceptTfPaymentDetailsmainDto.getSpayacctbankname())) {
			return "现付款人银行：["
					+ returnTfPaymentDetailsmainDto.getSpayacctbankname()
					+ "]与原付款人银行：["
					+ acceptTfPaymentDetailsmainDto.getSpayacctbankname()
					+ "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSpaybankcode().equals(
				acceptTfPaymentDetailsmainDto.getSpaybankcode())) {
			return "现代理银行编码：["
					+ returnTfPaymentDetailsmainDto.getSpaybankcode()
					+ "]与原代理银行编码：["
					+ acceptTfPaymentDetailsmainDto.getSpaybankcode() + "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSpaybankname().equals(
				acceptTfPaymentDetailsmainDto.getSpaybankname())) {
			return "现代理银行名称：["
					+ returnTfPaymentDetailsmainDto.getSpaybankname()
					+ "]与原代理银行名称：["
					+ acceptTfPaymentDetailsmainDto.getSpaybankname() + "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSbusinesstypecode()
				.equals(acceptTfPaymentDetailsmainDto.getSbusinesstypecode())) {
			return "现业务类型编码：["
					+ returnTfPaymentDetailsmainDto.getSbusinesstypecode()
					+ "]与原业务类型编码：["
					+ acceptTfPaymentDetailsmainDto.getSbusinesstypecode()
					+ "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSbusinesstypename()
				.equals(acceptTfPaymentDetailsmainDto.getSbusinesstypename())) {
			return "现业务类型名称：["
					+ returnTfPaymentDetailsmainDto.getSbusinesstypename()
					+ "]与原业务类型名称：["
					+ acceptTfPaymentDetailsmainDto.getSbusinesstypename()
					+ "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSpaytypecode().equals(
				acceptTfPaymentDetailsmainDto.getSpaytypecode())) {
			return "现支付方式编码：["
					+ returnTfPaymentDetailsmainDto.getSpaytypecode()
					+ "]与原支付方式编码：["
					+ acceptTfPaymentDetailsmainDto.getSpaytypecode() + "]不一致！";
		} else if (!returnTfPaymentDetailsmainDto.getSpaytypename().equals(
				acceptTfPaymentDetailsmainDto.getSpaytypename())) {
			return "现支付方式名称：["
					+ returnTfPaymentDetailsmainDto.getSpaytypename()
					+ "]与原支付方式名称：["
					+ acceptTfPaymentDetailsmainDto.getSpaytypename() + "]不一致！";
		} else if (StringUtils.isBlank(returnTfPaymentDetailsmainDto
				.getSxpaydate())) {
			return "实际支付日期不能为空！";
		} else if (null == returnTfPaymentDetailsmainDto.getNxsumamt()) {
			return "实际支付汇总金额不能为空！";
		}
		// 把X开都信息补录在主信息
		acceptTfPaymentDetailsmainDto
				.setSxpaydate(returnTfPaymentDetailsmainDto.getSxpaydate()); // 实际支付日期
		acceptTfPaymentDetailsmainDto.setNxsumamt(returnTfPaymentDetailsmainDto
				.getNxsumamt()); // 实际支付汇总金额
		// 比对明细
		List returnTfList = (List) returnList.get(2);
		List acceptTfList = (List) acceptList.get(2);
		if (null == returnList || returnTfList.size() == 0
				|| null == acceptTfList || acceptTfList.size() == 0) {
			return "与原凭证明细信息不符！";
		} else if (returnTfList.size() != acceptTfList.size()) {
			return "与原凭证明细信息条数不符！";
		}
		Map<String, TfPaymentDetailssubDto> returnMaps = assemble8207SubMap(returnTfList);
		Map<String, TfPaymentDetailssubDto> acceptMaps = assemble8207SubMap(acceptTfList);
		TfPaymentDetailssubDto returnSubDto;
		TfPaymentDetailssubDto acceptSubDto;
		String id;
		for (int i = 0; i < acceptTfList.size(); i++) {
			id = ((TfPaymentDetailssubDto) returnTfList.get(i)).getSid();
			returnSubDto = returnMaps.get(id);
			acceptSubDto = acceptMaps.get(id);
			if (null == returnSubDto || null == acceptSubDto) {
				return "与原凭证明细信息不符！";
			} else if (!returnSubDto.getSid().equals(acceptSubDto.getSid())) {
				return "现明细编号：[" + returnSubDto.getSid() + "]与原明细编号：["
						+ acceptSubDto.getSid() + "]不一致！";
			} else if (!returnSubDto.getSpayeeacctno().equals(
					acceptSubDto.getSpayeeacctno())) {
				return "现收款人账号：[" + returnSubDto.getSid() + "]与原收款人账号：["
						+ acceptSubDto.getSid() + "]不一致！";
			} else if (!returnSubDto.getSpayeeacctname().equals(
					acceptSubDto.getSpayeeacctname())) {
				return "现收款人名称：[" + returnSubDto.getSpayeeacctname()
						+ "]与原收款人名称：[" + acceptSubDto.getSpayeeacctname()
						+ "]不一致！";
			} else if (!returnSubDto.getSpayeeacctbankname().equals(
					acceptSubDto.getSpayeeacctbankname())) {
				return "现收款人银行：[" + returnSubDto.getSpayeeacctbankname()
						+ "]与原收款人银行：[" + acceptSubDto.getSpayeeacctbankname()
						+ "]不一致！";
			} else if (!MtoCodeTrans.transformString(
					df.format(returnSubDto.getNpayamt())).equals(
					MtoCodeTrans.transformString((acceptSubDto.getNpayamt())))) {
				return "现支付金额：["
						+ MtoCodeTrans.transformString(df.format(returnSubDto
								.getNpayamt()))
						+ "]与原支付金额：["
						+ MtoCodeTrans.transformString(acceptSubDto
								.getNpayamt()) + "]不一致！";
			} else if (StringUtils.isBlank(returnSubDto.getSxpaydate())) {
				return "实际支付日期不能为空！";
			} else if (StringUtils.isBlank(returnSubDto.getSxagentbusinessno())) {
				return "银行交易流水号不能为空！";
			} else if (null == returnSubDto.getNxpayamt()) {
				return "实际支付金额不能为空！";
			} else if (StringUtils.isBlank(returnSubDto
					.getSxpayeeacctbankname())) {
				return "实际收款人银行不能为空！";
			} else if (StringUtils.isBlank(returnSubDto.getSxpayeeacctno())) {
				return "实际收款人账号不能为空！";
			}
			acceptSubDto.setSxpaydate(returnSubDto.getSxpaydate()); // 实际支付日期
			acceptSubDto.setSxagentbusinessno(returnSubDto
					.getSxagentbusinessno()); // 银行交易流水号
			acceptSubDto.setNxpayamt(returnSubDto.getNxpayamt()); // 实际支付金额
			acceptSubDto.setSxpayeeacctno(returnSubDto.getSxpayeeacctno()); // 实际收款人账号
			acceptSubDto.setSxaddwordcode(returnSubDto.getSxaddwordcode()); // 失败原因代码
			acceptSubDto.setSxaddword(returnSubDto.getSxaddword()); // 失败原因

		}

		return "";
	}

	/**
	 * 组装8207明细信息进行对比 key：为8207Id vouale：为subdto
	 */
	private static Map<String, TfPaymentDetailssubDto> assemble8207SubMap(
			List<TfPaymentDetailssubDto> list) {
		Map<String, TfPaymentDetailssubDto> maps = new HashMap<String, TfPaymentDetailssubDto>();
		for (TfPaymentDetailssubDto subdto : list) {
			maps.put(subdto.getSid(), subdto);
		}
		return maps;
	}

}

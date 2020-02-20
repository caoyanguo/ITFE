package com.cfcc.itfe.voucher.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutDetailMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.persistence.pk.TsInfoconnorgaccPK;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;
import com.ibm.db2.jcc.am.t;

/**
 * 凭证校验
 * 
 * @author Yuan
 * 
 */
public class VoucherVerify {
	private static Log logger = LogFactory.getLog(VoucherVerify.class);

	private String tmpFailReason;// 校验失败原因
	
	public VoucherVerify() {
		tmpFailReason = "";
	}

	/**
	 * 凭证校验
	 * 
	 * @param Lists
	 *            凭证集合
	 * @param vtcode
	 *            凭证类型
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public int verify(List lists, String vtCode) throws ITFEBizException {
		// 返回校验成功的凭证List
		List<TvVoucherinfoDto> successList = new ArrayList<TvVoucherinfoDto>();
		// 上海无纸化校验业务要素成功后比对的凭证List
		List succList;
		int count = 0;
		VoucherCompare voucherCompare = new VoucherCompare();
		try {
			if (vtCode.equals(MsgConstant.VOUCHER_NO_5267)) {
				succList = verifyFor5207(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// 比对凭证
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5207)) {
				succList = verifyFor5207(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// 比对凭证
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_2301)) {
				succList = verifyFor2301(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// 比对凭证
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_2302)) {
				successList = verifyFor2302(lists, vtCode);
				count = successList.size();
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5108)) {
				succList = verifyFor5108(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// 比对凭证
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5106)) {
				succList = verifyFor5106(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// 比对凭证
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5209)) {
				count = verifyFor5209(lists, vtCode);
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5201)) {
				succList = verifyFor5201(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// 比对凭证
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_2252)) {
				count = verifyFor2252(lists, vtCode);
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5351)) {
				succList = verifyFor5351(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// 比对凭证
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5253)) {
				succList = verifyFor5253(lists, vtCode);
				count = voucherCompare.VoucherCompare(succList);// 比对凭证
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_5257)) {
				successList = verifyFor5257(lists, vtCode);
				count = successList.size();
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_3507)) {
				count = verifyFor3507(lists, vtCode);
			} else if (vtCode.equals(MsgConstant.VOUCHER_NO_3508)) {
				count = verifyFor3508(lists, vtCode);
			}else if (vtCode.equals(MsgConstant.VOUCHER_NO_5407)) {
				count = verifyFor5407(lists, vtCode);
			}else if (vtCode.equals(MsgConstant.VOUCHER_NO_5671)||vtCode.equals(MsgConstant.VOUCHER_NO_5408)) {
				count = verifyFor5671(lists, vtCode);
			}
			
			//上海增加除科目校验失败时，其他校验错误全部退回
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") > 0){
				TvVoucherinfoDto tvVoucherinfoDto = null;
				TvVoucherinfoPK tvVoucherinfoPK = null;
				List returnBackVoucherList = new ArrayList();
				for(int i = 0 ; i < lists.size() ; i ++ ){
					tvVoucherinfoDto = (TvVoucherinfoDto) ((List)lists.get(i)).get(1);
					tvVoucherinfoPK = new TvVoucherinfoPK();
					tvVoucherinfoPK.setSdealno(tvVoucherinfoDto.getSdealno());
					tvVoucherinfoDto = (TvVoucherinfoDto) DatabaseFacade.getODB().find(tvVoucherinfoPK);
					//ITFE_001代表科目校验问题
//					System.out.println("状态：" + tvVoucherinfoDto.getSstatus() + "凭证编号:" + tvVoucherinfoDto.getSvoucherno() + "附言：" + tvVoucherinfoDto.getSdemo());
					if(DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(tvVoucherinfoDto.getSstatus().trim()) && !(tvVoucherinfoDto.getSdemo().startsWith("ITFE_001"))){
						returnBackVoucherList.add(tvVoucherinfoDto);
					}
				}
				if(null != returnBackVoucherList && returnBackVoucherList.size() > 0 ){
					Voucher voucher = (Voucher) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER);
					voucher.voucherReturnBack(returnBackVoucherList);
				}
				
			}
			
			
		} catch (ITFEBizException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查看业务主表信息或更新凭证状态异常！", e);
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("凭证校验操作异常！", e);
		}
		return count;
	}
	/**
	 * 校验  非税收入数据5671
	 * @param lists
	 * @param vtCode
	 * @return
	 */
	private int verifyFor5671(List lists, String vtCode) throws JAFDatabaseException, ValidateException, ITFEBizException,
	Exception{
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			TvNontaxmainDto mainDto = (TvNontaxmainDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);

			// 预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			
			if (verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())&& 
			verifySfinOrgCode(mainDto.getSfinorgcode(),vDto.getSorgcode(), vDto.getStrecode())
					&& verifySubject(vDto.getSorgcode(),expFuncCodeList, vtCode)
			) {
				
				VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
				count++;
				succList.add(new Integer(count));
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return count;
	}

	/**
	 * 校验 实拨拨款凭证清单信息5257
	 * 
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	private List<TvVoucherinfoDto> verifyFor5257(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List<TvVoucherinfoDto> returnList = new ArrayList<TvVoucherinfoDto>();
		for (List list : (List<List>) lists) {
			TvPayoutDetailMainDto mainDto = (TvPayoutDetailMainDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// 校验
			if (verifySfinOrgCode(mainDto.getSfinorgcode(), vDto.getSorgcode(),mainDto
					.getStrecode())) {
				returnList.add(vDto);
				// 更新凭证状态
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), null, true);
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);

			}
		}
		return returnList;
	}

	/**
	 * 申请退款凭证回单
	 * 
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	@SuppressWarnings( { "static-access", "unchecked" })
	public List<TvVoucherinfoDto> verifyFor2302(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List<TvVoucherinfoDto> returnList = new ArrayList<TvVoucherinfoDto>();
		for (List list : (List<List>) lists) {
			TvPayreckBankBackDto mainDto = (TvPayreckBankBackDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// 预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// 预算单位代码list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);

			// 校验
			if (verifyTreasury(mainDto.getStrecode(), mainDto.getSbookorgcode())
					&& verifySfinOrgCode(mainDto.getSfinorgcode(), vDto.getSorgcode(),mainDto
							.getStrecode())
					&& verifySubject(mainDto.getSbookorgcode(),
							expFuncCodeList, vtCode)
					&& verifyCorpcodeList(mainDto.getSbookorgcode(), tdcorpList)
					&& verifySpayInfo(mainDto)// 校验收付款人信息验证 根据 收款人开户行行号 找收付款人信息
					&& verifyPaybankName(
							mainDto.getSbookorgcode(),
							mainDto.getStrecode(),
							mainDto.getSagentbnkcode(),
							"201053100013".equals(ITFECommonConstant.SRC_NODE) ? mainDto
									.getSagentacctbankname()
									: mainDto.getSpaybankname())// 校验代理银行行名201053100013的山东节点校验名称字段和其它名称字段不同
			) {
				// 如果退款类型为正常退款需要校验2301是否存在，负凭证 不校验2301是否存在
				boolean checkflag = true;
				if (ITFECommonConstant.PUBLICPARAM.indexOf("verify2302")>0 && StateConstant.TKLX_1.equals(mainDto.getShold1())) {
					// 校验划款申请原凭证信息是否存在
					List<IDto> payreckList = verifyVoucherExists(mainDto,
							vtCode);
					if (null != payreckList && payreckList.size() > 0) {
						returnList.add(vDto);
						checkflag = true;
					} else {
						checkflag = false;
					}
				} else {
					returnList.add(vDto);
				}
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), checkflag ? null
								: this.tmpFailReason, checkflag);
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return returnList;
	}

	/**
	 * 申请划款凭证回单
	 * 
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 */
	@SuppressWarnings( { "static-access", "unchecked" })
	public List<TvVoucherinfoDto> verifyFor2301(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List<TvVoucherinfoDto> returnList = new ArrayList<TvVoucherinfoDto>();
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			TvPayreckBankDto mainDto = (TvPayreckBankDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);

			// 预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// 预算单位代码list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
			// 校验
			if (verifyInfoAndPay(vDto)&&verifyTreasury(mainDto.getStrecode(), mainDto.getSbookorgcode())
					&& verifySfinOrgCode(mainDto.getSfinorgcode(), vDto.getSorgcode(),mainDto
							.getStrecode())
					&& verifySubject(mainDto.getSbookorgcode(),
							expFuncCodeList, vtCode)
					&& verifyCorpcodeList(mainDto.getSbookorgcode(), tdcorpList)
					&& verifySpayInfo(mainDto)// 校验收付款人信息验证 根据 收款人开户行行号 找收付款人信息
					&& verifyPaybankName(
							mainDto.getSbookorgcode(),
							mainDto.getStrecode(),
							mainDto.getSpayeeopbkno(),
							"201053100013".equals(ITFECommonConstant.SRC_NODE) ? mainDto
									.getSagentacctbankname()
									: mainDto.getSpaybankname())// 校验代理银行行名201053100013的山东节点校验名称字段和其它名称字段不同
			) {
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					vDto.setShold3(mainDto.getSvouno());
					// 更新状态为"校验中"
					VoucherUtil.voucherVerifyUpdateStatus(vDto,
							MsgConstant.VOUCHER_NO_5106);
					succList.add(list);
				} else {
					// 授权支付2301索引表更新状态为"校验成功"
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
					count++;
					succList.add(new Integer(count));
				}
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return succList;
	}

	/**
	 * 财政直接支付凭证5201
	 * 
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 * @throws Exception
	 */

	@SuppressWarnings( { "static-access", "unchecked" })
	public List verifyFor5201(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			TfDirectpaymsgmainDto mainDto = (TfDirectpaymsgmainDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// 功能科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// 预算单位代码list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
			// 校验
			if (verifySubject(mainDto.getSorgcode(), expFuncCodeList, vtCode)
					&& verifyCorpcodeList(mainDto.getSorgcode(), tdcorpList)
					&& (mainDto.getSbusinesstypecode().equals(
							StateConstant.BIZTYPE_CODE_BATCH) ? (verifySpayInfo(mainDto) && verifyPaybankName(
							mainDto.getSorgcode(), mainDto.getStrecode(),
							mainDto.getSpayeeacctbankno(), mainDto
									.getSpayeeacctbankname()))
							: verifyPayeeBankNo(mainDto.getSpayeeacctbankno(),
									vtCode))) {
				// 批量业务需要比对凭证
				// if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
				// && mainDto.getSbusinesstypecode().equals(
				// StateConstant.BIZTYPE_CODE_BATCH)) {
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					// 批量业务直接更新状态为"校验中"
					VoucherUtil.voucherVerifyUpdateStatus(vDto,
							MsgConstant.VOUCHER_NO_5108);
				} else {
					// 单笔业务直接更新状态为"校验成功"
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
				}
				succList.add(list);
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return succList;
	}

	/**
	 * 直接支付调整凭证5253
	 * 
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	public List verifyFor5253(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			TfDirectpayAdjustmainDto mainDto = (TfDirectpayAdjustmainDto) list
					.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// 功能科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// 预算单位代码list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
			// 校验
			if (verifyCorpcodeList(mainDto.getSorgcode(), tdcorpList)
					&& verifySubject(mainDto.getSorgcode(), expFuncCodeList,
							vtCode)
					&& verifyPayeeBankNo(mainDto.getSpayeeacctbankno(), vtCode)// 代理银行校验
			) {
				if ((ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0)) {
					VoucherUtil.voucherVerifyUpdateStatus(vDto,
							MsgConstant.VOUCHER_NO_5108);
					succList.add(list);
				} else {
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
				}
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return succList;
	}

	/**
	 * 收款银行退款通知凭证2252
	 * 
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "static-access", "unchecked" })
	public int verifyFor2252(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		int count = 0;
		for (List list : (List<List>) lists) {
			TfPaybankRefundmainDto mainDto = (TfPaybankRefundmainDto) list
					.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			List<TfPaybankRefundsubDto> subList = (List<TfPaybankRefundsubDto>) list
					.get(2);// 明细子信息，用于明细里的收付款人信息校验
			if (verifyPayeeBankNo(mainDto.getSpaysndbnkno(), vtCode)
					&& verifyOriVoucher(mainDto, subList)) {
				// 更新凭证状态
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), null, true);
				count++;
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return count;
	}

	/**
	 * 实拨资金校验类
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public List verifyFor5207(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		int count = 0;
		List succList = new ArrayList();
		for (List list : (List<List>) lists) {
			TvPayoutmsgmainDto mainDto = (TvPayoutmsgmainDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// 预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			List<TvPayoutmsgsubDto> subdtos = list.size()>=4?(List<TvPayoutmsgsubDto>)list.get(3):null;
			// 校验
			if (verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())
					&& verifySfinOrgCode(mainDto.getSpayunit(), mainDto.getSorgcode(),mainDto.getStrecode())
					&& verifyAccName(mainDto.getSorgcode(), mainDto
							.getStrecode(), mainDto.getSpayeracct())
					&& verifySubject(mainDto.getSorgcode(), expFuncCodeList,
							vtCode)
					&& verifyCorpcodeList(mainDto.getSorgcode(), mainDto
							.getStrecode(), mainDto.getSbudgetunitcode(),
							vtCode)
					&& verifyPayeeBankNo(mainDto.getSrecbankno(), vtCode,mainDto.getStrecode(),subdtos)
					&& verifyPayOutMoveFunSubject(mainDto, expFuncCodeList)) {
				/**
				 * 实拨资金5207与批量明细8207比对校验(上海)
				 * 
				 * @author 张会斌
				 */
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
						&& mainDto.getSbusinesstypecode().equals(
								StateConstant.BIZTYPE_CODE_BATCH)) {
					// 业务要素全部校验成功后更新状态为"校验中"
					VoucherUtil.voucherVerifyUpdateStatus(vDto,
							MsgConstant.VOUCHER_NO_8207);
					succList.add(list);
				} else {
					// 更新凭证状态为"校验成功"
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
					//批量拨款由于主单行号可能为空，不需要补录，直接改为复核成功 20190507
					HashMap  tremap = SrvCacheFacade.cacheTreasuryInfo(null);
					if (tremap.containsKey(vDto.getStrecode())) {
						TsTreasuryDto t = (TsTreasuryDto) tremap.get(vDto.getStrecode());
						if (StateConstant.COMMON_YES.equals(t.getSpayunitname()) ){
							vDto.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
							vDto.setSdemo("复核成功");
					        DatabaseFacade.getDb().update(vDto);
						}
					}
					if (ITFECommonConstant.PUBLICPARAM
							.indexOf(",verify=false,") >= 0) {
						if (StateConstant.CHECKSTATUS_4.equals(mainDto
								.getScheckstatus())) {
							vDto
									.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
							vDto.setSdemo("复核成功");
							DatabaseFacade.getDb().update(vDto);
						}
					}
					count++;
					succList.add(new Integer(count));
				}
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return succList;
	}

	/**
	 * 直接支付额度业务校验类
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public List<TvVoucherinfoDto> verifyFor5108(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			TvDirectpaymsgmainDto mainDto = (TvDirectpaymsgmainDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// 预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
			// 校验
			if (verifyInfoAndPay(vDto)&&verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())
					&& verifySfinOrgCode((ITFECommonConstant.PUBLICPARAM
							.indexOf(",sh,") < 0 ? mainDto.getSpayunit()
							: mainDto.getShold2()), mainDto.getSorgcode(),vDto.getStrecode())
					&& verifyCorpcodeList(mainDto.getSorgcode(), tdcorpList)
					&& verifySubject(mainDto.getSorgcode(), expFuncCodeList,
							vtCode)
					&& verifyPayeeBankNo(mainDto.getStransactunit(), vtCode)// 代理银行校验
					&&(ITFECommonConstant.PUBLICPARAM.contains(",quotapayinfo,")?verifySpayInfo(mainDto):true)
					&&(ITFECommonConstant.PUBLICPARAM.contains(",quotapaycodeverify,")?verifyPaybankNo(vDto.getSorgcode(),vDto.getStrecode(),mainDto.getSpaybankno(),mainDto.getSpaybankname()):true)
			) {
				/**
				 * 1、金额=0.00 直接支付额度5108与直接支付调整5253比对校验(上海) 2、金额<>0.00
				 * 直接支付额度5108与直接支付业务5201比对校验，
				 * 然后直接支付5201与批量明细8207比对校验，比对结果返回更新5108(上海)
				 */
				// if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
				// && mainDto.getShold1().equals("1")
				// && (mainDto.getNmoney().compareTo(BigDecimal.ZERO) >= 0)) {
				// // 5108负额度不需要勾兑5201
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
						&& (mainDto.getNmoney().compareTo(BigDecimal.ZERO) >= 0)) { // 5108负额度不需要勾兑5201
					// 业务要素全部校验成功后更新状态为"校验中"
					vDto.setShold3(mainDto.getSpayvoucherno());
					if (vDto.getNmoney().compareTo(BigDecimal.ZERO) == 0)
						VoucherUtil.voucherVerifyUpdateStatus(vDto,
								MsgConstant.VOUCHER_NO_5253);
					else
						VoucherUtil.voucherVerifyUpdateStatus(vDto,
								MsgConstant.VOUCHER_NO_5201);
					succList.add(list);
				} else {
					// 更新凭证状态为"校验成功"
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
					count++;
					succList.add(new Integer(count));
				}

			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), tmpFailReason, false);
			}
		}
		return succList;
	}

	/**
	 * 授权支付额度业务校验类
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public List<TvVoucherinfoDto> verifyFor5106(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List<TvVoucherinfoDto> returnList = new ArrayList<TvVoucherinfoDto>();
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			// 授权支付额度主表
			IDto idto = (IDto) list.get(0);
			TvGrantpaymsgmainDto mainDto = (TvGrantpaymsgmainDto) idto;
			// 索引表
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// 预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// 预算单位代码列表
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
			// 校验
			if (verifyInfoAndPay(vDto)&&verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())
					&& verifySfinOrgCode(mainDto.getSpayunit(), mainDto
							.getSorgcode(),vDto.getStrecode())
					&& verifyCorpcodeList(mainDto.getSorgcode(), tdcorpList)
					&& verifySubject(mainDto.getSorgcode(), expFuncCodeList,
							vtCode)
					&& verifyPayeeBankNo(mainDto.getStransactunit(), vtCode)// 代理银行校验
					&&(ITFECommonConstant.PUBLICPARAM.contains(",quotapayinfo,")?verifySpayInfo(mainDto):true)
					&&(ITFECommonConstant.PUBLICPARAM.contains(",quotapaycodeverify,")?verifyPaybankNo(vDto.getSorgcode(),vDto.getStrecode(),mainDto.getSpaybankno(),mainDto.getSpaybankname()):true)
			) {
				/**
				 * 授权支付额度与授权支付调整额度5351和授权支付业务2301比对校验(上海)
				 * 
				 * @author 张会斌
				 */
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
						&& mainDto.getNmoney().compareTo(BigDecimal.ZERO) >= 0) {
					if (mainDto.getNmoney().compareTo(BigDecimal.ZERO) == 0) {
						vDto.setShold3(mainDto.getSclearvoucherno());// 5106主单.ClearVoucherNo=5351主单.VoucherNo
						// 如果与授权调整零额度信息5351校验，则授权支付额度索引表更新状态为"校验中"
						VoucherUtil.voucherVerifyUpdateStatus(vDto,
								MsgConstant.VOUCHER_NO_5351);
					} else {
						vDto.setShold3(mainDto.getSclearvoucherno());// 5106主单.ClearVoucherNo=2301主单.VoucherNo
						// 如果与授权支付信息2301校验，则更新授权支付额度索引表状态为"校验中"
						VoucherUtil.voucherVerifyUpdateStatus(vDto,
								MsgConstant.VOUCHER_NO_2301);
					}
					succList.add(list);
				} else {
					// 更新凭证状态为"校验成功"
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
					count++;
					succList.add(new Integer(count));
				}
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return succList;
	}

	/**
	 * 授权支付调整业务校验类
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public List<TvVoucherinfoDto> verifyFor5351(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		List succList = new ArrayList();
		for (List list : (List<List>) lists) {
			IDto idto = (IDto) list.get(0);
			TfGrantpayAdjustmainDto mainDto = (TfGrantpayAdjustmainDto) idto;
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// 预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// 预算单位代码list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);

			// 校验
			if (verifyCorpcodeList(mainDto.getSorgcode(), tdcorpList)
					&& verifySubject(mainDto.getSorgcode(), expFuncCodeList,
							vtCode)
					&& verifyPayeeBankNo(mainDto.getSpaybankcode(), vtCode)// 代理银行校验
			) {
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					VoucherUtil.voucherVerifyUpdateStatus(vDto,
							MsgConstant.VOUCHER_NO_5106);
					succList.add(list);
				} else {
					VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
				}
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);

			}
		}
		return succList;
	}

	/**
	 * 收入退付校验类
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public int verifyFor5209(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		int count = 0;
		for (List list : (List<List>) lists) {
			TvDwbkDto mainDto = (TvDwbkDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// 预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);

			// 校验
			if (verifyTreasury(mainDto.getSaimtrecode(), mainDto
					.getSbookorgcode())
					&& verifySfinOrgCode(mainDto.getStaxorgcode(), mainDto
							.getSbookorgcode(),vDto.getStrecode())
					&& verifySubject(mainDto.getSbookorgcode(),
							expFuncCodeList, vtCode)
					&& verifyPayeeBankNo(mainDto.getSpayeeopnbnkno(), vtCode)
					&& verifyStrelevel(mainDto)) {

				// 更新凭证状态
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), null, true);
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",verify=false,") >= 0) {
					if (StateConstant.CHECKSTATUS_4.equals(mainDto
							.getScheckstatus())) {
						vDto
								.setSstatus(DealCodeConstants.VOUCHER_CHECK_SUCCESS);
						vDto.setSdemo("复核成功");
						DatabaseFacade.getDb().update(vDto);
					}
				}
				count++;
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);

			}

		}
		return count;
	}

	/**
	 * 清算信息对账校验类
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public int verifyFor3507(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		int count = 0;
		for (List list : (List<List>) lists) {
			TfReconcilePayinfoMainDto mainDto = (TfReconcilePayinfoMainDto) list
					.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// 预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			// 预算单位代码list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
			// 校验
			if (verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())
					&& verifyCorpcodeList(mainDto.getSorgcode(), tdcorpList)
					&& verifySubject(mainDto.getSorgcode(), expFuncCodeList,
							vtCode)) {
				// 更新凭证状态
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), null, true);
				count++;
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return count;
	}

	/**
	 * 实拨信息对账校验类
	 * 
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	@SuppressWarnings( { "unchecked", "static-access" })
	public int verifyFor3508(List lists, String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException,
			Exception {
		int count = 0;
		for (List list : (List<List>) lists) {
			TfReconcilePayinfoMainDto mainDto = (TfReconcilePayinfoMainDto) list
					.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);
			// 校验
			if (verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())) {
				// 更新凭证状态
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), null, true);
				count++;
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return count;
	}

	/**
	 * 校验收付款人信息验证 根据 收款人开户行行号 找收付款人信息
	 * 
	 * @param mainDto
	 * @return
	 * @throws ITFEBizException
	 */
	private boolean verifySpayInfo(IDto dto) throws ITFEBizException {
		if (dto instanceof TvPayreckBankDto) {
			TvPayreckBankDto mainDto = (TvPayreckBankDto) dto;
			// 用于单一账户的收付款人的校验
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(mainDto.getSbookorgcode()); // 获取收付款人信息
			if (StringUtils.isBlank(mainDto.getSpayeeopbkno())) {
				this.tmpFailReason = "报文不规范, 没有找到收款人开户行行号信息!";
				return false;
			} else {
				TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap
						.get((mainDto.getStrecode() + mainDto.getSpayeeopbkno()
								+ mainDto.getSpayeracct() + mainDto
								.getSpayeeacct()).trim());
				if (null == tmppayacctinfoDto) {
					this.tmpFailReason = "根据国库代码：" + mainDto.getStrecode()
							+ ", 收款人开户行行号：" + mainDto.getSpayeeopbkno()
							+ ", 付款人账号：" + mainDto.getSpayeracct() + ", 收款人账号："
							+ mainDto.getSpayeeacct() + " 没有找到收付款人信息!";
					return false;
				} else {
					if (!mainDto.getSpayeracct().equals(
							tmppayacctinfoDto.getSpayeracct())) { // 付款人帐户
						this.tmpFailReason = "付款人帐户:" + mainDto.getSpayeracct()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
					if (!mainDto.getSpayername().equals(
							tmppayacctinfoDto.getSpayername())) { // 付款人名称
						this.tmpFailReason = "付款人名称:" + mainDto.getSpayername()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
					if (!mainDto.getSpayeeacct().equals(
							tmppayacctinfoDto.getSpayeeacct())) { // 收款人账户
						this.tmpFailReason = "收款人账户:" + mainDto.getSpayeeacct()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
					if (!mainDto.getSpayeename().equals(
							tmppayacctinfoDto.getSpayeename())) { // 收款人名称
						this.tmpFailReason = "收款人名称:" + mainDto.getSpayeename()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
				}
			}
		} else if (dto instanceof TvPayreckBankBackDto) {
			TvPayreckBankBackDto mainDto = (TvPayreckBankBackDto) dto;
			// 用于单一账户的收付款人的校验
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(mainDto.getSbookorgcode()); // 获取收付款人信息
			if (StringUtils.isBlank(mainDto.getSpaysndbnkno())) {
				this.tmpFailReason = "报文不规范, 没有找到收款人开户行行号信息";
				return false;
			} else {
				TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap
						.get((mainDto.getStrecode() + mainDto.getSpaysndbnkno()
								+ mainDto.getSpayeracct() + mainDto
								.getSpayeeacct()).trim());
				if (null == tmppayacctinfoDto) {
					this.tmpFailReason = "根据国库代码：" + mainDto.getStrecode()
							+ ", 收款人开户行行号：" + mainDto.getSpaysndbnkno()
							+ ", 付款人账号：" + mainDto.getSpayeracct() + ", 收款人账号："
							+ mainDto.getSpayeeacct() + " 没有找到收付款人信息!";
					return false;
				} else {
					if (!mainDto.getSpayeracct().equals(
							tmppayacctinfoDto.getSpayeracct())) { // 付款人帐户
						this.tmpFailReason = "付款人帐户:" + mainDto.getSpayeracct()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
					if (!mainDto.getSpayername().equals(
							tmppayacctinfoDto.getSpayername())) { // 付款人名称
						this.tmpFailReason = "付款人名称:" + mainDto.getSpayername()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
					if (!mainDto.getSpayeeacct().equals(
							tmppayacctinfoDto.getSpayeeacct())) { // 收款人账户
						this.tmpFailReason = "收款人账户:" + mainDto.getSpayeeacct()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
					if (!mainDto.getSpayeename().equals(
							tmppayacctinfoDto.getSpayeename())) { // 收款人名称
						this.tmpFailReason = "收款人名称:" + mainDto.getSpayeename()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
				}
			}
		} else if (dto instanceof TfDirectpaymsgmainDto) {
			TfDirectpaymsgmainDto mainDto = (TfDirectpaymsgmainDto) dto;
			// 用于单一账户的收付款人的校验
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(mainDto.getSorgcode()); // 获取收付款人信息
			if (StringUtils.isBlank(mainDto.getSpayeeacctbankno())) {
				this.tmpFailReason = "报文不规范, 没有找到收款人开户行行号信息";
				return false;
			} else {
				TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap
						.get((mainDto.getStrecode()
								+ mainDto.getSpayeeacctbankno()
								+ mainDto.getSpayacctno() + mainDto
								.getSpayeeacctno()));
				if (null == tmppayacctinfoDto) {
					this.tmpFailReason = "根据国库代码：" + mainDto.getStrecode()
							+ ", 收款人开户行行号：" + mainDto.getSpayeeacctbankno()
							+ ", 付款人账号：" + mainDto.getSpayacctno() + ", 收款人账号："
							+ mainDto.getSpayeeacctno() + " 没有找到收付款人信息!";
					return false;
				} else {
					if (!mainDto.getSpayacctno().equals(
							tmppayacctinfoDto.getSpayeracct())) { // 付款人帐户
						this.tmpFailReason = "付款人帐户:" + mainDto.getSpayacctno()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
					if (!mainDto.getSpayacctname().equals(
							tmppayacctinfoDto.getSpayername())) { // 付款人名称
						this.tmpFailReason = "付款人名称:"
								+ mainDto.getSpayacctname() + "与维护的收付款人信息中不一致!";
						return false;
					}
					if (!mainDto.getSpayeeacctno().equals(
							tmppayacctinfoDto.getSpayeeacct())) { // 收款人账户
						this.tmpFailReason = "收款人账户:"
								+ mainDto.getSpayeeacctno() + "与维护的收付款人信息中不一致!";
						return false;
					}
					if (!mainDto.getSpayeeacctname().equals(
							tmppayacctinfoDto.getSpayeename())) { // 收款人名称
						this.tmpFailReason = "收款人名称:"
								+ mainDto.getSpayeeacctname()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
				}
			}
		}else if (dto instanceof TvGrantpaymsgmainDto) {
			TvGrantpaymsgmainDto mainDto = (TvGrantpaymsgmainDto) dto;
			if(StringUtils.isBlank(mainDto.getSpaybankno())||StringUtils.isBlank(mainDto.getShold1())||StringUtils.isBlank(mainDto.getSclearbankcode()))
				return true;
			// 用于单一账户的收付款人的校验
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(mainDto.getSorgcode()); // 获取收付款人信息
			if (StringUtils.isBlank(mainDto.getSpaybankno())) {
				this.tmpFailReason = "报文不规范, 没有找到收款人开户行行号信息!";
				return false;
			} else {
				TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap
						.get((mainDto.getStrecode() + mainDto.getSpaybankno()
								+ mainDto.getSclearbankcode() + mainDto
								.getShold1()).trim());
				if (null == tmppayacctinfoDto) {
					this.tmpFailReason = "根据国库代码：" + mainDto.getStrecode()
							+ ", 收款人开户行行号：" + mainDto.getSpaybankno()
							+ ", 付款人账号：" + mainDto.getSclearbankcode() + ", 收款人账号："
							+ mainDto.getShold1() + " 没有找到收付款人信息!";
					return false;
				} else {
					if (!mainDto.getSclearbankcode().equals(
							tmppayacctinfoDto.getSpayeracct())) { // 付款人帐户
						this.tmpFailReason = "付款人帐户:" + mainDto.getSclearbankcode()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
					if (!mainDto.getSclearbankname().equals(
							tmppayacctinfoDto.getSpayername())) { // 付款人名称
						this.tmpFailReason = "付款人名称:" + mainDto.getSclearbankname()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
					if (!mainDto.getShold1().equals(
							tmppayacctinfoDto.getSpayeeacct())) { // 收款人账户
						this.tmpFailReason = "收款人账户:" + mainDto.getShold1()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
					if (!mainDto.getShold2().equals(
							tmppayacctinfoDto.getSpayeename())) { // 收款人名称
						this.tmpFailReason = "收款人名称:" + mainDto.getShold2()
								+ "与维护的收付款人信息中不一致!";
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 校验授权划款凭证明细是否存在
	 * 
	 * @param mainIDto
	 * @param vtCode
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List<IDto> verifyVoucherExists(IDto mainIDto, String vtCode)
			throws JAFDatabaseException, ValidateException {
		if (vtCode.equals(MsgConstant.VOUCHER_NO_2302)) {
			TvPayreckBankBackDto mainDto = (TvPayreckBankBackDto) mainIDto;
			TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
			subdto.setIvousrlno(mainDto.getIvousrlno());
			List<IDto> PayBankBackList = CommonFacade.getODB().findRsByDto(
					subdto);
			StringBuffer sbf = new StringBuffer("");
			if (null != PayBankBackList && PayBankBackList.size() > 0) {
				TvPayreckBankBackListDto backListDto = null;
				String sql = "SELECT * FROM TV_PAYRECK_BANK_LIST a WHERE I_VOUSRLNO =(SELECT I_VOUSRLNO FROM TV_PAYRECK_BANK WHERE S_BOOKORGCODE = ?  AND S_TRECODE = ? AND S_VOUNO =  ? AND S_RESULT = ?) AND S_ID = ? ";
				SQLExecutor sqlExecutor = DatabaseFacade.getODB()
						.getSqlExecutorFactory().getSQLExecutor();
				SQLResults sqlResults = null;
				TvPayreckBankListDto tmpSubDto = null;
				List updateSubList = new ArrayList();
				for (IDto dto : PayBankBackList) {
					backListDto = (TvPayreckBankBackListDto) dto;
					sqlExecutor.addParam(mainDto.getSbookorgcode());
					sqlExecutor.addParam(mainDto.getStrecode());
					sqlExecutor.addParam(backListDto.getSorivouno());
					sqlExecutor.addParam("80000");
					sqlExecutor.addParam(backListDto.getSorivoudetailno());
					// TvPayreckBankDto PayreckBankDto = new TvPayreckBankDto();
					// PayreckBankDto.setSbookorgcode(mainDto.getSbookorgcode());
					// PayreckBankDto.setStrecode(mainDto.getStrecode());
					// PayreckBankDto.setSvouno(backListDto.getSorivouno());
					// List<TvPayreckBankDto> PayreckBankDtoList = CommonFacade
					// .getODB().findRsByDto(PayreckBankDto);
					sqlResults = sqlExecutor.runQuery(sql,
							TvPayreckBankListDto.class);
					if (null == sqlResults || sqlResults.getRowCount() == 0) {
						this.tmpFailReason = "根据划款申请退款中的原凭证单号["
								+ backListDto.getSorivouno() + "]未能找到原划款申请凭证!";
						return null;
					}
					tmpSubDto = (TvPayreckBankListDto) ((List) sqlResults
							.getDtoCollection()).get(0);
					if (StringUtils.isNotBlank(tmpSubDto.getShold1())) {
						this.tmpFailReason = "凭证编号为：" + mainDto.getSvouno() +  "中,明细ID：" + backListDto.getSid() + "不允许重复退款！";
						return null;
					}
					if (backListDto.getFamt().compareTo(tmpSubDto.getFamt()) != 0) {
						this.tmpFailReason = "凭证编号为：" + mainDto.getSvouno() +  "中,明细ID：" + backListDto.getSid() + "退款金额：" + backListDto.getFamt() +"与原划款凭证金额:" + tmpSubDto.getFamt() + "不符！";
						return null;
					}
					tmpSubDto.setShold1("1"); // 代表该笔明细已经退款，不允许重复退
					updateSubList.add(tmpSubDto);
					sqlExecutor.clearParams();
				}
				DatabaseFacade.getODB().update(CommonUtil.listTArray(updateSubList));
				return PayBankBackList;
			}
		}
		return null;

	}

	/**
	 * 提供对所属国库进行验证
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyTreasury(String trecode, String orgcode)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		HashMap<String, TsTreasuryDto> map = SrvCacheFacade
				.cacheTreasuryInfo(orgcode);
		HashMap<String, TsTreasuryDto> newMap = new HashMap<String, TsTreasuryDto>();

		Set<Map.Entry<String, TsTreasuryDto>> set = map.entrySet();
		if(orgcode!=null&&!orgcode.equals("")){
			for (Iterator<Map.Entry<String, TsTreasuryDto>> it = set.iterator(); it.hasNext();) {
				Map.Entry<String, TsTreasuryDto> entry = (Map.Entry<String, TsTreasuryDto>) it.next();
				newMap.put(entry.getKey() + entry.getValue().getSorgcode(), entry.getValue());
			}
		}
		if (newMap != null && newMap.containsKey(trecode + orgcode)) {
			return true;
		} else {
			this.tmpFailReason = "国库主体代码[" + trecode + "]在国库主体信息参数中不存在!";
			return false;
		}
	}

	/**
	 * 校验国库预算级次
	 * 
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyStrelevel(TvDwbkDto dto) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		if (ITFECommonConstant.SRC_NODE.equals("000057400006")||ITFECommonConstant.SRC_NODE.equals("201057100006")) {// 如果为宁波不需要校验
			return true;
		}
		HashMap<String, TsTreasuryDto> map = SrvCacheFacade
				.cacheTreasuryInfo(dto.getSbookorgcode());
		TsTreasuryDto tDto = map.get(dto.getSaimtrecode());
		if (tDto.getStrelevel() == null || tDto.getStrelevel().equals("")) {
			this.tmpFailReason = "国库[" + tDto.getStrecode() + "]预算级次未维护！";
			return false;
		}
		dto.setCbdglevel(tDto.getStrelevel());
		DatabaseFacade.getODB().update(dto);
		return true;

	}

	/**
	 * 校验功能科目代码
	 * 
	 * @param orgcode
	 *            机构代码
	 * @param funccode
	 *            功能科目列表
	 * @param vtCode
	 *            业务类型
	 * 
	 * @return ture/false
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	public boolean verifySubject(String orgcode, List<String> funccodeList,
			String vtcode) throws ITFEBizException, JAFDatabaseException {
		Map<String, TsBudgetsubjectDto> smap = SrvCacheFacade
				.cacheTsBdgsbtInfo(orgcode);
		for (String funccode : funccodeList) {
			TsBudgetsubjectDto dto = smap.get(funccode);
			if (null == funccode || "".equals(funccode)) {
				this.tmpFailReason = "ITFE_001明细信息中存在功能科目代码为空的记录!";
				return false;
			}
			if (null == dto || "".equals(dto.getSsubjectcode())) {
				this.tmpFailReason = "ITFE_001功能科目代码[" + funccode + "]不存在!";
				return false;
			} else {
				if(vtcode.equals(MsgConstant.VOUCHER_NO_5407))
					return true;
				if (vtcode.equals(MsgConstant.VOUCHER_NO_5209) || vtcode.equals(MsgConstant.VOUCHER_NO_5671)|| vtcode.equals(MsgConstant.VOUCHER_NO_5408)) {
					if (!"1".equals(dto.getSsubjectclass())) {
						this.tmpFailReason = "ITFE_001功能科目代码[" + funccode + "]不是收入科目!";
						return false;
					}
				} else {
					if (!"2".equals(dto.getSsubjectclass())) {
						this.tmpFailReason = "ITFE_001功能科目代码[" + funccode
								+ "]不是支出功能科目!";
						return false;
					}
				}
				if (!"1".equals(dto.getSwriteflag())) {
					this.tmpFailReason = "ITFE_001功能科目代码[" + funccode + "]的录入标志为不可录入!";
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 校验实拨资金科目代码是否是调拨科目
	 * 
	 * @param funccode
	 *            功能科目列表
	 * @param maindto
	 *            实拨资金主表dto
	 * 
	 * @return ture/false
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	public boolean verifyPayOutMoveFunSubject(TvPayoutmsgmainDto mainDto,
			List<String> funccodeList) throws ITFEBizException,
			JAFDatabaseException {
		Map<String, TsBudgetsubjectDto> smap = SrvCacheFacade
				.cacheTsBdgsbtInfo(mainDto.getSorgcode());
		String ischeck = ITFECommonConstant.CHECKPAYOUTSUBJECT;
		if (ischeck != null
				&& ischeck.equals(MsgConstant.VOUCHER_CHECKPAYOUTSUBJECT)
				&& mainDto != null && mainDto.getSrecbankno().startsWith("011")) {
			for (String funccode : funccodeList) {
				TsBudgetsubjectDto dto = smap.get(funccode);
				if (null == funccode || "".equals(funccode)) {
					this.tmpFailReason = "ITFE_001明细信息中存在功能科目代码为空的记录!";
					return false;
				}
				if (null == dto || "".equals(dto.getSsubjectcode())) {
					this.tmpFailReason = "ITFE_001功能科目代码[" + funccode + "]不存在!";
					return false;
				} else {
					if (MsgConstant.MOVE_FUND_SIGN_NO
							.equals(dto.getSmoveflag())) {
						this.tmpFailReason = "ITFE_001功能科目代码[" + funccode + "]为非调拨!";
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 进行预算单位代码列表校验
	 * 
	 * @param orgcode
	 *            机构代码
	 * @param tdCorpList
	 *            预算单位代码列表
	 * @param vtCode
	 *            业务类型
	 * 
	 * @return true/false
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyCorpcodeList(String orgcode, String trecode,
			String tdCorp, String vtCode) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		Map<String, TdCorpDto> map = SrvCacheFacade.cacheTdCorpInfo(orgcode);
		if (map == null) {
			this.tmpFailReason = "机构[" + orgcode + "]没有维护预算单位代码参数!";
			return false;
		}
		if ("".equals(tdCorp) || tdCorp == null) {
			this.tmpFailReason = "预算单位代码为空!";
			return false;
		}
		if (vtCode.equals(MsgConstant.VOUCHER_NO_5207)) {
			TdCorpDto cDto = map.get(trecode + tdCorp);
			if (cDto == null || !cDto.getScorpcode().equals(tdCorp)) {
				this.tmpFailReason = "预算单位代码[" + tdCorp + "]在预算单位代码参数中不存在!";
				return false;
			} else {
				if (!"1".equals(cDto.getCmayaprtfund().trim())) {
					this.tmpFailReason = "预算单位代码[" + tdCorp + "]不能进行实拨资金!";
					return false;
				}
			}

		} else {
			if (!map.containsKey(trecode + tdCorp)) {
				this.tmpFailReason = "预算单位代码[" + tdCorp + "]在预算单位代码参数中不存在!";
				return false;
			}

		}
		return true;
	}

	/**
	 * 校验付款人账号
	 * 
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */

	public boolean verifyAccName(String bookorgCode, String strecode,
			String spayeraccount) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		Map<String, TsInfoconnorgaccDto> map = SrvCacheFacade
				.cacheFinTreAcctInfo();
		if (map.get(bookorgCode + spayeraccount) == null) {
			this.tmpFailReason = "付款人账号[" + spayeraccount
					+ "]没有在'财政库款账户参数维护中维护!";
			return false;
		}
		return true;
	}

	/**
	 * 校验收款人开户行
	 * 
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyPayeeBankNo(String PayeeAcctBankNo, String vtcode)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_5209.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)) {
			if ("".equals(PayeeAcctBankNo) || PayeeAcctBankNo == null) {// 财政没有填写行号，不进行校验
				return true;
			}
		} else {
			if ("".equals(PayeeAcctBankNo) || PayeeAcctBankNo == null) {
				this.tmpFailReason = "收款银行代码为空";
				return false;
			}
		}
		HashMap<String, TsPaybankDto> bankmap = SrvCacheFacade
				.cachePayBankInfo();
		if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
				&& PayeeAcctBankNo.trim().length() == 10) { // 上海如果为10国库代码不进行校验
			return true;
		} else if (bankmap.get(PayeeAcctBankNo) == null) {
			this.tmpFailReason = "收款行号[" + PayeeAcctBankNo + "]在支付系统行号中不存在!";
			return false;
		}
		return true;
	}
	/**
	 * 校验收款人开户行
	 * 
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyPayeeBankNo(String PayeeAcctBankNo, String vtcode,String strecode,List<TvPayoutmsgsubDto> subdtos)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		HashMap<String, TsPaybankDto> bankmap = SrvCacheFacade
				.cachePayBankInfo();
		if (strecode != null && !"".equals(strecode) && subdtos != null
				&& subdtos.size() > 0) {
			TsTreasuryDto tredto = SrvCacheFacade.cacheTreasuryInfo(null).get(strecode);
			if (StateConstant.COMMON_YES.equals(String
					.valueOf(tredto == null ? "" : tredto.getSpayunitname()))) {
				for (int i = 0; i < subdtos.size(); i++) {
					if (bankmap.get(String.valueOf(subdtos.get(i)
							.getSpayeebankno())) == null) {
						this.tmpFailReason = "收款行号["
								+ String.valueOf(subdtos.get(i)
										.getSpayeebankno()) + "]在支付系统行号中不存在!";
						return false;
					}
				}
			} else {
				if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
						|| MsgConstant.VOUCHER_NO_5209.equals(vtcode)
						|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)) {
					if ("".equals(PayeeAcctBankNo) || PayeeAcctBankNo == null) {// 财政没有填写行号，不进行校验
						return true;
					} else {
						TsPaybankDto paybankdto = bankmap.get(PayeeAcctBankNo);
						if (paybankdto == null) {
							this.tmpFailReason = PayeeAcctBankNo + " 支付系统无此行号";
							return false;
						}
					}
				} else {
					if ("".equals(PayeeAcctBankNo) || PayeeAcctBankNo == null) {
						this.tmpFailReason = "收款银行代码为空";
						return false;
					}
				}
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
						&& PayeeAcctBankNo.trim().length() == 10) { // 上海如果为10国库代码不进行校验
					return true;
				} else if (bankmap.get(PayeeAcctBankNo) == null) {
					this.tmpFailReason = "收款行号[" + PayeeAcctBankNo
							+ "]在支付系统行号中不存在!";
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * 校验财政机构代码
	 * 
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifySfinOrgCode(String SfinOrgCode, String sbookorgcode)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		if ("".equals(SfinOrgCode) || SfinOrgCode == null) {
			this.tmpFailReason = "报文不规范，财政机构不能为空";
			return false;
		}
		HashMap<String, TsConvertfinorgDto> bankmap = SrvCacheFacade
				.cacheFincInfoByFinc(sbookorgcode);
		if (bankmap.get(SfinOrgCode) == null) {
			this.tmpFailReason = "财政机构[" + SfinOrgCode + "]在财政机构中不存在!";
			return false;
		}
		return true;
	}
	/**
	 * 校验财政机构代码
	 * 
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifySfinOrgCode(String SfinOrgCode, String sbookorgcode,String trecode)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		if ("".equals(SfinOrgCode) || SfinOrgCode == null) {
			this.tmpFailReason = "报文不规范，财政机构不能为空";
			return false;
		}
		HashMap<String, TsConvertfinorgDto> bankmap = SrvCacheFacade
				.cacheFincInfoByFinc(sbookorgcode);
		if (bankmap.get(SfinOrgCode) == null) {
			this.tmpFailReason = "财政机构[" + SfinOrgCode + "]在财政机构中不存在!";
			return false;
		}else
		{
			TsConvertfinorgDto temp = bankmap.get(SfinOrgCode);
			if(!temp.getStrecode().equals(trecode))
			{
				this.tmpFailReason = "财政机构[" + SfinOrgCode + "]参数维护的国库代码"+temp.getStrecode()+"和凭证中的国库代码不一致!";
				return false;
			}
		}
		return true;
	}
	public String getTmpFailReason() {
		return tmpFailReason;
	}

	public void setTmpFailReason(String tmpFailReason) {
		this.tmpFailReason = tmpFailReason;
	}

	/**
	 * 进行法人代码校验
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyCorpcodeList(String orgcode, List<String> tdCorpList)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		Map<String, TdCorpDto> map = SrvCacheFacade.cacheTdCorpInfo(orgcode);
		if (map != null) {
			for (String tdCorp : tdCorpList) {
				if (!map.containsKey(tdCorp)) {
					// 去掉预算单位校验中的国库代码
					tdCorp = tdCorp.substring(10);
					this.tmpFailReason = "预算单位代码[" + tdCorp + "]在预算单位代码参数中不存在";
					return false;
				}
			}
		} else {
			this.tmpFailReason = "机构[" + orgcode + "]在法人代码参数中不存在";
			return false;
		}
		return true;
	}

	/**
	 * 代理银行名称校验（集中支付清算用）
	 */
	public boolean verifyPaybankName(String orgcode, String treCode,
			String bankno, String bankname) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		if ("".equals(bankname) || bankname == null) {
			this.tmpFailReason = "代理银行名称不能为空。";
			return false;
		}
		Map<String, TsConvertbanktypeDto> map = SrvCacheFacade
				.cacheTsconvertBankType(orgcode);
		if (map != null && !map.isEmpty()) {
			if (map.containsKey(treCode + bankno)) {
				String inputBankname = map.get(treCode + bankno).getSbankname();
				if (!bankname.equals(inputBankname)) {
					this.tmpFailReason = "代理银行名称[" + bankname
							+ "]与【代理银行信息维护】参数中不一致。";
					return false;
				}
			} else {
				this.tmpFailReason = "代理银行代码没有在【代理银行信息维护】中维护。";
				return false;
			}
		} else {
			this.tmpFailReason = "【代理银行信息维护】没有维护。";
			return false;
		}
		return true;
	}
	/**
	 * 代理银行名称校验（集中支付清算用）
	 */
	public boolean verifyPaybankNo(String orgcode, String treCode,
			String bankno, String bankname) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		if ("".equals(bankname) || bankname == null) {
			this.tmpFailReason = "代理银行名称不能为空。";
			return false;
		}
		Map<String, TsConvertbanktypeDto> map = SrvCacheFacade
				.cacheTsconvertBankType(orgcode);
		if (map != null && !map.isEmpty()) {
			if (!map.containsKey(treCode + bankno)) {
				this.tmpFailReason = "代理银行代码没有在【代理银行信息维护】中维护。";
				return false;
			}
		} else {
			this.tmpFailReason = "【代理银行信息维护】没有维护。";
			return false;
		}
		return true;
	}
	/**
	 * 校验原凭证是否存在
	 * 
	 * @author 张会斌
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	public boolean verifyOriVoucher(TfPaybankRefundmainDto mainDto,
			List<TfPaybankRefundsubDto> subList) throws ITFEBizException,
			JAFDatabaseException {
		String svtcode = "";
		// 根据支付方式编码确定何种业务 11-直接支付 12-授权支付 91-实拨资金
		if (mainDto.getSpaytypecode().equals(StateConstant.PAYOUT_PAY_CODE)) {
			svtcode = MsgConstant.VOUCHER_NO_5207;
		} else if (mainDto.getSpaytypecode().equals(MsgConstant.directPay)) {
			svtcode = MsgConstant.VOUCHER_NO_5201;
		}
		List list = VoucherUtil.findVoucherDto(mainDto.getStrecode(), svtcode,
				mainDto.getSoriginalvoucherno(),
				DealCodeConstants.VOUCHER_SUCCESS);
		if (list == null || list.size() == 0) {
			this.tmpFailReason = "根据凭证编号：" + mainDto.getSoriginalvoucherno()
					+ "未找到类型为" + svtcode + "划款凭证";
			return false;
		}
		TvVoucherinfoDto dto = (TvVoucherinfoDto) list.get(0);
		if (mainDto.getNpayamt().compareTo(dto.getNmoney()) > 0) {
			this.tmpFailReason = MsgConstant.VOUCHER_NO_2252 + "退款金额[-"
					+ mainDto.getNpayamt() + "]的绝对值不能大于" + svtcode + "凭证金额["
					+ dto.getNmoney() + "]。";
			return false;
		} else if (mainDto.getNpayamt().compareTo(dto.getNmoney()) == 0)
			mainDto.setSrefundtype("2");
		else
			mainDto.setSrefundtype("1");
		try {
			DatabaseFacade.getODB().update(mainDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("更新业务主表退款类型异常!", e);
		}
		// 需校验2252明细Id是否存在与直接支付5201对应的明细Id
		List updateSubList = new ArrayList();
		if (svtcode.equals(MsgConstant.VOUCHER_NO_5201)) {
			TfDirectpaymsgmainDto maindto5201 = (TfDirectpaymsgmainDto) VoucherUtil
					.findMainDtoByVoucher(dto).get(0);
			HashMap subdtoMap = VoucherUtil.convertListToMap(PublicSearchFacade
					.findSubDtoByMain(maindto5201));
			TfDirectpaymsgsubDto tempsudto = null;
			for (TfPaybankRefundsubDto subdto : subList) {
				tempsudto = (TfDirectpaymsgsubDto) subdtoMap.get(subdto
						.getSid());
				if (null == tempsudto) {
					this.tmpFailReason += MsgConstant.VOUCHER_NO_2252 + "明细Id："
							+ subdto.getSid() + "在" + svtcode + "明细中不存在。";
					break;
				} else if (StringUtils.isNotBlank(tempsudto.getSext3())) {
					this.tmpFailReason += MsgConstant.VOUCHER_NO_2252 + "明细Id："
							+ subdto.getSid() + "不允许重复退款";
					break;
				} else if (subdto.getNpayamt()
						.compareTo(tempsudto.getNpayamt()) != 0) {
					this.tmpFailReason += MsgConstant.VOUCHER_NO_2252 + "明细Id："
							+ subdto.getSid() + "退款金额[" + subdto.getNpayamt()
							+ "]与原划款金额[" + tempsudto.getNpayamt() + "]不符";
					break;
				}
				tempsudto.setSext3("1"); // 代表已经做过退款的记录
				updateSubList.add(tempsudto);
			}
		} else if (svtcode.equals(MsgConstant.VOUCHER_NO_5207)) {
			TvPayoutmsgmainDto tvPayoutmsgmainDto = (TvPayoutmsgmainDto) VoucherUtil
					.findMainDtoByVoucher(dto).get(0);
			HashMap subdtoMap = VoucherUtil.convertListToMap(PublicSearchFacade
					.findSubDtoByMain(tvPayoutmsgmainDto));
			TvPayoutmsgsubDto tmpSubDto = null;
			for (TfPaybankRefundsubDto subdto : subList) {
				tmpSubDto = (TvPayoutmsgsubDto) subdtoMap.get(subdto.getSid());
				if (null == tmpSubDto) {
					this.tmpFailReason += MsgConstant.VOUCHER_NO_2252 + "明细Id："
							+ subdto.getSid() + "在" + svtcode + "明细中不存在。";
					break;
				} else if (StringUtils.isNotBlank(tmpSubDto.getShold4())) {
					this.tmpFailReason += MsgConstant.VOUCHER_NO_2252 + "明细Id："
							+ subdto.getSid() + "不允许重复退款";
					break;
				} else if (subdto.getNpayamt().compareTo(tmpSubDto.getNmoney()) != 0) {
					this.tmpFailReason += MsgConstant.VOUCHER_NO_2252 + "明细Id："
							+ subdto.getSid() + "退款金额[" + subdto.getNpayamt()
							+ "]与原划款金额[" + tmpSubDto.getNmoney() + "]不符";
					break;

				}
				tmpSubDto.setShold4("1");
				updateSubList.add(tmpSubDto);
			}
		}
		if (StringUtils.isNotBlank(tmpFailReason)) {
			return false;
		}
		DatabaseFacade.getODB().update(CommonUtil.listTArray(updateSubList));
		return true;
	}

	/**
	 * 校验上海无纸化业务关键字段（只进行格式校验）
	 * 
	 * @author 张会斌
	 * @param allList
	 * @return String
	 */
	public String checkVerify(List<IDto> subDtoList, String vtcode) {
		StringBuffer sb = new StringBuffer("");
		if (vtcode.equals(MsgConstant.VOUCHER_NO_5207)) {
			for (IDto dto : subDtoList) {
				sb.append(checkValidDetailFor5207(dto));
			}
		} else if (vtcode.equals(MsgConstant.VOUCHER_NO_2252)) {
			for (IDto dto : subDtoList) {
				sb.append(checkValidDetailFor2252(dto));
			}
		}
		return sb.toString();
	}

	/**
	 * 校验明细ID是否在原支付明细中
	 * 
	 * @param subDtoList
	 * @param idto
	 * @return
	 */
	public String checkVerify(List<IDto> subDtoList, IDto idto) {
		StringBuffer sb = new StringBuffer("");
		try {
			if (idto instanceof TfPaybankRefundmainDto) {
				TfPaybankRefundmainDto tmpDto = (TfPaybankRefundmainDto) idto;
				TvVoucherinfoDto tmpInfo = new TvVoucherinfoDto();
				tmpInfo.setSvoucherno(tmpDto.getSoriginalvoucherno());
				tmpInfo.setSstatus(DealCodeConstants.VOUCHER_SUCCESS);
				List searchInfosList = CommonFacade.getODB().findRsByDto(
						tmpInfo);
				if (null == searchInfosList || searchInfosList.size() == 0) {
					return "根据凭证编号" + tmpDto.getSoriginalvoucherno()
							+ "没有查询到对应的支付主单信息！";
				}
				tmpInfo = (TvVoucherinfoDto) searchInfosList.get(0);
				List<IDto> list = null;
				// 校验实拨资金退款明细ID
				if (StateConstant.PAYOUT_PAY_CODE.equals(tmpDto
						.getSpaytypecode().trim())) {
					TvPayoutmsgmainDto tmpMaindto = new TvPayoutmsgmainDto();
					tmpMaindto.setSbizno(tmpInfo.getSdealno());
					list = PublicSearchFacade.findSubDtoByMain(tmpMaindto);
				} else if (MsgConstant.directPay.equals(tmpDto
						.getSpaytypecode().trim())) { // 校验直接支付明细ID
					TfDirectpaymsgmainDto tmpMainDto = new TfDirectpaymsgmainDto();
					tmpMainDto.setIvousrlno(Long.valueOf(tmpInfo.getSdealno()));
					list = PublicSearchFacade.findSubDtoByMain(tmpMainDto);
				}
				if (null == list || list.size() == 0) {
					sb.append("查询明细表信息失败！");
					return sb.toString();
				} else {
					Map<String, IDto> map = VoucherUtil.convertListToMap(list);
					for (IDto dto : subDtoList) {
						if (null == map.get(((TfPaybankRefundsubDto) dto)
								.getSid())) {
							sb.append("明细ID："
									+ ((TfPaybankRefundsubDto) dto).getSid()
									+ "在支付单中不存在！");
							return sb.toString();
						}
					}
				}
			}
		} catch (ITFEBizException e) {
			logger.error(e);
			sb.append("查询明细表信息失败！");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			sb.append("查询明细表信息失败！");
		} catch (ValidateException e) {
			logger.error(e);
			sb.append("查询明细表信息失败！");
		}
		return sb.toString();
	}

	/**
	 * 校验上海无纸化业务关键字段（实拨资金5207）
	 * 
	 * @author 张会斌
	 * @param list
	 * @return String
	 */
	public String checkValidDetailFor5207(IDto dto) {
		// 用于存储校验信息
		StringBuffer sb = new StringBuffer("");
		Matcher match = null;

		TvPayoutmsgsubDto subDto = (TvPayoutmsgsubDto) dto;

		// 收款银行账号校验
		Pattern subPayeeAcctNoPattern = Pattern.compile("[0-9]{1,42}");// 匹配小于32位数字
		if (StringUtils.isBlank(subDto.getSpayeeacctno())) {
			sb.append("明细中的收款账号不能为空。");
		} else {
			match = subPayeeAcctNoPattern.matcher(subDto.getSpayeeacctno());
			if (match.matches() == false) {
				sb.append("明细中的收款账号[" + subDto.getSpayeeacctno()
						+ "]格式错误，必须为小于42位数字。");
			}
		}

		// 收款银行账户名称
		if (StringUtils.isBlank(subDto.getSpayeeacctname())) {
			sb.append("明细中的收款账号名称不能为空。");
		} else if (subDto.getSpayeeacctname().getBytes().length > 120) {
			sb.append("明细中的收款账号名称[" + subDto.getSpayeeacctname()
					+ "]格式错误，必须为小于120个字符（60个汉字）。");
		}

		// 收款银行名称
		if (StringUtils.isBlank(subDto.getSpayeeacctbankname())) {
			sb.append("明细中的收款银行名称不能为空。");
		} else if (subDto.getSpayeeacctbankname().getBytes().length > 60) {
			sb.append("明细中的收款银行名称[" + subDto.getSpayeeacctbankname()
					+ "]格式错误，必须为小于60个字符（30个汉字）。");
		}

		// 用途名称
		if (StringUtils.isBlank(subDto.getSpaysummaryname())) {
			sb.append("明细中的用途名称不能为空。");
		} else if (subDto.getSpaysummaryname().getBytes().length > 200) {
			sb.append("明细中的用途名称[" + subDto.getSpaysummaryname()
					+ "]格式错误，必须为小于200个字符（100个汉字）。");
		}

		return sb.toString();
	}

	/**
	 * 校验上海无纸化业务关键字段（收款银行退款通知凭证2252）
	 * 
	 * @author 张会斌
	 * @param list
	 * @return String
	 */
	public String checkValidDetailFor2252(IDto dto) {
		// 用于存储校验信息
		StringBuffer sb = new StringBuffer("");
		Matcher match = null;
		TfPaybankRefundsubDto subDto = (TfPaybankRefundsubDto) dto;

		// 原付款人账号校验
		Pattern subPayAcctNoPattern = Pattern.compile("([0-9A-Z]|[-]){1,32}");// 匹配小于42位数字
		if (StringUtils.isBlank(subDto.getSpayacctno())) {
			sb.append("明细中的原付款人账号不能为空。");
		} else {
			match = subPayAcctNoPattern.matcher(subDto.getSpayacctno());
			if (match.matches() == false) {
				sb.append("明细中的原付款人账号[" + subDto.getSpayacctno()
						+ "]格式错误，必须为小于42位数字。");
			}
		}

		// 原付款人账户名称
		if (StringUtils.isBlank(subDto.getSpayacctname())) {
			sb.append("明细中的原付款人账户名称不能为空。");
		} else if (subDto.getSpayacctname().getBytes().length > 120) {
			sb.append("明细中的原付款人账户名称[" + subDto.getSpayacctname()
					+ "]格式错误，必须为小于120个字符（60个汉字）。");
		}

		// 原付款人银行名称
		if (StringUtils.isBlank(subDto.getSpayacctbankname())) {
			sb.append("明细中的原付款人银行名称不能为空。");
		} else if (subDto.getSpayacctbankname().getBytes().length > 60) {
			sb.append("明细中的原付款人银行名称[" + subDto.getSpayacctbankname()
					+ "]格式错误，必须为小于60个字符（30个汉字）。");
		}

		// 收款银行账号校验
		Pattern subPayeeAcctNoPattern = Pattern.compile("([0-9A-Z]|[-]){1,32}");// 匹配小于42位数字
		if (StringUtils.isBlank(subDto.getSpayeeacctno())) {
			sb.append("明细中的原收款账号不能为空。");
		} else {
			match = subPayeeAcctNoPattern.matcher(subDto.getSpayeeacctno());
			if (match.matches() == false) {
				sb.append("明细中的原收款账号[" + subDto.getSpayeeacctno()
						+ "]格式错误，必须为小于42位数字。");
			}
		}

		// 收款银行账户名称
		if (StringUtils.isBlank(subDto.getSpayeeacctname())) {
			sb.append("明细中的原收款账号名称不能为空。");
		} else if (subDto.getSpayeeacctname().getBytes().length > 120) {
			sb.append("明细中的原收款账号名称[" + subDto.getSpayeeacctname()
					+ "]格式错误，必须为小于120个字符（60个汉字）。");
		}

		// 收款银行名称
		if (StringUtils.isBlank(subDto.getSpayeeacctbankname())) {
			sb.append("明细中的原收款银行名称不能为空。");
		} else if (subDto.getSpayeeacctbankname().getBytes().length > 60) {
			sb.append("明细中的原收款银行名称[" + subDto.getSpayeeacctbankname()
					+ "]格式错误，必须为小于60个字符（30个汉字）。");
		}

		Pattern bankcodePattern = Pattern.compile("[0-9]{12}");// 匹配12位数字
		// 原收款人银行行号
		if (StringUtils.isNotBlank(subDto.getSpayeeacctbankno())
				&& subDto.getSpayeeacctbankno().getBytes().length > 200) {
			match = bankcodePattern.matcher(subDto.getSpayeeacctbankno());
			if (match.matches() == false) {
				sb.append("明细中的原收款人银行行号[" + subDto.getSpayeeacctbankno()
						+ "]格式错误，必须为12位的数字。");
			}
		}
		return sb.toString();
	}

	/**
	 * 校验报文关键字段公共类（只进行格式校验）
	 * 
	 * @param verifyDto
	 * @param vtcode
	 * @return
	 */
	public String checkValid(VoucherVerifyDto dto, String vtcode) {
		Pattern trecodePattern = Pattern.compile("[0-9]{10}");// 匹配10位数字
		Pattern finorgcodePattern = Pattern.compile("[0-9]{1,12}");// 匹配小于12位数字
		Pattern bankcodePattern = Pattern.compile("[0-9]{12}");// 匹配12位数字
		Pattern yearPattern = Pattern.compile("[0-9]{4}");// 匹配4位数字
		Pattern monthPattern = Pattern.compile("[0-9]{2}");// 匹配2位数字
		Matcher match = null;
		StringBuffer sb = new StringBuffer();
		if (vtcode.equalsIgnoreCase(MsgConstant.VOUCHER_NO_5407)) {
			return null;
		}
		// 国库代码
		if (StringUtils.isBlank(dto.getTrecode())) {
			sb.append("国库代码不能为空。");
		} else {
			match = trecodePattern.matcher(dto.getTrecode());
			if (match.matches() == false) {
				sb.append("国库代码[" + dto.getTrecode() + "]格式错误，必须为10位数字。");
			}
		}
		// 财政机构代码
		if (StringUtils.isBlank(dto.getFinorgcode())) {
			sb.append("财政机构代码不能为空。");
		} else {
			match = finorgcodePattern.matcher(dto.getFinorgcode());
			if (match.matches() == false) {
				sb.append("财政机构代码[" + dto.getFinorgcode()
						+ "]格式错误，必须为小于12位的数字。");
			}
		}

		// 所属年度
		if (StringUtils.isBlank(dto.getOfyear())) {
			sb.append("所属年度不能为空。");
		} else {
			match = yearPattern.matcher(dto.getOfyear().trim());
			if (match.matches() == false) {
				sb.append("所属年度[" + dto.getOfyear() + "]格式错误，必须为4位数字。");
			}
			String currentyear = TimeFacade.getCurrentStringTime().substring(0,
					4);
			if (Integer.valueOf(dto.getOfyear().trim()) > Integer
					.valueOf(currentyear)) {
				sb.append("所属年度[" + dto.getOfyear() + "]不能超出当前年度。");
			}
		}
		// 所属月份
		if (MsgConstant.VOUCHER_NO_5106.equals(vtcode)) {
			if (StringUtils.isBlank(dto.getOfmonth())) {
				sb.append("所属月份不能为空。");
			} else {
				match = monthPattern.matcher(dto.getOfmonth().trim());
				if (match.matches() == false) {
					sb.append("所属月份[" + dto.getOfmonth() + "]格式错误，必须为2位数字。");
				}
				if (Integer.valueOf(dto.getOfmonth()) > 12
						|| Integer.valueOf(dto.getOfmonth()) < 1) {
					sb.append("所属月份[" + dto.getOfmonth() + "]格式错误：不能小于1超过12。");
				}
			}
		}

		// 发生额
		if (!MsgConstant.VOUCHER_NO_5951.equals(vtcode)
				&& !isCurrency(dto.getFamt())) {
			sb.append("支付金额[" + dto.getFamt()
					+ "]格式错：符号位可选，单位为元，整数部分最长15位，小数部分固定两位，不能包含逗号等分隔符。");
		} else {
			if (MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2252.equals(vtcode)) {// 退款申请金额为负
				if (Double.valueOf(dto.getFamt()) >= 0) {
					sb.append("支付金额[" + dto.getFamt() + "]格式错：必须为负数。");
				}
			}
			// else if ((MsgConstant.VOUCHER_NO_5108.equals(vtcode) ||
			// MsgConstant.VOUCHER_NO_5106
			// .equals(vtcode))
			// && ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
			// if (Double.valueOf(dto.getFamt()) < 0) {
			// sb.append("支付金额[" + dto.getFamt() + "]格式错：不能为负数。");
			// }
			// }
			else if (MsgConstant.VOUCHER_NO_5351.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5253.equals(vtcode)) {// 授权支付调整额度和直接支付调整金额只能为零
				if (!dto.getFamt().equals("0.00")) {
					sb.append("支付金额[" + dto.getFamt() + "]格式错：必须为零。");
				}
			} else if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5267.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2301.equals(vtcode)) {
				if (Double.valueOf(dto.getFamt()) <= 0) {
					sb.append("支付金额[" + dto.getFamt() + "]格式错：必须为正数。");
				}
			} else if ((MsgConstant.VOUCHER_NO_5108.equals(vtcode) || MsgConstant.VOUCHER_NO_5106
					.equals(vtcode))
					&& ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0) {
				if (Double.valueOf(dto.getFamt()) == 0)
					sb.append("支付金额[" + dto.getFamt() + "]格式错：额度金额不能是0。");
			}
		}

		// 支付方式编码
		if (StringUtils.isBlank(dto.getPaytypecode())) {
			// 2301、2302、5201、8207、5253、5351、2252、5207需校验支付方式编码
			if (MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5267.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5253.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_8207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2252.equals(vtcode))
				sb.append("支付方式编码不能为空。");
		} else {
			Pattern payTypePattern = Pattern.compile("[0-9]{2,6}");// 匹配2-6位数字
			match = payTypePattern.matcher(dto.getPaytypecode());
			if (match.matches() == false) {
				sb.append("支付方式编码格式错误，必须为2位或6位的数字。");
			} else {
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
					if ((MsgConstant.VOUCHER_NO_2301.equals(vtcode) || MsgConstant.VOUCHER_NO_2302
							.equals(vtcode))
							&& !"12".equals(dto.getPaytypecode())) {
						sb.append("支付方式编码[" + dto.getPaytypecode()
								+ "]类型错：只能为 12-授权支付");
					} else if ((MsgConstant.VOUCHER_NO_5201.equals(vtcode) || MsgConstant.VOUCHER_NO_5253
							.equals(vtcode))
							&& !"11".equals(dto.getPaytypecode())) {
						sb.append("支付方式编码[" + dto.getPaytypecode()
								+ "]类型错：只能为  11-直接支付");
					} else if (MsgConstant.VOUCHER_NO_2252.equals(vtcode)
							&& !("11".equals(dto.getPaytypecode()) || "91"
									.equals(dto.getPaytypecode()))) {
						sb.append("支付方式编码[" + dto.getPaytypecode()
								+ "]类型错：只能为  11-直接支付  91-实拨");
					} else if ((MsgConstant.VOUCHER_NO_5207.equals(vtcode) || MsgConstant.VOUCHER_NO_5267
							.equals(vtcode))
							&& !"91".equals(dto.getPaytypecode())) {
						sb.append("支付方式编码[" + dto.getPaytypecode()
								+ "]类型错：只能为  91-实拨");
					} else if (MsgConstant.VOUCHER_NO_8207.equals(vtcode)) {
						if (StringUtils.isNotBlank(dto.getOriginalVtCode())
								&& dto.getOriginalVtCode().equals(
										MsgConstant.VOUCHER_NO_5201)
								&& !"11".equals(dto.getPaytypecode()))
							sb.append("支付方式编码[" + dto.getPaytypecode()
									+ "]类型错：主凭证类型编号为" + dto.getOriginalVtCode()
									+ "，支付方式编码只能为 11-直接支付");
						else if (StringUtils
								.isNotBlank(dto.getOriginalVtCode())
								&& dto.getOriginalVtCode().equals(
										MsgConstant.VOUCHER_NO_5207)
								&& !"91".equals(dto.getPaytypecode()))
							sb.append("支付方式编码[" + dto.getPaytypecode()
									+ "]类型错：主凭证类型编号为" + dto.getOriginalVtCode()
									+ "，支付方式编码只能为 91-实拨");
						else if (!"11".equals(dto.getPaytypecode())
								&& !"12".equals(dto.getPaytypecode())
								&& !"91".equals(dto.getPaytypecode()))
							sb.append("支付方式编码[" + dto.getPaytypecode()
									+ "]类型错：只能为  11-直接支付 12-授权支付 91-实拨");
					}
				} else {
					if ((MsgConstant.VOUCHER_NO_2301.equals(vtcode) || MsgConstant.VOUCHER_NO_2302
							.equals(vtcode))
							&& !("11".equals(dto.getPaytypecode())
									|| "001001".equals(dto.getPaytypecode())
									|| "001002".equals(dto.getPaytypecode()) || "12"
									.equals(dto.getPaytypecode()))) {
						sb.append("支付方式编码[" + dto.getPaytypecode()
								+ "]类型错：11(001001)-直接支付　12(001002)-授权支付");
					} else if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
							&& !"91".equals(dto.getPaytypecode())
							&& !"002".equals(dto.getPaytypecode())
							&& !dto.getPaytypecode().startsWith("92")) {
						if(dto.getTrecode().startsWith("27") && !"21".equals(dto.getPaytypecode())){//甘肃
							sb.append("支付方式编码[" + dto.getPaytypecode()
									+ "]类型错：实拨只能为  21 ");
						}else if(dto.getTrecode().startsWith("01")&& !dto.getPaytypecode().startsWith("91")){
							sb.append("支付方式编码[" + dto.getPaytypecode()
									+ "]类型错：实拨只能为  91 、002或者以91开头");
						}else if(!dto.getTrecode().startsWith("27")&&!dto.getTrecode().startsWith("01")){
							sb.append("支付方式编码[" + dto.getPaytypecode()
									+ "]类型错：实拨只能为  91 或者002");
						}
						
					}
				}
			}
		}

		// 业务类型编码
		if (StringUtils.isBlank(dto.getBusinessTypeCode())) {
			if ((MsgConstant.VOUCHER_NO_5207.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
					.indexOf(",sh,") >= 0)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_8207.equals(vtcode))
				sb.append("业务类型编码不能为空。");
		} else {
			if (match.matches() == false) {
				sb.append("业务类型编码格式错误，必须为1位的数字。");
			} else if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					&& ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				if (!dto.getBusinessTypeCode().equals(
						StateConstant.BIZTYPE_CODE_SINGLE)
						&& !dto.getBusinessTypeCode().equals(
								StateConstant.BIZTYPE_CODE_BATCH))
					sb.append("业务类型编码[" + dto.getBusinessTypeCode()
							+ "]只能为1-单笔业务    4-批量业务!");
			} else if (MsgConstant.VOUCHER_NO_5201.equals(vtcode)) {
				if (!dto.getBusinessTypeCode().equals(
						StateConstant.BIZTYPE_CODE_SINGLE)
						&& !dto.getBusinessTypeCode().equals(
								StateConstant.BIZTYPE_CODE_SALARY)
						&& !dto.getBusinessTypeCode().equals(
								StateConstant.BIZTYPE_CODE_BATCH))
					sb.append("业务类型编码[" + dto.getBusinessTypeCode()
							+ "]只能为1-单笔业务 3-工资业务    4-批量业务!");
			} else if (MsgConstant.VOUCHER_NO_8207.equals(vtcode)) {
				if (StringUtils.isNotBlank(dto.getOriginalVtCode())
						&& (dto.getOriginalVtCode().equals(
								MsgConstant.VOUCHER_NO_5207) || dto
								.getOriginalVtCode().equals(
										MsgConstant.VOUCHER_NO_5201))
						&& !dto.getBusinessTypeCode().equals(
								StateConstant.BIZTYPE_CODE_BATCH))
					sb.append("业务类型编码[" + dto.getBusinessTypeCode()
							+ "]类型错：主凭证类型编号为" + dto.getOriginalVtCode()
							+ "，业务类型编码只能为 4-批量业务!");
				else if (!dto.getBusinessTypeCode().equals(
						StateConstant.BIZTYPE_CODE_SINGLE)
						&& !dto.getBusinessTypeCode().equals(
								StateConstant.BIZTYPE_CODE_SALARY)
						&& !dto.getBusinessTypeCode().equals(
								StateConstant.BIZTYPE_CODE_BATCH))
					sb.append("业务类型编码[" + dto.getBusinessTypeCode()
							+ "]只能为1-单笔业务 3-工资业务  4-批量业务!");
			}
		}

		// 业务类型名称
		if (StringUtils.isBlank(dto.getBusinessTypeName())) {
			if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					&& ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0)
				sb.append("业务类型名称不能为空。");
		} else {
			if (dto.getBusinessTypeName().getBytes().length > 60)
				sb.append("业务类型名称[" + dto.getBusinessTypeName()
						+ "]格式错误，必须为小于60个字符（30个汉字）。");
		}

		// 资金性质编码
		if (StringUtils.isBlank(dto.getFundTypeCode())) {

			if ((MsgConstant.VOUCHER_NO_5207.equals(vtcode) || MsgConstant.VOUCHER_NO_5267
					.equals(vtcode))
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_8207.equals(vtcode))
				sb.append("资金性质编码不能为空");
		} else {
			if (!dto.getFundTypeCode().startsWith("1")
					&& ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0)
				sb.append("资金性质编码[" + dto.getFundTypeCode() + "]错：只能为1！");
			else if (!dto.getFundTypeCode().startsWith("1")
					&& !dto.getFundTypeCode().startsWith("2")
					&& !dto.getFundTypeCode().startsWith("3")
					&& !dto.getFundTypeCode().startsWith("8")
					&& !dto.getFundTypeCode().startsWith("9")
					&& !dto.getFundTypeCode().endsWith("2")
					&& !dto.getFundTypeCode().endsWith("1")) {
				sb.append("资金性质编码[" + dto.getFundTypeCode() + "]错：只能与1、2、8、9开头！");
			}
		}

		// 原凭证类型
		if (StringUtils.isBlank(dto.getOriginalVtCode())) {
			if (MsgConstant.VOUCHER_NO_8207.equals(vtcode))
				sb.append("主凭证类型编号不能为空");
		} else {
			if (!(dto.getOriginalVtCode().equals(MsgConstant.VOUCHER_NO_5207)
					|| dto.getOriginalVtCode().equals(
							MsgConstant.VOUCHER_NO_5201) || dto
					.getOriginalVtCode().equals(MsgConstant.VOUCHER_NO_8202)))
				sb.append("主凭证类型编号[" + dto.getOriginalVtCode()
						+ "]错：只能为5207、5201或8202");
		}

		// 凭证编号
		if (StringUtils.isBlank(dto.getVoucherno())) {
			sb.append("凭证编号不能为空。");
		} else {
			if (MsgConstant.VOUCHER_NO_5209.equals(vtcode)) {// 收入退付
				Pattern oldVouPattern = Pattern.compile("[0-9]{8}");// 匹配8位数字
				if (ITFECommonConstant.IFNEWINTERFACE.equals("1")) {// 新接口
					if (dto.getVoucherno().getBytes().length > 20) {
						sb.append("凭证编号必须小于20位。");
					}
				} else {// 旧接口
					match = oldVouPattern.matcher(dto.getVoucherno());
					if (match.matches() == false) {
						sb.append("凭证编号格式错误，必须为8位的数字。");
					}
				}
			} else if (MsgConstant.VOUCHER_NO_5106.equals(vtcode)) {
				if (dto.getVoucherno().getBytes().length > 17) {
					sb.append("凭证编号必须小于17位。");
				}
			} else {
				if (dto.getVoucherno().getBytes().length > 20) {
					sb.append("凭证编号必须小于20位。");
				}
			}
		}

		// 比对凭证编号（上海特色）
		if (StringUtils.isBlank(dto.getPayVoucherNo())) {
			if ((MsgConstant.VOUCHER_NO_5106.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
					.indexOf(",sh,") >= 0)
					|| (MsgConstant.VOUCHER_NO_5108.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
							.indexOf(",sh,") >= 0))
				sb.append("支付凭证号（调整凭证号）不能为空。");
			else if (MsgConstant.VOUCHER_NO_2252.equals(vtcode))
				sb.append("原业务单据号不能为空。");
			else if (MsgConstant.VOUCHER_NO_8207.equals(vtcode))
				sb.append("主支付凭证编号不能为空。");
		} else {
			if (dto.getVoucherno().getBytes().length > 20) {
				if ((MsgConstant.VOUCHER_NO_5106.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
						.indexOf(",sh,") >= 0)
						|| (MsgConstant.VOUCHER_NO_5108.equals(vtcode) && ITFECommonConstant.PUBLICPARAM
								.indexOf(",sh,") >= 0))
					sb.append("支付凭证号（调整凭证号）必须小于20位。");
				else if (MsgConstant.VOUCHER_NO_2252.equals(vtcode))
					sb.append("原业务单据号 必须小于20位。");
				else if (MsgConstant.VOUCHER_NO_8207.equals(vtcode))
					sb.append("主支付凭证编号 必须小于20位。");
			}
		}

		// 凭证日期
		if (StringUtils.isBlank(dto.getVoudate())) {
			sb.append("凭证日期不能为空。");
		} else {// 增加凭证日期不能大于当前日期的校验
			String voucherdate = dto.getVoudate();
			Date comparedate = TimeFacade.parseDate(voucherdate);
			Date currentdate = TimeFacade.parseDate(TimeFacade
					.getCurrentStringTime());
			if (comparedate.after(currentdate)) {
				sb.append("凭证日期不能大于当前日期。");
			}
		}

		// 收款人账号
		if (StringUtils.isBlank(dto.getAgentAcctNo())) {
			if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5267.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5209.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode))
				sb.append("收款账号不能为空。");
		} else {
			Pattern acctPattern;
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				acctPattern = Pattern.compile("([0-9A-Z]|[-]){1,32}");// 匹配小于32位数字
			} else {
				acctPattern = Pattern.compile("([0-9A-Z]|[-]){1,32}");// 匹配小于32位数字
			}
			match = acctPattern.matcher(dto.getAgentAcctNo());
			if (match.matches() == false) {
				sb.append("收款账号[" + dto.getAgentAcctNo()
						+ "]格式错误，必须为小于32位的数字、字符[0-9A-Z]。");
			}
		}

		// 收款人账号名称
		if (StringUtils.isBlank(dto.getAgentAcctName())) {
			if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5267.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5209.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode))
				sb.append("收款人账号名称不能为空。");
		} else {
			if (dto.getAgentAcctName().getBytes().length > 60) {
				sb.append("收款人账号名称[" + dto.getAgentAcctName()
						+ "]格式错误，必须为小于60个字符（30个汉字）。");
			} else if (MsgConstant.VOUCHER_NO_5209.equals(vtcode)) {// 收入退付校收款人名称校验生僻字
				String errChi_9 = "";
				try {
					errChi_9 = VerifyParamTrans.verifyNotUsableChinese(dto
							.getAgentAcctName());
				} catch (Exception e) {
					logger.error(e);
				}
				if (null != errChi_9 && !"".equals(errChi_9)) {
					sb.append("收款人账号名称[" + dto.getAgentAcctName() + "]中存在非法字符："
							+ errChi_9 + "。");
				}
			}
		}

		// 收款人银行名称
		if (StringUtils.isBlank(dto.getPaybankname())) {
			if (MsgConstant.VOUCHER_NO_5207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5267.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5209.equals(vtcode))
				sb.append("收款人银行名称不能为空。");
		}

		// 代理银行行号
		if (StringUtils.isBlank(dto.getPaybankno())) {
			if (MsgConstant.VOUCHER_NO_5106.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5108.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_8207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5253.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5351.equals(vtcode))
				sb.append("代理银行行号不能为空。");
		} else {
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
					&& (MsgConstant.VOUCHER_NO_5108.equals(vtcode)
							|| MsgConstant.VOUCHER_NO_5253.equals(vtcode) || MsgConstant.VOUCHER_NO_5201
							.equals(vtcode))) {
				match = finorgcodePattern.matcher(dto.getPaybankno());
			} else {
				match = bankcodePattern.matcher(dto.getPaybankno());
			}
			if (match.matches() == false) {
				sb.append("代理银行行号格式错误，必须为12位的数字。");
			}
		}

		// 付款人账号
		if (StringUtils.isBlank(dto.getClearAcctNo())) {
			if (MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_8207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5253.equals(vtcode))
				sb.append("付款人账号不能为空。");
		} else {
			if (dto.getClearAcctNo().getBytes().length > 32) {
				sb.append("付款人账号[" + dto.getClearAcctNo() + "]格式错误，必须小于32位。");
			}
		}

		// 付款人账号名称
		if (StringUtils.isBlank(dto.getClearAcctName())) {
			if (MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5201.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_8207.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_5253.equals(vtcode))
				sb.append("付款人账号名称不能为空。");
		} else {
			if (dto.getClearAcctName().getBytes().length > 60)
				sb.append("付款人账号名称[" + dto.getClearAcctName()
						+ "]格式错误，必须为小于60个字符（30个汉字）。");
		}

		// 实拨资金预算单位名称
		if ((MsgConstant.VOUCHER_NO_5207.equals(vtcode) || MsgConstant.VOUCHER_NO_5267
				.equals(vtcode))
				&& StringUtils.isBlank(dto.getAgencyName())) {
			sb.append("预算单位名称不能为空。");
		} else {
			if ((MsgConstant.VOUCHER_NO_5207.equals(vtcode) || MsgConstant.VOUCHER_NO_5267
					.equals(vtcode))
					&& dto.getAgencyName().getBytes().length > 120)
				sb.append("预算单位名称[" + dto.getAgencyName()
						+ "]格式错误，必须为小于120个字符（60个汉字）。");
		}

		/**
		 * 上海商行发往人行的收款银行退款通知凭证2252特殊字段格式校验
		 * 
		 * @author 张会斌
		 */
		if (MsgConstant.VOUCHER_NO_2252.equals(vtcode)) {

			// 支付交易序号
			Pattern pattern = Pattern.compile("[0-9]{8}");// 匹配8位数字
			// 用银行代码paybankno代传支付交易序号payDictateNo
			if (StringUtils.isBlank(dto.getPaybankname())) {
				sb.append("支付交易序号不能为空。");
			} else {
				match = pattern.matcher(dto.getPaybankname());
				if (match.matches() == false) {
					sb.append("支付交易序号[" + dto.getPaybankname()
							+ "]格式错误，必须为8位的数字。");
				}
			}

			// 支付报文编号
			Pattern acctPattern = Pattern.compile("[0-9]{3}");// 匹配3位数字
			// 用收款银行账号代传支付报文编号
			if (StringUtils.isBlank(dto.getAgentAcctNo())) {
				sb.append("支付报文编号不能为空。");
			} else {
				match = acctPattern.matcher(dto.getAgentAcctNo());
				if (match.matches() == false) {
					sb.append("支付报文编号[" + dto.getAgentAcctNo()
							+ "]格式错误，必须为3位数字。");
				} else if (!"100".equals(dto.getAgentAcctNo())
						&& !"103".equals(dto.getAgentAcctNo())
						&& !"001".equals(dto.getAgentAcctNo())
						&& !"111".equals(dto.getAgentAcctNo())
						&& !"112".equals(dto.getAgentAcctNo())
						&& !"121".equals(dto.getAgentAcctNo())
						&& !"122".equals(dto.getAgentAcctNo())) {
					sb
							.append("支付报文编号["
									+ dto.getAgentAcctNo()
									+ "]类型错误，必须为一代编号：100、 103、001 或者二代编号： 111、112、121、122。");
				}
			}

			// 用收款银行账户名称代传支付委托日期
			if (StringUtils.isBlank(dto.getAgentAcctName().trim())) {
				sb.append("支付委托日期不能为空。");
			} else {// 支付委托日期不能大于当前日期的校验
				String payEntrustDate = dto.getAgentAcctName();
				Date comparedate = TimeFacade.parseDate(payEntrustDate);
				Date currentdate = TimeFacade.parseDate(TimeFacade
						.getCurrentStringTime());
				if (comparedate.after(currentdate)) {
					sb.append("支付委托日期不能大于当前日期。");
				}
			}

			// 支付发起行行号
			Pattern paySendBankPattern = Pattern.compile("[0-9]{1,14}");// 匹配小于14位数字
			// 用付款账号代传支付发起行行号
			if (StringUtils.isBlank(dto.getClearAcctNo())) {
				sb.append("支付发起行行号不能为空。");
			} else {
				match = paySendBankPattern.matcher(dto.getClearAcctNo());
				if (match.matches() == false) {
					sb.append("支付发起行行号[" + dto.getClearAcctNo()
							+ "]格式错误，必须小于14位数字。");
				}
			}

			// 用付款账号名称代传实际退款日期
			if (StringUtils.isBlank(dto.getClearAcctName())) {
				sb.append("实际退款日期不能为空。");
			} else {// 实际退款日期不能大于当前日期的校验
				if (Integer.parseInt(TimeFacade.getCurrentStringTime()) < Integer
						.parseInt(dto.getClearAcctName().substring(0, 8)))
					sb.append("实际退款日期不能大于当前日期。");
			}
		}

		// 预算单位进行校验
		/**
		 * 校验账号与预算种类是否一致 湖南区分预算内（外）其他默认预算内 校验方式((S_ORGCODE, S_TRECODE,
		 * S_PAYERACCOUNT)
		 */
		if ("000073100012".equals(ITFECommonConstant.SRC_NODE)&&(MsgConstant.VOUCHER_NO_2301.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_5207.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_5267.equals(vtcode))) {
			TsInfoconnorgaccPK tmppk = new TsInfoconnorgaccPK();
			tmppk.setSorgcode(dto.getOrgcode()); // 核算主体
			tmppk.setStrecode(dto.getTrecode()); // TreCode国库主体
			tmppk.setSpayeraccount(dto.getClearAcctNo());// ClearAcctNo 付款账号
			TsInfoconnorgaccDto resultdto = null;
			try {
				resultdto = (TsInfoconnorgaccDto) DatabaseFacade.getDb().find(
						tmppk);
			} catch (JAFDatabaseException e) {
				sb.append("查询库款账户异常:" + e.getMessage());
			}
			if (null == resultdto) {
				sb.append("报文中所填账户信息在财政库款账户中不存在!");
			} else {
				if (StringUtils.isBlank(resultdto.getSbiztype())) {
					resultdto.setSbiztype("1");
				}
				if (!resultdto.getSbiztype().equalsIgnoreCase(
						dto.getBudgettype())) {
					sb.append("预算种类与财政库款账户信息中维护的不一致");
				}
			}
		}

		String msg = sb.toString();
		if (msg.length() == 0) {
			return null;
		}
		return msg;
	}

	/**
	 * 校验货币金额：符号位可选，单位为元，整数部分最长15位，小数部分固定两位，不能包含逗号等分隔符，如：8979.05
	 * 
	 * @param str
	 * @return
	 */
	private boolean isCurrency(String str) {
		Pattern pattern = Pattern.compile("^[+-]?[0-9]{1,15}(\\.\\d{2}){1}");
		Matcher match = pattern.matcher(str.trim());
		return match.matches();
	}

	/**
	 * 校验明细Id是否为空或重复
	 * 
	 * @param subDtoIdList
	 * @return
	 */
	public static String checkValidSudDtoId(List<String> subDtoIdList) {
		if (subDtoIdList.size() == 0)
			return "明细Id存在空值";
		StringBuffer sb = new StringBuffer();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (String id : subDtoIdList) {
			if (StringUtils.isBlank(id))
				sb.append("明细Id存在空值、");
			if (id.equals("节点不存在"))
				return null;
			Integer count = map.get(id);
			if (count == null)
				map.put(id, 1);
			else
				map.put(id, ++count);
		}
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String id = (String) it.next();
			Integer count = map.get(id);
			if (count > 1) {
				sb.append(id + "存在" + count + "个、");
			}
		}
		return StringUtils.isBlank(sb.toString()) ? null : "明细Id重复："
				+ sb.toString().substring(0, sb.toString().length() - 1) + "。";
	}
	
	private int verifyFor5407(List lists, String vtCode) throws JAFDatabaseException, ValidateException, ITFEBizException {
		List succList = new ArrayList();
		int count = 0;
		for (List list : (List<List>) lists) {
			TvInCorrhandbookDto mainDto = (TvInCorrhandbookDto) list.get(0);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) list.get(1);

			// 预算科目代码list
			ArrayList<String> expFuncCodeList = new ArrayList<String>();
			expFuncCodeList.add(mainDto.getSoribdgsbtcode());
			expFuncCodeList.add(mainDto.getScurbdgsbtcode());
			// 校验浙江要求不校验国库代码verifyTreasury(mainDto.getSoripayeetrecode(), "")&&verifyTreasury(mainDto.getScurpayeetrecode(), "")&& 
			if (verifySfinOrgCode(mainDto.getSfinorgcode(),vDto.getSorgcode(), vDto.getStrecode())
					&& verifySubject(vDto.getSorgcode(),expFuncCodeList, vtCode)
			) {
				
				VoucherUtil.voucherVerifyUpdateStatus(vDto, mainDto
							.tableName(), null, true);
				count++;
				succList.add(new Integer(count));
			} else {
				VoucherUtil.voucherVerifyUpdateStatus(vDto,
						mainDto.tableName(), this.tmpFailReason, false);
			}
		}
		return count;
	}
	/**
	 * 校验国库预算级次
	 * 
	 * @return
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public boolean verifyInfoAndPay(TvVoucherinfoDto vDto) throws JAFDatabaseException,
			ValidateException, ITFEBizException {
		try{
			Map<String,TsOrganDto> orgmap = SrvCacheFacade.cacheOrgMap();
			Voucher ver = new Voucher();
			if("hold1".equals(orgmap.get(vDto.getSorgcode()).getSofprovorgcode()))
			{
				TvVoucherinfoDto vsdto = new TvVoucherinfoDto();
				if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301))
					vsdto.setShold1(vDto.getSvoucherno());
				else if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108))
					vsdto.setSvoucherno(vDto.getShold1());
				vsdto.setScreatdate(vDto.getScreatdate());
				vsdto.setSstyear(vDto.getSstyear());
				vsdto.setSorgcode(vDto.getSorgcode());
				vsdto.setStrecode(vDto.getStrecode());
				List<TvVoucherinfoDto> questlist = CommonFacade.getODB().findRsByDto(vsdto);
				TvVoucherinfoDto seardto = null;
				if(questlist!=null&&questlist.size()==1)
					seardto = questlist.get(0);
				logger.info(vDto.getSvtcode()+"额度和清算匹配查询"+(seardto==null?"查询为空!":seardto.getSvtcode()));
				logger.info(vDto.getSvoucherno()+"额度和清算匹配查询"+(seardto==null?"查询为空!":seardto.getSvoucherno()));
				logger.info(vDto.getSstatus()+"额度和清算匹配查询"+(seardto==null?"查询为空!":seardto.getSstatus()));
				if(seardto==null||"16627290".contains(seardto.getSstatus()))
				{
					this.tmpFailReason = "比对额度信息和清算信息失败!";
					return false;
				}else if(DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(seardto.getSstatus())&&"10151720".contains(vDto.getSstatus()))
				{
					if((vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)))
					{
						List<TvVoucherinfoDto> verlist = new ArrayList<TvVoucherinfoDto>();
						verlist.add(seardto);
						ver.voucherVerify(verlist);
					}
				}
			}
		}catch(Exception e)
		{
			logger.error(e);
		}
		return true;
	}
}

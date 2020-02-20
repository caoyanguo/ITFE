package com.cfcc.itfe.twcs;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TfFundAppropriationDto;
import com.cfcc.itfe.persistence.dto.TfReconciliationDto;
import com.cfcc.itfe.persistence.dto.TfRefundNoticeDto;
import com.cfcc.itfe.persistence.dto.TfResultReconciDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsTbsorgstatusDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.webservice.VoucherService;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author Administrator
 * 
 */

@WebService(endpointInterface = "com.cfcc.itfe.twcs.ITwcs", serviceName = "TwcsImpl")
public class TwcsImpl implements ITwcs {
	private static Log logger = LogFactory.getLog(TwcsImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.itfe.tbs.ITbs#sayHi(java.lang.String)
	 */
	public String sayHi(String text) {
		try {
			logger.info("测试性连接:" + text);
			return VoucherUtil.base64Encode(text + "hello World!");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}
	}
	/**
	 * 读取凭证
	 * 
	 * @param admDivCode区划代码
	 * @param vtCode
	 *            凭证编号
	 * @param treCode
	 *            国库代码
	 * @param vouDate
	 *            凭证日期
	 * @return 返回凭证内容，用Base64加密
	 * @throws ITFEBizException
	 */
	public String readVoucher(String admDivCode, String vtCode, String treCode,
			String vouDate) {
		logger.info("TWCS调用接口(readVoucher)参数admDivCode:" + admDivCode + "vtCode:"
				+ vtCode + "treCode:" + treCode + "vouDate:" + vouDate);
		SQLExecutor sqlExecutor = null;
		try {
			List params = new ArrayList();
			params.add(vtCode);
			params.add(treCode);
			params.add(vouDate);
			// 查询 S_EXT1为空(从凭证库读取过来时已经赋值为空字符) 凭证状态为20 校验成功,50复核成功的数据(5207为审核成功--补录收款人开户行行号)
			String whereSql=" WHERE  length(S_EXT1) != 2  AND  S_VTCODE = ?  AND S_TRECODE  = ? AND S_CREATDATE <= ? AND (S_STATUS = '20' OR S_STATUS = '50') AND S_PAYBANKCODE IS NOT NULL FETCH FIRST 100 ROWS ONLY";
			if (treCode.startsWith("22") && (MsgConstant.VOUCHER_NO_5207.equals(vtCode))) {
				whereSql=" WHERE  length(S_EXT1) != 2  AND  S_VTCODE = ?  AND S_TRECODE  = ? AND S_CREATDATE <= ? AND (S_STATUS = '20') AND S_PAYBANKCODE IS NOT NULL FETCH FIRST 100 ROWS ONLY";
			}
			//对于重庆的事中监督，读取已回单状态的记录用于汇总对账
			if (treCode.startsWith("22") && (MsgConstant.VOUCHER_NO_2301.equals(vtCode)|| MsgConstant.VOUCHER_NO_2302.equals(vtCode))) {
				 whereSql=" WHERE  (S_EXT3 IS NULL OR length(S_EXT3) != 2)  AND  S_VTCODE = ?  AND S_TRECODE  = ? AND S_CREATDATE <= ? AND (S_STATUS = '80') AND S_PAYBANKCODE IS NOT NULL FETCH FIRST 100 ROWS ONLY";
			}
			List<TvVoucherinfoDto> result = DatabaseFacade.getODB().find(TvVoucherinfoDto.class, whereSql, params);
			if (null == result || result.size() == 0) {
				return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL);
			}
			List bizDatas = TWCSVerifyUtils.getBizDatas(result);
			if (null == bizDatas || bizDatas.size() == 0) {
				logger.error("未找到需要读取的凭证信息");
				return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + "未找到需要读取的凭证信息");
			}
			// Twcs读取状态保存在S_EXT1；同时更新status字段为"已收妥"
			List<IDto> infoList = (List<IDto>) bizDatas.get(0);
			for (IDto tmpDto : infoList) {
				TvVoucherinfoDto voucherDto = (TvVoucherinfoDto)tmpDto;
				//重庆国库，集中支付业务读取后,S_EXT3状态修改85 : 备注监管系统已读取
				if (treCode.startsWith("22") && (MsgConstant.VOUCHER_NO_2301.equals(vtCode)|| MsgConstant.VOUCHER_NO_2302.equals(vtCode))) {
					voucherDto.setSext3(TwcsDealCodeConstants.VOUCHER_ACCEPT_RETURNVOUCHER);
					voucherDto.setSdemo(TwcsDealCodeConstants.VOUCHER_ACCEPT_INFO);
					voucherDto.setSext1(TwcsDealCodeConstants.VOUCHER_ACCEPT_INFO);
				}else{//四川读取后 修改  status 为 监管中57  附言显示监管中
					voucherDto.setSext1(DealCodeConstants.VOUCHER_REGULATORY_AUDITING);
					voucherDto.setSstatus(DealCodeConstants.VOUCHER_REGULATORY_AUDITING);
					voucherDto.setSdemo(TwcsDealCodeConstants.VOUCHER_ACCEPT_AUDITING);
				}
			}
			DatabaseFacade.getODB().update(CommonUtil.listTArray(infoList));
			logger.info("结果：" + (String) bizDatas.get(1));
			return VoucherUtil.base64Encode((String) bizDatas.get(1));
		} catch (Exception e) {
			logger.error("TwcsImpl.readVoucher[" + e.getMessage() + "]", e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} finally {
			if (null != sqlExecutor) {
				sqlExecutor.closeConnection();
			}
		}
	}
	/**
	 * 重庆读取集中支付清算成功后，调用该接口更新集中支付读取状态为读取成功
	 * 
	 * @param admDivCode
	 *            区划代码
	 * @param vtCode
	 *            凭证编号
	 * @param treCode
	 *            国库代码
	 * @param vouDate
	 *            凭证日期
	 * @param vouNo
	 *            凭证编号
	 * @return 返回操作结果
	 * @throws ITFEBizException
	 */
	public String updateConPayState( String vtCode,
			String treCode, String vouDate) {
		logger.info("CQ集中支付业务签收" + "vtCode:"
				+ vtCode + "treCode:" + treCode + "vouDate:" + vouDate);
		try {
			// 查询索引表对应的信息
			List <TvVoucherinfoDto> list  = TWCSVerifyUtils
					.getTvVoucherinfoList(treCode, vtCode, vouDate,
							null);
			if (null == list || list.size()>0) {
				logger.error("未找到对应的凭证信息");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "未找到对应的凭证信息");
			}
			// 设置TWCS读取数据成功
			List <IDto> updateList = new ArrayList<IDto>();
			for (TvVoucherinfoDto tvd : list) {
				tvd.setSext3(TwcsDealCodeConstants.VOUCHER_ACCEPT_SUCCESS);
				tvd.setSdemo(TwcsDealCodeConstants.TWCS_ACCEPT_SUCCESS);
				updateList.add(tvd);
			}
			DatabaseFacade.getODB().update(CommonUtil.listTArray(updateList));
			return VoucherUtil
					.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
		} catch (ITFEBizException e) {
			logger.error("TbsImpl.confirmVoucher[" + e.getMessage() + "]", e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TbsImpl.confirmVoucher[" + e.getMessage() + "]", e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}

	}

	/**
	 * 更新5106和5108 X字段
	 * 
	 * @param vDto
	 * @throws ITFEBizException
	 */
	private static void updateMainDto(TvVoucherinfoDto vDto)
			throws ITFEBizException {
		try {
			String status = DealCodeConstants.DEALCODE_ITFE_SUCCESS;
			String xpaydate = TimeFacade.getCurrentStringTime();
			if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106)) {
				TvGrantpaymsgmainDto mainDto = new TvGrantpaymsgmainDto();
				mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
				TvGrantpaymsgsubDto subDto = new TvGrantpaymsgsubDto();
				subDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
				mainDto = (TvGrantpaymsgmainDto) CommonFacade.getODB()
						.findRsByDto(mainDto).get(0);
				List<TvGrantpaymsgsubDto> subDtoList = CommonFacade.getODB()
						.findRsByDto(subDto);
				if (subDtoList == null || subDtoList.size() == 0)
					throw new ITFEBizException("查询子表信息异常：与凭证："
							+ vDto.getSvoucherno() + "对应的子表记录不存在！");
				mainDto.setSxacctdate(xpaydate);
				mainDto.setSstatus(status);
				DatabaseFacade.getODB().update(mainDto);
				for (TvGrantpaymsgsubDto subdto : subDtoList) {
					subdto.setSstatus(status);
					DatabaseFacade.getODB().update(subdto);
				}
			} else if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {
				TvDirectpaymsgmainDto mainDto = new TvDirectpaymsgmainDto();
				mainDto.setIvousrlno(Long.parseLong(vDto.getSdealno()));
				mainDto = (TvDirectpaymsgmainDto) CommonFacade.getODB()
						.findRsByDto(mainDto).get(0);
				mainDto.setSxacctdate(xpaydate);
				mainDto.setSstatus(status);
				DatabaseFacade.getODB().update(mainDto);
			}
		} catch (java.lang.IndexOutOfBoundsException e) {
			logger.error("查询主表信息异常：与凭证：" + vDto.getSvoucherno()
					+ "对应的主表记录不存在！   ");
			throw new ITFEBizException("查询主表信息异常：与凭证：" + vDto.getSvoucherno()
					+ "对应的主表记录不存在！", e);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	
	/**
	 * 凭证退回
	 * 
	 * @param admDivCode
	 *            区划代码
	 * @param vtCode
	 *            凭证编号
	 * @param treCode
	 *            国库代码
	 * @param vouDate
	 *            凭证日期
	 * @param vouNo
	 *            凭证编号
	 * @param errMsg
	 *            失败原因
	 * @return 返回操作结果 例如(0:成功，1:失败)
	 * @throws ITFEBizException
	 */
	public String returnVoucherBack(String admDivCode, String vtCode,
			String treCode, String vouDate, String vouNo, String errMsg) {
		logger.info("TBS调用接口(returnVoucherBack)参数admDivCode:" + admDivCode
				+ "vtCode:" + vtCode + "treCode:" + treCode + "vouDate:"
				+ vouDate + "vouNo:" + vouNo);
		try {
			// 校验参数信息
			String str = TWCSVerifyUtils.verifyReturnVoucherBackParam(
					admDivCode, vtCode, treCode, vouDate, vouNo, errMsg);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + str);
			}
			// 查询索引表对应的信息
			TvVoucherinfoDto tvVoucherinfoDto = TWCSVerifyUtils
					.getTvVoucherinfoDto(treCode, vtCode, vouDate, vouNo,
							admDivCode);
			if (null == tvVoucherinfoDto) {
				logger.error(VoucherUtil.base64Encode("未找到对应的凭证信息"));
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "未找到对应的凭证信息");
			}
			// 判断凭证状态是否锁定
			str = TWCSVerifyUtils.verifyInfoLocked(tvVoucherinfoDto);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + str);
			}
			// 等待清算行清算资金结果凭证 不允许退回
			if (!(TwcsDealCodeConstants.VOUCHER_ACCEPT.equals(tvVoucherinfoDto
					.getSext1().trim())
					|| TwcsDealCodeConstants.VOUCHER_VALIDAT_SUCCESS
							.equals(tvVoucherinfoDto.getSext1().trim())
					|| TwcsDealCodeConstants.VOUCHER_VALIDAT_FAIL
							.equals(tvVoucherinfoDto.getSext1().trim()) || TwcsDealCodeConstants.VOUCHER_LIQUIDATION_FAILE
					.equals(tvVoucherinfoDto.getSext1().trim()))) {
				logger.error("状态为清算中、清算成功、回单成功或退回成功不允许操作");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "状态为清算中、清算成功、回单成功或退回成功不允许操作");
			}
			// 调用凭证库执行 凭证退回
			VoucherService voucherService = new VoucherService();
			String err = voucherService.returnVoucherBack(null, admDivCode,
					Integer.parseInt(tvVoucherinfoDto.getSstyear()), vtCode,
					new String[] { vouNo },
					new String[] { errMsg.length() > 50 ? errMsg.substring(0,
							50) : errMsg });
			if (StringUtils.isNotBlank(err)) {
				// 调用凭证库失败的，记录状态和原因 方便跟踪问题
				TWCSVerifyUtils.updateVoucherInfoStatus(tvVoucherinfoDto,
						TwcsDealCodeConstants.VOUCHER_APP_FAIL, errMsg);
				logger.error("[国库代码:" + treCode + "凭证编号:" + vouNo + "凭证日期:"
						+ vouDate + "]调用凭证库returnVoucher失败");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + err);
			}
			// 记录凭证状态和退回原因
			TWCSVerifyUtils.updateVoucherInfoStatus(tvVoucherinfoDto,
					TwcsDealCodeConstants.VOUCHER_FAIL, "退回成功");
			return VoucherUtil
					.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
		} catch (ITFEBizException e) {
			logger
					.error("TbsImpl.returnVoucherBack[" + e.getMessage() + "]",
							e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger
					.error("TbsImpl.returnVoucherBack[" + e.getMessage() + "]",
							e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}
	}

	/**
	 * 发送凭证
	 * 
	 * @param admDivCode
	 *            区划代码
	 * @param vtCode
	 *            凭证编号
	 * @param treCode
	 *            国库代码
	 * @param vouDate
	 *            凭证日期
	 * @param vouNo
	 *            凭证编号
	 * @return 返回操作结果 例如(0:成功，1:失败)
	 * @throws ITFEBizException
	 */
	public String sendVoucher(String admDivCode, String vtCode, String treCode,
			String vouDate, String vouNo) {
		logger.info("TBS调用接口(sendVoucher)参数admDivCode:" + admDivCode + "vtCode:"
				+ vtCode + "treCode:" + treCode + "vouDate:" + vouDate
				+ "vouNo:" + vouNo);
		// 校验参数信息
		try {
			String str = TWCSVerifyUtils.verifySendVoucherParam(admDivCode,
					vtCode, treCode, vouDate, vouNo);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + str);
			}
			// 查询索引表对应的信息
			TvVoucherinfoDto tvVoucherinfoDto = TWCSVerifyUtils
					.getTvVoucherinfoDto(treCode, vtCode, vouDate, vouNo,
							admDivCode);
			if (null == tvVoucherinfoDto) {
				logger.error("未找到对应的凭证信息");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "未找到对应的凭证信息");
			}
			// 判断凭证状态是否锁定
//			str = TBSVerifyUtils.verifyInfoLocked(tvVoucherinfoDto);
//			if (StringUtils.isNotBlank(str)) {
//				logger.error(str);
//				return VoucherUtil
//						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
//			}

			if (ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode01,") >= 0) { // 厦门凭证库清算模式
				if (!(TwcsDealCodeConstants.VOUCHER_VALIDAT_SUCCESS
						.equals(tvVoucherinfoDto.getSext1().trim()))) {
					logger.error("只能发送校验成功的凭证");
					return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + "只能发送校验成功的凭证");
				}
			} else if (ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode02,") >= 0) { // 北京清算模式
//				if (!(TbsDealCodeConstants.VOUCHER_LIQUIDATION_SUCCEED.equals(tvVoucherinfoDto.getSext1().trim()))) {
//					logger.error("只能发送清算成功的凭证");
//					return VoucherUtil.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + "只能发送清算成功的凭证");
//				}
				if((tvVoucherinfoDto.getSvtcode().equals("2301") || tvVoucherinfoDto.getSvtcode().equals("5207") 
						|| tvVoucherinfoDto.getSvtcode().equals("5209")) 
						&& !TwcsDealCodeConstants.VOUCHER_LIQUIDATION_SUCCEED.equals(tvVoucherinfoDto.getSext1().trim())){
					logger.error("只能发送清算成功的凭证");
					return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + "只能发送清算成功的凭证");
				}
			} else {
				logger.error("TBS资金清算模式配置有误");
				return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + "TBS资金清算模式配置有误");
			}

			// 是否系统自动生成凭证信息
			boolean isSysGenVoucherFlag = Boolean.FALSE;
			if (MsgConstant.VOUCHER_NO_3208.equals(tvVoucherinfoDto.getSvtcode())) {
				isSysGenVoucherFlag = true;
			}
			// 签名、签章和转发操作SYSConfig.xml配置
			if (ITFECommonConstant.TBS_011.containsKey(tvVoucherinfoDto.getSvtcode())) {
				TWCSVerifyUtils.sendVoucherUtil(ITFECommonConstant.TBS_011,
						tvVoucherinfoDto, StateConstant.ORGCODE_FIN,
						isSysGenVoucherFlag);
			}
			if (ITFECommonConstant.TBS_111.containsKey(tvVoucherinfoDto.getSvtcode())) {
				if(null == ITFECommonConstant.TBS_TREANDBANK || ITFECommonConstant.TBS_TREANDBANK.size() == 0){
					throw new ITFEBizException("根据国库代码获取代理行信息失败！");
				}
				TWCSVerifyUtils.sendVoucherUtil(ITFECommonConstant.TBS_111,
						tvVoucherinfoDto, ITFECommonConstant.TBS_TREANDBANK.get(tvVoucherinfoDto.getStrecode()),
						isSysGenVoucherFlag);
			}
			return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
		} catch (UnsupportedEncodingException e) {
			logger.error("TbsImpl.sendVoucher[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TbsImpl.sendVoucher[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		}
	}

	/**
	 * 凭证状态同步
	 * @param 凭证流水号集合
	 * @throws ITFEBizException
	 */
	public String synchronousVoucherStatus(String vtCode,String dealnos) {
		logger.info("TWCS调用接口(synchronousVoucherStatus)参数:dealnos" +dealnos );
		try {
			String str = VoucherUtil.base64Decode(dealnos);
			if (StringUtils.isBlank(str)) {
				logger.error("回执解密失败"+str);
				return null;
			}
			String retMsg = TWCSVerifyUtils
					.updateVoucherStatus(vtCode,str);
			// 返回TWCS读取的凭证状态
			return VoucherUtil.base64Encode(retMsg);
		} catch (ITFEBizException e) {
			logger.error("TwcsImpl.synchronousVoucherStatus[" + e.getMessage()
					+ "]", e);
		
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TwcsImpl.synchronousVoucherStatus[" + e.getMessage()
					+ "]", e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}
	}
	
	
	/**
	 * 同步四川所有业务凭证的清算状态
	 * @param 凭证流水号集合
	 * @throws ITFEBizException
	 */
	public String updateVoucherReckStatus(String vtCode,String dealnos) {
		logger.info("TWCS调用接口(updateVoucherReckStatus)参数:dealnos" +dealnos );
		try {
			String str = VoucherUtil.base64Encode(dealnos);
			if (StringUtils.isBlank(str)) {
				logger.error("回执解密失败"+str);
				return null;
			}
			String retMsg = TWCSVerifyUtils
					.updateVoucherReckStatus(vtCode,str);
			// 返回TWCS读取的凭证状态
			return VoucherUtil.base64Encode(retMsg);
		} catch (ITFEBizException e) {
			logger.error("TwcsImpl.synchronousVoucherStatus[" + e.getMessage()
					+ "]", e);
		
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TwcsImpl.synchronousVoucherStatus[" + e.getMessage()
					+ "]", e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}
	}

	/**
	 * 重新读取凭证
	 * 
	 * @param admDivCode区划代码
	 * @param vtCode
	 *            凭证编号
	 * @param treCode
	 *            国库代码
	 * @param vouDate
	 *            凭证日期
	 * @return 返回凭证内容，用Base64加密
	 * @throws ITFEBizException
	 */
	public String readVoucherAgain(String admDivCode, String vtCode,
			String treCode, String vouDate) {
		logger.info("TBS调用接口(readVoucherAgain)参数admDivCode:" + admDivCode
				+ "vtCode:" + vtCode + "treCode:" + treCode + "vouDate:"
				+ vouDate + "vouNo:");
		SQLExecutor sqlExecutor = null;
		try {
			// 校验参数信息
			String str = TWCSVerifyUtils.verifyReadVoucherParam(admDivCode,
					vtCode, treCode, vouDate);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + str);
			}
			List params = new ArrayList();
			// params.add(admDivCode);
			params.add(vtCode);
			params.add(treCode);
			params.add(vouDate);
			// 不读取凭证状态S_STATUS为 已读取 签收成功 签收失败数据
			List<TvVoucherinfoDto> result = DatabaseFacade
					.getODB()
					.find(
							TvVoucherinfoDto.class,
							" WHERE length(S_EXT1) = 2  AND S_VTCODE = ? AND S_TRECODE = ? AND S_CREATDATE = ? AND (S_STATUS != '10' OR S_STATUS != '15' OR S_STATUS != '16') AND S_PAYBANKCODE IS NOT NULL ",
							params);
			if (null == result || result.size() == 0) {
				logger.error("未找到需要读取的凭证信息");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "未找到需要读取的凭证信息");
			}
			List bizDatas = TWCSVerifyUtils.getBizDatas(result);
			if (null == bizDatas || bizDatas.size() == 0) {
				logger.error("未找到需要读取的凭证信息");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "未找到需要读取的凭证信息");
			}
			// 根性索引表记录为 锁定状态
			List<IDto> infoList = (List<IDto>) bizDatas.get(0);
			for (IDto tmpDto : infoList) {
				((TvVoucherinfoDto) tmpDto)
						.setSext3(TwcsDealCodeConstants.VOUCHER_LOCKING);
			}
			DatabaseFacade.getODB().update(CommonUtil.listTArray(infoList));
			return VoucherUtil.base64Encode((String) bizDatas.get(1));
		} catch (JAFDatabaseException e) {
			logger.error("TbsImpl.readVoucherAgain[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (UnsupportedEncodingException e) {
			logger.error("TbsImpl.readVoucherAgain[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error("TbsImpl.readVoucherAgain[" + e.getMessage() + "]", e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} finally {
			if (null != sqlExecutor) {
				sqlExecutor.closeConnection();
			}
		}
	}

	/**
	 * 资金清算接口
	 * 
	 * @param vtCode
	 *            凭证编号
	 * @param treCode
	 *            国库代码
	 * @param vouDate
	 *            凭证日期
	 * @param vouNo
	 *            凭证编号
	 * @throws ITFEBizException
	 */
	public String fundLiquidation(String admDivCode, String vtCode,
			String treCode, String vouDate, String vouNo) {
		logger.info("TBS调用接口(fundLiquidation)参数admDivCode:" + admDivCode + "vtCode:"
				+ vtCode + "treCode:" + treCode + "vouDate:" + vouDate
				+ "vouNo:");
		SQLExecutor sqlExecutor = null;
		try {
			if (MsgConstant.VOUCHER_NO_5106.equals(vtCode)
					|| MsgConstant.VOUCHER_NO_5108.equals(vtCode)) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "清算额度信息不允许进行资金清算操作");
			}
			// 检验配置参数
			if (!(ITFECommonConstant.PUBLICPARAM.indexOf(",tbsmode=mode02,") >= 0)) {
				logger.error("代理库资金清算模式配置有误");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "代理库资金清算模式配置有误");
			}
			// 校验参数信息
			String str = TWCSVerifyUtils.verifyConfirmVoucherParam(admDivCode,
					vtCode, treCode, vouDate, vouNo);
			if (StringUtils.isNotBlank(str)) {
				logger.error(str);
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + str);

			}
			// 查询索引表信息
			TvVoucherinfoDto vDto = TWCSVerifyUtils.getTvVoucherinfoDto(treCode,
					vtCode, vouDate, vouNo, admDivCode);
			if (null == vDto) {
				logger.error("未找到对应的凭证信息");
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "未找到对应的凭证信息");
			}
			// 校验节点状态
//			str = TBSVerifyUtils.verifyNodeStatus(vDto.getSpaybankcode());
//			if (StringUtils.isNotBlank(str)) {
//				return VoucherUtil
//						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL + str);
//			}
//			if (!TbsDealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(vDto
//					.getSext1())) {
//				logger.error("只有状态为校验成功才可以清算");
//				return VoucherUtil
//						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
//								+ "只有状态为校验成功才可以清算");
//			}
			MuleMessage message = new DefaultMuleMessage("");
			MuleClient client = new MuleClient();
			message.setStringProperty(MessagePropertyKeys.MSG_NO_KEY,
					"TBS_1000");
			message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
			// message.setProperty("orgCode", vDto.getSorgcode());
			message = client.send("vm://ManagerMsgWithCommBank", message);
			vDto = TWCSVerifyUtils.getTvVoucherinfoDto(treCode, vtCode, vouDate,
					vouNo, admDivCode);
			String sql = "SELECT count(*) FROM TF_FUND_APPROPRIATION WHERE S_PACKNO = ? AND S_TRECODE = ? AND S_VOUNO = ? AND S_VOUDATE = ?";
			sqlExecutor = DatabaseFacade.getODB().getSqlExecutorFactory()
					.getSQLExecutor();
			sqlExecutor.addParam(vDto.getSpackno());
			sqlExecutor.addParam(vDto.getStrecode());
			sqlExecutor.addParam(vDto.getSvoucherno());
			sqlExecutor.addParam(vDto.getSext4());
			SQLResults sqlResults = sqlExecutor.runQuery(sql);
			if (sqlResults.getInt(0, 0) == 0) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "发送清算报文失败");
			} else {

				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
			}
		} catch (Exception e) {
			logger.error(e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} finally {
			if (null != sqlExecutor) {
				sqlExecutor.closeConnection();
			}
		}
	}

	/**
	 * 测试性连接
	 * 
	 * @throws ITFEBizException
	 */
	public void testConnect(String desOrgCode, String vouDate, String orgCode) {
		// TODO Auto-generated method stub
		try {
			logger.info("TBS调用接口(testConnect)参数desOrgCode:" + desOrgCode
					+ "vouDate:" + vouDate + "orgCode:" + orgCode);
			if (StringUtils.isBlank(desOrgCode) || StringUtils.isBlank(vouDate)
					|| StringUtils.isBlank(orgCode)) {
				throw new ITFEBizException(VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "接收机构或账务日期不能为空"));
			}
			HeadDto headdto = new HeadDto();
			headdto.set_VER(MsgConstant.MSG_HEAD_VER);
			headdto.set_SRC("111111111111");
			headdto.set_DES(desOrgCode);
			headdto.set_APP("TCQS");
			headdto.set_msgNo(MsgConstant.MSG_TBS_NO_3000);
			headdto.set_workDate(vouDate);
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headdto.set_msgID(msgid);
			headdto.set_msgRef(msgid);
			TvVoucherinfoDto vDto = new TvVoucherinfoDto();
			vDto.setSpaybankcode(desOrgCode);
			// 发送报文
			MuleClient client = new MuleClient();
			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
					MsgConstant.MSG_TBS_NO_3000 + "_OUT");
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
			message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE, orgCode);
			message.setPayload(map);
			message = client.send("vm://ManagerMsgWithCommBank", message);
		} catch (Exception e) {
			logger.error(e);
			// return
			// TBSVerifyUtils.base64Encode(TbsDealCodeConstants.OPERATION_FAIL +
			// e.getMessage());
		}
	}

	public String userLoginOrOut(String orgCode, String password, String operate) {
		try {
			logger.info("接收(userLoginOrOut)参数orgCode:" + orgCode + "password:"
					+ password + "operate:" + operate);
			if (StringUtils.isBlank(orgCode)) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "机构代码不能为空");
			}
			Pattern p = Pattern.compile("[0-9]{12}");
			Matcher m = p.matcher(orgCode);
			if (!m.matches()) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "机构代码必须为12为数字");
			}
			if (StringUtils.isBlank(password)) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "密码不能为空");
			} else if (!("00000000".equals(password))) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "密码错误");
			}
			if (StringUtils.isBlank(operate)) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "操作类型不能为空");
			} else if (operate.length() != 1) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "操作类型长度为1为：0登陆1退出");
			} else if (!("0".equals(operate) || "1".equals(operate))) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "操作类型长度为1为：0登陆1退出");
			}
			TsTbsorgstatusDto tsTbsorgstatusDto = new TsTbsorgstatusDto();
			tsTbsorgstatusDto.setSorgcode(orgCode);
			tsTbsorgstatusDto.setSpassword(password);
			List resultList = CommonFacade.getODB().findRsByDto(
					tsTbsorgstatusDto);
			if (null == resultList || resultList.size() == 0) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "该机构未找到");
			}
			tsTbsorgstatusDto = (TsTbsorgstatusDto) resultList.get(0);
			tsTbsorgstatusDto.setSstatus(operate);
			DatabaseFacade.getODB().update(tsTbsorgstatusDto);
			return VoucherUtil
					.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error(e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}
	}

	/**
	 * 
	 * @param voucherType
	 *            报文类型编号
	 * @param fileContent
	 *            文件内容
	 * @return
	 * @throws ITFEBizException
	 */
	public String genVoucherTKXml(String voucherType, String fileContent) {
		// TODO Auto-generated method stub
		logger.info("TBS调用接口(genVoucherXml)参数voucherType:" + voucherType
				+ "fileContent:" + fileContent);
		try {
			String dirsep = File.separator;
			if (StringUtils.isBlank(voucherType)
					|| StringUtils.isBlank(fileContent)) {
				return VoucherUtil
						.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
								+ "报文类型编号或内容不能为空");
			}
//			String FileName = ITFECommonConstant.FILE_ROOT_PATH
//					+ dirsep
//					+ "Voucher"
//					+ dirsep
//					+ TimeFacade.getCurrentStringTime()
//					+ dirsep
//					+ "rev"
//					+ voucherType
//					+ "_"
//					+ new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System
//							.currentTimeMillis()) + ".msg";
//			FileUtil.getInstance().writeFile(FileName, fileContent);
//			if (!(voucherType.equals(MsgConstant.VOUCHER_NO_3208) || MsgConstant.VOUCHER_NO_5209
//					.equals(voucherType))) {
//				return VoucherUtil
//						.base64Encode(TbsDealCodeConstants.OPERATION_FAIL
//								+ "报文类型编号错误");
//			}
			// 解析接口 获取总记录数
			// int count = Integer.valueOf(fileContent.substring(2, fileContent
			// .indexOf("**")));
			// 获取主单和明细信息
			// String[] fileContents = fileContent.substring(
			// fileContent.indexOf("**")).split("\\*\\*");
			String[] fileContents = new String[1];
			fileContents[0] = fileContent;
			// if (count != (fileContents.length - 1)) {
			// return VoucherUtil
			// .base64Encode(TbsDealCodeConstants.OPERATION_FAIL
			// + "记录总数匹配有误");
			// }
			return TWCSVerifyUtils.jxVoucherByType(voucherType, fileContents);
			// if (StringUtils.isNotBlank(result)) {
			// return VoucherUtil
			// .base64Encode(TbsDealCodeConstants.OPERATION_FAIL
			// + result);
			// }
			//
			// return VoucherUtil
			// .base64Encode(TbsDealCodeConstants.OPERATION_SUCCESS);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		} catch (Exception e) {
			logger.error(e);
			// throw new ITFEBizException(e.getMessage(), e);
			return TWCSVerifyUtils
					.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL
							+ e.getMessage());
		}
	}

	/**
	 * tbs的参数同步到ITFE(科目代码，法人代码)
	 * @param paraType 代表是科目代码还是法人代码 0--科目代码   1--法人代码
	 * @param paramContent 同步的数据
	 * @return 是否成功 0:操作成功 -1:操作失败
	 */
	public String synchronousParam(String orgCode, String paraType, String paramContent) {
		logger.info("TBS调用接口(synchronousParam)参数orgCode:" + orgCode + "paraType:" + paraType
				+ "paramContent:" + paramContent);
		if(paraType == null || paraType.trim().equals("")){
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"-参数类型paraType不能为空");
		}
		if(paramContent == null || paramContent.trim().equals("")){
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"参数内容paramContent不能为空");
		}
		String[] datas = paramContent.substring(paramContent.indexOf("**") + 2).split("\\*\\*");
		TsBudgetsubjectDto bsDto = null;
		TdCorpDto  corpDto = null;
		List paramList = new ArrayList();
		StringBuffer sberror = new StringBuffer();
		if(paraType.equals("0")){ // 0--科目代码
			for(String str:datas){
				bsDto = new TsBudgetsubjectDto();
				String[] columns = str.split(",");
				bsDto.setSorgcode(columns[0]);
				bsDto.setSsubjectcode(columns[1]);
				bsDto.setSsubjectname(columns[2]);
				bsDto.setSsubjectclass(columns[3]);
				bsDto.setSsubjecttype(columns[4]);
				bsDto.setSsubjectattr(columns[5]);
				bsDto.setSinoutflag(columns[6]);
				bsDto.setSwriteflag(columns[7]);
				bsDto.setSmoveflag(columns[8].equals("null")?"":columns[8]);
				bsDto.setSbudgettype(columns[9]);
				bsDto.setSclassflag(columns[10]);
				bsDto.setSdrawbacktype(columns[11]);
				paramList.add(bsDto);
			}
		}else if(paraType.equals("1")){ // 1--法人代码
			Calendar cal = Calendar.getInstance();
			for(String str:datas){
				corpDto = new TdCorpDto();
				String[] columns = str.split(",");
				corpDto.setSbookorgcode(columns[0]);
				corpDto.setStrecode(columns[1]);
				corpDto.setScorpcode(columns[2]);
				corpDto.setCtrimflag(columns[3]);
				corpDto.setScorpname(columns[4]);
				corpDto.setScorpsht(columns[5]);
				corpDto.setCmayaprtfund(columns[6]);
				corpDto.setCpayoutprop("N");
				corpDto.setCtaxpayerprop(columns[7]);
				corpDto.setCtradeprop("N");
				corpDto.setTssysupdate(new Timestamp(cal.getTimeInMillis()));
				paramList.add(corpDto);
			}
		}else{
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"无法同步未知的参数！");
		}
		String sql = "";
		if(paraType.equals("0")){ // 0--科目代码
			sql = "DELETE FROM TS_BUDGETSUBJECT WHERE S_ORGCODE = '" + orgCode + "'";
			try {
				SQLExecutor sqlEx = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				sqlEx.runQueryCloseCon(sql);
				DatabaseFacade.getODB().create((TsBudgetsubjectDto[])paramList.toArray(new TsBudgetsubjectDto[paramList.size()]));
				SrvCacheFacade.reloadBuffer(orgCode,StateConstant.CacheBdgSbt);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			} catch (ITFEBizException e) {
				logger.error(e);
				return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			}
		}
		if(paraType.equals("1")){ // 1--法人代码
			sql = "DELETE FROM TD_CORP WHERE S_BOOKORGCODE = '" + orgCode + "'";
			try {
				SQLExecutor sqlEx = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				sqlEx.runQueryCloseCon(sql);
				DatabaseFacade.getODB().create((TdCorpDto[])paramList.toArray(new TdCorpDto[paramList.size()]));
				SrvCacheFacade.reloadBuffer(orgCode,StateConstant.CacheTDCrop);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			} catch (ITFEBizException e) {
				logger.error(e);
				return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			}
		}
		return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
	}

	/**
	 * tbs的报表数据同步到ITFE(收入，支出)
	 * @param orgCode 核算主体代码
	 * @param paraType 代表一个占位符
	 * @param paramContent 同步的数据
	 * @return 是否成功 0:操作成功 -1:操作失败
	 */
	public String synchronousIncomeDayRpt(String orgCode, String paraType,
			String paramContent) {
		logger.info("TBS调用接口(synchronousIncomeDayRpt)orgCode:" + orgCode + "paraType:" + paraType
				+ "paramContent:" + paramContent);
		if(paraType == null || paraType.trim().equals("")){
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"-报表数据类型paraType不能为空");
		}
		if(paramContent == null || paramContent.trim().equals("")){
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"报表数据paramContent不能为空");
		}
		String[] strArry = paramContent.split("@");
		String incomecontent = strArry[0];
		String payoutcontent = strArry[1];
		String[] datas1 = incomecontent.substring(incomecontent.indexOf("**") + 2).split("\\*\\*");
		String[] datas2 = payoutcontent.substring(payoutcontent.indexOf("**") + 2).split("\\*\\*");
		TrIncomedayrptDto bsDto = null;//收入表dto
		TrTaxorgPayoutReportDto  corpDto = null;//支出表dto
		List paramList1 = new ArrayList();
		List paramList2 = new ArrayList();
		StringBuffer sberror = new StringBuffer();
		if(paraType.equals("0")){
			// 收入
			for(String str:datas1){
				bsDto = new TrIncomedayrptDto();
				String[] columns = str.split(",");
				bsDto.setSfinorgcode(orgCode);//财政机关代码
				bsDto.setStaxorgcode(columns[3]);//征收机关代码
				bsDto.setStrecode(columns[2]);//国库代码
				bsDto.setSrptdate(columns[1]);//srptdate
				bsDto.setSbudgettype(columns[4]);//预算种类
				bsDto.setSbudgetlevelcode(columns[5]);//预算级次代码 
				bsDto.setSbudgetsubcode(columns[6]);//预算科目
				bsDto.setSbudgetsubname(columns[7]);//预算科目名称
				bsDto.setNmoneyday(new BigDecimal(columns[8]));//日累计金额
				bsDto.setNmoneytenday(new BigDecimal(columns[9]));//旬累计金额
				bsDto.setNmoneymonth(new BigDecimal(columns[10]));//月累计金额
				bsDto.setNmoneyquarter(new BigDecimal(columns[11]));//季累计金额
				bsDto.setNmoneyyear(new BigDecimal(columns[12]));//年累计金额
				bsDto.setSbelongflag(columns[13]);//辖属标志
				bsDto.setStrimflag(columns[14]);//调整期标志
				bsDto.setSdividegroup(columns[15]);//分成组标志 
				bsDto.setSbillkind(columns[16]);//报表种类 
				paramList1.add(bsDto);
			}
			// 支出
			Calendar cal = Calendar.getInstance();
			for(String str:datas2){
				corpDto = new TrTaxorgPayoutReportDto();
				String[] columns = str.split(",");
				corpDto.setSfinorgcode(orgCode);//财政机关代码
				corpDto.setStaxorgcode(columns[3]);//征收机关代码
				corpDto.setStrecode(columns[2]);//国库代码
				corpDto.setSrptdate(columns[1]);//srptdate
				corpDto.setSbudgettype(columns[4]);//预算种类
				corpDto.setSbudgetlevelcode(columns[5]);//预算级次代码 
				corpDto.setSbudgetsubcode(columns[6]);//预算科目
				corpDto.setSbudgetsubname(columns[7]);//预算科目名称
				corpDto.setNmoneyday(new BigDecimal(columns[8]));//日累计金额
				corpDto.setNmoneytenday(new BigDecimal(columns[9]));//旬累计金额
				corpDto.setNmoneymonth(new BigDecimal(columns[10]));//月累计金额
				corpDto.setNmoneyquarter(new BigDecimal(columns[11]));//季累计金额
				corpDto.setNmoneyyear(new BigDecimal(columns[12]));//年累计金额
				paramList2.add(corpDto);
			}
		}else{
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"无法同步未知的数据！");
		}
		String sql1 = "";
		String sql2 = "";
		if(paraType.equals("0")){
			sql1 = "DELETE FROM TR_INCOMEDAYRPT WHERE S_FINORGCODE = '" + orgCode + "'";
			sql2 = "DELETE FROM TR_TAXORG_PAYOUT_REPORT WHERE S_FINORGCODE = '" + orgCode + "'";
			try {
				SQLExecutor sqlEx = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				sqlEx.runQueryCloseCon(sql1);
				sqlEx.runQueryCloseCon(sql2);
				DatabaseFacade.getODB().create((TrIncomedayrptDto[])paramList1.toArray(new TrIncomedayrptDto[paramList1.size()]));
				DatabaseFacade.getODB().create((TrTaxorgPayoutReportDto[])paramList2.toArray(new TrTaxorgPayoutReportDto[paramList2.size()]));
			} catch (JAFDatabaseException e) {
				logger.error(e);
				return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			}
		}
		return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
	}

	/**
	 * tbs的库存同步到ITFE(库存)
	 * @param orgCode 核算主体代码
	 * @param paraType 代表一个占位符为0
	 * @param paramContent 同步的数据
	 * @return 是否成功 0:操作成功 -1:操作失败
	 */
	public String synchronousStockRpt(String orgCode, String paraType,
			String paramContent) {
		logger.info("TBS调用接口(synchronousStockRpt)orgCode:" + orgCode + "paraType:" + paraType
				+ "paramContent:" + paramContent);
		if(paraType == null || paraType.trim().equals("")){
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"-参数类型paraType不能为空");
		}
		if(paramContent == null || paramContent.trim().equals("")){
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"库存报表数据paramContent不能为空");
		}
		String[] datas = paramContent.substring(paramContent.indexOf("**") + 2).split("\\*\\*");
		TrStockdayrptDto bsDto = null;
		List paramList = new ArrayList();
		StringBuffer sberror = new StringBuffer();
		if(paraType.equals("0")){ // 0
			for(String str:datas){
				bsDto = new TrStockdayrptDto();
				String[] columns = str.split(",");
				bsDto.setSorgcode(orgCode);//财政机构代码 S_ORGCODE
				bsDto.setStrecode(columns[1]);//国库代码 S_TRECODE
				bsDto.setSrptdate(columns[0]);//S_RPTDATE
				bsDto.setSaccno(columns[2]);//帐户代码 S_ACCNO
				bsDto.setSaccname(columns[3]);//帐户名称 S_ACCNAME
				bsDto.setSaccdate("");//帐户日期 S_ACCDATE
				bsDto.setNmoneyyesterday(new BigDecimal(columns[4]));//上日余额 N_MONEYYESTERDAY
				bsDto.setNmoneyin(new BigDecimal(columns[5]));//本日收入 N_MONEYIN
				bsDto.setNmoneyout(new BigDecimal(columns[6]));//本日支出 N_MONEYOUT
				bsDto.setNmoneytoday(new BigDecimal(columns[7]));//本日余额 N_MONEYTODAY

				paramList.add(bsDto);
			}
		
		}else{
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL+"无法同步未知的数据！");
		}
		String sql = "";
		if(paraType.equals("0")){ // 0
			sql = "DELETE FROM TR_STOCKDAYRPT WHERE S_ORGCODE = '" + orgCode + "'";
			try {
				SQLExecutor sqlEx = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				sqlEx.runQueryCloseCon(sql);
				DatabaseFacade.getODB().create((TrStockdayrptDto[])paramList.toArray(new TrStockdayrptDto[paramList.size()]));
				
			} catch (JAFDatabaseException e) {
				logger.error(e);
				return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
			}
		}
		return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
	}

	/**
	 * 将对账明细同步到tbs中
	 * @param strecode 国库代码
	 * @param checkDate 对账日期
	 * @param checkNum 对账次数
	 */
	public String synchronousAcctBalance(String strecode, String checkDate, String checkNum) {
		logger.info("TBS调用接口(synchronousAcctBalance)strecode:" + strecode + "checkDate:" + checkDate
				+ "checkNum:" + checkNum);
		StringBuffer sbuf = new StringBuffer();
		try {
			List<TfReconciliationDto> recList = null;
			TfReconciliationDto recDto = new TfReconciliationDto();
			recDto.setStrecode(strecode);
			recDto.setSchkdate(checkDate);
			recList = CommonFacade.getODB().findRsByDtoWithUR(recDto);
			if(recList==null || recList.size()==0){
				return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL);
			}
			
			//判断对账次数和tbs已同步过去的对账数据的对账次数是否相同，如果相同，不需要再同步数据(前置的对账信息表中取最大的对账次数与tbs中已同步过去的对账次数相比)
			Integer chkNo = recList.get(0).getIpackno();
			for(int i=1;i<recList.size();i++){
				if(recList.get(i).getIpackno()>chkNo){
					chkNo = recList.get(i).getIpackno();
				}
			}
			if(checkNum != null && !checkNum.trim().equals("") && Integer.parseInt(checkNum)==chkNo){
				return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL);
			}
			
			//对账中前置缺少的退款通知信息，只有接收的对账信息表里有记录，要把结果同步到tbs
			String sql = "select S_TRECODE,S_PACKNO,N_CURPACKVOUAMT,S_PAYOUTVOUTYPE,S_EXT1 " +
					"from TF_RECONCILIATION where S_TRECODE = ? and S_CHKDATE = ? and S_EXT1 = '3' " +
					"and I_PACKNO = (SELECT max(I_PACKNO) FROM TF_RECONCILIATION)";
			SQLExecutor sqlExe = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExe.addParam(strecode);
			sqlExe.addParam(checkDate);
			SQLResults rs = sqlExe.runQueryCloseCon(sql);
			if(rs != null && rs.getRowCount()>0){
				for(int i=0;i<=rs.getRowCount();i++){
					sbuf.append("**");
					sbuf.append(rs.getString(i, "S_TRECODE") + ","); //国库代码
					sbuf.append(rs.getString(i, "S_PACKNO") + ","); //包号作为凭证编号
					sbuf.append(rs.getDouble(i, "N_CURPACKVOUAMT") + ","); //发生额
//					sbuf.append(rs.getString(i, "S_PAYOUTVOUTYPE") + ","); //业务类型
					sbuf.append("35,"); //业务类型--退库通知，业务类型写成35
					sbuf.append(rs.getString(i, "S_TRECODE") + ","); //付款银行代码
					sbuf.append(checkDate + ","); //对账日期
					sbuf.append(rs.getString(i, "S_EXT1") + ","); //对账结果
					sbuf.append(recList.get(0).getIpackno()); //对账次数
				}
			}
			
			TfFundAppropriationDto appropriationDto = new TfFundAppropriationDto();
			appropriationDto.setSentrustdate(checkDate);
			appropriationDto.setStrecode(strecode);
			List<TfFundAppropriationDto> fundList = null;
			fundList = CommonFacade.getODB().findRsByDtoWithUR(appropriationDto);
			if (null == fundList || fundList.size() == 0) {//如果当天没有资金拨付，则没有和代理行发生业务
				return VoucherUtil.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL);
			}
			
			for(int i=0;i<fundList.size();i++){
				appropriationDto =  fundList.get(i);
				sbuf.append("**");
				sbuf.append(appropriationDto.getStrecode() + ","); //国库代码
				sbuf.append(appropriationDto.getSvouno() + ","); //凭证编号
				sbuf.append(appropriationDto.getNallamt() + ","); //发生额
				if(appropriationDto.getSpayoutvoutype().equals("1")){//实拨，退库
					sbuf.append("17,"); //业务类型
					sbuf.append(appropriationDto.getStrecode() + ","); //银行代码 填国库代码
				}else if(appropriationDto.getSpayoutvoutype().equals("2")){
					sbuf.append("13,"); //业务类型
					sbuf.append(appropriationDto.getStrecode() + ","); //银行代码 填国库代码
				}else if(appropriationDto.getSpayoutvoutype().equals("3")){//集中支付
					sbuf.append("25,"); //业务类型       25-直接支付   27-授权支付     暂写25
					sbuf.append(appropriationDto.getSpayeeopbkno() + ",");
				}else{
					sbuf.append("N,"); //业务类型
					sbuf.append("N,"); //付款银行代码
				}
				sbuf.append(checkDate + ","); //对账日期
				sbuf.append(((appropriationDto.getSext1()==null || appropriationDto.getSext1().equals(""))?"0":appropriationDto.getSext1()) + ","); //对账结果
				sbuf.append(recList.get(0).getIpackno()); //对账次数
			}
			
			List<TfRefundNoticeDto> refundList = null;
			TfRefundNoticeDto refundDto = new TfRefundNoticeDto();
			refundDto.setStrecode(strecode);
			refundDto.setSentrustdate(checkDate);
			refundList = CommonFacade.getODB().findRsByDtoWithUR(refundDto);
			if(refundList != null && refundList.size()>0){
				for(int i=0;i<refundList.size();i++){
					refundDto = refundList.get(i);
					sbuf.append("**");
					sbuf.append(refundDto.getStrecode() + ","); //国库代码
					sbuf.append(refundDto.getSvouno() + ","); //凭证编号
					sbuf.append(refundDto.getNallamt() + ","); //发生额
					sbuf.append("35,"); //业务类型
					sbuf.append(refundDto.getSpayerbankno() + ","); //付款银行代码
					sbuf.append(checkDate + ","); //对账日期
					sbuf.append(((refundDto.getSext1()==null || refundDto.getSext1().equals(""))?"0":refundDto.getSext1()) + ","); //对账结果
					sbuf.append(recList.get(0).getIpackno()); //对账次数
				}
			}
			
			//对账结果同步完成后，将明细的状态恢复，再次对账使用
			String fundSql = "update TF_FUND_APPROPRIATION set S_EXT1 = '' where S_TRECODE = ? and S_ENTRUSTDATE = ?";
			String checkSql = "update TF_RECONCILIATION set S_EXT1 = '' where S_TRECODE = ? and S_CHKDATE = ?";
			String refundSql = "update TF_REFUND_NOTICE set S_EXT1 = '' where S_TRECODE = ? and S_ENTRUSTDATE = ?";
			SQLExecutor sqlExe1 = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sqlExe1.addParam(strecode);
			sqlExe1.addParam(checkDate);
			sqlExe1.runQuery(fundSql);
			
			sqlExe1.clearParams();
			sqlExe1.addParam(strecode);
			sqlExe1.addParam(checkDate);
			sqlExe1.runQuery(checkSql);
			
			sqlExe1.clearParams();
			sqlExe1.addParam(strecode);
			sqlExe1.addParam(checkDate);
			sqlExe1.runQueryCloseCon(refundSql);
		} catch (Exception e) {
			logger.error(e);
			return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		}
		
		return TWCSVerifyUtils.base64Encode(sbuf.toString());
	}
	
	/**
	 * 更新无纸化凭证和索引表的清算状态
	 * @param strecode 国库代码
	 * @param checkDate 对账日期
	 * @param checkNum 对账次数
	 */
	public String updateItfeVoucherInfo(String vtCode, String msgInfo) {
		SQLExecutor  updateExce = null;
		try {
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String[] str  = msgInfo.split("####\\*\\*\\*\\*");
			if (str.length> 0) {
				for (String str1 : str) {
					String[] strcol  = str1.split("\\|\\|");
					updateExce.clearParams();
					//字符串组合：凭证类型 +凭证流水号+清算状态+附言+清算金额+账务日期+支付发起行行号
					if (MsgConstant.VOUCHER_NO_2301.equals(vtCode)) {
						String updateSql = "update TV_PAYRECK_BANK  set S_RESULT = ? , S_ADDWORD = ? ,D_ACCTDATE= ? ,S_XPAYAMT = ? , S_XCLEARDATE = ? ,S_XPAYSNDBNKNO = ?  where I_VOUSRLNO = ?  and S_RESULT = ? ";
						updateExce.addParam(strcol[2]);//清算状态
						updateExce.addParam(strcol[3]);//附言
						updateExce.addParam(java.sql.Date.valueOf(strcol[5]));//账务日期
						updateExce.addParam(new BigDecimal(strcol[4]));//清算金额
						updateExce.addParam(java.sql.Date.valueOf(strcol[5]));//清算日期
						updateExce.addParam(strcol[6]);//支付发起行行号
						updateExce.addParam(Long.valueOf(strcol[1]));//凭证流水号
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// 处理中状态
						updateExce.runQuery(updateSql);
					}
					//字符串组合：凭证类型 +凭证流水号+清算状态+附言+清算金额+账务日期+支付发起行行号
					if (MsgConstant.VOUCHER_NO_2302.equals(vtCode)) {
						String updateSql = "update TV_PAYRECK_BANK_BACK  set S_STATUS = ? , S_ADDWORD = ? ,S_XPAYAMT = ? ,S_XCLEARDATE = ? ,S_XPAYSNDBNKNO = ?  where I_VOUSRLNO = ?  and S_STATUS = ? ";
						updateExce.addParam(strcol[2]);//清算状态
						updateExce.addParam(strcol[3]);//附言
						updateExce.addParam(new BigDecimal(strcol[4]));//清算金额
						updateExce.addParam(java.sql.Date.valueOf(strcol[5]));//清算日期
						updateExce.addParam(strcol[6]);//支付发起行行号
						updateExce.addParam(Long.valueOf(strcol[1]));//凭证流水号
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// 处理中状态
						updateExce.runQuery(updateSql);
					}
					if (MsgConstant.VOUCHER_NO_5108.equals(vtCode)) {
					 //字符串组合：凭证类型 +凭证流水号+清算状态+附言+账务日期
					  String updateSql =	"update TV_DIRECTPAYMSGMAIN set S_STATUS = ? , S_DEMO = ? ,S_ACCDATE = ?   where I_VOUSRLNO = ? AND S_STATUS =?";
						updateExce.addParam(strcol[2]);//清算状态
						updateExce.addParam(strcol[3]);//附言
						updateExce.addParam(strcol[4]);//清算日期
						updateExce.addParam(Long.valueOf(strcol[1]));//凭证流水号
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// 处理中状态
						updateExce.runQuery(updateSql);
					}
					//字符串组合：凭证类型 +凭证流水号+清算状态+附言+账务日期+支付金额+交易流水号+委托日期
					if (MsgConstant.VOUCHER_NO_5106.equals(vtCode)) {
						String updateSql =	"update TV_GRANTPAYMSGMAIN set S_STATUS = ? , S_DEMO = ? ,S_ACCDATE = ? ,N_XALLAMT=?  where I_VOUSRLNO = ? AND S_STATUS =?";
						updateExce.addParam(strcol[2]);//清算状态
						updateExce.addParam(strcol[3]);//附言
						updateExce.addParam(strcol[4]);//清算日期
						updateExce.addParam(new BigDecimal(strcol[5]));//支付金额
						updateExce.addParam(Long.valueOf(strcol[1]));//凭证流水号
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// 处理中状态
						if(VoucherUtil.updateVoucherSplitReceiveTCBS(strcol[6], strcol[2], new BigDecimal(strcol[5]), strcol[4], strcol[7], strcol[3])){
							continue;
						}
						updateExce.runQuery(updateSql);	
					}
					if (MsgConstant.VOUCHER_NO_5207.equals(vtCode)) {
						//字符串组合：凭证类型 +凭证流水号+清算状态+附言+清算金额+银行流水号
						String updateSql =	"update TV_PAYOUTMSGMAIN set S_STATUS = ? , S_DEMO = ? , S_XPAYAMT = ? , S_XPAYDATE = ? , S_XAGENTBUSINESSNO = ?   where S_BIZNO = ? AND S_STATUS =?";
						updateExce.addParam(strcol[2]);//清算状态
						updateExce.addParam(strcol[3]);//附言
						updateExce.addParam(new BigDecimal(strcol[4]));//清算金额
						updateExce.addParam(strcol[5]);//清算日期
						updateExce.addParam(strcol[6]);//银行流水号
						updateExce.addParam(Long.valueOf(strcol[1]));//凭证流水号
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// 处理中状态
						updateExce.runQuery(updateSql);
					}
					if (MsgConstant.VOUCHER_NO_5209.equals(vtCode)) {
						//字符串组合：凭证类型 +凭证流水号+清算状态+附言+清算金额
						String updateSql =	"update TV_DWBK set S_STATUS = ? , S_DEMO = ? ,XPAYAMT=?   where I_VOUSRLNO = ? AND S_STATUS =?";
						updateExce.addParam(strcol[2]);//清算状态
						updateExce.addParam(strcol[3]);//附言
						updateExce.addParam(new BigDecimal(strcol[4]));//清算金额
						updateExce.addParam(Long.valueOf(strcol[1]));//凭证流水号
						updateExce.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING);// 处理中状态
						updateExce.runQuery(updateSql);
					}
					//更新索引表状态
					updateInfoFacade.VoucherReceiveTWCS(strcol[1], strcol[2], strcol[3]);
				}
			}
		} catch (JAFDatabaseException e) {
			logger.error(e, e);
			TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		} catch (Exception e) {
			logger.error(e, e);
			TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_FAIL + e.getMessage());
		}finally{
			updateExce.closeConnection();
		}
		return TWCSVerifyUtils.base64Encode(TwcsDealCodeConstants.OPERATION_SUCCESS);
	}
	
}

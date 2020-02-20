package com.cfcc.itfe.facade;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustmainDto;
import com.cfcc.itfe.persistence.dto.TfGrantpayAdjustsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailssubDto;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttobankDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsSystemDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvNontaxsubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.TvPbcpaySubDto;
import com.cfcc.itfe.persistence.pk.TsConverttobankPK;
import com.cfcc.itfe.persistence.pk.TsInfoconnorgPK;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;
@SuppressWarnings("unchecked")
public class PublicSearchFacade {

	private static Log logger = LogFactory.getLog(PublicSearchFacade.class);

	/**
	 * 转化文件名称 - 支持模糊查询
	 * 
	 * @param whereStr
	 *            其他查询条件
	 * @param billno
	 *            票据编号
	 * @return
	 */
	public static String changeFileName(String whereStr, String filename) {
		// 票据编号条件
		String filenameStr = null;
		if (null != filename && !"".equals(filename.trim())) {
			filenameStr = " (S_FILENAME like '" + filename
					+ "%' or S_FILENAME like '" + filename.toLowerCase()
					+ "%') ";
		}

		if (null == filenameStr
				&& (null == whereStr || "".equals(whereStr.trim()))) {
			return null;
		} else {
			if (null == filenameStr) {
				return whereStr;
			} else if (null == whereStr || "".equals(whereStr.trim())) {
				return filenameStr;
			} else {
				return filenameStr + " and " + whereStr;
			}
		}
	}

	/**
	 * 根据报文编号找到对应的业务类型
	 * 
	 * @param String
	 *            smsgno 报文编号
	 * @return
	 */
	public static String getBizTypeByMsgNo(String smsgno)
			throws ITFEBizException {
		if (MsgConstant.MSG_NO_1103.equals(smsgno)
				|| MsgConstant.MSG_NO_7211.equals(smsgno)) {
			return BizTypeConstant.BIZ_TYPE_INCOME;
		} else if (MsgConstant.MSG_NO_5101.equals(smsgno)) {
			return BizTypeConstant.BIZ_TYPE_PAY_OUT;
		} else if (MsgConstant.MSG_NO_5102.equals(smsgno)) {
			return BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN;
		} else if (MsgConstant.MSG_NO_5103.equals(smsgno)) {
			return BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN;
		} else if (MsgConstant.MSG_NO_3131.equals(smsgno)){
			return BizTypeConstant.BIZ_TYPE_PAY_OUT;
		} else if ( MsgConstant.MSG_NO_3133.equals(smsgno)){
			return BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN;
		}else if ( MsgConstant.MSG_NO_3134.equals(smsgno)) {
			return BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN;
		}else if (MsgConstant.MSG_NO_5112.equals(smsgno)) {
			return BizTypeConstant.BIZ_TYPE_BATCH_OUT;
		} else if (MsgConstant.MSG_NO_1104.equals(smsgno)) {
			return BizTypeConstant.BIZ_TYPE_RET_TREASURY;
		} else if (MsgConstant.MSG_NO_1105.equals(smsgno)) {
			return BizTypeConstant.BIZ_TYPE_CORRECT;
		} else if (MsgConstant.MSG_NO_5104.equals(smsgno)) {
			return BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY;
		} else if(MsgConstant.MSG_NO_3146.equals(smsgno)){
			return BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY_BACK;
		} else if (MsgConstant.MSG_NO_2201.equals(smsgno)) {
			return BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY;
		} else if(MsgConstant.MSG_NO_2202.equals(smsgno)){
			return BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK;
		}
		logger.error("没有找到报文编号[" + smsgno + "]对应的业务类型!");
		return smsgno;
	}

	/**
	 * 根据业务类型找到对应的报文呢编号
	 * 
	 * @param String
	 *            biztype 业务类型
	 * @return
	 * @throws ITFEBizException
	 */
	public static String getMsgNoByBizType(String biztype)
			throws ITFEBizException {
		if (BizTypeConstant.BIZ_TYPE_INCOME.equals(biztype)) {
			return MsgConstant.MSG_NO_7211;
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(biztype)) {
			return MsgConstant.MSG_NO_5101;
		} else if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(biztype)) {
			return MsgConstant.MSG_NO_5102;
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(biztype)) {
			return MsgConstant.MSG_NO_5103;
		} else {
			return biztype;
		}
	}

	/**
	 * 根据TIPS的处理结果查转化应包交易状态
	 * 
	 * @param String
	 *            result 处理结果
	 * @return
	 */
	public static String getPackageStateByDealCode(String result) {

		if (!DealCodeConstants.DEALCODE_TIPS_SUCCESS.equals(result)) {
			return result;
		}

		return DealCodeConstants.DEALCODE_ITFE_RECEIVER;
	}

	/**
	 * 根据TIPS的处理结果查转化应明细交易状态
	 * 
	 * @param String
	 *            result 处理结果
	 * @return
	 */
	public static String getDetailStateByDealCode(String result) {

		if ((!DealCodeConstants.DEALCODE_TIPS_SUCCESS.equals(result)) && (!DealCodeConstants.DEALCODE_DWBK_SUCCESS.equals(result))  && (!StateConstant.COMMON_NO.equals(result)) ) {
			return DealCodeConstants.DEALCODE_ITFE_FAIL;
		}

		return DealCodeConstants.DEALCODE_ITFE_SUCCESS;
	}

	/**
	 * 根据机构代码和财政局标志查找财政局代码DTO
	 * 
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            sfinflag 财政局标志
	 * @return
	 * @throws ITFEBizException
	 */
	public static TsConvertfinorgDto findFinOrgDto(String sorgcode,
			String sfinflag) throws ITFEBizException {
		TsConvertfinorgDto finorgdto =new TsConvertfinorgDto();
		finorgdto.setSorgcode(sorgcode);
		finorgdto.setSfinflag(sfinflag);

		try {
			List<TsConvertfinorgDto> list = CommonFacade.getODB().findRsByDto(finorgdto);
			if(list==null||list.size()<=0)
				throw new ITFEBizException("机构下财政局标志参数没维护,"+sorgcode+"-"+sfinflag+"!");
			else
				return list.get(0);
		} catch (JAFDatabaseException e) {
			logger.error("查询财政局标志参数表发生数据库异常!", e);
			throw new ITFEBizException("查询财政局标志参数表发生数据库异常!", e);
		} catch (ValidateException e) {
			logger.error("查询财政局标志参数表发生数据库异常!", e);
			throw new ITFEBizException("查询财政局标志参数表发生数据库异常!", e);
		}
	}

	/**
	 * 根据财政代码找到财政代码参数对象
	 * 
	 * @param String
	 *            sfinorgcode 财政代码
	 * @return
	 * @throws ITFEBizException
	 */
	public static TsConvertfinorgDto findFinOrgDto(String sfinorgcode)
			throws ITFEBizException {
		TsConvertfinorgDto dto = new TsConvertfinorgDto();
		dto.setSfinorgcode(sfinorgcode);

		try {
			List<TsConvertfinorgDto> list = CommonFacade.getODB().findRsByDto(
					dto);
			if (null == list || list.size() == 0) {
				logger.error("在财政局标志参数表中没有找到对应的记录[" + sfinorgcode + "]!");
				throw new ITFEBizException("在财政局标志参数表中没有找到对应的记录[" + sfinorgcode
						+ "]!");
			}

			return list.get(0);
		} catch (JAFDatabaseException e) {
			logger.error("查询财政局标志参数表发生数据库异常!", e);
			throw new ITFEBizException("查询财政局标志参数表发生数据库异常!", e);
		} catch (ValidateException e) {
			logger.error("查询财政局标志参数表发生数据库异常!", e);
			throw new ITFEBizException("查询财政局标志参数表发生数据库异常!", e);
		}
	}

	/**
	 * 查找联网机构密钥参数对象
	 * 
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            sfinorgcode 财政代码
	 * @return
	 * @throws ITFEBizException
	 */
	public static TsInfoconnorgDto findOrgKeyDto(String sorgcode,
			String sfinorgcode) throws ITFEBizException {
		TsInfoconnorgPK _pk = new TsInfoconnorgPK();
		_pk.setSconnorgcode(sfinorgcode);
		_pk.setSorgcode(sorgcode);

		try {
			IDto dto = DatabaseFacade.getDb().find(_pk);
			if (null == dto) {
				logger.error("在联网机构密钥参数表中没有找到对应的记录[" + sorgcode + "_"
						+ sfinorgcode + "]!");
				throw new ITFEBizException("在联网机构密钥参数表中没有找到对应的记录[" + sorgcode
						+ "_" + sfinorgcode + "]!");
			}

			return (TsInfoconnorgDto) dto;
		} catch (JAFDatabaseException e) {
			logger.error("查询联网机构密钥参数表发生数据库异常!", e);
			throw new ITFEBizException("查询联网机构密钥参数表发生数据库异常!", e);
		}
	}

	// /**
	// * 根据机构代码和接收单位查找接收单位银行对应关系sDTO
	// *
	// * @param String
	// * sorgcode 机构代码
	// * @param String
	// * srecvunit 接收单位
	// * @return
	// * @throws ITFEBizException
	// */
	// public static TsConverttobankDto findBankDto(String sorgcode, String
	// srecvunit) throws ITFEBizException {
	// TsConverttobankPK _pk = new TsConverttobankPK();
	// _pk.setSorgcode(sorgcode);
	// _pk.setSrecvunit(srecvunit);
	//
	// try {
	// return (TsConverttobankDto) DatabaseFacade.getDb().find(_pk);
	// } catch (JAFDatabaseException e) {
	// logger.error("查询接收单位参数表发生数据库异常!", e);
	// throw new ITFEBizException("查询接收单位参数表发生数据库异常!", e);
	// }
	// }

	/**
	 * 根据机构代码、接收单位和国库代码查找接收单位银行对应关系sDTO
	 * 
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            srecvunit 接收单位
	 * @param String
	 *            strecode 国库代码
	 * @return
	 * @throws ITFEBizException
	 */
	public static TsConverttobankDto findBankDto(String sorgcode,
			String srecvunit, String strecode) throws ITFEBizException {
		TsConverttobankPK _pk = new TsConverttobankPK();
		_pk.setSorgcode(sorgcode);
		_pk.setSrecvunit(srecvunit);
		_pk.setStrecode(strecode);

		try {
			return (TsConverttobankDto) DatabaseFacade.getDb().find(_pk);
		} catch (JAFDatabaseException e) {
			logger.error("查询接收单位参数表发生数据库异常!", e);
			throw new ITFEBizException("查询接收单位参数表发生数据库异常!", e);
		}
	}

	/**
	 * 查询重复导入的文件是否有对应失败的记录 - 直接支付业务
	 * 
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            sfilename 文件名称
	 * @param String
	 *            status 交易状态
	 */
	public static boolean findFailedDirectRecord(String sorgcode,
			String sfilename) throws ITFEBizException {
		TvDirectpaymsgmainDto finddto = new TvDirectpaymsgmainDto();
		finddto.setSorgcode(sorgcode); // 机构代码
		finddto.setSfilename(sfilename); // 文件名称
		finddto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL); // 交易状态-处理失败
		try {
			List list = CommonFacade.getODB().findRsByDto(finddto);

			if (null == list || list.size() == 0) {
				return false;
			}

			return true;
		} catch (JAFDatabaseException e) {
			logger.error("查询直接支付额度表的时候出现异常!", e);
			throw new ITFEBizException("查询直接支付额度表的时候出现异常!", e);
		} catch (ValidateException e) {
			logger.error("查询直接支付额度表的时候出现异常!", e);
			throw new ITFEBizException("查询直接支付额度表的时候出现异常!", e);
		}
	}

	/**
	 * 查询重复导入的文件是否有对应失败的记录 - 授权支付业务
	 * 
	 * @param String
	 *            sorgcode 机构代码
	 * @param String
	 *            sfilename 文件名称
	 * @param String
	 *            status 交易状态
	 */
	public static boolean findFailedGrantRecord(String sorgcode,
			String sfilename) throws ITFEBizException {
		TvGrantpaymsgsubDto finddto = new TvGrantpaymsgsubDto();
		finddto.setSorgcode(sorgcode); // 机构代码
		finddto.setSfilename(sfilename); // 文件名称
		finddto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL); // 交易状态-处理失败
		try {
			List list = CommonFacade.getODB().findRsByDto(finddto);

			if (null == list || list.size() == 0) {
				return false;
			}

			return true;
		} catch (JAFDatabaseException e) {
			logger.error("查询授权支付额度表的时候出现异常!", e);
			throw new ITFEBizException("查询授权支付额度表的时候出现异常!", e);
		} catch (ValidateException e) {
			logger.error("查询授权支付额度表的时候出现异常!", e);
			throw new ITFEBizException("查询授权支付额度表的时候出现异常!", e);
		}
	}

	/**
	 * 根据主表记录查询子表记录
	 * 
	 * @param IDto
	 *            idto
	 * @return
	 * @throws ITFEBizException
	 */
	public static List<IDto> findSubDtoByMain(IDto idto)
			throws ITFEBizException {
		try {
			if (idto instanceof TvDirectpaymsgmainDto) {
				TvDirectpaymsgmainDto maindto = (TvDirectpaymsgmainDto) idto;
				TvDirectpaymsgsubDto subdto = new TvDirectpaymsgsubDto();
				//凭证流水号
				subdto.setIvousrlno(maindto.getIvousrlno());
				return CommonFacade.getODB().findRsByDto(subdto);
			}

			if (idto instanceof TvGrantpaymsgmainDto) {
				TvGrantpaymsgmainDto maindto = (TvGrantpaymsgmainDto) idto;
				TvGrantpaymsgsubDto subdto = new TvGrantpaymsgsubDto();
				//凭证流水号
				subdto.setIvousrlno(maindto.getIvousrlno());
				subdto.setSpackageticketno(maindto.getSpackageticketno());
				return CommonFacade.getODB().findRsByDto(subdto);
			}

			if (idto instanceof TvPayoutmsgmainDto) {
				TvPayoutmsgmainDto maindto = (TvPayoutmsgmainDto) idto;
				TvPayoutmsgsubDto subdto = new TvPayoutmsgsubDto();
				subdto.setSbizno(maindto.getSbizno()); // 发送流水号
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			if (idto instanceof TvPayoutfinanceMainDto) {
				TvPayoutfinanceMainDto maindto = (TvPayoutfinanceMainDto) idto;
				TvPayoutfinanceSubDto subdto = new TvPayoutfinanceSubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // 凭证流水号
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			if (idto instanceof TvPbcpayMainDto) {
				TvPbcpayMainDto maindto = (TvPbcpayMainDto) idto;
				TvPbcpaySubDto subdto = new TvPbcpaySubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // 凭证流水号
				return CommonFacade.getODB().findRsByDto(subdto);
			}

			if (idto instanceof TvPayreckBankDto) {
				TvPayreckBankDto maindto = (TvPayreckBankDto) idto;
				TvPayreckBankListDto subdto = new TvPayreckBankListDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // 凭证流水号
				return CommonFacade.getODB().findRsByDto(subdto);
			}

			if (idto instanceof TvPayreckBankBackDto) {
				TvPayreckBankBackDto maindto = (TvPayreckBankBackDto) idto;
				TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // 凭证流水号
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			
			if (idto instanceof TvPayoutbackmsgMainDto) {//实拨资金退回
				TvPayoutbackmsgMainDto maindto = (TvPayoutbackmsgMainDto) idto;
				TvPayoutbackmsgSubDto subdto = new TvPayoutbackmsgSubDto();
				subdto.setSbizno(maindto.getSbizno()); // 凭证流水号
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			
			//财政直接支付凭证5201主表
			if (idto instanceof TfDirectpaymsgmainDto) {
				TfDirectpaymsgmainDto maindto = (TfDirectpaymsgmainDto) idto;
				TfDirectpaymsgsubDto subdto = new TfDirectpaymsgsubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // 凭证流水号
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			
			//财政批量业务直接支付明细8207主表
			if (idto instanceof TfPaymentDetailsmainDto) {
				TfPaymentDetailsmainDto maindto = (TfPaymentDetailsmainDto) idto;
				TfPaymentDetailssubDto subdto = new TfPaymentDetailssubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // 凭证流水号
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			
			//财政直接支付调整5253主表
			if (idto instanceof TfDirectpayAdjustmainDto) {
				TfDirectpayAdjustmainDto maindto = (TfDirectpayAdjustmainDto) idto;
				TfDirectpayAdjustsubDto subdto = new TfDirectpayAdjustsubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // 凭证流水号
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			//财政授权支付调整5351主表
			if (idto instanceof TfGrantpayAdjustmainDto) {
				TfGrantpayAdjustmainDto maindto = (TfGrantpayAdjustmainDto) idto;
				TfGrantpayAdjustsubDto subdto = new TfGrantpayAdjustsubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // 凭证流水号
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			// 收款银行退款通知2252主表
			if (idto instanceof TfPaybankRefundmainDto) {
				TfPaybankRefundmainDto maindto = (TfPaybankRefundmainDto) idto;
				TfPaybankRefundsubDto subdto = new TfPaybankRefundsubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // 凭证流水号
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			// 税票收入通知5671主表
			if (idto instanceof TvNontaxmainDto) {
				TvNontaxmainDto maindto = (TvNontaxmainDto) idto;
				TvNontaxsubDto subdto = new TvNontaxsubDto();
				subdto.setSdealno(maindto.getSdealno()); // 凭证流水号
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			
			throw new ITFEBizException("没有定义此业务类型!");
		} catch (JAFDatabaseException e) {
			logger.error("查询业务数据表发生数据库异常!", e);
			throw new ITFEBizException("查询业务数据表发生数据库异常!", e);
		} catch (ValidateException e) {
			logger.error("查询业务数据表发生数据库异常!", e);
			throw new ITFEBizException("查询业务数据表发生数据库异常!", e);
		}
	}

	/**
	 * 根据预算级次代码得到对应的名称
	 * 
	 * @param String
	 *            sbdglevelcode 预算级次
	 * @return
	 */
	public static String getBdgLevelNameByCode(String sbdglevelcode) {

		if (MsgConstant.BUDGET_LEVEL_SHARE.equals(sbdglevelcode)) {
			return "共享";
		} else if (MsgConstant.BUDGET_LEVEL_CENTER.equals(sbdglevelcode)) {
			return "中央";
		} else if (MsgConstant.BUDGET_LEVEL_PROVINCE.equals(sbdglevelcode)) {
			return "省";
		} else if (MsgConstant.BUDGET_LEVEL_DISTRICT.equals(sbdglevelcode)) {
			return "市";
		} else if (MsgConstant.BUDGET_LEVEL_PREFECTURE.equals(sbdglevelcode)) {
			return "县";
		} else if (MsgConstant.BUDGET_LEVEL_VILLAGE.equals(sbdglevelcode)) {
			return "乡";
		}

		return sbdglevelcode;
	}

	/**
	 * 根据预算种类代码得到对应的名称
	 * 
	 * @param String
	 *            sbdgkindcode 预算种类
	 * @return
	 */
	public static String getBdgKindNameByCode(String sbdgkindcode) {
		if (MsgConstant.BDG_KIND_IN.equals(sbdgkindcode)) {
			return "预算内";
		} else if (MsgConstant.BDG_KIND_OUT.equals(sbdgkindcode)) {
			return "预算外";
		} else if (MsgConstant.BDG_KIND_IN_CASH.equals(sbdgkindcode)) {
			return "预算内暂存";
		}

		return "预算内";
	}

	/**
	 * 根据文件名称转化DTO
	 * 
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileObjDto getFileObjByFileName(String filename,Date tzdate,String tzsdate)
			throws ITFEBizException {
		// 对文件名称处理
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();

		if (tmpfilename_new.indexOf(".txt") > 0) {
			// 收入税票(TBS或地税接口)
			String tmpfilename = tmpfilename_new.replace(".txt", "");
			if (tmpfilename.length() == 13) {
				// TBS接口
				// 8位日期，2位批次号，2位业务类型，1位调整期标志组成
				fileobj.setSdate(tmpfilename.substring(0, 8)); // 日期
				fileobj.setSbatch(tmpfilename.substring(8, 10)); // 批次号
				fileobj.setSbiztype(tmpfilename.substring(10, 12)); // 业务类型
				fileobj.setCtrimflag(tmpfilename.substring(12, 13)); // 调整期标志
				fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_TBS);// TBS接口数据

				if (!BizTypeConstant.BIZ_TYPE_INCOME.equals(fileobj
						.getSbiztype())) {
					throw new ITFEBizException("业务类型不符合，必须为“11”!");
				}
				if (!MsgConstant.TIME_FLAG_NORMAL
						.equals(fileobj.getCtrimflag())
						&& !MsgConstant.TIME_FLAG_TRIM.equals(fileobj
								.getCtrimflag())) {
					throw new ITFEBizException("调整期标志不符合，必须为“0”正常期 或 “1”调整期!");
				}else if(MsgConstant.TIME_FLAG_TRIM.equals(fileobj
						.getCtrimflag())){
					Date adjustdate = tzdate;
					String str = tzsdate;
					Date systemDate = Date.valueOf(str.substring(0, 4)+ "-" + str.substring(4, 6) + "-" + str.substring(6, 8));
					if(com.cfcc.jaf.common.util.DateUtil.after(systemDate,adjustdate)){
						throw new ITFEBizException("调整期" + com.cfcc.deptone.common.util.DateUtil.date2String(adjustdate) + "已过，不能处理调整期业务！");
					}
				}
			} else if (tmpfilename.length() == 20) {
				// 地税接口
				fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_PLACE);// 地税接口数据
				fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME); // 业务类型
			} else if (tmpfilename.length() == 26) {
				// TIPS接口
				fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_TIPS);// TIPS接口数据
				fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME); // 业务类型
			} else {
				throw new ITFEBizException("文件名格式不符!");
			}
		} else if (tmpfilename_new.indexOf(".xml") > 0) {
			// 支出业务(直接支付额度、授权支付额度、实拨资金业务)
			String tmpfilename = tmpfilename_new.replace(".xml", "");
			if (tmpfilename.length() == 17) {
				// 直接(授权)支付额度业务
				// 8位日期，4位表示发送方标志，3位表示业务类型，PP表示当天该类业务下的批次号，从01开始，不超过99
				fileobj.setSdate(tmpfilename.substring(0, 8)); // 日期
				fileobj.setSbatch(tmpfilename.substring(8, 12)); // 发送标志
				fileobj.setSbiztype(tmpfilename.substring(12, 15)); // 业务类型
				fileobj.setCtrimflag(tmpfilename.substring(15, 17)); // 批次号
				if (!BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN_ZJ.equals(fileobj.getSbiztype())&& !BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN_ZJ.equals(fileobj.getSbiztype())) {
					throw new ITFEBizException("业务类型不符合，必须为“201”授权支付额度业务 或 “301”直接支付额度业务!");
				}
			} else {
				// 实拨资金业务
				String[] strs = tmpfilename.split("_");
				if (strs.length != 6) {
					throw new ITFEBizException("文件名格式不符!");
				} else {
					fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_PAY_OUT); // 业务类型 - 一般预算支出
					fileobj.setStrecode(strs[1]); // 国库代码
					fileobj.setStaxorgcode(strs[2]); // 出票单位
					fileobj.setSdate(strs[4]); // 日期
					fileobj.setSpackno(strs[5]); // 包流水号
				}
			}
		} else if (tmpfilename_new.indexOf(".dbf") > 0) {
			// 国税接口
			fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_NATION);// 国税接口数据
			fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME); // 业务类型
		} else {
			throw new ITFEBizException("文件名格式不符!");
		}

		//  需要增加各个字段的校验，比如日期，批次号，业务类型
		return fileobj;
	}
	/**
	 * 根据文件名称转化DTO
	 * 
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileObjDto getFileObjByFileName(String filename)
			throws ITFEBizException {
		// 对文件名称处理
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();

		if (tmpfilename_new.indexOf(".txt") > 0) {
			// 收入税票(TBS或地税接口)
			String tmpfilename = tmpfilename_new.replace(".txt", "");
			if (tmpfilename.length() == 13) {
				// TBS接口
				// 8位日期，2位批次号，2位业务类型，1位调整期标志组成
				fileobj.setSdate(tmpfilename.substring(0, 8)); // 日期
				fileobj.setSbatch(tmpfilename.substring(8, 10)); // 批次号
				fileobj.setSbiztype(tmpfilename.substring(10, 12)); // 业务类型
				fileobj.setCtrimflag(tmpfilename.substring(12, 13)); // 调整期标志
				fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_TBS);// TBS接口数据

				if (!BizTypeConstant.BIZ_TYPE_INCOME.equals(fileobj
						.getSbiztype())) {
					throw new ITFEBizException("业务类型不符合，必须为“11”!");
				}
				if (!MsgConstant.TIME_FLAG_NORMAL
						.equals(fileobj.getCtrimflag())
						&& !MsgConstant.TIME_FLAG_TRIM.equals(fileobj
								.getCtrimflag())) {
					throw new ITFEBizException("调整期标志不符合，必须为“0”正常期 或 “1”调整期!");
				}else if(MsgConstant.TIME_FLAG_TRIM.equals(fileobj
						.getCtrimflag())){
					Date adjustdate = getAdjustDate();
					String str = getSysDBDate();
					Date systemDate = Date.valueOf(str.substring(0, 4)+ "-" + str.substring(4, 6) + "-" + str.substring(6, 8));
					if(com.cfcc.jaf.common.util.DateUtil.after(systemDate,adjustdate)){
						throw new ITFEBizException("调整期" + com.cfcc.deptone.common.util.DateUtil.date2String(adjustdate) + "已过，不能处理调整期业务！");
					}
				}
			} else if (tmpfilename.length() == 20) {
				// 地税接口
				fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_PLACE);// 地税接口数据
				fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME); // 业务类型
			} else if (tmpfilename.length() == 26) {
				// TIPS接口
				fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_TIPS);// TIPS接口数据
				fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME); // 业务类型
			} else {
				throw new ITFEBizException("文件名格式不符!");
			}
		} else if (tmpfilename_new.indexOf(".xml") > 0) {
			// 支出业务(直接支付额度、授权支付额度、实拨资金业务)
			String tmpfilename = tmpfilename_new.replace(".xml", "");
			if (tmpfilename.length() == 17) {
				// 直接(授权)支付额度业务
				// 8位日期，4位表示发送方标志，3位表示业务类型，PP表示当天该类业务下的批次号，从01开始，不超过99
				fileobj.setSdate(tmpfilename.substring(0, 8)); // 日期
				fileobj.setSbatch(tmpfilename.substring(8, 12)); // 发送标志
				fileobj.setSbiztype(tmpfilename.substring(12, 15)); // 业务类型
				fileobj.setCtrimflag(tmpfilename.substring(15, 17)); // 批次号
				if (!BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN_ZJ.equals(fileobj.getSbiztype())&& !BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN_ZJ.equals(fileobj.getSbiztype())) {
					throw new ITFEBizException("业务类型不符合，必须为“201”授权支付额度业务 或 “301”直接支付额度业务!");
				}
			} else {
				// 实拨资金业务
				String[] strs = tmpfilename.split("_");
				if (strs.length != 6) {
					throw new ITFEBizException("文件名格式不符!");
				} else {
					fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_PAY_OUT); // 业务类型 - 一般预算支出
					fileobj.setStrecode(strs[1]); // 国库代码
					fileobj.setStaxorgcode(strs[2]); // 出票单位
					fileobj.setSdate(strs[4]); // 日期
					fileobj.setSpackno(strs[5]); // 包流水号
				}
			}
		} else if (tmpfilename_new.indexOf(".dbf") > 0) {
			// 国税接口
			fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_NATION);// 国税接口数据
			fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME); // 业务类型
		} else {
			throw new ITFEBizException("文件名格式不符!");
		}

		//  需要增加各个字段的校验，比如日期，批次号，业务类型
		return fileobj;
	}
	/**
	 * 根据国库代码取得对应的机构代码
	 * 
	 * @param trecode
	 * @return
	 * @throws ITFEBizException
	 */
	public static String getOrgByTreCode(String trecode)
			throws ITFEBizException {
		try {
			TsTreasuryDto tredto = new TsTreasuryDto();
			tredto.setStrecode(trecode);

			List<TsTreasuryDto> list = CommonFacade.getODB()
					.findRsByDto(tredto);

			if (null == list || list.size() == 0) {
				throw new ITFEBizException("没有维护国库代码[" + trecode + "]参数!");
			}

			return list.get(0).getSorgcode();
		} catch (JAFDatabaseException e) {
			logger.error("查询国库参数表发生数据库异常!", e);
			throw new ITFEBizException("查询国库参数表发生数据库异常!", e);
		} catch (ValidateException e) {
			logger.error("查询国库参数表发生数据库异常!", e);
			throw new ITFEBizException("查询国库参数表发生数据库异常!", e);
		}
	}

	/**
	 * 取得IMPORT方式数据库导入的数据库连接语句( 根据属性配置文件)
	 * 
	 * @return
	 */
	public static String getSqlConnectByProp() {
		String sql = "connect to " + ITFECommonConstant.DB_ALIAS + " user "
				+ ITFECommonConstant.DB_USERNAME + " using "
				+ ITFECommonConstant.DB_PASSWORD + ";\n";
		return sql;
	}

	/**
	 * 根据核算主体查询联网机构密钥
	 * @param _sorgcode
	 * @return
	 * @throws ITFEBizException
	 */
	public static HashMap<String, String> findOrgKey(String _sorgcode)
			throws ITFEBizException {
		try {
			HashMap<String, String> keymap = new HashMap<String, String>();
			TsInfoconnorgDto _dto = new TsInfoconnorgDto();
			_dto.setSorgcode(_sorgcode);
			List<TsInfoconnorgDto> list = CommonFacade.getODB().findRsByDto(
					_dto);
			if (list.size() > 0) {
				for (TsInfoconnorgDto tmpdto : list) {
					keymap.put(tmpdto.getSconnorgcode(), tmpdto.getSkey());
				}
			}
			return keymap;
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询联网机构密钥出错1", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询联网机构密钥出错2", e);
		}
	}

	private static Date getAdjustDate() throws ITFEBizException {
		try {
			TsSystemDto _dto = new TsSystemDto();
			List<TsSystemDto> list = CommonFacade.getODB().findRsByDto(_dto);
			if (list != null && list.size() > 0) {
				String str = list.get(0).getSendadjustday();
				return Date.valueOf(str.substring(0, 4) + "-"
						+ str.substring(4, 6) + "-" + str.substring(6, 8));
			} else {
				throw new ITFEBizException("获取调整期时间失败");
			}
		} catch (Exception e) {
			logger.error("获取调整期日期失败！", e);
			throw new ITFEBizException(e);
		}
	}

		/**
		 * 返回服务器数据库时间 有时获得系统时间不对，所以取数据库时间
		 */
		private static String getSysDBDate() throws ITFEBizException {
			try {
				// 获取当前时间
				Date now = TSystemFacade.findDBSystemDate();
				// 将文件保存到接收目录中
				return DateUtil.date2String2(now);
			} catch (Exception e) {
				throw new ITFEBizException("获取系统时间异常", e);
			}
		}
		public static List<TsTreasuryDto> getSubTreCode(TsTreasuryDto dto) throws ITFEBizException 
		{
			List<TsTreasuryDto> trelist = null;
			try{
				if(dto.getStrecode()==null||"".equals(dto.getStrecode().trim()))
					return trelist;
				else
				{
					trelist = new ArrayList<TsTreasuryDto>();
					TsTreasuryDto finddto = new TsTreasuryDto();
					finddto.setSgoverntrecode(dto.getStrecode());
					List<TsTreasuryDto> templist = null;
					if(dto.getStrelevel()==null||"".equals(dto.getStrelevel()))
					{
						templist = CommonFacade.getODB().findRsByDto(dto);
						if(templist!=null&&templist.size()>0)
							dto = templist.get(0);
					}
					trelist.add(dto);
					templist = CommonFacade.getODB().findRsByDto(finddto);
					if(templist!=null&&templist.size()>0)
					{
						trelist.addAll(templist);
						TsTreasuryDto tempdto = null;
						for(int i=0;i<templist.size();i++)
						{
							tempdto = templist.get(i);
							if(!tempdto.getStrecode().equals(tempdto.getSgoverntrecode()))
								trelist.addAll(getSubTreCode(tempdto));
						}
					}	
				}
			} catch (JAFDatabaseException e) {
				logger.error("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
				throw new ITFEBizException("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
			} catch (ValidateException e) {
				logger.error("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
				throw new ITFEBizException("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
			}
			return trelist;
		}
		public static List<TsTreasuryDto> getSubTreCode(String orgCode) throws ITFEBizException 
		{
			List<TsTreasuryDto> returnList = null;
			try{
				if(orgCode==null||"".equals(orgCode.trim()))
					return returnList;
				else
				{
					List<TsTreasuryDto> trelist = new ArrayList<TsTreasuryDto>();
					returnList = new ArrayList<TsTreasuryDto>();
					TsTreasuryDto finddto = new TsTreasuryDto();
					finddto.setSorgcode(orgCode);
					List<TsTreasuryDto> templist = CommonFacade.getODB().findRsByDto(finddto);
					if(templist!=null&&templist.size()>0)
					{
						trelist.addAll(templist);
						for(int i=0;i<templist.size();i++)
							trelist.addAll(getSubTreCode(templist.get(i)));
					}
					Set<String> tmpTreSet = new HashSet<String>();
					if(trelist!=null&&trelist.size()>0)
					{
						for(int i=0;i<trelist.size();i++)
						{
							if(tmpTreSet.add(trelist.get(i).getStrecode()))
								returnList.add(trelist.get(i));
						}
					}
				}
			} catch (JAFDatabaseException e) {
				logger.error("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
				throw new ITFEBizException("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
			} catch (ValidateException e) {
				logger.error("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
				throw new ITFEBizException("查询国库参数表发生数据库异常-递归查询全辖国库!", e);
			}
			return returnList;
		}
	/**
	 * @param args
	 * @throws ITFEBizException
	 */
	public static void main(String[] args) throws ITFEBizException {
		//  Auto-generated method stub
		getFileObjByFileName("c:\\5101_1102000000_0101_313331000014_20100113_00000001.XML");
	}
	
	/**
	 * 根据银行代码查找对应的银行名称
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException 
	 */
	public static String findPayBankNameByPayBankCode(TnConpaycheckbillDto dto)throws ITFEBizException{
		HashMap<String, TsPaybankDto> map;
		try {
			map = SrvCacheFacade.cachePayBankInfo();
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}
		String errMsg="银行代码"+dto.getSbnkno()+"在[支付行号查询]中未维护！";	
		if(map==null||map.size()==0)
			throw new ITFEBizException(errMsg);
		TsPaybankDto bankDto = map.get(dto.getSbnkno());
		if(bankDto==null)
			throw new ITFEBizException(errMsg);		
		return bankDto.getSbankname();
	}
	
	/**
	 * 根据银行代码查找对应的银行名称
	 * @param dto
	 * @return
	 * @throws JAFDatabaseException 
	 */
	public static String findPayBankNameByPayBankCode(String bankno)throws ITFEBizException{
		TnConpaycheckbillDto dto = new TnConpaycheckbillDto();
		dto.setSbnkno(bankno);
		return findPayBankNameByPayBankCode(dto);
	}
	
	/**
	 * 根据支出功能分类科目查找对应的支出功能分类科目名称
	 * @param dto
	 * @return
	 */
	public static String findExpFuncNameByExpFuncCode(TnConpaycheckbillDto dto)throws ITFEBizException{
		Map<String, TsBudgetsubjectDto> map;
		try {
			map = SrvCacheFacade.cacheTsBdgsbtInfo(dto.getSbookorgcode());
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}
		String errMsg="支出功能分类科目"+dto.getSfuncsbtcode()+"在[预算科目查询]中未维护！";
		if(map==null||map.size()==0)
			throw new ITFEBizException(errMsg);
		TsBudgetsubjectDto subjectDto = map.get(dto.getSfuncsbtcode());
		if(subjectDto==null)
			throw new ITFEBizException(errMsg);		
		return subjectDto.getSsubjectname();		
	}
	
	/**
	 * 根据支出功能分类科目查找对应的支出功能分类科目名称
	 * @param sorgcode
	 * @param ExpFuncCode
	 * @return
	 * @throws ITFEBizException
	 */
	public static String findExpFuncNameByExpFuncCode(String sorgcode,String ExpFuncCode)throws ITFEBizException{
		TnConpaycheckbillDto dto = new TnConpaycheckbillDto();
		dto.setSbookorgcode(sorgcode);
		dto.setSfuncsbtcode(ExpFuncCode);
		return findExpFuncNameByExpFuncCode(dto);
	}
	
	/**
	 * 根据预算科目代码查找对应的预算科目名称
	 * @param dto
	 * @return
	 */
	public static String findSupDepCodeBySupDepName(TdCorpDto dto)throws ITFEBizException{
		HashMap<String, TdCorpDto> map;
		try {
			map = SrvCacheFacade.cacheTdCorpInfo(dto.getSbookorgcode());
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
		}
		String errMsg="预算单位代码["+dto.getScorpcode()+"]在预算单位代码参数中不存在!";
		if(map==null||map.size()==0)
			throw new ITFEBizException(errMsg);
		TdCorpDto subjectDto = map.get(dto.getStrecode() + dto.getScorpcode());
		if(subjectDto==null)
			throw new ITFEBizException(errMsg);		
		return subjectDto.getScorpname();		
	}
	
	/**
	 * 根据预算科目代码查找对应的预算科目名称
	 * @param sorgcode
	 * @param strecode
	 * @param SupDepCode
	 * @return
	 * @throws ITFEBizException
	 */
	public static String findSupDepCodeBySupDepName(String sorgcode,String strecode,String SupDepCode)throws ITFEBizException{
		TdCorpDto dto=new TdCorpDto();
		dto.setSbookorgcode(sorgcode);
		dto.setStrecode(strecode);
		dto.setScorpcode(SupDepCode);
		return findSupDepCodeBySupDepName(dto);
	}
	
}

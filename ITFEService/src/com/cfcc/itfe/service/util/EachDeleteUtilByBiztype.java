package com.cfcc.itfe.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TbsTvFreeDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author 朱国珍
 * @time 12-02-21 08:45:49 逐笔删除
 */

public class EachDeleteUtilByBiztype {
	private static Log logger = LogFactory
			.getLog(EachDeleteUtilByBiztype.class);

	/**
	 * 
	 * @generated
	 * @param bizType业务类型
	 * @param idto
	 * @return java.lang.Integer 返回结果：0失败,1成功,2已修改
	 * @throws ITFEBizException
	 */
	public static Integer eachDelete(String bizType, IDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		/*
		 * 判断临时表是否已销号
		 */
		if (CheckIfConfirm.ifEachConfirm(bizType, idto)) {
			return StateConstant.SUBMITSTATE_DONE;
		}
		/*
		 * 按业务类型进行确认提交
		 */
		Boolean b = null;
		String bizname = CheckBizParam.getBizname(bizType);
		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// 直接支付
			b = EachDeleteUtil.deleteDircetPay(idto, loginfo);
			// 销号删除操作日志
			if (b) {
				TbsTvDirectpayplanMainDto dto = (TbsTvDirectpayplanMainDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto
						.getSvouno(), dto.getFamt(), loginfo);
			}
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// 授权支付
			b = EachDeleteUtil.deleteGrantPay(idto, loginfo);
			// 销号删除操作日志
			if (b) {
				TbsTvGrantpayplanMainDto dto = (TbsTvGrantpayplanMainDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto
						.getSvouno(),dto.getFsmallsumamt().add(dto.getFzerosumamt()), loginfo);
			}
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// 实拨资金
			b = EachDeleteUtil.deletePayout(idto, loginfo);
			// 销号删除操作日志
			if (b) {
				TbsTvPayoutDto dto = (TbsTvPayoutDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto
						.getSvouno(),dto.getFamt(), loginfo);
			}
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)) {// 更正
			b = EachDeleteUtil.deleteCorrect(idto, loginfo);
			// 销号删除操作日志
			if (b) {
				TbsTvInCorrhandbookDto dto = (TbsTvInCorrhandbookDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto.getScorrvouno(), dto.getFcurcorramt(),loginfo);
			}
		} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// 退库
			b = EachDeleteUtil.deleteRetTreasury(idto, loginfo);
			// 销号删除操作日志
			if (b) {
				TbsTvDwbkDto dto = (TbsTvDwbkDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto.getSdwbkvoucode(),dto.getFamt(), loginfo);
			}
		} else if (BizTypeConstant.BIZ_TYPE_TAX_FREE.equals(bizType)) {// 免抵调
			b = EachDeleteUtil.deleteFree(idto, loginfo);
			// 销号删除操作日志
			if (b) {
				TbsTvFreeDto dto = (TbsTvFreeDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto.getSfreevouno(),dto.getFfreepluamt().add(dto.getFfreemiamt()), loginfo);
			}
		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// 人行办理支付
			b = EachDeleteUtil.deletePbcPayout(idto, loginfo);
			// 销号删除操作日志
			if (b) {
				TbsTvPbcpayDto dto = (TbsTvPbcpayDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto.getSvouno(),dto.getFamt(), loginfo);
			}
		}
		// 系统操作日志
		WriteOperLog.operLog(loginfo.getSuserCode(), bizType, bizname,
				"batchDelete", b, loginfo);

		if (b) {
			return StateConstant.SUBMITSTATE_SUCCESS;

		} else {
			return StateConstant.SUBMITSTATE_FAILURE;
		}

	}

}
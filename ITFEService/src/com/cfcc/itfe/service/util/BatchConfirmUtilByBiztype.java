package com.cfcc.itfe.service.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * @author 曹艳国 实拨资金
 * @time 12-02-21 08:45:49 批量销号，逐笔销号，直接提交，一些判断等
 */

public class BatchConfirmUtilByBiztype {
	private static Log logger = LogFactory.getLog(BatchConfirmUtilByBiztype.class);

	/**
	 * batchConfirm 批量销号-确认提交(按业务类型、文件名确认提交)
	 * 
	 * @generated
	 * @param bizType业务类型
	 * @param fileName文件名
	 * @return java.lang.Integer 返回结果：0失败,1成功，2已销号过
	 * @throws ITFEBizException
	 */
	public static Integer bizBatchConfirm(String bizType, TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		/*
		 * 判断文件名与包对应关系中是否已销号
		 */
		if (CheckIfConfirm.ifConfirm(bizType, idto, loginfo)) {
			return StateConstant.SUBMITSTATE_DONE;
		}
		/*
		 * 按业务类型进行确认提交
		 */
		Boolean b = null;
		String bizname = CheckBizParam.getBizname(bizType);
		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// 直接支付
			 b = BatchDirectPayCommitUtil.confirmDircetPay(bizType,
					idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// 授权支付
			 b = BatchGrantPayCommitUtil.confirmGrantPay(bizType,
						idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// 实拨资金
			 b = BatchPayOutCommitUtil.confirmPayout(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)) {// 更正
			 b =BatchCorrectCommitUtil.confirmCorrect(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// 退库
			b=BatchDwbkCommitUtil.confirmRetTreasury(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_TAX_FREE.equals(bizType)) {// 免抵调
			b=BatchFreeCommitUtil.confirmFree(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// 批量拨付
			b=BatchPayBatchCommitUtil.confirmBatch(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// 人行办理支付
			b=BatchPBCPayOutCommitUtil.confirmPbcPayout(bizType, idto, loginfo);
		}else if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)) {// 商行办理支付划款申请
			b=BatchPayreckBankCommitUtil.confirmPayreckBank(bizType, idto, loginfo);
		}
		WriteOperLog.operLog(loginfo.getSuserCode(), bizType, bizname,
				"batchConfirm", b, loginfo);
		if (b) {
			return StateConstant.SUBMITSTATE_SUCCESS;

		} else {
			return StateConstant.SUBMITSTATE_FAILURE;
		}

	}
	
	
	public static Integer bizApplyBackBatchConfirm(String bizType, TvFilepackagerefDto idto,
			ITFELoginInfo loginfo,IDto _dto) throws ITFEBizException {
		/*
		 * 判断文件名与包对应关系中是否已销号
		 */
		if (CheckIfConfirm.ifConfirm(bizType, idto, loginfo)) {
			return StateConstant.SUBMITSTATE_DONE;
		}
		/*
		 * 按业务类型进行确认提交
		 */
		Boolean b = null;
		String bizname = CheckBizParam.getBizname(bizType);
	    if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {// 商行办理支付划款申请退回
			b=BatchPayreckBankBackCommitUtil.confirmPayreckBackBank(bizType, idto, loginfo,_dto);
		}
		WriteOperLog.operLog(loginfo.getSuserCode(), bizType, bizname,
				"batchConfirm", b, loginfo);
		if (b) {
			return StateConstant.SUBMITSTATE_SUCCESS;

		} else {
			return StateConstant.SUBMITSTATE_FAILURE;
		}

	}

}
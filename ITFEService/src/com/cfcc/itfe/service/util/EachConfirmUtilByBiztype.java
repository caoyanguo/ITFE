package com.cfcc.itfe.service.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TsSyslogDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.ServiceUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author 曹艳国
 * @time 12-02-21 08:45:49 逐笔销号
 */

public class EachConfirmUtilByBiztype {
	private static Log logger = LogFactory.getLog(EachConfirmUtilByBiztype.class);

	/**
	 * batchConfirm 批量销号-确认提交(按业务类型、文件名确认提交)
	 * 
	 * @generated
	 * @param bizType业务类型
	 * @param fileName文件名
	 * @return java.lang.Integer 返回结果：0失败,1成功，2已销号过
	 * @throws ITFEBizException
	 */
	public static Integer bizEachConfirm(String bizType, IDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		/*
		 * 判断文件名与包对应关系中是否已销号
		 */
		if (CheckIfConfirm.ifEachConfirm(bizType, idto)){
			return StateConstant.SUBMITSTATE_DONE;
		}
		/*
		 * 按业务类型进行确认提交
		 */
		Boolean b = null;
		String bizname = CheckBizParam.getBizname(bizType);
		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// 直接支付
			 b= EachDirectpayCommitUtil.eachConfirmDircetPay(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// 授权支付
			 b= EachGrantPayCommitUtil.confirmGrantPay(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// 实拨资金
			 b=EachPayOutCommitUtil.confirmPayout(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)) {// 更正
			 b =EachCorrectCommitUtil.confirmCorrect(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// 退库
			b=EachDwbkCommitUtil.confirmRetTreasury(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_TAX_FREE.equals(bizType)) {// 免抵调
			b=EachFreeCommitUtil.confirmFree(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// 批量拨付
//			b=eachPayBatchCommitUtil.confirmBatch(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// 人行办理支付
			b=EachPBCPayOutCommitUtil.confirmPbcPayout(bizType, idto, loginfo);
		}else if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)) {// 商行办理支付划款申请
			b=EachPayreckBankCommitUtil.confirmPayreckBank(bizType, idto, loginfo);
		}
		WriteOperLog.operLog(loginfo.getSuserCode(), bizType, bizname,
				"bizEachConfirm", b, loginfo);
		if (b) {
			return StateConstant.SUBMITSTATE_SUCCESS;
		} else {
			return StateConstant.SUBMITSTATE_FAILURE;
		}

	}
	
	
	
	public static Integer bizApplyBackEachConfirm(String bizType, IDto idto,
			ITFELoginInfo loginfo,IDto _dto) throws ITFEBizException {
		/*
		 * 判断文件名与包对应关系中是否已销号
		 */
		if (CheckIfConfirm.ifEachConfirm(bizType, idto)){
			return StateConstant.SUBMITSTATE_DONE;
		}
		/*
		 * 按业务类型进行确认提交
		 */
		Boolean b = null;
		String bizname = CheckBizParam.getBizname(bizType);
		if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {// 商行办理支付划款申请
			b=EachPayreckBackBankCommitUtil.confirmPayreckBackBank(bizType, idto, loginfo,_dto);
		}
		WriteOperLog.operLog(loginfo.getSuserCode(), bizType, bizname,
				"bizEachConfirm", b, loginfo);
		if (b) {
			return StateConstant.SUBMITSTATE_SUCCESS;
		} else {
			return StateConstant.SUBMITSTATE_FAILURE;
		}

	}

}
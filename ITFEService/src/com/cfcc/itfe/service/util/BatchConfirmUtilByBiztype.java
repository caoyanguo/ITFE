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
 * @author ���޹� ʵ���ʽ�
 * @time 12-02-21 08:45:49 �������ţ�������ţ�ֱ���ύ��һЩ�жϵ�
 */

public class BatchConfirmUtilByBiztype {
	private static Log logger = LogFactory.getLog(BatchConfirmUtilByBiztype.class);

	/**
	 * batchConfirm ��������-ȷ���ύ(��ҵ�����͡��ļ���ȷ���ύ)
	 * 
	 * @generated
	 * @param bizTypeҵ������
	 * @param fileName�ļ���
	 * @return java.lang.Integer ���ؽ����0ʧ��,1�ɹ���2�����Ź�
	 * @throws ITFEBizException
	 */
	public static Integer bizBatchConfirm(String bizType, TvFilepackagerefDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		/*
		 * �ж��ļ��������Ӧ��ϵ���Ƿ�������
		 */
		if (CheckIfConfirm.ifConfirm(bizType, idto, loginfo)) {
			return StateConstant.SUBMITSTATE_DONE;
		}
		/*
		 * ��ҵ�����ͽ���ȷ���ύ
		 */
		Boolean b = null;
		String bizname = CheckBizParam.getBizname(bizType);
		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// ֱ��֧��
			 b = BatchDirectPayCommitUtil.confirmDircetPay(bizType,
					idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// ��Ȩ֧��
			 b = BatchGrantPayCommitUtil.confirmGrantPay(bizType,
						idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// ʵ���ʽ�
			 b = BatchPayOutCommitUtil.confirmPayout(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)) {// ����
			 b =BatchCorrectCommitUtil.confirmCorrect(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// �˿�
			b=BatchDwbkCommitUtil.confirmRetTreasury(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_TAX_FREE.equals(bizType)) {// ��ֵ�
			b=BatchFreeCommitUtil.confirmFree(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_BATCH_OUT.equals(bizType)) {// ��������
			b=BatchPayBatchCommitUtil.confirmBatch(bizType, idto, loginfo);
		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// ���а���֧��
			b=BatchPBCPayOutCommitUtil.confirmPbcPayout(bizType, idto, loginfo);
		}else if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY)) {// ���а���֧����������
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
		 * �ж��ļ��������Ӧ��ϵ���Ƿ�������
		 */
		if (CheckIfConfirm.ifConfirm(bizType, idto, loginfo)) {
			return StateConstant.SUBMITSTATE_DONE;
		}
		/*
		 * ��ҵ�����ͽ���ȷ���ύ
		 */
		Boolean b = null;
		String bizname = CheckBizParam.getBizname(bizType);
	    if (bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY_BACK)
				||bizType.contains(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY_BACK)) {// ���а���֧�����������˻�
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
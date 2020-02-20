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
 * @author �����
 * @time 12-02-21 08:45:49 ���ɾ��
 */

public class EachDeleteUtilByBiztype {
	private static Log logger = LogFactory
			.getLog(EachDeleteUtilByBiztype.class);

	/**
	 * 
	 * @generated
	 * @param bizTypeҵ������
	 * @param idto
	 * @return java.lang.Integer ���ؽ����0ʧ��,1�ɹ�,2���޸�
	 * @throws ITFEBizException
	 */
	public static Integer eachDelete(String bizType, IDto idto,
			ITFELoginInfo loginfo) throws ITFEBizException {
		/*
		 * �ж���ʱ���Ƿ�������
		 */
		if (CheckIfConfirm.ifEachConfirm(bizType, idto)) {
			return StateConstant.SUBMITSTATE_DONE;
		}
		/*
		 * ��ҵ�����ͽ���ȷ���ύ
		 */
		Boolean b = null;
		String bizname = CheckBizParam.getBizname(bizType);
		if (BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN.equals(bizType)) {// ֱ��֧��
			b = EachDeleteUtil.deleteDircetPay(idto, loginfo);
			// ����ɾ��������־
			if (b) {
				TbsTvDirectpayplanMainDto dto = (TbsTvDirectpayplanMainDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto
						.getSvouno(), dto.getFamt(), loginfo);
			}
		} else if (BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN.equals(bizType)) {// ��Ȩ֧��
			b = EachDeleteUtil.deleteGrantPay(idto, loginfo);
			// ����ɾ��������־
			if (b) {
				TbsTvGrantpayplanMainDto dto = (TbsTvGrantpayplanMainDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto
						.getSvouno(),dto.getFsmallsumamt().add(dto.getFzerosumamt()), loginfo);
			}
		} else if (BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(bizType)
				|| BizTypeConstant.BIZ_TYPE_PAY_OUT2.equals(bizType)) {// ʵ���ʽ�
			b = EachDeleteUtil.deletePayout(idto, loginfo);
			// ����ɾ��������־
			if (b) {
				TbsTvPayoutDto dto = (TbsTvPayoutDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto
						.getSvouno(),dto.getFamt(), loginfo);
			}
		} else if (BizTypeConstant.BIZ_TYPE_CORRECT.equals(bizType)) {// ����
			b = EachDeleteUtil.deleteCorrect(idto, loginfo);
			// ����ɾ��������־
			if (b) {
				TbsTvInCorrhandbookDto dto = (TbsTvInCorrhandbookDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto.getScorrvouno(), dto.getFcurcorramt(),loginfo);
			}
		} else if (BizTypeConstant.BIZ_TYPE_RET_TREASURY.equals(bizType)) {// �˿�
			b = EachDeleteUtil.deleteRetTreasury(idto, loginfo);
			// ����ɾ��������־
			if (b) {
				TbsTvDwbkDto dto = (TbsTvDwbkDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto.getSdwbkvoucode(),dto.getFamt(), loginfo);
			}
		} else if (BizTypeConstant.BIZ_TYPE_TAX_FREE.equals(bizType)) {// ��ֵ�
			b = EachDeleteUtil.deleteFree(idto, loginfo);
			// ����ɾ��������־
			if (b) {
				TbsTvFreeDto dto = (TbsTvFreeDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto.getSfreevouno(),dto.getFfreepluamt().add(dto.getFfreemiamt()), loginfo);
			}
		} else if (BizTypeConstant.BIZ_TYPE_PBC_DIRECT_PAY.equals(bizType)) {// ���а���֧��
			b = EachDeleteUtil.deletePbcPayout(idto, loginfo);
			// ����ɾ��������־
			if (b) {
				TbsTvPbcpayDto dto = (TbsTvPbcpayDto) idto;
				WriteOperLog.checkDelOptLog(bizType, dto.getSfilename(), dto.getSvouno(),dto.getFamt(), loginfo);
			}
		}
		// ϵͳ������־
		WriteOperLog.operLog(loginfo.getSuserCode(), bizType, bizname,
				"batchDelete", b, loginfo);

		if (b) {
			return StateConstant.SUBMITSTATE_SUCCESS;

		} else {
			return StateConstant.SUBMITSTATE_FAILURE;
		}

	}

}
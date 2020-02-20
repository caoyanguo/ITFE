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
	 * ת���ļ����� - ֧��ģ����ѯ
	 * 
	 * @param whereStr
	 *            ������ѯ����
	 * @param billno
	 *            Ʊ�ݱ��
	 * @return
	 */
	public static String changeFileName(String whereStr, String filename) {
		// Ʊ�ݱ������
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
	 * ���ݱ��ı���ҵ���Ӧ��ҵ������
	 * 
	 * @param String
	 *            smsgno ���ı��
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
		logger.error("û���ҵ����ı��[" + smsgno + "]��Ӧ��ҵ������!");
		return smsgno;
	}

	/**
	 * ����ҵ�������ҵ���Ӧ�ı����ر��
	 * 
	 * @param String
	 *            biztype ҵ������
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
	 * ����TIPS�Ĵ�������ת��Ӧ������״̬
	 * 
	 * @param String
	 *            result ������
	 * @return
	 */
	public static String getPackageStateByDealCode(String result) {

		if (!DealCodeConstants.DEALCODE_TIPS_SUCCESS.equals(result)) {
			return result;
		}

		return DealCodeConstants.DEALCODE_ITFE_RECEIVER;
	}

	/**
	 * ����TIPS�Ĵ�������ת��Ӧ��ϸ����״̬
	 * 
	 * @param String
	 *            result ������
	 * @return
	 */
	public static String getDetailStateByDealCode(String result) {

		if ((!DealCodeConstants.DEALCODE_TIPS_SUCCESS.equals(result)) && (!DealCodeConstants.DEALCODE_DWBK_SUCCESS.equals(result))  && (!StateConstant.COMMON_NO.equals(result)) ) {
			return DealCodeConstants.DEALCODE_ITFE_FAIL;
		}

		return DealCodeConstants.DEALCODE_ITFE_SUCCESS;
	}

	/**
	 * ���ݻ�������Ͳ����ֱ�־���Ҳ����ִ���DTO
	 * 
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            sfinflag �����ֱ�־
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
				throw new ITFEBizException("�����²����ֱ�־����ûά��,"+sorgcode+"-"+sfinflag+"!");
			else
				return list.get(0);
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ�����ֱ�־�����������ݿ��쳣!", e);
			throw new ITFEBizException("��ѯ�����ֱ�־�����������ݿ��쳣!", e);
		} catch (ValidateException e) {
			logger.error("��ѯ�����ֱ�־�����������ݿ��쳣!", e);
			throw new ITFEBizException("��ѯ�����ֱ�־�����������ݿ��쳣!", e);
		}
	}

	/**
	 * ���ݲ��������ҵ����������������
	 * 
	 * @param String
	 *            sfinorgcode ��������
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
				logger.error("�ڲ����ֱ�־��������û���ҵ���Ӧ�ļ�¼[" + sfinorgcode + "]!");
				throw new ITFEBizException("�ڲ����ֱ�־��������û���ҵ���Ӧ�ļ�¼[" + sfinorgcode
						+ "]!");
			}

			return list.get(0);
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ�����ֱ�־�����������ݿ��쳣!", e);
			throw new ITFEBizException("��ѯ�����ֱ�־�����������ݿ��쳣!", e);
		} catch (ValidateException e) {
			logger.error("��ѯ�����ֱ�־�����������ݿ��쳣!", e);
			throw new ITFEBizException("��ѯ�����ֱ�־�����������ݿ��쳣!", e);
		}
	}

	/**
	 * ��������������Կ��������
	 * 
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            sfinorgcode ��������
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
				logger.error("������������Կ��������û���ҵ���Ӧ�ļ�¼[" + sorgcode + "_"
						+ sfinorgcode + "]!");
				throw new ITFEBizException("������������Կ��������û���ҵ���Ӧ�ļ�¼[" + sorgcode
						+ "_" + sfinorgcode + "]!");
			}

			return (TsInfoconnorgDto) dto;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ����������Կ�����������ݿ��쳣!", e);
			throw new ITFEBizException("��ѯ����������Կ�����������ݿ��쳣!", e);
		}
	}

	// /**
	// * ���ݻ�������ͽ��յ�λ���ҽ��յ�λ���ж�Ӧ��ϵsDTO
	// *
	// * @param String
	// * sorgcode ��������
	// * @param String
	// * srecvunit ���յ�λ
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
	// logger.error("��ѯ���յ�λ�����������ݿ��쳣!", e);
	// throw new ITFEBizException("��ѯ���յ�λ�����������ݿ��쳣!", e);
	// }
	// }

	/**
	 * ���ݻ������롢���յ�λ�͹��������ҽ��յ�λ���ж�Ӧ��ϵsDTO
	 * 
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            srecvunit ���յ�λ
	 * @param String
	 *            strecode �������
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
			logger.error("��ѯ���յ�λ�����������ݿ��쳣!", e);
			throw new ITFEBizException("��ѯ���յ�λ�����������ݿ��쳣!", e);
		}
	}

	/**
	 * ��ѯ�ظ�������ļ��Ƿ��ж�Ӧʧ�ܵļ�¼ - ֱ��֧��ҵ��
	 * 
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            sfilename �ļ�����
	 * @param String
	 *            status ����״̬
	 */
	public static boolean findFailedDirectRecord(String sorgcode,
			String sfilename) throws ITFEBizException {
		TvDirectpaymsgmainDto finddto = new TvDirectpaymsgmainDto();
		finddto.setSorgcode(sorgcode); // ��������
		finddto.setSfilename(sfilename); // �ļ�����
		finddto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL); // ����״̬-����ʧ��
		try {
			List list = CommonFacade.getODB().findRsByDto(finddto);

			if (null == list || list.size() == 0) {
				return false;
			}

			return true;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯֱ��֧����ȱ��ʱ������쳣!", e);
			throw new ITFEBizException("��ѯֱ��֧����ȱ��ʱ������쳣!", e);
		} catch (ValidateException e) {
			logger.error("��ѯֱ��֧����ȱ��ʱ������쳣!", e);
			throw new ITFEBizException("��ѯֱ��֧����ȱ��ʱ������쳣!", e);
		}
	}

	/**
	 * ��ѯ�ظ�������ļ��Ƿ��ж�Ӧʧ�ܵļ�¼ - ��Ȩ֧��ҵ��
	 * 
	 * @param String
	 *            sorgcode ��������
	 * @param String
	 *            sfilename �ļ�����
	 * @param String
	 *            status ����״̬
	 */
	public static boolean findFailedGrantRecord(String sorgcode,
			String sfilename) throws ITFEBizException {
		TvGrantpaymsgsubDto finddto = new TvGrantpaymsgsubDto();
		finddto.setSorgcode(sorgcode); // ��������
		finddto.setSfilename(sfilename); // �ļ�����
		finddto.setSstatus(DealCodeConstants.DEALCODE_ITFE_FAIL); // ����״̬-����ʧ��
		try {
			List list = CommonFacade.getODB().findRsByDto(finddto);

			if (null == list || list.size() == 0) {
				return false;
			}

			return true;
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ��Ȩ֧����ȱ��ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ��Ȩ֧����ȱ��ʱ������쳣!", e);
		} catch (ValidateException e) {
			logger.error("��ѯ��Ȩ֧����ȱ��ʱ������쳣!", e);
			throw new ITFEBizException("��ѯ��Ȩ֧����ȱ��ʱ������쳣!", e);
		}
	}

	/**
	 * ���������¼��ѯ�ӱ��¼
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
				//ƾ֤��ˮ��
				subdto.setIvousrlno(maindto.getIvousrlno());
				return CommonFacade.getODB().findRsByDto(subdto);
			}

			if (idto instanceof TvGrantpaymsgmainDto) {
				TvGrantpaymsgmainDto maindto = (TvGrantpaymsgmainDto) idto;
				TvGrantpaymsgsubDto subdto = new TvGrantpaymsgsubDto();
				//ƾ֤��ˮ��
				subdto.setIvousrlno(maindto.getIvousrlno());
				subdto.setSpackageticketno(maindto.getSpackageticketno());
				return CommonFacade.getODB().findRsByDto(subdto);
			}

			if (idto instanceof TvPayoutmsgmainDto) {
				TvPayoutmsgmainDto maindto = (TvPayoutmsgmainDto) idto;
				TvPayoutmsgsubDto subdto = new TvPayoutmsgsubDto();
				subdto.setSbizno(maindto.getSbizno()); // ������ˮ��
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			if (idto instanceof TvPayoutfinanceMainDto) {
				TvPayoutfinanceMainDto maindto = (TvPayoutfinanceMainDto) idto;
				TvPayoutfinanceSubDto subdto = new TvPayoutfinanceSubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // ƾ֤��ˮ��
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			if (idto instanceof TvPbcpayMainDto) {
				TvPbcpayMainDto maindto = (TvPbcpayMainDto) idto;
				TvPbcpaySubDto subdto = new TvPbcpaySubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // ƾ֤��ˮ��
				return CommonFacade.getODB().findRsByDto(subdto);
			}

			if (idto instanceof TvPayreckBankDto) {
				TvPayreckBankDto maindto = (TvPayreckBankDto) idto;
				TvPayreckBankListDto subdto = new TvPayreckBankListDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // ƾ֤��ˮ��
				return CommonFacade.getODB().findRsByDto(subdto);
			}

			if (idto instanceof TvPayreckBankBackDto) {
				TvPayreckBankBackDto maindto = (TvPayreckBankBackDto) idto;
				TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // ƾ֤��ˮ��
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			
			if (idto instanceof TvPayoutbackmsgMainDto) {//ʵ���ʽ��˻�
				TvPayoutbackmsgMainDto maindto = (TvPayoutbackmsgMainDto) idto;
				TvPayoutbackmsgSubDto subdto = new TvPayoutbackmsgSubDto();
				subdto.setSbizno(maindto.getSbizno()); // ƾ֤��ˮ��
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			
			//����ֱ��֧��ƾ֤5201����
			if (idto instanceof TfDirectpaymsgmainDto) {
				TfDirectpaymsgmainDto maindto = (TfDirectpaymsgmainDto) idto;
				TfDirectpaymsgsubDto subdto = new TfDirectpaymsgsubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // ƾ֤��ˮ��
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			
			//��������ҵ��ֱ��֧����ϸ8207����
			if (idto instanceof TfPaymentDetailsmainDto) {
				TfPaymentDetailsmainDto maindto = (TfPaymentDetailsmainDto) idto;
				TfPaymentDetailssubDto subdto = new TfPaymentDetailssubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // ƾ֤��ˮ��
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			
			//����ֱ��֧������5253����
			if (idto instanceof TfDirectpayAdjustmainDto) {
				TfDirectpayAdjustmainDto maindto = (TfDirectpayAdjustmainDto) idto;
				TfDirectpayAdjustsubDto subdto = new TfDirectpayAdjustsubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // ƾ֤��ˮ��
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			//������Ȩ֧������5351����
			if (idto instanceof TfGrantpayAdjustmainDto) {
				TfGrantpayAdjustmainDto maindto = (TfGrantpayAdjustmainDto) idto;
				TfGrantpayAdjustsubDto subdto = new TfGrantpayAdjustsubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // ƾ֤��ˮ��
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			// �տ������˿�֪ͨ2252����
			if (idto instanceof TfPaybankRefundmainDto) {
				TfPaybankRefundmainDto maindto = (TfPaybankRefundmainDto) idto;
				TfPaybankRefundsubDto subdto = new TfPaybankRefundsubDto();
				subdto.setIvousrlno(maindto.getIvousrlno()); // ƾ֤��ˮ��
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			// ˰Ʊ����֪ͨ5671����
			if (idto instanceof TvNontaxmainDto) {
				TvNontaxmainDto maindto = (TvNontaxmainDto) idto;
				TvNontaxsubDto subdto = new TvNontaxsubDto();
				subdto.setSdealno(maindto.getSdealno()); // ƾ֤��ˮ��
				return CommonFacade.getODB().findRsByDto(subdto);
			}
			
			throw new ITFEBizException("û�ж����ҵ������!");
		} catch (JAFDatabaseException e) {
			logger.error("��ѯҵ�����ݱ������ݿ��쳣!", e);
			throw new ITFEBizException("��ѯҵ�����ݱ������ݿ��쳣!", e);
		} catch (ValidateException e) {
			logger.error("��ѯҵ�����ݱ������ݿ��쳣!", e);
			throw new ITFEBizException("��ѯҵ�����ݱ������ݿ��쳣!", e);
		}
	}

	/**
	 * ����Ԥ�㼶�δ���õ���Ӧ������
	 * 
	 * @param String
	 *            sbdglevelcode Ԥ�㼶��
	 * @return
	 */
	public static String getBdgLevelNameByCode(String sbdglevelcode) {

		if (MsgConstant.BUDGET_LEVEL_SHARE.equals(sbdglevelcode)) {
			return "����";
		} else if (MsgConstant.BUDGET_LEVEL_CENTER.equals(sbdglevelcode)) {
			return "����";
		} else if (MsgConstant.BUDGET_LEVEL_PROVINCE.equals(sbdglevelcode)) {
			return "ʡ";
		} else if (MsgConstant.BUDGET_LEVEL_DISTRICT.equals(sbdglevelcode)) {
			return "��";
		} else if (MsgConstant.BUDGET_LEVEL_PREFECTURE.equals(sbdglevelcode)) {
			return "��";
		} else if (MsgConstant.BUDGET_LEVEL_VILLAGE.equals(sbdglevelcode)) {
			return "��";
		}

		return sbdglevelcode;
	}

	/**
	 * ����Ԥ���������õ���Ӧ������
	 * 
	 * @param String
	 *            sbdgkindcode Ԥ������
	 * @return
	 */
	public static String getBdgKindNameByCode(String sbdgkindcode) {
		if (MsgConstant.BDG_KIND_IN.equals(sbdgkindcode)) {
			return "Ԥ����";
		} else if (MsgConstant.BDG_KIND_OUT.equals(sbdgkindcode)) {
			return "Ԥ����";
		} else if (MsgConstant.BDG_KIND_IN_CASH.equals(sbdgkindcode)) {
			return "Ԥ�����ݴ�";
		}

		return "Ԥ����";
	}

	/**
	 * �����ļ�����ת��DTO
	 * 
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileObjDto getFileObjByFileName(String filename,Date tzdate,String tzsdate)
			throws ITFEBizException {
		// ���ļ����ƴ���
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();

		if (tmpfilename_new.indexOf(".txt") > 0) {
			// ����˰Ʊ(TBS���˰�ӿ�)
			String tmpfilename = tmpfilename_new.replace(".txt", "");
			if (tmpfilename.length() == 13) {
				// TBS�ӿ�
				// 8λ���ڣ�2λ���κţ�2λҵ�����ͣ�1λ�����ڱ�־���
				fileobj.setSdate(tmpfilename.substring(0, 8)); // ����
				fileobj.setSbatch(tmpfilename.substring(8, 10)); // ���κ�
				fileobj.setSbiztype(tmpfilename.substring(10, 12)); // ҵ������
				fileobj.setCtrimflag(tmpfilename.substring(12, 13)); // �����ڱ�־
				fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_TBS);// TBS�ӿ�����

				if (!BizTypeConstant.BIZ_TYPE_INCOME.equals(fileobj
						.getSbiztype())) {
					throw new ITFEBizException("ҵ�����Ͳ����ϣ�����Ϊ��11��!");
				}
				if (!MsgConstant.TIME_FLAG_NORMAL
						.equals(fileobj.getCtrimflag())
						&& !MsgConstant.TIME_FLAG_TRIM.equals(fileobj
								.getCtrimflag())) {
					throw new ITFEBizException("�����ڱ�־�����ϣ�����Ϊ��0�������� �� ��1��������!");
				}else if(MsgConstant.TIME_FLAG_TRIM.equals(fileobj
						.getCtrimflag())){
					Date adjustdate = tzdate;
					String str = tzsdate;
					Date systemDate = Date.valueOf(str.substring(0, 4)+ "-" + str.substring(4, 6) + "-" + str.substring(6, 8));
					if(com.cfcc.jaf.common.util.DateUtil.after(systemDate,adjustdate)){
						throw new ITFEBizException("������" + com.cfcc.deptone.common.util.DateUtil.date2String(adjustdate) + "�ѹ������ܴ��������ҵ��");
					}
				}
			} else if (tmpfilename.length() == 20) {
				// ��˰�ӿ�
				fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_PLACE);// ��˰�ӿ�����
				fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME); // ҵ������
			} else if (tmpfilename.length() == 26) {
				// TIPS�ӿ�
				fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_TIPS);// TIPS�ӿ�����
				fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME); // ҵ������
			} else {
				throw new ITFEBizException("�ļ�����ʽ����!");
			}
		} else if (tmpfilename_new.indexOf(".xml") > 0) {
			// ֧��ҵ��(ֱ��֧����ȡ���Ȩ֧����ȡ�ʵ���ʽ�ҵ��)
			String tmpfilename = tmpfilename_new.replace(".xml", "");
			if (tmpfilename.length() == 17) {
				// ֱ��(��Ȩ)֧�����ҵ��
				// 8λ���ڣ�4λ��ʾ���ͷ���־��3λ��ʾҵ�����ͣ�PP��ʾ�������ҵ���µ����κţ���01��ʼ��������99
				fileobj.setSdate(tmpfilename.substring(0, 8)); // ����
				fileobj.setSbatch(tmpfilename.substring(8, 12)); // ���ͱ�־
				fileobj.setSbiztype(tmpfilename.substring(12, 15)); // ҵ������
				fileobj.setCtrimflag(tmpfilename.substring(15, 17)); // ���κ�
				if (!BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN_ZJ.equals(fileobj.getSbiztype())&& !BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN_ZJ.equals(fileobj.getSbiztype())) {
					throw new ITFEBizException("ҵ�����Ͳ����ϣ�����Ϊ��201����Ȩ֧�����ҵ�� �� ��301��ֱ��֧�����ҵ��!");
				}
			} else {
				// ʵ���ʽ�ҵ��
				String[] strs = tmpfilename.split("_");
				if (strs.length != 6) {
					throw new ITFEBizException("�ļ�����ʽ����!");
				} else {
					fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_PAY_OUT); // ҵ������ - һ��Ԥ��֧��
					fileobj.setStrecode(strs[1]); // �������
					fileobj.setStaxorgcode(strs[2]); // ��Ʊ��λ
					fileobj.setSdate(strs[4]); // ����
					fileobj.setSpackno(strs[5]); // ����ˮ��
				}
			}
		} else if (tmpfilename_new.indexOf(".dbf") > 0) {
			// ��˰�ӿ�
			fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_NATION);// ��˰�ӿ�����
			fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME); // ҵ������
		} else {
			throw new ITFEBizException("�ļ�����ʽ����!");
		}

		//  ��Ҫ���Ӹ����ֶε�У�飬�������ڣ����κţ�ҵ������
		return fileobj;
	}
	/**
	 * �����ļ�����ת��DTO
	 * 
	 * @param filename
	 * @return
	 * @throws ITFEBizException
	 */
	public static FileObjDto getFileObjByFileName(String filename)
			throws ITFEBizException {
		// ���ļ����ƴ���
		String tmpfilename_new = filename.trim().toLowerCase();
		FileObjDto fileobj = new FileObjDto();

		if (tmpfilename_new.indexOf(".txt") > 0) {
			// ����˰Ʊ(TBS���˰�ӿ�)
			String tmpfilename = tmpfilename_new.replace(".txt", "");
			if (tmpfilename.length() == 13) {
				// TBS�ӿ�
				// 8λ���ڣ�2λ���κţ�2λҵ�����ͣ�1λ�����ڱ�־���
				fileobj.setSdate(tmpfilename.substring(0, 8)); // ����
				fileobj.setSbatch(tmpfilename.substring(8, 10)); // ���κ�
				fileobj.setSbiztype(tmpfilename.substring(10, 12)); // ҵ������
				fileobj.setCtrimflag(tmpfilename.substring(12, 13)); // �����ڱ�־
				fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_TBS);// TBS�ӿ�����

				if (!BizTypeConstant.BIZ_TYPE_INCOME.equals(fileobj
						.getSbiztype())) {
					throw new ITFEBizException("ҵ�����Ͳ����ϣ�����Ϊ��11��!");
				}
				if (!MsgConstant.TIME_FLAG_NORMAL
						.equals(fileobj.getCtrimflag())
						&& !MsgConstant.TIME_FLAG_TRIM.equals(fileobj
								.getCtrimflag())) {
					throw new ITFEBizException("�����ڱ�־�����ϣ�����Ϊ��0�������� �� ��1��������!");
				}else if(MsgConstant.TIME_FLAG_TRIM.equals(fileobj
						.getCtrimflag())){
					Date adjustdate = getAdjustDate();
					String str = getSysDBDate();
					Date systemDate = Date.valueOf(str.substring(0, 4)+ "-" + str.substring(4, 6) + "-" + str.substring(6, 8));
					if(com.cfcc.jaf.common.util.DateUtil.after(systemDate,adjustdate)){
						throw new ITFEBizException("������" + com.cfcc.deptone.common.util.DateUtil.date2String(adjustdate) + "�ѹ������ܴ��������ҵ��");
					}
				}
			} else if (tmpfilename.length() == 20) {
				// ��˰�ӿ�
				fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_PLACE);// ��˰�ӿ�����
				fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME); // ҵ������
			} else if (tmpfilename.length() == 26) {
				// TIPS�ӿ�
				fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_TIPS);// TIPS�ӿ�����
				fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME); // ҵ������
			} else {
				throw new ITFEBizException("�ļ�����ʽ����!");
			}
		} else if (tmpfilename_new.indexOf(".xml") > 0) {
			// ֧��ҵ��(ֱ��֧����ȡ���Ȩ֧����ȡ�ʵ���ʽ�ҵ��)
			String tmpfilename = tmpfilename_new.replace(".xml", "");
			if (tmpfilename.length() == 17) {
				// ֱ��(��Ȩ)֧�����ҵ��
				// 8λ���ڣ�4λ��ʾ���ͷ���־��3λ��ʾҵ�����ͣ�PP��ʾ�������ҵ���µ����κţ���01��ʼ��������99
				fileobj.setSdate(tmpfilename.substring(0, 8)); // ����
				fileobj.setSbatch(tmpfilename.substring(8, 12)); // ���ͱ�־
				fileobj.setSbiztype(tmpfilename.substring(12, 15)); // ҵ������
				fileobj.setCtrimflag(tmpfilename.substring(15, 17)); // ���κ�
				if (!BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN_ZJ.equals(fileobj.getSbiztype())&& !BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN_ZJ.equals(fileobj.getSbiztype())) {
					throw new ITFEBizException("ҵ�����Ͳ����ϣ�����Ϊ��201����Ȩ֧�����ҵ�� �� ��301��ֱ��֧�����ҵ��!");
				}
			} else {
				// ʵ���ʽ�ҵ��
				String[] strs = tmpfilename.split("_");
				if (strs.length != 6) {
					throw new ITFEBizException("�ļ�����ʽ����!");
				} else {
					fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_PAY_OUT); // ҵ������ - һ��Ԥ��֧��
					fileobj.setStrecode(strs[1]); // �������
					fileobj.setStaxorgcode(strs[2]); // ��Ʊ��λ
					fileobj.setSdate(strs[4]); // ����
					fileobj.setSpackno(strs[5]); // ����ˮ��
				}
			}
		} else if (tmpfilename_new.indexOf(".dbf") > 0) {
			// ��˰�ӿ�
			fileobj.setCsourceflag(MsgConstant.MSG_SOURCE_NATION);// ��˰�ӿ�����
			fileobj.setSbiztype(BizTypeConstant.BIZ_TYPE_INCOME); // ҵ������
		} else {
			throw new ITFEBizException("�ļ�����ʽ����!");
		}

		//  ��Ҫ���Ӹ����ֶε�У�飬�������ڣ����κţ�ҵ������
		return fileobj;
	}
	/**
	 * ���ݹ������ȡ�ö�Ӧ�Ļ�������
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
				throw new ITFEBizException("û��ά���������[" + trecode + "]����!");
			}

			return list.get(0).getSorgcode();
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ��������������ݿ��쳣!", e);
			throw new ITFEBizException("��ѯ��������������ݿ��쳣!", e);
		} catch (ValidateException e) {
			logger.error("��ѯ��������������ݿ��쳣!", e);
			throw new ITFEBizException("��ѯ��������������ݿ��쳣!", e);
		}
	}

	/**
	 * ȡ��IMPORT��ʽ���ݿ⵼������ݿ��������( �������������ļ�)
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
	 * ���ݺ��������ѯ����������Կ
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
			throw new ITFEBizException("��ѯ����������Կ����1", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ����������Կ����2", e);
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
				throw new ITFEBizException("��ȡ������ʱ��ʧ��");
			}
		} catch (Exception e) {
			logger.error("��ȡ����������ʧ�ܣ�", e);
			throw new ITFEBizException(e);
		}
	}

		/**
		 * ���ط��������ݿ�ʱ�� ��ʱ���ϵͳʱ�䲻�ԣ�����ȡ���ݿ�ʱ��
		 */
		private static String getSysDBDate() throws ITFEBizException {
			try {
				// ��ȡ��ǰʱ��
				Date now = TSystemFacade.findDBSystemDate();
				// ���ļ����浽����Ŀ¼��
				return DateUtil.date2String2(now);
			} catch (Exception e) {
				throw new ITFEBizException("��ȡϵͳʱ���쳣", e);
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
				logger.error("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
				throw new ITFEBizException("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
			} catch (ValidateException e) {
				logger.error("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
				throw new ITFEBizException("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
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
				logger.error("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
				throw new ITFEBizException("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
			} catch (ValidateException e) {
				logger.error("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
				throw new ITFEBizException("��ѯ��������������ݿ��쳣-�ݹ��ѯȫϽ����!", e);
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
	 * �������д�����Ҷ�Ӧ����������
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
		String errMsg="���д���"+dto.getSbnkno()+"��[֧���кŲ�ѯ]��δά����";	
		if(map==null||map.size()==0)
			throw new ITFEBizException(errMsg);
		TsPaybankDto bankDto = map.get(dto.getSbnkno());
		if(bankDto==null)
			throw new ITFEBizException(errMsg);		
		return bankDto.getSbankname();
	}
	
	/**
	 * �������д�����Ҷ�Ӧ����������
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
	 * ����֧�����ܷ����Ŀ���Ҷ�Ӧ��֧�����ܷ����Ŀ����
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
		String errMsg="֧�����ܷ����Ŀ"+dto.getSfuncsbtcode()+"��[Ԥ���Ŀ��ѯ]��δά����";
		if(map==null||map.size()==0)
			throw new ITFEBizException(errMsg);
		TsBudgetsubjectDto subjectDto = map.get(dto.getSfuncsbtcode());
		if(subjectDto==null)
			throw new ITFEBizException(errMsg);		
		return subjectDto.getSsubjectname();		
	}
	
	/**
	 * ����֧�����ܷ����Ŀ���Ҷ�Ӧ��֧�����ܷ����Ŀ����
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
	 * ����Ԥ���Ŀ������Ҷ�Ӧ��Ԥ���Ŀ����
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
		String errMsg="Ԥ�㵥λ����["+dto.getScorpcode()+"]��Ԥ�㵥λ��������в�����!";
		if(map==null||map.size()==0)
			throw new ITFEBizException(errMsg);
		TdCorpDto subjectDto = map.get(dto.getStrecode() + dto.getScorpcode());
		if(subjectDto==null)
			throw new ITFEBizException(errMsg);		
		return subjectDto.getScorpname();		
	}
	
	/**
	 * ����Ԥ���Ŀ������Ҷ�Ӧ��Ԥ���Ŀ����
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

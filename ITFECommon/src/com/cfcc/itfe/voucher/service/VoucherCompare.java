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
 * ƾ֤���ıȶ���
 * 
 * @author hejianrong
 * @time 2014-09-15
 */
public class VoucherCompare {
	private static Log logger = LogFactory.getLog(VoucherCompare.class);
	private String tmpFailReason = "";// ����ʧ��ԭ��
	private TvVoucherinfoDto checkdto;// ��ԭƾ֤�ȶԵ�ƾ֤��Ϣ
	private IDto checkMaindDto;// ��ԭƾ֤�ȶԵ�������Ϣ
	private List checkSubdtoList;// ��ԭƾ֤�ȶԵ��ӱ���Ϣ
	private TvVoucherinfoDto tempdto;// ��ʼƾ֤��Ϣ(5108ƾ֤)
	private int count = 0;// �ȶԳɹ�ƾ֤����

	/**
	 * ƾ֤�ؼ�ҵ��Ҫ�ع���(����)
	 * 
	 * @param dto
	 *            ��������Ϣ
	 * @param maindto
	 *            ������Ϣ
	 * @param subdtoList
	 *            �ӱ���
	 * @return
	 * @throws ITFEBizException
	 */
	public int VoucherCompare(List<List> succList) throws ITFEBizException {
		// ���Ϻ���ֽ����У��ҵ��Ҫ�سɹ���ƾ֤����Ϊ0��ƾ֤���ȶ�
		if (succList == null || succList.size() == 0)
			return 0;
		else if(((Object)succList.get(0)) instanceof Integer)
			return ((Integer)(Object)succList.get(0)).intValue();
			
		// �Ϻ���ֽ����ɫƾ֤���ıȶ�
		for (List list : succList) {
			TvVoucherinfoDto dto = (TvVoucherinfoDto) list.get(1);
			IDto maindto = (IDto) list.get(0);
			List subdtoList = null;
			// ���У��ɹ����򲻱ȶԣ���Ҫ����Ե���ҵ��
			if (dto.getSstatus().equals(
					DealCodeConstants.VOUCHER_VALIDAT_SUCCESS)) {
				count += 1;
				continue;
			}
			// �ӱ���
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
				throw new ITFEBizException("û�ж����ҵ������!");
			VoucherCompare(dto, maindto, subdtoList);
		}
		return count;
	}

	/**
	 * ƾ֤�ؼ�ҵ��Ҫ�ع���
	 * 
	 * @param dto
	 *            ��������Ϣ
	 * @param maindto
	 *            ������Ϣ
	 * @param subdtoList
	 *            �ӱ���
	 * @return
	 * @throws ITFEBizException
	 */
	public void VoucherCompare(TvVoucherinfoDto dto, IDto maindto,
			List subdtoList) throws ITFEBizException {
		try {
			// �ж�ƾ֤�Ƿ��ظ�
			if (voucherCompareIsRepeat(dto))
				return;
			// 5201��8207��5207��8207��ȫҵ��Ҫ�ع���(һ��һ)
			if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)
					|| dto.getScheckvouchertype().equals(
							MsgConstant.VOUCHER_NO_8207)) {
				voucherCompareAll(dto, maindto, subdtoList);
			}
			// 5108��5201��5106��2301��5108��5253��5106��5351�ǻ��ܹ���
			else
				voucherCompareSummary(dto, maindto, subdtoList);
			// δ�ҵ���Ӧ��ƾ֤���ģ��ȶԲ�������
			if (checkdto == null) {
				// ���������ݷ��뻺��
				SrvCacheFacade.cacheVoucherCompare(dto, maindto, subdtoList);
				return;
			}
			// ���ıȶԳɹ�
			if (StringUtils.isBlank(tmpFailReason))
				voucherCompareUpdateStatusSuccess(dto, maindto, subdtoList);
			// ���ıȶ�ʧ��
			else
				voucherCompareUpdateStatusFail(dto);
			// �ȶԲ������ɾ����Ӧ�Ļ�������
			removeCacheVoucherCompare(dto);
		} catch (ITFEBizException e) {
			logger.error(e);
			// �ȶԲ����쳣ɾ����Ӧ�Ļ�������
			removeCacheVoucherCompare(dto);
			throw new ITFEBizException(e.getMessage());
		} catch (JAFDatabaseException e) {
			logger.error(e);
			removeCacheVoucherCompare(dto);
			throw new ITFEBizException("�鿴���ݿ���Ϣ�����ƾ֤״̬�쳣��", e);
		} catch (Exception e) {
			logger.error(e);
			removeCacheVoucherCompare(dto);
			throw new ITFEBizException("ƾ֤�ȶԲ����쳣��", e);
		}
		tmpFailReason = "";
	}

	/**
	 * ����������Ϣƾ֤���� ���������������к�+�ܽ�� ��ϸ��Ԥ�㵥λ����+���ܿ�Ŀ����+���
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
		// �ӻ�����Ҷ�Ӧ��ƾ֤�ȶ���Ϣ
		List voucherCheckList = SrvCacheFacade.cacheVoucherCompare(dto);
		if (voucherCheckList != null && voucherCheckList.size() > 0) {
			checkdto = (TvVoucherinfoDto) voucherCheckList.get(0);
			checkMaindDto = (IDto) voucherCheckList.get(1);
			checkSubdtoList = (List) voucherCheckList.get(2);
		} else {
			// ������������Ϣ
			checkdto = VoucherUtil.findVoucherDto(dto,
					DealCodeConstants.VOUCHER_VALIDAT);
			// δ�ҵ�������������������Ϣ���ȶԲ�����ֹ
			if (checkdto == null)
				return;
			checkMaindDto = (IDto) VoucherUtil.findMainDtoByVoucher(checkdto)
					.get(0);
			// ��Ȩ֧�����5106�漰��ƾ֤��֣�����Ҫ��ѯ������ҵ���ӱ�ͻ���ҵ��������
			if (dto.getScheckvouchertype().equals(MsgConstant.VOUCHER_NO_5106)) {
				((TvGrantpaymsgmainDto) checkMaindDto).setNmoney(checkdto
						.getNmoney());
				((TvGrantpaymsgmainDto) checkMaindDto)
						.setSpackageticketno(null);
			}
			checkSubdtoList = PublicSearchFacade
					.findSubDtoByMain(checkMaindDto);
		}
		// ����������Ϣƾ֤����
			voucherCompareSummaryMain(dto, VoucherUtil
					.convertMainDtoToMap(maindto), VoucherUtil
					.convertMainDtoToMap(checkMaindDto));
		// �����ȶ�ʧ�ܣ����ٱȶ���ϸ�����̽���
		if (StringUtils.isNotBlank(tmpFailReason))
			return;
		// �ȶ���ϸԤ�㵥λ����+���ܿ�Ŀ����+���
		voucherCompareSummarySub(dto, VoucherUtil
				.convertSubListToMap(subdtoList), VoucherUtil
				.convertSubListToMap(checkSubdtoList));
	}

	/**
	 * ����������Ϣƾ֤���� ���������������к�+�ܽ��
	 * 
	 * @param dto
	 * @param maindtoMap
	 * @param checkMaindtoMap
	 */
	private void voucherCompareSummaryMain(TvVoucherinfoDto dto,
			HashMap maindtoMap, HashMap checkMaindtoMap) {
		String paybankcode = (String) maindtoMap.get("paybankcode");// �����к�
		String checkPaybankcode = (String) checkMaindtoMap.get("paybankcode");// �ȶԴ����к�
		BigDecimal payAmt = MtoCodeTrans.transformBigDecimal(maindtoMap
				.get("payAmt"));// �ܽ��
		BigDecimal checkPayAmt = MtoCodeTrans
				.transformBigDecimal(checkMaindtoMap.get("payAmt"));// �ȶ��ܽ��
		if (!paybankcode.equals(checkPaybankcode)) {
			tmpFailReason += "����[���������к�]����ȣ�" + dto.getSvtcode() + "���������кţ�"
					+ paybankcode + "��" + dto.getScheckvouchertype()
					+ "���������кţ�" + checkPaybankcode + "��";
		}
		if (payAmt.compareTo(checkPayAmt) != 0) {
			tmpFailReason += "����[���]����ȣ�" + dto.getSvtcode() + "��" + payAmt
					+ "��" + dto.getScheckvouchertype() + "��" + checkPayAmt
					+ "��";
		}
	}

	/**
	 * ����������Ϣƾ֤���� ��ϸ��Ԥ�㵥λ����+���ܿ�Ŀ����+���
	 * 
	 * @param dto
	 * @param subdtoMap
	 * @param checkSubdtoMap
	 */
	private void voucherCompareSummarySub(TvVoucherinfoDto dto,
			HashMap subdtoMap, HashMap checkSubdtoMap) {
		// �ȶ���ϸ���ܱ���
		voucherCompareSubDtoCount(subdtoMap, checkSubdtoMap, "��ϸ���ܱ���", dto);
		// ��ϸ���ܱ�������ȣ����ٱȶ���ϸ��ϸ��Ϣ�����̽���
		if (StringUtils.isNotBlank(tmpFailReason))
			return;
		String code = null;// Ԥ�㵥λ����|���ܿ�Ŀ����
		BigDecimal detailPayAmt = BigDecimal.ZERO;// ��ϸ���ܽ��
		BigDecimal checkDetailPayAmt = BigDecimal.ZERO;// �ȶ���ϸ���ܽ��
		// �ȶ���ϸԤ�㵥λ����+���ܿ�Ŀ����+���
		for (Iterator<String> it = subdtoMap.keySet().iterator(); it.hasNext();) {
			code = it.next();
			if (!checkSubdtoMap.containsKey(code)) {
				this.tmpFailReason += dto.getSvtcode() + "��ϸ[Ԥ�㵥λ����]"
						+ code.substring(0, code.indexOf("|")) + "��[֧�����ܿ�Ŀ����]"
						+ code.substring(code.indexOf("|") + 1) + "��"
						+ dto.getScheckvouchertype() + "�����ڡ�";
				continue;
			}
			detailPayAmt = (BigDecimal) subdtoMap.get(code);
			checkDetailPayAmt = (BigDecimal) checkSubdtoMap.get(code);
			if (detailPayAmt.compareTo(checkDetailPayAmt) != 0)
				this.tmpFailReason += "��ϸ��ͬԤ�㵥λ����"
						+ code.substring(0, code.indexOf("|")) + "��֧�����ܿ�Ŀ����"
						+ code.substring(code.indexOf("|") + 1) + "[���]����ȣ�"
						+ dto.getSvtcode() + "��" + detailPayAmt + "��"
						+ dto.getScheckvouchertype() + "��"
						+ checkDetailPayAmt + "��";
		}
	}

	/**
	 * ȫҵ��Ҫ�ع���(һ��һ)
	 * 
	 * @param dto
	 * @param maindto
	 * @param subdtoList
	 * @throws ITFEBizException
	 */
	private void voucherCompareAll(TvVoucherinfoDto dto, IDto maindto,
			List subdtoList) throws ITFEBizException {
		HashMap checkSubdtoMap = new HashMap();
		// �ӻ�����Ҷ�Ӧ��ƾ֤�ȶ���Ϣ
		List voucherCheckList = SrvCacheFacade.cacheVoucherCompare(dto);
		if (voucherCheckList != null && voucherCheckList.size() > 0) {
			checkdto = (TvVoucherinfoDto) voucherCheckList.get(0);
			checkMaindDto = (IDto) voucherCheckList.get(1);
			checkSubdtoList = (List) voucherCheckList.get(2);
		} else {
			// ������������Ϣ
			checkdto = VoucherUtil.findVoucherDto(dto,
					DealCodeConstants.VOUCHER_VALIDAT);
			// δ�ҵ�������������������Ϣ���ȶԲ�����ֹ
			if (checkdto == null)
				return;
			checkMaindDto = (IDto) VoucherUtil.findMainDtoByVoucher(checkdto)
					.get(0);
			checkSubdtoList = PublicSearchFacade
					.findSubDtoByMain(checkMaindDto);
		}
		// �ȶ�������Ϣ
		voucherCompareAllMain(dto, maindto);
		// �����ȶ�ʧ�ܣ����ٱȶ���ϸ�����̽���
		if (StringUtils.isNotBlank(tmpFailReason))
			return;
		// �ȶ���ϸ��Ϣ
		voucherCompareAllSub(dto, VoucherUtil.convertListToMap(subdtoList),
				VoucherUtil.convertListToMap(checkSubdtoList));
	}

	/**
	 * ȫҵ��Ҫ�ع���(һ��һ) �ȶ�������Ϣ
	 * 
	 * @param dto
	 * @param maindto
	 * @param subdtoMap
	 * @param checkSubdtoMap
	 */
	private void voucherCompareAllMain(TvVoucherinfoDto dto, IDto maindto) {
		// �ȶ�������Ϣ
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
			// 5207��8207������Ϣ�ȶ�
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
			// 5201��8207������Ϣ�ȶ�
			voucherCompareMainDto5201(maindto5201, maindto8207);
		}
	}

	/**
	 * ȫҵ��Ҫ�ع���(һ��һ) �ȶ���ϸ��Ϣ
	 * 
	 * @param dto
	 * @param subdtoMap
	 * @param checkSubdtoMap
	 */
	private void voucherCompareAllSub(TvVoucherinfoDto dto, HashMap subdtoMap,
			HashMap checkSubdtoMap) {
		// �ȶԱ�����ϸ����
		voucherCompareSubDtoCount(subdtoMap, checkSubdtoMap, "��ϸ����", dto);
		// ��ϸ��������ȣ����ٱȶ���ϸ��ϸ��Ϣ�����̽���
		if (StringUtils.isNotBlank(tmpFailReason))
			return;
		// �ȶԱ�����ϸId�Ƿ����
		for (Iterator<String> it = subdtoMap.keySet().iterator(); it.hasNext();) {
			String sid = it.next();
			if (!checkSubdtoMap.containsKey(sid))
				tmpFailReason += dto.getSvtcode() + "��ϸId��" + sid + "��"
						+ dto.getScheckvouchertype() + "�����ڡ�";
		}
		// ��ϸId�ȶ�ʧ�ܣ����ٱȶ���ϸ��ϸ��Ϣ�����̽���
		if (StringUtils.isNotBlank(tmpFailReason))
			return;
		// �ȶ���ϸ��ϸ��Ϣ
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
				// 5207��8207��ϸ��ϸ��Ϣ�ȶ�
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
				// 5207��8207��ϸ��ϸ��Ϣ�ȶ�
				voucherCompareSubDto5201(suddto5201, suddto8207);
			}
		}
	}

	/**
	 * ʵ���ʽ�5207���ĺ�������ϸ8207���ıȶ�������Ϣ
	 * 
	 * @param maindto5207
	 * @param maindto8207
	 */
	private void voucherCompareMainDto5207(TvPayoutmsgmainDto maindto5207,
			TfPaymentDetailsmainDto maindto8207) {
		if (maindto5207.getNmoney().compareTo(maindto8207.getNsumamt()) != 0) {
			tmpFailReason += "����[���]����ȣ�" + MsgConstant.VOUCHER_NO_5207 + "��"
					+ maindto5207.getNmoney() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "��"
					+ maindto8207.getNsumamt() + "��";
		}
		if (!maindto5207.getSpayeracct().equals(maindto8207.getSpayacctno())) {
			tmpFailReason += "����[�������˺�]����ȣ�" + MsgConstant.VOUCHER_NO_5207
					+ "�������˺ţ�" + maindto5207.getSpayeracct() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "�������˺ţ�"
					+ maindto8207.getSpayacctno() + "��";
		}
		if (!maindto5207.getSpayername().equals(maindto8207.getSpayacctname())) {
			tmpFailReason += "����[����������]����ȣ�" + MsgConstant.VOUCHER_NO_5207
					+ "���������ƣ�" + maindto5207.getSpayername() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "���������ƣ�"
					+ maindto8207.getSpayacctname() + "��";
		}
		if (!maindto5207.getSpayerbankname().equals(
				maindto8207.getSpayacctbankname())) {
			tmpFailReason += "����[��������������]����ȣ�" + MsgConstant.VOUCHER_NO_5207
					+ "����������������" + maindto5207.getSpayerbankname() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "����������������"
					+ maindto8207.getSpayacctbankname() + "��";
		}
		if (!maindto5207.getSrecbankno().equals(maindto8207.getSpaybankcode())) {
			tmpFailReason += "����[�������б���]����ȣ�" + MsgConstant.VOUCHER_NO_5207
					+ "�������б��룺" + maindto5207.getSrecbankno() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "�������б��룺"
					+ maindto8207.getSpaybankcode() + "��";
		}
		// if(!maindto5207.getSclearbankname().equals(maindto8207.getSpaybankname())){
		// tmpFailReason+="����[������������]����ȣ�"+MsgConstant.VOUCHER_NO_5207+"�����������ƣ�"+
		// maindto5207.getSclearbankname()+"��"+MsgConstant.VOUCHER_NO_8207+"�����������ƣ�"+maindto8207.getSpaybankname()+"��";
		// }
	}

	/**
	 * ʵ���ʽ�5207���ĺ�������ϸ8207���ıȶ���ϸ��Ϣ
	 * 
	 * @param suddto5207
	 * @param suddto8207
	 */
	private void voucherCompareSubDto5207(TvPayoutmsgsubDto suddto5207,
			TfPaymentDetailssubDto suddto8207) {
		if (suddto5207.getNmoney().compareTo(suddto8207.getNpayamt()) != 0) {
			tmpFailReason += "��ϸ[���]����ȣ�" + MsgConstant.VOUCHER_NO_5207 + "��"
					+ suddto5207.getNmoney() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "��"
					+ suddto8207.getNpayamt() + "��";
		}
		if (!suddto5207.getSpayeeacctno().equals(suddto8207.getSpayeeacctno())) {
			tmpFailReason += "��ϸ[�տ����˺�]����ȣ�" + MsgConstant.VOUCHER_NO_5207
					+ "�տ����˺ţ�" + suddto5207.getSpayeeacctno() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "�տ����˺ţ�"
					+ suddto8207.getSpayeeacctno() + "��";
		}
		if (!suddto5207.getSpayeeacctname().equals(
				suddto8207.getSpayeeacctname())) {
			tmpFailReason += "��ϸ[�տ�������]����ȣ�" + MsgConstant.VOUCHER_NO_5207
					+ "�տ������ƣ�" + suddto5207.getSpayeeacctname() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "�տ������ƣ�"
					+ suddto8207.getSpayeeacctname() + "��";
		}
		if (!suddto5207.getSpayeeacctbankname().equals(
				suddto8207.getSpayeeacctbankname())) {
			tmpFailReason += "��ϸ[�տ�������]����ȣ�" + MsgConstant.VOUCHER_NO_5207
					+ "�տ������У�" + suddto5207.getNmoney() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "�տ������У�"
					+ suddto8207.getSpayeeacctbankname() + "��";
		}
	}

	/**
	 * ֱ��֧��5201���ĺ�������ϸ8207���ıȶ�������Ϣ
	 * 
	 * @param dto5201
	 * @param dto8207
	 */
	private void voucherCompareMainDto5201(TfDirectpaymsgmainDto maindto5201,
			TfPaymentDetailsmainDto maindto8207) {
		if (maindto5201.getNpayamt().compareTo(maindto8207.getNsumamt()) != 0) {
			tmpFailReason += "����[���]����ȣ�" + MsgConstant.VOUCHER_NO_5201 + "��"
					+ maindto5201.getNpayamt() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "��"
					+ maindto8207.getNsumamt() + "��";
		}
		if (!maindto5201.getSpayacctno().equals(maindto8207.getSpayacctno())) {
			tmpFailReason += "����[�������˺�]����ȣ�" + MsgConstant.VOUCHER_NO_5201
					+ "�������˺ţ�" + maindto5201.getSpayacctno() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "�������˺ţ�"
					+ maindto8207.getSpayacctno() + "��";
		}
		if (!maindto5201.getSpayacctname()
				.equals(maindto8207.getSpayacctname())) {
			tmpFailReason += "����[����������]����ȣ�" + MsgConstant.VOUCHER_NO_5201
					+ "���������ƣ�" + maindto5201.getSpayacctname() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "���������ƣ�"
					+ maindto8207.getSpayacctname() + "��";
		}
		if (!maindto5201.getSpayacctbankname().equals(
				maindto8207.getSpayacctbankname())) {
			tmpFailReason += "����[��������������]����ȣ�" + MsgConstant.VOUCHER_NO_5201
					+ "����������������" + maindto5201.getSpayacctbankname() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "����������������"
					+ maindto8207.getSpayacctbankname() + "��";
		}
		if (!maindto5201.getSpayeeacctbankno().equals(
				maindto8207.getSpaybankcode())) {
			tmpFailReason += "����[�������б���]����ȣ�" + MsgConstant.VOUCHER_NO_5201
					+ "�������б��룺" + maindto5201.getSpayeeacctbankno() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "�������б��룺"
					+ maindto8207.getSpaybankcode() + "��";
		}
		if (!maindto5201.getSpayeeacctbankname().equals(
				maindto8207.getSpaybankname())) {
			tmpFailReason += "����[������������]����ȣ�" + MsgConstant.VOUCHER_NO_5201
					+ "�����������ƣ�" + maindto5201.getSpayeeacctbankname() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "�����������ƣ�"
					+ maindto8207.getSpaybankname() + "��";
		}
	}

	/**
	 * ֱ��֧��5201���ĺ�������ϸ8207���ıȶ���ϸ��Ϣ
	 * 
	 * @param suddto5201
	 * @param suddto8207
	 */
	private void voucherCompareSubDto5201(TfDirectpaymsgsubDto suddto5201,
			TfPaymentDetailssubDto suddto8207) {
		if (suddto5201.getNpayamt().compareTo(suddto8207.getNpayamt()) != 0) {
			tmpFailReason += "��ϸ[���]����ȣ�" + MsgConstant.VOUCHER_NO_5201 + "��"
					+ suddto5201.getNpayamt() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "��"
					+ suddto8207.getNpayamt() + "��";
		}
		if (!suddto5201.getSpayeeacctno().equals(suddto8207.getSpayeeacctno())) {
			tmpFailReason += "��ϸ[�տ����˺�]����ȣ�" + MsgConstant.VOUCHER_NO_5201
					+ "�տ����˺ţ�" + suddto5201.getSpayeeacctno() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "�տ����˺ţ�"
					+ suddto8207.getSpayeeacctno() + "��";
		}
		if (!suddto5201.getSpayeeacctname().equals(
				suddto8207.getSpayeeacctname())) {
			tmpFailReason += "��ϸ[�տ�������]����ȣ�" + MsgConstant.VOUCHER_NO_5201
					+ "�տ������ƣ�" + suddto5201.getSpayeeacctname() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "�տ������ƣ�"
					+ suddto8207.getSpayeeacctname() + "��";
		}
		if (!suddto5201.getSpayeeacctbankname().equals(
				suddto8207.getSpayeeacctbankname())) {
			tmpFailReason += "��ϸ[�տ�������]����ȣ�" + MsgConstant.VOUCHER_NO_5201
					+ "�տ������У�" + suddto5201.getSpayeeacctbankname() + "��"
					+ MsgConstant.VOUCHER_NO_8207 + "�տ������У�"
					+ suddto8207.getSpayeeacctbankname() + "��";
		}
	}

	/**
	 * �ȶԱ�����ϸ����
	 * 
	 * @param subdtoMap
	 * @param checkSubdtoMap
	 * @param sdemo
	 * @param dto
	 */
	private void voucherCompareSubDtoCount(HashMap subdtoMap,
			HashMap checkSubdtoMap, String sdemo, TvVoucherinfoDto dto) {
		if (subdtoMap.size() != checkSubdtoMap.size())
			tmpFailReason += sdemo + "����ȣ�" + dto.getSvtcode() + sdemo + "��"
					+ subdtoMap.size() + "��" + dto.getScheckvouchertype()
					+ sdemo + "��" + checkSubdtoMap.size() + "��";
	}

	/**
	 * ���ıȶԳɹ�����ƾ֤״̬
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
		// ���ıȶԳɹ����жϱ����Ƿ����������Ľ��бȶ�
		// true �����ȶ� false ���ٱȶԣ����±���״̬Ϊ�ɹ�
		if (VoucherCompreIsContinue(dto, maindto, subdtoList))
			return;
		// ���ıȶԳɹ�������ƾ֤״̬
		VoucherUtil.voucherVerifyUpdateStatus(dto, null, "���ıȶԳɹ�", true);
		VoucherUtil.voucherVerifyUpdateStatus(checkdto, null, "���ıȶԳɹ�", true);
		tempdto = findTempVoucher(dto);
		if (tempdto != null)
			VoucherUtil.voucherVerifyUpdateStatus(tempdto, null, "ҵ��Ҫ�ع��ҳɹ�",
					true);
		logger.debug("=====================���ıȶԳɹ�========================");
		count++;

	}

	/**
	 * ���ıȶ�ʧ�ܸ���ƾ֤״̬
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
		VoucherUtil.voucherVerifyUpdateStatus(dto, null, "ҵ��Ҫ�ع���ʧ�ܣ�"
				+ failReason, false);
		if (checkdto != null)
			VoucherUtil.voucherVerifyUpdateStatus(checkdto, null, "ҵ��Ҫ�ع���ʧ�ܣ�"
					+ failReason, false);
		tempdto = findTempVoucher(dto);
		if (tempdto != null)
			VoucherUtil.voucherVerifyUpdateStatus(tempdto, null, "ҵ��Ҫ�ع���ʧ�ܣ�"
					+ failReason, false);
		logger.debug("=====================���ıȶ�ʧ��========================");
		logger.debug(dto.getSvtcode() + "������" + dto.getScheckvouchertype()
				+ "���ıȶ�ʧ�ܣ�ʧ��ԭ��" + tmpFailReason);
	}

	/**
	 * ���ıȶԳɹ����жϱ����Ƿ����������Ľ��бȶ� true �����ȶ� false ���ٱȶԣ����±���״̬Ϊ�ɹ�
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
		// ���ʵ�5201����Ҫ��8207�ȶ�
		// 5201��5108���ıȶԳɹ���5201��8207���ļ����ȶ�
		if ((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)
				&& dto.getScheckvouchertype().equals(
						MsgConstant.VOUCHER_NO_5201) && "1".equals(dto
				.getShold1().trim()))
				|| (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5201)
						&& dto.getScheckvouchertype().equals(
								MsgConstant.VOUCHER_NO_5108) && StateConstant.BIZTYPE_CODE_BATCH
						.equals(dto.getShold4().trim()))) {
			flag = true;
			String sdemo5108 = "�ȴ�[5201]ƾ֤��[8207]�ı��Ľ���ҵ��Ҫ�ع���";
			String sdemo5201 = "�ȴ�[8207]�ı��Ľ���ҵ��Ҫ�ع���";
			if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {
				dto.setSdemo(sdemo5108);
				checkdto.setSdemo(sdemo5201);
				checkdto.setScheckvouchertype(MsgConstant.VOUCHER_NO_8207);
				tempdto = dto;// ����dtoΪ5108ƾ֤dto
			} else {
				dto.setSdemo(sdemo5201);
				dto.setScheckvouchertype(MsgConstant.VOUCHER_NO_8207);
				checkdto.setSdemo(sdemo5108);
				tempdto = checkdto;
			}
			DatabaseFacade.getODB().update(dto);
			DatabaseFacade.getODB().update(checkdto);
			logger.debug("=====================���ıȶԳɹ�========================");
			if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)) {
				dto = checkdto;
				maindto = checkMaindDto;
				subdtoList = checkSubdtoList;
			}
			// ƾ֤�����ȶ�
			VoucherCompare(dto, maindto, subdtoList);
		}
		return flag;
	}

	/**
	 * 5201��5207ƾ֤�ȶԳɹ���ҵ����������д��Ӧ��8207ƾ֤���
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
				sdemo = checkdto.getSvoucherno() + "��" + maindto5201.getSdemo();
			} else {
				maindto5201 = (TfDirectpaymsgmainDto) checkMaindDto;
				sdemo = dto.getSvoucherno() + "��" + maindto5201.getSdemo();
			}
			// 5201ƾ֤������д��Ӧ��8207ƾ֤���
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
				sdemo = checkdto.getSvoucherno() + "��" + maindto5207.getSdemo();
			} else {
				maindto5207 = (TvPayoutmsgmainDto) checkMaindDto;
				sdemo = dto.getSvoucherno() + "��" + maindto5207.getSdemo();
			}
			// 5207ƾ֤������д��Ӧ��8207ƾ֤���
			maindto5207.setSdemo(sdemo.getBytes().length > 60 ? sdemo
					.substring(0, 30) : sdemo);
			DatabaseFacade.getODB().update(maindto5207);
		}
	}

	/**
	 * �ȶԲ������ɾ����Ӧ�Ļ�������
	 * 
	 * @param dto
	 */
	private void removeCacheVoucherCompare(TvVoucherinfoDto dto) {
		// �ȶԲ������ɾ����Ӧ�Ļ�������
		SrvCacheFacade.removeCacheVoucherCompare(dto);
		SrvCacheFacade.removeCacheVoucherCompare(checkdto);
		SrvCacheFacade.removeCacheVoucherCompare(tempdto);
	}

	/**
	 * ����ԭ5108ƾ֤
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
	 * �ж�ƾ֤�Ƿ��ظ� true �ظ���ƾ֤���ٱȶԣ�����ƾ֤״̬ΪУ��ʧ�� false ���ظ��������ȶ�ƾ֤
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
			tmpFailReason = "����ƾ֤���" + dto.getShold3() + "�ظ���";
		else
			tmpFailReason = "ƾ֤���" + dto.getSvoucherno() + "�ظ���";
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
	 * �Ա� �ص�8207�ͽ��ղ�������8207�������ݶԱ� returnList �ص����� acceptList ���յ�8207
	 */
	public String compare8207AcceptAndReturn(List returnList, List acceptList) {
		// <VoucherFlag>0���͵�/1�ص�/ </ VoucherFlag >
		DecimalFormat df = new DecimalFormat("0.00");
		if (null == returnList || returnList.size() == 0 || null == acceptList
				|| acceptList.size() == 0) {
			return "�Ա�8207��Ϣʱ����֯������Ϣ����";
		}
		TvVoucherinfoDto returnDto = (TvVoucherinfoDto) returnList.get(0);
		TvVoucherinfoDto acceptDto = (TvVoucherinfoDto) acceptList.get(0);
		// �ȶ�ƾ֤������Ϣ
		if (!returnDto.getSvoucherno().equals(acceptDto.getSvoucherno())) {
			return "��ƾ֤��ţ�[" + returnDto.getSvoucherno() + "]��ԭƾ֤���룺["
					+ acceptDto.getSvoucherno() + "]��һ�£�";
		} else if (!returnDto.getStrecode().equals(acceptDto.getStrecode())) {
			return "��ƾ֤������룺[" + returnDto.getStrecode() + "]��ԭƾ֤������룺["
					+ acceptDto.getStrecode() + "]��һ�£�";
		} else if (!returnDto.getSadmdivcode().equals(
				acceptDto.getSadmdivcode())) {
			return "��ƾ֤�����������룺[" + returnDto.getStrecode() + "]��ԭƾ֤�����������룺["
					+ acceptDto.getStrecode() + "]��һ�£�";
		} else if (!MtoCodeTrans.transformString(returnDto.getNmoney()).equals(
				MtoCodeTrans.transformString(acceptDto.getNmoney()))) {
			return "��ƾ��["
					+ MtoCodeTrans.transformString(returnDto.getNmoney())
					+ "]��ԭ��["
					+ MtoCodeTrans.transformString(acceptDto.getNmoney())
					+ "]��һ�£�";
		} else if (!returnDto.getSstyear().equals(acceptDto.getSstyear())) {
			return "��ƾ֤��ȣ�[" + returnDto.getSstyear() + "]��ԭƾ֤��ȣ�["
					+ acceptDto.getSstyear() + "]��һ�£�";
		}
		// �ȶ�������Ϣ
		TfPaymentDetailsmainDto returnTfPaymentDetailsmainDto = (TfPaymentDetailsmainDto) returnList
				.get(1);
		TfPaymentDetailsmainDto acceptTfPaymentDetailsmainDto = (TfPaymentDetailsmainDto) acceptList
				.get(1);
		if (!returnTfPaymentDetailsmainDto.getSvoudate().equals(
				acceptTfPaymentDetailsmainDto.getSvoudate())) {
			return "��ƾ֤���ڣ�[" + returnTfPaymentDetailsmainDto.getSvoudate()
					+ "]��ԭƾ֤���ڣ�[" + acceptTfPaymentDetailsmainDto.getSvoudate()
					+ "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSoriginalvtcode().equals(
				acceptTfPaymentDetailsmainDto.getSoriginalvtcode())) {
			return "����ƾ֤���ͱ�ţ�["
					+ returnTfPaymentDetailsmainDto.getSoriginalvtcode()
					+ "]��ԭ��ƾ֤���ͱ�ţ�["
					+ acceptTfPaymentDetailsmainDto.getSoriginalvtcode()
					+ "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSoriginalvoucherno()
				.equals(acceptTfPaymentDetailsmainDto.getSoriginalvoucherno())) {
			return "����֧��ƾ֤��ţ�["
					+ returnTfPaymentDetailsmainDto.getSoriginalvoucherno()
					+ "]��ԭ��֧��ƾ֤��ţ�["
					+ acceptTfPaymentDetailsmainDto.getSoriginalvoucherno()
					+ "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSfundtypecode().equals(
				acceptTfPaymentDetailsmainDto.getSfundtypecode())) {
			return "���ʽ����ʱ��룺["
					+ returnTfPaymentDetailsmainDto.getSfundtypecode()
					+ "]��ԭ�ʽ����ʱ��룺["
					+ acceptTfPaymentDetailsmainDto.getSfundtypecode()
					+ "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSfundtypename().equals(
				acceptTfPaymentDetailsmainDto.getSfundtypename())) {
			return "���ʽ��������ƣ�["
					+ returnTfPaymentDetailsmainDto.getSfundtypename()
					+ "]��ԭ�ʽ��������ƣ�["
					+ acceptTfPaymentDetailsmainDto.getSfundtypename()
					+ "]��һ�£�";
		} else if (!MtoCodeTrans.transformString(
				df.format(returnTfPaymentDetailsmainDto.getNsumamt())).equals(
				MtoCodeTrans.transformString(acceptTfPaymentDetailsmainDto
						.getNsumamt()))) {
			return "�ֻ���֧����["
					+ MtoCodeTrans
							.transformString(df
									.format(returnTfPaymentDetailsmainDto
											.getNsumamt()))
					+ "]��ԭ����֧����["
					+ MtoCodeTrans
							.transformString(acceptTfPaymentDetailsmainDto
									.getNsumamt()) + "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSpayacctno().equals(
				acceptTfPaymentDetailsmainDto.getSpayacctno())) {
			return "�ָ������˺ţ�[" + returnTfPaymentDetailsmainDto.getSpayacctno()
					+ "]��ԭ�������˺ţ�["
					+ acceptTfPaymentDetailsmainDto.getSpayacctno() + "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSpayacctname().equals(
				acceptTfPaymentDetailsmainDto.getSpayacctname())) {
			return "�ָ��������ƣ�[" + returnTfPaymentDetailsmainDto.getSpayacctname()
					+ "]��ԭ���������ƣ�["
					+ acceptTfPaymentDetailsmainDto.getSpayacctname() + "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSpayacctbankname().equals(
				acceptTfPaymentDetailsmainDto.getSpayacctbankname())) {
			return "�ָ��������У�["
					+ returnTfPaymentDetailsmainDto.getSpayacctbankname()
					+ "]��ԭ���������У�["
					+ acceptTfPaymentDetailsmainDto.getSpayacctbankname()
					+ "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSpaybankcode().equals(
				acceptTfPaymentDetailsmainDto.getSpaybankcode())) {
			return "�ִ������б��룺["
					+ returnTfPaymentDetailsmainDto.getSpaybankcode()
					+ "]��ԭ�������б��룺["
					+ acceptTfPaymentDetailsmainDto.getSpaybankcode() + "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSpaybankname().equals(
				acceptTfPaymentDetailsmainDto.getSpaybankname())) {
			return "�ִ����������ƣ�["
					+ returnTfPaymentDetailsmainDto.getSpaybankname()
					+ "]��ԭ�����������ƣ�["
					+ acceptTfPaymentDetailsmainDto.getSpaybankname() + "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSbusinesstypecode()
				.equals(acceptTfPaymentDetailsmainDto.getSbusinesstypecode())) {
			return "��ҵ�����ͱ��룺["
					+ returnTfPaymentDetailsmainDto.getSbusinesstypecode()
					+ "]��ԭҵ�����ͱ��룺["
					+ acceptTfPaymentDetailsmainDto.getSbusinesstypecode()
					+ "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSbusinesstypename()
				.equals(acceptTfPaymentDetailsmainDto.getSbusinesstypename())) {
			return "��ҵ���������ƣ�["
					+ returnTfPaymentDetailsmainDto.getSbusinesstypename()
					+ "]��ԭҵ���������ƣ�["
					+ acceptTfPaymentDetailsmainDto.getSbusinesstypename()
					+ "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSpaytypecode().equals(
				acceptTfPaymentDetailsmainDto.getSpaytypecode())) {
			return "��֧����ʽ���룺["
					+ returnTfPaymentDetailsmainDto.getSpaytypecode()
					+ "]��ԭ֧����ʽ���룺["
					+ acceptTfPaymentDetailsmainDto.getSpaytypecode() + "]��һ�£�";
		} else if (!returnTfPaymentDetailsmainDto.getSpaytypename().equals(
				acceptTfPaymentDetailsmainDto.getSpaytypename())) {
			return "��֧����ʽ���ƣ�["
					+ returnTfPaymentDetailsmainDto.getSpaytypename()
					+ "]��ԭ֧����ʽ���ƣ�["
					+ acceptTfPaymentDetailsmainDto.getSpaytypename() + "]��һ�£�";
		} else if (StringUtils.isBlank(returnTfPaymentDetailsmainDto
				.getSxpaydate())) {
			return "ʵ��֧�����ڲ���Ϊ�գ�";
		} else if (null == returnTfPaymentDetailsmainDto.getNxsumamt()) {
			return "ʵ��֧�����ܽ���Ϊ�գ�";
		}
		// ��X������Ϣ��¼������Ϣ
		acceptTfPaymentDetailsmainDto
				.setSxpaydate(returnTfPaymentDetailsmainDto.getSxpaydate()); // ʵ��֧������
		acceptTfPaymentDetailsmainDto.setNxsumamt(returnTfPaymentDetailsmainDto
				.getNxsumamt()); // ʵ��֧�����ܽ��
		// �ȶ���ϸ
		List returnTfList = (List) returnList.get(2);
		List acceptTfList = (List) acceptList.get(2);
		if (null == returnList || returnTfList.size() == 0
				|| null == acceptTfList || acceptTfList.size() == 0) {
			return "��ԭƾ֤��ϸ��Ϣ������";
		} else if (returnTfList.size() != acceptTfList.size()) {
			return "��ԭƾ֤��ϸ��Ϣ����������";
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
				return "��ԭƾ֤��ϸ��Ϣ������";
			} else if (!returnSubDto.getSid().equals(acceptSubDto.getSid())) {
				return "����ϸ��ţ�[" + returnSubDto.getSid() + "]��ԭ��ϸ��ţ�["
						+ acceptSubDto.getSid() + "]��һ�£�";
			} else if (!returnSubDto.getSpayeeacctno().equals(
					acceptSubDto.getSpayeeacctno())) {
				return "���տ����˺ţ�[" + returnSubDto.getSid() + "]��ԭ�տ����˺ţ�["
						+ acceptSubDto.getSid() + "]��һ�£�";
			} else if (!returnSubDto.getSpayeeacctname().equals(
					acceptSubDto.getSpayeeacctname())) {
				return "���տ������ƣ�[" + returnSubDto.getSpayeeacctname()
						+ "]��ԭ�տ������ƣ�[" + acceptSubDto.getSpayeeacctname()
						+ "]��һ�£�";
			} else if (!returnSubDto.getSpayeeacctbankname().equals(
					acceptSubDto.getSpayeeacctbankname())) {
				return "���տ������У�[" + returnSubDto.getSpayeeacctbankname()
						+ "]��ԭ�տ������У�[" + acceptSubDto.getSpayeeacctbankname()
						+ "]��һ�£�";
			} else if (!MtoCodeTrans.transformString(
					df.format(returnSubDto.getNpayamt())).equals(
					MtoCodeTrans.transformString((acceptSubDto.getNpayamt())))) {
				return "��֧����["
						+ MtoCodeTrans.transformString(df.format(returnSubDto
								.getNpayamt()))
						+ "]��ԭ֧����["
						+ MtoCodeTrans.transformString(acceptSubDto
								.getNpayamt()) + "]��һ�£�";
			} else if (StringUtils.isBlank(returnSubDto.getSxpaydate())) {
				return "ʵ��֧�����ڲ���Ϊ�գ�";
			} else if (StringUtils.isBlank(returnSubDto.getSxagentbusinessno())) {
				return "���н�����ˮ�Ų���Ϊ�գ�";
			} else if (null == returnSubDto.getNxpayamt()) {
				return "ʵ��֧������Ϊ�գ�";
			} else if (StringUtils.isBlank(returnSubDto
					.getSxpayeeacctbankname())) {
				return "ʵ���տ������в���Ϊ�գ�";
			} else if (StringUtils.isBlank(returnSubDto.getSxpayeeacctno())) {
				return "ʵ���տ����˺Ų���Ϊ�գ�";
			}
			acceptSubDto.setSxpaydate(returnSubDto.getSxpaydate()); // ʵ��֧������
			acceptSubDto.setSxagentbusinessno(returnSubDto
					.getSxagentbusinessno()); // ���н�����ˮ��
			acceptSubDto.setNxpayamt(returnSubDto.getNxpayamt()); // ʵ��֧�����
			acceptSubDto.setSxpayeeacctno(returnSubDto.getSxpayeeacctno()); // ʵ���տ����˺�
			acceptSubDto.setSxaddwordcode(returnSubDto.getSxaddwordcode()); // ʧ��ԭ�����
			acceptSubDto.setSxaddword(returnSubDto.getSxaddword()); // ʧ��ԭ��

		}

		return "";
	}

	/**
	 * ��װ8207��ϸ��Ϣ���жԱ� key��Ϊ8207Id vouale��Ϊsubdto
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

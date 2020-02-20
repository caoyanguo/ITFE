package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

public class VoucherDto2MapFor3208 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3208.class);

	@SuppressWarnings("unchecked")
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException {
		List list = findMainDto(vDto);
		if (list.size() == 0)
			return list;
		List lists = new ArrayList();
		for (TvVoucherinfoAllocateIncomeDto mainDto : (List<TvVoucherinfoAllocateIncomeDto>) list) {
//			List<TvVoucherinfoDto> voutherList = voucherIsRepeat(vDto, mainDto);
//			if (voutherList != null && voutherList.size() > 0) {
//				continue;
//			}
			lists.add(voucherTranfor(vDto, mainDto));
		}
		return lists;
	}

	/**
	 * ����ƾ֤ ����ƾ֤����
	 * 
	 * @param vDto
	 * @param mainDto
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	private List voucherTranfor(TvVoucherinfoDto vDto,
			TvVoucherinfoAllocateIncomeDto mainDto) throws ITFEBizException {
		TvVoucherinfoDto dto = (TvVoucherinfoDto) vDto.clone();
		dto.setSdealno(VoucherUtil.getGrantSequence());
		dto.setSvoucherno(dto.getSdealno());
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));
		dto.setSvoucherflag("1");
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto
				.getScreatdate(), dto.getSdealno()));
		dto.setSadmdivcode(mainDto.getSadmdivcode());
		dto.setSvoucherno(mainDto.getSdealno());
		dto.setNmoney(mainDto.getNmoney());
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("����ɹ�");
		dto.setShold4("1");	//��ʶ�ñ�ΪTC�ʽ���
		List lists = new ArrayList();
		lists.add(dto);
		lists.add(mainDto);
		Map map = tranforPayoutBack(lists);
		lists.clear();
		lists.add(map);
		lists.add(dto);
		return lists;
	}

	/**
	 * ��ѯʵ���ʽ��˿�ҵ�����Ϣ
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List findMainDto(TvVoucherinfoDto dto) throws ITFEBizException {
		TvVoucherinfoAllocateIncomeDto mainDto = new TvVoucherinfoAllocateIncomeDto();
		mainDto.setSorgcode(dto.getSorgcode());
		mainDto.setStrecode(dto.getStrecode());
		mainDto.setScommitdate(dto.getScreatdate());
		mainDto.setSvtcode(dto.getSvtcode());
		try {
			return CommonFacade.getODB().findRsByDto(mainDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ʊ����Ϣ����", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ʊ����Ϣ����", e);
		}
	}

	/**
	 * ƾ֤����
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List voucherIsRepeat(TvVoucherinfoDto dto,
			TvVoucherinfoAllocateIncomeDto mainDto) throws ITFEBizException {
		TvVoucherinfoDto vDto = (TvVoucherinfoDto) dto.clone();
		vDto.setSadmdivcode(mainDto.getSadmdivcode());
		vDto.setSvoucherno(mainDto.getSdealno());
		try {
			return CommonFacade.getODB().findRsByDto(vDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ʊ����Ϣ����", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ʊ����Ϣ����", e);
		}
	}

	/**
	 * DTOת��XML����
	 * 
	 * @param List
	 *            ���ɱ���Ҫ�ؼ���
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map tranforPayoutBack(List lists) throws ITFEBizException {
		try {
			// ����ʵ���ʽ��˿�(���½ӿ�20141015_V2.0�汾)�����ݽڵ����201057100006�������ڵ����000057400006
			if (ITFECommonConstant.SRC_NODE.equals("201057100006")
					|| ITFECommonConstant.SRC_NODE.equals("000057400006")) {
				return tranforPayoutBackForHZ(lists);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) lists
					.get(1);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", vDto.getSdealno());// ʵ���˿�֪ͨ��Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// ������������
			vouchermap.put("StYear", vDto.getSstyear());// ҵ�����
			vouchermap.put("VtCode", vDto.getSvtcode());// ƾ֤���ͱ��
			vouchermap.put("VouDate", vDto.getScreatdate());// ƾ֤����
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// ƾ֤��
			vouchermap.put("AgentBusinessNo", returnValue(dto.getSdealno()));// ԭ���н�����ˮ��
			vouchermap.put("OriBillNo", returnValue(""));// ԭ�������
			vouchermap.put("OriVouDate", returnValue(dto.getShold2()));// ԭ���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(dto.getShold2()));// ԭ֧������
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				vouchermap.put("FundTypeCode", returnValue("1"));// �ʽ����ʱ���
				vouchermap.put("FundTypeName", returnValue("Ԥ������ʽ�"));// �ʽ���������
				vouchermap.put("PayTypeCode", returnValue("91"));// ֧����ʽ����
				vouchermap.put("PayTypeName", returnValue("ʵ��"));// ֧����ʽ����
				// vouchermap.put("AgencyCode", returnValue("")); // Ԥ�㵥λ����
				// vouchermap.put("AgencyName", returnValue("")); // Ԥ�㵥λ����
			} else {
				vouchermap.put("FundTypeCode", returnValue(""));// �ʽ����ʱ���
				vouchermap.put("FundTypeName", returnValue(""));// �ʽ���������
				vouchermap.put("PayTypeCode", returnValue(dto.getSvtcodedes()));// ֧����ʽ����
				vouchermap.put("PayTypeName", returnValue(dto.getStradekind()));// ֧����ʽ����
			}
			vouchermap.put("AgencyCode", returnValue("")); // Ԥ�㵥λ����
			vouchermap.put("AgencyName", returnValue("")); // Ԥ�㵥λ����
			vouchermap.put("ProCatCode", returnValue(""));// ��֧�������
			vouchermap.put("ProCatName", returnValue(""));// ��֧��������
			vouchermap.put("PayAcctNo", returnValue(dto.getSpayacctno()));// �������˺�
			vouchermap.put("PayAcctName", returnValue(dto.getSpayacctname()));// ����������
			vouchermap.put("PayAcctBankName", returnValue(dto
					.getSpayacctbankname()));// ����������
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayeeacctno()));// �տ����˺�
			vouchermap.put("PayeeAcctName",
					returnValue(dto.getSpayeeacctname()));// �տ�������
			vouchermap.put("PayeeAcctBankName", returnValue(dto
					.getSpayeeacctbankname()));// �տ�������
			vouchermap.put("PayeeAcctBankNo", returnValue(dto
					.getSpayeeacctbankname()));// �տ������к�
			vouchermap.put("PaySummaryCode", returnValue(""));// ��;����
			vouchermap.put("PaySummaryName", returnValue(""));// ��;����
			vouchermap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(dto.getNmoney()));// �����˿���
			vouchermap.put("Remark", returnValue(dto.getSdemo())); // ��ע��Ϣ
			vouchermap.put("Hold1", returnValue(dto.getSdemo()));// Ԥ���ֶ�1����ռ��"000043100019".equals(ITFECommonConstant.SRC_NODE)?dto.getSdemo():""
			vouchermap.put("Hold2", returnValue(dto.getStradekind()));// Ԥ���ֶ�2

			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			Detailmap.put("Id", dto.getSdealno()); // �˿�֪ͨ����ϸ���
			Detailmap.put("VoucherBillId", dto.getSdealno()); // �˿�֪ͨ��Id
			Detailmap.put("BgtTypeCode", returnValue("")); // Ԥ�����ͱ���
			Detailmap.put("BgtTypeName", returnValue("")); // Ԥ����������
			Detailmap.put("ProCatCode", returnValue("")); // ��֧�������
			Detailmap.put("ProCatName", returnValue("")); // ��֧��������
			Detailmap.put("PayeeAcctNo", returnValue(dto.getSpayeeacctno()));// �տ����˺�
			Detailmap.put("PayeeAcctName",returnValue(dto.getSpayeeacctname()));// �տ�������
			Detailmap.put("PayeeAcctBankName", returnValue(dto.getSpayeeacctbankname()));// �տ�������
			Detailmap.put("PayeeAcctBankNo", returnValue(dto.getSpayeeacctbankname()));// �տ������к�
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				Detailmap.put("ExpFuncCode", returnValue("99999")); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName", returnValue("99999")); // ֧�����ܷ����Ŀ����
				Detailmap.put("AgencyCode", returnValue("99999")); // Ԥ�㵥λ����
				Detailmap.put("AgencyName", returnValue("99999")); // Ԥ�㵥λ����
			} else {
				Detailmap.put("AgencyCode", returnValue("")); // Ԥ�㵥λ����
				Detailmap.put("AgencyName", returnValue("")); // Ԥ�㵥λ����
				Detailmap.put("ExpFuncCode", returnValue("")); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName", returnValue("")); // ֧�����ܷ����Ŀ����
			}
			Detailmap.put("ExpFuncCode1", returnValue("-"));// ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncName1", returnValue("-"));// ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncCode2", returnValue("-"));// ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncName2", returnValue("-"));// ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncCode3", returnValue("-"));// ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncName3", returnValue("-"));// ֧�����ܷ����Ŀ����
			Detailmap.put("ExpEcoCode", returnValue("-")); // ֧�����÷����Ŀ����
			Detailmap.put("ExpEcoName", returnValue("-")); // ֧�����÷����Ŀ����
			Detailmap.put("Amt", "-"
					+ MtoCodeTrans.transformString(dto.getNmoney())); // �˿���
			Detailmap.put("Hold1", returnValue("")); // Ԥ���ֶ�1
			Detailmap.put("Hold2", returnValue("")); // Ԥ���ֶ�2
			Detailmap.put("Hold3", returnValue("")); // Ԥ���ֶ�3
			Detailmap.put("Hold4", returnValue("")); // Ԥ���ֶ�4
			Detail.add(Detailmap);

			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("��װƾ֤�����쳣��", e);
		}
	}

	/**
	 * ����ʵ���ʽ��˿�_TCBS�ʽ��ĵ���(20141015_V2.0���½ӿ�)
	 * 
	 * @param lists
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> tranforPayoutBackForHZ(List lists)
			throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) lists
					.get(1);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", vDto.getSdealno());// ʵ���˿�֪ͨ��Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// ������������
			vouchermap.put("StYear", vDto.getSstyear());// ҵ�����
			vouchermap.put("VtCode", vDto.getSvtcode());// ƾ֤���ͱ��
			vouchermap.put("VouDate", vDto.getScreatdate());// ƾ֤����
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// ƾ֤��
			vouchermap.put("AgentBusinessNo", returnValue(dto.getSdealno()));// ԭ���н�����ˮ��
			vouchermap.put("OriBillNo", returnValue(""));// ԭ�������
			vouchermap.put("OriVouDate", returnValue(dto.getShold2()));// ԭ���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(dto.getShold2()));// ԭ֧������
			vouchermap.put("FundTypeCode", returnValue(""));// �ʽ����ʱ���
			vouchermap.put("FundTypeName", returnValue(""));// �ʽ���������
			vouchermap.put("PayTypeCode", returnValue(dto.getSvtcodedes()));// ֧����ʽ����
			vouchermap.put("PayTypeName", returnValue(dto.getStradekind()));// ֧����ʽ����
			vouchermap.put("ProCatCode", returnValue(""));// ��֧�������
			vouchermap.put("ProCatName", returnValue(""));// ��֧��������
			vouchermap.put("AgencyCode", returnValue("")); // Ԥ�㵥λ����
			vouchermap.put("AgencyName", returnValue("")); // Ԥ�㵥λ����

			vouchermap.put("PayAcctNo", returnValue(dto.getSpayeeacctno()));// ԭ�������˺�
			vouchermap.put("PayAcctName", returnValue(dto.getSpayeeacctname()));// ԭ����������
			HashMap<String, TsPaybankDto> bankmap = null;
			if (StringUtils.isBlank(dto.getShold1())
					&& !StringUtils.isBlank(dto.getSreceivebankno())) {
				bankmap = SrvCacheFacade.cachePayBankInfo();
				TsPaybankDto paybankDto = bankmap.get(dto.getSreceivebankno());
				vouchermap.put("PayAcctBankName", returnValue(paybankDto
						.getSbankname()));// ԭ�տ�������
			} else {
				vouchermap.put("PayAcctBankName", returnValue(dto.getShold1()));// ԭ�տ�������
			}

			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayacctno()));// ԭ�տ����˺�
			vouchermap.put("PayeeAcctName", returnValue(dto.getSpayacctname()));// ԭ�տ�������
			vouchermap.put("PayeeAcctBankName", returnValue(dto
					.getSforwardbankname()));// ԭ�տ�������
			vouchermap.put("PayeeAcctBankNo", returnValue(dto
					.getSpayeeacctbankname()));// ԭ�տ������к�

			vouchermap.put("PaySummaryCode", returnValue(""));// ��;����
			vouchermap.put("PaySummaryName", returnValue(""));// ��;����
			vouchermap.put("Remark", returnValue(dto.getSdemo()));// �˿�ԭ��
			vouchermap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(dto.getNmoney()));// �����˿���
			vouchermap.put("Hold1", returnValue(dto.getSdemo()));// Ԥ���ֶ�1����ռ��"000043100019".equals(ITFECommonConstant.SRC_NODE)?dto.getSdemo():""
			vouchermap.put("Hold2", returnValue(""));// Ԥ���ֶ�2

			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			Detailmap.put("Id", dto.getSdealno()); // �˿�֪ͨ����ϸ���
			Detailmap.put("VoucherBillId", vDto.getSdealno()); // �˿�֪ͨ��Id
			Detailmap.put("BgtTypeCode", returnValue("")); // Ԥ�����ͱ���
			Detailmap.put("BgtTypeName", returnValue("")); // Ԥ����������
			Detailmap.put("ProCatCode", returnValue("")); // ��֧�������
			Detailmap.put("ProCatName", returnValue("")); // ��֧��������
			Detailmap.put("AgencyCode", returnValue("")); // Ԥ�㵥λ����
			Detailmap.put("AgencyName", returnValue("")); // Ԥ�㵥λ����
			Detailmap.put("PayeeAcctNo", returnValue(dto.getSpayacctno()));// ԭ�տ����˺�
			Detailmap.put("PayeeAcctName", returnValue(dto.getSpayacctname()));// ԭ�տ�������
			Detailmap.put("PayeeAcctBankName", returnValue(dto.getSforwardbankname()));// ԭ�տ�������
			Detailmap.put("PayeeAcctBankNo", returnValue(dto.getSpayeeacctbankname()));// ԭ�տ������к�
			Detailmap.put("ExpFuncCode", returnValue("-")); // ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncName", returnValue("-")); // ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncCode1", returnValue("-"));// ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncName1", returnValue("-"));// ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncCode2", returnValue("-"));// ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncName2", returnValue("-"));// ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncCode3", returnValue("-"));// ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncName3", returnValue("-"));// ֧�����ܷ����Ŀ����
			Detailmap.put("ExpEcoCode", returnValue("-")); // ֧�����÷����Ŀ����
			Detailmap.put("ExpEcoName", returnValue("-")); // ֧�����÷����Ŀ����
			Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(dto.getNmoney())); // �˿���
			Detailmap.put("Hold1", returnValue("")); // Ԥ���ֶ�1
			Detailmap.put("Hold2", returnValue("")); // Ԥ���ֶ�2
			Detailmap.put("Hold3", returnValue("")); // Ԥ���ֶ�3
			Detailmap.put("Hold4", returnValue("")); // Ԥ���ֶ�4
			Detail.add(Detailmap);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("��װƾ֤�����쳣��", e);
		}
	}

	/**
	 * DTOת��XML����
	 * 
	 * @param List
	 *            ���ɱ���Ҫ�ؼ���
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map tranfor(List lists) throws ITFEBizException {
		try {
			// ����ʵ���ʽ��˿�(���½ӿ�20141015_V2.0�汾),���ݽڵ����201057100006,�����ڵ����000057400006
			if (ITFECommonConstant.SRC_NODE.equals("201057100006")
					|| ITFECommonConstant.SRC_NODE.equals("000057400006")) {
				return tranforForHZ(lists);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvPayoutmsgmainDto dto = (TvPayoutmsgmainDto) lists.get(1);// ʵ���ʽ𲦿�ƾ֤����
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(2);// ʵ���ʽ𲦿�������
			List<TvPayoutmsgsubDto> sublist = (List<TvPayoutmsgsubDto>) lists
					.get(3);// ʵ���ʽ𲦿���ϸ
			// ʵ���ʽ��˻�ԭ��
			String payoutBackReason = (String) lists.get(4);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", vDto.getSdealno());// ʵ���˿�֪ͨ��Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// ������������
			vouchermap.put("StYear", vDto.getSstyear());// ҵ�����
			vouchermap.put("VtCode", vDto.getSvtcode());// ƾ֤���ͱ��
			vouchermap.put("VouDate", vDto.getScreatdate());// ƾ֤����
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// ƾ֤��
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();

			vouchermap.put("AgentBusinessNo", returnValue(dto
					.getSxagentbusinessno()));// ԭ���н�����ˮ��
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				vouchermap.put("OriBillNo",returnValue(sublist.get(0).getSid()));// ԭ����ƾ֤�Ĳ�����ϸ���
			} else {
				vouchermap.put("OriBillNo",returnValue(dto.getStaxticketno()));// ԭ�������
			}
			vouchermap.put("OriVouDate", returnValue(dto.getSgenticketdate()));// ԭ���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(dto.getSxpaydate()));// ԭ֧������
			vouchermap.put("FundTypeCode", dto.getSfundtypecode());// �ʽ����ʱ���
			vouchermap.put("FundTypeName", dto.getSfundtypename());// �ʽ���������
			vouchermap.put("PayTypeCode", dto.getSpaytypecode());// ֧����ʽ����
			vouchermap.put("PayTypeName", dto.getSpaytypename());// ֧����ʽ����
			vouchermap.put("AgencyCode", returnValue(dto.getSbudgetunitcode())); // Ԥ�㵥λ����
			vouchermap.put("AgencyName", returnValue(dto.getSunitcodename())); // Ԥ�㵥λ����
			vouchermap.put("ProCatCode", returnValue(""));// ��֧�������
			vouchermap.put("ProCatName", returnValue(""));// ��֧��������
			
			vouchermap.put("PayAcctNo", dto.getSpayeracct());// �������˺�vouchermap.put("PayAcctNo",// dto.getSpayeracct());
			vouchermap.put("PayAcctName",dto.getSpayername() );// ����������vouchermap.put("PayAcctName",// dto.getSpayername());
			vouchermap.put("PayAcctBankName", dto.getSpayerbankname());// ����������vouchermap.put("PayAcctBankName",// dto.getSpayerbankname());
			vouchermap.put("PayeeAcctNo", dto.getSrecacct());// �տ����˺�vouchermap.put("PayeeAcctNo",// dto.getSrecacct());
			vouchermap.put("PayeeAcctName", dto.getSrecname());// �տ�������vouchermap.put("PayeeAcctName",// dto.getSrecname());
			vouchermap.put("PayeeAcctBankName",dto.getSrecbankname() );// �տ�������vouchermap.put("PayeeAcctBankName",// dto.getSrecbankname());
			TsPaybankDto paydto = new TsPaybankDto();
			paydto.setSpaybankname(dto.getSrecbankname());
			List<TsPaybankDto> payList = CommonFacade.getODB().findRsByDto(paydto);
			vouchermap.put("PayeeAcctBankNo", payList != null&& payList.size() > 0 ? returnValue(payList.get(0).getSpaybankno()): returnValue(dto.getSrecbankno()));// �տ������к�vouchermap.put("PayeeAcctBankNo",// returnValue(dto.getSrecbankno()));	
			
			vouchermap.put("PaySummaryCode", returnValue(dto.getSpaysummarycode()));// ��;����
			vouchermap.put("PaySummaryName", returnValue(dto.getSpaysummaryname()));// ��;����
			vouchermap.put("PayAmt", "-"+ MtoCodeTrans.transformString(dto.getShold2()));// �����˿���
			vouchermap.put("Remark", returnValue(payoutBackReason)); // ��ע��Ϣ
			if(ITFECommonConstant.PUBLICPARAM.contains(",jilin,")){
				vouchermap.put("Hold1", returnValue(payoutBackReason));///���� ��ǰʹ��shold1�ֶ���Ϊ����ʹ��
			}else{
				vouchermap.put("Hold1", returnValue(dto.getShold1()));// Ԥ���ֶ�1 
			}
			vouchermap.put("Hold2", returnValue(""));// Ԥ���ֶ�2
			TsTreasuryDto tredto = SrvCacheFacade.cacheTreasuryInfo(null).get(dto.getStrecode());
			for (TvPayoutmsgsubDto subdto : sublist) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", dto.getSdealno() + subdto.getSbizno()); // �˿�֪ͨ����ϸ���
				Detailmap.put("VoucherBillId", vDto.getSdealno()); // �˿�֪ͨ��Id
				Detailmap.put("BgtTypeCode", returnValue(subdto.getSbgttypecode())); // Ԥ�����ͱ���
				Detailmap.put("BgtTypeName", returnValue(subdto.getSbgttypename())); // Ԥ����������
				Detailmap.put("ProCatCode",returnValue(subdto.getSprocatcode())); // ��֧�������
				Detailmap.put("ProCatName",returnValue(subdto.getSprocatname())); // ��֧��������
				Detailmap.put("AgencyCode", subdto.getSagencycode()); // Ԥ�㵥λ����
				Detailmap.put("AgencyName", subdto.getSagencyname()); // Ԥ�㵥λ����
				/*Detailmap.put("PayeeAcctNo", dto.getSpayeracct());// �տ����˺�vouchermap.put("PayeeAcctNo",// dto.getSrecacct());
				Detailmap.put("PayeeAcctName", dto.getSpayername());// �տ�������vouchermap.put("PayeeAcctName",// dto.getSrecname());
				Detailmap.put("PayeeAcctBankName", dto.getSpayerbankname());// �տ�������
				Detailmap.put("PayeeAcctBankNo", payList != null&& payList.size() > 0 ? returnValue(payList.get(0).getSpaybankno()): returnValue(dto.getSrecbankno()));// �տ������к�
			*/	
				//���޹��޸�һ������
				Detailmap.put("PayeeAcctNo", dto.getSrecacct());// �տ����˺�vouchermap.put("PayeeAcctNo",// dto.getSrecacct());
				Detailmap.put("PayeeAcctName", dto.getSrecname());// �տ�������vouchermap.put("PayeeAcctName",// dto.getSrecname());
				Detailmap.put("PayeeAcctBankName", dto.getSrecbankname());// �տ�������
				Detailmap.put("PayeeAcctBankNo", payList != null&& payList.size() > 0 ? returnValue(payList.get(0).getSpaybankno()): returnValue(dto.getSrecbankno()));// �տ������к�
				
				Detailmap.put("VoucherDetailNo", (dto.getStaxticketno()==null||"".equals(dto.getStaxticketno()))?"12345678":dto.getStaxticketno());//ԭƾ֤���
				Detailmap.put("ExpFuncCode", subdto.getSfunsubjectcode()); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName", subdto.getSexpfuncname()); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncCode1", "-");// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName1", "-");// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncCode2", "-");// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName2", "-");// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncCode3", "-");// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName3", "-");// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpEcoCode",returnValue(subdto.getSexpecocode())); // ֧�����÷����Ŀ����
				Detailmap.put("ExpEcoName",returnValue(subdto.getSexpeconame())); // ֧�����÷����Ŀ����
				if (dto.getNmoney().compareTo(new BigDecimal(dto.getShold2())) == 0) {
					Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(subdto.getNmoney())); // ȫ���˿���
				} else {
					Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(dto.getShold2())); // �����˿���
				}
				Detailmap.put("Hold1", returnValue(subdto.getShold1())); // Ԥ���ֶ�1
				Detailmap.put("Hold2", returnValue(subdto.getShold2())); // Ԥ���ֶ�2
				Detailmap.put("Hold3", returnValue(subdto.getShold3())); // Ԥ���ֶ�3
				Detailmap.put("Hold4", returnValue(subdto.getShold4())); // Ԥ���ֶ�4
				//�����嵥ģʽ
				if (StateConstant.COMMON_YES.equals(String.valueOf(tredto==null?"":tredto.getSpayunitname()))) {
					vouchermap.put("PayeeAcctNo", subdto.getSpayeeacctno());// �տ����˺�vouchermap.put("PayeeAcctNo",// dto.getSrecacct());
					vouchermap.put("PayeeAcctName", subdto.getSpayeeacctname());// �տ�������vouchermap.put("PayeeAcctName",// dto.getSrecname());
					vouchermap.put("PayeeAcctBankName",subdto.getSpayeeacctbankname() );// �տ�������vouchermap.put("PayeeAcctBankName",// dto.getSrecbankname());
				}
				Detail.add(Detailmap);
			}
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("��װƾ֤�����쳣��", e);
		}
	}

	/**
	 * ����ʵ���ʽ��˿�_ǰ�÷������½ӿ�20141015_V2.0��
	 * 
	 * @param lists
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> tranforForHZ(List lists) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvPayoutmsgmainDto dto = (TvPayoutmsgmainDto) lists.get(1);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(2);
			List<TvPayoutmsgsubDto> sublist = (List<TvPayoutmsgsubDto>) lists
					.get(3);
			// ʵ���ʽ��˻�ԭ��
			String payoutBackReason = (String) lists.get(4);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", vDto.getSdealno());// ʵ���˿�֪ͨ��Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// ������������
			vouchermap.put("StYear", vDto.getSstyear());// ҵ�����
			vouchermap.put("VtCode", vDto.getSvtcode());// ƾ֤���ͱ��
			vouchermap.put("VouDate", vDto.getScreatdate());// ƾ֤����
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// ƾ֤��
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();

			vouchermap.put("AgentBusinessNo", returnValue(dto
					.getSxagentbusinessno()));// ԭ���н�����ˮ��
			vouchermap.put("OriBillNo", returnValue(dto.getStaxticketno()));// ԭ�������
			vouchermap.put("OriVouDate", returnValue(dto.getSgenticketdate()));// ԭ���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(dto.getSxpaydate()));// ԭ֧������
			vouchermap.put("FundTypeCode", dto.getSfundtypecode());// �ʽ����ʱ���
			vouchermap.put("FundTypeName", dto.getSfundtypename());// �ʽ���������
			vouchermap.put("PayTypeCode", dto.getSpaytypecode());// ֧����ʽ����
			vouchermap.put("PayTypeName", dto.getSpaytypename());// ֧����ʽ����
			vouchermap.put("ProCatCode", returnValue(""));// ��֧�������
			vouchermap.put("ProCatName", returnValue(""));// ��֧��������

			vouchermap.put("PayAcctNo", returnValue(dto.getSpayeracct()));// ԭ�������˺�
			vouchermap.put("PayAcctName", returnValue(dto.getSpayername()));// ԭ����������
			vouchermap.put("PayAcctBankName", returnValue(dto
					.getSpayerbankname()));// ԭ����������

			vouchermap.put("PayeeAcctNo", returnValue(dto.getSrecacct()));// ԭ�տ����˺�
			vouchermap.put("PayeeAcctName", returnValue(dto.getSrecname()));// ԭ�տ�������
			vouchermap.put("PayeeAcctBankName", returnValue(dto
					.getSrecbankname()));// ԭ�տ�������
			vouchermap.put("PayeeAcctBankNo", returnValue(dto.getSrecbankno()));// ԭ�տ������к�

			vouchermap.put("PaySummaryCode", returnValue(dto
					.getSpaysummarycode()));// ��;����
			vouchermap.put("PaySummaryName", returnValue(dto.getSaddword()));// ��;����
			// ���ݽڵ����20105710000
			if (ITFECommonConstant.SRC_NODE.equals("201057100006")) {
				vouchermap.put("Remark", returnValue(payoutBackReason));// �˿�ԭ��
				vouchermap.put("Hold1", returnValue(dto.getShold1()));// Ԥ���ֶ�1
			} else {
				vouchermap.put("Hold1", returnValue(payoutBackReason));// Ԥ���ֶ�1(�˿�ԭ��,������)
			}
			vouchermap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(dto.getShold2()));// �����˿���
			vouchermap.put("Hold2", returnValue(""));// Ԥ���ֶ�2
			String subjectCode = "";
			String subjectName = "";
			for (TvPayoutmsgsubDto subdto : sublist) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", subdto.getSbizno()); // �˿�֪ͨ����ϸ���
				Detailmap.put("VoucherBillId", vDto.getSdealno()); // �˿�֪ͨ��Id
				Detailmap.put("BgtTypeCode", returnValue(subdto
						.getSbgttypecode())); // Ԥ�����ͱ���
				Detailmap.put("BgtTypeName", returnValue(subdto
						.getSbgttypename())); // Ԥ����������
				Detailmap.put("ProCatCode",
						returnValue(subdto.getSprocatcode())); // ��֧�������
				Detailmap.put("ProCatName",
						returnValue(subdto.getSprocatname())); // ��֧��������
				subjectCode = subdto.getSagencycode();
				subjectName = subdto.getSagencyname();
				// ���ݽڵ����20105710000
				if (ITFECommonConstant.SRC_NODE.equals("201057100006")) {
					Detailmap.put("AgencyCode", subjectCode); // Ԥ�㵥λ����
					Detailmap.put("AgencyName", subjectName); // Ԥ�㵥λ����
					Detailmap
							.put("PayeeAcctNo", returnValue(dto.getSrecacct()));// ԭ�տ����˺�
					Detailmap.put("PayeeAcctName", returnValue(dto
							.getSrecname()));// ԭ�տ�������
					Detailmap.put("PayeeAcctBankName", returnValue(dto
							.getSrecbankname()));// ԭ�տ�������
					Detailmap.put("PayeeAcctBankNo", returnValue(dto
							.getSrecbankno()));// ԭ�տ������к�
				}
				Detailmap.put("ExpFuncCode", subdto.getSfunsubjectcode()); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName", subdto.getSexpfuncname()); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpEcoCode",
						returnValue(subdto.getSexpecocode())); // ֧�����÷����Ŀ����
				Detailmap.put("ExpEcoName",
						returnValue(subdto.getSexpeconame())); // ֧�����÷����Ŀ����
				if (dto.getNmoney().compareTo(new BigDecimal(dto.getShold2())) == 0) {
					Detailmap.put("Amt", "-"
							+ MtoCodeTrans.transformString(subdto.getNmoney())); // ȫ���˿���
				} else {
					Detailmap.put("Amt", "-"
							+ MtoCodeTrans.transformString(dto.getShold2())); // �����˿���
				}
				Detailmap.put("ExpFuncCode1", returnValue(""));// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName1", returnValue(""));// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncCode2", returnValue(""));// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName2", returnValue(""));// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncCode3", returnValue(""));// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName3", returnValue(""));// ֧�����ܷ����Ŀ����
				Detailmap.put("Hold1", returnValue(subdto.getShold1())); // Ԥ���ֶ�1
				Detailmap.put("Hold2", returnValue(subdto.getShold2())); // Ԥ���ֶ�2
				Detailmap.put("Hold3", returnValue(subdto.getShold3())); // Ԥ���ֶ�3
				Detailmap.put("Hold4", returnValue(subdto.getShold4())); // Ԥ���ֶ�4
				Detail.add(Detailmap);
			}
			vouchermap.put("AgencyCode", subjectCode); // Ԥ�㵥λ����
			vouchermap.put("AgencyName", subjectName); // Ԥ�㵥λ����
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("��װƾ֤�����쳣��", e);
		}
	}

	/**
	 * DTOת��XML����(����TCbs��ִ���ɷ�ʽ)
	 * 
	 * @param List
	 *            ���ɱ���Ҫ�ؼ���
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map tranforForTCBS(TvPayoutbackmsgMainDto maindto,
			TvVoucherinfoDto voucherdto) throws ITFEBizException {
		try {
			// ����ʵ���ʽ��˿�_TCBS��ִ(���½ӿ�20141015_V2.0�汾),���ݽڵ����201057100006,�����ڵ����000057400006
			if (ITFECommonConstant.SRC_NODE.equals("201057100006")
					|| ITFECommonConstant.SRC_NODE.equals("000057400006")) {
				return tranforForTCBSForHZ(maindto, voucherdto);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", voucherdto.getSdealno());// ʵ���˿�֪ͨ��Id
			vouchermap.put("AdmDivCode", voucherdto.getSadmdivcode());// ������������
			vouchermap.put("StYear", voucherdto.getSstyear());// ҵ�����
			vouchermap.put("VtCode", voucherdto.getSvtcode());// ƾ֤���ͱ��

			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			vouchermap.put("VouDate", maindto.getSvoudate());// ƾ֤����
			vouchermap.put("VoucherNo", maindto.getSvouno());// ƾ֤��
			vouchermap.put("AgentBusinessNo", returnValue(maindto.getSoritrano()));// ԭ���н�����ˮ��
			HashMap<String, TsPaybankDto>  bankmap = null;
			try {
				bankmap = SrvCacheFacade.cachePayBankInfo();
			} catch (JAFDatabaseException e1) {
				logger.error("��ȡ����������Ϣ����"+e1);
			}
			//����ʵ���ʽ�ԭ��Ϣ��������Ҫ�ֶ���Ҫ����
			TvPayoutmsgmainDto payinfo = null;
			TvPayoutmsgmainDto payoutdto = new TvPayoutmsgmainDto();
			payoutdto.setStaxticketno(maindto.getSorivouno());
			List payoutlist = CommonFacade.getODB().findRsByDto(payoutdto);
			if (payoutlist.size()>0) {
				payinfo = (TvPayoutmsgmainDto) payoutlist.get(0);
			}else{
				HtvPayoutmsgmainDto hpayoutdto = new HtvPayoutmsgmainDto();
				hpayoutdto.setStaxticketno(maindto.getSorivouno());
				List hpayoutlist = CommonFacade.getODB().findRsByDto(hpayoutdto);
				if (hpayoutlist.size()>0) {
					payinfo = (TvPayoutmsgmainDto) hpayoutlist.get(0);
				}
			}
			if (null==payinfo) {
				payinfo = new TvPayoutmsgmainDto();
			}
			
			if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
				List<TvPayoutmsgsubDto> payoutSublist = this
						.findPayoutSubDtoByMain(maindto);
				if (payoutSublist == null || payoutSublist.size() == 0) {
					vouchermap.put("OriBillNo", "");
				} else {
					vouchermap.put("OriBillNo", returnValue(payoutSublist
							.get(0).getSid()));// ԭ����ƾ֤�Ĳ�����ϸ���
				}
				vouchermap.put("FundTypeCode", payinfo.getSfundtypecode()==null ? "1" : payinfo.getSfundtypecode());// �ʽ����ʱ���
				vouchermap.put("FundTypeName", payinfo.getSfundtypename()==null ? "Ԥ������ʽ�" :payinfo.getSfundtypename());// �ʽ���������
				vouchermap.put("Remark", returnValue(maindto.getSdemo())); // ��ע��Ϣ
			} else {
				vouchermap.put("OriBillNo", returnValue(maindto.getSorivouno()));// ԭ�������
				vouchermap.put("FundTypeCode", payinfo.getSfundtypecode()==null ? "1" : payinfo.getSfundtypecode());// �ʽ����ʱ���
				vouchermap.put("FundTypeName", payinfo.getSfundtypename()==null ? "Ԥ������ʽ�" :payinfo.getSfundtypename());// �ʽ���������
				vouchermap.put("AgencyCode", maindto.getSbudgetunitcode()==null ? payinfo.getSbudgetunitcode() : maindto.getSbudgetunitcode());// Ԥ�㵥λ����
				vouchermap.put("AgencyName",returnValue(maindto.getSunitcodename()==null ? payinfo.getSunitcodename() : maindto.getSunitcodename())); // Ԥ�㵥λ����
				vouchermap.put("Remark", returnValue(maindto.getSdemo())); // ��ע��Ϣ
			}
			vouchermap.put("OriVouDate", returnValue(maindto.getSvoudate()));// ԭ���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(maindto.getSorivoudate()));// ԭ֧������
			vouchermap.put("PayTypeCode", "91");// ֧����ʽ����
			vouchermap.put("PayTypeName", "ʵ���˻�");// ֧����ʽ����
			vouchermap.put("ProCatCode", returnValue(""));// ��֧�������
			vouchermap.put("ProCatName", returnValue(""));// ��֧��������
			vouchermap.put("PayeeAcctNo", returnValue(maindto.getSpayeracct()));// �տ����˺�
			vouchermap.put("PayeeAcctName", returnValue(maindto.getSpayername()));// �տ�������
			TsPaybankDto paydto = bankmap.get(maindto.getSpayername());
			vouchermap.put("PayeeAcctBankName", payinfo.getSrecbankname());// �տ�������
			
			if (!StringUtils.isBlank(maindto.getSpayeeopbkno())) {// �տ��˿������к�
				bankmap = SrvCacheFacade.cachePayBankInfo();
				TsPaybankDto paybankDto = bankmap
						.get(maindto.getSpayeeopbkno());
				vouchermap.put("PayAcctBankName", returnValue(paybankDto
						.getSbankname()));// ԭ����������
			} else {
				vouchermap.put("PayAcctBankName", returnValue(maindto
						.getSpayeebankno()) );// ԭ����������
			}
			if(vouchermap.get("PayeeAcctBankName")==null)
				vouchermap.put("PayeeAcctBankName", returnValue("--"));// �տ�������
			if(vouchermap.get("PayAcctBankName")==null)
				vouchermap.put("PayAcctBankName", returnValue("--"));
			vouchermap.put("PayAcctNo", returnValue(maindto.getSpayeeacct()));// �������˺�
			vouchermap.put("PayAcctName", returnValue(maindto.getSpayeename()));// ����������
			vouchermap.put("PayeeAcctBankNo", paydto!=null ? returnValue(paydto.getSbankno()): returnValue(payinfo.getSrecbankno()));// ԭ�տ������к�
			vouchermap.put("PayeeAcctBankName", paydto!=null ? returnValue(paydto.getSbankname()) : returnValue(payinfo.getSrecbankname()));// ԭ�տ���������
			vouchermap.put("PaySummaryCode", payinfo.getSpaysummarycode()==null ? "" : payinfo.getSpaysummarycode() );// ��;����
			vouchermap.put("PaySummaryName", payinfo.getSpaysummaryname()==null ? maindto.getSbckreason() : payinfo.getSpaysummaryname());// ��;����
			vouchermap.put("PayAmt", "-"+ MtoCodeTrans.transformString(maindto.getNmoney()));// �����˿���
			vouchermap.put("Hold1", (maindto.getSaddword()!=null&&maindto.getSaddword().getBytes().length>42)? CommonUtil.subString(maindto.getSaddword(),42) : maindto.getSaddword());// Ԥ���ֶ�1
			if(ITFECommonConstant.PUBLICPARAM.contains(",xm5207,"))
			{
				if(payoutdto!=null&&payoutdto.getShold1()!=null&&!"".equals(payoutdto.getShold1()))
					vouchermap.put("Hold1",payoutdto.getShold1());
				/*else if(hpayoutdto!=null&&hpayoutdto.getShold1()!=null&&!"".equals(hpayoutdto.getShold1()))
					vouchermap.put("Hold1",hpayoutdto.getShold1());*/
			}
			vouchermap.put("Hold2", "");// Ԥ���ֶ�2
			
			for (int j = 0; j < sublist.size(); j++) {
				TvPayoutbackmsgSubDto subdto = (TvPayoutbackmsgSubDto) sublist
						.get(j);
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", maindto.getSvouno() + subdto.getSseqno()); // �˿�֪ͨ����ϸ���
				Detailmap.put("VoucherBillId", voucherdto.getSdealno()); // �˿�֪ͨ��Id
				Detailmap.put("BgtTypeCode", "-"); // Ԥ�����ͱ���
				Detailmap.put("BgtTypeName", "-"); // Ԥ����������
				Detailmap.put("ProCatCode", ""); // ��֧�������
				Detailmap.put("ProCatName", ""); // ��֧��������
				Detailmap.put("AgencyCode", maindto.getSbudgetunitcode()==null ? payinfo.getSbudgetunitcode() : maindto.getSbudgetunitcode()); // Ԥ�㵥λ����
				Detailmap.put("AgencyName", returnValue(returnValue(maindto.getSunitcodename()==null ? payinfo.getSunitcodename() : maindto.getSunitcodename()))); // Ԥ�㵥λ����
				Detailmap.put("PayeeAcctNo", returnValue(maindto.getSpayeracct()));// ԭ�տ����˺�
				Detailmap.put("PayeeAcctName", returnValue(maindto.getSpayername()));// ԭ�տ�������
				Detailmap.put("PayeeAcctBankName", paydto!=null ? returnValue(paydto.getSbankname()) : returnValue(payinfo.getSrecbankname()));// ԭ�տ���������
				Detailmap.put("PayeeAcctBankNo", paydto!=null ? returnValue(paydto.getSbankno()): returnValue(payinfo.getSrecbankno()));// ԭ�տ������к�
				Detailmap.put("VoucherDetailNo", maindto.getSvouno());//ԭƾ֤���
				Detailmap.put("ExpFuncCode", subdto.getSfunsubjectcode()); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName", returnValue(subdto.getSfunsubjectname())); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncCode1", "-");// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName1", "-");// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncCode2", "-");// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName2", "-");// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncCode3", "-");// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName3", "-");// ֧�����ܷ����Ŀ����
				Detailmap.put("ExpEcoCode", "-"); // ֧�����÷����Ŀ����
				Detailmap.put("ExpEcoName", "-"); // ֧�����÷����Ŀ����
				Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(subdto.getNmoney())); // �˿���
				Detailmap.put("Hold1", returnValue(subdto.getShold1())); // Ԥ���ֶ�1
				Detailmap.put("Hold2", returnValue(subdto.getShold2())); // Ԥ���ֶ�2
				Detailmap.put("Hold3", returnValue(subdto.getShold3())); // Ԥ���ֶ�3
				Detailmap.put("Hold4", returnValue(subdto.getShold4())); // Ԥ���ֶ�4
				Detail.add(Detailmap);
			}
			/***
			 * �����ֶη���棬��������3508ʹ��
			 */
			maindto.setSbudgetunitcode(maindto.getSbudgetunitcode()==null ? payinfo.getSbudgetunitcode() : maindto.getSbudgetunitcode());
			maindto.setSunitcodename(returnValue(returnValue(maindto.getSunitcodename()==null ? payinfo.getSunitcodename() : maindto.getSunitcodename())));
			maindto.setShold1(payinfo.getSrecbankname());
			maindto.setShold2(payinfo.getSrecbankno());
			maindto.setShold3(payinfo.getSfundtypecode()==null ? "1" : payinfo.getSfundtypecode());
			maindto.setShold4(payinfo.getSfundtypename()==null ? "Ԥ������ʽ�" :payinfo.getSfundtypename());
			DatabaseFacade.getODB().update(maindto);
			
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("��װƾ֤�����쳣��", e);
		}
	}

	/**
	 * ����ʵ���ʽ��˿�_TCBS��ִ(���½ӿ�20141015_V2.0�汾)
	 * 
	 * @param maindto
	 * @param voucherdto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> tranforForTCBSForHZ(
			TvPayoutbackmsgMainDto maindto, TvVoucherinfoDto voucherdto)
			throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", voucherdto.getSdealno());// ʵ���˿�֪ͨ��Id
			vouchermap.put("AdmDivCode", voucherdto.getSadmdivcode());// ������������
			vouchermap.put("StYear", voucherdto.getSstyear());// ҵ�����
			vouchermap.put("VtCode", voucherdto.getSvtcode());// ƾ֤���ͱ��

			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			vouchermap.put("VouDate", maindto.getSvoudate());// ƾ֤����
			vouchermap.put("VoucherNo", maindto.getSvouno());// ƾ֤��
			vouchermap.put("AgentBusinessNo", returnValue(maindto
					.getSoritrano()));// ԭ���н�����ˮ��
			vouchermap.put("OriBillNo", returnValue(maindto.getSorivouno()));// ԭ�������
			vouchermap.put("OriVouDate", returnValue(maindto.getSorivoudate()));// ԭ���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(""));// ԭ֧������
			vouchermap.put("FundTypeCode", "");// �ʽ����ʱ���
			vouchermap.put("FundTypeName", "");// �ʽ���������
			vouchermap.put("PayTypeCode", "91");// ֧����ʽ����
			vouchermap.put("PayTypeName", "ʵ���˻�");// ֧����ʽ����
			vouchermap.put("ProCatCode", returnValue(""));// ��֧�������
			vouchermap.put("ProCatName", returnValue(""));// ��֧��������
			vouchermap.put("AgencyCode", maindto.getSbudgetunitcode()); // Ԥ�㵥λ����
			vouchermap.put("AgencyName",
					returnValue(maindto.getSunitcodename())); // Ԥ�㵥λ����

			vouchermap.put("PayAcctNo", returnValue(maindto.getSpayeeacct()));// ԭ�������˺�
			vouchermap.put("PayAcctName", returnValue(maindto.getSpayeename()));// ԭ����������
			HashMap<String, TsPaybankDto> bankmap = null;
			if (!StringUtils.isBlank(maindto.getSpayeeopbkno())) {// �տ��˿������к�
				bankmap = SrvCacheFacade.cachePayBankInfo();
				TsPaybankDto paybankDto = bankmap
						.get(maindto.getSpayeeopbkno());
				vouchermap.put("PayAcctBankName", returnValue(paybankDto
						.getSbankname()));// ԭ����������
			} else {
				vouchermap.put("PayAcctBankName", returnValue(maindto
						.getSpayeename()));// ԭ����������
			}
			vouchermap.put("PayeeAcctNo", returnValue(maindto.getSpayeracct()));// ԭ�տ����˺�
			vouchermap.put("PayeeAcctName",
					returnValue(maindto.getSpayername()));// ԭ�տ�������
			TsPaybankDto paydto = new TsPaybankDto();
			paydto.setSbankname(maindto.getSpayername());
			List<TsPaybankDto> payList = CommonFacade.getODB().findRsByDto(paydto);
			vouchermap.put("PayeeAcctBankName", payList != null
					&& payList.size() > 0 ? payList.get(0).getSbankname()
					: returnValue(maindto.getSpayername()));// ԭ�տ���������
			vouchermap.put("PayeeAcctBankNo", payList != null
					&& payList.size() > 0 ? payList.get(0).getSbankno()
					: returnValue(""));// ԭ�տ������к�

			vouchermap.put("PaySummaryCode", "");// ��;����
			vouchermap
					.put("PaySummaryName", returnValue(maindto.getSaddword()));// ��;����
			// ���ݽڵ����20105710000
			if (ITFECommonConstant.SRC_NODE.equals("201057100006")) {
				vouchermap.put("Remark", returnValue(maindto.getSbckreason()));// �˿�ԭ��
				vouchermap.put("Hold1", "");// Ԥ���ֶ�1
			} else {
				vouchermap.put("Hold1", returnValue(maindto.getSbckreason()));// Ԥ���ֶ�1(�˿�ԭ��,������)
			}
			vouchermap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(maindto.getNmoney()));// �����˿���
			vouchermap.put("Hold2", "");// Ԥ���ֶ�2
			for (int j = 0; j < sublist.size(); j++) {
				TvPayoutbackmsgSubDto subdto = (TvPayoutbackmsgSubDto) sublist
						.get(j);
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", subdto.getSseqno()); // �˿�֪ͨ����ϸ���
				Detailmap.put("VoucherBillId", voucherdto.getSdealno()); // �˿�֪ͨ��Id
				Detailmap.put("BgtTypeCode", ""); // Ԥ�����ͱ���
				Detailmap.put("BgtTypeName", ""); // Ԥ����������
				Detailmap.put("ProCatCode", ""); // ��֧�������
				Detailmap.put("ProCatName", ""); // ��֧��������
				// ���ݽڵ����20105710000
				if (ITFECommonConstant.SRC_NODE.equals("201057100006")) {
					Detailmap.put("AgencyCode", maindto.getSbudgetunitcode()); // Ԥ�㵥λ����
					Detailmap.put("AgencyName", returnValue(maindto.getSunitcodename())); // Ԥ�㵥λ����
					Detailmap.put("PayeeAcctNo", returnValue(maindto.getSpayeracct()));// ԭ�տ����˺�
					Detailmap.put("PayeeAcctName", returnValue(maindto.getSpayername()));// ԭ�տ�������
					Detailmap.put("PayeeAcctBankName", payList != null&& payList.size() > 0 ? payList.get(0).getSbankname() : returnValue(maindto.getSpayername()));// ԭ�տ���������
					Detailmap.put("PayeeAcctBankNo", payList != null&& payList.size() > 0 ? payList.get(0).getSbankno(): returnValue(""));// ԭ�տ������к�
				}
				Detailmap.put("ExpFuncCode", subdto.getSfunsubjectcode()); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName", returnValue(subdto.getSfunsubjectname())); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpEcoCode", ""); // ֧�����÷����Ŀ����
				Detailmap.put("ExpEcoName", ""); // ֧�����÷����Ŀ����
				Detailmap.put("Amt", "-"
						+ MtoCodeTrans.transformString(subdto.getNmoney())); // �˿���
				Detailmap.put("Hold1", returnValue(subdto.getShold1())); // Ԥ���ֶ�1
				Detailmap.put("Hold2", returnValue(subdto.getShold2())); // Ԥ���ֶ�2
				Detailmap.put("Hold3", returnValue(subdto.getShold3())); // Ԥ���ֶ�3
				Detailmap.put("Hold4", returnValue(subdto.getShold4())); // Ԥ���ֶ�4
				Detail.add(Detailmap);
			}
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("��װƾ֤�����쳣��", e);
		}
	}

	/**
	 * ����ԭƾ֤�Ų���ԭ����ƾ֤�Ĳ�����ϸ���
	 * 
	 * @param maindto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List<TvPayoutmsgsubDto> findPayoutSubDtoByMain(
			TvPayoutbackmsgMainDto maindto) throws ITFEBizException {
		TvPayoutmsgmainDto payoutMainDto = new TvPayoutmsgmainDto();
		payoutMainDto.setSorgcode(maindto.getSorgcode());
		payoutMainDto.setStrecode(maindto.getStrecode());
		payoutMainDto.setStaxticketno(maindto.getSorivouno());
		payoutMainDto.setSgenticketdate(maindto.getSorivoudate());
		// payoutMainDto.setSbusinesstypecode(StateConstant.BIZTYPE_CODE_BATCH);
		try {
			List<TvPayoutmsgmainDto> payoutMainDtoList = CommonFacade.getODB()
					.findRsByDto(payoutMainDto);
			if (null != payoutMainDtoList && payoutMainDtoList.size() > 0) {
				TvPayoutmsgsubDto paySubDto = new TvPayoutmsgsubDto();
				paySubDto.setSbizno(payoutMainDtoList.get(0).getSbizno());
				List<TvPayoutmsgsubDto> payDetailDtoList = CommonFacade
						.getODB().findRsByDto(paySubDto);
				if (null != payDetailDtoList && payDetailDtoList.size() > 0) {
					return payDetailDtoList;
				}
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("����ԭƾ֤�Ų���ԭ����ƾ֤�Ĳ�����ϸ��ų���", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("����ԭƾ֤�Ų���ԭ����ƾ֤�Ĳ�����ϸ��ų���", e);
		}
		return null;
	}

	private String returnValue(String value) {
		if (value == null || "".equals(value)) {
			return "";
		} else {
			return value;
		}
	}

	@SuppressWarnings("unchecked")
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

}

package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.DateUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ֱ��֧���˿�֪ͨ(�Ϻ���չ)
 * 
 * @author hejianrong time 2014-10-29
 * 
 */
public class VoucherDto2MapFor2203 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor2203.class);

	/**
	 * ����ƾ֤ ����ƾ֤����
	 * 
	 * @param list
	 * @throws ITFEBizException
	 */
	public Map tranfor(List list) throws ITFEBizException {
		TvVoucherinfoDto dto = null;
		List subdtoList = new ArrayList();
		Map map = null;
		String svtcode = (String) list.get(0);
		IDto idto = (IDto) list.get(1);
		List lists = new ArrayList();
		if (idto instanceof TfDirectpaymsgmainDto) {
			TfDirectpaymsgmainDto maindto = (TfDirectpaymsgmainDto) idto;
			maindto.setSbackflag("1");
			subdtoList = PublicSearchFacade.findSubDtoByMain(maindto);
			if (maindto.getSrefundtype().equals("1")) {
				for (TfDirectpaymsgsubDto suddto : (List<TfDirectpaymsgsubDto>) subdtoList) {
					if (suddto.getIseqno() == 1) {
						subdtoList.clear();
						subdtoList.add(suddto);
						break;
					}
				}
			}
			dto = tranfor(maindto.getSorgcode(), maindto.getStrecode(), maindto
					.getSadmdivcode(), maindto.getNbackmoney(), maindto
					.getSrefundtype(), String.valueOf(maindto.getIvousrlno()));
		} else if (idto instanceof TvPayreckBankBackDto) {
			TvPayreckBankBackDto maindto = (TvPayreckBankBackDto) idto;
			maindto.setSbackflag("1");
			subdtoList = PublicSearchFacade.findSubDtoByMain(maindto);
			dto = tranfor(maindto.getSbookorgcode(), maindto.getStrecode(),
					maindto.getSadmdivcode(), maindto.getFamt(), maindto
							.getSrefundtype(), String.valueOf(maindto
							.getIvousrlno()));

		}
		lists.add(dto);
		lists.add(idto);
		lists.add(subdtoList);
		map = voucherTranfor(lists);
		VoucherUtil.sendTips(dto, map);
		try {
			// ����ƾ֤�������¼
			DatabaseFacade.getODB().create(dto);
			// ����������Ϣ
			DatabaseFacade.getODB().update(idto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("����ƾ֤��Ϣд�����ݿ��쳣!");
		}
		map.put("voucherDto", dto);
		return map;
	}

	@SuppressWarnings("unchecked")
	private TvVoucherinfoDto tranfor(String orgcode, String strecode,
			String admdivcode, BigDecimal amt, String refundtype,
			String voucherno) throws ITFEBizException {
		TvVoucherinfoDto dto = new TvVoucherinfoDto();
		dto.setSorgcode(orgcode);
		dto.setStrecode(strecode);
		dto.setSdealno(VoucherUtil.getGrantSequence());
		dto.setSvoucherno(dto.getSdealno());
		dto.setSvtcode(MsgConstant.VOUCHER_NO_2203);
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));
		dto.setSvoucherflag("1");
		dto.setScreatdate(TimeFacade.getCurrentStringTime());
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto
				.getScreatdate(), dto.getSdealno()));
		dto.setSadmdivcode(admdivcode);
		dto.setNmoney(amt);
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("����ɹ�");
		dto.setScreatdate(TimeFacade.getCurrentStringTime());
		dto.setShold1(refundtype);
		dto.setShold2(voucherno + "01"); // ��ȡԭ�����ĺ�
		return dto;
	}

	/**
	 * DTOת��XML���� ��2202����
	 * 
	 * @param List
	 *            ���ɱ���Ҫ�ؼ���
	 * @return
	 * @throws ITFEBizException
	 */
	public Map tranfor2202(List lists) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvPayreckBankBackDto maindto = (TvPayreckBankBackDto) lists.get(1);
			List<TvPayreckBankBackListDto> subdtoList2202 = (List<TvPayreckBankBackListDto>) lists
					.get(2);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", String.valueOf(vDto.getSdealno()));// ʵ���˿�֪ͨ��Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// ������������
			vouchermap.put("StYear", vDto.getSstyear());// ҵ�����
			vouchermap.put("VtCode", vDto.getSvtcode());// ƾ֤���ͱ��
			vouchermap.put("VouDate", vDto.getScreatdate());// ƾ֤����
			vouchermap.put("VoucherNo", String.valueOf(vDto.getSdealno()));// ƾ֤��
			vouchermap.put("PayBankCode", "");// �������б���
			vouchermap.put("PayBankName", "");// ������������
			vouchermap.put("RefundType", maindto.getSrefundtype());// �˿�����
			if (StateConstant.REFUNDTYPE_5.equals(maindto.getSrefundtype()
					.trim())) { // RefundType=1����Ҫ�˿��ԭ֧��ƾ֤����֧����ϸ���ţ�RefundType=2����Ҫ�˿��ԭ֧��ƾ֤����
				vouchermap.put("OriBillNo", "");// ԭҵ�񵥾ݺ�
				vouchermap.put("AgentBusinessNo", "");// ���н�����ˮ��
			} else {
				vouchermap.put("OriBillNo", maindto.getSorivouno());// ԭҵ�񵥾ݺ�
				vouchermap.put("AgentBusinessNo", maindto.getStrano());// ���н�����ˮ��
			}

			vouchermap.put("PayAcctNo", maindto.getSpayeracct());// ԭ�������˺�
			vouchermap.put("PayAcctName", maindto.getSpayername());// ԭ����������
			vouchermap.put("PayAcctBankName", maindto.getSclearacctbankname());// ԭ����������
			vouchermap.put("PayeeAcctNo", maindto.getSpayeeacct());// ԭ�տ����˺�
			vouchermap.put("PayeeAcctName", maindto.getSpayeename());// ԭ�տ�������
			vouchermap
					.put("PayeeAcctBankName", maindto.getSagentacctbankname());// ԭ�տ�������
			vouchermap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(maindto.getFamt()));// �˿���
			vouchermap.put("Remark", maindto.getSremark());// ��ע��Ϣ
			vouchermap.put("PayDate", maindto.getSxcleardate().toString()
					.replaceAll("-", ""));// ʵ���˿�����
			vouchermap.put("Hold1", "");// Ԥ���ֶ�1
			vouchermap.put("Hold2", "");// Ԥ���ֶ�2
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			int count = 1;
			TfDirectpaymsgsubDto tfDirectpaymsgmainDto;
			for (TvPayreckBankBackListDto subdto : subdtoList2202) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", subdto.getSid()); // �˿�֪ͨ����ϸ���
				Detailmap.put("VoucherBillId", subdto.getSorivouno()
						+ subdto.getSid()); // �˿�֪ͨ��Id ��ƾ֤���+���
				Detailmap.put("BgtTypeCode", maindto.getSbgttypecode()); // Ԥ�����ͱ���
				Detailmap.put("BgtTypeName", maindto.getSbgttypename()); // Ԥ����������
				Detailmap.put("ProCatCode", ""); // ��֧�������
				Detailmap.put("ProCatName", ""); // ��֧��������
				Detailmap.put("PayKindCode", ""); // ֧�����ͱ���
				Detailmap.put("PayKindName", ""); // ֧����������
				Detailmap.put("MOFDepCode", ""); // ҵ���ұ���
				Detailmap.put("MOFDepName", ""); // ҵ��������
				if (StateConstant.REFUNDTYPE_5.equals(maindto.getSrefundtype()
						.trim())) {
					Detailmap.put("AgencyCode", ""); // ����Ԥ�㵥λ����
					Detailmap.put("AgencyName", ""); // ����Ԥ�㵥λ����
					Detailmap.put("ExpFuncCode", "99999"); // ���ܷ����Ŀ����
					Detailmap.put("ExpFuncName", ""); // ���ܷ����Ŀ����
					// ԭ֧��������� ��ȡhold1 �洢5201��voucherno�ֶ�
					Detailmap.put("VoucherNo", subdto.getShold1()); // ԭ֧���������
				} else {
					Detailmap.put("AgencyCode", subdto.getSbdgorgcode()); // ����Ԥ�㵥λ����
					Detailmap.put("AgencyName", subdto.getSsupdepname()); // ����Ԥ�㵥λ����
					Detailmap.put("ExpFuncCode", subdto.getSfuncbdgsbtcode()); // ���ܷ����Ŀ����
					Detailmap.put("ExpFuncName", subdto.getSexpfuncname()); // ���ܷ����Ŀ����
					// ԭ֧��������� ��ȡhold1 �洢5201��voucherno�ֶ�
					Detailmap.put("VoucherNo", subdto.getShold1()); // ԭ֧���������
				}

				Detailmap.put("ExpEcoCode", subdto.getSecnomicsubjectcode()); // ���÷����Ŀ����
				Detailmap.put("ExpEcoName", ""); // ���÷����Ŀ����
				Detailmap.put("DepProCode", ""); // Ԥ����Ŀ����
				Detailmap.put("DepProName", ""); // Ԥ����Ŀ����
				Detailmap.put("PayAcctNo", maindto.getSpayeracct()); // ԭ�������˺�
				Detailmap.put("PayAcctName", maindto.getSpayername()); // ԭ����������
				Detailmap.put("PayAcctBankName", maindto
						.getSclearacctbankname()); // ԭ����������
				Detailmap.put("PayeeAcctNo", maindto.getSpayeeacct()); // ԭ�տ����˺�
				Detailmap.put("PayeeAcctName", maindto.getSpayeename()); // ԭ�տ�������
				Detailmap.put("PayeeAcctBankName", maindto
						.getSagentacctbankname()); // ԭ�տ�������
//				Detailmap.put("PayeeAcctBankNo", maindto.getSagentbnkcode()); // ԭ�տ��������к�
				Detailmap.put("PayeeAcctBankNo", ""); // ԭ�տ��������к�
				Detailmap.put("PayAmt", "-"
						+ MtoCodeTrans.transformString(subdto.getFamt())); // �˿���
				Detailmap.put("Remark", subdto.getSpaysummaryname()); // �˿�ԭ��
				Detailmap.put("Hold1", ""); // Ԥ���ֶ�1
				Detailmap.put("Hold2", ""); // Ԥ���ֶ�2
				Detailmap.put("Hold3", ""); // Ԥ���ֶ�3
				Detailmap.put("Hold4", ""); // Ԥ���ֶ�4
				count++;
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
	 * DTOת��XML���ģ�����ҵ�� ��5201����
	 * 
	 * @param List
	 *            ���ɱ���Ҫ�ؼ���
	 * @return
	 * @throws ITFEBizException
	 */
	public Map tranfor5201(List lists) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TfDirectpaymsgmainDto maindto5201 = (TfDirectpaymsgmainDto) lists
					.get(1);
			List<TfDirectpaymsgsubDto> subdtoList5201 = (List<TfDirectpaymsgsubDto>) lists
					.get(2);
			TfPaybankRefundsubDto refundSubDto = new TfPaybankRefundsubDto();
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", vDto.getSdealno());// ʵ���˿�֪ͨ��Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// ������������
			vouchermap.put("StYear", vDto.getSstyear());// ҵ�����
			vouchermap.put("VtCode", vDto.getSvtcode());// ƾ֤���ͱ��
			vouchermap.put("VouDate", vDto.getScreatdate());// ƾ֤����
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// ƾ֤��
			vouchermap.put("PayBankCode", "");// �������б���
			vouchermap.put("PayBankName", "");// ������������
			vouchermap.put("AgentBusinessNo", maindto5201.getSdealno());// ���н�����ˮ��
			vouchermap.put("OriBillNo", maindto5201.getSvoucherno());// ԭҵ�񵥾ݺ�
			vouchermap.put("RefundType", maindto5201.getSrefundtype());// �˿�����
			vouchermap.put("PayAcctNo", maindto5201.getSpayacctno());// ԭ�������˺�
			vouchermap.put("PayAcctName", maindto5201.getSpayacctname());// ԭ����������
			vouchermap
					.put("PayAcctBankName", maindto5201.getSpayacctbankname());// ԭ����������
			vouchermap.put("PayeeAcctNo", maindto5201.getSpayeeacctno());// ԭ�տ����˺�
			vouchermap.put("PayeeAcctName", maindto5201.getSpayeeacctname());// ԭ�տ�������
			vouchermap.put("PayeeAcctBankName", maindto5201
					.getSpayeeacctbankname());// ԭ�տ�������
			vouchermap
					.put("PayAmt", "-"
							+ MtoCodeTrans.transformString(maindto5201
									.getNbackmoney()));// �˿���
			vouchermap.put("Remark", maindto5201.getSbckreason());// ��ע��Ϣ
			vouchermap.put("PayDate", TimeFacade.getCurrentStringTime());// ʵ���˿�����
			vouchermap.put("Hold1", "");// Ԥ���ֶ�1
			vouchermap.put("Hold2", "");// Ԥ���ֶ�2
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			for (TfDirectpaymsgsubDto subdto5201 : subdtoList5201) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", subdto5201.getSid()); // �˿�֪ͨ����ϸ���
				Detailmap.put("VoucherBillId", vDto.getSdealno()); // �˿�֪ͨ��Id
				Detailmap.put("VoucherNo", subdto5201.getSvoucherno()); // ԭ֧���������
				Detailmap.put("BgtTypeCode", subdto5201.getSbgttypecode()); // Ԥ�����ͱ���
				Detailmap.put("BgtTypeName", subdto5201.getSbgttypename()); // Ԥ����������
				Detailmap.put("ProCatCode", subdto5201.getSprocatcode()); // ��֧�������
				Detailmap.put("ProCatName", subdto5201.getSprocatname()); // ��֧��������
				Detailmap.put("PayKindCode", subdto5201.getSpaykindcode()); // ֧�����ͱ���
				Detailmap.put("PayKindName", subdto5201.getSpaykindname()); // ֧����������
				Detailmap.put("MOFDepCode", subdto5201.getSmofdepcode()); // ҵ���ұ���
				Detailmap.put("MOFDepName", subdto5201.getSmofdepname()); // ҵ��������
				Detailmap.put("AgencyCode", subdto5201.getSagencycode()); // ����Ԥ�㵥λ����
				Detailmap.put("AgencyName", subdto5201.getSagencyname()); // ����Ԥ�㵥λ����
				Detailmap.put("ExpFuncCode", subdto5201.getSexpecocode()); // ���ܷ����Ŀ����
				Detailmap.put("ExpFuncName", subdto5201.getSexpeconame()); // ���ܷ����Ŀ����
				Detailmap.put("ExpEcoCode", subdto5201.getSexpecocode()); // ���÷����Ŀ����
				Detailmap.put("ExpEcoName", subdto5201.getSexpeconame()); // ���÷����Ŀ����
				Detailmap.put("DepProCode", null); // Ԥ����Ŀ����
				Detailmap.put("DepProName", null); // Ԥ����Ŀ����
				Detailmap.put("PayAcctNo", maindto5201.getSpayacctno()); // ԭ�������˺�
				Detailmap.put("PayAcctName", maindto5201.getSpayacctname()); // ԭ����������
				Detailmap.put("PayAcctBankName", maindto5201
						.getSpayacctbankname()); // ԭ����������
				Detailmap.put("PayeeAcctNo", subdto5201.getSpayeeacctno()); // ԭ�տ����˺�
				Detailmap.put("PayeeAcctName", subdto5201.getSpayeeacctname()); // ԭ�տ�������
				Detailmap.put("PayeeAcctBankName", subdto5201
						.getSpayeeacctbankname()); // ԭ�տ�������
//				Detailmap.put("PayeeAcctBankNo", subdto5201
//						.getSpayeeacctbankno()); // ԭ�տ��������к�
				Detailmap.put("PayeeAcctBankNo", ""); // ԭ�տ��������к�
				Detailmap
						.put("PayAmt", "-"
								+ MtoCodeTrans.transformString(subdto5201
										.getNpayamt())); // �˿���
				Detailmap.put("Remark", maindto5201.getSbckreason()); // �˿�ԭ��
				Detailmap.put("Hold1", ""); // Ԥ���ֶ�1
				Detailmap.put("Hold2", ""); // Ԥ���ֶ�2
				Detailmap.put("Hold3", ""); // Ԥ���ֶ�3
				Detailmap.put("Hold4", ""); // Ԥ���ֶ�4
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

	public Map voucherTranfor(List lists) throws ITFEBizException {
		Map map = null;
		IDto maindto = (IDto) lists.get(1);
		if (maindto instanceof TfDirectpaymsgmainDto) {
			map = tranfor5201(lists);
		} else if (maindto instanceof TvPayreckBankBackDto) {
			map = tranfor2202(lists);
		} else if (maindto instanceof TvVoucherinfoAllocateIncomeDto) {
			map = tranfor2203(lists);
		}
		return map;

	}

	@SuppressWarnings("unchecked")
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException {
		List list = findMainDto(vDto);
		if (list.size() == 0)
			return list;
		List lists = new ArrayList();
		for (TvVoucherinfoAllocateIncomeDto mainDto : (List<TvVoucherinfoAllocateIncomeDto>) list) {
			List<TvVoucherinfoDto> voutherList = voucherIsRepeat(vDto, mainDto);
			if (voutherList != null && voutherList.size() > 0) {
				continue;
			}
			List tmpList = (List) voucherTranfor(vDto, mainDto);
			lists.add(tmpList);
			try {
				copyMain2203to2202((TvVoucherinfoDto) tmpList.get(1), mainDto);
				makeSub2203to2202((TvVoucherinfoDto) tmpList.get(1), mainDto);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("����2203ƾ֤��Ϣ����", e);
			} catch (ValidateException e) {
				logger.error(e);
				throw new ITFEBizException("����2203ƾ֤��Ϣ����", e);
			}
		}
		return lists;
	}

	/**
	 * ��ѯֱ��֧�������ļ�ҵ�����Ϣ
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
			throw new ITFEBizException("��ѯֱ��֧������ҵ����Ϣ����", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯֱ��֧������ҵ����Ϣ����", e);
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
			throw new ITFEBizException("��ѯ������ֱ��֧����Ϣ����", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ������ֱ��֧����Ϣ����", e);
		}
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
		// dto.setSvoucherno(mainDto.getSdealno());
		dto.setNmoney(mainDto.getNmoney());
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("����ɹ�");
		dto.setShold2(dto.getSdealno() + "01");
		dto.setShold4("1");	//��ʶ�ñ�ΪTC�ʽ���
		List lists = new ArrayList();
		lists.add(dto);
		lists.add(mainDto);
		Map map = tranfor2203(lists);
		lists.clear();
		lists.add(map);
		lists.add(dto);
		return lists;
	}

	/**
	 * DTOת��XML���� ��2203����
	 * 
	 * @param List
	 *            ���ɱ���Ҫ�ؼ���
	 * @return
	 * @throws ITFEBizException
	 */
	public Map tranfor2203(List lists) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto fundDto = (TvVoucherinfoAllocateIncomeDto) lists
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
			vouchermap.put("PayBankCode", "");// �������б���
			vouchermap.put("PayBankName", "");// ������������
			vouchermap.put("AgentBusinessNo", "");// ���н�����ˮ��
			vouchermap.put("OriBillNo", "");// ԭҵ�񵥾ݺ�
			vouchermap.put("RefundType", StateConstant.REFUNDTYPE_5);// ��ȷ���˿�
			vouchermap.put("PayAcctNo", fundDto.getSpayacctno());// ԭ�������˺�
			vouchermap.put("PayAcctName", fundDto.getSpayacctname());// ԭ����������
			vouchermap.put("PayAcctBankName", fundDto.getSpayacctbankname());// ԭ����������
			vouchermap.put("PayeeAcctNo", fundDto.getSpayeeacctno());// ԭ�տ����˺�
			vouchermap.put("PayeeAcctName", fundDto.getSpayeeacctname());// ԭ�տ�������
			vouchermap
					.put("PayeeAcctBankName", fundDto.getSpayeeacctbankname());// ԭ�տ�������
			vouchermap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(fundDto.getNmoney()));// �˿���
			vouchermap.put("Remark", "��ȷ�����˿�");// ��ע��Ϣ
			vouchermap.put("PayDate", TimeFacade.getCurrentStringTime());// ʵ���˿�����
			vouchermap.put("Hold1", "");// Ԥ���ֶ�1
			vouchermap.put("Hold2", "");// Ԥ���ֶ�2
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			// ��TCBS�ʽ����ҵ��û����ϸ�������ս������һ��
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			Detailmap.put("Id", "1"); // �˿�֪ͨ����ϸ���
			Detailmap.put("VoucherBillId", "1"); // �˿�֪ͨ��Id
			Detailmap.put("VoucherNo", ""); // // ԭ֧���������
			Detailmap.put("BgtTypeCode", ""); // Ԥ�����ͱ���
			Detailmap.put("BgtTypeName", ""); // Ԥ����������
			Detailmap.put("ProCatCode", ""); // ��֧�������
			Detailmap.put("ProCatName", ""); // ��֧��������
			Detailmap.put("PayKindCode", ""); // ֧�����ͱ���
			Detailmap.put("PayKindName", ""); // ֧����������
			Detailmap.put("MOFDepCode", ""); // ҵ���ұ���
			Detailmap.put("MOFDepName", ""); // ҵ��������
			Detailmap.put("AgencyCode", ""); // ����Ԥ�㵥λ����
			Detailmap.put("AgencyName", ""); // ����Ԥ�㵥λ����
			Detailmap.put("ExpFuncCode", "99999"); // ���ܷ����Ŀ����
			Detailmap.put("ExpFuncName", ""); // ���ܷ����Ŀ����
			Detailmap.put("ExpEcoCode", ""); // ���÷����Ŀ����
			Detailmap.put("ExpEcoName", ""); // ���÷����Ŀ����
			Detailmap.put("DepProCode", ""); // Ԥ����Ŀ����
			Detailmap.put("DepProName", ""); // Ԥ����Ŀ����
			Detailmap.put("PayAcctNo", fundDto.getSpayacctno()); // ԭ�������˺�
			Detailmap.put("PayAcctName", fundDto.getSpayacctname()); // ԭ����������
			Detailmap.put("PayAcctBankName", fundDto.getSpayacctbankname()); // ԭ����������
			Detailmap.put("PayeeAcctNo", fundDto.getSpayeeacctno()); // ԭ�տ����˺�
			Detailmap.put("PayeeAcctName", fundDto.getSpayeeacctname());// ԭ�տ�������
			Detailmap.put("PayeeAcctBankName", fundDto.getSpayeeacctbankname()); // ԭ�տ�������
			Detailmap.put("PayeeAcctBankNo", ""); // ԭ�տ��������к�
			Detailmap.put("PayAmt", "-"
					+ MtoCodeTrans.transformString(fundDto.getNmoney())); // �˿���
			Detailmap.put("Remark", "��ȷ�����˿�"); // �˿�ԭ��
			Detailmap.put("Hold1", ""); // Ԥ���ֶ�1
			Detailmap.put("Hold2", ""); // Ԥ���ֶ�2
			Detailmap.put("Hold3", ""); // Ԥ���ֶ�3
			Detailmap.put("Hold4", ""); // Ԥ���ֶ�4
			Detail.add(Detailmap);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("2203��װƾ֤�����쳣��", e);
		}
	}

	/**
	 * ����TCBS�����ʽ�ƾ֤��2203����,����ǰ������2302����ȷ���˿�ֻ������û����ϸ
	 * 
	 * @param dto
	 * @param maindto5201
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException 
	 * @throws ValidateException 
	 */
	private static TvPayreckBankBackDto copyMain2203to2202(
			TvVoucherinfoDto dto, TvVoucherinfoAllocateIncomeDto maindto2203)
			throws JAFDatabaseException, ITFEBizException, ValidateException {
		Map<String, TsInfoconnorgaccDto> accMap = null;
		List<TsInfoconnorgaccDto> accList = null;
		TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
		accdto.setStrecode(dto.getStrecode());
		accdto.setSorgcode(dto.getSorgcode());
		accList = (List<TsInfoconnorgaccDto>) CommonFacade.getODB()
				.findRsByDto(accdto);
		if (accList != null && accList.size() > 0) {
			accMap = new HashMap<String, TsInfoconnorgaccDto>();
			for (TsInfoconnorgaccDto tempdto : accList) {
				if (tempdto.getSpayeraccount().indexOf(
						dto.getSorgcode() + "271") >= 0)
					accMap.put("271", tempdto); // �Ϻ��в����ֹ����
				else if (tempdto.getSpayeraccount().indexOf(dto.getSorgcode() + "371") >= 0 && !(tempdto.getSpayername().indexOf("��Ȩ") >= 0))
					accMap.put("371", tempdto);
			}
		}
		TvPayreckBankBackDto maindto = new TvPayreckBankBackDto();
		maindto.setStrano(maindto2203.getSpaydealno());// ���뻮��ƾ֤Id
		maindto.setIvousrlno(Long.valueOf(dto.getSdealno()));// ƾ֤��ˮ��
		maindto.setSid("1");// ���뻮��ƾ֤Id
		maindto.setSadmdivcode(maindto2203.getSadmdivcode());// ������������
		maindto.setSofyear(maindto2203.getScommitdate().substring(0, 4));// ҵ�����
		maindto.setSvtcode(dto.getSvtcode());// ƾ֤���ͱ��
		maindto.setDvoudate(CommonUtil.strToDate(dto.getScreatdate())); // ƾ֤����
		maindto.setSvouno(dto.getSvoucherno());// ƾ֤��
		maindto.setSbookorgcode(maindto2203.getSorgcode());// �����������
		maindto.setStrecode(maindto2203.getStrecode()); // �����������
		TsConvertfinorgDto tmpFinOrg = getConverFinortInfo(maindto2203.getSorgcode(), maindto2203.getStrecode());
		if(null == tmpFinOrg){
		maindto.setSfinorgcode("000000000000");// �������ش���
		}else{
			maindto.setSfinorgcode(tmpFinOrg.getSfinorgcode());// �������ش���
		}
		maindto.setSbgttypecode("");// Ԥ�����ͱ���
		maindto.setSbgttypename("");// Ԥ����������
		maindto.setSfundtypecode(StateConstant.COMMON_YES);// �ʽ����ʱ���
		maindto.setSfundtypename("Ԥ����");// �ʽ���������
		maindto.setSpaymode("0");
		maindto.setSpaytypecode("0");// ֧����ʽ����
		maindto.setSpaytypename("ֱ��֧��");// ֧����ʽ����
		maindto.setSpayeeacct(accMap.get("271").getSpayeraccount());// �տ����˺�
		maindto.setSpayeename(accMap.get("271").getSpayername());// �տ����˻�����
//		maindto.setSagentacctbankname(maindto2203.getSpayeeacctbankname());// �տ�����
		maindto.setSagentacctbankname("�й����������Ϻ����й��⴦");// �տ�����
		maindto.setSpayeracct(accMap.get("371").getSpayeraccount());// �����˺�
		maindto.setSpayername(accMap.get("371").getSpayername());// �����˻�����
//		maindto.setSclearacctbankname(maindto2203.getSpayacctbankname());// ��������
		maindto.setSclearacctbankname("�й����������Ϻ����й��⴦");// ��������
		maindto.setFamt(maindto2203.getNmoney());// ����������
//		maindto.setSpaybankname(maindto2203.getSpayacctbankname());// ������������
		maindto.setSpaybankname("�й����������Ϻ����й��⴦");// ������������
		maindto.setSagentbnkcode(maindto2203.getSforwardbankno());// ���������к�
		maindto.setSremark(maindto2203.getSdemo());// ժҪ
		maindto.setSmoneycorpcode("");// ���ڻ�������
		maindto.setShold1(maindto2203.getShold1());// Ԥ���ֶ�1
		maindto.setShold2(maindto2203.getShold2());// Ԥ���ֶ�2
		maindto.setDentrustdate(DateUtil.currentDate());// ί������
		maindto.setDorientrustdate(CommonUtil.strToDate(maindto2203
				.getScommitdate()));// ԭί������
		maindto.setSpackno(maindto2203.getSpaydealno());// ����ˮ��
		maindto.setSoritrano(maindto2203.getSpaydealno());// ԭ������ˮ��
		maindto.setDacceptdate(CommonUtil.strToDate(dto.getScreatdate()));// ��������
		maindto.setStrimsign(MsgConstant.TIME_FLAG_NORMAL);// �����ڱ�־
		maindto.setSbudgettype(MsgConstant.BDG_KIND_IN);// Ԥ������(Ĭ��Ԥ����)
		maindto.setDorivoudate(CommonUtil.strToDate(maindto2203
				.getScommitdate()));// ԭƾ֤����
		maindto.setSorivouno("0");// ԭƾ֤���
		maindto.setSpaydictateno(maindto2203.getSpaydealno());// ���֧���˿�����
		maindto.setSpaymsgno(maindto2203.getSreportkind());// ֧�����ı��
		maindto.setDpayentrustdate(CommonUtil.strToDate(maindto2203
				.getScommitdate()));// ֧��ί������
		maindto.setSpaysndbnkno(maindto2203.getSforwardbankno());// ֧���������к�
		maindto.setSfilename(dto.getSfilename());
		maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);// ״̬ ������
		maindto.setIstatinfnum(0);
		maindto.setSxpaysndbnkno(maindto2203.getSforwardbankno());// ֧���������к�
		maindto.setSaddword(maindto2203.getSdemo());// ����
		maindto.setSbackflag(StateConstant.COMMON_YES);
		maindto.setSrefundtype(StateConstant.NOREFUNDTYPE);
		maindto.setNbackmoney(maindto2203.getNmoney());
		maindto.setSbckreason(maindto2203.getSdemo());
		maindto.setSxcleardate(CommonUtil.strToDate(maindto2203
				.getScommitdate()));// ��������
		maindto.setSxpayamt(maindto2203.getNmoney());
		maindto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// ϵͳʱ��
		DatabaseFacade.getODB().create(maindto);
		return maindto;
	}

	/**
	 * ���ݺ�������͹�������ȡ������������
	 * @throws ITFEBizException 
	 */
	private static TsConvertfinorgDto getConverFinortInfo(String orgcode,
			String trecode) throws ITFEBizException {
		try {
			TsConvertfinorgDto searchDto = new TsConvertfinorgDto();
			searchDto.setSorgcode(orgcode);
			searchDto.setStrecode(trecode);
			List<TsConvertfinorgDto> lists = CommonFacade.getODB().findRsByDto(
					searchDto);
			if(null == lists || lists.size() == 0){
				return null;
			}else{
				return lists.get(0);
			}
		} catch (JAFDatabaseException e) {
			logger.error("���ݺ������壺" + orgcode + "������룺" + trecode +"���Ҳ������������쳣��",e);
			throw new ITFEBizException("���ݺ������壺" + orgcode + "������룺" + trecode +"���Ҳ������������쳣��", e);
		} catch (ValidateException e) {
			logger.error("���ݺ������壺" + orgcode + "������룺" + trecode +"���Ҳ������������쳣��",e);
			throw new ITFEBizException("���ݺ������壺" + orgcode + "������룺" + trecode +"���Ҳ������������쳣��", e);
		}
	}

	/**
	 * * ����TCBS�����ʽ�ƾ֤��2203����,����ǰ������2302����ȷ���˿�ֻ��������ϸ�Լ�����һ������Ŀ�Ŀ��Ԥ�㵥λ
	 * 
	 * @param maindto5201
	 * @return
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException
	 */
	private static TvPayreckBankBackListDto makeSub2203to2202(
			TvVoucherinfoDto dto, TvVoucherinfoAllocateIncomeDto maindto2203)
			throws ITFEBizException, JAFDatabaseException {
		TvPayreckBankBackListDto subdto = new TvPayreckBankBackListDto();
		subdto.setIseqno(1);
		subdto.setSid("1");
		subdto.setSacctprop(MsgConstant.ACCT_PROP_ZERO);// �˻�����
		subdto.setIvousrlno(Long.valueOf(dto.getSdealno()));// ƾ֤��ˮ��
		subdto.setSvoucherno(dto.getSvoucherno());// �ӱ���ϸ���
		subdto.setSbdgorgcode("0000");// һ��Ԥ�㵥λ����
		subdto.setSsupdepname("0000");// һ��Ԥ�㵥λ����
		subdto.setSfuncbdgsbtcode("999999");// ֧�����ܷ����Ŀ����
		subdto.setSexpfuncname("999999");// ֧�����ܷ����Ŀ����
		subdto.setSecnomicsubjectcode("");// ���ÿ�Ŀ����
		subdto.setFamt(maindto2203.getNmoney());// ֧�����
		subdto.setSpaysummaryname(maindto2203.getSdemo());// ժҪ����
		subdto.setShold1("");// Ԥ���ֶ�1
		subdto.setShold2("");// Ԥ���ֶ�2
		subdto.setShold3("");// Ԥ���ֶ�3
		subdto.setShold4("");// Ԥ���ֶ�4
		subdto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// ����ʱ��
		subdto.setSorivouno("");// ԭ֧��ƾ֤����
		subdto.setSorivoudetailno("");// ԭ֧��ƾ֤��ϸ����
		subdto.setDorivoudate(CommonUtil
				.strToDate(maindto2203.getScommitdate()));
		subdto.setShold1(dto.getSvoucherno());
		DatabaseFacade.getODB().create(subdto);

		return null;

	}

}

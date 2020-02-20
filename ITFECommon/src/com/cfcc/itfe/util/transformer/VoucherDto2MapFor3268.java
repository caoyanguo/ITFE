package com.cfcc.itfe.util.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

public class VoucherDto2MapFor3268 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3268.class);

	@SuppressWarnings("unchecked")
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException {
		List list = findMainDto(vDto);
		if (list.size() == 0)
			return list;
		List lists = new ArrayList();
		for (TvVoucherinfoAllocateIncomeDto mainDto : (List<TvVoucherinfoAllocateIncomeDto>) list) {
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
			vouchermap.put("OriBillNo", returnValue(dto.getSoripaydealno()));// ԭ�������
			vouchermap.put("OriVouDate", returnValue(dto.getShold2()));// ԭ���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(dto.getShold2()));// ԭ֧������
			vouchermap.put("FundTypeCode", returnValue("1"));// �ʽ����ʱ���
			vouchermap.put("FundTypeName", returnValue("Ԥ������ʽ�"));// �ʽ���������
			vouchermap.put("PayTypeCode", returnValue(dto.getSvtcodedes()));// ֧����ʽ����
			vouchermap.put("PayTypeName", returnValue(dto.getStradekind()));// ֧����ʽ����
			vouchermap.put("ProCatCode", returnValue("99999"));// ��֧�������
			vouchermap.put("ProCatName", returnValue("99999"));// ��֧��������
			vouchermap.put("AgencyCode", returnValue("99999")); // Ԥ�㵥λ����
			vouchermap.put("AgencyName", returnValue("99999")); // Ԥ�㵥λ����
			vouchermap.put("PayAcctNo", returnValue(dto.getSpayacctno()));// �������˺�
			vouchermap.put("PayAcctName", returnValue(dto.getSpayacctname()));// ����������
			vouchermap.put("PayAcctBankName", returnValue(dto.getSpayacctbankname()));// ����������
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayeeacctno()));// �տ����˺�
			vouchermap.put("PayeeAcctName",returnValue(dto.getSpayeeacctname()));// �տ�������
			vouchermap.put("PayeeAcctBankName", returnValue(dto.getSpayeeacctbankname()));// �տ�������
			vouchermap.put("PayeeAcctBankNo", returnValue(dto.getSpayeeacctbankname()));// �տ������к�
			vouchermap.put("PaySummaryCode", returnValue(""));// ��;����
			vouchermap.put("PaySummaryName", returnValue(""));// ��;����
			vouchermap.put("PayAmt", "-"+ MtoCodeTrans.transformString(dto.getNmoney()));// �����˿���
			vouchermap.put("Remark", returnValue(dto.getSdemo())); // ��ע��Ϣ
			vouchermap.put("Hold1", returnValue(""));// Ԥ���ֶ�1
			vouchermap.put("Hold2", returnValue(""));// Ԥ���ֶ�2
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			Detailmap.put("Id", dto.getSdealno()); // �˿�֪ͨ����ϸ���
			Detailmap.put("VoucherBillId", dto.getSdealno()); // �˿�֪ͨ��Id
			Detailmap.put("BgtTypeCode", returnValue("99999")); // Ԥ�����ͱ���
			Detailmap.put("BgtTypeName", returnValue("99999")); // Ԥ����������
			Detailmap.put("DepProCode", returnValue("99999")); // Ԥ����Ŀ����
			Detailmap.put("DepProName", returnValue("99999")); // Ԥ����Ŀ����
			Detailmap.put("ProCatCode", returnValue("99999")); // ��֧�������
			Detailmap.put("ProCatName", returnValue("99999")); // ��֧��������
			Detailmap.put("FundTypeCode", returnValue("1"));// �ʽ����ʱ���
			Detailmap.put("FundTypeName", returnValue("Ԥ������ʽ�"));// �ʽ���������
			Detailmap.put("AgencyCode", returnValue("99999")); // Ԥ�㵥λ����
			Detailmap.put("AgencyName", returnValue("99999")); // Ԥ�㵥λ����
			Detailmap.put("PayTypeCode", returnValue(dto.getSvtcodedes()));// ֧����ʽ����
			Detailmap.put("PayTypeName", returnValue(dto.getStradekind()));// ֧����ʽ����
			Detailmap.put("PayAcctNo", returnValue(dto.getSpayacctno()));// �������˺�
			Detailmap.put("PayAcctName", returnValue(dto.getSpayacctname()));// ����������
			Detailmap.put("PayAcctBankName", returnValue(dto.getSpayacctbankname()));// ����������
			Detailmap.put("PayeeAcctNo", returnValue(dto.getSpayeeacctno()));// �տ����˺�
			Detailmap.put("PayeeAcctName",returnValue(dto.getSpayeeacctname()));// �տ�������
			Detailmap.put("PayeeAcctBankName", returnValue(dto.getSpayeeacctbankname()));// �տ�������
			Detailmap.put("PayeeAcctBankNo", returnValue(dto.getSpayeeacctbankname()));// �տ������к�
			Detailmap.put("ProjectCategoryCode", returnValue("99999")); // ר����Ŀ�������
			Detailmap.put("ProjectCategoryName", returnValue("99999")); // ר����Ŀ��������
			Detailmap.put("ExpFuncCode", returnValue("99999")); // ֧�����ܷ����Ŀ����
			Detailmap.put("ExpFuncName", returnValue("99999")); // ֧�����ܷ����Ŀ����
			Detailmap.put("ExpEcoCode", returnValue("99999")); // ֧�����÷����Ŀ����
			Detailmap.put("ExpEcoName", returnValue("99999")); // ֧�����÷����Ŀ����
			Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(dto.getNmoney())); // �˿���
			Detailmap.put("Hold1", returnValue("")); // Ԥ���ֶ�1
			Detailmap.put("Hold2", returnValue("")); // Ԥ���ֶ�2
			Detailmap.put("Hold3", returnValue(""));// Ԥ���ֶ�3
			Detailmap.put("Hold4", returnValue(""));// Ԥ���ֶ�4
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
			vouchermap.put("AgentBusinessNo", returnValue(dto.getSxagentbusinessno()));// ԭ���н�����ˮ��
			vouchermap.put("OriBillNo", returnValue(dto.getStaxticketno()));// ԭ�������
			vouchermap.put("OriVouDate", returnValue(dto.getSgenticketdate()));// ԭ���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(dto.getSxpaydate()));// ԭ֧������
			vouchermap.put("FundTypeCode", dto.getSfundtypecode());// �ʽ����ʱ���
			vouchermap.put("FundTypeName", dto.getSfundtypename());// �ʽ���������
			vouchermap.put("PayTypeCode", dto.getSpaytypecode());// ֧����ʽ����
			vouchermap.put("PayTypeName", dto.getSpaytypename());// ֧����ʽ����
			if(vouchermap.get("FundTypeCode")==null||StringUtils.isBlank(String.valueOf(vouchermap.get("FundTypeCode")))||StringUtils.isEmpty(String.valueOf(vouchermap.get("FundTypeCode"))))
				vouchermap.put("FundTypeCode", "1");// �ʽ����ʱ���
			if(vouchermap.get("FundTypeName")==null||StringUtils.isNotBlank(String.valueOf(vouchermap.get("FundTypeName")))||StringUtils.isNotEmpty(String.valueOf(vouchermap.get("FundTypeName"))))
				vouchermap.put("FundTypeName", "Ԥ������ʽ�");// �ʽ���������
			if(vouchermap.get("PayTypeCode")==null||StringUtils.isNotBlank(String.valueOf(vouchermap.get("PayTypeCode")))||StringUtils.isNotEmpty(String.valueOf(vouchermap.get("PayTypeCode"))))
				vouchermap.put("PayTypeCode", "91");// ֧����ʽ����
			if(vouchermap.get("PayTypeName")==null||StringUtils.isNotBlank(String.valueOf(vouchermap.get("PayTypeName")))||StringUtils.isNotEmpty(String.valueOf(vouchermap.get("PayTypeName"))))
				vouchermap.put("PayTypeName", "ר��ʵ���˻�");// ֧����ʽ����
			vouchermap.put("ProCatCode", returnValue("99999"));// ��֧�������
			vouchermap.put("ProCatName", returnValue("99999"));// ��֧��������
			vouchermap.put("AgencyCode", returnValue(dto.getSbudgetunitcode())); // Ԥ�㵥λ����
			vouchermap.put("AgencyName", returnValue(dto.getSunitcodename())); // Ԥ�㵥λ����
			TsPaybankDto paydto = new TsPaybankDto();
			vouchermap.put("PayAcctNo", returnValue(dto.getSrecacct()));// �������˺�vouchermap.put("PayAcctNo",// dto.getSpayeracct());
			vouchermap.put("PayAcctName", returnValue(dto.getSrecname()));// ����������vouchermap.put("PayAcctName",// dto.getSpayername());
			vouchermap.put("PayAcctBankName", returnValue(dto.getSrecbankname()));// ����������vouchermap.put("PayAcctBankName",// dto.getSpayerbankname());
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayeracct()));// �տ����˺�vouchermap.put("PayeeAcctNo",// dto.getSrecacct());
			vouchermap.put("PayeeAcctName", returnValue(dto.getSpayername()));// �տ�������vouchermap.put("PayeeAcctName",// dto.getSrecname());
			vouchermap.put("PayeeAcctBankName", returnValue(dto.getSpayerbankname()));// �տ�������vouchermap.put("PayeeAcctBankName",// dto.getSrecbankname());
			List<TsPaybankDto> payList = null;
			if(dto.getSpayerbankname()!=null&&!"".equals(dto.getSpayerbankname()))
			{
				paydto.setSpaybankname(dto.getSpayerbankname());
				payList = CommonFacade.getODB().findRsByDto(paydto);
			}
			vouchermap.put("PayeeAcctBankNo", payList != null&& payList.size() > 0 ? payList.get(0).getSpaybankno():returnValue(dto.getSrecbankno()));// �տ������к�vouchermap.put("PayeeAcctBankNo",// returnValue(dto.getSrecbankno()));
			vouchermap.put("PaySummaryCode", returnValue(dto.getSpaysummarycode()));// ��;����
			vouchermap.put("PaySummaryName", returnValue(dto.getSpaysummaryname()));// ��;����
			vouchermap.put("PayAmt", "-"+ MtoCodeTrans.transformString(dto.getShold2()));// �����˿���
			vouchermap.put("Remark", returnValue(payoutBackReason)); // ��ע��Ϣ
			vouchermap.put("Hold1", returnValue(dto.getShold1()));// Ԥ���ֶ�1
			vouchermap.put("Hold2", returnValue(""));// Ԥ���ֶ�2
			for (TvPayoutmsgsubDto subdto : sublist) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", dto.getSdealno() + subdto.getSbizno()); // �˿�֪ͨ����ϸ���
				Detailmap.put("VoucherBillId", vDto.getSdealno()); // �˿�֪ͨ��Id
				Detailmap.put("BgtTypeCode", returnValue(subdto.getSbgttypecode())); // Ԥ�����ͱ���
				Detailmap.put("BgtTypeName", returnValue(subdto.getSbgttypename())); // Ԥ����������
				Detailmap.put("DepProCode", returnValue(subdto.getSbudgetprjcode())); // Ԥ����Ŀ����
				Detailmap.put("DepProName", returnValue(subdto.getShold4())); // Ԥ����Ŀ����
				Detailmap.put("ProCatCode",returnValue(subdto.getSprocatcode())); // ��֧�������
				Detailmap.put("ProCatName",returnValue(subdto.getSprocatname())); // ��֧��������
				Detailmap.put("AgencyCode", subdto.getSagencycode()); // Ԥ�㵥λ����
				Detailmap.put("AgencyName", subdto.getSagencyname()); // Ԥ�㵥λ����
				Detailmap.put("FundTypeCode", dto.getSfundtypecode());// �ʽ����ʱ���
				Detailmap.put("FundTypeName", dto.getSfundtypename());// �ʽ���������
				Detailmap.put("PayTypeCode", dto.getSpaytypecode());// ֧����ʽ����
				Detailmap.put("PayTypeName", dto.getSpaytypename());// ֧����ʽ����
				Detailmap.put("PayAcctNo", returnValue(dto.getSrecacct()));// �������˺�
				Detailmap.put("PayAcctName", returnValue(dto.getSrecname()));// ����������
				Detailmap.put("PayAcctBankName", returnValue(dto.getSrecbankname()));// ����������
				Detailmap.put("PayeeAcctBankName", returnValue(dto.getSpayerbankname()));// �տ�������
				Detailmap.put("PayeeAcctBankNo", vouchermap.get("PayeeAcctBankNo"));// �տ������к�
				Detailmap.put("PayeeAcctNo", returnValue(dto.getSpayeracct()));// �տ����˺�
				Detailmap.put("PayeeAcctName", returnValue(dto.getSpayername()));// �տ�������
				Detailmap.put("ProjectCategoryCode", returnValue(subdto.getShold2())); // ר����Ŀ�������
				Detailmap.put("ProjectCategoryName", returnValue(subdto.getShold3())); // ר����Ŀ��������
				Detailmap.put("ExpFuncCode", returnValue(subdto.getSfunsubjectcode())); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName", returnValue(subdto.getSexpfuncname())); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpEcoCode",returnValue(subdto.getSexpecocode())); // ֧�����÷����Ŀ����
				Detailmap.put("ExpEcoName",returnValue(subdto.getSexpeconame())); // ֧�����÷����Ŀ����
				Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(subdto.getNmoney())); // ȫ���˿���
				Detailmap.put("Hold1", returnValue(subdto.getShold1())); // Ԥ���ֶ�1
				Detailmap.put("Hold2", returnValue("")); // Ԥ���ֶ�2
				Detailmap.put("Hold3", returnValue(""));// Ԥ���ֶ�3
				Detailmap.put("Hold4", returnValue(""));// Ԥ���ֶ�4
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
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			vouchermap.put("Id", voucherdto.getSdealno());// ʵ���˿�֪ͨ��Id
			vouchermap.put("AdmDivCode", voucherdto.getSadmdivcode());// ������������
			vouchermap.put("StYear", voucherdto.getSstyear());// ҵ�����
			vouchermap.put("VtCode", voucherdto.getSvtcode());// ƾ֤���ͱ��
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, TsPaybankDto>  bankmap = null;
			try {
				bankmap = SrvCacheFacade.cachePayBankInfo();
			} catch (JAFDatabaseException e1) {
				logger.error("��ȡ����������Ϣ����"+e1);
			}
			vouchermap.put("VouDate", maindto.getSvoudate());// ƾ֤����
			vouchermap.put("VoucherNo", maindto.getSvouno());// ƾ֤��
			vouchermap.put("AgentBusinessNo", returnValue(maindto.getSoritrano()));// ԭ���н�����ˮ��
			vouchermap.put("OriBillNo", returnValue(maindto.getSorivouno()));// ԭ�������
			vouchermap.put("OriVouDate", returnValue(maindto.getSorivoudate()));// ԭ���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(maindto.getSorivoudate()));// ԭ֧������
			vouchermap.put("FundTypeCode", "1");// �ʽ����ʱ���
			vouchermap.put("FundTypeName", "Ԥ������ʽ�");// �ʽ���������
			vouchermap.put("PayTypeCode", "91");// ֧����ʽ����
			vouchermap.put("PayTypeName", "ר��ʵ���˻�");// ֧����ʽ����
			vouchermap.put("ProCatCode", returnValue("-"));// ��֧�������
			vouchermap.put("ProCatName", returnValue("-"));// ��֧��������
			vouchermap.put("AgencyCode", maindto.getSbudgetunitcode()); // Ԥ�㵥λ����
			vouchermap.put("AgencyName", returnValue(maindto.getSunitcodename())); // Ԥ�㵥λ����
			vouchermap.put("PayAcctNo", maindto.getSpayeracct());// �������˺�
			vouchermap.put("PayAcctName", maindto.getSpayername());// ����������
			TvPayoutmsgmainDto payoutdto = new TvPayoutmsgmainDto();
			payoutdto.setStaxticketno(maindto.getSorivouno());
			List payoutlist = CommonFacade.getODB().findRsByDto(payoutdto);
			if(payoutlist!=null&&payoutlist.size()==1)
			{
				payoutdto = (TvPayoutmsgmainDto)payoutlist.get(0);
				vouchermap.put("PayAcctBankName", payoutdto.getSrecbankname());// ����������
			}else
			{
				HtvPayoutmsgmainDto hpayoutdto = new HtvPayoutmsgmainDto();
				hpayoutdto.setStaxticketno(maindto.getSorivouno());
				List hpayoutlist = CommonFacade.getODB().findRsByDto(hpayoutdto);
				if(hpayoutlist!=null&&hpayoutlist.size()==1)
				{
					hpayoutdto = (HtvPayoutmsgmainDto)hpayoutlist.get(0);
					vouchermap.put("PayAcctBankName", hpayoutdto.getSrecbankname());// ����������
				}
			}
			if(vouchermap.get("PayAcctBankName")==null)
				vouchermap.put("PayAcctBankName", returnValue("--"));// ����������
			vouchermap.put("PayeeAcctNo", maindto.getSpayeeacct());// �տ����˺�
			vouchermap.put("PayeeAcctName", maindto.getSpayeename());// �տ�������
			vouchermap.put("PayeeAcctBankName", returnValue(bankmap==null?maindto.getSpayeename():bankmap.get(maindto.getSpayeebankno())==null?maindto.getSpayeename():bankmap.get(maindto.getSpayeebankno()).getSbankname()));// �տ�������
			vouchermap.put("PayeeAcctBankNo", maindto.getSpayeebankno());// �տ������к�
			vouchermap.put("PaySummaryCode", "");// ��;����
			vouchermap.put("PaySummaryName", maindto.getSaddword());// ��;����
			vouchermap.put("PayAmt", "-"+ MtoCodeTrans.transformString(maindto.getNmoney()));// �����˿���
			vouchermap.put("Remark", returnValue(maindto.getSdemo())); // ��ע��Ϣ
			vouchermap.put("Hold1", maindto.getShold1());// Ԥ���ֶ�1
			vouchermap.put("Hold2", maindto.getShold2());// Ԥ���ֶ�2
			TsPaybankDto paydto = bankmap.get(maindto.getSpayername());
			for (int j = 0; j < sublist.size(); j++) {
				TvPayoutbackmsgSubDto subdto = (TvPayoutbackmsgSubDto) sublist
						.get(j);
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id", maindto.getSvouno() + subdto.getSseqno()); // �˿�֪ͨ����ϸ���
				Detailmap.put("VoucherBillId", voucherdto.getSdealno()); // �˿�֪ͨ��Id
				Detailmap.put("DepProCode", returnValue(subdto.getSbudgetprjcode())); // Ԥ����Ŀ����
				Detailmap.put("DepProName", returnValue("")); // Ԥ����Ŀ����
				Detailmap.put("BgtTypeCode", ""); // Ԥ�����ͱ���
				Detailmap.put("BgtTypeName", ""); // Ԥ����������
				Detailmap.put("ProCatCode", ""); // ��֧�������
				Detailmap.put("ProCatName", ""); // ��֧��������
				Detailmap.put("AgencyCode", maindto.getSbudgetunitcode()); // Ԥ�㵥λ����
				Detailmap.put("AgencyName", returnValue(maindto.getSunitcodename())); // Ԥ�㵥λ����
				Detailmap.put("PayAcctNo", maindto.getSpayeracct());// �������˺�
				Detailmap.put("PayAcctName", maindto.getSpayername());// ����������
				Detailmap.put("PayAcctBankName", maindto.getSpayername());// ����������
				Detailmap.put("PayeeAcctNo", returnValue(maindto.getSpayeracct()));// ԭ�տ����˺�
				Detailmap.put("PayeeAcctName", returnValue(maindto.getSpayername()));// ԭ�տ�������
				Detailmap.put("PayeeAcctBankName", paydto!=null? paydto.getSbankname() : returnValue(maindto.getSpayername()));// ԭ�տ���������
				Detailmap.put("PayeeAcctBankNo",paydto!=null ? paydto.getSbankno(): returnValue(""));// ԭ�տ������к�
				Detailmap.put("ProjectCategoryCode", returnValue("")); // ר����Ŀ�������
				Detailmap.put("ProjectCategoryName", returnValue("")); // ר����Ŀ��������
				Detailmap.put("ExpFuncCode", subdto.getSfunsubjectcode()); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpFuncName", returnValue(subdto.getSfunsubjectname())); // ֧�����ܷ����Ŀ����
				Detailmap.put("ExpEcoCode", ""); // ֧�����÷����Ŀ����
				Detailmap.put("ExpEcoName", ""); // ֧�����÷����Ŀ����
				Detailmap.put("Amt", "-"+ MtoCodeTrans.transformString(subdto.getNmoney())); // �˿���
				Detailmap.put("Hold1", returnValue(subdto.getShold1())); // Ԥ���ֶ�1
				Detailmap.put("Hold2", returnValue(subdto.getShold2())); // Ԥ���ֶ�2
				Detailmap.put("Hold3", returnValue(""));// Ԥ���ֶ�3
				Detailmap.put("Hold4", returnValue(""));// Ԥ���ֶ�4
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

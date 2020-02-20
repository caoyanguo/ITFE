package com.cfcc.itfe.util.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

public class VoucherDto2MapFor3251 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3251.class);	

	@SuppressWarnings("unchecked")
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException {		
		List list=findMainDto(vDto);
		if(list.size()==0)
			return list;
		List lists=new ArrayList();
		for(TvVoucherinfoAllocateIncomeDto mainDto:(List<TvVoucherinfoAllocateIncomeDto>)list){
//			List<TvVoucherinfoDto> voutherList=voucherIsRepeat(vDto, mainDto);
//			if(voutherList!=null&&voutherList.size()>0){
//				continue;
//			}
			lists.add(voucherTranfor(vDto, mainDto));
		}
		return lists;
	}
	/**
	 * ����ƾ֤
	 * ����ƾ֤����
	 * @param vDto
	 * @param mainDto
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	private List voucherTranfor(TvVoucherinfoDto vDto,TvVoucherinfoAllocateIncomeDto mainDto) throws ITFEBizException {		
		TvVoucherinfoDto dto=(TvVoucherinfoDto) vDto.clone();
		dto.setSdealno(VoucherUtil.getGrantSequence());
		dto.setSvoucherno(dto.getSdealno());
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));			
		dto.setSvoucherflag("1");
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));		
		dto.setSadmdivcode(mainDto.getSadmdivcode());
		dto.setSvoucherno(mainDto.getSdealno());
		dto.setNmoney(mainDto.getNmoney());
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("����ɹ�");
		dto.setShold4("1");	//��ʶ�ñ�ΪTC�ʽ���
		List lists=new ArrayList();
		lists.add(dto);
		lists.add(mainDto);
		Map map=tranforDwbkBack(lists);
		lists.clear();		
		lists.add(map);
		lists.add(dto);
		return lists;
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
	public Map tranforDwbkBack(List lists) throws ITFEBizException {
		try {
			//�����������½ӿ�20141015_V2.0�汾
			if(ITFECommonConstant.SRC_NODE.equals("000057400006")){
				return tranforDwbkBackForNB(lists);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) lists.get(1);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", vDto.getSdealno());// ʵ���˿�֪ͨ��Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// ������������
			vouchermap.put("StYear", vDto.getSstyear());// ҵ�����
			vouchermap.put("VtCode", vDto.getSvtcode());// ƾ֤���ͱ��
			vouchermap.put("VouDate", vDto.getScreatdate());// ƾ֤����
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// ƾ֤��
			vouchermap.put("AgentBusinessNo", returnValue(dto.getSsecretsign()));//ԭ���н�����ˮ��
			vouchermap.put("OriBillNo", returnValue(""));// ԭ�˸�������
			vouchermap.put("OriVouDate", returnValue(""));// ԭ�˸���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(""));// ԭ�˸�����
			vouchermap.put("TreCode", returnValue(dto.getStrecode()));//�������
			vouchermap.put("FinOrgCode", returnValue(""));// �������ش���
			vouchermap.put("FundTypeCode", returnValue(dto.getSvtcodedes()));// �ʽ����ʱ���
			vouchermap.put("FundTypeName", returnValue(dto.getStradekind()));// �ʽ���������
			vouchermap.put("ClearBankCode", returnValue(""));// �������б���
			vouchermap.put("ClearBankName", returnValue(""));// ������������
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayeeacctno()));// �տ����˺�
			vouchermap.put("PayeeAcctName", returnValue(dto.getSpayeeacctname()));// �տ�������
			vouchermap.put("PayeeAcctBankName", returnValue(dto.getShold1()));// �տ�������
			vouchermap.put("PayeeAcctBankNo", returnValue(dto.getSpayeeacctbankname()));// �տ������к�
			vouchermap.put("PayAcctNo", returnValue(dto.getSpayacctno()));// �������˺�
			vouchermap.put("PayAcctName", returnValue(dto.getSpayacctname()));// ����������
			vouchermap.put("PayAcctBankName", returnValue(dto.getSpayacctbankname()));// ����������
			vouchermap.put("ReturnReasonName", returnValue(""));// �˿�ԭ��
			vouchermap.put("PayAmt", String.valueOf(dto.getNmoney()));// �˿���
			vouchermap.put("XpayAmt", String.valueOf(dto.getNmoney()));//���
			vouchermap.put("XPayDate", returnValue(dto.getScommitdate()));// �˿�����
			vouchermap.put("XAgentBusinessNo", returnValue(dto.getSpaydealno())); //���н�����ˮ��
			vouchermap.put("Hold1", returnValue("")); // Ԥ���ֶ�1
			vouchermap.put("Hold2", returnValue("")); // Ԥ���ֶ�2
			
			
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			
			String mainvou = VoucherUtil.getGrantSequence();
			Detailmap.put("Id", returnValue(mainvou)); // �˸���ϸ���
			Detailmap.put("VoucherBillId", vDto.getSdealno()); // �˿�֪ͨ��Id		
			Detailmap.put("BgtTypeCode", returnValue("")); // Ԥ�����ͱ���
			Detailmap.put("BgtTypeName", returnValue("")); // Ԥ����������
			Detailmap.put("ProCatCode", returnValue("")); // ��֧�������
			Detailmap.put("ProCatName", returnValue("")); // ��֧��������
			Detailmap.put("AgencyCode", returnValue("")); // Ԥ�㵥λ����
			Detailmap.put("AgencyName", returnValue("")); // Ԥ�㵥λ����
			Detailmap.put("IncomeSortCode",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortName",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortCode1",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortName1",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortCode2",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortName2",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortCode3",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortName3",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortCode4",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortName4",returnValue("")); // ��������Ŀ����
			Detailmap.put("PayAmt", String.valueOf(dto.getNmoney())); // �˿���
			Detailmap.put("Hold1", returnValue("")); // Ԥ���ֶ�1
			Detailmap.put("Hold2", returnValue("")); // Ԥ���ֶ�2
			Detailmap.put("Hold3", returnValue("")); // Ԥ���ֶ�3				
			Detailmap.put("Hold4", returnValue("")); // Ԥ���ֶ�3				
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
	 *  ��װ���������˸��˿��_TCBS�ʽ��ļ�����(���½ӿ�20141015_V2.0�汾)
	 * @param lists
	 * @return Map
	 * @throws ITFEBizException
	 */
	public Map tranforDwbkBackForNB(List lists) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) lists.get(1);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", vDto.getSdealno());// ʵ���˿�֪ͨ��Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// ������������
			vouchermap.put("StYear", vDto.getSstyear());// ҵ�����
			vouchermap.put("VtCode", vDto.getSvtcode());// ƾ֤���ͱ��
			vouchermap.put("VouDate", vDto.getScreatdate());// ƾ֤����
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// ƾ֤��
			vouchermap.put("AgentBusinessNo", returnValue(dto.getSsecretsign()));//ԭ���н�����ˮ��
			vouchermap.put("OriBillNo", returnValue(""));// ԭ�˸�������
			vouchermap.put("OriVouDate", returnValue(""));// ԭ�˸���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(""));// ԭ�˸�����
			vouchermap.put("TreCode", returnValue(dto.getStrecode()));//�������
			vouchermap.put("FinOrgCode", returnValue(""));// �������ش���
			vouchermap.put("FundTypeCode", returnValue(dto.getSvtcodedes()));// �ʽ����ʱ���
			vouchermap.put("FundTypeName", returnValue(dto.getStradekind()));// �ʽ���������
			vouchermap.put("ClearBankCode", returnValue(""));// �������б���
			vouchermap.put("ClearBankName", returnValue(""));// ������������
			
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayacctno()));// ԭ�տ����˺�
			vouchermap.put("PayeeAcctName", returnValue(dto.getSpayacctname()));// ԭ�տ�������
			vouchermap.put("PayeeAcctBankName", returnValue(dto.getSforwardbankname()));// ԭ�տ�������
			vouchermap.put("PayeeAcctBankNo", returnValue(dto.getSpayacctbankname()));// ԭ�տ������к�
			vouchermap.put("PayAcctNo", returnValue(dto.getSpayeeacctno()));// ԭ�������˺�
			vouchermap.put("PayAcctName", returnValue(dto.getSpayeeacctname()));// ԭ����������
			vouchermap.put("PayAcctBankName", returnValue(dto.getShold1()));// ԭ����������
			
			vouchermap.put("AgencyCode", returnValue("")); // Ԥ�㵥λ����
			vouchermap.put("AgencyName", returnValue("")); // Ԥ�㵥λ����
			vouchermap.put("Remark", returnValue(""));// �˿�ԭ��
			vouchermap.put("PayAmt", "-"+MtoCodeTrans.transformString(dto.getNmoney()));// �˿���
			vouchermap.put("Hold1", returnValue("")); // Ԥ���ֶ�1
			vouchermap.put("Hold2", returnValue("")); // Ԥ���ֶ�2
			
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			
			String mainvou = VoucherUtil.getGrantSequence();
			Detailmap.put("Id", returnValue(mainvou)); // �˸���ϸ���
			Detailmap.put("VoucherBillId", vDto.getSdealno()); // �˿�֪ͨ��Id		
			Detailmap.put("BgtTypeCode", returnValue("")); // Ԥ�����ͱ���
			Detailmap.put("BgtTypeName", returnValue("")); // Ԥ����������
			Detailmap.put("ProCatCode", returnValue("")); // ��֧�������
			Detailmap.put("ProCatName", returnValue("")); // ��֧��������
			Detailmap.put("PayeeAcctNo", returnValue(dto.getSpayacctno()));// ԭ�տ����˺�
			Detailmap.put("PayeeAcctName", returnValue(dto.getSpayacctname()));// ԭ�տ�������
			Detailmap.put("PayeeAcctBankName", returnValue(dto.getSforwardbankname()));// ԭ�տ�������
			Detailmap.put("PayeeAcctBankNo", returnValue(dto.getSpayacctbankname()));// ԭ�տ������к�
			Detailmap.put("AgencyCode", returnValue("")); // Ԥ�㵥λ����
			Detailmap.put("AgencyName", returnValue("")); // Ԥ�㵥λ����
			Detailmap.put("IncomeSortCode",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortName",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortCode1",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortName1",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortCode2",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortName2",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortCode3",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortName3",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortCode4",returnValue("")); // ��������Ŀ����
			Detailmap.put("IncomeSortName4",returnValue("")); // ��������Ŀ����
			Detailmap.put("PayAmt", "-"+MtoCodeTrans.transformString(dto.getNmoney())); // �˿���
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
	public Map tranfor(List lists) throws ITFEBizException {
		try {
			//�����������½ӿ�20141015_V2.0�汾
			if(ITFECommonConstant.SRC_NODE.equals("000057400006")){
				return tranforForNB(lists);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvDwbkDto dto = (TvDwbkDto) lists.get(1);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(2);
			//�˿��˻�ԭ��
			String dwbkBackReason = (String) lists.get(3);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", returnValue(vDto.getSdealno()));// �����˸�ƾ֤Id
			vouchermap.put("AdmDivCode", returnValue(vDto.getSadmdivcode()));// ������������
			vouchermap.put("StYear", returnValue(vDto.getSstyear()));// ҵ�����
			vouchermap.put("VtCode", returnValue(vDto.getSvtcode()));// ƾ֤���ͱ��
			vouchermap.put("VouDate", returnValue(vDto.getScreatdate()));// ƾ֤����
			vouchermap.put("VoucherNo", returnValue(vDto.getSvoucherno()));// ƾ֤��
			vouchermap.put("AgentBusinessNo", returnValue(String.valueOf(dto.getIvousrlno())));// ԭ���н�����ˮ��
			vouchermap.put("OriBillNo", returnValue(dto.getSdwbkvoucode()));// ԭ�������
			vouchermap.put("OriVouDate", returnValue(String.valueOf(dto.getDaccept())));// ԭ���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(String.valueOf(dto.getDacct())));// ԭ֧������
			vouchermap.put("TreCode", returnValue(dto.getSpayertrecode()));// �����������
			vouchermap.put("FinOrgCode", returnValue(dto.getStaxorgcode()));// �������ش���
			vouchermap.put("FundTypeCode", returnValue(dto.getSfundtypecode()));// �ʽ����ʱ���
			vouchermap.put("FundTypeName", returnValue(dto.getSfundtypename()));// �ʽ���������
			vouchermap.put("ClearBankCode", returnValue(dto.getSclearbankcode()));// �������б���
			vouchermap.put("ClearBankName", returnValue(dto.getSclearbankname()));// ������������
			TsPaybankDto paydto = new TsPaybankDto();
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayacctno()));// �տ����˺�dto.getSpayeeacct()
			vouchermap.put("PayeeAcctName", returnValue(dto.getSpayacctname()));// �տ�������dto.getSpayeename()
			vouchermap.put("PayeeAcctBankName", returnValue(dto.getSpayacctbankname()));// �տ�������dto.getSpayeecode()
			paydto.setSbankname(dto.getSpayacctbankname());
			List<TsPaybankDto> payList = CommonFacade.getODB().findRsByDto(paydto);
			vouchermap.put("PayeeAcctBankNo",payList!=null&&payList.size()>0?payList.get(0).getSbankno():returnValue(dto.getSpayeeopnbnkno()));// �տ������к�dto.getSpayeeopnbnkno()
			vouchermap.put("PayAcctNo", returnValue(dto.getSpayeeacct()));// �������˺�dto.getSpayacctno()
			vouchermap.put("PayAcctName", returnValue(dto.getSpayeename()));// ����������dto.getSpayacctname()
			paydto = new TsPaybankDto();
			paydto.setSbankno(dto.getSpayeecode());
			payList = CommonFacade.getODB().findRsByDto(paydto);
			vouchermap.put("PayAcctBankName", payList!=null&&payList.size()>0?payList.get(0).getSbankname():returnValue(dto.getSpayeename()));// ����������dto.getSpayacctbankname()
			vouchermap.put("ReturnReasonName", returnValue(dto.getSreturnreasonname()));// �˸�ԭ��
			vouchermap.put("PayAmt", dto.getShold2());// �˸����
			vouchermap.put("XpayAmt", String.valueOf(dto.getFamt()));//�˸� ���dto.getXpayamt()
			vouchermap.put("XPayDate", TimeFacade.getCurrentStringTime());// �˸�����
			vouchermap.put("XAgentBusinessNo", returnValue(dto.getXagentbusinessno()));// ���н�����ˮ��
			vouchermap.put("Remark", returnValue(dwbkBackReason));// �˿�ԭ��
			vouchermap.put("Hold1", returnValue(dto.getShold1()));// Ԥ���ֶ�1
			vouchermap.put("Hold2", returnValue(""));// Ԥ���ֶ�2	
			
			
			
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			
			Detailmap.put("Id", returnValue(dto.getSbizno())); // �˸���ϸ���
			Detailmap.put("VoucherBillId", returnValue(vDto.getSdealno())); // �˸�ƾ֤Id		
			Detailmap.put("BgtTypeCode", returnValue(dto.getSbgttypecode())); // Ԥ�����ͱ���
			Detailmap.put("BgtTypeName", returnValue(dto.getSbgttypename())); // Ԥ����������
			Detailmap.put("ProCatCode", returnValue(dto.getSprocatcode())); // ��֧�������
			Detailmap.put("ProCatName", returnValue(dto.getSprocatname())); // ��֧��������
			Detailmap.put("AgencyCode", returnValue(dto.getSpayeecode())); // Ԥ�㵥λ����
			Detailmap.put("AgencyName", returnValue(dto.getSagencyname())); // Ԥ�㵥λ����
			Detailmap.put("IncomeSortCode",returnValue(dto.getSbdgsbtcode())); // ��������Ŀ����
			Detailmap.put("IncomeSortName",returnValue(dto.getSincomesortname())); // ��������Ŀ����
			Detailmap.put("IncomeSortCode1", returnValue(dto.getSincomesortcode1())); // ��������Ŀ�����
			Detailmap.put("IncomeSortName1", returnValue(dto.getSincomesortname1())); // ��������Ŀ������
			Detailmap.put("IncomeSortCode2", returnValue(dto.getSincomesortcode2())); // ��������Ŀ�����
			Detailmap.put("IncomeSortName2", returnValue(dto.getSincomesortname2())); // ��������Ŀ������
			Detailmap.put("IncomeSortCode3", returnValue(dto.getSincomesortcode3())); // ��������Ŀ�����
			Detailmap.put("IncomeSortName3", returnValue(dto.getSincomesortname3())); // ��������Ŀ������
			Detailmap.put("IncomeSortCode4", returnValue(dto.getSincomesortcode4())); // ��������ĿĿ����
			Detailmap.put("IncomeSortName4", returnValue(dto.getSincomesortname4())); // ��������ĿĿ����
			Detailmap.put("PayAmt", dto.getShold2()); // ���
			vouchermap.put("Hold1", returnValue(dto.getSdetailhold1())); // Ԥ���ֶ�1
			vouchermap.put("Hold2", returnValue("")); // Ԥ���ֶ�2
			vouchermap.put("Hold3", returnValue(dto.getSdetailhold3())); // Ԥ���ֶ�3				
			vouchermap.put("Hold4",returnValue(dto.getSdetailhold4())); // Ԥ���ֶ�4		
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
	 * ��װ���������˸��˿��_ǰ�÷���(���½ӿ�20141015_V2.0�汾)
	 * @param lists
	 * @return Map
	 * @throws ITFEBizException
	 */
	public Map tranforForNB(List lists) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvDwbkDto dto = (TvDwbkDto) lists.get(1);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(2);	
			//�˿��˻�ԭ��
			String dwbkBackReason = (String) lists.get(3);
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", returnValue(vDto.getSdealno()));// �����˸�ƾ֤Id
			vouchermap.put("AdmDivCode", returnValue(vDto.getSadmdivcode()));// ������������
			vouchermap.put("StYear", returnValue(vDto.getSstyear()));// ҵ�����
			vouchermap.put("VtCode", returnValue(vDto.getSvtcode()));// ƾ֤���ͱ��
			vouchermap.put("VouDate", returnValue(vDto.getScreatdate()));// ƾ֤����
			vouchermap.put("VoucherNo", returnValue(vDto.getSvoucherno()));// ƾ֤��
			vouchermap.put("AgentBusinessNo", returnValue(String.valueOf(dto.getIvousrlno())));// ԭ���н�����ˮ��
			vouchermap.put("OriBillNo", returnValue(dto.getSdwbkvoucode()));// ԭ�������
			vouchermap.put("OriVouDate", returnValue(String.valueOf(dto.getDaccept())));// ԭ���ƾ֤����
			vouchermap.put("OriPayDate", returnValue(String.valueOf(dto.getDacct())));// ԭ֧������
			vouchermap.put("TreCode", returnValue(dto.getSpayertrecode()));// �����������
			vouchermap.put("FinOrgCode", returnValue(dto.getStaxorgcode()));// �������ش���
			vouchermap.put("FundTypeCode", returnValue(dto.getSfundtypecode()));// �ʽ����ʱ���
			vouchermap.put("FundTypeName", returnValue(dto.getSfundtypename()));// �ʽ���������
			vouchermap.put("ClearBankCode", returnValue(dto.getSclearbankcode()));// �������б���
			vouchermap.put("ClearBankName", returnValue(dto.getSclearbankname()));// ������������

			//ԭ�ո�������Ϣ
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayeeacct()));// ԭ�տ����˺�
			vouchermap.put("PayeeAcctName", returnValue(dto.getSpayeename()));// ԭ�տ�������
			vouchermap.put("PayeeAcctBankName", returnValue(dto.getSinputrecbankname()));// ԭ�տ�������
			vouchermap.put("PayeeAcctBankNo",returnValue(dto.getSpayeeopnbnkno()));// ԭ�տ������к�
			vouchermap.put("PayAcctNo", returnValue(dto.getSpayacctno()));// ԭ�������˺�
			vouchermap.put("PayAcctName", returnValue(dto.getSpayacctname()));// ԭ����������
			vouchermap.put("PayAcctBankName", returnValue(dto.getSpayacctbankname()));// ԭ����������
			
			vouchermap.put("AgencyCode", returnValue(dto.getSpayeecode())); // Ԥ�㵥λ����
			vouchermap.put("AgencyName", returnValue(dto.getSagencyname())); // Ԥ�㵥λ����
			vouchermap.put("Remark", returnValue(dwbkBackReason));// �˿�ԭ��
			vouchermap.put("PayAmt", "-"+MtoCodeTrans.transformString(dto.getSdetailhold1().trim()));// �˸����
			vouchermap.put("Hold1", returnValue(dto.getShold1()));// Ԥ���ֶ�1(Ԥ�㼶�δ��룬������)
			vouchermap.put("Hold2", returnValue(dto.getShold2()));// Ԥ���ֶ�2(���ջ��ش��룬������)
			
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			
			Detailmap.put("Id", returnValue(dto.getSbizno())); // �˸���ϸ���
			Detailmap.put("VoucherBillId", returnValue(vDto.getSdealno())); // �˸�ƾ֤Id		
			Detailmap.put("BgtTypeCode", returnValue(dto.getSbgttypecode())); // Ԥ�����ͱ���
			Detailmap.put("BgtTypeName", returnValue(dto.getSbgttypename())); // Ԥ����������
			Detailmap.put("ProCatCode", returnValue(dto.getSprocatcode())); // ��֧�������
			Detailmap.put("ProCatName", returnValue(dto.getSprocatname())); // ��֧��������
			Detailmap.put("PayeeAcctNo", returnValue(dto.getSpayeeacct()));// ԭ�տ����˺�
			Detailmap.put("PayeeAcctName", returnValue(dto.getSpayeename()));// ԭ�տ�������
			Detailmap.put("PayeeAcctBankName", returnValue(dto.getSinputrecbankname()));// ԭ�տ�������
			Detailmap.put("PayeeAcctBankNo",returnValue(dto.getSpayeeopnbnkno()));// ԭ�տ������к�
			Detailmap.put("AgencyCode", returnValue(dto.getSpayeecode())); // Ԥ�㵥λ����
			Detailmap.put("AgencyName", returnValue(dto.getSagencyname())); // Ԥ�㵥λ����
			Detailmap.put("IncomeSortCode",returnValue(dto.getSbdgsbtcode())); // ��������Ŀ����
			Detailmap.put("IncomeSortName",returnValue(dto.getSincomesortname())); // ��������Ŀ����
			Detailmap.put("IncomeSortCode1", returnValue(dto.getSincomesortcode1())); // ��������Ŀ�����
			Detailmap.put("IncomeSortName1", returnValue(dto.getSincomesortname1())); // ��������Ŀ������
			Detailmap.put("IncomeSortCode2", returnValue(dto.getSincomesortcode2())); // ��������Ŀ�����
			Detailmap.put("IncomeSortName2", returnValue(dto.getSincomesortname2())); // ��������Ŀ������
			Detailmap.put("IncomeSortCode3", returnValue(dto.getSincomesortcode3())); // ��������Ŀ�����
			Detailmap.put("IncomeSortName3", returnValue(dto.getSincomesortname3())); // ��������Ŀ������
			Detailmap.put("IncomeSortCode4", returnValue(dto.getSincomesortcode4())); // ��������ĿĿ����
			Detailmap.put("IncomeSortName4", returnValue(dto.getSincomesortname4())); // ��������ĿĿ����
			Detailmap.put("PayAmt", "-"+MtoCodeTrans.transformString(dto.getSdetailhold1().trim())); // Ԥ���ֶ�1
			Detailmap.put("Hold1", returnValue(dto.getSdetailhold3())); // Ԥ���ֶ�1(Ԥ������,������)
			Detailmap.put("Hold2", returnValue(dto.getSdetailhold2())); // Ԥ���ֶ�2(�˿�ԭ�����,������)
			Detailmap.put("Hold3", returnValue("")); // Ԥ���ֶ�3(�˿����ݣ�������)			
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
	
	

	private String returnValue(String value)
	{
		if(value == null || "".equals(value))
		{
			return "";
		}else 
		{
			return value;
		}
	}
	/**	
	 * ��ѯʵ���ʽ��˿�ҵ�����Ϣ
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List findMainDto(TvVoucherinfoDto dto) throws ITFEBizException {		
		TvVoucherinfoAllocateIncomeDto mainDto=new TvVoucherinfoAllocateIncomeDto();
		mainDto.setSorgcode(dto.getSorgcode());
		mainDto.setStrecode(dto.getStrecode());
		mainDto.setScommitdate(dto.getScreatdate());
		mainDto.setSvtcode(dto.getSvtcode());
		try {
			return CommonFacade.getODB().findRsByDto(mainDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ʊ����Ϣ����",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ʊ����Ϣ����",e);
		}
	}
	
	
	
	/**
	 * ƾ֤����
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List voucherIsRepeat(TvVoucherinfoDto dto,TvVoucherinfoAllocateIncomeDto mainDto) throws ITFEBizException{
		TvVoucherinfoDto vDto=(TvVoucherinfoDto) dto.clone();
		vDto.setSadmdivcode(mainDto.getSadmdivcode());
		vDto.setSvoucherno(mainDto.getSdealno());
		try {
			return CommonFacade.getODB().findRsByDto(vDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ʊ����Ϣ����",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("��ѯ��������Ʊ����Ϣ����",e);
		}		
	}
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}

}

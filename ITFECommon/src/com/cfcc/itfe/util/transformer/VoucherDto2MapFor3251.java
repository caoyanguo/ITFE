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
	 * 生成凭证
	 * 生成凭证报文
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
		dto.setSdemo("处理成功");
		dto.setShold4("1");	//标识该笔为TC资金导入
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
	 * DTO转化XML报文
	 * 
	 * @param List
	 *            生成报文要素集合
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map tranforDwbkBack(List lists) throws ITFEBizException {
		try {
			//宁波采用最新接口20141015_V2.0版本
			if(ITFECommonConstant.SRC_NODE.equals("000057400006")){
				return tranforDwbkBackForNB(lists);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) lists.get(1);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", vDto.getSdealno());// 实拨退款通知书Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());// 业务年度
			vouchermap.put("VtCode", vDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());// 凭证日期
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// 凭证号
			vouchermap.put("AgentBusinessNo", returnValue(dto.getSsecretsign()));//原银行交易流水号
			vouchermap.put("OriBillNo", returnValue(""));// 原退付单单号
			vouchermap.put("OriVouDate", returnValue(""));// 原退付单凭证日期
			vouchermap.put("OriPayDate", returnValue(""));// 原退付日期
			vouchermap.put("TreCode", returnValue(dto.getStrecode()));//国库代码
			vouchermap.put("FinOrgCode", returnValue(""));// 财政机关代码
			vouchermap.put("FundTypeCode", returnValue(dto.getSvtcodedes()));// 资金性质编码
			vouchermap.put("FundTypeName", returnValue(dto.getStradekind()));// 资金性质名称
			vouchermap.put("ClearBankCode", returnValue(""));// 人民银行编码
			vouchermap.put("ClearBankName", returnValue(""));// 人民银行名称
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayeeacctno()));// 收款人账号
			vouchermap.put("PayeeAcctName", returnValue(dto.getSpayeeacctname()));// 收款人名称
			vouchermap.put("PayeeAcctBankName", returnValue(dto.getShold1()));// 收款人银行
			vouchermap.put("PayeeAcctBankNo", returnValue(dto.getSpayeeacctbankname()));// 收款银行行号
			vouchermap.put("PayAcctNo", returnValue(dto.getSpayacctno()));// 付款人账号
			vouchermap.put("PayAcctName", returnValue(dto.getSpayacctname()));// 付款人名称
			vouchermap.put("PayAcctBankName", returnValue(dto.getSpayacctbankname()));// 付款人银行
			vouchermap.put("ReturnReasonName", returnValue(""));// 退款原因
			vouchermap.put("PayAmt", String.valueOf(dto.getNmoney()));// 退款金额
			vouchermap.put("XpayAmt", String.valueOf(dto.getNmoney()));//金额
			vouchermap.put("XPayDate", returnValue(dto.getScommitdate()));// 退款日期
			vouchermap.put("XAgentBusinessNo", returnValue(dto.getSpaydealno())); //银行交易流水号
			vouchermap.put("Hold1", returnValue("")); // 预留字段1
			vouchermap.put("Hold2", returnValue("")); // 预留字段2
			
			
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			
			String mainvou = VoucherUtil.getGrantSequence();
			Detailmap.put("Id", returnValue(mainvou)); // 退付明细编号
			Detailmap.put("VoucherBillId", vDto.getSdealno()); // 退款通知书Id		
			Detailmap.put("BgtTypeCode", returnValue("")); // 预算类型编码
			Detailmap.put("BgtTypeName", returnValue("")); // 预算类型名称
			Detailmap.put("ProCatCode", returnValue("")); // 收支管理编码
			Detailmap.put("ProCatName", returnValue("")); // 收支管理名称
			Detailmap.put("AgencyCode", returnValue("")); // 预算单位编码
			Detailmap.put("AgencyName", returnValue("")); // 预算单位名称
			Detailmap.put("IncomeSortCode",returnValue("")); // 收入分类科目编码
			Detailmap.put("IncomeSortName",returnValue("")); // 收入分类科目名称
			Detailmap.put("IncomeSortCode1",returnValue("")); // 收入分类科目编码
			Detailmap.put("IncomeSortName1",returnValue("")); // 收入分类科目名称
			Detailmap.put("IncomeSortCode2",returnValue("")); // 收入分类科目编码
			Detailmap.put("IncomeSortName2",returnValue("")); // 收入分类科目名称
			Detailmap.put("IncomeSortCode3",returnValue("")); // 收入分类科目编码
			Detailmap.put("IncomeSortName3",returnValue("")); // 收入分类科目名称
			Detailmap.put("IncomeSortCode4",returnValue("")); // 收入分类科目编码
			Detailmap.put("IncomeSortName4",returnValue("")); // 收入分类科目名称
			Detailmap.put("PayAmt", String.valueOf(dto.getNmoney())); // 退款金额
			Detailmap.put("Hold1", returnValue("")); // 预留字段1
			Detailmap.put("Hold2", returnValue("")); // 预留字段2
			Detailmap.put("Hold3", returnValue("")); // 预留字段3				
			Detailmap.put("Hold4", returnValue("")); // 预留字段3				
			Detail.add(Detailmap);
			
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！", e);
		}
	}
	
	
	
	/**
	 *  组装宁波收入退付退款报文_TCBS资金文件导入(最新接口20141015_V2.0版本)
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
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", vDto.getSdealno());// 实拨退款通知书Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());// 业务年度
			vouchermap.put("VtCode", vDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());// 凭证日期
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// 凭证号
			vouchermap.put("AgentBusinessNo", returnValue(dto.getSsecretsign()));//原银行交易流水号
			vouchermap.put("OriBillNo", returnValue(""));// 原退付单单号
			vouchermap.put("OriVouDate", returnValue(""));// 原退付单凭证日期
			vouchermap.put("OriPayDate", returnValue(""));// 原退付日期
			vouchermap.put("TreCode", returnValue(dto.getStrecode()));//国库代码
			vouchermap.put("FinOrgCode", returnValue(""));// 财政机关代码
			vouchermap.put("FundTypeCode", returnValue(dto.getSvtcodedes()));// 资金性质编码
			vouchermap.put("FundTypeName", returnValue(dto.getStradekind()));// 资金性质名称
			vouchermap.put("ClearBankCode", returnValue(""));// 人民银行编码
			vouchermap.put("ClearBankName", returnValue(""));// 人民银行名称
			
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayacctno()));// 原收款人账号
			vouchermap.put("PayeeAcctName", returnValue(dto.getSpayacctname()));// 原收款人名称
			vouchermap.put("PayeeAcctBankName", returnValue(dto.getSforwardbankname()));// 原收款人银行
			vouchermap.put("PayeeAcctBankNo", returnValue(dto.getSpayacctbankname()));// 原收款银行行号
			vouchermap.put("PayAcctNo", returnValue(dto.getSpayeeacctno()));// 原付款人账号
			vouchermap.put("PayAcctName", returnValue(dto.getSpayeeacctname()));// 原付款人名称
			vouchermap.put("PayAcctBankName", returnValue(dto.getShold1()));// 原付款人银行
			
			vouchermap.put("AgencyCode", returnValue("")); // 预算单位编码
			vouchermap.put("AgencyName", returnValue("")); // 预算单位名称
			vouchermap.put("Remark", returnValue(""));// 退款原因
			vouchermap.put("PayAmt", "-"+MtoCodeTrans.transformString(dto.getNmoney()));// 退款金额
			vouchermap.put("Hold1", returnValue("")); // 预留字段1
			vouchermap.put("Hold2", returnValue("")); // 预留字段2
			
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			
			String mainvou = VoucherUtil.getGrantSequence();
			Detailmap.put("Id", returnValue(mainvou)); // 退付明细编号
			Detailmap.put("VoucherBillId", vDto.getSdealno()); // 退款通知书Id		
			Detailmap.put("BgtTypeCode", returnValue("")); // 预算类型编码
			Detailmap.put("BgtTypeName", returnValue("")); // 预算类型名称
			Detailmap.put("ProCatCode", returnValue("")); // 收支管理编码
			Detailmap.put("ProCatName", returnValue("")); // 收支管理名称
			Detailmap.put("PayeeAcctNo", returnValue(dto.getSpayacctno()));// 原收款人账号
			Detailmap.put("PayeeAcctName", returnValue(dto.getSpayacctname()));// 原收款人名称
			Detailmap.put("PayeeAcctBankName", returnValue(dto.getSforwardbankname()));// 原收款人银行
			Detailmap.put("PayeeAcctBankNo", returnValue(dto.getSpayacctbankname()));// 原收款银行行号
			Detailmap.put("AgencyCode", returnValue("")); // 预算单位编码
			Detailmap.put("AgencyName", returnValue("")); // 预算单位名称
			Detailmap.put("IncomeSortCode",returnValue("")); // 收入分类科目编码
			Detailmap.put("IncomeSortName",returnValue("")); // 收入分类科目名称
			Detailmap.put("IncomeSortCode1",returnValue("")); // 收入分类科目编码
			Detailmap.put("IncomeSortName1",returnValue("")); // 收入分类科目名称
			Detailmap.put("IncomeSortCode2",returnValue("")); // 收入分类科目编码
			Detailmap.put("IncomeSortName2",returnValue("")); // 收入分类科目名称
			Detailmap.put("IncomeSortCode3",returnValue("")); // 收入分类科目编码
			Detailmap.put("IncomeSortName3",returnValue("")); // 收入分类科目名称
			Detailmap.put("IncomeSortCode4",returnValue("")); // 收入分类科目编码
			Detailmap.put("IncomeSortName4",returnValue("")); // 收入分类科目名称
			Detailmap.put("PayAmt", "-"+MtoCodeTrans.transformString(dto.getNmoney())); // 退款金额
			Detailmap.put("Hold1", returnValue("")); // 预留字段1
			Detailmap.put("Hold2", returnValue("")); // 预留字段2
			Detailmap.put("Hold3", returnValue("")); // 预留字段3				
			Detailmap.put("Hold4", returnValue("")); // 预留字段4				
			Detail.add(Detailmap);
			
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！", e);
		}
	}
	
	
	
	/**
	 * DTO转化XML报文
	 * 
	 * @param List
	 *            生成报文要素集合
	 * @return
	 * @throws ITFEBizException
	 */
	public Map tranfor(List lists) throws ITFEBizException {
		try {
			//宁波采用最新接口20141015_V2.0版本
			if(ITFECommonConstant.SRC_NODE.equals("000057400006")){
				return tranforForNB(lists);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvDwbkDto dto = (TvDwbkDto) lists.get(1);
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(2);
			//退库退回原因
			String dwbkBackReason = (String) lists.get(3);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", returnValue(vDto.getSdealno()));// 收入退付凭证Id
			vouchermap.put("AdmDivCode", returnValue(vDto.getSadmdivcode()));// 行政区划代码
			vouchermap.put("StYear", returnValue(vDto.getSstyear()));// 业务年度
			vouchermap.put("VtCode", returnValue(vDto.getSvtcode()));// 凭证类型编号
			vouchermap.put("VouDate", returnValue(vDto.getScreatdate()));// 凭证日期
			vouchermap.put("VoucherNo", returnValue(vDto.getSvoucherno()));// 凭证号
			vouchermap.put("AgentBusinessNo", returnValue(String.valueOf(dto.getIvousrlno())));// 原银行交易流水号
			vouchermap.put("OriBillNo", returnValue(dto.getSdwbkvoucode()));// 原拨款单单号
			vouchermap.put("OriVouDate", returnValue(String.valueOf(dto.getDaccept())));// 原拨款单凭证日期
			vouchermap.put("OriPayDate", returnValue(String.valueOf(dto.getDacct())));// 原支付日期
			vouchermap.put("TreCode", returnValue(dto.getSpayertrecode()));// 国库主体代码
			vouchermap.put("FinOrgCode", returnValue(dto.getStaxorgcode()));// 财政机关代码
			vouchermap.put("FundTypeCode", returnValue(dto.getSfundtypecode()));// 资金性质编码
			vouchermap.put("FundTypeName", returnValue(dto.getSfundtypename()));// 资金性质名称
			vouchermap.put("ClearBankCode", returnValue(dto.getSclearbankcode()));// 人民银行编码
			vouchermap.put("ClearBankName", returnValue(dto.getSclearbankname()));// 人民银行名称
			TsPaybankDto paydto = new TsPaybankDto();
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayacctno()));// 收款人账号dto.getSpayeeacct()
			vouchermap.put("PayeeAcctName", returnValue(dto.getSpayacctname()));// 收款人名称dto.getSpayeename()
			vouchermap.put("PayeeAcctBankName", returnValue(dto.getSpayacctbankname()));// 收款人银行dto.getSpayeecode()
			paydto.setSbankname(dto.getSpayacctbankname());
			List<TsPaybankDto> payList = CommonFacade.getODB().findRsByDto(paydto);
			vouchermap.put("PayeeAcctBankNo",payList!=null&&payList.size()>0?payList.get(0).getSbankno():returnValue(dto.getSpayeeopnbnkno()));// 收款银行行号dto.getSpayeeopnbnkno()
			vouchermap.put("PayAcctNo", returnValue(dto.getSpayeeacct()));// 付款人账号dto.getSpayacctno()
			vouchermap.put("PayAcctName", returnValue(dto.getSpayeename()));// 付款人名称dto.getSpayacctname()
			paydto = new TsPaybankDto();
			paydto.setSbankno(dto.getSpayeecode());
			payList = CommonFacade.getODB().findRsByDto(paydto);
			vouchermap.put("PayAcctBankName", payList!=null&&payList.size()>0?payList.get(0).getSbankname():returnValue(dto.getSpayeename()));// 付款人银行dto.getSpayacctbankname()
			vouchermap.put("ReturnReasonName", returnValue(dto.getSreturnreasonname()));// 退付原因
			vouchermap.put("PayAmt", dto.getShold2());// 退付金额
			vouchermap.put("XpayAmt", String.valueOf(dto.getFamt()));//退付 金额dto.getXpayamt()
			vouchermap.put("XPayDate", TimeFacade.getCurrentStringTime());// 退付日期
			vouchermap.put("XAgentBusinessNo", returnValue(dto.getXagentbusinessno()));// 银行交易流水号
			vouchermap.put("Remark", returnValue(dwbkBackReason));// 退款原因
			vouchermap.put("Hold1", returnValue(dto.getShold1()));// 预留字段1
			vouchermap.put("Hold2", returnValue(""));// 预留字段2	
			
			
			
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			
			Detailmap.put("Id", returnValue(dto.getSbizno())); // 退付明细编号
			Detailmap.put("VoucherBillId", returnValue(vDto.getSdealno())); // 退付凭证Id		
			Detailmap.put("BgtTypeCode", returnValue(dto.getSbgttypecode())); // 预算类型编码
			Detailmap.put("BgtTypeName", returnValue(dto.getSbgttypename())); // 预算类型名称
			Detailmap.put("ProCatCode", returnValue(dto.getSprocatcode())); // 收支管理编码
			Detailmap.put("ProCatName", returnValue(dto.getSprocatname())); // 收支管理名称
			Detailmap.put("AgencyCode", returnValue(dto.getSpayeecode())); // 预算单位编码
			Detailmap.put("AgencyName", returnValue(dto.getSagencyname())); // 预算单位名称
			Detailmap.put("IncomeSortCode",returnValue(dto.getSbdgsbtcode())); // 收入分类科目编码
			Detailmap.put("IncomeSortName",returnValue(dto.getSincomesortname())); // 收入分类科目名称
			Detailmap.put("IncomeSortCode1", returnValue(dto.getSincomesortcode1())); // 收入分类科目类编码
			Detailmap.put("IncomeSortName1", returnValue(dto.getSincomesortname1())); // 收入分类科目类名称
			Detailmap.put("IncomeSortCode2", returnValue(dto.getSincomesortcode2())); // 收入分类科目款编码
			Detailmap.put("IncomeSortName2", returnValue(dto.getSincomesortname2())); // 收入分类科目款名称
			Detailmap.put("IncomeSortCode3", returnValue(dto.getSincomesortcode3())); // 收入分类科目项编码
			Detailmap.put("IncomeSortName3", returnValue(dto.getSincomesortname3())); // 收入分类科目项名称
			Detailmap.put("IncomeSortCode4", returnValue(dto.getSincomesortcode4())); // 收入分类科目目编码
			Detailmap.put("IncomeSortName4", returnValue(dto.getSincomesortname4())); // 收入分类科目目名称
			Detailmap.put("PayAmt", dto.getShold2()); // 金额
			vouchermap.put("Hold1", returnValue(dto.getSdetailhold1())); // 预留字段1
			vouchermap.put("Hold2", returnValue("")); // 预留字段2
			vouchermap.put("Hold3", returnValue(dto.getSdetailhold3())); // 预留字段3				
			vouchermap.put("Hold4",returnValue(dto.getSdetailhold4())); // 预留字段4		
			Detail.add(Detailmap);
			
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！", e);
		}
	}
	
	
	/**
	 * 组装宁波收入退付退款报文_前置发起(最新接口20141015_V2.0版本)
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
			//退库退回原因
			String dwbkBackReason = (String) lists.get(3);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", returnValue(vDto.getSdealno()));// 收入退付凭证Id
			vouchermap.put("AdmDivCode", returnValue(vDto.getSadmdivcode()));// 行政区划代码
			vouchermap.put("StYear", returnValue(vDto.getSstyear()));// 业务年度
			vouchermap.put("VtCode", returnValue(vDto.getSvtcode()));// 凭证类型编号
			vouchermap.put("VouDate", returnValue(vDto.getScreatdate()));// 凭证日期
			vouchermap.put("VoucherNo", returnValue(vDto.getSvoucherno()));// 凭证号
			vouchermap.put("AgentBusinessNo", returnValue(String.valueOf(dto.getIvousrlno())));// 原银行交易流水号
			vouchermap.put("OriBillNo", returnValue(dto.getSdwbkvoucode()));// 原拨款单单号
			vouchermap.put("OriVouDate", returnValue(String.valueOf(dto.getDaccept())));// 原拨款单凭证日期
			vouchermap.put("OriPayDate", returnValue(String.valueOf(dto.getDacct())));// 原支付日期
			vouchermap.put("TreCode", returnValue(dto.getSpayertrecode()));// 国库主体代码
			vouchermap.put("FinOrgCode", returnValue(dto.getStaxorgcode()));// 财政机关代码
			vouchermap.put("FundTypeCode", returnValue(dto.getSfundtypecode()));// 资金性质编码
			vouchermap.put("FundTypeName", returnValue(dto.getSfundtypename()));// 资金性质名称
			vouchermap.put("ClearBankCode", returnValue(dto.getSclearbankcode()));// 人民银行编码
			vouchermap.put("ClearBankName", returnValue(dto.getSclearbankname()));// 人民银行名称

			//原收付款人信息
			vouchermap.put("PayeeAcctNo", returnValue(dto.getSpayeeacct()));// 原收款人账号
			vouchermap.put("PayeeAcctName", returnValue(dto.getSpayeename()));// 原收款人名称
			vouchermap.put("PayeeAcctBankName", returnValue(dto.getSinputrecbankname()));// 原收款人银行
			vouchermap.put("PayeeAcctBankNo",returnValue(dto.getSpayeeopnbnkno()));// 原收款银行行号
			vouchermap.put("PayAcctNo", returnValue(dto.getSpayacctno()));// 原付款人账号
			vouchermap.put("PayAcctName", returnValue(dto.getSpayacctname()));// 原付款人名称
			vouchermap.put("PayAcctBankName", returnValue(dto.getSpayacctbankname()));// 原付款人银行
			
			vouchermap.put("AgencyCode", returnValue(dto.getSpayeecode())); // 预算单位编码
			vouchermap.put("AgencyName", returnValue(dto.getSagencyname())); // 预算单位名称
			vouchermap.put("Remark", returnValue(dwbkBackReason));// 退款原因
			vouchermap.put("PayAmt", "-"+MtoCodeTrans.transformString(dto.getSdetailhold1().trim()));// 退付金额
			vouchermap.put("Hold1", returnValue(dto.getShold1()));// 预留字段1(预算级次代码，宁波用)
			vouchermap.put("Hold2", returnValue(dto.getShold2()));// 预留字段2(征收机关代码，宁波用)
			
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			
			Detailmap.put("Id", returnValue(dto.getSbizno())); // 退付明细编号
			Detailmap.put("VoucherBillId", returnValue(vDto.getSdealno())); // 退付凭证Id		
			Detailmap.put("BgtTypeCode", returnValue(dto.getSbgttypecode())); // 预算类型编码
			Detailmap.put("BgtTypeName", returnValue(dto.getSbgttypename())); // 预算类型名称
			Detailmap.put("ProCatCode", returnValue(dto.getSprocatcode())); // 收支管理编码
			Detailmap.put("ProCatName", returnValue(dto.getSprocatname())); // 收支管理名称
			Detailmap.put("PayeeAcctNo", returnValue(dto.getSpayeeacct()));// 原收款人账号
			Detailmap.put("PayeeAcctName", returnValue(dto.getSpayeename()));// 原收款人名称
			Detailmap.put("PayeeAcctBankName", returnValue(dto.getSinputrecbankname()));// 原收款人银行
			Detailmap.put("PayeeAcctBankNo",returnValue(dto.getSpayeeopnbnkno()));// 原收款银行行号
			Detailmap.put("AgencyCode", returnValue(dto.getSpayeecode())); // 预算单位编码
			Detailmap.put("AgencyName", returnValue(dto.getSagencyname())); // 预算单位名称
			Detailmap.put("IncomeSortCode",returnValue(dto.getSbdgsbtcode())); // 收入分类科目编码
			Detailmap.put("IncomeSortName",returnValue(dto.getSincomesortname())); // 收入分类科目名称
			Detailmap.put("IncomeSortCode1", returnValue(dto.getSincomesortcode1())); // 收入分类科目类编码
			Detailmap.put("IncomeSortName1", returnValue(dto.getSincomesortname1())); // 收入分类科目类名称
			Detailmap.put("IncomeSortCode2", returnValue(dto.getSincomesortcode2())); // 收入分类科目款编码
			Detailmap.put("IncomeSortName2", returnValue(dto.getSincomesortname2())); // 收入分类科目款名称
			Detailmap.put("IncomeSortCode3", returnValue(dto.getSincomesortcode3())); // 收入分类科目项编码
			Detailmap.put("IncomeSortName3", returnValue(dto.getSincomesortname3())); // 收入分类科目项名称
			Detailmap.put("IncomeSortCode4", returnValue(dto.getSincomesortcode4())); // 收入分类科目目编码
			Detailmap.put("IncomeSortName4", returnValue(dto.getSincomesortname4())); // 收入分类科目目名称
			Detailmap.put("PayAmt", "-"+MtoCodeTrans.transformString(dto.getSdetailhold1().trim())); // 预留字段1
			Detailmap.put("Hold1", returnValue(dto.getSdetailhold3())); // 预留字段1(预算种类,宁波用)
			Detailmap.put("Hold2", returnValue(dto.getSdetailhold2())); // 预留字段2(退库原因代码,宁波用)
			Detailmap.put("Hold3", returnValue("")); // 预留字段3(退款依据，宁波用)			
			Detailmap.put("Hold4", returnValue("")); // 预留字段4			
			Detail.add(Detailmap);
			
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！", e);
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
	 * 查询实拨资金退款业务表信息
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
			throw new ITFEBizException("查询国库往来票据信息出错！",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！",e);
		}
	}
	
	
	
	/**
	 * 凭证判重
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
			throw new ITFEBizException("查询国库往来票据信息出错！",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！",e);
		}		
	}
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}

}

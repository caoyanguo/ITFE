package com.cfcc.itfe.util.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 国库往来票据（3403）
 * 
 * @author hejianrong
 * @time 2014-04-02
 * 
 */
public class VoucherDto2MapFor3403 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3403.class);

	/**
	 * 凭证生成
	 * 
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public List voucherGenerate(TvVoucherinfoDto dto) throws ITFEBizException {
		List list = findMainDto(dto);
		if (list.size() == 0)
			return list;
		List lists = new ArrayList();
		for (TvVoucherinfoAllocateIncomeDto mainDto : (List<TvVoucherinfoAllocateIncomeDto>) list) {
			List<TvVoucherinfoDto> voutherList = voucherIsRepeat(dto, mainDto);
			if (voutherList != null && voutherList.size() > 0) {
				continue;
			}
			lists.add(voucherTranfor(dto, mainDto));
		}
		return lists;
	}

	/**
	 * 生成凭证 生成凭证报文
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
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));
		dto.setSvoucherflag("1");
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto
				.getScreatdate(), dto.getSdealno()));
		dto.setSadmdivcode(mainDto.getSadmdivcode());
//		dto.setSvoucherno(mainDto.getScommitdate() + mainDto.getSpaydealno()+"序号");
		dto.setSvoucherno(dto.getSdealno());
		mainDto.setShold2(dto.getSvoucherno());
		dto.setNmoney(mainDto.getNmoney());
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("处理成功");
		List lists = new ArrayList();
//		List listmain = new ArrayList(); //用于存放等待更新的主表信息，设置生成标志
//		listmain.add(mainDto);
		lists.add(dto);
		lists.add(mainDto);
		Map map = tranfor(lists);
		lists.clear();
		lists.add(map);
		lists.add(dto);
//		lists.add(listmain);
		lists.add(mainDto);
		return lists;
	}

	/**
	 * 查询中央及省市往来票据(调拨收入凭证)业务表信息
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
			return CommonFacade.getODB().findRsByDtoForWhere(mainDto, " AND  S_HOLD2 IS NULL");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！", e);
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
	@SuppressWarnings("unchecked")
	public Map tranfor(List lists) throws ITFEBizException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		// 上海国库往来票据接口
		if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0) {
			map = (HashMap<String, Object>) tranforForSH(lists);
		} else {// 最新20141015_V2.0接口
			map = (HashMap<String, Object>) tranforForOther(lists);
		}
		return map;
	}

	public Map<String, Object> tranforForSH(List lists) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto mainDto = (TvVoucherinfoAllocateIncomeDto) lists
					.get(1);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", vDto.getSdealno());// 中央及省市往来票据Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());// 业务年度
			vouchermap.put("VtCode", vDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());// 凭证日期
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// 凭证号
			vouchermap.put("PayeeAcctNo", mainDto.getSpayeeacctno());// 收款人账号
			vouchermap.put("PayeeAcctName", mainDto.getSpayeeacctname());// 收款人名称
			vouchermap
					.put("PayeeAcctBankName", mainDto.getSpayeeacctbankname());// 收款人银行
			vouchermap.put("PayAcctNo", mainDto.getSpayacctno());// 付款人账号
			vouchermap.put("PayAcctName", mainDto.getSpayacctname());// 付款人名称
			vouchermap.put("PayAcctBankName", mainDto.getSpayacctbankname());// 付款人银行
			vouchermap.put("PaySummaryCode", "");// 用途编码
			vouchermap.put("PaySummaryName", "");// 用途名称
			vouchermap.put("PayAmt", MtoCodeTrans.transformString(mainDto
					.getNmoney()));// 拨款金额
			vouchermap.put("AgencyCode", "");// 基层预算单位编码
			vouchermap.put("AGencyName", "");// 基层预算单位名称
			vouchermap.put("MsgType", mainDto.getSreportkind());// 报文种类
			vouchermap.put("PayTraseqNo", mainDto.getSpaydealno());// 支付交易序号
			vouchermap.put("TrasType", "");// 交易种类
			// vouchermap.put("BizType", mainDto.getSvtcodekind());//业务类型
			vouchermap.put("BizType", "");// 业务类
			if (!(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0)) {
				vouchermap.put("SndBnkNo", mainDto.getSforwardbankno());// 发起行行号
			}
			vouchermap.put("PayerOpnBnkNo", "");// 付款人开户行行号
			vouchermap.put("PayeeOpnbnkNo", "");// 收款人开户行行号
//			vouchermap.put("DebitAcct", mainDto.getSborrow());// 记账账户借方
			vouchermap.put("DebitAcct", "");// 记账账户借方
//			vouchermap.put("LoanAcct", mainDto.getSlend());// 记账账户贷方
			vouchermap.put("LoanAcct", "");// 记账账户贷方
			vouchermap.put("AddWord", mainDto.getSdemo());// 附言
			vouchermap.put("TrasrlNo", mainDto.getSdealno());// 交易流水号
			vouchermap.put("Hold1", "");// 预留字段1
			vouchermap.put("Hold2", "");// 预留字段2
			vouchermap.put("Hold3", "");// 预留字段3
			vouchermap.put("Hold4", "");// 预留字段4
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！", e);
		}

	}

	public Map<String, Object> tranforForOther(List lists)
			throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto = (TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto mainDto = (TvVoucherinfoAllocateIncomeDto) lists
					.get(1);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", vDto.getSdealno());// 中央及省市往来票据Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());// 业务年度
			vouchermap.put("VtCode", vDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());// 凭证日期
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// 凭证号
			vouchermap.put("PayeeAcctNo",
					returnValue(mainDto.getSpayeeacctno()));// 收款人账号
			vouchermap.put("PayeeAcctName", returnValue(mainDto
					.getSpayeeacctname()));// 收款人名称
			if (StringUtils.isBlank(mainDto.getShold1())
					&& !StringUtils.isBlank(mainDto.getSreceivebankno())) {
				HashMap<String, TsPaybankDto> bankmap = SrvCacheFacade
						.cachePayBankInfo();
				TsPaybankDto paybankDto = bankmap.get(mainDto
						.getSreceivebankno());
				vouchermap.put("PayeeAcctBankName", returnValue(paybankDto
						.getSbankname()));// 收款人银行
			} else {
				vouchermap.put("PayeeAcctBankName", returnValue(mainDto
						.getShold1()));// 收款人银行
			}
			vouchermap.put("PayAcctNo", returnValue(mainDto.getSpayacctno()));// 付款人账号
			vouchermap.put("PayAcctName",
					returnValue(mainDto.getSpayacctname()));// 付款人名称
			vouchermap.put("PayAcctBankName", returnValue(mainDto
					.getSforwardbankname()));// 付款人银行
			vouchermap.put("PaySummaryCode", "");// 用途编码
			vouchermap.put("PaySummaryName", "");// 用途名称
			vouchermap.put("PayAmt", MtoCodeTrans.transformString(mainDto
					.getNmoney()));// 拨款金额
			vouchermap.put("AgencyCode", "");// 基层预算单位编码
			vouchermap.put("AGencyName", "");// 基层预算单位名称
			vouchermap.put("MsgType", mainDto.getSreportkind());// 报文种类
			vouchermap.put("PayTraseqNo", mainDto.getSpaydealno());// 支付交易序号
			vouchermap.put("TrasType", mainDto.getStradekind());// 交易种类
			vouchermap.put("BizType", mainDto.getSvtcodekind());// 业务类型
			// 宁波接口
			if (ITFECommonConstant.SRC_NODE.equals("000057400006")) {
				vouchermap.put("PayerOpnBnkNo", mainDto.getSpayacctbankname());// 付款人开户行行号
				vouchermap
						.put("PayeeOpnbnkNo", mainDto.getSpayeeacctbankname());// 收款人开户行行号
				vouchermap.put("TrasrlNo", mainDto.getSdealno());// 交易流水号
			} else {
				vouchermap.put("PayAcctBankNo", mainDto.getSpayacctbankname());// 付款人开户行行号
				vouchermap.put("PayeeAcctBankNo", mainDto
						.getSpayeeacctbankname());// 收款人开户行行号
				vouchermap.put("AgentBusinessNo", mainDto.getSdealno());// 交易流水号
			}
			vouchermap.put("DebitAcct", mainDto.getSborrow());// 记账账户借方
			vouchermap.put("LoanAcct", mainDto.getSlend());// 记账账户贷方
			vouchermap.put("AddWord", mainDto.getSdemo());// 附言
			vouchermap.put("Hold1", "");// 预留字段1
			vouchermap.put("Hold2", "");// 预留字段2
			vouchermap.put("Hold3", "");// 预留字段2
			vouchermap.put("Hold4", "");// 预留字段4
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！", e);
		}

	}

	@SuppressWarnings("unchecked")
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

	/**
	 * 凭证判重
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
//		vDto.setSvoucherno(mainDto.getScommitdate() + mainDto.getSpaydealno());
		vDto.setSvoucherno(mainDto.getSpaydealno());
		try {
			return CommonFacade.getODB().findRsByDto(vDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库往来票据信息出错！", e);
		}
	}

	private String returnValue(String value) {
		if (value == null || "".equals(value)) {
			return "";
		} else {
			return value;
		}
	}

}

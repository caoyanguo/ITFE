package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 划款申请退款转化
 * 
 * @author wangyunbin
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor2302 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor2301.class);
	private Map<String, TsInfoconnorgaccDto> accMap = null;

	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

	/**
	 * 凭证生成
	 * 
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException {
		if (vDto == null)
			return null;
		List returnList = new ArrayList();
		vDto.setShold1("0");
		searchBankBackInfo(vDto, returnList);
		return returnList;
	}

	/**
	 * 组装退回信息map信息
	 * 
	 * @param vDto
	 * @param returnList
	 * @throws ITFEBizException
	 */
	private void searchBankBackInfo(TvVoucherinfoDto vDto, List returnList)
			throws ITFEBizException {
		TvVoucherinfoDto directVoucherDto;
		try {
			// 通过2252生成的直接支付退款，通过前置生成的直接支付退款也包含在退款表中
			List<TvPayreckBankBackDto> directBankBackList = getPayreckBankBack(vDto);
			if (null != directBankBackList && directBankBackList.size() > 0) {

				List<TsInfoconnorgaccDto> accList = null;
				TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
				accdto.setStrecode(vDto.getStrecode());
				accdto.setSorgcode(vDto.getSorgcode());
				accList = (List<TsInfoconnorgaccDto>) CommonFacade.getODB()
						.findRsByDto(accdto);
				if (accList != null && accList.size() > 0) {
					accMap = new HashMap<String, TsInfoconnorgaccDto>();
					for (TsInfoconnorgaccDto tempdto : accList) {
						if (tempdto.getSpayeraccount().indexOf(
								vDto.getSorgcode() + "271") >= 0)
							accMap.put("271", tempdto); // 上海市财政局国库存款户
						else if (tempdto.getSpayeraccount().indexOf(vDto.getSorgcode() + "371") >= 0 && !(tempdto.getSpayername().indexOf("授权") >= 0))
							accMap.put("371", tempdto);
					}
				}

				// 根据日期和国库代码查询业务明细表
				TvPayreckBankBackListDto tvPayreckBankBackListDto;
				List<IDto> directBankBackDetailList;
				for (TvPayreckBankBackDto tvPayreckBankBackDto : directBankBackList) {
					tvPayreckBankBackListDto = new TvPayreckBankBackListDto();
					tvPayreckBankBackListDto.setIvousrlno(tvPayreckBankBackDto
							.getIvousrlno());
					directBankBackDetailList = CommonFacade.getODB()
							.findRsByDto(tvPayreckBankBackListDto);
					if (null == directBankBackDetailList
							|| directBankBackDetailList.size() == 0) {
						continue;
					}
					// 生成TvVoucherinfoDto
					directVoucherDto = getVouhcerinfoDto(tvPayreckBankBackDto,
							vDto);
					tvPayreckBankBackDto.setSpayeeacct(accMap.get("271")
							.getSpayeraccount());
					tvPayreckBankBackDto.setSpayeename(accMap.get("271")
							.getSpayername());
					tvPayreckBankBackDto.setSpayeracct(accMap.get("371").getSpayeraccount());
					tvPayreckBankBackDto.setSpayername(accMap.get("371").getSpayername());
					// 根据业务表组装Map
					Map xmlMapDirect = Dto2XmlMap(tvPayreckBankBackDto,
							directBankBackDetailList, directVoucherDto);
					tvPayreckBankBackDto.setSisreturn("1");		//已经生成过2302的记录不再重新生成
					DatabaseFacade.getODB().update(tvPayreckBankBackDto);
					List directList = new ArrayList();
					directList.add(xmlMapDirect);
					directList.add(directVoucherDto);
					returnList.add(directList);
					
				}

			}
		} catch (JAFDatabaseException e) {
			logger.error("查询数据异常！", e);
			throw new ITFEBizException("查询数据异常！", e);
		} catch (ValidateException e) {
			logger.error("查询数据异常！", e);
			throw new ITFEBizException("查询数据异常！", e);
		} catch (ITFEBizException e) {
			logger.error(e.getMessage(), e);
			throw new ITFEBizException(e.getMessage(), e);
		}

	}

	/**
	 * @throws ITFEBizException
	 * 
	 */
	private TvVoucherinfoDto getVouhcerinfoDto(
			TvPayreckBankBackDto bankBackDto, TvVoucherinfoDto vDto)
			throws ITFEBizException {
		String dirsep = File.separator;
		TvVoucherinfoDto vDtoReturn = new TvVoucherinfoDto();
		vDtoReturn.setSorgcode(bankBackDto.getSbookorgcode());
		vDtoReturn.setSadmdivcode(bankBackDto.getSadmdivcode());
		vDtoReturn.setSvtcode(MsgConstant.VOUCHER_NO_2302);
		vDtoReturn.setScreatdate(TimeFacade.getCurrentStringTime());
		vDtoReturn.setStrecode(bankBackDto.getStrecode());
		String mainvou = null;
		try {
			mainvou = VoucherUtil.getGrantSequence();
		} catch (ITFEBizException e) {
			logger.debug(e);
		}
		String FileName = ITFECommonConstant.FILE_ROOT_PATH + dirsep
				+ "Voucher" + dirsep + vDtoReturn.getScreatdate() + dirsep
				+ "send" + vDtoReturn.getSvtcode() + "_" + mainvou + ".msg";
		vDtoReturn.setSfilename(FileName);
		vDtoReturn.setSdealno(mainvou);
		vDtoReturn.setSstyear(vDtoReturn.getScreatdate().substring(0, 4));
		vDtoReturn.setScheckdate(vDto.getScheckdate());
		// vDtoReturn.setSpaybankcode(bankBackDto.getSxpaysndbnkno());
		vDtoReturn.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		vDtoReturn.setSdemo("处理成功");
		vDtoReturn.setSvoucherflag("1");
		vDtoReturn.setSvoucherno(mainvou);
		vDtoReturn.setIcount(1);
		vDtoReturn
				.setTssysupdate(new Timestamp(new java.util.Date().getTime()));// 更新时间
		BigDecimal moneySum = new BigDecimal(0.00);
		vDtoReturn.setNmoney(bankBackDto.getFamt());
		return vDtoReturn;
	}

	/**
	 * 根据根据日期和国库代码查询业务表 把所有生成的2203直接支付退款都放入业务表中，为了生成2302比较方便
	 * 
	 * @throws ITFEBizException
	 */
	private List<TvPayreckBankBackDto> getPayreckBankBack(TvVoucherinfoDto vDto)
			throws ITFEBizException {
		List<TvPayreckBankBackDto> dtoList = new ArrayList();
		String ls_SQL = "  AND s_paymode ='"
				+ vDto.getShold1()
				+ "' AND S_BACKFLAG = '1' AND S_ISRETURN IS NULL "
				+ " AND S_STATUS='"
				+ DealCodeConstants.DEALCODE_ITFE_SUCCESS
				+ "' "
				+ (StringUtils.isBlank(vDto.getScheckdate()) ? ""
						: " AND D_VOUDATE ='"
								+ CommonUtil.strToDate(vDto.getScheckdate())
								+ "'");
		try {
			TvPayreckBankBackDto _dto = new TvPayreckBankBackDto();
			_dto.setSbookorgcode(vDto.getSorgcode());
			_dto.setStrecode(vDto.getStrecode());
			dtoList = CommonFacade.getODB().findRsByDtoForWhere(_dto, ls_SQL);
		} catch (JAFDatabaseException e) {
			logger.debug(e);
			throw new ITFEBizException("查询直接支付退款数据出现异常！", e);
		} catch (ValidateException e) {
			logger.debug(e);
			throw new ITFEBizException("查询直接支付退款数据出现异常！", e);
		}
		return dtoList;
	}

	private void TvPayreckBankBackDto() {
		// TODO Auto-generated method stub

	}

	/**
	 * 根据根据日期和国库代码查询业务明细表
	 */
	private List<TvPayreckBankBackListDto> getPayreckBankBackDetail(
			TvVoucherinfoDto vDto) {
		String ls_SQL = " SELECT b.* FROM TV_PAYRECK_BANK_BACK a,TV_PAYRECK_BANK_BACK_LIST b "
				+ "WHERE a.I_VOUSRLNO = b.I_VOUSRLNO "
				+ "AND a.S_TRECODE = '"
				+ vDto.getStrecode()
				+ "' "
				+ "AND replace(CHAR(S_XCLEARDATE,iso),'-','') = '"
				+ vDto.getScheckdate() + "' " + "and a.S_PAYTYPECODE = ? ";
		List<TvPayreckBankBackListDto> bankBackDetailList = new ArrayList();
		try {
			SQLExecutor execDetail = DatabaseFacade.getDb()
					.getSqlExecutorFactory().getSQLExecutor();
			execDetail.addParam(vDto.getShold1());// 使用 该 字段临时存储支付方式
			SQLResults result = execDetail.runQueryCloseCon(ls_SQL,
					TvPayreckBankBackListDto.class);
			for (int i = 0; i < result.getRowCount(); i++) {
				TvPayreckBankBackListDto bankBackDetailDto = new TvPayreckBankBackListDto();
				bankBackDetailDto.setIvousrlno(result.getLong(i, "I_VOUSRLNO"));
				bankBackDetailDto.setSid(result.getString(i, "S_ID"));
				bankBackDetailDto.setSorivouno(result
						.getString(i, "S_ORIVOUNO"));
				bankBackDetailDto.setSvoucherno(result.getString(i,
						"S_VOUCHERNO"));
				bankBackDetailDto.setSbdgorgcode(result.getString(i,
						"S_BDGORGCODE"));
				bankBackDetailDto.setSsupdepname(result.getString(i,
						"S_SUPDEPNAME"));
				bankBackDetailDto.setSfuncbdgsbtcode(result.getString(i,
						"S_FUNCBDGSBTCODE"));
				bankBackDetailDto.setSexpfuncname(result.getString(i,
						"S_EXPFUNCNAME"));
				bankBackDetailDto.setFamt(result.getBigDecimal(i, "F_AMT"));
				bankBackDetailList.add(bankBackDetailDto);
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
		}
		return bankBackDetailList;
	}

	private Map Dto2XmlMap(TvPayreckBankBackDto bankBackDto,
			List<IDto> bankBackDetailList, TvVoucherinfoDto vDto)
			throws ITFEBizException {
		// 国库代理银行在 行名行别表中维护 行别简码为 001
		String PayBankName, PayBankNo;
		// 获取维护的财政机构信息
		TsConvertfinorgDto cDto = new TsConvertfinorgDto();
		cDto.setSorgcode(vDto.getSorgcode());
		cDto.setStrecode(vDto.getStrecode());
		List<TsConvertfinorgDto> tsConvertfinorgList;
		try {
			tsConvertfinorgList = (List<TsConvertfinorgDto>) CommonFacade
					.getODB().findRsByDto(cDto);

			if (tsConvertfinorgList == null || tsConvertfinorgList.size() == 0) {
				throw new ITFEBizException("国库：" + vDto.getStrecode()
						+ "对应的财政机关代码参数未维护！");
			}
			cDto = (TsConvertfinorgDto) tsConvertfinorgList.get(0);
			vDto.setSadmdivcode(cDto.getSadmdivcode());
			if (cDto.getSadmdivcode() == null
					|| cDto.getSadmdivcode().equals("")) {
				throw new ITFEBizException("国库：" + cDto.getStrecode()
						+ "对应的区划代码未维护！");
			}
		} catch (JAFDatabaseException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！", e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！", e2);
		}
		Map map = new HashMap();
		HashMap<String, Object> vouchermap = new HashMap<String, Object>();
		// 设置报文节点 Voucher
		map.put("Voucher", vouchermap);
		// 设置报文消息体
		vouchermap.put("Id", vDto.getSvoucherno());// 申请退款凭证Id
		vouchermap.put("AdmDivCode", bankBackDto.getSadmdivcode());// 行政区划代码
		vouchermap.put("StYear", getString(bankBackDto.getSofyear()));// 业务年度
		vouchermap.put("VtCode", getString(vDto.getSvtcode()));// 凭证类型编号
		vouchermap.put("VouDate", DateUtil.date2String2(bankBackDto
				.getDvoudate()));// 凭证日期
		vouchermap.put("VoucherNo", getString(vDto.getSvoucherno()));// 凭证号
		vouchermap.put("TreCode", getString(bankBackDto.getStrecode()));// 国库主体代码
		vouchermap.put("FinOrgCode", getString(bankBackDto.getSfinorgcode()));// 财政机关代码
		vouchermap.put("BgtTypeCode", getString(bankBackDto.getSbgttypecode()));// 预算类型编码
		vouchermap.put("BgtTypeName", getString(bankBackDto.getSbgttypename()));// 预算类型名称
		vouchermap.put("FundTypeCode",
				getString(bankBackDto.getSfundtypecode()));// 资金性质编码
		vouchermap.put("FundTypeName",
				getString(bankBackDto.getSfundtypename()));// 资金性质名称
		if ("1".equals(bankBackDto.getSpaytypecode())) {
			vouchermap.put("PayTypeCode", "12");// 支付方式编码
		} else if ("0".equals(bankBackDto.getSpaytypecode())) {
			vouchermap.put("PayTypeCode", "11");// 支付方式编码
		}
		// vouchermap.put("PayTypeCode",bankBackDto.getSpaytypecode());//支付方式编码
		vouchermap.put("PayTypeName", getString(bankBackDto.getSpaytypename()));// 支付方式名称
		vouchermap.put("AgentAcctNo", getString(bankBackDto.getSpayeeacct()));// 原收款银行账号
		vouchermap.put("AgentAcctName", getString(bankBackDto.getSpayeename()));// 原收款银行账户名称
		vouchermap.put("AgentAcctBankName", getString(bankBackDto
				.getSclearacctbankname()));// 原收款银行
		vouchermap.put("ClearAcctNo", getString(bankBackDto.getSpayeracct()));// 原付款账号
		vouchermap.put("ClearAcctName", getString(bankBackDto.getSpayername()));// 原付款账户名称
		vouchermap.put("ClearAcctBankName", getString(bankBackDto
				.getSclearacctbankname()));// 原付款银行
		vouchermap.put("PayDictateNo",
				getString(bankBackDto.getSpaydictateno()));// 支付交易序号
		vouchermap.put("PayMsgNo", getString(bankBackDto.getSpaymsgno()));// 支付报文编号
		vouchermap.put("PayEntrustDate", DateUtil.date2String2(bankBackDto
				.getDorientrustdate()));// 支付委托日期
		vouchermap.put("PayBankName", getString(bankBackDto.getSpaybankname()));// 代理银行名称
		vouchermap.put("PayBankNo", getString(bankBackDto.getSagentbnkcode()));// 代理银行行号
		vouchermap.put("Remark", "");// 摘要
		vouchermap.put("MoneyCorpCode", "");// 金融机构编码
		vouchermap.put("TKLX", StateConstant.TKLX_1); // 退款类型
		vouchermap
				.put("XPaySndBnkNo", getString(bankBackDto.getSpaysndbnkno()));// 支付发起行行号
		vouchermap.put("XAddWord", "");// 附言
		vouchermap.put("XClearDate", DateUtil.date2String2(bankBackDto
				.getSxcleardate()));// 清算日期
		vouchermap.put("Hold1", "");// 预留字段1
		vouchermap.put("Hold2", "");// 预留字段2
		HashMap<String, Object> DetailList = new HashMap<String, Object>();
		List detailList = new ArrayList();
		BigDecimal sumamt = new BigDecimal("0.00");
		HashMap<String, Object> Detailmap = new HashMap<String, Object>();
		Detailmap.put("Id", "01");// 序号
		// 查找发送2203的报文编号
		TvVoucherinfoDto tmpDto = new TvVoucherinfoDto();
		tmpDto.setSorgcode(vDto.getSorgcode());
		tmpDto.setStrecode(vDto.getStrecode());
		tmpDto.setSadmdivcode(vDto.getSadmdivcode());
		tmpDto.setShold2(String.valueOf(bankBackDto.getIvousrlno()) + "01");
		List<TvVoucherinfoDto> lists;
		try {
			lists = CommonFacade.getODB().findRsByDto(tmpDto);
			if (null == lists || lists.size() == 0) {
				throw new ITFEBizException(
						"没有需要生成对应的直接支付凭证信息，请查证是否存在已发送的直接支付退款通知书！");
			} else {
				tmpDto = lists.get(0);
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(
					"没有需要生成对应的直接支付凭证信息，请查证是否存在已发送的直接支付退款通知书！", e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException(
					"没有需要生成对应的直接支付凭证信息，请查证是否存在已发送的直接支付退款通知书！", e);
		}
		Detailmap.put("VoucherNo", getString(tmpDto.getSdealno()));
		Detailmap.put("SupDepCode", ((TvPayreckBankBackListDto)bankBackDetailList.get(0)).getSbdgorgcode());// 一级预算单位编码
		Detailmap.put("SupDepName", ((TvPayreckBankBackListDto)bankBackDetailList.get(0)).getSsupdepname());// 一级预算单位名称
		Detailmap.put("ExpFuncCode", ((TvPayreckBankBackListDto)bankBackDetailList.get(0)).getSfuncbdgsbtcode());// 支出功能分类科目编码
		Detailmap.put("ExpFuncName", ((TvPayreckBankBackListDto)bankBackDetailList.get(0)).getSexpfuncname());// 支出功能分类科目名称
		Detailmap.put("PaySummaryName", "");
		Detailmap.put("Hold1", getString(""));// 预留字段1
		Detailmap.put("Hold2", getString(""));// 预留字段2
		Detailmap.put("Hold3", getString(""));// 预留字段3
		Detailmap.put("Hold4", getString(""));// 预留字段4
		for (int i = 0; i < bankBackDetailList.size(); i++) {
			TvPayreckBankBackListDto detailDto = (TvPayreckBankBackListDto) bankBackDetailList
					.get(i);
			// DetailList.put("Detail", Detailmap);
			sumamt = sumamt.add(detailDto.getFamt());

		}
		Detailmap.put("PayAmt", sumamt.compareTo(BigDecimal.ZERO) == 0 ? "0.00"
				: "-" + MtoCodeTrans.transformString(sumamt));// 支付金额
		detailList.add(Detailmap);
		vouchermap.put("PayAmt",
				sumamt.compareTo(BigDecimal.ZERO) == 0 ? "0.00" : "-"
						+ MtoCodeTrans.transformString(sumamt));// 汇总清算金额
		vouchermap.put("XPayAmt",
				sumamt.compareTo(BigDecimal.ZERO) == 0 ? "0.00" : "-"
						+ MtoCodeTrans.transformString(sumamt));// 汇总清算金额
		DetailList.put("Detail", detailList);
		vouchermap.put("DetailList", DetailList);
		return map;
	}

	private String getString(String key) {
		if (key == null)
			key = "";
		return key;
	}

	public Map<String, TsInfoconnorgaccDto> getAccMap() {
		return accMap;
	}

	public void setAccMap(Map<String, TsInfoconnorgaccDto> accMap) {
		this.accMap = accMap;
	}
}

package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.PayreckCountDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoSubDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 清算信息对帐3507转化
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3550 implements IVoucherDto2Map {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3507.class);

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
		vDto.setScreatdate(TimeFacade.getCurrentStringTime());
		return getVoucher(vDto);
	}

	private List getVoucher(TvVoucherinfoDto vDto) throws ITFEBizException {
		List returnList = new ArrayList();
		TsConvertfinorgDto cDto = new TsConvertfinorgDto();
		SQLExecutor execDetail = null;
		try {
			cDto.setSorgcode(vDto.getSorgcode());
			cDto.setStrecode(vDto.getStrecode());
			List<TsConvertfinorgDto> tsConvertfinorgList = (List<TsConvertfinorgDto>) CommonFacade
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
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory()
					.getSQLExecutor();
			Map<String, PayreckCountDto> map = getDataList(vDto, execDetail);

			createVoucher(vDto, returnList, cDto, map);

		} catch (JAFDatabaseException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！", e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！", e2);
		} finally {
			if (execDetail != null)
				execDetail.closeConnection();
		}
		return returnList;
	}

	private void createVoucher(TvVoucherinfoDto vDto, List returnList,
			TsConvertfinorgDto cDto, Map<String, PayreckCountDto> allmap)
			throws ITFEBizException {
		if (allmap != null && allmap.size() > 0) {
			for (String keystr : allmap.keySet()) {
				PayreckCountDto dtopay = allmap.get(keystr);
				String danhao = null;
				String FileName = null;
				String dirsep = File.separator;
				String mainvou = "";
				TvVoucherinfoDto dto = new TvVoucherinfoDto();
				dto.setSorgcode(vDto.getSorgcode());
				dto.setSadmdivcode(vDto.getSadmdivcode());
				dto.setSvtcode(vDto.getSvtcode());
				dto.setScreatdate(TimeFacade.getCurrentStringTime());
				dto.setStrecode(vDto.getStrecode());
				dto.setSstyear(dto.getScreatdate().substring(0, 4));
				dto.setScheckdate(vDto.getScheckdate());
				dto.setShold3(vDto.getShold3());
				dto.setShold4(vDto.getShold4());
				dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				dto.setSdemo("处理成功");
				dto.setSvoucherflag("1");
				dto.setSext1(getString(vDto.getSext1()));// 发起方式1:人行发起,2:财政发起,3:商行发起
				mainvou = VoucherUtil.getGrantSequence();
				vDto.setSdealno(mainvou);
				vDto.setSvoucherno(mainvou);
				if (danhao == null)
					danhao = mainvou;
				FileName = ITFECommonConstant.FILE_ROOT_PATH + dirsep
						+ "Voucher" + dirsep + vDto.getScreatdate() + dirsep
						+ "send" + vDto.getSvtcode() + "_" + mainvou + ".msg";
				dto.setSfilename(FileName);
				// mapList.add(vDto);
				// mapList.add(cDto);
				// mapList.add(tempList);
				List<IDto> idtoList = new ArrayList<IDto>();
				Map map = tranfor(vDto, dtopay, danhao, idtoList);
				dto.setSattach(danhao);// 对帐单号
				dto.setNmoney(MtoCodeTrans.transformBigDecimal(((Map) map
						.get("Voucher")).get("AllAmt")));
				// 此处填写错误，不能填写账号，应该明细中获取对账机构（
				// dto.setSpaybankcode(String.valueOf(((Map)map.get("Voucher")).get("ClearAccNo")));
				List<Map> list = (List) (((Map) ((Map) map.get("Voucher"))
						.get("DetailList"))).get("Detail");
				dto.setSpaybankcode("011");
				dto.setSdealno(mainvou);
				dto.setSvoucherno(mainvou);
				List vouList = new ArrayList();
				vouList.add(map);
				vouList.add(dto);
				vouList.add(idtoList);
				returnList.add(vouList);
			}
		}
	}

	private Map tranfor(TvVoucherinfoDto vDto, PayreckCountDto paydto,
			String danhao, List<IDto> idtoList) throws ITFEBizException {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			if(paydto.getPayamt()==null){
				paydto.setPayamt(BigDecimal.valueOf(Double.valueOf("0.00")));
			}
			if(paydto.getAmt()==null){
				paydto.setAmt(BigDecimal.valueOf(Double.valueOf("0.00")));
			}
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体
			vouchermap.put("Id", vDto.getSvoucherno());// 行政区划代码
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());// 行政区划代码
			vouchermap.put("StYear", vDto.getScreatdate().substring(0, 4));// 业务年度
			vouchermap.put("VtCode", vDto.getSvtcode());// 凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());// 凭证日期
			vouchermap.put("VoucherNo", vDto.getSvoucherno());// 凭证号
			vouchermap.put("TreCode", vDto.getStrecode());// 国库主体代码
			TsTreasuryDto dto = SrvCacheFacade.cacheTreasuryInfo(
					vDto.getSorgcode()).get(vDto.getStrecode());
			vouchermap.put("ClearBankCode", "001001");// 人民银行编码
			vouchermap.put("ClearBankName", dto.getStrename());// 人民银行名称

			vouchermap.put("ClearAccNo", getString(paydto.getClearaccno()));// 清算账号
			vouchermap.put("ClearAccNanme", getString(paydto.getClearaccname()));// 清算账户名称

			vouchermap.put("FundTypeCode", getString(paydto.getFundtypecode()));// 资金性质编码
			if (paydto.getFundtypecode().equals("1")) {
				vouchermap.put("FundTypeName", getString("预算管理资金"));// 资金性质名称
			} else if (paydto.getFundtypecode().equals("2")) {
				vouchermap.put("FundTypeName", getString("财政专户管理资金"));// 资金性质名称
			} else if (paydto.getFundtypecode().equals("9")) {
				vouchermap.put("FundTypeName", getString("其他资金"));// 资金性质名称
			}
			// vouchermap.put("FundTypeName",
			// getString(paydto.getFundtypename()));// 资金性质名称
			vouchermap.put("PayTypeCode", getString(paydto.getPaytypecode()));// 支付方式编码
			if(paydto.getPaytypecode().equals(StateConstant.GRANT_PAY_CODE)){
				vouchermap.put("PayTypeName", "授权支付");// 支付方式名称
			}else{
				vouchermap.put("PayTypeName", "直接支付");// 支付方式名称
			}
			
			vouchermap.put("BeginDate", getString(vDto.getShold3()));// 对账起始日期
			vouchermap.put("EndDate", getString(vDto.getShold4()));// 对账终止日期
			vouchermap.put("XCheckResult", " ");// 对账结果
			vouchermap.put("XCheckReason", " ");// 不符原因
			vouchermap.put("XAcctDate", " ");// 对账日期
			vouchermap.put("Hold1", " ");// 预留字段1
			vouchermap.put("Hold2", " ");// 预留字段2

			vouchermap.put("AllAmt", getString(paydto.getPayamt().toString()));// 预留字段2
			List<Object> Detail = new ArrayList<Object>();
			int id = 0;
			List subdtolist = new ArrayList();
			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			Detailmap.put("Id", vDto.getSvoucherno() + id);// 序号
			Detailmap.put("VoucherBillId", getString(vDto.getSvoucherno()));// 对账单Id
			Detailmap.put("VoucherBillNo", getString(vDto.getSvoucherno()));// 对账凭证单号
			
			Detailmap.put("CurReckMoney", getString(paydto.getPayamt()
					.toString()));// 国库本月清算额
			Detailmap.put("CurDateMoney", getString((paydto.getAmt().subtract(
					paydto.getPayamt()).toString())));// 国库本月额度余额
			Detailmap.put("XCurReckMoney", getString(""));// 财政机关本月清算额
			Detailmap.put("XCurDateMoney", getString(""));// 财政机关本页额度余额
			Detailmap.put("Hold1", getString(" "));// 预留字段1
			Detailmap.put("Hold2", getString(" "));// 预留字段2
			Detailmap.put("Hold3", getString(" "));// 预留字段3
			Detailmap.put("Hold4", getString(" "));// 预留字段4
			Detail.add(Detailmap);
			subdtolist.add(getSubDto(Detailmap, vouchermap));
			idtoList.add(getMainDto(vouchermap, vDto));
			idtoList.addAll(subdtolist);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

	private TfReconcilePayinfoMainDto getMainDto(Map<String, Object> mainMap,
			TvVoucherinfoDto vDto) {
		TfReconcilePayinfoMainDto mainDto = new TfReconcilePayinfoMainDto();
		mainDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		mainDto.setSdemo("处理成功");
		mainDto.setSorgcode(vDto.getSorgcode());
		String voucherno = getString(mainMap, "VoucherNo");
		if (voucherno.length() > 19)
			voucherno = voucherno.substring(voucherno.length() - 19);
		mainDto.setIvousrlno(Long.valueOf(voucherno));
		mainDto.setSadmdivcode(getString(mainMap, "AdmDivCode"));// AdmDivCode",vDto.getSadmdivcode());//行政区划代码
		mainDto.setSstyear(getString(mainMap, "StYear"));// StYear",vDto.getScreatdate().substring(0,4));//业务年度
		mainDto.setSvtcode(getString(mainMap, "VtCode"));// VtCode",vDto.getSvtcode());//凭证类型编号
		mainDto.setSvoudate(getString(mainMap, "VouDate"));// VouDate",vDto.getScreatdate());//凭证日期
		mainDto.setSvoucherno(getString(mainMap, "VoucherNo"));// VoucherNo",vDto.getSvoucherno());//凭证号
		mainDto.setSvouchercheckno(getString(mainMap, "VoucherNo"));//
		// VoucherCheckNo",danhao);//对账单号
		mainDto.setSchildpacknum("1");//
		// ChildPackNum",count);//子包总数
		mainDto.setScurpackno("1");//
		// CurPackNo",xuhao);//本包序号
		mainDto.setStrecode(getString(mainMap, "TreCode"));// TreCode",vDto.getStrecode());//国库主体代码
		mainDto.setSclearaccno(getString(mainMap, "ClearAccNo"));// 清算账号
		mainDto.setSclearaccnanme(getString(mainMap, "ClearAccNanme"));// 清算账户名称
		mainDto.setSclearbankcode(getString(mainMap, "ClearBankCode"));// 人民银行编码
		mainDto.setSclearbankname(getString(mainMap, "ClearBankName"));// 人民银行名称
		mainDto.setSbegindate(getString(mainMap, "BeginDate"));// BeginDate",vDto.getScheckdate());//对账起始日期
		mainDto.setSenddate(getString(mainMap, "EndDate"));// EndDate",vDto.getSpaybankcode());//对账终止日期
		mainDto.setSallnum("1");//
		// AllNum",detailLis1t.size());//总笔数
		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,
				"AllAmt")));// AllAmt","");//总金额
		mainDto.setShold1(getString(mainMap, "Hold1"));// Hold1","");//预留字段1
		mainDto.setShold2(getString(mainMap, "Hold2"));// Hold2","");//预留字段2
		mainDto.setSext1("1");// 发起方式1:人行发起,2:财政发起,3:商行发起
		return mainDto;
	}

	private TfReconcilePayinfoSubDto getSubDto(HashMap<String, Object> subMap,
			HashMap<String, Object> mainMap) {
		TfReconcilePayinfoSubDto subDto = new TfReconcilePayinfoSubDto();
		String voucherno = getString(mainMap, "VoucherNo");
		if (voucherno.length() > 19)
			voucherno = voucherno.substring(voucherno.length() - 19);
		subDto.setIvousrlno(Long.valueOf(voucherno));
		String id = getString(subMap, "Id");
		if (id.length() > 19)
			id = id.substring(id.length() - 19);
		subDto.setIseqno(Long.valueOf(id));// Id",vDto.getSdealno()+(++id));//序号
		subDto.setSid(getString(subMap, "Id"));
		subDto.setSsupdepcode(" ");// 预算单位编码
		subDto.setSsupdepname(" ");// 一级预算单位名称
		subDto.setSfundtypecode(getString(mainMap, "FundTypeCode"));// 资金性质编码
		subDto.setSfundtypename(getString(mainMap, "FundTypeName"));// 资金性质名称
		// subDto.setSpaybankcode(getString(subMap, "PayBankCode"));// 代理银行编码
		// subDto.setSpaybankname(getString(subMap, "PayBankName"));// 代理银行名称
		subDto.setSpaybankno(" ");// 代理银行行号
		subDto.setSexpfunccode(" ");//
//		 支出功能分类科目编码
		subDto.setSexpfuncname(" ");//
		// 支出功能分类科目名称
		// subDto.setSprocatcode(getString(subMap, "ProCatCode"));// 收支管理编码
		// subDto.setSprocatname(getString(subMap, "ProCatName"));// 收支管理名称
		subDto.setSpaytypecode(getString(mainMap, "PayTypeCode"));// 支付方式编码
		subDto.setSpaytypename(getString(mainMap, "PayTypeName"));// 支付方式名称
		subDto.setNpayamt(MtoCodeTrans.transformBigDecimal(getString(subMap,
				"CurReckMoney")));// 支付金额
		subDto.setSxcheckresult("0");// 对账结果默认成功
		subDto.setShold1(getString(subMap, "Hold1"));// 预留字段1
		subDto.setShold2(getString(subMap, "Hold2"));// 预留字段2
		subDto.setShold3(getString(subMap, "Hold3"));// 预留字段3
		subDto.setShold4(getString(subMap, "Hold4"));// 预留字段4
		subDto.setSext1(getString(subMap, "CurDateMoney"));// 预留字段4
		return subDto;
	}

	private String getString(Map datamap, String key) {
		if (datamap == null || key == null)
			return "";
		else
			return String.valueOf(datamap.get(key));
	}

	private List getSubLists(List list, int subsize) {
		List getList = null;
		if (list != null && list.size() > 0) {
			if (subsize < 1)
				subsize = 500;
			int count = list.size() / subsize;
			int yu = list.size() % subsize;
			getList = new ArrayList();
			for (int i = 0; i < count; i++)
				getList.add(list.subList(i * subsize, subsize * (i + 1)));
			if (yu > 0)
				getList.add(list.subList(count * subsize, (count * subsize)
						+ yu));
		}
		return getList;
	}

	public Map tranfor(List list) throws ITFEBizException {
		Map map = new HashMap<String, PayreckCountDto>();
		for (PayreckCountDto dto : (List<PayreckCountDto>) list) {
			if (map.get(dto.getFundtypecode() + "-" + dto.getPaytypecode()) == null) {
				if(dto.getAmt()==null){
					dto.setAmt(BigDecimal.valueOf(Double.valueOf("0.00")));
				}
				if(dto.getPayamt()==null){
					dto.setPayamt(BigDecimal.valueOf(Double.valueOf("0.00")));
				}
				map
						.put(
								dto.getFundtypecode() + "-"
										+ dto.getPaytypecode(), dto);
			}
		}
		return map;
	}

	private Map<String, PayreckCountDto> getDataList(TvVoucherinfoDto vDto,
			SQLExecutor execDetail) throws ITFEBizException {
		Map<String, PayreckCountDto> map = new HashMap<String, PayreckCountDto>();
		StringBuffer sql = null;
		try {
			List<PayreckCountDto> list = new ArrayList<PayreckCountDto>();
			if (execDetail == null) {
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory()
						.getSQLExecutor();
			} else {
				execDetail.clearParams();
			}
			// 2301查询商行划款已经回单的数据
			sql = new StringBuffer(
					"SELECT S_BOOKORGCODE AS orgcode,S_TRECODE AS trecode,S_PAYERACCT AS clearaccno,S_PAYERNAME AS clearaccname,"
							+ "S_FUNDTYPECODE AS fundtypecode,S_PAYTYPECODE AS paytypecode,sum(F_AMT) AS payamt FROM TV_PAYRECK_BANK "
							+ "WHERE I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_TRECODE= ? AND S_VTCODE= ? AND S_STATUS= ? "
							+ "AND S_CONFIRUSERCODE BETWEEN ? AND ? )");
			sql.append(vDto.getSext1() == null ? "" : " AND (S_PAYTYPECODE='"
					+ vDto.getSext1() + "' OR S_PAYMODE='"+ vDto.getSext1() + "')");
			sql.append(vDto.getSext2() == null ? "" : " AND S_FUNDTYPECODE='"
					+ vDto.getSext2() + "' ");
			sql
					.append(" GROUP BY S_BOOKORGCODE,S_TRECODE,S_PAYERACCT,S_PAYERNAME,S_FUNDTYPECODE,S_PAYTYPECODE");

			addparam(MsgConstant.VOUCHER_NO_2301, execDetail, vDto);

			list = (List<PayreckCountDto>) execDetail.runQuery(sql.toString(),
					PayreckCountDto.class).getDtoCollection();// 划款正式表
			if(list!=null&&list.size()>0){
				map = tranfor(list);
			}
			

			addparam(MsgConstant.VOUCHER_NO_2301, execDetail, vDto);
			List<PayreckCountDto> listh = (List<PayreckCountDto>) execDetail
					.runQuery(
							StringUtil.replace(StringUtil.replace(sql
									.toString(), "TV_VOUCHERINFO",
									"HTV_VOUCHERINFO"), "TV_PAYRECK_BANK",
									"HTV_PAYRECK_BANK"), PayreckCountDto.class)
					.getDtoCollection();// 划款历史表
			if (listh != null && listh.size() > 0) {
				for (PayreckCountDto dtoh : listh) {
					if (map.get(dtoh.getFundtypecode() + "-"
							+ dtoh.getPaytypecode()) != null) {
						PayreckCountDto mapdto = (PayreckCountDto) map.get(dtoh
								.getFundtypecode()
								+ "-" + dtoh.getPaytypecode());
						mapdto.setPayamt(mapdto.getPayamt().add(
								dtoh.getPayamt()));
						map.put(dtoh.getFundtypecode() + "-"
								+ dtoh.getPaytypecode(), mapdto);
						// ((PayreckCountDto)map.get(dtoh.getFundtypecode()+"-"+dtoh.getPaytypecode())).setPayamt(((PayreckCountDto)map.get(dtoh.getFundtypecode()+"-"+dtoh.getPaytypecode())).getPayamt().add(dtoh.getPayamt()));
					} else {
						map.put(dtoh.getFundtypecode() + "-"
								+ dtoh.getPaytypecode(), dtoh);
					}
				}
			}

			addparam(MsgConstant.VOUCHER_NO_2302, execDetail, vDto);
			List<PayreckCountDto> listback = (List<PayreckCountDto>) execDetail
					.runQuery(
							StringUtil.replace(sql.toString(),
									"TV_PAYRECK_BANK", "TV_PAYRECK_BANK_BACK"),
							PayreckCountDto.class).getDtoCollection();// 退款正式表
			if (listback != null && listback.size() > 0) {
				for (PayreckCountDto dtob : listback) {
					if (map.get(dtob.getFundtypecode() + "-"
							+ dtob.getPaytypecode()) != null) {
						PayreckCountDto mapdto = (PayreckCountDto) map.get(dtob
								.getFundtypecode()
								+ "-" + dtob.getPaytypecode());
						mapdto.setPayamt(mapdto.getPayamt().add(
								dtob.getPayamt()));
						map.put(dtob.getFundtypecode() + "-"
								+ dtob.getPaytypecode(), mapdto);
						// ((PayreckCountDto)map.get(dtob.getFundtypecode()+"-"+dtob.getPaytypecode())).setPayamt(((PayreckCountDto)map.get(dtob.getFundtypecode()+"-"+dtob.getPaytypecode())).getPayamt().subtract(dtob.getPayamt()));
					} else {
						map.put(dtob.getFundtypecode() + "-"
								+ dtob.getPaytypecode(), dtob);
					}
				}
			}

			addparam(MsgConstant.VOUCHER_NO_2302, execDetail, vDto);
			List<PayreckCountDto> listbackh = (List<PayreckCountDto>) execDetail
					.runQuery(
							StringUtil.replace(StringUtil.replace(sql
									.toString(), "TV_VOUCHERINFO",
									"HTV_VOUCHERINFO"), "TV_PAYRECK_BANK",
									"HTV_PAYRECK_BANK_BACK"),
							PayreckCountDto.class).getDtoCollection();// 退款历史表
			if (listbackh != null && listbackh.size() > 0) {
				for (PayreckCountDto dtobh : listbackh) {
					if (map.get(dtobh.getFundtypecode() + "-"
							+ dtobh.getPaytypecode()) != null) {
						PayreckCountDto mapdto = (PayreckCountDto) map
								.get(dtobh.getFundtypecode() + "-"
										+ dtobh.getPaytypecode());
						mapdto.setPayamt(mapdto.getPayamt().add(
								dtobh.getPayamt()));
						map.put(dtobh.getFundtypecode() + "-"
								+ dtobh.getPaytypecode(), mapdto);
						// ((PayreckCountDto)map.get(dtobh.getFundtypecode()+"-"+dtobh.getPaytypecode())).setPayamt(((PayreckCountDto)map.get(dtobh.getFundtypecode()+"-"+dtobh.getPaytypecode())).getPayamt().subtract(dtobh.getPayamt()));
					} else {
						map.put(dtobh.getFundtypecode() + "-"
								+ dtobh.getPaytypecode(), dtobh);
					}
				}
			}
			
			// 授权支付额度
			if (vDto.getSext1() == null
					|| vDto.getSext1().equals(MsgConstant.grantPay)) {
				sql = new StringBuffer(
						"SELECT S_ORGCODE AS orgcode,S_TRECODE AS trecode,S_FUNDTYPECODE AS fundtypecode,sum(N_MONEY) AS amt FROM "
								+ "TV_GRANTPAYMSGMAIN WHERE I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_TRECODE=? "
								+ "AND S_VTCODE=? AND S_STATUS=? AND S_CONFIRUSERCODE BETWEEN ? AND ? ) ");
				sql.append(vDto.getSext2() == null ? ""
						: " AND S_FUNDTYPECODE='" + vDto.getSext2() + "' ");
				sql.append(" GROUP BY S_ORGCODE,S_TRECODE,S_FUNDTYPECODE");
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory()
						.getSQLExecutor();
				addparam(MsgConstant.VOUCHER_NO_5106, execDetail, vDto);

				List<PayreckCountDto> listgrant = (List<PayreckCountDto>) execDetail
						.runQuery(sql.toString(), PayreckCountDto.class)
						.getDtoCollection();// 正式表
				if(listgrant!=null&&listgrant.size()>0){
					for (PayreckCountDto dtogrant : listgrant) {
						dtogrant.setPaytypecode(StateConstant.GRANT_PAY_CODE);
						if (map.get(dtogrant.getFundtypecode() + "-"
								+ StateConstant.GRANT_PAY_CODE) != null) {
							PayreckCountDto mapdto = ((PayreckCountDto) map
									.get(dtogrant.getFundtypecode() + "-"
											+ StateConstant.GRANT_PAY_CODE));
							mapdto.setPaytypename("授权支付");
							BigDecimal amt = mapdto.getAmt();
							if (amt != null) {
								amt = amt.add(dtogrant.getAmt());
							} else {
								amt = dtogrant.getAmt();
							}
							mapdto.setAmt(amt);
							map.put(dtogrant.getFundtypecode() + "-"
									+ StateConstant.GRANT_PAY_CODE, mapdto);
						} else {
							map.put(dtogrant.getFundtypecode() + "-"
									+ StateConstant.GRANT_PAY_CODE, dtogrant);
						}
					}
				}
				
				addparam(MsgConstant.VOUCHER_NO_5106, execDetail, vDto);
				List<PayreckCountDto> listgranth = (List<PayreckCountDto>) execDetail
						.runQuery(
								StringUtil.replace(StringUtil.replace(sql
										.toString(), "TV_VOUCHERINFO",
										"HTV_VOUCHERINFO"),
										"TV_GRANTPAYMSGMAIN",
										"HTV_GRANTPAYMSGMAIN"),
								PayreckCountDto.class).getDtoCollection();// 正式表
				if(listgranth!=null&&listgranth.size()>0){
					for (PayreckCountDto dtogranth : listgranth) {
						dtogranth.setPaytypecode(StateConstant.GRANT_PAY_CODE);
						if (map.get(dtogranth.getFundtypecode() + "-"
								+ StateConstant.GRANT_PAY_CODE) != null) {
							PayreckCountDto mapdto = ((PayreckCountDto) map
									.get(dtogranth.getFundtypecode() + "-"
											+ StateConstant.GRANT_PAY_CODE));
							mapdto.setPaytypename("授权支付");
							BigDecimal amt = mapdto.getAmt();
							if (amt != null) {
								amt = amt.add(dtogranth.getAmt());
							} else {
								amt = dtogranth.getAmt();
							}
							mapdto.setAmt(amt);
							map.put(dtogranth.getFundtypecode() + "-"
									+ StateConstant.GRANT_PAY_CODE, mapdto);
						} else {
							map.put(dtogranth.getFundtypecode() + "-"
									+ StateConstant.GRANT_PAY_CODE, dtogranth);
						}
					}
				}
			}

			// 直接支付额度
			if (vDto.getSext1() == null
					|| vDto.getSext1().equals(MsgConstant.directPay)) {
				sql = new StringBuffer(
						"SELECT S_ORGCODE AS orgcode,S_TRECODE AS trecode,S_PAYACCTNO AS clearaccno,S_PAYACCTNAME AS clearaccname,"
								+ "S_FUNDTYPECODE AS fundtypecode,'11' AS paytypecode,sum(N_MONEY) AS amt FROM TV_DIRECTPAYMSGMAIN "
								+ "WHERE I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_TRECODE=? AND S_VTCODE=? AND "
								+ "S_STATUS=? AND S_CONFIRUSERCODE BETWEEN ? AND ? ) ");
				sql.append(vDto.getSext2() == null ? ""
						: " AND S_FUNDTYPECODE='" + vDto.getSext2() + "' ");
				sql
						.append(" GROUP BY S_ORGCODE,S_TRECODE,S_PAYACCTNO,S_PAYACCTNAME,S_FUNDTYPECODE");
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory()
						.getSQLExecutor();
				addparam(MsgConstant.VOUCHER_NO_5108, execDetail, vDto);

				List<PayreckCountDto> listdirect = (List<PayreckCountDto>) execDetail
						.runQuery(sql.toString(), PayreckCountDto.class)
						.getDtoCollection();// 正式表
				if(listdirect!=null&&listdirect.size()>0){
					for (PayreckCountDto dtodirect : listdirect) {
						dtodirect.setPaytypecode(StateConstant.DIRECT_PAY_CODE);
						if (map.get(dtodirect.getFundtypecode() + "-"
								+ StateConstant.DIRECT_PAY_CODE) != null) {
							PayreckCountDto mapdto = ((PayreckCountDto) map
									.get(dtodirect.getFundtypecode() + "-"
											+ StateConstant.DIRECT_PAY_CODE));
							mapdto.setPaytypename("直接支付");
							BigDecimal amt = mapdto.getAmt();
							if (amt != null) {
								amt = amt.add(dtodirect.getAmt());
							} else {
								amt = dtodirect.getAmt();
							}
							mapdto.setAmt(amt);
							map.put(dtodirect.getFundtypecode() + "-"
									+ StateConstant.DIRECT_PAY_CODE, mapdto);
						} else {
							map.put(dtodirect.getFundtypecode() + "-"
									+ StateConstant.DIRECT_PAY_CODE, dtodirect);
						}
					}
				}
				
				addparam(MsgConstant.VOUCHER_NO_5108, execDetail, vDto);
				List<PayreckCountDto> listdirecth = (List<PayreckCountDto>) execDetail
						.runQuery(
								StringUtil.replace(StringUtil.replace(sql
										.toString(), "TV_VOUCHERINFO",
										"HTV_VOUCHERINFO"),
										"TV_DIRECTPAYMSGMAIN",
										"HTV_DIRECTPAYMSGMAIN"),
								PayreckCountDto.class).getDtoCollection();// 正式表
				if(listdirecth!=null&&listdirecth.size()>0){
					for (PayreckCountDto dtodirecth : listdirecth) {
						dtodirecth.setPaytypecode(StateConstant.DIRECT_PAY_CODE);
						if (map.get(dtodirecth.getFundtypecode() + "-"
								+ StateConstant.DIRECT_PAY_CODE) != null) {
							PayreckCountDto mapdto = ((PayreckCountDto) map
									.get(dtodirecth.getFundtypecode() + "-"
											+ StateConstant.DIRECT_PAY_CODE));
							mapdto.setPaytypename("直接支付");
							BigDecimal amt = mapdto.getAmt();
							if (amt != null) {
								amt = amt.add(dtodirecth.getAmt());
							} else {
								amt = dtodirecth.getAmt();
							}
							mapdto.setAmt(amt);
							map.put(dtodirecth.getFundtypecode() + "-"
									+ StateConstant.DIRECT_PAY_CODE, mapdto);
						} else {
							map.put(dtodirecth.getFundtypecode() + "-"
									+ StateConstant.DIRECT_PAY_CODE, dtodirecth);
						}
					}
				}
			}
		} catch (Exception e) {
			if (execDetail != null)
				execDetail.closeConnection();
			logger.error(e.getMessage(), e);
			throw new ITFEBizException("查询" + sql == null ? "" : sql.toString()
					+ "库存明细信息异常！", e);
		} finally {
			if (execDetail != null)
				execDetail.closeConnection();
		}
		return map;
	}

	public Map<String, MainAndSubDto> get5201Map(String mainsql5201,
			String subsql5201, SQLExecutor execDetail)
			throws JAFDatabaseException {
		Map<String, MainAndSubDto> getMap = null;
		if (mainsql5201 != null && subsql5201 != null) {
			List list5201 = (List<IDto>) execDetail.runQuery(mainsql5201,
					TfDirectpaymsgmainDto.class).getDtoCollection();
			if (list5201 != null && list5201.size() > 0) {
				getMap = new HashMap<String, MainAndSubDto>();
				List<IDto> subList = (List<IDto>) execDetail.runQuery(
						subsql5201, TfDirectpaymsgsubDto.class)
						.getDtoCollection();
				Map<String, List<IDto>> subMap = new HashMap<String, List<IDto>>();
				if (subList != null && subList.size() > 0) {
					List<IDto> tempList = null;
					TfDirectpaymsgsubDto subdto = null;
					for (IDto tempdto : subList) {
						subdto = (TfDirectpaymsgsubDto) tempdto;
						tempList = subMap.get(String.valueOf(subdto
								.getIvousrlno()));
						if (tempList == null) {
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()),
									tempList);
						} else {
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()),
									tempList);
						}
					}
				}
				TfDirectpaymsgmainDto tempdto = null;
				MainAndSubDto datadto = null;
				for (int i = 0; i < list5201.size(); i++) {
					tempdto = (TfDirectpaymsgmainDto) list5201.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto
							.getIvousrlno())));
					getMap.put(String.valueOf(tempdto.getIvousrlno()), datadto);
				}
			}
		}
		return getMap;
	}

	private String getString(String key) {
		if (key == null)
			key = " ";
		return key;
	}

	/**
	 * 将查询结果按资金类型分包
	 * 
	 * @param alldata
	 * @return
	 */
	private Map<String, List> getFundTypeMap(List<IDto> alldata) {
		Map<String, List> map = new HashMap<String, List>();
		for (IDto msdto : alldata) {
			MainAndSubDto dto = (MainAndSubDto) msdto;
			if (dto.getMainDto() instanceof TvPayreckBankDto) {
				if (map.get(((TvPayreckBankDto) dto.getMainDto())
						.getSfundtypecode()) == null) {
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(((TvPayreckBankDto) dto.getMainDto())
							.getSfundtypecode(), data);
				} else {
					List<IDto> data = map.get(((TvPayreckBankDto) dto
							.getMainDto()).getSfundtypecode());
					data.add(dto);
				}
			} else if (dto.getMainDto() instanceof TvPayreckBankBackDto) {
				if (map.get(((TvPayreckBankBackDto) dto.getMainDto())
						.getSfundtypecode()) == null) {
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(((TvPayreckBankBackDto) dto.getMainDto())
							.getSfundtypecode(), data);
				} else {
					List<IDto> data = map.get(((TvPayreckBankBackDto) dto
							.getMainDto()).getSfundtypecode());
					data.add(dto);
				}
			}

		}
		return map;
	}

	private void addparam(String vtcode, SQLExecutor execDetail,
			TvVoucherinfoDto vDto) {
		execDetail.clearParams();
		execDetail.addParam(vDto.getStrecode());
		execDetail.addParam(vtcode);
		execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
		execDetail.addParam(vDto.getShold3());
		execDetail.addParam(vDto.getShold4());
	}
}

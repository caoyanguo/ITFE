package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.Report3553LKXMUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * ֧������(3533)ת��
 * 
 * @author renqingbin
 * @time 2013-08-16
 * 
 */
public class VoucherDto2MapFor3553 {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3553.class);

	/**
	 * DTOת��XML����
	 * 
	 * @param List
	 *            ���ɱ���Ҫ�ؼ���
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(List<TrTaxorgPayoutReportDto> list,
			TvVoucherinfoDto vDto) throws ITFEBizException {
		try {
			HashMap<String, Object> returnmap = new HashMap<String, Object>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			// ȡ�ù�������
			String treName = BusinessFacade
					.findTreasuryInfo(vDto.getSorgcode()).get(
							vDto.getStrecode()) == null ? "" : BusinessFacade
					.findTreasuryInfo(vDto.getSorgcode()).get(
							vDto.getStrecode()).getStrename();
			// ���ñ��Ľڵ� Voucher
			map.put("Voucher", vouchermap);
			// ���ñ�����Ϣ��
			vouchermap.put("Id", vDto.getSdealno());
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());
			vouchermap.put("StYear", vDto.getSstyear());
			vouchermap.put("VtCode", vDto.getSvtcode());
			vouchermap.put("VouDate", vDto.getScreatdate());
			vouchermap.put("VoucherNo", vDto.getSvoucherno());
			vouchermap.put("BillKind", vDto.getScheckvouchertype());// ��������
			vouchermap.put("ReportDate", vDto.getScheckdate());
			vouchermap.put("FinOrgCode", vDto.getSpaybankcode());
			vouchermap.put("TreCode", vDto.getStrecode());
			vouchermap.put("TreName", treName);
			vouchermap.put("BgtTypeCode", vDto.getShold1());
			if (vDto.getShold1().equals(MsgConstant.BDG_KIND_IN)) {
				vouchermap.put("BgtTypeName", "Ԥ����");
			} else {
				vouchermap.put("BgtTypeName", "Ԥ����");
			}
			vouchermap.put("BelongFlag", vDto.getShold3());// Ͻ����־
			vouchermap.put("TrimFlag", vDto.getShold2());// �����ڱ�־
			vouchermap.put("PayType", vDto.getShold4());// ֧������
			vouchermap.put("Hold1", "");
			vouchermap.put("Hold2", "");
			BigDecimal allamt = new BigDecimal("0.00");
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail = new ArrayList<Object>();
			list = new Report3553LKXMUtil().computePayLKM(vDto.getSorgcode(), list, "1");
			for (TrTaxorgPayoutReportDto dto : list) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
//				if (dto.getSbudgetsubcode().contains("heji")) {
//					Detailmap.put("ExpFuncCode", "------");
//				} else {
				Detailmap.put("ExpFuncCode", dto.getSbudgetsubcode()); // ��������
//				}
				Detailmap.put("ExpFuncName", dto.getSbudgetsubname());
				Detailmap.put("DayAmt", MtoCodeTrans.transformString(dto
						.getNmoneyday()));
				Detailmap.put("TenDayAmt", MtoCodeTrans.transformString(dto
						.getNmoneytenday()));
				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(dto
						.getNmoneymonth()));
				Detailmap.put("QuarterAmt", MtoCodeTrans.transformString(dto
						.getNmoneyquarter()));
				Detailmap.put("YearAmt", MtoCodeTrans.transformString(dto
						.getNmoneyyear()));
				Detailmap.put("Hold1", "");
				Detailmap.put("Hold2", "");
				Detailmap.put("Hold3", "");
				Detailmap.put("Hold4", "");
				allamt = allamt.add(dto.getSbudgetsubcode().length() == 3 ? dto
						.getNmoneyday() : new BigDecimal("0.00"));
				Detail.add(Detailmap);
			}
			vouchermap.put("SumMoney", MtoCodeTrans.transformString(allamt));
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail", Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			returnmap.put("SumMoney", allamt);
			returnmap.put("Map", map);
			return returnmap;
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage(), e);
		}

	}
}

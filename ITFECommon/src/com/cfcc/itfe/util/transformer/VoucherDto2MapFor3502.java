package com.cfcc.itfe.util.transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;

/**
 * 和商行每日对账(3502)转化
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
public class VoucherDto2MapFor3502 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3502.class);
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
		return null;
	}
	
	/**
	 * DTO转化XML报文
	 * 
	 * @param List
	 *            	生成报文要素集合
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public Map tranfor(List lists) throws ITFEBizException{
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> vouchermap = new HashMap<String, Object>();
		TvVoucherinfoDto dto=(TvVoucherinfoDto) lists.get(0);
		map.put("Voucher", vouchermap);
		vouchermap.put("AdmDivCode", dto.getSadmdivcode());
		vouchermap.put("StYear", dto.getSstyear());
		vouchermap.put("VtCode", dto.getSvtcode());
		vouchermap.put("VouDate", dto.getScreatdate());
		vouchermap.put("VoucherNo", dto.getSvoucherno());
		vouchermap.put("TreCode", dto.getStrecode());
		vouchermap.put("PayBankCode", dto.getSdemo());
		vouchermap.put("CheckDate", dto.getSverifyusercode());
		vouchermap.put("EVoucherType", dto.getSattach());
		vouchermap.put("AllNum", dto.getIcount());
		vouchermap.put("AllAmt", dto.getNmoney());
		vouchermap.put("Hold1", "");
		vouchermap.put("Hold2", "");
		vouchermap.put("Hold3", "");
		vouchermap.put("Hold4", "");
		List<Object> DetailList = new ArrayList<Object>();
		List<Object> Detail= new ArrayList<Object>();
		List<TvVoucherinfoDto> detailDtos=(List<TvVoucherinfoDto>) lists.get(1);
		for (TvVoucherinfoDto tvVoucherinfoDto : detailDtos) {
			Map<String, Object> detailmap = new HashMap<String, Object>();
			detailmap.put("Id", tvVoucherinfoDto.getSdealno());
			detailmap.put("VoucherNo", tvVoucherinfoDto.getSvoucherno());
			detailmap.put("Amt", tvVoucherinfoDto.getNmoney());
			//根据凭证库凭证的处理状态确定明细处理状态(0-正常[回单成功-80]  1-退回[退回成功-90])
			String status="";
			if(tvVoucherinfoDto.getShold1().equals(DealCodeConstants.VOUCHER_SUCCESS)){
				status=StateConstant.VOUCHE_PROSTATE_NORMAL;
			}else if(tvVoucherinfoDto.getShold1().equals(DealCodeConstants.VOUCHER_FAIL)){
				status=StateConstant.VOUCHE_PROSTATE_RETURN;
			}
			detailmap.put("ProState", status);
			detailmap.put("Hold1", "");
			detailmap.put("Hold2", "");
			Detail.add(detailmap);
		}
		HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
		DetailListmap.put("Detail",Detail);
		DetailList.add(DetailListmap);
		vouchermap.put("DetailList", DetailList);
		return map;
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		Map map = new HashMap();
		Map vouchermap = new HashMap();
		map.put("Voucher", vouchermap);
		vouchermap.put("AdmDivCode", dto.getSadmdivcode());
		vouchermap.put("StYear", dto.getSstyear());
		vouchermap.put("VtCode", dto.getSvtcode());
		vouchermap.put("VouDate", dto.getScreatdate());
		vouchermap.put("VoucherNo", dto.getSvoucherno());
		vouchermap.put("TreCode", dto.getStrecode());
		vouchermap.put("PayBankCode", dto.getSdemo());
		vouchermap.put("CheckDate", dto.getSverifyusercode());
		vouchermap.put("EVoucherType", dto.getSattach());
		vouchermap.put("AllNum", dto.getIcount());
		vouchermap.put("AllAmt", dto.getNmoney());
		vouchermap.put("Hold1", "");
		vouchermap.put("Hold2", "");
		vouchermap.put("Hold3", "");
		vouchermap.put("Hold4", "");
		return map;
	}
}

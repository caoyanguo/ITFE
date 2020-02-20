package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 收入退付对账（3506）
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
public class VoucherDto2MapFor3506 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3401.class);
	
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
		BigDecimal Total=new BigDecimal("0.00");
		TvVoucherinfoDto dto=new TvVoucherinfoDto();		
		dto.setSvtcode(vDto.getSvtcode());
		dto.setScreatdate(TimeFacade.getCurrentStringTime());
		dto.setSstampid(vDto.getSstampid());
		dto.setSstampuser(vDto.getSstampuser());
		vDto.setSvtcode(MsgConstant.VOUCHER_NO_5209);	
		vDto.setSfilename(null);		
		String sqlWhere=" AND ( S_CREATDATE BETWEEN '"+vDto.getShold3()+"' AND '"+vDto.getShold4()+"') ";
		dto.setShold3(vDto.getShold3());
		dto.setShold4(vDto.getShold4());
		vDto.setScreatdate(null);
		vDto.setShold3(null);
		vDto.setShold4(null);
		List<TvVoucherinfoDto> list=null;
		try {
			list=CommonFacade.getODB().findRsByDtoForWhere(vDto, sqlWhere);						
		} catch (JAFDatabaseException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		}catch(Exception e2 ){
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		}
		if(list==null||list.size()==0){
			return null;
		}
		for(TvVoucherinfoDto vouDto:list){
			Total=Total.add(vouDto.getNmoney());
		}
		String mainvou=VoucherUtil.getGrantSequence();
		dto.setSdealno(mainvou);
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));
		dto.setSvoucherno(mainvou);		
		dto.setSorgcode(vDto.getSorgcode());
		dto.setStrecode(vDto.getStrecode());
		dto.setSadmdivcode(vDto.getSadmdivcode() );
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));				
		dto.setSattach("");		
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("处理成功");				
		dto.setSvoucherflag("1");		
		dto.setNmoney(Total);
		dto.setShold2("");
		List lists=new ArrayList();
		lists.add(dto);
		Map map= tranfor(lists);
		List voucherList=new ArrayList();
		voucherList.add(map);
		voucherList.add(dto);
		List listss=new ArrayList();
		listss.add(voucherList);
		return listss;
	}
	
	/**
	 * DTO转化XML报文
	 * 
	 * @param List
	 *            	生成报文要素集合
	 * @return
	 * @throws ITFEBizException
	 */
	public Map tranfor(List lists) throws ITFEBizException{
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(0);			
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 			
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());
			vouchermap.put("StYear", vDto.getSstyear());		
			vouchermap.put("VtCode", vDto.getSvtcode());		
			vouchermap.put("VouDate", vDto.getScreatdate());		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());
			vouchermap.put("VoucherCheckNo", vDto.getSdealno());
			vouchermap.put("ChildPackNum", "1");
			vouchermap.put("CurPackNo", "1");
			vouchermap.put("TreCode", vDto.getStrecode());		
			vouchermap.put("ClearBankCode", "");
			vouchermap.put("ClearBankName", "");
			vouchermap.put("ClearAccNo", "");
			vouchermap.put("ClearAccNanme", "");
			vouchermap.put("BeginDate", vDto.getShold3());
			vouchermap.put("EndDate", vDto.getShold4());			
			vouchermap.put("AllAmt", MtoCodeTrans.transformString(vDto.getNmoney()));
			vouchermap.put("XCheckResult", ""); 	
			vouchermap.put("XCheckReason", "");
			vouchermap.put("Hold1", "");		
			vouchermap.put("Hold2", "");	
				
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail= new ArrayList<Object>();

			HashMap<String, Object> Detailmap = new HashMap<String, Object>();
			Detailmap.put("Id", vDto.getSdealno());
			Detailmap.put("FundTypeCode", ""); 
			Detailmap.put("FundTypeName", ""); 
			Detailmap.put("PayeeAcctNo",""); 
			Detailmap.put("PayeeAcctName", ""); 
			Detailmap.put("PayeeAcctBankName", ""); 
			Detailmap.put("PayAcctNo", ""); 
			Detailmap.put("PayAcctName", ""); 
			Detailmap.put("PayAcctBankName", ""); 
			Detailmap.put("AgencyCode", ""); 
			Detailmap.put("AgencyName", ""); 
			Detailmap.put("IncomeSortCode", ""); 
			Detailmap.put("IncomeSortName", "");  				
			Detailmap.put("PayAmt", MtoCodeTrans.transformString(vDto.getNmoney()));
			Detailmap.put("XCheckResult", ""); 	
			Detailmap.put("XCheckReason", "");
			Detailmap.put("Hold1", ""); 
			Detailmap.put("Hold2", ""); 
			Detailmap.put("Hold3", ""); 
			Detailmap.put("Hold4", ""); 							
			Detail.add(Detailmap);									
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！",e);
		}
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}
	

}

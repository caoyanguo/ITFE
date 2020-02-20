package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 总额分成日报表（下级汇总、全辖）（3454）
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3454 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3454.class);
	private  BigDecimal Total;
	
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
		Total=new BigDecimal("0.00");
//		vDto.setScheckdate(TimeFacade.getEndDateOfMonth(vDto.getScheckdate()));
		vDto.setSdealno(VoucherUtil.getGrantSequence());
		//vDto.svtcode报表类型
		//vDto.shold1预算种类
		//vDto.shold2调整期标志
		//vDto.shold3报表范围
		//vDto.shold4科目代码
		//vDto.strecode国库主体代码
		//vDto.scheckdate报表日期
		//vDto.sadmdivcode 区划代码
		//vDto.getSattach辖属标志
		TrIncomedayrptDto dto=new TrIncomedayrptDto();
		dto.setStrecode(vDto.getStrecode());//国库代码
		dto.setStaxorgcode(vDto.getSpaybankcode());//征收机关代码
		dto.setSbudgetsubcode(vDto.getShold4());//预算科目代码
		dto.setSrptdate(vDto.getScheckdate());//报表日期
		dto.setSbelongflag(vDto.getSattach());//辖属标志
		dto.setStrimflag(vDto.getShold2());//调整期标志
		dto.setSbudgettype(vDto.getShold1());//预算种类
		if(MsgConstant.VOUCHER_NO_3454.equals(vDto.getSvtcode()))
			dto.setSbillkind("4");
		return tranforList(dto,vDto);
	}
	private List tranforList(TrIncomedayrptDto dto,TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List<TrIncomedayrptDto> list=null;
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		try {
			cDto.setSorgcode(vDto.getSorgcode());		
			cDto.setStrecode(vDto.getStrecode());
			List TsConvertfinorgDtoList=CommonFacade.getODB().findRsByDto(cDto);
			if(TsConvertfinorgDtoList==null||TsConvertfinorgDtoList.size()==0){
				throw new ITFEBizException("国库："+cDto.getStrecode()+" 对应的财政代码未维护！");
			}
			cDto=(TsConvertfinorgDto) TsConvertfinorgDtoList.get(0);
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){			
				throw new ITFEBizException("国库："+cDto.getStrecode()+" 对应的区划代码未维护！");
			}
			String where = "";
			if("0".equals(vDto.getShold3()))//不包含下级
				where = "S_FINORGCODE='"+cDto.getSfinorgcode()+"' and (S_TRECODE = '"+dto.getStrecode()+"') AND (S_RPTDATE = '"+dto.getSrptdate()+"' ) ";
			else
			{
				TsTreasuryDto tDto=new TsTreasuryDto();
				tDto.setSgoverntrecode(dto.getStrecode());
				List<TsTreasuryDto> trelist = CommonFacade.getODB().findRsByDto(tDto);
				StringBuffer orsql = new StringBuffer();
//				StringBuffer consql = new StringBuffer("");
				if(trelist!=null&&trelist.size()>0)
				{
					for(TsTreasuryDto tsDto:trelist)
					{
						if(!tsDto.getStrecode().equals(dto.getStrecode()))
							orsql.append(" or S_TRECODE = '"+tsDto.getStrecode()+"' ");
					}
//					List<TsConvertfinorgDto> conList = CommonFacade.getODB().findRsByWhereForDto(new TsConvertfinorgDto(), "(S_TRECODE='"+dto.getStrecode()+"' "+orsql.toString()+")");
//					if(conList!=null&&conList.size()>0&&trelist.size()==conList.size())
//					{
//						for(TsConvertfinorgDto condto:conList)
//						{
//							if(!cDto.getSfinorgcode().equals(condto.getSfinorgcode()))
//								consql.append(" or S_FINORGCODE='"+condto.getSfinorgcode()+"'");
//						}
//					}else
//					{
//						throw new ITFEBizException("本级国库或下级国库存在对应的财政代码未维护！");
//					}+consql.toString()
				}
				where = " (S_TRECODE = '"+dto.getStrecode()+"'"+orsql.toString()+") AND (S_RPTDATE = '"+dto.getSrptdate()+"' ) ";//"(S_FINORGCODE='"+cDto.getSfinorgcode()+"') OR
			}
			if(dto.getStaxorgcode()!=null)
				where+="AND (S_TAXORGCODE = '"+dto.getStaxorgcode()+"' ) ";
			dto.setStrecode(null);
			dto.setSrptdate(null);
			dto.setStaxorgcode(null);
			list = CommonFacade.getODB().findRsByWhereForDto(dto, where);
		} catch (JAFDatabaseException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		}catch(Exception e2 ){
			logger.error(e2);
			throw new ITFEBizException(e2.getMessage(),e2);
		}
		if(list==null||list.size()==0){
			return null;
		}
		String dirsep = File.separator;
		String mainvou=VoucherUtil.getGrantSequence();
		String FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ TimeFacade.getCurrentStringTime()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+ new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+ mainvou+".msg";
		TvVoucherinfoDto voucherdto=new TvVoucherinfoDto();
		voucherdto.setSdealno(mainvou);
		voucherdto.setSorgcode(vDto.getSorgcode());
		voucherdto.setSadmdivcode(vDto.getSadmdivcode());
		voucherdto.setSvtcode(vDto.getSvtcode());
		voucherdto.setScreatdate(TimeFacade.getCurrentStringTime());//凭证日期
		voucherdto.setScheckdate(vDto.getScheckdate());//报表日期
		voucherdto.setStrecode(vDto.getStrecode());
		voucherdto.setSadmdivcode(cDto.getSadmdivcode());
		voucherdto.setSstyear(voucherdto.getScreatdate().substring(0, 4));				
		voucherdto.setSattach(vDto.getSattach());//辖属标志
		voucherdto.setSpaybankcode(vDto.getSpaybankcode());//征收机关代码
		voucherdto.setShold1(vDto.getShold1());//预算种类
		voucherdto.setShold2(vDto.getShold2());//调整期标志
		voucherdto.setShold3(vDto.getShold3());//报表范围
		voucherdto.setShold4(vDto.getShold4());//科目代码
		voucherdto.setSfilename(FileName);
		voucherdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		voucherdto.setSdemo("处理成功");				
		voucherdto.setSvoucherflag("1");
		voucherdto.setSvoucherno(mainvou);		
		voucherdto.setSvoucherflag("1");
		voucherdto.setSvoucherno(mainvou);
		voucherdto.setIcount(list.size());
		List lists=new ArrayList();
		lists.add(list);
		lists.add(voucherdto);
		lists.add(cDto);
		Map map= tranfor(lists);				
		List voucherList=new ArrayList();
		voucherList.add(map);
		voucherList.add(voucherdto);
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
			List<TrIncomedayrptDto> list=(List<TrIncomedayrptDto>) lists.get(0);
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(1);
			TsConvertfinorgDto cDto=(TsConvertfinorgDto) lists.get(2);
			
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("Id", vDto.getSdealno());
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear", vDto.getScreatdate().substring(0, 4));	//业务年度	
			vouchermap.put("VtCode", vDto.getSvtcode());//凭证类型编号		
			vouchermap.put("VouDate", vDto.getScreatdate());//凭证日期		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//凭证号
			vouchermap.put("BillKind", vDto.getShold3());//报表种类、范围
			vouchermap.put("BudgetType", vDto.getShold1());//预算种类
			vouchermap.put("ReportDate", vDto.getScheckdate());//报表日期
			vouchermap.put("FinOrgCode", cDto.getSfinorgcode());//财政机关代码
			vouchermap.put("TreCode", vDto.getStrecode());//国库代码
			vouchermap.put("TreName", SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());//国库名称
			vouchermap.put("Hold1", "");//预留字段1		
			vouchermap.put("Hold2", "");//预留字段2	
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			for (TrIncomedayrptDto dto:list) {

				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("TreCode", dto.getStrecode()); //下级国库主体代码
				Detailmap.put("TreName", SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(dto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(dto.getStrecode()).getStrename()); //下级国库主体名称
				Detailmap.put("DayAmt", MtoCodeTrans.transformString(dto.getNmoneyday())); 
				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(dto.getNmoneymonth())); 
				Detailmap.put("YearAmt", MtoCodeTrans.transformString(dto.getNmoneyyear())); 
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", ""); 
				allamt=allamt.add(dto.getNmoneyday());										
				Detail.add(Detailmap);
			}		
			Total=Total.add(allamt);			
			vouchermap.put("SumMoney",MtoCodeTrans.transformString(allamt));	
			vDto.setNmoney(Total);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
		
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}
}

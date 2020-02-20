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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TdTaxorgParamDto;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.ReportLKXMUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 收入月报表3507转化
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3562 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3562.class);
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
		vDto.setSdealno(VoucherUtil.getGrantSequence());
		TrIncomedayrptDto dto=new TrIncomedayrptDto();
		dto.setSrptdate(vDto.getScheckdate());//报表日期
		dto.setSbelongflag("1");//辖属标志为全辖
		dto.setStaxorgcode("000000000000");//征收机关代码
		dto.setSbudgettype(vDto.getShold1());//预算种类预算内预算外
		return tranforList(dto,vDto);
	}
	private List tranforList(TrIncomedayrptDto dto,TvVoucherinfoDto vDto) throws ITFEBizException
	{
		HtrIncomedayrptDto hdto=new HtrIncomedayrptDto();
		hdto.setSrptdate(dto.getSrptdate());
		hdto.setSbelongflag(dto.getSbelongflag());
		hdto.setStaxorgcode(dto.getStaxorgcode());
		hdto.setSbudgettype(dto.getSbudgettype());
		List<TsTreasuryDto> treList = null;
		List<TrIncomedayrptDto> list=null;
		List listss=new ArrayList();
		TsConvertfinorgDto cDto=null;
		Map<String,TsConvertfinorgDto> contreMap = new HashMap<String,TsConvertfinorgDto>();
		try {
			cDto = new TsConvertfinorgDto();
			List<TsConvertfinorgDto> tsConvertfinorgDtoList=CommonFacade.getODB().findRsByDto(cDto);
			if(tsConvertfinorgDtoList!=null&&tsConvertfinorgDtoList.size()>0)
			{
				TsConvertfinorgDto confindto = null;
				for(int i=0;i<tsConvertfinorgDtoList.size();i++)
				{
					confindto = tsConvertfinorgDtoList.get(i);
					contreMap.put(confindto.getStrecode(), confindto);
					if(vDto.getSorgcode().indexOf(confindto.getStrecode())>=0)
						vDto.setSadmdivcode(confindto.getSadmdivcode());
				}

			}
			treList = CommonFacade.getODB().findRsByDto(new TsTreasuryDto());
			List<TsTreasuryDto> checkSuccessList = new ArrayList<TsTreasuryDto>();
			if(treList!=null&&treList.size()>0)
			{
				StringBuffer info = new StringBuffer("");
				for(TsTreasuryDto trdto:treList)
				{
					if(contreMap.get(trdto.getStrecode())==null)
						info.append(trdto.getStrecode()+",");
					else
						checkSuccessList.add(trdto);
				}
//				if(info.length()>0)
//					throw new ITFEBizException("国库："+info.substring(0,50)+"......"+" 对应的财政代码未维护！");
			}else
			{
				throw new ITFEBizException("国库代码未维护！");
			}
			String where = null;
			String[] billkinds;
			if(vDto.getShold2()==null||"".equals(vDto.getShold2()))
			{
				billkinds = new String[2];
				billkinds[0] = "1";//1-正常期预算收入报表
				billkinds[1] = "5";//5-调整期预算收入报表
			}else
			{
				billkinds = new String[1];
				billkinds[0] = vDto.getShold2();
			}
			for(TsTreasuryDto trdto:checkSuccessList)
			{
				cDto = contreMap.get(trdto.getStrecode());
				vDto.setStrecode(trdto.getStrecode());
				where = "AND S_FINORGCODE='"+cDto.getSfinorgcode()+"' and (S_TRECODE = '"+trdto.getStrecode()+"') AND (S_RPTDATE = '"+vDto.getScheckdate()+"' )  ORDER BY S_BUDGETSUBCODE ";
				dto.setStrecode(null);
				dto.setSrptdate(null);
				for(String billkind:billkinds)
				{
					dto.setSbillkind(billkind);
					hdto.setSbillkind(dto.getSbillkind());
					
//					list = CommonFacade.getODB().findRsByWhereForDto(dto, where);
//					list.addAll(CommonFacade.getODB().findRsByWhereForDto(hdto, where));
					list = CommonFacade.getODB().findRsByDtoForWhere(dto, where);					
					list.addAll(CommonFacade.getODB().findRsByDtoForWhere(hdto, where));
					ReportLKXMUtil reportUtil = new ReportLKXMUtil();
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=0,")<0&&ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=null,")<0)
						list = reportUtil.computeLKM(vDto.getSorgcode(), list, "1");
					else if(ITFECommonConstant.PUBLICPARAM.indexOf(",collectpayment=0,")>=0)
						list = reportUtil.computeLKM(vDto.getSorgcode(), list, "0");
			
					
					if(list!=null&&list.size()>0)
					{
						String dirsep = File.separator;
						String mainvou=VoucherUtil.getGrantSequence();
						String FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+ 
				        new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+ mainvou+".msg";
						TvVoucherinfoDto voucherdto=new TvVoucherinfoDto();
						voucherdto.setSdealno(mainvou);
						voucherdto.setSorgcode(vDto.getSorgcode());
						voucherdto.setSvtcode(vDto.getSvtcode());
						voucherdto.setScreatdate(TimeFacade.getCurrentStringTime());//凭证日期
						voucherdto.setScheckdate(vDto.getScheckdate());//报表日期
						voucherdto.setStrecode(vDto.getStrecode());
						voucherdto.setSadmdivcode(vDto.getSadmdivcode());
						voucherdto.setSstyear(voucherdto.getScheckdate().substring(0, 4));				
						voucherdto.setShold1(vDto.getShold1());//预算种类
						voucherdto.setShold2(billkind);//1-正常期预算收入报表5-调整期预算收入报表
						voucherdto.setSfilename(FileName);
						voucherdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
						voucherdto.setSdemo("处理成功");				
						voucherdto.setSvoucherno(mainvou);		
						voucherdto.setSvoucherflag("1");
						voucherdto.setIcount(list.size());
						List lists=new ArrayList();
						lists.add(list);
						lists.add(voucherdto);
						lists.add(cDto);
						Map map= tranfor(lists,vDto);				
						List voucherList=new ArrayList();
						voucherList.add(map);
						voucherList.add(voucherdto);
						listss.add(voucherList);
					}
				}
			}
			
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
	@SuppressWarnings("null")
	public Map tranfor(List lists,TvVoucherinfoDto infodto) throws ITFEBizException{
		try{	
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			List list=(List) lists.get(0);
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(1);
			TsConvertfinorgDto cDto=(TsConvertfinorgDto) lists.get(2);
			
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("Id", vDto.getSdealno());
			vouchermap.put("AdmDivCode", cDto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear", vDto.getScheckdate().substring(0, 4));	//业务年度	
			vouchermap.put("VtCode", vDto.getSvtcode());//凭证类型编号		
			vouchermap.put("VouDate", vDto.getScreatdate());//凭证日期		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//凭证号
			vouchermap.put("VoucherCheckNo", infodto.getSdealno());//对账单号
			vouchermap.put("BudgetType", vDto.getShold1());//预算种类
			vouchermap.put("ReportDate", vDto.getScheckdate());//报表日期	
			vouchermap.put("FinOrgCode", cDto.getSfinorgcode());//财政机关代码
			vouchermap.put("TreCode", vDto.getStrecode());//国库代码
			vouchermap.put("TreName", SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());//国库名称
			vouchermap.put("Hold1", "");//预留字段1		
			vouchermap.put("Hold2", "");//预留字段2	
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			Map<String,TdTaxorgParamDto> taxorgcodeMap = getTdTaxorgParam(vDto.getSorgcode());
			TrIncomedayrptDto rdto = null;
			HtrIncomedayrptDto hrdto = null;
			for (int i=0;i<list.size();i++) {
				if(list.get(i) instanceof TrIncomedayrptDto)
					rdto = (TrIncomedayrptDto)list.get(i);
				else if(list.get(i) instanceof HtrIncomedayrptDto)
					hrdto = (HtrIncomedayrptDto)list.get(i);
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("AdmDivCode", cDto.getSadmdivcode()); // 区划代码
				Detailmap.put("StYear", vDto.getScheckdate().substring(0, 4)); 
				Detailmap.put("TaxOrgCode",rdto==null?hrdto.getStaxorgcode():rdto.getStaxorgcode()); 				
				if(taxorgcodeMap.get(rdto==null?hrdto.getStaxorgcode():rdto.getStaxorgcode())==null){
					Detailmap.put("TaxOrgName", VoucherUtil.getStaxorgname(rdto==null?hrdto.getStaxorgcode():rdto.getStaxorgcode()));
				}else{
					Detailmap.put("TaxOrgName", taxorgcodeMap.get(rdto==null?hrdto.getStaxorgcode():rdto.getStaxorgcode()).getStaxorgname());
				}
				Detailmap.put("BankCode", vDto.getStrecode());//国库代码
				Detailmap.put("TreName", SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());//国库名称
				Detailmap.put("BudgetType", rdto==null?hrdto.getSbudgettype():rdto.getSbudgettype()); 
				Detailmap.put("BudgetLevelCode",rdto==null?hrdto.getSbudgetlevelcode():rdto.getSbudgetlevelcode()); 
				Detailmap.put("BudgetSubjectCode", rdto==null?hrdto.getSbudgetsubcode():rdto.getSbudgetsubcode()); 
				Detailmap.put("BudgetSubjectName", rdto==null?hrdto.getSbudgetsubname():rdto.getSbudgetsubname());  
				Detailmap.put("YearAmt", MtoCodeTrans.transformString(rdto==null?hrdto.getNmoneyyear():rdto.getNmoneyyear())); 
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", ""); 
				allamt=allamt.add(rdto==null?hrdto.getNmoneyyear():rdto.getNmoneyyear());										
				Detail.add(Detailmap);
				rdto = null;
				hrdto = null;
			}				
			vDto.setNmoney(allamt);
			vouchermap.put("SumMoney", String.valueOf(allamt));//年报合计金额
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
	private Map<String,TdTaxorgParamDto> getTdTaxorgParam(String sorgcode) throws ITFEBizException 
	{
		Map<String,TdTaxorgParamDto> getmap = new HashMap<String,TdTaxorgParamDto>();
		TdTaxorgParamDto tDto=new TdTaxorgParamDto();			
		tDto.setSbookorgcode(sorgcode);
		try {
			List<TdTaxorgParamDto> tdTaxorgParamDtoList=CommonFacade.getODB().findRsByDto(tDto);
			if(tdTaxorgParamDtoList!=null&&tdTaxorgParamDtoList.size()>0)
			{
				for(TdTaxorgParamDto dto:tdTaxorgParamDtoList)
					getmap.put(dto.getStaxorgcode(), dto);
			}
		} catch (JAFDatabaseException e) {
			throw new ITFEBizException(e);
		} catch (ValidateException e) {
			throw new ITFEBizException(e);
		}
		return getmap;
	}
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}

}

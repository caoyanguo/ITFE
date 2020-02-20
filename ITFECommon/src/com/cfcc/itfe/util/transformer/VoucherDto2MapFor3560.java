package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrIncomedayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 各地资金留用情况表（3560）
 * 
 * @author hejianrong
 * @time  2013-08-16
 * 
 */
public class VoucherDto2MapFor3560 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3560.class);
	private static final String Eta_BUDGETSUBCODE = "110019101";//省税收预抵返还科目编码
	private static final String Tax_BUDGETSUBCODE = "110019001";//两税返还科目编码
	private static final String S_TAXORGCODE = "000000000000";//全辖征收机关 
	private int S_BUDGETTYPE ;//预算种类
	private BigDecimal Total;//总金额
	
	/**
	 * 生成凭证
	 * @param vDto
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		List list=new ArrayList();
		List lists=new ArrayList();
		int max;
		if(StringUtils.isBlank(vDto.getShold1())){
			max=2;
			vDto.setShold1("1");
		}else
			max=Integer.parseInt(vDto.getShold1());		
		for(S_BUDGETTYPE=Integer.parseInt(vDto.getShold1());S_BUDGETTYPE<=max;S_BUDGETTYPE++){
			list=findReport(vDto);
			if(list!=null&&list.size()>0)
				lists.add(voucherTranfor(vDto, list));			
		}return lists;
	}
	
	/**
	 * 生成凭证
	 * 生成凭证报文
	 * @param vDto
	 * @param list
	 * @throws ITFEBizException
	 */
	public List voucherTranfor(TvVoucherinfoDto vDto,List list) throws ITFEBizException {		
		TvVoucherinfoDto dto=(TvVoucherinfoDto) vDto.clone();		
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));
		dto.setSvoucherflag("1");		
		String mainvou=VoucherUtil.getGrantSequence();
		dto.setSdealno(mainvou);		
		dto.setSvoucherno(mainvou);
		dto.setScreatdate(TimeFacade.getCurrentStringTime());
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("处理成功");
		dto.setShold1(S_BUDGETTYPE+"");
		Total=new BigDecimal("0.00");
		List lists=new ArrayList();
		lists.add(dto);
		lists.add(list);				
		List voucherList=new ArrayList();
		voucherList.add(tranfor(lists));
		dto.setNmoney(Total);		
		voucherList.add(dto);
		return voucherList;		
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
			List<TrIncomedayrptDto> resultList=(List) lists.get(1);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("Id", vDto.getSdealno());//各地资金留用情况表Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());//业务年度		
			vouchermap.put("VtCode", vDto.getSvtcode());//凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());//凭证日期		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//凭证号		
			vouchermap.put("BudgetType", vDto.getShold1());//预算种类
			vouchermap.put("ReportDate", vDto.getScheckdate());//报表所属日期
			vouchermap.put("TreCode", vDto.getStrecode());//国库主体代码			
			vouchermap.put("TreName", vDto.getShold2());//国库主体名称
			vouchermap.put("FinOrgCode", vDto.getShold3());//财政机关代码			
			vouchermap.put("Hold1", "");//预留字段1		
			vouchermap.put("Hold2", "");//预留字段2
			BigDecimal SumEtaMoney=new BigDecimal("0.00");
			BigDecimal SumTaxMoney=new BigDecimal("0.00");
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail= new ArrayList<Object>();			
			for(TrIncomedayrptDto mainDto:resultList){
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("AdmDivCode", findAdmDivCodeByStrecode(mainDto.getStrecode()));//行政区划代码
				Detailmap.put("StYear", vDto.getSstyear());//业务年度
				Detailmap.put("TaxOrgCode",S_TAXORGCODE);//征收机关代码
				Detailmap.put("TaxOrgName", "全辖"); //征收机关名称
				Detailmap.put("BankCode", mainDto.getStrecode());//国库主体代码
				Detailmap.put("TreName", findStreNameByStrecode(mainDto.getStrecode()));//国库主体名称
				Detailmap.put("BudgetType", S_BUDGETTYPE);//资金性质
				Detailmap.put("TotalAmt", MtoCodeTrans.transformString(mainDto.getNmoneyquarter().add(mainDto.getNmoneyyear())));//本地合计金额
				Detailmap.put("EtaAmt", MtoCodeTrans.transformString(mainDto.getNmoneyquarter()));//省税收预抵返还金额
				Detailmap.put("TaxAmt", MtoCodeTrans.transformString(mainDto.getNmoneyyear()));//两税返还金额		
				Detailmap.put("Hold1", "");//预留字段1 
				Detailmap.put("Hold2", "");//预留字段2 	
				Detailmap.put("Hold3", "");//预留字段3 
				Detailmap.put("Hold4", "");//预留字段4
				Detail.add(Detailmap);
				SumEtaMoney=SumEtaMoney.add(mainDto.getNmoneyquarter());
				SumTaxMoney=SumTaxMoney.add(mainDto.getNmoneyyear());
				Total=Total.add(mainDto.getNmoneyquarter());
				Total=Total.add(mainDto.getNmoneyyear());
			}
			vouchermap.put("SumTotalMoney", MtoCodeTrans.transformString(Total));//各地合计金额
			vouchermap.put("SumEtaMoney", MtoCodeTrans.transformString(SumEtaMoney));//省税收预抵返还合计金额
			vouchermap.put("SumTaxMoney", MtoCodeTrans.transformString(SumTaxMoney));//两税返还合计金额
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			return map;	
		}catch(ITFEBizException e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage());
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！",e);
		}
	
	}
	
	/**
	 * 根据国库代码查询国库名称
	 * @param strecode
	 * @return
	 * @throws ITFEBizException
	 */
	public String findStreNameByStrecode(String strecode) throws ITFEBizException{
		TsTreasuryDto dto=new TsTreasuryDto();
		dto.setStrecode(strecode);
		List<TsTreasuryDto> list;
		try {
			list = CommonFacade.getODB().findRsByDto(dto);
			if(list!=null && list.size() > 0)
				  return (list.get(0).getStrename());			 
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库代码信息异常！");
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询国库代码信息异常！");
		}throw new ITFEBizException("国库主体代码"+strecode+"在[国库主体信息参数]中未维护！");		  
	}
	
	 /**
     * 根据国库代码查找相应的区划代码
     * @return
	 * @throws ITFEBizException 
     */
    public String findAdmDivCodeByStrecode(String strecode) throws ITFEBizException{
    	TsConvertfinorgDto finorgDto=new TsConvertfinorgDto();
    	finorgDto.setStrecode(strecode);
    	try {
			List list = CommonFacade.getODB().findRsByDto(finorgDto);
			if(list==null||list.size()==0)
				throw new ITFEBizException("国库代码"+strecode+"对应的财政代码参数未维护！");							
			finorgDto=(TsConvertfinorgDto) list.get(0);
			if(StringUtils.isBlank(finorgDto.getSadmdivcode()))
				throw new ITFEBizException("财政代码"+finorgDto.getSfinorgcode()+"对应的区划代码参数未维护！");
			else
				return 	finorgDto.getSadmdivcode();	
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询政代码信息异常！");		
		}	catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询政代码信息异常！");		
		}
    }
    
	/**
	 * 查询报表信息
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public List findReport(TvVoucherinfoDto dto) throws ITFEBizException{		
		try {
			SQLExecutor sqlExecutor=null;
			sqlExecutor = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.clearParams();
			SQLResults rs = sqlExecutor.runQueryCloseCon(getSql(dto),TrIncomedayrptDto.class);
			return (List) rs.getDtoCollection();
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询报表信息出错！",e);
		}
	}
	
	/**
	 * 查询SQL语句
	 * @param dto
	 * @return
	 */
	private String getSql(TvVoucherinfoDto dto){
//		return " SELECT (CASE WHEN t1.S_BUDGETSUBCODE IS NULL THEN t2.S_TRECODE ELSE t1.S_TRECODE END) AS S_TRECODE,"+
//				" (CASE WHEN t1.S_FINORGCODE IS NULL THEN t2.S_FINORGCODE ELSE t1.S_FINORGCODE END) AS S_FINORGCODE,"+
//				" VALUE(N_MONEYQUARTER,0) AS N_MONEYQUARTER,VALUE(N_MONEYYEAR,0) AS N_MONEYYEAR FROM "+
//				" (SELECT TR_INCOMEDAYRPT.S_TRECODE,S_FINORGCODE ,S_BUDGETSUBCODE ,sum (N_MONEYDAY) AS N_MONEYQUARTER FROM TR_INCOMEDAYRPT ,TS_TREASURY WHERE "+
//				" S_BUDGETTYPE='"+S_BUDGETTYPE+"' AND S_TAXORGCODE = '"+S_TAXORGCODE+"' AND  S_BUDGETSUBCODE ='"+Eta_BUDGETSUBCODE+"' "+
//				" AND S_RPTDATE='"+dto.getScheckdate()+"' AND S_BUDGETLEVELCODE= S_TRELEVEL AND TR_INCOMEDAYRPT.S_TRECODE=TS_TREASURY.S_TRECODE"+
//				" GROUP BY TR_INCOMEDAYRPT.S_TRECODE ,S_FINORGCODE,S_BUDGETSUBCODE ORDER BY TR_INCOMEDAYRPT.S_TRECODE ) t1 FULL  JOIN  "+
//				" (SELECT TR_INCOMEDAYRPT.S_TRECODE,S_FINORGCODE ,S_BUDGETSUBCODE ,sum (N_MONEYDAY) AS N_MONEYYEAR FROM TR_INCOMEDAYRPT ,TS_TREASURY WHERE "+ 
//				" S_BUDGETTYPE='"+S_BUDGETTYPE+"' AND S_TAXORGCODE = '"+S_TAXORGCODE+"' AND  S_BUDGETSUBCODE ='"+Tax_BUDGETSUBCODE+"' "+
//				" AND S_RPTDATE='"+dto.getScheckdate()+"' AND S_BUDGETLEVELCODE= S_TRELEVEL AND TR_INCOMEDAYRPT.S_TRECODE=TS_TREASURY.S_TRECODE " +
//				" GROUP BY TR_INCOMEDAYRPT.S_TRECODE ,S_FINORGCODE,S_BUDGETSUBCODE ORDER BY TR_INCOMEDAYRPT.S_TRECODE) t2 ON t1.S_TRECODE=t2.S_TRECODE";

		return "SELECT S_TRECODE,S_FINORGCODE,SUM(N_MONEYQUARTER) AS N_MONEYQUARTER,SUM(N_MONEYYEAR) AS N_MONEYYEAR FROM ("+
		" SELECT (CASE WHEN t1.S_BUDGETSUBCODE IS NULL THEN t2.S_TRECODE ELSE t1.S_TRECODE END) AS S_TRECODE,"+
		" (CASE WHEN t1.S_FINORGCODE IS NULL THEN t2.S_FINORGCODE ELSE t1.S_FINORGCODE END) AS S_FINORGCODE,"+
		" VALUE(N_MONEYQUARTER,0) AS N_MONEYQUARTER,VALUE(N_MONEYYEAR,0) AS N_MONEYYEAR FROM "+
		" (SELECT TR_INCOMEDAYRPT.S_TRECODE,S_FINORGCODE ,S_BUDGETSUBCODE ,sum (N_MONEYYEAR) AS N_MONEYQUARTER FROM TR_INCOMEDAYRPT ,TS_TREASURY WHERE "+
		" S_BUDGETTYPE='"+S_BUDGETTYPE+"' AND (S_TAXORGCODE = '"+S_TAXORGCODE+"' OR S_TAXORGCODE = '12290020000') AND  S_BUDGETSUBCODE ='"+Eta_BUDGETSUBCODE+"' "+
		" AND S_RPTDATE='"+dto.getScheckdate()+"' AND (((S_TRELEVEL = '3' OR S_TRELEVEL = '2') AND S_BUDGETLEVELCODE= S_TRELEVEL) OR ( S_TRELEVEL = '4' AND S_BUDGETLEVELCODE>= S_TRELEVEL)) AND TR_INCOMEDAYRPT.S_TRECODE=TS_TREASURY.S_TRECODE"+
		" GROUP BY TR_INCOMEDAYRPT.S_TRECODE ,S_FINORGCODE,S_BUDGETSUBCODE ORDER BY TR_INCOMEDAYRPT.S_TRECODE ) t1 FULL  JOIN  "+
		" (SELECT TR_INCOMEDAYRPT.S_TRECODE,S_FINORGCODE ,S_BUDGETSUBCODE ,sum (N_MONEYYEAR) AS N_MONEYYEAR FROM TR_INCOMEDAYRPT ,TS_TREASURY WHERE "+ 
		" S_BUDGETTYPE='"+S_BUDGETTYPE+"' AND (S_TAXORGCODE = '"+S_TAXORGCODE+"' OR S_TAXORGCODE = '12290020000') AND  S_BUDGETSUBCODE ='"+Tax_BUDGETSUBCODE+"' "+
		" AND S_RPTDATE='"+dto.getScheckdate()+"' AND (((S_TRELEVEL = '3' OR S_TRELEVEL = '2') AND (S_BUDGETLEVELCODE= S_TRELEVEL)) OR (S_TRELEVEL = '4' AND S_BUDGETLEVELCODE>= S_TRELEVEL)) AND TR_INCOMEDAYRPT.S_TRECODE=TS_TREASURY.S_TRECODE " +
		" GROUP BY TR_INCOMEDAYRPT.S_TRECODE ,S_FINORGCODE,S_BUDGETSUBCODE ORDER BY TR_INCOMEDAYRPT.S_TRECODE) t2 ON t1.S_TRECODE=t2.S_TRECODE"
		+") GROUP BY S_TRECODE,S_FINORGCODE ORDER BY S_TRECODE";
}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}
	

}

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
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 人民银行与财政部门支付对账单（4004）
 * 
 * @author hejianrong
 * @time  2014-06-16
 * 
 */
public class VoucherDto2MapFor4004 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor4004.class);
	private BigDecimal Total;//总金额
	
	/**
	 * 生成凭证
	 * @param vDto
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		List list=new ArrayList();
		List lists=new ArrayList();
		list=findReport(vDto);
		if(list!=null&&list.size()>0)
			lists.add(voucherTranfor(vDto, list));			
		return lists;
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
		dto.setSpaybankcode("");
		dto.setShold1("");
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
			List<TvVoucherinfoDto> resultList=(List) lists.get(1);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("Id", vDto.getSdealno());//各地资金留用情况表Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());//业务年度		
			vouchermap.put("VtCode", vDto.getSvtcode());//凭证类型编号
			vouchermap.put("VouDate", vDto.getScreatdate());//凭证日期		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//凭证号		
			vouchermap.put("VoucherCheckNo", vDto.getSvoucherno());
			vouchermap.put("TreCode", vDto.getStrecode());//国库主体代码			
			vouchermap.put("CheckDate", vDto.getScheckdate());	
			vouchermap.put("Hold1", "");//预留字段1		
			vouchermap.put("Hold2", "");//预留字段2
			BigDecimal SumEtaMoney=new BigDecimal("0.00");
			BigDecimal SumTaxMoney=new BigDecimal("0.00");
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail= new ArrayList<Object>();			
			for(TvVoucherinfoDto mainDto:resultList){
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("EVoucherType", mainDto.getSvtcode());//发送的凭证类型编号
				Detailmap.put("EVoucherTypeName", mainDto.getSdemo());
				Detailmap.put("AllNum",mainDto.getIcount());//总笔数
				Detailmap.put("AllAmt", MtoCodeTrans.transformString(mainDto.getNmoney())); //总金额				
				Detailmap.put("Hold1", "");//预留字段1
				Detailmap.put("Hold2", "");//预留字段2 	
				Detailmap.put("Hold3", "");//预留字段3 
				Detailmap.put("Hold4", "");//预留字段4
				Detail.add(Detailmap);				
				Total=Total.add(mainDto.getNmoney());				
			}			
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
			SQLResults rs = sqlExecutor.runQueryCloseCon(getSql(dto),TvVoucherinfoDto.class);
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
		return " SELECT CASE S_VTCODE WHEN '5106' THEN '授权支付额度' WHEN '5108' THEN '直接支付额度' WHEN '5207' THEN '实拨资金' WHEN '3208' "+
			   " THEN '实拨退款'  ELSE '未知业务类型'  END  AS S_DEMO,S_VTCODE,"+
			   " VALUE(SUM(n_money),0) AS n_money,count(0) AS I_COUNT FROM TV_VOUCHERINFO "+
			   " WHERE S_VTCODE  IN ('5207','3208','5106','5108')"+
			   " AND S_STATUS='80' AND S_TRECODE = '"+dto.getStrecode()+"' AND S_CREATDATE = '"+dto.getScheckdate()+"' GROUP BY S_VTCODE";
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}
	

}

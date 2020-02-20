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
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * 人民银行与代理银行集中支付业务对账单（4005）
 * 
 * @author hejianrong
 * @time  2014-06-16
 * 
 */
public class VoucherDto2MapFor4005 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor4005.class);
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
		if(list!=null&&list.size()>0){
			for(TvVoucherinfoDto dto:(List<TvVoucherinfoDto>)list){
				vDto.setSpaybankcode(dto.getSpaybankcode());
				vDto.setShold3(MsgConstant.VOUCHER_NO_2301);
				List result = findReport(vDto);
				vDto.setShold3(MsgConstant.VOUCHER_NO_2302);
				result.addAll(findReport(vDto));								
				if(result!=null && result.size()>0)
					lists.add(voucherTranfor(vDto, result));
			}
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
		String bankType=VoucherUtil.getSpaybankType(dto.getStrecode(), dto.getSpaybankcode());
		dto.setSvoucherno(bankType+"_"+mainvou);
		dto.setSattach(bankType);
		dto.setScreatdate(TimeFacade.getCurrentStringTime());
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("处理成功");
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
			vouchermap.put("PayBankCode",vDto.getSpaybankcode());
			vouchermap.put("PayBankName", PublicSearchFacade.findPayBankNameByPayBankCode(vDto.getSpaybankcode()));
			vouchermap.put("CheckDate", vDto.getScheckdate());
			vouchermap.put("Hold1", "");//预留字段1		
			vouchermap.put("Hold2", "");//预留字段2
			BigDecimal SumEtaMoney=new BigDecimal("0.00");
			BigDecimal SumTaxMoney=new BigDecimal("0.00");
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail= new ArrayList<Object>();			
			for(TvVoucherinfoDto mainDto:resultList){
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("EVoucherType", mainDto.getShold3());//发送的凭证类型编号
				Detailmap.put("EVoucherTypeName", mainDto.getSdemo());
				Detailmap.put("PayTypeCode", Integer.parseInt(mainDto.getShold1())+1);
				Detailmap.put("PayTypeName", mainDto.getShold2());
				Detailmap.put("AllNum",mainDto.getIcount());//总笔数
				Detailmap.put("AllAmt", MtoCodeTrans.transformString(mainDto.getNmoney()));		
				Detailmap.put("Hold1", "");//总金额
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
		}catch(ITFEBizException e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);
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
			SQLExecutor sqlExecutor=DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExecutor.clearParams();
			SQLResults rs = sqlExecutor.runQueryCloseCon(StringUtils.isBlank(dto.getShold3())?getSql(dto):getDetailSql(dto),
					TvVoucherinfoDto.class);
			return (List) rs.getDtoCollection();
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询凭证信息出错！",e);
		}
	}
	
	/**
	 * 查询SQL语句
	 * @param dto
	 * @return
	 */
	private String getSql(TvVoucherinfoDto dto){
		return " SELECT DISTINCT(S_PAYBANKCODE) FROM TV_VOUCHERINFO  WHERE S_VTCODE  IN ('2301','2302')"+ 
			   " AND S_STATUS='80' AND S_TRECODE = '"+dto.getStrecode()+"' AND S_CREATDATE = '"+dto.getScheckdate()+"' "+ 
			   " AND S_PAYBANKCODE IS NOT NULL ";
	}
	
	/**
	 * 查询明细SQL语句
	 * @param dto
	 * @return
	 */
	private String getDetailSql(TvVoucherinfoDto dto){
		return " SELECT "+ (dto.getShold3().equals(MsgConstant.VOUCHER_NO_2301)?"'2301' AS S_HOLD3 ,CASE S_PAYMODE WHEN '0' THEN '直接支付' WHEN '1' " +
			   " THEN '授权支付' END ":"'2302' AS S_HOLD3 ,CASE S_PAYMODE WHEN '0' THEN '直接支付退款' WHEN '1' THEN '授权支付退款' END ")+
               " AS S_DEMO,S_PAYMODE AS S_HOLD1,"+
               " CASE S_PAYMODE WHEN '0' THEN '直接支付' WHEN '1' THEN '授权支付' END AS S_HOLD2,"+
               " VALUE(sum(F_AMT),0) AS N_MONEY,count(0) AS I_COUNT from "+
               " (select * from "+(dto.getShold3().equals(MsgConstant.VOUCHER_NO_2301)?"TV_PAYRECK_BANK":"TV_PAYRECK_BANK_BACK") +
               " union all  select * from H"+(dto.getShold3().equals(MsgConstant.VOUCHER_NO_2301)?"TV_PAYRECK_BANK":"TV_PAYRECK_BANK_BACK")+") as t"+
               " Where I_VOUSRLNO IN (SELECT S_DEALNO FROM TV_VOUCHERINFO WHERE S_VTCODE= '"+dto.getShold3()+"'  AND S_TRECODE= '"+dto.getStrecode()+"'"+
               " AND S_STATUS='80' AND S_PAYBANKCODE='"+dto.getSpaybankcode()+"' AND S_CREATDATE='"+dto.getScheckdate()+"' ) GROUP BY S_PAYMODE ";
	}
	
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}
	

}

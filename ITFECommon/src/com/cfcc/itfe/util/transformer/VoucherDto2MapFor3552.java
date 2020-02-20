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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;



/**
 * 凭证库存日报表(3552)转化
 * 
 * @author zhb
 * @time  2014-08-16
 * 
 */
public class VoucherDto2MapFor3552 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3552.class);
	
	@SuppressWarnings("unchecked")
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{	
		//始终为当天的凭证日期
		vDto.setScreatdate(TimeFacade.getCurrentStringTime());
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		cDto.setSorgcode(vDto.getSorgcode());		
		cDto.setStrecode(vDto.getStrecode());
		List<TrStockdayrptDto> sList=new ArrayList<TrStockdayrptDto>();
		//取得对账月份的最后一天
		String rptdate = TimeFacade.getEndDateOfMonth(vDto.getScheckdate()+"01");
		String stockDayRptSql="SELECT * FROM TR_STOCKDAYRPT WHERE S_TRECODE = ? AND S_RPTDATE = (SELECT MAX(S_RPTDATE) FROM TR_STOCKDAYRPT WHERE S_TRECODE = ? AND S_RPTDATE LIKE '"+vDto.getScheckdate()+"%') ";
		try {
			List<TsConvertfinorgDto> tsConvertfinorgList=(List<TsConvertfinorgDto>) CommonFacade.getODB().findRsByDto(cDto);
			if(tsConvertfinorgList==null||tsConvertfinorgList.size()==0){
				throw new ITFEBizException("国库："+vDto.getStrecode()+"对应的财政机关代码参数未维护！");
			}
			cDto=(TsConvertfinorgDto) tsConvertfinorgList.get(0);
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){				
				throw new ITFEBizException("国库："+cDto.getStrecode()+"对应的区划代码未维护！");
			}
			SQLExecutor exec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			exec.addParam(vDto.getStrecode());
			exec.addParam(vDto.getStrecode());
//			exec.addParam(rptdate);
			sList=  (List<TrStockdayrptDto>) exec.runQueryCloseCon(stockDayRptSql,TrStockdayrptDto.class).getDtoCollection();
			if(sList==null||sList.size()==0){
				return null;
			}
		} catch (JAFDatabaseException e2) {		
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		}		
		List<List> lists=new ArrayList<List>();
		String FileName=null;
		String dirsep = File.separator; 
		for(TrStockdayrptDto stDto:sList){
			String mainvou=VoucherUtil.getGrantSequence();
			FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+mainvou+ ".msg";
			TvVoucherinfoDto dto=new TvVoucherinfoDto();			
			dto.setSorgcode(vDto.getSorgcode());
			dto.setSadmdivcode(vDto.getSadmdivcode());
			dto.setSvtcode(vDto.getSvtcode());
			dto.setScreatdate(vDto.getScreatdate());
			dto.setStrecode(vDto.getStrecode());
			dto.setSfilename(FileName);
			dto.setSdealno(mainvou);		
			dto.setSadmdivcode(cDto.getSadmdivcode());
			dto.setSstyear(dto.getScreatdate().substring(0, 4));				
			dto.setSattach("");
			dto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
			dto.setSdemo("处理成功");				
			dto.setSvoucherflag("1");
			dto.setSvoucherno(mainvou);	
			dto.setScheckdate(vDto.getScheckdate());
			dto.setNmoney(stDto.getNmoneytoday());
			dto.setShold1(stDto.getSaccno());
			SQLExecutor execDetail;
			String stockDayRptDetailSql="SELECT * FROM TR_STOCKDAYRPT WHERE S_TRECODE = '"+stDto.getStrecode()+"' AND S_ACCNO = '"+stDto.getSaccno()+"' AND S_RPTDATE >= '"+vDto.getScheckdate()+"01' AND S_RPTDATE <= '"+stDto.getSrptdate()+"' ORDER BY S_RPTDATE ASC";
			List<TrStockdayrptDto> detailList=new ArrayList<TrStockdayrptDto>();;
			try {
				execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
				detailList=  (List<TrStockdayrptDto>) execDetail.runQueryCloseCon(stockDayRptDetailSql,TrStockdayrptDto.class).getDtoCollection();
				if(detailList==null||detailList.size()==0){
					return null;
				}
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("查询"+vDto.getScheckdate().substring(0, 4)+"年"+vDto.getScheckdate().substring(4, 6)+"月份的库存明细信息异常！",e);
			}
			List mapList=new ArrayList();
			mapList.add(stDto);
			mapList.add(dto);
			mapList.add(detailList);
			Map map=tranfor(mapList);		
			List vouList=new ArrayList();
			vouList.add(map);
			vouList.add(dto);			
			lists.add(vouList);			
		}
		return lists;
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
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> vouchermap = new HashMap<String, Object>();
		TrStockdayrptDto sDto=(TrStockdayrptDto) lists.get(0);
		TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(1);
		
		// 设置报文节点 Voucher
		map.put("Voucher", vouchermap);
		// 设置报文消息体 
		vouchermap.put("Id", vDto.getSdealno());
		vouchermap.put("AdmDivCode", vDto.getSadmdivcode());
		vouchermap.put("StYear", vDto.getSstyear());		
		vouchermap.put("VtCode", vDto.getSvtcode());		
		vouchermap.put("VouDate", vDto.getScreatdate());		
		vouchermap.put("VoucherNo", vDto.getSvoucherno());		
		vouchermap.put("SetMonth", (vDto.getScheckdate()).substring(4,6));	
		vouchermap.put("AllAmt", MtoCodeTrans.transformString(sDto.getNmoneytoday()));			
		vouchermap.put("AcctNo", sDto.getSaccno());	
		vouchermap.put("AcctName", sDto.getSaccname());	
		vouchermap.put("XCurDateMoney",	new BigDecimal(0.00));	
		vouchermap.put("XDiffMoney", new BigDecimal(0.00));	
		vouchermap.put("Hold1", "");	
		vouchermap.put("Hold2", "");	
		List<Object> DetailList = new ArrayList<Object>();
		List<Object> Detail= new ArrayList<Object>();
		List<TrStockdayrptDto> detailList=(List<TrStockdayrptDto>) lists.get(2);
		for (TrStockdayrptDto detailStockDto : detailList) {
			Map<String, Object> detailmap = new HashMap<String, Object>();
			detailmap.put("CreateDate", detailStockDto.getSrptdate());
			detailmap.put("PbcProCat", "收入："+detailStockDto.getNmoneyin()+";支出："+detailStockDto.getNmoneyout());
			detailmap.put("PbcPayAmt", MtoCodeTrans.transformString(detailStockDto.getNmoneyin().subtract(detailStockDto.getNmoneyout())));
			detailmap.put("MofProCat", "");
			detailmap.put("MofPayAmt", "");
			detailmap.put("Remark", "");
			detailmap.put("Hold1", "");
			detailmap.put("Hold2", "");
			detailmap.put("Hold3", "");
			detailmap.put("Hold4", "");
			Detail.add(detailmap);
		}
		HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
		DetailListmap.put("Detail",Detail);
		DetailList.add(DetailListmap);
		vouchermap.put("DetailList", DetailList);
		return map;
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}
	

}

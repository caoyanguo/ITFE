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
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;



/**
 * 凭证库存日报表(3402)转化
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3402 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3402.class);
	private  BigDecimal Total;
	
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		cDto.setSorgcode(vDto.getSorgcode());		
		cDto.setStrecode(vDto.getStrecode());
		List<TrStockdayrptDto> sList=new ArrayList<TrStockdayrptDto>();
		TrStockdayrptDto sDto=new TrStockdayrptDto();
		sDto.setStrecode(vDto.getStrecode());
		sDto.setSrptdate(vDto.getScheckdate());
		try {
			List tsConvertfinorgList=(List) CommonFacade.getODB().findRsByDto(cDto);
			if(tsConvertfinorgList==null||tsConvertfinorgList.size()==0){
				throw new ITFEBizException("国库："+vDto.getStrecode()+"对应的财政机关代码参数未维护！");
			}
			cDto=(TsConvertfinorgDto) tsConvertfinorgList.get(0);
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){				
				throw new ITFEBizException("国库："+cDto.getStrecode()+"对应的区划代码未维护！");
			}
			sList= CommonFacade.getODB().findRsByDto(sDto);		
			if(sList==null||sList.size()==0){
				return null;
			}
			vDto.setSadmdivcode(cDto.getSadmdivcode());
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
		if(sList!=null&&sList.size()>0){
			
			String mainvou=VoucherUtil.getGrantSequence();
			FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+ 
	        new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+ mainvou+".msg";
			TvVoucherinfoDto dto=new TvVoucherinfoDto();			
			dto.setSorgcode(vDto.getSorgcode());
			dto.setSadmdivcode(vDto.getSadmdivcode());
			dto.setSvtcode(vDto.getSvtcode());
			dto.setScreatdate(TimeFacade.getCurrentStringTime());//凭证日期
			dto.setScheckdate(vDto.getScheckdate());//报表日期
			dto.setStrecode(vDto.getStrecode());
			dto.setSfilename(FileName);
			dto.setSdealno(mainvou);		
			dto.setSadmdivcode(cDto.getSadmdivcode());
			dto.setSstyear(dto.getScreatdate().substring(0, 4));				
			dto.setSattach("");
			dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
			dto.setSdemo("处理成功");				
			dto.setSvoucherflag("1");
			dto.setSvoucherno(mainvou);
			dto.setIcount(sList.size());
			Map map=tranfor(sList,dto);					
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
	private Map tranfor(List<TrStockdayrptDto> dtoList,TvVoucherinfoDto vDto) throws ITFEBizException{
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> vouchermap = new HashMap<String, Object>();
		
		// 设置报文节点 Voucher
		map.put("Voucher", vouchermap);
		// 设置报文消息体 
		vouchermap.put("Id", vDto.getSdealno());//库存日报表Id
		vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//行政区划代码
		vouchermap.put("StYear", vDto.getScheckdate().substring(0,4));//业务年度		
		vouchermap.put("VtCode", vDto.getSvtcode());//凭证类型编号		
		vouchermap.put("VouDate", vDto.getScreatdate());//凭证日期	
		vouchermap.put("AcctDate", vDto.getScheckdate());//报表日期
		vouchermap.put("VoucherNo", vDto.getSvoucherno());//凭证号
		vouchermap.put("BankCode", vDto.getStrecode());//国库主体代码
		vouchermap.put("TreName", BusinessFacade.findTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":BusinessFacade.findTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());//国库主体名称
		vouchermap.put("Hold1", "");//预留字段1	
		vouchermap.put("Hold2", "");//预留字段2	
		Map detailListMap = new HashMap();
		List detailList = new ArrayList();
		Map detailMap = null;//Voucher/DetailList/Detail/
		Total=new BigDecimal("0.00");
		int id=0;
		for(TrStockdayrptDto sdto:dtoList)
		{
			if(!ITFECommonConstant.PUBLICPARAM.contains(",3402acct371=true,")&&sdto.getSaccno().contains(vDto.getSorgcode()+"371"))//核算主体代码加371开头的账户代码不生成
			{
				continue;
			}
			detailMap = new HashMap();
			detailMap.put("Id", vDto.getSdealno()+(id++));//id
			detailMap.put("AcctCode", sdto.getSaccno());//账户代码
			detailMap.put("AcctName", sdto.getSaccname());//账户名称
			detailMap.put("YesterdayBalance", MtoCodeTrans.transformString(sdto.getNmoneyyesterday()));//上日余额
			detailMap.put("TodayReceipt", MtoCodeTrans.transformString(sdto.getNmoneyin()));//本日收入
			detailMap.put("TodayPay", MtoCodeTrans.transformString(sdto.getNmoneyout()));//本日支出
			detailMap.put("TodayBalance", MtoCodeTrans.transformString(sdto.getNmoneytoday()));//本日余额
			detailMap.put("Hold1", "");//预留字段1
			detailMap.put("Hold2", "");//预留字段2
			detailMap.put("Hold3", "");//预留字段3
			detailMap.put("Hold4", "");//预留字段4
			Total =Total.add(sdto.getNmoneytoday());
			detailList.add(detailMap);
		}
		vDto.setNmoney(Total);
		vouchermap.put("DetailList", detailListMap);
		detailListMap.put("Detail", detailList);
		return map;
	}

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}

	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	

}

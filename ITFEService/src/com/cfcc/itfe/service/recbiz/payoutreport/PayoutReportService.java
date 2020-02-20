package com.cfcc.itfe.service.recbiz.payoutreport;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor3508;
import com.cfcc.itfe.util.transformer.VoucherDto2MapFor3553;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author renqingbin
 * @time   14-03-28 09:04:23
 * codecomment: 
 */

public class PayoutReportService extends AbstractPayoutReportService {
	private static Log log = LogFactory.getLog(PayoutReportService.class);	

	/**
	 * 支出报表生成
	 	 
	 * @generated
	 * @param list<vocherlist[凭证索引信息list],reportTypeList[报表种类list]>
	 * @return com.cfcc.itfe.facade.data.MulitTableDto
	 * @throws ITFEBizException	 
	 */
    @SuppressWarnings("unchecked")
	public MulitTableDto payoutVoucherGenerate(List list) throws ITFEBizException {
    	int count=0;
    	MulitTableDto mulitTableDto = new MulitTableDto();
    	String currentDate = TimeFacade.getCurrentStringTime(); 
    	String dirsep = File.separator;
    	List<TvVoucherinfoDto> vocherlist = (List<TvVoucherinfoDto>) list.get(0);
    	List<String> reportTypeList = (List<String>) list.get(1);
    	List<TrTaxorgPayoutReportDto> payoutReportList = null;
    	List<String> errorList = new ArrayList<String>();
    	for(TvVoucherinfoDto voucherdto : vocherlist){
    		//校验国库对应的财政机构信息、区划代码是否维护
    		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
    		try {
    			cDto.setSorgcode(voucherdto.getSorgcode());		
    			cDto.setStrecode(voucherdto.getStrecode());
    			List finorgDtoList=CommonFacade.getODB().findRsByDto(cDto);
    			if(finorgDtoList==null||finorgDtoList.size()==0){
    				errorList.add("国库："+cDto.getStrecode()+" 对应的财政代码未维护，该国库的报表未生成！");
					continue;
    				//throw new ITFEBizException("国库："+cDto.getStrecode()+" 对应的财政代码未维护！");
    			}
    			cDto=(TsConvertfinorgDto) finorgDtoList.get(0);
    			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){
    				errorList.add("国库："+cDto.getStrecode()+" 对应的区划代码未维护，该国库的报表未生成！");
					continue;
    				//throw new ITFEBizException("国库："+cDto.getStrecode()+" 对应的区划代码未维护！");
    			}
				for(String voucherTpe : reportTypeList) {
					//校验凭证是否已经生成
					TvVoucherinfoDto tempdto = new TvVoucherinfoDto();
					tempdto.setSorgcode(voucherdto.getSorgcode());
					tempdto.setStrecode(voucherdto.getStrecode());
					tempdto.setScheckdate(voucherdto.getScheckdate());
					tempdto.setScheckvouchertype(voucherTpe);
					tempdto.setSattach(voucherdto.getSattach());
					if(StateConstant.REPORT_DAY.equals(tempdto.getSattach())){//日报
						tempdto.setSvtcode(MsgConstant.VOUCHER_NO_3553);
					}else if(StateConstant.REPORT_MONTH.equals(tempdto.getSattach())){//月报
						tempdto.setSvtcode(MsgConstant.VOUCHER_NO_3508);
					}
					tempdto.setShold1(voucherdto.getShold1());
					tempdto.setShold2(voucherdto.getShold2());
					tempdto.setShold3(voucherdto.getShold3());
					tempdto.setShold4(voucherdto.getShold4());
					List templist = CommonFacade.getODB().findRsByDto(tempdto);
					if(templist.size()>0){
						errorList.add("国库代码["+voucherdto.getStrecode()+"]的["+getReportName(voucherTpe)+"]已经生成，不能重复生成！");
						continue;
					}
					//组装凭证索引表dto
		    		String mainvou=VoucherUtil.getGrantSequence();
		    		voucherdto.setSdealno(mainvou);
		    		voucherdto.setSstyear(voucherdto.getScheckdate().substring(0, 4));
		    		voucherdto.setScreatdate(currentDate);
		    		voucherdto.setSvoucherno(mainvou);
		    		voucherdto.setSadmdivcode(cDto.getSadmdivcode());
					voucherdto.setSpaybankcode(cDto.getSfinorgcode());
		    		//文件名
		    		String fileName = ITFECommonConstant.FILE_ROOT_PATH + dirsep + "Voucher" + dirsep + currentDate + dirsep
						+ "send" + voucherdto.getSvtcode() + "_" + voucherdto.getSattach() + "_" + voucherTpe + "_"
						+ mainvou + ".msg";
		    		voucherdto.setSfilename(fileName);
		    		voucherdto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		    		voucherdto.setSdemo("处理成功");				
		    		voucherdto.setSvoucherflag("1");
		    		voucherdto.setScheckvouchertype(voucherTpe);
		    		//根据条件查询报表数据
					payoutReportList = queryPayoutReport(voucherTpe,voucherdto);
					if(payoutReportList == null || payoutReportList.size()==0){
						errorList.add("国库代码["+voucherdto.getStrecode()+"]的["+getReportName(voucherTpe)+"]没有数据，请确认是否已经导入！");
						continue;
					}
					Map map = null;
					if(StateConstant.REPORT_DAY.equals(voucherdto.getSattach())){//日报
						map = VoucherDto2MapFor3553.tranfor(payoutReportList, voucherdto);
					}else{
						map = VoucherDto2MapFor3508.tranfor(payoutReportList, voucherdto);
					}
	    			Map xmlmap = (Map) map.get("Map");
	    			BigDecimal sumamt = (BigDecimal) map.get("SumMoney");
	    			voucherdto.setNmoney(sumamt);
	    			VoucherUtil.sendTips(voucherdto,xmlmap);
	    			try {
						DatabaseFacade.getODB().create(voucherdto);
					} catch (JAFDatabaseException e) {
						log.error(e);					
						throw new ITFEBizException(e.getMessage(),e);
					}
					count++;
				}
    		} catch (JAFDatabaseException e2) {
    			log.error(e2);
    			throw new ITFEBizException("查询信息异常！",e2);
    		} catch (ValidateException e2) {
    			log.error(e2);
    			throw new ITFEBizException("查询信息异常！",e2);
    		}catch(Exception e2 ){
    			log.error(e2);
    			throw new ITFEBizException(e2.getMessage(),e2);
    		}
    	}
    	mulitTableDto.setTotalCount(count);
    	mulitTableDto.setErrorList(errorList);
    	return mulitTableDto;
    }
    /**
	 * 按照条件查询支出报表数据
	 * 
	 * @param subVoucherType 报表种类
	 * @param voucherdto     条件dto
	 * @throws ITFEBizException	 
	 */
	private List<TrTaxorgPayoutReportDto> queryPayoutReport(String voucherTpe,
			TvVoucherinfoDto voucherdto) throws ITFEBizException{
		List<TrTaxorgPayoutReportDto> payoutReportList = null;
	    TrTaxorgPayoutReportDto payoutdto = new TrTaxorgPayoutReportDto();
	    payoutdto.setStrecode(voucherdto.getStrecode());
	    payoutdto.setStaxorgcode(voucherTpe);//报表种类
	    payoutdto.setSrptdate(voucherdto.getScheckdate());
	    payoutdto.setSbudgettype(voucherdto.getShold1());//预算种类
	    payoutdto.setSbelongflag(voucherdto.getShold3());//辖属标志
	    payoutdto.setSpaytype(voucherdto.getShold4());//支出类型
	    payoutdto.setStrimflag(voucherdto.getShold2());//调整期
	    try {
			payoutReportList = CommonFacade.getODB().findRsByDtoForWhere(payoutdto, " order by S_BUDGETSUBCODE");//RsByDto(payoutdto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("支出报表查询异常！",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("支出报表查询异常！",e);
		}
		return payoutReportList;
	}

	/**
	 * 按照条件查询支出报表数据
	 * 
	 * @param subVoucherType 报表种类
	 * @param reportDate     报表日期
	 * @param strecode       国库代码
	 * @return String
	 * @throws ITFEBizException	 
	 */
	private List<TrTaxorgPayoutReportDto> queryPayoutReport(String subVoucherType,String reportDate,String strecode) throws ITFEBizException{
		List<TrTaxorgPayoutReportDto> payoutReportList = null;
	    TrTaxorgPayoutReportDto payoutdto = new TrTaxorgPayoutReportDto();
	    payoutdto.setStrecode(strecode);
	    payoutdto.setStaxorgcode(subVoucherType);//报表种类
	    payoutdto.setSrptdate(reportDate);
	    try {
			payoutReportList = CommonFacade.getODB().findRsByDto(payoutdto);
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("支出报表查询异常！",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("支出报表查询异常！",e);
		}
		return payoutReportList;
	}
	
	/**
	 * 
	 * @return
	 */
	private String getReportName(String voucherType){
		if(StateConstant.REPORT_PAYOUT_TYPE_1.equals(voucherType)){
			return "一般预算支出报表";
		}else if(StateConstant.REPORT_PAYOUT_TYPE_2.equals(voucherType)){
			return "实拨资金支出报表";
		}else if(StateConstant.REPORT_PAYOUT_TYPE_3.equals(voucherType)){
			return "调拨预算支出报表";
		}else if(StateConstant.REPORT_PAYOUT_TYPE_4.equals(voucherType)){
			return "直接支付日报表";
		}else if(StateConstant.REPORT_PAYOUT_TYPE_5.equals(voucherType)){
			return "授权支付日报表";
		}else if(StateConstant.REPORT_PAYOUT_TYPE_6.equals(voucherType)){
			return "直接支付退回日报表";
		}else if(StateConstant.REPORT_PAYOUT_TYPE_7.equals(voucherType)){
			return "授权支付退回日报表";					
		}
		return voucherType;
	}

}
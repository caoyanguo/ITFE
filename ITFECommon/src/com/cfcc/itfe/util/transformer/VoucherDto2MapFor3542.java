package com.cfcc.itfe.util.transformer;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 中央及省市往来票据(调拨收入凭证)（3542）
 * 
 * @author hejianrong
 * @time  2014-04-02
 * 
 */
public class VoucherDto2MapFor3542 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3542.class);

	/**
	 * 凭证生成
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto dto) throws ITFEBizException{
		List list=findMainDto(dto);
		if(list.size()==0)
			return list;
		List lists=new ArrayList();
		for(TvVoucherinfoAllocateIncomeDto mainDto:(List<TvVoucherinfoAllocateIncomeDto>)list){
			lists.add(voucherTranfor(dto, mainDto));
		}return lists;
	}
		
	/**
	 * 生成凭证
	 * 生成凭证报文
	 * @param vDto
	 * @param mainDto
	 * @throws ITFEBizException
	 */
	private List voucherTranfor(TvVoucherinfoDto vDto,TvVoucherinfoAllocateIncomeDto mainDto) throws ITFEBizException {		
		TvVoucherinfoDto dto=(TvVoucherinfoDto) vDto.clone();
		dto.setSdealno(VoucherUtil.getGrantSequence());
		dto.setSvoucherno(dto.getSdealno());
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));			
		dto.setSvoucherflag("1");
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));		
		dto.setSvoucherno(mainDto.getSdealno());
		dto.setSadmdivcode(mainDto.getSadmdivcode());
		dto.setNmoney(mainDto.getNmoney());
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("处理成功");		
		List lists=new ArrayList();
		lists.add(dto);
		lists.add(mainDto);
		Map map=tranfor(lists);
		lists.clear();		
		lists.add(map);
		lists.add(dto);
		return lists;
	}
	
	/**	
	 * 查询中央及省市往来票据(调拨收入凭证)业务表信息
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public List findMainDto(TvVoucherinfoDto dto) throws ITFEBizException {		
		TvVoucherinfoAllocateIncomeDto mainDto=new TvVoucherinfoAllocateIncomeDto();
		mainDto.setSorgcode(dto.getSorgcode());
		mainDto.setStrecode(dto.getStrecode());
		mainDto.setScommitdate(dto.getScreatdate());
		try {
			return CommonFacade.getODB().findRsByDto(mainDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询中央及省市往来票据业务表信息出错！",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询中央及省市往来票据业务表信息出错！",e);
		}
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
			BigDecimal Total=new BigDecimal("0.00");
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(0);
			TvVoucherinfoAllocateIncomeDto mainDto=(TvVoucherinfoAllocateIncomeDto) lists.get(1);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("Id", vDto.getSdealno());//中央及省市往来票据Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());//业务年度		
			vouchermap.put("VtCode", vDto.getSvtcode());//凭证类型编号			
			vouchermap.put("VouDate", vDto.getScreatdate());//凭证日期		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//凭证号		
			vouchermap.put("PayeeAcctNo", mainDto.getSpayeeacctno());//收款人账号
			vouchermap.put("PayeeAcctName", mainDto.getSpayeeacctname());//收款人名称
			vouchermap.put("PayeeAcctBankName", mainDto.getSpayeeacctbankname());//收款人银行
			vouchermap.put("PayAcctNo", mainDto.getSpayacctno());//付款人账号
			vouchermap.put("PayAcctName", mainDto.getSpayacctname());//付款人名称
			vouchermap.put("PayAcctBankName", mainDto.getSpayacctbankname());//付款人银行
			vouchermap.put("PaySummaryCode", "");//用途编码
			vouchermap.put("PaySummaryName", "");//用途名称
			vouchermap.put("PayAmt",  MtoCodeTrans.transformString(mainDto.getNmoney()));//拨款金额
			vouchermap.put("AgencyCode", "");//基层预算单位编码
			vouchermap.put("AGencyName", "");//基层预算单位名称			
			vouchermap.put("Hold1", "");//预留字段1		
			vouchermap.put("Hold2", "");//预留字段2					
			return map;	
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException("组装凭证报文异常！",e);
		}
	}
		
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {		
		return null;
	}	
}

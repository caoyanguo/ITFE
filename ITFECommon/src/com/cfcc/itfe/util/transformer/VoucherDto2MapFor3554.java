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
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 商业清算对账单（3554）
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
public class VoucherDto2MapFor3554 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3554.class);

	/**
	 * 凭证生成
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto dto) throws ITFEBizException{
		List list=new VoucherDto2MapFor3551().findCamtkindDAndPayBankCode(dto);
		if(list.size()==0)
			return list;
		List lists=new ArrayList();
		for(TnConpaycheckbillDto mainDto:(List<TnConpaycheckbillDto>)list){		
			lists.add(voucherTranfor(dto, new VoucherDto2MapFor3551().findMainDto(dto, mainDto)));			
		}return voucherGenerate(lists);
	}
	
	/**
	 * 凭证报文发送MQ后台处理
	 * 凭证写入数据库
	 * @param lists
	 * @return
	 * @throws ITFEBizException
	 */
	private List voucherGenerate(List lists) throws ITFEBizException{
		for(List list:(List<List>)lists){
			VoucherUtil.sendTips((TvVoucherinfoDto)list.get(0), (Map)list.get(2));
			try {
				DatabaseFacade.getODB().create((TvVoucherinfoDto)list.get(0));
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("凭证写入数据库异常！",e);
			}
		}int count=lists.size();
		lists.clear();
		lists.add(count);
		return lists;
	}
	
	/**
	 * 生成凭证
	 * 生成凭证报文
	 * @param vDto
	 * @param list
	 * @throws ITFEBizException
	 */
	private List voucherTranfor(TvVoucherinfoDto vDto,List list) throws ITFEBizException {		
		TvVoucherinfoDto dto=(TvVoucherinfoDto) vDto.clone();	
		dto.setSstyear(TimeFacade.getCurrentStringTime().substring(0, 4));			
		dto.setSvoucherflag("1");				
		dto.setSdealno(VoucherUtil.getGrantSequence());
		dto.setSfilename(VoucherUtil.getVoucherFileName(dto.getSvtcode(), dto.getScreatdate(), dto.getSdealno()));
		dto.setSvoucherno(dto.getSdealno());
		dto.setSpaybankcode(((TnConpaycheckbillDto)list.get(0)).getSbnkno());
		dto.setShold2((Integer.parseInt(((TnConpaycheckbillDto)list.get(0)).getCamtkind())-1)+"");
		dto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
		dto.setSdemo("处理成功");		
		List lists=new ArrayList();
		lists.add(dto);
		lists.add(list);		
		lists.add(tranfor(lists));	
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
	public Map tranfor(List lists) throws ITFEBizException{
		try{
			BigDecimal Total=new BigDecimal("0.00");
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) lists.get(0);
			List<TnConpaycheckbillDto> resultList=(List) lists.get(1);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("Id", vDto.getSdealno());//清算对账单Id
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear", vDto.getSstyear());//业务年度		
			vouchermap.put("VtCode", vDto.getSvtcode());//凭证类型编号
			vouchermap.put("EVoucherType", ((TnConpaycheckbillDto) resultList.get(0)).getCamtkind());//对账单类型  1直接支付清算 2授权支付清算
			vouchermap.put("VouDate", vDto.getScreatdate());//凭证日期		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());//凭证号		
			vouchermap.put("BeginDate", vDto.getShold3());
			vouchermap.put("EndDate", vDto.getShold4());
			vouchermap.put("AllNum", resultList.size());//明细总笔数			
			vouchermap.put("PayBankCode", vDto.getSpaybankcode());//代理银行编码
			vouchermap.put("PayBankName", PublicSearchFacade.findPayBankNameByPayBankCode((TnConpaycheckbillDto) resultList.get(0)));//代理银行名称
			vouchermap.put("PayBankNo", vDto.getSpaybankcode());//代理银行行号
			vouchermap.put("PayTypeCode", "");//支付方式编码
			vouchermap.put("PayTypeName", "");//支付方式名称
			vouchermap.put("Hold1", "");//预留字段1		
			vouchermap.put("Hold2", "");//预留字段2	
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail= new ArrayList<Object>();			
			for(TnConpaycheckbillDto mainDto:resultList){
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("SupDepCode", mainDto.getSbdgorgcode());//一级预算单位编码
				Detailmap.put("SupDepName", mainDto.getSbdgorgname());//一级预算单位名称
				Detailmap.put("ExpFuncCode",mainDto.getSfuncsbtcode());//支出功能分类科目编码 
				Detailmap.put("ExpFuncName", PublicSearchFacade.findExpFuncNameByExpFuncCode(mainDto)); //支出功能分类科目名称
				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(mainDto.getFcurreckzeroamt()));//本期累计金额
				Detailmap.put("XCheckResult", "");//对账结果
				Detailmap.put("XCurFinReckMoney", "");//本期银行清算额度
				Detailmap.put("XDiffMoney", "");//差额 
				Detailmap.put("Remark", "");//备注				
				Detailmap.put("Hold1", "");//预留字段1 
				Detailmap.put("Hold2", "");//预留字段2 							
				Detail.add(Detailmap);
				Total=Total.add((BigDecimal)mainDto.getFcurreckzeroamt());
			}
			vouchermap.put("AllAmt", MtoCodeTrans.transformString(Total));//明细总笔数
			vDto.setNmoney(Total);
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

	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {		
		return null;
	}
	

}

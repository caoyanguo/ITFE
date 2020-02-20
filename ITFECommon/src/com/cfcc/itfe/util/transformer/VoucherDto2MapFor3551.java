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
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanknameDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 国库与财政集中支付额度余额对账单（3551）
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
public class VoucherDto2MapFor3551 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3551.class);

	/**
	 * 凭证生成
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	 */
	public List voucherGenerate(TvVoucherinfoDto dto) throws ITFEBizException{
		List list=findCamtkindDAndPayBankCode(dto);
		if(list.size()==0)
			return list;
		List lists=new ArrayList();
		for(TnConpaycheckbillDto mainDto:(List<TnConpaycheckbillDto>)list){		
			lists.add(voucherTranfor(dto, findMainDto(dto, mainDto)));			
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
		dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		dto.setSdemo("处理成功");		
		List lists=new ArrayList();
		lists.add(dto);
		lists.add(list);		
		lists.add(tranfor(lists));	
		return lists;
	}
	
	/**
	 * 根据组织机构代码、国库代码、银行代码、支付方式、起始日期、结束日期
	 * 查询集中支付额度对账单业务表信息
	 * @param dto
	 * @param tnConpaycheckbillDto
	 * @return
	 * @throws ITFEBizException
	 */
	public List findMainDto(TvVoucherinfoDto dto,TnConpaycheckbillDto tnConpaycheckbillDto) throws ITFEBizException {		
		TnConpaycheckbillDto mainDto=new TnConpaycheckbillDto();
		mainDto.setSbookorgcode(dto.getSorgcode());
		mainDto.setStrecode(tnConpaycheckbillDto.getStrecode());
		mainDto.setSbnkno(tnConpaycheckbillDto.getSbnkno());
		mainDto.setCamtkind(tnConpaycheckbillDto.getCamtkind());
		mainDto.setDstartdate(CommonUtil.strToDate(dto.getShold3()));
		mainDto.setDenddate(CommonUtil.strToDate(dto.getShold4()));
		try {
			return CommonFacade.getODB().findRsByDto(mainDto);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询集中支付额度对账单业务表信息出错！",e);
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("查询集中支付额度对账单业务表信息出错！",e);
		}
	}
	
	/**
	 * 查找根据国库代码、支付方式和银行代码分组SQL语句
	 * @param dto
	 * @return
	 */
	private String findCamtkindDAndPayBankCodeSQL(TvVoucherinfoDto dto){
		String sql= "SELECT S_TRECODE,C_AMTKIND,S_BNKNO FROM TN_CONPAYCHECKBILL ";
		sql+= "WHERE S_BOOKORGCODE='"+dto.getSorgcode()+"' ";
		sql+= dto.getStrecode()==null?"":"AND S_TRECODE='"+dto.getStrecode()+"' ";
		sql+= dto.getShold2()==null?"":"AND C_AMTKIND='"+(Integer.parseInt(dto.getShold2())+1)+"' ";
		sql+= dto.getSpaybankcode()==null?"":"AND S_BNKNO='"+dto.getSpaybankcode()+"' ";
		sql+= "AND D_STARTDATE='"+CommonUtil.strToDate(dto.getShold3())+"' AND D_ENDDATE='"+CommonUtil.strToDate(dto.getShold4())+"' ";
		return sql+= "GROUP BY S_TRECODE,C_AMTKIND,S_BNKNO ";
	}
	
	/**
	 * 查找国库代码、支付方式和银行代码
	 * @param dto
	 * @return
	 * @throws ITFEBizException
	 */
	public List findCamtkindDAndPayBankCode(TvVoucherinfoDto dto) throws ITFEBizException{
		try {
			SQLExecutor sqlExec= DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			SQLResults rs = sqlExec.runQueryCloseCon(findCamtkindDAndPayBankCodeSQL(dto),TnConpaycheckbillDto.class);			
			return (List) rs.getDtoCollection();
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询集中支付额度对账单业务表信息出错！",e);
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
			List<TnConpaycheckbillDto> resultList=(List) lists.get(1);
			//取得国库名称
			String treName = BusinessFacade
					.findTreasuryInfo(vDto.getSorgcode()).get(
							vDto.getStrecode()) == null ? "" : BusinessFacade
					.findTreasuryInfo(vDto.getSorgcode()).get(
							vDto.getStrecode()).getStrename();
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
			vouchermap.put("TreCode", vDto.getStrecode());//国库代码
			vouchermap.put("TreName", treName);//国库名称
			vouchermap.put("BeginDate", vDto.getShold3());//对账起始日期
			vouchermap.put("EndDate", vDto.getShold4());//对账终止日期
			vouchermap.put("AllNum", resultList.size());//明细总笔数			
			vouchermap.put("PayBankCode", vDto.getSpaybankcode());//代理银行编码
			vouchermap.put("PayBankName", PublicSearchFacade.findPayBankNameByPayBankCode((TnConpaycheckbillDto) resultList.get(0)));//代理银行名称
			vouchermap.put("PayBankNo", vDto.getSpaybankcode());//代理银行行号
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
				Detailmap.put("PreDateMoney", MtoCodeTrans.transformString(mainDto.getFlastmonthzeroamt()));//上期额度余额
				Detailmap.put("CurAddMoney", MtoCodeTrans.transformString(mainDto.getFcursmallamt()));//本期增加额度
				Detailmap.put("CurReckMoney", MtoCodeTrans.transformString(mainDto.getFcurreckzeroamt()));//本期已清算额度
				Detailmap.put("CurDateMoney", MtoCodeTrans.transformString(mainDto.getFcurzeroamt()));//本期额度余额
				Detailmap.put("XCheckResult", "");//对账结果
				Detailmap.put("XCurFinReckMoney", "");//本期财政清算额度
				Detailmap.put("XCurFinAddMoney", "");//本期财政增加额度 
				Detailmap.put("Remark", "");//备注				
				Detailmap.put("Hold1", "");//预留字段1 
				Detailmap.put("Hold2", "");//预留字段2
				Detailmap.put("Hold3", "");//预留字段3 
				Detailmap.put("Hold4", "");//预留字段4 							
				Detail.add(Detailmap);
				Total=Total.add((BigDecimal)mainDto.getFcurzeroamt());
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

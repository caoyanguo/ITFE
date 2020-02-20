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
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TfReportDefrayMainDto;
import com.cfcc.itfe.persistence.dto.TfReportDefraySubDto;
import com.cfcc.itfe.persistence.dto.TnConpaycheckpaybankDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 支出报表(3512)转化
 * 
 * @author renqingbin
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3582 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3582.class);
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{
		vDto.setSdealno(VoucherUtil.getGrantSequence());
		return getVoucher(vDto);
	}
	private List getVoucher(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List returnList = new ArrayList();
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		SQLExecutor execDetail = null;
		try {
			cDto.setSorgcode(vDto.getSorgcode());		
			cDto.setStrecode(vDto.getStrecode());
			List<TsConvertfinorgDto> tsConvertfinorgList=(List<TsConvertfinorgDto>) CommonFacade.getODB().findRsByDto(cDto);
			if(tsConvertfinorgList==null||tsConvertfinorgList.size()==0){
				throw new ITFEBizException("国库："+vDto.getStrecode()+"对应的财政机关代码参数未维护！");
			}
			cDto=(TsConvertfinorgDto) tsConvertfinorgList.get(0);
			vDto.setSadmdivcode(cDto.getSadmdivcode());
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){				
				throw new ITFEBizException("国库："+cDto.getStrecode()+"对应的区划代码未维护！");
			}
		} catch (JAFDatabaseException e2) {		
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		}
		
		String stockDayRptDetailSql="SELECT * FROM HTR_TAXORG_PAYOUT_REPORT WHERE S_RPTDATE >= ? AND S_RPTDATE <= ? ";
		if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode()))
			stockDayRptDetailSql+=" and S_TRECODE = ? ";
		if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))
			stockDayRptDetailSql+=" and S_BUDGETTYPE = ? ";
		if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))
			stockDayRptDetailSql+=" and S_TAXORGCODE = ? ";
		stockDayRptDetailSql+=" ORDER BY S_RPTDATE ASC";
		List<IDto> detailList=new ArrayList<IDto>();
		try {
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			if(vDto.getSext5()!=null&&"35122".equals(vDto.getSext5()))
			{
				String sql = "select * from TN_CONPAYCHECKPAYBANK where s_orgcode=? and s_trecode=? and s_bankcode=? and s_bgtlevel=? and s_bgttypecode=? and s_paytypecode=? and s_ext2=? order by S_SUBJECTCODE ";
				execDetail.addParam(vDto.getSorgcode());//机构代码
				execDetail.addParam(vDto.getStrecode());//国库代码
				execDetail.addParam(vDto.getSpaybankcode());//代理银行
				execDetail.addParam(vDto.getSext3());//辖属标志
				execDetail.addParam(vDto.getSext2());//预算种类
				execDetail.addParam(vDto.getSext1());//支付方式
				execDetail.addParam(vDto.getSext4());//月份
				detailList.addAll((List<TnConpaycheckpaybankDto>)execDetail.runQueryCloseCon(sql,TnConpaycheckpaybankDto.class).getDtoCollection());
				if(detailList==null||detailList.size()==0)
					return null;
			}else
			{
				execDetail.addParam(vDto.getScheckdate());
				execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode()))
					execDetail.addParam(vDto.getStrecode());
				if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))
					execDetail.addParam(vDto.getShold1());
				if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))
					execDetail.addParam(vDto.getScheckvouchertype());
				detailList=  (List<IDto>) execDetail.runQuery(stockDayRptDetailSql,TrTaxorgPayoutReportDto.class).getDtoCollection();
				stockDayRptDetailSql=StringUtil.replace(stockDayRptDetailSql, "HTR_TAXORG_PAYOUT_REPORT", "TR_TAXORG_PAYOUT_REPORT");
				execDetail.addParam(vDto.getScheckdate());
				execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode()))
					execDetail.addParam(vDto.getStrecode());
				if(vDto.getShold1()!=null&&!"".equals(vDto.getShold1()))
					execDetail.addParam(vDto.getShold1());
				if(vDto.getScheckvouchertype()!=null&&!"".equals(vDto.getScheckvouchertype()))
					execDetail.addParam(vDto.getScheckvouchertype());
				detailList.addAll((List<TrTaxorgPayoutReportDto>)execDetail.runQueryCloseCon(stockDayRptDetailSql,TrTaxorgPayoutReportDto.class).getDtoCollection());
				if(detailList==null||detailList.size()==0){
					return null;
				}
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查询"+stockDayRptDetailSql+"库存明细信息异常！",e);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		List<List> sendList = this.getSubLists(detailList, 500);
		if(sendList!=null&&sendList.size()>0)
		{
			List<IDto> tempList = null;
			String danhao=null;
			for(int i=0;i<sendList.size();i++)
			{
				tempList = sendList.get(i);
				//3512-dto.shold1预算种类dto.scheckvouchertype报表种类dto.spaybankcode对帐截止日期dto.scheckdate对帐起始日期dto.screatdate凭证日期dto.strecode国库代码
				//TrTaxorgPayoutReportDto dto=new TrTaxorgPayoutReportDto();dto.setStrecode(vDto.getStrecode());//国库代码dto.setSbudgettype(vDto.getShold1());//预算种类
				//dto.setStaxorgcode(vDto.getScheckvouchertype());//报表种类-数据库存的一般预算支出日报表1-实拨资金日报表2-调拨支出日报表3--凭证库规范-1一般预算支出报表-2调拨支出
				String FileName=null;
				String dirsep = File.separator; 
				String mainvou=VoucherUtil.getGrantSequence();
				vDto.setSdealno(mainvou);
				vDto.setSvoucherno(mainvou);
				if(danhao==null)
					danhao=mainvou;
				FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+mainvou+ ".msg";
				TvVoucherinfoDto dto=new TvVoucherinfoDto();			
				dto.setSorgcode(vDto.getSorgcode());
				dto.setSadmdivcode(vDto.getSadmdivcode());
				dto.setSvtcode(vDto.getSvtcode());
				dto.setScreatdate(TimeFacade.getCurrentStringTime());
				dto.setStrecode(vDto.getStrecode());
				dto.setSfilename(FileName);
				dto.setSdealno(mainvou);
				dto.setSattach(danhao);//对账单号
				dto.setShold3(String.valueOf(sendList.size()));//包总数
				dto.setShold4(String.valueOf(i+1));//本包序号
				dto.setSstyear(dto.getScreatdate().substring(0, 4));				
				dto.setScheckdate(vDto.getScheckdate());
				dto.setSpaybankcode(vDto.getSpaybankcode()==null?"":vDto.getSpaybankcode());
				dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				dto.setSdemo("处理成功");
				dto.setSvoucherflag("1");
				dto.setSvoucherno(mainvou);	
				if(vDto.getSext5()!=null&&"35122".equals(vDto.getSext5()))
				{
					dto.setSext1(vDto.getSext1());
					dto.setSext2(vDto.getSext2());
					dto.setSext3(vDto.getSext3());
					dto.setSext4(vDto.getSext4());
					dto.setSext5(vDto.getSext5());
					dto.setShold3(vDto.getShold3());
					dto.setShold4(vDto.getShold4());
				}else
				{
					dto.setShold1(vDto.getShold1()==null?"":vDto.getShold1());//预算种类
					dto.setScheckvouchertype(vDto.getScheckvouchertype()==null?"":vDto.getScheckvouchertype());//报表种类
					dto.setIcount(tempList.size());
					dto.setSext1("1");//发起方式1:人行发起,2:财政发起,3:商行发起
				}
				List mapList = new ArrayList();
				mapList.add(vDto);
				mapList.add(cDto);
				mapList.add(tempList);
				List<IDto> idtoList = new ArrayList<IDto>();
				Map map=null;
				if(vDto.getSext5()!=null&&"35122".equals(vDto.getSext5()))
					map = tranfor35122(mapList,sendList.size(),i+1,danhao,idtoList);
				else
					map = tranfor(mapList,sendList.size(),i+1,danhao,idtoList);
				dto.setIcount(tempList.size());
				String amt = ((Map)map.get("Voucher")).get("AllAmt").toString();
				dto.setNmoney(new BigDecimal(amt));
				List vouList=new ArrayList();
				vouList.add(map);
				vouList.add(dto);
				vouList.add(idtoList);
				returnList.add(vouList);
			}
		}
		return returnList;
	}
	private Map tranfor(List mapList,int count,int xuhao,String danhao,List<IDto> idtoList) throws ITFEBizException {
		
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) mapList.get(0);
			TsConvertfinorgDto cDto = (TsConvertfinorgDto)mapList.get(1);
			List<TrTaxorgPayoutReportDto> detailList=(List<TrTaxorgPayoutReportDto>) mapList.get(2);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("AdmDivCode",vDto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear",vDto.getScreatdate().substring(0,4));//业务年度
			vouchermap.put("VtCode",vDto.getSvtcode());//凭证类型编号
			vouchermap.put("VouDate",vDto.getScreatdate());//凭证日期
			vouchermap.put("VoucherNo",vDto.getSvoucherno());//凭证号
			vouchermap.put("VoucherCheckNo",danhao);//对账单号
			vouchermap.put("ChildPackNum",count);//子包总数
			vouchermap.put("CurPackNo",xuhao);//本包序号
			vouchermap.put("BillKind","1".equals(vDto.getScheckvouchertype())?"1":"2");//报表种类1,对应1-一般预算支出报表3对应2-调拨支出
			vouchermap.put("FinOrgCode",cDto.getSfinorgcode());//财政机关代码
			vouchermap.put("TreCode",vDto.getStrecode());//国库主体代码
			vouchermap.put("TreName",SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());
			vouchermap.put("BgtTypeCode",vDto.getShold1());//预算类型编码
			vouchermap.put("BgtTypeName","1".equals(vDto.getShold1())?"预算内":"预算外");//预算类型名称
			vouchermap.put("ClearBankCode","");//人民银行编码
			vouchermap.put("ClearBankName","");//人民银行名称
			vouchermap.put("ClearAccNo","");//拨款账号
			vouchermap.put("ClearAccNanme","");//拨款账户名称
			vouchermap.put("BeginDate",vDto.getScheckdate());//对账起始日期
			vouchermap.put("EndDate",vDto.getSpaybankcode());//对账终止日期
			vouchermap.put("AllNum",detailList.size());//总笔数
			vouchermap.put("Hold1","");//预留字段1
			vouchermap.put("Hold2",vDto.getSverifyusercode()==null?"":vDto.getSverifyusercode());//预留字段2
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			TsTreasuryDto trdto = SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode());
			for (TrTaxorgPayoutReportDto dto:detailList) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",vDto.getSdealno()+(++id));//序号
				Detailmap.put("ExpFuncCode",dto.getSbudgetsubcode());//支出功能分类科目编码
				Detailmap.put("ExpFuncName",dto.getSbudgetsubname());//支出功能分类科目名称
				Detailmap.put("CurPayAmt",String.valueOf(dto.getNmoneyday()));//本期金额
				Detailmap.put("SumPayAmt",String.valueOf(dto.getNmoneyyear()));//累计金额
				if(vDto.getSorgcode().startsWith("13")){//福建对账
					Detailmap.put("Hold1","001001");//预留字段1
					Detailmap.put("Hold2",trdto.getStrename());//预留字段2
				}else
				{
					Detailmap.put("Hold1","");//预留字段1
					Detailmap.put("Hold2","");//预留字段2
				}
				
				Detailmap.put("Hold3","");//预留字段3
				Detailmap.put("Hold4","");//预留字段4
				if(dto.getSbudgetsubcode().length()==3)
					allamt=allamt.add(dto.getNmoneyday());
				Detail.add(Detailmap);
				subdtolist.add(getSubDto(Detailmap,vouchermap));
			}
			vDto.setNmoney(allamt);
			vouchermap.put("AllAmt",MtoCodeTrans.transformString(allamt));
			idtoList.add(getMainDto(vouchermap,vDto));
			idtoList.addAll(subdtolist);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
	}
	private Map tranfor35122(List mapList,int count,int xuhao,String danhao,List<IDto> idtoList) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) mapList.get(0);
			TsConvertfinorgDto cDto = (TsConvertfinorgDto)mapList.get(1);
			List<TnConpaycheckpaybankDto> detailList=(List<TnConpaycheckpaybankDto>) mapList.get(2);
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("AdmDivCode",vDto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear",vDto.getScreatdate().substring(0,4));//业务年度
			vouchermap.put("VtCode",vDto.getSvtcode());//凭证类型编号
			vouchermap.put("VouDate",vDto.getScreatdate());//凭证日期
			vouchermap.put("VoucherNo",vDto.getSvoucherno());//凭证号
			vouchermap.put("VoucherCheckNo",danhao);//对账单号
			vouchermap.put("ChildPackNum",count);//子包总数
			vouchermap.put("CurPackNo",xuhao);//本包序号
			vouchermap.put("BillKind","1");//一般预算支出
			vouchermap.put("BelongFlag", vDto.getSext3());//辖属标志
			vouchermap.put("FinOrgCode",cDto.getSfinorgcode());//财政机关代码
			vouchermap.put("TreCode",vDto.getStrecode());//国库主体代码
			vouchermap.put("TreName",SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode())==null?"":SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()).getStrename());
			vouchermap.put("BgtTypeCode",vDto.getSext2());//预算类型编码
			vouchermap.put("BgtTypeName","1".equals(vDto.getSext2())?"预算内":"预算外");//预算类型名称
			vouchermap.put("PayTypeCode","1".equals(vDto.getSext1())?"12":"11");//支付方式编码
			vouchermap.put("PayTypeName","1".equals(vDto.getSext1())?"授权支付":"直接支付");//人民银行名称
			vouchermap.put("ClearBankCode","");//人民银行编码
			vouchermap.put("ClearBankName","");//人民银行名称
			vouchermap.put("ClearAccNo","");//拨款账号
			vouchermap.put("ClearAccNanme","");//拨款账户名称
			vouchermap.put("BeginDate",vDto.getShold3());//对账起始日期
			vouchermap.put("EndDate",vDto.getShold4());//对账终止日期
			vouchermap.put("AllNum",detailList.size());//总笔数
			vouchermap.put("AllAmt",new BigDecimal(0));//总金额
			vouchermap.put("Hold1",vDto.getSpaybankcode());//预留字段1
			vouchermap.put("Hold2","");//预留字段2
			BigDecimal allamt=new BigDecimal("0.00");	
			BigDecimal allmonthamt=new BigDecimal("0.00");
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			TsTreasuryDto trdto = SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode());
			for (TnConpaycheckpaybankDto dto:detailList) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",vDto.getSdealno()+(++id));//序号
				Detailmap.put("ExpFuncCode",dto.getSsubjectcode());//支出功能分类科目编码
				Detailmap.put("ExpFuncName",dto.getSsubjectname());//支出功能分类科目名称
				Detailmap.put("CurPayAmt",String.valueOf(dto.getNmonthamt()));//本期金额
				Detailmap.put("SumPayAmt",String.valueOf(dto.getNyearamt()));//累计金额
				if(vDto.getSorgcode().startsWith("13")){//福建对账
					Detailmap.put("Hold1","001001");//预留字段1
					Detailmap.put("Hold2",trdto.getStrename());//预留字段2
				}else
				{
					Detailmap.put("Hold1","");//预留字段1
					Detailmap.put("Hold2","");//预留字段2
				}
				
				Detailmap.put("Hold3","");//预留字段3
				Detailmap.put("Hold4","");//预留字段4
				if(dto.getSsubjectcode().length()==3)
				{
					allamt=allamt.add(dto.getNyearamt());
					allmonthamt = allmonthamt.add(dto.getNmonthamt());
				}
				Detail.add(Detailmap);
				subdtolist.add(getSubDto(Detailmap,vouchermap));
			}
			vDto.setNmoney(allamt);
			vouchermap.put("AllAmt",MtoCodeTrans.transformString(allamt));
			vouchermap.put("Hold2",MtoCodeTrans.transformString(allmonthamt));
			idtoList.add(getMainDto(vouchermap,vDto));
			idtoList.addAll(subdtolist);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
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
	public static Map tranfor(List<TrTaxorgPayoutReportDto> list, TvVoucherinfoDto vDto) throws ITFEBizException{
		try{
			HashMap<String, Object> returnmap = new HashMap<String, Object>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			//取得国库名称
			String treName = BusinessFacade
					.findTreasuryInfo(vDto.getSorgcode()).get(
							vDto.getStrecode()) == null ? "" : BusinessFacade
					.findTreasuryInfo(vDto.getSorgcode()).get(
							vDto.getStrecode()).getStrename();
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("AdmDivCode","");//行政区划代码
			vouchermap.put("StYear","");//业务年度
			vouchermap.put("VtCode","");//凭证类型编号
			vouchermap.put("VouDate","");//凭证日期
			vouchermap.put("VoucherNo","");//凭证号
			vouchermap.put("VoucherCheckNo","");//对账单号
			vouchermap.put("ChildPackNum","");//子包总数
			vouchermap.put("CurPackNo","");//本包序号
			vouchermap.put("BillKind","");//报表种类
			vouchermap.put("FinOrgCode","");//财政机关代码
			vouchermap.put("TreCode","");//国库主体代码
			vouchermap.put("TreName","");//国库主体名称
			vouchermap.put("BgtTypeCode","");//预算类型编码
			vouchermap.put("BgtTypeName","");//预算类型名称
			vouchermap.put("BeginDate","");//对账起始日期
			vouchermap.put("EndDate","");//对账终止日期
			vouchermap.put("AllNum","");//总笔数
			vouchermap.put("AllAmt","");//总金额
			vouchermap.put("Hold1","");//预留字段1
			vouchermap.put("Hold2","");//预留字段2
			vouchermap.put("Id", vDto.getSdealno());
			vouchermap.put("AdmDivCode", vDto.getSadmdivcode());
			vouchermap.put("StYear", vDto.getSstyear());		
			vouchermap.put("VtCode", vDto.getSvtcode());		
			vouchermap.put("VouDate", vDto.getScreatdate());		
			vouchermap.put("VoucherNo", vDto.getSvoucherno());
			vouchermap.put("BillKind", vDto.getScheckvouchertype());//报表种类
			vouchermap.put("ReportDate", vDto.getScheckdate());		
			vouchermap.put("FinOrgCode", vDto.getSpaybankcode());
			vouchermap.put("TreCode", vDto.getStrecode());
			vouchermap.put("TreName", treName);
			vouchermap.put("BgtTypeCode", "");
			vouchermap.put("BgtTypeName", "");
			vouchermap.put("Hold1", "");	
			vouchermap.put("Hold2", "");
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> DetailList = new ArrayList<Object>();
			List<Object> Detail= new ArrayList<Object>();
			for (TrTaxorgPayoutReportDto dto:list) {
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id","");//序号
				Detailmap.put("ExpFuncCode","");//支出功能分类科目编码
				Detailmap.put("ExpFuncName","");//支出功能分类科目名称
				Detailmap.put("CurPayAmt","");//本期金额
				Detailmap.put("SumPayAmt","");//累计金额
				Detailmap.put("Hold1","");//预留字段1
				Detailmap.put("Hold2","");//预留字段2
				Detailmap.put("Hold3","");//预留字段3
				Detailmap.put("Hold4","");//预留字段4
				Detailmap.put("ExpFuncCode", dto.getSbudgetsubcode()); // 区划代码
				Detailmap.put("ExpFuncName", dto.getSbudgetsubname()); 
				Detailmap.put("DayAmt", MtoCodeTrans.transformString(dto.getNmoneyday())); 
				Detailmap.put("TenDayAmt", MtoCodeTrans.transformString(dto.getNmoneytenday())); 
				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(dto.getNmoneymonth())); 
				Detailmap.put("QuarterAmt", MtoCodeTrans.transformString(dto.getNmoneyquarter())); 
				Detailmap.put("YearAmt",MtoCodeTrans.transformString(dto.getNmoneyyear()));
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", ""); 
				if(dto.getSbudgetsubcode().length()==3)
					allamt=allamt.add(dto.getNmoneyday());
				Detail.add(Detailmap);
			}		
			vouchermap.put("SumMoney",MtoCodeTrans.transformString(allamt));		
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			DetailList.add(DetailListmap);
			vouchermap.put("DetailList", DetailList);
			returnmap.put("SumMoney", allamt);
			returnmap.put("Map", map);
			return returnmap;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
		
	}
	private List getSubLists(List list,int subsize)
	{
		List getList = null;
		if(list!=null&&list.size()>0)
		{
			if(subsize<1)
				subsize=500;
			int count = list.size()/subsize;
			int yu = list.size()%subsize;
			getList = new ArrayList();
			for(int i=0;i<count;i++)
				getList.add(list.subList(i*subsize, subsize*(i+1)));
			if(yu>0)
				getList.add(list.subList(count*subsize, (count*subsize)+yu));
		}
		return getList;
	}
	private TfReportDefrayMainDto getMainDto(Map<String, Object> mainMap,TvVoucherinfoDto vDto)
	{
		TfReportDefrayMainDto mainDto = new TfReportDefrayMainDto();
		mainDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
		mainDto.setSdemo("处理成功");
		mainDto.setSorgcode(vDto.getSorgcode());
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		mainDto.setIvousrlno(Long.valueOf(voucherno));
		mainDto.setSadmdivcode(getString(mainMap,"AdmDivCode"));//AdmDivCode",vDto.getSadmdivcode());//行政区划代码
		mainDto.setSstyear(getString(mainMap,"StYear"));//StYear",vDto.getScreatdate().substring(0,4));//业务年度
		mainDto.setSvtcode(getString(mainMap,"VtCode"));//VtCode",vDto.getSvtcode());//凭证类型编号
		mainDto.setSvoudate(getString(mainMap,"VouDate"));//VouDate",vDto.getScreatdate());//凭证日期
		mainDto.setSvoucherno(getString(mainMap,"VoucherNo"));//VoucherNo",vDto.getSvoucherno());//凭证号
		mainDto.setSvouchercheckno(getString(mainMap,"VoucherCheckNo"));//VoucherCheckNo",danhao);//对账单号
		mainDto.setSchildpacknum(getString(mainMap,"ChildPackNum"));//ChildPackNum",count);//子包总数
		mainDto.setScurpackno(getString(mainMap,"CurPackNo"));//CurPackNo",xuhao);//本包序号
		mainDto.setSbillkind(getString(mainMap,"BillKind"));//BillKind","1".equals(vDto.getScheckvouchertype())?"1":"2");//报表种类1,对应1-一般预算支出报表3对应2-调拨支出
		mainDto.setSfinorgcode(getString(mainMap,"FinOrgCode"));//FinOrgCode",cDto.getSfinorgcode());//财政机关代码
		mainDto.setStrecode(getString(mainMap,"TreCode"));//TreCode",vDto.getStrecode());//国库主体代码
		mainDto.setStrename(getString(mainMap,"TreName"));//TreName"
		mainDto.setSbgttypecode(getString(mainMap,"BgtTypeCode"));//BgtTypeCode",vDto.getShold1());//预算类型编码
		mainDto.setSbgttypename(getString(mainMap,"BgtTypeName"));//BgtTypeName","1".equals(vDto.getShold1())?"预算内":"预算外");//预算类型名称
		mainDto.setSbegindate(getString(mainMap,"BeginDate"));//BeginDate",vDto.getScheckdate());//对账起始日期
		mainDto.setSenddate(getString(mainMap,"EndDate"));//EndDate",vDto.getSpaybankcode());//对账终止日期
		mainDto.setSallnum(getString(mainMap,"AllNum"));//AllNum",detailList.size());//总笔数
		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,"AllAmt")));//AllAmt","");//总金额
		mainDto.setShold1(getString(mainMap,"Hold1"));//Hold1","");//预留字段1
		mainDto.setShold2(getString(mainMap,"Hold2"));//Hold2","");//预留字段2
		mainDto.setSext1("1");//发起方式1:人行发起,2:财政发起,3:商行发起
		return mainDto;
	}
	private TfReportDefraySubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
	{
		TfReportDefraySubDto subDto = new TfReportDefraySubDto();
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		subDto.setIvousrlno(Long.valueOf(voucherno));
		String id = getString(subMap,"Id");
		if(id.length()>19)
			id = id.substring(id.length()-19);
		subDto.setIseqno(Long.valueOf(id));//Id",vDto.getSdealno()+(++id));//序号
		subDto.setSid(getString(subMap,"Id"));
		subDto.setSexpfunccode(getString(subMap,"ExpFuncCode"));//ExpFuncCode",dto.getSbudgetsubcode());//支出功能分类科目编码
		subDto.setSexpfuncname(getString(subMap,"ExpFuncName"));//ExpFuncName",dto.getSbudgetsubname());//支出功能分类科目名称
		subDto.setNcurpayamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"CurPayAmt")));//CurPayAmt",dto.getNmoneyday());//本期金额
		subDto.setNsumpayamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"SumPayAmt")));//SumPayAmt",dto.getNmoneymonth());//累计金额
		subDto.setShold1(getString(subMap,"Hold1"));//Hold1","");//预留字段1
		subDto.setShold2(getString(subMap,"Hold2"));//Hold2","");//预留字段2
		subDto.setShold3(getString(subMap,"Hold3"));//Hold3","");//预留字段3
		subDto.setShold4(getString(subMap,"Hold4"));//Hold4","");//预留字段4
		subDto.setSxcheckresult("0");//对账结果默认成功
		return subDto;
	}
	private String getString(Map datamap,String key)
	{
		if(datamap==null||key==null)
			return "";
		else
			return String.valueOf(datamap.get(key));
	}
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {
		return null;
	}
}

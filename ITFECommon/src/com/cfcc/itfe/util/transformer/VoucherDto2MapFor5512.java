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
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfReportDefrayMainDto;
import com.cfcc.itfe.persistence.dto.TfReportDefraySubDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 * 人行支出报表对账请求3512回执5512转化
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor5512 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5512.class);
											
	/**
	 * 凭证生成
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	*/
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		if(vDto==null)
			return null;
		return getVoucher(vDto);	
	}
	private List getVoucher(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List returnList = new ArrayList();
		try {
			MainAndSubDto datadto =  get3512Data(vDto);
			List<IDto> alldata = getDataList(datadto);
			Map<String,Object> getMap = getSplitPack(alldata);
			if(getMap!=null&&getMap.size()>0&&datadto!=null&&datadto.getMainDto()!=null)
			{
				String FileName=null;
				String dirsep = File.separator; 
				String mainvou=VoucherUtil.getGrantSequence();
				FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+mainvou+ ".msg";
				TvVoucherinfoDto dto=new TvVoucherinfoDto();			
				dto.setSorgcode(vDto.getSorgcode());
				dto.setSadmdivcode(vDto.getSadmdivcode());
				dto.setSvtcode(MsgConstant.VOUCHER_NO_5512);
				dto.setScreatdate(TimeFacade.getCurrentStringTime());
				dto.setStrecode(vDto.getStrecode());
				dto.setSfilename(FileName);
				dto.setSdealno(mainvou);		
				dto.setSstyear(dto.getScreatdate().substring(0, 4));				
				dto.setScheckdate(vDto.getScheckdate());
				dto.setSpaybankcode(vDto.getSpaybankcode()==null?"":vDto.getSpaybankcode());
				dto.setShold3(vDto.getShold3());
				dto.setShold4(vDto.getShold4());
				dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				dto.setSdemo("处理成功");
				dto.setSvoucherflag("1");
				dto.setSvoucherno(mainvou);	
				dto.setIcount(getMap.size());
				dto.setSext1(vDto.getSdealno());
				List<IDto> idtoList = new ArrayList<IDto>();
				Map map=tranfor(datadto,getMap,dto);
				List vouList=new ArrayList();
				vouList.add(map);
				vouList.add(dto);
				vouList.add(idtoList);
				returnList.add(vouList);
			}
		} catch (Exception e2) {		
			logger.error(e2);
			throw new ITFEBizException("信息异常！",e2);
		}
		return returnList;
	}
	private Map tranfor(MainAndSubDto datadto,Map<String,Object> getMap,TvVoucherinfoDto dto) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TfReportDefrayMainDto main3512dto = (TfReportDefrayMainDto)datadto.getMainDto();
			TfReportDefraySubDto sub3512dto = null;
			map.put("Voucher", vouchermap);// 设置报文节点 Voucher
			// 设置报文消息体 
			vouchermap.put("AdmDivCode",main3512dto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear",main3512dto.getSstyear());//业务年度
			vouchermap.put("VtCode",MsgConstant.VOUCHER_NO_5512);//凭证类型编号
			vouchermap.put("VouDate",TimeFacade.getCurrentStringTime());//凭证日期
			vouchermap.put("VoucherNo",dto.getSvoucherno());//凭证号
			vouchermap.put("VoucherCheckNo",main3512dto.getSvouchercheckno());//对账单号
			vouchermap.put("ChildPackNum",main3512dto.getSchildpacknum());//子包总数
			vouchermap.put("CurPackNo",main3512dto.getScurpackno());//本包序号
			vouchermap.put("BillKind",main3512dto.getSbillkind());//报表种类
			vouchermap.put("FinOrgCode",main3512dto.getSfinorgcode());//财政机关代码
			vouchermap.put("TreCode",main3512dto.getStrecode());//国库主体代码
			vouchermap.put("TreName",main3512dto.getStrename());//国库主体名称
			vouchermap.put("BgtTypeCode",main3512dto.getSbgttypecode());//预算类型编码
			vouchermap.put("BgtTypeName",main3512dto.getSbgttypename());//预算类型名称
			vouchermap.put("BeginDate",main3512dto.getSbegindate());//对账起始日期
			vouchermap.put("EndDate",main3512dto.getSenddate());//对账终止日期
			vouchermap.put("AllNum",getMap.get("allnum"));//总笔数
			vouchermap.put("AllAmt",String.valueOf(getMap.get("allamt")));//总金额
			List<Object> Detail= new ArrayList<Object>();
			Object tempdto = null;
			int error = 0;
			Map<String,String> tempMap = null;
			for(IDto idto:datadto.getSubDtoList())
			{
				sub3512dto = (TfReportDefraySubDto)idto;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",sub3512dto.getSid());//序号
				Detailmap.put("ExpFuncCode",sub3512dto.getSexpfunccode());//支出功能分类科目编码
				Detailmap.put("ExpFuncName",sub3512dto.getSexpfuncname());//支出功能分类科目名称
				Detailmap.put("CurPayAmt",String.valueOf(sub3512dto.getNcurpayamt()));//本期金额
				Detailmap.put("SumPayAmt",String.valueOf(sub3512dto.getNsumpayamt()));//累计金额
				tempdto = getObject(getMap,sub3512dto);
				if(tempdto==null)
				{
					error++;
					Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
					Detailmap.put("XCheckReason","无此凭证");//不符原因
				}else if(tempdto instanceof Map)
				{
					tempMap = (Map<String,String>)tempdto;
					if(sub3512dto.getNcurpayamt()!=null&&sub3512dto.getNcurpayamt().toString().equals(tempMap.get("CurPayAmt")))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//对账结果
						Detailmap.put("XCheckReason","");//不符原因
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
						Detailmap.put("XCheckReason","金额不符,本方金额:"+tempMap.get("CurPayAmt"));//不符原因
					}
					
				}
				Detailmap.put("Hold1",sub3512dto.getShold1());//预留字段1
				Detailmap.put("Hold2",sub3512dto.getShold2());//预留字段2
				Detailmap.put("Hold3",sub3512dto.getShold3());//预留字段3
				Detailmap.put("Hold4",sub3512dto.getShold4());//预留字段4

				Detail.add(Detailmap);
			}
			if(main3512dto.getSallnum()!=null&&main3512dto.getSallnum().equals(String.valueOf(getMap.get("allcount"))))
			{
				if(main3512dto.getNallamt()!=null&&main3512dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//对账结果
					vouchermap.put("XDiffNum","0");//不符笔数
				}else
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
					vouchermap.put("XDiffNum",error);//不符金额
				}
			}else
			{
				if(main3512dto.getNallamt()!=null&&main3512dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
					vouchermap.put("XDiffNum",error);//不符笔数
				}else
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
					vouchermap.put("XDiffNum",error);//不符笔数
				}
			}
			vouchermap.put("Hold1","");//预留字段1
			vouchermap.put("Hold2","");//预留字段2	
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;		
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
	}
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {		
		return null;
	}
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	private List<IDto> getDataList(MainAndSubDto dto3512) throws ITFEBizException
	{
		List<IDto> getList = null;
		StringBuffer sql = null;
		SQLExecutor execDetail=null;
		TfReportDefrayMainDto vDto = (TfReportDefrayMainDto)dto3512.getMainDto();
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			
			sql = new StringBuffer("SELECT * FROM HTR_INCOMEDAYRPT WHERE S_RPTDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"' ");
			if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode().trim()))
				sql.append(" and S_TRECODE='"+vDto.getStrecode()+"' ");
			if(vDto.getSbgttypecode()!=null&&!"".equals(vDto.getSbgttypecode().trim()))
				sql.append(" and S_BUDGETTYPE='"+vDto.getSbgttypecode()+"' ");
			if(vDto.getSbillkind()!=null&&"1".equals(vDto.getSbillkind().trim()))
				sql.append(" and S_TAXORGCODE='"+vDto.getSbillkind()+"' ");
			else
				sql.append(" and S_TAXORGCODE<>'1' ");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TrTaxorgPayoutReportDto.class).getDtoCollection();//历史表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(sql.toString(),"HTR_INCOMEDAYRPT","TR_INCOMEDAYRPT"),TrTaxorgPayoutReportDto.class).getDtoCollection();//正式表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
		} catch (Exception e) {
			if(execDetail!=null)
				execDetail.closeConnection();
			logger.error(e.getMessage(),e);
			throw new ITFEBizException("查询"+sql==null?"":sql.toString()+"库存明细信息异常！",e);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return getList;
	}
	private Map<String,Object> getSplitPack(List<IDto> dataList)
	{
		Map<String,Object> getMap = null;
		BigDecimal allamt=new BigDecimal("0.00");
		if(dataList!=null&&dataList.size()>0)
		{
			getMap = new HashMap<String,Object>();
			Map<String,String> dataMap = null;
			TrTaxorgPayoutReportDto dto = null;
			for(IDto idto:dataList)
			{
				dto = (TrTaxorgPayoutReportDto)idto;
				if(getMap.get(dto.getSbudgetsubcode())==null)
				{
					dataMap = new HashMap<String,String>();
					dataMap.put("ExpFuncCode",dto.getSbudgetsubcode());//支出功能分类科目编码
					dataMap.put("CurPayAmt",String.valueOf(dto.getNmoneyday()));//本期金额
					dataMap.put("SumPayAmt",String.valueOf(dto.getNmoneyyear()));//累计金额	

					getMap.put(dto.getSbudgetsubcode(),dataMap);
					allamt=allamt.add(dto.getNmoneyday());
				}else
				{
					dataMap = (Map<String,String>)getMap.get(dto.getSbudgetsubcode());
					dataMap.put("CurPayAmt",String.valueOf(new BigDecimal(dataMap.get("CurPayAmt")).add(dto.getNmoneyday())));
					dataMap.put("SumPayAmt",String.valueOf(dto.getNmoneyyear()));//累计金额
				}
			}
			getMap.put("allamt",allamt);
			getMap.put("allcount", getMap.size());
		}
		return getMap;
	}
	private MainAndSubDto get3512Data(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		SQLExecutor execDetail = null;
		MainAndSubDto dataDto = null;
		if(vDto!=null&&vDto.getSdealno()!=null)
		{
			try {
				if(execDetail==null)
					execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = " select * from TF_REPORT_DEFRAY_MAIN where I_VOUSRLNO="+vDto.getSdealno();
				List<IDto> dataList = (List<IDto>)execDetail.runQuery(sql,TfReportDefrayMainDto.class).getDtoCollection();
				if(dataList!=null&&dataList.size()>0)
				{
					dataDto = new MainAndSubDto();
					dataDto.setMainDto(dataList.get(0));
					sql = "select * from TF_REPORT_DEFRAY_SUB where I_VOUSRLNO="+vDto.getSdealno();
					dataDto.setSubDtoList((List<IDto>)execDetail.runQuery(sql,TfReportDefraySubDto.class).getDtoCollection());
				}
				
			} catch (JAFDatabaseException e) {
				logger.error(e.getMessage(),e);
				throw new ITFEBizException("回执5512时查询3512数据信息异常！",e);
			}finally
			{
				if(execDetail!=null)
					execDetail.closeConnection();
			}
		}
		return dataDto;
	}
	private Object getObject(Map map,TfReportDefraySubDto sub3512dto)
	{
		Object getObject = null;
		if(map!=null&&sub3512dto!=null)
		{
			getObject = map.get(sub3512dto.getSexpfunccode());
		}
		return getObject;
	}
}

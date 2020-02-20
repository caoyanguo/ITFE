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
import com.cfcc.itfe.persistence.dto.TfReportDepositMainDto;
import com.cfcc.itfe.persistence.dto.TfReportDepositSubDto;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 * 人行库款账户报表对账请求3513回执5513转化
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor5513 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5513.class);
											
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
			MainAndSubDto datadto =  get3513Data(vDto);
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
				dto.setSvtcode(MsgConstant.VOUCHER_NO_5513);
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
			TfReportDepositMainDto main3513dto = (TfReportDepositMainDto)datadto.getMainDto();
			TfReportDepositSubDto sub3513dto = null;
			map.put("Voucher", vouchermap);// 设置报文节点 Voucher
			// 设置报文消息体 
			vouchermap.put("AdmDivCode",main3513dto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear",main3513dto.getSstyear());//业务年度
			vouchermap.put("VtCode",MsgConstant.VOUCHER_NO_5513);//凭证类型编号
			vouchermap.put("VouDate",TimeFacade.getCurrentStringTime());//凭证日期
			vouchermap.put("VoucherNo",dto.getSvoucherno());//凭证号
			vouchermap.put("VoucherCheckNo",main3513dto.getSvouchercheckno());//对账单号
			vouchermap.put("TreCode",main3513dto.getStrecode());//国库主体代码
			vouchermap.put("TreName",main3513dto.getStrename());//国库主体名称
			vouchermap.put("AcctNo",main3513dto.getSacctno());//库款账号
			vouchermap.put("AcctName",main3513dto.getSacctname());//库款账户名称
			vouchermap.put("BeginDate",main3513dto.getSbegindate());//对账起始日期
			vouchermap.put("EndDate",main3513dto.getSenddate());//对账终止日期
			vouchermap.put("AllNum",getMap.get("allcount"));//总笔数
			vouchermap.put("AcctAmt",String.valueOf(main3513dto.getNacctamt()));//账面余额
			vouchermap.put("XAcctAmt",String.valueOf(getMap.get("allamt")));//反馈账面余额
			vouchermap.put("XDiffMoney",String.valueOf(main3513dto.getNacctamt().subtract(new BigDecimal(String.valueOf(getMap.get("allamt")))).abs()));//差额
			List<Object> Detail= new ArrayList<Object>();
			Object tempdto = null;
			Map<String,String> tempMap = null;
			int error = 0;
			for(IDto idto:datadto.getSubDtoList())
			{
				sub3513dto = (TfReportDepositSubDto)idto;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",sub3513dto.getSid());//序号
				Detailmap.put("AcctDate",sub3513dto.getSacctdate());//明细日期
				Detailmap.put("IncomeAmt",String.valueOf(sub3513dto.getNincomeamt()));//收入金额
				Detailmap.put("PayAmt",String.valueOf(sub3513dto.getNpayamt()));//支出金额
				tempdto = getObject(getMap,sub3513dto);
				if(tempdto==null)
				{
					error++;
					Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
					Detailmap.put("XCheckReason","无此凭证");//不符原因
				}else if(tempdto instanceof Map)
				{
					tempMap = (Map<String,String>)tempdto;
					if(sub3513dto.getNpayamt()!=null&&sub3513dto.getNincomeamt()!=null&&String.valueOf(sub3513dto.getNpayamt()).equals(tempMap.get("PayAmt"))&&String.valueOf(sub3513dto.getNincomeamt()).equals(tempMap.get("IncomeAmt")))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//对账结果
						Detailmap.put("XCheckReason","");//不符原因
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
						if(!String.valueOf(sub3513dto.getNpayamt()).equals(tempMap.get("PayAmt")))
							Detailmap.put("XCheckReason","支出金额不符");//不符原因
						if(!String.valueOf(sub3513dto.getNincomeamt()).equals(tempMap.get("IncomeAmt")))
							Detailmap.put("XCheckReason","收入金额不符");//不符原因
					}
					
				}
				Detailmap.put("Hold1",sub3513dto.getShold1());//预留字段1
				Detailmap.put("Hold2",sub3513dto.getShold2());//预留字段2
				Detailmap.put("Hold3",sub3513dto.getShold3());//预留字段3
				Detailmap.put("Hold4",sub3513dto.getShold4());//预留字段4
				Detail.add(Detailmap);
			}
			vouchermap.put("XDiffNum",error);//不符笔数
			vouchermap.put("Hold1",main3513dto.getShold1());//预留字段1
			vouchermap.put("Hold2",main3513dto.getShold2());//预留字段2
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
	private List<IDto> getDataList(MainAndSubDto dto3513) throws ITFEBizException
	{
		List<IDto> getList = null;
		StringBuffer sql = null;
		SQLExecutor execDetail=null;
		TfReportDepositMainDto vDto = (TfReportDepositMainDto)dto3513.getMainDto();
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			sql = new StringBuffer("SELECT * FROM HTR_STOCKDAYRPT WHERE S_RPTDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"' ");
			if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode()))
				sql.append(" and S_TRECODE='"+vDto.getStrecode());
			if(vDto.getSacctno()!=null&&!"".equals(vDto.getSacctno().trim()))
				sql.append(" and S_ACCNO='"+vDto.getSacctno()+"' ");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TrStockdayrptDto.class).getDtoCollection();//历史表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(sql.toString(),"HTR_STOCKDAYRPT","TR_STOCKDAYRPT"),TrStockdayrptDto.class).getDtoCollection();//正式表主表
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
			throw new ITFEBizException("查询"+sql==null?"":sql.toString()+"5513库存明细信息异常！",e);
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
			TrStockdayrptDto dto = null;
			for(IDto idto:dataList)
			{
				dto = (TrStockdayrptDto)idto;
				dataMap = new HashMap<String,String>();
				dataMap.put("AcctDate",dto.getSaccdate());//明细日期
				dataMap.put("IncomeAmt",String.valueOf(dto.getNmoneyin()));//收入金额
				dataMap.put("PayAmt",String.valueOf(dto.getNmoneyout()));//支出金额
				getMap.put(dto.getSaccdate(),dataMap);
				allamt=dto.getNmoneytoday();
			}
			getMap.put("allamt",allamt);
			getMap.put("allcount", getMap.size());
		}
		return getMap;
	}
	private MainAndSubDto get3513Data(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		SQLExecutor execDetail = null;
		MainAndSubDto dataDto = null;
		if(vDto!=null&&vDto.getSdealno()!=null)
		{
			try {
				if(execDetail==null)
					execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = " select * from TF_REPORT_DEPOSIT_MAIN where I_VOUSRLNO="+vDto.getSdealno();
				List<IDto> dataList = (List<IDto>)execDetail.runQuery(sql,TfReportDepositMainDto.class).getDtoCollection();
				if(dataList!=null&&dataList.size()>0)
				{
					dataDto = new MainAndSubDto();
					dataDto.setMainDto(dataList.get(0));
					sql = "select * from TF_REPORT_DEPOSIT_SUB where I_VOUSRLNO="+vDto.getSdealno();
					dataDto.setSubDtoList((List<IDto>)execDetail.runQuery(sql,TfReportDepositSubDto.class).getDtoCollection());
				}
				
			} catch (JAFDatabaseException e) {
				logger.error(e.getMessage(),e);
				throw new ITFEBizException("回执2507时查询3507数据信息异常！",e);
			}finally
			{
				if(execDetail!=null)
					execDetail.closeConnection();
			}
		}
		return dataDto;
	}
	private Object getObject(Map map,TfReportDepositSubDto sub3513dto)
	{
		Object getObject = null;
		if(map!=null&&sub3513dto!=null)
		{
			getObject = map.get(sub3513dto.getSacctdate());
		}
		return getObject;
	}
}

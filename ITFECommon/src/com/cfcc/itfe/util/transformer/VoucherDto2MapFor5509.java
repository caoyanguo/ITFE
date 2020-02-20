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
import com.cfcc.itfe.persistence.dto.TfReconcileRefundMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRefundSubDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 *人行收入退付对账请求3509回执5509转化
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor5509 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5509.class);
											
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
			MainAndSubDto datadto =  get3509Data(vDto);
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
				dto.setSvtcode(MsgConstant.VOUCHER_NO_5509);
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
			TfReconcileRefundMainDto main3509dto = (TfReconcileRefundMainDto)datadto.getMainDto();
			TfReconcileRefundSubDto sub3509dto = null;
			map.put("Voucher", vouchermap);// 设置报文节点 Voucher
			// 设置报文消息体 
			vouchermap.put("AdmDivCode",main3509dto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear",main3509dto.getSstyear());//业务年度
			vouchermap.put("VtCode",MsgConstant.VOUCHER_NO_5509);//凭证类型编号
			vouchermap.put("VouDate",TimeFacade.getCurrentStringTime());//凭证日期
			vouchermap.put("VoucherNo",dto.getSvoucherno());//凭证号
			vouchermap.put("VoucherCheckNo",main3509dto.getSvouchercheckno());//对账单号
			vouchermap.put("ChildPackNum",main3509dto.getSchildpacknum());//子包总数
			vouchermap.put("CurPackNo",main3509dto.getScurpackno());//本包序号
			vouchermap.put("TreCode",main3509dto.getStrecode());//国库主体代码
			vouchermap.put("ClearBankCode","");//人民银行编码
			vouchermap.put("ClearBankName","");//人民银行名称
			vouchermap.put("ClearAccNo",main3509dto.getSclearaccno());//清算账号
			vouchermap.put("ClearAccNanme",main3509dto.getSclearaccnanme());//清算账户名称
			vouchermap.put("BeginDate",main3509dto.getSbegindate());//对账起始日期
			vouchermap.put("EndDate",main3509dto.getSenddate());//对账终止日期
			vouchermap.put("AllNum",getMap.get("allcount"));//总笔数
			vouchermap.put("AllAmt",String.valueOf(getMap.get("allamt")));//总金额
			List<Object> Detail= new ArrayList<Object>();
			Object tempdto = null;
			int error = 0;
			TvDwbkDto dwdto = null;
			TvVoucherinfoAllocateIncomeDto alldto = null;
			for(IDto idto:datadto.getSubDtoList())
			{
				sub3509dto = (TfReconcileRefundSubDto)idto;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",sub3509dto.getSid());//序号
				Detailmap.put("PayDetailId",sub3509dto.getSpaydetailid());//退付明细Id
				Detailmap.put("FundTypeCode",sub3509dto.getSfundtypecode());//资金性质编码
				Detailmap.put("FundTypeName",sub3509dto.getSfundtypecode());//资金性质名称
				Detailmap.put("PayeeAcctNo",sub3509dto.getSpayeeacctno());//收款人账号
				Detailmap.put("PayeeAcctName",sub3509dto.getSpayeeacctname());//收款人名称
				Detailmap.put("PayeeAcctBankName",sub3509dto.getSpayeeacctbankname());//收款人银行
				Detailmap.put("PayAcctNo",sub3509dto.getSpayacctno());//付款账户账号
				Detailmap.put("PayAcctName",sub3509dto.getSpayacctname());//付款账户名称
				Detailmap.put("PayAcctBankName",sub3509dto.getSpayacctbankname());//付款账户银行
				Detailmap.put("AgencyCode",sub3509dto.getSagencycode());//预算单位编码
				Detailmap.put("AgencyName",sub3509dto.getSagencyname());//预算单位名称
				Detailmap.put("IncomeSortCode",sub3509dto.getSincomesortcode());//收入分类科目编码
				Detailmap.put("IncomeSortName",sub3509dto.getSincomesortname());//收入分类科目名称
				Detailmap.put("PayAmt",String.valueOf(sub3509dto.getNpayamt()));//退付金额
				tempdto = getObject(getMap,sub3509dto);
				if(tempdto==null)
				{
					error++;
					Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
					Detailmap.put("XCheckReason","无此凭证");//不符原因
				}else if(tempdto instanceof TvDwbkDto)
				{
					dwdto = (TvDwbkDto)tempdto;
					if(dwdto.getFamt()!=null&&sub3509dto.getNpayamt()!=null&&dwdto.getFamt().abs().toString().equals(sub3509dto.getNpayamt().abs().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//对账结果
						Detailmap.put("XCheckReason","");//不符原因
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
						Detailmap.put("XCheckReason","金额不符");//不符原因
					}
					
				}else if (tempdto instanceof TvVoucherinfoAllocateIncomeDto)
				{
					alldto = (TvVoucherinfoAllocateIncomeDto)tempdto;
					if(alldto.getNmoney()!=null&&sub3509dto.getNpayamt()!=null&&alldto.getNmoney().abs().toString().equals(sub3509dto.getNpayamt().abs().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//对账结果
						Detailmap.put("XCheckReason","");//不符原因
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
						Detailmap.put("XCheckReason","金额不符");//不符原因
					}
				}
				Detailmap.put("Hold1","");//预留字段1
				Detailmap.put("Hold2","");//预留字段2
				Detailmap.put("Hold3","");//预留字段3
				Detailmap.put("Hold4","");//预留字段4

				Detail.add(Detailmap);
			}
			if(main3509dto.getSallnum()!=null&&main3509dto.getSallnum().equals(String.valueOf(getMap.get("allcount"))))
			{
				if(main3509dto.getNallamt()!=null&&main3509dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
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
				if(main3509dto.getNallamt()!=null&&main3509dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
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
	private List<IDto> getDataList(MainAndSubDto dto3507) throws ITFEBizException
	{
		List<IDto> getList = null;
		StringBuffer sql = null;
		SQLExecutor execDetail=null;
		TfReconcileRefundMainDto vDto = (TfReconcileRefundMainDto)dto3507.getMainDto();
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			
			sql = new StringBuffer("SELECT * FROM HTV_DWBK WHERE D_ACCEPT BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"' ");
			if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode()))
				sql.append(" and S_TRECODE='"+vDto.getStrecode());
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvDwbkDto.class).getDtoCollection();//历史表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(sql.toString(),"HTV_DWBK","TV_DWBK"),TvDwbkDto.class).getDtoCollection();//正式表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.addAll(detailList);
			}
			sql = new StringBuffer("SELECT * FROM TV_VOUCHERINFO_ALLOCATE_INCOME WHERE S_COMMITDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"' ");
			if(vDto.getStrecode()!=null&&!"".equals(vDto.getStrecode()))
				sql.append(" and S_TRECODE='"+vDto.getStrecode());
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvVoucherinfoAllocateIncomeDto.class).getDtoCollection();//历史表主表
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
			int count=0;
			for(IDto idto:dataList)
			{
				count++;
				if(idto instanceof TvDwbkDto)
				{
					TvDwbkDto maindto = (TvDwbkDto)idto;
					getMap.put(maindto.getSbizno(), maindto);
					
				}else if(idto instanceof TvVoucherinfoAllocateIncomeDto)
				{
					TvVoucherinfoAllocateIncomeDto maindto = (TvVoucherinfoAllocateIncomeDto)idto;
					getMap.put(maindto.getSdealno(), maindto);
				}
			}
			getMap.put("allamt",allamt);
			getMap.put("allcount", count);
		}
		return getMap;
	}
	private MainAndSubDto get3509Data(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		SQLExecutor execDetail = null;
		MainAndSubDto dataDto = null;
		if(vDto!=null&&vDto.getSdealno()!=null)
		{
			try {
				if(execDetail==null)
					execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = " select * from TF_RECONCILE_REFUND_MAIN where I_VOUSRLNO="+vDto.getSdealno();
				List<IDto> dataList = (List<IDto>)execDetail.runQuery(sql,TfReconcileRefundMainDto.class).getDtoCollection();
				if(dataList!=null&&dataList.size()>0)
				{
					dataDto = new MainAndSubDto();
					dataDto.setMainDto(dataList.get(0));
					sql = "select * from TF_RECONCILE_REFUND_SUB where I_VOUSRLNO="+vDto.getSdealno();
					dataDto.setSubDtoList((List<IDto>)execDetail.runQuery(sql,TfReconcileRefundSubDto.class).getDtoCollection());
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
	private Object getObject(Map map,TfReconcileRefundSubDto sub3509dto)
	{
		Object getObject = null;
		if(map!=null&&sub3509dto!=null)
		{
			getObject = map.get(sub3509dto.getSid());
		}
		return getObject;
	}
}

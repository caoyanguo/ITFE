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
import com.cfcc.itfe.persistence.dto.TfReconcileRealdialMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRealdialSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgSubDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 *人行实拨信息对账请求3508回执5508转化
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor5508 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5508.class);
											
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
			MainAndSubDto datadto =  get3508Data(vDto);
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
				dto.setSvtcode(MsgConstant.VOUCHER_NO_5508);
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
			TfReconcileRealdialMainDto main3508dto = (TfReconcileRealdialMainDto)datadto.getMainDto();
			TfReconcileRealdialSubDto sub3508dto = null;
			map.put("Voucher", vouchermap);// 设置报文节点 Voucher
			// 设置报文消息体 
			vouchermap.put("AdmDivCode",main3508dto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear",main3508dto.getSstyear());//业务年度
			vouchermap.put("VtCode",MsgConstant.VOUCHER_NO_5508);//凭证类型编号
			vouchermap.put("VouDate",TimeFacade.getCurrentStringTime());//凭证日期
			vouchermap.put("VoucherNo",dto.getSvoucherno());//凭证号
			vouchermap.put("VoucherCheckNo",main3508dto.getSvouchercheckno());//对账单号
			vouchermap.put("ChildPackNum",main3508dto.getSchildpacknum());//子包总数
			vouchermap.put("CurPackNo",main3508dto.getScurpackno());//本包序号
			vouchermap.put("TreCode",main3508dto.getStrecode());//国库主体代码
			vouchermap.put("ClearBankCode",main3508dto.getSclearbankcode());//人民银行编码
			vouchermap.put("ClearBankName",main3508dto.getSclearbankname());//人民银行名称
			vouchermap.put("ClearAccNo",main3508dto.getSclearaccno());//拨款账号
			vouchermap.put("ClearAccNanme",main3508dto.getSclearaccnanme());//拨款账户名称
			vouchermap.put("BeginDate",main3508dto.getSbegindate());//对账起始日期
			vouchermap.put("EndDate",main3508dto.getSenddate());//对账终止日期
			vouchermap.put("AllNum",getMap.get("allcount"));//总笔数
			vouchermap.put("AllAmt",String.valueOf(getMap.get("allamt")));//总金额
			List<Object> Detail= new ArrayList<Object>();
			Object tempdto = null;
			TvPayoutmsgsubDto sub5207 = null;
			TvPayoutbackmsgSubDto sub5207back = null;
			TvVoucherinfoAllocateIncomeDto sub5207import = null;
			int error = 0;
			for(IDto idto:datadto.getSubDtoList())
			{
				sub3508dto = (TfReconcileRealdialSubDto)idto;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",sub3508dto.getSid());//序号
				Detailmap.put("PayDetailId",sub3508dto.getSpaydetailid());//拨款明细Id
				Detailmap.put("BgtTypeCode",sub3508dto.getSbgttypecode());//预算类型编码
				Detailmap.put("BgtTypeName",sub3508dto.getSbgttypename());//预算类型名称
				Detailmap.put("FundTypeCode",sub3508dto.getSfundtypecode());//资金性质编码
				Detailmap.put("FundTypeName",sub3508dto.getSfundtypename());//资金性质名称
				Detailmap.put("PayTypeCode",sub3508dto.getSpaytypecode());//支付方式编码
				Detailmap.put("PayTypeName",sub3508dto.getSpaytypename());//支付方式名称
				Detailmap.put("PayeeAcctNo",sub3508dto.getSpayeeacctno());//收款人账号
				Detailmap.put("PayeeAcctName",sub3508dto.getSpayeeacctname());//收款人名称
				Detailmap.put("PayeeAcctBankName",sub3508dto.getSpayeeacctbankname());//收款人银行
				Detailmap.put("PayAcctNo",sub3508dto.getSpayacctno());//付款账户账号
				Detailmap.put("PayAcctName",sub3508dto.getSpayacctname());//付款账户名称
				Detailmap.put("PayAcctBankName",sub3508dto.getSpayacctbankname());//付款账户银行
				Detailmap.put("AgencyCode",sub3508dto.getSagencycode());//预算单位编码
				Detailmap.put("AgencyName",sub3508dto.getSagencyname());//预算单位名称
				Detailmap.put("ExpFuncCode",sub3508dto.getSexpfunccode());//支出功能分类科目编码
				Detailmap.put("ExpFuncName",sub3508dto.getSexpfuncname());//支出功能分类科目名称
				Detailmap.put("ExpEcoCode",sub3508dto.getSexpecocode());//经济分类科目编码
				Detailmap.put("ExpEcoName",sub3508dto.getSexpeconame());//经济分类科目名称
				Detailmap.put("PayAmt",String.valueOf(sub3508dto.getNpayamt()));//拨款金额
				Detailmap.put("XCheckResult","");//对账结果
				Detailmap.put("XCheckReason","");//不符原因
				Detailmap.put("Hold1","");//预留字段1
				Detailmap.put("Hold2","");//预留字段2
				Detailmap.put("Hold3","");//预留字段3
				Detailmap.put("Hold4","");//预留字段4

				tempdto = getObject(getMap,main3508dto,sub3508dto);
				if(tempdto==null)
				{
					error++;
					Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
					Detailmap.put("XCheckReason","无此凭证");//不符原因
				}else if(tempdto instanceof TvPayoutmsgsubDto)
				{
					sub5207 = (TvPayoutmsgsubDto)tempdto;
					if(sub5207.getNmoney()!=null&&sub3508dto.getNpayamt()!=null&&sub5207.getNmoney().abs().toString().equals(sub3508dto.getNpayamt().abs().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//对账结果
						Detailmap.put("XCheckReason","");//不符原因
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
						Detailmap.put("XCheckReason","金额不符");//不符原因
					}
					
				}else if (tempdto instanceof TvPayoutbackmsgSubDto)
				{
					sub5207back = (TvPayoutbackmsgSubDto)tempdto;
					if(sub5207back.getNmoney()!=null&&sub3508dto.getNpayamt()!=null&&sub5207back.getNmoney().abs().toString().equals(sub3508dto.getNpayamt().abs().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//对账结果
						Detailmap.put("XCheckReason","");//不符原因
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
						Detailmap.put("XCheckReason","金额不符");//不符原因
					}
				}else if(tempdto instanceof TvVoucherinfoAllocateIncomeDto)
				{
					sub5207import = (TvVoucherinfoAllocateIncomeDto)tempdto;
					if(sub5207import.getNmoney()!=null&&sub3508dto.getNpayamt()!=null&&sub5207import.getNmoney().abs().toString().equals(sub3508dto.getNpayamt().abs().toString()))
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
			if(main3508dto.getSallnum()!=null&&main3508dto.getSallnum().equals(String.valueOf(getMap.get("allcount"))))
			{
				if(main3508dto.getNallamt()!=null&&main3508dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//对账结果
					vouchermap.put("XDiffNum",error);//不符笔数
				}else
				{
					vouchermap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
					vouchermap.put("XDiffNum",error);//不符金额
				}
			}else
			{
				if(main3508dto.getNallamt()!=null&&main3508dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
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
	private List<IDto> getDataList(MainAndSubDto dto3508) throws ITFEBizException
	{
		List<IDto> getList = null;
		StringBuffer sql = null;
		SQLExecutor execDetail=null;
		TfReconcileRealdialMainDto vDto = (TfReconcileRealdialMainDto)dto3508.getMainDto();
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			//5207实拨资金已经回单的数据
			sql = new StringBuffer("SELECT * FROM HTV_PAYOUTMSGMAIN WHERE I_VOUSRLNO in(");
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_5207+"' AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
			sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
			sql.append("AND S_CREATDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"') and S_PAYERACCT='"+vDto.getSclearaccno()+"'");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayoutmsgmainDto.class).getDtoCollection();//历史表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_PAYOUTMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";//实拨资金数据
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutmsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutmsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutmsgsubDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getSbizno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getSbizno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getSbizno()), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvPayoutmsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutmsgmainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getSbizno())));//历史表子表
					getList.add(datadto);
				}
			}
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYOUTMSGMAIN","TV_PAYOUTMSGMAIN"),TvPayoutmsgmainDto.class).getDtoCollection();//正式表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYOUTMSGSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYOUTMSGMAIN","TV_PAYOUTMSGMAIN"),"*", "I_VOUSRLNO")+")";//实拨资金子表数据
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutmsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutmsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutmsgsubDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getSbizno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getSbizno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getSbizno()), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvPayoutmsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutmsgmainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getSbizno())));//正式表子表
					getList.add(datadto);
				}
			}
			sql = new StringBuffer("SELECT * FROM TV_PAYOUTBACKMSG_MAIN WHERE S_VOUNO IN(");//查询tcbs回执生成退款已经发送凭证库的数据
			sql.append("SELECT S_VOUCHERNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_3208+"'  AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
			sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
			sql.append("AND S_CREATDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"') and S_PAYERACCT='"+vDto.getSclearaccno()+"'");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayoutbackmsgMainDto.class).getDtoCollection();//历史表主表
			detailList.addAll(execDetail.runQuery(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvPayoutbackmsgMainDto.class).getDtoCollection());//历史表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYOUTBACKMSG_SUB where S_BIZNO in("+StringUtil.replace(sql.toString(), "*", "S_BIZNO")+")";
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutbackmsgSubDto.class).getDtoCollection();
				subList.addAll(execDetail.runQuery(StringUtil.replace(subsql,"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvPayoutbackmsgSubDto.class).getDtoCollection());
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutbackmsgSubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutbackmsgSubDto)tempdto;
						tempList = subMap.get(subdto.getSbizno());
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(subdto.getSbizno(), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(subdto.getSbizno(), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvPayoutbackmsgMainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutbackmsgMainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(tempdto.getSbizno()));//历史表子表
					getList.add(datadto);
				}
			}
			sql = new StringBuffer("SELECT * FROM TV_VOUCHERINFO_ALLOCATE_INCOME WHERE S_DEALNO IN(");//查询实拨资金历史表已经回单的数据
			sql.append("SELECT S_VOUCHERNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_3208+"'  AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
			sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
			sql.append("AND S_CREATDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"') and S_PAYACCTNO='"+vDto.getSclearaccno()+"'");
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvVoucherinfoAllocateIncomeDto.class).getDtoCollection();//正式表主表
			if(detailList!=null&&detailList.size()>0)
			{
				MainAndSubDto datadto = null;
				TvVoucherinfoAllocateIncomeDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvVoucherinfoAllocateIncomeDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					getList.add(datadto);
				}
			}
		} catch (Exception e) {
			if(execDetail!=null)
				execDetail.closeConnection();
			logger.error(e);
			throw new ITFEBizException("查询"+sql==null?"":sql.toString()+"明细信息异常！",e);
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
			MainAndSubDto dto = null;
			int count=0;
			for(IDto idto:dataList)
			{
				dto = (MainAndSubDto)idto;
				if(dto.getMainDto() instanceof TvPayoutmsgmainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					TvPayoutmsgmainDto maindto = (TvPayoutmsgmainDto)dto.getMainDto();
					TvPayoutmsgsubDto subdto = null;
					for(int i=0;i<dto.getSubDtoList().size();i++)
					{
						count++;
						subdto = (TvPayoutmsgsubDto)dto.getSubDtoList().get(i);
						getMap.put(maindto.getStaxticketno()+subdto.getSid(), subdto);
						getMap.put(subdto.getSid(), subdto);
						getMap.put(subdto.getSbgttypecode()+maindto.getSfundtypecode()+maindto.getSpaytypecode()
								+maindto.getSrecacct()+maindto.getSpayeracct()+maindto.getSbudgetunitcode()+subdto.getSexpecocode()+subdto.getNmoney(),subdto);//凭证编号+预算单位编码+收款人编号+付款人编号+金额
						//预算类型编码+资金性质编码+支付方式编码+收款人账号+付款账户账号+预算单位编码+经济分类科目编码+拨款金额
						allamt=allamt.add(subdto.getNmoney());
					}
				}else if(dto.getMainDto() instanceof TvPayoutbackmsgMainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					TvPayoutbackmsgMainDto maindto = (TvPayoutbackmsgMainDto)dto.getMainDto();
					TvPayoutbackmsgSubDto subdto = null;
					for(int i=0;i<dto.getSubDtoList().size();i++)
					{
						count++;
						subdto = (TvPayoutbackmsgSubDto)dto.getSubDtoList().get(i);
						getMap.put(maindto.getSvouno()+subdto.getSseqno(), subdto);
						getMap.put(subdto.getSbudgetprjcode()+maindto.getSpayeeacct()+maindto.getSpayeracct()+maindto.getSbudgetunitcode()+subdto.getSecnomicsubjectcode()+subdto.getNmoney(),subdto
								);//预算类型编码+收款人账号+付款账户账号+预算单位编码+经济分类科目编码+拨款金额
						allamt=allamt.subtract(subdto.getNmoney().abs());
					}
				}
				else if(dto.getMainDto() instanceof TvVoucherinfoAllocateIncomeDto)
				{
					count++;
					TvVoucherinfoAllocateIncomeDto maindto = (TvVoucherinfoAllocateIncomeDto)dto.getMainDto();
					getMap.put(maindto.getSpaydealno(), maindto);
					getMap.put(maindto.getSdealno(), maindto);
					getMap.put(maindto.getSpayacctno()+maindto.getSpayeeacctno()+maindto.getNmoney(),maindto);
					allamt=allamt.subtract(maindto.getNmoney().abs());
				}
			}
			getMap.put("allamt",allamt);
			getMap.put("allcount", count);
		}
		return getMap;
	}
	private MainAndSubDto get3508Data(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		SQLExecutor execDetail = null;
		MainAndSubDto dataDto = null;
		if(vDto!=null&&vDto.getSdealno()!=null)
		{
			try {
				if(execDetail==null)
					execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = " select * from TF_RECONCILE_REALDIAL_MAIN where I_VOUSRLNO="+vDto.getSdealno();
				List<IDto> dataList = (List<IDto>)execDetail.runQuery(sql,TfReconcileRealdialMainDto.class).getDtoCollection();
				if(dataList!=null&&dataList.size()>0)
				{
					dataDto = new MainAndSubDto();
					dataDto.setMainDto(dataList.get(0));
					sql = "select * from TF_RECONCILE_REALDIAL_SUB where I_VOUSRLNO="+vDto.getSdealno();
					dataDto.setSubDtoList((List<IDto>)execDetail.runQuery(sql,TfReconcileRealdialSubDto.class).getDtoCollection());
				}
				
			} catch (JAFDatabaseException e) {
				logger.error(e.getMessage(),e);
				throw new ITFEBizException("回执5508时查询3508数据信息异常！",e);
			}finally
			{
				if(execDetail!=null)
					execDetail.closeConnection();
			}
		}
		return dataDto;
	}
	private Object getObject(Map map,TfReconcileRealdialMainDto main3508dto,TfReconcileRealdialSubDto sub3508dto)
	{
		Object getObject = null;
		if(map!=null&&sub3508dto!=null)
		{
			getObject = map.get(sub3508dto.getSpaydetailid());
			if(getObject==null)
			{
				getObject = map.get(sub3508dto.getSid());
				if(getObject==null)
				{
					getObject = map.get(sub3508dto.getSbgttypecode()+sub3508dto.getSfundtypecode()+sub3508dto.getSpaytypecode()+sub3508dto.getSpayeeacctno()+
						sub3508dto.getSpayacctno()+sub3508dto.getSagencycode()+sub3508dto.getSexpecocode()+sub3508dto.getNpayamt()
						);//预算类型编码+资金性质编码+支付方式编码+收款人账号+付款账户账号+预算单位编码+经济分类科目编码+拨款金额
					if(getObject==null)
					{
						getObject = map.get(sub3508dto.getSbgttypecode()+sub3508dto.getSpayeeacctno()+
								sub3508dto.getSpayacctno()+sub3508dto.getSagencycode()+sub3508dto.getSexpecocode()+sub3508dto.getNpayamt()
								);//预算类型编码+收款人账号+付款账户账号+预算单位编码+经济分类科目编码+拨款金额
					}
				}
			}
		}
		return getObject;
	}
}

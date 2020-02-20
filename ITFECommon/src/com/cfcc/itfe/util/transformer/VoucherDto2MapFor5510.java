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
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayquotaMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayquotaSubDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
/**
 * 人行清算额度对账请求3510回执5510转化
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor5510 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5510.class);
											
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
			MainAndSubDto datadto =  get3510Data(vDto);
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
				dto.setSvtcode(MsgConstant.VOUCHER_NO_5510);
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
			TfReconcilePayquotaMainDto main3510dto = (TfReconcilePayquotaMainDto)datadto.getMainDto();
			TfReconcilePayquotaSubDto sub3510dto = null;
			map.put("Voucher", vouchermap);// 设置报文节点 Voucher
			// 设置报文消息体 
			vouchermap.put("AdmDivCode",main3510dto.getSadmdivcode());//行政区划代码
			vouchermap.put("StYear",main3510dto.getSstyear());//业务年度
			vouchermap.put("VtCode",MsgConstant.VOUCHER_NO_5510);//凭证类型编号
			vouchermap.put("VouDate",TimeFacade.getCurrentStringTime());//凭证日期
			vouchermap.put("VoucherNo",main3510dto.getSvoucherno());//凭证号
			vouchermap.put("VoucherCheckNo",main3510dto.getSvouchercheckno());//对账单号
			vouchermap.put("ChildPackNum",main3510dto.getSchildpacknum());//子包总数
			vouchermap.put("CurPackNo",main3510dto.getScurpackno());//本包序号
			vouchermap.put("TreCode",main3510dto.getStrecode());//国库主体代码
			vouchermap.put("ClearBankCode",main3510dto.getSclearbankcode());//人民银行编码
			vouchermap.put("ClearBankName",main3510dto.getSclearbankname());//人民银行名称
			vouchermap.put("ClearAccNo",main3510dto.getSclearaccno());//清算账号
			vouchermap.put("ClearAccNanme",main3510dto.getSclearaccnanme());//清算账户名称
			vouchermap.put("BeginDate",main3510dto.getSbegindate());//对账起始日期
			vouchermap.put("EndDate",main3510dto.getSenddate());//对账终止日期
			vouchermap.put("AllNum",getMap.get("allnum"));//总笔数
			vouchermap.put("AllAmt",String.valueOf(getMap.get("allamt")));//总金额
			List<Object> Detail= new ArrayList<Object>();
			Object tempdto = null;
			Map tempMap = null;
			int error = 0;
			for(IDto idto:datadto.getSubDtoList())
			{
				sub3510dto = (TfReconcilePayquotaSubDto)idto;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",sub3510dto.getSid());//序号
				Detailmap.put("SupDepCode",sub3510dto.getSsupdepcode());//一级预算单位编码
				Detailmap.put("SupDepName",sub3510dto.getSsupdepname());//一级预算单位名称
				Detailmap.put("FundTypeCode",sub3510dto.getSfundtypecode());//资金性质编码
				Detailmap.put("FundTypeName",sub3510dto.getSfundtypename());//资金性质名称
				Detailmap.put("PayBankCode",sub3510dto.getSpaybankcode());//代理银行编码
				Detailmap.put("PayBankName",sub3510dto.getSpaybankname());//代理银行名称
				Detailmap.put("PayBankNo",sub3510dto.getSpaybankno());//代理银行行号
				Detailmap.put("ExpFuncCode",sub3510dto.getSexpfunccode());//支出功能分类科目编码
				Detailmap.put("ExpFuncName",sub3510dto.getSexpfuncname());//支出功能分类科目名称
				Detailmap.put("ProCatCode",sub3510dto.getSprocatcode());//收支管理编码
				Detailmap.put("ProCatName",sub3510dto.getSprocatname());//收支管理名称
				Detailmap.put("PayTypeCode",sub3510dto.getSpaytypecode());//支付方式编码
				Detailmap.put("PayTypeName",sub3510dto.getSpaytypename());//支付方式名称
				Detailmap.put("PreDateMoney",sub3510dto.getNpredatemoney());//上期额度余额
				Detailmap.put("ClearAmt",String.valueOf(sub3510dto.getNclearamt()));//本期新增清算额度
				Detailmap.put("CurReckMoney",String.valueOf(sub3510dto.getNcurreckmoney()));//本期已清算额度
				Detailmap.put("CurDateMoney",String.valueOf(sub3510dto.getNclearamt()));//本期额度余额
				tempdto = getObject(getMap,sub3510dto);
				if(tempdto==null)
				{
					error++;
					Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
					Detailmap.put("XCheckReason","查找不到凭证");//不符原因
				}else
				{
					tempMap = (Map)tempdto;
					if(tempMap.get("ClearAmt")!=null&&sub3510dto.getNclearamt()!=null&&tempMap.get("ClearAmt").toString().equals(sub3510dto.getNclearamt().toString()))
					{
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_YES);//对账结果
						Detailmap.put("XCheckReason","");//不符原因
					}else
					{
						error++;
						Detailmap.put("XCheckResult",MsgConstant.VOUCHER_RECONCIIATION_NO);//对账结果
						Detailmap.put("XCheckReason","金额不符,本方金额:"+tempMap.get("ClearAmt"));//不符原因
					}
					
				}
				Detailmap.put("Hold1",sub3510dto);//预留字段1
				Detailmap.put("Hold2",sub3510dto);//预留字段2
				Detailmap.put("Hold3",sub3510dto);//预留字段3
				Detailmap.put("Hold4",sub3510dto);//预留字段4

				Detail.add(Detailmap);
			}
			if(main3510dto.getSallnum()!=null&&main3510dto.getSallnum().equals(String.valueOf(getMap.get("allcount"))))
			{
				if(main3510dto.getNallamt()!=null&&main3510dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
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
				if(main3510dto.getNallamt()!=null&&main3510dto.getNallamt().toString().equals(String.valueOf(getMap.get("allamt"))))
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
		TfReconcilePayinfoMainDto vDto = (TfReconcilePayinfoMainDto)dto3507.getMainDto();
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			//2301查询商行划款已经回单的数据
			sql = new StringBuffer("SELECT * FROM HTV_GRANTPAYMSGMAIN WHERE I_VOUSRLNO in(");
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_5106+"' AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
			sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
			sql.append("AND S_CREATDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"')");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvGrantpaymsgmainDto.class).getDtoCollection();//历史表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_GRANTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";//授权支付子表数据
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvGrantpaymsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvGrantpaymsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvGrantpaymsgsubDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvGrantpaymsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvGrantpaymsgmainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//历史表子表
					getList.add(datadto);
				}
			}
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_GRANTPAYMSGMAIN","TV_GRANTPAYMSGMAIN"),TvGrantpaymsgmainDto.class).getDtoCollection();//正式表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_GRANTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_GRANTPAYMSGMAIN","TV_GRANTPAYMSGMAIN"),"*", "I_VOUSRLNO")+")";//授权支付子表数据
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvGrantpaymsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvGrantpaymsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvGrantpaymsgsubDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvGrantpaymsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvGrantpaymsgmainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//正式表子表
					getList.add(datadto);
				}
			}
			//查询直接支付已经回单的数据
			sql = new StringBuffer("SELECT * FROM HTV_DIRECTPAYMSGMAIN WHERE I_VOUSRLNO in(");//查询直接支付已经回单的数据
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_5108+"' AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
			sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
			sql.append("AND S_CREATDATE BETWEEN '"+vDto.getSbegindate()+"' AND '"+vDto.getSenddate()+"')");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvDirectpaymsgmainDto.class).getDtoCollection();//历史表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvDirectpaymsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvDirectpaymsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvDirectpaymsgsubDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvDirectpaymsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvDirectpaymsgmainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//历史表子表
					getList.add(datadto);
				}
			}
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTV_DIRECTPAYMSGMAIN","TV_DIRECTPAYMSGMAIN"),TvDirectpaymsgmainDto.class).getDtoCollection();//正式表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTV_DIRECTPAYMSGMAIN","TV_DIRECTPAYMSGMAIN"), "*", "I_VOUSRLNO")+")";
				List<IDto> subList = null;
				subList = (List<IDto>)execDetail.runQuery(subsql, TvDirectpaymsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvDirectpaymsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvDirectpaymsgsubDto)tempdto;
						tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
						if(tempList==null)
						{
							tempList = new ArrayList<IDto>();
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}else
						{
							tempList.add(subdto);
							subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
						}
					}
				}
				MainAndSubDto datadto = null;
				TvDirectpaymsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvDirectpaymsgmainDto)detailList.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//正式表子表
					getList.add(datadto);
				}
			}
			
		} catch (Exception e) {
			if(execDetail!=null)
				execDetail.closeConnection();
			logger.error(e.getMessage(),e);
			throw new ITFEBizException("查询"+sql==null?"":sql.toString()+"5510数据异常！",e);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return getList;
	}
	private Map<String,Object> getSplitPack(List<IDto> dataList)
	{
		Map<String,Object> tempMap = new HashMap<String,Object>();
		BigDecimal allamt=new BigDecimal("0.00");
		if(dataList!=null&&dataList.size()>0)
		{
			MainAndSubDto dto = null;
			TvDirectpaymsgmainDto ddto = null;
			TvGrantpaymsgmainDto gdto = null;
			Map<String,String> dataMap = null;
			for(IDto idto:dataList)
			{
				dto = (MainAndSubDto)idto;
				if(dto.getMainDto() instanceof TvDirectpaymsgmainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					
					ddto = (TvDirectpaymsgmainDto)dto.getMainDto();
					TvDirectpaymsgsubDto subdto = null;
					for(int sub=0;sub<dto.getSubDtoList().size();sub++)
					{
						subdto = (TvDirectpaymsgsubDto)dto.getSubDtoList().get(sub);
						//预算单位编码+资金性质编码+代理银行行号+支出功能分类科目编码+支付方式编码(11直接支付12授权支付)
						if(tempMap.get(subdto.getSagencycode()+ddto.getSfundtypecode()+ddto.getSpaybankno()+subdto.getSfunsubjectcode()+"11")==null)
						{
							dataMap = new HashMap<String,String>();
							dataMap.put("Id",String.valueOf(ddto.getIvousrlno())+subdto.getIdetailseqno());//序号
							dataMap.put("SupDepCode",getString(subdto.getSagencycode()));//一级预算单位编码
							dataMap.put("SupDepName",getString(subdto.getSagencyname()));//一级预算单位名称
							dataMap.put("FundTypeCode",getString(ddto.getSfundtypecode()));//资金性质编码
							dataMap.put("FundTypeName",getString(ddto.getSfundtypename()));//资金性质名称
							dataMap.put("PayBankCode",getString(ddto.getSpaybankcode()));//代理银行编码
							dataMap.put("PayBankName",getString(ddto.getSpaybankname()));//代理银行名称
							dataMap.put("PayBankNo",getString(ddto.getSpaybankno()));//代理银行行号
							dataMap.put("ExpFuncCode",getString(subdto.getSfunsubjectcode()));//支出功能分类科目编码
							dataMap.put("ExpFuncName",getString(subdto.getSexpfuncname()));//支出功能分类科目名称
							dataMap.put("ProCatCode",getString(subdto.getSprocatcode()));//收支管理编码
							dataMap.put("ProCatName",getString(subdto.getSprocatname()));//收支管理名称
							dataMap.put("PayTypeCode","11");//支付方式编码
							dataMap.put("PayTypeName","直接支付");//支付方式名称
							dataMap.put("PreDateMoney","");//上期额度余额
							dataMap.put("ClearAmt",String.valueOf(subdto.getNmoney()));//本期新增清算额度
							dataMap.put("CurReckMoney","");//本期已清算额度
							dataMap.put("CurDateMoney","");//本期额度余额
							dataMap.put("Hold1","");//预留字段1
							dataMap.put("Hold2","");//预留字段2
							dataMap.put("Hold3","");//预留字段3
							dataMap.put("Hold4","");//预留字段4
							tempMap.put(subdto.getSagencycode()+ddto.getSfundtypecode()+ddto.getSpaybankno()+subdto.getSfunsubjectcode()+"11",dataMap);
						}else
						{
							dataMap = (Map)tempMap.get(subdto.getSagencycode()+ddto.getSfundtypecode()+ddto.getSpaybankno()+subdto.getSfunsubjectcode()+"11");
							dataMap.put("ClearAmt", String.valueOf(new BigDecimal(dataMap.get("ClearAmt")).add(subdto.getNmoney())));
						}
					}
					
				}else if(dto.getMainDto() instanceof TvGrantpaymsgmainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					gdto = (TvGrantpaymsgmainDto)dto.getMainDto();
					TvGrantpaymsgsubDto subdto = null;
					for(int sub=0;sub<dto.getSubDtoList().size();sub++)
					{
						subdto = (TvGrantpaymsgsubDto)dto.getSubDtoList().get(sub);
						//预算单位编码+资金性质编码+代理银行行号+支出功能分类科目编码+支付方式编码(11直接支付12授权支付)
						if(tempMap.get(gdto.getSbudgetunitcode()+gdto.getSfundtypecode()+gdto.getSpaybankno()+subdto.getSfunsubjectcode()+"12")==null)
						{
							dataMap = new HashMap<String,String>();
							dataMap.put("Id",String.valueOf(gdto.getIvousrlno())+subdto.getIdetailseqno());//序号
							dataMap.put("SupDepCode",getString(subdto.getSbudgetunitcode()));//一级预算单位编码
							dataMap.put("SupDepName",getString(subdto.getSsupdepname()));//一级预算单位名称
							dataMap.put("FundTypeCode",getString(gdto.getSfundtypecode()));//资金性质编码
							dataMap.put("FundTypeName",getString(gdto.getSfundtypename()));//资金性质名称
							dataMap.put("PayBankCode",getString(gdto.getSpaybankcode()));//代理银行编码
							dataMap.put("PayBankName",getString(gdto.getSpaybankname()));//代理银行名称
							dataMap.put("PayBankNo",getString(gdto.getSpaybankno()));//代理银行行号
							dataMap.put("ExpFuncCode",getString(subdto.getSfunsubjectcode()));//支出功能分类科目编码
							dataMap.put("ExpFuncName",getString(subdto.getSexpfunccode1()));//支出功能分类科目名称
							dataMap.put("ProCatCode",getString(subdto.getSprocatcode()));//收支管理编码
							dataMap.put("ProCatName",getString(subdto.getSprocatname()));//收支管理名称
							dataMap.put("PayTypeCode","12");//支付方式编码
							dataMap.put("PayTypeName","授权支付");//支付方式名称
							dataMap.put("PreDateMoney","");//上期额度余额
							dataMap.put("ClearAmt",String.valueOf(subdto.getNmoney()));//本期新增清算额度
							dataMap.put("CurReckMoney","");//本期已清算额度
							dataMap.put("CurDateMoney","");//本期额度余额
							dataMap.put("Hold1","");//预留字段1
							dataMap.put("Hold2","");//预留字段2
							dataMap.put("Hold3","");//预留字段3
							dataMap.put("Hold4","");//预留字段4
							tempMap.put(gdto.getSbudgetunitcode()+gdto.getSfundtypecode()+gdto.getSpaybankno()+subdto.getSfunsubjectcode()+"12",dataMap);
						}else
						{
							dataMap = (Map)tempMap.get(gdto.getSbudgetunitcode()+gdto.getSfundtypecode()+gdto.getSpaybankno()+subdto.getSfunsubjectcode()+"12");
							dataMap.put("ClearAmt", String.valueOf(new BigDecimal(dataMap.get("ClearAmt")).add(subdto.getNmoney())));
						}
					}
				}
			}
			tempMap.put("allamt",allamt);
			tempMap.put("allcount", tempMap.size());
		}
		return tempMap;
	}
	private MainAndSubDto get3510Data(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		SQLExecutor execDetail = null;
		MainAndSubDto dataDto = null;
		if(vDto!=null&&vDto.getSdealno()!=null)
		{
			try {
				if(execDetail==null)
					execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = " select * from TF_RECONCILE_PAYQUOTA_MAIN where I_VOUSRLNO="+vDto.getSdealno();
				List<IDto> dataList = (List<IDto>)execDetail.runQuery(sql,TfReconcilePayquotaMainDto.class).getDtoCollection();
				if(dataList!=null&&dataList.size()>0)
				{
					dataDto = new MainAndSubDto();
					dataDto.setMainDto(dataList.get(0));
					sql = "select * TF_RECONCILE_PAYQUOTA_SUB where I_VOUSRLNO="+vDto.getSdealno();
					dataDto.setSubDtoList((List<IDto>)execDetail.runQuery(sql,TfReconcilePayquotaSubDto.class).getDtoCollection());
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
	private Object getObject(Map map,TfReconcilePayquotaSubDto sub3510dto)
	{
		Object getObject = null;
		if(map!=null&&sub3510dto!=null)
		{
			//预算单位编码+资金性质编码+代理银行行号+支出功能分类科目编码+支付方式编码(11直接支付12授权支付)
			getObject = map.get(sub3510dto.getSsupdepcode()+sub3510dto.getSfundtypecode()+sub3510dto.getSpaybankcode()+sub3510dto.getSexpfunccode()+sub3510dto.getSpaytypecode());
		}
		return getObject;
	}
	private String getString(String key)
	{
		if(key==null)
			key="";
		return key;
	}
}

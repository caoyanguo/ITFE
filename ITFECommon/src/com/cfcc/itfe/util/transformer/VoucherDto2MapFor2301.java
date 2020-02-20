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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 直接支付清算回单（2301）
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor2301 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor2301.class);
	private Map<String,TsInfoconnorgaccDto> accMap = null;
	/**
	 * 凭证生成
	 * @param vDto
	 * @return
	 * @throws ITFEBizException
	*/
	public List voucherGenerate(TvVoucherinfoDto vDto) throws ITFEBizException{		
		if(vDto==null)
			return null;
		vDto.setScreatdate(TimeFacade.getCurrentStringTime());
		return getVoucher(vDto);	
	}
	private List getVoucher(TvVoucherinfoDto vDto) throws ITFEBizException
	{
		List returnList = new ArrayList();
		TsConvertfinorgDto cDto=new TsConvertfinorgDto();
		cDto.setStrecode(vDto.getStrecode());
		SQLExecutor execDetail = null;
		try {
			List<TsConvertfinorgDto> tsConvertfinorgList=(List<TsConvertfinorgDto>) CommonFacade.getODB().findRsByDto(cDto);
			if(tsConvertfinorgList==null||tsConvertfinorgList.size()==0){
				throw new ITFEBizException("国库："+vDto.getStrecode()+"对应的财政机关代码参数未维护！");
			}
			cDto=(TsConvertfinorgDto) tsConvertfinorgList.get(0);
			vDto.setSadmdivcode(cDto.getSadmdivcode());
			if(cDto.getSadmdivcode()==null||cDto.getSadmdivcode().equals("")){				
				throw new ITFEBizException("国库："+cDto.getStrecode()+"对应的区划代码未维护！");
			}
			List<TsInfoconnorgaccDto> accList = null;
			TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
			accdto.setStrecode(vDto.getStrecode());
			accdto.setSorgcode(vDto.getSorgcode());
			accList = (List<TsInfoconnorgaccDto>) CommonFacade.getODB()
					.findRsByDto(accdto);
			if (accList != null && accList.size() > 0) {
				accMap = new HashMap<String, TsInfoconnorgaccDto>();
				for (TsInfoconnorgaccDto tempdto : accList) {
					if (tempdto.getSpayeraccount().indexOf(
							vDto.getSorgcode() + "271") >= 0)
						accMap.put("271", tempdto); // 上海市财政局国库存款户
					else if (tempdto.getSpayeraccount().indexOf(vDto.getSorgcode() + "371") >= 0 && !(tempdto.getSpayername().indexOf("授权") >= 0))
						accMap.put("371", tempdto);
				}
			}
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			List<MainAndSubDto> dataList = getDataList(vDto,execDetail);
			if(dataList!=null&&dataList.size()>0)
			{
				List<MainAndSubDto> sendList = null;
				List mapList=null;
				for(int k=0;k<dataList.size();k++)
				{
					sendList = new ArrayList<MainAndSubDto>();
					sendList.add(dataList.get(k));
					mapList=new ArrayList();
					String FileName=null;
					String dirsep = File.separator; 
					String mainvou=VoucherUtil.getGrantSequence();
					vDto.setSdealno(mainvou);
					vDto.setSvoucherno(mainvou);
					FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+mainvou+ ".msg";
					TvVoucherinfoDto dto=new TvVoucherinfoDto();			
					dto.setSorgcode(vDto.getSorgcode());
					dto.setSadmdivcode(vDto.getSadmdivcode());
					dto.setSvtcode(vDto.getSvtcode());
					dto.setScreatdate(TimeFacade.getCurrentStringTime());
					dto.setStrecode(vDto.getStrecode());
					dto.setSfilename(FileName);
					dto.setSdealno(mainvou);		
					dto.setSstyear(dto.getScreatdate().substring(0, 4));				
					dto.setScheckdate(vDto.getScheckdate());
					dto.setShold1(vDto.getShold1());//批量单笔标记4批量1、3单笔
					dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					dto.setSdemo("处理成功");	
					dto.setSvoucherflag("1");
					dto.setSvoucherno(mainvou);	
					dto.setIcount(sendList.size());
					mapList.add(vDto);
					mapList.add(sendList);
					List<IDto> idtoList = new ArrayList<IDto>();
					Map map=tranfor(mapList,idtoList);
					dto.setNmoney(MtoCodeTrans.transformBigDecimal(((Map)map.get("Voucher")).get("XPayAmt")));
					dto.setIcount(Integer.valueOf(String.valueOf(((Map)map.get("Voucher")).get("AllNum"))));
					List vouList=new ArrayList();
					vouList.add(map);
					vouList.add(dto);
					vouList.add(idtoList);
					returnList.add(vouList);
				}
			}
		} catch (JAFDatabaseException e2) {		
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		} finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return returnList;
	}
	private Map tranfor(List mapList,List<IDto> idtoList) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) mapList.get(0);
			List<IDto> detailList=(List<IDto>) mapList.get(1);
			MainAndSubDto mdto = (MainAndSubDto)detailList.get(0);
			TfDirectpaymsgmainDto maindto = null;
			TfDirectpaymsgsubDto subdto = null;
			
			
			maindto = (TfDirectpaymsgmainDto)mdto.getMainDto();
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
			vouchermap.put("Id",getString(vDto.getSvoucherno()));//申请划款凭证Id
			vouchermap.put("AdmDivCode",getString(vDto.getSadmdivcode()));//行政区划代码
			vouchermap.put("StYear",getString(vDto.getScreatdate().substring(0,4)));//业务年度
			vouchermap.put("VtCode",getString(vDto.getSvtcode()));//凭证类型编号
			vouchermap.put("VouDate",getString(vDto.getScreatdate()));//凭证日期
			vouchermap.put("VoucherNo",getString(vDto.getSvoucherno()));//凭证号
			vouchermap.put("TreCode",getString(vDto.getStrecode()));//国库主体代码
			vouchermap.put("FinOrgCode",getString(maindto.getSfinorgcode()));//财政机关代码
			vouchermap.put("BgtTypeCode",getString(maindto.getSbgttypecode()));//预算类型编码
			vouchermap.put("BgtTypeName",getString(maindto.getSbgttypename()));//预算类型名称
			vouchermap.put("FundTypeCode",getString(maindto.getSfundtypecode()));//资金性质编码
			vouchermap.put("FundTypeName",getString(maindto.getSfundtypename()));//资金性质名称
			vouchermap.put("PayTypeCode","11");//支付方式编码
			vouchermap.put("PayTypeName","直接支付");//支付方式名称
			vouchermap.put("AgentAcctNo",getString(accMap.get("371").getSpayeraccount()));//收款银行账号
			vouchermap.put("AgentAcctName",getString(accMap.get("371").getSpayername()));//收款银行账户名称
			vouchermap.put("AgentAcctBankName",getString(maindto.getSpayacctbankname()));//收款银行
			vouchermap.put("ClearAcctNo",getString(accMap.get("271").getSpayeraccount()));//付款账号271
			vouchermap.put("ClearAcctName",getString(accMap.get("271").getSpayername()));//付款账户名称
			vouchermap.put("ClearAcctBankName",getString(maindto.getSpayacctbankname()));//付款银行
//			vouchermap.put("PayAmt",getString(""));//汇总清算金额
			//如果是批量的取系统带过来的    
			if("4".equals(vDto.getShold1())){
				vouchermap.put("PayBankName",getString(maindto.getSpaybankname()));//代理银行名称
				vouchermap.put("PayBankNo",getString(maindto.getSpaybankcode()));//代理银行行号
			}else{
				vouchermap.put("PayBankName",getString(maindto.getSinputrecbankname()));//代理银行名称  系统补录
				vouchermap.put("PayBankNo",getString(maindto.getSpayeeacctbankno()));//代理银行行号    系统补录
			}
			
			vouchermap.put("Remark",getString(""));//摘要
			vouchermap.put("MoneyCorpCode",getString(""));//金融机构编码
			vouchermap.put("XPaySndBnkNo",getString(""));//支付发起行行号
			vouchermap.put("XAddWord",getString(""));//附言 
			vouchermap.put("XClearDate",getString(vDto.getScheckdate()));//清算日期
			vouchermap.put("XPayAmt",getString(""));//汇总清算金额
			vouchermap.put("Hold1",getString(""));//预留字段1
			vouchermap.put("Hold2",getString(""));//预留字段2
			BigDecimal allamt=new BigDecimal("0.00");
			BigDecimal xallamt=new BigDecimal("0.00");
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			for(IDto idto:detailList)
			{
				id++;
				mdto = (MainAndSubDto)idto;
				maindto = (TfDirectpaymsgmainDto)mdto.getMainDto();
				if(mdto!=null&&mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0)
				{
					HashMap<String, Object> Detailmap = new HashMap<String, Object>();
					Detailmap.put("Id",String.valueOf(maindto.getIvousrlno())+id);//序号
					Detailmap.put("VoucherNo",getString(maindto.getSvoucherno()));//支付凭证单号
					Detailmap.put("SupDepCode",getString(((TfDirectpaymsgsubDto)mdto.getSubDtoList().get(0)).getSagencycode()));//一级预算单位编码	取明细基层代码
					Detailmap.put("SupDepName",getString(((TfDirectpaymsgsubDto)mdto.getSubDtoList().get(0)).getSagencyname()));//一级预算单位名称	取明细基层代码
					Detailmap.put("ExpFuncCode",getString(maindto.getSexpfunccode()));//支出功能分类科目编码
					Detailmap.put("ExpFuncName",getString(maindto.getSexpfuncname()));//支出功能分类科目名称
					Detailmap.put("PaySummaryName",getString(""));//摘要名称
					Detailmap.put("Hold1",getString(""));//预留字段1
					Detailmap.put("Hold2",getString(""));//预留字段2
					Detailmap.put("Hold3",getString(""));//预留字段3
					Detailmap.put("Hold4",getString(""));//预留字段4
					
					for(int i=0;i<mdto.getSubDtoList().size();i++)
					{
						subdto = (TfDirectpaymsgsubDto)mdto.getSubDtoList().get(i);
						if(subdto.getNxpayamt()!=null)
							xallamt=xallamt.add(subdto.getNxpayamt());
						else
							xallamt=xallamt.add(subdto.getNpayamt());
						allamt=allamt.add(subdto.getNpayamt());
					}
					Detailmap.put("PayAmt",allamt.compareTo(BigDecimal.ZERO) == 0 ? "0.00" : MtoCodeTrans.transformString(allamt));//支付金额
					Detail.add(Detailmap);
				}
			}
			vouchermap.put("AllNum",id);//总笔数
			vouchermap.put("PayAmt",allamt.compareTo(BigDecimal.ZERO) == 0 ? "0.00" : MtoCodeTrans.transformString(allamt));//总金额
			vouchermap.put("XPayAmt",xallamt.compareTo(BigDecimal.ZERO) == 0 ? "0.00" : MtoCodeTrans.transformString(xallamt));//总金额
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
//	
//	private TfReconcilePayquotaMainDto getMainDto(Map<String, Object> mainMap,TvVoucherinfoDto vDto)
//	{
//		TfReconcilePayquotaMainDto mainDto = new TfReconcilePayquotaMainDto();
//		mainDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
//		mainDto.setSdemo("处理成功");
//		mainDto.setSorgcode(vDto.getSorgcode());
//		mainDto.setIvousrlno(Long.valueOf(getString(mainMap,"VoucherNo")));
//		mainDto.setSadmdivcode(getString(mainMap,"AdmDivCode"));//AdmDivCode",vDto.getSadmdivcode());//行政区划代码
//		mainDto.setSstyear(getString(mainMap,"StYear"));//StYear",vDto.getScreatdate().substring(0,4));//业务年度
//		mainDto.setSvtcode(getString(mainMap,"VtCode"));//VtCode",vDto.getSvtcode());//凭证类型编号
//		mainDto.setSvoudate(getString(mainMap,"VouDate"));//VouDate",vDto.getScreatdate());//凭证日期
//		mainDto.setSvoucherno(getString(mainMap,"VoucherNo"));//VoucherNo",vDto.getSvoucherno());//凭证号
//		mainDto.setSvouchercheckno(getString(mainMap,"VoucherCheckNo"));//VoucherCheckNo",danhao);//对账单号
//		mainDto.setSchildpacknum(getString(mainMap,"ChildPackNum"));//ChildPackNum",count);//子包总数
//		mainDto.setScurpackno(getString(mainMap,"CurPackNo"));//CurPackNo",xuhao);//本包序号
//		mainDto.setStrecode(getString(mainMap,"TreCode"));//TreCode",vDto.getStrecode());//国库主体代码
//		mainDto.setSclearbankcode(getString(mainMap,"ClearBankCode"));//人民银行编码
//		mainDto.setSclearbankname(getString(mainMap,"ClearBankName"));//人民银行名称
//		mainDto.setSclearaccno(getString(mainMap,"ClearAccNo"));//清算账号
//		mainDto.setSclearaccnanme(getString(mainMap,"ClearAccNanme"));//清算账户名称
//		mainDto.setSbegindate(getString(mainMap,"BeginDate"));//BeginDate",vDto.getScheckdate());//对账起始日期
//		mainDto.setSenddate(getString(mainMap,"EndDate"));//EndDate",vDto.getSpaybankcode());//对账终止日期
//		mainDto.setSallnum(getString(mainMap,"AllNum"));//AllNum",detailList.size());//总笔数
//		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,"AllAmt")));//AllAmt","");//总金额
//		mainDto.setShold1(getString(mainMap,"Hold1"));//Hold1","");//预留字段1
//		mainDto.setShold2(getString(mainMap,"Hold2"));//Hold2","");//预留字段2
//		return mainDto;
//	}
//	private TfReconcilePayquotaSubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
//	{
//		TfReconcilePayquotaSubDto subDto = new TfReconcilePayquotaSubDto();
//		subDto.setIvousrlno(Long.valueOf(getString(mainMap,"VoucherNo")));
//		subDto.setIseqno(Long.valueOf(getString(subMap,"Id")));//Id",vDto.getSdealno()+(++id));//序号
//		subDto.setSid(getString(subMap,"Id"));
//		subDto.setSsupdepcode(getString(subMap,"SupDepCode"));//一级预算单位编码
//		subDto.setSsupdepname(getString(subMap,"SupDepName"));//一级预算单位名称
//		subDto.setSfundtypecode(getString(subMap,"FundTypeCode"));//资金性质编码
//		subDto.setSfundtypename(getString(subMap,"FundTypeName"));//资金性质名称
//		subDto.setSpaybankcode(getString(subMap,"PayBankCode"));//代理银行编码
//		subDto.setSpaybankname(getString(subMap,"PayBankName"));//代理银行名称
//		subDto.setSpaybankno(getString(subMap,"PayBankNo"));//代理银行行号
//		subDto.setSexpfunccode(getString(subMap,"ExpFuncCode"));//支出功能分类科目编码
//		subDto.setSexpfuncname(getString(subMap,"ExpFuncName"));//支出功能分类科目名称
//		subDto.setSprocatcode(getString(subMap,"ProCatCode"));//收支管理编码
//		subDto.setSprocatname(getString(subMap,"ProCatName"));//收支管理名称
//		subDto.setSpaytypecode(getString(subMap,"PayTypeCode"));//支付方式编码
//		subDto.setSpaytypename(getString(subMap,"PayTypeName"));//支付方式名称
//		subDto.setNpredatemoney(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"PreDateMoney"))?"0":getString(subMap,"PreDateMoney")));//上期额度余额
//		subDto.setNclearamt(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"ClearAmt"))?"0":getString(subMap,"ClearAmt")));//本期新增清算额度
//		subDto.setNcurreckmoney(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"CurReckMoney"))?"0":getString(subMap,"CurReckMoney")));//本期已清算额度
//		subDto.setNcurdatemoney(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"CurDateMoney"))?"0":getString(subMap,"CurDateMoney")));//本期额度余额
//		subDto.setShold1(getString(subMap,"Hold1"));//Hold1","");//预留字段1
//		subDto.setShold2(getString(subMap,"Hold2"));//Hold2","");//预留字段2
//		subDto.setShold3(getString(subMap,"Hold3"));//Hold3","");//预留字段3
//		subDto.setShold4(getString(subMap,"Hold4"));//Hold4","");//预留字段4
//		return subDto;
//	}
//	private String getString(Map datamap,String key)
//	{
//		if(datamap==null||key==null)
//			return "";
//		else
//			return String.valueOf(datamap.get(key));
//	}
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	private List<MainAndSubDto> getDataList(TvVoucherinfoDto vDto,SQLExecutor execDetail) throws ITFEBizException
	{
		MainAndSubDto datadto = null;
		StringBuffer sql = null;
		List<MainAndSubDto> dataList = null;
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sql = new StringBuffer("SELECT * FROM TF_DIRECTPAYMSGMAIN WHERE S_TRECODE='");//查询-财政直接支付凭证5201主表
			sql.append(vDto.getStrecode()+"' and S_XPAYDATE='"+vDto.getScheckdate()+"' and S_STATUS='"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' ");
			if("4".equals(vDto.getShold1()))//批量
				sql.append(" and S_BUSINESSTYPECODE='4' order by I_VOUSRLNO desc");
			else//单笔
				sql.append(" and (S_BUSINESSTYPECODE='1' or S_BUSINESSTYPECODE='3') order by I_VOUSRLNO desc");
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TfDirectpaymsgmainDto.class).getDtoCollection();//查询数据
			if(detailList!=null&&detailList.size()>0)
			{
				StringBuffer subsql = new StringBuffer("select * from TF_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+") order by I_VOUSRLNO desc");//财政直接支付凭证5201子表数据
				datadto = new MainAndSubDto();
				datadto.setMainDtoList(detailList);
				datadto.setSubDtoList((List<IDto>) execDetail.runQuery(subsql.toString(),TfDirectpaymsgsubDto.class).getDtoCollection());//子表数据
			}
			if(datadto!=null&&datadto.getMainDtoList()!=null&&datadto.getMainDtoList().size()>0&&datadto.getSubDtoList()!=null&&datadto.getSubDtoList().size()>0)
			{
				Map<String,MainAndSubDto> tempMap = new HashMap<String,MainAndSubDto>();
				TfDirectpaymsgmainDto dto = null;
				MainAndSubDto tempdto = null;
				for(IDto idto:datadto.getMainDtoList())
				{
					dto = (TfDirectpaymsgmainDto)idto;
					tempdto = new MainAndSubDto();
					tempdto.setMainDto(dto);
					tempMap.put(String.valueOf(dto.getIvousrlno()), tempdto);
				}
				TfDirectpaymsgsubDto subdto = null;
				for(IDto idto:datadto.getSubDtoList())
				{
					subdto = (TfDirectpaymsgsubDto)idto;
					tempdto = tempMap.get(String.valueOf(subdto.getIvousrlno()));
					if(tempdto!=null)
					{
						if(tempdto.getSubDtoList()!=null)
							tempdto.getSubDtoList().add(subdto);
						else
						{
							tempdto.setSubDtoList(new ArrayList<IDto>());
							tempdto.getSubDtoList().add(subdto);
						}
					}	
				}
				if(tempMap!=null&&tempMap.size()>0)
				{
					dataList = new ArrayList<MainAndSubDto>();
					for(String mapkey:tempMap.keySet())
						dataList.add(tempMap.get(mapkey));
				}
			}
		} catch (JAFDatabaseException e) {
			if(execDetail!=null)
				execDetail.closeConnection();
			logger.error(e);
			throw new ITFEBizException("查询"+sql==null?"":sql.toString()+"库存明细信息异常！",e);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return dataList;
	}
	private List<List<MainAndSubDto>> getSplitPack(List<MainAndSubDto> dataList)
	{
		List<List<MainAndSubDto>> getList = null;
		if(dataList!=null&&dataList.size()>0)
		{
			Map<String,List<MainAndSubDto>> tempMap = new HashMap<String,List<MainAndSubDto>>();
			List<MainAndSubDto> tempList = null;
			MainAndSubDto dto = null;
			for(IDto idto:dataList)
			{
				dto = (MainAndSubDto)idto;
				TfDirectpaymsgmainDto pdto = (TfDirectpaymsgmainDto)dto.getMainDto();
				if(tempMap.get(pdto.getSbgttypecode())==null)
				{
					tempList = new ArrayList<MainAndSubDto>();
					tempList.add(dto);
					tempMap.put(pdto.getSbgttypecode(), tempList);
				}else{
					tempList = tempMap.get(pdto.getSbgttypecode());
					tempList.add(dto);
					tempMap.put(pdto.getSbgttypecode(), tempList);
				}
			}
			if(tempMap!=null&&tempMap.size()>0)
			{
				getList = new ArrayList<List<MainAndSubDto>>();
				for(String mapkey:tempMap.keySet())
					getList.add(tempMap.get(mapkey));
			}
		}
		return getList;
	}
	private String getString(String key)
	{
		if(key==null)
			key="";
		return key;
	}
	public Map<String, TsInfoconnorgaccDto> getAccMap() {
		return accMap;
	}
	public void setAccMap(Map<String, TsInfoconnorgaccDto> accMap) {
		this.accMap = accMap;
	}
}

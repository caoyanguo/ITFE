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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayquotaMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayquotaSubDto;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.CommonQto;
import com.cfcc.jaf.persistence.util.SqlUtil;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 清算额度对账（3510）
 * 
 * @author hjr
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3580 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3580.class);
	private Map<String,String> bankmap = null;
	private HashMap<String, TsBudgetsubjectDto> subjectMap = null;
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
			if(bankmap==null)
			{
				TsConvertbanktypeDto finddto = new TsConvertbanktypeDto();
				finddto.setSorgcode(vDto.getSorgcode());
				List<TsConvertbanktypeDto> resultList = CommonFacade.getODB().findRsByDto(finddto);
				if(resultList!=null&&resultList.size()>0)
				{
					bankmap = new HashMap<String,String>();
					for(TsConvertbanktypeDto temp:resultList)
					{
						bankmap.put(temp.getSbankcode()+temp.getStrecode(), temp.getSbankname());
					}
				}
			}
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			List<IDto> alldata = getDataList(vDto,execDetail);
			if(alldata!=null&&alldata.size()>0){
				subjectMap = SrvCacheFacade.cacheTsBdgsbtInfo(vDto.getSorgcode());
				//获取全部不可录入的科目代码
				TsBudgetsubjectDto tdto = new TsBudgetsubjectDto();
				tdto.setSorgcode(vDto.getSorgcode());
				tdto.setSwriteflag(StateConstant.COMMON_NO);
				CommonQto qto = SqlUtil.IDto2CommonQto(tdto);
				List <TsBudgetsubjectDto> tlist =  DatabaseFacade.getODB().findWithUR(tdto.getClass(), qto);
				for (TsBudgetsubjectDto t :tlist) {
					subjectMap.put(t.getSsubjectcode(), t);
				}
				if(vDto.getSorgcode().startsWith("13")){//福建对账
					Map<String, List>  map = getFundTypeMap(alldata);
					for(java.util.Map.Entry<String, List> entry : map.entrySet()){
						createVoucher(vDto, returnList, cDto, entry.getValue());
					}
				}else {
					if(ITFECommonConstant.PUBLICPARAM.contains(",3510pack=paybank,"))
					{
						Map<String, List>  map = getPaybankAmtTypeMap(alldata);
						for(java.util.Map.Entry<String, List> entry : map.entrySet())
							createVoucher(vDto, returnList, cDto, entry.getValue());
					}else
					{
						createVoucher(vDto, returnList, cDto, alldata);
					}
				}
			}
		}catch (JAFDatabaseException e2) {		
			logger.error(e2.getMessage(),e2);
			throw new ITFEBizException("查询信息异常！",e2);
		}catch (ValidateException e2) {
			logger.error(e2.getMessage(),e2);
			throw new ITFEBizException("查询信息异常！",e2);
		}catch (Exception e2) {
			logger.error(e2.getMessage(),e2);
			throw new ITFEBizException("查询信息异常！",e2);
		}finally
		{
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		return returnList;
	}
	private void createVoucher(TvVoucherinfoDto vDto, List returnList,
			TsConvertfinorgDto cDto, List<IDto> alldata)
			throws ITFEBizException {
		List<Map<String,String>> dataList = getSplitPack(alldata);
		if(dataList!=null&&dataList.size()>0)
		{
			List<List> sendList = null;
			List mapList=null;
			sendList = this.getSubLists(dataList, 500);
			if(sendList!=null&&sendList.size()>0)
			{
				BigDecimal preDateMoney=new BigDecimal("0.00");
				BigDecimal clearAmt=new BigDecimal("0.00");
				BigDecimal curReckMoney=new BigDecimal("0.00");
				BigDecimal curDateMoney=new BigDecimal("0.00");
				String temp=null;
				for(Map<String,String> dataMap:dataList)
				{
					temp = dataMap.get("PreDateMoney");//上期额度余额
					if(temp!=null&&!"".equals(temp)&&!"0".equals(temp))
						preDateMoney = preDateMoney.add(new BigDecimal(temp));
					temp = dataMap.get("ClearAmt");//本期新增清算额度
					if(temp!=null&&!"".equals(temp)&&!"0".equals(temp))
						clearAmt = clearAmt.add(new BigDecimal(temp));
					temp = dataMap.get("CurReckMoney");//本期已清算额度
					if(temp!=null&&!"".equals(temp)&&!"0".equals(temp))
						curReckMoney = curReckMoney.add(new BigDecimal(temp));
					temp = dataMap.get("CurDateMoney");//本期额度余额
					if(temp!=null&&!"".equals(temp)&&!"0".equals(temp))
						curDateMoney = curDateMoney.add(new BigDecimal(temp));
					
				}
				List<Map<String,String>> tempList = null;
				String danhao=null;
				for(int i=0;i<sendList.size();i++)
				{
					mapList=new ArrayList();
					tempList = sendList.get(i);
					String FileName=null;
					String dirsep = File.separator; 
					String mainvou = "";
		            if (ITFECommonConstant.SRC_NODE.equals("201053200014"))
		              mainvou = VoucherUtil.getCheckNo(vDto,new ArrayList<IDto>(),i);
		            else {
		              mainvou = VoucherUtil.getGrantSequence();
		            }
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
					dto.setSstyear(dto.getScreatdate().substring(0, 4));				
					dto.setScheckdate(vDto.getScheckdate());
					dto.setSpaybankcode(vDto.getSpaybankcode()==null?"":vDto.getSpaybankcode());
					dto.setShold3(vDto.getShold3());
					dto.setShold4(vDto.getShold4());
					dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
					dto.setSdemo("处理成功");
					dto.setSvoucherflag("1");
					dto.setSvoucherno(mainvou);
					dto.setSattach(danhao);//对账单号
					dto.setShold1(String.valueOf(sendList.size()));//包总数
					dto.setShold2(String.valueOf(i+1));//包序号
					dto.setSext2(getString(vDto.getSext2()));
					dto.setSext3(getString(vDto.getSext3()));
					dto.setSext4(getString(vDto.getSext4()));
					dto.setSext5(getString(vDto.getSext5()));
					dto.setSext4(vDto.getSext4());//厦门个性化标示是tcbs导入数据生成
					dto.setIcount(tempList.size());
					dto.setSext1(getString(vDto.getSext1()));//发起方式1:人行发起,2:财政发起,3:商行发起
					mapList.add(vDto);
					mapList.add(cDto);
					mapList.add(tempList);
					List<IDto> idtoList = new ArrayList<IDto>();
					Map map=tranfor(mapList,sendList.size(),i+1,danhao,idtoList);
					dto.setNmoney(MtoCodeTrans.transformBigDecimal(((Map)map.get("Voucher")).get("AllAmt")));
					dto.setIcount(Integer.valueOf(String.valueOf(((Map)map.get("Voucher")).get("AllNum"))));
					((Map)map.get("Voucher")).put("PreDateMoney", preDateMoney);
					((Map)map.get("Voucher")).put("ClearAmt", clearAmt);
					((Map)map.get("Voucher")).put("CurReckMoney", curReckMoney);
					((Map)map.get("Voucher")).put("CurDateMoney", curDateMoney);
					if(dto.getSpaybankcode()==null||"".equals(dto.getSpaybankcode()))
						dto.setSpaybankcode(tempList.get(0).get("PayBankCode"));
//					if(dto.getSext1()==null||"".equals(dto.getSext1()))
//						dto.setSext1("11".equals(tempList.get(0).get("PayTypeCode"))?"0":"1");
					List vouList=new ArrayList();
					vouList.add(map);
					vouList.add(dto);
					vouList.add(idtoList);
					returnList.add(vouList);
				}
			}
		}
	}
	private Map tranfor(List mapList,int count,int xuhao,String danhao,List<IDto> idtoList) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) mapList.get(0);
//			TsConvertfinorgDto cDto = (TsConvertfinorgDto)mapList.get(1);
			List<Map<String,String>> detailList=(List<Map<String,String>>) mapList.get(2);
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
			vouchermap.put("TreCode",vDto.getStrecode());//国库主体代码
			vouchermap.put("ClearAccNo","");//清算账号
			vouchermap.put("ClearAccNanme","");//清算账户名称
			TsTreasuryDto dto = SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode());
			if(vDto.getSorgcode().startsWith("13")){//福建对账
				vouchermap.put("ClearBankCode","001001");//预留字段1
				vouchermap.put("ClearBankName",dto.getStrename());//预留字段2
			}else
			{
				vouchermap.put("ClearBankCode",dto.getStrecode());//人民银行编码
				vouchermap.put("ClearBankName",dto.getStrename());//人民银行名称
			}
			vouchermap.put("BeginDate",vDto.getShold3());//对账起始日期
			vouchermap.put("EndDate",vDto.getShold4());//对账终止日期
			if(vDto.getSorgcode().startsWith("02")&&(MsgConstant.grantPay.equals(vDto.getSext1())||"12".equals(vDto.getSext1())))//天津的支付方式为001002和001001
			{
				vouchermap.put("PayTypeCode",getString("001002"));//支付方式编码;
				vouchermap.put("PayTypeName","授权支付");//支付方式名称
			}
			else if(vDto.getSorgcode().startsWith("02")&&(MsgConstant.directPay.equals(vDto.getSext1())||"11".equals(vDto.getSext1())))//天津的支付方式为001002和001001
			{
				vouchermap.put("PayTypeCode",getString("001001"));//支付方式编码;
				vouchermap.put("PayTypeName","直接支付");//支付方式名称
			}else
			{
				vouchermap.put("PayTypeCode",getString(vDto.getSext1()));//支付方式编码
				if(MsgConstant.grantPay.equals(vDto.getSext1())||"12".equals(vDto.getSext1()))
					vouchermap.put("PayTypeName","授权支付");//支付方式名称
				else if(MsgConstant.directPay.equals(vDto.getSext1())||"12".equals(vDto.getSext1()))
					vouchermap.put("PayTypeName","直接支付");//支付方式名称
				else
					vouchermap.put("PayTypeName","全部");//支付方式名称
			}
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
			{
				vouchermap.put("BgtTypeCode",vDto.getSext3());//预算类型
				vouchermap.put("BgtTypeName","1".equals(vDto.getSext3())?"预算内":"预算外");//预算类型名称
			}
			if(vDto.getStrecode().startsWith("13")){//福州预留字段添加预算类型
				vouchermap.put("Hold1",detailList.get(0).get("FundTypeCode"));//预留字段1
				vouchermap.put("Hold2",detailList.get(0).get("FundTypeName"));//预留字段2
			}else{
				vouchermap.put("Hold1",(detailList!=null&&detailList.size()>0)?detailList.get(0).get("PayBankCode"):"");//预留字段1
				vouchermap.put("Hold2",getString(vDto.getSverifyusercode()));//预留字段2
			}
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			for(Map<String,String> dataMap:detailList)
			{
				id++;
				HashMap<String, Object> Detailmap = new HashMap<String, Object>();
				Detailmap.put("Id",dataMap.get("Id"));//序号
				Detailmap.put("SupDepCode",dataMap.get("SupDepCode"));//一级预算单位编码
				Detailmap.put("SupDepName",dataMap.get("SupDepName"));//一级预算单位名称
				Detailmap.put("FundTypeCode",dataMap.get("FundTypeCode"));//资金性质编码
				Detailmap.put("FundTypeName",dataMap.get("FundTypeName"));//资金性质名称
				Detailmap.put("PayBankCode",dataMap.get("PayBankCode"));//代理银行编码
				Detailmap.put("PayBankName",dataMap.get("PayBankName"));//代理银行名称
				Detailmap.put("PayBankNo",dataMap.get("PayBankNo"));//代理银行行号
				Detailmap.put("ExpFuncCode",dataMap.get("ExpFuncCode"));//支出功能分类科目编码
				Detailmap.put("ExpFuncName",dataMap.get("ExpFuncName"));//支出功能分类科目名称
				Detailmap.put("ProCatCode",dataMap.get("ProCatCode"));//收支管理编码
				Detailmap.put("ProCatName",dataMap.get("ProCatName"));//收支管理名称
//				Detailmap.put("PayTypeCode",dataMap.get("PayTypeCode"));//支付方式编码
				if(vDto.getSorgcode().startsWith("02")&&(MsgConstant.grantPay.equals(dataMap.get("PayTypeCode"))||"12".equals(dataMap.get("PayTypeCode"))))//天津的支付方式为001002和001001
				{
					Detailmap.put("PayTypeCode",getString("001002"));//支付方式编码;
					Detailmap.put("PayTypeName","授权支付");//支付方式名称
				}
				else if(vDto.getSorgcode().startsWith("02")&&(MsgConstant.directPay.equals(dataMap.get("PayTypeCode"))||"11".equals(dataMap.get("PayTypeCode"))))//天津的支付方式为001002和001001
				{
					Detailmap.put("PayTypeCode",getString("001001"));//支付方式编码;
					Detailmap.put("PayTypeName","直接支付");//支付方式名称
				}
				else
				{
					Detailmap.put("PayTypeCode",getString(dataMap.get("PayTypeCode")));//支付方式编码
					if(MsgConstant.grantPay.equals(dataMap.get("PayTypeCode"))||"12".equals(dataMap.get("PayTypeCode")))
						Detailmap.put("PayTypeName","授权支付");//支付方式名称
					else if(MsgConstant.directPay.equals(dataMap.get("PayTypeCode"))||"12".equals(dataMap.get("PayTypeCode")))
						Detailmap.put("PayTypeName","直接支付");//支付方式名称
					else
						Detailmap.put("PayTypeName","全部");//支付方式名称
				}
				Detailmap.put("PreDateMoney",(dataMap.get("PreDateMoney")==null||"".equals(String.valueOf(dataMap.get("PreDateMoney"))))?"0":dataMap.get("PreDateMoney"));//上期额度余额
				Detailmap.put("ClearAmt",(dataMap.get("ClearAmt")==null||"".equals(String.valueOf(dataMap.get("ClearAmt"))))?"0":dataMap.get("ClearAmt"));//本期新增清算额度
				Detailmap.put("CurReckMoney",(dataMap.get("CurReckMoney")==null||"".equals(String.valueOf(dataMap.get("CurReckMoney"))))?"0":dataMap.get("CurReckMoney"));//本期已清算额度
				Detailmap.put("CurDateMoney",(dataMap.get("CurDateMoney")==null||"".equals(String.valueOf(dataMap.get("CurDateMoney"))))?"0":dataMap.get("CurDateMoney"));//本期额度余额
				Detailmap.put("Hold1",dataMap.get("Hold1"));//预留字段1
				Detailmap.put("Hold2",dataMap.get("Hold2"));//预留字段2
				Detailmap.put("Hold3",dataMap.get("Hold3"));//预留字段3
				Detailmap.put("Hold4",dataMap.get("Hold4"));//预留字段4
				if(dataMap.get("ClearAmt")!=null&&!"null".equals(dataMap.get("ClearAmt")))
					allamt=allamt.add(new BigDecimal(dataMap.get("ClearAmt")));
				Detail.add(Detailmap);
				subdtolist.add(getSubDto(Detailmap,vouchermap));
			}
			vouchermap.put("AllNum",id);//总笔数
			vouchermap.put("AllAmt",MtoCodeTrans.transformString(allamt));//总金额
			idtoList.add(getMainDto(vouchermap,vDto));
			idtoList.addAll(subdtolist);
			HashMap<String, Object> DetailListmap = new HashMap<String, Object>();
			DetailListmap.put("Detail",Detail);
			vouchermap.put("DetailList", DetailListmap);
			return map;		
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
	}
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {		
		return null;
	}
	
	private TfReconcilePayquotaMainDto getMainDto(Map<String, Object> mainMap,TvVoucherinfoDto vDto)
	{
		TfReconcilePayquotaMainDto mainDto = new TfReconcilePayquotaMainDto();
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
		mainDto.setStrecode(getString(mainMap,"TreCode"));//TreCode",vDto.getStrecode());//国库主体代码
		mainDto.setSclearbankcode(getString(mainMap,"ClearBankCode"));//人民银行编码
		mainDto.setSclearbankname(getString(mainMap,"ClearBankName"));//人民银行名称
		mainDto.setSclearaccno(getString(mainMap,"ClearAccNo"));//清算账号
		mainDto.setSclearaccnanme(getString(mainMap,"ClearAccNanme"));//清算账户名称
		mainDto.setSbegindate(getString(mainMap,"BeginDate"));//BeginDate",vDto.getScheckdate());//对账起始日期
		mainDto.setSenddate(getString(mainMap,"EndDate"));//EndDate",vDto.getSpaybankcode());//对账终止日期
		mainDto.setSallnum(getString(mainMap,"AllNum"));//AllNum",detailList.size());//总笔数
		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,"AllAmt")));//AllAmt","");//总金额
		mainDto.setShold1(getString(mainMap,"Hold1"));//Hold1","");//预留字段1
		mainDto.setShold2(getString(mainMap,"Hold2"));//Hold2","");//预留字段2
		mainDto.setSext1("1");//发起方式1:人行发起,2:财政发起,3:商行发起
		return mainDto;
	}
	private TfReconcilePayquotaSubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
	{
		TfReconcilePayquotaSubDto subDto = new TfReconcilePayquotaSubDto();
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		subDto.setIvousrlno(Long.valueOf(voucherno));
		String id = getString(subMap,"Id");
		if(id.length()>19)
			id = id.substring(id.length()-19);
		try {
			if(!id.matches("[0-9]+")){
				subDto.setIseqno(Long.valueOf(VoucherUtil.getGrantSequence()));
			}else{
				subDto.setIseqno(Long.valueOf(id));//Id",vDto.getSdealno()+(++id));//序号
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}
		subDto.setSid(getString(subMap,"Id"));
		subDto.setSsupdepcode(getString(subMap,"SupDepCode"));//一级预算单位编码
		subDto.setSsupdepname(getString(subMap,"SupDepName"));//一级预算单位名称
		subDto.setSfundtypecode(getString(subMap,"FundTypeCode"));//资金性质编码
		subDto.setSfundtypename(getString(subMap,"FundTypeName"));//资金性质名称
		subDto.setSpaybankcode(getString(subMap,"PayBankCode"));//代理银行编码
		subDto.setSpaybankname(getString(subMap,"PayBankName"));//代理银行名称
		subDto.setSpaybankno(getString(subMap,"PayBankNo"));//代理银行行号
		subDto.setSexpfunccode(getString(subMap,"ExpFuncCode"));//支出功能分类科目编码
		subDto.setSexpfuncname(getString(subMap,"ExpFuncName"));//支出功能分类科目名称
		subDto.setSprocatcode(getString(subMap,"ProCatCode"));//收支管理编码
		subDto.setSprocatname(getString(subMap,"ProCatName"));//收支管理名称
		subDto.setSpaytypecode(getString(subMap,"PayTypeCode"));//支付方式编码
		subDto.setSpaytypename(getString(subMap,"PayTypeName"));//支付方式名称
		subDto.setNpredatemoney(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"PreDateMoney"))?"0":getString(subMap,"PreDateMoney")));//上期额度余额
		subDto.setNclearamt(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"ClearAmt"))?"0":getString(subMap,"ClearAmt")));//本期新增清算额度
		subDto.setNcurreckmoney(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"CurReckMoney"))?"0":getString(subMap,"CurReckMoney")));//本期已清算额度
		subDto.setNcurdatemoney(MtoCodeTrans.transformBigDecimal("".equals(getString(subMap,"CurDateMoney"))?"0":getString(subMap,"CurDateMoney")));//本期额度余额
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
	public Map tranfor(List list) throws ITFEBizException {
		return null;
	}
	private List<IDto> getDataList(TvVoucherinfoDto vDto,SQLExecutor execDetail) throws ITFEBizException
	{
		List<IDto> getList = null;
		StringBuffer sql = null;
		try {
			List<IDto> detailList=new ArrayList<IDto>();
			if(execDetail==null)
				execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			if("-1".equals(vDto.getSext1()))
				vDto.setSext1(null);
			if ("TCBS".equals(vDto.getSext4()))
			{
				sql = new StringBuffer("SELECT * FROM TN_CONPAYCHECKBILL WHERE S_BOOKORGCODE=? ");
				sql.append(vDto.getSpaybankcode()==null?"":" AND S_BNKNO=? ");
				sql.append(vDto.getSext1()==null?"":" AND C_AMTKIND=? ");
				sql.append(vDto.getSext3()==null?"":" AND S_ECOSBTCODE=? ");
				sql.append(vDto.getStrecode()==null?"":" and S_TRECODE=? ");
				sql.append("AND D_STARTDATE BETWEEN ? AND ? ");
				sql.append(" order by S_BDGORGCODE,S_FUNCSBTCODE ");
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getSext1()!=null)
				{
					if("0".equals(vDto.getSext1()))
						execDetail.addParam("1");
					else
						execDetail.addParam("2");
				}
				if(vDto.getSext3()!=null)
					execDetail.addParam(vDto.getSext3());
				if(vDto.getStrecode()!=null)
					execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(CommonUtil.strToDate(vDto.getShold3()));
				execDetail.addParam(CommonUtil.strToDate(vDto.getShold4()));
				execDetail.setMaxRows(500000);
				detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TnConpaycheckbillDto.class).getDtoCollection();//查询数据
				if(execDetail!=null)
					execDetail.closeConnection();
				MainAndSubDto datadto = new MainAndSubDto();
				datadto.setMainDtoList(detailList);
//				subMap = new HashMap<String,TsBudgetsubjectDto>();
				if(getList==null)
					getList = new ArrayList<IDto>();
				getList.add(datadto);
				return getList;
			}
			if(vDto.getSext1()==null||MsgConstant.grantPay.equals(vDto.getSext1()))
			{
				sql = new StringBuffer("SELECT * FROM HTV_GRANTPAYMSGMAIN WHERE I_VOUSRLNO in(");//查询授权支付已经回单的数据
				sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ? AND S_ORGCODE= ? ");
				sql.append(vDto.getSpaybankcode()==null?"":" AND S_PAYBANKCODE=? ");
				sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
				sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?)");
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					sql.append(" AND S_BUDGETTYPE=? ");
				execDetail.addParam(MsgConstant.VOUCHER_NO_5106);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvGrantpaymsgmainDto.class).getDtoCollection();//历史表主表
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from HTV_GRANTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";//查询授权支付子表数据
					List<IDto> subList = null;
					execDetail.addParam(MsgConstant.VOUCHER_NO_5106);
					execDetail.addParam(vDto.getSorgcode());
					if(vDto.getSpaybankcode()!=null)
						execDetail.addParam(vDto.getSpaybankcode());
					execDetail.addParam(vDto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vDto.getShold3());
					execDetail.addParam(vDto.getShold4());
					if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
						execDetail.addParam(vDto.getSext3());
					execDetail.setMaxRows(500000);
					subList = (List<IDto>)execDetail.runQuery(subsql, TvGrantpaymsgsubDto.class).getDtoCollection();
					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
					if(subList!=null&&subList.size()>0)
					{
						List<IDto> tempList = null;
						TvGrantpaymsgsubDto subdto = null;
						for(IDto tempdto:subList)
						{
							subdto = (TvGrantpaymsgsubDto)tempdto;
							tempList = subMap.get(String.valueOf(subdto.getIvousrlno())+String.valueOf(subdto.getSpackageticketno()));
							if(tempList==null)
							{
								tempList = new ArrayList<IDto>();
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()+String.valueOf(subdto.getSpackageticketno())), tempList);
							}else
							{
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()+String.valueOf(subdto.getSpackageticketno())), tempList);
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
						datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())+String.valueOf(tempdto.getSpackageticketno())));//历史表子表
						getList.add(datadto);
					}
				}
				execDetail.addParam(MsgConstant.VOUCHER_NO_5106);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_GRANTPAYMSGMAIN","TV_GRANTPAYMSGMAIN"),TvGrantpaymsgmainDto.class).getDtoCollection();//正式表主表
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from TV_GRANTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_GRANTPAYMSGMAIN","TV_GRANTPAYMSGMAIN"), "*", "I_VOUSRLNO")+")";//查询
					List<IDto> subList = null;
					execDetail.addParam(MsgConstant.VOUCHER_NO_5106);
					execDetail.addParam(vDto.getSorgcode());
					if(vDto.getSpaybankcode()!=null)
						execDetail.addParam(vDto.getSpaybankcode());
					execDetail.addParam(vDto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vDto.getShold3());
					execDetail.addParam(vDto.getShold4());
					if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
						execDetail.addParam(vDto.getSext3());
					execDetail.setMaxRows(500000);
					subList = (List<IDto>)execDetail.runQuery(subsql, TvGrantpaymsgsubDto.class).getDtoCollection();
					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
					if(subList!=null&&subList.size()>0)
					{
						List<IDto> tempList = null;
						TvGrantpaymsgsubDto subdto = null;
						for(IDto tempdto:subList)
						{
							subdto = (TvGrantpaymsgsubDto)tempdto;
							tempList = subMap.get(String.valueOf(subdto.getIvousrlno())+String.valueOf(subdto.getSpackageticketno()));
							if(tempList==null)
							{
								tempList = new ArrayList<IDto>();
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()+String.valueOf(subdto.getSpackageticketno())), tempList);
							}else
							{
								tempList.add(subdto);
								subMap.put(String.valueOf(subdto.getIvousrlno()+String.valueOf(subdto.getSpackageticketno())), tempList);
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
						datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())+String.valueOf(tempdto.getSpackageticketno())));//正式表子表
						getList.add(datadto);
					}
				}
			}
			if(vDto.getSext1()==null||MsgConstant.directPay.equals(vDto.getSext1()))
			{
				//5108查询直接支付已经回单的数据
				sql = new StringBuffer("SELECT * FROM HTV_DIRECTPAYMSGMAIN WHERE I_VOUSRLNO in(");//查询直接支付已经回单的数据
				sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ? AND S_ORGCODE= ? ");
				sql.append(vDto.getSpaybankcode()==null?"":" AND S_PAYBANKCODE=? ");
				sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
				sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?)");
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					sql.append(" AND S_BUDGETTYPE=? ");
				execDetail.addParam(MsgConstant.VOUCHER_NO_5108);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvDirectpaymsgmainDto.class).getDtoCollection();//历史表主表
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from HTV_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";
					List<IDto> subList = null;
					execDetail.addParam(MsgConstant.VOUCHER_NO_5108);
					execDetail.addParam(vDto.getSorgcode());
					if(vDto.getSpaybankcode()!=null)
						execDetail.addParam(vDto.getSpaybankcode());
					execDetail.addParam(vDto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vDto.getShold3());
					execDetail.addParam(vDto.getShold4());
					if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
						execDetail.addParam(vDto.getSext3());
					execDetail.setMaxRows(500000);
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
				execDetail.addParam(MsgConstant.VOUCHER_NO_5108);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_DIRECTPAYMSGMAIN","TV_DIRECTPAYMSGMAIN"),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvDirectpaymsgmainDto.class).getDtoCollection();//正式表主表
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from TV_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_DIRECTPAYMSGMAIN","TV_DIRECTPAYMSGMAIN"),"HTV_VOUCHERINFO","TV_VOUCHERINFO"), "*", "I_VOUSRLNO")+")";
					List<IDto> subList = null;
					execDetail.addParam(MsgConstant.VOUCHER_NO_5108);
					execDetail.addParam(vDto.getSorgcode());
					if(vDto.getSpaybankcode()!=null)
						execDetail.addParam(vDto.getSpaybankcode());
					execDetail.addParam(vDto.getStrecode());
					execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
					execDetail.addParam(vDto.getShold3());
					execDetail.addParam(vDto.getShold4());
					if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
						execDetail.addParam(vDto.getSext3());
					execDetail.setMaxRows(500000);
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
		return getList;
	}
	private List getSplitPack(List<IDto> dataList)
	{
		List<Map<String,String>> getList = null;
		if(dataList!=null&&dataList.size()>0)
		{
			Map<String,Map> tempMap = new HashMap<String,Map>();
			TvDirectpaymsgmainDto ddto = null;
			TvGrantpaymsgmainDto gdto = null;
			MainAndSubDto dto = null;
			Map<String,String> dataMap = null;
			//预算单位编码+资金性质编码+代理银行行号+支出功能分类科目编码+支付方式编码(11直接支付12授权支付)
			//sub-S_AGENCYCODE+main-S_FUNDTYPECODE+main-S_PAYBANKNO+sub-S_FUNSUBJECTCODE+(11直接支付12授权支付)
			//main-S_BUDGETUNITCODE+main-S_FUNDTYPECODE+main-S_PAYBANKNO+sub-S_FUNSUBJECTCODE+(11直接支付12授权支付)
			for(IDto idto:dataList)
			{
				dto = (MainAndSubDto)idto;
				if(dto.getMainDto()!=null&&dto.getMainDto() instanceof TvDirectpaymsgmainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					
					ddto = (TvDirectpaymsgmainDto)dto.getMainDto();
					TvDirectpaymsgsubDto subdto = null;
					for(int sub=0;sub<dto.getSubDtoList().size();sub++)
					{
						subdto = (TvDirectpaymsgsubDto)dto.getSubDtoList().get(sub);
						//预算单位编码+资金性质编码+代理银行行号+支出功能分类科目编码+支付方式编码(11直接支付12授权支付)
//						if(tempMap.get(subdto.getSagencycode()+ddto.getSfundtypecode()+ddto.getSpaybankno()+subdto.getSfunsubjectcode()+"11")==null)
//						{
							dataMap = new HashMap<String,String>();
							dataMap.put("Id",String.valueOf(ddto.getSid())+(subdto.getSid()==null||"null".equals(subdto.getSid())?subdto.getStaxticketno():subdto.getSid()));//序号
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
							if (ddto.getStrecode().startsWith("33")) {
					              dataMap.put("Id", subdto.getSid());//序号
					              tempMap.put(subdto.getSid(), dataMap);
					        } else {
					              dataMap.put("Id", String.valueOf(ddto.getSid()) + subdto.getSid());//序号
					              tempMap.put(String.valueOf(ddto.getSid()) + subdto.getSid() + "11", dataMap);
					        }
//						}else
//						{
//							dataMap = tempMap.get(subdto.getSagencycode()+ddto.getSfundtypecode()+ddto.getSpaybankno()+subdto.getSfunsubjectcode()+"11");
//							dataMap.put("ClearAmt", String.valueOf(new BigDecimal(dataMap.get("ClearAmt")).add(subdto.getNmoney())));
//						}
					}
					
				}else if(dto.getMainDto()!=null&&dto.getMainDto() instanceof TvGrantpaymsgmainDto&&dto.getSubDtoList()!=null&&dto.getSubDtoList().size()>0)
				{
					gdto = (TvGrantpaymsgmainDto)dto.getMainDto();
					TvGrantpaymsgsubDto subdto = null;
					TsBudgetsubjectDto budto = null;
					for(int sub=0;sub<dto.getSubDtoList().size();sub++)
					{
						subdto = (TvGrantpaymsgsubDto)dto.getSubDtoList().get(sub);
						//预算单位编码+资金性质编码+代理银行行号+支出功能分类科目编码+支付方式编码(11直接支付12授权支付)
//						if(tempMap.get(gdto.getSbudgetunitcode()+gdto.getSfundtypecode()+gdto.getSpaybankno()+subdto.getSfunsubjectcode()+"12")==null)
//						{
							dataMap = new HashMap<String,String>();
							dataMap.put("Id",String.valueOf(gdto.getSid())+(subdto.getSid()==null||"null".equals(subdto.getSid())?subdto.getSdealno():subdto.getSid()));//序号
							dataMap.put("SupDepCode",getString(subdto.getSbudgetunitcode()));//一级预算单位编码
							dataMap.put("SupDepName",getString(subdto.getSsupdepname()));//一级预算单位名称
							dataMap.put("FundTypeCode",getString(gdto.getSfundtypecode()));//资金性质编码
							dataMap.put("FundTypeName",getString(gdto.getSfundtypename()));//资金性质名称
							dataMap.put("PayBankCode",getString(gdto.getSpaybankcode()));//代理银行编码
							dataMap.put("PayBankName",getString(gdto.getSpaybankname()));//代理银行名称
							dataMap.put("PayBankNo",getString(gdto.getSpaybankno()));//代理银行行号
							dataMap.put("ExpFuncCode",getString(subdto.getSfunsubjectcode()));//支出功能分类科目编码
							budto =  subjectMap==null?null:subjectMap.get(subdto.getSfunsubjectcode());
							dataMap.put("ExpFuncName",budto==null?"未知":budto.getSsubjectname());//支出功能分类科目名称
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
							if (ITFECommonConstant.SRC_NODE.equals("201053200014")) {
								dataMap.put("Id",String.valueOf(subdto.getSid()));//序号
					            tempMap.put(subdto.getSid(), dataMap);
					        } else {
					        	dataMap.put("Id",String.valueOf(gdto.getSid())+subdto.getSid());//序号
					            tempMap.put(String.valueOf(gdto.getSid()) + subdto.getSid()+"12",dataMap);
					        }
//						}else
//						{
//							dataMap = tempMap.get(gdto.getSbudgetunitcode()+gdto.getSfundtypecode()+gdto.getSpaybankno()+subdto.getSfunsubjectcode()+"12");
//							dataMap.put("ClearAmt", String.valueOf(new BigDecimal(dataMap.get("ClearAmt")).add(subdto.getNmoney())));
//						}
					}
				}else if(dto.getMainDtoList()!=null&&dto.getMainDtoList().size()>0)
				{
					List<IDto> templist = dto.getMainDtoList();
					TnConpaycheckbillDto tempdto = null;
					TsBudgetsubjectDto budto = null;
					for(IDto bdto:templist)
					{
						tempdto = (TnConpaycheckbillDto)bdto;
						dataMap = new HashMap<String,String>();
						dataMap.put("Id",TimeFacade.getCurrentStringTime()+String.valueOf(tempdto.getIenrolsrlno()));//序号
						dataMap.put("SupDepCode",getString(tempdto.getSbdgorgcode()));//一级预算单位编码
						dataMap.put("SupDepName",getString(tempdto.getSbdgorgname()));//一级预算单位名称
						dataMap.put("FundTypeCode",getString(tempdto.getSecosbtcode()));//资金性质编码
						dataMap.put("FundTypeName",getString("1".equals(tempdto.getSecosbtcode())?"预算内":"预算外"));//资金性质名称
						dataMap.put("PayBankCode",getString(tempdto.getSbnkno()));//代理银行编码
						dataMap.put("PayBankName",getString(getBankmap()==null?"":getBankmap().get(tempdto.getSbnkno()+tempdto.getStrecode())));//代理银行名称
						dataMap.put("PayBankNo",getString(tempdto.getSbnkno()));//代理银行行号
						dataMap.put("ExpFuncCode",getString(tempdto.getSfuncsbtcode()));//支出功能分类科目编码
						budto =  subjectMap==null?null:subjectMap.get(tempdto.getSfuncsbtcode());
						dataMap.put("ExpFuncName",budto==null?"未知":budto.getSsubjectname());//支出功能分类科目名称
						dataMap.put("ProCatCode","99999");//收支管理编码
						dataMap.put("ProCatName","99999");//收支管理名称
						dataMap.put("PayTypeCode","1".equals(tempdto.getCamtkind())?"11":"12");//支付方式编码
						dataMap.put("PayTypeName","1".equals(tempdto.getCamtkind())?"直接支付":"授权支付");//支付方式名称
						dataMap.put("PreDateMoney",String.valueOf(tempdto.getFlastmonthzeroamt()));//上期额度余额
						dataMap.put("ClearAmt",String.valueOf(tempdto.getFcursmallamt()));//本期新增清算额度
						dataMap.put("CurReckMoney",String.valueOf(tempdto.getFcurreckzeroamt()));//本期已清算额度
						dataMap.put("CurDateMoney",String.valueOf(tempdto.getFcurzeroamt()));//本期额度余额
						dataMap.put("Hold1","");//预留字段1
						dataMap.put("Hold2","");//预留字段2
						dataMap.put("Hold3","");//预留字段3
						dataMap.put("Hold4","");//预留字段4
						tempMap.put(String.valueOf(tempdto.getIenrolsrlno())+tempdto.getSbnkno(),dataMap);
					}
				}
			}
			if(tempMap!=null&&tempMap.size()>0)
			{
				getList = new ArrayList<Map<String,String>>();
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
	public Map<String, String> getBankmap() {
		return bankmap;
	}
	public void setBankmap(Map<String, String> bankmap) {
		this.bankmap = bankmap;
	}
	public HashMap<String, TsBudgetsubjectDto> getSubjectMap() {
		return subjectMap;
	}
	public void setSubjectMap(HashMap<String, TsBudgetsubjectDto> subMap) {
		this.subjectMap = subMap;
	}
	
	/**
	 * 将查询结果按资金类型分包
	 * @param alldata
	 * @return
	 */
	private Map<String, List>  getFundTypeMap(List<IDto> alldata){
		Map<String, List> map = new HashMap<String, List>();
		for(IDto msdto : alldata){
			MainAndSubDto dto = (MainAndSubDto) msdto;
			if(dto.getMainDto() instanceof TvDirectpaymsgmainDto){
				if(map.get(((TvDirectpaymsgmainDto)dto.getMainDto()).getSfundtypecode())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(((TvDirectpaymsgmainDto)dto.getMainDto()).getSfundtypecode(), data);
				}else{
					List<IDto> data = map.get(((TvDirectpaymsgmainDto)dto.getMainDto()).getSfundtypecode());
					data.add(dto);
				}
			}else if(dto.getMainDto() instanceof TvGrantpaymsgmainDto){
				if(map.get(((TvGrantpaymsgmainDto)dto.getMainDto()).getSfundtypecode())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(((TvGrantpaymsgmainDto)dto.getMainDto()).getSfundtypecode(), data);
				}else{
					List<IDto> data = map.get(((TvGrantpaymsgmainDto)dto.getMainDto()).getSfundtypecode());
					data.add(dto);
				}
			}else if(dto.getMainDtoList()!=null&&dto.getMainDtoList().size()>0)
			{
				TnConpaycheckbillDto tncondto = null;
				MainAndSubDto tnmain = new MainAndSubDto();
				List<IDto> tnmainlist = new ArrayList<IDto>();
				List<IDto> data = new ArrayList<IDto>();
				for(int i=0;i<dto.getMainDtoList().size();i++)
				{
					tncondto = (TnConpaycheckbillDto)dto.getMainDtoList().get(i);
					tnmainlist.add(tncondto);
				}
				tnmain.setMainDtoList(tnmainlist);
				data.add(tnmain);
				map.put("data", data);
			}
			
		}
		return map;
	}
	/**
	 * 将查询结果按代理银行额度种类分包
	 * @param alldata
	 * @return
	 */
	private Map<String, List>  getPaybankAmtTypeMap(List<IDto> alldata){
		Map<String, List> map = new HashMap<String, List>();
		TvDirectpaymsgmainDto ddto = null;
		TvGrantpaymsgmainDto gdto = null;
		TnConpaycheckbillDto cdto = null;
		for(IDto msdto : alldata){
			MainAndSubDto dto = (MainAndSubDto) msdto;
			if(dto.getMainDto() instanceof TvDirectpaymsgmainDto){
				ddto = (TvDirectpaymsgmainDto)dto.getMainDto();
				if(map.get(ddto.getSpaybankcode()+ddto.getSamttype())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(ddto.getSpaybankcode()+ddto.getSamttype(), data);
				}else{
					List<IDto> data = map.get(ddto.getSpaybankcode()+ddto.getSamttype());
					data.add(dto);
				}
			}else if(dto.getMainDto() instanceof TvGrantpaymsgmainDto){
				gdto = (TvGrantpaymsgmainDto)dto.getMainDto();
				if(map.get(gdto.getSpaybankcode()+gdto.getSamttype())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(gdto.getSpaybankcode()+gdto.getSamttype(), data);
				}else{
					List<IDto> data = map.get(gdto.getSpaybankcode()+gdto.getSamttype());
					data.add(dto);
				}
			}else if(dto.getMainDtoList()!=null&&dto.getMainDtoList().size()>0)
			{
				MainAndSubDto tnmain = null;
				List<IDto> data = null;
				List<IDto> mdata = null;
				for(int i=0;i<dto.getMainDtoList().size();i++)
				{
					cdto = (TnConpaycheckbillDto)dto.getMainDtoList().get(i);
					if(map.get(cdto.getSbnkno()+cdto.getCamtkind())==null)
					{
						tnmain = new MainAndSubDto();
						data = new ArrayList<IDto>();
						data.add(cdto);
						tnmain.setMainDtoList(data);
						mdata = new ArrayList<IDto>();
						mdata.add(tnmain);
						map.put(cdto.getSbnkno()+cdto.getCamtkind(),mdata);
					}else
					{
						mdata = map.get(cdto.getSbnkno()+cdto.getCamtkind());
						tnmain = (MainAndSubDto)mdata.get(0);
						tnmain.getMainDtoList().add(cdto);
					}
				}
			}
			
		}
		return map;
	}
}

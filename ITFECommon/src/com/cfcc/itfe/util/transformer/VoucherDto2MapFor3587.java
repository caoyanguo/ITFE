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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcilePayinfoSubDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 清算信息对帐3507转化
 * 
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3587 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3587.class);
											
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
			execDetail = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			List<IDto> alldata = getDataList(vDto,execDetail);
			if(vDto.getSorgcode().startsWith("13")){//福建对账
				Map<String, List>  map = getFundTypeMap(alldata);
				for(java.util.Map.Entry<String, List> entry : map.entrySet()){
					createVoucher(vDto, returnList, cDto, entry.getValue());
				}
			}else {
				createVoucher(vDto, returnList, cDto, alldata);
			}
			
		} catch (JAFDatabaseException e2) {		
			logger.error(e2);
			throw new ITFEBizException("查询信息异常！",e2);
		} catch (ValidateException e2) {
			logger.error(e2);
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
		List<List<MainAndSubDto>> dataList = getSplitPack(alldata,true);
		if(dataList!=null&&dataList.size()>0)
		{
			List<List> sendList = null;
			List mapList=null;
			for(int k=0;k<dataList.size();k++)
			{
				sendList = this.getSubLists(dataList.get(k), 500);
				if(sendList!=null&&sendList.size()>0)
				{
					List<IDto> tempList = null;
					String danhao=null;
					for(int i=0;i<sendList.size();i++)
					{
						mapList=new ArrayList();
						tempList = sendList.get(i);
						String FileName=null;
						String dirsep = File.separator; 
						String mainvou = "";
						TvVoucherinfoDto dto=new TvVoucherinfoDto();			
						dto.setSorgcode(vDto.getSorgcode());
						dto.setSadmdivcode(vDto.getSadmdivcode());
						dto.setSvtcode(vDto.getSvtcode());
						dto.setScreatdate(TimeFacade.getCurrentStringTime());
						dto.setStrecode(vDto.getStrecode());
						dto.setShold1(String.valueOf(sendList.size()));//包总数
						dto.setShold2(String.valueOf(i+1));//本包序号
						dto.setSstyear(dto.getScreatdate().substring(0, 4));				
						dto.setScheckdate(vDto.getScheckdate());
						dto.setShold3(vDto.getShold3());
						dto.setShold4(vDto.getShold4());
						dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
						dto.setSdemo("处理成功");
						dto.setSvoucherflag("1");
						dto.setSext1(getString(vDto.getSext1()));//发起方式1:人行发起,2:财政发起,3:商行发起
						dto.setSext2(getString(vDto.getSext2()));
						dto.setSext3(getString(vDto.getSext3()));
						dto.setSext4(getString(vDto.getSext4()));
						dto.setSext5(getString(vDto.getSext5()));
						if (ITFECommonConstant.SRC_NODE.equals("201053200014")){
				              mainvou = VoucherUtil.getCheckNo(dto,tempList,i);
						} else {
				              mainvou = VoucherUtil.getGrantSequence();
				        }
						vDto.setSdealno(mainvou);
						vDto.setSvoucherno(mainvou);
						if(danhao==null)
							danhao=mainvou;
						FileName = ITFECommonConstant.FILE_ROOT_PATH+ dirsep+ "Voucher"+ dirsep+ vDto.getScreatdate()+ dirsep+ "send"+ vDto.getSvtcode()+ "_"+mainvou+ ".msg";
						dto.setSfilename(FileName);
						mapList.add(vDto);
						mapList.add(cDto);
						mapList.add(tempList);
						List<IDto> idtoList = new ArrayList<IDto>();
						Map map=tranfor(mapList,sendList.size(),i+1,danhao,idtoList);
						dto.setSattach(danhao);//对帐单号
						dto.setNmoney(MtoCodeTrans.transformBigDecimal(((Map)map.get("Voucher")).get("AllAmt")));
						dto.setIcount(Integer.valueOf(String.valueOf(((Map)map.get("Voucher")).get("AllNum"))));
						//此处填写错误，不能填写账号，应该明细中获取对账机构（
						//dto.setSpaybankcode(String.valueOf(((Map)map.get("Voucher")).get("ClearAccNo")));
						 List <Map> list = (List) (((Map)((Map)map.get("Voucher")).get("DetailList"))).get("Detail");
						 String paybankno = (String) list.get(0).get("PayBankNo");
						if(((MainAndSubDto)tempList.get(0)).getMainDto() instanceof TfDirectpaymsgmainDto)
						{
							TfDirectpaymsgmainDto tempdto = (TfDirectpaymsgmainDto)((MainAndSubDto)tempList.get(0)).getMainDto();
							if("1".equals(tempdto.getSbusinesstypecode())||"3".equals(tempdto.getSbusinesstypecode()))
							{
								dto.setSpaybankcode("011");
							}else
							{
								dto.setSpaybankcode(paybankno);
							}
						}else
						{
							 dto.setSpaybankcode(paybankno);
						}
						dto.setSdealno(mainvou);
						dto.setSvoucherno(mainvou);	
						List vouList=new ArrayList();
						vouList.add(map);
						vouList.add(dto);
						vouList.add(idtoList);
						returnList.add(vouList);
					}
				}
			}
		}
	}
	private Map tranfor(List mapList,int count,int xuhao,String danhao,List<IDto> idtoList) throws ITFEBizException {
		try{
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) mapList.get(0);
			List<IDto> detailList=(List<IDto>) mapList.get(2);
			MainAndSubDto mdto = (MainAndSubDto)detailList.get(0);
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
			if(mdto.getMainDto() instanceof TvPayreckBankDto)
			{
				vouchermap.put("ClearAccNo",((TvPayreckBankDto)mdto.getMainDto()).getSpayeracct());//清算账号
				vouchermap.put("ClearAccNanme",((TvPayreckBankDto)mdto.getMainDto()).getSpayername());//清算账户名称
				vouchermap.put("ClearAccName",((TvPayreckBankDto)mdto.getMainDto()).getSpayername());//清算账户名称
				vouchermap.put("Hold1",((TvPayreckBankDto)mdto.getMainDto()).getSagentbnkcode());//预留字段1
			}else if(mdto.getMainDto() instanceof TvPayreckBankBackDto)
			{
				vouchermap.put("ClearAccNo",((TvPayreckBankBackDto)mdto.getMainDto()).getSpayeracct());//清算账号
				vouchermap.put("ClearAccNanme",((TvPayreckBankBackDto)mdto.getMainDto()).getSpayername());//清算账户名称
				vouchermap.put("ClearAccName",((TvPayreckBankBackDto)mdto.getMainDto()).getSpayername());//清算账户名称
				vouchermap.put("Hold1",((TvPayreckBankBackDto)mdto.getMainDto()).getSagentbnkcode());//预留字段1
			}else if(mdto.getMainDto() instanceof TfDirectpaymsgmainDto)
			{
				vouchermap.put("ClearAccNo",((TfDirectpaymsgmainDto)mdto.getMainDto()).getSclearbankcode());//清算账号
				vouchermap.put("ClearAccNanme",((TfDirectpaymsgmainDto)mdto.getMainDto()).getSclearbankcode());//清算账户名称
				vouchermap.put("ClearAccName",((TfDirectpaymsgmainDto)mdto.getMainDto()).getSclearbankcode());//清算账户名称
				vouchermap.put("Hold1",((TfDirectpaymsgmainDto)mdto.getMainDto()).getSpaybankcode());//预留字段1
			}else if(mdto.getMainDto() instanceof TfPaybankRefundmainDto)
			{
				vouchermap.put("ClearAccNo",((TfPaybankRefundmainDto)mdto.getMainDto()).getSpaysndbnkno());//清算账号
				vouchermap.put("ClearAccNanme","");//清算账户名称-2252规范无此字段
				vouchermap.put("ClearAccName","");//清算账户名称-2252规范无此字段
				vouchermap.put("Hold1",((TvPayreckBankDto)mdto.getMainDto()).getSagentbnkcode());//预留字段1
			}

			TsTreasuryDto dto = SrvCacheFacade.cacheTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode());
			if(vDto.getSorgcode().startsWith("13")){//福建对账
				vouchermap.put("ClearBankCode","001001");//预留字段1
				vouchermap.put("ClearBankName",dto.getStrename());//预留字段2
			}else{
				vouchermap.put("ClearBankCode",dto.getStrecode());//人民银行编码
				vouchermap.put("ClearBankName",dto.getStrename());//人民银行名称
			}
			vouchermap.put("BeginDate",vDto.getShold3());//对账起始日期
			vouchermap.put("EndDate",vDto.getShold4());//对账终止日期
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
			{
				vouchermap.put("BgtTypeCode",vDto.getSext3());//预算类型
				vouchermap.put("BgtTypeName","1".equals(vDto.getSext3())?"预算内":"预算外");//预算类型名称
			}
			if(vDto.getStrecode().startsWith("13")){//福州预留字段添加资金性质
				if(mdto.getMainDto() instanceof TvPayreckBankDto){
					vouchermap.put("Hold1",((TvPayreckBankDto)mdto.getMainDto()).getSfundtypecode());//预留字段1
					vouchermap.put("Hold2",((TvPayreckBankDto)mdto.getMainDto()).getSfundtypename());//预留字段2
				}else if(mdto.getMainDto() instanceof TvPayreckBankBackDto){
					vouchermap.put("Hold1",((TvPayreckBankBackDto)mdto.getMainDto()).getSfundtypecode());//预留字段1
					vouchermap.put("Hold2",((TvPayreckBankBackDto)mdto.getMainDto()).getSfundtypename());//预留字段2
				}
			}else{
				vouchermap.put("Hold2",getString(vDto.getSverifyusercode()));//预留字段2
			}
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			for(IDto idto:detailList)
			{
				mdto = (MainAndSubDto)idto;
				if(mdto.getMainDto() instanceof TvPayreckBankDto)
				{
					TvPayreckBankDto ddto = (TvPayreckBankDto) mdto.getMainDto();
					if(mdto!=null&&mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0)
					{
						for(int i=0;i<mdto.getSubDtoList().size();i++)
						{
							id++;
							TvPayreckBankListDto dsdto = (TvPayreckBankListDto)mdto.getSubDtoList().get(i);
							HashMap<String, Object> Detailmap = new HashMap<String, Object>();
							Detailmap.put("Id",vDto.getSvoucherno()+id);//序号
							Detailmap.put("PayAgentBillNo",ddto.getSvouno());//清算单号
							Detailmap.put("PayDetailId",dsdto.getSid()==null?dsdto.getSvouchern0():dsdto.getSid());//清算明细ID
							Detailmap.put("VoucherNo",getString(dsdto.getSvouchern0()));//支付凭证单号
							Detailmap.put("SupDepCode",getString(dsdto.getSbdgorgcode()));//预算单位编码
							Detailmap.put("SupDepName",getString(dsdto.getSsupdepname()));//一级预算单位名称
							Detailmap.put("FundTypeCode",getString(ddto.getSfundtypecode()));//资金性质编码
							Detailmap.put("FundTypeName",getString(ddto.getSfundtypename()));//资金性质名称
							Detailmap.put("PayBankCode",getString(ddto.getSagentbnkcode()));//代理银行编码
							Detailmap.put("PayBankName",getString(ddto.getSpaybankname()));//代理银行名称
							Detailmap.put("PayBankNo",getString(ddto.getSagentbnkcode()));//代理银行行号
							Detailmap.put("ExpFuncCode",getString(dsdto.getSfuncbdgsbtcode()));//支出功能分类科目编码
							Detailmap.put("ExpFuncName",getString(dsdto.getSexpfuncname()));//支出功能分类科目名称
							Detailmap.put("ProCatCode",getString(""));//收支管理编码
							Detailmap.put("ProCatName",getString(""));//收支管理名称
							if(vDto.getSorgcode().startsWith("02")&&MsgConstant.grantPay.equals(ddto.getSpaytypecode()))//天津的支付方式为001002和001001
								Detailmap.put("PayTypeCode",getString("001002"));//支付方式编码;
							else if(vDto.getSorgcode().startsWith("02")&&MsgConstant.directPay.equals(ddto.getSpaytypecode()))//天津的支付方式为001002和001001
								Detailmap.put("PayTypeCode",getString("001001"));//支付方式编码;
							else
								Detailmap.put("PayTypeCode",getString(ddto.getSpaytypecode()));//支付方式编码
							Detailmap.put("PayTypeName",getString(ddto.getSpaytypename()));//支付方式名称
							Detailmap.put("PayAmt",String.valueOf(dsdto.getFamt()));//支付金额
							if(vDto.getStrecode().startsWith("13")){
								Detailmap.put("Hold1",getString(dsdto.getSvouchern0()));//预留字段1
							}else{
								Detailmap.put("Hold1",getString(""));//预留字段1
							}
							Detailmap.put("Hold2",getString(""));//预留字段2
							Detailmap.put("Hold3",getString(""));//预留字段3
							Detailmap.put("Hold4",getString(""));//预留字段4
							if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
								vDto.setSpaybankcode(ddto.getSagentbnkcode());
							allamt=allamt.add(dsdto.getFamt());
							Detail.add(Detailmap);
							subdtolist.add(getSubDto(Detailmap,vouchermap));
						}
					}
				}else if(mdto.getMainDto() instanceof TvPayreckBankBackDto)
				{
					TvPayreckBankBackDto gdto = (TvPayreckBankBackDto)mdto.getMainDto();
					if(mdto!=null&&mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0)
					{
						for(int i=0;i<mdto.getSubDtoList().size();i++)
						{
							id++;
							TvPayreckBankBackListDto gsdto = (TvPayreckBankBackListDto)mdto.getSubDtoList().get(i);
							HashMap<String, Object> Detailmap = new HashMap<String, Object>();
							Detailmap.put("Id",vDto.getSvoucherno()+id);//序号
							Detailmap.put("PayAgentBillNo",gdto.getSvouno());//清算单号
							Detailmap.put("PayDetailId",gsdto.getSid()==null?gsdto.getSvoucherno():gsdto.getSid());//清算明细ID
							Detailmap.put("VoucherNo",getString(gsdto.getSvoucherno()));//支付凭证单号
							Detailmap.put("SupDepCode",getString(gsdto.getSbdgorgcode()));//预算单位编码
							Detailmap.put("SupDepName",getString(gsdto.getSsupdepname()));//一级预算单位名称
							Detailmap.put("FundTypeCode",getString(gdto.getSfundtypecode()));//资金性质编码
							Detailmap.put("FundTypeName",getString(gdto.getSfundtypename()));//资金性质名称
							Detailmap.put("PayBankCode",getString(gdto.getSagentbnkcode()));//代理银行编码
							Detailmap.put("PayBankName",getString(gdto.getSpaybankname()));//代理银行名称
							Detailmap.put("PayBankNo",getString(gdto.getSagentbnkcode()));//代理银行行号
							Detailmap.put("ExpFuncCode",getString(gsdto.getSfuncbdgsbtcode()));//支出功能分类科目编码
							Detailmap.put("ExpFuncName",getString(gsdto.getSexpfuncname()));//支出功能分类科目名称
							Detailmap.put("ProCatCode",getString(""));//收支管理编码
							Detailmap.put("ProCatName",getString(""));//收支管理名称
							if("0".equals(gdto.getSpaytypecode()))
								Detailmap.put("PayTypeCode",getString("11"));//支付方式编码
							else if("1".equals(gdto.getSpaytypecode()))
								Detailmap.put("PayTypeCode",getString("12"));//支付方式编码
							else
								Detailmap.put("PayTypeCode",getString(gdto.getSpaytypecode()));//支付方式编码
							if(vDto.getSorgcode().startsWith("02")&&MsgConstant.grantPay.equals(gdto.getSpaytypecode()))//天津的支付方式为001002和001001
								Detailmap.put("PayTypeCode",getString("001002"));//支付方式编码;
							else if(vDto.getSorgcode().startsWith("02")&&MsgConstant.directPay.equals(gdto.getSpaytypecode()))//天津的支付方式为001002和001001
								Detailmap.put("PayTypeCode",getString("001001"));//支付方式编码;
							Detailmap.put("PayTypeName",getString(gdto.getSpaytypename()));//支付方式名称
							if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
								Detailmap.put("PayAmt",String.valueOf(gsdto.getFamt()));//支付金额
							}else{
								Detailmap.put("PayAmt","-"+String.valueOf(gsdto.getFamt()));//支付金额
							}
							if(vDto.getStrecode().startsWith("13")){
								Detailmap.put("Hold1",getString(gsdto.getSvoucherno()));//预留字段1
							}else{
								Detailmap.put("Hold1",getString(""));//预留字段1
							}
							Detailmap.put("Hold2",getString(""));//预留字段2
							Detailmap.put("Hold3",getString(""));//预留字段3
							Detailmap.put("Hold4",getString(""));//预留字段4
							if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
								vDto.setSpaybankcode(gdto.getSagentbnkcode());
							allamt=allamt.subtract(gsdto.getFamt().abs());
							Detail.add(Detailmap);
							subdtolist.add(getSubDto(Detailmap,vouchermap));
						}
					}
				}else if(mdto.getMainDto() instanceof TfDirectpaymsgmainDto)
				{
					TfDirectpaymsgmainDto gdto = (TfDirectpaymsgmainDto)mdto.getMainDto();
					if(mdto!=null&&mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0)
					{
						for(int i=0;i<mdto.getSubDtoList().size();i++)
						{
							id++;
							TfDirectpaymsgsubDto gsdto = (TfDirectpaymsgsubDto)mdto.getSubDtoList().get(i);
							HashMap<String, Object> Detailmap = new HashMap<String, Object>();
							Detailmap.put("Id",vDto.getSvoucherno()+id);//序号
							Detailmap.put("PayAgentBillNo",gdto.getSvoucherno());//清算单号
							Detailmap.put("PayDetailId",gsdto.getSid()==null?gsdto.getSvoucherno():gsdto.getSid());//清算明细ID
							Detailmap.put("VoucherNo",getString(gsdto.getSvoucherno()));//支付凭证单号
							Detailmap.put("SupDepCode","".equals(getString(gsdto.getSsupdepcode()))?gsdto.getSagencycode():gsdto.getSsupdepcode());//预算单位编码
							Detailmap.put("SupDepName","".equals(getString(gsdto.getSsupdepname()))?gsdto.getSagencyname():gsdto.getSsupdepname());//一级预算单位名称
							Detailmap.put("FundTypeCode",getString(gdto.getSfundtypecode()));//资金性质编码
							Detailmap.put("FundTypeName",getString(gdto.getSfundtypename()));//资金性质名称
							Detailmap.put("PayBankCode",getString(gdto.getSpaybankcode()));//代理银行编码
							Detailmap.put("PayBankName",getString(gdto.getSpaybankname()));//代理银行名称
							Detailmap.put("PayBankNo",getString(gdto.getSpayeeacctbankno()));//代理银行行号
							Detailmap.put("ExpFuncCode",getString(gsdto.getSexpfunccode()));//支出功能分类科目编码
							Detailmap.put("ExpFuncName",getString(gsdto.getSexpfuncname()));//支出功能分类科目名称
							Detailmap.put("ProCatCode",getString(""));//收支管理编码
							Detailmap.put("ProCatName",getString(""));//收支管理名称
							if("0".equals(gdto.getSpaytypecode()))
								Detailmap.put("PayTypeCode",getString("11"));//支付方式编码
							else if("1".equals(gdto.getSpaytypecode()))
								Detailmap.put("PayTypeCode",getString("12"));//支付方式编码
							else
								Detailmap.put("PayTypeCode",getString(gdto.getSpaytypecode()));//支付方式编码
							if(vDto.getSorgcode().startsWith("02")&&MsgConstant.grantPay.equals(gdto.getSpaytypecode()))//天津的支付方式为001002和001001
								Detailmap.put("PayTypeCode",getString("001002"));//支付方式编码;
							else if(vDto.getSorgcode().startsWith("02")&&MsgConstant.directPay.equals(gdto.getSpaytypecode()))//天津的支付方式为001002和001001
								Detailmap.put("PayTypeCode",getString("001001"));//支付方式编码;
							Detailmap.put("PayTypeName",getString(gdto.getSpaytypename()));//支付方式名称
							Detailmap.put("PayAmt",String.valueOf(gsdto.getNpayamt()));//支付金额
							Detailmap.put("Hold1",getString(""));//预留字段1
							Detailmap.put("Hold2",getString(""));//预留字段2
							Detailmap.put("Hold3",getString(""));//预留字段3
							Detailmap.put("Hold4",getString(""));//预留字段4
							if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
								vDto.setSpaybankcode(gdto.getSpayeeacctbankno());
							allamt=allamt.add(gsdto.getNpayamt());
							Detail.add(Detailmap);
							subdtolist.add(getSubDto(Detailmap,vouchermap));
						}
					}
				}else if(mdto.getMainDto() instanceof TfPaybankRefundmainDto)
				{
					if(mdto.getExtdto()!=null&&mdto.getExtdto().getSubDtoList()!=null)
					{
						TfDirectpaymsgmainDto gdto = (TfDirectpaymsgmainDto)mdto.getExtdto().getMainDto();
						for(int i=0;i<mdto.getSubDtoList().size();i++)
						{
							id++;
							TfDirectpaymsgsubDto gsdto = (TfDirectpaymsgsubDto)mdto.getSubDtoList().get(i);
							HashMap<String, Object> Detailmap = new HashMap<String, Object>();
							Detailmap.put("Id",vDto.getSvoucherno()+id);//序号
							Detailmap.put("PayAgentBillNo",gdto.getSvoucherno());//清算单号
							Detailmap.put("PayDetailId",gsdto.getSid()==null?gsdto.getSvoucherno():gsdto.getSid());//清算明细ID
							Detailmap.put("VoucherNo",getString(gsdto.getSvoucherno()));//支付凭证单号
							Detailmap.put("SupDepCode",getString(gsdto.getSsupdepcode()==null?gsdto.getSagencycode():gsdto.getSsupdepcode()));//预算单位编码
							Detailmap.put("SupDepName",getString(gsdto.getSsupdepname())==null?gsdto.getSagencyname():gsdto.getSsupdepname());//一级预算单位名称
							Detailmap.put("FundTypeCode",getString(gdto.getSfundtypecode()));//资金性质编码
							Detailmap.put("FundTypeName",getString(gdto.getSfundtypename()));//资金性质名称
							Detailmap.put("PayBankCode",getString(gdto.getSpaybankcode()));//代理银行编码
							Detailmap.put("PayBankName",getString(gdto.getSpaybankname()));//代理银行名称
							Detailmap.put("PayBankNo",getString(gdto.getSclearbankcode()));//代理银行行号
							Detailmap.put("ExpFuncCode",getString(gsdto.getSexpfunccode()));//支出功能分类科目编码
							Detailmap.put("ExpFuncName",getString(gsdto.getSexpfuncname()));//支出功能分类科目名称
							Detailmap.put("ProCatCode",getString(""));//收支管理编码
							Detailmap.put("ProCatName",getString(""));//收支管理名称
							if("0".equals(gdto.getSpaytypecode()))
								Detailmap.put("PayTypeCode",getString("11"));//支付方式编码
							else if("1".equals(gdto.getSpaytypecode()))
								Detailmap.put("PayTypeCode",getString("12"));//支付方式编码
							else
								Detailmap.put("PayTypeCode",getString(gdto.getSpaytypecode()));//支付方式编码
							if(vDto.getSorgcode().startsWith("02")&&MsgConstant.grantPay.equals(gdto.getSpaytypecode()))//天津的支付方式为001002和001001
								Detailmap.put("PayTypeCode",getString("001002"));//支付方式编码;
							else if(vDto.getSorgcode().startsWith("02")&&MsgConstant.directPay.equals(gdto.getSpaytypecode()))//天津的支付方式为001002和001001
								Detailmap.put("PayTypeCode",getString("001001"));//支付方式编码;
							Detailmap.put("PayTypeName",getString(gdto.getSpaytypename()));//支付方式名称
							Detailmap.put("PayAmt",String.valueOf(gsdto.getNpayamt()));//支付金额
//							Detailmap.put("PayAmt","-"+String.valueOf(gsdto.getNpayamt()));//支付金额
							Detailmap.put("Hold1",getString(""));//预留字段1
							Detailmap.put("Hold2",getString(""));//预留字段2
							Detailmap.put("Hold3",getString(""));//预留字段3
							Detailmap.put("Hold4",getString(""));//预留字段4
							if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
								vDto.setSpaybankcode(gdto.getSpayeeacctbankno());
							allamt=allamt.subtract(gsdto.getNpayamt());
							Detail.add(Detailmap);
							subdtolist.add(getSubDto(Detailmap,vouchermap));
						}
					}else
					{
						TfPaybankRefundmainDto gdto = (TfPaybankRefundmainDto)mdto.getMainDto();
						if(mdto!=null&&mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0)
						{
							for(int i=0;i<mdto.getSubDtoList().size();i++)
							{
								id++;
								TfPaybankRefundsubDto gsdto = (TfPaybankRefundsubDto)mdto.getSubDtoList().get(i);
								HashMap<String, Object> Detailmap = new HashMap<String, Object>();
								Detailmap.put("Id",vDto.getSvoucherno()+id);//序号
								Detailmap.put("PayAgentBillNo",gdto.getSvoucherno());//清算单号
								Detailmap.put("PayDetailId",gsdto.getSid());//清算明细ID
								Detailmap.put("VoucherNo",getString(""));//支付凭证单号
								Detailmap.put("SupDepCode",getString(""));//预算单位编码
								Detailmap.put("SupDepName",getString(""));//一级预算单位名称
								Detailmap.put("FundTypeCode",getString(""));//资金性质编码
								Detailmap.put("FundTypeName",getString(""));//资金性质名称
								Detailmap.put("PayBankCode",getString(""));//代理银行编码
								Detailmap.put("PayBankName",getString(""));//代理银行名称
								Detailmap.put("PayBankNo",getString(gdto.getSpaysndbnkno()));//代理银行行号
								Detailmap.put("ExpFuncCode",getString(""));//支出功能分类科目编码
								Detailmap.put("ExpFuncName",getString(""));//支出功能分类科目名称
								Detailmap.put("ProCatCode",getString(""));//收支管理编码
								Detailmap.put("ProCatName",getString(""));//收支管理名称
								if("0".equals(gdto.getSpaytypecode()))
									Detailmap.put("PayTypeCode",getString("11"));//支付方式编码
								else if("1".equals(gdto.getSpaytypecode()))
									Detailmap.put("PayTypeCode",getString("12"));//支付方式编码
								else
									Detailmap.put("PayTypeCode",getString(gdto.getSpaytypecode()));//支付方式编码
								if(vDto.getSorgcode().startsWith("02")&&MsgConstant.grantPay.equals(gdto.getSpaytypecode()))//天津的支付方式为001002和001001
									Detailmap.put("PayTypeCode",getString("001002"));//支付方式编码;
								else if(vDto.getSorgcode().startsWith("02")&&MsgConstant.directPay.equals(gdto.getSpaytypecode()))//天津的支付方式为001002和001001
									Detailmap.put("PayTypeCode",getString("001001"));//支付方式编码;
								Detailmap.put("PayTypeName",getString(gdto.getSpaytypename()));//支付方式名称
								Detailmap.put("PayAmt",String.valueOf(gsdto.getNpayamt()));//支付金额
//								Detailmap.put("PayAmt","-"+String.valueOf(gsdto.getNpayamt()));//支付金额
								Detailmap.put("Hold1",getString(""));//预留字段1
								Detailmap.put("Hold2",getString(""));//预留字段2
								Detailmap.put("Hold3",getString(""));//预留字段3
								Detailmap.put("Hold4",getString(""));//预留字段4
								if(vDto.getSpaybankcode()==null||"".equals(vDto.getSpaybankcode()))
									vDto.setSpaybankcode(gdto.getSpaysndbnkno());
								allamt=allamt.subtract(gsdto.getNpayamt().abs());
								Detail.add(Detailmap);
								subdtolist.add(getSubDto(Detailmap,vouchermap));
							}
						}
					}
				}
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
			logger.error(e);
			throw new ITFEBizException(e.getMessage(),e);	
		}
	}
	public Map voucherTranfor(TvVoucherinfoDto dto) throws ITFEBizException {		
		return null;
	}
	private TfReconcilePayinfoMainDto getMainDto(Map<String, Object> mainMap,TvVoucherinfoDto vDto)
	{
		TfReconcilePayinfoMainDto mainDto = new TfReconcilePayinfoMainDto();
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
		mainDto.setSclearaccno(getString(mainMap,"ClearAccNo"));//清算账号
		mainDto.setSclearaccnanme(getString(mainMap,"ClearAccNanme"));//清算账户名称
		mainDto.setSclearbankcode(getString(mainMap,"ClearBankCode"));//人民银行编码
		mainDto.setSclearbankname(getString(mainMap,"ClearBankName"));//人民银行名称
		mainDto.setSbegindate(getString(mainMap,"BeginDate"));//BeginDate",vDto.getScheckdate());//对账起始日期
		mainDto.setSenddate(getString(mainMap,"EndDate"));//EndDate",vDto.getSpaybankcode());//对账终止日期
		mainDto.setSallnum(getString(mainMap,"AllNum"));//AllNum",detailList.size());//总笔数
		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,"AllAmt")));//AllAmt","");//总金额
		mainDto.setShold1(getString(mainMap,"Hold1"));//Hold1","");//预留字段1
		mainDto.setShold2(getString(mainMap,"Hold2"));//Hold2","");//预留字段2
		mainDto.setSext1("1");//发起方式1:人行发起,2:财政发起,3:商行发起
		return mainDto;
	}
	private TfReconcilePayinfoSubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
	{
		TfReconcilePayinfoSubDto subDto = new TfReconcilePayinfoSubDto();
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		subDto.setIvousrlno(Long.valueOf(voucherno));
		String id = getString(subMap,"Id");
		if(id.length()>19)
			id = id.substring(id.length()-19);
		subDto.setIseqno(Long.valueOf(id));//Id",vDto.getSdealno()+(++id));//序号
		subDto.setSid(getString(subMap,"Id"));
		subDto.setSsupdepcode(getString(subMap,"SupDepCode"));//预算单位编码
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
		subDto.setNpayamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"PayAmt")));//支付金额
		subDto.setSxcheckresult("0");//对账结果默认成功
		subDto.setShold1(getString(subMap,"Hold1"));//预留字段1
		subDto.setShold2(getString(subMap,"Hold2"));//预留字段2
		subDto.setShold3(getString(subMap,"Hold3"));//预留字段3
		subDto.setShold4(getString(subMap,"Hold4"));//预留字段4
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
				execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
			//2301查询商行划款已经回单的数据
			sql = new StringBuffer("SELECT * FROM HTV_PAYRECK_BANK WHERE I_VOUSRLNO in(");
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ? AND S_ORGCODE= ? ");
			sql.append(vDto.getSpaybankcode()==null?"":" AND S_PAYBANKCODE=? ");
			sql.append(vDto.getSext1()==null?"":" AND S_EXT1=? ");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				sql.append(" AND S_EXT4=? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?)");//S_CONFIRUSERCODE
			execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
			execDetail.addParam(vDto.getSorgcode());
			if(vDto.getSpaybankcode()!=null)
				execDetail.addParam(vDto.getSpaybankcode());
			if(vDto.getSext1()!=null)
				execDetail.addParam(vDto.getSext1());
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			execDetail.setMaxRows(500000);
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayreckBankDto.class).getDtoCollection();//历史表主表
			List<IDto> mainsublist = null;
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_PAYRECK_BANK_LIST where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";//商行划款子表数据
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getSext1()!=null)
					execDetail.addParam(vDto.getSext1());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayreckBankListDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayreckBankListDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayreckBankListDto)tempdto;
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
				TvPayreckBankDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayreckBankDto)detailList.get(i);
					mainsublist = subMap.get(String.valueOf(tempdto.getIvousrlno()));
					if(mainsublist!=null&&mainsublist.size()>0)
					{
						for(int j=0;j<mainsublist.size();j++)
						{
							datadto = new MainAndSubDto();
							datadto.setMainDto(tempdto);
							subList = new ArrayList<IDto>();
							subList.add(mainsublist.get(j));
							datadto.setSubDtoList(subList);//历史表子表
							getList.add(datadto);
						}
					}
				}
			}
			execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
			execDetail.addParam(vDto.getSorgcode());
			if(vDto.getSpaybankcode()!=null)
				execDetail.addParam(vDto.getSpaybankcode());
			if(vDto.getSext1()!=null)
				execDetail.addParam(vDto.getSext1());
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			execDetail.setMaxRows(500000);
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYRECK_BANK","TV_PAYRECK_BANK"),TvPayreckBankDto.class).getDtoCollection();//正式表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYRECK_BANK_LIST where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYRECK_BANK","TV_PAYRECK_BANK"),"*", "I_VOUSRLNO")+")";//商行划款子表数据
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_2301);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getSext1()!=null)
					execDetail.addParam(vDto.getSext1());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayreckBankListDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayreckBankListDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayreckBankListDto)tempdto;
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
				TvPayreckBankDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayreckBankDto)detailList.get(i);
					mainsublist = subMap.get(String.valueOf(tempdto.getIvousrlno()));
					if(mainsublist!=null&&mainsublist.size()>0)
					{
						for(int j=0;j<mainsublist.size();j++)
						{
							datadto = new MainAndSubDto();
							datadto.setMainDto(tempdto);
							subList = new ArrayList<IDto>();
							subList.add(mainsublist.get(j));
							datadto.setSubDtoList(subList);//历史表子表
							getList.add(datadto);
						}
					}
				}
			}
			//2302查询商行退款已经回单的数据
			sql = new StringBuffer("SELECT * FROM HTV_PAYRECK_BANK_BACK WHERE I_VOUSRLNO in(");//查询商行划款已经回单的数据
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE (S_VTCODE= ? ");
			/*
			 * 如果为上海模式，需要把直接支付退款内容加上去
			 */
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0){
				sql.append(" OR S_VTCODE= ? ");
			}
			sql.append(") AND S_ORGCODE= ? ");
			sql.append(vDto.getSpaybankcode()==null?"":" AND S_PAYBANKCODE=? ");
			sql.append(vDto.getSext1()==null?"":" AND S_EXT1=? ");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				sql.append(" AND S_EXT4=? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?)");
			execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
			if(ITFECommonConstant.PUBLICPARAM.contains(",sh,"))
				execDetail.addParam(MsgConstant.VOUCHER_NO_2203);
			execDetail.addParam(vDto.getSorgcode());
			if(vDto.getSpaybankcode()!=null)
				execDetail.addParam(vDto.getSpaybankcode());
			if(vDto.getSext1()!=null)
				execDetail.addParam(vDto.getSext1());
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			execDetail.setMaxRows(500000);
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayreckBankBackDto.class).getDtoCollection();//历史表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_PAYRECK_BANK_BACK_LIST where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
				if(ITFECommonConstant.PUBLICPARAM.contains(",sh,"))
					execDetail.addParam(MsgConstant.VOUCHER_NO_2203);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getSext1()!=null)
					execDetail.addParam(vDto.getSext1());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayreckBankBackListDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayreckBankListDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayreckBankListDto)tempdto;
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
				TvPayreckBankBackDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayreckBankBackDto)detailList.get(i);
					mainsublist = subMap.get(String.valueOf(tempdto.getIvousrlno()));
					if(mainsublist!=null&&mainsublist.size()>0)
					{
						for(int j=0;j<mainsublist.size();j++)
						{
							datadto = new MainAndSubDto();
							datadto.setMainDto(tempdto);
							subList = new ArrayList<IDto>();
							subList.add(mainsublist.get(j));
							datadto.setSubDtoList(subList);//子表
							getList.add(datadto);
						}
					}
				}
			}
			execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
			if(ITFECommonConstant.PUBLICPARAM.contains(",sh,"))
				execDetail.addParam(MsgConstant.VOUCHER_NO_2203);
			execDetail.addParam(vDto.getSorgcode());
			if(vDto.getSpaybankcode()!=null)
				execDetail.addParam(vDto.getSpaybankcode());
			if(vDto.getSext1()!=null)
				execDetail.addParam(vDto.getSext1());
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			execDetail.setMaxRows(500000);
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTV_PAYRECK_BANK_BACK","TV_PAYRECK_BANK_BACK"),TvPayreckBankBackDto.class).getDtoCollection();//正式表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				execDetail.addParam(MsgConstant.VOUCHER_NO_2302);
				if(ITFECommonConstant.PUBLICPARAM.contains(",sh,"))
					execDetail.addParam(MsgConstant.VOUCHER_NO_2203);
				execDetail.addParam(vDto.getSorgcode());
				if(vDto.getSpaybankcode()!=null)
					execDetail.addParam(vDto.getSpaybankcode());
				if(vDto.getSext1()!=null)
					execDetail.addParam(vDto.getSext1());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				String subsql = "select * from TV_PAYRECK_BANK_BACK_LIST where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTV_PAYRECK_BANK_BACK","TV_PAYRECK_BANK_BACK"), "*", "I_VOUSRLNO")+")";
				List<IDto> subList = null;
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayreckBankBackListDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayreckBankBackListDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayreckBankBackListDto)tempdto;
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
				TvPayreckBankBackDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayreckBankBackDto)detailList.get(i);
					mainsublist = subMap.get(String.valueOf(tempdto.getIvousrlno()));
					if(mainsublist!=null&&mainsublist.size()>0)
					{
						for(int j=0;j<mainsublist.size();j++)
						{
							datadto = new MainAndSubDto();
							datadto.setMainDto(tempdto);
							subList = new ArrayList<IDto>();
							subList.add(mainsublist.get(j));
							datadto.setSubDtoList(subList);//子表
							getList.add(datadto);
						}
					}
				}
			}
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")>=0&&(vDto.getSext1()==null||MsgConstant.directPay.equals(vDto.getSext1())))
			{
				//财政直接支付凭证5201主表
				sql = new StringBuffer("SELECT * FROM HTF_DIRECTPAYMSGMAIN WHERE I_VOUSRLNO in(");//查询商行划款已经回单的数据
				sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_5201+"' AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
				sql.append(vDto.getSpaybankcode()==null?"":" AND S_PAYBANKCODE='"+vDto.getSpaybankcode()+"' ");
				sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
				sql.append("AND S_CONFIRUSERCODE BETWEEN '"+vDto.getShold3()+"' AND '"+vDto.getShold4()+"')");
				detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TfDirectpaymsgmainDto.class).getDtoCollection();//历史表主表
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from HTF_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";
					List<IDto> subList = null;
					subList = (List<IDto>)execDetail.runQuery(subsql, TfDirectpaymsgsubDto.class).getDtoCollection();
					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
					if(subList!=null&&subList.size()>0)
					{
						List<IDto> tempList = null;
						TfDirectpaymsgsubDto subdto = null;
						for(IDto tempdto:subList)
						{
							subdto = (TfDirectpaymsgsubDto)tempdto;
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
					TfDirectpaymsgmainDto tempdto = null;
					for(int i=0;i<detailList.size();i++)
					{
						tempdto = (TfDirectpaymsgmainDto)detailList.get(i);
						mainsublist = subMap.get(String.valueOf(tempdto.getIvousrlno()));
						if(mainsublist!=null&&mainsublist.size()>0)
						{
							for(int j=0;j<mainsublist.size();j++)
							{
								datadto = new MainAndSubDto();
								datadto.setMainDto(tempdto);
								subList = new ArrayList<IDto>();
								subList.add(mainsublist.get(j));
								datadto.setSubDtoList(subList);//子表
								getList.add(datadto);
							}
						}
					}
				}
				detailList=  (List<IDto>) execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTF_DIRECTPAYMSGMAIN","TF_DIRECTPAYMSGMAIN"),TfDirectpaymsgmainDto.class).getDtoCollection();//主表
				if(detailList!=null&&detailList.size()>0)
				{
					if(getList==null)
						getList = new ArrayList<IDto>();
					String subsql = "select * from TF_DIRECTPAYMSGSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTF_DIRECTPAYMSGMAIN","TF_DIRECTPAYMSGMAIN"), "*", "I_VOUSRLNO")+")";
					List<IDto> subList = null;
					subList = (List<IDto>)execDetail.runQuery(subsql, TfDirectpaymsgsubDto.class).getDtoCollection();
					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
					if(subList!=null&&subList.size()>0)
					{
						List<IDto> tempList = null;
						TfDirectpaymsgsubDto subdto = null;
						for(IDto tempdto:subList)
						{
							subdto = (TfDirectpaymsgsubDto)tempdto;
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
					TfDirectpaymsgmainDto tempdto = null;
					for(int i=0;i<detailList.size();i++)
					{
						tempdto = (TfDirectpaymsgmainDto)detailList.get(i);
						mainsublist = subMap.get(String.valueOf(tempdto.getIvousrlno()));
						if(mainsublist!=null&&mainsublist.size()>0)
						{
							for(int j=0;j<mainsublist.size();j++)
							{
								datadto = new MainAndSubDto();
								datadto.setMainDto(tempdto);
								subList = new ArrayList<IDto>();
								subList.add(mainsublist.get(j));
								datadto.setSubDtoList(subList);//子表
								getList.add(datadto);
							}
						}
					}
				}
				/**
				 * 上海直接支付退款2252提交时，会把2252表内容复制到该TV_PAYRECK_BANK_BACK表，所以退款信息需要在该TV_PAYRECK_BANK_BACK表信息去数据，无需再TF_PAYBANK_REFUNDMAIN去数据
				 */
//				sql = new StringBuffer("SELECT * FROM HTF_PAYBANK_REFUNDMAIN WHERE I_VOUSRLNO in(");//收款银行退款通知2252主表历史表
//				sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= '"+MsgConstant.VOUCHER_NO_2252+"' AND S_ORGCODE= '"+vDto.getSorgcode()+"' ");
//				sql.append("AND S_TRECODE= '"+vDto.getStrecode()+"'  AND S_STATUS='"+DealCodeConstants.VOUCHER_SUCCESS+"' ");
//				sql.append("AND S_CREATDATE BETWEEN '"+vDto.getShold3()+"' AND '"+vDto.getShold4()+"')");
//				detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TfPaybankRefundmainDto.class).getDtoCollection();//历史表主表
//				if(detailList!=null&&detailList.size()>0)
//				{
//					if(getList==null)
//						getList = new ArrayList<IDto>();
//					String subsql = "select * from HTF_PAYBANK_REFUNDSUB where I_VOUSRLNO in("+StringUtil.replace(sql.toString(), "*", "I_VOUSRLNO")+")";
//					List<IDto> subList = null;
//					subList = (List<IDto>)execDetail.runQuery(subsql, TfPaybankRefundsubDto.class).getDtoCollection();
//					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
//					if(subList!=null&&subList.size()>0)
//					{
//						List<IDto> tempList = null;
//						TfPaybankRefundsubDto subdto = null;
//						for(IDto tempdto:subList)
//						{
//							subdto = (TfPaybankRefundsubDto)tempdto;
//							tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
//							if(tempList==null)
//							{
//								tempList = new ArrayList<IDto>();
//								tempList.add(subdto);
//								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
//							}else
//							{
//								tempList.add(subdto);
//								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
//							}
//						}
//					}
//					Map<String,MainAndSubDto> map5201 = get5201Map(StringUtil.replace(sql.toString(), "HTF_PAYBANK_REFUNDMAIN", "HTF_DIRECTPAYMSGMAIN"),StringUtil.replace(subsql,"HTF_PAYBANK_REFUNDSUB","HTF_DIRECTPAYMSGSUB"),execDetail);
//					MainAndSubDto datadto = null;
//					TfPaybankRefundmainDto tempdto = null;
//					for(int i=0;i<detailList.size();i++)
//					{
//						tempdto = (TfPaybankRefundmainDto)detailList.get(i);
//						datadto = new MainAndSubDto();
//						datadto.setMainDto(tempdto);
//						datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//历史表子表
//						datadto.setExtdto(map5201.get(String.valueOf(tempdto.getIvousrlno())));
//						getList.add(datadto);
//					}
//					
//				}
//				detailList=  (List<IDto>) execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTF_PAYBANK_REFUNDMAIN","TF_PAYBANK_REFUNDMAIN"),TfPaybankRefundmainDto.class).getDtoCollection();//主表
//				if(detailList!=null&&detailList.size()>0)
//				{
//					if(getList==null)
//						getList = new ArrayList<IDto>();
//					String subsql = "select * from TF_PAYBANK_REFUNDSUB where I_VOUSRLNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),"HTF_PAYBANK_REFUNDMAIN","TF_PAYBANK_REFUNDMAIN"), "*", "I_VOUSRLNO")+")";
//					List<IDto> subList = null;
//					subList = (List<IDto>)execDetail.runQuery(subsql, TfPaybankRefundsubDto.class).getDtoCollection();
//					Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
//					if(subList!=null&&subList.size()>0)
//					{
//						List<IDto> tempList = null;
//						TfPaybankRefundsubDto subdto = null;
//						for(IDto tempdto:subList)
//						{
//							subdto = (TfPaybankRefundsubDto)tempdto;
//							tempList = subMap.get(String.valueOf(subdto.getIvousrlno()));
//							if(tempList==null)
//							{
//								tempList = new ArrayList<IDto>();
//								tempList.add(subdto);
//								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
//							}else
//							{
//								tempList.add(subdto);
//								subMap.put(String.valueOf(subdto.getIvousrlno()), tempList);
//							}
//						}
//					}
//					MainAndSubDto datadto = null;
//					TfPaybankRefundmainDto tempdto = null;
//					for(int i=0;i<detailList.size();i++)
//					{
//						tempdto = (TfPaybankRefundmainDto)detailList.get(i);
//						datadto = new MainAndSubDto();
//						datadto.setMainDto(tempdto);
//						datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));//子表
//						getList.add(datadto);
//					}
//				}
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
	public Map<String, MainAndSubDto> get5201Map(String mainsql5201, String subsql5201,SQLExecutor execDetail) throws JAFDatabaseException {
		Map<String,MainAndSubDto> getMap = null;
		if(mainsql5201!=null&&subsql5201!=null)
		{
			List list5201 = (List<IDto>)execDetail.runQuery(mainsql5201,TfDirectpaymsgmainDto.class).getDtoCollection();
			if(list5201!=null&&list5201.size()>0)
			{
				getMap = new HashMap<String,MainAndSubDto>();
				List<IDto> subList = (List<IDto>)execDetail.runQuery(subsql5201, TfDirectpaymsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TfDirectpaymsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TfDirectpaymsgsubDto)tempdto;
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
				TfDirectpaymsgmainDto tempdto = null;
				MainAndSubDto datadto = null;
				for(int i=0;i<list5201.size();i++)
				{
					tempdto = (TfDirectpaymsgmainDto)list5201.get(i);
					datadto = new MainAndSubDto();
					datadto.setMainDto(tempdto);
					datadto.setSubDtoList(subMap.get(String.valueOf(tempdto.getIvousrlno())));
					getMap.put(String.valueOf(tempdto.getIvousrlno()), datadto);
				}
			}
		}
		return getMap;
	}
	private List<List<MainAndSubDto>> getSplitPack(List<IDto> dataList,boolean ispackage)
	{
		List<List<MainAndSubDto>> getList = null;
		if(dataList!=null&&dataList.size()>0)
		{
			Map<String,List<MainAndSubDto>> tempMap = new HashMap<String,List<MainAndSubDto>>();
			TvPayreckBankDto ddto = null;
			TvPayreckBankBackDto gdto = null;
			TfDirectpaymsgmainDto fdto = null;
			TfPaybankRefundmainDto pdto = null;
			List<MainAndSubDto> tempList = null;
			MainAndSubDto dto = null;
			for(IDto idto:dataList)
			{
				dto = (MainAndSubDto)idto;
				if(!ispackage)
				{
					if(tempMap.get("alldata")==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put("alldata", tempList);
					}else
					{
						tempList = tempMap.get("alldata");
						tempList.add(dto);
						tempMap.put("alldata",tempList);
					}
				}else if(dto.getMainDto() instanceof TvPayreckBankDto)
				{
					ddto = (TvPayreckBankDto)dto.getMainDto();
					if(tempMap.get(ddto.getSagentbnkcode())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put(ddto.getSagentbnkcode(), tempList);
					}else
					{
						tempList = tempMap.get(ddto.getSagentbnkcode());
						tempList.add(dto);
						tempMap.put(ddto.getSagentbnkcode(),tempList);
					}
				}else if(dto.getMainDto() instanceof TvPayreckBankBackDto)
				{
					gdto = (TvPayreckBankBackDto)dto.getMainDto();
					if(tempMap.get(gdto.getSagentbnkcode())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put(gdto.getSagentbnkcode(), tempList);
					}else
					{
						tempList = tempMap.get(gdto.getSagentbnkcode());
						tempList.add(dto);
						tempMap.put(gdto.getSagentbnkcode(),tempList);
					}
				}
				else if(dto.getMainDto() instanceof TfDirectpaymsgmainDto)
				{
					fdto = (TfDirectpaymsgmainDto)dto.getMainDto();
					if("1".equals(fdto.getSbusinesstypecode())||"3".equals(fdto.getSbusinesstypecode()))
					{
						if(tempMap.get("011")==null)
						{
							tempList = new ArrayList<MainAndSubDto>();
							tempList.add(dto);
							tempMap.put("011", tempList);
						}else
						{
							tempList = tempMap.get("011");
							tempList.add(dto);
							tempMap.put("011",tempList);
						}
					}else
					{
						if(tempMap.get(fdto.getSpaybankcode())==null)
						{
							tempList = new ArrayList<MainAndSubDto>();
							tempList.add(dto);
							tempMap.put(fdto.getSpaybankcode(), tempList);
						}else
						{
							tempList = tempMap.get(fdto.getSpaybankcode());
							tempList.add(dto);
							tempMap.put(fdto.getSpaybankcode(),tempList);
						}
					}
				}
				else if(dto.getMainDto() instanceof TfPaybankRefundmainDto)
				{
					pdto = (TfPaybankRefundmainDto)dto.getMainDto();
					if(tempMap.get(pdto.getSpaysndbnkno())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put(pdto.getSpaysndbnkno(), tempList);
					}else
					{
						tempList = tempMap.get(pdto.getSpaysndbnkno());
						tempList.add(dto);
						tempMap.put(pdto.getSpaysndbnkno(),tempList);
					}
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
	
	
	/**
	 * 将查询结果按资金类型分包
	 * @param alldata
	 * @return
	 */
	private Map<String, List>  getFundTypeMap(List<IDto> alldata){
		Map<String, List> map = new HashMap<String, List>();
		if(alldata==null||alldata.size()<=0)
			return map;
		for(IDto msdto : alldata){
			MainAndSubDto dto = (MainAndSubDto) msdto;
			if(dto.getMainDto() instanceof TvPayreckBankDto){
				if(map.get(((TvPayreckBankDto)dto.getMainDto()).getSfundtypecode())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(((TvPayreckBankDto)dto.getMainDto()).getSfundtypecode(), data);
				}else{
					List<IDto> data = map.get(((TvPayreckBankDto)dto.getMainDto()).getSfundtypecode());
					data.add(dto);
				}
			}else if(dto.getMainDto() instanceof TvPayreckBankBackDto){
				if(map.get(((TvPayreckBankBackDto)dto.getMainDto()).getSfundtypecode())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(((TvPayreckBankBackDto)dto.getMainDto()).getSfundtypecode(), data);
				}else{
					List<IDto> data = map.get(((TvPayreckBankBackDto)dto.getMainDto()).getSfundtypecode());
					data.add(dto);
				}
			}
			
		}
		return map;
	}
	
}

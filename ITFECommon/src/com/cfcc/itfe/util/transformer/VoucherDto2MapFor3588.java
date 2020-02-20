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
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.MainAndSubDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRealdialMainDto;
import com.cfcc.itfe.persistence.dto.TfReconcileRealdialSubDto;
import com.cfcc.itfe.persistence.dto.TrTaxorgPayoutReportDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
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
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * 实拨信息对账请求(3508)转化
 * 
 * @author renqingbin
 * @time  2013-08-16
 * 
 */
@SuppressWarnings("unchecked")
public class VoucherDto2MapFor3588 implements IVoucherDto2Map{

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor3588.class);
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
		if (ITFECommonConstant.SRC_NODE.equals("000087100006"))
		{
			List returnList = new ArrayList();
			if(vDto.getSext3()==null||"".equals(vDto.getSext3()))
			{
				vDto.setSext3("011");//云南地税也对账实拨资金、财政也对账实拨资金1财政2地税
				returnList.addAll(getVoucher(vDto));
				if(ITFECommonConstant.PUBLICPARAM.contains(",send3508=more,"))
				{
					vDto.setSext3("012");
					returnList.addAll(getVoucher(vDto));
				}
			}else
			{
				returnList.addAll(getVoucher(vDto));
			}
			return returnList;
		}else
		{
			return getVoucher(vDto);
		}
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
		List<List<MainAndSubDto>> dataList = getSplitPack(alldata);
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
			            if (ITFECommonConstant.SRC_NODE.equals("201053200014"))
			              mainvou = VoucherUtil.getCheckNo(vDto,tempList,i);
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
						dto.setSattach(danhao);//对账单号
						dto.setShold1(String.valueOf(sendList.size()));//包总数
						dto.setShold2(String.valueOf(i+1));//包序号
						dto.setSstyear(dto.getScreatdate().substring(0, 4));				
						dto.setScheckdate(vDto.getScheckdate());
						dto.setSpaybankcode(vDto.getSpaybankcode()==null?"":vDto.getSpaybankcode());
						dto.setShold3(vDto.getShold3());
						dto.setShold4(vDto.getShold4());
//							if(ITFECommonConstant.PUBLICPARAM.indexOf(",stampmode=sign,")<0)//是否采用签名
//							{
						dto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
						dto.setSdemo("处理成功");
						dto.setSvoucherflag("1");
						dto.setSvoucherno(mainvou);	
						dto.setIcount(tempList.size());
						dto.setSext1("1");//发起方式1:人行发起,2:财政发起,3:商行发起
						dto.setSext2(getString(vDto.getSext2()));
						dto.setSext3(getString(vDto.getSext3()));
						dto.setSext4(getString(vDto.getSext4()));
						dto.setSext5(getString(vDto.getSext5()));
						if(ITFECommonConstant.PUBLICPARAM.contains(",send3508=more,")&&"012".equals(vDto.getSext3()))
						{
							dto.setSext5(vDto.getSext3());
						}
						mapList.add(vDto);
						mapList.add(cDto);
						mapList.add(tempList);
						List<IDto> idtoList = new ArrayList<IDto>();
						Map map=tranfor(mapList,sendList.size(),i+1,danhao,idtoList);
						dto.setNmoney(MtoCodeTrans.transformBigDecimal(map.get("Voucher")==null?null:((Map)map.get("Voucher")).get("AllAmt")));
						dto.setIcount(Integer.valueOf(String.valueOf((map.get("Voucher")==null?null:((Map)map.get("Voucher")).get("AllNum")))));
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
			MainAndSubDto mdto = null;
			TvPayoutmsgmainDto pdto = null;
			TvPayoutbackmsgMainDto badto = null;
			TvVoucherinfoAllocateIncomeDto tadto = null;
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
			vouchermap.put("BeginDate",vDto.getShold3());//对账起始日期
			vouchermap.put("EndDate",vDto.getShold4());//对账终止日期
			vouchermap.put("ClearBankCode",getString(""));//人民银行编码
			vouchermap.put("ClearBankName",getString(""));//人民银行名称
			vouchermap.put("ClearAccNo",getString(""));//拨款账号
			vouchermap.put("ClearAccNanme",getString(""));//拨款账户名称
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
			{
				vouchermap.put("BgtTypeCode",vDto.getSext3());//预算类型
				vouchermap.put("BgtTypeName","1".equals(vDto.getSext3())?"预算内":"预算外");//预算类型名称
			}
			BigDecimal allamt=new BigDecimal("0.00");		
			List<Object> Detail= new ArrayList<Object>();
			int id=0;
			List subdtolist = new ArrayList();
			for(IDto idto:detailList)
			{
				mdto = (MainAndSubDto)idto;
				setIdtoToMap(vouchermap,mdto.getMainDto());
				if(mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0&&mdto.getMainDto() instanceof TvPayoutmsgmainDto)
				{
					pdto = (TvPayoutmsgmainDto)mdto.getMainDto();
					TvPayoutmsgsubDto subdto = null;
					for(int i=0;i<mdto.getSubDtoList().size();i++)
					{
						id++;
						subdto = (TvPayoutmsgsubDto)mdto.getSubDtoList().get(i);
						HashMap<String, Object> Detailmap = new HashMap<String, Object>();
						Detailmap.put("Id",vDto.getSvoucherno()+id);//序号
						Detailmap.put("PayDetailId",subdto.getSid()==null?subdto.getStaxticketno():subdto.getSid());//拨款明细Id
						Detailmap.put("VoucherNo",getString(pdto.getStaxticketno()));//预留字段1
						Detailmap.put("BgtTypeCode",getString(subdto.getSbgttypecode()));//预算类型编码
						Detailmap.put("BgtTypeName",getString(subdto.getSbgttypename()));//预算类型名称
						Detailmap.put("FundTypeCode",getString(pdto.getSfundtypecode()));//资金性质编码
						Detailmap.put("FundTypeName",getString(pdto.getSfundtypename()));//资金性质名称
						Detailmap.put("PayTypeCode",getString("91"));//支付方式编码
						Detailmap.put("PayTypeName",getString("实拨资金"));//支付方式名称
						Detailmap.put("PayeeAcctNo","".equals(getString(subdto.getSpayeeacctno()))?getString(pdto.getSrecacct()):getString(subdto.getSpayeeacctno()));//收款人账号
						Detailmap.put("PayeeAcctName","".equals(getString(subdto.getSpayeeacctname()))?getString(pdto.getSrecname()):getString(subdto.getSpayeeacctname()));//收款人名称
						Detailmap.put("PayeeAcctBankName","".equals(getString(subdto.getSpayeeacctbankname()))?getString(pdto.getSrecbankname()):getString(subdto.getSpayeeacctbankname()));//收款人银行
						Detailmap.put("PayAcctNo",getString(pdto.getSpayeracct()));//付款账户账号
						Detailmap.put("PayAcctName",getString(pdto.getSpayername()));//付款账户名称
						Detailmap.put("PayAcctBankName",getString(pdto.getSpayerbankname()));//付款账户银行
						Detailmap.put("AgencyCode","".equals(getString(pdto.getSbudgetunitcode()))?getString(subdto.getSagencycode()):getString(pdto.getSbudgetunitcode()));//预算单位编码
						Detailmap.put("AgencyName","".equals(getString(pdto.getSunitcodename()))?getString(subdto.getSagencyname()):getString(pdto.getSunitcodename()));//预算单位名称
						Detailmap.put("ExpFuncCode",getString(subdto.getSfunsubjectcode()));//支出功能分类科目编码
						Detailmap.put("ExpFuncName",getString(subdto.getSexpfuncname()));//支出功能分类科目名称
						Detailmap.put("ExpEcoCode",getString(subdto.getSexpecocode()));//经济分类科目编码
						Detailmap.put("ExpEcoName",getString(subdto.getSexpeconame()));//经济分类科目名称
						Detailmap.put("PayAmt",String.valueOf(subdto.getNmoney()));//拨款金额
						Detailmap.put("Hold1",getString(subdto.getStaxticketno()));//预留字段1
						Detailmap.put("Hold2",getString(""));//预留字段2
						Detailmap.put("Hold3",getString(""));//预留字段3
						Detailmap.put("Hold4",getString(""));//预留字段4
						allamt=allamt.add(subdto.getNmoney());
						Detail.add(Detailmap);
						subdtolist.add(getSubDto(Detailmap,vouchermap));
					}
					if("1".equals(pdto.getSbackflag()))
					{
						for(int i=0;i<mdto.getSubDtoList().size();i++)
						{
							id++;
							subdto = (TvPayoutmsgsubDto)mdto.getSubDtoList().get(i);
							HashMap<String, Object> Detailmap = new HashMap<String, Object>();
							Detailmap.put("Id",vDto.getSvoucherno()+id);//序号
							Detailmap.put("PayDetailId",subdto.getSid()==null?subdto.getStaxticketno():subdto.getSid());//拨款明细Id
							Detailmap.put("VoucherNo",getString(pdto.getStaxticketno()));//预留字段1
							Detailmap.put("BgtTypeCode",getString(subdto.getSbgttypecode()));//预算类型编码
							Detailmap.put("BgtTypeName",getString(subdto.getSbgttypename()));//预算类型名称
							Detailmap.put("FundTypeCode",getString(pdto.getSfundtypecode()));//资金性质编码
							Detailmap.put("FundTypeName",getString(pdto.getSfundtypename()));//资金性质名称
							Detailmap.put("PayTypeCode",getString("91"));//支付方式编码
							Detailmap.put("PayTypeName",getString("实拨资金退款"));//支付方式名称
							Detailmap.put("PayeeAcctNo","".equals(getString(subdto.getSpayeeacctno()))?getString(pdto.getSrecacct()):getString(subdto.getSpayeeacctno()));//收款人账号
							Detailmap.put("PayeeAcctName","".equals(getString(subdto.getSpayeeacctname()))?getString(pdto.getSrecname()):getString(subdto.getSpayeeacctname()));//收款人名称
							Detailmap.put("PayeeAcctBankName","".equals(getString(subdto.getSpayeeacctbankname()))?getString(pdto.getSrecbankname()):getString(subdto.getSpayeeacctbankname()));//收款人银行
							Detailmap.put("PayAcctNo",getString(pdto.getSpayeracct()));//付款账户账号
							Detailmap.put("PayAcctName",getString(pdto.getSpayername()));//付款账户名称
							Detailmap.put("PayAcctBankName",getString(pdto.getSpayerbankname()));//付款账户银行
							Detailmap.put("AgencyCode",getString(pdto.getSbudgetunitcode()));//预算单位编码
							Detailmap.put("AgencyName",getString(pdto.getSunitcodename()));//预算单位名称
							Detailmap.put("ExpFuncCode",getString(subdto.getSfunsubjectcode()));//支出功能分类科目编码
							Detailmap.put("ExpFuncName",getString(subdto.getSexpfuncname()));//支出功能分类科目名称
							Detailmap.put("ExpEcoCode",getString(subdto.getSexpecocode()));//经济分类科目编码
							Detailmap.put("ExpEcoName",getString(subdto.getSexpeconame()));//经济分类科目名称
							if (ITFECommonConstant.SRC_NODE.equals("201053200014")){//青岛
								Detailmap.put("PayDetailId", pdto.getStaxticketno());
								Detailmap.put("PayAmt","-"+String.valueOf(pdto.getShold2()));//拨款金额
							}else{
					            Detailmap.put("PayDetailId", pdto.getSdealno() + subdto.getSbizno());
					            Detailmap.put("PayAmt","-"+String.valueOf(subdto.getNmoney()));//拨款金额
					        }
							Detailmap.put("Hold1",getString(""));//预留字段1
							Detailmap.put("Hold2",getString(""));//预留字段2
							Detailmap.put("Hold3",getString(""));//预留字段3
							Detailmap.put("Hold4",getString(""));//预留字段4
							allamt=allamt.subtract(subdto.getNmoney());
							Detail.add(Detailmap);
							subdtolist.add(getSubDto(Detailmap,vouchermap));
						}
					}
				}else if(mdto.getMainDto() instanceof TvVoucherinfoAllocateIncomeDto)
				{
					tadto = (TvVoucherinfoAllocateIncomeDto)mdto.getMainDto();
					id++;
					HashMap<String, Object> Detailmap = new HashMap<String, Object>();
					Detailmap.put("Id",vDto.getSvoucherno()+String.valueOf(tadto.getSdealno())+id);//序号
					Detailmap.put("PayDetailId",tadto.getSdealno());//拨款明细Id
					Detailmap.put("VoucherNo",getString(tadto.getSdealno()));//预留字段1
					Detailmap.put("BgtTypeCode",getString(""));//预算类型编码
					Detailmap.put("BgtTypeName",getString(""));//预算类型名称
					Detailmap.put("FundTypeCode",getString("9"));//资金性质编码
					Detailmap.put("FundTypeName",getString("其它资金"));//资金性质名称
					Detailmap.put("PayTypeCode",getString("91"));//支付方式编码
					Detailmap.put("PayTypeName",getString("实拨退款"));//支付方式名称
					Detailmap.put("PayeeAcctNo",getString(tadto.getSpayeeacctno()));//收款人账号
					Detailmap.put("PayeeAcctName",getString(tadto.getSpayeeacctname()));//收款人名称
					Detailmap.put("PayeeAcctBankName",getString(tadto.getSpayeeacctbankname()));//收款人银行
					Detailmap.put("PayAcctNo",getString(tadto.getSpayacctno()));//付款账户账号
					Detailmap.put("PayAcctName",getString(tadto.getSpayacctname()));//付款账户名称
					Detailmap.put("PayAcctBankName",getString(tadto.getSpayacctbankname()));//付款账户银行
					Detailmap.put("AgencyCode",getString("99999"));//预算单位编码
					Detailmap.put("AgencyName",getString("99999"));//预算单位名称
					Detailmap.put("ExpFuncCode",getString("99999"));//支出功能分类科目编码
					Detailmap.put("ExpFuncName",getString("99999"));//支出功能分类科目名称
					Detailmap.put("ExpEcoCode",getString(""));//经济分类科目编码
					Detailmap.put("ExpEcoName",getString(""));//经济分类科目名称
					Detailmap.put("PayAmt",String.valueOf(tadto.getNmoney()));//拨款金额
//					Detailmap.put("PayAmt","-"+String.valueOf(tadto.getNmoney()));//拨款金额
					Detailmap.put("Hold1",getString(""));//预留字段1
					Detailmap.put("Hold2",getString(""));//预留字段2
					Detailmap.put("Hold3",getString(""));//预留字段3
					Detailmap.put("Hold4",getString(""));//预留字段4
					allamt=allamt.subtract(tadto.getNmoney());
					Detail.add(Detailmap);
					subdtolist.add(getSubDto(Detailmap,vouchermap));
				}else if(mdto.getSubDtoList()!=null&&mdto.getSubDtoList().size()>0&&mdto.getMainDto() instanceof TvPayoutbackmsgMainDto)
				{
					badto = (TvPayoutbackmsgMainDto)mdto.getMainDto();
					TvPayoutbackmsgSubDto subdto = null;
					for(int i=0;i<mdto.getSubDtoList().size();i++)
					{
						id++;
						subdto = (TvPayoutbackmsgSubDto)mdto.getSubDtoList().get(i);
						HashMap<String, Object> Detailmap = new HashMap<String, Object>();
						Detailmap.put("Id",vDto.getSvoucherno()+id);//序号
						Detailmap.put("PayDetailId",subdto.getSseqno());//拨款明细Id
						Detailmap.put("VoucherNo",getString(badto.getSvouno()));//预留字段1
						Detailmap.put("BgtTypeCode",getString(subdto.getSbudgetprjcode()));//预算类型编码
						Detailmap.put("BgtTypeName",getString(""));//预算类型名称
						Detailmap.put("FundTypeCode",getString("9"));//资金性质编码
						Detailmap.put("FundTypeName",getString("其它资金"));//资金性质名称
						Detailmap.put("PayTypeCode",getString("91"));//支付方式编码
						Detailmap.put("PayTypeName",getString("实拨退款"));//支付方式名称
						Detailmap.put("PayeeAcctNo",getString(badto.getSpayeeacct()));//收款人账号
						Detailmap.put("PayeeAcctName",getString(badto.getSpayeename()));//收款人名称
						Detailmap.put("PayeeAcctBankName",getString(badto.getSpayeeopbkno()));//收款人银行
						Detailmap.put("PayAcctNo",getString(badto.getSpayeracct()));//付款账户账号
						Detailmap.put("PayAcctName",getString(badto.getSpayername()));//付款账户名称
						Detailmap.put("PayAcctBankName",getString(""));//付款账户银行
						Detailmap.put("AgencyCode",getString(badto.getSbudgetunitcode()));//预算单位编码
						Detailmap.put("AgencyName",getString(badto.getSunitcodename()));//预算单位名称
						Detailmap.put("ExpFuncCode",getString(subdto.getSfunsubjectcode()));//支出功能分类科目编码
						Detailmap.put("ExpFuncName",getString(subdto.getSfunsubjectname()));//支出功能分类科目名称
						Detailmap.put("ExpEcoCode",getString(subdto.getSecnomicsubjectcode()));//经济分类科目编码
						Detailmap.put("ExpEcoName",getString(""));//经济分类科目名称
						Detailmap.put("PayAmt",String.valueOf(subdto.getNmoney()));//拨款金额
//						Detailmap.put("PayAmt","-"+String.valueOf(subdto.getNmoney()));//拨款金额
						Detailmap.put("Hold1",getString(""));//预留字段1
						Detailmap.put("Hold2",getString(""));//预留字段2
						Detailmap.put("Hold3",getString(""));//预留字段3
						Detailmap.put("Hold4",getString(""));//预留字段4
						allamt=allamt.subtract(subdto.getNmoney());
						Detail.add(Detailmap);
						subdtolist.add(getSubDto(Detailmap,vouchermap));
					}
				}
			}
			vouchermap.put("AllNum",id);//总笔数
			vouchermap.put("AllAmt",MtoCodeTrans.transformString(allamt));//总金额
			if(vDto.getStrecode().startsWith("13")){//福州预留字段添加资金性质
				vouchermap.put("Hold1",getString(pdto.getSfundtypecode()));//预留字段1
				vouchermap.put("Hold2",getString(pdto.getSfundtypename()));//预留字段2
			}else{
				vouchermap.put("Hold1","");//预留字段1
				vouchermap.put("Hold2",getString(vDto.getSverifyusercode()));//预留字段2
			}
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
	private TfReconcileRealdialMainDto getMainDto(Map<String, Object> mainMap,TvVoucherinfoDto vDto)
	{
		TfReconcileRealdialMainDto mainDto = new TfReconcileRealdialMainDto();
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
		mainDto.setSclearaccno(getString(mainMap,"ClearAccNo"));//拨款账号
		mainDto.setSclearaccnanme(getString(mainMap,"ClearAccNanme"));//拨款账户名称
		mainDto.setSbegindate(getString(mainMap,"BeginDate"));//BeginDate",vDto.getScheckdate());//对账起始日期
		mainDto.setSenddate(getString(mainMap,"EndDate"));//EndDate",vDto.getSpaybankcode());//对账终止日期
		mainDto.setSallnum(getString(mainMap,"AllNum"));//AllNum",detailList.size());//总笔数
		mainDto.setNallamt(MtoCodeTrans.transformBigDecimal(getString(mainMap,"AllAmt")));//AllAmt","");//总金额
		mainDto.setShold1(getString(mainMap,"Hold1"));//Hold1","");//预留字段1
		mainDto.setShold2(getString(mainMap,"Hold2"));//Hold2","");//预留字段2
		mainDto.setSext1("1");//发起方式1:人行发起,2:财政发起,3:商行发起
		return mainDto;
	}
	private TfReconcileRealdialSubDto getSubDto(HashMap<String, Object> subMap,HashMap<String, Object> mainMap)
	{
		TfReconcileRealdialSubDto subDto = new TfReconcileRealdialSubDto();
		String voucherno = getString(mainMap,"VoucherNo");
		if(voucherno.length()>19)
			voucherno = voucherno.substring(voucherno.length()-19);
		subDto.setIvousrlno(Long.valueOf(voucherno));
		String id = getString(subMap,"Id");
		if(id.length()>19)
			id = id.substring(id.length()-19);
		subDto.setIseqno(Long.valueOf(id));//Id",vDto.getSdealno()+(++id));//序号
		subDto.setSid(getString(subMap,"Id"));
		subDto.setSpaydetailid(getString(subMap,"PayDetailId"));//拨款明细Id
		subDto.setSbgttypecode(getString(subMap,"BgtTypeCode"));//预算类型编码
		subDto.setSbgttypename(getString(subMap,"BgtTypeName"));//预算类型名称
		subDto.setSfundtypecode(getString(subMap,"FundTypeCode"));//资金性质编码
		subDto.setSfundtypename(getString(subMap,"FundTypeName"));//资金性质名称
		subDto.setSpaytypecode(getString(subMap,"PayTypeCode"));//支付方式编码
		subDto.setSpaytypename(getString(subMap,"PayTypeName"));//支付方式名称
		subDto.setSpayeeacctno(getString(subMap,"PayeeAcctNo"));//收款人账号
		subDto.setSpayeeacctname(getString(subMap,"PayeeAcctName"));//收款人名称
		subDto.setSpayeeacctbankname(getString(subMap,"PayeeAcctBankName"));//收款人银行
		subDto.setSpayacctno(getString(subMap,"PayAcctNo"));//付款账户账号
		subDto.setSpayacctname(getString(subMap,"PayAcctName"));//付款账户名称
		subDto.setSpayacctbankname(getString(subMap,"PayAcctBankName"));//付款账户银行
		subDto.setSagencycode(getString(subMap,"AgencyCode"));//预算单位编码
		subDto.setSagencyname(getString(subMap,"AgencyName"));//预算单位名称
		subDto.setSexpfunccode(getString(subMap,"ExpFuncCode"));//支出功能分类科目编码
		subDto.setSexpfuncname(getString(subMap,"ExpFuncName"));//支出功能分类科目名称
		subDto.setShold1(getString(subMap,"ExpEcoCode"));//经济分类科目编码
		subDto.setShold1(getString(subMap,"ExpEcoName"));//经济分类科目名称
		subDto.setNpayamt(MtoCodeTrans.transformBigDecimal(getString(subMap,"PayAmt")));//拨款金额
		subDto.setShold1(getString(subMap,"Hold1"));//预留字段1
		subDto.setShold2(getString(subMap,"Hold2"));//预留字段2
		subDto.setShold3(getString(subMap,"Hold3"));//预留字段3
		subDto.setShold4(getString(subMap,"Hold4"));//预留字段4
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
			sql = new StringBuffer("SELECT * FROM HTV_PAYOUTMSGMAIN WHERE S_BIZNO in(");//查询实拨资金历史表已经回单的数据
			sql.append("SELECT S_DEALNO FROM HTV_VOUCHERINFO WHERE (S_VTCODE= ? "+(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,")?" OR S_VTCODE= ?":"")+")  AND S_ORGCODE= ? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ? ");
			if(vDto.getSext3()!=null&&"011".equals(vDto.getSext3()))//云南地税也对账实拨资金、财政也对账实拨资金011财政012地税
					sql.append(" and S_ATTACH<>?");
			else if(vDto.getSext3()!=null&&"012".equals(vDto.getSext3()))//云南地税也对账实拨资金、财政也对账实拨资金011财政012地税
				sql.append(" and S_ATTACH=?");
			sql.append(") ");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				sql.append(" AND S_BUDGETTYPE=? ");
			execDetail.addParam(MsgConstant.VOUCHER_NO_5207);
			if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
			{
				execDetail.addParam(MsgConstant.VOUCHER_NO_5267);
			}
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
				execDetail.addParam("012");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.setMaxRows(500000);
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayoutmsgmainDto.class).getDtoCollection();//历史表主表
			List<IDto> mainsublist = null;
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_PAYOUTMSGSUB where S_BIZNO in("+StringUtil.replace(sql.toString(), "*", "S_BIZNO")+")";//查询实拨资金子表历史表数据
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_5207);
				if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
				{
					execDetail.addParam(MsgConstant.VOUCHER_NO_5267);
				}
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
					execDetail.addParam("012");
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutmsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutmsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutmsgsubDto)tempdto;
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
				TvPayoutmsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutmsgmainDto)detailList.get(i);
					tempdto.setSbackflag("0");
					mainsublist = subMap.get(String.valueOf(tempdto.getSbizno()));
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
			//查询实拨资金正式表已经回单的数据
			execDetail.addParam(MsgConstant.VOUCHER_NO_5207);
			if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
			{
				execDetail.addParam(MsgConstant.VOUCHER_NO_5267);
			}
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
				execDetail.addParam("012");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.setMaxRows(500000);
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYOUTMSGMAIN","TV_PAYOUTMSGMAIN"),TvPayoutmsgmainDto.class).getDtoCollection();//正式表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYOUTMSGSUB where S_BIZNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYOUTMSGMAIN","TV_PAYOUTMSGMAIN"), "*", "S_BIZNO")+")";//查询实拨资金子表正式表表数据
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_5207);
				if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
				{
					execDetail.addParam(MsgConstant.VOUCHER_NO_5267);
				}
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
					execDetail.addParam("012");
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutmsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutmsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutmsgsubDto)tempdto;
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
				TvPayoutmsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutmsgmainDto)detailList.get(i);
					tempdto.setSbackflag("0");
					mainsublist = subMap.get(String.valueOf(tempdto.getSbizno()));
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
			//前置发起实拨资金退款数据
			sql = new StringBuffer("SELECT * FROM HTV_PAYOUTMSGMAIN WHERE S_BIZNO in(");//查询实拨资金历史表已经回单的数据
			sql.append("SELECT S_EXT4 FROM HTV_VOUCHERINFO WHERE S_EXT4 is not null and S_EXT4<>'' and (S_VTCODE= ? "+(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,")?" OR S_VTCODE= ?":"")+")  AND S_ORGCODE= ? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ? ");
			if(vDto.getSext3()!=null&&"011".equals(vDto.getSext3()))//云南地税也对账实拨资金、财政也对账实拨资金011财政012地税
					sql.append(" and S_ATTACH<>?");
			else if(vDto.getSext3()!=null&&"012".equals(vDto.getSext3()))//云南地税也对账实拨资金、财政也对账实拨资金011财政012地税
				sql.append(" and S_ATTACH=?");
			sql.append(") ");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				sql.append(" AND S_BUDGETTYPE=? ");
			execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
			if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
			{
				execDetail.addParam(MsgConstant.VOUCHER_NO_3268);
			}
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
				execDetail.addParam("012");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.setMaxRows(500000);
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayoutmsgmainDto.class).getDtoCollection();//历史表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from HTV_PAYOUTMSGSUB where S_BIZNO in("+StringUtil.replace(sql.toString(), "*", "S_BIZNO")+")";//查询实拨资金子表历史表数据
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
				if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
				{
					execDetail.addParam(MsgConstant.VOUCHER_NO_3268);
				}
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
					execDetail.addParam("012");
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutmsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutmsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutmsgsubDto)tempdto;
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
				TvPayoutmsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutmsgmainDto)detailList.get(i);
					mainsublist = subMap.get(String.valueOf(tempdto.getSbizno()));
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
			//查询实拨资金正式表已经回单的数据
			execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
			if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
			{
				execDetail.addParam(MsgConstant.VOUCHER_NO_3268);
			}
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
				execDetail.addParam("012");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.setMaxRows(500000);
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYOUTMSGMAIN","TV_PAYOUTMSGMAIN"),TvPayoutmsgmainDto.class).getDtoCollection();//正式表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYOUTMSGSUB where S_BIZNO in("+StringUtil.replace(StringUtil.replace(StringUtil.replace(sql.toString(), "HTV_VOUCHERINFO", "TV_VOUCHERINFO"),"HTV_PAYOUTMSGMAIN","TV_PAYOUTMSGMAIN"), "*", "S_BIZNO")+")";//查询实拨资金子表正式表表数据
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
				if(ITFECommonConstant.PUBLICPARAM.contains(",collectpayment=1,"))
				{
					execDetail.addParam(MsgConstant.VOUCHER_NO_3268);
				}
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("011".equals(vDto.getSext3())||"012".equals(vDto.getSext3())))
					execDetail.addParam("012");
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutmsgsubDto.class).getDtoCollection();
				Map<String,List<IDto>> subMap = new HashMap<String,List<IDto>>();
				if(subList!=null&&subList.size()>0)
				{
					List<IDto> tempList = null;
					TvPayoutmsgsubDto subdto = null;
					for(IDto tempdto:subList)
					{
						subdto = (TvPayoutmsgsubDto)tempdto;
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
				TvPayoutmsgmainDto tempdto = null;
				for(int i=0;i<detailList.size();i++)
				{
					tempdto = (TvPayoutmsgmainDto)detailList.get(i);
					mainsublist = subMap.get(String.valueOf(tempdto.getSbizno()));
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
			sql = new StringBuffer("SELECT * FROM TV_PAYOUTBACKMSG_MAIN WHERE S_VOUNO IN(");//查询tcbs回执生成退款已经发送凭证库的数据
			sql.append("SELECT S_VOUCHERNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ?  AND S_ORGCODE= ? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?) ");
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				sql.append(" AND S_BUDGETTYPE=? ");
			execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.setMaxRows(500000);
			detailList=  (List<IDto>) execDetail.runQuery(sql.toString(),TvPayoutbackmsgMainDto.class).getDtoCollection();//历史表主表
			execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
				execDetail.addParam(vDto.getSext3());
			execDetail.setMaxRows(500000);
			detailList.addAll(execDetail.runQuery(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvPayoutbackmsgMainDto.class).getDtoCollection());//历史表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
				String subsql = "select * from TV_PAYOUTBACKMSG_SUB where S_BIZNO in("+StringUtil.replace(sql.toString(), "*", "S_BIZNO")+")";
				List<IDto> subList = null;
				execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
				subList = (List<IDto>)execDetail.runQuery(subsql, TvPayoutbackmsgSubDto.class).getDtoCollection();
				execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
				execDetail.addParam(vDto.getSorgcode());
				execDetail.addParam(vDto.getStrecode());
				execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
				execDetail.addParam(vDto.getShold3());
				execDetail.addParam(vDto.getShold4());
				if(vDto.getSext3()!=null&&("1".equals(vDto.getSext3())||"2".equals(vDto.getSext3())))
					execDetail.addParam(vDto.getSext3());
				execDetail.setMaxRows(500000);
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
					mainsublist = subMap.get(String.valueOf(tempdto.getSbizno()));
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
			sql = new StringBuffer("SELECT * FROM TV_VOUCHERINFO_ALLOCATE_INCOME WHERE S_DEALNO IN(");//查询实拨资金历史表已经回单的数据
			sql.append("SELECT S_VOUCHERNO FROM HTV_VOUCHERINFO WHERE S_VTCODE= ?  AND S_ORGCODE= ? ");
			sql.append("AND S_TRECODE= ?  AND S_STATUS=? ");
			sql.append("AND S_CONFIRUSERCODE BETWEEN ? AND ?) ");
			execDetail.addParam(MsgConstant.VOUCHER_NO_3208);
			execDetail.addParam(vDto.getSorgcode());
			execDetail.addParam(vDto.getStrecode());
			execDetail.addParam(DealCodeConstants.VOUCHER_SUCCESS);
			execDetail.addParam(vDto.getShold3());
			execDetail.addParam(vDto.getShold4());
			execDetail.setMaxRows(500000);
			detailList = (List<IDto>)execDetail.runQuery(StringUtil.replace(sql.toString(),"HTV_VOUCHERINFO","TV_VOUCHERINFO"),TvVoucherinfoAllocateIncomeDto.class).getDtoCollection();//正式表主表
			if(detailList!=null&&detailList.size()>0)
			{
				if(getList==null)
					getList = new ArrayList<IDto>();
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
	private List<List<MainAndSubDto>> getSplitPack(List<IDto> dataList)
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
				if(dto.getMainDto() instanceof TvPayoutmsgmainDto)
				{
					TvPayoutmsgmainDto pdto = (TvPayoutmsgmainDto)dto.getMainDto();
					if(tempMap.get(pdto.getSpayeracct())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put(pdto.getSpayeracct(), tempList);
					}else
					{
						tempList = tempMap.get(pdto.getSpayeracct());
						tempList.add(dto);
						tempMap.put(pdto.getSpayeracct(), tempList);
					}
				}else if(dto.getMainDto() instanceof TvPayoutbackmsgMainDto)
				{
					TvPayoutbackmsgMainDto bdto = (TvPayoutbackmsgMainDto)dto.getMainDto();
					if(tempMap.get(bdto.getSpayeracct())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put(bdto.getSpayeracct(), tempList);
					}else
					{
						tempList = tempMap.get(bdto.getSpayeracct());
						tempList.add(dto);
						tempMap.put(bdto.getSpayeracct(), tempList);
					}
				}else if(dto.getMainDto() instanceof TvVoucherinfoAllocateIncomeDto)
				{
					TvVoucherinfoAllocateIncomeDto adto = (TvVoucherinfoAllocateIncomeDto)dto.getMainDto();
					if(tempMap.get(adto.getSpayacctno())==null)
					{
						tempList = new ArrayList<MainAndSubDto>();
						tempList.add(dto);
						tempMap.put(adto.getSpayacctno(), tempList);
					}else
					{
						tempList = tempMap.get(adto.getSpayacctno());
						tempList.add(dto);
						tempMap.put(adto.getSpayacctno(), tempList);
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
		if(key==null||"null".equals(key)||"NULL".equals(key))
			key="";
		return key;
	}
	public static Map tranfor(List<TrTaxorgPayoutReportDto> list,TvVoucherinfoDto vDto) throws ITFEBizException {
		try{
			//取得国库名称
			String treName = BusinessFacade.findTreasuryInfo(vDto.getSorgcode()).get(vDto.getStrecode()) == null ? "" : BusinessFacade
					.findTreasuryInfo(vDto.getSorgcode()).get(
							vDto.getStrecode()).getStrename();
			HashMap<String, Object> returnmap = new HashMap<String, Object>();
			HashMap<String, Object> map = new HashMap<String, Object>();
			HashMap<String, Object> vouchermap = new HashMap<String, Object>();
			
			// 设置报文节点 Voucher
			map.put("Voucher", vouchermap);
			// 设置报文消息体 
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
				Detailmap.put("ExpFuncCode", dto.getSbudgetsubcode()); // 区划代码
				Detailmap.put("ExpFuncName", dto.getSbudgetsubname()); 
				Detailmap.put("MonthAmt", MtoCodeTrans.transformString(dto.getNmoneymonth())); 
				Detailmap.put("YearAmt",MtoCodeTrans.transformString(dto.getNmoneyyear()));
				Detailmap.put("Hold1", ""); 
				Detailmap.put("Hold2", ""); 
				Detailmap.put("Hold3", ""); 
				Detailmap.put("Hold4", ""); 
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
	private void setIdtoToMap(Map vouchermap,IDto idto)
	{
		if(vouchermap!=null&&idto!=null)
		{
			if(vouchermap.get("ClearBankCode")==null)
				vouchermap.put("ClearBankCode","");
			if(vouchermap.get("ClearBankName")==null)
				vouchermap.put("ClearBankName","");
			if(idto instanceof TvPayoutmsgmainDto)
			{
				TvPayoutmsgmainDto pdto = (TvPayoutmsgmainDto)idto;
				if(vouchermap.get("ClearBankCode")==null||"".equals(vouchermap.get("ClearBankCode")))
				{
					if(!"".equals(getString(pdto.getSclearbankcode())))
						vouchermap.put("ClearBankCode",pdto.getSclearbankcode());//人民银行编码
				}
				if(vouchermap.get("ClearBankName")==null||"".equals(vouchermap.get("ClearBankName")))
				{
					if(!"".equals(getString(pdto.getSclearbankname())))
						vouchermap.put("ClearBankName",pdto.getSclearbankname());//人民银行名称
				}
				if(vouchermap.get("ClearAccNo")==null||"".equals(vouchermap.get("ClearAccNo")))
				{
					if(!"".equals(getString(pdto.getSpayeracct())))
						vouchermap.put("ClearAccNo",pdto.getSpayeracct());//拨款账号
				}
				if(vouchermap.get("ClearAccNanme")==null||"".equals(vouchermap.get("ClearAccNanme")))
				{
					if(!"".equals(getString(pdto.getSpayername())))
						vouchermap.put("ClearAccNanme",pdto.getSpayername());//拨款账户名称
				}
			}else if(idto instanceof TvPayoutbackmsgMainDto)
			{
				TvPayoutbackmsgMainDto pdto = (TvPayoutbackmsgMainDto)idto;
				if(vouchermap.get("ClearAccNo")==null||"".equals(vouchermap.get("ClearAccNo")))
				{
					if(!"".equals(getString(pdto.getSpayeracct())))
						vouchermap.put("ClearAccNo",pdto.getSpayeracct());//拨款账号
				}
				if(vouchermap.get("ClearAccNanme")==null||"".equals(vouchermap.get("ClearAccNanme")))
				{
					if(!"".equals(getString(pdto.getSpayername())))
						vouchermap.put("ClearAccNanme",pdto.getSpayername());//拨款账户名称
				}
			}else if(idto instanceof TvVoucherinfoAllocateIncomeDto)
			{
				TvVoucherinfoAllocateIncomeDto pdto = (TvVoucherinfoAllocateIncomeDto)idto;
				if(vouchermap.get("ClearAccNo")==null||"".equals(vouchermap.get("ClearAccNo")))
				{
					if(!"".equals(getString(pdto.getSpayacctno())))
						vouchermap.put("ClearAccNo",pdto.getSpayacctno());//拨款账号
				}
				if(vouchermap.get("ClearAccNanme")==null||"".equals(vouchermap.get("ClearAccNanme")))
				{
					if(!"".equals(getString(pdto.getSpayacctname())))
						vouchermap.put("ClearAccNanme",pdto.getSpayacctname());//拨款账户名称
				}
			}
		}
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
			if(dto.getMainDto() instanceof TvPayoutmsgmainDto){
				if(map.get(((TvPayoutmsgmainDto)dto.getMainDto()).getSfundtypecode())==null){
					List<IDto> data = new ArrayList<IDto>();
					data.add(dto);
					map.put(((TvPayoutmsgmainDto)dto.getMainDto()).getSfundtypecode(), data);
				}else{
					List<IDto> data = map.get(((TvPayoutmsgmainDto)dto.getMainDto()).getSfundtypecode());
					data.add(dto);
				}
			}
//			else if(dto.getMainDto() instanceof TvPayoutbackmsgMainDto){
//				if(map.get(((TvPayoutbackmsgMainDto)dto.getMainDto()).getSfundtypecode())==null){
//					List<IDto> data = new ArrayList<IDto>();
//					data.add(dto);
//					map.put(((TvPayoutbackmsgMainDto)dto.getMainDto()).getSfundtypecode(), data);
//				}else{
//					List<IDto> data = map.get(((TvPayoutbackmsgMainDto)dto.getMainDto()).getSfundtypecode());
//					data.add(dto);
//				}
//			}else if(dto.getMainDto() instanceof TvVoucherinfoAllocateIncomeDto){
//				if(map.get(((TvVoucherinfoAllocateIncomeDto)dto.getMainDto()).getSfundtypecode())==null){
//					List<IDto> data = new ArrayList<IDto>();
//					data.add(dto);
//					map.put(((TvVoucherinfoAllocateIncomeDto)dto.getMainDto()).getSfundtypecode(), data);
//				}else{
//					List<IDto> data = map.get(((TvVoucherinfoAllocateIncomeDto)dto.getMainDto()).getSfundtypecode());
//					data.add(dto);
//				}
//			}
			
		}
		return map;
	}
}

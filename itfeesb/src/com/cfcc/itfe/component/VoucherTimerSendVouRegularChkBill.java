package com.cfcc.itfe.component;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TrStockdayrptDto;
import com.cfcc.itfe.persistence.dto.TsBillautosendDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;


/**
 * 功能：定时生成无纸化业务对账单
 * @author hejianrong
 * @time   2014-08-15
 *
 */
public class VoucherTimerSendVouRegularChkBill implements Callable{
	private static Log logger = LogFactory.getLog(VoucherTimerSendVouRegularChkBill.class);
	private String S_HOLD3 = TimeFacade.getCurrentStringTime();//对账起始日期：取系统当前日期
	private String S_HOLD4 = TimeFacade.getCurrentStringTime();//对账终止日期：取系统当前日期
		
	@SuppressWarnings({ "unchecked", "static-access" })
	public Object onCall(MuleEventContext eventContext)  {
		logger.debug("======================== 凭证库定时生成业务对账单任务开始========================");
		Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER);
		try {
			Map<String, List> map=SrvCacheFacade.cacheVoucherAuto(new TsVouchercommitautoDto().columnSstampauto());
			if(map!=null&&map.size()>0){
				List datalist = map.get("VOUCHER");
				if(datalist!=null&&datalist.size()>0)
				{
					String orgCode = null;
					String treCode = null;
					List<String> vous=null;
					String svtcode = null;
					TvVoucherinfoDto gendto = null;
					List<TvVoucherinfoDto> list3507 = new ArrayList<TvVoucherinfoDto>();
					List<TvVoucherinfoDto> list3508 = new ArrayList<TvVoucherinfoDto>();
					List<TvVoucherinfoDto> list3510 = new ArrayList<TvVoucherinfoDto>();
					Set<String> tmpTreSet = new HashSet<String>();
					for(int i=0;i<datalist.size();i++)
					{
						vous = (List<String>)datalist.get(i);
						orgCode = vous.get(0);
						treCode = vous.get(4);
						svtcode = vous.get(3);
						if(tmpTreSet.add(orgCode+treCode)&&"3507350835103515".contains(svtcode))
						{
							if(ITFECommonConstant.PUBLICPARAM.contains(",3507paybank=true,"))
							{
								Map<String,TsConvertbanktypeDto> mapTsconvertBankType = BusinessFacade.findTsconvertBankType(orgCode);
								TsConvertbanktypeDto tempdto = null;
								for(String mapkey:mapTsconvertBankType.keySet())
								{
									if(mapkey!=null&&mapkey.startsWith(treCode))
									{
										tempdto = mapTsconvertBankType.get(mapkey);
										gendto = new TvVoucherinfoDto();
										gendto.setSorgcode(orgCode);
										gendto.setStrecode(treCode);
										gendto.setSvtcode(MsgConstant.VOUCHER_NO_3507);
										gendto.setShold3(S_HOLD3);
										gendto.setShold4(S_HOLD4);
										gendto.setSpaybankcode(tempdto.getSbankcode());
										gendto.setScreatdate(TimeFacade.getCurrentStringTime());
										gendto.setSext1("0");//直接支付
										list3507.add(gendto);
										gendto = new TvVoucherinfoDto();
										gendto.setSorgcode(orgCode);
										gendto.setStrecode(treCode);
										gendto.setSvtcode(MsgConstant.VOUCHER_NO_3507);
										gendto.setShold3(S_HOLD3);
										gendto.setShold4(S_HOLD4);
										gendto.setSpaybankcode(tempdto.getSbankcode());
										gendto.setScreatdate(TimeFacade.getCurrentStringTime());
										gendto.setSext1("1");//授权支付
										list3507.add(gendto);
									}
								}
							}else
							{
								gendto = new TvVoucherinfoDto();
								gendto.setSorgcode(orgCode);
								gendto.setStrecode(treCode);
								gendto.setSvtcode(MsgConstant.VOUCHER_NO_3507);
								gendto.setShold3(S_HOLD3);
								gendto.setShold4(S_HOLD4);
								gendto.setScreatdate(TimeFacade.getCurrentStringTime());
								gendto.setSext1("0");//直接支付
								list3507.add(gendto);
								gendto = new TvVoucherinfoDto();
								gendto.setSorgcode(orgCode);
								gendto.setStrecode(treCode);
								gendto.setSvtcode(MsgConstant.VOUCHER_NO_3507);
								gendto.setShold3(S_HOLD3);
								gendto.setShold4(S_HOLD4);
								gendto.setScreatdate(TimeFacade.getCurrentStringTime());
								gendto.setSext1("1");//授权支付
								list3507.add(gendto);
							}
							if(ITFECommonConstant.PUBLICPARAM.contains(",autobuss=3508,"))
							{
								gendto = new TvVoucherinfoDto();
								gendto.setSorgcode(orgCode);
								gendto.setStrecode(treCode);
								gendto.setSvtcode(MsgConstant.VOUCHER_NO_3508);
								gendto.setSext3("011");
								gendto.setShold3(S_HOLD3);
								gendto.setShold4(S_HOLD4);
								gendto.setScreatdate(TimeFacade.getCurrentStringTime());
								list3508.add(gendto);
							}
							if(ITFECommonConstant.PUBLICPARAM.contains(",autobuss=3510,"))
							{
								gendto = new TvVoucherinfoDto();
								gendto.setSorgcode(orgCode);
								gendto.setStrecode(treCode);
								gendto.setSvtcode(MsgConstant.VOUCHER_NO_3510);
								gendto.setShold3(S_HOLD3);
								gendto.setShold4(S_HOLD4);
								gendto.setSext1("0");//直接支付
								gendto.setScreatdate(TimeFacade.getCurrentStringTime());
								list3510.add(gendto);
								
								gendto = new TvVoucherinfoDto();
								gendto.setSorgcode(orgCode);
								gendto.setStrecode(treCode);
								gendto.setSvtcode(MsgConstant.VOUCHER_NO_3510);
								gendto.setShold3(S_HOLD3);
								gendto.setShold4(S_HOLD4);
								gendto.setSext1("1");//授权支付
								gendto.setScreatdate(TimeFacade.getCurrentStringTime());
								list3510.add(gendto);
							}
						}
					}
					if(list3507!=null&&list3507.size()>0)
					{
						try
						{
							voucher.voucherGenerate(list3507);
						}catch(Exception e){
							logger.error(e);
							VoucherException.saveErrInfo(null, e);
						}
					}
					if(list3508!=null&&list3508.size()>0)
					{
						try
						{
							voucher.voucherGenerate(list3508);
						}catch(Exception e){
							logger.error(e);
							VoucherException.saveErrInfo(null, e);
						}
					}
					if(list3510!=null&&list3510.size()>0)
					{
						try
						{
							voucher.voucherGenerate(list3510);
						}catch(Exception e){
							logger.error(e);
							VoucherException.saveErrInfo(null, e);
						}
					}
				}
			}
			if(ITFECommonConstant.PUBLICPARAM.contains(",autoreport=true,"))
			{
				TrStockdayrptDto kcdto = new TrStockdayrptDto();
				String getdate = null;
				Calendar calendar = null;
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 时间格式
				List datalist = null;
				for(int i=1;i<15;i++)
				{
					calendar = Calendar.getInstance();
					calendar.setTime(TimeFacade.getCurrentDate());
					calendar.add(Calendar.DATE, -i);
					getdate = dateFormat.format(calendar.getTime());
					kcdto.setSaccdate(getdate);
					kcdto.setSrptdate(getdate);
					datalist = CommonFacade.getODB().findRsByDto(kcdto);
					if(datalist!=null&&datalist.size()>0)
						break;
				}
				TsBillautosendDto _dto = new TsBillautosendDto();
				_dto.setSisauto(StateConstant.COMMON_NO);
				List<TsBillautosendDto> list = CommonFacade.getODB().findRsByDto(_dto);
				TvVoucherinfoDto vDto = null;
				List<TvVoucherinfoDto> report3511 = new ArrayList<TvVoucherinfoDto>();
				List<TvVoucherinfoDto> report3513 = new ArrayList<TvVoucherinfoDto>();
				Set<String> tmpTreSet = new HashSet<String>();
				for (TsBillautosendDto tmp : list) {
					vDto = new TvVoucherinfoDto();
					vDto.setSorgcode(tmp.getSorgcode());//机构代码
					vDto.setSvtcode(MsgConstant.VOUCHER_NO_3511);//类型
					vDto.setStrecode(tmp.getStrecode());//国库代码
					vDto.setScheckdate(getdate);//报表起始日期
					vDto.setSpaybankcode(getdate);//报表截止日期
					vDto.setShold3(tmp.getSbelongflag());//辖属标志
					vDto.setSattach(tmp.getStaxorgcode());//征收机关
					vDto.setShold1(tmp.getSbudgettype());//预算种类
					vDto.setScheckvouchertype(tmp.getSbillkind());//报表种类
					vDto.setShold2(tmp.getSbudgetlevelcode());//预算级次
					vDto.setScreatdate(TimeFacade.getCurrentStringTime());
					report3511.add(vDto);
					if(tmpTreSet.add(vDto.getSorgcode()+vDto.getStrecode()))
					{
						vDto = new TvVoucherinfoDto();
						vDto.setSorgcode(tmp.getSorgcode());//机构代码
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_3513);//类型
						vDto.setStrecode(tmp.getStrecode());//国库代码
						vDto.setScheckdate(getdate);//报表起始日期
						vDto.setSpaybankcode(getdate);//报表截止日期
						vDto.setScreatdate(TimeFacade.getCurrentStringTime());
						report3513.add(vDto);
					}
				}
				if(report3511!=null&&report3511.size()>0)
				{
					try
					{
						voucher.voucherGenerate(report3511);
					}catch(Exception e){
						logger.error(e);
						VoucherException.saveErrInfo(null, e);
					}
					voucher.voucherGenerate(report3513);
				}
			}
		}catch(Exception e){
			logger.error(e);
			VoucherException.saveErrInfo(null, e);
		}		
		logger.debug("======================== 凭证库定时生成业务对账单任务关闭 ========================");
		return null;	
	}
		
}

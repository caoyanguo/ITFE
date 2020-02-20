package com.cfcc.itfe.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TsUsersPK;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 功能：无纸化凭证自动签章任务
 * @author 何健荣
 * @time  2014-03-07
 */
public class VoucherTimerStamp implements Callable{
	private static Log logger = LogFactory.getLog(VoucherTimerStamp.class);
	private TsUsersDto userDto=new TsUsersDto();
	
	public Object onCall(MuleEventContext eventContext)  {
		logger.debug("======================== 凭证库定时签章任务开启 ========================");
		Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER);
		try {
			Map<String, List> map = SrvCacheFacade.cacheVoucherAuto(new TsVouchercommitautoDto().columnSstampauto());
			if(map.size()>0){
				List lists=new VoucherTimerSendTips().findVoucherDto(map.get("VOUCHER"),DealCodeConstants.VOUCHER_SUCCESS_NO_BACK,false);
				if(lists!=null&&lists.size()>0){
					for(List list:(List<List>)lists){
						List stampPositionList=findStamppositionDto(((List<TvVoucherinfoDto>)list).get(0));						
						if(stampPositionList==null||stampPositionList.size()==0)
							continue;
						List strecodeList=findTsTreasuryDto(((List<TvVoucherinfoDto>)list).get(0));
						List newList=list.size()>=100?list.subList(0, 100):list;
						for(TvVoucherinfoDto dto:(List<TvVoucherinfoDto>)newList){
							try{
								voucherStamp(voucher,stampPositionList.size(),findStampTypeDtoList(sort(stampPositionList), dto), strecodeList, dto);
							}catch(Exception e)
							{
								continue;
							}
						}
					}				
				}
			}
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",payoutstampmode=true,")>=0)
			{
				map = SrvCacheFacade.cacheVoucherAuto(new TsVouchercommitautoDto().columnSstampauto());
				if(map.size()>0){
					List lists=new VoucherTimerSendTips().findVoucherDto(map.get("VOUCHER"),DealCodeConstants.VOUCHER_FAIL_TCBS,false);
					if(lists!=null&&lists.size()>0){
						for(List list:(List<List>)lists){
							List stampPositionList=findStamppositionDto(((List<TvVoucherinfoDto>)list).get(0));						
							if(stampPositionList==null||stampPositionList.size()==0)
								continue;
							List strecodeList=findTsTreasuryDto(((List<TvVoucherinfoDto>)list).get(0));
							List newList=list.size()>=100?list.subList(0, 100):list;
							for(TvVoucherinfoDto dto:(List<TvVoucherinfoDto>)newList){
								try{
									voucherStamp(voucher,stampPositionList.size(),findStampTypeDtoListfail(sort(stampPositionList), dto), strecodeList, dto);
								}catch(Exception e)
								{
									continue;
								}
							}
						}				
					}
				}
			}
		}catch(Exception e){
			logger.error(e);
			VoucherException.saveErrInfo(null, e);
		}		
		logger.debug("======================== 凭证库定时签章任务关闭 ========================");
		return null;	
	}
	
	/**
	 * 根据组织机构代码、区划代码、凭证类型、状态查找签章位置参数
	 * @param voucherDto
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	private List findStamppositionDto(TvVoucherinfoDto voucherDto) throws JAFDatabaseException, ValidateException{
		TsStamppositionDto dto=new TsStamppositionDto();
		dto.setSorgcode(voucherDto.getSorgcode());
		dto.setSadmdivcode(voucherDto.getSadmdivcode());
		dto.setSvtcode(voucherDto.getSvtcode());
		return CommonFacade.getODB().findRsByDto(dto);		
	}
	
	/**
	 * 根据组织机构代码、国库代码查找国库
	 * @param voucherDto
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 */
	public List findTsTreasuryDto(TvVoucherinfoDto voucherDto) throws JAFDatabaseException, ValidateException{
		TsTreasuryDto dto=new TsTreasuryDto();
		dto.setSorgcode(voucherDto.getSorgcode());
		dto.setStrecode(voucherDto.getStrecode());
		return CommonFacade.getODB().findRsByDto(dto);		
	}
	
	/**
	 * 查找未签章的签章类型
	 * @param list
	 * @param dto
	 * @return
	 */
	private List findStampTypeDtoList(List list,TvVoucherinfoDto dto){		
		List stampList=new ArrayList();
		stampList.addAll(list);
		if(!StringUtils.isBlank(dto.getSstampid())){
			String[] stampids=dto.getSstampid().split(",");
			HashSet<TsStamppositionDto> set=new HashSet<TsStamppositionDto>();
			for(int i=0;i<stampids.length;i++){
				for(TsStamppositionDto stampDto:(List<TsStamppositionDto>)stampList){
					if(stampDto.getSstamptype().equals(stampids[i]))
						set.add(stampDto);
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",payoutstampmode=true,")>=0&&stampDto.getSstamptype().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)
						set.add(stampDto);
				}		
			}stampList.removeAll(set);			
		}else
		{
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",payoutstampmode=true,")>=0)
			{
				HashSet<TsStamppositionDto> set=new HashSet<TsStamppositionDto>();
				for(TsStamppositionDto stampDto:(List<TsStamppositionDto>)stampList)
				{
					if(stampDto.getSstamptype().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)
						set.add(stampDto);
				}
				if(set!=null&&set.size()>0)
					stampList.removeAll(set);
			}
		}
		return stampList;	
	}
	/**
	 * 查找未签章的签章类型
	 * @param list
	 * @param dto
	 * @return
	 */
	private List findStampTypeDtoListfail(List list,TvVoucherinfoDto dto){		
		List stampList=new ArrayList();
		stampList.addAll(list);
		if(!StringUtils.isBlank(dto.getSstampid())){
			String[] stampids=dto.getSstampid().split(",");
			HashSet<TsStamppositionDto> set=new HashSet<TsStamppositionDto>();
			for(int i=0;i<stampids.length;i++){
				for(TsStamppositionDto stampDto:(List<TsStamppositionDto>)stampList){
					if(stampDto.getSstamptype().equals(stampids[i]))
						set.add(stampDto);
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",payoutstampmode=true,")>=0&&stampDto.getSstamptype().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)<0)
						set.add(stampDto);
				}		
			}stampList.removeAll(set);			
		}else
		{
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",payoutstampmode=true,")>=0)
			{
				HashSet<TsStamppositionDto> set=new HashSet<TsStamppositionDto>();
				for(TsStamppositionDto stampDto:(List<TsStamppositionDto>)stampList)
				{
					if(stampDto.getSstamptype().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)<0)
						set.add(stampDto);
				}
				if(set!=null&&set.size()>0)
					stampList.removeAll(set);
			}
		}
		return stampList;	
	}						
	/**
	 * 根据签章顺序大小对List集合排序
	 * @param list
	 * @return
	 */
	public List sort(List list) {		
		Collections.sort(list, new Comparator<TsStamppositionDto>(){
			public int compare(TsStamppositionDto dto1,TsStamppositionDto dto2){
				return Integer.parseInt(dto1.getSstampsequence())-Integer.parseInt(dto2.getSstampsequence());
			}				
		});return list;	
	}
		
	/**
	 * 凭证签章 
	 * @param voucher
	 * @param size
	 * @param stampPositionList
	 * @param strecodeList
	 * @param dto
	 */
	public void voucherStamp(Voucher voucher,int size,List stampPositionList,List strecodeList,TvVoucherinfoDto dto){
		if(stampPositionList==null||stampPositionList.size()==0)
			return;
		int sequence=0;
		boolean flag=false;
		if(ITFECommonConstant.PUBLICPARAM.indexOf(",payoutstampmode=true,")>=0)
			size = stampPositionList.size();
		for(int i=0;i<stampPositionList.size();i++){
			TsStamppositionDto stampDto=(TsStamppositionDto)stampPositionList.get(i);
			if(flag&&sequence<Integer.parseInt(stampDto.getSstampsequence()))
				continue;			
			if(verifyStamp(stampDto, strecodeList)){
				sequence=Integer.parseInt(stampDto.getSstampsequence());
				flag=true;
				continue;				
			}voucherStamp(voucher, size, stampDto, (TsTreasuryDto) strecodeList.get(0), dto);
		}	
	}
	
	/**
	 * 校验国库参数、签章ID、证书ID、默认签章用户
	 * @param stampDto
	 * @param strecodeList
	 * @return
	 */
	private boolean verifyStamp(TsStamppositionDto stampDto,List strecodeList){
		boolean flag=true;
		if(stampDto.getSstamptype().equals(MsgConstant.VOUCHERSAMP_ROTARY)||
				stampDto.getSstamptype().equals(MsgConstant.VOUCHERSAMP_OFFICIAL)||stampDto.getSstamptype().equals(MsgConstant.VOUCHERSAMP_ATTACH)
				||stampDto.getSstamptype().equals(MsgConstant.VOUCHERSAMP_BUSS)){
			if(strecodeList==null||strecodeList.size()==0)
				VoucherException.saveErrInfo(stampDto.getSvtcode(), "组织机构代码"+stampDto.getSorgcode()+
						" 国库主体代码"+stampDto.getStrecode()+"在[国库主体信息参数维护]中不存在！");				
			else if((stampDto.getSstamptype().equals(MsgConstant.VOUCHERSAMP_ROTARY)&&
					StringUtils.isBlank(((TsTreasuryDto) strecodeList.get(0)).getSrotaryid()))||
						(stampDto.getSstamptype().equals(MsgConstant.VOUCHERSAMP_OFFICIAL)&&
							StringUtils.isBlank(((TsTreasuryDto) strecodeList.get(0)).getSstampid()))||
							(stampDto.getSstamptype().equals(MsgConstant.VOUCHERSAMP_ATTACH)&&
									StringUtils.isBlank(((TsTreasuryDto) strecodeList.get(0)).getSattachid()))
							&&(stampDto.getSstamptype().equals(MsgConstant.VOUCHERSAMP_BUSS)&&
									StringUtils.isBlank(((TsTreasuryDto) strecodeList.get(0)).getSattachcertid()))
							)
					VoucherException.saveErrInfo(stampDto.getSvtcode(), "国库主体代码"+stampDto.getStrecode()+
							"在[国库主体信息参数维护]中 "+stampDto.getSstampname()+" 签章ID参数未维护！ ");
			else{
				userDto = new TsUsersDto();
				userDto.setSorgcode(stampDto.getSorgcode());
				userDto.setSusercode(StringUtils.isBlank(stampDto.getSstampuser())?"00001":stampDto.getSstampuser());
				return false;
			}			
		}else{
			if(StringUtils.isBlank(stampDto.getSstampuser()))
				logger.error("私章："+stampDto.getSstampname()+" 对应的默认签章用户在[签章位置参数维护]中未维护！ ");
			else{
				userDto = new TsUsersDto();
				userDto.setSusercode(stampDto.getSstampuser());
				userDto.setSorgcode(stampDto.getSorgcode());
				try {
					List<TsUsersDto> usersDtoList=CommonFacade.getODB().findRsByDto(userDto);
					if(usersDtoList==null||usersDtoList.size()==0)
						VoucherException.saveErrInfo(stampDto.getSvtcode(),"私章："+stampDto.getSstampname()+" 对应的默认签章用户"+
								" 用户代码："+userDto.getSusercode()+" 在[用户信息维护]中不存在！");
					else{
						userDto=usersDtoList.get(0);
						if(StringUtils.isBlank(userDto.getScertid()))
							VoucherException.saveErrInfo(stampDto.getSvtcode(),"私章："+stampDto.getSstampname()+" 对应的默认签章用户"+
								" 用户姓名："+userDto.getSusername()+" 用户代码："+userDto.getSusercode()+"在[用户信息维护]中的证书ID参数未维护！");
						else if(StringUtils.isBlank(userDto.getSstampid()))
							VoucherException.saveErrInfo(stampDto.getSvtcode(),"私章："+stampDto.getSstampname()+" 对应的默认签章用户"+
								" 用户姓名："+userDto.getSusername()+" 用户代码："+userDto.getSusercode()+"在[用户信息维护]中的签章ID参数未维护！");	
						else
							return false;
					}					
				} catch (Exception e) {
					logger.error(e);
					VoucherException.saveErrInfo(stampDto.getSvtcode(),e.getMessage());
				}
			}			
		}return flag;
	}
	
	/**
	 * 凭证签章
	 * @param voucher
	 * @param size
	 * @param stampDto
	 * @param strecodeDto
	 * @param dto
	 */
	private void voucherStamp(Voucher voucher,int size,TsStamppositionDto stampDto,TsTreasuryDto strecodeDto,TvVoucherinfoDto dto){		
		List<List> lists=new ArrayList<List>();
		List list = new ArrayList();
		List vList=new ArrayList();
		List sinList=new ArrayList();
		vList.add(sinList);
		sinList.add(dto);
		try {
			list.add(userDto);
			list.add(strecodeDto);
			list.add(stampDto);
			list.add(size);
			list.add(vList);
			lists.add(list);
			voucher.voucherStamp(lists);
		} catch (ITFEBizException e) {
			logger.error(e);
			VoucherException.saveErrInfo(stampDto.getSvtcode(),e.getMessage());
		}
	}
}

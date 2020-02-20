package com.cfcc.itfe.voucher.sxservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbankSxDto;
import com.cfcc.itfe.persistence.dto.TvPayreckbankbackSxDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.dto.VoucherVerifyDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * 凭证校验
 * @author sunyan
 *
 */
public class VoucherVerifySX {
	private static Log logger = LogFactory.getLog(VoucherVerifySX.class);
	
	private String tmpFailReason;//校验失败原因
	public VoucherVerifySX(){
		tmpFailReason="";
	}
	
	
	/**
	 * 凭证校验
	 * @param Lists 凭证集合
	 * @param vtcode 凭证类型
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public  int verify(List lists,String vtCode) throws ITFEBizException{
		//返回校验成功的凭证List
		List<TvVoucherinfoSxDto> successList = new ArrayList<TvVoucherinfoSxDto>();
		int count=0;
		try{
			if(vtCode.equals(MsgConstant.VOUCHER_NO_5207)){
				count = verifyFor5207(lists,vtCode);
			}else if(vtCode.equals(MsgConstant.VOUCHER_NO_2301)){
				successList = verifyFor2301(lists,vtCode);
				count = successList.size();
			}else if(vtCode.equals(MsgConstant.VOUCHER_NO_2302)){
				successList = verifyFor2302(lists,vtCode);
				count = successList.size();
			}else if(vtCode.equals(MsgConstant.VOUCHER_NO_5108)){
				successList = verifyFor5108(lists,vtCode);
				count = successList.size();
			}else if(vtCode.equals(MsgConstant.VOUCHER_NO_5106)){
				successList = verifyFor5106(lists,vtCode);
				count = successList.size();
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("查看业务主表信息或更新凭证状态异常！",e);
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException("凭证校验操作异常！",e);
		}
		return count;
	}
	/**
	 * 申请退款凭证回单
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws ITFEBizException 
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 * @throws ITFEBizException 
	 */
	public List<TvVoucherinfoSxDto> verifyFor2302(List lists, String vtCode) throws JAFDatabaseException, ValidateException, ITFEBizException,Exception {
		List<TvVoucherinfoSxDto> returnList = new ArrayList<TvVoucherinfoSxDto>();
		for(List list:(List<List>)lists){
			SQLExecutor sqlExecutor = null;
			TvPayreckbankbackSxDto mainDto = (TvPayreckbankbackSxDto) list.get(0);
			TvVoucherinfoSxDto vDto=(TvVoucherinfoSxDto) list.get(1);
			//预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			//预算单位代码list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
				//校验
			if(verifyTreasury(mainDto.getStrecode(), mainDto.getSbookorgcode())
					&& verifySfinOrgCode(mainDto.getSfinorgcode(),mainDto.getStrecode())
					&& verifySubject(mainDto.getSbookorgcode(), expFuncCodeList, vtCode)
					&& verifyCorpcodeList(mainDto.getSbookorgcode(),tdcorpList)
					&& verifySpayInfo(mainDto)//校验收付款人信息验证 根据 收款人开户行行号 找收付款人信息
					&& verifyPaybankName(mainDto.getSbookorgcode(),mainDto.getStrecode(), mainDto.getSagentbnkcode(),mainDto.getSpaybankname())//校验代理银行行名
				){
					returnList.add(vDto);
					//更新凭证状态
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), null, true);
				}else{
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), this.tmpFailReason, false);
					
				}
		}
		return returnList;
	}
	/**
	 * 申请划款凭证回单
	 * @param lists
	 * @param vtCode
	 * @return
	 * @throws ITFEBizException 
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 */
	public List<TvVoucherinfoSxDto> verifyFor2301(List lists, String vtCode) throws JAFDatabaseException, ValidateException, ITFEBizException ,Exception{
		List<TvVoucherinfoSxDto> returnList = new ArrayList<TvVoucherinfoSxDto>();
		
		for(List list:(List<List>)lists){
			TvPayreckbankSxDto mainDto = (TvPayreckbankSxDto) list.get(0);
			TvVoucherinfoSxDto vDto=(TvVoucherinfoSxDto) list.get(1);
			
			//预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			//预算单位代码list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
				//校验
				if(verifyTreasury(mainDto.getStrecode(), mainDto.getSbookorgcode())
						&& verifySfinOrgCode(mainDto.getSfinorgcode(),mainDto.getStrecode())
						&& verifySubject(mainDto.getSbookorgcode(), expFuncCodeList, vtCode)
						&& verifyCorpcodeList(mainDto.getSbookorgcode(),tdcorpList)
						&& verifySpayInfo(mainDto)//校验收付款人信息验证 根据 收款人开户行行号 找收付款人信息
						&& verifyPaybankName(mainDto.getSbookorgcode(),mainDto.getStrecode(), mainDto.getSpayeeopbkno(),mainDto.getSpaybankname())//校验代理银行行名
				){
					returnList.add(vDto);
					//更新凭证状态
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), null, true);
				}else{
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), this.tmpFailReason, false);
				}
			
		}
		return returnList;
	}

	/**
	 * 校验收付款人信息验证 根据 收款人开户行行号 找收付款人信息
	 * @param mainDto
	 * @return 
	 * @throws ITFEBizException 
	 */
	private boolean verifySpayInfo(IDto dto) throws ITFEBizException {
		if (dto instanceof TvPayreckbankSxDto ) {
			TvPayreckbankSxDto mainDto=(TvPayreckbankSxDto) dto;
			// 用于单一账户的收付款人的校验
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(mainDto.getSbookorgcode()); // 获取收付款人信息
			if (StringUtils.isBlank(mainDto.getSpayeeopbkno())) {
				this.tmpFailReason = "报文不规范, 没有找到收款人开户行行号信息!";
				return false;
			}else {
				TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap.get((mainDto.getStrecode()+mainDto.getSpayeeopbkno()+mainDto.getSpayeracct()+mainDto.getSpayeeacct()).trim());
				if (null == tmppayacctinfoDto) {
					this.tmpFailReason="根据国库代码："+mainDto.getStrecode()+", 收款人开户行行号："+ mainDto.getSpayeeopbkno()+", 付款人账号："+mainDto.getSpayeracct()+", 收款人账号："+mainDto.getSpayeeacct()+ " 没有找到收付款人信息!";
					return false;
				}else{
					if(!mainDto.getSpayeracct().equals(tmppayacctinfoDto.getSpayeracct())){	// 付款人帐户
						this.tmpFailReason="付款人帐户:"  + mainDto.getSpayeracct() + "与维护的收付款人信息中不一致!";
						return false;
					}
					if(!mainDto.getSpayername().equals(tmppayacctinfoDto.getSpayername())){	// 付款人名称
						this.tmpFailReason="付款人名称:"  + mainDto.getSpayername() + "与维护的收付款人信息中不一致!";
						return false;
					}
					if(!mainDto.getSpayeeacct().equals(tmppayacctinfoDto.getSpayeeacct())){	// 收款人账户
						this.tmpFailReason="收款人账户:"  + mainDto.getSpayeeacct() + "与维护的收付款人信息中不一致!";
						return false;
					}
					if(!mainDto.getSpayeename().equals(tmppayacctinfoDto.getSpayeename())){	// 收款人名称
						this.tmpFailReason="收款人名称:"  + mainDto.getSpayeename() + "与维护的收付款人信息中不一致!";
						return false;
					}
				}
			}
		}else if ( dto instanceof  TvPayreckbankbackSxDto) {
			TvPayreckbankbackSxDto mainDto=(TvPayreckbankbackSxDto) dto;
			// 用于单一账户的收付款人的校验
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(mainDto.getSbookorgcode()); // 获取收付款人信息
			if (StringUtils.isBlank(mainDto.getSpaysndbnkno())) {
				this.tmpFailReason = "报文不规范, 没有找到收款人开户行行号信息";
				return false;
			}else {
				TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap.get((mainDto.getStrecode()+mainDto.getSpaysndbnkno()+mainDto.getSpayeracct()+mainDto.getSpayeeacct()).trim());
				if (null == tmppayacctinfoDto) {
					this.tmpFailReason="根据国库代码："+mainDto.getStrecode()+", 收款人开户行行号："+ mainDto.getSpaysndbnkno()+", 付款人账号："+mainDto.getSpayeracct()+", 收款人账号："+mainDto.getSpayeeacct()+ " 没有找到收付款人信息!";
					return false;
				}else{
					if(!mainDto.getSpayeracct().equals(tmppayacctinfoDto.getSpayeracct())){	// 付款人帐户
						this.tmpFailReason="付款人帐户:"  + mainDto.getSpayeracct() + "与维护的收付款人信息中不一致!";
						return false;
					}
					if(!mainDto.getSpayername().equals(tmppayacctinfoDto.getSpayername())){	// 付款人名称
						this.tmpFailReason="付款人名称:"  + mainDto.getSpayername() + "与维护的收付款人信息中不一致!";
						return false;
					}
					if(!mainDto.getSpayeeacct().equals(tmppayacctinfoDto.getSpayeeacct())){	// 收款人账户
						this.tmpFailReason="收款人账户:"  + mainDto.getSpayeeacct() + "与维护的收付款人信息中不一致!";
						return false;
					}
					if(!mainDto.getSpayeename().equals(tmppayacctinfoDto.getSpayeename())){	// 收款人名称
						this.tmpFailReason="收款人名称:"  + mainDto.getSpayeename() + "与维护的收付款人信息中不一致!";
						return false;
					}
				}
			}
		}
		return true;
	}


	/**
	 * 实拨资金校验类
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException 
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	public  int verifyFor5207(List lists,String vtCode)throws JAFDatabaseException, ValidateException, ITFEBizException ,Exception{
		int count=0;
		for(List list:(List<List>)lists){			
			TvPayoutmsgmainSxDto mainDto = (TvPayoutmsgmainSxDto) list.get(0);
			TvVoucherinfoSxDto vDto=(TvVoucherinfoSxDto) list.get(1);
			//预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
				//校验
				if(verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())
						&& verifyAccName(mainDto.getSorgcode(), mainDto.getStrecode(), mainDto.getSpayeracct())
						&& verifySubject(mainDto.getSorgcode(), expFuncCodeList, vtCode)
						&& verifyCorpcodeList(mainDto.getSorgcode(), mainDto.getStrecode(), mainDto.getSbudgetunitcode(), vtCode)
					&& verifyPayeeBankNo(mainDto.getSrecbankno(), vtCode)
					){

					//更新凭证状态
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), null, true);
					count++;
				}else{
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), this.tmpFailReason, false);
					
				}
			
		}
		return count;
	}
	/**
	 * 直接支付额度业务校验类
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException 
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	public  List<TvVoucherinfoSxDto> verifyFor5108(List lists,String vtCode)throws JAFDatabaseException, ValidateException, ITFEBizException ,Exception{
		List<TvVoucherinfoSxDto> returnList = new ArrayList<TvVoucherinfoSxDto>();
		for(List list:(List<List>)lists){
			TvDirectpaymsgmainSxDto mainDto = (TvDirectpaymsgmainSxDto) list.get(0);
			TvVoucherinfoSxDto vDto=(TvVoucherinfoSxDto) list.get(1);
			//预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
				//校验
				if(verifyTreasury(mainDto.getStrecode(),mainDto.getSorgcode())
						&&verifyCorpcodeList(mainDto.getSorgcode(),tdcorpList)
						&&verifySubject(mainDto.getSorgcode(),expFuncCodeList, vtCode)
						&&verifyPayeeBankNo(mainDto.getStransactunit(), vtCode)//代理银行校验
				){
					//更新凭证状态
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), null, true);
					returnList.add(vDto);
				}else{
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), this.tmpFailReason, false);
					
				}
			
		}
		return returnList;
	}
	/**
	 * 授权支付额度业务校验类
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException 
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	public  List<TvVoucherinfoSxDto> verifyFor5106(List lists,String vtCode)throws JAFDatabaseException, ValidateException, ITFEBizException ,Exception{
		List<TvVoucherinfoSxDto> returnList = new ArrayList<TvVoucherinfoSxDto>();
		for(List list:(List<List>)lists){
			TvGrantpaymsgmainSxDto mainDto = (TvGrantpaymsgmainSxDto) list.get(0);
			TvVoucherinfoSxDto vDto=(TvVoucherinfoSxDto) list.get(1);
			//预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
				//校验
				if(verifyTreasury(mainDto.getStrecode(),mainDto.getSorgcode())
						&&verifyCorpcodeList(mainDto.getSorgcode(),tdcorpList)
						&&verifySubject(mainDto.getSorgcode(),expFuncCodeList, vtCode)
						&&verifyPayeeBankNo(mainDto.getStransactunit(), vtCode)//代理银行校验
				){
					//更新凭证状态
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), null, true);
					returnList.add(vDto);
				}else{
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), this.tmpFailReason, false);
					
				}
		}
		return returnList;
	}

	/**
	 * 收入退付校验类
	 * @param lists
	 * @param AdmDivCode
	 * @throws ITFEBizException 
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 * @throws ITFEBizException
	 * @throws Exception
	 */
	public  int verifyFor5209(List lists,String vtCode)throws JAFDatabaseException, ValidateException, ITFEBizException ,Exception{
		int count=0;
		for(List list:(List<List>)lists){			
			TvDwbkDto mainDto = (TvDwbkDto) list.get(0);
			TvVoucherinfoDto vDto=(TvVoucherinfoDto) list.get(1);
			//预算科目代码list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
		
				//校验
				if(verifyTreasury(mainDto.getSaimtrecode(), mainDto.getSbookorgcode())
						&& verifySfinOrgCode(mainDto.getStaxorgcode(), mainDto.getSbookorgcode())
						&& verifySubject(mainDto.getSbookorgcode(), expFuncCodeList, vtCode)						
						&& verifyPayeeBankNo(mainDto.getSpayeeopnbnkno(), vtCode)
						&& verifyStrelevel(mainDto)
					){

					//更新凭证状态
					VoucherUtil.voucherVerifyUpdateStatus(vDto,mainDto.tableName(), null, true);
					count++;
				}else{
					VoucherUtil.voucherVerifyUpdateStatus(vDto,mainDto.tableName(), this.tmpFailReason, false);
					
				}
			
		}
		return count;
	}
	
	/**
	 * 提供对所属国库进行验证
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyTreasury(String trecode, String orgcode)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		HashMap<String, TsTreasuryDto> map = SrvCacheFacade.cacheTreasuryInfo(orgcode);
		if (map!=null&&map.containsKey(trecode)) {
			return true;
		} else {
			this.tmpFailReason="国库主体代码"+trecode+"在国库主体信息参数中不存在!";
			return false;
		}
	}
	
	/**
	 * 校验国库预算级次	
	 * @return
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 * @throws ITFEBizException 
	 */
	public boolean verifyStrelevel(TvDwbkDto dto) throws JAFDatabaseException, ValidateException, ITFEBizException{
		HashMap<String, TsTreasuryDto> map = SrvCacheFacade.cacheTreasuryInfo(dto.getSbookorgcode());
			TsTreasuryDto tDto=map.get(dto.getSaimtrecode());
			if(tDto.getStrelevel()==null||tDto.getStrelevel().equals("")){
				this.tmpFailReason="国库："+tDto.getStrecode()+"预算级次未维护！";
				return false;
			}
			dto.setCbdglevel(tDto.getStrelevel());
			DatabaseFacade.getODB().update(dto);
			return true;
		
	}
	
	/**
	 * 校验功能科目代码
	 * @param orgcode   机构代码
	 * @param funccode  功能科目列表
	 * @param vtCode    业务类型
	 * 
	 * @return  ture/false
	 * @throws ITFEBizException
	 * @throws JAFDatabaseException 
	 */
	public boolean verifySubject(String orgcode, List<String> funccodeList, String vtcode) throws ITFEBizException, JAFDatabaseException {
		Map<String, TsBudgetsubjectDto> smap=SrvCacheFacade.cacheTsBdgsbtInfo(orgcode);
		for(String funccode:funccodeList){
			TsBudgetsubjectDto dto = smap.get(funccode);
			if (null == funccode || "".equals(funccode)) {
				this.tmpFailReason="明细信息中存在功能科目代码为空的记录!";
				return false;
			} 
			if (null == dto || "".equals(dto.getSsubjectcode())) {
				this.tmpFailReason="功能科目代码"+funccode+"不存在!";
				return false;
			}else{
				if(vtcode.equals(MsgConstant.VOUCHER_NO_5209)){
					if (!"1".equals(dto.getSsubjectclass())) {
						this.tmpFailReason="功能科目代码"+funccode+"不是收入科目!";
						return false;
					}
				}else{
					if (!"2".equals(dto.getSsubjectclass())) {
						this.tmpFailReason="功能科目代码"+funccode+"不是支出功能科目!";
						return false;
					}
				}
				if (!"1".equals(dto.getSwriteflag())) {
					this.tmpFailReason="功能科目代码"+funccode+"的录入标志为不可录入!";
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 进行预算单位代码列表校验
	 * @param orgcode    机构代码
	 * @param tdCorpList 预算单位代码列表
	 * @param vtCode     业务类型
	 * 
	 * @return true/false
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyCorpcodeList(String orgcode, String trecode, String tdCorp,String vtCode)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		Map<String, TdCorpDto> map = SrvCacheFacade.cacheTdCorpInfo(orgcode);
		if (map==null) {
			this.tmpFailReason="机构"+orgcode+"没有维护预算单位代码参数!";
			return false;
		}
		if("".equals(tdCorp) || tdCorp == null){
			this.tmpFailReason="预算单位代码为空!";
			return false;
		}
		if(vtCode.equals(MsgConstant.VOUCHER_NO_5207)){
			TdCorpDto cDto=map.get(trecode+tdCorp);
			if(cDto==null||!cDto.getScorpcode().equals(tdCorp)){
					this.tmpFailReason="预算单位代码"+tdCorp+"在预算单位代码参数中不存在!";
					return false;
				}else{
					if(!"1".equals(cDto.getCmayaprtfund().trim())){
						this.tmpFailReason="预算单位代码"+tdCorp+"不能进行实拨资金!";
						return false;
					}
				}
			
		}else{
			if(!map.containsKey(trecode+tdCorp)){
					this.tmpFailReason="预算单位代码"+tdCorp+"在预算单位代码参数中不存在!";
					return false;
				}
			
		}
		return true;
	}
	
	
	/**
	 * 校验付款人账号
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	 
	public boolean verifyAccName(String bookorgCode, String strecode, String spayeraccount)throws JAFDatabaseException, ValidateException, ITFEBizException{
		Map<String, TsInfoconnorgaccDto> map = SrvCacheFacade.cacheFinTreAcctInfo();
		if(map.get(bookorgCode+spayeraccount) == null) {
			this.tmpFailReason="付款人账号 "+spayeraccount+" 没有在'财政库款账户参数维护中维护!";
			return false;
		}
		return true;
	}
	
	/**
	 * 校验收款人开户行
	
	 * @return
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 * @throws ITFEBizException 
	 */
	public boolean verifyPayeeBankNo(String PayeeAcctBankNo, String vtcode) throws JAFDatabaseException, ValidateException, ITFEBizException{
		if(MsgConstant.VOUCHER_NO_5207.equals(vtcode)||MsgConstant.VOUCHER_NO_5209.equals(vtcode)){
			if("".equals(PayeeAcctBankNo) ||PayeeAcctBankNo==null){//财政没有填写行号，不进行校验
				return true;
			}
		}else{
			if("".equals(PayeeAcctBankNo) ||PayeeAcctBankNo==null){
				this.tmpFailReason = "银行代码为空";
				return false;
			}
		}
		HashMap<String, TsPaybankDto> bankmap = SrvCacheFacade.cachePayBankInfo();
		if(bankmap.get(PayeeAcctBankNo)==null){
			this.tmpFailReason = "收款行号"+PayeeAcctBankNo+"在支付系统行号中不存在!";
			return false;
		}
		return true;
	}
	
	/**
	 * 校验财政机构代码
	
	 * @return
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 * @throws ITFEBizException 
	 */
	public boolean verifySfinOrgCode(String SfinOrgCode,String sbookorgcode) throws JAFDatabaseException, ValidateException, ITFEBizException{
		if("".equals(SfinOrgCode) ||SfinOrgCode==null){
			this.tmpFailReason = "报文不规范，财政机构不能为空";
			return false;
		}
		HashMap<String, TsConvertfinorgDto> bankmap = SrvCacheFacade.cacheFincInfoByFinc(sbookorgcode);
		if(bankmap.get(SfinOrgCode)==null){
			this.tmpFailReason = "财政机构"+SfinOrgCode+"在财政机构中不存在!";
			return false;
		}
		return true;
	}


	public String getTmpFailReason() {
		return tmpFailReason;
	}

	public void setTmpFailReason(String tmpFailReason) {
		this.tmpFailReason = tmpFailReason;
	}
	
	/**
	 * 进行法人代码校验
	 * 
	 * @throws ValidateException
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public boolean verifyCorpcodeList(String orgcode,List<String> tdCorpList)
			throws JAFDatabaseException, ValidateException, ITFEBizException {
		Map<String, TdCorpDto> map = SrvCacheFacade.cacheTdCorpInfo(orgcode);
		if (map!=null) {
			for(String tdCorp:tdCorpList){
				if(!map.containsKey(tdCorp)){
					//去掉预算单位校验中的国库代码
					tdCorp =tdCorp.substring(10);
					this.tmpFailReason="预算单位代码"+tdCorp+"在预算单位代码参数中不存在";
					return false;
				}
			}
		} else {
			this.tmpFailReason="机构"+orgcode+"在法人代码参数中不存在";
			return false;
		}
		return true;
	}
	
	/**
	 * 代理银行名称校验（集中支付清算用）
	 */
	public boolean verifyPaybankName(String orgcode,String treCode, String bankno, String bankname)
	throws JAFDatabaseException, ValidateException, ITFEBizException {
		if("".equals(bankname) || bankname == null){
			this.tmpFailReason = "代理银行名称不能为空。";
			return false;
		}
		Map<String, TsConvertbanktypeDto> map = SrvCacheFacade.cacheTsconvertBankType(orgcode);
		if (map!=null && !map.isEmpty()) {
			if(map.containsKey(treCode+bankno)){
				String inputBankname = map.get(treCode+bankno).getSbankname();
				if(!bankname.equals(inputBankname)){
					this.tmpFailReason="代理银行名称"+bankname+"与【代理银行行号与行别对应关系】中不一致。";
					return false;
				}
			}else{
				this.tmpFailReason="代理银行代码没有在【代理银行行号与行别对应关系】中维护。";
				return false;
			}
		} else {
			this.tmpFailReason="代理银行行号与行别对应关系没有维护";
			return false;
		}
		return true;
	}
	
	/**
	 * 校验报文关键字段公共类（只进行格式校验）
	 * @param verifyDto
	 * @param vtcode
	 * @return
	 */
	public String checkValid(VoucherVerifyDto dto, String vtcode) {
		Pattern trecodePattern=Pattern.compile("[0-9]{10}");//匹配10位数字
		Pattern finorgcodePattern=Pattern.compile("[0-9]{1,12}");//匹配小于12位数字
		Pattern bankcodePattern=Pattern.compile("[0-9]{12}");//匹配12位数字
		Matcher match = null;
		StringBuffer sb = new StringBuffer() ;
		//国库代码
		if(StringUtils.isBlank(dto.getTrecode())){
			sb.append("国库代码不能为空");
		}else {
			match=trecodePattern.matcher(dto.getTrecode());
			if(match.matches()==false){
				sb.append("国库代码格式错误，必须为10位数字。");
			}
		}
		//财政机构代码
		if(StringUtils.isBlank(dto.getFinorgcode())){
			sb.append("财政机构代码不能为空");
		}else {
			match=finorgcodePattern.matcher(dto.getFinorgcode());
			if(match.matches()==false){
				sb.append("财政机构代码格式错误，必须为小于12位的数字。");
			}
		}
		//银行代码
		if(MsgConstant.VOUCHER_NO_5106.equals(vtcode) 
				|| MsgConstant.VOUCHER_NO_5108.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2301.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)){
			if(StringUtils.isBlank(dto.getPaybankno())){
				sb.append("代理银行代码不能为空");
			}else {
				match=bankcodePattern.matcher(dto.getPaybankno());
				if(match.matches()==false){
					sb.append("代理银行代码格式错误，必须为12位的数字。");
				}
			}
		}
		//凭证日期
		if(StringUtils.isBlank(dto.getVoudate())){
			sb.append("凭证日期不能为空");
		}
		//凭证编号
		if(StringUtils.isBlank(dto.getVoucherno())){
			sb.append("凭证编号不能为空");
		}else if(MsgConstant.VOUCHER_NO_5209.equals(vtcode)){//收入退付
			Pattern oldVouPattern=Pattern.compile("[0-9]{8}");//匹配8位数字
			if(ITFECommonConstant.IFNEWINTERFACE.equals("1")){//新接口
				if(dto.getVoucherno().length() > 20){
					sb.append("凭证编号必须小于20位");
				}
			}else{//旧接口
				match=oldVouPattern.matcher(dto.getVoucherno());
				if(match.matches()==false){
					sb.append("凭证编号格式错误，必须为8位的数字。");
				}
			}
		}else{
			if(dto.getVoucherno().length() > 20){
				sb.append("凭证编号必须小于20位");
			}
		}
		
		//支付方式
		if(MsgConstant.VOUCHER_NO_2301.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)){
			if(StringUtils.isBlank(dto.getPaytypecode().trim())){
				sb.append("支付方式不能为空");
			}else{
				if(!"11".equals(dto.getPaytypecode()) && !"12".equals(dto.getPaytypecode())){
					sb.append("支付方式只能为11或者12");
				}
			}
		}
		if(MsgConstant.VOUCHER_NO_5207.equals(vtcode) 
				|| MsgConstant.VOUCHER_NO_5209.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2301.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)){
			//收款银行账号
			if(StringUtils.isBlank(dto.getAgentAcctNo())){
				sb.append("收款账号不能为空");
			}else {
				if(dto.getAgentAcctNo().length()>60){
					sb.append("收款账号["+dto.getAgentAcctNo()+"]格式错误，必须为小于32位。");
				}
			}
			
			//收款银行账户名称
			if(StringUtils.isBlank(dto.getAgentAcctName())){
				sb.append("收款账号名称不能为空");
			}else if(dto.getAgentAcctName().length()>60){
				sb.append("收款账号名称格式错误，必须为小于60位（30个字）。");
			}
			
			//付款银行账号
			if(StringUtils.isBlank(dto.getClearAcctNo())){
				sb.append("付款账号不能为空");
			}else {
				if(dto.getClearAcctNo().length() > 32){
					sb.append("付款账号["+dto.getClearAcctNo()+"]格式错误，必须小于32位。");
				}
			}
			
			//付款银行账户名称
			if(StringUtils.isBlank(dto.getClearAcctName())){
				sb.append("付款账号名称不能为空");
			}else if(dto.getClearAcctName().length()>60){
				sb.append("付款账号名称格式错误，必须为小于60位（30个字）。");
			}
		}
		String msg = sb.toString() ;
		if (msg.length() == 0){
			return null ;
		}
		return  msg;
	}

		
}

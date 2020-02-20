package com.cfcc.itfe.facade;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TdBookacctMainDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertbanktypeDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsDwbkReasonDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * 服务器端缓存数据
 * 
 * @author db2admin
 * 
 */
@SuppressWarnings("unchecked")
public class SrvCacheFacade {
	// 支付系统参与者信息
	private static HashMap<String, TsPaybankDto> mapTsPayBank = null;
	// 预算科目代码信息
	private static HashMap<String, HashMap<String, TsBudgetsubjectDto>> mapTsBdgsbt = null;
	// 预算单位信息
	private static HashMap<String, HashMap<String, TdCorpDto>> mapTdCorp = null;
	// 国库代码缓存
	private static HashMap<String, TsTreasuryDto> mapTreInfo = null;
	// 财政机构代码缓存 key为国库代码
	private static HashMap<String, TsConvertfinorgDto> mapFincInfo = null;
	// 征收机关与国库对应关系缓存
	private static HashMap<String, TsConverttaxorgDto> mapTaxInfo = null;
	// 财政机构代码缓存key为财政代码
	private static HashMap<String, TsConvertfinorgDto> mapFincInfoByFinc = null;
	// 代理银行与支付行对应关系
	private static HashMap<String, TsGenbankandreckbankDto> mapGenBankByBankCode = null;
	// 缓存核算主体信息
	private static HashMap<String, TsOrganDto> mapOrgInfo = null;
	// 缓存核算主体信息
	private static HashMap<String, TsOrganDto> orgMapInfo = null;
	// 缓存财政库款账户信息
	private static HashMap<String, TsInfoconnorgaccDto> mapFinTreAcctInfo = null;
	// 缓存财政库款账户从账户表中
	private static HashMap<String, TdBookacctMainDto> mapFinTreAcctFromBookAcctInfo = null;
	// 缓存收付款人信息
	private static HashMap<String, HashMap<String, TsPayacctinfoDto>> payAcctInfoMap = new HashMap<String, HashMap<String, TsPayacctinfoDto>>();
	// 凭证自动提交参数表缓存
	private static HashMap<String, TsVouchercommitautoDto> mapVoucherAutoCommit = null;
	// 凭证自动读取参数缓存
	private static HashMap<String, List> mapVoucherAutoRead = null;
	// 凭证自动发送电子凭证库参数缓存
	private static HashMap<String, List> mapVoucherAutoSendSuccess = null;
	// 凭证自动提交Tips参数缓存
	private static HashMap<String, List> mapVoucherAutoCommitTips = null;
	// 凭证自动签章参数缓存
	private static HashMap<String, List> mapVoucherAutoStamp = null;
	// 银行代码与行别参数缓存
	private static HashMap<String, TsConvertbanktypeDto> mapTsconvertBankType = null;
	// 收入退付原因缓存
	private static HashMap<String, TsDwbkReasonDto> mapTsDwbkReason = null;
	//缓存生僻字用于校验
	private static HashMap<String,String> mapNotUsableChinese =null;
	//凭证报文比对信息缓存
	private static HashMap<String,List> mapVoucherCompare = new HashMap<String, List>();	
	
	/**
	 * HashMap<核算主体信息, HashMap<代理银行行号, 收付款人信息>>
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsPayacctinfoDto> cachePayacctInfo(
			String sorgcode) throws ITFEBizException {
		if (null == payAcctInfoMap || null == payAcctInfoMap.get(sorgcode)) {
			List<TsPayacctinfoDto> list = BusinessFacade
					.findPayacctInfos(sorgcode);
			if (null == list || list.size() == 0) {
				return new HashMap<String, TsPayacctinfoDto>();
			}
			if (StringUtils.isBlank(sorgcode)) {
				payAcctInfoMap.clear();
			}
			HashMap<String, TsPayacctinfoDto> tmpMap;
			for (TsPayacctinfoDto tmp : list) {
				if (null == payAcctInfoMap.get(tmp.getSorgcode())) {
					tmpMap = new HashMap<String, TsPayacctinfoDto>();
					tmpMap
							.put(
									(tmp.getStrecode() + tmp.getSgenbankcode()
											+ tmp.getSpayeracct() + tmp
											.getSpayeeacct()).trim(), tmp);
					payAcctInfoMap.put(tmp.getSorgcode(), tmpMap);
				} else {
					tmpMap = payAcctInfoMap.get(tmp.getSorgcode());
					tmpMap
							.put(
									(tmp.getStrecode() + tmp.getSgenbankcode()
											+ tmp.getSpayeracct() + tmp
											.getSpayeeacct()).trim(), tmp);
				}
			}
			return payAcctInfoMap.get(sorgcode);
		} else {
			return payAcctInfoMap.get(sorgcode);
		}
	}

	/**
	 *核算主体信息，按照支付行号为KEY
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsOrganDto> cacheOrgInfo()
			throws JAFDatabaseException, ITFEBizException {
		if (mapOrgInfo == null) {
			// 重新查库
			mapOrgInfo = BusinessFacade.findTsOrganInfo();
		}
		return mapOrgInfo;
	}
	/**
	 *核算主体信息，按照核算主体代码KEY
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsOrganDto> cacheOrgMap()
			throws JAFDatabaseException, ITFEBizException {
		if (orgMapInfo == null) {
			// 重新查库
			orgMapInfo = BusinessFacade.organInfo();
		}
		return orgMapInfo;
	}
	/**
	 *财政库款账户，按照支付行号为KEY
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsInfoconnorgaccDto> cacheFinTreAcctInfo()
			throws JAFDatabaseException, ITFEBizException {
		if (mapFinTreAcctInfo == null) {
			// 重新查库
			mapFinTreAcctInfo = BusinessFacade.findTsFinTreAcctInfo();
		}
		return mapFinTreAcctInfo;
	}

	/**
	 *财政库款账户，按照账户作为KEY
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TdBookacctMainDto> cacheFinTreAcctFromBookAcctInfo()
			throws JAFDatabaseException, ITFEBizException {
		if (mapFinTreAcctFromBookAcctInfo == null) {
			// 重新查库
			mapFinTreAcctFromBookAcctInfo = BusinessFacade
					.findAllFinTreAcctFromBookAcctInfo();
		}
		return mapFinTreAcctFromBookAcctInfo;
	}

	/**
	 *缓存支付系统参与者信息 主键：支付行号
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsPaybankDto> cachePayBankInfo()
			throws JAFDatabaseException, ITFEBizException {
		if (mapTsPayBank == null) {
			// 重新查库
			mapTsPayBank = BusinessFacade.findTsPayBankInfo();
		}
		return mapTsPayBank;
	}

	/**
	 * 缓存预算科目代码信息 主键：机构代码+"|"+预算科目代码
	 * 
	 * @param sbookorgcode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsBudgetsubjectDto> cacheTsBdgsbtInfo(
			String sbookorgcode) throws JAFDatabaseException, ITFEBizException {
		Integer lock = 1;
		if (mapTsBdgsbt == null) {
			synchronized (lock) {
				if (mapTsBdgsbt == null) {
					// 重新查库
					mapTsBdgsbt = BusinessFacade.findTsBdgsbtInfo(sbookorgcode);
				}
			}
		}
		return mapTsBdgsbt.get(sbookorgcode);
	}

	/**
	 * 预算单位信息 主键：机构代码+"|"+预算单位代码
	 * 
	 * @param sbookorgcode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TdCorpDto> cacheTdCorpInfo(String sbookorgcode)
			throws JAFDatabaseException, ITFEBizException {
		Integer lock = 1;
		if (mapTdCorp == null) {
			synchronized (lock) {
				if (mapTdCorp == null) {
					// 重新查库
					mapTdCorp = BusinessFacade.findTdCorp(sbookorgcode);
				}
			}
		}
		return mapTdCorp.get(sbookorgcode);
	}

	/**
	 * 国库代码 主键：机构代码+"|"+国库代码
	 * 
	 * @param sbookorgcode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsTreasuryDto> cacheTreasuryInfo(
			String sbookorgcode) throws JAFDatabaseException, ITFEBizException {
		if (mapTreInfo == null) {
			// 重新查库
			mapTreInfo = BusinessFacade.findTreasuryInfo(sbookorgcode);
		}
		return mapTreInfo;
	}

	/**
	 * 财政代码 主键：机构代码+"|"+财政代码
	 * 
	 * @param dacct
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConvertfinorgDto> cacheFincInfo(
			String sbookorgcode) throws JAFDatabaseException, ITFEBizException {
		if (mapFincInfo == null) {
			// 重新查库
			mapFincInfo = BusinessFacade.findFincInfo(sbookorgcode);
		}
		return mapFincInfo;
	}

	/**
	 * 银行代码 主键：机构代码+"|"+TBS银行代码
	 * 
	 * @param dacct
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsGenbankandreckbankDto> cacheGenBankInfo(
			String sbookorgcode) throws JAFDatabaseException, ITFEBizException {
		if (mapGenBankByBankCode == null) {
			// 重新查库
			mapGenBankByBankCode = BusinessFacade.findGenBankInfo(sbookorgcode);
		}
		return mapGenBankByBankCode;
	}

	/**
	 * 财政代码 主键：机构代码+"|"+财政代码
	 * 
	 * @param dacct
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConvertfinorgDto> cacheFincInfoByFinc(
			String sbookorgcode) throws JAFDatabaseException, ITFEBizException {
		if (mapFincInfoByFinc == null) {
			// 重新查库
			mapFincInfoByFinc = BusinessFacade.findFincInfoByFinc(sbookorgcode);
		}
		return mapFincInfoByFinc;
	}

	/**
	 * 财政代码 主键：机构代码+"|"+TBS征收机关代码+国库代码
	 * 
	 * @param dacct
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConverttaxorgDto> cacheTaxInfo(
			String sbookorgcode) throws JAFDatabaseException, ITFEBizException {
		if (mapTaxInfo == null) {
			// 重新查库
			mapTaxInfo = BusinessFacade.findTaxInfo(sbookorgcode);
		}
		return mapTaxInfo;
	}

	/**
	 * 重新载入缓存,在参数导入的地方调用
	 * 
	 * @param
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static void reloadBuffer(String bookorgcode, String paramType)
			throws JAFDatabaseException, ITFEBizException {

		if (paramType.equals(StateConstant.CachePayBank)) {
			// 支付系统参与者信息
			mapTsPayBank = BusinessFacade.findTsPayBankInfo();
		} else if (paramType.equals(StateConstant.CacheBdgSbt)) {
			// 预算科目代码信息
			mapTsBdgsbt = BusinessFacade.findTsBdgsbtInfo(bookorgcode);
		} else if (paramType.equals(StateConstant.CacheTDCrop)) {
			// 预算单位信息
			mapTdCorp = BusinessFacade.findTdCorp(bookorgcode);
		}

	}

	/**
	 * 重新载入缓存
	 * 
	 * @param
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static void reloadBuffer(String sbookorgcode)
			throws JAFDatabaseException, ITFEBizException {
		// 支付系统参与者信息 不在这个地方刷新，在导入后直接刷新
		mapTsPayBank = BusinessFacade.findTsPayBankInfo();
		// 预算科目代码信息
		mapTsBdgsbt = BusinessFacade.findTsBdgsbtInfo(sbookorgcode);
		// 预算单位信息
		mapTdCorp = BusinessFacade.findTdCorp(sbookorgcode);
		// 国库代码缓存
		mapTreInfo = BusinessFacade.findTreasuryInfo(sbookorgcode);
		// 财政机构代码缓存
		mapFincInfo = BusinessFacade.findFincInfo(sbookorgcode);
		mapFincInfoByFinc = BusinessFacade.findFincInfoByFinc(sbookorgcode);
		// 征收机关与对照表
		mapTaxInfo = BusinessFacade.findTaxInfo(sbookorgcode);
		// 代理银行与支付行对应关系
		mapGenBankByBankCode = BusinessFacade.findGenBankInfo(sbookorgcode);
		// 核算主体信息
		mapOrgInfo = BusinessFacade.findTsOrganInfo();
		//核算主体代码为key
		orgMapInfo = BusinessFacade.organInfo();
		// 财政库款账户
		mapFinTreAcctInfo = BusinessFacade.findTsFinTreAcctInfo();
		// 库款账户缓存从账户表中
		mapFinTreAcctFromBookAcctInfo = BusinessFacade
				.findAllFinTreAcctFromBookAcctInfo();
		// 收付款人信息
		cachePayacctInfo(null);
		// 凭证自动提交参数表
		mapVoucherAutoCommit = BusinessFacade.findTsVoucherAutoCommit();
		new TsVouchercommitautoDto();
		// 凭证自动控制业务参数
		mapVoucherAutoRead = BusinessFacade.findTsVoucherAuto(TsVouchercommitautoDto.columnSreadauto());
		// 凭证自动发送电子凭证库业务参数
		mapVoucherAutoSendSuccess = BusinessFacade.findTsVoucherAuto(TsVouchercommitautoDto.columnSreturnvoucherauto());
		// 凭证自动提交Tips业务参数
		mapVoucherAutoCommitTips = BusinessFacade.findTsVoucherAuto(TsVouchercommitautoDto.columnScommitauto());
		new TsVouchercommitautoDto();
		// 凭证自动签章业务参数
		mapVoucherAutoStamp = BusinessFacade.findTsVoucherAuto(TsVouchercommitautoDto.columnSstampauto());
		// 代理银行与行别对应关系
		mapTsconvertBankType = BusinessFacade.findTsconvertBankType(sbookorgcode);
		// 缓存生僻字信息
		mapNotUsableChinese = BusinessFacade.findNotUsableChinese();
		//凭证报文比对信息缓存
		mapVoucherCompare = new HashMap<String, List>();
	}

	/**
	 *缓存凭证自动提交参数表 主键：国库代码+凭证类型，value=TsVouchercommitautoDto
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsVouchercommitautoDto> cacheVoucherAutoCommit()
			throws JAFDatabaseException, ITFEBizException {
		if (null == mapVoucherAutoCommit) {
			mapVoucherAutoCommit = BusinessFacade.findTsVoucherAutoCommit();
		}
		return mapVoucherAutoCommit;
	}

	/**
	 *缓存凭证自动控制参数
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, List> cacheVoucherAuto(String column)
			throws JAFDatabaseException, ITFEBizException {
		if (column.equals(TsVouchercommitautoDto.columnSreadauto())) {
			if (null == mapVoucherAutoRead) {
				mapVoucherAutoRead = BusinessFacade.findTsVoucherAuto(column);

			}
			return mapVoucherAutoRead;
		} else if (column.equals(TsVouchercommitautoDto.columnSreturnvoucherauto())) {

			if (null == mapVoucherAutoSendSuccess) {
				mapVoucherAutoSendSuccess = BusinessFacade
						.findTsVoucherAuto(column);
			}
			return mapVoucherAutoSendSuccess;
		}else if (column.equals(TsVouchercommitautoDto.columnScommitauto())) {
			if (null == mapVoucherAutoCommitTips) {
				mapVoucherAutoCommitTips = BusinessFacade
						.findTsVoucherAuto(column);
			}
			return mapVoucherAutoCommitTips;
		}else if (column.equals(TsVouchercommitautoDto.columnSstampauto())) {
			if (null == mapVoucherAutoStamp) {
				mapVoucherAutoStamp = BusinessFacade
						.findTsVoucherAuto(column);
			}return mapVoucherAutoStamp;
		}else if(column.equals(TsVouchercommitautoDto.columnSreturbacknauto())){
			if (null == mapVoucherAutoStamp) {
				mapVoucherAutoStamp = BusinessFacade
						.findTsVoucherAuto(column);
			}return mapVoucherAutoStamp;
		}
		return mapVoucherAutoRead;

	}
	
	/**
	 *银行代码与行别参数缓存,主键 国库代码+银行行号
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConvertbanktypeDto> cacheTsconvertBankType(String sbookorgcode)
			throws JAFDatabaseException, ITFEBizException {
		if (mapTsconvertBankType == null) {
			// 重新查库
			mapTsconvertBankType = BusinessFacade.findTsconvertBankType(sbookorgcode);
		}
		return mapTsconvertBankType;

	}
	
	/**
	 *收入退付原因缓存,主键 退付原因名称
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws ValidateException 
	 */
	public static HashMap<String, TsDwbkReasonDto> cacheTsDwbkReason(String sbookorgcode)
			throws JAFDatabaseException, ITFEBizException, ValidateException {
		if (mapTsDwbkReason == null) {
			// 重新查库
			mapTsDwbkReason = BusinessFacade.findTsDwbkReason(sbookorgcode);
		}
		return mapTsDwbkReason;

	}
	
	/**
	 * 非法字符缓存 
	 * 
	 * @param dacct
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, String> cacheNotUsableChinese()
			throws JAFDatabaseException, ITFEBizException {
		if (mapNotUsableChinese == null) {
			// 重新查库
			mapNotUsableChinese = BusinessFacade.findNotUsableChinese();
			
		}
		return mapNotUsableChinese;
	}
	
	/**
	 * 获取凭证比对缓存信息
	 * 主键：国库代码+凭证类型+比对凭证类型+凭证编号 
	 * 缓存集合：索引表dto+业务主表maindto+子表集合+subdtoList
	 * @param dto
	 * @return
	 */
	public static List cacheVoucherCompare(TvVoucherinfoDto dto) {
		//获取凭证比对勾兑缓存信息
		if(dto!=null&&dto.getSstatus().equals(DealCodeConstants.VOUCHER_VALIDAT))
			return mapVoucherCompare.get(dto.getStrecode()+dto.getScheckvouchertype()+dto.getSvtcode()+
					((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)||
							dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)||
							dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106))?
									dto.getShold3():dto.getSvoucherno()));		
		return null;			
	}	
	
	/**
	 * 移除凭证比对缓存信息
	 * @param dto
	 */
	public static void removeCacheVoucherCompare(TvVoucherinfoDto dto){
		if(dto!=null){
			mapVoucherCompare.remove(dto.getStrecode()+dto.getSvtcode()+
					dto.getScheckvouchertype()+dto.getSvoucherno());
			mapVoucherCompare.remove(dto.getStrecode()+dto.getSvtcode()+
					dto.getScheckvouchertype()+dto.getShold3());
			mapVoucherCompare.remove(dto.getStrecode()+dto.getScheckvouchertype()+
					dto.getSvtcode()+dto.getSvoucherno());
			mapVoucherCompare.remove(dto.getStrecode()+dto.getScheckvouchertype()+
					dto.getSvtcode()+dto.getShold3());		
		}
	}
	
	/**
	 * 放入凭证比对缓存信息
	 * 主键：国库代码+凭证类型+比对凭证类型+凭证编号 
	 * 缓存集合：索引表dto+业务主表maindto+子表集合+subdtoList
	 * @param dto
	 * @param maindto
	 * @param subdtoList
	 * @throws ITFEBizException 
	 */
	public static void cacheVoucherCompare(TvVoucherinfoDto dto,Object maindto,List subdtoList) throws ITFEBizException {
		if(dto.getSstatus().equals(DealCodeConstants.VOUCHER_VALIDAT)&&mapVoucherCompare.size()<=200){
			List list=new ArrayList();
			list.add(dto);
			list.add(maindto);
			list.add(subdtoList);
			mapVoucherCompare.put(dto.getStrecode()+dto.getSvtcode()+dto.getScheckvouchertype()+
					((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)||
							dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)||
							dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106))?
							dto.getShold3():dto.getSvoucherno()), list);
		}			
	}

	public static HashMap<String, List> getMapVoucherCompare() {
		return mapVoucherCompare;
	}

	public static void setMapVoucherCompare(HashMap<String, List> mapVoucherCompare) {
		SrvCacheFacade.mapVoucherCompare = mapVoucherCompare;
	}

	public static HashMap<String, TsPaybankDto> getMapTsPayBank() {
		return mapTsPayBank;
	}

	public static void setMapTsPayBank(HashMap<String, TsPaybankDto> mapTsPayBank) {
		SrvCacheFacade.mapTsPayBank = mapTsPayBank;
	}

	public static HashMap<String, HashMap<String, TsBudgetsubjectDto>> getMapTsBdgsbt() {
		return mapTsBdgsbt;
	}

	public static void setMapTsBdgsbt(
			HashMap<String, HashMap<String, TsBudgetsubjectDto>> mapTsBdgsbt) {
		SrvCacheFacade.mapTsBdgsbt = mapTsBdgsbt;
	}

	public static HashMap<String, HashMap<String, TdCorpDto>> getMapTdCorp() {
		return mapTdCorp;
	}

	public static void setMapTdCorp(
			HashMap<String, HashMap<String, TdCorpDto>> mapTdCorp) {
		SrvCacheFacade.mapTdCorp = mapTdCorp;
	}

	public static HashMap<String, TsTreasuryDto> getMapTreInfo() {
		return mapTreInfo;
	}

	public static void setMapTreInfo(HashMap<String, TsTreasuryDto> mapTreInfo) {
		SrvCacheFacade.mapTreInfo = mapTreInfo;
	}

	public static HashMap<String, TsConvertfinorgDto> getMapFincInfo() {
		return mapFincInfo;
	}

	public static void setMapFincInfo(
			HashMap<String, TsConvertfinorgDto> mapFincInfo) {
		SrvCacheFacade.mapFincInfo = mapFincInfo;
	}

	public static HashMap<String, TsConverttaxorgDto> getMapTaxInfo() {
		return mapTaxInfo;
	}

	public static void setMapTaxInfo(HashMap<String, TsConverttaxorgDto> mapTaxInfo) {
		SrvCacheFacade.mapTaxInfo = mapTaxInfo;
	}

	public static HashMap<String, TsConvertfinorgDto> getMapFincInfoByFinc() {
		return mapFincInfoByFinc;
	}

	public static void setMapFincInfoByFinc(
			HashMap<String, TsConvertfinorgDto> mapFincInfoByFinc) {
		SrvCacheFacade.mapFincInfoByFinc = mapFincInfoByFinc;
	}

	public static HashMap<String, TsGenbankandreckbankDto> getMapGenBankByBankCode() {
		return mapGenBankByBankCode;
	}

	public static void setMapGenBankByBankCode(
			HashMap<String, TsGenbankandreckbankDto> mapGenBankByBankCode) {
		SrvCacheFacade.mapGenBankByBankCode = mapGenBankByBankCode;
	}

	public static HashMap<String, TsOrganDto> getMapOrgInfo() {
		return mapOrgInfo;
	}

	public static void setMapOrgInfo(HashMap<String, TsOrganDto> mapOrgInfo) {
		SrvCacheFacade.mapOrgInfo = mapOrgInfo;
	}

	public static HashMap<String, TsOrganDto> getOrgMapInfo() {
		return orgMapInfo;
	}

	public static void setOrgMapInfo(HashMap<String, TsOrganDto> orgMapInfo) {
		SrvCacheFacade.orgMapInfo = orgMapInfo;
	}

	public static HashMap<String, TsInfoconnorgaccDto> getMapFinTreAcctInfo() {
		return mapFinTreAcctInfo;
	}

	public static void setMapFinTreAcctInfo(
			HashMap<String, TsInfoconnorgaccDto> mapFinTreAcctInfo) {
		SrvCacheFacade.mapFinTreAcctInfo = mapFinTreAcctInfo;
	}

	public static HashMap<String, TdBookacctMainDto> getMapFinTreAcctFromBookAcctInfo() {
		return mapFinTreAcctFromBookAcctInfo;
	}

	public static void setMapFinTreAcctFromBookAcctInfo(
			HashMap<String, TdBookacctMainDto> mapFinTreAcctFromBookAcctInfo) {
		SrvCacheFacade.mapFinTreAcctFromBookAcctInfo = mapFinTreAcctFromBookAcctInfo;
	}

	public static HashMap<String, HashMap<String, TsPayacctinfoDto>> getPayAcctInfoMap() {
		return payAcctInfoMap;
	}

	public static void setPayAcctInfoMap(
			HashMap<String, HashMap<String, TsPayacctinfoDto>> payAcctInfoMap) {
		SrvCacheFacade.payAcctInfoMap = payAcctInfoMap;
	}

	public static HashMap<String, TsVouchercommitautoDto> getMapVoucherAutoCommit() {
		return mapVoucherAutoCommit;
	}

	public static void setMapVoucherAutoCommit(
			HashMap<String, TsVouchercommitautoDto> mapVoucherAutoCommit) {
		SrvCacheFacade.mapVoucherAutoCommit = mapVoucherAutoCommit;
	}

	public static HashMap<String, List> getMapVoucherAutoRead() {
		return mapVoucherAutoRead;
	}

	public static void setMapVoucherAutoRead(
			HashMap<String, List> mapVoucherAutoRead) {
		SrvCacheFacade.mapVoucherAutoRead = mapVoucherAutoRead;
	}

	public static HashMap<String, List> getMapVoucherAutoSendSuccess() {
		return mapVoucherAutoSendSuccess;
	}

	public static void setMapVoucherAutoSendSuccess(
			HashMap<String, List> mapVoucherAutoSendSuccess) {
		SrvCacheFacade.mapVoucherAutoSendSuccess = mapVoucherAutoSendSuccess;
	}

	public static HashMap<String, List> getMapVoucherAutoCommitTips() {
		return mapVoucherAutoCommitTips;
	}

	public static void setMapVoucherAutoCommitTips(
			HashMap<String, List> mapVoucherAutoCommitTips) {
		SrvCacheFacade.mapVoucherAutoCommitTips = mapVoucherAutoCommitTips;
	}

	public static HashMap<String, List> getMapVoucherAutoStamp() {
		return mapVoucherAutoStamp;
	}

	public static void setMapVoucherAutoStamp(
			HashMap<String, List> mapVoucherAutoStamp) {
		SrvCacheFacade.mapVoucherAutoStamp = mapVoucherAutoStamp;
	}

	public static HashMap<String, TsConvertbanktypeDto> getMapTsconvertBankType() {
		return mapTsconvertBankType;
	}

	public static void setMapTsconvertBankType(
			HashMap<String, TsConvertbanktypeDto> mapTsconvertBankType) {
		SrvCacheFacade.mapTsconvertBankType = mapTsconvertBankType;
	}

	public static HashMap<String, TsDwbkReasonDto> getMapTsDwbkReason() {
		return mapTsDwbkReason;
	}

	public static void setMapTsDwbkReason(
			HashMap<String, TsDwbkReasonDto> mapTsDwbkReason) {
		SrvCacheFacade.mapTsDwbkReason = mapTsDwbkReason;
	}

	public static HashMap<String, String> getMapNotUsableChinese() {
		return mapNotUsableChinese;
	}

	public static void setMapNotUsableChinese(
			HashMap<String, String> mapNotUsableChinese) {
		SrvCacheFacade.mapNotUsableChinese = mapNotUsableChinese;
	}
}

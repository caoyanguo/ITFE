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
 * �������˻�������
 * 
 * @author db2admin
 * 
 */
@SuppressWarnings("unchecked")
public class SrvCacheFacade {
	// ֧��ϵͳ��������Ϣ
	private static HashMap<String, TsPaybankDto> mapTsPayBank = null;
	// Ԥ���Ŀ������Ϣ
	private static HashMap<String, HashMap<String, TsBudgetsubjectDto>> mapTsBdgsbt = null;
	// Ԥ�㵥λ��Ϣ
	private static HashMap<String, HashMap<String, TdCorpDto>> mapTdCorp = null;
	// ������뻺��
	private static HashMap<String, TsTreasuryDto> mapTreInfo = null;
	// �����������뻺�� keyΪ�������
	private static HashMap<String, TsConvertfinorgDto> mapFincInfo = null;
	// ���ջ���������Ӧ��ϵ����
	private static HashMap<String, TsConverttaxorgDto> mapTaxInfo = null;
	// �����������뻺��keyΪ��������
	private static HashMap<String, TsConvertfinorgDto> mapFincInfoByFinc = null;
	// ����������֧���ж�Ӧ��ϵ
	private static HashMap<String, TsGenbankandreckbankDto> mapGenBankByBankCode = null;
	// �������������Ϣ
	private static HashMap<String, TsOrganDto> mapOrgInfo = null;
	// �������������Ϣ
	private static HashMap<String, TsOrganDto> orgMapInfo = null;
	// �����������˻���Ϣ
	private static HashMap<String, TsInfoconnorgaccDto> mapFinTreAcctInfo = null;
	// �����������˻����˻�����
	private static HashMap<String, TdBookacctMainDto> mapFinTreAcctFromBookAcctInfo = null;
	// �����ո�������Ϣ
	private static HashMap<String, HashMap<String, TsPayacctinfoDto>> payAcctInfoMap = new HashMap<String, HashMap<String, TsPayacctinfoDto>>();
	// ƾ֤�Զ��ύ��������
	private static HashMap<String, TsVouchercommitautoDto> mapVoucherAutoCommit = null;
	// ƾ֤�Զ���ȡ��������
	private static HashMap<String, List> mapVoucherAutoRead = null;
	// ƾ֤�Զ����͵���ƾ֤���������
	private static HashMap<String, List> mapVoucherAutoSendSuccess = null;
	// ƾ֤�Զ��ύTips��������
	private static HashMap<String, List> mapVoucherAutoCommitTips = null;
	// ƾ֤�Զ�ǩ�²�������
	private static HashMap<String, List> mapVoucherAutoStamp = null;
	// ���д������б��������
	private static HashMap<String, TsConvertbanktypeDto> mapTsconvertBankType = null;
	// �����˸�ԭ�򻺴�
	private static HashMap<String, TsDwbkReasonDto> mapTsDwbkReason = null;
	//������Ƨ������У��
	private static HashMap<String,String> mapNotUsableChinese =null;
	//ƾ֤���ıȶ���Ϣ����
	private static HashMap<String,List> mapVoucherCompare = new HashMap<String, List>();	
	
	/**
	 * HashMap<����������Ϣ, HashMap<���������к�, �ո�������Ϣ>>
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
	 *����������Ϣ������֧���к�ΪKEY
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsOrganDto> cacheOrgInfo()
			throws JAFDatabaseException, ITFEBizException {
		if (mapOrgInfo == null) {
			// ���²��
			mapOrgInfo = BusinessFacade.findTsOrganInfo();
		}
		return mapOrgInfo;
	}
	/**
	 *����������Ϣ�����պ����������KEY
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsOrganDto> cacheOrgMap()
			throws JAFDatabaseException, ITFEBizException {
		if (orgMapInfo == null) {
			// ���²��
			orgMapInfo = BusinessFacade.organInfo();
		}
		return orgMapInfo;
	}
	/**
	 *��������˻�������֧���к�ΪKEY
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsInfoconnorgaccDto> cacheFinTreAcctInfo()
			throws JAFDatabaseException, ITFEBizException {
		if (mapFinTreAcctInfo == null) {
			// ���²��
			mapFinTreAcctInfo = BusinessFacade.findTsFinTreAcctInfo();
		}
		return mapFinTreAcctInfo;
	}

	/**
	 *��������˻��������˻���ΪKEY
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TdBookacctMainDto> cacheFinTreAcctFromBookAcctInfo()
			throws JAFDatabaseException, ITFEBizException {
		if (mapFinTreAcctFromBookAcctInfo == null) {
			// ���²��
			mapFinTreAcctFromBookAcctInfo = BusinessFacade
					.findAllFinTreAcctFromBookAcctInfo();
		}
		return mapFinTreAcctFromBookAcctInfo;
	}

	/**
	 *����֧��ϵͳ��������Ϣ ������֧���к�
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsPaybankDto> cachePayBankInfo()
			throws JAFDatabaseException, ITFEBizException {
		if (mapTsPayBank == null) {
			// ���²��
			mapTsPayBank = BusinessFacade.findTsPayBankInfo();
		}
		return mapTsPayBank;
	}

	/**
	 * ����Ԥ���Ŀ������Ϣ ��������������+"|"+Ԥ���Ŀ����
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
					// ���²��
					mapTsBdgsbt = BusinessFacade.findTsBdgsbtInfo(sbookorgcode);
				}
			}
		}
		return mapTsBdgsbt.get(sbookorgcode);
	}

	/**
	 * Ԥ�㵥λ��Ϣ ��������������+"|"+Ԥ�㵥λ����
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
					// ���²��
					mapTdCorp = BusinessFacade.findTdCorp(sbookorgcode);
				}
			}
		}
		return mapTdCorp.get(sbookorgcode);
	}

	/**
	 * ������� ��������������+"|"+�������
	 * 
	 * @param sbookorgcode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsTreasuryDto> cacheTreasuryInfo(
			String sbookorgcode) throws JAFDatabaseException, ITFEBizException {
		if (mapTreInfo == null) {
			// ���²��
			mapTreInfo = BusinessFacade.findTreasuryInfo(sbookorgcode);
		}
		return mapTreInfo;
	}

	/**
	 * �������� ��������������+"|"+��������
	 * 
	 * @param dacct
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConvertfinorgDto> cacheFincInfo(
			String sbookorgcode) throws JAFDatabaseException, ITFEBizException {
		if (mapFincInfo == null) {
			// ���²��
			mapFincInfo = BusinessFacade.findFincInfo(sbookorgcode);
		}
		return mapFincInfo;
	}

	/**
	 * ���д��� ��������������+"|"+TBS���д���
	 * 
	 * @param dacct
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsGenbankandreckbankDto> cacheGenBankInfo(
			String sbookorgcode) throws JAFDatabaseException, ITFEBizException {
		if (mapGenBankByBankCode == null) {
			// ���²��
			mapGenBankByBankCode = BusinessFacade.findGenBankInfo(sbookorgcode);
		}
		return mapGenBankByBankCode;
	}

	/**
	 * �������� ��������������+"|"+��������
	 * 
	 * @param dacct
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConvertfinorgDto> cacheFincInfoByFinc(
			String sbookorgcode) throws JAFDatabaseException, ITFEBizException {
		if (mapFincInfoByFinc == null) {
			// ���²��
			mapFincInfoByFinc = BusinessFacade.findFincInfoByFinc(sbookorgcode);
		}
		return mapFincInfoByFinc;
	}

	/**
	 * �������� ��������������+"|"+TBS���ջ��ش���+�������
	 * 
	 * @param dacct
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConverttaxorgDto> cacheTaxInfo(
			String sbookorgcode) throws JAFDatabaseException, ITFEBizException {
		if (mapTaxInfo == null) {
			// ���²��
			mapTaxInfo = BusinessFacade.findTaxInfo(sbookorgcode);
		}
		return mapTaxInfo;
	}

	/**
	 * �������뻺��,�ڲ�������ĵط�����
	 * 
	 * @param
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static void reloadBuffer(String bookorgcode, String paramType)
			throws JAFDatabaseException, ITFEBizException {

		if (paramType.equals(StateConstant.CachePayBank)) {
			// ֧��ϵͳ��������Ϣ
			mapTsPayBank = BusinessFacade.findTsPayBankInfo();
		} else if (paramType.equals(StateConstant.CacheBdgSbt)) {
			// Ԥ���Ŀ������Ϣ
			mapTsBdgsbt = BusinessFacade.findTsBdgsbtInfo(bookorgcode);
		} else if (paramType.equals(StateConstant.CacheTDCrop)) {
			// Ԥ�㵥λ��Ϣ
			mapTdCorp = BusinessFacade.findTdCorp(bookorgcode);
		}

	}

	/**
	 * �������뻺��
	 * 
	 * @param
	 * @return
	 * @throws JAFDatabaseException
	 */
	public static void reloadBuffer(String sbookorgcode)
			throws JAFDatabaseException, ITFEBizException {
		// ֧��ϵͳ��������Ϣ ��������ط�ˢ�£��ڵ����ֱ��ˢ��
		mapTsPayBank = BusinessFacade.findTsPayBankInfo();
		// Ԥ���Ŀ������Ϣ
		mapTsBdgsbt = BusinessFacade.findTsBdgsbtInfo(sbookorgcode);
		// Ԥ�㵥λ��Ϣ
		mapTdCorp = BusinessFacade.findTdCorp(sbookorgcode);
		// ������뻺��
		mapTreInfo = BusinessFacade.findTreasuryInfo(sbookorgcode);
		// �����������뻺��
		mapFincInfo = BusinessFacade.findFincInfo(sbookorgcode);
		mapFincInfoByFinc = BusinessFacade.findFincInfoByFinc(sbookorgcode);
		// ���ջ�������ձ�
		mapTaxInfo = BusinessFacade.findTaxInfo(sbookorgcode);
		// ����������֧���ж�Ӧ��ϵ
		mapGenBankByBankCode = BusinessFacade.findGenBankInfo(sbookorgcode);
		// ����������Ϣ
		mapOrgInfo = BusinessFacade.findTsOrganInfo();
		//�����������Ϊkey
		orgMapInfo = BusinessFacade.organInfo();
		// ��������˻�
		mapFinTreAcctInfo = BusinessFacade.findTsFinTreAcctInfo();
		// ����˻�������˻�����
		mapFinTreAcctFromBookAcctInfo = BusinessFacade
				.findAllFinTreAcctFromBookAcctInfo();
		// �ո�������Ϣ
		cachePayacctInfo(null);
		// ƾ֤�Զ��ύ������
		mapVoucherAutoCommit = BusinessFacade.findTsVoucherAutoCommit();
		new TsVouchercommitautoDto();
		// ƾ֤�Զ�����ҵ�����
		mapVoucherAutoRead = BusinessFacade.findTsVoucherAuto(TsVouchercommitautoDto.columnSreadauto());
		// ƾ֤�Զ����͵���ƾ֤��ҵ�����
		mapVoucherAutoSendSuccess = BusinessFacade.findTsVoucherAuto(TsVouchercommitautoDto.columnSreturnvoucherauto());
		// ƾ֤�Զ��ύTipsҵ�����
		mapVoucherAutoCommitTips = BusinessFacade.findTsVoucherAuto(TsVouchercommitautoDto.columnScommitauto());
		new TsVouchercommitautoDto();
		// ƾ֤�Զ�ǩ��ҵ�����
		mapVoucherAutoStamp = BusinessFacade.findTsVoucherAuto(TsVouchercommitautoDto.columnSstampauto());
		// �����������б��Ӧ��ϵ
		mapTsconvertBankType = BusinessFacade.findTsconvertBankType(sbookorgcode);
		// ������Ƨ����Ϣ
		mapNotUsableChinese = BusinessFacade.findNotUsableChinese();
		//ƾ֤���ıȶ���Ϣ����
		mapVoucherCompare = new HashMap<String, List>();
	}

	/**
	 *����ƾ֤�Զ��ύ������ �������������+ƾ֤���ͣ�value=TsVouchercommitautoDto
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
	 *����ƾ֤�Զ����Ʋ���
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
	 *���д������б��������,���� �������+�����к�
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, TsConvertbanktypeDto> cacheTsconvertBankType(String sbookorgcode)
			throws JAFDatabaseException, ITFEBizException {
		if (mapTsconvertBankType == null) {
			// ���²��
			mapTsconvertBankType = BusinessFacade.findTsconvertBankType(sbookorgcode);
		}
		return mapTsconvertBankType;

	}
	
	/**
	 *�����˸�ԭ�򻺴�,���� �˸�ԭ������
	 * 
	 * @param sbookorgcode
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 * @throws ValidateException 
	 */
	public static HashMap<String, TsDwbkReasonDto> cacheTsDwbkReason(String sbookorgcode)
			throws JAFDatabaseException, ITFEBizException, ValidateException {
		if (mapTsDwbkReason == null) {
			// ���²��
			mapTsDwbkReason = BusinessFacade.findTsDwbkReason(sbookorgcode);
		}
		return mapTsDwbkReason;

	}
	
	/**
	 * �Ƿ��ַ����� 
	 * 
	 * @param dacct
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public static HashMap<String, String> cacheNotUsableChinese()
			throws JAFDatabaseException, ITFEBizException {
		if (mapNotUsableChinese == null) {
			// ���²��
			mapNotUsableChinese = BusinessFacade.findNotUsableChinese();
			
		}
		return mapNotUsableChinese;
	}
	
	/**
	 * ��ȡƾ֤�ȶԻ�����Ϣ
	 * �������������+ƾ֤����+�ȶ�ƾ֤����+ƾ֤��� 
	 * ���漯�ϣ�������dto+ҵ������maindto+�ӱ���+subdtoList
	 * @param dto
	 * @return
	 */
	public static List cacheVoucherCompare(TvVoucherinfoDto dto) {
		//��ȡƾ֤�ȶԹ��һ�����Ϣ
		if(dto!=null&&dto.getSstatus().equals(DealCodeConstants.VOUCHER_VALIDAT))
			return mapVoucherCompare.get(dto.getStrecode()+dto.getScheckvouchertype()+dto.getSvtcode()+
					((dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207)||
							dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5108)||
							dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5106))?
									dto.getShold3():dto.getSvoucherno()));		
		return null;			
	}	
	
	/**
	 * �Ƴ�ƾ֤�ȶԻ�����Ϣ
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
	 * ����ƾ֤�ȶԻ�����Ϣ
	 * �������������+ƾ֤����+�ȶ�ƾ֤����+ƾ֤��� 
	 * ���漯�ϣ�������dto+ҵ������maindto+�ӱ���+subdtoList
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

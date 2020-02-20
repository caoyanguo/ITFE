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
 * ƾ֤У��
 * @author sunyan
 *
 */
public class VoucherVerifySX {
	private static Log logger = LogFactory.getLog(VoucherVerifySX.class);
	
	private String tmpFailReason;//У��ʧ��ԭ��
	public VoucherVerifySX(){
		tmpFailReason="";
	}
	
	
	/**
	 * ƾ֤У��
	 * @param Lists ƾ֤����
	 * @param vtcode ƾ֤����
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	public  int verify(List lists,String vtCode) throws ITFEBizException{
		//����У��ɹ���ƾ֤List
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
			throw new ITFEBizException("�鿴ҵ��������Ϣ�����ƾ֤״̬�쳣��",e);
		}catch(Exception e){
			logger.error(e);
			throw new ITFEBizException("ƾ֤У������쳣��",e);
		}
		return count;
	}
	/**
	 * �����˿�ƾ֤�ص�
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
			//Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			//Ԥ�㵥λ����list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
				//У��
			if(verifyTreasury(mainDto.getStrecode(), mainDto.getSbookorgcode())
					&& verifySfinOrgCode(mainDto.getSfinorgcode(),mainDto.getStrecode())
					&& verifySubject(mainDto.getSbookorgcode(), expFuncCodeList, vtCode)
					&& verifyCorpcodeList(mainDto.getSbookorgcode(),tdcorpList)
					&& verifySpayInfo(mainDto)//У���ո�������Ϣ��֤ ���� �տ��˿������к� ���ո�������Ϣ
					&& verifyPaybankName(mainDto.getSbookorgcode(),mainDto.getStrecode(), mainDto.getSagentbnkcode(),mainDto.getSpaybankname())//У�������������
				){
					returnList.add(vDto);
					//����ƾ֤״̬
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), null, true);
				}else{
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), this.tmpFailReason, false);
					
				}
		}
		return returnList;
	}
	/**
	 * ���뻮��ƾ֤�ص�
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
			
			//Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			//Ԥ�㵥λ����list
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
				//У��
				if(verifyTreasury(mainDto.getStrecode(), mainDto.getSbookorgcode())
						&& verifySfinOrgCode(mainDto.getSfinorgcode(),mainDto.getStrecode())
						&& verifySubject(mainDto.getSbookorgcode(), expFuncCodeList, vtCode)
						&& verifyCorpcodeList(mainDto.getSbookorgcode(),tdcorpList)
						&& verifySpayInfo(mainDto)//У���ո�������Ϣ��֤ ���� �տ��˿������к� ���ո�������Ϣ
						&& verifyPaybankName(mainDto.getSbookorgcode(),mainDto.getStrecode(), mainDto.getSpayeeopbkno(),mainDto.getSpaybankname())//У�������������
				){
					returnList.add(vDto);
					//����ƾ֤״̬
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), null, true);
				}else{
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), this.tmpFailReason, false);
				}
			
		}
		return returnList;
	}

	/**
	 * У���ո�������Ϣ��֤ ���� �տ��˿������к� ���ո�������Ϣ
	 * @param mainDto
	 * @return 
	 * @throws ITFEBizException 
	 */
	private boolean verifySpayInfo(IDto dto) throws ITFEBizException {
		if (dto instanceof TvPayreckbankSxDto ) {
			TvPayreckbankSxDto mainDto=(TvPayreckbankSxDto) dto;
			// ���ڵ�һ�˻����ո����˵�У��
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(mainDto.getSbookorgcode()); // ��ȡ�ո�������Ϣ
			if (StringUtils.isBlank(mainDto.getSpayeeopbkno())) {
				this.tmpFailReason = "���Ĳ��淶, û���ҵ��տ��˿������к���Ϣ!";
				return false;
			}else {
				TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap.get((mainDto.getStrecode()+mainDto.getSpayeeopbkno()+mainDto.getSpayeracct()+mainDto.getSpayeeacct()).trim());
				if (null == tmppayacctinfoDto) {
					this.tmpFailReason="���ݹ�����룺"+mainDto.getStrecode()+", �տ��˿������кţ�"+ mainDto.getSpayeeopbkno()+", �������˺ţ�"+mainDto.getSpayeracct()+", �տ����˺ţ�"+mainDto.getSpayeeacct()+ " û���ҵ��ո�������Ϣ!";
					return false;
				}else{
					if(!mainDto.getSpayeracct().equals(tmppayacctinfoDto.getSpayeracct())){	// �������ʻ�
						this.tmpFailReason="�������ʻ�:"  + mainDto.getSpayeracct() + "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if(!mainDto.getSpayername().equals(tmppayacctinfoDto.getSpayername())){	// ����������
						this.tmpFailReason="����������:"  + mainDto.getSpayername() + "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if(!mainDto.getSpayeeacct().equals(tmppayacctinfoDto.getSpayeeacct())){	// �տ����˻�
						this.tmpFailReason="�տ����˻�:"  + mainDto.getSpayeeacct() + "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if(!mainDto.getSpayeename().equals(tmppayacctinfoDto.getSpayeename())){	// �տ�������
						this.tmpFailReason="�տ�������:"  + mainDto.getSpayeename() + "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
				}
			}
		}else if ( dto instanceof  TvPayreckbankbackSxDto) {
			TvPayreckbankbackSxDto mainDto=(TvPayreckbankbackSxDto) dto;
			// ���ڵ�һ�˻����ո����˵�У��
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(mainDto.getSbookorgcode()); // ��ȡ�ո�������Ϣ
			if (StringUtils.isBlank(mainDto.getSpaysndbnkno())) {
				this.tmpFailReason = "���Ĳ��淶, û���ҵ��տ��˿������к���Ϣ";
				return false;
			}else {
				TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap.get((mainDto.getStrecode()+mainDto.getSpaysndbnkno()+mainDto.getSpayeracct()+mainDto.getSpayeeacct()).trim());
				if (null == tmppayacctinfoDto) {
					this.tmpFailReason="���ݹ�����룺"+mainDto.getStrecode()+", �տ��˿������кţ�"+ mainDto.getSpaysndbnkno()+", �������˺ţ�"+mainDto.getSpayeracct()+", �տ����˺ţ�"+mainDto.getSpayeeacct()+ " û���ҵ��ո�������Ϣ!";
					return false;
				}else{
					if(!mainDto.getSpayeracct().equals(tmppayacctinfoDto.getSpayeracct())){	// �������ʻ�
						this.tmpFailReason="�������ʻ�:"  + mainDto.getSpayeracct() + "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if(!mainDto.getSpayername().equals(tmppayacctinfoDto.getSpayername())){	// ����������
						this.tmpFailReason="����������:"  + mainDto.getSpayername() + "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if(!mainDto.getSpayeeacct().equals(tmppayacctinfoDto.getSpayeeacct())){	// �տ����˻�
						this.tmpFailReason="�տ����˻�:"  + mainDto.getSpayeeacct() + "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
					if(!mainDto.getSpayeename().equals(tmppayacctinfoDto.getSpayeename())){	// �տ�������
						this.tmpFailReason="�տ�������:"  + mainDto.getSpayeename() + "��ά�����ո�������Ϣ�в�һ��!";
						return false;
					}
				}
			}
		}
		return true;
	}


	/**
	 * ʵ���ʽ�У����
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
			//Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
				//У��
				if(verifyTreasury(mainDto.getStrecode(), mainDto.getSorgcode())
						&& verifyAccName(mainDto.getSorgcode(), mainDto.getStrecode(), mainDto.getSpayeracct())
						&& verifySubject(mainDto.getSorgcode(), expFuncCodeList, vtCode)
						&& verifyCorpcodeList(mainDto.getSorgcode(), mainDto.getStrecode(), mainDto.getSbudgetunitcode(), vtCode)
					&& verifyPayeeBankNo(mainDto.getSrecbankno(), vtCode)
					){

					//����ƾ֤״̬
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), null, true);
					count++;
				}else{
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), this.tmpFailReason, false);
					
				}
			
		}
		return count;
	}
	/**
	 * ֱ��֧�����ҵ��У����
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
			//Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
				//У��
				if(verifyTreasury(mainDto.getStrecode(),mainDto.getSorgcode())
						&&verifyCorpcodeList(mainDto.getSorgcode(),tdcorpList)
						&&verifySubject(mainDto.getSorgcode(),expFuncCodeList, vtCode)
						&&verifyPayeeBankNo(mainDto.getStransactunit(), vtCode)//��������У��
				){
					//����ƾ֤״̬
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), null, true);
					returnList.add(vDto);
				}else{
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), this.tmpFailReason, false);
					
				}
			
		}
		return returnList;
	}
	/**
	 * ��Ȩ֧�����ҵ��У����
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
			//Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
			ArrayList<String> tdcorpList = (ArrayList<String>) list.get(3);
				//У��
				if(verifyTreasury(mainDto.getStrecode(),mainDto.getSorgcode())
						&&verifyCorpcodeList(mainDto.getSorgcode(),tdcorpList)
						&&verifySubject(mainDto.getSorgcode(),expFuncCodeList, vtCode)
						&&verifyPayeeBankNo(mainDto.getStransactunit(), vtCode)//��������У��
				){
					//����ƾ֤״̬
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), null, true);
					returnList.add(vDto);
				}else{
					VoucherUtil.voucherVerifyUpdateStatusSX(vDto,mainDto.tableName(), this.tmpFailReason, false);
					
				}
		}
		return returnList;
	}

	/**
	 * �����˸�У����
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
			//Ԥ���Ŀ����list
			ArrayList<String> expFuncCodeList = (ArrayList<String>) list.get(2);
		
				//У��
				if(verifyTreasury(mainDto.getSaimtrecode(), mainDto.getSbookorgcode())
						&& verifySfinOrgCode(mainDto.getStaxorgcode(), mainDto.getSbookorgcode())
						&& verifySubject(mainDto.getSbookorgcode(), expFuncCodeList, vtCode)						
						&& verifyPayeeBankNo(mainDto.getSpayeeopnbnkno(), vtCode)
						&& verifyStrelevel(mainDto)
					){

					//����ƾ֤״̬
					VoucherUtil.voucherVerifyUpdateStatus(vDto,mainDto.tableName(), null, true);
					count++;
				}else{
					VoucherUtil.voucherVerifyUpdateStatus(vDto,mainDto.tableName(), this.tmpFailReason, false);
					
				}
			
		}
		return count;
	}
	
	/**
	 * �ṩ���������������֤
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
			this.tmpFailReason="�����������"+trecode+"�ڹ���������Ϣ�����в�����!";
			return false;
		}
	}
	
	/**
	 * У�����Ԥ�㼶��	
	 * @return
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 * @throws ITFEBizException 
	 */
	public boolean verifyStrelevel(TvDwbkDto dto) throws JAFDatabaseException, ValidateException, ITFEBizException{
		HashMap<String, TsTreasuryDto> map = SrvCacheFacade.cacheTreasuryInfo(dto.getSbookorgcode());
			TsTreasuryDto tDto=map.get(dto.getSaimtrecode());
			if(tDto.getStrelevel()==null||tDto.getStrelevel().equals("")){
				this.tmpFailReason="���⣺"+tDto.getStrecode()+"Ԥ�㼶��δά����";
				return false;
			}
			dto.setCbdglevel(tDto.getStrelevel());
			DatabaseFacade.getODB().update(dto);
			return true;
		
	}
	
	/**
	 * У�鹦�ܿ�Ŀ����
	 * @param orgcode   ��������
	 * @param funccode  ���ܿ�Ŀ�б�
	 * @param vtCode    ҵ������
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
				this.tmpFailReason="��ϸ��Ϣ�д��ڹ��ܿ�Ŀ����Ϊ�յļ�¼!";
				return false;
			} 
			if (null == dto || "".equals(dto.getSsubjectcode())) {
				this.tmpFailReason="���ܿ�Ŀ����"+funccode+"������!";
				return false;
			}else{
				if(vtcode.equals(MsgConstant.VOUCHER_NO_5209)){
					if (!"1".equals(dto.getSsubjectclass())) {
						this.tmpFailReason="���ܿ�Ŀ����"+funccode+"���������Ŀ!";
						return false;
					}
				}else{
					if (!"2".equals(dto.getSsubjectclass())) {
						this.tmpFailReason="���ܿ�Ŀ����"+funccode+"����֧�����ܿ�Ŀ!";
						return false;
					}
				}
				if (!"1".equals(dto.getSwriteflag())) {
					this.tmpFailReason="���ܿ�Ŀ����"+funccode+"��¼���־Ϊ����¼��!";
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * ����Ԥ�㵥λ�����б�У��
	 * @param orgcode    ��������
	 * @param tdCorpList Ԥ�㵥λ�����б�
	 * @param vtCode     ҵ������
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
			this.tmpFailReason="����"+orgcode+"û��ά��Ԥ�㵥λ�������!";
			return false;
		}
		if("".equals(tdCorp) || tdCorp == null){
			this.tmpFailReason="Ԥ�㵥λ����Ϊ��!";
			return false;
		}
		if(vtCode.equals(MsgConstant.VOUCHER_NO_5207)){
			TdCorpDto cDto=map.get(trecode+tdCorp);
			if(cDto==null||!cDto.getScorpcode().equals(tdCorp)){
					this.tmpFailReason="Ԥ�㵥λ����"+tdCorp+"��Ԥ�㵥λ��������в�����!";
					return false;
				}else{
					if(!"1".equals(cDto.getCmayaprtfund().trim())){
						this.tmpFailReason="Ԥ�㵥λ����"+tdCorp+"���ܽ���ʵ���ʽ�!";
						return false;
					}
				}
			
		}else{
			if(!map.containsKey(trecode+tdCorp)){
					this.tmpFailReason="Ԥ�㵥λ����"+tdCorp+"��Ԥ�㵥λ��������в�����!";
					return false;
				}
			
		}
		return true;
	}
	
	
	/**
	 * У�鸶�����˺�
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ValidateException
	 * @throws ITFEBizException
	 */
	 
	public boolean verifyAccName(String bookorgCode, String strecode, String spayeraccount)throws JAFDatabaseException, ValidateException, ITFEBizException{
		Map<String, TsInfoconnorgaccDto> map = SrvCacheFacade.cacheFinTreAcctInfo();
		if(map.get(bookorgCode+spayeraccount) == null) {
			this.tmpFailReason="�������˺� "+spayeraccount+" û����'��������˻�����ά����ά��!";
			return false;
		}
		return true;
	}
	
	/**
	 * У���տ��˿�����
	
	 * @return
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 * @throws ITFEBizException 
	 */
	public boolean verifyPayeeBankNo(String PayeeAcctBankNo, String vtcode) throws JAFDatabaseException, ValidateException, ITFEBizException{
		if(MsgConstant.VOUCHER_NO_5207.equals(vtcode)||MsgConstant.VOUCHER_NO_5209.equals(vtcode)){
			if("".equals(PayeeAcctBankNo) ||PayeeAcctBankNo==null){//����û����д�кţ�������У��
				return true;
			}
		}else{
			if("".equals(PayeeAcctBankNo) ||PayeeAcctBankNo==null){
				this.tmpFailReason = "���д���Ϊ��";
				return false;
			}
		}
		HashMap<String, TsPaybankDto> bankmap = SrvCacheFacade.cachePayBankInfo();
		if(bankmap.get(PayeeAcctBankNo)==null){
			this.tmpFailReason = "�տ��к�"+PayeeAcctBankNo+"��֧��ϵͳ�к��в�����!";
			return false;
		}
		return true;
	}
	
	/**
	 * У�������������
	
	 * @return
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 * @throws ITFEBizException 
	 */
	public boolean verifySfinOrgCode(String SfinOrgCode,String sbookorgcode) throws JAFDatabaseException, ValidateException, ITFEBizException{
		if("".equals(SfinOrgCode) ||SfinOrgCode==null){
			this.tmpFailReason = "���Ĳ��淶��������������Ϊ��";
			return false;
		}
		HashMap<String, TsConvertfinorgDto> bankmap = SrvCacheFacade.cacheFincInfoByFinc(sbookorgcode);
		if(bankmap.get(SfinOrgCode)==null){
			this.tmpFailReason = "��������"+SfinOrgCode+"�ڲ��������в�����!";
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
	 * ���з��˴���У��
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
					//ȥ��Ԥ�㵥λУ���еĹ������
					tdCorp =tdCorp.substring(10);
					this.tmpFailReason="Ԥ�㵥λ����"+tdCorp+"��Ԥ�㵥λ��������в�����";
					return false;
				}
			}
		} else {
			this.tmpFailReason="����"+orgcode+"�ڷ��˴�������в�����";
			return false;
		}
		return true;
	}
	
	/**
	 * ������������У�飨����֧�������ã�
	 */
	public boolean verifyPaybankName(String orgcode,String treCode, String bankno, String bankname)
	throws JAFDatabaseException, ValidateException, ITFEBizException {
		if("".equals(bankname) || bankname == null){
			this.tmpFailReason = "�����������Ʋ���Ϊ�ա�";
			return false;
		}
		Map<String, TsConvertbanktypeDto> map = SrvCacheFacade.cacheTsconvertBankType(orgcode);
		if (map!=null && !map.isEmpty()) {
			if(map.containsKey(treCode+bankno)){
				String inputBankname = map.get(treCode+bankno).getSbankname();
				if(!bankname.equals(inputBankname)){
					this.tmpFailReason="������������"+bankname+"�롾���������к����б��Ӧ��ϵ���в�һ�¡�";
					return false;
				}
			}else{
				this.tmpFailReason="�������д���û���ڡ����������к����б��Ӧ��ϵ����ά����";
				return false;
			}
		} else {
			this.tmpFailReason="���������к����б��Ӧ��ϵû��ά��";
			return false;
		}
		return true;
	}
	
	/**
	 * У�鱨�Ĺؼ��ֶι����ֻࣨ���и�ʽУ�飩
	 * @param verifyDto
	 * @param vtcode
	 * @return
	 */
	public String checkValid(VoucherVerifyDto dto, String vtcode) {
		Pattern trecodePattern=Pattern.compile("[0-9]{10}");//ƥ��10λ����
		Pattern finorgcodePattern=Pattern.compile("[0-9]{1,12}");//ƥ��С��12λ����
		Pattern bankcodePattern=Pattern.compile("[0-9]{12}");//ƥ��12λ����
		Matcher match = null;
		StringBuffer sb = new StringBuffer() ;
		//�������
		if(StringUtils.isBlank(dto.getTrecode())){
			sb.append("������벻��Ϊ��");
		}else {
			match=trecodePattern.matcher(dto.getTrecode());
			if(match.matches()==false){
				sb.append("��������ʽ���󣬱���Ϊ10λ���֡�");
			}
		}
		//������������
		if(StringUtils.isBlank(dto.getFinorgcode())){
			sb.append("�����������벻��Ϊ��");
		}else {
			match=finorgcodePattern.matcher(dto.getFinorgcode());
			if(match.matches()==false){
				sb.append("�������������ʽ���󣬱���ΪС��12λ�����֡�");
			}
		}
		//���д���
		if(MsgConstant.VOUCHER_NO_5106.equals(vtcode) 
				|| MsgConstant.VOUCHER_NO_5108.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2301.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)){
			if(StringUtils.isBlank(dto.getPaybankno())){
				sb.append("�������д��벻��Ϊ��");
			}else {
				match=bankcodePattern.matcher(dto.getPaybankno());
				if(match.matches()==false){
					sb.append("�������д����ʽ���󣬱���Ϊ12λ�����֡�");
				}
			}
		}
		//ƾ֤����
		if(StringUtils.isBlank(dto.getVoudate())){
			sb.append("ƾ֤���ڲ���Ϊ��");
		}
		//ƾ֤���
		if(StringUtils.isBlank(dto.getVoucherno())){
			sb.append("ƾ֤��Ų���Ϊ��");
		}else if(MsgConstant.VOUCHER_NO_5209.equals(vtcode)){//�����˸�
			Pattern oldVouPattern=Pattern.compile("[0-9]{8}");//ƥ��8λ����
			if(ITFECommonConstant.IFNEWINTERFACE.equals("1")){//�½ӿ�
				if(dto.getVoucherno().length() > 20){
					sb.append("ƾ֤��ű���С��20λ");
				}
			}else{//�ɽӿ�
				match=oldVouPattern.matcher(dto.getVoucherno());
				if(match.matches()==false){
					sb.append("ƾ֤��Ÿ�ʽ���󣬱���Ϊ8λ�����֡�");
				}
			}
		}else{
			if(dto.getVoucherno().length() > 20){
				sb.append("ƾ֤��ű���С��20λ");
			}
		}
		
		//֧����ʽ
		if(MsgConstant.VOUCHER_NO_2301.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)){
			if(StringUtils.isBlank(dto.getPaytypecode().trim())){
				sb.append("֧����ʽ����Ϊ��");
			}else{
				if(!"11".equals(dto.getPaytypecode()) && !"12".equals(dto.getPaytypecode())){
					sb.append("֧����ʽֻ��Ϊ11����12");
				}
			}
		}
		if(MsgConstant.VOUCHER_NO_5207.equals(vtcode) 
				|| MsgConstant.VOUCHER_NO_5209.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2301.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)){
			//�տ������˺�
			if(StringUtils.isBlank(dto.getAgentAcctNo())){
				sb.append("�տ��˺Ų���Ϊ��");
			}else {
				if(dto.getAgentAcctNo().length()>60){
					sb.append("�տ��˺�["+dto.getAgentAcctNo()+"]��ʽ���󣬱���ΪС��32λ��");
				}
			}
			
			//�տ������˻�����
			if(StringUtils.isBlank(dto.getAgentAcctName())){
				sb.append("�տ��˺����Ʋ���Ϊ��");
			}else if(dto.getAgentAcctName().length()>60){
				sb.append("�տ��˺����Ƹ�ʽ���󣬱���ΪС��60λ��30���֣���");
			}
			
			//���������˺�
			if(StringUtils.isBlank(dto.getClearAcctNo())){
				sb.append("�����˺Ų���Ϊ��");
			}else {
				if(dto.getClearAcctNo().length() > 32){
					sb.append("�����˺�["+dto.getClearAcctNo()+"]��ʽ���󣬱���С��32λ��");
				}
			}
			
			//���������˻�����
			if(StringUtils.isBlank(dto.getClearAcctName())){
				sb.append("�����˺����Ʋ���Ϊ��");
			}else if(dto.getClearAcctName().length()>60){
				sb.append("�����˺����Ƹ�ʽ���󣬱���ΪС��60λ��30���֣���");
			}
		}
		String msg = sb.toString() ;
		if (msg.length() == 0){
			return null ;
		}
		return  msg;
	}

		
}

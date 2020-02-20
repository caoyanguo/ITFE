package com.cfcc.itfe.voucher.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.persistence.dto.TvAmtControlInfoDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class VoucherAmtUtil {
	private static Log logger = LogFactory.getLog(VoucherAmtUtil.class);
	
	/*
	 * �ж϶��ֵ���Ƿ�Ϊͬһ������
	 */
	private static Boolean isSameAmtDto(TvAmtControlInfoDto dto1,TvAmtControlInfoDto dto2){
		if(dto1.getStrecode().equals(dto2.getStrecode())&&
				dto1.getSbankcode().equals(dto2.getSbankcode())&&
				dto1.getSbudgetunitcode().equals(dto2.getSbudgetunitcode())&&
				dto1.getSfuncbdgsbtcode().equals(dto2.getSfuncbdgsbtcode())&&
				dto1.getSbudgettype().equals(dto2.getSbudgettype())&&
				dto1.getSyear().equals(dto2.getSyear())&&
				dto1.getSpaytypecode().equals(dto2.getSpaytypecode())){
			return true;
		}
		else
			return false;
	}
	 
	/*
	 * //�Ⱥϲ���ͬԤ�㵥λ-��Ŀ����Ľ��
	 */
	private  static List<TvAmtControlInfoDto> mergerVoucherMoney(List<TvAmtControlInfoDto> list){
		List<TvAmtControlInfoDto> returnList= null;
		if(list!=null&&list.size()>0){
			returnList = new ArrayList<TvAmtControlInfoDto>();
			Map<String,TvAmtControlInfoDto> map = new HashMap<String,TvAmtControlInfoDto>();
			String ls_Merger = "";
			BigDecimal mergerMoney =null;
			for(int i=0;i<list.size();i++){
				ls_Merger = list.get(i).getSbudgetunitcode()+"-"+list.get(i).getSfuncbdgsbtcode();
				if(map.containsKey(ls_Merger)){
					mergerMoney = map.get(ls_Merger).getNamt().add(list.get(i).getNamt());
					map.get(ls_Merger).setNamt(mergerMoney);
				}else{
					map.put(ls_Merger, list.get(i));
				}
			}
			Set<Map.Entry<String, TvAmtControlInfoDto>> set = map.entrySet();   
			for (Iterator<Map.Entry<String, TvAmtControlInfoDto>> it = set.iterator(); it.hasNext();) {  
				Map.Entry<String, TvAmtControlInfoDto> entry = (Map.Entry<String, TvAmtControlInfoDto>) it.next();    
				returnList.add(entry.getValue());
			}      
		}
		return returnList;
	}
	
	
	/*
	 * ����У����Ϣ�����¶��ֵ
	 */
	private static String returnAmtResultInfo(List<TvAmtControlInfoDto> list) throws ITFEBizException{
		StringBuffer ls_Result = new StringBuffer("");
		Boolean flag = true;
		if(list!=null&&list.size()>0){
			TvAmtControlInfoDto tempDto = null;
			List updateAmtInfoDto = new ArrayList();
			List newAmtInfoDto = new ArrayList();
			for(int i=0;i<list.size();i++){
				tempDto = list.get(i);
				String ls_Remark = tempDto.getSremark();
				if("F".equals(ls_Remark)){
					flag = false;
					ls_Result.append("Ԥ�㵥λ"+tempDto.getSbudgetunitcode()+"�й��ܿ�Ŀ"+tempDto.getSfuncbdgsbtcode()+"���ֵ"+tempDto.getNamt()+"�������㷶Χ��"+"\n");
				}
				//�����������޸ĵļ�¼
				if("NEW".equals(ls_Remark)){
					tempDto.setSremark("");
					newAmtInfoDto.add(tempDto);
				}else{
					updateAmtInfoDto.add(tempDto);
				}
			}
			if(flag){
				//�ֱ�����޸ĺ������Ķ����Ϣ
				try {
					if(updateAmtInfoDto.size()>0){				
						DatabaseFacade.getODB().update(CommonUtil.listTArray(updateAmtInfoDto));
					}
					if(newAmtInfoDto.size()>0){
						DatabaseFacade.getODB().create(CommonUtil.listTArray(newAmtInfoDto));						
					}
				} catch (JAFDatabaseException e) {
					logger.error(e);
					VoucherException voucherException = new VoucherException();
					voucherException.saveErrInfo("", e);
					throw new ITFEBizException("���¶���������쳣��",e);
				}
			}
		}
		return ls_Result.toString();
	}
	
	/**
	 * ��ȿ�����Ϣ��ѯ
	 * @param dto
	 * @return
	 */
	private static List<TvAmtControlInfoDto> getAmtControlInfoByDTO(List<TvAmtControlInfoDto> dtos)throws ITFEBizException{
		SQLExecutor sqlExecutor = null;
		List<TvAmtControlInfoDto> listResult = new ArrayList<TvAmtControlInfoDto>();
		List<TvAmtControlInfoDto> list = new ArrayList<TvAmtControlInfoDto>();
		if(dtos!=null&&dtos.size()>0){
			try{
				sqlExecutor  = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
				String sql = "SELECT * FROM TV_AMT_CONTROL_INFO WHERE S_TRECODE IN( SELECT S_TRECODE FROM TS_TREASURY WHERE S_ORGCODE = ? )  ";
				sqlExecutor.addParam(SrvCacheFacade.cacheTreasuryInfo(null).get(dtos.get(0).getStrecode()).getSorgcode());
				list = (List<TvAmtControlInfoDto>)sqlExecutor.runQueryCloseCon(sql, TvAmtControlInfoDto.class).getDtoCollection();
				for(TvAmtControlInfoDto dtoNew:dtos){
					for(TvAmtControlInfoDto dtoOld:list){
						if(isSameAmtDto(dtoOld, dtoNew)){
							listResult.add(dtoOld);
						}
					}
				}
			}catch (JAFDatabaseException e) {
				logger.error("��ѯ��ȿ�����Ϣ��ʱ�����쳣��", e);
				VoucherException voucherException = new VoucherException();
				voucherException.saveErrInfo("", e);
				throw new ITFEBizException("��ѯ��ȿ�����Ϣ��ʱ�����쳣",e);
			}finally{
				if(sqlExecutor!=null){
					sqlExecutor.closeConnection();
				}
			}
		}
		return listResult;
	}
	
	/*
	 * //��֤ԭ����ö��ֵ
	 *	//�Ӳ�ѯ���Ľ����У�����Ƿ���ִ�еķ�Χ��
	 * list��ǰҪУ��Ķ��list��
	 * oldList����list��ѯ����ԭ�ж���б�
	 */
	private static List<TvAmtControlInfoDto> verifyVoucherMoneyResutl(List<TvAmtControlInfoDto> list,List<TvAmtControlInfoDto> oldDtos, String voucherType){
		//���У��ʧ�ܵĶ����Ϣֵ��Ҫ���µĶ����Ϣֵ�����ֶα�עsetSremark����ʱ���У��ʧ�ܵı�־��F��
		List<TvAmtControlInfoDto> listResult = new ArrayList();
		BigDecimal bigDecimal0 = new BigDecimal(0);//�Ƚ�ʱ�õĻ�������0
		TvAmtControlInfoDto tempDto = null;//ҪУ��Ķ����Ϣ
		//��֤ԭ����ö��ֵ
		for(TvAmtControlInfoDto oldDto:oldDtos){
			for(int i=0;i<list.size();i++){
				if(isSameAmtDto(oldDto,list.get(i))){
					tempDto = list.get(i);
					BigDecimal bigDecimalResult = oldDto.getNamt().add(tempDto.getNamt());
					if(voucherType.equals(MsgConstant.VOUCHER_NO_5106)||voucherType.equals(MsgConstant.VOUCHER_NO_5108)){
						//�������Ϊ��ֵ�򳬳���ȷ�Χ��У��ʧ��
						if(bigDecimal0.compareTo(bigDecimalResult)==1){
							tempDto.setSremark("F");
							listResult.add(tempDto);
						}else{
							tempDto.setNamt(bigDecimalResult);
							listResult.add(tempDto);
						}
					}if(voucherType.equals(MsgConstant.VOUCHER_NO_2302)){
						//���Ϊ��ֵ��У��ʧ��
						if(bigDecimal0.compareTo(tempDto.getNamt())==1){
							tempDto.setSremark("F");
							listResult.add(tempDto);
						}else{
							tempDto.setNamt(bigDecimalResult);
							listResult.add(tempDto);
						}
					}else if(voucherType.equals(MsgConstant.VOUCHER_NO_2301)){
						//���������ȶ��ֵҪ��Ϊ��ֵ�����Ϊ��ֵ��У��ʧ��
						if(bigDecimal0.compareTo(tempDto.getNamt())==1){
							tempDto.setSremark("F");
							listResult.add(tempDto);
						}else{
							BigDecimal subtractResult = oldDto.getNamt().subtract(tempDto.getNamt());
							//���ֵ�������򷵻�ʧ�ܣ��ڶ�ȵķ�Χ�ڣ���ۼ����
							if(bigDecimal0.compareTo(subtractResult)==1){
								tempDto.setSremark("F");
								listResult.add(tempDto);
							}else{
								tempDto.setNamt(subtractResult);
								listResult.add(tempDto);
							}
						}
					}
					list.remove(i);
					i--;
				}
			}
		}
		
		//��֤������ȵĶ��ֵ
		//����ڽ���в����ڣ�˵���˶��ֵ��һ�γ���
		if(list.size()>0){
			for(TvAmtControlInfoDto newDto:list){
				if(voucherType.equals(MsgConstant.VOUCHER_NO_5106)||voucherType.equals(MsgConstant.VOUCHER_NO_5108)||voucherType.equals(MsgConstant.VOUCHER_NO_2302)){
					//���Ϊ��ֵ��У��ʧ��
					if(bigDecimal0.compareTo(newDto.getNamt())==1){
						newDto.setSremark("F");
						listResult.add(newDto);
					}else{
						newDto.setSremark("NEW");
						listResult.add(newDto);
					}
				}if(voucherType.equals(MsgConstant.VOUCHER_NO_2301)||voucherType.equals(MsgConstant.VOUCHER_NO_2302)){
					//��Ϊû�ж�ȣ����ܳ���������Ϣ
					newDto.setSremark("F");
					listResult.add(newDto);
				}
			}
		}
		return listResult;
	}
	
	
	/*
	 * ����ƾ֤��Ϣ��ѯƾ֤��ϸ�еĶ��ֵ��Ϣ
	 */
	private static Map<TvVoucherinfoDto,List<TvAmtControlInfoDto>> getAmtInfoByVoucher(List<TvVoucherinfoDto> voucherList,String voucherType) throws ITFEBizException{
		//Ҫ���ص�ƾ֤��Ϣ�Ͷ�Ӧ�Ķ��ֵ��Ϣ
		Map<TvVoucherinfoDto,List<TvAmtControlInfoDto>> voucherAndAmtDtosMap = null;
		if(voucherList.size()>0){
			voucherAndAmtDtosMap = new HashMap<TvVoucherinfoDto,List<TvAmtControlInfoDto>> ();
			//��ѯvoucherList��Ӧ��ҵ���list
			String ls_Select = "and I_VOUSRLNO in ( ";
			for(TvVoucherinfoDto voucherDto:voucherList){
				String ls_DealNo  = voucherDto.getSdealno();
				ls_Select+=ls_DealNo+"," ;
			}
			ls_Select = ls_Select.substring(0,ls_Select.length()-1)+")";
			IDto  dto = VoucherUtil.returnDtoByVtcode(voucherType);
			List dtoList  = null;
			try {
				dtoList = CommonFacade.getODB().findRsByDtoForWhere(dto,ls_Select);
				if(dtoList.size()>0){
					//�������Ȩ֧�����
					if( dto instanceof TvGrantpaymsgmainDto){
						for(TvVoucherinfoDto voucherDto: voucherList){
							//����漰��ƾ֤��֣����Ը�����ˮ�Ų�ѯ���Ķ����������ΪList
							List<TvGrantpaymsgmainDto> mainDtoList = new ArrayList<TvGrantpaymsgmainDto>();
							for(int i=0;i<dtoList.size();i++){
								TvGrantpaymsgmainDto tempMainDto = (TvGrantpaymsgmainDto)dtoList.get(i);
								if(tempMainDto.getIvousrlno()==Long.parseLong(voucherDto.getSdealno())){
									mainDtoList.add(tempMainDto);
								}
							}
							TvGrantpaymsgsubDto subDto = new TvGrantpaymsgsubDto();
							List<TvAmtControlInfoDto> amtList  = new ArrayList<TvAmtControlInfoDto>();
							for(TvGrantpaymsgmainDto tempMainDto:mainDtoList){
								subDto.setIvousrlno(tempMainDto.getIvousrlno());
								subDto.setSpackageticketno(tempMainDto.getSpackageticketno());
								//����������Ϣ��ѯ�ֱ�
								List<TvGrantpaymsgsubDto> tempSubDtoList = CommonFacade.getODB().findRsByDto(subDto);
								for (TvGrantpaymsgsubDto sDto : tempSubDtoList) {
									//�������Ϣ
									TvAmtControlInfoDto amtDto = new TvAmtControlInfoDto();
									amtDto.setStrecode(tempMainDto.getStrecode());
									amtDto.setSbankcode(tempMainDto.getSpaybankno());
									amtDto.setSbudgetunitcode(tempMainDto.getSbudgetunitcode());
									amtDto.setSfuncbdgsbtcode(sDto.getSfunsubjectcode());
									amtDto.setSyear(tempMainDto.getSofyear());
									amtDto.setNamt(sDto.getNmoney());
									amtDto.setSpaytypecode(MsgConstant.grantPay);
									amtDto.setSbudgettype(tempMainDto.getSbudgettype());
									amtList.add(amtDto);
								}
							}
							voucherAndAmtDtosMap.put(voucherDto, amtList);
						}
					}else if( dto instanceof TvDirectpaymsgmainDto){
						//���Ϊֱ��֧�����ƾ֤
						for(TvVoucherinfoDto voucherDto: voucherList){
							TvDirectpaymsgmainDto mainDto = new TvDirectpaymsgmainDto();
							TvDirectpaymsgsubDto subDto = new TvDirectpaymsgsubDto();
							List<TvDirectpaymsgsubDto> subList = new ArrayList<TvDirectpaymsgsubDto>();
							subDto.setIvousrlno(Long.parseLong(voucherDto.getSdealno()));
							mainDto.setIvousrlno(Long.parseLong(voucherDto.getSdealno()));
							// ����
							mainDto = (TvDirectpaymsgmainDto) CommonFacade.getODB().findRsByDto(mainDto).get(0);					
							subList = CommonFacade.getODB().findRsByDto(subDto);
							//�����ȿ�����Ҫ�Ķ����ϢList
							List<TvAmtControlInfoDto> amtList  = new ArrayList<TvAmtControlInfoDto>();
							for (TvDirectpaymsgsubDto sDto : subList) {
								TvAmtControlInfoDto amtDto = new TvAmtControlInfoDto();
								amtDto.setStrecode(mainDto.getStrecode());
								amtDto.setSbankcode(mainDto.getSpaybankno());
								amtDto.setSbudgetunitcode(sDto.getSbudgetunitcode());
								amtDto.setSfuncbdgsbtcode(sDto.getSfunsubjectcode());
								amtDto.setSyear(mainDto.getSofyear());
								amtDto.setNamt(sDto.getNmoney());
								amtDto.setSpaytypecode(MsgConstant.directPay);
								amtDto.setSbudgettype(mainDto.getSbudgettype());
								amtList.add(amtDto);
							}
							voucherAndAmtDtosMap.put(voucherDto, amtList);
						}
					}else if( dto instanceof TvPayreckBankDto ){
						//���Ϊ��������ƾ֤
						for(TvVoucherinfoDto voucherDto: voucherList){
							TvPayreckBankDto mainDto = new TvPayreckBankDto();
							TvPayreckBankListDto subDto = new TvPayreckBankListDto();
							List<TvPayreckBankListDto> subList = new ArrayList<TvPayreckBankListDto>();
							subDto.setIvousrlno(Long.valueOf(voucherDto.getSdealno()));
							mainDto.setIvousrlno(Long.parseLong(voucherDto.getSdealno()));
							// ����
							mainDto = (TvPayreckBankDto) CommonFacade.getODB().findRsByDto(mainDto).get(0);
							subList = CommonFacade.getODB().findRsByDto(subDto);
							//�����ȿ�����Ҫ�Ķ����ϢList
							List<TvAmtControlInfoDto> amtList  = new ArrayList<TvAmtControlInfoDto>();
							for (TvPayreckBankListDto sDto : subList) {
								TvAmtControlInfoDto amtDto = new TvAmtControlInfoDto();
								amtDto.setStrecode(mainDto.getStrecode());
								amtDto.setSbankcode(mainDto.getSagentbnkcode());
								amtDto.setSbudgetunitcode(sDto. getSbdgorgcode());
								amtDto.setSfuncbdgsbtcode(sDto.getSfuncbdgsbtcode());
								amtDto.setSyear(mainDto.getSofyear());
								amtDto.setNamt(sDto.getFamt());
								amtDto.setSpaytypecode(mainDto.getSpaymode());
								amtDto.setSbudgettype(mainDto.getSbudgettype());
								amtList.add(amtDto);
							}
							voucherAndAmtDtosMap.put(voucherDto, amtList);
						}
					}else if( dto instanceof  TvPayreckBankBackDto ){
						//���Ϊ���������˿�ƾ֤
						for(TvVoucherinfoDto voucherDto: voucherList){
							TvPayreckBankBackDto mainDto = new TvPayreckBankBackDto();
							TvPayreckBankBackListDto subDto = new TvPayreckBankBackListDto();
							List<TvPayreckBankBackListDto> subList = new ArrayList<TvPayreckBankBackListDto>();
							subDto.setIvousrlno(Long.valueOf(voucherDto.getSdealno()));
							mainDto.setIvousrlno(Long.valueOf(voucherDto.getSdealno()));
							// ����
							mainDto = (TvPayreckBankBackDto) CommonFacade.getODB().findRsByDto(mainDto).get(0);
							subList = CommonFacade.getODB().findRsByDto(subDto);
							//�����ȿ�����Ҫ�Ķ����ϢList
							List<TvAmtControlInfoDto> amtList  = new ArrayList<TvAmtControlInfoDto>();
							for (TvPayreckBankBackListDto sDto : subList) {
								TvAmtControlInfoDto amtDto = new TvAmtControlInfoDto();
								amtDto.setStrecode(mainDto.getStrecode());
								amtDto.setSbankcode(mainDto.getSagentbnkcode());
								amtDto.setSbudgetunitcode(sDto. getSbdgorgcode());
								amtDto.setSfuncbdgsbtcode(sDto.getSfuncbdgsbtcode());
								amtDto.setSyear(mainDto.getSofyear());
								amtDto.setNamt(sDto.getFamt());
								amtDto.setSpaytypecode(mainDto.getSpaymode());
								amtDto.setSbudgettype(mainDto.getSbudgettype());
								amtList.add(amtDto);
							}
							voucherAndAmtDtosMap.put(voucherDto, amtList);
						}
					}
				}
			} catch (JAFDatabaseException e){
				logger.error("��ѯҵ����Ϣ��ʱ�����쳣��", e);
				VoucherException voucherException = new VoucherException();
				voucherException.saveErrInfo("", e);
				throw new ITFEBizException("��ѯҵ����Ϣ��ʱ�����쳣",e);
			} catch (ValidateException e){
				logger.error("��ѯҵ����Ϣ��ʱ�����쳣��", e);
				VoucherException voucherException = new VoucherException();
				voucherException.saveErrInfo("", e);
				throw new ITFEBizException("��ѯҵ����Ϣ��ʱ�����쳣",e);
			}
		}
		return voucherAndAmtDtosMap;
	}
	
	/*
	 * //
	 *	�˻�ʱ�ָ����
	 * list��ǰҪ���µĶ��list��
	 * oldList����list��ѯ����ԭ�ж���б�
	 */
	private static String backVCucherUpdateMoney(List<TvAmtControlInfoDto> list,List<TvAmtControlInfoDto> oldDtos, String voucherType){
		String ls_Result = "";
		Boolean flag = true;
		//���Ҫ���µĶ����Ϣ
		List<TvAmtControlInfoDto> updateAmtList = new ArrayList();
		//���У��ʧ�ܵĶ����Ϣlist
		List<TvAmtControlInfoDto> failAmtList = new ArrayList();
		BigDecimal bigDecimal0 = new BigDecimal(0);//�Ƚ�ʱ�õĻ�������0
		TvAmtControlInfoDto tempDto = null;//Ҫ���µĶ����Ϣ
		//��֤ԭ����ö��ֵ
		for(TvAmtControlInfoDto oldDto:oldDtos){
			for(int i=0;i<list.size();i++){
				if(isSameAmtDto(oldDto,list.get(i))){
					tempDto = list.get(i);
					if(voucherType.equals(MsgConstant.VOUCHER_NO_5106)
							||voucherType.equals(MsgConstant.VOUCHER_NO_5108)
							||voucherType.equals(MsgConstant.VOUCHER_NO_2302)){
						//���������Ϊ�������ֵ�Ѿ�ʹ�ã�ƾ֤���ܱ��˻�
						BigDecimal bigDecimalResult = oldDto.getNamt().subtract(tempDto.getNamt());
						if(bigDecimal0.compareTo(bigDecimalResult)==1){
							//������Ϊ�������ֵ�Ѿ�ʹ�ã�ƾ֤���ܱ��˻�
							tempDto.setSremark("Ԥ�㵥λ��"+tempDto.getSbudgetunitcode()+"���ֵ��"+tempDto.getNamt()+"�Ѿ�ʹ�ò��ܱ��˻أ�"+"\n");
							failAmtList.add(tempDto);
						}else{
							tempDto.setNamt(bigDecimalResult);
							updateAmtList.add(tempDto);
						}
					}else if(voucherType.equals(MsgConstant.VOUCHER_NO_2301)){
						BigDecimal bigDecimalResult = oldDto.getNamt().add(tempDto.getNamt());
						tempDto.setNamt(bigDecimalResult);
						updateAmtList.add(tempDto);
					}
					list.remove(i);
					i--;
				}
			}
		}
		//��Ҫ���µĶ��ֵ
		if(failAmtList.size()<=0){
			TvAmtControlInfoDto[] amtDtos=new TvAmtControlInfoDto[updateAmtList.size()];
			amtDtos=(TvAmtControlInfoDto[]) updateAmtList.toArray(amtDtos);
			try {
				DatabaseFacade.getODB().update(amtDtos);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				VoucherException voucherException = new VoucherException();
				voucherException.saveErrInfo("", e);
				flag = false;
			}
		}else{
			for(int i=0;i<failAmtList.size();i++){
				ls_Result +=failAmtList.get(i).getSremark();
			}
		}
		return ls_Result;
	}
	
	/*��ȿ��Ƶķ������ύ�������˻ز���ʱ����
	 * status����״̬������ƾ֤״̬[90] �˻سɹ�����Ҫ���˻ز�����ƾ֤״̬[71] ����ɹ�Ҫ���ύ����
	 * �˻�ʱУ����ֵ�����ֵҪ�ָ����ۼ�����漰����Ȩ֧����ȣ�ֱ��֧����Ⱥͻ��������˿����������Ҫ�ָ���ȣ����ӣ�
	 * voucherTypeƾ֤����
	 * ע��У��ʧ�ܵ�ƾ֤�˻�ʱ����Ҫ���ж�ȵĿ���
	 * List<TvVoucherinfoDto> voucherResult����У��ʧ�ܵļ�¼��ֵΪnull����size==0�����˻�ʱ���У��ɹ���size��Ϊ0������У��ʧ�ܵļ�¼
	 * TvVoucherinfoDto����У��ʧ�ܵ���Ϣ��sdemo�д洢
	 */
	public static List<TvVoucherinfoDto> updateAmtByVoucher(List<TvVoucherinfoDto> voucherList,String voucherType,String status) throws ITFEBizException{
		
		//����У��ʧ�ܵļ�¼
		List<TvVoucherinfoDto> voucherResult  = new ArrayList<TvVoucherinfoDto>();
		//��voucherListȥ��״̬Ϊ��У��ɹ�������У��ʧ�ܡ�,��TCBS����ʧ�ܡ���ƾ֤���ⲿ��ƾ֤�������ȿ����߼�
		for(int i=0;i<voucherList.size();i++){
			if(DealCodeConstants.VOUCHER_VALIDAT_FAIL.equals(voucherList.get(i).getSstatus())){
				voucherList.remove(i);
				i--;
			}
		}
		
		if(DealCodeConstants.VOUCHER_FAIL.equals(status)){
			for(int i=0;i<voucherList.size();i++){
				if(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS.equals(voucherList.get(i).getSstatus())||DealCodeConstants.VOUCHER_FAIL_TCBS.equals(voucherList.get(i).getSstatus())||DealCodeConstants.VOUCHER_FAIL_TIPS.equals(voucherList.get(i).getSstatus())||DealCodeConstants.VOUCHER_RECEIVE_SUCCESS.equals(voucherList.get(i).getSstatus())){
					voucherList.remove(i);
					i--;
				}
			}
		}
		
		if(voucherList.size()>0){
			
			
			//��ѯ��ѡ��ƾ֤��ϸ�еĶ��ֵ��Ϣ
			Map<TvVoucherinfoDto,List<TvAmtControlInfoDto>> voucherAndAmtDtosMap = getAmtInfoByVoucher(voucherList,voucherType);
			if(voucherAndAmtDtosMap.size()>0){
				
				Set<Map.Entry<TvVoucherinfoDto,List<TvAmtControlInfoDto>>> voucherSet = voucherAndAmtDtosMap.entrySet();   
				for (Iterator<Entry<TvVoucherinfoDto, List<TvAmtControlInfoDto>>> it = voucherSet.iterator(); it.hasNext();) {  
					Map.Entry<TvVoucherinfoDto,List<TvAmtControlInfoDto>> entry = (Map.Entry<TvVoucherinfoDto,List<TvAmtControlInfoDto>>) it.next();    
					TvVoucherinfoDto voucherDto = entry.getKey();
					List<TvAmtControlInfoDto> amtDtoList = entry.getValue();
					//�Ⱥϲ���ͬԤ�㵥λ-��Ŀ����Ľ��
					amtDtoList = mergerVoucherMoney(amtDtoList);
					if(amtDtoList!=null&&amtDtoList.size()>0){
						//��ѯԭ�ж����Ϣ�б�
						List<TvAmtControlInfoDto> oldListDtos=null;
						try {
							oldListDtos = getAmtControlInfoByDTO(amtDtoList);
							String ls_Result = "";
							if(DealCodeConstants.VOUCHER_FAIL.equals(status)){
								//�˻�
								ls_Result = backVCucherUpdateMoney(amtDtoList,oldListDtos, voucherType);
							}else{
								//�ύʱ��ȿ���
								List<TvAmtControlInfoDto> resultDtos  = verifyVoucherMoneyResutl(amtDtoList,oldListDtos, voucherType);
								//����ִ�ж�ȼ�����
								ls_Result = returnAmtResultInfo(resultDtos);
							}
							if(!ls_Result.equals("")){
								if(ls_Result.length()>512)
									ls_Result = ls_Result.substring(0, 512);
								voucherDto.setSdemo(ls_Result);
								voucherResult.add(voucherDto);
							}
						} catch (ITFEBizException e) {
							logger.error(e);
							VoucherException voucherException = new VoucherException();
							voucherException.saveErrInfo("", e);
							throw new  ITFEBizException("��ȿ���ʱ�����쳣��",e);
						}
					}
				}
			}else{
				for(TvVoucherinfoDto dto:voucherList){
					dto.setSdemo("����ƾ֤��δ��ѯ��ҵ������ݣ�");
				}
				voucherResult.addAll(voucherList);
			}
		}
		return voucherResult;
	}
	
}

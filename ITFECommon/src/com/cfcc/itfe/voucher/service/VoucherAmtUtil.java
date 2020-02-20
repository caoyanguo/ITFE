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
	 * 判断额度值类是否为同一个对象
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
	 * //先合并相同预算单位-科目代码的金额
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
	 * 返回校验信息并更新额度值
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
					ls_Result.append("预算单位"+tempDto.getSbudgetunitcode()+"中功能科目"+tempDto.getSfuncbdgsbtcode()+"额度值"+tempDto.getNamt()+"超出清算范围！"+"\n");
				}
				//分离新增和修改的记录
				if("NEW".equals(ls_Remark)){
					tempDto.setSremark("");
					newAmtInfoDto.add(tempDto);
				}else{
					updateAmtInfoDto.add(tempDto);
				}
			}
			if(flag){
				//分别更新修改和新增的额度信息
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
					throw new ITFEBizException("更新额度余额出现异常！",e);
				}
			}
		}
		return ls_Result.toString();
	}
	
	/**
	 * 额度控制信息查询
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
				logger.error("查询额度控制信息表时出现异常：", e);
				VoucherException voucherException = new VoucherException();
				voucherException.saveErrInfo("", e);
				throw new ITFEBizException("查询额度控制信息表时出现异常",e);
			}finally{
				if(sqlExecutor!=null){
					sqlExecutor.closeConnection();
				}
			}
		}
		return listResult;
	}
	
	/*
	 * //验证原有相得额度值
	 *	//从查询到的结果中校验额度是否在执行的范围内
	 * list当前要校验的额度list，
	 * oldList根据list查询出的原有额度列表
	 */
	private static List<TvAmtControlInfoDto> verifyVoucherMoneyResutl(List<TvAmtControlInfoDto> list,List<TvAmtControlInfoDto> oldDtos, String voucherType){
		//存放校验失败的额度信息值和要更新的额度信息值，在字段备注setSremark中暂时存放校验失败的标志“F”
		List<TvAmtControlInfoDto> listResult = new ArrayList();
		BigDecimal bigDecimal0 = new BigDecimal(0);//比较时用的基础数据0
		TvAmtControlInfoDto tempDto = null;//要校验的额度信息
		//验证原有相得额度值
		for(TvAmtControlInfoDto oldDto:oldDtos){
			for(int i=0;i<list.size();i++){
				if(isSameAmtDto(oldDto,list.get(i))){
					tempDto = list.get(i);
					BigDecimal bigDecimalResult = oldDto.getNamt().add(tempDto.getNamt());
					if(voucherType.equals(MsgConstant.VOUCHER_NO_5106)||voucherType.equals(MsgConstant.VOUCHER_NO_5108)){
						//如果计算为负值则超出额度范围，校验失败
						if(bigDecimal0.compareTo(bigDecimalResult)==1){
							tempDto.setSremark("F");
							listResult.add(tempDto);
						}else{
							tempDto.setNamt(bigDecimalResult);
							listResult.add(tempDto);
						}
					}if(voucherType.equals(MsgConstant.VOUCHER_NO_2302)){
						//如果为负值，校验失败
						if(bigDecimal0.compareTo(tempDto.getNamt())==1){
							tempDto.setSremark("F");
							listResult.add(tempDto);
						}else{
							tempDto.setNamt(bigDecimalResult);
							listResult.add(tempDto);
						}
					}else if(voucherType.equals(MsgConstant.VOUCHER_NO_2301)){
						//划款申请额度额度值要求不为负值，如果为负值则校验失败
						if(bigDecimal0.compareTo(tempDto.getNamt())==1){
							tempDto.setSremark("F");
							listResult.add(tempDto);
						}else{
							BigDecimal subtractResult = oldDto.getNamt().subtract(tempDto.getNamt());
							//额度值不够，则返回失败，在额度的范围内，则扣减额度
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
		
		//验证新增额度的额度值
		//如果在结果中不存在，说明此额度值第一次出现
		if(list.size()>0){
			for(TvAmtControlInfoDto newDto:list){
				if(voucherType.equals(MsgConstant.VOUCHER_NO_5106)||voucherType.equals(MsgConstant.VOUCHER_NO_5108)||voucherType.equals(MsgConstant.VOUCHER_NO_2302)){
					//如果为负值则校验失败
					if(bigDecimal0.compareTo(newDto.getNamt())==1){
						newDto.setSremark("F");
						listResult.add(newDto);
					}else{
						newDto.setSremark("NEW");
						listResult.add(newDto);
					}
				}if(voucherType.equals(MsgConstant.VOUCHER_NO_2301)||voucherType.equals(MsgConstant.VOUCHER_NO_2302)){
					//因为没有额度，不能出现清算信息
					newDto.setSremark("F");
					listResult.add(newDto);
				}
			}
		}
		return listResult;
	}
	
	
	/*
	 * 根据凭证信息查询凭证明细中的额度值信息
	 */
	private static Map<TvVoucherinfoDto,List<TvAmtControlInfoDto>> getAmtInfoByVoucher(List<TvVoucherinfoDto> voucherList,String voucherType) throws ITFEBizException{
		//要返回的凭证信息和对应的额度值信息
		Map<TvVoucherinfoDto,List<TvAmtControlInfoDto>> voucherAndAmtDtosMap = null;
		if(voucherList.size()>0){
			voucherAndAmtDtosMap = new HashMap<TvVoucherinfoDto,List<TvAmtControlInfoDto>> ();
			//查询voucherList对应的业务表list
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
					//如果是授权支付额度
					if( dto instanceof TvGrantpaymsgmainDto){
						for(TvVoucherinfoDto voucherDto: voucherList){
							//额度涉及到凭证拆分，所以根据流水号查询出的额度主表数据为List
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
								//根据主表信息查询字表
								List<TvGrantpaymsgsubDto> tempSubDtoList = CommonFacade.getODB().findRsByDto(subDto);
								for (TvGrantpaymsgsubDto sDto : tempSubDtoList) {
									//填充额度信息
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
						//如果为直接支付额度凭证
						for(TvVoucherinfoDto voucherDto: voucherList){
							TvDirectpaymsgmainDto mainDto = new TvDirectpaymsgmainDto();
							TvDirectpaymsgsubDto subDto = new TvDirectpaymsgsubDto();
							List<TvDirectpaymsgsubDto> subList = new ArrayList<TvDirectpaymsgsubDto>();
							subDto.setIvousrlno(Long.parseLong(voucherDto.getSdealno()));
							mainDto.setIvousrlno(Long.parseLong(voucherDto.getSdealno()));
							// 主表
							mainDto = (TvDirectpaymsgmainDto) CommonFacade.getODB().findRsByDto(mainDto).get(0);					
							subList = CommonFacade.getODB().findRsByDto(subDto);
							//构造额度控制需要的额度信息List
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
						//如果为划款申请凭证
						for(TvVoucherinfoDto voucherDto: voucherList){
							TvPayreckBankDto mainDto = new TvPayreckBankDto();
							TvPayreckBankListDto subDto = new TvPayreckBankListDto();
							List<TvPayreckBankListDto> subList = new ArrayList<TvPayreckBankListDto>();
							subDto.setIvousrlno(Long.valueOf(voucherDto.getSdealno()));
							mainDto.setIvousrlno(Long.parseLong(voucherDto.getSdealno()));
							// 主表
							mainDto = (TvPayreckBankDto) CommonFacade.getODB().findRsByDto(mainDto).get(0);
							subList = CommonFacade.getODB().findRsByDto(subDto);
							//构造额度控制需要的额度信息List
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
						//如果为划款申请退款凭证
						for(TvVoucherinfoDto voucherDto: voucherList){
							TvPayreckBankBackDto mainDto = new TvPayreckBankBackDto();
							TvPayreckBankBackListDto subDto = new TvPayreckBankBackListDto();
							List<TvPayreckBankBackListDto> subList = new ArrayList<TvPayreckBankBackListDto>();
							subDto.setIvousrlno(Long.valueOf(voucherDto.getSdealno()));
							mainDto.setIvousrlno(Long.valueOf(voucherDto.getSdealno()));
							// 主表
							mainDto = (TvPayreckBankBackDto) CommonFacade.getODB().findRsByDto(mainDto).get(0);
							subList = CommonFacade.getODB().findRsByDto(subDto);
							//构造额度控制需要的额度信息List
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
				logger.error("查询业务信息表时出现异常：", e);
				VoucherException voucherException = new VoucherException();
				voucherException.saveErrInfo("", e);
				throw new ITFEBizException("查询业务信息表时出现异常",e);
			} catch (ValidateException e){
				logger.error("查询业务信息表时出现异常：", e);
				VoucherException voucherException = new VoucherException();
				voucherException.saveErrInfo("", e);
				throw new ITFEBizException("查询业务信息表时出现异常",e);
			}
		}
		return voucherAndAmtDtosMap;
	}
	
	/*
	 * //
	 *	退回时恢复额度
	 * list当前要更新的额度list，
	 * oldList根据list查询出的原有额度列表
	 */
	private static String backVCucherUpdateMoney(List<TvAmtControlInfoDto> list,List<TvAmtControlInfoDto> oldDtos, String voucherType){
		String ls_Result = "";
		Boolean flag = true;
		//存放要更新的额度信息
		List<TvAmtControlInfoDto> updateAmtList = new ArrayList();
		//存放校验失败的额度信息list
		List<TvAmtControlInfoDto> failAmtList = new ArrayList();
		BigDecimal bigDecimal0 = new BigDecimal(0);//比较时用的基础数据0
		TvAmtControlInfoDto tempDto = null;//要更新的额度信息
		//验证原有相得额度值
		for(TvAmtControlInfoDto oldDto:oldDtos){
			for(int i=0;i<list.size();i++){
				if(isSameAmtDto(oldDto,list.get(i))){
					tempDto = list.get(i);
					if(voucherType.equals(MsgConstant.VOUCHER_NO_5106)
							||voucherType.equals(MsgConstant.VOUCHER_NO_5108)
							||voucherType.equals(MsgConstant.VOUCHER_NO_2302)){
						//如果计算结果为负，额度值已经使用，凭证不能被退回
						BigDecimal bigDecimalResult = oldDto.getNamt().subtract(tempDto.getNamt());
						if(bigDecimal0.compareTo(bigDecimalResult)==1){
							//计算结果为负，额度值已经使用，凭证不能被退回
							tempDto.setSremark("预算单位："+tempDto.getSbudgetunitcode()+"额度值："+tempDto.getNamt()+"已经使用不能被退回；"+"\n");
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
		//需要更新的额度值
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
	
	/*额度控制的方法，提交操作和退回操作时调用
	 * status操作状态有两个凭证状态[90] 退回成功代表要做退回操作，凭证状态[71] 处理成功要做提交操作
	 * 退回时校验额度值，额度值要恢复（扣减额度涉及到授权支付额度，直接支付额度和划款申请退款），划款申请要恢复额度（增加）
	 * voucherType凭证类型
	 * 注意校验失败的凭证退回时不需要进行额度的控制
	 * List<TvVoucherinfoDto> voucherResult返回校验失败的记录，值为null或者size==0代表退回时额度校验成功，size不为0代表有校验失败的记录
	 * TvVoucherinfoDto对象校验失败的信息在sdemo中存储
	 */
	public static List<TvVoucherinfoDto> updateAmtByVoucher(List<TvVoucherinfoDto> voucherList,String voucherType,String status) throws ITFEBizException{
		
		//返回校验失败的记录
		List<TvVoucherinfoDto> voucherResult  = new ArrayList<TvVoucherinfoDto>();
		//从voucherList去除状态为“校验成功”，“校验失败”,“TCBS处理失败”的凭证，这部分凭证不参与额度控制逻辑
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
			
			
			//查询出选中凭证明细中的额度值信息
			Map<TvVoucherinfoDto,List<TvAmtControlInfoDto>> voucherAndAmtDtosMap = getAmtInfoByVoucher(voucherList,voucherType);
			if(voucherAndAmtDtosMap.size()>0){
				
				Set<Map.Entry<TvVoucherinfoDto,List<TvAmtControlInfoDto>>> voucherSet = voucherAndAmtDtosMap.entrySet();   
				for (Iterator<Entry<TvVoucherinfoDto, List<TvAmtControlInfoDto>>> it = voucherSet.iterator(); it.hasNext();) {  
					Map.Entry<TvVoucherinfoDto,List<TvAmtControlInfoDto>> entry = (Map.Entry<TvVoucherinfoDto,List<TvAmtControlInfoDto>>) it.next();    
					TvVoucherinfoDto voucherDto = entry.getKey();
					List<TvAmtControlInfoDto> amtDtoList = entry.getValue();
					//先合并相同预算单位-科目代码的金额
					amtDtoList = mergerVoucherMoney(amtDtoList);
					if(amtDtoList!=null&&amtDtoList.size()>0){
						//查询原有额度信息列表
						List<TvAmtControlInfoDto> oldListDtos=null;
						try {
							oldListDtos = getAmtControlInfoByDTO(amtDtoList);
							String ls_Result = "";
							if(DealCodeConstants.VOUCHER_FAIL.equals(status)){
								//退回
								ls_Result = backVCucherUpdateMoney(amtDtoList,oldListDtos, voucherType);
							}else{
								//提交时额度控制
								List<TvAmtControlInfoDto> resultDtos  = verifyVoucherMoneyResutl(amtDtoList,oldListDtos, voucherType);
								//更新执行额度计算结果
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
							throw new  ITFEBizException("额度控制时出现异常！",e);
						}
					}
				}
			}else{
				for(TvVoucherinfoDto dto:voucherList){
					dto.setSdemo("按照凭证号未查询到业务表数据！");
				}
				voucherResult.addAll(voucherList);
			}
		}
		return voucherResult;
	}
	
}

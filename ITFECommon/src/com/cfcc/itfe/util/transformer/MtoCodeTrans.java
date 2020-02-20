package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.persistence.dto.TfVoucherSplitDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class MtoCodeTrans {
	
	/**
	 * 发送报文空字段处理
	 * @param obj
	 * @return
	 */
	public static String transformString(Object obj){
		if(null == obj){
			return "";
		}
		
		return String.valueOf(obj);
	}
	
	/**
	 * 接收报文BIGDECIMAL字段处理
	 * @param obj
	 * @return
	 */
	public static BigDecimal transformBigDecimal(Object obj){
		if(null == obj){
			return null;
		}
		
		return new BigDecimal((String)obj);
	}

	/**
	 * 拆分零余额的业务主表（上海扩展）
	 * @param list
	 * @return
	 * @throws ITFEBizException
	 */
	public static List transformZeroAmtMainDto(List list) throws ITFEBizException{
		//非上海无纸化不拆分凭证
		List lists=new ArrayList();
		List list1=new ArrayList();
		List list2=new ArrayList();
		if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")<0){
			lists.add(list);
			return lists;
		}
		String spackageno;
		try {
			spackageno = SequenceGenerator
			.changePackNoForLocal(SequenceGenerator.getNextByDb2(
					SequenceName.FILENAME_PACKNO_REF_SEQ,
					SequenceName.TRAID_SEQ_CACHE,
					SequenceName.TRAID_SEQ_STARTWITH,
					MsgConstant.SEQUENCE_MAX_DEF_VALUE));
		} catch (SequenceException e) {
			throw new ITFEBizException(e);
		}
		lists.add(list1);
		
		if(list.get(0) instanceof TvDirectpaymsgmainDto){
			for(TvDirectpaymsgmainDto maindto:(List<TvDirectpaymsgmainDto>)list){
				list1.add(maindto);
				if(maindto.getNmoney().compareTo(BigDecimal.ZERO)==0){
					TvDirectpaymsgmainDto maindto1=(TvDirectpaymsgmainDto) maindto.clone();//正金额
					//更新拆分业务主表凭证编号
					maindto1.setSpackageticketno(maindto.getSpackageticketno()+"1");
					maindto1.setShold2("已拆分业务主表标志");//已拆分业务主表标志
					TvDirectpaymsgmainDto maindto2=(TvDirectpaymsgmainDto) maindto.clone();//负金额
					//更新拆分业务主表凭证编号、交易流水号
					maindto2.setSpackageno(spackageno);
					maindto2.setSdealno(VoucherUtil.getGrantSequence().substring(8, 16));
					maindto2.setSpackageticketno(maindto.getSpackageticketno()+"2");
					maindto2.setShold2("已拆分业务主表标志");//已拆分业务主表标志
					list1.remove(maindto);
					list1.add(maindto1);
					list2.add(maindto2);
				}	
			}			
		} else if(list.get(0) instanceof TvGrantpaymsgmainDto){
			for(TvGrantpaymsgmainDto maindto:(List<TvGrantpaymsgmainDto>)list){
				list1.add(maindto);
				if(maindto.getNmoney().compareTo(BigDecimal.ZERO)==0&&!maindto.getShold2().equals("已拆分业务主表标志")){
					TvGrantpaymsgmainDto maindto1=(TvGrantpaymsgmainDto) maindto.clone();//正金额
					//更新拆分业务主表凭证编号
					maindto1.setShold1(maindto.getSpackageticketno()+"1");//该字段充当拆分凭证编号
					maindto1.setShold2("已拆分业务主表标志");//已拆分业务主表标志
					TvGrantpaymsgmainDto maindto2=(TvGrantpaymsgmainDto) maindto.clone();//负金额
					//更新拆分业务主表凭证编号、交易流水号
					maindto2.setSpackageno(spackageno);
					maindto2.setSdealno(VoucherUtil.getGrantSequence().substring(8, 16));
					maindto2.setShold1(maindto.getSpackageticketno()+"2");//该字段充当拆分凭证编号
					maindto2.setShold2("已拆分业务主表标志");//已拆分业务主表标志
					list1.remove(maindto);
					list1.add(maindto1);
					list2.add(maindto2);
				}
			}
		}else{
			return list;
		}
		if(list2.size()>0)
			lists.add(list2);
		return lists;
	}
	
	/**
	 * 拆分零余额的业务子表（上海扩展）
	 * @param maindto
	 * @param sublist
	 * @return
	 * @throws ITFEBizException
	 */
	public static List transformZeroAmtSubDto(IDto maindto,List sublist) throws ITFEBizException{
		//非上海无纸化不拆分凭证
		if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")<0)
			return sublist;
		List newSubdtoList=new ArrayList();
		BigDecimal sumAmt=new BigDecimal("0.00");//明细总金额
		if(sublist.get(0) instanceof TvDirectpaymsgsubDto){
			TvDirectpaymsgmainDto mainDto=(TvDirectpaymsgmainDto) maindto;
			if(mainDto.getNmoney().compareTo(BigDecimal.ZERO)!=0)
				return sublist;
			for(TvDirectpaymsgsubDto subdto:(List<TvDirectpaymsgsubDto>) sublist){
				if(((mainDto.getIvousrlno()+"").substring(8, 16).equals(mainDto.getSdealno())
						&&subdto.getNmoney().compareTo(BigDecimal.ZERO)>0)||
						(!(mainDto.getIvousrlno()+"").substring(8, 16).equals(mainDto.getSdealno())
						     &&subdto.getNmoney().compareTo(BigDecimal.ZERO)<0)){
					sumAmt=sumAmt.add(subdto.getNmoney());
					newSubdtoList.add(subdto);
				}				
			}
			if(newSubdtoList.size()==0)
				return sublist;
			//更新拆分凭证索引表
			TfVoucherSplitDto dto=new TfVoucherSplitDto();
			dto.setIvousrlno(mainDto.getIvousrlno());//原凭证流水号
			dto.setSsplitno(mainDto.getSpackageticketno());//拆分凭证编号
			dto.setSmainno(mainDto.getSpackageticketno().substring(0, mainDto.getSpackageticketno().length()-1));//原凭证号			
			dto.setSvtcode(MsgConstant.VOUCHER_NO_5108);//业务类型		
			dto.setSsplitvousrlno(mainDto.getSdealno());//拆分流水号						
			dto.setSorgcode(mainDto.getSorgcode());//机构代码
			dto.setStrecode(mainDto.getStrecode());//国库代码
			dto.setSstatus(mainDto.getSstatus());//状态
			dto.setSdemo(mainDto.getSdemo());//描述
			dto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//系统时间
			dto.setSpackageno(mainDto.getSpackageno());//包流水号
			dto.setScommitdate(mainDto.getScommitdate());//委托日期
			dto.setSallnum(newSubdtoList.size()+"");//总笔数
			dto.setNallamt(sumAmt);//总金额
			//将拆分凭证索引表写入数据库
			try {
				DatabaseFacade.getODB().create(dto);
			} catch (JAFDatabaseException e) {
			}
			//更新拆分业务主表金额
			((TvDirectpaymsgmainDto)maindto).setNmoney(sumAmt);
		}else if(sublist.get(0) instanceof TvGrantpaymsgsubDto){
			TvGrantpaymsgmainDto mainDto=(TvGrantpaymsgmainDto) maindto;
			if(mainDto.getNmoney().compareTo(BigDecimal.ZERO)!=0)
				return sublist;
			for(TvGrantpaymsgsubDto subdto:(List<TvGrantpaymsgsubDto>) sublist){
				if(((mainDto.getIvousrlno()+"").substring(8, 16).equals(mainDto.getSdealno())
						&&subdto.getNmoney().compareTo(BigDecimal.ZERO)>0)||
						(!(mainDto.getIvousrlno()+"").substring(8, 16).equals(mainDto.getSdealno())
						     &&subdto.getNmoney().compareTo(BigDecimal.ZERO)<0)){
					sumAmt=sumAmt.add(subdto.getNmoney());
					newSubdtoList.add(subdto);
				}				
			}
			if(newSubdtoList.size()==0)
				return sublist;
			//更新拆分凭证索引表
			TfVoucherSplitDto dto=new TfVoucherSplitDto();
			dto.setIvousrlno(mainDto.getIvousrlno());//原凭证流水号
			dto.setSsplitno(mainDto.getShold1());//拆分凭证编号
			dto.setSmainno(mainDto.getSpackageticketno());//原凭证号			
			dto.setSvtcode(MsgConstant.VOUCHER_NO_5106);//业务类型		
			dto.setSsplitvousrlno(mainDto.getSdealno());//拆分流水号						
			dto.setSorgcode(mainDto.getSorgcode());//机构代码
			dto.setStrecode(mainDto.getStrecode());//国库代码
			dto.setSstatus(mainDto.getSstatus());//状态
			dto.setSdemo(mainDto.getSdemo());//描述
			dto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//系统时间
			dto.setSpackageno(mainDto.getSpackageno());//包流水号
			dto.setScommitdate(mainDto.getScommitdate());//委托日期
			dto.setSallnum(newSubdtoList.size()+"");//总笔数
			dto.setNallamt(sumAmt);//总金额
			//将拆分凭证索引表写入数据库
			try {
				DatabaseFacade.getODB().create(dto);
			} catch (JAFDatabaseException e) {
			}
			//更新拆分业务主表金额
			((TvGrantpaymsgmainDto)maindto).setNmoney(sumAmt);
		}else
			return sublist;
		return newSubdtoList;
	}

}


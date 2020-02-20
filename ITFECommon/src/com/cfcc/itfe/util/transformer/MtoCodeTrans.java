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
	 * ���ͱ��Ŀ��ֶδ���
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
	 * ���ձ���BIGDECIMAL�ֶδ���
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
	 * ���������ҵ�������Ϻ���չ��
	 * @param list
	 * @return
	 * @throws ITFEBizException
	 */
	public static List transformZeroAmtMainDto(List list) throws ITFEBizException{
		//���Ϻ���ֽ�������ƾ֤
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
					TvDirectpaymsgmainDto maindto1=(TvDirectpaymsgmainDto) maindto.clone();//�����
					//���²��ҵ������ƾ֤���
					maindto1.setSpackageticketno(maindto.getSpackageticketno()+"1");
					maindto1.setShold2("�Ѳ��ҵ�������־");//�Ѳ��ҵ�������־
					TvDirectpaymsgmainDto maindto2=(TvDirectpaymsgmainDto) maindto.clone();//�����
					//���²��ҵ������ƾ֤��š�������ˮ��
					maindto2.setSpackageno(spackageno);
					maindto2.setSdealno(VoucherUtil.getGrantSequence().substring(8, 16));
					maindto2.setSpackageticketno(maindto.getSpackageticketno()+"2");
					maindto2.setShold2("�Ѳ��ҵ�������־");//�Ѳ��ҵ�������־
					list1.remove(maindto);
					list1.add(maindto1);
					list2.add(maindto2);
				}	
			}			
		} else if(list.get(0) instanceof TvGrantpaymsgmainDto){
			for(TvGrantpaymsgmainDto maindto:(List<TvGrantpaymsgmainDto>)list){
				list1.add(maindto);
				if(maindto.getNmoney().compareTo(BigDecimal.ZERO)==0&&!maindto.getShold2().equals("�Ѳ��ҵ�������־")){
					TvGrantpaymsgmainDto maindto1=(TvGrantpaymsgmainDto) maindto.clone();//�����
					//���²��ҵ������ƾ֤���
					maindto1.setShold1(maindto.getSpackageticketno()+"1");//���ֶγ䵱���ƾ֤���
					maindto1.setShold2("�Ѳ��ҵ�������־");//�Ѳ��ҵ�������־
					TvGrantpaymsgmainDto maindto2=(TvGrantpaymsgmainDto) maindto.clone();//�����
					//���²��ҵ������ƾ֤��š�������ˮ��
					maindto2.setSpackageno(spackageno);
					maindto2.setSdealno(VoucherUtil.getGrantSequence().substring(8, 16));
					maindto2.setShold1(maindto.getSpackageticketno()+"2");//���ֶγ䵱���ƾ֤���
					maindto2.setShold2("�Ѳ��ҵ�������־");//�Ѳ��ҵ�������־
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
	 * ���������ҵ���ӱ��Ϻ���չ��
	 * @param maindto
	 * @param sublist
	 * @return
	 * @throws ITFEBizException
	 */
	public static List transformZeroAmtSubDto(IDto maindto,List sublist) throws ITFEBizException{
		//���Ϻ���ֽ�������ƾ֤
		if(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,")<0)
			return sublist;
		List newSubdtoList=new ArrayList();
		BigDecimal sumAmt=new BigDecimal("0.00");//��ϸ�ܽ��
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
			//���²��ƾ֤������
			TfVoucherSplitDto dto=new TfVoucherSplitDto();
			dto.setIvousrlno(mainDto.getIvousrlno());//ԭƾ֤��ˮ��
			dto.setSsplitno(mainDto.getSpackageticketno());//���ƾ֤���
			dto.setSmainno(mainDto.getSpackageticketno().substring(0, mainDto.getSpackageticketno().length()-1));//ԭƾ֤��			
			dto.setSvtcode(MsgConstant.VOUCHER_NO_5108);//ҵ������		
			dto.setSsplitvousrlno(mainDto.getSdealno());//�����ˮ��						
			dto.setSorgcode(mainDto.getSorgcode());//��������
			dto.setStrecode(mainDto.getStrecode());//�������
			dto.setSstatus(mainDto.getSstatus());//״̬
			dto.setSdemo(mainDto.getSdemo());//����
			dto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//ϵͳʱ��
			dto.setSpackageno(mainDto.getSpackageno());//����ˮ��
			dto.setScommitdate(mainDto.getScommitdate());//ί������
			dto.setSallnum(newSubdtoList.size()+"");//�ܱ���
			dto.setNallamt(sumAmt);//�ܽ��
			//�����ƾ֤������д�����ݿ�
			try {
				DatabaseFacade.getODB().create(dto);
			} catch (JAFDatabaseException e) {
			}
			//���²��ҵ��������
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
			//���²��ƾ֤������
			TfVoucherSplitDto dto=new TfVoucherSplitDto();
			dto.setIvousrlno(mainDto.getIvousrlno());//ԭƾ֤��ˮ��
			dto.setSsplitno(mainDto.getShold1());//���ƾ֤���
			dto.setSmainno(mainDto.getSpackageticketno());//ԭƾ֤��			
			dto.setSvtcode(MsgConstant.VOUCHER_NO_5106);//ҵ������		
			dto.setSsplitvousrlno(mainDto.getSdealno());//�����ˮ��						
			dto.setSorgcode(mainDto.getSorgcode());//��������
			dto.setStrecode(mainDto.getStrecode());//�������
			dto.setSstatus(mainDto.getSstatus());//״̬
			dto.setSdemo(mainDto.getSdemo());//����
			dto.setTssysupdate(new Timestamp(new java.util.Date().getTime()));//ϵͳʱ��
			dto.setSpackageno(mainDto.getSpackageno());//����ˮ��
			dto.setScommitdate(mainDto.getScommitdate());//ί������
			dto.setSallnum(newSubdtoList.size()+"");//�ܱ���
			dto.setNallamt(sumAmt);//�ܽ��
			//�����ƾ֤������д�����ݿ�
			try {
				DatabaseFacade.getODB().create(dto);
			} catch (JAFDatabaseException e) {
			}
			//���²��ҵ��������
			((TvGrantpaymsgmainDto)maindto).setNmoney(sumAmt);
		}else
			return sublist;
		return newSubdtoList;
	}

}


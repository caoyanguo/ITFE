package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * ��������ת��
 * 
 * @author wangwenbo
 * 
 */
public class Dto2MapFor1000_2201 {

	private static Log logger = LogFactory.getLog(Dto2MapFor1000_2201.class);
	
	/**
	 * DTOת��XML����(��������ҵ��)
	 * 
	 * @param List
	 *            <TvDirectpaymsgmainDto> list ���ͱ��Ķ��󼯺�
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            packno ����ˮ��
	 * @param boolean isRepeat �Ƿ��ط�����
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(List<TvPayreckBankDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
		if (null == list || list.size() == 0) {
			throw new ITFEBizException("Ҫת���Ķ���Ϊ��,��ȷ��!");
		}

		HashMap<String, Object> map = new HashMap<String, Object>();
		HashMap<String, Object> cfxMap = new HashMap<String, Object>();
		HashMap<String, Object> headMap = new HashMap<String, Object>();
		HashMap<String, Object> msgMap = new HashMap<String, Object>();

		// ���ñ��Ľڵ� CFX
		map.put("cfx", cfxMap);
		cfxMap.put("HEAD", headMap);
		cfxMap.put("MSG", msgMap);

		// ����ͷ HEAD
		headMap.put("VER", MsgConstant.MSG_HEAD_VER);
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.APPLYPAY_DAORU);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		headMap.put("Reserve", MsgConstant.CFCCHL);
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}

		// ���ñ�����Ϣ�� MSG
		HashMap<String, Object> head1000Map = new HashMap<String, Object>();
		
		head1000Map.put("BillOrg",list.get(0).getSfinorgcode() ); ///��Ʊ��λ, TvPayreckBankDto ��û�д���?
		
		head1000Map.put("EntrustDate", list.get(0).getDentrustdate().toString().replaceAll("-", ""));
		head1000Map.put("PackNo", list.get(0).getSpackno()); // �����
		head1000Map.put("TreCode", list.get(0).getStrecode()); // �������
		head1000Map.put("AllNum", String.valueOf(list.size())); // �ܱ���
		
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������
		head1000Map.put("PayoutVouType", MsgConstant.SAME_BANK_PAYOUT_VOU_TYPE3); // ֧��ƾ֤����: 0����ֽƾ֤1����ֽƾ֤

		List<Object> bill1000List = new ArrayList<Object>();
		
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill1000Map = new HashMap<String, Object>();
			TvPayreckBankDto maindto = list.get(i);

			allamt = allamt.add(maindto.getFamt());

			bill1000Map.put("TraNo", maindto.getStrano()); // ������ˮ��
			bill1000Map.put("VouNo", maindto.getSvouno()); // ƾ֤���
			bill1000Map.put("VouDate", maindto.getDvoudate().toString().replaceAll("-", "")); // ƾ֤����[��Ʊ����]
			bill1000Map.put("PayerAcct", maindto.getSpayeracct());
			bill1000Map.put("PayerName", maindto.getSpayername());
			bill1000Map.put("PayerAddr", maindto.getSpayeraddr());
			bill1000Map.put("Amt", MtoCodeTrans.transformString(maindto.getFamt())); //���
			
			bill1000Map.put("PayeeBankNo", maindto.getSpayeeopbkno()); ///�տ����к�,  TvPayreckBankDto ��û�д���?
			bill1000Map.put("PayeeOpBkNo", maindto.getSpayeeopbkno());
			bill1000Map.put("PayeeAcct", maindto.getSpayeeacct());
			bill1000Map.put("PayeeName", maindto.getSpayeename());
			
			bill1000Map.put("PayReason",maindto.getSaddword() ); ///������˿�ԭ��, TvPayreckBankDto ��û�д���?
			bill1000Map.put("AddWord", maindto.getSaddword());bill1000Map.put("OfYear", maindto.getSofyear());
			bill1000Map.put("OfYear", maindto.getSofyear());
			bill1000Map.put("Flag", MsgConstant.MSG_1000_FLAG2); //�տ��ʶ: 1-����ҵ�� 2-ͬ�ǿ��� 3-��ؿ���

			bill1000List.add(bill1000Map);
		}

		head1000Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead1000", head1000Map);
		msgMap.put("BillSend1000", bill1000List);

		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

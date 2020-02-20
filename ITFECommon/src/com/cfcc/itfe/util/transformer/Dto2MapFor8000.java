package com.cfcc.itfe.util.transformer;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.voucher.service.VoucherUtil;


/**
 * ���˱��Ľӿ�(8000)������ⷢ��ת��
 * 
 * @author wangyunbin
 * 
 */
public class Dto2MapFor8000 {

	private static Log logger = LogFactory.getLog(Dto2MapFor8000.class);
	
	/**
	 * DTOת��XML����(�˿�����)�ɰ汾
	 * 
	 * @param List
	 *            <TvFilepackagerefDto> list ���ͱ��Ķ��󼯺�
	 * @return
	 * @throws ITFEBizException
	 */
	public static Map tranfor(List<TvFilepackagerefDto> list,String orgcode) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_8000);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		String msgid  = "";
		try {
			msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}
		// ���ñ�����Ϣ�� MSG
		HashMap<String, Object> BatchHead8000 = new HashMap<String, Object>();
		BatchHead8000.put("ChkDate", TimeFacade.getCurrentStringTime()); ///��������
		BatchHead8000.put("TreCode", list.get(0).getStrecode()); // �����������
		BatchHead8000.put("SendPackNum", list.size()); // ���շ��Ͱ��ܸ���
		
		///<AllAmt>�ܽ��</AllAmt>
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������

		List<Object> BillCheck8000List = new ArrayList<Object>();
		
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> BillCheck8000Map = new HashMap<String, Object>();
			TvFilepackagerefDto filedPackegeDto = list.get(i);
			allamt = allamt.add(filedPackegeDto.getNmoney());
			BillCheck8000Map.put("PayoutVouType", VoucherUtil.getPayOutVouType(filedPackegeDto.getSoperationtypecode())); // ƾ֤����
			BillCheck8000Map.put("EntrustDate", filedPackegeDto.getScommitdate());
			BillCheck8000Map.put("PackNo", filedPackegeDto.getSpackageno());
			BillCheck8000Map.put("CurPackVouNum",filedPackegeDto.getIcount());
			BillCheck8000List.add(BillCheck8000Map);
		}
		//BatchHead1000.put("AllAmt", MtoCodeTrans.transformString(allamt));
		BatchHead8000.put("SendPackAmt",MtoCodeTrans.transformString(allamt)); //���շ��Ͱ��ܽ��
		msgMap.put("BatchHead8000", BatchHead8000);
		msgMap.put("BillCheck8000", BillCheck8000List);
		return map;
	}
}
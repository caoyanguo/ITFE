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
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * �˿�����
 * 
 * @author wangyunbin
 * 
 */
public class Dto2MapFor11042 {

	private static Log logger = LogFactory.getLog(Dto2MapFor11042.class);
	
	/**
	 * DTOת��XML����(�˿�����)�ɰ汾
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
	 * @throws ValidateException 
	 * @throws JAFDatabaseException 
	 */
	@SuppressWarnings("unchecked")
	public static Map tranfor(List<TvDwbkDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
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
				// ���ñ�����Ϣ�� MSG
		HashMap<String, Object> head1104Map = new HashMap<String, Object>();
		head1104Map.put("TaxOrgCode", list.get(0).getStaxorgcode()); // ���ջ��ش���
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		if("000057400006".equals(ITFECommonConstant.SRC_NODE)&&head1104Map.get("TaxOrgCode")!=null&&!"".equals(String.valueOf(head1104Map.get("TaxOrgCode"))))//����ר�ù�˰��˰�Ѿ�����tips�����˿⻹��ǰ����
		{
			if(String.valueOf(head1104Map.get("TaxOrgCode")).startsWith("1"))//��˰
				headMap.put("SRC","201057400009");
			else if(String.valueOf(head1104Map.get("TaxOrgCode")).startsWith("2"))//��˰
				headMap.put("SRC","202057400000");
		}
		//ɽ������˰�͵�˰���ݲ���ά�������д�ڵ��
		if(ITFECommonConstant.SRC_NODE.equals("201053100013")){
			TsTaxorgDto dto = new TsTaxorgDto();
			dto.setSorgcode(StateConstant.ORG_CENTER_CODE);
			dto.setStaxorgcode(list.get(0).getStaxorgcode());
			dto.setStrecode(list.get(0).getSpayertrecode());
			
			List<TsTaxorgDto> listdto = new ArrayList<TsTaxorgDto>();
			try {
				listdto = CommonFacade.getODB().findRsByDto(dto);
			} catch (JAFDatabaseException e) {
				e.printStackTrace();
			} catch (ValidateException e) {
				e.printStackTrace();
			}
			if(listdto!=null && listdto.size()>0){
				headMap.put("SRC",listdto.get(0).getSnodecode());
			}
			
		}
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.MSG_NO_1104);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		String msgid = "";
		try {
			msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}
		head1104Map.put("EntrustDate", list.get(0).getDaccept().toString().replaceAll("-", "")); // ί������
		head1104Map.put("PackNo", list.get(0).getSpackageno()); // ����ˮ��
		head1104Map.put("AllNum", String.valueOf(list.size())); // �ܱ���
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������

		List<Object> bill1104List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill1104Map = new HashMap<String, Object>();
			TvDwbkDto maindto = list.get(i);

			allamt = allamt.add(maindto.getFamt());

			bill1104Map.put("TraNo", maindto.getSdealno()); // ������ˮ��
			bill1104Map.put("DrawBackTreCode", maindto.getSpayertrecode()); // �˿�������
			bill1104Map.put("PayeeBankNo", ""); // �տ����к�
			bill1104Map.put("PayeeOpBkCode", maindto.getSpayeeopnbnkno()); // �տ�����к�
			bill1104Map.put("PayeeAcct", maindto.getSpayeeacct()); // �տ����˺�
			bill1104Map.put("PayeeName", maindto.getSpayeename()); // �տ�������
			bill1104Map.put("TaxPayCode", ""); // ��˰�˱���
			bill1104Map.put("TaxPayName", ""); // ��˰������
			bill1104Map.put("DrawBackVouNo", maindto.getSdwbkvoucode()); // �˿�ƾ֤���
			bill1104Map.put("ElectroTaxVouNo", maindto.getSelecvouno()); // ����ƾ֤����
			//bill1104Map.put("OriTaxVouNo", ""); // ԭ˰Ʊ����(tips�°汾ɾ��ԭ˰Ʊ����)
			bill1104Map.put("TraAmt", MtoCodeTrans.transformString(maindto.getFamt())); //���׽��
			bill1104Map.put("BillDate", maindto.getDvoucher().toString().replaceAll("-", "")); // ��Ʊ����
			bill1104Map.put("CorpCode", maindto.getSpayeecode()); // ��ҵ����
			bill1104Map.put("BudgetType", maindto.getCbdgkind()); // Ԥ������
			bill1104Map.put("BudgetSubjectCode", maindto.getSbdgsbtcode()); // Ԥ���Ŀ����
			bill1104Map.put("BudgetLevelCode", maindto.getCbdglevel()); // Ԥ�㼶�δ���
			bill1104Map.put("TrimSign", maindto.getCtrimflag()); // �����ڱ�־
			bill1104Map.put("ViceSign", maindto.getSastflag()); // ������־
			bill1104Map.put("DrawBackReasonCode", maindto.getSdwbkreasoncode()); // �˿�ԭ�����
			bill1104Map.put("DrawBackVou", maindto.getSdwbkby()); // �˿�����
			bill1104Map.put("ExamOrg", maindto.getSexamorg()); // ��������
			bill1104Map.put("AuthNo", ""); // ��׼�ĺ�
			bill1104Map.put("TransType",""); // ת������
			bill1104Map.put("ChannelCode",""); // ��������
			List OriTaxList1104MapList = new ArrayList();
			OriTaxList1104MapList.add(new HashMap());
			bill1104Map.put("OriTaxList1104", OriTaxList1104MapList);
			bill1104List.add(bill1104Map);
		}

		head1104Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead1104", head1104Map);
		msgMap.put("RetTreasury1104", bill1104List);
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

	}

}

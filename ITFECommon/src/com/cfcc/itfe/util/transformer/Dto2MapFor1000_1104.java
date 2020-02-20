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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;


/**
 * �˿�����
 * 
 * @author wangwenbo
 * 
 */
public class Dto2MapFor1000_1104 {

	private static Log logger = LogFactory.getLog(Dto2MapFor1000_1104.class);
	
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
	 */
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
		if(orgcode!=null&&orgcode.startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.MSG_NO_1104);
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
		HashMap<String, Object> BatchHead1000 = new HashMap<String, Object>();
		
		BatchHead1000.put("BillOrg", list.get(0).getStaxorgcode()); ///��Ʊ��λ,  TvDwbkDto ��û�д���?
		BatchHead1000.put("EntrustDate", list.get(0).getDaccept().toString().replaceAll("-", "")); // ί������
		BatchHead1000.put("PackNo", list.get(0).getSpackageno()); // ����ˮ��
		BatchHead1000.put("TreCode", list.get(0).getSpayertrecode()); //�����������,  TvDwbkDto ��û�д���?
		BatchHead1000.put("AllNum", String.valueOf(list.size())); // �ܱ���
		BatchHead1000.put("PayoutVouType", MsgConstant.SAME_BANK_PAYOUT_VOU_TYPE2); //ƾ֤����: 1ʵ��, 2�˿�, 3���л���, 4����
		
		///<AllAmt>�ܽ��</AllAmt>
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������

		List<Object> bill1000List = new ArrayList<Object>();
		
		//ȡ����˻���Ϣ
		TsInfoconnorgaccDto dto =  getDoconnorgAcc(list.get(0).getSbookorgcode(),list.get(0).getSpayertrecode());
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill1000Map = new HashMap<String, Object>();
			TvDwbkDto maindto = list.get(i);
			allamt = allamt.add(maindto.getFamt());
			bill1000Map.put("TraNo", maindto.getSdealno()); // ������ˮ��
			
			String vouno = maindto.getSdwbkvoucode();
			if (vouno.length() > 8) {
				vouno = vouno.substring(vouno.length()-8,vouno.length());
			}else if(vouno.length() < 8){
				vouno = changeVouNo(vouno);
			}
			bill1000Map.put("VouNo", vouno); // �˿�ƾ֤���
			bill1000Map.put("VouDate", maindto.getDvoucher().toString().replaceAll("-", "")); // ��Ʊ���� -> ƾ֤���� ?
			
			bill1000Map.put("PayerAcct",dto.getSpayeraccount()); // �������˺�, TvDwbkDto ��û�д���?
			
			bill1000Map.put("PayerName",dto.getSpayername()); // ����������, TvDwbkDto ��û�д���?
			
			bill1000Map.put("PayerAddr",dto.getSpayeraddr()); // �����˵�ַ, TvDwbkDto ��û�д���?
			
			bill1000Map.put("Amt", MtoCodeTrans.transformString(maindto.getFamt())); //���׽�� -> ���?
			
			bill1000Map.put("PayeeBankNo", maindto.getSpayeeopnbnkno()); // �տ����к�, TvDwbkDto ��û�д���?
			bill1000Map.put("PayeeOpBkNo", maindto.getSpayeeopnbnkno()); // �տ��˿������к�
			bill1000Map.put("PayeeAcct", maindto.getSpayeeacct()); // �տ����˺�
			bill1000Map.put("PayeeName", maindto.getSpayeename()); // �տ�������
			
			bill1000Map.put("PayReason", maindto.getSdwbkreasoncode()); // �˿�ԭ�����->������˿�ԭ��?
			
			bill1000Map.put("OfYear",BatchHead1000.get("EntrustDate").toString().substring(0, 4) ); // �������, TvDwbkDto ��û�д���?
			bill1000Map.put("Flag", MsgConstant.MSG_1000_FLAG2); //�տ��ʶ: 1-����ҵ�� 2-ͬ�ǿ��� 3-��ؿ���
			
			
			
			/***********************************<1104>********************************************************/
			
			bill1000Map.put("DrawBackTreCode", maindto.getSpayertrecode()); // �˿�������
			bill1000Map.put("PayeeBankNo", ""); // �տ����к�
			bill1000Map.put("PayeeOpBkCode", maindto.getSpayeeopnbnkno()); // �տ�����к�
			bill1000Map.put("PayeeAcct", maindto.getSpayeeacct()); // �տ����˺�
			bill1000Map.put("PayeeName", maindto.getSpayeename()); // �տ�������
			bill1000Map.put("TaxPayCode", ""); // ��˰�˱���
			bill1000Map.put("TaxPayName", ""); // ��˰������
			
			bill1000Map.put("ElectroTaxVouNo", maindto.getSelecvouno()); // ����ƾ֤����
			bill1000Map.put("OriTaxVouNo", ""); // ԭ˰Ʊ����
			bill1000Map.put("TraAmt", MtoCodeTrans.transformString(maindto.getFamt())); //���׽��
			
			bill1000Map.put("CorpCode", maindto.getSpayeecode()); // ��ҵ����
			bill1000Map.put("BudgetType", maindto.getCbdgkind()); // Ԥ������
			bill1000Map.put("BudgetSubjectCode", maindto.getSbdgsbtcode()); // Ԥ���Ŀ����
			bill1000Map.put("BudgetLevelCode", maindto.getCbdglevel()); // Ԥ�㼶�δ���
			bill1000Map.put("TrimSign", maindto.getCtrimflag()); // �����ڱ�־
			bill1000Map.put("ViceSign", maindto.getSastflag()); // ������־
			bill1000Map.put("DrawBackReasonCode", maindto.getSdwbkreasoncode()); // �˿�ԭ�����
			bill1000Map.put("DrawBackVou", maindto.getSdwbkby()); // �˿�����
			bill1000Map.put("ExamOrg", maindto.getSexamorg()); // ��������
			bill1000Map.put("AuthNo", ""); // ��׼�ĺ�
			bill1000Map.put("TransType",""); // ת������
			
			/***********************************</1104>********************************************************/
			
			bill1000List.add(bill1000Map);
		}

		BatchHead1000.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead1000", BatchHead1000);
		msgMap.put("BillSend1000", bill1000List);
		
		return map;
	}
	
	/**
	 * �����˿�ƾ֤���ת��������8λ���㣩
	 * @param String seqno ԭʼƾ֤���
	 * @return
	 */
	public static String changeVouNo(String seqno){
		String tmpVouNo = "00000000" + seqno;
		String trasrlNo = tmpVouNo.substring(tmpVouNo.length()- 8 ,tmpVouNo.length());
		return trasrlNo;
	}

	private static TsInfoconnorgaccDto getDoconnorgAcc(String orgCode,String treCode){
		TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
		accdto.setStrecode(orgCode);
		accdto.setSorgcode(treCode);
		List<TsInfoconnorgaccDto> accList = null;
		try {
			accList = (List<TsInfoconnorgaccDto>) CommonFacade.getODB().findRsByDto(accdto);
		} catch (JAFDatabaseException e) {
			logger.debug(e);
		} catch (ValidateException e) {
			logger.debug(e);
		}
		if(accList!=null&&accList.size()>0)
		{
			for(TsInfoconnorgaccDto tempdto:accList)
			{
				if(orgCode.equals("")&&treCode.equals(""))
					if(tempdto.getSpayeraccount().indexOf("371")>0)
						accdto =  tempdto;
			}
		}
		return accdto;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
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
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPbcpayMainDto;
import com.cfcc.itfe.persistence.dto.TvPbcpaySubDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * ���а���ֱ��֧��ת��
 * 
 * @author zhouchuan
 * 
 */
public class Dto2MapFor5104 {

	private static Log logger = LogFactory.getLog(Dto2MapFor5104.class);

	/**
	 * DTOת��XML����(���а���ֱ��֧��ҵ��)
	 * 
	 * @param List
	 *            <TvPayoutmsgmainDto> list ���ͱ��Ķ��󼯺�
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
	public static Map tranfor(List<TvPbcpayMainDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_5104);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}

		// ���ñ�����Ϣ�� MSG
		HashMap<String, Object> head5104Map = new HashMap<String, Object>();
		head5104Map.put("TreCode", list.get(0).getStrecode()); // �������
		head5104Map.put("BillOrg", list.get(0).getSbillorg()); // ��Ʊ��λ
		head5104Map.put("PayeeBankNo", list.get(0).getSrcvbnkno()); // �տ����к�
		head5104Map.put("EntrustDate", list.get(0).getSentrustdate().replaceAll("-", "")); // ί������
		head5104Map.put("PackNo", list.get(0).getSpackno()); // �����
		head5104Map.put("AllNum", String.valueOf(list.size())); // �ܱ���
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������
		head5104Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // ֧��ƾ֤����  0����ֽƾ֤1����ֽƾ֤
		head5104Map.put("PayMode", MsgConstant.directPay); 
		
		List<Object> bill5104List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill5104Map = new HashMap<String, Object>();
			List<Object> detail5104List = new ArrayList<Object>();

			TvPbcpayMainDto maindto = list.get(i);
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("Ҫת���Ķ����Ӧ���Ӽ�¼Ϊ��,��ȷ��![" + list.get(i).toString() + "]");
			}

			allamt = allamt.add(maindto.getFamt());

			bill5104Map.put("TraNo", maindto.getStrano()); // ������ˮ��
			bill5104Map.put("VouNo", maindto.getSvouno()); // ƾ֤���
			bill5104Map.put("VouDate", maindto.getDvoucher().replaceAll("-", "")); // ƾ֤����[��Ʊ����]
			bill5104Map.put("PayerAcct", maindto.getSpayeracct()); // �������˺�
			
			bill5104Map.put("PayerName", maindto.getSpayername()); // ����������
            if (null==maindto.getSpayeraddr()) {
        	    maindto.setSpayeraddr("");
			}
			bill5104Map.put("PayerAddr", maindto.getSpayeraddr()); // �����˵�ַ
			bill5104Map.put("Amt", MtoCodeTrans.transformString(maindto.getFamt())); // ���׽��
			bill5104Map.put("PayeeOpnBnkNo", maindto.getSpayeeopnbnkno()); // �տ��˿������к�
			bill5104Map.put("PayeeAcct", maindto.getSpayeeacct()); // �տ����˺�
			bill5104Map.put("PayeeName", maindto.getSpayeename()); // �տ�������
			 if (null== maindto.getSpayeeaddr()) {
	        	    maindto.setSpayeeaddr("");
				}
			bill5104Map.put("PayeeAddr", maindto.getSpayeeaddr()); // �տ��˵�ַ
			bill5104Map.put("AddWord", maindto.getSaddword()); // ����
			bill5104Map.put("OfYear", ""+maindto.getIofyear()); // �������
			bill5104Map.put("BudgetType", maindto.getCbdgkind()); // Ԥ������
			 if (null== maindto.getSbdgadmtype()) {
	        	    maindto.setSbdgadmtype("");
				}
			bill5104Map.put("BdgAdmType", maindto.getSbdgadmtype()); //Ԥ���������
			
			bill5104Map.put("TrimSign", maindto.getCtrimflag()); // �����ڱ�־
			bill5104Map.put("StatInfNum", sublist.size()); // ͳ����Ϣ����
			
			String s = "0";
			 try {
				HashMap<String,TsTreasuryDto> mapTreInfo =SrvCacheFacade.cacheTreasuryInfo(null);
			    s = mapTreInfo.get(list.get(0).getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("���ҹ�������쳣!", e);
				throw new ITFEBizException("���ҹ�������쳣��", e);
			}

			// ѭ������Ϣ
			for (int j = 0; j < sublist.size(); j++) {
				HashMap<String, Object> detail5104Map = new HashMap<String, Object>();
				TvPbcpaySubDto subdto = (TvPbcpaySubDto) sublist.get(j);
				
				detail5104Map.put("SeqNo", subdto.getIseqno());
				detail5104Map.put("FuncSbtCode", subdto.getSfuncsbtcode());
				 if (null== subdto.getSecosbtcode()) {
					 subdto.setSecosbtcode("");
					}
				if (StateConstant.COMMON_YES.equals(s)) {
					detail5104Map.put("EcnomicSubjectCode", subdto.getSecosbtcode());
				}else{
					detail5104Map.put("EcnomicSubjectCode", "");
				}
				detail5104Map.put("BdgOrgCode", subdto.getSbdgorgcode());
				detail5104Map.put("Amt", MtoCodeTrans.transformString(subdto.getFamt()));
				detail5104Map.put("AcctProp", StateConstant.COMMON_YES);
				detail5104List.add(detail5104Map);
			}

			bill5104Map.put("Detail5104", detail5104List);
			bill5104List.add(bill5104Map);

		}

		head5104Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead5104", head5104Map);
		msgMap.put("Bill5104", bill5104List);
		
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

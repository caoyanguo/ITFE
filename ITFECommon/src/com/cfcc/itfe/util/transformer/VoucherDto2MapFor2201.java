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
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgsubDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * TIPS���л������뱨�ģ�2201��(�Ϻ���չ)
 * ֱ��֧����5201������ҵ������ 
 * @author hejianrong
 * @time 2014-09-19
 * 
 */
public class VoucherDto2MapFor2201 {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor2201.class);
	
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
	public static Map tranfor(List<TfDirectpaymsgmainDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
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
		HashMap<String, Object> head2201Map = new HashMap<String, Object>();
		head2201Map.put("AgentBnkCode", list.get(0).getSpayeeacctbankno());//���������к�
		head2201Map.put("FinOrgCode", list.get(0).getSfinorgcode());//�������ش���
		head2201Map.put("TreCode", list.get(0).getStrecode()); // �������
		head2201Map.put("EntrustDate", list.get(0).getScommitdate());//ί������
		head2201Map.put("PackNo", list.get(0).getSpackageno()); // �����
		head2201Map.put("AllNum", String.valueOf(list.size())); // �ܱ���
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������
		head2201Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // ֧��ƾ֤����: 0����ֽƾ֤1����ֽƾ֤	
		head2201Map.put("PayMode",list.get(0).getSpaytypecode());//֧����ʽ
		List<Object> bill2201List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill2201Map = new HashMap<String, Object>();
			List<Object> detail2201List = new ArrayList<Object>();
			TfDirectpaymsgmainDto maindto = list.get(i);
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("Ҫת���Ķ����Ӧ���Ӽ�¼Ϊ��,��ȷ��![" + list.get(i).toString() + "]");
			}
			
			allamt = allamt.add(maindto.getNpayamt());
			bill2201Map.put("TraNo", maindto.getSdealno()); // ������ˮ��
			bill2201Map.put("VouNo", maindto.getSvoucherno()); // ƾ֤���
			bill2201Map.put("VouDate", maindto.getSvoudate()); // ƾ֤����[��Ʊ����]
			bill2201Map.put("PayerAcct", maindto.getSpayacctno());//�������˺�
			bill2201Map.put("PayerName", maindto.getSpayacctname());//����������
			bill2201Map.put("PayerAddr", "");//�����˵�ַ 
			bill2201Map.put("PayeeAcct", maindto.getSpayeeacctno());//�տ����˺�
			bill2201Map.put("PayeeName", maindto.getSpayeeacctname());//�տ�������
			bill2201Map.put("PayeeAddr","");//�տ��˵�ַ
			bill2201Map.put("PayeeOpnBnkNo", maindto.getSpayeeacctbankno());//�տ��˿������к� 
			bill2201Map.put("AddWord", maindto.getSdemo());//���� 
			bill2201Map.put("BudgetType", MsgConstant.BDG_KIND_IN);//Ԥ������       Ĭ�� 1 Ԥ����
			bill2201Map.put("TrimSign", MsgConstant.TIME_FLAG_NORMAL);//�����ڱ�־
			bill2201Map.put("OfYear", maindto.getSstyear());//�������
			bill2201Map.put("Amt", MtoCodeTrans.transformString(maindto.getNpayamt()));//���
			bill2201Map.put("StatInfNum", sublist.size());//ͳ����Ϣ����
			String s = "0";
			 try {
				HashMap<String,TsTreasuryDto> mapTreInfo =SrvCacheFacade.cacheTreasuryInfo(null);
			    s = mapTreInfo.get(list.get(0).getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("���ҹ�������쳣!", e);
				throw new ITFEBizException("���ҹ�������쳣��", e);
			}
			
			// ��ϸ��Ϣ
			for (int j = 0; j < sublist.size(); j++) {
				HashMap<String, Object> detail2201Map = new HashMap<String, Object>();
				TfDirectpaymsgsubDto subdto = (TfDirectpaymsgsubDto) sublist.get(j);
				detail2201Map.put("SeqNo", subdto.getIseqno().toString());//���
				detail2201Map.put("BdgOrgCode", subdto.getSagencycode());//Ԥ�㵥λ����
				detail2201Map.put("FuncSbtCode", subdto.getSexpfunccode());//�������Ŀ����
				if (StateConstant.COMMON_YES.equals(s)) {//�������Ŀ����
					detail2201Map.put("EcnomicSubjectCode", MtoCodeTrans.transformString(subdto.getSexpecocode()));
				}else{
					detail2201Map.put("EcnomicSubjectCode", "");
				}
				detail2201Map.put("Amt", MtoCodeTrans.transformString(subdto.getNpayamt()));//���
				detail2201Map.put("AcctProp", MsgConstant.ACCT_PROP_ZERO);//�˻�����	     Ĭ�� 1 ����� 
				detail2201List.add(detail2201Map);
			}
			bill2201Map.put("Detail2201", detail2201List);
			bill2201List.add(bill2201Map);
		}
		
		head2201Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead2201", head2201Map);
		msgMap.put("Bill2201", bill2201List);
		return map;
	}	

}

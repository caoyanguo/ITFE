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
 * TIPS�������а���ֱ��֧�����ģ�5104��(�Ϻ���չ)
 * ֱ��֧����5201������ҵ������
 * @author hejianrong
 * @time 2014-09-19
 * 
 */
public class VoucherDto2MapFor5104 {

	private static Log logger = LogFactory.getLog(VoucherDto2MapFor5104.class);
	
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
		if(list.get(0).getStrecode()!=null&&list.get(0).getStrecode().startsWith("1702"))
			headMap.put("SRC", ITFECommonConstant.SRCCITY_NODE);
		else
			headMap.put("SRC", ITFECommonConstant.SRC_NODE);
		headMap.put("DES", ITFECommonConstant.DES_NODE);
		headMap.put("APP", MsgConstant.MSG_HEAD_APP);
		headMap.put("MsgNo", MsgConstant.PBC_DIRECT_IMPORT);
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
		HashMap<String, Object> head5104Map = new HashMap<String, Object>();
		head5104Map.put("TreCode", list.get(0).getStrecode()); // �������
		head5104Map.put("BillOrg", list.get(0).getSfinorgcode()); // ��Ʊ��λ
		head5104Map.put("PayeeBankNo", list.get(0).getSpayeeacctbankno()); // �տ����к�
		head5104Map.put("EntrustDate", list.get(0).getScommitdate()); // ί������
		head5104Map.put("PackNo", list.get(0).getSpackageno()); // �����
		head5104Map.put("AllNum", String.valueOf(list.size())); // �ܱ���
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������
		head5104Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // ֧��ƾ֤����  0����ֽƾ֤1����ֽƾ֤
		head5104Map.put("PayMode", list.get(0).getSpaytypecode()); 
		
		//���������ҵ�������Ϻ���չ��
		list=MtoCodeTrans.transformZeroAmtMainDto(list);
		
		List<Object> bill2201List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill5104Map = new HashMap<String, Object>();
			List<Object> detail51041List = new ArrayList<Object>();
			TfDirectpaymsgmainDto maindto = list.get(i);
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("Ҫת���Ķ����Ӧ���Ӽ�¼Ϊ��,��ȷ��![" + list.get(i).toString() + "]");
			}
			//���������ҵ�������Ϻ���չ��
			sublist=MtoCodeTrans.transformZeroAmtSubDto(maindto, sublist);
			
			allamt = allamt.add(maindto.getNpayamt());
			bill5104Map.put("TraNo", maindto.getSdealno()); // ������ˮ��
			bill5104Map.put("VouNo", maindto.getSvoucherno()); // ƾ֤���
			bill5104Map.put("VouDate", maindto.getSvoudate()); // ƾ֤����[��Ʊ����]
			bill5104Map.put("PayerAcct", maindto.getSpayacctno());//�������˺�
			bill5104Map.put("PayerName", maindto.getSpayacctname());//����������
			bill5104Map.put("PayerAddr", "");//�����˵�ַ 
			bill5104Map.put("PayeeAcct", maindto.getSpayeeacctno());//�տ����˺�
			bill5104Map.put("PayeeName", maindto.getSpayeeacctname());//�տ�������
			bill5104Map.put("PayeeAddr","");//�տ��˵�ַ
			bill5104Map.put("PayeeOpnBnkNo", maindto.getSpayeeacctbankno());//�տ��˿������к� 
			bill5104Map.put("AddWord", maindto.getSdemo());//���� 
			bill5104Map.put("BudgetType", MsgConstant.BDG_KIND_IN);//Ԥ������       Ĭ�� 1 Ԥ����
			bill5104Map.put("TrimSign", MsgConstant.TIME_FLAG_NORMAL);//�����ڱ�־
			bill5104Map.put("OfYear", maindto.getSstyear());//�������
			bill5104Map.put("BdgAdmType", ""); //Ԥ���������
			bill5104Map.put("Amt", MtoCodeTrans.transformString(maindto.getNpayamt()));//���
			bill5104Map.put("StatInfNum", sublist.size());//ͳ����Ϣ����
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
				HashMap<String, Object> detail5104Map = new HashMap<String, Object>();
				TfDirectpaymsgsubDto subdto = (TfDirectpaymsgsubDto) sublist.get(j);
				detail5104Map.put("SeqNo", subdto.getIseqno().toString());//���
				detail5104Map.put("BdgOrgCode", subdto.getSagencycode());//Ԥ�㵥λ����
				detail5104Map.put("FuncSbtCode", subdto.getSexpfunccode());//�������Ŀ����
				if (StateConstant.COMMON_YES.equals(s)) {//�������Ŀ����
					detail5104Map.put("EcnomicSubjectCode", MtoCodeTrans.transformString(subdto.getSexpecocode()));
				}else{
					detail5104Map.put("EcnomicSubjectCode", "");
				}
				detail5104Map.put("Amt", MtoCodeTrans.transformString(subdto.getNpayamt()));//���
				detail5104Map.put("AcctProp", MsgConstant.ACCT_PROP_ZERO);//�˻�����	     Ĭ�� 1 ����� 
				detail51041List.add(detail5104Map);
			}
			bill5104Map.put("Detail5104", detail51041List);
			bill2201List.add(bill5104Map);
		}
		
		head5104Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead5104", head5104Map);
		msgMap.put("Bill5104", bill2201List);
		return map;
	}	

}

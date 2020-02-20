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
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * ʵ���ʽ���ת��
 * 
 * @author zhouchuan
 * 
 */
public class Dto2MapFor5101 {

	private static Log logger = LogFactory.getLog(Dto2MapFor5101.class);
	
	/**
	 * DTOת��XML����(ʵ���ʽ�ҵ��)
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
	public static Map tranfor(List<TvPayoutmsgmainDto> list, String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException {
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
		headMap.put("MsgNo", MsgConstant.MSG_NO_5101);
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
		HashMap<String, Object> head5101Map = new HashMap<String, Object>();
		head5101Map.put("TreCode", list.get(0).getStrecode()); // �������
		head5101Map.put("BillOrg", list.get(0).getSpayunit()); // ��Ʊ��λ
		head5101Map.put("PayeeBankNo", list.get(0).getSpayeebankno()); // �տ����к�
		head5101Map.put("EntrustDate", list.get(0).getScommitdate().replaceAll("-", "")); // ί������
		head5101Map.put("PackNo", list.get(0).getSpackageno()); // �����
		head5101Map.put("AllNum", String.valueOf(list.size())); // �ܱ���
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������
		head5101Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // ֧��ƾ֤����  0����ֽƾ֤1����ֽƾ֤
		
		List<Object> bill5101List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			HashMap<String, Object> bill5101Map = new HashMap<String, Object>();
			List<Object> detail5101List = new ArrayList<Object>();

			TvPayoutmsgmainDto maindto = list.get(i);
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(maindto);
			if (null == sublist || sublist.size() == 0) {
				throw new ITFEBizException("Ҫת���Ķ����Ӧ���Ӽ�¼Ϊ��,��ȷ��![" + list.get(i).toString() + "]");
			}
			allamt = allamt.add(maindto.getNmoney());

			bill5101Map.put("TraNo", maindto.getSdealno()); // ������ˮ��
			bill5101Map.put("VouNo", maindto.getStaxticketno()); // ƾ֤���
			bill5101Map.put("VouDate", maindto.getSgenticketdate().replaceAll("-", "")); // ƾ֤����[��Ʊ����]
			bill5101Map.put("PayerAcct", maindto.getSpayeracct()); // �������˺�
			bill5101Map.put("PayerName", maindto.getSpayername()); // ����������
			bill5101Map.put("PayerAddr", maindto.getSpayeraddr()); // �����˵�ַ
			bill5101Map.put("Amt", MtoCodeTrans.transformString(maindto.getNmoney())); // ���׽��
			bill5101Map.put("PayeeOpBkNo", maindto.getSrecbankno()); // �տ��˿������к�
			bill5101Map.put("PayeeAcct", maindto.getSrecacct()); // �տ����˺�
			bill5101Map.put("PayeeName", maindto.getSrecname()); // �տ�������
			//����ʵ���ʽ��������޸�
			String addword = maindto.getSdemo();// ����demo�д�ŵ��ǵ���ԭ���֧��ԭ��
			if ("000059100005".equals(ITFECommonConstant.SRC_NODE)&&null != addword&&!addword.equals("")) { 
				addword = addword.getBytes().length >= 60 ? addword.substring(0, 30) : addword;
				bill5101Map.put("AddWord", addword); 
			}else{
				bill5101Map.put("AddWord", maindto.getSaddword()); // ����
			}
			bill5101Map.put("BdgOrgCode", maindto.getSbudgetunitcode()); // Ԥ�㵥λ����
			bill5101Map.put("BudgetOrgName", maindto.getSunitcodename()); // Ԥ�㵥λ����
			bill5101Map.put("OfYear", maindto.getSofyear()); // �������
			bill5101Map.put("BudgetType", maindto.getSbudgettype()); // Ԥ������
			bill5101Map.put("TrimSign", maindto.getStrimflag()); // �����ڱ�־
			bill5101Map.put("StatInfNum", sublist.size()); // ͳ����Ϣ����
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
				HashMap<String, Object> detail5101Map = new HashMap<String, Object>();
				TvPayoutmsgsubDto subdto = (TvPayoutmsgsubDto) sublist.get(j);
				
				detail5101Map.put("SeqNo", subdto.getSseqno());
				detail5101Map.put("FuncBdgSbtCode", subdto.getSfunsubjectcode());
				if (StateConstant.COMMON_YES.equals(s)) {
					detail5101Map.put("EcnomicSubjectCode", subdto.getSecnomicsubjectcode());
				}else{
					detail5101Map.put("EcnomicSubjectCode", "");
				}
				detail5101Map.put("BudgetPrjCode", subdto.getSbudgetprjcode());
				detail5101Map.put("Amt", MtoCodeTrans.transformString(subdto.getNmoney()));
				detail5101List.add(detail5101Map);
			}

			bill5101Map.put("Detail5101", detail5101List);
			bill5101List.add(bill5101Map);

		}

		head5101Map.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead5101", head5101Map);
		msgMap.put("Bill5101", bill5101List);
		
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

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
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * ʵ���ʽ���ת��
 * 
 * @author zhouchuan
 * 
 */
public class Dto2MapFor5101ForBatch {

	private static Log logger = LogFactory.getLog(Dto2MapFor5101ForBatch.class);
	
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
	 * @throws JAFDatabaseException 
	 */
	public static Map tranfor(TvPayoutmsgmainDto maindto, List<TvPayoutmsgsubDto> subList,String orgcode, String filename, String packno,boolean isRepeat) throws ITFEBizException, JAFDatabaseException {
		if (null == maindto || null==subList||subList.size()==0) {
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
		String tmpPackNo;
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
			tmpPackNo = SequenceGenerator
			.changePackNoForLocal(SequenceGenerator.getNextByDb2(
					SequenceName.FILENAME_PACKNO_REF_SEQ,
					SequenceName.TRAID_SEQ_CACHE,
					SequenceName.TRAID_SEQ_STARTWITH,
					MsgConstant.SEQUENCE_MAX_DEF_VALUE));
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}
		
		// ���ñ�����Ϣ�� MSG
		HashMap<String, Object> head5101Map = new HashMap<String, Object>();
		head5101Map.put("TreCode", maindto.getStrecode()); // �������
		head5101Map.put("BillOrg", maindto.getSpayunit()); // ��Ʊ��λ
		head5101Map.put("PayeeBankNo", subList.get(0).getSpayeebankno()); // �տ����к�
		head5101Map.put("EntrustDate", maindto.getScommitdate().replaceAll("-", "")); // ί������
		head5101Map.put("PackNo", tmpPackNo); // �����
		head5101Map.put("AllNum", String.valueOf(subList.size())); // �ܱ���
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��-��ֵ������
		head5101Map.put("PayoutVouType", MsgConstant.PAYOUT_VOU_TYPE_PAPER_YES); // ֧��ƾ֤����  0����ֽƾ֤1����ֽƾ֤
		List<Object> bill5101List = new ArrayList<Object>();
		List updateList = new ArrayList();
		for (int i = 0; i < subList.size(); i++) {
			HashMap<String, Object> bill5101Map = new HashMap<String, Object>();
			List<Object> detail5101List = new ArrayList<Object>();
			
			TvPayoutmsgsubDto subdto = (TvPayoutmsgsubDto) subList.get(i);
			
			allamt = allamt.add(subdto.getNmoney());
			String  strasno = VoucherUtil.getGrantSequence();
			subdto.setSpackageno(tmpPackNo);
			subdto.setStrasno(strasno.subSequence(8, 16).toString());
			updateList.add(subdto);
			bill5101Map.put("TraNo", strasno.subSequence(8, 16)); // ������ˮ��
			bill5101Map.put("VouNo", subdto.getSvoucherno()); // ��ϸ��ƾ֤���
			bill5101Map.put("VouDate", maindto.getSgenticketdate().replaceAll("-", "")); // ƾ֤����[��Ʊ����]
			bill5101Map.put("PayerAcct", maindto.getSpayeracct()); // �������˺�
			bill5101Map.put("PayerName", maindto.getSpayername()); // ����������
			bill5101Map.put("PayerAddr", maindto.getSpayeraddr()); // �����˵�ַ
			bill5101Map.put("Amt", MtoCodeTrans.transformString(subdto.getNmoney())); // ���׽��
			bill5101Map.put("PayeeOpBkNo", subdto.getSpayeebankno()); // �տ��˿������к�
			bill5101Map.put("PayeeAcct", subdto.getSpayeeacctno()); // �տ����˺�
			bill5101Map.put("PayeeName", subdto.getSpayeeacctname()); // �տ�������
			//����ʵ���ʽ��������޸�
			String addword = subdto.getSpaysummaryname();// ����demo�д�ŵ��ǵ���ԭ���֧��ԭ��
			if ("000059100005".equals(ITFECommonConstant.SRC_NODE)&&null != addword&&!addword.equals("")) { 
				addword = addword.getBytes().length >= 60 ? addword.substring(0, 30) : addword;
				bill5101Map.put("AddWord", addword); 
			}else{
				bill5101Map.put("AddWord",subdto.getSpaysummaryname()); // ����
			}
			bill5101Map.put("BdgOrgCode", subdto.getSagencycode()); // Ԥ�㵥λ����
			bill5101Map.put("BudgetOrgName", subdto.getSagencyname()); // Ԥ�㵥λ����
			bill5101Map.put("OfYear", maindto.getSofyear()); // �������
			bill5101Map.put("BudgetType", maindto.getSbudgettype()); // Ԥ������
			bill5101Map.put("TrimSign", maindto.getStrimflag()); // �����ڱ�־
			bill5101Map.put("StatInfNum", 1); // ͳ����Ϣ����
			String s = "0";
			try {
				HashMap<String,TsTreasuryDto> mapTreInfo =SrvCacheFacade.cacheTreasuryInfo(null);
			    s = mapTreInfo.get(maindto.getStrecode()).getSisuniontre();
			} catch (JAFDatabaseException e) {
				logger.error("���ҹ�������쳣!", e);
				throw new ITFEBizException("���ҹ�������쳣��", e);
			}
				HashMap<String, Object> detail5101Map = new HashMap<String, Object>();
				
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
			
			bill5101Map.put("Detail5101", detail5101List);
			bill5101List.add(bill5101Map);

		}
		DatabaseFacade.getODB().update(CommonUtil.listTArray(updateList));
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

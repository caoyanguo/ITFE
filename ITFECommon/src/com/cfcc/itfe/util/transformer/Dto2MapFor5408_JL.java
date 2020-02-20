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
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvNontaxsubDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
public class Dto2MapFor5408_JL {
	
	private static Log logger = LogFactory.getLog(Dto2MapFor5408_JL.class);
	
	/**
	 * DTOת��XML����(��˰����)Tips�ɰ汾
	 * 
	 * @param List
	 *            <TvInfileDto> list ���ͱ��Ķ��󼯺�
	 * @param String
	 *            orgcode ��������
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            packno ����ˮ��
	 * @return
	 * @throws ITFEBizException
	 */
	@SuppressWarnings("unchecked")
	public static Map tranfor(List<TvNontaxmainDto> list,String orgcode,String filename,String packno ,boolean isRepeat) throws ITFEBizException{
		if(null == list || list.size() == 0){
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
		headMap.put("MsgNo",MsgConstant.MSG_NO_7211);
		headMap.put("WorkDate", TimeFacade.getCurrentStringTime());
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headMap.put("MsgID", msgid);
			headMap.put("MsgRef", msgid);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}

		/** 
		 * ���ñ�����Ϣ�� MSG
		 * ��һ������ͷ��һ������ͷ��һ��ת����Ϣ�Ρ����������Ϣ��(����һ��������Ϣ�Ρ�һ��˰Ʊ��Ϣ�Ρ�һ��˰����Ϣ��(�����㵽���˰Ŀ��Ϣ��))����
		 */
		//1.1������ͷ
		HashMap<String, String> batchHeadMap = new HashMap<String, String>();
		batchHeadMap.put("TaxOrgCode", list.get(0).getStaxorgcode());// ���ջ��ش���
		batchHeadMap.put("EntrustDate", list.get(0).getScommitdate().replaceAll("-", "")); // ί������
		batchHeadMap.put("PackNo", list.get(0).getSpackageno()); // ����ˮ��
		batchHeadMap.put("AllNum", String.valueOf(list.size())); // �ܱ���
		BigDecimal allamt = new BigDecimal("0.00"); // �ܽ��
//		batchHeadMap.put("AllAmt", "0.00");

		// 1.2��ת����Ϣ
		HashMap<String, String> turnAccountMap = new HashMap<String, String>();
		turnAccountMap.put("BizType", MsgConstant.MSG_BIZ_MODEL_NET); // ҵ��ģʽ[�ط���������ҵ��]
		turnAccountMap.put("FundSrlNo", MtoCodeTrans.transformString(list.get(0).getShold1())); // �ʽ�������ˮ��
		turnAccountMap.put("PayBnkNo", MtoCodeTrans.transformString(list.get(0).getSpaybankno())); // �������к�
		turnAccountMap.put("PayeeTreCode", list.get(0).getStrecode()); // �տ�������
//		turnAccountMap.put("PayeeTreName", MtoCodeTrans.transformString(null)); // �տ��������
		
		// 1.3��������(���������Ϣ��)
		HashMap<String, Object> taxBody7211Map = new HashMap<String, Object>();
		
		List<Object> taxInfo7211List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			TvNontaxsubDto subdto = new TvNontaxsubDto();
			subdto.setSdealno(list.get(i).getSdealno());
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(list.get(i));
			
			allamt = allamt.add(list.get(i).getNmoney()); // ������ܽ��
			
			// 1.3.1��������(������Ϣ��)
			HashMap<String, Object> taxInfo7211Map = new HashMap<String, Object>();
			
			// 1.3.1.1��������(������Ϣ��)- һ��������Ϣ��
			HashMap<String, String> payment7211Map = new HashMap<String, String>();
			if (list.get(i).getSid().length()<8) {
				String transno = "00000000"+list.get(i).getSid();
				payment7211Map.put("TraNo", transno); // ������ˮ��
			}else{
			    payment7211Map.put("TraNo", list.get(i).getSid()); // ������ˮ��
			}
			
			payment7211Map.put("TraAmt", MtoCodeTrans.transformString(list.get(i).getNmoney())); //���׽��
			payment7211Map.put("PayOpBnkNo", MtoCodeTrans.transformString(list.get(i).getSpaybankno())); //��������к�
//			payment7211Map.put("PayOpBnkName", MtoCodeTrans.transformString(null)); // �����������
			if(null == list.get(i).getStaxpayname() || "".equals(list.get(i).getStaxpayname().trim())){
				payment7211Map.put("HandOrgName", "dfhl"); // �ɿλ����
			}else{
				payment7211Map.put("HandOrgName", MtoCodeTrans.transformString(list.get(i).getSpayacctname())); // �ɿλ����
			}
			
			payment7211Map.put("PayAcct", MtoCodeTrans.transformString(list.get(i).getSpayacctno())); // �����˻�

			// 1.3.1.2��������(������Ϣ��)-һ��˰Ʊ��Ϣ��
			HashMap<String, String> taxVou7211Map = new HashMap<String, String>();
			taxVou7211Map.put("TaxVouNo", list.get(i).getSvoucherno()); // ˰Ʊ����
			taxVou7211Map.put("BillDate", list.get(i).getSvoudate()); // ��Ʊ����
			if(null == list.get(i).getStaxpaycode() || "".equals(list.get(i).getStaxpaycode().trim())){
				taxVou7211Map.put("TaxPayCode", "10101"); // ��˰�˱���
			}else{
				taxVou7211Map.put("TaxPayCode", MtoCodeTrans.transformString(list.get(i).getStaxpaycode())); // ��˰�˱���
			}
			if(null == list.get(i).getStaxpayname() || "".equals(list.get(i).getStaxpayname().trim())){
				taxVou7211Map.put("TaxPayName", "dfhl"); // ��˰������
			}else{
				taxVou7211Map.put("TaxPayName", MtoCodeTrans.transformString(list.get(i).getStaxpayname())); // ��˰������
			}

			taxVou7211Map.put("BudgetType", list.get(i).getSbudgettype()); // Ԥ������
			taxVou7211Map.put("TrimSign", list.get(i).getStrimflag()); // �����ڱ�־
			taxVou7211Map.put("CorpCode",  MtoCodeTrans.transformString(list.get(i).getStaxpaycode())); // ��ҵ����
			taxVou7211Map.put("CorpName", MtoCodeTrans.transformString(list.get(i).getStaxpayname())); // ��ҵ����
//			taxVou7211Map.put("CorpType", MtoCodeTrans.transformString(null)); // ��ҵע������
//			taxVou7211Map.put("Remark", MtoCodeTrans.transformString(null)); // ��ע
//			taxVou7211Map.put("Remark1", MtoCodeTrans.transformString(null)); // ��ע1
//			taxVou7211Map.put("Remark2", MtoCodeTrans.transformString(null)); // ��ע2

			// 1.3.1.3��������(������Ϣ��)-һ��˰����Ϣ��
			HashMap<String, Object> taxType7211Map = new HashMap<String, Object>();
			taxType7211Map.put("BudgetSubjectCode", ((TvNontaxsubDto)sublist.get(0)).getSbudgetsubject()); // Ԥ���Ŀ����
//			taxType7211Map.put("BudgetSubjectName", MtoCodeTrans.transformString(null)); // Ԥ���Ŀ����
			
			if(null == list.get(i).getSexpdate() || "".equals(list.get(i).getSexpdate().trim())){
				taxType7211Map.put("LimitDate", list.get(i).getSvoudate().replaceAll("-", "")); // �޽�����
			}else{
				taxType7211Map.put("LimitDate", list.get(i).getSexpdate().replaceAll("-", "")); // �޽�����
			}
			
			taxType7211Map.put("TaxTypeCode", MtoCodeTrans.transformString(list.get(i).getStaxtypecode())); // ˰�ִ���
			taxType7211Map.put("TaxTypeName", MtoCodeTrans.transformString(list.get(i).getStaxtypename())); // ˰������
			taxType7211Map.put("BudgetLevelCode", list.get(i).getSbudgetlevelcode()); // Ԥ�㼶�δ���
			taxType7211Map.put("BudgetLevelName", list.get(i).getSbudgetlevelname()); // Ԥ�㼶������
			
			if(null == list.get(i).getStaxstartdate() || "".equals(list.get(i).getStaxstartdate().trim())){
				taxType7211Map.put("TaxStartDate", list.get(i).getSvoudate()); // ˰������������
			}else{
				taxType7211Map.put("TaxStartDate", list.get(i).getStaxstartdate()); // ˰������������
			}
			
			if(null == list.get(i).getSexpdate() || "".equals(list.get(i).getSexpdate().trim())){
				taxType7211Map.put("TaxEndDate", list.get(i).getSvoudate().replaceAll("-", "")); // �޽�����
			}else{
				taxType7211Map.put("TaxEndDate", list.get(i).getSexpdate().replaceAll("-", "")); // ˰����������ֹ
			}
			
			
			taxType7211Map.put("ViceSign", MtoCodeTrans.transformString(((TvNontaxsubDto)sublist.get(i)).getSvicesign())); // ������־
			taxType7211Map.put("TaxType", "1"); // ˰������
			taxType7211Map.put("HandBookKind", "2"); // �ɿ�������
			
			
			if(MsgConstant.MSG_SOURCE_PLACE.equals(list.get(i).getSsourceflag())){
				if(null == list.get(i).getSext1() || null == list.get(i).getSext1()){
					taxType7211Map.put("DetailNum", 0); //��ϸ����
					taxType7211Map.put("SubjectList7211", new ArrayList<Object>());
				}else{
					taxType7211Map.put("DetailNum", 1); //��ϸ����
					HashMap<String, Object> subjectList7211Map = new HashMap<String, Object>();
					subjectList7211Map.put("DetailNo", "1");
					subjectList7211Map.put("TaxSubjectCode", MtoCodeTrans.transformString(list.get(i).getStaxtypecode()));
					subjectList7211Map.put("TaxSubjectName", MtoCodeTrans.transformString(list.get(i).getStaxtypename()));
					subjectList7211Map.put("TaxNumber", MtoCodeTrans.transformString(list.get(i).getScount()));
					subjectList7211Map.put("TaxAmt", MtoCodeTrans.transformString(list.get(i).getNmoney()));
					subjectList7211Map.put("TaxRate", MtoCodeTrans.transformString(list.get(i).getNmoney()));
					subjectList7211Map.put("ExpTaxAmt", MtoCodeTrans.transformString(list.get(i).getNmoney()));
					subjectList7211Map.put("DiscountTaxAmt", MtoCodeTrans.transformString(list.get(i).getNmoney()));
					subjectList7211Map.put("FactTaxAmt", MtoCodeTrans.transformString(list.get(i).getNmoney()));
					
					List<Object> subjectList7211List = new ArrayList<Object>();
					subjectList7211List.add(subjectList7211Map);
					taxType7211Map.put("SubjectList7211", subjectList7211List);
				}
			}else{
				taxType7211Map.put("DetailNum", 0); //��ϸ����
				taxType7211Map.put("SubjectList7211", new ArrayList<Object>());
			}
			
			// 1.3.1.3.1��������(������Ϣ��)-һ��˰����Ϣ_���������˰Ŀ��Ϣ��
//			taxType7211Map.put("SubjectList7211", new ArrayList<Object>());
			
			taxInfo7211Map.put("Payment7211", payment7211Map);
			taxInfo7211Map.put("TaxVou7211", taxVou7211Map);
			taxInfo7211Map.put("TaxType7211", taxType7211Map);
			taxInfo7211List.add(taxInfo7211Map);
		}
		
		taxBody7211Map.put("TaxInfo7211", taxInfo7211List);
		batchHeadMap.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead7211", batchHeadMap);
		msgMap.put("TurnAccount7211", turnAccountMap);
		msgMap.put("TaxBody7211", taxBody7211Map);
		return map;
	}

}

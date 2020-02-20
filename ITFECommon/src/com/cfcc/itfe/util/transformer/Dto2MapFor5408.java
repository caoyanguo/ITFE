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
public class Dto2MapFor5408 {
	
	private static Log logger = LogFactory.getLog(Dto2MapFor5408.class);
	
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
		TvNontaxsubDto subdto = null;
		TvNontaxmainDto maindto = null;
		for (int i = 0; i < list.size(); i++) {
			maindto = list.get(i);
			List<IDto> sublist = PublicSearchFacade.findSubDtoByMain(list.get(i));
			allamt = allamt.add(list.get(i).getNmoney()); // ������ܽ��
			for(int j=0;j<sublist.size();j++)
			{
				HashMap<String, Object> taxInfo7211Map = new HashMap<String, Object>();// 1.3.1.1��������(������Ϣ��)- һ��������Ϣ��
				subdto = (TvNontaxsubDto)sublist.get(j);
				HashMap<String, String> payment7211Map = new HashMap<String, String>();
				if (list.get(i).getSid().length()<8) {
					String transno = "10000000"+list.get(i).getSid();
					payment7211Map.put("TraNo", transno); // ������ˮ��
				}else{
				    payment7211Map.put("TraNo", list.get(i).getSid()); // ������ˮ��
				}
				payment7211Map.put("TraAmt", MtoCodeTrans.transformString(subdto.getNtraamt())); //���׽��
				payment7211Map.put("PayOpBnkNo", MtoCodeTrans.transformString(maindto.getSpaybankno())); //��������к�
				payment7211Map.put("PayOpBnkName", MtoCodeTrans.transformString(maindto.getSpayacctbankname())); // �����������
				if(null == subdto.getShold2() || "".equals(subdto.getShold2().trim())){
					payment7211Map.put("HandOrgName", "dfhl"); // �ɿλ����
				}else{
					payment7211Map.put("HandOrgName", subdto.getShold2()); // �ɿλ����
				}
				
				payment7211Map.put("PayAcct", MtoCodeTrans.transformString(maindto.getSpayacctno())); // �����˻�

				// 1.3.1.2��������(������Ϣ��)-һ��˰Ʊ��Ϣ��
				HashMap<String, String> taxVou7211Map = new HashMap<String, String>();
				taxVou7211Map.put("TaxVouNo", MtoCodeTrans.transformString(subdto.getShold1())); // ˰Ʊ����
				taxVou7211Map.put("BillDate", MtoCodeTrans.transformString(maindto.getSvoudate())); // ��Ʊ����
				if(null == subdto.getShold1() || "".equals(subdto.getShold1().trim())){
					taxVou7211Map.put("TaxPayCode", "10101"); // ��˰�˱���
				}else{
					taxVou7211Map.put("TaxPayCode", MtoCodeTrans.transformString(subdto.getShold1())); // ��˰�˱���
				}
				if(null == subdto.getShold2() || "".equals(subdto.getShold2().trim())){
					taxVou7211Map.put("TaxPayName", "dfhl"); // ��˰������
				}else{
					taxVou7211Map.put("TaxPayName", MtoCodeTrans.transformString(subdto.getShold2())); // ��˰������
				}

				taxVou7211Map.put("BudgetType", MtoCodeTrans.transformString(maindto.getSbudgettype())); // Ԥ������
				taxVou7211Map.put("TrimSign", MtoCodeTrans.transformString(maindto.getStrimflag())); // �����ڱ�־
				taxVou7211Map.put("CorpCode",  MtoCodeTrans.transformString(maindto.getStaxpaycode())); // ��ҵ����
				taxVou7211Map.put("CorpName", MtoCodeTrans.transformString(maindto.getStaxpayname())); // ��ҵ����

				// 1.3.1.3��������(������Ϣ��)-һ��˰����Ϣ��
				HashMap<String, Object> taxType7211Map = new HashMap<String, Object>();
				taxType7211Map.put("BudgetSubjectCode", MtoCodeTrans.transformString(subdto.getSitemcode())); // Ԥ���Ŀ����
				taxType7211Map.put("BudgetSubjectName", MtoCodeTrans.transformString(subdto.getSitemname())); // Ԥ���Ŀ����
				
				if(null == maindto.getSexpdate() || "".equals(maindto.getSexpdate().trim())){
					taxType7211Map.put("LimitDate", maindto.getSvoudate().replaceAll("-", "")); // �޽�����
				}else{
					taxType7211Map.put("LimitDate", maindto.getSexpdate().replaceAll("-", "")); // �޽�����
				}
				
				taxType7211Map.put("TaxTypeCode", MtoCodeTrans.transformString(maindto.getStaxtypecode())); // ˰�ִ���
				taxType7211Map.put("TaxTypeName", MtoCodeTrans.transformString(maindto.getStaxtypename())); // ˰������
				taxType7211Map.put("BudgetLevelCode", MtoCodeTrans.transformString(subdto.getSxaddword())); // Ԥ�㼶�δ���
				if(subdto.getSxaddword()!=null&&"0".equals(subdto.getSxaddword()))
					taxType7211Map.put("BudgetLevelName", "����"); // Ԥ�㼶������
				else if(subdto.getSxaddword()!=null&&"1".equals(subdto.getSxaddword()))
					taxType7211Map.put("BudgetLevelName", "����"); // Ԥ�㼶������
				else if(subdto.getSxaddword()!=null&&"2".equals(subdto.getSxaddword()))
					taxType7211Map.put("BudgetLevelName", "ʡ"); // Ԥ�㼶������
				else if(subdto.getSxaddword()!=null&&"3".equals(subdto.getSxaddword()))
					taxType7211Map.put("BudgetLevelName", "��"); // Ԥ�㼶������
				else if(subdto.getSxaddword()!=null&&"4".equals(subdto.getSxaddword()))
					taxType7211Map.put("BudgetLevelName", "��"); // Ԥ�㼶������
				else if(subdto.getSxaddword()!=null&&"5".equals(subdto.getSxaddword()))
					taxType7211Map.put("BudgetLevelName", "��"); // Ԥ�㼶������
				else
					taxType7211Map.put("BudgetLevelName", MtoCodeTrans.transformString(maindto.getSbudgetlevelname())); // Ԥ�㼶������
				
				if(null == list.get(i).getStaxstartdate() || "".equals(list.get(i).getStaxstartdate().trim())){
					taxType7211Map.put("TaxStartDate", maindto.getSvoudate()); // ˰������������
				}else{
					taxType7211Map.put("TaxStartDate", maindto.getStaxstartdate()); // ˰������������
				}
				
				if(null == list.get(i).getSexpdate() || "".equals(list.get(i).getSexpdate().trim())){
					taxType7211Map.put("TaxEndDate", maindto.getSvoudate().replaceAll("-", "")); // �޽�����
				}else{
					taxType7211Map.put("TaxEndDate", maindto.getSexpdate().replaceAll("-", "")); // ˰����������ֹ
				}
				taxType7211Map.put("ViceSign", MtoCodeTrans.transformString(subdto.getSvicesign())); // ������־
				taxType7211Map.put("TaxType", "1"); // ˰������
				taxType7211Map.put("HandBookKind", "2"); // �ɿ�������
				taxType7211Map.put("DetailNum", 0); //��ϸ����
				taxType7211Map.put("SubjectList7211", new ArrayList<Object>());
				
				// 1.3.1.3.1��������(������Ϣ��)-һ��˰����Ϣ_���������˰Ŀ��Ϣ��
//				taxType7211Map.put("SubjectList7211", new ArrayList<Object>());
				
				taxInfo7211Map.put("Payment7211", payment7211Map);
				taxInfo7211Map.put("TaxVou7211", taxVou7211Map);
				taxInfo7211Map.put("TaxType7211", taxType7211Map);
				taxInfo7211List.add(taxInfo7211Map);
			}
		}
		
		taxBody7211Map.put("TaxInfo7211", taxInfo7211List);
		batchHeadMap.put("AllAmt", MtoCodeTrans.transformString(allamt));
		msgMap.put("BatchHead7211", batchHeadMap);
		msgMap.put("TurnAccount7211", turnAccountMap);
		msgMap.put("TaxBody7211", taxBody7211Map);
		
//		// ����ƾ֤״̬Ϊ�Ѿ�����
//		
//		String updateSQL = "update TV_VOUCHERINFO set S_STATUS = ? where S_ORGCODE = ? and S_FILENAME = ? and S_PACKAGENO = ? and S_COMMITDATE = ? ";
//		SQLExecutor sqlExec;
//		try {
//			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
//			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING); // ������
//			sqlExec.addParam(orgcode);
//			sqlExec.addParam(filename);
//			sqlExec.addParam(packno);
//			sqlExec.addParam(list.get(0).getScommitdate());
//			sqlExec.runQueryCloseCon(updateSQL);
//		} catch (JAFDatabaseException e) {
//			logger.error("����˰Ʊ��ϸ״̬��ʱ������쳣��", e);
//			throw new ITFEBizException("����˰Ʊ��ϸ״̬��ʱ������쳣��", e);
//		}
		
//		// ���±���ͷ��״̬Ϊ������
//		String updateFileSQL = "update TV_FILEPACKAGEREF set S_RETCODE = ? where S_ORGCODE = ? and S_TRECODE = ? and S_FILENAME = ? and S_PACKAGENO = ? ";
//		SQLExecutor sqlUpdateExec;
//		try {
//			sqlUpdateExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
//			sqlUpdateExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING); // ������
//			sqlUpdateExec.addParam(orgcode);
//			sqlUpdateExec.addParam(list.get(0).getStrecode());
//			sqlUpdateExec.addParam(filename);
//			sqlUpdateExec.addParam(packno);
//			sqlUpdateExec.runQueryCloseCon(updateFileSQL);
//		} catch (JAFDatabaseException e) {
//			logger.error("���±���ͷ��ʱ������쳣��", e);
//			throw new ITFEBizException("���±���ͷ��ʱ������쳣��", e);
//		}
		return map;
	}

}

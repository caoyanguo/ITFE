package com.cfcc.itfe.util.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvInfileDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class Dto2MapFor7211 {

	private static Log logger = LogFactory.getLog(Dto2MapFor7211.class);

	/**
	 * DTOת��XML����(����˰Ʊҵ��)
	 * 
	 * @param List
	 *            <TvInfileDto> list ���ͱ��Ķ��󼯺�
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
	public static Map tranfor(List<TvInfileDto> list,String orgcode,String filename,String packno,boolean isRepeat) throws ITFEBizException{
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
		turnAccountMap.put("FundSrlNo", MtoCodeTrans.transformString(list.get(0).getStrasrlno())); // �ʽ�������ˮ��
		turnAccountMap.put("PayBnkNo", MtoCodeTrans.transformString(list.get(0).getSpaybnkno())); // �������к�
		turnAccountMap.put("PayeeTreCode", list.get(0).getSrecvtrecode()); // �տ�������
//		turnAccountMap.put("PayeeTreName", MtoCodeTrans.transformString(null)); // �տ��������
		
		// 1.3��������(���������Ϣ��)
		HashMap<String, Object> taxBody7211Map = new HashMap<String, Object>();
		
		List<Object> taxInfo7211List = new ArrayList<Object>();
		for (int i = 0; i < list.size(); i++) {
			allamt = allamt.add(list.get(i).getNmoney()); // ������ܽ��
			
			// 1.3.1��������(������Ϣ��)
			HashMap<String, Object> taxInfo7211Map = new HashMap<String, Object>();
			
			// 1.3.1.1��������(������Ϣ��)- һ��������Ϣ��
			HashMap<String, String> payment7211Map = new HashMap<String, String>();
			payment7211Map.put("TraNo", list.get(i).getSdealno()); // ������ˮ��
			payment7211Map.put("TraAmt", MtoCodeTrans.transformString(list.get(i).getNmoney())); //���׽��
			payment7211Map.put("PayOpBnkNo", MtoCodeTrans.transformString(list.get(i).getSopenaccbankcode())); //��������к�
//			payment7211Map.put("PayOpBnkName", MtoCodeTrans.transformString(null)); // �����������
			if(null == list.get(i).getStaxpayname() || "".equals(list.get(i).getStaxpayname().trim())){
				payment7211Map.put("HandOrgName", "dfhl"); // �ɿλ����
			}else{
				payment7211Map.put("HandOrgName", MtoCodeTrans.transformString(list.get(i).getStaxpayname())); // �ɿλ����
			}
			
			payment7211Map.put("PayAcct", MtoCodeTrans.transformString(list.get(i).getSpayacct())); // �����˻�

			// 1.3.1.2��������(������Ϣ��)-һ��˰Ʊ��Ϣ��
			HashMap<String, String> taxVou7211Map = new HashMap<String, String>();
			taxVou7211Map.put("TaxVouNo", list.get(i).getStaxticketno()); // ˰Ʊ����
			taxVou7211Map.put("BillDate", list.get(i).getSgenticketdate()); // ��Ʊ����
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
			taxType7211Map.put("BudgetSubjectCode", list.get(i).getSbudgetsubcode()); // Ԥ���Ŀ����
//			taxType7211Map.put("BudgetSubjectName", MtoCodeTrans.transformString(null)); // Ԥ���Ŀ����
			
			if(null == list.get(i).getSlimitdate() || "".equals(list.get(i).getSlimitdate().trim())){
				taxType7211Map.put("LimitDate", list.get(i).getSaccdate().replaceAll("-", "")); // �޽�����
			}else{
				taxType7211Map.put("LimitDate", list.get(i).getSlimitdate().replaceAll("-", "")); // �޽�����
			}
			
			taxType7211Map.put("TaxTypeCode", MtoCodeTrans.transformString(list.get(i).getStaxtypecode())); // ˰�ִ���
			taxType7211Map.put("TaxTypeName", MtoCodeTrans.transformString(list.get(i).getStaxtypename())); // ˰������
			taxType7211Map.put("BudgetLevelCode", list.get(i).getSbudgetlevelcode()); // Ԥ�㼶�δ���
			taxType7211Map.put("BudgetLevelName", PublicSearchFacade.getBdgLevelNameByCode(list.get(i).getSbudgetlevelcode())); // Ԥ�㼶������
			
			if(null == list.get(i).getStaxstartdate() || "".equals(list.get(i).getStaxstartdate().trim())){
				taxType7211Map.put("TaxStartDate", list.get(i).getSaccdate()); // ˰������������
			}else{
				taxType7211Map.put("TaxStartDate", list.get(i).getStaxstartdate()); // ˰������������
			}
			
			if(null == list.get(i).getStaxenddate() || "".equals(list.get(i).getStaxenddate().trim())){
				taxType7211Map.put("TaxEndDate", list.get(i).getSaccdate().replaceAll("-", "")); // �޽�����
			}else{
				taxType7211Map.put("TaxEndDate", list.get(i).getStaxenddate().replaceAll("-", "")); // ˰����������ֹ
			}
			
			
			taxType7211Map.put("ViceSign", MtoCodeTrans.transformString(list.get(i).getSassitsign())); // ������־
			taxType7211Map.put("TaxType", "1"); // ˰������
			taxType7211Map.put("HandBookKind", "2"); // �ɿ�������
			
			
			if(MsgConstant.MSG_SOURCE_PLACE.equals(list.get(i).getSsourceflag())){
				if(null == list.get(i).getNtaxamt() || null == list.get(i).getNfacttaxamt()){
					taxType7211Map.put("DetailNum", 0); //��ϸ����
					taxType7211Map.put("SubjectList7211", new ArrayList<Object>());
				}else{
					taxType7211Map.put("DetailNum", 1); //��ϸ����
					HashMap<String, Object> subjectList7211Map = new HashMap<String, Object>();
					subjectList7211Map.put("DetailNo", "1");
					subjectList7211Map.put("TaxSubjectCode", MtoCodeTrans.transformString(list.get(i).getStaxtypecode()));
					subjectList7211Map.put("TaxSubjectName", MtoCodeTrans.transformString(list.get(i).getStaxsubjectname()));
					subjectList7211Map.put("TaxNumber", MtoCodeTrans.transformString(list.get(i).getStaxnumber()));
					subjectList7211Map.put("TaxAmt", MtoCodeTrans.transformString(list.get(i).getNtaxamt()));
					subjectList7211Map.put("TaxRate", MtoCodeTrans.transformString(list.get(i).getNtaxrate()));
					subjectList7211Map.put("ExpTaxAmt", MtoCodeTrans.transformString(list.get(i).getNfacttaxamt()));
					subjectList7211Map.put("DiscountTaxAmt", MtoCodeTrans.transformString(list.get(i).getNdiscounttaxamt()));
					subjectList7211Map.put("FactTaxAmt", MtoCodeTrans.transformString(list.get(i).getNfacttaxamt()));
					
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
		
		// ����ƾ֤״̬Ϊ�Ѿ�����
		String updateSQL = "update TV_INFILE set S_STATUS = ? where S_ORGCODE = ? and S_FILENAME = ? and S_PACKAGENO = ? and S_COMMITDATE = ? ";
		SQLExecutor sqlExec;
		try {
			sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING); // ������
			sqlExec.addParam(orgcode);
			sqlExec.addParam(filename);
			sqlExec.addParam(packno);
			sqlExec.addParam(list.get(0).getScommitdate());
			sqlExec.runQueryCloseCon(updateSQL);
		} catch (JAFDatabaseException e) {
			logger.error("����˰Ʊ��ϸ״̬��ʱ������쳣��", e);
			throw new ITFEBizException("����˰Ʊ��ϸ״̬��ʱ������쳣��", e);
		}
		
		// ���±���ͷ��״̬Ϊ������
		String updateFileSQL = "update TV_FILEPACKAGEREF set S_RETCODE = ? where S_ORGCODE = ? and S_TRECODE = ? and S_FILENAME = ? and S_PACKAGENO = ? ";
		SQLExecutor sqlUpdateExec;
		try {
			sqlUpdateExec = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			sqlUpdateExec.addParam(DealCodeConstants.DEALCODE_ITFE_DEALING); // ������
			sqlUpdateExec.addParam(orgcode);
			sqlUpdateExec.addParam(list.get(0).getSrecvtrecode());
			sqlUpdateExec.addParam(filename);
			sqlUpdateExec.addParam(packno);
			sqlUpdateExec.runQueryCloseCon(updateFileSQL);
		} catch (JAFDatabaseException e) {
			logger.error("���±���ͷ��ʱ������쳣��", e);
			throw new ITFEBizException("���±���ͷ��ʱ������쳣��", e);
		}
	
//		// ���°�ͷ״̬Ϊ�Ѿ����� 
//		TvFilepackagerefDto packdto = new TvFilepackagerefDto();
//		packdto.setSorgcode(orgcode); // ��������
//		packdto.setStrecode(list.get(0).getSrecvtrecode()); // �������
//		packdto.setSfilename(filename); // �����ļ���
//		packdto.setStaxorgcode(list.get(0).getStaxorgcode()); // ���ش���
//		packdto.setScommitdate(list.get(0).getScommitdate()); // ί������
//		packdto.setSaccdate(TimeFacade.getCurrentStringTime()); // ��������
//		packdto.setSpackageno(packno); // ����ˮ��
//		packdto.setSoperationtypecode(BizTypeConstant.BIZ_TYPE_INCOME); // ҵ������
//		packdto.setIcount(list.size()); // �ܱ���
//		packdto.setNmoney(allamt); // �ܽ��
//		packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_DEALING); // ������
//		packdto.setSusercode(list.get(0).getSusercode()); // ����Ա����
//		packdto.setSdemo(list.get(0).getStrasrlno()); // �ʽ�������ˮ��
//		packdto.setImodicount(2);
//
//		try {
//			DatabaseFacade.getDb().update(packdto);
//		} catch (JAFDatabaseException e) {
//			logger.error("���±���ͷ��ʱ������쳣��", e);
//			throw new ITFEBizException("���±���ͷ��ʱ������쳣��", e);
//		}
		
		return map;
	}
}

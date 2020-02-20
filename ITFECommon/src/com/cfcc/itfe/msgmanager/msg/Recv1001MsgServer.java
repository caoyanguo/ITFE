/**
 * ʵʱ��˰����
 */
package com.cfcc.itfe.msgmanager.msg;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvTaxDto;
import com.cfcc.itfe.persistence.dto.TvTaxItemDto;
import com.cfcc.itfe.persistence.dto.TvTaxKindDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * 
 * @author wangtuo
 * 
 */
public class Recv1001MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv1001MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// ��������ͷ headMap
		String orgcode = (String) headMap.get("DES");// ���ջ�������
		String sendorgcode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��
		String sbookorgcode = sendorgcode;

		/**
		 * ����ʵʱҵ��ͷ msgMap-->RealHead1001
		 */
		HashMap realhead1001 = (HashMap) msgMap.get("RealHead1001");

		String taxorgcode = (String) realhead1001.get("TaxOrgCode");// ���ջ��ش���
		String entrustdate = (String) realhead1001.get("EntrustDate");// ί������
		String trano = (String) realhead1001.get("TraNo");// ������ˮ��

		/**
		 * ����ת����Ϣ msgMap-->TurnAccount1001
		 */
		HashMap turnaccount1001 = (HashMap) msgMap.get("TurnAccount1001");

		String handletype = (String) turnaccount1001.get("HandleType");// �������
		String payeebankno = (String) turnaccount1001.get("PayeeBankNo");// �տ����к�
		String payeeorgcode = (String) turnaccount1001.get("PayeeOrgCode");// �տλ����
		String payeeacct = (String) turnaccount1001.get("PayeeAcct");// �տ����˺�
		String payeename = (String) turnaccount1001.get("PayeeName");// �տ�������
		String paybkcode = (String) turnaccount1001.get("PayBkCode");// �������к�
		String payopbkcode = (String) turnaccount1001.get("PayOpBkCode");// ���������

		/**
		 * ����������Ϣ��������Ϣ msgMap-->Payment1001
		 */
		HashMap payment1001 = (HashMap) msgMap.get("Payment1001");

		String payacct = (String) payment1001.get("PayAcct");// �����˻�
		String handorgname = (String) payment1001.get("HandOrgName");// �ɿλ����
		String traamt = (String) payment1001.get("TraAmt");// ���׽��
		String Yaxvouno = (String) payment1001.get("TaxVouNo");// ˰Ʊ����
		String billdate = (String) payment1001.get("BillDate");// ��Ʊ����
		String taxpaycode = (String) payment1001.get("TaxPayCode");// ��˰�˱���
		String taxpayname = (String) payment1001.get("TaxPayName");// ��˰������
		String corpcode = (String) payment1001.get("CorpCode");// ��ҵ����
		String protocolno = (String) payment1001.get("ProtocolNo");// Э�����
		String budgettype = (String) payment1001.get("BudgetType");// Ԥ������
		String trimsign = (String) payment1001.get("TrimSign");// �����ڱ�־
		String corptype = (String) payment1001.get("CorpType");// ��ҵע������
		String printvousign = (String) payment1001.get("PrintVouSign");// ��ӡ����ƾ֤��־
		String remark = (String) payment1001.get("Remark");// ��ע
		String remark1 = (String) payment1001.get("Remark1");// ��ע1
		String remark2 = (String) payment1001.get("Remark2");// ��ע2
		String taxtypenum = (String) payment1001.get("TaxTypeNum");// ˰������

		// ��֯DTO׼����������******************************************************
		// ʵʱ��˰��Ϣ TvTaxDto
		TvTaxDto tvtaxdto = new TvTaxDto();

		/*
		 * ʵʱҵ��ͷ��Ϣ msgMap-->RealHead1001
		 */
		tvtaxdto.setStaxorgcode(taxorgcode);// ���ջ��ش���
		tvtaxdto.setStrano(trano);// ������ˮ��
		tvtaxdto.setSentrustdate(entrustdate);// ί������
		/*
		 * ת����Ϣ msgMap-->TurnAccount1001
		 */
		tvtaxdto.setChandletype(handletype);// �������
		tvtaxdto.setSpayeebankno(payeebankno);// �տ����к�
		tvtaxdto.setSpayeeorgcode(payeeorgcode);// �տλ����
		tvtaxdto.setSpayeeacct(payeeacct);// �տ����˺�
		tvtaxdto.setSpayeename(payeename);// �տ�������
		tvtaxdto.setSpaybkcode(paybkcode);// �������к�
		tvtaxdto.setSpayopbkcode(payopbkcode);// ��������к�
		/*
		 * ������Ϣ��������Ϣ msgMap-->Payment1001
		 */
		tvtaxdto.setSpayacct(payacct);// �����˻�
		tvtaxdto.setShandorgname(handorgname);// �ɿλ����
		tvtaxdto.setFtraamt(MtoCodeTrans.transformBigDecimal(traamt));// ���׽��
		tvtaxdto.setStaxvouno(Yaxvouno);// ˰Ʊ����
		tvtaxdto.setSbilldate(billdate);// ��Ʊ����
		tvtaxdto.setStaxpaycode(taxpaycode);// ��˰�˱���
		tvtaxdto.setStaxpayname(taxpayname);// ��˰������
		tvtaxdto.setScorpcode(corpcode);// ��ҵ����
		tvtaxdto.setSprotocolno(protocolno);// Э�����
		tvtaxdto.setCbudgettype(budgettype);// Ԥ������
		tvtaxdto.setCtrimsign(trimsign);// �����ڱ�־
		tvtaxdto.setScorptype(corptype);// ��ҵע������
		tvtaxdto.setCprintflag(printvousign);// ��ӡ����ƾ֤��־
		tvtaxdto.setSremark(remark);// ��ע
		tvtaxdto.setSremark1(remark1);// ��ע1
		tvtaxdto.setSremark2(remark2);// ��ע2
		tvtaxdto.setItaxkindcount(Short.valueOf(taxtypenum));// ˰������

		String detailDealCode = StateConstant.DATA_FLAG_CHECK;// ����״̬ Ĭ�Ͻ��ճɹ�
		String cancelDealCode = StateConstant.CANCEL_FLAG_NOCHECK;// Ĭ��δ����
		String reckontype = StateConstant.RECKONTYPE_FLAG_ONE;// Ĭ��1��������

		tvtaxdto.setSmsgid(msgid);// ���ı�ʶ��
		// tvtaxdto.setSmqmsgid(msgid);//��Ϣ��ʶ��
		tvtaxdto.setSpayeeopbkcode(payeebankno);// �տ�����к�
		tvtaxdto.setCreckontype(reckontype);// ��������
		tvtaxdto.setSprocstat(detailDealCode);// ����״̬
		tvtaxdto.setSresult("00000");// ������
		tvtaxdto.setCcancelstat(cancelDealCode);// ����״̬
		tvtaxdto.setSacceptdate(TimeFacade.getCurrentStringTime());// ��������
		tvtaxdto.setTsupdate(new Timestamp(new java.util.Date().getTime()));// ����ʱ��
		tvtaxdto.setSstatus("00000");// ����״̬
		String bizseq;// ҵ����ˮ��
		try {
			bizseq = StampFacade.getBizSeq("SSKS");
			tvtaxdto.setSseq(bizseq);// ҵ����ˮ��
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȡҵ����ˮ��SEQ����");
		}

		// ����ʵʱ��˰��Ϣ���� ,������dto�ǿյ����
		if (null != tvtaxdto) {
			try {
				// ����ʵʱ��˰��Ϣ����
				DatabaseFacade.getDb().create(tvtaxdto);
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException("���ݿ����", e);
			}
		}

		List taxtypelist1001 = (List) payment1001.get("TaxTypeList1001");// ˰����ϸ
		/**
		 * ����˰���ѣ�Ʊ��Ϣ��˰���ѣ�����ϸ msgMap-->payment1001-->taxtypelist1001
		 */
		int taxtypecount = taxtypelist1001.size();
		for (int i = 0; i < taxtypecount; i++) {
			HashMap taxtypelist1001map = (HashMap) taxtypelist1001.get(i);

			String projectid = (String) taxtypelist1001map.get("ProjectId");// ��Ŀ���
			String budgetsubjectcode = (String) taxtypelist1001map
					.get("BudgetSubjectCode");// Ԥ���Ŀ����
			String limitdate = (String) taxtypelist1001map.get("LimitDate");// �޽�����
			String taxtypename = (String) taxtypelist1001map.get("TaxTypeName");// ˰������
//			String budgetlevelcode = (String) taxtypelist1001map
//					.get("BudgetLevelCode");// Ԥ�㼶�δ���
//			String budgetlevelname = (String) taxtypelist1001map
//					.get("BudgetLevelName");// Ԥ�㼶������
			String taxstartdate = (String) taxtypelist1001map
					.get("TaxStartDate");// ˰������������
			String taxenddate = (String) taxtypelist1001map.get("TaxEndDate");// ˰����������ֹ
			String vicesign = (String) taxtypelist1001map.get("ViceSign");// ������־
			String taxtype = (String) taxtypelist1001map.get("TaxType");// ˰������
			String taxtypeamt = (String) taxtypelist1001map.get("TaxTypeAmt");// ˰�ֽ��
			String detailnum = (String) taxtypelist1001map.get("DetailNum");// ��ϸ����
			List taxsubjectlist1001 = (List) taxtypelist1001map
					.get("TaxSubjectList1001");// ˰Ŀ��ϸ

			// ��֯DTO׼����������******************************************************
			// ʵʱ��˰˰����Ϣ dto
			TvTaxKindDto tvtaxkinddto = new TvTaxKindDto();

			String budgetlevel = MsgConstant.BUDGET_LEVEL_CENTER;// Ԥ�㼶�� 1 ����

			tvtaxkinddto.setSseq(bizseq);// ҵ����ˮ��
			tvtaxkinddto.setCbudgetlevel(budgetlevel);// Ԥ�㼶�� 1 ����
			tvtaxkinddto.setFsubjectamt(MtoCodeTrans
					.transformBigDecimal(taxtypeamt));// ��Ŀ���
			tvtaxkinddto.setIprojectid(Integer.valueOf(projectid));// ��Ŀ���
			// ˰����ţ���СС��100
			tvtaxkinddto.setIdetailno(Integer.valueOf(detailnum));// ��ϸ����
			tvtaxkinddto.setSbudgetsubjectcode(budgetsubjectcode);// Ԥ���Ŀ����
			// tvtaxkinddto.setSentrustdate(entrustdate);// ί������
			tvtaxkinddto.setSlimitdate(limitdate);// �޽�����
			tvtaxkinddto.setStaxenddate(taxenddate);// ˰����������ֹ
			// tvtaxkinddto.setStaxorgcode(taxorgcode);// ���ջ��ش���
			tvtaxkinddto.setStaxstartdate(taxstartdate);// ˰������������
			tvtaxkinddto.setStaxtypename(taxtypename);// ˰������
			// tvtaxkinddto.setStrano(trano);// ������ˮ��
			tvtaxkinddto.setSvicesign(vicesign);// ������־
			tvtaxkinddto.setCtaxtype(taxtype);// ˰������
			tvtaxkinddto.setTsupdate(new Timestamp(new java.util.Date()
					.getTime()));// ����ʱ��

			// ����ʵʱ��˰˰����Ϣ���� ,������List �ǿյ����
			if (null != tvtaxkinddto) {
				try {
					// ��������
					DatabaseFacade.getDb().create(tvtaxkinddto);
				} catch (JAFDatabaseException e) {
					logger.error(e);
					throw new ITFEBizException("���ݿ����", e);
				}
			}

			/**
			 * ����˰����Ϣ��˰Ŀ��ϸ
			 * msgMap-->payment1001-->taxtypelist1001-->taxsubjectlist1001
			 */
			int taxsubjectcount = taxsubjectlist1001.size();
			for (int j = 0; j < taxsubjectcount; j++) {
				HashMap taxsubjectlist1001map = (HashMap) taxsubjectlist1001
						.get(j);

				String detailno = (String) taxsubjectlist1001map
						.get("DetailNo");// ��ϸ���
				String taxsubjectcode = (String) taxsubjectlist1001map
						.get("TaxSubjectCode");// ˰Ŀ����
				String taxsubjectname = (String) taxsubjectlist1001map
						.get("TaxSubjectName");// ˰Ŀ����
				String taxnumber = (String) taxsubjectlist1001map
						.get("TaxNumber");// ��˰����
				String taxamt = (String) taxsubjectlist1001map.get("TaxAmt");// ��˰���
				String taxrate = (String) taxsubjectlist1001map.get("TaxRate");// ˰��
				String exptaxamt = (String) taxsubjectlist1001map
						.get("ExpTaxAmt");// Ӧ��˰��
				String discounttaxamt = (String) taxsubjectlist1001map
						.get("DiscountTaxAmt");// �۳���
				String facttaxamt = (String) taxsubjectlist1001map
						.get("FactTaxAmt");// ʵ��˰��

				// ��֯DTo׼����������******************************************************
				// ʵʱ��˰˰Ŀ��Ϣ dto
				TvTaxItemDto tvtaxitemdto = new TvTaxItemDto();

				tvtaxitemdto.setSseq(bizseq);// ҵ����ˮ��
				// tvtaxitemdto.setStrano(trano);// ������ˮ��
				tvtaxitemdto.setIprojectid(Integer.valueOf(projectid));// ��Ŀ���
				// tvtaxitemdto.setSentrustdate(entrustdate);// ί������
				// tvtaxitemdto.setStaxorgcode(taxorgcode);// ���ջ��ش���
				tvtaxitemdto.setIdetailno(Integer.valueOf(detailno));// ��ϸ���
				tvtaxitemdto.setStaxsubjectcode(taxsubjectcode);// ˰Ŀ����
				tvtaxitemdto.setStaxsubjectname(taxsubjectname);// ˰Ŀ����
				tvtaxitemdto.setItaxnumber(Integer.valueOf(taxnumber));// ��˰����
				tvtaxitemdto.setFtaxamt(MtoCodeTrans
						.transformBigDecimal(taxamt));// ��˰���
				tvtaxitemdto.setFtaxrate(MtoCodeTrans
						.transformBigDecimal(taxrate));// ˰��
				tvtaxitemdto.setFexptaxamt(MtoCodeTrans
						.transformBigDecimal(exptaxamt));// Ӧ��˰��
				tvtaxitemdto.setFdiscounttaxamt(MtoCodeTrans
						.transformBigDecimal(discounttaxamt));// �۳���
				tvtaxitemdto.setFrealamt(MtoCodeTrans
						.transformBigDecimal(facttaxamt));// ʵ��˰��
				tvtaxitemdto.setTsupdate(new Timestamp(new java.util.Date()
						.getTime()));// ����ʱ��

				// ����ʵʱ��˰˰Ŀ��Ϣ���� ,�������ǿյ����
				if (null != tvtaxitemdto) {
					try {
						// ��������
						DatabaseFacade.getDb().create(tvtaxitemdto);
					} catch (JAFDatabaseException e) {
						logger.error(e);
						throw new ITFEBizException("���ݿ����", e);
					}
				}
			}
		}
		/*
		 * ����/������־
		 */
		String recvseqno;// ������־��ˮ��
		String sendseqno;// ������־��ˮ��
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ
			// ���ݹ����ѯ��������
			TsTreasuryDto _dto = SrvCacheFacade.cacheTreasuryInfo(null).get(payeeorgcode);
			if (null != _dto) {
				sbookorgcode = _dto.getSorgcode();
			}
			
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȡ������־SEQ����");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("���ݹ������ȡ��������������!");
		}

		// �ǽ�����־
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode, entrustdate, (String) headMap.get("MsgNo"),
				(String) headMap.get("SRC"), (String) eventContext.getMessage()
						.getProperty("XML_MSG_FILE_PATH"), Integer
						.valueOf(taxtypenum), MtoCodeTrans
						.transformBigDecimal(traamt), trano, null, payeebankno,
				taxorgcode, null, (String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_RECV);

		// д������־
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, sbookorgcode, (String) headMap.get("DES"), entrustdate,
				(String) headMap.get("MsgNo"), (String) eventContext
						.getMessage().getProperty("XML_MSG_FILE_PATH"), Integer
						.valueOf(taxtypenum), MtoCodeTrans
						.transformBigDecimal(traamt), trano, payeeorgcode,
				payeebankno, taxorgcode, null, (String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND);
		
		// ��¼���ղ�������Ϣ��¼--����MQ��ϢIDƥ������޸�
		String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
		String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
		MsgLogFacade.writeMQMessageLog(sendorgcode, orgcode, msgNo, msgid, TimeFacade.getCurrentStringTime(), trano, jmsMessageID, jmsCorrelationID, taxorgcode);
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}

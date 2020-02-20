/**
 * �����ˮ��ϸ
 */
package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.HtvTaxorgIncomeDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvTaxorgIncomeDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * @author wangtuo
 * 
 */
public class Proc3126MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Proc3126MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		HashMap BillHead3126 = (HashMap) msgMap.get("BillHead3126");

		// ��������ͷ headMap
		String orgcode = (String) headMap.get("DES");// ���ջ�������
		String sendorgcode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��
		String msgref = (String) headMap.get("MsgRef"); // ����id��
		/**
		 * �����������ͷ��Ϣ msgMap-->BillHead3126
		 */
		String TaxOrgCode = (String) BillHead3126.get("TaxOrgCode"); // ���ջ��ش���
		String InTreDate = (String) BillHead3126.get("InTreDate"); // ���ƾ֤����
		String PackNo = (String) BillHead3126.get("PackNo"); // ����ˮ��
		String ChildPackNum = (String) BillHead3126.get("ChildPackNum"); // �Ӱ�����
		String CurPackNo = (String) BillHead3126.get("CurPackNo"); // �������
		String TaxAllNum = (String) BillHead3126.get("TaxAllNum"); // ����˰Ʊ�ܱ���
		String DrawBackAllNum = (String) BillHead3126.get("DrawBackAllNum"); // �����˿��ܱ���
		String CorrAllNum = (String) BillHead3126.get("CorrAllNum"); // ���������ܱ���
		String FreeAllNum = (String) BillHead3126.get("FreeAllNum"); // ������ֵ��ܱ���

		List Bill3126list = (List) msgMap.get("Bill3126");// �����ˮ��Ϣ
        //�Ȳ��������ݣ�ֱ��ת�������
		/*// �Ȳ���������
		TvTaxorgIncomeDto deldto = new TvTaxorgIncomeDto();
		deldto.setStaxorgcode(TaxOrgCode);
		deldto.setSintredate(InTreDate);
		// ��ɾ����Ӧ������
		HtvTaxorgIncomeDto hdeldto = new HtvTaxorgIncomeDto();
		hdeldto.setStaxorgcode(TaxOrgCode);
		hdeldto.setSintredate(InTreDate);
		try {
			CommonFacade.getODB().deleteRsByDto(deldto);
			CommonFacade.getODB().deleteRsByDto(hdeldto);
		} catch (ValidateException e1) {
			logger.error("ɾ�������ˮ��ϸʱ�������ݿ��쳣!", e1);
			throw new ITFEBizException("��ѯ�����ˮ��ϸʱ�������ݿ��쳣!", e1);
		} catch (JAFDatabaseException e1) {
			logger.error("ɾ��Ԥ�����ˮ��ϸʱ�������ݿ��쳣!", e1);
			throw new ITFEBizException("ɾ�������ˮ��ϸʱ�������ݿ��쳣!", e1);
		}
*/
		/**
		 * ���������ˮ��Ϣ msgMap-> Bill3126
		 
		if (null == Bill3126list || Bill3126list.size() == 0) {
			// û�������ˮ��ϸ����
		} else {

			int taxtypecount = Bill3126list.size();
			// �����LIST
			List<IDto> incomelist = new ArrayList<IDto>();
			for (int i = 0; i < taxtypecount; i++) {
				HashMap Bill3126 = (HashMap) Bill3126list.get(i);

				String TreCode = (String) Bill3126.get("TreCode"); // �������
				String ExportVouType = (String) Bill3126.get("ExportVouType"); // ����ƾ֤����
				String ExpTaxVouNo = (String) Bill3126.get("ExpTaxVouNo"); // ƾ֤����
				String BudgetType = (String) Bill3126.get("BudgetType"); // Ԥ������
				String BudgetLevelCode = (String) Bill3126
						.get("BudgetLevelCode"); // Ԥ�㼶��
				String BudgetSubjectCode = (String) Bill3126
						.get("BudgetSubjectCode"); // Ԥ���Ŀ
				BigDecimal TraAmt = MtoCodeTrans.transformBigDecimal(Bill3126
						.get("TraAmt")); // ���׽��
				String Origin = (String) Bill3126.get("Origin"); // ƾ֤��Դ

				// ��֯DTO׼����������******************************************************
				// �����ˮ��ϸ��ִ��Ϣ TvTaxorgIncomeDto
				TvTaxorgIncomeDto tvtaxorgincomedto = new TvTaxorgIncomeDto();
				tvtaxorgincomedto.setStaxorgcode(TaxOrgCode); // �������ش���
				tvtaxorgincomedto.setStrecode(TreCode); // �������
				tvtaxorgincomedto.setSintredate(InTreDate); // ���ƾ֤����
				tvtaxorgincomedto.setIpkgseqno(PackNo); // ����ˮ��
				tvtaxorgincomedto.setSexpvouno(ExpTaxVouNo); // ƾ֤���
				tvtaxorgincomedto.setSexpvoutype(ExportVouType); // ����ƾ֤����
				tvtaxorgincomedto.setCbdglevel(BudgetLevelCode); // Ԥ�㼶��
				tvtaxorgincomedto.setSbdgsbtcode(BudgetSubjectCode); // Ԥ���Ŀ����
				tvtaxorgincomedto.setCbdgkind(BudgetType); // Ԥ������
				tvtaxorgincomedto.setFamt(TraAmt); // ���
				tvtaxorgincomedto.setCvouchannel(Origin); // ƾ֤��Դ
				incomelist.add(tvtaxorgincomedto);
				if (((i + 1) % MsgConstant.TIPS_MAX_OF_PACK == 0 && i != 0)
						|| (i + 1) == taxtypecount) {
					try {
						DatabaseFacade.getDb().create(
								CommonUtil.listTArray(incomelist));
					} catch (JAFDatabaseException e) {
						logger.error("���������ˮ��ϸ��ִ����ʱ�������ݿ��쳣!", e);
						throw new ITFEBizException("���������ˮ��ϸ��ִ����ʱ�������ݿ��쳣!", e);
					}
					incomelist.clear();
				}
			}
		}
		*/
		/*
		 * ����/������־
		 */
		String recvseqno;// ������־��ˮ��
		String sendseqno;// ������־��ˮ��
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȡ����/������־SEQ����");
		}
		String path = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_FILE_NAME);
		// �ǽ�����־
		MsgLogFacade.writeRcvLog(recvseqno, recvseqno, orgcode, InTreDate, msgNo,
				sendorgcode, path, Integer
						.valueOf(TaxAllNum), new BigDecimal(0), PackNo, null,
				null, TaxOrgCode, null, msgid,
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(
						MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_RECV_TIPS+msgNo);

		/*// �ж��Ƿ���Ҫת�������������Ϊ2��ת�����������Ƿ�����־
		TvSendlogDto tvsendlog = null;
		List tvsendloglist = null;
		try {
			tvsendlog = new TvSendlogDto();
			tvsendlog.setSdate(InTreDate);
			tvsendlog.setSbillorg(TaxOrgCode);
			tvsendlog.setSoperationtypecode(MsgConstant.MSG_NO_1024);
//			tvsendlog.setSseq(msgref);
			tvsendloglist = CommonFacade.getODB().findRsByDto(tvsendlog);
		} catch (Exception e1) {
			logger.error("��ѯ��Ӧԭʼ����ʱ�������ݿ��쳣!", e1);
			throw new ITFEBizException("��ѯ��Ӧԭʼ����ʱ�������ݿ��쳣!", e1);
		}

		if (null == tvsendloglist || tvsendloglist.size() == 0) {
			eventContext.setStopFurtherProcessing(true);
			logger.error("�Ҳ�����Ӧ��1024ԭʼ���ͱ���!");
		} else if (tvsendloglist.size() >= 2) {
			try {
				tvsendlog = (TvSendlogDto) tvsendloglist.get(0);
				tvsendlog.setSretcode(DealCodeConstants.DEALCODE_TIPS_SUCCESS);
				tvsendlog
						.setSproctime(new Timestamp(new java.util.Date().getTime()));
				DatabaseFacade.getDb().update(tvsendlog);
			} catch (JAFDatabaseException e1) {
				logger.error("���·�����־����״̬�������ݿ��쳣!", e1);
				throw new ITFEBizException("���·�����־����״̬�������ݿ��쳣!", e1);
			}
			*/
//			if (StateConstant.MSG_SENDER_FLAG_2.equals(tvsendlog.getSifsend())) {
				// д������־
			

//				// ������һ������
				eventContext.setStopFurtherProcessing(true);
				return;
//			}
			
//		}
	}
}

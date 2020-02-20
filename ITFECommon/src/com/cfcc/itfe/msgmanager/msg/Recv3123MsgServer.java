package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 
 * ��Ҫ����:���ջ����˿�˶�֪ͨ����
 * @author wangyunbin
 * 
 */
public class Recv3123MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3123MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		/**
		 * �����������ͷ MSG->BillHead3123
		 */
		// ����ʵʱҵ��ͷ CFX->MSG->BatchReturn3136
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3123");
		String ChkDate = (String) batchheadMap.get("ChkDate"); // ��������
		String PackNo = (String) batchheadMap.get("PackNo"); // ����ˮ��
		String TaxOrgCode = (String) batchheadMap.get("TaxOrgCode"); // ���ջ��ش���
		int AllNum = Integer.parseInt(batchheadMap.get("AllNum").toString().trim()); // �ܱ���
		BigDecimal AllAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("AllAmt")); // �ܽ��
		int ChildPackNum = Integer.parseInt(batchheadMap.get("ChildPackNum").toString().trim()); // �Ӱ�����
		String CurPackNo = (String) batchheadMap.get("CurPackNo"); // �������
		int CurPackNum = Integer.parseInt(batchheadMap.get("CurPackNum").toString().trim()); // ��������
		BigDecimal CurPackAmt = MtoCodeTrans.transformBigDecimal(batchheadMap.get("CurPackAmt")); // �������

		/**
		 * ���������־
		 */
		try {
			TvRecvlogDto recvlogdto = new TvRecvlogDto();
			recvlogdto.setSrecvno(StampFacade.getStampSendSeq("JS")); // ��ˮ��
			recvlogdto.setSpackno(PackNo); // ����ˮ��
			recvlogdto.setSdate(ChkDate);// ��������-ί������
			recvlogdto.setSoperationtypecode(MsgConstant.MSG_NO_3123);// ҵ��ƾ֤����
			recvlogdto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));// ����ʱ��
			recvlogdto.setSretcode(""); // ������
			recvlogdto.setSretcodedesc(AllNum+"-"+AllAmt); // ����˵��- ����
			recvlogdto.setIcount(AllNum);
			recvlogdto.setNmoney(AllAmt);
			recvlogdto.setStitle((String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"));
			recvlogdto.setSsenddate(TimeFacade.getCurrentStringTime());
			recvlogdto.setSrecvorgcode((String) headMap.get("DES")); // ���ͻ�������Ĭ��
			recvlogdto.setSsendorgcode((String) headMap.get("SRC ")); // ���ջ�������Ĭ��
			recvlogdto.setSseq((String) headMap.get("MsgID"));
			DatabaseFacade.getDb().create(recvlogdto);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʧ��!", e);
			throw new ITFEBizException("ȡ������ˮ��ʧ��", e);
		} catch (JAFDatabaseException e) {
			logger.error("����������־�������ݿ��쳣!", e);
			throw new ITFEBizException("����������־�������ݿ��쳣!", e);
		}
		/**
		 * 
		3123��ʱ������״̬
		List returnList = (List) msgMap.get("CompRetTrea3123");
		if (null == returnList || returnList.size() == 0) {
			return;
		}
		
		SQLExecutor updateExce = null;
		try {
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updateSql = "update " + TvDwbkDto.tableName() + " set S_STATUS = ? "
			+ " where S_TAXORGCODE = ? and D_ACCEPT  = ? and S_PACKAGENO = ? and S_DEALNO = ? and F_AMT = ? "
			+ " and S_DWBKVOUCODE = ? ";
			int count = returnList.size();
			for (int i = 0; i < count; i++) {
				updateExce.clearParams();
				HashMap CompRetTrea3123Map = (HashMap) returnList.get(i);
				String OriSendOrgCode = (String) CompRetTrea3123Map.get("OriSendOrgCode"); // ԭ�����������
				String OriEntrustDate = (String) CompRetTrea3123Map.get("OriEntrustDate"); // ԭί������
				String OriPackNo = (String) CompRetTrea3123Map.get("OriPackNo"); // ԭ����ˮ��
				String OriTraNo = (String) CompRetTrea3123Map.get("OriTraNo"); // ԭ������ˮ��
				String DrawBackVouNo = (String) CompRetTrea3123Map.get("DrawBackVouNo"); // �˿�ƾ֤���
				BigDecimal DrawBackAmt = MtoCodeTrans.transformBigDecimal(CompRetTrea3123Map.get("DrawBackAmt"));// �˿���
				String OpStat = (String) CompRetTrea3123Map.get("OpStat"); // ����״̬
				String AddWord = (String) CompRetTrea3123Map.get("AddWord"); // ����

//				String sstatus = PublicSearchFacade.getDetailStateByDealCode(OpStat);

				updateExce.addParam(OpStat);
				updateExce.addParam(OriSendOrgCode);
				updateExce.addParam(CommonUtil.strToDate(OriEntrustDate).toString());
				updateExce.addParam(OriPackNo);
				updateExce.addParam(OriTraNo);
				updateExce.addParam(DrawBackAmt);
				updateExce.addParam(DrawBackVouNo);

				updateExce.runQuery(updateSql);
			}

			updateExce.closeConnection();
			
		} catch (JAFDatabaseException e) {
			String error = "�������ջ����˿�˶�֪ͨ�����ִ״̬ʱ�������ݿ��쳣��";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (null != updateExce) {
				updateExce.closeConnection();
			}
		}
		* */
		eventContext.setStopFurtherProcessing(true);
		return;
	}
}

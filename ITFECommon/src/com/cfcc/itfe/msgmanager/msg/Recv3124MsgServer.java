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
import com.cfcc.itfe.persistence.dto.TvInCorrhandbookDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

/**
 * 
 * ��Ҫ���ܣ����������ջ��ظ����˶�֪ͨ
 * @author wangyunbin
 * 
 */
public class Recv3124MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv3124MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		/**
		 * �����������ͷ MSG->BillHead3139
		 */
		// ����ʵʱҵ��ͷ CFX->MSG->BatchReturn3136
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3124");
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
			recvlogdto.setSpackno(PackNo); // ԭ����ˮ��
			recvlogdto.setSdate(ChkDate);// ��������-ί������
			recvlogdto.setSoperationtypecode(MsgConstant.MSG_NO_3124);// ҵ��ƾ֤����
			recvlogdto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));// ����ʱ��
			recvlogdto.setSretcode(""); // ������
			recvlogdto.setSretcodedesc(AllNum+"-"+AllAmt); // ����˵��- ����
			recvlogdto.setIcount(AllNum);
			recvlogdto.setNmoney(AllAmt);
			recvlogdto.setStitle((String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"));
			recvlogdto.setSsenddate(TimeFacade.getCurrentStringTime());
			recvlogdto.setSrecvorgcode((String) headMap.get("DES"));
			recvlogdto.setSsendorgcode((String) headMap.get("SRC"));
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
		
		List returnList = (List) msgMap.get("CompCorrTrea3124");
		if (null == returnList || returnList.size() == 0) {
			return;
		}
		
		SQLExecutor updateExce = null;
		try {
			updateExce = DatabaseFacade.getDb().getSqlExecutorFactory().getSQLExecutor();
			String updateSql = "update " + TvInCorrhandbookDto.tableName() + " set S_STATUS = ? "
			+ " where S_CURTAXORGCODE = ? and D_ACCEPT  = ? and S_PACKAGENO = ? and S_DEALNO = ? and F_CURCORRAMT = ? "
			+ " and S_CORRVOUNO = ? ";
			int count = returnList.size();
			for (int i = 0; i < count; i++) {
				updateExce.clearParams();
				HashMap CompRetTrea3124Map = (HashMap) returnList.get(i);
				String OriSendOrgCode = (String) CompRetTrea3124Map.get("OriSendOrgCode"); // ԭ�����������
				String OriEntrustDate = (String) CompRetTrea3124Map.get("OriEntrustDate"); // ԭί������
				String OriPackNo = (String) CompRetTrea3124Map.get("OriPackNo"); // ԭ����ˮ��
				String OriTraNo = (String) CompRetTrea3124Map.get("OriTraNo"); // ԭ������ˮ��
				String CorrVouNo = (String) CompRetTrea3124Map.get("CorrVouNo"); // ����ƾ֤���
				BigDecimal OriTraAmt = MtoCodeTrans.transformBigDecimal(CompRetTrea3124Map.get("OriTraAmt"));// ԭ���׽��
				BigDecimal CurTraAmt = MtoCodeTrans.transformBigDecimal(CompRetTrea3124Map.get("CurTraAmt"));// �ֽ��׽��
				String OpStat = (String) CompRetTrea3124Map.get("OpStat"); // ����״̬
				String AddWord = (String) CompRetTrea3124Map.get("AddWord"); // ����
				String sstatus = PublicSearchFacade.getDetailStateByDealCode(OpStat);
				updateExce.addParam(sstatus);
				updateExce.addParam(OriSendOrgCode);
				updateExce.addParam(CommonUtil.strToDate(OriEntrustDate).toString());
				updateExce.addParam(OriPackNo);
				updateExce.addParam(OriTraNo);
				updateExce.addParam(CurTraAmt);
				updateExce.addParam(CorrVouNo);

				updateExce.runQuery(updateSql);
			}

			updateExce.closeConnection();
			
		} catch (JAFDatabaseException e) {
			String error = "���¸��������ִ״̬ʱ�������ݿ��쳣��";
			logger.error(error, e);
			throw new ITFEBizException(error, e);
		} finally {
			if (null != updateExce) {
				updateExce.closeConnection();
			}
		}
		 * 
		 */
		eventContext.setStopFurtherProcessing(true);
		return;
	}
}

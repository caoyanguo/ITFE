package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.PublicSearchFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class Proc3122MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Proc3122MsgServer.class);

	/**
	 * Tips����������3122�����ջ��ء��籣˰���ѣ�Ʊ��ϸ�˶�֪ͨ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();
		HashMap cfxMap = (HashMap) muleMessage.getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// ��������
		HashMap batchheadMap = (HashMap) msgMap.get("BatchHead3122");
		String chkDate = (String) batchheadMap.get("ChkDate"); // �˶�����
		String packNo = (String) batchheadMap.get("PackNo"); // ����ˮ��
		String taxOrgCode = (String) batchheadMap.get("TaxOrgCode"); // ���ջ��ش�������籣�������
		String  msgno =(String) headMap.get("MsgNo");
		String sendorg=(String) headMap.get("SRC");
		String recvorg=(String) headMap.get("DES");
		String curPackNum = (String) batchheadMap.get("CurPackNum"); // ��������
		BigDecimal curPackAmt = MtoCodeTrans.transformBigDecimal(batchheadMap
				.get("CurPackAmt"));// �������

		List returnList = (List) msgMap.get("CompDeduct3122");
		String _srecvno = null;
		try {
			_srecvno = StampFacade.getStampSendSeq("JS");
		} catch (SequenceException e1) {
			logger.error("ȡ���ջ��߷�����ˮ�Ŵ���!", e1);
			throw new ITFEBizException("ȡ���ջ��߷�����ˮ�Ŵ���!", e1);
		}
		String path = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_FILE_NAME);
		// �ǽ�����־
		MsgLogFacade.writeRcvLog(_srecvno, _srecvno, recvorg, chkDate, msgno,
				sendorg, path, Integer
						.valueOf(curPackNum), curPackAmt, packNo, null,
				taxOrgCode, taxOrgCode, null, (String) headMap.get("MsgID"),
				DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER),
				null,MsgConstant.LOG_ADDWORD_RECV_TIPS+msgno);
		eventContext.setStopFurtherProcessing(true);
		return;
	}

}

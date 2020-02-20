package com.cfcc.itfe.msgmanager.msg;

import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

public class Recv9116MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9116MsgServer.class);

	/**
	 * 9116��������˶԰��ط����� ��������ϵͳδ�յ���������˶԰�3122ʱ������TIPS���·���
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {

		MuleMessage muleMessage = eventContext.getMessage();

		HashMap cfxMap = (HashMap) muleMessage.getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");
		HashMap getMsg9116Map = (HashMap) msgMap.get("GetMsg9116");

		// ���������е�������ϢCFX->MSG->GetMsg9116
		String endOrgCode = (String) getMsg9116Map.get("SendOrgCode"); // �����������
		String ntrustDate = (String) getMsg9116Map.get("EntrustDate"); // ί������
		String oriPackMsgNo = (String) getMsg9116Map.get("OriPackMsgNo");// ԭ�����ı��
		String oriChkDate = (String) getMsg9116Map.get("OriChkDate");// ԭ�˶�����
		String oriChkAcctType = (String) getMsg9116Map.get("OriChkAcctType");// ԭ�ļ���������
		String oriReportMonth = (String) getMsg9116Map.get("OriReportMonth");// ԭ������������
		String oirDocNameMonth = (String) getMsg9116Map.get("OirDocNameMonth");// ԭ�ļ�����������

		String ls_SendSeq = "";
		String ls_RecvSeq = "";
		// �ǽ�����־
		try {
			ls_SendSeq = StampFacade.getStampSendSeq("FS");
			ls_RecvSeq =  StampFacade.getStampSendSeq("JS");
			TvRecvlogDto recvlogdto = new TvRecvlogDto();
			recvlogdto.setSrecvno(ls_RecvSeq);// ������־��ˮ��
			recvlogdto.setSsendno(ls_SendSeq);// ��Ӧ������־��ˮ��
			recvlogdto.setSrecvorgcode((String) headMap.get("DES")); // ���ջ�������
			recvlogdto.setSdate(TimeFacade.getCurrentStringTime());// ��������
			recvlogdto.setSoperationtypecode((String) headMap.get("MsgNo"));// ҵ��ƾ֤����
			recvlogdto.setSsendorgcode((String) headMap.get("SRC"));// ���ͻ�������
			recvlogdto.setStitle((String) eventContext.getMessage()
					.getProperty("XML_MSG_FILE_PATH"));// ��������Ӧ�ļ���·��
			recvlogdto.setSrecvtime(new Timestamp(new java.util.Date()
					.getTime()));// ����ʱ��

			recvlogdto.setSretcode(""); // ������
			recvlogdto.setSretcodedesc(""); // ����˵��//
			recvlogdto.setSdemo("");// ��ϸ��Ϣ
			recvlogdto.setSstate("");// ����״̬
			recvlogdto.setSbatch(""); // ���κ�
			recvlogdto.setSbillorg(endOrgCode);
			recvlogdto.setSseq((String) headMap.get("MsgID"));
			recvlogdto.setSbatch(oriPackMsgNo);
			DatabaseFacade.getDb().create(recvlogdto);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʧ��!", e);
			throw new ITFEBizException("ȡ������ˮ��ʧ��", e);
		} catch (JAFDatabaseException e) {
			logger.error("����������־�������ݿ��쳣!", e);
			throw new ITFEBizException("����������־�������ݿ��쳣!", e);
		}

		// д������־
		try {

			TvSendlogDto sendlogdto = new TvSendlogDto();
			sendlogdto.setSsendno(ls_SendSeq);// ������־��ˮ��
			sendlogdto.setSsendorgcode((String) headMap.get("SRC"));// ���ͻ�������
			sendlogdto.setSdate(TimeFacade.getCurrentStringTime());// ��������
			sendlogdto.setSoperationtypecode((String) headMap.get("MsgNo"));// ҵ��ƾ֤����
			sendlogdto.setSrecvorgcode((String) headMap.get("DES")); // ���ջ������룬Ŀǰָ����ϵͳ
			sendlogdto.setStitle((String) eventContext.getMessage()
					.getProperty("XML_MSG_FILE_PATH"));// ��������Ӧ�ļ���·��
			sendlogdto.setSsendtime(new Timestamp(new java.util.Date()
					.getTime()));// ����ʱ��
			sendlogdto.setSretcode(""); // ������
			sendlogdto.setSdemo("");// ��ϸ��Ϣ
			sendlogdto.setSstate("");// ����״̬
			sendlogdto.setSseq((String) headMap.get("MsgID")); // ����id��
			sendlogdto.setSbillorg(endOrgCode);
			DatabaseFacade.getDb().create(sendlogdto);
		} catch (JAFDatabaseException e) {
			logger.error("����������־�������ݿ��쳣!", e);
			throw new ITFEBizException("����������־�������ݿ��쳣!", e);
		}

		// �޸�payload���Ա�transformerת�������к���ת��
		HashMap hm = new HashMap();
		hm.put("cfx", cfxMap);
		muleMessage.setPayload(hm);

	}

}

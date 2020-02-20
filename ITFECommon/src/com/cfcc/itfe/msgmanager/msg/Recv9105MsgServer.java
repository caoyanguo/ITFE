package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.JmsSendUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

public class Recv9105MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv9105MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap msgMap = (HashMap) cfxMap.get("MSG");
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			String msgRef = (String) headMap.get("MsgRef");
			String sendorgcode = (String) headMap.get("SRC");// ���ͻ�������
			HashMap FreeFormat9105 = (HashMap) msgMap.get("FreeFormat9105");
			String ls_SrcNodeCode = (String) FreeFormat9105.get("SrcNodeCode");
			String ls_DesNodeCode = (String) FreeFormat9105.get("DesNodeCode");
			String ls_SendOrgCode = (String) FreeFormat9105.get("SendOrgCode");
			String ls_RcvOrgCode = (String) FreeFormat9105.get("RcvOrgCode");
			String ls_Content = (String) FreeFormat9105.get("Content");

			String msgid = (String) headMap.get("MsgID");
			String sdate = (String) headMap.get("WorkDate");
			String sendno = null;// ������ˮ��
			String sbillorg = null;// ��Ʊ��λ
			String packno = null;// ԭ����
			String strecode = null;// �������
			String sdemo = "�յ�9105��ִ����";
			// ת��������
			// String state =
			// PublicSearchFacade.getPackageStateByDealCode(ls_OpStat);
			// ������־��ˮd
			String recvseqno = StampFacade.getStampSendSeq("JS");
			String sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ
			// ����ԭ������־

			String recvorg = (String) headMap.get("DES");
			String sendorg = (String) headMap.get("SRC");
			String biztype = (String) headMap.get("MsgNo");
			String filepath = (String) eventContext.getMessage().getProperty(
					"XML_MSG_FILE_PATH");
			String stamp = TimeFacade.getCurrentStringTime();
			String ifsend = (String) eventContext.getMessage().getProperty(
					MessagePropertyKeys.MSG_SENDER);

			// // ��¼������־
			// TvRecvlogDto dto = new TvRecvlogDto();
			// dto.setSrecvno(_srecvno); // ������ˮ��
			// dto.setSsendno(sendno); // ��Ӧ������־��ˮ��
			// dto.setSrecvorgcode(ls_SendOrgCode);// ���ջ�������
			// dto.setSdate(sdate); // �������� ������ͷ�е�ί������
			// dto.setSoperationtypecode(MsgConstant.MSG_NO_9105);// ���ı��
			// dto.setSsendorgcode(ls_RcvOrgCode);// ���ջ���
			// dto.setStitle((String) eventContext.getMessage().getProperty(
			// "XML_MSG_FILE_PATH"));// ��д���Ĵ��·��
			// dto.setSrecvtime(new Timestamp(new
			// java.util.Date().getTime()));// ���Ľ���ʱ��
			// dto.setIcount(0); // ����
			// dto.setNmoney(new BigDecimal(0));// ���
			// dto.setSpackno("");// ����ˮ��
			// dto.setStrecode("");// �������
			// dto.setSpayeebankno("");// �տ����к�
			// dto.setSbillorg(""); // ��Ʊ��λ
			// dto.setSpayoutvoutype(""); // ֧��ƾ֤����
			// dto.setSusercode("");
			// dto.setSseq((String) headMap.get("MsgID"));// MSGID����ID
			// dto.setSretcode(DealCodeConstants.DEALCODE_ITFE_RECEIVER);// ������
			// dto.setSsenddate(TimeFacade.getCurrentStringTime());// ��������
			// dto.setSretcodedesc(sdemo);// ������˵��
			// dto.setSproctime(new Timestamp(new
			// java.util.Date().getTime()));// ����ʱ��
			// dto.setSifsend("0");// �Ƿ�ת�������𷽡�ǰ�÷���Ϊ0��Tips����Ϊ1����������Ϊ2
			// dto.setSturnsendflag("");// ת����־
			// if (null != sdemo) {
			// sdemo = sdemo.getBytes().length >= 300 ? sdemo
			// .substring(0, 150) : sdemo;
			// }
			// dto.setSdemo(ls_Content);
			// dto.setSbatch("");
			// dto.setSsenddate("");
			// DatabaseFacade.getDb().create(dto);

			if (!"100000000000".equals(sendorgcode)) {	//���ղ������ʹ������պ�ֱ��ת��
				// ���˽�����־
				MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sendorgcode,
						sdate, (String) headMap.get("MsgNo"), (String) headMap
								.get("SRC"), (String) eventContext.getMessage()
								.getProperty("XML_MSG_FILE_PATH"), 1,
						BigDecimal.ZERO, packno, null, null, null, null,
						(String) headMap.get("MsgID"),
						DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
						(String) eventContext.getMessage().getProperty(
								MessagePropertyKeys.MSG_SENDER), null,
						MsgConstant.LOG_ADDWORD_RECV);
				
			
				// ��¼���ղ�������Ϣ��¼
				String jmsMessageID = (String) eventContext.getMessage().getProperty("JMSMessageID");
				String jmsCorrelationID = (String) eventContext.getMessage().getProperty("JMSCorrelationID");
				MsgLogFacade.writeMQMessageLog(sendorgcode, sendorgcode, (String) headMap.get("MsgNo"), msgid, sdate, 
						packno, jmsMessageID, jmsCorrelationID, null);
				
				// д������־
				MsgLogFacade.writeSendLog(sendseqno, recvseqno, sendorgcode, (String) headMap.get("DES"), sdate,
						(String) headMap.get("MsgNo"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), 1, BigDecimal.ZERO , packno, null,
						null, null, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
						(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND);
				
				// ���ԭ���ģ����·���
				Object msg = eventContext.getMessage().getProperty("MSG_INFO");
				eventContext.getMessage().setPayload(msg);			
			}else{
				// ����ԭ������־
				TvSendlogDto senddto = MsgRecvFacade.findSendLogByMsgId(msgRef,
						MsgConstant.MSG_NO_9105);
				if (null != senddto) {
					// ����ԭ������־��ˮ��
					MsgRecvFacade.updateMsgSendLogByMsgId(senddto,
							DealCodeConstants.DEALCODE_ITFE_RECEIVER,
							recvseqno, sdemo);
					sendno = senddto.getSsendno();
					sbillorg = senddto.getSbillorg();
					// packno = senddto.getSpackno();
					strecode = senddto.getStrecode();
					
					
					Object msg = eventContext.getMessage().getProperty("MSG_INFO");
					if(strecode!=null&&strecode.startsWith("1702"))
						JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINECITY, (String)msg, "524551000000000000000000000000000000000000000000", false,strecode);
					else
						JmsSendUtil.putJMSMessage(MsgConstant.QUEUE_ONLINE, (String)msg, "524551000000000000000000000000000000000000000000", false,strecode);
					eventContext.setStopFurtherProcessing(true);// ������һ������
				}
			}
		} catch (Exception e) {
			logger.error("����9105���Ĵ���ʧ��!", e);
			throw new ITFEBizException("����9105���Ĵ���ʧ��", e);
		}
//		eventContext.setStopFurtherProcessing(true);
		return;
	}

}

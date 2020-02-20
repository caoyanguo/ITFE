package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.BusinessFacade;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;


/**
 *�����ı���漰��2202��ҵ���а���֧���ޣ��У�ֽƾ֤�˿�����ת��tips��
 * 
 * @author zhouchuan
 * 
 */
public class Recv2202MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv2202MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
		HashMap headMap = (HashMap) cfxMap.get("HEAD");
		HashMap msgMap = (HashMap) cfxMap.get("MSG");

		// ��������ͷ headMap
			//(String) headMap.get("DES");// ���ջ�������
		String sendorgcode = (String) headMap.get("SRC");// ���ͻ�������
		String msgNo = (String) headMap.get("MsgNo");// ���ı��
		String msgid = (String) headMap.get("MsgID"); // ����id��
		String orgcode = sendorgcode;


		/**
		 * ��������ҵ��ͷ msgMap-->BatchHead2202
		 */
		HashMap batchhead2202 = (HashMap) msgMap.get("Head2202");
		// ��Ʊ��λ
		String finorgcode = (String) batchhead2202.get("AgentBnkCode");
		// �������
		String trecode = (String) batchhead2202.get("TreCode");
		// ί������
		String entrustdate = (String) batchhead2202.get("EntrustDate");
		// ����ˮ��
		String packno = (String) batchhead2202.get("PackNo");
		// �ܱ���
		Integer allnum = Integer.valueOf((String) batchhead2202.get("AllNum"));
		// �ܽ��
		BigDecimal allamt = MtoCodeTrans.transformBigDecimal(batchhead2202.get("AllAmt"));
		/*
		 * ����/������־
		 */
		String recvseqno;// ������־��ˮ��
		String sendseqno;// ������־��ˮ��
		TsConvertfinorgDto finorgdto = new TsConvertfinorgDto();//���ݹ������ȡ�����������
		finorgdto.setStrecode(trecode);
		
		try {
			recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ
			List<TsConvertfinorgDto> orglist = CommonFacade.getODB().findRsByDto(finorgdto);
			if(orglist!=null&&orglist.size()>0){
				orgcode = orglist.get(0).getSorgcode();
			}
		} catch (SequenceException e) {
			logger.error(e);
			throw new ITFEBizException("ȡ������־SEQ����");
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("���ݹ�������ѯ�����������");
		} catch (ValidateException e) {
			logger.error(e);
			throw new ITFEBizException("���ݹ�������ѯ�����������");
		}
		
		
		//�Ѿ�������ֽ���Ĵ����в�ת��
		String info=BusinessFacade.isRelayMsgToTips(orgcode, trecode, msgNo, finorgcode);
		if(!info.equals("")){
			// �ǽ�����־
			MsgLogFacade.writeRcvLog(recvseqno, sendseqno, orgcode, entrustdate, (String) headMap.get("MsgNo"),
					(String) headMap.get("SRC"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), allnum, allamt, packno, trecode, null,
					finorgcode, null, (String) headMap.get("MsgID"),DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
					(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, info);
			eventContext.setStopFurtherProcessing(true);
			return;
		}
		
		// �ǽ�����־
		MsgLogFacade.writeRcvLog(recvseqno, sendseqno, orgcode, entrustdate, msgNo,
				(String) headMap.get("SRC"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), allnum, allamt, packno, trecode, null,
				finorgcode, null, msgid,DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_RECV);

		// д������־
		MsgLogFacade.writeSendLog(sendseqno, recvseqno, orgcode, (String) headMap.get("DES"), entrustdate,
				msgNo, (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), allnum, allamt, packno, trecode,
				null, finorgcode, null, msgid,DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
				(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, MsgConstant.LOG_ADDWORD_SEND);
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}	


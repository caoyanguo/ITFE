package com.cfcc.itfe.msgmanager.msg;

import java.math.BigDecimal;
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
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.util.ValidateException;

/**
 * �����ı���漰��2102������˰��ִ��
 * 
 * @author zhouchuan
 * 
 */
public class Recv2102MsgServer extends AbstractMsgManagerServer {

	private static Log logger = LogFactory.getLog(Recv2102MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	@SuppressWarnings("unchecked")
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {
			HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
			HashMap headMap = (HashMap) cfxMap.get("HEAD");
			HashMap msgMap = (HashMap) cfxMap.get("MSG");
			// ��������ͷ headMap
//			String sorgcode = (String) headMap.get("SRC"); // �����������
			String sdescode = (String) headMap.get("DES");// ���սڵ����
			String MsgNo = (String) headMap.get("MsgNo");// ���ı��
			String MsgID = (String) headMap.get("MsgID");// ���ı�ʶ��
//			String MsgRef = (String) headMap.get("MsgRef");// ���Ĳο���
//			String WorkDate = (String) headMap.get("WorkDate");// ��������
			String sbookorgcode=null;
			String recvseqno = StampFacade.getStampSendSeq("JS"); // ȡ������־��ˮ
			String sendseqno = StampFacade.getStampSendSeq("FS"); // ȡ������־��ˮ
			/**
			 * ȡ�û�ִͷ��Ϣ
			 */
			HashMap batchHeadMap = (HashMap) msgMap.get("BatchHead2102");
//			String paybkcode = (String) batchHeadMap.get("PayBkCode"); // �������к�
			String entrustdate = (String) batchHeadMap.get("EntrustDate");// ί������
			String packno = (String) batchHeadMap.get("PackNo");// ����ˮ��
			String oritaxorgcode = (String) batchHeadMap.get("OriTaxOrgCode"); // ԭ���ջ��ش���
			String orientrustdate = (String) batchHeadMap.get("OriEntrustDate"); // ԭί������
			String oripackno = (String) batchHeadMap.get("OriPackNo"); // ԭ����ˮ��
			int allnum = Integer.parseInt(batchHeadMap.get("AllNum").toString()
					.trim());// �ܱ���
			String AllAmt = (String) batchHeadMap.get("AllAmt");// �ܽ��
//			String succnum = (String) batchHeadMap.get("SuccNum"); // �ɹ�����
//			String succamt = (String) batchHeadMap.get("SuccAmt");// �ɹ����
//			String result = (String) batchHeadMap.get("Result");//������
			String addword = (String) batchHeadMap.get("AddWord");//����
			BigDecimal allamt = new BigDecimal(AllAmt);
			TsConvertfinorgDto finorgdto = new TsConvertfinorgDto();//���ݲ�������ȡ�����������
			finorgdto.setSfinorgcode(oritaxorgcode);
			List<TsConvertfinorgDto> orglist = CommonFacade.getODB().findRsByDto(finorgdto);
			if(orglist!=null&&orglist.size()>0)
				sbookorgcode = orglist.get(0).getSorgcode();
			/**
			 * �ǽ�����־
			 *///<Object> batchReturnList = (List<Object>) msgMap.get("BatchReturn2102");
			MsgLogFacade.writeRcvLog(recvseqno, sendseqno, sbookorgcode,
					entrustdate, MsgNo, sdescode, (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"),allnum, allamt, packno, "", "", "",
					"", MsgID,DealCodeConstants.DEALCODE_ITFE_RECEIVER, null, null,(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null, null);
//			TsOperationmodelDto _dto = new TsOperationmodelDto();
//			String _smodelid = recvseqno.substring(0, 10);
//			int _ino = Integer.valueOf(recvseqno.substring(10, 20));
//			String path = (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH");
//			_dto.setSmodelid(_smodelid);
//			_dto.setIno(_ino);
//			_dto.setSoperationtypecode(MsgNo);
//			_dto.setSmodelsavepath(path);
//			DatabaseFacade.getDb().create(_dto);
			//���·�����־
			TvSendlogDto tvsendlog = null;
			List tvsendloglist = null;
			tvsendlog = new TvSendlogDto();
			tvsendlog.setSbillorg(oritaxorgcode);
			tvsendlog.setSdate(orientrustdate);
			tvsendlog.setSpackno(oripackno);
			tvsendloglist = CommonFacade.getODB().findRsByDto(tvsendlog);
		    if (null!=tvsendloglist && tvsendloglist.size() > 0) {
		    	tvsendlog = (TvSendlogDto) tvsendloglist.get(0);
				tvsendlog.setSretcode(DealCodeConstants.DEALCODE_TIPS_SUCCESS);
				tvsendlog.setSdemo(addword);
				tvsendlog.setSproctime(new Timestamp(new java.util.Date().getTime()));
				DatabaseFacade.getDb().update(tvsendlog);
				if (StateConstant.MSG_SENDER_FLAG_2.equals(tvsendlog.getSifsend())) {
					// д������־
					MsgLogFacade.writeSendLog(sendseqno, recvseqno,sbookorgcode, oritaxorgcode, orientrustdate,
							(String) headMap.get("MsgNo"), (String) eventContext.getMessage().getProperty("XML_MSG_FILE_PATH"), 0,
							new BigDecimal(0), null, null, null, oritaxorgcode, null,(String) headMap.get("MsgID"),
							DealCodeConstants.DEALCODE_ITFE_SEND, null, null,
							(String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER), null,MsgConstant.LOG_ADDWORD_SEND);
					// ���ԭ���ģ����·���
					//ȡ��ԭ�����ĵ�MQMSGID
//					TvMqmessageDto dto = MsgLogFacade.queryMQMSGID("1102", MsgRef);
//					if(dto==null){
						eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
//					}else{
//						eventContext.getMessage().setCorrelationId(dto.getSmqmsgid());
//					}
					Object msg = eventContext.getMessage().getProperty("MSG_INFO");
					eventContext.getMessage().setPayload(msg);
				} else {
					// ������һ������
					eventContext.setStopFurtherProcessing(true);
					return;
				}
			}
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʧ��!", e);
			throw new ITFEBizException("ȡ������ˮ��ʧ��!", e);
		} catch (JAFDatabaseException e) {
			logger.error("�ǽ��ձ�����־��ʧ��!", e);
			throw new ITFEBizException("��¼���ձ�����־��ʧ��!", e);
		} catch (ValidateException e) {
			logger.error("�Ƿ��ͱ�����־ʧ��!", e);
			throw new ITFEBizException("�Ƿ��ͱ�����־ʧ��!", e);
		}
	}
}	


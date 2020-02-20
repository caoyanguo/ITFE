package com.cfcc.itfe.msgmanager.msg;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.MsgRecvFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TfFundAppropriationDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.util.FileUtil;

public class ReSendMsgServer extends AbstractMsgManagerServer {
	private static Log logger = LogFactory.getLog(ReSendMsgServer.class);

	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		try {
			MuleMessage message = eventContext.getMessage();
			TvSendlogDto _dto = (TvSendlogDto) message.getPayload();
			File msgxmlFile = new File(_dto.getStitle());
			String msg = org.mule.util.FileUtils.readFileToString(msgxmlFile,
					"GB18030");

			msg = FileUtil.getInstance().removeCAOfXML(_dto.getStitle(), msg, "</CFX>");

			StringBuffer newMsg = new StringBuffer();
			// ����ǰ�ķ�����־���ϵ�
			_dto.setSretcode(DealCodeConstants.DEALCODE_ITFE_CANCELLATION);
			_dto.setSretcodedesc("�ط�ʱ����");
			_dto.setSproctime(new Timestamp(new java.util.Date().getTime()));
			DatabaseFacade.getDb().update(_dto);

			// ��������־��Ϊ�ѷ��ͣ�����ʱ����Ϊnull��������˵����Ϊnull������������ˮ�ţ��Ӷ��Ƿ�����־
			_dto.setSretcode(DealCodeConstants.DEALCODE_ITFE_SEND);// ������
			_dto.setSretcodedesc(null);
			_dto.setSproctime(null);
			_dto.setSdemo(null);
			_dto.setSsendno(StampFacade.getStampSendSeq("FS"));// ��ˮ��
			_dto.setSsendtime(new Timestamp(new java.util.Date().getTime()));// ����ʱ��
			
			if(ITFECommonConstant.PUBLICPARAM.indexOf("tbsmode")>=0){//tbs�ط�
				String msgid = MsgSeqFacade.getMsgSendSeq();
				if(_dto.getSoperationtypecode().equals(MsgConstant.MSG_NO_1000)){
					TfFundAppropriationDto fundDto = new TfFundAppropriationDto();
					fundDto.setSmsgid(_dto.getSseq());
					fundDto.setSentrustdate(_dto.getSdate());
					fundDto.setSmsgno(MsgConstant.MSG_NO_1000);
					List<TfFundAppropriationDto> fundList = CommonFacade.getODB().findRsByDto(fundDto);
					if(fundList==null || fundList.size()==0){
						logger.error("δ�ҵ�ԭ���͵��ʽ���Ϣ��msgid:" + _dto.getSseq());
						throw new ITFEBizException("δ�ҵ�ԭ���͵��ʽ���Ϣ��msgid:" + _dto.getSseq());
					}
					fundDto = fundList.get(0);
					fundDto.setSmsgid(msgid);
					fundDto.setSmsgref(msgid);
					DatabaseFacade.getDb().update(fundDto);
				}
				// ���±����ļ�����ͷ��Ϣ
				int msgidBegin = msg.indexOf("<MsgID>");
				int msgidEnd = msg.indexOf("</MsgID>") + 8;
				int msgrefEnd = msg.indexOf("</MsgRef>") + 9;
				newMsg.append(msg.substring(0, msgidBegin));
				newMsg.append("<MsgID>" + msgid + "</MsgID>");
				if(_dto.getSoperationtypecode().equals(MsgConstant.MSG_TBS_NO_3001)){
					newMsg.append(msg.substring(msgidEnd, msg.length()));
				}else{
					newMsg.append("<MsgRef>" + msgid + "</MsgRef>");
					newMsg.append(msg.substring(msgrefEnd, msg.length()));
				}
				_dto.setStitle("");
				_dto.setSseq(msgid);
				message.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO, DatabaseFacade.getDb().createWithResult(_dto));
//				message.setProperty(MessagePropertyKeys.MSG_NO_KEY, _dto.getSoperationtypecode());
				message.setPayload(newMsg.toString());
			}else{
				// ������η��͵ģ�����Ҫ�޸�Msgid
				if (MsgLogFacade.resendtofin(_dto.getSifsend(), _dto
						.getSoperationtypecode())) {
					newMsg.append(msg);
					DatabaseFacade.getDb().create(_dto);// ���淢����־
					// ��Tips���͵ģ���Ҫ�޸�Msgid
				} else {
					String msgid = MsgSeqFacade.getMsgSendSeq();
					String tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
							.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
									SequenceName.TRAID_SEQ_CACHE,
									SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
					// ���±����ļ�����ͷ��Ϣ
					int msgidBegin = msg.indexOf("<MsgID>");
					int msgrefEnd = msg.indexOf("</MsgRef>") + 9;
					newMsg.append(msg.substring(0, msgidBegin));
					newMsg.append("<MsgID>" + msgid + "</MsgID>");
					newMsg.append("<MsgRef>" + msgid + "</MsgRef>");
					int packNoBegin = msg.indexOf("<PackNo>");
					int packNoEnd = msg.indexOf("</PackNo>") + 9;
					//������ڰ���ˮ�ŵĻ���Ҫ�޸� ����ˮ��
					if (packNoBegin >0) {
						String substr =msg.substring(msgrefEnd, packNoBegin);
						newMsg.append(substr);
					    newMsg.append("<PackNo>" + tmpPackNo + "</PackNo>");
						newMsg.append(msg.substring(packNoEnd, msg.length()));
						//ͬʱ������ϸ�����еð���ˮ��
						HashMap<String, String> bizMsgNoMap = ITFECommonConstant.bizMsgNoList;
						// �����ҵ������Ҫ�����ļ�����Ķ�Ӧ��ϵ��ҵ�����ϸ��״̬
						if (bizMsgNoMap.containsKey(_dto
								.getSoperationtypecode())) {
							// �������������ļ�����Ķ�Ӧ��ϵ��
							MsgRecvFacade.updateFileRefPackNo( _dto.getSpackno(), tmpPackNo);
							// ����ҵ����еİ���ˮ��
								MsgRecvFacade.updateNewPackNoByOldPackNo(_dto
										.getSoperationtypecode(), _dto.getSpackno(), tmpPackNo, bizMsgNoMap);
							// ����ƾ֤�������еİ���ˮ��
							MsgRecvFacade.updateVoucherInfoByOldPackNo( _dto.getSpackno(), tmpPackNo);	
						}
					}else{
						newMsg.append(msg.substring(msgrefEnd, msg.length()));
					}
						
					_dto.setStitle("");
					_dto.setSseq(msgid);
					_dto.setSpackno(tmpPackNo);
					message.setProperty(MessagePropertyKeys.MSG_SEND_LOG_DTO,
							DatabaseFacade.getDb().createWithResult(_dto));
				}
				message.setProperty(MessagePropertyKeys.MSG_NO_KEY, _dto
						.getSoperationtypecode());
				//�ط��ж��¾ɽӿ�
				if(ITFECommonConstant.IFNEWINTERFACE.equals("1")&&("1104".equals( _dto.getSoperationtypecode()) || "1105".equals( _dto.getSoperationtypecode()))){
					message.setProperty(MessagePropertyKeys.MSG_NO_KEY, _dto
							.getSoperationtypecode()+"2");
				}
				
				message.setPayload(newMsg.toString());
			}
		} catch (Exception e) {
			logger.error("�����ط������쳣", e);
			throw new ITFEBizException("�����ط������쳣", e);
		}
	}
}

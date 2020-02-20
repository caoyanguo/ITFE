package com.cfcc.itfe.component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.config.MuleProperties;
import org.mule.api.lifecycle.Callable;
import org.mule.util.FileUtils;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.MsgLogFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.msgmanager.core.IMsgManagerServer;
import com.cfcc.itfe.persistence.dto.TdDownloadReportCheckDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvMqmessageDto;
import com.cfcc.itfe.persistence.dto.TvRecvlogDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.JmsSendUtil;
import com.cfcc.itfe.persistence.pk.TdDownloadReportCheckPK;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;

/**
 * 
 * ���ķ��ͽ���
 * 
 */
public class MessageComponent implements Callable {
	
	private static Log logger = LogFactory.getLog(MessageComponent.class);

	@SuppressWarnings("unchecked")
	public Object onCall(MuleEventContext eventContext) throws Exception {
		long start=System.currentTimeMillis();
		eventContext.getMessage().setStringProperty(MuleProperties.MULE_REPLY_TO_STOP_PROPERTY, "true");
		eventContext.getMessage().setReplyTo(null);		
		// ���ձ���ʱ�������õĲ������ת��
		eventContext.transformMessage();
		
		String msgno = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_NO_KEY);
		
		// �õ����Ĳο���
		String msgref = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_REF);
		// �õ����Ĳο���
		String msgid = (String) eventContext.getMessage().getProperty(
				MessagePropertyKeys.MSG_ID);
		//��ȡ������Ϣ
		String msg = (String) eventContext.getMessage().getProperty("MSG_INFO");
		//��ȡ�ڵ��
		String recvorg = (String) eventContext.getMessage().getProperty("DES");
		
		//�õ�������Ϣ
		Object obj = eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DTO);
		TvVoucherinfoDto vDto = null;
		if(obj!=null && obj instanceof TvVoucherinfoDto){
			vDto = (TvVoucherinfoDto)obj;
		}
		
		//���ñ��Ĵ�����flag
		boolean bflag = true;
		String msgsender = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_SENDER);
		if(msgid!=null&&msgsender!=null&&msgsender.equals(StateConstant.MSG_SENDER_FLAG_2))
		{
			TvRecvlogDto recvdto = new TvRecvlogDto();
			recvdto.setSseq(msgid);
			List recvlist = CommonFacade.getODB().findRsByDto(recvdto);
			if(recvlist!=null&&recvlist.size()>0)
			{
				logger.debug(msgid+"=============================�Ѿ�ת�����˱��ģ�");
				eventContext.setStopFurtherProcessing(true);// ������һ������
				return eventContext.getMessage().getPayload();
			}
		}
		if(ITFECommonConstant.PUBLICPARAM.contains(",fileswapcz,")||ITFECommonConstant.PUBLICPARAM.contains(",senddataswap,"))
		{
			try{
				savefile(msgno,msg,eventContext);
			}catch(Exception e)
			{}
		}
        logger.debug("==============����" + msgno + "���Ĵ�����=============="+eventContext.getEncoding());
		logger.debug("==============" + msgsender + "=============="+eventContext.getEncoding());
      if(msgsender != null && !msgsender.equals("2")){		
	       if(ITFECommonConstant.IFNEWINTERFACE.equals("1")&&("1104".equals(msgno) || "1105".equals(msgno)
	        		||"1104_OUT".equals(msgno) || "1105_OUT".equals(msgno)||"1106".equals(msgno) )){
				msgno = msgno.substring(0, 4)+"2";
				eventContext.getMessage().setStringProperty(MessagePropertyKeys.MSG_NO_KEY, msgno);
			}
       }
		
      if (ITFECommonConstant.msgNoConfig.containsKey(msgno)) {
			/**
			 * ˵����Ҫ���ô˷�����������conf/config/bizconfig/MsgManagerServer.xml�����úá�
			 */
      
			IMsgManagerServer msgServer = (IMsgManagerServer) ContextFactory
					.getApplicationContext().getBean(
							MsgConstant.SPRING_MSG_SERVER + msgno);
			if(ITFECommonConstant.PUBLICPARAM.contains(",tips=twofin,"))
			{
				if(msgno.equals("3129") || msgno.equals("3139") || msgno.equals("3128")){
					eventContext.getMessage().setPayload(msg);
					String Dispatch = "Dispatch" + "8888";
					eventContext.dispatchEvent(eventContext.getMessage(), Dispatch);
				}
			}
			//����ⷢ������ҵ��
			if(vDto!=null && ITFECommonConstant.TBS_TREANDBANK.containsKey(vDto.getStrecode()) 
					&& ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()).startsWith("102")){
				msgServer.dealMsg(eventContext);
//				eventContext.getMessage().setPayload(msg);
				String dispatch = "";
				if(vDto.getSext5() != null && vDto.getSext5().equals("1")){
					dispatch = "Redispatch" + "102";
				}else{
					dispatch = "Dispatch" + "102";
				}
				eventContext.dispatchEvent(eventContext.getMessage(),dispatch);
				eventContext.setStopFurtherProcessing(true);// ������һ������
				bflag = false;
				return eventContext.getMessage().getPayload();
			}
			//����ⷢ��ũ��ҵ��
			else if(vDto!=null && ITFECommonConstant.TBS_TREANDBANK.containsKey(vDto.getStrecode()) 
					&& ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()).startsWith("103")){
				msgServer.dealMsg(eventContext);
//				eventContext.getMessage().setPayload(msg);
				String dispatch = "";
				if(vDto.getSext5() != null && vDto.getSext5().equals("1")){
					dispatch = "Redispatch" + "103";
				}else{
					dispatch = "Dispatch" + "103";
				}
				eventContext.dispatchEvent(eventContext.getMessage(),dispatch);
				eventContext.setStopFurtherProcessing(true);// ������һ������
				bflag = false;
				return eventContext.getMessage().getPayload();
			}
			//����ⷢ���й�����ҵ��
			else if(vDto!=null && ITFECommonConstant.TBS_TREANDBANK.containsKey(vDto.getStrecode()) 
					&& ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()).startsWith("104")){
				msgServer.dealMsg(eventContext);
//				eventContext.getMessage().setPayload(msg);
				String dispatch = "";
				if(vDto.getSext5() != null && vDto.getSext5().equals("1")){
					dispatch = "Redispatch" + "104";
				}else{
					dispatch = "Dispatch" + "104";
				}
				eventContext.dispatchEvent(eventContext.getMessage(),dispatch);
				eventContext.setStopFurtherProcessing(true);// ������һ������
				bflag = false;
				return eventContext.getMessage().getPayload();
			}
			//����ⷢ������ҵ��
			else if(vDto!=null && ITFECommonConstant.TBS_TREANDBANK.containsKey(vDto.getStrecode()) 
					&& ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()).startsWith("105")){
				msgServer.dealMsg(eventContext);
//				eventContext.getMessage().setPayload(msg);
				String dispatch = "";
				if(vDto.getSext5() != null && vDto.getSext5().equals("1")){
					dispatch = "Redispatch" + "105";
				}else{
					dispatch = "Dispatch" + "105";
				}
				eventContext.dispatchEvent(eventContext.getMessage(),dispatch);
				eventContext.setStopFurtherProcessing(true);// ������һ������
				bflag = false;
				return eventContext.getMessage().getPayload();
			}
			//����ⷢ������ҵ��
			else if(vDto!=null && ITFECommonConstant.TBS_TREANDBANK.containsKey(vDto.getStrecode()) 
					&& ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()).startsWith("301")){
				msgServer.dealMsg(eventContext);
//				eventContext.getMessage().setPayload(msg);
				String dispatch = "";
				if(vDto.getSext5() != null && vDto.getSext5().equals("1")){
					dispatch = "Redispatch" + "301";
				}else{
					dispatch = "Dispatch" + "301";
				}
				eventContext.dispatchEvent(eventContext.getMessage(),dispatch);
				eventContext.setStopFurtherProcessing(true);// ������һ������
				bflag = false;
				return eventContext.getMessage().getPayload();
			}
			//����ⷢ����������ҵ��
			else if(vDto!=null && ITFECommonConstant.TBS_TREANDBANK.containsKey(vDto.getStrecode()) 
					&& ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode()).startsWith("313")){
				msgServer.dealMsg(eventContext);
//				eventContext.getMessage().setPayload(msg);
				String dispatch = "";
				if(vDto.getSext5() != null && vDto.getSext5().equals("1")){
					dispatch = "Redispatch" + "313";
				}else{
					dispatch = "Dispatch" + "313";
				}
				eventContext.dispatchEvent(eventContext.getMessage(),dispatch);
				eventContext.setStopFurtherProcessing(true);// ������һ������
				bflag = false;
				return eventContext.getMessage().getPayload();
			}
			//����ⷢ��ũ������ҵ��
			else if(vDto!=null && ITFECommonConstant.TBS_TREANDBANK.containsKey(vDto.getStrecode())){
				msgServer.dealMsg(eventContext);
//				eventContext.getMessage().setPayload(msg);
				String dispatch = "";
				if(vDto.getSext5() != null && vDto.getSext5().equals("1")){
					dispatch = "Redispatch" + ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode());
				}else{
					dispatch = "Dispatch" + ITFECommonConstant.TBS_TREANDBANK.get(vDto.getStrecode());
				}
				eventContext.dispatchEvent(eventContext.getMessage(),dispatch);
				eventContext.setStopFurtherProcessing(true);// ������һ������
				bflag = false;
				return eventContext.getMessage().getPayload();
			}
			
			//�����й���ǰ�÷ַ�
			if(ITFECommonConstant.PUBLICPARAM.indexOf(",systemMode=Dispatch,")>=0){
				//���빲����Ϣ
				if(msgno.equals("3129") || msgno.equals("3139") || msgno.equals("3128")){
					//�����й���ǰ�÷ַ�
					eventContext.getMessage().setPayload(msg);
					String Dispatch = "Dispatch" + "8888";
					eventContext.dispatchEvent(eventContext.getMessage(), Dispatch);
					//�����й���ǰ�÷ַ�
					eventContext.getMessage().setPayload(msg);
					Dispatch = "Dispatch" + "7777";
					eventContext.dispatchEvent(eventContext.getMessage(), Dispatch);
					//���ַ�˰�ַ�
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",dispatchMode=jilin,")>=0){
						eventContext.getMessage().setPayload(msg);
						Dispatch = "Dispatch" + "6666batch";
						eventContext.dispatchEvent(eventContext.getMessage(), Dispatch);
					}
				}
				//TIPS��ִ
				else if (msgno.equals("9120") || msgno.equals("9121")
						|| msgno.equals("9122") || msgno.equals("9110")) {
					// �����й���ǰ�÷ַ�
					if ("8".equals(msgref.substring(0, 1))) {
						eventContext.getMessage().setPayload(msg);
						String Dispatch = "Dispatch" + "8888";
						eventContext.dispatchEvent(eventContext.getMessage(),
								Dispatch);
						eventContext.setStopFurtherProcessing(true);// ������һ������
						bflag = false;
					}
					else if ("7".equals(msgref.substring(0, 1))) {
						eventContext.getMessage().setPayload(msg);
						String Dispatch = "Dispatch" + "7777";
						eventContext.dispatchEvent(eventContext.getMessage(),
								Dispatch);
						eventContext.setStopFurtherProcessing(true);// ������һ������
						bflag = false;
					}
					else if ("6".equals(msgref.substring(0, 1))) {
					}else{
						eventContext.getMessage().setPayload(msg);
						String Dispatch = "Dispatch" + "6666";
						eventContext.dispatchEvent(eventContext.getMessage(),
								Dispatch);
						eventContext.setStopFurtherProcessing(true);// ������һ������
						bflag = false;
					}
				}
				//ʵ���ʽ�ֱ��֧����ȡ���Ȩ֧����Ȼ�ִ
				else if(msgno.equals("3131") || msgno.equals("3133") || msgno.equals("3134")){
					TvSendlogDto messagedto = new TvSendlogDto();
					int pos = msg.indexOf("<BillOrg>");
					int endPos = msg.indexOf("</BillOrg>");
					String sbillorg = "";
					if(pos > 0){
						sbillorg = msg.substring(pos+9,endPos).replaceAll(" ", "");
					}
					messagedto.setSbillorg(sbillorg);
					
					pos = msg.indexOf("<OriPackNo>");
					endPos = msg.indexOf("</OriPackNo>");
					String spackno = "";
					if(pos > 0){
						spackno = msg.substring(pos+11,endPos).replaceAll(" ", "");
					}
					messagedto.setSpackno(spackno);
					try{
						List list = CommonFacade.getODB().findRsByDto(messagedto);
						if(list != null && list.size() > 0){
							TvSendlogDto dto = (TvSendlogDto)list.get(0);
							if("8".equals(dto.getSseq().substring(0,1))){
								eventContext.getMessage().setPayload(msg);
								String Dispatch = "Dispatch" + "8888";
								eventContext.dispatchEvent(eventContext.getMessage(),
										Dispatch);
								eventContext.setStopFurtherProcessing(true);// ������һ������
								bflag = false;
							}
							else if("7".equals(dto.getSseq().substring(0,1))){
								eventContext.getMessage().setPayload(msg);
								String Dispatch = "Dispatch" + "7777";
								eventContext.dispatchEvent(eventContext.getMessage(),
										Dispatch);
								eventContext.setStopFurtherProcessing(true);// ������һ������
								bflag = false;
							}
						}
					}catch (JAFDatabaseException e) {
						logger.error(e);
						throw new ITFEBizException("��ѯ���ݿ����", e);
					}
				}
				//���л������롢�˿����롢ʵ���ʽ��˿�����
				else if(msgno.equals("3143") || msgno.equals("3144")|| msgno.equals("3145")){
				int pos = msg.indexOf("<TreCode>");
				int endPos = msg.indexOf("</TreCode>");
				String strecode = "";
				if(pos > 0){
					strecode = msg.substring(pos+9,endPos).replaceAll(" ", "");
					logger.error("----------strecode=" + strecode + "---------");
					//ʡ����Ҫת��������������Ҫת��
					if(!"00".equals(strecode.substring(2, 4)) && !"26".equals(strecode.substring(0, 2))){
						eventContext.getMessage().setPayload(msg);
						String Dispatch = "Dispatch" + "8888";
						eventContext.dispatchEvent(eventContext.getMessage(),
								Dispatch);
						Dispatch = "Dispatch" + "7777";
						eventContext.dispatchEvent(eventContext.getMessage(),
								Dispatch);
						eventContext.setStopFurtherProcessing(true);// ������һ������
						bflag = false;
					}
				}
			//ʵʩ��˰��������˰��ִ	
			}else if(msgno.equals("2102") || msgno.equals("2001")){
				int pos = msg.indexOf("<OriTaxOrgCode>");
				int endPos = msg.indexOf("</OriTaxOrgCode>");
				logger.error("----------2102msgno=2001---------");
				String staxcode = "";
				if(pos > 0){
					staxcode = msg.substring(pos+15,endPos).replaceAll(" ", "");
				}
				try {
					TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null).get(staxcode);
					String sbookorgcode ="";
					if(_dto!=null)
						sbookorgcode = _dto.getSorgcode();
					else if("000002900001".equals(ITFECommonConstant.SRC_NODE))
						sbookorgcode = "260000000002";
					logger.error("----------2102msgno-sbookorgcode=---------"+sbookorgcode);
					//������Ҫ�ַ�
					if("26".equals(sbookorgcode.substring(0,2))){
						HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
						eventContext.getMessage().setPayload(msg);
						String Dispatch = "Dispatch" + "8888";
						eventContext.dispatchEvent(eventContext.getMessage(),
									Dispatch);
						eventContext.getMessage().setPayload(cfxMap);
					}
					//���ַ�˰�ַ�
					else if(ITFECommonConstant.PUBLICPARAM.indexOf(",dispatchMode=jilin,")>=0){
//						HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
//						eventContext.getMessage().setPayload(msg);
//						String Dispatch = "Dispatch" + "6666";
//						eventContext.dispatchEvent(eventContext.getMessage(),
//									Dispatch);
//						eventContext.getMessage().setPayload(cfxMap);
					}
				} catch (JAFDatabaseException e) {
					logger.error(e);
					throw new ITFEBizException("���ݲ�������ȡ�����������ʧ�ܣ�");
				}
					//�˸����ִ	
				}else if(msgno.equals("3190")){
					int pos = msg.indexOf("<OriTaxOrgCode>");
					int endPos = msg.indexOf("</OriTaxOrgCode>");
					String staxcode = "";
					if(pos > 0){
						staxcode = msg.substring(pos+15,endPos).replaceAll(" ", "");
					}
					try {
						TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null).get(staxcode);
						if (null!=_dto) {
							String sbookorgcode =_dto.getSorgcode();
							//������Ҫ�ַ�����ʡ������Ҫת��
							if("26".equals(sbookorgcode.substring(0,2)) || !"00".equals(sbookorgcode.substring(2,4))){
								eventContext.getMessage().setPayload(msg);
								String Dispatch = "Dispatch" + "8888";
								eventContext.dispatchEvent(eventContext.getMessage(),
										Dispatch);
								eventContext.setStopFurtherProcessing(true);// ������һ������
							}
							//���ַ�˰�ַ�
							else if(ITFECommonConstant.PUBLICPARAM.indexOf(",dispatchMode=jilin,")>=0){
//								HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
//								eventContext.getMessage().setPayload(msg);
//								String Dispatch = "Dispatch" + "6666";
//								eventContext.dispatchEvent(eventContext.getMessage(),
//											Dispatch);
//								eventContext.getMessage().setPayload(cfxMap);
							}
						}
					} catch (JAFDatabaseException e) {
						logger.error(e);
						throw new ITFEBizException("���ݲ�������ȡ�����������ʧ�ܣ�");
					}
				}
				//����Э���ִ
				else if(msgno.equals("9115")){
					int pos = msg.indexOf("<OriSendOrgCode>");
					int endPos = msg.indexOf("</OriSendOrgCode>");
					String sbillorg = "";
					if(pos > 0){
						sbillorg = msg.substring(pos + 16,endPos).replaceAll(" ", "");
					}
	
					try {
						TsConvertfinorgDto _dto = SrvCacheFacade.cacheFincInfoByFinc(null).get(sbillorg);
						if (null!=_dto) {
							String sbookorgcode =_dto.getSorgcode();
							//������Ҫ�ַ�
							if("26".equals(sbookorgcode.substring(0,2))){
								eventContext.getMessage().setPayload(msg);
								String Dispatch = "Dispatch" + "8888";
								eventContext.dispatchEvent(eventContext.getMessage(),
										Dispatch);
							}
							//���ַ�˰�ַ�
							else if(ITFECommonConstant.PUBLICPARAM.indexOf(",dispatchMode=jilin,")>=0){
//								HashMap cfxMap = (HashMap) eventContext.getMessage().getPayload();
//								eventContext.getMessage().setPayload(msg);
//								String Dispatch = "Dispatch" + "6666";
//								eventContext.dispatchEvent(eventContext.getMessage(),
//											Dispatch);
//								eventContext.getMessage().setPayload(cfxMap);
							}
						}
					} catch (JAFDatabaseException e) {
						logger.error(e);
						throw new ITFEBizException("���ݲ�������ȡ�����������ʧ�ܣ�");
					}
				}
			}else if(ITFECommonConstant.PUBLICPARAM.indexOf(",systemMode=BosiDispatch,")>=0){
				String que6666 = "ITFE.6666.ONLINE.OUT";
				//���빲����Ϣ
				if(msgno.equals("3129") || msgno.equals("3139") || msgno.equals("3128")){
					//����˼ǰ�÷ַ�
					eventContext.getMessage().setPayload(msg);
					String Dispatch = "Dispatch" + "6666Batch";
					eventContext.dispatchEvent(eventContext.getMessage(), Dispatch);
					//�����˰�ַ�
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",dispatchMode=shanxi,")>=0){
						eventContext.getMessage().setPayload(msg);
						Dispatch = "Dispatch" + "8888";
						eventContext.dispatchEvent(eventContext.getMessage(), Dispatch);
					}
				}
				//TIPS��ִ
				else if (msgno.equals("9120") || msgno.equals("9121")
						|| msgno.equals("9122") || msgno.equals("9110")) {
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",dispatchMode=shanxi,")>=0){
						// ������ǰ�÷ַ�
						if ("8".equals(msgref.substring(0, 1))) {
							String mqmsgid = null;
							eventContext.getMessage().setPayload(msg);
							String Dispatch = "Dispatch" + "8888";
							eventContext.dispatchEvent(eventContext.getMessage(),
									Dispatch);
							eventContext.setStopFurtherProcessing(true);// ������һ������
							bflag = false;
						}
					}
					if("6".equals(msgref.substring(0, 1))){
						int pos = msg.indexOf("<OriMsgNo>");
						int endPos = msg.indexOf("</OriMsgNo>");
						String orimsgno = "";
						if(pos > 0){
							orimsgno = msg.substring(pos+10,endPos).replaceAll(" ", "");
						}

						//ȡ��ԭ�����ĵ�MQMSGID
						TvMqmessageDto dto = MsgLogFacade.queryMQMSGID(orimsgno, msgref);
						String mqmsgid = null;
						if(dto==null){
							mqmsgid = "ID:524551000000000000000000000000000000000000000000";
						}else{
							mqmsgid = dto.getSmqmsgid();
						}
						eventContext.getMessage().setCorrelationId(mqmsgid);
						
						if(!"1102".equals(orimsgno) &&!"5001".equals(orimsgno)&&!"5002".equals(orimsgno)&&!"5003".equals(orimsgno)){
							JmsSendUtil.putJMSMessage(que6666, (String)msg, mqmsgid, false,recvorg);
							eventContext.setStopFurtherProcessing(true);// ������һ������
						}else
						{
							eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");//����������Ϣͷд����id
							eventContext.getMessage().setPayload(msg);
							String Dispatch = "Dispatch" + "6666";
							eventContext.dispatchEvent(eventContext.getMessage(),
									Dispatch);
							eventContext.setStopFurtherProcessing(true);// ������һ������
							bflag = false;
						}
					}
				}else if( msgno.equals("2001")){//ʵʩ��˰��ִ	
					//ȡ��ԭ�����ĵ�MQMSGID
					TvMqmessageDto dto = MsgLogFacade.queryMQMSGID("1001", msgref);
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",dispatchMode=shanxi,")>=0){
						if("8".equals(msgref.substring(0, 1))){
							eventContext.getMessage().setPayload(msg);
							String Dispatch = "Dispatch" + "8888";
							eventContext.dispatchEvent(eventContext.getMessage(),
									Dispatch);
							eventContext.setStopFurtherProcessing(true);// ������һ������
							bflag = false;
						}
					}
					if("6".equals(msgref.substring(0, 1))){
						eventContext.getMessage().setPayload(msg);
						String correlationId = "ID:524551000000000000000000000000000000000000000000";
						if(dto==null){
							eventContext.getMessage().setCorrelationId(correlationId);
						}else{
							correlationId = dto.getSmqmsgid();
							eventContext.getMessage().setCorrelationId(correlationId);
						}
						JmsSendUtil.putJMSMessage(que6666, (String)msg, correlationId, false,recvorg);
						eventContext.setStopFurtherProcessing(true);// ������һ������
						bflag = false;
					}
				}else if( msgno.equals("2102")){//������˰��ִ	
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",dispatchMode=shanxi,")>=0){
						if("8".equals(msgref.substring(0, 1))){
							eventContext.getMessage().setPayload(msg);
							String Dispatch = "Dispatch" + "8888";
							eventContext.dispatchEvent(eventContext.getMessage(),
									Dispatch);
							eventContext.setStopFurtherProcessing(true);// ������һ������
							bflag = false;
						}
					}
					if("6".equals(msgref.substring(0, 1))){
						String correlationId = "ID:524551000000000000000000000000000000000000000000";
						eventContext.getMessage().setCorrelationId(correlationId);//����������Ϣͷд����id
						eventContext.getMessage().setPayload(msg);
						String Dispatch = "Dispatch" + "6666";
						eventContext.dispatchEvent(eventContext.getMessage(),
								Dispatch);
						eventContext.setStopFurtherProcessing(true);// ������һ������
						bflag = false;
					}
				}else if( msgno.equals("9115")){//����Э���ִ	
					//ȡ��ԭ�����ĵ�MQMSGID
					TvMqmessageDto dto = MsgLogFacade.queryMQMSGID("9114", msgref);
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",dispatchMode=shanxi,")>=0){
						if("8".equals(msgref.substring(0, 1))){
							String Dispatch = "Dispatch" + "8888";
							eventContext.dispatchEvent(eventContext.getMessage(),
									Dispatch);
							eventContext.setStopFurtherProcessing(true);// ������һ������
							bflag = false;
						}
					}
					if("6".equals(msgref.substring(0, 1))){
						String correlationId = "ID:524551000000000000000000000000000000000000000000";
						if(dto==null){
							eventContext.getMessage().setCorrelationId(correlationId);
						}else{
							correlationId = dto.getSmqmsgid();
							eventContext.getMessage().setCorrelationId(correlationId);
						}
						JmsSendUtil.putJMSMessage(que6666, (String)msg, correlationId, false,recvorg);
						eventContext.setStopFurtherProcessing(true);// ������һ������
						bflag = false;
					}
				}
			}
			//����ת���ı���
			if(bflag){
				msgServer.dealMsg(eventContext);
			}
			logger.info("XXXXXXXXXXXXXXXXXXXX="+(System.currentTimeMillis()-start)/1000);
		} else {
			eventContext.getMessage().setPayload(msg);
			String Dispatch = "Dispatch" + "9999";
			eventContext.dispatchEvent(eventContext.getMessage(), Dispatch);
			logger.debug("û���ҵ�ƥ��ı��ı��[" + msgno + "]������");
			throw new ITFEBizException("û���ҵ�ƥ��ı��ı��[" + msgno + "]������");
		}
      return eventContext.getMessage().getPayload();
	}
	private void savefile(String msgno,String msg,MuleEventContext eventContext) throws Exception
	{
		if(msgno.contains("3128")||msgno.contains("3129")||msgno.contains("3139"))
		{
			int pos = msg.indexOf("<FinOrgCode>");
			int endPos = msg.indexOf("</FinOrgCode>");
			String finorgcode = null;
			String reportDate = null;
			if(pos > 0){
				finorgcode = msg.substring(pos+12,endPos).replaceAll(" ", "");
				eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_BILL_CODE,finorgcode);
			}
			pos = msg.indexOf("<TreCode>");
			endPos = msg.indexOf("</TreCode>");
			String strecode = "";
			if(pos > 0){
				strecode = msg.substring(pos+9,endPos).replaceAll(" ", "");
			}
			if(msgno.contains("3128"))
			{
				pos = msg.indexOf("<ReportDate>");
				endPos = msg.indexOf("</ReportDate>");
				if(pos > 0){
					reportDate = msg.substring(pos+12,endPos).replaceAll(" ", "");
					eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_DATE,reportDate);
				}
			}else if(msgno.contains("3129"))
			{
				pos = msg.indexOf("<ApplyDate>");
				endPos = msg.indexOf("</ApplyDate>");
				if(pos > 0){
					reportDate = msg.substring(pos+11,endPos).replaceAll(" ", "");
					eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_DATE,reportDate);
				}
			}else if(msgno.contains("3139"))
			{
				pos = msg.indexOf("<InTreDate>");
				endPos = msg.indexOf("</InTreDate>");
				if(pos > 0){
					reportDate = msg.substring(pos+11,endPos).replaceAll(" ", "");
					eventContext.getMessage().setProperty(MessagePropertyKeys.MSG_DATE,reportDate);
				}
			}
			String filepath = null;
			if(isWin())
				filepath = "D:/itfeext/czftp/"+TimeFacade.getCurrentStringTime()+"/";
			else
				filepath = "/itfeext/czftp/"+TimeFacade.getCurrentStringTime()+"/";
			String filename = msgno+"_"+TimeFacade.getCurrentStringTime()+"_"+finorgcode+"_"+reportDate+"_"+UUID.randomUUID().toString().replace("-","")+".msg";
			File file = new File(filepath+filename);
			if(!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			FileUtils.writeStringToFile(file, msg.substring(0,msg.indexOf("<!--")), "GBK");
			try{
				checkdownload(reportDate,strecode,msgno,filepath,filename);
			}catch(Exception e)
			{
				logger.error(e);
			}
		}
	}
	private void checkdownload(String reportDate,String strecode,String msgno,String filepath,String filename) throws Exception
	{
		if(ITFECommonConstant.PUBLICPARAM.contains(",senddataswap,"))
		{
			TdDownloadReportCheckPK finpk = new TdDownloadReportCheckPK();
			finpk.setSdates(reportDate);
			finpk.setStrecode(strecode);
			TdDownloadReportCheckDto findto = (TdDownloadReportCheckDto)DatabaseFacade.getDb().find(finpk);
			boolean insert = false;
			if(findto==null)
			{
				insert = true;
				findto = new TdDownloadReportCheckDto();
			}
			if(msgno.contains("3128"))
			{
				findto.setSissendrb("1");//0δ�յ�1���յ�2��ת��
				findto.setSrbfilename(filepath+filename);
				findto.setSkucun("1");
				findto.setSribao("1");
			}else if(msgno.contains("3129"))
			{
				findto.setSissendsp("1");//0δ�յ�1���յ�2��ת��
				findto.setSspfilename(filepath+filename);
				findto.setSshuipiao("1");
			}else if(msgno.contains("3139"))
			{
				findto.setSissendls("1");//0δ�յ�1���յ�2��ת��
				findto.setSlsfilename(filepath+filename);
				findto.setSliushui("1");
			}
			if(insert)
				DatabaseFacade.getDb().create(findto);
			else
				DatabaseFacade.getDb().update(findto);
		}
	}
	private boolean isWin()
	 {
			String osName = System.getProperty("os.name");
			if (osName.indexOf("Windows") >= 0) {
				return true;
			} else {
				return false;
			}
	}
}

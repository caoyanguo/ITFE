package com.cfcc.itfe.service.subsysmanage.testconnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.MsgSeqFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HeadDto;
import com.cfcc.itfe.persistence.dto.TvSendlogDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
/**
 * @author zhouchuan
 * @time   09-11-09 17:53:57
 * codecomment: 
 */

public class TestConnectionService extends AbstractTestConnectionService {
	
	private static Log logger = LogFactory.getLog(TestConnectionService.class);	


	/**
	 * ���Ͳ��Ա���	 
	 * @generated
	 * @param srcvMsgNode
	 * @return java.lang.String
	 * @throws ITFEBizException	 
	 */
    public String sendTestMsg(String srcvMsgNode) throws ITFEBizException {
    	
		HeadDto headdto = new HeadDto();
		headdto.set_VER(MsgConstant.MSG_HEAD_VER);
		if(getLoginInfo().getSorgcode()!=null&&getLoginInfo().getSorgcode().startsWith("1702"))
			headdto.set_SRC(ITFECommonConstant.SRCCITY_NODE);
		else
			headdto.set_SRC(ITFECommonConstant.SRC_NODE);
		headdto.set_DES(srcvMsgNode);
		headdto.set_APP(MsgConstant.MSG_HEAD_APP);
		headdto.set_msgNo(MsgConstant.MSG_NO_9005);
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headdto.set_msgID(msgid);
			headdto.set_msgRef(msgid);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}
		headdto.set_workDate(TimeFacade.getCurrentStringTime());

		try {
			MuleClient client = new MuleClient();

			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
					MsgConstant.MSG_NO_9005+"_OUT");
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE,getLoginInfo().getSorgcode());
			message.setPayload(map);
			if(getLoginInfo().getSorgcode()!=null&&getLoginInfo().getSorgcode().startsWith("1702"))
				message = client.send("vm://ManagerMsgToPbcCity", message);
			else
				message = client.send("vm://ManagerMsgToPbc", message);
		} catch (MuleException e) {
			logger.error("���ú�̨���Ĵ����ʱ������쳣!", e);
			throw new ITFEBizException("���ú�̨���Ĵ����ʱ������쳣!", e);
		}
		
		return null;
	}

	/**
	 * ��ѯ���Խ��	 
	 * @generated
	 * @return java.util.List
	 * @throws ITFEBizException	 
	 */
    public List searchTestResult(String sMsgNo, String sMsgDate) throws ITFEBizException {
		/**
		 * ���ݲ�ѯ������װ��ѯ���
		 */
		String where = " where S_DATE = '" + sMsgDate + "' and S_OPERATIONTYPECODE = '" + sMsgNo + "' ";

		try {
			return DatabaseFacade.getDb().find(TvSendlogDto.class, where);
		} catch (JAFDatabaseException e) {
			logger.error("��ѯ���Խ��ʱ�����쳣��", e);
			throw new ITFEBizException("��ѯ���Խ��ʱ�����쳣��", e);
		}
	}

	public void testMsg() throws ITFEBizException {
		HeadDto headdto = new HeadDto();
		headdto.set_VER(MsgConstant.MSG_HEAD_VER);
		headdto.set_SRC("111111111111");
		HashMap<String, String> hm= ITFECommonConstant.TBS_TREANDBANK;
		if(hm.containsKey(getLoginInfo().getSorgcode().substring(0, 10))){
			headdto.set_DES(hm.get(getLoginInfo().getSorgcode().substring(0, 10))+"000000000");
		}else{
			logger.error("=====�����Բ��Զ�Ӧ��������δ��sysconfig.xml��TBS_TREANDBANK������=====");
			return;
		}
		headdto.set_APP("TCQS");
		headdto.set_msgNo(MsgConstant.MSG_TBS_NO_3000);
		try {
			String msgid = MsgSeqFacade.getMsgSendSeq();
			headdto.set_msgID(msgid);
			headdto.set_msgRef(msgid);
		} catch (SequenceException e) {
			logger.error("ȡ������ˮ��ʱ�����쳣��", e);
			throw new ITFEBizException("ȡ������ˮ��ʱ�����쳣��", e);
		}
		headdto.set_workDate(TimeFacade.getCurrentStringTime());
		headdto.set_reserve("����");

		try {
			TvVoucherinfoDto vDto = new TvVoucherinfoDto();
			vDto.setStrecode(getLoginInfo().getSorgcode().substring(0, 10));
			MuleClient client = new MuleClient();

			Map map = new HashMap();
			MuleMessage message = new DefaultMuleMessage(map);
			message.setProperty(MessagePropertyKeys.MSG_NO_KEY,
					MsgConstant.MSG_TBS_NO_3000+"_OUT");
			message.setProperty(MessagePropertyKeys.MSG_HEAD_DTO, headdto);
			message.setProperty(MessagePropertyKeys.MSG_ORGCODE,getLoginInfo().getSorgcode());
			message.setProperty(MessagePropertyKeys.MSG_DTO, vDto);
			message.setPayload(map);

			message = client.send("vm://ManagerMsgWithCommBank", message);
		} catch (MuleException e) {
			logger.error("���ú�̨���Ĵ����ʱ������쳣!", e);
			throw new ITFEBizException("���ú�̨���Ĵ����ʱ������쳣!", e);
		}
		
	}

}
package com.cfcc.itfe.msgmanager.msg;

import org.mule.api.MuleEventContext;

import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;

public class Send1105MsgServer extends AbstractMsgManagerServer {

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		String filename = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_FILE_NAME); // �ļ����� (����Ϊҵ������)
		String orgcode = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_ORGCODE); // ��������
		String packno = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_PACK_NO); // ����ˮ�� 
		String commitdate = (String) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_DATE); // ί������
		boolean isRepeat =  (Boolean) eventContext.getMessage().getProperty(MessagePropertyKeys.MSG_REPEAT); // �Ƿ��ط�����
		
	}
}

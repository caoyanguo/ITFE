package com.cfcc.itfe.msgmanager.msg;


import org.mule.api.MuleEventContext;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.msgmanager.core.AbstractMsgManagerServer;


/**
 *�����ı���漰��9101״̬���֪ͨ��
 * 
 * ֻת�������������κδ���
 * 
 */
public class Recv9101MsgServer extends AbstractMsgManagerServer {

//	private static Log logger = LogFactory.getLog(Recv9101MsgServer.class);

	/**
	 * ������Ϣ����
	 */
	public void dealMsg(MuleEventContext eventContext) throws ITFEBizException {
		// ����Tips����ҵ��--����MQ��Ϣ�����ʼ��
		eventContext.getMessage().setCorrelationId("ID:524551000000000000000000000000000000000000000000");
		// ���ԭ���ģ����·���
		Object msg = eventContext.getMessage().getProperty("MSG_INFO");
		eventContext.getMessage().setPayload(msg);
	}
}	


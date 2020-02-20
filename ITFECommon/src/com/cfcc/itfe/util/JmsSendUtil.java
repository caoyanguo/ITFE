package com.cfcc.itfe.util;


import java.io.UnsupportedEncodingException;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.cfcc.deptone.common.core.exception.MessageException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQXAConnectionFactory;

public class JmsSendUtil {

	/**
	 * ����һ�������ģ���һ��������������
	 * 
	 * @param outputQ
	 *            ������
	 * @param obj
	 *            Ҫ���͵Ķ���
	 * @param msgid
	 *            ԭ��Ϣmsgid
	 * @param transacted
	 *            �Ƿ��������
	 * @throws MessageException
	 */
	public static void putJMSMessage(String outputQ,
			String obj, String msgid, boolean transacted,String strecode) throws MessageException {
		MQXAConnectionFactory connectionFactory = null;
		Connection connection = null;
		Session session = null;
		MessageProducer messageProducer = null;
		try {
			// ������Ӳ���MQ�Ĺ�����
			if(strecode!=null&&(strecode.contains("000002700009")||strecode.startsWith("0702")))
				connectionFactory = (MQXAConnectionFactory) ContextFactory.getApplicationContext().getBean("JMSConnectFactory4");
			else
				connectionFactory = (MQXAConnectionFactory) ContextFactory.getApplicationContext().getBean("JMSConnectFactory2");
			// ��������
			connection = connectionFactory.createConnection();
			// ����һ��session����
			session = connection.createSession(transacted,
					Session.AUTO_ACKNOWLEDGE);
			// ����MQ���ж���
			MQQueue queue =  (MQQueue)session.createQueue(outputQ);
			//���ö��е�targetClient����--Force MQSTR format,��RFHͷ(MQHRF2)
			queue.setTargetClient(com.ibm.mq.jms.JMSC.MQJMS_CLIENT_NONJMS_MQ); 
			// ���ö���ccsid
			queue.setCCSID(819);
			messageProducer = session.createProducer(queue);
			// ������ϢΪ�ǳ־�
			messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			BytesMessage bytesMessage = session.createBytesMessage();
			//������Ϣ��correctionIdΪԭ��Ϣ��msgid
			bytesMessage.setJMSCorrelationID(msgid);
			bytesMessage.writeBytes(obj.getBytes("GBK"));
			// ������Ϣ����ʱ��Ϊ300000ms
			messageProducer.setTimeToLive(300000);
			// ������Ϣ
			messageProducer.send(bytesMessage);
		} catch (JMSException e1) {
			String errorMsg = "������Ϣʧ��:������" + e1.getErrorCode() + ",����ԭ��:"
					+ e1.getMessage();
			throw new MessageException(errorMsg, e1);
		} catch (RuntimeException e) {
			String errorMsg = "������Ϣʧ��:" + e.getMessage();
			throw new MessageException(errorMsg, e);
		} catch (UnsupportedEncodingException e) {
			String errorMsg = "�ַ���ת��Ϊ�ֽ�ʧ��:" + e.getMessage();
			throw new MessageException(errorMsg, e);
		} finally {
			if (messageProducer != null) {
				try {
					messageProducer.close();
				} catch (JMSException e2) {
					String errorMsg = "�ر�messageProducer�쳣";
					throw new MessageException(errorMsg, e2);
				}
			}
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e2) {
					String errorMsg = "�ر�session�쳣";
					throw new MessageException(errorMsg, e2);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e2) {
					String errorMsg = "�ر�connection�쳣";
					throw new MessageException(errorMsg, e2);
				}
			}
		}
	}
}

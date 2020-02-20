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
	 * 发送一条对象报文，将一个对象放入队列中
	 * 
	 * @param outputQ
	 *            队列名
	 * @param obj
	 *            要发送的对象
	 * @param msgid
	 *            原消息msgid
	 * @param transacted
	 *            是否参与事务
	 * @throws MessageException
	 */
	public static void putJMSMessage(String outputQ,
			String obj, String msgid, boolean transacted,String strecode) throws MessageException {
		MQXAConnectionFactory connectionFactory = null;
		Connection connection = null;
		Session session = null;
		MessageProducer messageProducer = null;
		try {
			// 获得连接财政MQ的工厂类
			if(strecode!=null&&(strecode.contains("000002700009")||strecode.startsWith("0702")))
				connectionFactory = (MQXAConnectionFactory) ContextFactory.getApplicationContext().getBean("JMSConnectFactory4");
			else
				connectionFactory = (MQXAConnectionFactory) ContextFactory.getApplicationContext().getBean("JMSConnectFactory2");
			// 创建连接
			connection = connectionFactory.createConnection();
			// 创建一个session对象
			session = connection.createSession(transacted,
					Session.AUTO_ACKNOWLEDGE);
			// 创建MQ队列对象
			MQQueue queue =  (MQQueue)session.createQueue(outputQ);
			//设置队列的targetClient属性--Force MQSTR format,无RFH头(MQHRF2)
			queue.setTargetClient(com.ibm.mq.jms.JMSC.MQJMS_CLIENT_NONJMS_MQ); 
			// 设置队列ccsid
			queue.setCCSID(819);
			messageProducer = session.createProducer(queue);
			// 设置消息为非持久
			messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			BytesMessage bytesMessage = session.createBytesMessage();
			//设置消息的correctionId为原消息的msgid
			bytesMessage.setJMSCorrelationID(msgid);
			bytesMessage.writeBytes(obj.getBytes("GBK"));
			// 设置消息生存时间为300000ms
			messageProducer.setTimeToLive(300000);
			// 发送消息
			messageProducer.send(bytesMessage);
		} catch (JMSException e1) {
			String errorMsg = "发送消息失败:错误码" + e1.getErrorCode() + ",错误原因:"
					+ e1.getMessage();
			throw new MessageException(errorMsg, e1);
		} catch (RuntimeException e) {
			String errorMsg = "发送消息失败:" + e.getMessage();
			throw new MessageException(errorMsg, e);
		} catch (UnsupportedEncodingException e) {
			String errorMsg = "字符串转换为字节失败:" + e.getMessage();
			throw new MessageException(errorMsg, e);
		} finally {
			if (messageProducer != null) {
				try {
					messageProducer.close();
				} catch (JMSException e2) {
					String errorMsg = "关闭messageProducer异常";
					throw new MessageException(errorMsg, e2);
				}
			}
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e2) {
					String errorMsg = "关闭session异常";
					throw new MessageException(errorMsg, e2);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e2) {
					String errorMsg = "关闭connection异常";
					throw new MessageException(errorMsg, e2);
				}
			}
		}
	}
}

package com.cfcc.test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.util.FileUtil;
import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class MQSendUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("------------------Start");
			
			List <String> list = FileUtil.getInstance().listFileAbspath("E:/20120510");
			int i =0;
            for (int j = 0; j < list.size(); j++) {
            	
            	String fileName = list.get(j);
            	if (fileName.contains("3129_") ||fileName.contains("31391_") ||fileName.contains("31281_")) {
            		i++;
            		String content=readFile2(fileName);
        			asynSend(content.getBytes(), "PBC.201410000010.BATCH.OUT");
        			if (i>29) {
						break;
					}
				}
    			
				
			}
			

		} catch (Exception e) {
			
			e.printStackTrace();
		}

	}

	/**
	 * MQ�첽���ͱ���
	 * 
	 * @param res
	 *            ������Ϣ
	 * @param queueName
	 *            ��������
	 */
	public static void asynSend(byte[] res, String queueName) {
		MQQueue queue = null;
		MQQueueManager qMgr = null;
		try {
			int port = 5050;// ���й������������˿ں�
			String hostName = "127.0.0.1";// ���й�����������ַ
			String channel = "SVRCONN";// ����������ͨ��
			String qManager = "QM_TIPS_201410000010_01";// ���й���������

			int waitInterval = 10000;

			/* ����MQEnvironment �����Ա�ͻ������� */
			MQEnvironment.hostname = hostName;
			MQEnvironment.channel = channel;
			MQEnvironment.CCSID = 819;// CCSID
			MQEnvironment.port = port;
			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,
					MQC.TRANSPORT_MQSERIES);

			/* ���ӵ����й����� */
			qMgr = new MQQueueManager(qManager);

			/* ���ô�ѡ���Ա����������Ķ��У�������й�����ֹͣ������Ҳ��������ѡ��ȥӦ�Բ��ɹ���� */
			int openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;

			/* �򿪴򿪶��� */
			queue = qMgr.accessQueue(queueName, openOptions, null, null, null);

			/* ���÷�����Ϣѡ����ǽ�ʹ��Ĭ������ */
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			pmo.options = pmo.options + MQC.MQPMO_NEW_MSG_ID;
			pmo.options = pmo.options + MQC.MQPMO_SYNCPOINT;
			MQMessage outMsg = new MQMessage();
			/* ����MQMD ��ʽ�ֶ� */
			outMsg.characterSet = 819;

			// outMsg.format = MQC.MQFMT_STRING;
			// outMsg.messageFlags = MQC.MQMT_REQUEST;
			outMsg.write(res);
			/* �ڶ����Ϸ�����Ϣ */
			queue.put(outMsg, pmo);
			/* ������Ϣ������ */

			qMgr.commit();

			queue.close();
			qMgr.disconnect();
			qMgr.close();

		} catch (MQException e) {
			// ex.completionCode
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (queue != null)
					queue.close();
			} catch (MQException e) {
				e.printStackTrace();
			}
			try {
				if (qMgr != null) {
					qMgr.close();
				}
			} catch (MQException e) {
				e.printStackTrace();
			}
			try {
				if (qMgr != null) {
					qMgr.disconnect();
				}
			} catch (MQException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ���ı��ļ��ж����ַ���
	 * 
	 * @param fileName
	 *            �ļ���
	 * @return �ļ��е�����
	 * @throws IOException 
	 */
	public static String readFile2(String fileName) throws FileOperateException, IOException {
		long lBegin = 0;
	
		FileInputStream input = null;
		String message = null;
		
			input = new FileInputStream(fileName);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];

			do {
				int size = input.read(buffer);
				if (size == -1)
					break;
				byteArray.write(buffer, 0, size);
			} while (true);
			byte[] data = byteArray.toByteArray();
			message = new String(data, "GBK");
		
		return message;

	}

	/**
	 * ���ļ�����
	 * 
	 * @param fileName
	 *            �ļ�����
	 * @return
	 * @throws Exception
	 */
	public static byte[] readFile(String fileName) throws Exception {
		byte[] res = null;
		FileInputStream input = null;
		try {
			input = new FileInputStream(fileName);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			byte[] buffer = new byte[10240];

			do {
				int size = input.read(buffer);
				if (size == -1)
					break;
				byteArray.write(buffer, 0, size);
			} while (true);
			res = byteArray.toByteArray();

		} catch (Exception e) {
			String msg = new String("��ȡ�ļ�ʧ��,δ�ҵ��ļ�." + fileName);
			e.printStackTrace();
			throw new Exception(msg, e);

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();

				}
			}
		}

		return res;

	}

}

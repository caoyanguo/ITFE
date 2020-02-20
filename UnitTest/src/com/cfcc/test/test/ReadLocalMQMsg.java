package com.cfcc.test.test;

import java.io.IOException;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class ReadLocalMQMsg {
	
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}

	protected String qManager = "QM_TIPS_202057100007_01"; // define name of queue manager

	protected String qManagerHost = "localhost";

	protected String queuName = "PBC.202057100007.BATCH.OUT"; // define name of queue 测试接收
//	protected String queuName = "PBC.ITFE.BATCH.IN"; // define name of queue 测试发送
	
	
	protected MQQueue mqQueue;

	protected MQQueueManager qMgr;

	public static void main(String[] argv) throws MQException, FileOperateException {
//		String filepath = "D:\\国库前置资料文件\\杭州\\测试数据\\recviver\\3128.xml";
		for(int i = 0 ;i <1 ; i++){
			ReadLocalMQMsg mqMsg = new ReadLocalMQMsg();
			mqMsg.initMq();
			mqMsg.openQueue();
//			String file = FileUtil.getInstance().readFile(filepath);
//			mqMsg.putMessageToQueue(file);
			
//			String msg = mqMsg.getMessageFromQueue();
//			if(null == msg){
//				System.out.println("队列为空");
//				return ;
//			}
//			FileUtil.getInstance().writeFile("c:\\mq\\123424.xml", msg);
//			System.out.println(msg);
//			String file = FileUtil.getInstance().readFile("c:\\3201.xml");
//			mqMsg.putMessageToQueue(file);
			mqMsg.putMessageToQueue("<?xml version=\"1.0\" encoding=\"GBK\"?><CFX><HEAD><VER>1.0</VER><SRC>100000000000</SRC><DES>202100000010</DES><APP>TIPS</APP><MsgNo>9121</MsgNo><MsgID>20090916912100010005</MsgID><MsgRef>20100226000000005767</MsgRef><WorkDate>20090916</WorkDate></HEAD><MSG><MsgReturn9121><OriMsgNo>7211</OriMsgNo><OriSendOrgCode>21101030000</OriSendOrgCode><OriEntrustDate>20100201</OriEntrustDate><OriRequestNo>03075000</OriRequestNo><Result>94052</Result><AddWord>asf</AddWord></MsgReturn9121></MSG></CFX>");
			
			
//			
			//mqMsg.putMessageToQueue("<?xRCtest</OriSendOrgCode><OriEntrustDate>test</OriEntrustDate><OriPackNo>test</OriPackNo><AllNum>1</AllNum><AllAmt>100.00</AllAmt><Result>1</Result><AddWord></AddWord></BatchHead9110><BatchReturn9110><OriTraNo>test</OriTraNo><TraAmt>100.00</TraAmt><Result>1</Result><AddWord></AddWord></BatchReturn9110></MSG></CFX>");

			mqMsg.closeQueue();
			System.out.println("fffffffff");
//			System.out.println("发送报文[" + filepath + "]成功!");
		}
		
	}

	// 初始化队列
	public void initMq() {
		try {
			MQEnvironment.channel = "SVRCONN";
			MQEnvironment.hostname = qManagerHost;
			MQEnvironment.port = 9009;
			qMgr = new MQQueueManager(qManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 打开队列
	public void openQueue() throws MQException {
		// Set up the options on the queue we wish to open...
		// Note. All WebSphere MQ Options are prefixed with MQC in Java.
		int openOptions = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_OUTPUT;
		// Now specify the queue that we wish to open,
		// and the open options...

		try {
			mqQueue = qMgr.accessQueue(queuName, openOptions);
		} catch (MQException mqe) {
			// check if MQ reason code 2045
			// means that opened queu is remote and it can not be opened as
			// input queue
			// try to open as output only
			if (mqe.reasonCode == 2045) {
				openOptions = MQC.MQOO_OUTPUT;
				mqQueue = qMgr.accessQueue(queuName, openOptions);
			}
		}
	}

	public void putMessageToQueue(String msg) throws MQException {
		try {
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			MQMessage mqMsg = new MQMessage();
			mqMsg.replyToQueueName="PBC.EXT.BATCH.IN";
			mqMsg.replyToQueueManagerName="QM1";
			
			mqMsg.write(msg.getBytes());
			// put the message on the queue
			mqQueue.put(mqMsg, pmo);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// 从队列中取得消息
	public String getMessageFromQueue() throws MQException {
		try {
			MQMessage mqMsg = new MQMessage();
			MQGetMessageOptions gmo = new MQGetMessageOptions();

			// Get a message from the queue
			mqQueue.get(mqMsg, gmo);

			// Extract the message data
			int len = mqMsg.getDataLength();
			byte[] message = new byte[len];
			mqMsg.readFully(message, 0, len);
			return new String(message);
		} catch (MQException mqe) {
			int reason = mqe.reasonCode;

			if (reason == 2033)// no messages
			{
				return null;
			} else {
				throw mqe;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void closeQueue() throws MQException {
		// Close the queue...
		mqQueue.close();
		// Disconnect from the queue manager
		qMgr.disconnect();

	}

}

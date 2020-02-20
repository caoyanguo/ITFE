package com.cfcc.test.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;

import org.apache.axis.utils.ByteArray;
import org.apache.axis.utils.JavaUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.util.FileUtil;

public class SocketServer {
	private static Log log = LogFactory.getLog(SocketServer.class);
	public static final int PORT = 12345;// �����Ķ˿ں�

	public static void main(String[] args) {
		System.out.println("����������...\n");
		SocketServer server = new SocketServer();
		server.init();
	}

	public void init() {
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			while (true) {
				// һ���ж���, ���ʾ��������ͻ��˻��������
				Socket client = serverSocket.accept();
				// �����������
				new HandlerThread(client);
			}
		} catch (Exception e) {
			System.out.println("�������쳣: " + e.getMessage());
		}
	}

	private class HandlerThread implements Runnable {
		private Socket socket;

		public HandlerThread(Socket client) {
			socket = client;
			new Thread(this).start();
		}

		public void run() {
			try {
				// ��ȡ�ͻ�������s
			System.out.println("��ʼ��������client����:����������������������������������" );  
			InputStream in = socket.getInputStream();
			
            String msg = read(in);
            System.out.println(msg);
            String filename ="c:/"+UUID.randomUUID().toString().replace("-","")+".TXT";
            writeFile(filename, msg);
            in.close();
				// // ��ͻ��˻ظ���Ϣ
				// DataOutputStream out = new
				// DataOutputStream(socket.getOutputStream());
				// System.out.print("������:\t");
				// // ���ͼ��������һ��
				// String s = new BufferedReader(new
				// InputStreamReader(System.in)).readLine();
				// out.writeUTF(s);
				//                  
				// out.close();
				
			} catch (Exception e) {
				System.out.println("������ run �쳣: " + e.getMessage());
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (Exception e) {
						socket = null;
						System.out.println("����� finally �쳣:" + e.getMessage());
					}
				}
			}
		}
	}

	public String read(InputStream is) throws IOException {

		DataInputStream dis = new DataInputStream(is);
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];
		do {
			int size = dis.read(buffer);
			if (size == -1)
				break;
			byteArray.write(buffer, 0, size);
		} while (true);
		dis.close();
	  
		byte[] data = byteArray.toByteArray();
		String message = new String(data, "GBK");
		return message;
	}

	public void writeFile(String fileName, String fileContent)
			throws FileOperateException {
		
		File file = new File(fileName);
		File dir = new File(file.getParent());

		FileOutputStream output = null;
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			output = new FileOutputStream(fileName, false);
			output.write(fileContent.getBytes("GBK"));

		} catch (IOException e) {
			String msg = new String("д�ļ�ʧ��,IO����." + fileName);
	
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("д�ļ�ʧ�ܣ�����ʱ�쳣." + fileName);

			throw new FileOperateException(msg, e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					log.error("�ر��ļ�����", ex);

				}
			}
		}

	}

}

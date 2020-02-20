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
	public static final int PORT = 12345;// 监听的端口号

	public static void main(String[] args) {
		System.out.println("服务器启动...\n");
		SocketServer server = new SocketServer();
		server.init();
	}

	public void init() {
		try {
			ServerSocket serverSocket = new ServerSocket(PORT);
			while (true) {
				// 一旦有堵塞, 则表示服务器与客户端获得了连接
				Socket client = serverSocket.accept();
				// 处理这次连接
				new HandlerThread(client);
			}
		} catch (Exception e) {
			System.out.println("服务器异常: " + e.getMessage());
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
				// 读取客户端数据s
			System.out.println("开始接收数据client数据:。。。。。。。。。。。。。。。。。" );  
			InputStream in = socket.getInputStream();
			
            String msg = read(in);
            System.out.println(msg);
            String filename ="c:/"+UUID.randomUUID().toString().replace("-","")+".TXT";
            writeFile(filename, msg);
            in.close();
				// // 向客户端回复信息
				// DataOutputStream out = new
				// DataOutputStream(socket.getOutputStream());
				// System.out.print("请输入:\t");
				// // 发送键盘输入的一行
				// String s = new BufferedReader(new
				// InputStreamReader(System.in)).readLine();
				// out.writeUTF(s);
				//                  
				// out.close();
				
			} catch (Exception e) {
				System.out.println("服务器 run 异常: " + e.getMessage());
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (Exception e) {
						socket = null;
						System.out.println("服务端 finally 异常:" + e.getMessage());
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
			String msg = new String("写文件失败,IO错误." + fileName);
	
			throw new FileOperateException(msg, e);
		} catch (RuntimeException e) {
			String msg = new String("写文件失败，运行时异常." + fileName);

			throw new FileOperateException(msg, e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (Exception ex) {
					log.error("关闭文件出错！", ex);

				}
			}
		}

	}

}

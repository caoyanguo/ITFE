package com.cfcc.test.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class TCPClent {
    public static void main(String[] args) throws UnknownHostException,
	    IOException {
	Socket socket = new Socket("localhost", 5000);
//	OutputStream outputStream = socket.getOutputStream();
//	outputStream.write(("connect to the server at time " + new Date()
//		.toString()).getBytes());
//	outputStream.flush();
//	System.out.println(socket);
	InputStream is = socket.getInputStream();
	byte[] bytes = new byte[1024];
	int n = is.read(bytes);
	System.out.println(new String(bytes, 0, n));
	is.close();
	socket.close();
    }
}

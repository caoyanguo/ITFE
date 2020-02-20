/*
 * $Id: TcpMessageDispatcher.java 14004 2009-02-12 23:37:43Z aperepel $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.tcp;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.retry.RetryContext;
import org.mule.api.transformer.TransformerException;
import org.mule.transport.AbstractMessageDispatcher;
import org.mule.transport.tcp.TcpConnector;
import org.mule.transport.tcp.TcpInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * Send transformed Mule events over TCP.
 */
public class TcpMessageForYakDispatcher extends AbstractMessageDispatcher {

	private final  TcpConnector connector;
	private final static String ISHEARTBEAT="isHeartbeat";
	private  static HashMap LASTSENDMAP=new HashMap() ;
	
	public TcpMessageForYakDispatcher(OutboundEndpoint endpoint) {
		super(endpoint);
		this.connector = (TcpConnector) endpoint.getConnector();
		 
	}

	protected synchronized void doDispatch(MuleEvent event) throws Exception {
		Socket socket = connector.getSocket(event.getEndpoint());
		
	//connector.toString()
		try {
			dispatchToSocket(socket, event);
		} catch (IOException e1) { /**tucd change code 20100414 add catch */
			socket.close();
			throw e1;
		} finally {
			connector.releaseSocket(socket, event.getEndpoint());
		}
	}

	protected synchronized MuleMessage doSend(MuleEvent event) throws Exception {
		/**
		 * tucd 增加心跳报文 处理
		 */
		//event.getMessage()
		String mapkey=event.getEndpoint().getEndpointURI().getHost()+"_"+event.getEndpoint().getEndpointURI().getPort();
		
		if (event.getMessage().getProperty(ISHEARTBEAT) != null && event.getMessage().getProperty(ISHEARTBEAT).equals("yes")){
			long l1=(LASTSENDMAP.get(mapkey)==null)?0l:(Long) LASTSENDMAP.get(mapkey);
			long l2=System.currentTimeMillis();
			if ((l2-l1)<=3000l){
				//发现有业务报文 3秒内发送，此处不再发送心跳
				return null;
			}
		}else{
			LASTSENDMAP.put(mapkey,System.currentTimeMillis());
		}
			
		Socket socket = connector.getSocket(event.getEndpoint());
		/**
		 * 此处更改代码 增加异常处理 ,SocketException 调用 connector.releaseSocket(socket,
		 * endpoint)
		 */		
		/**tucd change code 20100414 begin */
		try {
			dispatchToSocket(socket, event);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			socket.close();
			connector.releaseSocket(socket, endpoint);
			//失败情况不记录时间
			LASTSENDMAP.put(mapkey,0l);
			throw e1;
		}
/**tucd change code 20100414 end */
		
		try {
			if (returnResponse(event)) {
				try {
					Object result = receiveFromSocket(socket, event
							.getTimeout(), endpoint);
					if (result == null) {
						return null;
					}

					if (result instanceof MuleMessage) {
						return (MuleMessage) result;
					}

					return new DefaultMuleMessage(connector
							.getMessageAdapter(result));
				} catch (SocketTimeoutException e) {
					// we don't necessarily expect to receive a response here
					logger
							.info("Socket timed out normally while doing a synchronous receive on endpointUri: "
									+ event.getEndpoint().getEndpointURI());
					return null;
				}
			} else {
				return null;
			}
		} finally {
			if (!returnResponse(event)) {
				connector.releaseSocket(socket, endpoint);
			}
		}

	}

	// Socket management (get and release) is handled outside this method
	private void dispatchToSocket(Socket socket, MuleEvent event)
			throws Exception {
		Object payload = event.transformMessage();
		logger.debug("***********paload******************** = " + new String((byte[])payload));
		write(socket, payload);
	}

	private void write(Socket socket, Object data) throws IOException,
			TransformerException {
		BufferedOutputStream bos = new BufferedOutputStream(socket
				.getOutputStream());
		connector.getTcpProtocol().write(bos, data);
		bos.flush();
	}

	protected static Object receiveFromSocket(final Socket socket, int timeout,
			final ImmutableEndpoint endpoint) throws Exception {
		final TcpConnector connector = (TcpConnector) endpoint.getConnector();
		DataInputStream underlyingIs = new DataInputStream(
				new BufferedInputStream(socket.getInputStream()));
		TcpInputStream tis = new TcpInputStream(underlyingIs) {
			@Override
			public void close() throws IOException {
				try {
					connector.releaseSocket(socket, endpoint);
				} catch (IOException e) {
					throw e;
				} catch (Exception e) {
					IOException e2 = new IOException();
					e2.initCause(e);
					throw e2;
				}
			}

		};

		if (timeout >= 0) {
			socket.setSoTimeout(timeout);
		}

		try {
			return connector.getTcpProtocol().read(tis);
		} catch (Exception e) {
			throw e;
		} finally {
			if (!tis.isStreaming()) {
				tis.close();
			}
		}
	}

	@Override
	protected synchronized void doDispose() {
		try {
			doDisconnect();
		} catch (Exception e) {
			logger.error("Failed to shutdown the dispatcher.", e);
		}
	}

	@Override
	protected void doConnect() throws Exception {
		// nothing, there is an optional validation in validateConnection()
	}

	@Override
	protected void doDisconnect() throws Exception {
		// nothing to do
	}

	@Override
	public RetryContext validateConnection(RetryContext retryContext) {
		Socket socket = null;
		try {
			socket = connector.getSocket(endpoint);

			retryContext.setOk();
		} catch (Exception ex) {
			retryContext.setFailed(ex);
		} finally {
			if (socket != null) {
				try {
					connector.releaseSocket(socket, endpoint);
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						logger.debug("Failed to release a socket " + socket, e);
					}
				}
			}
		}

		return retryContext;
	}
}

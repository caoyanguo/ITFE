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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

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

/**
 * Send transformed Mule events over TCP.
 */
public class TcpMsgDisForYakSocketTimeOut extends AbstractMessageDispatcher
{

    private final TcpConnector connector;

    public TcpMsgDisForYakSocketTimeOut(OutboundEndpoint endpoint)
    {
        super(endpoint);
        this.connector = (TcpConnector) endpoint.getConnector();
    }

    protected synchronized void doDispatch(MuleEvent event) throws Exception
    {
        Socket socket = connector.getSocket(event.getEndpoint());
        try 
        {
            dispatchToSocket(socket, event);
        }
        finally 
        {
            connector.releaseSocket(socket, event.getEndpoint());
        }
    }

    protected synchronized MuleMessage doSend(MuleEvent event) throws Exception
    {
        Socket socket = connector.getSocket(event.getEndpoint());
        dispatchToSocket(socket, event);

        try 
        {
            if (returnResponse(event))
            {
                try
                {
                    Object result = receiveFromSocket(socket, event.getTimeout(), endpoint);
                    if (result == null)
                    {
                        return null;
                    }
                    
                    if (result instanceof MuleMessage)
                    {
                        return (MuleMessage) result;
                    }
                    
                    return new DefaultMuleMessage(connector.getMessageAdapter(result));
                }
                catch (SocketTimeoutException e)
                {
                    // we don't necessarily expect to receive a response here
                    logger.info("Socket timed out normally while doing a synchronous receive on endpointUri: "
                        + event.getEndpoint().getEndpointURI());
//                    return null;
                    throw e;
                }
            }
            else
            {
                return null;
            }
        }
        finally
        {
            if (!returnResponse(event))
            {
                connector.releaseSocket(socket, endpoint);
            }
        }
        
    }

    // Socket management (get and release) is handled outside this method
    private void dispatchToSocket(Socket socket, MuleEvent event) throws Exception
    {
        Object payload = event.transformMessage();
        write(socket, payload);
    }

    private void write(Socket socket, Object data) throws IOException, TransformerException
    {
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        connector.getTcpProtocol().write(bos, data);
        bos.flush();
    }

    protected static Object receiveFromSocket(final Socket socket, int timeout, final ImmutableEndpoint endpoint)
            throws IOException
    {
        final TcpConnector connector = (TcpConnector) endpoint.getConnector();
        DataInputStream underlyingIs = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        TcpInputStream tis = new TcpInputStream(underlyingIs)
        {
            @Override
            public void close() throws IOException
            {
                try
                {
                    connector.releaseSocket(socket, endpoint);
                }
                catch (IOException e)
                {
                   throw e;
                }
                catch (Exception e)
                {
                    IOException e2 = new IOException();
                    e2.initCause(e);
                    throw e2;
                }
            }

        };

        if (timeout >= 0)
        {
            socket.setSoTimeout(timeout);
        }

        try
        {
            return connector.getTcpProtocol().read(tis);
        }
        finally
        {
            if (!tis.isStreaming())
            {
                tis.close();
            }
        }
    }

    @Override
    protected synchronized void doDispose()
    {
        try
        {
            doDisconnect();
        }
        catch (Exception e)
        {
            logger.error("Failed to shutdown the dispatcher.", e);
        }
    }

    @Override
    protected void doConnect() throws Exception
    {
        // nothing, there is an optional validation in validateConnection()
    }

    @Override
    protected void doDisconnect() throws Exception
    {
        //nothing to do
    }

    @Override
    public RetryContext validateConnection(RetryContext retryContext)
    {
        Socket socket = null;
        try
        {
            socket = connector.getSocket(endpoint);

            retryContext.setOk();
        }
        catch (Exception ex)
        {
            retryContext.setFailed(ex);
        }
        finally
        {
            if (socket != null)
            {
                try
                {
                    connector.releaseSocket(socket, endpoint);
                }
                catch (Exception e)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Failed to release a socket " + socket, e);
                    }
                }
            }
        }
        
        return retryContext;
    }
}

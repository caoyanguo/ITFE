package com.cfcc.yak.transport.tcp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.transport.tcp.protocols.DirectProtocol;

public class YakLengthProtocolClient extends DirectProtocol
{
  private static final Log logger = LogFactory.getLog(YakLengthProtocolClient.class);
  private static final int SIZE_INT = 4;
  public static final int NO_MAX_LENGTH = -1;
  private int maxMessageLength;

  public YakLengthProtocolClient()
  {
    this(-1);
  }

  public YakLengthProtocolClient(int maxMessageLength) {
    super(false, 4);
    setMaxMessageLength(maxMessageLength);
  }

  public Object read(InputStream is)
    throws IOException
  {
    return new byte[0];
  }

  protected void writeByteArray(OutputStream os, byte[] data)
    throws IOException
  {
    DataOutputStream dos = new DataOutputStream(os);
    if (logger.isDebugEnabled()) {
      logger.debug("发送报文长度为: " + data.length);
    }
    dos.write(append(data.length).getBytes());
    String strData = new String(data);
    logger.debug("*******发送报文: " + strData);
    if ((strData != null) && (!strData.equals("")) && (!strData.equals("ACK"))) {
      dos.write(data);
    }
    dos.flush();
  }

  private String append(int length) {
    String lengthStr = String.valueOf(length);
    int tmp = 4 - lengthStr.length();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < tmp; i++) {
      builder.append("0");
    }
    builder.append(lengthStr);
    return builder.toString();
  }

  protected boolean isRepeat(int len, int available)
  {
    return true;
  }

  public int getMaxMessageLength() {
    return this.maxMessageLength;
  }

  public void setMaxMessageLength(int maxMessageLength) {
    this.maxMessageLength = maxMessageLength;
  }
}
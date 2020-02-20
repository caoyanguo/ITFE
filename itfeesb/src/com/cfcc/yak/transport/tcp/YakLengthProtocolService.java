package com.cfcc.yak.transport.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.transport.tcp.protocols.DirectProtocol;

public class YakLengthProtocolService extends DirectProtocol
{
  private static final Log logger = LogFactory.getLog(YakLengthProtocolService.class);
  private static final int SIZE_INT = 4;
  public static final int NO_MAX_LENGTH = -1;
  private int maxMessageLength;

  public YakLengthProtocolService()
  {
    this(-1);
  }

  public YakLengthProtocolService(int maxMessageLength) {
    super(false, 4);
    setMaxMessageLength(maxMessageLength);
  }

  public Object read(InputStream is)
    throws IOException
  {
    DataInputStream dis = new DataInputStream(is);

    dis.mark(4);

    if (super.read(dis, 4) == null) {
      return null;
    }

    dis.reset();
    byte[] lengthByt = new byte[4];
    dis.read(lengthByt);
    String lengthStr = new String(lengthByt);
    int length = 0;
    try {
      length = Integer.parseInt(lengthStr.trim());
    } catch (NumberFormatException e) {
      logger.error("不是合法的报文", e);
      return new byte[0];
    }
    if (logger.isDebugEnabled()) {
      logger.debug("接收到标识长度为 " + length + " 的报文");
    }

    if ((length < 0) || (
      (getMaxMessageLength() > 0) && (length > getMaxMessageLength()))) {
      throw new IOException("Length " + length + " exceeds limit: " + 
        getMaxMessageLength());
    }

    byte[] buffer = new byte[length];

    dis.readFully(buffer);
    if (logger.isDebugEnabled()) {
      logger.debug("实际接收报文长度为: " + buffer.length);
    }

    if (length == 0)
    {
      logger.debug(" 侦测报文");
      return new byte[0];
    }

    return buffer;
  }

  protected void writeByteArray(OutputStream os, byte[] data)
    throws IOException
  {
    DataOutputStream dos = new DataOutputStream(os);
    if (logger.isDebugEnabled()) {
      logger.debug("发送报文长度为: " + data.length);
    }
    dos.write(append(0).getBytes());
    logger.info("××××××××××××××××××××××同步回复：" + append(0));

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
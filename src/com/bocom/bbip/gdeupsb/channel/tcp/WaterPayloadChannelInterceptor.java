package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.IOException;
import java.net.Socket;

import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.ChannelContext;
import com.bocom.jump.bp.channel.tcp.interceptors.SocketChannelInterceptor;
import com.bocom.jump.bp.core.ContextEx;
import com.bocom.jump.bp.support.TRACER;

public class WaterPayloadChannelInterceptor extends WaterLengthStreamResolver
  implements SocketChannelInterceptor
{
  public void onRequest(ChannelContext<Socket, Socket> paramChannelContext, ContextEx paramContextEx)
    throws JumpException
  {
    Socket localSocket = (Socket)paramChannelContext.getRequest();
    try
    {
      paramChannelContext.setRequestPayload(resolve(localSocket.getInputStream()));
    } catch (IOException localIOException) {
      throw new JumpException("JUMPTP8000", "socket_read_error", localIOException);
    }
  }

  public void onResponse(ChannelContext<Socket, Socket> paramChannelContext, ContextEx paramContextEx, Throwable paramThrowable) {
    byte[] arrayOfByte = (byte[])paramChannelContext.getResponsePalyload();
    try {
      ((Socket)paramChannelContext.getResponse()).getOutputStream().write(arrayOfByte);
    } catch (IOException localIOException) {
      TRACER.trace("write socket error", localIOException);
    }
  }
}
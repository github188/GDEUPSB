package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.IOException;
import java.net.Socket;

import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.ChannelContext;
import com.bocom.jump.bp.channel.tcp.interceptors.SocketChannelInterceptor;
import com.bocom.jump.bp.core.ContextEx;
import com.bocom.jump.bp.util.Hex;

public class NoFrontPayloadChannelInterceptorRq extends NoFrontLengthStreamResolverEb
		implements SocketChannelInterceptor
{

	public NoFrontPayloadChannelInterceptorRq()
	{
	}

	public void onRequest(ChannelContext channelContext, ContextEx context)
			throws JumpException
	{
		Socket socket = (Socket) channelContext.getRequest();
		try
		{
			channelContext.setRequestPayload(resolve(socket.getInputStream()));
		} catch (IOException io)
		{
			throw new JumpException("JUMPTP8000", "socket_read_error", io);
		}
	}

	public void onResponse(ChannelContext channelContext, ContextEx context, Throwable throwable)
	{
		byte arrayOfByte[] = (byte[]) channelContext.getResponsePalyload();
		
//        System.out.println("处理后，byte=\n"+Hex.toDumpString(arrayOfByte)+",发送的报文转化为明文为:"+new String(arrayOfByte));
		try
		{
			((Socket) channelContext.getResponse()).getOutputStream().write(arrayOfByte);
		} catch (IOException localIOException)
		{
			throw new RuntimeException(localIOException);
		}
	}
}

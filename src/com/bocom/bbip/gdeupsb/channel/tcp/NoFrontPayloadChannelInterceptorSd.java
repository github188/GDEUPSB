package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.IOException;
import java.net.Socket;

import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.ChannelContext;
import com.bocom.jump.bp.channel.tcp.interceptors.SocketChannelInterceptor;
import com.bocom.jump.bp.core.ContextEx;
import com.bocom.jump.bp.util.Hex;

public class NoFrontPayloadChannelInterceptorSd extends NoFrontLengthStreamResolverEb
		implements SocketChannelInterceptor
{

	public NoFrontPayloadChannelInterceptorSd()
	{
	}

	public void onRequest(ChannelContext channelContext, ContextEx context)
			throws JumpException
	{
		Socket socket = (Socket) channelContext.getRequest();
		try
		{
			
			byte[] ints=resolve(socket.getInputStream());
			
			System.out.println("接收到的初始报文为:"+Hex.toDumpString(ints));
			byte[] deal=new byte[ints.length-10];
//			syste
			System.arraycopy(ints, 10, deal, 0, deal.length);
			
			System.out.println("经过处理后的报文为:"+Hex.toDumpString(deal));
			channelContext.setRequestPayload(deal);
		} catch (IOException io)
		{
			throw new JumpException("JUMPTP8000", "socket_read_error", io);
		}
	}

	public void onResponse(ChannelContext channelContext, ContextEx context, Throwable throwable)
	{
		byte arrayOfByte[] = (byte[]) channelContext.getResponsePalyload();
		try
		{
			((Socket) channelContext.getResponse()).getOutputStream().write(arrayOfByte);
		} catch (IOException localIOException)
		{
			throw new RuntimeException(localIOException);
		}
	}
}

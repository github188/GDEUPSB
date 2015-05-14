package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import utils.system;

import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.ChannelContext;
import com.bocom.jump.bp.channel.tcp.interceptors.SocketChannelInterceptor;
import com.bocom.jump.bp.core.ContextEx;
import com.bocom.jump.bp.util.Hex;

public class NoFrontPayloadChannelInterceptorGzd extends NoFrontLengthStreamResolverEb
		implements SocketChannelInterceptor
{

	public NoFrontPayloadChannelInterceptorGzd()
	{
	}

	public void onRequest(ChannelContext channelContext, ContextEx context)
			throws JumpException
	{
		Socket socket = (Socket) channelContext.getRequest();
		try
		{
			byte[] a = resolve(socket.getInputStream());
			
//			System.out.println("初始的byte="+Hex.toDumpString(a));
			byte[] inputB = new byte[a.length + 81];
			byte[] b = a;
			int j = 0;
			int t = 0;
			int p = 0;
			int m = 0;
			int len = 0;
			byte[] reamrk = new byte[73];
			
			byte[] msgB=new byte[6];
			System.arraycopy(a, 4, msgB, 0, 6);
			String msgS=new String(msgB);
//			System.out.println("当前的msgS="+msgS);
			if("020005".equals(msgS)){
				for (int i = 0; i < b.length; i++) {
					byte c = b[i];
					if (c == 0x7c) {
						j++;
						// byte[] d=new byte[]{c};
						// System.out.println(new String(d));
					}
					if (j == 12) {
						if (t == 0) {
							t = i + 1; // 得到第12个|的长度
//							System.out.println("t=" + t);
						}
					}
					if (j == 13) {
						if (p == 0) {
							p = i;
							len = p - t;
						}
					}
					if (j == 14) {
						if (m == 0) {
							m = i;
						}
					}
				}
				// 判断用户编号是否含有|,如果p-i的小于20，则表示用户编号含有|
				if (len > 0 && len < 20) {
					len = m - t;
//					System.out.println("用户编号含有|，当前的len处理后为：" + len + ",当前的m=" + m + ",当前的t=" + t);
				}
				System.arraycopy(b, t, reamrk, 0, len);
			}else if("020006".equals(msgS)){
				for (int i = 0; i < b.length; i++) {
					byte c = b[i];
					if (c == 0x7c) {
						j++;
						// byte[] d=new byte[]{c};
						// System.out.println(new String(d));
					}
					if (j == 11) {
						if (t == 0) {
							t = i + 1; // 得到第12个|的长度
//							System.out.println("t=" + t);
						}
					}
					if (j == 12) {
						if (p == 0) {
							p = i;
							len = p - t;
						}
					}
					if (j == 13) {
						if (m == 0) {
							m = i;
						}
					}
				}
				// 判断用户编号是否含有|,如果p-i的小于20，则表示用户编号含有|
				if (len > 0 && len < 20) {
					len = m - t;
//					System.out.println("用户编号含有|，当前的len处理后为：" + len + ",当前的m=" + m + ",当前的t=" + t);
				}
				System.arraycopy(b, t, reamrk, 0, len);
			}

			// 将a复制到inputB
			System.arraycopy(a, 0, inputB, 0, a.length);
			String c="start"+new String(reamrk)+"|";

			System.arraycopy(c.getBytes(), 0, inputB, a.length, c.getBytes().length);
			
			int aftDel=inputB.length;
			String aftDealS=String.valueOf(aftDel);
			
			aftDealS=StringUtils.leftPad(aftDealS, 4, '0');
			byte[] realB=new byte[aftDel];
			
//			System.out.println(Hex.toDumpString(inputB));
//			System.out.println(Hex.toDumpString(realB));
			byte[] aftDealB=aftDealS.getBytes();
			System.arraycopy(aftDealB, 0, realB, 0, 4);
			System.arraycopy(inputB, 4, realB, 4, inputB.length-4);
//			System.out.println(Hex.toDumpString(realB));
			
			
//			System.out.println("在payload阶段，当前得到的东西为:\n" + Hex.toDumpString(realB));
			channelContext.setRequestPayload(realB);

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
		// Map<String, Object> payLoadMap = (Map<String, Object>)
		// channelContext.getResponsePalyload();
		//
		// GzDl8583DE de = new GzDl8583DE();
		// Map map8583 = null;
		// try {
		// map8583 = de.Read8583CFG();
		//
		// byte[] b = de.Pack8583(payLoadMap, map8583);
		//
		// byte[] resultB=new byte[b.length-8];
		// System.arraycopy(b, 8, resultB, 0, resultB.length);
		//
		// System.out.println("返回给通讯前置机的报文为:\n"+Hex.toDumpString(resultB));
		//
		// ((Socket)
		// channelContext.getResponse()).getOutputStream().write(resultB);
		// } catch (IOException localIOException)
		// {
		// throw new RuntimeException(localIOException);
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
	}
}

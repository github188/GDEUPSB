package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.IOException;
import java.io.InputStream;

import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.channel.tcp.StreamResolver;
import com.bocom.jump.bp.util.Hex;

public class ElecFrontStreamResolver implements StreamResolver
{

	public ElecFrontStreamResolver()
	{
	}

	public byte[] resolve(InputStream in)
	{
		return read(in);
	}

	private byte[] read(InputStream in)
	{
		try {
			int count = 0;
			while (true) {
				count = in.available();
				if (count > 0) {
					break;
				}
			}
			byte[] resultByte = new byte[count];
			in.read(resultByte);
			
			  byte[] sendBuffer = (byte[]) resultByte;
		        
		        int len=sendBuffer.length-10;
		        String lenS=StringUtils.leftPad(String.valueOf(len), 10, "0");
		        System.arraycopy(lenS.getBytes(), 0, sendBuffer, 0, 10);
		        System.out.println("接收消息，处理后的byte=\n"+Hex.toDumpString(sendBuffer));
			
			return sendBuffer;
		} catch (IOException e) {
			throw new RuntimeException("socket_read_error");
		}
	}
}

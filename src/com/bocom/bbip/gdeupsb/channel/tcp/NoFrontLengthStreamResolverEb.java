package com.bocom.bbip.gdeupsb.channel.tcp;

import com.bocom.jump.bp.channel.tcp.StreamResolver;

import java.io.*;

public class NoFrontLengthStreamResolverEb
		implements StreamResolver
{

	public NoFrontLengthStreamResolverEb()
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
			int a = 0;
			while (count == 0) {
				count = in.available();
				Thread.sleep(500);
				a++;
				if (a > 10) {
					break;
				}
			}
			byte[] resultByte = new byte[count];
			int readCount = 0;
			while (readCount < count) {
				readCount += in.read(resultByte, readCount, count - readCount);
			}
			return resultByte;
		} catch (IOException e) {
			throw new RuntimeException("socket_read_error");
		} catch (InterruptedException e) {
			throw new RuntimeException("socket_read_error");
		}
	}
}

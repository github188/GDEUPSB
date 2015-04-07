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
			while (true) {
				count = in.available();
				if (count > 0) {
					break;
				}
			}
			byte[] resultByte = new byte[count];
			in.read(resultByte);
			return resultByte;
		} catch (IOException e) {
			throw new RuntimeException("socket_read_error");
		}
	}
}

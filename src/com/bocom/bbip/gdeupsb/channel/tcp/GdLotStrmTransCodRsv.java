package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.bocom.bbip.thd.org.apache.commons.io.output.ByteArrayOutputStream;
import com.bocom.jump.bp.channel.tcp.StreamResolver;

public class GdLotStrmTransCodRsv implements StreamResolver{
	
	private String TTxnCd;
	public void setTTxnCd(String tTxnCd) {
		TTxnCd = tTxnCd;
	}

	@Override
	public byte[] resolve(InputStream in) {
		try {
			return read(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private byte[] read(InputStream in) throws IOException{
		BufferedReader is = new BufferedReader(new InputStreamReader(in));
		ByteArrayOutputStream out = new ByteArrayOutputStream(64);
		String outDat = null;
//		outDat = is.read();
//		System.out.println("outDat["+outDat+"]");
		out.write(outDat.getBytes("GBK"));
		return out.toByteArray();
	}

}

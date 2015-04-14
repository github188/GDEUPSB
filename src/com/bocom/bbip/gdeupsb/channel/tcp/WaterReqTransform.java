package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.UnsupportedEncodingException;

import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.Transform;

public class WaterReqTransform implements Transform{

	@Override
	public boolean support(Object arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object transform(Object obj, String arg1) throws JumpException {
		byte[] arrayOfByte = (byte[]) obj;
		String sndStrPre ="";
		try {
			sndStrPre = new String(arrayOfByte, "GBK");
			String length= sndStrPre.substring(0, 4);
			String []len=new String[4];
			String s="";
			if(length.startsWith("0")){
					len[0]=length.substring(1, 2);
					len[1]=length.substring(2, 3);
					len[2]=length.substring(3, 4);
				     len[3]=" ";
			}
			StringBuilder builder=new StringBuilder();
		    for(String ss:len){
		    	builder.append(ss);
		    }
		    sndStrPre=sndStrPre.replace(sndStrPre.substring(0, 4), builder.toString());
		    arrayOfByte=sndStrPre.getBytes("GBK");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return arrayOfByte;
	}

}

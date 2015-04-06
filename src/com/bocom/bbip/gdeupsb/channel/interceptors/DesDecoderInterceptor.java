package com.bocom.bbip.gdeupsb.channel.interceptors;

import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.Transform;

public class DesDecoderInterceptor implements Transform {

	@Override
	public boolean support(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object transform(Object object, String s) throws JumpException {

		String objString=object.toString();
		
		if (objString.substring(5,9).equals("8918")){
            int inIndex = objString.indexOf("<IN>");
			objString = objString.substring(0,inIndex+4)+"<ftpNo>tbcCheckFile</ftpNo>"+objString.substring(inIndex+4,objString.length());
			int length=Integer.parseInt(objString.substring(0,4));
			length=length+27;
			String header=Integer.toString(length);
			if(header.length()<4){
				header="0"+header;
			}
			objString=header+objString.substring(5,objString.length());
		}
		return objString;
	}
}

package com.bocom.bbip.gdeupsb.channel.interceptors;

import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.Transform;

public class DelXmlLenTransform implements Transform {
	
	private int lengthSize;

	public void setLengthSize(int lengthSize) {
		this.lengthSize = lengthSize;
	}

	@Override
	public boolean support(Object obj) {
		return true;
	}

	@Override
	public Object transform(Object obj, String s) throws JumpException {
		byte[] msg = null;
		if (obj instanceof byte[]) {
			msg = (byte[]) obj;
		}
		byte[] newmsg = new byte[msg.length-lengthSize];
		System.arraycopy(msg, lengthSize, newmsg, 0, msg.length-lengthSize);
		return newmsg;
	}

}

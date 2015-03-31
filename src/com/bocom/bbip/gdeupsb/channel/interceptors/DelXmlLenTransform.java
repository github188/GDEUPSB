package com.bocom.bbip.gdeupsb.channel.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.gdeupsb.utils.GzFcXmlExchangeUtil;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.Transform;

public class DelXmlLenTransform implements Transform {
	
	private static Logger log = LoggerFactory.getLogger(DelXmlLenTransform.class);
	
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
		try {
			newmsg = GzFcXmlExchangeUtil.GzFcXmlAdd(new String(newmsg,"UTF-8")).getBytes("UTF-8");
			log.info("recv msg:"+new String(newmsg,"UTF-8"));
		}  catch (Exception e) {
			e.printStackTrace();
			throw new JumpException("recv xml exchange error");
		}
		return newmsg;
	}

}

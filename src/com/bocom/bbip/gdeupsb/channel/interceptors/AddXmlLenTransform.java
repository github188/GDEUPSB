package com.bocom.bbip.gdeupsb.channel.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.Transform;
public class AddXmlLenTransform implements Transform {
    
    private static Logger log = LoggerFactory.getLogger(AddXmlLenTransform.class);
    
    private int lengthSize = 0;

    public void setLengthSize(int lengthSize) {
        this.lengthSize = lengthSize;
    }

    @Override
    public boolean support(Object obj) {
        return true;
    }

    @Override
    public Object transform(Object obj, String s) throws JumpException {
        byte[] req = null;
        if (obj instanceof byte[]) {
            req = (byte[]) obj;
        } else {
            throw new JumpException("msg type is error");
        }
        log.info("lengthSize="+lengthSize);
        if (lengthSize == 0) {
            return req;
        } else {
            byte[] newreq = new byte[req.length+lengthSize];
            String len = StringUtils.leftPad(String.valueOf(req.length), lengthSize, "0");
            System.arraycopy(len.getBytes(), 0, newreq, 0, lengthSize);
            System.arraycopy(req, 0, newreq, lengthSize, req.length);
            log.info("send msg = "+new String(newreq));
            return newreq;
        }
    }

}

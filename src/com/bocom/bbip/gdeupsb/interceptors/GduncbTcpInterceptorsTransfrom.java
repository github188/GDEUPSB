package com.bocom.bbip.gdeupsb.interceptors;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.gdeupsb.utils.UtilsCnlty;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.Transform;
import com.bocom.jump.bp.channel.interceptors.RequestTransform;

public class GduncbTcpInterceptorsTransfrom implements Transform{

    public GduncbTcpInterceptorsTransfrom()
    {
    	a = "GBK";
    }

    public void setEncoding(String s)
    {
        if("UTF-8".equals(s))
            s = "UTF8";
        else
        if("UTF-16BE".equals(s))
            s = "UTF16_BE";
        else
        if("UTF-16LE".equals(s))
            s = "UTF16_LE";
        else
        if("UTF-32BE".equals(s))
            s = "UTF32_BE";
        else
        if("UTF-32LE".equals(s))
            s = "UTF32_LE";
        else
            throw new IllegalArgumentException("unsupported encoding, supported: UTF-8, UTF-16BE, UTF-16LE, UTF-32BE, UTF-32LE.");
        a = s;
    }

    public boolean support(Object obj)
    {
        return obj != null && ((obj instanceof Map) || (obj instanceof byte[]) || (obj instanceof String));
    }

    @SuppressWarnings("rawtypes")
	public Object transform(Object obj, String s)
        throws JumpException
    {
        if(RequestTransform.isTraceOn)
        {
            String s1 = null;
            if(obj instanceof byte[])
                try
                {
                    s1 = a != null ? new String((byte[])obj, a) : new String((byte[])obj);
                }
                catch(UnsupportedEncodingException unsupportedencodingexception)
                {
                    throw new JumpException("JUMPHP8008", "unsupport_encoding", unsupportedencodingexception);
                }
            else
            if(obj instanceof Map){
                s1 = ((Map)obj).toString();
                
            }
            else
            {
                s1 = (String)obj;
            }
			//广东联通代扣返回拦截
            s1 = s1.trim();
        	Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
			Matcher m = p.matcher(s1);
			int count = 0;
			while (m.find()) {
				count++;
			}
            s1 =UtilsCnlty.fillEmptyRt(String.valueOf((s1.length()+count)),4) + s1.substring(4);
            try {
				obj = s1.getBytes("GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
            logger.debug((new StringBuilder(String.valueOf(b))).append(s1).toString());
        }
        
        return obj;
    }

    protected static Logger logger = LoggerFactory.getLogger("com.bocom.jump.bp.messageTracing");
    private String a;
    private static String b = "Response Message Data : ";

}

	
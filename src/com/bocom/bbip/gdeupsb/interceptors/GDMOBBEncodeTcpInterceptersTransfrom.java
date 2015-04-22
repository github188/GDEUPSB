package com.bocom.bbip.gdeupsb.interceptors;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.bocom.bbip.gdeupsb.utils.MacEcbUtils;
import com.bocom.bbip.gdeupsb.utils.UtilsCnlty;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.Transform;
import com.bocom.jump.bp.channel.interceptors.RequestTransform;

public class GDMOBBEncodeTcpInterceptersTransfrom implements Transform {
	protected static Logger logger = LoggerFactory.getLogger("com.bocom.jump.bp.messageTracing");
	 private String a;
	 private static String b = "Response Message Data : ";
	 public GDMOBBEncodeTcpInterceptersTransfrom()
	    {
	    	//a = "UTF8";
		 a = "GBK";
	    }

	    public void setEncoding(String s)
	    {
	        if("GBK".equals(s))
	            s = "GBK";
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

	    @SuppressWarnings("unchecked")
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
	            if(obj instanceof Map)
	                s1 = ((Map<String,Object>)obj).toString();
	            else
	                s1 = (String)obj;
	        	Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
				Matcher m = p.matcher(s1);
				int count = 0;
				while (m.find()) {
					count++;
				}
	            s1 =UtilsCnlty.fillZero(String.valueOf((s1.length()+4+count)),4) + s1;
	            try {
	            	String strCode = s1.substring(8,12);
	            	if(!strCode.equals("0004")){
	            		String macCode = MacEcbUtils.getGdmobbMac(s1, "GBK")[1];
	            		s1 = s1.substring(0, s1.length()-8)+macCode;
	            	}
				} catch (Exception e) {
					e.printStackTrace();
				}
	            try {
					obj = s1.getBytes("GBK");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	          
	            logger.debug((new StringBuilder(String.valueOf(b))).append(s1).toString());
	        }
	        return obj;
	    }
}	


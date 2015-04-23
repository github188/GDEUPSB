package com.bocom.bbip.gdeupsb.interceptors;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.gdeupsb.utils.MacEcbUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.Transform;
import com.bocom.jump.bp.channel.interceptors.RequestTransform;

public class GDMOBBDecodeTcpInterceptersTranfrom implements Transform{
	protected static Logger log = LoggerFactory.getLogger("com.bocom.jump.bp.messageTracing");
	 private String a;
	 private static String b = "Response Message Data : ";
	 public GDMOBBDecodeTcpInterceptersTranfrom()
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
	            
	            String strCode  = s1.substring(8,12);
	            if(!strCode.equals("0004")){
	            	String mac = s1.substring(s1.length()-8,s1.length());
	            	log.info("接受的mac:"+mac);
		            try {
						String strMac = MacEcbUtils.getGdmobbMac(s1.substring(4,s1.length()-8),"GBK")[1];
						log.info("加密后的mac:"+strMac);
//						if(!StringUtils.equals(mac, strMac)){
//							log.info("mac校验不通过！");
//							throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
//						}else{
//							log.info("mac校验已通过...");
//						}
					} catch (UnsupportedEncodingException e) {
						throw new JumpException(e.getMessage());
					}catch(Exception e1){
						throw new JumpException(e1.getMessage());
					}
		            
	            }
	            log.debug((new StringBuilder(String.valueOf(b))).append(s1).toString());
	        }
	        return obj;
	    }
}

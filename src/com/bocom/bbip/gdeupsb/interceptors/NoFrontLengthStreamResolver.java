/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
package  com.bocom.bbip.gdeupsb.interceptors;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.jump.bp.JumpRuntimeException;
import com.bocom.jump.bp.channel.tcp.StreamResolver;

/**
 * 无前置长度流解析
 * 
 * @version 1.0.0,2015-2-4
 * @author sunbg
 * @since 1.0.0
 * {@inheritDoc}
 */

public class NoFrontLengthStreamResolver implements StreamResolver{

	protected static Logger log = LoggerFactory.getLogger(NoFrontLengthStreamResolver.class);

	@Override
	public byte[] resolve(InputStream inputstream) {
		try {
			int count = inputstream.available();
			int a = 0;
			while(count==0){
				count = inputstream.available();
				Thread.sleep(300);
				a++;
				if(a>300){
					break;
				}
				log.info("count:"+count+",a:"+a);
			}
			log.info("报文长度"+count);
			byte[] b = new byte[count];
			int readCount = 0;
			while(readCount<count){
				readCount+=inputstream.read(b,readCount,count-readCount);
			}
			log.info("报文GBK:"+new String(b,"GBK"));
			log.info("报文UTF-8:"+new String(b,"UTF-8"));
			return b;
			
			
//			byte[] bs = new byte[2048];
//			inputstream.read(bs);
//			log.info("bs byte[]"+Arrays.toString(bs));
//			log.info("bs String"+new String(bs));
//			log.info("bs="+Hex.encode(bs));
			// 如果没有长度配置,则循环读取到没有数据
		    //log.info("inputstream:"+new String(IOUtils.toString(inputstream).getBytes(),"GBK"));
		    //log.info("inputstream:"+new String(IOUtils.toString(inputstream).getBytes(),"UTF-8"));
//			log.info("inputStream:"+IOUtils.toString(inputstream));
//			log.info("inputStream:"+inputstream.read(new byte[2048]));
//			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(64);
//			int length = 0;
//			while ((length = inputstream.read(b)) != -1) {
//				log.info("inputstream length is" + length);
//				byte[] tempB = new byte[length];
//				for (int i = 0; i < tempB.length; i++) {
//					tempB[i] = b[i];
//				}
//				bytearrayoutputstream.write(tempB);
//				if(length<1024){
//					break;
//				}
//			}
//			return bytearrayoutputstream.toByteArray();
			
//			byte[] dsbyte = IOUtils.toByteArray(inputstream);
//			log.info("dsbyte String"+new String(dsbyte));
//		    String ds = new String(IOUtils.toString(inputstream).getBytes("ISO-8859-1"),"GBK");
//		    String ds1 = new String(IOUtils.toString(inputstream).getBytes("ISO-8859-1"),"UTF-8");
//		    String ds2 = new String(IOUtils.toString(inputstream).getBytes("ISO-8859-1"),"UTF-16BE");
//		    String ds3 = new String(IOUtils.toString(inputstream).getBytes("ISO-8859-1"));
//		    log.info("16进制"+MacEcbUtils.toHexString(ds));
//		    log.info("length:"+inputstream.available());
//		    log.info("ds:"+ds +"byte length:"+IOUtils.toString(inputstream).getBytes("ISO-8859-1").length);
//		    log.info("ds:"+ds +"byte length:"+IOUtils.toString(inputstream).getBytes("UTF-8").length);
//		    log.info("ds:"+ds +"byte length:"+IOUtils.toString(inputstream).getBytes("GBK").length);
//		    log.info("ds:"+ds +"byte length:"+IOUtils.toString(inputstream).getBytes().length);
//		    log.info("ds1"+ds1);
//		    log.info("ds2"+ds2);
//		    log.info("ds3"+ds3);
//		 
//		    log.info("ds a"+Arrays.toString(IOUtils.toString(inputstream).getBytes("ISO-8859-1")));
//		    log.info("ds b"+Arrays.toString(IOUtils.toString(inputstream).getBytes("UTF-8")));
//		    log.info("ds c"+Arrays.toString(IOUtils.toString(inputstream).getBytes("GBK")));
//		    log.info("ds d"+Arrays.toString(IOUtils.toString(inputstream).getBytes()));
//		    log.info("ds length:"+ds.getBytes("GBK").length);
//		    byte[] dsbyte = ds.getBytes("GBK");
			
			
//		    return bs;
		    
		    /*log.info("GBK length:"+IOUtils.toString(inputstream).getBytes("GBK").length);
		    log.info("UTF-8 length:"+IOUtils.toString(inputstream).getBytes("UTF-8").length);
		    log.info("default length:"+IOUtils.toString(inputstream).getBytes().length);
		    byte[] response = IOUtils.toString(inputstream).getBytes("UTF-8");
		    log.info("response length:"+response.length);
		    return response;*/
			/*byte[] b = new byte[1024];// 每次读取1024长度
			int length = 0;
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(64);
			while ((length = inputstream.read(b)) != -1) {
				log.info("inputstream length is" + length);
				byte[] tempB = new byte[length];
				for (int i = 0; i < tempB.length; i++) {
					tempB[i] = b[i];
				}
				bytearrayoutputstream.write(tempB);
				if(length<1024){
					break;
				}
			}
			return bytearrayoutputstream.toByteArray();*/
		} catch (Exception ex) {
			throw new JumpRuntimeException("JUMPTP8000", "socket_read_error",ex);
		}
		
	}
}


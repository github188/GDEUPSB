package com.bocom.bbip.gdeupsb.channel.interceptors;

import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.adaptor.exception.SocketReadException;
import com.bocom.bbip.gdeupsb.channel.tcp.WatrSocketGateway;
import com.bocom.bbip.utils.CryptoUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.Transform;

public class WatrResponseTransform implements Transform {

	private static final Log log = LogFactory.getLog(WatrResponseTransform.class);
	
	@Override
	public boolean support(Object arg0) {
		return true;
	}

	@Override
	public Object transform(Object arg0, String arg1) throws JumpException {
		byte[] arrayOfByte1 = (byte[]) arg0;
		String sndStrPre;
		try {
			sndStrPre = new String(arrayOfByte1, "GBK");
			log.info("sndStrPre=" + sndStrPre);
			String detailDate = new String(sndStrPre.substring(129).getBytes("GBK"),"GBK");

			log.info("detailDate,即拼接在后面的报文内容=" + detailDate);
			String md5Date = CryptoUtils.md5(detailDate.toString().getBytes("GBK")); // 校验位
			log.info("md5Date=" + md5Date);

			StringBuffer sb = new StringBuffer();
			log.info("初始报文头(不含校验位)=" + sndStrPre.substring(0, 97));
			sb.append(sndStrPre.substring(0, 97)); // 初始报文头(不含校验位)
			sb.append(md5Date);
			
			sb.append(detailDate);
			arrayOfByte1 = sb.toString().getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			throw new JumpException("msg encode error", e);
		}
		return arrayOfByte1;
	}

}

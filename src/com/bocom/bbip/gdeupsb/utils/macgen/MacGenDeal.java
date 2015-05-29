package com.bocom.bbip.gdeupsb.utils.macgen;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.infrastructure.mac.MACGenerator;
import com.bocom.bbip.utils.CryptoUtils;
import com.bocom.bbip.utils.StringUtils;



/**
 * MAC 生成器，MD5 算法
 * @author xuxinchao
 *
 */
public class MacGenDeal extends MACGenerator {

	private static final Log logger = LogFactory.getLog(MacGenDeal .class);
	
    @Override
    public Object doGenerateMac(Map<String,Object> data, byte[] bytes) {
        //本处写接收到MAC的字节，具体的算法.
    	//获取工作密钥
    	String key = StringUtils.leftPad(" ", 32);
    	logger.info("md5 key:[" + key + "]");
    	//工作密钥加入到末尾
    	String md5BlockSrc;
    	String md5="";
		try {
			md5BlockSrc = new String(bytes,"GBK");
			StringBuffer md5Block = new StringBuffer(md5BlockSrc); 
	    	md5Block.append("~"+key);
	    	logger.info("calcuate md5 block :[" + md5Block + "]");
	    	md5 = CryptoUtils.md5(md5Block.toString().getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			
		}
    	
    	logger.info("md5 result: [" + md5 + "]");
        return md5;
    }
}

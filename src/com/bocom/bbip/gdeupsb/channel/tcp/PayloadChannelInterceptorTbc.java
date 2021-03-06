package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.gdeupsb.entity.GdTbcBasInf;
import com.bocom.bbip.gdeupsb.repository.GdTbcBasInfRepository;
import com.bocom.bbip.utils.CryptoUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.ChannelContext;
import com.bocom.jump.bp.channel.tcp.interceptors.PayloadChannelInterceptor;
import com.bocom.jump.bp.channel.tcp.interceptors.SocketChannelInterceptor;
import com.bocom.jump.bp.core.ContextEx;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.support.TRACER;
import com.bocom.jump.bp.util.Hex;

public class PayloadChannelInterceptorTbc extends PayloadChannelInterceptor
		implements SocketChannelInterceptor
{
	@Autowired
	GdTbcBasInfRepository gdTbcBasInfRepository;
	
	private Logger log = LoggerFactory.getLogger(PayloadChannelInterceptorTbc.class);

	public PayloadChannelInterceptorTbc()
	{
	}

	public void onRequest(ChannelContext channelContext, ContextEx context)
			throws JumpException
	{
		log.info("收到烟草的请求!..");
		Socket socket = (Socket) channelContext.getRequest();
		try
		{
			// 获取初始报文
			byte[] inB = resolve(socket.getInputStream());
			int inL = inB.length;
			log.info("收到的初始报文为:\n" + Hex.toDumpString(inB));

			byte[] realB = new byte[inL];

			// 获取头消息
			byte[] frtB = new byte[15];
			System.arraycopy(inB, 0, frtB, 0, 15);
			System.arraycopy(inB, 0, realB, 0, 15);
			
			// 获取交易码
			String tbcTransCode = new String(frtB);
			String dptId = tbcTransCode.substring(11); //截取烟草单位编码
			String decKey = getKeyFromDB(dptId);  // 获取通讯密钥
			tbcTransCode = tbcTransCode.substring(7, 11);

			// 获取加密体
			byte[] desBody = new byte[inL - 18]; // 长度为总报文-15位头，-3位尾
			System.arraycopy(inB, 15, desBody, 0, desBody.length);

			log.info("收到的加密报文体为\n:" + Hex.toDumpString(desBody));

			// 初始秘钥报文解密:
			if ("8910".equals(tbcTransCode) || "8888".equals(tbcTransCode) || "8918".equals(tbcTransCode)) {
				log.info("当前的烟草交易码为:[" + tbcTransCode + "],使用初始秘钥进行解密！..");
				byte[] relBody = trbDes("1111111111111111", desBody, "1"); // 使用初始秘钥解密
				System.arraycopy(relBody, 0, realB, 15, inL - 18); // 复制明文,去除后三位报文尾，前十五位报文头
			}
			else{ // 通讯密钥处理
				log.info("当前的烟草交易码为:[" + tbcTransCode + "],使用通讯密钥进行解密！..");
				byte[] relBody = trbDes(decKey, desBody, "1"); // 使用通讯密钥解密
				System.arraycopy(relBody, 0, realB, 15, inL - 18); // 复制明文,去除后三位报文尾，前十五位报文头
			}
			
			System.arraycopy(inB, inL - 3, realB, inL - 3, 3); // 复制明文,去除后三位报文尾，前十五位报文头

			log.info("最终传给context的字节为:" + Hex.toDumpString(realB));
			channelContext.setRequestPayload(realB);
		}
		catch (IOException ioexception)
		{
			throw new JumpException("JUMPTP8000", "socket_read_error", ioexception);
		}
	}

	public void onResponse(ChannelContext channelContext, ContextEx context, Throwable throwable)
	{
		byte[] abyte0 = (byte[]) channelContext.getResponsePalyload();
		
					//报文头长度15，报文结尾长度3
					int headLength = 15, lastLength = 3;
					//明文字节数
					byte[] inBody = abyte0;
					//明文字节数总长度
					int inLength = inBody.length;
					log.info("context返回的报文为:\n" + Hex.toDumpString(inBody));
					//返回加密后报文长度
					int encodeBodyLength = 0;
					//返回加密后报文长度
					byte[] encodeBody = null;

					// 获取报文头
					byte[] frontBody = new byte[headLength];
					System.arraycopy(inBody, 0, frontBody, 0, headLength);

					// 获取交易码
					String tbcTransCode = new String(frontBody);
					String dptId = tbcTransCode.substring(11); //截取烟草单位编码
					String decKey = null; 
					try {
						decKey = getKeyFromDB(dptId); //获取通讯密钥
					} catch (CoreException e) {
						log.info("~~~~~~~~~~~~ 获取通讯密钥出错，" + e);
					}
					tbcTransCode = tbcTransCode.substring(7, 11);

					// 获取明文中需要加密的部分
					byte[] srcBody = new byte[inLength - headLength];
					System.arraycopy(inBody, 15, srcBody, 0, srcBody.length);

					log.info("待加密的初始报文体为\n:" + Hex.toDumpString(srcBody));

					// 初始报文加
					byte[] destBody = null;
					if ("8910".equals(tbcTransCode) || "8888".equals(tbcTransCode) || "8918".equals(tbcTransCode)) {
						log.info("当前的烟草交易码为:[" + tbcTransCode + "],使用初始秘钥进行加密！..");
						destBody = trbDes("1111111111111111", srcBody, "0"); // 使用初始秘钥加密
						log.info("加密后的报文尝试解密\n:" + Hex.toDumpString(trbDes("1111111111111111", destBody, "1")));
					}else{
						log.info("当前的烟草交易码为:[" + tbcTransCode + "],使用通讯密钥进行加密！..");
						destBody = trbDes(decKey, srcBody, "0"); // 使用通讯密钥加密
						log.info("加密后的报文尝试解密\n:" + Hex.toDumpString(trbDes("1111111111111111", destBody, "1")));
					}
					encodeBodyLength += headLength;
					encodeBodyLength += destBody.length;
					encodeBodyLength += lastLength;
					//由于原报文长度没有计算加密后的长度，需要重新计算报文长度
					int totalBodyLength = 0;
					totalBodyLength += encodeBodyLength - 4;
					log.info("加密后报文长度为\n:" + String.format("%04d", totalBodyLength));
					encodeBody = new byte[encodeBodyLength];
					System.arraycopy(inBody, 0, encodeBody, 0, headLength); // 复制明文,去除后三位报文尾，前十五位报文头
					System.arraycopy(String.format("%04d", totalBodyLength).getBytes(), 0, encodeBody, 0, 4); // 更正加密后的报文长度
					System.arraycopy(destBody, 0, encodeBody, 15, destBody.length); // 复制明文,去除后三位报文尾，前十五位报文头
					System.arraycopy("***".getBytes(), 0, encodeBody, headLength+destBody.length, lastLength); // 复制明文,去除后三位报文尾，前十五位报文头
					log.info("最终返回给第三方的字节为:" + Hex.toDumpString(encodeBody));
		try
		{
			((Socket) channelContext.getResponse()).getOutputStream().write(encodeBody);
		} catch (IOException ioexception)
		{
			TRACER.trace("write socket error", ioexception);
		}
	}

	/**
	 * 加解密处理
	 * 
	 * @param desKey
	 * @param desBody
	 * @param desFlg
	 */
	private byte[] trbDes(String desKey, byte[] desBody, String desFlg) {

    	String desKeyL=desKey.substring(0,8);
    	String keyL=Hex.encode(desKeyL.getBytes());
    	
    	String desKeyR=desKey.substring(8,16);
    	String keyR=Hex.encode(desKeyR.getBytes());

		log.info("加密key=" + desKey);
		
		byte[] msg = desBody;
		int i = msg.length / 8;
		byte[] newMsg = null;
		if (msg.length % 8 != 0) {
			newMsg = new byte[(i + 1) * 8];
			System.arraycopy(msg, 0, newMsg, 0, msg.length);
			int j = msg.length;
			while (j % 8 != 0) {
				newMsg[j] = 0x00;
				j++;
			}
		} else {
			newMsg = msg;
		}
		log.info("补齐位数后的待处理报文为:\n[" + Hex.toDumpString(newMsg) + "]");
		byte[] result = null;

		if ("0".equals(desFlg)) { // 加密
			
			String bb=CryptoUtils.desEncrpty(Hex.encode(newMsg), keyL);
			
			String cc=CryptoUtils.desDecrypt(bb, keyR);
			
			String dd=CryptoUtils.desEncrpty(cc, keyL);
			result=Hex.decode(dd);
			log.info("最终的加密结果为:"+Hex.toDumpString(result));
			
		}
		else if ("1".equals(desFlg)) { // 解密
			
			String bb=CryptoUtils.desDecrypt(Hex.encode(newMsg), keyL);
			
			String cc=CryptoUtils.desEncrpty(bb, keyR);
			
			String dd=CryptoUtils.desDecrypt(cc, keyL);
			result=Hex.decode(dd);
			log.info("最终的解密结果为:"+new String(result));
		}
		return result;
	}
	/**
	 * 1.根据报文头后4位（单位编码 dptId ）查表 GdTbcBasInf  ，用findOne(dptId)
	 * 2.取rsvFld1 即为通讯密钥
	 * 3.异常控制
	 */
	private String getKeyFromDB(String dptId) throws CoreException{
		log.info("~~~~~~~~~~~~ getKeyFromDB started...");
		GdTbcBasInf basInf = gdTbcBasInfRepository.findOne(dptId);
		String key = basInf.getRsvFld1();
		if(StringUtils.isEmpty(key)){
			throw new CoreException("~~~~~~~~~ 通讯密钥为空 ~~~~~~~~");
		}
		key = key.toString().trim();
		log.info("~~~~~~~~~~~~ the key from DB is : " + key);
		return key;
	}
}

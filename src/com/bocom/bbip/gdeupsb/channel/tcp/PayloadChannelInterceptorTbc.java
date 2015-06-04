package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.IOException;
import java.net.Socket;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.system;

import com.bocom.bbip.utils.CryptoUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.ChannelContext;
import com.bocom.jump.bp.channel.tcp.interceptors.PayloadChannelInterceptor;
import com.bocom.jump.bp.channel.tcp.interceptors.SocketChannelInterceptor;
import com.bocom.jump.bp.core.ContextEx;
import com.bocom.jump.bp.support.TRACER;
import com.bocom.jump.bp.util.Hex;

public class PayloadChannelInterceptorTbc extends PayloadChannelInterceptor
		implements SocketChannelInterceptor
{
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
			tbcTransCode = tbcTransCode.substring(7, 11);

			// 获取加密体
			byte[] desBody = new byte[inL - 18]; // 长度为总报文-15位头，-3位尾
			System.arraycopy(inB, 15, desBody, 0, desBody.length);

			log.info("收到的加密报文体为\n:" + Hex.toDumpString(desBody));

			// 初始报文解密:
			if ("8910".equals(tbcTransCode) || "8888".equals(tbcTransCode) || "8918".equals(tbcTransCode)) {
				log.info("当前的烟草交易码为:[" + tbcTransCode + "],使用初始秘钥进行解密！..");
				byte[] relBody = trbDes("1111111111111111", desBody, "1"); // 使用初始秘钥解密
				
				System.arraycopy(relBody, 0, realB, 15, inL - 18); // 复制明文,去除后三位报文尾，前十五位报文头
			}
			System.arraycopy(inB, inL - 3, realB, inL - 3, 3); // 复制明文,去除后三位报文尾，前十五位报文头

			log.info("最终传给context的字节为:" + Hex.toDumpString(realB));
			channelContext.setRequestPayload(realB);
		} catch (IOException ioexception)
		{
			throw new JumpException("JUMPTP8000", "socket_read_error", ioexception);
		}
	}

	public void onResponse(ChannelContext channelContext, ContextEx context, Throwable throwable)
	{
		byte[] abyte0 = (byte[]) channelContext.getResponsePalyload();
		
					byte[] inB = abyte0;
					int inL = inB.length;

					byte[] realB = new byte[inL];

					// 获取头消息
					byte[] frtB = new byte[15];
					System.arraycopy(inB, 0, frtB, 0, 15);
					System.arraycopy(inB, 0, realB, 0, 15);

					// 获取交易码
					String tbcTransCode = new String(frtB);
					tbcTransCode = tbcTransCode.substring(7, 11);

					// 获取加密体
					byte[] desBody = new byte[inL - 18]; // 长度为总报文-15位头，-3位尾
					System.arraycopy(inB, 15, desBody, 0, desBody.length);

					log.info("context返回的报文为:\n" + Hex.toDumpString(inB) + "\n" + ",待加密的初始报文体为\n:" + Hex.toDumpString(desBody));

					// 初始报文加:
					if ("8910".equals(tbcTransCode) || "8888".equals(tbcTransCode) || "8918".equals(tbcTransCode)) {
						log.info("当前的烟草交易码为:[" + tbcTransCode + "],使用初始秘钥进行加密！..");
						byte[] relBody = trbDes("1111111111111111", desBody, "0"); // 使用初始秘钥加密
						
						System.arraycopy(relBody, 0, realB, 15, inL - 18); // 复制明文,去除后三位报文尾，前十五位报文头
					}
					System.arraycopy(inB, inL - 3, realB, inL - 3, 3); // 复制明文,去除后三位报文尾，前十五位报文头

					log.info("最终返回给第三方的字节为:" + Hex.toDumpString(realB));
		try
		{
			((Socket) channelContext.getResponse()).getOutputStream().write(realB);
		} catch (IOException ioexception)
		{
			TRACER.trace("write socket error", ioexception);
		}
	}

	// 加解密方法:
	private static final String Algorithm = "DESede"; // 定义加密算法,可用
														// DES,DESede,Blowfish

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

	// keybyte为加密密钥，长度为24字节
	// src为被加密的数据缓冲区（源）
	public static byte[] encryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			// 加密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);// 在单一方面的加密或解密
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	// keybyte为加密密钥，长度为24字节
	// src为加密后的缓冲区
	public static byte[] decryptMode(byte[] keybyte, byte[] src) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
			// 解密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}
	
}

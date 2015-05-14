package com.bocom.bbip.gdeupsb.utils;

import java.util.Arrays;

/**
 * @categoryMac工具类，采用ECB算法
 * @author sunbg
 * 
 */
public class MacEcbUtils {
	public static byte[] IV = new byte[8];

	public static byte byteXOR(byte src, byte src1) {
		return (byte) ((src & 0xFF) ^ (src1 & 0xFF));
	}

	public static byte[] bytesXOR(byte[] src, byte[] src1) {
		int length = src.length;
		if (length != src1.length) {
			return null;
		}
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = byteXOR(src[i], src1[i]);
		}
		return result;
	}

	/**
	 * mac计算,数据不为8的倍数，需要补0，将数据8个字节进行异或，再将异或的结果与下一个8个字节异或，一直到最后，将异或后的数据进行DES计算
	 * 
	 * @param key
	 * @param Input
	 * @return
	 * @throws Exception
	 */
	public static byte[] clacMac(byte[] key, byte[] Input) throws Exception {
		int length = Input.length;
		int x = length % 8;
		int addLen = 0;
		if (x != 0) {
			addLen = 8 - length % 8;
		}
		int pos = 0;
		byte[] data = new byte[length + addLen];
		System.arraycopy(Input, 0, data, 0, length);
		byte[] oper1 = new byte[8];
		System.arraycopy(data, pos, oper1, 0, 8);
		pos += 8;
		for (int i = 1; i < data.length / 8; i++) {
			byte[] oper2 = new byte[8];
			System.arraycopy(data, pos, oper2, 0, 8);
			byte[] t = bytesXOR(oper1, oper2);
			oper1 = t;
			pos += 8;
		}
		byte[] buff = DesUtil.encrypt(oper1, key);
		return buff;
	}

	/**
	 * 获取16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < b.length; i++) {
			tmp = (Integer.toHexString(b[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}

	/**
	 * 
	 * 2次加密后gbk显示字符串 0 mac 2次加密返回的16进制字符串 1
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String[] twoEcbMacHex(String str, String charset)
			throws Exception {
		byte[] bs1 = clacMac(("12345678").getBytes(), "12345678".getBytes());
		byte[] bs = clacMac(bs1, str.getBytes(charset));
		byte[] retBuf = new byte[8];
		System.arraycopy(bs, 0, retBuf, 0, 8);
		String[] bbs = new String[2];
		// bbs[0] = new String(retBuf ,"ISO-8859-1");
		// bbs[0] = new String(retBuf ,charset);
		bbs[1] = byte2hex(retBuf);
		return bbs;
	}

	/**
	 * 加密为8个字节
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static byte[] twoEcbMacByte(String str) throws Exception {
		byte[] bs1 = clacMac(("12345678").getBytes(), "12345678".getBytes());
		byte[] bs = clacMac(bs1, str.getBytes("GBK"));
		byte[] retBuf = new byte[8];
		System.arraycopy(bs, 0, retBuf, 0, 8);
		return retBuf;
	}
	
	/**
	 * 佛山加密 密钥 随机数
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static byte[] twoEcbMacByteFS(String str) throws Exception{
		byte[] bs1 = clacMac(("jt3vr67d").getBytes(), "swv3n321".getBytes());
//		byte[] bs1 = clacMac(("swv3n321").getBytes(), "jt3vr67d".getBytes());
		byte[] bs = clacMac(bs1, str.getBytes("GBK"));
		byte[] retBuf = new byte[8];
		System.arraycopy(bs, 0, retBuf, 0, 8);
		return retBuf;
	}
	/**
	 * 
	 * 2次加密后gbk显示字符串 0 mac 2次加密返回的16进制字符串 1
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String[] twoEcbMacHexNoCharset(String str) throws Exception {
		byte[] bs1 = clacMac(("12345678").getBytes(), "12345678".getBytes());
		byte[] bs = clacMac(bs1, str.getBytes());
		byte[] retBuf = new byte[8];
		System.arraycopy(bs, 0, retBuf, 0, 8);
		String[] bbs = new String[2];
		// bbs[0] = new String(retBuf ,"ISO-8859-1");
		bbs[0] = new String(retBuf);
		bbs[1] = byte2hex(retBuf);
		return bbs;
	}

	/**
	 * 
	 * 1次加密后gbk显示字符串 0 mac 1次加密返回的16进制字符串 1
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String[] oneEcbMacHex(String str, String charset)
			throws Exception {
		byte[] bs = clacMac(("12345678").getBytes(), str.getBytes(charset));
		byte[] retBuf = new byte[8];
		System.arraycopy(bs, 0, retBuf, 0, 8);
		String[] bbs = new String[2];
		bbs[0] = new String(retBuf, charset);
		bbs[1] = byte2hex(retBuf);
		return bbs;
	}

	/**
	 * 省行移动mac加密
	 * 
	 * @param sbody
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static String[] getGdmobbMac(String sbody, String charset)
			throws Exception {
		String key = "32466278";
		byte[] m = { 0, 0, 0, 0, 0, 0, 0, 0 };
		byte[] bbody = sbody.getBytes(charset);
		int length = bbody.length;
		for (int i = 0; i < length;) {
			m[i & 7] ^= bbody[i];
			i++;
			if ((i & 7) == 0 || i == length) {
				byte[] bs = MacEcbUtils.clacMac(key.getBytes(), m);
				System.arraycopy(bs, 0, m, 0, 8);
			}
		}
		byte[] retBuf = new byte[4];
		System.arraycopy(m, 0, retBuf, 0, 4);
		String[] strs = new String[2];
		strs[0] = new String(m, charset);
		strs[1] = MacEcbUtils.byte2hex(retBuf).toUpperCase();
		return strs;
	}

	/**
	 * 转化字符串为十六进制编码
	 * @param s
	 * @return
	 */
	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}

	public static void main(String[] args) throws Exception {
		String str = "Q101005030400006329201503041006412015030424    0090W0DK115113381029   ";
		// byte[] bs1 = clacMac(("12345678").getBytes(),"12345678".getBytes());
		// //返回的数据
		// byte[] bs = clacMac(bs1,str.getBytes());
		// // 取8个长度字节
		// byte[] retBuf = new byte[8];
		// System.arraycopy(bs, 0, retBuf, 0, 8);
		// for(int i=0;i<bs.length;i++){
		// System.out.print((char)bs[i]);
		// }
		// System.out.println(new String(bs,"GBK"));
		// System.out.println(Arrays.toString(oneEcbMacHex(str)));
	}
}

package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Gateway;

/**
 * 珠海路桥外发
 * <br/>外发报文前6位为交易码,需要在报文中拼好
 * @author Liu_gz
 * @version 1.0.0,2015-3-25
 */
public class ZhLqSocketGateway implements Gateway {
	
	public static Logger log = LoggerFactory.getLogger(ZhLqSocketGateway.class);
	
	private String host;
	private int port;
	private int timeout;
	
	String wjjsBz = "WJJSWJJSWJJSWJJSWJJSWJJSWJJSWJ"; //文件结束标志
	String sucBz = "AGR"; //成功回执

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public Object receive(Object arg0, String arg1)
			throws CommunicationException {
		return null;
	}

	public Object send(Object arg0, String arg1) throws CommunicationException {
		return null;
	}

	public Object sendAndReceive(Object arg0, String arg1)
			throws CommunicationException {
		byte[] req = null;
		if (arg0 instanceof byte[]) {
			req = (byte[]) arg0;
			log.info("req msg = "+new String(req));
		} else {
			throw new CommunicationException(false, "req msg type is error");
		}
		byte[] tb = new byte[6];
		System.arraycopy(req, 0, tb, 0, tb.length);
		String txncod = new String(tb);
		log.info("txncod = "+ txncod);
		
		byte[] newreq = new byte[req.length-6];
		System.arraycopy(req, 6, newreq, 0, newreq.length);
		
		Socket socket = newSocket();
		DataInputStream is = null;
		OutputStream os = null;
		byte[] rsp = null;
		try {
			is = new DataInputStream(socket.getInputStream());
			os = socket.getOutputStream();
			exChange(is,os,txncod);
			String len = StringUtils.leftPad(String.valueOf(newreq.length), 4, "0");
			os.write(len.getBytes()); //外发报文长度
			os.write(newreq); //外发请求报文
			log.info("send msg="+len+new String(newreq, "GBK"));
			String s4 = recvThree(is, 4);
			if (!s4.equals("AGR")) {
				throw new IOException("third 4 return error !");
			}
			
			os.write(wjjsBz.getBytes()); //发送文件结束标志
			log.info("send wjbz ok");
			
			byte[] rcvLen = new byte[4];
			is.readFully(rcvLen);
			int rlen = Integer.parseInt(new String(rcvLen).trim());
			rsp = new byte[rlen];
			is.readFully(rsp);
			log.info("recv msg="+new String(rsp, "GBK"));
			os.write(sucBz.getBytes());
			
			byte[] rcvbz = new byte[30];
			is.readFully(rcvbz);
			log.info("recv wjbz ok");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("交易外发失败,err msg :"+e.getMessage());
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (os != null) {
					os.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e2) {
				
			}
		}
		
		byte[] allrsp = new byte[tb.length+rsp.length];
		System.arraycopy(tb, 0, allrsp, 0, tb.length);
		System.arraycopy(rsp, 0, allrsp, 6, rsp.length);
		log.info("rsp msg="+new String(allrsp));
		return allrsp;
	}
	
	/**
	 * 发报文前处理
	 * @param is
	 * @param os
	 * @param txncod
	 * @throws IOException
	 */
	private void exChange(DataInputStream is, OutputStream os, String txncod) throws IOException {
		String str = recvThree(is, 0);
		if (!str.equals("AGR")) {
			throw new IOException("third 0 return error !");
		}
		
		String exStr = "VER2.0  ";
		os.write(exStr.getBytes());
		log.info("send msg1:"+exStr);
		String s = recvThree(is, 1);
		if (!s.equals("2.0")) {
			throw new IOException("third 1 return error !");
		}
		
		exStr="000000000"+txncod;
		os.write(exStr.getBytes());
		log.info("send msg2:"+exStr);
		str = recvThree(is, 2);
		if (!str.equals("AGR")) {
			throw new IOException("third 2 return error !");
		}
		
		if (s.equals("2.0")) {
			byte[] b2 = new byte[8];
			is.readFully(b2);
			log.info("version 2.0 recv msg:"+new String(b2));
		}
	}
	
	private String recvThree(DataInputStream is,int idx) throws IOException {
		byte[] b1 = new byte[3];
		is.readFully(b1);
		String str1 = new String(b1);
		log.info("recv return str"+idx+" = "+str1);
		return str1;
	}

	private Socket newSocket() throws CommunicationException {
		try {
			Socket socket = new Socket();
			log.info("conn host=" + host + "  port=" + port);

			InetSocketAddress inetsocketaddress = new InetSocketAddress(host, port);
			socket.connect(inetsocketaddress);
			if (timeout > 0) {
				socket.setSoTimeout(timeout * 1000);
			}
			log.info("conn ok");
			return socket;
		} catch (IOException ioexception) {
			throw new CommunicationException(false, "cannot_new_socket", ioexception);
		}
	}
}

package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			os.write(newreq);
			while (true) {
				if (is.available() > 0) {
					byte[] rb = new byte[2048];
					int i = is.read(rb);
					rsp = new byte[i];
					System.arraycopy(rb, 0, rsp, 0, rsp.length);
					log.info("recv msg="+new String(rsp));
					break;
				}
			}
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
		recvThree(is, 0);
		
		String exStr = "VER2.0  ";
		os.write(exStr.getBytes());
		log.info("send msg1:"+exStr);
		recvThree(is, 1);
		
		exStr="000000000"+txncod;
		os.write(exStr.getBytes());
		log.info("send msg2:"+exStr);
		String s = recvThree(is, 2);
		
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
		if (!str1.equals("AGR")||!str1.equals("2.0")) {
			throw new IOException("third cann't allow conn !");
		}
		return str1;
	}

	private Socket newSocket() throws CommunicationException {
		try {
			Socket socket = new Socket();
			log.info("conn host=" + host + "  port=" + port);

			InetSocketAddress inetsocketaddress = new InetSocketAddress(host, port);
			socket.connect(inetsocketaddress);
			socket.setSoTimeout(timeout * 1000);
			log.info("conn ok");
			return socket;
		} catch (IOException ioexception) {
			throw new CommunicationException(false, "cannot_new_socket", ioexception);
		}
	}
}

package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
	private String filePath;
	
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
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
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
			recvThree(is, 3);
			os.write(wjjsBz.getBytes()); //发送文件结束标志
			log.info("send wjbz ok");
			
			/** 开始接收 **/
			byte[] rcvLen = new byte[4]; //接收前4位
			is.readFully(rcvLen);
			int rlen = Integer.parseInt(new String(rcvLen).trim());
			rsp = new byte[rlen];//接收报文
			is.readFully(rsp);
			log.info("recv msg="+new String(rsp));
			if ("GetChk".equals(txncod)) { //接收对账文件
				if (!new String(rsp).equals("000|")) {
					os.write("REJ".getBytes());
					return (new String(rsp)+"|").getBytes();
				} else {
					os.write(sucBz.getBytes());
				}
				byte[] fb = new byte[30];
				is.readFully(fb);
				String filNam = new String(fb,"GBK").trim();
				int i = StringUtils.indexOf(filNam, "AGR");
				filNam = filNam.substring(0, i);
				log.info("read file name="+filNam);
				File file = new File(filePath+File.separator+filNam);
				FileOutputStream fos = new FileOutputStream(file);
				while (true) {
					byte[] wjLen = new byte[4]; //接收前4位
					is.readFully(wjLen);
					if (new String(wjLen).equals("0000")) {
						byte[] wjjs = new byte[30];
						is.readFully(wjjs);
						fos.close();
						log.info("recv file suc!");
						rsp = ("000000|"+filNam+"|").getBytes();
						break;
					} else {
						int wjLeng = Integer.parseInt(new String(wjLen).trim());
						log.info("recv file len="+wjLeng);
						byte[] wjb = new byte[wjLeng];
						is.readFully(wjb);
						os.write(sucBz.getBytes());
						log.info("recv file msg="+new String(wjb, "GBK"));
						fos.write(wjb);
					}
				}
			} else {
				log.info("recv msg="+new String(rsp, "GBK"));
				os.write(sucBz.getBytes());
				
				byte[] rcvbz = new byte[30];
				is.readFully(rcvbz);
				log.info("recv wjbz ok");
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
		recvThree(is, 0);
		
		String exStr = "VER2.0  ";
		os.write(exStr.getBytes());
		log.info("send msg1:"+exStr);
		String str2 = recvThree(is, 1);
		
		exStr="000000000"+txncod;
		os.write(exStr.getBytes());
		log.info("send msg2:"+exStr);
		recvThree(is, 2);
		
		if ("2.0".equals(str2)) {
			byte[] b2 = new byte[8];
			is.readFully(b2);
			log.info("version 2.0 recv msg:"+new String(b2));
		}
	}
	
	private String recvThree(DataInputStream is,int idx) throws IOException {
		byte[] b = new byte[3];
		is.readFully(b);
		String str = new String(b);
		log.info("recv return str"+idx+" = "+str);
		String sh3 = str.substring(0, 3);
		if (!"AGR".equals(sh3)&&!"2.0".equals(sh3)) {
			throw new IOException("third "+idx+" return error !");
		}
		return str;
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

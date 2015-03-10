package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.adaptor.channel.EupsGateway;
import com.bocom.jump.bp.JumpRuntimeException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.tcp.SocketGateway;
import com.bocom.jump.bp.channel.tcp.StreamResolver;

/**
 * 无前置网关
 * @author hefengwen
 *
 */
public class GdLotSocketTransCodGateway extends SocketGateway{
	
	private static Logger logger = LoggerFactory.getLogger(GdLotSocketTransCodGateway.class);

	/**主机*/
	private String host;
	/**端口*/
	private int port;
	/**连接超时时间*/
	private int connectTimeout;
	/**重用地址*/
	private boolean reuseAddress;
	/**接收缓冲区大小*/
	private int receiveBufferSize;
	/**发送缓冲区大小*/
	private int sendBufferSize;
	/**超时时间*/
	private int soLinger = -1;
	/**超时时间*/
	private int soTimeout;
	/**字节流解析*/
	private StreamResolver streamResolver;

	public GdLotSocketTransCodGateway() {
//		this.localFile = new File(System.getProperty("user.home")+"/packets");
//		if(!(this.localFile.exists())){
//			this.localFile.mkdirs();
//		}
	}

	public void setHost(String host) {
		this.host = host;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}
	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}
	public void setSoLinger(int soLinger) {
		this.soLinger = soLinger;
	}
	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}
	public void setStreamResolver(StreamResolver streamResolver) {
		this.streamResolver = streamResolver;
	}
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}
	
	public Object receive(Object obj,String str) throws CommunicationException{
		throw new UnsupportedOperationException();
	}
	
//	public void saveLocalFile(byte[] array,String filePath,int sequenceNo,boolean isSaveFile){
//		StringBuffer buffer = new StringBuffer();
//		if((filePath!=null)&&(filePath.length()>0)){
//			buffer.append(filePath).append('#');
//		}
//		if(isSaveFile){
//			buffer.append(sequenceNo).append(".rcv");
//		}else{
//			buffer.append(sequenceNo).append(".snd");
//		}
//		FileOutputStream out = null;
//		try {
//			out = new FileOutputStream(new File(this.localFile,buffer.toString()));
//			out.write(array);
//			out.flush();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally{
//			if(out!=null){
//				try {
//					out.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//	/**发送*/
//	@Override
//	public Object send(Object obj, String filePath) throws CommunicationException {
//		byte[] array = (byte[])obj;
//		Socket localSocket = createSocket();
//		try {
//			localSocket.getOutputStream().write(array);
//			localSocket.getOutputStream().flush();
//			localSocket.shutdownOutput();
//		} catch (Exception e) {
//			throw new CommunicationException(false,"write socket error",e);
//		}finally{
//			new EupsGateway().close(localSocket);
//		}
//		return null;
//	}
	
	/**发送或接收报文*/
	@Override
	public Object sendAndReceive(Object obj, String ttxnCd) throws CommunicationException {
		logger.info("sendAndReceive start ... ...");
		byte[] sendBuffer = (byte[])obj;
		Socket socket = createSocket();
		try {
			String sndData = new String(sendBuffer,"GBK");
			//去掉root节点
//			sndData = sndData.replaceAll("<root>", "").replaceAll("</root>", "");
			logger.info("sndData:["+sndData+"]");
			
			socket.getOutputStream().write(sndData.getBytes("GBK"));
			socket.getOutputStream().flush();
			socket.shutdownOutput();
			InputStream in = socket.getInputStream();
//			byte[] rcvBuffer = new byte[in.available()];
//			in.read(rcvBuffer);
			byte[] rcvBuffer = streamResolver.resolve(in);
			String rcvData = new String(rcvBuffer,"GBK");
			logger.info("rcvData:["+rcvData+"]");
			//添加root节点
//			rcvData = "<root>"+rcvData+"</root>";
			return rcvData.getBytes("GBK");
		} catch (JumpRuntimeException jumpe) {
			Throwable localThrowable = jumpe.getCause();
			if(localThrowable instanceof SocketTimeoutException){
				throw new CommunicationException(false,"socket read error",jumpe);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			new EupsGateway().close(socket);
		}
		return null;
	}
	
	/**新建socket套接字*/
	protected Socket createSocket() throws CommunicationException{
		Socket socket = null;
		try {
			socket = new Socket();
			config(socket);
			InetSocketAddress addr = new InetSocketAddress(this.host,this.port);
			if(this.connectTimeout>0){
				socket.connect(addr,this.connectTimeout);
			}else{
				socket.connect(addr);
			}
			return socket;
		} catch (Exception e) {
			if(socket!=null){
				new EupsGateway().close(socket);
			}
			throw new CommunicationException(false,"cannot_new_socket");
		}
	}
	
	protected void config(Socket paramSocket) throws SocketException{
		if(this.receiveBufferSize>0){
			paramSocket.setReceiveBufferSize(this.receiveBufferSize);
		}
		if(this.sendBufferSize>0){
			paramSocket.setSendBufferSize(this.sendBufferSize);
		}
		if(this.soLinger>=0){
			paramSocket.setSoLinger(true, this.soLinger);
		}
		if(this.soTimeout>0){
			paramSocket.setSoTimeout(this.soTimeout);
		}
	}
	
}

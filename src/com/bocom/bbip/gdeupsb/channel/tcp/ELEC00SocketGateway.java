package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.adaptor.channel.EupsGateway;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.JumpRuntimeException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.tcp.SocketGateway;
import com.bocom.jump.bp.channel.tcp.StreamResolver;
import com.bocom.jump.bp.util.Hex;

/**
 * 无前置网关
 * @author hefengwen
 *
 */
public class ELEC00SocketGateway extends SocketGateway{
	
	private static Logger logger = LoggerFactory.getLogger(ELEC00SocketGateway.class);

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

	public ELEC00SocketGateway() {
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
	
	/**发送或接收报文*/
	@Override
	public Object sendAndReceive(Object obj, String ttxnCd) throws CommunicationException {
		
    	if (this.streamResolver == null) {
            throw new IllegalArgumentException("no stream resolver defined");
        }
        byte[] sendBuffer = (byte[]) obj;
        
        int len=sendBuffer.length-10;
        String lenS=StringUtils.rightPad(String.valueOf(len), 10, " ");
        System.arraycopy(lenS.getBytes(), 0, sendBuffer, 0, 10);
        System.out.println("处理后，byte=\n"+Hex.toDumpString(sendBuffer)+",发送的报文转化为明文为:"+new String(sendBuffer));

        Socket socket = createSocket();
        try {
            socket.getOutputStream().write(sendBuffer);
            socket.getOutputStream().flush();
            // 关闭上行流
            ElecFrontStreamResolver streamResolver=new ElecFrontStreamResolver();
            byte[] receiveBuffer = streamResolver.resolve(socket.getInputStream());
            socket.shutdownOutput();

            return receiveBuffer;
        } catch (IOException io) {

        } catch (JumpRuntimeException jump) {
            Throwable localThrowable = jump.getCause();
            if (localThrowable instanceof SocketTimeoutException) {
                try {
					throw new CommunicationException(false, "socket read error", jump);
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        } finally {
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
	
//	public static void main(String[] args) {
//		  byte[] sendBuffer = new byte[]{0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x31,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x30,0x31,0x30};
//	        System.out.println("----"+new String(sendBuffer));
//	        int len=sendBuffer.length-10;
//	        System.out.println(len);
//	        String lenS=StringUtils.rightPad(String.valueOf(len), 10, " ");
//	        System.out.println(lenS);
//	        System.arraycopy(lenS.getBytes(), 0, sendBuffer, 0, 10);
//	        System.out.println("hahahhaa:["+new String(sendBuffer)+"]");
//	        System.out.println("处理后，byte=\n"+Hex.toDumpString(sendBuffer));
//	}
}

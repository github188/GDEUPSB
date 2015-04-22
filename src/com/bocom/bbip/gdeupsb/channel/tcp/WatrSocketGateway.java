package com.bocom.bbip.gdeupsb.channel.tcp;

import com.bocom.bbip.eups.adaptor.exception.ConnectionException;
import com.bocom.bbip.eups.adaptor.exception.SocketReadException;
import com.bocom.bbip.eups.adaptor.exception.SocketTimeOutException;
import com.bocom.bbip.eups.adaptor.exception.SocketWriteException;
import com.bocom.bbip.utils.CryptoUtils;
import com.bocom.jump.bp.JumpRuntimeException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Gateway;
import com.bocom.jump.bp.channel.tcp.LengthStreamResolver;
import com.bocom.jump.bp.util.Hex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 特殊账务类Gateway
 * <p>
 * 
 * @version 1.0.0,2014-3-24
 * @author qc.w
 * @since 1.0.0
 */
public class WatrSocketGateway implements Gateway<Object, Object> {

	private static final Log log = LogFactory.getLog(WatrSocketGateway.class);
	private String a;

	private int b;

	private int c;

	private boolean d;

	private int e;

	private int f;

	private int g = -1;

	private int soTimeout;

	private LengthStreamResolver streamResolver;

	private File j;

	private AtomicInteger k = new AtomicInteger();

	private boolean booleanFalse = false;

	public WatrSocketGateway() {
		this.j = new File(System.getProperty("user.home") + "/packets");
		if (!this.j.exists())
			this.j.mkdirs();
	}

	public void setHost(String paramString) {
		this.a = paramString;
	}

	public void setPort(int paramInt) {
		this.b = paramInt;
	}

	public void setReceiveBufferSize(int paramInt) {
		this.e = paramInt;
	}

	public void setSendBufferSize(int paramInt) {
		this.f = paramInt;
	}

	public void setSoLinger(int paramInt) {
		this.g = paramInt;
	}

	public void setSoTimeout(int paramInt) {
		this.soTimeout = paramInt;
	}

	public void setConnectTimeout(int paramInt) {
		this.c = paramInt;
	}

	public void setReuseAddress(boolean paramBoolean) {
		this.d = paramBoolean;
	}

	public void setStreamResolver(LengthStreamResolver paramStreamResolver) {
		this.streamResolver = paramStreamResolver;
	}

	public Object receive(Object paramObject, String paramString) throws CommunicationException {
		throw new UnsupportedOperationException();
	}

	void a(byte[] paramArrayOfByte, String paramString, int paramInt, boolean paramBoolean) {
		StringBuffer localStringBuffer = new StringBuffer();
		if ((paramString != null) && (paramString.length() > 0))
			localStringBuffer.append(paramString).append('#');
		if (paramBoolean)
			localStringBuffer.append(paramInt).append(".rcv");
		else {
			localStringBuffer.append(paramInt).append(".snd");
		}

		FileOutputStream localFileOutputStream = null;
		try {
			localFileOutputStream = new FileOutputStream(new File(this.j, localStringBuffer.toString()));
			localFileOutputStream.write(paramArrayOfByte);
			localFileOutputStream.flush();
		} catch (Exception localException1) {
			localException1.printStackTrace();

			if (localFileOutputStream != null)
				try {
					localFileOutputStream.close();
				} catch (Exception localException2) {
				}
		} finally {
			if (localFileOutputStream != null)
				try {
					localFileOutputStream.close();
				} catch (Exception localException3) {
				}
		}
	}

	public Object send(Object paramObject, String paramString) throws CommunicationException {
		byte[] arrayOfByte = (byte[]) paramObject;
		if (this.booleanFalse) {
			a(arrayOfByte, paramString, this.k.getAndIncrement(), false);
		}

		Socket localSocket = newSocket();
		try {
			localSocket.getOutputStream().write(arrayOfByte);
		} catch (IOException localIOException) {
			throw new CommunicationException(false, "write socket error", localIOException);
		} finally {
			close(localSocket);
		}
		return null;
	}

	public Object sendAndReceive(Object inputByte, String paramString) throws ConnectionException, SocketReadException, SocketWriteException,
			SocketTimeOutException {

		if (this.streamResolver == null) {
			throw new IllegalArgumentException("no stream resolver defined");
		}
		byte[] arrayOfByte1 = (byte[]) inputByte;
		log.info("原始报文=" + arrayOfByte1);

		String sndStrPre;
		try {
			sndStrPre = new String(arrayOfByte1, "GBK");
			log.info("sndStrPre=" + sndStrPre);
			String detailDate = new String(sndStrPre.substring(129).getBytes("GBK"),"GBK");

			log.info("detailDate,即拼接在后面的报文内容=" + detailDate);
			
			byte[] orgB=new byte[129];
			
			String md5Date = CryptoUtils.md5(orgB); // 校验位
			log.info("md5Date=" + md5Date);

			StringBuffer sb = new StringBuffer();
			log.info("初始报文头(不含校验位)=" + sndStrPre.substring(0, 97));
			sb.append(sndStrPre.substring(0, 97)); // 初始报文头(不含校验位)
			sb.append(md5Date);
			
			sb.append(detailDate);
			arrayOfByte1 = sb.toString().getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			throw new SocketReadException(false, "socket write error", e);
		}
		int m = 0;
		if (this.booleanFalse) {
			m = this.k.getAndIncrement();
			a(arrayOfByte1, paramString, m, false);
		}
		log.info("send byte: \n" + Hex.toDumpString(arrayOfByte1));

		Socket localSocket = newSocket();
		try {
			localSocket.getOutputStream().write(arrayOfByte1);
		} catch (IOException localIOException) {
			throw new SocketWriteException(false, "socket write error", localIOException);
		}
		byte[] arrayOfByte2 = null;
		try {
			arrayOfByte2 = this.streamResolver.resolve(localSocket.getInputStream());
			if (this.booleanFalse) {
				a(arrayOfByte2, paramString, m, true);
			}
		} catch (IOException localIOException) {
			throw new SocketReadException(false, "socket read error", localIOException);
		} catch (JumpRuntimeException localJumpRuntimeException) {
			Throwable localThrowable = localJumpRuntimeException.getCause();
			if ((localThrowable instanceof SocketTimeoutException))
				throw new SocketTimeOutException(true, "socket time out", localJumpRuntimeException);
		} finally {
			close(localSocket);
		}
		return arrayOfByte2;
	}

	protected void config(Socket paramSocket) throws SocketException {
		if (this.e > 0)
			paramSocket.setReceiveBufferSize(this.e);
		if (this.f > 0)
			paramSocket.setSendBufferSize(this.f);
		if (this.g >= 0)
			paramSocket.setSoLinger(true, this.g);
		if (this.soTimeout > 0)
			paramSocket.setSoTimeout(this.soTimeout);
	}

	protected Socket newSocket() throws ConnectionException {
		Socket localSocket = null;
		try {
			localSocket = new Socket();
			localSocket.setReuseAddress(this.d);
			config(localSocket);
			log.info("conn "+a +":"+b);
			InetSocketAddress localInetSocketAddress = new InetSocketAddress(this.a, this.b);
			if (this.c > 0)
				localSocket.connect(localInetSocketAddress, this.c);
			else
				localSocket.connect(localInetSocketAddress);
			return localSocket;
		} catch (IOException localIOException) {
			if (localSocket != null)
				close(localSocket);
			throw new ConnectionException(false, "cannot_new_socket", localIOException);
		}
	}

	public void close(Socket paramSocket) {
		try {
			paramSocket.close();
		} catch (IOException localIOException) {
		}
	}
}
package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.jump.bp.JumpRuntimeException;
import com.bocom.jump.bp.channel.tcp.NumberConverter;
import com.bocom.jump.bp.channel.tcp.StreamResolver;

public class WaterLengthStreamResolver implements StreamResolver {
	 protected static Logger log = LoggerFactory.getLogger(WaterLengthStreamResolver.class);
	  private int a;
	  private int b;
	  private int c;
	  private int d = 20971520;

	  private NumberConverter e = new WaterNumberConverter(this);

	  public void setLengthOffset(int paramInt)
	  {
	    this.a = paramInt;
	  }

	  public void setLengthSize(int paramInt) {
	    this.b = paramInt;
	  }

	  public void setLengthAddtive(int paramInt) {
	    this.c = paramInt;
	  }

	  public void setNumberConverter(NumberConverter paramNumberConverter) {
	    this.e = paramNumberConverter;
	  }

	  public void setMaxLengthThreshold(int paramInt) {
	    this.d = paramInt;
	  }

	  public byte[] resolve(InputStream paramInputStream) {
	    if (this.b > 0) {
	      return a(paramInputStream, this.a, this.b, this.d, this.e, this.c);
	    }
	    throw new IllegalStateException("length size must > 0");
	  }

	  static byte[] a(InputStream paramInputStream, int paramInt1, int paramInt2, int paramInt3, NumberConverter paramNumberConverter, int paramInt4)
	  {
	    try
	    {
	      DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
	      byte[] arrayOfByte1 = new byte[paramInt1 + paramInt2];
	      byte[] arrayOfByte11 = new byte[paramInt1 + paramInt2];
	      localDataInputStream.readFully(arrayOfByte1);
	      for (int i = 0; i < arrayOfByte11.length; i++) {
			arrayOfByte11[i] = arrayOfByte1[i];
		}
		arrayOfByte1[0]=48;
          for(int k=1;k<arrayOfByte1.length;k++){
        	  arrayOfByte1[k]=arrayOfByte11[k-1];
          }
	      int i = paramNumberConverter.toInt(arrayOfByte1, paramInt1, paramInt2);
          i-=4;
	      if ((paramInt3 > 0) && 
	        (i >= paramInt3)) {
	        log.error("Length [" + i + "] exceeds the limit [" + paramInt3 + "]. ");
	        throw new JumpRuntimeException(
	          "JUMPTP8000", "socket_read_error");
	      }

	      i += paramInt4;
	      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(64);
	      localByteArrayOutputStream.write(arrayOfByte1);

	      byte[] arrayOfByte2 = new byte[i];
	     
	      localDataInputStream.readFully(arrayOfByte2);

	      localByteArrayOutputStream.write(arrayOfByte2);

	      return localByteArrayOutputStream.toByteArray();
	    } catch (IOException localIOException) {
	      throw new JumpRuntimeException("JUMPTP8000", "socket_read_error", localIOException);
	    }
	  }
}

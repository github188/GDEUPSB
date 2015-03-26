package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.bocom.bbip.eups.adaptor.exception.SocketReadException;
import com.bocom.bbip.thd.org.apache.commons.lang.StringUtils;
import com.bocom.jump.bp.channel.tcp.LengthStreamResolver;
import com.bocom.jump.bp.channel.tcp.NumberConverter;

public class WatrLengthStreamResolver  extends LengthStreamResolver {

    private int lengthOffset;

    private int lengthSize;

    private int lengthAddtive;

    private NumberConverter d = new NumberStreamResolver(this);

    public void setLengthOffset(int paramInt) {
        this.lengthOffset = paramInt;
    }

    public void setLengthSize(int paramInt) {
        this.lengthSize = paramInt;
    }

    public void setLengthAddtive(int paramInt) {
        this.lengthAddtive = paramInt;
    }

    public void setNumberConverter(NumberConverter paramNumberConverter) {
        this.d = paramNumberConverter;
    }

    @Override
    public byte[] resolve(InputStream paramInputStream) {
        if (this.lengthSize > 0) {
            try {
                return getBytes(paramInputStream, this.lengthOffset, this.lengthSize, this.d, this.lengthAddtive);
            } catch (SocketReadException e) {
            }
        }
        throw new IllegalStateException("length size must > 0");
    }

    static byte[] getBytes(InputStream paramInputStream, int lengthOffset, int lengthSize, NumberConverter paramNumberConverter, int lengthAddtive)
        throws SocketReadException {
        try {
            DataInputStream localDataInputStream = new DataInputStream(paramInputStream);

            byte[] arrayOfByte1 = new byte[lengthOffset + lengthSize];
            localDataInputStream.readFully(arrayOfByte1);

            int i = paramNumberConverter.toInt(arrayOfByte1, lengthOffset, lengthSize);
            i += lengthAddtive;
            //TODO：前置长度需要算在总长度类的需要在此处减去前置长度位数
//            i = i - 4;
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(64);
            
            String lenStr1=new String(arrayOfByte1).trim();
            lenStr1=StringUtils.leftPad(lenStr1, lengthSize, "0");
            
            localByteArrayOutputStream.write(lenStr1.getBytes());

            byte[] arrayOfByte2 = new byte[i];
            localDataInputStream.readFully(arrayOfByte2);

            localByteArrayOutputStream.write(arrayOfByte2);

            return localByteArrayOutputStream.toByteArray();
        } catch (IOException localIOException) {
            throw new SocketReadException(false, "socket read error", localIOException);
        }
    }
}

class NumberStreamResolver implements NumberConverter {

    NumberStreamResolver(LengthStreamResolver paramLengthStreamResolver) {
    }
    
    public int toInt(byte[] paramArrayOfByte, int lengthOffset, int lengSize) {
    	byte[] lenb = new byte[lengSize];
        System.arraycopy(paramArrayOfByte, lengthOffset, lenb, 0, lengSize);
    	String len = new String(lenb).trim();
        return Integer.parseInt(len);
    }
}

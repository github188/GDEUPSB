package com.bocom.bbip.gdeupsb.channel.tcp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.jump.bp.channel.tcp.NumberConverter;

public class WaterNumberConverter  implements NumberConverter
{	    	private static final Log logger = LogFactory.getLog(WaterNumberConverter.class);

	WaterNumberConverter(WaterLengthStreamResolver paramLengthStreamResolver)
	  {
	  }

	  public int toInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
	  {
	    int i = 0;
	    for (int j = 0; j < paramInt2; j++) {
          
	    
			i = i * 10 + ((paramArrayOfByte[(paramInt1 + j)] & 0xFF) - 48); 
			logger.info(((paramArrayOfByte[(paramInt1 + j)] & 0xFF)));
			
		
	    }
	    logger.info(i);
	    return i;
	  }
	}
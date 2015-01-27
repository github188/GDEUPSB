
package com.bocom.bbip.gdeupsb.common;

/**
 * 统一定义公共事业缴费的错误码.
 * 
 * @version 1.0.0,2015-01-04
 * @author q.c
 * @modify
 * @since 1.0.0
 */
public final class GDErrorCodes {
	
	/** 批扣文件信息异常 */
    public static final String EUPS_COM_BAT_FILE_ERROR = "BBIP4400EU9900";
	
  
	/** 广东电力未知错误 */
    public static final String EUPS_ELE_GZ_UNKNOWN_ERROR = "BBIP4400EU0100";
    
    /** 广东电力对公对私标志错误 */
    public static final String EUPS_ELE_GZ_PAY_MOD_FLAG_ERROR = "BBIP4400EU0101";
    
    /** 广东电力凭单号错误  */
    public static final String EUPS_ELE_GZ_TCK_ERROR = "BBIP4400EU0102";
    
    /** 广东电力原交易流水已抹帐  */
    public static final String EUPS_ELE_GZ_JNL_ALREADY_CANCLE = "BBIP4400EU0103";
    
    /** 广东电力原交易流水信息错误  */
    public static final String EUPS_ELE_GZ_CANCLE_INFO_ERROR = "BBIP4400EU0104";
	
    
	
}

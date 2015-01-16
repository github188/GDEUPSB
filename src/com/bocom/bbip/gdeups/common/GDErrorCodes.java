
package com.bocom.bbip.gdeups.common;

/**
 * 统一定义公共事业缴费的错误码.
 * 
 * @version 1.0.0,2015-01-04
 * @author q.c
 * @modify
 * @since 1.0.0
 */
public final class GDErrorCodes {
  
	/** 广东电力未知错误 */
    public static final String EUPS_ELE_GZ_UNKNOWN_ERROR = "BBIP4400EU0100";
    
    /** 广东电力对公对私标志错误 */
    public static final String EUPS_ELE_GZ_PAY_MOD_FLAG_ERROR = "BBIP4400EU0101";
    
    /** 广东电力凭单号错误  */
    public static final String EUPS_ELE_GZ_TCK_ERROR = "BBIP4400EU0102";
    
    /** 广东电力原交易流水已抹帐  */
    public static final String EUPS_ELE_GZ_JNL_ALREADY_CANCLE = "BBIP4400EU0103";
	
	
}

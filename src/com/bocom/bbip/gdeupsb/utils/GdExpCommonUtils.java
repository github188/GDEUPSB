package com.bocom.bbip.gdeupsb.utils;

import java.math.BigDecimal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GdExpCommonUtils {
    static final Log log = LogFactory.getLog(GdExpCommonUtils.class);
   

    //FMUL($TxnAmt,100,0) 金额乘以100后去掉小数点后面的数字，元to分后，不保留小数11234.7890 to 112345678
    public static final String transYuanToFen(String txnAmt){
        log.info("transYuanToFen method !");
        BigDecimal fmt = new BigDecimal(txnAmt);
        fmt = fmt.movePointRight(2);
        String fmtstr = String.valueOf(fmt);
        int abc = fmtstr.indexOf(".");
        String subFmt = fmtstr.substring(0, abc);
        return subFmt;
    }
     
}

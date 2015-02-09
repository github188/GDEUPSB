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

    // 截取字符串
       public static final String subStr(String ags, int sta, int end) {
           deleteAllSpace(ags);
           ags.substring(sta, end);
           return ags;
       }

    // 删除所有空格
       public static final String deleteAllSpace(String str) {
           System.out.println("delAllSpc"+str);
           if (str == null || str.length() == 0)
               return str;
           int sz = str.length();
           char chs[] = new char[sz];
           int count = 0;
           for (int i = 0; i < sz; i++)
               if (!Character.isWhitespace(str.charAt(i)))
                   chs[count++] = str.charAt(i);

           if (count == sz)
               return str;
           else
               return new String(chs, 0, count);
       }

}

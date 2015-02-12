package com.bocom.bbip.gdeupsb.utils;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;

public class GdExpCommonUtils {
    static final Log log = LogFactory.getLog(GdExpCommonUtils.class);
   
public static final int multiple = 8;



//先将金额转换成123.12形式然后AddChar
public static final String FmtAmtAndAddChar(String amt, int len, char des, char LorR){
	   amt = AMTFMT2(amt);
	   amt = AddChar(amt, len, des, LorR);
	   return amt;
}




    
    public static Integer loopBodyLength(String retNum){
     
        int number=Integer.valueOf(retNum);
        return number*166;
    } 
    public static Integer loopBodyLength1(String retNum){
        
        int number=Integer.valueOf(retNum);
        return number*34;
    } 
    public static String rspCodeCSW(String rRTxnCd,String thdRspCde){
        if(rRTxnCd!=null&&rRTxnCd.equals("520")){
            if(thdRspCde!=null&&thdRspCde.equals("004")){
                thdRspCde="000";
            }
        }
        return thdRspCde;
 } 
    public static Integer loopBodyLength3(String retNum){
        int number=1;
 	   while(retNum.startsWith("0")){
 		   if(retNum.equals("0")){
 			   break;
 		   }
         	retNum=retNum.substring(number,retNum.length());
             number++;      	
         }
         return Integer.parseInt(retNum)*34;
     } 
    
    public static String  AMTFMT3(String str){
    	if(StringUtils.isEmpty(str)){
    		return "0";
    	}
    	str=str.trim();
    	return new BigDecimal(str).divide(new BigDecimal("100")).toString();
    }
    
   public static String returnBk(){
       return "06";
   }
   
   
   
   
   //先deleAllSpace  再addChar
   public static final String DeleteAllSpaceAndADDChar(String res, int len, char des, char LorR){
       res=DeleteAllSpace(res);
       res=AddChar(res, len, des, LorR);
       return res;
   }
   //AMTPOWER+DELBOTHSPACE
   public static final String AMTPOWER(String amt,int num){
       amt=amt.trim();
       if(amt==null||amt.length()==0){
           return "";
       }
       int idx = 1;
       for (int c = 0; c < num; c++)
              idx *= 10;
       long l = new BigDecimal(amt.trim()).multiply(new BigDecimal(idx)).longValue();
       return String.valueOf(l);
   }
   public static Integer loopBodyLength2(String retNum) {

       retNum = AMTDELZERO(retNum);
       int number = Integer.valueOf(retNum);
       return number * 34;
   }

   // 去左边0，加小数点加逗号ex:09123123>>>>91,231.23
    public static final String AMTFMT(String res) {
        System.out.println("AMTFMT>>>>>>>>>>"+res);
        int i,j,len;
        String amt=AMTDELZERO(res);
        StringBuffer buf = new StringBuffer(amt);
        len = amt.length();
        if ((len - 2) % 3 == 0) {
            j = (len - 2) / 3 - 1;
        } else {
            j = (len - 2) / 3;
        }
        for (i = 0; i < j; i++) {
            buf.insert(len - 2 - (i + 1) * 3, ',');
        }
        if(buf.length()==2){
            buf.insert(0, 0);
        }
        buf.insert(buf.length() - 2, '.');
        
        return buf.toString();
    }
    //金额转化成元
    // 去左边0，加小数点ex:09123123>>>>91231.23
    public static final String AMTFMT2(String res) {
        System.out.println("AMTFMT>>>>>>>>>>"+res);
        int i,j,len;
        String amt=AMTDELZERO(res);
        if(StringUtils.isEmpty(amt)){
        	return "0";
        }
        
        StringBuffer buf = new StringBuffer(amt.trim());
        len = amt.length();
        if ((len - 2) % 3 == 0) {
            j = (len - 2) / 3 - 1;
        } else {
            j = (len - 2) / 3;
        }
        for (i = 0; i < j; i++) {
            buf.insert(len - 2 - (i + 1) * 3, ',');
        }
        if(buf.length()==2){
            buf.insert(0, 0);
        }
        buf.insert(buf.length() - 2, '.');
        String str=buf.toString();
        
        return str.replace(",","");
    }

    
    
    
    
    
    
    
    
    // GETDATETIME
    public static final Date GETDATETIME(String fmt) {
        return DateUtils.parse(DateUtils.format(new Date(), fmt));
    }

    // AMTDELZERO_DELSPACE
    public static final String AMTDELZERO_DELSPACE(String res) {
        res = DeleteAllSpace(res);
        res = AMTDELZERO(res);
        return res;
    }

    // 删除金额左边的零
    public static final String AMTDELZERO(String res) {
        if(!res.contentEquals(".")){
            while (res.startsWith("0") && res.length() > 0) {
                res=res.substring(1);
            }
        }
        return res;
    }

    // AMTDELZERO(DELSPACE(REPCHAR(DELSPACE($**)
    public static final String AMTDELZERO_DELSPACE_REPCHAR_DELSPACE(String res, char rs, char ds) {
        res = DeleteAllSpace(res);
        res = replaceChar(res, rs, ds);
        res = DeleteAllSpace(res);
        res = AMTDELZERO(res);
        return res;
    }

 // 替换字符
    public static final String replaceChar(String res, char rs, char ds) {
        String str = null;
        str = res.replace(rs, ds);
        return str;
    }

    // 原日期格式改为fmt
    public static final String StrCatDte(Date res,String fmt) {
    	if(null==res)
    		return "";
    	
        String des = null;
        System.out.println("fmt>>>"+fmt);
        des=DateUtils.format(res, fmt);
        System.out.println(des);
        return des;
    }

 // 截取字符串
    public static final String SubStr(String ags, int sta, int end) {
        ags.substring(sta, end);
        return ags;
    }

 // 删除所有空格
    public static final String DeleteAllSpace(String str) {
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

 // 加小数点并判断是否补足字符长度
    public static final String AMTADDDOTandADDCHAR(String res, int len, char des, char LorR, char addCharFlg) {// 1-���ո�
       System.out.println("txnAmt>>"+res);
    	if (res == null || res.length() == 0)
            return "";
        StringBuffer buf = new StringBuffer(res);
        int flag = 1;
        if (buf.charAt(0) == '-') {
            flag = -1;
            buf = buf.delete(0, 1);
        }
        if (buf.length() >= 3) {
            buf.insert(buf.length() - 2, '.');
        } else if (buf.length() == 2) {
            buf.insert(0, "0.");
        } else if (buf.length() == 1) {
            buf.insert(0, "0.0");
        } else if (buf.length() == 0) {
            buf.insert(0, '0');
        }
        if (flag == -1) {
            buf.insert(0, '-');
        }
        res = buf.toString();
        // ------------------
        if (addCharFlg == '1') {
            res = AddChar(res, len, des, LorR);
        }
        return null;
    }
    //金额元转成分 补0
    public static final String AMTFMT3(Object txnAmt,int len){
       String txnAmt2=txnAmt.toString().replace(".","");
       char data='0';
       char data1='2';
       return AddChar(txnAmt2,len,data,data1); 
    }
    
    
    
    
 // 补空格
    public static final String AddChar(String res, int len, char des, char LorR) {
    	System.out.println("res>>>"+res);
        System.out.println("addChar>>"+"‘"+des+"’");
        if (res.trim().length() != len) {
            int tm = len - res.trim().length();
            if (LorR == '0') {
            	System.out.println("0右补-"+LorR);
                for (int i = 0; i < tm; i++) {
                    res =res+des;
                }
            } else {
            	System.out.println("1左补-"+LorR);
                for (int i = 0; i < tm; i++) {
                    res = des + res;
                }
            }
            return res;
        }
        return res;
    }

 // 转换代码为信息
    public static final String TrnToMsg(String rpCod) {
        String retSmr = null;
        if (rpCod.equals("0000")) {
            retSmr = "执行成功";
        } else if (rpCod.equals("0001")) {
            retSmr = "执行不成功";
        } else if (rpCod.equals("0002")) {
            retSmr = "燃气使用证号不存在";
        } else if (rpCod.equals("0022")) {
            retSmr = "数据接收格式不对";
        } else if (rpCod.equals("2000")) {
            retSmr = "未知错误";
        } else if (rpCod.equals("2001")) {
            retSmr = "本次交费已存在，重复交费";
        } else if (rpCod.equals("2002")) {
            retSmr = "没有找到客户编号";
        } else if (rpCod.equals("2003")) {
            retSmr = "存入的钱不足够交费，因为钱不够";
        } else if (rpCod.equals("3001")) {
            retSmr = "不是最后一张收费发票，请从最后一个张发票作废";
        } else if (rpCod.equals("3002")) {
            retSmr = "没有这张发票，或是本次交费已作废，不可重复作废";
        } else if (rpCod.equals("3003")) {
            retSmr = "没有这个用户或是这个用户当天没有交费。";
        } else if (rpCod.equals("3004")) {
            retSmr = "未知错误";
        }
        return retSmr;
    }

 // 0000转为000000
    public static final String GetRtn(String ags) {
        System.out.println(ags);
        if (ags.equals("0000")) {
            return "000000";
        }
        return "000001";
    }

    /**
     * 
     * @param ags
     * @return
     */
    public static final String DeleteSpace(String ags) {
        return ags.trim();
    }

    /**
     * 
     * @param rpCod
     * @return
     */
    public static final String CheckRpCod(String rpCod) {
        if (rpCod.equals("0000")) {
            return "0000";
        }
        return "1111";
    }

    //判断第三方返回码，附加报文
    public static final String CheckRspCod(String rspcod) {
        if (rspcod.equals("001") || rspcod.equals("004")) {
            return "000";
        }
        return "001";
    }
    
    //左补7位0
    public static final String addChar(String rspcod) {
        
    	String ret = StringUtils.leftPad(rspcod, 7, "0");
        return ret;
    }

    //左补8位0
    public static final String add8Char(String rspcod) {
        
    	String ret = StringUtils.leftPad(rspcod, 8, "0");
        return ret;
    }
    
    //左补15位0
    public static final String add15Char(String rspcod) {
        
    	String ret = StringUtils.leftPad(rspcod, 15, "0");
        return ret;
    }
    
    //左补10位0
    public static final String add10Char(String rspcod) {
        
    	String ret = StringUtils.leftPad(rspcod, 10, "0");
        return ret;
    }
    
    //删除空格补10位0
    public static final String addChar10trim(String rspcod) {
    	String ret = "";
    	if (rspcod != null) {
    		rspcod = rspcod.trim();
    		ret = StringUtils.leftPad(rspcod, 10, "0");
    	}
    	return ret;
    }
    
  //删除空格补8位0
    public static final String addChar8trim(String rspcod) {
    	String ret = "";
    	if (rspcod != null) {
    		rspcod = rspcod.trim();
    		ret = StringUtils.leftPad(rspcod, 8, "0");
    	}
    	return ret;
    }
    
    //截字符串
    public static final String Sub60String(String rspcod) {
        
    	String ret = rspcod.trim();
    	if (ret.length() > 60) {
    		ret = ret.substring(0, 60);
    	}
        return ret;
    }
    
   public static final String convert(String rspcod) {
	   if(rspcod.isEmpty()){
		   rspcod = null;
		   }
	   return rspcod;
	   }
   public static final String strCat(String str1,String str2,String str3,String str4,String str5,String str6,
		   String str7,String str8,String str9,String str10,String str11,String str12,String str13,String str14) {
	  String str = str1+"|"+str2+"|"+str3+"|"+str4+"|"+str5+"|"+str6+"|"+str7+"|"+str8+"|"+str9+"|"+str10+"|"+str11+"|"+str12+"|"+str13+"|"+str14;
	   return str;
   }
   public static final String strCat1(String str1, String str2){
	   StringBuffer buf = new StringBuffer();
	   buf.append(str1+"|"+str2);
	   return buf.toString();
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

/* getCnlty.java   2015-1-17 
 * Copy Right 2015 Bank of Communications Co.Ltd.
 * All Copyright Reserved
 */

package com.bocom.bbip.gdeupsb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CardInfo;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsCusAgent;
import com.bocom.bbip.eups.repository.EupsCusAgentRepository;
import com.bocom.bbip.eups.utils.ExpCommonUtils;

import com.bocom.bbip.utils.CryptoUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 工具类
 * 
 * @version 1.0.0,2015-1-17
 * @author Administrator
 * @since 1.0.0
 */
public class UtilsCnlty {

	private static Logger log = LoggerFactory.getLogger(UtilsCnlty.class);
	@Autowired
	private EupsCusAgentRepository eupsCusAgentRepository;
	@Autowired
	@Qualifier("accountService")
	private AccountService accountService;

	public String getAccountType(String cusAc) {
		CardInfo cardInfo = accountService.getCardInfoByCardNo(cusAc);
		return cardInfo.getCardType();

	}

	/**
	 * 记账要素(必输) json 格式
	 * @return
	 */
	public static String getPrvDat() {
		String str = "{\"GIA-SYS-NO\":\"1001\",\"optId\":\"doBE2010\",\"AMT\":\"100\",\"GIA-TR-BR\":\"01310800999\",\"OPP-AC\":\"310066629018170206519\",\"GIA-TRACE-SEQ\":\"20141028CN2110000201\",\"GIA-TR-TLR\":\"EPA0BB7\",\"GIA-OP-BK\":\"01310999999"
				+ "\",\"GIA-SRV-CODE\":\"342010\",\"CCY\":\"CNY\",\"GIA-OP-TLR\":\"EPA0BB7\",\"EXG-NO\":\"000000404508\",\"GIA-REQ-TYPE\":\"T\",\"RMK-100\":null,\"OPP-AC-NME\":\"XX??\",\"GIA-OP-BR\":\"01310800999\",\"EXG-BUS-KND\":\"11\",\"GIA-TR-BK\":\"01310999999\",\"GIA-TRACE-SYS-ID\":\"11110000"
				+ "\",\"GIA-VERSION\":\"1.00\",\"AC-DTE\":\"20141025\",\"CUS-AC\":\"310066629018170206519\",\"OPP-EXG-NO\":\"000000021541\",\"BV-NO\":\"0\",\"ISE-BK\":\"01310999999\",\"GIA-CHNL-TYPE\":\"00\",\"CTL-WRD\":\"0\",\"CUS-NME\":\"?1ō?\",\"EXG-ARA-NO\":\"443001\",\"GIA-FILLER\":\"Y\",\"BV-CDE"
				+ "\":\"123\",\"TRADE_CODE\":\"GEMS\",\"GIA-TX-SYS-ID\":\"11110000\",\"EXI-SCE\":\"001\"}";

		return str;
	}

	/**
	 * 渠道转换
	 * 
	 * @param TxnSrc
	 * @return
	 */
	public static String getCnlty(String TxnSrc) {
		/*
		 * <Item TxnSrc="TE441" CnlTyp="1"/> <!--电话银行--> <Item TxnSrc="MT441"
		 * CnlTyp="5"/> <!--多媒体--> <Item TxnSrc="WE441" CnlTyp="2"/> <!--网上银行-->
		 * <Item TxnSrc="MB441" CnlTyp="6"/> <!--手机银行--> <Item TxnSrc="SF441"
		 * CnlTyp="T"/> <!-- 收富宝,总行要求要用T就是生活管--> <Default CnlTyp="0"/>
		 */
		String CnlTyp = "0";
		// String TxnCnl="TM";
		if (TxnSrc.length() == 0) {
			CnlTyp = "0";
		}
		if (TxnSrc == "TE441") {
			CnlTyp = "1";
		} else if (TxnSrc == "MT441") {
			CnlTyp = "5";
		} else if (TxnSrc == "WE441") {
			CnlTyp = "2";
		} else if (TxnSrc == "MB441") {
			CnlTyp = "6";
		} else if (TxnSrc == "SF441") {
			CnlTyp = "T";
		}
		return CnlTyp;
	}

	/**
	 * 根据渠道标识确定渠道
	 * 
	 * @param TxnSrc
	 * @return
	 */
	public static String getTxnCnl(String TxnSrc) {
		String TxnCnl = "TRM";
		if (TxnSrc == "TE441") {
			TxnCnl = "TRM";
		} else if (TxnSrc == "MT441") {
			TxnCnl = "MMT";
		} else if (TxnSrc == "WE441") {
			TxnCnl = "WEB";
		} else if (TxnSrc == "MB441") {
			TxnCnl = "MOB";
		} else if (TxnSrc == "SF441") {
			TxnCnl = "SFB";
		}
		return TxnCnl;

	}

	public static String getCnlNam(String TxnCnl) {

		String CnlNam = "其他渠道";
		if (TxnCnl == "TRM") {
			CnlNam = "自助通";
		} else if (TxnCnl == "MMT") {
			CnlNam = "自助通";
		} else if (TxnCnl == "WEB") {
			CnlNam = "个人网银";
		} else if (TxnCnl == "MOB") {
			CnlNam = "手机银行";
		} else if (TxnCnl == "FBP") {
			CnlNam = "柜面批量";
		} else if (TxnCnl == "THD") {
			CnlNam = "第三方渠道";
		}
		return CnlNam;

	}

	//
	/**
	 * 左补0
	 * 
	 * @param item
	 * @param len
	 * @return
	 */
	public static String fillZero(String item, int len) {
		String strItem = "";
		StringBuffer strBuf = new StringBuffer();
		int itemLeng = item.length();
		for (int i = 0; i < len - itemLeng; i++) {
			strItem = strBuf.append("0").toString();
		}
		strItem = strBuf.append(item).toString();
		return strItem;
	}

	/**
	 * 左补空格
	 * 
	 * @param item
	 * @param len
	 * @return
	 * @throws CoreException 
	 */
	public static String fillEmpty(String item, int len) throws CoreException {
		String strItem = "";
		StringBuffer strBuf = new StringBuffer();
		int itemLeng = 0;
		try {
			itemLeng = item.getBytes("GBK").length;
		} catch (UnsupportedEncodingException e) {
			throw new CoreException(e.getMessage());
		}
		if (itemLeng > len) {
			item = item.substring(itemLeng - len, len);
		}
		if (itemLeng < len) {
			for (int i = 0; i < len - itemLeng; i++) {
				strItem = strBuf.append(" ").toString();
			}
		}
		strItem = strBuf.append(item).toString();
		return strItem;
	}

	/**
	 * 右补空格
	 * 
	 * @param item
	 * @param len
	 * @return
	 */
	public static String fillEmptyRt(String item, int len) {
		int length = 0;
		try {
			length = item.getBytes("GBK").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (length > len) {
			item = item.substring(length - len, len);
		}
		if (length < len) {
			for (int i = 0; i < len - length; i++) {
				item = item + " ";
			}
		}
		return item;
	}

	/**
	 * 补空格 如果长度超长截取
	 * 
	 * @param item
	 * @param len
	 * @return
	 */
	@SuppressWarnings("unused")
	public static String fillSpace(String item, int len) {
		String strItem = "";
		StringBuffer strBuf = new StringBuffer();
		int itemLeng = item.length();
		if (itemLeng > len) {
			item = item.substring(itemLeng - len, itemLeng);
		} else if (itemLeng < len) {
			for (int i = 0; i < len - itemLeng; i++) {
				strItem = strBuf.append(" ").toString();
			}
			item = strBuf.append(item).toString();
		}

		return item;
	}

	/**
	 * 获取客户协议信息
	 * 
	 * @param eupsCusAgent
	 * @return
	 */
	public List<EupsCusAgent> getEupsCusAgent(EupsCusAgent eupsCusAgent) {

		List<EupsCusAgent> EupsCusAgentList = eupsCusAgentRepository
				.find(eupsCusAgent);

		return EupsCusAgentList;

	}

	/**
	 * 获取金额大写
	 * 
	 * @param v
	 * @return
	 */
	public static String getTool(double v) {
		final String UNIT = "万千佰拾亿千佰拾万千佰拾元角分";
		final String DIGIT = "零壹贰叁肆伍陆柒捌玖";
		final double MAX_VALUE = 9999999999999.99D;

		if (v < 0 || v > MAX_VALUE) {
			return "参数非法!";
		}
		long l = Math.round(v * 100);
		if (l == 0) {
			return "零元整";
		}
		String strValue = l + "";
		int i = 0;
		int j = UNIT.length() - strValue.length();
		String rs = "";
		boolean isZero = false;
		for (; i < strValue.length(); i++, j++) {
			char ch = strValue.charAt(i);
			if (ch == '0') {
				isZero = true;
				if (UNIT.charAt(j) == '亿' || UNIT.charAt(j) == '万'
						|| UNIT.charAt(j) == '元') {
					rs = rs + UNIT.charAt(j);
					isZero = false;
				}
			} else {
				if (isZero) {
					rs = rs + '零';
					isZero = false;
				}
				rs = rs + DIGIT.charAt(ch - '0') + UNIT.charAt(j);
			}
		}
		if (!rs.endsWith("分")) {
			rs = rs + "整";
		}
		rs = rs.replaceAll("亿万", "亿");
		return rs;
	}

	/**
	 * 截取流水号后8位,不足前补零
	 * 
	 * @param item
	 * @return
	 */
	public static String subStr(String item) {
		StringBuffer sb = new StringBuffer();
		String strItem = "";
		if (item.length() >= 8) {
			strItem = sb.append(
					item.substring(item.length() - 8, item.length()))
					.toString();// 截取流水号后8位
		} else {
			for (int i = 0; i < 8 - item.length(); i++) {
				sb.append("0");
			}
			strItem = sb.append(item).toString();
		}
		return strItem;
	}

	/**
	 * 截取流水号前6位，不足前补零
	 * 
	 * @param item
	 * @return
	 */
	public static String getCutOut(String item) {
		StringBuffer sbuf = new StringBuffer();
		String strItem = "";
		if (item.length() >= 6) {
			strItem = sbuf.append(item.substring(0, 6)).toString();// 截取流水号前6位
		} else {
			for (int i = 0; i < 6 - item.length(); i++) {
				sbuf.append("0");
			}
			strItem = sbuf.append(item).toString();
		}
		return strItem;
	}

	/**
	 * 去掉空格，null则返回空字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String trimString(String str) {
		if (str != null) {
			return str.trim();
		}
		return "";
	}

	/**
	 * 将元转换为分 *100
	 * 
	 * @param bigDecimal
	 * @return
	 */
	public static String multiplyToString(BigDecimal big) {
		if (big != null) {
			return big.multiply(new BigDecimal("100")).setScale(0).toString();
		}
		return "";
	}

	

	/**
	 * 日期转String类型
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toDateStringFormat(Date date, String pattern) {
		if (date != null) {
			return DateUtils.format(date, pattern);
		}
		return null;
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public static String getChkFleNmeAre(int i) {
		switch (i) {
		case 1:
			return "CZ";
		case 2:
			return "DG";
		case 3:
			return "FS";
		case 4:
			return "GZ";
		case 5:
			return "HY";
		case 6:
			return "HZ";
		case 7:
			return "JM";
		case 8:
			return "JY";
		case 9:
			return "MM";
		case 10:
			return "MZ";
		case 11:
			return "QY";
		case 12:
			return "SD";
		case 13:
			return "SG";
		case 14:
			return "ST";
		case 15:
			return "SW";
		case 16:
			return "SZ";
		case 17:
			return "YF";
		case 18:
			return "YJ";
		case 19:
			return "ZH";
		case 20:
			return "ZJ";
		case 21:
			return "ZQ";
		case 22:
			return "ZS";
		default:
			return "";
		}
	}

	/**
	 * 将字符串转化为16进制字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String str2HexStr(String str) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString();
	}

	/**
	 * 将第三方返回错误码转换为错误信息（第三方返回码必须为thdRspCde）
	 * 
	 * @param thdReturnMsg
	 *            第三方返回map
	 * @param eupsBusTyp
	 *            业务码
	 * @return
	 */
	public static String getThdRspMsg(Map<String, Object> thdReturnMsg,
			String eupsBusTyp) {
		CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
		String responseCode = rspCdeAction.getThdRspCde(thdReturnMsg,
				eupsBusTyp);
		String thdRspMsg = null;
		InputStream errorMsgInput = UtilsCnlty.class.getClassLoader()
				.getResourceAsStream("config/msg/errors.properties");
		Map<String, String> errorMap = ExpCommonUtils
				.getPropertyFile(errorMsgInput);
		thdRspMsg = (String) errorMap.get(responseCode);
		if (StringUtils.isEmpty(thdRspMsg)) {
			String responseMessage = rspCdeAction.getThdRspMsg(thdReturnMsg);
			if (StringUtils.isNotEmpty(responseMessage)) {
				thdRspMsg = responseMessage;
			} else {
				thdRspMsg = "交易失败，无错误返回信息!";
			}
		} else if (thdRspMsg.length() > 60)
			thdRspMsg = "交易失败!";
		try {
			errorMsgInput.close();
		} catch (IOException e) {
			log.error("errorMsgInput close error!");
		}
		log.error((new StringBuilder(
				"The third response Failed!!!! response map: ")).append(
				thdReturnMsg.toString()).toString());
		return null;
	}

	/**
	 * 将字符串金额分 转化为 元
	 * 
	 * @param txnAmt
	 * @return
	 */
	public static String getStrTxnAmtYuan(String txnAmtFen) {
		if (StringUtils.isNotBlank(txnAmtFen)) {
			return txnAmtFen.substring(0, txnAmtFen.length() - 2) + "."
					+ txnAmtFen.substring(txnAmtFen.length() - 2);
		}
		return "";
	}

	/**
	 * 将字符串通过MD5加密
	 * 
	 * @param strMd5
	 * @return
	 */
	public static String encodeByMd5(String strMd5) {
		if (StringUtils.isNotEmpty(strMd5)) {
			return CryptoUtils.md5(strMd5.getBytes());
		}
		return "";
	}

	/**
	 * mac加密
	 * 
	 * @param key
	 * @param randomStr
	 * @param str
	 * @return
	 */
	public static String encodeByMac(String key, String randomStr, String str) {
		String chStr = toStringByBytes(str);
		String ranStr = toStringByBytes(randomStr);
		if (StringUtils.isNotEmpty(str)) {
			CryptoUtils.desEncrpty(chStr, CryptoUtils.desEncrpty(ranStr, key));
		}
		return "";
	}

	/**
	 * 银行和BOSS之间的存在一个密钥和一个随机数。分别为masterKey和random。其中masterKey可以通过密钥更新交易进行更改，
	 * 而random则是与银行约定后不再变化。计算MAC的步骤如下：
	 * 用RANDOM做为数据源，masterKey做为加密的KEY，做一个单DES，得出的结果为MACKEY1；
	 * 从报文的交易包类型标志字段（Trans_Pac_Type
	 * ）开始，将参与的数据分成8个字节的小份，D1，D2，D3……Dn，如果报文不是8的倍数，后补16进制的0（0x00），补足8的倍数。
	 * 用8个字节的16进制0的初值D0和数据块D1，D2，……Dn进行异或，结果为8个字节的MACDATA；
	 * 用MACKEY1做单DES加密数据源MACDATA，得出结果为8字节的MAC
	 * 
	 * @param str
	 * @return
	 */
	public static String toStringByBytes(String str) {
		if (StringUtils.isEmpty(str)) {
			return "";
		}
		byte[] bs = str.getBytes();
		int j = 8 - bs.length % 8;
		int k = bs.length % 8 == 0 ? (bs.length / 8) : (bs.length / 8) + 1;
		byte[] bs1 = Arrays.copyOf(bs, k * 8);
		for (int i = 0; i < j; i++) {
			bs1[bs.length + i] = 0x00;
		}
		byte b1 = 0x00;
		;
		byte b2 = 0x00;
		byte b3 = 0x00;
		byte b4 = 0x00;
		byte b5 = 0x00;
		byte b6 = 0x00;
		byte b7 = 0x00;
		byte b8 = 0x00;
		for (int m = 0; m < k; m++) {
			b1 = (byte) ((byte) b1 ^ bs1[m * 8 + 0]);
			b2 = (byte) ((byte) b2 ^ bs1[m * 8 + 1]);
			b3 = (byte) ((byte) b3 ^ bs1[m * 8 + 2]);
			b4 = (byte) ((byte) b4 ^ bs1[m * 8 + 3]);
			b5 = (byte) ((byte) b5 ^ bs1[m * 8 + 4]);
			b6 = (byte) ((byte) b6 ^ bs1[m * 8 + 5]);
			b7 = (byte) ((byte) b7 ^ bs1[m * 8 + 6]);
			b8 = (byte) ((byte) b8 ^ bs1[m * 8 + 7]);
		}
		bs = new byte[8];
		// byte a = 0x00;
		// byte b = 0x01;
		// int c = a^b;
		bs[0] = b1;
		bs[1] = b2;
		bs[2] = b3;
		bs[3] = b4;
		bs[4] = b5;
		bs[5] = b6;
		bs[6] = b7;
		bs[7] = b8;
		String chStr = new String(bs);
		return chStr;
	}
	
	
	


	public static void fetchTlrAndBr(Context context) {
		context.setData(ParamKeys.BR, "01441131999");// 网点号
		context.setData("tlr", "ABIR148");
	}

	
	/**
	 * 将主机以及平台错误码转义为第三方错误码
	 * 
	 * @param s
	 * @param eupsBusTyp
	 * @return
	 * @throws CoreException
	 */
	public static String fetchThdCde(String s, String eupsBusTyp)
			throws CoreException {
		Properties pro = new Properties();
		try {
			pro.load(UtilsCnlty.class.getClassLoader().getResourceAsStream(
					"config/stream/bbipRspCode.properties"));
			Enumeration<Object> es = pro.keys();
			while (es.hasMoreElements()) {
				String key = (String)es.nextElement();
				String value = pro.getProperty(key);
				if (value.equals(s) && key.substring(0, 6).equals(eupsBusTyp)) {
					return key.substring(7);
				}
			}
		} catch (Exception e1) {

		}
		return "";
	}

	/**
	 * 将平台错误码解析为返回第三方的错误信息
	 * 
	 * @param s
	 * @return
	 * @throws CoreException
	 */
	public static String fetchThdMsg(String s) throws CoreException {
		Properties pro = new Properties();
		try {
			pro.load(UtilsCnlty.class.getClassLoader().getResourceAsStream(
					"config/msg/errors.properties"));
			String value = pro.getProperty(s);
			return value;
		} catch (Exception e) {
			// throw new CoreException(GDEUPSErrorCodes.READ_FLE_FAIL);
		}
		return "其他错误";
	}

	/**
	 * 移动代收费模糊查询 返回前端
	 * 
	 * @param context
	 */
	public static void setProperty(Context context) {
		context.setData("pageNo", "1");
		context.setData("varSiz", "3");
		context.setData("ttl", "柜员流水查询");
	}
	/**
	 * 时间转yyyyMMdd
	 * @param obj
	 * @return
	 */
	 public static String dateFormat(Object obj){
		 if(obj!=null){
			 String str=DateUtils.format((Date)obj, DateUtils.STYLE_yyyyMMdd);
			 return str;
		 }else{
			 return "";
		 }
	 }
	
	 
	 /**
	  * 返回给第三方流水
	  * @param sqn
	  * @return
	  */
	 public static String fetchMobbSqn(String sqn) {
		 if(StringUtils.isBlank(sqn)){
			 return "";
		 }else{
			 return DateUtils.format(new Date(), DateUtils.STYLE_MMdd)+sqn.substring(12,20);
		 }
	 }

}

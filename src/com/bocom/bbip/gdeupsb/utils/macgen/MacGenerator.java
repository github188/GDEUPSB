package com.bocom.bbip.gdeupsb.utils.macgen;

import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.utils.CryptoUtils;
import com.bocom.jump.bp.core.Context;

public class MacGenerator {

	private static final Log log = LogFactory.getLog(MacGenerator.class);

	/**
	 * 电力mac生成
	 * 
	 * @return
	 */
	// public static String eleGzMacGen(Context context){
	//
	// String Key="1111222233334444";
	// String traTyp=context.getData(GDParamKeys.GZ_ELE_TRA_TYP); //交易类型
	// if(GDConstants.GZ_ELE_TXN_TYP_HZ.equals(traTyp)){
	//
	//
	// }else if(GDConstants.GZ_ELE_TXN_TYP_JF.equals(traTyp)){
	//
	// }
	/**
	 * 广州电力md5生成
	 * 
	 * @param context
	 * @return
	 */
	public static String macGenDeal(Context context) {
		String cusAc = context.getData("cusAcEx2"); // 2域
		String thdTxnAmt = context.getData("amount4"); // 4域
		String eleBkNo = context.getData("eleBkNo32"); // 32域
		String eleThdSqn = context.getData("eleThdSqn37"); // 37域
		String thdRspCde = context.getData("thdRspCde"); // 39域
		String tTrmId = context.getData("tTrmId41"); // 41域
		String thdRgnNo100 = context.getData("thdRgnNo100"); // 100域

		String md5 = "";
		try {
			StringBuffer md5Block = new StringBuffer();
			md5Block.append(cusAc).append(thdTxnAmt).append(eleBkNo).append(eleThdSqn).append(thdRspCde).append(tTrmId).append(thdRgnNo100);
			// 工作密钥加入到末尾
			log.info(">>>>The beforeMd5Info is: :[" + md5Block + "]");
			md5 = CryptoUtils.md5(md5Block.toString().getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		log.info("经过md5加密过后，结果为:" + md5);
		return md5;

	}

}

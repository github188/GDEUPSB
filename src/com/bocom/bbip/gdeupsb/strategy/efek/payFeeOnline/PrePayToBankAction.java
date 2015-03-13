package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsThdTranCtlDetailRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class PrePayToBankAction implements Executable{
		private final static Log logger=LogFactory.getLog(PrePayToBankAction.class);
		@Autowired
		EupsThdTranCtlDetailRepository eupsThdTranCtlDetailRepository;
		/**
		 * 第三方单边记账处理前
		 */
		@Override
		public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("=========Start PrePayToBankAction");
			context.setData(GDParamKeys.TOTNUM, "1");
			context.setData(ParamKeys.TXN_DTE, DateUtils.parse(DateUtils.formatAsSimpleDate(new Date())));
			String time=DateUtils.format(new Date(),DateUtils.STYLE_TRANS_TIME);
			Date txnTme=DateUtils.parse(time);
			context.setData(ParamKeys.TXN_TME, txnTme);
			
			//TODO 待定  
			String ActFlg=(String)context.getData(ParamKeys.ACC_TYPE);
			//TODO 
			ActFlg="0";
			if("0".equals(ActFlg)){              //对公
				//GDContants定义常量
				context.setData(ParamKeys.TXN_CODE,"451240");
				context.setData("NamChk", "1");//		<Set>NamChk=1</Set> <!--测试不检查，生产考虑打开-->
				context.setData(ParamKeys.TXN_CHL, "0");//     <Set>VchFlg=0</Set> <!--渠道交易不检查-->
				//TODO
// 字段长度				context.setData(ParamKeys.BV_KIND,"000"); 凭证种类  字符长度
				context.setData(ParamKeys.BV_KIND,"00");
				
				context.setData(ParamKeys.BV_NO,"00000000");
				context.setData(ParamKeys.TXN_DAT,(Date)context.getData(ParamKeys.TXN_DTE));
				context.setData(ParamKeys.CCY_NO,Constants.EUPS_PAYMENT_TO_ACPS_CCY_CDE);
				context.setData(GDParamKeys.ACCMOD, "1");
				context.setData(ParamKeys.BAK_FLD1,"代扣电费");
			}else if("2".equals(ActFlg) || "4".equals(ActFlg)){                  //卡
				context.setData(ParamKeys.TXN_CODE,"471140");
				context.setData(ParamKeys.CHL_TYP, "L");  //<Set>CnlTyp=L</Set>
				context.setData("Mask", "9102");//					<Set>Mask=9102</Set>
				
				//TODO
//字段长度				context.setData(ParamKeys.BV_KIND,"000"); 凭证种类  字符长度
				context.setData(ParamKeys.BV_KIND,"00");
				
				context.setData(ParamKeys.BV_NO,"00000000");//  <Set>VchCod=00000000</Set>
				context.setData(ParamKeys.CCY_NO,Constants.EUPS_PAYMENT_TO_ACPS_CCY_CDE);

				context.setData(ParamKeys.PAY_MDE, "0");
				context.setData(GDParamKeys.CCYTYP,"1");
				context.setData(GDParamKeys.VCHCHK, "0");
				
				context.setData("CAgtNo","EFE9999999");  //清算单位协议号要改 
				
				context.setData("GthFlg", "N");//			<Set>GthFlg=N</Set>
			}
			//TODO xtnAmt
			double i=Double.parseDouble(context.getData(ParamKeys.TXN_AMT).toString());
			double d=i/100;
			DecimalFormat df=new DecimalFormat("#.00");
			BigDecimal txnAmt=new BigDecimal(df.format(d));
			context.setData(ParamKeys.TXN_AMT,txnAmt );
			context.setData(ParamKeys.BUS_TYP, "2");
		}
}

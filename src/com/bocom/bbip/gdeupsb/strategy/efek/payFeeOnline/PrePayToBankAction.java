package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

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
			
			String tmlNo=context.getData(ParamKeys.BBIP_TERMINAL_NO).toString().substring(0,6);
			context.setData(ParamKeys.TERMINAL_NO, tmlNo);
			//TODO 待定  
			String ActFlg=(String)context.getData(ParamKeys.ACC_TYPE);
			//TODO 
			ActFlg="0";
			// <Set>ActSqn=SUBSTR($InAcNo,14,5)</Set>　　 <Set>ActNod=SUBSTR($InAcNo,1,6)</Set>
			String ActNod=context.getData(ParamKeys.CUS_AC).toString().substring(1, 7);
			String ActSqn=context.getData(ParamKeys.CUS_AC).toString().substring(14, 19);
			if("0".equals(ActFlg)){              //对公
				//GDContants定义常量
				context.setData(ParamKeys.TXN_CODE,"451240");
				context.setData("NamChk", "1");//		<Set>NamChk=1</Set> <!--测试不检查，生产考虑打开-->
				context.setData(ParamKeys.TXN_CHL, "0");//     <Set>VchFlg=0</Set> <!--渠道交易不检查-->
				
// 字段长度				context.setData(ParamKeys.BV_KIND,"000"); 凭证种类  字符长度
				context.setData(ParamKeys.BV_KIND,"00");
				
				context.setData(ParamKeys.BV_NO,"00000000");
				context.setData(ParamKeys.TXN_DAT,(Date)context.getData(ParamKeys.TXN_DTE));
				context.setData(ParamKeys.CCY_NO,Constants.EUPS_PAYMENT_TO_ACPS_CCY_CDE);
				context.setData(GDParamKeys.ACCMOD, "1");
				context.setData("ActSqn",ActSqn);//	 <Set>ActSqn=SUBSTR($InAcNo,14,5)</Set>   InAcNo日间记账账号
				context.setData("ActNod",ActNod);//   <Set>ActNod=SUBSTR($InAcNo,1,6)</Set>
				context.setData(ParamKeys.BAK_FLD1,"代扣电费");
			}else if("2".equals(ActFlg) || "4".equals(ActFlg)){                  //卡
				context.setData(ParamKeys.TXN_CODE,"471140");
				context.setData(ParamKeys.CHL_TYP, "L");  //<Set>CnlTyp=L</Set>
				context.setData("Mask", "9102");//					<Set>Mask=9102</Set>
				
//字段长度				context.setData(ParamKeys.BV_KIND,"000"); 凭证种类  字符长度
				context.setData(ParamKeys.BV_KIND,"00");
				
				context.setData(ParamKeys.BV_NO,"00000000");//  <Set>VchCod=00000000</Set>
				context.setData(ParamKeys.CCY_NO,Constants.EUPS_PAYMENT_TO_ACPS_CCY_CDE);

				context.setData(ParamKeys.PAY_MDE, "0");
				context.setData(GDParamKeys.CCYTYP,"1");
				context.setData(GDParamKeys.VCHCHK, "0");
				
				context.setData("ActSeq",ActSqn);  //	<Set>ActSeq=SUBSTR($InAcNo,14,5)</Set>   InAcNo日间记账账号
				context.setData("CAgtNo","EFE9999999");  //清算单位协议号要改 
				
				context.setData("GthFlg", "N");//			<Set>GthFlg=N</Set>
			}
			
			context.setData(ParamKeys.TXN_TLR, "4430");
		}
}

package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class PrePayFeeAction implements Executable{
	private final static Log logger=LogFactory.getLog(PrePayFeeAction.class);

	/**
	 * 记账前策略处理
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("============Start  PrePayFeeAction");
					String ActFlg=(String)context.getData(ParamKeys.ACC_TYPE);          //银行内部账务类型
					
					
					//TODO InAcNo 不确定  日间记账账号
					// <Set>ActSqn=SUBSTR($InAcNo,14,5)</Set>　　 <Set>ActNod=SUBSTR($InAcNo,1,6)</Set>
					String ActNod=context.getData(ParamKeys.CUS_AC).toString().substring(1, 6);
					String ActSqn=context.getData(ParamKeys.CUS_AC).toString().substring(14, 19);
					if("0".equals(ActFlg)){              //对公
						//需要GDContants定义常量
						context.setData(ParamKeys.THD_TXN_CDE,"451240");
						
						context.setData(GDParamKeys.CCYTYP, "EFE999999999");
						context.setData("NamChk", "1");//		<Set>NamChk=1</Set> <!--测试不检查，生产考虑打开-->
						context.setData(ParamKeys.TXN_CHL, "0");//     <Set>VchFlg=0</Set> <!--渠道交易不检查-->
						
// 字段长度				context.setData(ParamKeys.BV_KIND,"000"); 凭证种类  字符长度
						context.setData(ParamKeys.BV_KIND,"00");
						
						context.setData(ParamKeys.BV_NO,"00000000");
						context.setData(ParamKeys.CCY_NO,Constants.EUPS_PAYMENT_TO_ACPS_CCY_CDE);
						
						context.setData(GDParamKeys.ACCMOD, "1");
						context.setData("ActSqn",ActSqn);//	 <Set>ActSqn=SUBSTR($InAcNo,14,5)</Set>   InAcNo日间记账账号
						context.setData("ActNod",ActNod);//   <Set>ActNod=SUBSTR($InAcNo,1,6)</Set>
						context.setData(ParamKeys.BAK_FLD1,"代扣电费");
					}else if("2".equals(ActFlg) ||  "4".equals(ActFlg)){                  //卡  存折
						context.setData(ParamKeys.TXN_CODE,"471140");
						context.setData(ParamKeys.CHL_TYP, "L");  //<Set>CnlTyp=L</Set>
						context.setData("Mask", "9102");//					<Set>Mask=9102</Set>
						
// 字段长度				context.setData(ParamKeys.BV_KIND,"000"); 凭证种类  字符长度
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
			}
}

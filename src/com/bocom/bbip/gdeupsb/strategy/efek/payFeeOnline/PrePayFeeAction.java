package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class PrePayFeeAction implements Executable{
	private final static Log logger=LogFactory.getLog(PrePayFeeAction.class);

	/**
	 * 联机单笔记账 记账前特色处理 
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("============Start  PrePayFeeAction");
					String ActFlg=(String)context.getData(ParamKeys.PAY_MDE);          //银行内部账务类型
					
					if("0".equals(ActFlg)){              //对公
						context.setData(ParamKeys.THD_TXN_CDE,"451240");
						context.setData(ParamKeys.BAK_FLD1,"代扣电费");
					}else if("2".equals(ActFlg) ||  "4".equals(ActFlg)){                  //卡  存折
						context.setData(ParamKeys.TXN_CODE,"471140");
					}else if("5".equals(ActFlg)){
						context.setData("acTyp", "02");
					}else{
						throw new CoreException("没有该付款方式");
					}
					context.setData(ParamKeys.CUS_NME, context.getData("CusNme"));
					context.setData(ParamKeys.BUS_TYP, "2");
					
					String str=(String)context.getData("company");
					if(str !=null){
						if(str.length()>4){
							context.setData("bakFld2", str.substring(0,4));
						}else{
							context.setData("bakFld2", str);
						}
					}
			}
}

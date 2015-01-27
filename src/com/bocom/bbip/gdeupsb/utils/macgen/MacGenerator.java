package com.bocom.bbip.gdeupsb.utils.macgen;

import java.util.Map;

import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;

public class MacGenerator {
	
	/**
	 * 电力mac生成
	 * @return
	 */
	public static String eleGzMacGen(Context context){
	
		String Key="1111222233334444";
		String traTyp=context.getData(GDParamKeys.GZ_ELE_TRA_TYP);  //交易类型
		if(GDConstants.GZ_ELE_TXN_TYP_HZ.equals(traTyp)){
			
			
		}else if(GDConstants.GZ_ELE_TXN_TYP_JF.equals(traTyp)){
			
		}
		
		
		return null;
	}

}

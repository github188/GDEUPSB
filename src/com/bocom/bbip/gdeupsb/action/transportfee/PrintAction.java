package com.bocom.bbip.gdeupsb.action.transportfee;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PrintAction extends BaseAction{

	public void execute(Context ctx) throws CoreRuntimeException,CoreException{
		ctx.setData(GDParamKeys.OINV_NO, ctx.getData(GDParamKeys.INV_NO));
		//TODO:待考虑！！！！
		if(!ctx.getData(GDParamKeys.OINV_NO).toString().trim().equals(ctx.getData(GDParamKeys.INV_NO).toString().trim())){
			ctx.setData(ParamKeys.RSP_MSG, "发票号码改变，请补打");
			throw new CoreRuntimeException(GDErrorCodes.EUPS_INVOIC_NO_ERROR);
		}
//		 <Set>FilNam=STRCAT(INVO,$TlrId,00)</Set>
//         <Exec func="PUB:GenerateReport">
//            <Arg name="ObjFil"  value="STRCAT($TSDir,$FilNam)"/>
//            <Arg name="FmtFil"  value="STRCAT(etc/,BRBFINV_RPT.XML)"/>
//            <Arg name="OLogNo"  value="$OLogNo"/>
//         </Exec>
         
	}
}

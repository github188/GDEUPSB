package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspNpManag;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspZzManag;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspNpManagRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspZzManagRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class ZZAction extends BaseAction{
	private final static Log log = LogFactory.getLog(ZZAction.class);
	
	@Autowired
	GDEupsbTrspZzManagRepository gdEupsbTrspZzManagRepository;
	
	@Autowired
	GDEupsbTrspNpManagRepository gdEupsbTrspNpManagRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("ZZAction start......");

		ctx.setData(GDParamKeys.NOD_NO, "123");
		
		GDEupsbTrspZzManag gdEupsbTrspZzManag = new GDEupsbTrspZzManag();
		gdEupsbTrspZzManag.setFlg("0");
		gdEupsbTrspZzManag.setNodNo(ctx.getData(GDParamKeys.NOD_NO).toString());
		gdEupsbTrspZzManag.setZzDat(ctx.getData(GDParamKeys.ZZ_DAT).toString());
		List<GDEupsbTrspZzManag> zzManagList = gdEupsbTrspZzManagRepository.find(gdEupsbTrspZzManag);
		if(!CollectionUtils.isEmpty(zzManagList)){
			ctx.setData(ParamKeys.RSP_MSG, "该日期已轧账，不能重复轧账！");
		}else{
			GDEupsbTrspNpManag gdEupsbTrspNpManag = new GDEupsbTrspNpManag();
			gdEupsbTrspNpManag.setPrtDat(DateUtils.parse(ctx.getData(GDParamKeys.ZZ_DAT).toString()));
			gdEupsbTrspNpManag.setNodNo(ctx.getData(GDParamKeys.NOD_NO).toString());
			List<Integer> sum1 = gdEupsbTrspNpManagRepository.findCountByStatus(gdEupsbTrspNpManag);
			String suma=sum1.get(0)+"";
			gdEupsbTrspNpManag.setStatus("1");
			List<Integer> sum2 = gdEupsbTrspNpManagRepository.findCount(gdEupsbTrspNpManag);
			String sumb = sum2.get(0)+"";

			if(ctx.getData("amo1").equals(suma)&&ctx.getData("amo2").equals(sumb)){
				ctx.setData(ParamKeys.RSP_MSG, "日终轧账成功");
				gdEupsbTrspZzManag.setNodNo(ctx.getData(GDParamKeys.NOD_NO).toString());
				gdEupsbTrspZzManag.setZzDat(ctx.getData(GDParamKeys.ZZ_DAT).toString());
				gdEupsbTrspZzManag.setFlg("0");
				gdEupsbTrspZzManagRepository.insert(gdEupsbTrspZzManag);
			}else{

				if( (!ctx.getData("amo1").equals(suma)) && ctx.getData("amo2").equals(sumb)){
					ctx.setData(ParamKeys.RSP_MSG, "使用数量不平，无法轧账！");
				}else if(ctx.getData("amo1").equals(suma) && (!ctx.getData("amo2").equals(sumb))){
					ctx.setData(ParamKeys.RSP_MSG, "作废数量不平，无法轧账！");
				}else {
					ctx.setData(ParamKeys.RSP_MSG, "使用数量和作废数量都不平，无法轧账！");
				}
			}
		}
	}
}

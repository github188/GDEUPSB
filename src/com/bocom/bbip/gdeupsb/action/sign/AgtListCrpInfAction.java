package com.bocom.bbip.gdeupsb.action.sign;

import java.util.HashMap;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 代收付机构资料浏览
 * 
 * @author qc.w
 * 
 */
public class AgtListCrpInfAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("AgtListCrpInfAction start!..");

		// 因为listCrpInf 接口要求交换区域和机构代码，所以原多页查询无法实现，这两个字段为必输
		String exgAra = context.getData("cityCd"); // 交换区域
		String eprCde = context.getData("orgCod"); // 机构代码
		String eprNme = context.getData("orgNam"); // 机构名称

		Map<String, Object> inpara = new HashMap<String, Object>();
		inpara.put("exgAra", exgAra); // 交换区域
		inpara.put("eprCde", eprCde); // 机构代码
		inpara.put("eprNme", eprNme); // 机构名称

		Result dsResult = get(BGSPServiceAccessObject.class).callServiceFlatting("listCrpInf", inpara);
		Map<String, Object> resultMap = dsResult.getPayload();

		context.setData("crpInfMap", resultMap.get("chpsCrpInfList"));
	}

}

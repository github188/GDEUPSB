package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.sqlmap.SqlMap;
/**
 * 自助通发票使用情况明细表查询打印
 * @author hefengwen
 *
 */
public class AtmInvDtlServiceActionPROF00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(AtmInvDtlServiceActionPROF00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("AtmInvDtlServiceActionPROF00 start ... ...");
		String invTyp = context.getData("invTyp");
		String begDat = context.getData("begDat");
		String endDat = context.getData("endDat");
		String qryNod = context.getData("qryNod");
		String prtFlg = context.getData("prtFlg");
		logger.info("invTyp["+invTyp+"]begDat["+begDat+"]endDat["+endDat+"]qryNod["+qryNod+"]prtFlg["+prtFlg+"]");
		List<Map<String,Object>> list = ((SqlMap)get("sqlMap")).queryForList("prof00.atmInvDtl", context.getDataMap());
		if(CollectionUtils.isEmpty(list)){
			logger.error("不存在记录");
			throw new CoreException("");
		}
		for(Map<String,Object> map:list){
			map.put("actDat", map.get("ACT_DAT"));
			map.put("oprTlr", map.get("TLR_ID"));
			map.put("invTyp", map.get("INV_TYP"));
			map.put("inBegNo", map.get("IV_BEG_NO"));
			map.put("ivEndNo", map.get("IV_END_NO"));
			map.put("tolNum", map.get("TOL_NUM"));
			map.put("regTlr", map.get("REG_TLR"));
			map.put("lodNum", map.get("LOD_NUM"));
			map.put("useNum", map.get("USE_NUM"));
			map.put("clrNum", map.get("CLR_NUM"));
			map.put("invNum", map.get("INV_NUM"));
		}
		context.setData("rspList", list);
		logger.info("AtmInvDtlServiceActionPROF00 end ... ...");
	}

}

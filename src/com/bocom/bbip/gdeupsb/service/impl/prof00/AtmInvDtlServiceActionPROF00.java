package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.thd.org.apache.commons.lang.math.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.sqlmap.SqlMap;
import com.bocom.jump.bp.service.sqlmap.impl.SqlMapImpl;
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
		String func = context.getData("func");
		logger.info("func["+func+"]");
		if("1".equals(func)){//自助通发票使用情况明细表
			invDtl(context);
		}else if("2".equals(func)){//自助通发票使用情况汇总表
			invSum(context);
		}else{//企业发票使用情况明细表
			qyInvSum(context);
		}
		logger.info("AtmInvDtlServiceActionPROF00 end ... ...");
	}
	/**
	 * 自助通发票使用情况明细表
	 * @param context
	 * @throws CoreException 
	 */
	private void invDtl(Context context) throws CoreException{
		logger.info("AtmInvDtlServiceActionPROF00 invDtl start ... ...");
		String invTyp = context.getData("invTyp");
		String begDat = context.getData("begDat");
		String endDat = context.getData("endDat");
		String qryNod = context.getData("qryNod");
		String prtFlg = context.getData("prtFlg");
		logger.info("invTyp["+invTyp+"]begDat["+begDat+"]endDat["+endDat+"]qryNod["+qryNod+"]prtFlg["+prtFlg+"]");
		if("1".equals(prtFlg)){//打印
			
		}else{//查询
			List<Map<String,Object>> list = ((SqlMap)get("sqlMap")).queryForList("prof00.atmInvDtl", context.getDataMap());
			if(CollectionUtils.isEmpty(list)){
				logger.error("不存在记录");
				throw new CoreException("");
			}
			for(Map<String,Object> map:list){
				map.put("actDat", map.get("ACT_DAT"));
				map.put("oprTlr", map.get("TLR_ID"));
				map.put("invTyp", map.get("INV_TYP"));
				map.put("ivBegNo", map.get("IV_BEG_NO"));
				map.put("ivEndNo", map.get("IV_END_NO"));
				map.put("tolNum", map.get("TOL_NUM"));
				map.put("regTlr", map.get("REG_TLR"));
				map.put("lodNum", map.get("LOD_NUM"));
				map.put("useNum", map.get("USE_NUM"));
				map.put("clrNum", map.get("CLR_NUM"));
				map.put("invNum", map.get("INV_NUM"));
			}
			context.setData("tol", list.size());
			context.setData("rec1", list);
		}
		
		logger.info("AtmInvDtlServiceActionPROF00 invDtl end ... ...");
	}
	/**
	 * 自助通发票使用情况汇总表
	 * @param context
	 * @throws CoreException 
	 */
	private void invSum(Context context) throws CoreException{
		logger.info("AtmInvDtlServiceActionPROF00 invSum start ... ...");
		String invTyp = context.getData("invTyp");
		String begDat = context.getData("begDat");
		String endDat = context.getData("endDat");
		String qryNod = context.getData("qryNod");
		String prtFlg = context.getData("prtFlg");
		logger.info("invTyp["+invTyp+"]begDat["+begDat+"]endDat["+endDat+"]qryNod["+qryNod+"]prtFlg["+prtFlg+"]");
		if("1".equals(prtFlg)){//打印
			
		}else{//查询
			List<Map<String,Object>> list = ((SqlMap)get("sqlMap")).queryForList("prof00.atmInvSum", context.getDataMap());
//			List<Map<String,Object>> list = get(SqlMapImpl.class).queryForList("prof00.atmInvSum", context.getDataMap());
			if(CollectionUtils.isEmpty(list)){
				logger.error("不存在记录");
				throw new CoreException("不存在记录");
			}
			for(Map<String,Object> map:list){
				map.put("nodno", map.get("NODNO"));
				map.put("oprTlr", map.get("TLR_ID"));
				map.put("invTyp", map.get("INV_TYP"));
				map.put("lodNum", map.get("LOD_NUM"));
				map.put("useNum", map.get("USE_NUM"));
				map.put("clrNum", map.get("CLR_NUM"));
				String lodNum = map.get("lodNum")==null?"0":map.get("lodNum").toString();
				String useNum = map.get("useNum")==null?"0":map.get("useNum").toString();
				String clrNum = map.get("clrNum")==null?"0":map.get("clrNum").toString();
				map.put("invNum", NumberUtils.createInteger(lodNum)-NumberUtils.createInteger(useNum)-NumberUtils.createInteger(clrNum));
			}
			context.setData("tol", list.size());
			context.setData("rec2", list);
		}
		
		logger.info("AtmInvDtlServiceActionPROF00 invSum end ... ...");
	}
	/**
	 * 企业发票使用情况明细表
	 * @param context
	 * @throws CoreException 
	 */
	private void qyInvSum(Context context) throws CoreException{
		logger.info("AtmInvDtlServiceActionPROF00 qyInvSum start ... ...");
		String invTyp = context.getData("invTyp");
		String begDat = context.getData("begDat");
		String endDat = context.getData("endDat");
		String qryNod = context.getData("qryNod");
		String prtFlg = context.getData("prtFlg");
		logger.info("invTyp["+invTyp+"]begDat["+begDat+"]endDat["+endDat+"]qryNod["+qryNod+"]prtFlg["+prtFlg+"]");
		if("1".equals(prtFlg)){//打印
			
		}else{//查询
			List<Map<String,Object>> list = ((SqlMap)get("sqlMap")).queryForList("prof00.qyInvSum", context.getDataMap());
			if(CollectionUtils.isEmpty(list)){
				logger.error("不存在记录");
				throw new CoreException("不存在记录");
			}
			for(Map<String,Object> map:list){
				map.put("qyNo", map.get("QY_NO"));
				map.put("useNum", map.get("USE_NUM"));
			}
			context.setData("tol", list.size());
			context.setData("rec3", list);
		}
		
		logger.info("AtmInvDtlServiceActionPROF00 qyInvSum end ... ...");
	}

}

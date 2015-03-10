package com.bocom.bbip.gdeupsb.service.impl.lotr01;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotSysCfg;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotSysCfgRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.sqlmap.SqlMap;
/**
 * 定投返奖操作
 * @author hefengwen
 *
 */
public class ProPlanPayServiceActionLOTR01 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ProPlanPayServiceActionLOTR01.class);
	
	private String dealId = "141";
	/**
	 * 初始化操作，获取系统配置、福彩时间、奖期信息
	 * @param context
	 * @throws CoreException 
	 */
	public void init(Context context) throws CoreException{
		logger.info("ProPlanPayServiceActionLOTR01 init start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		/*1.获取系统配置
		 * 根据dealId查询系统配置表(代收单位编号、代发单位编号、华祥对公账号、维护人员手机)
		 * 根据代收单位编号查询代收内部户(可配置在平台参数表中)
		 * 根据代发单位编号查询代发内部户(可配置在平台参数表中)
		 */
		GdLotSysCfg gdLotSysCfg = new GdLotSysCfg();
		gdLotSysCfg.setDealId(dealId);
		List<GdLotSysCfg> gdLotSysCfgs = get(GdLotSysCfgRepository.class).find(gdLotSysCfg);
		if(CollectionUtils.isEmpty(gdLotSysCfgs)){
			//系统配置信息不存在
			logger.error("福彩系统配置信息不存在!");
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_14_ERROR);
		}
		gdLotSysCfg = gdLotSysCfgs.get(0);
		//查询代收内部户
		Map<String,Object> m1 = ((SqlMap)get("sqlMap")).queryForObject("lotr01.findDsActNo", gdLotSysCfg);
		logger.info("dsActNo["+m1.get("DSACTNO")+"]");
		if(m1==null||m1.get("DSACTNO")==null){
			//代收内部户不存在
			logger.error("代收内部户不存在!");
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_15_ERROR);
		}
		//查询代发内部户
		Map<String,Object> m2 = ((SqlMap)get("sqlMap")).queryForObject("lotr01.findDfActNo", gdLotSysCfg);
		if(m2==null||m2.get("DFACTNO")==null){
			//代发内部户不存在
			logger.error("代发内部户不存在!");
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_16_ERROR);
		}
		String dsActNo = (String) m1.get("DSACTNO");
		String dfActNo = (String) m2.get("DFACTNO");
		logger.info("dsActNo["+dsActNo+"]dfActNo["+dfActNo+"]");
		
		context.setData("gdLotSysCfg", gdLotSysCfg);
		context.setData("dsActNo", dsActNo);
		context.setData("dfActNo", dfActNo);
		/*2.计算时差，暂时默认时间差为0
		 * 
		 */
		String sysTim = DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss);
		context.setData("sysTim", sysTim);
		//查询未返奖奖期信息
		GdLotDrwTbl gdLotDrwTbl = new GdLotDrwTbl();
		gdLotDrwTbl.setCshStr(sysTim);
		List<GdLotDrwTbl> gdLotDrwTbls = get(GdLotDrwTblRepository.class).query(gdLotDrwTbl);
		if(CollectionUtils.isEmpty(gdLotDrwTbls)){
			//不存在未返奖奖期
			logger.error("不存在未返奖奖期!");
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_19_ERROR);
		}
		context.setData("gdLotDrwTbls", gdLotDrwTbls);
		logger.info("未返奖奖期数量["+gdLotDrwTbls.size()+"]");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("ProPlanPayServiceActionLOTR01 init end ... ...");
	}
	/**
	 * 循环处理奖期信息
	 * @param context
	 * @throws CoreException 
	 * @throws CoreRuntimeException 
	 */
	public void proPlanPay(Context context) throws CoreRuntimeException, CoreException{
		logger.info("ProPlanPayServiceActionLOTR01 proPlanPay start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		List<GdLotDrwTbl> gdLotDrwTbls = context.getData("gdLotDrwTbls");
		for(GdLotDrwTbl gdLotDrwTbl:gdLotDrwTbls){
			context.setData("gdLotDrwTbl", gdLotDrwTbl);
			logger.info("开始执行奖期gameId["+gdLotDrwTbl.getGameId()+"]drawId["+gdLotDrwTbl.getDrawId()+"]kenoId["+gdLotDrwTbl.getKenoId()+"]");
			
			context = get(BBIPPublicService.class).synExecute("eups.payPlan", context);
			
			logger.info("结束执行奖期gameId["+gdLotDrwTbl.getGameId()+"]drawId["+gdLotDrwTbl.getDrawId()+"]kenoId["+gdLotDrwTbl.getKenoId()+"]");
		}
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("ProPlanPayServiceActionLOTR01 proPlanPay end ... ...");
	}
	/**
	 *计算返奖总金额与购彩总金额，更新扎差金额
	 * @param context
	 */
	public void updAmt1(Context context){
		logger.info("ProPlanPayServiceActionLOTR01 updAmt1 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = new GdLotDrwTbl();
		gdLotDrwTbl.setKenoId("AAAAA");
		gdLotDrwTbl.setPrzAmt("");
		gdLotDrwTbl.setFlwCtl("10");
		List<GdLotDrwTbl> gdLotDrwTbls = get(GdLotDrwTblRepository.class).queryKeno(gdLotDrwTbl);
		for(GdLotDrwTbl drw:gdLotDrwTbls){
			drw.setKenoId("AAAAA");
			drw.setPrzAmt("");
			drw.setFlwCtl("10");
			List<GdLotDrwTbl> drws = get(GdLotDrwTblRepository.class).queryKenoCnt(drw);
			if(CollectionUtils.isEmpty(drws)){
				//该期的所有Keno期都已经返奖完成，更新AAAAA记录的汇总信息
				Map<String,Object> map = get(GdLotDrwTblRepository.class).sumPrzDrw(gdLotDrwTbl);
				context.setData("przSumAmt", map.get("PRZSUMAMT"));
				context.setData("gameId", drw.getGameId());
				context.setData("drawId", drw.getDrawId());
				get(GdLotDrwTblRepository.class).updDrwPrzInf(context.getDataMap());
			}
		}
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("ProPlanPayServiceActionLOTR01 updAmt1 end ... ...");
	}
	/**
	 * 查询未汇总返奖的记录，更新汇总返奖总金额
	 * @param context
	 */
	public void updAmt2(Context context){
		logger.info("ProPlanPayServiceActionLOTR01 updAmt2 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("ProPlanPayServiceActionLOTR01 updAmt2 end ... ...");
	}
	/**
	 * 查询符合条件的未扎差记录，更新扎差
	 * @param context
	 */
	public void updAmt3(Context context){
		logger.info("ProPlanPayServiceActionLOTR01 updAmt3 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		List<GdLotDrwTbl> gdLotDrwTbls = get(GdLotDrwTblRepository.class).calcLotDifAmt(context.getDataMap());
		for(GdLotDrwTbl gdLotDrwTbl:gdLotDrwTbls){
			String totAmt = gdLotDrwTbl.getTotAmt();
			String przAmt = gdLotDrwTbl.getPrzAmt();
			String difFlg = null;
			BigDecimal difAmt = null;
			if(totAmt.compareTo(przAmt)>=0){//借方，购彩总金额大于返奖总金额
				difFlg = "1";
				difAmt = new BigDecimal(totAmt).subtract(new BigDecimal(przAmt));
			}else{
				difFlg = "0";
				difAmt = new BigDecimal(przAmt).subtract(new BigDecimal(totAmt));
			}
			gdLotDrwTbl.setDifAmt(difAmt.toString());
			gdLotDrwTbl.setDifFlg(difFlg);
			get(GdLotDrwTblRepository.class).updLotDifAmt(gdLotDrwTbl);
		}
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("ProPlanPayServiceActionLOTR01 updAmt3 end ... ...");
	}
	/**
	 * 结束操作方法
	 * @param context
	 */
	public void finalize(Context context){
		logger.info("ProPlanPayServiceActionLOTR01 finalize start ... ...");
		
		logger.info("ProPlanPayServiceActionLOTR01 finalize end ... ...");
	}

}

package com.bocom.bbip.gdeupsb.service.impl.lotr01;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.action.lot.CommonLotAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdLotAwdDtl;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotPrzCtl;
import com.bocom.bbip.gdeupsb.entity.GdLotPrzDtl;
import com.bocom.bbip.gdeupsb.entity.GdLotPrzInf;
import com.bocom.bbip.gdeupsb.entity.GdLotSysCfg;
import com.bocom.bbip.gdeupsb.entity.GdLotTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GdLotAwdDtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotPrzCtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotPrzDtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotPrzInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Transport;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * 定投返奖：根据奖期执行返奖操作 
 * @author hefengwen
 *
 */
public class PayPlanServiceActionLOTR01 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(PayPlanServiceActionLOTR01.class);
	/**
	 * 查询奖期信息流程状态
	 * @param context
	 */
	public void stsChkStep(Context context){
		logger.info("PayPlanServiceActionLOTR01 stsChkStep start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//0
		context.setData("flwCtl",flwCtl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 stsChkStep end ... ...");
	}
	/**
	 * 返奖流程初始状态，设置FLWCTL=1
	 * @throws CoreException 
	 */
	public void initStep(Context context){
		logger.info("PayPlanServiceActionLOTR01 initStep start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//0
		gdLotDrwTbl.setFlwCtl(""+(Integer.parseInt(flwCtl)+1));
		context.setData("flwCtl",gdLotDrwTbl.getFlwCtl());
		get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		//测试代码
//		if(gdLotDrwTbl.getDrawId().equals("1488")){
//			context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//		}
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 initStep end ... ...");
	}
	/**
	 * 第1步 获取开奖公告信息
	 * @param context
	 * @throws CoreException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void step1(Context context) throws CoreException{
		logger.info("PayPlanServiceActionLOTR01 step1 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//1
		
		GdLotPrzInf gdLotPrzInf = new GdLotPrzInf();
		gdLotPrzInf.setGameId(gdLotDrwTbl.getGameId());
		gdLotPrzInf.setDrawId(gdLotDrwTbl.getDrawId());
		gdLotPrzInf.setKenoId(gdLotDrwTbl.getKenoId());
		List<GdLotPrzInf> gdLotPrzInfs = get(GdLotPrzInfRepository.class).find(gdLotPrzInf);
		if(CollectionUtils.isEmpty(gdLotPrzInfs)){
			//TODO:无记录，从福彩中心下载开奖公告
			String action = "236";
			String dealer_id = ((GdLotSysCfg)context.getData("gdLotSysCfg")).getDealId();//运营商ID
			context.setData("action", action);
			context.setData("dealer_id", dealer_id);
			
			context.setData("gameId", gdLotDrwTbl.getGameId());//设置游戏编号
			context.setData("drawId", gdLotDrwTbl.getDrawId());
			context.setData("kenoId", gdLotDrwTbl.getKenoId());
			
			Transport ts = context.getService("STHDLOT1");
			Map<String,Object> thdReturnMessage = null;//开奖公告下载
			try {
				thdReturnMessage = (Map<String, Object>) ts.submit(context.getDataMap(), context);
				context.setDataMap(thdReturnMessage);
//				context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
			} catch (CommunicationException e1) {
				e1.printStackTrace();
				logger.error("奖期信息下载失败1");
				throw new CoreException("奖期信息下载失败1");
			} catch (JumpException e1) {
				e1.printStackTrace();
				logger.error("奖期信息下载失败2");
				throw new CoreException("奖期信息下载失败2");
			}
			
			logger.info("thdReturnMessage["+thdReturnMessage+"]");
			logger.info("state["+context.getState()+"]");
			
			CommThdRspCdeAction commThdRspCdeAction = new CommThdRspCdeAction();
			String responseCode = commThdRspCdeAction.getThdRspCde(thdReturnMessage, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
			if(responseCode.equals(Constants.RESPONSE_CODE_SUCC)){
				List<Map<String,Object>> prizeItems = context.getData("prize");
				for(Map<String,Object> prizeItem:prizeItems){
					List<Map<String,Object>> classes = (List<Map<String, Object>>) prizeItem.get("clses");
					for(Map<String,Object> c:classes){
						gdLotPrzInf.setGameNm(this.trim(context.getData("gameNm")));
						gdLotPrzInf.setDrawNm(this.trim(context.getData("drawNm")));
						gdLotPrzInf.setKenoNm(this.trim(context.getData("kenoNm")));
						gdLotPrzInf.setStrTim(this.trim(context.getData("strTim")));
						gdLotPrzInf.setStpTim(this.trim(context.getData("stpTim")));
						gdLotPrzInf.setTotPrz(this.trim(context.getData("totPrz")));
						gdLotPrzInf.setJacPot(this.trim(context.getData("jacPot")));
						gdLotPrzInf.setOpnTot(this.trim(context.getData("opnTot")));
						gdLotPrzInf.setOpnNum(this.trim(prizeItem.get("opnNum")));
						gdLotPrzInf.setBonCod(this.trim(prizeItem.get("bonCod")));
						gdLotPrzInf.setClsNum(this.trim(prizeItem.get("clsNum")));
						gdLotPrzInf.setClsNam(this.trim(c.get("clsNam")));
						gdLotPrzInf.setBonAmt(this.trim(c.get("bonAmt")));
						gdLotPrzInf.setBonNum(this.trim(c.get("bonNum")));
						get(GdLotPrzInfRepository.class).insert(gdLotPrzInf);
					}
				}
			}else{
				//TODO:第三方返回失败，发送短信给维护人员
				String message = "银福通系统游戏编号"+gdLotDrwTbl.getGameId()+"期号"+gdLotDrwTbl.getDrawId()+"小期号"+gdLotDrwTbl.getKenoId()+"定时返奖交易第1步，新增开奖公告信息失败，定投执行程序退出，请检查！";
				
				throw new CoreException(GDErrorCodes.EUPS_LOTR01_09_ERROR);
			}
		}
		gdLotDrwTbl.setFlwCtl(""+(Integer.parseInt(flwCtl)+1));
		context.setData("flwCtl",gdLotDrwTbl.getFlwCtl());
		get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step1 end ... ...");
	}
	/**
	 * 更新奖期状态信息
	 * @param context
	 */
	public void step2(Context context){
		logger.info("PayPlanServiceActionLOTR01 step2 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//2
		
		
		gdLotDrwTbl.setFlwCtl(""+(Integer.parseInt(flwCtl)+1));
		context.setData("flwCtl",gdLotDrwTbl.getFlwCtl());
		get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step2 end ... ...");
	}
	/**
	 * 获取中奖记录明细信息
	 * @param context
	 * @throws CoreException 
	 */
	public void step3(Context context) throws CoreException{
		logger.info("PayPlanServiceActionLOTR01 step3 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//3
		
//		get(CommonLotAction.class).downloadFile(context, "2", gdLotDrwTbl.getGameId(), gdLotDrwTbl.getDrawId());
		GdLotPrzCtl gdLotPrzCtl = new GdLotPrzCtl();
		gdLotPrzCtl.setGameId(gdLotDrwTbl.getGameId());
		gdLotPrzCtl.setDrawId(gdLotDrwTbl.getDrawId());
		gdLotPrzCtl.setKenoId(gdLotDrwTbl.getKenoId());
		List<GdLotPrzCtl> gdLotPrzCtls = get(GdLotPrzCtlRepository.class).find(gdLotPrzCtl);
		if(CollectionUtils.isEmpty(gdLotPrzCtls)){//无记录，向福彩中心下载中奖信息
			if("7".equals(gdLotPrzCtl.getGameId())){//快乐十分
				String action = "240";
				String dealer_id = ((GdLotSysCfg)context.getData("gdLotSysCfg")).getDealId();//运营商ID
				context.setData("action", action);
				context.setData("dealer_id", dealer_id);
				
				context.setData("gameId", gdLotDrwTbl.getGameId());//设置游戏编号
				context.setData("drawId", gdLotDrwTbl.getDrawId());
				context.setData("kenoId", gdLotDrwTbl.getKenoId());
				
				Transport ts = context.getService("STHDLOT1");
				Map<String,Object> thdReturnMessage = null;
				try {
					thdReturnMessage = (Map<String, Object>) ts.submit(context.getDataMap(), context);
					context.setDataMap(thdReturnMessage);
//					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
				} catch (CommunicationException e1) {
					e1.printStackTrace();
					logger.error("下载中奖信息失败1");
					throw new CoreException("下载中奖信息失败1");
				} catch (JumpException e1) {
					e1.printStackTrace();
					logger.error("下载中奖信息失败2");
					throw new CoreException("下载中奖信息失败2");
				}
				
				logger.info("thdReturnMessage["+thdReturnMessage+"]");
				logger.info("state["+context.getState()+"]");
				
				CommThdRspCdeAction commThdRspCdeAction = new CommThdRspCdeAction();
				String responseCode = commThdRspCdeAction.getThdRspCde(thdReturnMessage, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
				if(responseCode.equals(Constants.RESPONSE_CODE_SUCC)){
					List<Map<String,Object>> items = context.getData("bonusItems");
					for(Map<String,Object> item:items){
						//新增中奖记录控制表
						gdLotPrzCtl.setCipher(this.trim(item.get("cipher")));
						gdLotPrzCtl.setBigBon(this.trim(item.get("bigBon")));
						gdLotPrzCtl.setTotPrz(this.trim(item.get("totPrz")));
						gdLotPrzCtl.setTxnlog(this.trim(item.get("txnLog")));
						gdLotPrzCtl.settLogNo(this.trim(item.get("tLogNo")));
						gdLotPrzCtl.setTermId(this.trim(item.get("termID")));
						get(GdLotPrzCtlRepository.class).insert(gdLotPrzCtl);
						
						List<Map<String,Object>> nodes = (List<Map<String, Object>>) item.get("nodes");
						for(Map<String,Object> node:nodes){
							GdLotPrzDtl gdLotPrzDtl = new GdLotPrzDtl();
							
							gdLotPrzDtl.setGameId(gdLotPrzCtl.getGameId());
							gdLotPrzDtl.setDrawId(gdLotPrzCtl.getDrawId());
							gdLotPrzDtl.setKenoId(gdLotPrzCtl.getKenoId());
							gdLotPrzDtl.setTxnLog(gdLotPrzCtl.getTxnlog());
							gdLotPrzDtl.settLogNo(gdLotPrzCtl.gettLogNo());
							gdLotPrzDtl.setBetMod(this.trim(node.get("betMod")));
							gdLotPrzDtl.setBetMul(this.trim(node.get("betMul")));
							gdLotPrzDtl.setClassNo(this.trim(node.get("class")));
							gdLotPrzDtl.setPrzAmt(this.trim(node.get("przAmt")));
							gdLotPrzDtl.setBetLin(this.trim(node.get("betLin")));
							get(GdLotPrzDtlRepository.class).insert(gdLotPrzDtl);
						}
					}
				}else{
					//TODO:第三方返回失败，发送短信给维护人员
					String message = "银福通系统游戏编号"+gdLotDrwTbl.getGameId()+"期号"+gdLotDrwTbl.getDrawId()+"小期号"+gdLotDrwTbl.getKenoId()+"定时返奖交易第3步，新增中奖明细异常，定投执行程序退出，请检查！";
					
					throw new CoreException(GDErrorCodes.EUPS_LOTR01_11_ERROR);
				}
			}else{//双色球
				context.setData(ParamKeys.FILE_TYPE, "2");
				context.setData("GameId", gdLotDrwTbl.getGameId());
				context.setData("DrawId", gdLotDrwTbl.getDrawId());
				get(CommonLotAction.class).downloadFile(context);
			}
		}
//				String filTyp = "2";
//				context.setData("filTyp", filTyp);
//				
//				String action = "237";
//				String dealer_id = ((GdLotSysCfg)context.getData("gdLotSysCfg")).getDealId();//运营商ID
//				context.setData("action", action);
//				context.setData("dealer_id", dealer_id);
//				
//				context.setData("gameId", gdLotDrwTbl.getGameId());//设置游戏编号
//				context.setData("drawId", gdLotDrwTbl.getDrawId());
//				context.setData("kenoId", gdLotDrwTbl.getKenoId());
//				
//				Transport ts = context.getService("STHDLOT1");
//				Map<String,Object> thdReturnMessage = null;
//				try {
//					thdReturnMessage = (Map<String, Object>) ts.submit(context.getDataMap(), context);//查询文件地址
//					context.setDataMap(thdReturnMessage);
////					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
//				} catch (CommunicationException e1) {
//					e1.printStackTrace();
//					logger.error("查询双色球中奖文件失败1");
//					throw new CoreException("查询双色球中奖文件失败1");
//				} catch (JumpException e1) {
//					e1.printStackTrace();
//					logger.error("查询双色球中奖文件失败2");
//					throw new CoreException("查询双色球中奖文件失败2");
//				}
//				
//				logger.info("thdReturnMessage["+thdReturnMessage+"]");
//				logger.info("state["+context.getState()+"]");
//				
//				CommThdRspCdeAction commThdRspCdeAction = new CommThdRspCdeAction();
//				String responseCode = commThdRspCdeAction.getThdRspCde(thdReturnMessage, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
//				if(responseCode.equals(Constants.RESPONSE_CODE_SUCC)){
//					logger.info("file["+context.getData("file")+"]");
//				}else{
//					//TODO:第三方返回失败，发送短信给维护人员
//					String message = "银福通系统游戏编号"+gdLotDrwTbl.getGameId()+"期号"+gdLotDrwTbl.getDrawId()+"小期号"+gdLotDrwTbl.getKenoId()+"定时返奖交易第3步，新增中奖明细异常，定投执行程序退出，请检查！";
//					
//					throw new CoreException(GDErrorCodes.EUPS_LOTR01_11_ERROR);
//				}
//				
//				action = "238";
//				try {
//					thdReturnMessage = (Map<String, Object>) ts.submit(context.getDataMap(), context);//下载文件
//					context.setDataMap(thdReturnMessage);
////					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
//				} catch (CommunicationException e1) {
//					e1.printStackTrace();
//					logger.error("下载双色球中奖文件失败1");
//					throw new CoreException("下载双色球中奖文件失败1");
//				} catch (JumpException e1) {
//					e1.printStackTrace();
//					logger.error("下载双色球中奖文件失败2");
//					throw new CoreException("下载双色球中奖文件失败2");
//				}
//				
//				logger.info("thdReturnMessage["+thdReturnMessage+"]");
//				logger.info("state["+context.getState()+"]");
//				
//				commThdRspCdeAction = new CommThdRspCdeAction();
//				responseCode = commThdRspCdeAction.getThdRspCde(thdReturnMessage, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
//				if(responseCode.equals(Constants.RESPONSE_CODE_SUCC)){
//					logger.info("file["+context.getData("file")+"]");
//					List<Map<String,Object>> items = context.getData("bonusItems");
//					for(Map<String,Object> item:items){
//						//新增中奖记录控制表
//						gdLotPrzCtl.setCipher(this.trim(item.get("cipher")));
//						gdLotPrzCtl.setBigBon(this.trim(item.get("bigBon")));
//						gdLotPrzCtl.setTotPrz(this.trim(item.get("totPrz")));
//						gdLotPrzCtl.setTxnlog(this.trim(item.get("txnLog")));
//						gdLotPrzCtl.settLogNo(this.trim(item.get("tLogNo")));
//						gdLotPrzCtl.setTermId(this.trim(item.get("termID")));
//						get(GdLotPrzCtlRepository.class).insert(gdLotPrzCtl);
//						
//						List<Map<String,Object>> nodes = (List<Map<String, Object>>) item.get("nodes");
//						for(Map<String,Object> node:nodes){
//							GdLotPrzDtl gdLotPrzDtl = new GdLotPrzDtl();
//							
//							gdLotPrzDtl.setGameId(gdLotPrzCtl.getGameId());
//							gdLotPrzDtl.setDrawId(gdLotPrzCtl.getDrawId());
//							gdLotPrzDtl.setKenoId(gdLotPrzCtl.getKenoId());
//							gdLotPrzDtl.setTxnLog(gdLotPrzCtl.getTxnlog());
//							gdLotPrzDtl.settLogNo(gdLotPrzCtl.gettLogNo());
//							gdLotPrzDtl.setBetMod(this.trim(node.get("betMod")));
//							gdLotPrzDtl.setBetMul(this.trim(node.get("betMul")));
//							gdLotPrzDtl.setClassNo(this.trim(node.get("class")));
//							gdLotPrzDtl.setPrzAmt(this.trim(node.get("przAmt")));
//							gdLotPrzDtl.setBetLin(this.trim(node.get("betLin")));
//							get(GdLotPrzDtlRepository.class).insert(gdLotPrzDtl);
//						}
//					}
//					
//				}else{
//					//TODO:第三方返回失败，发送短信给维护人员
//					String message = "银福通系统游戏编号"+gdLotDrwTbl.getGameId()+"期号"+gdLotDrwTbl.getDrawId()+"小期号"+gdLotDrwTbl.getKenoId()+"定时返奖交易第3步，新增中奖明细异常，定投执行程序退出，请检查！";
//					
//					throw new CoreException(GDErrorCodes.EUPS_LOTR01_11_ERROR);
//				}
//			}
//		}
		gdLotDrwTbl.setFlwCtl(""+(Integer.parseInt(flwCtl)+1));
		context.setData("flwCtl",gdLotDrwTbl.getFlwCtl());
		get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step3 end ... ...");
	}
	/**
	 * 检查中奖记录控制表
	 * @param context
	 */
	public void step4(Context context){
		logger.info("PayPlanServiceActionLOTR01 step4 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//4
		GdLotPrzCtl gdLotPrzCtl = new GdLotPrzCtl();
		gdLotPrzCtl.setGameId(gdLotPrzCtl.getGameId());
		gdLotPrzCtl.setDrawId(gdLotPrzCtl.getDrawId());
		gdLotPrzCtl.setKenoId(gdLotPrzCtl.getKenoId());
		List<GdLotPrzCtl> gdLotPrzCtls = get(GdLotPrzCtlRepository.class).find(gdLotPrzCtl);
		if(CollectionUtils.isEmpty(gdLotPrzCtls)){
			//更新返奖垫付标志、返奖垫付金额、返奖款划拨标志
			String payflg = "0";//0:没垫付
			String payamt = "0";//返奖垫付金额为0
			String xfeflg = "0";//0:无返奖划拨
			String przamt = "0";//当期返奖总金额为0
			gdLotDrwTbl.setPayFlg(payflg);
			gdLotDrwTbl.setPayAmt(payamt);
			gdLotDrwTbl.setXfeFlg(xfeflg);
			gdLotDrwTbl.setPrzAmt(przamt);
			get(GdLotDrwTblRepository.class).updatePayFlg(gdLotDrwTbl);
			get(GdLotDrwTblRepository.class).updatePrzAmt(gdLotDrwTbl);
			gdLotDrwTbl.setFlwCtl("10");
			context.setData("flwCtl",gdLotDrwTbl.getFlwCtl());
			get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		}else{
			gdLotDrwTbl.setFlwCtl(""+(Integer.parseInt(flwCtl)+1));
			context.setData("flwCtl",gdLotDrwTbl.getFlwCtl());
			get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		}
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step4 end ... ...");
	}
	/**
	 * 第5步根据中奖记录控制表和投注流水表生成返奖明细记录
	 * @param context
	 * @throws CoreException 
	 */
	public void step5(Context context) throws CoreException{
		logger.info("PayPlanServiceActionLOTR01 step5 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//5
		
		GdLotAwdDtl gdLotAwdDtl = new GdLotAwdDtl();
		gdLotAwdDtl.setGameId(gdLotDrwTbl.getGameId());
		gdLotAwdDtl.setDrawId(gdLotDrwTbl.getDrawId());
		gdLotAwdDtl.setKenoId(gdLotDrwTbl.getKenoId());
		List<GdLotAwdDtl> gdLotAwdDtls = get(GdLotAwdDtlRepository.class).find(gdLotAwdDtl);
		if(CollectionUtils.isNotEmpty(gdLotAwdDtls)){
			//TODO:不应该有记录，如果有，发送短信给维护人员
			String message = "银福通系统游戏编号"+gdLotDrwTbl.getGameId()+"期号"+gdLotDrwTbl.getDrawId()+"小期号"+gdLotDrwTbl.getKenoId()+"定时返奖交易第5步，检查返奖记录表有返奖记录存在异常，定投执行程序退出，请检查！";
			
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_13_ERROR);
		}else{
			//将中奖信息插入返奖记录明细表
			get(GdLotAwdDtlRepository.class).insertAwdDtl(gdLotAwdDtl);
			//查看有无返奖记录
			gdLotAwdDtls = get(GdLotAwdDtlRepository.class).find(gdLotAwdDtl);
			if(CollectionUtils.isEmpty(gdLotAwdDtls)){
				//无小奖记录
				//统计返奖总金额
				Map<String,Object> amtMap = get(GdLotAwdDtlRepository.class).sumAmt(gdLotAwdDtl);
				String przamt =  amtMap.get("PRZAMT")==null?"0":amtMap.get("PRZAMT").toString();
				gdLotDrwTbl.setPrzAmt(przamt);
				String payflg = "0";//0:未垫付
				String payamt="0";//返奖垫付金额
				gdLotDrwTbl.setPayFlg(payflg);
				gdLotDrwTbl.setPayAmt(payamt);
				get(GdLotDrwTblRepository.class).updatePayFlg(gdLotDrwTbl);
				get(GdLotDrwTblRepository.class).updatePrzAmt(gdLotDrwTbl);
				
				gdLotDrwTbl.setFlwCtl("10");
				context.setData("flwCtl",gdLotDrwTbl.getFlwCtl());
				get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
			}
		}
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step5 end ... ...");
	}
	/**
	 * 第5步检查中奖记录是否重复
	 * @param context
	 */
	public void step6(Context context){
		logger.info("PayPlanServiceActionLOTR01 step6 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//5
		//检查同意游戏编号、期号、投注流水号是否有多条记录存在
		Map<String,Object> dupMap = get(GdLotAwdDtlRepository.class).checkTxnLog();
		if(dupMap!=null){
			String dupnum = dupMap.get("DUPNUM").toString().trim();
			if(Integer.parseInt(dupnum)>1){
				//TODO:有重复流水
				String message = "银福通系统游戏编号"+gdLotDrwTbl.getGameId()+"期号"+gdLotDrwTbl.getDrawId()+"小期号"+gdLotDrwTbl.getKenoId()+"定时返奖交易第5步，返奖记录存在同一投注流水号有重复，定投执行程序退出，请检查！";
				
				
			}
		}
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step6 end ... ...");
	}
	/**
	 * 第5步统计返奖总金额，更新奖期信息表返奖总金额
	 * @param context
	 */
	public void step7(Context context){
		logger.info("PayPlanServiceActionLOTR01 step7 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//5
		GdLotAwdDtl gdLotAwdDtl = new GdLotAwdDtl();
		gdLotAwdDtl.setGameId(gdLotDrwTbl.getGameId());
		gdLotAwdDtl.setDrawId(gdLotDrwTbl.getDrawId());
		gdLotAwdDtl.setKenoId(gdLotDrwTbl.getKenoId());
		//统计返奖总金额
		Map<String,Object> amtMap = get(GdLotAwdDtlRepository.class).sumAmt(gdLotAwdDtl);
		String przAmt = amtMap.get("PRZAMT").toString();
		logger.info("返奖总金额["+przAmt+"]");
		gdLotDrwTbl.setPrzAmt(przAmt);
		get(GdLotDrwTblRepository.class).updatePrzAmt(gdLotDrwTbl);
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step7 end ... ...");
	}
	/**
	 * 更新投注流水表中奖记录的中奖标志
	 * @param context
	 */
	public void step8(Context context){
		logger.info("PayPlanServiceActionLOTR01 step8 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//5
		//更新流水表中奖标志AWDFLG=1
		get(GdLotTxnJnlRepository.class).updateAwdFlg(gdLotDrwTbl);
		
		gdLotDrwTbl.setFlwCtl(""+(Integer.parseInt(flwCtl)+1));
		context.setData("flwCtl",gdLotDrwTbl.getFlwCtl());
		get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step8 end ... ...");
	}
	/**
	 * 更新奖期信息状态
	 * @param context
	 */
	public void step9(Context context){
		logger.info("PayPlanServiceActionLOTR01 step9 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//6
		
		
		gdLotDrwTbl.setFlwCtl(""+(Integer.parseInt(flwCtl)+1));
		context.setData("flwCtl",gdLotDrwTbl.getFlwCtl());
		get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step9 end ... ...");
	}
	/**
	 * 第8步返奖资金划拨检查
	 * @param context
	 */
	public void step10(Context context){
		logger.info("PayPlanServiceActionLOTR01 step10 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//7
		//检查该期返奖资金是否已经划拨
		List<GdLotDrwTbl> gdLotDrwTbls = get(GdLotDrwTblRepository.class).find(gdLotDrwTbl);
		if(CollectionUtils.isEmpty(gdLotDrwTbls)){
			//TODO:不存在奖期信息
			String message = "银福通系统游戏编号"+gdLotDrwTbl.getGameId()+"期号"+gdLotDrwTbl.getDrawId()+"小期号"+gdLotDrwTbl.getKenoId()+"定时返奖交易第7步，奖期信息不存在，返奖执行程序退出，请检查！";
			
		}
		gdLotDrwTbl = gdLotDrwTbls.get(0);
		if("1".equals(gdLotDrwTbl.getXfeFlg())){
			//TODO:已划款
			String message = "银福通系统游戏编号"+gdLotDrwTbl.getGameId()+"期号"+gdLotDrwTbl.getDrawId()+"小期号"+gdLotDrwTbl.getKenoId()+"定时返奖交易第7步，该期返奖款已经划拨，定投执行程序退出，请检查！";
			
		}
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step10 end ... ...");
	}
	/**
	 * 第8步检查代收内部户余额是否大于返奖总金额
	 * @param context
	 */
	public void step11(Context context){
		logger.info("PayPlanServiceActionLOTR01 step11 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//7
		//查询代收内部户
		String dsActNo = context.getData("dsActNo");
		logger.info("代收内部户dsActNo["+dsActNo+"]");
		//查询返奖总金额
		String przAmt = gdLotDrwTbl.getPrzAmt();
		logger.info("返奖总金额przAmt["+przAmt+"]");
		
		//TODO:查询代收内部户余额
		String tlrId = "EPBKBI0";
		
		
		
		//TODO:判断代收内部户余额是否大于等于当期返奖总金额
		if("".equals("")){//大于返奖总金额
			context.setData("isGreater", "yes");
		}else{//小于返奖总金额
			context.setData("isGreater", "no");
		}
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step11 end ... ...");
	}
	/**
	 * 代收内部户余额大于返奖总金额，直接划账
	 * @param context
	 */
	public void step12(Context context){
		logger.info("PayPlanServiceActionLOTR01 step12 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//7
		//从购彩内部户划款到代发内部户，
		
		
		//更新返奖流程控制标志为8，返奖垫付标志为0
		String payFlg = "0";
		String payAmt = "0";
		String xfeFlg = "1";
		gdLotDrwTbl.setPayFlg(payFlg);
		gdLotDrwTbl.setPayAmt(payAmt);
		gdLotDrwTbl.setXfeFlg(xfeFlg);
//		get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step12 end ... ...");
	}
	/**
	 * 代收内部户余额小于返奖总金额，查询对公户余额，判断是否大于返奖总金额
	 * @param context
	 */
	public void step13(Context context){
		logger.info("PayPlanServiceActionLOTR01 step13 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//7
		//查询华祥对公户余额
		GdLotSysCfg gdLotSysCfg = context.getData("gdLotSysCfg");
		String actNo = gdLotSysCfg.getHsActNo();
		logger.info("华祥对公户actNo["+actNo+"]");
		
		
		//查询返奖总金额
		String przAmt = gdLotDrwTbl.getPrzAmt();
		logger.info("返奖总金额przAmt["+przAmt+"]"); 
		if("".equals("")){
			//TODO:余额小于返奖金额
			
		}
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step13 end ... ...");
	}
	/**
	 * 第8步对公户划款到代发内部户
	 * @param context
	 */
	public void step14(Context context){
		logger.info("PayPlanServiceActionLOTR01 step14 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//7
		//TODO：从对公户划款到代发内部户
		
		String payFlg = "1";
		String payAmt = gdLotDrwTbl.getPrzAmt();
		String xfeFlg = "1";
		gdLotDrwTbl.setPayFlg(payFlg);
		gdLotDrwTbl.setPayAmt(payAmt);
		gdLotDrwTbl.setXfeFlg(xfeFlg);
		
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step14 end ... ...");
	}
	/**
	 * 第8步更新返奖垫付标志、返奖总金额、返奖款划拨标志
	 * @param context
	 */
	public void step15(Context context){
		logger.info("PayPlanServiceActionLOTR01 step15 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//7
		
		get(GdLotDrwTblRepository.class).updatePayFlg(gdLotDrwTbl);
		
		gdLotDrwTbl.setFlwCtl(""+(Integer.parseInt(flwCtl)+1));
		context.setData("flwCtl",gdLotDrwTbl.getFlwCtl());
		get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step15 end ... ...");
	}
	/**
	 * 8.更新奖期状态信息
	 * @param context
	 */
	public void step16(Context context){
		logger.info("PayPlanServiceActionLOTR01 step16 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//8
		
		
		gdLotDrwTbl.setFlwCtl(""+(Integer.parseInt(flwCtl)+1));
		context.setData("flwCtl",gdLotDrwTbl.getFlwCtl());
		get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step16 end ... ...");
	}
	/**
	 * 9.根据返奖明细记录进行返奖
	 * @param context
	 */
	public void step17(Context context){
		logger.info("PayPlanServiceActionLOTR01 step17 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//9
		
		
		gdLotDrwTbl.setFlwCtl(""+(Integer.parseInt(flwCtl)+1));
		context.setData("flwCtl",gdLotDrwTbl.getFlwCtl());
		get(GdLotDrwTblRepository.class).updateFlwCtl(gdLotDrwTbl);
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step17 end ... ...");
	}
	/**
	 * 10.返奖完成
	 * @param context
	 */
	public void step18(Context context){
		logger.info("PayPlanServiceActionLOTR01 step18 start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String flwCtl = gdLotDrwTbl.getFlwCtl();
		logger.info("flwCtl["+flwCtl+"]");//10
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("PayPlanServiceActionLOTR01 step18 end ... ...");
	}
	public void finalize(Context context){
		logger.info("PayPlanServiceActionLOTR01 finalize start ... ...");
		logger.info("state["+context.getState()+"]");
		logger.info("PayPlanServiceActionLOTR01 finalize end ... ...");
	}
	
	private String trim(Object obj){
		return obj==null?"":obj.toString();
	}

}

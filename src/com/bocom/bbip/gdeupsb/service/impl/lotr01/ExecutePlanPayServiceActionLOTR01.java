package com.bocom.bbip.gdeupsb.service.impl.lotr01;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.action.lot.LoginAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdLotAutPln;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotPlnCtl;
import com.bocom.bbip.gdeupsb.repository.GdLotAutPlnRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotPlnCtlRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 定投计划执行
 * @author hefengwen
 *
 *1.申请奖期信息
 *1.1申请成功，至第2步
 *1.2申请失败，判断失败原因
 *1.2.1如果是未登录，调用登录交易，至第1步
 *1.2.2如果是其他错误，报错
 *
 *2.检查定投控制信息
 *2.1无记录，增加一条
 *2.2有记录，判断是否执行
 *2.2.1已执行，返回
 *2.2.2未执行，执行下一步
 *
 *3.检查奖期信息
 *3.1无记录，增加一条
 *3.2有记录，执行下一步
 *
 *4.判断是否购彩时间
 *4.1非购彩时间，返回
 *4.2是购彩时间，执行下一步
 *
 *5.查询定投计划表，获取定投信息
 *
 *6.循环处理定投信息
 *{
 *6.1判断是否当前期号
 *6.1.1是当前期号，不执行投注
 *6.1.2非当前期号，执行下一步
 *6.2发起购彩交易
 *}
 */
public class ExecutePlanPayServiceActionLOTR01 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ExecutePlanPayServiceActionLOTR01.class);
	
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	
	
	/**
	 * 1.调用登录交易
	 * 2.调用奖期信息下载交易
	 * @param context
	 * @throws CoreException
	 */
	public void initStep(Context context) throws CoreException{
		logger.info("ExecutePlanPayStrategyActionLOTR01 initStep start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		String gameId = "5";
		String action = "235";
		String dealer_id = "boc";//运营商ID
		String sent_time = DateUtils.format(new Date(),DateUtils.STYLE_FULL);
		context.setData("dealer_id", dealer_id);
		//定投执行前执行登录操作，调用已有交易
//		get(LoginAction.class).execute(context);
		get(BBIPPublicService.class).synExecute("gdeupsb.lotLogin", context);
		if(!context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			//TODO:系统登录失败，发送短信给维护人员
			
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_17_ERROR);
		}
		//奖期信息下载，调用已有交易
//		get()
		if(!context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			//TODO:奖期信息下载失败，发送短信给维护人员
			
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_18_ERROR);
		}
		
		
		context.setData("gameId", gameId);//设置游戏编号
		context.setData("action", action);
		context.setData("dealer_id", dealer_id);
		context.setData("sent_time", sent_time);
		logger.info("gameid:["+gameId+"]");
		/**
		Transport ts = context.getService("STHDLOT1");
		Map<String,Object> thdReturnMessage = null;//申请当前期号，奖期信息下载
		try {
			thdReturnMessage = (Map<String, Object>) ts.submit(context.getDataMap(), context);
			context.setDataMap(thdReturnMessage);
			context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		} catch (CommunicationException e1) {
			e1.printStackTrace();
		} catch (JumpException e1) {
			e1.printStackTrace();
		}
		
		logger.info("thdReturnMessage["+thdReturnMessage+"]");
		if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
			CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
			String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
			logger.info("responseCode:["+responseCode+"]");
//			if(responseCode.equals(GDErrorCodes.EUPS_LOTR01_04_ERROR)){
//				//系统未登陆，重新做登陆交易，调用已有登录交易，申请当前期号，奖期信息下载
//				context = get(BBIPPublicService.class).synExecute("", context);//同步调用登录交易
//				if(!context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
//					//系统登录失败，发送短信给维护人员
//					
//					
//				}
//				context.setData("gameId", gameId);//设置游戏编号
//				context.setData("action", action);
//				context.setData("dealer_id", dealer_id);
//				context.setData("sent_time", sent_time);
//				try {
//					thdReturnMessage = (Map<String, Object>) ts.submit(context.getDataMap(), context);
//					context.setDataMap(thdReturnMessage);
//					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
//				} catch (CommunicationException e) {
//					e.printStackTrace();
//				} catch (JumpException e) {
//					e.printStackTrace();
//				}//再次申请当前期号，奖期信息下载
//				if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
//					cRspCdeAction = new CommThdRspCdeAction();
//					responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
//					logger.info("responseCode:["+responseCode+"]");
//					if(!responseCode.equals(Constants.RESPONSE_CODE_SUCC)){
//						if(StringUtil.isEmpty(responseCode))
//							responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
//						throw new CoreException(responseCode);//下载奖期信息失败
//					}
//				}
//			}else 
			if(!responseCode.equals(Constants.RESPONSE_CODE_SUCC)){
				if(StringUtil.isEmpty(responseCode))
					responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
				throw new CoreException(responseCode);
			}
			context.setDataMap(thdReturnMessage);
		}else{
			//TODO:发送短信给维护人员，定投执行出错
			
			
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_08_ERROR);
		}*/
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("ExecutePlanPayStrategyActionLOTR01 initStep end ... ...");
	}
	/**
	 * 检查定投控制表
	 * @param context
	 * @throws CoreException
	 */
	public void chkCtlStep(Context context) throws CoreException{
		logger.info("ExecutePlanPayStrategyActionLOTR01 chkCtlStep start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		String gameId = context.getData("gameId");
		//检查定投控制信息表有无该期记录，没有就增加一条
		String drawId = context.getData("drawId");
		String drawNm = context.getData("drawNm");
		logger.info("drawId:["+drawId+"]drawNm:["+drawNm+"]");
		GdLotPlnCtl gdLotPlnCtl = new GdLotPlnCtl();
		gdLotPlnCtl.setDrawId(drawId);
		gdLotPlnCtl.setGameId(gameId);
		List<GdLotPlnCtl> gdLotPlnCtls = get(GdLotPlnCtlRepository.class).find(gdLotPlnCtl);
		if(CollectionUtils.isEmpty(gdLotPlnCtls)){
			String betDat = DateUtils.formatAsSimpleDate(new Date());
			String begTim = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss);
			String endTim = " ";
			String txnSts = "U";
			gdLotPlnCtl.setDrawNm(drawNm);
			gdLotPlnCtl.setBetDat(betDat);
			gdLotPlnCtl.setBegTim(begTim);
			gdLotPlnCtl.setEndTim(endTim);
			gdLotPlnCtl.setTxnSts(txnSts);
			get(GdLotPlnCtlRepository.class).insert(gdLotPlnCtl);
		}else{
			gdLotPlnCtl = gdLotPlnCtls.get(0);
		}
		//检查是否已经执行过该期定投
		if("S".equals(gdLotPlnCtl.getTxnSts())){
			logger.error("期号:"+drawId+"已经执行过！");
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_05_ERROR);
		}
		context.setData("gdLotPlnCtl", gdLotPlnCtl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("ExecutePlanPayStrategyActionLOTR01 chkCtlStep end ... ...");
	}
	/**
	 * 检查期号表
	 * @param context
	 */
	public void chkDrwStep(Context context){
		logger.info("ExecutePlanPayStrategyActionLOTR01 chkDrwStep start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		//检查期号表，如果无，增加期号记录，如果有，检查是否完成
		String gameId = context.getData("gameId");
		String drawId = context.getData("drawId");
		String drawNm = context.getData("drawNm");
		logger.info("drawId:["+drawId+"]drawNm:["+drawNm+"]");
		GdLotDrwTbl gdLotDrwTbl = new GdLotDrwTbl();
		gdLotDrwTbl.setGameId(gameId);
		gdLotDrwTbl.setDrawId(drawId);
		List<GdLotDrwTbl> gdLotDrwTbls = get(GdLotDrwTblRepository.class).find(gdLotDrwTbl);
		if(CollectionUtils.isEmpty(gdLotDrwTbls)){
			String salStr = context.getData("salStr");//销售开始时间
			String salEnd = context.getData("salEnd");//销售结束时间
			String cshStr = context.getData("cshStr");//兑奖开始时间
			String cshEnd = context.getData("cshEnd");//兑奖结束时间
			gdLotDrwTbl.setDrawNm(drawNm);
			gdLotDrwTbl.setSalStr(salStr);
			gdLotDrwTbl.setSalEnd(salEnd);
			gdLotDrwTbl.setCshStr(cshStr);
			gdLotDrwTbl.setCshEnd(cshEnd);
			get(GdLotDrwTblRepository.class).insert(gdLotDrwTbl);
		}else{
			gdLotDrwTbl = gdLotDrwTbls.get(0);
		}
		context.setData("gdLotDrwTbl", gdLotDrwTbl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("ExecutePlanPayStrategyActionLOTR01 chkDrwStep end ... ...");
	}
	/**
	 * 判断当前系统时间是否为购彩时间
	 * @param context
	 * @throws CoreException
	 */
	public void chkTimStep(Context context) throws CoreException{
		logger.info("ExecutePlanPayStrategyActionLOTR01 chkTimStep start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		//检查当前系统时间是否为购彩时间
		GdLotDrwTbl gdLotDrwTbl = context.getData("gdLotDrwTbl");
		String curtim = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss);
		if(curtim.compareTo(gdLotDrwTbl.getSalEnd())>0){
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_06_ERROR);
		}
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("ExecutePlanPayStrategyActionLOTR01 chkTimStep end ... ...");
	}
	/**
	 * 获取定投记录，执行投注
	 * @param context
	 * @throws CoreException 
	 * @throws CoreRuntimeException 
	 */
	public void exePlanStep(Context context) throws CoreRuntimeException, CoreException{
		logger.info("ExecutePlanPayStrategyActionLOTR01 exePlanStep start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		//查询定投计划表，获取待投注记录
		String gameId = context.getData("gameId");
		String drawId = context.getData("drawId");
		String drawNm = context.getData("drawNm");
		logger.info("drawId:["+drawId+"]drawNm:["+drawNm+"]");
		GdLotAutPln gdLotAutPln = new GdLotAutPln();
		gdLotAutPln.setStatus("0");
		List<GdLotAutPln> gdLotAutPlns = get(GdLotAutPlnRepository.class).find(gdLotAutPln);
		for(GdLotAutPln pln:gdLotAutPlns){
			if(drawId.equals(pln.getCurPer())){
				logger.info("crdNo["+pln.getCurPer()+"]非当前期号");
				continue;
			}
			context.setData("nodno", "441800");
			context.setData("tiaTyp", "T");
			context.setData("tlrid", "EPBKBI0");
			context.setData("ttxnCd", "485411");
			context.setData("txnSrc", "EAUTO");//TODO:这几个参数是否需要？
			
			context.setData("schtyp", "1");//方案类型：1-直接投注
			context.setData("seclev", "1");//方案等级：1-保密投注
			context.setData("bettyp", "1");//投注类型：0-实时投注，1-定时投注
			String grpNum = "";
			if("13".equals(pln.getBetMod()))//投注方式：1-单注，12-复式，13-胆托
				grpNum = "3";
			else
				grpNum = "2";
			context.setData("grpNum", grpNum);
			//总号码个数=（投注号码长度-分区组数*2）/2
			String betNum = ""+(pln.getBetLin().length()-Integer.parseInt(grpNum)*2)/2;
			context.setData("betNum", betNum);
			//TODO:发起购彩交易，调用已有投注交易
			try {
				context = get(BBIPPublicService.class).synExecute("gdeupsb.lotCathecticOnTime", context);
			} catch (Exception e) {
				logger.error("定投执行失败!");
				String message = "购彩系统异常：定投交易终止执行，请检查定投交易";
				//TODO:调用短信发送接口
				
				break;
			}
			
			String rspCde = context.getData(ParamKeys.RESPONSE_CODE);
			String tzCod = context.getData("tzCod");
			//TODO:判断购彩交易是否成功
			if("000000".equals(rspCde)){
				//购彩成功，更新定投计划表当前期号，已执行期数加1，置当前失败次数和连续失败次数为0
				pln.setCurPer(drawId);//当前期号
				pln.setDoPer(""+Integer.parseInt(pln.getDoPer()+1));//已执行期数加1
				pln.setCurFal("0");//当前失败次数清0
				pln.setConFal("0");//连续失败次数清0
				pln.setCclTim(" ");//撤销时间为空
				if(pln.getDoPer().equals(pln.getBetPer())){
					//检查期数是否已执行完
					pln.setCclTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
					pln.setStatus("3");
				}
			}else{
				//购彩失败，置当前失败次数和连续失败次数加1，判断连续失败次数大于3，定投计划取消
				pln.setCurPer(drawId);
				//TODO:判断失败原因
				if("TZ9009".equals(tzCod)){
					//余额不足
					pln.setCurFal(""+(Integer.parseInt(pln.getCurFal())+1));//当前失败次数加1
					pln.setConFal(""+(Integer.parseInt(pln.getConFal())+1));//连续失败次数加1
					pln.setCclTim(" ");//撤销时间为空
					String message = "尊敬的客户:由于您的卡号"+pln.getCrdNo()+"余额不足，定投购彩不成功!";
					//TODO:调用短信发送接口
					
					if("3".equals(pln.getConFal())){//连续失败3次，撤销该定投计划
						pln.setCclTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
						pln.setStatus("2");
						message = "尊敬的客户:由于您的定投购彩计划已累计三次购买彩票不成功，对定投计划编号为"+pln.getPlanNo()+"进行撤消!";
						//TODO:调用短信发送接口
						
					}
				}else{
					pln.setCurFal(""+(Integer.parseInt(pln.getCurFal())+1));//当前失败次数加1
					pln.setConFal(""+(Integer.parseInt(pln.getConFal())+1));//连续失败次数加1
					pln.setCclTim(" ");//撤销时间为空
					String message = "尊敬的客户:由于您的卡号"+pln.getCrdNo()+"定投购彩不成功,请重新操作或拨打95559查询原因!";
					//TODO:调用短信发送接口
					
					if("3".equals(pln.getConFal())){//连续失败3次，撤销该定投计划
						pln.setCclTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
						pln.setStatus("2");
						message = "尊敬的客户:由于您的定投购彩计划已累计三次购买彩票不成功，对定投计划编号为"+pln.getPlanNo()+"进行撤消!";
						//TODO:调用短信发送接口
						
					}
				}
				
				//更新定投计划表
				try {
					get(GdLotAutPlnRepository.class).updateResult(pln);
				} catch (Exception e) {
					e.printStackTrace();
					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
					break;
				}
			}
		}
//		context.setData("gdLotPlnCtl", gdLotPlnCtl);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("ExecutePlanPayStrategyActionLOTR01 exePlanStep end ... ...");
	}
	public void savPlanStep(Context context){
		logger.info("ExecutePlanPayStrategyActionLOTR01 savPlanStep start ... ...");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdLotPlnCtl gdLotPlnCtl = context.getData("gdLotPlnCtl");
		if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_FAIL)){
			//定投执行失败
			logger.error(">>>>>>更新定投计划表失败<<<<<");
			gdLotPlnCtl.setEndTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
			gdLotPlnCtl.setTxnSts("F");
			get(GdLotPlnCtlRepository.class).updateTxnsts(gdLotPlnCtl);
			
			context.setData(ParamKeys.RESPONSE_TYPE, "E");
			context.setData(ParamKeys.RESPONSE_CODE, GDErrorCodes.EUPS_LOTR01_07_ERROR);
		}else{
			//定投计划执行完成
			gdLotPlnCtl.setEndTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
			gdLotPlnCtl.setTxnSts("S");
			get(GdLotPlnCtlRepository.class).updateTxnsts(gdLotPlnCtl);
			String message = "购彩系统信息：定投交易顺利执行完成!";
			//TODO:发送短信给维护人员
			
		}
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		logger.info("ExecutePlanPayStrategyActionLOTR01 savPlanStep end ... ...");
	}
	public void finalize(Context context){
		logger.info("ExecutePlanPayStrategyActionLOTR01 finalize start ... ...");
		
		logger.info("ExecutePlanPayStrategyActionLOTR01 finalize end ... ...");
	}
	/*
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("ExecutePlanPayStrategyActionLOTR01 start ... ...");
		String gameId = context.getData("gameId");
		//检查定投控制信息表有无该期记录，没有就增加一条
		String drawId = context.getData("drawId");
		String drawNm = context.getData("drawNm");
		logger.info("drawId:["+drawId+"]drawNm:["+drawNm+"]");
		GdLotPlnCtl gdLotPlnCtl = new GdLotPlnCtl();
		gdLotPlnCtl.setDrawId(drawId);
		gdLotPlnCtl.setGameId(gameId);
		List<GdLotPlnCtl> gdLotPlnCtls = get(GdLotPlnCtlRepository.class).find(gdLotPlnCtl);
		if(CollectionUtils.isEmpty(gdLotPlnCtls)){
			String betDat = DateUtils.formatAsSimpleDate(new Date());
			String begTim = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss);
			String endTim = " ";
			String txnSts = "U";
			gdLotPlnCtl.setDrawNm(drawNm);
			gdLotPlnCtl.setBetDat(betDat);
			gdLotPlnCtl.setBegTim(begTim);
			gdLotPlnCtl.setEndTim(endTim);
			gdLotPlnCtl.setTxnSts(txnSts);
			get(GdLotPlnCtlRepository.class).insert(gdLotPlnCtl);
		}else{
			gdLotPlnCtl = gdLotPlnCtls.get(0);
		}
		//检查是否已经执行过该期定投
		if("S".equals(gdLotPlnCtl.getTxnSts())){
			logger.error("期号:"+drawId+"已经执行过！");
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_05_ERROR);
		}
		
		//检查期号表，如果无，增加期号记录，如果有，检查是否完成
		GdLotDrwTbl gdLotDrwTbl = new GdLotDrwTbl();
		gdLotDrwTbl.setGameId(gameId);
		gdLotDrwTbl.setDrawId(drawId);
		List<GdLotDrwTbl> gdLotDrwTbls = get(GdLotDrwTblRepository.class).find(gdLotDrwTbl);
		if(CollectionUtils.isEmpty(gdLotDrwTbls)){
			String salStr = context.getData("salStr");//销售开始时间
			String salEnd = context.getData("salEnd");//销售结束时间
			String cshStr = context.getData("cshStr");//兑奖开始时间
			String cshEnd = context.getData("cshEnd");//兑奖结束时间
			gdLotDrwTbl.setDrawNm(drawNm);
			gdLotDrwTbl.setSalStr(salStr);
			gdLotDrwTbl.setSalEnd(salEnd);
			gdLotDrwTbl.setCshStr(cshStr);
			gdLotDrwTbl.setCshEnd(cshEnd);
			get(GdLotDrwTblRepository.class).insert(gdLotDrwTbl);
		}else{
			gdLotDrwTbl = gdLotDrwTbls.get(0);
		}
		//检查当前系统时间是否为购彩时间
		String curtim = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss);
		if(curtim.compareTo(gdLotDrwTbl.getSalEnd())>0){
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_06_ERROR);
		}
		//查询定投计划表，获取待投注记录
		GdLotAutPln gdLotAutPln = new GdLotAutPln();
		gdLotAutPln.setStatus("0");
		List<GdLotAutPln> gdLotAutPlns = get(GdLotAutPlnRepository.class).find(gdLotAutPln);
		for(GdLotAutPln pln:gdLotAutPlns){
			if(drawId.equals(pln.getCurPer())){
				logger.info("crdNo["+pln.getCurPer()+"]非当前期号");
				continue;
			}
			context.setData("nodno", "441800");
			context.setData("tiaTyp", "T");
			context.setData("tlrid", "EPBKBI0");
			context.setData("ttxnCd", "485411");
			context.setData("txnSrc", "EAUTO");//TODO:这几个参数是否需要？
			
			context.setData("schtyp", "1");//方案类型：1-直接投注
			context.setData("seclev", "1");//方案等级：1-保密投注
			context.setData("bettyp", "1");//投注类型：0-实时投注，2-定时投注
			String grpNum = "";
			if("13".equals(pln.getBetMod()))//投注方式：1-单注，12-复式，13-胆托
				grpNum = "3";
			else
				grpNum = "2";
			context.setData("grpNum", grpNum);
			//总号码个数=（投注号码长度-分区组数*2）/2
			String betNum = ""+(pln.getBetLin().length()-Integer.parseInt(grpNum)*2)/2;
			//TODO:发起购彩交易，调用已有投注交易
			
//			get(BBIPPublicService.class).synExecute("", context);
			
			
			//TODO:判断购彩交易是否成功
			if(context.getState().equals(Constants.RESPONSE_CODE_SUCC)){
				//购彩成功，更新定投计划表当前期号，已执行期数加1，置当前失败次数和连续失败次数为0
				pln.setCurPer(drawId);//当前期号
				pln.setDoPer(""+Integer.parseInt(pln.getDoPer()+1));//已执行期数加1
				pln.setCurFal("0");//当前失败次数清0
				pln.setConFal("0");//连续失败次数清0
				pln.setCclTim(" ");//撤销时间为空
				if(pln.getDoPer().equals(pln.getBetPer())){
					//检查期数是否已执行完
					pln.setCclTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
					pln.setStatus("3");
				}
			}else{
				//购彩失败，置当前失败次数和连续失败次数加1，判断连续失败次数大于3，定投计划取消
				pln.setCurPer(drawId);
				//TODO:判断失败原因
				if("".equals("")){
					//1.余额不足
					pln.setCurFal(""+(Integer.parseInt(pln.getCurFal())+1));//当前失败次数加1
					pln.setConFal(""+(Integer.parseInt(pln.getConFal())+1));//连续失败次数加1
					pln.setCclTim(" ");//撤销时间为空
					String message = "尊敬的客户:由于您的卡号"+pln.getCrdNo()+"余额不足，定投购彩不成功!";
					//TODO:调用短信发送接口
					
					if("3".equals(pln.getConFal())){//连续失败3次，撤销该定投计划
						pln.setCclTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
						pln.setStatus("2");
						message = "尊敬的客户:由于您的定投购彩计划已累计三次购买彩票不成功，对定投计划编号为"+pln.getPlanNo()+"进行撤消!";
						//TODO:调用短信发送接口
						
					}
				}else if("".equals("")){
					//2.卡状态不正常
					pln.setCurFal(""+(Integer.parseInt(pln.getCurFal())+1));//当前失败次数加1
					pln.setConFal(""+(Integer.parseInt(pln.getConFal())+1));//连续失败次数加1
					pln.setCclTim(" ");//撤销时间为空
					String message = "尊敬的客户:由于您的卡号"+pln.getCrdNo()+"状态不正常，定投购彩第"+drawId+"期不成功!";
					//TODO:调用短信发送接口
					
					if("3".equals(pln.getConFal())){//连续失败3次，撤销该定投计划
						pln.setCclTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
						pln.setStatus("2");
						message = "尊敬的客户:由于您的定投购彩计划已累计三次购买彩票不成功，对定投计划编号为"+pln.getPlanNo()+"进行撤消!";
						//TODO:调用短信发送接口
						
					}
				}else if("".equals("")){
					//3.卡正式挂失
					pln.setCurFal(""+(Integer.parseInt(pln.getCurFal())+1));//当前失败次数加1
					pln.setConFal(""+(Integer.parseInt(pln.getConFal())+1));//连续失败次数加1
					pln.setCclTim(" ");//撤销时间为空
					String message = "尊敬的客户:由于您的卡号"+pln.getCrdNo()+"已正式挂失，定投购彩第"+drawId+"期不成功!";
					//TODO:调用短信发送接口
					
					if("3".equals(pln.getConFal())){//连续失败3次，撤销该定投计划
						pln.setCclTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
						pln.setStatus("2");
						message = "尊敬的客户:由于您的定投购彩计划已累计三次购买彩票不成功，对定投计划编号为"+pln.getPlanNo()+"进行撤消!";
						//TODO:调用短信发送接口
						
					}
				}else if("".equals("")){
					//4.卡已冻结
					pln.setCurFal(""+(Integer.parseInt(pln.getCurFal())+1));//当前失败次数加1
					pln.setConFal(""+(Integer.parseInt(pln.getConFal())+1));//连续失败次数加1
					pln.setCclTim(" ");//撤销时间为空
					String message = "尊敬的客户:由于您的卡号"+pln.getCrdNo()+"已冻结，定投购彩第"+drawId+"期不成功!";
					//TODO:调用短信发送接口
					
					if("3".equals(pln.getConFal())){//连续失败3次，撤销该定投计划
						pln.setCclTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
						pln.setStatus("2");
						message = "尊敬的客户:由于您的定投购彩计划已累计三次购买彩票不成功，对定投计划编号为"+pln.getPlanNo()+"进行撤消!";
						//TODO:调用短信发送接口
						
					}
				}else if("".equals("")){
					//5.发送购彩超时
					String message = "尊敬的客户:由于您的定投购彩遇到超时，请半小时后查询第"+drawId+"期购彩是否成功!";
					//TODO:调用短信发送接口
					
				}else if("".equals("")){
					//6.主机记账失败
					String message = "尊敬的客户:由于您的卡号"+pln.getCrdNo()+"扣款异常，定投购彩第"+drawId+"期不成功!";
					//TODO:调用短信发送接口
					
				}else if("".equals("")){
					//7.投注不成功
					String message = "尊敬的客户:您的卡号"+pln.getCrdNo()+"定投购彩第"+drawId+"期不成功!";
					//TODO:调用短信发送接口
					
				}else if("".equals("")){
					//8.购彩系统异常，联系维护人员
					String message = "购彩系统异常，定投交易终止执行，请检查定投交易!";
					//TODO:调用短信发送接口
					
				}else if("".equals("")){
					
				}
				
				//更新定投计划表
				try {
					get(GdLotAutPlnRepository.class).updateResult(pln);
				} catch (Exception e) {
					e.printStackTrace();
					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
					break;
				}
			}
		}
		context.setData("gdLotPlnCtl", gdLotPlnCtl);
		
		
		logger.info("ExecutePlanPayStrategyActionLOTR01 end ... ...");
	}
	
	public void saveCtlRst(Context context){
		logger.info("ExecutePlanPayStrategyActionLOTR01 saveCtlRst start ... ...");
		GdLotPlnCtl gdLotPlnCtl = context.getData("gdLotPlnCtl");
		if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_FAIL)){
			//定投执行失败
			logger.error(">>>>>>更新定投计划表失败<<<<<");
			gdLotPlnCtl.setEndTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
			gdLotPlnCtl.setTxnSts("F");
			get(GdLotPlnCtlRepository.class).updateTxnsts(gdLotPlnCtl);
			
			context.setData(ParamKeys.RESPONSE_TYPE, "E");
			context.setData(ParamKeys.RESPONSE_CODE, GDErrorCodes.EUPS_LOTR01_07_ERROR);
		}else{
			//定投计划执行完成
			gdLotPlnCtl.setEndTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
			gdLotPlnCtl.setTxnSts("S");
			get(GdLotPlnCtlRepository.class).updateTxnsts(gdLotPlnCtl);
			String message = "购彩系统信息：定投交易顺利执行完成!";
			//TODO:发送短信给维护人员
			
		}
		logger.info("ExecutePlanPayStrategyActionLOTR01 saveCtlRst end ... ...");
	}
	*/
}

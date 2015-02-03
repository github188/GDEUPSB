package com.bocom.bbip.gdeupsb.service.impl.lotr01;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdLotAutPln;
import com.bocom.bbip.gdeupsb.entity.GdLotCusInf;
import com.bocom.bbip.gdeupsb.repository.GdLotAutPlnRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotCusInfRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.id.seq.StepSequenceFactory;
/**
 * 定投录入计划
 * @author hefengwen
 *
 */
public class InsertPlanPayServiceActionLOTR01 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(InsertPlanPayServiceActionLOTR01.class);
	
//	@Autowired
//	BGSPServiceAccessObject a;
	
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	
	
//	public void execute(Context context) throws CoreException ,CoreRuntimeException {
//		logger.info("InsertPlanPayStrategyActionLOTR01 start ... ...");
//		context.setData("action","212");
//		context.setData("dealer_id","140");
//		context.setData("sent_time","20150122");
//		context.setData("user","hfw");
//		context.setData("pwd","");
////		context.setData("test","456");
////		callThdTradeManager.trade(context);
//		Transport ts = context.getService("STHDLOT1");
//		try {
//			ts.submit(context.getDataMap(),context);
//		} catch (CommunicationException e) {
//			e.printStackTrace();
//		} catch (JumpException e) {
//			e.printStackTrace();
//		}
//		
//		logger.info("InsertPlanPayStrategyActionLOTR01 end ... ...");
//	};
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("InsertPlanPayStrategyActionLOTR01 start ... ...");
		
		//检查用户注册信息
//		String planNm = context.getData("planNm");
		String gameId = context.getData("gameId");
//		String gamNam = context.getData("gamNam");
		String playId = context.getData("playId");
		String betPer = context.getData("betPer");
		String betMet = context.getData("betMet");
		String betMod = context.getData("betMod");
		String betMul = context.getData("betMul");
		String betAmt = context.getData("betAmt").toString();
		String betLin = context.getData("betLin");
		String crdNo = context.getData("crdNo");
		String txnCnl = context.getData("txnCnl");
		logger.info("crdNo:["+crdNo+"]");
		GdLotCusInf gdLotCusInf = new GdLotCusInf();
		gdLotCusInf.setCrdNo(crdNo);
		gdLotCusInf.setStatus("1");
		List<GdLotCusInf> eupsLotCusInfs = get(GdLotCusInfRepository.class).find(gdLotCusInf);
		if(eupsLotCusInfs==null||eupsLotCusInfs.size()==0){
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_00_ERROR);//如果用户注册信息不存在，报错返回
		}
		
		//判断是否为双色球游戏
		logger.info("gameId:["+gameId+"]");//游戏编号，5:双色球
		if(!"5".equals(gameId)){
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_01_ERROR);//非双色球玩法直接报错返回
		}
		//判断投注方式
		logger.info("betMod:["+betMod+"]");//投注方式，1:单式；12：复式；13：胆托
		if(!"1".equals(betMod)&&!"12".equals(betMod)&&!"13".equals(betMod)){
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_02_ERROR);//双色球投注方式错误，报错返回
		}
		//准备定投录入数据
		GdLotAutPln gdLotAutPln = new GdLotAutPln();
		gdLotAutPln.setGameId(gameId);
		gdLotAutPln.setPlayId(playId);
		gdLotAutPln.setBetPer(betPer);
		gdLotAutPln.setBetMet(betMet);
		gdLotAutPln.setBetMod(betMod);
		gdLotAutPln.setBetMul(betMul);
		gdLotAutPln.setBetAmt(betAmt);
		gdLotAutPln.setBetLin(betLin);
		gdLotAutPln.setCrdNo(crdNo);
		gdLotAutPln.setTxnCnl(txnCnl);
		//生成20位长度定投计划编号，99+日期(yyyymmdd)+10位顺序号
		StepSequenceFactory service = context.getService("logNoService");
		String logno = service.create().toString();
		String planno = "99"+logno;
		logger.info("planno:["+planno+"]");
		gdLotAutPln.setPlanNo(planno);
		gdLotAutPln.setPlanNm("BOCPLAN");//定投计划名称
		gdLotAutPln.setGamNam( "双色球");//游戏名称
		gdLotAutPln.setBetDat(DateUtils.formatAsSimpleDate(new Date()));//定投日期
		gdLotAutPln.setBetTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));//定投时间
		gdLotAutPln.setStatus("0");//定投状态
		
		//定投信息插入定投计划表
		get(GdLotAutPlnRepository.class).insert(gdLotAutPln);
		
		logger.info("InsertPlanPayStrategyActionLOTR01 end ... ...");
	}
	
//	public static void main(String[] args) {
//		System.out.println(DateUtils.format(new Date(), "yyyyMMddHHmmss"));
//	}

}

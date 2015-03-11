package com.bocom.bbip.gdeupsb.action.lot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class QryLotWinRecordAction extends BaseAction {
	private final static Logger logger = LoggerFactory.getLogger(QryLotWinRecordAction.class);
	
	public void execute(Context context)throws CoreException, CoreRuntimeException{
		logger.info("Enter in QryLotWinRecordAction!!!.....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		logger.info("=================context:" + context);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("crdNo", context.getData("crdNo").toString().trim());
		map.put("gameId", context.getData("gameId"));	//gameId=5,福彩
		
		//查询投注方式 前端传来 betTyp为空则查询全部（实时、定投）在SQL语句解决
		String betTyp = context.getData(GDParamKeys.LOT_BET_TYP).toString();
		if(betTyp==null || betTyp=="" || betTyp=="?"){ 
			betTyp = "";
		}
		logger.info("投注方式是：" + betTyp);
		
		map.put("betTyp", betTyp);
		

		//限定查询条件
		 String endDate =context.getData("endDat").toString().replace("-", "").trim();
		 Date date = new Date();
	        if (StringUtils.isEmpty(endDate) || endDate==null){
	            endDate = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);
	            context.setData(GDParamKeys.LOT_END_DATE, endDate);
	            map.put("endDat",endDate);
	        }else{
	         map.put("endDat",endDate);
	        }
	        String begDate = context.getData("begDat").toString().replace("-", "").trim();
	        if (StringUtils.isEmpty(begDate)){
	        	Calendar calendar = Calendar.getInstance();
	        	calendar.setTime(date);
	        	calendar.add(Calendar.MONTH, date.getMonth()-3);
	        	date = calendar.getTime();
	        	begDate = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);
	        	map.put("begDat", begDate);
	        } else {
	            begDate = context.getData("begDat").toString();
	            map.put("begDat", begDate);
	        }
	        context.setData(GDParamKeys.LOT_BEG_DATE, begDate);
			
	        logger.info("=========MAP===========MAP======="+map.toString());
	        
	        List<Map<String, Object>> winMapLst = get(GdLotTxnJnlRepository.class).qryLotWinRecord(map);
	        logger.info("==========查询结果winMap========"+winMapLst.size());
	        
	        
	        if(CollectionUtils.isEmpty(winMapLst)){
	        	context.setData("msgTyp", "E");
	        	throw new CoreException("无符合条件的记录");
	        }
	        
	        /*
==========查询结果winMap========[{BET_LIN=01020305070811, DRAW_ID=95, T_LOG_NO=201501120003, TXN_TIM=20150111110000, BET_MOD=1, PLAY_ID=00006, TOT_PRZ=4, KENO_ID=007, BET_MUL=50, TXN_LOG=201501120003}]*/
	        
	        
	        List<Map<String, Object>> qryResultList = new ArrayList<Map<String,Object>>();
//	        for(int i=0; i<winMapLst.size(); i++){
	        	for(Map<String, Object> maps:winMapLst){
	        	Map<String, Object> tempMap = new HashMap<String, Object>();
//	        	<Item name="TxnLog"   length="30"  desc="投注流水号"/>
//				<Item name="DrawId"   length="5"   desc="当前大期"/>
//				<Item name="KenoId"   length="5"   desc="当前小期"/>
//				<Item name="BetMul"   length="3"   desc="投注倍数"/>
//				<Item name="BetLin"   length="128" desc="投注号码"/>
//				<Item name="BetMod"   length="5"   desc="投注方式  1单式，12复式，13胆托"/>
//				<Item name="PrzAmt"   length="15"  desc="返奖金额"/>
	        	tempMap.put("txnLog", maps.get("T_LOG_NO"));
	        	tempMap.put("drawId", maps.get("DRAW_ID"));
	        	tempMap.put("kenoId", maps.get("KENO_ID"));
	        	tempMap.put("betMul", maps.get("BET_MUL"));
	        	tempMap.put("betLin", maps.get("BET_LIN"));
	        	tempMap.put("betMod", maps.get("BET_MOD"));
	        	tempMap.put("przAmt", maps.get("PRZ_AMT"));
	        	qryResultList.add(tempMap);
	        }
	        
	        
	        
            context.setData("rec", qryResultList);
            
            logger.info("============context11111111==========" + context);
            
//			<Set>MsgTyp=N</Set>
//			<Set>RspCod=000000</Set>
	        
	        
	        	context.setData("apCode", "32");
	    		context.setData("ofmtCd", "z01");
	    		context.setData("pageNo", "0001");
	    		context.setData("varSize", "3");
	    		
	    		
	    		context.setData("msgTyp", "N");
	    		context.setData("rspCod", "000000");
		
			logger.info("============context222222222222==========" + context);
			
	    	context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}

}

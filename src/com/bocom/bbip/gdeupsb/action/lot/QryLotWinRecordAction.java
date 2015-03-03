package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;


import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
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
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("crdNo", context.getData("crdNo").toString().trim());
		map.put("gameId", "5");	//gameId=5,福彩
		
		
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
	        
	        List<Map<String, Object>> winMap = get(GdLotTxnJnlRepository.class).qryLotWinRecord(map);
	        logger.info("==========查询结果winMap========"+winMap.toString());
	        
/*	        ==========查询结果winMap========
	        		[{BETMUL=50, BETLIN=01020305070811,
	        		PLAYID=00006, TXNTIM=20150111110000, 
	        		BETMOD=1, KENOID=007, TOTPRZ=4, TXNLOG=201501120003, 
	        		DRAWID=95, TLOGNO=201501120003}]
	        */
	        
            context.setData("rec", winMap);
            
            logger.info("============context11111111==========" + context.toString());
            
//			<Set>MsgTyp=N</Set>
//			<Set>RspCod=000000</Set>
	        
	        
	        	context.setData("apCode", "32");
	    		context.setData("ofmtCd", "z01");
	    		context.setData("pageNo", "0001");
	    		context.setData("varSize", "3");
	    		
	    		
	    		context.setData("msgTyp", "N");
	    		context.setData("rspCod", "000000");
		
			logger.info("============context222222222222==========" + context.toString());
			
	    	context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}

}

package com.bocom.bbip.gdeupsb.action.lot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * 福彩投注查询 485413
 * @version 1.0.0
 * Date 2015-01-22
 * @author GuiLin.Li
 */
public class QueryCathecticAction extends BaseAction{
    
    @SuppressWarnings("deprecation")
    @Override
    public void execute(Context context) throws CoreException {
        log.info("Enter in QueryCathecticAction... ");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("betTyp",context.getData("betTyp").toString());
        map.put("crdNo",context.getData("crdNo").toString());
        String endDate =context.getData("endDat").toString().replace("-", "").trim();
        if (StringUtils.isEmpty(endDate)){
            Date date = new Date();
            endDate = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);
        }
         map.put("endDat",endDate);
         //TODO 银付通测试前端没有传递gameId字段以后待确实，先定位5
         String gameId = context.getData("gameId");
         if(StringUtils.isEmpty(gameId)){
        	 gameId="5";
         }
         map.put("gameId",gameId);
        String begDate = context.getData("begDat").toString().replace("-", "").trim();
        if (StringUtils.isEmpty(begDate)){
            Calendar calendar = Calendar.getInstance();
            Date date = new Date();
            
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, date.getMonth()-3);
            date = calendar.getTime();
            begDate = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);
        } else {
            begDate = context.getData("begDat").toString();
        }

        map.put("begDat",begDate);
        List<Map<String, Object>> gdLotTxnJnls = get(GdLotTxnJnlRepository.class).qryLotTxnJnl(map);
        if (CollectionUtils.isEmpty(gdLotTxnJnls)){
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "没符合查询条件的记录");
        } else { 
            List<Map<String, Object>> qryResultList = new ArrayList<Map<String,Object>>();
                for(Map<String, Object> maps:gdLotTxnJnls){
                Map<String, Object> tempMap = new HashMap<String, Object>();
                tempMap.put("gameId", maps.get("Game_Id"));
                tempMap.put("drawId", maps.get("DRAW_ID"));
                tempMap.put("kenoId", maps.get("KENO_ID"));
                tempMap.put("betMul", maps.get("BET_MUL"));
                tempMap.put("betLin", maps.get("BET_LIN"));
                tempMap.put("playId", maps.get("Play_Id"));
                tempMap.put("txnAmt", maps.get("PRZ_AMT")); 
                tempMap.put("txnLog", maps.get("T_Log_No"));
                tempMap.put("drawNm", maps.get("Draw_Nm"));
                qryResultList.add(tempMap);
            }
            context.setData("rec", qryResultList);
            context.setData("rspCod", Constants.RESPONSE_CODE_SUCC);
            context.setData(ParamKeys.RSP_MSG, Constants.RESPONSE_MSG);
        }
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}

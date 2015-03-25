package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
import com.bocom.bbip.utils.BeanUtils;
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
       // map.put("gameId",context.getData("gameId").toString());前端没有gameID
        map.put("crdNo",context.getData("crdNo").toString());
        String endDate =context.getData("endDat").toString().replace("-", "").trim();
        if (StringUtils.isEmpty(endDate)){
            Date date = new Date();
            endDate = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);
        }
         map.put("endDat",endDate);
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
        int pageSize = context.getData(ParamKeys.PAGE_SIZE);
        int pageNum = context.getData(ParamKeys.PAGE_NUM);
        Pageable page =new PageRequest(pageNum, pageSize);
        List<GdLotTxnJnl>gdLotTxnJnls = get(GdLotTxnJnlRepository.class).qryLotTxnJnl(page,map);
        if (CollectionUtils.isEmpty(gdLotTxnJnls)){
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "没符合查询条件的记录");
        } else { 
            List<Map<String,Object>> resultList=(List<Map<String, Object>>) BeanUtils.toMaps(gdLotTxnJnls);
            context.setData("rec", resultList);
            context.setData(ParamKeys.RSP_CDE, Constants.RESPONSE_CODE_SUCC);
            context.setData(ParamKeys.RSP_MSG, Constants.RESPONSE_MSG);
        }
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}

package com.bocom.bbip.gdeupsb.action.vech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.entity.GDVechIndentInfo;
import com.bocom.bbip.gdeupsb.repository.GDVechIndentInfoRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 长途汽车订单查询 (不调用接口直接查询本地订单表)
 * Date 2015-03-13
 * @author Guilin.Li
 * @version 1.0.0
 */
public class QryEleticketOrder extends BaseAction{

    @Autowired
    GDVechIndentInfoRepository vechIndentInfoRepository;
    
    @Override
    public void execute(Context context) throws CoreException, CoreRuntimeException {

        log.info("QryEleticketOrder Start!!");
        GDVechIndentInfo  vechIndentInfo = new GDVechIndentInfo();
        vechIndentInfo.setClaNo(context.getData("claNo").toString());//班次
        vechIndentInfo.setClaDte(context.getData("claDte").toString());//乘车日期
        vechIndentInfo.setUserId(context.getData("userId").toString());//乘车人身份证
        vechIndentInfo.setUserNam(context.getData("userNam").toString());//乘车人姓名
        
        List<GDVechIndentInfo> vechIndentInfos = vechIndentInfoRepository.find(vechIndentInfo);
        List<Map<String, Object>> qryResultList = new ArrayList<Map<String,Object>>();
        for(GDVechIndentInfo indentInfo:vechIndentInfos){
        //车票信息：
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("orderId", indentInfo.getOrderId());//订单号 ORDER_ID
        tempMap.put("claDte", indentInfo.getClaDte());//班次 CLA_NO
        tempMap.put("claNo", indentInfo.getClaNo());//班次时间 CLA_TIM
        tempMap.put("opeTyp", indentInfo.getOpeTyp());//运营方式OPE_TYP
        tempMap.put("claLev", indentInfo.getClaLev());//        班次档次CLA_LEV
        tempMap.put("ridStatNo", indentInfo.getRidStatNo());//        发车地点RID_STAT_NO
        tempMap.put("destination", indentInfo.getDesNam());//        到站名称DES_NAM
        tempMap.put("busStopNo", indentInfo.getBusStopNo());//        卡位/检票口 BUS_STOP_NO/TIC_ENT_NO
        tempMap.put("ticEntNo",indentInfo.getTicEntNo());
        tempMap.put("ticPri", indentInfo.getTicPri());//        票价 TIC_PRI
        //取票人信息：
        tempMap.put("userNam", indentInfo.getUserNam());//       姓名 USER_NAM
        tempMap.put("userId", indentInfo.getUserId());//       身份证号 USER_ID
        tempMap.put("mobile", indentInfo.getMobile());//       手机号码MOBILE
        qryResultList.add(tempMap);
    }
    context.setData("rec", qryResultList);
    }

}

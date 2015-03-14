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
 * 长途汽车订单查询
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
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("claDte", indentInfo.getClaDte());
        tempMap.put("claNo", indentInfo.getClaNo());
        qryResultList.add(tempMap);
    }
    context.setData("rec", qryResultList);
    }

}

package com.bocom.bbip.gdeupsb.action.vech;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 长途汽车票车次查询
 * Date 2015-03-15
 * @author Guilin.Li
 * @version 1.0.0
 */
public class QryCarSequenceAction  extends BaseAction{
    /**
     *  查询成功，以字符串数组的形式返回班次信息，最多可返回60个班次信息
     * 一个字符串为一条班次信息，中间以“,”分割
     */
     @Override
    public void execute(Context context) throws CoreException, CoreRuntimeException {
        log.info("QryCarSequenceAction Start!!");
        Date date = DateUtils.parse(context.getData("qryDate").toString(), DateUtils.STYLE_yyyyMMdd);
        String dateString = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);//查询日期
        String timeString = context.getData("qryTime").toString();//班次时间
        String destination = context.getData("des").toString();//>到站代码（或者拼音，或者汉字
        String ownerdepot = context.getData("ownerdepot").toString();//班次所属站代码
        
        //TODO // <param name="storeNo">商户编号</param>
        // <param name="terminalNo">终端编号</param>
        // <param name="sign">签名：sign=md5(storeNo + terminalNo + date + md5(password))  password 为终端密码</param>

        //易票联长途汽车班车查询接口 TODO;
        String[] schemes = terminalQuerySchemeInfo(dateString, destination, ownerdepot, timeString);
        int schemesLength =schemes.length;
        if (schemesLength<1) {
            context.setData(ParamKeys.RSP_MSG, "对不起，按您输入的条件没有查询到车次");
            return;
        }
        //车次信息List
        List<Map<String, Object>> resultMapLst =new ArrayList<Map<String, Object>>();
        for (int i=0; i<schemesLength; i++){
            String[] infoArr =new String[16];
            Map<String, Object> tempMap = new HashMap<String, Object>();
            //一个字符串为一条班次信息
            String scheme = schemes[schemesLength];
            //一条班次信息，中间以“,”分割 放入infoArr数组中
            for (int j=0; j<15; j++){
                infoArr[j]=scheme.substring(0,scheme.indexOf(","));
                scheme= scheme.substring(scheme.indexOf(",")+1);
                if (j==15){
                    infoArr[15]=scheme;
                }
            }
            tempMap.put("ticSouStatNo", infoArr[0]);
            tempMap.put("claNo", infoArr[1]);
            tempMap.put("claNam", infoArr[2]);
            tempMap.put("claTim", infoArr[3]);
            tempMap.put("claprop", infoArr[4]);
            tempMap.put("claTyp", infoArr[5]);
            tempMap.put("opeTyp", infoArr[6]);
            tempMap.put("claLev", infoArr[7]);
            tempMap.put("destNo", infoArr[8]);
            tempMap.put("destNam", infoArr[9]);
            tempMap.put("endNam", infoArr[10]);
            tempMap.put("souStatNam", infoArr[11]);
            tempMap.put("surTicNum", infoArr[12]);
            tempMap.put("ridStatNo", infoArr[13]);// 目前与票源客运站编码一致
            tempMap.put("ownerdepot", infoArr[14]);// 目前与票源客运站编码一致
            tempMap.put("remark", infoArr[15]);
            resultMapLst.add(tempMap);
        }
        context.setData("resultList", resultMapLst);
    }
     
    //模拟易票联长途汽车班车查询接口 
     public String[] terminalQuerySchemeInfo (String date, String Station, String ownerdepot, String time ){
         String[] arr = {"","","","","","","","","","","","","","","",""};
         return arr;
     }
}

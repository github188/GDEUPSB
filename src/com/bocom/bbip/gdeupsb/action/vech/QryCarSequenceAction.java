package com.bocom.bbip.gdeupsb.action.vech;

import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
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
    
     @Override
    public void execute(Context context) throws CoreException, CoreRuntimeException {
        log.info("QryCarSequenceAction Start!!");
        Date date = DateUtils.parse(context.getData("startDate").toString(), DateUtils.STYLE_yyyyMMdd);
        String dateString = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);
        String timeString = context.getData("startTime").toString();
        String station = context.getData("destination").toString();
        String ownerdepot = context.getData("ownerdepot").toString();
        //易票联长途汽车班车查询接口 TODO;
        String[] schemes = terminalQuerySchemeInfo(dateString, station, ownerdepot, timeString);
        int schemesLength =schemes.length;
        if (schemesLength<1) {
            context.setData(ParamKeys.RSP_MSG, "对不起，按您输入的条件没有查询到车次");
            return;
        }
        for (int i=1; i<=schemesLength; i++){
            String scheme = schemes[schemesLength];
            context.setData("", scheme.substring(0,4));
        }
        
    }
     
    //模拟易票联长途汽车班车查询接口 
     public String[] terminalQuerySchemeInfo (String date, String Station, String ownerdepot, String time ){
         String[] arr = {"","","","","","",""};
         return arr;
     }
}

package com.bocom.bbip.gdeupsb.action.vech;

import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 长途汽车票价格查询
 * Date 2015-03-15
 * @author Guilin.Li
 * @version 1.0.0
 */
public class QryCarTicketPriceAction extends BaseAction{
    /**
     * 根据查询得到的班次数据（班次号），到客运站取到对应班次的票价信息，
     * 其中还包含其它一些乘车有关信息，如停车卡位、检票口、目的站（这三个数据在车票的打印过程中要用到）
     */
    @Override
   public void execute(Context context) throws CoreException, CoreRuntimeException {
       log.info("QryCarTicketPriceAction Start!!");
       Date date = DateUtils.parse(context.getData("startDate").toString(), DateUtils.STYLE_yyyyMMdd);
       String dateString = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);
       String classesNo = context.getData("classesNo").toString();
       String destinationNo = context.getData("destinationNo").toString();
       String ticSouStatNo = context.getData("ticSouStatNo").toString();
       String ownerdepot = context.getData("ownerdepot").toString();
       String souStatNam = context.getData("souStatNam").toString();
       
       //易票联长途汽车查询票价接口 TODO;
       String ticketPrice = terminalGetPrice(dateString, classesNo, destinationNo, ticSouStatNo,ownerdepot,souStatNam);

       if (StringUtil.isEmpty(ticketPrice)) {
           context.setData(ParamKeys.RSP_MSG, "对不起，按您输入的条件没有查询到车次票价信息，请查证后再次查询！");
           return;
       }
       String[] infoArr = {"","","",""};
       for (int i=0; i<3; i++){
           infoArr[i]=ticketPrice.substring(0,ticketPrice.indexOf(","));
           ticketPrice= ticketPrice.substring(ticketPrice.indexOf(",")+1);
           if (i==2){
               infoArr[3]=ticketPrice;
           }
       }
       context.setData("ticketPrice", infoArr[0]);
       context.setData("busStopNo", infoArr[1]);
       context.setData("ticketEntranceNo", infoArr[2]);
       context.setData("bunkerSurchCost", infoArr[3]);
   }

   //模拟易票联长途汽车班车票价查询接口 
    public String terminalGetPrice (String date, String Station, String ownerdepot, String time ,String ticSouStatNo,String ownerpot){
        String ticketPrice = "11111111,2>>2,3<><>3,4++++4";
        return ticketPrice;
    }
}

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
 * 长途汽车票价格查询（嵌套交易） 
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
       Date date = DateUtils.parse(context.getData("qryDate").toString(), DateUtils.STYLE_yyyyMMdd);
       String dateString = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);//查询日期
       String claNo = context.getData("claNo").toString();//班次（或者线路）编号
       String destinationNo = context.getData("desNo").toString();//到站站点编码
       String ticSouStatNo = context.getData("ticSouStatNo").toString();//票源客运站编码
       String ownerdepot = context.getData("ownerdepot").toString();//班次所属站代码
       String souStatNam = context.getData("souStaNam").toString();//发车区域名称
        // TODO; <param name="storeNo">商户编号</param>
       // <param name="terminalNo">终端编号</param>
       // <param name="sign">签名：sign=md5(storeNo + terminalNo + date + md5(password))  password 为终端密码</param>

       //易票联长途汽车查询票价接口 TODO;
       String ticketPrice = terminalGetPrice(dateString, claNo, destinationNo, ticSouStatNo,ownerdepot,souStatNam);

       if (StringUtil.isEmpty(ticketPrice)) {
           context.setData(ParamKeys.RSP_MSG, "对不起，按您输入的条件没有查询到车次票价信息，请查证后再次查询！");
           return;
       }
       //查询成功，字符串信息（使用“,”分割符）
       String[] infoArr = {"","","",""};
       for (int i=0; i<3; i++){
           infoArr[i]=ticketPrice.substring(0,ticketPrice.indexOf(","));
           ticketPrice= ticketPrice.substring(ticketPrice.indexOf(",")+1);
           if (i==2){
               infoArr[3]=ticketPrice;
           }
       }
       context.setData("ticPri", infoArr[0]);
       context.setData("busStopNo", infoArr[1]);
       context.setData("ticEntNo", infoArr[2]);
       context.setData("bunSurPri", infoArr[3]);
   }

   //模拟易票联长途汽车班车票价查询接口 
    public String terminalGetPrice (String date, String Station, String ownerdepot, String time ,String ticSouStatNo,String ownerpot){
        String ticketPrice = "80,3,8,5";//test数据
        return ticketPrice;
    }
}

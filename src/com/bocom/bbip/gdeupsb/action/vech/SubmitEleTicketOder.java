package com.bocom.bbip.gdeupsb.action.vech;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDVechIndentInfo;
import com.bocom.bbip.gdeupsb.entity.GdLotChkCtl;
import com.bocom.bbip.gdeupsb.repository.GDVechIndentInfoRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.util.Hex;

/**
 * 长途汽车订单提交
 * Date 2015-03-15
 * @author Guilin.Li
 * @version 1.0.0
 */
public class SubmitEleTicketOder extends BaseAction{

    @Autowired
    GDVechIndentInfoRepository vechIndentInfoRepository;
    
    @Override
    public void execute(Context context) throws CoreException, CoreRuntimeException {
        
        String storeId = context.getData("storeId").toString();//商家ID
        String terminalId = context.getData("terminalId").toString();//终端ID
        String account = context.getData("account").toString();//商户帐户
        String requestTime = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss);//请求时间
        String operType ="1";//操作类型
        String pwd = context.getData("pwd").toString();
        String storeSeq = context.getData("storeSeq").toString();//商户订单号
        String sign =null;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update((storeId+terminalId+operType+requestTime+storeSeq +pwd.getBytes()).getBytes());
            sign = Hex.encode(md.digest());
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      
       //订单信息ETicketOrder
        String orderId = context.getData("orderId").toString();
        String cardId = context.getData("cardId").toString();
        String priceId = context.getData("priceId").toString();
        String count = context.getData("buyNum").toString();//购买数量count
        String mobile = context.getData("mobile").toString();
        String userName = context.getData("userNam").toString();
        String voucher ="0";//取货凭证类型 规定为0 
        String voucherCode = context.getData("userId").toString();//取货凭证号 系统定死为身份证因为需求没有选择其他凭证的下拉框
        String payType = "3";//支付方式
        String orderState = "0";//订单状态
        //查询票价 
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
        context.setData("ticPri", infoArr[0]);//票价
        context.setData("busStopNo", infoArr[1]);//停车卡位
        context.setData("ticEntNo", infoArr[2]);// 检票口
        context.setData("bunSurPri", infoArr[3]);//燃油附加费金额
        BigDecimal bunSurPri = new BigDecimal(0.0);
        bunSurPri = bunSurPri.add(BigDecimal.valueOf(Double.valueOf(infoArr[3])));
       
        //易票联 电子票下单报文拼装
        
        
        //订单表数据插入
        GDVechIndentInfo  vechIndentInfo = new GDVechIndentInfo();
        vechIndentInfo.setBuyNum(count);
        vechIndentInfo.setTxnTim(requestTime);
        vechIndentInfo.setClaDte(context.getData("claDte").toString());
        vechIndentInfo.setClaTim(context.getData("claTim").toString());
        vechIndentInfo.setMobile(mobile);
        vechIndentInfo.setUserId(voucherCode);
        vechIndentInfo.setUserNam(userName);
        vechIndentInfo.setPayType(payType);
        vechIndentInfo.setOrdSta(orderState);
        vechIndentInfo.setVoucher(voucher);
        vechIndentInfo.setBusStopNo(context.getData("busStopNo").toString());
        vechIndentInfo.setTicEntNo(context.getData("ticEntNo").toString());
        vechIndentInfo.setBunSurPri(bunSurPri);
        vechIndentInfoRepository.insert(vechIndentInfo);
    }
    
    //模拟易票联长途汽车班车票价查询接口 
    private String terminalGetPrice (String date, String Station, String ownerdepot, String time ,String ticSouStatNo,String ownerpot){
        String ticketPrice = "80,3,8,5";//test数据
        return ticketPrice;
    }
}

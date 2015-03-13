package com.bocom.bbip.gdeupsb.action.vech;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.utils.DateUtils;
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

    @Override
    public void execute(Context context) throws CoreException, CoreRuntimeException {
        
        String storeId = context.getData("storeId").toString();//商家ID
        String terminalId = context.getData("terminalId").toString();//终端ID
        String account = context.getData("account").toString();//商户帐户
        String requestTime = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss);//请求时间
        String operType = context.getData("operTyp").toString();//操作类型
        String pwd = context.getData("pwd").toString();
        String storeSeq = context.getData("storeSeq").toString();
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
        String buyNumber = context.getData("buyNum").toString();//购买数量count
      
        }
}

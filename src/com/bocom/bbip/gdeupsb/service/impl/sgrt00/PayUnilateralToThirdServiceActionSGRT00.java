package com.bocom.bbip.gdeupsb.service.impl.sgrt00;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.single.PayUnilateralToThirdService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.jump.bp.core.Context;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.CoreException;

public class PayUnilateralToThirdServiceActionSGRT00 implements PayUnilateralToThirdService{

    private final static Logger log = LoggerFactory.getLogger(PayUnilateralToThirdServiceActionSGRT00.class);
    
    @Autowired
    BGSPServiceAccessObject serviceAccess;

    @Override
    public Map<String, Object> aftPayToTHD(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain,
            Context context) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Map<String, Object> prePayToTHD(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain,
            Context context) throws CoreException {
        log.info("PayUnilateralToBankServiceActionSGRT00 start!");
        
        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        //上面公共报文头，下面报文体
        context.setData("custId", context.getData("CUST_ID"));
        context.setData("tCusNm", context.getData("CUST_NAME"));
        context.setData("cusTyp", context.getData("CUST_TYPE"));
        context.setData("pasId", context.getData("PASS_ID"));
        context.setData("liceId", context.getData("LICE_ID"));
        context.setData("accTyp", context.getData("ACC_TYPE"));
        context.setData("actNo", context.getData("ACC"));
        context.setData("DevId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));

        context.setData(ParamKeys.THD_TXN_CDE,"483805");
        //TODO;
        context.setData("tCusId",context.getData("cusId"));
        //TODO; 待确定
        context.setData("rsFld2",context.getData("dptId"));
        context.setData("comNo",context.getData("dptId"));
        context.setData(ParamKeys.TXN_DATE, context.getData(ParamKeys.TXN_TIME).toString().substring(0, 8));


        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cusAc", context.getData("actNo").toString());
        Result accessObject =  serviceAccess.callServiceFlatting("queryListAgentCollectAgreement", map);
        if (null != accessObject) {
            context.setDataMap(accessObject.getPayload());
        } else {
            throw new CoreException("用户未开户！");
        }
        
        context.setData("payMod","0");
        context.setData(ParamKeys.CHL_TYP,"L");//<!--交易渠道类型：L第三方系统-->
        context.setData("vchChk","1");//<!--监督标志由业务上确定-->
        context.setData("cchCod","00000000");
        context.setData("aplCls","438");
        context.setData("mstChk","1");
        context.setData("itgTyp","0");
        context.setData(ParamKeys.TXN_TYP,Constants.RESPONSE_TYPE_SUCC);

        return null;
    }

}


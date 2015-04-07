package com.bocom.bbip.gdeupsb.strategy.sgrt00;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.single.PayUnilateralToBankService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdTbcCusAgtInfo;
import com.bocom.bbip.gdeupsb.repository.GdTbcCusAgtInfoRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class PayUnilateralToBankServiceActionSGRT00 implements PayUnilateralToBankService {

    private final static Logger log = LoggerFactory.getLogger(PayUnilateralToBankServiceActionSGRT00.class);

    @Autowired
    BGSPServiceAccessObject serviceAccess;
    @Autowired
    BBIPPublicServiceImpl publicService;
    @Autowired
    GdTbcCusAgtInfoRepository cusAgtInfoRepository;

    @Override
    public Map<String, Object> aftPayToBank(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain,
            Context context) throws CoreException {
        context.setData(GDParamKeys.RSP_CDE, context.getData("responseCode"));
        if (Constants.RESPONSE_CODE_SUCC.equals(context.getData("responseCode").toString())) {
            context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        } else {
            context.setData(GDParamKeys.RSP_MSG, context.getData("responseMessage"));
        }
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        context.setData("BANK_SEQ", context.getData("sqn"));
        return null;
    }

    @Override
    public Map<String, Object> prePayToBank(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain,
            Context context) throws CoreException {
        context.setData("payMod", "0");
        context.setData(ParamKeys.CHL_TYP, "L");// <!--交易渠道类型：L第三方系统-->
        context.setData("vchChk", "1");// <!--监督标志由业务上确定-->
        context.setData("cchCod", "00000000");
        context.setData("aplCls", "438");
        context.setData("mstChk", "1");
        context.setData("itgTyp", "0");
        context.setData(ParamKeys.COMPANY_NO, context.getData("cAgtNo"));
        context.setData(ParamKeys.TXN_TYP, Constants.RESPONSE_TYPE_SUCC);
        return null;
    }

    @Override
    public Map<String, Object> prepareCheckDeal(CommHeadDomain commheaddomain, PayFeeOnlineDomain payfeeonlinedomain,
            Context context) throws CoreException {
        log.info("PayUnilateralToBankServiceActionSGRT00 start!");
        // 转换
        context.setData("txnTme", DateUtils.parse(context.getData("TRAN_TIME").toString(), DateUtils.STYLE_yyyyMMddHHmmss));
        String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("DPT_ID").toString());
        if (null == cAgtNo) {
            cAgtNo ="4410000560";
        }
        context.setData("cAgtNo", cAgtNo);
        context.setData(ParamKeys.THD_TXN_CDE, "483805");
        // TODO; 不知何用
        context.setData("tCusId", context.getData("CUST_ID"));
        // TODO; 待确定
        context.setData("rsFld2", context.getData("DPT_ID"));
        context.setData(ParamKeys.TXN_DATE, DateUtils.parse(context.getData("TRAN_TIME").toString().substring(0, 8), DateUtils.STYLE_yyyyMMdd));
        context.setData(ParamKeys.THD_CUS_NO, context.getData("CUST_ID"));
        GdTbcCusAgtInfo cusAgtInfo = cusAgtInfoRepository.findOne(context.getData("CUST_ID").toString());
        context.setData(ParamKeys.CUS_AC, cusAgtInfo.getActNo());
        context.setData(ParamKeys.THD_SEQUENCE, publicService.getBBIPSequence());
        context.setData(ParamKeys.BR, context.getData(ParamKeys.BR));
        //context.setData(ParamKeys.TELLER, publicService.getETeller(context.getData(ParamKeys.BR).toString()));
        context.setData(ParamKeys.TELLER, "ABIR148");
        context.setData(ParamKeys.EUPS_BUSS_TYPE, "SGRT00");
        context.setData(ParamKeys.TXN_AMT, context.getData("QTY_TRADE"));

        context.setData(ParamKeys.THD_TXN_CDE, "483805");
        return null;
    }
}

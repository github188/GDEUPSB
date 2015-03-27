
package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBusType;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.infrastructure.shg.OnlineServerStatusSet;
import com.bocom.bbip.eups.infrastructure.shg.OnlineServiceDeal;
import com.bocom.bbip.eups.repository.EupsBusTypeRepository;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.channel.http.support.JsonUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Interceptor;

/**
 * 自定义第三方拦截器（含生活馆渠道）
 * 
 * @version 1.0.0,2014-5-6
 * @author qianc.sdc
 * @since 1.0.0
 */
public class ThdELEC00Interceptor implements Interceptor {

    private Logger log = LoggerFactory.getLogger(ThdELEC00Interceptor.class);

    private String SHG_QUERY_ID = "eups.queryThirdFeeOnline";

    private String SHG_PAY_ID = "eups.payFeeOnline";

    @Autowired
    private BBIPPublicService bbipPublicService;

    @Autowired
    private EupsThdBaseInfoRepository eupsThdBaseInfoRepository;

    @Autowired
    private EupsBusTypeRepository eupsBusTypeRepository;

    /** 生活馆配置相关map */
    private Map<String, String> loadMap;

    @Override
    public void onRequest(Context context) throws CoreException, CoreRuntimeException {
        log.info("=================interceptor start!context=" + context);
        
        //String 日期装换成时间类型
        String thdTxnDate=context.getData(ParamKeys.THD_TXN_DATE);
        if(null != thdTxnDate){
            Date thdTxnDte=DateUtils.parse(context.getData(ParamKeys.THD_TXN_DATE).toString(),DateUtils.STYLE_yyyyMMdd);
            context.setData(ParamKeys.THD_TXN_DATE, thdTxnDte);
        }else{
        	 context.setData(ParamKeys.THD_TXN_DATE, DateUtils.parse(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)));
        }
        String thdTxnDte=DateUtils.format((Date)context.getData(ParamKeys.THD_TXN_DATE),DateUtils.STYLE_yyyyMMdd);
        if(null != context.getData(ParamKeys.THD_TXN_TIME) && null !=thdTxnDate){
            Date thdTxnTme=DateUtils.parse(thdTxnDte+context.getData(ParamKeys.THD_TXN_TIME).toString(),DateUtils.STYLE_yyyyMMddHHmmss);
            context.setData(ParamKeys.THD_TXN_TIME, thdTxnTme);
        }else{
        	context.setData(ParamKeys.THD_TXN_TIME, DateUtils.parse(DateUtils.formatAsTranstime(new Date())));
        }
        
        log.info("start!loadMap=" + this.loadMap);
        // 请求字段处理
        switchChannelCodeReq(context);

        Assert.hasLengthInData(context, ParamKeys.EUPS_BUSS_TYPE, ErrorCodes.EUPS_EUPS_BUS_TYP_NOTEXIST);
        String eupsBusTyp = context.getData(ParamKeys.EUPS_BUSS_TYPE);

        // 生活馆相关处理
        eupsBusTyp = onlinePreDeal(eupsBusTyp, context);

        // 验证EUPS业务类型及对应单位信息是否存在
        comInfoCheck(eupsBusTyp, context);

        // 系统内部数据生成
        systemDateGen(context);
        
        String svrNme=context.getData(ParamKeys.SERVICE_NAME).toString();
        if(svrNme.equals("eups.automaticCancelELEC00")){
        	Date date=new Date();
        	context.setData(ParamKeys.TXN_DTE, DateUtils.parse(DateUtils.formatAsSimpleDate(date)));
        	context.setData(ParamKeys.TXN_TME, DateUtils.parse(DateUtils.formatAsTranstime(date)));
        }
        log.info("============on request end,context=" + context);
    }

    @Override
    public void onResponse(Context context, Throwable arg1) {
        log.info("==============================onResponse start!!");
        log.info("this is onresponse start context" + context);
        if("eups.payUnilateralToBankELEC00".equals(context.getData("svrNme"))){
		        Date txnTme=(Date)context.getData(ParamKeys.TXN_TME);
		        String txnTime=DateUtils.format(txnTme, DateUtils.STYLE_HHmmss);
		        context.setData(ParamKeys.TXN_TME, txnTime);
        }
        // 返回字段处理
        switchChannelCodeRsp(context);

        log.info("channle=" + context.getData(ParamKeys.CHANNEL));
        // 生活馆json处理
        try {
            onlineAftDeal(context);
        } catch (IOException e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        if(context.getData(ParamKeys.SERVICE_NAME).toString().equals("eups.payUnilateralToBankELEC00")){
        		context.setData(ParamKeys.THD_TXN_DATE, DateUtils.format((Date)context.getData(ParamKeys.THD_TXN_DATE), DateUtils.STYLE_yyyyMMdd));
        		context.setData(ParamKeys.THD_TXN_TIME, DateUtils.formatAsHHmmss((Date)context.getData(ParamKeys.THD_TXN_TIME)));
        }
        log.info("interceptor end !!!!!context=" + context);
    }

    /**
     * 读取配置文件key-value
     * 
     * @param location
     * @return
     */
    private Map<String, String> getProperty(InputStream location) {

        Properties prop = new Properties();
        Map<String, String> propMap = new HashMap<String, String>();
        try {
            prop.load(location);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Object key : prop.keySet()) {
            String keyStr = key.toString();
            String value = prop.getProperty(keyStr);
            propMap.put(keyStr, value);
        }
        return propMap;
    }

    private String onlinePreDeal(String eupsBusType, Context context) throws CoreException {
        String chn = context.getData(ParamKeys.CHANNEL);

        if (null == context.getData(ParamKeys.IS_NEED_DEDUCT_FLAG)) {
            context.setData(ParamKeys.IS_NEED_DEDUCT_FLAG, Constants.NEED_DEDUCT_YES);
        }
        context.setData(ParamKeys.SHG_FLAG, Constants.SHGFLAG__NO); // 生活馆标志：1非

        // 生活馆json处理
        if (StringUtils.isNotEmpty(chn)) {
            String channel = chn.trim();
            if (Constants.ADDRESS_SHG_FROM.equals(context.getData(ParamKeys.REQ_SYS_CDE))) {
                eupsBusType = onlineDeal(context, eupsBusType, channel);
            }
        }
        return eupsBusType;
    }

    /**
     * 生活馆相关处理
     * 
     * @throws CoreException
     */
    @SuppressWarnings("unchecked")
    private String onlineDeal(Context context, String eupsBusTyp, String channel) throws CoreException {
        if (Constants.CHL_SHG_SYSTEM.equals(channel) || Constants.CHL_PER_ONLINE.equals(channel)) {
            context.setData(ParamKeys.SHG_FLAG, Constants.SHGFLAG_YES); // 生活馆标志：0是
            log.info("process begin,before json transform,context=" + context);
            // 若eupsBusTyp='needTraFom'则需要进行转换
            if (ParamKeys.ONLINE_BUSTYP_TRANSFORM_FLAG.equals(eupsBusTyp.trim())) {
                if (null == this.loadMap) {
                    InputStream location = this.getClass().getClassLoader().getResourceAsStream("config/interceptor/onlineTransDct.properties");
                    this.loadMap = getProperty(location);
                } else {
                    log.info("this.loadmap=" + this.loadMap);
                }
                String transCode = context.getData(ParamKeys.ONLINE_TRAN_CODE).toString().trim();
                if (this.loadMap.containsKey(transCode)) {
                    eupsBusTyp = this.loadMap.get(transCode);
                } else {
                    throw new CoreException(ErrorCodes.EUPS_BUS_TYP_NOEXIST);
                }
            }
            context.setData(ParamKeys.EUPS_BUSS_TYPE, eupsBusTyp);
            // 请求方流水号
            context.setData(ParamKeys.REQ_JRN_NO, context.getData(ParamKeys.ONLINE_REQ_JNL));
            // br,tmlId处理-使用出账机构代码
            String agencyCode = context.getData(ParamKeys.ONLINE_AGENTCY_CODE);
            if (StringUtils.isNotEmpty(agencyCode)) {
                agencyCode = agencyCode.trim();
                if (agencyCode.length() > 8) {
                    context.setData(ParamKeys.TERMINAL, agencyCode.substring(agencyCode.length() - 7, agencyCode.length()));
                } else {
                    context.setData(ParamKeys.TERMINAL, agencyCode);
                }
            }
            // 交易码转换
            String txnCde = context.getData(ParamKeys.TXN_CODE);
            if (StringUtils.isNotEmpty(txnCde)) {
                txnCde = txnCde.trim();
                txnCde = txnCde.substring(txnCde.length() - 6, txnCde.length());
            }
            context.setData(ParamKeys.TXN_CODE, txnCde);

            if (context.getProcessId().equals(SHG_QUERY_ID)) {
                context.setDataMap(JsonUtils.objectFromJson(context.getData(ParamKeys.ONLINE_REQ_JSON_MESSAGE).toString(), Map.class));
            } else if (context.getProcessId().equals(SHG_PAY_ID)) {
                if (null != context.getData(ParamKeys.ONLINE_PAY_JSON_MESSAGE)) {
                    String jsonMsg = context.getData(ParamKeys.ONLINE_PAY_JSON_MESSAGE).toString().trim();
                    log.info("jsonMsg :"+jsonMsg);
                    // 去掉json中的[],否则JsonUtils无法解析
                    if(jsonMsg.contains("[")&&jsonMsg.contains("]")){
                        jsonMsg=jsonMsg.replace("[", "").replace("]", "");
                    }
                    if (jsonMsg.length() != 0) {
                        context.setDataMap(JsonUtils.objectFromJson(jsonMsg, Map.class));
                    }
                    log.info("context message :"+context.getDataMap());
                }
            }
            // 交易金额转换
            if (null != context.getData(ParamKeys.ONLINE_AMOUNT)) {
                context.setData(ParamKeys.TXN_AMOUNT, context.getData(ParamKeys.ONLINE_AMOUNT).toString().trim());
            }
            // 客户账号转换
            if (null != context.getData(ParamKeys.ONLINE_ACCOUNT)) {
                context.setData(ParamKeys.CUS_AC, context.getData(ParamKeys.ONLINE_ACCOUNT).toString().trim());
            }
            // 机构号转换
            if (null != context.getData(ParamKeys.ONLINE_AGENTCY_CODE)) {
                context.setData(ParamKeys.TXN_ORG_CDE, context.getData(ParamKeys.ONLINE_AGENTCY_CODE).toString().trim());
            }
            // 第三方客户编号转换
            if (null != context.getData(ParamKeys.ONLINE_CUST_NO)) {
                context.setData(ParamKeys.THD_CUSTOMER_NO, context.getData(ParamKeys.ONLINE_CUST_NO).toString().trim());
            }
            // 柜员号，终端号设值
            if (null == context.getData(ParamKeys.TXN_TLR)) {
                String tlr = bbipPublicService.getETeller(context.getData(ParamKeys.BK).toString());
                context.setData(ParamKeys.TXN_TLR, tlr);
            }
            if (null == context.getData(ParamKeys.TERMINAL_NO)) {
                context.setData(ParamKeys.TERMINAL_NO, context.getData(ParamKeys.TXN_TLR));
            }
            context.setData(ParamKeys.TELLER_ID, context.getData(ParamKeys.TXN_TLR));
            // remark-设置原交易流水号及第三方客户标志
            if (null != context.getData(ParamKeys.REMARK)) {
                context.setDataMap(JsonUtils.objectFromJson(context.getData(ParamKeys.REMARK).toString(), Map.class));
            }
            log.info("process begin,after json transform,context=" + context);
        }

        return eupsBusTyp;
    }

    /**
     * 渠道字段转换
     * 
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    private void switchChannelCodeReq(Context context) {
        // 渠道号
        String chn = context.getData(ParamKeys.CHANNEL);
        if (StringUtils.isNotEmpty(chn)) {
            context.setData(ParamKeys.CHL_TYP, chn.trim());
        }
        // 柜员号
        String tlr = context.getData(ParamKeys.TELLER);
        if (StringUtils.isNotEmpty(tlr)) {
            context.setData(ParamKeys.TXN_TLR, tlr.trim());
        }
        // mlId终端号
        String tmlId = context.getData(ParamKeys.TERMINAL);
        String tlrTmlId= context.getData(ParamKeys.BBIP_TERMINAL_NO);
        if (StringUtils.isNotEmpty(tmlId)) {
            context.setData(ParamKeys.TERMINAL_NO, tmlId.trim());
        }else if(StringUtils.isNotEmpty(tlrTmlId)){           
            context.setData(ParamKeys.TERMINAL_NO,tlrTmlId.trim());
        }
        
        context.setData(ParamKeys.SERVICE_NAME, context.getProcessId());
        // 日期:acDate
        String acDte = context.getData(ParamKeys.ACCOUNT_DATE);
        if (StringUtils.isNotEmpty(acDte)) {
            acDte = DateUtils.format(DateUtils.parse(acDte.trim()), DateUtils.STYLE_yyyyMMdd);
        } else {
            acDte = DateUtils.format(bbipPublicService.getAcDate(), DateUtils.STYLE_yyyyMMdd);
        }
        context.setData(ParamKeys.ACCOUNT_DATE, DateUtils.parse(acDte));
        context.setData(ParamKeys.AC_DATE, DateUtils.parse(acDte));

        Date txnTme = new Date();
        if (null == context.getData(ParamKeys.TXN_DATE)) {
            context.setData(ParamKeys.TXN_DATE, txnTme);
        }
        if (null == context.getData(ParamKeys.TXN_TIME)) {
            context.setData(ParamKeys.TXN_TIME, txnTme);
        }
        // ccy
        // context.setData(ParamKeys.CCY, Constants.EPS_CCY_NO);

        // 支付方式转成代收付账户类型
        String payMde = context.getData(ParamKeys.PAY_MDE);
        // 生活馆渠道payMde存放在remark中
        if (Constants.ADDRESS_SHG_FROM.equals(context.getAttribute(ParamKeys.ATTR_SOAP_ADDR_FROM_KEY))) {
            if (Constants.CHL_SHG_SYSTEM.equals(chn) || Constants.CHL_PER_ONLINE.equals(chn)) {// 生活馆渠道
                if (null != context.getData(ParamKeys.REMARK)) {
                    Map<String, Object> remarkMap = JsonUtils.objectFromJson(context.getData("remark").toString(), Map.class);
                    payMde = (String) remarkMap.get(ParamKeys.PAY_MDE);
                }
            }
        }
        String acTyp = new String();
        acTyp = context.getData(ParamKeys.AC_TYP);
        if (StringUtils.isBlank(acTyp)) {
            if (Constants.PAY_MDE_0.equals(payMde)) {
                acTyp = Constants.AC_TYP_00;
            } else if (Constants.PAY_MDE_1.equals(payMde)) {
                acTyp = Constants.AC_TYP_04;
            } else if (Constants.PAY_MDE_2.equals(payMde)) {
                acTyp = Constants.AC_TYP_03;
            } else if (Constants.PAY_MDE_4.equals(payMde)) {
                acTyp = Constants.AC_TYP_05;
            } else if (Constants.PAY_MDE_5.equals(payMde)) {
                acTyp = Constants.AC_TYP_00;
            } else if (Constants.PAY_MDE_6.equals(payMde)) {
                acTyp = Constants.AC_TYP_06;
            } else if (Constants.PAY_MDE_7.equals(payMde)) {
                acTyp = Constants.AC_TYP_XX;
            } else if (Constants.PAY_MDE_8.equals(payMde)) {
                acTyp = Constants.AC_TYP_02;
            } else if (Constants.PAY_MDE_9.equals(payMde)) {
                acTyp = Constants.AC_TYP_TK;
            }
        }
        context.setData(ParamKeys.AC_TYP, acTyp);
    }

    /**
     * 渠道字段转换-返回
     * 
     * @param context
     * @return
     */
    private void switchChannelCodeRsp(Context context) {

        String thdSqn = context.getData(ParamKeys.THD_SEQUENCE);
        if (StringUtils.isNotEmpty(thdSqn)) {
            context.setData(ParamKeys.THD_JRN_NO, thdSqn.trim());
        }
        context.setData(ParamKeys.SERVICE_NAME, context.getProcessId());
        // 第三方对账日期thdRcnTme
        Timestamp thdRcnTme = context.getData(ParamKeys.THD_RCN_TME);
        if (null != thdRcnTme) {
            context.setData(ParamKeys.THD_RCN_DATE, DateUtils.format(thdRcnTme, DateUtils.STYLE_yyyyMMdd));
        }
        // 第三方返回时间thdRspTime
        Date thdTxnDte = context.getData(ParamKeys.THD_TXN_DATE);
        Date thdTxnTme = context.getData(ParamKeys.THD_TXN_TIME);
        if (null != thdTxnDte && null != thdTxnTme) {
            String thdTxnDteStr = DateUtils.format(thdTxnDte, DateUtils.STYLE_yyyyMMdd);
            String thdTxnTmeStr = DateUtils.format(thdTxnTme, DateUtils.STYLE_HHmmss);
            context.setData(ParamKeys.THD_RSP_TME, thdTxnDteStr + thdTxnTmeStr);
        }

        String sqn = context.getData(ParamKeys.SEQUENCE);
        if (StringUtils.isNotEmpty(sqn)) {
            context.setData(ParamKeys.RSP_JRN_NO, sqn);
        }
        log.info("================get sqn:context=" + context);
        Date rspTime = null;
        if(null !=context.getData(ParamKeys.TXN_DATE) && !context.getData(ParamKeys.SERVICE_NAME).toString().equals(SHG_PAY_ID+"ELEC00")){
        		rspTime=DateUtils.parse(context.getData(ParamKeys.TXN_DATE).toString());
        }else if(null !=context.getData(ParamKeys.TXN_DATE) && context.getData(ParamKeys.SERVICE_NAME).toString().equals(SHG_PAY_ID+"ELEC00") && context.getData(ParamKeys.PAY_MDE).toString().equals("4")){
        		rspTime=(Date)context.getData(ParamKeys.TXN_DATE);
        }
        if (null != rspTime) {
            context.setData(ParamKeys.RESPONSE_TIME, rspTime);
        } else {
            context.setData(ParamKeys.RESPONSE_TIME, new Date());
        }
        String pmpFlowNoRsp = context.getData(ParamKeys.ONLINE_RSP_JNL);
        if (StringUtils.isEmpty(pmpFlowNoRsp)) {
            context.setData(ParamKeys.ONLINE_RSP_JNL, context.getData(ParamKeys.ONLINE_REQ_JNL));
        }
        if (null != context.getData(ParamKeys.OWE_FEE_AMT)) {
            String oweFeeAmt = context.getData(ParamKeys.OWE_FEE_AMT).toString();
            BigDecimal amt = new BigDecimal(oweFeeAmt.trim());
            amt = amt.setScale(2, BigDecimal.ROUND_HALF_UP);
            context.setData(ParamKeys.OWE_FEE_AMT, amt.toString());
        }
        if(context.getData(ParamKeys.SERVICE_NAME).toString().equals(SHG_PAY_ID+"ELEC00")){
    		context.setData("thdTxnDte", DateUtils.format(thdTxnDte, DateUtils.STYLE_yyyyMMdd));
    		context.setData("thdTxnTme", DateUtils.formatAsHHmmss(thdTxnTme));
    		context.setData("txnDte",DateUtils.format((Date)context.getData("txnDte"),DateUtils.STYLE_SIMPLE_DATE));
    		context.setData("txnTme",context.getData("txnTme").toString().substring(11,19));
        }
    }

    /**
     * 验证EUPS业务类型及对应单位信息是否存在
     * 
     * @param eupsBusTyp
     * @param context
     * @throws CoreException
     */
    private void comInfoCheck(String eupsBusTyp, Context context) throws CoreException {
        log.info("eupsbusTyp=[" + eupsBusTyp + "]");
        EupsBusType eupsBusTypt = new EupsBusType();
        eupsBusTypt.setEupsBusTyp(eupsBusTyp);
        eupsBusTypt.setUseSts(Constants.USE_STS_NORMAL);

        List<EupsBusType> resultList = eupsBusTypeRepository.find(eupsBusTypt);
        if (CollectionUtils.isNotEmpty(resultList)) {
            // 验证第三方基本信息
            EupsThdBaseInfo eupsThdBaseInfo = new EupsThdBaseInfo();
            eupsThdBaseInfo.setEupsBusTyp(eupsBusTyp);
            eupsThdBaseInfo.setUseSts(Constants.USE_STS_NORMAL);

            List<EupsThdBaseInfo> baseInfos = eupsThdBaseInfoRepository.find(eupsThdBaseInfo);
            if (CollectionUtils.isNotEmpty(baseInfos)) {
                // 查找记账系统信息，获取商户号，终端号等
                context.setData(ParamKeys.COMPANY_NO, baseInfos.get(0).getComNo());
                context.setData(ParamKeys.BUSS_KIND, baseInfos.get(0).getBusKnd());
                context.setData(ParamKeys.CMU_MDE, baseInfos.get(0).getCmuMde());
                context.setData(ParamKeys.THD_IP_ADR, baseInfos.get(0).getThdIpAdr());
                context.setData(ParamKeys.BID_POT, baseInfos.get(0).getBidPot());
                context.setData(ParamKeys.BIL_VOU_FLAG, baseInfos.get(0).getBilVouFlg());
            }
        } else {
            throw new CoreException(ErrorCodes.EUPS_USE_STS_ERROR);
        }
    }

    /**
     * 请求方系统码设置，BBIP流水号设置，context state设置
     * 
     * @param context
     */
    private void systemDateGen(Context context) {
        // 获取BBIP流水号
        String sqn = context.getData(ParamKeys.SEQUENCE);
        if (StringUtils.isBlank(sqn)) {
            sqn = bbipPublicService.getBBIPSequence();
            context.setData(ParamKeys.SEQUENCE, sqn);
        }
        log.info("sqn is:" + sqn);
        // 统一在拦截器中增加了business processing state normal 避免在每个action中都重复定义.
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }

    private void onlineAftDeal(Context context) throws IOException {
        if (Constants.SHGFLAG_YES.equals(context.getData(ParamKeys.SHG_FLAG))) {
            log.info("start onlineAftDeal!..");
            String processId = context.getProcessId();
            // 查询，缴费的返回信息处理
            OnlineServerStatusSet onlineServerStatusSet = new OnlineServerStatusSet();
            if (processId.equals(SHG_PAY_ID)) {
                onlineServerStatusSet.statusSet(context);
            } else if (processId.equals(SHG_QUERY_ID)) {
                onlineServerStatusSet.remarkSet(context);
            }
            OnlineServiceDeal serviceDeal = new OnlineServiceDeal();
            log.info("prepare to do onlineServiceDeal!..");
            serviceDeal.onlineServiceDeal(context);
            log.info("onlineAftDeal end!");
        }
    }

    public void setLoadMap(Map<String, String> loadMap) {
        this.loadMap = loadMap;
    }

}

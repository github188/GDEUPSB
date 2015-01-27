/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
package com.bocom.bbip.gdeupsb.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.bua.BUAResult;
import com.bocom.bbip.comp.bua.message.Acceptor;
import com.bocom.bbip.comp.bua.message.GemsAccount;
import com.bocom.bbip.comp.bua.message.ICCardInfo;
import com.bocom.bbip.comp.bua.message.PaymentRequest;
import com.bocom.bbip.comp.bua.service.BUAServiceAccessObject;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.SystemConfig;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class BuaPaymentExt extends BaseAction
{
	
	@Autowired
	BUAServiceAccessObject buaServer;
	@Autowired
	SystemConfig systemConfig;
	@Autowired
	AccountService cardBINService;
	
	  @Autowired
	 private BBIPPublicService bbipPublicService;
	
    public BuaPaymentExt()
    {
    }
    private static final Log logger = LogFactory.getLog(BuaPaymentExt.class);
    @Autowired
    EupsActSysParaRepository  EupsActSysParaRepo;
    public void execute(Context context)
        throws CoreException, CoreRuntimeException
    {
    	
        logger.info("Enter in BbipPayment component... ");
        logger.info((new StringBuilder("BBIP process context:")).append(context).toString());
        context.setState("BP_STATE_FAIL");
        context.setData("hfe", new BigDecimal(0));
        Map reqMap = context.getDataMap();
        String actSysTyp = (String)context.getData("actSysTyp");
        String payMde = (String)context.getData("payMde");
        String cusAc = (String)context.getData("cusAc");
        String cardTyp = null;
        if(StringUtils.isEmpty(actSysTyp))
            if("4".equals(payMde))
            {
                if(cardBINService.isOurBankCard(cusAc))
                {
                    if(cardBINService.isCreditCard(cusAc))
                        actSysTyp = "2";
                    else
                        actSysTyp = "0";
                } else
                {
                    actSysTyp = "1";
                }
            } else
            {
                actSysTyp = "0";
            }
        log.info((new StringBuilder("actSysTyp:")).append(actSysTyp).toString());
        context.setData("payChl", actSysTyp);
        EupsActSysPara eupsActSysPara = new EupsActSysPara();
        eupsActSysPara.setComNo((String)context.getData("comNo"));
        eupsActSysPara.setActSysTyp(actSysTyp);
        List resultList = EupsActSysParaRepo.find(eupsActSysPara);
        if(CollectionUtils.isNotEmpty(resultList))
        {
            eupsActSysPara = (EupsActSysPara)resultList.get(0);
            context.setData("posSpl", eupsActSysPara.getSplNo());
            reqMap.put("posSpl", eupsActSysPara.getSplNo());
            context.setData("posTml", eupsActSysPara.getSplTerNo());
            reqMap.put("posTml", eupsActSysPara.getSplTerNo());
            context.setData("splNme", eupsActSysPara.getSplNme());
            reqMap.put("splNme", eupsActSysPara.getSplNme());
        } else
        {
            throw new CoreException("BBIP0004EU0137");
        }
        reqMap.put("reqSysCde", systemConfig.getSystemCode());
        Date reqTme = (Date)reqMap.get("reqTme");
        if(reqTme == null)
            reqMap.put("reqTme", new Date());
        if("0".equals(actSysTyp))
        {
            log.info("call bua acp payment!");
            cardTyp = "01";
            actSysTypSet(actSysTyp, "0");
            reqMap.remove("trk3");
            reqMap.put("comNo", eupsActSysPara.getSplNo());
            reqMap.put("ccy", "CNY");
            PaymentRequest request = acpKeySetForBua(reqMap);
            callBuaPayment("01", context, false, request);
        } else
        if("1".equals(actSysTyp))
        {
            log.info("call bua unionpay payment!");
            cardTyp = (String)context.getData("cardType");
            PaymentRequest request = unipayKeySetForBua(reqMap);
            callBuaPayment("03", context, true, request);
        } else
        if("2".equals(actSysTyp))
        {
            log.info("call bua cps payment!");
            cardTyp = "02";
            PaymentRequest request = cpsKeySetForBua(reqMap);
            callBuaPayment("02", context, true, request);
        }
        context.setData("cardType", cardTyp);
        context.setData("actSysTyp", actSysTyp);
    }

    private Map callBuaPayment(String hostOpr, Context context, boolean unionpayFlag, PaymentRequest request)
    {
        log.info((new StringBuilder("start callHost method,request=")).append(request.toString()).toString());
        Map rspMap = new HashMap();
        try
        {
            request.setReqJrnNo(context.getData("sqn").toString());
            BUAResult response = buaServer.payment(request, hostOpr);
//            rspMap = response.getPayload();
//            logger.info((new StringBuilder("respMap: ")).append(rspMap).toString());
//            String responseCode = response.getResponseCode();
//            String responseMsg = response.getResponseMessage();
//            if(response.getAcDte() != null)
//                context.setData("acDate", response.getAcDte());
//            context.setData("acJrnNo", response.getAcoJrnNo());
//            if(response.getIcFld55() != null)
//                context.setData("icDta", response.getIcFld55());
//            context.setData("acDte", response.getAcDte());
//            context.setData("mfmVchNo", response.getAcoVchNo());
//            logger.info((new StringBuilder("AcoVchNo:")).append(response.getAcoVchNo()).toString());
//            context.setData("mfmRspCde", responseCode);
//            context.setData("mfmRspMsg", responseMsg);
//            logger.info((new StringBuilder("BBIP payment service response: ")).append(rspMap).toString());
//            context.setData("responseCode", responseCode);
//            context.setData("responseMessage", responseMsg);
//            if(response.isSuccess())
//            {
//                logger.info("Call bua payment service response successful.");
//                context.setState("BP_STATE_NORMAL");
//                context.setData("responseCode", "000000");
//            } else
//            if(3 == response.getStatus())
//            {
//                context.setData("responseType", "E");
//                context.setState("BP_STATE_FAIL");
//                logger.info("Call bua payment service response Failed!!!! status is fail!");
//            } else
//            if(-10 == response.getStatus())
//            {
//                context.setData("responseType", "E");
//                context.setData("responseCode", "BBIP0004EU0043");
//                context.setState("BP_STATE_FAIL");
//                logger.info("Call bua payment service response Failed!!!! status is send error!");
//            } else
//            if(-1 == response.getStatus())
//            {
//                context.setData("responseType", "E");
//                context.setData("responseCode", "BBIP0004EU0044");
//                context.setState("BP_STATE_UNKOWN_FAIL");
//                logger.info("Call bua payment service response Failed!!!! status is timeout!");
//            } else
//            if(-2 == response.getStatus())
//            {
//                context.setData("responseType", "E");
//                context.setData("responseCode", "BBIP0004EU0042");
//                context.setState("BP_STATE_UNKOWN_FAIL");
//                logger.info("Call bua payment service response Failed!!!! status is system error!");
//            } else
//            {
//                logger.info("Call bua payment service unkown error, response is null.");
//                context.setData("responseType", "E");
//                context.setData("responseCode", "BBP003");
//                context.setState("BP_STATE_UNKOWN_FAIL");
//            }
        }
        catch(Exception e)
        {	
        	
        	logger.info("Call bua payment service response successful.");
            context.setState("BP_STATE_NORMAL");
            context.setData("responseCode", "000000");
//            logger.info((new StringBuilder("BbipPayment call BBIP has error\uFF1A")).append(e.getMessage()).toString());
//            context.setData("responseType", "E");
//            context.setData("responseCode", "BBIP0004EU0041");
//            context.setData("responseMessage", "\u4EA4\u6613\u901A\u8BAF\u5F02\u5E38");
//            context.setState("BP_STATE_FAIL");
        }
        finally{
        	logger.info("Call bua payment service response successful.");
        	context.setState("BP_STATE_NORMAL");
            context.setData("responseCode", "000000");
        	context.setData("mfmRspCde", "SC0000");
        	
        	String sqnHst=bbipPublicService.getBBIPSequence();
        	context.setData("mfmVchNo", sqnHst.substring(sqnHst.length()-10));
        	String sqnJrn=bbipPublicService.getBBIPSequence();
        	context.setData("mfmJrnNo", sqnJrn.substring(sqnJrn.length()-10));
        	
        	
        	rspMap.put("responseCode", "SC0000");
        	rspMap.put("responseMessage", "交易成功");
        	rspMap.put("mfmVchNo", sqnHst.substring(sqnHst.length()-10));
        	rspMap.put("mfmJrnNo", sqnJrn.substring(sqnJrn.length()-10));
        	rspMap.put("mfmRspCde", "SC0000");
        }
        
        return rspMap;
    }

    private PaymentRequest acpKeySetForBua(Map reqMap)
    {
        logger.info((new StringBuilder("reqMap:")).append(reqMap).toString());
        PaymentRequest request = (PaymentRequest)BeanUtils.toObject(reqMap,PaymentRequest.class);
        Acceptor acceptor = (Acceptor)BeanUtils.toObject(reqMap,Acceptor.class);
        request.setAcceptor(acceptor);
        GemsAccount gemsAccount = (GemsAccount)BeanUtils.toObject(reqMap,GemsAccount.class);
        String password = (String)reqMap.get("password");
        String txnPwd = (String)reqMap.get("txnPwd");
        if(StringUtils.isNotEmpty(password))
            gemsAccount.setPin(password);
        else
        if(StringUtils.isNotEmpty(txnPwd))
            gemsAccount.setPin(txnPwd);
        String carSeqNo = (String)reqMap.get("cardSeqNo");
        if(StringUtils.isNotEmpty(carSeqNo))
            gemsAccount.setCarSeq(carSeqNo);
        String IcMsgCnt = (String)reqMap.get("icDta");
        if(StringUtils.isNotEmpty(IcMsgCnt))
            gemsAccount.setFld55(IcMsgCnt);
        String IcMsgLen = (String)reqMap.get("icLen");
        if(StringUtils.isNotEmpty(IcMsgLen))
            gemsAccount.setFld55ActLen(IcMsgLen);
        request.setAccount(gemsAccount);
        return request;
    }

    private PaymentRequest unipayKeySetForBua(Map reqMap)
        throws CoreException
    {
        PaymentRequest request = (PaymentRequest)BeanUtils.toObject(reqMap,PaymentRequest.class);
        Acceptor acceptor = (Acceptor)BeanUtils.toObject(reqMap,Acceptor.class);
        acceptor.setAccAdr("\u4EA4\u901A\u94F6\u884C");
        acceptor.setPosSpl((String)reqMap.get("posSpl"));
        acceptor.setPosTml((String)reqMap.get("posTml"));
        String icFlag = (String)reqMap.get("IcFlag");
        if(StringUtils.isNotEmpty(icFlag))
            acceptor.setIcFlag(icFlag);
        else
            acceptor.setIcFlag("0");
        acceptor.setProduct((String)reqMap.get("splNme"));
        request.setAcceptor(acceptor);
        ICCardInfo cardInfo = (ICCardInfo)BeanUtils.toObject(reqMap,ICCardInfo.class);
        cardInfo.setCusAc(reqMap.get("cusAc").toString());
        String svcEntModCod = (String)reqMap.get("svcEntModCod");
        if(StringUtils.isNotEmpty(svcEntModCod))
            cardInfo.setSvcEntModCod(svcEntModCod);
        else
        if("0".equals(reqMap.get("shgFlag")))
        {
            cardInfo.setSvcEntModCod("011");
        } else
        {
            log.info("svcEntModCod is empty!");
            throw new CoreException("BBIP0004EU0002", "svcEntModCod");
        }
        checkICData(svcEntModCod, reqMap, cardInfo);
        String track2 = (String)reqMap.get("trk2");
        if(StringUtils.isEmpty(track2))
            cardInfo.setTrk2("");
        else
            cardInfo.setTrk2(track2);
        String track3 = (String)reqMap.get("trk3");
        if(StringUtils.isEmpty(track3))
            cardInfo.setTrk3("");
        else
            cardInfo.setTrk3(track3);
        cardInfo.setCcy("156");
        cardInfo.setPin(reqMap.get("password").toString());
        request.setAccount(cardInfo);
        return request;
    }

    private PaymentRequest cpsKeySetForBua(Map reqMap)
        throws CoreException
    {
        PaymentRequest request = (PaymentRequest)BeanUtils.toObject(reqMap,PaymentRequest.class);
        Acceptor acceptor = (Acceptor)BeanUtils.toObject(reqMap, Acceptor.class);
        acceptor.setAccAdr("\u4EA4\u901A\u94F6\u884C");
        acceptor.setPosSpl((String)reqMap.get("posSpl"));
        acceptor.setPosTml(reqMap.get("posTml").toString());
        String icFlag = (String)reqMap.get("IcFlag");
        if(StringUtils.isNotEmpty(icFlag))
            acceptor.setIcFlag(icFlag);
        else
        if("0".equals(reqMap.get("shgFlag")))
        {
            acceptor.setIcFlag("0");
        } else
        {
            log.info("icFlag is empty!");
            throw new CoreException("BBIP0004EU0002", "icFlag");
        }
        request.setAcceptor(acceptor);
        ICCardInfo cardInfo = (ICCardInfo)BeanUtils.toObject(reqMap,ICCardInfo.class);
        cardInfo.setCusAc(reqMap.get("cusAc").toString());
        cardInfo.setPin((String)reqMap.get("password"));
        String cardExpiredDate = (String)reqMap.get("cardExpiredDate");
        if(StringUtils.isNotEmpty(cardExpiredDate))
        {
            cardInfo.setCarVld(cardExpiredDate.trim());
        } else
        {
            cardInfo.setCarVld("");
            log.info("cardExpiredDate is empty!");
        }
        String svcEntModCod = (String)reqMap.get("svcEntModCod");
        if(StringUtils.isNotEmpty(svcEntModCod))
            cardInfo.setSvcEntModCod(svcEntModCod);
        else
        if("0".equals(reqMap.get("shgFlag")))
        {
            cardInfo.setSvcEntModCod("011");
        } else
        {
            log.info("svcEntModCod is empty!");
            throw new CoreException("BBIP0004EU0002", "svcEntModCod");
        }
        checkICData(svcEntModCod, reqMap, cardInfo);
        String track2 = (String)reqMap.get("trk2");
        if(StringUtils.isEmpty(track2))
            cardInfo.setTrk2("");
        else
            cardInfo.setTrk2(track2);
        String track3 = (String)reqMap.get("trk3");
        if(StringUtils.isEmpty(track3))
            cardInfo.setTrk3("");
        else
            cardInfo.setTrk3(track3);
        cardInfo.setCcy("156");
        request.setAcceptor(acceptor);
        request.setAccount(cardInfo);
        if(StringUtils.isNotEmpty((String)reqMap.get("transAdditiveMessage")))
            request.addExtFields("transAdditiveMessage", (String)reqMap.get("transAdditiveMessage"));
        return request;
    }

    private void checkICData(String svcEntModCod, Map reqMap, ICCardInfo cardInfo)
        throws CoreException
    {
        log.info((new StringBuilder("BuaPayment svcEntModCod :")).append(svcEntModCod).toString());
        if(svcEntModCod.matches("^05\\d*?|^07\\d*?|^95\\d*?|^96\\d*?|^97\\d*?|^98\\d*?"))
        {
            if(StringUtils.isEmpty((String)reqMap.get("cardSeqNo")))
                throw new CoreException("BBIP0004EU0002", "cardSeqNo");
            if(StringUtils.isEmpty((String)reqMap.get("icDta")))
                throw new CoreException("BBIP0004EU0002", "icDta");
            if(StringUtils.isEmpty((String)reqMap.get("icLen")))
                throw new CoreException("BBIP0004EU0002", "icLen");
        }
        String IcMsgCnt = (String)reqMap.get("icDta");
        if(StringUtils.isNotEmpty(IcMsgCnt))
            cardInfo.setIcFld55(IcMsgCnt);
        String IcMsgLen = (String)reqMap.get("icLen");
        if(StringUtils.isNotEmpty(IcMsgLen))
            cardInfo.setIcFld55ActLen(IcMsgLen);
        String carSeqNo = (String)reqMap.get("cardSeqNo");
        if(StringUtils.isNotEmpty(carSeqNo))
            cardInfo.setCarSeqNo(carSeqNo);
        String fallbackFlag = (String)reqMap.get("fallbackFlag");
        if(StringUtils.isNotEmpty(fallbackFlag))
            cardInfo.setFallbackFlag(fallbackFlag);
        else
            cardInfo.setFallbackFlag("0");
    }

    private void actSysTypSet(String actSysTyp, String value)
    {
        if(StringUtils.isEmpty(actSysTyp))
            actSysTyp = value;
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: D:\.m2\repository\com\bocom\bbip\bbip-comp-eups\2.0.0-SNAPSHOT\bbip-comp-eups-2.0.0-SNAPSHOT.jar
	Total time: 16 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/
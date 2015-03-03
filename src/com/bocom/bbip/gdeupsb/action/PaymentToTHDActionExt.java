/*jadclipse*/// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.

package com.bocom.bbip.gdeupsb.action;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupsonline.PaymentToTHDAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.utils.ExpCommonUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentToTHDActionExt extends BaseAction
{
	
	 private static Logger logger = LoggerFactory.getLogger(PaymentToTHDAction.class);

    public PaymentToTHDActionExt()
    {
    }

    public void execute(Context context)
        throws CoreException, CoreRuntimeException
    {
        logger.info("==========Payment to the third begin=================");
        logger.info("get call third flag.");
        String conFlag = (String)context.getData("isConnectThird");
        if(conFlag != null && "payOnlineCallThirdFalse".equals(conFlag))
        {
            logger.info("set not need call third, return!");
            String txnSts = (String)context.getData("txnSts");
            String thdTxnSts = (String)context.getData("thdTxnSts");
            if(StringUtils.isEmpty(txnSts))
                context.setData("txnSts", "S");
            if(StringUtils.isEmpty(thdTxnSts))
                context.setData("thdTxnSts", "S");
            logger.info((new StringBuilder("=======txnSts:")).append(txnSts).append("=======thdTxnSts:").append(thdTxnSts).toString());
            return;
        }
        Map rspMap = null;
        try
        {
            ThirdPartyAdaptor callThirdOther = (ThirdPartyAdaptor)get("callThdTradeManager");
            rspMap = callThirdOther.trade(context);
            logger.info((new StringBuilder("==============payment comminication to the third end,the transaction state is [state=")).append(context.getState()).append("]").toString());
//            if(BPState.isBPStateOvertime(context))
//            {
//                logger.info("The third response is overtime.");
//                context.setData("responseType", "E");
//                context.setData("responseCode", "BBIP0004EU0044");
//                context.setData("responseMessage", "\u7B2C\u4E09\u65B9\u901A\u8BAF\u8D85\u65F6,\u4EA4\u6613\u7ED3\u679C\u672A\u77E5,\u8BF7\u67E5\u8BE2\u8D26\u6237\u4F59\u989D");
//                context.setData("thdRspMsg", "\u7B2C\u4E09\u65B9\u901A\u8BAF\u8D85\u65F6,\u4EA4\u6613\u7ED3\u679C\u672A\u77E5,\u8BF7\u67E5\u8BE2\u8D26\u6237\u4F59\u989D");
//                context.setData("txnSts", "F");
//                context.setData("thdTxnSts", "T");
//                context.setState("BP_STATE_OVERTIME");
//            } else
//            if(BPState.isBPStateNormal(context))
//            {
//                if(rspMap == null)
//                {
//                    logger.info("Bypass call THIRD response failed or unknow error.");
//                    context.setData("txnSts", "F");
//                    context.setData("thdTxnSts", "F");
//                    context.setState("BP_STATE_FAIL");
//                }
                logger.info((new StringBuilder("===payment communication to the third success,the third process response: [")).append(rspMap.toString()).append("] ===========").toString());
                context.setDataMap(rspMap);
                context.setData("thdResponseMessage", rspMap);
//                CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
//                String responseCode = rspCdeAction.getThdRspCde(rspMap, context.getData("eupsBusTyp").toString());
                
                String responseCode ="000000";
                log.info((new StringBuilder("third response code=")).append(responseCode).toString());
                if(StringUtils.isEmpty(responseCode))
                    responseCode = "BBIP0004EU0053";
                context.setData("responseCode", responseCode);
                if("000000".equals(responseCode))
                {
                    logger.info("The third process response successful.");
                    context.setData("txnSts", "S");
                    context.setData("thdTxnSts", "S");
                    context.setState("BP_STATE_NORMAL");
                } else
                {
                    logger.info("The third response failed or unknow error.");
                    context.setState("BP_STATE_FAIL");
                    context.setData("txnSts", "F");
                    context.setData("thdTxnSts", "F");
                    context.setData("responseType", "E");
                    context.setData("responseCode", responseCode);
                    String thdRspMsg = null;
                    InputStream errorMsgInput = getClass().getClassLoader().getResourceAsStream("config/msg/errors.properties");
                    Map errorMap = ExpCommonUtils.getPropertyFile(errorMsgInput);
                    thdRspMsg = (String)errorMap.get(responseCode);
                    if(StringUtils.isEmpty(thdRspMsg))
                        thdRspMsg = "\u4EA4\u6613\u5931\u8D25,\u65E0\u9519\u8BEF\u8FD4\u56DE\u4FE1\u606F!";
                    else
                    if(thdRspMsg.length() > 60)
                        thdRspMsg = "\u4EA4\u6613\u5931\u8D25!";
                    context.setData("responseMessage", thdRspMsg);
                    context.setData("thdRspMsg", thdRspMsg);
                    try
                    {
                        errorMsgInput.close();
                    }
                    catch(IOException e)
                    {
                        log.error("errorMsgInput close error!");
                    }
                    logger.error((new StringBuilder("The third response Failed!!!! response map: ")).append(rspMap.toString()).toString());
                }
//            } else
//            {
//                logger.info("===============payment communication to the third return error,process failed==================");
//                context.setState("BP_STATE_FAIL");
//                context.setData("txnSts", "F");
//                context.setData("thdTxnSts", "F");
//                context.setData("responseType", "E");
//                context.setData("responseCode", "BBIP0004EU0053");
//                context.setData("responseMessage", "\u4EA4\u6613\u901A\u8BAF\u5F02\u5E38");
//                context.setData("thdRspMsg", "\u4EA4\u6613\u901A\u8BAF\u5F02\u5E38");
//            }
//            logger.info("============Payment to the third is end============");
        }
        catch(CoreException e)
        {
            logger.info("Bypass call THIRD response failed or unknow error.");
            context.setData("txnSts", "F");
            context.setData("thdTxnSts", "F");
            context.setState("BP_STATE_FAIL");
        }
        EupsTransJournal journal = new EupsTransJournal();
        journal.setSqn((String)context.getData("sqn"));
        journal.setThdTxnSts((String)context.getData("thdTxnSts"));
        journal.setTxnSts((String)context.getData("txnSts"));
        
        get(EupsTransJournalRepository.class).update(journal);
    }


}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\Users\Administrator\.m2\repository\com\bocom\bbip\bbip-comp-eups\2.0.2-SNAPSHOT\bbip-comp-eups-2.0.2-SNAPSHOT.jar
	Total time: 234 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/
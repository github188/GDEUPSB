/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   PrintReportAction.java

package com.bocom.bbip.gdeupsb.action.common;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.file.transfer.mftp.MftpTransferImpl;
import com.bocom.jump.bp.core.*;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GdPrintReportAction extends BaseAction
{

    public GdPrintReportAction()
    {
    }

    public void execute(Context context)
        throws CoreException, CoreRuntimeException
    {
        String reportDir = (String)context.getVariable("reportDir");
        String reportName = (String)context.getVariable("reportName");
        String printReportName = getPrintReportName(context);
        MftpTransfer transfer = new MftpTransferImpl();
//        String printServerIp = bbipPublicService.getParam("BBIP", "BBOS_MFTP_IP");
//        logger.info((new StringBuilder("printServerIp:")).append(printServerIp).toString());
        String reportPath = (new StringBuilder(String.valueOf(reportDir))).append(reportName).toString();
        if(!(new File(reportPath)).exists())
        {
            logger.info((new StringBuilder("report file not exist, report path is :")).append(reportPath).toString());
            throw new CoreException("BBIP0004CPOS0506");
        } else
        {
            transfer.send(reportPath, printReportName, "182.53.15.187");
            context.setData("printReportName", printReportName);
            
            return;
        }
    }

    private String getPrintReportName(Context context)
    {
        String reportName = (String)context.getVariable("reportName");
        reportName = reportName.replaceAll("_", "");
        int index = reportName.indexOf(".");
        if(index != -1)
            reportName = reportName.substring(0, index);
        String br = (String)context.getData("br");
        String tlr = (String)context.getData("tlr");
        return (new StringBuilder(String.valueOf(reportName))).append("_p_").append(br).append("_").append(tlr).toString();
    }

    private static final String REPORT_NAME = "reportName";
    private static final Log logger = LogFactory.getLog(GdPrintReportAction.class);
    private BBIPPublicService bbipPublicService;

}


/*
	DECOMPILATION REPORT

	Decompiled from: D:\.m2\repository\com\bocom\bbip\bbip-comp-cpos\1.1.0-SNAPSHOT\bbip-comp-cpos-1.1.0-SNAPSHOT.jar
	Total time: 109 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/

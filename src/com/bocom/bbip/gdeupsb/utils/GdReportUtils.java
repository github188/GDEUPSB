/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.bocom.bbip.gdeupsb.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.jump.bp.SystemConfig;

public class GdReportUtils
{
private static Logger logger = LoggerFactory.getLogger(GdReportUtils.class);
    public GdReportUtils()
    {
    }

    public static String reportPath(BBIPPublicService bbipPublicService, SystemConfig systemConfig)
    {
        String reportPath = (new StringBuilder(String.valueOf(bbipPublicService.getParam("BBIP", "BBIP_DATA_PATH")))).append("/").append(systemConfig.getSystemCode()).append("/").append(bbipPublicService.getAcDateAsString()).append("/report").append("/").toString();
        logger.info("================reportPath:[" + reportPath + "]");
        return reportPath;
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: D:\.m2\repository\com\bocom\bbip\bbip-comp-cpos\1.1.0-SNAPSHOT\bbip-comp-cpos-1.1.0-SNAPSHOT.jar
	Total time: 15 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/
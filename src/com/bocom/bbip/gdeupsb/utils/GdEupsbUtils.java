/*@(#)GdEupsaUtils.java   2015-2-4 
 * Copy Right 2015 Bank of Communications Co.Ltd.
 * All Copyright Reserved
 */

package com.bocom.bbip.gdeupsb.utils;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * TODO Document GdEupsaUtils
 * <p>
 * @version 1.0.0,2015-2-4
 * @author tadnun
 * @since 1.0.0
 */
public class GdEupsbUtils {
  
	 private static final Log log = LogFactory.getLog(GdEupsbUtils.class);
	 
	 public static void renderReportFile(Context context, VelocityTemplatedReportRender velocityTemplatedReportRender, MftpTransfer mftpTransfer,
		        String reportName, String reportFileName, String localDir, String bbosDir) throws CoreException, FileNotFoundException {
		        String result = velocityTemplatedReportRender.renderAsString(reportName, context);
		        BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(localDir + reportFileName));
		        try {
		            outStream.write(result.getBytes());
		            outStream.close();
		        } catch (IOException e) {
		            log.error("renderReportFile error happen.", e);
		        }
		        //mftp传送远程IP未定,待定
		       // mftpTransfer.send(localDir+reportFileName, bbosDir+reportFileName, "远程IP");
		    }
}

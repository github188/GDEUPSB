package com.bocom.bbip.gdeupsb.action.elea;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupsreport.ReportHelper;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.MFTPConfigInfo;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 供电局财务对帐文件生成交易
 * 
 * @author qc.w
 * 
 */
public class EleChkFleAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("EleGzComPayPrtAction start!..");
		
		
		
		
		
	}

}

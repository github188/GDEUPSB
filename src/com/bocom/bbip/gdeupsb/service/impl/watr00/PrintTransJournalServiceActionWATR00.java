package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupsreport.ReportHelper;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.MFTPConfigInfo;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.entity.GdeupsWatBatInfTmp;
import com.bocom.bbip.gdeupsb.repository.GdeupsWatBatInfTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.NumberUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 汕头水费历史交易查询打印
 * @author hefengwen
 *
 */
public class PrintTransJournalServiceActionWATR00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(PrintTransJournalServiceActionWATR00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("QueryAndPrintTransJournalServiceActionWATR00 execute start ... ...");
		String txnDat = context.getData("txnDat").toString();
		String br = context.getData("br");
        logger.info("txnDat["+txnDat+"]br["+br+"]");

//        GdeupsWatBatInfTmp tmp = new GdeupsWatBatInfTmp();
        EupsBatchInfoDetail tmp = new EupsBatchInfoDetail();
        tmp.setTxnDte(DateUtils.parse(txnDat));
        
        List<GdeupsWatBatInfTmp> ret=get(GdeupsWatBatInfTmpRepository.class).find(tmp);
        List<Map<String,Object>> retMap=(List<Map<String, Object>>) BeanUtils.toMaps(ret);
        double totAmt=0.0;
        for(Map map:retMap){
        	
        	map.put("thdCusNo", map.get("agtSrvCusId"));
//        	map.put("txnAmt", NumberUtils.centToYuanAsString(map.get("je").toString()));
//        	map.put("bakFld1", "S");
        	totAmt+=Double.parseDouble(map.get("txnAmt").toString());
        	
        }
        context.setData("sumCnt", ret.size());
        context.setData("sumAmt", totAmt);
        EupsThdFtpConfigRepository eupsThdFtpConfigRepository = get(EupsThdFtpConfigRepository.class);
		ReportHelper reportHelper = get(ReportHelper.class);
		//MFTPConfigInfo mftpConfigInfo = reportHelper.getMFTPConfigInfo(eupsThdFtpConfigRepository);
		//logger.info((new StringBuilder("mftpConfigInfo:>>>>").append(BeanUtils.toMap(mftpConfigInfo))).toString());
		
		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,String> map = new HashMap<String,String>();
		map.put("sample", "config/report/watr00/watr00_printTransJournal.vm");
		render.setReportNameTemplateLocationMapping(map);
		context.setData("eles", retMap);
		String result = render.renderAsString("sample", context);
		logger.info(result);
		String date = DateUtils.format(new Date(), DateUtils.STYLE_HHmmss);
		StringBuffer fileName = new StringBuffer((new StringBuilder("WATR00"+br+txnDat).append(date).toString()));
	//	reportHelper.createFileAndSendMFTP(context, result, fileName, mftpConfigInfo);
       context.setData("filName", fileName);
		logger.info("QueryAndPrintTransJournalServiceActionWATR00 execute end ... ...");
	}
	


}

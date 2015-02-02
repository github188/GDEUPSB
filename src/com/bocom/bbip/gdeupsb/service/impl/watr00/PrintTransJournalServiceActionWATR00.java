package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupsreport.ReportHelper;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.entity.EupsTransJournalTemp;
import com.bocom.bbip.eups.entity.MFTPConfigInfo;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.vo.TransJournalRequest;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
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
        EupsTransJournal eupsTransJournal = new EupsTransJournal();
        eupsTransJournal.setTxnDte(DateUtils.parse(txnDat, DateUtils.STYLE_SIMPLE_DATE));
        eupsTransJournal.setEupsBusTyp(context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
        eupsTransJournal.setBr(br);
        eupsTransJournal.setMfmTxnSts("S");
        List<EupsTransJournal> eupsTransJournals = get(EupsTransJournalRepository.class).find(eupsTransJournal);
        
        EupsThdFtpConfigRepository eupsThdFtpConfigRepository = get(EupsThdFtpConfigRepository.class);
		ReportHelper reportHelper = get(ReportHelper.class);
		MFTPConfigInfo mftpConfigInfo = reportHelper.getMFTPConfigInfo(eupsThdFtpConfigRepository);
		logger.info((new StringBuilder("mftpConfigInfo:>>>>").append(BeanUtils.toMap(mftpConfigInfo))).toString());
		
		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,String> map = new HashMap<String,String>();
		map.put("sample", "config/report/watr00_printTransJournal.vm");
		render.setReportNameTemplateLocationMapping(map);
		context.setData("eles", eupsTransJournals);
		String result = render.renderAsString("sample", context);
		logger.info(result);
		String date = DateUtils.format(new Date(), DateUtils.STYLE_HHmmss);
		StringBuffer fileName = new StringBuffer((new StringBuilder("WATR00"+br+txnDat).append(date).toString()));
		reportHelper.createFileAndSendMFTP(context, result, fileName, mftpConfigInfo);
       context.setData("filName", fileName);
		logger.info("QueryAndPrintTransJournalServiceActionWATR00 execute end ... ...");
	}
	
	
	
	
//	public void execute(Context context) throws CoreException,	CoreRuntimeException {
//		logger.info("QueryAndPrintTransJournalServiceActionWATR00 execute start ... ...");
//		TransJournalRequest  transJournalRequest = new TransJournalRequest();
//		BeanUtils.copyProperties(context.getDataMap(),transJournalRequest);
//		if(StringUtils.isBlank(transJournalRequest.getSqn()))
//            transJournalRequest.setSqn(null);
//        if(StringUtils.isBlank(transJournalRequest.getCusAc()))
//            transJournalRequest.setCusAc(null);
//        if(StringUtils.isBlank(transJournalRequest.getMfmVchNo()))
//            transJournalRequest.setMfmVchNo(null);
//        if(StringUtils.isBlank(transJournalRequest.getReqJnlNo()))
//            transJournalRequest.setReqJnlNo(null);
//        if(StringUtils.isBlank(transJournalRequest.getThdCusNo()))
//            transJournalRequest.setThdCusNo(null);
//        if(StringUtils.isBlank(transJournalRequest.getThdRgnNo()))
//            transJournalRequest.setThdRgnNo(null);
//        logger.info("beginDate["+transJournalRequest.getBeginDate()+"]endDate["+transJournalRequest.getEndDate()+"]");
//        transJournalRequest.setBeginDate(DateUtils.parse(context.getData("beginDate").toString(), "yyyy-MM-dd"));
//        transJournalRequest.setEndDate(DateUtils.parse(context.getData("endDate").toString(), "yyyy-MM-dd"));
//        if(transJournalRequest.getBeginDate() == null || transJournalRequest.getEndDate() == null)
//        {
//            logger.error("queryLocalJournalAction beginDate or endDate is empty!");
//            throw new CoreException(ErrorCodes.EUPS_QUERY_DATE_ISEMPTY);
//        }
//        Pageable pageable = BeanUtils.toObject(context.getDataMap(),PageRequest.class);
//        Page<EupsTransJournal> transJoulInfoPage = get(EupsTransJournalRepository.class).findLocalJournal(pageable, transJournalRequest);
//        if(transJoulInfoPage.getTotalElements()==0L){
//        	logger.info("eups query null!");
//        	throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
//        }
//        context.setData("totalElements", Long.valueOf(transJoulInfoPage.getTotalElements()));
//        context.setData("totalPages", Integer.valueOf(transJoulInfoPage.getTotalPages()));
//        List<EupsTransJournal> etllist = transJoulInfoPage.getElements();
//        EupsTransJournalTemp eupsTransJournal;
//        List<EupsTransJournalTemp> resultlist = new ArrayList<EupsTransJournalTemp>();
//        for(Iterator<EupsTransJournal> iterator = etllist.iterator();iterator.hasNext();resultlist.add(eupsTransJournal)){
//        	EupsTransJournal etj = (EupsTransJournal)iterator.next();
//            eupsTransJournal = new EupsTransJournalTemp();
//            context.setDataMap(BeanUtils.toMap(etj));
//            eupsTransJournal = (EupsTransJournalTemp)BeanUtils.toObject(context.getDataMap(), EupsTransJournalTemp.class);
//            eupsTransJournal.setTxnDte(DateUtils.format(etj.getTxnDte(), "yyyy-MM-dd"));
//            eupsTransJournal.setTxnTme(DateUtils.format(etj.getTxnTme(), "yyyy-MM-dd HH:mm:ss.SSS"));
//        }
//        context.setData("lclJnlList", BeanUtils.toMaps(resultlist));
//		logger.info("QueryAndPrintTransJournalServiceActionWATR00 execute end ... ...");
//	}

}

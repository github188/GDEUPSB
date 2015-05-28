package com.bocom.bbip.gdeupsb.action.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class QryReportInfoAction extends BaseAction {

	private Logger logger = LoggerFactory.getLogger(QryReportInfoAction.class);

	public void execute(Context context) throws CoreException {
		logger.info("-------------------->>>>>>>>>>>> QryReportInfoAction start! ----------------------->>>>>>>>>>");
		String prtDte = context.getData("prtDte");
		Date printDate = DateUtils.parse(prtDte);
		// 如果单位编号为空，需根据业务类型确定单位编号
		EupsThdBaseInfo baseInfo = new EupsThdBaseInfo();
		baseInfo.setEupsBusTyp((String) context
				.getData(ParamKeys.EUPS_BUSS_TYPE));
		if (StringUtils.isNotBlank((String) context
				.getData(ParamKeys.COMPANY_NO))) {
			baseInfo.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
		}
		List<EupsThdBaseInfo> infoList = get(EupsThdBaseInfoRepository.class)
				.find(baseInfo);
		String comNme = infoList.get(0).getComNme();
		context.setData(ParamKeys.COMPANY_NAME, comNme);
//		context.setData(ParamKeys.COMPANY_NO, comNo);

		logger.info("=============context:" + context);
		GdEupsTransJournal eupsJnl = new GdEupsTransJournal();
		eupsJnl.setEupsBusTyp((String) context
				.getData(ParamKeys.EUPS_BUSS_TYPE));
		if (StringUtils.isNotBlank((String) context
				.getData(ParamKeys.COMPANY_NO))) {
			eupsJnl.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
		}
		eupsJnl.setTxnDte(DateUtils.parse(prtDte));

		String prtTyp = context.getData("prtTyp");
		
		if("0".equals(prtTyp) || "1".equals(prtTyp) || "2".equals(prtTyp) || "3".equals(prtTyp) || "4".equals(prtTyp) ){
		// 统计单笔交易信息
			List<Map<String, Object>> prtList = new ArrayList<Map<String,Object>>(); 
			if("ELEC00".equals(((String) context
					.getData(ParamKeys.EUPS_BUSS_TYPE)))){
				prtList = get(
						GdEupsTransJournalRepository.class).findEfekRptJnlInfo(eupsJnl);
			}
			else if("PGAS00".equals(((String) context
					.getData(ParamKeys.EUPS_BUSS_TYPE)))){
				prtList = get(
						GdEupsTransJournalRepository.class).findAllTxnList(eupsJnl);
			}
			
		if (null == prtList || CollectionUtils.isEmpty(prtList)) {
			logger.info("There are no records for select check trans journal ");
			throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		}

		// 返回信息
		context.setData("totCnt", prtList.get(0).get("TOTCNT") + "");
		context.setData("totAmt", prtList.get(0).get("TOTAMT") + "");
		context.setData("succCnt", prtList.get(0).get("SUCCCNT") + "");
		context.setData("totSuccAmt", prtList.get(0).get("TOTSUCCAMT") + "");
		context.setData("failCnt", prtList.get(0).get("FAILCNT") + "");
		context.setData("totFailAmt", prtList.get(0).get("TOTFAILAMT") + "");
		context.setData("doubtCnt", prtList.get(0).get("DOUBTCNT") + "");
		context.setData("totDoubtAmt", prtList.get(0).get("TOTDOUBTAMT") + "");
		context.setData("otherCnt", prtList.get(0).get("OTHERCNT") + "");
		context.setData("totOtherAmt", prtList.get(0).get("TOTOTHERAMT") + "");
		logger.info("======== jnl rpt info =====context:" + context);
		}
		else{
		//统计批量交易信息 批量交易不是一天只有一个批次的！！！
		if("ELEC02".equals((String) context
				.getData(ParamKeys.EUPS_BUSS_TYPE))){
			Map<String, Object> baseMap = new HashMap<String, Object>();
			baseMap.put(ParamKeys.EUPS_BUSS_TYPE, (String) context
					.getData(ParamKeys.EUPS_BUSS_TYPE));
			baseMap.put(ParamKeys.TXN_DTE, printDate);
			List<Map<String, Object>> elec02BatInfoList = get(GDEupsBatchConsoleInfoRepository.class).findElec02BatchRptInformation(baseMap);
			if (null == elec02BatInfoList || CollectionUtils.isEmpty(elec02BatInfoList)) {
				logger.info("There are no records for select elec02BatInfoList ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}
			
			context.setData("batTotCnt", elec02BatInfoList.get(0).get("TOTCNT") + "");
			context.setData("batTotAmt", elec02BatInfoList.get(0).get("TOTAMT") + "");
			context.setData("batSucCnt", elec02BatInfoList.get(0).get("SUCTOTCNT") + "");
			context.setData("batSucAmt", elec02BatInfoList.get(0).get("SUCTOTAMT") + "");
			context.setData("batFalCnt", elec02BatInfoList.get(0).get("FALTOTCNT") + "");
			context.setData("batFalAmt", elec02BatInfoList.get(0).get("FALTOTAMT") + "");
			
		}else{
			GDEupsBatchConsoleInfo eupsBatchConsoleInfo = new GDEupsBatchConsoleInfo();
			eupsBatchConsoleInfo.setExeDte(printDate);
			eupsBatchConsoleInfo.setEupsBusTyp((String) context
				.getData(ParamKeys.EUPS_BUSS_TYPE));
			if(StringUtils.isNotEmpty((String)context.getData(ParamKeys.COMPANY_NO))){
				eupsBatchConsoleInfo.setComNo((String) context
						.getData(ParamKeys.COMPANY_NO));
			}
//			eupsBatchConsoleInfo.setComNo(comNo);
			List<GDEupsBatchConsoleInfo> batInfoList = get(
					GDEupsBatchConsoleInfoRepository.class)
					.find(eupsBatchConsoleInfo);
			if (null == batInfoList || CollectionUtils.isEmpty(batInfoList)) {
				logger.info("There are no records for select GDEupsBatchConsoleInfo batInfo ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}

			int batTotCnt = 0;
			int batSucCnt = 0;
			int batFalCnt = 0;
			BigDecimal batTotAmt = new BigDecimal(0.00);
			BigDecimal batSucAmt = new BigDecimal(0.00);
			BigDecimal batFalAmt = new BigDecimal(0.00);

			for (GDEupsBatchConsoleInfo perInfo : batInfoList) {
				batTotCnt = batTotCnt + perInfo.getTotCnt();
				batSucCnt = batSucCnt + perInfo.getSucTotCnt();
				batFalCnt = batFalCnt + perInfo.getFalTotCnt();
				batTotAmt = batTotAmt.add(perInfo.getTotAmt());
				batSucAmt = batSucAmt.add(perInfo.getSucTotAmt());
				batFalAmt = batFalAmt.add(perInfo.getFalTotAmt());
			}

			context.setData("batTotCnt", batTotCnt + "");
			context.setData("batTotAmt", batTotAmt + "");
			context.setData("batSucCnt", batSucCnt + "");
			context.setData("batSucAmt", batSucAmt + "");
			context.setData("batFalCnt", batFalCnt + "");
			context.setData("batFalAmt", batFalAmt + "");
			logger.info("======== bat rpt info =====context:" + context);
		}
		
		}

	}
}

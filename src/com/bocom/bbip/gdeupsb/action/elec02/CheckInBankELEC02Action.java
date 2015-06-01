package com.bocom.bbip.gdeupsb.action.elec02;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import weblogic.utils.io.FilenameEncoder;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.support.CallThdService;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.channel.http.support.JsonUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * @author wuyh
 * 
 */
public class CheckInBankELEC02Action extends BaseAction {
	private static final Log logger = LogFactory.getLog(CheckInBankELEC02Action.class);

	public void process(Context context) throws CoreException {
		logger.info("对账开始");
		List<Map<String, String>> ret = getData(context);
		/** 得到总金额 */
		BigDecimal decimal = getTotAmt(context);
		/** 生成发送对账文件到第三方 */
		sendCheckFile(context, decimal, ret);
		/** 通知第三方 */
		context.setData("AppTradeCode", "50");
		context.setData("WorkDate", DateUtils.format(new Date(), "yyyyMMdd"));
		context.setData("SendTime", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
		context.setData("chn", "00");
		Map<String, Object> map = get(CallThdService.class).callTHD(context);
	}

	private void sendCheckFile(Context context, BigDecimal decimal,
			List<Map<String, String>> ret) throws CoreException {
		String fileDate = DateUtils.format(new Date(), "yyyyMMdd");
		Map<String, String> header = new HashMap<String, String>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		EupsThdFtpConfig config = get(EupsThdFtpConfigRepository.class).findOne((String) context.getData(ParamKeys.FTP_ID));
		Assert.isFalse(null == config, ErrorCodes.EUPS_THD_FTP_CONFIG_NOTEXIST);
		header.put("totAmt", String.valueOf(decimal));
		header.put("totNum", String.valueOf(ret.size()));
		header.put("CCD", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		header.put("WDO", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		retMap.put("header", header);
		retMap.put("detail", ret);
		String FileName = "301190003dz" + fileDate + ".txt"; // 301190003dz20080101.txt
		context.setData("FileName", FileName);
		((OperateFileAction) get("opeFile")).
				createCheckFile(config, "ELEC02Check", FileName, retMap);
		((OperateFTPAction) get("opeFTP")).putCheckFile(config);
	}

	private List<Map<String, String>> getData(Context context) throws CoreException {
		final String comNo = (String) context.getData(ParamKeys.COMPANY_NO);
		final String txndte = (String) context.getData("WDO");
		List<String> condList = new ArrayList<String>();
		condList.add(" COM_NO='" + comNo + "' ");
		condList.add(" TXN_DTE='" + txndte + "' ");
		condList.add(" TXN_STS='S' ");
		condList.add(" SVR_NME in ('eups.payFeeOnline','eups.payUnilateralToBank') ");

		Map<String, Object> dynSqlMap = new HashMap<String, Object>();
		dynSqlMap.put("select", "SELECT");
		dynSqlMap.put("from", "FROM");
		dynSqlMap.put("table", "EUPS.EUPS_TRANS_JOURNAL");

		Map<String, Object> subMap = new HashMap<String, Object>();
		subMap.put("conditions", condList);

		dynSqlMap.put("subMap", subMap);
		List<Object> list = get(EupsTransJournalRepository.class).getCheckDynamicSql(dynSqlMap);
		Assert.isNotEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
		List<Map<String, String>> jnlList = new ArrayList<Map<String, String>>();
		for (Object obj : list) {
			EupsTransJournal map = (EupsTransJournal) obj;
			Date date = map.getTxnDte();
			String txnDte = DateUtils.format(date, "yyyyMMdd");
			Map mm = BeanUtils.toFlatMap(map);
			/***/
			List<Map> tempmap = JsonUtils.objectFromJson(map.getRsvFld3(), List.class);
			for (Map tmp : tempmap) {
				mm.putAll(tmp);
				mm.put("txnDte", txndte);
				jnlList.add(mm);
			}

		}

		return jnlList;
	}

	private BigDecimal getTotAmt(Context context) throws CoreException {
		final String comNo = (String) context.getData(ParamKeys.COMPANY_NO);
		final String txndte = (String) context.getData("WDO");
		EupsTransJournal temp = new EupsTransJournal();
		temp.setTxnSts("S");
		temp.setTxnDte(DateUtils.parse(txndte));
		temp.setComNo(comNo);
		temp.setSvrNme("eups.payFeeOnline");
		/** 单笔缴费 */
		List<EupsTransJournal> list = get(EupsTransJournalRepository.class).findBySvrNme(temp);
		temp.setSvrNme("eups.payUnilateralToBank");
		/** 单笔扣费 */
		List<EupsTransJournal> lst = get(EupsTransJournalRepository.class).findBySvrNme(temp);

		BigDecimal decinal = list.get(0).getTotalAmt();
		BigDecimal decinl = lst.get(0).getTotalAmt();

		// BigDecimal ret=new
		// BigDecimal(decinal.doubleValue()+decinl.doubleValue());
		BigDecimal ret = decinal.add(decinl); // modify by qc 0601

		return ret;
	}
}

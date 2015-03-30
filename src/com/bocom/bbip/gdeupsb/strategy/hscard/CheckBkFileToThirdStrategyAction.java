package com.bocom.bbip.gdeupsb.strategy.hscard;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 
 * 
 * @author lhx
 * 
 */
public class CheckBkFileToThirdStrategyAction implements Executable {

	@Autowired
	GdEupsTransJournalRepository gdEupsTransJournalRepository;

	@Autowired
	OperateFTPAction operateFTPAction;

	@Autowired
	OperateFileAction operateFileAction;
	public final static Log logger = LogFactory
			.getLog(CheckBkFileToThirdStrategyAction.class);

	@SuppressWarnings("unchecked")
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("CheckBkFileToThirdStrategyAction start.......");

		//查询交易记录
		GdEupsTransJournal eups = new GdEupsTransJournal();
		eups.setThdTxnDte(DateUtils.parse((String) context.getData(GDParamKeys.TTXN_DT), DateUtils.STYLE_yyyyMMdd));
		eups.setTxnSts(GDConstants.GAS_TXN_STS_S);
		eups.setMfmTxnSts(GDConstants.GAS_TXN_STS_S);
		eups.setEupsBusTyp((String) context.getData(ParamKeys.EUPS_BUSS_TYPE));
		List<Map<String, Object>> list = gdEupsTransJournalRepository
				.findSumTxnAmt(eups);
		Map<String, Object> totMap = list.get(0);

		context.setData("totNum", totMap.get("TOTNUM"));
		context.setData("totAmt", totMap.get("TOTAMT"));

		// 拼装对账文件map
		Map<String, Object> map = encodeFileMap(context);

		// 获取FTP信息,发送文件到指定路径
		EupsThdFtpConfig eupsThdFtpConfig = context
				.getData(ParamKeys.CONSOLE_THD_FTP_CONFIG_LIST);

		List<Map<String, Object>> detailList = (List<Map<String, Object>>) map
				.get(ParamKeys.EUPS_FILE_DETAIL);
		for (int i = 0; i < detailList.size(); i++) {
			detailList.get(i).put(
					ParamKeys.THD_TXN_DATE,
					DateUtils.format(
							(Date) detailList.get(i)
									.get(ParamKeys.THD_TXN_DATE),
							DateUtils.STYLE_yyyyMMdd));
			detailList.get(i).put(
					ParamKeys.TXN_TIME,
					DateUtils.formatAsHHmmss((Date) detailList.get(i).get(
							ParamKeys.TXN_TIME)));
		}
		map.put(ParamKeys.EUPS_FILE_DETAIL, detailList);

		// 设置文件名
		String date = (String)context.getData(GDParamKeys.TTXN_DT);
		String fileName = (String) context.getData(ParamKeys.BAK_FLD1) + date
				+ ".txt";
		context.setData("checkFilNam", fileName);

		try {
			// 生成对账文件到指定路径
			operateFileAction.createCheckFile(eupsThdFtpConfig, "HSCardFile",
					fileName, map);
			logger.info("对账文件已生成");
		} catch (Exception e) {
			logger.error("File create error : " + e.getMessage());
			throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL, e);
		}

		// 向指定FTP路径放文件
		eupsThdFtpConfig.setLocFleNme(fileName);
		operateFTPAction.putCheckFile(eupsThdFtpConfig);
		logger.info("对账文件FTP放置成功！");
	}

	/**
	 * 拼装对账文件Map
	 * 
	 * @param context
	 * @return
	 */
	private Map<String, Object> encodeFileMap(Context context)
			throws CoreException {
		Map<String, Object> map = new HashMap<String, Object>();

		GdEupsTransJournal gdeups = new GdEupsTransJournal();

		gdeups.setThdTxnDte(DateUtils.parse((String) context.getData(GDParamKeys.TTXN_DT), DateUtils.STYLE_yyyyMMdd));
		gdeups.setTxnSts("S");
		gdeups.setMfmTxnSts("S");
		gdeups.setEupsBusTyp((String) context.getData(ParamKeys.EUPS_BUSS_TYPE));
		List<GdEupsTransJournal> chkGDEupsTransJournal = gdEupsTransJournalRepository
				.findCheck(gdeups);

		if (CollectionUtils.isEmpty(chkGDEupsTransJournal)) {
			logger.info("There are no records for select check trans journal ");
			throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		}
		map.put(ParamKeys.EUPS_FILE_DETAIL,
				BeanUtils.toMaps(chkGDEupsTransJournal));
		return map;
	}
}

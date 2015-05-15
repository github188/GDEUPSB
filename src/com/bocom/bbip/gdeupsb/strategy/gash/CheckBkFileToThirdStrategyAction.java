package com.bocom.bbip.gdeupsb.strategy.gash;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.gdeupsb.action.common.OperateFTPActionExt;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.repository.GdGashBatchTmpRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * GASH 对账文件上传 460707 生成对账文件并上传 银行发起，前端传入交易日期 文件名：ss+银行标志+年月日（实际交易日期）+.txt
 * 示例：ssCNJT20130930.txt 生成文件到 ftp://bank/银行标志/reckoning/
 * 补充说明：包含成功及失败的交易流水，但银行需剔除已冲帐的交易。 此txt文件（前一天的代扣交易结果）每天早上6点前
 * 
 * @author WMQ
 * 
 */
public class CheckBkFileToThirdStrategyAction extends BaseAction 
//implements	CheckBkFileToThirdService 
{

	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	OperateFileAction operateFileAction;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Autowired
	GdEupsTransJournalRepository gdEupsTransJournalRepository;
	@Autowired
	GdGashBatchTmpRepository gdGashBatchTmpRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

	private static final Log logger = LogFactory
			.getLog(CheckBkFileToThirdStrategyAction.class);

	/**
	 * 拼装对账文件Map
	 */
	private Map<String, Object> encodeFileMap(Context context)
			throws CoreException {
		Date checkDte = (Date) (context.getData("fileDte"));
		Map<String, Object> baseMap = new HashMap<String, Object>();
		baseMap.put("txnDte", checkDte);
//		List checkFileList = new ArrayList();
		//查询单笔的记录
		List<Map<String, Object>> chkGasJnlList = gdEupsTransJournalRepository.findGasCheckRec(baseMap);
		if (CollectionUtils.isEmpty(chkGasJnlList)) {
            logger.info("There are no records for select check trans journal ");
            throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
        }
		//		checkFileList.add(chkGasJnlList);
		
//		List<Map<String, Object>> chkGasBatList = gdGashBatchTmpRepository.findGasCheckRec(baseMap);
//		checkFileList.add(chkGasBatList);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ParamKeys.EUPS_FILE_DETAIL, chkGasJnlList);
		return map;
	}

	public Date getYesterDay() {
		Calendar canl = Calendar.getInstance();
		Date date = new Date();
		canl.setTime(date);
		canl.add(Calendar.DATE, -1);
		date = canl.getTime();
		return date;
	}

	@Override
	public void execute(Context context) throws CoreException {
		/*
		 * 根据有无交易日期判断交易是否自动发起 (自动发起则设置实际交易日期) done 根据交易日期设定对账文件名 done
		 * 根据业务类型查FTP配置表获取FTP配置信息 done 修改ftp配置信息里的文件名字段(本地/远程 文件路径配置在FTP配置表)
		 * done 拼装对账文件map done 配置对账文件格式 done 按格式生成对账文件 done 上传对账文件到燃气ftp done
		 */

		logger.info("====================Enter In CheckFileToThdHzActionPGAS00@checkBkFileToThird....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

		// String thdTxnCde = "460707";
		// context.setData(ParamKeys.THD_TXN_CDE, thdTxnCde);
		// String sqn = context.getData(ParamKeys.SEQUENCE);
		
		
		String bk = "01491999999";
		String br = "01491001999";
		context.setData(ParamKeys.BR, br);
		context.setData(ParamKeys.BK, bk);
		String tlr = bbipPublicService.getETeller(bk);
		context.setData(ParamKeys.TELLER, tlr);
		context.setData("extFields", br);
		
		// 交易日期
		String fileDte = null;

		if (StringUtils.isNotBlank((String) context.getData("fileDte"))) {
			fileDte = context.getData("fileDte");
			Date fileDate = DateUtils.parse(fileDte);
			fileDte = DateUtils.format(fileDate, DateUtils.STYLE_yyyyMMdd);
			context.setData("fileDte", fileDate);
		} else {
			Date fileDate = getYesterDay();
			fileDte = DateUtils.format(fileDate, DateUtils.STYLE_yyyyMMdd);
			context.setData("fileDte", fileDate);
		}

		// 拼接文件名 ssCNJT20141231.txt
		String filNam = "ssCNJT" + fileDte + ".txt";
		logger.info("========================fileName【" + filNam + "】");

		// 设置生成文件的名字
		EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository
				.findOne("gasCheckFilToThd"); // FTP_NO
		eupsThdFtpConfig.setLocFleNme(filNam);
		eupsThdFtpConfig.setRmtFleNme(filNam);

		// 拼装对账文件Map
		Map<String, Object> checkFileMap = encodeFileMap(context);

		// 生成对账文件到指定路径
		try {
			operateFileAction.createCheckFile(eupsThdFtpConfig,
					"creHzGasCheckFile", filNam, checkFileMap);
			logger.info("对账文件生成成功！");
		} catch (Exception e) {
			logger.error("File create error : " + e.getMessage());
			throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
		}

		// 向指定FTP路径放文件
		get(OperateFTPActionExt.class).putCheckFile(eupsThdFtpConfig);
		logger.info("对账文件FTP放置成功！");

		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
//		return null;
	}

}

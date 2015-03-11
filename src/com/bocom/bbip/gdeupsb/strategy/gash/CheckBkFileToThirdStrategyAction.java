package com.bocom.bbip.gdeupsb.strategy.gash;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.spi.service.check.CheckBkFileToThirdService;
import com.bocom.bbip.eups.spi.vo.CheckDomain;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * GASH 对账文件上传 460707 生成对账文件并上传 银行发起，前端传入交易日期 文件名：ss+银行标志+年月日（实际交易日期）+.txt
 * 示例：ssCNJT20130930.txt 生成文件到 ftp://bank/银行标志/reckoning/
 * 补充说明：包含成功及失败的交易流水，但银行需剔除已冲帐的交易。
 * 此txt文件（前一天的代扣交易结果）每天早上6点前
 * @author WMQ
 * 
 */
public class CheckBkFileToThirdStrategyAction implements CheckBkFileToThirdService{

	@Autowired
	OperateFileAction operateFileAction;

	@Autowired
	OperateFTPAction operateFTPAction;

	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;

	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

	private static final Log logger = LogFactory.getLog(CheckBkFileToThirdStrategyAction.class);


	/**
	 * 拼装对账文件Map
	 */
	private Map<String, Object> encodeFileMap(Context context)
			throws CoreException {
		Map<String, Object> map = new HashMap<String, Object>();
		//先从流水表中查询需要的records
		// ICS SQL:<!--单笔（成功或失败），不包含冲账的-->
		//select * from gastxnjnl491 where OptDat='%s' and TTxnCd='SMPCPAY'  and PkgFlg='0'	
		
		EupsTransJournal etj = new EupsTransJournal();
		etj.setComNo(context.getData(ParamKeys.COMPANY_NO).toString().trim());
		etj.setTxnDte((Date) (context.getData(ParamKeys.TXN_DTE)));
		etj.setThdTxnCde("SMPCPAY");//460710?

		List<EupsTransJournal> chkEtjList = eupsTransJournalRepository.find(etj);
		if (null == chkEtjList && CollectionUtils.isEmpty(chkEtjList)) {
			logger.info("There are no records for select check trans journal ");
			throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		}
		List<EupsTransJournal> etjLst = new ArrayList<EupsTransJournal>();
		for (EupsTransJournal etjnl : chkEtjList) {
			// 判断chkEtjList中的交易状态，取状态为“S”、“F”、的record
			if ("S".equals(etjnl.getTxnSts()) || "F".equals(etjnl.getTxnSts())) {
				etjnl.setBk("CNJT");
				etjLst.add(etjnl);
			}
		}

		map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(etjLst));
		return map;
	}

	public Date getYesterDay(){
		Calendar canl = Calendar.getInstance();
		Date date = new Date();
		canl.setTime(date);
		canl.add(Calendar.DATE,-1);
		date = canl.getTime();
		return date;
	}

	@Override
	public Map<String, Object> checkBkFileToThird(CheckDomain checkdomain,
			Context context) throws CoreException {
		/*
		 * 根据有无交易日期判断交易是否自动发起 (自动发起则设置实际交易日期) done   
		 * 根据交易日期设定对账文件名  done
		 * 根据业务类型查FTP配置表获取FTP配置信息 done
		 * 修改ftp配置信息里的文件名字段(本地/远程 文件路径配置在FTP配置表) done
		 * 拼装对账文件map done
		 * 配置对账文件格式 done
		 * 按格式生成对账文件 done
		 * 上传对账文件到燃气ftp done
		 */

		logger.info("====================Enter In CheckFileToThdHzActionPGAS00@checkBkFileToThird....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

//		String thdTxnCde = "460707";
//		context.setData(ParamKeys.THD_TXN_CDE, thdTxnCde);
//		String sqn = context.getData(ParamKeys.SEQUENCE);
		context.setData(ParamKeys.BK, "CNJT");
		// 交易日期
		String txnDte1 = DateUtils.format((Date) context.getData(ParamKeys.TXN_DTE), DateUtils.STYLE_yyyyMMdd);
		if (null == txnDte1) {
			Date txnDte = getYesterDay();
			context.setData(ParamKeys.TXN_DTE, txnDte);
			txnDte1 = DateUtils.format(txnDte, DateUtils.STYLE_yyyyMMdd);
		}
		
		//前端传入的日期是String形式的，转换为Date类型 
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		try {
//			Date txnDte = sdf.parse((String) context.getData(ParamKeys.TXN_DTE));
//			context.setData(ParamKeys.TXN_DTE, txnDte);
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//		}
//		txnDte1 = txnDte1.replace("-", "");
		// 银行编号
		String bk = context.getData(ParamKeys.BK);
		// //拼接文件名 ssCNJT20141231.txt
		String filNam = "ss" + bk + txnDte1 + ".txt";
		logger.info("========================fileName" + filNam);

		// 设置生成文件的名字
		// EupsThdFtpConfig eupsThdFtpConfig = new EupsThdFtpConfig();
		EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("gasCheckFilToThd");	//FTP_NO
		eupsThdFtpConfig.setLocFleNme(filNam);
		eupsThdFtpConfig.setRmtFleNme(filNam);

		// 拼装对账文件Map
		Map<String, Object> checkFileMap = encodeFileMap(context);

		// 生成对账文件到指定路径
		try {
			operateFileAction.createCheckFile(eupsThdFtpConfig,	"creHzGasCheckFile", filNam, checkFileMap);
			logger.info("对账文件生成成功！");
		} catch (Exception e) {
			logger.error("File create error : " + e.getMessage());
			throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
		}

		// 向指定FTP路径放文件
		operateFTPAction.putCheckFile(eupsThdFtpConfig);
		logger.info("对账文件FTP放置成功！");

		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		return null;
	}
	
}

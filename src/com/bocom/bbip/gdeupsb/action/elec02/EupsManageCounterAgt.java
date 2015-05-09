package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdeupsAgtElecTmp;
import com.bocom.bbip.gdeupsb.repository.GdeupsAgtElecTmpRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class EupsManageCounterAgt extends BaseAction {
	private static Logger logger = LoggerFactory
			.getLogger(EupsManageCounterAgt.class);
	private static final int ADD = 0;
	private static final int UPDATE = 3;
	private static final int QUERY = 5;
	private static final int DELETE = 9;
	private static final String MGR_DATE = DateUtils.format(new Date(),
			DateUtils.STYLE_SIMPLE_DATE);

	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	AccountService accountService;
	@Autowired
	ThirdPartyAdaptor callThdTradeManager;

	public void process(Context context) throws CoreException {
		logger.info("协议维护（柜面）");

		final int oprType = Integer.parseInt((String) context.getData("CHT"));
		logger.info("===============oprType: " + oprType);

		switch (oprType) {
		case ADD:
			addAgentDeal(context);
			break;
		case UPDATE:
			// 不允许更改卡号缴费号对应关系，只修改其他辅助信息
			checkOldBaseInfo(context);
			updateAgentDeal(context);
			break;
		case QUERY:
			queryAgentDeal(context);
			break;
		case DELETE:
			deleteAgentDeal(context);
			break;
		}

		logger.info("======== ending context :" + context);
	}

	private void checkOldBaseInfo(Context context) throws CoreException {
		
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		String feeNum = (String) context.getData("JFH");
		String actNo = (String) context.getData("ActNo");
		if (feeNum != null && !"".equals(feeNum.trim())) {
			agtElecTmp.setFeeNum(feeNum); //
		}
		if (actNo != null && !"".equals(actNo.trim())) {
			agtElecTmp.setActNo(actNo); //
		}
		List<GdeupsAgtElecTmp> tmpList = get(GdeupsAgtElecTmpRepository.class)
				.findBase(agtElecTmp);
		if (null == tmpList || CollectionUtils.isEmpty(tmpList)) {
			logger.info("There are no records for select check elec agt tmp ");
			throw new CoreException("协议不存在或缴费号与原签约账号不匹配");
		}
		String OAC = tmpList.get(0).getActNo();
		String OKH = tmpList.get(0).getNewBankNum();
		context.setData("OKH", OKH);
		logger.info("context after checkOldBaseInfo : " + context);
	}

	private void buildContextAndCallThd(Context context) throws CoreException {

		Date date = new Date();
		String YYYYMMDD = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);
		String YYYYMMDDHHMMSS = DateUtils.format(date,
				DateUtils.STYLE_yyyyMMddHHmmss);

		String br = context.getData(ParamKeys.BR);
		String sqnTmp = bbipPublicService.getBBIPSequence();
		sqnTmp = sqnTmp.substring(12);
		String msgId = br + " " + sqnTmp;
		// 1.拼装外发报文头
		context.setData("AppTradeCode", "30");
		context.setData("StartAddr", "301");// wangxianfen:301
		context.setData("DestAddr", "0500");// 供电局代码 wangxianfen:0500
		context.setData("MesgID", msgId);

		context.setData("WorkDate", YYYYMMDD);
		context.setData("SendTime", YYYYMMDDHHMMSS);
		context.setData("mesgPRI", "9");

		context.setData("recordNum", "12"); // 不校验，但是要有值
		context.setData("FileName", "");// 无文件，空字符串
		context.setData("zipFlag", "0");//

		// 2.拼装外发报文体
		// <fixString name="SBN" length="12" /> <!--发起业务的节点代码 -->
		// <fixString name="WDO" length="8 " /> <!--工作日期 -->
		// <fixString name="TLogNo" length="16" /> <!--供电局流水 -->
		// 其他字段前端输入
		context.setData("ECD", "0500");
		context.setData("EDD", "000");
		context.setData("SBN", "301");
		context.setData("WDO", YYYYMMDD);
		context.setData("TLogNo", ""); // 供电局流水
		context.setData("KKB", br);
		context.setData("ZPF", "0");
		context.setData("FPF", "0");
		context.setData("IdTyp", "0");
		// 根据银行卡获取开户信息 身份证号
		// context.setData("TIdNo", "123123");

		logger.info("=====外发电力========>>>>>>>>>>>>>>> context : " + context);
		// 外发thd
		Map<String, Object> callThdRsp = callThdTradeManager.trade(context);
		if (BPState.isBPStateOvertime(context)) {
			throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		}
		context.setDataMap(callThdRsp);

	}

	public void checkCusInfoByCusAc(Context context) throws CoreException {
		String cusAc = context.getData("ActNo").toString().trim();

		CusActInfResult cusactinfresult = new CusActInfResult();
		try {
			cusactinfresult = accountService.getAcInf(
					CommonRequest.build(context), cusAc);
		} catch (CoreException e) {
			logger.info("查询账号状态失败", e);
		}

		if (!cusactinfresult.isSuccess()) {
			throw new CoreException(cusactinfresult.getResponseMessage());
		}
		String cusAcSts = cusactinfresult.getCusAcSts();
		context.setData("cusAcSts", cusAcSts);
		logger.info("===========cusAcSts : " + cusAcSts);
		// TODO
		// if ("00".equals(cusAcSts)) {
		// throw new CoreException("该帐号处于不正常状态，不可进行交易");
		// }
		// if ("01".equals(cusAcSts)) {
		// throw new CoreException("该账号为预开户，不可进行交易");
		// }
		if ("02".equals(cusAcSts)) {
			throw new CoreException("该账号待激活或已销户，不可进行交易");
		}
		// if ("04".equals(cusAcSts)) {
		// throw new CoreException("该账号处于抹帐状态，不可进行交易");
		// }
		// if ("05".equals(cusAcSts)) {
		// throw new CoreException("该账号已销户，不可进行交易");
		// }
		// if ("06".equals(cusAcSts)) {
		// throw new CoreException("该账号已销卡，不可进行交易");
		// }
		String idNo = cusactinfresult.getIdNo();
		context.setData("TIdNo", idNo);
	}

	private void addAgentDeal(Context context) throws CoreException {
		// TODO checkCusInfoByCusAc(context);

		// ICS： 先签本地再签thd
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		agtElecTmp.setFeeNum((String) context.getData("JFH"));
		// agtElecTmp.setStatus("0");
		List<GdeupsAgtElecTmp> list = get(GdeupsAgtElecTmpRepository.class)
				.find(agtElecTmp);
		if (list.size() > 0) {
			if ("0".equals(list.get(0).getStatus())) {
				log.info("协议已经存在");
				throw new CoreException("协议已经存在");
			}
			if ("1".equals(list.get(0).getStatus())) {
				// 更新
				updateAgentDeal(context);
			}
		} else {
			agtElecTmp = toGdeupsAgtElecTmp(context);
			agtElecTmp.setBrNo((String) context.getData(ParamKeys.BK));
			agtElecTmp.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
			// agtElecTmp.setAgtNo(getAgtNo()); // 445202 + 7位序列码
			agtElecTmp.setBankNo("301");
			agtElecTmp.setComCode("0500");
			agtElecTmp.setFeeCode("000");
			agtElecTmp.setStatus("0");
			agtElecTmp.setRemark("签约日期:" + MGR_DATE);
			get(GdeupsAgtElecTmpRepository.class).insert(agtElecTmp);

			// 为返回的list 赋值
			List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("JFH", context.getData("JFH"));
			infoMap.put("UsrNam", context.getData("UsrNam"));
			infoMap.put("ActNo", context.getData("ActNo"));
			infoMap.put("ActNm", context.getData("ActNm"));
			infoMap.put("ACT", context.getData("ACT"));
			infoMap.put("GPF", context.getData("GPF"));
			infoMap.put("MOB", context.getData("MOB"));
			infoMap.put("TEL", context.getData("TEL"));
			infoMap.put("TXT", agtElecTmp.getRemark());
			infoList.add(infoMap);
			context.setData("infoList", infoList);
			logger.info(" context after set infoList : ", context);

			buildContextAndCallThd(context);
			log.info("新增协议成功");
		}
	}

	/*
	 * private String getAgtNo() throws CoreException { // 不可行，假若a b c ....
	 * 签约时间无限接近，将可能取得同一个编号！！！ String agtNo = null; List<Map<String, Object>>
	 * agtNoList = get( GdeupsAgtElecTmpRepository.class).findAgtNo(); String
	 * subAgtNo = String.valueOf(agtNoList.get(0).get("SUBAGTNO")); long agtNoL
	 * = Long.parseLong(subAgtNo) + 1; agtNo = String.valueOf(agtNoL); agtNo =
	 * GdExpCommonUtils.AddChar(agtNo, 7, '0', '1'); agtNo = "445202" + agtNo;
	 * logger.info("===========agtNo = " + agtNo); return agtNo; }
	 */

	private void updateAgentDeal(Context context) throws CoreException {

		// TODO checkCusInfoByCusAc(context);
//		checkOldBaseInfo(context);
		
		String feeNum = (String) context.getData("JFH");
		
		logger.info("feeNum in updateAgentDeal : " + feeNum);
		List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
		
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		agtElecTmp.setFeeNum(feeNum);
		
		System.out.println("asdasdasdasdasdsaasdasdasdasdasdasdasdsa");
		// 旧协议信息，返显
		logger.info("context before select old agt :" + context);
		List<GdeupsAgtElecTmp> oldAgtElecTmps = get(
				GdeupsAgtElecTmpRepository.class).findBase(agtElecTmp);
//		for (GdeupsAgtElecTmp perTmp : oldAgtElecTmps) {
//			Map<String, Object> infoMap = new HashMap<String, Object>();
//			infoMap.put("JFH", perTmp.getFeeNum());
//			infoMap.put("UsrNam", perTmp.getUserName());
//			infoMap.put("ActNo", perTmp.getActNo());
//			infoMap.put("ActNm", perTmp.getAcountName());
//			infoMap.put("ACT", perTmp.getActType());
//			infoMap.put("GPF", perTmp.getPerComFlag());
//			infoMap.put("MOB", perTmp.getPhoneNum());
//			infoMap.put("TEL", perTmp.getTelNum());
//			infoMap.put("TXT", agtElecTmp.getRemark());
//			infoList.add(infoMap);
//		}
//		context.setData("infoList", infoList);
		logger.info(" context after set infoList : ", context);
//		String oldCardNo = context.getData("OAC");
		String oldCardNo = oldAgtElecTmps.get(0).getActNo();
		String oldBankNum = context.getData("OKH");

		agtElecTmp = toGdeupsAgtElecTmp(context);
		agtElecTmp.setOldBankNum(oldBankNum);
		// agtElecTmp.setOldCardNo(oldCardNo);
		// get(GdeupsAgtElecTmpRepository.class).updateByAc(agtElecTmp);
		agtElecTmp.setRemark("签约日期:" + MGR_DATE);
		agtElecTmp.setStatus("0");
		get(GdeupsAgtElecTmpRepository.class).updateByFeeNum(agtElecTmp);

		List<GdeupsAgtElecTmp> tmpList = get(GdeupsAgtElecTmpRepository.class)
				.findBase(agtElecTmp);
		// 修改后的协议信息，tmpList有且只有一条修改后的协议，返显
		for (GdeupsAgtElecTmp perTmp : tmpList) {
			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("JFH", perTmp.getFeeNum());
			infoMap.put("UsrNam", perTmp.getUserName());
			infoMap.put("ActNo", perTmp.getActNo());
			infoMap.put("ActNm", perTmp.getAcountName());
			infoMap.put("ACT", perTmp.getActType());
			infoMap.put("GPF", perTmp.getPerComFlag());
			infoMap.put("MOB", perTmp.getPhoneNum());
			infoMap.put("TEL", perTmp.getTelNum());
			infoMap.put("TXT", agtElecTmp.getRemark());
			infoList.add(infoMap);
		}
		context.setData("infoList", infoList);
		logger.info(" context after set infoList : ", context);

		// 外发电力更改协议
		buildContextAndCallThd(context);
	}

	private void queryAgentDeal(Context context) throws CoreException {
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		String feeNum = (String) context.getData("JFH");
		String actNo = (String) context.getData("ActNo");
		if (feeNum != null && !"".equals(feeNum.trim())) {
			agtElecTmp.setFeeNum(feeNum); //
		}
		if (actNo != null && !"".equals(actNo.trim())) {
			agtElecTmp.setActNo(actNo); //
		}
		List<GdeupsAgtElecTmp> tmpList = get(GdeupsAgtElecTmpRepository.class)
				.findBase(agtElecTmp);
		if (tmpList.size() > 0) {
			agtElecTmp = tmpList.get(0);
			// 查询结果数据处理
			setResponseResultFromAgts(context, agtElecTmp);
		} else {
			log.info("没有查询到协议信息！");
			throw new CoreException("没有查询到协议信息！");
		}

		List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
		for (GdeupsAgtElecTmp perTmp : tmpList) {
			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("JFH", perTmp.getFeeNum());
			infoMap.put("UsrNam", perTmp.getUserName());
			infoMap.put("ActNo", perTmp.getActNo());
			infoMap.put("ActNm", perTmp.getAcountName());
			infoMap.put("ACT", perTmp.getActType());
			infoMap.put("GPF", perTmp.getPerComFlag());
			infoMap.put("MOB", perTmp.getPhoneNum());
			infoMap.put("TEL", perTmp.getTelNum());
			infoMap.put("TXT", perTmp.getRemark());
			infoList.add(infoMap);
		}
		context.setData("infoList", infoList);
		logger.info(" context after set infoList : ", context);
	}

	// 删除交易（必输缴费号）
	private void deleteAgentDeal(Context context) throws CoreException {
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();

		String feeNum = (String) context.getData("JFH");
		String actNo = (String) context.getData("ActNo");
		if (feeNum != null && !"".equals(feeNum.trim())) {
			agtElecTmp.setFeeNum(feeNum); //
		}

		if (actNo != null && !"".equals(actNo.trim())) {
			agtElecTmp.setActNo(actNo); //
		}

		List<GdeupsAgtElecTmp> list = get(GdeupsAgtElecTmpRepository.class)
				.findBase(agtElecTmp);
		if (list.size() > 0) {
			agtElecTmp = list.get(0);
			// 查询结果数据处理
			setResponseResultFromAgts(context, agtElecTmp);
		} else {
			log.info("没有查询到协议信息！");
			throw new CoreException("没有查询到协议信息！");
		}

		List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>();
		for (GdeupsAgtElecTmp perTmp : list) {
			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("JFH", perTmp.getFeeNum());
			infoMap.put("UsrNam", perTmp.getUserName());
			infoMap.put("ActNo", perTmp.getActNo());
			infoMap.put("ActNm", perTmp.getAcountName());
			infoMap.put("ACT", perTmp.getActType());
			infoMap.put("GPF", perTmp.getPerComFlag());
			infoMap.put("MOB", perTmp.getPhoneNum());
			infoMap.put("TEL", perTmp.getTelNum());
			infoMap.put("TXT", perTmp.getRemark());
			infoList.add(infoMap);
		}
		context.setData("infoList", infoList);

		GdeupsAgtElecTmp delElecTmp = toGdeupsAgtElecTmp(context);
		delElecTmp.setStatus("1");
		delElecTmp.setRemark("注销日期:" + MGR_DATE);
		get(GdeupsAgtElecTmpRepository.class).updateByFeeNum(delElecTmp);

		buildContextAndCallThd(context);

		// context.setData("ACN", list.get(0).getActNo());
		// context.setData("ActNo", list.get(0).getActNo());
		// context.setData("TXT", list.get(0).getUserName());

	}

	// 封装GdeupsAgtElecTmp对象
	private GdeupsAgtElecTmp toGdeupsAgtElecTmp(Context context) {

		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		agtElecTmp.setComCode((String) context.getData("ECD"));
		agtElecTmp.setFeeCode((String) context.getData("EDD"));
		agtElecTmp.setFeeNum((String) context.getData("JFH")); //
		agtElecTmp.setUserName((String) context.getData("UsrNam"));
		agtElecTmp.setActNo((String) context.getData("ActNo")); // 账号
		agtElecTmp.setAcountName((String) context.getData("ActNm")); // 账户名称
		agtElecTmp.setActType((String) context.getData("ACT")); // 账号类型
		agtElecTmp.setPerComFlag((String) context.getData("GPF")); // 个人/对公
		agtElecTmp.setPhoneNum((String) context.getData("MOB"));
		agtElecTmp.setTelNum((String) context.getData("TEL"));
		// agtElecTmp.setPrcessPassword((String) context.getData("Pin")); //
		// 不保存密码
		agtElecTmp.setNewBankNum((String) context.getData(ParamKeys.BR));
		agtElecTmp.setIdNo((String) context.getData("TIdNo"));
		return agtElecTmp;
	}

	// 为查询返回报文复制
	private void setResponseResultFromAgts(Context context,
			GdeupsAgtElecTmp agtElecTmp) {

		context.setData("JFH", agtElecTmp.getFeeNum()); // 协议编号
		context.setData("UsrNam", agtElecTmp.getUserName()); // 单位编号
		context.setData("ActNo", agtElecTmp.getActNo()); // 第三方客户名称
		context.setData("ActNm", agtElecTmp.getAcountName()); // 缴费号码
		context.setData("ACT", agtElecTmp.getActType()); // 账号
		context.setData("GPF", agtElecTmp.getPerComFlag()); // 名称
		context.setData("MOB", agtElecTmp.getPhoneNum()); // 证件类型
		context.setData("TEL", agtElecTmp.getTelNum()); // 证件号码
	}

}

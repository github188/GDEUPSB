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
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class EupsManageCounterAgt extends BaseAction {
	private static Logger logger = LoggerFactory
			.getLogger(EupsManageCounterAgt.class);
	private static final int ADD = 0;
	private static final int UPDATE = 3;
	private static final int QUERY = 5;
	private static final int DELETE = 9;
	private static final String MGR_DATE = DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE);
	
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
//			checkCusInfoByCusAc(context);
			buildContextAndCallThd(context);
			addAgentDeal(context);
			break;
		case UPDATE:
//			checkCusInfoByCusAc(context);
			checkOldBaseInfo(context);
			//不允许更改卡号缴费对应关系，只修改其他辅助信息
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
		String feeNum = context.getData("JFH");
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		agtElecTmp.setFeeNum(feeNum);
		List<GdeupsAgtElecTmp> tmpList = get(GdeupsAgtElecTmpRepository.class)
				.find(agtElecTmp);
		if (null == tmpList || CollectionUtils.isEmpty(tmpList)) {
			logger.info("There are no records for select check elec agt tmp ");
			throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
		}
		String OAC = tmpList.get(0).getActNo();
		String OKH = tmpList.get(0).getNewBankNum();
		context.setData("OAC", OAC);
		context.setData("OKH", OKH);
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
		context.setData("StartAddr", "301");//  wangxianfen:301
		context.setData("DestAddr", "0500");//  供电局代码 wangxianfen:0500
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
		context.setData("TLogNo", ""); //供电局流水
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

		context.setData("comCode", "0500");
		context.setData("feeCode", "0000");
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

		if ("E".equals(cusactinfresult.getResponseType())) {
			throw new CoreException(cusactinfresult.getResponseMessage());
		}
		String cusAcSts = cusactinfresult.getCusAcSts();
		context.setData("cusAcSts", cusAcSts);
		logger.info("===========cusAcSts : " + cusAcSts);
		//TODO
//		if ("00".equals(cusAcSts)) {
//			throw new CoreException("该帐号处于不正常状态，不可进行交易");
//		}
//		if ("01".equals(cusAcSts)) {
//			throw new CoreException("该账号为预开户，不可进行交易");
//		}
		if ("02".equals(cusAcSts)) {
			throw new CoreException("该账号待激活或已销户，不可进行交易");
		}
//		if ("04".equals(cusAcSts)) {
//			throw new CoreException("该账号处于抹帐状态，不可进行交易");
//		}
//		if ("05".equals(cusAcSts)) {
//			throw new CoreException("该账号已销户，不可进行交易");
//		}
//		if ("06".equals(cusAcSts)) {
//			throw new CoreException("该账号已销卡，不可进行交易");
//		}
		String idNo = cusactinfresult.getIdNo();
		context.setData("TIdNo", idNo);
	}

	private void addAgentDeal(Context context) throws CoreException {
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		agtElecTmp.setFeeNum((String) context.getData("JFH"));
		agtElecTmp.setStatus("0");
		List<GdeupsAgtElecTmp> list = get(GdeupsAgtElecTmpRepository.class)
				.find(agtElecTmp);
		if (list.size() > 0) {
			log.info("协议已经存在");
			throw new CoreException("协议已经存在");
		}
		
		agtElecTmp = toGdeupsAgtElecTmp(context);
		agtElecTmp.setBrNo((String) context.getData(ParamKeys.BK));
		agtElecTmp.setComNo("4450000002");
		//TODO 协议编号			
//		agtElecTmp.setAgtNo(agtNo);   445202 + 7位序列码
		agtElecTmp.setBankNo("301");
		agtElecTmp.setStatus("0");
		agtElecTmp.setRemark("签约日期:" + MGR_DATE);
		get(GdeupsAgtElecTmpRepository.class).insert(agtElecTmp);
		
		//为返回的list 赋值
		List<Map<String, Object>> infoList = new ArrayList<Map<String,Object>>();
		Map<String, Object> infoMap = new HashMap<String, Object>();
		infoMap.put("JFH", context.getData("JFH"));
		infoMap.put("UsrNam", context.getData("UsrNam"));
		infoMap.put("ActNo", context.getData("ActNo"));
		infoMap.put("ActNm", context.getData("ActNm"));
		infoMap.put("ACT", context.getData("ACT"));
		infoMap.put("GPF", context.getData("GPF"));
		infoMap.put("MOB", context.getData("MOB"));
		infoMap.put("TEL", context.getData("TEL"));
		//TODO		infoMap.put("TXT", agtElecTmp.getRemark());
		infoList.add(infoMap);
		context.setData("infoList", infoList);
		logger.info(" context after set infoList : ", context);
		log.info("新增协议成功");

	}

	private void updateAgentDeal(Context context) throws CoreException {
		List<Map<String, Object>> infoList = new ArrayList<Map<String,Object>>();
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		agtElecTmp.setFeeNum((String) context.getData("JHF"));
		agtElecTmp.setStatus("0");
		//旧协议信息，返显
		List<GdeupsAgtElecTmp> oldAgtElecTmps = get(GdeupsAgtElecTmpRepository.class).findBase(agtElecTmp);
		for(GdeupsAgtElecTmp perTmp : oldAgtElecTmps){
			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("JFH", perTmp.getFeeNum());
			infoMap.put("UsrNam", perTmp.getUserName());
			infoMap.put("ActNo", perTmp.getActNo());
			infoMap.put("ActNm", perTmp.getAcountName());
			infoMap.put("ACT", perTmp.getActType());
			infoMap.put("GPF", perTmp.getPerComFlag());
			infoMap.put("MOB", perTmp.getPhoneNum());
			infoMap.put("TEL", perTmp.getTelNum());
			//TODO			infoMap.put("TXT", agtElecTmp.getRemark());
			infoList.add(infoMap);
		}
		String oldCardNo = context.getData("OAC");
		String oldBankNum = context.getData("OKH");
		
		//外发电力更改协议
		buildContextAndCallThd(context);
		
		agtElecTmp = toGdeupsAgtElecTmp(context);
		agtElecTmp.setOldBankNum(oldBankNum);
		agtElecTmp.setOldCardNo(oldCardNo);
		// get(GdeupsAgtElecTmpRepository.class).updateByAc(agtElecTmp);
		agtElecTmp.setRemark("修改日期:" + MGR_DATE);
		get(GdeupsAgtElecTmpRepository.class).updateByFeeNum(agtElecTmp);
		
		List<GdeupsAgtElecTmp> tmpList = get(GdeupsAgtElecTmpRepository.class).find(agtElecTmp);
		//修改后的协议信息，tmpList有且只有一条修改后的协议，返显
		for(GdeupsAgtElecTmp perTmp : tmpList){
			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("JFH", perTmp.getFeeNum());
			infoMap.put("UsrNam", perTmp.getUserName());
			infoMap.put("ActNo", perTmp.getActNo());
			infoMap.put("ActNm", perTmp.getAcountName());
			infoMap.put("ACT", perTmp.getActType());
			infoMap.put("GPF", perTmp.getPerComFlag());
			infoMap.put("MOB", perTmp.getPhoneNum());
			infoMap.put("TEL", perTmp.getTelNum());
			//TODO			infoMap.put("TXT", agtElecTmp.getRemark());
			infoList.add(infoMap);
		}
		context.setData("infoList", infoList);
		logger.info(" context after set infoList : ", context);
		

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
		
//		agtElecTmp.setStatus("0");
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
		
		List<Map<String, Object>> infoList = new ArrayList<Map<String,Object>>();
		for(GdeupsAgtElecTmp perTmp : tmpList){
			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("JFH", perTmp.getFeeNum());
			infoMap.put("UsrNam", perTmp.getUserName());
			infoMap.put("ActNo", perTmp.getActNo());
			infoMap.put("ActNm", perTmp.getAcountName());
			infoMap.put("ACT", perTmp.getActType());
			infoMap.put("GPF", perTmp.getPerComFlag());
			infoMap.put("MOB", perTmp.getPhoneNum());
			infoMap.put("TEL", perTmp.getTelNum());
//TODO			infoMap.put("TXT", perTmp.getRemark());
			infoList.add(infoMap);
		}
		context.setData("infoList", infoList);
		logger.info(" context after set infoList : ", context);
		
//		context.setData("ACN", list.get(0).getActNo());
//		context.setData("ActNo", list.get(0).getActNo());
//		context.setData("TXT", list.get(0).getUserName());
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
		
		List<Map<String, Object>> infoList = new ArrayList<Map<String,Object>>();
		for(GdeupsAgtElecTmp perTmp : list){
			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap.put("JFH", perTmp.getFeeNum());
			infoMap.put("UsrNam", perTmp.getUserName());
			infoMap.put("ActNo", perTmp.getActNo());
			infoMap.put("ActNm", perTmp.getAcountName());
			infoMap.put("ACT", perTmp.getActType());
			infoMap.put("GPF", perTmp.getPerComFlag());
			infoMap.put("MOB", perTmp.getPhoneNum());
			infoMap.put("TEL", perTmp.getTelNum());
			//TODO	infoMap.put("TXT", perTmp.getRemark());
			infoList.add(infoMap);
		}
		context.setData("infoList", infoList);
		

		GdeupsAgtElecTmp delElecTmp = toGdeupsAgtElecTmp(context);
		delElecTmp.setStatus("1");
		delElecTmp.setRemark("删除日期:" + MGR_DATE);
		get(GdeupsAgtElecTmpRepository.class).updateByFeeNum(delElecTmp);

		buildContextAndCallThd(context);

		context.setData("ACN", list.get(0).getActNo());
		context.setData("ActNo", list.get(0).getActNo());
		context.setData("TXT", list.get(0).getUserName());

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
		agtElecTmp.setPrcessPassword((String) context.getData("Pin"));
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

package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.Date;
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
			checkCusInfoByCusAc(context);
			buildContextAndCallThd(context);
			addAgentDeal(context);
//			context.setData("TXT", context.getData("UsrNam"));
			break;
		case UPDATE:
			checkCusInfoByCusAc(context);
			checkOldBaseInfo(context);
			buildContextAndCallThd(context);
			updateAgentDeal(context);
//			context.setData("TXT", context.getData("UsrNam"));
			break;
		case QUERY:
//			buildContextAndCallThd(context);
			queryAgentDeal(context);
			break;
		case DELETE:
			buildContextAndCallThd(context);
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
			logger.info("There are no records for select check trans journal ");
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
		context.setData("StartAddr", "301");// TODO wangxianfen:301
		context.setData("DestAddr", "0500");// TODO 供电局代码 wangxianfen:0500
		context.setData("MesgID", msgId);

		context.setData("WorkDate", YYYYMMDD);
		context.setData("SendTime", YYYYMMDDHHMMSS);
		context.setData("mesgPRI", "9");

		context.setData("recordNum", "12"); // 不校验，但是要有值
		context.setData("FileName", "");// 无文件，空字符串
		context.setData("zipFlag", "0");// TODO

		// 2.拼装外发报文体
		// <fixString name="SBN" length="12" /> <!--发起业务的节点代码 -->
		// <fixString name="WDO" length="8 " /> <!--工作日期 -->
		// <fixString name="TLogNo" length="16" /> <!--供电局流水 -->
		// 其他字段前端输入
		context.setData("ECD", "0500");
		context.setData("EDD", "000");
		context.setData("SBN", "301");
		context.setData("WDO", YYYYMMDD);
		context.setData("TLogNo", ""); // TODO 供电局流水
		context.setData("KKB", br);
		context.setData("ZPF", "0");
		context.setData("FPF", "0");
		// TODO 根据银行卡获取开户信息 身份证号
		context.setData("IdTyp", "0");
//		context.setData("TIdNo", "123123");

		logger.info("=====外发电力========>>>>>>>>>>>>>>> context : " + context);
		// 外发thd
		Map<String, Object> callThdRsp = callThdTradeManager.trade(context);
		if (BPState.isBPStateOvertime(context)) {
			throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		}
		context.setDataMap(callThdRsp);

		// TODO 处理第三方返回消息，错误则不签约
		// if(((String) context.getData("rspMsg")).contains("错误")){
		// throw new CoreException((String) context.getData("rspMsg"));
		// }

		context.setData("comCode", "0500");
		context.setData("feeCode", "0000");
	}

	private void checkCusInfoByCusAc(Context context) throws CoreException {
		String cusAc = context.getData("ActNo").toString().trim();

		CusActInfResult cusactinfresult = new CusActInfResult();
		try {
			cusactinfresult = accountService.getAcInf(
					CommonRequest.build(context), cusAc);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		if ("E".equals(cusactinfresult.getResponseType())) {
			throw new CoreException(cusactinfresult.getResponseMessage());
		}
		String idNo = cusactinfresult.getIdNo();
		context.setData("TIdNo", idNo);
	}

	private void addAgentDeal(Context context) throws CoreException {
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		agtElecTmp.setFeeNum((String) context.getData("JFH"));
		List<GdeupsAgtElecTmp> list = get(GdeupsAgtElecTmpRepository.class)
				.find(agtElecTmp);
		if (list.size() > 0) {
			log.info("协议已经存在");
			throw new CoreException("协议已经存在");
		}
		agtElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).insert(agtElecTmp);
		log.info("新增协议成功");

	}

	private void updateAgentDeal(Context context) throws CoreException {
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);

		String oldCardNo = context.getData("OAC");
		String oldBankNum = context.getData("OKH");

		agtElecTmp.setOldBankNum(oldBankNum);
		agtElecTmp.setOldCardNo(oldCardNo);
		// get(GdeupsAgtElecTmpRepository.class).updateByAc(agtElecTmp);

		get(GdeupsAgtElecTmpRepository.class).updateByFeeNum(agtElecTmp);

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
		
		context.setData("ACN", list.get(0).getActNo());
		context.setData("ActNo", list.get(0).getActNo());
		context.setData("TXT", list.get(0).getUserName());
	}

	// 删除交易
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
		
		context.setData("ACN", list.get(0).getActNo());
		context.setData("ActNo", list.get(0).getActNo());
		context.setData("TXT", list.get(0).getUserName());
		
		GdeupsAgtElecTmp delElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).deleteByFeeNum(delElecTmp);

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

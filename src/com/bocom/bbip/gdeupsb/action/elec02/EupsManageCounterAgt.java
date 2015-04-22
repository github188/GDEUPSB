package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
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

	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	ThirdPartyAdaptor callThdTradeManager;

	public void process(Context context) throws CoreException {
		logger.info("协议维护");
		
		Date date = new Date();
		String YYYYMMDD = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);
		String YYYYMMDDHHMMSS = DateUtils.format(date, DateUtils.STYLE_yyyyMMddHHmmss);
		
		String br = context.getData(ParamKeys.BR);
		String sqnTmp = bbipPublicService.getBBIPSequence();
		sqnTmp = sqnTmp.substring(12);
		String msgId = br + " " + sqnTmp;
		
		// TODO 外发汕头电力，通知电力签约
		// 1.拼装外发报文头
		context.setData("AppTradeCode", "30");
		context.setData("StartAddr", "301");//TODO wangxianfen:301
		context.setData("DestAddr", "0500");//TODO 供电局代码 wangxianfen:0500
		context.setData("MesgID", msgId); 
		
		context.setData("WorkDate", YYYYMMDD);
		context.setData("SendTime", YYYYMMDDHHMMSS);
		context.setData("mesgPRI", "9");
//		recordNum	80	10	n	数据明细项数	　报文含有明细项数或者文件明细行数
//		FileName	90	32	a	文件名	没有文件则为32个空格
//		zipFlag	122	1	F	压缩标志	0：不压缩；1：压缩报文体；2：压缩文件
		context.setData("recordNum", "");//TODO
		context.setData("FileName", "");//无文件，空字符串
		context.setData("zipFlag", "0");//TODO

		// 2.拼装外发报文体
//		<fixString name="SBN" length="12" />     <!--发起业务的节点代码 -->
//		<fixString name="WDO" length="8 " />     <!--工作日期 -->
//		<fixString name="TLogNo" length="16" />  <!--供电局流水 -->
		//其他字段前端输入
		
		context.setData("SBN", br);
		context.setData("WDO", YYYYMMDD);
		context.setData("TLogNo", ""); //TODO 供电局流水
		
		//外发thd
		Map<String, Object> callThdRsp = callThdTradeManager.trade(context);
		CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
		String responseCode = rspCdeAction.getThdRspCde(callThdRsp, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
		if (BPState.isBPStateOvertime(context)) {
			throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		} else if (!Constants.RESPONSE_CODE_SUCC.equals(responseCode)) {
			if (StringUtils.isEmpty(responseCode)) {
				throw new CoreException(GDErrorCodes.EUPS_ELE_ST_UNKNOWN_ERROR);
			}
			throw new CoreException(responseCode);
		}
		context.setDataMap(callThdRsp);

		final int oprType = Integer.parseInt((String) context.getData("CHT"));
		switch (oprType) {
		case ADD:
			addAgentDeal(context);
			break;
		case UPDATE:
			updateAgentDeal(context);
			break;
		case QUERY:
			queryAgentDeal(context);
			break;
		case DELETE:
			deleteAgentDeal(context);
			break;
		}

	}

	private void addAgentDeal(Context context) throws CoreException {
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);
		List<GdeupsAgtElecTmp> list = get(GdeupsAgtElecTmpRepository.class)
				.findBase(agtElecTmp);
		if (list.size() > 0) {
			log.info("协议已经存在");
			throw new CoreException("协议已经存在");
		} else {
			get(GdeupsAgtElecTmpRepository.class).insert(agtElecTmp);
			log.info("新增协议成功");

		}
	}

	private void updateAgentDeal(Context context) throws CoreException {
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).updateByAc(agtElecTmp);

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

	}

	// 删除交易
	private void deleteAgentDeal(Context context) throws CoreException {
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).deleteByAc(agtElecTmp);

	}

	// 封装GdeupsAgtElecTmp对象
	private GdeupsAgtElecTmp toGdeupsAgtElecTmp(Context context) {

		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		agtElecTmp.setFeeNum((String) context.getData("JFH")); //
		agtElecTmp.setUserName((String) context.getData("UsrNam"));
		agtElecTmp.setActNo((String) context.getData("ActNo")); // 账号
		agtElecTmp.setAcountName((String) context.getData("ActNm")); // 账户名称
		agtElecTmp.setActType((String) context.getData("ACT")); // 账号类型
		agtElecTmp.setPerComFlag((String) context.getData("GPF")); // 个人/对公
		agtElecTmp.setPhoneNum((String) context.getData("MOB"));
		agtElecTmp.setTelNum((String) context.getData("TEL"));
		agtElecTmp.setPrcessPassword((String) context.getData("Pin"));

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

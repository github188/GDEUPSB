package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GdeupsAgtElecTmp;
import com.bocom.bbip.gdeupsb.repository.GdeupsAgtElecTmpRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class EupsManageAgt extends BaseAction {
	private static Logger logger = LoggerFactory
			.getLogger(EupsManageCounterAgt.class);
	private static final int ADD = 0;
	private static final int UPDATE = 3;
	private static final int QUERY = 5;
	private static final int DELETE = 9;
	@Autowired
	BBIPPublicService bbipPublicService;

	public void process(Context context) throws CoreException {
		logger.info("协议维护");
		context.setData("ActDat",
				DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		context.setData("LogNo", StringUtils.substring(
				((BBIPPublicServiceImpl) get(GDConstants.BBIP_PUBLIC_SERVICE))
						.getBBIPSequence(), 4));

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

	//TODO catch exceptions
	
	private void addAgentDeal(Context context) throws CoreException {
		// 新增，入库本地
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).insert(agtElecTmp);
	}

	private void updateAgentDeal(Context context) throws CoreException {
		// 修改本地协议
		GdeupsAgtElecTmp agtElecTmp = toGdeupsAgtElecTmp(context);
		get(GdeupsAgtElecTmpRepository.class).save(agtElecTmp);
	}

	private void queryAgentDeal(Context context) throws CoreException {
		//查询本地协议表
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();

		String feeNum = (String) context.getData("JFH");
		if (feeNum != null || feeNum.trim() != "") {
			agtElecTmp.setFeeNum(feeNum); //
		}
		agtElecTmp.setActNo((String) context.getData("TActNo")); // 账号
		List<GdeupsAgtElecTmp> list = get(GdeupsAgtElecTmpRepository.class).findBase(agtElecTmp);
		if(list.size()>0){
			agtElecTmp = list.get(0);
			//查询结果数据处理
			setResponseResultFromAgts(context,agtElecTmp);
		}else{
			log.info("没有查询到协议信息！");
			 throw new CoreException("没有查询到协议信息！");
		}
	}

	// 删除交易
	private void deleteAgentDeal(Context context) throws CoreException {
		// 删除本地协议
		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();
		agtElecTmp.setActNo((String) context.getData("TActNo"));
		agtElecTmp.setFeeNum((String) context.getData("JFH"));
		get(GdeupsAgtElecTmpRepository.class).delete(agtElecTmp);
	}

	// 为查询返回报文赋值
	private void setResponseResultFromAgts(Context context,
			GdeupsAgtElecTmp agtElecTmp) {

		context.setData("JFH", agtElecTmp.getFeeNum()); // 协议编号
		context.setData("UsrNam", agtElecTmp.getUserName()); // 单位编号
		context.setData("TActNo", agtElecTmp.getActNo()); // 第三方客户名称
		context.setData("TActNm", agtElecTmp.getAcountName()); // 缴费号码
		context.setData("ACT", agtElecTmp.getActType()); // 账号leixing
		context.setData("TActNo", agtElecTmp.getActNo()); //
		context.setData("GPF", agtElecTmp.getPerComFlag()); // 名称
		context.setData("MOB", agtElecTmp.getPhoneNum()); // 证件类型
		context.setData("TEL", agtElecTmp.getTelNum()); // 证件号码

	}

	private GdeupsAgtElecTmp toGdeupsAgtElecTmp(Context context) {

		GdeupsAgtElecTmp agtElecTmp = new GdeupsAgtElecTmp();

		// agtElecTmp.setChangeType( (String)context.getData("CHT"));
		agtElecTmp.setComCode((String) context.getData("ECD"));
		agtElecTmp.setFeeCode((String) context.getData("EDD"));
		agtElecTmp.setFeeNum((String) context.getData("JFH"));
		agtElecTmp.setUserName((String) context.getData("UsrNam"));
		agtElecTmp.setOldBankNum((String) context.getData("OKH"));
		agtElecTmp.setOldCardNo((String) context.getData("OAC"));
		agtElecTmp.setNewBankNum((String) context.getData("KKB"));
		agtElecTmp.setActNo((String) context.getData("TActNo"));
		agtElecTmp.setAcountName((String) context.getData("TActNm"));
		agtElecTmp.setActType((String) context.getData("ACT"));
		agtElecTmp.setPerComFlag((String) context.getData("GPF"));
		agtElecTmp.setIdType((String) context.getData("TIdTyp"));
		agtElecTmp.setIdNo((String) context.getData("TIdNo"));
		agtElecTmp.setCheckSendType((String) context.getData("ZPF"));
		agtElecTmp.setInvoiceSnedType((String) context.getData("FPF"));
		agtElecTmp.setPointNum((String) context.getData("JLD"));

		agtElecTmp.setInvoiceSnedMan((String) context.getData("FPM"));
		agtElecTmp.setInvoiceSendZip((String) context.getData("FPC"));
		agtElecTmp.setInvoiceSendAddr((String) context.getData("FPA"));
		agtElecTmp.setCheckSendMan((String) context.getData("ZPM"));
		agtElecTmp.setCheckSendZip((String) context.getData("ZPC"));
		agtElecTmp.setCheckSendAddr((String) context.getData("ZPA"));
		agtElecTmp.setNotifyType((String) context.getData("YBZ"));
		agtElecTmp.setEmail((String) context.getData("EML"));
		agtElecTmp.setPhoneNum((String) context.getData("MOB"));
		agtElecTmp.setTelNum((String) context.getData("TEL"));
		agtElecTmp.setRemark((String) context.getData("TXT"));
		agtElecTmp.setPrcessPassword((String) context.getData("Pin"));

		return agtElecTmp;
	}

}

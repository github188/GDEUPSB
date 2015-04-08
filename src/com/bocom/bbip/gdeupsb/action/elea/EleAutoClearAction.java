package com.bocom.bbip.gdeupsb.action.elea;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdElecClrInf;
import com.bocom.bbip.gdeupsb.entity.GdElecClrJnl;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GdElecClrInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdElecClrJnlRepository;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class EleAutoClearAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("广州电力自动清算开始!..");
		
		//基本数据生成
		String sqn = get(BBIPPublicService.class).getBBIPSequence(); // 获取sqn
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		context.setData(ParamKeys.BK, "01441999999"); // 分行号
		context.setData(ParamKeys.BR, "01444800999"); // 机构号
		context.setData(ParamKeys.TXN_DATE, new Date());
		context.setData(ParamKeys.TXN_TIME, new Date());
		context.setData(ParamKeys.SEQUENCE, sqn);
		// 获取虚拟柜员
         String teller = get(BBIPPublicService.class).getETeller(context.getData(ParamKeys.BK).toString());
         context.setData(ParamKeys.TELLER, teller);
         
		// TODO:交易上锁
//		Result ret = get(BBIPPublicService.class).tryLock("eleAutoClear", (long) 0, (long) 1000 * 60 * 20);
//		int status = ret.getStatus();
//		if (status != 0) {
//			throw new CoreException(GDErrorCodes.EUPS_LOCK_FAIL, "交易加锁失败!!!");
//		}

		String comNo = "GDELEC01";
		// 判断业务是否签退状态
		EupsThdTranCtlInfo tranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(comNo);
		String txnCtlSts = tranCtlInfo.getTxnCtlSts();
		if (!Constants.SIGN_SET_TYPE_SIGNOUT.equals(txnCtlSts)) {
			throw new CoreException(ErrorCodes.EUPS_THD_TRAN_CTL_NOLOGIN);
		}

		// 获取电力清算信息表信息
		GdElecClrInf gdElecClrInf = new GdElecClrInf();
		gdElecClrInf.setBrNo("441999");
		List<GdElecClrInf> gdElecClrInfList = get(GdElecClrInfRepository.class).find(gdElecClrInf);
		if (CollectionUtils.isEmpty(gdElecClrInfList)) {
			log.error("不存在清算参数信息");
			throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CLEAR_INFO_ERROR); // 不存在清算参数信息
		}
		gdElecClrInf = gdElecClrInfList.get(0);

		// 检查是否允许自动清算
		String autFlg = gdElecClrInf.getAutFlg();
		if ("1".equals(autFlg)) { // 不允许自动清算
			log.error("不允许自动清算!");
			throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_AUTO_CLEAR_ERROR);
		}
		// 获取清算信息表中记录的清算日期时间

		String oClrDat = gdElecClrInf.getClrDat();
		String oClrTme = gdElecClrInf.getClrTim();

		// 查找电力清算明细表，判断当日是否已有清算记录
		GdElecClrJnl elecClrJnl = new GdElecClrJnl();
		elecClrJnl.setClrDat(oClrDat);
		List<GdElecClrJnl> gdElecClrJnl = get(GdElecClrJnlRepository.class).find(elecClrJnl);
		if (CollectionUtils.isNotEmpty(gdElecClrJnl)) {
			throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_ALREADY_CLEAR_ERROR);
		}

		// TODO：判断清算的时间是否相等
		String nowDteStr = DateUtils.format(new Date(), "yyyyMMDDhhmm");
		String delClrTme = oClrDat + oClrTme;
//		if (!nowDteStr.equals(delClrTme)) {
		log.info("当前时间为:"+nowDteStr+"预期的清算时间为:"+delClrTme);
//			throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CLEAR_TIME_ERROR);
//		}

		// 修改清算信息表为清算状态
		gdElecClrInf.setClrSts("1");
		gdElecClrInf = get(GdElecClrInfRepository.class).save(gdElecClrInf);
		log.info("更改清算信息表状态为清算成功");

		// TODO:查询主机内部账户当日入账金额

		// 查找当日缴费及划扣金额，条件为主机及交易状态均为成功
		GdEupsTransJournal transJnl = new GdEupsTransJournal();
		transJnl.setBakFld1(oClrDat);
		transJnl.setMfmTxnSts("S");
		transJnl.setTxnSts("S");
		transJnl.setEupsBusTyp("ELEC01");
		String clrAmt = "0.00";
		String clrCnt = "0";
		List<Map<String, Object>> transJnlList = get(GdEupsTransJournalRepository.class).findGzClrJnl(transJnl);
		if (CollectionUtils.isNotEmpty(transJnlList)) {
			Map<String, Object> transJnlInfo = transJnlList.get(0);
			if (null != transJnlInfo.get("TXNAMT")) {
				clrAmt = transJnlInfo.get("TXNAMT").toString();
			}
			if (null != transJnlInfo.get("TXNCNT")) {
				clrCnt = transJnlInfo.get("TXNCNT").toString();
			}
			log.info("当前的清算金额为:" + clrAmt + ",当前的清算笔数为:" + clrAmt);
		} else {
			throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CLEAR_INFO_NOT_EXIST);
		}

		
		// 初始化一条清算信息到清算流水表中去
		String agtNo = CodeSwitchUtils.codeGenerator("DptTypToCAgtNo_DL", "0000");
		context.setData("inAct", agtNo);

		GdElecClrJnl clrJnl = new GdElecClrJnl();
	
		clrJnl.setSqn(sqn);
		clrJnl.setcAgtNo(agtNo);
		clrJnl.setClrAmt(clrAmt); // 清算金额
		clrJnl.setClrDat(oClrDat);
		clrJnl.setClrRst("0"); // 清算状态为未清算
		clrJnl.setClrTyp("0"); // 自动清算
		clrJnl.setClrTot(clrCnt); // 清算笔数
		clrJnl.setBrNo(teller);
		clrJnl.setNodNo("01441800"); // 机构号

		context.setData("clrAmt", clrAmt);
		context.setData("sqn", sqn);
		// 初始化
		get(GdElecClrJnlRepository.class).save(clrJnl);
		
		context.setVariable("clrJnl", clrJnl);  //清算初始化数据
		context.setVariable("clrInf", gdElecClrInf);  //清算基本信息
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);

		log.info("交易初始化结束,准备进行清算账务处理");
	}
}

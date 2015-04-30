package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspNpManag;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspTxnJnl;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspZzManag;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspNpManagRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspTxnJnlRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspZzManagRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PrintNpAction extends BaseAction{
	private final static Log log = LogFactory.getLog(PrintNpAction.class);
	
	@Autowired
	GDEupsbTrspZzManagRepository gdEupsbTrspZzManagRepository;
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	@Autowired
	GDEupsbTrspTxnJnlRepository gdEupsbTrspTxnJnlRepository;
	@Autowired
	GDEupsbTrspNpManagRepository gdEupsbTrspNpManagRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PrintNpAction start......");
		String today=DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
		ctx.setData(GDParamKeys.NOD_NO, ctx.getData(ParamKeys.BR));
		ctx.setData(GDParamKeys.TLR_ID, ctx.getData(ParamKeys.TELLER));
		ctx.setData(GDParamKeys.BR_NO, ctx.getData(ParamKeys.BK));
		ctx.setData("oTlrId", ctx.getData(GDParamKeys.TLR_ID));


		GDEupsbTrspZzManag gdEupsbTrspZzManag = new GDEupsbTrspZzManag();
		gdEupsbTrspZzManag.setZzDat(today);
		gdEupsbTrspZzManag.setNodNo(ctx.getData(GDParamKeys.NOD_NO).toString());
		List< GDEupsbTrspZzManag> zzManagList = gdEupsbTrspZzManagRepository.find(gdEupsbTrspZzManag);
		if(!CollectionUtils.isEmpty(zzManagList)){
//			ctx.setData(ParamKeys.RSP_MSG, "当日已轧帐，不允许打印");
			throw new CoreRuntimeException("BBIP4400EU0735");
		}

		//检查是否已经打了发票
		GDEupsbTrspFeeInfo gdeupsbrspfFeeInfo = new GDEupsbTrspFeeInfo();
		gdeupsbrspfFeeInfo.setPayLog(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
		List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.find(gdeupsbrspfFeeInfo);

		if(CollectionUtils.isEmpty(feeInfoList)){
//			ctx.setData(ParamKeys.RSP_MSG, "缴费记录不存在");
			throw new CoreRuntimeException("BBIP4400EU0730");
		}else if(!GDConstants.DP.equals(feeInfoList.get(0).getStatus())){
//			ctx.setData(ParamKeys.RSP_MSG, "该缴费流水号未打印路桥发票");
			throw new CoreRuntimeException("BBIP4400EU0733");	
		}else{
			ctx.setData("statusT", feeInfoList.get(0).getStatus());
			ctx.setData(GDParamKeys.INV_NO, feeInfoList.get(0).getInvNo());
		}
		
		//查询缴费记录表中是否满足缴费成功且缴费12个月的记录 
		GDEupsbTrspTxnJnl gdEupsbTrspTxnJnl = new GDEupsbTrspTxnJnl();
		gdEupsbTrspTxnJnl.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
		gdEupsbTrspTxnJnl.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspTxnJnl.setTxnSt("S");
		gdEupsbTrspTxnJnl.setPayMon("12");
		List<GDEupsbTrspTxnJnl> txnJnlList = gdEupsbTrspTxnJnlRepository.find(gdEupsbTrspTxnJnl);
		if(CollectionUtils.isEmpty(txnJnlList)){
//			ctx.setData(ParamKeys.RSP_MSG, "该缴费流水号未缴纳全年路桥费");
			throw new CoreRuntimeException("BBIP4400EU0736");
		}
		
		//查询打印情况
		GDEupsbTrspNpManag gdEupsbTrspNpManag = new GDEupsbTrspNpManag();
		gdEupsbTrspNpManag.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
		gdEupsbTrspNpManag.setIdNo(ctx.getData(GDParamKeys.ID_NO).toString());
		List<GDEupsbTrspNpManag> npManagList = gdEupsbTrspNpManagRepository.find(gdEupsbTrspNpManag);
		if(CollectionUtils.isEmpty(npManagList)){
			GDEupsbTrspNpManag gdEupsbTrspNpManagA = new GDEupsbTrspNpManag();
			gdEupsbTrspNpManagA.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
			List<GDEupsbTrspNpManag> npManagListA=gdEupsbTrspNpManagRepository.find(gdEupsbTrspNpManagA);
			if(!CollectionUtils.isEmpty(npManagListA)){
				if("0".equals(npManagListA.get(0).getStatus())){
//					ctx.setData(ParamKeys.RSP_MSG, "该缴费流水号已打印");
					throw new CoreRuntimeException("BBIP4400EU0737");
				}
			}
			
			//导入年票管理表
			GDEupsbTrspNpManag gdEupsbTrspNpManagB = new GDEupsbTrspNpManag();
			gdEupsbTrspNpManagB.setNodNo(ctx.getData(GDParamKeys.NOD_NO).toString());
			gdEupsbTrspNpManagB.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
			gdEupsbTrspNpManagB.setInvNo(ctx.getData(GDParamKeys.INV_NO).toString());
			gdEupsbTrspNpManagB.setIdNo(ctx.getData(GDParamKeys.ID_NO).toString());
			gdEupsbTrspNpManagB.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
			gdEupsbTrspNpManagB.setBegDat((Date)ctx.getData(GDParamKeys.BEG_DAT));
			gdEupsbTrspNpManagB.setEndDat((Date)ctx.getData(GDParamKeys.END_DAT));
			gdEupsbTrspNpManagB.setTlrId(ctx.getData("oTlrId").toString());
			gdEupsbTrspNpManagRepository.insert(gdEupsbTrspNpManagB);
		}else{
			ctx.setData("statusR", npManagList.get(0).getStatus());
			if("0".equals(ctx.getData("statusR"))){
//				ctx.setData(ParamKeys.RSP_MSG, "该缴费流水号已打印");
				throw new CoreRuntimeException("BBIP4400EU0737");
			}
			
			if("1".equals(ctx.getData("statusR"))){
//				ctx.setData(ParamKeys.RSP_MSG, "该缴费流水号已作废");
				throw new CoreRuntimeException("BBIP4400EU0738");
			}
		}
		//更新年票管理表状态
		GDEupsbTrspNpManag gdEupsbTrspNpManagC = new GDEupsbTrspNpManag();
		gdEupsbTrspNpManagC.setStatus("0");
		gdEupsbTrspNpManagC.setPrtDat(new Date());
		gdEupsbTrspNpManagC.setSqn(ctx.getData(ParamKeys.OLD_TXN_SQN).toString());
		gdEupsbTrspNpManagC.setIdNo(ctx.getData(GDParamKeys.ID_NO).toString());
		gdEupsbTrspNpManagRepository.updateSt(gdEupsbTrspNpManagC);
		log.info("@@@@@@@@@@@+"+ctx);
	}

}

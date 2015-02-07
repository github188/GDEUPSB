package com.bocom.bbip.gdeupsb.action.efek.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CancelBatchAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(CancelBatchAction.class);
    @Autowired
    GDEupsBatchConsoleInfoRepository gdEupsBatchConsoleInfoRepository;
    /**
     * 南方电网 批量撤销
     */
    public void execute(Context context)throws CoreException{
	   logger.info("===========Start  CancelBatchAction");
	   //获得原交易流水日期时间
	   String batNo=context.getData(ParamKeys.BAT_NO).toString();
	   String rsvFld2=context.getData(ParamKeys.RSV_FLD2).toString();
	   String rsvFld3=context.getData(ParamKeys.RSV_FLD3).toString();
	   //从批次表中得到信息
	   GDEupsBatchConsoleInfo gdEupsBatchConsoleInfo=gdEupsBatchConsoleInfoRepository.findOne(batNo);
	   //得到状态和批次号
	   String batSts=gdEupsBatchConsoleInfo.getBatSts();
	   //上锁
	   get(BBIPPublicService.class).tryLock(batNo, (long)0, (long)1000 * 60 * 20);
	   //判断状态和返回
	   if("S".equals(batSts)){
		   context.setData(GDParamKeys.MSGTYP, "N");
		   context.setData(ParamKeys.RSP_CDE, Constants.HOST_RESPONSE_CODE_SUCC);
		   context.setData(ParamKeys.RSP_MSG, "批次【批次号："+batNo+"】【交易日期："+rsvFld2+"】【交易时间："+rsvFld3+"】已扣款，取消失败");
		   context.setData(ParamKeys.RSV_FLD4, context.getData(ParamKeys.RSP_MSG));
		   logger.info("~~~~~~~~~~~账务处理完成");
	   }else if("U".equals(batSts) || "W".equals(batSts)){
		   gdEupsBatchConsoleInfo.setBatSts("C");
		   gdEupsBatchConsoleInfo.setBatNo(batNo);
		   gdEupsBatchConsoleInfoRepository.updateConsoleInfo(gdEupsBatchConsoleInfo);

		   context.setData(GDParamKeys.MSGTYP, "N");
		   context.setData(ParamKeys.RSP_CDE, Constants.HOST_RESPONSE_CODE_SUCC);
		   context.setData(ParamKeys.RSP_MSG, "批次批次【批次号："+batNo+"】【交易日期："+rsvFld2+"】【交易时间："+rsvFld3+"】取消成功");
		   context.setData(GDParamKeys.CANCEL_SIGN, "1");
		   context.setData(ParamKeys.RSV_FLD4, context.getData(ParamKeys.RSP_MSG));
		   logger.info("~~~~~~~~~~~~~~~~取消成功");
	   }else if("C".equals(batSts)){
		   context.setData(GDParamKeys.MSGTYP, "N");
		   context.setData(ParamKeys.RSP_CDE, Constants.HOST_RESPONSE_CODE_SUCC);
		   context.setData(ParamKeys.RSP_MSG, "批次【批次号："+batNo+"】【交易日期："+rsvFld2+"】【交易时间："+rsvFld3+"】已经撤销");
		   context.setData(GDParamKeys.CANCEL_SIGN, "1");
		   context.setData(ParamKeys.RSV_FLD4, context.getData(ParamKeys.RSP_MSG));
		   logger.info("~~~~~~~~~~~~~~~~已经撤销");
	   } else{
		   context.setData(GDParamKeys.MSGTYP, "E");
		   context.setData(ParamKeys.RSP_CDE, "EFE999");
		   context.setData(ParamKeys.RSP_MSG, "批次【批次号："+batNo+"】【交易日期："+rsvFld2+"】【交易时间："+rsvFld3+"】处理异常");
		   context.setData(GDParamKeys.CANCEL_SIGN, "4");
		   context.setData(ParamKeys.RSV_FLD4, context.getData(ParamKeys.RSP_MSG));
	   }
	   //解锁
	   get(BBIPPublicService.class).unlock(batNo);
	   logger.info("===========End  CancelBatchAction");
   }
}

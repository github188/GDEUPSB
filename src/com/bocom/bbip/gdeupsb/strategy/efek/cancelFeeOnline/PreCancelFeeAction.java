package com.bocom.bbip.gdeupsb.strategy.efek.cancelFeeOnline;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;
/**
 * @author liyawei
 */
public class PreCancelFeeAction implements Executable{
	private final static Log logger=LogFactory.getLog(PreCancelFeeAction.class);
	@Autowired
	EupsThdTranCtlInfoRepository eupsThdTranCtlInfoRepository;
	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;
	/**
	 *抹账前处理 
	 */
	@Override
	public void execute(Context context) throws CoreException,CoreRuntimeException{
		logger.info("==========Start  PreCancelFeeAction");
		context.setData(ParamKeys.TRADE_TXN_DIR, "O");   //交易方向
		
		EupsTransJournal eupsTransJournal=eupsTransJournalRepository.findOne(context.getData("oldTxnSqn").toString());
		String txnSts=eupsTransJournal.getTxnSts();
		if(txnSts.equals("C")){
				throw new CoreException("已进行抹账，不能再次抹账");
		}else if(txnSts.equals("F") ){
				throw new CoreException("交易失败，不能进行抹账");
		}else if(txnSts.equals("R") ){
				throw new CoreException("交易正在重发，不能经行抹账");
		}
		
		context.setData(GDParamKeys.SVRCOD, "12");
		context.setData(ParamKeys.THD_CUS_NO, context.getData(GDParamKeys.PAY_NO));
		BigDecimal bigDecimal=new BigDecimal(context.getData(ParamKeys.TXN_AMT).toString()).scaleByPowerOfTen(2);
		context.setData("txnAmts", bigDecimal);
		context.setData("CXMoney", bigDecimal);
		context.setData(ParamKeys.CUS_NME, context.getData("CusNme"));
		context.setData("accountsSerialNos", context.getData("accountsSerialNo"));
		context.setData("comNos", "032015");
		System.out.println();
		System.out.println("~~~~~~~~~~"+context.getData("comNo"));
		//TODO 
		context.setData("thdObkCde", context.getData(ParamKeys.BANK_NO));
					context.setData(GDParamKeys.TOTNUM, "1");
					callThd(context);
				
				context.setData(GDParamKeys.BAG_TYPE, "0");
				logger.info("==========End  PreCancelFeeAction  ");
		 }
/**
 *报文信息
 */
	public void callThd(Context context){
		logger.info("==========Start  PreCancelFeeAction  callThd");
		context.setData(GDParamKeys.TREATY_VERSION, GDConstants.TREATY_VERSION);//协议版本
		context.setData(GDParamKeys.TRADE_PERSON_IDENTIFY, GDConstants.TRADE_PERSON_IDENTIFY);//交易人标识
		context.setData(GDParamKeys.BAG_TYPE, GDConstants.BAG_TYPE);//数据包类型
		context.setData(GDParamKeys.TRADE_START,GDConstants.TRADE_START);//交易发起方
				
				context.setData(GDParamKeys.TRADE_SEND_DATE,DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));//交易发送日期
				context.setData(GDParamKeys.TRADE_SEND_TIME, DateUtils.formatAsHHmmss(new Date()));//交易发送时间
				context.setData(GDParamKeys.TRADE_PRIORITY, GDConstants.TRADE_PRIORITY);//交易优先
				context.setData(GDParamKeys.REDUCE_SIGN, GDConstants.REDUCE_SIGN);//压缩标志
				context.setData(GDParamKeys.TRADE_RETURN_CODE, GDConstants.TRADE_RETURN_CODE);//交易返回代码

				
				context.setData(GDParamKeys.NET_NAME, GDConstants.NET_NAME);//网点名称
				context.setData(GDParamKeys.SECRETKEY_INDEX, GDConstants.SECRETKEY_INDEX);//密钥索引
				context.setData(GDParamKeys.SECRETKEY_INIT, GDConstants.SECRETKEY_INIT);//密钥初始向量
				context.setData(GDParamKeys.TRADE_RECEIVE, GDConstants.TRADE_RECEIVE);//交易接收方
				context.setData(GDParamKeys.TRADE_SOURCE_ADD, GDConstants.TRADE_SOURCE_ADD);//交易源地址
				context.setData(GDParamKeys.TRADE_AIM_ADD, GDConstants.TRADE_AIM_ADD);//交易目标地址
				context.setData("PKGCNT", "000001");
				context.setData("sqns", context.getData("sqn"));
				context.setData(GDParamKeys.BUS_IDENTIFY, "YDLW05");
				context.setData(ParamKeys.THD_TXN_DATE, DateUtils.format((Date)context.getData("thdTxnDte"), DateUtils.STYLE_yyyyMMdd));
				context.setData(ParamKeys.THD_TXN_TIME, DateUtils.format((Date)context.getData("thdTxnTme"), DateUtils.STYLE_HHmmss));
	}
}

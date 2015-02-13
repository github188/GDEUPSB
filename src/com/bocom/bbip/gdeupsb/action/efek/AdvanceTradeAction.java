package com.bocom.bbip.gdeupsb.action.efek;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class AdvanceTradeAction extends BaseAction {
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
    @Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
	private final static Log logger = LogFactory.getLog(AdvanceTradeAction.class);

	/**
	 * 银行发起存入预付款
	 */

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {

		logger.info("===========Start  AdvanceTradeAction");
		context.setData(GDParamKeys.TOTNUM, "1");
		context.setData(ParamKeys.SEQUENCE, bbipPublicService.getBBIPSequence());
		context.setData(ParamKeys.REQ_JRN_NO, context.getData(ParamKeys.SEQUENCE));
		// 第三方客户标识
		context.setData(ParamKeys.THD_CUS_NO,context.getData(GDParamKeys.PAY_NO));
		
//	      context.setData(ParamKeys.CHL_TYP, "TRM");
//	      context.setData(ParamKeys.TRADE_TXN_DIR, "O");
//	      context.setData(ParamKeys.BAT_FLAG, "S");
//	      context.setData(ParamKeys.THD_TXN_NO, "0000000001");
	      
		// 预付费金额设定
		context.setData(ParamKeys.TXN_AMT,context.getData(GDParamKeys.PAYMENTIN_ADVANCE_MONEY));
		context.setData(ParamKeys.PAYFEE_TYPE, context.getData(ParamKeys.PAY_MDE));
		
		context.setData(ParamKeys.TXN_DTE,DateUtils.parse(DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd)));
		context.setData(ParamKeys.TXN_TME,DateUtils.formatAsTranstime(new Date()));
		// 柜员号 
		// 记账
		String ActFlg = (String) context.getData(ParamKeys.ACC_TYPE); // 银行内部账务类型
//		TML_NO
//		TXN_TLR
//		SVR_NME
		// TODO InAcNo 不确定 日间记账账号
		// <Set>ActSqn=SUBSTR($InAcNo,14,5)</Set>　　
		// <Set>ActNod=SUBSTR($InAcNo,1,6)</Set>
		String ActNod = context.getData(ParamKeys.CUS_AC).toString().substring(1, 6);
		String ActSqn = context.getData(ParamKeys.CUS_AC).toString().substring(14, 19);
		
		if ("0".equals(ActFlg)) { // 对公
			// 需要GDContants定义常量
			context.setData(ParamKeys.THD_TXN_CDE, "451240");

			context.setData(GDParamKeys.CCYTYP, "EFE999999999");
			context.setData("NamChk", "1");// <Set>NamChk=1</Set>
											// <!--测试不检查，生产考虑打开-->
			context.setData(ParamKeys.TXN_CHL, "0");// <Set>VchFlg=0</Set>
													// <!--渠道交易不检查-->

			// 字段长度 context.setData(ParamKeys.BV_KIND,"000"); 凭证种类 字符长度
			context.setData(ParamKeys.BV_KIND, "00");

			context.setData(ParamKeys.BV_NO, "00000000");
			context.setData(ParamKeys.CCY_NO,Constants.EUPS_PAYMENT_TO_ACPS_CCY_CDE);

			context.setData(GDParamKeys.ACCMOD, "1");
			context.setData("ActSqn", ActSqn);// <Set>ActSqn=SUBSTR($InAcNo,14,5)</Set>
												// InAcNo日间记账账号
			context.setData("ActNod", ActNod);// <Set>ActNod=SUBSTR($InAcNo,1,6)</Set>
			context.setData(ParamKeys.BAK_FLD1, "代扣电费");
		} else if ("4".equals(ActFlg)) { // 卡
			context.setData(ParamKeys.TXN_CODE, "471140");
			context.setData(ParamKeys.CHL_TYP, "L"); // <Set>CnlTyp=L</Set>
			context.setData("Mask", "9102");// <Set>Mask=9102</Set>

			// 字段长度 context.setData(ParamKeys.BV_KIND,"000"); 凭证种类 字符长度
			context.setData(ParamKeys.BV_KIND, "00");

			context.setData(ParamKeys.BV_NO, "00000000");// <Set>VchCod=00000000</Set>
			context.setData(ParamKeys.CCY_NO,
					Constants.EUPS_PAYMENT_TO_ACPS_CCY_CDE);

			context.setData(ParamKeys.PAY_MDE, "0");
			context.setData(GDParamKeys.CCYTYP, "1");
			context.setData(GDParamKeys.VCHCHK, "0");

			context.setData("ActSeq", ActSqn); // <Set>ActSeq=SUBSTR($InAcNo,14,5)</Set>
												// InAcNo日间记账账号
			context.setData("CAgtNo", "EFE9999999"); // 清算单位协议号要改

			context.setData("GthFlg", "N");// <Set>GthFlg=N</Set>
		}
			context.setData(GDParamKeys.SVRCOD, "13");
			callThd(context);
			
			logger.info("~~~~~~~~~~~~End AdvanceTradeAction");
	}
	/**
	 *报文信息  外发第三方
	 */
	public void callThd(Context context){  
		
		context.setData(GDParamKeys.TREATY_VERSION, "1.0.0");//协议版本
		context.setData(GDParamKeys.TRADE_PERSON_IDENTIFY, "301_030600");//交易人标识
		context.setData(GDParamKeys.BAG_TYPE, "0");//数据包类型
		context.setData(GDParamKeys.TRADE_START, "301");//交易发起方
		
		context.setData(GDParamKeys.TRADE_SEND_DATE,DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));//交易发送日期
		context.setData(GDParamKeys.TRADE_SEND_TIME, DateUtils.formatAsHHmmss(new Date()));//交易发送时间
				context.setData(GDParamKeys.TRADE_PRIORITY, "2");//交易优先
				context.setData(GDParamKeys.REDUCE_SIGN, "0");//压缩标志
				context.setData(GDParamKeys.TRADE_RETURN_CODE, "00");//交易返回代码
		
				context.setData(GDParamKeys.NET_NAME, "@BCFG.BrNam");//网点名称
				context.setData(GDParamKeys.SECRETKEY_INDEX, "0");//密钥索引
				context.setData(GDParamKeys.SECRETKEY_INIT, "");//密钥初始向量
				context.setData(GDParamKeys.TRADE_RECEIVE, "030600");//交易接收方
				context.setData(GDParamKeys.TRADE_SOURCE_ADD, "");//交易源地址
				context.setData(GDParamKeys.TRADE_AIM_ADD, "");//交易目标地址
	/*		
				try{
					Map<String, Object> rspMap = callThdTradeManager.trade(context);
					
						if(BPState.isBPStateNormal(context)){
								if(null !=rspMap){
									 	context.setDataMap(rspMap);
						                context.setData(ParamKeys.THIRD_RETURN_MESSAGE, rspMap);
						                //第三方返回码
						                CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
						                String responseCode = rspCdeAction.getThdRspCde(rspMap, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
						                logger.info("third response code="+responseCode);
						                if(StringUtils.isEmpty(responseCode)){
						                	responseCode=ErrorCodes.EUPS_THD_SYS_ERROR;
						                }
						                context.setData(ParamKeys.RESPONSE_CODE, responseCode);
						                
						             // 第三方交易成功
							                if (GDConstants.SUCCESS_CODE.equals(responseCode)) {
							                    logger.info("The third process response successful.");
							                    context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_SUCCESS);
							                    context.setData(ParamKeys.THD_TXN_STS, Constants.THD_TXNSTS_SUCCESS);
							                    context.setData(ParamKeys.RSP_CDE, GDConstants.SUCCESS_CODE);
							                    context.setData(ParamKeys.RSP_MSG, "交易成功");
							                }else if(BPState.isBPStateReversalFail(context)){
							                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
							                	context.setData(GDParamKeys.MSGTYP, "E");
							                	context.setData(ParamKeys.RSP_CDE, "EFE999");
							                	context.setData(ParamKeys.RSP_MSG, "交易失败");
							                }else if(BPState.isBPStateOvertime(context)){
							                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
							                	context.setData(GDParamKeys.MSGTYP, "E");
							                	context.setData(ParamKeys.RSP_CDE, "EFE999");
							                	context.setData(ParamKeys.RSP_MSG, "交易超时");
							                }else if(BPState.isBPStateSystemError(context)){
							                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
							                	context.setData(GDParamKeys.MSGTYP, "E");
							                	context.setData(ParamKeys.RSP_CDE, "EFE999");
							                	context.setData(ParamKeys.RSP_MSG, "系统错误");
							                }else if(BPState.isBPStateTransFail(context)){
							                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
							                	context.setData(GDParamKeys.MSGTYP, "E");
							                	context.setData(ParamKeys.RSP_CDE, "EFE999");
							                	context.setData(ParamKeys.RSP_MSG, "发送失败");
							                }else{
							                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
							                	context.setData(GDParamKeys.MSGTYP, "E");
							                	context.setData(ParamKeys.RSP_CDE, "EFE999");
							                	context.setData(ParamKeys.RSP_MSG, "交易失败，其他未知情况");
							                }
								}
						}else{
								logger.info("~~~~~~~~~~~发送失败");
							    context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
				                context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_REVERSE);
				                context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
				                context.setData(ParamKeys.RESPONSE_TYPE, Constants.RESPONSE_TYPE_FAIL);
				                context.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.EUPS_THD_SYS_ERROR);
				                context.setData(ParamKeys.RESPONSE_MESSAGE, Constants.RESPONSE_MSG_FAIL);
				                context.setData(ParamKeys.THD_RSP_MSG,Constants.RESPONSE_MSG_FAIL);
				                throw new CoreException("发送失败");
						}
				}catch(CoreException e){
					logger.info("Bypass call THIRD response failed or unknow error.");
					context.setData(ParamKeys.TXN_STS, Constants.TXNSTS_REVERSE);
					context.setData(ParamKeys.THD_TXN_STS, Constants.TXNSTS_FAIL);
					context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
				}
		*/	
	}
}

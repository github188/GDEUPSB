package com.bocom.bbip.gdeupsb.strategy.efek.checkThdAcct;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlDetail;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsThdTranCtlDetailRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.spi.service.check.CheckThdSumAcctService;
import com.bocom.bbip.eups.spi.vo.CheckDomain;
import com.bocom.bbip.eups.utils.CommonUtil;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.thd.org.apache.commons.lang.StringUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CheckThdSumAcctAction implements  CheckThdSumAcctService{
	private final static Log logger=LogFactory.getLog(CheckThdSumAcctAction.class);
	@Autowired
	EupsThdTranCtlDetailRepository eupsThdTranCtlDetailRepository;
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	EupsTransJournalRepository eupsTransJournalRepository;
	@Autowired
	@Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
	/**
	 * 对总账
	 */
	@Override
	public Map<String, Object> checkThdSumAcct(CheckDomain arg0, Context context)
			throws CoreException {
		logger.info("=========Start  CheckThdSumAcctAction");
		
		//流水日期时间
		String sqn =bbipPublicService.getBBIPSequence();
		Date txnDte=DateUtils.parse(DateUtils.formatAsSimpleDate(new Date()));
		Date txnTme=DateUtils.parse(DateUtils.formatAsTranstime(new Date()));
		context.setData(ParamKeys.SEQUENCE, sqn);
		context.setData(ParamKeys.TXN_DTE, txnDte);
		context.setData(ParamKeys.TXN_TME, txnTme);
		if(null == context.getData(GDParamKeys.CHECKTIME)){
				context.setData(GDParamKeys.CHECKTIME, DateUtils.formatAsHHmmss(txnTme));
		}
		
		context.setData(GDParamKeys.TOTNUM, "1");
		context.setData("PKGCNT", "000001");
		context.setData(GDParamKeys.SVRCOD, "50");
		
		//对账日期
		String chkDte=null;
		if(StringUtils.isNotEmpty(context.getData(GDParamKeys.CHECKDATE).toString())){
				chkDte=context.getData(GDParamKeys.CHECKDATE).toString();
		}else{
				chkDte=DateUtils.format(com.bocom.bbip.thd.org.apache.commons.lang.time.DateUtils.addDays(new Date(), -1),DateUtils.STYLE_yyyyMMdd);
		}
		Date acDate=DateUtils.parse(chkDte);
		context.setData(GDParamKeys.CHECKDATE, acDate);
		
		System.out.println("~~~~~~~CHECKDATE~~~~~"+chkDte);
		System.out.println("~~~~~~CHECKTIME~~~~~~"+context.getData(GDParamKeys.CHECKTIME));
		//统计交易
		EupsTransJournal eupsTransJournal=new EupsTransJournal();
		eupsTransJournal.setTxnDte(acDate);
		eupsTransJournal.setComNo(context.getData(ParamKeys.COMPANY_NO).toString());
		eupsTransJournal.setEupsBusTyp(context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
		eupsTransJournal.setTxnSts(Constants.TXNSTS_SUCCESS);
		StringBuffer bakFld2=new StringBuffer("");
		if(!context.getData(GDParamKeys.BUS_TYPE).toString().isEmpty()){
				bakFld2.append(context.getData(GDParamKeys.BUS_TYPE).toString());
		}
		if(!context.getData(ParamKeys.PAY_TYPE).toString().isEmpty()){
				bakFld2.append("&&"+context.getData(ParamKeys.PAY_TYPE).toString());
		}
		if(StringUtils.isNotEmpty(bakFld2.toString())){
				eupsTransJournal.setBakFld2(bakFld2.toString());
		}
		//得到记录
		List<EupsTransJournal> eupsTransJournalList=eupsTransJournalRepository.sumLocalAcctAmt(eupsTransJournal);
		//总笔数  总金额
		int amount=0;
		BigDecimal allmoney=new BigDecimal("0.00");	
		if(CollectionUtils.isEmpty(eupsTransJournalList)){
				context.setData(GDParamKeys.AMOUNT, amount);
				context.setData(GDParamKeys.ALL_MONEY, allmoney);
		}else{
				amount = eupsTransJournalList.get(0).getTotalCount();
				context.setData(GDParamKeys.AMOUNT, amount);
				allmoney = eupsTransJournalList.get(0).getTotalAmt();
	            context.setData(GDParamKeys.ALL_MONEY, allmoney);     
		}
		// 输入的总笔数和总金额 
		int thdAmount =Integer.valueOf(context.getData(GDParamKeys.THD_AMOUNT).toString().trim());
        
        BigDecimal thdAllMoney = new BigDecimal(context.getData(GDParamKeys.THD_ALL_MONEY).toString()).divide(new BigDecimal(100));
        //相等 设置0否则1
        if (amount == thdAmount && allmoney.compareTo(thdAllMoney) == 0) {
            context.setData(ParamKeys.TXN_STS, Constants.TXN_THD_STS_CHECKSUMACCT_EQUALS);
        } else {
            context.setData(ParamKeys.TXN_STS, Constants.TXN_THD_STS_CHECKSUMACCT_NOT_EQUALS);
        }
		//把信息保存到第三方明细表中
        eupsThdTranCtlDetailRepository.insert(BeanUtils.toObject(context.getDataMap(), EupsThdTranCtlDetail.class));
        //更改status状态
        if(context.getData(ParamKeys.TXN_STS).equals(Constants.TXN_THD_STS_CHECKSUMACCT_EQUALS)){
        		context.setData("state", "000");
        }else{
        		context.setData("status", "MMM");
        }
        //得到相差的金额
        BigDecimal totAmt = CommonUtil.decimalFormat(allmoney).subtract(CommonUtil.decimalFormat(thdAllMoney));
        context.setData("totAmt", totAmt.abs());
        //SendThirdOther
        callThd(context);
        
        return null;
	}
			
		
	/**
	 *报文信息 外发第三方
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
				
	}
}

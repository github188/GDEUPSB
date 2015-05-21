package com.bocom.bbip.gdeupsb.strategy.efek.checkThdAcct;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlDetail;
import com.bocom.bbip.eups.repository.EupsThdTranCtlDetailRepository;
import com.bocom.bbip.eups.spi.service.check.CheckThdSumAcctService;
import com.bocom.bbip.eups.spi.vo.CheckDomain;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.repository.EupsStreamNoRepository;
import com.bocom.bbip.thd.org.apache.commons.lang.StringUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * @author liyawei
 */
public class CheckThdSumAcctAction extends BaseAction implements  CheckThdSumAcctService{
	private final static Log logger=LogFactory.getLog(CheckThdSumAcctAction.class);
	@Autowired
	EupsThdTranCtlDetailRepository eupsThdTranCtlDetailRepository;
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	EupsStreamNoRepository eupsStreamNoRepository;
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
		//柜员
		context.setData("bk", bbipPublicService.getParam("BBIP", "BK"));
		String tlr = bbipPublicService.getETeller(context.getData("bk").toString());
        context.setData("tlr", tlr);
        context.setData("txnTlr", tlr);
        context.setData("chlTyp", "90");
		//流水日期时间
		 Map<String,Object> inmap=context.getData("jopSchedulingData");
	        if(null!=inmap){
	        	String dptTyp= (String)inmap.get("txnDte");
	            String clrDat= (String)inmap.get("txnDte");
	            if (null != clrDat) {
	                context.setData("txnDte",clrDat);
	            } 
	        }
        Date txnDte=null;
        if(context.getData("txnDte") == null){
        	log.info("=============new date============");
        	txnDte=DateUtils.calDate(DateUtils.parse(DateUtils.formatAsSimpleDate(new Date())),-1);
        	log.info("=============new date=============txnDte=["+txnDte+"]");
        }else{
        	log.info("=============get date=============");
        	 txnDte=DateUtils.parse((String)context.getData("txnDte"));
        	log.info("=============get date=============txnDte=["+txnDte+"]");
        }
		Date txnTme=DateUtils.parse(DateUtils.formatAsTranstime(new Date()));
		context.setData(ParamKeys.TXN_DTE, txnDte);
		context.setData(ParamKeys.TXN_TME, txnTme);
		//其他
		context.setData(GDParamKeys.TOTNUM, "1");
		context.setData(GDParamKeys.SVRCOD, "50");
		
		//对账日期
		String chkDte=DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd);
		
		context.setData(ParamKeys.RCN_DATE, txnDte);
		context.setData(GDParamKeys.CHECKDATE, chkDte);
		context.setData(GDParamKeys.CHECKTIME, DateUtils.formatAsHHmmss(txnTme));
		Map<String, Object> maps=new HashMap<String, Object>();
		maps.put("txnDte", txnDte);
		//得到记录
		List<Map<String, Object>> mapList=eupsStreamNoRepository.findMsgToChkTot(maps);
		for(Map<String, Object> map:mapList){
				String sqn=bbipPublicService.getBBIPSequence();
				context.setData(ParamKeys.COMPANY_NO, map.get("COM_NO").toString());
			
//				context.setData(ParamKeys.COMPANY_NO, "030615");
				
				context.setData(ParamKeys.SEQUENCE, sqn);
				long amount =Long.parseLong(map.get("TOT_COUNT").toString());
				BigDecimal allmoney=new BigDecimal("0.00");
				if(null != map.get("ALL_MONEY")){
					allmoney=allmoney.add(new BigDecimal(map.get("ALL_MONEY").toString()));
				}
				allmoney=allmoney.scaleByPowerOfTen(2);
				context.setData(ParamKeys.BANK_NO, map.get("THD_OBK_CDE").toString());
				context.setData(GDParamKeys.AMOUNT, amount);
				context.setData(GDParamKeys.ALL_MONEY, allmoney);  
				context.setData(GDParamKeys.BUS_TYPE, map.get("RSV_FLD4"));
				context.setData(GDParamKeys.PAY_TYPE, map.get("RSV_FLD5"));
				context.setData("PKGCNT", "000001");
			        //外发第三方 
			       callThd(context,sqn,(map.get("COM_NO").toString().substring(0,4)+"00"));
			       
			        //修改时间格式
			        String thdTxnDate=context.getData(GDParamKeys.TRADE_SEND_DATE).toString();
			        String thdTxnTime=context.getData(GDParamKeys.TRADE_SEND_TIME).toString();
			        Date thdTxnDte = DateUtils.parse(thdTxnDate,DateUtils.STYLE_yyyyMMdd);
			        Date thdTxnTme = DateUtils.parse(thdTxnDate+thdTxnTime,DateUtils.STYLE_yyyyMMddHHmmss);
			        context.setData(ParamKeys.THD_TXN_DATE, thdTxnDte);
			        context.setData(ParamKeys.THD_TXN_TIME, thdTxnTme);
			        
			        context.setData("bakFld1", map.get("RSV_FLD4"));
			        context.setData("bakFld2", map.get("RSV_FLD5"));
			        
			        EupsThdTranCtlDetail eupsThdTranCtlDetail=new EupsThdTranCtlDetail();
			        eupsThdTranCtlDetail.setBakFld1((String)map.get("RSV_FLD4"));
			        eupsThdTranCtlDetail.setBakFld2((String)map.get("RSV_FLD5"));
			        eupsThdTranCtlDetail.setComNo(map.get("COM_NO").toString());
			        eupsThdTranCtlDetail.setEupsBusTyp("ELEC00");
			        eupsThdTranCtlDetail.setTxnDte(txnDte);
			        List<EupsThdTranCtlDetail>  eupsThdTranCtlDetailList=eupsThdTranCtlDetailRepository.find(eupsThdTranCtlDetail);
			        if(CollectionUtils.isNotEmpty(eupsThdTranCtlDetailList)){
			        	eupsThdTranCtlDetailRepository.delete(eupsThdTranCtlDetailList.get(0));
			        }
			        //把信息保存到第三方明细表中
			        eupsThdTranCtlDetailRepository.insert(BeanUtils.toObject(context.getDataMap(), EupsThdTranCtlDetail.class));
		}
		logger.info("=========End  CheckThdSumAcctAction");
        return null;
	}
			
		
	/**
	 *报文信息 外发第三方
	 */
	public void callThd(Context context,String sqn ,String comNo) throws CoreException{  
		
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
				context.setData(GDParamKeys.TRADE_RECEIVE, comNo);//交易接收方
				context.setData(GDParamKeys.TRADE_SOURCE_ADD, GDConstants.TRADE_SOURCE_ADD);//交易源地址
				context.setData(GDParamKeys.TRADE_AIM_ADD, GDConstants.TRADE_AIM_ADD);//交易目标地址
				context.setData(ParamKeys.BAT_NO, context.getData(ParamKeys.SEQUENCE));
				context.setData(GDParamKeys.BUS_IDENTIFY, "YDLW17");
				context.setData(ParamKeys.THD_CUS_NO, "");
				context.setData("sqns",sqn);
					Map<String, Object> rspMap = callThdTradeManager.trade(context);
					
						if(BPState.isBPStateNormal(context)){
								if(null !=rspMap){
									 	context.setDataMap(rspMap);
						                context.setData(ParamKeys.THIRD_RETURN_MESSAGE, rspMap);
						                //第三方返回码
						                CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
						                String responseCode = rspCdeAction.getThdRspCde(rspMap, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
						                responseCode="000000";
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
						                }else if(BPState.isBPStateOvertime(context)){
						                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
						                	context.setData(GDParamKeys.MSGTYP, "E");
						                	context.setData(ParamKeys.RSP_CDE, "EFE999");
						                	context.setData(ParamKeys.RSP_MSG, "交易超时");
						                	throw new CoreException("交易超时");
						                }else{
						                	context.setData(ParamKeys.THD_TXN_STS,Constants.THD_TXNSTS_FAIL);
						                	context.setData(GDParamKeys.MSGTYP, "E");
						                	context.setData(ParamKeys.RSP_CDE, "EFE999");
						                	context.setData(ParamKeys.RSP_MSG, "交易失败");
						                	throw new CoreException(responseCode);
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
				
	}
}

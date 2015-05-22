package com.bocom.bbip.gdeupsb.strategy.efek.queryFeeOnline;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsAmountInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;
/**
 * @author liyawei
 */
public class QueryFeeResultAction implements Executable{
		private final static Log logger=LogFactory.getLog(QueryFeeResultAction.class);
		@Autowired
		EupsAmountInfoRepository eupsAmountInfoRepository;
		@Autowired
		@Qualifier("callThdTradeManager")
		ThirdPartyAdaptor callThdTradeManager;
		/**
		 * 查询结果
		 */
		@Override
		public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("===========Start QueryFeeResultAction");
			callThd(context);
			Date thdTxnDate=DateUtils.parse(context.getData(GDParamKeys.TRADE_SEND_DATE).toString(),DateUtils.STYLE_yyyyMMdd);
	        Date thdTxnTme = DateUtils.parse(context.getData(GDParamKeys.TRADE_SEND_TIME).toString(),DateUtils.STYLE_HHmmss);
	        context.setData(ParamKeys.THD_TXN_DATE, thdTxnDate);
	        context.setData(ParamKeys.THD_TXN_TIME, thdTxnTme);
	        context.setData(ParamKeys.BANK_NO, "301");
	        
		}
		/**
		 *  报文常量 外发第三方 
		 * @throws CoreException 
		 */
		public void callThd(Context context) throws CoreException {
			logger.info("=============Start  QueryFeeResultAction callThd");
				
				//报文头常量
			context.setData(GDParamKeys.TREATY_VERSION, GDConstants.TREATY_VERSION);//协议版本
			context.setData(GDParamKeys.BAG_TYPE, GDConstants.BAG_TYPE);//数据包类型
			context.setData(GDParamKeys.TRADE_START,GDConstants.TRADE_START);//交易发起方
				
				context.setData(GDParamKeys.TRADE_SEND_DATE,DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));//交易发送日期
				context.setData(GDParamKeys.TRADE_SEND_TIME, DateUtils.formatAsHHmmss(new Date()));//交易发送时间
				context.setData(GDParamKeys.TRADE_PRIORITY, GDConstants.TRADE_PRIORITY);//交易优先
				context.setData(GDParamKeys.REDUCE_SIGN, GDConstants.REDUCE_SIGN);//压缩标志
				context.setData(GDParamKeys.TRADE_RETURN_CODE, GDConstants.TRADE_RETURN_CODE);//交易返回代码

				context.setData("sqns", context.getData("sqn"));
				context.setData(GDParamKeys.NET_NAME, GDConstants.NET_NAME);//网点名称
				context.setData(GDParamKeys.SECRETKEY_INDEX, GDConstants.SECRETKEY_INDEX);//密钥索引
				context.setData(GDParamKeys.SECRETKEY_INIT, GDConstants.SECRETKEY_INIT);//密钥初始向量
				context.setData(GDParamKeys.TRADE_PERSON_IDENTIFY, "301_030000");//交易人标识
				context.setData(GDParamKeys.TRADE_RECEIVE, "030000");//交易接收方
				context.setData(GDParamKeys.TRADE_SOURCE_ADD, GDConstants.TRADE_SOURCE_ADD);//交易源地址
				context.setData(GDParamKeys.TRADE_AIM_ADD, GDConstants.TRADE_AIM_ADD);//交易目标地址
				
				context.setData(GDParamKeys.BUS_IDENTIFY, "YDLW04");		
                String responseCode =null;
							Map<String, Object> rspMap = callThdTradeManager.trade(context);
								if(BPState.isBPStateNormal(context)){
										if(null !=rspMap){
											 	context.setDataMap(rspMap);
								                context.setData(ParamKeys.THIRD_RETURN_MESSAGE, rspMap);
								                //第三方返回码
								                CommThdRspCdeAction rspCdeAction = new CommThdRspCdeAction();
								                responseCode = rspCdeAction.getThdRspCde(rspMap, context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
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
									                List<Map<String, Object>> list=(List<Map<String, Object>>)rspMap.get("Information");
									               //页数
									                pageGet(context, list);
									                //返回list
									                creatList(list,context);
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
		
		/**
		 *页数记录数得到 
		 */
		public void pageGet(Context context,List<Map<String, Object>> list){
			logger.info("==========Start  QueryFeeResultAction  pageGet");
			int pageNow=1;
			if(context.getData("pageNum")!=null){
					pageNow=Integer.parseInt(context.getData("pageNum").toString());
			}
			int pageSize=10;
			if(context.getData("pageSize")!=null){
					Integer.parseInt(context.getData("pageSize").toString());
			}
			int allSize=list.size();
			//总页数
			int pageAll=0;
			if((allSize%pageSize) ==0){
					pageAll=allSize/pageSize;
			}else{
					pageAll=allSize/pageSize+1;
			}
			
			//每页显示的内容
			int pageShowFirst=(pageNow-1)*pageSize;
			int pageShowLast=pageNow*pageSize-1;
			if(pageAll*pageSize>list.size()){
					pageShowLast=list.size()-((pageAll-1)*pageSize);
			}
			List<Map<String, Object>> listMap=new ArrayList<Map<String,Object>>();
			for(int i=pageShowFirst;i<pageShowLast;i++){
					Map<String, Object> maps=list.get(i);
					listMap.add(maps);
			}
			context.setData("Information", listMap);
            context.setData(ParamKeys.TOTAL_PAGES,pageAll);
            context.setData(ParamKeys.TOTAL_ELEMETS, allSize);
            
            logger.info("==========End  QueryFeeResultAction  pageGet");
		}
/**
 * 生成返回List
 */
		public void creatList(List<Map<String, Object>> list,Context context){
			logger.info("===========Start QueryFeeResultAction creatList");
            context.setData(ParamKeys.CUS_AC, list.get(0).get(ParamKeys.CUS_AC));
            //欠费总金额   总违约金  总本金 单位编码
            BigDecimal oweFeeAmt=new BigDecimal("0.00");
            BigDecimal pbd=new BigDecimal("0.00");
            BigDecimal capitial=new BigDecimal("0.00");
            List<Map<String, Object>> InformationList=new ArrayList<Map<String,Object>>();
            
            List<Map<String, Object>> shpList=new ArrayList<Map<String,Object>>();
            for (Map<String, Object> map : list) {
            		BigDecimal amtAdd=new BigDecimal(map.get(ParamKeys.OWE_FEE_AMT).toString()).scaleByPowerOfTen(-2);
            		BigDecimal detitAdd=new BigDecimal("0.00");
            		if(StringUtils.isNotEmpty((String)map.get(GDParamKeys.DEDIT))){
            				detitAdd=new BigDecimal(map.get(GDParamKeys.DEDIT).toString()).scaleByPowerOfTen(-2);
	                		pbd=pbd.add(detitAdd);
            		}
            		BigDecimal capitialAdd=new BigDecimal("0.00");
            		if(StringUtils.isNotEmpty((String)map.get(GDParamKeys.CAPITIAL))){
            				capitialAdd=new BigDecimal(map.get(GDParamKeys.CAPITIAL).toString()).scaleByPowerOfTen(-2);
	                		capitial=capitial.add(capitialAdd);
            		}
            		//行的list赋值 
            		Map<String, Object> mapInformation=new HashMap<String, Object>();
            		
            		Map<String, Object> mapList=new HashMap<String, Object>();
            		mapInformation.put("payType", map.get("payType"));
            		mapInformation.put("comNo", map.get("comNo"));
            		mapInformation.put("payNo", map.get("payNo"));
            		mapInformation.put("thdCusNme", map.get("thdCusNme"));
            		mapInformation.put("bankNo", map.get("bankNo"));
            		mapInformation.put("fulDedFlg", map.get("fulDedFlg"));
            		mapInformation.put("accountsSerialNo", map.get("accountsSerialNo"));
            		mapInformation.put("electricityYearMonth", map.get("electricityYearMonth"));
            		//金额数值转换
            		mapInformation.put("oweFeeAmt", amtAdd);
            		mapInformation.put("capital",capitialAdd );
            		mapInformation.put("dedit",detitAdd );
            		
            		mapList.put("payType", map.get("payType"));
            		mapList.put("comNo", map.get("comNo"));
            		mapList.put("payNo", map.get("payNo"));
            		mapList.put("bankNo", map.get("bankNo"));
            		mapList.put("fulDedFlg", map.get("fulDedFlg"));
            		mapList.put("accountsSerialNo", map.get("accountsSerialNo"));
            		mapList.put("electricityYearMonth", map.get("electricityYearMonth"));
            		//金额数值转换
            		mapList.put("oweFeeAmt", amtAdd);
            		mapList.put("capital",capitialAdd );
            		mapList.put("dedit",detitAdd );
            		
            		
            		InformationList.add(mapInformation);
            		shpList.add(mapList);
            		oweFeeAmt=oweFeeAmt.add(amtAdd);
			}
            context.setData("Information", InformationList);
            context.setData("InformationList", shpList);
            context.setData("PKGCNT", context.getData("PKGCNT"));
            
            //欠费总金额 总违约金  总本金 单位编码
            context.setData(ParamKeys.OWE_FEE_AMT, oweFeeAmt);
            context.setData("pbd",pbd );
            context.setData(GDParamKeys.DEDIT, pbd);
            context.setData("capital",capitial );
            context.setData(ParamKeys.BAK_FLD1,  list.get(0).get(ParamKeys.BANK_NO));
            context.setData(ParamKeys.RSV_FLD2, list.get(0).get(GDParamKeys.ACCOUNTS_SERIAL_NO));
            context.setData(ParamKeys.RSV_FLD3, list.get(0).get(ParamKeys.FULL_DED_FLAG));
	       
            context.setData(ParamKeys.RSV_FLD4, list.get(0).get(GDParamKeys.BUS_TYPE));
	        context.setData(ParamKeys.RSV_FLD5, list.get(0).get(GDParamKeys.PAY_TYPE));
	        context.setData(ParamKeys.RSV_FLD6, list.get(0).get(ParamKeys.RSV_FLD6));
	        
	        context.setData(ParamKeys.THD_CUS_NME,  list.get(0).get(ParamKeys.THD_CUS_NME));
			logger.info("===========End  QueryFeeResultAction creatList");
		}
}

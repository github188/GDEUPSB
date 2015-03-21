package com.bocom.bbip.gdeupsb.action.gas;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 惠州燃气用户协议维护
 * 
 * @author WangMQ
 * 
 */
public class OprGasCusAgentAction extends BaseAction {
	public static Logger logger = LoggerFactory
			.getLogger(OprGasCusAgentAction.class);

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("Enter in OprGasCusAgentAction!...........");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

		// logger.info("=================traceNo:" +
		// context.getData(ParamKeys.TRACE_NO));
		// String traceNo = get(BBIPPublicService.class).getTraceNo();
		// context.setData(ParamKeys.TRACE_NO, traceNo);
		// context.setTraceNo(traceNo);
		// logger.info("=================context after set traceNo:" + context);
		
		
//		logger.info("=====================context:"+context);
//		String subTraceNo = "2004";
//		String traceNo = context.getData(ParamKeys.TRACE_NO);
//		
//		traceNo = subTraceNo + traceNo.substring(4);
//		logger.info("==============traceNo:" + traceNo);
//		context.setTraceNo(traceNo);
		
		context.setData(GDParamKeys.GAS_BK, "CNJT");
		context.setData(ParamKeys.CUS_NO, context.getData("thdCusNo"));
		logger.info("================now context =" + context);

		logger.info("=======功能选择(1-新增 2-修改 3-删除 4-查询):"
				+ context.getData("optFlg"));

		if ("4".equals(context.getData("optFlg"))) { // 4:查询交易
			// select * from Gascusall491 where UserNo='%s' or idno='%s'
			// GdGasCusAll qryCusInf = new GdGasCusAll();
			// qryCusInf.setIdNo(context.getData(ParamKeys.ID_NO).toString()
			// .trim());
			// qryCusInf.setCusNo(context.getData(ParamKeys.THD_CUS_NO).toString()
			// .trim());
			// List<GdGasCusAll> infList =
			// get(GdGasCusAllRepository.class).find(
			// qryCusInf);
			// if (CollectionUtils.isEmpty(infList)) {
			// context.setData(GDParamKeys.GAS_MSG_TYP, "E");
			// throw new CoreRuntimeException("该用户未签约,不可进行查询交易");
			// }
			//
			Map<String, Object> cusListMap = new HashMap<String, Object>();
			cusListMap.put(ParamKeys.CUS_AC, context.getData(ParamKeys.CUS_AC));
			Result accessObjList = get(BGSPServiceAccessObject.class)
					.callServiceFlatting("queryListAgentCollectAgreement",
							cusListMap);
			if (CollectionUtils.isEmpty((Collection<?>) accessObjList
					.getPayload())) {
				throw new CoreException(GDErrorCodes.GAS_QRY_AGT_ERR_EMT);
			}
			Map<String, Object> cusDtlMap = new HashMap<String, Object>();
			cusDtlMap.put("agdAgrNo", accessObjList.getPayload()
					.get("agdAgrNo"));
			Result qryCusDetail = get(BGSPServiceAccessObject.class)
					.callServiceFlatting("queryDetailAgentCollectAgreement",
							cusDtlMap);

			if (CollectionUtils.isEmpty((Collection<?>) qryCusDetail
					.getPayload())) {
				throw new CoreException(GDErrorCodes.GAS_QRY_AGT_ERR_EMT);
			}

			context.setDataMap(qryCusDetail.getPayload());

			logger.info("用户协议查询完成");
		}

		// 前端授权，此处只检查
		// 非查询交易要授权
		else {
			String authTlr = context.getData(ParamKeys.AUTHOR_LEVEL);
			if (StringUtils.isEmpty(authTlr)) {
				// throw new CoreException("未授权，不能进行交易");
				throw new CoreException(ErrorCodes.EUPS_CANCEL_CHECK_AUTH_FAIL);
			}
		}
		logger.info("已授权，可进行交易");

		// <Set>TActNo=491800012620190029499</Set>
		// <Set>@MSG.OIP=$HstIp</Set> <!--第三方IP-->
		// <Set>@MSG.OPT=$HstPrt</Set> <!--第三方PORT-->
		context.setData("TActNo", GDConstants.GAS_THD_ACT_NO);
		// context.setData("@MSG.OIP", context.getData("hstIp"));
		// context.setData("@MSG.OPT", context.getData("HstPrt"));

		context.setData("tCommd", "QryUser");
		context.setData("TransCode", "GASHXX");

		context.setData("txnCod", "GASHXX");
		context.setData("objSvr", "STDHGAS1");
		// 发燃气第三方，查看有无该用户编号信息
		logger.info("=============context=======" + context);

		logger.info("发燃气第三方，查看有无该用户编号信息");
		Map<String, Object> thdRspMsg = get(ThirdPartyAdaptor.class).trade(
				context);
		context.setDataMap(thdRspMsg);
		logger.info("======================context========" + context);
		// context.setData("TransCode", null);
		// logger.info(""+context.getData("TransCode"));

		// 处理燃气返回的信息
		logger.info("==============rtnCod=" + context.getData("rtnCod")
				+ "============optFlg=" + context.getData("optFlg"));
		if ("QryUser".equals(context.getData("rtnCod").toString().trim())) {
			if ("1".equals(context.getData("optFlg"))) { // optFlg = 1
				// 燃气返回QryUser,
				// 燃气存在该用户编号？
				// List<GdGasCusAll> qryCusInf = qryCusInfoList(context);
				// if(CollectionUtils.isNotEmpty(qryCusInf)){
				// throw new CoreRuntimeException("该用户编号已经签约,无法新增签约!");
				// }
				// 调代收付接口查协议
				Map<String, Object> cusListMap = new HashMap<String, Object>();
				cusListMap.put(ParamKeys.CUS_AC,
						context.getData(ParamKeys.CUS_AC));
				Result accessObjList = get(BGSPServiceAccessObject.class)
						.callServiceFlatting("queryListAgentCollectAgreement",
								cusListMap);
				if (CollectionUtils.isEmpty((Collection<?>) accessObjList
						.getPayload())) {
					logger.info("================代收付无协议，可新增");
					context.setData("tCommd", "Add");
				} else {
					
					Map<String, Object> cusDtlMap = new HashMap<String, Object>();
					cusDtlMap.put("agdAgrNo",
							accessObjList.getPayload().get("agdAgrNo"));
					Result qryCusDetail = get(BGSPServiceAccessObject.class)
							.callServiceFlatting(
									"queryDetailAgentCollectAgreement",
									cusDtlMap);
					if (CollectionUtils.isNotEmpty((Collection<?>) qryCusDetail
							.getPayload())) {
						context.setDataMap(qryCusDetail
								.getPayload());
						logger.info("===========已存在协议，返回协议详细信息");
						throw new CoreException("该用户已签约,不可进行新增签约交易");
					}
				}
				
			}

			if ("2".equals(context.getData("optFlg"))) { // optFlg = 2
															// 燃气返回QryUser,
															// 说明燃气存在该用户编号，可修改
				// List<GdGasCusAll> qryCusInf = qryCusInfoList(context);
				// if(CollectionUtils.isEmpty(qryCusInf)){
				// context.setData(ParamKeys.RSP_CDE,
				// GDConstants.GAS_ERROR_CODE);
				// context.setData(ParamKeys.RSP_MSG, "该用户编号未签约,无法修改!");
				// throw new CoreRuntimeException("该用户编号未签约,无法修改!");
				// }
				Map<String, Object> cusListMap = new HashMap<String, Object>();
				cusListMap.put(ParamKeys.CUS_AC,
						context.getData(ParamKeys.CUS_AC));
				Result accessObjList = get(BGSPServiceAccessObject.class)
						.callServiceFlatting("queryListAgentCollectAgreement",
								cusListMap);
				if (CollectionUtils.isEmpty((Collection<?>) accessObjList
						.getPayload())) {
					throw new CoreException(GDErrorCodes.GAS_QRY_AGT_ERR_EMT);//未签约，不可进行更改签约交易
				}
//				Map<String, Object> cusDtlMap = new HashMap<String, Object>();
//				cusDtlMap.put("agdAgrNo",
//						accessObjList.getPayload().get("agdAgrNo"));
//				Result qryCusDetail = get(BGSPServiceAccessObject.class)
//						.callServiceFlatting(
//								"queryDetailAgentCollectAgreement", cusDtlMap);
//				if (CollectionUtils.isEmpty((Collection<?>) qryCusDetail
//						.getPayload())) {
//					throw new CoreException("该用户未签约,不可进行更改签约交易");
//				}
				context.setData("tCommd", "Edit");
				logger.info("============可以对签约协议进行修改");
			}

			if ("3".equals(context.getData("optFlg"))) { // optFlg = 3
															// 燃气返回QryUser,
															// 说明燃气存在该用户编号，可删除
				// List<GdGasCusAll> qryCusInf = qryCusInfoList(context);
				// if(CollectionUtils.isEmpty(qryCusInf)){
				// context.setData(ParamKeys.RSP_CDE,
				// GDConstants.GAS_ERROR_CODE);
				// context.setData(ParamKeys.RSP_MSG, "该用户编号未签约,无法删除!");
				// throw new CoreRuntimeException("该用户编号未签约,无法删除!");
				// }

				Map<String, Object> cusListMap = new HashMap<String, Object>();
				cusListMap.put(ParamKeys.CUS_AC,
						context.getData(ParamKeys.CUS_AC));
				Result accessObjList = get(BGSPServiceAccessObject.class)
						.callServiceFlatting("queryListAgentCollectAgreement",
								cusListMap);
				if (CollectionUtils.isEmpty((Collection<?>) accessObjList
						.getPayload())) {
					throw new CoreException(GDErrorCodes.GAS_QRY_AGT_ERR_EMT);
				} else {
//					Map<String, Object> cusDtlMap = new HashMap<String, Object>();
//					cusDtlMap.put("agdAgrNo",
//							accessObjList.getPayload().get("agdAgrNo"));
//					Result qryCusDetail = get(BGSPServiceAccessObject.class)
//							.callServiceFlatting(
//									"queryDetailAgentCollectAgreement",
//									cusDtlMap);
//					if (CollectionUtils.isNotEmpty((Collection<?>) qryCusDetail
//							.getPayload())) {
//						context.setDataMap(qryCusDetail
//								.getPayload());
//					}
					
					//context存入agdAgrNo，等待删除
					context.setData(ParamKeys.AGD_AGR_NO, accessObjList.getPayload().get("agdAgrNo"));
					
					context.setData("tCommd", "Stop");
					logger.info("===========可以删除");
				}
			}
			
			context.setData(ParamKeys.BUS_TYP, "A087");
			
			logger.info("=======非查询交易，增删改=====context:" + context);
			
			if (!"4".equals(context.getData("optFlg"))) { // 非查询交易   1-增   2-改   3-删
				if ("1".equals(context.getData("optFlg"))) {
					context.setData(ParamKeys.OPERATION_TPYE, "0");  //oprTyp=0：增
					get(BBIPPublicService.class).synExecute(
							"eups.commInsertCusAgent", context);
				}
				if ("2".equals(context.getData("optFlg"))) {
					context.setData(ParamKeys.OPERATION_TPYE, "1");	 ////oprTyp=1: 改
					get(BBIPPublicService.class).synExecute(
							"eups.commUpdateCusAgent", context);
				}
				if ("3".equals(context.getData("optFlg"))) {
					get(BBIPPublicService.class).synExecute(
							"eups.commDelCusAgent", context);
				}
			}
		} else if ("NOUser".equals(context.getData("rtnCod").toString().trim())) { // NOUser
																					// 为无此用户（用户编码错误）
																					// context.setData("msgTyp",
																					// "E");
			// context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
			// context.setData("rspMsg", "无此用户或用户编码错误!");
			throw new CoreRuntimeException("无此用户或用户编码错误!");
		} else if ("UserStop".equals(context.getData("rtnCod").toString()
				.trim())) { // UserStop为此用户已报停
		// context.setData("msgTyp", "E");
		// context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
		// context.setData("rspMsg", "此用户已报停!");
			throw new CoreRuntimeException("此用户已报停!");
		} else {
//			context.setData("msgTyp", "E");
//			context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
//			context.setData("rspMsg", "系统错误!");
			throw new CoreRuntimeException("GDEUPSBBP9001");
		}

		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);

	}

	/**
	 * 查燃气协议表信息
	 * 
	 * @param context
	 * @return
	 */
	// private List<GdGasCusAll> qryCusInfoList(Context context) {
	// GdGasCusAll qryCusInf = new GdGasCusAll();
	// // qryCusInf.setIdNo(context.getData(ParamKeys.ID_NO).toString().trim());
	// qryCusInf.setCusNo(context.getData(ParamKeys.THD_CUS_NO).toString()
	// .trim());
	// List<GdGasCusAll> infList = get(GdGasCusAllRepository.class).find(
	// qryCusInf);
	// return infList;
	// }

	/**
	 * 查代收付协议是否已签约
	 * 
	 * @param context
	 * @return
	 */
	// public Result cusIsExist(Context context){
	// Map<String, Object> cusListMap = new HashMap<String, Object>();
	// cusListMap.put(ParamKeys.CUS_AC, context.getData(ParamKeys.CUS_AC));
	// Result accessObjList =
	// get(BGSPServiceAccessObject.class).callServiceFlatting("queryListAgentCollectAgreement",
	// cusListMap);
	// if(CollectionUtils.isEmpty(accessObjList.getPayload())){
	// context.setData(GDParamKeys.GAS_MSG_TYP, "E");
	// throw new CoreRuntimeException("该用户未签约,不可进行交易");
	// }
	// String agdAgrNo = (String) accessObjList.getPayload().get("agdAgrNo");
	// Map<String, Object> cusInfoMap = new HashMap<String, Object>();
	// cusInfoMap.put("agdAgrNo", agdAgrNo);
	// Result accessObject =
	// get(BGSPServiceAccessObject.class).callServiceFlatting("queryDetailAgentCollectAgreement",
	// cusInfoMap);
	// return accessObject;
	// }

}

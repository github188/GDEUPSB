package com.bocom.bbip.gdeupsb.action.gas;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdGasCusAll;
import com.bocom.bbip.gdeupsb.entity.GdGasCusDay;
import com.bocom.bbip.gdeupsb.repository.GdGasCusAllRepository;
import com.bocom.bbip.gdeupsb.repository.GdGasCusDayRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
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
public class OprGasCusAgentActionV4 extends BaseAction {
	public static Logger logger = LoggerFactory
			.getLogger(OprGasCusAgentActionV4.class);

	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;

	@Autowired
	BBIPPublicService bbipPublicService;

	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("Enter in OprGasCusAgentActionV4!...........");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

//		context.setData("thdthdCusNo", context.getData("thdCusNo"));

		// 备份，修改时用 TODO 修改做不了
		String cusTypBak = context.getData("cusTyp");
		context.setData("cusAcBak", context.getData(ParamKeys.CUS_AC));
		boolean cmuTelSts = false;
		if (StringUtils
				.isNoneBlank((String) context.getData(ParamKeys.CMU_TEL))) {
			context.setData("cmuTelBak", context.getData(ParamKeys.CMU_TEL));
			cmuTelSts = true;
		}

		context.setData(GDParamKeys.GAS_BK, "CNJT");
		context.setData(ParamKeys.CUS_NO, context.getData(ParamKeys.THD_CUS_NO));
		context.setData("agtSrvCusId", context.getData(ParamKeys.THD_CUS_NO));

		context.setData("ccy", "CNY");
		logger.info("================now context =" + context);
		logger.info("=======功能选择(1-新增 2-修改 3-删除 4-查询):"
				+ context.getData("optFlg"));

		if ("4".equals(context.getData("optFlg"))) { // 4:查询交易

			GdGasCusAll qryCusAll = new GdGasCusAll();
			qryCusAll.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			qryCusAll.setCusNo((String) context.getData(ParamKeys.THD_CUS_NO));
			List<Map<String, Object>> infoList = get(
					GdGasCusAllRepository.class).findCusInfo(qryCusAll);

			if (CollectionUtils.isEmpty(infoList)) {
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}

			context.setData("cusNo", infoList.get(0).get("CUSNO"));
			context.setData("cusAc", infoList.get(0).get("CUSAC"));
			if (StringUtils.isNotBlank((String) infoList.get(0).get("CUSNME"))) {
				context.setData("cusNme", infoList.get(0).get("CUSNME"));
			}
//			context.setData("cusTyp", infoList.get(0).get("CUSTYP"));
			context.setData("optDat", infoList.get(0).get("OPTDAT"));
			context.setData("optNod", infoList.get(0).get("OPTNOD"));
			context.setData("idTyp", infoList.get(0).get("IDTYP"));
			context.setData("idNo", infoList.get(0).get("IDNO"));
			context.setData("thdCusNme", infoList.get(0).get("THDCUSNME"));
			context.setData("cmuTel", infoList.get(0).get("CMUTEL"));
			context.setData("thdCusAdr", infoList.get(0).get("THDCUSADR"));
			context.setData("cusTyp", cusTypBak);
			logger.info("========context after qry cus info:" + context);
			
			logger.info("========PGAS00 用户协议查询完成=======");
		} else {

			String cusTyp = context.getData("cusTyp");
			if ("0".equals(cusTyp)) { // 0 对公账户 2存折 4对私卡
				context.setData("cusTyp", "1");
				context.setData("bvCde", null);
			}
			if ("2".equals(cusTyp)) { // 0 对公账户 2存折 4对私卡
				context.setData("cusTyp", "0");
				context.setData("bvCde", "704");
			}
			if ("4".equals(cusTyp)) { // 0 对公账户 2存折 4对私卡
				context.setData("cusTyp", "0");
				context.setData("bvCde", "009");
			}



			// 前端授权，此处只检查
			// 非查询交易要授权
			// String authTlr = context.getData(ParamKeys.AUTHOR_LEVEL);
			// if (StringUtils.isEmpty(authTlr)) {
			// throw new CoreException(ErrorCodes.EUPS_CANCEL_CHECK_AUTH_FAIL);
			// }
			//
			// logger.info("已授权，可进行交易");

			// <Set>TActNo=491800012620190029499</Set>
			context.setData("TActNo", GDConstants.GAS_THD_ACT_NO);
			context.setData("TransCode", "QryUser");

			// 发燃气第三方，查看有无该用户编号信息
			logger.info("=============context=======" + context);

			logger.info("发燃气第三方，查看有无该用户编号信息");
			Map<String, Object> thdRspMsg = callThdTradeManager.trade(
					context);
			context.setDataMap(thdRspMsg);
			logger.info("======================context after callThd qryUser========"
					+ context);

			if ("QryUser"
					.equals(context.getData("TransCode").toString().trim())) {// 燃气返回QryUser,说明燃气存在该用户编号，银行可新增，可修改,可删除

				if ("1".equals(context.getData("optFlg"))) { // 新增
					setAgtCltAndCusInf(context);

					// 上代收付查询协议，先根据cusAc进行列表查询得到协议编号，再用协议编号查询明细（用户信息）
					Map<String, Object> cusListMap = setAcpMap(context);
					cusListMap.put(ParamKeys.CUS_AC,
							context.getData(ParamKeys.CUS_AC));
					context.setDataMap(cusListMap);

					Result accessObjList = bgspServiceAccessObject
							.callServiceFlatting(
									"queryListAgentCollectAgreement",
									cusListMap);
					logger.info("=========after optFlg=1 accessObjList：【accessObjList.getResponseCode():"
							+ accessObjList.getResponseCode()
							+ "】【accessObjList.getResponseMessage():"
							+ accessObjList.getResponseMessage()
							+ "】【accessObjList.getResponseType()："
							+ accessObjList.getResponseType() + "】");
					if ("N".equals(accessObjList.getResponseType())) {
						throw new CoreRuntimeException(
								GDErrorCodes.GAS_QRY_AGT_ERR_EXIST);
					}
					logger.info("============未签约，可签约");
					context.setData("tCommd", "Add");
					context.setData("TransCode", "Add");
					context.setData("agrChl", "01");
					context.setData(ParamKeys.OPERATION_TPYE, "0"); // oprTyp=0:增

					setAgtCltAndCusInf(context);

					// get(BBIPPublicService.class).synExecute(
					// "eups.commInsertCusAgent", context);

					// 不使用代收付签模板，直接调用接口
					
					Result insertCusAgtResult = bgspServiceAccessObject
							.callServiceFlatting(
									"maintainAgentCollectAgreement",
									context.getDataMap());
					if (!"N".equals(insertCusAgtResult.getResponseType())) {
						throw new CoreRuntimeException(
								insertCusAgtResult.getResponseMessage());
					}
					logger.info("=============代收付签约成功，发THD签约===========");
					callThdAndOprateLclCusAgt(context);
					context.setData("cusTyp", cusTypBak);
				}
				if ("2".equals(context.getData("optFlg"))) { // 修改
					setAgtCltAndCusInf(context);

					// 上代收付查询协议，先根据cusAc进行列表查询
					Map<String, Object> cusListMap = setAcpMap(context);
					cusListMap.put(ParamKeys.CUS_AC,
							context.getData(ParamKeys.CUS_AC));
					context.setDataMap(cusListMap);

					Result accessObjList = bgspServiceAccessObject
							.callServiceFlatting(
									"queryListAgentCollectAgreement",
									cusListMap);
					logger.info("=========after optFlg=2 accessObjList：【accessObjList.getResponseCode():"
							+ accessObjList.getResponseCode()
							+ "】【accessObjList.getResponseMessage():"
							+ accessObjList.getResponseMessage()
							+ "】【accessObjList.getResponseType()："
							+ accessObjList.getResponseType() + "】");
					if (!("N".equals(accessObjList.getResponseType()))) {
						context.setData(ParamKeys.RESPONSE_MESSAGE,
								accessObjList.getResponseMessage());
						throw new CoreException(
								accessObjList.getResponseMessage());
					} else {
						logger.info("======================context after qryCusAgtList:"
								+ context);
						context.setDataMap(accessObjList.getPayload());
						logger.info("======================context after qryCusAgtList & setDataMap:"
								+ context);

						List<Map<String, Object>> customerInfoMaps = new ArrayList<Map<String, Object>>();
						Map<String, Object> cusInfoMap = setCustomerInfoMap(context);
						cusInfoMap.put(ParamKeys.CUS_AC,
								context.getData("cusAcBak"));
						if (cmuTelSts == true) {
							cusInfoMap.put(ParamKeys.CMU_TEL,
									context.getData("cmuTelBak"));
						}
						context.setData("agtCllCusId", customerInfoMaps.get(0)
								.get("agtCllCusId"));

						context.setData("customerInfo", customerInfoMaps);
						logger.info("============ context after set customerInfo : "
								+ context);

						@SuppressWarnings("unchecked")
						List<Map<String, Object>> agentCollectAgreementMaps = (List<Map<String, Object>>) context
								.getData("agentCollectAgreement");
						agentCollectAgreementMaps.get(0).put(
								"agrVldDte",
								DateUtils.format(new Date(),
										DateUtils.STYLE_yyyyMMdd));
						agentCollectAgreementMaps.get(0).put("agrExpDte",
								"99991231");
						agentCollectAgreementMaps.get(0).put(ParamKeys.CUS_AC,
								context.getData("cusAcBak"));
						if (cmuTelSts == true) {
							agentCollectAgreementMaps.get(0).put(
									ParamKeys.CMU_TEL,
									context.getData("cmuTelBak"));
						}
						context.setData("agentCollectAgreement",
								agentCollectAgreementMaps);
						logger.info("============ context after set agentCollectAgreement : "
								+ context);
						context.setData("tCommd", "Edit");
						context.setData("TransCode", "Edit");
						logger.info("============有协议，可修改");
						context.setData("agrChl", "01");
						context.setData(ParamKeys.OPERATION_TPYE, "1"); // //oprTyp=1:改

						// get(BBIPPublicService.class).synExecute(
						// "eups.commUpdateCusAgent", context);

						Result editCusAgtResult = bgspServiceAccessObject
								.callServiceFlatting(
										"maintainAgentCollectAgreement",
										context.getDataMap());
						if (!"N".equals(editCusAgtResult.getResponseType())) {
							throw new CoreRuntimeException(
									editCusAgtResult.getResponseMessage());
						}
						logger.info("=============代收付修改签约成功，发THD修改签约===========");
						callThdUpdateOprateLclCusAgt(context);
						context.setData("cusTyp", cusTypBak);

					}
				}

				if ("3".equals(context.getData("optFlg"))) { // 删除
					setAgtCltAndCusInf(context);

					// // 上代收付查询协议，先根据cusAc进行列表查询得到协议编号，再用协议编号查询明细（用户信息）
					Map<String, Object> cusListMap = setAcpMap(context);
					cusListMap.put(ParamKeys.CUS_AC,
							context.getData(ParamKeys.CUS_AC));
					context.setDataMap(cusListMap);

					Result accessObjList = bgspServiceAccessObject
							.callServiceFlatting(
									"queryListAgentCollectAgreement",
									cusListMap);
					logger.info("========after optFlg=3 queryListAgentCollectAgreement ======="
							+ context);
					logger.info("=========after optFlg=3 accessObjList：【accessObjList.getResponseCode():"
							+ accessObjList.getResponseCode()
							+ "】【accessObjList.getResponseMessage():"
							+ accessObjList.getResponseMessage()
							+ "】【accessObjList.getResponseType()："
							+ accessObjList.getResponseType() + "】");
					if (!("N".equals(accessObjList.getResponseType()))) {
						throw new CoreException(accessObjList.getResponseMessage());
					} else {
						logger.info("======================context after qryCusAgtList:"
								+ context);
						context.setDataMap(accessObjList.getPayload());
						logger.info("======================context after qryCusAgtList & setDataMap:"
								+ context);

						@SuppressWarnings("unchecked")
						List<Map<String, Object>> agentCollectAgreementMaps = (List<Map<String, Object>>) context
								.getData("agentCollectAgreement");
						//返回的协议信息List可能包含多条信息，剔除与目标删除用户无关的协议信息
						for(int i=0; i<agentCollectAgreementMaps.size(); i++){
							if(!(context.getData(ParamKeys.THD_CUS_NME).equals(agentCollectAgreementMaps.get(i).get("agtSrvCusPnm")))){
								agentCollectAgreementMaps.remove(i);
							}
						}
						logger.info("==================");
						context.setData("agentCollectAgreement", agentCollectAgreementMaps);
						
						List<String> agdAgrNoList =  new ArrayList<String>();
						agdAgrNoList.add((String) agentCollectAgreementMaps.get(0).get("agdAgrNo"));
						context.setData("agdAgrNo", agdAgrNoList);

						logger.info("==============agdAgrNo in context :"
								+ context.getData("agdAgrNo"));
						
						
						logger.info("============ context after set agdAgrNo : "
								+ context);

						context.setData("tCommd", "Stop");
						context.setData("TransCode", "Stop");
						logger.info("===========有协议，可删除");

//						 get(BBIPPublicService.class).synExecute(
//						 "eups.commDelCusAgent", context);

						Result stopCusAgtResult 
						= bgspServiceAccessObject.callServiceFlatting("deleteAgentCollectAgreement", context.getDataMap());
						if (!"N".equals(stopCusAgtResult.getResponseType())) {
							throw new CoreRuntimeException(
									stopCusAgtResult.getResponseMessage());
						}
						
						callThdStopOprateLclCusAgt(context);
						
						context.setData("cusTyp", cusTypBak);
					}
				}
			} else if ("NOUser".equals(context.getData("TransCode").toString()
					.trim())) {
				throw new CoreRuntimeException(GDErrorCodes.GAS_QRY_AGT_ERR_NO);
			} else if ("UserStop".equals(context.getData("TransCode")
					.toString().trim())) { // UserStop为此用户已报停
				throw new CoreRuntimeException(
						GDErrorCodes.GAS_QRY_AGT_ERR_STOP);
			} else {
				throw new CoreRuntimeException(GDErrorCodes.GAS_SYS_ERROR);
			}
		}

		context.setData("cusTyp", cusTypBak);
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}

	private void callThdStopOprateLclCusAgt(Context context)
			throws CoreException {
		logger.info("callThdStopOprateLclCusAgt start ... ...");

		String date = DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE);
		context.setData("TransCode", "Stop");
		context.setData("regDat", date);
		context.setData("txnCod", "GASHXY");
		context.setData("objSvr", "STDHGAS1");

		context.setProcessId("gdeupsb.oprGasCusAgent1");

		Map<String, Object> tradeMap1 = callThdTradeManager.trade(context);
		context.setDataMap(tradeMap1);

		context.setData("optDat", date);
		context.setData("optNod", context.getData("NodNo"));
		logger.info("===oprGasCusAgentAction1=====context=" + context);

		context.setProcessId("gdeupsb.oprGasCusAgentAction");
		logger.info("===oprGasCusAgentAction=====context=" + context);

		if ("StopOK".equals(context.getData("TransCode").toString().trim())) {
			// 动态协议表
			GdGasCusDay insCusInfo = new GdGasCusDay();
			insCusInfo.setSequence(get(BBIPPublicService.class)
					.getBBIPSequence());
			insCusInfo.setCusNo((String) context.getData(ParamKeys.CUS_NO));
			;
			insCusInfo.settCommd((String) context.getData("tCommd"));
			insCusInfo.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			if (StringUtils.isNoneBlank((String) context
					.getData(ParamKeys.CUS_NME))) {
				insCusInfo.setCusNme((String) context
						.getData(ParamKeys.CUS_NME));
			}
			insCusInfo.setOptDat(date);
			insCusInfo.setAccTyp((String) context.getData("cusTyp"));
			insCusInfo.setOptNod((String) context.getData(ParamKeys.OBK_BR));
			insCusInfo.setIdTyp((String) context.getData(ParamKeys.ID_TYPE));
			insCusInfo.setIdNo((String) context.getData(ParamKeys.ID_NO));
			insCusInfo.setThdCusNam((String) context
					.getData(ParamKeys.THD_CUS_NME));
			insCusInfo.setCmuTel((String) context.getData(ParamKeys.CMU_TEL));
			insCusInfo.setThdCusAdr((String) context
					.getData(ParamKeys.THD_CUSTOMER_ADDR));
			get(GdGasCusDayRepository.class).insert(insCusInfo);

			// 燃气协议表
			GdGasCusAll delGasCusAll = new GdGasCusAll();
			delGasCusAll.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			delGasCusAll.setCusNo((String) context.getData(ParamKeys.CUS_NO));
			get(GdGasCusAllRepository.class).delete(delGasCusAll);

			// context.setData("msgTyp", "N");
			// context.setData("rspCod", GDConstants.GDEUPSB_TXN_SUCC_CODE);
			// context.setData("rspMsg", "新增成功");

			logger.info("============del本地协议，新增动态协议成功");
		} else {
			throw new CoreException(GDErrorCodes.GAS_CUS_AGT_STOPNO);
		}
		logger.info("callThdStopOprateLclCusAgt end ... ...");
 
	}

	private void callThdUpdateOprateLclCusAgt(Context context)
			throws CoreException {
		logger.info("callThdUpdateOprateLclCusAgt start ... ...");

		String date = DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE);
		context.setData("TransCode", "Edit");
		context.setData("regDat", date);
		context.setData("txnCod", "GASHXY");
		context.setData("objSvr", "STDHGAS1");

		context.setProcessId("gdeupsb.oprGasCusAgent1");

		Map<String, Object> tradeMap1 = callThdTradeManager.trade(context);
		context.setDataMap(tradeMap1);

		context.setData("optDat", date);
		context.setData("optNod", context.getData("NodNo"));
		logger.info("===oprGasCusAgentAction1=====context=" + context);

		context.setProcessId("gdeupsb.oprGasCusAgentAction");
		logger.info("===oprGasCusAgentAction=====context=" + context);

		if ("EditOK".equals(context.getData("TransCode").toString().trim())) {

			// 动态协议表
			GdGasCusDay insCusInfo = new GdGasCusDay();
			insCusInfo.setSequence(get(BBIPPublicService.class)
					.getBBIPSequence());
			insCusInfo.setCusNo((String) context.getData(ParamKeys.CUS_NO));
			;
			insCusInfo.settCommd((String) context.getData("tCommd"));
			insCusInfo.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			insCusInfo.setCusNme((String) context.getData(ParamKeys.CUS_NME));
			insCusInfo.setOptDat(date);
			insCusInfo.setAccTyp((String) context.getData("cusTyp"));
			insCusInfo.setOptNod((String) context.getData(ParamKeys.OBK_BR));
			insCusInfo.setIdTyp((String) context.getData(ParamKeys.ID_TYPE));
			insCusInfo.setIdNo((String) context.getData(ParamKeys.ID_NO));
			insCusInfo.setThdCusNam((String) context
					.getData(ParamKeys.THD_CUS_NME));
			insCusInfo.setCmuTel((String) context.getData(ParamKeys.CMU_TEL));
			insCusInfo.setThdCusAdr((String) context
					.getData(ParamKeys.THD_CUSTOMER_ADDR));
			get(GdGasCusDayRepository.class).insert(insCusInfo);

			// 燃气协议表
			GdGasCusAll updateGasCusAll = BeanUtils.toObject(
					context.getDataMap(), GdGasCusAll.class);
			updateGasCusAll
					.setCusNo((String) context.getData(ParamKeys.CUS_NO));
			;
			updateGasCusAll
					.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			if (StringUtils.isNoneBlank((String) context
					.getData(ParamKeys.CUS_NME))) {
				insCusInfo.setCusNme((String) context
						.getData(ParamKeys.CUS_NME));
			}
			updateGasCusAll.setCusTyp((String) context.getData("cusTyp"));
			updateGasCusAll.setOptDat(date);
			updateGasCusAll.setOptNod((String) context
					.getData(ParamKeys.OBK_BR));
			updateGasCusAll.setIdTyp((String) context
					.getData(ParamKeys.ID_TYPE));
			updateGasCusAll.setIdNo((String) context.getData(ParamKeys.ID_NO));
			updateGasCusAll.setThdCusNme((String) context
					.getData(ParamKeys.THD_CUS_NME));
			updateGasCusAll.setCmuTel((String) context
					.getData(ParamKeys.CMU_TEL));
			updateGasCusAll.setThdCusAdr((String) context
					.getData(ParamKeys.THD_CUSTOMER_ADDR));
			get(GdGasCusAllRepository.class).update(updateGasCusAll);

			// context.setData("msgTyp", "N");
			// context.setData("rspCod", GDConstants.GDEUPSB_TXN_SUCC_CODE);
			// context.setData("rspMsg", "新增成功");

			logger.info("============修改本地协议，新增动态协议成功");
		} else {
			throw new CoreException(GDErrorCodes.GAS_CUS_AGT_EDITNO);
		}

		logger.info("callThdUpdateOprateLclCusAgt end ... ...");

	}

	private void callThdAndOprateLclCusAgt(Context context)
			throws CoreException {
		logger.info("callThdAndOprateLclCusAgt start ... ...");
		logger.info("=================context before insCusAgt callThd"
				+ context);
		String date = DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE);
		context.setData("regDat", date);
		context.setData("txnCod", "GASHXY");
		context.setData("objSvr", "STDHGAS1");

		context.setProcessId("gdeupsb.oprGasCusAgent1");

		Map<String, Object> tradeMap1 = callThdTradeManager.trade(context);
		context.setDataMap(tradeMap1);

		context.setData("optDat", date);
		context.setData("optNod", context.getData("NodNo"));
		logger.info("===oprGasCusAgentAction1=====context=" + context);

		context.setProcessId("gdeupsb.oprGasCusAgentAction");
		logger.info("===oprGasCusAgentAction=====context=" + context);

		if ("AddOK".equals(context.getData("TransCode").toString().trim())) {

			// 动态协议表新增一条数据
			GdGasCusDay insCusInfo = new GdGasCusDay();
			insCusInfo.setSequence(get(BBIPPublicService.class)
					.getBBIPSequence());
			insCusInfo.setCusNo((String) context.getData(ParamKeys.CUS_NO));
			;
			insCusInfo.settCommd((String) context.getData("tCommd"));
			insCusInfo.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			if (StringUtils.isNoneBlank((String) context
					.getData(ParamKeys.CUS_NME))) {
				insCusInfo.setCusNme((String) context
						.getData(ParamKeys.CUS_NME));
			}
			insCusInfo.setAccTyp((String) context.getData("cusTyp"));
			insCusInfo.setOptDat(date);
			insCusInfo.setOptNod((String) context.getData(ParamKeys.OBK_BR));
			insCusInfo.setIdTyp((String) context.getData(ParamKeys.ID_TYPE));
			insCusInfo.setIdNo((String) context.getData(ParamKeys.ID_NO));
			insCusInfo.setThdCusNam((String) context
					.getData(ParamKeys.THD_CUS_NME));
			insCusInfo.setCmuTel((String) context.getData(ParamKeys.CMU_TEL));
			insCusInfo.setThdCusAdr((String) context
					.getData(ParamKeys.THD_CUSTOMER_ADDR));
			get(GdGasCusDayRepository.class).insert(insCusInfo);

			// 燃气协议表新增一条数据
			GdGasCusAll addGasCusAll = BeanUtils.toObject(context.getDataMap(),
					GdGasCusAll.class);
			addGasCusAll.setCusNo((String) context.getData(ParamKeys.CUS_NO));
			;
			addGasCusAll.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			addGasCusAll.setCusNme((String) context.getData(ParamKeys.CUS_NME));
			addGasCusAll.setCusTyp((String) context.getData("cusTyp"));
			addGasCusAll.setOptDat(date);
			addGasCusAll.setOptNod((String) context.getData(ParamKeys.OBK_BR));
			addGasCusAll.setIdTyp((String) context.getData(ParamKeys.ID_TYPE));
			addGasCusAll.setIdNo((String) context.getData(ParamKeys.ID_NO));
			addGasCusAll.setThdCusNme((String) context
					.getData(ParamKeys.THD_CUS_NME));
			addGasCusAll.setCmuTel((String) context.getData(ParamKeys.CMU_TEL));
			addGasCusAll.setThdCusAdr((String) context
					.getData(ParamKeys.THD_CUSTOMER_ADDR));

			get(GdGasCusAllRepository.class).insert(addGasCusAll);

			logger.info("============新增本地协议，动态协议成功");
		}
		if ("Double".equals(context.getData("TransCode").toString().trim())) {
			throw new CoreException(GDErrorCodes.GAS_CUS_AGT_DOUBLE);
		}
		if ("AddNo".equals(context.getData("TransCode").toString().trim())) {
			// 冲代收付协议
			get(BBIPPublicService.class).synExecute(
					"eups.commReversalACPSCusAgent", context);
			throw new CoreException(GDErrorCodes.GAS_CUS_AGT_ADDNO);
		}
		logger.info("callThdAndOprateLclCusAgt end ... ...");
	}

	private void setAgtCltAndCusInf(Context context) {
		List<Map<String, Object>> agentCollectAgreementList = new ArrayList<Map<String, Object>>();
		Map<String, Object> agentCollectAgreementListMap = setAgentCollectAgreementMap(context);
		agentCollectAgreementList.add(agentCollectAgreementListMap);
		context.setData("agentCollectAgreement", agentCollectAgreementList);
		// context.setDataMap(agentCollectAgreementListMap);

		List<Map<String, Object>> customerInfoList = new ArrayList<Map<String, Object>>();
		Map<String, Object> customerInfoMap = setCustomerInfoMap(context);
		customerInfoList.add(customerInfoMap);
		context.setData("customerInfo", customerInfoList);
		// context.setDataMap(customerInfoMap);

		logger.info("============context after setAgtCltAndCusInf :" + context);
	}

	private Map<String, Object> setAcpMap(Context context) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("traceNo", context.getData(ParamKeys.TRACE_NO));
		map.put("traceSrc", context.getData(ParamKeys.TRACE_SOURCE));
		map.put("version", context.getData(ParamKeys.VERSION));
		map.put("reqTme", new Date());
		map.put("reqJrnNo", get(BBIPPublicService.class).getBBIPSequence());
		map.put("reqSysCde", context.getData(ParamKeys.REQ_SYS_CDE));
		map.put("tlr", context.getData(ParamKeys.TELLER));
		map.put("chn", context.getData(ParamKeys.CHANNEL));
		map.put("bk", context.getData(ParamKeys.BK));
		map.put("br", context.getData(ParamKeys.BR));
		context.setDataMap(map);
		return map;
	}

	private Map<String, Object> setCustomerInfoMap(Context context) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("agtCllCusId", context.getData(ParamKeys.THD_CUS_NO));
		map.put("cusTyp", context.getData("cusTyp"));
		map.put(ParamKeys.CUS_NO, context.getData(ParamKeys.THD_CUS_NO));
		map.put(ParamKeys.CUS_AC, context.getData(ParamKeys.CUS_AC));
		map.put(ParamKeys.CUS_NME, context.getData(ParamKeys.CUS_NME));
		map.put(ParamKeys.CCY, "CNY");
		map.put(ParamKeys.ID_TYPE, context.getData(ParamKeys.ID_TYPE));
		map.put("idNo", context.getData(ParamKeys.ID_NO));
		map.put("bvCde", context.getData("bvCde"));
		if (StringUtils.isNotBlank((String) context.getData("bvNo"))) {
			map.put("bvNo", (String) context.getData("bvNo"));
		}
		map.put("ourOthFlg", "0");
		return map;
	}

	private Map<String, Object> setAgentCollectAgreementMap(Context context) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank((String) context.getData("agdAgrNo"))) {
			map.put("agdAgrNo", context.getData("agdAgrNo"));
		}
		map.put(ParamKeys.CUS_AC, context.getData(ParamKeys.CUS_AC));
		map.put("acoAc", context.getData(ParamKeys.CUS_AC));
		if (StringUtils.isNotBlank((String) context.getData("pwd"))) {
			map.put("pwd", (String) context.getData("pwd"));
		}
		map.put("bvCde", context.getData("bvCde"));
		if (StringUtils.isNotBlank((String) context.getData("bvNo"))) {
			map.put("bvNo", (String) context.getData("bvNo"));
		}
		map.put(ParamKeys.COMPANY_NO, context.getData(ParamKeys.COMPANY_NO));
		EupsThdBaseInfo baseInfo = new EupsThdBaseInfo();
		baseInfo.setEupsBusTyp((String) context
				.getData(ParamKeys.EUPS_BUSS_TYPE));
		if (StringUtils.isNotBlank((String) context
				.getData(ParamKeys.COMPANY_NO))) {
			baseInfo.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
		}
		List<EupsThdBaseInfo> infoList = get(EupsThdBaseInfoRepository.class)
				.find(baseInfo);
		String comNme = infoList.get(0).getComNme();
		context.setData(ParamKeys.COMPANY_NAME, comNme);
		map.put("comNum", comNme);

		map.put(ParamKeys.BUS_TYP, "0"); // TODO 0-代收； 暂用0，待确认0-代收,1-代付,2-代缴
											// 0-批量代收；1-批量代付；2-联机缴费；
		map.put(ParamKeys.BUSS_KIND, context.getData(ParamKeys.BUSS_KIND));
		map.put(ParamKeys.CCY, "CNY");
		map.put("cusFeeDerFlg", "0"); // TODO 暂用0，待确认
		map.put("agtSrvCusId", context.getData("agtSrvCusId"));
		map.put("agtSrvCusPnm", context.getData(ParamKeys.THD_CUS_NME));
		map.put("agrVldDte",
				DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)); // YYYYMMDD默认当日
		map.put("agrExpDte", "99991231"); // YYYYMMDD默认最大日，99991231
		map.put(ParamKeys.CMU_TEL, context.getData(ParamKeys.CMU_TEL));
		map.put(ParamKeys.THD_CUS_NO, context.getData(ParamKeys.THD_CUS_NO));
		return map;
	}

}

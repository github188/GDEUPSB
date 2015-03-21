package com.bocom.bbip.gdeupsb.action.gas;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
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
public class OprGasCusAgentActionV2 extends BaseAction {
	public static Logger logger = LoggerFactory
			.getLogger(OprGasCusAgentActionV2.class);

	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("Enter in OprGasCusAgentActionV2!...........");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

		logger.info("=============context:"
				+ context.getData(ParamKeys.TRACE_NO));

		context.setData(GDParamKeys.GAS_BK, "CNJT");
		context.setData(ParamKeys.CUS_NO, context.getData("thdCusNo"));

		logger.info("================now context =" + context);
		logger.info("=======功能选择(1-新增 2-修改 3-删除 4-查询):"
				+ context.getData("optFlg"));

		if ("4".equals(context.getData("optFlg"))) { // 4:查询交易
			// 上代收付查询协议，先根据cusAc进行列表查询得到协议编号，再用协议编号查询明细（用户信息）
			Map<String, Object> cusListMap = setAcpMap(context);
			cusListMap.put(ParamKeys.CUS_AC, context.getData(ParamKeys.CUS_AC));

			context.setDataMap(cusListMap);
			Result accessObjList = bgspServiceAccessObject.callServiceFlatting(
					"queryListAgentCollectAgreement", cusListMap);
			logger.info("========after optFlg=4 queryListAgentCollectAgreement ======="
					+ context);
			logger.info("=========after optFlg=4 accessObjList：【accessObjList.getResponseCode():"
					+ accessObjList.getResponseCode()
					+ "】【accessObjList.getResponseMessage():"
					+ accessObjList.getResponseMessage()
					+ "】【accessObjList.getResponseType()："
					+ accessObjList.getResponseType() + "】");
			// 【accessObjList.getResponseCode():BBIP0004AGPM66】responseMessage():客户信息不存在。responseType()：E】
			if (!("N".equals(accessObjList.getResponseType()))) {
				context.setData(ParamKeys.RESPONSE_MESSAGE, accessObjList.getResponseMessage());
				throw new CoreException(GDErrorCodes.GAS_QRY_AGT_ERR_EMT);
			}

			Map<String, Object> cusDtlMap = setAcpMap(context);
			cusDtlMap.put("agdAgrNo", accessObjList.getPayload()
					.get("agdAgrNo"));

			context.setData("agdAgrNo",
					accessObjList.getPayload().get("agdAgrNo"));

			logger.info("========before optFlg=4 queryDetailAgentCollectAgreement ======="
					+ context);
			Result qryCusDetail = bgspServiceAccessObject.callServiceFlatting(
					"queryDetailAgentCollectAgreement", cusDtlMap);
			logger.info("========before optFlg=4 queryDetailAgentCollectAgreement ======="
					+ context);
			logger.info("==============msg:【operateAcpAgtResult.getResponseCode()："
					+ qryCusDetail.getResponseCode()
					+ "】【accessObjList.getResponseMessage():"
					+ qryCusDetail.getResponseMessage()
					+ "】【accessObjList.getResponseType()："
					+ qryCusDetail.getResponseType() + "】");
			if (!("N".equals(qryCusDetail.getResponseType()))) {
				context.setData(ParamKeys.RESPONSE_MESSAGE, qryCusDetail.getResponseMessage());
				throw new CoreException(GDErrorCodes.GAS_QRY_AGT_ERR_EMT);
			}

			context.setDataMap(qryCusDetail.getPayload());
			context.setData(ParamKeys.THD_CUS_NO, qryCusDetail.getPayload()
					.get("cusNo"));
			logger.info("用户协议查询完成");
		} else {
			// 非查询交易要授权，前端授权，此处只检查
			String authTlr = context.getData(ParamKeys.AUTHOR_LEVEL);
			if (StringUtils.isEmpty(authTlr)) {
				throw new CoreException(ErrorCodes.EUPS_CANCEL_CHECK_AUTH_FAIL);
			}
			logger.info("已授权，可进行交易");

			// context.setData("TActNo", GDConstants.GAS_THD_ACT_NO);

			context.setData("tCommd", "QryUser");
			context.setData("TransCode", "GASHXX");

			// context.setData("txnCod", "GASHXX");
			// context.setData("objSvr", "STDHGAS1");

			logger.info("=============context=======" + context);
			logger.info("发燃气第三方，查看有无该用户编号信息");

			Map<String, Object> thdRspMsg = get(ThirdPartyAdaptor.class).trade(
					context);
			context.setDataMap(thdRspMsg);
			logger.info("======================context========" + context);

			// 处理燃气返回的信息
			logger.info("==============rtnCod=" + context.getData("rtnCod")
					+ "============optFlg=" + context.getData("optFlg"));

			if ("QryUser".equals(context.getData("rtnCod").toString().trim())) {

				Map<String, Object> cusListMap = setAcpMap(context);
				cusListMap.put(ParamKeys.CUS_AC,
						context.getData(ParamKeys.CUS_AC));
				Result accessObjList = bgspServiceAccessObject
						.callServiceFlatting("queryListAgentCollectAgreement",
								cusListMap);
				logger.info("==============msg:【operateAcpAgtResult.getResponseCode()："
						+ accessObjList.getResponseCode()
						+ "】【accessObjList.getResponseMessage():"
						+ accessObjList.getResponseMessage()
						+ "】【accessObjList.getResponseType()："
						+ accessObjList.getResponseType() + "】");
				
				context.setData(ParamKeys.RESPONSE_MESSAGE, accessObjList.getResponseMessage());
				
				if ("1".equals(context.getData("optFlg"))) { // optFlg = 1
					if ("N".equals(accessObjList.getResponseType())) {
						throw new CoreRuntimeException(
								GDErrorCodes.GAS_QRY_AGT_ERR_EXIST);
					}
					context.setData("tCommd", "Add");
					logger.info("============未签约，可签约");
				}

				if ("2".equals(context.getData("optFlg"))) { // optFlg = 2
																// 燃气返回QryUser,
																// 说明燃气存在该用户编号，可修改
					if (!("N".equals(accessObjList.getResponseType()))) {
						throw new CoreRuntimeException(GDErrorCodes.GAS_QRY_AGT_ERR_EMT);
					}
					context.setData("tCommd", "Edit");
					logger.info("============有协议，可修改");
				}

				if ("3".equals(context.getData("optFlg"))) { // optFlg = 3
																// 燃气返回QryUser,
																// 说明燃气存在该用户编号，可删除
					if (!("N".equals(accessObjList.getResponseType()))) {
						throw new CoreRuntimeException(GDErrorCodes.GAS_QRY_AGT_ERR_EMT);
					}
					context.setData("tCommd", "Stop");
					logger.info("===========有协议，可删除");
				}

				String date = DateUtils.format(new Date(),
						DateUtils.STYLE_SIMPLE_DATE);

				// 非查询交易，送第三方
				// 发起交易
				context.setData("TransCode", "GASHXY");
				context.setData("regDat", date);
				context.setData("txnCod", "GASHXY");
				context.setData("objSvr", "STDHGAS1");

				context.setProcessId("gdeupsb.oprGasCusAgent1");

				Map<String, Object> tradeGASHXY = get(ThirdPartyAdaptor.class)
						.trade(context);
				context.setDataMap(tradeGASHXY);

				context.setData("optDat", date);
				context.setData("optNod", context.getData("NodNo"));
				context.setProcessId("gdeupsb.oprGasCusAgentAction");

				Result operateAcpAgtResult = null;

				if ("AddOK".equals(context.getData("rtnCod").toString().trim())) { // 第三方返回新增成功，协议表、动态协议表插入新增数据
					// 代扣协议管理-修改和新增 maintainAgentCollectAgreement
					Map<String, Object> addCusMap = setAcpMap(context);
					addCusMap.put(ParamKeys.OPERATION_TPYE, "0"); // oprTyp=0,新增
					addCusMap.put("cusTyp", context.getData("cusTyp")); // 账户类型
					addCusMap.put("cusAc", context.getData(ParamKeys.CUS_AC));
					addCusMap.put("ccy", "CNY");
					addCusMap.put("idTyp", context.getData(ParamKeys.ID_TYPE));
					addCusMap.put("idNo", context.getData(ParamKeys.ID_NO));
					addCusMap.put("comNo",
							context.getData(ParamKeys.COMPANY_NO));
					addCusMap.put(ParamKeys.BUS_TYP, "1"); // TODO
															// 暂用1，待确认0-代收,1-代付,2-代缴
					addCusMap.put(ParamKeys.BUSS_KIND, "PGAS");// TODO
																// 暂用PGAS，待确认
					addCusMap.put("cusFeeDerFlg", "0"); // TODO 暂用0，待确认
					addCusMap.put("agtSrvCusId", "0"); // TODO 暂用0，待确认
					addCusMap.put("agrVldDte", DateUtils.format(new Date(),
							DateUtils.STYLE_yyyyMMdd)); // YYYYMMDD默认当日
					addCusMap.put("agrExpDte", "99991231"); // YYYYMMDD默认最大日，99991231

					// TODO
					addCusMap.put("agrChl", "90");

					try {
						operateAcpAgtResult = bgspServiceAccessObject
								.callServiceFlatting(
										"maintainAgentCollectAgreement",
										addCusMap);
					} catch (Exception e) {
						throw new CoreException(GDErrorCodes.GAS_OPR_ACP_ERR);
					}
					
					logger.info("==============msg:【operateAcpAgtResult.getResponseCode()："
							+ operateAcpAgtResult.getResponseCode()
							+ "】【accessObjList.getResponseMessage():"
							+ operateAcpAgtResult.getResponseMessage()
							+ "】【accessObjList.getResponseType()："
							+ operateAcpAgtResult.getResponseType() + "】");
					
					context.setData(ParamKeys.RESPONSE_MESSAGE, operateAcpAgtResult.getResponseMessage());
					
					if (!("N".equals(operateAcpAgtResult.getResponseType()))) {
						throw new CoreRuntimeException(GDErrorCodes.GAS_QRY_AGT_ERR_EMT);
					}

					insertCusInfo(context);

					// 同时往燃气协议表新增一条数据
					GdGasCusAll addGasCusAll = BeanUtils.toObject(
							context.getDataMap(), GdGasCusAll.class);
					addGasCusAll.setOptDat(date);
					addGasCusAll.setOptNod((String) context
							.getData(ParamKeys.OBK_BR));
					get(GdGasCusAllRepository.class).insert(addGasCusAll);

					context.setData("msgTyp", "N");
					context.setData("rspCod", GDConstants.GDEUPSB_TXN_SUCC_CODE);
					context.setData("rspMsg", "新增成功");

					logger.info("============新增成功");
				}
				if ("EditOK"
						.equals(context.getData("rtnCod").toString().trim())) { // 第三方返回修改成功，更新协议表，动态协议表插入数据
					// 代扣协议管理-修改和新增 maintainAgentCollectAgreement
					Map<String, Object> editCusMap = setAcpMap(context);
					editCusMap.put(ParamKeys.OPERATION_TPYE, "1"); // oprTyp=1,修改
					editCusMap.put("cusTyp", context.getData("cusTyp")); // 账户类型
					editCusMap.put("cusAc", context.getData(ParamKeys.CUS_AC));
					editCusMap.put("ccy", "CNY");
					editCusMap.put("idTyp", context.getData(ParamKeys.ID_TYPE));
					editCusMap.put("idNo", context.getData(ParamKeys.ID_NO));
					editCusMap.put("comNo",
							context.getData(ParamKeys.COMPANY_NO));
					editCusMap.put(ParamKeys.BUS_TYP, "1"); // TODO 暂用1，待确认
															// 0-代收,1-代付,2-代缴
					editCusMap.put(ParamKeys.BUSS_KIND, "PGAS");// TODO
																// 暂用PGAS，待确认
					editCusMap.put("cusFeeDerFlg", "0"); // TODO 暂用0，待确认
					editCusMap.put("agtSrvCusId", "0"); // TODO 暂用0，待确认
					editCusMap.put("agrVldDte", DateUtils.format(new Date(),
							DateUtils.STYLE_yyyyMMdd)); // YYYYMMDD默认当日
					editCusMap.put("agrExpDte", "99991231"); // YYYYMMDD默认最大日，99991231
					editCusMap.put("agrChl", "90");
					try {
						operateAcpAgtResult = bgspServiceAccessObject
								.callServiceFlatting(
										"maintainAgentCollectAgreement",
										editCusMap);
					} catch (Exception e) {
						throw new CoreException(GDErrorCodes.GAS_OPR_ACP_ERR);
					}
					
					logger.info("==============msg:【operateAcpAgtResult.getResponseCode()："
							+ operateAcpAgtResult.getResponseCode()
							+ "】【accessObjList.getResponseMessage():"
							+ operateAcpAgtResult.getResponseMessage()
							+ "】【accessObjList.getResponseType()："
							+ operateAcpAgtResult.getResponseType() + "】");
					context.setData(ParamKeys.RESPONSE_MESSAGE, operateAcpAgtResult.getResponseMessage());
					
					if (!("N".equals(operateAcpAgtResult.getResponseType()))) {
						throw new CoreRuntimeException(GDErrorCodes.GAS_OPR_ACP_ERR);
					}

					insertCusInfo(context);

					// 修改燃气协议表中对应的协议数据
					GdGasCusAll updGasCusAll = BeanUtils.toObject(
							context.getDataMap(), GdGasCusAll.class);
					updGasCusAll.setOptDat(date);
					updGasCusAll.setOptNod((String) context
							.getData(ParamKeys.OBK_BR));
					get(GdGasCusAllRepository.class).update(updGasCusAll);

					context.setData("msgTyp", "N");
					context.setData("rspCod", GDConstants.GDEUPSB_TXN_SUCC_CODE);
					context.setData("rspMsg", "修改成功");

					logger.info("============修改成功");
				}
				if ("StopOK"
						.equals(context.getData("rtnCod").toString().trim())) { // //第三方返回删除成功，delete协议表/代收付协议表相关数据，动态协议表插入数据

					String agdAgrNo = (String) accessObjList.getPayload().get(
							"agdAgrNo");
					Map<String, Object> delCusInfoMap = setAcpMap(context);
					delCusInfoMap.put("agdAgrNo", agdAgrNo);
					try {
						operateAcpAgtResult = bgspServiceAccessObject
								.callServiceFlatting(
										"deleteAgentCollectAgreement",
										delCusInfoMap);
					} catch (Exception e) {
						throw new CoreException(GDErrorCodes.GAS_OPR_ACP_ERR);
					}
					
					logger.info("==============msg:【operateAcpAgtResult.getResponseCode()："
							+ operateAcpAgtResult.getResponseCode()
							+ "】【accessObjList.getResponseMessage():"
							+ operateAcpAgtResult.getResponseMessage()
							+ "】【accessObjList.getResponseType()："
							+ operateAcpAgtResult.getResponseType() + "】");
					
					context.setData(ParamKeys.RESPONSE_MESSAGE, operateAcpAgtResult.getResponseMessage());

					if (!("N".equals(operateAcpAgtResult.getResponseType()))) {
						throw new CoreRuntimeException(GDErrorCodes.GAS_OPR_ACP_ERR);
					}

					insertCusInfo(context);

					// 删除燃气协议表相关数据
					GdGasCusAll delCus = new GdGasCusAll();
					delCus.setIdNo(context.getData(ParamKeys.ID_NO).toString()
							.trim());
					delCus.setCusNo(context.getData(ParamKeys.THD_CUS_NO)
							.toString().trim());
					get(GdGasCusAllRepository.class).delete(delCus);

					context.setData("msgTyp", "N");
					context.setData("rspCod", GDConstants.GDEUPSB_TXN_SUCC_CODE);
					context.setData("rspMsg", "删除成功");

					logger.info("============删除成功");
				}
				if ("Double"
						.equals(context.getData("rtnCod").toString().trim())) { // 燃气返回已存在协议
					context.setData("msgTyp", "E");
					context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
					context.setData("rspMsg", "已存在协议，请联系系统管理员或者燃气公司!");
					throw new CoreRuntimeException(
							GDErrorCodes.GAS_QRY_AGT_ERR_DOUBLE);
				}
				if ("AddNo".equals(context.getData("rtnCod").toString().trim())
						|| "EditNo".equals(context.getData("rtnCod").toString()
								.trim())
						|| "StopNo".equals(context.getData("rtnCod").toString()
								.trim())) { // 燃气返回别的操作失败情况

					context.setData("msgTyp", "E");
					context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
					context.setData("rspMsg", "燃气方返回操作失败!");
					throw new CoreRuntimeException(
							GDErrorCodes.GAS_CUS_AGT_OPRE);
				}
			} else if ("NOUser".equals(context.getData("rtnCod").toString()
					.trim())) { // NOUser 为无此用户（用户编码错误）
				throw new CoreRuntimeException(GDErrorCodes.GAS_QRY_AGT_ERR_NO);
			} else if ("UserStop".equals(context.getData("rtnCod").toString()
					.trim())) { // UserStop为此用户已报停
				throw new CoreRuntimeException(
						GDErrorCodes.GAS_QRY_AGT_ERR_STOP);
			} else {
				throw new CoreRuntimeException("GDEUPSBBP9001");
			}

		}
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);

	}

	private Map<String, Object> setAcpMap(Context context) {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("traceNo", context.getData(ParamKeys.TRACE_NO));
		// map.put("traceSrc", "GDEUPSB");
		// map.put("version", "GDEUPSB-1.0.0");
		map.put("traceSrc", context.getData(ParamKeys.TRACE_SOURCE));
		map.put("version", context.getData(ParamKeys.VERSION));
		map.put("reqTme", new Date());
		map.put("reqJrnNo", get(BBIPPublicService.class).getBBIPSequence());
		map.put("reqSysCde", context.getData(ParamKeys.EUPS_BUSS_TYPE));
		map.put("tlr", context.getData(ParamKeys.TELLER));
		map.put("chn", context.getData(ParamKeys.CHANNEL));
		map.put("bk", context.getData(ParamKeys.BK));
		map.put("br", context.getData(ParamKeys.BR));

		return map;
	}

	/**
	 * 无论增删改用户协议表，均向燃气每天动态协议表插入一条数据， tCommd可表示增删改
	 * 
	 * @param context
	 * @throws CoreException
	 * @throws CoreRuntimeException
	 */
	public void insertCusInfo(Context context) throws CoreException,
			CoreRuntimeException {
		GdGasCusDay insCusInfo = BeanUtils.toObject(context.getDataMap(),
				GdGasCusDay.class);
		String sqn = get(BBIPPublicService.class).getBBIPSequence();
		insCusInfo.setSequence(sqn);
		get(GdGasCusDayRepository.class).insert(insCusInfo);
	}
}

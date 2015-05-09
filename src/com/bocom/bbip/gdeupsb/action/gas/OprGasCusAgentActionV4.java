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
import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
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
	@Autowired
	AccountService accountService;
	@Autowired
	BGSPServiceAccessObject bgspServiceAccessObject;
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;

	public static Logger logger = LoggerFactory
			.getLogger(OprGasCusAgentActionV4.class);

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("Enter in OprGasCusAgentActionV4!...........");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		String AC_DTE = DateUtils.format((Date) bbipPublicService.getAcDate(),
				DateUtils.STYLE_yyyyMMdd);
		logger.info("============acDte:" + AC_DTE);

		context.setData("STR_AC_DTE", AC_DTE);

		// 备份，修改时用 
		String cusTypBak = context.getData("cusTyp");
		context.setData("cusTypBak", cusTypBak);
		context.setData("cusAcBak", context.getData(ParamKeys.CUS_AC));
		context.setData("cusNmeBak", context.getData(ParamKeys.CUS_NME));
		context.setData("cmuTelBak", context.getData(ParamKeys.CMU_TEL));
		context.setData("idTypBak", context.getData(ParamKeys.ID_TYPE));
		context.setData("idNoBak", context.getData(ParamKeys.ID_NO));

		context.setData(GDParamKeys.GAS_BK, "cnjt");
		context.setData(ParamKeys.CUS_NO, context.getData(ParamKeys.THD_CUS_NO));
		context.setData("agtSrvCusId", context.getData(ParamKeys.THD_CUS_NO));

		context.setData("ccy", "CNY");
		logger.info("================now context =" + context);
		String optFlg = context.getData("optFlg");
		logger.info("=======功能选择(1-新增 2-修改 3-删除 4-查询):" + optFlg);

		if ("5".equals(optFlg)) {// for test cusAcSts
			checkCusAcSts(context);
		}

		if ("4".equals(optFlg)) { // 4:查询交易

			GdGasCusAll qryCusAll = new GdGasCusAll();

			if (StringUtils.isNotBlank((String) context
					.getData(ParamKeys.CUS_AC))) {
				qryCusAll.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			}
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
			// context.setData("cusTyp", infoList.get(0).get("CUSTYP"));
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
			String cusAc = context.getData(ParamKeys.CUS_AC).toString().trim();
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

			if ("1".equals(optFlg)) { // 新增
				//TODO 校验账号状态
				// checkCusAcSts(context);

				// 查询本地协议表是否存在该cusNo
				GdGasCusAll gdGasCusAll = new GdGasCusAll();
				gdGasCusAll.setCusNo((String) context
						.getData(ParamKeys.THD_CUS_NO));
				List<GdGasCusAll> existCus = get(GdGasCusAllRepository.class)
						.find(gdGasCusAll);
				if (CollectionUtils.isNotEmpty(existCus)) {
					throw new CoreRuntimeException(
							GDErrorCodes.GAS_QRY_AGT_ERR_EXIST);
				}

				logger.info("============未签约，可签约");
				context.setData("tCommd", "Add");
				context.setData("agrChl", "01");
				context.setData(ParamKeys.OPERATION_TPYE, "0"); // oprTyp=0:增

				setAgtCltAndCusInf(context);

				// 不使用代收付签模板以及processId，直接调用接口
				Result insertCusAgtResult = bgspServiceAccessObject
						.callServiceFlatting("maintainAgentCollectAgreement",
								context.getDataMap());
				if (!insertCusAgtResult.isSuccess()) {
					throw new CoreRuntimeException(
							insertCusAgtResult.getResponseMessage());
				}
				logger.info("=============代收付签约成功，发THD签约===========");
				callThdAndAddThdLclCusAgt(context);
				context.setData("cusTyp", cusTypBak);
			}
			if ("2".equals(optFlg)) { // 修改 （账号 户名 地址 手机）
				//TODO 校验账号状态
//				checkCusAcSts(context);
				
				// 根据用户编号查询本地协议表得原签约CusAc
				String thdCusno = context.getData(ParamKeys.THD_CUS_NO);
				GdGasCusAll baseCusInfo = new GdGasCusAll();
				baseCusInfo.setCusNo(thdCusno);
				List<GdGasCusAll> cuslist = get(GdGasCusAllRepository.class)
						.find(baseCusInfo);
				if (null == cuslist || CollectionUtils.isEmpty(cuslist)) {
					logger.info("There are no records for select agents from GdGasCusAll ");
					throw new CoreException("协议不存在或已被删除");
				}
				String oldCusAc = (String) cuslist.get(0).getCusAc();
				context.setData(ParamKeys.CUS_AC, oldCusAc);
				// 用原签约CusAc到acp列表查询,得agtCllCusId等相关信息
				Result accessObjList = bgspServiceAccessObject
						.callServiceFlatting("queryListAgentCollectAgreement",
								context.getDataMap());
				if (!accessObjList.isSuccess()) {
					context.setData(ParamKeys.RESPONSE_MESSAGE,
							accessObjList.getResponseMessage());
					throw new CoreException(accessObjList.getResponseMessage());
				}
				context.setDataMap(accessObjList.getPayload());
				logger.info("======================context after qryCusAgtList:"
						+ context);
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> agentCollectAgreementMaps = (List<Map<String, Object>>) context
						.getData("agentCollectAgreement");
				// 返回的协议信息List可能包含多条协议信息，剔除与目标删除用户无关的协议信息
				for (int i = 0; i < agentCollectAgreementMaps.size(); i++) {
					if (!(context.getData(ParamKeys.THD_CUS_NME)
							.equals(agentCollectAgreementMaps.get(i).get(
									"agtSrvCusPnm")))) {
						agentCollectAgreementMaps.remove(i);
					}
				}
				//更新协议信息	<agentCollectAgreement>
				if(StringUtils.isNotBlank((String)context.getData("cusAcBak"))){
					agentCollectAgreementMaps.get(0).put(ParamKeys.CUS_AC, context.getData("cusAcBak"));
					agentCollectAgreementMaps.get(0).put("acoAc", context.getData("cusAcBak"));
				}
				agentCollectAgreementMaps.get(0).put("bvCde", context.getData("bvCde"));
				if (StringUtils.isNotBlank((String) context.getData("bvNo"))) {
					agentCollectAgreementMaps.get(0).put("bvNo", (String) context.getData("bvNo"));
				}
				agentCollectAgreementMaps.get(0).put("agrVldDte", AC_DTE);
				agentCollectAgreementMaps.get(0).put("ageBr", context.getData(ParamKeys.BK));
				agentCollectAgreementMaps.get(0).put("agrBr", context.getData(ParamKeys.BR));
				agentCollectAgreementMaps.get(0).put("agrTlr", context.getData(ParamKeys.TELLER));
				if (StringUtils.isNotBlank((String) context.getData("cmuTelBak"))) {
					agentCollectAgreementMaps.get(0).put("cmuTel", context.getData("cmuTelBak"));
				}
				

				context.setData("agentCollectAgreement",
						agentCollectAgreementMaps);
				logger.info(
						"=========context after delete other agentCollectAgreement set agentCollectAgreement=========",
						context);
				
				@SuppressWarnings("unchecked")
				//更新客户信息	<customerInfo>
				List<Map<String, Object>> customerInfoMaps = (List<Map<String, Object>>) context
						.getData("customerInfo");
				if(StringUtils.isNotBlank((String)context.getData("cusAcBak"))){
					customerInfoMaps.get(0).put(ParamKeys.CUS_AC, context.getData("cusAcBak"));
					customerInfoMaps.get(0).put(ParamKeys.CUS_NME, context.getData("cusNmeBak"));
				}
				if(StringUtils.isNotBlank((String)context.getData("idNoBak"))){
					customerInfoMaps.get(0).put(ParamKeys.ID_NO, context.getData("idNoBak"));
					customerInfoMaps.get(0).put(ParamKeys.ID_TYPE, context.getData("idTypBak"));
				}
//				customerInfoMaps.get(0).put(ParamKeys.ID_TYPE, context.getData(ParamKeys.ID_TYPE));
//				customerInfoMaps.get(0).put(ParamKeys.ID_NO, context.getData(ParamKeys.ID_NO));
				context.setData("customerInfo", customerInfoMaps);
				logger.info("============ context after set customerInfo : "
						, context);
				
				context.setData("tCommd", "Edit");
				context.setData("TransCode", "Edit");
				logger.info("============有协议，可修改");
				context.setData("agrChl", "01");
				context.setData(ParamKeys.OPERATION_TPYE, "1"); // //oprTyp=1:改

				Result editCusAgtResult = bgspServiceAccessObject
						.callServiceFlatting("maintainAgentCollectAgreement",
								context.getDataMap());
				if (!editCusAgtResult.isSuccess()) {
					throw new CoreRuntimeException(
							editCusAgtResult.getResponseMessage());
				}
				logger.info("=============代收付修改签约成功，发THD修改签约===========");
				callThdAndUpdateThdLclCusAgt(context);
				context.setData("cusTyp", cusTypBak);

			}

			if ("3".equals(optFlg)) { // 删除
				setAgtCltAndCusInf(context);
				context.setData("agrChl", "01");
				// 上代收付查询协议，先根据cusAc进行列表查询得到协议编号，再用协议编号查询明细（用户信息）

				Result accessObjList = bgspServiceAccessObject
						.callServiceFlatting("queryListAgentCollectAgreement",
								context.getDataMap());
				logger.info("========after optFlg=3 queryListAgentCollectAgreement ======="
						+ context);
				if (!accessObjList.isSuccess()) {
					throw new CoreException(accessObjList.getResponseMessage());
				} else {
					context.setDataMap(accessObjList.getPayload());
					logger.info("======================context after qryCusAgtList & setDataMap:"
							+ context);

					@SuppressWarnings("unchecked")
					List<Map<String, Object>> agentCollectAgreementMaps = (List<Map<String, Object>>) context
							.getData("agentCollectAgreement");
					// 返回的协议信息List可能包含多条协议信息，剔除与目标删除用户无关的协议信息
					 for (int i = 0; i < agentCollectAgreementMaps.size();
					 i++) {
					 if (!(context.getData(ParamKeys.THD_CUS_NME)
					 .equals(agentCollectAgreementMaps.get(i).get(
					 "agtSrvCusPnm")))) {
					 agentCollectAgreementMaps.remove(i);
					 }
					 }
					logger.info("==================");
					context.setData("agentCollectAgreement",
							agentCollectAgreementMaps);

					List<String> agdAgrNoList = new ArrayList<String>();
					agdAgrNoList.add((String) agentCollectAgreementMaps.get(0)
							.get("agdAgrNo"));
					context.setData("agdAgrNo", agdAgrNoList);

					List<Map<String, Object>> customerInfoMaps = (List<Map<String, Object>>) context
							.getData("customerInfo");

					String idNo = (String) customerInfoMaps.get(0).get("idNo");
					String idTyp = customerInfoMaps.get(0).get("idTyp")
							.toString().trim();
					context.setData(ParamKeys.ID_NO, idNo);
					context.setData(ParamKeys.ID_TYPE, idTyp);

					logger.info("============ context after set agdAgrNo : "
							+ context);

					logger.info("===========有协议，可删除");

					Result stopCusAgtResult = bgspServiceAccessObject
							.callServiceFlatting("deleteAgentCollectAgreement",
									context.getDataMap());

					if (!stopCusAgtResult.isSuccess()) {
						throw new CoreRuntimeException(
								stopCusAgtResult.getResponseMessage());
					}
					callThdAndStopThdLclCusAgt(context);
					context.setData("cusTyp", cusTypBak);
				}
			}
		}

		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}

	private void checkCusAcSts(Context context) throws CoreException {

		String cusAc = context.getData(ParamKeys.CUS_AC).toString().trim();
		// 校验卡折使用状态
		CusActInfResult cusactinfresult = new CusActInfResult();
		try {
			cusactinfresult = accountService.getAcInf(
					CommonRequest.build(context), cusAc);
		} catch (CoreException e) {
			e.printStackTrace();
			logger.info("check cusAcInfo error :", e);
		}

		if ("E".equals(cusactinfresult.getResponseType())) {
			throw new CoreException(cusactinfresult.getResponseMessage());
		}
		String cusAcSts = cusactinfresult.getCusAcSts();
		logger.info("///////////////// cusAcSts:" + cusAcSts);
		// TODO判断状态
		if ("00".equals(cusAcSts)) {
			throw new CoreException("该帐号处于不正常状态，不可进行交易");
		} else if ("01".equals(cusAcSts)) {
			throw new CoreException("该账号为预开户，不可进行交易");
		} else if ("02".equals(cusAcSts)) {
			throw new CoreException("该账号待激活，不可进行交易");
		} else if ("04".equals(cusAcSts)) {
			throw new CoreException("该账号处于抹帐状态，不可进行交易");
		} else if ("05".equals(cusAcSts)) {
			throw new CoreException("该账号已销户，不可进行交易");
		} else if ("06".equals(cusAcSts)) {
			throw new CoreException("该账号已销卡，不可进行交易");
		}
	}

	private void callThdAndStopThdLclCusAgt(Context context)
			throws CoreException {
		logger.info("callThdStopOprateLclCusAgt start ... ...");
		context.setData("tCommd", "Stop");
		context.setData("TransCode", "Stop");
		String date = DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE);
		context.setData("regDat", date);
		context.setData("txnCod", "GASHXY");
		context.setData("objSvr", "STDHGAS1");

		context.setProcessId("gdeupsb.oprGasCusAgent1");

		Map<String, Object> tradeMap1 = callThdTradeManager.trade(context);
		if (BPState.isBPStateOvertime(context)) {
			throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		}
		context.setDataMap(tradeMap1);

		context.setData("optDat", date);
		context.setData("optNod", context.getData("NodNo"));
		logger.info("===oprGasCusAgentAction1=====context=" + context);

		context.setProcessId("gdeupsb.oprGasCusAgentAction");
		logger.info("===oprGasCusAgentAction=====context=" + context);

		if ("StopOK".equalsIgnoreCase(context.getData("TransCode").toString()
				.trim())) {
			// 燃气协议表
			GdGasCusAll delGasCusAll = new GdGasCusAll();
			delGasCusAll.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			delGasCusAll.setCusNo((String) context
					.getData(ParamKeys.THD_CUS_NO));
			get(GdGasCusAllRepository.class).delete(delGasCusAll);
			// 动态协议表
			GdGasCusDay insCusInfo = new GdGasCusDay();
			insCusInfo.setSequence(get(BBIPPublicService.class)
					.getBBIPSequence());
			insCusInfo.setCusNo((String) context.getData(ParamKeys.THD_CUS_NO));
			;
			insCusInfo.settCommd((String) context.getData("tCommd"));
			insCusInfo.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			if (StringUtils.isNoneBlank((String) context
					.getData(ParamKeys.CUS_NME))) {
				insCusInfo.setCusNme((String) context
						.getData(ParamKeys.CUS_NME));
			}
			insCusInfo.setOptDat(date);
			insCusInfo.setAccTyp((String) context.getData("cusTypBak"));
			insCusInfo.setOptNod((String) context.getData(ParamKeys.BR)
					.toString().substring(2, 8));
			insCusInfo.setIdTyp((String) context.getData(ParamKeys.ID_TYPE));
			insCusInfo.setIdNo((String) context.getData(ParamKeys.ID_NO));
			insCusInfo.setThdCusNam((String) context
					.getData(ParamKeys.THD_CUS_NME));
			insCusInfo.setCmuTel((String) context.getData(ParamKeys.CMU_TEL));
			insCusInfo.setThdCusAdr((String) context
					.getData(ParamKeys.THD_CUSTOMER_ADDR));
			get(GdGasCusDayRepository.class).insert(insCusInfo);

			logger.info("============del本地协议，新增动态协议成功");
		} else {
			throw new CoreException(GDErrorCodes.GAS_CUS_AGT_STOPNO);
		}
		logger.info("callThdStopOprateLclCusAgt end ... ...");

	}

	private void callThdAndUpdateThdLclCusAgt(Context context)
			throws CoreException {
		logger.info("callThdUpdateOprateLclCusAgt start ... ...");

		String date = DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE);
		context.setData("TransCode", "Edit");
		context.setData("regDat", date);
		context.setData("txnCod", "GASHXY");
		context.setData("objSvr", "STDHGAS1");

		context.setProcessId("gdeupsb.oprGasCusAgent1");
		Map<String, Object> tradeMap1 = callThdTradeManager.trade(context);
		if (BPState.isBPStateOvertime(context)) {
			throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		}
		context.setDataMap(tradeMap1);
		context.setData("optDat", date);
		context.setData("optNod", context.getData("NodNo"));
		logger.info("===oprGasCusAgentAction1=====context=" + context);

		context.setProcessId("gdeupsb.oprGasCusAgentAction");
		logger.info("===oprGasCusAgentAction=====context=" + context);

		if ("EditOK".equals(context.getData("TransCode").toString().trim())) {
			// 燃气协议表
			GdGasCusAll updateGasCusAll = BeanUtils.toObject(
					context.getDataMap(), GdGasCusAll.class);
			updateGasCusAll
					.setCusNo((String) context.getData(ParamKeys.CUS_NO));
			updateGasCusAll
					.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			if (StringUtils.isNoneBlank((String) context
					.getData(ParamKeys.CUS_NME))) {
				updateGasCusAll.setCusNme((String) context
						.getData(ParamKeys.CUS_NME));
			}
			updateGasCusAll.setCusTyp((String) context.getData("cusTypBak"));
			updateGasCusAll.setOptDat(date);
			updateGasCusAll.setOptNod((String) context.getData(ParamKeys.BR));
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
			// 动态协议表
			GdGasCusDay insCusInfo = new GdGasCusDay();
			insCusInfo.setSequence(get(BBIPPublicService.class)
					.getBBIPSequence());
			insCusInfo.setCusNo((String) context.getData(ParamKeys.CUS_NO));
			insCusInfo.settCommd((String) context.getData("tCommd"));
			insCusInfo.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			insCusInfo.setCusNme((String) context.getData(ParamKeys.CUS_NME));
			insCusInfo.setOptDat(date);
			insCusInfo.setAccTyp((String) context.getData("cusTypBak"));
			insCusInfo.setOptNod((String) context.getData(ParamKeys.BR)
					.toString().substring(2, 8));
			insCusInfo.setIdTyp((String) context.getData(ParamKeys.ID_TYPE));
			insCusInfo.setIdNo((String) context.getData(ParamKeys.ID_NO));
			insCusInfo.setThdCusNam((String) context
					.getData(ParamKeys.THD_CUS_NME));
			insCusInfo.setCmuTel((String) context.getData(ParamKeys.CMU_TEL));
			insCusInfo.setThdCusAdr((String) context
					.getData(ParamKeys.THD_CUSTOMER_ADDR));
			get(GdGasCusDayRepository.class).insert(insCusInfo);

			logger.info("============修改本地协议，新增动态协议成功");
		} else {
			throw new CoreException(GDErrorCodes.GAS_CUS_AGT_EDITNO);
		}
		logger.info("callThdUpdateOprateLclCusAgt end ... ...");
	}

	private void callThdAndAddThdLclCusAgt(Context context)
			throws CoreException {
		logger.info("callThdAndOprateLclCusAgt start ... ...");
		logger.info("=================context before insCusAgt callThd"
				+ context);
		context.setData("TransCode", "Add");
		String date = DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE);
		context.setData("regDat", date);
		context.setData("txnCod", "GASHXY");
		context.setData("objSvr", "STDHGAS1");

		context.setProcessId("gdeupsb.oprGasCusAgent1");

		Map<String, Object> tradeMap1 = callThdTradeManager.trade(context);
		if (BPState.isBPStateOvertime(context)) {
			throw new CoreException(ErrorCodes.TRANSACTION_ERROR_TIMEOUT);
		}
		context.setDataMap(tradeMap1);

		context.setData("optDat", date);
		context.setData("optNod", context.getData("NodNo"));
		logger.info("===oprGasCusAgentAction1=====context=" + context);

		context.setProcessId("gdeupsb.oprGasCusAgentAction");
		logger.info("===oprGasCusAgentAction=====context=" + context);

		if ("AddOK".equals(context.getData("TransCode").toString().trim())) {
			// 燃气协议表新增一条数据
			GdGasCusAll addGasCusAll = BeanUtils.toObject(context.getDataMap(),
					GdGasCusAll.class);
			addGasCusAll.setCusNo((String) context.getData(ParamKeys.CUS_NO));
			;
			addGasCusAll.setCusAc((String) context.getData(ParamKeys.CUS_AC));
			addGasCusAll.setCusNme((String) context.getData(ParamKeys.CUS_NME));
			addGasCusAll.setCusTyp((String) context.getData("cusTypBak"));
			addGasCusAll.setOptDat(date);
			addGasCusAll.setOptNod((String) context.getData(ParamKeys.BR));
			addGasCusAll.setIdTyp((String) context.getData(ParamKeys.ID_TYPE));
			addGasCusAll.setIdNo((String) context.getData(ParamKeys.ID_NO));
			addGasCusAll.setThdCusNme((String) context
					.getData(ParamKeys.THD_CUS_NME));
			addGasCusAll.setCmuTel((String) context.getData(ParamKeys.CMU_TEL));
			addGasCusAll.setThdCusAdr((String) context
					.getData(ParamKeys.THD_CUSTOMER_ADDR));
			get(GdGasCusAllRepository.class).insert(addGasCusAll);

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
			insCusInfo.setAccTyp((String) context.getData("cusTypBak"));
			insCusInfo.setOptDat(date);
			insCusInfo.setOptNod((String) context.getData(ParamKeys.BR)
					.toString().substring(2, 8));
			insCusInfo.setIdTyp((String) context.getData(ParamKeys.ID_TYPE));
			insCusInfo.setIdNo((String) context.getData(ParamKeys.ID_NO));
			insCusInfo.setThdCusNam((String) context
					.getData(ParamKeys.THD_CUS_NME));
			insCusInfo.setCmuTel((String) context.getData(ParamKeys.CMU_TEL));
			insCusInfo.setThdCusAdr((String) context
					.getData(ParamKeys.THD_CUSTOMER_ADDR));
			get(GdGasCusDayRepository.class).insert(insCusInfo);
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

		List<Map<String, Object>> customerInfoList = new ArrayList<Map<String, Object>>();
		Map<String, Object> customerInfoMap = setCustomerInfoMap(context);
		customerInfoList.add(customerInfoMap);
		context.setData("customerInfo", customerInfoList);

		logger.info("============context after setAgtCltAndCusInf :" + context);
	}

	private Map<String, Object> setCustomerInfoMap(Context context) {
		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("agtCllCusId", context.getData("agtCllCusId"));
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
		map.put("obkBk", context.getData(ParamKeys.BR));
		return map;
	}

	private Map<String, Object> setAgentCollectAgreementMap(Context context) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank((String) context.getData("agdAgrNo"))) {
			map.put("agdAgrNo", context.getData("agdAgrNo"));
		}
		map.put(ParamKeys.CUS_AC, context.getData(ParamKeys.CUS_AC));
		map.put("acoAc", context.getData(ParamKeys.CUS_AC));//TODO 暂用CUS_AC，待确认
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
		map.put(ParamKeys.BUS_TYP, "0");
		map.put(ParamKeys.BUSS_KIND, context.getData(ParamKeys.BUSS_KIND));
		map.put(ParamKeys.CCY, "CNY");
		map.put("cusFeeDerFlg", "0"); // TODO 暂用0，待确认
		map.put("agtSrvCusId", context.getData("agtSrvCusId"));
		map.put("agtSrvCusPnm", context.getData(ParamKeys.THD_CUS_NME));
		map.put("agrVldDte", context.getData("STR_AC_DTE"));
		map.put("agrExpDte", "99991231"); // YYYYMMDD默认最大日，99991231
		map.put(ParamKeys.CMU_TEL, context.getData(ParamKeys.CMU_TEL));
		map.put(ParamKeys.THD_CUS_NO, context.getData(ParamKeys.THD_CUS_NO));
		map.put("pedAgrSts", "0");
		map.put("cnlSts", "10000000000000000010");
		map.put("ageBr", context.getData(ParamKeys.BK));
		map.put("agrBr", context.getData(ParamKeys.BR));
		return map;
	}
}

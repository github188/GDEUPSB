package com.bocom.bbip.gdeupsb.action.gas;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsCusAgent;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.repository.EupsCusAgentRepository;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdGasCusDay;
import com.bocom.bbip.gdeupsb.repository.GdGasCusDayRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.service.support.BBIPAsyncMessageService;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 惠州燃气用户协议维护
 * @author WangMQ
 *
 */
public class OprGasCusAgentAction extends BaseAction{
	public static Logger logger = LoggerFactory.getLogger(OprGasCusAgentAction.class);
	
	public void execute(Context context) throws CoreException, CoreRuntimeException{
		logger.info("Enter in OprGasCusAgentAction!...........");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		
//		context.setData("thdTxnCde", "460701");
//		context.setData("eupsBusTyp", "PGAS00");
//		context.setData("comNo", "PGAS00");
		context.setData(ParamKeys.BK, "CNJT");
		context.setData(ParamKeys.CUS_NO, context.getData("thdCusNo"));
		logger.info("================now context =" + context);
		//TODO: 个人协议查询方法错误！！！！！
		Map<String, Object> cusInfoMap = new HashMap<String, Object>();
		cusInfoMap.put(ParamKeys.COMPANY_NO,  context.getData("comNo"));
		cusInfoMap.put(ParamKeys.BK, "CNJT");
		cusInfoMap.put(ParamKeys.CUS_NO, context.getData("thdCusNo"));
		cusInfoMap.put(ParamKeys.ID_NO, context.getData("idNo"));
		
		logger.info("=======交易类型(1-新增 2-修改 3-删除 4-查询):" + context.getData("authLvl"));
		
		if("4".equals(context.getData("optFlg"))){		//4:查询交易, 先进行用户签约状态的查询
//			select * from Gascusall491 where UserNo='%s' or idno='%s'
			
			Result accessObject = get(BGSPServiceAccessObject.class).callServiceFlatting("queryDetailAgentCollectAgreement", cusInfoMap);

			if(CollectionUtils.isEmpty(accessObject.getPayload())){			
				context.setData(GDParamKeys.GAS_MSG_TYP, "E");
				throw new CoreRuntimeException("该用户未签约");
			}
			logger.info("该用户已签约，可进行交易");
		}
		
		
		//前端授权，此处只检查
		if(!"4".equals(context.getData("optFlg"))){		//非查询交易要授权
			String authTlr = context.getData(ParamKeys.AUTHOR_LEVEL);
			if (StringUtils.isEmpty(authTlr)) {
				throw new CoreException("未授权，不能进行交易");
//				throw new CoreException(ErrorCodes.EUPS_CANCEL_CHECK_AUTH_FAIL);
			}
		}
	logger.info("已授权，可进行交易");
		//无需查询单位协议 now
//查询单位协议   SELECT TActNo,BBusTyp,CrpCod FROM savcrpagr where CAgtNo='%s' and SvrSts='1'      //comNo    useSts
//		EupsThdBaseInfo thdBaseInfo = BeanUtils.toObject(context.getDataMap(), EupsThdBaseInfo.class);
//		thdBaseInfo.setUseSts("0");
//		List<EupsThdBaseInfo> thdBaseInfoList = get(EupsThdBaseInfoRepository.class).find(thdBaseInfo);
//		if(CollectionUtils.isEmpty(thdBaseInfoList)){
//			context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
//			context.setData("rspmsg", "单位协议不存在");
//			throw new CoreRuntimeException("单位协议不存在");
//		}
//		logger.info("存在单位协议，可进行交易");

		
		
//		<Set>TActNo=491800012620190029499</Set>  
//	      <Set>@MSG.OIP=$HstIp</Set>  <!--第三方IP-->
//	      <Set>@MSG.OPT=$HstPrt</Set> <!--第三方PORT-->
		context.setData("tTxnNo", GDConstants.GAS_THD_ACT_NO);
//		context.setData("@MSG.OIP", context.getData("hstIp"));
//		context.setData("@MSG.OPT", context.getData("HstPrt"));
		
		//Test			Test	Test
//		context.setData("@MSG.OIP", "127.0.0.1");
//		context.setData("@MSG.OPT", "1201");
		
		context.setData("tCommd", "QryUser");
		context.setData("thdTxnCde", "GASHXX");
		
		context.setData("txnCod", "GASHXX");
		context.setData("objSvr", "STDHGAS1");
		//发燃气第三方，查看有无该用户编号信息
		logger.info("=============context=======" + context);
		
		logger.info("发燃气第三方，查看有无该用户编号信息");
		Map<String, Object> thdRspMsg = get(ThirdPartyAdaptor.class).trade(context);
		context.setDataMap(thdRspMsg);
		logger.info("======================context========" + context);
		System.out.println();
		System.out.println("~~~~~~~~~~~~~~~~~~~~"+thdRspMsg);
		//处理燃气返回的信息
		logger.info("==============rtnCod=" + context.getData("rtnCod") + "============opoptFlg=" +context.getData("optFlg"));
		if("QryUser".equals(context.getData("rtnCod").toString().trim())){
			if("1".equals(context.getData("optFlg"))){ 	// optFlg = 1 	新增
				Result accessObject = get(BGSPServiceAccessObject.class).callServiceFlatting("queryDetailAgentCollectAgreement", cusInfoMap);
				if(!CollectionUtils.isEmpty(accessObject.getPayload())){			
					context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
					context.setData("rspMsg", "该用户编号已经签约,无法新增签约!");
					throw new CoreRuntimeException("该用户编号已经签约,无法新增签约!");
				}
				logger.info("============accessObject=" + accessObject);
				context.setDataMap(accessObject.getPayload());
				context.setData("tCommd", "Add");
				logger.info("可以新增签约");
			}
			
			if("2".equals(context.getData("optFlg"))){ 	// optFlg = 2 	修改
				Result accessObject = get(BGSPServiceAccessObject.class).callServiceFlatting("queryDetailAgentCollectAgreement", cusInfoMap);

				if(CollectionUtils.isEmpty(accessObject.getPayload())){			
					context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
					context.setData("rspMsg", "该用户编号未签约,无法修改!");
					throw new CoreRuntimeException("该用户编号未签约,无法修改!");
				}
				context.setDataMap(accessObject.getPayload());
				context.setData("tCommd", "Edit");
				logger.info("============可以对签约协议进行修改");
			}
			
			if("3".equals(context.getData("optFlg"))){ 	// optFlg = 3	删除
				Result accessObject = get(BGSPServiceAccessObject.class).callServiceFlatting("queryDetailAgentCollectAgreement", cusInfoMap);

				if(CollectionUtils.isEmpty(accessObject.getPayload())){			
					context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
					context.setData("rspMsg", "该用户编号未签约,无法删除!");
					throw new CoreRuntimeException("该用户编号未签约,无法删除!");
				}
				context.setDataMap(accessObject.getPayload());
				context.setData("tCommd", "Stop");
				logger.info("===========可以删除");
			}
			
			String date = DateUtils.format(new Date(), DateUtils.STYLE_SIMPLE_DATE);
			if(!"4".equals(context.getData("optFlg"))){ 	// 非查询交易，送第三方
				//发起交易
				context.setData("thdTxnCde", "GASHXY");
				context.setData("regDat", date);
				context.setData("txnCod", "GASHXY");
				context.setData("objSvr", "STDHGAS1");
				
				logger.info("=========context=" + context);
				
				Map<String, Object> tradeMap1 = get(ThirdPartyAdaptor.class).trade(context);
				context.setDataMap(tradeMap1);
				
				context.setData("optDat", date);
				context.setData("optNod", context.getData("NodNo"));
				logger.info("========context=" + context);
				cusInfoMap = BeanUtils.toFlatMap(context.getDataMap());
				logger.info("===========cusInfoMap=" + cusInfoMap);
				if("AddOK".equals(context.getData("rtnCod").toString().trim())){	//第三方返回新增成功，协议表、动态协议表插入新增数据
					//代扣协议管理-修改和新增 maintainAgentCollectAgreement
					get(BGSPServiceAccessObject.class).callServiceFlatting("maintainAgentCollectAgreement", cusInfoMap);
					insertCusInfo(context);
					
					context.setData("msgTyp", "N");
					context.setData("rspCod", GDConstants.GDEUPSB_TXN_SUCC_CODE);
					context.setData("rspMsg", "新增成功");
					
					logger.info("============新增成功");
				}
				if("EditOK".equals(context.getData("rtnCod").toString().trim())){	 //第三方返回修改成功，更新协议表，动态协议表插入数据
					//代扣协议管理-修改和新增 maintainAgentCollectAgreement
					get(BGSPServiceAccessObject.class).callServiceFlatting("maintainAgentCollectAgreement", cusInfoMap);
					insertCusInfo(context);
					
					context.setData("msgTyp", "N");
					context.setData("rspCod", GDConstants.GDEUPSB_TXN_SUCC_CODE);
					context.setData("rspMsg", "修改成功");
					
					logger.info("============修改成功");
				}
				if("StopOK".equals(context.getData("rtnCod").toString().trim())){	////第三方返回删除成功，delete协议表相关数据，动态协议表插入数据
					//代扣协议管理-删除	deleteAgentCollectAgreement
					get(BGSPServiceAccessObject.class).callServiceFlatting("deleteAgentCollectAgreement", cusInfoMap);
					insertCusInfo(context);	
					
					context.setData("msgTyp", "N");
					context.setData("rspCod", GDConstants.GDEUPSB_TXN_SUCC_CODE);
					context.setData("rspMsg", "删除成功");
					
					logger.info("============删除成功");
				}
				if("Double".equals(context.getData("rtnCod").toString().trim())){		//燃气返回已存在协议
					context.setData("msgTyp", "E");
					context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
					context.setData("rspMsg", "已存在协议，请联系系统管理员或者燃气公司!");
					throw new CoreRuntimeException("已存在协议，请联系系统管理员或者燃气公司!");
				}
				if("AddNo".equals(context.getData("rtnCod").toString().trim()) || "EditNo".equals(context.getData("rtnCod").toString().trim()) || "StopNo".equals(context.getData("rtnCod").toString().trim())){	//燃气返回别的操作失败情况
					context.setData("msgTyp", "E");
					context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
					context.setData("rspMsg", "燃气方返回操作失败!");
					throw new CoreRuntimeException("燃气方返回操作失败!");
				}
			}
		}
		else if("NOUser".equals(context.getData("rtnCod").toString().trim())){  //NOUser 为无此用户（用户编码错误）
			context.setData("msgTyp", "E");
			context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
			context.setData("rspMsg", "无此用户或用户编码错误!");
			throw new CoreRuntimeException("无此用户或用户编码错误!");
		}
		else if("UserStop".equals(context.getData("rtnCod").toString().trim())){  //UserStop为此用户已报停
			context.setData("msgTyp", "E");
			context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
			context.setData("rspMsg", "此用户已报停!");
			throw new CoreRuntimeException("此用户已报停!");
		}
		else{	
			context.setData("msgTyp", "E");
			context.setData("rspCod", GDConstants.GAS_ERROR_CODE);
			context.setData("rspMsg", "系统错误!");
			throw new CoreRuntimeException("系统错误");
		}
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		
	}
	
	
	/**
	 * 无论增删改用户协议表，均向燃气每天动态协议表插入一条数据，  tCommd可表示增删改
	 * @param context
	 * @throws CoreException
	 * @throws CoreRuntimeException
	 */
	public void insertCusInfo(Context context) throws CoreException, CoreRuntimeException{
		GdGasCusDay insCusInfo = BeanUtils.toObject(context.getDataMap(), GdGasCusDay.class);
		String sqn = get(BBIPPublicService.class).getBBIPSequence();
		insCusInfo.setSequence(sqn);	
		get(GdGasCusDayRepository.class).insert(insCusInfo);
	}
}

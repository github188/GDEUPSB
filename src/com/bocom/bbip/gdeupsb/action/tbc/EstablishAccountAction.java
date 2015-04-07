package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.service.Result;
import com.bocom.bbip.service.Status;
import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdTbcBasInf;
import com.bocom.bbip.gdeupsb.entity.GdTbcCusAgtInfo;
import com.bocom.bbip.gdeupsb.repository.GdTbcBasInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdTbcCusAgtInfoRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 烟草开户
 * Date 2015-1-29
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class EstablishAccountAction extends BaseAction {
   
    @Autowired
    BGSPServiceAccessObject serviceAccess;
    @Autowired
    GdTbcCusAgtInfoRepository cusAgtInfoRepository;
    @Autowired
    BBIPPublicService publicService;
    
    @Override
     public void execute(Context context) throws CoreException {
        log.info("EstablishAccount Action start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        
        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData(ParamKeys.BK, context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        //上面公共报文头，下面报文体
        context.setData("custId", context.getData("CUST_ID"));
        context.setData("comId", context.getData("COM_ID"));
        context.setData("tCusNm", context.getData("CUST_NAME"));
        context.setData("cusTyp", context.getData("CUST_TYPE"));
        context.setData("pasTyp", context.getData("PASS_TYPE"));
        context.setData("pasId", context.getData("PASS_ID"));
        context.setData("liceId", context.getData("LICE_ID"));
        context.setData("accTyp", context.getData("ACC_TYPE"));
        context.setData("actNo", context.getData("ACC"));
        context.setData("pasFlg", context.getData("PASS_FLAG"));
        context.setData("pasWrd", context.getData("PASSWORD"));
        context.setData("addres", context.getData("ADDR"));
        context.setData("telNum", context.getData("TEL"));
        context.setData("devId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));

        GdTbcBasInf resultTbcBasInfo = get(GdTbcBasInfRepository.class).findOne(context.getData("dptId").toString());
        if (null == resultTbcBasInfo) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (resultTbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        }
        
        //构成网点号
        context.setData("comNum", resultTbcBasInfo.getDptNam());
        String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("dptId").toString());
        if (null == cAgtNo) {
            cAgtNo ="4410000560";
        }
        context.setData("cAgtNo", cAgtNo);
        String brNo = cAgtNo.substring(0,3)+"999";
        String nodNo = CodeSwitchUtils.codeGenerator("GDYC_nodSwitch",brNo);
        if (null == nodNo) {
            nodNo ="441800";
        }
        context.setData("nodNo",nodNo);
        // 获取电子柜员
        context.setData("tTxnCd","483803");
        context.setData("txnCtlSts", "0");
        //   检查该客户是否已签约
        log.info("向核心开户查询！");
        Map<String, Object> cusListMap = setAcpMap(context);
        cusListMap.put(ParamKeys.CUS_AC,context.getData("actNo"));
        context.setDataMap(cusListMap);
        Result accessObjList = serviceAccess.callServiceFlatting("queryListAgentCollectAgreement",cusListMap);
        System.out.println(accessObjList);
        GdTbcCusAgtInfo tbcCusAgtInfo = new GdTbcCusAgtInfo();
        tbcCusAgtInfo.setActNo(context.getData("actNo").toString());
        List<GdTbcCusAgtInfo> gdTbcAgtInfo = get(GdTbcCusAgtInfoRepository.class).find(tbcCusAgtInfo);
        if (CollectionUtils.isEmpty(gdTbcAgtInfo)) { 
        	setAgtCltAndCusInf(context);
        	context.setData("oprTyp", "0");
        	context.setData("agrChl", "01");
        	context.setData("bk", "01441800999");//TODO
        	context.setData("br", "01441999999");
        	context.setData("cusNme", context.getData("tCusNm"));
        	context.setData("ccy", "CNY");
        	context.setData("idTyp", context.getData("pasTyp"));
        	context.setData("idNo", context.getData("pasId"));
        	context.setData("reqTme",  new Date());
        	
               Result operateAcpAgtResult = serviceAccess.callServiceFlatting("maintainAgentCollectAgreement", context.getDataMap());
            log.info("===========respMap: " + operateAcpAgtResult.getPayload() + "===========");
            log.info("代收付系统接口调用增加协议");
            if (!operateAcpAgtResult.isSuccess()) {
                Throwable e = operateAcpAgtResult.getException();
                if (Status.SEND_ERROR == operateAcpAgtResult.getStatus()) {
                    context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                    context.setData(GDParamKeys.RSP_CDE,"9999");
                    context.setData(GDParamKeys.RSP_MSG,GDErrorCodes.TBC_COM_OTHER_ERROR);
                    log.error(GDErrorCodes.TBC_COM_OTHER_ERROR,e);
                    throw new CoreException(GDErrorCodes.TBC_COM_OTHER_ERROR);
                }
                // 连接错误或等待超时,但不知道是否已上送,这里交易已处于未知状态
                context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
                if (Status.TIMEOUT == operateAcpAgtResult.getStatus()) {
                    context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                    context.setData(GDParamKeys.RSP_CDE,"9999");
                    context.setData(GDParamKeys.RSP_MSG,GDErrorCodes.TBC_OUT_TIME_ERROR);
                    log.error(GDErrorCodes.TBC_OUT_TIME_ERROR,e);
                    throw new CoreException(GDErrorCodes.TBC_OUT_TIME_ERROR);
                }
            }
            //GDEUPS协议临时表添加数据
            GdTbcCusAgtInfo  cusAgtInfo =new  GdTbcCusAgtInfo();
            cusAgtInfo.setActNo(context.getData("actNo").toString());
            cusAgtInfo.setCustId(context.getData("custId").toString());
            cusAgtInfo.setCusNm(context.getData("tCusNm").toString());
            cusAgtInfo.setPasId(context.getData("pasId").toString());
            cusAgtInfo.setComId(context.getData("comId").toString());
            cusAgtInfo.setAccTyp(context.getData("accTyp").toString());
            cusAgtInfoRepository.insert(cusAgtInfo);
        } else {
        	setAgtCltAndCusInf(context);
        	context.setData("oprTyp", "0");
        	context.setData("agrChl", "01");
        	context.setData("bk", "01441800999");//TODO
        	context.setData("br", "01441999999");
        	context.setData("cusNme", context.getData("tCusNm"));
        	context.setData("ccy", "CNY");
        	context.setData("idTyp", context.getData("pasTyp"));
        	context.setData("idNo", context.getData("pasId"));
        	context.setData("reqTme",  new Date());
            Result result = serviceAccess.callServiceFlatting("maintainAgentCollectAgreement", context.getDataMap());
            log.info("===========respMap: " + result.getPayload() + "===========");
            if (!result.isSuccess()) {
                Throwable e = result.getException();
                if (Status.SEND_ERROR == result.getStatus()) {
                    context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                    context.setData(GDParamKeys.RSP_CDE,"9999");
                    context.setData(GDParamKeys.RSP_MSG,GDErrorCodes.TBC_COM_OTHER_ERROR);
                    log.error(GDErrorCodes.TBC_COM_OTHER_ERROR,e);
                    throw new CoreException(GDErrorCodes.TBC_COM_OTHER_ERROR);
                }
                // 连接错误或等待超时,但不知道是否已上送,这里交易已处于未知状态
                context.setState(BPState.BUSINESS_PROCESSNIG_STATE_UNKOWN_FAIL);
                if (Status.TIMEOUT == result.getStatus()) {
                    context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                    context.setData(GDParamKeys.RSP_CDE,"9999");
                    context.setData(GDParamKeys.RSP_MSG,GDErrorCodes.TBC_OUT_TIME_ERROR);
                    log.error(GDErrorCodes.TBC_OUT_TIME_ERROR,e);
                    throw new CoreException(GDErrorCodes.TBC_OUT_TIME_ERROR);
                 }
            }
            //GDEUPS协议临时表更改数据
            GdTbcCusAgtInfo  cusAgtInfo =new  GdTbcCusAgtInfo();
            cusAgtInfo.setActNo(context.getData("actNo").toString());
            cusAgtInfo.setCustId(context.getData("custId").toString());
            cusAgtInfo.setCusNm(context.getData("tCusNm").toString());
            cusAgtInfo.setPasId(context.getData("pasId").toString());
            cusAgtInfo.setComId(context.getData("comId").toString());
            cusAgtInfo.setAccTyp(context.getData("accTyp").toString());
            cusAgtInfoRepository.update(cusAgtInfo);
        }
        context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
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

		log.info("============context after setAgtCltAndCusInf :" + context);
	}


	private Map<String, Object> setAcpMap(Context context) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("traceNo", context.getData(ParamKeys.TRACE_NO));
		map.put("traceSrc", "GDEUPSB");
		map.put("version", context.getData(ParamKeys.VERSION));
		map.put("reqTme", new Date());
		map.put("reqJrnNo", get(BBIPPublicService.class).getBBIPSequence());
		map.put("reqSysCde","SGRT00");
		map.put("tlr","ABIR148");
		map.put("chn", context.getData(ParamKeys.CHANNEL));
		map.put("bk", "01441800999");
		map.put("br", "01441999999");
		return map;
	}

	private Map<String, Object> setCustomerInfoMap(Context context) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("agtCllCusId", context.getData(ParamKeys.THD_CUS_NO));
		map.put("cusTyp", context.getData("cusTyp"));
		map.put(ParamKeys.CUS_NO, context.getData("custId"));
		map.put(ParamKeys.CUS_AC, context.getData(ParamKeys.CUS_AC));
		map.put(ParamKeys.CUS_NME, context.getData("tCusNm"));
		map.put(ParamKeys.CCY, "CNY");
		map.put(ParamKeys.ID_TYPE, context.getData("pasTyp"));
		map.put("idNo", context.getData("pasId"));
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
		map.put(ParamKeys.CUS_AC, context.getData("actNo"));
		map.put("acoAc", context.getData(ParamKeys.CUS_AC));
		if (StringUtils.isNotBlank((String) context.getData("pasWrd"))) {
			map.put("pwd", (String) context.getData("pasWrd"));
		}
		map.put("bvCde", context.getData("bvCde"));
		if (StringUtils.isNotBlank((String) context.getData("bvNo"))) {
			map.put("bvNo", (String) context.getData("bvNo"));
		}
		map.put(ParamKeys.COMPANY_NO,"4410011485");//TODO
		map.put("comNum", context.getData("comNum"));

		map.put(ParamKeys.BUS_TYP, "0"); // TODO 0-代收； 暂用0，待确认0-代收,1-代付,2-代缴
											// 0-批量代收；1-批量代付；2-联机缴费；
		map.put(ParamKeys.BUSS_KIND, "A123");
		map.put(ParamKeys.CCY, "CNY");
		map.put("cusFeeDerFlg", "0"); // TODO 暂用0，待确认
		map.put("agtSrvCusId", context.getData("CUST_ID").toString());
		map.put("agtSrvCusPnm", context.getData(ParamKeys.THD_CUS_NME));
		map.put("agrVldDte",DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)); // YYYYMMDD默认当日
		map.put("agrExpDte", "99991231"); // YYYYMMDD默认最大日，99991231
		map.put(ParamKeys.CMU_TEL, context.getData("telNum"));
		map.put(ParamKeys.THD_CUS_NO, context.getData(ParamKeys.THD_CUS_NO));
		return map;
	}

}

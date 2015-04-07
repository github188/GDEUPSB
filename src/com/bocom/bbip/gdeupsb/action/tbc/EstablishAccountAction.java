package com.bocom.bbip.gdeupsb.action.tbc;

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
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
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
        String bankId = context.getData(ParamKeys.BK).toString();
        context.setData("tTxnCd","483803");

        context.setData("txnCtlSts", "0");
        //   检查该客户是否已签约
        log.info("向核心开户查询！");
        Map<String, Object> cusListMap =getMap();
        cusListMap.put(ParamKeys.CUS_AC,context.getData("actNo"));
        context.setDataMap(cusListMap);
        Result accessObjList = serviceAccess.callServiceFlatting("queryListAgentCollectAgreement",cusListMap);
        if (accessObjList.getPayload().get("").equals("")){}

        GdTbcCusAgtInfo tbcCusAgtInfo = new GdTbcCusAgtInfo();
        tbcCusAgtInfo.setActNo(context.getData("actNo").toString());
        List<GdTbcCusAgtInfo> gdTbcAgtInfo = get(GdTbcCusAgtInfoRepository.class).find(tbcCusAgtInfo);
        if (CollectionUtils.isEmpty(gdTbcAgtInfo)) { 
        	Map<String, Object> establishMap = getMap();
            establishMap.put("agrChl","01");
            establishMap.put("oprTyp", "0");
            establishMap.put("cusTyp","0");
            establishMap.put("cusNo",context.getData("custId"));
            establishMap.put("cusNme",context.getData("tCusNm"));
            establishMap.put("idNo",context.getData("pasId"));
            establishMap.put("idTyp",context.getData("pasTyp"));
            establishMap.put("ccy","CNY");
            establishMap.put("bvCde","007");
            establishMap.put("bvNo","12345678");
            establishMap.put("ourOthFlg","0");
            establishMap.put("obkBk","441999");
            establishMap.put("cmuTel",context.getData("telNum").toString());
            establishMap.put("comNo",cAgtNo);
            establishMap.put("cusAc",context.getData("actNo"));
            establishMap.put("acTyp","3");
            establishMap.put("bk", bankId);
            establishMap.put("busKnd", "A087");//busKnd
            establishMap.put("busTyp", "0");
            establishMap.put("cusFeeDerFlg", "0");
            establishMap.put("agtSrvCusId", context.getData("CUST_ID").toString());
            establishMap.put("agrVldDte", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
            establishMap.put("agrExpDte", "21151230");
            establishMap.put("br", context.getData(ParamKeys.BR));
    		
            Result operateAcpAgtResult = serviceAccess.callServiceFlatting("maintainAgentCollectAgreement", establishMap);
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
            Map<String, Object> establishMap = getMap();
            establishMap.put("agrChl","01");
            establishMap.put("oprTyp", "1");//0新增 1修改 
            establishMap.put("cusTyp","0");
            establishMap.put("cusNo",context.getData("custId"));
            establishMap.put("cusNme",context.getData("tCusNm"));
            establishMap.put("idNo",context.getData("pasId"));
            establishMap.put("idTyp",context.getData("pasTyp"));
            establishMap.put("ccy","CNY");
            establishMap.put("bvCde","007");
            establishMap.put("bvNo","12345678");
            establishMap.put("ourOthFlg","0");
            establishMap.put("obkBk",bankId);
            establishMap.put("cmuTel",context.getData("telNum").toString());
            establishMap.put("comNo",cAgtNo);
            establishMap.put("cusAc",context.getData("actNo"));
            establishMap.put("acTyp","3");
            establishMap.put("bk", bankId);
            establishMap.put("busKnd", "A087");
            establishMap.put("busTyp", "0");
            establishMap.put("cusFeeDerFlg", "0");
            establishMap.put("agtSrvCusId", context.getData("telNum").toString());
            establishMap.put("agrVldDte", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
            establishMap.put("agrExpDte", "21151230");
            establishMap.put("br", context.getData(ParamKeys.BR));
            Result result = serviceAccess.callServiceFlatting("maintainAgentCollectAgreement", establishMap);
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
    
    private Map<String, Object> getMap(){
        Map<String, Object> map =  new HashMap<String, Object>();
        map.put("traceNo", publicService.getTraceNo());
        map.put("traceSrc", "GDEUPSB");
        map.put("version","GDEUPSB-1.0.0");
        map.put("reqTme", new Date());
        map.put("reqJrnNo",  publicService.getBBIPSequence());
        map.put("reqSysCde", "SGRT00");
        map.put("tlr", "ABIR148");
        map.put("chn", "00");
        return map;
    }
    
    private Map<String, Object> setCustomerInfoMap(Context context) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("agtCllCusId", context.getData("cusNo"));
		map.put("agtSrvCusId", context.getData("cusNo"));
		map.put("agtSrvCusPnm", context.getData("settleAccountsName"));
		map.put("cusTyp", context.getData("cusTyp"));
		map.put("cusNo", context.getData(ParamKeys.CUS_NO));
		map.put("cusAc", context.getData("newCusAc"));
		map.put("cusNme", context.getData("newCusName"));
		map.put("ccy", "CNY");
		map.put("bvCde", "009");
		map.put("idTyp", context.getData(ParamKeys.ID_TYPE));
		map.put("idNo", context.getData(ParamKeys.ID_NO));
		//TODO
		map.put("bvCde", "009");
		
		if (StringUtils.isNotBlank((String) context.getData("bvNo"))) {
			map.put("bvNo", (String) context.getData("bvNo"));
		}
		map.put("ourOthFlg", "0");
		return map;
	}

	private Map<String, Object> setAgentCollectAgreementMap(Context context) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ParamKeys.BUS_TYP, "0");
		map.put("cusNo", context.getData("cusNo"));
		map.put("agtCllCusId", context.getData("cusNo"));
		map.put("cusNme", context.getData("cusNme"));
		map.put("agtSrvCusId", context.getData("cusNo"));
		map.put("bvCde", "009");
		map.put("agtSrvCusPnm", context.getData("settleAccountsName"));
		map.put("cusAc", context.getData(GDParamKeys.NEWCUSAC));
		map.put("acoAc", context.getData(GDParamKeys.NEWCUSAC));
		if (StringUtils.isNotBlank((String) context.getData("pwd"))) {
			map.put("pwd", (String) context.getData("pwd"));
		}
		map.put("bvCde", context.getData("bvCde"));
		map.put("buyTyp", "0");
		map.put("busKnd", "A089");
		map.put("agrVldDte", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		map.put("agrExpDte", "99991231");
		map.put("agrTlr",context.getData(ParamKeys.TXN_TLR));
		//TODO 
		map.put("agtSrvCusPnm",context.getData("thdCusNme"));
		map.put("agtSrvCusId",context.getData("cusNo"));
		
		map.put("comNo", context.getData(ParamKeys.COMPANY_NO));
		EupsThdBaseInfo baseInfo = new EupsThdBaseInfo();
		baseInfo.setEupsBusTyp((String) context.getData(ParamKeys.EUPS_BUSS_TYPE));
		if (StringUtils.isNotBlank((String) context.getData(ParamKeys.COMPANY_NO))) {
			baseInfo.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
		}
		//TODO 调用代收付接口  获得单位名称
//		String comNme=get(EupsThdBaseInfoRepository.class).findOne(context.getData("comNo").toString()).getComNme();
//		map.put("comNum", comNme);
//		context.setData("comNum", comNme);
		
		map.put(ParamKeys.CCY, "CNY");
		// TODO 暂用0，待确认
		map.put("cusFeeDerFlg", "0"); 
		map.put("agtSrvCusId", context.getData("agtSrvCusId"));
		map.put("agtSrvCusPnm", context.getData(ParamKeys.THD_CUS_NME));
		// YYYYMMDD默认当日
		map.put("agrVldDte",DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd)); 
		// YYYYMMDD默认最大日，99991231
		map.put("agrExpDte", "99991231"); 
		map.put("idNo", context.getData(ParamKeys.ID_NO));
		map.put(ParamKeys.CMU_TEL, context.getData(ParamKeys.CMU_TEL));
		return map;

	}
	/**
	 * 列表查询
	 */
	public String  selectList(Context context) throws CoreException{
		String cusAc=context.getData("cusAc").toString();
		//列表查询 获得协议编号
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("cusAc", cusAc);
		//header 设定
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
		map.put("cusAc", context.getData("cusAc"));
		log.info("~~~~~~~~~~requestHeader~~~~map~~~~~ "+map);
		log.info("~~~~~~~~~~列表查询开始 ");
		//上代收付取协议编号
		Result accessObjList = serviceAccess.callServiceFlatting("queryListAgentCollectAgreement",map);
		log.info("~~~~~~~~~~列表查询结束~~~~"+accessObjList);
		List<Map<String,Object>> list=(List<Map<String, Object>>)accessObjList.getPayload().get("agentCollectAgreement");
		String agdAgrNo=list.get(0).get("agdAgrNo").toString();
		log.info("~~~~~~~~~~~~~~~协议编号： "+agdAgrNo);
		context.setData(ParamKeys.CUS_AC, context.getData(GDParamKeys.NEWCUSAC));
		context.setData("agdAgrNo", agdAgrNo);
		return agdAgrNo;
	}
}

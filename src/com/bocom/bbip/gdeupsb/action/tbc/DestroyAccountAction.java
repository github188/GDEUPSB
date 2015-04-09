package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.service.Status;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 烟草销户
 * Date 2015-1-13
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class DestroyAccountAction extends BaseAction {
   
    @Autowired
    BGSPServiceAccessObject serviceAccess;
    @Autowired
    BBIPPublicService publicService;
    @Autowired
    GdTbcCusAgtInfoRepository cusAgtInfoRepository;
    
    @Override
     public void execute(Context context) throws CoreException {
        log.info("DestroyAccount Action start!...");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        //上面公共报文头，下面报文体
        context.setData("custId", context.getData("CUST_ID"));
        context.setData("cusNam", context.getData("CUST_NAME"));
        context.setData("cusTyp", context.getData("CUST_TYPE"));
        context.setData("pasId", context.getData("PASS_ID"));
        context.setData("liceId", context.getData("LICE_ID"));
        context.setData("accTyp", context.getData("ACC_TYPE"));
        context.setData("actNo", context.getData("ACC"));
        context.setData("devId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));

        //检查系统签到状态
        GdTbcBasInf resultTbcBasInfo = get(GdTbcBasInfRepository.class).findOne(context.getData("dptId").toString());
        if (null == resultTbcBasInfo) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (resultTbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_SIGNOUT)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_OUT);
        }
        //客户签约状态
        GdTbcCusAgtInfo tbcCusAgtInfo = new GdTbcCusAgtInfo();
        tbcCusAgtInfo.setActNo(context.getData("actNo").toString());
        List<GdTbcCusAgtInfo> gdTbcAgtInfo = get(GdTbcCusAgtInfoRepository.class).find(tbcCusAgtInfo);
        if (CollectionUtils.isEmpty(gdTbcAgtInfo)){
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"账号已注销!！！");
            return; 
        }
        
        //检查用户名是否匹配
        String cusNme = context.getData("cusNam").toString().trim();
        String tCusNm = gdTbcAgtInfo.get(0).getCusNm().trim();
        if (!cusNme.equals(tCusNm)) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"客户姓名不符!！！");
            return;
        }

        String pasId = context.getData("pasId").toString().trim();
        String idNo =gdTbcAgtInfo.get(0).getPasId().trim();
        if (!idNo.equals(pasId)) {
        	log.info("身份证号码不符，当前context中的pasId="+pasId+",数据库中记录的pasId="+idNo);
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"身份证号码不符!！！");
            return;
        }
        String actNo = context.getData("actNo").toString().trim();
        String cusAc =gdTbcAgtInfo.get(0).getActNo().trim();
        if (!cusAc.equals(actNo)) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"卡/账户号码不符!！！");
            return;
        }

        selectList(context);
        Result result = serviceAccess.callServiceFlatting("deleteAgentCollectAgreement", context.getDataMap());
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
        //GDEUPS协议临时表删除数据
        cusAgtInfoRepository.delete(context.getData("custId").toString());
        context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
        context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }

    /**
	 * 列表查询
	 */
	@SuppressWarnings("unchecked")
	public String  selectList(Context context) throws CoreException{

		//列表查询 获得协议编号
		Map<String, Object> map=new HashMap<String, Object>();
		//header 设定
		map.put("traceNo", context.getData(ParamKeys.TRACE_NO));
		map.put("traceSrc", context.getData(ParamKeys.TRACE_SOURCE));
		map.put("version", context.getData(ParamKeys.VERSION));
		map.put("reqTme", new Date());
		map.put("reqJrnNo", get(BBIPPublicService.class).getBBIPSequence());
		map.put("reqSysCde", context.getData(ParamKeys.REQ_SYS_CDE));
		map.put("tlr", context.getData(ParamKeys.TELLER));
		map.put("chn", context.getData(ParamKeys.CHANNEL));
		map.put("bk", "01441999999");//TODO
		map.put("br", "01441800999");
		map.put("cusAc", context.getData("actNo"));
		log.info("~~~~~~~~~~requestHeader~~~~map~~~~~ "+map);
		context.setDataMap(map);
		log.info("~~~~~~~~~~列表查询开始 ");
		//上代收付取协议编号
		Result accessObjList = serviceAccess.callServiceFlatting("queryListAgentCollectAgreement",map);
		log.info("~~~~~~~~~~列表查询结束~~~~"+accessObjList);
		List<Map<String,Object>> list=(List<Map<String, Object>>)accessObjList.getPayload().get("agentCollectAgreement");
		String agdAgrNo=list.get(0).get("agdAgrNo").toString();
		log.info("~~~~~~~~~~~~~~~协议编号： "+agdAgrNo);
		List<String> agdAgrNoList=new ArrayList<String>();
		agdAgrNoList.add(agdAgrNo);
		context.setData("agdAgrNo", agdAgrNoList);
		return agdAgrNo;
	}
}

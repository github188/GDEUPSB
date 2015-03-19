package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CathecticPreExecuteAction extends BaseAction{

    @Autowired
    CommonLotAction commonLotAction;
    @Autowired
    BBIPPublicService publicService;
    @Override
    public void execute(Context context) throws CoreException {
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        //<!-- 检查当前是否有可用奖期，如果没有则下载一次，下载后再检查一次，如果没有则返回错误 -->
        //获取福彩时间
        String lotTime = commonLotAction.getFcTim( context.getData(ParamKeys.BR).toString());
        //codingSwitch判断是福彩还是快乐十分
        String gameId = context.getData("gameId").toString();
        String isKeno = CodeSwitchUtils.codeGenerator("IsKenoChg", gameId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isKeno",isKeno);
        map.put("gameId", gameId);
        map.put("lotTime",lotTime);

        List<GdLotDrwTbl> lotDrwInf;
        try {
        lotDrwInf = get(GdLotDrwTblRepository.class).qryLotDrwInf(map);
        } catch(Exception e) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"数据库异常！！");
            context.setData("tzCod", "TZ9002");
            return;
        }
        
        if (CollectionUtils.isEmpty(lotDrwInf)) {
            // 没有奖期信息，执行奖期下载 -- 
            try {
                get(BBIPPublicService.class).synExecute("gdeups.downloadPrizeInfo", context);
            } catch (Exception e){
                log.info("下载奖期信息异常!!!");
                context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                context.setData(ParamKeys.RSP_CDE,"LOT999");
                context.setData(ParamKeys.RSP_MSG,"下载奖期信息异常");
                context.setData("tzCod", "TZ9002");
                return;
            }
            //下载奖期信息成功后，再次检查是否有符合的奖期
            List<GdLotDrwTbl> lotDrwInfTwice = get(GdLotDrwTblRepository.class).qryLotDrwInf(map);
            if (CollectionUtils.isEmpty(lotDrwInfTwice)) {
                context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                context.setData(ParamKeys.RSP_CDE,"LOT999");
                context.setData(ParamKeys.RSP_MSG,"不在购买时间（无可用奖期）");
                context.setData("tzCod", "TZ9003");
                return;
               
            }
        }
        // 登记购彩记录(commit)
        String gameIdDesc = CodeSwitchUtils.codeGenerator("GameIdDescChg", gameId);
        //获取流水号
        String sqn = publicService.getBBIPSequence();
        context.setData("sqn",sqn);
        context.setData("txnAmt",context.getData("betAmt"));
        context.setData("tTxnCd",context.getData("231"));
        context.setData("txnCod",context.getData("485412"));
        context.setData("schTit","直接投注"); //codeswitching 中只有一个 直接投注
        context.setData("gamNam",gameIdDesc);
        Date date =new Date();
        String dateString = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);
        String dateTimeString = DateUtils.format(date, DateUtils.STYLE_yyyyMMddHHmmss);
        context.setData("betDat",dateString);
        context.setData("txnTim",dateTimeString);
        context.setData("txnLog",context.getData("selVal"));
        context.setData("schId","");
        context.setData("tLogNo","");
        context.setData("cipher","");
        context.setData("verify","");
        context.setData("hTxnCd","471140");
        context.setData("hTxnSb","002");
        context.setData("hLogNo","");
        context.setData("hResCd","");
        context.setData("hTxnSt","U");
        context.setData("tRspCd","");
        context.setData("tTxnSt","U");
        context.setData("thdChk","0");
        context.setData("tChkNo","00000000000");
        context.setData("chkTim","");
        context.setData("chkFlg","0");
        context.setData("awdFlg","0");
        context.setData("awdRtn","0");
        context.setData("cAgtNo",context.getData("dSCAgtNo"));//TODO; 商户协议编号 
        context.setData("tckNo","0");
        context.setData("lChkTm","");
        context.setData("txnSts","U");
        GdLotTxnJnl lotTxnJnl= BeanUtils.toObject(context.getDataMap(), GdLotTxnJnl.class);
        try {
            get(GdLotTxnJnlRepository.class).insert(lotTxnJnl);
        }catch (Exception e){
            log.info("新增购彩流水异常!!!");
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"新增购彩流水异常");
            context.setData("tzCod", "TZ9005");
            return;
        }
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}

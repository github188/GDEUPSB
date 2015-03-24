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
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CathecticPreExecuteAction extends BaseAction{

    @Autowired
    CommonLotAction commonLotAction;
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
        GdLotTxnJnl lotTxnJnl= new GdLotTxnJnl();
        String brNo = context.getData(ParamKeys.BK).toString();      
        String cityNo =CodeSwitchUtils.codeGenerator("SubNod2CityId", brNo.substring(2,5));
        if (StringUtil.isEmptyOrNull(cityNo)) {
            cityNo="1";
        }
        lotTxnJnl.setCityId(cityNo);
        lotTxnJnl.setBrNo(brNo);
        lotTxnJnl.setDrawId(context.getData("drawId").toString());
        lotTxnJnl.setKenoId(context.getData("kenoId").toString());
        lotTxnJnl.setGameId(gameId);
        
        String playId = context.getData("playId");//如果为空则为一
        if (StringUtil.isEmptyOrNull(playId)) {
            playId="1";
        }
        lotTxnJnl.setPlayId(playId);
        lotTxnJnl.setTxnAmt((String)context.getData("betAmt"));
        lotTxnJnl.settTxnCd("231");
        lotTxnJnl.setTxnCod("485412");
        lotTxnJnl.setSchTit("直接投注");//codeswitching 中只有一个 直接投注
        lotTxnJnl.setGamNam(gameIdDesc);
        lotTxnJnl.setBetMod((String)context.getData("betMod"));
        lotTxnJnl.setBetMet((String)context.getData("betMet"));
        lotTxnJnl.setBetMul((String)context.getData("betMul"));
        lotTxnJnl.setBetLin((String)context.getData("betLin"));
       // lotTxnJnl.setLotNam(context.getData("lotNam").toString());//TODO 是否在登录时候  所得
        lotTxnJnl.setLotNam("13162036230");
        Date date =new Date();
        String dateString = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);
        String dateTimeString = DateUtils.format(date, DateUtils.STYLE_yyyyMMddHHmmss);
        lotTxnJnl.setBetDat(dateString);
        lotTxnJnl.setTxnTim(dateTimeString);
       // lotTxnJnl.setTxnLog(get(BBIPPublicService.class).getBBIPSequence());
        lotTxnJnl.setTxnLog("selVal");
        lotTxnJnl.setSchTyp("1");
        lotTxnJnl.setSecLev("1");
        
        lotTxnJnl.setSchId("1212121");//TODO系统生成的方案编号
        lotTxnJnl.settLogNo(lotTxnJnl.getTxnLog());
        lotTxnJnl.setCipher("");
        lotTxnJnl.setVerify("");
        lotTxnJnl.sethTxnCd("471140");
        lotTxnJnl.sethTxnSb("002");
        lotTxnJnl.sethTxnSt("U");
        lotTxnJnl.sethLogNo("");
        lotTxnJnl.sethRspCd("");
        lotTxnJnl.settRspCd("");
        lotTxnJnl.settTxnSt("U");
        lotTxnJnl.setThdChk("0");
        lotTxnJnl.settChkNo("00000000000");
        lotTxnJnl.setChkTim("");
        lotTxnJnl.setChkFlg("0");
        lotTxnJnl.setAwdFlg("0");
        lotTxnJnl.setAwdRtn("0");
        lotTxnJnl.setTxnSts("U");
     
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

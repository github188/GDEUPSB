package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CathecticPreExecuteAction extends BaseAction{

    CommonLotAction commonLotAction =new CommonLotAction();
    
    @Override
    public void execute(Context context) throws CoreException {
       
        //<!-- 检查当前是否有可用奖期，如果没有则下载一次，下载后再检查一次，如果没有则返回错误 -->
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isKeno", context.getData("isKeno"));
        map.put("gameId", context.getData("gameId"));
        map.put("lotTime", context.getData("lotTime"));
        //map.put("isKeno", "Y"); 测试
        // map.put("gameId", "5");
        // map.put("lotTime", "20150111223344");
        
        GdLotDrwTbl lotDrwInf =new GdLotDrwTbl();
        try {
        lotDrwInf= get(GdLotDrwTblRepository.class).qryLotDrwInf(map);
        } catch(Exception e) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"数据库异常！！");
            context.setData("tzCod", "TZ9002");
            return;
        }
        
        if (null == lotDrwInf) {
            // 没有奖期信息，执行奖期下载 -- 
            try {
                context = get(BBIPPublicService.class).synExecute("485441", context); //TODO; 调用奖期下载 
            } catch (Exception e){
                log.info("下载奖期信息异常!!!");
                context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                context.setData(ParamKeys.RSP_CDE,"LOT999");
                context.setData(ParamKeys.RSP_MSG,"下载奖期信息异常");
                context.setData("tzCod", "TZ9002");
                return;
            }
            //下载奖期信息成功后，再次检查是否有符合的奖期
            GdLotDrwTbl lotDrwInfTwice = get(GdLotDrwTblRepository.class).qryLotDrwInf(map);
            if (null ==lotDrwInfTwice) {
                context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                context.setData(ParamKeys.RSP_CDE,"LOT999");
                context.setData(ParamKeys.RSP_MSG,"不在购买时间（无可用奖期）");
                context.setData("tzCod", "TZ9003");
                return;
               
            }
        }
        //TODO; PUB:CodeSwitching
        /* <!-- 登记购彩记录(commit) -->
        <Exec func="PUB:CodeSwitching">
            <Arg name="DatSrc" value="OPFCSW"/>
            <Arg name="SourNam" value="SchTyp"/>
            <Arg name="DestNam" value="SchTypDesc"/>
            <Arg name="TblNam" value="SchTypDescChg"/>
        </Exec>
        syso
        <Exec func="PUB:CodeSwitching">
            <Arg name="DatSrc" value="OPFCSW"/>
            <Arg name="SourNam" value="GameId"/>
            <Arg name="DestNam" value="GameIdDesc"/>
            <Arg name="TblNam" value="GameIdDescChg"/>
        </Exec>*/
        //获取流水号
        //getTxnLogNo();
        context.setData("txnAmt",context.getData("betAmt"));
        context.setData("tTxnCd",context.getData("231"));
        context.setData("txnCod",context.getData("485412"));
        context.setData("schTit",context.getData("schTypDesc")); //codeswitching
        context.setData("gamNam",context.getData("gameIdDesc"));//codeswitching
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
    }
}

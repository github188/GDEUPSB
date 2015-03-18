package com.bocom.bbip.gdeupsb.action.lot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotChkCtl;
import com.bocom.bbip.gdeupsb.entity.GdLotChkDtl;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GdLotChkCtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotChkDtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 *  福彩对账
 * @version 1.0.0
 * Date 2015-02-02
 * @author GuiLin.Li
 */
public class DeductMoneyAction extends BaseAction {

    @Override
    public void execute(Context context) throws CoreException {
        log.info("DeductMoneyAction start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        context.setData("curTim",DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
        GdLotChkCtl lotChkCtl = new GdLotChkCtl();
        String gameId = context.getData("gameId").toString();
        String drawId = context.getData("drawId").toString();
        lotChkCtl.setGameId(gameId);
        lotChkCtl.setDrawId(drawId);
        List<GdLotChkCtl> lotChkCtls = new ArrayList<GdLotChkCtl>();
        try {
            lotChkCtls = get(GdLotChkCtlRepository.class).find(lotChkCtl);
        } catch (Exception e) {
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "查询对账信息异常!");
            return;
        }
        CommonLotAction commAction = new CommonLotAction();
        if (CollectionUtils.isEmpty(lotChkCtls)) {
            //无信息则下载对账信息 
            context.setData(ParamKeys.FILE_TYPE,"3");
            commAction.downloadFile(context);
            List<GdLotChkCtl> gdLotChkCtls =  get(GdLotChkCtlRepository.class).find(lotChkCtl);
            if (CollectionUtils.isEmpty(gdLotChkCtls)) {
                context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
                context.setData(ParamKeys.RSP_CDE, "LOT999");
                context.setData(ParamKeys.RSP_MSG, "无对账信息!");
                return;
            }
        }
        
        GdLotTxnJnl lotTxnJnl = new GdLotTxnJnl();
        lotTxnJnl.setGameId(gameId);
        lotTxnJnl.setDrawId(drawId);
        lotTxnJnl.setChkFlg("0");
        lotTxnJnl.setChkTim("");
        try {
            get(GdLotTxnJnlRepository.class).updateByGameIdAndDrawId(lotTxnJnl);
        } catch(Exception e){
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "对账更新状态失败!");
            return;
        }
        
        GdLotChkDtl  lotChkDtl = new GdLotChkDtl();
        lotChkDtl.setGameId(gameId);
        lotChkDtl.setDrawId(drawId);
        lotChkDtl.setChkFlg("0");
        lotChkDtl.setChkTim("");
        try {
            get(GdLotChkDtlRepository.class).updateByGameIdAndDrawId(lotChkDtl);
        } catch(Exception e){
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "对账更新状态失败!");
            return;
        }
        //  对购彩记录进行对账   --(对账成功)
        GdLotTxnJnl gdLotTxnJnl = new GdLotTxnJnl();
        gdLotTxnJnl.setGameId(gameId);
        gdLotTxnJnl.setDrawId(drawId);
        gdLotTxnJnl.setChkTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
        try {
            get(GdLotTxnJnlRepository.class).updateMatchLotTxnJnl(gdLotTxnJnl);
        } catch(Exception e){
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "对账更新状态失败!");
            return;
        }
        //对对账明细进行对账
        GdLotChkDtl  gdLotChkDtl = new GdLotChkDtl();
        gdLotChkDtl.setGameId(gameId);
        gdLotChkDtl.setDrawId(drawId);
        gdLotChkDtl.setChkTim(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
      
        try {
            get(GdLotChkDtlRepository.class).updateMatchLotChkDtl(gdLotChkDtl);
        } catch(Exception e){
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "对账更新状态失败!");
            return;
        }
        //  更新我方多账的状态 --"对购彩记录进行对账(对账失败)
        try {
            get(GdLotTxnJnlRepository.class).updateUnMatchLotTxnJnl(gdLotTxnJnl);
        } catch(Exception e){
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "对账更新状态失败!");
            return;
        }
        //  <!-- 判断是否对账成功，如果是则更新奖期表的对账标志并通知购彩总金额 -->
        //<!-- 判断对账明细中是否还有未对账的 -->
        int txnJnlUnCheckNum =get(GdLotTxnJnlRepository.class).statLotTxnJnlUnChk(gdLotTxnJnl);
        int chkDtlUnCheckNum = get(GdLotChkDtlRepository.class).statLotChkDtlUnChk(gdLotChkDtl);
        context.setData("chkTim",DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
        if (txnJnlUnCheckNum==0 && chkDtlUnCheckNum ==0) {
            try {
                List<GdLotChkCtl> gdLotChkCtls =  get(GdLotChkCtlRepository.class).find(lotChkCtl);
                BigDecimal totAmt = new BigDecimal(0.0);
                BigDecimal totNum = new BigDecimal(0.0);
                for (GdLotChkCtl eupslotChkCtl : gdLotChkCtls) {
                    totAmt = totAmt.add(BigDecimal.valueOf(Double.valueOf(eupslotChkCtl.getTotAmt())));
                    totNum = totAmt.add(BigDecimal.valueOf(Double.valueOf(eupslotChkCtl.getTotNum())));
                }
                context.setData("totAmt", totAmt);
                context.setData("totNum", totNum);
            } catch (Exception e) {
                context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
                context.setData(ParamKeys.RSP_CDE, "LOT999");
                context.setData(ParamKeys.RSP_MSG, "统计购彩总金额失败!");
                return;
            }
            context.setData("chkFlg", "2");
        } else {
            context.setData("chkFlg", "1");
            context.setData("totAmt", "");
        }
        //更新对账控制表中对应的对账信息
        GdLotChkCtl lotChkCtlInput = new GdLotChkCtl();
        lotChkCtlInput.setChkFlg(context.getData("chkFlg").toString());
        lotChkCtlInput.setChkTim(context.getData("chkTim").toString());
        lotChkCtlInput.setGameId(gameId);
        lotChkCtlInput.setDrawId(drawId);
        try {
            get(GdLotChkCtlRepository.class).update(lotChkCtlInput);
        } catch (Exception e) {
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "对账更新状态失败!");
            return;
        }
        //更新奖期表中对应的对账信息
        GdLotDrwTbl lotDrwTbl = new GdLotDrwTbl();
        lotDrwTbl.setTotAmt(context.getData("totAmt").toString());
        lotDrwTbl.setChkFlg(context.getData("chkFlg").toString());
        lotDrwTbl.setChkTim(context.getData("chkTim").toString());
        lotDrwTbl.setGameId(gameId);
        lotDrwTbl.setDrawId(drawId);
        try {
            get(GdLotDrwTblRepository.class).update(lotDrwTbl);
        } catch (Exception e) {
            context.setData("msgTyp", Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE, "LOT999");
            context.setData(ParamKeys.RSP_MSG, "对账更新状态失败!");
            return;
        }
        //生成对账报表  代码为空
        //TODO; 计算轧差 
        commAction.calcLotDifAmt(context);
        context.setData("msgTyp", Constants.RESPONSE_TYPE_SUCC);
        context.setData(ParamKeys.RSP_CDE, Constants.RESPONSE_CODE_SUCC);
        context.setData(ParamKeys.RSP_MSG, "对账完成，请检查差错报表!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }
}

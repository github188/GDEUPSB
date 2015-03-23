package com.bocom.bbip.gdeupsb.action.tbc;

import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdTbcBasInf;
import com.bocom.bbip.gdeupsb.repository.GdTbcBasInfRepository;
import com.bocom.bbip.utils.*;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 烟草方签到  8910
 * Date 2015-1-12
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class SignInAction extends BaseAction {

    @Override
    public void execute(Context context) throws CoreException {
        log.info("SignInAction Action start!");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        //转换
        context.setData("txnTme", context.getData("TRAN_TIME"));
        context.setData("bk", context.getData("BANK_ID"));
        context.setData("dptId", context.getData("DPT_ID"));
        context.setData("devId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));

     
      
        GdTbcBasInf resultTbcBasInfo = get(GdTbcBasInfRepository.class).findOne(context.getData("dptId").toString());
        if (resultTbcBasInfo == null) {
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"你提供的数据不存在!");
            return;
        } 
        if (resultTbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_SIGNIN)) {
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"第三方渠道已签到!");
            return;
        } else if (resultTbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_CHECKBILL_ING)) {
            context.setData(GDParamKeys.RSP_CDE,"9999");
            context.setData(GDParamKeys.RSP_MSG,"不是签到时间不允许第三方签到!");
            return;
        } else {
            GdTbcBasInf tbcBasInfo = BeanUtils.toObject(context.getDataMap(), GdTbcBasInf.class);
            tbcBasInfo.setSigSts(Constants.TXN_CTL_STS_SIGNIN);
            Date date =DateUtils.parse(context.getData("txnTme").toString().substring(0,8), DateUtils.STYLE_yyyyMMdd);
            tbcBasInfo.setTranDt(DateUtils.format(date, DateUtils.STYLE_yyyyMMdd));
   
            // 根据默认算法生成一个密钥
            Date timeStp = new Date();
            String mainkey = DateUtils.format(timeStp, DateUtils.STYLE_yyyyMMddHHmmss);
            if (null !=resultTbcBasInfo.getComKey()) {
                mainkey = resultTbcBasInfo.getComKey() + mainkey;
            }
            tbcBasInfo.setComKey(mainkey);
            
            //;加密秘钥
           // String readyMD5 = buildReadyStr(context);
           // String beforeMacChk =GDEUPSConstants.EUPS_TBC_BLANK, macChk =GDEUPSConstants.EUPS_TBC_BLANK;
            if (StringUtils.isNotEmpty(mainkey)) {
                //TODO;加密
                /*try {
                    //得到主密钥
                    mainkey = mainkey.trim();
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(readyMD5.getBytes());
                     // 待加密的字符串生成MD5码
                    String afterMD5 = Hex.encode(md.digest());
                    beforeMacChk = afterMD5.substring(0, 16);
                    // 用主密钥Des加密转成MD5码的串
                    String mac = CryptoUtils.desEncrpty(afterMD5, mainkey);
                    macChk = CryptoUtils.desEncrpty(beforeMacChk, Hex.encode("00000000".getBytes()));
                    context.setData(ParamKeys.MACKEY, mac.substring(0, 16));
                    context.setData(ParamKeys.MACCHK, macChk.substring(0, 8));
    
                    log.info("makeTbcRspFile start!..");
                    String newKey = mac.substring(0, 16);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(GDEUPSParamKeys.EUPS_TBC_TRANKEY, newKey);
                    String DatFileName = "key/" + eupsThdTranCtlInfo.getComNo() + ".key";
                    EupsThdFtpConfig eupsThdFtpConfig = context.getData(ParamKeys.CONSOLE_THD_FTP_CONFIG_LIST);
                    eupsThdFtpConfig.setLocFleNme(DatFileName);
                    eupsThdFtpConfig.setRmtFleNme(DatFileName);

                    get(OperateFileAction.class).createCheckFile(eupsThdFtpConfig, "TBCMainKeyFileFormat", DatFileName, map);
                    log.info("create file end!..");
                    // 更新数据库签到签退状态
                    get(EupsThdTranCtlInfoRepository.class).update(eupsThdTranCtlInfo);
                    context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
                } catch (NoSuchAlgorithmException e) {
                    throw new CoreException(ErrorCodes.EUPS_GEN_KEY_ERROR);
                }*/
                get(GdTbcBasInfRepository.class).update(tbcBasInfo);
                } else {
                throw new CoreException("主密钥不存在 !!");
            }
            context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
            context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
            context.setData("NEW_KEY", mainkey);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        }
    }
}
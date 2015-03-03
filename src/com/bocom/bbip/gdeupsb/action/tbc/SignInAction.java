package com.bocom.bbip.gdeupsb.action.tbc;


import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
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
        context.setData("DevId", context.getData("DEV_ID"));
        context.setData("teller", context.getData("TELLER"));
        
        String cAgtNo = CodeSwitchUtils.codeGenerator("GDYC_DPTID",  context.getData("dptId").toString());
        if (null == cAgtNo) {
            cAgtNo ="441";
        }
        String comNo = cAgtNo.substring(0,3)+"999";
        EupsThdTranCtlInfo eupsThdTranCtlInfo = BeanUtils.toObject(context.getDataMap(), EupsThdTranCtlInfo.class);
        EupsThdTranCtlInfo resultThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(comNo);
        if (resultThdTranCtlInfo == null) {
            throw new CoreException(ErrorCodes.THD_CHL_NOT_FOUND);
        } 
        if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_SIGNIN)) {
            throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_IN);
        } else if (resultThdTranCtlInfo.getTxnCtlSts().equals(Constants.TXN_CTL_STS_CHECKBILL_ING)) {
            throw new CoreException(ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
        } else {

            eupsThdTranCtlInfo.setTxnCtlSts(Constants.TXN_CTL_STS_SIGNIN);
            // 根据默认算法生成一个密钥
            Date timeStp = new Date();
            String mainkey = DateUtils.format(timeStp, DateUtils.STYLE_yyyyMMddHHmmss);
            if (null !=resultThdTranCtlInfo.getManKey()) {
                mainkey = resultThdTranCtlInfo.getManKey() + mainkey;
            }
            //;加密秘钥
           // String readyMD5 = buildReadyStr(context);
           // String beforeMacChk =GDEUPSConstants.EUPS_TBC_BLANK, macChk =GDEUPSConstants.EUPS_TBC_BLANK;
            if (StringUtils.isNotEmpty(mainkey)) {
                //TODO;加密
          /* try {
                    //得到主密钥
                    mainkey = mainkey.trim();
                    MessageDigest md = MessageDigest.getInstance(GDEUPSConstants.EUPS_TBC_MD5);
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
    
                    // 生成对账文件到指定路径
                    get(OperateFileAction.class)
                            .createCheckFile(eupsThdFtpConfig, "TBCMainKeyFileFormat", DatFileName, map);
                    log.info("create file end!..");
                    // 更新数据库签到签退状态
                    get(EupsThdTranCtlInfoRepository.class).update(eupsThdTranCtlInfo);
                    context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
                } catch (NoSuchAlgorithmException e) {
                    throw new CoreException(ErrorCodes.EUPS_GEN_KEY_ERROR);
                }*/
            } else {
                throw new CoreException("主密钥不存在 !!");
            }
            context.setData(GDParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
            context.setData(GDParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
        }
    }

    /**
     * 根据MAC加区域编号，渠道编号，交易时间生成签到待加密的字符串
     * @param para
     * @return
     */
   /* private String buildReadyStr(Context context) {
        String strDate = DateUtils.formatAsMMddHHmmss(new Date());
        String strPreifx = "MAC";
        StringBuffer beforeMAC = new StringBuffer();
        beforeMAC.append(strPreifx).append(context.getData(ParamKeys.THD_REGION_NO))
                .append(context.getData(ParamKeys.TXN_CHL)).append(strDate);
        return beforeMAC.toString();
    }*/

}
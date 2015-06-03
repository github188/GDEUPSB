package com.bocom.bbip.gdeupsb.action.tbc;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdTbcBasInf;
import com.bocom.bbip.gdeupsb.repository.GdTbcBasInfRepository;
import com.bocom.bbip.utils.*;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 烟草方签到 8910 Date 2015-1-12
 * 
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class SignInAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException {
		log.info("SignInAction Action start!");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		// 转换
		context.setData("txnTme", context.getData("TRAN_TIME"));
		context.setData("bk", context.getData("BANK_ID"));
		context.setData("dptId", context.getData("DPT_ID"));
		context.setData("devId", context.getData("DEV_ID"));
		context.setData("teller", context.getData("TELLER"));
		
		//获得加密密码
		GdTbcBasInf resultTbcBasInfo = get(GdTbcBasInfRepository.class)
				.findOne(context.getData("dptId").toString());
		if (resultTbcBasInfo == null) {
			context.setData(GDParamKeys.RSP_CDE, "9999");
			context.setData(GDParamKeys.RSP_MSG, GDErrorCodes.TBC_OFF_NOT_EXIST);
			throw new CoreException(GDErrorCodes.TBC_OFF_NOT_EXIST);
		}
//		if (resultTbcBasInfo.getSigSts().equals(Constants.TXN_CTL_STS_SIGNIN)) {
//			context.setData(GDParamKeys.RSP_CDE, "9999");
//			context.setData(GDParamKeys.RSP_MSG,
//					ErrorCodes.THD_CHL_ALDEAY_SIGN_IN);
//			throw new CoreException(ErrorCodes.THD_CHL_ALDEAY_SIGN_IN);
//		} else if (resultTbcBasInfo.getSigSts().equals(
//				Constants.TXN_CTL_STS_CHECKBILL_ING)) {
//			context.setData(GDParamKeys.RSP_CDE, "9999");
//			context.setData(GDParamKeys.RSP_MSG,
//					ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
//			throw new CoreException(ErrorCodes.THD_CHL_SIGNIN_NOT_ALLOWWED);
//		} else {
			GdTbcBasInf tbcBasInfo = BeanUtils.toObject(context.getDataMap(),
					GdTbcBasInf.class);
			tbcBasInfo.setSigSts(Constants.TXN_CTL_STS_SIGNIN);
			Date date = DateUtils.parse(context.getData("txnTme").toString()
					.substring(0, 8), DateUtils.STYLE_yyyyMMdd);
			tbcBasInfo.setTranDt(DateUtils.format(date,
					DateUtils.STYLE_yyyyMMdd));

			// 根据默认算法生成一个密钥
			Date timeStp = new Date();
//			String mainkey = DateUtils.format(timeStp,
//					DateUtils.STYLE_yyyyMMddHHmmss);
			
			String mainkey = DateUtils.format(timeStp,"yyMMddHHmmss");//by zds
			
			if (null != resultTbcBasInfo.getComKey()) {
				mainkey = resultTbcBasInfo.getComKey() + mainkey;
			}
			
			String newKey = getMD5Code(mainkey).substring(0, 16);//by zds		
			context.setData("NEW_KEY", newKey);
			
			//生成通讯密钥文件
			
			// tbcBasInfo.setComKey(mainkey);
			// ;加密秘钥
			// String readyMD5 = buildReadyStr(context);
			// String beforeMacChk =GDEUPSConstants.EUPS_TBC_BLANK, macChk
			// =GDEUPSConstants.EUPS_TBC_BLANK;
			if (StringUtils.isNotEmpty(mainkey)) {
				// TODO;加密
				/*
				 * try { //得到主密钥 mainkey = mainkey.trim(); MessageDigest md =
				 * MessageDigest.getInstance("MD5");
				 * md.update(readyMD5.getBytes()); // 待加密的字符串生成MD5码 String
				 * afterMD5 = Hex.encode(md.digest()); beforeMacChk =
				 * afterMD5.substring(0, 16); // 用主密钥Des加密转成MD5码的串 String mac =
				 * CryptoUtils.desEncrpty(afterMD5, mainkey); macChk =
				 * CryptoUtils.desEncrpty(beforeMacChk,
				 * Hex.encode("00000000".getBytes()));
				 * context.setData(ParamKeys.MACKEY, mac.substring(0, 16));
				 * context.setData(ParamKeys.MACCHK, macChk.substring(0, 8));
				 * 
				 * log.info("makeTbcRspFile start!.."); String newKey =
				 * mac.substring(0, 16); Map<String, Object> map = new
				 * HashMap<String, Object>();
				 * map.put(GDEUPSParamKeys.EUPS_TBC_TRANKEY, newKey); String
				 * DatFileName = "key/" + eupsThdTranCtlInfo.getComNo() +
				 * ".key"; EupsThdFtpConfig eupsThdFtpConfig =
				 * context.getData(ParamKeys.CONSOLE_THD_FTP_CONFIG_LIST);
				 * eupsThdFtpConfig.setLocFleNme(DatFileName);
				 * eupsThdFtpConfig.setRmtFleNme(DatFileName);
				 * 
				 * get(OperateFileAction.class).createCheckFile(eupsThdFtpConfig,
				 * "TBCMainKeyFileFormat", DatFileName, map);
				 * log.info("create file end!.."); // 更新数据库签到签退状态
				 * get(EupsThdTranCtlInfoRepository
				 * .class).update(eupsThdTranCtlInfo);
				 * context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL); }
				 * catch (NoSuchAlgorithmException e) { throw new
				 * CoreException(ErrorCodes.EUPS_GEN_KEY_ERROR); }
				 */
				get(GdTbcBasInfRepository.class).update(tbcBasInfo);
			} else {
				throw new CoreException(GDErrorCodes.TBC_MAIN_KEY_NOT_EXIST);
			}
			context.setData(GDParamKeys.RSP_CDE, GDConstants.TBC_RESPONSE_CODE_SUCC);
			context.setData(GDParamKeys.RSP_MSG, Constants.RESPONSE_MSG);
			
			context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
//		}
	}
	
    public static String getMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }
    
    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
    
    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }
    
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
}
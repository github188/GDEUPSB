package com.bocom.bbip.gdeupsb.action.hscard;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CryptoUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.util.Hex;

/**
 * 华商一卡通签退
 * 
 * @author lhx
 * 
 */
public class HScardSignInAction extends BaseAction {
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		context.setData(ParamKeys.TXN_TYP, Constants.SIGN_SET_TYPE_SIGNIN);
		Assert.hasLengthInData(context, ParamKeys.COMPANY_NO, ErrorCodes.EUPS_FIELD_EMPTY, "单位编号");
		Assert.hasLengthInData(context, ParamKeys.TXN_TYP, ErrorCodes.EUPS_FIELD_EMPTY, "交易状态");
		
		String DCC_ID_NO = context.getData("DCC_ID_NO").toString().trim();
		context.setData("DCC_ID_NO", DCC_ID_NO);
		// String comNo = "";

		/*
		 * 渠道签到函数(根据主密钥生成工作秘钥)
		 */
		EupsThdTranCtlInfo eupsThdTranCtlInfo = BeanUtils.toObject(context.getDataMap(), EupsThdTranCtlInfo.class);
		EupsThdTranCtlInfo resultThdTranCtlInfo = get(EupsThdTranCtlInfoRepository.class).findOne(eupsThdTranCtlInfo.getComNo());
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
			String readyMD5 = buildReadyStr(context);
			String mainkey = resultThdTranCtlInfo.getManKey();// "535A5F424F434F4D";
			String beforeMacChk = "", macChk = "";
			// context.getData("");
			if (StringUtils.isNotEmpty(mainkey)) {
				try {
					// 得到主密钥
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
					eupsThdTranCtlInfo.setWrkKey(mac.substring(0, 16));
				} catch (NoSuchAlgorithmException e) {
					throw new CoreException(ErrorCodes.EUPS_GEN_KEY_ERROR);
				}
			}
			eupsThdTranCtlInfo.setTxnDte(new Date());
			eupsThdTranCtlInfo.setTxnTme(new Date());
			get(EupsThdTranCtlInfoRepository.class).update(eupsThdTranCtlInfo);
		}
	}

	/**
	 * 根据MAC加区域编号，渠道编号，交易时间生成签到待加密的字符串
	 * 
	 * @param para
	 * @return
	 */
	private String buildReadyStr(Context context) {
		String strDate = DateUtils.formatAsMMddHHmmss(new Date());
		String strPreifx = "MAC";
		StringBuffer beforeMAC = new StringBuffer();
		beforeMAC.append(strPreifx).
				append(context.getData(ParamKeys.THD_REGION_NO)).
				append(context.getData(ParamKeys.TXN_CHL)).append(strDate);
		return beforeMAC.toString();
	}
}

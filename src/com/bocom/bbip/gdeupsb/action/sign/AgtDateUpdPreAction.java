package com.bocom.bbip.gdeupsb.action.sign;

import java.io.File;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.repository.GdsRunCtlRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 签约一站通 -协议数据更新-获取配置参数（GetRunCtl）
 * 
 * @author qc.w
 * 
 */
public class AgtDateUpdPreAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("AgtDateUpdPreAction start!..");

		GdsRunCtlRepository gdsRunCtlRepository = get(GdsRunCtlRepository.class);

		String gdsBid = context.getData(GDParamKeys.SIGN_STATION_BID); // 业务类型

		GdsRunCtl gdsRunCtl = gdsRunCtlRepository.findOne(gdsBid);
		if (null == gdsRunCtl) {
			throw new CoreException(GDErrorCodes.EUPS_SIGN_GDSBID_NOT_EXIST);
		}
		context.setVariable(GDParamKeys.SIGN_STATION_RUN_CTL_INFO, gdsRunCtl); // 将签约控制信息表信息放到context中，便于后续取值

		// 从远程服务器取文件到本地服务器
		String fileNme = context.getData("filNam"); // 文件名称

		String br = context.getData(ParamKeys.BR);// 机构号
		String tlr = context.getData(ParamKeys.TELLER); // 柜员号

		// 设置ftp参数
//		EupsThdFtpConfig eupsThdFtpConfig = get(EupsThdFtpConfigRepository.class).findOne("frtUsbWrtSgnFilGet");
//		String rmtWay = "/home/weblogic/JumpServer/WEB-INF/save/tfiles/" + br + "/" + tlr + "/";
//		eupsThdFtpConfig.setRmtWay(rmtWay);
//		eupsThdFtpConfig.setRmtFleNme(fileNme);
//		eupsThdFtpConfig.setLocFleNme(fileNme);
//
//		get(OperateFTPAction.class).getFileFromFtp(eupsThdFtpConfig);
		
		// 按照RCV44101 读取广州自来水处理结果文件
		EupsThdFtpConfig eupsThdFtpConfig = get(EupsThdFtpConfigRepository.class).findOne("watrAgtResult");
		
		try {
			get(BBIPPublicService.class).getFileFromBBOS(new File(eupsThdFtpConfig.getLocDir(),fileNme), fileNme, MftpTransfer.FTYPE_NORMAL);			
		}catch (Exception e) {
			throw new CoreException(ErrorCodes.EUPS_MFTP_FILEDOWN_FAIL);
		}

		log.info("get usb file from usb,rmt dir=["  + "]+,rmt file=[" + fileNme + "],local dir=[" + eupsThdFtpConfig.getLocDir()
				+ "],local filename=[" + fileNme + "]");

		log.info("AgtDateUpdPreAction end,start to do impl!..");
	}

}

//package com.bocom.bbip.gdeupsb.action;
//
//import java.util.Date;
//import java.util.List;
//
//import com.bocom.bbip.eups.action.BaseAction;
//import com.bocom.bbip.eups.action.common.OperateFTPAction;
//import com.bocom.bbip.eups.common.Constants;
//import com.bocom.bbip.eups.common.ErrorCodes;
//import com.bocom.bbip.eups.common.ParamKeys;
//import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
//import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
//import com.bocom.bbip.eups.entity.EupsThdTranCtlInfo;
//import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
//import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
//import com.bocom.bbip.gdeupsb.common.GDConstants;
//import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
//import com.bocom.bbip.gdeupsb.common.GDParamKeys;
//import com.bocom.bbip.utils.CollectionUtils;
//import com.bocom.bbip.utils.StringUtils;
//import com.bocom.jump.bp.core.Context;
//import com.bocom.jump.bp.core.CoreException;
//import com.bocom.jump.bp.core.CoreRuntimeException;
//
///**
// * 获取批扣文件，有手工发起及自动发起两种方式,手工发起需要在context中含有batchFileName，自动发起需要有第三方ftp循环信息:
// * 手动发起的判断批量控制表中是否已有当前文件数据，有则报错；自动发起的批扣，不做文件是否已导入判断，需要自行在实现中判断！ batchFleFtpList
// * 
// * @author qc.w
// * 
// */
//public class BatchFileDealGetFileAction extends BaseAction {
//
//	@Override
//	public void execute(Context context) throws CoreException, CoreRuntimeException {
//		log.info("BatchFileDealGetFileAction start!..");
//
//		// 根据是否含有文件名判断是手工发起的还是定时发起的
//		String fileName = context.getData(GDParamKeys.BATCH_COM_FILE_NAME);
//		String dealTyp = null;
//		List<EupsThdFtpConfig> autoThdFtpList = null;
//
//		if (StringUtils.isNotEmpty(fileName)) {
//			dealTyp = GDConstants.COM_BATCH_DEAL_TYP_PE; // 手工
//
//			// 判断批量控制表中是否已有当前文件数据，有则报错
//			EupsBatchConsoleInfo batchInfo = new EupsBatchConsoleInfo();
//			batchInfo.setFleNme(fileName);
//			batchInfo.setExeDte(new Date());
//			List<EupsBatchConsoleInfo> batchList = get(EupsBatchConsoleInfoRepository.class).find(batchInfo);
//			if (CollectionUtils.isNotEmpty(batchList)) {
//				throw new CoreException(ErrorCodes.EUPS_BATCH_DATA_IS_SUBMIT); // 数据已导入
//			}
//
//		} else {
//			// 若是自动发起的，则通过读取context中存在的第三方配置信息表信息循环取文件
//			autoThdFtpList = context.getData(GDParamKeys.BATCH_COM_AUTO_FILE_FTP_LIST);
//			if (CollectionUtils.isNotEmpty(autoThdFtpList)) {
//				dealTyp = GDConstants.COM_BATCH_DEAL_TYP_AU; // 自动
//			} else {
//				throw new CoreException(GDErrorCodes.EUPS_COM_BAT_FILE_ERROR);
//			}
//		}
//
//		// 检查单位签到签退状态
//		String comNo = context.getData(ParamKeys.COMPANY_NO);
//		EupsThdTranCtlInfo comInfo = get(EupsThdTranCtlInfoRepository.class).findOne(comNo);
//		if (null != comInfo) {
//			if (Constants.SIGN_SET_TYPE_SIGNIN.equals(comInfo.getTxnCtlSts())) {
//				log.info("com check-in status check success!");
//			} else {
//				throw new CoreException(ErrorCodes.EUPS_THD_TRAN_CTL_STS_ERR);
//			}
//		}
//		getBatActFile(context, dealTyp, fileName, autoThdFtpList); // 获取文件-将文件保存至本地
//
//		//新增一条批次信息
//		EupsBatchConsoleInfo eupsBatchConsoleInfo=new EupsBatchConsoleInfo();
//		eupsBatchConsoleInfo.setBatSts(Constants.BATCH_STATUS_U);
//		eupsBatchConsoleInfo.setComNo(comNo);
//		eupsBatchConsoleInfo.setComNme((String)context.getData(ParamKeys.COMPANY_NAME));
//		eupsBatchConsoleInfo.setRapTyp(Constants.TXN_RAP_TYPE_COLLECTION);  //代收
//		eupsBatchConsoleInfo.setTxnMde(Constants.TXN_MDE_FILE);  //文件方式
//		
////		get(EupsBatchConsoleInfoRepository.class).insert(arg0);
//		
//		context.setData(GDParamKeys.COM_BATCH_DEAL_TYP, dealTyp);
//	}
//
//	/**
//	 * 获取第三方批扣文件
//	 * 
//	 * @param context
//	 * @param dealTyp
//	 * @throws CoreException
//	 * @throws CoreRuntimeException
//	 */
//	private void getBatActFile(Context context, String dealTyp, String fileName, List<EupsThdFtpConfig> autoThdFtpList) throws CoreRuntimeException,
//			CoreException {
//
//		// TODO:判断本地是否存在该文件信息，若存在则报错
//		if (GDConstants.COM_BATCH_DEAL_TYP_PE.equals(dealTyp)) { // 手工
//			EupsThdFtpConfig eupsThdFtpInf = context.getData(GDParamKeys.COM_BATCH_DEAL_FTP_INF);
//			eupsThdFtpInf.setLocFleNme(fileName); // 为防止数据库中所存文件名不规范，此处使用传进来的文件名
//
//			log.info("start get batch file: now thdftp info=" + eupsThdFtpInf.toString());
//			get(OperateFTPAction.class).getFileFromFtp(eupsThdFtpInf);
//		}
//		// 注意：自动发起的批扣，不做文件判断，需要自行在实现中判断！
//		else if (GDConstants.COM_BATCH_DEAL_TYP_AU.equals(dealTyp)) {
//			for (EupsThdFtpConfig eupsThdFtpInf : autoThdFtpList) {
//				log.info("start get batch file: now thdftp info=" + eupsThdFtpInf.toString());
//				get(OperateFTPAction.class).getFileFromFtp(eupsThdFtpInf);
//			}
//		} else {
//			throw new CoreException(GDErrorCodes.EUPS_COM_BAT_FILE_ERROR);
//		}
//
//	}
//
//}

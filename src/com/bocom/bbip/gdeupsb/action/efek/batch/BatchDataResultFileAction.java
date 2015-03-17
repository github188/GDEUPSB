package com.bocom.bbip.gdeupsb.action.efek.batch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsEleTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsEleTmpRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class BatchDataResultFileAction implements AfterBatchAcpService{
	private final static Log logger=LogFactory.getLog(BatchDataResultFileAction.class);
	@Autowired
	OperateFileAction operateFile;
	@Autowired
	OperateFTPAction operateFTP;
	@Autowired
	GDEupsBatchConsoleInfoRepository gdeupsBatchConsoleInfoRepository;
	@Autowired
	GDEupsEleTmpRepository gdEupsEleTmpRepository;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	/**
	 * 南方电网 结果文件处理
	 */
	public void afterBatchDeal(AfterBatchAcpDomain afterbatchacpdomain, Context context) throws CoreException {
		logger.info("===============Start  BatchDataResultFileAction  afterBatchDeal");	
			
			String batNo=context.getData(ParamKeys.BAT_NO).toString();
			GDEupsBatchConsoleInfo  gdeupsBatchConsoleInfo = gdeupsBatchConsoleInfoRepository.findOne(batNo);
			//更改控制表
			GDEupsBatchConsoleInfo gdEupsBatchConsoleInfoUpdate=updateInfo(context, gdeupsBatchConsoleInfo);
			//文件名
			String fileName=gdeupsBatchConsoleInfo.getFleNme();
			EupsThdFtpConfig eupsThdFtpConfig=eupsThdFtpConfigRepository.findOne("elecBatch");
			// 生成文件
			try{
					Map<String, Object> resultMap=createFileMap(context,gdEupsBatchConsoleInfoUpdate);
					operateFile.createCheckFile(eupsThdFtpConfig, "efekBatchResulf", fileName, resultMap);
			}catch(CoreException e){
					logger.info("~~~~~~~~~~~Error  Message",e);
			}
			// 将生成的文件上传至指定服务器
			eupsThdFtpConfig.setLocFleNme(fileName);
			eupsThdFtpConfig.setRmtFleNme(fileName);
			operateFTP.putCheckFile(eupsThdFtpConfig);
			logger.info("===============End  BatchDataResultFileAction  afterBatchDeal");	
		}
	/**
	 * 把信息保存到控制表
	 */
	public GDEupsBatchConsoleInfo  updateInfo(Context context,GDEupsBatchConsoleInfo gdeupsBatchConsoleInfo){
			logger.info("===============Start  BatchDataResultFileAction  updateInfo");	
			Integer totCnt=Integer.parseInt(gdeupsBatchConsoleInfo.getTotCnt().toString());
			//成功笔数
			Integer sucTotCnt=Integer.parseInt(context.getData(GDParamKeys.SUC_TOT_CNT).toString());
			gdeupsBatchConsoleInfo.setSucTotCnt(sucTotCnt);
			//失败笔数
			Integer falTotCnt=totCnt-sucTotCnt;
			gdeupsBatchConsoleInfo.setFalTotCnt(falTotCnt);
			
			BigDecimal totAmt=gdeupsBatchConsoleInfo.getTotAmt();
			//成功总金额
			BigDecimal sucTotAmt=new BigDecimal(context.getData("sucTotAmt").toString());
			gdeupsBatchConsoleInfo.setSucTotAmt(sucTotAmt);
			//失败总金额
			BigDecimal falTotAmt=totAmt.subtract(sucTotAmt);
			gdeupsBatchConsoleInfo.setFalTotAmt(falTotAmt);
			
			//更新批次表
			gdeupsBatchConsoleInfoRepository.updateConsoleInfo(gdeupsBatchConsoleInfo);
			logger.info("===============End  BatchDataResultFileAction  updateInfo");	
			return gdeupsBatchConsoleInfo;
	}
	/**
	 * 拼装map文件
	 */
	public Map<String, Object> createFileMap(Context context,GDEupsBatchConsoleInfo gdEupsBatchConsoleInfoUpdate){
			logger.info("===============Start  BatchDataResultFileAction  createFileMap");	
			//代收付文件内容
			List<Map<String, Object>> mapList=context.getVariable(ParamKeys.EUPS_FILE_DETAIL);
			List<GDEupsEleTmp> gdEupsEleTmpList = gdEupsEleTmpRepository.findAllOrderBySqn();
			//内容主体
			List<GDEupsEleTmp> list=new ArrayList<GDEupsEleTmp>();
			for(int i=0;i<mapList.size();i++){
						GDEupsEleTmp gdEupsEleTmp=gdEupsEleTmpList.get(i);
						Map<String, Object> map=mapList.get(i);
						//<!--客户账号|姓名|金额|代理服务客户标识|代理服务客户姓名|本行标志|开户银行|备注一|备注二|状态|描述  -->
						gdEupsEleTmp.setTxnAmt(new BigDecimal(map.get("TXNAMT").toString()));
						gdEupsEleTmp.setPaymentResult(map.get("sts").toString());
						gdEupsEleTmp.setBankNo(gdEupsEleTmp.getSqn());
						//TODO 
						Date date=new Date();
						gdEupsEleTmp.setRsvFld1(DateUtils.format(date, DateUtils.STYLE_yyyyMMdd));
						gdEupsEleTmp.setRsvFld2(DateUtils.formatAsHHmmss(date));
						gdEupsEleTmp.setBakFld(map.get("RMK1").toString());
						list.add(gdEupsEleTmp);
			}
			Map<String, Object> resultMap=new HashMap<String, Object>(); 
			resultMap.put(ParamKeys.EUPS_FILE_HEADER, BeanUtils.toMap(gdEupsBatchConsoleInfoUpdate));
			resultMap.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(list));
			logger.info("===============End  BatchDataResultFileAction  createFileMap");	
			return null;
	}
}

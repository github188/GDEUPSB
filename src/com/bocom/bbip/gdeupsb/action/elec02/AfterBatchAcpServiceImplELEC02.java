package com.bocom.bbip.gdeupsb.action.elec02;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.support.CallThdService;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsBatchConsoleInfo;
import com.bocom.bbip.eups.entity.EupsBatchInfoDetail;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsBatchConsoleInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GDEupsbElecstBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbElecstBatchTmpRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * @author wuyh
 * @date 2015-3-07 上午09:06:35
 * @Company TD
 */
public class AfterBatchAcpServiceImplELEC02 extends BaseAction implements AfterBatchAcpService {
	private static final Log logger = LogFactory.getLog(AfterBatchAcpServiceImplELEC02.class);

	@Autowired
	GDEupsbElecstBatchTmpRepository gdEupsbElecstBatchTmpRepository;
	@Autowired
	EupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
	@Autowired
	GDEupsBatchConsoleInfoRepository gdeupsBatchConsoleInfoRepository;
	@Override
	public void afterBatchDeal(AfterBatchAcpDomain arg0, Context context)throws CoreException {
		logger.info("电力返盘文件处理开始");
//		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).afterBatchProcess(context);
		((BatchFileCommon)get(GDConstants.BATCH_FILE_COMMON_UTILS)).eupsBatchConSoleInfoAndgdEupsBatchConSoleInfo(context);
		Map<String,Object>ret=new HashMap<String,Object>();
        final List<EupsBatchInfoDetail> result=(List<EupsBatchInfoDetail>)context.getVariable("detailList");
        Assert.isNotEmpty(result, ErrorCodes.EUPS_QUERY_NO_DATA);
        
        
        //TODO 代收付处理产生的sqn必定比临时表中的sqn后，若返回文件用户顺序需与来盘文件一致
        //应该将代收付批扣处理后的数据更新进临时表，然后用临时表的该批次数据生成返回第三方文件  
//        同一批次存在同一个客户多条数据  e.g：
//        00000000500820080802                      301         445900191847000013202           郭燕光                                  594         2                                        050702  2014091 郭鸿铭                                  445006040010149018423           490         104         1    1248922894      
//        00000000510820080802                      301         445900191847000013202           郭燕光                                  520         2                                        050702  2014101 郭鸿铭                                  445006040010149018423           420         100         1    1252792281      
        //明细表中的RMK1对应临时表sqn 解决批次信息排序问题
        String tmpSqn = null;
         String sts = null;
         String errMsg = null; 
        for(EupsBatchInfoDetail dtl : result){
        	GDEupsbElecstBatchTmp elec02batchTmp = new GDEupsbElecstBatchTmp();
        	
        	tmpSqn = dtl.getRmk1();
        	sts = dtl.getSts();
        	
        	elec02batchTmp.setSqn(tmpSqn);
        	
        	elec02batchTmp.setRsvFld15(sts);//TODO
        	
        	/** TODO
        	if("S".equals(sts)){
        		elec02batchTmp.setRsvFld15("1");
        	}else{
        		errMsg = dtl.getErrMsg();
        		elec02batchTmp.setRsvFld16(errMsg);
            	//TODO 根据errMsg确定rsvFld15,回盘用！
//            	1-已扣
//            	2-余额不足
//            	3-帐号不符
//            	4-帐号已销
//            	5-坏帐号及其他, 除“已扣”，其他扣不到款
//            	8-直接借记支付中的金额超过事先规定限额
//            	9-直接借记无授权记录
        		
        		
        	}
*/
        	
        	gdEupsbElecstBatchTmpRepository.update(elec02batchTmp);
        }
        
        //更新临时表后取数据组成回盘文件
        
        String batNo=context.getData(ParamKeys.BAT_NO).toString();
		EupsBatchConsoleInfo eupsBatchConsoleInfo=eupsBatchConsoleInfoRepository.findOne(batNo);
		String sqns=eupsBatchConsoleInfo.getRsvFld2();
		GDEupsBatchConsoleInfo  Info=new GDEupsBatchConsoleInfo();
		Info.setRsvFld7(sqns);
		GDEupsBatchConsoleInfo  gdeupsBatchConsoleInfo = gdeupsBatchConsoleInfoRepository.find(Info).get(0);
		String gdBatNo = gdeupsBatchConsoleInfo.getBatNo();
        
        List<GDEupsbElecstBatchTmp> tempList = gdEupsbElecstBatchTmpRepository.findByBatNo(gdBatNo);
        Assert.isNotEmpty(tempList, ErrorCodes.EUPS_QUERY_NO_DATA);
        
        /**
        //把代扣协议不存在的添加到代收付返回的数据中去 begin
        String batNo = (String) context.getData("batNo");
        GDEupsbElecstBatchTmp tmp = new GDEupsbElecstBatchTmp();
        tmp.setBatNo(batNo);
        tmp.setRsvFld12("1");
        List<GDEupsbElecstBatchTmp>tmpList = get(GDEupsbElecstBatchTmpRepository.class).find(tmp);
        for (GDEupsbElecstBatchTmp batchTmp : tmpList) {
        	EupsBatchInfoDetail detail = new EupsBatchInfoDetail();
        	detail.setBatNo(batchTmp.getBatNo());
        	detail.setCusAc(batchTmp.getCusAc());
        	detail.setCusNme(batchTmp.getCusNme());
        	detail.setAgtSrvCusId(batchTmp.getThdCusNo());
        	detail.setAgtSrvCusNme(batchTmp.getThdCusNme());
        	detail.setSts("X");
        	detail.setTxnAmt(new BigDecimal(batchTmp.getTxnAmt()));
        	detail.setErrMsg("不存在代扣协议");
        	result.add(detail);
        }
        //把代扣协议不存在的添加到代收付返回的数据中去 end
        
        Assert.isNotEmpty(result, ErrorCodes.EUPS_QUERY_NO_DATA);
        EupsThdFtpConfig config=get(EupsThdFtpConfigRepository.class).findOne("elec02batch");
		Assert.isFalse(null == config, ErrorCodes.EUPS_THD_FTP_CONFIG_NOTEXIST);
        List<Map<String,String>>resultMap=(List<Map<String, String>>) BeanUtils.toMaps(result);
		List <GDEupsbElecstBatchTmp>lt=get(GDEupsbElecstBatchTmpRepository.class).findByBatNo((String)context.getData(ParamKeys.BAT_NO));
		List<Map<String,Object>>tempMap=(List<Map<String, Object>>) BeanUtils.toMaps(lt);
		for(int i=0;i<tempMap.size();i++){
			tempMap.get(i).putAll(resultMap.get(i));
		}
		*/
        
//        EupsThdFtpConfig config=get(EupsThdFtpConfigRepository.class).findOne("elec02batch");
        EupsThdFtpConfig config=get(EupsThdFtpConfigRepository.class).findOne("elec02BatchThdFile");
		Assert.isFalse(null == config, ErrorCodes.EUPS_THD_FTP_CONFIG_NOTEXIST);
		config.setFtpDir("0");
		
		ret.put("header", context.getDataMapDirectly());
		ret.put("detail", tempList);
		//这个要从配置项读取
//		config.setLocDir("E:\\");
//		config.setLocFleNme("elecfs20150412.txt");
//		config.setRmtWay("/app/ics/dat/efe");
//		config.setRmtFleNme("elecfs20150412.txt");
//      ((OperateFileAction)get("opeFile")).createCheckFile(config, "ELEC02BatchBack", "elecfs20150412.txt", ret);

        ((OperateFileAction)get("opeFile")).createCheckFile(config, "ELEC02BatchBack", config.getLocFleNme(), ret);

        config.setFtpDir("");//TODO
		((OperateFTPAction)get("opeFTP")).putCheckFile(config);
		/**通知第三方*/ 
		 context.setData("TransCode", "23");
		 context.setData("WD0", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		 String logNo=((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE)).getBBIPSequence();
		 context.setData("LogNo", StringUtils.substring(logNo, 4));
		 String tmn=StringUtils.substring(logNo, 8);
		 context.setData("TMN", tmn);
		 context.setData("FileName", ""); //TODO
		 context.setData("recordNum", (String)context.getData("totCnt"));
		 Map<String,Object>thdResult= get(CallThdService.class).callTHD(context);
		 logger.info("电力返盘文件处理结束");
	}
  
}

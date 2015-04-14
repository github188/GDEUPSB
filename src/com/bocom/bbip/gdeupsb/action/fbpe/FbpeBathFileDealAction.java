package com.bocom.bbip.gdeupsb.action.fbpe;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.repository.EupsThdTranCtlInfoRepository;
import com.bocom.bbip.eups.spi.service.batch.BatchAcpService;
import com.bocom.bbip.eups.spi.vo.PrepareBatchAcpDomain;
import com.bocom.bbip.file.Marshaller;
import com.bocom.bbip.file.transfer.TransferUtils;
import com.bocom.bbip.gdeupsb.action.common.BatchFileCommon;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.AgtFileBatchDetail;
import com.bocom.bbip.gdeupsb.entity.GdFbpeFileBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpeFileBatchTmpRepository;
import com.bocom.bbip.thd.org.apache.commons.lang3.ArrayUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.support.CollectionUtils;

/**
 * 佛山文本批量文件准备
 * Date 2015-01-23
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class FbpeBathFileDealAction extends BaseAction implements BatchAcpService {

    private final static Log logger=LogFactory.getLog(FbpeBathFileDealAction.class);
    @Autowired
    EupsThdTranCtlInfoRepository thdTranCtlInfoRepository;
    @Autowired
    GDEupsBatchConsoleInfoRepository batchConsoleInfoRepository;
    @Autowired
    EupsThdBaseInfoRepository thdBaseInfoRepository;
    @Autowired
    Marshaller marshaller;
    @Autowired
    BatchFileCommon batchFileCommon;
    @Autowired
    OperateFTPAction operateFTPAction;
    @Autowired
    BBIPPublicServiceImpl bbipPublicServiceImpl;
    @Autowired
    GdFbpeFileBatchTmpRepository fileBatchTmpRepository;
    @SuppressWarnings("unchecked")
    @Override
    public void prepareBatchDeal(PrepareBatchAcpDomain arg0, Context context) throws CoreException {

        logger.info("=================Start  FbpeBathFileDealAction prepareBatchDeal");
        batchFileCommon.BeforeBatchProcess(context);
        get(BatchFileCommon.class).BeforeBatchProcess(context);
        String batNo=context.getData("batNo").toString();
        		
        /**  第三方文件名*/
        String fileName = context.getData("fleNme").toString();
       
        EupsThdFtpConfig eupsThdFtpConfig = get(EupsThdFtpConfigRepository.class).findOne("FSAG00");
        eupsThdFtpConfig.setRmtFleNme(fileName);
        eupsThdFtpConfig.setLocFleNme(fileName);
        eupsThdFtpConfig.setFtpDir("1");
		operateFTPAction.getFileFromFtp(eupsThdFtpConfig);
		
		logger.info("===============获取文件成功");
        
		String filePath=eupsThdFtpConfig.getLocDir();
		String pathName=filePath+"//"+fileName;
		//自行实现解析文件
        Map<String, List<Map<String, Object>>> map= new HashMap<String, List<Map<String,Object>>>();
        Resource resource=new FileSystemResource(TransferUtils.resolveFilePath(filePath, fileName));
        String comNo = context.getData("comNo").toString();

        //根据单位编号寻找格式文件解析
        if(comNo.equals("4460000011")) {
            try { 
                map= marshaller.unmarshal("tvFbpeBatFmt", resource, Map.class);
            } catch (JumpException e) {
                e.printStackTrace();
            }
        } else if (comNo.equals("4460002194")) {
        		//解析文件
        		try {
					map=resourceFile(pathName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }  else if (comNo.equals("4460000010")) {
            try {
                map= marshaller.unmarshal("mobFbpeBatFmt", resource, Map.class);
            } catch (JumpException e) {
                e.printStackTrace();
            }
        } else if (comNo.equals("4460000014")) {
            try {
                map= marshaller.unmarshal("telFbpeBatFmt", resource, Map.class);
            } catch (JumpException e) {
                e.printStackTrace();
            }
        }
        logger.info("===============解析文件成功");
        Map head=(Map)map.get("header");
		if (null!=head) {
					context.getDataMapDirectly().putAll(head);
		}
		
        //整个list
        List <GdFbpeFileBatchTmp> payDetailLst = new ArrayList<GdFbpeFileBatchTmp>();
        List<Map<String, Object>> parseMap = (List<Map<String, Object>>) map.get("detail");  //文件体
        BigDecimal bigDecimal=new BigDecimal("0.00");
        
        logger.info("===============Start  insert  GDEUPS_FBPE_FILE_BATCH_TMP");
        for (Map<String, Object> orgMap : parseMap) {
            GdFbpeFileBatchTmp gdFbpeFileBatchTmp = new GdFbpeFileBatchTmp();
            String sqn=bbipPublicServiceImpl.getBBIPSequence().substring(6).trim();
            gdFbpeFileBatchTmp.setSqn(sqn);
            gdFbpeFileBatchTmp.setAccAmt((String)orgMap.get("accAmt"));
            gdFbpeFileBatchTmp.setAccNo((String)orgMap.get("accNo"));
            gdFbpeFileBatchTmp.setActNo((String)orgMap.get("actNo"));
            gdFbpeFileBatchTmp.setRsvFld6((String)orgMap.get("smsSqn")); //SMS交易流水号
            gdFbpeFileBatchTmp.setTxnNo((String)orgMap.get("txnNo"));
            gdFbpeFileBatchTmp.setOrgCde((String)orgMap.get("orgCde"));
            gdFbpeFileBatchTmp.setTlrNo((String)orgMap.get("tlrNo"));
            gdFbpeFileBatchTmp.setTxnTim((String)orgMap.get("txnTme"));
            gdFbpeFileBatchTmp.setCusNo((String)orgMap.get("cusNo"));
            gdFbpeFileBatchTmp.setCusAc((String)orgMap.get("cusAc"));
            gdFbpeFileBatchTmp.setCusNam((String)orgMap.get("cusNam"));
            gdFbpeFileBatchTmp.setTxnAmt((String)orgMap.get("txnAmt"));
            gdFbpeFileBatchTmp.setCosMon((String)orgMap.get("months"));
            gdFbpeFileBatchTmp.setRsvFld1((String)orgMap.get("rsvFld1"));
            gdFbpeFileBatchTmp.setBankNo((String)orgMap.get("bankNo"));
            gdFbpeFileBatchTmp.setBankNam((String)orgMap.get("bankNam"));
            gdFbpeFileBatchTmp.setTxnNo((String)orgMap.get("txnNo"));
            gdFbpeFileBatchTmp.setRsvFld2((String)orgMap.get("rsvFld2"));
            
            if(orgMap.get("txnNo")==null){
            		gdFbpeFileBatchTmp.setTxnNo("fbpe");
            }
            fileBatchTmpRepository.insert(gdFbpeFileBatchTmp);
            bigDecimal=bigDecimal.add(new BigDecimal((String)orgMap.get("txnAmt")).scaleByPowerOfTen(-2));
            payDetailLst.add(gdFbpeFileBatchTmp);
        }
        logger.info("===============End  insert  GDEUPS_FBPE_FILE_BATCH_TMP");
		context.setData("totCnt", parseMap.size());
		context.setData("totAmt", bigDecimal);
		
        String comNoAcps=context.getData("comNoAcps").toString();
        
		Map<String, Object> headerMap=new HashMap<String, Object>();
		headerMap.put("comNo", comNoAcps);
		headerMap.put("totAmt", bigDecimal);
		headerMap.put("totCount", parseMap.size());
		
		logger.info("===============开始代收付文件数据准备");
		Map<String, Object> temp = CollectionUtils.createMap();
		List<AgtFileBatchDetail> detailList=createList(context, payDetailLst);
		temp.put(ParamKeys.EUPS_FILE_HEADER,headerMap);
		temp.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(detailList));
        context.setVariable(GDParamKeys.COM_BATCH_AGT_FILE_MAP, temp);
        logger.info("===============代收付文件准备数据完成");
        batchFileCommon.sendBatchFileToACP(context);
        logger.info("===============End  FbpeBathFileDealAction prepareBatchDeal");
    }
    /**
     * 生成代收付文件数据准备
    */
    public List<AgtFileBatchDetail> createList(Context context,List<GdFbpeFileBatchTmp> payDetailLst){
		logger.info("=================Start  FbpeBathFileDealAction  createMap ");
		
			List<AgtFileBatchDetail> detailList=new ArrayList<AgtFileBatchDetail>();
			for (GdFbpeFileBatchTmp gdeFbpeFileBatchTmp : payDetailLst) {
				AgtFileBatchDetail agtFileBatchDetail=new AgtFileBatchDetail();
				agtFileBatchDetail.setAGTSRVCUSID(gdeFbpeFileBatchTmp.getCusNo());
				agtFileBatchDetail.setAGTSRVCUSNME(gdeFbpeFileBatchTmp.getCusNam());
				String cusAc=gdeFbpeFileBatchTmp.getCusAc();
				agtFileBatchDetail.setCUSAC(cusAc);
				//本行标志
				if(get(AccountService.class).isOurBankCard(cusAc)){
						agtFileBatchDetail.setOUROTHFLG("0");
				}else{
					agtFileBatchDetail.setOUROTHFLG("1");
				}
				agtFileBatchDetail.setTXNAMT(new BigDecimal(gdeFbpeFileBatchTmp.getTxnAmt()).scaleByPowerOfTen(-2));
				//备注 不用
				detailList.add(agtFileBatchDetail);
			}
		
		logger.info("=================End  FbpeBathFileDealAction  createMap ");
		return detailList;
    }
 
    public Map<String, List<Map<String, Object>>> resourceFile(String pathName) throws IOException{
    	 logger.info("===============Start  FbpeBathFileDealAction resourceFile");
    	 Map<String, List<Map<String, Object>>> mapList=new HashMap<String, List<Map<String,Object>>>();
    	 int LEN=3;
		 int pos=0;
		 int firstLen=0;
		 int secondLen=0;
		 int thirdLen=0;
		 int fourthLen=0;
		List<Map<String,Object>> ret=new ArrayList<Map<String,Object>>();
		Map<String,Object>map=null;
		File file=new File(pathName);
		List<String>list=FileUtils.readLines(file, "gbk");
		for(String str:list){
			map=new HashMap<String,Object>();
			byte[]b=str.getBytes("gbk");
			byte _1st[]=ArrayUtils.subarray(b, pos, LEN);
			firstLen=calLen(_1st);
			pos=LEN;
			byte[]_1stVal=ArrayUtils.subarray(b, pos, firstLen+pos);
			pos=pos+firstLen;
			map.put("cusNo", new String(_1stVal));
			byte _2nd[]=ArrayUtils.subarray(b, pos, pos+LEN);
			
			secondLen=calLen(_2nd);
			pos=pos+LEN;
			byte[]_2ndVal=ArrayUtils.subarray(b, pos, secondLen+pos);
			
			map.put("cusAc", new String(_2ndVal));
			pos=pos+secondLen;
			byte _3rd[]=ArrayUtils.subarray(b, pos, pos+LEN);
			thirdLen=calLen(_3rd);
			pos=pos+LEN;
			byte[]_3rdVal=ArrayUtils.subarray(b, pos, thirdLen+pos);
			map.put("cusNam", new String(_3rdVal));
			pos=pos+thirdLen;
			byte _4th[]=ArrayUtils.subarray(b, pos, pos+LEN);
			fourthLen=calLen(_4th);
			pos=pos+LEN;
			byte[]_4thVal=ArrayUtils.subarray(b, pos, fourthLen+pos);
			map.put("txnAmt", new String(_4thVal));
			ret.add(map);
			  firstLen=0;
			  secondLen=0;
			  thirdLen=0;
			  fourthLen=0;
			  pos=0;
		}
		mapList.put("detail", ret);
		 logger.info("===============End  FbpeBathFileDealAction resourceFile");
		return mapList;
    }
	private  static int calLen(byte[]len){
		int length=0;
		length+=100*(len[0]-48);length+=10*(len[1]-48);length+=(len[2]-48);
		return length;
	}
}

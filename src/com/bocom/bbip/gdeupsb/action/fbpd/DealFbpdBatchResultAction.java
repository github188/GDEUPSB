package com.bocom.bbip.gdeupsb.action.fbpd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.spi.service.batch.AfterBatchAcpService;
import com.bocom.bbip.eups.spi.vo.AfterBatchAcpDomain;
import com.bocom.bbip.gdeupsb.entity.GDEupsBatchConsoleInfo;
import com.bocom.bbip.gdeupsb.entity.GdFbpdMposBatchTmp;
import com.bocom.bbip.gdeupsb.entity.GdFbpdNeleBatchTmp;
import com.bocom.bbip.gdeupsb.entity.GdFbpdObusBatchTmp;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpdMposBatchTmpRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpdNeleBatchTmpRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpdObusBatchTmpRepository;
import com.bocom.bbip.gdeupsb.repository.GdFbpeFileBatchTmpRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 中山文件批扣-批扣结果处理
 * @version 1.0.0
 * @author WangMQ
 */
public class DealFbpdBatchResultAction implements AfterBatchAcpService {

    @Autowired
    GDEupsBatchConsoleInfoRepository eupsBatchConsoleInfoRepository;
    
    @Autowired
    GdFbpdNeleBatchTmpRepository gdFbpdNeleBatchTmpRepository;
    
    @Autowired
    GdFbpdMposBatchTmpRepository gdFbpdMposBatchTmpRepository;
    
    @Autowired
    GdFbpdObusBatchTmpRepository gdFbpdObusBatchTmpRepository;
    
    @Autowired
    GdFbpeFileBatchTmpRepository fileRepository;

    @Autowired
    EupsThdFtpConfigRepository eupsThdFtpConfigRepository;

    @Autowired
    OperateFileAction operateFileAction;

    @Autowired
    OperateFTPAction operateFTPAction;

    private final static Logger logger = LoggerFactory.getLogger(DealFbpdBatchResultAction.class);

  
    @Override
    public void afterBatchDeal(AfterBatchAcpDomain afterBatchAcpDomain, Context context) throws CoreException {
        logger.info("Enter in DealFbpdBatchResultAction! ");
        logger.info("============context=" + context);
        
        //根据批次号查询批次状态
        String batNo = (String) context.getData("dskNo");
        GDEupsBatchConsoleInfo eupsBatchConsoleInfo = eupsBatchConsoleInfoRepository.findOne(batNo);
        if ("S" !=eupsBatchConsoleInfo.getBatSts()) {
            context.setData(ParamKeys.RSP_CDE, "481299");
            context.setData(ParamKeys.RSP_MSG, "批次未处理完毕，请稍后!");
//            <Return/>
            throw new CoreRuntimeException("批次未处理完毕");
        }
        
        //根据comNo指定文件格式
        String comNo = eupsBatchConsoleInfo.getComNo();
        String createFileFmt = null;
        if("4840000015".equals(comNo)){	//中山水费 WATR   fbpdWaterFmtOut
        	createFileFmt = "fbpdWaterFmtOut";
        }
        if("4840000167".equals(comNo)){	//NELE_out_484999_1 电费 fbpdNele1FmtOut
        	createFileFmt = "fbpdNele1FmtOut";
        }
        if("4840000016".equals(comNo) || "4840000484".equals(comNo) || "4840000352".equals(comNo) || "4840000363".equals(comNo) || "4840000475".equals(comNo)){	//NELE_out_484999 电费	fbpdNeleFmtOut
        	createFileFmt = "fbpdNeleFmtOut";
        }
        if("4840000018".equals(comNo)){	//GGAS_out_484999 中山煤气费	fbpdGgasFmtOut
        	createFileFmt = "fbpdGgasFmtOut";
        }
        if("4840000017".equals(comNo)){	//VANK_out_484999中山物业管理费 fbpdVankFmtOut
        	createFileFmt = "fbpdVankFmtOut";
        }
        if("4840000020".equals(comNo)){	//TTOM_out_484999中山铁通 fbpdTtomFmtOut
        	createFileFmt = "fbpdTtomFmtOut";
        }
        if("4840000019".equals(comNo)){	//CTTV_out_484999 中山有线电视 fbpdCttvFmtOut
        	createFileFmt = "fbpdCttvFmtOut";
        }
        if("4840000598".equals(comNo)){	//NQTV_out_484999南区有线电视 fbpdNqtvFmtOut
        	createFileFmt = "fbpdNqtvFmtOut";
        }
        if("4840000414".equals(comNo)){	//XIND_out_484999 新都物业管理 fbpdXindFmtOut
        	createFileFmt = "fbpdXindFmtOut";
        }
        if("4840000416".equals(comNo)){	//MPOS_out_484999 中山移动POS fbpdMposFmtOut
        	createFileFmt = "fbpdMposFmtOut";
        }
        
        //消息明细
        //根据batno查询对应业务临时表中的明细 
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<Map<String, Object>> detailList = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMapHead = new HashMap<String, Object>();
        Map<String, Object> detailMap = new HashMap<String, Object>();
        
        if("4840000016".equals(comNo) || "4840000484".equals(comNo) || "4840000352".equals(comNo) || "4840000363".equals(comNo) || "4840000475".equals(comNo)){ //NELE电费有header 
            resultMapHead.put("batFlg", "");	//TOTO 批量代收信息标识  待确定
            resultMapHead.put("ccy", "RMB");	//币种固定为RMB
            resultMapHead.put("payFlg", "1");	//收付标志,固定为收1
            resultMapHead.put("feeTyp", "");	// TODO 费项代码  待确定
            resultMapHead.put("totCnt", eupsBatchConsoleInfo.getTotCnt());	//总笔数
            resultMapHead.put("totAmt", eupsBatchConsoleInfo.getTotAmt());	//总金额
            resultMapHead.put("subDte", eupsBatchConsoleInfo.getSubDte());	//提交日期
            resultMapHead.put("txnDte", eupsBatchConsoleInfo.getExeDte());	//交易日期
            resultMapHead.put("rsFld1", "");	//TODO 处理说明 待确定
            resultMapHead.put("sqn", "");	// TODO 流水号 待确定
            resultMapHead.put("bk", context.getData(ParamKeys.BK));	//分行号
            //put header to resultMap
            resultMap.put(ParamKeys.EUPS_FILE_HEADER, resultMapHead);
        	
            GdFbpdNeleBatchTmp batchTmp = new GdFbpdNeleBatchTmp();
            batchTmp.setSubDte(DateUtils.format(eupsBatchConsoleInfo.getSubDte(), DateUtils.STYLE_yyyyMMdd));
            List<GdFbpdNeleBatchTmp> neleDetailList =  gdFbpdNeleBatchTmpRepository.find(batchTmp);
            for(GdFbpdNeleBatchTmp batchList : neleDetailList){
            	detailMap.put("serNo", batchList.getSerNo());
            	detailMap.put("payFeeNo", batchList.getPayFeeNo());
            	detailMap.put("br", batchList.getBr());
            	detailMap.put("cusAc", batchList.getCusAc());
            	detailMap.put("cusNme", batchList.getCusNme());
            	detailMap.put("txnAmt", batchList.getTxnAmt());
            	detailMap.put("txnTyp", batchList.getTxnTyp());
            	detailMap.put("RsFld3", batchList.getRsFld3());
            	detailMap.put("cusDpm", batchList.getCusDpm());
            	detailMap.put("feeMon", batchList.getFeeMon());
            	detailMap.put("feeCnt", batchList.getFeeCnt());
            	detailMap.put("thdCusNme", batchList.getThdCusNme());
            	detailMap.put("eleAc", batchList.getEleAc());
            	detailMap.put("frzAmt", batchList.getFrzAmt());
            	detailMap.put("fSeqNo", batchList.getfSeqNo());
            	detailMap.put("crpCod", batchList.getCrpCod());
            	detailMap.put("evidNo", batchList.getEvidNo());
            	
            	detailList.add(detailMap);
            }
          //put detailList to resultMap
            resultMap.put("detail", detailList);
        }
        
        else if("4840000416".equals(comNo)){ //中山移动 Mpos  
        	GdFbpdMposBatchTmp batchTmp = new GdFbpdMposBatchTmp();
            batchTmp.setPosFld2(DateUtils.format(eupsBatchConsoleInfo.getSubDte(), DateUtils.STYLE_yyyyMMdd));
            List<GdFbpdMposBatchTmp> mposDetailList =  gdFbpdMposBatchTmpRepository.find(batchTmp);
            for(GdFbpdMposBatchTmp batchListBatchTmp : mposDetailList){
            	detailMap.put("sqn", batchListBatchTmp.getSqn());
            	detailMap.put("thdSqn", batchListBatchTmp.getThdSqn());
            	detailMap.put("posNo", batchListBatchTmp.getPosNo());
            	detailMap.put("comAc", batchListBatchTmp.getComAc());
            	detailMap.put("comNo", batchListBatchTmp.getComNo());
            	detailMap.put("cusAc", batchListBatchTmp.getCusAc());
            	detailMap.put("txnDte", batchListBatchTmp.getTxnDte());
            	detailMap.put("txnTme", batchListBatchTmp.getTxnTme());
            	detailMap.put("txnFee", batchListBatchTmp.getTxnFee());
            	detailMap.put("txnAmt", batchListBatchTmp.getTxnAmt());
            	detailMap.put("txnChr", batchListBatchTmp.getTxnChr());
            	detailMap.put("txnRnt", batchListBatchTmp.getTxnRnt());
            	detailMap.put("chkDate", batchListBatchTmp.getChkDate());
            	detailMap.put("seqNo", batchListBatchTmp.getSeqNo());
            	detailMap.put("posFld1", batchListBatchTmp.getPosFld1());
            	detailMap.put("posFld2", batchListBatchTmp.getPosFld2());
            	detailMap.put("posFld3", batchListBatchTmp.getPosFld3());
            	detailMap.put("posFld4", batchListBatchTmp.getPosFld4());
            	detailMap.put("posFld5", batchListBatchTmp.getPosFld5());
           	 
           	 detailList.add(detailMap);
            }
        	resultMap.put("detail", detailList);
        }
        
        else{ //其他
        	 GdFbpdObusBatchTmp batchTmp = new GdFbpdObusBatchTmp();
             batchTmp.setSubDte(DateUtils.format(eupsBatchConsoleInfo.getSubDte(), DateUtils.STYLE_yyyyMMdd));
             List<GdFbpdObusBatchTmp> obusDetailList =  gdFbpdObusBatchTmpRepository.find(batchTmp);
             for(GdFbpdObusBatchTmp batchListBatchTmp : obusDetailList){
            	 detailMap.put("sqn", batchListBatchTmp.getSqn());
            	 detailMap.put("comNo", batchListBatchTmp.getComNo());
            	 detailMap.put("cusAc", batchListBatchTmp.getCusAc());
            	 detailMap.put("cusNo", batchListBatchTmp.getCusNo());
            	 detailMap.put("cusNme", batchListBatchTmp.getCusNme());
            	 detailMap.put("thdCusNo", batchListBatchTmp.getThdCusNo());
            	 detailMap.put("ccy", batchListBatchTmp.getCcy());
            	 detailMap.put("txnAmt", batchListBatchTmp.getTxnAmt());
            	 detailMap.put("sucFlg", batchListBatchTmp.getSucFlg());
            	 detailMap.put("chrDte", batchListBatchTmp.getChrDte());
            	 detailMap.put("subDte", batchListBatchTmp.getSubDte());
            	 detailMap.put("seqNo", batchListBatchTmp.getSeqNo());
            	 detailMap.put("tmpFld1", batchListBatchTmp.getTmpFld1());
            	 detailMap.put("tmpFld2", batchListBatchTmp.getTmpFld2());
            	 detailMap.put("tmpFld3", batchListBatchTmp.getTmpFld3());
            	 detailMap.put("tmpFld4", batchListBatchTmp.getTmpFld4());
            	 detailMap.put("tmpFld5", batchListBatchTmp.getTmpFld5());
            	 
            	 detailList.add(detailMap);
             }
             resultMap.put("detail", detailList); 
        }
        
        EupsThdFtpConfig eupsThdFtpConfig = eupsThdFtpConfigRepository.findOne("fbpeBathReturnFmt");
        
        // TODO  文件名 文件路径
        String fileName = context.getData("filNam")+"_"+batNo+"_"+ context.getData("bk");
        eupsThdFtpConfig.setLocDir("dat/fbp/"+fileName);
        eupsThdFtpConfig.setFtpDir("dat/term/send/"+fileName);
        eupsThdFtpConfig.setLocFleNme(fileName);

        // 生成文件
        operateFileAction.createCheckFile(eupsThdFtpConfig, createFileFmt, fileName, resultMap);

        // 将生成的文件上传至指定服务器
        eupsThdFtpConfig.setLocFleNme(fileName);
        eupsThdFtpConfig.setRmtFleNme(fileName);
        operateFTPAction.putCheckFile(eupsThdFtpConfig);
    }

}

package com.bocom.bbip.gdeupsb.action.gas;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * GASH 对账文件上传  460707
 * 生成对账文件并上传
 * 
 * 文件名：ss+银行标志+年月日（实际交易日期）+.txt
 * 示例：ssCNJT20130930.txt
 * 生成文件到  ftp://bank/银行标志/reckoning/
 * @author WMQ
 *
 */
public class CheckFileToThdHzAction extends BaseAction{
	
	private static final Log logger=LogFactory.getLog(CheckFileToThdHzAction.class);

	int count = 0;//ics中的Num， 用于统计对账文件中的交易笔数
	
	public void execute (Context context) throws CoreException, CoreRuntimeException{
		
		logger.info("====================Enter In CheckFileToThdHZAction....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);

		String thdTxnCde = "460707";
		context.setData(ParamKeys.THD_TXN_CDE, thdTxnCde);
		
//		 <FlowCtrl>
//	      <Exec func="PUB:InitTransaction"/>
//	      <Set>TxnDat=FMTDATE($ActDat,4,)</Set>      <!-- 实际交易日期有疑问 -->
//	      <!-- 交易日期，输入格式（4）：YYYY-MM-DD,输出默认格式YYYYMMDD -->
//	      <!--文件名-->
//	      <Set>LclFil=STRCAT(ss,$BnkCod,$TxnDat,.txt)</Set> 
//	      <Set>ObjFil=$LclFil</Set>
//	      <Set>FilNam=$LclFil</Set>
		String sqn = context.getData(ParamKeys.SEQUENCE);
//		//交易日期
		String txnDte = DateUtils.format((Date) context.getData(ParamKeys.TXN_DTE), DateUtils.STYLE_yyyyMMdd);
//		context.setData(ParamKeys.TXN_DTE, txnDte);
		//银行编号
		String bk = context.getData(ParamKeys.BK);
//		//拼接文件名
		String lclFilNam = "ss" + bk + txnDte + ".txt";
		context.setData("LclFil", lclFilNam);
		context.setData("ObjFil", lclFilNam);
		context.setData("FilNam", lclFilNam);
		
//		 <!--文件路径-->s
//	      <Set>ObjDir=STRCAT(BANK/,$BnkCod,/agrement/)</Set>
//	      <Set>LclDir=STRCAT($SendDir,$BnkCod,/)</Set>
//	      <Set>DatFil=STRCAT($LclDir,$LclFil)</Set>
		//TODO ICS源码中的路径与技术方案中的文件路径不一，暂用技术方案中和需求文档中的文件路径（文件都放在ftp://bank/银行标志/reckoning/  此目录下）
		//本地文件路径
		String objDir = "ftp://bank/" + bk + "/reckoning/" ; 
		context.setData("ObjDir", objDir);
		String lclDir = context.getData("SendDir") + bk ;
		context.setData("LclDir", lclDir);
		String datFil = lclDir + lclFilNam ;
		context.setData("DatFil", datFil);

		 // 获取FTP信息,发送文件到指定路径
//		EupsThdFtpConfig eupsThdFtpConfig = context.getData(ParamKeys.CONSOLE_THD_FTP_CONFIG_LIST);
		
		//设置生成文件的名字，路径
		EupsThdFtpConfig eupsThdFtpConfig = new EupsThdFtpConfig();
		eupsThdFtpConfig.setLocFleNme(lclFilNam);
		eupsThdFtpConfig.setLocDir(lclDir);
		
		// 拼装对账文件Map
        Map<String, Object> map = encodeFileMap(context);
        
        try {
            // 生成对账文件到指定路径
            get(OperateFileAction.class).createCheckFile(eupsThdFtpConfig, "creHzGasCheckFile", lclFilNam, map);
            logger.info("对账文件生成成功！");
        } catch (Exception e) {
        	logger.error("File create error : " + e.getMessage());
            throw new CoreException(ErrorCodes.EUPS_FILE_CREATE_FAIL);
        }
        
        
//        //TODO 向非税服务器上传对账文件ftpput (琛说暂时不做)
//        <Exec func="PUB:FtpPut" error="IGNORE">
//          <Arg name="IpAdr" value="$FtpAdr"></Arg>
//          <Arg name="UsrNam" value="$UsrNam"></Arg>
//          <Arg name="UsrPwd" value="$UsrPwd"></Arg>
//          <Arg name="ObjDir" value="YHSC"></Arg>
//          <Arg name="LclDir" value="STRCAT(dat,/gsah/,$BnkCod)"></Arg>
//          <Arg name="LclFil" value="DELBOTHSPACE($FilNam)"></Arg>
//        </Exec>
//        <If condition="~RetCod !=0">
//          <Set>MsgTyp=E</Set>
//          <Set>RspCod=329999</Set>
//          <Set>RspMsg=STRCAT(文件传输错-,$FilNam,请联系银行中心处理)</Set>
//          <Return/>
//        </If>
        
        
//        // 修改远程对账文件名称
//        String rmtFileName = eupsThdFtpConfig.getRmtFleNme();
//        rmtFileName = StringUtils.replace(rmtFileName, "$chkDte", chkDate);
//        eupsThdFtpConfig.setRmtFleNme(rmtFileName);
//        
//        // 向指定FTP路径放文件
//         get(OperateFTPAction.class).putCheckFile(eupsThdFtpConfig);
//         log.info("对账文件FTP放置成功！");
         
         
         //TODO  疑问：
         //更新流水，变更状态为已对账  ActDAt--TXN_DTE, HTxnSt--MFM_TXN_STS
         //        Status账务状态(0:正常;1:抹账;2:已对帐;3:冲正)，EUPS流水表中对应字段：    ？？？？
//         <Exec func="PUB:ExecSql" error="IGNORE">
//         <Arg name="SqlCmd"   value="UpdJnlSts"/>   
//        UpdJnlSts : update gastxnjnl491 set Status='2' where ActDat='%s' and HTxnSt IN ('S','T','U') and Status='0'
//       </Exec>
//       <If condition="~RetCod !=0">
//       	<Set>MsgTyp=E</Set>
//       	<Set>RspCod=329999</Set>
//       	<Set>RspMsg=数据库操作失败，请联系系统管理员</Set>
//         <Return/>
        
//        EupsTransJournal etj = new EupsTransJournal();
//        logger.info("sqn>>>"+context.getData(ParamKeys.SEQUENCE));
//        etj.setSqn(sqn);
//        etj.setTxnDte((Date)context.getData(ParamKeys.TXN_DTE));
//        etj.setMfmTxnSts("S");
//        etj.setExtRcnFlg("2");
//        get(EupsTransJournalRepository.class).update(etj);
        
        
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        
	}
	
	/** 拼装对账文件Map
	 */
    private Map<String, Object> encodeFileMap(Context context) throws CoreException {
        Map<String, Object> map = new HashMap<String, Object>();
        
   	 //TODO 先从流水表中查询需要的records
//   	 ICS SQL:
//   	 select * from gastxnjnl491 where ActDat='%s' and HTxnSt IN ('S','T','U')  and Status='0'
//       HTxnSt 为主机交易状态 对应EUPS流水表中的MFM_TXN_STS （U-预计,S-成功,F-失败,T-超时,R-已冲正,C-抹账）
//疑问点:Status 在ICS SQL中为账务状态(0:正常;1:抹账;2:已对帐;3:冲正)，EUPS流水表中无Status字段不确定使用哪个 (琛说是TXN_TYP ：N.正常交易 C.冲正交易 R.重发交易)  
//   	 对账的records应符合流水表中交易状态、主机交易状态均为S（成功）的条件
        EupsTransJournal etj = new EupsTransJournal();
        etj.setComNo(context.getData(ParamKeys.COMPANY_NO).toString().trim());
        etj.setTxnDte((Date) context.getData(ParamKeys.CHECK_DATE));
//      etj.setMfmTxnSts("S");
        etj.setTxnSts("S");
        etj.setTxnTyp("N"); 
        
        
        List<EupsTransJournal> chkEtjList = get(EupsTransJournalRepository.class).find(etj);
        if (null == chkEtjList && CollectionUtils.isEmpty(chkEtjList)) {
        	logger.info("There are no records for select check trans journal ");
            throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
        }
        List<EupsTransJournal> etjLst = new LinkedList();
        for(EupsTransJournal etjnl : chkEtjList){
        	//判断chkEtjList中的主机状态，取状态为“S”、“T”、“U”的record
        	if("S".equals(etjnl.getMfmTxnSts()) || "T".equals(etjnl.getMfmTxnSts()) || "U".equals(etjnl.getMfmTxnSts()) ){
        		etjLst.add(etjnl);
        		count++;
        	}
        }
        
        context.setData("count", count);
        map.put(ParamKeys.EUPS_FILE_DETAIL, BeanUtils.toMaps(etjLst));
        return map;
    }
    
}

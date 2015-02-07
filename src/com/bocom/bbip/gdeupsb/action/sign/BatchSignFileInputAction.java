/*@(#)BatchSignFileInputAction.java   2015-2-3 
 * Copy Right 2015 Bank of Communications Co.Ltd.
 * All Copyright Reserved
 */

package com.bocom.bbip.gdeupsb.action.sign;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdFtpConfig;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.eups.utils.SupportFtpUtils;
import com.bocom.bbip.file.MftpTransfer;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.common.OperateFileService;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.entity.Gdsagtmob;
import com.bocom.bbip.gdeupsb.entity.Gdsbatagtinf;
import com.bocom.bbip.gdeupsb.repository.GdsAgtInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdsRunCtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdsagtmobRepository;
import com.bocom.bbip.gdeupsb.repository.GdsagtuncaRepository;
import com.bocom.bbip.gdeupsb.repository.GdsbatagtinfRepository;
import com.bocom.bbip.gdeupsb.utils.GdEupsbUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * TODO Document BatchSignFileInputAction
 * @category 批量签约文件导入
 * @version 1.0.0,2015-2-3
 * @author tandun
 * @since 1.0.0
 */
public class BatchSignFileInputAction extends BaseAction{
    @Autowired
	GdsRunCtlRepository gdsRunCtlRepository;
    @Autowired
    GdsbatagtinfRepository gdsbatagtinfRepository;
    @Autowired
    SupportFtpUtils supportFtpUtils;
    @Autowired
    OperateFTPAction operateFTPAction;
    @Autowired
    EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
    @Autowired
    OperateFileAction operateFileAction;
    @Autowired
    OperateFileService operateFileService;
	@Autowired
	BBIPPublicService bbipublicService;
	@Autowired
	AccountService accountService;
	@Autowired
	GdsagtmobRepository gdsagtmobRepository;
	@Autowired
    MftpTransfer mftpTransfer;
    @Autowired
    VelocityTemplatedReportRender reportRender;
    @Autowired
    GdsAgtInfRepository gdsAgtInfRepository;
    @Autowired
    GdsagtuncaRepository gdsagtuncaRepository;
	// 本地存放目录
    @Value("${localDir}")
    private String localDir;
    
   // 发送前端打印目录
    @Value("${bbosDir}")
    private String bbosDir;
	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("BatchSignFileInputAction start!..");
		//获取I：导入处理  P：报表打印  V:浏览
		context.setData("retcod","000000");
	    context.setData("retmsg","批量导入成功");
		String funcTyp=context.getData("funcTyp");
		if(funcTyp=="I"){
		 fileInput(context);	
		}else if(funcTyp=="P"){
		 reportPrint(context);
		}else if(funcTyp=="V"){
		 reportBrowse(context);
		}
		//返回前端的信息
		 String retcod=context.getData("retcod");
		   if(retcod=="000000"){
			context.setData("ApCode", "46");   
			context.setData("OFmtCd", "999");   
			context.setData("RetMsg", context.getData("retmsg")); 
			context.setData("SucRpt", "");   
			context.setData("FailRpt", "");  
			 
		   }else {
			    context.setData("ApCode", "SC");   
				context.setData("OFmtCd", "D04");   
				context.setData("RspCod", retcod);   
				context.setData("InPos", "0001");   
				context.setData("RspMsg", context.getData("retmsg"));  
		   }
	}
	
	@SuppressWarnings( "unchecked" )
	public void fileInput(Context context) throws CoreException, CoreRuntimeException{
		GdsRunCtl gdsRunCtl=new GdsRunCtl();
		Map<String,Object> map = new HashMap<String,Object>();
		Gdsbatagtinf gdsbatagtinf=new Gdsbatagtinf();
		//==是否继续的标识
		String isContinue="N";
		String mcusid="";
		//文件名称
		String filNm="";
		//===状态
		String status="W";
		//====明细总数
		int chkDtlCnt=0;
		//===头的总笔数
		int headCnt=0;
		
		//=====截取文件名=======
		filNm= context.getData("filNm").toString().trim();
		String gdsBid=filNm.substring(0,5);
	    String strGdsBId=context.getData("gdsBid");
	   
		//====初始化====获取控制信息表数据
		gdsRunCtl.setGdsBid(gdsBid);
		List <GdsRunCtl> gdsRunCtllist=gdsRunCtlRepository.find(gdsRunCtl);
		if(CollectionUtils.isEmpty(gdsRunCtllist)){//BBIPVNB0001
			 /*log.info("check fileName rule error...... ");
			 throw new CoreException(GDErrorCodes.EUPS_FILE_BUSTYPE_ERROR);	*/
		}
		/*agtmtb=gdsRunCtllist.get(0).getAgtMtb();
		agtstb=gdsRunCtllist.get(0).getAgtStb();*/
		//====检查该文件是否已经接收
		gdsbatagtinf.setFilnm(filNm);
	    List<Gdsbatagtinf>	gdsbatagtinflist =gdsbatagtinfRepository.find(gdsbatagtinf);
	    if(CollectionUtils.isNotEmpty(gdsbatagtinflist)){
	    	log.info("check filedown exist...... ");
	    	throw new CoreException(ErrorCodes.EUPS_FTP_FILEDOWN_EXIST);
	    }
	    //=====检查文件名格式是否正确===44101_20140101_441049_001.txt 
	    if(filNm.length()!=29||filNm.substring(5,1)!="_"||filNm.substring(14,1)!="_"||filNm.substring(21,1)!="_"||filNm.substring(24,4)!=".txt"){
	    	log.info("check fileName rule error...... ");
	    	throw new CoreException(GDErrorCodes.EUPS_FILE_RULE_ERROR);
	    }
	    //====检验文件名中的业务类型==
	   if(gdsBid!=strGdsBId){
		 log.info("check fileName rule error...... ");
		 throw new CoreException(GDErrorCodes.EUPS_FILE_BUSTYPE_ERROR);	
		}
	  //===检查文件头中的网点号 ===
	   String Br=(String)context.getData(ParamKeys.BR);
	   if(filNm.substring(14,6)!=Br){
		 log.info("check fileName BR error...... ");
		 throw new CoreException(GDErrorCodes.EUPS_FILE_HEADBR_ERROR);		 
	 }
	  //======登录FTP开始下载文件到本地======
	  //取FTP信息
	    EupsThdFtpConfig eupsThdFtpConfig= operateFTPAction.getFTPInfo(strGdsBId, eupsThdFtpConfigRepository);
	   //登录FTP
	   operateFTPAction.loginFTP(eupsThdFtpConfig);
	   //===设置远程文件名称=====
	   eupsThdFtpConfig.setRmtWay(filNm);
	   // ===下载远程文件到本地
	   operateFTPAction.getFileFromFtp(eupsThdFtpConfig);
	   //====解析文件====String filePath, String fileName, String fileId
	   map=operateFileService.pareseFileByPath(eupsThdFtpConfig.getLocDir(), filNm, GDParamKeys.GDS_BATCH_FILE);
	   //===把MAP转换List
	   List<Map<String,Object>> detailList=null;
	   
	   detailList=(List<Map<String,Object>>) map.get("detail");
	   //===明细的总笔数===
	   chkDtlCnt=detailList.size();
	   //===头笔数===
	   headCnt=(Integer) map.get("fhcnt");
	   //文件头中的汇总笔数
	   if(headCnt!=chkDtlCnt){
		    log.info("check fileName headCnt error...... ");
			throw new CoreException(GDErrorCodes.EUPS_FILE_HEADCNT_ERROR);		  
	   }
	   //===检验文件头的业务类型
	  String fhgdsbid=(String) map.get("fhgdsbid");
	  if(fhgdsbid!=gdsBid){
		    log.info("check fileName headgdsbid error...... ");
			throw new CoreException(GDErrorCodes.EUPS_FILE_HEADSBID_ERROR);	 
	  }
	 
	   //===检查文件头中的日期与文件的日期是否一致
	  String fhagtdat=(String) map.get("fhagtdat");
	  if(filNm.substring(6,8)!=fhagtdat){
		  log.info("check fileName headdate error...... ");
			throw new CoreException(GDErrorCodes.EUPS_FILE_HEADDATE_ERROR);	  
	  }
	  
	   //
	   for(int i=0;i<detailList.size();i++){
		   //==行账户性质不能为空
		   String acttyp=(String)detailList.get(i).get("acttyp");
		   if(StringUtils.isEmpty((String)detailList.get(i).get("acttyp"))){
			   log.info("check filedetail acttyp error...... "+"文件明细第，"+i+"行账户性质不能为空");
			  throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行账户性质不能为空"); 
		   }
		   //==行缴款账号不能为空
		   String actno=(String)detailList.get(i).get("actno");
            if(StringUtils.isEmpty((String)detailList.get(i).get("actno"))){
            	log.info("check filedetail actno error...... "+"文件明细第，"+i+"行缴款账号不能为空");
    			throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行缴款账号不能为空");
		   }
		   //==行缴款账号户名不能为空
           String actnm=(String)detailList.get(i).get("actnm");
            if(StringUtils.isEmpty(actnm)){
            	log.info("check fileName actnm error...... "+"文件明细第，"+i+"行缴款账号户名不能为空");
    			throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行缴款账号户名不能为空"); 
		   }
		   //==行证件类型不能为空 
            String idtyp=(String)detailList.get(i).get("idtyp");
         if(StringUtils.isEmpty(idtyp)){
        	 log.info("check filedetail idtyp error...... "+"文件明细第，"+i+"行证件类型不能为空");
        	 throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行证件类型不能为空");
		   }
		   //==行证件号码不能为空
         String idno=(String)detailList.get(i).get("idno");
		 if(StringUtils.isEmpty(idno)){
			 log.info("check filedetail idno error...... "+"文件明细第，"+i+"行证件号码不能为空");
				throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行证件号码不能为空");	   
				   }
		   //==行移动电话类型不能为空
		 String mobtyp=(String)(detailList.get(i).get("mobtyp"));
		 if(StringUtils.isEmpty(mobtyp)){
			 log.info("check filedetail mobtyp error...... "+"文件明细第，"+i+"行移动电话类型不能为空");
				throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行移动电话类型不能为空");	   
				   }
		   //==行移动电话号码不能为空
		   String mobtel=(String)detailList.get(i).get("mobtel");
		 if(StringUtils.isEmpty(mobtel)){
			 log.info("check filedetail mobtel error...... "+"文件明细第，"+i+"行移动电话号码不能为空");
				throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行移动电话号码不能为空");	   
				   }
				   //==行企业编码不能为空
				   
		 if(StringUtils.isEmpty((String)detailList.get(i).get("orgcod"))){
			 log.info("check fileName orgcod error...... "+"文件明细第，"+i+"行企业编码不能为空");
				throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行企业编码不能为空");	   
				   }
				   //==行业务种类代码不能为空
				String tbustp=  (String)detailList.get(i).get("tbustp");
		 if(StringUtils.isEmpty(tbustp)){
			 log.info("check fileName tbustp error...... "+"文件明细第，"+i+"行业务种类代码不能为空");
				throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行业务种类代码不能为空");	   
				   }
				   //==行用户编号不能为空
				   
		 if(StringUtils.isEmpty((String)detailList.get(i).get("tcusid"))){
			 log.info("check filedetail tcusid error...... "+"文件明细第，"+i+"行用户编号不能为空");
				throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行用户编号不能为空");	   
				   }
		   //==行用户名称不能空
		   
          if(StringUtils.isEmpty((String)detailList.get(i).get("tcusnm"))){
        	log.info("check filedetail tcusnm error...... "+"文件明细第，"+i+"行用户名称不能空");
        	throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行用户名称不能空");	 
		   }
		   //==行生效日期不能为空
         if(StringUtils.isEmpty((String)detailList.get(i).get("effdat"))){
    	   log.info("check filedetail effdat error...... "+"文件明细第，"+i+"行生效日期不能为空");
			throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行生效日期不能为空");
		   }
		   //===行移动签约种类不能为空TAgtTp
		   String tAgtTp=(String) detailList.get(i).get("tagttp");
		   if(strGdsBId=="44103"&&StringUtils.isEmpty(tAgtTp)){
			   log.info("check filedetail tagttp error...... "+"文件明细第，"+i+"行移动签约种类不能为空");
				throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行移动签约种类不能为空");  
		   }
		   //===行移动主号号码不能为空
		   if(strGdsBId=="44103"&&StringUtils.isEmpty((String)detailList.get(i).get("mcusid"))){
			   log.info("check fileName mcusid error...... "+"文件明细第，"+i+"行移动主号号码不能为空");
				throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"行移动主号号码不能为空");
		   }
		   
		   mcusid=(String)detailList.get(i).get("mcusid");
		   
		   //====如果是移动签约，且为主号签约，则设置主号号码与TCusId一致===
		   if(strGdsBId=="44103"&&tAgtTp=="0"){
			  // detailList.set(detailList.indexOf(o), element)
			   mcusid=(String)detailList.get(i).get("tcusid");
		   }
		 
		   //===状态正常为N
		   //status="N";
		    Date appealTime = new Date();
			// 交易时间
			String strTime = DateUtils.format(appealTime,
					DateUtils.STYLE_yyyyMMddHHmmss);
		   //获取流水号6为日期+流水号最后六位
		    StringBuffer strSub = new StringBuffer();
		    String sqn = bbipublicService.getBBIPSequence();
		    String strDate = DateUtils.format(appealTime, "yyyyMMdd");
		    context.setData("strDate", strDate);
		    //取最后六位
		    String id= strSub.append(strDate).append(sqn.subSequence(sqn.length()-6, 6)).toString();
		    //把明细list转换成bean
		    Gdsbatagtinf gdsbatagtinf1=  BeanUtils.toObject(detailList.get(i), Gdsbatagtinf.class);
		    gdsbatagtinf1.setFilnm(filNm);
		    gdsbatagtinf1.setMcusid(mcusid);
		    gdsbatagtinf1.setImptim(strTime);
		    gdsbatagtinf1.setStatus(status);
		    gdsbatagtinf1.setId(id);
		  //===插入gdsBatAgtInf
		    try{
		    gdsbatagtinfRepository.insert(gdsbatagtinf1);
		    isContinue="Y";
		    
		    }catch(Exception CoreException ){
		       isContinue="N";
		 	   log.info("check fileName insert error...... "+"文件明细第，"+i+"插入不成功");
		 	   throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"明细登记失败");
		    }
		    //==============上送主机进行身份证验证==============
		    if(isContinue=="Y"){
		    	context.setData("QRYACT_ACTTYP", acttyp);
		    	context.setData("QRYACT_ACTNO", actno);
		    	context.setData("idno", "idno");
		        preDealCheck(context);
		        isContinue=context.getData("isContinue");
		        status=context.getData("status");
		        
		    }
		    //=============检查是否已经签约 根据账号ActNo、号码TCusId、TBusTp===========
		    if(isContinue=="Y"){
		    	context.setData("GdsBId", strGdsBId);
		    	context.setData("ActNo", actno);
		    	context.setData("TBusTp", tbustp);
		    	checkAgtInfo(context);
		    	isContinue=context.getData("isContinue");
		    	status=context.getData("status");
		    }
		    gdsbatagtinf1.setPrctim(strTime);
		    gdsbatagtinf1.setStatus((String)context.getData("status"));
		    gdsbatagtinf1.setRetcod((String)context.getData("retcod"));
		    gdsbatagtinf1.setRetcod((String)context.getData("retmsg"));
		    gdsbatagtinf1.setStatus(status);
		    try{
		    gdsbatagtinfRepository.save(gdsbatagtinf1);
		    }catch(Exception CoreException){
		    	   log.info("update table  gds_batagt_inf error...... "+"文件明细第，"+i+"修改不成功");
			 	   throw new CoreException(GDErrorCodes.EUPS_FILE_DETAIL_ERROR,i+"修改失败");
		    }
	   }
	  
	 
	}
	
	//报表打印 BBIPIBV0001
	public void reportPrint(Context context) throws CoreException, CoreRuntimeException{
		
		
	}
	
	//浏览
    public void reportBrowse(Context context) throws CoreException, CoreRuntimeException{
     //生成报表之前的验证RptSrc=STRCAT($GdsBId,_batimp_suc)报表模板验证
    	String gdsBId=(String)context.getData("gdsBId");
    	StringBuffer strSub = new StringBuffer();
    	String batimp_suc=strSub.append(gdsBId).append("_batimp_suc").toString();
    	String rptFmtFil=context.getData(GDParamKeys.GDS_RPTSUCC_CONFI);
    	//===无模板报异常
    	if(rptFmtFil.indexOf(batimp_suc)==-1){
    		log.info("print succfmtefil not exist...... ");
   	    	throw new CoreException(GDErrorCodes.PRINT_FMTFIL_ERROR);	
    	}
    	Gdsbatagtinf gdsbatagtinf=new Gdsbatagtinf();
    	//===========生成成功的报表================查询汇总信息和明细=============
    	gdsbatagtinf.setStatus("S");
    	gdsbatagtinf.setFilnm((String)context.getData("filNm"));
    	List <Gdsbatagtinf> gdsbatagtinfList=null;
    	
    	try{
    	gdsbatagtinfList =gdsbatagtinfRepository.find(gdsbatagtinf);
    	}catch(Exception CoreException){
    		log.info("renderReport succ fail..... ");
   	    	throw new CoreException(GDErrorCodes.PRINT_SUCC_ERROR);	
    	}
    	
        if(CollectionUtils.isNotEmpty(gdsbatagtinfList)){
        	 context.setData("reportId", GDParamKeys.GDS_SUCC_REPORT);
        	 //FilNam=STRCAT(GDS,$GdsBId,_,$TlrId,_Suc,.RPT)生成成功的报表名称
        	 StringBuffer str = new StringBuffer();
        	 String reportName=str.append("GDS").append(gdsBId).append("_").append(context.getData(ParamKeys.TELLER)).append("_Suc.RPT").toString();
        	 context.setData("reportName", reportName);
        	 renderActingCusReceiptFile(gdsbatagtinfList,context);
        	 context.setData("SucRpt", reportName);
        	 
   	    }
    	 //===========生成失败的报表============
        String batimp_fail=strSub.append(gdsBId).append("_batimp_fail").toString();
       	String rptFmtFail=context.getData(GDParamKeys.GDS_RPTFIAL_CONFI);
       	//模板的验证
       	if(rptFmtFail.indexOf(batimp_fail)==-1){
       		log.info("print failfmtefil not exist...... ");
   	    	throw new CoreException(GDErrorCodes.PRINT_FMTFIL_ERROR);		
       	}
    	   gdsbatagtinf.setStatus("F");
    	   List <Gdsbatagtinf> gdsbatagtinflist=null;
    	   try{
    		   
    	   gdsbatagtinflist =gdsbatagtinfRepository.find(gdsbatagtinf);
    	   
    	   }catch(Exception CoreException){
    			log.info("renderReport fail fail...... ");
       	    	throw new CoreException(GDErrorCodes.PRINT_FAIL_ERROR);	  
    	   }
    	 if(CollectionUtils.isNotEmpty(gdsbatagtinflist)){
    		 StringBuffer str = new StringBuffer();
    		 context.setData("reportId", GDParamKeys.GDS_FAIL_REPORT);
    		 String reportName=str.append("GDS").append(gdsBId).append("_").append(context.getData(ParamKeys.TELLER)).append("_Fail.RPT").toString();
        	 context.setData("reportName", reportName);
    		 renderActingCusReceiptFile(gdsbatagtinflist,context);
    		 context.setData("FailRpt", reportName);
      	   }
    	
    	
	}
    
    //上送主机进行身份证验证
	public void preDealCheck(Context context)
			throws CoreException {
		log.info("preDealCheck start!...............");
		context.setData("isContinue", "Y");
		context.setData("status", "S");
		//String qryactActtyp=context.getData("QRYACT_ACTTYP");
		String cusAc=context.getData("QRYACT_ACTNO");
		String idNo=context.getData("idno");
		//BBIPVNB0001 不清楚上送那些值
		CommonRequest commonRequest = CommonRequest.build(context);
		CusActInfResult cusActInfResult = accountService.getAcInf(commonRequest, cusAc);
		//校验身份信息
		if(cusActInfResult.equals("")||cusActInfResult==null||cusActInfResult.getIdNo()!=idNo){
			context.setData("retcod", "E00001");
			context.setData("retmsg", "卡号与身份证号不匹配");
			context.setData("status", "F");
			context.setData("isContinue", "N");
		}

	}
	//检查是否已经签约
	public void checkAgtInfo(Context context){
		log.info("checkAgtInfo start!...............");
		Gdsagtmob gdsagtmob=new Gdsagtmob();
		context.setData("isContinue", "Y");
		context.setData("status", "S");
		gdsagtmob.setActno((String)context.getData("ActNo"));
		gdsagtmob.setGdsbid((String)context.getData("GdsBid"));
		gdsagtmob.setTbustp((String)context.getData("TBusTp"));
		List<Gdsagtmob> gdsagtmobList=gdsagtmobRepository.find(gdsagtmob);
		if(CollectionUtils.isEmpty(gdsagtmobList)){
			context.setData("retcod", "E99999");
			context.setData("retmsg", "卡协议已存在");
			context.setData("status", "F");
			context.setData("isContinue", "N");
		}
	}
	
	private void renderActingCusReceiptFile(List<Gdsbatagtinf> gdsbatagtinfList,Context context)throws CoreException,CoreRuntimeException {
		log.info("renderActingCusReceiptFile start!...............");
		context.setData("gdsbatagtinfList", gdsbatagtinfList);//明细
        context.setData("totCnt", gdsbatagtinfList.size());//总笔数
        context.setData("tlrId", context.getData(ParamKeys.TELLER));//制作人
        context.setData("filNm", context.getData("filNm"));//文件名
        context.setData("gdsBid", context.getData("gdsBid"));//企业种类
        Date appealTime = new Date();
        String strDate = DateUtils.format(appealTime, "yyyyMMdd");
       //打印日期
        context.setData("prntDat",strDate.substring(0, 4));
        context.setData("prntMoth",strDate.substring(4, 2));
        context.setData("prntDay",strDate.substring(6, 2));

       String reportFileName =context.getData("reportName") ;// 打印报表文件名
       String reportId=context.getData("reportId");
       try {
    	   GdEupsbUtils.renderReportFile(context, reportRender, mftpTransfer, reportId, reportFileName, localDir, bbosDir);
       } catch (FileNotFoundException e) {
         
           throw new CoreException(GDErrorCodes.PRINT_REPORT_ERROR);
       }
      
   }

    /*public void agtSignDeal(Context context,GdsAgtInf gdsAgtInf) throws CoreException, CoreRuntimeException{
		//====查询签约主表信息是否存在该记录
    	
    	GdsAgtInf gdsagtinf=new GdsAgtInf();
    	Gdsagtunca gdsagtunca =new Gdsagtunca();
    	Gdsagtmob gdsagtmob =new Gdsagtmob();
    	String actno=gdsAgtInf.getActNo();
    	String gdsBid=gdsAgtInf.getGdsBid();
    	gdsagtinf.setActNo(actno);
    	gdsagtinf.setGdsBid(gdsBid);
    	//=====移动===
    	gdsagtmob.setActno(actno);
    	gdsagtmob.setGdsbid(gdsBid);
    	//===联通===
    	gdsagtunca.setActno(actno);
    	gdsagtunca.setGdsbid(gdsBid);
    	List <GdsAgtInf> gdsAgtInflist=null;
    	//联通
    	List <Gdsagtunca> gdsagtuncalist =null;
    	//移动
    	List <Gdsagtmob> gdsagtmoblist=null;
    	try{
    	gdsAgtInflist=gdsAgtInfRepository.find(gdsagtinf);
    	
    	}catch(Exception CoreException){//查询协议表出错
    		
    	}
    	///===不为空修改数据
    	if(CollectionUtils.isNotEmpty(gdsAgtInflist)){
    		try{
    		gdsAgtInfRepository.updateOldAgtInfo(BeanUtils.toMap(gdsAgtInf));
    		
    		}catch(Exception CoreException){
    			
    			
    		}
    		//===查询字表是否有数据44103 44102
    		if(gdsBid=="44102"){
    			//查询联通字表是否有数据
    			try{
    			gdsagtuncalist= gdsagtuncaRepository.find(gdsagtunca);
    			}catch(Exception CoreException){
    				
    			}
    			//===存在更新
    			if(CollectionUtils.isNotEmpty(gdsagtuncalist)){
    				gdsagtuncaRepository.s
    			}
    			
    		}else if(gdsBid=="44103"){
    			//查询移动字表是否有数据
    			try{
    			gdsagtmoblist=gdsagtmobRepository.find(gdsagtmob);
    			}catch(Exception CoreException){
    				
    			}
    		}else if(gdsBid==""){
    			
    		}else if(gdsBid==""){
    			
    		}
    		
    		
    	}
    	//为空新增数据
    	if(CollectionUtils.isEmpty(gdsAgtInflist)){
    		gdsAgtInf.setIvdDat("99991231");
    		gdsAgtInf.setEffDat((String)context.getData("strDate"));
    		gdsAgtInfRepository.insert(gdsAgtInf);
    	}
    	//------处理子表记录，将所有记录置"无效"
	}*/
}

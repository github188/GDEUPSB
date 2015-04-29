package com.bocom.bbip.gdeupsb.strategy.sign;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;

import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.expand.AgtValidCheckService;
import com.bocom.bbip.gdeupsb.interceptors.GDUNCBHttpSoapTrasnport;
import com.bocom.bbip.gdeupsb.repository.GdsAgtWaterRepository;
import com.bocom.bbip.gdeupsb.repository.GdsRunCtlRepository;

import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * @category 广东联通协议校验
 * 
 * @author qc.w
 * 
 */
public class GduncbAgtValidCheckImlAction implements AgtValidCheckService {

	private final static Logger log = LoggerFactory.getLogger(GduncbAgtValidCheckImlAction.class);

	@Autowired
	GdsAgtWaterRepository gdsAgtWaterRepository;

	@Autowired
	BBIPPublicService bBIPPublicService;
	
	@Autowired
	GdsRunCtlRepository gdsRunCtlRepository;
	/*@Autowired
    private GduncbThdTcpServiceAccessObject gduncbThdTcpServiceAccessObject;*/
	@Autowired
	GDUNCBHttpSoapTrasnport GDUNCBHttpTransport;
	@Override
	public Map<String, Object> agtValidCheckService(Context context) throws CoreException {
		log.info("agtValidCheckService start!..");
		boolean flag=context.getDataMap().containsKey("funcTyp");
		String gdsBId = context.getData(GDParamKeys.SIGN_STATION_BID); // 业务类型
		GdsRunCtl gdsRunctl = context.getVariable(GDParamKeys.SIGN_STATION_RUN_CTL_INFO);
		if(null==gdsRunctl){
			gdsRunctl=gdsRunCtlRepository.findOne(gdsBId); 
		}
		
		String agtMtb = gdsRunctl.getAgtMtb(); // 协议主表
		String agtStb = gdsRunctl.getAgtStb(); // 协议子表

		// 批量查询，获取协议表数据
		Map<String, Object> inpara = new HashMap<String, Object>();
		inpara.put("agtMtb", agtMtb);
		inpara.put("agtStb", agtStb);
		List<Map<String, Object>> agtList = gdsAgtWaterRepository.findAgtCheckInf(inpara);
		if (CollectionUtils.isEmpty(agtList)) {
			log.info("未发现协议信息！开始解锁交易并退出..");
			// 交易解锁
			/*Result ret1 = bBIPPublicService.unlock(gdsBId);
			int status1 = ret1.getStatus();
			if (status1 != 0) {
				throw new CoreException(GDErrorCodes.EUPS_UNLOCK_FAIL, "交易解锁失败!!!");
			}*/
			throw new CoreException(GDErrorCodes.EUPS_SIGN_NO_RECORD_FOUND);
		}
		for (Map<String, Object> agtDeatil : agtList) {
			// <Quote name="CbsAgtProc"/>金融平台相关操作不做
			//广东联通和广东移动需调用第三方
			
			String lAgtSt = "S";
			String lErMsg = "验证成功";

			// 水费不需要调用第三方
			String tAgtSt = "S";
			String terMsg = "验证成功";
			String actNo = agtDeatil.get("ACT_NO").toString().trim(); // 卡号
			String gdsAId = agtDeatil.get("GDS_AID").toString().trim(); // 协议号
			String subSts = agtDeatil.get("SUB_STS").toString().trim(); // 状态
			//44102联通业务本地协议转发流程 、44103移动业务协议第三方验证流程
            if(gdsRunctl.getThdChk().trim().equals("0")){
            	context.setData("tcusid",agtDeatil.get("TCUS_ID") );
            	context.setData("actNo", actNo);
            	context.setData("actName", agtDeatil.get("ACT_NM"));
            	context.setData("gdsBid", gdsBId);
            	context.setData("actTyp", agtDeatil.get("ACT_TYP"));
            	context.setData("idNo", agtDeatil.get("ID_NO"));
            	context.setData("idTyp", agtDeatil.get("ID_TYP"));
            	context.setData("cusNam", agtDeatil.get("TCUS_NM"));
            	context.setData("mstTel", agtDeatil.get("MCUS_ID"));
            	context.setData("sigTel", agtDeatil.get("TCUS_ID"));
            	context.setData("sigFlg", agtDeatil.get("TAGT_ST"));
            	preQueryThddeal(context);
            	tAgtSt=(String)context.getData("TAgtSt");
            	terMsg=(String)context.getData("TErMsg");
            }
		

			inpara.put("lAgtSt", lAgtSt);
			inpara.put("tAgtSt", tAgtSt);
			inpara.put("lerMsg", lErMsg);
			inpara.put("terMsg", terMsg);
			inpara.put("actNo", actNo);
			inpara.put("gdsAId", gdsAId);
			inpara.put("subSts", subSts);
			inpara.put("gdsBId", gdsBId);
			
			
			System.out.println("===============inpara=" + inpara);
			gdsAgtWaterRepository.updateAgtChkSts(inpara);
		}

		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> preQueryThddeal(Context context)throws CoreException{
		//GduncbHeaderVo gduncbHeaderVo=new GduncbHeaderVo();
		Map<String,Object> requestData = new HashMap<String,Object>();
		Map<String,Object> infoMap = new HashMap<String,Object>();
		Map<String,Object> map = new HashMap<String,Object>();
		String sqn=bBIPPublicService.getBBIPSequence();
		//String gdsBId=context.getData("gdsBid").toString();
		String strTime = DateUtils.format(new Date(),
				DateUtils.STYLE_yyyyMMddHHmmss);

	        requestData.put("MSG_SENDER", "5101");
			requestData.put("MSG_RECEIVER", "5100");
			requestData.put("TRANS_IDO", sqn);
			requestData.put("PROCESS_TIME", strTime);
			requestData.put("EPARCHY_CODE", "0755");
			requestData.put("CHANNEL_ID", "7BB73");
			requestData.put("OPER_ID", "SZDQWSC1");//context.getData("tlr"));
			requestData.put("SERVICE_NAME", "UserInfoService");
			requestData.put("OPERATE_NAME", "qryUserProInfo");
			requestData.put("RSP_CODE", "");
			requestData.put("RSP_DESC", "");
			requestData.put("TEST_FLAG", "0");
			requestData.put("SERIAL_NUMBER", context.getData("tcusid"));
			requestData.put("SERVICE_CALSS_CODE", "G");
			requestData.put("TransCode", "qryUserProInfo");
			
			context.setData("TransCode", "qryUserProInfo");
			//Object obj=null;
		 try {
			 infoMap=(Map<String, Object>) GDUNCBHttpTransport.submit(requestData, context);
		 } catch (CommunicationException e) {
			 if(context.getDataMap().containsKey("funcTyp")==false){
				throw new CoreException( "签约失败连接第三方失败");	
				}
		 } catch (JumpException e) {
			 if(context.getDataMap().containsKey("funcTyp")==false){
				throw new CoreException( "签约失败连接第三方失败");	
				}
		 }
		 
		 log.info("context========11232334"+context.getDataMap());
		 log.info("obj========11232334"+infoMap);
		 String RSP_CODE=(String)infoMap.get("RSP_CODE");
	if(RSP_CODE.equals("000000")||RSP_CODE.equals("0000")||RSP_CODE.equals("00")){
        requestData.put("MSG_SENDER", "5101");
		requestData.put("MSG_RECEIVER", "5100");
		requestData.put("TRANS_IDO", sqn);
		requestData.put("PROCESS_TIME", strTime);
		requestData.put("EPARCHY_CODE", "0755");
		requestData.put("CHANNEL_ID", "7BB73");
		requestData.put("OPER_ID","SZDQWSC1" );//context.getData("tlr"));
		requestData.put("SERVICE_NAME", "AcctInfoService");
		 requestData.put("OPERATE_NAME", "acctInfoChange");
		 requestData.put("RSP_CODE", "");
		 requestData.put("RSP_DESC", "");
		 requestData.put("TEST_FLAG", "0");
		 requestData.put("SERIAL_NUMBER", (String)context.getData("tcusid"));
		 requestData.put("ACCT_TYPE", context.getData("actTyp"));
		 requestData.put("PAY_TYPE", "8");
		 requestData.put("SUPER_BANK_CODE", "JT");
		 requestData.put("BANK_CODE", "JTYH001");
		 requestData.put("CONSIGN_NO", (String)context.getData("actNo"));
		 requestData.put("CONSIGN_NAME", context.getData("actName"));
		 requestData.put("SERVICE_CALSS_CODE", "G");
	     requestData.put("TransCode", "acctInfoChange");
			
	   context.setData("TransCode", "acctInfoChange");
	   try {
		   map= (Map<String, Object>) GDUNCBHttpTransport.submit(requestData, context);
		 } catch (CommunicationException e) {
			 if(context.getDataMap().containsKey("funcTyp")==false){
					throw new CoreException( "签约失败连接第三方失败");	
				}
		 } catch (JumpException e) {
			 if(context.getDataMap().containsKey("funcTyp")==false){
					throw new CoreException( "签约失败连接第三方失败");	
				}
		 }
	   log.info("context========333333"+context.getDataMap());
		 log.info("obj========444444"+map);
		String rspCode=(String)map.get("RSP_CODE");
      if(rspCode.equals("000000")||rspCode.equals("0000")||rspCode.equals("00")){
    		context.setData("TAgtSt", "S");
			context.setData("TErMsg", "签约成功");
			context.setData("status", "S");
			context.setData("retcod", "000000");
			context.setData("retmsg", "签约成功");
      }else if(!rspCode.equals("000000")&&!rspCode.equals("0000")&&!rspCode.equals("00")){
    	    context.setData("TAgtSt", "F");
			context.setData("TErMsg", "签约失败"+"["+map.get("RSP_DESC")+"]");
			context.setData("status", "F");
			context.setData("retcod", map.get("RSP_CODE"));
			context.setData("retmsg", "签约失败"+"["+map.get("RSP_DESC")+"]");  
			if(context.getDataMap().containsKey("funcTyp")==false){
				throw new CoreException( "签约失败"+"["+map.get("RSP_DESC")+"]");	
			}
			//throw new CoreException( "签约失败"+"["+map.get("RSP_DESC")+"]");	   
      }
	
		}else if(!RSP_CODE.equals("000000")&&!RSP_CODE.equals("0000")&&!RSP_CODE.equals("00")){
			    context.setData("TAgtSt", "F");
				context.setData("TErMsg", "签约失败"+"["+infoMap.get("RSP_DESC")+"]");
				context.setData("status", "F");
				context.setData("retcod", infoMap.get("RSP_CODE"));
				context.setData("retmsg", "签约失败"+"["+infoMap.get("RSP_DESC")+"]");
				if(context.getDataMap().containsKey("funcTyp")==false){
					throw new CoreException( "签约失败"+"["+infoMap.get("RSP_DESC")+"]");	
				}
				//throw new CoreException( "签约失败"+"["+infoMap.get("RSP_DESC")+"]");	  	
		}
		return null;
	}
}


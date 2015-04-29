package com.bocom.bbip.gdeupsb.strategy.sign;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;

import com.bocom.bbip.gdeupsb.action.vo.gdmobb.GdmobbHeaderVo;

import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.expand.AgtValidCheckService;
import com.bocom.bbip.gdeupsb.repository.GdsAgtWaterRepository;
import com.bocom.bbip.gdeupsb.repository.GdsRunCtlRepository;
import com.bocom.bbip.gdeupsb.service.impl.gdmobb.GdmobbResult;
import com.bocom.bbip.gdeupsb.service.impl.gdmobb.GdmobbThdTcpServiceAccessObject;
import com.bocom.bbip.gdeupsb.utils.UtilsCnlty;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 协议校验-广东移动
 * 
 * @author tandun
 * 
 */
public class GdmobbAgtValidCheckImlAction implements AgtValidCheckService {

	private final static Logger log = LoggerFactory.getLogger(GdmobbAgtValidCheckImlAction.class);

	@Autowired
	GdsAgtWaterRepository gdsAgtWaterRepository;

	@Autowired
	BBIPPublicService bBIPPublicService;
	
	@Autowired
	GdsRunCtlRepository gdsRunCtlRepository;

	@Autowired
    private GdmobbThdTcpServiceAccessObject gdmobbThdTcpServiceAccessObject;
	@Override
	public Map<String, Object> agtValidCheckService(Context context) throws CoreException {
		log.info("agtValidCheckService start!..");

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
            if(gdsRunctl.getThdChk().toString().trim().equals("0")){
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
            	context.setData("sigFlg", agtDeatil.get("TAGT_TP"));
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
	
	private Map<String, Object> preQueryThddeal(Context context)throws CoreException{
		
		Map<String,Object> requestData = new HashMap<String,Object>();
		String sqn=bBIPPublicService.getBBIPSequence();
		
		//报文头
		//44102联通业务本地协议转发流程 、44103移动业务协议第三方验证流程
	
		    //进行修改签约信息	
			GdmobbHeaderVo gdmobbHeaderVo=new GdmobbHeaderVo();
			//Date date=bbipPubService.getAcDate();
			String strTime = DateUtils.format(new Date(),
					DateUtils.STYLE_yyyyMMddHHmmss);
		    requestData = new HashMap<String,Object>();
			StringBuffer str=new StringBuffer();
			//String sqn=((String)context.getData(ParamKeys.SEQUENCE));
			String seqNo=sqn.substring(sqn.length()-10,sqn.length());
			String tlogno=str.append("10JT").append(strTime).append(sqn.substring(sqn.length()-13,sqn.length())).toString();
			//<!--01：A接口；02：C接口；03：T接口；06：M接口；08：N接口--> 长度为2
			gdmobbHeaderVo.setCategory("02");
			//<!--操作码-->
			gdmobbHeaderVo.setOpcode("0003");
	        //<!--命令状态-->长度为4
			gdmobbHeaderVo.setCommand_status("0000");
			// <!--消息目的地址，同步保留-->
			gdmobbHeaderVo.setDes_addr("2000GD");

			// <!--序列号，同步保留-->截流水号从第5位开始截10位长度
			gdmobbHeaderVo.setSeq_no(seqNo);
			//<!--消息源地址,同步保留-->
			gdmobbHeaderVo.setSou_addr("1000JT");
			// <!--01：业务请求;02：业务请求应答-->
			gdmobbHeaderVo.setSub_command("01");
			//账户类型
			
			if(StringUtils.isEmpty(((String)context.getData("sigFlg")))){
				context.setData("sigFlg", "1");
				context.setData("sigTel", context.getData("sigTel"));
			}
			String acttype=context.getData("actTyp");
			String rsvval1=" ";
			String 	actno=UtilsCnlty.fillEmpty((String)context.getData("actNo"),28);
			String  actNam=UtilsCnlty.fillEmpty((String)context.getData("actName"),32);
			String  idTyp=((String)context.getData("idTyp"));
			String  idNo=UtilsCnlty.fillEmpty(((String)context.getData("idNo")),20);
			String  cusNam=UtilsCnlty.fillEmpty(((String)context.getData("cusNam")),32);
			String  mstTel=UtilsCnlty.fillEmpty(((String)context.getData("mstTel")),20);
			String  sigFlg=((String)context.getData("sigFlg"));
			String  sigTel=UtilsCnlty.fillEmpty(((String)context.getData("sigTel")),20);
			String  rsvval2=UtilsCnlty.fillEmpty(rsvval1,20);
			String pacttyp="";
			if(acttype.equals("9")){
				pacttyp="29";
			}else if(acttype.equals("5")){
				pacttyp="25";
			}else if(acttype.equals("6")){
				pacttyp="26";		
			}else if(acttype.equals("7")){
				pacttyp="27";
			}else if(acttype.equals("8")){
				pacttyp="28";
			}else if(acttype.equals("1")){
				pacttyp="21";
			}else if(acttype.equals("2")){
				pacttyp="22";
			}else if(acttype.equals("3")){
				pacttyp="23";
			}else if(acttype.equals("4")){
				pacttyp="24";
			}
		        String pidTyp = "Z";// 其它
		        if(idTyp.equals("15")){
		            pidTyp = "A";//居民身份证
		        }else if (idTyp.equals("17")) {
		            pidTyp = "E";//解放军士兵证
		        }else if (idTyp.equals("18")) {
		            pidTyp = "L";//武警士兵证
		        }else if (idTyp.equals("19")){
		            pidTyp = "G";// (港澳)回乡证/通行证 
		        }else if(idTyp.equals("20")){
		            pidTyp = "I";//(外国)护照
		        }else if(idTyp.equals("23")){
		            pidTyp = "F";//户口簿
		        } 
		     
		   
			requestData.put("tlogno", tlogno);
			requestData.put("bakcod", "JT");
			requestData.put("pacttyp", acttype);
			requestData.put("actno", actno);
			requestData.put("actnam", actNam);
			requestData.put("pidtyp", pidTyp);
			requestData.put("idno", idNo);
			requestData.put("cusnam", cusNam);
			requestData.put("msttel", mstTel);
			requestData.put("rsvval1", rsvval2);
			requestData.put("rsvval2", rsvval2);
			requestData.put("sigflg", sigFlg);
			requestData.put("sigtel", sigTel);
			//requestData.put("maccode", "00000000");//待定BBIPVNB0001
			//====请求第三方===
			GdmobbResult gdmobbResult =gdmobbThdTcpServiceAccessObject.callThdTcpService(gdmobbHeaderVo, requestData);
			
			if(gdmobbResult.getStatus()==0){
				if(gdmobbResult.getResponseMap().get("rspCod").equals("00")){
			    context.setData("TAgtSt", "S");
			    context.setData("status", "S");
			    context.setData("retcod", "000000");
			    context.setData("retmsg", "签约成功");
			    context.setData("TErMsg", "签约成功");
			    
				}else{
					 String responseCode=(String)gdmobbResult.getResponseMap().get("rspCod");
					  CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
					  // 获取第三方返回码
		             String responseMess =(String)cRspCdeAction.getThdRspCde(responseCode,  "GDMOBB");
					 //转换第三方错误吗
					 // throw new CoreException(responseCode);
		              context.setData("TAgtSt", "F");
					  context.setData("TErMsg", responseMess);
					  context.setData("status", "F");
					  context.setData("retcod", responseCode);
					  context.setData("retmsg", responseMess);
					  if(context.getDataMap().containsKey("funcTyp")==false){
							throw new CoreException( responseMess);	
						}
				}
			}else if(gdmobbResult.getStatus()==-2){
				context.setData("TAgtSt", "U");
				context.setData("TErMsg", "系统错误");
				context.setData("status", "U");
				context.setData("retcod", "E99999");
				context.setData("retmsg", "系统错误");
				if(context.getDataMap().containsKey("funcTyp")==false){
					throw new CoreException( "签约失败系统错误");	
				}
			}else if(gdmobbResult.getStatus()==3){
				context.setData("TAgtSt", "F");
				context.setData("TErMsg", "交易失败");
				context.setData("status", "F");
				context.setData("retcod", "E99999");
				context.setData("retmsg", "交易失败");
				if(context.getDataMap().containsKey("funcTyp")==false){
					throw new CoreException( "交易失败");	
				}
			}else{
				context.setData("TAgtSt", "F");
				context.setData("TErMsg", "未知错误");
				context.setData("status", "F");
				context.setData("retcod", "E99999");
				context.setData("retmsg", "未知错误");
				if(context.getDataMap().containsKey("funcTyp")==false){
					throw new CoreException( "未知错误");	
				}
			}
			
			
		
		return null;
	}
}

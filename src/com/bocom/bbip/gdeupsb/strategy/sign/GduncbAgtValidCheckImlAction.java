package com.bocom.bbip.gdeupsb.strategy.sign;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;

import com.bocom.bbip.gdeupsb.action.vo.gduncb.GduncbHeaderVo;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdsRunCtl;
import com.bocom.bbip.gdeupsb.expand.AgtValidCheckService;
import com.bocom.bbip.gdeupsb.repository.GdsAgtWaterRepository;
import com.bocom.bbip.gdeupsb.repository.GdsRunCtlRepository;

import com.bocom.bbip.gdeupsb.service.impl.gduncb.GduncbResult;
import com.bocom.bbip.gdeupsb.service.impl.gduncb.GduncbThdTcpServiceAccessObject;
import com.bocom.bbip.gdeupsb.utils.UtilsCnlty;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
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
	@Autowired
    private GduncbThdTcpServiceAccessObject gduncbThdTcpServiceAccessObject;
	
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
			Result ret1 = bBIPPublicService.unlock(gdsBId);
			int status1 = ret1.getStatus();
			if (status1 != 0) {
				throw new CoreException(GDErrorCodes.EUPS_UNLOCK_FAIL, "交易解锁失败!!!");
			}
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
	
	private Map<String, Object> preQueryThddeal(Context context)throws CoreException{
		GduncbHeaderVo gduncbHeaderVo=new GduncbHeaderVo();
		Map<String,Object> requestData = new HashMap<String,Object>();
		String sqn=bBIPPublicService.getBBIPSequence();
		//String gdsBId=context.getData("gdsBid").toString();
		//报文头
		//44102联通业务本地协议转发流程 
        String Rsp_code=" ";
        String Rsp_desc=" ";
        String Oper_id="ITFJTYH1";
        String Channel_id="A3B23";
        String Operate_name="qryUserProInfo";
        String Service_name="UserInfoService";
        String Eparchy_code="0020";
		String strTime = DateUtils.format(new Date(),
				DateUtils.STYLE_yyyyMMddHHmmss);
		gduncbHeaderVo.setChannel_id(UtilsCnlty.fillEmptyRt(Channel_id, 20));
		gduncbHeaderVo.setEparchy_code(UtilsCnlty.fillEmptyRt(Eparchy_code, 6));//待确认
		gduncbHeaderVo.setMsg_receiver("5100");
		gduncbHeaderVo.setOper_id(UtilsCnlty.fillEmptyRt(Oper_id, 20));
		gduncbHeaderVo.setOperate_name(Operate_name);
		gduncbHeaderVo.setProcess_time(strTime);
		gduncbHeaderVo.setRsp_code(UtilsCnlty.fillEmpty(Rsp_code, 16));
		gduncbHeaderVo.setRsp_desc(UtilsCnlty.fillEmpty(Rsp_desc, 500));
		gduncbHeaderVo.setService_name(Service_name);
		gduncbHeaderVo.setTest_flag("0");
		gduncbHeaderVo.setTrans_ido(UtilsCnlty.fillEmpty(sqn, 30));
		gduncbHeaderVo.setMsg_sender("5101");
		
		//报文体
		String svrtyp="GDSUNICOM";
		requestData.put("svrtyp", UtilsCnlty.fillEmptyRt(svrtyp, 20));
		requestData.put("qrytyp", "0001");
		requestData.put("serial_number", UtilsCnlty.fillEmptyRt((String)context.getData("tcusid"),20));
		requestData.put("service_calss_code", "G");
		GduncbResult gduncbResult =gduncbThdTcpServiceAccessObject.callThdTcpService(gduncbHeaderVo, requestData);
		if(gduncbResult.getStatus()==0){
			 Operate_name="acctInfoChange";
			 Service_name="AcctInfoService";
			gduncbHeaderVo.setOperate_name(Operate_name);
			gduncbHeaderVo.setService_name(Service_name);
		
			//报文体
			String super_bank_code="JT";
			String bank_code="JTYH001";
			requestData.put("svrtyp", UtilsCnlty.fillEmptyRt(svrtyp, 20));
			requestData.put("qrytyp", "0002");
			requestData.put("serial_number", UtilsCnlty.fillEmptyRt((String)context.getData("tcusid"),20));
			requestData.put("service_calss_code", "G");
			requestData.put("acct_type", context.getData("actTyp"));
			requestData.put("pay_type", "8");
			requestData.put("super_bank_code", UtilsCnlty.fillEmptyRt(super_bank_code,8));
			requestData.put("bank_code", UtilsCnlty.fillEmptyRt(bank_code,8));
			requestData.put("consign_no",  UtilsCnlty.fillEmptyRt((String)context.getData("actNo"),30));
			requestData.put("consign_name",context.getData("actName"));
			GduncbResult gduncbResult1 =gduncbThdTcpServiceAccessObject.callThdTcpService(gduncbHeaderVo, requestData);
			if(gduncbResult1.getStatus()==0){
				String rspCode=gduncbResult1.getResponseMap().get("rsp_code").toString().trim();
				if(rspCode.equals("0000")||rspCode.equals("300377")){
					context.setData("TAgtSt", "S");
					context.setData("TErMsg", "签约成功");
				}else{
					context.setData("TAgtSt", "F");
					context.setData("TErMsg", "签约失败"+"["+rspCode+"]");
				}
			}else if(gduncbResult1.getStatus()==-2){
				context.setData("TAgtSt", "U");
				context.setData("TErMsg", "系统错误");
			}else if(gduncbResult1.getStatus()==3){
				context.setData("TAgtSt", "F");
				context.setData("TErMsg", "交易失败");
			}else{
				context.setData("TAgtSt", "F");
				context.setData("TErMsg", "未知错误");
			}
		}else if(gduncbResult.getStatus()==3){
		   context.setData("TAgtSt", "U");
			context.setData("TErMsg", "系统错误");
		}else if(gduncbResult.getStatus()==3){
			context.setData("TAgtSt", "F");
			context.setData("TErMsg", "交易失败");
		}else{
			context.setData("TAgtSt", "F");
			context.setData("TErMsg", "未知错误");
		}
		
		return null;
	}
}

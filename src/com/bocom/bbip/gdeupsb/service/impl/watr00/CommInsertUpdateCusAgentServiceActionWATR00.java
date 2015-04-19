package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdEupsWatAgtInf;
import com.bocom.bbip.gdeupsb.repository.GdEupsWatAgtInfRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 协议新增修改
 * @author hefengwen
 *
 */
public class CommInsertUpdateCusAgentServiceActionWATR00 extends BaseAction{
	
	private static Logger logger = LoggerFactory.getLogger(CommInsertUpdateCusAgentServiceActionWATR00.class);
	
	@Autowired
	GdEupsWatAgtInfRepository gdEupsWatAgtInfRepository;
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		String oprTyp = context.getData("oprTyp");//操作类型：0-新增，1-修改
		logger.info("oprTyp["+oprTyp+"]");
		context.setData(ParamKeys.CUS_AC, context.getData("cusAC"));
		context.setData(ParamKeys.BUS_TYP, "0");  //业务类型暂定为代收
		if("0".equals(oprTyp)){
			context.setData("ageBr", context.getData(ParamKeys.BK));
			context.setData("agrBr", context.getData(ParamKeys.BR));
			Map<String, Object> agentMap = new HashMap<String, Object>();
//			agentMap.put("agdAgrNo", (String)context.getData("agdAgrNo"));
//			agentMap.put("cusAc", (String)context.getData("cusAc"));
			agentMap.put("acoAc", (String)context.getData("cusAc"));
			agentMap.put("pwd", (String)context.getData("pwd"));
//			agentMap.put("bvCde", (String)context.getData("agtCllCusId"));
//			agentMap.put("bvNo", (String)context.getData("agtCllCusId"));
			agentMap.put("comNo", "4450000685");
			agentMap.put("comNum", "汕头自来水公司");
			
			agentMap.put("busTyp","0");
			agentMap.put("busKnd", "A115");
//			agentMap.put("busKndNme", (String)context.getData("agtCllCusId"));
			agentMap.put("ccy", "CNY");
			agentMap.put("cusFeeDerFlg", "0");  //个人账户收费减免标志
			agentMap.put("agtSrvCusId", (String)context.getData("thdCusNo"));
			agentMap.put("agtSrvCusPnm", (String)context.getData("thdCusNme"));
			agentMap.put("agrVldDte", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
			agentMap.put("agrExpDte", "99991231");
			agentMap.put("ageBr", context.getData(ParamKeys.BK));
			agentMap.put("agrBr", context.getData(ParamKeys.BR));
//			agentMap.put("agrVldDte", (String)context.getData("agtCllCusId"));
//			agentMap.put("des1", (String)context.getData("agtCllCusId"));
//			agentMap.put("des2", (String)context.getData("agtCllCusId"));
//			agentMap.put("des3", (String)context.getData("agtCllCusId"));
//			agentMap.put("des4", (String)context.getData("agtCllCusId"));
//			agentMap.put("des5", (String)context.getData("agtCllCusId"));
//			agentMap.put("selId", (String)context.getData("agtCllCusId"));
//			agentMap.put("selNme", (String)context.getData("agtCllCusId"));
//			agentMap.put("rcoId", (String)context.getData("agtCllCusId"));
//			agentMap.put("rcoNme", (String)context.getData("agtCllCusId"));
//			agentMap.put("pedAgrSts", (String)context.getData("agtCllCusId"));
//			agentMap.put("mkiEvtNo", (String)context.getData("agtCllCusId"));
//			agentMap.put("ageBr", (String)context.getData("agtCllCusId"));
//			agentMap.put("agrBr", (String)context.getData("agtCllCusId"));
//			agentMap.put("agrTlr", (String)context.getData("agtCllCusId"));
//			agentMap.put("athTlr", (String)context.getData("agtCllCusId"));
//			agentMap.put("agrTme", (String)context.getData("agtCllCusId"));
//			agentMap.put("cmuTel", (String)context.getData("agtCllCusId"));
//			agentMap.put("eml", (String)context.getData("agtCllCusId"));
			
			
			List<Map<String,Object>> agentCollectAgreement = new ArrayList<Map<String,Object>>();
			agentCollectAgreement.add(agentMap);
			context.setData(ParamKeys.AGENT_COLLECT_AGREEMENT, agentCollectAgreement);//上代收付用
			
			
			
			Map<String, Object> infoMap = new HashMap<String, Object>();
			List<Map<String,Object>> customerInfo = new ArrayList<Map<String,Object>>();
//			infoMap.put("agtCllCusId", "");//TODO:修改协议时必输
			infoMap.put("cusTyp", (String)context.getData("cusTyp"));
//			infoMap.put("cusAc", "");
			infoMap.put("ccy", "CNY");
			infoMap.put("idTyp", context.getData("idTyp"));
			infoMap.put("idNo", context.getData("idNo"));
			
			customerInfo.add(infoMap);
			context.setData("agrChl", "1");//签约渠道设为公共事业缴费，上代收付用
			context.setData("customerInfo", customerInfo);
		
			context.setData("ccy", "CNY");
			context.setData("bvCde", "008");
			context.setData("cusNo", context.getData("thdCusNo"));
			get(BBIPPublicService.class).synExecute("gdeupsb.commInsertCusAgent", context);
		}else if("1".equals(oprTyp)){
			
			
			//查询本地水费协议信息表，得到协议编号
			GdEupsWatAgtInf gdEupsWatAgtInf = new GdEupsWatAgtInf();
			gdEupsWatAgtInf.setThdCusNo((String)context.getData(ParamKeys.THD_CUS_NO));
			List<GdEupsWatAgtInf> infoList = gdEupsWatAgtInfRepository.find(gdEupsWatAgtInf);
			context.setData("agdAgrNo", infoList.get(0).getAgdAgrNo());
			
			Map<String, Object> agentMap = new HashMap<String, Object>();
//			agentMap.put("agdAgrNo", (String)context.getData("agdAgrNo"));
//			agentMap.put("cusAc", (String)context.getData("cusAc"));
			agentMap.put("acoAc", (String)context.getData("cusAc"));
			agentMap.put("pwd", (String)context.getData("pwd"));
//			agentMap.put("bvCde", (String)context.getData("agtCllCusId"));
//			agentMap.put("bvNo", (String)context.getData("agtCllCusId"));
			agentMap.put("comNo", "4450000685");
			agentMap.put("comNum", "汕头自来水公司");
			
			agentMap.put("busTyp","0");
			agentMap.put("busKnd", "A115");
//			agentMap.put("busKndNme", (String)context.getData("agtCllCusId"));
			agentMap.put("ccy", "CNY");
			agentMap.put("cusFeeDerFlg", "0");  //个人账户收费减免标志
			agentMap.put("agtSrvCusId", (String)context.getData("thdCusNo"));
			agentMap.put("agtSrvCusPnm", (String)context.getData("thdCusNme"));
			agentMap.put("agrVldDte", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
			agentMap.put("agrExpDte", "99991231");
			agentMap.put("agdAgrNo", (String)context.getData("agdAgrNo"));

			
			List<Map<String,Object>> agentCollectAgreement = new ArrayList<Map<String,Object>>();
			agentCollectAgreement.add(agentMap);
			context.setData(ParamKeys.AGENT_COLLECT_AGREEMENT, agentCollectAgreement);//上代收付用
			
			
			
			Map<String, Object> infoMap = new HashMap<String, Object>();
			List<Map<String,Object>> customerInfo = new ArrayList<Map<String,Object>>();
			infoMap.put("agtCllCusId", context.getData("agtCllCusId"));//TODO:修改协议时必输
			infoMap.put("cusTyp", (String)context.getData("cusTyp"));
//			infoMap.put("cusAc", "");
			infoMap.put("ccy", "CNY");
			infoMap.put("idTyp", context.getData("idTyp"));
			infoMap.put("idNo", context.getData("idNo"));
			
			customerInfo.add(infoMap);
			context.setData("agrChl", "1");//签约渠道设为公共事业缴费，上代收付用
			context.setData("customerInfo", customerInfo);
		
			context.setData("ccy", "CNY");
			context.setData("bvCde", "008");
			context.setData("cusNo", context.getData("thdCusNo"));
			get(BBIPPublicService.class).synExecute("gdeupsb.commUpdateCusAgent", context);
		}else{
			throw new CoreException("操作类型有误!");
		}
	}
	
}

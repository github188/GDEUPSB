package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdEupsWatAgtInf;
import com.bocom.bbip.gdeupsb.repository.GdEupsWatAgtInfRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.id.seq.StepSequenceFactory;

public class CallThdAction extends BaseAction{

private static Logger logger = LoggerFactory.getLogger(PreDelCusAgentAction.class);

	@Autowired
	ThirdPartyAdaptor callThdTradeManager;
	@Autowired
	GdEupsWatAgtInfRepository gdEupsWatAgtInfRepository;

	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("CallThdAction start......");
		GdEupsWatAgtInf gdeups = new GdEupsWatAgtInf();
		gdeups.setAgdAgrNo((String)context.getData("agdAgrNo"));
		gdEupsWatAgtInfRepository.delete(gdeups);
		// TODO:为第三方接口报文字段赋值，发送请求至第三方
				context.setData("type", "Y007");
				context.setData("accountdate", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
				StepSequenceFactory s = context.getService("logNoService");
				String logNo = s.create().toString();
				context.setData("waterno", "JH"+logNo);//流水号生成
				context.setData("bankcode", "交行");
				context.setData("salesdepart",((String)context.getData(ParamKeys.BR)).substring(2, 8));
				context.setData("salesperson", ((String)context.getData(ParamKeys.TELLER)).substring(4, 7));
				context.setData("busitime", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMddHHmmss));
				context.setData("thdRspCde", "0");
				context.setData("zprice", "");
				context.setData("months", "");
				context.setData("operano", "");
				context.setData("password", "        ");
				context.setData("md5digest", " ");
				
				System.out.println("context="+context);
				List<Map<String,Object>> infoList = context.getData("listAgreementInfo");
				Map<String, Object> infoMap = infoList.get(0);
				
//				List<Map<String,String>> agentCollectAgreements = context.getData("agentCollectAgreement");
//				List<Map<String,String>> customerInfos = context.getData("customerInfo");
				
				
				String hno = (String)infoMap.get("agtSrvCusId");
				String name = (String)infoMap.get("agtSrvCusPnm");
				String addr = context.getData("addr");
//						StepSequenceFactory s = context.getService("logNoService");
				logNo = s.create().toString();
				String wtno = logNo.substring(8);
				String bank = "交行";
				String czman = (String)context.getData("cusNme");
				String bcount =(String)context.getData("cusAc");
				String byyno = ((String)context.getData(ParamKeys.BR)).substring(2, 8);
				String byyman = ((String)context.getData(ParamKeys.TELLER)).substring(4, 7);
				String wtdate = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
//				String wtman = (String)infoMap.get("cusNme");
				String haddr = " ";
				String hphone = context.getData("hphone");
				String lphone = context.getData("lphone");
				String post = context.getData("post");
				String sjman = context.getData("sjman");
				String postno = context.getData("postno");
				String taddr = context.getData("taddr");
				String status = "D";
				
				context.setData("hno", hno);
				context.setData("name", name);
				context.setData("addr", addr);
				context.setData("wtno", wtno);
				context.setData("bank", bank);
				context.setData("czman", czman);
				context.setData("bcount", bcount);
				context.setData("byyno", byyno);
				context.setData("byyman", byyman);
				context.setData("wtdate", wtdate);
//				context.setData("wtman", wtman);
				context.setData("haddr", haddr);
				context.setData("hphone", hphone);
				context.setData("lphone", lphone);
				context.setData("post", post);
				context.setData("sjman", sjman);
				context.setData("postno", postno);
				context.setData("taddr", taddr);
				context.setData("status", status);
				
				
				Map<String,Object> thdReturnMessage = callThdTradeManager.trade(context);
				
				if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
					CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
					String responseCode = cRspCdeAction.getThdRspCde(thdReturnMessage, 	context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
					logger.info("responseCode:["+responseCode+"]");
					if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){
						logger.info(" callThd success!");
						context.setDataMap(thdReturnMessage);
					}else{
						if(StringUtil.isEmpty(responseCode)){
							responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
						}
						throw new CoreException(responseCode);
					}
				}else{
					logger.error(" callThd return has error!");
					throw new CoreException("BBIP4400EU0430");
				}
				logger.error(" callThd end!");
				logger.info("@@@@@@@@@@context="+context);
				
				
//				List<Map<String, Object>> agdAgrNoList = context.getData("agentCollectAgreement");
				
				
	}
}

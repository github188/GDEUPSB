package com.bocom.bbip.gdeupsb.action.efek;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsCusAgent;
import com.bocom.bbip.eups.repository.EupsCusAgentRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PowerChangeTreatyAction extends BaseAction{
	@Autowired
	BBIPPublicService bbipPublicService;
		/**
		 * 供电方发起供电到银行变更代扣协议请求
		 */
		public void execute(Context context)throws CoreException,CoreRuntimeException{
			log.info("===========Start PowerChangeTreatyAction");
			log.info("========context====="+context);
			context.setData(GDParamKeys.SVRCOD, "32");   
			context.setData(GDParamKeys.TOTNUM, "1");
//			context.setData(ParamKeys.SEQUENCE, bbipPublicService.getBBIPSequence());
			context.setData(ParamKeys.TXN_CHL, "EFE");  //交易渠道
			context.setData("CnlSub", "");
			String nodNo=context.getData(ParamKeys.BANK_NO).toString().substring(0, 3)+"800";
			context.setData("NodNo", nodNo);
		    //签订标志
			String conSign=context.getData(GDParamKeys.CONSIGN).toString();
			
			Map<String, Object> comMap = new HashMap<String, Object>();
			comMap.putAll(context.getDataMap());
			
			Result comResult= get(BGSPServiceAccessObject.class).callServiceFlatting("queryCorporInfo",comMap);
			//判断是否签约   conSign=2
			if(!CollectionUtils.isEmpty(comResult.getPayload()) && conSign.equals("2")){
					context.setData(ParamKeys.BANK_NO, context.getData(GDParamKeys.NEWBANKNO));
					context.setData(ParamKeys.CUS_AC, context.getData(GDParamKeys.NEWCUSAC));
					context.setData(ParamKeys.CUS_NME, context.getData(GDParamKeys.NEWCUSNAME));
					String accTyp=context.getData(ParamKeys.ACC_TYPE);
					if("0".equals(accTyp)){
						context.setData(ParamKeys.TXN_CODE, "109000");
					}else if("1".equals(accTyp) || "3".equals(accTyp)){
						context.setData(ParamKeys.TXN_CODE, "476520");
						context.setData(ParamKeys.CCY,"CNY");
					}else if("2".equals(accTyp) || "4".equals(accTyp)){
							context.setData(ParamKeys.MESSAGE_TYPE, "E");
							context.setData(ParamKeys.RSP_CDE, "EFE999");
				            context.setData(ParamKeys.RSP_MSG, "该类账户暂不支持");
					}else{
							context.setData(ParamKeys.MESSAGE_TYPE, "E");
							context.setData(ParamKeys.RSP_CDE, "EFE999");
							context.setData(ParamKeys.RSP_MSG, "账户类型错误");
							throw new CoreRuntimeException("账户类型错误");
					}
					context.setDataMap(comResult.getPayload());
					log.info("===========context======"+context);
					
					context.setData("ChkFlg", "S");
					context.setData(ParamKeys.MESSAGE_TYPE, "N");  
					context.setData(ParamKeys.RSP_CDE, GDConstants.SUCCESS_CODE);
					//修改签约表
					EupsCusAgent eupscusagent=BeanUtils.toObject(context.getDataMap(), EupsCusAgent.class);
					get(EupsCusAgentRepository.class).update(eupscusagent);
					log.info("===========End PowerChangeTreatyAction");
			}else{
				throw new CoreException("该单位未签约");
			}
		}
}

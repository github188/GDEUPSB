package com.bocom.bbip.gdeupsb.services.single.zerobalance;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.online.SpecialAccountSystemService;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.eups.spi.vo.PayFeeOnlineDomain;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.service.GDEGPS.EGPSExtSysPayService;
import com.bocom.bbip.gdeupsb.utils.GdZeroBanlanceUtils;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * @category 财政零余额 完成划扣交易的零余额功能
 * @author sunbg
 * @since 1.0.0 2015-03-24
 */
public class SpecialAccountSystemServiceImplGDEUPSA implements SpecialAccountSystemService{
	private Logger log = LoggerFactory.getLogger(SpecialAccountSystemServiceImplGDEUPSA.class);
	@Autowired
	private EGPSExtSysPayService egpsExtSysPayService;
	@Override
	public Map<String, Object> callSpecialAccountSystem(CommHeadDomain commHeadDomain,
			PayFeeOnlineDomain payFeeOnlineDomain, Context context) throws CoreException {
		/*
		 * 外围系统联机支付
		 */
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("paoAc", context.getData(ParamKeys.CUS_AC));//付款人账号(必输）
		dataMap.put("paoAcNme", "");//付款人户名
		dataMap.put("paeAc", "");//收款人账号
		dataMap.put("paeAcNme", "");//收款人户名
		dataMap.put("payAmt", new BigDecimal(context.getData(ParamKeys.TXN_AMT).toString()));//实际支付金额(必输）
		dataMap.put("hfe", "");//手续费
		dataMap.put("agtDat", "{\"busCnl\":\""+context.getData(ParamKeys.CHANNEL)+"\"}");//协议要素(必输) json 格式
		dataMap.put("prvDat", GdZeroBanlanceUtils.getPrvDat());//记账要素(必输) json 格式
		dataMap.put("mfmTxnCde", " ");//主机交易码(必输）
		dataMap.put("resonDat", "");//预留域 json格式，扩展用
		
		Result result = egpsExtSysPayService.submit("extSysChlPay", dataMap, context);
		if(result.getStatus()==0){
			Map<String,Object> resultMap = result.getPayload();//返回map
			Map<String,Object> map = context.getDataMap();
			map.putAll(resultMap);
			context.setDataMap(map);
		}else if(result.getStatus()==-1){
			log.info("请求服务超时！");
			throw new CoreException(GDErrorCodes.REQUEST_TIME_OUT);
		}else if(result.getStatus()==3){
			log.info("请求服务失败！");
			throw new CoreException("");
		}else if(result.getStatus()==10){
			log.info("请求服务发送失败！");
			throw new CoreException("");
		}else if(result.getStatus()==-2){
			log.info("系统错误！");
			throw new CoreException("");
		}else{
			log.info("请求服务未知错误！");
		}
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
		return null;
	}
	
}

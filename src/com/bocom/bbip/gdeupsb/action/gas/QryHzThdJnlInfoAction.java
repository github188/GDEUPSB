package com.bocom.bbip.gdeupsb.action.gas;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/** 
 * 惠州燃气代扣流水信息查询
 * 
 * @author WangMQ
 *
 */
public class QryHzThdJnlInfoAction extends BaseAction{

	private static Logger logger = LoggerFactory.getLogger(QryHzThdJnlInfoAction.class);
	
	public void execute(Context context) throws CoreException, CoreRuntimeException{
		logger.info("Enter in QryHzThdJnlInfoAction!!.....");
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		logger.info("=========context=====" + context);
		
//		select userno,payyea,actno,actnam,optamt,opttim,chkdat,tckno,logno,clogno from gastxnjnl491
//        where (userno='%s' or '%s'='') and (actno='%s' or '%s'='') 
//        and (actnam='%s' or '%s'='') and (chkflg='%s' or '%s'='') 
//        and OPTDAT &gt;='%s' and OPTDAT &lt;='%s' and HTxnSt='S' and HRspCd='SC0000' and Status='1' and ThdSts='B0'
//														主机交易状态		主机返回码		账务状态1正常扣款	返回第三方状态B0扣费成功

//eups流水表无status， thdSts, payyea使用备1， 备2, 预留6
//不能使用	BeanUtils.toObject();
			
	
			GdEupsTransJournal txnJnl = new GdEupsTransJournal();
			
			//TEST			TEST			TEST
			txnJnl.setEupsBusTyp("PGAS00");
			
			if(!((null==context.getData(ParamKeys.THD_CUS_NO)) || "".equals(context.getData(ParamKeys.THD_CUS_NO)) ||  "?".equals(context.getData(ParamKeys.THD_CUS_NO)) )){
				txnJnl.setThdCusNo(context.getData(ParamKeys.THD_CUS_NO).toString());
			}
			if(!((null==context.getData(ParamKeys.CUS_AC)) || "".equals(context.getData(ParamKeys.CUS_AC)) ||  "?".equals(context.getData(ParamKeys.CUS_AC)) )){
				txnJnl.setCusAc(context.getData(ParamKeys.CUS_AC).toString());
			}
			if(!((null==context.getData(ParamKeys.CUS_NME)) || "".equals(context.getData(ParamKeys.CUS_NME)) ||  "?".equals(context.getData(ParamKeys.CUS_NME)) )){
				txnJnl.setCusNme(context.getData(ParamKeys.CUS_NME).toString());
			}
			//
			logger.info("==========" + txnJnl.getThdCusNo() + "====" + txnJnl.getCusAc() + "=====" + txnJnl.getCusNme());
			
			txnJnl.setBeginDate(DateUtils.parse((String) context.getData("beginDate")));
			txnJnl.setEndDate(DateUtils.parse((String) context.getData("endDate")));
			logger.info("======" + txnJnl.getBeginDate() + "====" + txnJnl.getEndDate());
			
//			and HTxnSt='S' and HRspCd='SC0000' and Status='1' and ThdSts='B0'    在SQLMapping写死

			
			
			List<GdEupsTransJournal> txnJnlList = get(GdEupsTransJournalRepository.class).findGasJnlInfo(txnJnl);
			logger.info("==============txnJnlList====" + txnJnlList);
			
			List<Map<String,Object>> resultList=(List<Map<String, Object>>) BeanUtils.toMaps(txnJnlList);
			logger.info("==============resultList====" + resultList);

			
			if(CollectionUtils.isEmpty(resultList)){
				context.setData(GDParamKeys.GAS_MSG_TYP, "E");
				context.setData(GDParamKeys.GAS_AP_CDE, "SC");
				context.setData(GDParamKeys.GAS_OFMT_COD, "D04");
				context.setData(GDParamKeys.GAS_IN_POS, "0001");
				context.setData(GDParamKeys.GDEUPSB_RSP_MSG, "系统错误");
				context.setData(GDParamKeys.GAS_RSP_COD, GDConstants.GAS_ERROR_CODE);
				throw new CoreRuntimeException("系统异常");
			}else{
				List<Map<String,Object>> resultListTmp = new ArrayList<Map<String,Object>>();
				for(Map<String,Object> maps : resultList){
			        Map<String, Object> tempMap = new HashMap<String, Object>();
			        tempMap.put("thdCusNo", maps.get("thdCusNo"));
			        tempMap.put("payYea", maps.get("rsvFld6"));
			        tempMap.put("cusAc", maps.get("cusAc"));
			        tempMap.put("cusNme", maps.get("cusNme"));
			        tempMap.put("txnAmt", maps.get("txnAmt"));
			        tempMap.put("txnTme", maps.get("txnTme"));
			        tempMap.put("acDte", maps.get("acDte"));
			        tempMap.put("mfmVchNo", maps.get("mfmVchNo"));
			        tempMap.put("sqn", maps.get("sqn"));
			        tempMap.put("thdSqn", maps.get("thdSqn"));
					resultListTmp.add(tempMap);
				}
		        context.setData("loop", resultListTmp);
		        logger.info("=======resultListTmp in context ======" + context);
		        context.setData(GDParamKeys.GAS_AP_CDE, "32");
		        context.setData(GDParamKeys.GAS_OFMT_COD, "z01");
		        context.setData(GDParamKeys.GAS_PAGE_NO, "0001");
		        context.setData(GDParamKeys.GAS_VAR_SIZE, "3");
		        context.setData(GDParamKeys.GAS_TXN_TTL, "惠州燃气代扣流水信息查询");
		        context.setData(GDParamKeys.GAS_SUB_TTL, "查询内容");
				
				context.setData(GDParamKeys.GAS_MSG_TYP, "N");
				context.setData(GDParamKeys.GDEUPSB_RSP_MSG, "交易成功");
				context.setData(GDParamKeys.GAS_RSP_COD, GDConstants.GDEUPSB_TXN_SUCC_CODE);
			}
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
}

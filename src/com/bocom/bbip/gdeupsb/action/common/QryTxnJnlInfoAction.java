package com.bocom.bbip.gdeupsb.action.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/** 
 * 公共流水信息查询
 * 
 * @author WangMQ
 *
 */
public class QryTxnJnlInfoAction extends BaseAction{

	private static Logger logger = LoggerFactory.getLogger(QryTxnJnlInfoAction.class);
	
	public void execute(Context context) throws CoreException, CoreRuntimeException{
		logger.info("Enter in QryTxnJnlInfoAction!!.....");
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		logger.info("=========context=====" + context);
		
//		select userno,payyea,actno,actnam,optamt,opttim,chkdat,tckno,logno,clogno from gastxnjnl491
//        where (userno='%s' or '%s'='') and (actno='%s' or '%s'='') 
//        and (actnam='%s' or '%s'='') and (chkflg='%s' or '%s'='') 
//        and OPTDAT &gt;='%s' and OPTDAT &lt;='%s' and HTxnSt='S' and HRspCd='SC0000' and Status='1' and ThdSts='B0'
//														主机交易状态		主机返回码		账务状态1正常扣款	返回第三方状态B0扣费成功

//eups流水表无status， thdSts, payyea使用备1， 备2, 预留6
//不能使用	BeanUtils.toObject();
		
		
//
//		交易类型	eupsBusTyp	CHAR	10	Y
//		会计流水号	mfmVchNo	CHAR	20	
//		用户编号	thdCusNo	CHAR	40	
//		客户账号	cusAc	VARCHAR	40	
//		开始日期	beginDate	CHAR	10	Y
//		结束日期	endDate	CHAR	10	Y

			
	
			GdEupsTransJournal txnJnl = new GdEupsTransJournal();
			txnJnl.setEupsBusTyp((String) context.getData(ParamKeys.EUPS_BUSS_TYPE));
			if(!((null==context.getData(ParamKeys.THD_CUS_NO)) || "".equals(context.getData(ParamKeys.THD_CUS_NO)) ||  "?".equals(context.getData(ParamKeys.THD_CUS_NO)) )){
				txnJnl.setThdCusNo(context.getData(ParamKeys.THD_CUS_NO).toString());
			}
			if(!((null==context.getData(ParamKeys.CUS_AC)) || "".equals(context.getData(ParamKeys.CUS_AC)) ||  "?".equals(context.getData(ParamKeys.CUS_AC)) )){
				txnJnl.setCusAc(context.getData(ParamKeys.CUS_AC).toString());
			}
			if(!((null==context.getData(ParamKeys.MFM_VCH_NO)) || "".equals(context.getData(ParamKeys.MFM_VCH_NO)) ||  "?".equals(context.getData(ParamKeys.MFM_VCH_NO)) )){
				txnJnl.setCusNme(context.getData(ParamKeys.MFM_VCH_NO).toString());
			}
			logger.info("==========" + txnJnl.getThdCusNo() + "====" + txnJnl.getCusAc() + "=====" + txnJnl.getMfmVchNo());
			
			txnJnl.setBeginDate(DateUtils.parse((String) context.getData("beginDate")));
			txnJnl.setEndDate(DateUtils.parse((String) context.getData("endDate")));
			logger.info("== QRY JNL BETWEEN====" + txnJnl.getBeginDate() + " AND " + txnJnl.getEndDate());
			
			List<Map<String, Object>> qryTxnJnlInfo = get(GdEupsTransJournalRepository.class).findTxnJnlInfo(txnJnl);
			if(CollectionUtils.isEmpty(qryTxnJnlInfo)){
				logger.info("There are no records for select check trans journal ");
				throw new CoreException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}
			
			List<Map<String, Object>> temp = new ArrayList<Map<String,Object>>();
			for(Map<String, Object> tmpMap : qryTxnJnlInfo){
				Map<String, Object> tmp = new HashMap<String, Object>();
				tmp.put("sqn", tmpMap.get("SQN"));
				tmp.put("mfmVchNo", tmpMap.get("MFM_VCH_NO"));
				tmp.put("thdSqn", tmpMap.get("THD_SQN"));
				tmp.put("comNo", tmpMap.get("COM_NO"));
				tmp.put("thdCusNo", tmpMap.get("THD_CUS_NO"));
				tmp.put("cusNme", tmpMap.get("CUS_NME"));
				tmp.put("cusAc", tmpMap.get("CUS_AC"));
				tmp.put("reqTxnAmt", tmpMap.get("REQ_TXN_AMT"));
				tmp.put("hfe", tmpMap.get("HFE"));
				tmp.put("txnAmt", tmpMap.get("TXN_AMT"));
				tmp.put("txnDte", tmpMap.get("TXN_DTE"));
				tmp.put("txnTme", tmpMap.get("TXN_TME"));
				tmp.put("acDte", tmpMap.get("AC_DTE"));
				tmp.put("txnSts", tmpMap.get("TXN_STS"));
				temp.add(tmp);
			}
			context.setData("loop", temp);
			
			
//			List<GdEupsTransJournal> txnJnlList = get(GdEupsTransJournalRepository.class).findGasJnlInfo(txnJnl);
//			logger.info("==============txnJnlList====" + txnJnlList);
//			
//			List<Map<String,Object>> resultList=(List<Map<String, Object>>) BeanUtils.toMaps(txnJnlList);
//			logger.info("==============resultList====" + resultList);
//			
//			if(CollectionUtils.isEmpty(resultList)){
//				context.setData(GDParamKeys.GAS_MSG_TYP, "E");
//				context.setData(GDParamKeys.GAS_AP_CDE, "SC");
//				context.setData(GDParamKeys.GAS_OFMT_COD, "D04");
//				context.setData(GDParamKeys.GAS_IN_POS, "0001");
//				context.setData(GDParamKeys.GDEUPSB_RSP_MSG, "系统错误");
//				context.setData(GDParamKeys.GAS_RSP_COD, GDConstants.GAS_ERROR_CODE);
//				throw new CoreRuntimeException("系统异常");
//			}else{
//				List<Map<String,Object>> resultListTmp = new ArrayList<Map<String,Object>>();
//				for(Map<String,Object> maps : resultList){
//			        Map<String, Object> tempMap = new HashMap<String, Object>();
//			        tempMap.put("thdCusNo", maps.get("thdCusNo"));
//			        tempMap.put("payYea", maps.get("rsvFld6"));
//			        tempMap.put("cusAc", maps.get("cusAc"));
//			        tempMap.put("cusNme", maps.get("cusNme"));
//			        tempMap.put("txnAmt", maps.get("txnAmt"));
//			        tempMap.put("txnTme", maps.get("txnTme"));
//			        tempMap.put("acDte", maps.get("acDte"));
//			        tempMap.put("mfmVchNo", maps.get("mfmVchNo"));
//			        tempMap.put("sqn", maps.get("sqn"));
//			        tempMap.put("thdSqn", maps.get("thdSqn"));
//					resultListTmp.add(tempMap);
//				}
//		        context.setData("loop", resultListTmp);
//		        logger.info("=======resultListTmp in context ======" + context);
//		        context.setData(GDParamKeys.GAS_AP_CDE, "32");
//		        context.setData(GDParamKeys.GAS_OFMT_COD, "z01");
//		        context.setData(GDParamKeys.GAS_PAGE_NO, "0001");
//		        context.setData(GDParamKeys.GAS_VAR_SIZE, "3");
//		        context.setData(GDParamKeys.GAS_TXN_TTL, "惠州燃气代扣流水信息查询");
//		        context.setData(GDParamKeys.GAS_SUB_TTL, "查询内容");
//				
//				context.setData(GDParamKeys.GAS_MSG_TYP, "N");
//				context.setData(GDParamKeys.GDEUPSB_RSP_MSG, "交易成功");
//				context.setData(GDParamKeys.GAS_RSP_COD, GDConstants.GDEUPSB_TXN_SUCC_CODE);
//			}
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
}

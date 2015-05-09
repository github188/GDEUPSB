package com.bocom.bbip.gdeupsb.action.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdEupsTransJournal;
import com.bocom.bbip.gdeupsb.repository.GDEupsBatchConsoleInfoRepository;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.utils.BeanUtils;
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
		
		GdEupsTransJournal txnJnl = new GdEupsTransJournal();
			txnJnl.setEupsBusTyp((String) context.getData(ParamKeys.EUPS_BUSS_TYPE));
			if(!((null==context.getData(ParamKeys.THD_CUS_NO)) || "".equals(context.getData(ParamKeys.THD_CUS_NO)) ||  "?".equals(context.getData(ParamKeys.THD_CUS_NO)) )){
				txnJnl.setThdCusNo(context.getData(ParamKeys.THD_CUS_NO).toString());
			}
			if(!((null==context.getData(ParamKeys.CUS_AC)) || "".equals(context.getData(ParamKeys.CUS_AC)) ||  "?".equals(context.getData(ParamKeys.CUS_AC)) )){
				txnJnl.setCusAc(context.getData(ParamKeys.CUS_AC).toString());
			}
			if(!((null==context.getData(ParamKeys.MFM_VCH_NO)) || "".equals(context.getData(ParamKeys.MFM_VCH_NO)) ||  "?".equals(context.getData(ParamKeys.MFM_VCH_NO)) )){
				txnJnl.setMfmVchNo(context.getData(ParamKeys.MFM_VCH_NO).toString());
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
			
			
			Pageable pageable = BeanUtils.toObject(context.getDataMap(), PageRequest.class);
			Page<Map<String, Object>> page = get(GdEupsTransJournalRepository.class).findTxnJnlInfo(pageable, txnJnl);
			logger.info("=== page.getElements() :" + page.getElements());
			
			setResponseFromPage(context, "loop", page);
			logger.info("============== contxt after set loop : " + context);

			
			List<Map<String, Object>> tempList = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> pageElements = page.getElements();
			for(Map<String, Object> tmpMap : pageElements){
				Map<String, Object> tmp = new HashMap<String, Object>();
				//wsdl接口全部为Char
				tmp.put("sqn", tmpMap.get("SQN"));
				tmp.put("mfmVchNo", tmpMap.get("MFM_VCH_NO"));
				tmp.put("thdSqn", tmpMap.get("THD_SQN"));
				tmp.put("comNo", tmpMap.get("COM_NO"));
				tmp.put("thdCusNo", tmpMap.get("THD_CUS_NO"));
				tmp.put("cusNme", tmpMap.get("CUS_NME"));
				tmp.put("cusAc", tmpMap.get("CUS_AC"));
				
				tmp.put("reqTxnAmt", tmpMap.get("REQ_TXN_AMT")+"");
				tmp.put("hfe", ""+tmpMap.get("HFE"));
				tmp.put("txnAmt", ""+tmpMap.get("TXN_AMT"));
				tmp.put("txnDte", DateUtils.format((Date)tmpMap.get("TXN_DTE"), DateUtils.STYLE_SIMPLE_DATE));
				tmp.put("txnTme", DateUtils.format((Date)tmpMap.get("TXN_TME"), DateUtils.STYLE_FULL));
				tmp.put("acDte", DateUtils.format((Date)tmpMap.get("AC_DTE"), DateUtils.STYLE_SIMPLE_DATE));
				
				tmp.put("txnSts", tmpMap.get("TXN_STS"));
				tempList.add(tmp);
			}
			context.setData("loop", tempList);
			logger.info("==============context after set loop to context:" + context);
			
		/*	int pageNum = (Integer) context.getData("pageNum");
			int pageSize = (Integer) context.getData("pageSize");

			int totalElements = tempList.size();
			int totalPages = 0;
			if (totalElements % pageSize == 0) {
				totalPages = totalElements / pageSize;
			} else {
				totalPages = totalElements / pageSize + 1;
			}

			List<Map<String, Object>> pageableResponse = new ArrayList<Map<String, Object>>();
			Map<String, Object> pageMap = new HashMap<String, Object>();
			pageMap.put("totalElements", totalElements);
			pageMap.put("totalPages", totalPages);
			pageableResponse.add(pageMap);
			context.setData("pageableResponse", pageableResponse);
			
			context.setData("totalElements", totalElements);
			context.setData("totalPages", totalPages);*/
			
			
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
}

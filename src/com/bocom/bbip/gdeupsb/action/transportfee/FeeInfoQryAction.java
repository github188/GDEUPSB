package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class FeeInfoQryAction extends BaseAction{
	Log log = LogFactory.getLog(FeeInfoQryAction.class);
	
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("FeeInfoQryAction start.......");
		
//			<Exec func="PUB:ReadRecord" error="IGNORE">
//            <Arg name="SqlCmd" value="TolOfRoadTxn"/>
//         </Exec>
			
//			 SELECT count(*) as TolNum
//	           FROM   rbfbtxnbok444
//	           WHERE  BrNo='%s' and (TCusId='%s' or '%s'='粤C') and (PayDat BETWEEN '%s' AND '%s') and (Status='%s' or '%s'='A')
//	         </Sentence>
//	         <Fields>BrNo|TCusId|TCusId|BgnDat|EndDat|Status|Status|</Fields>
//			<Item name="TCusId"    length="20" expression="DELBOTHSPACE($TCusId)"/>      <!--车牌号码-->
//            <Item name="Status"    length="1"  expression="DELBOTHSPACE($Status)"/>      <!--状态查询-->
//            <Item name="BgnDat"    length="8"  expression="DELBOTHSPACE($BgnDat)"/>      <!--开始日期-->
//            <Item name="EndDat"    length="8"  expression="DELBOTHSPACE($EndDat)"/>      <!--结束日期-->
//         <If condition="~RetCod!=0">
//            <Set>MsgTyp=E</Set>
//            <Set>RspCod=329999</Set>
//            <Set>RspMsg=交易成功</Set>  
//            <Return/>
//         </If>
//         <If condition="$TolNum=0">
//            <Set>MsgTyp=E</Set>
//            <Set>RspCod=329999</Set>
//            <Set>RspMsg=无记录</Set>  
//            <Return/>
//         </If>
		int totalElements;
		ctx.setData(GDParamKeys.BR_NO, GDConstants.BR_NO);
		GDEupsbTrspFeeInfo gdEupsbTrspFeeInfo = new GDEupsbTrspFeeInfo();
		gdEupsbTrspFeeInfo.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbTrspFeeInfo.setBrNo(GDConstants.BR_NO);
		gdEupsbTrspFeeInfo.setBegDat(DateUtils.parse(ctx.getData(GDParamKeys.BEG_DAT).toString(),DateUtils.STYLE_SIMPLE_DATE));
		gdEupsbTrspFeeInfo.setEndDat(DateUtils.parse(ctx.getData(GDParamKeys.END_DAT).toString(),DateUtils.STYLE_SIMPLE_DATE));
		log.info(DateUtils.parse(ctx.getData(GDParamKeys.BEG_DAT).toString(),DateUtils.STYLE_SIMPLE_DATE));
		gdEupsbTrspFeeInfo.setStatus(ctx.getData(GDParamKeys.STATUS).toString());
		if("T".equals(ctx.getData("tiaTyp"))){  //tiaTyp来源未知
			totalElements = gdEupsbTrspFeeInfoRepository.findInfoCount(gdEupsbTrspFeeInfo);
			if(totalElements == 0){
				ctx.setData(ParamKeys.RSP_MSG, "无记录");
				throw new CoreRuntimeException(ErrorCodes.EUPS_QUERY_NO_DATA);
			}else{
				ctx.setData(GDParamKeys.totalElements, totalElements);
			}
		}
			
//			int pageNum =ctx.getData("pageNum");
//			int pageSize =ctx.getData("pageSize");
//			 Pageable pageable =new PageRequest(pageNum,pageSize);
//			 System.out.println(123);
//			List<GDEupsbTrspFeeInfo> feeInfoList = gdEupsbTrspFeeInfoRepository.findInfo(pageable,gdEupsbTrspFeeInfo);
		 System.out.println(123);
			Pageable pageable =  BeanUtils.toObject(ctx.getDataMap(), PageRequest.class);
			Page<GDEupsbTrspFeeInfo> TrspFeeInfoPage = get(GDEupsbTrspFeeInfoRepository.class).findInfo(pageable,gdEupsbTrspFeeInfo);
			
			System.out.println(5646);
//				List<Map<String,Object>> resultList=(List<Map<String, Object>>) BeanUtils.toMaps(feeInfoList);
//				ctx.setData("resultList", resultList);
			setResponseFromPage(ctx, "resultList", TrspFeeInfoPage);
			
	}
	
	

}

package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspNpManag;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspNpManagRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class NpQryAction extends BaseAction {
	private final static Log log = LogFactory.getLog(NpQryAction.class);

	@Autowired
	GDEupsbTrspNpManagRepository gdEupsbTrspNpManagRepository;

	public void execute(Context ctx) throws CoreException, CoreRuntimeException {
		log.info("NpQryAction start......");
//---------------------------------------------------------------------------------------------
		// 统计总数,成功或失败
		GDEupsbTrspNpManag gdEupsbTrspNpManag = new GDEupsbTrspNpManag();
		gdEupsbTrspNpManag.setBegDat((Date) ctx.getData(GDParamKeys.BEG_DAT));
		gdEupsbTrspNpManag.setEndDat((Date) ctx.getData(GDParamKeys.END_DAT));
		gdEupsbTrspNpManag.setNodNo((String)ctx.getData(GDParamKeys.NOD_NO));
		gdEupsbTrspNpManag.setStatus(ctx.getData(GDParamKeys.STATUS).toString());
		gdEupsbTrspNpManag.setIdNo(ctx.getData("idNo").toString());
		gdEupsbTrspNpManag.setCarNo(ctx.getData(GDParamKeys.CAR_NO).toString());
		List<Integer> tolNum = gdEupsbTrspNpManagRepository.findCountSum(gdEupsbTrspNpManag);
		if (0 == tolNum.get(0)) {
			ctx.setData(ParamKeys.RSP_MSG, "无记录");
		} else {
			ctx.setData(ParamKeys.RSP_MSG, "交易成功");
		}

		Pageable pageable = BeanUtils.toObject(ctx.getDataMap(), PageRequest.class);
//		Page<GDEupsbTrspNpManag> TrspNpManagPage = get(GDEupsbTrspNpManagRepository.class).findNpInfo(pageable, gdEupsbTrspNpManag);
//		setResponseFromPage(ctx, "npResultList", TrspNpManagPage);
		
		
		//---------------------------------------------------
		int totalElements;
		totalElements = gdEupsbTrspNpManagRepository.findCountSum(gdEupsbTrspNpManag).get(0);
		int size = pageable.getPageSize();
        int num = pageable.getPageNum();
        int totalPages = size == 0 ? 0 : (int)Math.ceil((double)totalElements / (double)size);
		
        log.info("@@@@@@@@@@@@@="+totalElements+"!!"+size+"!!"+num+"!!"+totalPages);
        Iterable<Map<String, Object>> list = null;
        if(num <= totalPages)
        {
        	Page<GDEupsbTrspNpManag> TrspNpManagPage = get(GDEupsbTrspNpManagRepository.class).findNpInfo(pageable, gdEupsbTrspNpManag);
            ctx.setData("totalElements", Long.valueOf(TrspNpManagPage.getTotalElements()));
            ctx.setData("totalPages", Integer.valueOf(TrspNpManagPage.getTotalPages()));           
			list = BeanUtils.toMaps(TrspNpManagPage.getElements());
		
              	
            }     
            
            ctx.setData("npResultList", list);
//			List<Map<String,Object>> resultList=(List<Map<String, Object>>) BeanUtils.toMaps(feeInfoList);
//			ctx.setData("resultList", resultList);
//		setResponseFromPage(ctx, "resultList", TrspFeeInfoPage);
		log.info("@@@@@@@context end="+ctx);

		
	}

}

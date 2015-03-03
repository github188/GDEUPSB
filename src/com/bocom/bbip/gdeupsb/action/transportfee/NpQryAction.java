package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.Date;
import java.util.List;

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

		System.out.println(ctx.getDataMap());
		// 统计总数,成功或失败
		GDEupsbTrspNpManag gdEupsbTrspNpManag = new GDEupsbTrspNpManag();
		gdEupsbTrspNpManag.setBegDat((Date) ctx.getData(GDParamKeys.BEG_DAT));
		gdEupsbTrspNpManag.setEndDat((Date) ctx.getData(GDParamKeys.END_DAT));
		gdEupsbTrspNpManag.setNodNo(ctx.getData(GDParamKeys.NOD_NO).toString());
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
		Page<GDEupsbTrspNpManag> TrspNpManagPage = get(GDEupsbTrspNpManagRepository.class).findNpInfo(pageable, gdEupsbTrspNpManag);
		setResponseFromPage(ctx, "npResultList", TrspNpManagPage);
	}

}

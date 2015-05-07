package com.bocom.bbip.gdeupsb.action.elea;

import java.util.Date;
import java.util.List;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdElecClrInf;
import com.bocom.bbip.gdeupsb.repository.GdElecClrInfRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 广州电力第三方清算日期定时日切
 * 
 * @author qc.w
 * 
 */
public class EleAutoThdAcDChangeAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("开始进行广州电力第三方日切交易!..");

		GdElecClrInfRepository gdElecClrInfRepository = get(GdElecClrInfRepository.class);
		// 根据单位编号(协议号)查询清算信息
		GdElecClrInf gdElecClrInf = new GdElecClrInf();
		gdElecClrInf.setcAgtNo("GDELEC01");
		List<GdElecClrInf> gdElecClrInfList = gdElecClrInfRepository.find(gdElecClrInf);
		if (CollectionUtils.isEmpty(gdElecClrInfList)) {
			log.error("不存在清算日期参数");
			throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CLEAR_INFO_ERROR); // 不存在清算参数信息
		}
		else {
			gdElecClrInf = gdElecClrInfList.get(0);
			// 将清算日期+1，并更新相关信息表
			String clrDatStr = gdElecClrInf.getClrDat();
			Date clrDat = DateUtils.parse(clrDatStr);
			clrDat = DateUtils.calDate(clrDat, 1);
			gdElecClrInf.setClrDat(DateUtils.format(clrDat, DateUtils.STYLE_yyyyMMdd));
			gdElecClrInfRepository.updClrDte(gdElecClrInf);
			log.info("第三方清算日期change from [" + clrDatStr + "] to [" + DateUtils.format(clrDat, DateUtils.STYLE_yyyyMMdd) + "]");
		}
	}
}

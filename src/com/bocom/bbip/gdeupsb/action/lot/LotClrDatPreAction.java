package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotSysCfg;
import com.bocom.bbip.gdeupsb.repository.GdLotSysCfgRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 清算轧差准备-获取系统配置
 * 
 * @author qc.w
 * 
 */
public class LotClrDatPreAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("LotClearAcDealAction start!..");
		// 获取系统配置
		String dealId = GDConstants.LOT_DEAL_ID; // 运营商编号
		GdLotSysCfg gdLotSysCfg = get(GdLotSysCfgRepository.class).findOne(dealId);

		// 查询代收单位协议信息
		String dscAgtNo = gdLotSysCfg.getDsCAgtNo(); // 代收单位编号
		Map<String, Object> inpara = new HashMap<String, Object>();
		inpara.put(ParamKeys.COMPANY_NO, dscAgtNo);
		inpara.put("inqBusLstFlg", "N");

		// TODO：for test,先注释掉
		// Result dsResult =
		// get(BGSPServiceAccessObject.class).callServiceFlatting("queryCorporInfo",
		// inpara);
		Map<String, Object> dsMap = new HashMap<String, Object>();
		// dsMap = dsResult.getPayload();

		String gcActNo = (String) dsMap.get("actNo");// 交易账户
		String fCActNo = (String) dsMap.get("hfeStlAc"); // 代收结算账户,用于处理轧差入账帐号

		String curTim = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss); // 当前时间

		context.setVariable(GDParamKeys.GD_LOT_SYS_CFG, gdLotSysCfg);
		context.setVariable(GDParamKeys.LOT_CURTIM, curTim);
		context.setVariable(GDParamKeys.LOT_FC_ACT_NO, fCActNo);
		log.info("LotClearAcDealAction end!..");
	}
}

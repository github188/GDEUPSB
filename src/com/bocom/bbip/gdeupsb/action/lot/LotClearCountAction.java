package com.bocom.bbip.gdeupsb.action.lot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotSysCfg;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotSysCfgRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class LotClearCountAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("LotClearCountAction start!..");

		String gameId = context.getData(GDParamKeys.LOT_GAME_ID); // 游戏编码
		String clrDat = context.getData(GDParamKeys.LOT_CLR_DAT);// 清算日期
		String difFlg = new String();

		// 获取系统配置
		   List<GdLotSysCfg> lotSysCfgInfos  = get(GdLotSysCfgRepository.class).findSysCfg();

		// 查询代收单位协议信息
		String dscAgtNo = lotSysCfgInfos.get(0).getDsCAgtNo(); // 代收单位编号
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
		String fCActNo = (String) dsMap.get("hfeStlAc"); // 代收结算账户

		// 查询代发单位协议信息
		String dfcAgtNo = lotSysCfgInfos.get(0).getDfCAgtNo(); // 代发单位编号
		inpara.put(ParamKeys.COMPANY_NO, dfcAgtNo);

		// TODO：for test,先注释掉
		// Result dfResult =
		// get(BGSPServiceAccessObject.class).callServiceFlatting("queryCorporInfo",
		// inpara);
		Map<String, Object> dfMap = new HashMap<String, Object>();
		// dfMap = dfResult.getPayload();
		String dfActNo = (String) dfMap.get("hfeStlAc"); // 代发结算账户

		GdLotDrwTblRepository gdLotDrwTblRepository = get(GdLotDrwTblRepository.class);
		// 统计轧差和垫付金额
		GdLotDrwTbl gdLotDrwTbl = new GdLotDrwTbl();
		gdLotDrwTbl.setGameId(gameId);
		gdLotDrwTbl.setSalEnd(clrDat);
		List<Map<String, Object>> totPatList = gdLotDrwTblRepository.findTolPayInf(gdLotDrwTbl);
		if (CollectionUtils.isNotEmpty(totPatList)) {
			Map<String, Object> totPatMap = totPatList.get(0);
			String tolPayAmt = (String) totPatMap.get("TOTPAYAMT");
			context.setData("tolPayAmt", tolPayAmt); // 垫付金额
		}

		// 统计轧差金额失败
		List<Map<String, Object>> totDifList = gdLotDrwTblRepository.findTolDifInf(gdLotDrwTbl);
		Map<String, Object> totDifInf = totDifList.get(0);
		long tolDifAmtL = (Long) totDifInf.get("TOLDIFAMT");
		String tolDifAmt = String.valueOf(tolDifAmtL);

		if (tolDifAmt.contains("-")) {
			difFlg = "0";
			tolDifAmt = tolDifAmt.substring(1); // 去掉"-"
		}
		else {
			difFlg = "1";
		}
		context.setData("tolDifAmt", tolDifAmt); // 轧差金额
		context.setData("difFlg", difFlg); // 轧差标识

		// 查询内部账户余额
		// 判断账户是否余额不足
		// TODO: for test,先注释掉
		// DefaultGemsServiceAccessObject gemsServiceAccessObject =
		// get(DefaultGemsServiceAccessObject.class);
		// GIA gia = gemsServiceAccessObject.build(context, "TP9000");
		// GemsResult gemsResult = gemsServiceAccessObject.callService(gia,
		// context.getDataMap());
		// Map<String, Object> balQry = gemsResult.getPayload();
		// String curBal = (String) balQry.get("bal"); // 余额
		String curBal = "20000.00";
		context.setData("curBal", curBal);

		String clrFlg = new String();
		// 检查是否可以进行清算
		List<String> chkList = gdLotDrwTblRepository.findChkClrFlg(gdLotDrwTbl);
		if (CollectionUtils.isEmpty(chkList)) {
			clrFlg = "1";
		} else {
			clrFlg = "0";
		}
		context.setData("clrFlg", clrFlg);
		log.info("LotClearCountAction end!..");
	}

}

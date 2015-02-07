package com.bocom.bbip.gdeupsb.action.lot;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 福彩清算-更新垫款状态
 * 
 * @author qc.w
 * 
 */
public class LotClearDfUpdAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("LotClearDfUpdAction start!..");

		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_REVERSAL); // 初始化状态为需要冲正
		String payAmt = context.getData(GDParamKeys.LOT_CLEAR_PAY_AMT); // 还款金额
		if (!"0".equals(payAmt)) {

			String gameId = context.getData(GDParamKeys.LOT_GAME_ID); // 游戏id
			String curTim = context.getVariable(GDParamKeys.LOT_CURTIM); // 当前时间
			String clrDat = context.getData("clrDat"); // 清算日期

			// 更新垫付状态
			GdLotDrwTbl lotDrwTbl = new GdLotDrwTbl();
			lotDrwTbl.setGameId(gameId);
			lotDrwTbl.setRtnTim(curTim);
			lotDrwTbl.setSalEnd(clrDat); // 销售结束时间-设置为清算日期
			get(GdLotDrwTblRepository.class).updateTolPayInf(lotDrwTbl);
		}
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}
}

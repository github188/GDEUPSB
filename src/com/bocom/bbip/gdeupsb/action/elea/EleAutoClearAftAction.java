package com.bocom.bbip.gdeupsb.action.elea;

import java.util.Date;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.entity.GdElecClrInf;
import com.bocom.bbip.gdeupsb.entity.GdElecClrJnl;
import com.bocom.bbip.gdeupsb.repository.GdElecClrInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdElecClrJnlRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class EleAutoClearAftAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("修改清算基本信息状态为交易状态！");
		GdElecClrInf gdElecClrInf=context.getVariable("clrInf");
		gdElecClrInf.setClrSts("0");  //交易状态
		gdElecClrInf = get(GdElecClrInfRepository.class).save(gdElecClrInf);
		
		log.info("准备更新清算流水信息!..");
		GdElecClrJnl clrJnl = context.getVariable("clrJnl"); // 清算流水信息
		clrJnl.setClrRst("1"); // 更新清算状态为已清算
		clrJnl.setClrTim(DateUtils.format(new Date(), "hhmmss"));
		get(GdElecClrJnlRepository.class).save(clrJnl);

		log.info("更新清算流水信息成功");
	}

}

package com.bocom.bbip.gdeupsb.action.elea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.entity.GdElecClrJnl;
import com.bocom.bbip.gdeupsb.repository.GdElecClrJnlRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class EleAutoClrInfQryAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("查询电力清算情况开始");

		// 清算日期
		String clrDat = context.getData("delDte");

		GdElecClrJnl para = new GdElecClrJnl();
		para.setClrDat(clrDat);
		List<GdElecClrJnl> qryInf = get(GdElecClrJnlRepository.class).find(para);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (GdElecClrJnl jnl : qryInf) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("clrNodNo", jnl.getNodNo());
			resultMap.put("clrTlrId", jnl.getTlrId());
			resultMap.put("clrCAgtNo", jnl.getcAgtNo());
			resultMap.put("delDte", jnl.getClrDat());
			resultMap.put("delTme", jnl.getClrTim());
			resultMap.put("clrRst", jnl.getClrRst());
			resultMap.put("clrTyp", jnl.getClrTyp());
			resultMap.put("clrTot", jnl.getClrTot());
			resultMap.put("clrAmt", jnl.getClrAmt());
			result.add(resultMap);
		}
		log.info("返回的result=" + result);
		context.setData("clrInf", result);
	}

}

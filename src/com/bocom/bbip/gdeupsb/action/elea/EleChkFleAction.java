package com.bocom.bbip.gdeupsb.action.elea;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.repository.GdEupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.utils.CodeSwitchUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 供电局财务对帐文件生成交易
 * 
 * @author qc.w
 * 
 */
public class EleChkFleAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("财务对帐文件生成 start!..");

		String dptTyp = context.getData("dptTyp");
		String clrDat=context.getData("clrDat");

		log.info("当前的dptTyp=" + dptTyp);
		// 获取CAgtNo
		String cAgtNoDl = CodeSwitchUtils.codeGenerator("DptTypToCAgtNo_DL", dptTyp);
		context.setData("cAgtNo_dl", cAgtNoDl);

		String cAgtNoYh = CodeSwitchUtils.codeGenerator("DptTypToCAgtNo_YH", dptTyp);
		context.setData("cAgtNo_yh", cAgtNoYh);

		String cAgtNoPl = CodeSwitchUtils.codeGenerator("DptTypToCAgtNo_PL", dptTyp);
		context.setData("cAgtNo_pl", cAgtNoPl);

		String lclFil = "gzdl_" + clrDat;

		Map<String, Object> para = new HashMap<String, Object>();
		para.put("cAgtNoDl", cAgtNoDl);
		para.put("cAgtNoYh", cAgtNoYh);
		para.put("cAgtNoPl", cAgtNoPl);
		para.put("acDte", clrDat);
		log.info("输入参数为:" + para);
		// 产生上送供电方的文件
		List<Map<String, Object>> reustlList = get(GdEupsTransJournalRepository.class).findGzEleHstSucJnl(para);
		log.info("返回的list=" + reustlList);
	}

}

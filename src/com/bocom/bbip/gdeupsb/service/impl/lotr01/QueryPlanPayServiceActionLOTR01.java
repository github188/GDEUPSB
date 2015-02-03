package com.bocom.bbip.gdeupsb.service.impl.lotr01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdLotAutPln;
import com.bocom.bbip.gdeupsb.repository.GdLotAutPlnRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 定投计划明细查询
 * @author hefengwen
 *
 */
public class QueryPlanPayServiceActionLOTR01 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(QueryPlanPayServiceActionLOTR01.class);

	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("QueryPlanPayServiceActionLOTR01 start ... ...");
		String crdNo = context.getData("crdNo");
		logger.info("crdNo:["+crdNo+"]");
		GdLotAutPln gdLotAutPln = new GdLotAutPln();
		gdLotAutPln.setCrdNo(crdNo);
		gdLotAutPln.setStatus("0");
		List<GdLotAutPln> gdLotAutPlns = get(GdLotAutPlnRepository.class).find(gdLotAutPln);
		if(gdLotAutPlns==null||gdLotAutPlns.size()==0){
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_03_ERROR);//定投信息不存在
		}
		List<Map<String,Object>> detailList = new ArrayList<Map<String,Object>>();
		for(GdLotAutPln e:gdLotAutPlns){
			Map<String,Object> m = new HashMap<String,Object>();
			m.put("planNo", e.getPlanNo());
			m.put("planNm", e.getPlanNm());
			m.put("mobTel", e.getMobTel());
			m.put("betPer", e.getBetPer());
			m.put("betLin", e.getBetLin());
			m.put("betAmt", e.getBetAmt());
			m.put("doPer", e.getDoPer());
			m.put("levPer", Integer.parseInt(e.getBetPer())-Integer.parseInt(e.getDoPer()));
			m.put("status", e.getStatus());
			detailList.add(m);
		}
		context.setData("retNum", gdLotAutPlns.size());
		context.setData("detailList", detailList);
		logger.info("QueryPlanPayServiceActionLOTR01 end ... ...");
	}

}

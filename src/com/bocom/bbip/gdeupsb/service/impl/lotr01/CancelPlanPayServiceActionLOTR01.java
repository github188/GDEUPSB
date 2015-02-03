package com.bocom.bbip.gdeupsb.service.impl.lotr01;

import java.util.List;

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
 * 定投计划取消
 * @author hefengwen
 *
 */
public class CancelPlanPayServiceActionLOTR01 extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(CancelPlanPayServiceActionLOTR01.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("CancelPlanPayServiceActionLOTR01 start ... ...");
		
		String planNo = context.getData("planNo");
		String crdNo = context.getData("crdNo");
		logger.info("planNo:["+planNo+"]-crdNo:["+crdNo+"]");
		
		//查询定投信息是否存在
		GdLotAutPln gdLotAutPln = new GdLotAutPln();
		gdLotAutPln.setPlanNo(planNo);
		gdLotAutPln.setCrdNo(crdNo);
		gdLotAutPln.setStatus("0");
		List<GdLotAutPln> eupsLotAutPlns = get(GdLotAutPlnRepository.class).find(gdLotAutPln);
		if(eupsLotAutPlns==null||eupsLotAutPlns.size()==0){
			throw new CoreException(GDErrorCodes.EUPS_LOTR01_03_ERROR);//定投信息不存在
		}
		//取消定投计划
		gdLotAutPln.setStatus("1");
		get(GdLotAutPlnRepository.class).updateStatus(gdLotAutPln);
		logger.info("CancelPlanPayServiceActionLOTR01 end ... ...");
	}

}

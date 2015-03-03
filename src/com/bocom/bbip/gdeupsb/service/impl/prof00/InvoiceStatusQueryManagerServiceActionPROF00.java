package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvDtlBok;
import com.bocom.bbip.gdeupsb.repository.GdeupsInvDtlBokRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 凭证状态查询
 * @author hefengwen
 *
 */
public class InvoiceStatusQueryManagerServiceActionPROF00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(InvoiceStatusQueryManagerServiceActionPROF00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("InvoiceStatusQueryManagerServiceActionPROF00 start ... ...");
		String tckNo = context.getData("tckNo").toString();
		logger.info("tckNo["+tckNo+"]");
		GdeupsInvDtlBok gdeupsInvDtlBok = new GdeupsInvDtlBok();
		gdeupsInvDtlBok.setTckNo(tckNo);
		List<GdeupsInvDtlBok> gdeupsInvDtlBoks = get(GdeupsInvDtlBokRepository.class).find(gdeupsInvDtlBok);
		if(gdeupsInvDtlBoks==null||gdeupsInvDtlBoks.size()==0){
			//TODO：凭证信息不存在
			throw new CoreException(GDErrorCodes.EUPS_PROF00_04_ERROR);
		}
		gdeupsInvDtlBok = gdeupsInvDtlBoks.get(0);
		context.setData("invTyp",gdeupsInvDtlBok.getInvTyp());
		context.setData("oprTlr",gdeupsInvDtlBok.getOprTlr());
		context.setData("ivBegNo",gdeupsInvDtlBok.getIvBegNo());
		context.setData("ivEndNo",gdeupsInvDtlBok.getIvEndNo());
		context.setData("tolNum",gdeupsInvDtlBok.getTolNum());
		context.setData("status",gdeupsInvDtlBok.getStatus());
		logger.info("InvoiceStatusQueryManagerServiceActionPROF00 end ... ...");
	}

}

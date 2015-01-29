package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.EupsInvDtlBok;
import com.bocom.bbip.gdeupsb.repository.EupsInvDtlBokRepository;
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
		EupsInvDtlBok eupsInvDtlBok = new EupsInvDtlBok();
		eupsInvDtlBok.setTckno(tckNo);
		List<EupsInvDtlBok> eupsInvDtlBoks = get(EupsInvDtlBokRepository.class).find(eupsInvDtlBok);
		if(eupsInvDtlBoks==null||eupsInvDtlBoks.size()==0){
			//TODO：凭证信息不存在
			throw new CoreException(GDErrorCodes.EUPS_PROF00_04_ERROR);
		}
		eupsInvDtlBok = eupsInvDtlBoks.get(0);
		context.setData("invTyp",eupsInvDtlBok.getInvtyp());
		context.setData("oprTlr",eupsInvDtlBok.getOprtlr());
		context.setData("ivBegNo",eupsInvDtlBok.getIvbegno());
		context.setData("ivEndNo",eupsInvDtlBok.getIvendno());
		context.setData("tolNum",eupsInvDtlBok.getTolnum());
		context.setData("status",eupsInvDtlBok.getStatus());
		logger.info("InvoiceStatusQueryManagerServiceActionPROF00 end ... ...");
	}

}

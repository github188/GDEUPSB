package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.EupsInvDtlBok;
import com.bocom.bbip.gdeupsb.entity.EupsInvTermInf;
import com.bocom.bbip.gdeupsb.repository.EupsInvDtlBokRepository;
import com.bocom.bbip.gdeupsb.repository.EupsInvTermInfRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 珠海自助通发票管理系统凭证结账
 * @author hefengwen
 *
 */
public class InvoiceCheckManagerServiceActionPROF00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(InvoiceCheckManagerServiceActionPROF00.class);

	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("InvoiceCheckManagerServiceActionPROF00 start ... ...");
		String invTyp = context.getData("invTyp");
		String tlrId = context.getData("tlr");
		String nodno = context.getData("br");
		logger.info("invType["+invTyp+"]tlrId["+tlrId+"]nodno["+nodno+"]");
		EupsInvTermInf eupsInvTermInf = new EupsInvTermInf();
		eupsInvTermInf.setInvtyp(invTyp);
		eupsInvTermInf.setTlrid(tlrId);
		eupsInvTermInf.setNodno(nodno);
		List<EupsInvTermInf> eupsInvTermInfs = get(EupsInvTermInfRepository.class).find(eupsInvTermInf);
		if(eupsInvTermInfs==null||eupsInvTermInfs.size()==0){
			//TODO:当前终端无凭证
			throw new CoreException(GDErrorCodes.EUPS_PROF00_04_ERROR);
		}
		eupsInvTermInf = eupsInvTermInfs.get(0);
		if(Integer.parseInt(eupsInvTermInf.getInvnum())!=0){
			//TODO:剩余发票，不能结算
			throw new CoreException(GDErrorCodes.EUPS_PROF00_10_ERROR);
		}
		EupsInvDtlBok eupsInvDtlBok = new EupsInvDtlBok();
		eupsInvDtlBok.setInvtyp(invTyp);
		eupsInvDtlBok.setIvbegno(eupsInvTermInf.getIvbegno());
		eupsInvDtlBok.setIvendno(eupsInvTermInf.getIvendno());
		eupsInvDtlBok.setStatus("2");
		eupsInvDtlBok.setUsenum(eupsInvTermInf.getUsenum());
		eupsInvDtlBok.setClrnum(eupsInvTermInf.getClrnum());
		eupsInvDtlBok.setChkdat(DateUtils.formatAsSimpleDate(new Date()));
		get(EupsInvDtlBokRepository.class).updateCheck(eupsInvDtlBok);
		
		get(EupsInvTermInfRepository.class).deleteInvTermInf(eupsInvTermInf);
		
		context.setData("useDat", eupsInvTermInf.getUsedat());
		context.setData("useNum", eupsInvTermInf.getUsenum());
		context.setData("clrNum", eupsInvTermInf.getClrnum());
		context.setData("actDat", DateUtils.formatAsSimpleDate(new Date()));
		context.setData("ivBegNo", eupsInvTermInf.getIvbegno());
		logger.info("InvoiceCheckManagerServiceActionPROF00 end ... ...");
	}

}

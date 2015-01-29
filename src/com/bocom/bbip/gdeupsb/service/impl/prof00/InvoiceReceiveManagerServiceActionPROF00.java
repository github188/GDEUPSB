package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.EupsInvDtlBok;
import com.bocom.bbip.gdeupsb.entity.EupsInvTermInf;
import com.bocom.bbip.gdeupsb.entity.EupsInvTxnInf;
import com.bocom.bbip.gdeupsb.repository.EupsInvDtlBokRepository;
import com.bocom.bbip.gdeupsb.repository.EupsInvTermInfRepository;
import com.bocom.bbip.gdeupsb.repository.EupsInvTxnInfRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 凭证领用
 * @author hefengwen
 *
 */
public class InvoiceReceiveManagerServiceActionPROF00 extends BaseAction  {
	
	private static Logger logger = LoggerFactory.getLogger(InvoiceReceiveManagerServiceActionPROF00.class);

	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("InvoiceReceiveManagerServiceActionPROF00 start ... ...");
		String invTyp = context.getData("invTyp");
		String ivBegNo = context.getData("ivBegNo");
		logger.info("invTyp["+invTyp+"]ivBegNo["+ivBegNo+"]");
		
		EupsInvDtlBok eupsInvDtlBok = new EupsInvDtlBok();
		eupsInvDtlBok.setInvtyp(invTyp);
		eupsInvDtlBok.setIvbegno(ivBegNo);
		eupsInvDtlBok.setOprtlr(context.getData("tlr").toString());
		eupsInvDtlBok.setNodno(context.getData("br").toString());
		eupsInvDtlBok.setStatus("0");
		List<EupsInvDtlBok> eupsInvDtlBoks = get(EupsInvDtlBokRepository.class).find(eupsInvDtlBok);
		if(eupsInvDtlBoks==null||eupsInvDtlBoks.size()==0){
			//TODO:无对应的下发发票记录
			throw new CoreException(GDErrorCodes.EUPS_PROF00_04_ERROR);
		}
		eupsInvDtlBok = eupsInvDtlBoks.get(0);
		EupsInvTermInf eupsInvTermInf = new EupsInvTermInf();
		eupsInvTermInf.setTlrid(context.getData("tlr").toString());
		eupsInvTermInf.setNodno(context.getData("br").toString());
		List<EupsInvTermInf> eupsInvTermInfs = get(EupsInvTermInfRepository.class).find(eupsInvTermInf);
		if(eupsInvTermInfs!=null&&eupsInvTermInfs.size()!=0){
			//TODO:终端未结账
			throw new CoreException(GDErrorCodes.EUPS_PROF00_06_ERROR);
		}
		
		//TODO:取流水号
		String seqNo = "";
		
		eupsInvTermInf.setInvtyp(invTyp);
		eupsInvTermInf.setIvbegno(ivBegNo);
		eupsInvTermInf.setIvendno(eupsInvDtlBok.getIvendno());
		eupsInvTermInf.setInvnum(eupsInvDtlBok.getTolnum());
		eupsInvTermInf.setClrnum("0");
		eupsInvTermInf.setUsenum("0");
		eupsInvTermInf.setSeqno(seqNo);
		eupsInvTermInf.setUsedat(DateUtils.formatAsSimpleDate(new Date()));
		get(EupsInvTermInfRepository.class).insert(eupsInvTermInf);
		
		EupsInvDtlBok eupsInvDtlBoktmp = new EupsInvDtlBok();
		eupsInvDtlBoktmp.setStatus("1");
		eupsInvDtlBoktmp.setUsedat(DateUtils.formatAsSimpleDate(new Date()));
		eupsInvDtlBoktmp.setSeqno(seqNo);
		eupsInvDtlBoktmp.setInvtyp(invTyp);
		eupsInvDtlBoktmp.setIvbegno(ivBegNo);
		eupsInvDtlBoktmp.setOprtlr(context.getData("tlr").toString());
		eupsInvDtlBoktmp.setNodno(context.getData("br").toString());
		get(EupsInvDtlBokRepository.class).updateStatus(eupsInvDtlBoktmp);
		
		EupsInvTxnInf eupsInvTxnInf = new EupsInvTxnInf();
		eupsInvTxnInf.setInvtyp(invTyp);
		eupsInvTxnInf.setIvbegno(ivBegNo);
		eupsInvTxnInf.setIvendno(eupsInvTermInf.getIvendno());
		eupsInvTxnInf.setUseseq("0");
		eupsInvTxnInf.setStlnum(eupsInvTermInf.getInvnum());
		eupsInvTxnInf.setStlflg("U");
		eupsInvTxnInf.setActdat(DateUtils.formatAsSimpleDate(new Date()));
		eupsInvTxnInf.setTlrid(context.getData("tlr").toString());
		eupsInvTxnInf.setNodno(context.getData("br").toString());
		get(EupsInvTxnInfRepository.class).insert(eupsInvTxnInf);
		logger.info("InvoiceReceiveManagerServiceActionPROF00 end ... ...");
	}

}

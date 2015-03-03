package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvDtlBok;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvTermInf;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvTxnInf;
import com.bocom.bbip.gdeupsb.repository.GdeupsInvDtlBokRepository;
import com.bocom.bbip.gdeupsb.repository.GdeupsInvTermInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdeupsInvTxnInfRepository;
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
		
		GdeupsInvDtlBok gdeupsInvDtlBok = new GdeupsInvDtlBok();
		gdeupsInvDtlBok.setInvTyp(invTyp);
		gdeupsInvDtlBok.setIvBegNo(ivBegNo);
		gdeupsInvDtlBok.setOprTlr(context.getData("tlr").toString());
		gdeupsInvDtlBok.setNodno(context.getData("br").toString());
		gdeupsInvDtlBok.setStatus("0");
		List<GdeupsInvDtlBok> eupsInvDtlBoks = get(GdeupsInvDtlBokRepository.class).find(gdeupsInvDtlBok);
		if(eupsInvDtlBoks==null||eupsInvDtlBoks.size()==0){
			//TODO:无对应的下发发票记录
			throw new CoreException(GDErrorCodes.EUPS_PROF00_04_ERROR);
		}
		gdeupsInvDtlBok = eupsInvDtlBoks.get(0);
		GdeupsInvTermInf gdeupsInvTermInf = new GdeupsInvTermInf();
		gdeupsInvTermInf.setTlrId(context.getData("tlr").toString());
		gdeupsInvTermInf.setNodno(context.getData("br").toString());
		List<GdeupsInvTermInf> eupsInvTermInfs = get(GdeupsInvTermInfRepository.class).find(gdeupsInvTermInf);
		if(eupsInvTermInfs!=null&&eupsInvTermInfs.size()!=0){
			//TODO:终端未结账
			throw new CoreException(GDErrorCodes.EUPS_PROF00_06_ERROR);
		}
		
		//TODO:取流水号
		String seqNo = "";
		
		gdeupsInvTermInf.setInvTyp(invTyp);
		gdeupsInvTermInf.setIvBegNo(ivBegNo);
		gdeupsInvTermInf.setIvEndNo(gdeupsInvDtlBok.getIvEndNo());
		gdeupsInvTermInf.setInvNum(gdeupsInvDtlBok.getTolNum());
		gdeupsInvTermInf.setClrNum("0");
		gdeupsInvTermInf.setUseNum("0");
		gdeupsInvTermInf.setSeqNo(seqNo);
		gdeupsInvTermInf.setUseDat(DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
		get(GdeupsInvTermInfRepository.class).insert(gdeupsInvTermInf);
		
		GdeupsInvDtlBok gdeupsInvDtlBoktmp = new GdeupsInvDtlBok();
		gdeupsInvDtlBoktmp.setStatus("1");
		gdeupsInvDtlBoktmp.setUseDat(DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
		gdeupsInvDtlBoktmp.setSeqNo(seqNo);
		gdeupsInvDtlBoktmp.setInvTyp(invTyp);
		gdeupsInvDtlBoktmp.setIvBegNo(ivBegNo);
		gdeupsInvDtlBoktmp.setOprTlr(context.getData("tlr").toString());
		gdeupsInvDtlBoktmp.setNodno(context.getData("br").toString());
		get(GdeupsInvDtlBokRepository.class).updateStatus(gdeupsInvDtlBoktmp);
		
		GdeupsInvTxnInf eupsInvTxnInf = new GdeupsInvTxnInf();
		eupsInvTxnInf.setInvTyp(invTyp);
		eupsInvTxnInf.setIvBegNo(ivBegNo);
		eupsInvTxnInf.setIvEndNo(gdeupsInvTermInf.getIvEndNo());
		eupsInvTxnInf.setUseSeq("0");
		eupsInvTxnInf.setStlNum(gdeupsInvTermInf.getInvNum());
		eupsInvTxnInf.setStlFlg("U");
		eupsInvTxnInf.setActDat(DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
		eupsInvTxnInf.setTlrId(context.getData("tlr").toString());
		eupsInvTxnInf.setNodno(context.getData("br").toString());
		get(GdeupsInvTxnInfRepository.class).insert(eupsInvTxnInf);
		logger.info("InvoiceReceiveManagerServiceActionPROF00 end ... ...");
	}

}

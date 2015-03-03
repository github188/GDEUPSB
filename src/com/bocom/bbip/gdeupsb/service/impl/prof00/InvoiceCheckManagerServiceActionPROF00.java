package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvDtlBok;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvTermInf;
import com.bocom.bbip.gdeupsb.repository.GdeupsInvDtlBokRepository;
import com.bocom.bbip.gdeupsb.repository.GdeupsInvTermInfRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
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
		GdeupsInvTermInf gdeupsInvTermInf = new GdeupsInvTermInf();
		gdeupsInvTermInf.setInvTyp(invTyp);
		gdeupsInvTermInf.setTlrId(tlrId);
		gdeupsInvTermInf.setNodno(nodno);
		List<GdeupsInvTermInf> gdeupsInvTermInfs = get(GdeupsInvTermInfRepository.class).find(gdeupsInvTermInf);
		if(CollectionUtils.isEmpty(gdeupsInvTermInfs)){
			//TODO:当前终端无凭证
			throw new CoreException(GDErrorCodes.EUPS_PROF00_04_ERROR);
		}
		gdeupsInvTermInf = gdeupsInvTermInfs.get(0);
		if(Integer.parseInt(gdeupsInvTermInf.getInvNum())!=0){
			//TODO:剩余发票，不能结算
			throw new CoreException(GDErrorCodes.EUPS_PROF00_10_ERROR);
		}
		GdeupsInvDtlBok gdeupsInvDtlBok = new GdeupsInvDtlBok();
		gdeupsInvDtlBok.setInvTyp(invTyp);
		gdeupsInvDtlBok.setIvBegNo(gdeupsInvTermInf.getIvBegNo());
		gdeupsInvDtlBok.setIvEndNo(gdeupsInvTermInf.getIvEndNo());
		gdeupsInvDtlBok.setStatus("2");
		gdeupsInvDtlBok.setUseNum(gdeupsInvTermInf.getUseNum());
		gdeupsInvDtlBok.setClrNum(gdeupsInvTermInf.getClrNum());
		gdeupsInvDtlBok.setChkDat(DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
		get(GdeupsInvDtlBokRepository.class).updateCheck(gdeupsInvDtlBok);
		
		get(GdeupsInvTermInfRepository.class).deleteInvTermInf(gdeupsInvTermInf);
		
		context.setData("useDat", gdeupsInvTermInf.getUseDat());
		context.setData("useNum", gdeupsInvTermInf.getUseNum());
		context.setData("clrNum", gdeupsInvTermInf.getClrNum());
		context.setData("actDat", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
		context.setData("ivBegNo", gdeupsInvTermInf.getIvBegNo());
		logger.info("InvoiceCheckManagerServiceActionPROF00 end ... ...");
	}

}

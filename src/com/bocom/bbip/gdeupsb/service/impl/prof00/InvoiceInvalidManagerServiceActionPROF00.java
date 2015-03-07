package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvTermInf;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvTxnInf;
import com.bocom.bbip.gdeupsb.repository.GdeupsInvTermInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdeupsInvTxnInfRepository;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 发票作废、丢失处理
 * @author hefengwen
 *
 */
public class InvoiceInvalidManagerServiceActionPROF00 extends BaseAction  {

	private static Logger logger = LoggerFactory.getLogger(InvoiceInvalidManagerServiceActionPROF00.class);
	
	@Override
	public void execute(Context context) throws CoreException,CoreRuntimeException {
		logger.info("InvoiceInvalidManagerServiceActionPROF00 start ... ...");
		String invTyp = context.getData("invTyp").toString().trim();
		String doType = context.getData("doType").toString().trim();
		String stlNum = context.getData("stlNum").toString().trim();
		logger.info("invTyp["+invTyp+"]doType["+doType+"]stlNum["+stlNum+"]");
		if(Integer.parseInt(stlNum)<1){
			//TODO:凭证数量为0
			throw new CoreException(GDErrorCodes.EUPS_PROF00_07_ERROR);
		}
		GdeupsInvTermInf gdeupsInvTermInf = new GdeupsInvTermInf();
		gdeupsInvTermInf.setTlrId(context.getData("tlr").toString());
		List<GdeupsInvTermInf> eupsInvTermInfs = get(GdeupsInvTermInfRepository.class).find(gdeupsInvTermInf);
		if(eupsInvTermInfs==null||eupsInvTermInfs.size()==0){
			//TODO:当前终端没有凭证
			throw new CoreException(GDErrorCodes.EUPS_PROF00_04_ERROR);
		}
		gdeupsInvTermInf = eupsInvTermInfs.get(0);
		if(Integer.parseInt(stlNum)>Integer.parseInt(gdeupsInvTermInf.getInvNum())){
			//TODO:作废发票数大于剩余发票数
			throw new CoreException(GDErrorCodes.EUPS_PROF00_08_ERROR);
		}
		
		context.setData("actDat", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		context.setData("ivBegNo", gdeupsInvTermInf.getIvBegNo());
		
		String stlFlg = "1";
		String useSeq = Integer.parseInt(gdeupsInvTermInf.getUseNum())+
				Integer.parseInt(gdeupsInvTermInf.getClrNum())+1+"";
		String invNum = Integer.parseInt(gdeupsInvTermInf.getInvNum())-Integer.parseInt(stlNum)+"";
		String clrNum = Integer.parseInt(gdeupsInvTermInf.getClrNum())+Integer.parseInt(stlNum)+"";
		GdeupsInvTermInf gdeupsInvTermInftmp = new GdeupsInvTermInf();
		gdeupsInvTermInftmp.setInvNum(invNum);
		gdeupsInvTermInftmp.setClrNum(clrNum);
		gdeupsInvTermInftmp.setTlrId(gdeupsInvTermInf.getTlrId());
		gdeupsInvTermInftmp.setNodno(gdeupsInvTermInf.getNodno());
		get(GdeupsInvTermInfRepository.class).updateInvalidNum(gdeupsInvTermInftmp);
		
		GdeupsInvTxnInf gdeupsInvTxnInf = new GdeupsInvTxnInf();
		gdeupsInvTxnInf.setInvTyp(invTyp);
		gdeupsInvTxnInf.setIvBegNo(gdeupsInvTermInf.getIvBegNo());
		gdeupsInvTxnInf.setIvEndNo(gdeupsInvTermInf.getIvEndNo());
		gdeupsInvTxnInf.setUseSeq(useSeq);
		gdeupsInvTxnInf.setStlNum(stlNum);
		gdeupsInvTxnInf.setStlFlg(stlFlg);
		gdeupsInvTxnInf.setActDat(DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
		gdeupsInvTxnInf.setTlrId(gdeupsInvTermInf.getTlrId());
		gdeupsInvTxnInf.setNodno(gdeupsInvTermInf.getNodno());
		get(GdeupsInvTxnInfRepository.class).insert(gdeupsInvTxnInf);
		logger.info("InvoiceInvalidManagerServiceActionPROF00 end ... ...");
	}

}

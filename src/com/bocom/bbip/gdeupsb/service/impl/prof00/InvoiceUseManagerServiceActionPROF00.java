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
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 珠海自助通发票管理系统凭证付出
 * @author hefengwen
 *
 */
public class InvoiceUseManagerServiceActionPROF00 extends BaseAction  {
	
	private static Logger logger = LoggerFactory.getLogger(InvoiceUseManagerServiceActionPROF00.class);
	

	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("InvoiceUseManagerServiceActionPROF00 start ... ...");
		
		String invTyp = context.getData("invTyp").toString();
		String qyNo = context.getData("qyNo").toString();
		String oldSeq = context.getData("oldSeq").toString();
		String oldTrdate = context.getData("oldTrdate").toString();
		logger.info("invTyp:["+context.getData("invTyp")+"]qyNo:["+
				context.getData("qyNo")+"]oldSeq:["+
				context.getData("oldSeq")+"]oldTrdate:["+
				context.getData("oldTrdate")+"]");
		
		GdeupsInvTermInf  gdeupsInvTermInf = new GdeupsInvTermInf();
		gdeupsInvTermInf.setTlrId(context.getData("tlr").toString());
		List<GdeupsInvTermInf> eupsInvTermInfs = get(GdeupsInvTermInfRepository.class).find(gdeupsInvTermInf);
		if(CollectionUtils.isEmpty(eupsInvTermInfs)){
			//TODO:当前终端无凭证信息
			throw new CoreException(GDErrorCodes.EUPS_PROF00_04_ERROR);
		}
		gdeupsInvTermInf = eupsInvTermInfs.get(0);
		String invNum = gdeupsInvTermInf.getInvNum();//可用发票数
		String useNum = gdeupsInvTermInf.getUseNum();//已用发票数
		String clrNum = gdeupsInvTermInf.getClrNum();//作废发票数
		
		if(Integer.parseInt(invNum)<1){
			//TODO:凭证不足
			throw new CoreException(GDErrorCodes.EUPS_PROF00_09_ERROR);
		}
		String stlNum = "1";//使用张数
		String stlFlg = "0";//使用方式
		invNum = (Integer.parseInt(invNum)-1)+"";
		useNum = (Integer.parseInt(useNum)+1)+"";
		String useSeq = (Integer.parseInt(useNum)+Integer.parseInt(clrNum))+"";
		GdeupsInvTermInf gdeupsInvTermInftmp = new GdeupsInvTermInf();
		gdeupsInvTermInftmp.setTlrId(context.getData("tlr").toString());
		gdeupsInvTermInftmp.setInvNum(invNum);
		gdeupsInvTermInftmp.setUseNum(useNum);
		get(GdeupsInvTermInfRepository.class).updateNum(gdeupsInvTermInftmp);
		//TODO:登记发票交易信息表
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
		gdeupsInvTxnInf.setQyNo(qyNo);
		gdeupsInvTxnInf.setOldSeq(oldSeq);
		gdeupsInvTxnInf.setOldTrDate(oldTrdate);
		get(GdeupsInvTxnInfRepository.class).insert(gdeupsInvTxnInf);
		context.setData("seqNo", useSeq);
		context.setData("actDat", DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
		logger.info("InvoiceUseManagerServiceActionPROF00 end ... ...");
	}

}

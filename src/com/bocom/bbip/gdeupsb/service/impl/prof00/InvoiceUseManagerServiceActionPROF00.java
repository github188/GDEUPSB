package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.EupsInvTermInf;
import com.bocom.bbip.gdeupsb.entity.EupsInvTxnInf;
import com.bocom.bbip.gdeupsb.repository.EupsInvTermInfRepository;
import com.bocom.bbip.gdeupsb.repository.EupsInvTxnInfRepository;
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
		
		EupsInvTermInf  eupsInvTermInf = new EupsInvTermInf();
		eupsInvTermInf.setTlrid(context.getData("tlr").toString());
		List<EupsInvTermInf> eupsInvTermInfs = get(EupsInvTermInfRepository.class).find(eupsInvTermInf);
		if(eupsInvTermInfs==null||eupsInvTermInfs.size()==0){
			//TODO:当前终端无凭证信息
			throw new CoreException(GDErrorCodes.EUPS_PROF00_04_ERROR);
		}
		eupsInvTermInf = eupsInvTermInfs.get(0);
		String invNum = eupsInvTermInf.getInvnum();//可用发票数
		String useNum = eupsInvTermInf.getUsenum();//已用发票数
		String clrNum = eupsInvTermInf.getClrnum();//作废发票数
		
		if(Integer.parseInt(invNum)<1){
			//TODO:凭证不足
			throw new CoreException(GDErrorCodes.EUPS_PROF00_09_ERROR);
		}
		String stlNum = "1";//使用张数
		String stlFlg = "1";//使用方式
		invNum = (Integer.parseInt(invNum)-1)+"";
		useNum = (Integer.parseInt(useNum)+1)+"";
		String useSeq = (Integer.parseInt(useNum)+Integer.parseInt(clrNum))+"";
		EupsInvTermInf eupsInvTermInftmp = new EupsInvTermInf();
		eupsInvTermInftmp.setTlrid(context.getData("tlr").toString());
		eupsInvTermInftmp.setInvnum(invNum);
		eupsInvTermInftmp.setUsenum(useNum);
		get(EupsInvTermInfRepository.class).updateNum(eupsInvTermInftmp);
		//TODO:登记发票交易信息表
		EupsInvTxnInf eupsInvTxnInf = new EupsInvTxnInf();
		eupsInvTxnInf.setInvtyp(invTyp);
		eupsInvTxnInf.setIvbegno(eupsInvTermInf.getIvbegno());
		eupsInvTxnInf.setIvendno(eupsInvTermInf.getIvendno());
		eupsInvTxnInf.setUseseq(useSeq);
		eupsInvTxnInf.setStlnum(stlNum);
		eupsInvTxnInf.setStlflg(stlFlg);
		eupsInvTxnInf.setActdat(DateUtils.formatAsSimpleDate(new Date()));
		eupsInvTxnInf.setTlrid(eupsInvTermInf.getTlrid());
		eupsInvTxnInf.setNodno(eupsInvTermInf.getNodno());
		eupsInvTxnInf.setQyno(qyNo);
		eupsInvTxnInf.setOldseq(oldSeq);
		eupsInvTxnInf.setOldtrdate(oldTrdate);
		get(EupsInvTxnInfRepository.class).insert(eupsInvTxnInf);
		context.setData("seqNo", useSeq);
		context.setData("actDat", DateUtils.formatAsSimpleDate(new Date()));
		logger.info("InvoiceUseManagerServiceActionPROF00 end ... ...");
	}

}

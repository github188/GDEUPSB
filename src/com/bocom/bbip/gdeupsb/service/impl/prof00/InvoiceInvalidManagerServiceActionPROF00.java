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
		EupsInvTermInf eupsInvTermInf = new EupsInvTermInf();
		eupsInvTermInf.setTlrid(context.getData("tlr").toString());
		List<EupsInvTermInf> eupsInvTermInfs = get(EupsInvTermInfRepository.class).find(eupsInvTermInf);
		if(eupsInvTermInfs==null||eupsInvTermInfs.size()==0){
			//TODO:当前终端没有凭证
			throw new CoreException(GDErrorCodes.EUPS_PROF00_04_ERROR);
		}
		eupsInvTermInf = eupsInvTermInfs.get(0);
		if(Integer.parseInt(stlNum)>Integer.parseInt(eupsInvTermInf.getInvnum())){
			//TODO:作废发票数大于剩余发票数
			throw new CoreException(GDErrorCodes.EUPS_PROF00_08_ERROR);
		}
		String stlFlg = "1";
		String useSeq = Integer.parseInt(eupsInvTermInf.getUsenum())+
				Integer.parseInt(eupsInvTermInf.getClrnum())+1+"";
		String invNum = Integer.parseInt(eupsInvTermInf.getInvnum())-Integer.parseInt(stlNum)+"";
		String clrNum = Integer.parseInt(eupsInvTermInf.getClrnum())+Integer.parseInt(stlNum)+"";
		EupsInvTermInf eupsInvTermInftmp = new EupsInvTermInf();
		eupsInvTermInftmp.setInvnum(invNum);
		eupsInvTermInftmp.setClrnum(clrNum);
		eupsInvTermInftmp.setTlrid(eupsInvTermInf.getTlrid());
		eupsInvTermInftmp.setNodno(eupsInvTermInf.getNodno());
		get(EupsInvTermInfRepository.class).updateInvalidNum(eupsInvTermInftmp);
		
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
		get(EupsInvTxnInfRepository.class).insert(eupsInvTxnInf);
		logger.info("InvoiceInvalidManagerServiceActionPROF00 end ... ...");
	}

}

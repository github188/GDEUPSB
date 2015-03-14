package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvDtlBok;
import com.bocom.bbip.gdeupsb.repository.GdeupsInvDtlBokRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.thd.org.apache.commons.lang.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * 凭证发放撤销
 * @author hefengwen
 *
 */
public class InvoiceCancelGrantManagerServiceActionPROF00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(InvoiceCancelGrantManagerServiceActionPROF00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("InvoiceCancelGrantManagerServiceActionPROF00 execute start ... ...");
		String func = context.getData("func");
		logger.info("func["+func+"]");
		if("1".equals(func)){//根据功能码判断处理方法
			cancel(context);
		}else{
			query(context);
		}
		logger.info("InvoiceCancelGrantManagerServiceActionPROF00 execute end ... ...");
	}
	/**
	 * 凭证发放撤销
	 * @param context
	 * @throws CoreException
	 */
	private void cancel(Context context) throws CoreException{
		logger.info("InvoiceCancelGrantManagerServiceActionPROF00 cancel start ... ...");
		String tckNo = context.getData("tckNo");
		logger.info("tckNo["+tckNo+"]");
		GdeupsInvDtlBok gdeupsInvDtlBok = new GdeupsInvDtlBok();
		gdeupsInvDtlBok.setTckNo(tckNo);
		List<GdeupsInvDtlBok> eupsInvDtlBoks = get(GdeupsInvDtlBokRepository.class).find(gdeupsInvDtlBok);
		if(CollectionUtils.isEmpty(eupsInvDtlBoks)){
			//TODO：凭证信息不存在
			throw new CoreException(GDErrorCodes.EUPS_PROF00_04_ERROR);
		}
		gdeupsInvDtlBok = eupsInvDtlBoks.get(0);
		if(!"0".equals(gdeupsInvDtlBok.getStatus())){
			//TODO:已领用，不能撤销
			throw new CoreException(GDErrorCodes.EUPS_PROF00_05_ERROR);
		}
//		if(StringUtils.isEmpty((String)context.getData("sup1Id"))){
//			logger.error("无授权信息!");
//			logger.error("sup1Id["+context.getData("sup1Id")+"]");
//			throw new CoreException(GDErrorCodes.EUPS_PROF00_00_ERROR);
//		}
//		if(!"40".equals(context.getData("authLvl"))){
//			logger.error("授权级别不够!");
//			logger.error("authLvl["+context.getData("authLvl")+"]");
//			throw new CoreException(GDErrorCodes.EUPS_PROF00_01_ERROR);
//		}
		get(GdeupsInvDtlBokRepository.class).deleteInvDtlBok(gdeupsInvDtlBok);
//		context.setData("invTyp",eupsInvDtlBok.getInvtyp());
//		context.setData("oprTlr",eupsInvDtlBok.getOprtlr());
//		context.setData("ivBegNo",eupsInvDtlBok.getIvbegno());
//		context.setData("ivEndNo",eupsInvDtlBok.getIvendno());
//		context.setData("tolNum",eupsInvDtlBok.getTolnum());
//		context.setData("status",eupsInvDtlBok.getStatus());
		logger.info("InvoiceCancelGrantManagerServiceActionPROF00 cancel end ... ...");
	}
	/**
	 * 凭证状态查询
	 * @param context
	 * @throws CoreException
	 */
	private void query(Context context) throws CoreException{
		logger.info("InvoiceCancelGrantManagerServiceActionPROF00 query start ... ...");
		String tckNo = context.getData("tckNo");
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
		logger.info("InvoiceCancelGrantManagerServiceActionPROF00 query end ... ...");
	}

}

package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdeupsInvDtlBok;
import com.bocom.bbip.gdeupsb.repository.GdeupsInvDtlBokRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.thd.org.apache.commons.lang.StringUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 凭证发放
 * @author hefengwen
 *
 */
public class InvoiceGrantManagerServiceActionPROF00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(InvoiceGrantManagerServiceActionPROF00.class);
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("InvoiceGrantManagerServiceActionPROF00 start ... ...");
		if(StringUtils.isEmpty((String)context.getData("sup1Id"))){
			logger.error("无授权信息!");
			logger.error("sup1Id["+context.getData("sup1Id")+"]");
			throw new CoreException(GDErrorCodes.EUPS_PROF00_00_ERROR);
		}
		if(!"40".equals(context.getData("authLvl"))){
			logger.error("授权级别不够!");
			logger.error("authLvl["+context.getData("authLvl")+"]");
			throw new CoreException(GDErrorCodes.EUPS_PROF00_01_ERROR);
		}
		
		//TODO:检查自助柜员是否已经存在下发的凭证
		String invTyp = context.getData("invTyp");//凭证类型
		String oprTlr = context.getData("oprTlr");//领用柜员
		
		logger.info("invTyp["+invTyp+"]oprTlr["+oprTlr+"]");
		GdeupsInvDtlBok gdeupsInvDtlBok = new GdeupsInvDtlBok();
		gdeupsInvDtlBok.setInvTyp(invTyp);
		gdeupsInvDtlBok.setOprTlr(oprTlr);
		gdeupsInvDtlBok.setStatus("0");
		List<GdeupsInvDtlBok> gdeupsInvDtlBoks = get(GdeupsInvDtlBokRepository.class).find(gdeupsInvDtlBok);
		if(!CollectionUtils.isEmpty(gdeupsInvDtlBoks)){//存在已下发凭证
			logger.error("该柜员"+oprTlr+"已存在已发放的凭证，不允许多次发放!");
			throw new CoreException(GDErrorCodes.EUPS_PROF00_02_ERROR);
		}
		
		//TODO:检查凭证是否已经使用过
		String ivBegNo = context.getData("ivBegNo");//凭证开始号码
		String ivEndNo = context.getData("ivEndNo");//凭证结束号码
		
		gdeupsInvDtlBok.setIvBegNo(ivBegNo);
		gdeupsInvDtlBok.setIvEndNo(ivEndNo);
		gdeupsInvDtlBoks = get(GdeupsInvDtlBokRepository.class).findIsExist(gdeupsInvDtlBok);
		if(!CollectionUtils.isEmpty(gdeupsInvDtlBoks)){//凭证已使用
			logger.error("["+ivBegNo+"-"+"ivEndNo"+"]存在已经登记使用过的发票!");
			throw new CoreException(GDErrorCodes.EUPS_PROF00_03_ERROR);
		}
		//TODO:申请非会计流水
		String tckno = get(BBIPPublicService.class).getBBIPSequence();
		gdeupsInvDtlBok.setTckNo(tckno);
		context.setData("tckNo", tckno);//非会计流水号
		//TODO:登记发票记录
		String tolNum = context.getData("tolNum");//张数
		String regTlr = context.getData("sup1Id");//授权柜员
		String nodno = context.getData("br");//机构号
		gdeupsInvDtlBok.setTolNum(tolNum);
		gdeupsInvDtlBok.setRegTlr(regTlr);
		gdeupsInvDtlBok.setNodno(nodno);
		gdeupsInvDtlBok.setRegDat(DateUtils.formatAsSimpleDate(new Date()));
		gdeupsInvDtlBok.setUseNum("0");//使用张数
		gdeupsInvDtlBok.setClrNum("0");//作废张数
		get(GdeupsInvDtlBokRepository.class).insert(gdeupsInvDtlBok);
		context.setData("tckNo", tckno);
		logger.info("InvoiceGrantManagerServiceActionPROF00 end ... ...");
	}

}

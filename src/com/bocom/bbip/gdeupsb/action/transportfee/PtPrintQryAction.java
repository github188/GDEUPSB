package com.bocom.bbip.gdeupsb.action.transportfee;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;








import com.asn1c.core.Int16;
import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbPubInvInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbPubInvInfoRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PtPrintQryAction extends BaseAction{
	
	private final static Log log = LogFactory.getLog(PtPrintQryAction.class);
	@Autowired
	GDEupsbPubInvInfoRepository gdEupsbPubInvInfoRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("PtPrintQryAction start......");
		
		GDEupsbPubInvInfo gdEupsbPubInvInfo = new GDEupsbPubInvInfo();
		gdEupsbPubInvInfo.setActNo(ctx.getData(GDParamKeys.ACT_NO).toString());
		gdEupsbPubInvInfo.setTmp01(ctx.getData(GDParamKeys.CAR_NO).toString());
		gdEupsbPubInvInfo.setTmp02(ctx.getData(GDParamKeys.CAR_TYP).toString());
		
		int num =  gdEupsbPubInvInfoRepository.findCount(gdEupsbPubInvInfo);
		if(num==0){
			throw new CoreException(ErrorCodes.EUPS_CONSOLE_INFO_NOTEXIST);
		}
		
		Pageable pageable = BeanUtils.toObject(ctx.getDataMap(), PageRequest.class);
		Page<GDEupsbPubInvInfo> invInfoPage = get(GDEupsbPubInvInfoRepository.class).find(pageable, gdEupsbPubInvInfo);
		setResponseFromPage(ctx, "resultList", invInfoPage);
	}

}

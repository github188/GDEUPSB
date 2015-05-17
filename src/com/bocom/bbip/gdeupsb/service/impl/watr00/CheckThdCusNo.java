package com.bocom.bbip.gdeupsb.service.impl.watr00;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.CommonRequest;
import com.bocom.bbip.comp.account.AccountService;
import com.bocom.bbip.comp.account.support.CardInfo;
import com.bocom.bbip.comp.account.support.CusActInfResult;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdEupsWatAgtInf;
import com.bocom.bbip.gdeupsb.repository.GdEupsWatAgtInfRepository;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class CheckThdCusNo extends BaseAction{
	
	private static final Log log = LogFactory.getLog(CheckThdCusNo.class);
	@Autowired
	GdEupsWatAgtInfRepository gdEupsWatAgtInfRepository;
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("CheckThdCusNo start.......");
		ctx.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		GdEupsWatAgtInf gdEupsWatAgtInf = new GdEupsWatAgtInf();
		gdEupsWatAgtInf.setThdCusNo(ctx.getData(ParamKeys.THD_CUS_NO).toString());
		List<GdEupsWatAgtInf> list = gdEupsWatAgtInfRepository.find(gdEupsWatAgtInf);
		if(!CollectionUtils.isEmpty(list)){
//			ctx.setData(ParamKeys.RSP_MSG, "协议已存在");
			throw new CoreException("BBIP0004EU0132");
		}
		
		// 验密
		log.info("进行密码校验！..");
		Result auth = get(AccountService.class).auth(CommonRequest.build(ctx), ctx.getData("cusAC").toString(), ctx.getData("pwd").toString());
		log.info("check pwd end");
		if (!auth.isSuccess()) {
			log.info("check pwd eroor");
			throw new CoreException(GDErrorCodes.EUPS_PASSWORD_ERROR); // 密码验证错误
		}
		
		ctx.setData("pwd", null);
		
		ctx.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	}

}

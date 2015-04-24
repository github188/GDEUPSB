package com.bocom.bbip.gdeupsb.action.wat;

import java.util.List;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdEupsWatAgtInf;
import com.bocom.bbip.gdeupsb.repository.GdEupsWatAgtInfRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class CommQryWatCusAgentAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("水费协议信息查询开始！..");

//		String cusAc = context.getData("cusAC"); // 记账卡号
//		context.setData("cusAc", cusAc);
//		GdEupsWatAgtInf para = BeanUtils.toObject(context.getDataMap(), GdEupsWatAgtInf.class);
		GdEupsWatAgtInf para = new GdEupsWatAgtInf();
		para.setThdCusNo((String)context.getData(ParamKeys.THD_CUS_NO));
		List<GdEupsWatAgtInf> resultList = get(GdEupsWatAgtInfRepository.class).find(para);
		log.info("查询的返回信息为:" + resultList);
		if (CollectionUtils.isEmpty(resultList)) {
			throw new CoreException(ErrorCodes.EUPS_AGE_AGR_INFO_NOT_EXIST);
		}
		else if (resultList.size() > 1) {
			throw new CoreException(ErrorCodes.EUPS_AGENT_CHK_ERROR);
		} else {
			context.setDataMap(BeanUtils.toMap(resultList.get(0)));
		}
		
		log.info("最终的返回信息为:" + context.getDataMap());
	}

}

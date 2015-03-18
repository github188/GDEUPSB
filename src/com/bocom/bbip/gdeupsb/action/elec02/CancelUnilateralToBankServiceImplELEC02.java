package com.bocom.bbip.gdeupsb.action.elec02;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.eups.spi.service.single.CancelUnilateralToBankService;
import com.bocom.bbip.eups.spi.vo.CancelDomain;
import com.bocom.bbip.eups.spi.vo.CommHeadDomain;
import com.bocom.bbip.utils.Assert;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public class CancelUnilateralToBankServiceImplELEC02 extends BaseAction implements
		CancelUnilateralToBankService {
	private static final Log logger = LogFactory.getLog(CancelUnilateralToBankServiceImplELEC02.class);

	@Override
	public Map<String, Object> aftCclToBank(CommHeadDomain arg0,
			CancelDomain arg1, Context context) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> preCclToBank(CommHeadDomain arg0,
			CancelDomain arg1, Context context) throws CoreException {
		context.setData(ParamKeys.TXN_DTE, new Date());
		EupsTransJournal journal=new EupsTransJournal();
		journal.setThdSqn((String)context.getData(ParamKeys.THD_SEQUENCE));
		List<EupsTransJournal>list=get(EupsTransJournalRepository.class).find(journal);
		Assert.isNotEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
		context.setData(ParamKeys.OLD_TXN_SEQUENCE, list.get(0).getSqn());
		return null;
	}

}

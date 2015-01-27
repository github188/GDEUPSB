package com.bocom.bbip.gdeupsb.expand;

import java.util.Map;

import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public interface BatchAccountDealService {

	public abstract Map<String, Object> initDeal(Context context)
			throws CoreException;

	public abstract Map<String, Object> getAcFileDeal(Context context)
			throws CoreException;
}

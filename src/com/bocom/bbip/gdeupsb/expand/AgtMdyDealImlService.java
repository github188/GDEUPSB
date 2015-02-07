package com.bocom.bbip.gdeupsb.expand;

import java.util.Map;

import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public interface AgtMdyDealImlService {
	public abstract  Map<String,Object> agtDelByGdsIdService(Context context)
			throws CoreException;

}

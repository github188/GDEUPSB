package com.bocom.bbip.gdeupsb.expand;

import java.util.Map;

import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

public interface AgtFileSendImlService {
	public abstract  Map<String,Object> agtFleSndDelService(Context context)
			throws CoreException;
}

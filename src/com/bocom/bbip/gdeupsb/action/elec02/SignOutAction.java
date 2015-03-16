package com.bocom.bbip.gdeupsb.action.elec02;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
/**
 * @author wuyh
 * @date 2015-2-13 上午09:06:35
 * @Company TD
 */
public class SignOutAction extends BaseAction {

	public void execute(Context context) throws CoreException {
		
		((BBIPPublicServiceImpl)get(GDConstants.BBIP_PUBLIC_SERVICE))
		 .synExecute("eups.signInOutThird", context);
	}
}

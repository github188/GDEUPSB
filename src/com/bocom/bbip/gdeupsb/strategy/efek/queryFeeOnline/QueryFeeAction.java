package com.bocom.bbip.gdeupsb.strategy.efek.queryFeeOnline;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.entity.EupsAmountInfo;
import com.bocom.bbip.eups.repository.EupsAmountInfoRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class QueryFeeAction implements Executable{
	private final static Log logger=LogFactory.getLog(QueryFeeAction.class);

	/**
	 * 欠费查询 处理前
	 */
	@Override
	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("=========Start  CheckFeeAction ");
			context.setData(GDParamKeys.TOTNUM, "1");
			context.setData(GDParamKeys.SVRCOD, "10");
	}
}

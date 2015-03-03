package com.bocom.bbip.gdeupsb.action.zh;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.PageRequest;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.gdeupsb.entity.AreaInfo;
import com.bocom.bbip.gdeupsb.repository.AreaInfoRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/** 481217 地区代码模糊查询*/
public class EupsQueryAreaCodebyAreaName  extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsQueryAreaCodebyAreaName.class);

	public void process(Context context)throws CoreException,CoreRuntimeException,IOException{
		logger.info("地区代码模糊查询 start!!");
		final String AreaNam=ContextUtils.assertDataHasLengthAndGetNNR(context, "AreaNam", ErrorCodes.EUPS_FIELD_EMPTY);
		Pageable pageable=ContextUtils.getDataAsObject(context, PageRequest.class);
		Assert.isFalse(null==pageable, ErrorCodes.EUPS_FIELD_EMPTY);
        Page<AreaInfo> page=get(AreaInfoRepository.class).findByAreaName(pageable,AreaNam);
		Assert.isNotEmpty(page.getElements(), ErrorCodes.EUPS_QUERY_NO_DATA);
		logger.info("查找出的数据为【"+BeanUtils.toMaps(page.getElements())+"】");
		/** 查询结果分页返回BBOS*/
        setResponseFromPage(context,"data",page);
		logger.info("地区代码模糊查询 成功!!");
	}

	
	
	
}

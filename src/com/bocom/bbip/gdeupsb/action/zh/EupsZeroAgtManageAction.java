package com.bocom.bbip.gdeupsb.action.zh;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.entity.Zero;
import com.bocom.bbip.gdeupsb.repository.ZeroRepository;
import com.bocom.bbip.thd.org.apache.commons.io.IOUtils;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.support.CollectionUtils;

/** 481216 零余额账户托收协议管理 */
/**
 * @author wuyh
 *
 */
public class EupsZeroAgtManageAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(EupsZeroAgtManageAction.class);
	private static final int ADD = 1;
	private static final int QUERY = 2;
	private static final int UPDATE = 3;
	private static final int DELETE = 4;
	private static final String UPDAT="3";
	public void process(Context context) throws CoreException {

        logger.info("------零余额账户托收协议管理start----");
         /**操作类型1.新增 2.查询 3.修改 4.删除*/
         final String oprTyp=ContextUtils.assertDataNotEmptyAndGet(context, ParamKeys.OPERATION_TPYE, ErrorCodes.EUPS_FIELD_EMPTY);
         
		try {
			proces(context, oprTyp);
		} catch (IOException e) {
			logger.info("----操作类型错误----");
			throw new CoreException(ErrorCodes.EUPS_OPR_TYP_ERR);
			
		}
         
         logger.info("------零余额账户托收协议管理success----");
	}

	private void proces(Context context, final String oprTyp)throws CoreException, IOException {
		Zero zero = ContextUtils.getDataAsObject(context, Zero.class);
		List<Zero> list = get(ZeroRepository.class).findZero(zero);
		int operate = QUERY;
		try {
			operate = Integer.parseInt(oprTyp);
		} catch (Exception e) {
			logger.info("----操作类型错误----");
			throw new CoreException(ErrorCodes.EUPS_OPR_TYP_ERR);
		}
		switch (operate) {
		case ADD:
			Assert.isEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
			add(context, zero);
			break;
		case QUERY:
			Assert.isNotEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
			query(context, list.get(0));
			break;
		case UPDATE:
			Assert.isNotEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
			update(context,zero,list.get(0));
			break;
		case DELETE:
			Assert.isNotEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
			delete(context, zero);
			break;
		default:
			logger.info("----操作类型错误----");
			throw new CoreException(ErrorCodes.EUPS_OPR_TYP_ERR);

		}
	}

	private void add(Context context, Zero zero) throws CoreException, IOException  {
		logger.info("----协议新增----");
		get(ZeroRepository.class).addZero(zero);
		//generateReport(context);
	}

	private void query(Context context, Zero zero) throws CoreException {
		logger.info("----协议查询----");
		logger.info(BeanUtils.toFlatMap(zero));
		ContextUtils.setDataMapAsFlatMap(context, zero);
	}

	private void update(Context context, Zero src,Zero dest) throws CoreException, IOException {
		logger.info("----协议修改----");
		get(ZeroRepository.class).updateZero(src);
		context.setData("payCod1", src.getPayCod());
		context.setData("payNam1", src.getPayNam());
		ContextUtils.setDataMapAsFlatMap(context, dest);
		//generateReport(context);
	}

	private void delete(Context context, Zero zero) throws CoreException, IOException {
		logger.info("----协议删除----");
		get(ZeroRepository.class).deleteZero(zero);
		//generateReport(context);
	}
	private void generateReport(Context context)throws CoreException, IOException {
		Map<String, String> mapping = CollectionUtils.createMap();
		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		String sampleFile="config/report/zh/ZHZeroAgt.vm";
		if (UPDAT.equals(context.getData(ParamKeys.OPERATION_TPYE))) {
			sampleFile = "config/report/zh/ZHZeroAgtUpd.vm";
		}
		mapping.put("sample", sampleFile);
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		render.setReportNameTemplateLocationMapping(mapping);
		String result = render.renderAsString("sample", context);
		logger.info("generate report content:****"+ new String(result.getBytes("gbk")));
		IOUtils.write(result.getBytes("gbk"),new FileOutputStream("D:\\report.txt") );
		
	}
}

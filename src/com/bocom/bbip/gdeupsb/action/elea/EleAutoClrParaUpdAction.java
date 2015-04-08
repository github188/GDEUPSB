package com.bocom.bbip.gdeupsb.action.elea;

import java.util.Date;
import java.util.List;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdElecClrInf;
import com.bocom.bbip.gdeupsb.entity.GdElecClrTbl;
import com.bocom.bbip.gdeupsb.repository.GdElecClrInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdElecClrTblRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

/**
 * 广州电力清算参数修改
 * 
 * @author qc.w
 * 
 */
public class EleAutoClrParaUpdAction extends BaseAction {

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("开始更新/查询清算参数");
		String oprFlg = context.getData("oprFlg"); // 操作标志

		if ("0".equals(oprFlg)) { // 查询
			log.info("准备查询清算参数");
			// 获取电力清算信息表信息
			GdElecClrInf gdElecClrInf = new GdElecClrInf();
			gdElecClrInf.setBrNo(GDConstants.GZ_ELE_BK_GZ);
			List<GdElecClrInf> gdElecClrInfList = get(GdElecClrInfRepository.class).find(gdElecClrInf);
			if (CollectionUtils.isEmpty(gdElecClrInfList)) {
				log.error("不存在清算参数信息");
				throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CLEAR_INFO_ERROR); // 不存在清算参数信息
			}
			else {
				gdElecClrInf = gdElecClrInfList.get(0);
				// 获取与第三方约定的第三方会计日期
				context.setDataMap(BeanUtils.toMap(gdElecClrInf));
				context.setData("delDte", gdElecClrInf.getClrDat());
				context.setData("delTme", gdElecClrInf.getClrTim());
			}
		} else if ("1".equals(oprFlg)) { // 修改
//			String authLvl = context.getData("authLvl"); // 授权
//			if (StringUtils.isEmpty(authLvl)) {
//				throw new CoreException(GDErrorCodes.EUPS_PROF00_01_ERROR);
//			}

			GdElecClrInf gdElecClrInf = new GdElecClrInf();
			gdElecClrInf.setBrNo(GDConstants.GZ_ELE_BK_GZ);
			List<GdElecClrInf> gdElecClrInfList = get(GdElecClrInfRepository.class).find(gdElecClrInf);
			if (CollectionUtils.isEmpty(gdElecClrInfList)) {
				log.error("不存在清算参数信息");
				throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CLEAR_INFO_ERROR); // 不存在清算参数信息
			}
			gdElecClrInf = gdElecClrInfList.get(0);

			gdElecClrInf.setAutFlg((String) context.getData("autFlg"));
			gdElecClrInf.setClrSts((String) context.getData("clrSts"));
			gdElecClrInf.setClrDat((String) context.getData("delDte")); // 清算日期
			gdElecClrInf.setClrTim((String) context.getData("delTme")); // 清算时间
			gdElecClrInf.setBrNo(GDConstants.GZ_ELE_BK_GZ);

			// 更新清算信息表信息
			get(GdElecClrInfRepository.class).save(gdElecClrInf);

			// 新增一条操作记录
			GdElecClrTbl gdElecClrTbl = BeanUtils.toObject(context.getDataMap(), GdElecClrTbl.class);
			gdElecClrTbl.setSqn(get(BBIPPublicService.class).getBBIPSequence());
			gdElecClrTbl.setClrDat((String) context.getData("delDte"));
			gdElecClrTbl.setClrTim((String) context.getData("delTme"));
			gdElecClrTbl.setLogDat(DateUtils.format(new Date(), "yyyyMMdd"));
			gdElecClrTbl.setLogTim(DateUtils.format(new Date(), "hhmmss"));
			get(GdElecClrTblRepository.class).insert(gdElecClrTbl);

			log.info("交易处理完毕");
		}

	}

}

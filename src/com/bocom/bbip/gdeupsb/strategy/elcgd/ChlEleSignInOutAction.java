package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.spi.service.basemanage.ChlAutoSignInOutService;
import com.bocom.bbip.eups.spi.vo.SignInOutDomain;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.entity.GdElecClrInf;
import com.bocom.bbip.gdeupsb.repository.GdElecClrInfRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 电力签到签退-若签到，则更改清算日期信息表
 * 
 * @author qc.w
 * 
 */
public class ChlEleSignInOutAction implements ChlAutoSignInOutService {

	// implements Executable {

	private final static Logger log = LoggerFactory.getLogger(ChlEleSignInOutAction.class);

	@Autowired
	GdElecClrInfRepository gdElecClrInfRepository;

	// @Override
	// public void execute(Context context) throws CoreException,
	// CoreRuntimeException {
	// log.info("ChlEleSignInOutAction signInOutDeal start!..");
	//
	// // 判断是否是签到
	// String txnTyp = context.getData(ParamKeys.TXN_TYP);
	// if (Constants.SIGN_SET_TYPE_SIGNIN.equals(txnTyp)) {
	// log.info("类型为签到，处理清算日期");
	// // 根据分行号查询清算信息
	// GdElecClrInf gdElecClrInf = new GdElecClrInf();
	// gdElecClrInf.setBrNo("441999");
	// List<GdElecClrInf> gdElecClrInfList =
	// gdElecClrInfRepository.findClrInf(gdElecClrInf);
	// if (CollectionUtils.isEmpty(gdElecClrInfList)) {
	// log.error("不存在清算日期参数");
	// throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CLEAR_INFO_ERROR); //
	// 不存在清算参数信息
	// }
	// else {
	// gdElecClrInf = gdElecClrInfList.get(0);
	// // 将清算日期+1，并更新相关信息表
	// String clrDatStr = gdElecClrInf.getClrDat();
	// Date clrDat = DateUtils.parse(clrDatStr);
	// clrDat = DateUtils.calDate(clrDat, 1);
	// gdElecClrInf.setClrDat(DateUtils.format(clrDat,
	// DateUtils.STYLE_yyyyMMdd));
	// gdElecClrInfRepository.updClrDte(gdElecClrInf);
	// }
	// } else {
	// log.info("类型为签退，不需要做处理");
	// }

	// }

	@Override
	public Map<String, Object> signInOutDeal(SignInOutDomain signinoutdomain, Context context) throws CoreException {
		log.info("ChlEleSignInOutAction signInOutDeal start!..");

		// 判断是否是签到
		String txnTyp = context.getData(ParamKeys.TXN_TYP);
		if (Constants.SIGN_SET_TYPE_SIGNIN.equals(txnTyp)) {
			log.info("类型为签到，处理清算日期");
			// 根据分行号查询清算信息
			GdElecClrInf gdElecClrInf = new GdElecClrInf();
			gdElecClrInf.setBrNo("441999");
			List<GdElecClrInf> gdElecClrInfList = gdElecClrInfRepository.find(gdElecClrInf);
			if (CollectionUtils.isEmpty(gdElecClrInfList)) {
				log.error("不存在清算日期参数");
				throw new CoreException(GDErrorCodes.EUPS_ELE_GZ_CLEAR_INFO_ERROR); // 不存在清算参数信息
			}
			else {
				gdElecClrInf = gdElecClrInfList.get(0);
				// 将清算日期+1，并更新相关信息表
				String clrDatStr = gdElecClrInf.getClrDat();
				Date clrDat = DateUtils.parse(clrDatStr);
				clrDat = DateUtils.calDate(clrDat, 1);
				gdElecClrInf.setClrDat(DateUtils.format(clrDat, DateUtils.STYLE_yyyyMMdd));
				gdElecClrInfRepository.updClrDte(gdElecClrInf);
			}
		} else {
			log.info("类型为签退，不需要做处理");
		}
		return null;
	}

}

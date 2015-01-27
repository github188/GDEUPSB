package com.bocom.bbip.gdeupsb.strategy.elcgd;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsActSysPara;
import com.bocom.bbip.eups.repository.EupsActSysParaRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 
 * @author qc.w
 * 
 */
public class PrePayElecStrategyAction implements Executable {

	private final static Logger log = LoggerFactory.getLogger(PrePayElecStrategyAction.class);

	// @Autowired
	// AcpsCorporInfoRepository acpsCorporInfoRepository;

	@Autowired
	EupsActSysParaRepository eupsActSysParaRepository;

	@Override
	public void execute(Context context) throws CoreException, CoreRuntimeException {
		log.info("PreInitElecStrategyAction start!..");

		String realSplNo = null;
		// 查找单位编号
//		EupsActSysPara eupsActSysPara = new EupsActSysPara();
//		eupsActSysPara.setComNo((String) context.getData(ParamKeys.COMPANY_NO));
//		List<EupsActSysPara> resultList = eupsActSysParaRepository.find(eupsActSysPara);
//		if (CollectionUtils.isNotEmpty(resultList)) {
//			eupsActSysPara = resultList.get(0);
//			realSplNo = eupsActSysPara.getSplNo();
//		} else {
//			throw new CoreException(ErrorCodes.EUPS_COM_NO_NOTEXIST);
//		}

		
		String vchTyp = context.getData(ParamKeys.BV_KIND); // 凭证种类
		// TODO:需要支持以下支付方式:现金缴费,银行卡,活期存折,本外活本,个人支票,本票,现金支票,转账支票,划线支票

		// 对私
		if (GDConstants.GZ_ELE_PAY_KND_CASH.equals(vchTyp) || GDConstants.GZ_ELE_PAY_KND_CARD.equals(vchTyp)
				|| GDConstants.GZ_ELE_PAY_KND_ALVC.equals(vchTyp) || GDConstants.GZ_ELE_PAY_KND_AOVC.equals(vchTyp)
				|| GDConstants.GZ_ELE_PAY_KND_PRCK.equals(vchTyp))
		{
			context.setData("rmkCde", "9102"); // 备注
		}
		else { // 对公
			context.setData(ParamKeys.BUSS_KIND, GDConstants.GZ_ELE_BUS_KND_ELEC); // TODO:业务种类：电费,此处需要修改，老的是CRP51，数据库中长度不足
			context.setData(ParamKeys.BUS_TYP, Constants.BUS_TYP_2); // 业务类型：代缴
			// context.setData("rmkCde", "代扣电费"); //备注
			context.setData("bakFld1", "代扣电费"); // TODO:以前的摘要码是代扣电费，现在的摘要码为4位，故使用备注
		}
		context.setData(ParamKeys.CCY, Constants.CCY_CDE_CNY); // 币种
		
		//TODO: 用第三方地区编号当作配型部类型，存入数据库
		context.setData("dptTyp", context.getData(ParamKeys.THD_REGION_NO));

		// 原代码中的渠道号转换不需要做

		// TODO:TRM和D441渠道启动完整性检查，其他渠道是否超时不冲正？目前标准版超时都会去冲正，请确认 vipQc
		// <Set>inTxnCnl=STRCAT(|,$TxnCnl,|)</Set>
		// <If
		// condition="AND(IS_EQUAL_STRING(@PARA.RollBack,1),INTCMP(GETSTRPOS(@PARA.RollBackCnls,$inTxnCnl),5,0))">
		// <Exec func="PUB:BeginWork"/> <!--启动完整性控制-->
		// </If>

	}
}

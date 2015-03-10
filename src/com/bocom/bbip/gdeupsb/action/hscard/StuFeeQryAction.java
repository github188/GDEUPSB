package com.bocom.bbip.gdeupsb.action.hscard;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbStuFeeInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbStuFeeInfoRepository;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class StuFeeQryAction extends BaseAction {

	private final static Log log = LogFactory.getLog(StuFeeQryAction.class);
	
	@Autowired
	GDEupsbStuFeeInfoRepository gdEupsbStuFeeInfoRepository;

	public void execute(Context ctx) throws CoreException, CoreRuntimeException {
		log.info("StuFeeQryAction start.......");

		GDEupsbStuFeeInfo gdEupsbStuFeeInfo = new GDEupsbStuFeeInfo();
		gdEupsbStuFeeInfo.setSchCod(ctx.getData(GDParamKeys.SCH_COD).toString()); //学校代码
		gdEupsbStuFeeInfo.setStuCod(ctx.getData(GDParamKeys.STU_COD).toString());//学籍编码
		gdEupsbStuFeeInfo.setPayTem(ctx.getData(GDParamKeys.PAY_TEM).toString());//缴费学期(一学年:01 第一学期:02 第二学期:03 第三学期:04)
		gdEupsbStuFeeInfo.setPayYea(ctx.getData(GDParamKeys.PAY_YEA).toString());//缴费年份
		List<GDEupsbStuFeeInfo> feeInfoList = gdEupsbStuFeeInfoRepository.find(gdEupsbStuFeeInfo);

		GDEupsbStuFeeInfo gdEupsbStuFeeInfo1 = feeInfoList.get(0);
		ctx.setData(GDParamKeys.STU_NAM, gdEupsbStuFeeInfo1.getStuNam());//学生姓名
		ctx.setData(GDParamKeys.XZF_AMT, gdEupsbStuFeeInfo1.getXzfAmt());//学费
		ctx.setData(GDParamKeys.ROM_AMT, gdEupsbStuFeeInfo1.getRomAmt());//住宿费
		ctx.setData(GDParamKeys.TXN_AMT, gdEupsbStuFeeInfo1.getTxnAmt());//总费用
	}
}

package com.bocom.bbip.gdeupsb.action.hscard;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbStuFeeInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbStuFeeInfoRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class StuPayFeeAction extends BaseAction{
	
	private final static Log log = LogFactory.getLog(StuPayFeeAction.class);
	
	@Autowired
	GDEupsbStuFeeInfoRepository gdEupsbStuFeeInfoRepository;
	
	public  void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("StuPayFeeAction start......");
		
		GDEupsbStuFeeInfo gdEupsbStuFeeInfo = new GDEupsbStuFeeInfo();
		
//		SELECT Status DtlSts,Flag DtlFlg,StuNam FeeNam,XZFAmt,LrnAmt,RomAmt,OthAmt,SchNam FROM srfdtlrec441 
//		WHERE SchCod='%s' and StuCod='%s' and PayTem='%s' and PayYea='%s'
//	</Sentence>
//	<Fields>SchCod|StuCod|PayTem|PayYea|</Fields>
		gdEupsbStuFeeInfo.setSchCod((String)ctx.getData(GDParamKeys.SCH_COD));
		gdEupsbStuFeeInfo.setStuCod((String)ctx.getData(GDParamKeys.STU_COD));
		gdEupsbStuFeeInfo.setPayTem((String)ctx.getData(GDParamKeys.PAY_TEM));
		gdEupsbStuFeeInfo.setPayYea((String)ctx.getData(GDParamKeys.PAY_YEA));
		List<GDEupsbStuFeeInfo> feeInfoList = gdEupsbStuFeeInfoRepository.find(gdEupsbStuFeeInfo);
		if(CollectionUtils.isEmpty(feeInfoList)){
			//TODO:
//			<Set>MsgTyp=E</Set>
//			<Set>RspCod=478399</Set>
//			<Set>RspMsg=学籍编码不存在</Set>
			throw new CoreException(ErrorCodes.EUPS_FIND_ISEMPTY);
		}
//		TODO:<Set>StuNam=$FeeNam</Set>
//		<If condition="$DtlSts=1">
//		<Set>MsgTyp=E</Set>
//		<Set>RspCod=478399</Set>
//		<Set>RspMsg=正在缴费,不能重复缴费!</Set>
//		<Return/>
//	</If>
	}

}

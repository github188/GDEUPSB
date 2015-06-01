package com.bocom.bbip.gdeupsb.strategy.efek.payFeeOnline;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsThdTranCtlDetailRepository;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

/**
 * 划扣   联机银行方单边记账
 * @author liyawei
 *
 */
public class PrePayToBankAction implements Executable{
		private final static Log logger=LogFactory.getLog(PrePayToBankAction.class);
		@Autowired
		EupsThdTranCtlDetailRepository eupsThdTranCtlDetailRepository;
		@Autowired
		BBIPPublicService bbipPublicService;
		/**
		 * 第三方单边记账处理前
		 */
		@Override
		public void execute(Context context) throws CoreException,
			CoreRuntimeException {
			logger.info("=========Start PrePayToBankAction");
			context.setData("acoBr", "01441800999");
			
//			context.setData(ParamKeys.COMPANY_NO, context.getData("comNos"));
			context.setData(ParamKeys.THD_TXN_STS,"S");
			context.setData(GDParamKeys.TOTNUM, "1");
			  
			String ActFlg=(String)context.getData(ParamKeys.PAY_TYPE);
			if("0".equals(ActFlg)){              //对公
				//GDContants定义常量
				context.setData(ParamKeys.TXN_CODE,"451240");
				context.setData("NamChk", "1");//		<Set>NamChk=1</Set> <!--测试不检查，生产考虑打开-->
				context.setData(ParamKeys.CCY_NO,Constants.EUPS_PAYMENT_TO_ACPS_CCY_CDE);
				context.setData(GDParamKeys.ACCMOD, "1");
				context.setData(ParamKeys.BAK_FLD1,"代扣电费");
			}else if("2".equals(ActFlg) || "4".equals(ActFlg)){                  //卡
				context.setData(ParamKeys.TXN_CODE,"471140");
				context.setData(ParamKeys.CHL_TYP, "L");  //<Set>CnlTyp=L</Set>
				context.setData(ParamKeys.CCY_NO,Constants.EUPS_PAYMENT_TO_ACPS_CCY_CDE);
			}
		
			BigDecimal bigDecimal=new BigDecimal(context.getData(ParamKeys.TXN_AMT).toString());
			BigDecimal txnAmt=bigDecimal.scaleByPowerOfTen(-2);
			context.setData(ParamKeys.TXN_AMT,txnAmt );
			context.setData(ParamKeys.BUS_TYP, "2");
			context.setData("rsvFld3",context.getData("mfmVchNos"));
			
			context.setData("thdTxnDate", DateUtils.format((Date)context.getData(ParamKeys.THD_TXN_DATE),DateUtils.STYLE_yyyyMMdd));
			context.setData("thdTxnTime", DateUtils.format((Date)context.getData(ParamKeys.THD_TXN_TIME),DateUtils.STYLE_HHmmss));
			
			context.setData("br", "01441800999");
			context.setData("bk", "01441999999");
			
			String str=(String)context.getData("comNos");
			if(str !=null){
				if(str.length()>4){
					context.setData("bakFld2", str.substring(0,4));
				}else{
					context.setData("bakFld2", str);
				}
			}
		}
}

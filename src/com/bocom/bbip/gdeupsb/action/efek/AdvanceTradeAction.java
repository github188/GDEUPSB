package com.bocom.bbip.gdeupsb.action.efek;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.euif.component.util.StringUtil;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * @author liyawei
 */
public class AdvanceTradeAction extends BaseAction {
	@Autowired
	BBIPPublicService bbipPublicService;
	@Autowired
    @Qualifier("callThdTradeManager")
    ThirdPartyAdaptor callThdTradeManager;
	private final static Log logger = LogFactory.getLog(AdvanceTradeAction.class);

	/**
	 * 银行发起存入预付款
	 */

	public void execute(Context context) throws CoreException,
			CoreRuntimeException {
		logger.info("===========Start  AdvanceTradeAction");
		
		
		context.setData("extFields", "01441800999");
		context.setData(GDParamKeys.TOTNUM, "1");
		// 第三方客户标识
		context.setData(ParamKeys.THD_CUS_NO,context.getData(GDParamKeys.PAY_NO));
		context.setData(ParamKeys.RSV_FLD1, context.getData("traPerIdenty"));
		// 预付费金额设定
		context.setData(ParamKeys.TXN_AMT,context.getData(GDParamKeys.PAYMENTIN_ADVANCE_MONEY));
		context.setData(ParamKeys.PAYFEE_TYPE, Constants.TXN_PAYFEE_TYPE_RECHARGE);
		
		context.setData(ParamKeys.TXN_DTE,DateUtils.parse(DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd)));
		context.setData(ParamKeys.TXN_TME,DateUtils.formatAsTranstime(new Date()));
		// 柜员号 
		// 记账
		String ActFlg = (String) context.getData(ParamKeys.PAY_MDE); 
		
		if ("0".equals(ActFlg)) { // 对公
			// 需要GDContants定义常量
			context.setData(ParamKeys.THD_TXN_CDE, "451240");

			context.setData(GDParamKeys.CCYTYP, "EFE999999999");
			context.setData("NamChk", "1");// <Set>NamChk=1</Set>
											// <!--测试不检查，生产考虑打开-->
			context.setData(ParamKeys.TXN_CHL, "0");// <Set>VchFlg=0</Set>
													// <!--渠道交易不检查-->

			// 字段长度 context.setData(ParamKeys.BV_KIND,"000"); 凭证种类 字符长度
			context.setData(ParamKeys.BV_KIND, "00");

			context.setData(ParamKeys.BV_NO, "00000000");
			context.setData(ParamKeys.CCY_NO,Constants.EUPS_PAYMENT_TO_ACPS_CCY_CDE);

			context.setData(GDParamKeys.ACCMOD, "1");
			context.setData(ParamKeys.BAK_FLD1, "代扣电费");
		} else if ("2".equals(ActFlg) || "4".equals(ActFlg)) { // 卡
			context.setData(ParamKeys.TXN_CODE, "471140");
			context.setData(ParamKeys.CHL_TYP, "L"); // <Set>CnlTyp=L</Set>
			context.setData("Mask", "9102");// <Set>Mask=9102</Set>

			// 字段长度 context.setData(ParamKeys.BV_KIND,"000"); 凭证种类 字符长度
			context.setData(ParamKeys.BV_KIND, "00");

			context.setData(ParamKeys.BV_NO, "00000000");// <Set>VchCod=00000000</Set>
			context.setData(ParamKeys.CCY_NO,
					Constants.EUPS_PAYMENT_TO_ACPS_CCY_CDE);

			context.setData(GDParamKeys.CCYTYP, "1");
			context.setData(GDParamKeys.VCHCHK, "0");

			context.setData("CAgtNo", "EFE9999999"); // 清算单位协议号要改

			context.setData("GthFlg", "N");// <Set>GthFlg=N</Set>
		}else if(ActFlg.equals("5")){
			context.setData(ParamKeys.BAK_FLD1, "支票代扣电费");
		}
			context.setData(GDParamKeys.SVRCOD, "13");
			
			callThd(context);
			context.setData(ParamKeys.RSV_FLD4, context.getData(GDParamKeys.BUS_TYPE));
			context.setData(ParamKeys.RSV_FLD5, context.getData(GDParamKeys.PAY_TYPE));
			context.setData(ParamKeys.CUS_NME, context.getData("CusNme"));
			context.setData(ParamKeys.BUS_TYP, "2");
			
			String bakFld2=context.getData("comNos").toString().substring(0,4);
			context.setData("bakFld2", bakFld2);
			logger.info("~~~~~~~~~~~~End AdvanceTradeAction");
	}
	/**
	 *报文信息  外发第三方
	 */
	public void callThd(Context context){  
		
		context.setData(GDParamKeys.TREATY_VERSION, GDConstants.TREATY_VERSION);//协议版本
		context.setData(GDParamKeys.TRADE_PERSON_IDENTIFY, GDConstants.TRADE_PERSON_IDENTIFY);//交易人标识
		context.setData(GDParamKeys.BAG_TYPE, GDConstants.BAG_TYPE);//数据包类型
		context.setData(GDParamKeys.TRADE_START,GDConstants.TRADE_START);//交易发起方
		
		context.setData(GDParamKeys.TRADE_SEND_DATE,DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));//交易发送日期
		context.setData(GDParamKeys.TRADE_SEND_TIME, DateUtils.formatAsHHmmss(new Date()));//交易发送时间
				context.setData(GDParamKeys.TRADE_PRIORITY, GDConstants.TRADE_PRIORITY);//交易优先
				context.setData(GDParamKeys.REDUCE_SIGN, GDConstants.REDUCE_SIGN);//压缩标志
				context.setData(GDParamKeys.TRADE_RETURN_CODE, GDConstants.TRADE_RETURN_CODE);//交易返回代码
		
				context.setData(GDParamKeys.NET_NAME, GDConstants.NET_NAME);//网点名称
				context.setData(GDParamKeys.SECRETKEY_INDEX, GDConstants.SECRETKEY_INDEX);//密钥索引
				context.setData(GDParamKeys.SECRETKEY_INIT, GDConstants.SECRETKEY_INIT);//密钥初始向量
				String comNos=context.getData("comNos");
				if(StringUtils.isNotEmpty(comNos)){
					if(comNos.length()>4){
						comNos=comNos.substring(0,4)+"00";
					}while(comNos.length()<6){
						comNos=comNos+"0";
					}
				}else{
					comNos="030000";
				}
				context.setData(GDParamKeys.TRADE_RECEIVE,comNos);//交易接收方
				context.setData(GDParamKeys.TRADE_SOURCE_ADD, GDConstants.TRADE_SOURCE_ADD);//交易源地址
				context.setData(GDParamKeys.TRADE_AIM_ADD, GDConstants.TRADE_AIM_ADD);//交易目标地址
				context.setData("PKGCNT", "000001");
				context.setData(ParamKeys.BANK_NO, "301");
				context.setData("thdObkCde", "301");
				context.setData(GDParamKeys.BUS_IDENTIFY, "YDLW19");		
				context.setData("sqns", context.getData(ParamKeys.SEQUENCE));
	}
}

package com.bocom.bbip.gdeupsb.action.efek;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupspayment.BuaPayment;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PowerChangeTreatyAction extends BaseAction{
	@Autowired
	BBIPPublicService bbipPublicService;
		/**
		 * 供电方发起供电到银行变更代扣协议请求
		 */
		public void execute(Context context)throws CoreException,CoreRuntimeException{
			log.info("===========Start PowerChangeTreatyAction");
			context.setData(GDParamKeys.SVRCOD, "32");
			if(context.getData(GDParamKeys.NET_NAME)){
					context.setData(ParamKeys.BR, context.getData(GDParamKeys.NET_NAME));
			}
			context.setData(GDParamKeys.TOTNUM, "1");
			context.setData(ParamKeys.SEQUENCE, bbipPublicService.getBBIPSequence());
			context.setData(ParamKeys.TXN_CHL, "EFE");  //交易渠道
			context.setData("CnlSub", "");
		    String NodNo=context.getData(GDParamKeys.NET_NAME).toString().substring(1, 4)+"800";
		    context.setData(GDParamKeys.NET_NAME, NodNo); //网点号
		    
			String conSign=context.getData(GDParamKeys.CONSIGN).toString();
			if("2".equals(conSign)){
					context.setData(ParamKeys.BANK_NO, context.getData(GDParamKeys.NEWBANKNO));
					context.setData(ParamKeys.CUS_AC, context.getData(GDParamKeys.NEWCUSAC));
					context.setData(GDParamKeys.CUSACNAME, context.getData(GDParamKeys.NEWCUSACNAME));
					String accTyp=context.getData(ParamKeys.ACC_TYPE);
					if("0".equals(accTyp)){
						context.setData(ParamKeys.TXN_CODE, "109000");
					}else if("1".equals(accTyp) || "3".equals(accTyp)){
						context.setData(ParamKeys.TXN_CODE, "476520");
						context.setData(ParamKeys.CCY,"CNY");
					}else if("2".equals(accTyp) || "4".equals(accTyp)){
							context.setData(ParamKeys.MESSAGE_TYPE, "E");
							context.setData(ParamKeys.RSP_CDE, "EFE999");
				            context.setData(ParamKeys.RSP_MSG, "该类账户暂不支持");
					}else{
							context.setData(ParamKeys.MESSAGE_TYPE, "E");
							context.setData(ParamKeys.RSP_CDE, "EFE999");
							context.setData(ParamKeys.RSP_MSG, "账户类型错误");
							throw new CoreRuntimeException("账户类型错误");
					}
					//TODO 主机 
					new BuaPayment().execute(context);
//					<Exec func="PUB:CallHostOther" error="IGNORE">
//			          <Arg name="HTxnCd" value="$HTxnCd"/>
//			          <Arg name="ObjSvr" value="SHSTPUB1"/>
//			        </Exec>
//			        <If condition="INTCMP(~RetCod,4,0)">
//			          <Set>MsgTyp=E</Set>
//			          if(){           <If condition="INTCMP(STRLEN(TRIM($HRspCd,all)),4,0)">
//			            <Set>RspCod=$HRspCd</Set>
//			          }else{
//			            context.setData(ParamKeys.RSP_CDE, "EFE999");
//			         	}
//			          context.setData(ParamKeys.RSP_MSG, "账户资料核对出错")
//			          throw new CoreRuntimeException("账户资料核对出错");
//			        }
//			        <If condition="IS_NOEQUAL_STRING(DELSPACE($ActNam,both),$XQYZHMC)">
		//						context.setData(ParamKeys.MESSAGE_TYPE, "E");
		//						context.setData(ParamKeys.RSP_CDE, "EFE999");
		//						context.setData(ParamKeys.RSP_MSG, "账户名称【"+context.getData(GDParamKeys.NEWCUSACNAME);+"】与银行系统不一致");
		//			            throw new CoreRuntimeException("账户名称【"+context.getData(GDParamKeys.NEWCUSACNAME);+"】与银行系统不一致");
//			        </If>
					//TODO 修改
					String accType=context.getData(ParamKeys.ACC_TYPE).toString();
					String idNo=context.getData(ParamKeys.ECIF_REF_NUM).toString();
					if("0".equals(accType)){
							if("06".equals(idNo) || context.getData("ActQ04").toString().trim().equals(context.getData(ParamKeys.ECIF_REF_NUM).toString())){
								context.setData(ParamKeys.MESSAGE_TYPE, "E");
								context.setData(ParamKeys.RESPONSE_CODE, "EFE999");
								context.setData(ParamKeys.RSP_MSG, "证件种类【"+accType+"】或证件号码【"+idNo+"】与银行系统不一致");
								throw new CoreRuntimeException("证件种类【"+accType+"】或证件号码【"+idNo+"】与银行系统不一致");
							}
					}else if("1".equals(accType) || "3".equals(accType)){
							//TODO   <If condition="OR(IS_NOEQUAL_STRING($ZJLX,$IdTyp),IS_NOEQUAL_STRING($ZJHM,$IdNo))">
							if(accTyp.equals(accType) || null !=idNo){
								context.setData(ParamKeys.MESSAGE_TYPE, "E");
								context.setData(ParamKeys.RESPONSE_CODE, "EFE999");
								context.setData(ParamKeys.RSP_MSG, "证件种类【"+accType+"】或证件号码【"+idNo+"】与银行系统不一致");
							}
					}
			}
			context.setData("ChkFlg", "S");
			//TODO 核对签约表
//		      <Switch expression="~RetCod">
//			<Case value="0" desc="存在记录">
//		      //TODO 跟新签约表-
//		          <Exec func="PUB:UpdateRecord" desc="更新记录">
//		            <Arg name="TblNam" value="EfekCusAgt"/>
//		            <Arg name="CndSts" value="UpdCusAgt"/>
//		          </Exec>
//		          <Break/>
//		        </Case>
//		        <Case value="-2" desc="不存在记录">
//		          <Exec func="PUB:InsertRecord">
//		            <Args>
//		              <Arg name="tablename" value="EfekCusAgt"/>
//		            </Args>
//		          </Exec>
//		          <Break/>
//		        </Case>
//		        <Default>
//		          <Set>MsgTyp=E</Set>
//		          <Set>RspCod=EFE999</Set>
//		          <Set>RspMsg=STRCAT(登记【,$JFH,】错误)</Set>
//		          <Return/>
//		        </Default>
//		      </Switch>
			context.setData("PKGCNT", Constants.HOST_RESPONSE_CODE_SUCC);
			context.setData(ParamKeys.MESSAGE_TYPE, "N");
			context.setData(ParamKeys.RSP_CDE, Constants.HOST_RESPONSE_CODE_SUCC);
		}
}

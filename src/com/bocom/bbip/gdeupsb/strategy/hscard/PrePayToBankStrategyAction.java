package com.bocom.bbip.gdeupsb.strategy.hscard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;



import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.core.Executable;

public class PrePayToBankStrategyAction implements Executable{
	private final static  Log logger = LogFactory.getLog(PrePayToBankStrategyAction.class);
	
	@Autowired
	BBIPPublicService bbipPublicService;
	public void execute(Context context) throws CoreException,CoreRuntimeException{
		
		logger.info("PrePayToBankStrategyAction start......");
//		产生银行处理流水号,返回给第三方


	      context.setData(ParamKeys.MFM_VCH_NO, bbipPublicService.getBBIPSequence());
//			TODO:<Set>BnkId=STRCAT(SUBSTR($BrNo,1,3),$ActDat,$SelVal)</Set>
	      
	      
//	     TODO:
//	      context.setData(ParamKeys.TXN_CHL,"CPL" );
	      context.setData(ParamKeys.CCY,Constants.CCY_CDE_CNY );
//	      context.setData(ParamKeys.AC_TYP,"4" );   //ICS中是ACT_TYP,paramkeys里还有acc_typ,还不确定用哪个
	      
	      context.setData(ParamKeys.ACT_NO, context.getData(ParamKeys.CUS_AC));   
//	      TODO:
//	    	  <Exec func="PUB:CallHostOther" error="IGNORE">
//	        <Arg name="HTxCd"  value="476520"/>
//	        <Arg name="ObjSvr" value="SHSTPUB1"/>
//	      </Exec>
//	      <Switch expression="~RetCod">
//	        <Case value="0" desc="成功">
//	          <Set>ActNam=BANKCOMM</Set><!-- 测试用===================================== -->
//	          <If condition="IS_NOEQUAL_STRING($TIdNo,DELSPACE($IdNo,all))">
//	            <Set>MsgTyp=E</Set>
//	            <Set>RspCod=CPL992</Set>
//	            <Set>RspMsg=证件号码匹配失败</Set>
//	            <Return/>
//	          </If>
//	          <If condition="IS_NOEQUAL_STRING($CardNm,DELSPACE($ActNam,all))">
//	            <Set>MsgTyp=E</Set>
//	            <Set>RspCod=CPL993</Set>
//	            <Set>RspMsg=姓名匹配失败</Set>
//	            <Return/>
//	          </If>
//	          <Break/>
//	        </Case>
//	        <Case value="-1" desc="超时"/>
//	        <Case value="-2" desc="系统错误"/>
//	        <Case value="-10" desc="未发送">
//	          <Set>MsgTyp=E</Set>
//	          <Set>RspCod=CPL999</Set>
//	          <Set>RspMsg=银行系统繁忙，请稍后再做</Set>
//	          <Return/>
//	        </Case>
//	        <Case value="3" desc="失败">
//	          <Set>MsgTyp=E</Set>
//	          <Set>RspMsg=查询用户信息失败</Set>
//	          <Set>RspCod=CPL991</Set>
//	          <Return/>
//	        </Case>
//	        <Case value="-9" desc="需要授权">
//	          <Set>MsgTyp=E</Set>
//	          <Set>RspCod=CPL999</Set>
//	          <Set>RspMsg=卡状态异常，请咨询开户银行</Set>
//	          <Return/>
//	        </Case>
//	        <Default>
//	          <Set>MsgTyp=E</Set>
//	          <Set>RspCod=CPL999</Set>
//	          <Set>RspMsg=未知错误</Set>
//	          <Return/>
//	        </Default>
//	      </Switch>
//	      
//	      <!--交易卡协议验证-->TODO
//	      <Set>CrdNo=$CardNo</Set>
//	      <Exec func="PUB:CallHostOther" error="IGNORE">
//	        <Arg name="HTxnCd" value="010420"/>
//	        <Arg name="ObjSvr" value="SHSTPUB1"/>
//	      </Exec>
//	      <Switch expression="~RetCod">
//	        <Case value="0" desc="成功">
//	          <Break/>
//	        </Case>
//	        <Case value="-1" desc="超时"/>
//	        <Case value="-2" desc="系统错误"/>
//	        <Case value="-10" desc="未发送">
//	          <Set>RspCod=CPL999</Set>
//	          <Set>RspMsg=银行系统繁忙，请稍后再做</Set>
//	          <Break/>
//	        </Case>
//	        <Case value="3" desc="失败">
//	          <Set>RspCod=CPL999</Set>
//	          <Set>RspMsg=检验交易卡协议失败</Set>
//	          <Break/>
//	        </Case>
//	        <Case value="-9" desc="需要授权">
//	          <Set>RspCod=CPL999</Set>
//	          <Set>RspMsg=卡状态异常，请咨询开户银行</Set>
//	          <Break/>
//	        </Case>
//	        <Default>
//	          <Set>RspCod=CPL999</Set>
//	          <Set>RspMsg=未知错误</Set>
//	          <Break/>
//	        </Default>
//	      </Switch>
	     
	      //登记交易流水(这步标准版做了，此处不需要做。)
//	      context.setData(ParamKeys.BUSS_KIND, "PCL52");
//	      context.setData(ParamKeys.CHL_TYP, "L");
//	      context.setData(ParamKeys.REMARK, "9199");
//	      context.setData(ParamKeys.ACC_TYPE, "4");  //ICS中是ACTFLG,paramkeys里有ac_type,acc_type,暂不确定用哪个。
//	      context.setData(ParamKeys.ACT_NO, ParamKeys.CUS_AC);
//	      context.setData(ParamKeys.PAY_MDE, "0");   //paramkeys中有pay_mde,pay_mode,暂不确定。
//	     <Set>TActNo=$CrpAct</Set>  tactno找不到。
//	      <Set>VchChk=0</Set>    监督标志，没找到。
//	      <Set>CAgtNo=9999999999</Set> 没找到。
//	       <Set>GthFlg=N</Set>  没找到。
//	      <Set>TckNo=ZZZZZZZZZZZ</Set>  本字段应该是会计流水号，但不知与前文的银行处理流水号有何区别。
//	      context.setData(ParamKeys.TXN_STS, "U");
//	     <Set>VchCod=00000000</Set>  没找到。
//	      context.setData(ParamKeys.CCY, "1");


//	      
	      
//	      EupsTransJournal eupsTransJournal = BeanUtils.toObject(context.getDataMap(), EupsTransJournal.class);
	      
//	      eupsTransJournalRepository.save(eupsTransJournal);
//	      if(!eupsTransJournal.getRspCde().equals("0")){
	      
//	    	  context.setData(ParamKeys.MESSAGE_TYPE, "E");
//	    	  context.setData(ParamKeys.RESPONSE_CODE, "CPL999");
//				context.setData(ParamKeys.RESPONSE_MESSAGE, "处理交易信息异常");
//				return;
//	      }
	}
}

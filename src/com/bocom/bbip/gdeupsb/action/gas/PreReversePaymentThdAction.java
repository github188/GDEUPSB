package com.bocom.bbip.gdeupsb.action.gas;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.python.modules.newmodule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;


/**
 * 惠州燃气 单笔冲正
 * @author WangMingQin
 *
 */
public class PreReversePaymentThdAction extends BaseAction{
	
	private static final Log logger=LogFactory.getLog(PreReversePaymentThdAction.class);
	
//	@Autowired
//	ThirdPartyAdaptor thirdPartyAdaptor;

//	@Autowired
//	EupsThdBaseInfo etbi;
	

	EupsTransJournal etj = new EupsTransJournal();
	
	
	public void execute(Context context) throws CoreException, CoreRuntimeException{
		logger.info("============Enter in PreReversePaymentThdAction....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//		<Transaction code="460711" desc="单笔托收冲正">
//		   
//		   <DynSentence name="QryCrpAgr"><!--查询单位信息 -->
//					<Sentence>
//						SELECT TActNo,BBusTyp,CrpCod FROM savcrpagr where CAgtNo='%s' and SvrSts='1'
//					</Sentence>
//					<Fields>CAgtNo|</Fields>
//				</DynSentence>
//				
//		    <DynSentence name="Sel_CLogNo"><!--获取详细流水信息,Status='1'表示正常扣款-->
//		      <Sentence>
//		        select * from Gastxnjnl491 where CLogNo='%s' and  Status='1' and OptAmt='%s' and ThdSts='B0' and HRspCd='SC0000' and HTxnSt='S' and TTxnCd='SMPCPAY'
//		      </Sentence>
//		      <Fields>CLogNo|TxnAmt|</Fields>
//		    </DynSentence>
//		    <DynSentence name="UdpEfeaSts"> <!--更新流水表 -->
//		       <Sentence>
//		         update Gastxnjnl491 set Status='3',OptAmt='0',OptAmt1='0',TTxnCd='UPay',HTxnSt='C',ErrMsg='第三方冲正' where CLogNo='%s' and  Status='1' and OptAmt='%s' and ThdSts='B0' and HRspCd='SC0000' and HTxnSt='S' and TTxnCd='SMPCPAY'
//		       </Sentence>
//		       <Fields>CLogNo|TxnAmt|</Fields>
//		    </DynSentence>
//		    
//		    <FlowCtrl>
//		      <Set>AplCls=207</Set>
//		      <Set>BusTyp=GASH1</Set>
//		      <Set>Result=NoPay</Set> 
//		      <Set>TxnAmt1=0</Set>
		      
		//TODO 应用类别码 AplCls 不知道具体有何用
		context.setData(GDParamKeys.GAS_APL_CLS, "207");
		context.setData(ParamKeys.BUS_TYP, GDParamKeys.EUPS_BUS_TYP_GAS);
		context.setData(GDParamKeys.GAS_RESULT, "NoPay");//默认冲正未成功
		BigDecimal txnAmt1 = new BigDecimal(0.0);
		context.setData(ParamKeys.BAK_FLD3, String.valueOf(txnAmt1));
		
		
//		      <Exec func="PUB:InitTransaction" error="IGNORE"/>    <!--交易初始化,预留20140609cjx优化-->
//		      <If condition="~RetCod!=0">
//		     	    <Set>MsgTyp=E</Set>
//		     	    <Set>RspMsg=初始化失败</Set> 
//		     	    <Set>RspCod=GAS999</Set>  <!--返回错误码-->    	   
//		          <Return/>        
//		       </If>
		
//			<Exec func="PUB:ReadRecord">
//				<Arg name="SqlCmd" value="QryCrpAgr"/>
//			</Exec>
//			<If condition="~RetCod!=0">
//		        <Set>RspCod=GAS999</Set>
//		        <Set>RspMsg=单位协议不存在</Set>
//		        <Return/>
//		      </If> 
		
		
		//根据CAgtNo,SvrSts查询单位信息表
//		SELECT TActNo,BBusTyp,CrpCod FROM savcrpagr where CAgtNo='%s' and SvrSts='1'
		String comNo = (String)context.getData(ParamKeys.COMPANY_NO);
		String useSts = "0";	////useSts--0:正常,1:停用,2:删除        SvrSts状态1对应useSts的哪个状态？暂用状态0，为正常使用状态
		EupsThdBaseInfo etbi = new EupsThdBaseInfo();
		etbi.setComNo(comNo);
		etbi.setUseSts(useSts);
		List<EupsThdBaseInfo> etbiList = get(EupsThdBaseInfoRepository.class).find(etbi);
		if(CollectionUtils.isEmpty(etbiList) && etbiList==null ){
			context.setData(ParamKeys.MESSAGE_TYPE, "E");
			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
			context.setData(ParamKeys.RSP_MSG, "单位协议不存在");
			logger.info("=======================单位协议不存在===================");
			throw new CoreRuntimeException("单位协议不存在");
		}
		logger.info("=======================================================================");
//		      <Set>TActNo=491800012620190029499</Set>  
		//TODO TActNo是第三方交易序号？不确定，暂用
		context.setData(ParamKeys.THD_TXN_NO, "491800012620190029499");
		
		
//		       <Exec func="PUB:ReadRecord">  <!--获取详细流水信息-->
//		       <Arg name="SqlCmd" value="Sel_CLogNo"/>
//		       </Exec>
//		       <If condition="~RetCod!=0">     <!--无该流水信息-->
//		        <Set>MsgTyp=E</Set>
//		        <Set>RspCod=GAS999</Set>
//		        <Set>RspMsg=无该笔流水信息</Set>  
//		        <Return/>
//		       </If> 
		
		String bakFld5 = "1";	//satus = "1"
		String bakFld4 = "B0";	//ThdSts='B0'
		String mfmRspCde = "SC0000";	//HRspCd='SC0000'
		String mfmTxnSts = "S";	//HTxnSt='S'
		
//	根据燃气托收流水、账务状态（正常扣款）、已扣费用、返回第三方状态（B0扣款成功）、主机返回码（SC0000）、主机交易状态（S）、第三方交易码查询流水表
//	CTD会传： 交易码thdTxnCde、托收流水thdSqn、银行标识BK、冲正金额txnAmt
//		select * from Gastxnjnl491 where CLogNo='%s' and  Status='1' and OptAmt='%s' and ThdSts='B0' and HRspCd='SC0000' and HTxnSt='S' and TTxnCd='SMPCPAY'
		
		EupsTransJournal etj = new EupsTransJournal();
		etj = BeanUtils.toObject(context.getDataMap(), EupsTransJournal.class);
		etj.setBakFld4(bakFld4);
		etj.setBakFld5(bakFld5);
		etj.setMfmRspCde(mfmRspCde);
		etj.setMfmTxnSts(mfmTxnSts);
		
		List<EupsTransJournal> etjList = get(EupsTransJournalRepository.class).find(etj);
		if(CollectionUtils.isEmpty(etjList) && etjList==null ){
			context.setData(ParamKeys.MESSAGE_TYPE, "E");
			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
			context.setData(ParamKeys.RSP_MSG, "无该笔流水信息");
			throw new CoreRuntimeException("无该笔流水信息");
		}
		
		//TODO 校验各分行账号
		//TODO ReadModuleCfg
		//TODO 判断对公对私
		//TODO 启动完整性控制
		//TODO 上送主机进行冲正
		
		
//		      <!--校验各分行账号-->    <!--TODO-->
//		      <Set>AcJudFunc=AcJud_441999</Set>
//		      <Call function="$AcJudFunc">
//		        <Input name="ActNo|NewFlg|"/>
//		        <Output name="OActNo|ActCls|"/>
//		      </Call>
//		      <If condition="$MsgTyp=E">
//		        <Set>MsgTyp=E</Set>
//		        <Set>RspCod=GAS999</Set>
//		        <Set>RspMsg=帐户类型有误</Set>  
//		        <Return/>
//		       </If> 
//		       <Exec func="PUB:ReadModuleCfg">
//		        <Arg name="Application" value="GAS_DB"/>
//		        <Arg name="Transaction" value="460711"/>
//		      </Exec>
//		      <Switch expression="$ActCls">   //ActCls ？？？
//		        <Case value="2" /> <!-- 对私 -->
//		        <Case value="3" />
//		        <Case value="5" >
//		         <If condition="INTCMP($ActCls,3,3)">
//		             <Set>ActFlg=4</Set>  <!--对私卡号-->
//		             <Set>ActTyp=4</Set>
//		          </If>
//		          <Else>
//		             <Set>ActFlg=2</Set>  <!--对私帐号-->
//		             <Set>ActTyp=2</Set>
//		          </Else>
//		          <Break/>
//		        </Case>
//		        <Case value="0" /> <!-- 对公 -->
//		        <Case value="4" >
//		          <Set>ActTyp=1</Set>
//		          <Break/>
//		        </Case>
//		        <Default>
//		        <Set>MsgTyp=E</Set>
//		        <Set>RspCod=GAS999</Set>
//		        <Set>RspMsg=帐户类型不存在</Set>  
//		        <Return/>
//		        </Default>
//		      </Switch>
//		      <If condition="OR(INTCMP($ActTyp,3,0),INTCMP($ActTyp,3,2),INTCMP($ActTyp,3,4))"><!--对私-->
//		        <Set>HTxnCd=471149</Set>
//		      </If>
//		      <ElseIf condition="INTCMP($ActTyp,3,1)"> <!--对公-->
//		        <Set>HTxnCd=451619</Set>
//		      </ElseIf>
//		      <Else>                                   <!--第三方-->
//		        <Return/>
//		      </Else>
//		      <Set>TTxnCd=460711</Set>
//		      <Set>TIATyp=C</Set>
//		      <Set>TlrId=ERQTDT1</Set>
//		      <Exec func="PUB:BeginWork"/>    <!--启动完整性控制-->
//		      <Exec func="PUB:CallHostAcc" error="IGNORE"><!--上送主机进行冲正交易-->
//		        <Arg name="HTxnCd" value="959999"/>
//		        <Arg name="ObjSvr" value="SHSTPUB1"/>
//		      </Exec>
//
		
		
//		      <If condition="IS_EQUAL_STRING(~RetCod,0)">    <!--上主机冲正成功-->
//		        <Set>HTxnSt=C</Set>   <!--  U-预计,S-成功,F-失败,T-超时,R-已冲正,C-抹账  -->
//		        <Set>TxnSts=C</Set>
//		        <Set>Result=UPay</Set>
//		        <Set>TxnAmt1=$TxnAmt</Set>
//		        <Set>Status=3</Set>
//		        <Exec func="PUB:ExecSql" error="IGNORE">
//		           <Arg name="SqlCmd"   value="UdpEfeaSts"/>   <!--更新流水表-->
//		        </Exec>
//		        <If condition="~RetCod=-1">
//		          <Set>MsgTyp=E</Set>
//		          <Set>RspCod=GAS999</Set>
//		          <Set>RspMsg=数据库处理失败!</Set>
//		          <Return/>
//		        </If>
//		      </If> 
//		      <Else>
//		        <Set>MsgTyp=E</Set>
//		        <Set>RspCod=$HRspCd</Set>
//		        <Set>RspMsg=冲正不成功</Set>      	
//		      </Else>
//		    </FlowCtrl>
//		  </Transaction>	
		
		//上主机冲正成功则更新流水表,更新失败同为冲正失败
		
		//Test    Test    Test    Test    Test    Test    Test    
		if("0".equals("0")){//模拟冲正成功
			context.setData(ParamKeys.MFM_TXN_STS, "C");
			context.setData(ParamKeys.TXN_STS, "C");
			context.setData(GDParamKeys.GAS_RESULT, "Upay");
			context.setData(ParamKeys.BAK_FLD3, context.getData(ParamKeys.TXN_AMOUNT));
			context.setData(ParamKeys.BAK_FLD5, "3");
			
			etj = BeanUtils.toObject(context.getDataMap(), EupsTransJournal.class);
			get(EupsTransJournalRepository.class).update(etj);
		}else{
			context.setData(ParamKeys.MESSAGE_TYPE, "E");
			context.setData(ParamKeys.RSP_CDE, context.getData(ParamKeys.MFM_RSP_CDE));
			context.setData(ParamKeys.RSP_MSG, "冲正不成功");
		}
		
			
//			context.setData(ParamKeys.MFM_TXN_STS, GDConstants.GAS_TXN_STS_C);
//			context.setData(ParamKeys.THD_TXN_STS, GDConstants.GAS_TXN_STS_C);
//			context.setData(GDParamKeys.GAS_RESULT, "UPay");
//			context.setData(ParamKeys.TXN_AMOUNT, arg1);  //<Set>TxnAmt1=$TxnAmt</Set>
//			context.setData(ParamKeys.TXN_STS, "3");  //<Set>Status=3</Set> 此处应为交易状态TXN_STS，而不是 BAK_FLD5
//			
//			etj = BeanUtils.toObject(context.getDataMap(), EupsTransJournal.class);
//			try{
//				get(EupsTransJournalRepository.class).update(etj);
//			}catch(Exception e){
//				context.setData(ParamKeys.MESSAGE_TYPE, "E");
//				context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
//				context.setData(ParamKeys.RSP_MSG, "数据库处理失败，冲正不成功!");
//			}
//			
		
//		
//		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	
	}
}

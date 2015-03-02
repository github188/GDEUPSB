package com.bocom.bbip.gdeupsb.action.gas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.python.modules.newmodule;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsCusAgent;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.entity.EupsTransJournal;
import com.bocom.bbip.eups.repository.EupsCusAgentRepository;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.eups.repository.EupsTransJournalRepository;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdGasCusDay;
import com.bocom.bbip.gdeupsb.repository.GdGasCusDayRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class PreparePayHstHzAction extends BaseAction{
	
	private static final Log logger=LogFactory.getLog(PreparePayHstHzAction.class);
	
	public void execute(Context context) throws CoreException, CoreRuntimeException{
//		logger.info("Enter in PreparePayHstHzAction...........");
//		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
//		
		
//		  <Transaction code="460710" desc="单笔托收">
//		    
//		    <DynSentence name="QryCrpAgr"><!--查询单位信息 -->
//					<Sentence>
//						SELECT TActNo,BBusTyp,CrpCod FROM savcrpagr where CAgtNo='%s' and SvrSts='1'
//					</Sentence>
//					<Fields>CAgtNo|</Fields>
//				</DynSentence>
//		    <DynSentence name="Existcusall">   <!--查询该用户编号与该卡号是否有签约!-->
//		      <Sentence>
//		       select count(*) cnt1 from Gascusall491 where UserNo='%s' and ActNo='%s'
//		      </Sentence>
//		      <Fields>UserNo|ActNo|</Fields>
//		    </DynSentence>	
//		    <DynSentence name="QrycusallActNam">   <!--查询该用户编号与该卡号是否有签约!-->
//		      <Sentence>
//		       select ActNam from Gascusall491 where UserNo='%s' and ActNo='%s'
//		      </Sentence>
//		      <Fields>UserNo|ActNo|</Fields>
//		    </DynSentence>
//		    <DynSentence name="FailInsGastxnjnl491">          <!--失败流水表 -->
//		      <Sentence>
//		        INSERT INTO Gastxnjnl491(ActNam,HTxnCd,TxnTyp,LogNo,CLogNo,OptDat,OptTim,UserNo,ActNo,OptAmt,OptAmt1,PayAmt,PayYea,Status,TTxnCd,ThdSts,ErrMsg)
//		     VALUES(' ',' ',' ','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')
//		      </Sentence>
//		     <Fields>LogNo|CLogNo|OptDat|OptTim|UserNo|ActNo|OptAmt|OptAmt1|PayAmt|PayYea|Status|TTxnCd|ThdSts|ErrMsg|</Fields>
//		    </DynSentence>
//		    
//		    <DynSentence name="SucGastxnjnl491">          <!--成功流水表 -->
//		      <Sentence>
//		        update Gastxnjnl491 set TxnTyp='%s',ActNam='%s',OptAmt='%s',OptAmt1='%s',ThdSts='%s',Status='%s',HTxnCd='%s',HRspCd='%s',HTxnSt='%s',HLogNo='%s',TckNo='%s',ErrMsg='%s' where LogNo='%s' 
//		      </Sentence>
//		     <Fields>TxnTyp|ActNam|OptAmt|OptAmt1|ThdSts|Status|HTxnCd|HRspCd|HTxnSt|HLogNo|TckNo|ErrMsg|LogNo|</Fields>
//		    </DynSentence>
//		  
//		    <FlowCtrl>
//		    	
//		      <Set>AplCls=207</Set>
//		      <Set>BusTyp=GASH1</Set>  <!--业务类型-->
//		      <Set>Mask=GASH</Set>     
//		      <Set>OptTim=GETDATETIME(YYYY-MM-DD HH:MM:SS)</Set> <!--交易时间-->
//		      <Set>OptDat=GETDATETIME(YYYY-MM-DD)</Set> <!--交易日期-->
//		      <Set>TTxnCd=SMPCPAY</Set> <!--第三方交易码-->
//		      <Set>OptAmt=0</Set> <!--已扣费用-->
//		      <Set>OptAmt1=0</Set>
//		      <Set>ThdSts=B3</Set> <!--返回第三方状态 (B0为扣费成功 B1为金额不足扣费失败 B2为无此帐号或账号与用户编号匹配错误扣费失败 B3其它原因扣费失败)-->
//		      <Set>Status=0</Set>  <!-- 账务状态(0：未扣款;1:正常扣款;2:抹账;3:冲正)-->
//		      <Set>RspMsg=扣款失败</Set>
//		      <Set>ErrMsg=扣款失败</Set>
		
//				BigDecimal txnAmt = new BigDecimal(0.0) ;
//				BigDecimal txnAmt1 = new BigDecimal(0.0) ;
//				txnAmt = txnAmt.add((BigDecimal)context.getData(ParamKeys.TXN_AMOUNT));
//				txnAmt1 = txnAmt1.add((BigDecimal) context.getData(ParamKeys.REQUEST_TXN_AMOUNT));
				
//				context.setData(GDParamKeys.GAS_APL_CLS, "207");
//				context.setData(ParamKeys.EUPS_BUSS_TYPE, GDParamKeys.EUPS_BUS_TYP_GAS);
//				context.setData(GDParamKeys.GAS_MASK, "GASH");	// <Set>Mask=GASH</Set>
				
//				context.setData(ParamKeys.TXN_TME, DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
//				context.setData(ParamKeys.TXN_DTE, DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
				
//				context.setData(ParamKeys.TXN_TME, new Date());
//				context.setData(ParamKeys.TXN_DTE, new Date());
//				
//				context.setData(ParamKeys.THD_TXN_CDE, GDParamKeys.GAS_SMPCPAY);
//				context.setData(ParamKeys.TXN_AMOUNT, txnAmt);	//OptAmt=0 
//				context.setData(ParamKeys.BAK_FLD3, String.valueOf(txnAmt1));	//OptAmt1=0  备用字段3
//				context.setData(ParamKeys.BAK_FLD4, GDConstants.GAS_THDSTS_TYP_B3);	//ThdSts=B3
//				context.setData(ParamKeys.BAK_FLD5, "0");	//Status=0 
//				
//				context.setData(ParamKeys.RSP_MSG, "扣款失败");	//<Set>RspMsg=扣款失败</Set>
//				context.setData(ParamKeys.BAK_FLD2, "扣款失败");	//<Set>ErrMsg=扣款失败</Set>
				
//		      <Exec func="PUB:InitTransaction" error="IGNORE"/>    <!--交易初始化,20140609cjx优化-->
//		      <If condition="~RetCod!=0">
//		     	    <Set>MsgTyp=E</Set>
//		     	    <Set>RspMsg=初始化失败</Set> 
//		     	    <Set>RspCod=GAS999</Set>  <!--返回错误码-->    	   
//		          <Return/>        
//		       </If>
//		      <!--取前置流水号-->
//		      <Exec func="PUB:GetLogNo" error="IGNORE"/>
			
//		String sqn = context.getData(ParamKeys.SEQUENCE);
//		
//		context.setData(ParamKeys.SEQUENCE, "20150126000001");
//		context.setData(ParamKeys.EUPS_BUSS_TYPE, "PGAS00");
//		context.setData(ParamKeys.ITG_TYP, "0");
//		context.setData(ParamKeys.TXN_STS, "U");
//		context.setData(ParamKeys.BR, "1");
//		context.setData(ParamKeys.ORG_CDE, "1");
//		context.setData(ParamKeys.TERMINAL_NO, "1");
//		context.setData(ParamKeys.TXN_TLR, "1");
//		context.setData(ParamKeys.SERVICE_NAME, "1");
//		context.setData(ParamKeys.COMPANY_NO, "12354");
//		context.setData(ParamKeys.CCY, "1");
//		context.setData(ParamKeys.FIL_FLG, "1");
//		
//		String thdTxnCde = context.getData(ParamKeys.THD_TXN_CDE);
//		燃气托收流水号	18	年月日（8位）+银行标志（4位）+6位流水号  示例：20130927CNJT000001 (会传)
//		String thdSqn = context.getData(ParamKeys.THD_SQN);
//		String bk = context.getData(ParamKeys.BK);
//		String cusNo = context.getData(ParamKeys.CUS_NO);
//		String cusAc = context.getData(ParamKeys.CUS_AC);
//		//PayYea 备用字段1
//		String bakFld1 = context.getData(GDParamKeys.GAS_PAY_YEA);
//		context.setData(ParamKeys.BAK_FLD1, bakFld1);
		//应缴费用payAmt   REQ_TXN_AMT
//		BigDecimal reqTxnAmt = new BigDecimal(0.0);
//		reqTxnAmt =reqTxnAmt.add((BigDecimal) context.getData(ParamKeys.REQUEST_TXN_AMOUNT));
//		context.setData(ParamKeys.SEQUENCE, sqn);
//		context.setData(ParamKeys.THD_TXN_CDE, thdTxnCde);
//		context.setData(ParamKeys.THD_SEQUENCE, thdSqn);
//		context.setData(ParamKeys.BK, bk);
		
//		      <If condition="~RetCod!=0">
//		     	    <Set>MsgTyp=E</Set>
//		     	    <Set>RspMsg=初始化失败</Set> 
//		     	    <Set>RspCod=GAS999</Set>  <!--返回错误码-->    	   
//		          <Return/>        
//		       </If>
		
//		        <Exec func="PUB:ExecSql" error="IGNORE">
//		            <Arg name="SqlCmd"   value="FailInsGastxnjnl491"/>
//		          </Exec>
		
		/*INSERT INTO Gastxnjnl491(ActNam,HTxnCd,TxnTyp,LogNo,CLogNo,OptDat,OptTim,UserNo,ActNo,OptAmt,OptAmt1,PayAmt,PayYea,Status,TTxnCd,ThdSts,ErrMsg)
	     VALUES(' ',' ',' ','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')
		*/	
//		EupsTransJournal etj = new EupsTransJournal();
//		etj = BeanUtils.toObject(context.getDataMap(), EupsTransJournal.class);
//		
//		etj.setCusNme("");	//ActNam
//		etj.setMfmTxnCde("");	//HTxnCd
//		etj.setTxnTyp("");	//TxnTyp
//		etj.setSqn(sqn);	//LogNo
//		etj.setThdSqn(thdSqn);	//CLogNo
//		etj.setTxnDte((Date) context.getData(ParamKeys.TXN_DTE));	//OptDat
//		etj.setTxnTme((Date) context.getData(ParamKeys.TXN_TME));	//OptTim
//		etj.setCusAc(context.getData(ParamKeys.CUS_AC).toString());	//UserNo
//		etj.setCusAc(cusAc);	//ActNo
//		etj.setReqTxnAmt(context.getData(ParamKeys.REQUEST_TXN_AMOUNT));	//PayAmt
//		etj.setBakFld1(bakFld1);	//PayYea
//		etj.setThdTxnCde(thdTxnCde);	//TTxncd
//		etj.setBakFld2(errMsg);	//ErrMsg  备用字段2
//		
//		// TODO OptAmt
//		etj.setTxnAmt(txnAmt);
//		// TODO OptAmt1
//		etj.setBakFld3(String.valueOf(txnAmt1));
//		// TODO Status
//		etj.setBakFld5(context.getData(ParamKeys.BAK_FLD5).toString());
//		// TODO ThdSts 返回第三方状态
//		etj.setBakFld4(thdSts);
		
		
//		try{
//			get(EupsTransJournalRepository.class).insert(etj);
//			logger.info("=================================================================");
//		}catch(Exception e){
//			context.setData(ParamKeys.MESSAGE_TYPE, "E");
//			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
//			context.setData(ParamKeys.RSP_MSG, "数据库出错-请联系银行中心处理");
//			throw new CoreRuntimeException("数据库出错-请联系银行中心处理");
//		}
		
//		        <If condition="~RetCod=-1">
//		          <Exec func="PUB:RollbackWork" error="IGNORE" />  
//		          <Set>MsgTyp=E</Set>
//		          <Set>RspCod=GAS999</Set>
//		          <Set>RspMsg=数据库处理失败!</Set>
//		          <Return/>
//		        </If>
		
//		      <Exec func="PUB:ReadRecord">
//						<Arg name="SqlCmd" value="QrycusallActNam"/>
//					</Exec>
//		SQL:select ActNam from Gascusall491 where UserNo='%s' and ActNo='%s'
//		EupsCusAgent eca = new EupsCusAgent();
//		eca = BeanUtils.toObject(context.getDataMap(), EupsCusAgent.class);
//		
//		eca.setCusNo(context.getData(ParamKeys.CUS_NO).toString());
//		eca.setCusAc(context.getData(ParamKeys.CUS_AC).toString());
//		List<EupsCusAgent> ecaList =  new ArrayList<EupsCusAgent>();
//		try{
//			ecaList = get(EupsCusAgentRepository.class).find(eca);
//			if(CollectionUtils.isEmpty(ecaList) || ecaList==null){
//				context.setData(ParamKeys.MESSAGE_TYPE, "E");
//				context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
//				context.setData(ParamKeys.RSP_MSG, "单位协议不存在");
//			}
//		}catch(Exception e){
//			context.setData(ParamKeys.MESSAGE_TYPE, "E");
//			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
//			context.setData(ParamKeys.RSP_MSG, "系统出错-请联系银行中心处理");
//		}
// 查第三方基本信息表
//					<Exec func="PUB:ReadRecord">
//						<Arg name="SqlCmd" value="QryCrpAgr"/>
//					</Exec>
//		SQL:SELECT TActNo,BBusTyp,CrpCod FROM savcrpagr where CAgtNo='%s' and SvrSts='1'
//					<If condition="~RetCod!=0">
//						<Set>MsgTyp=E</Set>
//		          <Set>RspCod=GAS999</Set>
//		        <Set>RspMsg=单位协议不存在</Set>
//		        <Return/>
//		      </If>   
//		String comNo = context.getData(ParamKeys.COMPANY_NO).toString();
//		context.setData(ParamKeys.USE_STS, "0");
//		
//		get(QryThdBaseInfo.class).qryThdbaseinfo(context);
		
//		EupsThdBaseInfo etbi = new EupsThdBaseInfo();
//		etbi = BeanUtils.toObject(context.getDataMap(), EupsThdBaseInfo.class);
//		
//		List<EupsThdBaseInfo> etbiList = get(EupsThdBaseInfoRepository.class).find(etbi);
//		if(CollectionUtils.isEmpty(etbiList) && etbiList==null ){
//			context.setData(ParamKeys.MESSAGE_TYPE, "E");
//			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
//			context.setData(ParamKeys.RSP_MSG, "单位协议不存在");
//		}
//		
//		
	
		//TODO TActNo 第三方银行账号 不确定是流水表中的哪个字段
//		       <Set>TActNo=491800012620190029499</Set>
		
		//查协议表
//		         <Exec func="PUB:ReadRecord">
//		            <Arg name="SqlCmd" value="Existcusall"/>
//		          </Exec>
//		SQL:（客户号cusNo客户账号cusAc) select count(*) cnt1 from Gascusall491 where UserNo='%s' and ActNo='%s'
		
		
//		eca.setCusNo(cusNo);
//		eca.setCusAc(cusAc);
		
//		logger.info("++++++++++++++++++++++++++++++++++++++++++++++");
//		logger.info(context.getData(ParamKeys.CUS_AC));
//		logger.info(context.getData(ParamKeys.CUS_NO));
//		eca = BeanUtils.toObject(context.getDataMap(), EupsCusAgent.class);
//
//		ecaList = new ArrayList<EupsCusAgent>();
//		try{
//			ecaList = get(EupsCusAgentRepository.class).find(eca);
//			
//		}catch(Exception e ){
//			context.setData(ParamKeys.MESSAGE_TYPE, "E");
//			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
//			context.setData(ParamKeys.RSP_MSG, "用户编号查询失败!");
//		}
//		if(ecaList == null && CollectionUtils.isEmpty(ecaList)){
//			context.setData(ParamKeys.MESSAGE_TYPE, "E");
//			context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
//			context.setData(ParamKeys.RSP_MSG, "该用户编号未与该卡号绑定签约!");
//		}
//		
//		Map<String, Object> ecaListMap = BeanUtils.toMap(ecaList);
//		context.setDataMap(ecaListMap);
//		          <If condition="~RetCod!=0">
//		          	<Set>MsgTyp=E</Set>
//		          <Set>RspCod=GAS999</Set>
//		            <Set>RspMsg=用户编号查询失败!</Set>
//		            <Return/>
//		          </If>
//		          
//		          <If condition="STRCMP($cnt1,0)=0">
//		          	<Set>MsgTyp=E</Set>
//		            <Set>RspCod=GAS999</Set>
//		            <Set>RspMsg=该用户编号未与该卡号绑定签约!</Set>
//		            <Return/>
//		          </If>  
//
//		       
//		      
			// TODO 校验各分行账号
//		      <Set>AcJudFunc=AcJud_441999</Set>    <!--用广州的行不行呢-->
//		      <Call function="$AcJudFunc">
//		        <Input name="ActNo|NewFlg|"/>
//		        <Output name="OActNo|ActCls|"/>
//		      </Call>
//		      <If condition="$MsgTyp=E">
//		            <Return/>
//		       </If>
//
//		      <Switch expression="$ActCls">
//		        <Case value="2" /> <!-- 对私 -->
//		        <Case value="3" />
//		        <Case value="5" >
//		         <If condition="INTCMP($ActCls,3,3)">   <!--$ActCls = 3 -->
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
//		            <Return/>
//		        </Default>
//		      </Switch>
//		      <If condition="OR(INTCMP($ActTyp,3,0),INTCMP($ActTyp,3,2),INTCMP($ActTyp,3,4))"><!--对私-->
//		        <Set>HTxnCd=471140</Set>
//		        <Set>PayMod=1</Set>
//		        <Set>ActFlg=$ActTyp</Set>
//		        <Set>VchChk=1</Set><!--监督标志由业务上确定-->
//		      </If>
//		      <ElseIf condition="INTCMP($ActTyp,3,1)"><!--对公-->
//		        <Set>HTxnCd=451610</Set>
//		        <Set>ActNod=SUBSTR($TActNo,1,6)</Set>
//		        <Set>ActSqn=SUBSTR($TActNo,14,5)</Set>
//		        <Delete>TActNo</Delete>
//		        <Set>BusTyp=CRP51</Set>
//		        <Set>BusSub=01</Set>   <!--应用子码，对应业务类型CRP51-->
//		        <Set>ActFlg=2</Set>
//		        <Set>Smr=代扣燃气费</Set>
//		      </ElseIf>
//		      <Else> 
//		           <Return/>
//		      </Else>
		
		
		 
//		      <Exec func="PUB:ReadModuleCfg">
//		        <Arg name="Application" value="GAS_DB"/><!--可以使用单位编号-->
//		        <Arg name="Transaction" value="460710"/><!--可以用交易码或者模块名-->
//		      </Exec>
		
		
		
//		      <Set>TxnAmt=ADDCHAR(MUL(100,$PayAmt),12,0,1)</Set> <!--应缴费用*100，左边补0共12位--> 
//		      <Set>TCusNm=$ActNam</Set>
//		      <Set>PayMod=0</Set>
//		      <Set>CnlTyp=L</Set><!--交易渠道类型：L第三方系统-->
//		      <Set>VchChk=1</Set><!--监督标志由业务上确定-->
//		      <Set>VchCod=00000000</Set>
		
//			context.setData(ParamKeys.TXN_AMOUNT, reqTxnAmt.multiply(multiplicand, mc));
//			context.setData(ParamKeys.CUS_NME, context.getData(arg0));
//			context.setData(ParamKeys.PAY_CHANNEL, GDConstants.GAS_PAY_CHL_0);	// <Set>PayMod=0</Set>
//			context.setData(ParamKeys.CHL_TYP, "L");	//TODO <Set>CnlTyp=L</Set> 疑问：CHL_TYP的取值（渠道类型:参考531数据标准）
			//context.setData(arg0, arg1);	//TODO <Set>VchChk=1</Set><!--监督标志由业务上确定--> 流水表中有几个标志，不确定监督标志用哪个
			//context.setData(arg0, arg1);	//TODO <Set>VchCod=00000000</Set> 监督码？
			
			
//		      <Set>MstChk=1</Set>  
//			context.setData(GDParamKeys.MST_CHK, "1");
//		      <Set>FRspCd= </Set>  
			
//		      <Set>ItgTyp=0</Set>  <!-- 完整性类型 -->
//			context.setData(ParamKeys.ITG_TYP, "0");
//		      <Set>TxnTyp=N</Set> 	<!-- 交易类型 -->
//			context.setData(ParamKeys.TXN_TYP, GDConstants.GAS_TXN_TYP_N);
//		      <Set>TlrId=ERQTDT1</Set>
//			context.setData(ParamKeys.TELLER_ID, GDConstants.GAS_TLR_ID);
//		      <Set>NodNo=491800</Set> <!--br -->
//			context.setData(ParamKeys.BR, GDConstants.GAS_BR);
//		      <Set>CcyTyp=0</Set>
//			context.setData(ParamKeys.CCY, "0");
//		      <Set>TTxnCd=460710</Set>
//			context.setData(ParamKeys.THD_TXN_CDE, GDConstants.GAS_SMPC_TXN_CDE);
			
			
	/**		
			
			//TEST 		TEST 		TEST 		TEST 		TEST 		TEST
			context.setData(ParamKeys.RSP_CDE, "000000");
			
			if("000000".equals(context.getData(ParamKeys.RSP_CDE))){
				
//		 	<Set>OptAmt=$PayAmt</Set>
				context.setData(ParamKeys.TXN_AMOUNT, context.getData(ParamKeys.REQUEST_TXN_AMOUNT));
		// TODO <Set>OptAmt1=ADDCHAR(MUL(100,$PayAmt),12,0,1)</Set>

		//   <Set>ThdSts=B0</Set>
				context.setData(ParamKeys.BAK_FLD4, GDConstants.GAS_THDSTS_TYP_B0);
		//   <Set>Status=1</Set>
				context.setData(ParamKeys.BAK_FLD5, "1");
		//   <Set>HTxnSt=S</Set>
				context.setData(ParamKeys.MFM_TXN_STS, GDConstants.MFM_TXN_STS_S);
		//   <Set>HRspCd=SC0000</Set>
				context.setData(ParamKeys.MFM_RSP_CDE, GDConstants.GAS_MFM_RSP_CD);
		//   <Set>RspMsg=扣款成功</Set>
				context.setData(ParamKeys.RSP_MSG, GDConstants.PAY_SUCC);
		//   <Set>ErrMsg=扣款成功</Set>
				context.setData(ParamKeys.BAK_FLD2, GDConstants.PAY_SUCC);







//		       <Arg name="SqlCmd"   value="SucGastxnjnl491"/>	<!-- 更新成功流水 -->
//		     </Exec>
		//SQL:
		//update Gastxnjnl491 set
//			TxnTyp='%s',ActNam='%s',OptAmt='%s',OptAmt1='%s',ThdSts='%s',Status='%s',HTxnCd='%s',
//			HRspCd='%s',HTxnSt='%s',HLogNo='%s',TckNo='%s',ErrMsg='%s' 
		//where LogNo='%s' 
			
//			etj.setTxnTyp(context.getData(ParamKeys.TXN_TYP).toString());
//			etj.setCusNme(context.getData(ParamKeys.CUS_NME).toString());
//			etj.setTxnAmt((BigDecimal)context.getData(ParamKeys.TXN_AMOUNT));
//			etj.setBakFld3(context.getData(ParamKeys.BAK_FLD3).toString());
//			etj.setBakFld4(context.getData(ParamKeys.BAK_FLD4).toString());
//			etj.setMfmTxnCde(context.getData(GDParamKeys.MFM_TXN_CDE).toString());
//			etj.setMfmRspCde(context.getData(ParamKeys.MFM_RSP_CDE).toString());
//			etj.setMfmTxnSts(context.getData(ParamKeys.MFM_TXN_STS).toString());
			//TODO 主机流水号HLogNo 
		//	
//			etj.setMfmVchNo(context.getData(ParamKeys.MFM_VCH_NO).toString());
//			etj.setBakFld2(context.getData(ParamKeys.BAK_FLD2).toString());

				 etj = BeanUtils.toObject(context.getDataMap(), EupsTransJournal.class);
				logger.info("============================================");
				get(EupsTransJournalRepository.class).update(etj);

				/*context.setData(ParamKeys.MESSAGE_TYPE, "E");
				context.setData(ParamKeys.RSP_CDE, GDConstants.GAS_ERROR_CODE);
				context.setData(ParamKeys.RSP_MSG, "数据库处理失败!");
			}
		//   <If condition="~RetCod=-1">
//		     <Exec func="PUB:RollbackWork" error="IGNORE" />
//		     <Set>MsgTyp=E</Set>
//		     <Set>RspCod=GAS999</Set>
//		     <Set>RspMsg=数据库处理失败!</Set>
//		     <Return/>
		//   </If>
		//  </If>
			
//			}else{
//				context.setData(ParamKeys.RSP_MSG, "扣款失败");
//				throw new CoreRuntimeException("扣款失败");
//			}
			
		// <Else>
//		 	<Set>RspMsg=扣款失败</Set>
//		 	 </Else>
		// 
		//</FlowCtrl>
		//</Transaction>
	
	
	*/
//			context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);	
			}
		}

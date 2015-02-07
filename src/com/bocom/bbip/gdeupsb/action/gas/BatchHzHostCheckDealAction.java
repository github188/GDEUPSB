package com.bocom.bbip.gdeupsb.action.gas;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsCusAgent;
import com.bocom.bbip.eups.repository.EupsCusAgentRepository;
import com.bocom.bbip.gdeupsb.common.GDErrorCodes;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;


/**
 * 惠州燃气大小通道批量主机记帐处理
 * @author TD08
 *
 */
public class BatchHzHostCheckDealAction extends BaseAction{
	
	private static final Log logger=LogFactory.getLog(BatchHzHostCheckDealAction.class);
	
	
	public void execute (Context context) throws CoreException, CoreRuntimeException{
		logger.info("Enter in BatchHzHostCheckDealAction!.....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		
		//初始化数据
		Date txnTme = DateUtils.parse(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss));
		Date txnDte = DateUtils.parse(DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		context.setData(ParamKeys.TXN_TIME, txnTme);
		context.setData(ParamKeys.TXN_TIME, txnDte);
   
	    BigDecimal txnAmt = new BigDecimal(0.0);
	    BigDecimal txnAmt1 = new BigDecimal(0.0);
	    context.setData(ParamKeys.TXN_AMOUNT, txnAmt);
	    context.setData(ParamKeys.BAK_FLD1, String.valueOf(txnAmt1));
	      
	    context.setData(ParamKeys.BAK_FLD4, "B3");
	    context.setData(ParamKeys.RSP_MSG, "扣款失败");
	    context.setData(GDParamKeys.ERR_MSG, "扣款失败");
	      
		//查询用户是否签约  select ActNam from Gascusall491 where UserNo='%s' and ActNo='%s'
	    EupsCusAgent eupsCusAgent = new EupsCusAgent();
	    eupsCusAgent = BeanUtils.toObject(context.getDataMap(), EupsCusAgent.class);
	    List<EupsCusAgent> ecaList = get(EupsCusAgentRepository.class).find(eupsCusAgent);
	    if(CollectionUtils.isEmpty(ecaList) || null==ecaList){		//未签约
	    	throw new CoreRuntimeException(GDErrorCodes.EUPS_CUSNAME_NEVR_SIGN);
	    }
	    
	    //TODO 根据流水表中的批次号，同一批次序号查询缴费年月份 select PayAmt from gastxnjnl491 where DskNo='%s' and PNo='%s'
	    //TODO update Gastxnjnl491 set ActNam='%s',OptDat='%s',TxnTyp='PL',OptTim='%s',OptAmt='%s',OptAmt1='%s',ThdSts='%s',Status='%s',ErrMsg='%s' where DskNo='%s' AND PNo='%s' 
	    /*
	     * 先根据第三方输入的燃气批量文件名获取批量文件的日期，
	    */
	    
	    
	    //TODO 校验各分行账号
	    //TODO 校验对公对私
	    
	    //上主机
	    context.setData(ParamKeys.TXN_TYP, "N");
	    context.setData(ParamKeys.ITG_TYP, "0");
//	      <Set>TActNo=$CActNo</Set>
//	      <Set>CnlTyp=L</Set> 
//	      <Set>TxnTyp=N</Set> <!--normal-->
//	      <Set>ItgTyp=0</Set> <!-- 完整性类型-->
//	      <Set>FRspCd=000000</Set>
//	      <Set>MstChk=1</Set>
//	     <Set>TTxnCd=460716</Set>
//	      <Exec func="PUB:CallHostOther">
//	        <Arg name="HTxnCd" value="$HTxnCd"/>
//	        <Arg name="ObjSvr" value="SHSTPUB1"/>
//	      </Exec>
//	      <If condition="~RetCod=0">  <!--recsts 0:初始化 1:成功 2:失败 -->
//	      	<Set>RspMsg=扣款成功</Set>
//	      	<Set>Status=1</Set>
//	      	<Set>ErrMsg=扣款成功</Set>
//	      	<Set>ThdSts=B0</Set>
//	        <Set>HTxnSt=S</Set>
//	        <Set>HRspCd=SC0000</Set>
//	        <Set>OptAmt=$PayAmt</Set>
//	        <Set>OptAmt1=$TxnAmt</Set>
//	        <Exec func="PUB:ExecSql">  <!-- 执行非查询动态SQL语句 -->
//	            <Arg name="SqlCmd" value="sucGastxnjnl491"/> <!-- 更新成功流水 -->
//	      </Exec>
//	        <Set>RecSts=1</Set>
//	        <Exec func="PUB:ExecSql">
//	          <Arg name="SqlCmd" value="UpdRecSts"/>
//	        </Exec>
//	        <Set>MsgTyp=N</Set>
//	        <Exec func="PUB:PutResponse"/>
//	        <Return/>
//	      </If>
//	      <ElseIf condition="~RetCod=3">  <!--recsts 0:初始化 1:成功 2:失败 -->
//	        <Set>RecSts=2</Set>
//	        <Exec func="PUB:ExecSql">
//	          <Arg name="SqlCmd" value="UpdRecSts"/>
//	        </Exec>
//	        <Set>MsgTyp=N</Set>
//	        <Exec func="PUB:PutResponse"/>
//	        <Return/>
//	      </ElseIf>	    
		
		
	}
	
	
}

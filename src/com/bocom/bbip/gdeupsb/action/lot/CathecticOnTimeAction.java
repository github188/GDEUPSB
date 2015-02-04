package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.CommThdRspCdeAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotCusInf;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GdLotCusInfRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;

/**
 * 福彩实时投注
 * Date 2015-01-23
 * @author GuiLin.Li
 * @version 1.0.0
 */
public class CathecticOnTimeAction extends BaseAction{

    @Override
    public void execute(Context context) throws CoreException {
        log.info("Enter in CathecticOnTimeAction... ");
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
        
        //TODO;该方法含义
        //<Call package="LOT_PKG" function="GetSysCfg" desc="获取系统配置"/>
        //  <Set>CTTxnCd=$TTxnCd</Set>
        //TODO;
        context.setData("cTTxnCd", context.getData("tTxnCd"));
        context.setData("tzCod", "TZ0000");
        
        //检查用户状态
        GdLotCusInf lotCusInf = new GdLotCusInf();
        lotCusInf.setStatus("1");
        lotCusInf.setCrdNo(context.getData("crdNo").toString());
        List<GdLotCusInf> lotCusInfs = get(GdLotCusInfRepository.class).find(lotCusInf);
        if (CollectionUtils.isEmpty(lotCusInfs)) {
           context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
           context.setData(ParamKeys.RSP_CDE,"LOT999");
           context.setData(ParamKeys.RSP_MSG,"客户未注册或状态异常");
           context.setData("tzCod", "TZ9001");
           return;
        }
        //TODO;
        //<!-- 检查当前是否有可用奖期，如果没有则下载一次，下载后再检查一次，如果没有则返回错误 -->
        //<Call package="LOT_PKG" function="GetFcTim" desc="获取福彩时间"/>
        
        //<Exec func="PUB:CodeSwitching">
        Map<String, Object> map = new HashMap<String, Object>();
       // map.put("isKeno", context.getData("isKeno"));
        //map.put("gameId", context.getData("gameId"));
        //map.put("lotTime", context.getData("lotTime"));
        map.put("isKeno", "Y");
        map.put("gameId", "5");
        map.put("lotTime", "20150111223344");
        
        GdLotDrwTbl lotDrwInf =new GdLotDrwTbl();
        try {
        lotDrwInf= get(GdLotDrwTblRepository.class).qryLotDrwInf(map);
        } catch(Exception e) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"数据库异常！！");
            context.setData("tzCod", "TZ9002");
            return;
        }
        
        if (null == lotDrwInf) {
            // 没有奖期信息，执行奖期下载 -- 
            try {
                context = get(BBIPPublicService.class).synExecute("485441", context); //TODO; 奖期下载 
            } catch (Exception e){
                log.info("下载奖期信息异常!!!");
                context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                context.setData(ParamKeys.RSP_CDE,"LOT999");
                context.setData(ParamKeys.RSP_MSG,"下载奖期信息异常");
                context.setData("tzCod", "TZ9002");
                return;
            }
            //下载奖期信息成功后，再次检查是否有符合的奖期
            GdLotDrwTbl lotDrwInfTwice = get(GdLotDrwTblRepository.class).qryLotDrwInf(map);
            if (null ==lotDrwInfTwice) {
                log.info("下载奖期信息异常!!!");
                context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
                context.setData(ParamKeys.RSP_CDE,"LOT999");
                context.setData(ParamKeys.RSP_MSG,"不在购买时间（无可用奖期）");
                context.setData("tzCod", "TZ9003");
                return;
               
            }
        }
        //TODO; PUB:CodeSwitching
        /* <!-- 登记购彩记录(commit) -->
        <Exec func="PUB:CodeSwitching">
            <Arg name="DatSrc" value="OPFCSW"/>
            <Arg name="SourNam" value="SchTyp"/>
            <Arg name="DestNam" value="SchTypDesc"/>
            <Arg name="TblNam" value="SchTypDescChg"/>
        </Exec>
        syso
        <Exec func="PUB:CodeSwitching">
            <Arg name="DatSrc" value="OPFCSW"/>
            <Arg name="SourNam" value="GameId"/>
            <Arg name="DestNam" value="GameIdDesc"/>
            <Arg name="TblNam" value="GameIdDescChg"/>
        </Exec>*/
        
        getTxnLogNo();
        context.setData("txnAmt",context.getData("betAmt"));
        context.setData("tTxnCd",context.getData("231"));
        context.setData("txnCod",context.getData("485412"));
        context.setData("schTit",context.getData("schTypDesc")); //codeswitching
        context.setData("gamNam",context.getData("gameIdDesc"));//codeswitching
        Date date =new Date();
        String dateString = DateUtils.format(date, DateUtils.STYLE_yyyyMMdd);
        String dateTimeString = DateUtils.format(date, DateUtils.STYLE_yyyyMMddHHmmss);
        context.setData("betDat",dateString);
        context.setData("txnTim",dateTimeString);
        context.setData("txnLog",context.getData("selVal"));
        context.setData("schId","");
        context.setData("tLogNo","");
        context.setData("cipher","");
        context.setData("verify","");
        context.setData("hTxnCd","471140");
        context.setData("hTxnSb","002");
        context.setData("hLogNo","");
        context.setData("hResCd","");
        context.setData("hTxnSt","U");
        context.setData("tRspCd","");
        context.setData("tTxnSt","U");
        context.setData("thdChk","0");
        context.setData("tChkNo","00000000000");
        context.setData("chkTim","");
        context.setData("chkFlg","0");
        context.setData("awdFlg","0");
        context.setData("awdRtn","0");
        context.setData("cAgtNo",context.getData("dSCAgtNo"));//TODO; 商户协议编号 
        context.setData("tckNo","0");
        context.setData("lChkTm","");
        context.setData("txnSts","U");
        GdLotTxnJnl lotTxnJnl= BeanUtils.toObject(context.getDataMap(), GdLotTxnJnl.class);
        try {
            get(GdLotTxnJnlRepository.class).insert(lotTxnJnl);
        }catch (Exception e){
            log.info("新增购彩流水异常!!!");
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"新增购彩流水异常");
            context.setData("tzCod", "TZ9005");
            return;
        }
        //TODO;
        //  <!-- 防止后续账务处理异常导致回滚，此处先提交数据库事务 -->
        // <Exec  func="PUB:CommitWork"  />
        context.setData("tTxnCd", context.getData("cTTxnCd"));
        //QryAgtInf"协议表查询 
        //TODO;
       /* <If condition="IS_NOEQUAL_STRING(~RetCod,0)">
            <Set>MsgTyp=E</Set>
            <Set>RspCod=LOT999</Set>
            <Set>RspMsg=送主机记账失败（无法获取代收协议信息）</Set>
            <Set>TzCod=TZ9006</Set>
            <Return/>
        </If>*/
        
        context.setData("BusTyp","CBS52");
        context.setData("cnlTyp","L");
        context.setData("actFlg","4");
        context.setData("mask","9145");
        context.setData("actNo",context.getData("crdNo"));
        context.setData("vchTyp","000");
        context.setData("vchCod","00000000");
        context.setData("payMod","0");
        context.setData("ccyCod","CNY");
        context.setData("ccyTyp","1");
        context.setData("vchChk","0");
        context.setData("actSeq",context.getData("crdNo").toString().substring(13,18));
        context.setData("txnAmt",context.getData("betAmt"));
        context.setData("smr","彩票投注");
        context.setData("cAgtNo",context.getData("dSCAgtNo"));
        context.setData("","");
       
        
        //<!--取前置流水号-->
        String sqn = get(BBIPPublicService.class).getBBIPSequence();
        if (StringUtils.isEmpty(sqn)) {
            log.info("送主机记账失败（无法获取前置流水号）!!!");
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"送主机记账失败（无法获取前置流水号）");
            context.setData("tzCod", "TZ9007");
            return;
        }
        context.setData("accStatus", "N");
       
        //TODO; 上主机
        /*<Exec func="PUB:CallHostOther" error="IGNORE">
        <Arg name="HTxnCd" value="471140" desc="主机交易码"/>
        <Arg name="ObjSvr" value="SHSTPUB1" desc="服务"/>
        </Exec>
         <Set>TxnSts=$TxnSts</Set><!-- 交易状态 -->
         */
        String accStatus = context.getData("accStatus").toString();
        /*
         <Switch expression="~RetCod">
                <Case value="-10" desc="发送不成功"/>
                <Case value="-2" desc="系统错误， 可以归为发送不成功">
                    <Set>AccStatus=F</Set>
                    <Set>AccMsg=送主机记账失败（系统错误)</Set>
                    <Set>TxnSts=F</Set>
                    <Set>TzCod=TZ9008</Set>
                    <Break/>
                </Case>
                <Case value="3" desc="交易失败">
                    <Set>AccStatus=F</Set>
                    <Set>AccMsg=送主机记账失败（交易失败)</Set>
                    <Switch expression="$HRspCd">
                       <Case value="PD5100">
                         <Set>AccMsg=卡可用余额不足</Set>
                         <Break/>
                       </Case>
                    </Switch>
                    <Set>TxnSts=F</Set>
                    <Set>TzCod=TZ9009</Set>
                    <Break/>
                </Case>
                <Case value="-1" desc="超时">
                    <Set>AccStatus=T</Set>
                    <Set>AccMsg=送主机记账超时</Set>
                    <Set>TxnSts=T</Set>
                    <Set>TzCod=TZ9010</Set>
                    <Break/>
                </Case>
                <Case value="0">
                    <Set>AccStatus=S</Set>
                    <Set>AccMsg=送主机记账成功</Set>
                    <Set>TxnSts=A</Set>
                    <Set>TzCod=TZ9011</Set>
                    <Break/>
                </Case>
                <Default>
                    <Set>AccStatus=F</Set>
                    <Set>AccMsg=送主机记账失败（未知错误）</Set>
                    <Set>TxnSts=F</Set>
                    <Set>TzCod=TZ9012</Set>
                    <Break/>
                </Default>
            </Switch>
         */
        //   更新扣账结果(commit)
        context.setData("hTxnSt",accStatus);
        GdLotTxnJnl lotTxnJnlInput = BeanUtils.toObject(context.getDataMap(), GdLotTxnJnl.class);
        try {
            get(GdLotTxnJnlRepository.class).update(lotTxnJnlInput);
        } catch (Exception e) {
            log.info("更新购彩流水异常!!!（账务结果）");
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"更新购彩流水异常!!!");
            context.setData("tzCod", "TZ9013");
            return;
        }
        //TODO;
        //  <!-- 送第三方报文之前先提交数据库事务 -->
        //<Exec  func="PUB:CommitWork"  />
        context.setData("tTxnCd", "231");
        if (!accStatus.equals("S")) {
            log.info("账务处理未成功");
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,context.getData("accMsg"));
            return;
        }
        String lotTxnTim = DateUtils.format(date, DateUtils.STYLE_yyyyMMddHHmmss);
        context.setData("lotTxnTim", lotTxnTim);
        // 调第三方
        Map<String, Object> resultMap= get(ThirdPartyAdaptor.class).trade(context);
        String sndStatus = "F";
        if(context.getState().equals(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL)){
            CommThdRspCdeAction cRspCdeAction = new CommThdRspCdeAction();
            String responseCode = cRspCdeAction.getThdRspCde(resultMap,  context.getData(ParamKeys.EUPS_BUSS_TYPE).toString());
            log.info("responseCode:["+responseCode+"]");
            if(Constants.RESPONSE_CODE_SUCC.equals(responseCode)){
                log.info("QueryLot success!");
                sndStatus = "S";
                context.setData("sndMsg", "发送购彩成功");
                context.setData("txnSts", "S");
                context.setData("tTxnSt", "S");
            }/*else if (){
                responseCode = ErrorCodes.EUPS_THD_RSP_CODE_ERROR;
                sndStatus = "T";
                context.setData("sndMsg", "发送购彩超时");
                context.setData("txnSts", "T");
                context.setData("tTxnSt", "T");
                context.setData("tzCod", "TZ9014");
                throw new CoreException(responseCode);
            }*/else {
                sndStatus = "F";
                context.setData("sndMsg", resultMap.get("rRspMsg")+"["+responseCode+"]");
                if (responseCode.equals("1618")){
                    context.setData("sndMsg", "快乐十分该期结束，请稍后购买");
                }
                context.setData("txnSts", "F");
                context.setData("tTxnSt", "F");
                context.setData("tzCod", "TZ9015");
            }
        }else{
            log.error("QueryLot return has error!");
            throw new CoreException(ErrorCodes.EUPS_THD_SYS_ERROR);
        }
       
        if(sndStatus.equals("F")&& null != context.getData("tckNo").toString()){
            context.setData("tzCod", "TZ9017");
            context.setData("tTxnCd", context.getData("cTTxnCd"));
            context.setData("hTxnCd", "471149");
            context.setData("oHLogNo", context.getData("hLogNo"));
            context.setData("oTckNo", context.getData("tckNo"));
            context.setData("tIATyp", "C");
            context.setData("oTTxnCd", context.getData("cTTxnCd"));
            context.setData("hLogNo", "");
            //TODO;PUB:CallHostOther
            /*<Exec func="PUB:CallHostOther"><!--上主机系统抹账 -->
                    <Arg name="HTxnCd" value="959999" /><!--主机交易码 -->
                    <Arg name="ObjSvr" value="SHSTPUB1" />
                </Exec>
                <If condition="IS_NOEQUAL_STRING(~RetCod,0)">
                    <Set>AccStatus=$AccStatus</Set><!-- 如果冲正失败，则主机状态不变（S） -->
                </If>
                <Else>
                    <Set>AccStatus=F</Set><!-- 如果冲正成功，主机状态为失败（F） -->
                </Else> */
        }
        context.setData("rRspCd", context.getData("rRspCod"));
        context.setData("accStatus", accStatus);
        
        GdLotTxnJnl inputLotTxnJnl = BeanUtils.toObject(context.getDataMap(), GdLotTxnJnl.class);
        try {
            get(GdLotTxnJnlRepository.class).update(inputLotTxnJnl);
        } catch (Exception e) {
            log.info("更新购彩流水状态为"+context.getData("txnSts").toString()+"$TxnSts,时失败");
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"更新购彩流水状态为"+context.getData("txnSts").toString()+"$TxnSts,时失败");
            context.setData("tzCod", "TZ9016");
            return;
        }
        String txnStatus = context.getData("txnSts").toString();
        if (txnStatus.equals("S")) {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_SUCC);
            context.setData(ParamKeys.RSP_CDE,Constants.RESPONSE_CODE_SUCC);
            context.setData(ParamKeys.RSP_MSG,Constants.RESPONSE_MSG);
           
        }else {
            context.setData("MsgTyp",Constants.RESPONSE_TYPE_FAIL);
            context.setData(ParamKeys.RSP_CDE,"LOT999");
            context.setData(ParamKeys.RSP_MSG,"购彩失败");
        }
        context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
    }

    /**
     * 
     * 
     * 获取系统配置
     */
    private void GetSysCfg() {
        

    }
    /**
     * 获取彩票时间
     */
    private void getLotTime() {
      /*  <Function name="GetFcTim" desc="根据与福彩之间的时差获取福彩时间">
        <Input>DealId|NodNo|BrNo|</Input>
        <Output>FcTim|</Output>
        <DynSentence name="GetSysCfg" desc="获取系统配置">
           <Sentence>
               select DealId,UsrPam,UsrPas,SigTim,LclTim,LotTim,DiffTm
               from LotSysCfg
               where DealId='%s'
           </Sentence>
           <Fields>DealId|</Fields>
        </DynSentence>
        <Process>
           <!-- 查询系统参数，获取当前本地与福彩系统的时差 -->
           <Exec func="PUB:ReadRecord" error="IGNORE">
              <Arg name="SqlCmd" value="GetSysCfg"/>
           </Exec>
           <If condition="IS_NOEQUAL_STRING(~RetCod,0)">
               <Set>DiffTm=0</Set><!-- 如果无法获取时差，则默认时差为0 -->
           </If>
           
           <!-- 根据时差计算当前福彩系统时间 -->
           <Set>CurTim=GETDATETIME(YYYYMMDDHHMISS)</Set>
           
           <!-- 技术难题待解决，暂时默认时间差为0 -->
           <Set>SrcDate=$CurTim</Set>
           <Set>DifTim=$DiffTm</Set>
           <Call package="LOT_PKG" function="CalTim" desc="根据时差计算时间"/>
           <Set>FcTim=$DesDate</Set>*/
    }
    
    /**
     * --计算时间
     */
    private void calTime(){
    /*  
           NodNo:网点号
           BrNo:分行号
           SrcDate:原时间
           DifTim:时差（秒）
        OUTPUT:
           DesDate:计算后的时间
        -->
        <Function name="CalTim" desc="计算时间">
           <Input>NodNo|BrNo|SrcDate|DifTim|</Input>
           <Output>DesDate|</Output>
           <Process>
               <!-- 检查分行号是否存在 -->
               <If condition="IS_EQUAL_STRING($BrNo,)">
                  <Exec func="PUB:GetBranchNoByNodeNo" error="IGNORE"></Exec>
                  <If condition="IS_EQUAL_STRING(~RetCod,)">
                      <Set>BrNo=441999</Set>
                  </If>
               </If>
               
               <!-- 获取调用序号 
               <Exec func="PUB:nGetPubSeqNo" desc="获得$SelVal">
                  <Arg name="SeqNam" value="LOT:CALLID"/>
                  <Arg name="Len" value="9"/>
                  <Arg name="CycCnd" value="D"/>
               </Exec>
               -->
               <Set>SelVal=000000001</Set>
               
               <!-- 调用外部命令获取结果 -->
               <Set>ResultFile=STRCAT(/app/ics/dat/lot/,TIMEc,$SelVal,.dat)</Set>
               <System command="java -cp /app/ics/app/lot/bin TimeCalTool " error="IGNORE">
                  <Arg name="funcTyp" value="c"/>
                  <Arg name="callId" value="$SelVal"/>
                  <Arg name="resultPath" value="/app/ics/dat/lot"/>
                  <Arg name="date1Fmt" value="yyyyMMddHHmmss"/>
                  <Arg name="date1" value="$SrcDate"/>
                  <Arg name="date2Fmt" value="yyyyMMddHHmmss"/>
                  <Arg name="DifTim" value="$DifTim"/>
               </System> 
               
               <!-- 读取结果 -->
               <Exec func="PUB:OpenFile">
                  <Arg name="FileName" value="$ResultFile"/>
                  <Arg name="Mode" value="r"/>
               </Exec>
               <Exec func="PUB:ReadFile">
                  <Arg name="FieldName" value="CallResult"/>
                  <Arg name="ReadLen" value="20"/>
               </Exec>
               <Exec func="PUB:CloseFile">
               </Exec>
               
               <!-- 返回结果 -->
               <Set>DesDate=DELBOTHSPACE($CallResult)</Set>

           </Process>
        </Function>*/
    }
    /**
     * 获取购彩流水号
     */
    private void getTxnLogNo() {
        
       /* <Function name="GetTxnLogNo" desc="获取购彩流水号">
        <Input>DealId|</Input>
        <Output>SelVal|</Output>
        <DynSentence name="CndSts" desc="条件">
           <Sentence>
               DealId='%s'
           </Sentence>
           <Fields>DealId|</Fields>
        </DynSentence>
        <Process>
           <Exec func="PUB:GetSeqNoCircle" desc="获得$SelVal">
               <Arg name="TblNam" value="LotSysCfg"/>
               <Arg name="SeqCol" value="LOGSEQ"/>
               <Arg name="Len" value="9"/>
               <Arg name="CndSts" value="CndSts"/>
               <Arg name="ColNam" value="SelVal"/>
           </Exec>
           <Set>SelVal=STRCAT($DealId,$SelVal)</Set>
        </Process>
    </Function>*/
    }
}

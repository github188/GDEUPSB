package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotChkCtl;
import com.bocom.bbip.gdeupsb.entity.GdLotChkDtl;
import com.bocom.bbip.gdeupsb.entity.GdLotPrzCtl;
import com.bocom.bbip.gdeupsb.entity.GdLotPrzDtl;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotSysCfg;
import com.bocom.bbip.gdeupsb.repository.GdLotChkCtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotPrzCtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotPrzDtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotSysCfgRepository;
import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.service.sqlmap.SqlMap;

/**
 *  提供福彩公共方法调用
 * @version 1.0.0
 * Date 2015-01-26
 * @author GuiLin.Li
 */
public class CommonLotAction extends BaseAction{
    
    @Autowired
    GdLotSysCfgRepository lotSysCfgRepository;
    @Autowired
    BGSPServiceAccessObject bgspServiceAccessObject;
    @Autowired
    GdLotDrwTblRepository lotDrwInfRepository;
    /**
     * 计算时差
     * INPUT:
        nodNo:网点号
        brNo:分行号
        lotTim:福彩时间
        lclTim:本地时间
     * @return  difTim:时差（秒）
     */
    public String difTime(String nodNo, String brNo,String lotTim, String lclTim) {
        
       /*
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
            <Set>ResultFile=STRCAT(/app/ics/dat/lot/,TIMEd,$SelVal,.dat)</Set>
            <System command="java -cp /app/ics/app/lot/bin TimeCalTool " error="IGNORE">
                <Arg name="funcTyp" value="d"/>
                <Arg name="callId" value="$SelVal"/>
                <Arg name="resultPath" value="/app/ics/dat/lot"/>
                <Arg name="date1Fmt" value="yyyyMMddHHmmss"/>
                <Arg name="date1" value="$Date1"/>
                <Arg name="date2Fmt" value="yyyyMMddHHmmss"/>
                <Arg name="date2" value="$Date2"/>
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
            <Set>DifTim=DELBOTHSPACE($CallResult)</Set>
        </Process>
          */
        return null;
    }
    /**
     * 下载不同的文件并入库
     * @param FileType 文件类型（1：游戏规则文件分类  2：中奖记录文件分类  3:对账文件分类）
     * @param gameId 游戏编号
     * @param drawId 期号
     * @return map 1.文件下载状态（0：成功  -1：失败） 2.文件下载结果描述
     */
    public Map<String, String> downloadFile(String type,String gameId,String drawId){
        Map<String, String>  map =new HashMap<String, String>();
        return map;
    }
  
     /**
     * 计算轧差
     * CalStat 轧差状态（0：成功  -1：失败）
     *  CalMsg:轧差结果描述
     */
    public void calcLotDifAmt(Context context) {
       
        /* 
    <Function name="CalcLotDifAmt" desc="计算轧差">
        <Output>CalStat|CalMsg|</Output>
        <DynSentence name="CalcLotDifAmt" desc="计算轧差">
            <Sentence>
                select GameId, DrawId, KenoId, TotAmt, PrzAmt
                from LotDrwTbl 
                where PrzAmt!='' and TotAmt!='' and ChkFlg='2' and FlwCtl='10' and KenoId in ('','AAAAA')
                    and (DifAmt='' or DifFlg='')
            </Sentence>
            <Fields></Fields>
        </DynSentence>
        <DynSentence name="UpdLotDifAmt" desc="更新轧差">
            <Sentence>
                update LotDrwTbl
                set DifAmt='%s', DifFlg='%s'
                where GameId='%s' and DrawId='%s' and KenoId='%s'
            </Sentence>
            <Fields>DifAmt|DifFlg|GameId|DrawId|KenoId|</Fields>
        </DynSentence>
        <DynSentence name="QryUnPrzDrw" desc="查询未汇总返奖的记录">
            <Sentence>
                select GameId, DrawId 
                from LotDrwTbl 
                where KenoId='AAAAA' and PrzAmt='' and FlwCtl!='10'
            </Sentence>
            <Fields></Fields>
        </DynSentence>
        <DynSentence name="StatUnPrzDrw" desc="统计Keno期未返奖的数量">
            <Sentence>
                select count(*) UnPrzCnt 
                from LotDrwTbl 
                where GameId='%s' and DrawId='%s' and PrzAmt='' and FlwCtl!='10' and KenoId != 'AAAAA'
            </Sentence>
            <Fields>GameId|DrawId|</Fields>
        </DynSentence>
        <DynSentence name="StatUnPrzDrw" desc="统计Keno期未返奖的数量">
            <Sentence>
                select count(*) UnPrzCnt 
                from LotDrwTbl 
                where GameId='%s' and DrawId='%s' and PrzAmt='' and FlwCtl!='10' and KenoId != 'AAAAA'
            </Sentence>
            <Fields>GameId|DrawId|</Fields>
        </DynSentence>
        <DynSentence name="SumPrzDrw" desc="汇总返奖金额">
            <Sentence>
                select COALESCE(sum(bigint(PrzAmt)),0) PrzSumAmt
                from LotDrwTbl 
                where GameId='%s' and DrawId='%s' and PrzAmt!='' and FlwCtl='10' and KenoId != 'AAAAA'
            </Sentence>
            <Fields>GameId|DrawId|</Fields>
        </DynSentence>
        <DynSentence name="UpdDrwPrzInf" desc="更新汇总返奖金额">
            <Sentence>
                update LotDrwTbl a
                set a.PrzAmt='%s', a.FlwCtl='10',
                    a.PayAmt = (select cast(sum(bigint(case when b.PayAmt = '' then '0' else b.PayAmt end)) as char(15)) from LotDrwTbl b where a.GameId = b.GameId and a.DrawId = b.DrawId and b.KenoId != 'AAAAA'),
                    a.PayFlg = ( select (case when exists(select 'Y' from LotDrwTbl c where c.GameId = a.GameId and c.DrawId = a.DrawId and c.KenoId != 'AAAAA' and c.PayFlg = '1') then '1' else '0' end) from LotDrwTbl d where d.GameId=a.GameId and d.DrawId=a.DrawId and d.KenoId='AAAAA')
                where a.GameId='%s' and a.DrawId='%s' and a.KenoId = 'AAAAA'
            </Sentence>
            <Fields>PrzSumAmt|GameId|DrawId|</Fields>
        </DynSentence>*/
        //处理Keno期记录，如果某期的所有Keno期都完成返奖，则更新改期的返奖总金额和返奖标志
        List <GdLotDrwTbl> lotDrwInfos = lotDrwInfRepository.qryUnPrzDrw();
        if(CollectionUtils.isEmpty(lotDrwInfos)) {
            context.setData("calStat", "-1");
            context.setData("calMsg", "查询返奖汇总信息失败!");
        }
        for (GdLotDrwTbl gdLotDrwTbl : lotDrwInfos) {
            context.setData("gameId", null);
            context.setData("drawId", null);
            Map <String, String> drwTblMap = lotDrwInfRepository.statUnPrzDrw(gdLotDrwTbl);
            if (drwTblMap.get("unPrzCnt")=="0") {
                //该期的所有Keno期都已经返奖完成，更新AAAAA记录的汇总信息 
                
            }
        }
/*                <If condition="IS_EQUAL_STRING($UnPrzCnt,0)">
                    <!-- 该期的所有Keno期都已经返奖完成，更新AAAAA记录的汇总信息 -->
                    <Exec func="PUB:ReadRecord" error="IGNORE">
                       <Arg name="SqlCmd" value="SumPrzDrw"/>
                    </Exec>
                    <Exec func="PUB:ExecSql" error="IGNORE">
                        <Arg name="SqlCmd"    value="UpdDrwPrzInf"/>
                    </Exec>
                    <If condition="IS_NOEQUAL_STRING(~RetCod,0)">
                        <Set>CalStat=-1</Set>
                        <Set>CalMsg=STRCAT(更新汇总返奖信息失败[,$GameId,-,$DrawId,])</Set>
                        <Return/>
                    </If>
                </If>
            </While>
            <Exec func="PUB:CloseCursor" error="IGNORE">
                <Arg name="CursorName" value="CURSOR_PRZ"/>
            </Exec>
        
            <!-- 查询是否有符合条件的未计算轧差的记录 -->
            <Exec func="PUB:OpenCursor" error="IGNORE">
                <Arg name="sql" value="CalcLotDifAmt"/>
                <Arg name="CursorName" value="CURSOR_CAL"/>
            </Exec>
            <If condition="AND(IS_NOEQUAL_STRING(~RetCod,0),IS_NOEQUAL_STRING(~RetCod,-2))">
                <Set>CalStat=-1</Set>
                <Set>CalMsg=统计轧差失败</Set>
                <Return/>
            </If>
            <While>
                <Delete>ROOT.GameId</Delete>
                <Delete>ROOT.DrawId</Delete>
                <Delete>ROOT.KenoId</Delete>
                <Delete>ROOT.TotAmt</Delete>
                <Delete>ROOT.PrzAmt</Delete>
                <Exec func="PUB:FetchCursor" error="IGNORE">
                    <Arg name="CursorName" value="CURSOR_CAL"/>
                </Exec>
                <If condition="~RetCod=100">
                    <Break/>
                </If>
                
                <If condition="INTCMP($TotAmt,5,$PrzAmt)">
                    <!-- 1借方（购彩总金额大于返奖总金额） -->
                    <Set>DifFlg=1</Set>
                    <Set>DifAmt=SUB($TotAmt,$PrzAmt)</Set>
                </If>
                <Else>
                    <!-- 0贷方（购彩总金额小于奖总金额） -->
                    <Set>DifFlg=0</Set>
                    <Set>DifAmt=SUB($PrzAmt,$TotAmt)</Set>
                </Else>
                
                <Exec func="PUB:ExecSql" error="IGNORE">
                    <Arg name="SqlCmd"    value="UpdLotDifAmt"/>
                </Exec>
                <If condition="IS_NOEQUAL_STRING(~RetCod,0)">
                    <Set>CalStat=-1</Set>
                    <Set>CalMsg=STRCAT(更新轧差失败[,$GameId,-,$DrawId,])</Set>
                    <Return/>
                </If>
                
            </While>
            <Exec func="PUB:CloseCursor" error="IGNORE">
                <Arg name="CursorName" value="CURSOR_CAL"/>
            </Exec>
        </Process>
    </Function> */
    }

	public void fileImport(Context context) throws CoreException {
		final String filetyp = context.getData(ParamKeys.FILE_TYPE);
		if ("2".equals(filetyp)) {
			/** 中奖文件入库 */
			fileImport2(context);
		}
		if ("3".equals(filetyp)) {
			/** 对账文件入库 */
			fileImport2(context);
		} else {
			throw new CoreException("");
		}

	}
	public void fileImport2(Context context)throws CoreException{
		/**-- 删除旧记录 */
		GdLotPrzCtl ctl=new GdLotPrzCtl();
		ctl.setGameId((String)context.getData("GameId"));
		ctl.setDrawId((String)context.getData("DrawId"));
		ctl.setKenoId((String)context.getData("KenoId"));
		get(GdLotPrzCtlRepository.class).delete(ctl);
		GdLotPrzDtl dtl=new GdLotPrzDtl();
		dtl.setGameId((String)context.getData("GameId"));
		dtl.setDrawId((String)context.getData("DrawId"));
		dtl.setKenoId((String)context.getData("KenoId"));
		get(GdLotPrzDtlRepository.class).delete(dtl);
		/**新增中奖控制信息*/
		List<GdLotPrzCtl>ctlList=(List<GdLotPrzCtl>) BeanUtils.toObjects(
				(List<Map<String,Object>>)context.getData("bonusItem"), GdLotPrzCtl.class);
		Assert.isNotEmpty(ctlList, ErrorCodes.EUPS_QUERY_NO_DATA);
		((SqlMap)get("sqlMap")).insert("com.bocom.bbip.gdeupsb.entity.GdLotPrzCtl.LotCtlBatchInsert", ctlList);
		
		/**新增明细信息*/
		
		for(Map<String, Object> temp:(List<Map<String,Object>>)context.getData("bonusItem")){
			List<GdLotPrzDtl>dtlList=(List<GdLotPrzDtl>) BeanUtils.toObjects(
					(List<Map<String,Object>>)temp.get("bonusNode"), GdLotPrzDtl.class);
			Assert.isNotEmpty(dtlList, ErrorCodes.EUPS_QUERY_NO_DATA);
			for(GdLotPrzDtl tmp:dtlList){
				tmp.setGameId((String)context.getData("GameId"));
				tmp.setDrawId((String)context.getData("drawId"));
				tmp.setKenoId((String)context.getData("kenoId"));
				tmp.settLogNo((String)temp.get("TLogNo"));
				tmp.setTxnLog((String)temp.get("txnLog"));
			}
			((SqlMap)get("sqlMap")).insert("com.bocom.bbip.gdeupsb.entity.GdLotPrzDtl.LotDtlBatchInsert", dtlList);

		}
		
	}
    public void fileImport3(Context context)throws CoreException{
    	/**-- 删除旧记录 */
		GdLotPrzCtl ctl=new GdLotPrzCtl();
		ctl.setGameId((String)context.getData("GameId"));
		ctl.setDrawId((String)context.getData("DrawId"));
		ctl.setKenoId((String)context.getData("KenoId"));
		get(GdLotPrzCtlRepository.class).delete(ctl);
		GdLotPrzDtl dtl=new GdLotPrzDtl();
		dtl.setGameId((String)context.getData("GameId"));
		dtl.setDrawId((String)context.getData("DrawId"));
		dtl.setKenoId((String)context.getData("KenoId"));
		get(GdLotPrzDtlRepository.class).delete(dtl);
		/**新增对账控制信息*/
		GdLotChkCtl ChkCtl=new GdLotChkCtl();
		ChkCtl.setChkDat(DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
		ChkCtl.setChkFlg("0");
		ChkCtl.setTotAmt((String)context.getData("totalMoney"));
		ChkCtl.setTotNum((String)context.getData("sucessNum"));
		get(GdLotChkCtlRepository.class).insert(ChkCtl);
		if("Y".equals((String)(context).getData("IsKeno"))){
			List<GdLotChkDtl>dtlList=(List<GdLotChkDtl>) BeanUtils.toObjects(
					(List<Map<String,Object>>)context.getData("scheme"), GdLotChkDtl.class);
			((SqlMap)get("sqlMap")).insert("com.bocom.bbip.gdeupsb.entity.GdLotChkDtl.LotDtlBatchInsert", dtlList);
		}
		else{
			List<GdLotChkDtl>dtlList=(List<GdLotChkDtl>) BeanUtils.toObjects(
					(List<Map<String,Object>>)context.getData("scheme"), GdLotChkDtl.class);
			((SqlMap)get("sqlMap")).insert("com.bocom.bbip.gdeupsb.entity.GdLotChkDtl.LotDtlBatchInsert", dtlList);

		}
	}
    /**
     * 获取系统配置
     */
    
    public void GetSysCfg(Context context) {
        String dealId = GDConstants.LOT_DEAL_ID; // 运营商编号
        GdLotSysCfg gdLotSysCfg = lotSysCfgRepository.findOne(dealId);

        // 查询代收单位协议信息
        String dscAgtNo = gdLotSysCfg.getDsCAgtNo(); // 代收单位编号
        Map<String, Object> inpara = new HashMap<String, Object>();
        inpara.put(ParamKeys.COMPANY_NO, dscAgtNo);
        inpara.put("inqBusLstFlg", "N");

         Result dsResult = bgspServiceAccessObject.callServiceFlatting("queryCorporInfo", inpara);
        Map<String, Object> dsMap = new HashMap<String, Object>();
        dsMap = dsResult.getPayload();

        String fCActNo = (String) dsMap.get("hfeStlAc"); // 代收结算账户,用于处理轧差入账帐号
        String curTim = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss); // 当前时间

        context.setVariable(GDParamKeys.GD_LOT_SYS_CFG, gdLotSysCfg);
        context.setVariable(GDParamKeys.LOT_CURTIM, curTim);
        context.setVariable(GDParamKeys.LOT_FC_ACT_NO, fCActNo);

    }
}

package com.bocom.bbip.gdeupsb.action.lot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GdLotChkCtl;
import com.bocom.bbip.gdeupsb.entity.GdLotChkDtl;
import com.bocom.bbip.gdeupsb.entity.GdLotPrzCtl;
import com.bocom.bbip.gdeupsb.entity.GdLotPrzDtl;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotSysCfg;
import com.bocom.bbip.gdeupsb.repository.GdLotChkCtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotChkDtlRepository;
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
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Transport;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.service.sqlmap.SqlMap;

/**
 *  提供福彩公共方法调用
 * @version 1.0.0
 * Date 2015-01-26
 * @author GuiLin.Li
 * @Update Wuyanhui 2015-02-28
 */
public class CommonLotAction extends BaseAction{
    private static final String ACTION_QUERY_FILE_NAME="237";
    private static final String ACTION_DOWNLOAD_FILE="238";
    @Autowired
    GdLotSysCfgRepository lotSysCfgRepository;
    @Autowired
    BGSPServiceAccessObject bgspServiceAccessObject;
    @Autowired
    GdLotDrwTblRepository lotDrwInfRepository;
    /**
     * 根据与福彩之间的时差获取福彩时间
     * INPUT:
        brNo:分行号
        dealId:福彩时间
        lclTim:本地时间
     * @return  fcTim 福彩时间
     */
    public String getFcTim(String dealId, String brNo) {
        //查询系统参数，获取当前本地与福彩系统的时差
        List<GdLotSysCfg> gdLotSysCfg = lotSysCfgRepository.findSysCfg();
        String difTim = "0";
        if (null != gdLotSysCfg) {
            difTim= gdLotSysCfg.get(0).getDiffTm();
        }
        String srcDate = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss);
        String fcTim = calTim(brNo,srcDate,difTim);
        return fcTim.trim();
    }
    
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
        //检查分行号是否存在
        if (null == brNo) {
            brNo="441999";
        }
        //计算时间
        String [] arr={"d",lotTim,lclTim};
        String difTim = timeDifTools(arr);
        return difTim.trim();
    }
    
    /**
     * 计算时间
     * INPUT:
        brNo:分行号
        srcDate:原时间
        difTim:时差（秒）
     * @return  desDate:计算后的时间
     */
    public String calTim(String brNo,String srcDate, String difTim) {
        //检查分行号是否存在
        if (null == brNo) {
            brNo="441999";
        }
        //计算时间
        String [] arr={"c",srcDate,difTim};
        String desDate = timeDifTools(arr);
        return desDate.trim();
    }
    /**
     * 下载不同的文件并入库
     * @param FileType 文件类型（1：游戏规则文件分类  2：中奖记录文件分类  3:对账文件分类）
     * @param gameId 游戏编号
     * @param drawId 期号
     * @return map 1.文件下载状态（0：成功  -1：失败） 2.文件下载结果描述
     */
    public void downloadFile(Context context)throws CoreException{
        Map<String, String>  map =new HashMap<String, String>();
        context.setData("action", ACTION_QUERY_FILE_NAME);	//237
        context.setData("dealer_id", ACTION_QUERY_FILE_NAME);
        Transport ts = context.getService("STHDLOT1");
        Map<String,Object> resultMap = null;
        try {
            resultMap = (Map<String, Object>) ts.submit(context.getDataMap(), context);
            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
        } catch (CommunicationException e1) {
            e1.printStackTrace();
        } catch (JumpException e1) {
            e1.printStackTrace();
        }
       context.getDataMapDirectly().putAll(resultMap);
       /**文件入库*/
       fileImport(context);
       
    }
  
     /**
     * 计算轧差
     * CalStat 轧差状态（0：成功  -1：失败）
     * CalMsg:轧差结果描述
     */
    public void calcLotDifAmt(Context context) throws CoreException{
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
                Map<String, String> sumPrzDrwMap = lotDrwInfRepository.findSumPrzDrw(gdLotDrwTbl);
                if (sumPrzDrwMap.get("przSumAmt") !="0") {
                    gdLotDrwTbl.setPayAmt(sumPrzDrwMap.get("przSumAmt"));
                    try{
                        lotDrwInfRepository.upateDrwPrzTbl(gdLotDrwTbl);
                    } catch (Exception e) {
                        context.setData("calStat", "-1");
                        context.setData("calMsg", "更新汇总返奖信息失败["+gdLotDrwTbl.getGameId()+"-"+gdLotDrwTbl.getDrawId()+"]");
                        throw new CoreException("更新汇总返奖信息失败["+gdLotDrwTbl.getGameId()+"-"+gdLotDrwTbl.getDrawId()+"]");
                    }
                }
            }
        }
        //查询是否有符合条件的未计算轧差的记录
        List <GdLotDrwTbl> lotDrwTbls = lotDrwInfRepository.qryLotDrwDifAmt();
        if (CollectionUtils.isEmpty(lotDrwTbls)) {
            context.setData("calStat", "-1");
            context.setData("calMsg", "统计轧差失败!");
            return;
        }
        for (GdLotDrwTbl lotDrwTbl : lotDrwTbls) {
            context.setData("gameId", null);
            context.setData("drawId", null);
            context.setData("totAmt", null);
            context.setData("przAmt", null);
            context.setData("kenoId", null);
            int totAmt = Integer.parseInt(lotDrwTbl.getTotAmt());
            int przAmt = Integer.parseInt(lotDrwTbl.getPrzAmt());
            if (totAmt > przAmt) {
                //1借方（购彩总金额大于返奖总金额）
                lotDrwTbl.setDifFlg("1");
                int difAmt = totAmt - przAmt;
                lotDrwTbl.setDifAmt(String.valueOf(difAmt));
            } else {
                // 0贷方（购彩总金额小于奖总金额）
                lotDrwTbl.setDifFlg("0");
                int difAmt = przAmt - totAmt;
                lotDrwTbl.setDifAmt(String.valueOf(difAmt));
            }
            try{
                lotDrwInfRepository.UpdLotDifAmt(lotDrwTbl);
            } catch (Exception e) {
                context.setData("calStat", "-1");
                context.setData("calMsg", "更新轧差失败["+lotDrwTbl.getGameId()+"-"+lotDrwTbl.getDrawId()+"]");
                throw new CoreException("更新轧差失败["+lotDrwTbl.getGameId()+"-"+lotDrwTbl.getDrawId()+"]");
            }
        }
    }

	public void fileImport(Context context) throws CoreException {
		final String filetyp = context.getData(ParamKeys.FILE_TYPE);
		if ("2".equals(filetyp)) {
			/** 中奖文件入库 */
			fileImport2(context);
		}
		if ("3".equals(filetyp)) {
			/** 对账文件入库 */
			fileImport3(context);
		}

	}
	/***/
	@SuppressWarnings("unchecked")
    private void fileImport2(Context context)throws CoreException{
		context.setData("action", ACTION_DOWNLOAD_FILE);
		context.setData("file_type", context.getData(ParamKeys.FILE_TYPE));
		   Transport ts = context.getService("STHDLOT1");
	        Map<String,Object> resultMap = null;
	        try {
	            resultMap = (Map<String, Object>) ts.submit(context.getDataMap(), context);
	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	        } catch (CommunicationException e1) {
	            e1.printStackTrace();
	        } catch (JumpException e1) {
	            e1.printStackTrace();
	        }
		/**-- 删除旧记录 */
		GdLotPrzCtl ctl=new GdLotPrzCtl();
		ctl.setGameId((String)context.getData("GameId"));
		ctl.setDrawId((String)context.getData("DrawId"));
		ctl.setKenoId("");
		get(GdLotPrzCtlRepository.class).deleteByGameId(ctl);
		GdLotPrzDtl dtl=new GdLotPrzDtl();
		dtl.setGameId((String)context.getData("GameId"));
		dtl.setDrawId((String)context.getData("DrawId"));
		dtl.setKenoId("");
		get(GdLotPrzDtlRepository.class).deleteByGameId(dtl);
		/**新增中奖控制信息*/
		List<GdLotPrzCtl>ctlList=(List<GdLotPrzCtl>) BeanUtils.toObjects(
				(List<Map<String,Object>>)resultMap.get("ret"), GdLotPrzCtl.class);
		Assert.isNotEmpty(ctlList, ErrorCodes.EUPS_QUERY_NO_DATA);
		((SqlMap)get("sqlMap")).insert("com.bocom.bbip.gdeupsb.entity.GdLotPrzCtl.LotCtlBatchInsert", ctlList);
		
		/**新增明细信息*/
		
		for(Map<String, Object> temp:(List<Map<String,Object>>)resultMap.get("ret")){
			List<GdLotPrzDtl>dtlList=(List<GdLotPrzDtl>) BeanUtils.toObjects(
					(List<Map<String,Object>>)temp.get("bonusNodes"), GdLotPrzDtl.class);
			Assert.isNotEmpty(dtlList, ErrorCodes.EUPS_QUERY_NO_DATA);
			
			((SqlMap)get("sqlMap")).insert("com.bocom.bbip.gdeupsb.entity.GdLotPrzDtl.LotDtlBatchInsert", dtlList);
		}
	}
    @SuppressWarnings("unchecked")
    private void fileImport3(Context context)throws CoreException{
    	
    	context.setData("action", ACTION_DOWNLOAD_FILE);
		context.setData("file_type", context.getData(ParamKeys.FILE_TYPE));
		   Transport ts = context.getService("STHDLOT1");
	        Map<String,Object> resultMap = null;
	        try {
	            resultMap = (Map<String, Object>) ts.submit(context.getDataMap(), context);
	            context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL);
	        } catch (CommunicationException e1) {
	            e1.printStackTrace();
	        } catch (JumpException e1) {
	            e1.printStackTrace();
	        }
    	/**-- 删除旧记录 */
		GdLotPrzCtl ctl=new GdLotPrzCtl();
		ctl.setGameId((String)context.getData("GameId"));
		ctl.setDrawId((String)context.getData("DrawId"));
		ctl.setKenoId("");
		get(GdLotPrzCtlRepository.class).deleteByGameId(ctl);
		GdLotPrzDtl dtl=new GdLotPrzDtl();
		dtl.setGameId((String)context.getData("GameId"));
		dtl.setDrawId((String)context.getData("DrawId"));
		dtl.setKenoId("");
		get(GdLotPrzDtlRepository.class).deleteByGameId(dtl);
		/**新增对账控制信息*/
		GdLotChkCtl ChkCtl=new GdLotChkCtl();
		ChkCtl.setGameId((String)context.getData("GameId"));
		ChkCtl.setDrawId((String)context.getData("DrawId"));
		ChkCtl.setKenoId("");
		ChkCtl.setChkTim("");
		ChkCtl.setChkDat(DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
		ChkCtl.setChkFlg("0");
		ChkCtl.setTotAmt((String)resultMap.get("totalMoney"));
		ChkCtl.setTotNum((String)resultMap.get("sucessNum"));
		get(GdLotChkCtlRepository.class).insert(ChkCtl);
		if(((String)resultMap.get("game_id")).equals("7")){
			for(Map<String, Object> temp:(List<Map<String,Object>>)resultMap.get("ret")){
			List<GdLotChkDtl>dtlList=(List<GdLotChkDtl>) BeanUtils.toObjects(
					(List<Map<String,Object>>)temp.get("kenoNodes"), GdLotChkDtl.class);
			((SqlMap)get("sqlMap")).insert("com.bocom.bbip.gdeupsb.entity.GdLotChkDtl.LotDtlBatchInsert", dtlList);
		}
		}
		else{
			GdLotChkDtl ChkDtl=new GdLotChkDtl();
			ChkDtl.setGameId((String)context.getData("GameId"));
			ChkDtl.setDrawId((String)context.getData("DrawId"));
			ChkDtl.setKenoId("");
			ChkDtl.setChkTim("");
			ChkDtl.setChkDat(DateUtils.format(new Date(),DateUtils.STYLE_yyyyMMdd));
			ChkDtl.setChkFlg("0");
			get(GdLotChkDtlRepository.class).insert(ChkDtl);
		}
	}
    /**
     * 获取系统配置
     */
    
    public void GetSysCfg(Context context) {
        log.info(" Get Systerm Config  Start...!");
        
       List <GdLotSysCfg> gdLotSysCfgs = lotSysCfgRepository.findSysCfg();

        // 查询代收单位协议信息
        String dscAgtNo = gdLotSysCfgs.get(0).getDsCAgtNo(); // 代收单位编号
        Map<String, Object> inpara = new HashMap<String, Object>();
        inpara.put(ParamKeys.COMPANY_NO, dscAgtNo);
        inpara.put("inqBusLstFlg", "N");

        Result dsResult = bgspServiceAccessObject.callServiceFlatting("queryCorporInfo", inpara);
        Map<String, Object> dsMap = new HashMap<String, Object>();
        dsMap = dsResult.getPayload();

        String fCActNo = (String) dsMap.get("hfeStlAc"); // 代收结算账户,用于处理轧差入账帐号
        String curTim = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMddHHmmss); // 当前时间

        context.setVariable(GDParamKeys.GD_LOT_SYS_CFG, gdLotSysCfgs.get(0));
        context.setVariable(GDParamKeys.LOT_CURTIM, curTim);
        context.setVariable(GDParamKeys.LOT_FC_ACT_NO, fCActNo);

    }
    /**
     * 时差计算方法
     * @param paramArrayOfString
     */
    private String timeDifTools (String[] paramArrayOfString)
    {
        String funcTyp = paramArrayOfString[0];
        String difTime = "";
        try {
            if ("d".equals(funcTyp)) {
                difTime =difTime(paramArrayOfString);
            } else if ("c".equals(funcTyp)) {
                difTime = calTime(paramArrayOfString);
            }
        } catch (Exception localException1) {
            localException1.printStackTrace();
        }

      return difTime;
    }
    private String difTime(String[] paramArrayOfString) throws Exception {
        Date localDate1 = new SimpleDateFormat("yyyyMMddHHmmss").parse(paramArrayOfString[1]);
        Date localDate2 = new SimpleDateFormat("yyyyMMddHHmmss").parse(paramArrayOfString[2]);
        return (localDate1.getTime() - localDate2.getTime()) / 1000L + "";
    }

    private String calTime(String[] paramArrayOfString) throws Exception {
         Date localDate1 = new SimpleDateFormat("yyyyMMddHHmmss").parse(paramArrayOfString[1]);
         Date localDate2 = new Date(localDate1.getTime() + Long.parseLong(paramArrayOfString[2]) * 1000L);
        return new SimpleDateFormat("yyyyMMddHHmmss").format(localDate2);
    }
}

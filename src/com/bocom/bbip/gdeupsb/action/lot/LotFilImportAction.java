package com.bocom.bbip.gdeupsb.action.lot;

import java.util.Date;

import javax.ejb.EnterpriseBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.getProperty;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.BPState;
import com.bocom.bbip.gdeupsb.entity.GDEupsbLotChkCtl;
import com.bocom.bbip.gdeupsb.entity.GDEupsbLotChkDtl;
import com.bocom.bbip.gdeupsb.entity.GDEupsbLotPrzCtl;
import com.bocom.bbip.gdeupsb.entity.GDEupsbLotPrzDtl;
import com.bocom.bbip.gdeupsb.repository.GDEupsbLotChkCtlRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbLotChkDtlRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbLotPrzCtlRepository;
import com.bocom.bbip.gdeupsb.repository.GDEupsbLotPrzDtlRepository;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class LotFilImportAction extends BaseAction{
	private static Logger logger = LoggerFactory.getLogger(LotFilImportAction.class);
	
	public void execute(Context context) throws CoreException, CoreRuntimeException{
		
		if("2".equals(context.getData("filTyp"))){	//中奖文件入库
			fileImport2(context);
		}
		if("3".equals(context.getData("filTyp"))){	//对账文件入库
			fileImport3(context);
		}
	}
	
	/**
	 * 中奖文件入库
	 * @param context
	 */
	public void fileImport2(Context context) {
		logger.info("Enter In LotFilImportAction.fileImport2....");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
		/*delete from LotPrzCtl
		where GameId='%s' and DrawId='%s' and KenoId='%s'*/
		/*delete from LotPrzDtl
		where GameId='%s' and DrawId='%s' and KenoId='%s'*/
		//删除中奖记录控制表、中奖记录明细表中的相关数据
		GDEupsbLotPrzCtl deletePrzCtl = new GDEupsbLotPrzCtl();
		deletePrzCtl.setGameId(context.getData("gameId").toString());
		deletePrzCtl.setDrawId(context.getData("drawId").toString());
		deletePrzCtl.setKenoId(context.getData("kenoId").toString());
		get(GDEupsbLotPrzCtlRepository.class).delete(deletePrzCtl);
		
		GDEupsbLotPrzDtl deletePrzDtl = new GDEupsbLotPrzDtl();
		deletePrzDtl.setGameId(context.getData("gameId").toString());
		deletePrzDtl.setDrawId(context.getData("drawId").toString());
		deletePrzDtl.setKenoId(context.getData("kenoId").toString());
		get(GDEupsbLotPrzDtlRepository.class).delete(deletePrzDtl);
		
		//新增记录
		int cnt = 1;
		while(cnt <= Integer.parseInt((String) context.getData("bonsCnt"))){
			
			context.setData("ROOT.LotSn", context.getData("ROOT.bonusItem_" + cnt + ".LotSn"));
			context.setData("ROOT.LogDumpId", context.getData("ROOT.bonusItem_" + cnt + ".LogDumpId"));
			context.setData("ROOT.Cipher", context.getData("ROOT.bonusItem_" + cnt + ".Cipher"));
			context.setData("ROOT.BigBon", context.getData("ROOT.bonusItem_" + cnt + ".BigBon"));
			context.setData("ROOT.TotPrz", context.getData("ROOT.bonusItem_" + cnt + ".TotPrz"));
			context.setData("ROOT.TLogNo", context.getData("ROOT.bonusItem_" + cnt + ".TLogNo"));
			context.setData("ROOT.TermID", context.getData("ROOT.bonusItem_" + cnt + ".TermID"));
			context.setData("ROOT.TxnLog", context.getData("ROOT.bonusItem_" + cnt + ".TxnLog"));
			context.setData("ROOT.BonsNodCnt", context.getData("ROOT.bonusItem_" + cnt + ".BonsNodCnt"));
			
			GDEupsbLotPrzCtl insLotPrzCtl = BeanUtils.toObject(context.getDataMap(), GDEupsbLotPrzCtl.class);
			get(GDEupsbLotPrzCtlRepository.class).insert(insLotPrzCtl);
		
			int nodCnt = 1;
			while(nodCnt <= Integer.parseInt((String)context.getData("ROOT.BonsNodCnt"))){
				//从节点取值
				context.setData("ROOT.BetMod", context.getData("ROOT.bonusItem_" + cnt + ".BetMod"));
				context.setData("ROOT.BetMul", context.getData("ROOT.bonusItem_" + cnt + ".BetMul"));
				context.setData("ROOT.classNo", context.getData("ROOT.bonusItem_" + cnt + ".classNo"));
				context.setData("ROOT.PrzAmt", context.getData("ROOT.bonusItem_" + cnt + ".PrzAmt"));
				context.setData("ROOT.BetLin", context.getData("ROOT.bonusItem_" + cnt + ".BetLin"));
				//insert into GDEupsbLotPrzDtl
				GDEupsbLotPrzDtl insLotPrzDtl = BeanUtils.toObject(context.getDataMap(), GDEupsbLotPrzDtl.class);
				get(GDEupsbLotPrzDtlRepository.class).insert(insLotPrzDtl);
				
				nodCnt++;
			}
			cnt++;
		}
//		<Set>FileImportStatus=0</Set>
//		<Set>FileImportMsg=中奖明入库成功</Set>
		
		context.setData("fileImportStatus", "0");
		context.setData("fileImportMsg", "中奖明细入库成功");
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL);
	}


	/**
	 * 对账文件入库
	 * @param context
	 */
	public void fileImport3(Context context) {
		logger.info("=============对账文件入库操作===========");
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_FAIL); 
		//对账文件不单独处理一个keno期
		context.setData("kenoId", "");
		
		//删除删除对账控制表和对账明细表中对应的数据
		/*delete from LotChkCtl
		where GameId='%s' and DrawId='%s' and KenoId='%s'*/
		GDEupsbLotChkCtl delLotChkCtl = new GDEupsbLotChkCtl();
		delLotChkCtl.setGameId(context.getData("gameId").toString());
		delLotChkCtl.setDrawId(context.getData("drawId").toString());
		delLotChkCtl.setKenoId(context.getData("kenoId").toString());
		get(GDEupsbLotChkCtlRepository.class).delete(delLotChkCtl);
		
		/*delete from LotChkDtl
		where GameId='%s' and DrawId='%s' and KenoId='%s'*/
		GDEupsbLotChkDtl delLotChkDtl = new GDEupsbLotChkDtl();
		delLotChkDtl.setGameId(context.getData("gameId").toString());
		delLotChkDtl.setDrawId(context.getData("drawId").toString());
		delLotChkDtl.setKenoId(context.getData("kenoId").toString());
		get(GDEupsbLotChkDtlRepository.class).delete(delLotChkDtl);
		
		//新增对账控制表
		/*insert into LotChkCtl (ChkDat, GameId, DrawId, KenoId, TotNum, TotAmt, ChkFlg, ChkTim)
		values('%s','%s','%s','%s','%s','%s','%s','%s')
		*/
		//gameId已存在context gameId=5
		context.setData("chkDat", DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd));
		context.setData("chkFlg", "0");
		context.setData("chkTim", "");
		context.setData("totNum", context.getData("successNum"));
		context.setData("totAmt", context.getData("totalMoney"));
		
		GDEupsbLotChkCtl insertLotChkCtl = BeanUtils.toObject(context.getDataMap(), GDEupsbLotChkCtl.class);
		get(GDEupsbLotChkCtlRepository.class).insert(insertLotChkCtl);
	 
		
		if("Y".equals(context.getData("isKeno"))){
			int cnt = 1;
			while(cnt <= Integer.parseInt((String)context.getData("recNum"))){
				int subCnt = 1;
				context.setData("ROOT.KenoId", context.getData("ROOT.Rec_" + cnt + ".KenoId")); //ICS: GetValue  真的需要吗？？？
				while(true){
					context.setData("ROOT.SeqNo", "ROOT.Rec_" + cnt + ".SubCnt_" + subCnt + ".SeqNo");
					if(null == context.getData("ROOT.SeqNo")){
						break;
					}
					context.setData("ROOT.SchId", context.getData("ROOT.Rec_" + cnt + ".SubCnt_" + subCnt + ".SchId"));
					context.setData("ROOT.LotNam", context.getData("ROOT.Rec_" + cnt + ".SubCnt_" + subCnt + ".LotNam"));
					context.setData("ROOT.TxnLog", context.getData("ROOT.Rec_" + cnt + ".SubCnt_" + subCnt + ".TxnLog"));
					context.setData("ROOT.playId", context.getData("ROOT.Rec_" + cnt + ".SubCnt_" + subCnt + ".palyId"));
					context.setData("ROOT.TxnTim", context.getData("ROOT.Rec_" + cnt + ".SubCnt_" + subCnt + ".TxnTim"));
					context.setData("ROOT.TxnAmt", context.getData("ROOT.Rec_" + cnt + ".SubCnt_" + subCnt + ".TxnAmt"));
					//增加对账明细表数据
					/*
					 * insert into LotChkDtl (ChkDat, GameId, DrawId, KenoId, SeqNo, SchId, LotNam, TxnLog, PlayId, TxnTim, TxnAmt, ChkFlg, ChkTim)
				values('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')
					 */
					GDEupsbLotChkDtl insertLotChkDtl = BeanUtils.toObject(context.getDataMap(), GDEupsbLotChkDtl.class);
					get(GDEupsbLotChkDtlRepository.class).insert(insertLotChkDtl);
					
					subCnt++;
				}
				cnt++;
			}
		}else{ //新增对账明细表数据
			GDEupsbLotChkDtl insertLotChkDtl = BeanUtils.toObject(context.getDataMap(), GDEupsbLotChkDtl.class);
			get(GDEupsbLotChkDtlRepository.class).insert(insertLotChkDtl);
		}
		context.setData("fileImportStatus", "0");
		context.setData("fileImportMsg", "对账信息入库成功");
		
		context.setState(BPState.BUSINESS_PROCESSNIG_STATE_NORMAL); 
	}
}

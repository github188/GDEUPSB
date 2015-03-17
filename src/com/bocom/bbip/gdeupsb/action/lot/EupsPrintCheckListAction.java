package com.bocom.bbip.gdeupsb.action.lot;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.entity.GdLotDrwTbl;
import com.bocom.bbip.gdeupsb.entity.GdLotTxnJnl;
import com.bocom.bbip.gdeupsb.repository.GdLotChkDtlRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotDrwTblRepository;
import com.bocom.bbip.gdeupsb.repository.GdLotTxnJnlRepository;
import com.bocom.bbip.utils.Assert;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.ContextUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.support.CollectionUtils;

/**
 * @author wuyh
 *
 */
public class EupsPrintCheckListAction  extends BaseAction {
	private static final Log logger=LogFactory.getLog(EupsPrintCheckListAction.class);
	private static final String RPT_TYPE_SUCC = "1";
	private static final String RPT_TYPE_BANK_FAIL = "2";
	private static final String RPT_TYPE_THD_FAIL = "3";
	public void process(Context context)throws IOException, CoreRuntimeException, JumpException{
	
		logger.info("游戏对账清单打印 start!!");
		/** 操作类型 0:浏览  1:打印 */
		final String FunTyp=ContextUtils.assertDataHasLengthAndGetNNR(context, "FunTyp", ErrorCodes.EUPS_FIELD_EMPTY);
		final String fleNme=ContextUtils.assertDataHasLengthAndGetNNR(context, ParamKeys.FLE_NME, ErrorCodes.EUPS_FIELD_EMPTY);
		final String GameId=ContextUtils.assertDataHasLengthAndGetNNR(context, "GameId", ErrorCodes.EUPS_FIELD_EMPTY);
		final String DrawId=ContextUtils.assertDataHasLengthAndGetNNR(context, "DrawId", ErrorCodes.EUPS_FIELD_EMPTY);
		/** 报表类型 1:对账成功清单 2:银行端对账失败清单  3:福彩方对账失败清单 */
		final String RptTyp=ContextUtils.assertDataHasLengthAndGetNNR(context, "RptTyp", ErrorCodes.EUPS_FIELD_EMPTY);
		
		final String GameDesc = getGameIdDesc(GameId);
		GdLotDrwTbl info=new GdLotDrwTbl();
		info.setDrawId(DrawId);
		info.setGameId(GameId);
		GdLotDrwTbl result=get(GdLotDrwTblRepository.class).findOne(info);
		Assert.isFalse(null==result, ErrorCodes.EUPS_QUERY_NO_DATA);
		/** 得到DrawNme */
		context.setData("DrawNme", result.getDrawNm());
		context.setData("GameNme", GameDesc);
        context.setVariable("type", RptTyp);
		/** 打印报表 */
        Map param=CollectionUtils.createMap();
        param.put("GameId", GameId);
        param.put("DrawId", DrawId);
		printReport(context, RptTyp,param);
		logger.info("游戏对账清单打印!!");
	}
    private void printReport(Context context,final String RptTyp,final Map<String,String>map)throws CoreException, IOException{
    	if (RptTyp.equals(RPT_TYPE_SUCC)) {
    		/** 对账成功清单 */
    		printSuccess(context,map);
		} else if (RptTyp.equals(RPT_TYPE_BANK_FAIL)) {
			/** 银行端对账失败清单*/
			printFail(context,map);
		}else if (RptTyp.equals(RPT_TYPE_THD_FAIL)) {
			/** 福彩方对账失败清单 */
			printFuCaiFail(context,map);
		}
		else {
			throw new CoreException("GameId NOT found!!");
		}
    }
	private String getGameIdDesc(final String GameId) throws CoreException {
		String desc = "";
		if (GameId.equals(GDConstants.LOTR_GAMEID_DOUBLE_COLOUR_BALL)) {
			/** 双色球 */
			desc = "双色球";
		} else if (GameId.equals(GDConstants.LOTR_GAMEID_HAPPY_TEN_MINUTE)) {
			/** 快乐十分 */
			desc = "快乐十分";
		} else {
			throw new CoreException("GameId NOT found!!");
		}
		return desc;
	}

	

	private void printSuccess(Context context,final Map<String,String>map) throws CoreException, IOException {
		Map<String, String> mapping = CollectionUtils.createMap();
		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		String sampleFile="config/report/lot/LotCheck.vm";
		List<GdLotTxnJnl> list=get(GdLotTxnJnlRepository.class).findCheckSuccess(map);
		Assert.isNotEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
		List<Map<String,Object>>eles=(List<Map<String,Object>>)BeanUtils.toMaps(list);
		context.setData("SumCnt", list.size());
		mapping.put("sample", sampleFile);
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		context.setData("eles", eles);
		render.setReportNameTemplateLocationMapping(mapping);
		String result = render.renderAsString("sample", context);
		logger.info("generate report content:****"+new String(result.getBytes(GDConstants.CHARSET_ENCODING_GBK)));
		BufferedOutputStream outStream = null;
		try {

			outStream = new BufferedOutputStream(new FileOutputStream(
					"D:\\lotCheck.txt"));
			outStream.write(result.getBytes(GDConstants.CHARSET_ENCODING_GBK));
		} catch (IOException e) {
			throw new CoreException("BBIP0004EU0128");
		}
		outStream.close();
		
	}
	private void printFail(Context context,final Map<String,String>map) throws CoreException, IOException {
		Map<String, String> mapping = CollectionUtils.createMap();
		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		String sampleFile="config/report/lot/LotCheck.vm";
		List<GdLotTxnJnl> list=get(GdLotTxnJnlRepository.class).findCheckFail(map);
		Assert.isNotEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
		List<Map<String,Object>>eles=(List<Map<String,Object>>)BeanUtils.toMaps(list);
		context.setData("SumCnt", list.size());
		mapping.put("sample", sampleFile);
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		context.setData("eles", eles);
		render.setReportNameTemplateLocationMapping(mapping);
		String result = render.renderAsString("sample", context);
		logger.info("generate report content:****"+new String(result.getBytes(GDConstants.CHARSET_ENCODING_GBK)));
		BufferedOutputStream outStream = null;
		try {

			outStream = new BufferedOutputStream(new FileOutputStream(
					"D:\\lotCheck.txt"));
			outStream.write(result.getBytes(GDConstants.CHARSET_ENCODING_GBK));
		} catch (IOException e) {
			throw new CoreException("BBIP0004EU0128");
		}
		outStream.close();
		
	}
	private void printFuCaiFail(Context context,final Map<String,String>map) throws CoreException, IOException{
		Map<String, String> mapping = CollectionUtils.createMap();
		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		String sampleFile="config/report/lot/LotCheck.vm";
		List <Map<String,String>>list=get(GdLotChkDtlRepository.class).findThdFailList(map);
		Assert.isNotEmpty(list, ErrorCodes.EUPS_QUERY_NO_DATA);
		context.setData("SumCnt", 1);
		mapping.put("sample", sampleFile);
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		context.setData("eles", list);
		render.setReportNameTemplateLocationMapping(mapping);
		String result = render.renderAsString("sample", context);
		logger.info("generate report content:****"+new String(result.getBytes(GDConstants.CHARSET_ENCODING_GBK)));
		BufferedOutputStream outStream = null;
		try {

			outStream = new BufferedOutputStream(new FileOutputStream(
					"D:\\lotCheck.txt"));
			outStream.write(result.getBytes(GDConstants.CHARSET_ENCODING_GBK));
		} catch (IOException e) {
			throw new CoreException("BBIP0004EU0128");
		}
		outStream.close();
	}
}

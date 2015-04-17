package com.bocom.bbip.gdeupsb.action.transportfee;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.common.OperateFTPAction;
import com.bocom.bbip.eups.action.common.OperateFileAction;
import com.bocom.bbip.eups.adaptor.ThirdPartyAdaptor;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.gdeupsb.entity.GDEupsbTrspFeeInfo;
import com.bocom.bbip.gdeupsb.repository.GDEupsbTrspFeeInfoRepository;
import com.bocom.bbip.gdeupsb.repository.TrspCheckTmpRepository;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class NodTrspCheckFileAction extends BaseAction{
	@Autowired
	TrspCheckTmpRepository trspCheckTmpRepository;
	@Autowired
	GDEupsbTrspFeeInfoRepository gdEupsbTrspFeeInfoRepository;
	@Autowired
	OperateFTPAction operateFTPAction;
	@Autowired
	EupsThdFtpConfigRepository eupsThdFtpConfigRepository;
	@Autowired
	OperateFileAction operateFileAction;
	@Autowired
	@Qualifier("callThdTradeManager")
	ThirdPartyAdaptor callThdTradeManager;
	private final static Log log = LogFactory.getLog(NodTrspCheckFileAction.class);
	
	public void execute(Context ctx) throws CoreException,CoreRuntimeException{
		log.info("NodTrspCheckFileAction start......");
//		String tChkNo=((BTPService)get("BTPService")).applyBatchNo(ParamKeys.BUSINESS_CODE_COLLECTION);
		String journalModel =(String) ctx.getData("journalModel");
		Map<String, Object> map = new HashMap<String, Object>();
		
		Date startDate = DateUtils.parse((String)ctx.getData("startDate"));
		Date endDate = DateUtils.parse((String)ctx.getData("endDate"));
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		map.put("nodNo", (String)ctx.getData("nodNo"));
		map.put("bk", ctx.getData(ParamKeys.BK).toString());
 		if("0".equals(journalModel)){
			
//			SELECT Status,count(*) as count,value(sum(cast(TxnAmt as BIGINT)),0) as amt
//            FROM   rbfbtxnbok444
//            WHERE  BrNo='%s'
//            AND  ((Status='0' and (PayDat between '%s' and '%s') and (PayNod='%s' or '%s'='A'))
//            OR   (Status='1' and (TActDt between '%s' and '%s') and (PrtNod='%s' or '%s'='A') )
//            OR   ((Status='2' or Status='3') and (RvsDat between '%s' and '%s') and (RvsNod='%s' or '%s'='A')))
//            GROUP BY Status">
//  <FieldList>$BrNo|$BegDat|$EndDat|$QryNod|$QryNod|$BegDat|$EndDat|$QryNod|$QryNod|$BegDat|$EndDat|$QryNod|$QryNod|
			//得到搜索的集合
			List<GDEupsbTrspFeeInfo> list =gdEupsbTrspFeeInfoRepository.find0(map);

			//清单打印 
			printDetail(ctx, list,map);
		}
//		//得到搜索的集合
//		List<GDEupsbTrspFeeInfo> list =gdEupsbTrspFeeInfoRepository.find("");
//
//		//清单打印 
//		printDetail(ctx, list);
	}
	
	/**
	 * 打印清单
	 */
	public void printDetail(Context context, 
			List<GDEupsbTrspFeeInfo> detailList,Map<String, Object> mapSum) throws CoreException {
		log.info("~~~~~~~~~~~Start  CheckTrspFile   printDetail");
		// 报表模式
		int i = Integer.parseInt(context.getData(GDParamKeys.JOURNAL_MODEL)
				.toString());
		// context.setData("rptFil", rptFil);

		String rptFmt = "rptFmt";
		
		
		//重写   得到总笔数 总金额
		List<Map<String, Object>> list = gdEupsbTrspFeeInfoRepository.findSum(mapSum);
		System.out.println("@@@list="+list);
		// 得到总笔数 总金额
		if (CollectionUtils.isEmpty(list)) {
			context.setData(ParamKeys.RSP_CDE, "329999");
			context.setData(ParamKeys.RSP_MSG, "查询统计信息失败");
			throw new CoreException("查询统计信息失败");
		} else {
			System.out.println("@@@@@@@@@@@@"+list.get(0));
			int ornCnt = Integer.parseInt(list.get(0).get("ORNCNT") + "");
			BigDecimal ornAmt = new BigDecimal(list.get(0).get("ORNAMT")
					+ "");
			int sucCnt = Integer.parseInt(list.get(0).get("SUCCNT") + "");
			BigDecimal sucAmt = new BigDecimal(list.get(0).get("SUCAMT")
					+ "");
			context.setData("ornCnt", ornCnt);
			context.setData("ornAmt", ornAmt);
			context.setData("sucCnt", sucCnt);
			context.setData("sucAmt", sucAmt);
		}

		if (0 == i) { // ~~~~~~~~汇总方式
			rptFmt = rptFmt + 00;
		} else if (1 == i) {// ~~~~~~~~~~~~~清单方式
			rptFmt = rptFmt + 1;
		} else if (2 == i) {// ~~~~~~~~~~~~~更改发票清单
			rptFmt = rptFmt + 2;
		} else if (3 == i) {// ~~~~~~~~~~~~~未打印发票清单
			rptFmt = rptFmt + 3;
		} else {
			context.setData(GDParamKeys.MSGTYP, "E");
			context.setData(ParamKeys.RSP_CDE, "329999");
			context.setData(ParamKeys.RSP_MSG, "统计模式错误");
			throw new CoreException("统计模式错误");
		}
		String path = "config/report/zhTransport/" + rptFmt + ".vm";
		context.setData(rptFmt, path);
		// TODO 报表生成
		VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
		try {
			render.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String rptFil = "Car"
				+ context.getData("tlr").toString()
				+ context.getData(GDParamKeys.START_DATE).toString()
						.substring(5) + ".dat";
		// 拼装文件
		Map<String, String> map = new HashMap<String, String>();
		map.put(rptFil, path);
		render.setReportNameTemplateLocationMapping(map);
		context.setData("eles", detailList);
		String result = render.renderAsString(rptFil, context);
		System.out
				.println("~~~~~~~~~~~~~result~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println(result);
		// 文件路径
		StringBuffer rpFmts = new StringBuffer();
		rpFmts.append("F:\\");
		File file = new File(rptFil.toString());
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(rpFmts
					.append(rptFil).toString());
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					fileOutputStream, "GBK");
			BufferedWriter bufferedWriter = new BufferedWriter(
					outputStreamWriter);
			PrintWriter printWriter = new PrintWriter(bufferedWriter);
			printWriter.write(result);
			// 关闭
			printWriter.close();
			bufferedWriter.close();
			outputStreamWriter.close();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO 没有确定下名字rptFil 还是 fileName
		// String fileName=context.getData("fileName").toString();
		System.out
				.println("~~~~~~~~~~~~~sendFile~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		// sendFile(context,rptFmt);
	}
}

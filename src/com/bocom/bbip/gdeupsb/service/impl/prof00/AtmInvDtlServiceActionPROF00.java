package com.bocom.bbip.gdeupsb.service.impl.prof00;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.bocom.bbip.comp.BBIPPublicServiceImpl;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.action.eupsreport.ReportHelper;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.MFTPConfigInfo;
import com.bocom.bbip.eups.repository.EupsThdFtpConfigRepository;
import com.bocom.bbip.file.reporting.impl.VelocityTemplatedReportRender;
import com.bocom.bbip.file.transfer.ftp.FTPTransfer;
import com.bocom.bbip.gdeupsb.common.GDConstants;
import com.bocom.bbip.gdeupsb.common.GDParamKeys;
import com.bocom.bbip.thd.org.apache.commons.collections.CollectionUtils;
import com.bocom.bbip.thd.org.apache.commons.lang.math.NumberUtils;
import com.bocom.bbip.thd.org.apache.velocity.util.StringUtils;
import com.bocom.bbip.utils.BeanUtils;
import com.bocom.bbip.utils.DateUtils;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
import com.bocom.jump.bp.service.sqlmap.SqlMap;
import com.bocom.jump.bp.service.sqlmap.impl.SqlMapImpl;
/**
 * 自助通发票使用情况明细表查询打印
 * @author hefengwen
 *
 */
public class AtmInvDtlServiceActionPROF00 extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(AtmInvDtlServiceActionPROF00.class);
	
	@Autowired
	BBIPPublicServiceImpl bbipPublicService;
	
	@Override
	public void execute(Context context) throws CoreException,	CoreRuntimeException {
		logger.info("AtmInvDtlServiceActionPROF00 start ... ...");
		String func = context.getData("func");
		logger.info("func["+func+"]");
		if("1".equals(func)){//自助通发票使用情况明细表
			invDtl(context);
		}else if("2".equals(func)){//自助通发票使用情况汇总表
			invSum(context);
		}else{//企业发票使用情况明细表
			qyInvSum(context);
		}
		logger.info("AtmInvDtlServiceActionPROF00 end ... ...");
	}
	/**
	 * 自助通发票使用情况明细表
	 * @param context
	 * @throws CoreException 
	 */
	private void invDtl(Context context) throws CoreException{
		logger.info("AtmInvDtlServiceActionPROF00 invDtl start ... ...");
		String invTyp = context.getData("invTyp");
		String begDat = context.getData("begDat");
		String endDat = context.getData("endDat");
		String qryNod = context.getData("qryNod");
		String prtFlg = context.getData("prtFlg");
		logger.info("invTyp["+invTyp+"]begDat["+begDat+"]endDat["+endDat+"]qryNod["+qryNod+"]prtFlg["+prtFlg+"]");
		if("1".equals(prtFlg)){//打印
			//TODO:根据分行号和网点号查询网点名称，待实现
//			context.setData("nodNam", context.getData("filler"));//测试代码
			
			EupsThdFtpConfigRepository eupsThdFtpConfigRepository = get(EupsThdFtpConfigRepository.class);
			ReportHelper reportHelper = get(ReportHelper.class);
			MFTPConfigInfo mftpConfigInfo = reportHelper.getMFTPConfigInfo(eupsThdFtpConfigRepository);
			logger.info((new StringBuilder("mftpConfigInfo:>>>>").append(BeanUtils.toMap(mftpConfigInfo))).toString());
			
			List<Map<String,Object>> dtls = ((SqlMap)get("sqlMap")).queryForList("prof00.atmInvDtl", context.getDataMap());
			Map<String,Object> sum = ((SqlMap)get("sqlMap")).queryForObject("prof00.atmInvSumRpt", context.getDataMap());
			for(Map<String,Object> map:dtls){
				map.put("actDat", map.get("ACT_DAT"));
				map.put("oprTlr", map.get("TLR_ID"));
				map.put("regTlr", map.get("REG_TLR"));
				map.put("invTyp", map.get("INV_TYP"));
				map.put("ivNo", map.get("IV_BEG_NO")+"-"+map.get("IV_END_NO"));
				map.put("tolNum", String.valueOf(map.get("TOL_NUM")));
				map.put("lodNum", String.valueOf(map.get("LOD_NUM")));
				map.put("useNum", String.valueOf(map.get("USE_NUM")));
				map.put("clrNum", String.valueOf(map.get("CLR_NUM")));
				map.put("invNum", String.valueOf(map.get("INV_NUM")));
				logger.info("====="+map.get("lodNum")+map.get("useNum")+map.get("clrNum")+map.get("invNum"));
			}
			context.setData("eles", dtls);
			context.setData("slodNum", sum==null?"0":sum.get("LOD_NUM"));
			context.setData("suseNum", sum==null?"0":sum.get("USE_NUM"));
			context.setData("sclrNum", sum==null?"0":sum.get("CLR_NUM"));
			context.setData("tol", dtls.size());
//			------------------------------------------------------------------
//			VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
//			try {
//				render.afterPropertiesSet();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			Map<String,String> map = new HashMap<String,String>();
//			map.put("sample", "config/report/prof00/prof00_printInvDtl.vm");
//			render.setReportNameTemplateLocationMapping(map);
//			String result = render.renderAsString("sample", context);
//			logger.info(result);
//			String date = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
//			StringBuffer fileName = new StringBuffer((new StringBuilder("").append(date).toString()));
//			reportHelper.createFileAndSendMFTP(context, result, fileName, mftpConfigInfo);
//			------------------------------------------------------------------------
			VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
			String sampleFile="config/report/prof00/prof00_printInvDtl.vm";
			
			Map<String,String> mapping = new HashMap<String,String>();
	
			mapping.put("sample", sampleFile);
			try {
				render.afterPropertiesSet();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			
			
			render.setReportNameTemplateLocationMapping(mapping);
			String result = render.renderAsString("sample", context);
			try {
				log.info("generate report content:****"+new String(result.getBytes(GDConstants.CHARSET_ENCODING_GBK)));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String date = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
//			StringBuffer fileName = new StringBuffer((new StringBuilder("").append(date).toString()));
			String fileName = date;
			BufferedOutputStream outStream = null;
			try {
	
				outStream = new BufferedOutputStream(new FileOutputStream(
						"/home/bbipadm/data/mftp/BBIP/GDEUPSB/prof/"+fileName));
				outStream.write(result.getBytes(GDConstants.CHARSET_ENCODING_GBK));
				outStream.close();
			} catch (IOException e) {
				throw new CoreException("BBIP0004EU0128");
			}
			
			String path = "/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/";
			
			String FilNam = "/home/bbipadm/data/mftp/BBIP/GDEUPSB/prof/" +fileName;

			
			// 上传FTP
			FTPTransfer tFTPTransfer = new FTPTransfer();
			// FTP上传设置
			tFTPTransfer.setHost("182.53.15.187");
			tFTPTransfer.setPort(21);
			tFTPTransfer.setUserName("weblogic");
			tFTPTransfer.setPassword("123456");

			try {
				tFTPTransfer.logon();
				Resource tResource = new FileSystemResource(FilNam);
				tFTPTransfer.putResource(tResource,
						"/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/",
						fileName);

			} catch (Exception e) {
				throw new CoreException("文件上传失败");
			} finally {
				tFTPTransfer.logout();
			}

			context.setData("fleNme", fileName);
			logger.info("文件上传完成，等待打印！" + context);
	         
		}else{//查询
			List<Map<String,Object>> list = ((SqlMap)get("sqlMap")).queryForList("prof00.atmInvDtl", context.getDataMap());
			if(CollectionUtils.isEmpty(list)){
				logger.error("不存在记录");
				throw new CoreException("");
			}
			for(Map<String,Object> map:list){
				map.put("actDat", map.get("ACT_DAT"));
				map.put("oprTlr", map.get("TLR_ID"));
				map.put("invTyp", map.get("INV_TYP"));
				map.put("ivBegNo", map.get("IV_BEG_NO"));
				map.put("ivEndNo", map.get("IV_END_NO"));
				map.put("tolNum", map.get("TOL_NUM"));
				map.put("regTlr", map.get("REG_TLR"));
				map.put("lodNum", map.get("LOD_NUM"));
				map.put("useNum", map.get("USE_NUM"));
				map.put("clrNum", map.get("CLR_NUM"));
				map.put("invNum", map.get("INV_NUM"));
			}
			context.setData("tol", list.size());
			context.setData("rec1", list);
		}
		
		logger.info("AtmInvDtlServiceActionPROF00 invDtl end ... ...");
	}
	/**
	 * 自助通发票使用情况汇总表
	 * @param context
	 * @throws CoreException 
	 */
	private void invSum(Context context) throws CoreException{
		logger.info("AtmInvDtlServiceActionPROF00 invSum start ... ...");
		String invTyp = context.getData("invTyp");
		String begDat = context.getData("begDat");
		String endDat = context.getData("endDat");
		String qryNod = context.getData("qryNod");
		String prtFlg = context.getData("prtFlg");
		logger.info("invTyp["+invTyp+"]begDat["+begDat+"]endDat["+endDat+"]qryNod["+qryNod+"]prtFlg["+prtFlg+"]");
		if("1".equals(prtFlg)){//打印
			EupsThdFtpConfigRepository eupsThdFtpConfigRepository = get(EupsThdFtpConfigRepository.class);
			ReportHelper reportHelper = get(ReportHelper.class);
			MFTPConfigInfo mftpConfigInfo = reportHelper.getMFTPConfigInfo(eupsThdFtpConfigRepository);
			logger.info((new StringBuilder("mftpConfigInfo:>>>>").append(BeanUtils.toMap(mftpConfigInfo))).toString());
			
			List<Map<String,Object>> list = ((SqlMap)get("sqlMap")).queryForList("prof00.atmInvSum", context.getDataMap());
			Map<String,Object> sum = ((SqlMap)get("sqlMap")).queryForObject("prof00.atmInvSumRpt1", context.getDataMap());
			for(Map<String,Object> map:list){
				map.put("nodno", map.get("NODNO"));
				map.put("oprTlr", map.get("TLR_ID"));
				map.put("invTyp", map.get("INV_TYP"));
				map.put("lodNum", map.get("LOD_NUM"));
				map.put("useNum", map.get("USE_NUM"));
				map.put("clrNum", map.get("CLR_NUM"));
				String lodNum = map.get("lodNum")==null?"0":map.get("lodNum").toString();
				String useNum = map.get("useNum")==null?"0":map.get("useNum").toString();
				String clrNum = map.get("clrNum")==null?"0":map.get("clrNum").toString();
				map.put("invNum", NumberUtils.createInteger(lodNum)-NumberUtils.createInteger(useNum)-NumberUtils.createInteger(clrNum));
			}
			
			context.setData("eles", list);
			context.setData("slodNum", sum==null?"0":sum.get("LOD_NUM"));
			context.setData("suseNum", sum==null?"0":sum.get("USE_NUM"));
			context.setData("sclrNum", sum==null?"0":sum.get("CLR_NUM"));
			context.setData("tol", list.size());
//			---------------------------------------------------------------------------------------------
//			VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
//			try {
//				render.afterPropertiesSet();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			Map<String,String> map = new HashMap<String,String>();
//			map.put("sample", "config/report/prof00/prof00_printInvSum.vm");
//			render.setReportNameTemplateLocationMapping(map);
//			String result = render.renderAsString("sample", context);
//			logger.info(result);
//			String date = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
//			StringBuffer fileName = new StringBuffer((new StringBuilder("").append(date).toString()));
//			reportHelper.createFileAndSendMFTP(context, result, fileName, mftpConfigInfo);
//			------------------------------------------------------------------------------------------------
			VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
			String sampleFile="config/report/prof00/prof00_printInvSum.vm";
			
			Map<String,String> mapping = new HashMap<String,String>();
	
			mapping.put("sample", sampleFile);
			try {
				render.afterPropertiesSet();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			
			
			render.setReportNameTemplateLocationMapping(mapping);
			String result = render.renderAsString("sample", context);
			try {
				log.info("generate report content:****"+new String(result.getBytes(GDConstants.CHARSET_ENCODING_GBK)));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String date = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
//			StringBuffer fileName = new StringBuffer((new StringBuilder("").append(date).toString()));
			String fileName = date;
			BufferedOutputStream outStream = null;
			try {
	
				outStream = new BufferedOutputStream(new FileOutputStream(
						"/home/bbipadm/data/mftp/BBIP/GDEUPSB/prof/"+fileName));
				outStream.write(result.getBytes(GDConstants.CHARSET_ENCODING_GBK));
				outStream.close();
			} catch (IOException e) {
				throw new CoreException("BBIP0004EU0128");
			}
			
			String path = "/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/";
			
			String FilNam = "/home/bbipadm/data/mftp/BBIP/GDEUPSB/prof/" +fileName;

			
			// 上传FTP
			FTPTransfer tFTPTransfer = new FTPTransfer();
			// FTP上传设置
			tFTPTransfer.setHost("182.53.15.187");
			tFTPTransfer.setPort(21);
			tFTPTransfer.setUserName("weblogic");
			tFTPTransfer.setPassword("123456");

			try {
				tFTPTransfer.logon();
				Resource tResource = new FileSystemResource(FilNam);
				tFTPTransfer.putResource(tResource,
						"/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/",
						fileName);

			} catch (Exception e) {
				throw new CoreException("文件上传失败");
			} finally {
				tFTPTransfer.logout();
			}

			context.setData("fleNme", fileName);
			logger.info("文件上传完成，等待打印！" + context);
		}else{//查询
			List<Map<String,Object>> list = ((SqlMap)get("sqlMap")).queryForList("prof00.atmInvSum", context.getDataMap());
//			List<Map<String,Object>> list = get(SqlMapImpl.class).queryForList("prof00.atmInvSum", context.getDataMap());
			if(CollectionUtils.isEmpty(list)){
				logger.error("不存在记录");
				throw new CoreException("不存在记录");
			}
			for(Map<String,Object> map:list){
				map.put("nodno", map.get("NODNO"));
				map.put("oprTlr", map.get("TLR_ID"));
				map.put("invTyp", map.get("INV_TYP"));
				map.put("lodNum", map.get("LOD_NUM"));
				map.put("useNum", map.get("USE_NUM"));
				map.put("clrNum", map.get("CLR_NUM"));
				String lodNum = map.get("lodNum")==null?"0":map.get("lodNum").toString();
				String useNum = map.get("useNum")==null?"0":map.get("useNum").toString();
				String clrNum = map.get("clrNum")==null?"0":map.get("clrNum").toString();
				map.put("invNum", NumberUtils.createInteger(lodNum)-NumberUtils.createInteger(useNum)-NumberUtils.createInteger(clrNum));
			}
			context.setData("tol", list.size());
			context.setData("rec2", list);
		}
		
		logger.info("AtmInvDtlServiceActionPROF00 invSum end ... ...");
	}
	/**
	 * 企业发票使用情况明细表
	 * @param context
	 * @throws CoreException 
	 */
	private void qyInvSum(Context context) throws CoreException{
		logger.info("AtmInvDtlServiceActionPROF00 qyInvSum start ... ...");
		String invTyp = context.getData("invTyp");
		String begDat = context.getData("begDat");
		String endDat = context.getData("endDat");
		String qryNod = context.getData("qryNod");
		String prtFlg = context.getData("prtFlg");
		logger.info("invTyp["+invTyp+"]begDat["+begDat+"]endDat["+endDat+"]qryNod["+qryNod+"]prtFlg["+prtFlg+"]");
		if("1".equals(prtFlg)){//打印
			if(qryNod==null){
				context.setData("nodNam", "珠海分行");
			}else{
				//TODO:根据分行号和网点号查询网点名称，待实现
				context.setData("nodNam", "珠海分行");//测试代码
			}
			
			
			
			EupsThdFtpConfigRepository eupsThdFtpConfigRepository = get(EupsThdFtpConfigRepository.class);
			ReportHelper reportHelper = get(ReportHelper.class);
			MFTPConfigInfo mftpConfigInfo = reportHelper.getMFTPConfigInfo(eupsThdFtpConfigRepository);
			logger.info((new StringBuilder("mftpConfigInfo:>>>>").append(BeanUtils.toMap(mftpConfigInfo))).toString());
			
			List<Map<String,Object>> list = ((SqlMap)get("sqlMap")).queryForList("prof00.qyInvSum", context.getDataMap());
			Map<String,Object> sum = ((SqlMap)get("sqlMap")).queryForObject("prof00.qyInvSumRpt", context.getDataMap());
			for(Map<String,Object> map:list){
				map.put("qyNo", map.get("QY_NO"));
				map.put("useNum", map.get("USE_NUM"));
			}
			
			context.setData("eles", list);
			context.setData("suseNum", sum==null?"0":sum.get("USE_NUM"));
			context.setData("tol", list.size());
//			------------------------------------------------------------------------------
//			VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
//			try {
//				render.afterPropertiesSet();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			Map<String,String> map = new HashMap<String,String>();
//			map.put("sample", "config/report/prof00/prof00_qyInvSum.vm");
//			render.setReportNameTemplateLocationMapping(map);
//			String result = render.renderAsString("sample", context);
//			logger.info(result);
//			String date = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
//			StringBuffer fileName = new StringBuffer((new StringBuilder("").append(date).toString()));
//			reportHelper.createFileAndSendMFTP(context, result, fileName, mftpConfigInfo);
//			-------------------------------------------------------------------------------------------
			
			VelocityTemplatedReportRender render = new VelocityTemplatedReportRender();
			String sampleFile="config/report/prof00/prof00_qyInvSum.vm";
			
			Map<String,String> mapping = new HashMap<String,String>();
	
			mapping.put("sample", sampleFile);
			try {
				render.afterPropertiesSet();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			
			
			render.setReportNameTemplateLocationMapping(mapping);
			String result = render.renderAsString("sample", context);
			try {
				log.info("generate report content:****"+new String(result.getBytes(GDConstants.CHARSET_ENCODING_GBK)));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String date = DateUtils.format(new Date(), DateUtils.STYLE_yyyyMMdd);
//			StringBuffer fileName = new StringBuffer((new StringBuilder("").append(date).toString()));
			String fileName = date;
			BufferedOutputStream outStream = null;
			try {
	
				outStream = new BufferedOutputStream(new FileOutputStream(
						"/home/bbipadm/data/mftp/BBIP/GDEUPSB/prof/"+fileName));
				outStream.write(result.getBytes(GDConstants.CHARSET_ENCODING_GBK));
				outStream.close();
			} catch (IOException e) {
				throw new CoreException("BBIP0004EU0128");
			}
			
			String path = "/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/";
			
			String FilNam = "/home/bbipadm/data/mftp/BBIP/GDEUPSB/prof/" +fileName;

			
			// 上传FTP
			FTPTransfer tFTPTransfer = new FTPTransfer();
			// FTP上传设置
			tFTPTransfer.setHost("182.53.15.187");
			tFTPTransfer.setPort(21);
			tFTPTransfer.setUserName("weblogic");
			tFTPTransfer.setPassword("123456");

			try {
				tFTPTransfer.logon();
				Resource tResource = new FileSystemResource(FilNam);
				tFTPTransfer.putResource(tResource,
						"/home/weblogic/JumpServer/WEB-INF/data/mftp_recv/",
						fileName);

			} catch (Exception e) {
				throw new CoreException("文件上传失败");
			} finally {
				tFTPTransfer.logout();
			}

			context.setData("fleNme", fileName);
			logger.info("文件上传完成，等待打印！" + context);
		}else{//查询
			List<Map<String,Object>> list = ((SqlMap)get("sqlMap")).queryForList("prof00.qyInvSum", context.getDataMap());
			if(CollectionUtils.isEmpty(list)){
				logger.error("不存在记录");
				throw new CoreException("不存在记录");
			}
			for(Map<String,Object> map:list){
				map.put("qyNo", map.get("QY_NO"));
				map.put("useNum", map.get("USE_NUM"));
			}
			context.setData("tol", list.size());
			context.setData("rec3", list);
		}
		
		logger.info("AtmInvDtlServiceActionPROF00 qyInvSum end ... ...");
	}

}

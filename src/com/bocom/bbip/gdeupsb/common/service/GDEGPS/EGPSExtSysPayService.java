package com.bocom.bbip.gdeupsb.common.service.GDEGPS;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;
/**
 * @category call财政系统服务
 * @author Administrator
 *
 */
public class EGPSExtSysPayService extends BGSPClientBaseWithGDEGPSA{
	private Logger log = LoggerFactory.getLogger(EGPSExtSysPayService.class);
	/**
	 * call EGPSExtSysPayService 返回map
	 * @param operation 执行交易名
	 * @param requestMap 请求数据
	 * @return
	 * @throws CoreRuntimeException
	 * @throws CoreException
	 */
	public Map<String,Object> callEGPSExtSysPay(String operation,Map<String,Object> requestData,Context context) throws CoreRuntimeException, CoreException{
		Map<String,Object> requestMap = new HashMap<String,Object>();
		requestMap.putAll(createBBIPHeader(context));
		requestMap.putAll(requestData);
		Map<String,Object> map = super.submit(operation, requestMap).getPayload();
		return map;
	}
	/**
	 * 向BGSP发送请求
	 * @param operation 执行交易名
	 * @param data 报文参数
	 * @throws CoreRuntimeException
	 * @throws CoreException
	 */
	public Result submit(String operation, Map<String, Object> requestData,Context context)
			throws CoreRuntimeException, CoreException {
		// 使用扁平化数据访问bgsp服务。
		Map<String,Object> requestMap = new HashMap<String,Object>();
		Map<String,Object> bbipHeaderMap = createBBIPHeader(context); 
		requestMap.putAll(bbipHeaderMap);
		log.info("request body:"+requestData);
		requestMap.putAll(requestData);
		Result result = super.submit(operation,requestMap);
		log.info(operation + " BGSP Result:" + result.getPayload());
		return result;
	}
	/**
	 * 获取BBIP报文头
	 * @param context
	 * @return
	 */
	private Map<String,Object> createBBIPHeader(Context context){
		Map<String,Object> bbipHeader = new HashMap<String,Object>();
		bbipHeader.put("traceNo", context.getData("traceNo"));
		bbipHeader.put("version", context.getData("version"));
		bbipHeader.put("traceSrc", context.getData("traceSrc"));
		bbipHeader.put("txnCde", context.getData("txnCde"));
		bbipHeader.put("chn", context.getData("chn"));
		bbipHeader.put("reqSysCde", context.getData("reqSysCde"));
		bbipHeader.put("subSysCde", context.getData("subSysCde"));
		bbipHeader.put("bbipBusCde", context.getData("bbipBusCde"));
		bbipHeader.put("reqJrnNo", context.getData("reqJrnNo"));
		bbipHeader.put("tlr", context.getData("tlr"));
		bbipHeader.put("br", context.getData("br"));
		bbipHeader.put("bk", context.getData("bk"));
//		bbipHeader.put("tlrTmlId", context.getData("tlrTmlId"));
//		bbipHeader.put("reqTyp", context.getData("reqTyp"));
//		bbipHeader.put("authLvl", context.getData("authLvl"));
//		bbipHeader.put("sup1Id", context.getData("sup1Id"));
//		bbipHeader.put("sup1Auth", context.getData("sup1Auth"));
//		bbipHeader.put("sup2Id", context.getData("sup2Id"));
//		bbipHeader.put("sup2Auth", context.getData("sup2Auth"));
//		bbipHeader.put("authResn", context.getData("authResn"));
//		bbipHeader.put("authLog", context.getData("authLog"));
		bbipHeader.put("reqTme", new Date());
		
		log.info("request BBIPHeader:"+bbipHeader.toString());
		return bbipHeader;
	}
}

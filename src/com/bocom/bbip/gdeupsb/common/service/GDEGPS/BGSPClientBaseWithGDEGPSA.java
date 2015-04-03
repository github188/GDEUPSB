package com.bocom.bbip.gdeupsb.common.service.GDEGPS;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bocom.bbip.service.BGSPServiceAccessObject;
import com.bocom.bbip.service.Result;
import com.bocom.jump.bp.core.CoreException;
import com.bocom.jump.bp.core.CoreRuntimeException;

public class BGSPClientBaseWithGDEGPSA {
	private Logger log = LoggerFactory.getLogger(BGSPClientBaseWithGDEGPSA.class);
	@Autowired
	private BGSPServiceAccessObject bgspServiceAccessObject;
	/**
	 * 向BGSP发送请求
	 * 
	 * @param operation
	 *            执行交易名
	 * @param data
	 *            报文参数
	 * @throws CoreRuntimeException
	 * @throws CoreException
	 */
	public Result submit(String operation, Map<String, Object> data)
			throws CoreRuntimeException, CoreException {
		// 使用扁平化数据访问bgsp服务。
		Result result = bgspServiceAccessObject.callServiceFlatting(operation,
				data);
		log.info(operation + " BGSP Result:" + result.getPayload());
		return result;
	}
}

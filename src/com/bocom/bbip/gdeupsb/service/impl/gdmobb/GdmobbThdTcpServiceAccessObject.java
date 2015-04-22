package com.bocom.bbip.gdeupsb.service.impl.gdmobb;

import java.util.Map;

import com.bocom.bbip.gdeupsb.action.vo.gdmobb.GdmobbHeaderVo;



public interface GdmobbThdTcpServiceAccessObject {
	/**
     * @Title: callThdTcpService 
     * @Description: 
     * @param @param hmfHeaderVo 请求报文头
     * @param @param requestData 请求报文体
     * @param @return 设定文件 
     * @return HmfResult 返回类型 
     * @throws
     */
	GdmobbResult callThdTcpService(GdmobbHeaderVo gdmobbHeaderVo, Map<String, Object> requestData);
}

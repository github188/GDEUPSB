package com.bocom.bbip.gdeupsb.service.impl.gduncb;

import java.util.Map;

import com.bocom.bbip.gdeupsb.action.vo.gduncb.GduncbHeaderVo;



public interface GduncbThdTcpServiceAccessObject {
	/**
     * @Title: callThdTcpService 
     * @Description: 
     * @param @param hmfHeaderVo 请求报文头
     * @param @param requestData 请求报文体
     * @param @return 设定文件 
     * @return HmfResult 返回类型 
     * @throws
     */
	GduncbResult callThdTcpService(GduncbHeaderVo gduncbHeaderVo, Map<String, Object> requestData);
}

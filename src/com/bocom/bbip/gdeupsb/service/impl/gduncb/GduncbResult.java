package com.bocom.bbip.gdeupsb.service.impl.gduncb;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class GduncbResult implements ThdTcpResult {
	private static final Log logger = LogFactory.getLog(GduncbResult.class);

    // 第三方返回消息
    private Map<String, Object> responseMap;

    // 第三方返回状态
    private int status;

    public GduncbResult() {

    }
    public GduncbResult(Map<String, Object> responseMap) {
        // 存储第三方返回信息
        setResponseMap(responseMap);

        // 输出信息类型,N-正常 A-要求提供授权 E-错误
        if (null != responseMap) {
            logger.info((new StringBuilder("Response is [")).append(responseMap).append("]").toString());
            //String returnCode = (String) responseMap.get(HmfThdTcpCompFields.RETURN_CODE);
            //if (null != returnCode && returnCode.equals("0000")) { // 返回码 0000表示成功
                setStatus(ThdTcpResult.STATUS_SUCCESS);//成功
            //} else{
               // setStatus(ThdTcpResult.STATUS_FAIL);//失败
            //}
            logger.info((new StringBuilder("HmfResult status is [")).append(this.getStatus()).append("].").toString());
        }
    }
    public GduncbResult(Map<String, Object> responseMap, int status) {
        this.setResponseMap(responseMap);
        this.setStatus(status);
    }
    @Override
    public boolean isSuccess() {
        return this.getStatus() == ThdTcpResult.STATUS_SUCCESS;
    }
    @Override
    public Map<String, Object> getResponseMap() {
        return this.responseMap;
    }

    @Override
    public int getStatus() {
        return status;
    }
    /**
     * @param The status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @param The responseMap
     */
    public void setResponseMap(Map<String, Object> responseMap) {
        this.responseMap = responseMap;
    }
}

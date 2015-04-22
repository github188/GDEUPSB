package com.bocom.bbip.gdeupsb.service.impl.gduncb;

import java.util.Map;

/**
 * 封装请求第三方TCP返回结果
 * <p>
 * 
 * @version 1.0.0,2015-1-22
 * @author sunbg
 * @since 1.0.0
 */
public interface ThdTcpResult {
	 /**
     * @Title: isSuccess
     * @Description: 判断返回是否成功
     * @param @return 设定文件
     * @return boolean 返回类型
     * @throws
     */
    abstract boolean isSuccess();

    /**
     * @Title: getStatus
     * @Description: 返回交易状态
     * @param @return 设定文件
     * @return int 返回类型
     * @throws
     */
    abstract int getStatus();

    /**
     * @Title: getResponseMap
     * @Description: 接收返回结果
     * @param @return 设定文件
     * @return Map<String,Object> 返回类型
     * @throws
     */
    abstract Map<String, Object> getResponseMap();

    /**
     * 第三方返回状态：成功
     */
    static final int STATUS_SUCCESS = 0;

    /**
     * 第三方返回状态：失败
     */
    static final int STATUS_FAIL = 1;

    /**
     * 第三方返回状态：超时
     */
    static final int STATUS_TIMEOUT = -1;

    /**
     * 第三方返回状态：发送错误
     */
    static final int STATUS_SEND_ERROR = -10;

    /**
     * 第三方返回状态：系统异常
     */
    static final int STATUS_SYSTEM_ERROR = -2;
}

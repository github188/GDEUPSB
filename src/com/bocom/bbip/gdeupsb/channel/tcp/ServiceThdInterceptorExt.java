
package com.bocom.bbip.gdeupsb.channel.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.bocom.bbip.comp.BBIPPublicService;
import com.bocom.bbip.eups.action.BaseAction;
import com.bocom.bbip.eups.common.Constants;
import com.bocom.bbip.eups.common.ErrorCodes;
import com.bocom.bbip.eups.common.ParamKeys;
import com.bocom.bbip.eups.entity.EupsThdBaseInfo;
import com.bocom.bbip.eups.repository.EupsThdBaseInfoRepository;
import com.bocom.bbip.utils.CollectionUtils;
import com.bocom.bbip.utils.StringUtils;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.ChannelContext;
import com.bocom.jump.bp.channel.ChannelInterceptor;
import com.bocom.jump.bp.core.ContextEx;
import com.bocom.jump.bp.core.CoreException;

/**
 * 自定义第三方拦截器
 * 
 * @version 1.0.0,2014-3-22
 * @author kejy.sdc
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public class ServiceThdInterceptorExt extends BaseAction implements ChannelInterceptor {

    @Autowired
    private BBIPPublicService bbipPublicService;

    @Autowired
    EupsThdBaseInfoRepository eupsThdBaseInfoRep;

    private final static Logger log = LoggerFactory.getLogger(ServiceThdInterceptorExt.class);

    private Map<String, String> loadMap;

    public ServiceThdInterceptorExt(String port) {
        this.port = port;
        log.info("ServiceThdInterceptor start from here!");
    }

    @Override
    public void onRequest(ChannelContext channelcontext, ContextEx contextex) throws JumpException {

        String eupsBusTyp = null;
        String comNo = null;
        // 默认成功
        contextex.setData(ParamKeys.RESPONSE_CODE, Constants.RESPONSE_CODE_SUCC);

        //根据端口查询eups业务类型
        EupsThdBaseInfo eupsQryPara = new EupsThdBaseInfo();
        eupsQryPara.setLsdPot(port);
        List<EupsThdBaseInfo> eupsThdBaseInfoList = eupsThdBaseInfoRep.find(eupsQryPara);
        if (CollectionUtils.isEmpty(eupsThdBaseInfoList)) {
            log.error("listened port info not exist!");
            throw new CoreException(ErrorCodes.EUPS_BUS_TYP_NOEXIST);
        } else {
            if(eupsThdBaseInfoList.size()!=1){
                log.error("listened port info error! expect 1  but found "+eupsThdBaseInfoList.size());
                throw new CoreException(ErrorCodes.EUPS_USE_STS_ERROR);
            }
            EupsThdBaseInfo eupsThdBaseInfo = eupsThdBaseInfoList.get(0);
//            if(Constants.USE_STS_LISTEN_FALSE.equals(eupsThdBaseInfo.getCmuSts().trim())){     TODO: 放开即可控制监听使用状态
//                log.error("listened port info use status error! ");
//                throw new CoreException(ErrorCodes.EUPS_USE_STS_ERROR);
//            }
            eupsBusTyp = eupsThdBaseInfo.getEupsBusTyp().trim();
            comNo = eupsThdBaseInfo.getComNo().trim();
            log.info("comNo="+comNo+",and eupsBusTyp="+eupsBusTyp); 
        }
        // 第三方发起字段设置
        contextex.setData(ParamKeys.EUPS_BUSS_TYPE, eupsBusTyp);
        contextex.setData(ParamKeys.COMPANY_NO, comNo);
        contextex.setData(ParamKeys.REQ_SYS_CDE, eupsBusTyp);
        contextex.setData(ParamKeys.CHL_TYP, Constants.CHL_TYPE_THIRD_SYSTEM);
        contextex.setData(ParamKeys.CHANNEL, Constants.CHL_TYPE_THIRD_SYSTEM);

        contextex.setData(ParamKeys.REQ_TIME, new Date());
        if (null == contextex.getData(ParamKeys.TXN_DATE)) {
            contextex.setData(ParamKeys.TXN_DATE, new Date());
            log.info("new txnDte:" + new Date());
        }
        contextex.setData(ParamKeys.REQ_JRN_NO, bbipPublicService.getBBIPSequence());
        contextex.setData(ParamKeys.PASSWORD_CHECK_FLAG, Constants.PASSWORD_CHECK_FLAG_NO); // 需要校验密码

        contextex.setData(ParamKeys.BUS_TYP, Constants.BUS_TYP_2); // 2-代缴不验户名
        contextex.setData(ParamKeys.VERSION, Constants.BBIP_VERSION); // eups版本号
        String thdReqCde = contextex.getProcessId();
        if (thdReqCde.length() > 10) {
            thdReqCde = thdReqCde.substring(0, 10);
        }
        contextex.setData(ParamKeys.TXN_CODE, thdReqCde);

        //银行方相关参数设置
        contextex.setData(ParamKeys.BK, bbipPublicService.getParam("BBIP", "BK"));
        log.info("BK:" + bbipPublicService.getParam("BBIP", "BK"));
        if (null == contextex.getData(ParamKeys.BR)) {
            log.info("BR:" + bbipPublicService.getParam("THD_BR"));
            contextex.setData(ParamKeys.BR, bbipPublicService.getParam("THD_BR"));
        }
        if (null == contextex.getData(ParamKeys.TELLER)) {
            // 参数传入br或bk，此处传入bk
            String tlr = bbipPublicService.getETeller(contextex.getData(ParamKeys.BK).toString());
            contextex.setData(ParamKeys.TELLER, tlr);
        }
        if (null == contextex.getData(ParamKeys.TERMINAL)) {
            contextex.setData(ParamKeys.TERMINAL, contextex.getData(ParamKeys.TELLER));
        }
        if (null == contextex.getData(ParamKeys.TRACE_NO)) {
            String traceNo = bbipPublicService.getTraceNo();
            traceNo = traceNo.replace("SZXEUPS", "30034403");
            contextex.setData(ParamKeys.TRACE_NO, traceNo);
        }

        if (null == contextex.getData(ParamKeys.SEQUENCE)) {
            contextex.setData(ParamKeys.SEQUENCE, bbipPublicService.getBBIPSequence());
        }
        if (null == contextex.getData(ParamKeys.ACCOUNT_TYPE)) {
            contextex.setData(ParamKeys.ACCOUNT_TYPE, "0");
        }
        log.info("on request interceptor,listen port=" + port + ",and eupsBusTyp=" + eupsBusTyp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResponse(ChannelContext channelcontext, ContextEx contextex, Throwable throwable) {
    	System.out.println("~~~~channelcontext~~~~~~"+channelcontext);
    	System.out.println("~~~~~~contextex~~~~~"+contextex);
        //第三方返回码处理
    	//TODO throwable

        if (null != throwable) {
            contextex.setData(ParamKeys.RESPONSE_CODE, throwable.getMessage().replace("[", "").replace("]", ""));
            // thdRspMsgDeal(contextex);
        } else {
            log.info("this is onPrsponse!" + contextex.getData(ParamKeys.RESPONSE_CODE));
            if (Constants.RESPONSE_CODE_SUCC.equals(contextex.getData(ParamKeys.RESPONSE_CODE))
                || Constants.RESPONSE_CODE_SUCC_HOST.equals(contextex.getData(ParamKeys.RESPONSE_CODE))
                || Constants.RESPONSE_CODE_SUCC_APOS.equals(contextex.getData(ParamKeys.RESPONSE_CODE))) {
                contextex.setData(ParamKeys.RESPONSE_CODE, Constants.RESPONSE_CODE_SUCC);
                contextex.setData(ParamKeys.THD_RSP_MSG, "交易成功");
            } else {
                if (null == contextex.getData(ParamKeys.RESPONSE_CODE)) {
                    contextex.setData(ParamKeys.RESPONSE_CODE, ErrorCodes.EUPS_THD_SYS_ERROR); // 系统错误
                    contextex.setData(ParamKeys.THD_RSP_MSG, "系统内部错误-无返回信息");
                }
            }
        }
        thdRspMsgDeal(contextex);
    }

    /**
     * 交易错误信息处理
     */
    private void thdRspMsgDeal(ContextEx contextex) {
        String eupsBusTyp = contextex.getData(ParamKeys.EUPS_BUSS_TYPE);

        InputStream thdRspCdeInput = this.getClass().getClassLoader().getResourceAsStream(ParamKeys.THIRD_RETURN_CODE_CONVERT_DICTIONARY);
        String responseCode = contextex.getData(ParamKeys.RESPONSE_CODE);
        contextex.setData(ParamKeys.RESPONSE_CODE, Constants.RESPONSE_CODE_SUCC);
        log.info("responseCode=" + responseCode);
        Map<String, String> propertyMap = getProperty(thdRspCdeInput);
        String responseCodeTHD = propertyMap.get(eupsBusTyp + "~" + responseCode);
        //TODO responseCodeTHD 修改
        if (null == responseCodeTHD) {
            // 第三方默认错误为JUMPBP9001
            //responseCode = "JUMPBP9001";
            contextex.setData(ParamKeys.THIRD_RETURN_CODE, propertyMap.get(eupsBusTyp + "~" + "JUMPBP9001"));
        } else
            contextex.setData(ParamKeys.THIRD_RETURN_CODE, responseCodeTHD);
        
        log.info("responseCodeTHD=" + responseCodeTHD);
        try {
            thdRspCdeInput.close();
        } catch (IOException e) {
            log.error("propertyMap close error!");
        }
        if (null != contextex.getData(ParamKeys.RESPONSE_MESSAGE)) {
            contextex.setData(ParamKeys.THD_RSP_MSG, contextex.getData(ParamKeys.RESPONSE_MESSAGE).toString().trim());
            return;
        }
        //返回错误信息处理
        responseCode=Constants.RESPONSE_CODE_SUCC;
        if("SC0000".equals(responseCode)||"000000".equals(responseCode)){
            if(StringUtils.isEmpty((String)contextex.getData(ParamKeys.THD_RSP_MSG))){
                contextex.setData(ParamKeys.THD_RSP_MSG, "交易成功");
            }
            return;
        }else{        	
//            if (null == this.loadMap) {
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                Resource[] resources;
                try {
                    resources = resolver.getResources("classpath*:" + ParamKeys.THD_ERROR_MESSAGE_FILE);
                    String thdRspMsg = null;
                    for (Resource resourc : resources) {

                        InputStream errorMsgInput = resourc.getInputStream();// this.getClass().getClassLoader().getResourceAsStream(ParamKeys.THD_ERROR_MESSAGE_FILE);

                        this.loadMap = getProperty(errorMsgInput);

                        if(null!=this.loadMap.get(responseCode)){
                        	thdRspMsg = this.loadMap.get(responseCode);
                        }
                        log.info("=========this is thdRspMsg!" + thdRspMsg);
                        
                        if (StringUtils.isEmpty(thdRspMsg) || thdRspMsg.length() > 60) {
                        	 log.info("=========thdRspMsg is null,set it to fail");
                            thdRspMsg = "交易失败!";
                        }
                        contextex.setData(ParamKeys.THD_RSP_MSG, thdRspMsg);
                        try {
                            errorMsgInput.close();
                        } catch (IOException e) {
                            log.error("errorMsgInput close error!");
                        }
                    }
                } catch (IOException e1) {
                    log.error("deal return msg error!");
                }
//            } else {
//                String thdRspMsg = null;
//                log.info("==============onresponse errorMessage deal2,this.loadMap="+this.loadMap);
//                thdRspMsg = this.loadMap.get(responseCode);
//                if (StringUtils.isEmpty(thdRspMsg) || thdRspMsg.length() > 60) {
//                    thdRspMsg = "交易失败!";
//                }
//                contextex.setData(ParamKeys.THD_RSP_MSG, thdRspMsg);
//            }
        }
        log.info("after thdRspMsgDeal,context="+contextex);
    }

    /**
     * 读取配置文件value-keyStr
     * 
     * @param location
     * @return
     */
    private Map<String, String> getProperty(InputStream location) {
        Properties prop = new Properties();
        Map<String, String> propMap = new HashMap<String, String>();
        try {
            InputStreamReader sr = new InputStreamReader(location, "utf-8");
            prop.load(sr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Object key : prop.keySet()) {
            String keyStr = key.toString();
            String value = prop.getProperty(keyStr);
            propMap.put(keyStr, value);
        }
        return propMap;
    }

    private String port;
}

package com.bocom.bbip.gdeupsb.service.impl.gdmobb;

import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.gdeupsb.action.vo.gdmobb.GdmobbHeaderVo;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Transport;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.impl.DefaultContextEx;

/**
 * 封装请求广东移动服务
 * <p>
 * @version 1.0.0,2015-1-22
 * @author tandun
 * @since 1.0.0
 */
public class GdmobbThdTcpServiceAccessObjectImpl implements GdmobbThdTcpServiceAccessObject {
	private static final Log logger = LogFactory.getLog(GdmobbThdTcpServiceAccessObjectImpl.class);

    @Resource(name = "GDMOBBTransport")
    private Transport<Map<String, Object>, Map<String, Object>> gdmobbSocketTransport;
    
    public GdmobbThdTcpServiceAccessObjectImpl() {

    }
	@Override
	public GdmobbResult callThdTcpService(GdmobbHeaderVo gdmobbHeaderVo,
			Map<String, Object> requestData) {
		logger.info((new StringBuilder("call ICS service with GdmobbHeaderVo[")).append(requestData).append("]!").toString());
        final Map<String, Object> requestMap = gdmobbHeaderVo.toMap();
        if (null != requestData) {
            requestMap.putAll(requestData);
            logger.info((new StringBuilder("the requestMap is [")).append(requestMap.toString()).append("]").toString());
        }
        Map<String, Object> responseMap = null;
        try {
            Context context =  new DefaultContextEx();
            context.setProcessId(gdmobbHeaderVo.getOpcode());//设置交易的processId
            
            responseMap = (Map<String, Object>) gdmobbSocketTransport.submit(requestMap, context);
            return new GdmobbResult(responseMap);
        } catch (CommunicationException e) {
            if (e.isTimeout()) {
                logger.error("Message receive timeout:", e);
                return new GdmobbResult(null, ThdTcpResult.STATUS_TIMEOUT);
            } else {
                logger.error("Message send error happen:", e);
                return new GdmobbResult(null, ThdTcpResult.STATUS_SEND_ERROR);
            }
        } catch (JumpException e) {
            logger.error("Message send error happen:", e);
            return new GdmobbResult(null, ThdTcpResult.STATUS_SYSTEM_ERROR);
        } catch (Exception e) {
            logger.error("Other error happen:", e);
            return new GdmobbResult(null, ThdTcpResult.STATUS_SYSTEM_ERROR);
        }
	}

}

package com.bocom.bbip.gdeupsb.service.impl.gduncb;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bocom.bbip.gdeupsb.action.vo.gduncb.GduncbHeaderVo;
import com.bocom.jump.bp.JumpException;
import com.bocom.jump.bp.channel.CommunicationException;
import com.bocom.jump.bp.channel.Transport;
import com.bocom.jump.bp.core.Context;
import com.bocom.jump.bp.core.impl.DefaultContextEx;

/**
 * 封装请求廣東聯通服務
 * <p>
 * @version 1.0.0,2015-1-22
 * @author sunbg
 * @since 1.0.0
 */
public class GduncbThdTcpServiceAccessObjectImpl implements GduncbThdTcpServiceAccessObject {
	private static final Log logger = LogFactory.getLog(GduncbThdTcpServiceAccessObjectImpl.class);

    @Resource(name = "GDUNCBTransport")
    private Transport<Map<String, Object>, Map<String, Object>> gduncaSocketTransport;
    
    public GduncbThdTcpServiceAccessObjectImpl() {

    }
	@Override
	public GduncbResult callThdTcpService(GduncbHeaderVo gduncaHeaderVo,
			Map<String, Object> requestData) {
		logger.info((new StringBuilder("call gdunca service with GduncaHeaderVo[")).append(requestData).append("]!").toString());
        final Map<String, Object> requestMap = gduncaHeaderVo.toMap();
        if (null != requestData) {
            requestMap.putAll(requestData);
            logger.info((new StringBuilder("the requestMap is [")).append(requestMap.toString()).append("]").toString());
        }
        Map<String, Object> responseMap = null;
        try {
            Context context =  new DefaultContextEx();
            context.setProcessId(gduncaHeaderVo.getMsg_sender());
            
            responseMap = (Map<String, Object>) gduncaSocketTransport.submit(requestMap, context);
            return new GduncbResult(responseMap);
        } catch (CommunicationException e) {
            if (e.isTimeout()) {
                logger.error("Message receive timeout:", e);
                return new GduncbResult(null, ThdTcpResult.STATUS_TIMEOUT);
            } else {
                logger.error("Message send error happen:", e);
                return new GduncbResult(null, ThdTcpResult.STATUS_SEND_ERROR);
            }
        } catch (JumpException e) {
            logger.error("Message send error happen:", e);
            return new GduncbResult(null, ThdTcpResult.STATUS_SYSTEM_ERROR);
        } catch (Exception e) {
            logger.error("Other error happen:", e);
            return new GduncbResult(null, ThdTcpResult.STATUS_SYSTEM_ERROR);
        }
	}

}
